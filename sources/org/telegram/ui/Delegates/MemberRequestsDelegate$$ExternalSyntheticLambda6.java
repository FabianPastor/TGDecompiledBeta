package org.telegram.ui.Delegates;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class MemberRequestsDelegate$$ExternalSyntheticLambda6 implements Runnable {
    public final /* synthetic */ MemberRequestsDelegate f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ Runnable f$2;
    public final /* synthetic */ String f$3;
    public final /* synthetic */ TLRPC$TL_error f$4;
    public final /* synthetic */ TLObject f$5;
    public final /* synthetic */ boolean f$6;

    public /* synthetic */ MemberRequestsDelegate$$ExternalSyntheticLambda6(MemberRequestsDelegate memberRequestsDelegate, boolean z, Runnable runnable, String str, TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, boolean z2) {
        this.f$0 = memberRequestsDelegate;
        this.f$1 = z;
        this.f$2 = runnable;
        this.f$3 = str;
        this.f$4 = tLRPC$TL_error;
        this.f$5 = tLObject;
        this.f$6 = z2;
    }

    public final void run() {
        this.f$0.lambda$loadMembers$3(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
    }
}
