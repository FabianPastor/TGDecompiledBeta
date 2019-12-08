package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.ui.Components.AlertsCreator.ScheduleDatePickerDelegate;
import org.telegram.ui.DocumentSelectActivity.AnonymousClass2;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DocumentSelectActivity$2$yl01IzUpli-PVnRCzfTZ4UDQ9vY implements ScheduleDatePickerDelegate {
    private final /* synthetic */ AnonymousClass2 f$0;
    private final /* synthetic */ ArrayList f$1;

    public /* synthetic */ -$$Lambda$DocumentSelectActivity$2$yl01IzUpli-PVnRCzfTZ4UDQ9vY(AnonymousClass2 anonymousClass2, ArrayList arrayList) {
        this.f$0 = anonymousClass2;
        this.f$1 = arrayList;
    }

    public final void didSelectDate(boolean z, int i) {
        this.f$0.lambda$onItemClick$0$DocumentSelectActivity$2(this.f$1, z, i);
    }
}
