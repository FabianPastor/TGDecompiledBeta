package org.telegram.ui.ActionBar;

import android.view.KeyEvent;
import android.widget.TextView;

public final /* synthetic */ class ActionBarMenuItem$$ExternalSyntheticLambda8 implements TextView.OnEditorActionListener {
    public final /* synthetic */ ActionBarMenuItem f$0;

    public /* synthetic */ ActionBarMenuItem$$ExternalSyntheticLambda8(ActionBarMenuItem actionBarMenuItem) {
        this.f$0 = actionBarMenuItem;
    }

    public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        return this.f$0.lambda$setIsSearchField$9(textView, i, keyEvent);
    }
}