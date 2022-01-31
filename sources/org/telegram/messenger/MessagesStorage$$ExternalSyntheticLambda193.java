package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.ui.Adapters.DialogsSearchAdapter;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda193 implements Comparator {
    public static final /* synthetic */ MessagesStorage$$ExternalSyntheticLambda193 INSTANCE = new MessagesStorage$$ExternalSyntheticLambda193();

    private /* synthetic */ MessagesStorage$$ExternalSyntheticLambda193() {
    }

    public final int compare(Object obj, Object obj2) {
        return MessagesStorage.lambda$localSearch$192((DialogsSearchAdapter.DialogSearchResult) obj, (DialogsSearchAdapter.DialogSearchResult) obj2);
    }
}
