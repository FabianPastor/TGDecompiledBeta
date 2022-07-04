package j$.util.stream;

import j$.util.function.IntConsumer;
import j$.util.stream.Node;

public final /* synthetic */ class Node$OfInt$$ExternalSyntheticLambda0 implements IntConsumer {
    public static final /* synthetic */ Node$OfInt$$ExternalSyntheticLambda0 INSTANCE = new Node$OfInt$$ExternalSyntheticLambda0();

    private /* synthetic */ Node$OfInt$$ExternalSyntheticLambda0() {
    }

    public final void accept(int i) {
        Node.OfInt.CC.lambda$truncate$0(i);
    }

    public /* synthetic */ IntConsumer andThen(IntConsumer intConsumer) {
        return IntConsumer.CC.$default$andThen(this, intConsumer);
    }
}
