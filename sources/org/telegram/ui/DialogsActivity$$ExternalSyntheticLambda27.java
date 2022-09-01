package org.telegram.ui;

import android.view.View;
import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.TLRPC$Dialog;

public final /* synthetic */ class DialogsActivity$$ExternalSyntheticLambda27 implements View.OnClickListener {
    public final /* synthetic */ DialogsActivity f$0;
    public final /* synthetic */ MessagesController.DialogFilter f$1;
    public final /* synthetic */ TLRPC$Dialog f$2;
    public final /* synthetic */ long f$3;

    public /* synthetic */ DialogsActivity$$ExternalSyntheticLambda27(DialogsActivity dialogsActivity, MessagesController.DialogFilter dialogFilter, TLRPC$Dialog tLRPC$Dialog, long j) {
        this.f$0 = dialogsActivity;
        this.f$1 = dialogFilter;
        this.f$2 = tLRPC$Dialog;
        this.f$3 = j;
    }

    public final void onClick(View view) {
        this.f$0.lambda$showChatPreview$29(this.f$1, this.f$2, this.f$3, view);
    }
}
