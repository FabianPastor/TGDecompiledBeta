package org.telegram.ui.Components;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.messenger.SharedConfig;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AlertsCreator$KjNI7sT_C-bswPjwnMDRlnskBPc implements OnClickListener {
    private final /* synthetic */ int[] f$0;

    public /* synthetic */ -$$Lambda$AlertsCreator$KjNI7sT_C-bswPjwnMDRlnskBPc(int[] iArr) {
        this.f$0 = iArr;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        SharedConfig.setKeepMedia(this.f$0[0]);
    }
}