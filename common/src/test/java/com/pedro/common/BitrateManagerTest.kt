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

import com.pedro.common.util.MainDispatcherRule
import com.pedro.common.util.Utils
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.times
import org.mockito.kotlin.verify


/**
 * Created by pedro on 9/9/23.
 */
@RunWith(MockitoJUnitRunner::class)
class BitrateManagerTest {

  @get:Rule
  val mainDispatcherRule = MainDispatcherRule()
  @Mock
  private lateinit var connectChecker: ConnectChecker
  private val timeUtilsMocked = Mockito.mockStatic(TimeUtils::class.java)
  private var fakeTime = 7502849023L

  @Before
  fun setup() {
    timeUtilsMocked.`when`<Long>(TimeUtils::getCurrentTimeMillis).then { fakeTime }
  }

  @After
  fun teardown() {
    fakeTime = 7502849023L
  }

  @Test
  fun `WHEN set multiple values THEN return total of values each second`() = runTest {
    Utils.useStatics(listOf(timeUtilsMocked)) {
      val bitrateManager = BitrateManager(connectChecker)
      val fakeValues = arrayOf(100L, 200L, 300L, 400L, 500L)
      var expectedResult = 0L
      fakeValues.forEach {
        bitrateManager.calculateBitrate(it)
        expectedResult += it
      }
      fakeTime += 1000
      val value = 100L
      bitrateManager.calculateBitrate(value)
      expectedResult += value
      val resultValue = argumentCaptor<Long>()
      verify(connectChecker, times(1)).onNewBitrate(resultValue.capture())
      val marginError = 20
      assertTrue(expectedResult - marginError <= resultValue.firstValue && resultValue.firstValue <= expectedResult + marginError)
    }
  }

  @Test
  fun `WHEN calculateBitrateAndBandwidth called multiple times THEN return total bitrate and bandwidth stats`() = runTest {
    Utils.useStatics(listOf(timeUtilsMocked)) {
      val bitrateManager = BitrateManager(connectChecker)
      val fakeValues = arrayOf(100L, 200L, 300L, 400L, 500L)
      val queueValues = arrayOf(50L, 100L, 150L, 200L, 250L)
      var expectedBitrate = 0L
      
      // Call calculateBitrateAndBandwidth multiple times within the same second
      fakeValues.forEachIndexed { index, value ->
        bitrateManager.calculateBitrateAndBandwidth(value, queueValues[index])
        expectedBitrate += (value * 8) // Convert bytes to bits
      }
      
      // Advance time by 1 second to trigger the calculation
      fakeTime += 1000
      
      // Call one more time to trigger the final calculation
      val finalValue = 100L
      val finalQueueValue = 300L
      bitrateManager.calculateBitrateAndBandwidth(finalValue, finalQueueValue)
      expectedBitrate += (finalValue * 8)
      
      // Verify onNewBitrate was called
      val bitrateCaptor = argumentCaptor<Long>()
      verify(connectChecker, times(1)).onNewBitrate(bitrateCaptor.capture())
      
      // Verify onStreamingStats was called
      val bitrateStatsCaptor = argumentCaptor<Long>()
      val bytesSentCaptor = argumentCaptor<Long>()
      val bytesQueuedCaptor = argumentCaptor<Long>()
      val throughputCaptor = argumentCaptor<Throughput>()
      
      verify(connectChecker, times(1)).onStreamingStats(
        bitrateStatsCaptor.capture(),
        bytesSentCaptor.capture(),
        bytesQueuedCaptor.capture(),
        throughputCaptor.capture()
      )
      
      // Check bitrate calculation (with margin for exponential moving average)
      val marginError = 100
      assertTrue(expectedBitrate - marginError <= bitrateCaptor.firstValue && bitrateCaptor.firstValue <= expectedBitrate + marginError)
      
      // Check streaming stats
      assertTrue(bitrateStatsCaptor.firstValue > 0)
      assertTrue(bytesSentCaptor.firstValue > 0)
      assertEquals(finalQueueValue, bytesQueuedCaptor.firstValue)
      assertEquals(Throughput.Unknown, throughputCaptor.firstValue) // Should be Unknown for first measurement
    }
  }
}