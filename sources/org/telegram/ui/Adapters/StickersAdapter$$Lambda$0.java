package org.telegram.ui.Adapters;

final /* synthetic */ class StickersAdapter$$Lambda$0 implements Runnable {
    private final StickersAdapter arg$1;
    private final String arg$2;

    StickersAdapter$$Lambda$0(StickersAdapter stickersAdapter, String str) {
        this.arg$1 = stickersAdapter;
        this.arg$2 = str;
    }

    public void run() {
        this.arg$1.lambda$searchEmojiByKeyword$1$StickersAdapter(this.arg$2);
    }
}
