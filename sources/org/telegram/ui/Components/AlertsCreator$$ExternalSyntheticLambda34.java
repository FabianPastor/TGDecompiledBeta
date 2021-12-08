package org.telegram.ui.Components;

import android.content.DialogInterface;
import android.widget.EditText;
import org.telegram.messenger.AndroidUtilities;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda34 implements DialogInterface.OnDismissListener {
    public final /* synthetic */ EditText f$0;

    public /* synthetic */ AlertsCreator$$ExternalSyntheticLambda34(EditText editText) {
        this.f$0 = editText;
    }

    public final void onDismiss(DialogInterface dialogInterface) {
        AndroidUtilities.hideKeyboard(this.f$0);
    }
}
