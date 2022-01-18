package com.gitlab.techschool.pcbook.service;

import com.gitlab.techschool.pcbook.pb.CreateLaptopRequest;
import com.gitlab.techschool.pcbook.pb.CreateLaptopResponse;
import com.gitlab.techschool.pcbook.pb.Laptop;
import com.gitlab.techschool.pcbook.pb.LaptopServiceGrpc;
import com.gitlab.techschool.pcbook.sample.Generator;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LaptopClient1 {
    private static final Logger logger = Logger.getLogger(LaptopClient1.class.getName());
    private final ManagedChannel channel;
    private final LaptopServiceGrpc.LaptopServiceBlockingStub blockingStub;

    public LaptopClient1(String host,int port){
       channel = ManagedChannelBuilder.forAddress(host,port)
               .usePlaintext()
               .build();
       blockingStub = LaptopServiceGrpc.newBlockingStub(channel);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public void createLaptop(Laptop laptop){
       // System.out.println("I am inside LaptopClient1: method createLaptop()");
        CreateLaptopRequest request = CreateLaptopRequest.newBuilder().setLaptop(laptop).build();
        CreateLaptopResponse response = CreateLaptopResponse.getDefaultInstance();
        try{
            response=blockingStub.createLaptop(request);
           // System.out.println("I am here 1");
        }catch (Exception e){
            logger.log(Level.SEVERE,"request failed:"+ e.getMessage());
            return;
        }
        logger.info("Laptop created with ID :"+response.getId());

    }

    public static void main(String[] args) throws InterruptedException {
        LaptopClient1 client = new LaptopClient1("localhost",9092);

        Generator generator = new Generator();
        Laptop laptop=generator.NewLaptop().toBuilder().setId("d63d84d0-065b-439f-a5f3-da86f3320c08").build();
        //System.out.println("Laptop is :"+laptop);
        try {
            client.createLaptop(laptop);
        }
        finally {
            client.shutdown();
        }
    }

}
