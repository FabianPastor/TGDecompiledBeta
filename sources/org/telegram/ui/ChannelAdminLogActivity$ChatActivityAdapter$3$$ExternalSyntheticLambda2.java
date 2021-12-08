package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ChannelAdminLogActivity;

public final /* synthetic */ class ChannelAdminLogActivity$ChatActivityAdapter$3$$ExternalSyntheticLambda2 implements RequestDelegate {
    public final /* synthetic */ ChannelAdminLogActivity.ChatActivityAdapter.AnonymousClass3 f$0;
    public final /* synthetic */ TLRPC.TL_chatInviteExported f$1;
    public final /* synthetic */ boolean[] f$2;
    public final /* synthetic */ AlertDialog f$3;

    public /* synthetic */ ChannelAdminLogActivity$ChatActivityAdapter$3$$ExternalSyntheticLambda2(ChannelAdminLogActivity.ChatActivityAdapter.AnonymousClass3 r1, TLRPC.TL_chatInviteExported tL_chatInviteExported, boolean[] zArr, AlertDialog alertDialog) {
        this.f$0 = r1;
        this.f$1 = tL_chatInviteExported;
        this.f$2 = zArr;
        this.f$3 = alertDialog;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m1624x962b4a4e(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
    }
}
