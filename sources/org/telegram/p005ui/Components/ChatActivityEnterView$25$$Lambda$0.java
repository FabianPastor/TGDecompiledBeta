package org.telegram.p005ui.Components;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.p005ui.Components.ChatActivityEnterView.CLASSNAME;

/* renamed from: org.telegram.ui.Components.ChatActivityEnterView$25$$Lambda$0 */
final /* synthetic */ class ChatActivityEnterView$25$$Lambda$0 implements OnClickListener {
    private final CLASSNAME arg$1;

    ChatActivityEnterView$25$$Lambda$0(CLASSNAME CLASSNAME) {
        this.arg$1 = CLASSNAME;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$onClearEmojiRecent$0$ChatActivityEnterView$25(dialogInterface, i);
    }
}
