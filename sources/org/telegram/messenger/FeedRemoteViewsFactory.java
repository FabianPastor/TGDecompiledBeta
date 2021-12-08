package org.telegram.messenger;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import androidx.core.content.FileProvider;
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import org.telegram.messenger.NotificationCenter;
import org.telegram.tgnet.ConnectionsManager;

/* compiled from: FeedWidgetService */
class FeedRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory, NotificationCenter.NotificationCenterDelegate {
    private AccountInstance accountInstance;
    private int classGuid;
    private CountDownLatch countDownLatch = new CountDownLatch(1);
    private long dialogId;
    private Context mContext;
    private ArrayList<MessageObject> messages = new ArrayList<>();

    public FeedRemoteViewsFactory(Context context, Intent intent) {
        this.mContext = context;
        int appWidgetId = intent.getIntExtra("appWidgetId", 0);
        SharedPreferences preferences = context.getSharedPreferences("shortcut_widget", 0);
        int accountId = preferences.getInt("account" + appWidgetId, -1);
        if (accountId >= 0) {
            this.dialogId = preferences.getLong("dialogId" + appWidgetId, 0);
            this.accountInstance = AccountInstance.getInstance(accountId);
        }
    }

    public void onCreate() {
        ApplicationLoader.postInitApplication();
    }

    public void onDestroy() {
    }

    public int getCount() {
        return this.messages.size();
    }

    /* access modifiers changed from: protected */
    public void grantUriAccessToWidget(Context context, Uri uri) {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        for (ResolveInfo resolveInfo : context.getPackageManager().queryIntentActivities(intent, 65536)) {
            context.grantUriPermission(resolveInfo.activityInfo.packageName, uri, 1);
        }
    }

    public RemoteViews getViewAt(int position) {
        MessageObject messageObject = this.messages.get(position);
        RemoteViews rv = new RemoteViews(this.mContext.getPackageName(), NUM);
        if (messageObject.type == 0) {
            rv.setTextViewText(NUM, messageObject.messageText);
            rv.setViewVisibility(NUM, 0);
        } else if (TextUtils.isEmpty(messageObject.caption)) {
            rv.setViewVisibility(NUM, 8);
        } else {
            rv.setTextViewText(NUM, messageObject.caption);
            rv.setViewVisibility(NUM, 0);
        }
        if (messageObject.photoThumbs == null || messageObject.photoThumbs.isEmpty()) {
            rv.setViewVisibility(NUM, 8);
        } else {
            File f = FileLoader.getPathToAttach(FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, AndroidUtilities.getPhotoSize()));
            if (f.exists()) {
                rv.setViewVisibility(NUM, 0);
                Uri uri = FileProvider.getUriForFile(this.mContext, "org.telegram.messenger.beta.provider", f);
                grantUriAccessToWidget(this.mContext, uri);
                rv.setImageViewUri(NUM, uri);
            } else {
                rv.setViewVisibility(NUM, 8);
            }
        }
        Bundle extras = new Bundle();
        extras.putLong("chatId", -messageObject.getDialogId());
        extras.putInt("message_id", messageObject.getId());
        extras.putInt("currentAccount", this.accountInstance.getCurrentAccount());
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);
        rv.setOnClickFillInIntent(NUM, fillInIntent);
        return rv;
    }

    public RemoteViews getLoadingView() {
        return null;
    }

    public int getViewTypeCount() {
        return 1;
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public boolean hasStableIds() {
        return true;
    }

    public void onDataSetChanged() {
        AccountInstance accountInstance2 = this.accountInstance;
        if (accountInstance2 == null || !accountInstance2.getUserConfig().isClientActivated()) {
            this.messages.clear();
            return;
        }
        AndroidUtilities.runOnUIThread(new FeedRemoteViewsFactory$$ExternalSyntheticLambda0(this));
        try {
            this.countDownLatch.await();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* renamed from: lambda$onDataSetChanged$0$org-telegram-messenger-FeedRemoteViewsFactory  reason: not valid java name */
    public /* synthetic */ void m617xab71b43d() {
        this.accountInstance.getNotificationCenter().addObserver(this, NotificationCenter.messagesDidLoad);
        if (this.classGuid == 0) {
            this.classGuid = ConnectionsManager.generateClassGuid();
        }
        this.accountInstance.getMessagesController().loadMessages(this.dialogId, 0, false, 20, 0, 0, true, 0, this.classGuid, 0, 0, 0, 0, 0, 1);
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.messagesDidLoad && args[10].intValue() == this.classGuid) {
            this.messages.clear();
            this.messages.addAll(args[2]);
            this.countDownLatch.countDown();
        }
    }
}
