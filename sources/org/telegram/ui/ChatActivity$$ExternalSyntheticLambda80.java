package org.telegram.ui;

import androidx.core.util.Consumer;
import java.util.List;
import org.telegram.ui.Components.ReactedUsersListView;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda80 implements Consumer {
    public final /* synthetic */ ReactedUsersListView f$0;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda80(ReactedUsersListView reactedUsersListView) {
        this.f$0 = reactedUsersListView;
    }

    public final void accept(Object obj) {
        this.f$0.setSeenUsers((List) obj);
    }
}
