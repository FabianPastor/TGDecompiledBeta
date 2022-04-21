package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.ChatAttachAlertContactsLayout;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda85 implements ChatAttachAlertContactsLayout.PhonebookShareAlertDelegate {
    public final /* synthetic */ LaunchActivity f$0;
    public final /* synthetic */ ChatActivity f$1;
    public final /* synthetic */ ArrayList f$2;
    public final /* synthetic */ int f$3;
    public final /* synthetic */ CharSequence f$4;
    public final /* synthetic */ boolean f$5;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda85(LaunchActivity launchActivity, ChatActivity chatActivity, ArrayList arrayList, int i, CharSequence charSequence, boolean z) {
        this.f$0 = launchActivity;
        this.f$1 = chatActivity;
        this.f$2 = arrayList;
        this.f$3 = i;
        this.f$4 = charSequence;
        this.f$5 = z;
    }

    public final void didSelectContact(TLRPC.User user, boolean z, int i) {
        this.f$0.m2326lambda$didSelectDialogs$72$orgtelegramuiLaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, user, z, i);
    }
}
