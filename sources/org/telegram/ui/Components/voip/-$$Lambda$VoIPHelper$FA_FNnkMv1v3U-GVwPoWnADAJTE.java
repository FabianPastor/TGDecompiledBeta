package org.telegram.ui.Components.voip;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$VoIPHelper$FA_FNnkMv1v3U-GVwPoWnADAJTE implements OnClickListener {
    private final /* synthetic */ Activity f$0;

    public /* synthetic */ -$$Lambda$VoIPHelper$FA_FNnkMv1v3U-GVwPoWnADAJTE(Activity activity) {
        this.f$0 = activity;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        VoIPHelper.lambda$permissionDenied$3(this.f$0, dialogInterface, i);
    }
}
