package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

final /* synthetic */ class ThemeActivity$$Lambda$3 implements OnClickListener {
    private final ThemeActivity arg$1;

    ThemeActivity$$Lambda$3(ThemeActivity themeActivity) {
        this.arg$1 = themeActivity;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$showPermissionAlert$10$ThemeActivity(dialogInterface, i);
    }
}
