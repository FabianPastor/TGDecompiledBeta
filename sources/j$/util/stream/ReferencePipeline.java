package j$.util.stream;

import j$.util.Optional;
import j$.util.Spliterator;
import j$.util.Spliterators;
import j$.util.function.BiConsumer;
import j$.util.function.BinaryOperator;
import j$.util.function.Consumer;
import j$.util.function.DoubleConsumer;
import j$.util.function.Function;
import j$.util.function.IntConsumer;
import j$.util.function.IntFunction;
import j$.util.function.LongConsumer;
import j$.util.function.Predicate;
import j$.util.function.Supplier;
import j$.util.function.ToDoubleFunction;
import j$.util.function.ToIntFunction;
import j$.util.function.ToLongFunction;
import j$.util.stream.Collector;
import j$.util.stream.DoublePipeline;
import j$.util.stream.IntPipeline;
import j$.util.stream.LongPipeline;
import j$.util.stream.Node;
import j$.util.stream.Sink;
import j$.util.stream.StreamSpliterators;
import java.util.Comparator;
import java.util.Iterator;

abstract class ReferencePipeline<P_IN, P_OUT> extends AbstractPipeline<P_IN, P_OUT, Stream<P_OUT>> implements Stream<P_OUT> {
    ReferencePipeline(Supplier<? extends Spliterator<?>> supplier, int sourceFlags, boolean parallel) {
        super(supplier, sourceFlags, parallel);
    }

    ReferencePipeline(Spliterator<?> spliterator, int sourceFlags, boolean parallel) {
        super(spliterator, sourceFlags, parallel);
    }

    ReferencePipeline(AbstractPipeline<?, P_IN, ?> upstream, int opFlags) {
        super(upstream, opFlags);
    }

    /* access modifiers changed from: package-private */
    public final StreamShape getOutputShape() {
        return StreamShape.REFERENCE;
    }

    /* access modifiers changed from: package-private */
    public final <P_IN> Node<P_OUT> evaluateToNode(PipelineHelper<P_OUT> helper, Spliterator<P_IN> spliterator, boolean flattenTree, IntFunction<P_OUT[]> intFunction) {
        return Nodes.collect(helper, spliterator, flattenTree, intFunction);
    }

    /* access modifiers changed from: package-private */
    public final <P_IN> Spliterator<P_OUT> wrap(PipelineHelper<P_OUT> ph, Supplier<Spliterator<P_IN>> supplier, boolean isParallel) {
        return new StreamSpliterators.WrappingSpliterator(ph, supplier, isParallel);
    }

    /* access modifiers changed from: package-private */
    public final Spliterator<P_OUT> lazySpliterator(Supplier<? extends Spliterator<P_OUT>> supplier) {
        return new StreamSpliterators.DelegatingSpliterator(supplier);
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:0:0x0000 A[LOOP:0: B:0:0x0000->B:3:0x000a, LOOP_START, MTH_ENTER_BLOCK] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void forEachWithCancel(j$.util.Spliterator<P_OUT> r2, j$.util.stream.Sink<P_OUT> r3) {
        /*
            r1 = this;
        L_0x0000:
            boolean r0 = r3.cancellationRequested()
            if (r0 != 0) goto L_0x000c
            boolean r0 = r2.tryAdvance(r3)
            if (r0 != 0) goto L_0x0000
        L_0x000c:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.stream.ReferencePipeline.forEachWithCancel(j$.util.Spliterator, j$.util.stream.Sink):void");
    }

    /* access modifiers changed from: package-private */
    public final Node.Builder<P_OUT> makeNodeBuilder(long exactSizeIfKnown, IntFunction<P_OUT[]> intFunction) {
        return Nodes.builder(exactSizeIfKnown, intFunction);
    }

    public final Iterator<P_OUT> iterator() {
        return Spliterators.iterator(spliterator());
    }

    public Stream<P_OUT> unordered() {
        if (!isOrdered()) {
            return this;
        }
        return new StatelessOp<P_OUT, P_OUT>(this, StreamShape.REFERENCE, StreamOpFlag.NOT_ORDERED) {
            /* access modifiers changed from: package-private */
            public Sink<P_OUT> opWrapSink(int flags, Sink<P_OUT> sink) {
                return sink;
            }
        };
    }

    public final Stream<P_OUT> filter(Predicate<? super P_OUT> predicate) {
        predicate.getClass();
        final Predicate<? super P_OUT> predicate2 = predicate;
        return new StatelessOp<P_OUT, P_OUT>(this, StreamShape.REFERENCE, StreamOpFlag.NOT_SIZED) {
            /* access modifiers changed from: package-private */
            public Sink<P_OUT> opWrapSink(int flags, Sink<P_OUT> sink) {
                return new Sink.ChainedReference<P_OUT, P_OUT>(sink) {
                    public void begin(long size) {
                        this.downstream.begin(-1);
                    }

                    public void accept(P_OUT u) {
                        if (predicate2.test(u)) {
                            this.downstream.accept(u);
                        }
                    }
                };
            }
        };
    }

    public final <R> Stream<R> map(Function<? super P_OUT, ? extends R> function) {
        function.getClass();
        final Function<? super P_OUT, ? extends R> function2 = function;
        return new StatelessOp<P_OUT, R>(this, StreamShape.REFERENCE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) {
            /* access modifiers changed from: package-private */
            public Sink<P_OUT> opWrapSink(int flags, Sink<R> sink) {
                return new Sink.ChainedReference<P_OUT, R>(sink) {
                    public void accept(P_OUT u) {
                        this.downstream.accept(function2.apply(u));
                    }
                };
            }
        };
    }

    public final IntStream mapToInt(ToIntFunction<? super P_OUT> toIntFunction) {
        toIntFunction.getClass();
        final ToIntFunction<? super P_OUT> toIntFunction2 = toIntFunction;
        return new IntPipeline.StatelessOp<P_OUT>(this, StreamShape.REFERENCE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) {
            /* access modifiers changed from: package-private */
            public Sink<P_OUT> opWrapSink(int flags, Sink<Integer> sink) {
                return new Sink.ChainedReference<P_OUT, Integer>(sink) {
                    public void accept(P_OUT u) {
                        this.downstream.accept(toIntFunction2.applyAsInt(u));
                    }
                };
            }
        };
    }

    public final LongStream mapToLong(ToLongFunction<? super P_OUT> toLongFunction) {
        toLongFunction.getClass();
        final ToLongFunction<? super P_OUT> toLongFunction2 = toLongFunction;
        return new LongPipeline.StatelessOp<P_OUT>(this, StreamShape.REFERENCE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) {
            /* access modifiers changed from: package-private */
            public Sink<P_OUT> opWrapSink(int flags, Sink<Long> sink) {
                return new Sink.ChainedReference<P_OUT, Long>(sink) {
                    public void accept(P_OUT u) {
                        this.downstream.accept(toLongFunction2.applyAsLong(u));
                    }
                };
            }
        };
    }

    public final DoubleStream mapToDouble(ToDoubleFunction<? super P_OUT> toDoubleFunction) {
        toDoubleFunction.getClass();
        final ToDoubleFunction<? super P_OUT> toDoubleFunction2 = toDoubleFunction;
        return new DoublePipeline.StatelessOp<P_OUT>(this, StreamShape.REFERENCE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) {
            /* access modifiers changed from: package-private */
            public Sink<P_OUT> opWrapSink(int flags, Sink<Double> sink) {
                return new Sink.ChainedReference<P_OUT, Double>(sink) {
                    public void accept(P_OUT u) {
                        this.downstream.accept(toDoubleFunction2.applyAsDouble(u));
                    }
                };
            }
        };
    }

    public final <R> Stream<R> flatMap(Function<? super P_OUT, ? extends Stream<? extends R>> function) {
        function.getClass();
        final Function<? super P_OUT, ? extends Stream<? extends R>> function2 = function;
        return new StatelessOp<P_OUT, R>(this, StreamShape.REFERENCE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT | StreamOpFlag.NOT_SIZED) {
            /* access modifiers changed from: package-private */
            public Sink<P_OUT> opWrapSink(int flags, Sink<R> sink) {
                return new Sink.ChainedReference<P_OUT, R>(sink) {
                    public void begin(long size) {
                        this.downstream.begin(-1);
                    }

                    public void accept(P_OUT u) {
                        Stream stream = (Stream) function2.apply(u);
                        if (stream != null) {
                            try {
                                ((Stream) stream.sequential()).forEach(this.downstream);
                            } catch (Throwable th) {
                            }
                        }
                        if (stream != null) {
                            stream.close();
                            return;
                        }
                        return;
                        throw th;
                    }
                };
            }
        };
    }

    public final IntStream flatMapToInt(Function<? super P_OUT, ? extends IntStream> function) {
        function.getClass();
        final Function<? super P_OUT, ? extends IntStream> function2 = function;
        return new IntPipeline.StatelessOp<P_OUT>(this, StreamShape.REFERENCE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT | StreamOpFlag.NOT_SIZED) {
            /* access modifiers changed from: package-private */
            public Sink<P_OUT> opWrapSink(int flags, Sink<Integer> sink) {
                return new Sink.ChainedReference<P_OUT, Integer>(sink) {
                    IntConsumer downstreamAsInt;

                    {
                        Sink sink = this.downstream;
                        sink.getClass();
                        this.downstreamAsInt = new IntPipeline$$ExternalSyntheticLambda6(sink);
                    }

                    public void begin(long size) {
                        this.downstream.begin(-1);
                    }

                    public void accept(P_OUT u) {
                        IntStream result = (IntStream) function2.apply(u);
                        if (result != null) {
                            try {
                                result.sequential().forEach(this.downstreamAsInt);
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
                };
            }
        };
    }

    public final DoubleStream flatMapToDouble(Function<? super P_OUT, ? extends DoubleStream> function) {
        function.getClass();
        final Function<? super P_OUT, ? extends DoubleStream> function2 = function;
        return new DoublePipeline.StatelessOp<P_OUT>(this, StreamShape.REFERENCE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT | StreamOpFlag.NOT_SIZED) {
            /* access modifiers changed from: package-private */
            public Sink<P_OUT> opWrapSink(int flags, Sink<Double> sink) {
                return new Sink.ChainedReference<P_OUT, Double>(sink) {
                    DoubleConsumer downstreamAsDouble;

                    {
                        Sink sink = this.downstream;
                        sink.getClass();
                        this.downstreamAsDouble = new DoublePipeline$$ExternalSyntheticLambda6(sink);
                    }

                    public void begin(long size) {
                        this.downstream.begin(-1);
                    }

                    public void accept(P_OUT u) {
                        DoubleStream result = (DoubleStream) function2.apply(u);
                        if (result != null) {
                            try {
                                result.sequential().forEach(this.downstreamAsDouble);
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
                };
            }
        };
    }

    public final LongStream flatMapToLong(Function<? super P_OUT, ? extends LongStream> function) {
        function.getClass();
        final Function<? super P_OUT, ? extends LongStream> function2 = function;
        return new LongPipeline.StatelessOp<P_OUT>(this, StreamShape.REFERENCE, StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT | StreamOpFlag.NOT_SIZED) {
            /* access modifiers changed from: package-private */
            public Sink<P_OUT> opWrapSink(int flags, Sink<Long> sink) {
                return new Sink.ChainedReference<P_OUT, Long>(sink) {
                    LongConsumer downstreamAsLong;

                    {
                        Sink sink = this.downstream;
                        sink.getClass();
                        this.downstreamAsLong = new LongPipeline$$ExternalSyntheticLambda7(sink);
                    }

                    public void begin(long size) {
                        this.downstream.begin(-1);
                    }

                    public void accept(P_OUT u) {
                        LongStream result = (LongStream) function2.apply(u);
                        if (result != null) {
                            try {
                                result.sequential().forEach(this.downstreamAsLong);
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
                };
            }
        };
    }

    public final Stream<P_OUT> peek(Consumer<? super P_OUT> consumer) {
        consumer.getClass();
        final Consumer<? super P_OUT> consumer2 = consumer;
        return new StatelessOp<P_OUT, P_OUT>(this, StreamShape.REFERENCE, 0) {
            /* access modifiers changed from: package-private */
            public Sink<P_OUT> opWrapSink(int flags, Sink<P_OUT> sink) {
                return new Sink.ChainedReference<P_OUT, P_OUT>(sink) {
                    public void accept(P_OUT u) {
                        consumer2.accept(u);
                        this.downstream.accept(u);
                    }
                };
            }
        };
    }

    public final Stream<P_OUT> distinct() {
        return DistinctOps.makeRef(this);
    }

    public final Stream<P_OUT> sorted() {
        return SortedOps.makeRef(this);
    }

    public final Stream<P_OUT> sorted(Comparator<? super P_OUT> comparator) {
        return SortedOps.makeRef(this, comparator);
    }

    public final Stream<P_OUT> limit(long maxSize) {
        if (maxSize >= 0) {
            return SliceOps.makeRef(this, 0, maxSize);
        }
        throw new IllegalArgumentException(Long.toString(maxSize));
    }

    public final Stream<P_OUT> skip(long n) {
        if (n < 0) {
            throw new IllegalArgumentException(Long.toString(n));
        } else if (n == 0) {
            return this;
        } else {
            return SliceOps.makeRef(this, n, -1);
        }
    }

    public void forEach(Consumer<? super P_OUT> consumer) {
        evaluate(ForEachOps.makeRef(consumer, false));
    }

    public void forEachOrdered(Consumer<? super P_OUT> consumer) {
        evaluate(ForEachOps.makeRef(consumer, true));
    }

    public final <A> A[] toArray(IntFunction<A[]> intFunction) {
        IntFunction rawGenerator = intFunction;
        return Nodes.flatten(evaluateToArrayNode(rawGenerator), rawGenerator).asArray(rawGenerator);
    }

    static /* synthetic */ Object[] lambda$toArray$0(int x$0) {
        return new Object[x$0];
    }

    public final Object[] toArray() {
        return toArray(ReferencePipeline$$ExternalSyntheticLambda1.INSTANCE);
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [j$.util.function.Predicate<? super P_OUT>, j$.util.function.Predicate] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final boolean anyMatch(j$.util.function.Predicate<? super P_OUT> r2) {
        /*
            r1 = this;
            j$.util.stream.MatchOps$MatchKind r0 = j$.util.stream.MatchOps.MatchKind.ANY
            j$.util.stream.TerminalOp r0 = j$.util.stream.MatchOps.makeRef(r2, r0)
            java.lang.Object r0 = r1.evaluate(r0)
            java.lang.Boolean r0 = (java.lang.Boolean) r0
            boolean r0 = r0.booleanValue()
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.stream.ReferencePipeline.anyMatch(j$.util.function.Predicate):boolean");
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [j$.util.function.Predicate<? super P_OUT>, j$.util.function.Predicate] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final boolean allMatch(j$.util.function.Predicate<? super P_OUT> r2) {
        /*
            r1 = this;
            j$.util.stream.MatchOps$MatchKind r0 = j$.util.stream.MatchOps.MatchKind.ALL
            j$.util.stream.TerminalOp r0 = j$.util.stream.MatchOps.makeRef(r2, r0)
            java.lang.Object r0 = r1.evaluate(r0)
            java.lang.Boolean r0 = (java.lang.Boolean) r0
            boolean r0 = r0.booleanValue()
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.stream.ReferencePipeline.allMatch(j$.util.function.Predicate):boolean");
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [j$.util.function.Predicate<? super P_OUT>, j$.util.function.Predicate] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final boolean noneMatch(j$.util.function.Predicate<? super P_OUT> r2) {
        /*
            r1 = this;
            j$.util.stream.MatchOps$MatchKind r0 = j$.util.stream.MatchOps.MatchKind.NONE
            j$.util.stream.TerminalOp r0 = j$.util.stream.MatchOps.makeRef(r2, r0)
            java.lang.Object r0 = r1.evaluate(r0)
            java.lang.Boolean r0 = (java.lang.Boolean) r0
            boolean r0 = r0.booleanValue()
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.stream.ReferencePipeline.noneMatch(j$.util.function.Predicate):boolean");
    }

    public final Optional<P_OUT> findFirst() {
        return (Optional) evaluate(FindOps.makeRef(true));
    }

    public final Optional<P_OUT> findAny() {
        return (Optional) evaluate(FindOps.makeRef(false));
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [j$.util.function.BinaryOperator, j$.util.function.BiFunction, j$.util.function.BinaryOperator<P_OUT>] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final P_OUT reduce(P_OUT r2, j$.util.function.BinaryOperator<P_OUT> r3) {
        /*
            r1 = this;
            j$.util.stream.TerminalOp r0 = j$.util.stream.ReduceOps.makeRef(r2, r3, r3)
            java.lang.Object r0 = r1.evaluate(r0)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.stream.ReferencePipeline.reduce(java.lang.Object, j$.util.function.BinaryOperator):java.lang.Object");
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [j$.util.function.BinaryOperator, j$.util.function.BinaryOperator<P_OUT>] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final j$.util.Optional<P_OUT> reduce(j$.util.function.BinaryOperator<P_OUT> r2) {
        /*
            r1 = this;
            j$.util.stream.TerminalOp r0 = j$.util.stream.ReduceOps.makeRef(r2)
            java.lang.Object r0 = r1.evaluate(r0)
            j$.util.Optional r0 = (j$.util.Optional) r0
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.stream.ReferencePipeline.reduce(j$.util.function.BinaryOperator):j$.util.Optional");
    }

    /* JADX WARNING: type inference failed for: r4v0, types: [j$.util.function.BinaryOperator, j$.util.function.BinaryOperator<R>] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final <R> R reduce(R r2, j$.util.function.BiFunction<R, ? super P_OUT, R> r3, j$.util.function.BinaryOperator<R> r4) {
        /*
            r1 = this;
            j$.util.stream.TerminalOp r0 = j$.util.stream.ReduceOps.makeRef(r2, r3, r4)
            java.lang.Object r0 = r1.evaluate(r0)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.stream.ReferencePipeline.reduce(java.lang.Object, j$.util.function.BiFunction, j$.util.function.BinaryOperator):java.lang.Object");
    }

    public final <R, A> R collect(Collector<? super P_OUT, A, R> collector) {
        A container;
        if (!isParallel() || !collector.characteristics().contains(Collector.Characteristics.CONCURRENT) || (isOrdered() && !collector.characteristics().contains(Collector.Characteristics.UNORDERED))) {
            container = evaluate(ReduceOps.makeRef(collector));
        } else {
            container = collector.supplier().get();
            forEach(new ReferencePipeline$$ExternalSyntheticLambda0(collector.accumulator(), container));
        }
        if (collector.characteristics().contains(Collector.Characteristics.IDENTITY_FINISH)) {
            return container;
        }
        return collector.finisher().apply(container);
    }

    public final <R> R collect(Supplier<R> supplier, BiConsumer<R, ? super P_OUT> biConsumer, BiConsumer<R, R> biConsumer2) {
        return evaluate(ReduceOps.makeRef(supplier, biConsumer, biConsumer2));
    }

    public final Optional<P_OUT> max(Comparator<? super P_OUT> comparator) {
        return reduce(BinaryOperator.CC.maxBy(comparator));
    }

    public final Optional<P_OUT> min(Comparator<? super P_OUT> comparator) {
        return reduce(BinaryOperator.CC.minBy(comparator));
    }

    static /* synthetic */ long lambda$count$2(Object e) {
        return 1;
    }

    public final long count() {
        return mapToLong(ReferencePipeline$$ExternalSyntheticLambda2.INSTANCE).sum();
    }

    static class Head<E_IN, E_OUT> extends ReferencePipeline<E_IN, E_OUT> {
        Head(Supplier<? extends Spliterator<?>> supplier, int sourceFlags, boolean parallel) {
            super(supplier, sourceFlags, parallel);
        }

        Head(Spliterator<?> spliterator, int sourceFlags, boolean parallel) {
            super(spliterator, sourceFlags, parallel);
        }

        /* access modifiers changed from: package-private */
        public final boolean opIsStateful() {
            throw new UnsupportedOperationException();
        }

        /* access modifiers changed from: package-private */
        public final Sink<E_IN> opWrapSink(int flags, Sink<E_OUT> sink) {
            throw new UnsupportedOperationException();
        }

        public void forEach(Consumer<? super E_OUT> consumer) {
            if (!isParallel()) {
                sourceStageSpliterator().forEachRemaining(consumer);
            } else {
                ReferencePipeline.super.forEach(consumer);
            }
        }

        public void forEachOrdered(Consumer<? super E_OUT> consumer) {
            if (!isParallel()) {
                sourceStageSpliterator().forEachRemaining(consumer);
            } else {
                ReferencePipeline.super.forEachOrdered(consumer);
            }
        }
    }

    static abstract class StatelessOp<E_IN, E_OUT> extends ReferencePipeline<E_IN, E_OUT> {
        static final /* synthetic */ boolean $assertionsDisabled = true;

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

    static abstract class StatefulOp<E_IN, E_OUT> extends ReferencePipeline<E_IN, E_OUT> {
        static final /* synthetic */ boolean $assertionsDisabled = true;

        /* access modifiers changed from: package-private */
        public abstract <P_IN> Node<E_OUT> opEvaluateParallel(PipelineHelper<E_OUT> pipelineHelper, Spliterator<P_IN> spliterator, IntFunction<E_OUT[]> intFunction);

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
