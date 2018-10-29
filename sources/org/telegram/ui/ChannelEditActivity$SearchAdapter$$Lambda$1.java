package org.telegram.ui;

import org.telegram.ui.Cells.ManageChatUserCell;
import org.telegram.ui.Cells.ManageChatUserCell.ManageChatUserCellDelegate;

final /* synthetic */ class ChannelEditActivity$SearchAdapter$$Lambda$1 implements ManageChatUserCellDelegate {
    private final SearchAdapter arg$1;

    ChannelEditActivity$SearchAdapter$$Lambda$1(SearchAdapter searchAdapter) {
        this.arg$1 = searchAdapter;
    }

    public boolean onOptionsButtonCheck(ManageChatUserCell manageChatUserCell, boolean z) {
        return this.arg$1.lambda$onCreateViewHolder$1$ChannelEditActivity$SearchAdapter(manageChatUserCell, z);
    }
}
