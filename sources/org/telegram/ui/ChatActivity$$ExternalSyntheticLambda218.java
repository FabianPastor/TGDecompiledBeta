package org.telegram.ui;

import android.view.MotionEvent;
import org.telegram.ui.Components.RecyclerListView;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda218 implements RecyclerListView.OnInterceptTouchListener {
    public static final /* synthetic */ ChatActivity$$ExternalSyntheticLambda218 INSTANCE = new ChatActivity$$ExternalSyntheticLambda218();

    private /* synthetic */ ChatActivity$$ExternalSyntheticLambda218() {
    }

    public final boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return ChatActivity.lambda$showChatThemeBottomSheet$220(motionEvent);
    }
}
