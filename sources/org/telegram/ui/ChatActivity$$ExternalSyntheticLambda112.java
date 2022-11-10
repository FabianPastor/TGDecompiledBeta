package org.telegram.ui;

import androidx.core.util.Consumer;
import java.util.List;
import org.telegram.ui.Components.ReactedUsersListView;
/* loaded from: classes3.dex */
public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda112 implements Consumer {
    public final /* synthetic */ ReactedUsersListView f$0;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda112(ReactedUsersListView reactedUsersListView) {
        this.f$0 = reactedUsersListView;
    }

    @Override // androidx.core.util.Consumer
    public final void accept(Object obj) {
        this.f$0.setSeenUsers((List) obj);
    }
}
