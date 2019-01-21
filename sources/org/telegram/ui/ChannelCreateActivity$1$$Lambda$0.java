package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import org.telegram.ui.ChannelCreateActivity.AnonymousClass1;

final /* synthetic */ class ChannelCreateActivity$1$$Lambda$0 implements OnCancelListener {
    private final AnonymousClass1 arg$1;

    ChannelCreateActivity$1$$Lambda$0(AnonymousClass1 anonymousClass1) {
        this.arg$1 = anonymousClass1;
    }

    public void onCancel(DialogInterface dialogInterface) {
        this.arg$1.lambda$onItemClick$0$ChannelCreateActivity$1(dialogInterface);
    }
}
