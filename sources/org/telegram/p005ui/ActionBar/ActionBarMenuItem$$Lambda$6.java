package org.telegram.p005ui.ActionBar;

import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

/* renamed from: org.telegram.ui.ActionBar.ActionBarMenuItem$$Lambda$6 */
final /* synthetic */ class ActionBarMenuItem$$Lambda$6 implements OnEditorActionListener {
    private final ActionBarMenuItem arg$1;

    ActionBarMenuItem$$Lambda$6(ActionBarMenuItem actionBarMenuItem) {
        this.arg$1 = actionBarMenuItem;
    }

    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        return this.arg$1.lambda$setIsSearchField$6$ActionBarMenuItem(textView, i, keyEvent);
    }
}
