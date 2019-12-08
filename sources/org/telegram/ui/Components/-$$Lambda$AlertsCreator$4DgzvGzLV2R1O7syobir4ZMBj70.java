package org.telegram.ui.Components;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.messenger.MessagesStorage.IntCallback;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AlertsCreator$4DgzvGzLV2R1O7syobir4ZMBj70 implements OnClickListener {
    private final /* synthetic */ int[] f$0;
    private final /* synthetic */ IntCallback f$1;

    public /* synthetic */ -$$Lambda$AlertsCreator$4DgzvGzLV2R1O7syobir4ZMBj70(int[] iArr, IntCallback intCallback) {
        this.f$0 = iArr;
        this.f$1 = intCallback;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        AlertsCreator.lambda$createLocationUpdateDialog$38(this.f$0, this.f$1, dialogInterface, i);
    }
}
