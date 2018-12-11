package org.telegram.p005ui;

import android.view.View;
import android.view.View.OnClickListener;
import org.telegram.messenger.LocaleController.LocaleInfo;
import org.telegram.p005ui.Cells.LanguageCell;

/* renamed from: org.telegram.ui.LaunchActivity$$Lambda$28 */
final /* synthetic */ class LaunchActivity$$Lambda$28 implements OnClickListener {
    private final LocaleInfo[] arg$1;
    private final LanguageCell[] arg$2;

    LaunchActivity$$Lambda$28(LocaleInfo[] localeInfoArr, LanguageCell[] languageCellArr) {
        this.arg$1 = localeInfoArr;
        this.arg$2 = languageCellArr;
    }

    public void onClick(View view) {
        LaunchActivity.lambda$showLanguageAlertInternal$43$LaunchActivity(this.arg$1, this.arg$2, view);
    }
}
