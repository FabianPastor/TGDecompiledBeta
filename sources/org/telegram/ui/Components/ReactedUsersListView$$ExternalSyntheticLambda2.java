package org.telegram.ui.Components;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC$TL_messageUserReaction;

public final /* synthetic */ class ReactedUsersListView$$ExternalSyntheticLambda2 implements Comparator {
    public static final /* synthetic */ ReactedUsersListView$$ExternalSyntheticLambda2 INSTANCE = new ReactedUsersListView$$ExternalSyntheticLambda2();

    private /* synthetic */ ReactedUsersListView$$ExternalSyntheticLambda2() {
    }

    public final int compare(Object obj, Object obj2) {
        return ReactedUsersListView.lambda$load$1((TLRPC$TL_messageUserReaction) obj, (TLRPC$TL_messageUserReaction) obj2);
    }
}