package org.telegram.p005ui;

import android.view.View;
import android.view.View.OnClickListener;
import org.telegram.p005ui.ActionBar.AlertDialog;
import org.telegram.p005ui.Components.EditTextBoldCursor;

/* renamed from: org.telegram.ui.ThemeActivity$$Lambda$8 */
final /* synthetic */ class ThemeActivity$$Lambda$8 implements OnClickListener {
    private final ThemeActivity arg$1;
    private final EditTextBoldCursor arg$2;
    private final AlertDialog arg$3;

    ThemeActivity$$Lambda$8(ThemeActivity themeActivity, EditTextBoldCursor editTextBoldCursor, AlertDialog alertDialog) {
        this.arg$1 = themeActivity;
        this.arg$2 = editTextBoldCursor;
        this.arg$3 = alertDialog;
    }

    public void onClick(View view) {
        this.arg$1.lambda$null$4$ThemeActivity(this.arg$2, this.arg$3, view);
    }
}
