package org.telegram.messenger;

import android.content.Context;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ActionBar.AlertDialog;

final /* synthetic */ class MessagesController$$Lambda$93 implements RequestDelegate {
    private final MessagesController arg$1;
    private final Context arg$2;
    private final AlertDialog arg$3;

    MessagesController$$Lambda$93(MessagesController messagesController, Context context, AlertDialog alertDialog) {
        this.arg$1 = messagesController;
        this.arg$2 = context;
        this.arg$3 = alertDialog;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$convertToMegaGroup$142$MessagesController(this.arg$2, this.arg$3, tLObject, tL_error);
    }
}
