package org.telegram.ui.Cells;

import j$.util.function.ToIntFunction;
import org.telegram.tgnet.TLRPC$TL_forumTopic;
/* loaded from: classes3.dex */
public final /* synthetic */ class DialogCell$$ExternalSyntheticLambda6 implements ToIntFunction {
    public static final /* synthetic */ DialogCell$$ExternalSyntheticLambda6 INSTANCE = new DialogCell$$ExternalSyntheticLambda6();

    private /* synthetic */ DialogCell$$ExternalSyntheticLambda6() {
    }

    @Override // j$.util.function.ToIntFunction
    public final int applyAsInt(Object obj) {
        int i;
        i = ((TLRPC$TL_forumTopic) obj).date;
        return i;
    }
}
