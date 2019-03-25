package org.telegram.ui.Components;

import android.os.Bundle;
import android.support.v13.view.inputmethod.InputConnectionCompat.OnCommitContentListener;
import android.support.v13.view.inputmethod.InputContentInfoCompat;
import org.telegram.ui.Components.ChatActivityEnterView.AnonymousClass8;

final /* synthetic */ class ChatActivityEnterView$8$$Lambda$0 implements OnCommitContentListener {
    private final AnonymousClass8 arg$1;

    ChatActivityEnterView$8$$Lambda$0(AnonymousClass8 anonymousClass8) {
        this.arg$1 = anonymousClass8;
    }

    public boolean onCommitContent(InputContentInfoCompat inputContentInfoCompat, int i, Bundle bundle) {
        return this.arg$1.lambda$onCreateInputConnection$0$ChatActivityEnterView$8(inputContentInfoCompat, i, bundle);
    }
}
