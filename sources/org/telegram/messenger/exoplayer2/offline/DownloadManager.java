package org.telegram.messenger.exoplayer2.offline;

import android.os.ConditionVariable;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;
import org.telegram.messenger.exoplayer2.DefaultLoadControl;
import org.telegram.messenger.exoplayer2.offline.DownloadAction.Deserializer;
import org.telegram.messenger.exoplayer2.upstream.DataSource.Factory;
import org.telegram.messenger.exoplayer2.upstream.cache.Cache;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.Util;

public final class DownloadManager {
    private static final boolean DEBUG = false;
    public static final int DEFAULT_MAX_SIMULTANEOUS_DOWNLOADS = 1;
    public static final int DEFAULT_MIN_RETRY_COUNT = 5;
    private static final String TAG = "DownloadManager";
    private final ActionFile actionFile;
    private final ArrayList<Task> activeDownloadTasks;
    private final Deserializer[] deserializers;
    private final DownloaderConstructorHelper downloaderConstructorHelper;
    private boolean downloadsStopped;
    private final Handler fileIOHandler;
    private final HandlerThread fileIOThread;
    private final Handler handler;
    private boolean initialized;
    private final CopyOnWriteArraySet<Listener> listeners;
    private final int maxActiveDownloadTasks;
    private final int minRetryCount;
    private int nextTaskId;
    private boolean released;
    private final ArrayList<Task> tasks;

    /* renamed from: org.telegram.messenger.exoplayer2.offline.DownloadManager$2 */
    class C06912 implements Runnable {
        C06912() {
        }

        public void run() {
            DownloadAction[] loadedActions;
            try {
                loadedActions = DownloadManager.this.actionFile.load(DownloadManager.this.deserializers);
                DownloadManager.logd("Action file is loaded.");
            } catch (Throwable e) {
                Log.e(DownloadManager.TAG, "Action file loading failed.", e);
                loadedActions = new DownloadAction[0];
            }
            final DownloadAction[] actions = loadedActions;
            DownloadManager.this.handler.post(new Runnable() {
                public void run() {
                    if (!DownloadManager.this.released) {
                        List<Task> pendingTasks = new ArrayList(DownloadManager.this.tasks);
                        DownloadManager.this.tasks.clear();
                        for (DownloadAction action : actions) {
                            DownloadManager.this.addTaskForAction(action);
                        }
                        DownloadManager.logd("Tasks are created.");
                        DownloadManager.this.initialized = true;
                        Iterator it = DownloadManager.this.listeners.iterator();
                        while (it.hasNext()) {
                            ((Listener) it.next()).onInitialized(DownloadManager.this);
                        }
                        if (!pendingTasks.isEmpty()) {
                            DownloadManager.this.tasks.addAll(pendingTasks);
                            DownloadManager.this.saveActions();
                        }
                        DownloadManager.this.maybeStartTasks();
                        for (int i = 0; i < DownloadManager.this.tasks.size(); i++) {
                            Task task = (Task) DownloadManager.this.tasks.get(i);
                            if (task.currentState == 0) {
                                DownloadManager.this.notifyListenersTaskStateChange(task);
                            }
                        }
                    }
                }
            });
        }
    }

    public interface Listener {
        void onIdle(DownloadManager downloadManager);

        void onInitialized(DownloadManager downloadManager);

        void onTaskStateChanged(DownloadManager downloadManager, TaskState taskState);
    }

    private static final class Task implements Runnable {
        public static final int STATE_QUEUED_CANCELING = 5;
        public static final int STATE_STARTED_CANCELING = 6;
        public static final int STATE_STARTED_STOPPING = 7;
        private final DownloadAction action;
        private volatile int currentState;
        private final DownloadManager downloadManager;
        private volatile Downloader downloader;
        private Throwable error;
        private final int id;
        private final int minRetryCount;
        private Thread thread;

        /* renamed from: org.telegram.messenger.exoplayer2.offline.DownloadManager$Task$1 */
        class C06931 implements Runnable {
            C06931() {
            }

            public void run() {
                Task.this.changeStateAndNotify(5, 3);
            }
        }

        @Retention(RetentionPolicy.SOURCE)
        public @interface InternalState {
        }

        private Task(int id, DownloadManager downloadManager, DownloadAction action, int minRetryCount) {
            this.id = id;
            this.downloadManager = downloadManager;
            this.action = action;
            this.currentState = 0;
            this.minRetryCount = minRetryCount;
        }

        public TaskState getDownloadState() {
            return new TaskState(this.id, this.action, getExternalState(), getDownloadPercentage(), getDownloadedBytes(), this.error);
        }

        public boolean isFinished() {
            return this.currentState == 4 || this.currentState == 2 || this.currentState == 3;
        }

        public boolean isActive() {
            return this.currentState == 5 || this.currentState == 1 || this.currentState == 7 || this.currentState == 6;
        }

        public float getDownloadPercentage() {
            return this.downloader != null ? this.downloader.getDownloadPercentage() : -1.0f;
        }

        public long getDownloadedBytes() {
            return this.downloader != null ? this.downloader.getDownloadedBytes() : 0;
        }

        public String toString() {
            return super.toString();
        }

        private static String toString(byte[] data) {
            if (data.length > 100) {
                return "<data is too long>";
            }
            return '\'' + Util.fromUtf8Bytes(data) + '\'';
        }

        private String getStateString() {
            switch (this.currentState) {
                case 5:
                case 6:
                    return "CANCELING";
                case 7:
                    return "STOPPING";
                default:
                    return TaskState.getStateString(this.currentState);
            }
        }

        private int getExternalState() {
            switch (this.currentState) {
                case 5:
                    return 0;
                case 6:
                case 7:
                    return 1;
                default:
                    return this.currentState;
            }
        }

        private void start() {
            if (changeStateAndNotify(0, 1)) {
                this.thread = new Thread(this);
                this.thread.start();
            }
        }

        private boolean canStart() {
            return this.currentState == 0;
        }

        private void cancel() {
            if (changeStateAndNotify(0, 5)) {
                this.downloadManager.handler.post(new C06931());
            } else if (changeStateAndNotify(1, 6)) {
                cancelDownload();
            }
        }

        private void stop() {
            if (changeStateAndNotify(1, 7)) {
                DownloadManager.logd("Stopping", this);
                this.thread.interrupt();
            }
        }

        private boolean changeStateAndNotify(int oldState, int newState) {
            return changeStateAndNotify(oldState, newState, null);
        }

        private boolean changeStateAndNotify(int oldState, int newState, Throwable error) {
            if (this.currentState != oldState) {
                return false;
            }
            boolean isInternalState;
            this.currentState = newState;
            this.error = error;
            if (this.currentState != getExternalState()) {
                isInternalState = true;
            } else {
                isInternalState = false;
            }
            if (!isInternalState) {
                this.downloadManager.onTaskStateChange(this);
            }
            return true;
        }

        private void cancelDownload() {
            if (this.downloader != null) {
                this.downloader.cancel();
            }
            this.thread.interrupt();
        }

        public void run() {
            int errorCount;
            long errorPosition;
            DownloadManager.logd("Task is started", this);
            Throwable error = null;
            this.downloader = this.action.createDownloader(this.downloadManager.downloaderConstructorHelper);
            if (this.action.isRemoveAction) {
                this.downloader.remove();
            } else {
                errorCount = 0;
                errorPosition = -1;
                while (!Thread.interrupted()) {
                    try {
                        this.downloader.download();
                        break;
                    } catch (IOException e) {
                        downloadedBytes = this.downloader.getDownloadedBytes();
                        long downloadedBytes;
                        if (downloadedBytes != errorPosition) {
                            DownloadManager.logd("Reset error count. downloadedBytes = " + downloadedBytes, this);
                            errorPosition = downloadedBytes;
                            errorCount = 0;
                        }
                        if (this.currentState == 1) {
                            errorCount++;
                            if (errorCount <= this.minRetryCount) {
                                DownloadManager.logd("Download error. Retry " + errorCount, this);
                                Thread.sleep((long) getRetryDelayMillis(errorCount));
                            }
                        }
                        throw e;
                    } catch (Throwable e2) {
                        error = e2;
                    }
                }
            }
            final Throwable finalError = error;
            this.downloadManager.handler.post(new Runnable() {
                public void run() {
                    if (!Task.this.changeStateAndNotify(1, finalError != null ? 4 : 2, finalError) && !Task.this.changeStateAndNotify(6, 3) && !Task.this.changeStateAndNotify(7, 0)) {
                        throw new IllegalStateException();
                    }
                }
            });
        }

        private int getRetryDelayMillis(int errorCount) {
            return Math.min((errorCount - 1) * 1000, DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS);
        }
    }

    public static final class TaskState {
        public static final int STATE_CANCELED = 3;
        public static final int STATE_COMPLETED = 2;
        public static final int STATE_FAILED = 4;
        public static final int STATE_QUEUED = 0;
        public static final int STATE_STARTED = 1;
        public final DownloadAction action;
        public final float downloadPercentage;
        public final long downloadedBytes;
        public final Throwable error;
        public final int state;
        public final int taskId;

        @Retention(RetentionPolicy.SOURCE)
        public @interface State {
        }

        public static String getStateString(int state) {
            switch (state) {
                case 0:
                    return "QUEUED";
                case 1:
                    return "STARTED";
                case 2:
                    return "COMPLETED";
                case 3:
                    return "CANCELED";
                case 4:
                    return "FAILED";
                default:
                    throw new IllegalStateException();
            }
        }

        private TaskState(int taskId, DownloadAction action, int state, float downloadPercentage, long downloadedBytes, Throwable error) {
            this.taskId = taskId;
            this.action = action;
            this.state = state;
            this.downloadPercentage = downloadPercentage;
            this.downloadedBytes = downloadedBytes;
            this.error = error;
        }
    }

    public DownloadManager(Cache cache, Factory upstreamDataSourceFactory, File actionSaveFile, Deserializer... deserializers) {
        this(new DownloaderConstructorHelper(cache, upstreamDataSourceFactory), actionSaveFile, deserializers);
    }

    public DownloadManager(DownloaderConstructorHelper constructorHelper, File actionFile, Deserializer... deserializers) {
        this(constructorHelper, 1, 5, actionFile, deserializers);
    }

    public DownloadManager(DownloaderConstructorHelper constructorHelper, int maxSimultaneousDownloads, int minRetryCount, File actionFile, Deserializer... deserializers) {
        Assertions.checkArgument(deserializers.length > 0, "At least one Deserializer is required.");
        this.downloaderConstructorHelper = constructorHelper;
        this.maxActiveDownloadTasks = maxSimultaneousDownloads;
        this.minRetryCount = minRetryCount;
        this.actionFile = new ActionFile(actionFile);
        this.deserializers = deserializers;
        this.downloadsStopped = true;
        this.tasks = new ArrayList();
        this.activeDownloadTasks = new ArrayList();
        Looper looper = Looper.myLooper();
        if (looper == null) {
            looper = Looper.getMainLooper();
        }
        this.handler = new Handler(looper);
        this.fileIOThread = new HandlerThread("DownloadManager file i/o");
        this.fileIOThread.start();
        this.fileIOHandler = new Handler(this.fileIOThread.getLooper());
        this.listeners = new CopyOnWriteArraySet();
        loadActions();
        logd("Created");
    }

    public void addListener(Listener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(Listener listener) {
        this.listeners.remove(listener);
    }

    public void startDownloads() {
        Assertions.checkState(!this.released);
        if (this.downloadsStopped) {
            this.downloadsStopped = false;
            maybeStartTasks();
            logd("Downloads are started");
        }
    }

    public void stopDownloads() {
        Assertions.checkState(!this.released);
        if (!this.downloadsStopped) {
            this.downloadsStopped = true;
            for (int i = 0; i < this.activeDownloadTasks.size(); i++) {
                ((Task) this.activeDownloadTasks.get(i)).stop();
            }
            logd("Downloads are stopping");
        }
    }

    public int handleAction(byte[] actionData) throws IOException {
        Assertions.checkState(!this.released);
        return handleAction(DownloadAction.deserializeFromStream(this.deserializers, new ByteArrayInputStream(actionData)));
    }

    public int handleAction(DownloadAction action) {
        Assertions.checkState(!this.released);
        Task task = addTaskForAction(action);
        if (this.initialized) {
            saveActions();
            maybeStartTasks();
            if (task.currentState == 0) {
                notifyListenersTaskStateChange(task);
            }
        }
        return task.id;
    }

    public int getTaskCount() {
        Assertions.checkState(!this.released);
        return this.tasks.size();
    }

    public TaskState getTaskState(int taskId) {
        Assertions.checkState(!this.released);
        for (int i = 0; i < this.tasks.size(); i++) {
            Task task = (Task) this.tasks.get(i);
            if (task.id == taskId) {
                return task.getDownloadState();
            }
        }
        return null;
    }

    public TaskState[] getAllTaskStates() {
        Assertions.checkState(!this.released);
        TaskState[] states = new TaskState[this.tasks.size()];
        for (int i = 0; i < states.length; i++) {
            states[i] = ((Task) this.tasks.get(i)).getDownloadState();
        }
        return states;
    }

    public boolean isInitialized() {
        Assertions.checkState(!this.released);
        return this.initialized;
    }

    public boolean isIdle() {
        Assertions.checkState(!this.released);
        if (!this.initialized) {
            return false;
        }
        for (int i = 0; i < this.tasks.size(); i++) {
            if (((Task) this.tasks.get(i)).isActive()) {
                return false;
            }
        }
        return true;
    }

    public void release() {
        if (!this.released) {
            this.released = true;
            for (int i = 0; i < this.tasks.size(); i++) {
                ((Task) this.tasks.get(i)).stop();
            }
            final ConditionVariable fileIOFinishedCondition = new ConditionVariable();
            this.fileIOHandler.post(new Runnable() {
                public void run() {
                    fileIOFinishedCondition.open();
                }
            });
            fileIOFinishedCondition.block();
            this.fileIOThread.quit();
            logd("Released");
        }
    }

    private Task addTaskForAction(DownloadAction action) {
        int i = this.nextTaskId;
        this.nextTaskId = i + 1;
        Task task = new Task(i, this, action, this.minRetryCount);
        this.tasks.add(task);
        logd("Task is added", task);
        return task;
    }

    private void maybeStartTasks() {
        if (this.initialized && !this.released) {
            boolean skipDownloadActions;
            if (this.downloadsStopped || this.activeDownloadTasks.size() == this.maxActiveDownloadTasks) {
                skipDownloadActions = true;
            } else {
                skipDownloadActions = false;
            }
            for (int i = 0; i < this.tasks.size(); i++) {
                Task task = (Task) this.tasks.get(i);
                if (task.canStart()) {
                    DownloadAction action = task.action;
                    boolean isRemoveAction = action.isRemoveAction;
                    if (isRemoveAction || !skipDownloadActions) {
                        boolean canStartTask = true;
                        for (int j = 0; j < i; j++) {
                            Task otherTask = (Task) this.tasks.get(j);
                            if (otherTask.action.isSameMedia(action)) {
                                if (isRemoveAction) {
                                    canStartTask = false;
                                    logd(task + " clashes with " + otherTask);
                                    otherTask.cancel();
                                } else if (otherTask.action.isRemoveAction) {
                                    canStartTask = false;
                                    skipDownloadActions = true;
                                    break;
                                }
                            }
                        }
                        if (canStartTask) {
                            task.start();
                            if (!isRemoveAction) {
                                this.activeDownloadTasks.add(task);
                                skipDownloadActions = this.activeDownloadTasks.size() == this.maxActiveDownloadTasks;
                            }
                        }
                    }
                }
            }
        }
    }

    private void maybeNotifyListenersIdle() {
        if (isIdle()) {
            logd("Notify idle state");
            Iterator it = this.listeners.iterator();
            while (it.hasNext()) {
                ((Listener) it.next()).onIdle(this);
            }
        }
    }

    private void onTaskStateChange(Task task) {
        if (!this.released) {
            boolean stopped = !task.isActive();
            if (stopped) {
                this.activeDownloadTasks.remove(task);
            }
            notifyListenersTaskStateChange(task);
            if (task.isFinished()) {
                this.tasks.remove(task);
                saveActions();
            }
            if (stopped) {
                maybeStartTasks();
                maybeNotifyListenersIdle();
            }
        }
    }

    private void notifyListenersTaskStateChange(Task task) {
        logd("Task state is changed", task);
        TaskState taskState = task.getDownloadState();
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            ((Listener) it.next()).onTaskStateChanged(this, taskState);
        }
    }

    private void loadActions() {
        this.fileIOHandler.post(new C06912());
    }

    private void saveActions() {
        if (!this.released) {
            final DownloadAction[] actions = new DownloadAction[this.tasks.size()];
            for (int i = 0; i < this.tasks.size(); i++) {
                actions[i] = ((Task) this.tasks.get(i)).action;
            }
            this.fileIOHandler.post(new Runnable() {
                public void run() {
                    try {
                        DownloadManager.this.actionFile.store(actions);
                        DownloadManager.logd("Actions persisted.");
                    } catch (IOException e) {
                        Log.e(DownloadManager.TAG, "Persisting actions failed.", e);
                    }
                }
            });
        }
    }

    private static void logd(String message) {
    }

    private static void logd(String message, Task task) {
        logd(message + ": " + task);
    }
}
