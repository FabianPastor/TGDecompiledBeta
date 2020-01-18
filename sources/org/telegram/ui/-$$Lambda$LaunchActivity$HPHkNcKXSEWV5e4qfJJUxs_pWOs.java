package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LaunchActivity$HPHkNcKXSEWV5e4qfJJUxs_pWOs implements OnCancelListener {
    private final /* synthetic */ boolean[] f$0;

    public /* synthetic */ -$$Lambda$LaunchActivity$HPHkNcKXSEWV5e4qfJJUxs_pWOs(boolean[] zArr) {
        this.f$0 = zArr;
    }

    public final void onCancel(DialogInterface dialogInterface) {
        this.f$0[0] = true;
    }
}
