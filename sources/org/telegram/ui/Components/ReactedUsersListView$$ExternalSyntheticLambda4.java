package org.telegram.ui.Components;

import j$.util.function.ToIntFunction;
import org.telegram.tgnet.TLRPC$MessagePeerReaction;

public final /* synthetic */ class ReactedUsersListView$$ExternalSyntheticLambda4 implements ToIntFunction {
    public static final /* synthetic */ ReactedUsersListView$$ExternalSyntheticLambda4 INSTANCE = new ReactedUsersListView$$ExternalSyntheticLambda4();

    private /* synthetic */ ReactedUsersListView$$ExternalSyntheticLambda4() {
    }

    public final int applyAsInt(Object obj) {
        return ReactedUsersListView.lambda$load$2((TLRPC$MessagePeerReaction) obj);
    }
}
