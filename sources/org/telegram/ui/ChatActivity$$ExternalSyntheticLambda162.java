package org.telegram.ui;

import android.view.MotionEvent;
import org.telegram.ui.Components.RecyclerListView;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda162 implements RecyclerListView.OnInterceptTouchListener {
    public static final /* synthetic */ ChatActivity$$ExternalSyntheticLambda162 INSTANCE = new ChatActivity$$ExternalSyntheticLambda162();

    private /* synthetic */ ChatActivity$$ExternalSyntheticLambda162() {
    }

    public final boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return ChatActivity.lambda$showChatThemeBottomSheet$248(motionEvent);
    }
}
