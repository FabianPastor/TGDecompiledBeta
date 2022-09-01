package org.telegram.ui;

import android.content.DialogInterface;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda35 implements DialogInterface.OnClickListener {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ boolean[] f$2;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda35(ChatActivity chatActivity, int i, boolean[] zArr) {
        this.f$0 = chatActivity;
        this.f$1 = i;
        this.f$2 = zArr;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$processSelectedOption$200(this.f$1, this.f$2, dialogInterface, i);
    }
}
