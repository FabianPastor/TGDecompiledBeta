package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.Components.PhonebookSelectShareAlert.PhonebookShareAlertDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LaunchActivity$9anx_tgfTio4iIoi7gzkQJJpir4 implements PhonebookShareAlertDelegate {
    private final /* synthetic */ LaunchActivity f$0;
    private final /* synthetic */ ChatActivity f$1;
    private final /* synthetic */ ArrayList f$2;
    private final /* synthetic */ int f$3;

    public /* synthetic */ -$$Lambda$LaunchActivity$9anx_tgfTio4iIoi7gzkQJJpir4(LaunchActivity launchActivity, ChatActivity chatActivity, ArrayList arrayList, int i) {
        this.f$0 = launchActivity;
        this.f$1 = chatActivity;
        this.f$2 = arrayList;
        this.f$3 = i;
    }

    public final void didSelectContact(User user, boolean z, int i) {
        this.f$0.lambda$didSelectDialogs$43$LaunchActivity(this.f$1, this.f$2, this.f$3, user, z, i);
    }
}
