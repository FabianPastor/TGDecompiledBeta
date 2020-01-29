package drinkless.org.ton;

import drinkless.org.ton.TonApi;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class Client implements Runnable {
    private static final int MAX_EVENTS = 1000;
    private final AtomicLong currentQueryId = new AtomicLong();
    private volatile ExceptionHandler defaultExceptionHandler = null;
    private final long[] eventIds = new long[1000];
    private final TonApi.Object[] events = new TonApi.Object[1000];
    private final ConcurrentHashMap<Long, Handler> handlers = new ConcurrentHashMap<>();
    private volatile boolean isClientDestroyed = false;
    private final long nativeClientId = createNativeClient();
    private final Lock readLock = this.readWriteLock.readLock();
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private volatile boolean stopFlag = false;
    private final Lock writeLock = this.readWriteLock.writeLock();

    public interface ExceptionHandler {
        void onException(Throwable th);
    }

    public interface ResultHandler {
        void onResult(TonApi.Object object);
    }

    private static native long createNativeClient();

    private static native void destroyNativeClient(long j);

    private static native TonApi.Object nativeClientExecute(TonApi.Function function);

    private static native int nativeClientReceive(long j, long[] jArr, TonApi.Object[] objectArr, double d);

    private static native void nativeClientSend(long j, long j2, TonApi.Function function);

    public void send(TonApi.Function function, ResultHandler resultHandler, ExceptionHandler exceptionHandler) {
        if (function != null) {
            this.readLock.lock();
            try {
                if (this.isClientDestroyed) {
                    if (resultHandler != null) {
                        handleResult(new TonApi.Error(500, "Client is closed"), resultHandler, exceptionHandler);
                    }
                    return;
                }
                long incrementAndGet = this.currentQueryId.incrementAndGet();
                this.handlers.put(Long.valueOf(incrementAndGet), new Handler(resultHandler, exceptionHandler));
                nativeClientSend(this.nativeClientId, incrementAndGet, function);
                this.readLock.unlock();
            } finally {
                this.readLock.unlock();
            }
        } else {
            throw new NullPointerException("query is null");
        }
    }

    public void send(TonApi.Function function, ResultHandler resultHandler) {
        send(function, resultHandler, (ExceptionHandler) null);
    }

    public static TonApi.Object execute(TonApi.Function function) {
        if (function != null) {
            return nativeClientExecute(function);
        }
        throw new NullPointerException("query is null");
    }

    public void setUpdatesHandler(ResultHandler resultHandler, ExceptionHandler exceptionHandler) {
        this.handlers.put(0L, new Handler(resultHandler, exceptionHandler));
    }

    public void setUpdatesHandler(ResultHandler resultHandler) {
        setUpdatesHandler(resultHandler, (ExceptionHandler) null);
    }

    public void setDefaultExceptionHandler(ExceptionHandler exceptionHandler) {
        this.defaultExceptionHandler = exceptionHandler;
    }

    public void run() {
        while (!this.stopFlag) {
            receiveQueries(300.0d);
        }
    }

    public static Client create(ResultHandler resultHandler, ExceptionHandler exceptionHandler, ExceptionHandler exceptionHandler2) {
        Client client = new Client(resultHandler, exceptionHandler, exceptionHandler2);
        new Thread(client, "tonlib thread").start();
        return client;
    }

    public void close() {
        this.writeLock.lock();
        try {
            if (!this.isClientDestroyed) {
                this.isClientDestroyed = true;
                while (!this.stopFlag) {
                    Thread.yield();
                }
                while (this.handlers.size() != 1) {
                    receiveQueries(300.0d);
                }
                destroyNativeClient(this.nativeClientId);
                this.writeLock.unlock();
            }
        } finally {
            this.writeLock.unlock();
        }
    }

    private static class Handler {
        final ExceptionHandler exceptionHandler;
        final ResultHandler resultHandler;

        Handler(ResultHandler resultHandler2, ExceptionHandler exceptionHandler2) {
            this.resultHandler = resultHandler2;
            this.exceptionHandler = exceptionHandler2;
        }
    }

    private Client(ResultHandler resultHandler, ExceptionHandler exceptionHandler, ExceptionHandler exceptionHandler2) {
        this.handlers.put(0L, new Handler(resultHandler, exceptionHandler));
        this.defaultExceptionHandler = exceptionHandler2;
    }

    /* access modifiers changed from: protected */
    public void finalize() throws Throwable {
        try {
            close();
        } finally {
            super.finalize();
        }
    }

    private void processResult(long j, TonApi.Object object) {
        Handler handler;
        if (j == 0) {
            handler = this.handlers.get(Long.valueOf(j));
        } else {
            handler = this.handlers.remove(Long.valueOf(j));
        }
        if (handler != null) {
            handleResult(object, handler.resultHandler, handler.exceptionHandler);
        }
    }

    private void handleResult(TonApi.Object object, ResultHandler resultHandler, ExceptionHandler exceptionHandler) {
        if (resultHandler != null) {
            try {
                resultHandler.onResult(object);
            } catch (Throwable unused) {
            }
        }
    }

    private void receiveQueries(double d) {
        int nativeClientReceive = nativeClientReceive(this.nativeClientId, this.eventIds, this.events, d);
        for (int i = 0; i < nativeClientReceive; i++) {
            processResult(this.eventIds[i], this.events[i]);
            this.events[i] = null;
        }
    }
}
