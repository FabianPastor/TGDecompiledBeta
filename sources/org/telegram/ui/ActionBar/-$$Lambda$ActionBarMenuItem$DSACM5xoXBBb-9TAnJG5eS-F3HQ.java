package org.telegram.ui.ActionBar;

import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ActionBarMenuItem$DSACM5xoXBBb-9TAnJG5eS-F3HQ implements OnEditorActionListener {
    private final /* synthetic */ ActionBarMenuItem f$0;

    public /* synthetic */ -$$Lambda$ActionBarMenuItem$DSACM5xoXBBb-9TAnJG5eS-F3HQ(ActionBarMenuItem actionBarMenuItem) {
        this.f$0 = actionBarMenuItem;
    }

    public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        return this.f$0.lambda$setIsSearchField$7$ActionBarMenuItem(textView, i, keyEvent);
    }
}
