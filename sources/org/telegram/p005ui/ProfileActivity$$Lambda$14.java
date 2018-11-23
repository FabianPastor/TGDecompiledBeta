package org.telegram.p005ui;

import java.util.Comparator;

/* renamed from: org.telegram.ui.ProfileActivity$$Lambda$14 */
final /* synthetic */ class ProfileActivity$$Lambda$14 implements Comparator {
    private final ProfileActivity arg$1;

    ProfileActivity$$Lambda$14(ProfileActivity profileActivity) {
        this.arg$1 = profileActivity;
    }

    public int compare(Object obj, Object obj2) {
        return this.arg$1.lambda$updateOnlineCount$21$ProfileActivity((Integer) obj, (Integer) obj2);
    }
}
