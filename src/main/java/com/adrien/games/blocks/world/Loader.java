package com.adrien.games.blocks.world;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedList;
import java.util.Queue;

public class Loader extends Thread {

    private static final Logger LOG = LogManager.getLogger(Loader.class);
    private static final String THREAD_NAME = "Loader";

    private final Queue<Chunk> chunksToLoad;
    private boolean shouldRun;

    public Loader() {
        super.setName(THREAD_NAME);
        this.chunksToLoad = new LinkedList<>();
        this.shouldRun = false;
    }

    @Override
    public void run() {
        LOG.trace("Started loader thread");
        this.shouldRun = true;
        while (this.shouldRun) {
            if (!this.chunksToLoad.isEmpty()) {
                this.chunksToLoad.poll().load();
            } else {
                this.takeANap(1);
            }
        }
        LOG.trace("Ended loader thread");
    }

    private void takeANap(final long ms) {
        try {
            Thread.sleep(ms);
        } catch (final InterruptedException exception) {
            LOG.error("An error occurred while trying to put the thread to rest", exception);
        }
    }

    public void load(final Chunk chunk) {
        this.chunksToLoad.add(chunk);
    }

    public void end() {
        this.shouldRun = false;
    }

}
