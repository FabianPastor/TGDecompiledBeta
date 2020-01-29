package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

/* renamed from: org.telegram.ui.-$$Lambda$StickersActivity$eNJ1JVvMbECvyP_jXUZlvL1o67I  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$StickersActivity$eNJ1JVvMbECvyP_jXUZlvL1o67I implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$StickersActivity$eNJ1JVvMbECvyP_jXUZlvL1o67I INSTANCE = new $$Lambda$StickersActivity$eNJ1JVvMbECvyP_jXUZlvL1o67I();

    private /* synthetic */ $$Lambda$StickersActivity$eNJ1JVvMbECvyP_jXUZlvL1o67I() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        StickersActivity.lambda$sendReorder$2(tLObject, tL_error);
    }
}
