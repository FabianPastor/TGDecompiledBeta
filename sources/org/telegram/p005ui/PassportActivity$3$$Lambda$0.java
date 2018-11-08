package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.p005ui.PassportActivity.C19143;
import org.telegram.p005ui.PassportActivity.ErrorRunnable;

/* renamed from: org.telegram.ui.PassportActivity$3$$Lambda$0 */
final /* synthetic */ class PassportActivity$3$$Lambda$0 implements OnClickListener {
    private final C19143 arg$1;
    private final String arg$2;
    private final String arg$3;
    private final String arg$4;
    private final Runnable arg$5;
    private final ErrorRunnable arg$6;

    PassportActivity$3$$Lambda$0(C19143 c19143, String str, String str2, String str3, Runnable runnable, ErrorRunnable errorRunnable) {
        this.arg$1 = c19143;
        this.arg$2 = str;
        this.arg$3 = str2;
        this.arg$4 = str3;
        this.arg$5 = runnable;
        this.arg$6 = errorRunnable;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$onIdentityDone$0$PassportActivity$3(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, dialogInterface, i);
    }
}
