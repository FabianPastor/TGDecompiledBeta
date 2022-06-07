package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class GroupCallActivity$$ExternalSyntheticLambda54 implements RequestDelegate {
    public final /* synthetic */ GroupCallActivity f$0;
    public final /* synthetic */ TLRPC$ChatFull f$1;
    public final /* synthetic */ boolean f$2;

    public /* synthetic */ GroupCallActivity$$ExternalSyntheticLambda54(GroupCallActivity groupCallActivity, TLRPC$ChatFull tLRPC$ChatFull, boolean z) {
        this.f$0 = groupCallActivity;
        this.f$1 = tLRPC$ChatFull;
        this.f$2 = z;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$getLink$39(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
    }
}
