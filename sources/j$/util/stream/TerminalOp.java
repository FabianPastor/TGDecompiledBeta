package j$.util.stream;

import j$.util.Spliterator;

interface TerminalOp<E_IN, R> {
    <P_IN> R evaluateParallel(PipelineHelper<E_IN> pipelineHelper, Spliterator<P_IN> spliterator);

    <P_IN> R evaluateSequential(PipelineHelper<E_IN> pipelineHelper, Spliterator<P_IN> spliterator);

    int getOpFlags();

    StreamShape inputShape();

    /* renamed from: j$.util.stream.TerminalOp$-CC  reason: invalid class name */
    public final /* synthetic */ class CC {
        public static StreamShape $default$inputShape(TerminalOp _this) {
            return StreamShape.REFERENCE;
        }

        public static int $default$getOpFlags(TerminalOp _this) {
            return 0;
        }

        public static <P_IN> Object $default$evaluateParallel(TerminalOp _this, PipelineHelper helper, Spliterator spliterator) {
            if (Tripwire.ENABLED) {
                Tripwire.trip(_this.getClass(), "{0} triggering TerminalOp.evaluateParallel serial default");
            }
            return _this.evaluateSequential(helper, spliterator);
        }
    }
}
