package org.telegram.ui;

final /* synthetic */ class PassportActivity$$Lambda$53 implements Runnable {
    private final PassportActivity arg$1;

    PassportActivity$$Lambda$53(PassportActivity passportActivity) {
        this.arg$1 = passportActivity;
    }

    public void run() {
        this.arg$1.needHideProgress();
    }
}
