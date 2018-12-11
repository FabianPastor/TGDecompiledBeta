package org.telegram.p005ui;

import android.view.View;
import android.view.View.OnApplyWindowInsetsListener;
import android.view.WindowInsets;

/* renamed from: org.telegram.ui.SecretMediaViewer$$Lambda$0 */
final /* synthetic */ class SecretMediaViewer$$Lambda$0 implements OnApplyWindowInsetsListener {
    private final SecretMediaViewer arg$1;

    SecretMediaViewer$$Lambda$0(SecretMediaViewer secretMediaViewer) {
        this.arg$1 = secretMediaViewer;
    }

    public WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
        return this.arg$1.lambda$setParentActivity$0$SecretMediaViewer(view, windowInsets);
    }
}
