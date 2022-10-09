package org.telegram.ui.Components.Reactions;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC$User;
/* loaded from: classes3.dex */
public final /* synthetic */ class ReactionsLayoutInBubble$$ExternalSyntheticLambda1 implements Comparator {
    public static final /* synthetic */ ReactionsLayoutInBubble$$ExternalSyntheticLambda1 INSTANCE = new ReactionsLayoutInBubble$$ExternalSyntheticLambda1();

    private /* synthetic */ ReactionsLayoutInBubble$$ExternalSyntheticLambda1() {
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        int lambda$static$0;
        lambda$static$0 = ReactionsLayoutInBubble.lambda$static$0((TLRPC$User) obj, (TLRPC$User) obj2);
        return lambda$static$0;
    }
}
