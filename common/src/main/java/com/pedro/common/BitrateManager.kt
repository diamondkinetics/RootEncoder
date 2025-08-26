/*
 * Copyright (C) 2024 pedroSG94.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pedro.common


/**
 * Created by pedro on 8/04/21.
 * Improved by perotom on 7/05/24.
 *
 * Calculate video and audio bitrate per second based on an exponential moving average.
 */
open class BitrateManager(private val bitrateChecker: BitrateChecker) {

  private var bitrate = 0L
  private var bitrateOld = 0L
  var exponentialFactor: Float = 1f
  private var timeStamp = TimeUtils.getCurrentTimeMillis()

  private var byterate = 0L
  private var queuedBytes: Long = 0
  private val measureInterval = 3
  private val previousQueueBytesOut: MutableList<Long> = mutableListOf()

  fun queueBytes(size: Long) {
    queuedBytes += size
  }

  suspend fun calculateBitrateAndBandwidth(bytesSendPerSecond: Long, myQueueValue: Long) {
    // Track bitrate and queue management
    bitrate += (bytesSendPerSecond * 8)
    byterate += bytesSendPerSecond
    queuedBytes -= bytesSendPerSecond
    
    val timeDiff = TimeUtils.getCurrentTimeMillis() - timeStamp
    if (timeDiff >= 1000) {
      // Calculate bitrate with exponential moving average
      val currentValue = (bitrate / (timeDiff / 1000f)).toLong()
      if (bitrateOld == 0L) { bitrateOld = currentValue }
      bitrateOld = (bitrateOld + exponentialFactor * (currentValue - bitrateOld)).toLong()

      // Calculate bandwidth and throughput analysis
      var throughput: Throughput = Throughput.Unknown
      previousQueueBytesOut.add(queuedBytes)
      if (measureInterval <= previousQueueBytesOut.size) {
        var countQueuedBytesGrowing = 0
        for (i in 0 until previousQueueBytesOut.size - 1) {
          if (previousQueueBytesOut[i] < previousQueueBytesOut[i + 1]) {
            countQueuedBytesGrowing++
          }
        }
        if (countQueuedBytesGrowing == measureInterval - 1) {
          throughput = Throughput.Insufficient
        } else if (countQueuedBytesGrowing == 0) {
          throughput = Throughput.Sufficient
        }
        previousQueueBytesOut.removeAt(0)
      }

      // Call both callbacks on main thread
      onMainThread {
        bitrateChecker.onNewBitrate(bitrateOld)
        bitrateChecker.onStreamingStats(
          ((bytesSendPerSecond * 8) / (timeDiff / 1000f)).toLong(),
          bytesSendPerSecond,
          myQueueValue,
          throughput
        )
      }

      // Reset all counters
      timeStamp = TimeUtils.getCurrentTimeMillis()
      bitrate = 0
    }
  }

  suspend fun calculateBitrate(size: Long) {
    bitrate += size
    val timeDiff = TimeUtils.getCurrentTimeMillis() - timeStamp
    if (timeDiff >= 1000) {
      val currentValue = (bitrate / (timeDiff / 1000f)).toLong()
      if (bitrateOld == 0L) { bitrateOld = currentValue }
      bitrateOld = (bitrateOld + exponentialFactor * (currentValue - bitrateOld)).toLong()
      onMainThread { bitrateChecker.onNewBitrate(bitrateOld) }
      timeStamp = TimeUtils.getCurrentTimeMillis()
      bitrate = 0
    }
  }

  fun reset() {
    byterate = 0
    bitrateOld = 0
    queuedBytes = 0
    previousQueueBytesOut.clear()
  }
}