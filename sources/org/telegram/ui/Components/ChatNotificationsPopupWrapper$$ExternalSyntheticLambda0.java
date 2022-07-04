package org.telegram.ui.Components;

import android.content.Context;
import android.view.View;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.ChatNotificationsPopupWrapper;

public final /* synthetic */ class ChatNotificationsPopupWrapper$$ExternalSyntheticLambda0 implements View.OnClickListener {
    public final /* synthetic */ ChatNotificationsPopupWrapper f$0;
    public final /* synthetic */ Context f$1;
    public final /* synthetic */ Theme.ResourcesProvider f$2;
    public final /* synthetic */ int f$3;
    public final /* synthetic */ ChatNotificationsPopupWrapper.Callback f$4;

    public /* synthetic */ ChatNotificationsPopupWrapper$$ExternalSyntheticLambda0(ChatNotificationsPopupWrapper chatNotificationsPopupWrapper, Context context, Theme.ResourcesProvider resourcesProvider, int i, ChatNotificationsPopupWrapper.Callback callback) {
        this.f$0 = chatNotificationsPopupWrapper;
        this.f$1 = context;
        this.f$2 = resourcesProvider;
        this.f$3 = i;
        this.f$4 = callback;
    }

    public final void onClick(View view) {
        this.f$0.m869xCLASSNAMEeb4a5(this.f$1, this.f$2, this.f$3, this.f$4, view);
    }
}
