package j$.util.stream;

import j$.util.function.LongConsumer;
import j$.util.stream.Node;

public final /* synthetic */ class Node$OfLong$$ExternalSyntheticLambda0 implements LongConsumer {
    public static final /* synthetic */ Node$OfLong$$ExternalSyntheticLambda0 INSTANCE = new Node$OfLong$$ExternalSyntheticLambda0();

    private /* synthetic */ Node$OfLong$$ExternalSyntheticLambda0() {
    }

    public final void accept(long j) {
        Node.OfLong.CC.lambda$truncate$0(j);
    }

    public /* synthetic */ LongConsumer andThen(LongConsumer longConsumer) {
        return LongConsumer.CC.$default$andThen(this, longConsumer);
    }
}
