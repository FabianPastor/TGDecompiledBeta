package org.telegram.ui.Components;

import android.content.Context;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_importChatInvite;

public final /* synthetic */ class JoinGroupAlert$$ExternalSyntheticLambda6 implements Runnable {
    public final /* synthetic */ JoinGroupAlert f$0;
    public final /* synthetic */ TLRPC$TL_error f$1;
    public final /* synthetic */ Context f$2;
    public final /* synthetic */ boolean f$3;
    public final /* synthetic */ TLRPC$TL_messages_importChatInvite f$4;

    public /* synthetic */ JoinGroupAlert$$ExternalSyntheticLambda6(JoinGroupAlert joinGroupAlert, TLRPC$TL_error tLRPC$TL_error, Context context, boolean z, TLRPC$TL_messages_importChatInvite tLRPC$TL_messages_importChatInvite) {
        this.f$0 = joinGroupAlert;
        this.f$1 = tLRPC$TL_error;
        this.f$2 = context;
        this.f$3 = z;
        this.f$4 = tLRPC$TL_messages_importChatInvite;
    }

    public final void run() {
        this.f$0.lambda$new$3(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}