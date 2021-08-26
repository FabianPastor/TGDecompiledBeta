package org.telegram.ui;

import org.telegram.messenger.MessagesStorage;
import org.telegram.tgnet.TLRPC$InputCheckPasswordSRP;

public final /* synthetic */ class ChatRightsEditActivity$$ExternalSyntheticLambda13 implements MessagesStorage.IntCallback {
    public final /* synthetic */ ChatRightsEditActivity f$0;
    public final /* synthetic */ TLRPC$InputCheckPasswordSRP f$1;
    public final /* synthetic */ TwoStepVerificationActivity f$2;

    public /* synthetic */ ChatRightsEditActivity$$ExternalSyntheticLambda13(ChatRightsEditActivity chatRightsEditActivity, TLRPC$InputCheckPasswordSRP tLRPC$InputCheckPasswordSRP, TwoStepVerificationActivity twoStepVerificationActivity) {
        this.f$0 = chatRightsEditActivity;
        this.f$1 = tLRPC$InputCheckPasswordSRP;
        this.f$2 = twoStepVerificationActivity;
    }

    public final void run(int i) {
        this.f$0.lambda$initTransfer$7(this.f$1, this.f$2, i);
    }
}
