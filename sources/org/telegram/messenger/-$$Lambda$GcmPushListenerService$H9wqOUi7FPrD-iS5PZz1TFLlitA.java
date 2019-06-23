package org.telegram.messenger;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$GcmPushListenerService$H9wqOUi7FPrD-iS5PZz1TFLlitA implements Runnable {
    private final /* synthetic */ int f$0;
    private final /* synthetic */ String f$1;

    public /* synthetic */ -$$Lambda$GcmPushListenerService$H9wqOUi7FPrD-iS5PZz1TFLlitA(int i, String str) {
        this.f$0 = i;
        this.f$1 = str;
    }

    public final void run() {
        MessagesController.getInstance(this.f$0).registerForPush(this.f$1);
    }
}
