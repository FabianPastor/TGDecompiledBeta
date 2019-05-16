package org.telegram.messenger;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MediaController$hrz-cghaZ1kTzzeIoiWSaviEy-E implements OnCancelListener {
    private final /* synthetic */ boolean[] f$0;

    public /* synthetic */ -$$Lambda$MediaController$hrz-cghaZ1kTzzeIoiWSaviEy-E(boolean[] zArr) {
        this.f$0 = zArr;
    }

    public final void onCancel(DialogInterface dialogInterface) {
        this.f$0[0] = true;
    }
}
