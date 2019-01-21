package org.telegram.ui.Components;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.tgnet.TLRPC.TL_langPackLanguage;
import org.telegram.ui.LaunchActivity;

final /* synthetic */ class AlertsCreator$$Lambda$2 implements OnClickListener {
    private final TL_langPackLanguage arg$1;
    private final LaunchActivity arg$2;

    AlertsCreator$$Lambda$2(TL_langPackLanguage tL_langPackLanguage, LaunchActivity launchActivity) {
        this.arg$1 = tL_langPackLanguage;
        this.arg$2 = launchActivity;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        AlertsCreator.lambda$createLanguageAlert$2$AlertsCreator(this.arg$1, this.arg$2, dialogInterface, i);
    }
}
