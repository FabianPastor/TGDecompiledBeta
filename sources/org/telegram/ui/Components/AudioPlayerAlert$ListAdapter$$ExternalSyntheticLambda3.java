package org.telegram.ui.Components;

import java.util.ArrayList;
import org.telegram.ui.Components.AudioPlayerAlert;

public final /* synthetic */ class AudioPlayerAlert$ListAdapter$$ExternalSyntheticLambda3 implements Runnable {
    public final /* synthetic */ AudioPlayerAlert.ListAdapter f$0;
    public final /* synthetic */ ArrayList f$1;
    public final /* synthetic */ String f$2;

    public /* synthetic */ AudioPlayerAlert$ListAdapter$$ExternalSyntheticLambda3(AudioPlayerAlert.ListAdapter listAdapter, ArrayList arrayList, String str) {
        this.f$0 = listAdapter;
        this.f$1 = arrayList;
        this.f$2 = str;
    }

    public final void run() {
        this.f$0.lambda$updateSearchResults$3(this.f$1, this.f$2);
    }
}
