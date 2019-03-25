package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC.MessageEntity;

final /* synthetic */ class DataQuery$$Lambda$121 implements Comparator {
    static final Comparator $instance = new DataQuery$$Lambda$121();

    private DataQuery$$Lambda$121() {
    }

    public int compare(Object obj, Object obj2) {
        return DataQuery.lambda$static$84$DataQuery((MessageEntity) obj, (MessageEntity) obj2);
    }
}
