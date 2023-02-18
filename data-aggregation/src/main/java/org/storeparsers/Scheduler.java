package org.storeparsers;

import java.util.Timer;

public class Scheduler {

    private static final int SIX_HOURS = 21600000;
    public static void main(String[] args) {
        Timer timer = new Timer();
        StoresParser task = new StoresParser();
        task.run();
        timer.scheduleAtFixedRate(task, 0, SIX_HOURS);
    }
}
