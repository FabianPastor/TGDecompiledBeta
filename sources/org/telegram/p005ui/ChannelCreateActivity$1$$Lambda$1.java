package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import org.telegram.p005ui.ChannelCreateActivity.CLASSNAME;

/* renamed from: org.telegram.ui.ChannelCreateActivity$1$$Lambda$1 */
final /* synthetic */ class ChannelCreateActivity$1$$Lambda$1 implements OnCancelListener {
    private final CLASSNAME arg$1;
    private final int arg$2;

    ChannelCreateActivity$1$$Lambda$1(CLASSNAME CLASSNAME, int i) {
        this.arg$1 = CLASSNAME;
        this.arg$2 = i;
    }

    public void onCancel(DialogInterface dialogInterface) {
        this.arg$1.lambda$onItemClick$1$ChannelCreateActivity$1(this.arg$2, dialogInterface);
    }
}
