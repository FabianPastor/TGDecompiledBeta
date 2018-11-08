package org.telegram.p005ui;

import org.telegram.p005ui.Cells.ManageChatUserCell;
import org.telegram.p005ui.Cells.ManageChatUserCell.ManageChatUserCellDelegate;
import org.telegram.p005ui.ChannelUsersActivity.SearchAdapter;

/* renamed from: org.telegram.ui.ChannelUsersActivity$SearchAdapter$$Lambda$2 */
final /* synthetic */ class ChannelUsersActivity$SearchAdapter$$Lambda$2 implements ManageChatUserCellDelegate {
    private final SearchAdapter arg$1;

    ChannelUsersActivity$SearchAdapter$$Lambda$2(SearchAdapter searchAdapter) {
        this.arg$1 = searchAdapter;
    }

    public boolean onOptionsButtonCheck(ManageChatUserCell manageChatUserCell, boolean z) {
        return this.arg$1.lambda$onCreateViewHolder$3$ChannelUsersActivity$SearchAdapter(manageChatUserCell, z);
    }
}
