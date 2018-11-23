package org.telegram.p005ui.Cells;

/* renamed from: org.telegram.ui.Cells.ChatActionCell$$Lambda$0 */
final /* synthetic */ class ChatActionCell$$Lambda$0 implements Runnable {
    private final ChatActionCell arg$1;

    ChatActionCell$$Lambda$0(ChatActionCell chatActionCell) {
        this.arg$1 = chatActionCell;
    }

    public void run() {
        this.arg$1.requestLayout();
    }
}
