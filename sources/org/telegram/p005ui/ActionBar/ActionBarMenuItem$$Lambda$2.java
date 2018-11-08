package org.telegram.p005ui.ActionBar;

import android.view.KeyEvent;
import org.telegram.p005ui.ActionBar.ActionBarPopupWindow.OnDispatchKeyEventListener;

/* renamed from: org.telegram.ui.ActionBar.ActionBarMenuItem$$Lambda$2 */
final /* synthetic */ class ActionBarMenuItem$$Lambda$2 implements OnDispatchKeyEventListener {
    private final ActionBarMenuItem arg$1;

    ActionBarMenuItem$$Lambda$2(ActionBarMenuItem actionBarMenuItem) {
        this.arg$1 = actionBarMenuItem;
    }

    public void onDispatchKeyEvent(KeyEvent keyEvent) {
        this.arg$1.lambda$createPopupLayout$2$ActionBarMenuItem(keyEvent);
    }
}
