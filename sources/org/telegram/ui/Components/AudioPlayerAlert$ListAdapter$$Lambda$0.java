package org.telegram.ui.Components;

final /* synthetic */ class AudioPlayerAlert$ListAdapter$$Lambda$0 implements Runnable {
    private final ListAdapter arg$1;
    private final String arg$2;

    AudioPlayerAlert$ListAdapter$$Lambda$0(ListAdapter listAdapter, String str) {
        this.arg$1 = listAdapter;
        this.arg$2 = str;
    }

    public void run() {
        this.arg$1.lambda$processSearch$1$AudioPlayerAlert$ListAdapter(this.arg$2);
    }
}
