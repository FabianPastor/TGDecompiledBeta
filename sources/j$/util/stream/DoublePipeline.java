package j$.util.stream;

import j$.util.DoubleSummaryStatistics;
import j$.util.OptionalDouble;
import j$.util.PrimitiveIterator;
import j$.util.Spliterator;
import j$.util.Spliterators;
import j$.util.function.BiConsumer;
import j$.util.function.DoubleBinaryOperator;
import j$.util.function.DoubleConsumer;
import j$.util.function.DoubleFunction;
import j$.util.function.DoublePredicate;
import j$.util.function.DoubleToIntFunction;
import j$.util.function.DoubleToLongFunction;
import j$.util.function.DoubleUnaryOperator;
import j$.util.function.IntFunction;
import j$.util.function.ObjDoubleConsumer;
import j$.util.function.Supplier;
import j$.util.stream.IntPipeline;
import j$.util.stream.LongPipeline;
import j$.util.stream.MatchOps;
import j$.util.stream.Node;
import j$.util.stream.ReferencePipeline;
import j$.util.stream.Sink;
import j$.util.stream.StreamSpliterators;

abstract class DoublePipeline<E_IN> extends AbstractPipeline<E_IN, Double, DoubleStream> implements DoubleStream {
    public /* bridge */ /* synthetic */ DoubleStream parallel() {
        return (DoubleStream) super.parallel();
    }

    public /* bridge */ /* synthetic */ DoubleStream sequential() {
        return (DoubleStream) super.sequential();
    }

    DoublePipeline(Supplier<? extends Spliterator<Double>> supplier, int sourceFlags, boolean parallel) {
        super((Supplier<? extends Spliterator<?>>) supplier, sourceFlags, parallel);
    }

    DoublePipeline(Spliterator<Double> spliterator, int sourceFlags, boolean parallel) {
        super((Spliterator<?>) spliterator, sourceFlags, parallel);
    }

    DoublePipeline(AbstractPipeline<?, E_IN, ?> upstream, int opFlags) {
        super(upstream, opFlags);
    }

    private static DoubleConsumer adapt(Sink<Double> sink) {
        if (sink instanceof DoubleConsumer) {
            return (DoubleConsumer) sink;
        }
        if (Tripwire.ENABLED) {
            Tripwire.trip(AbstractPipeline.class, "using DoubleStream.adapt(Sink<Double> s)");
        }
        sink.getClass();
        return new DoublePipeline$$ExternalSyntheticLambda6(sink);
    }

    /* access modifiers changed from: private */
    public static Spliterator.OfDouble adapt(Spliterator<Double> spliterator) {
        if (spliterator instanceof Spliterator.OfDouble) {
            return (Spliterator.OfDouble) spliterator;
        }
        if (Tripwire.ENABLED) {
            Tripwire.trip(AbstractPipeline.class, "using DoubleStream.adapt(Spliterator<Double> s)");
        }
        throw new UnsupportedOperationException("DoubleStream.adapt(Spliterator<Double> s)");
    }

    /* access modifiers changed from: package-private */
    public final StreamShape getOutputShape() {
        return StreamShape.DOUBLE_VALUE;
    }

    /* access modifiers changed from: package-private */
    public final <P_IN> Node<Double> evaluateToNode(PipelineHelper<Double> helper, Spliterator<P_IN> spliterator, boolean flattenTree, IntFunction<Double[]> intFunction) {
        return Nodes.collectDouble(helper, spliterator, flattenTree);
    }

    /* access modifiers changed from: package-private */
    public final <P_IN> Spliterator<Double> wrap(PipelineHelper<Double> ph, Supplier<Spliterator<P_IN>> supplier, boolean isParallel) {
        return new StreamSpliterators.DoubleWrappingSpliterator(ph, supplier, isParallel);
    }

    /* access modifiers changed from: package-private */
    public final Spliterator.OfDouble lazySpliterator(Supplier<? extends Spliterator<Double>> supplier) {
        return new StreamSpliterators.DelegatingSpliterator.OfDouble(supplier);
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:1:0x0008 A[LOOP:0: B:1:0x0008->B:4:0x0012, LOOP_START] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void forEachWithCancel(j$.util.Spliterator<java.lang.Double> r4, j$.util.stream.Sink<java.lang.Double> r5) {
        /*
            r3 = this;
            j$.util.Spliterator$OfDouble r0 = adapt((j$.util.Spliterator<java.lang.Double>) r4)
            j$.util.function.DoubleConsumer r1 = adapt((j$.util.stream.Sink<java.lang.Double>) r5)
        L_0x0008:
            boolean r2 = r5.cancellationRequested()
            if (r2 != 0) goto L_0x0014
            boolean r2 = r0.tryAdvance((j$.util.function.DoubleConsumer) r1)
            if (r2 != 0) goto L_0x0008
        L_0x0014:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.stream.DoublePipeline.forEachWithCancel(j$.util.Spliterator, j$.util.stream.Sink):void");
    }

    /* access modifiers changed from: package-private */
    public final Node.Builder<Double> makeNodeBuilder(long exactSizeIfKnown, IntFunction<Double[]> intFunction) {
        return Nodes.doubleBuilder(exactSizeIfKnown);
    }

    public final PrimitiveIterator.OfDouble iterator() {
        return Spliterators.iterator(spliterator());
    }

    public final Spliterator.OfDouble spliterator() {
        return adapt((Spliterator<Double>) super.spliterator());
    }

    public final Stream<Double> boxed() {
        return mapToObj(DoublePipeline$$ExternalSyntheticLambda7.INSTANCE);
    }

    public final DoubleStream map(DoubleUnaryOperator mapper) {
        mapper.getClass();
        final DoubleUnaryOperator doubleUnaryOperator = mapper;
        return new StatelessOp<Double>(this, StreamShape.DOUBLE_VALUE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) {
            /* access modifiers changed from: package-private */
            public Sink<Double> opWrapSink(int flags, Sink<Double> sink) {
                return new Sink.ChainedDouble<Double>(sink) {
                    public void accept(double t) {
                        this.downstream.accept(doubleUnaryOperator.applyAsDouble(t));
                    }
                };
            }
        };
    }

    public final <U> Stream<U> mapToObj(DoubleFunction<? extends U> doubleFunction) {
        doubleFunction.getClass();
        final DoubleFunction<? extends U> doubleFunction2 = doubleFunction;
        return new ReferencePipeline.StatelessOp<Double, U>(this, StreamShape.DOUBLE_VALUE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) {
            /* access modifiers changed from: package-private */
            public Sink<Double> opWrapSink(int flags, Sink<U> sink) {
                return new Sink.ChainedDouble<U>(sink) {
                    public void accept(double t) {
                        this.downstream.accept(doubleFunction2.apply(t));
                    }
                };
            }
        };
    }

    public final IntStream mapToInt(DoubleToIntFunction mapper) {
        mapper.getClass();
        final DoubleToIntFunction doubleToIntFunction = mapper;
        return new IntPipeline.StatelessOp<Double>(this, StreamShape.DOUBLE_VALUE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) {
            /* access modifiers changed from: package-private */
            public Sink<Double> opWrapSink(int flags, Sink<Integer> sink) {
                return new Sink.ChainedDouble<Integer>(sink) {
                    public void accept(double t) {
                        this.downstream.accept(doubleToIntFunction.applyAsInt(t));
                    }
                };
            }
        };
    }

    public final LongStream mapToLong(DoubleToLongFunction mapper) {
        mapper.getClass();
        final DoubleToLongFunction doubleToLongFunction = mapper;
        return new LongPipeline.StatelessOp<Double>(this, StreamShape.DOUBLE_VALUE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) {
            /* access modifiers changed from: package-private */
            public Sink<Double> opWrapSink(int flags, Sink<Long> sink) {
                return new Sink.ChainedDouble<Long>(sink) {
                    public void accept(double t) {
                        this.downstream.accept(doubleToLongFunction.applyAsLong(t));
                    }
                };
            }
        };
    }

    public final DoubleStream flatMap(DoubleFunction<? extends DoubleStream> doubleFunction) {
        final DoubleFunction<? extends DoubleStream> doubleFunction2 = doubleFunction;
        return new StatelessOp<Double>(this, StreamShape.DOUBLE_VALUE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT | StreamOpFlag.NOT_SIZED) {
            /* access modifiers changed from: package-private */
            public Sink<Double> opWrapSink(int flags, Sink<Double> sink) {
                return new Sink.ChainedDouble<Double>(sink) {
                    public void begin(long size) {
                        this.downstream.begin(-1);
                    }

                    public void accept(double t) {
                        DoubleStream result = (DoubleStream) doubleFunction2.apply(t);
                        if (result != null) {
                            try {
                                result.sequential().forEach(new DoublePipeline$5$1$$ExternalSyntheticLambda0(this));
                            } catch (Throwable th) {
                            }
                        }
                        if (result != null) {
                            result.close();
                            return;
                        }
                        return;
                        throw th;
                    }

                    /* renamed from: lambda$accept$0$java-util-stream-DoublePipeline$5$1  reason: not valid java name */
                    public /* synthetic */ void m512lambda$accept$0$javautilstreamDoublePipeline$5$1(double i) {
                        this.downstream.accept(i);
                    }
                };
            }
        };
    }

    public DoubleStream unordered() {
        if (!isOrdered()) {
            return this;
        }
        return new StatelessOp<Double>(this, StreamShape.DOUBLE_VALUE, StreamOpFlag.NOT_ORDERED) {
            /* access modifiers changed from: package-private */
            public Sink<Double> opWrapSink(int flags, Sink<Double> sink) {
                return sink;
            }
        };
    }

    public final DoubleStream filter(DoublePredicate predicate) {
        predicate.getClass();
        final DoublePredicate doublePredicate = predicate;
        return new StatelessOp<Double>(this, StreamShape.DOUBLE_VALUE, StreamOpFlag.NOT_SIZED) {
            /* access modifiers changed from: package-private */
            public Sink<Double> opWrapSink(int flags, Sink<Double> sink) {
                return new Sink.ChainedDouble<Double>(sink) {
                    public void begin(long size) {
                        this.downstream.begin(-1);
                    }

                    public void accept(double t) {
                        if (doublePredicate.test(t)) {
                            this.downstream.accept(t);
                        }
                    }
                };
            }
        };
    }

    public final DoubleStream peek(DoubleConsumer action) {
        action.getClass();
        final DoubleConsumer doubleConsumer = action;
        return new StatelessOp<Double>(this, StreamShape.DOUBLE_VALUE, 0) {
            /* access modifiers changed from: package-private */
            public Sink<Double> opWrapSink(int flags, Sink<Double> sink) {
                return new Sink.ChainedDouble<Double>(sink) {
                    public void accept(double t) {
                        doubleConsumer.accept(t);
                        this.downstream.accept(t);
                    }
                };
            }
        };
    }

    public final DoubleStream limit(long maxSize) {
        if (maxSize >= 0) {
            return SliceOps.makeDouble(this, 0, maxSize);
        }
        throw new IllegalArgumentException(Long.toString(maxSize));
    }

    public final DoubleStream skip(long n) {
        if (n < 0) {
            throw new IllegalArgumentException(Long.toString(n));
        } else if (n == 0) {
            return this;
        } else {
            return SliceOps.makeDouble(this, n, -1);
        }
    }

    public final DoubleStream sorted() {
        return SortedOps.makeDouble(this);
    }

    public final DoubleStream distinct() {
        return boxed().distinct().mapToDouble(DoublePipeline$$ExternalSyntheticLambda15.INSTANCE);
    }

    public void forEach(DoubleConsumer consumer) {
        evaluate(ForEachOps.makeDouble(consumer, false));
    }

    public void forEachOrdered(DoubleConsumer consumer) {
        evaluate(ForEachOps.makeDouble(consumer, true));
    }

    static /* synthetic */ double[] lambda$sum$1() {
        return new double[3];
    }

    public final double sum() {
        return Collectors.computeFinalSum((double[]) collect(DoublePipeline$$ExternalSyntheticLambda14.INSTANCE, DoublePipeline$$ExternalSyntheticLambda12.INSTANCE, DoublePipeline$$ExternalSyntheticLambda2.INSTANCE));
    }

    static /* synthetic */ void lambda$sum$2(double[] ll, double d) {
        Collectors.sumWithCompensation(ll, d);
        ll[2] = ll[2] + d;
    }

    static /* synthetic */ void lambda$sum$3(double[] ll, double[] rr) {
        Collectors.sumWithCompensation(ll, rr[0]);
        Collectors.sumWithCompensation(ll, rr[1]);
        ll[2] = ll[2] + rr[2];
    }

    public final OptionalDouble min() {
        return reduce(DoublePipeline$$ExternalSyntheticLambda5.INSTANCE);
    }

    public final OptionalDouble max() {
        return reduce(DoublePipeline$$ExternalSyntheticLambda4.INSTANCE);
    }

    static /* synthetic */ double[] lambda$average$4() {
        return new double[4];
    }

    public final OptionalDouble average() {
        double[] avg = (double[]) collect(DoublePipeline$$ExternalSyntheticLambda13.INSTANCE, DoublePipeline$$ExternalSyntheticLambda11.INSTANCE, DoublePipeline$$ExternalSyntheticLambda1.INSTANCE);
        if (avg[2] > 0.0d) {
            return OptionalDouble.of(Collectors.computeFinalSum(avg) / avg[2]);
        }
        return OptionalDouble.empty();
    }

    static /* synthetic */ void lambda$average$5(double[] ll, double d) {
        ll[2] = ll[2] + 1.0d;
        Collectors.sumWithCompensation(ll, d);
        ll[3] = ll[3] + d;
    }

    static /* synthetic */ void lambda$average$6(double[] ll, double[] rr) {
        Collectors.sumWithCompensation(ll, rr[0]);
        Collectors.sumWithCompensation(ll, rr[1]);
        ll[2] = ll[2] + rr[2];
        ll[3] = ll[3] + rr[3];
    }

    static /* synthetic */ long lambda$count$7(double e) {
        return 1;
    }

    public final long count() {
        return mapToLong(DoublePipeline$$ExternalSyntheticLambda8.INSTANCE).sum();
    }

    public final DoubleSummaryStatistics summaryStatistics() {
        return (DoubleSummaryStatistics) collect(Collectors$$ExternalSyntheticLambda78.INSTANCE, DoublePipeline$$ExternalSyntheticLambda10.INSTANCE, DoublePipeline$$ExternalSyntheticLambda0.INSTANCE);
    }

    public final double reduce(double identity, DoubleBinaryOperator op) {
        return ((Double) evaluate(ReduceOps.makeDouble(identity, op))).doubleValue();
    }

    public final OptionalDouble reduce(DoubleBinaryOperator op) {
        return (OptionalDouble) evaluate(ReduceOps.makeDouble(op));
    }

    public final <R> R collect(Supplier<R> supplier, ObjDoubleConsumer<R> objDoubleConsumer, BiConsumer<R, R> biConsumer) {
        return evaluate(ReduceOps.makeDouble(supplier, objDoubleConsumer, new DoublePipeline$$ExternalSyntheticLambda3(biConsumer)));
    }

    public final boolean anyMatch(DoublePredicate predicate) {
        return ((Boolean) evaluate(MatchOps.makeDouble(predicate, MatchOps.MatchKind.ANY))).booleanValue();
    }

    public final boolean allMatch(DoublePredicate predicate) {
        return ((Boolean) evaluate(MatchOps.makeDouble(predicate, MatchOps.MatchKind.ALL))).booleanValue();
    }

    public final boolean noneMatch(DoublePredicate predicate) {
        return ((Boolean) evaluate(MatchOps.makeDouble(predicate, MatchOps.MatchKind.NONE))).booleanValue();
    }

    public final OptionalDouble findFirst() {
        return (OptionalDouble) evaluate(FindOps.makeDouble(true));
    }

    public final OptionalDouble findAny() {
        return (OptionalDouble) evaluate(FindOps.makeDouble(false));
    }

    static /* synthetic */ Double[] lambda$toArray$9(int x$0) {
        return new Double[x$0];
    }

    public final double[] toArray() {
        return (double[]) Nodes.flattenDouble((Node.OfDouble) evaluateToArrayNode(DoublePipeline$$ExternalSyntheticLambda9.INSTANCE)).asPrimitiveArray();
    }

    static class Head<E_IN> extends DoublePipeline<E_IN> {
        public /* bridge */ /* synthetic */ DoubleStream parallel() {
            return (DoubleStream) DoublePipeline.super.parallel();
        }

        public /* bridge */ /* synthetic */ DoubleStream sequential() {
            return (DoubleStream) DoublePipeline.super.sequential();
        }

        Head(Supplier<? extends Spliterator<Double>> supplier, int sourceFlags, boolean parallel) {
            super(supplier, sourceFlags, parallel);
        }

        Head(Spliterator<Double> spliterator, int sourceFlags, boolean parallel) {
            super(spliterator, sourceFlags, parallel);
        }

        /* access modifiers changed from: package-private */
        public final boolean opIsStateful() {
            throw new UnsupportedOperationException();
        }

        /* access modifiers changed from: package-private */
        public final Sink<E_IN> opWrapSink(int flags, Sink<Double> sink) {
            throw new UnsupportedOperationException();
        }

        public void forEach(DoubleConsumer consumer) {
            if (!isParallel()) {
                DoublePipeline.adapt((Spliterator<Double>) sourceStageSpliterator()).forEachRemaining(consumer);
            } else {
                DoublePipeline.super.forEach(consumer);
            }
        }

        public void forEachOrdered(DoubleConsumer consumer) {
            if (!isParallel()) {
                DoublePipeline.adapt((Spliterator<Double>) sourceStageSpliterator()).forEachRemaining(consumer);
            } else {
                DoublePipeline.super.forEachOrdered(consumer);
            }
        }
    }

    static abstract class StatelessOp<E_IN> extends DoublePipeline<E_IN> {
        static final /* synthetic */ boolean $assertionsDisabled = true;

        public /* bridge */ /* synthetic */ DoubleStream parallel() {
            return (DoubleStream) DoublePipeline.super.parallel();
        }

        public /* bridge */ /* synthetic */ DoubleStream sequential() {
            return (DoubleStream) DoublePipeline.super.sequential();
        }

        StatelessOp(AbstractPipeline<?, E_IN, ?> upstream, StreamShape inputShape, int opFlags) {
            super(upstream, opFlags);
            if (!$assertionsDisabled && upstream.getOutputShape() != inputShape) {
                throw new AssertionError();
            }
        }

        /* access modifiers changed from: package-private */
        public final boolean opIsStateful() {
            return false;
        }
    }

    static abstract class StatefulOp<E_IN> extends DoublePipeline<E_IN> {
        static final /* synthetic */ boolean $assertionsDisabled = true;

        /* access modifiers changed from: package-private */
        public abstract <P_IN> Node<Double> opEvaluateParallel(PipelineHelper<Double> pipelineHelper, Spliterator<P_IN> spliterator, IntFunction<Double[]> intFunction);

        public /* bridge */ /* synthetic */ DoubleStream parallel() {
            return (DoubleStream) DoublePipeline.super.parallel();
        }

        public /* bridge */ /* synthetic */ DoubleStream sequential() {
            return (DoubleStream) DoublePipeline.super.sequential();
        }

        StatefulOp(AbstractPipeline<?, E_IN, ?> upstream, StreamShape inputShape, int opFlags) {
            super(upstream, opFlags);
            if (!$assertionsDisabled && upstream.getOutputShape() != inputShape) {
                throw new AssertionError();
            }
        }

        /* access modifiers changed from: package-private */
        public final boolean opIsStateful() {
            return true;
        }
    }
}
