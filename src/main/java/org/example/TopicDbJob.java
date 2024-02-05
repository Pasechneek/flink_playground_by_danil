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
import org.example.map.MyApplicationMap;
import org.json.JSONObject;
import org.model.Application;

//import static jdk.nashorn.internal.objects.NativeJava.type;

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
                .withUrl("jdbc:mariadb://" + System.getenv("EXAMPLE_HOST") + ":3306/" + System.getenv("EXAMPLE_DB"))
                .withDriverName("org.mariadb.jdbc.Driver")
                .withUsername(System.getenv("EXAMPLE_USER"))
                .withPassword(System.getenv("EXAMPLE_PASSWORD"))
                .build();

        DataStream<String> dataStream = env.fromSource(kafkaSource, WatermarkStrategy.noWatermarks(), "Kafka Source")
                .setParallelism(1)
                .name("data stream from Topic");

//        MyApplicationMap myMap = new MyApplicationMap();

        DataStream<Application> mappedDataStream = dataStream.map(new MapFunction<>() {
            @Override
            public Application map(String stringThatContainJson) throws Exception {

                JSONObject jsonObject = new JSONObject(
                        stringThatContainJson
                );

//                DebugPrint.deprint(stringThatContainJson, "origin");

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
                    var variable = jsonObject.getClass();
                    DebugPrint.deprint(variable.toString(), "JsonObject variable");
                    requested_amount = 555555555.00F;
                }

                try {
                    product = jsonObject.getJSONObject("after")
                            .getString("product");
                } catch (Exception e) {
                    DebugPrint.deprint(e.getMessage());
                    product = "555555555555";
                }

                Application apl = new Application(id, ucdb_id, requested_amount, product);

                return apl;
            }
        });

        JdbcExactlyOnceOptions.builder()
                .withTransactionPerConnection(true)
                .build();


        String query = "INSERT into Application_2 (id, ucdb_id, requested_amount, product) VALUES (?,?,?,?) " +
                "ON DUPLICATE KEY UPDATE " +
                "id = ?, ucdb_id = ?, requested_amount = ?, product = ?;";


        mappedDataStream.addSink(
                JdbcSink
                        .sink(
                query,
                (preparedStatement, someRecord) -> {
                    preparedStatement.setLong(1, someRecord.getId());
                    preparedStatement.setLong(2, someRecord.getUcdbId());
                    preparedStatement.setFloat(3, someRecord.getRequestedAmount());
                    preparedStatement.setString(4, someRecord.getProduct());
                    preparedStatement.setLong(5, someRecord.getId());
                    preparedStatement.setLong(6, someRecord.getUcdbId());
                    preparedStatement.setFloat(7, someRecord.getRequestedAmount());
                    preparedStatement.setString(8, someRecord.getProduct());
                },
                jdbcExecutionOptions,
                jdbcConnectionOptions
        )
        );

        env.execute("From topic to db");
    }
}
