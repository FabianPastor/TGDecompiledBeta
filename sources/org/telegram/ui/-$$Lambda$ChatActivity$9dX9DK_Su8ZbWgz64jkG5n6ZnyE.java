package org.telegram.ui;

import android.net.Uri;
import org.telegram.ui.Components.AlertsCreator.ScheduleDatePickerDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivity$9dX9DK_Su8ZbWgz64jkG5n6ZnyE implements ScheduleDatePickerDelegate {
    private final /* synthetic */ ChatActivity f$0;
    private final /* synthetic */ Uri f$1;

    public /* synthetic */ -$$Lambda$ChatActivity$9dX9DK_Su8ZbWgz64jkG5n6ZnyE(ChatActivity chatActivity, Uri uri) {
        this.f$0 = chatActivity;
        this.f$1 = uri;
    }

    public final void didSelectDate(boolean z, int i) {
        this.f$0.lambda$onActivityResultFragment$61$ChatActivity(this.f$1, z, i);
    }
}
