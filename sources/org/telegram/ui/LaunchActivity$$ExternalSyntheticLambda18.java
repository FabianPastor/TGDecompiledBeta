package org.telegram.ui;

import android.view.View;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.Cells.LanguageCell;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda18 implements View.OnClickListener {
    public final /* synthetic */ LocaleController.LocaleInfo[] f$0;
    public final /* synthetic */ LanguageCell[] f$1;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda18(LocaleController.LocaleInfo[] localeInfoArr, LanguageCell[] languageCellArr) {
        this.f$0 = localeInfoArr;
        this.f$1 = languageCellArr;
    }

    public final void onClick(View view) {
        LaunchActivity.lambda$showLanguageAlertInternal$99(this.f$0, this.f$1, view);
    }
}
