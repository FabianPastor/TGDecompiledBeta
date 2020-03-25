package org.telegram.ui.Components;

import org.telegram.messenger.MediaController;

/* renamed from: org.telegram.ui.Components.-$$Lambda$ChatActivityEnterView$ig8-uuPdwp02PNB5dFwZQd6fT5I  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$ChatActivityEnterView$ig8uuPdwp02PNB5dFwZQd6fT5I implements Runnable {
    public static final /* synthetic */ $$Lambda$ChatActivityEnterView$ig8uuPdwp02PNB5dFwZQd6fT5I INSTANCE = new $$Lambda$ChatActivityEnterView$ig8uuPdwp02PNB5dFwZQd6fT5I();

    private /* synthetic */ $$Lambda$ChatActivityEnterView$ig8uuPdwp02PNB5dFwZQd6fT5I() {
    }

    public final void run() {
        MediaController.getInstance().stopRecording(0, false, 0);
    }
}
