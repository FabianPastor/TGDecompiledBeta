package org.telegram.ui;

import org.telegram.messenger.MessageObject;
import org.telegram.ui.Cells.AudioCell.AudioCellDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AudioSelectActivity$ListAdapter$_RHioXAj8YlHkv8lhkCbfXzL_JQ implements AudioCellDelegate {
    private final /* synthetic */ ListAdapter f$0;

    public /* synthetic */ -$$Lambda$AudioSelectActivity$ListAdapter$_RHioXAj8YlHkv8lhkCbfXzL_JQ(ListAdapter listAdapter) {
        this.f$0 = listAdapter;
    }

    public final void startedPlayingAudio(MessageObject messageObject) {
        this.f$0.lambda$onCreateViewHolder$0$AudioSelectActivity$ListAdapter(messageObject);
    }
}
