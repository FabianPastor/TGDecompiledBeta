package org.telegram.ui;

import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;

final /* synthetic */ class NewContactActivity$$Lambda$6 implements OnKeyListener {
    private final NewContactActivity arg$1;

    NewContactActivity$$Lambda$6(NewContactActivity newContactActivity) {
        this.arg$1 = newContactActivity;
    }

    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        return this.arg$1.lambda$createView$8$NewContactActivity(view, i, keyEvent);
    }
}
