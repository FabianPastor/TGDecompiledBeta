package org.telegram.ui;

import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.ContentPreviewViewer;

public final /* synthetic */ class ContentPreviewViewer$1$$ExternalSyntheticLambda4 implements AlertsCreator.ScheduleDatePickerDelegate {
    public final /* synthetic */ ContentPreviewViewer.ContentPreviewViewerDelegate f$0;
    public final /* synthetic */ TLRPC.Document f$1;
    public final /* synthetic */ String f$2;
    public final /* synthetic */ Object f$3;

    public /* synthetic */ ContentPreviewViewer$1$$ExternalSyntheticLambda4(ContentPreviewViewer.ContentPreviewViewerDelegate contentPreviewViewerDelegate, TLRPC.Document document, String str, Object obj) {
        this.f$0 = contentPreviewViewerDelegate;
        this.f$1 = document;
        this.f$2 = str;
        this.f$3 = obj;
    }

    public final void didSelectDate(boolean z, int i) {
        this.f$0.sendSticker(this.f$1, this.f$2, this.f$3, z, i);
    }
}
