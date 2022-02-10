package org.telegram.ui;

import android.view.MotionEvent;
import org.telegram.ui.Components.RecyclerListView;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda219 implements RecyclerListView.OnInterceptTouchListener {
    public static final /* synthetic */ ChatActivity$$ExternalSyntheticLambda219 INSTANCE = new ChatActivity$$ExternalSyntheticLambda219();

    private /* synthetic */ ChatActivity$$ExternalSyntheticLambda219() {
    }

    public final boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return ChatActivity.lambda$showChatThemeBottomSheet$221(motionEvent);
    }
}
