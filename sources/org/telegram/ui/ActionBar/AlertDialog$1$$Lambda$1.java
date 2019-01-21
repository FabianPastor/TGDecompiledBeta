package org.telegram.ui.ActionBar;

import android.view.ViewTreeObserver.OnScrollChangedListener;
import org.telegram.ui.ActionBar.AlertDialog.AnonymousClass1;

final /* synthetic */ class AlertDialog$1$$Lambda$1 implements OnScrollChangedListener {
    private final AnonymousClass1 arg$1;

    AlertDialog$1$$Lambda$1(AnonymousClass1 anonymousClass1) {
        this.arg$1 = anonymousClass1;
    }

    public void onScrollChanged() {
        this.arg$1.lambda$onLayout$1$AlertDialog$1();
    }
}
