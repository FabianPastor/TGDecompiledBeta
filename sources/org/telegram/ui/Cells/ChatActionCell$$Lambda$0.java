package org.telegram.ui.Cells;

final /* synthetic */ class ChatActionCell$$Lambda$0 implements Runnable {
    private final ChatActionCell arg$1;

    ChatActionCell$$Lambda$0(ChatActionCell chatActionCell) {
        this.arg$1 = chatActionCell;
    }

    public void run() {
        this.arg$1.requestLayout();
    }
}
