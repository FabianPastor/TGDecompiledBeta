package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC.MessageEntity;

final /* synthetic */ class DataQuery$$Lambda$110 implements Comparator {
    static final Comparator $instance = new DataQuery$$Lambda$110();

    private DataQuery$$Lambda$110() {
    }

    public int compare(Object obj, Object obj2) {
        return DataQuery.lambda$static$84$DataQuery((MessageEntity) obj, (MessageEntity) obj2);
    }
}
