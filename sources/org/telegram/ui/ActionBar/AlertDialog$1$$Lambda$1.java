package org.telegram.ui.ActionBar;

import android.view.ViewTreeObserver.OnScrollChangedListener;
import org.telegram.ui.ActionBar.AlertDialog.C04841;

final /* synthetic */ class AlertDialog$1$$Lambda$1 implements OnScrollChangedListener {
    private final C04841 arg$1;

    AlertDialog$1$$Lambda$1(C04841 c04841) {
        this.arg$1 = c04841;
    }

    public void onScrollChanged() {
        this.arg$1.lambda$onLayout$1$AlertDialog$1();
    }
}
