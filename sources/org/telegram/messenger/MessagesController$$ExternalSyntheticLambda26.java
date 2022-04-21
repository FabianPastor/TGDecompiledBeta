package org.telegram.messenger;

import android.content.Context;
import org.telegram.messenger.MessagesStorage;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda26 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ MessagesStorage.BooleanCallback f$1;
    public final /* synthetic */ Context f$2;
    public final /* synthetic */ AlertDialog f$3;
    public final /* synthetic */ TLRPC.TL_error f$4;
    public final /* synthetic */ BaseFragment f$5;
    public final /* synthetic */ TLRPC.TL_channels_convertToGigagroup f$6;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda26(MessagesController messagesController, MessagesStorage.BooleanCallback booleanCallback, Context context, AlertDialog alertDialog, TLRPC.TL_error tL_error, BaseFragment baseFragment, TLRPC.TL_channels_convertToGigagroup tL_channels_convertToGigagroup) {
        this.f$0 = messagesController;
        this.f$1 = booleanCallback;
        this.f$2 = context;
        this.f$3 = alertDialog;
        this.f$4 = tL_error;
        this.f$5 = baseFragment;
        this.f$6 = tL_channels_convertToGigagroup;
    }

    public final void run() {
        this.f$0.m159x720a9e07(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
    }
}
