package org.telegram.ui.Components;

import android.view.View;
import android.view.View.OnClickListener;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme.ThemeAccent;
import org.telegram.ui.ActionBar.Theme.ThemeInfo;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AlertsCreator$kY_IuBuIRtt9K0_qm7YfE68TTG8 implements OnClickListener {
    private final /* synthetic */ BaseFragment f$0;
    private final /* synthetic */ EditTextBoldCursor f$1;
    private final /* synthetic */ ThemeAccent f$2;
    private final /* synthetic */ ThemeInfo f$3;
    private final /* synthetic */ AlertDialog f$4;

    public /* synthetic */ -$$Lambda$AlertsCreator$kY_IuBuIRtt9K0_qm7YfE68TTG8(BaseFragment baseFragment, EditTextBoldCursor editTextBoldCursor, ThemeAccent themeAccent, ThemeInfo themeInfo, AlertDialog alertDialog) {
        this.f$0 = baseFragment;
        this.f$1 = editTextBoldCursor;
        this.f$2 = themeAccent;
        this.f$3 = themeInfo;
        this.f$4 = alertDialog;
    }

    public final void onClick(View view) {
        AlertsCreator.lambda$createThemeCreateDialog$67(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, view);
    }
}
