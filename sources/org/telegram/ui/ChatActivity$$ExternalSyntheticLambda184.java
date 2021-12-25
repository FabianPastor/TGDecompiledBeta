package org.telegram.ui;

import android.view.MotionEvent;
import org.telegram.ui.Components.RecyclerListView;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda184 implements RecyclerListView.OnInterceptTouchListener {
    public static final /* synthetic */ ChatActivity$$ExternalSyntheticLambda184 INSTANCE = new ChatActivity$$ExternalSyntheticLambda184();

    private /* synthetic */ ChatActivity$$ExternalSyntheticLambda184() {
    }

    public final boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return ChatActivity.lambda$showChatThemeBottomSheet$184(motionEvent);
    }
}
