package org.telegram.ui;

import org.telegram.ui.Cells.ManageChatUserCell;
import org.telegram.ui.Cells.ManageChatUserCell.ManageChatUserCellDelegate;

final /* synthetic */ class ChatUsersActivity$ListAdapter$$Lambda$0 implements ManageChatUserCellDelegate {
    private final ListAdapter arg$1;

    ChatUsersActivity$ListAdapter$$Lambda$0(ListAdapter listAdapter) {
        this.arg$1 = listAdapter;
    }

    public boolean onOptionsButtonCheck(ManageChatUserCell manageChatUserCell, boolean z) {
        return this.arg$1.lambda$onCreateViewHolder$0$ChatUsersActivity$ListAdapter(manageChatUserCell, z);
    }
}
