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

package bigquery;
import com.google.api.services.bigquery.model.TableRow;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.gcp.bigquery.BigQueryIO;
import org.apache.beam.sdk.options.Default;
import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReadTableBQ {
    private static final Logger LOG = LoggerFactory.getLogger(ReadTableBQ.class);

    // Parameter parser
    public interface ReadTableBQOptions extends PipelineOptions {
        @Description("Table to read")
        @Default.String("bigquery-public-data:austin_bikeshare.bikeshare_stations")
        String getTable();

        void setTable(String value);
    }

    public static void main(String[] args) {
        // Reference the extended class
        ReadTableBQOptions options = PipelineOptionsFactory.fromArgs(args).withValidation().as(ReadTableBQOptions.class);

        Pipeline p = Pipeline.create(options);

        p
                .apply(BigQueryIO.readTableRows().from(options.getTable()))
                .apply(ParDo.of(new DoFn<TableRow, String>() {
                            @ProcessElement
                            public void processElement(ProcessContext c) {
                                c.output(c.element().toString());
                            }
                        })
                );

        p.run();
    }

}
