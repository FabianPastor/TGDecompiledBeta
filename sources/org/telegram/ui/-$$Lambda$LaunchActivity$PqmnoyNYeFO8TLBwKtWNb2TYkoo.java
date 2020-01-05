package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import java.util.HashMap;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LaunchActivity$PqmnoyNYeFO8TLBwKtWNb2TYkoo implements OnClickListener {
    private final /* synthetic */ LaunchActivity f$0;
    private final /* synthetic */ HashMap f$1;
    private final /* synthetic */ int f$2;

    public /* synthetic */ -$$Lambda$LaunchActivity$PqmnoyNYeFO8TLBwKtWNb2TYkoo(LaunchActivity launchActivity, HashMap hashMap, int i) {
        this.f$0 = launchActivity;
        this.f$1 = hashMap;
        this.f$2 = i;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$didReceivedNotification$52$LaunchActivity(this.f$1, this.f$2, dialogInterface, i);
    }
}
