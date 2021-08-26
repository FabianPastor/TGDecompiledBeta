package org.telegram.ui.Components;

import android.content.DialogInterface;
import android.view.KeyEvent;
import android.widget.TextView;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda62 implements TextView.OnEditorActionListener {
    public final /* synthetic */ int f$0;
    public final /* synthetic */ AlertDialog f$1;
    public final /* synthetic */ DialogInterface.OnClickListener f$2;

    public /* synthetic */ AlertsCreator$$ExternalSyntheticLambda62(int i, AlertDialog alertDialog, DialogInterface.OnClickListener onClickListener) {
        this.f$0 = i;
        this.f$1 = alertDialog;
        this.f$2 = onClickListener;
    }

    public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        return AlertsCreator.lambda$createChangeBioAlert$23(this.f$0, this.f$1, this.f$2, textView, i, keyEvent);
    }
}
