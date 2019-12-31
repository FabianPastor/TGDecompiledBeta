package org.telegram.ui.Components;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AlertsCreator$1jvar_OO0E8-EZLKIOCErJX2WIT8 implements Runnable {
    private final /* synthetic */ EditTextBoldCursor f$0;
    private final /* synthetic */ AlertDialog f$1;
    private final /* synthetic */ BaseFragment f$2;

    public /* synthetic */ -$$Lambda$AlertsCreator$1jvar_OO0E8-EZLKIOCErJX2WIT8(EditTextBoldCursor editTextBoldCursor, AlertDialog alertDialog, BaseFragment baseFragment) {
        this.f$0 = editTextBoldCursor;
        this.f$1 = alertDialog;
        this.f$2 = baseFragment;
    }

    public final void run() {
        AndroidUtilities.runOnUIThread(new -$$Lambda$AlertsCreator$nXM6RqSDfkyNRUJWFcG72h8C-tc(this.f$0, this.f$1, this.f$2));
    }
}
