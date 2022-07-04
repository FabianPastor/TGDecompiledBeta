package org.telegram.ui;

import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class ChatEditTypeActivity$$ExternalSyntheticLambda11 implements Runnable {
    public final /* synthetic */ ChatEditTypeActivity f$0;
    public final /* synthetic */ TLRPC$TL_error f$1;

    public /* synthetic */ ChatEditTypeActivity$$ExternalSyntheticLambda11(ChatEditTypeActivity chatEditTypeActivity, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0 = chatEditTypeActivity;
        this.f$1 = tLRPC$TL_error;
    }

    public final void run() {
        this.f$0.lambda$onFragmentCreate$0(this.f$1);
    }
}
