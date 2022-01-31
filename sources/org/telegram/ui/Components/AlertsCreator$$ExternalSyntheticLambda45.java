package org.telegram.ui.Components;

import android.view.View;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.Components.AlertsCreator;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda45 implements View.OnClickListener {
    public final /* synthetic */ ActionBarMenuItem f$0;
    public final /* synthetic */ AlertsCreator.ScheduleDatePickerColors f$1;

    public /* synthetic */ AlertsCreator$$ExternalSyntheticLambda45(ActionBarMenuItem actionBarMenuItem, AlertsCreator.ScheduleDatePickerColors scheduleDatePickerColors) {
        this.f$0 = actionBarMenuItem;
        this.f$1 = scheduleDatePickerColors;
    }

    public final void onClick(View view) {
        AlertsCreator.lambda$createScheduleDatePickerDialog$42(this.f$0, this.f$1, view);
    }
}
