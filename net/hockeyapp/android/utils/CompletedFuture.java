package net.hockeyapp.android.utils;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class CompletedFuture<T> implements Future<T> {
    private final T mResult;

    public CompletedFuture(T result) {
        this.mResult = result;
    }

    public boolean cancel(boolean b) {
        return false;
    }

    public boolean isCancelled() {
        return false;
    }

    public boolean isDone() {
        return true;
    }

    public T get() throws InterruptedException, ExecutionException {
        return this.mResult;
    }

    public T get(long l, TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
        return this.mResult;
    }
}
