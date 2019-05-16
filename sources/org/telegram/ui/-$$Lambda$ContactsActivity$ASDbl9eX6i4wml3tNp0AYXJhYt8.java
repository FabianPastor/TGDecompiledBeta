package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ContactsActivity$ASDbl9eX6i4wml3tNp0AYXJhYt8 implements OnItemClickListener {
    private final /* synthetic */ ContactsActivity f$0;
    private final /* synthetic */ boolean f$1;

    public /* synthetic */ -$$Lambda$ContactsActivity$ASDbl9eX6i4wml3tNp0AYXJhYt8(ContactsActivity contactsActivity, boolean z) {
        this.f$0 = contactsActivity;
        this.f$1 = z;
    }

    public final void onItemClick(View view, int i) {
        this.f$0.lambda$createView$1$ContactsActivity(this.f$1, view, i);
    }
}
