package org.telegram.ui;

import org.telegram.ui.Components.AlertsCreator.ScheduleDatePickerDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PhotoPickerActivity$euYzkjcWzyeckwYcPmSeZL7u1Vc implements ScheduleDatePickerDelegate {
    private final /* synthetic */ PhotoPickerActivity f$0;

    public /* synthetic */ -$$Lambda$PhotoPickerActivity$euYzkjcWzyeckwYcPmSeZL7u1Vc(PhotoPickerActivity photoPickerActivity) {
        this.f$0 = photoPickerActivity;
    }

    public final void didSelectDate(boolean z, int i) {
        this.f$0.sendSelectedPhotos(z, i);
    }
}
