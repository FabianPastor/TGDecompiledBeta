package org.telegram.messenger;

import android.content.Context;
import org.telegram.messenger.MessagesStorage.IntCallback;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_migrateChat;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$LdS6GlYHGoMq8IswkgK91zRCSrc implements RequestDelegate {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ Context f$1;
    private final /* synthetic */ AlertDialog f$2;
    private final /* synthetic */ IntCallback f$3;
    private final /* synthetic */ BaseFragment f$4;
    private final /* synthetic */ TL_messages_migrateChat f$5;

    public /* synthetic */ -$$Lambda$MessagesController$LdS6GlYHGoMq8IswkgK91zRCSrc(MessagesController messagesController, Context context, AlertDialog alertDialog, IntCallback intCallback, BaseFragment baseFragment, TL_messages_migrateChat tL_messages_migrateChat) {
        this.f$0 = messagesController;
        this.f$1 = context;
        this.f$2 = alertDialog;
        this.f$3 = intCallback;
        this.f$4 = baseFragment;
        this.f$5 = tL_messages_migrateChat;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$convertToMegaGroup$181$MessagesController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, tLObject, tL_error);
    }
}
