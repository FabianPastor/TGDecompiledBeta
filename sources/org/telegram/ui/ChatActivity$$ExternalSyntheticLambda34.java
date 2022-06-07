package org.telegram.ui;

import android.content.DialogInterface;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda34 implements DialogInterface.OnClickListener {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ String f$1;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda34(ChatActivity chatActivity, String str) {
        this.f$0 = chatActivity;
        this.f$1 = str;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$didPressMessageUrl$241(this.f$1, dialogInterface, i);
    }
}
