package org.example;

import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import com.ververica.cdc.connectors.mysql.source.config.ServerIdRange;
import com.ververica.cdc.debezium.JsonDebeziumDeserializationSchema;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.ExecutionMode;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple4;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.debug.print.DebugPrint;
import org.example.map.StringToPOJOMap;
import org.model.Application;

import javax.management.timer.Timer;

public class ModifyDataFromDbToTopic {
    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.enableCheckpointing(Timer.ONE_MINUTE);
        env.setParallelism(1);
        env.getConfig().setAutoWatermarkInterval(5000);

        String table = "Application";

        MySqlSource<String> mySQLSource = MySqlSource.<String>builder()
                .hostname(System.getenv("EXAMPLE_HOST"))
                .port(3306)
                .databaseList(System.getenv("EXAMPLE_DB"))
                .tableList(System.getenv("EXAMPLE_DB") + "." + table)
                .username(System.getenv("EXAMPLE_USER"))
                .password(System.getenv("EXAMPLE_PASSWORD"))
                .serverId(String.valueOf(new ServerIdRange(5401, 5404)))
                .deserializer(new JsonDebeziumDeserializationSchema())
                .build();

//        DebugPrint.deprint(System.getenv("EXAMPLE_HOST"), "EXAMPLE_HOST");
//        DebugPrint.deprint(System.getenv("EXAMPLE_DB"), "EXAMPLE_DB");
//        DebugPrint.deprint(System.getenv("EXAMPLE_USER"), "EXAMPLE_USER");
//        DebugPrint.deprint(System.getenv("EXAMPLE_PASSWORD"), "EXAMPLE_PASSWORD");



        final ExecutionConfig executionConfig = env.getConfig();
        executionConfig.setMaxParallelism(10);
        executionConfig.setExecutionMode(ExecutionMode.PIPELINED);

        DataStream<String> dataStream = env.fromSource(mySQLSource, WatermarkStrategy.noWatermarks(), "MySQL Source")
                .setParallelism(1) // mandatory
                .name("data stream from, MySQL");

        dataStream
                .print("dataStream");


        DataStream<Application> mappedToApplication = dataStream
                .map(new MapFunction<>() {
                    @Override
                    public Application map(String stringThatContainJson) throws Exception {
                        StringToPOJOMap myMap = new StringToPOJOMap();
                        return myMap.map(stringThatContainJson);
                    }
                });

        mappedToApplication.print("mappedToApplication");

//        TypeInformation<Application> applType = Types.POJO(Application.class);

//        mappedToApplication.keyBy(mappedToApplication => mappedToApplication.id);


//        KeyedStream<Object, Object> mappedToTuple;
        var mappedToTuple = mappedToApplication.map(new MapFunction<>() {
            @Override
            public Tuple4<Long, Long, Float, String> map(Application appl) {
                return Tuple4.of(
                        appl.getId(),
                        appl.getUcdbId(),
                        appl.getRequestedAmount(),
                        appl.getProduct()
                );
            }
        });



        TypeInformation<Tuple4<Long, Long, Float, String>> tupleType = Types
                .TUPLE(Types.LONG, Types.LONG, Types.FLOAT, Types.STRING);

//        mappedToTuple.keyBy(mappedToTuple,  tupleType);


        mappedToTuple
                .print("mappedToTuple");
        var keyedStream = mappedToTuple
                .keyBy(value -> ((Tuple4<Long, Long, Float, String>)value).f1)
                .windowAll(TumblingEventTimeWindows.of(Time.seconds(10)))
                ;

        int pos = 0;
        var res = keyedStream.sum(pos)
                .print("sum");

        DebugPrint.deprint(res.toString(), "RES");

//        KeyedStream<Tuple4<Long, Long, Float, String>, Long> keyed = mappedToTuple.keyBy(value -> ((Tuple4<Long, Long, Float, String>)value).f1);

//        KeyedStream<Tuple4<Long, Long, Float, String>, Long> keyed = mappedToTuple
//                .keyBy(new KeySelector<Tuple4<Long, Long, Float, String>, Long>() {
//            @Override
//            public Long getKey(Tuple4<Long, Long, Float, String> value) throws Exception {
//                return value.f1;
//            }
//        });


//        mappedToTuple.assignTimestampsAndWatermarks(


//        KeyedStream<Object, Tuple> keyed = mappedToTuple.keyBy();
//        DebugPrint.deprint(keyed.toString(), "KEYED");
////
//        keyed.print("keyed");









        String broker = System.getenv("KAFKA_HOST") + ":9092";

        KafkaSink<String> sinkApplications = KafkaSink.<String>builder()
                .setBootstrapServers(broker)
                .setRecordSerializer(KafkaRecordSerializationSchema.builder()
                        .setTopic("caught-applications")
                        .setValueSerializationSchema(new SimpleStringSchema())
                        .build()
                )
//                .setTransactionalIdPrefix("my-record-producer")
//                .setDeliveryGuarantee(DeliveryGuarantee.EXACTLY_ONCE)
                .setDeliveryGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)
                .build();

        dataStream
                .sinkTo(sinkApplications)
                .name("Kafka Sink")
                .setParallelism(1);

        DebugPrint.deprint(dataStream.print().toString());

        env.execute("Print MySQL Snapshot + Binlog");
    }
}
