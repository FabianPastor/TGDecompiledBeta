package org.telegram.ui.Components;

import android.view.MotionEvent;
import org.telegram.messenger.IMapsProvider;

public final /* synthetic */ class ChatAttachAlertLocationLayout$$ExternalSyntheticLambda17 implements IMapsProvider.ITouchInterceptor {
    public final /* synthetic */ ChatAttachAlertLocationLayout f$0;

    public /* synthetic */ ChatAttachAlertLocationLayout$$ExternalSyntheticLambda17(ChatAttachAlertLocationLayout chatAttachAlertLocationLayout) {
        this.f$0 = chatAttachAlertLocationLayout;
    }

    public final boolean onInterceptTouchEvent(MotionEvent motionEvent, IMapsProvider.ICallableMethod iCallableMethod) {
        return this.f$0.lambda$new$9(motionEvent, iCallableMethod);
    }
}
