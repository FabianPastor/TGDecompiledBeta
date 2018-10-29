package org.telegram.messenger;

final /* synthetic */ class SendMessagesHelper$$Lambda$29 implements Runnable {
    private final String arg$1;
    private final int arg$2;
    private final long arg$3;

    SendMessagesHelper$$Lambda$29(String str, int i, long j) {
        this.arg$1 = str;
        this.arg$2 = i;
        this.arg$3 = j;
    }

    public void run() {
        SendMessagesHelper.lambda$null$50$SendMessagesHelper(this.arg$1, this.arg$2, this.arg$3);
    }
}
