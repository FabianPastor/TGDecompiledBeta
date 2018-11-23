package org.telegram.p005ui.Components;

import android.os.Bundle;
import android.support.v13.view.inputmethod.InputConnectionCompat.OnCommitContentListener;
import android.support.v13.view.inputmethod.InputContentInfoCompat;
import org.telegram.p005ui.Components.ChatActivityEnterView.C13458;

/* renamed from: org.telegram.ui.Components.ChatActivityEnterView$8$$Lambda$0 */
final /* synthetic */ class ChatActivityEnterView$8$$Lambda$0 implements OnCommitContentListener {
    private final C13458 arg$1;

    ChatActivityEnterView$8$$Lambda$0(C13458 c13458) {
        this.arg$1 = c13458;
    }

    public boolean onCommitContent(InputContentInfoCompat inputContentInfoCompat, int i, Bundle bundle) {
        return this.arg$1.lambda$onCreateInputConnection$0$ChatActivityEnterView$8(inputContentInfoCompat, i, bundle);
    }
}
