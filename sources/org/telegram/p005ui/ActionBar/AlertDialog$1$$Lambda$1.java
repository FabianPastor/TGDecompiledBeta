package org.telegram.p005ui.ActionBar;

import android.view.ViewTreeObserver.OnScrollChangedListener;
import org.telegram.p005ui.ActionBar.AlertDialog.C07201;

/* renamed from: org.telegram.ui.ActionBar.AlertDialog$1$$Lambda$1 */
final /* synthetic */ class AlertDialog$1$$Lambda$1 implements OnScrollChangedListener {
    private final C07201 arg$1;

    AlertDialog$1$$Lambda$1(C07201 c07201) {
        this.arg$1 = c07201;
    }

    public void onScrollChanged() {
        this.arg$1.lambda$onLayout$1$AlertDialog$1();
    }
}
