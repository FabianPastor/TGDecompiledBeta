package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.ui.ChatLinkActivity;

public final /* synthetic */ class ChatLinkActivity$SearchAdapter$$ExternalSyntheticLambda3 implements Runnable {
    public final /* synthetic */ ChatLinkActivity.SearchAdapter f$0;
    public final /* synthetic */ ArrayList f$1;
    public final /* synthetic */ ArrayList f$2;

    public /* synthetic */ ChatLinkActivity$SearchAdapter$$ExternalSyntheticLambda3(ChatLinkActivity.SearchAdapter searchAdapter, ArrayList arrayList, ArrayList arrayList2) {
        this.f$0 = searchAdapter;
        this.f$1 = arrayList;
        this.f$2 = arrayList2;
    }

    public final void run() {
        this.f$0.lambda$updateSearchResults$3(this.f$1, this.f$2);
    }
}
