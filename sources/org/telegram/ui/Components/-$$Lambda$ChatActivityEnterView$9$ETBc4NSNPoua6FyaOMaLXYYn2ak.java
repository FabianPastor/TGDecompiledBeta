package org.telegram.ui.Components;

import android.os.Bundle;
import androidx.core.view.inputmethod.InputConnectionCompat.OnCommitContentListener;
import androidx.core.view.inputmethod.InputContentInfoCompat;
import org.telegram.ui.Components.ChatActivityEnterView.AnonymousClass9;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivityEnterView$9$ETBc4NSNPoua6FyaOMaLXYYn2ak implements OnCommitContentListener {
    private final /* synthetic */ AnonymousClass9 f$0;

    public /* synthetic */ -$$Lambda$ChatActivityEnterView$9$ETBc4NSNPoua6FyaOMaLXYYn2ak(AnonymousClass9 anonymousClass9) {
        this.f$0 = anonymousClass9;
    }

    public final boolean onCommitContent(InputContentInfoCompat inputContentInfoCompat, int i, Bundle bundle) {
        return this.f$0.lambda$onCreateInputConnection$0$ChatActivityEnterView$9(inputContentInfoCompat, i, bundle);
    }
}
