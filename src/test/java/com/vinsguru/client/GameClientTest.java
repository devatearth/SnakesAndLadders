package com.vinsguru.client;

import com.vinsguru.game.Die;
import com.vinsguru.game.GameServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.concurrent.CountDownLatch;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GameClientTest {


    private GameServiceGrpc.GameServiceStub stub;

    @BeforeAll
    public void setUp(){
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost",6565).usePlaintext().build();
        this.stub = GameServiceGrpc.newStub(channel);
    }

    @Test
    public void clientGame() throws InterruptedException {
        CountDownLatch latch =  new CountDownLatch(1);
        GameStateStreamingResponseObserver gameStateStreamingResponseObserver = new GameStateStreamingResponseObserver(latch);
        StreamObserver<Die> dieStreamObserver = this.stub.roll(gameStateStreamingResponseObserver);
        gameStateStreamingResponseObserver.setDieStreamObserver(dieStreamObserver);
        gameStateStreamingResponseObserver.roll();
        latch.await();
    }
}
