package com.vinsguru.client;

import com.google.common.util.concurrent.Uninterruptibles;
import com.vinsguru.game.Die;
import com.vinsguru.game.GameState;
import com.vinsguru.game.Player;
import io.grpc.stub.StreamObserver;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class GameStateStreamingResponseObserver implements StreamObserver<GameState> {

    private CountDownLatch latch;

    private StreamObserver<Die> dieStreamObserver;

    public GameStateStreamingResponseObserver(CountDownLatch latch) {
        this.latch = latch;
    }

    public void setDieStreamObserver(StreamObserver<Die> dieStreamObserver) {
        this.dieStreamObserver = dieStreamObserver;
    }

    @Override
    public void onNext(GameState gameState) {
        gameState.getPlayerList().stream().forEach(p-> System.out.println(p.getName() + " -- "+p.getPosition()));
        List<Player> playerList = gameState.getPlayerList();
        boolean isGameOver = playerList.stream().anyMatch(p -> p.getPosition() == 100);
        if (!isGameOver){
            Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
            this.roll();
        }else{
            this.dieStreamObserver.onCompleted();
        }
    }

    @Override
    public void onError(Throwable throwable) {
        this.latch.countDown();
    }

    @Override
    public void onCompleted() {
        this.latch.countDown();
    }
    public  void roll(){
        int dieValue = ThreadLocalRandom.current().nextInt(1,7);
        Die die = Die.newBuilder().setValue(dieValue).build();
        this.dieStreamObserver.onNext(die);
    }
}
