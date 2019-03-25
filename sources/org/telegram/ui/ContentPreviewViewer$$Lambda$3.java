package org.telegram.ui;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

final /* synthetic */ class ContentPreviewViewer$$Lambda$3 implements OnTouchListener {
    private final ContentPreviewViewer arg$1;

    ContentPreviewViewer$$Lambda$3(ContentPreviewViewer contentPreviewViewer) {
        this.arg$1 = contentPreviewViewer;
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        return this.arg$1.lambda$setParentActivity$3$ContentPreviewViewer(view, motionEvent);
    }
}
