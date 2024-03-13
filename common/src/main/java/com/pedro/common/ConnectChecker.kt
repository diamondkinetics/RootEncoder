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

enum class Throughput {
  Unknown,
  Sufficient,
  Insufficient
}
/**
 * Created by pedro on 21/08/23.
 */
interface ConnectChecker {
  fun onConnectionStarted(url: String)
  fun onConnectionSuccess()
  fun onConnectionFailed(reason: String)
  fun onStreamingStats(bitrate: Long, bytesSent: Long, bytesQueued:Long, throughput: Throughput)
  fun onDisconnect()
  fun onAuthError()
  fun onAuthSuccess()
}