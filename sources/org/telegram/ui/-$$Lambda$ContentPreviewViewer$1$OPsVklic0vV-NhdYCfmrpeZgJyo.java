package org.telegram.ui;

import org.telegram.tgnet.TLRPC.BotInlineResult;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.ui.Components.AlertsCreator.ScheduleDatePickerDelegate;
import org.telegram.ui.ContentPreviewViewer.AnonymousClass1;
import org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ContentPreviewViewer$1$OPsVklic0vV-NhdYCfmrpeZgJyo implements ScheduleDatePickerDelegate {
    private final /* synthetic */ ContentPreviewViewerDelegate f$0;
    private final /* synthetic */ Document f$1;
    private final /* synthetic */ BotInlineResult f$2;

    public /* synthetic */ -$$Lambda$ContentPreviewViewer$1$OPsVklic0vV-NhdYCfmrpeZgJyo(ContentPreviewViewerDelegate contentPreviewViewerDelegate, Document document, BotInlineResult botInlineResult) {
        this.f$0 = contentPreviewViewerDelegate;
        this.f$1 = document;
        this.f$2 = botInlineResult;
    }

    public final void didSelectDate(boolean z, int i) {
        AnonymousClass1.lambda$null$3(this.f$0, this.f$1, this.f$2, z, i);
    }
}
