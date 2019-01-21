package org.telegram.ui;

import org.telegram.ui.Cells.ManageChatUserCell;
import org.telegram.ui.Cells.ManageChatUserCell.ManageChatUserCellDelegate;

final /* synthetic */ class ChatUsersActivity$SearchAdapter$$Lambda$2 implements ManageChatUserCellDelegate {
    private final SearchAdapter arg$1;

    ChatUsersActivity$SearchAdapter$$Lambda$2(SearchAdapter searchAdapter) {
        this.arg$1 = searchAdapter;
    }

    public boolean onOptionsButtonCheck(ManageChatUserCell manageChatUserCell, boolean z) {
        return this.arg$1.lambda$onCreateViewHolder$3$ChatUsersActivity$SearchAdapter(manageChatUserCell, z);
    }
}
