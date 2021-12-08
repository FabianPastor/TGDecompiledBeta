package org.telegram.ui;

import org.telegram.tgnet.TLRPC$TL_authorization;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.SessionsActivity;

public final /* synthetic */ class SessionsActivity$2$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ SessionsActivity.AnonymousClass2 f$0;
    public final /* synthetic */ TLRPC$TL_error f$1;
    public final /* synthetic */ TLRPC$TL_authorization f$2;

    public /* synthetic */ SessionsActivity$2$$ExternalSyntheticLambda0(SessionsActivity.AnonymousClass2 r1, TLRPC$TL_error tLRPC$TL_error, TLRPC$TL_authorization tLRPC$TL_authorization) {
        this.f$0 = r1;
        this.f$1 = tLRPC$TL_error;
        this.f$2 = tLRPC$TL_authorization;
    }

    public final void run() {
        this.f$0.lambda$hide$0(this.f$1, this.f$2);
    }
}
