package org.telegram.ui.Components;

import org.telegram.messenger.MediaController;

/* renamed from: org.telegram.ui.Components.-$$Lambda$ChatActivityEnterView$A_nC1eD4I1yfzYCn-eX_xvHnwg8  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$ChatActivityEnterView$A_nC1eD4I1yfzYCneX_xvHnwg8 implements Runnable {
    public static final /* synthetic */ $$Lambda$ChatActivityEnterView$A_nC1eD4I1yfzYCneX_xvHnwg8 INSTANCE = new $$Lambda$ChatActivityEnterView$A_nC1eD4I1yfzYCneX_xvHnwg8();

    private /* synthetic */ $$Lambda$ChatActivityEnterView$A_nC1eD4I1yfzYCneX_xvHnwg8() {
    }

    public final void run() {
        MediaController.getInstance().stopRecording(0, false, 0);
    }
}
