package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.p005ui.ChannelAdminLogActivity.ChatActivityAdapter.CLASSNAME;

/* renamed from: org.telegram.ui.ChannelAdminLogActivity$ChatActivityAdapter$1$$Lambda$0 */
final /* synthetic */ class ChannelAdminLogActivity$ChatActivityAdapter$1$$Lambda$0 implements OnClickListener {
    private final CLASSNAME arg$1;
    private final String arg$2;

    ChannelAdminLogActivity$ChatActivityAdapter$1$$Lambda$0(CLASSNAME CLASSNAME, String str) {
        this.arg$1 = CLASSNAME;
        this.arg$2 = str;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.mo15101xeCLASSNAMEe(this.arg$2, dialogInterface, i);
    }
}
