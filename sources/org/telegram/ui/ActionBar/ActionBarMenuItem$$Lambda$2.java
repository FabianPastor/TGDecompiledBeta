package org.telegram.ui.ActionBar;

import android.view.KeyEvent;
import org.telegram.ui.ActionBar.ActionBarPopupWindow.OnDispatchKeyEventListener;

final /* synthetic */ class ActionBarMenuItem$$Lambda$2 implements OnDispatchKeyEventListener {
    private final ActionBarMenuItem arg$1;

    ActionBarMenuItem$$Lambda$2(ActionBarMenuItem actionBarMenuItem) {
        this.arg$1 = actionBarMenuItem;
    }

    public void onDispatchKeyEvent(KeyEvent keyEvent) {
        this.arg$1.lambda$createPopupLayout$2$ActionBarMenuItem(keyEvent);
    }
}
