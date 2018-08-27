package org.telegram.ui.Components;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.browser.Browser;

final /* synthetic */ class AlertsCreator$$Lambda$0 implements OnClickListener {
    private final Context arg$1;

    AlertsCreator$$Lambda$0(Context context) {
        this.arg$1 = context;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        Browser.openUrl(this.arg$1, BuildVars.PLAYSTORE_APP_URL);
    }
}
