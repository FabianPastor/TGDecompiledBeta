package org.telegram.messenger;

final /* synthetic */ class SendMessagesHelper$$Lambda$54 implements Runnable {
    private final SendMessagesHelper arg$1;
    private final String arg$2;

    SendMessagesHelper$$Lambda$54(SendMessagesHelper sendMessagesHelper, String str) {
        this.arg$1 = sendMessagesHelper;
        this.arg$2 = str;
    }

    public void run() {
        this.arg$1.lambda$null$12$SendMessagesHelper(this.arg$2);
    }
}
