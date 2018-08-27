package org.telegram.ui;

import org.telegram.ui.Cells.BotHelpCell.BotHelpCellDelegate;
import org.telegram.ui.ChatActivity.ChatActivityAdapter;

final /* synthetic */ class ChatActivity$ChatActivityAdapter$$Lambda$0 implements BotHelpCellDelegate {
    private final ChatActivityAdapter arg$1;

    ChatActivity$ChatActivityAdapter$$Lambda$0(ChatActivityAdapter chatActivityAdapter) {
        this.arg$1 = chatActivityAdapter;
    }

    public void didPressUrl(String str) {
        this.arg$1.lambda$onCreateViewHolder$0$ChatActivity$ChatActivityAdapter(str);
    }
}
