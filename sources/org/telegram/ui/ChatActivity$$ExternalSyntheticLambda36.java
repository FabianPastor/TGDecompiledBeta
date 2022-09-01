package org.telegram.ui;

import android.content.DialogInterface;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda36 implements DialogInterface.OnClickListener {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ String f$1;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda36(ChatActivity chatActivity, String str) {
        this.f$0 = chatActivity;
        this.f$1 = str;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$didPressMessageUrl$245(this.f$1, dialogInterface, i);
    }
}
