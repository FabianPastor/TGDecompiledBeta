package org.telegram.ui.Components.voip;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$VoIPHelper$p72unkFJ30ZMfZqzJkWjR1RFxeM implements OnClickListener {
    private final /* synthetic */ Activity f$0;
    private final /* synthetic */ Intent f$1;

    public /* synthetic */ -$$Lambda$VoIPHelper$p72unkFJ30ZMfZqzJkWjR1RFxeM(Activity activity, Intent intent) {
        this.f$0 = activity;
        this.f$1 = intent;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.startActivity(this.f$1);
    }
}
