/*
 * Copyright 2022 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gcs

import com.spotify.scio.values.SCollection
import com.spotify.scio.{Args, ContextAndArgs, ScioContext}
import org.slf4j.LoggerFactory


object ReadText {

  private val log = LoggerFactory.getLogger(this.getClass)

  def main(cmdlineArgs: Array[String]): Unit = {
    val (scontext: ScioContext, opts: Args) = ContextAndArgs(cmdlineArgs)
    implicit val sc: ScioContext = scontext

    // pipeline option parameters
    val input: String = opts.getOrElse("input", "gs://apache-beam-samples/shakespeare/kinglear.txt")

    val read: SCollection[String] = sc.textFile(input)

    read.map(log.info(_))


    sc.run()
  }
}
