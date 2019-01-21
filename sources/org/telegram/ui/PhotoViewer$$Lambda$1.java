package org.telegram.ui;

import android.view.View;
import android.view.View.OnApplyWindowInsetsListener;
import android.view.WindowInsets;

final /* synthetic */ class PhotoViewer$$Lambda$1 implements OnApplyWindowInsetsListener {
    private final PhotoViewer arg$1;

    PhotoViewer$$Lambda$1(PhotoViewer photoViewer) {
        this.arg$1 = photoViewer;
    }

    public WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
        return this.arg$1.lambda$setParentActivity$1$PhotoViewer(view, windowInsets);
    }
}
