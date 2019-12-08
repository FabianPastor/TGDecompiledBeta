package org.telegram.ui.Components.voip;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.tgnet.TLRPC.User;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$VoIPHelper$zyuwLXXVdgKRJLU2rpi7FSmrvEY implements OnClickListener {
    private final /* synthetic */ User f$0;
    private final /* synthetic */ Activity f$1;

    public /* synthetic */ -$$Lambda$VoIPHelper$zyuwLXXVdgKRJLU2rpi7FSmrvEY(User user, Activity activity) {
        this.f$0 = user;
        this.f$1 = activity;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        VoIPHelper.lambda$initiateCall$2(this.f$0, this.f$1, dialogInterface, i);
    }
}
