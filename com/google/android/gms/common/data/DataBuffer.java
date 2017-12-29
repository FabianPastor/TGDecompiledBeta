package com.google.android.gms.common.data;

import com.google.android.gms.common.api.Releasable;

public interface DataBuffer<T> extends Releasable, Iterable<T> {
    T get(int i);

    int getCount();
}
