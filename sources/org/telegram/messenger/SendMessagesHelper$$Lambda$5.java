package org.telegram.messenger;

final /* synthetic */ class SendMessagesHelper$$Lambda$5 implements Runnable {
    private final SendMessagesHelper arg$1;
    private final long arg$2;
    private final int arg$3;
    private final byte[] arg$4;

    SendMessagesHelper$$Lambda$5(SendMessagesHelper sendMessagesHelper, long j, int i, byte[] bArr) {
        this.arg$1 = sendMessagesHelper;
        this.arg$2 = j;
        this.arg$3 = i;
        this.arg$4 = bArr;
    }

    public void run() {
        this.arg$1.lambda$sendNotificationCallback$14$SendMessagesHelper(this.arg$2, this.arg$3, this.arg$4);
    }
}
