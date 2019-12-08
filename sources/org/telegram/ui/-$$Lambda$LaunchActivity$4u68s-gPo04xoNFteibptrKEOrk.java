package org.telegram.ui;

import android.view.View;
import android.view.View.OnClickListener;
import org.telegram.messenger.LocaleController.LocaleInfo;
import org.telegram.ui.Cells.LanguageCell;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LaunchActivity$4u68s-gPo04xoNFteibptrKEOrk implements OnClickListener {
    private final /* synthetic */ LocaleInfo[] f$0;
    private final /* synthetic */ LanguageCell[] f$1;

    public /* synthetic */ -$$Lambda$LaunchActivity$4u68s-gPo04xoNFteibptrKEOrk(LocaleInfo[] localeInfoArr, LanguageCell[] languageCellArr) {
        this.f$0 = localeInfoArr;
        this.f$1 = languageCellArr;
    }

    public final void onClick(View view) {
        LaunchActivity.lambda$showLanguageAlertInternal$57(this.f$0, this.f$1, view);
    }
}
