package org.telegram.ui;

public final /* synthetic */ class ChatActivity$112$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ Object[] f$3;

    public /* synthetic */ ChatActivity$112$$ExternalSyntheticLambda0(ChatActivity chatActivity, int i, int i2, Object[] objArr) {
        this.f$0 = chatActivity;
        this.f$1 = i;
        this.f$2 = i2;
        this.f$3 = objArr;
    }

    public final void run() {
        this.f$0.didReceivedNotification(this.f$1, this.f$2, this.f$3);
    }
}
