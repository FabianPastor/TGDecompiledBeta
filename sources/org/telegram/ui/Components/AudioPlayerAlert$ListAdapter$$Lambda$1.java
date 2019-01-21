package org.telegram.ui.Components;

import java.util.ArrayList;

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
