package org.telegram.p005ui;

/* renamed from: org.telegram.ui.ProfileActivity$$Lambda$13 */
final /* synthetic */ class ProfileActivity$$Lambda$13 implements Runnable {
    private final ProfileActivity arg$1;
    private final Object[] arg$2;

    ProfileActivity$$Lambda$13(ProfileActivity profileActivity, Object[] objArr) {
        this.arg$1 = profileActivity;
        this.arg$2 = objArr;
    }

    public void run() {
        this.arg$1.lambda$didReceivedNotification$21$ProfileActivity(this.arg$2);
    }
}
