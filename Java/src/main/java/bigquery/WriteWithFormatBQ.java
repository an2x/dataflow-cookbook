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
import com.google.api.services.bigquery.model.TableFieldSchema;
import com.google.api.services.bigquery.model.TableRow;
import com.google.api.services.bigquery.model.TableSchema;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.gcp.bigquery.BigQueryIO;
import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.Create;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WriteWithFormatBQ {

    private static final Logger LOG = LoggerFactory.getLogger(WriteWithFormatBQ.class);

    public interface WriteWithFormatBQOptions extends PipelineOptions {
        @Description("Table to write to")
        String getTable();

        void setTable(String value);
    }

    public static void main(String[] args) {

        WriteWithFormatBQOptions options = PipelineOptionsFactory.fromArgs(args).withValidation().as(WriteWithFormatBQOptions.class);

        Pipeline p = Pipeline.create(options);

        final List<String> elements = Arrays.asList(
                "John, 1990, USA",
                "Charles, 1995, USA",
                "Alice, 1997, Spain",
                "Bob, 1995, USA",
                "Amanda, 1991, France",
                "Alex, 1999, Mexico",
                "Eliza, 2000, Japan"
        );

        List<TableFieldSchema> fields = new ArrayList<>();
        fields.add(new TableFieldSchema().setName("name").setType("STRING"));
        fields.add(new TableFieldSchema().setName("year").setType("INTEGER"));
        fields.add(new TableFieldSchema().setName("country").setType("STRING"));
        TableSchema schema = new TableSchema().setFields(fields);

        p
                .apply(Create.of(elements))
                // to BigQuery with format
                // Using `withFormatFunction` has better performance than formatting in a previous DoFn, due to the
                // encoding of TableRows
                .apply(BigQueryIO.<String>write()
                        .withSchema(schema)
                        .withFormatFunction((String element) -> {
                            String[] columns = element.split(", ");

                            TableRow row = new TableRow();
                            row.set("name", columns[0]);
                            row.set("year", columns[1]);
                            row.set("country", columns[2]);

                            return row;
                        })
                        .to(options.getTable())
                        .withCreateDisposition(BigQueryIO.Write.CreateDisposition.CREATE_IF_NEEDED)
                        .withWriteDisposition(BigQueryIO.Write.WriteDisposition.WRITE_APPEND));
        p.run();
    }
}
