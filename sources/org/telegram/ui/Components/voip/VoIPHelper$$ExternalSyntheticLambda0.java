package org.telegram.ui.Components.voip;

import android.app.Activity;
import android.content.DialogInterface;

public final /* synthetic */ class VoIPHelper$$ExternalSyntheticLambda0 implements DialogInterface.OnClickListener {
    public final /* synthetic */ Activity f$0;

    public /* synthetic */ VoIPHelper$$ExternalSyntheticLambda0(Activity activity) {
        this.f$0 = activity;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        VoIPHelper.lambda$permissionDenied$7(this.f$0, dialogInterface, i);
    }
}
