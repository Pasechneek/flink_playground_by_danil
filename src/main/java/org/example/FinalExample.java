package org.example;

import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import com.ververica.cdc.connectors.mysql.source.config.ServerIdRange;
import io.debezium.data.Enum;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.ExecutionMode;
import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.core.execution.JobClient;
import org.apache.flink.streaming.api.datastream.DataStreamSink;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import com.ververica.cdc.debezium.JsonDebeziumDeserializationSchema;

import java.io.DataInputStream;


public class FinalExample {
    public static void main(String[] args) throws Exception {

//        final StreamExecutionEnvironment env = StreamExecutionEnvironment.createRemoteEnvironment("localhost", 3306, "flink-connector-mysql-cdc");
//        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();


        MySqlSource<String> mySQLSource = MySqlSource.<String>builder()
                .hostname("localhost")
                .port(3306)
                .databaseList("example")
                .tableList("example.Application")
                .username("hello")
                .password("hello")
                .serverId(String.valueOf(new ServerIdRange(5401, 5404)))
                .deserializer(new JsonDebeziumDeserializationSchema())
                .build();
        
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.enableCheckpointing(300000);

        final ExecutionConfig executionConfig = env.getConfig();
        executionConfig.setMaxParallelism(10);
        executionConfig.setExecutionMode(ExecutionMode.PIPELINED);


        DataStream<String> dataStream = env.fromSource(mySQLSource, WatermarkStrategy.noWatermarks(), "MySQL Source")
                .setParallelism(4);
//                .print().setParallelism(1);


        dataStream
                .print().setParallelism(1);



//        env.execute("Print MySQL Snapshot + Binlog");
        var jobExecutionResult = env.execute();
        DebugPrint.deprint(jobExecutionResult.toString(), "job result");

//        DebugPrint.deprint(dataStream, "data stream");
//        DataStream<Tuple2<String, Integer>> dataStream = env
//                .addSource(<Tuple2, int>)
//
//        DataStream<SourceObject> my_first_stream = env.addSource(new SourceObjectSource());
//
//        DataStream<EnrichedItem> enrichedNYCRides = my_first_stream
//                .keyBy(KeySelector<> obj -> obj.id)
//                .filter(new SomeObject.NYCFilter())
//                .map(new Enrichment());
//    }
//}
//
//public static class Enrichment implements MapFunction<TaxiRide, EnrichedItem> {
//    /*
//    A MapFunction is suitable only when performing a one-to-one transformation:
//    for each and every stream element coming in, map() will emit one transformed element.
//    Otherwise, you will want to use flatmap()
//     */
//
//    @Override
//    public EnrichedItem map(SourceObject sourceObject) throws Exception {
//        return new EnrichedItem(sourceObject);
    }
}
