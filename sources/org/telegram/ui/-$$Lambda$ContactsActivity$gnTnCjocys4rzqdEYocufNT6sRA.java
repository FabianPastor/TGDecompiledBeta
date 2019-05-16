package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ContactsActivity$gnTnCjocys4rzqdEYocufNT6sRA implements OnItemClickListener {
    private final /* synthetic */ ContactsActivity f$0;
    private final /* synthetic */ int f$1;

    public /* synthetic */ -$$Lambda$ContactsActivity$gnTnCjocys4rzqdEYocufNT6sRA(ContactsActivity contactsActivity, int i) {
        this.f$0 = contactsActivity;
        this.f$1 = i;
    }

    public final void onItemClick(View view, int i) {
        this.f$0.lambda$createView$1$ContactsActivity(this.f$1, view, i);
    }
}
