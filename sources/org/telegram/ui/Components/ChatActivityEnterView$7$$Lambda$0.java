package org.telegram.ui.Components;

import android.os.Bundle;
import android.support.v13.view.inputmethod.InputConnectionCompat.OnCommitContentListener;
import android.support.v13.view.inputmethod.InputContentInfoCompat;
import org.telegram.ui.Components.ChatActivityEnterView.C17667;

final /* synthetic */ class ChatActivityEnterView$7$$Lambda$0 implements OnCommitContentListener {
    private final C17667 arg$1;

    ChatActivityEnterView$7$$Lambda$0(C17667 c17667) {
        this.arg$1 = c17667;
    }

    public boolean onCommitContent(InputContentInfoCompat inputContentInfoCompat, int i, Bundle bundle) {
        return this.arg$1.lambda$onCreateInputConnection$0$ChatActivityEnterView$7(inputContentInfoCompat, i, bundle);
    }
}
