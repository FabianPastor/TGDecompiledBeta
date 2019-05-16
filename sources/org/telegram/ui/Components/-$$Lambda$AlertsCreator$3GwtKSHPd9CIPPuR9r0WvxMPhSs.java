package org.telegram.ui.Components;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.messenger.MessagesStorage.BooleanCallback;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AlertsCreator$3GwtKSHPd9CIPPuR9r0WvxMPhSs implements OnClickListener {
    private final /* synthetic */ BooleanCallback f$0;
    private final /* synthetic */ boolean[] f$1;

    public /* synthetic */ -$$Lambda$AlertsCreator$3GwtKSHPd9CIPPuR9r0WvxMPhSs(BooleanCallback booleanCallback, boolean[] zArr) {
        this.f$0 = booleanCallback;
        this.f$1 = zArr;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        AlertsCreator.lambda$createClearOrDeleteDialogAlert$10(this.f$0, this.f$1, dialogInterface, i);
    }
}
