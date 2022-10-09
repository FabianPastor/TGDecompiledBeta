package org.telegram.ui.Components;

import j$.util.function.ToIntFunction;
import org.telegram.tgnet.TLRPC$MessagePeerReaction;
/* loaded from: classes3.dex */
public final /* synthetic */ class ReactedUsersListView$$ExternalSyntheticLambda4 implements ToIntFunction {
    public static final /* synthetic */ ReactedUsersListView$$ExternalSyntheticLambda4 INSTANCE = new ReactedUsersListView$$ExternalSyntheticLambda4();

    private /* synthetic */ ReactedUsersListView$$ExternalSyntheticLambda4() {
    }

    @Override // j$.util.function.ToIntFunction
    public final int applyAsInt(Object obj) {
        int lambda$load$2;
        lambda$load$2 = ReactedUsersListView.lambda$load$2((TLRPC$MessagePeerReaction) obj);
        return lambda$load$2;
    }
}
