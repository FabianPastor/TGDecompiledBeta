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

    /* JADX WARNING: Code restructure failed: missing block: B:88:0x0269, code lost:
        if ((r0 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelMigrateFrom) == false) goto L_0x026c;
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
            r3 = 2131628096(0x7f0e1040, float:1.8883475E38)
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
            r5 = 2131627571(0x7f0e0e33, float:1.888241E38)
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
            if (r0 <= 0) goto L_0x0104
            org.telegram.messenger.AccountInstance r0 = r1.accountInstance
            org.telegram.messenger.MessagesController r0 = r0.getMessagesController()
            long r6 = r5.longValue()
            int r7 = (int) r6
            java.lang.Integer r6 = java.lang.Integer.valueOf(r7)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r6)
            if (r0 == 0) goto L_0x0100
            boolean r6 = org.telegram.messenger.UserObject.isUserSelf(r0)
            if (r6 == 0) goto L_0x00b2
            r6 = 2131627209(0x7f0e0cc9, float:1.8881676E38)
            java.lang.String r7 = "SavedMessages"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            goto L_0x00da
        L_0x00b2:
            boolean r6 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC$User) r0)
            if (r6 == 0) goto L_0x00c2
            r6 = 2131627098(0x7f0e0c5a, float:1.888145E38)
            java.lang.String r7 = "RepliesTitle"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            goto L_0x00da
        L_0x00c2:
            boolean r6 = org.telegram.messenger.UserObject.isDeleted(r0)
            if (r6 == 0) goto L_0x00d2
            r6 = 2131625727(0x7f0e06ff, float:1.887867E38)
            java.lang.String r7 = "HiddenName"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            goto L_0x00da
        L_0x00d2:
            java.lang.String r6 = r0.first_name
            java.lang.String r7 = r0.last_name
            java.lang.String r6 = org.telegram.messenger.ContactsController.formatName(r6, r7)
        L_0x00da:
            boolean r7 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC$User) r0)
            if (r7 != 0) goto L_0x00fe
            boolean r7 = org.telegram.messenger.UserObject.isUserSelf(r0)
            if (r7 != 0) goto L_0x00fe
            if (r0 == 0) goto L_0x00fe
            org.telegram.tgnet.TLRPC$UserProfilePhoto r7 = r0.photo
            if (r7 == 0) goto L_0x00fe
            org.telegram.tgnet.TLRPC$FileLocation r7 = r7.photo_small
            if (r7 == 0) goto L_0x00fe
            long r12 = r7.volume_id
            int r14 = (r12 > r8 ? 1 : (r12 == r8 ? 0 : -1))
            if (r14 == 0) goto L_0x00fe
            int r12 = r7.local_id
            if (r12 == 0) goto L_0x00fe
            r12 = r7
            r7 = r6
            r6 = r11
            goto L_0x013c
        L_0x00fe:
            r7 = r6
            goto L_0x0101
        L_0x0100:
            r7 = r10
        L_0x0101:
            r6 = r11
            r12 = r6
            goto L_0x013c
        L_0x0104:
            org.telegram.messenger.AccountInstance r0 = r1.accountInstance
            org.telegram.messenger.MessagesController r0 = r0.getMessagesController()
            long r6 = r5.longValue()
            int r7 = (int) r6
            int r6 = -r7
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r6)
            if (r0 == 0) goto L_0x0138
            java.lang.String r6 = r0.title
            org.telegram.tgnet.TLRPC$ChatPhoto r7 = r0.photo
            if (r7 == 0) goto L_0x0133
            org.telegram.tgnet.TLRPC$FileLocation r7 = r7.photo_small
            if (r7 == 0) goto L_0x0133
            long r12 = r7.volume_id
            int r14 = (r12 > r8 ? 1 : (r12 == r8 ? 0 : -1))
            if (r14 == 0) goto L_0x0133
            int r12 = r7.local_id
            if (r12 == 0) goto L_0x0133
            r12 = r7
            r7 = r6
            r6 = r0
            r0 = r11
            goto L_0x013c
        L_0x0133:
            r7 = r6
            r12 = r11
            r6 = r0
            r0 = r12
            goto L_0x013c
        L_0x0138:
            r6 = r0
            r7 = r10
            r0 = r11
            r12 = r0
        L_0x013c:
            android.widget.RemoteViews r13 = new android.widget.RemoteViews
            android.content.Context r14 = r1.mContext
            java.lang.String r14 = r14.getPackageName()
            r15 = 2131427346(0x7f0b0012, float:1.8476306E38)
            r13.<init>(r14, r15)
            r14 = 2131230907(0x7var_bb, float:1.807788E38)
            r13.setTextViewText(r14, r7)
            r7 = 1
            if (r12 == 0) goto L_0x0160
            java.io.File r12 = org.telegram.messenger.FileLoader.getPathToAttach(r12, r7)     // Catch:{ all -> 0x01fa }
            java.lang.String r12 = r12.toString()     // Catch:{ all -> 0x01fa }
            android.graphics.Bitmap r12 = android.graphics.BitmapFactory.decodeFile(r12)     // Catch:{ all -> 0x01fa }
            goto L_0x0161
        L_0x0160:
            r12 = r11
        L_0x0161:
            r14 = 1111490560(0x42400000, float:48.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)     // Catch:{ all -> 0x01fa }
            android.graphics.Bitmap$Config r15 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x01fa }
            android.graphics.Bitmap r15 = android.graphics.Bitmap.createBitmap(r14, r14, r15)     // Catch:{ all -> 0x01fa }
            r15.eraseColor(r4)     // Catch:{ all -> 0x01fa }
            android.graphics.Canvas r8 = new android.graphics.Canvas     // Catch:{ all -> 0x01fa }
            r8.<init>(r15)     // Catch:{ all -> 0x01fa }
            if (r12 != 0) goto L_0x01a0
            if (r0 == 0) goto L_0x0194
            org.telegram.ui.Components.AvatarDrawable r9 = new org.telegram.ui.Components.AvatarDrawable     // Catch:{ all -> 0x01fa }
            r9.<init>((org.telegram.tgnet.TLRPC$User) r0)     // Catch:{ all -> 0x01fa }
            boolean r12 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC$User) r0)     // Catch:{ all -> 0x01fa }
            if (r12 == 0) goto L_0x018a
            r0 = 12
            r9.setAvatarType(r0)     // Catch:{ all -> 0x01fa }
            goto L_0x0199
        L_0x018a:
            boolean r0 = org.telegram.messenger.UserObject.isUserSelf(r0)     // Catch:{ all -> 0x01fa }
            if (r0 == 0) goto L_0x0199
            r9.setAvatarType(r7)     // Catch:{ all -> 0x01fa }
            goto L_0x0199
        L_0x0194:
            org.telegram.ui.Components.AvatarDrawable r9 = new org.telegram.ui.Components.AvatarDrawable     // Catch:{ all -> 0x01fa }
            r9.<init>((org.telegram.tgnet.TLRPC$Chat) r6)     // Catch:{ all -> 0x01fa }
        L_0x0199:
            r9.setBounds(r4, r4, r14, r14)     // Catch:{ all -> 0x01fa }
            r9.draw(r8)     // Catch:{ all -> 0x01fa }
            goto L_0x01f0
        L_0x01a0:
            android.graphics.BitmapShader r0 = new android.graphics.BitmapShader     // Catch:{ all -> 0x01fa }
            android.graphics.Shader$TileMode r9 = android.graphics.Shader.TileMode.CLAMP     // Catch:{ all -> 0x01fa }
            r0.<init>(r12, r9, r9)     // Catch:{ all -> 0x01fa }
            android.graphics.Paint r9 = r1.roundPaint     // Catch:{ all -> 0x01fa }
            if (r9 != 0) goto L_0x01b9
            android.graphics.Paint r9 = new android.graphics.Paint     // Catch:{ all -> 0x01fa }
            r9.<init>(r7)     // Catch:{ all -> 0x01fa }
            r1.roundPaint = r9     // Catch:{ all -> 0x01fa }
            android.graphics.RectF r9 = new android.graphics.RectF     // Catch:{ all -> 0x01fa }
            r9.<init>()     // Catch:{ all -> 0x01fa }
            r1.bitmapRect = r9     // Catch:{ all -> 0x01fa }
        L_0x01b9:
            float r9 = (float) r14     // Catch:{ all -> 0x01fa }
            int r14 = r12.getWidth()     // Catch:{ all -> 0x01fa }
            float r14 = (float) r14     // Catch:{ all -> 0x01fa }
            float r9 = r9 / r14
            r8.save()     // Catch:{ all -> 0x01fa }
            r8.scale(r9, r9)     // Catch:{ all -> 0x01fa }
            android.graphics.Paint r9 = r1.roundPaint     // Catch:{ all -> 0x01fa }
            r9.setShader(r0)     // Catch:{ all -> 0x01fa }
            android.graphics.RectF r0 = r1.bitmapRect     // Catch:{ all -> 0x01fa }
            r9 = 0
            r14 = 0
            int r7 = r12.getWidth()     // Catch:{ all -> 0x01fa }
            float r7 = (float) r7     // Catch:{ all -> 0x01fa }
            int r4 = r12.getHeight()     // Catch:{ all -> 0x01fa }
            float r4 = (float) r4     // Catch:{ all -> 0x01fa }
            r0.set(r9, r14, r7, r4)     // Catch:{ all -> 0x01fa }
            android.graphics.RectF r0 = r1.bitmapRect     // Catch:{ all -> 0x01fa }
            int r4 = r12.getWidth()     // Catch:{ all -> 0x01fa }
            float r4 = (float) r4     // Catch:{ all -> 0x01fa }
            int r7 = r12.getHeight()     // Catch:{ all -> 0x01fa }
            float r7 = (float) r7     // Catch:{ all -> 0x01fa }
            android.graphics.Paint r9 = r1.roundPaint     // Catch:{ all -> 0x01fa }
            r8.drawRoundRect(r0, r4, r7, r9)     // Catch:{ all -> 0x01fa }
            r8.restore()     // Catch:{ all -> 0x01fa }
        L_0x01f0:
            r8.setBitmap(r11)     // Catch:{ all -> 0x01fa }
            r0 = 2131230903(0x7var_b7, float:1.8077872E38)
            r13.setImageViewBitmap(r0, r15)     // Catch:{ all -> 0x01fa }
            goto L_0x01fe
        L_0x01fa:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x01fe:
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
            if (r4 == 0) goto L_0x056c
            int r0 = r4.getFromChatId()
            if (r0 <= 0) goto L_0x022f
            org.telegram.messenger.AccountInstance r12 = r1.accountInstance
            org.telegram.messenger.MessagesController r12 = r12.getMessagesController()
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$User r0 = r12.getUser(r0)
            goto L_0x0243
        L_0x022f:
            org.telegram.messenger.AccountInstance r12 = r1.accountInstance
            org.telegram.messenger.MessagesController r12 = r12.getMessagesController()
            int r0 = -r0
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$Chat r0 = r12.getChat(r0)
            r18 = r11
            r11 = r0
            r0 = r18
        L_0x0243:
            android.content.Context r12 = r1.mContext
            android.content.res.Resources r12 = r12.getResources()
            r14 = 2131034146(0x7var_, float:1.7678801E38)
            int r12 = r12.getColor(r14)
            org.telegram.tgnet.TLRPC$Message r14 = r4.messageOwner
            boolean r14 = r14 instanceof org.telegram.tgnet.TLRPC$TL_messageService
            r15 = 2131034141(0x7var_d, float:1.7678791E38)
            if (r14 == 0) goto L_0x027a
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r6)
            if (r0 == 0) goto L_0x026c
            org.telegram.tgnet.TLRPC$Message r0 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r0 = r0.action
            boolean r6 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageActionHistoryClear
            if (r6 != 0) goto L_0x026e
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelMigrateFrom
            if (r0 == 0) goto L_0x026c
            goto L_0x026e
        L_0x026c:
            java.lang.CharSequence r10 = r4.messageText
        L_0x026e:
            android.content.Context r0 = r1.mContext
            android.content.res.Resources r0 = r0.getResources()
            int r12 = r0.getColor(r15)
            goto L_0x054f
        L_0x027a:
            java.lang.String r14 = "🎧 "
            java.lang.String r8 = "🎧 %s - %s"
            java.lang.String r16 = "🎤 "
            java.lang.String r17 = "📹 "
            r9 = 2
            if (r6 == 0) goto L_0x0463
            int r15 = r6.id
            if (r15 <= 0) goto L_0x0463
            if (r11 != 0) goto L_0x0463
            boolean r11 = org.telegram.messenger.ChatObject.isChannel(r6)
            if (r11 == 0) goto L_0x029b
            boolean r6 = org.telegram.messenger.ChatObject.isMegagroup(r6)
            if (r6 == 0) goto L_0x0463
        L_0x029b:
            boolean r6 = r4.isOutOwner()
            if (r6 == 0) goto L_0x02ac
            r0 = 2131625638(0x7f0e06a6, float:1.887849E38)
            java.lang.String r6 = "FromYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r6, r0)
        L_0x02aa:
            r6 = r0
            goto L_0x02bc
        L_0x02ac:
            if (r0 == 0) goto L_0x02b9
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            java.lang.String r6 = "\n"
            java.lang.String r0 = r0.replace(r6, r10)
            goto L_0x02aa
        L_0x02b9:
            java.lang.String r0 = "DELETED"
            goto L_0x02aa
        L_0x02bc:
            java.lang.String r0 = "%2$s: ⁨%1$s⁩"
            java.lang.CharSequence r11 = r4.caption
            r15 = 150(0x96, float:2.1E-43)
            if (r11 == 0) goto L_0x0322
            java.lang.String r8 = r11.toString()
            int r10 = r8.length()
            if (r10 <= r15) goto L_0x02d3
            r10 = 0
            java.lang.String r8 = r8.substring(r10, r15)
        L_0x02d3:
            boolean r10 = r4.isVideo()
            if (r10 == 0) goto L_0x02dc
            r14 = r17
            goto L_0x02f9
        L_0x02dc:
            boolean r10 = r4.isVoice()
            if (r10 == 0) goto L_0x02e5
            r14 = r16
            goto L_0x02f9
        L_0x02e5:
            boolean r10 = r4.isMusic()
            if (r10 == 0) goto L_0x02ec
            goto L_0x02f9
        L_0x02ec:
            boolean r10 = r4.isPhoto()
            if (r10 == 0) goto L_0x02f6
            java.lang.String r14 = "🖼 "
            goto L_0x02f9
        L_0x02f6:
            java.lang.String r14 = "📎 "
        L_0x02f9:
            java.lang.Object[] r9 = new java.lang.Object[r9]
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            r10.append(r14)
            r11 = 32
            r14 = 10
            java.lang.String r8 = r8.replace(r14, r11)
            r10.append(r8)
            java.lang.String r8 = r10.toString()
            r10 = 0
            r9[r10] = r8
            r8 = 1
            r9[r8] = r6
            java.lang.String r0 = java.lang.String.format(r0, r9)
            android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r0)
            goto L_0x0447
        L_0x0322:
            org.telegram.tgnet.TLRPC$Message r11 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r11 = r11.media
            if (r11 == 0) goto L_0x0414
            boolean r11 = r4.isMediaEmpty()
            if (r11 != 0) goto L_0x0414
            android.content.Context r10 = r1.mContext
            android.content.res.Resources r10 = r10.getResources()
            r11 = 2131034141(0x7var_d, float:1.7678791E38)
            int r10 = r10.getColor(r11)
            org.telegram.tgnet.TLRPC$Message r11 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r11 = r11.media
            boolean r12 = r11 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            r14 = 18
            if (r12 == 0) goto L_0x0375
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r11 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r11
            int r8 = android.os.Build.VERSION.SDK_INT
            if (r8 < r14) goto L_0x035d
            r8 = 1
            java.lang.Object[] r12 = new java.lang.Object[r8]
            org.telegram.tgnet.TLRPC$Poll r11 = r11.poll
            java.lang.String r11 = r11.question
            r14 = 0
            r12[r14] = r11
            java.lang.String r11 = "📊 ⁨%s⁩"
            java.lang.String r11 = java.lang.String.format(r11, r12)
            goto L_0x036e
        L_0x035d:
            r8 = 1
            r14 = 0
            java.lang.Object[] r12 = new java.lang.Object[r8]
            org.telegram.tgnet.TLRPC$Poll r8 = r11.poll
            java.lang.String r8 = r8.question
            r12[r14] = r8
            java.lang.String r8 = "📊 %s"
            java.lang.String r11 = java.lang.String.format(r8, r12)
        L_0x036e:
            r8 = 32
            r12 = 1
            r14 = 10
            r15 = 0
            goto L_0x03e5
        L_0x0375:
            boolean r12 = r11 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r12 == 0) goto L_0x03a4
            int r8 = android.os.Build.VERSION.SDK_INT
            if (r8 < r14) goto L_0x038f
            r8 = 1
            java.lang.Object[] r12 = new java.lang.Object[r8]
            org.telegram.tgnet.TLRPC$TL_game r11 = r11.game
            java.lang.String r11 = r11.title
            r15 = 0
            r12[r15] = r11
            java.lang.String r11 = "🎮 ⁨%s⁩"
            java.lang.String r11 = java.lang.String.format(r11, r12)
            goto L_0x03a0
        L_0x038f:
            r8 = 1
            r15 = 0
            java.lang.Object[] r12 = new java.lang.Object[r8]
            org.telegram.tgnet.TLRPC$TL_game r8 = r11.game
            java.lang.String r8 = r8.title
            r12[r15] = r8
            java.lang.String r8 = "🎮 %s"
            java.lang.String r11 = java.lang.String.format(r8, r12)
        L_0x03a0:
            r8 = 32
            r12 = 1
            goto L_0x03e3
        L_0x03a4:
            r15 = 0
            int r11 = r4.type
            r12 = 14
            if (r11 != r12) goto L_0x03da
            int r11 = android.os.Build.VERSION.SDK_INT
            if (r11 < r14) goto L_0x03c6
            java.lang.Object[] r8 = new java.lang.Object[r9]
            java.lang.String r11 = r4.getMusicAuthor()
            r8[r15] = r11
            java.lang.String r11 = r4.getMusicTitle()
            r12 = 1
            r8[r12] = r11
            java.lang.String r11 = "🎧 ⁨%s - %s⁩"
            java.lang.String r11 = java.lang.String.format(r11, r8)
            goto L_0x03e1
        L_0x03c6:
            r12 = 1
            java.lang.Object[] r11 = new java.lang.Object[r9]
            java.lang.String r14 = r4.getMusicAuthor()
            r11[r15] = r14
            java.lang.String r14 = r4.getMusicTitle()
            r11[r12] = r14
            java.lang.String r11 = java.lang.String.format(r8, r11)
            goto L_0x03e1
        L_0x03da:
            r12 = 1
            java.lang.CharSequence r8 = r4.messageText
            java.lang.String r11 = r8.toString()
        L_0x03e1:
            r8 = 32
        L_0x03e3:
            r14 = 10
        L_0x03e5:
            java.lang.String r8 = r11.replace(r14, r8)
            java.lang.Object[] r11 = new java.lang.Object[r9]
            r11[r15] = r8
            r11[r12] = r6
            java.lang.String r0 = java.lang.String.format(r0, r11)
            android.text.SpannableStringBuilder r8 = android.text.SpannableStringBuilder.valueOf(r0)
            org.telegram.ui.Components.ForegroundColorSpanThemable r0 = new org.telegram.ui.Components.ForegroundColorSpanThemable     // Catch:{ Exception -> 0x040d }
            java.lang.String r11 = "chats_attachMessage"
            r0.<init>(r11)     // Catch:{ Exception -> 0x040d }
            int r11 = r6.length()     // Catch:{ Exception -> 0x040d }
            int r11 = r11 + r9
            int r9 = r8.length()     // Catch:{ Exception -> 0x040d }
            r12 = 33
            r8.setSpan(r0, r11, r9, r12)     // Catch:{ Exception -> 0x040d }
            goto L_0x0411
        L_0x040d:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0411:
            r12 = r10
            r10 = r8
            goto L_0x0448
        L_0x0414:
            org.telegram.tgnet.TLRPC$Message r8 = r4.messageOwner
            java.lang.String r8 = r8.message
            if (r8 == 0) goto L_0x0443
            int r10 = r8.length()
            if (r10 <= r15) goto L_0x0426
            r10 = 0
            java.lang.String r8 = r8.substring(r10, r15)
            goto L_0x0427
        L_0x0426:
            r10 = 0
        L_0x0427:
            r11 = 32
            r14 = 10
            java.lang.String r8 = r8.replace(r14, r11)
            java.lang.String r8 = r8.trim()
            java.lang.Object[] r9 = new java.lang.Object[r9]
            r9[r10] = r8
            r8 = 1
            r9[r8] = r6
            java.lang.String r0 = java.lang.String.format(r0, r9)
            android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r0)
            goto L_0x0447
        L_0x0443:
            android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r10)
        L_0x0447:
            r10 = r0
        L_0x0448:
            org.telegram.ui.Components.ForegroundColorSpanThemable r0 = new org.telegram.ui.Components.ForegroundColorSpanThemable     // Catch:{ Exception -> 0x045d }
            java.lang.String r8 = "chats_nameMessage"
            r0.<init>(r8)     // Catch:{ Exception -> 0x045d }
            int r6 = r6.length()     // Catch:{ Exception -> 0x045d }
            r8 = 1
            int r6 = r6 + r8
            r8 = 33
            r9 = 0
            r10.setSpan(r0, r9, r6, r8)     // Catch:{ Exception -> 0x045d }
            goto L_0x054f
        L_0x045d:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x054f
        L_0x0463:
            org.telegram.tgnet.TLRPC$Message r0 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r6 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r6 == 0) goto L_0x0480
            org.telegram.tgnet.TLRPC$Photo r6 = r0.photo
            boolean r6 = r6 instanceof org.telegram.tgnet.TLRPC$TL_photoEmpty
            if (r6 == 0) goto L_0x0480
            int r6 = r0.ttl_seconds
            if (r6 == 0) goto L_0x0480
            r0 = 2131624391(0x7f0e01c7, float:1.887596E38)
            java.lang.String r6 = "AttachPhotoExpired"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r6, r0)
            goto L_0x054f
        L_0x0480:
            boolean r6 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r6 == 0) goto L_0x0499
            org.telegram.tgnet.TLRPC$Document r6 = r0.document
            boolean r6 = r6 instanceof org.telegram.tgnet.TLRPC$TL_documentEmpty
            if (r6 == 0) goto L_0x0499
            int r6 = r0.ttl_seconds
            if (r6 == 0) goto L_0x0499
            r0 = 2131624397(0x7f0e01cd, float:1.8875973E38)
            java.lang.String r6 = "AttachVideoExpired"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r6, r0)
            goto L_0x054f
        L_0x0499:
            java.lang.CharSequence r6 = r4.caption
            if (r6 == 0) goto L_0x04d6
            boolean r0 = r4.isVideo()
            if (r0 == 0) goto L_0x04a6
            r14 = r17
            goto L_0x04c3
        L_0x04a6:
            boolean r0 = r4.isVoice()
            if (r0 == 0) goto L_0x04af
            r14 = r16
            goto L_0x04c3
        L_0x04af:
            boolean r0 = r4.isMusic()
            if (r0 == 0) goto L_0x04b6
            goto L_0x04c3
        L_0x04b6:
            boolean r0 = r4.isPhoto()
            if (r0 == 0) goto L_0x04c0
            java.lang.String r14 = "🖼 "
            goto L_0x04c3
        L_0x04c0:
            java.lang.String r14 = "📎 "
        L_0x04c3:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r14)
            java.lang.CharSequence r6 = r4.caption
            r0.append(r6)
            java.lang.String r10 = r0.toString()
            goto L_0x054f
        L_0x04d6:
            boolean r6 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r6 == 0) goto L_0x04f4
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r0 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r0
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r8 = "📊 "
            r6.append(r8)
            org.telegram.tgnet.TLRPC$Poll r0 = r0.poll
            java.lang.String r0 = r0.question
            r6.append(r0)
            java.lang.String r0 = r6.toString()
        L_0x04f2:
            r10 = r0
            goto L_0x0536
        L_0x04f4:
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r0 == 0) goto L_0x0513
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
            goto L_0x04f2
        L_0x0513:
            int r0 = r4.type
            r6 = 14
            if (r0 != r6) goto L_0x052e
            java.lang.Object[] r0 = new java.lang.Object[r9]
            java.lang.String r6 = r4.getMusicAuthor()
            r9 = 0
            r0[r9] = r6
            java.lang.String r6 = r4.getMusicTitle()
            r9 = 1
            r0[r9] = r6
            java.lang.String r0 = java.lang.String.format(r8, r0)
            goto L_0x04f2
        L_0x052e:
            java.lang.CharSequence r0 = r4.messageText
            java.util.ArrayList<java.lang.String> r6 = r4.highlightedWords
            org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r0, (java.util.ArrayList<java.lang.String>) r6)
            goto L_0x04f2
        L_0x0536:
            org.telegram.tgnet.TLRPC$Message r0 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            if (r0 == 0) goto L_0x054f
            boolean r0 = r4.isMediaEmpty()
            if (r0 != 0) goto L_0x054f
            android.content.Context r0 = r1.mContext
            android.content.res.Resources r0 = r0.getResources()
            r6 = 2131034141(0x7var_d, float:1.7678791E38)
            int r12 = r0.getColor(r6)
        L_0x054f:
            org.telegram.tgnet.TLRPC$Message r0 = r4.messageOwner
            int r0 = r0.date
            long r8 = (long) r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.stringForMessageListDate(r8)
            r4 = 2131230908(0x7var_bc, float:1.8077882E38)
            r13.setTextViewText(r4, r0)
            java.lang.String r0 = r10.toString()
            r4 = 2131230906(0x7var_ba, float:1.8077878E38)
            r13.setTextViewText(r4, r0)
            r13.setTextColor(r4, r12)
            goto L_0x0587
        L_0x056c:
            r4 = 2131230908(0x7var_bc, float:1.8077882E38)
            if (r7 == 0) goto L_0x057e
            int r0 = r7.last_message_date
            if (r0 == 0) goto L_0x057e
            long r8 = (long) r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.stringForMessageListDate(r8)
            r13.setTextViewText(r4, r0)
            goto L_0x0581
        L_0x057e:
            r13.setTextViewText(r4, r10)
        L_0x0581:
            r4 = 2131230906(0x7var_ba, float:1.8077878E38)
            r13.setTextViewText(r4, r10)
        L_0x0587:
            r0 = 8
            r4 = 2131230904(0x7var_b8, float:1.8077874E38)
            if (r7 == 0) goto L_0x05c6
            int r6 = r7.unread_count
            if (r6 <= 0) goto L_0x05c6
            r8 = 1
            java.lang.Object[] r8 = new java.lang.Object[r8]
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            r9 = 0
            r8[r9] = r6
            java.lang.String r6 = "%d"
            java.lang.String r6 = java.lang.String.format(r6, r8)
            r13.setTextViewText(r4, r6)
            r13.setViewVisibility(r4, r9)
            org.telegram.messenger.AccountInstance r6 = r1.accountInstance
            org.telegram.messenger.MessagesController r6 = r6.getMessagesController()
            long r7 = r7.id
            boolean r6 = r6.isDialogMuted(r7)
            java.lang.String r7 = "setBackgroundResource"
            if (r6 == 0) goto L_0x05bf
            r6 = 2131166124(0x7var_ac, float:1.7946484E38)
            r13.setInt(r4, r7, r6)
            goto L_0x05ca
        L_0x05bf:
            r6 = 2131166123(0x7var_ab, float:1.7946482E38)
            r13.setInt(r4, r7, r6)
            goto L_0x05ca
        L_0x05c6:
            r9 = 0
            r13.setViewVisibility(r4, r0)
        L_0x05ca:
            android.os.Bundle r4 = new android.os.Bundle
            r4.<init>()
            long r6 = r5.longValue()
            r10 = 0
            int r8 = (r6 > r10 ? 1 : (r6 == r10 ? 0 : -1))
            if (r8 <= 0) goto L_0x05e5
            long r5 = r5.longValue()
            int r6 = (int) r5
            java.lang.String r5 = "userId"
            r4.putInt(r5, r6)
            goto L_0x05f0
        L_0x05e5:
            long r5 = r5.longValue()
            int r6 = (int) r5
            int r5 = -r6
            java.lang.String r6 = "chatId"
            r4.putInt(r6, r5)
        L_0x05f0:
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
            if (r2 != r4) goto L_0x0613
            r4 = 8
            goto L_0x0614
        L_0x0613:
            r4 = 0
        L_0x0614:
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
