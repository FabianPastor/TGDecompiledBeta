package org.telegram.p005ui.Components;

import java.util.ArrayList;
import org.telegram.p005ui.Components.AudioPlayerAlert.ListAdapter;

/* renamed from: org.telegram.ui.Components.AudioPlayerAlert$ListAdapter$$Lambda$2 */
final /* synthetic */ class AudioPlayerAlert$ListAdapter$$Lambda$2 implements Runnable {
    private final ListAdapter arg$1;
    private final String arg$2;
    private final ArrayList arg$3;

    AudioPlayerAlert$ListAdapter$$Lambda$2(ListAdapter listAdapter, String str, ArrayList arrayList) {
        this.arg$1 = listAdapter;
        this.arg$2 = str;
        this.arg$3 = arrayList;
    }

    public void run() {
        this.arg$1.lambda$null$0$AudioPlayerAlert$ListAdapter(this.arg$2, this.arg$3);
    }
}
