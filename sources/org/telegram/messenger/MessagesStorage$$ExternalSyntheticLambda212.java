package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.ui.Adapters.DialogsSearchAdapter;
/* loaded from: classes.dex */
public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda212 implements Comparator {
    public static final /* synthetic */ MessagesStorage$$ExternalSyntheticLambda212 INSTANCE = new MessagesStorage$$ExternalSyntheticLambda212();

    private /* synthetic */ MessagesStorage$$ExternalSyntheticLambda212() {
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        int lambda$localSearch$209;
        lambda$localSearch$209 = MessagesStorage.lambda$localSearch$209((DialogsSearchAdapter.DialogSearchResult) obj, (DialogsSearchAdapter.DialogSearchResult) obj2);
        return lambda$localSearch$209;
    }
}
