package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.ui.ActionBar.Theme.ThemeInfo;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ThemeActivity$ListAdapter$mOT1foTAY8nRoymoRurolXzJymU implements OnClickListener {
    private final /* synthetic */ ListAdapter f$0;
    private final /* synthetic */ ThemeInfo f$1;

    public /* synthetic */ -$$Lambda$ThemeActivity$ListAdapter$mOT1foTAY8nRoymoRurolXzJymU(ListAdapter listAdapter, ThemeInfo themeInfo) {
        this.f$0 = listAdapter;
        this.f$1 = themeInfo;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$showOptionsForTheme$1$ThemeActivity$ListAdapter(this.f$1, dialogInterface, i);
    }
}
