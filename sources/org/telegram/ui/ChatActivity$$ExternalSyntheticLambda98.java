package org.telegram.ui;

import android.view.MotionEvent;
import android.view.View;
import org.telegram.ui.ContentPreviewViewer;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda98 implements View.OnTouchListener {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ ContentPreviewViewer.ContentPreviewViewerDelegate f$1;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda98(ChatActivity chatActivity, ContentPreviewViewer.ContentPreviewViewerDelegate contentPreviewViewerDelegate) {
        this.f$0 = chatActivity;
        this.f$1 = contentPreviewViewerDelegate;
    }

    public final boolean onTouch(View view, MotionEvent motionEvent) {
        return this.f$0.lambda$createView$61(this.f$1, view, motionEvent);
    }
}
