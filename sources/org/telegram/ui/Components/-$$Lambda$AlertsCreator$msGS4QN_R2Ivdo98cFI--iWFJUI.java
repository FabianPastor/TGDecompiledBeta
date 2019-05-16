package org.telegram.ui.Components;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.browser.Browser;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AlertsCreator$msGS4QN_R2Ivdo98cFI--iWFJUI implements OnClickListener {
    private final /* synthetic */ Context f$0;

    public /* synthetic */ -$$Lambda$AlertsCreator$msGS4QN_R2Ivdo98cFI--iWFJUI(Context context) {
        this.f$0 = context;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        Browser.openUrl(this.f$0, BuildVars.PLAYSTORE_APP_URL);
    }
}
