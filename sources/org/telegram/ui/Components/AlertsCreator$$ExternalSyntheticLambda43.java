package org.telegram.ui.Components;

import android.content.DialogInterface;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.LaunchActivity;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda43 implements DialogInterface.OnClickListener {
    public final /* synthetic */ TLRPC.TL_langPackLanguage f$0;
    public final /* synthetic */ LaunchActivity f$1;

    public /* synthetic */ AlertsCreator$$ExternalSyntheticLambda43(TLRPC.TL_langPackLanguage tL_langPackLanguage, LaunchActivity launchActivity) {
        this.f$0 = tL_langPackLanguage;
        this.f$1 = launchActivity;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        AlertsCreator.lambda$createLanguageAlert$8(this.f$0, this.f$1, dialogInterface, i);
    }
}
