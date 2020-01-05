package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.ui.ActionBar.Theme.ThemeAccent;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ThemeActivity$ListAdapter$bBxbJopnBfliVbn7S3U5sGJ24Z4 implements OnClickListener {
    private final /* synthetic */ ListAdapter f$0;
    private final /* synthetic */ ThemeAccent f$1;
    private final /* synthetic */ ThemeAccentsListAdapter f$2;

    public /* synthetic */ -$$Lambda$ThemeActivity$ListAdapter$bBxbJopnBfliVbn7S3U5sGJ24Z4(ListAdapter listAdapter, ThemeAccent themeAccent, ThemeAccentsListAdapter themeAccentsListAdapter) {
        this.f$0 = listAdapter;
        this.f$1 = themeAccent;
        this.f$2 = themeAccentsListAdapter;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$null$4$ThemeActivity$ListAdapter(this.f$1, this.f$2, dialogInterface, i);
    }
}
