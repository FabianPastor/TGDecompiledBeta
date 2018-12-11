package org.telegram.messenger;

final /* synthetic */ class FileLog$$Lambda$2 implements Runnable {
    private final Throwable arg$1;

    FileLog$$Lambda$2(Throwable th) {
        this.arg$1 = th;
    }

    public void run() {
        FileLog.lambda$e$2$FileLog(this.arg$1);
    }
}
