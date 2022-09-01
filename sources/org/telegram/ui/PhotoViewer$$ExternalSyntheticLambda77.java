package org.telegram.ui;

import org.telegram.ui.Components.AlertsCreator;

public final /* synthetic */ class PhotoViewer$$ExternalSyntheticLambda77 implements AlertsCreator.ScheduleDatePickerDelegate {
    public final /* synthetic */ PhotoViewer f$0;

    public /* synthetic */ PhotoViewer$$ExternalSyntheticLambda77(PhotoViewer photoViewer) {
        this.f$0 = photoViewer;
    }

    public final void didSelectDate(boolean z, int i) {
        this.f$0.sendPressed(z, i);
    }
}
