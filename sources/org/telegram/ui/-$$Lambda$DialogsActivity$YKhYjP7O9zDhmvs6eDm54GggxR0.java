package org.telegram.ui;

import org.telegram.tgnet.TLRPC.Chat;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DialogsActivity$YKhYjP7O9zDhmvs6eDm54GggxR0 implements Runnable {
    private final /* synthetic */ DialogsActivity f$0;
    private final /* synthetic */ Chat f$1;
    private final /* synthetic */ long f$2;
    private final /* synthetic */ boolean f$3;

    public /* synthetic */ -$$Lambda$DialogsActivity$YKhYjP7O9zDhmvs6eDm54GggxR0(DialogsActivity dialogsActivity, Chat chat, long j, boolean z) {
        this.f$0 = dialogsActivity;
        this.f$1 = chat;
        this.f$2 = j;
        this.f$3 = z;
    }

    public final void run() {
        this.f$0.lambda$didReceivedNotification$19$DialogsActivity(this.f$1, this.f$2, this.f$3);
    }
}
