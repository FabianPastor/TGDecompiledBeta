package org.telegram.messenger.utils;

import j$.util.function.ToIntFunction;
import org.telegram.messenger.utils.BitmapsCache;

public final /* synthetic */ class BitmapsCache$$ExternalSyntheticLambda1 implements ToIntFunction {
    public static final /* synthetic */ BitmapsCache$$ExternalSyntheticLambda1 INSTANCE = new BitmapsCache$$ExternalSyntheticLambda1();

    private /* synthetic */ BitmapsCache$$ExternalSyntheticLambda1() {
    }

    public final int applyAsInt(Object obj) {
        return ((BitmapsCache.FrameOffset) obj).index;
    }
}
