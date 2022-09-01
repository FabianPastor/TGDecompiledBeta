package org.telegram.ui;

import org.telegram.messenger.MessagesStorage;
import org.telegram.tgnet.TLRPC$Chat;

public final /* synthetic */ class DialogsActivity$$ExternalSyntheticLambda50 implements MessagesStorage.BooleanCallback {
    public final /* synthetic */ DialogsActivity f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ TLRPC$Chat f$2;
    public final /* synthetic */ long f$3;
    public final /* synthetic */ boolean f$4;

    public /* synthetic */ DialogsActivity$$ExternalSyntheticLambda50(DialogsActivity dialogsActivity, int i, TLRPC$Chat tLRPC$Chat, long j, boolean z) {
        this.f$0 = dialogsActivity;
        this.f$1 = i;
        this.f$2 = tLRPC$Chat;
        this.f$3 = j;
        this.f$4 = z;
    }

    public final void run(boolean z) {
        this.f$0.lambda$performSelectedDialogsAction$40(this.f$1, this.f$2, this.f$3, this.f$4, z);
    }
}
