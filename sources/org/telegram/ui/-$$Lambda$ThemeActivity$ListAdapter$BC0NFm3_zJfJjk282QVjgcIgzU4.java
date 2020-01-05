package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.ui.ActionBar.Theme.ThemeAccent;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ThemeActivity$ListAdapter$BC0NFm3_zJfJjk282QVjgcIgzU4 implements OnClickListener {
    private final /* synthetic */ ListAdapter f$0;
    private final /* synthetic */ ThemeAccentsListAdapter f$1;
    private final /* synthetic */ ThemeAccent f$2;

    public /* synthetic */ -$$Lambda$ThemeActivity$ListAdapter$BC0NFm3_zJfJjk282QVjgcIgzU4(ListAdapter listAdapter, ThemeAccentsListAdapter themeAccentsListAdapter, ThemeAccent themeAccent) {
        this.f$0 = listAdapter;
        this.f$1 = themeAccentsListAdapter;
        this.f$2 = themeAccent;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$null$3$ThemeActivity$ListAdapter(this.f$1, this.f$2, dialogInterface, i);
    }
}
