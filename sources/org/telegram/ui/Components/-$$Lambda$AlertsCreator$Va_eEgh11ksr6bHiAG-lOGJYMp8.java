package org.telegram.ui.Components;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.ui.CacheControlActivity;
import org.telegram.ui.LaunchActivity;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AlertsCreator$Va_eEgh11ksr6bHiAG-lOGJYMp8 implements OnClickListener {
    private final /* synthetic */ LaunchActivity f$0;

    public /* synthetic */ -$$Lambda$AlertsCreator$Va_eEgh11ksr6bHiAG-lOGJYMp8(LaunchActivity launchActivity) {
        this.f$0 = launchActivity;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$runLinkRequest$30$LaunchActivity(new CacheControlActivity());
    }
}
