package org.example;

import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.ExecutionMode;
import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.core.execution.JobClient;
import org.apache.flink.runtime.dispatcher.Dispatcher;
import org.apache.flink.streaming.api.datastream.DataStreamSink;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.api.common.functions.FilterFunction;

public class Example {

    public static void main(String[] args) throws Exception {

        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        final ExecutionConfig executionConfig = env.getConfig();

        executionConfig.setMaxParallelism(10);
        executionConfig.setExecutionMode(ExecutionMode.PIPELINED);

        DataStream<Person> flintstones = env.fromElements(
                new Person("Fred", 35),
                new Person("Wilma", 35),
                new Person("Wilma", 35),
                new Person("Wilma", 35),
                new Person("Wilma", 35),
                new Person("Wilma", 35),
                new Person("Wilma", 35),
                new Person("Wilma", 35),
                new Person("Wilma", 35),
                new Person("Wilma", 35),
                new Person("Wilma", 35),
                new Person("Agata", 1),
                new Person("Danil", 31),
                new Person("Alexey", 23),
                new Person("Yana", 25),
                new Person("Bob", 20),
                new Person("Alice", 20),
                new Person("Thomas", 44),
                new Person("Pebbles", 2)
        );

        DataStream<Person> data_stream_adults = flintstones.filter(new FilterFunction<Person>() {
            @Override
            public boolean filter(Person person) throws Exception {
                return person.age >= 18;
            }
        });

        data_stream_adults.print();

//        int elem_1 = data_stream_adults.getId();
//        DebugPrint.deprint(String.valueOf(elem_1), "elem_1");

//        var elem_4 = data_stream_adults.executeAndCollect();
//        DebugPrint.deprint(String.valueOf(elem_4), "elem_4");





//        final JobClient jobClient = env.executeAsync();
//
//        final JobExecutionResult jobExecutionResult = jobClient.getJobExecutionResult().get();
//
//        DebugPrint.deprint(jobExecutionResult.toString(), "job result");
        var jobExecutionResult = env.execute();
        DebugPrint.deprint(jobExecutionResult.toString(), "job result");

//        var elem_2 = jobExecutionResult.getAllAccumulatorResults();
//        DebugPrint.deprint(elem_2.toString(), "getAllAccumulatorResults()");


//        int elem_3 = data_stream_adults.getId();
//        DebugPrint.deprint(String.valueOf(elem_3), "elem_3");



    }

    public static class Person {
        public String name;
        public Integer age;
        public Person() {}

        public Person(String name, Integer age) {
            this.name = name;
            this.age = age;
        }
    }
}
