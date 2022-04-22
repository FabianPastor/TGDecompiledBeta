package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_chatInviteExported;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ChannelAdminLogActivity;

public final /* synthetic */ class ChannelAdminLogActivity$ChatActivityAdapter$3$$ExternalSyntheticLambda2 implements RequestDelegate {
    public final /* synthetic */ ChannelAdminLogActivity.ChatActivityAdapter.AnonymousClass3 f$0;
    public final /* synthetic */ TLRPC$TL_chatInviteExported f$1;
    public final /* synthetic */ boolean[] f$2;
    public final /* synthetic */ AlertDialog f$3;

    public /* synthetic */ ChannelAdminLogActivity$ChatActivityAdapter$3$$ExternalSyntheticLambda2(ChannelAdminLogActivity.ChatActivityAdapter.AnonymousClass3 r1, TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported, boolean[] zArr, AlertDialog alertDialog) {
        this.f$0 = r1;
        this.f$1 = tLRPC$TL_chatInviteExported;
        this.f$2 = zArr;
        this.f$3 = alertDialog;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$needOpenInviteLink$2(this.f$1, this.f$2, this.f$3, tLObject, tLRPC$TL_error);
    }
}
