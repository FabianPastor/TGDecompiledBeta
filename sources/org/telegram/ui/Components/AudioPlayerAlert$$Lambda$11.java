package org.telegram.ui.Components;

import java.util.ArrayList;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.DialogsActivity.DialogsActivityDelegate;

final /* synthetic */ class AudioPlayerAlert$$Lambda$11 implements DialogsActivityDelegate {
    private final AudioPlayerAlert arg$1;
    private final ArrayList arg$2;

    AudioPlayerAlert$$Lambda$11(AudioPlayerAlert audioPlayerAlert, ArrayList arrayList) {
        this.arg$1 = audioPlayerAlert;
        this.arg$2 = arrayList;
    }

    public void didSelectDialogs(DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
        this.arg$1.lambda$onSubItemClick$10$AudioPlayerAlert(this.arg$2, dialogsActivity, arrayList, charSequence, z);
    }
}
