package org.telegram.ui.ActionBar;

import android.view.ViewTreeObserver.OnScrollChangedListener;
import org.telegram.ui.ActionBar.AlertDialog.C05971;

final /* synthetic */ class AlertDialog$1$$Lambda$1 implements OnScrollChangedListener {
    private final C05971 arg$1;

    AlertDialog$1$$Lambda$1(C05971 c05971) {
        this.arg$1 = c05971;
    }

    public void onScrollChanged() {
        this.arg$1.lambda$onLayout$1$AlertDialog$1();
    }
}
