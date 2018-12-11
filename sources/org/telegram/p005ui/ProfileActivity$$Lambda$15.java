package org.telegram.p005ui;

import java.util.Comparator;

/* renamed from: org.telegram.ui.ProfileActivity$$Lambda$15 */
final /* synthetic */ class ProfileActivity$$Lambda$15 implements Comparator {
    private final ProfileActivity arg$1;

    ProfileActivity$$Lambda$15(ProfileActivity profileActivity) {
        this.arg$1 = profileActivity;
    }

    public int compare(Object obj, Object obj2) {
        return this.arg$1.lambda$updateOnlineCount$22$ProfileActivity((Integer) obj, (Integer) obj2);
    }
}
