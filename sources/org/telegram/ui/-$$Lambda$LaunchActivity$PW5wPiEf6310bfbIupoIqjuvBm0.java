package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import java.util.HashMap;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LaunchActivity$PW5wPiEvar_bfbIupoIqjuvBm0 implements OnClickListener {
    private final /* synthetic */ LaunchActivity f$0;
    private final /* synthetic */ HashMap f$1;
    private final /* synthetic */ int f$2;

    public /* synthetic */ -$$Lambda$LaunchActivity$PW5wPiEvar_bfbIupoIqjuvBm0(LaunchActivity launchActivity, HashMap hashMap, int i) {
        this.f$0 = launchActivity;
        this.f$1 = hashMap;
        this.f$2 = i;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$didReceivedNotification$47$LaunchActivity(this.f$1, this.f$2, dialogInterface, i);
    }
}