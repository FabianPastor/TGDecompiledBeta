package org.telegram.ui.Adapters;

import org.telegram.messenger.MessagesStorage;

public final /* synthetic */ class DialogsSearchAdapter$$ExternalSyntheticLambda17 implements MessagesStorage.IntCallback {
    public final /* synthetic */ DialogsSearchAdapter f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ Object f$2;

    public /* synthetic */ DialogsSearchAdapter$$ExternalSyntheticLambda17(DialogsSearchAdapter dialogsSearchAdapter, long j, Object obj) {
        this.f$0 = dialogsSearchAdapter;
        this.f$1 = j;
        this.f$2 = obj;
    }

    public final void run(int i) {
        this.f$0.lambda$updateSearchResults$11(this.f$1, this.f$2, i);
    }
}
