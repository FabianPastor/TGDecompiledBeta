package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.ui.Adapters.DialogsSearchAdapter;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda194 implements Comparator {
    public static final /* synthetic */ MessagesStorage$$ExternalSyntheticLambda194 INSTANCE = new MessagesStorage$$ExternalSyntheticLambda194();

    private /* synthetic */ MessagesStorage$$ExternalSyntheticLambda194() {
    }

    public final int compare(Object obj, Object obj2) {
        return MessagesStorage.lambda$localSearch$193((DialogsSearchAdapter.DialogSearchResult) obj, (DialogsSearchAdapter.DialogSearchResult) obj2);
    }
}
