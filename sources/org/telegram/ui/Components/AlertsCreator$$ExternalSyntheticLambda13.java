package org.telegram.ui.Components;

import android.content.DialogInterface;
import android.widget.EditText;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda13 implements DialogInterface.OnClickListener {
    public final /* synthetic */ EditText f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ EditText f$3;

    public /* synthetic */ AlertsCreator$$ExternalSyntheticLambda13(EditText editText, long j, int i, EditText editText2) {
        this.f$0 = editText;
        this.f$1 = j;
        this.f$2 = i;
        this.f$3 = editText2;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        AlertsCreator.lambda$createChangeNameAlert$31(this.f$0, this.f$1, this.f$2, this.f$3, dialogInterface, i);
    }
}
