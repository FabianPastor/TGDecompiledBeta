package org.telegram.ui.Components.Reactions;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ReactionsLayoutInBubble$$ExternalSyntheticLambda1 implements Comparator {
    public static final /* synthetic */ ReactionsLayoutInBubble$$ExternalSyntheticLambda1 INSTANCE = new ReactionsLayoutInBubble$$ExternalSyntheticLambda1();

    private /* synthetic */ ReactionsLayoutInBubble$$ExternalSyntheticLambda1() {
    }

    public final int compare(Object obj, Object obj2) {
        return ReactionsLayoutInBubble.lambda$static$0((TLRPC.User) obj, (TLRPC.User) obj2);
    }
}
