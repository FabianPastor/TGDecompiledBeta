package org.telegram.ui;

final /* synthetic */ class PassportActivity$$Lambda$7 implements Runnable {
    private final PassportActivity arg$1;
    private final boolean arg$2;
    private final String arg$3;

    PassportActivity$$Lambda$7(PassportActivity passportActivity, boolean z, String str) {
        this.arg$1 = passportActivity;
        this.arg$2 = z;
        this.arg$3 = str;
    }

    public void run() {
        this.arg$1.lambda$onPasswordDone$13$PassportActivity(this.arg$2, this.arg$3);
    }
}
