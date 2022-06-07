package org.telegram.ui;

import android.net.Uri;
import org.telegram.ui.Components.AlertsCreator;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda233 implements AlertsCreator.ScheduleDatePickerDelegate {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ Uri f$1;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda233(ChatActivity chatActivity, Uri uri) {
        this.f$0 = chatActivity;
        this.f$1 = uri;
    }

    public final void didSelectDate(boolean z, int i) {
        this.f$0.lambda$onActivityResultFragment$114(this.f$1, z, i);
    }
}
