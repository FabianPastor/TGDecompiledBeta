package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.ui.ChatActivity.ChatActivityAdapter.AnonymousClass1;

final /* synthetic */ class ChatActivity$ChatActivityAdapter$1$$Lambda$0 implements OnClickListener {
    private final AnonymousClass1 arg$1;
    private final String arg$2;

    ChatActivity$ChatActivityAdapter$1$$Lambda$0(AnonymousClass1 anonymousClass1, String str) {
        this.arg$1 = anonymousClass1;
        this.arg$2 = str;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$didPressUrl$0$ChatActivity$ChatActivityAdapter$1(this.arg$2, dialogInterface, i);
    }
}
