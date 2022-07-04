package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_chatInviteExported;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class ManageLinksActivity$$ExternalSyntheticLambda14 implements RequestDelegate {
    public final /* synthetic */ ManageLinksActivity f$0;
    public final /* synthetic */ TLRPC$TL_chatInviteExported f$1;
    public final /* synthetic */ boolean f$2;

    public /* synthetic */ ManageLinksActivity$$ExternalSyntheticLambda14(ManageLinksActivity manageLinksActivity, TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported, boolean z) {
        this.f$0 = manageLinksActivity;
        this.f$1 = tLRPC$TL_chatInviteExported;
        this.f$2 = z;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$loadLinks$5(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
    }
}
