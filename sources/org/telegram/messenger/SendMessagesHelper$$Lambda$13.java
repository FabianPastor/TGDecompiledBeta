package org.telegram.messenger;

import java.util.ArrayList;

final /* synthetic */ class SendMessagesHelper$$Lambda$13 implements Runnable {
    private final SendMessagesHelper arg$1;
    private final ArrayList arg$2;
    private final ArrayList arg$3;
    private final ArrayList arg$4;
    private final ArrayList arg$5;

    SendMessagesHelper$$Lambda$13(SendMessagesHelper sendMessagesHelper, ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3, ArrayList arrayList4) {
        this.arg$1 = sendMessagesHelper;
        this.arg$2 = arrayList;
        this.arg$3 = arrayList2;
        this.arg$4 = arrayList3;
        this.arg$5 = arrayList4;
    }

    public void run() {
        this.arg$1.lambda$processUnsentMessages$42$SendMessagesHelper(this.arg$2, this.arg$3, this.arg$4, this.arg$5);
    }
}
