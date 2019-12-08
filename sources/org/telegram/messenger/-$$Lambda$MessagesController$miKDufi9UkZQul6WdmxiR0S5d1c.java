package org.telegram.messenger;

import android.content.Context;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_migrateChat;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$miKDufi9UkZQul6WdmxiR0S5d1c implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ Context f$1;
    private final /* synthetic */ AlertDialog f$2;
    private final /* synthetic */ TL_error f$3;
    private final /* synthetic */ BaseFragment f$4;
    private final /* synthetic */ TL_messages_migrateChat f$5;

    public /* synthetic */ -$$Lambda$MessagesController$miKDufi9UkZQul6WdmxiR0S5d1c(MessagesController messagesController, Context context, AlertDialog alertDialog, TL_error tL_error, BaseFragment baseFragment, TL_messages_migrateChat tL_messages_migrateChat) {
        this.f$0 = messagesController;
        this.f$1 = context;
        this.f$2 = alertDialog;
        this.f$3 = tL_error;
        this.f$4 = baseFragment;
        this.f$5 = tL_messages_migrateChat;
    }

    public final void run() {
        this.f$0.lambda$null$165$MessagesController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}
