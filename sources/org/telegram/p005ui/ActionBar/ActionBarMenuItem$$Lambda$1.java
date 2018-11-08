package org.telegram.p005ui.ActionBar;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/* renamed from: org.telegram.ui.ActionBar.ActionBarMenuItem$$Lambda$1 */
final /* synthetic */ class ActionBarMenuItem$$Lambda$1 implements OnTouchListener {
    private final ActionBarMenuItem arg$1;

    ActionBarMenuItem$$Lambda$1(ActionBarMenuItem actionBarMenuItem) {
        this.arg$1 = actionBarMenuItem;
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        return this.arg$1.lambda$createPopupLayout$1$ActionBarMenuItem(view, motionEvent);
    }
}
