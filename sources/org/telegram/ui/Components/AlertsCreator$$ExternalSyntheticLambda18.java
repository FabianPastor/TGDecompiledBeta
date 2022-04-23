package org.telegram.ui.Components;

import android.content.DialogInterface;
import org.telegram.messenger.MessagesStorage;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda18 implements DialogInterface.OnClickListener {
    public final /* synthetic */ MessagesStorage.BooleanCallback f$0;
    public final /* synthetic */ boolean[] f$1;

    public /* synthetic */ AlertsCreator$$ExternalSyntheticLambda18(MessagesStorage.BooleanCallback booleanCallback, boolean[] zArr) {
        this.f$0 = booleanCallback;
        this.f$1 = zArr;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.run(this.f$1[0]);
    }
}
