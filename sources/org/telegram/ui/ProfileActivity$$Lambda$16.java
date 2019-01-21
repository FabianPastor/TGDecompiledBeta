package org.telegram.ui;

import java.util.Comparator;

final /* synthetic */ class ProfileActivity$$Lambda$16 implements Comparator {
    private final ProfileActivity arg$1;
    private final int arg$2;

    ProfileActivity$$Lambda$16(ProfileActivity profileActivity, int i) {
        this.arg$1 = profileActivity;
        this.arg$2 = i;
    }

    public int compare(Object obj, Object obj2) {
        return this.arg$1.lambda$updateOnlineCount$22$ProfileActivity(this.arg$2, (Integer) obj, (Integer) obj2);
    }
}
