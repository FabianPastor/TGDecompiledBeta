package org.telegram.p005ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.ProfileActivity$$Lambda$3 */
final /* synthetic */ class ProfileActivity$$Lambda$3 implements RequestDelegate {
    private final ProfileActivity arg$1;

    ProfileActivity$$Lambda$3(ProfileActivity profileActivity) {
        this.arg$1 = profileActivity;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$createView$9$ProfileActivity(tLObject, tL_error);
    }
}
