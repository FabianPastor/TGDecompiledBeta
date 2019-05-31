package org.telegram.ui;

import org.telegram.ui.Cells.ManageChatUserCell;
import org.telegram.ui.Cells.ManageChatUserCell.ManageChatUserCellDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatUsersActivity$SearchAdapter$fSIulZwDi8NAXDLh6m3-GQxlD8U implements ManageChatUserCellDelegate {
    private final /* synthetic */ SearchAdapter f$0;

    public /* synthetic */ -$$Lambda$ChatUsersActivity$SearchAdapter$fSIulZwDi8NAXDLh6m3-GQxlD8U(SearchAdapter searchAdapter) {
        this.f$0 = searchAdapter;
    }

    public final boolean onOptionsButtonCheck(ManageChatUserCell manageChatUserCell, boolean z) {
        return this.f$0.lambda$onCreateViewHolder$4$ChatUsersActivity$SearchAdapter(manageChatUserCell, z);
    }
}
