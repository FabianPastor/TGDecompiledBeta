package org.telegram.ui.Components;

import android.content.DialogInterface;
import org.telegram.messenger.MessagesStorage;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda17 implements DialogInterface.OnClickListener {
    public final /* synthetic */ MessagesStorage.IntCallback f$0;

    public /* synthetic */ AlertsCreator$$ExternalSyntheticLambda17(MessagesStorage.IntCallback intCallback) {
        this.f$0 = intCallback;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.run(1);
    }
}
