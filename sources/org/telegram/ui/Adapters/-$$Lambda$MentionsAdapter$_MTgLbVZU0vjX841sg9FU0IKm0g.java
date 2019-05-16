package org.telegram.ui.Adapters;

import android.util.SparseArray;
import java.util.ArrayList;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MentionsAdapter$_MTgLbVZU0vjX841sg9FU0IKm0g implements Runnable {
    private final /* synthetic */ MentionsAdapter f$0;
    private final /* synthetic */ ArrayList f$1;
    private final /* synthetic */ SparseArray f$2;

    public /* synthetic */ -$$Lambda$MentionsAdapter$_MTgLbVZU0vjX841sg9FU0IKm0g(MentionsAdapter mentionsAdapter, ArrayList arrayList, SparseArray sparseArray) {
        this.f$0 = mentionsAdapter;
        this.f$1 = arrayList;
        this.f$2 = sparseArray;
    }

    public final void run() {
        this.f$0.lambda$searchUsernameOrHashtag$6$MentionsAdapter(this.f$1, this.f$2);
    }
}
