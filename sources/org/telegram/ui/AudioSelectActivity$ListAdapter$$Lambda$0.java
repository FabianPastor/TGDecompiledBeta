package org.telegram.ui;

import org.telegram.messenger.MessageObject;
import org.telegram.ui.Cells.AudioCell.AudioCellDelegate;

final /* synthetic */ class AudioSelectActivity$ListAdapter$$Lambda$0 implements AudioCellDelegate {
    private final ListAdapter arg$1;

    AudioSelectActivity$ListAdapter$$Lambda$0(ListAdapter listAdapter) {
        this.arg$1 = listAdapter;
    }

    public void startedPlayingAudio(MessageObject messageObject) {
        this.arg$1.lambda$onCreateViewHolder$0$AudioSelectActivity$ListAdapter(messageObject);
    }
}
