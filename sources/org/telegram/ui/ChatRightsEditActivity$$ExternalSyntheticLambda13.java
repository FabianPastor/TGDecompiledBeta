package org.telegram.ui;

import org.telegram.messenger.MessagesStorage;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ChatRightsEditActivity$$ExternalSyntheticLambda13 implements MessagesStorage.LongCallback {
    public final /* synthetic */ ChatRightsEditActivity f$0;
    public final /* synthetic */ TLRPC.InputCheckPasswordSRP f$1;
    public final /* synthetic */ TwoStepVerificationActivity f$2;

    public /* synthetic */ ChatRightsEditActivity$$ExternalSyntheticLambda13(ChatRightsEditActivity chatRightsEditActivity, TLRPC.InputCheckPasswordSRP inputCheckPasswordSRP, TwoStepVerificationActivity twoStepVerificationActivity) {
        this.f$0 = chatRightsEditActivity;
        this.f$1 = inputCheckPasswordSRP;
        this.f$2 = twoStepVerificationActivity;
    }

    public final void run(long j) {
        this.f$0.m3284lambda$initTransfer$7$orgtelegramuiChatRightsEditActivity(this.f$1, this.f$2, j);
    }
}
