package android.support.v4.app;

import android.app.Notification;
import android.app.Notification.Builder;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat.Action;
import android.support.v4.app.NotificationCompat.Style;
import android.widget.RemoteViews;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class NotificationCompatBuilder implements NotificationBuilderWithBuilderAccessor {
    private final List<Bundle> mActionExtrasList = new ArrayList();
    private RemoteViews mBigContentView;
    private final Builder mBuilder;
    private final NotificationCompat.Builder mBuilderCompat;
    private RemoteViews mContentView;
    private final Bundle mExtras = new Bundle();
    private int mGroupAlertBehavior;
    private RemoteViews mHeadsUpContentView;

    NotificationCompatBuilder(NotificationCompat.Builder b) {
        boolean z;
        Iterator it;
        boolean z2 = false;
        this.mBuilderCompat = b;
        if (VERSION.SDK_INT >= 26) {
            this.mBuilder = new Builder(b.mContext, b.mChannelId);
        } else {
            this.mBuilder = new Builder(b.mContext);
        }
        Notification n = b.mNotification;
        Builder lights = this.mBuilder.setWhen(n.when).setSmallIcon(n.icon, n.iconLevel).setContent(n.contentView).setTicker(n.tickerText, b.mTickerView).setSound(n.sound, n.audioStreamType).setVibrate(n.vibrate).setLights(n.ledARGB, n.ledOnMS, n.ledOffMS);
        if ((n.flags & 2) != 0) {
            z = true;
        } else {
            z = false;
        }
        lights = lights.setOngoing(z);
        if ((n.flags & 8) != 0) {
            z = true;
        } else {
            z = false;
        }
        lights = lights.setOnlyAlertOnce(z);
        if ((n.flags & 16) != 0) {
            z = true;
        } else {
            z = false;
        }
        Builder deleteIntent = lights.setAutoCancel(z).setDefaults(n.defaults).setContentTitle(b.mContentTitle).setContentText(b.mContentText).setContentInfo(b.mContentInfo).setContentIntent(b.mContentIntent).setDeleteIntent(n.deleteIntent);
        PendingIntent pendingIntent = b.mFullScreenIntent;
        if ((n.flags & 128) != 0) {
            z2 = true;
        }
        deleteIntent.setFullScreenIntent(pendingIntent, z2).setLargeIcon(b.mLargeIcon).setNumber(b.mNumber).setProgress(b.mProgressMax, b.mProgress, b.mProgressIndeterminate);
        if (VERSION.SDK_INT >= 16) {
            this.mBuilder.setSubText(b.mSubText).setUsesChronometer(b.mUseChronometer).setPriority(b.mPriority);
            it = b.mActions.iterator();
            while (it.hasNext()) {
                addAction((Action) it.next());
            }
            if (b.mExtras != null) {
                this.mExtras.putAll(b.mExtras);
            }
            if (VERSION.SDK_INT < 20) {
                if (b.mLocalOnly) {
                    this.mExtras.putBoolean("android.support.localOnly", true);
                }
                if (b.mGroupKey != null) {
                    this.mExtras.putString("android.support.groupKey", b.mGroupKey);
                    if (b.mGroupSummary) {
                        this.mExtras.putBoolean("android.support.isGroupSummary", true);
                    } else {
                        this.mExtras.putBoolean("android.support.useSideChannel", true);
                    }
                }
                if (b.mSortKey != null) {
                    this.mExtras.putString("android.support.sortKey", b.mSortKey);
                }
            }
            this.mContentView = b.mContentView;
            this.mBigContentView = b.mBigContentView;
        }
        if (VERSION.SDK_INT >= 19) {
            this.mBuilder.setShowWhen(b.mShowWhen);
            if (!(VERSION.SDK_INT >= 21 || b.mPeople == null || b.mPeople.isEmpty())) {
                this.mExtras.putStringArray("android.people", (String[]) b.mPeople.toArray(new String[b.mPeople.size()]));
            }
        }
        if (VERSION.SDK_INT >= 20) {
            this.mBuilder.setLocalOnly(b.mLocalOnly).setGroup(b.mGroupKey).setGroupSummary(b.mGroupSummary).setSortKey(b.mSortKey);
            this.mGroupAlertBehavior = b.mGroupAlertBehavior;
        }
        if (VERSION.SDK_INT >= 21) {
            this.mBuilder.setCategory(b.mCategory).setColor(b.mColor).setVisibility(b.mVisibility).setPublicVersion(b.mPublicVersion);
            it = b.mPeople.iterator();
            while (it.hasNext()) {
                this.mBuilder.addPerson((String) it.next());
            }
            this.mHeadsUpContentView = b.mHeadsUpContentView;
        }
        if (VERSION.SDK_INT >= 24) {
            this.mBuilder.setExtras(b.mExtras).setRemoteInputHistory(b.mRemoteInputHistory);
            if (b.mContentView != null) {
                this.mBuilder.setCustomContentView(b.mContentView);
            }
            if (b.mBigContentView != null) {
                this.mBuilder.setCustomBigContentView(b.mBigContentView);
            }
            if (b.mHeadsUpContentView != null) {
                this.mBuilder.setCustomHeadsUpContentView(b.mHeadsUpContentView);
            }
        }
        if (VERSION.SDK_INT >= 26) {
            this.mBuilder.setBadgeIconType(b.mBadgeIcon).setShortcutId(b.mShortcutId).setTimeoutAfter(b.mTimeout).setGroupAlertBehavior(b.mGroupAlertBehavior);
            if (b.mColorizedSet) {
                this.mBuilder.setColorized(b.mColorized);
            }
        }
    }

    public Builder getBuilder() {
        return this.mBuilder;
    }

    public Notification build() {
        Style style = this.mBuilderCompat.mStyle;
        if (style != null) {
            style.apply(this);
        }
        RemoteViews styleContentView = style != null ? style.makeContentView(this) : null;
        Notification n = buildInternal();
        if (styleContentView != null) {
            n.contentView = styleContentView;
        } else if (this.mBuilderCompat.mContentView != null) {
            n.contentView = this.mBuilderCompat.mContentView;
        }
        if (VERSION.SDK_INT >= 16 && style != null) {
            RemoteViews styleBigContentView = style.makeBigContentView(this);
            if (styleBigContentView != null) {
                n.bigContentView = styleBigContentView;
            }
        }
        if (VERSION.SDK_INT >= 21 && style != null) {
            RemoteViews styleHeadsUpContentView = this.mBuilderCompat.mStyle.makeHeadsUpContentView(this);
            if (styleHeadsUpContentView != null) {
                n.headsUpContentView = styleHeadsUpContentView;
            }
        }
        if (VERSION.SDK_INT >= 16 && style != null) {
            Bundle extras = NotificationCompat.getExtras(n);
            if (extras != null) {
                style.addCompatExtras(extras);
            }
        }
        return n;
    }

    private void addAction(Action action) {
        if (VERSION.SDK_INT >= 20) {
            Bundle actionExtras;
            Notification.Action.Builder actionBuilder = new Notification.Action.Builder(action.getIcon(), action.getTitle(), action.getActionIntent());
            if (action.getRemoteInputs() != null) {
                for (RemoteInput remoteInput : RemoteInput.fromCompat(action.getRemoteInputs())) {
                    actionBuilder.addRemoteInput(remoteInput);
                }
            }
            if (action.getExtras() != null) {
                actionExtras = new Bundle(action.getExtras());
            } else {
                actionExtras = new Bundle();
            }
            actionExtras.putBoolean("android.support.allowGeneratedReplies", action.getAllowGeneratedReplies());
            if (VERSION.SDK_INT >= 24) {
                actionBuilder.setAllowGeneratedReplies(action.getAllowGeneratedReplies());
            }
            actionBuilder.addExtras(actionExtras);
            this.mBuilder.addAction(actionBuilder.build());
        } else if (VERSION.SDK_INT >= 16) {
            this.mActionExtrasList.add(NotificationCompatJellybean.writeActionAndGetExtras(this.mBuilder, action));
        }
    }

    protected Notification buildInternal() {
        if (VERSION.SDK_INT >= 26) {
            return this.mBuilder.build();
        }
        Notification notification;
        if (VERSION.SDK_INT >= 24) {
            notification = this.mBuilder.build();
            if (this.mGroupAlertBehavior == 0) {
                return notification;
            }
            if (!(notification.getGroup() == null || (notification.flags & 512) == 0 || this.mGroupAlertBehavior != 2)) {
                removeSoundAndVibration(notification);
            }
            if (notification.getGroup() == null || (notification.flags & 512) != 0 || this.mGroupAlertBehavior != 1) {
                return notification;
            }
            removeSoundAndVibration(notification);
            return notification;
        } else if (VERSION.SDK_INT >= 21) {
            this.mBuilder.setExtras(this.mExtras);
            notification = this.mBuilder.build();
            if (this.mContentView != null) {
                notification.contentView = this.mContentView;
            }
            if (this.mBigContentView != null) {
                notification.bigContentView = this.mBigContentView;
            }
            if (this.mHeadsUpContentView != null) {
                notification.headsUpContentView = this.mHeadsUpContentView;
            }
            if (this.mGroupAlertBehavior == 0) {
                return notification;
            }
            if (!(notification.getGroup() == null || (notification.flags & 512) == 0 || this.mGroupAlertBehavior != 2)) {
                removeSoundAndVibration(notification);
            }
            if (notification.getGroup() == null || (notification.flags & 512) != 0 || this.mGroupAlertBehavior != 1) {
                return notification;
            }
            removeSoundAndVibration(notification);
            return notification;
        } else if (VERSION.SDK_INT >= 20) {
            this.mBuilder.setExtras(this.mExtras);
            notification = this.mBuilder.build();
            if (this.mContentView != null) {
                notification.contentView = this.mContentView;
            }
            if (this.mBigContentView != null) {
                notification.bigContentView = this.mBigContentView;
            }
            if (this.mGroupAlertBehavior == 0) {
                return notification;
            }
            if (!(notification.getGroup() == null || (notification.flags & 512) == 0 || this.mGroupAlertBehavior != 2)) {
                removeSoundAndVibration(notification);
            }
            if (notification.getGroup() == null || (notification.flags & 512) != 0 || this.mGroupAlertBehavior != 1) {
                return notification;
            }
            removeSoundAndVibration(notification);
            return notification;
        } else if (VERSION.SDK_INT >= 19) {
            actionExtrasMap = NotificationCompatJellybean.buildActionExtrasMap(this.mActionExtrasList);
            if (actionExtrasMap != null) {
                this.mExtras.putSparseParcelableArray("android.support.actionExtras", actionExtrasMap);
            }
            this.mBuilder.setExtras(this.mExtras);
            notification = this.mBuilder.build();
            if (this.mContentView != null) {
                notification.contentView = this.mContentView;
            }
            if (this.mBigContentView == null) {
                return notification;
            }
            notification.bigContentView = this.mBigContentView;
            return notification;
        } else if (VERSION.SDK_INT < 16) {
            return this.mBuilder.getNotification();
        } else {
            notification = this.mBuilder.build();
            Bundle extras = NotificationCompat.getExtras(notification);
            Bundle mergeBundle = new Bundle(this.mExtras);
            for (String key : this.mExtras.keySet()) {
                if (extras.containsKey(key)) {
                    mergeBundle.remove(key);
                }
            }
            extras.putAll(mergeBundle);
            actionExtrasMap = NotificationCompatJellybean.buildActionExtrasMap(this.mActionExtrasList);
            if (actionExtrasMap != null) {
                NotificationCompat.getExtras(notification).putSparseParcelableArray("android.support.actionExtras", actionExtrasMap);
            }
            if (this.mContentView != null) {
                notification.contentView = this.mContentView;
            }
            if (this.mBigContentView == null) {
                return notification;
            }
            notification.bigContentView = this.mBigContentView;
            return notification;
        }
    }

    private void removeSoundAndVibration(Notification notification) {
        notification.sound = null;
        notification.vibrate = null;
        notification.defaults &= -2;
        notification.defaults &= -3;
    }
}
