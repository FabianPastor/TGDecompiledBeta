package org.telegram.ui.Delegates;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MemberRequestsDelegate$$ExternalSyntheticLambda8 implements RequestDelegate {
    public final /* synthetic */ MemberRequestsDelegate f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ Runnable f$2;
    public final /* synthetic */ String f$3;
    public final /* synthetic */ boolean f$4;

    public /* synthetic */ MemberRequestsDelegate$$ExternalSyntheticLambda8(MemberRequestsDelegate memberRequestsDelegate, boolean z, Runnable runnable, String str, boolean z2) {
        this.f$0 = memberRequestsDelegate;
        this.f$1 = z;
        this.f$2 = runnable;
        this.f$3 = str;
        this.f$4 = z2;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$loadMembers$4(this.f$1, this.f$2, this.f$3, this.f$4, tLObject, tLRPC$TL_error);
    }
}
