package org.telegram.ui;

import android.content.DialogInterface;

public final /* synthetic */ class ChangeBioActivity$$ExternalSyntheticLambda0 implements DialogInterface.OnCancelListener {
    public final /* synthetic */ ChangeBioActivity f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ ChangeBioActivity$$ExternalSyntheticLambda0(ChangeBioActivity changeBioActivity, int i) {
        this.f$0 = changeBioActivity;
        this.f$1 = i;
    }

    public final void onCancel(DialogInterface dialogInterface) {
        this.f$0.lambda$saveName$5(this.f$1, dialogInterface);
    }
}
