package org.telegram.ui;

import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ChannelAdminLogActivity;

public final /* synthetic */ class ChannelAdminLogActivity$ChatActivityAdapter$3$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ ChannelAdminLogActivity.ChatActivityAdapter.AnonymousClass3 f$0;
    public final /* synthetic */ TLRPC.TL_chatInviteExported f$1;
    public final /* synthetic */ TLRPC.TL_messages_exportedChatInvite f$2;
    public final /* synthetic */ boolean[] f$3;
    public final /* synthetic */ AlertDialog f$4;

    public /* synthetic */ ChannelAdminLogActivity$ChatActivityAdapter$3$$ExternalSyntheticLambda1(ChannelAdminLogActivity.ChatActivityAdapter.AnonymousClass3 r1, TLRPC.TL_chatInviteExported tL_chatInviteExported, TLRPC.TL_messages_exportedChatInvite tL_messages_exportedChatInvite, boolean[] zArr, AlertDialog alertDialog) {
        this.f$0 = r1;
        this.f$1 = tL_chatInviteExported;
        this.f$2 = tL_messages_exportedChatInvite;
        this.f$3 = zArr;
        this.f$4 = alertDialog;
    }

    public final void run() {
        this.f$0.m1623xa29bCLASSNAMEd(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
