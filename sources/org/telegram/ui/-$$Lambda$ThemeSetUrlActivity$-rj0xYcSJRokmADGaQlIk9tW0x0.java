package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ThemeSetUrlActivity$-rj0xYcSJRokmADGaQlIk9tW0x0 implements OnCancelListener {
    private final /* synthetic */ ThemeSetUrlActivity f$0;
    private final /* synthetic */ int f$1;

    public /* synthetic */ -$$Lambda$ThemeSetUrlActivity$-rj0xYcSJRokmADGaQlIk9tW0x0(ThemeSetUrlActivity themeSetUrlActivity, int i) {
        this.f$0 = themeSetUrlActivity;
        this.f$1 = i;
    }

    public final void onCancel(DialogInterface dialogInterface) {
        this.f$0.lambda$saveTheme$13$ThemeSetUrlActivity(this.f$1, dialogInterface);
    }
}
