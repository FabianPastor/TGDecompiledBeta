package org.telegram.ui;

import android.view.View;
import android.view.View.OnApplyWindowInsetsListener;
import android.view.WindowInsets;

final /* synthetic */ class ContentPreviewViewer$$Lambda$2 implements OnApplyWindowInsetsListener {
    private final ContentPreviewViewer arg$1;

    ContentPreviewViewer$$Lambda$2(ContentPreviewViewer contentPreviewViewer) {
        this.arg$1 = contentPreviewViewer;
    }

    public WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
        return this.arg$1.lambda$setParentActivity$2$ContentPreviewViewer(view, windowInsets);
    }
}
