package org.telegram.ui.Adapters;

import org.telegram.ui.Cells.ContextLinkCell;
import org.telegram.ui.Cells.ContextLinkCell.ContextLinkCellDelegate;

final /* synthetic */ class MentionsAdapter$$Lambda$6 implements ContextLinkCellDelegate {
    private final MentionsAdapter arg$1;

    MentionsAdapter$$Lambda$6(MentionsAdapter mentionsAdapter) {
        this.arg$1 = mentionsAdapter;
    }

    public void didPressedImage(ContextLinkCell contextLinkCell) {
        this.arg$1.lambda$onCreateViewHolder$7$MentionsAdapter(contextLinkCell);
    }
}
