package org.telegram.p005ui.Cells;

import java.util.Comparator;
import org.telegram.p005ui.Cells.ChatMessageCell.PollButton;

/* renamed from: org.telegram.ui.Cells.ChatMessageCell$$Lambda$0 */
final /* synthetic */ class ChatMessageCell$$Lambda$0 implements Comparator {
    static final Comparator $instance = new ChatMessageCell$$Lambda$0();

    private ChatMessageCell$$Lambda$0() {
    }

    public int compare(Object obj, Object obj2) {
        return ChatMessageCell.lambda$setMessageObject$0$ChatMessageCell((PollButton) obj, (PollButton) obj2);
    }
}
