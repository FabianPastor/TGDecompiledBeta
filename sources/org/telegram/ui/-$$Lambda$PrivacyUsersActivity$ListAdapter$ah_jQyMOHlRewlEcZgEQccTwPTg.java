package org.telegram.ui;

import org.telegram.ui.Cells.ManageChatUserCell;
import org.telegram.ui.Cells.ManageChatUserCell.ManageChatUserCellDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PrivacyUsersActivity$ListAdapter$ah_jQyMOHlRewlEcZgEQccTwPTg implements ManageChatUserCellDelegate {
    private final /* synthetic */ ListAdapter f$0;

    public /* synthetic */ -$$Lambda$PrivacyUsersActivity$ListAdapter$ah_jQyMOHlRewlEcZgEQccTwPTg(ListAdapter listAdapter) {
        this.f$0 = listAdapter;
    }

    public final boolean onOptionsButtonCheck(ManageChatUserCell manageChatUserCell, boolean z) {
        return this.f$0.lambda$onCreateViewHolder$0$PrivacyUsersActivity$ListAdapter(manageChatUserCell, z);
    }
}
