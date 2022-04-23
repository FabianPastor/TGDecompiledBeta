package org.telegram.ui.Components;

import j$.util.function.ToIntFunction;
import org.telegram.tgnet.TLRPC$TL_messagePeerReaction;

public final /* synthetic */ class ReactedUsersListView$$ExternalSyntheticLambda3 implements ToIntFunction {
    public static final /* synthetic */ ReactedUsersListView$$ExternalSyntheticLambda3 INSTANCE = new ReactedUsersListView$$ExternalSyntheticLambda3();

    private /* synthetic */ ReactedUsersListView$$ExternalSyntheticLambda3() {
    }

    public final int applyAsInt(Object obj) {
        return ReactedUsersListView.lambda$load$1((TLRPC$TL_messagePeerReaction) obj);
    }
}
