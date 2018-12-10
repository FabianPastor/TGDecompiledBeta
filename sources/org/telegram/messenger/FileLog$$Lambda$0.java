package org.telegram.messenger;

final /* synthetic */ class FileLog$$Lambda$0 implements Runnable {
    private final String arg$1;
    private final Throwable arg$2;

    FileLog$$Lambda$0(String str, Throwable th) {
        this.arg$1 = str;
        this.arg$2 = th;
    }

    public void run() {
        FileLog.lambda$e$0$FileLog(this.arg$1, this.arg$2);
    }
}
