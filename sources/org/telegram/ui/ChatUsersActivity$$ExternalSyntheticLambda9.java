package org.telegram.ui;

import java.util.Comparator;
import org.telegram.tgnet.TLObject;

public final /* synthetic */ class ChatUsersActivity$$ExternalSyntheticLambda9 implements Comparator {
    public final /* synthetic */ ChatUsersActivity f$0;

    public /* synthetic */ ChatUsersActivity$$ExternalSyntheticLambda9(ChatUsersActivity chatUsersActivity) {
        this.f$0 = chatUsersActivity;
    }

    public final int compare(Object obj, Object obj2) {
        return this.f$0.lambda$onOwnerChaged$4((TLObject) obj, (TLObject) obj2);
    }
}
