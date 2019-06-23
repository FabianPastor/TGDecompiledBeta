package org.telegram.ui;

import org.telegram.messenger.MessagesStorage.IntCallback;
import org.telegram.tgnet.TLRPC.InputCheckPasswordSRP;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatRightsEditActivity$f-QWBMrtF1b2jpUj_yHfvf_4djU implements IntCallback {
    private final /* synthetic */ ChatRightsEditActivity f$0;
    private final /* synthetic */ InputCheckPasswordSRP f$1;
    private final /* synthetic */ TwoStepVerificationActivity f$2;

    public /* synthetic */ -$$Lambda$ChatRightsEditActivity$f-QWBMrtF1b2jpUj_yHfvf_4djU(ChatRightsEditActivity chatRightsEditActivity, InputCheckPasswordSRP inputCheckPasswordSRP, TwoStepVerificationActivity twoStepVerificationActivity) {
        this.f$0 = chatRightsEditActivity;
        this.f$1 = inputCheckPasswordSRP;
        this.f$2 = twoStepVerificationActivity;
    }

    public final void run(int i) {
        this.f$0.lambda$initTransfer$7$ChatRightsEditActivity(this.f$1, this.f$2, i);
    }
}
