package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LaunchActivity$_HOGKHkfKnPkfgwvch-IiBcFdxs implements OnCancelListener {
    private final /* synthetic */ boolean[] f$0;

    public /* synthetic */ -$$Lambda$LaunchActivity$_HOGKHkfKnPkfgwvch-IiBcFdxs(boolean[] zArr) {
        this.f$0 = zArr;
    }

    public final void onCancel(DialogInterface dialogInterface) {
        this.f$0[0] = true;
    }
}
