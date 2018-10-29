package org.telegram.ui.ActionBar;

import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;

final /* synthetic */ class ActionBarMenuItem$$Lambda$5 implements OnKeyListener {
    private final ActionBarMenuItem arg$1;

    ActionBarMenuItem$$Lambda$5(ActionBarMenuItem actionBarMenuItem) {
        this.arg$1 = actionBarMenuItem;
    }

    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        return this.arg$1.lambda$toggleSubMenu$5$ActionBarMenuItem(view, i, keyEvent);
    }
}
