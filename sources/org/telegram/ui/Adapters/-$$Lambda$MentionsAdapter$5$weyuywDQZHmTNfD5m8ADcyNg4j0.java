package org.telegram.ui.Adapters;

import android.util.SparseArray;
import java.util.ArrayList;
import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.Adapters.MentionsAdapter.AnonymousClass5;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MentionsAdapter$5$weyuywDQZHmTNfD5m8ADcyNg4j0 implements RequestDelegate {
    private final /* synthetic */ AnonymousClass5 f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ ArrayList f$2;
    private final /* synthetic */ SparseArray f$3;
    private final /* synthetic */ MessagesController f$4;

    public /* synthetic */ -$$Lambda$MentionsAdapter$5$weyuywDQZHmTNfD5m8ADcyNg4j0(AnonymousClass5 anonymousClass5, int i, ArrayList arrayList, SparseArray sparseArray, MessagesController messagesController) {
        this.f$0 = anonymousClass5;
        this.f$1 = i;
        this.f$2 = arrayList;
        this.f$3 = sparseArray;
        this.f$4 = messagesController;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$run$1$MentionsAdapter$5(this.f$1, this.f$2, this.f$3, this.f$4, tLObject, tL_error);
    }
}
