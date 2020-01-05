package org.telegram.ui;

import org.telegram.ui.Components.AlertsCreator.ScheduleDatePickerDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DocumentSelectActivity$gnHA3uCcnNOMtdSeprkWkUaVbNw implements ScheduleDatePickerDelegate {
    private final /* synthetic */ DocumentSelectActivity f$0;

    public /* synthetic */ -$$Lambda$DocumentSelectActivity$gnHA3uCcnNOMtdSeprkWkUaVbNw(DocumentSelectActivity documentSelectActivity) {
        this.f$0 = documentSelectActivity;
    }

    public final void didSelectDate(boolean z, int i) {
        this.f$0.sendSelectedFiles(z, i);
    }
}
