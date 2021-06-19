package com.strawci.ci;

public class Bootstrap {
    public static void main (final String[] args){
        Server straw = new Server();

        try {
            straw.start();
        } catch (final Exception exception) {
            System.out.println("");
            System.out.println("========= Application Crash =========");
            System.out.println("> Error: " + exception.getLocalizedMessage());
            System.out.println("> Stacktrace");
            exception.printStackTrace();
            System.out.println("=====================================");
            System.out.println("");
        }
    }
}