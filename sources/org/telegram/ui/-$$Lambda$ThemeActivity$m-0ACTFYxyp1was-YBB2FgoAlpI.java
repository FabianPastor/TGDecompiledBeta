package org.telegram.ui;

import android.view.View;
import android.view.View.OnClickListener;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.Components.EditTextBoldCursor;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ThemeActivity$m-0ACTFYxyp1was-YBB2FgoAlpI implements OnClickListener {
    private final /* synthetic */ ThemeActivity f$0;
    private final /* synthetic */ EditTextBoldCursor f$1;
    private final /* synthetic */ AlertDialog f$2;

    public /* synthetic */ -$$Lambda$ThemeActivity$m-0ACTFYxyp1was-YBB2FgoAlpI(ThemeActivity themeActivity, EditTextBoldCursor editTextBoldCursor, AlertDialog alertDialog) {
        this.f$0 = themeActivity;
        this.f$1 = editTextBoldCursor;
        this.f$2 = alertDialog;
    }

    public final void onClick(View view) {
        this.f$0.lambda$openThemeCreate$11$ThemeActivity(this.f$1, this.f$2, view);
    }
}
