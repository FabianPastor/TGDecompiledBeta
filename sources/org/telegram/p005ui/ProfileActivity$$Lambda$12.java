package org.telegram.p005ui;

/* renamed from: org.telegram.ui.ProfileActivity$$Lambda$12 */
final /* synthetic */ class ProfileActivity$$Lambda$12 implements Runnable {
    private final ProfileActivity arg$1;
    private final Object[] arg$2;

    ProfileActivity$$Lambda$12(ProfileActivity profileActivity, Object[] objArr) {
        this.arg$1 = profileActivity;
        this.arg$2 = objArr;
    }

    public void run() {
        this.arg$1.lambda$didReceivedNotification$20$ProfileActivity(this.arg$2);
    }
}
