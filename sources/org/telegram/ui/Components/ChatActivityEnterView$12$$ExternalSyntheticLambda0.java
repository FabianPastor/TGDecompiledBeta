package org.telegram.ui.Components;

import android.os.Bundle;
import androidx.core.view.inputmethod.InputConnectionCompat;
import androidx.core.view.inputmethod.InputContentInfoCompat;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.ChatActivityEnterView;

public final /* synthetic */ class ChatActivityEnterView$12$$ExternalSyntheticLambda0 implements InputConnectionCompat.OnCommitContentListener {
    public final /* synthetic */ ChatActivityEnterView.AnonymousClass12 f$0;
    public final /* synthetic */ Theme.ResourcesProvider f$1;

    public /* synthetic */ ChatActivityEnterView$12$$ExternalSyntheticLambda0(ChatActivityEnterView.AnonymousClass12 r1, Theme.ResourcesProvider resourcesProvider) {
        this.f$0 = r1;
        this.f$1 = resourcesProvider;
    }

    public final boolean onCommitContent(InputContentInfoCompat inputContentInfoCompat, int i, Bundle bundle) {
        return this.f$0.m3748x6fvar_c9(this.f$1, inputContentInfoCompat, i, bundle);
    }
}
