package j$.util.stream;

import j$.util.function.BiFunction;
import j$.util.function.BinaryOperator;
import j$.util.function.Function;
import j$.util.stream.Node;
import j$.util.stream.Nodes;

public final /* synthetic */ class Nodes$CollectorTask$OfLong$$ExternalSyntheticLambda0 implements BinaryOperator {
    public static final /* synthetic */ Nodes$CollectorTask$OfLong$$ExternalSyntheticLambda0 INSTANCE = new Nodes$CollectorTask$OfLong$$ExternalSyntheticLambda0();

    private /* synthetic */ Nodes$CollectorTask$OfLong$$ExternalSyntheticLambda0() {
    }

    public /* synthetic */ BiFunction andThen(Function function) {
        return BiFunction.CC.$default$andThen(this, function);
    }

    public final Object apply(Object obj, Object obj2) {
        return new Nodes.ConcNode.OfLong((Node.OfLong) obj, (Node.OfLong) obj2);
    }
}
