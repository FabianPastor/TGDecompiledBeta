package org.telegram.ui.Components;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC$TL_messagePeerReaction;

public final /* synthetic */ class ReactedUsersListView$$ExternalSyntheticLambda3 implements Comparator {
    public static final /* synthetic */ ReactedUsersListView$$ExternalSyntheticLambda3 INSTANCE = new ReactedUsersListView$$ExternalSyntheticLambda3();

    private /* synthetic */ ReactedUsersListView$$ExternalSyntheticLambda3() {
    }

    public final int compare(Object obj, Object obj2) {
        return ReactedUsersListView.lambda$load$1((TLRPC$TL_messagePeerReaction) obj, (TLRPC$TL_messagePeerReaction) obj2);
    }
}
