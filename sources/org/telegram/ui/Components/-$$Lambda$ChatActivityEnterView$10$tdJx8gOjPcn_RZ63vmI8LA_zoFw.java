package org.telegram.ui.Components;

import android.os.Bundle;
import androidx.core.view.inputmethod.InputConnectionCompat.OnCommitContentListener;
import androidx.core.view.inputmethod.InputContentInfoCompat;
import org.telegram.ui.Components.ChatActivityEnterView.AnonymousClass10;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivityEnterView$10$tdJx8gOjPcn_RZ63vmI8LA_zoFw implements OnCommitContentListener {
    private final /* synthetic */ AnonymousClass10 f$0;

    public /* synthetic */ -$$Lambda$ChatActivityEnterView$10$tdJx8gOjPcn_RZ63vmI8LA_zoFw(AnonymousClass10 anonymousClass10) {
        this.f$0 = anonymousClass10;
    }

    public final boolean onCommitContent(InputContentInfoCompat inputContentInfoCompat, int i, Bundle bundle) {
        return this.f$0.lambda$onCreateInputConnection$1$ChatActivityEnterView$10(inputContentInfoCompat, i, bundle);
    }
}
