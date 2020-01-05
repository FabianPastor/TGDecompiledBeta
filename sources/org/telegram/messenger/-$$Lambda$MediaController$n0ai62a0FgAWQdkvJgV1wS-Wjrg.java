package org.telegram.messenger;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MediaController$n0ai62a0FgAWQdkvJgV1wS-Wjrg implements OnCancelListener {
    private final /* synthetic */ boolean[] f$0;

    public /* synthetic */ -$$Lambda$MediaController$n0ai62a0FgAWQdkvJgV1wS-Wjrg(boolean[] zArr) {
        this.f$0 = zArr;
    }

    public final void onCancel(DialogInterface dialogInterface) {
        this.f$0[0] = true;
    }
}
