package org.telegram.ui.Components;

import org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemDelegate;
import org.telegram.ui.ActionBar.BottomSheet.Builder;
import org.telegram.ui.Components.AlertsCreator.ScheduleDatePickerDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AlertsCreator$KR_OgeK9dqo37BHX7GI6av8Vcek implements ActionBarMenuItemDelegate {
    private final /* synthetic */ ScheduleDatePickerDelegate f$0;
    private final /* synthetic */ Builder f$1;

    public /* synthetic */ -$$Lambda$AlertsCreator$KR_OgeK9dqo37BHX7GI6av8Vcek(ScheduleDatePickerDelegate scheduleDatePickerDelegate, Builder builder) {
        this.f$0 = scheduleDatePickerDelegate;
        this.f$1 = builder;
    }

    public final void onItemClick(int i) {
        AlertsCreator.lambda$createScheduleDatePickerDialog$23(this.f$0, this.f$1, i);
    }
}
