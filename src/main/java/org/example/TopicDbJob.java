package org.example;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.jdbc.JdbcConnectionOptions;
import org.apache.flink.connector.jdbc.JdbcExactlyOnceOptions;
import org.apache.flink.connector.jdbc.JdbcExecutionOptions;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.connector.jdbc.JdbcSink;
import org.debug.print.DebugPrint;
import org.json.JSONObject;
import org.model.Application;

public class TopicDbJob {

    public static void main(String[] args) throws Exception {
        String broker = "localhost:9092";

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        KafkaSource<String> kafkaSource = KafkaSource.<String>builder()
                .setBootstrapServers(broker)
                .setTopics("caught-applications")
                .setGroupId("my-group")
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();


        var jdbcExecutionOptions = JdbcExecutionOptions.builder()
                .withBatchIntervalMs(200)
                .withBatchSize(1000)
                .withMaxRetries(4)
                .build();

        var jdbcConnectionOptions = new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
                .withUrl("jdbc:mariadb://localhost:3306/db_example")
                .withDriverName("org.mariadb.jdbc.Driver")
                .withUsername("user")
                .withPassword("password")
                .build();

        DataStream<String> dataStream = env.fromSource(kafkaSource, WatermarkStrategy.noWatermarks(), "Kafka Source")
                .setParallelism(1)
                .name("data stream from Topic");

        DataStream<Application> mappedDataStream = dataStream.map(new MapFunction<>() {
            @Override
            public Application map(String someString) throws Exception {

                JSONObject jsonObject = new JSONObject(
                        someString
                );

                DebugPrint.deprint(someString, "origin");

                Long id;
                Long ucdb_id;
                Float requested_amount;
                String product;

                try {
                    id = jsonObject.getJSONObject("after")
                            .getLong("id");

                } catch (Exception e) {
                    DebugPrint.deprint(e.getMessage());
                    id = 555555555555L;
                }

                try {
                    ucdb_id = jsonObject.getJSONObject("after")
                            .getLong("ucdb_id");
                } catch (Exception e) {
                    DebugPrint.deprint(e.getMessage());
                    ucdb_id = 555555555555L;
                }

                try {
                    requested_amount = jsonObject.getJSONObject("after")
                            .getFloat("requested_amount");
                } catch (Exception e) {
                    DebugPrint.deprint(e.getMessage());
                    requested_amount = 555555555.00F;
                }

                try {
                    product = jsonObject.getJSONObject("after")
                            .getString("product");
                } catch (Exception e) {
                    DebugPrint.deprint(e.getMessage());
                    product = "555555555555";
                }

//                DebugPrint.deprint(String.valueOf(id), "id");
//                DebugPrint.deprint(String.valueOf(ucdb_id), "ucdb_id");
//                DebugPrint.deprint(String.valueOf(ucdb_id), "ucdb_id");
//                DebugPrint.deprint(String.valueOf(ucdb_id), "ucdb_id");


                Application apl = new Application(id, ucdb_id, requested_amount, product);

                return apl;
            }
        });

        JdbcExactlyOnceOptions.builder()
                .withTransactionPerConnection(true)
                .build();


        mappedDataStream.addSink(
                JdbcSink.sink(
                "insert into Application_2 (id, ucdb_id, requested_amount, product) values (?,?,?,?)",
                (preparedStatement, someRecord) -> {
                    preparedStatement.setLong(1, someRecord.getId());
                    preparedStatement.setLong(2, someRecord.getUcdbId());
                    preparedStatement.setFloat(3, someRecord.getRequestedAmount());
                    preparedStatement.setString(4, someRecord.getProduct());
                },
                jdbcExecutionOptions,
                jdbcConnectionOptions
        )
        );


        env.execute("From topic to db");
    }
}
