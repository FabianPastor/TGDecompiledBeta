package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ThemeActivity;

public final /* synthetic */ class ThemeActivity$ListAdapter$$ExternalSyntheticLambda2 implements DialogInterface.OnClickListener {
    public final /* synthetic */ ThemeActivity.ListAdapter f$0;
    public final /* synthetic */ Theme.ThemeInfo f$1;

    public /* synthetic */ ThemeActivity$ListAdapter$$ExternalSyntheticLambda2(ThemeActivity.ListAdapter listAdapter, Theme.ThemeInfo themeInfo) {
        this.f$0 = listAdapter;
        this.f$1 = themeInfo;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$showOptionsForTheme$1(this.f$1, dialogInterface, i);
    }
}