package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.messenger.LocaleController;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda11 implements DialogInterface.OnClickListener {
    public final /* synthetic */ LaunchActivity f$0;
    public final /* synthetic */ LocaleController.LocaleInfo[] f$1;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda11(LaunchActivity launchActivity, LocaleController.LocaleInfo[] localeInfoArr) {
        this.f$0 = launchActivity;
        this.f$1 = localeInfoArr;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$showLanguageAlertInternal$80(this.f$1, dialogInterface, i);
    }
}