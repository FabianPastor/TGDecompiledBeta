package org.telegram.messenger;

final /* synthetic */ class SendMessagesHelper$$Lambda$50 implements Runnable {
    private final SendMessagesHelper arg$1;
    private final String arg$2;

    SendMessagesHelper$$Lambda$50(SendMessagesHelper sendMessagesHelper, String str) {
        this.arg$1 = sendMessagesHelper;
        this.arg$2 = str;
    }

    public void run() {
        this.arg$1.lambda$null$20$SendMessagesHelper(this.arg$2);
    }
}
