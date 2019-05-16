package org.telegram.ui.Components;

import java.util.ArrayList;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.DialogsActivity.DialogsActivityDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AudioPlayerAlert$bZwv44or3-083Y0MYIMNRo1ORcQ implements DialogsActivityDelegate {
    private final /* synthetic */ AudioPlayerAlert f$0;
    private final /* synthetic */ ArrayList f$1;

    public /* synthetic */ -$$Lambda$AudioPlayerAlert$bZwv44or3-083Y0MYIMNRo1ORcQ(AudioPlayerAlert audioPlayerAlert, ArrayList arrayList) {
        this.f$0 = audioPlayerAlert;
        this.f$1 = arrayList;
    }

    public final void didSelectDialogs(DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
        this.f$0.lambda$onSubItemClick$10$AudioPlayerAlert(this.f$1, dialogsActivity, arrayList, charSequence, z);
    }
}
