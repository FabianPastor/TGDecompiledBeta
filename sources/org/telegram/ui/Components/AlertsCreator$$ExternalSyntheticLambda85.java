package org.telegram.ui.Components;

import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.Components.AlertsCreator;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda85 implements ActionBarMenuItem.ActionBarMenuItemDelegate {
    public final /* synthetic */ AlertsCreator.ScheduleDatePickerDelegate f$0;
    public final /* synthetic */ BottomSheet.Builder f$1;

    public /* synthetic */ AlertsCreator$$ExternalSyntheticLambda85(AlertsCreator.ScheduleDatePickerDelegate scheduleDatePickerDelegate, BottomSheet.Builder builder) {
        this.f$0 = scheduleDatePickerDelegate;
        this.f$1 = builder;
    }

    public final void onItemClick(int i) {
        AlertsCreator.lambda$createScheduleDatePickerDialog$41(this.f$0, this.f$1, i);
    }
}
