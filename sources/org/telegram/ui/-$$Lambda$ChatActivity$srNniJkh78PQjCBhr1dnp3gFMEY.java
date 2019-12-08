package org.telegram.ui;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivity$srNniJkh78PQjCBhr1dnp3gFMEY implements OnTouchListener {
    private final /* synthetic */ ChatActivity f$0;
    private final /* synthetic */ ContentPreviewViewerDelegate f$1;

    public /* synthetic */ -$$Lambda$ChatActivity$srNniJkh78PQjCBhr1dnp3gFMEY(ChatActivity chatActivity, ContentPreviewViewerDelegate contentPreviewViewerDelegate) {
        this.f$0 = chatActivity;
        this.f$1 = contentPreviewViewerDelegate;
    }

    public final boolean onTouch(View view, MotionEvent motionEvent) {
        return this.f$0.lambda$createView$28$ChatActivity(this.f$1, view, motionEvent);
    }
}
