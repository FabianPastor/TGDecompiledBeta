package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import org.telegram.ui.ChannelCreateActivity.AnonymousClass1;

final /* synthetic */ class ChannelCreateActivity$1$$Lambda$1 implements OnCancelListener {
    private final AnonymousClass1 arg$1;
    private final int arg$2;

    ChannelCreateActivity$1$$Lambda$1(AnonymousClass1 anonymousClass1, int i) {
        this.arg$1 = anonymousClass1;
        this.arg$2 = i;
    }

    public void onCancel(DialogInterface dialogInterface) {
        this.arg$1.lambda$onItemClick$1$ChannelCreateActivity$1(this.arg$2, dialogInterface);
    }
}
