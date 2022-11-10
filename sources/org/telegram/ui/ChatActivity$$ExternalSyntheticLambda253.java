package org.telegram.ui;

import android.view.MotionEvent;
import org.telegram.ui.Components.RecyclerListView;
/* loaded from: classes3.dex */
public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda253 implements RecyclerListView.OnInterceptTouchListener {
    public static final /* synthetic */ ChatActivity$$ExternalSyntheticLambda253 INSTANCE = new ChatActivity$$ExternalSyntheticLambda253();

    private /* synthetic */ ChatActivity$$ExternalSyntheticLambda253() {
    }

    @Override // org.telegram.ui.Components.RecyclerListView.OnInterceptTouchListener
    public final boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        boolean lambda$showChatThemeBottomSheet$253;
        lambda$showChatThemeBottomSheet$253 = ChatActivity.lambda$showChatThemeBottomSheet$253(motionEvent);
        return lambda$showChatThemeBottomSheet$253;
    }
}
