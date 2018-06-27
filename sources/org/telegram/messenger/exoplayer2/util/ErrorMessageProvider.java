package org.telegram.messenger.exoplayer2.util;

import android.util.Pair;

public interface ErrorMessageProvider<T extends Throwable> {
    Pair<Integer, String> getErrorMessage(T t);
}
