package org.telegram.ui;

import org.telegram.tgnet.TLRPC;
import org.telegram.ui.TwoStepVerificationActivity;

public final /* synthetic */ class ChatRightsEditActivity$$ExternalSyntheticLambda18 implements TwoStepVerificationActivity.TwoStepVerificationActivityDelegate {
    public final /* synthetic */ ChatRightsEditActivity f$0;
    public final /* synthetic */ TwoStepVerificationActivity f$1;

    public /* synthetic */ ChatRightsEditActivity$$ExternalSyntheticLambda18(ChatRightsEditActivity chatRightsEditActivity, TwoStepVerificationActivity twoStepVerificationActivity) {
        this.f$0 = chatRightsEditActivity;
        this.f$1 = twoStepVerificationActivity;
    }

    public final void didEnterPassword(TLRPC.InputCheckPasswordSRP inputCheckPasswordSRP) {
        this.f$0.m3285lambda$initTransfer$8$orgtelegramuiChatRightsEditActivity(this.f$1, inputCheckPasswordSRP);
    }
}
