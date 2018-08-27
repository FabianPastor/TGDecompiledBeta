package org.telegram.ui;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import org.telegram.ui.StickerPreviewViewer.StickerPreviewViewerDelegate;

final /* synthetic */ class ChatActivity$$Lambda$20 implements OnTouchListener {
    private final ChatActivity arg$1;
    private final StickerPreviewViewerDelegate arg$2;

    ChatActivity$$Lambda$20(ChatActivity chatActivity, StickerPreviewViewerDelegate stickerPreviewViewerDelegate) {
        this.arg$1 = chatActivity;
        this.arg$2 = stickerPreviewViewerDelegate;
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        return this.arg$1.lambda$createView$23$ChatActivity(this.arg$2, view, motionEvent);
    }
}
