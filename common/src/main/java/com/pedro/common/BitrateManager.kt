/*
 * Copyright (C) 2023 pedroSG94.
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
 *
 * Calculate video and audio bitrate per second
 */
open class BitrateManager(private val connectChecker: ConnectChecker) {

    private var byterate: Long = 0
    private var timeStamp = TimeUtils.getCurrentTimeMillis()
    private var queuedBytes: Long = 0

    private val measureInterval = 3
    private val previousQueueBytesOut: MutableList<Long> = mutableListOf()


    fun queueBytes(size: Long) {
        queuedBytes += size
    }

    fun start() {
        queuedBytes = 0
        previousQueueBytesOut.clear()
    }

    suspend fun calculateBandwidth(size: Long, myQueueValue: Long) {
        byterate += size
        queuedBytes -= size
        val timeDiff = TimeUtils.getCurrentTimeMillis() - timeStamp
        if (timeDiff >= 1000) {
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
                previousQueueBytesOut.removeFirst()
            }
            onMainThread {
                connectChecker.onStreamingStats(
                    ((byterate * 8) / (timeDiff / 1000f)).toLong(),
                    byterate,
                    myQueueValue,
                    throughput
                )
            }
            timeStamp = TimeUtils.getCurrentTimeMillis()
            byterate = 0
        }
    }
}