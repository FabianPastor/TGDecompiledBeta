package org.telegram.ui;

import android.content.DialogInterface;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda20 implements DialogInterface.OnCancelListener {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda20(ChatActivity chatActivity, int i) {
        this.f$0 = chatActivity;
        this.f$1 = i;
    }

    public final void onCancel(DialogInterface dialogInterface) {
        this.f$0.lambda$processSelectedOption$181(this.f$1, dialogInterface);
    }
}
