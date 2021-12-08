package org.telegram.ui.Components;

import android.view.View;
import android.view.WindowManager;
import org.telegram.ui.Components.GroupCallPip;

public final /* synthetic */ class GroupCallPip$9$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ View f$0;
    public final /* synthetic */ View f$1;
    public final /* synthetic */ WindowManager f$2;
    public final /* synthetic */ View f$3;
    public final /* synthetic */ View f$4;

    public /* synthetic */ GroupCallPip$9$$ExternalSyntheticLambda0(View view, View view2, WindowManager windowManager, View view3, View view4) {
        this.f$0 = view;
        this.f$1 = view2;
        this.f$2 = windowManager;
        this.f$3 = view3;
        this.f$4 = view4;
    }

    public final void run() {
        GroupCallPip.AnonymousClass9.lambda$onAnimationEnd$0(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
