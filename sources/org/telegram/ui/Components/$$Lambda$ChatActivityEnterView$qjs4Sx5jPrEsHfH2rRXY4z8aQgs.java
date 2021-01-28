package org.telegram.ui.Components;

import org.telegram.messenger.MediaController;

/* renamed from: org.telegram.ui.Components.-$$Lambda$ChatActivityEnterView$qjs4Sx5jPrEsHfH2rRXY4z8aQgs  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$ChatActivityEnterView$qjs4Sx5jPrEsHfH2rRXY4z8aQgs implements Runnable {
    public static final /* synthetic */ $$Lambda$ChatActivityEnterView$qjs4Sx5jPrEsHfH2rRXY4z8aQgs INSTANCE = new $$Lambda$ChatActivityEnterView$qjs4Sx5jPrEsHfH2rRXY4z8aQgs();

    private /* synthetic */ $$Lambda$ChatActivityEnterView$qjs4Sx5jPrEsHfH2rRXY4z8aQgs() {
    }

    public final void run() {
        MediaController.getInstance().stopRecording(0, false, 0);
    }
}
