package org.telegram.ui;

import androidx.core.util.Consumer;
import java.util.List;
import org.telegram.ui.Components.ReactedUsersListView;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda111 implements Consumer {
    public final /* synthetic */ ReactedUsersListView f$0;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda111(ReactedUsersListView reactedUsersListView) {
        this.f$0 = reactedUsersListView;
    }

    public final void accept(Object obj) {
        this.f$0.setSeenUsers((List) obj);
    }
}
