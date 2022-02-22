package org.telegram.ui.Components;

import android.content.Context;
import android.content.DialogInterface;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.browser.Browser;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda11 implements DialogInterface.OnClickListener {
    public final /* synthetic */ Context f$0;

    public /* synthetic */ AlertsCreator$$ExternalSyntheticLambda11(Context context) {
        this.f$0 = context;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        Browser.openUrl(this.f$0, BuildVars.PLAYSTORE_APP_URL);
    }
}
