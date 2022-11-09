package org.telegram.messenger.utils;

import j$.util.function.ToIntFunction;
import org.telegram.messenger.utils.BitmapsCache;
/* loaded from: classes.dex */
public final /* synthetic */ class BitmapsCache$$ExternalSyntheticLambda2 implements ToIntFunction {
    public static final /* synthetic */ BitmapsCache$$ExternalSyntheticLambda2 INSTANCE = new BitmapsCache$$ExternalSyntheticLambda2();

    private /* synthetic */ BitmapsCache$$ExternalSyntheticLambda2() {
    }

    @Override // j$.util.function.ToIntFunction
    public final int applyAsInt(Object obj) {
        int i;
        i = ((BitmapsCache.FrameOffset) obj).index;
        return i;
    }
}
