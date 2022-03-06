package org.telegram.ui.Components;

import org.telegram.ui.Components.ChatAttachAlertPhotoLayoutPreview;

public final /* synthetic */ class ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$$ExternalSyntheticLambda3 implements Runnable {
    public final /* synthetic */ ChatAttachAlertPhotoLayoutPreview.PreviewGroupsView f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ long f$2;
    public final /* synthetic */ ChatAttachAlertPhotoLayoutPreview.PreviewGroupsView.PreviewGroupCell.MediaCell f$3;

    public /* synthetic */ ChatAttachAlertPhotoLayoutPreview$PreviewGroupsView$$ExternalSyntheticLambda3(ChatAttachAlertPhotoLayoutPreview.PreviewGroupsView previewGroupsView, int i, long j, ChatAttachAlertPhotoLayoutPreview.PreviewGroupsView.PreviewGroupCell.MediaCell mediaCell) {
        this.f$0 = previewGroupsView;
        this.f$1 = i;
        this.f$2 = j;
        this.f$3 = mediaCell;
    }

    public final void run() {
        this.f$0.lambda$onTouchEvent$2(this.f$1, this.f$2, this.f$3);
    }
}
