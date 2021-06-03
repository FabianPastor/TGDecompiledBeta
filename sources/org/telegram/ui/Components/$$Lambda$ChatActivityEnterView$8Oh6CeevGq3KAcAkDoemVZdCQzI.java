package org.telegram.ui.Components;

import org.telegram.messenger.MediaController;

/* renamed from: org.telegram.ui.Components.-$$Lambda$ChatActivityEnterView$8Oh6CeevGq3KAcAkDoemVZdCQzI  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$ChatActivityEnterView$8Oh6CeevGq3KAcAkDoemVZdCQzI implements Runnable {
    public static final /* synthetic */ $$Lambda$ChatActivityEnterView$8Oh6CeevGq3KAcAkDoemVZdCQzI INSTANCE = new $$Lambda$ChatActivityEnterView$8Oh6CeevGq3KAcAkDoemVZdCQzI();

    private /* synthetic */ $$Lambda$ChatActivityEnterView$8Oh6CeevGq3KAcAkDoemVZdCQzI() {
    }

    public final void run() {
        MediaController.getInstance().stopRecording(0, false, 0);
    }
}
