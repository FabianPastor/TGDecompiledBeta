package org.telegram.ui.Components;

import android.content.DialogInterface;
import org.telegram.messenger.MessagesStorage;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda31 implements DialogInterface.OnClickListener {
    public final /* synthetic */ int[] f$0;
    public final /* synthetic */ MessagesStorage.IntCallback f$1;

    public /* synthetic */ AlertsCreator$$ExternalSyntheticLambda31(int[] iArr, MessagesStorage.IntCallback intCallback) {
        this.f$0 = iArr;
        this.f$1 = intCallback;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        AlertsCreator.lambda$createLocationUpdateDialog$69(this.f$0, this.f$1, dialogInterface, i);
    }
}