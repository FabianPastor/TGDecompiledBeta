package org.telegram.ui.Components;

import android.content.DialogInterface;
import android.widget.EditText;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda35 implements DialogInterface.OnDismissListener {
    public final /* synthetic */ EditText f$0;
    public final /* synthetic */ EditText f$1;

    public /* synthetic */ AlertsCreator$$ExternalSyntheticLambda35(EditText editText, EditText editText2) {
        this.f$0 = editText;
        this.f$1 = editText2;
    }

    public final void onDismiss(DialogInterface dialogInterface) {
        AlertsCreator.lambda$createChangeNameAlert$29(this.f$0, this.f$1, dialogInterface);
    }
}
