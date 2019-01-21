package org.telegram.messenger;

import android.content.Context;
import org.telegram.messenger.MessagesStorage.IntCallback;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ActionBar.AlertDialog;

final /* synthetic */ class MessagesController$$Lambda$100 implements RequestDelegate {
    private final MessagesController arg$1;
    private final Context arg$2;
    private final AlertDialog arg$3;
    private final IntCallback arg$4;

    MessagesController$$Lambda$100(MessagesController messagesController, Context context, AlertDialog alertDialog, IntCallback intCallback) {
        this.arg$1 = messagesController;
        this.arg$2 = context;
        this.arg$3 = alertDialog;
        this.arg$4 = intCallback;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$convertToMegaGroup$157$MessagesController(this.arg$2, this.arg$3, this.arg$4, tLObject, tL_error);
    }
}
