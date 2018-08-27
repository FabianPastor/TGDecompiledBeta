package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.ui.ChatActivity.ChatActivityAdapter.C14841;

final /* synthetic */ class ChatActivity$ChatActivityAdapter$1$$Lambda$0 implements OnClickListener {
    private final C14841 arg$1;
    private final String arg$2;

    ChatActivity$ChatActivityAdapter$1$$Lambda$0(C14841 c14841, String str) {
        this.arg$1 = c14841;
        this.arg$2 = str;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$didPressedUrl$0$ChatActivity$ChatActivityAdapter$1(this.arg$2, dialogInterface, i);
    }
}
