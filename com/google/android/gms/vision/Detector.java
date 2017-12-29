package com.google.android.gms.vision;

public abstract class Detector<T> {
    private final Object zzkvt = new Object();
    private Processor<T> zzkvu;

    public interface Processor<T> {
        void release();
    }

    public boolean isOperational() {
        return true;
    }

    public void release() {
        synchronized (this.zzkvt) {
            if (this.zzkvu != null) {
                this.zzkvu.release();
                this.zzkvu = null;
            }
        }
    }
}
