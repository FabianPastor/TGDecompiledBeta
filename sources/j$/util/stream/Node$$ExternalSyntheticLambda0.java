package j$.util.stream;

import j$.util.function.Consumer;
import j$.util.stream.Node;

public final /* synthetic */ class Node$$ExternalSyntheticLambda0 implements Consumer {
    public static final /* synthetic */ Node$$ExternalSyntheticLambda0 INSTANCE = new Node$$ExternalSyntheticLambda0();

    private /* synthetic */ Node$$ExternalSyntheticLambda0() {
    }

    public final void accept(Object obj) {
        Node.CC.lambda$truncate$0(obj);
    }

    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return Consumer.CC.$default$andThen(this, consumer);
    }
}
