package org.telegram.ui;

final /* synthetic */ class ProfileActivity$$Lambda$14 implements Runnable {
    private final ProfileActivity arg$1;
    private final Object[] arg$2;

    ProfileActivity$$Lambda$14(ProfileActivity profileActivity, Object[] objArr) {
        this.arg$1 = profileActivity;
        this.arg$2 = objArr;
    }

    public void run() {
        this.arg$1.lambda$didReceivedNotification$23$ProfileActivity(this.arg$2);
    }
}
