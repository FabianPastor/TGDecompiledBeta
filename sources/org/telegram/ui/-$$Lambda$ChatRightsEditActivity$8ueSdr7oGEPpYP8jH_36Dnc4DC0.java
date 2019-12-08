package org.telegram.ui;

import org.telegram.tgnet.TLRPC.InputCheckPasswordSRP;
import org.telegram.tgnet.TLRPC.TL_channels_editCreator;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatRightsEditActivity$8ueSdr7oGEPpYP8jH_36Dnc4DC0 implements Runnable {
    private final /* synthetic */ ChatRightsEditActivity f$0;
    private final /* synthetic */ TL_error f$1;
    private final /* synthetic */ InputCheckPasswordSRP f$2;
    private final /* synthetic */ TwoStepVerificationActivity f$3;
    private final /* synthetic */ TL_channels_editCreator f$4;

    public /* synthetic */ -$$Lambda$ChatRightsEditActivity$8ueSdr7oGEPpYP8jH_36Dnc4DC0(ChatRightsEditActivity chatRightsEditActivity, TL_error tL_error, InputCheckPasswordSRP inputCheckPasswordSRP, TwoStepVerificationActivity twoStepVerificationActivity, TL_channels_editCreator tL_channels_editCreator) {
        this.f$0 = chatRightsEditActivity;
        this.f$1 = tL_error;
        this.f$2 = inputCheckPasswordSRP;
        this.f$3 = twoStepVerificationActivity;
        this.f$4 = tL_channels_editCreator;
    }

    public final void run() {
        this.f$0.lambda$null$13$ChatRightsEditActivity(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
