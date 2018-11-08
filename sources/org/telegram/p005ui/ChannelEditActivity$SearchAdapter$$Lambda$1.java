package org.telegram.p005ui;

import org.telegram.p005ui.Cells.ManageChatUserCell;
import org.telegram.p005ui.Cells.ManageChatUserCell.ManageChatUserCellDelegate;
import org.telegram.p005ui.ChannelEditActivity.SearchAdapter;

/* renamed from: org.telegram.ui.ChannelEditActivity$SearchAdapter$$Lambda$1 */
final /* synthetic */ class ChannelEditActivity$SearchAdapter$$Lambda$1 implements ManageChatUserCellDelegate {
    private final SearchAdapter arg$1;

    ChannelEditActivity$SearchAdapter$$Lambda$1(SearchAdapter searchAdapter) {
        this.arg$1 = searchAdapter;
    }

    public boolean onOptionsButtonCheck(ManageChatUserCell manageChatUserCell, boolean z) {
        return this.arg$1.lambda$onCreateViewHolder$1$ChannelEditActivity$SearchAdapter(manageChatUserCell, z);
    }
}
