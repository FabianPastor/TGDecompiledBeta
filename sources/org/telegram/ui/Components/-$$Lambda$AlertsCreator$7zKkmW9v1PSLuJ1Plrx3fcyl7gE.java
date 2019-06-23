package org.telegram.ui.Components;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.ui.CacheControlActivity;
import org.telegram.ui.LaunchActivity;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AlertsCreator$7zKkmW9v1PSLuJ1Plrx3fcyl7gE implements OnClickListener {
    private final /* synthetic */ LaunchActivity f$0;

    public /* synthetic */ -$$Lambda$AlertsCreator$7zKkmW9v1PSLuJ1Plrx3fcyl7gE(LaunchActivity launchActivity) {
        this.f$0 = launchActivity;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$runLinkRequest$27$LaunchActivity(new CacheControlActivity());
    }
}
