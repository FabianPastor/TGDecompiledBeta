package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.ui.ChatActivity.ChatActivityAdapter.C09221;

final /* synthetic */ class ChatActivity$ChatActivityAdapter$1$$Lambda$0 implements OnClickListener {
    private final C09221 arg$1;
    private final String arg$2;

    ChatActivity$ChatActivityAdapter$1$$Lambda$0(C09221 c09221, String str) {
        this.arg$1 = c09221;
        this.arg$2 = str;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$didPressedUrl$0$ChatActivity$ChatActivityAdapter$1(this.arg$2, dialogInterface, i);
    }
}
