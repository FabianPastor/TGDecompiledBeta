package j$.util.stream;

import j$.util.function.Supplier;

interface TerminalSink<T, R> extends Sink<T>, Supplier<R> {
}
