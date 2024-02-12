package org.example;

public class Main {
    public static void main (String[] args) {


//        String sysEnvStr = System.getenv("TEST");
//        System.out.println(sysEnvStr);

        for( Object o : System.getenv().entrySet() ){
            System.out.println( o );
        }

    }
}
