package org.telegram.messenger;

import android.content.Context;
import org.telegram.messenger.MessagesStorage;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_migrateChat;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda275 implements RequestDelegate {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ Context f$1;
    public final /* synthetic */ AlertDialog f$2;
    public final /* synthetic */ MessagesStorage.LongCallback f$3;
    public final /* synthetic */ BaseFragment f$4;
    public final /* synthetic */ TLRPC$TL_messages_migrateChat f$5;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda275(MessagesController messagesController, Context context, AlertDialog alertDialog, MessagesStorage.LongCallback longCallback, BaseFragment baseFragment, TLRPC$TL_messages_migrateChat tLRPC$TL_messages_migrateChat) {
        this.f$0 = messagesController;
        this.f$1 = context;
        this.f$2 = alertDialog;
        this.f$3 = longCallback;
        this.f$4 = baseFragment;
        this.f$5 = tLRPC$TL_messages_migrateChat;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$convertToMegaGroup$205(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, tLObject, tLRPC$TL_error);
    }
}
