package org.telegram.ui;

import org.telegram.ui.Cells.ManageChatUserCell;
import org.telegram.ui.Cells.ManageChatUserCell.ManageChatUserCellDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatUsersActivity$ListAdapter$eixJJWW-1mDLHNoI-EEjSfsGLFc implements ManageChatUserCellDelegate {
    private final /* synthetic */ ListAdapter f$0;

    public /* synthetic */ -$$Lambda$ChatUsersActivity$ListAdapter$eixJJWW-1mDLHNoI-EEjSfsGLFc(ListAdapter listAdapter) {
        this.f$0 = listAdapter;
    }

    public final boolean onOptionsButtonCheck(ManageChatUserCell manageChatUserCell, boolean z) {
        return this.f$0.lambda$onCreateViewHolder$0$ChatUsersActivity$ListAdapter(manageChatUserCell, z);
    }
}
