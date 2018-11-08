package org.telegram.p005ui.Components;

import java.util.ArrayList;
import org.telegram.p005ui.Components.AudioPlayerAlert.ListAdapter;

/* renamed from: org.telegram.ui.Components.AudioPlayerAlert$ListAdapter$$Lambda$1 */
final /* synthetic */ class AudioPlayerAlert$ListAdapter$$Lambda$1 implements Runnable {
    private final ListAdapter arg$1;
    private final ArrayList arg$2;

    AudioPlayerAlert$ListAdapter$$Lambda$1(ListAdapter listAdapter, ArrayList arrayList) {
        this.arg$1 = listAdapter;
        this.arg$2 = arrayList;
    }

    public void run() {
        this.arg$1.lambda$updateSearchResults$2$AudioPlayerAlert$ListAdapter(this.arg$2);
    }
}
