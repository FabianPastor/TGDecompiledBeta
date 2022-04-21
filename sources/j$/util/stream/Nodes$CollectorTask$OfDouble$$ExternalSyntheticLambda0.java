package j$.util.stream;

import j$.util.function.BiFunction;
import j$.util.function.BinaryOperator;
import j$.util.function.Function;
import j$.util.stream.Node;
import j$.util.stream.Nodes;

public final /* synthetic */ class Nodes$CollectorTask$OfDouble$$ExternalSyntheticLambda0 implements BinaryOperator {
    public static final /* synthetic */ Nodes$CollectorTask$OfDouble$$ExternalSyntheticLambda0 INSTANCE = new Nodes$CollectorTask$OfDouble$$ExternalSyntheticLambda0();

    private /* synthetic */ Nodes$CollectorTask$OfDouble$$ExternalSyntheticLambda0() {
    }

    public /* synthetic */ BiFunction andThen(Function function) {
        return BiFunction.CC.$default$andThen(this, function);
    }

    public final Object apply(Object obj, Object obj2) {
        return new Nodes.ConcNode.OfDouble((Node.OfDouble) obj, (Node.OfDouble) obj2);
    }
}
