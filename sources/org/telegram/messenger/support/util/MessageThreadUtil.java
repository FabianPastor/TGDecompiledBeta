package org.telegram.messenger.support.util;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import org.telegram.messenger.support.util.ThreadUtil.BackgroundCallback;
import org.telegram.messenger.support.util.ThreadUtil.MainThreadCallback;
import org.telegram.messenger.support.util.TileList.Tile;

class MessageThreadUtil<T> implements ThreadUtil<T> {

    static class MessageQueue {
        private SyncQueueItem mRoot;

        MessageQueue() {
        }

        synchronized SyncQueueItem next() {
            if (this.mRoot == null) {
                return null;
            }
            SyncQueueItem syncQueueItem = this.mRoot;
            this.mRoot = this.mRoot.next;
            return syncQueueItem;
        }

        synchronized void sendMessageAtFrontOfQueue(SyncQueueItem syncQueueItem) {
            syncQueueItem.next = this.mRoot;
            this.mRoot = syncQueueItem;
        }

        synchronized void sendMessage(SyncQueueItem syncQueueItem) {
            if (this.mRoot == null) {
                this.mRoot = syncQueueItem;
                return;
            }
            SyncQueueItem syncQueueItem2 = this.mRoot;
            while (syncQueueItem2.next != null) {
                syncQueueItem2 = syncQueueItem2.next;
            }
            syncQueueItem2.next = syncQueueItem;
        }

        synchronized void removeMessages(int i) {
            while (this.mRoot != null && this.mRoot.what == i) {
                SyncQueueItem syncQueueItem = this.mRoot;
                this.mRoot = this.mRoot.next;
                syncQueueItem.recycle();
            }
            if (this.mRoot != null) {
                syncQueueItem = this.mRoot;
                SyncQueueItem access$000 = syncQueueItem.next;
                while (access$000 != null) {
                    SyncQueueItem access$0002 = access$000.next;
                    if (access$000.what == i) {
                        syncQueueItem.next = access$0002;
                        access$000.recycle();
                    } else {
                        syncQueueItem = access$000;
                    }
                    access$000 = access$0002;
                }
            }
        }
    }

    static class SyncQueueItem {
        private static SyncQueueItem sPool;
        private static final Object sPoolLock = new Object();
        public int arg1;
        public int arg2;
        public int arg3;
        public int arg4;
        public int arg5;
        public Object data;
        private SyncQueueItem next;
        public int what;

        SyncQueueItem() {
        }

        void recycle() {
            this.next = null;
            this.arg5 = 0;
            this.arg4 = 0;
            this.arg3 = 0;
            this.arg2 = 0;
            this.arg1 = 0;
            this.what = 0;
            this.data = null;
            synchronized (sPoolLock) {
                if (sPool != null) {
                    this.next = sPool;
                }
                sPool = this;
            }
        }

        static SyncQueueItem obtainMessage(int i, int i2, int i3, int i4, int i5, int i6, Object obj) {
            SyncQueueItem syncQueueItem;
            synchronized (sPoolLock) {
                if (sPool == null) {
                    syncQueueItem = new SyncQueueItem();
                } else {
                    syncQueueItem = sPool;
                    sPool = sPool.next;
                    syncQueueItem.next = null;
                }
                syncQueueItem.what = i;
                syncQueueItem.arg1 = i2;
                syncQueueItem.arg2 = i3;
                syncQueueItem.arg3 = i4;
                syncQueueItem.arg4 = i5;
                syncQueueItem.arg5 = i6;
                syncQueueItem.data = obj;
            }
            return syncQueueItem;
        }

        static SyncQueueItem obtainMessage(int i, int i2, int i3) {
            return obtainMessage(i, i2, i3, 0, 0, 0, null);
        }

        static SyncQueueItem obtainMessage(int i, int i2, Object obj) {
            return obtainMessage(i, i2, 0, 0, 0, 0, obj);
        }
    }

    MessageThreadUtil() {
    }

    public MainThreadCallback<T> getMainThreadProxy(final MainThreadCallback<T> mainThreadCallback) {
        return new MainThreadCallback<T>() {
            static final int ADD_TILE = 2;
            static final int REMOVE_TILE = 3;
            static final int UPDATE_ITEM_COUNT = 1;
            private final Handler mMainThreadHandler = new Handler(Looper.getMainLooper());
            private Runnable mMainThreadRunnable = new C06461();
            final MessageQueue mQueue = new MessageQueue();

            /* renamed from: org.telegram.messenger.support.util.MessageThreadUtil$1$1 */
            class C06461 implements Runnable {
                C06461() {
                }

                public void run() {
                    SyncQueueItem next = C18641.this.mQueue.next();
                    while (next != null) {
                        switch (next.what) {
                            case 1:
                                mainThreadCallback.updateItemCount(next.arg1, next.arg2);
                                break;
                            case 2:
                                mainThreadCallback.addTile(next.arg1, (Tile) next.data);
                                break;
                            case 3:
                                mainThreadCallback.removeTile(next.arg1, next.arg2);
                                break;
                            default:
                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append("Unsupported message, what=");
                                stringBuilder.append(next.what);
                                Log.e("ThreadUtil", stringBuilder.toString());
                                break;
                        }
                        next = C18641.this.mQueue.next();
                    }
                }
            }

            public void updateItemCount(int i, int i2) {
                sendMessage(SyncQueueItem.obtainMessage(1, i, i2));
            }

            public void addTile(int i, Tile<T> tile) {
                sendMessage(SyncQueueItem.obtainMessage(2, i, (Object) tile));
            }

            public void removeTile(int i, int i2) {
                sendMessage(SyncQueueItem.obtainMessage(3, i, i2));
            }

            private void sendMessage(SyncQueueItem syncQueueItem) {
                this.mQueue.sendMessage(syncQueueItem);
                this.mMainThreadHandler.post(this.mMainThreadRunnable);
            }
        };
    }

    public BackgroundCallback<T> getBackgroundProxy(final BackgroundCallback<T> backgroundCallback) {
        return new BackgroundCallback<T>() {
            static final int LOAD_TILE = 3;
            static final int RECYCLE_TILE = 4;
            static final int REFRESH = 1;
            static final int UPDATE_RANGE = 2;
            private Runnable mBackgroundRunnable = new C06471();
            AtomicBoolean mBackgroundRunning = new AtomicBoolean(null);
            private final Executor mExecutor = AsyncTask.THREAD_POOL_EXECUTOR;
            final MessageQueue mQueue = new MessageQueue();

            /* renamed from: org.telegram.messenger.support.util.MessageThreadUtil$2$1 */
            class C06471 implements Runnable {
                C06471() {
                }

                public void run() {
                    while (true) {
                        SyncQueueItem next = C18652.this.mQueue.next();
                        if (next != null) {
                            switch (next.what) {
                                case 1:
                                    C18652.this.mQueue.removeMessages(1);
                                    backgroundCallback.refresh(next.arg1);
                                    break;
                                case 2:
                                    C18652.this.mQueue.removeMessages(2);
                                    C18652.this.mQueue.removeMessages(3);
                                    backgroundCallback.updateRange(next.arg1, next.arg2, next.arg3, next.arg4, next.arg5);
                                    break;
                                case 3:
                                    backgroundCallback.loadTile(next.arg1, next.arg2);
                                    break;
                                case 4:
                                    backgroundCallback.recycleTile((Tile) next.data);
                                    break;
                                default:
                                    StringBuilder stringBuilder = new StringBuilder();
                                    stringBuilder.append("Unsupported message, what=");
                                    stringBuilder.append(next.what);
                                    Log.e("ThreadUtil", stringBuilder.toString());
                                    break;
                            }
                        }
                        C18652.this.mBackgroundRunning.set(false);
                        return;
                    }
                }
            }

            public void refresh(int i) {
                sendMessageAtFrontOfQueue(SyncQueueItem.obtainMessage(1, i, null));
            }

            public void updateRange(int i, int i2, int i3, int i4, int i5) {
                sendMessageAtFrontOfQueue(SyncQueueItem.obtainMessage(2, i, i2, i3, i4, i5, null));
            }

            public void loadTile(int i, int i2) {
                sendMessage(SyncQueueItem.obtainMessage(3, i, i2));
            }

            public void recycleTile(Tile<T> tile) {
                sendMessage(SyncQueueItem.obtainMessage(4, 0, (Object) tile));
            }

            private void sendMessage(SyncQueueItem syncQueueItem) {
                this.mQueue.sendMessage(syncQueueItem);
                maybeExecuteBackgroundRunnable();
            }

            private void sendMessageAtFrontOfQueue(SyncQueueItem syncQueueItem) {
                this.mQueue.sendMessageAtFrontOfQueue(syncQueueItem);
                maybeExecuteBackgroundRunnable();
            }

            private void maybeExecuteBackgroundRunnable() {
                if (this.mBackgroundRunning.compareAndSet(false, true)) {
                    this.mExecutor.execute(this.mBackgroundRunnable);
                }
            }
        };
    }
}
