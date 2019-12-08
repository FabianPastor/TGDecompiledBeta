package drinkless.org.ton;

import drinkless.org.ton.TonApi.Error;
import drinkless.org.ton.TonApi.Function;
import drinkless.org.ton.TonApi.Object;
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
    private final Object[] events = new Object[1000];
    private final ConcurrentHashMap<Long, Handler> handlers = new ConcurrentHashMap();
    private volatile boolean isClientDestroyed = false;
    private final long nativeClientId = createNativeClient();
    private final Lock readLock = this.readWriteLock.readLock();
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private volatile boolean stopFlag = false;
    private final Lock writeLock = this.readWriteLock.writeLock();

    public interface ExceptionHandler {
        void onException(Throwable th);
    }

    private static class Handler {
        final ExceptionHandler exceptionHandler;
        final ResultHandler resultHandler;

        Handler(ResultHandler resultHandler, ExceptionHandler exceptionHandler) {
            this.resultHandler = resultHandler;
            this.exceptionHandler = exceptionHandler;
        }
    }

    public interface ResultHandler {
        void onResult(Object object);
    }

    private static native long createNativeClient();

    private static native void destroyNativeClient(long j);

    private static native Object nativeClientExecute(Function function);

    private static native int nativeClientReceive(long j, long[] jArr, Object[] objectArr, double d);

    private static native void nativeClientSend(long j, long j2, Function function);

    public void send(Function function, ResultHandler resultHandler, ExceptionHandler exceptionHandler) {
        if (function != null) {
            this.readLock.lock();
            try {
                if (this.isClientDestroyed) {
                    if (resultHandler != null) {
                        handleResult(new Error(500, "Client is closed"), resultHandler, exceptionHandler);
                    }
                    this.readLock.unlock();
                    return;
                }
                long incrementAndGet = this.currentQueryId.incrementAndGet();
                this.handlers.put(Long.valueOf(incrementAndGet), new Handler(resultHandler, exceptionHandler));
                nativeClientSend(this.nativeClientId, incrementAndGet, function);
                this.readLock.unlock();
            } catch (Throwable th) {
                this.readLock.unlock();
            }
        } else {
            throw new NullPointerException("query is null");
        }
    }

    public void send(Function function, ResultHandler resultHandler) {
        send(function, resultHandler, null);
    }

    public static Object execute(Function function) {
        if (function != null) {
            return nativeClientExecute(function);
        }
        throw new NullPointerException("query is null");
    }

    public void setUpdatesHandler(ResultHandler resultHandler, ExceptionHandler exceptionHandler) {
        this.handlers.put(Long.valueOf(0), new Handler(resultHandler, exceptionHandler));
    }

    public void setUpdatesHandler(ResultHandler resultHandler) {
        setUpdatesHandler(resultHandler, null);
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

    private Client(ResultHandler resultHandler, ExceptionHandler exceptionHandler, ExceptionHandler exceptionHandler2) {
        this.handlers.put(Long.valueOf(0), new Handler(resultHandler, exceptionHandler));
        this.defaultExceptionHandler = exceptionHandler2;
    }

    /* Access modifiers changed, original: protected */
    public void finalize() throws Throwable {
        try {
            close();
        } finally {
            super.finalize();
        }
    }

    private void processResult(long j, Object object) {
        Handler handler;
        if (j == 0) {
            handler = (Handler) this.handlers.get(Long.valueOf(j));
        } else {
            handler = (Handler) this.handlers.remove(Long.valueOf(j));
        }
        if (handler != null) {
            handleResult(object, handler.resultHandler, handler.exceptionHandler);
        }
    }

    private void handleResult(Object object, ResultHandler resultHandler, ExceptionHandler exceptionHandler) {
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
