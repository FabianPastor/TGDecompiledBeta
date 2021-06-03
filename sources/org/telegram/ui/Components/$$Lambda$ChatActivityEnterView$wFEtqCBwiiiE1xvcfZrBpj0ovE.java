package org.telegram.ui.Components;

import org.telegram.messenger.MediaController;
import org.telegram.ui.Components.AlertsCreator;

/* renamed from: org.telegram.ui.Components.-$$Lambda$ChatActivityEnterView$wFEtqCBwii-iE1xvcfZrBpj0ovE  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$ChatActivityEnterView$wFEtqCBwiiiE1xvcfZrBpj0ovE implements AlertsCreator.ScheduleDatePickerDelegate {
    public static final /* synthetic */ $$Lambda$ChatActivityEnterView$wFEtqCBwiiiE1xvcfZrBpj0ovE INSTANCE = new $$Lambda$ChatActivityEnterView$wFEtqCBwiiiE1xvcfZrBpj0ovE();

    private /* synthetic */ $$Lambda$ChatActivityEnterView$wFEtqCBwiiiE1xvcfZrBpj0ovE() {
    }

    public final void didSelectDate(boolean z, int i) {
        MediaController.getInstance().stopRecording(1, z, i);
    }
}
