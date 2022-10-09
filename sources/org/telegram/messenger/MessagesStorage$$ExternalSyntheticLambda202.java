package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.ui.Adapters.DialogsSearchAdapter;
/* loaded from: classes.dex */
public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda202 implements Comparator {
    public static final /* synthetic */ MessagesStorage$$ExternalSyntheticLambda202 INSTANCE = new MessagesStorage$$ExternalSyntheticLambda202();

    private /* synthetic */ MessagesStorage$$ExternalSyntheticLambda202() {
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        int lambda$localSearch$199;
        lambda$localSearch$199 = MessagesStorage.lambda$localSearch$199((DialogsSearchAdapter.DialogSearchResult) obj, (DialogsSearchAdapter.DialogSearchResult) obj2);
        return lambda$localSearch$199;
    }
}
