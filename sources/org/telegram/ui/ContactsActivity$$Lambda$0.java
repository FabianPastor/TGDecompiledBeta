package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;

final /* synthetic */ class ContactsActivity$$Lambda$0 implements OnItemClickListener {
    private final ContactsActivity arg$1;

    ContactsActivity$$Lambda$0(ContactsActivity contactsActivity) {
        this.arg$1 = contactsActivity;
    }

    public void onItemClick(View view, int i) {
        this.arg$1.lambda$createView$1$ContactsActivity(view, i);
    }
}
