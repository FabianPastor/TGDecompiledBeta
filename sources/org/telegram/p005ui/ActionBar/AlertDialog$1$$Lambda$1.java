package org.telegram.p005ui.ActionBar;

import android.view.ViewTreeObserver.OnScrollChangedListener;
import org.telegram.p005ui.ActionBar.AlertDialog.C06621;

/* renamed from: org.telegram.ui.ActionBar.AlertDialog$1$$Lambda$1 */
final /* synthetic */ class AlertDialog$1$$Lambda$1 implements OnScrollChangedListener {
    private final C06621 arg$1;

    AlertDialog$1$$Lambda$1(C06621 c06621) {
        this.arg$1 = c06621;
    }

    public void onScrollChanged() {
        this.arg$1.lambda$onLayout$1$AlertDialog$1();
    }
}
