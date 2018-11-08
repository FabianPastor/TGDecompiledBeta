package org.telegram.p005ui.Components;

import android.os.Bundle;
import android.support.v13.view.inputmethod.InputConnectionCompat.OnCommitContentListener;
import android.support.v13.view.inputmethod.InputContentInfoCompat;
import org.telegram.p005ui.Components.ChatActivityEnterView.C12227;

/* renamed from: org.telegram.ui.Components.ChatActivityEnterView$7$$Lambda$0 */
final /* synthetic */ class ChatActivityEnterView$7$$Lambda$0 implements OnCommitContentListener {
    private final C12227 arg$1;

    ChatActivityEnterView$7$$Lambda$0(C12227 c12227) {
        this.arg$1 = c12227;
    }

    public boolean onCommitContent(InputContentInfoCompat inputContentInfoCompat, int i, Bundle bundle) {
        return this.arg$1.lambda$onCreateInputConnection$0$ChatActivityEnterView$7(inputContentInfoCompat, i, bundle);
    }
}
