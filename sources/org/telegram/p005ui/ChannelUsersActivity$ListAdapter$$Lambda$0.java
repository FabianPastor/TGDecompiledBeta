package org.telegram.p005ui;

import org.telegram.p005ui.Cells.ManageChatUserCell;
import org.telegram.p005ui.Cells.ManageChatUserCell.ManageChatUserCellDelegate;
import org.telegram.p005ui.ChannelUsersActivity.ListAdapter;

/* renamed from: org.telegram.ui.ChannelUsersActivity$ListAdapter$$Lambda$0 */
final /* synthetic */ class ChannelUsersActivity$ListAdapter$$Lambda$0 implements ManageChatUserCellDelegate {
    private final ListAdapter arg$1;

    ChannelUsersActivity$ListAdapter$$Lambda$0(ListAdapter listAdapter) {
        this.arg$1 = listAdapter;
    }

    public boolean onOptionsButtonCheck(ManageChatUserCell manageChatUserCell, boolean z) {
        return this.arg$1.lambda$onCreateViewHolder$0$ChannelUsersActivity$ListAdapter(manageChatUserCell, z);
    }
}
