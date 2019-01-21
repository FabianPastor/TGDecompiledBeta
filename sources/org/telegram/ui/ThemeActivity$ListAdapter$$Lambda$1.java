package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.ui.ActionBar.Theme.ThemeInfo;

final /* synthetic */ class ThemeActivity$ListAdapter$$Lambda$1 implements OnClickListener {
    private final ListAdapter arg$1;
    private final ThemeInfo arg$2;

    ThemeActivity$ListAdapter$$Lambda$1(ListAdapter listAdapter, ThemeInfo themeInfo) {
        this.arg$1 = listAdapter;
        this.arg$2 = themeInfo;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$null$1$ThemeActivity$ListAdapter(this.arg$2, dialogInterface, i);
    }
}
