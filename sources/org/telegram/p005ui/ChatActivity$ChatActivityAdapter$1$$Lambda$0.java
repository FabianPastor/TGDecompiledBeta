package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.p005ui.ChatActivity.ChatActivityAdapter.C11591;

/* renamed from: org.telegram.ui.ChatActivity$ChatActivityAdapter$1$$Lambda$0 */
final /* synthetic */ class ChatActivity$ChatActivityAdapter$1$$Lambda$0 implements OnClickListener {
    private final C11591 arg$1;
    private final String arg$2;

    ChatActivity$ChatActivityAdapter$1$$Lambda$0(C11591 c11591, String str) {
        this.arg$1 = c11591;
        this.arg$2 = str;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$didPressedUrl$0$ChatActivity$ChatActivityAdapter$1(this.arg$2, dialogInterface, i);
    }
}
