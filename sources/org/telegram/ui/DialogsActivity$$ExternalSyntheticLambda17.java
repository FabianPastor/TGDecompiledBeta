package org.telegram.ui;

import android.view.View;
import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class DialogsActivity$$ExternalSyntheticLambda17 implements View.OnClickListener {
    public final /* synthetic */ DialogsActivity f$0;
    public final /* synthetic */ MessagesController.DialogFilter f$1;
    public final /* synthetic */ TLRPC.Dialog f$2;
    public final /* synthetic */ long f$3;

    public /* synthetic */ DialogsActivity$$ExternalSyntheticLambda17(DialogsActivity dialogsActivity, MessagesController.DialogFilter dialogFilter, TLRPC.Dialog dialog, long j) {
        this.f$0 = dialogsActivity;
        this.f$1 = dialogFilter;
        this.f$2 = dialog;
        this.f$3 = j;
    }

    public final void onClick(View view) {
        this.f$0.m3414lambda$showChatPreview$27$orgtelegramuiDialogsActivity(this.f$1, this.f$2, this.f$3, view);
    }
}
