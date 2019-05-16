package org.telegram.ui;

import org.telegram.ui.Cells.ManageChatUserCell;
import org.telegram.ui.Cells.ManageChatUserCell.ManageChatUserCellDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatUsersActivity$SearchAdapter$a-9pjhx3qaGVfHCKv5RFU4cCrRc implements ManageChatUserCellDelegate {
    private final /* synthetic */ SearchAdapter f$0;

    public /* synthetic */ -$$Lambda$ChatUsersActivity$SearchAdapter$a-9pjhx3qaGVfHCKv5RFU4cCrRc(SearchAdapter searchAdapter) {
        this.f$0 = searchAdapter;
    }

    public final boolean onOptionsButtonCheck(ManageChatUserCell manageChatUserCell, boolean z) {
        return this.f$0.lambda$onCreateViewHolder$3$ChatUsersActivity$SearchAdapter(manageChatUserCell, z);
    }
}
