package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.PhotoViewer;

/* renamed from: org.telegram.ui.-$$Lambda$PhotoViewer$11$IO7TDA61UBtFZNyBU4cUtuu1pYc  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$PhotoViewer$11$IO7TDA61UBtFZNyBU4cUtuu1pYc implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$PhotoViewer$11$IO7TDA61UBtFZNyBU4cUtuu1pYc INSTANCE = new $$Lambda$PhotoViewer$11$IO7TDA61UBtFZNyBU4cUtuu1pYc();

    private /* synthetic */ $$Lambda$PhotoViewer$11$IO7TDA61UBtFZNyBU4cUtuu1pYc() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        PhotoViewer.AnonymousClass11.lambda$onItemClick$3(tLObject, tLRPC$TL_error);
    }
}
