package org.telegram.ui.Components.voip;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;

public final /* synthetic */ class VoIPHelper$$ExternalSyntheticLambda2 implements DialogInterface.OnClickListener {
    public final /* synthetic */ Activity f$0;
    public final /* synthetic */ Intent f$1;

    public /* synthetic */ VoIPHelper$$ExternalSyntheticLambda2(Activity activity, Intent intent) {
        this.f$0 = activity;
        this.f$1 = intent;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.startActivity(this.f$1);
    }
}
