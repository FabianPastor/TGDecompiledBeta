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
import org.telegram.tgnet.TLRPC$PhotoSize;

/* compiled from: FeedWidgetService */
class FeedRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory, NotificationCenter.NotificationCenterDelegate {
    private AccountInstance accountInstance;
    private int classGuid;
    private CountDownLatch countDownLatch = new CountDownLatch(1);
    private long dialogId;
    private Context mContext;
    private ArrayList<MessageObject> messages = new ArrayList<>();

    public long getItemId(int i) {
        return (long) i;
    }

    public RemoteViews getLoadingView() {
        return null;
    }

    public int getViewTypeCount() {
        return 1;
    }

    public boolean hasStableIds() {
        return true;
    }

    public void onDestroy() {
    }

    public FeedRemoteViewsFactory(Context context, Intent intent) {
        this.mContext = context;
        int intExtra = intent.getIntExtra("appWidgetId", 0);
        SharedPreferences sharedPreferences = context.getSharedPreferences("shortcut_widget", 0);
        int i = sharedPreferences.getInt("account" + intExtra, -1);
        if (i >= 0) {
            this.dialogId = sharedPreferences.getLong("dialogId" + intExtra, 0);
            this.accountInstance = AccountInstance.getInstance(i);
        }
    }

    public void onCreate() {
        ApplicationLoader.postInitApplication();
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

    public RemoteViews getViewAt(int i) {
        MessageObject messageObject = this.messages.get(i);
        RemoteViews remoteViews = new RemoteViews(this.mContext.getPackageName(), NUM);
        if (messageObject.type == 0) {
            remoteViews.setTextViewText(NUM, messageObject.messageText);
            remoteViews.setViewVisibility(NUM, 0);
        } else if (TextUtils.isEmpty(messageObject.caption)) {
            remoteViews.setViewVisibility(NUM, 8);
        } else {
            remoteViews.setTextViewText(NUM, messageObject.caption);
            remoteViews.setViewVisibility(NUM, 0);
        }
        ArrayList<TLRPC$PhotoSize> arrayList = messageObject.photoThumbs;
        if (arrayList == null || arrayList.isEmpty()) {
            remoteViews.setViewVisibility(NUM, 8);
        } else {
            File pathToAttach = FileLoader.getInstance(UserConfig.selectedAccount).getPathToAttach(FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, AndroidUtilities.getPhotoSize()));
            if (pathToAttach.exists()) {
                remoteViews.setViewVisibility(NUM, 0);
                Uri uriForFile = FileProvider.getUriForFile(this.mContext, "org.telegram.messenger.beta.provider", pathToAttach);
                grantUriAccessToWidget(this.mContext, uriForFile);
                remoteViews.setImageViewUri(NUM, uriForFile);
            } else {
                remoteViews.setViewVisibility(NUM, 8);
            }
        }
        Bundle bundle = new Bundle();
        bundle.putLong("chatId", -messageObject.getDialogId());
        bundle.putInt("message_id", messageObject.getId());
        bundle.putInt("currentAccount", this.accountInstance.getCurrentAccount());
        Intent intent = new Intent();
        intent.putExtras(bundle);
        remoteViews.setOnClickFillInIntent(NUM, intent);
        return remoteViews;
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

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onDataSetChanged$0() {
        this.accountInstance.getNotificationCenter().addObserver(this, NotificationCenter.messagesDidLoad);
        if (this.classGuid == 0) {
            this.classGuid = ConnectionsManager.generateClassGuid();
        }
        this.accountInstance.getMessagesController().loadMessages(this.dialogId, 0, false, 20, 0, 0, true, 0, this.classGuid, 0, 0, 0, 0, 0, 1);
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.messagesDidLoad && objArr[10].intValue() == this.classGuid) {
            this.messages.clear();
            this.messages.addAll(objArr[2]);
            this.countDownLatch.countDown();
        }
    }
}
