package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.messenger.LocaleController.LocaleInfo;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LaunchActivity$-_OYvDrFInFakn0WORCpq62kH08 implements OnClickListener {
    private final /* synthetic */ LaunchActivity f$0;
    private final /* synthetic */ LocaleInfo[] f$1;

    public /* synthetic */ -$$Lambda$LaunchActivity$-_OYvDrFInFakn0WORCpq62kH08(LaunchActivity launchActivity, LocaleInfo[] localeInfoArr) {
        this.f$0 = launchActivity;
        this.f$1 = localeInfoArr;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$showLanguageAlertInternal$51$LaunchActivity(this.f$1, dialogInterface, i);
    }
}
