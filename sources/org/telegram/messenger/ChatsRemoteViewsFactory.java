package org.telegram.messenger;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.RectF;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import androidx.collection.LongSparseArray;
import java.util.ArrayList;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$Dialog;
import org.telegram.tgnet.TLRPC$Message;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.Theme;

/* compiled from: ChatsWidgetService */
class ChatsRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private AccountInstance accountInstance;
    private int appWidgetId;
    private RectF bitmapRect;
    private boolean deleted;
    private LongSparseArray<TLRPC$Dialog> dialogs = new LongSparseArray<>();
    private ArrayList<Long> dids = new ArrayList<>();
    private Context mContext;
    private LongSparseArray<MessageObject> messageObjects = new LongSparseArray<>();
    private Paint roundPaint;

    public long getItemId(int i) {
        return (long) i;
    }

    public RemoteViews getLoadingView() {
        return null;
    }

    public int getViewTypeCount() {
        return 2;
    }

    public boolean hasStableIds() {
        return true;
    }

    public void onDestroy() {
    }

    public ChatsRemoteViewsFactory(Context context, Intent intent) {
        this.mContext = context;
        Theme.createDialogsResources(context);
        boolean z = false;
        this.appWidgetId = intent.getIntExtra("appWidgetId", 0);
        SharedPreferences sharedPreferences = context.getSharedPreferences("shortcut_widget", 0);
        int i = sharedPreferences.getInt("account" + this.appWidgetId, -1);
        if (i >= 0) {
            this.accountInstance = AccountInstance.getInstance(i);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("deleted");
        sb.append(this.appWidgetId);
        this.deleted = (sharedPreferences.getBoolean(sb.toString(), false) || this.accountInstance == null) ? true : z;
    }

    public void onCreate() {
        ApplicationLoader.postInitApplication();
    }

    public int getCount() {
        if (this.deleted) {
            return 1;
        }
        return this.dids.size() + 1;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:87:0x024c, code lost:
        if ((r0 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelMigrateFrom) == false) goto L_0x024f;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.widget.RemoteViews getViewAt(int r20) {
        /*
            r19 = this;
            r1 = r19
            r2 = r20
            boolean r0 = r1.deleted
            if (r0 == 0) goto L_0x0023
            android.widget.RemoteViews r0 = new android.widget.RemoteViews
            android.content.Context r2 = r1.mContext
            java.lang.String r2 = r2.getPackageName()
            int r3 = org.telegram.messenger.R.layout.widget_deleted
            r0.<init>(r2, r3)
            int r2 = org.telegram.messenger.R.id.widget_deleted_text
            int r3 = org.telegram.messenger.R.string.WidgetLoggedOff
            java.lang.String r4 = "WidgetLoggedOff"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r0.setTextViewText(r2, r3)
            return r0
        L_0x0023:
            java.util.ArrayList<java.lang.Long> r0 = r1.dids
            int r0 = r0.size()
            java.lang.String r3 = "currentAccount"
            r4 = 0
            if (r2 < r0) goto L_0x0070
            android.widget.RemoteViews r0 = new android.widget.RemoteViews
            android.content.Context r2 = r1.mContext
            java.lang.String r2 = r2.getPackageName()
            int r5 = org.telegram.messenger.R.layout.widget_edititem
            r0.<init>(r2, r5)
            int r2 = org.telegram.messenger.R.id.widget_edititem_text
            int r5 = org.telegram.messenger.R.string.TapToEditWidget
            java.lang.String r6 = "TapToEditWidget"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r0.setTextViewText(r2, r5)
            android.os.Bundle r2 = new android.os.Bundle
            r2.<init>()
            int r5 = r1.appWidgetId
            java.lang.String r6 = "appWidgetId"
            r2.putInt(r6, r5)
            java.lang.String r5 = "appWidgetType"
            r2.putInt(r5, r4)
            org.telegram.messenger.AccountInstance r4 = r1.accountInstance
            int r4 = r4.getCurrentAccount()
            r2.putInt(r3, r4)
            android.content.Intent r3 = new android.content.Intent
            r3.<init>()
            r3.putExtras(r2)
            int r2 = org.telegram.messenger.R.id.widget_edititem
            r0.setOnClickFillInIntent(r2, r3)
            return r0
        L_0x0070:
            java.util.ArrayList<java.lang.Long> r0 = r1.dids
            java.lang.Object r0 = r0.get(r2)
            r5 = r0
            java.lang.Long r5 = (java.lang.Long) r5
            long r6 = r5.longValue()
            boolean r0 = org.telegram.messenger.DialogObject.isUserDialog(r6)
            r6 = 0
            java.lang.String r8 = ""
            r9 = 0
            if (r0 == 0) goto L_0x00ed
            org.telegram.messenger.AccountInstance r0 = r1.accountInstance
            org.telegram.messenger.MessagesController r0 = r0.getMessagesController()
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r5)
            if (r0 == 0) goto L_0x00e9
            boolean r10 = org.telegram.messenger.UserObject.isUserSelf(r0)
            if (r10 == 0) goto L_0x00a3
            int r10 = org.telegram.messenger.R.string.SavedMessages
            java.lang.String r11 = "SavedMessages"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)
            goto L_0x00c9
        L_0x00a3:
            boolean r10 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC$User) r0)
            if (r10 == 0) goto L_0x00b2
            int r10 = org.telegram.messenger.R.string.RepliesTitle
            java.lang.String r11 = "RepliesTitle"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)
            goto L_0x00c9
        L_0x00b2:
            boolean r10 = org.telegram.messenger.UserObject.isDeleted(r0)
            if (r10 == 0) goto L_0x00c1
            int r10 = org.telegram.messenger.R.string.HiddenName
            java.lang.String r11 = "HiddenName"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)
            goto L_0x00c9
        L_0x00c1:
            java.lang.String r10 = r0.first_name
            java.lang.String r11 = r0.last_name
            java.lang.String r10 = org.telegram.messenger.ContactsController.formatName(r10, r11)
        L_0x00c9:
            boolean r11 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC$User) r0)
            if (r11 != 0) goto L_0x00ea
            boolean r11 = org.telegram.messenger.UserObject.isUserSelf(r0)
            if (r11 != 0) goto L_0x00ea
            org.telegram.tgnet.TLRPC$UserProfilePhoto r11 = r0.photo
            if (r11 == 0) goto L_0x00ea
            org.telegram.tgnet.TLRPC$FileLocation r11 = r11.photo_small
            if (r11 == 0) goto L_0x00ea
            long r12 = r11.volume_id
            int r14 = (r12 > r6 ? 1 : (r12 == r6 ? 0 : -1))
            if (r14 == 0) goto L_0x00ea
            int r6 = r11.local_id
            if (r6 == 0) goto L_0x00ea
            r6 = r9
            goto L_0x011f
        L_0x00e9:
            r10 = r8
        L_0x00ea:
            r6 = r9
            r11 = r6
            goto L_0x011f
        L_0x00ed:
            org.telegram.messenger.AccountInstance r0 = r1.accountInstance
            org.telegram.messenger.MessagesController r0 = r0.getMessagesController()
            long r10 = r5.longValue()
            long r10 = -r10
            java.lang.Long r10 = java.lang.Long.valueOf(r10)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r10)
            if (r0 == 0) goto L_0x011b
            java.lang.String r10 = r0.title
            org.telegram.tgnet.TLRPC$ChatPhoto r11 = r0.photo
            if (r11 == 0) goto L_0x0119
            org.telegram.tgnet.TLRPC$FileLocation r11 = r11.photo_small
            if (r11 == 0) goto L_0x0119
            long r12 = r11.volume_id
            int r14 = (r12 > r6 ? 1 : (r12 == r6 ? 0 : -1))
            if (r14 == 0) goto L_0x0119
            int r6 = r11.local_id
            if (r6 == 0) goto L_0x0119
            r6 = r0
            r0 = r9
            goto L_0x011f
        L_0x0119:
            r6 = r0
            goto L_0x011d
        L_0x011b:
            r6 = r0
            r10 = r8
        L_0x011d:
            r0 = r9
            r11 = r0
        L_0x011f:
            android.widget.RemoteViews r7 = new android.widget.RemoteViews
            android.content.Context r12 = r1.mContext
            java.lang.String r12 = r12.getPackageName()
            int r13 = org.telegram.messenger.R.layout.shortcut_widget_item
            r7.<init>(r12, r13)
            int r12 = org.telegram.messenger.R.id.shortcut_widget_item_text
            r7.setTextViewText(r12, r10)
            r10 = 1
            if (r11 == 0) goto L_0x0147
            int r12 = org.telegram.messenger.UserConfig.selectedAccount     // Catch:{ all -> 0x01df }
            org.telegram.messenger.FileLoader r12 = org.telegram.messenger.FileLoader.getInstance(r12)     // Catch:{ all -> 0x01df }
            java.io.File r11 = r12.getPathToAttach(r11, r10)     // Catch:{ all -> 0x01df }
            java.lang.String r11 = r11.toString()     // Catch:{ all -> 0x01df }
            android.graphics.Bitmap r11 = android.graphics.BitmapFactory.decodeFile(r11)     // Catch:{ all -> 0x01df }
            goto L_0x0148
        L_0x0147:
            r11 = r9
        L_0x0148:
            r12 = 1111490560(0x42400000, float:48.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)     // Catch:{ all -> 0x01df }
            android.graphics.Bitmap$Config r13 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x01df }
            android.graphics.Bitmap r13 = android.graphics.Bitmap.createBitmap(r12, r12, r13)     // Catch:{ all -> 0x01df }
            r13.eraseColor(r4)     // Catch:{ all -> 0x01df }
            android.graphics.Canvas r14 = new android.graphics.Canvas     // Catch:{ all -> 0x01df }
            r14.<init>(r13)     // Catch:{ all -> 0x01df }
            if (r11 != 0) goto L_0x0187
            if (r0 == 0) goto L_0x017b
            org.telegram.ui.Components.AvatarDrawable r11 = new org.telegram.ui.Components.AvatarDrawable     // Catch:{ all -> 0x01df }
            r11.<init>((org.telegram.tgnet.TLRPC$User) r0)     // Catch:{ all -> 0x01df }
            boolean r15 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC$User) r0)     // Catch:{ all -> 0x01df }
            if (r15 == 0) goto L_0x0171
            r0 = 12
            r11.setAvatarType(r0)     // Catch:{ all -> 0x01df }
            goto L_0x0180
        L_0x0171:
            boolean r0 = org.telegram.messenger.UserObject.isUserSelf(r0)     // Catch:{ all -> 0x01df }
            if (r0 == 0) goto L_0x0180
            r11.setAvatarType(r10)     // Catch:{ all -> 0x01df }
            goto L_0x0180
        L_0x017b:
            org.telegram.ui.Components.AvatarDrawable r11 = new org.telegram.ui.Components.AvatarDrawable     // Catch:{ all -> 0x01df }
            r11.<init>((org.telegram.tgnet.TLRPC$Chat) r6)     // Catch:{ all -> 0x01df }
        L_0x0180:
            r11.setBounds(r4, r4, r12, r12)     // Catch:{ all -> 0x01df }
            r11.draw(r14)     // Catch:{ all -> 0x01df }
            goto L_0x01d6
        L_0x0187:
            android.graphics.BitmapShader r0 = new android.graphics.BitmapShader     // Catch:{ all -> 0x01df }
            android.graphics.Shader$TileMode r15 = android.graphics.Shader.TileMode.CLAMP     // Catch:{ all -> 0x01df }
            r0.<init>(r11, r15, r15)     // Catch:{ all -> 0x01df }
            android.graphics.Paint r15 = r1.roundPaint     // Catch:{ all -> 0x01df }
            if (r15 != 0) goto L_0x01a0
            android.graphics.Paint r15 = new android.graphics.Paint     // Catch:{ all -> 0x01df }
            r15.<init>(r10)     // Catch:{ all -> 0x01df }
            r1.roundPaint = r15     // Catch:{ all -> 0x01df }
            android.graphics.RectF r15 = new android.graphics.RectF     // Catch:{ all -> 0x01df }
            r15.<init>()     // Catch:{ all -> 0x01df }
            r1.bitmapRect = r15     // Catch:{ all -> 0x01df }
        L_0x01a0:
            float r12 = (float) r12     // Catch:{ all -> 0x01df }
            int r15 = r11.getWidth()     // Catch:{ all -> 0x01df }
            float r15 = (float) r15     // Catch:{ all -> 0x01df }
            float r12 = r12 / r15
            r14.save()     // Catch:{ all -> 0x01df }
            r14.scale(r12, r12)     // Catch:{ all -> 0x01df }
            android.graphics.Paint r12 = r1.roundPaint     // Catch:{ all -> 0x01df }
            r12.setShader(r0)     // Catch:{ all -> 0x01df }
            android.graphics.RectF r0 = r1.bitmapRect     // Catch:{ all -> 0x01df }
            int r12 = r11.getWidth()     // Catch:{ all -> 0x01df }
            float r12 = (float) r12     // Catch:{ all -> 0x01df }
            int r15 = r11.getHeight()     // Catch:{ all -> 0x01df }
            float r15 = (float) r15     // Catch:{ all -> 0x01df }
            r10 = 0
            r0.set(r10, r10, r12, r15)     // Catch:{ all -> 0x01df }
            android.graphics.RectF r0 = r1.bitmapRect     // Catch:{ all -> 0x01df }
            int r10 = r11.getWidth()     // Catch:{ all -> 0x01df }
            float r10 = (float) r10     // Catch:{ all -> 0x01df }
            int r11 = r11.getHeight()     // Catch:{ all -> 0x01df }
            float r11 = (float) r11     // Catch:{ all -> 0x01df }
            android.graphics.Paint r12 = r1.roundPaint     // Catch:{ all -> 0x01df }
            r14.drawRoundRect(r0, r10, r11, r12)     // Catch:{ all -> 0x01df }
            r14.restore()     // Catch:{ all -> 0x01df }
        L_0x01d6:
            r14.setBitmap(r9)     // Catch:{ all -> 0x01df }
            int r0 = org.telegram.messenger.R.id.shortcut_widget_item_avatar     // Catch:{ all -> 0x01df }
            r7.setImageViewBitmap(r0, r13)     // Catch:{ all -> 0x01df }
            goto L_0x01e3
        L_0x01df:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x01e3:
            androidx.collection.LongSparseArray<org.telegram.messenger.MessageObject> r0 = r1.messageObjects
            long r10 = r5.longValue()
            java.lang.Object r0 = r0.get(r10)
            r10 = r0
            org.telegram.messenger.MessageObject r10 = (org.telegram.messenger.MessageObject) r10
            androidx.collection.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r0 = r1.dialogs
            long r11 = r5.longValue()
            java.lang.Object r0 = r0.get(r11)
            r11 = r0
            org.telegram.tgnet.TLRPC$Dialog r11 = (org.telegram.tgnet.TLRPC$Dialog) r11
            if (r10 == 0) goto L_0x0543
            long r12 = r10.getFromChatId()
            boolean r0 = org.telegram.messenger.DialogObject.isUserDialog(r12)
            if (r0 == 0) goto L_0x021a
            org.telegram.messenger.AccountInstance r0 = r1.accountInstance
            org.telegram.messenger.MessagesController r0 = r0.getMessagesController()
            java.lang.Long r12 = java.lang.Long.valueOf(r12)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r12)
            r12 = r0
            r0 = r9
            goto L_0x022a
        L_0x021a:
            org.telegram.messenger.AccountInstance r0 = r1.accountInstance
            org.telegram.messenger.MessagesController r0 = r0.getMessagesController()
            long r12 = -r12
            java.lang.Long r12 = java.lang.Long.valueOf(r12)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r12)
            r12 = r9
        L_0x022a:
            android.content.Context r13 = r1.mContext
            android.content.res.Resources r13 = r13.getResources()
            int r14 = org.telegram.messenger.R.color.widget_text
            int r13 = r13.getColor(r14)
            org.telegram.tgnet.TLRPC$Message r14 = r10.messageOwner
            boolean r14 = r14 instanceof org.telegram.tgnet.TLRPC$TL_messageService
            if (r14 == 0) goto L_0x025f
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r6)
            if (r0 == 0) goto L_0x024f
            org.telegram.tgnet.TLRPC$Message r0 = r10.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r0 = r0.action
            boolean r6 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageActionHistoryClear
            if (r6 != 0) goto L_0x0251
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelMigrateFrom
            if (r0 == 0) goto L_0x024f
            goto L_0x0251
        L_0x024f:
            java.lang.CharSequence r8 = r10.messageText
        L_0x0251:
            android.content.Context r0 = r1.mContext
            android.content.res.Resources r0 = r0.getResources()
            int r6 = org.telegram.messenger.R.color.widget_action_text
            int r13 = r0.getColor(r6)
            goto L_0x0528
        L_0x025f:
            java.lang.String r14 = "🖼 "
            java.lang.String r15 = "📎 "
            java.lang.String r16 = "🎧 "
            java.lang.String r9 = "🎧 %s - %s"
            java.lang.String r17 = "🎤 "
            java.lang.String r18 = "📹 "
            if (r6 == 0) goto L_0x0442
            if (r0 != 0) goto L_0x0442
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r6)
            if (r0 == 0) goto L_0x027b
            boolean r0 = org.telegram.messenger.ChatObject.isMegagroup(r6)
            if (r0 == 0) goto L_0x0442
        L_0x027b:
            boolean r0 = r10.isOutOwner()
            if (r0 == 0) goto L_0x028b
            int r0 = org.telegram.messenger.R.string.FromYou
            java.lang.String r6 = "FromYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r6, r0)
        L_0x0289:
            r6 = r0
            goto L_0x029b
        L_0x028b:
            if (r12 == 0) goto L_0x0298
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r12)
            java.lang.String r6 = "\n"
            java.lang.String r0 = r0.replace(r6, r8)
            goto L_0x0289
        L_0x0298:
            java.lang.String r0 = "DELETED"
            goto L_0x0289
        L_0x029b:
            java.lang.String r0 = "%2$s: ⁨%1$s⁩"
            java.lang.CharSequence r12 = r10.caption
            r4 = 150(0x96, float:2.1E-43)
            if (r12 == 0) goto L_0x0300
            java.lang.String r8 = r12.toString()
            int r9 = r8.length()
            if (r9 <= r4) goto L_0x02b2
            r9 = 0
            java.lang.String r8 = r8.substring(r9, r4)
        L_0x02b2:
            boolean r4 = r10.isVideo()
            if (r4 == 0) goto L_0x02bc
            r14 = r18
        L_0x02ba:
            r4 = 2
            goto L_0x02d7
        L_0x02bc:
            boolean r4 = r10.isVoice()
            if (r4 == 0) goto L_0x02c5
            r14 = r17
            goto L_0x02ba
        L_0x02c5:
            boolean r4 = r10.isMusic()
            if (r4 == 0) goto L_0x02ce
            r14 = r16
            goto L_0x02ba
        L_0x02ce:
            boolean r4 = r10.isPhoto()
            if (r4 == 0) goto L_0x02d5
            goto L_0x02ba
        L_0x02d5:
            r14 = r15
            goto L_0x02ba
        L_0x02d7:
            java.lang.Object[] r4 = new java.lang.Object[r4]
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            r9.append(r14)
            r12 = 32
            r14 = 10
            java.lang.String r8 = r8.replace(r14, r12)
            r9.append(r8)
            java.lang.String r8 = r9.toString()
            r9 = 0
            r4[r9] = r8
            r8 = 1
            r4[r8] = r6
            java.lang.String r0 = java.lang.String.format(r0, r4)
            android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r0)
            goto L_0x0426
        L_0x0300:
            org.telegram.tgnet.TLRPC$Message r12 = r10.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r12 = r12.media
            if (r12 == 0) goto L_0x03f2
            boolean r12 = r10.isMediaEmpty()
            if (r12 != 0) goto L_0x03f2
            android.content.Context r4 = r1.mContext
            android.content.res.Resources r4 = r4.getResources()
            int r8 = org.telegram.messenger.R.color.widget_action_text
            int r4 = r4.getColor(r8)
            org.telegram.tgnet.TLRPC$Message r8 = r10.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r8 = r8.media
            boolean r12 = r8 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            r13 = 18
            if (r12 == 0) goto L_0x0351
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r8 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r8
            int r9 = android.os.Build.VERSION.SDK_INT
            if (r9 < r13) goto L_0x0339
            r9 = 1
            java.lang.Object[] r12 = new java.lang.Object[r9]
            org.telegram.tgnet.TLRPC$Poll r8 = r8.poll
            java.lang.String r8 = r8.question
            r13 = 0
            r12[r13] = r8
            java.lang.String r8 = "📊 ⁨%s⁩"
            java.lang.String r8 = java.lang.String.format(r8, r12)
            goto L_0x0349
        L_0x0339:
            r9 = 1
            r13 = 0
            java.lang.Object[] r12 = new java.lang.Object[r9]
            org.telegram.tgnet.TLRPC$Poll r8 = r8.poll
            java.lang.String r8 = r8.question
            r12[r13] = r8
            java.lang.String r8 = "📊 %s"
            java.lang.String r8 = java.lang.String.format(r8, r12)
        L_0x0349:
            r9 = r8
            r8 = 2
            r12 = 32
            r13 = 1
            r14 = 0
            goto L_0x03c1
        L_0x0351:
            boolean r12 = r8 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r12 == 0) goto L_0x0380
            int r9 = android.os.Build.VERSION.SDK_INT
            if (r9 < r13) goto L_0x036a
            r9 = 1
            java.lang.Object[] r12 = new java.lang.Object[r9]
            org.telegram.tgnet.TLRPC$TL_game r8 = r8.game
            java.lang.String r8 = r8.title
            r14 = 0
            r12[r14] = r8
            java.lang.String r8 = "🎮 ⁨%s⁩"
            java.lang.String r8 = java.lang.String.format(r8, r12)
            goto L_0x037a
        L_0x036a:
            r9 = 1
            r14 = 0
            java.lang.Object[] r12 = new java.lang.Object[r9]
            org.telegram.tgnet.TLRPC$TL_game r8 = r8.game
            java.lang.String r8 = r8.title
            r12[r14] = r8
            java.lang.String r8 = "🎮 %s"
            java.lang.String r8 = java.lang.String.format(r8, r12)
        L_0x037a:
            r9 = r8
            r8 = 2
            r12 = 32
            r13 = 1
            goto L_0x03c1
        L_0x0380:
            r14 = 0
            int r8 = r10.type
            r12 = 14
            if (r8 != r12) goto L_0x03b7
            int r8 = android.os.Build.VERSION.SDK_INT
            if (r8 < r13) goto L_0x03a2
            r8 = 2
            java.lang.Object[] r9 = new java.lang.Object[r8]
            java.lang.String r12 = r10.getMusicAuthor()
            r9[r14] = r12
            java.lang.String r12 = r10.getMusicTitle()
            r13 = 1
            r9[r13] = r12
            java.lang.String r12 = "🎧 ⁨%s - %s⁩"
            java.lang.String r9 = java.lang.String.format(r12, r9)
            goto L_0x03bf
        L_0x03a2:
            r8 = 2
            r13 = 1
            java.lang.Object[] r12 = new java.lang.Object[r8]
            java.lang.String r15 = r10.getMusicAuthor()
            r12[r14] = r15
            java.lang.String r15 = r10.getMusicTitle()
            r12[r13] = r15
            java.lang.String r9 = java.lang.String.format(r9, r12)
            goto L_0x03bf
        L_0x03b7:
            r8 = 2
            r13 = 1
            java.lang.CharSequence r9 = r10.messageText
            java.lang.String r9 = r9.toString()
        L_0x03bf:
            r12 = 32
        L_0x03c1:
            r15 = 10
            java.lang.String r9 = r9.replace(r15, r12)
            java.lang.Object[] r12 = new java.lang.Object[r8]
            r12[r14] = r9
            r12[r13] = r6
            java.lang.String r0 = java.lang.String.format(r0, r12)
            android.text.SpannableStringBuilder r8 = android.text.SpannableStringBuilder.valueOf(r0)
            org.telegram.ui.Components.ForegroundColorSpanThemable r0 = new org.telegram.ui.Components.ForegroundColorSpanThemable     // Catch:{ Exception -> 0x03ec }
            java.lang.String r9 = "chats_attachMessage"
            r0.<init>(r9)     // Catch:{ Exception -> 0x03ec }
            int r9 = r6.length()     // Catch:{ Exception -> 0x03ec }
            r12 = 2
            int r9 = r9 + r12
            int r12 = r8.length()     // Catch:{ Exception -> 0x03ec }
            r13 = 33
            r8.setSpan(r0, r9, r12, r13)     // Catch:{ Exception -> 0x03ec }
            goto L_0x03f0
        L_0x03ec:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x03f0:
            r13 = r4
            goto L_0x0427
        L_0x03f2:
            org.telegram.tgnet.TLRPC$Message r9 = r10.messageOwner
            java.lang.String r9 = r9.message
            if (r9 == 0) goto L_0x0422
            int r8 = r9.length()
            if (r8 <= r4) goto L_0x0404
            r8 = 0
            java.lang.String r9 = r9.substring(r8, r4)
            goto L_0x0405
        L_0x0404:
            r8 = 0
        L_0x0405:
            r4 = 32
            r12 = 10
            java.lang.String r4 = r9.replace(r12, r4)
            java.lang.String r4 = r4.trim()
            r9 = 2
            java.lang.Object[] r9 = new java.lang.Object[r9]
            r9[r8] = r4
            r4 = 1
            r9[r4] = r6
            java.lang.String r0 = java.lang.String.format(r0, r9)
            android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r0)
            goto L_0x0426
        L_0x0422:
            android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r8)
        L_0x0426:
            r8 = r0
        L_0x0427:
            org.telegram.ui.Components.ForegroundColorSpanThemable r0 = new org.telegram.ui.Components.ForegroundColorSpanThemable     // Catch:{ Exception -> 0x043c }
            java.lang.String r4 = "chats_nameMessage"
            r0.<init>(r4)     // Catch:{ Exception -> 0x043c }
            int r4 = r6.length()     // Catch:{ Exception -> 0x043c }
            r6 = 1
            int r4 = r4 + r6
            r6 = 33
            r9 = 0
            r8.setSpan(r0, r9, r4, r6)     // Catch:{ Exception -> 0x043c }
            goto L_0x0528
        L_0x043c:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0528
        L_0x0442:
            org.telegram.tgnet.TLRPC$Message r0 = r10.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r4 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r4 == 0) goto L_0x045e
            org.telegram.tgnet.TLRPC$Photo r4 = r0.photo
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC$TL_photoEmpty
            if (r4 == 0) goto L_0x045e
            int r4 = r0.ttl_seconds
            if (r4 == 0) goto L_0x045e
            int r0 = org.telegram.messenger.R.string.AttachPhotoExpired
            java.lang.String r4 = "AttachPhotoExpired"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r4, r0)
            goto L_0x0528
        L_0x045e:
            boolean r4 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r4 == 0) goto L_0x0476
            org.telegram.tgnet.TLRPC$Document r4 = r0.document
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC$TL_documentEmpty
            if (r4 == 0) goto L_0x0476
            int r4 = r0.ttl_seconds
            if (r4 == 0) goto L_0x0476
            int r0 = org.telegram.messenger.R.string.AttachVideoExpired
            java.lang.String r4 = "AttachVideoExpired"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r4, r0)
            goto L_0x0528
        L_0x0476:
            java.lang.CharSequence r4 = r10.caption
            if (r4 == 0) goto L_0x04b0
            boolean r0 = r10.isVideo()
            if (r0 == 0) goto L_0x0483
            r14 = r18
            goto L_0x049d
        L_0x0483:
            boolean r0 = r10.isVoice()
            if (r0 == 0) goto L_0x048c
            r14 = r17
            goto L_0x049d
        L_0x048c:
            boolean r0 = r10.isMusic()
            if (r0 == 0) goto L_0x0495
            r14 = r16
            goto L_0x049d
        L_0x0495:
            boolean r0 = r10.isPhoto()
            if (r0 == 0) goto L_0x049c
            goto L_0x049d
        L_0x049c:
            r14 = r15
        L_0x049d:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r14)
            java.lang.CharSequence r4 = r10.caption
            r0.append(r4)
            java.lang.String r8 = r0.toString()
            goto L_0x0528
        L_0x04b0:
            boolean r4 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r4 == 0) goto L_0x04cd
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r0 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r0
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r6 = "📊 "
            r4.append(r6)
            org.telegram.tgnet.TLRPC$Poll r0 = r0.poll
            java.lang.String r0 = r0.question
            r4.append(r0)
            java.lang.String r0 = r4.toString()
        L_0x04cb:
            r8 = r0
            goto L_0x0510
        L_0x04cd:
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r0 == 0) goto L_0x04eb
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r4 = "🎮 "
            r0.append(r4)
            org.telegram.tgnet.TLRPC$Message r4 = r10.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            org.telegram.tgnet.TLRPC$TL_game r4 = r4.game
            java.lang.String r4 = r4.title
            r0.append(r4)
            java.lang.String r0 = r0.toString()
            goto L_0x04cb
        L_0x04eb:
            int r0 = r10.type
            r4 = 14
            if (r0 != r4) goto L_0x0507
            r0 = 2
            java.lang.Object[] r0 = new java.lang.Object[r0]
            java.lang.String r4 = r10.getMusicAuthor()
            r6 = 0
            r0[r6] = r4
            java.lang.String r4 = r10.getMusicTitle()
            r6 = 1
            r0[r6] = r4
            java.lang.String r0 = java.lang.String.format(r9, r0)
            goto L_0x04cb
        L_0x0507:
            java.lang.CharSequence r0 = r10.messageText
            java.util.ArrayList<java.lang.String> r4 = r10.highlightedWords
            r6 = 0
            org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r0, (java.util.ArrayList<java.lang.String>) r4, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r6)
            goto L_0x04cb
        L_0x0510:
            org.telegram.tgnet.TLRPC$Message r0 = r10.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            if (r0 == 0) goto L_0x0528
            boolean r0 = r10.isMediaEmpty()
            if (r0 != 0) goto L_0x0528
            android.content.Context r0 = r1.mContext
            android.content.res.Resources r0 = r0.getResources()
            int r4 = org.telegram.messenger.R.color.widget_action_text
            int r13 = r0.getColor(r4)
        L_0x0528:
            int r0 = org.telegram.messenger.R.id.shortcut_widget_item_time
            org.telegram.tgnet.TLRPC$Message r4 = r10.messageOwner
            int r4 = r4.date
            long r9 = (long) r4
            java.lang.String r4 = org.telegram.messenger.LocaleController.stringForMessageListDate(r9)
            r7.setTextViewText(r0, r4)
            int r0 = org.telegram.messenger.R.id.shortcut_widget_item_message
            java.lang.String r4 = r8.toString()
            r7.setTextViewText(r0, r4)
            r7.setTextColor(r0, r13)
            goto L_0x055e
        L_0x0543:
            if (r11 == 0) goto L_0x0554
            int r0 = r11.last_message_date
            if (r0 == 0) goto L_0x0554
            int r4 = org.telegram.messenger.R.id.shortcut_widget_item_time
            long r9 = (long) r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.stringForMessageListDate(r9)
            r7.setTextViewText(r4, r0)
            goto L_0x0559
        L_0x0554:
            int r0 = org.telegram.messenger.R.id.shortcut_widget_item_time
            r7.setTextViewText(r0, r8)
        L_0x0559:
            int r0 = org.telegram.messenger.R.id.shortcut_widget_item_message
            r7.setTextViewText(r0, r8)
        L_0x055e:
            r0 = 8
            if (r11 == 0) goto L_0x05a3
            int r4 = r11.unread_count
            if (r4 <= 0) goto L_0x05a3
            int r6 = org.telegram.messenger.R.id.shortcut_widget_item_badge
            r8 = 1
            java.lang.Object[] r9 = new java.lang.Object[r8]
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            r8 = 0
            r9[r8] = r4
            java.lang.String r4 = "%d"
            java.lang.String r4 = java.lang.String.format(r4, r9)
            r7.setTextViewText(r6, r4)
            r7.setViewVisibility(r6, r8)
            org.telegram.messenger.AccountInstance r4 = r1.accountInstance
            org.telegram.messenger.MessagesController r4 = r4.getMessagesController()
            long r9 = r11.id
            boolean r4 = r4.isDialogMuted(r9)
            java.lang.String r9 = "setBackgroundResource"
            java.lang.String r10 = "setEnabled"
            if (r4 == 0) goto L_0x0599
            r7.setBoolean(r6, r10, r8)
            int r4 = org.telegram.messenger.R.drawable.widget_badge_muted_background
            r7.setInt(r6, r9, r4)
            goto L_0x05a9
        L_0x0599:
            r4 = 1
            r7.setBoolean(r6, r10, r4)
            int r4 = org.telegram.messenger.R.drawable.widget_badge_background
            r7.setInt(r6, r9, r4)
            goto L_0x05a9
        L_0x05a3:
            r8 = 0
            int r4 = org.telegram.messenger.R.id.shortcut_widget_item_badge
            r7.setViewVisibility(r4, r0)
        L_0x05a9:
            android.os.Bundle r4 = new android.os.Bundle
            r4.<init>()
            long r9 = r5.longValue()
            boolean r6 = org.telegram.messenger.DialogObject.isUserDialog(r9)
            if (r6 == 0) goto L_0x05c2
            long r5 = r5.longValue()
            java.lang.String r9 = "userId"
            r4.putLong(r9, r5)
            goto L_0x05cc
        L_0x05c2:
            long r5 = r5.longValue()
            long r5 = -r5
            java.lang.String r9 = "chatId"
            r4.putLong(r9, r5)
        L_0x05cc:
            org.telegram.messenger.AccountInstance r5 = r1.accountInstance
            int r5 = r5.getCurrentAccount()
            r4.putInt(r3, r5)
            android.content.Intent r3 = new android.content.Intent
            r3.<init>()
            r3.putExtras(r4)
            int r4 = org.telegram.messenger.R.id.shortcut_widget_item
            r7.setOnClickFillInIntent(r4, r3)
            int r3 = org.telegram.messenger.R.id.shortcut_widget_item_divider
            int r4 = r19.getCount()
            if (r2 != r4) goto L_0x05ed
            r4 = 8
            goto L_0x05ee
        L_0x05ed:
            r4 = 0
        L_0x05ee:
            r7.setViewVisibility(r3, r4)
            return r7
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ChatsRemoteViewsFactory.getViewAt(int):android.widget.RemoteViews");
    }

    public void onDataSetChanged() {
        this.dids.clear();
        this.messageObjects.clear();
        AccountInstance accountInstance2 = this.accountInstance;
        if (accountInstance2 != null && accountInstance2.getUserConfig().isClientActivated()) {
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            LongSparseArray longSparseArray = new LongSparseArray();
            this.accountInstance.getMessagesStorage().getWidgetDialogs(this.appWidgetId, 0, this.dids, this.dialogs, longSparseArray, arrayList, arrayList2);
            this.accountInstance.getMessagesController().putUsers(arrayList, true);
            this.accountInstance.getMessagesController().putChats(arrayList2, true);
            this.messageObjects.clear();
            int size = longSparseArray.size();
            for (int i = 0; i < size; i++) {
                this.messageObjects.put(longSparseArray.keyAt(i), new MessageObject(this.accountInstance.getCurrentAccount(), (TLRPC$Message) longSparseArray.valueAt(i), (LongSparseArray<TLRPC$User>) null, (LongSparseArray<TLRPC$Chat>) null, false, true));
            }
        }
    }
}
