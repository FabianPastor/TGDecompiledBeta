package org.telegram.ui.Components;

import org.telegram.ui.Cells.PhotoAttachPhotoCell;
import org.telegram.ui.Cells.PhotoAttachPhotoCell.PhotoAttachPhotoCellDelegate;

final /* synthetic */ class ChatAttachAlert$PhotoAttachAdapter$$Lambda$0 implements PhotoAttachPhotoCellDelegate {
    private final PhotoAttachAdapter arg$1;

    ChatAttachAlert$PhotoAttachAdapter$$Lambda$0(PhotoAttachAdapter photoAttachAdapter) {
        this.arg$1 = photoAttachAdapter;
    }

    public void onCheckClick(PhotoAttachPhotoCell photoAttachPhotoCell) {
        this.arg$1.lambda$createHolder$0$ChatAttachAlert$PhotoAttachAdapter(photoAttachPhotoCell);
    }
}
