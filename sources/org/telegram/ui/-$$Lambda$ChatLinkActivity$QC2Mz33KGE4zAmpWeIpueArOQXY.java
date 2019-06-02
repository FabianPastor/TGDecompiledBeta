package org.telegram.ui;

import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatLinkActivity$QC2Mz33KGE4zAmpWeIpueArOQXY implements Runnable {
    private final /* synthetic */ ChatLinkActivity f$0;
    private final /* synthetic */ AlertDialog[] f$1;
    private final /* synthetic */ Chat f$2;
    private final /* synthetic */ BaseFragment f$3;

    public /* synthetic */ -$$Lambda$ChatLinkActivity$QC2Mz33KGE4zAmpWeIpueArOQXY(ChatLinkActivity chatLinkActivity, AlertDialog[] alertDialogArr, Chat chat, BaseFragment baseFragment) {
        this.f$0 = chatLinkActivity;
        this.f$1 = alertDialogArr;
        this.f$2 = chat;
        this.f$3 = baseFragment;
    }

    public final void run() {
        this.f$0.lambda$null$10$ChatLinkActivity(this.f$1, this.f$2, this.f$3);
    }
}
