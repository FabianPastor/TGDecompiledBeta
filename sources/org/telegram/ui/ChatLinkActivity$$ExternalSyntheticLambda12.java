package org.telegram.ui;

import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;

public final /* synthetic */ class ChatLinkActivity$$ExternalSyntheticLambda12 implements Runnable {
    public final /* synthetic */ ChatLinkActivity f$0;
    public final /* synthetic */ AlertDialog[] f$1;
    public final /* synthetic */ TLRPC$Chat f$2;
    public final /* synthetic */ BaseFragment f$3;

    public /* synthetic */ ChatLinkActivity$$ExternalSyntheticLambda12(ChatLinkActivity chatLinkActivity, AlertDialog[] alertDialogArr, TLRPC$Chat tLRPC$Chat, BaseFragment baseFragment) {
        this.f$0 = chatLinkActivity;
        this.f$1 = alertDialogArr;
        this.f$2 = tLRPC$Chat;
        this.f$3 = baseFragment;
    }

    public final void run() {
        this.f$0.lambda$linkChat$12(this.f$1, this.f$2, this.f$3);
    }
}