package j$.util.stream;

import j$.util.function.BiConsumer;
import java.util.LinkedHashSet;

public final /* synthetic */ class DistinctOps$1$$ExternalSyntheticLambda0 implements BiConsumer {
    public static final /* synthetic */ DistinctOps$1$$ExternalSyntheticLambda0 INSTANCE = new DistinctOps$1$$ExternalSyntheticLambda0();

    private /* synthetic */ DistinctOps$1$$ExternalSyntheticLambda0() {
    }

    public final void accept(Object obj, Object obj2) {
        ((LinkedHashSet) obj).add(obj2);
    }

    public /* synthetic */ BiConsumer andThen(BiConsumer biConsumer) {
        return BiConsumer.CC.$default$andThen(this, biConsumer);
    }
}
