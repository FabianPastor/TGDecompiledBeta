package org.telegram.ui.Components;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.ui.CacheControlActivity;
import org.telegram.ui.LaunchActivity;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AlertsCreator$iClqD2-jkNM7kdikk3gtVUT5Sn4 implements OnClickListener {
    private final /* synthetic */ LaunchActivity f$0;

    public /* synthetic */ -$$Lambda$AlertsCreator$iClqD2-jkNM7kdikk3gtVUT5Sn4(LaunchActivity launchActivity) {
        this.f$0 = launchActivity;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$runLinkRequest$27$LaunchActivity(new CacheControlActivity());
    }
}
