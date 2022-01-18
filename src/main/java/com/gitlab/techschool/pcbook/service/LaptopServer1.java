package com.gitlab.techschool.pcbook.service;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class LaptopServer1 {
    private static final Logger logger= Logger.getLogger(LaptopServer1.class.getName());

    private final int port;
    private final Server server;

    public LaptopServer1(int port,LaptopStore laptopStore){
        this(ServerBuilder.forPort(port),port,laptopStore);
    }
    public LaptopServer1(ServerBuilder serverBuilder,int port,LaptopStore store){
        this.port=port;
        LaptopService laptopService = new LaptopService(store);
        server = serverBuilder.addService(laptopService).build();

    }

    public void start() throws IOException {
        server.start();
        logger.info("server started on port " + port);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                System.err.println("shut down gRPC server because JVM shuts down");
                try {
                    LaptopServer1.this.stop();
                } catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                }
                System.err.println("server shut down");
            }
        });
    }

    public void stop() throws InterruptedException {
        if(server!=null)
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
    }

    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        InMemoryLaptopStore store = new InMemoryLaptopStore();
        LaptopServer1 server = new LaptopServer1(9092,store);
        server.start();
        server.blockUntilShutdown();
    }
}
