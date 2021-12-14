package org.telegram.ui.Components.voip;

import android.content.Context;
import android.content.DialogInterface;
import java.io.File;

public final /* synthetic */ class VoIPHelper$$ExternalSyntheticLambda3 implements DialogInterface.OnClickListener {
    public final /* synthetic */ Context f$0;
    public final /* synthetic */ File f$1;

    public /* synthetic */ VoIPHelper$$ExternalSyntheticLambda3(Context context, File file) {
        this.f$0 = context;
        this.f$1 = file;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        VoIPHelper.lambda$showRateAlert$13(this.f$0, this.f$1, dialogInterface, i);
    }
}
