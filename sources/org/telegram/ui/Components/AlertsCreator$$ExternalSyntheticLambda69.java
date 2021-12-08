package org.telegram.ui.Components;

import android.content.DialogInterface;
import android.view.KeyEvent;
import android.widget.TextView;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda69 implements TextView.OnEditorActionListener {
    public final /* synthetic */ long f$0;
    public final /* synthetic */ AlertDialog f$1;
    public final /* synthetic */ DialogInterface.OnClickListener f$2;

    public /* synthetic */ AlertsCreator$$ExternalSyntheticLambda69(long j, AlertDialog alertDialog, DialogInterface.OnClickListener onClickListener) {
        this.f$0 = j;
        this.f$1 = alertDialog;
        this.f$2 = onClickListener;
    }

    public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        return AlertsCreator.lambda$createChangeBioAlert$25(this.f$0, this.f$1, this.f$2, textView, i, keyEvent);
    }
}
