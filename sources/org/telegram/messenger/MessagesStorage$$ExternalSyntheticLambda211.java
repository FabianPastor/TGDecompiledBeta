package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.ui.Adapters.DialogsSearchAdapter;
/* loaded from: classes.dex */
public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda211 implements Comparator {
    public static final /* synthetic */ MessagesStorage$$ExternalSyntheticLambda211 INSTANCE = new MessagesStorage$$ExternalSyntheticLambda211();

    private /* synthetic */ MessagesStorage$$ExternalSyntheticLambda211() {
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        int lambda$localSearch$209;
        lambda$localSearch$209 = MessagesStorage.lambda$localSearch$209((DialogsSearchAdapter.DialogSearchResult) obj, (DialogsSearchAdapter.DialogSearchResult) obj2);
        return lambda$localSearch$209;
    }
}
