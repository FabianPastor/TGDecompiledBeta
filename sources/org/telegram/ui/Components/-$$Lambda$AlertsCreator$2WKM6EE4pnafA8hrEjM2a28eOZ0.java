package org.telegram.ui.Components;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.messenger.MessagesStorage.IntCallback;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AlertsCreator$2WKM6EE4pnafA8hrEjM2a28eOZ0 implements OnClickListener {
    private final /* synthetic */ IntCallback f$0;

    public /* synthetic */ -$$Lambda$AlertsCreator$2WKM6EE4pnafA8hrEjM2a28eOZ0(IntCallback intCallback) {
        this.f$0 = intCallback;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.run(1);
    }
}
