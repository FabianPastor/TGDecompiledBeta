package org.telegram.ui;

import org.telegram.ui.Cells.BotHelpCell.BotHelpCellDelegate;
import org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter;

final /* synthetic */ class ChannelAdminLogActivity$ChatActivityAdapter$$Lambda$0 implements BotHelpCellDelegate {
    private final ChatActivityAdapter arg$1;

    ChannelAdminLogActivity$ChatActivityAdapter$$Lambda$0(ChatActivityAdapter chatActivityAdapter) {
        this.arg$1 = chatActivityAdapter;
    }

    public void didPressUrl(String str) {
        this.arg$1.lambda$onCreateViewHolder$0$ChannelAdminLogActivity$ChatActivityAdapter(str);
    }
}
