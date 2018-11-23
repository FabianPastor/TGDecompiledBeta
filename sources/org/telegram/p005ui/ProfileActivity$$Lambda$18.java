package org.telegram.p005ui;

import org.telegram.tgnet.TLObject;

/* renamed from: org.telegram.ui.ProfileActivity$$Lambda$18 */
final /* synthetic */ class ProfileActivity$$Lambda$18 implements Runnable {
    private final ProfileActivity arg$1;
    private final TLObject arg$2;

    ProfileActivity$$Lambda$18(ProfileActivity profileActivity, TLObject tLObject) {
        this.arg$1 = profileActivity;
        this.arg$2 = tLObject;
    }

    public void run() {
        this.arg$1.lambda$null$8$ProfileActivity(this.arg$2);
    }
}
