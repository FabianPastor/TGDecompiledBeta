package org.telegram.ui;

import android.view.MotionEvent;
import org.telegram.ui.Components.RecyclerListView;
/* loaded from: classes3.dex */
public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda249 implements RecyclerListView.OnInterceptTouchListener {
    public static final /* synthetic */ ChatActivity$$ExternalSyntheticLambda249 INSTANCE = new ChatActivity$$ExternalSyntheticLambda249();

    private /* synthetic */ ChatActivity$$ExternalSyntheticLambda249() {
    }

    @Override // org.telegram.ui.Components.RecyclerListView.OnInterceptTouchListener
    public final boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        boolean lambda$showChatThemeBottomSheet$250;
        lambda$showChatThemeBottomSheet$250 = ChatActivity.lambda$showChatThemeBottomSheet$250(motionEvent);
        return lambda$showChatThemeBottomSheet$250;
    }
}
