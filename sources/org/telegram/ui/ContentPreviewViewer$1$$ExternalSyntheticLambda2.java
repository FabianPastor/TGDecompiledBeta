package org.telegram.ui;

import org.telegram.tgnet.TLRPC$BotInlineResult;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.ContentPreviewViewer;

public final /* synthetic */ class ContentPreviewViewer$1$$ExternalSyntheticLambda2 implements AlertsCreator.ScheduleDatePickerDelegate {
    public final /* synthetic */ ContentPreviewViewer.ContentPreviewViewerDelegate f$0;
    public final /* synthetic */ TLRPC$Document f$1;
    public final /* synthetic */ TLRPC$BotInlineResult f$2;
    public final /* synthetic */ Object f$3;

    public /* synthetic */ ContentPreviewViewer$1$$ExternalSyntheticLambda2(ContentPreviewViewer.ContentPreviewViewerDelegate contentPreviewViewerDelegate, TLRPC$Document tLRPC$Document, TLRPC$BotInlineResult tLRPC$BotInlineResult, Object obj) {
        this.f$0 = contentPreviewViewerDelegate;
        this.f$1 = tLRPC$Document;
        this.f$2 = tLRPC$BotInlineResult;
        this.f$3 = obj;
    }

    public final void didSelectDate(boolean z, int i) {
        ContentPreviewViewer.AnonymousClass1.lambda$run$0(this.f$0, this.f$1, this.f$2, this.f$3, z, i);
    }
}
