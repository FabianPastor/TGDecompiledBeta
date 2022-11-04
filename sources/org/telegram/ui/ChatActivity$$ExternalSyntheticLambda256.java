package org.telegram.ui;

import android.view.MotionEvent;
import org.telegram.ui.Components.RecyclerListView;
/* loaded from: classes3.dex */
public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda256 implements RecyclerListView.OnInterceptTouchListener {
    public static final /* synthetic */ ChatActivity$$ExternalSyntheticLambda256 INSTANCE = new ChatActivity$$ExternalSyntheticLambda256();

    private /* synthetic */ ChatActivity$$ExternalSyntheticLambda256() {
    }

    @Override // org.telegram.ui.Components.RecyclerListView.OnInterceptTouchListener
    public final boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        boolean lambda$showChatThemeBottomSheet$256;
        lambda$showChatThemeBottomSheet$256 = ChatActivity.lambda$showChatThemeBottomSheet$256(motionEvent);
        return lambda$showChatThemeBottomSheet$256;
    }
}
