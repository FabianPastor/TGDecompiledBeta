package org.telegram.ui;

import org.telegram.messenger.MessagesController;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivity$A6saBtiVC7KZVlD_BKI_DC5XR4I implements Runnable {
    private final /* synthetic */ ChatActivity f$0;
    private final /* synthetic */ CharSequence f$1;
    private final /* synthetic */ MessagesController f$2;
    private final /* synthetic */ boolean f$3;

    public /* synthetic */ -$$Lambda$ChatActivity$A6saBtiVC7KZVlD_BKI_DC5XR4I(ChatActivity chatActivity, CharSequence charSequence, MessagesController messagesController, boolean z) {
        this.f$0 = chatActivity;
        this.f$1 = charSequence;
        this.f$2 = messagesController;
        this.f$3 = z;
    }

    public final void run() {
        this.f$0.lambda$searchLinks$51$ChatActivity(this.f$1, this.f$2, this.f$3);
    }
}
