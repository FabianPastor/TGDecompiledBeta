package org.telegram.ui;

import android.view.MotionEvent;
import org.telegram.ui.Components.RecyclerListView;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda249 implements RecyclerListView.OnInterceptTouchListener {
    public static final /* synthetic */ ChatActivity$$ExternalSyntheticLambda249 INSTANCE = new ChatActivity$$ExternalSyntheticLambda249();

    private /* synthetic */ ChatActivity$$ExternalSyntheticLambda249() {
    }

    public final boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return ChatActivity.lambda$showChatThemeBottomSheet$250(motionEvent);
    }
}
