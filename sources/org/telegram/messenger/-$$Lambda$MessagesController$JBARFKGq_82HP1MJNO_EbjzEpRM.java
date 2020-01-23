package org.telegram.messenger;

import android.content.Context;
import org.telegram.messenger.MessagesStorage.IntCallback;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_migrateChat;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$JBARFKGq_82HP1MJNO_EbjzEpRM implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ IntCallback f$1;
    private final /* synthetic */ Context f$2;
    private final /* synthetic */ AlertDialog f$3;
    private final /* synthetic */ TL_error f$4;
    private final /* synthetic */ BaseFragment f$5;
    private final /* synthetic */ TL_messages_migrateChat f$6;

    public /* synthetic */ -$$Lambda$MessagesController$JBARFKGq_82HP1MJNO_EbjzEpRM(MessagesController messagesController, IntCallback intCallback, Context context, AlertDialog alertDialog, TL_error tL_error, BaseFragment baseFragment, TL_messages_migrateChat tL_messages_migrateChat) {
        this.f$0 = messagesController;
        this.f$1 = intCallback;
        this.f$2 = context;
        this.f$3 = alertDialog;
        this.f$4 = tL_error;
        this.f$5 = baseFragment;
        this.f$6 = tL_messages_migrateChat;
    }

    public final void run() {
        this.f$0.lambda$null$180$MessagesController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
    }
}
