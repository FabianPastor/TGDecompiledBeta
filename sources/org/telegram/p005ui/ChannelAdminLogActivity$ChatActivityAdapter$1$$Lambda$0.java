package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.p005ui.ChannelAdminLogActivity.ChatActivityAdapter.C12821;

/* renamed from: org.telegram.ui.ChannelAdminLogActivity$ChatActivityAdapter$1$$Lambda$0 */
final /* synthetic */ class ChannelAdminLogActivity$ChatActivityAdapter$1$$Lambda$0 implements OnClickListener {
    private final C12821 arg$1;
    private final String arg$2;

    ChannelAdminLogActivity$ChatActivityAdapter$1$$Lambda$0(C12821 c12821, String str) {
        this.arg$1 = c12821;
        this.arg$2 = str;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.mo16041xd3354c8f(this.arg$2, dialogInterface, i);
    }
}
