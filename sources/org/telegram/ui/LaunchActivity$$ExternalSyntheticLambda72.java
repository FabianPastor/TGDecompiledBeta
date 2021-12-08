package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.ChatAttachAlertContactsLayout;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda72 implements ChatAttachAlertContactsLayout.PhonebookShareAlertDelegate {
    public final /* synthetic */ LaunchActivity f$0;
    public final /* synthetic */ ChatActivity f$1;
    public final /* synthetic */ ArrayList f$2;
    public final /* synthetic */ int f$3;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda72(LaunchActivity launchActivity, ChatActivity chatActivity, ArrayList arrayList, int i) {
        this.f$0 = launchActivity;
        this.f$1 = chatActivity;
        this.f$2 = arrayList;
        this.f$3 = i;
    }

    public final void didSelectContact(TLRPC.User user, boolean z, int i) {
        this.f$0.m3075lambda$didSelectDialogs$60$orgtelegramuiLaunchActivity(this.f$1, this.f$2, this.f$3, user, z, i);
    }
}
