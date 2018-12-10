package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import org.telegram.p005ui.ChannelEditActivity.CLASSNAME;

/* renamed from: org.telegram.ui.ChannelEditActivity$1$$Lambda$0 */
final /* synthetic */ class ChannelEditActivity$1$$Lambda$0 implements OnCancelListener {
    private final CLASSNAME arg$1;

    ChannelEditActivity$1$$Lambda$0(CLASSNAME CLASSNAME) {
        this.arg$1 = CLASSNAME;
    }

    public void onCancel(DialogInterface dialogInterface) {
        this.arg$1.lambda$onItemClick$0$ChannelEditActivity$1(dialogInterface);
    }
}
