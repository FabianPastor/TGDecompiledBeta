package org.telegram.p005ui;

import org.telegram.p005ui.Cells.BotHelpCell.BotHelpCellDelegate;
import org.telegram.p005ui.ChatActivity.ChatActivityAdapter;

/* renamed from: org.telegram.ui.ChatActivity$ChatActivityAdapter$$Lambda$0 */
final /* synthetic */ class ChatActivity$ChatActivityAdapter$$Lambda$0 implements BotHelpCellDelegate {
    private final ChatActivityAdapter arg$1;

    ChatActivity$ChatActivityAdapter$$Lambda$0(ChatActivityAdapter chatActivityAdapter) {
        this.arg$1 = chatActivityAdapter;
    }

    public void didPressUrl(String str) {
        this.arg$1.lambda$onCreateViewHolder$0$ChatActivity$ChatActivityAdapter(str);
    }
}
