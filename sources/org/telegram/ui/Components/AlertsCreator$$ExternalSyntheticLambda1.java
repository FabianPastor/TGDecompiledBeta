package org.telegram.ui.Components;

import android.content.DialogInterface;
import android.widget.EditText;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda1 implements DialogInterface.OnClickListener {
    public final /* synthetic */ int f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ EditText f$2;

    public /* synthetic */ AlertsCreator$$ExternalSyntheticLambda1(int i, int i2, EditText editText) {
        this.f$0 = i;
        this.f$1 = i2;
        this.f$2 = editText;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        AlertsCreator.lambda$createChangeBioAlert$21(this.f$0, this.f$1, this.f$2, dialogInterface, i);
    }
}
