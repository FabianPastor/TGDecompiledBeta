package org.telegram.messenger;

import android.content.Context;
import org.telegram.messenger.MessagesStorage.IntCallback;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ActionBar.AlertDialog;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$EkRPBwXKrGufP9yBw39xD6g-xjo implements RequestDelegate {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ Context f$1;
    private final /* synthetic */ AlertDialog f$2;
    private final /* synthetic */ IntCallback f$3;

    public /* synthetic */ -$$Lambda$MessagesController$EkRPBwXKrGufP9yBw39xD6g-xjo(MessagesController messagesController, Context context, AlertDialog alertDialog, IntCallback intCallback) {
        this.f$0 = messagesController;
        this.f$1 = context;
        this.f$2 = alertDialog;
        this.f$3 = intCallback;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$convertToMegaGroup$164$MessagesController(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
    }
}
