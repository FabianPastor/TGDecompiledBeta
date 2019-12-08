package org.telegram.ui;

import org.telegram.tgnet.TLRPC.Document;
import org.telegram.ui.Components.AlertsCreator.ScheduleDatePickerDelegate;
import org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ContentPreviewViewer$1$VeEKHdYoA5-UzCB0iok2LPLcTCo implements ScheduleDatePickerDelegate {
    private final /* synthetic */ ContentPreviewViewerDelegate f$0;
    private final /* synthetic */ Document f$1;
    private final /* synthetic */ Object f$2;

    public /* synthetic */ -$$Lambda$ContentPreviewViewer$1$VeEKHdYoA5-UzCB0iok2LPLcTCo(ContentPreviewViewerDelegate contentPreviewViewerDelegate, Document document, Object obj) {
        this.f$0 = contentPreviewViewerDelegate;
        this.f$1 = document;
        this.f$2 = obj;
    }

    public final void didSelectDate(boolean z, int i) {
        this.f$0.sendSticker(this.f$1, this.f$2, z, i);
    }
}
