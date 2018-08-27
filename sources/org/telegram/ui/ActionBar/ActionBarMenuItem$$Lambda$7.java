package org.telegram.ui.ActionBar;

import android.view.View;
import android.view.View.OnClickListener;

final /* synthetic */ class ActionBarMenuItem$$Lambda$7 implements OnClickListener {
    private final ActionBarMenuItem arg$1;

    ActionBarMenuItem$$Lambda$7(ActionBarMenuItem actionBarMenuItem) {
        this.arg$1 = actionBarMenuItem;
    }

    public void onClick(View view) {
        this.arg$1.lambda$setIsSearchField$7$ActionBarMenuItem(view);
    }
}
