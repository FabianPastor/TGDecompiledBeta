package org.telegram.ui.Components;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.tgnet.TLRPC.TL_langPackLanguage;
import org.telegram.ui.LaunchActivity;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AlertsCreator$TOP_U4EklnBKZLMjFtDvABD1mPg implements OnClickListener {
    private final /* synthetic */ TL_langPackLanguage f$0;
    private final /* synthetic */ LaunchActivity f$1;

    public /* synthetic */ -$$Lambda$AlertsCreator$TOP_U4EklnBKZLMjFtDvABD1mPg(TL_langPackLanguage tL_langPackLanguage, LaunchActivity launchActivity) {
        this.f$0 = tL_langPackLanguage;
        this.f$1 = launchActivity;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        AlertsCreator.lambda$createLanguageAlert$2(this.f$0, this.f$1, dialogInterface, i);
    }
}
