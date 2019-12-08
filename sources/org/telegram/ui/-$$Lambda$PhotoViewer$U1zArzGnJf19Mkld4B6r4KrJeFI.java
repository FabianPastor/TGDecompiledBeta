package org.telegram.ui;

import org.telegram.ui.Components.AlertsCreator.ScheduleDatePickerDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PhotoViewer$U1zArzGnJvar_Mkld4B6r4KrJeFI implements ScheduleDatePickerDelegate {
    private final /* synthetic */ PhotoViewer f$0;

    public /* synthetic */ -$$Lambda$PhotoViewer$U1zArzGnJvar_Mkld4B6r4KrJeFI(PhotoViewer photoViewer) {
        this.f$0 = photoViewer;
    }

    public final void didSelectDate(boolean z, int i) {
        this.f$0.sendPressed(z, i);
    }
}
