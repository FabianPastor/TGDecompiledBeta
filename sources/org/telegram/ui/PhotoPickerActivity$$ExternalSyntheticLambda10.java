package org.telegram.ui;

import org.telegram.ui.Components.AlertsCreator;

public final /* synthetic */ class PhotoPickerActivity$$ExternalSyntheticLambda10 implements AlertsCreator.ScheduleDatePickerDelegate {
    public final /* synthetic */ PhotoPickerActivity f$0;

    public /* synthetic */ PhotoPickerActivity$$ExternalSyntheticLambda10(PhotoPickerActivity photoPickerActivity) {
        this.f$0 = photoPickerActivity;
    }

    public final void didSelectDate(boolean z, int i) {
        this.f$0.sendSelectedPhotos(z, i);
    }
}
