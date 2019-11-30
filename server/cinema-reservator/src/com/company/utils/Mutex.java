package com.company.utils;

public class Mutex {
    private boolean value;

    public Mutex(boolean val) {
        this.value = val;
    }

    public synchronized void P() {
        while (value == false)
            try {
                wait();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        value = false;
    }

    public synchronized void V() {
        value = true;
        notify();
    }
}
