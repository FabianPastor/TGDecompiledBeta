package org.telegram.ui;

import android.view.MotionEvent;
import org.telegram.ui.Components.RecyclerListView;
/* loaded from: classes3.dex */
public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda255 implements RecyclerListView.OnInterceptTouchListener {
    public static final /* synthetic */ ChatActivity$$ExternalSyntheticLambda255 INSTANCE = new ChatActivity$$ExternalSyntheticLambda255();

    private /* synthetic */ ChatActivity$$ExternalSyntheticLambda255() {
    }

    @Override // org.telegram.ui.Components.RecyclerListView.OnInterceptTouchListener
    public final boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        boolean lambda$showChatThemeBottomSheet$255;
        lambda$showChatThemeBottomSheet$255 = ChatActivity.lambda$showChatThemeBottomSheet$255(motionEvent);
        return lambda$showChatThemeBottomSheet$255;
    }
}
