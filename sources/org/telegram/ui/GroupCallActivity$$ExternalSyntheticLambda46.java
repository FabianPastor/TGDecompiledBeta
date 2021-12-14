package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class GroupCallActivity$$ExternalSyntheticLambda46 implements RequestDelegate {
    public final /* synthetic */ GroupCallActivity f$0;

    public /* synthetic */ GroupCallActivity$$ExternalSyntheticLambda46(GroupCallActivity groupCallActivity) {
        this.f$0 = groupCallActivity;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$toggleAdminSpeak$60(tLObject, tLRPC$TL_error);
    }
}
