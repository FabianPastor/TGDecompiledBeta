package org.telegram.p005ui.ActionBar;

import android.view.ViewTreeObserver.OnScrollChangedListener;
import org.telegram.p005ui.ActionBar.AlertDialog.CLASSNAME;

/* renamed from: org.telegram.ui.ActionBar.AlertDialog$1$$Lambda$1 */
final /* synthetic */ class AlertDialog$1$$Lambda$1 implements OnScrollChangedListener {
    private final CLASSNAME arg$1;

    AlertDialog$1$$Lambda$1(CLASSNAME CLASSNAME) {
        this.arg$1 = CLASSNAME;
    }

    public void onScrollChanged() {
        this.arg$1.lambda$onLayout$1$AlertDialog$1();
    }
}
