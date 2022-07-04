package org.telegram.messenger;

import android.content.Context;
import org.telegram.messenger.MessagesStorage;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_channels_convertToGigagroup;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda309 implements RequestDelegate {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ Context f$1;
    public final /* synthetic */ AlertDialog f$2;
    public final /* synthetic */ MessagesStorage.BooleanCallback f$3;
    public final /* synthetic */ BaseFragment f$4;
    public final /* synthetic */ TLRPC$TL_channels_convertToGigagroup f$5;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda309(MessagesController messagesController, Context context, AlertDialog alertDialog, MessagesStorage.BooleanCallback booleanCallback, BaseFragment baseFragment, TLRPC$TL_channels_convertToGigagroup tLRPC$TL_channels_convertToGigagroup) {
        this.f$0 = messagesController;
        this.f$1 = context;
        this.f$2 = alertDialog;
        this.f$3 = booleanCallback;
        this.f$4 = baseFragment;
        this.f$5 = tLRPC$TL_channels_convertToGigagroup;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$convertToGigaGroup$221(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, tLObject, tLRPC$TL_error);
    }
}
