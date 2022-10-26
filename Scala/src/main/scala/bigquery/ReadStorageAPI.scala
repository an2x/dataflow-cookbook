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

package bigquery

import com.spotify.scio.bigquery._
import com.spotify.scio.{Args, ContextAndArgs, ScioContext}
import org.slf4j.LoggerFactory


object ReadStorageAPI {

  private val log = LoggerFactory.getLogger(this.getClass)

  def main(cmdlineArgs: Array[String]): Unit = {
    val (scontext: ScioContext, opts: Args) = ContextAndArgs(cmdlineArgs)
    implicit val sc: ScioContext = scontext

    // pipeline option parameters
    val tableOp: String = opts.getOrElse("table", "bigquery-public-data:austin_bikeshare.bikeshare_stations")

    val table = Table.Spec(tableOp)

    val bqRead = sc.bigQueryStorage(
      table,
      selectedFields = List("station_id", "status"),
      rowRestriction = "status = \"active\""
    )

    bqRead
      .map {
        extractRow
      }
      .map {
        log.info(_)
      }

    def extractRow(row: TableRow): String = {
      val id = row.getLong("station_id")
      val status = row.getString("status")
      s"Bike station $id is $status"
    }

    sc.run()
  }


}
