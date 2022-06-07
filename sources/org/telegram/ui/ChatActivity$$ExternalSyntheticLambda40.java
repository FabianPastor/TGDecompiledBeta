package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.tgnet.TLRPC$User;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda40 implements DialogInterface.OnClickListener {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ TLRPC$User f$1;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda40(ChatActivity chatActivity, TLRPC$User tLRPC$User) {
        this.f$0 = chatActivity;
        this.f$1 = tLRPC$User;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$onTransitionAnimationEnd$132(this.f$1, dialogInterface, i);
    }
}
