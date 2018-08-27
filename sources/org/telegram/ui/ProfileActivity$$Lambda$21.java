package org.telegram.ui;

import org.telegram.tgnet.TLObject;

final /* synthetic */ class ProfileActivity$$Lambda$21 implements Runnable {
    private final ProfileActivity arg$1;
    private final TLObject arg$2;

    ProfileActivity$$Lambda$21(ProfileActivity profileActivity, TLObject tLObject) {
        this.arg$1 = profileActivity;
        this.arg$2 = tLObject;
    }

    public void run() {
        this.arg$1.lambda$null$9$ProfileActivity(this.arg$2);
    }
}
