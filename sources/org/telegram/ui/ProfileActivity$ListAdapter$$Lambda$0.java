package org.telegram.ui;

import org.telegram.ui.Cells.AboutLinkCell.AboutLinkCellDelegate;

final /* synthetic */ class ProfileActivity$ListAdapter$$Lambda$0 implements AboutLinkCellDelegate {
    private final ListAdapter arg$1;

    ProfileActivity$ListAdapter$$Lambda$0(ListAdapter listAdapter) {
        this.arg$1 = listAdapter;
    }

    public void didPressUrl(String str) {
        this.arg$1.lambda$onCreateViewHolder$0$ProfileActivity$ListAdapter(str);
    }
}
