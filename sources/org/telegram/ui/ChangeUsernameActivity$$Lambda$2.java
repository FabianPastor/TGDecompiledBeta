package org.telegram.ui;

final /* synthetic */ class ChangeUsernameActivity$$Lambda$2 implements Runnable {
    private final ChangeUsernameActivity arg$1;
    private final String arg$2;

    ChangeUsernameActivity$$Lambda$2(ChangeUsernameActivity changeUsernameActivity, String str) {
        this.arg$1 = changeUsernameActivity;
        this.arg$2 = str;
    }

    public void run() {
        this.arg$1.lambda$checkUserName$4$ChangeUsernameActivity(this.arg$2);
    }
}
