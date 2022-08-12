package org.telegram.ui;

import android.view.MotionEvent;
import org.telegram.messenger.IMapsProvider;

public final /* synthetic */ class LocationActivity$$ExternalSyntheticLambda28 implements IMapsProvider.ITouchInterceptor {
    public final /* synthetic */ LocationActivity f$0;

    public /* synthetic */ LocationActivity$$ExternalSyntheticLambda28(LocationActivity locationActivity) {
        this.f$0 = locationActivity;
    }

    public final boolean onInterceptTouchEvent(MotionEvent motionEvent, IMapsProvider.ICallableMethod iCallableMethod) {
        return this.f$0.lambda$createView$15(motionEvent, iCallableMethod);
    }
}
