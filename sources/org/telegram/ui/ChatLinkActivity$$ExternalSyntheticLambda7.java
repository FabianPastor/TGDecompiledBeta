package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;

public final /* synthetic */ class ChatLinkActivity$$ExternalSyntheticLambda7 implements RequestDelegate {
    public final /* synthetic */ ChatLinkActivity f$0;
    public final /* synthetic */ AlertDialog[] f$1;
    public final /* synthetic */ TLRPC.Chat f$2;
    public final /* synthetic */ BaseFragment f$3;

    public /* synthetic */ ChatLinkActivity$$ExternalSyntheticLambda7(ChatLinkActivity chatLinkActivity, AlertDialog[] alertDialogArr, TLRPC.Chat chat, BaseFragment baseFragment) {
        this.f$0 = chatLinkActivity;
        this.f$1 = alertDialogArr;
        this.f$2 = chat;
        this.f$3 = baseFragment;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m1923lambda$linkChat$13$orgtelegramuiChatLinkActivity(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
    }
}
