package org.telegram.ui;

import org.telegram.tgnet.TLRPC.InputCheckPasswordSRP;
import org.telegram.ui.TwoStepVerificationActivity.TwoStepVerificationActivityDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatRightsEditActivity$RcQ2sgyZbrpH4BvhvZ7bAqTBId0 implements TwoStepVerificationActivityDelegate {
    private final /* synthetic */ ChatRightsEditActivity f$0;
    private final /* synthetic */ TwoStepVerificationActivity f$1;

    public /* synthetic */ -$$Lambda$ChatRightsEditActivity$RcQ2sgyZbrpH4BvhvZ7bAqTBId0(ChatRightsEditActivity chatRightsEditActivity, TwoStepVerificationActivity twoStepVerificationActivity) {
        this.f$0 = chatRightsEditActivity;
        this.f$1 = twoStepVerificationActivity;
    }

    public final void didEnterPassword(InputCheckPasswordSRP inputCheckPasswordSRP) {
        this.f$0.lambda$null$8$ChatRightsEditActivity(this.f$1, inputCheckPasswordSRP);
    }
}
