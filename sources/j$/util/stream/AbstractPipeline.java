package j$.util.stream;

import j$.util.Spliterator;
import j$.util.function.IntFunction;
import j$.util.function.Supplier;
import j$.util.stream.BaseStream;
import j$.util.stream.Node;

abstract class AbstractPipeline<E_IN, E_OUT, S extends BaseStream<E_OUT, S>> extends PipelineHelper<E_OUT> implements BaseStream<E_OUT, S> {
    static final /* synthetic */ boolean $assertionsDisabled = true;
    private static final String MSG_CONSUMED = "source already consumed or closed";
    private static final String MSG_STREAM_LINKED = "stream has already been operated upon or closed";
    private int combinedFlags;
    private int depth;
    private boolean linkedOrConsumed;
    private AbstractPipeline nextStage;
    private boolean parallel;
    private final AbstractPipeline previousStage;
    private boolean sourceAnyStateful;
    private Runnable sourceCloseAction;
    protected final int sourceOrOpFlags;
    private Spliterator<?> sourceSpliterator;
    private final AbstractPipeline sourceStage;
    private Supplier<? extends Spliterator<?>> sourceSupplier;

    /* access modifiers changed from: package-private */
    public abstract <P_IN> Node<E_OUT> evaluateToNode(PipelineHelper<E_OUT> pipelineHelper, Spliterator<P_IN> spliterator, boolean z, IntFunction<E_OUT[]> intFunction);

    /* access modifiers changed from: package-private */
    public abstract void forEachWithCancel(Spliterator<E_OUT> spliterator, Sink<E_OUT> sink);

    /* access modifiers changed from: package-private */
    public abstract StreamShape getOutputShape();

    /* access modifiers changed from: package-private */
    public abstract Spliterator<E_OUT> lazySpliterator(Supplier<? extends Spliterator<E_OUT>> supplier);

    /* access modifiers changed from: package-private */
    public abstract Node.Builder<E_OUT> makeNodeBuilder(long j, IntFunction<E_OUT[]> intFunction);

    /* access modifiers changed from: package-private */
    public abstract boolean opIsStateful();

    /* access modifiers changed from: package-private */
    public abstract Sink<E_IN> opWrapSink(int i, Sink<E_OUT> sink);

    /* access modifiers changed from: package-private */
    public abstract <P_IN> Spliterator<E_OUT> wrap(PipelineHelper<E_OUT> pipelineHelper, Supplier<Spliterator<P_IN>> supplier, boolean z);

    AbstractPipeline(Supplier<? extends Spliterator<?>> supplier, int sourceFlags, boolean parallel2) {
        this.previousStage = null;
        this.sourceSupplier = supplier;
        this.sourceStage = this;
        int i = StreamOpFlag.STREAM_MASK & sourceFlags;
        this.sourceOrOpFlags = i;
        this.combinedFlags = ((i << 1) ^ -1) & StreamOpFlag.INITIAL_OPS_VALUE;
        this.depth = 0;
        this.parallel = parallel2;
    }

    AbstractPipeline(Spliterator<?> spliterator, int sourceFlags, boolean parallel2) {
        this.previousStage = null;
        this.sourceSpliterator = spliterator;
        this.sourceStage = this;
        int i = StreamOpFlag.STREAM_MASK & sourceFlags;
        this.sourceOrOpFlags = i;
        this.combinedFlags = ((i << 1) ^ -1) & StreamOpFlag.INITIAL_OPS_VALUE;
        this.depth = 0;
        this.parallel = parallel2;
    }

    AbstractPipeline(AbstractPipeline<?, E_IN, ?> previousStage2, int opFlags) {
        if (!previousStage2.linkedOrConsumed) {
            previousStage2.linkedOrConsumed = true;
            previousStage2.nextStage = this;
            this.previousStage = previousStage2;
            this.sourceOrOpFlags = StreamOpFlag.OP_MASK & opFlags;
            this.combinedFlags = StreamOpFlag.combineOpFlags(opFlags, previousStage2.combinedFlags);
            AbstractPipeline abstractPipeline = previousStage2.sourceStage;
            this.sourceStage = abstractPipeline;
            if (opIsStateful()) {
                abstractPipeline.sourceAnyStateful = true;
            }
            this.depth = previousStage2.depth + 1;
            return;
        }
        throw new IllegalStateException("stream has already been operated upon or closed");
    }

    /* access modifiers changed from: package-private */
    public final <R> R evaluate(TerminalOp<E_OUT, R> terminalOp) {
        if (!$assertionsDisabled && getOutputShape() != terminalOp.inputShape()) {
            throw new AssertionError();
        } else if (!this.linkedOrConsumed) {
            this.linkedOrConsumed = true;
            if (isParallel()) {
                return terminalOp.evaluateParallel(this, sourceSpliterator(terminalOp.getOpFlags()));
            }
            return terminalOp.evaluateSequential(this, sourceSpliterator(terminalOp.getOpFlags()));
        } else {
            throw new IllegalStateException("stream has already been operated upon or closed");
        }
    }

    /* access modifiers changed from: package-private */
    public final Node<E_OUT> evaluateToArrayNode(IntFunction<E_OUT[]> intFunction) {
        if (!this.linkedOrConsumed) {
            this.linkedOrConsumed = true;
            if (!isParallel() || this.previousStage == null || !opIsStateful()) {
                return evaluate(sourceSpliterator(0), true, intFunction);
            }
            this.depth = 0;
            AbstractPipeline abstractPipeline = this.previousStage;
            return opEvaluateParallel(abstractPipeline, abstractPipeline.sourceSpliterator(0), intFunction);
        }
        throw new IllegalStateException("stream has already been operated upon or closed");
    }

    /* access modifiers changed from: package-private */
    public final Spliterator<E_OUT> sourceStageSpliterator() {
        AbstractPipeline abstractPipeline = this.sourceStage;
        if (this != abstractPipeline) {
            throw new IllegalStateException();
        } else if (!this.linkedOrConsumed) {
            this.linkedOrConsumed = true;
            if (abstractPipeline.sourceSpliterator != null) {
                Spliterator<?> spliterator = abstractPipeline.sourceSpliterator;
                abstractPipeline.sourceSpliterator = null;
                return spliterator;
            }
            Supplier<? extends Spliterator<?>> supplier = abstractPipeline.sourceSupplier;
            if (supplier != null) {
                Spliterator<E_OUT> spliterator2 = (Spliterator) supplier.get();
                this.sourceStage.sourceSupplier = null;
                return spliterator2;
            }
            throw new IllegalStateException("source already consumed or closed");
        } else {
            throw new IllegalStateException("stream has already been operated upon or closed");
        }
    }

    public final S sequential() {
        this.sourceStage.parallel = false;
        return this;
    }

    public final S parallel() {
        this.sourceStage.parallel = true;
        return this;
    }

    public void close() {
        this.linkedOrConsumed = true;
        this.sourceSupplier = null;
        this.sourceSpliterator = null;
        AbstractPipeline abstractPipeline = this.sourceStage;
        if (abstractPipeline.sourceCloseAction != null) {
            Runnable closeAction = abstractPipeline.sourceCloseAction;
            abstractPipeline.sourceCloseAction = null;
            closeAction.run();
        }
    }

    public S onClose(Runnable closeHandler) {
        Runnable runnable;
        AbstractPipeline abstractPipeline = this.sourceStage;
        Runnable existingHandler = abstractPipeline.sourceCloseAction;
        if (existingHandler == null) {
            runnable = closeHandler;
        } else {
            runnable = Streams.composeWithExceptions(existingHandler, closeHandler);
        }
        abstractPipeline.sourceCloseAction = runnable;
        return this;
    }

    public Spliterator<E_OUT> spliterator() {
        if (!this.linkedOrConsumed) {
            this.linkedOrConsumed = true;
            AbstractPipeline abstractPipeline = this.sourceStage;
            if (this != abstractPipeline) {
                return wrap(this, new AbstractPipeline$$ExternalSyntheticLambda2(this), isParallel());
            }
            if (abstractPipeline.sourceSpliterator != null) {
                Spliterator<?> spliterator = abstractPipeline.sourceSpliterator;
                abstractPipeline.sourceSpliterator = null;
                return spliterator;
            } else if (abstractPipeline.sourceSupplier != null) {
                Supplier<? extends Spliterator<?>> supplier = abstractPipeline.sourceSupplier;
                abstractPipeline.sourceSupplier = null;
                return lazySpliterator(supplier);
            } else {
                throw new IllegalStateException("source already consumed or closed");
            }
        } else {
            throw new IllegalStateException("stream has already been operated upon or closed");
        }
    }

    /* renamed from: lambda$spliterator$0$java-util-stream-AbstractPipeline  reason: not valid java name */
    public /* synthetic */ Spliterator m1lambda$spliterator$0$javautilstreamAbstractPipeline() {
        return sourceSpliterator(0);
    }

    public final boolean isParallel() {
        return this.sourceStage.parallel;
    }

    /* access modifiers changed from: package-private */
    public final int getStreamFlags() {
        return StreamOpFlag.toStreamFlags(this.combinedFlags);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v10, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v6, resolved type: j$.util.Spliterator<?>} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private j$.util.Spliterator<?> sourceSpliterator(int r9) {
        /*
            r8 = this;
            r0 = 0
            j$.util.stream.AbstractPipeline r1 = r8.sourceStage
            j$.util.Spliterator<?> r2 = r1.sourceSpliterator
            r3 = 0
            if (r2 == 0) goto L_0x000d
            j$.util.Spliterator<?> r0 = r1.sourceSpliterator
            r1.sourceSpliterator = r3
            goto L_0x001c
        L_0x000d:
            j$.util.function.Supplier<? extends j$.util.Spliterator<?>> r1 = r1.sourceSupplier
            if (r1 == 0) goto L_0x0080
            java.lang.Object r1 = r1.get()
            r0 = r1
            j$.util.Spliterator r0 = (j$.util.Spliterator) r0
            j$.util.stream.AbstractPipeline r1 = r8.sourceStage
            r1.sourceSupplier = r3
        L_0x001c:
            boolean r1 = r8.isParallel()
            if (r1 == 0) goto L_0x0075
            j$.util.stream.AbstractPipeline r1 = r8.sourceStage
            boolean r2 = r1.sourceAnyStateful
            if (r2 == 0) goto L_0x0075
            r2 = 1
            j$.util.stream.AbstractPipeline r3 = r8.sourceStage
            j$.util.stream.AbstractPipeline r1 = r1.nextStage
            r4 = r8
        L_0x002e:
            if (r3 == r4) goto L_0x0075
            int r5 = r1.sourceOrOpFlags
            boolean r6 = r1.opIsStateful()
            if (r6 == 0) goto L_0x0064
            r2 = 0
            j$.util.stream.StreamOpFlag r6 = j$.util.stream.StreamOpFlag.SHORT_CIRCUIT
            boolean r6 = r6.isKnown(r5)
            if (r6 == 0) goto L_0x0046
            int r6 = j$.util.stream.StreamOpFlag.IS_SHORT_CIRCUIT
            r6 = r6 ^ -1
            r5 = r5 & r6
        L_0x0046:
            j$.util.Spliterator r0 = r1.opEvaluateParallelLazy(r3, r0)
            r6 = 64
            boolean r6 = r0.hasCharacteristics(r6)
            if (r6 == 0) goto L_0x005b
            int r6 = j$.util.stream.StreamOpFlag.NOT_SIZED
            r6 = r6 ^ -1
            r6 = r6 & r5
            int r7 = j$.util.stream.StreamOpFlag.IS_SIZED
            r6 = r6 | r7
            goto L_0x0063
        L_0x005b:
            int r6 = j$.util.stream.StreamOpFlag.IS_SIZED
            r6 = r6 ^ -1
            r6 = r6 & r5
            int r7 = j$.util.stream.StreamOpFlag.NOT_SIZED
            r6 = r6 | r7
        L_0x0063:
            r5 = r6
        L_0x0064:
            int r6 = r2 + 1
            r1.depth = r2
            int r2 = r3.combinedFlags
            int r2 = j$.util.stream.StreamOpFlag.combineOpFlags(r5, r2)
            r1.combinedFlags = r2
            r3 = r1
            j$.util.stream.AbstractPipeline r1 = r1.nextStage
            r2 = r6
            goto L_0x002e
        L_0x0075:
            if (r9 == 0) goto L_0x007f
            int r1 = r8.combinedFlags
            int r1 = j$.util.stream.StreamOpFlag.combineOpFlags(r9, r1)
            r8.combinedFlags = r1
        L_0x007f:
            return r0
        L_0x0080:
            java.lang.IllegalStateException r1 = new java.lang.IllegalStateException
            java.lang.String r2 = "source already consumed or closed"
            r1.<init>(r2)
            goto L_0x0089
        L_0x0088:
            throw r1
        L_0x0089:
            goto L_0x0088
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.stream.AbstractPipeline.sourceSpliterator(int):j$.util.Spliterator");
    }

    /* access modifiers changed from: package-private */
    public final StreamShape getSourceShape() {
        AbstractPipeline p = this;
        while (p.depth > 0) {
            p = p.previousStage;
        }
        return p.getOutputShape();
    }

    /* access modifiers changed from: package-private */
    public final <P_IN> long exactOutputSizeIfKnown(Spliterator<P_IN> spliterator) {
        if (StreamOpFlag.SIZED.isKnown(getStreamAndOpFlags())) {
            return spliterator.getExactSizeIfKnown();
        }
        return -1;
    }

    /* access modifiers changed from: package-private */
    public final <P_IN, S extends Sink<E_OUT>> S wrapAndCopyInto(S sink, Spliterator<P_IN> spliterator) {
        sink.getClass();
        copyInto(wrapSink((Sink) sink), spliterator);
        return sink;
    }

    /* access modifiers changed from: package-private */
    public final <P_IN> void copyInto(Sink<P_IN> wrappedSink, Spliterator<P_IN> spliterator) {
        wrappedSink.getClass();
        if (!StreamOpFlag.SHORT_CIRCUIT.isKnown(getStreamAndOpFlags())) {
            wrappedSink.begin(spliterator.getExactSizeIfKnown());
            spliterator.forEachRemaining(wrappedSink);
            wrappedSink.end();
            return;
        }
        copyIntoWithCancel(wrappedSink, spliterator);
    }

    /* access modifiers changed from: package-private */
    public final <P_IN> void copyIntoWithCancel(Sink<P_IN> wrappedSink, Spliterator<P_IN> spliterator) {
        AbstractPipeline p = this;
        while (p.depth > 0) {
            p = p.previousStage;
        }
        wrappedSink.begin(spliterator.getExactSizeIfKnown());
        p.forEachWithCancel(spliterator, wrappedSink);
        wrappedSink.end();
    }

    /* access modifiers changed from: package-private */
    public final int getStreamAndOpFlags() {
        return this.combinedFlags;
    }

    /* access modifiers changed from: package-private */
    public final boolean isOrdered() {
        return StreamOpFlag.ORDERED.isKnown(this.combinedFlags);
    }

    /* access modifiers changed from: package-private */
    public final <P_IN> Sink<P_IN> wrapSink(Sink<E_OUT> sink) {
        sink.getClass();
        AbstractPipeline p = this;
        Sink<E_OUT> sink2 = sink;
        while (p.depth > 0) {
            Sink<E_OUT> opWrapSink = p.opWrapSink(p.previousStage.combinedFlags, sink2);
            p = p.previousStage;
            sink2 = opWrapSink;
        }
        return sink2;
    }

    /* access modifiers changed from: package-private */
    public final <P_IN> Spliterator<E_OUT> wrapSpliterator(Spliterator<P_IN> spliterator) {
        if (this.depth == 0) {
            return spliterator;
        }
        return wrap(this, new AbstractPipeline$$ExternalSyntheticLambda1(spliterator), isParallel());
    }

    static /* synthetic */ Spliterator lambda$wrapSpliterator$1(Spliterator sourceSpliterator2) {
        return sourceSpliterator2;
    }

    /* access modifiers changed from: package-private */
    public final <P_IN> Node<E_OUT> evaluate(Spliterator<P_IN> spliterator, boolean flatten, IntFunction<E_OUT[]> intFunction) {
        if (isParallel()) {
            return evaluateToNode(this, spliterator, flatten, intFunction);
        }
        return ((Node.Builder) wrapAndCopyInto(makeNodeBuilder(exactOutputSizeIfKnown(spliterator), intFunction), spliterator)).build();
    }

    /* access modifiers changed from: package-private */
    public <P_IN> Node<E_OUT> opEvaluateParallel(PipelineHelper<E_OUT> pipelineHelper, Spliterator<P_IN> spliterator, IntFunction<E_OUT[]> intFunction) {
        throw new UnsupportedOperationException("Parallel evaluation is not supported");
    }

    static /* synthetic */ Object[] lambda$opEvaluateParallelLazy$2(int i) {
        return new Object[i];
    }

    /* access modifiers changed from: package-private */
    public <P_IN> Spliterator<E_OUT> opEvaluateParallelLazy(PipelineHelper<E_OUT> helper, Spliterator<P_IN> spliterator) {
        return opEvaluateParallel(helper, spliterator, AbstractPipeline$$ExternalSyntheticLambda0.INSTANCE).spliterator();
    }
}
