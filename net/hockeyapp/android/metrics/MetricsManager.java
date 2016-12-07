package net.hockeyapp.android.metrics;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.app.Application.ActivityLifecycleCallbacks;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import net.hockeyapp.android.Constants;
import net.hockeyapp.android.metrics.model.Data;
import net.hockeyapp.android.metrics.model.Domain;
import net.hockeyapp.android.metrics.model.SessionState;
import net.hockeyapp.android.metrics.model.SessionStateData;
import net.hockeyapp.android.metrics.model.TelemetryData;
import net.hockeyapp.android.utils.AsyncTaskUtils;
import net.hockeyapp.android.utils.HockeyLog;
import net.hockeyapp.android.utils.Util;

public class MetricsManager {
    protected static final AtomicInteger ACTIVITY_COUNT = new AtomicInteger(0);
    protected static final AtomicLong LAST_BACKGROUND = new AtomicLong(getTime());
    private static final Object LOCK = new Object();
    private static final Integer SESSION_RENEWAL_INTERVAL = Integer.valueOf(20000);
    private static final String TAG = "HA-MetricsManager";
    private static volatile MetricsManager instance;
    private static Channel sChannel;
    private static Sender sSender;
    private static TelemetryContext sTelemetryContext;
    private static WeakReference<Application> sWeakApplication;
    private volatile boolean mSessionTrackingDisabled;
    private TelemetryLifecycleCallbacks mTelemetryLifecycleCallbacks;

    @TargetApi(14)
    private class TelemetryLifecycleCallbacks implements ActivityLifecycleCallbacks {
        private TelemetryLifecycleCallbacks() {
        }

        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        }

        public void onActivityStarted(Activity activity) {
        }

        public void onActivityResumed(Activity activity) {
            MetricsManager.this.updateSession();
        }

        public void onActivityPaused(Activity activity) {
            MetricsManager.LAST_BACKGROUND.set(MetricsManager.getTime());
        }

        public void onActivityStopped(Activity activity) {
        }

        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        }

        public void onActivityDestroyed(Activity activity) {
        }
    }

    protected MetricsManager(Context context, TelemetryContext telemetryContext, Sender sender, Persistence persistence, Channel channel) {
        sTelemetryContext = telemetryContext;
        if (sender == null) {
            sender = new Sender();
        }
        sSender = sender;
        if (persistence == null) {
            persistence = new Persistence(context, sender);
        } else {
            persistence.setSender(sender);
        }
        sSender.setPersistence(persistence);
        if (channel == null) {
            sChannel = new Channel(sTelemetryContext, persistence);
        } else {
            sChannel = channel;
        }
    }

    public static void register(Context context, Application application) {
        String appIdentifier = Util.getAppIdentifier(context);
        if (appIdentifier == null || appIdentifier.length() == 0) {
            throw new IllegalArgumentException("HockeyApp app identifier was not configured correctly in manifest or build configuration.");
        }
        register(context, application, appIdentifier);
    }

    public static void register(Context context, Application application, String appIdentifier) {
        register(context, application, appIdentifier, null, null, null);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected static void register(Context context, Application application, String appIdentifier, Sender sender, Persistence persistence, Channel channel) {
        if (instance == null) {
            synchronized (LOCK) {
                try {
                    MetricsManager result;
                    MetricsManager result2 = instance;
                    if (result2 == null) {
                        try {
                            Constants.loadFromContext(context);
                            result = new MetricsManager(context, new TelemetryContext(context, appIdentifier), sender, persistence, channel);
                            sWeakApplication = new WeakReference(application);
                        } catch (Throwable th) {
                            Throwable th2 = th;
                            result = result2;
                            throw th2;
                        }
                    }
                    result = result2;
                    result.mSessionTrackingDisabled = !Util.sessionTrackingSupported();
                    instance = result;
                    if (!result.mSessionTrackingDisabled) {
                        setSessionTrackingDisabled(Boolean.valueOf(false));
                    }
                } catch (Throwable th3) {
                    th2 = th3;
                    throw th2;
                }
            }
        }
    }

    public static boolean sessionTrackingEnabled() {
        return !instance.mSessionTrackingDisabled;
    }

    public static void setSessionTrackingDisabled(Boolean disabled) {
        if (instance == null) {
            HockeyLog.warn(TAG, "MetricsManager hasn't been registered. No Metrics will be collected!");
            return;
        }
        synchronized (LOCK) {
            if (Util.sessionTrackingSupported()) {
                instance.mSessionTrackingDisabled = disabled.booleanValue();
                if (!disabled.booleanValue()) {
                    instance.registerTelemetryLifecycleCallbacks();
                }
            } else {
                instance.mSessionTrackingDisabled = true;
                instance.unregisterTelemetryLifecycleCallbacks();
            }
        }
    }

    public static void setCustomServerURL(String serverURL) {
        if (sSender != null) {
            sSender.setCustomServerURL(serverURL);
        } else {
            HockeyLog.warn(TAG, "HockeyApp couldn't set the custom server url. Please register(...) the MetricsManager before setting the server URL.");
        }
    }

    private static Application getApplication() {
        if (sWeakApplication != null) {
            return (Application) sWeakApplication.get();
        }
        return null;
    }

    private static long getTime() {
        return new Date().getTime();
    }

    protected static Channel getChannel() {
        return sChannel;
    }

    protected void setChannel(Channel channel) {
        sChannel = channel;
    }

    protected static Sender getSender() {
        return sSender;
    }

    protected static void setSender(Sender sender) {
        sSender = sender;
    }

    protected static MetricsManager getInstance() {
        return instance;
    }

    @TargetApi(14)
    private void registerTelemetryLifecycleCallbacks() {
        if (this.mTelemetryLifecycleCallbacks == null) {
            this.mTelemetryLifecycleCallbacks = new TelemetryLifecycleCallbacks();
        }
        getApplication().registerActivityLifecycleCallbacks(this.mTelemetryLifecycleCallbacks);
    }

    @TargetApi(14)
    private void unregisterTelemetryLifecycleCallbacks() {
        getApplication().unregisterActivityLifecycleCallbacks(this.mTelemetryLifecycleCallbacks);
        this.mTelemetryLifecycleCallbacks = null;
    }

    private void updateSession() {
        if (ACTIVITY_COUNT.getAndIncrement() != 0) {
            long now = getTime();
            long then = LAST_BACKGROUND.getAndSet(getTime());
            boolean shouldRenew = now - then >= ((long) SESSION_RENEWAL_INTERVAL.intValue());
            HockeyLog.debug(TAG, "Checking if we have to renew a session, time difference is: " + (now - then));
            if (shouldRenew && sessionTrackingEnabled()) {
                HockeyLog.debug(TAG, "Renewing session");
                renewSession();
            }
        } else if (sessionTrackingEnabled()) {
            HockeyLog.debug(TAG, "Starting & tracking session");
            renewSession();
        } else {
            HockeyLog.debug(TAG, "Session management disabled by the developer");
        }
    }

    protected void renewSession() {
        sTelemetryContext.renewSessionContext(UUID.randomUUID().toString());
        trackSessionState(SessionState.START);
    }

    private void trackSessionState(final SessionState sessionState) {
        AsyncTaskUtils.execute(new AsyncTask<Void, Void, Void>() {
            protected Void doInBackground(Void... params) {
                SessionStateData sessionItem = new SessionStateData();
                sessionItem.setState(sessionState);
                MetricsManager.sChannel.enqueueData(MetricsManager.this.createData(sessionItem));
                return null;
            }
        });
    }

    protected Data<Domain> createData(TelemetryData telemetryData) {
        Data<Domain> data = new Data();
        data.setBaseData(telemetryData);
        data.setBaseType(telemetryData.getBaseType());
        data.QualifiedName = telemetryData.getEnvelopeName();
        return data;
    }
}
