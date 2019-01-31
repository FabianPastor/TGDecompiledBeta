package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.messenger.LocaleController.LocaleInfo;

final /* synthetic */ class LaunchActivity$$Lambda$32 implements OnClickListener {
    private final LaunchActivity arg$1;
    private final LocaleInfo[] arg$2;

    LaunchActivity$$Lambda$32(LaunchActivity launchActivity, LocaleInfo[] localeInfoArr) {
        this.arg$1 = launchActivity;
        this.arg$2 = localeInfoArr;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$showLanguageAlertInternal$48$LaunchActivity(this.arg$2, dialogInterface, i);
    }
}
