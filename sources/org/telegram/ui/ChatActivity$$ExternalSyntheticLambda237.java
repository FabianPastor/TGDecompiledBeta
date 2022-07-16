package org.telegram.ui;

import android.net.Uri;
import org.telegram.ui.Components.AlertsCreator;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda237 implements AlertsCreator.ScheduleDatePickerDelegate {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ Uri f$1;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda237(ChatActivity chatActivity, Uri uri) {
        this.f$0 = chatActivity;
        this.f$1 = uri;
    }

    public final void didSelectDate(boolean z, int i) {
        this.f$0.lambda$onActivityResultFragment$115(this.f$1, z, i);
    }
}
