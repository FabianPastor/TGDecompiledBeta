package org.telegram.ui.Components;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.messenger.MessagesStorage.IntCallback;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AlertsCreator$mK-3N6UsQGpxwpYn5q6EG9jwH5M implements OnClickListener {
    private final /* synthetic */ IntCallback f$0;

    public /* synthetic */ -$$Lambda$AlertsCreator$mK-3N6UsQGpxwpYn5q6EG9jwH5M(IntCallback intCallback) {
        this.f$0 = intCallback;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.run(0);
    }
}
