package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ThemeActivity;

public final /* synthetic */ class ThemeActivity$ListAdapter$$ExternalSyntheticLambda3 implements DialogInterface.OnClickListener {
    public final /* synthetic */ ThemeActivity.ListAdapter f$0;
    public final /* synthetic */ ThemeActivity.ThemeAccentsListAdapter f$1;
    public final /* synthetic */ Theme.ThemeAccent f$2;

    public /* synthetic */ ThemeActivity$ListAdapter$$ExternalSyntheticLambda3(ThemeActivity.ListAdapter listAdapter, ThemeActivity.ThemeAccentsListAdapter themeAccentsListAdapter, Theme.ThemeAccent themeAccent) {
        this.f$0 = listAdapter;
        this.f$1 = themeAccentsListAdapter;
        this.f$2 = themeAccent;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$onCreateViewHolder$3(this.f$1, this.f$2, dialogInterface, i);
    }
}
