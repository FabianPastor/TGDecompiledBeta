package org.telegram.p005ui.Components;

import org.telegram.p005ui.Cells.PhotoAttachPhotoCell;
import org.telegram.p005ui.Cells.PhotoAttachPhotoCell.PhotoAttachPhotoCellDelegate;
import org.telegram.p005ui.Components.ChatAttachAlert.PhotoAttachAdapter;

/* renamed from: org.telegram.ui.Components.ChatAttachAlert$PhotoAttachAdapter$$Lambda$0 */
final /* synthetic */ class ChatAttachAlert$PhotoAttachAdapter$$Lambda$0 implements PhotoAttachPhotoCellDelegate {
    private final PhotoAttachAdapter arg$1;

    ChatAttachAlert$PhotoAttachAdapter$$Lambda$0(PhotoAttachAdapter photoAttachAdapter) {
        this.arg$1 = photoAttachAdapter;
    }

    public void onCheckClick(PhotoAttachPhotoCell photoAttachPhotoCell) {
        this.arg$1.lambda$createHolder$0$ChatAttachAlert$PhotoAttachAdapter(photoAttachPhotoCell);
    }
}
