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

    /* JADX WARNING: Code restructure failed: missing block: B:87:0x0258, code lost:
        if ((r0 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelMigrateFrom) == false) goto L_0x025b;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.widget.RemoteViews getViewAt(int r20) {
        /*
            r19 = this;
            r1 = r19
            r2 = r20
            boolean r0 = r1.deleted
            if (r0 == 0) goto L_0x0026
            android.widget.RemoteViews r0 = new android.widget.RemoteViews
            android.content.Context r2 = r1.mContext
            java.lang.String r2 = r2.getPackageName()
            r3 = 2131427356(0x7f0b001c, float:1.8476326E38)
            r0.<init>(r2, r3)
            r2 = 2131230948(0x7var_e4, float:1.8077963E38)
            r3 = 2131629000(0x7f0e13c8, float:1.8885309E38)
            java.lang.String r4 = "WidgetLoggedOff"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r0.setTextViewText(r2, r3)
            return r0
        L_0x0026:
            java.util.ArrayList<java.lang.Long> r0 = r1.dids
            int r0 = r0.size()
            java.lang.String r3 = "currentAccount"
            r4 = 0
            if (r2 < r0) goto L_0x0077
            android.widget.RemoteViews r0 = new android.widget.RemoteViews
            android.content.Context r2 = r1.mContext
            java.lang.String r2 = r2.getPackageName()
            r5 = 2131427357(0x7f0b001d, float:1.8476328E38)
            r0.<init>(r2, r5)
            r2 = 2131230950(0x7var_e6, float:1.8077967E38)
            r5 = 2131628319(0x7f0e111f, float:1.8883927E38)
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
            r2 = 2131230949(0x7var_e5, float:1.8077965E38)
            r0.setOnClickFillInIntent(r2, r3)
            return r0
        L_0x0077:
            java.util.ArrayList<java.lang.Long> r0 = r1.dids
            java.lang.Object r0 = r0.get(r2)
            r5 = r0
            java.lang.Long r5 = (java.lang.Long) r5
            long r6 = r5.longValue()
            boolean r0 = org.telegram.messenger.DialogObject.isUserDialog(r6)
            r6 = 0
            java.lang.String r8 = ""
            r9 = 0
            if (r0 == 0) goto L_0x00f7
            org.telegram.messenger.AccountInstance r0 = r1.accountInstance
            org.telegram.messenger.MessagesController r0 = r0.getMessagesController()
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r5)
            if (r0 == 0) goto L_0x00f3
            boolean r10 = org.telegram.messenger.UserObject.isUserSelf(r0)
            if (r10 == 0) goto L_0x00ab
            r10 = 2131627856(0x7f0e0var_, float:1.8882988E38)
            java.lang.String r11 = "SavedMessages"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)
            goto L_0x00d3
        L_0x00ab:
            boolean r10 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC$User) r0)
            if (r10 == 0) goto L_0x00bb
            r10 = 2131627715(0x7f0e0ec3, float:1.8882702E38)
            java.lang.String r11 = "RepliesTitle"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)
            goto L_0x00d3
        L_0x00bb:
            boolean r10 = org.telegram.messenger.UserObject.isDeleted(r0)
            if (r10 == 0) goto L_0x00cb
            r10 = 2131626016(0x7f0e0820, float:1.8879256E38)
            java.lang.String r11 = "HiddenName"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)
            goto L_0x00d3
        L_0x00cb:
            java.lang.String r10 = r0.first_name
            java.lang.String r11 = r0.last_name
            java.lang.String r10 = org.telegram.messenger.ContactsController.formatName(r10, r11)
        L_0x00d3:
            boolean r11 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC$User) r0)
            if (r11 != 0) goto L_0x00f4
            boolean r11 = org.telegram.messenger.UserObject.isUserSelf(r0)
            if (r11 != 0) goto L_0x00f4
            org.telegram.tgnet.TLRPC$UserProfilePhoto r11 = r0.photo
            if (r11 == 0) goto L_0x00f4
            org.telegram.tgnet.TLRPC$FileLocation r11 = r11.photo_small
            if (r11 == 0) goto L_0x00f4
            long r12 = r11.volume_id
            int r14 = (r12 > r6 ? 1 : (r12 == r6 ? 0 : -1))
            if (r14 == 0) goto L_0x00f4
            int r6 = r11.local_id
            if (r6 == 0) goto L_0x00f4
            r6 = r9
            goto L_0x0129
        L_0x00f3:
            r10 = r8
        L_0x00f4:
            r6 = r9
            r11 = r6
            goto L_0x0129
        L_0x00f7:
            org.telegram.messenger.AccountInstance r0 = r1.accountInstance
            org.telegram.messenger.MessagesController r0 = r0.getMessagesController()
            long r10 = r5.longValue()
            long r10 = -r10
            java.lang.Long r10 = java.lang.Long.valueOf(r10)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r10)
            if (r0 == 0) goto L_0x0125
            java.lang.String r10 = r0.title
            org.telegram.tgnet.TLRPC$ChatPhoto r11 = r0.photo
            if (r11 == 0) goto L_0x0123
            org.telegram.tgnet.TLRPC$FileLocation r11 = r11.photo_small
            if (r11 == 0) goto L_0x0123
            long r12 = r11.volume_id
            int r14 = (r12 > r6 ? 1 : (r12 == r6 ? 0 : -1))
            if (r14 == 0) goto L_0x0123
            int r6 = r11.local_id
            if (r6 == 0) goto L_0x0123
            r6 = r0
            r0 = r9
            goto L_0x0129
        L_0x0123:
            r6 = r0
            goto L_0x0127
        L_0x0125:
            r6 = r0
            r10 = r8
        L_0x0127:
            r0 = r9
            r11 = r0
        L_0x0129:
            android.widget.RemoteViews r7 = new android.widget.RemoteViews
            android.content.Context r12 = r1.mContext
            java.lang.String r12 = r12.getPackageName()
            r13 = 2131427349(0x7f0b0015, float:1.8476312E38)
            r7.<init>(r12, r13)
            r12 = 2131230914(0x7var_c2, float:1.8077894E38)
            r7.setTextViewText(r12, r10)
            r10 = 1
            if (r11 == 0) goto L_0x014d
            java.io.File r11 = org.telegram.messenger.FileLoader.getPathToAttach(r11, r10)     // Catch:{ all -> 0x01e7 }
            java.lang.String r11 = r11.toString()     // Catch:{ all -> 0x01e7 }
            android.graphics.Bitmap r11 = android.graphics.BitmapFactory.decodeFile(r11)     // Catch:{ all -> 0x01e7 }
            goto L_0x014e
        L_0x014d:
            r11 = r9
        L_0x014e:
            r12 = 1111490560(0x42400000, float:48.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)     // Catch:{ all -> 0x01e7 }
            android.graphics.Bitmap$Config r13 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x01e7 }
            android.graphics.Bitmap r13 = android.graphics.Bitmap.createBitmap(r12, r12, r13)     // Catch:{ all -> 0x01e7 }
            r13.eraseColor(r4)     // Catch:{ all -> 0x01e7 }
            android.graphics.Canvas r14 = new android.graphics.Canvas     // Catch:{ all -> 0x01e7 }
            r14.<init>(r13)     // Catch:{ all -> 0x01e7 }
            if (r11 != 0) goto L_0x018d
            if (r0 == 0) goto L_0x0181
            org.telegram.ui.Components.AvatarDrawable r11 = new org.telegram.ui.Components.AvatarDrawable     // Catch:{ all -> 0x01e7 }
            r11.<init>((org.telegram.tgnet.TLRPC$User) r0)     // Catch:{ all -> 0x01e7 }
            boolean r15 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC$User) r0)     // Catch:{ all -> 0x01e7 }
            if (r15 == 0) goto L_0x0177
            r0 = 12
            r11.setAvatarType(r0)     // Catch:{ all -> 0x01e7 }
            goto L_0x0186
        L_0x0177:
            boolean r0 = org.telegram.messenger.UserObject.isUserSelf(r0)     // Catch:{ all -> 0x01e7 }
            if (r0 == 0) goto L_0x0186
            r11.setAvatarType(r10)     // Catch:{ all -> 0x01e7 }
            goto L_0x0186
        L_0x0181:
            org.telegram.ui.Components.AvatarDrawable r11 = new org.telegram.ui.Components.AvatarDrawable     // Catch:{ all -> 0x01e7 }
            r11.<init>((org.telegram.tgnet.TLRPC$Chat) r6)     // Catch:{ all -> 0x01e7 }
        L_0x0186:
            r11.setBounds(r4, r4, r12, r12)     // Catch:{ all -> 0x01e7 }
            r11.draw(r14)     // Catch:{ all -> 0x01e7 }
            goto L_0x01dd
        L_0x018d:
            android.graphics.BitmapShader r0 = new android.graphics.BitmapShader     // Catch:{ all -> 0x01e7 }
            android.graphics.Shader$TileMode r15 = android.graphics.Shader.TileMode.CLAMP     // Catch:{ all -> 0x01e7 }
            r0.<init>(r11, r15, r15)     // Catch:{ all -> 0x01e7 }
            android.graphics.Paint r15 = r1.roundPaint     // Catch:{ all -> 0x01e7 }
            if (r15 != 0) goto L_0x01a6
            android.graphics.Paint r15 = new android.graphics.Paint     // Catch:{ all -> 0x01e7 }
            r15.<init>(r10)     // Catch:{ all -> 0x01e7 }
            r1.roundPaint = r15     // Catch:{ all -> 0x01e7 }
            android.graphics.RectF r15 = new android.graphics.RectF     // Catch:{ all -> 0x01e7 }
            r15.<init>()     // Catch:{ all -> 0x01e7 }
            r1.bitmapRect = r15     // Catch:{ all -> 0x01e7 }
        L_0x01a6:
            float r12 = (float) r12     // Catch:{ all -> 0x01e7 }
            int r15 = r11.getWidth()     // Catch:{ all -> 0x01e7 }
            float r15 = (float) r15     // Catch:{ all -> 0x01e7 }
            float r12 = r12 / r15
            r14.save()     // Catch:{ all -> 0x01e7 }
            r14.scale(r12, r12)     // Catch:{ all -> 0x01e7 }
            android.graphics.Paint r12 = r1.roundPaint     // Catch:{ all -> 0x01e7 }
            r12.setShader(r0)     // Catch:{ all -> 0x01e7 }
            android.graphics.RectF r0 = r1.bitmapRect     // Catch:{ all -> 0x01e7 }
            r12 = 0
            r15 = 0
            int r10 = r11.getWidth()     // Catch:{ all -> 0x01e7 }
            float r10 = (float) r10     // Catch:{ all -> 0x01e7 }
            int r4 = r11.getHeight()     // Catch:{ all -> 0x01e7 }
            float r4 = (float) r4     // Catch:{ all -> 0x01e7 }
            r0.set(r12, r15, r10, r4)     // Catch:{ all -> 0x01e7 }
            android.graphics.RectF r0 = r1.bitmapRect     // Catch:{ all -> 0x01e7 }
            int r4 = r11.getWidth()     // Catch:{ all -> 0x01e7 }
            float r4 = (float) r4     // Catch:{ all -> 0x01e7 }
            int r10 = r11.getHeight()     // Catch:{ all -> 0x01e7 }
            float r10 = (float) r10     // Catch:{ all -> 0x01e7 }
            android.graphics.Paint r11 = r1.roundPaint     // Catch:{ all -> 0x01e7 }
            r14.drawRoundRect(r0, r4, r10, r11)     // Catch:{ all -> 0x01e7 }
            r14.restore()     // Catch:{ all -> 0x01e7 }
        L_0x01dd:
            r14.setBitmap(r9)     // Catch:{ all -> 0x01e7 }
            r0 = 2131230910(0x7var_be, float:1.8077886E38)
            r7.setImageViewBitmap(r0, r13)     // Catch:{ all -> 0x01e7 }
            goto L_0x01eb
        L_0x01e7:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x01eb:
            androidx.collection.LongSparseArray<org.telegram.messenger.MessageObject> r0 = r1.messageObjects
            long r10 = r5.longValue()
            java.lang.Object r0 = r0.get(r10)
            r4 = r0
            org.telegram.messenger.MessageObject r4 = (org.telegram.messenger.MessageObject) r4
            androidx.collection.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r0 = r1.dialogs
            long r10 = r5.longValue()
            java.lang.Object r0 = r0.get(r10)
            r10 = r0
            org.telegram.tgnet.TLRPC$Dialog r10 = (org.telegram.tgnet.TLRPC$Dialog) r10
            if (r4 == 0) goto L_0x054b
            long r13 = r4.getFromChatId()
            boolean r0 = org.telegram.messenger.DialogObject.isUserDialog(r13)
            if (r0 == 0) goto L_0x0222
            org.telegram.messenger.AccountInstance r0 = r1.accountInstance
            org.telegram.messenger.MessagesController r0 = r0.getMessagesController()
            java.lang.Long r13 = java.lang.Long.valueOf(r13)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r13)
            r13 = r0
            r0 = r9
            goto L_0x0232
        L_0x0222:
            org.telegram.messenger.AccountInstance r0 = r1.accountInstance
            org.telegram.messenger.MessagesController r0 = r0.getMessagesController()
            long r13 = -r13
            java.lang.Long r13 = java.lang.Long.valueOf(r13)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r13)
            r13 = r9
        L_0x0232:
            android.content.Context r14 = r1.mContext
            android.content.res.Resources r14 = r14.getResources()
            r15 = 2131034146(0x7var_, float:1.7678801E38)
            int r14 = r14.getColor(r15)
            org.telegram.tgnet.TLRPC$Message r15 = r4.messageOwner
            boolean r15 = r15 instanceof org.telegram.tgnet.TLRPC$TL_messageService
            r11 = 2131034141(0x7var_d, float:1.7678791E38)
            if (r15 == 0) goto L_0x0269
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r6)
            if (r0 == 0) goto L_0x025b
            org.telegram.tgnet.TLRPC$Message r0 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r0 = r0.action
            boolean r6 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageActionHistoryClear
            if (r6 != 0) goto L_0x025d
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelMigrateFrom
            if (r0 == 0) goto L_0x025b
            goto L_0x025d
        L_0x025b:
            java.lang.CharSequence r8 = r4.messageText
        L_0x025d:
            android.content.Context r0 = r1.mContext
            android.content.res.Resources r0 = r0.getResources()
            int r14 = r0.getColor(r11)
            goto L_0x052e
        L_0x0269:
            java.lang.String r15 = "🎧 %s - %s"
            java.lang.String r17 = "🎤 "
            java.lang.String r18 = "📹 "
            r9 = 2
            if (r6 == 0) goto L_0x0442
            if (r0 != 0) goto L_0x0442
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r6)
            if (r0 == 0) goto L_0x0280
            boolean r0 = org.telegram.messenger.ChatObject.isMegagroup(r6)
            if (r0 == 0) goto L_0x0442
        L_0x0280:
            boolean r0 = r4.isOutOwner()
            if (r0 == 0) goto L_0x0291
            r0 = 2131625923(0x7f0e07c3, float:1.8879068E38)
            java.lang.String r6 = "FromYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r6, r0)
        L_0x028f:
            r6 = r0
            goto L_0x02a1
        L_0x0291:
            if (r13 == 0) goto L_0x029e
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r13)
            java.lang.String r6 = "\n"
            java.lang.String r0 = r0.replace(r6, r8)
            goto L_0x028f
        L_0x029e:
            java.lang.String r0 = "DELETED"
            goto L_0x028f
        L_0x02a1:
            java.lang.String r0 = "%2$s: ⁨%1$s⁩"
            java.lang.CharSequence r13 = r4.caption
            r12 = 32
            r11 = 150(0x96, float:2.1E-43)
            if (r13 == 0) goto L_0x0308
            java.lang.String r8 = r13.toString()
            int r13 = r8.length()
            if (r13 <= r11) goto L_0x02ba
            r13 = 0
            java.lang.String r8 = r8.substring(r13, r11)
        L_0x02ba:
            boolean r11 = r4.isVideo()
            if (r11 == 0) goto L_0x02c3
            r11 = r18
            goto L_0x02e1
        L_0x02c3:
            boolean r11 = r4.isVoice()
            if (r11 == 0) goto L_0x02cc
        L_0x02c9:
            r11 = r17
            goto L_0x02e1
        L_0x02cc:
            boolean r11 = r4.isMusic()
            if (r11 == 0) goto L_0x02d5
            java.lang.String r17 = "🎧 "
            goto L_0x02c9
        L_0x02d5:
            boolean r11 = r4.isPhoto()
            if (r11 == 0) goto L_0x02de
            java.lang.String r17 = "🖼 "
            goto L_0x02c9
        L_0x02de:
            java.lang.String r17 = "📎 "
            goto L_0x02c9
        L_0x02e1:
            java.lang.Object[] r9 = new java.lang.Object[r9]
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>()
            r13.append(r11)
            r11 = 10
            java.lang.String r8 = r8.replace(r11, r12)
            r13.append(r8)
            java.lang.String r8 = r13.toString()
            r11 = 0
            r9[r11] = r8
            r8 = 1
            r9[r8] = r6
            java.lang.String r0 = java.lang.String.format(r0, r9)
            android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r0)
            goto L_0x0426
        L_0x0308:
            org.telegram.tgnet.TLRPC$Message r13 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r13 = r13.media
            if (r13 == 0) goto L_0x03f5
            boolean r13 = r4.isMediaEmpty()
            if (r13 != 0) goto L_0x03f5
            android.content.Context r8 = r1.mContext
            android.content.res.Resources r8 = r8.getResources()
            r11 = 2131034141(0x7var_d, float:1.7678791E38)
            int r8 = r8.getColor(r11)
            org.telegram.tgnet.TLRPC$Message r11 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r11 = r11.media
            boolean r13 = r11 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            r14 = 18
            if (r13 == 0) goto L_0x0358
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r11 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r11
            int r13 = android.os.Build.VERSION.SDK_INT
            if (r13 < r14) goto L_0x0342
            r13 = 1
            java.lang.Object[] r14 = new java.lang.Object[r13]
            org.telegram.tgnet.TLRPC$Poll r11 = r11.poll
            java.lang.String r11 = r11.question
            r15 = 0
            r14[r15] = r11
            java.lang.String r11 = "📊 ⁨%s⁩"
            java.lang.String r11 = java.lang.String.format(r11, r14)
            goto L_0x0352
        L_0x0342:
            r13 = 1
            r15 = 0
            java.lang.Object[] r14 = new java.lang.Object[r13]
            org.telegram.tgnet.TLRPC$Poll r11 = r11.poll
            java.lang.String r11 = r11.question
            r14[r15] = r11
            java.lang.String r11 = "📊 %s"
            java.lang.String r11 = java.lang.String.format(r11, r14)
        L_0x0352:
            r13 = 10
            r14 = 1
            r16 = 0
            goto L_0x03c6
        L_0x0358:
            boolean r13 = r11 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r13 == 0) goto L_0x0387
            int r13 = android.os.Build.VERSION.SDK_INT
            if (r13 < r14) goto L_0x0372
            r13 = 1
            java.lang.Object[] r14 = new java.lang.Object[r13]
            org.telegram.tgnet.TLRPC$TL_game r11 = r11.game
            java.lang.String r11 = r11.title
            r16 = 0
            r14[r16] = r11
            java.lang.String r11 = "🎮 ⁨%s⁩"
            java.lang.String r11 = java.lang.String.format(r11, r14)
            goto L_0x0383
        L_0x0372:
            r13 = 1
            r16 = 0
            java.lang.Object[] r14 = new java.lang.Object[r13]
            org.telegram.tgnet.TLRPC$TL_game r11 = r11.game
            java.lang.String r11 = r11.title
            r14[r16] = r11
            java.lang.String r11 = "🎮 %s"
            java.lang.String r11 = java.lang.String.format(r11, r14)
        L_0x0383:
            r13 = 10
            r14 = 1
            goto L_0x03c6
        L_0x0387:
            r16 = 0
            int r11 = r4.type
            r13 = 14
            if (r11 != r13) goto L_0x03bd
            int r11 = android.os.Build.VERSION.SDK_INT
            if (r11 < r14) goto L_0x03a9
            java.lang.Object[] r11 = new java.lang.Object[r9]
            java.lang.String r13 = r4.getMusicAuthor()
            r11[r16] = r13
            java.lang.String r13 = r4.getMusicTitle()
            r14 = 1
            r11[r14] = r13
            java.lang.String r13 = "🎧 ⁨%s - %s⁩"
            java.lang.String r11 = java.lang.String.format(r13, r11)
            goto L_0x03c4
        L_0x03a9:
            r14 = 1
            java.lang.Object[] r11 = new java.lang.Object[r9]
            java.lang.String r13 = r4.getMusicAuthor()
            r11[r16] = r13
            java.lang.String r13 = r4.getMusicTitle()
            r11[r14] = r13
            java.lang.String r11 = java.lang.String.format(r15, r11)
            goto L_0x03c4
        L_0x03bd:
            r14 = 1
            java.lang.CharSequence r11 = r4.messageText
            java.lang.String r11 = r11.toString()
        L_0x03c4:
            r13 = 10
        L_0x03c6:
            java.lang.String r11 = r11.replace(r13, r12)
            java.lang.Object[] r12 = new java.lang.Object[r9]
            r12[r16] = r11
            r12[r14] = r6
            java.lang.String r0 = java.lang.String.format(r0, r12)
            android.text.SpannableStringBuilder r11 = android.text.SpannableStringBuilder.valueOf(r0)
            org.telegram.ui.Components.ForegroundColorSpanThemable r0 = new org.telegram.ui.Components.ForegroundColorSpanThemable     // Catch:{ Exception -> 0x03ee }
            java.lang.String r12 = "chats_attachMessage"
            r0.<init>(r12)     // Catch:{ Exception -> 0x03ee }
            int r12 = r6.length()     // Catch:{ Exception -> 0x03ee }
            int r12 = r12 + r9
            int r9 = r11.length()     // Catch:{ Exception -> 0x03ee }
            r13 = 33
            r11.setSpan(r0, r12, r9, r13)     // Catch:{ Exception -> 0x03ee }
            goto L_0x03f2
        L_0x03ee:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x03f2:
            r14 = r8
            r8 = r11
            goto L_0x0427
        L_0x03f5:
            org.telegram.tgnet.TLRPC$Message r13 = r4.messageOwner
            java.lang.String r13 = r13.message
            if (r13 == 0) goto L_0x0422
            int r8 = r13.length()
            if (r8 <= r11) goto L_0x0407
            r8 = 0
            java.lang.String r13 = r13.substring(r8, r11)
            goto L_0x0408
        L_0x0407:
            r8 = 0
        L_0x0408:
            r11 = 10
            java.lang.String r11 = r13.replace(r11, r12)
            java.lang.String r11 = r11.trim()
            java.lang.Object[] r9 = new java.lang.Object[r9]
            r9[r8] = r11
            r8 = 1
            r9[r8] = r6
            java.lang.String r0 = java.lang.String.format(r0, r9)
            android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r0)
            goto L_0x0426
        L_0x0422:
            android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r8)
        L_0x0426:
            r8 = r0
        L_0x0427:
            org.telegram.ui.Components.ForegroundColorSpanThemable r0 = new org.telegram.ui.Components.ForegroundColorSpanThemable     // Catch:{ Exception -> 0x043c }
            java.lang.String r9 = "chats_nameMessage"
            r0.<init>(r9)     // Catch:{ Exception -> 0x043c }
            int r6 = r6.length()     // Catch:{ Exception -> 0x043c }
            r9 = 1
            int r6 = r6 + r9
            r9 = 33
            r11 = 0
            r8.setSpan(r0, r11, r6, r9)     // Catch:{ Exception -> 0x043c }
            goto L_0x052e
        L_0x043c:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x052e
        L_0x0442:
            org.telegram.tgnet.TLRPC$Message r0 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r6 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r6 == 0) goto L_0x045f
            org.telegram.tgnet.TLRPC$Photo r6 = r0.photo
            boolean r6 = r6 instanceof org.telegram.tgnet.TLRPC$TL_photoEmpty
            if (r6 == 0) goto L_0x045f
            int r6 = r0.ttl_seconds
            if (r6 == 0) goto L_0x045f
            r0 = 2131624443(0x7f0e01fb, float:1.8876066E38)
            java.lang.String r6 = "AttachPhotoExpired"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r6, r0)
            goto L_0x052e
        L_0x045f:
            boolean r6 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r6 == 0) goto L_0x0478
            org.telegram.tgnet.TLRPC$Document r6 = r0.document
            boolean r6 = r6 instanceof org.telegram.tgnet.TLRPC$TL_documentEmpty
            if (r6 == 0) goto L_0x0478
            int r6 = r0.ttl_seconds
            if (r6 == 0) goto L_0x0478
            r0 = 2131624449(0x7f0e0201, float:1.8876078E38)
            java.lang.String r6 = "AttachVideoExpired"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r6, r0)
            goto L_0x052e
        L_0x0478:
            java.lang.CharSequence r6 = r4.caption
            if (r6 == 0) goto L_0x04b6
            boolean r0 = r4.isVideo()
            if (r0 == 0) goto L_0x0485
            r0 = r18
            goto L_0x04a3
        L_0x0485:
            boolean r0 = r4.isVoice()
            if (r0 == 0) goto L_0x048e
        L_0x048b:
            r0 = r17
            goto L_0x04a3
        L_0x048e:
            boolean r0 = r4.isMusic()
            if (r0 == 0) goto L_0x0497
            java.lang.String r17 = "🎧 "
            goto L_0x048b
        L_0x0497:
            boolean r0 = r4.isPhoto()
            if (r0 == 0) goto L_0x04a0
            java.lang.String r17 = "🖼 "
            goto L_0x048b
        L_0x04a0:
            java.lang.String r17 = "📎 "
            goto L_0x048b
        L_0x04a3:
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r0)
            java.lang.CharSequence r0 = r4.caption
            r6.append(r0)
            java.lang.String r8 = r6.toString()
            goto L_0x052e
        L_0x04b6:
            boolean r6 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r6 == 0) goto L_0x04d3
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r0 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r0
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r8 = "📊 "
            r6.append(r8)
            org.telegram.tgnet.TLRPC$Poll r0 = r0.poll
            java.lang.String r0 = r0.question
            r6.append(r0)
            java.lang.String r0 = r6.toString()
        L_0x04d1:
            r8 = r0
            goto L_0x0515
        L_0x04d3:
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r0 == 0) goto L_0x04f1
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r6 = "🎮 "
            r0.append(r6)
            org.telegram.tgnet.TLRPC$Message r6 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r6 = r6.media
            org.telegram.tgnet.TLRPC$TL_game r6 = r6.game
            java.lang.String r6 = r6.title
            r0.append(r6)
            java.lang.String r0 = r0.toString()
            goto L_0x04d1
        L_0x04f1:
            int r0 = r4.type
            r6 = 14
            if (r0 != r6) goto L_0x050c
            java.lang.Object[] r0 = new java.lang.Object[r9]
            java.lang.String r6 = r4.getMusicAuthor()
            r8 = 0
            r0[r8] = r6
            java.lang.String r6 = r4.getMusicTitle()
            r8 = 1
            r0[r8] = r6
            java.lang.String r0 = java.lang.String.format(r15, r0)
            goto L_0x04d1
        L_0x050c:
            java.lang.CharSequence r0 = r4.messageText
            java.util.ArrayList<java.lang.String> r6 = r4.highlightedWords
            r8 = 0
            org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r0, (java.util.ArrayList<java.lang.String>) r6, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r8)
            goto L_0x04d1
        L_0x0515:
            org.telegram.tgnet.TLRPC$Message r0 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            if (r0 == 0) goto L_0x052e
            boolean r0 = r4.isMediaEmpty()
            if (r0 != 0) goto L_0x052e
            android.content.Context r0 = r1.mContext
            android.content.res.Resources r0 = r0.getResources()
            r6 = 2131034141(0x7var_d, float:1.7678791E38)
            int r14 = r0.getColor(r6)
        L_0x052e:
            org.telegram.tgnet.TLRPC$Message r0 = r4.messageOwner
            int r0 = r0.date
            long r11 = (long) r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.stringForMessageListDate(r11)
            r4 = 2131230915(0x7var_c3, float:1.8077896E38)
            r7.setTextViewText(r4, r0)
            java.lang.String r0 = r8.toString()
            r4 = 2131230913(0x7var_c1, float:1.8077892E38)
            r7.setTextViewText(r4, r0)
            r7.setTextColor(r4, r14)
            goto L_0x0566
        L_0x054b:
            r4 = 2131230915(0x7var_c3, float:1.8077896E38)
            if (r10 == 0) goto L_0x055d
            int r0 = r10.last_message_date
            if (r0 == 0) goto L_0x055d
            long r11 = (long) r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.stringForMessageListDate(r11)
            r7.setTextViewText(r4, r0)
            goto L_0x0560
        L_0x055d:
            r7.setTextViewText(r4, r8)
        L_0x0560:
            r4 = 2131230913(0x7var_c1, float:1.8077892E38)
            r7.setTextViewText(r4, r8)
        L_0x0566:
            r0 = 8
            r4 = 2131230911(0x7var_bf, float:1.8077888E38)
            if (r10 == 0) goto L_0x05ae
            int r6 = r10.unread_count
            if (r6 <= 0) goto L_0x05ae
            r8 = 1
            java.lang.Object[] r9 = new java.lang.Object[r8]
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            r8 = 0
            r9[r8] = r6
            java.lang.String r6 = "%d"
            java.lang.String r6 = java.lang.String.format(r6, r9)
            r7.setTextViewText(r4, r6)
            r7.setViewVisibility(r4, r8)
            org.telegram.messenger.AccountInstance r6 = r1.accountInstance
            org.telegram.messenger.MessagesController r6 = r6.getMessagesController()
            long r9 = r10.id
            boolean r6 = r6.isDialogMuted(r9)
            java.lang.String r9 = "setBackgroundResource"
            java.lang.String r10 = "setEnabled"
            if (r6 == 0) goto L_0x05a3
            r7.setBoolean(r4, r10, r8)
            r6 = 2131166262(0x7var_, float:1.7946764E38)
            r7.setInt(r4, r9, r6)
            goto L_0x05b2
        L_0x05a3:
            r6 = 1
            r7.setBoolean(r4, r10, r6)
            r6 = 2131166261(0x7var_, float:1.7946762E38)
            r7.setInt(r4, r9, r6)
            goto L_0x05b2
        L_0x05ae:
            r8 = 0
            r7.setViewVisibility(r4, r0)
        L_0x05b2:
            android.os.Bundle r4 = new android.os.Bundle
            r4.<init>()
            long r9 = r5.longValue()
            boolean r6 = org.telegram.messenger.DialogObject.isUserDialog(r9)
            if (r6 == 0) goto L_0x05cb
            long r5 = r5.longValue()
            java.lang.String r9 = "userId"
            r4.putLong(r9, r5)
            goto L_0x05d5
        L_0x05cb:
            long r5 = r5.longValue()
            long r5 = -r5
            java.lang.String r9 = "chatId"
            r4.putLong(r9, r5)
        L_0x05d5:
            org.telegram.messenger.AccountInstance r5 = r1.accountInstance
            int r5 = r5.getCurrentAccount()
            r4.putInt(r3, r5)
            android.content.Intent r3 = new android.content.Intent
            r3.<init>()
            r3.putExtras(r4)
            r4 = 2131230909(0x7var_bd, float:1.8077884E38)
            r7.setOnClickFillInIntent(r4, r3)
            r3 = 2131230912(0x7var_c0, float:1.807789E38)
            int r4 = r19.getCount()
            if (r2 != r4) goto L_0x05f8
            r4 = 8
            goto L_0x05f9
        L_0x05f8:
            r4 = 0
        L_0x05f9:
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
