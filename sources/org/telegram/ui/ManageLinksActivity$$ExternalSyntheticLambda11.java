package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_chatInviteExported;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class ManageLinksActivity$$ExternalSyntheticLambda11 implements RequestDelegate {
    public final /* synthetic */ ManageLinksActivity f$0;
    public final /* synthetic */ TLRPC$TL_chatInviteExported f$1;

    public /* synthetic */ ManageLinksActivity$$ExternalSyntheticLambda11(ManageLinksActivity manageLinksActivity, TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported) {
        this.f$0 = manageLinksActivity;
        this.f$1 = tLRPC$TL_chatInviteExported;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$revokePermanent$12(this.f$1, tLObject, tLRPC$TL_error);
    }
}
