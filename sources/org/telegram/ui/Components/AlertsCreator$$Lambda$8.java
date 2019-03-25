package org.telegram.ui.Components;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.messenger.MessagesStorage.BooleanCallback;

final /* synthetic */ class AlertsCreator$$Lambda$8 implements OnClickListener {
    private final BooleanCallback arg$1;
    private final boolean[] arg$2;

    AlertsCreator$$Lambda$8(BooleanCallback booleanCallback, boolean[] zArr) {
        this.arg$1 = booleanCallback;
        this.arg$2 = zArr;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        AlertsCreator.lambda$createClearOrDeleteDialogAlert$10$AlertsCreator(this.arg$1, this.arg$2, dialogInterface, i);
    }
}
