package org.telegram.p005ui;

import org.telegram.p005ui.PassportActivity.ErrorRunnable;

/* renamed from: org.telegram.ui.PassportActivity$$Lambda$53 */
final /* synthetic */ class PassportActivity$$Lambda$53 implements ErrorRunnable {
    private final PassportActivity arg$1;

    PassportActivity$$Lambda$53(PassportActivity passportActivity) {
        this.arg$1 = passportActivity;
    }

    public void onError(String str, String str2) {
        this.arg$1.lambda$null$62$PassportActivity(str, str2);
    }
}
