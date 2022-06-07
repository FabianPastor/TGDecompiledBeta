package org.telegram.ui;

import org.telegram.ui.Components.AlertsCreator;

public final /* synthetic */ class PhotoViewer$$ExternalSyntheticLambda73 implements AlertsCreator.ScheduleDatePickerDelegate {
    public final /* synthetic */ PhotoViewer f$0;

    public /* synthetic */ PhotoViewer$$ExternalSyntheticLambda73(PhotoViewer photoViewer) {
        this.f$0 = photoViewer;
    }

    public final void didSelectDate(boolean z, int i) {
        this.f$0.sendPressed(z, i);
    }
}
