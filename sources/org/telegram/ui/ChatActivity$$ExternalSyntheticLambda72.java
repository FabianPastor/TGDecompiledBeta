package org.telegram.ui;

import android.net.Uri;
import org.telegram.ui.Components.AlertsCreator;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda72 implements AlertsCreator.ScheduleDatePickerDelegate {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ Uri f$1;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda72(ChatActivity chatActivity, Uri uri) {
        this.f$0 = chatActivity;
        this.f$1 = uri;
    }

    public final void didSelectDate(boolean z, int i) {
        this.f$0.m1740lambda$onActivityResultFragment$93$orgtelegramuiChatActivity(this.f$1, z, i);
    }
}
