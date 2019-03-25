package org.telegram.ui;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate;

final /* synthetic */ class ChatActivity$$Lambda$20 implements OnTouchListener {
    private final ChatActivity arg$1;
    private final ContentPreviewViewerDelegate arg$2;

    ChatActivity$$Lambda$20(ChatActivity chatActivity, ContentPreviewViewerDelegate contentPreviewViewerDelegate) {
        this.arg$1 = chatActivity;
        this.arg$2 = contentPreviewViewerDelegate;
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        return this.arg$1.lambda$createView$23$ChatActivity(this.arg$2, view, motionEvent);
    }
}
