package org.telegram.ui;

import android.content.DialogInterface;

public final /* synthetic */ class ChatLinkActivity$$ExternalSyntheticLambda1 implements DialogInterface.OnCancelListener {
    public final /* synthetic */ ChatLinkActivity f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ ChatLinkActivity$$ExternalSyntheticLambda1(ChatLinkActivity chatLinkActivity, int i) {
        this.f$0 = chatLinkActivity;
        this.f$1 = i;
    }

    public final void onCancel(DialogInterface dialogInterface) {
        this.f$0.lambda$linkChat$14(this.f$1, dialogInterface);
    }
}
