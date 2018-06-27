package org.telegram.messenger.exoplayer2.offline;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import java.io.IOException;
import java.util.HashMap;
import org.telegram.messenger.exoplayer2.offline.DownloadManager.Listener;
import org.telegram.messenger.exoplayer2.offline.DownloadManager.TaskState;
import org.telegram.messenger.exoplayer2.scheduler.Requirements;
import org.telegram.messenger.exoplayer2.scheduler.RequirementsWatcher;
import org.telegram.messenger.exoplayer2.scheduler.Scheduler;
import org.telegram.messenger.exoplayer2.util.NotificationUtil;
import org.telegram.messenger.exoplayer2.util.Util;

public abstract class DownloadService extends Service {
    public static final String ACTION_ADD = "com.google.android.exoplayer.downloadService.action.ADD";
    public static final String ACTION_INIT = "com.google.android.exoplayer.downloadService.action.INIT";
    private static final String ACTION_RESTART = "com.google.android.exoplayer.downloadService.action.RESTART";
    private static final String ACTION_START_DOWNLOADS = "com.google.android.exoplayer.downloadService.action.START_DOWNLOADS";
    private static final String ACTION_STOP_DOWNLOADS = "com.google.android.exoplayer.downloadService.action.STOP_DOWNLOADS";
    private static final boolean DEBUG = false;
    public static final long DEFAULT_FOREGROUND_NOTIFICATION_UPDATE_INTERVAL = 1000;
    public static final String KEY_DOWNLOAD_ACTION = "download_action";
    public static final String KEY_FOREGROUND = "foreground";
    private static final String TAG = "DownloadService";
    private static final HashMap<Class<? extends DownloadService>, RequirementsHelper> requirementsHelpers = new HashMap();
    private final String channelId;
    private final int channelName;
    private DownloadManager downloadManager;
    private DownloadManagerListener downloadManagerListener;
    private final ForegroundNotificationUpdater foregroundNotificationUpdater;
    private int lastStartId;
    private boolean startedInForeground;

    private final class ForegroundNotificationUpdater implements Runnable {
        private final Handler handler = new Handler(Looper.getMainLooper());
        private boolean notificationDisplayed;
        private final int notificationId;
        private boolean periodicUpdatesStarted;
        private final long updateInterval;

        public ForegroundNotificationUpdater(int notificationId, long updateInterval) {
            this.notificationId = notificationId;
            this.updateInterval = updateInterval;
        }

        public void startPeriodicUpdates() {
            this.periodicUpdatesStarted = true;
            update();
        }

        public void stopPeriodicUpdates() {
            this.periodicUpdatesStarted = false;
            this.handler.removeCallbacks(this);
        }

        public void update() {
            DownloadService.this.startForeground(this.notificationId, DownloadService.this.getForegroundNotification(DownloadService.this.downloadManager.getAllTaskStates()));
            this.notificationDisplayed = true;
            if (this.periodicUpdatesStarted) {
                this.handler.removeCallbacks(this);
                this.handler.postDelayed(this, this.updateInterval);
            }
        }

        public void showNotificationIfNotAlready() {
            if (!this.notificationDisplayed) {
                update();
            }
        }

        public void run() {
            update();
        }
    }

    private final class DownloadManagerListener implements Listener {
        private DownloadManagerListener() {
        }

        public void onInitialized(DownloadManager downloadManager) {
        }

        public void onTaskStateChanged(DownloadManager downloadManager, TaskState taskState) {
            DownloadService.this.onTaskStateChanged(taskState);
            if (taskState.state == 1) {
                DownloadService.this.foregroundNotificationUpdater.startPeriodicUpdates();
            } else {
                DownloadService.this.foregroundNotificationUpdater.update();
            }
        }

        public final void onIdle(DownloadManager downloadManager) {
            DownloadService.this.stop();
        }
    }

    private static final class RequirementsHelper implements RequirementsWatcher.Listener {
        private final Context context;
        private final Requirements requirements;
        private final RequirementsWatcher requirementsWatcher;
        private final Scheduler scheduler;
        private final Class<? extends DownloadService> serviceClass;

        private RequirementsHelper(Context context, Requirements requirements, Scheduler scheduler, Class<? extends DownloadService> serviceClass) {
            this.context = context;
            this.requirements = requirements;
            this.scheduler = scheduler;
            this.serviceClass = serviceClass;
            this.requirementsWatcher = new RequirementsWatcher(context, this, requirements);
        }

        public void start() {
            this.requirementsWatcher.start();
        }

        public void stop() {
            this.requirementsWatcher.stop();
            if (this.scheduler != null) {
                this.scheduler.cancel();
            }
        }

        public void requirementsMet(RequirementsWatcher requirementsWatcher) {
            startServiceWithAction(DownloadService.ACTION_START_DOWNLOADS);
            if (this.scheduler != null) {
                this.scheduler.cancel();
            }
        }

        public void requirementsNotMet(RequirementsWatcher requirementsWatcher) {
            startServiceWithAction(DownloadService.ACTION_STOP_DOWNLOADS);
            if (this.scheduler != null) {
                if (!this.scheduler.schedule(this.requirements, this.context.getPackageName(), DownloadService.ACTION_RESTART)) {
                    Log.e(DownloadService.TAG, "Scheduling downloads failed.");
                }
            }
        }

        private void startServiceWithAction(String action) {
            Util.startForegroundService(this.context, new Intent(this.context, this.serviceClass).setAction(action).putExtra(DownloadService.KEY_FOREGROUND, true));
        }
    }

    protected abstract DownloadManager getDownloadManager();

    protected abstract Notification getForegroundNotification(TaskState[] taskStateArr);

    protected abstract Scheduler getScheduler();

    protected DownloadService(int foregroundNotificationId) {
        this(foregroundNotificationId, 1000);
    }

    protected DownloadService(int foregroundNotificationId, long foregroundNotificationUpdateInterval) {
        this(foregroundNotificationId, foregroundNotificationUpdateInterval, null, 0);
    }

    protected DownloadService(int foregroundNotificationId, long foregroundNotificationUpdateInterval, String channelId, int channelName) {
        this.foregroundNotificationUpdater = new ForegroundNotificationUpdater(foregroundNotificationId, foregroundNotificationUpdateInterval);
        this.channelId = channelId;
        this.channelName = channelName;
    }

    public static Intent buildAddActionIntent(Context context, Class<? extends DownloadService> clazz, DownloadAction downloadAction, boolean foreground) {
        return new Intent(context, clazz).setAction(ACTION_ADD).putExtra(KEY_DOWNLOAD_ACTION, downloadAction.toByteArray()).putExtra(KEY_FOREGROUND, foreground);
    }

    public static void startWithAction(Context context, Class<? extends DownloadService> clazz, DownloadAction downloadAction, boolean foreground) {
        Intent intent = buildAddActionIntent(context, clazz, downloadAction, foreground);
        if (foreground) {
            Util.startForegroundService(context, intent);
        } else {
            context.startService(intent);
        }
    }

    public void onCreate() {
        RequirementsHelper requirementsHelper;
        logd("onCreate");
        if (this.channelId != null) {
            NotificationUtil.createNotificationChannel(this, this.channelId, this.channelName, 2);
        }
        this.downloadManager = getDownloadManager();
        this.downloadManagerListener = new DownloadManagerListener();
        this.downloadManager.addListener(this.downloadManagerListener);
        synchronized (requirementsHelpers) {
            Class<? extends DownloadService> clazz = getClass();
            requirementsHelper = (RequirementsHelper) requirementsHelpers.get(clazz);
            if (requirementsHelper == null) {
                requirementsHelper = new RequirementsHelper(this, getRequirements(), getScheduler(), clazz);
                requirementsHelpers.put(clazz, requirementsHelper);
            }
        }
        requirementsHelper.start();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int onStartCommand(Intent intent, int flags, int startId) {
        boolean z = false;
        this.lastStartId = startId;
        String intentAction = null;
        if (intent != null) {
            intentAction = intent.getAction();
            boolean z2 = this.startedInForeground;
            int i = (intent.getBooleanExtra(KEY_FOREGROUND, false) || ACTION_RESTART.equals(intentAction)) ? 1 : 0;
            this.startedInForeground = i | z2;
        }
        logd("onStartCommand action: " + intentAction + " startId: " + startId);
        switch (intentAction.hashCode()) {
            case -871181424:
                if (intentAction.equals(ACTION_RESTART)) {
                    z = true;
                    break;
                }
            case -382886238:
                if (intentAction.equals(ACTION_ADD)) {
                    z = true;
                    break;
                }
            case -337334865:
                if (intentAction.equals(ACTION_START_DOWNLOADS)) {
                    z = true;
                    break;
                }
            case 1015676687:
                if (intentAction.equals(ACTION_INIT)) {
                    break;
                }
            case 1286088717:
                if (intentAction.equals(ACTION_STOP_DOWNLOADS)) {
                    z = true;
                    break;
                }
            default:
                z = true;
                break;
        }
        switch (z) {
            case false:
            case true:
                break;
            case true:
                byte[] actionData = intent.getByteArrayExtra(KEY_DOWNLOAD_ACTION);
                if (actionData != null) {
                    try {
                        this.downloadManager.handleAction(actionData);
                        break;
                    } catch (IOException e) {
                        Log.e(TAG, "Failed to handle ADD action", e);
                        break;
                    }
                }
                Log.e(TAG, "Ignoring ADD action with no action data");
                break;
            case true:
                this.downloadManager.stopDownloads();
                break;
            case true:
                this.downloadManager.startDownloads();
                break;
            default:
                Log.e(TAG, "Ignoring unrecognized action: " + intentAction);
                break;
        }
        if (this.downloadManager.isIdle()) {
            stop();
        }
        return 1;
    }

    public void onDestroy() {
        logd("onDestroy");
        this.foregroundNotificationUpdater.stopPeriodicUpdates();
        this.downloadManager.removeListener(this.downloadManagerListener);
        if (this.downloadManager.getTaskCount() == 0) {
            synchronized (requirementsHelpers) {
                RequirementsHelper requirementsHelper = (RequirementsHelper) requirementsHelpers.remove(getClass());
                if (requirementsHelper != null) {
                    requirementsHelper.stop();
                }
            }
        }
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    protected Requirements getRequirements() {
        return new Requirements(1, false, false);
    }

    protected void onTaskStateChanged(TaskState taskState) {
    }

    private void stop() {
        this.foregroundNotificationUpdater.stopPeriodicUpdates();
        if (this.startedInForeground && Util.SDK_INT >= 26) {
            this.foregroundNotificationUpdater.showNotificationIfNotAlready();
        }
        logd("stopSelf(" + this.lastStartId + ") result: " + stopSelfResult(this.lastStartId));
    }

    private void logd(String message) {
    }
}
