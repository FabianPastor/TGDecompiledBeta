package org.telegram.messenger;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
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
        this.appWidgetId = intent.getIntExtra("appWidgetId", 0);
        SharedPreferences sharedPreferences = context.getSharedPreferences("shortcut_widget", 0);
        int i = sharedPreferences.getInt("account" + this.appWidgetId, -1);
        this.deleted = sharedPreferences.getBoolean("deleted" + this.appWidgetId, false);
        if (i >= 0) {
            this.accountInstance = AccountInstance.getInstance(i);
        }
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

    /* JADX WARNING: Code restructure failed: missing block: B:84:0x0257, code lost:
        if ((r0 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelMigrateFrom) == false) goto L_0x025a;
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
            r3 = 2131427352(0x7f0b0018, float:1.8476318E38)
            r0.<init>(r2, r3)
            r2 = 2131230935(0x7var_d7, float:1.8077937E38)
            r3 = 2131628053(0x7f0e1015, float:1.8883388E38)
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
            r5 = 2131427353(0x7f0b0019, float:1.847632E38)
            r0.<init>(r2, r5)
            r2 = 2131230937(0x7var_d9, float:1.807794E38)
            r5 = 2131627560(0x7f0e0e28, float:1.8882388E38)
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
            r2 = 2131230936(0x7var_d8, float:1.8077939E38)
            r0.setOnClickFillInIntent(r2, r3)
            return r0
        L_0x0077:
            java.util.ArrayList<java.lang.Long> r0 = r1.dids
            java.lang.Object r0 = r0.get(r2)
            r5 = r0
            java.lang.Long r5 = (java.lang.Long) r5
            long r6 = r5.longValue()
            r8 = 0
            java.lang.String r10 = ""
            r11 = 0
            int r0 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
            if (r0 <= 0) goto L_0x00f2
            org.telegram.messenger.AccountInstance r0 = r1.accountInstance
            org.telegram.messenger.MessagesController r0 = r0.getMessagesController()
            long r6 = r5.longValue()
            int r7 = (int) r6
            java.lang.Integer r6 = java.lang.Integer.valueOf(r7)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r6)
            if (r0 == 0) goto L_0x00ee
            boolean r6 = org.telegram.messenger.UserObject.isUserSelf(r0)
            if (r6 == 0) goto L_0x00b2
            r6 = 2131627199(0x7f0e0cbf, float:1.8881656E38)
            java.lang.String r7 = "SavedMessages"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            goto L_0x00ca
        L_0x00b2:
            boolean r6 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC$User) r0)
            if (r6 == 0) goto L_0x00c2
            r6 = 2131627089(0x7f0e0CLASSNAME, float:1.8881433E38)
            java.lang.String r7 = "RepliesTitle"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            goto L_0x00ca
        L_0x00c2:
            java.lang.String r6 = r0.first_name
            java.lang.String r7 = r0.last_name
            java.lang.String r6 = org.telegram.messenger.ContactsController.formatName(r6, r7)
        L_0x00ca:
            boolean r7 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC$User) r0)
            if (r7 != 0) goto L_0x00ec
            boolean r7 = org.telegram.messenger.UserObject.isUserSelf(r0)
            if (r7 != 0) goto L_0x00ec
            org.telegram.tgnet.TLRPC$UserProfilePhoto r7 = r0.photo
            if (r7 == 0) goto L_0x00ec
            org.telegram.tgnet.TLRPC$FileLocation r7 = r7.photo_small
            if (r7 == 0) goto L_0x00ec
            long r12 = r7.volume_id
            int r14 = (r12 > r8 ? 1 : (r12 == r8 ? 0 : -1))
            if (r14 == 0) goto L_0x00ec
            int r12 = r7.local_id
            if (r12 == 0) goto L_0x00ec
            r12 = r7
            r7 = r6
            r6 = r11
            goto L_0x012a
        L_0x00ec:
            r7 = r6
            goto L_0x00ef
        L_0x00ee:
            r7 = r10
        L_0x00ef:
            r6 = r11
            r12 = r6
            goto L_0x012a
        L_0x00f2:
            org.telegram.messenger.AccountInstance r0 = r1.accountInstance
            org.telegram.messenger.MessagesController r0 = r0.getMessagesController()
            long r6 = r5.longValue()
            int r7 = (int) r6
            int r6 = -r7
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r6)
            if (r0 == 0) goto L_0x0126
            java.lang.String r6 = r0.title
            org.telegram.tgnet.TLRPC$ChatPhoto r7 = r0.photo
            if (r7 == 0) goto L_0x0121
            org.telegram.tgnet.TLRPC$FileLocation r7 = r7.photo_small
            if (r7 == 0) goto L_0x0121
            long r12 = r7.volume_id
            int r14 = (r12 > r8 ? 1 : (r12 == r8 ? 0 : -1))
            if (r14 == 0) goto L_0x0121
            int r12 = r7.local_id
            if (r12 == 0) goto L_0x0121
            r12 = r7
            r7 = r6
            r6 = r0
            r0 = r11
            goto L_0x012a
        L_0x0121:
            r7 = r6
            r12 = r11
            r6 = r0
            r0 = r12
            goto L_0x012a
        L_0x0126:
            r6 = r0
            r7 = r10
            r0 = r11
            r12 = r0
        L_0x012a:
            android.widget.RemoteViews r13 = new android.widget.RemoteViews
            android.content.Context r14 = r1.mContext
            java.lang.String r14 = r14.getPackageName()
            r15 = 2131427346(0x7f0b0012, float:1.8476306E38)
            r13.<init>(r14, r15)
            r14 = 2131230907(0x7var_bb, float:1.807788E38)
            r13.setTextViewText(r14, r7)
            r7 = 1
            if (r12 == 0) goto L_0x014e
            java.io.File r12 = org.telegram.messenger.FileLoader.getPathToAttach(r12, r7)     // Catch:{ all -> 0x01e8 }
            java.lang.String r12 = r12.toString()     // Catch:{ all -> 0x01e8 }
            android.graphics.Bitmap r12 = android.graphics.BitmapFactory.decodeFile(r12)     // Catch:{ all -> 0x01e8 }
            goto L_0x014f
        L_0x014e:
            r12 = r11
        L_0x014f:
            r14 = 1111490560(0x42400000, float:48.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)     // Catch:{ all -> 0x01e8 }
            android.graphics.Bitmap$Config r15 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x01e8 }
            android.graphics.Bitmap r15 = android.graphics.Bitmap.createBitmap(r14, r14, r15)     // Catch:{ all -> 0x01e8 }
            r15.eraseColor(r4)     // Catch:{ all -> 0x01e8 }
            android.graphics.Canvas r8 = new android.graphics.Canvas     // Catch:{ all -> 0x01e8 }
            r8.<init>(r15)     // Catch:{ all -> 0x01e8 }
            if (r12 != 0) goto L_0x018e
            if (r0 == 0) goto L_0x0182
            org.telegram.ui.Components.AvatarDrawable r9 = new org.telegram.ui.Components.AvatarDrawable     // Catch:{ all -> 0x01e8 }
            r9.<init>((org.telegram.tgnet.TLRPC$User) r0)     // Catch:{ all -> 0x01e8 }
            boolean r12 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC$User) r0)     // Catch:{ all -> 0x01e8 }
            if (r12 == 0) goto L_0x0178
            r0 = 12
            r9.setAvatarType(r0)     // Catch:{ all -> 0x01e8 }
            goto L_0x0187
        L_0x0178:
            boolean r0 = org.telegram.messenger.UserObject.isUserSelf(r0)     // Catch:{ all -> 0x01e8 }
            if (r0 == 0) goto L_0x0187
            r9.setAvatarType(r7)     // Catch:{ all -> 0x01e8 }
            goto L_0x0187
        L_0x0182:
            org.telegram.ui.Components.AvatarDrawable r9 = new org.telegram.ui.Components.AvatarDrawable     // Catch:{ all -> 0x01e8 }
            r9.<init>((org.telegram.tgnet.TLRPC$Chat) r6)     // Catch:{ all -> 0x01e8 }
        L_0x0187:
            r9.setBounds(r4, r4, r14, r14)     // Catch:{ all -> 0x01e8 }
            r9.draw(r8)     // Catch:{ all -> 0x01e8 }
            goto L_0x01de
        L_0x018e:
            android.graphics.BitmapShader r0 = new android.graphics.BitmapShader     // Catch:{ all -> 0x01e8 }
            android.graphics.Shader$TileMode r9 = android.graphics.Shader.TileMode.CLAMP     // Catch:{ all -> 0x01e8 }
            r0.<init>(r12, r9, r9)     // Catch:{ all -> 0x01e8 }
            android.graphics.Paint r9 = r1.roundPaint     // Catch:{ all -> 0x01e8 }
            if (r9 != 0) goto L_0x01a7
            android.graphics.Paint r9 = new android.graphics.Paint     // Catch:{ all -> 0x01e8 }
            r9.<init>(r7)     // Catch:{ all -> 0x01e8 }
            r1.roundPaint = r9     // Catch:{ all -> 0x01e8 }
            android.graphics.RectF r9 = new android.graphics.RectF     // Catch:{ all -> 0x01e8 }
            r9.<init>()     // Catch:{ all -> 0x01e8 }
            r1.bitmapRect = r9     // Catch:{ all -> 0x01e8 }
        L_0x01a7:
            float r9 = (float) r14     // Catch:{ all -> 0x01e8 }
            int r14 = r12.getWidth()     // Catch:{ all -> 0x01e8 }
            float r14 = (float) r14     // Catch:{ all -> 0x01e8 }
            float r9 = r9 / r14
            r8.save()     // Catch:{ all -> 0x01e8 }
            r8.scale(r9, r9)     // Catch:{ all -> 0x01e8 }
            android.graphics.Paint r9 = r1.roundPaint     // Catch:{ all -> 0x01e8 }
            r9.setShader(r0)     // Catch:{ all -> 0x01e8 }
            android.graphics.RectF r0 = r1.bitmapRect     // Catch:{ all -> 0x01e8 }
            r9 = 0
            r14 = 0
            int r7 = r12.getWidth()     // Catch:{ all -> 0x01e8 }
            float r7 = (float) r7     // Catch:{ all -> 0x01e8 }
            int r4 = r12.getHeight()     // Catch:{ all -> 0x01e8 }
            float r4 = (float) r4     // Catch:{ all -> 0x01e8 }
            r0.set(r9, r14, r7, r4)     // Catch:{ all -> 0x01e8 }
            android.graphics.RectF r0 = r1.bitmapRect     // Catch:{ all -> 0x01e8 }
            int r4 = r12.getWidth()     // Catch:{ all -> 0x01e8 }
            float r4 = (float) r4     // Catch:{ all -> 0x01e8 }
            int r7 = r12.getHeight()     // Catch:{ all -> 0x01e8 }
            float r7 = (float) r7     // Catch:{ all -> 0x01e8 }
            android.graphics.Paint r9 = r1.roundPaint     // Catch:{ all -> 0x01e8 }
            r8.drawRoundRect(r0, r4, r7, r9)     // Catch:{ all -> 0x01e8 }
            r8.restore()     // Catch:{ all -> 0x01e8 }
        L_0x01de:
            r8.setBitmap(r11)     // Catch:{ all -> 0x01e8 }
            r0 = 2131230903(0x7var_b7, float:1.8077872E38)
            r13.setImageViewBitmap(r0, r15)     // Catch:{ all -> 0x01e8 }
            goto L_0x01ec
        L_0x01e8:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x01ec:
            android.util.LongSparseArray<org.telegram.messenger.MessageObject> r0 = r1.messageObjects
            long r7 = r5.longValue()
            java.lang.Object r0 = r0.get(r7)
            r4 = r0
            org.telegram.messenger.MessageObject r4 = (org.telegram.messenger.MessageObject) r4
            android.util.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r0 = r1.dialogs
            long r7 = r5.longValue()
            java.lang.Object r0 = r0.get(r7)
            r7 = r0
            org.telegram.tgnet.TLRPC$Dialog r7 = (org.telegram.tgnet.TLRPC$Dialog) r7
            if (r4 == 0) goto L_0x0560
            int r0 = r4.getFromChatId()
            if (r0 <= 0) goto L_0x021d
            org.telegram.messenger.AccountInstance r9 = r1.accountInstance
            org.telegram.messenger.MessagesController r9 = r9.getMessagesController()
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$User r0 = r9.getUser(r0)
            goto L_0x0231
        L_0x021d:
            org.telegram.messenger.AccountInstance r9 = r1.accountInstance
            org.telegram.messenger.MessagesController r9 = r9.getMessagesController()
            int r0 = -r0
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$Chat r0 = r9.getChat(r0)
            r18 = r11
            r11 = r0
            r0 = r18
        L_0x0231:
            android.content.Context r9 = r1.mContext
            android.content.res.Resources r9 = r9.getResources()
            r12 = 2131034146(0x7var_, float:1.7678801E38)
            int r9 = r9.getColor(r12)
            org.telegram.tgnet.TLRPC$Message r12 = r4.messageOwner
            boolean r12 = r12 instanceof org.telegram.tgnet.TLRPC$TL_messageService
            r14 = 2131034141(0x7var_d, float:1.7678791E38)
            if (r12 == 0) goto L_0x0268
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r6)
            if (r0 == 0) goto L_0x025a
            org.telegram.tgnet.TLRPC$Message r0 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r0 = r0.action
            boolean r6 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageActionHistoryClear
            if (r6 != 0) goto L_0x025c
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelMigrateFrom
            if (r0 == 0) goto L_0x025a
            goto L_0x025c
        L_0x025a:
            java.lang.CharSequence r10 = r4.messageText
        L_0x025c:
            android.content.Context r0 = r1.mContext
            android.content.res.Resources r0 = r0.getResources()
            int r9 = r0.getColor(r14)
            goto L_0x0543
        L_0x0268:
            java.lang.String r12 = "📎 "
            java.lang.String r15 = "🎧 "
            java.lang.String r8 = "🎧 %s - %s"
            java.lang.String r16 = "🎤 "
            java.lang.String r17 = "📹 "
            if (r6 == 0) goto L_0x0459
            int r14 = r6.id
            if (r14 <= 0) goto L_0x0459
            if (r11 != 0) goto L_0x0459
            boolean r11 = org.telegram.messenger.ChatObject.isChannel(r6)
            if (r11 == 0) goto L_0x028b
            boolean r6 = org.telegram.messenger.ChatObject.isMegagroup(r6)
            if (r6 == 0) goto L_0x0459
        L_0x028b:
            boolean r6 = r4.isOutOwner()
            if (r6 == 0) goto L_0x029c
            r0 = 2131625635(0x7f0e06a3, float:1.8878484E38)
            java.lang.String r6 = "FromYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r6, r0)
        L_0x029a:
            r6 = r0
            goto L_0x02ac
        L_0x029c:
            if (r0 == 0) goto L_0x02a9
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            java.lang.String r6 = "\n"
            java.lang.String r0 = r0.replace(r6, r10)
            goto L_0x029a
        L_0x02a9:
            java.lang.String r0 = "DELETED"
            goto L_0x029a
        L_0x02ac:
            java.lang.String r0 = "%2$s: ⁨%1$s⁩"
            java.lang.CharSequence r11 = r4.caption
            r14 = 150(0x96, float:2.1E-43)
            if (r11 == 0) goto L_0x0311
            java.lang.String r8 = r11.toString()
            int r10 = r8.length()
            if (r10 <= r14) goto L_0x02c3
            r10 = 0
            java.lang.String r8 = r8.substring(r10, r14)
        L_0x02c3:
            boolean r10 = r4.isVideo()
            if (r10 == 0) goto L_0x02cd
            r12 = r17
        L_0x02cb:
            r10 = 2
            goto L_0x02e8
        L_0x02cd:
            boolean r10 = r4.isVoice()
            if (r10 == 0) goto L_0x02d6
            r12 = r16
            goto L_0x02cb
        L_0x02d6:
            boolean r10 = r4.isMusic()
            if (r10 == 0) goto L_0x02de
            r12 = r15
            goto L_0x02cb
        L_0x02de:
            boolean r10 = r4.isPhoto()
            if (r10 == 0) goto L_0x02cb
            java.lang.String r12 = "🖼 "
            goto L_0x02cb
        L_0x02e8:
            java.lang.Object[] r10 = new java.lang.Object[r10]
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            r11.append(r12)
            r12 = 32
            r14 = 10
            java.lang.String r8 = r8.replace(r14, r12)
            r11.append(r8)
            java.lang.String r8 = r11.toString()
            r11 = 0
            r10[r11] = r8
            r8 = 1
            r10[r8] = r6
            java.lang.String r0 = java.lang.String.format(r0, r10)
            android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r0)
            goto L_0x043d
        L_0x0311:
            org.telegram.tgnet.TLRPC$Message r11 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r11 = r11.media
            if (r11 == 0) goto L_0x0409
            boolean r11 = r4.isMediaEmpty()
            if (r11 != 0) goto L_0x0409
            android.content.Context r9 = r1.mContext
            android.content.res.Resources r9 = r9.getResources()
            r10 = 2131034141(0x7var_d, float:1.7678791E38)
            int r9 = r9.getColor(r10)
            org.telegram.tgnet.TLRPC$Message r10 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r10 = r10.media
            boolean r11 = r10 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            r12 = 18
            if (r11 == 0) goto L_0x0365
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r10 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r10
            int r8 = android.os.Build.VERSION.SDK_INT
            if (r8 < r12) goto L_0x034c
            r8 = 1
            java.lang.Object[] r11 = new java.lang.Object[r8]
            org.telegram.tgnet.TLRPC$Poll r10 = r10.poll
            java.lang.String r10 = r10.question
            r12 = 0
            r11[r12] = r10
            java.lang.String r10 = "📊 ⁨%s⁩"
            java.lang.String r10 = java.lang.String.format(r10, r11)
            goto L_0x035d
        L_0x034c:
            r8 = 1
            r12 = 0
            java.lang.Object[] r11 = new java.lang.Object[r8]
            org.telegram.tgnet.TLRPC$Poll r8 = r10.poll
            java.lang.String r8 = r8.question
            r11[r12] = r8
            java.lang.String r8 = "📊 %s"
            java.lang.String r10 = java.lang.String.format(r8, r11)
        L_0x035d:
            r8 = r10
            r10 = 2
            r11 = 32
            r12 = 1
            r14 = 0
            goto L_0x03d8
        L_0x0365:
            boolean r11 = r10 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r11 == 0) goto L_0x0396
            int r8 = android.os.Build.VERSION.SDK_INT
            if (r8 < r12) goto L_0x037f
            r8 = 1
            java.lang.Object[] r11 = new java.lang.Object[r8]
            org.telegram.tgnet.TLRPC$TL_game r10 = r10.game
            java.lang.String r10 = r10.title
            r14 = 0
            r11[r14] = r10
            java.lang.String r10 = "🎮 ⁨%s⁩"
            java.lang.String r10 = java.lang.String.format(r10, r11)
            goto L_0x0390
        L_0x037f:
            r8 = 1
            r14 = 0
            java.lang.Object[] r11 = new java.lang.Object[r8]
            org.telegram.tgnet.TLRPC$TL_game r8 = r10.game
            java.lang.String r8 = r8.title
            r11[r14] = r8
            java.lang.String r8 = "🎮 %s"
            java.lang.String r10 = java.lang.String.format(r8, r11)
        L_0x0390:
            r8 = r10
            r10 = 2
            r11 = 32
            r12 = 1
            goto L_0x03d8
        L_0x0396:
            r14 = 0
            int r10 = r4.type
            r11 = 14
            if (r10 != r11) goto L_0x03ce
            int r10 = android.os.Build.VERSION.SDK_INT
            if (r10 < r12) goto L_0x03b9
            r10 = 2
            java.lang.Object[] r8 = new java.lang.Object[r10]
            java.lang.String r11 = r4.getMusicAuthor()
            r8[r14] = r11
            java.lang.String r11 = r4.getMusicTitle()
            r12 = 1
            r8[r12] = r11
            java.lang.String r11 = "🎧 ⁨%s - %s⁩"
            java.lang.String r8 = java.lang.String.format(r11, r8)
            goto L_0x03d6
        L_0x03b9:
            r10 = 2
            r12 = 1
            java.lang.Object[] r11 = new java.lang.Object[r10]
            java.lang.String r15 = r4.getMusicAuthor()
            r11[r14] = r15
            java.lang.String r15 = r4.getMusicTitle()
            r11[r12] = r15
            java.lang.String r8 = java.lang.String.format(r8, r11)
            goto L_0x03d6
        L_0x03ce:
            r10 = 2
            r12 = 1
            java.lang.CharSequence r8 = r4.messageText
            java.lang.String r8 = r8.toString()
        L_0x03d6:
            r11 = 32
        L_0x03d8:
            r15 = 10
            java.lang.String r8 = r8.replace(r15, r11)
            java.lang.Object[] r11 = new java.lang.Object[r10]
            r11[r14] = r8
            r11[r12] = r6
            java.lang.String r0 = java.lang.String.format(r0, r11)
            android.text.SpannableStringBuilder r8 = android.text.SpannableStringBuilder.valueOf(r0)
            org.telegram.ui.Components.ForegroundColorSpanThemable r0 = new org.telegram.ui.Components.ForegroundColorSpanThemable     // Catch:{ Exception -> 0x0403 }
            java.lang.String r10 = "chats_attachMessage"
            r0.<init>(r10)     // Catch:{ Exception -> 0x0403 }
            int r10 = r6.length()     // Catch:{ Exception -> 0x0403 }
            r11 = 2
            int r10 = r10 + r11
            int r11 = r8.length()     // Catch:{ Exception -> 0x0403 }
            r12 = 33
            r8.setSpan(r0, r10, r11, r12)     // Catch:{ Exception -> 0x0403 }
            goto L_0x0407
        L_0x0403:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0407:
            r10 = r8
            goto L_0x043e
        L_0x0409:
            org.telegram.tgnet.TLRPC$Message r8 = r4.messageOwner
            java.lang.String r8 = r8.message
            if (r8 == 0) goto L_0x0439
            int r10 = r8.length()
            if (r10 <= r14) goto L_0x041b
            r10 = 0
            java.lang.String r8 = r8.substring(r10, r14)
            goto L_0x041c
        L_0x041b:
            r10 = 0
        L_0x041c:
            r11 = 32
            r12 = 10
            java.lang.String r8 = r8.replace(r12, r11)
            java.lang.String r8 = r8.trim()
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]
            r11[r10] = r8
            r8 = 1
            r11[r8] = r6
            java.lang.String r0 = java.lang.String.format(r0, r11)
            android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r0)
            goto L_0x043d
        L_0x0439:
            android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r10)
        L_0x043d:
            r10 = r0
        L_0x043e:
            org.telegram.ui.Components.ForegroundColorSpanThemable r0 = new org.telegram.ui.Components.ForegroundColorSpanThemable     // Catch:{ Exception -> 0x0453 }
            java.lang.String r8 = "chats_nameMessage"
            r0.<init>(r8)     // Catch:{ Exception -> 0x0453 }
            int r6 = r6.length()     // Catch:{ Exception -> 0x0453 }
            r8 = 1
            int r6 = r6 + r8
            r8 = 33
            r11 = 0
            r10.setSpan(r0, r11, r6, r8)     // Catch:{ Exception -> 0x0453 }
            goto L_0x0543
        L_0x0453:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0543
        L_0x0459:
            org.telegram.tgnet.TLRPC$Message r0 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r6 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r6 == 0) goto L_0x0476
            org.telegram.tgnet.TLRPC$Photo r6 = r0.photo
            boolean r6 = r6 instanceof org.telegram.tgnet.TLRPC$TL_photoEmpty
            if (r6 == 0) goto L_0x0476
            int r6 = r0.ttl_seconds
            if (r6 == 0) goto L_0x0476
            r0 = 2131624389(0x7f0e01c5, float:1.8875956E38)
            java.lang.String r6 = "AttachPhotoExpired"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r6, r0)
            goto L_0x0543
        L_0x0476:
            boolean r6 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r6 == 0) goto L_0x048f
            org.telegram.tgnet.TLRPC$Document r6 = r0.document
            boolean r6 = r6 instanceof org.telegram.tgnet.TLRPC$TL_documentEmpty
            if (r6 == 0) goto L_0x048f
            int r6 = r0.ttl_seconds
            if (r6 == 0) goto L_0x048f
            r0 = 2131624395(0x7f0e01cb, float:1.8875969E38)
            java.lang.String r6 = "AttachVideoExpired"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r6, r0)
            goto L_0x0543
        L_0x048f:
            java.lang.CharSequence r6 = r4.caption
            if (r6 == 0) goto L_0x04c9
            boolean r0 = r4.isVideo()
            if (r0 == 0) goto L_0x049c
            r12 = r17
            goto L_0x04b6
        L_0x049c:
            boolean r0 = r4.isVoice()
            if (r0 == 0) goto L_0x04a5
            r12 = r16
            goto L_0x04b6
        L_0x04a5:
            boolean r0 = r4.isMusic()
            if (r0 == 0) goto L_0x04ad
            r12 = r15
            goto L_0x04b6
        L_0x04ad:
            boolean r0 = r4.isPhoto()
            if (r0 == 0) goto L_0x04b6
            java.lang.String r12 = "🖼 "
        L_0x04b6:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r12)
            java.lang.CharSequence r6 = r4.caption
            r0.append(r6)
            java.lang.String r10 = r0.toString()
            goto L_0x0543
        L_0x04c9:
            boolean r6 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r6 == 0) goto L_0x04e7
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r0 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r0
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r8 = "📊 "
            r6.append(r8)
            org.telegram.tgnet.TLRPC$Poll r0 = r0.poll
            java.lang.String r0 = r0.question
            r6.append(r0)
            java.lang.String r0 = r6.toString()
        L_0x04e5:
            r10 = r0
            goto L_0x052a
        L_0x04e7:
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r0 == 0) goto L_0x0506
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
            goto L_0x04e5
        L_0x0506:
            int r0 = r4.type
            r6 = 14
            if (r0 != r6) goto L_0x0522
            r0 = 2
            java.lang.Object[] r0 = new java.lang.Object[r0]
            java.lang.String r6 = r4.getMusicAuthor()
            r10 = 0
            r0[r10] = r6
            java.lang.String r6 = r4.getMusicTitle()
            r10 = 1
            r0[r10] = r6
            java.lang.String r0 = java.lang.String.format(r8, r0)
            goto L_0x04e5
        L_0x0522:
            java.lang.CharSequence r0 = r4.messageText
            java.util.ArrayList<java.lang.String> r6 = r4.highlightedWords
            org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r0, (java.util.ArrayList<java.lang.String>) r6)
            goto L_0x04e5
        L_0x052a:
            org.telegram.tgnet.TLRPC$Message r0 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            if (r0 == 0) goto L_0x0543
            boolean r0 = r4.isMediaEmpty()
            if (r0 != 0) goto L_0x0543
            android.content.Context r0 = r1.mContext
            android.content.res.Resources r0 = r0.getResources()
            r6 = 2131034141(0x7var_d, float:1.7678791E38)
            int r9 = r0.getColor(r6)
        L_0x0543:
            org.telegram.tgnet.TLRPC$Message r0 = r4.messageOwner
            int r0 = r0.date
            long r11 = (long) r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.stringForMessageListDate(r11)
            r4 = 2131230908(0x7var_bc, float:1.8077882E38)
            r13.setTextViewText(r4, r0)
            java.lang.String r0 = r10.toString()
            r4 = 2131230906(0x7var_ba, float:1.8077878E38)
            r13.setTextViewText(r4, r0)
            r13.setTextColor(r4, r9)
            goto L_0x056f
        L_0x0560:
            r4 = 2131230908(0x7var_bc, float:1.8077882E38)
            if (r7 == 0) goto L_0x056f
            int r0 = r7.last_message_date
            long r8 = (long) r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.stringForMessageListDate(r8)
            r13.setTextViewText(r4, r0)
        L_0x056f:
            r0 = 8
            r4 = 2131230904(0x7var_b8, float:1.8077874E38)
            if (r7 == 0) goto L_0x0591
            int r6 = r7.unread_count
            if (r6 <= 0) goto L_0x0591
            r7 = 1
            java.lang.Object[] r7 = new java.lang.Object[r7]
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            r8 = 0
            r7[r8] = r6
            java.lang.String r6 = "%d"
            java.lang.String r6 = java.lang.String.format(r6, r7)
            r13.setTextViewText(r4, r6)
            r13.setViewVisibility(r4, r8)
            goto L_0x0595
        L_0x0591:
            r8 = 0
            r13.setViewVisibility(r4, r0)
        L_0x0595:
            android.os.Bundle r4 = new android.os.Bundle
            r4.<init>()
            long r6 = r5.longValue()
            r9 = 0
            int r11 = (r6 > r9 ? 1 : (r6 == r9 ? 0 : -1))
            if (r11 <= 0) goto L_0x05b0
            long r5 = r5.longValue()
            int r6 = (int) r5
            java.lang.String r5 = "userId"
            r4.putInt(r5, r6)
            goto L_0x05bb
        L_0x05b0:
            long r5 = r5.longValue()
            int r6 = (int) r5
            int r5 = -r6
            java.lang.String r6 = "chatId"
            r4.putInt(r6, r5)
        L_0x05bb:
            org.telegram.messenger.AccountInstance r5 = r1.accountInstance
            int r5 = r5.getCurrentAccount()
            r4.putInt(r3, r5)
            android.content.Intent r3 = new android.content.Intent
            r3.<init>()
            r3.putExtras(r4)
            r4 = 2131230902(0x7var_b6, float:1.807787E38)
            r13.setOnClickFillInIntent(r4, r3)
            r3 = 2131230905(0x7var_b9, float:1.8077876E38)
            int r4 = r19.getCount()
            if (r2 != r4) goto L_0x05de
            r4 = 8
            goto L_0x05df
        L_0x05de:
            r4 = 0
        L_0x05df:
            r13.setViewVisibility(r3, r4)
            return r13
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
                this.messageObjects.put(longSparseArray.keyAt(i), new MessageObject(this.accountInstance.getCurrentAccount(), (TLRPC$Message) longSparseArray.valueAt(i), (SparseArray<TLRPC$User>) null, (SparseArray<TLRPC$Chat>) null, false, true));
            }
        }
    }
}
