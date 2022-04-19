package org.telegram.ui.Components;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda89 implements Runnable {
    public final /* synthetic */ EditTextBoldCursor f$0;
    public final /* synthetic */ AlertDialog f$1;
    public final /* synthetic */ BaseFragment f$2;

    public /* synthetic */ AlertsCreator$$ExternalSyntheticLambda89(EditTextBoldCursor editTextBoldCursor, AlertDialog alertDialog, BaseFragment baseFragment) {
        this.f$0 = editTextBoldCursor;
        this.f$1 = alertDialog;
        this.f$2 = baseFragment;
    }

    public final void run() {
        AndroidUtilities.runOnUIThread(new AlertsCreator$$ExternalSyntheticLambda88(this.f$0, this.f$1, this.f$2));
    }
}
