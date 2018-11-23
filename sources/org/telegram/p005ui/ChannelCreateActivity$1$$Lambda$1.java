package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.p005ui.ChannelCreateActivity.C12871;

/* renamed from: org.telegram.ui.ChannelCreateActivity$1$$Lambda$1 */
final /* synthetic */ class ChannelCreateActivity$1$$Lambda$1 implements OnClickListener {
    private final C12871 arg$1;
    private final int arg$2;

    ChannelCreateActivity$1$$Lambda$1(C12871 c12871, int i) {
        this.arg$1 = c12871;
        this.arg$2 = i;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$onItemClick$1$ChannelCreateActivity$1(this.arg$2, dialogInterface, i);
    }
}
