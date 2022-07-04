package j$.util.stream;

import j$.util.Optional;
import j$.util.OptionalDouble;
import j$.util.OptionalInt;
import j$.util.OptionalLong;
import j$.util.Spliterator;
import j$.util.function.Consumer;
import j$.util.function.DoubleConsumer;
import j$.util.function.IntConsumer;
import j$.util.function.LongConsumer;
import j$.util.function.Predicate;
import j$.util.function.Supplier;
import j$.util.stream.Sink;

final class FindOps {
    private FindOps() {
    }

    public static <T> TerminalOp<T, Optional<T>> makeRef(boolean mustFindFirst) {
        return new FindOp(mustFindFirst, StreamShape.REFERENCE, Optional.empty(), FindOps$$ExternalSyntheticLambda0.INSTANCE, FindOps$$ExternalSyntheticLambda7.INSTANCE);
    }

    public static TerminalOp<Integer, OptionalInt> makeInt(boolean mustFindFirst) {
        return new FindOp(mustFindFirst, StreamShape.INT_VALUE, OptionalInt.empty(), FindOps$$ExternalSyntheticLambda2.INSTANCE, FindOps$$ExternalSyntheticLambda5.INSTANCE);
    }

    public static TerminalOp<Long, OptionalLong> makeLong(boolean mustFindFirst) {
        return new FindOp(mustFindFirst, StreamShape.LONG_VALUE, OptionalLong.empty(), FindOps$$ExternalSyntheticLambda3.INSTANCE, FindOps$$ExternalSyntheticLambda6.INSTANCE);
    }

    public static TerminalOp<Double, OptionalDouble> makeDouble(boolean mustFindFirst) {
        return new FindOp(mustFindFirst, StreamShape.DOUBLE_VALUE, OptionalDouble.empty(), FindOps$$ExternalSyntheticLambda1.INSTANCE, FindOps$$ExternalSyntheticLambda4.INSTANCE);
    }

    private static final class FindOp<T, O> implements TerminalOp<T, O> {
        final O emptyValue;
        final boolean mustFindFirst;
        final Predicate<O> presentPredicate;
        private final StreamShape shape;
        final Supplier<TerminalSink<T, O>> sinkSupplier;

        FindOp(boolean mustFindFirst2, StreamShape shape2, O emptyValue2, Predicate<O> predicate, Supplier<TerminalSink<T, O>> supplier) {
            this.mustFindFirst = mustFindFirst2;
            this.shape = shape2;
            this.emptyValue = emptyValue2;
            this.presentPredicate = predicate;
            this.sinkSupplier = supplier;
        }

        public int getOpFlags() {
            return StreamOpFlag.IS_SHORT_CIRCUIT | (this.mustFindFirst ? 0 : StreamOpFlag.NOT_ORDERED);
        }

        public StreamShape inputShape() {
            return this.shape;
        }

        public <S> O evaluateSequential(PipelineHelper<T> helper, Spliterator<S> spliterator) {
            O result = ((TerminalSink) helper.wrapAndCopyInto(this.sinkSupplier.get(), spliterator)).get();
            return result != null ? result : this.emptyValue;
        }

        public <P_IN> O evaluateParallel(PipelineHelper<T> helper, Spliterator<P_IN> spliterator) {
            return new FindTask(this, helper, spliterator).invoke();
        }
    }

    private static abstract class FindSink<T, O> implements TerminalSink<T, O> {
        boolean hasValue;
        T value;

        public /* synthetic */ void accept(double d) {
            Sink.CC.$default$accept((Sink) this, d);
        }

        public /* synthetic */ void accept(int i) {
            Sink.CC.$default$accept((Sink) this, i);
        }

        public /* synthetic */ void accept(long j) {
            Sink.CC.$default$accept((Sink) this, j);
        }

        public /* synthetic */ Consumer andThen(Consumer consumer) {
            return Consumer.CC.$default$andThen(this, consumer);
        }

        public /* synthetic */ void begin(long j) {
            Sink.CC.$default$begin(this, j);
        }

        public /* synthetic */ void end() {
            Sink.CC.$default$end(this);
        }

        FindSink() {
        }

        public void accept(T value2) {
            if (!this.hasValue) {
                this.hasValue = true;
                this.value = value2;
            }
        }

        public boolean cancellationRequested() {
            return this.hasValue;
        }

        static final class OfRef<T> extends FindSink<T, Optional<T>> {
            OfRef() {
            }

            public Optional<T> get() {
                if (this.hasValue) {
                    return Optional.of(this.value);
                }
                return null;
            }
        }

        static final class OfInt extends FindSink<Integer, OptionalInt> implements Sink.OfInt {
            public /* synthetic */ IntConsumer andThen(IntConsumer intConsumer) {
                return IntConsumer.CC.$default$andThen(this, intConsumer);
            }

            OfInt() {
            }

            public /* bridge */ /* synthetic */ void accept(Integer num) {
                super.accept(num);
            }

            public void accept(int value) {
                accept(Integer.valueOf(value));
            }

            public OptionalInt get() {
                if (this.hasValue) {
                    return OptionalInt.of(((Integer) this.value).intValue());
                }
                return null;
            }
        }

        static final class OfLong extends FindSink<Long, OptionalLong> implements Sink.OfLong {
            public /* synthetic */ LongConsumer andThen(LongConsumer longConsumer) {
                return LongConsumer.CC.$default$andThen(this, longConsumer);
            }

            OfLong() {
            }

            public /* bridge */ /* synthetic */ void accept(Long l) {
                super.accept(l);
            }

            public void accept(long value) {
                accept(Long.valueOf(value));
            }

            public OptionalLong get() {
                if (this.hasValue) {
                    return OptionalLong.of(((Long) this.value).longValue());
                }
                return null;
            }
        }

        static final class OfDouble extends FindSink<Double, OptionalDouble> implements Sink.OfDouble {
            public /* synthetic */ DoubleConsumer andThen(DoubleConsumer doubleConsumer) {
                return DoubleConsumer.CC.$default$andThen(this, doubleConsumer);
            }

            OfDouble() {
            }

            public /* bridge */ /* synthetic */ void accept(Double d) {
                super.accept(d);
            }

            public void accept(double value) {
                accept(Double.valueOf(value));
            }

            public OptionalDouble get() {
                if (this.hasValue) {
                    return OptionalDouble.of(((Double) this.value).doubleValue());
                }
                return null;
            }
        }
    }

    private static final class FindTask<P_IN, P_OUT, O> extends AbstractShortCircuitTask<P_IN, P_OUT, O, FindTask<P_IN, P_OUT, O>> {
        private final FindOp<P_OUT, O> op;

        FindTask(FindOp<P_OUT, O> op2, PipelineHelper<P_OUT> helper, Spliterator<P_IN> spliterator) {
            super(helper, spliterator);
            this.op = op2;
        }

        FindTask(FindTask<P_IN, P_OUT, O> parent, Spliterator<P_IN> spliterator) {
            super(parent, spliterator);
            this.op = parent.op;
        }

        /* access modifiers changed from: protected */
        public FindTask<P_IN, P_OUT, O> makeChild(Spliterator<P_IN> spliterator) {
            return new FindTask<>(this, spliterator);
        }

        /* access modifiers changed from: protected */
        public O getEmptyResult() {
            return this.op.emptyValue;
        }

        private void foundResult(O answer) {
            if (isLeftmostNode()) {
                shortCircuit(answer);
            } else {
                cancelLaterNodes();
            }
        }

        /* access modifiers changed from: protected */
        public O doLeaf() {
            O result = ((TerminalSink) this.helper.wrapAndCopyInto(this.op.sinkSupplier.get(), this.spliterator)).get();
            if (!this.op.mustFindFirst) {
                if (result != null) {
                    shortCircuit(result);
                }
                return null;
            } else if (result == null) {
                return null;
            } else {
                foundResult(result);
                return result;
            }
        }

        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onCompletion(java.util.concurrent.CountedCompleter<?> r5) {
            /*
                r4 = this;
                j$.util.stream.FindOps$FindOp<P_OUT, O> r0 = r4.op
                boolean r0 = r0.mustFindFirst
                if (r0 == 0) goto L_0x002b
                j$.util.stream.AbstractTask r0 = r4.leftChild
                j$.util.stream.FindOps$FindTask r0 = (j$.util.stream.FindOps.FindTask) r0
                r1 = 0
            L_0x000b:
                if (r0 == r1) goto L_0x002b
                java.lang.Object r2 = r0.getLocalResult()
                if (r2 == 0) goto L_0x0024
                j$.util.stream.FindOps$FindOp<P_OUT, O> r3 = r4.op
                j$.util.function.Predicate<O> r3 = r3.presentPredicate
                boolean r3 = r3.test(r2)
                if (r3 == 0) goto L_0x0024
                r4.setLocalResult(r2)
                r4.foundResult(r2)
                goto L_0x002b
            L_0x0024:
                r1 = r0
                j$.util.stream.AbstractTask r2 = r4.rightChild
                r0 = r2
                j$.util.stream.FindOps$FindTask r0 = (j$.util.stream.FindOps.FindTask) r0
                goto L_0x000b
            L_0x002b:
                super.onCompletion(r5)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: j$.util.stream.FindOps.FindTask.onCompletion(java.util.concurrent.CountedCompleter):void");
        }
    }
}
