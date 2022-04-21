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
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;

/* compiled from: ChatsWidgetService */
class ChatsRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private AccountInstance accountInstance;
    private int appWidgetId;
    private RectF bitmapRect;
    private boolean deleted;
    private LongSparseArray<TLRPC.Dialog> dialogs = new LongSparseArray<>();
    private ArrayList<Long> dids = new ArrayList<>();
    private Context mContext;
    private LongSparseArray<MessageObject> messageObjects = new LongSparseArray<>();
    private Paint roundPaint;

    public ChatsRemoteViewsFactory(Context context, Intent intent) {
        this.mContext = context;
        Theme.createDialogsResources(context);
        boolean z = false;
        this.appWidgetId = intent.getIntExtra("appWidgetId", 0);
        SharedPreferences preferences = context.getSharedPreferences("shortcut_widget", 0);
        int accountId = preferences.getInt("account" + this.appWidgetId, -1);
        if (accountId >= 0) {
            this.accountInstance = AccountInstance.getInstance(accountId);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("deleted");
        sb.append(this.appWidgetId);
        this.deleted = (preferences.getBoolean(sb.toString(), false) || this.accountInstance == null) ? true : z;
    }

    public void onCreate() {
        ApplicationLoader.postInitApplication();
    }

    public void onDestroy() {
    }

    public int getCount() {
        if (this.deleted) {
            return 1;
        }
        return this.dids.size() + 1;
    }

    /* JADX WARNING: Removed duplicated region for block: B:248:0x0628  */
    /* JADX WARNING: Removed duplicated region for block: B:259:0x0657  */
    /* JADX WARNING: Removed duplicated region for block: B:263:0x0696  */
    /* JADX WARNING: Removed duplicated region for block: B:266:0x06a9  */
    /* JADX WARNING: Removed duplicated region for block: B:267:0x06b3  */
    /* JADX WARNING: Removed duplicated region for block: B:270:0x06de  */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x0169 A[SYNTHETIC, Splitter:B:51:0x0169] */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x0194  */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x01c3 A[SYNTHETIC, Splitter:B:71:0x01c3] */
    /* JADX WARNING: Removed duplicated region for block: B:94:0x0252  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.widget.RemoteViews getViewAt(int r31) {
        /*
            r30 = this;
            r1 = r30
            r2 = r31
            boolean r0 = r1.deleted
            if (r0 == 0) goto L_0x0026
            android.widget.RemoteViews r0 = new android.widget.RemoteViews
            android.content.Context r3 = r1.mContext
            java.lang.String r3 = r3.getPackageName()
            r4 = 2131427356(0x7f0b001c, float:1.8476326E38)
            r0.<init>(r3, r4)
            r3 = 2131230948(0x7var_e4, float:1.8077963E38)
            r4 = 2131629010(0x7f0e13d2, float:1.8885329E38)
            java.lang.String r5 = "WidgetLoggedOff"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r0.setTextViewText(r3, r4)
            return r0
        L_0x0026:
            java.util.ArrayList<java.lang.Long> r0 = r1.dids
            int r0 = r0.size()
            java.lang.String r3 = "currentAccount"
            r4 = 0
            if (r2 < r0) goto L_0x0077
            android.widget.RemoteViews r0 = new android.widget.RemoteViews
            android.content.Context r5 = r1.mContext
            java.lang.String r5 = r5.getPackageName()
            r6 = 2131427357(0x7f0b001d, float:1.8476328E38)
            r0.<init>(r5, r6)
            r5 = 2131230950(0x7var_e6, float:1.8077967E38)
            r6 = 2131628329(0x7f0e1129, float:1.8883948E38)
            java.lang.String r7 = "TapToEditWidget"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            r0.setTextViewText(r5, r6)
            android.os.Bundle r5 = new android.os.Bundle
            r5.<init>()
            int r6 = r1.appWidgetId
            java.lang.String r7 = "appWidgetId"
            r5.putInt(r7, r6)
            java.lang.String r6 = "appWidgetType"
            r5.putInt(r6, r4)
            org.telegram.messenger.AccountInstance r4 = r1.accountInstance
            int r4 = r4.getCurrentAccount()
            r5.putInt(r3, r4)
            android.content.Intent r3 = new android.content.Intent
            r3.<init>()
            r3.putExtras(r5)
            r4 = 2131230949(0x7var_e5, float:1.8077965E38)
            r0.setOnClickFillInIntent(r4, r3)
            return r0
        L_0x0077:
            java.util.ArrayList<java.lang.Long> r0 = r1.dids
            java.lang.Object r0 = r0.get(r2)
            r5 = r0
            java.lang.Long r5 = (java.lang.Long) r5
            java.lang.String r0 = ""
            r6 = 0
            r7 = 0
            r8 = 0
            long r9 = r5.longValue()
            boolean r9 = org.telegram.messenger.DialogObject.isUserDialog(r9)
            r10 = 0
            if (r9 == 0) goto L_0x010b
            org.telegram.messenger.AccountInstance r9 = r1.accountInstance
            org.telegram.messenger.MessagesController r9 = r9.getMessagesController()
            org.telegram.tgnet.TLRPC$User r7 = r9.getUser(r5)
            if (r7 == 0) goto L_0x0106
            boolean r9 = org.telegram.messenger.UserObject.isUserSelf(r7)
            if (r9 == 0) goto L_0x00ad
            r9 = 2131627866(0x7f0e0f5a, float:1.8883009E38)
            java.lang.String r12 = "SavedMessages"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r12, r9)
            goto L_0x00d5
        L_0x00ad:
            boolean r9 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC.User) r7)
            if (r9 == 0) goto L_0x00bd
            r9 = 2131627725(0x7f0e0ecd, float:1.8882723E38)
            java.lang.String r12 = "RepliesTitle"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r12, r9)
            goto L_0x00d5
        L_0x00bd:
            boolean r9 = org.telegram.messenger.UserObject.isDeleted(r7)
            if (r9 == 0) goto L_0x00cd
            r9 = 2131626025(0x7f0e0829, float:1.8879275E38)
            java.lang.String r12 = "HiddenName"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r12, r9)
            goto L_0x00d5
        L_0x00cd:
            java.lang.String r9 = r7.first_name
            java.lang.String r12 = r7.last_name
            java.lang.String r0 = org.telegram.messenger.ContactsController.formatName(r9, r12)
        L_0x00d5:
            boolean r9 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC.User) r7)
            if (r9 != 0) goto L_0x0147
            boolean r9 = org.telegram.messenger.UserObject.isUserSelf(r7)
            if (r9 != 0) goto L_0x0147
            org.telegram.tgnet.TLRPC$UserProfilePhoto r9 = r7.photo
            if (r9 == 0) goto L_0x0147
            org.telegram.tgnet.TLRPC$UserProfilePhoto r9 = r7.photo
            org.telegram.tgnet.TLRPC$FileLocation r9 = r9.photo_small
            if (r9 == 0) goto L_0x0147
            org.telegram.tgnet.TLRPC$UserProfilePhoto r9 = r7.photo
            org.telegram.tgnet.TLRPC$FileLocation r9 = r9.photo_small
            long r12 = r9.volume_id
            int r9 = (r12 > r10 ? 1 : (r12 == r10 ? 0 : -1))
            if (r9 == 0) goto L_0x0147
            org.telegram.tgnet.TLRPC$UserProfilePhoto r9 = r7.photo
            org.telegram.tgnet.TLRPC$FileLocation r9 = r9.photo_small
            int r9 = r9.local_id
            if (r9 == 0) goto L_0x0147
            org.telegram.tgnet.TLRPC$UserProfilePhoto r9 = r7.photo
            org.telegram.tgnet.TLRPC$FileLocation r6 = r9.photo_small
            r9 = r8
            r8 = r7
            r7 = r6
            r6 = r0
            goto L_0x0150
        L_0x0106:
            r9 = r8
            r8 = r7
            r7 = r6
            r6 = r0
            goto L_0x0150
        L_0x010b:
            org.telegram.messenger.AccountInstance r9 = r1.accountInstance
            org.telegram.messenger.MessagesController r9 = r9.getMessagesController()
            long r12 = r5.longValue()
            long r12 = -r12
            java.lang.Long r12 = java.lang.Long.valueOf(r12)
            org.telegram.tgnet.TLRPC$Chat r8 = r9.getChat(r12)
            if (r8 == 0) goto L_0x014c
            java.lang.String r0 = r8.title
            org.telegram.tgnet.TLRPC$ChatPhoto r9 = r8.photo
            if (r9 == 0) goto L_0x0147
            org.telegram.tgnet.TLRPC$ChatPhoto r9 = r8.photo
            org.telegram.tgnet.TLRPC$FileLocation r9 = r9.photo_small
            if (r9 == 0) goto L_0x0147
            org.telegram.tgnet.TLRPC$ChatPhoto r9 = r8.photo
            org.telegram.tgnet.TLRPC$FileLocation r9 = r9.photo_small
            long r12 = r9.volume_id
            int r9 = (r12 > r10 ? 1 : (r12 == r10 ? 0 : -1))
            if (r9 == 0) goto L_0x0147
            org.telegram.tgnet.TLRPC$ChatPhoto r9 = r8.photo
            org.telegram.tgnet.TLRPC$FileLocation r9 = r9.photo_small
            int r9 = r9.local_id
            if (r9 == 0) goto L_0x0147
            org.telegram.tgnet.TLRPC$ChatPhoto r9 = r8.photo
            org.telegram.tgnet.TLRPC$FileLocation r6 = r9.photo_small
            r9 = r8
            r8 = r7
            r7 = r6
            r6 = r0
            goto L_0x0150
        L_0x0147:
            r9 = r8
            r8 = r7
            r7 = r6
            r6 = r0
            goto L_0x0150
        L_0x014c:
            r9 = r8
            r8 = r7
            r7 = r6
            r6 = r0
        L_0x0150:
            android.widget.RemoteViews r0 = new android.widget.RemoteViews
            android.content.Context r10 = r1.mContext
            java.lang.String r10 = r10.getPackageName()
            r11 = 2131427349(0x7f0b0015, float:1.8476312E38)
            r0.<init>(r10, r11)
            r10 = r0
            r0 = 2131230914(0x7var_c2, float:1.8077894E38)
            r10.setTextViewText(r0, r6)
            r0 = 0
            r12 = 1
            if (r7 == 0) goto L_0x017e
            java.io.File r13 = org.telegram.messenger.FileLoader.getPathToAttach(r7, r12)     // Catch:{ all -> 0x0177 }
            java.lang.String r14 = r13.toString()     // Catch:{ all -> 0x0177 }
            android.graphics.Bitmap r14 = android.graphics.BitmapFactory.decodeFile(r14)     // Catch:{ all -> 0x0177 }
            r0 = r14
            goto L_0x017e
        L_0x0177:
            r0 = move-exception
            r20 = r6
            r21 = r7
            goto L_0x0231
        L_0x017e:
            r13 = 1111490560(0x42400000, float:48.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)     // Catch:{ all -> 0x022c }
            android.graphics.Bitmap$Config r14 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x022c }
            android.graphics.Bitmap r14 = android.graphics.Bitmap.createBitmap(r13, r13, r14)     // Catch:{ all -> 0x022c }
            r14.eraseColor(r4)     // Catch:{ all -> 0x022c }
            android.graphics.Canvas r15 = new android.graphics.Canvas     // Catch:{ all -> 0x022c }
            r15.<init>(r14)     // Catch:{ all -> 0x022c }
            if (r0 != 0) goto L_0x01c3
            if (r8 == 0) goto L_0x01b1
            org.telegram.ui.Components.AvatarDrawable r11 = new org.telegram.ui.Components.AvatarDrawable     // Catch:{ all -> 0x0177 }
            r11.<init>((org.telegram.tgnet.TLRPC.User) r8)     // Catch:{ all -> 0x0177 }
            boolean r17 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC.User) r8)     // Catch:{ all -> 0x0177 }
            if (r17 == 0) goto L_0x01a7
            r4 = 12
            r11.setAvatarType(r4)     // Catch:{ all -> 0x0177 }
            goto L_0x01b7
        L_0x01a7:
            boolean r4 = org.telegram.messenger.UserObject.isUserSelf(r8)     // Catch:{ all -> 0x0177 }
            if (r4 == 0) goto L_0x01b7
            r11.setAvatarType(r12)     // Catch:{ all -> 0x0177 }
            goto L_0x01b7
        L_0x01b1:
            org.telegram.ui.Components.AvatarDrawable r4 = new org.telegram.ui.Components.AvatarDrawable     // Catch:{ all -> 0x0177 }
            r4.<init>((org.telegram.tgnet.TLRPC.Chat) r9)     // Catch:{ all -> 0x0177 }
            r11 = r4
        L_0x01b7:
            r4 = 0
            r11.setBounds(r4, r4, r13, r13)     // Catch:{ all -> 0x0177 }
            r11.draw(r15)     // Catch:{ all -> 0x0177 }
            r20 = r6
            r21 = r7
            goto L_0x021b
        L_0x01c3:
            android.graphics.BitmapShader r4 = new android.graphics.BitmapShader     // Catch:{ all -> 0x022c }
            android.graphics.Shader$TileMode r11 = android.graphics.Shader.TileMode.CLAMP     // Catch:{ all -> 0x022c }
            android.graphics.Shader$TileMode r12 = android.graphics.Shader.TileMode.CLAMP     // Catch:{ all -> 0x022c }
            r4.<init>(r0, r11, r12)     // Catch:{ all -> 0x022c }
            android.graphics.Paint r11 = r1.roundPaint     // Catch:{ all -> 0x022c }
            if (r11 != 0) goto L_0x01df
            android.graphics.Paint r11 = new android.graphics.Paint     // Catch:{ all -> 0x0177 }
            r12 = 1
            r11.<init>(r12)     // Catch:{ all -> 0x0177 }
            r1.roundPaint = r11     // Catch:{ all -> 0x0177 }
            android.graphics.RectF r11 = new android.graphics.RectF     // Catch:{ all -> 0x0177 }
            r11.<init>()     // Catch:{ all -> 0x0177 }
            r1.bitmapRect = r11     // Catch:{ all -> 0x0177 }
        L_0x01df:
            float r11 = (float) r13
            int r12 = r0.getWidth()     // Catch:{ all -> 0x022c }
            float r12 = (float) r12     // Catch:{ all -> 0x022c }
            float r11 = r11 / r12
            r15.save()     // Catch:{ all -> 0x022c }
            r15.scale(r11, r11)     // Catch:{ all -> 0x022c }
            android.graphics.Paint r12 = r1.roundPaint     // Catch:{ all -> 0x022c }
            r12.setShader(r4)     // Catch:{ all -> 0x022c }
            android.graphics.RectF r12 = r1.bitmapRect     // Catch:{ all -> 0x022c }
            r19 = r4
            int r4 = r0.getWidth()     // Catch:{ all -> 0x022c }
            float r4 = (float) r4
            r20 = r6
            int r6 = r0.getHeight()     // Catch:{ all -> 0x0228 }
            float r6 = (float) r6
            r21 = r7
            r7 = 0
            r12.set(r7, r7, r4, r6)     // Catch:{ all -> 0x0226 }
            android.graphics.RectF r4 = r1.bitmapRect     // Catch:{ all -> 0x0226 }
            int r6 = r0.getWidth()     // Catch:{ all -> 0x0226 }
            float r6 = (float) r6     // Catch:{ all -> 0x0226 }
            int r7 = r0.getHeight()     // Catch:{ all -> 0x0226 }
            float r7 = (float) r7     // Catch:{ all -> 0x0226 }
            android.graphics.Paint r12 = r1.roundPaint     // Catch:{ all -> 0x0226 }
            r15.drawRoundRect(r4, r6, r7, r12)     // Catch:{ all -> 0x0226 }
            r15.restore()     // Catch:{ all -> 0x0226 }
        L_0x021b:
            r4 = 0
            r15.setBitmap(r4)     // Catch:{ all -> 0x0226 }
            r4 = 2131230910(0x7var_be, float:1.8077886E38)
            r10.setImageViewBitmap(r4, r14)     // Catch:{ all -> 0x0226 }
            goto L_0x0234
        L_0x0226:
            r0 = move-exception
            goto L_0x0231
        L_0x0228:
            r0 = move-exception
            r21 = r7
            goto L_0x0231
        L_0x022c:
            r0 = move-exception
            r20 = r6
            r21 = r7
        L_0x0231:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0234:
            androidx.collection.LongSparseArray<org.telegram.messenger.MessageObject> r0 = r1.messageObjects
            long r6 = r5.longValue()
            java.lang.Object r0 = r0.get(r6)
            r4 = r0
            org.telegram.messenger.MessageObject r4 = (org.telegram.messenger.MessageObject) r4
            androidx.collection.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r0 = r1.dialogs
            long r6 = r5.longValue()
            java.lang.Object r0 = r0.get(r6)
            r6 = r0
            org.telegram.tgnet.TLRPC$Dialog r6 = (org.telegram.tgnet.TLRPC.Dialog) r6
            java.lang.String r0 = ""
            if (r4 == 0) goto L_0x0628
            r12 = 0
            r13 = 0
            long r14 = r4.getFromChatId()
            boolean r19 = org.telegram.messenger.DialogObject.isUserDialog(r14)
            if (r19 == 0) goto L_0x026d
            org.telegram.messenger.AccountInstance r7 = r1.accountInstance
            org.telegram.messenger.MessagesController r7 = r7.getMessagesController()
            java.lang.Long r11 = java.lang.Long.valueOf(r14)
            org.telegram.tgnet.TLRPC$User r12 = r7.getUser(r11)
            goto L_0x0280
        L_0x026d:
            org.telegram.messenger.AccountInstance r7 = r1.accountInstance
            org.telegram.messenger.MessagesController r7 = r7.getMessagesController()
            r22 = r12
            long r11 = -r14
            java.lang.Long r11 = java.lang.Long.valueOf(r11)
            org.telegram.tgnet.TLRPC$Chat r13 = r7.getChat(r11)
            r12 = r22
        L_0x0280:
            android.content.Context r7 = r1.mContext
            android.content.res.Resources r7 = r7.getResources()
            r11 = 2131034146(0x7var_, float:1.7678801E38)
            int r7 = r7.getColor(r11)
            org.telegram.tgnet.TLRPC$Message r11 = r4.messageOwner
            boolean r11 = r11 instanceof org.telegram.tgnet.TLRPC.TL_messageService
            r22 = r7
            r7 = 2131034141(0x7var_d, float:1.7678791E38)
            if (r11 == 0) goto L_0x02c7
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r9)
            if (r0 == 0) goto L_0x02b1
            org.telegram.tgnet.TLRPC$Message r0 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r0 = r0.action
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageActionHistoryClear
            if (r0 != 0) goto L_0x02ae
            org.telegram.tgnet.TLRPC$Message r0 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r0 = r0.action
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChannelMigrateFrom
            if (r0 == 0) goto L_0x02b1
        L_0x02ae:
            java.lang.String r0 = ""
            goto L_0x02b3
        L_0x02b1:
            java.lang.CharSequence r0 = r4.messageText
        L_0x02b3:
            android.content.Context r11 = r1.mContext
            android.content.res.Resources r11 = r11.getResources()
            int r7 = r11.getColor(r7)
            r24 = r8
            r26 = r9
            r28 = r12
            r29 = r13
            goto L_0x060b
        L_0x02c7:
            r11 = 1
            java.lang.String r7 = "🎧 %s - %s"
            r24 = r8
            if (r9 == 0) goto L_0x04f0
            if (r13 != 0) goto L_0x04f0
            boolean r25 = org.telegram.messenger.ChatObject.isChannel(r9)
            if (r25 == 0) goto L_0x02e7
            boolean r25 = org.telegram.messenger.ChatObject.isMegagroup(r9)
            if (r25 == 0) goto L_0x02dd
            goto L_0x02e7
        L_0x02dd:
            r26 = r9
            r27 = r11
            r28 = r12
            r29 = r13
            goto L_0x04f8
        L_0x02e7:
            boolean r16 = r4.isOutOwner()
            if (r16 == 0) goto L_0x02f9
            r8 = 2131625932(0x7f0e07cc, float:1.8879086E38)
            r26 = r9
            java.lang.String r9 = "FromYou"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
            goto L_0x030a
        L_0x02f9:
            r26 = r9
            if (r12 == 0) goto L_0x0308
            java.lang.String r8 = org.telegram.messenger.UserObject.getFirstName(r12)
            java.lang.String r9 = "\n"
            java.lang.String r8 = r8.replace(r9, r0)
            goto L_0x030a
        L_0x0308:
            java.lang.String r8 = "DELETED"
        L_0x030a:
            java.lang.String r9 = "%2$s: ⁨%1$s⁩"
            r27 = r11
            java.lang.CharSequence r11 = r4.caption
            r28 = r12
            r12 = 150(0x96, float:2.1E-43)
            if (r11 == 0) goto L_0x037e
            java.lang.CharSequence r0 = r4.caption
            java.lang.String r0 = r0.toString()
            int r7 = r0.length()
            if (r7 <= r12) goto L_0x0327
            r7 = 0
            java.lang.String r0 = r0.substring(r7, r12)
        L_0x0327:
            boolean r7 = r4.isVideo()
            if (r7 == 0) goto L_0x0330
            java.lang.String r7 = "📹 "
            goto L_0x034d
        L_0x0330:
            boolean r7 = r4.isVoice()
            if (r7 == 0) goto L_0x0339
            java.lang.String r7 = "🎤 "
            goto L_0x034d
        L_0x0339:
            boolean r7 = r4.isMusic()
            if (r7 == 0) goto L_0x0342
            java.lang.String r7 = "🎧 "
            goto L_0x034d
        L_0x0342:
            boolean r7 = r4.isPhoto()
            if (r7 == 0) goto L_0x034b
            java.lang.String r7 = "🖼 "
            goto L_0x034d
        L_0x034b:
            java.lang.String r7 = "📎 "
        L_0x034d:
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            r12.append(r7)
            r23 = r7
            r29 = r13
            r7 = 32
            r13 = 10
            java.lang.String r7 = r0.replace(r13, r7)
            r12.append(r7)
            java.lang.String r7 = r12.toString()
            r12 = 0
            r11[r12] = r7
            r7 = 1
            r11[r7] = r8
            java.lang.String r7 = java.lang.String.format(r9, r11)
            android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r7)
            r12 = r0
            r7 = r22
            goto L_0x04cd
        L_0x037e:
            r29 = r13
            org.telegram.tgnet.TLRPC$Message r11 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r11 = r11.media
            if (r11 == 0) goto L_0x048f
            boolean r11 = r4.isMediaEmpty()
            if (r11 != 0) goto L_0x048f
            android.content.Context r0 = r1.mContext
            android.content.res.Resources r0 = r0.getResources()
            r11 = 2131034141(0x7var_d, float:1.7678791E38)
            int r11 = r0.getColor(r11)
            org.telegram.tgnet.TLRPC$Message r0 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll
            r12 = 18
            if (r0 == 0) goto L_0x03d2
            org.telegram.tgnet.TLRPC$Message r0 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r0 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r0
            int r7 = android.os.Build.VERSION.SDK_INT
            if (r7 < r12) goto L_0x03bf
            r7 = 1
            java.lang.Object[] r12 = new java.lang.Object[r7]
            org.telegram.tgnet.TLRPC$Poll r13 = r0.poll
            java.lang.String r13 = r13.question
            r17 = 0
            r12[r17] = r13
            java.lang.String r13 = "📊 ⁨%s⁩"
            java.lang.String r12 = java.lang.String.format(r13, r12)
            goto L_0x03d0
        L_0x03bf:
            r7 = 1
            r17 = 0
            java.lang.Object[] r12 = new java.lang.Object[r7]
            org.telegram.tgnet.TLRPC$Poll r7 = r0.poll
            java.lang.String r7 = r7.question
            r12[r17] = r7
            java.lang.String r7 = "📊 %s"
            java.lang.String r12 = java.lang.String.format(r7, r12)
        L_0x03d0:
            goto L_0x0448
        L_0x03d2:
            org.telegram.tgnet.TLRPC$Message r0 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame
            if (r0 == 0) goto L_0x0408
            int r0 = android.os.Build.VERSION.SDK_INT
            if (r0 < r12) goto L_0x03f3
            r7 = 1
            java.lang.Object[] r0 = new java.lang.Object[r7]
            org.telegram.tgnet.TLRPC$Message r12 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r12 = r12.media
            org.telegram.tgnet.TLRPC$TL_game r12 = r12.game
            java.lang.String r12 = r12.title
            r13 = 0
            r0[r13] = r12
            java.lang.String r12 = "🎮 ⁨%s⁩"
            java.lang.String r12 = java.lang.String.format(r12, r0)
            goto L_0x0448
        L_0x03f3:
            r7 = 1
            r13 = 0
            java.lang.Object[] r0 = new java.lang.Object[r7]
            org.telegram.tgnet.TLRPC$Message r7 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r7 = r7.media
            org.telegram.tgnet.TLRPC$TL_game r7 = r7.game
            java.lang.String r7 = r7.title
            r0[r13] = r7
            java.lang.String r7 = "🎮 %s"
            java.lang.String r12 = java.lang.String.format(r7, r0)
            goto L_0x0448
        L_0x0408:
            int r0 = r4.type
            r13 = 14
            if (r0 != r13) goto L_0x0442
            int r0 = android.os.Build.VERSION.SDK_INT
            if (r0 < r12) goto L_0x042b
            r0 = 2
            java.lang.Object[] r7 = new java.lang.Object[r0]
            java.lang.String r12 = r4.getMusicAuthor()
            r13 = 0
            r7[r13] = r12
            java.lang.String r12 = r4.getMusicTitle()
            r18 = 1
            r7[r18] = r12
            java.lang.String r12 = "🎧 ⁨%s - %s⁩"
            java.lang.String r12 = java.lang.String.format(r12, r7)
            goto L_0x0448
        L_0x042b:
            r0 = 2
            r13 = 0
            r18 = 1
            java.lang.Object[] r12 = new java.lang.Object[r0]
            java.lang.String r0 = r4.getMusicAuthor()
            r12[r13] = r0
            java.lang.String r0 = r4.getMusicTitle()
            r12[r18] = r0
            java.lang.String r12 = java.lang.String.format(r7, r12)
            goto L_0x0448
        L_0x0442:
            java.lang.CharSequence r0 = r4.messageText
            java.lang.String r12 = r0.toString()
        L_0x0448:
            r0 = 32
            r7 = 10
            java.lang.String r7 = r12.replace(r7, r0)
            r0 = 2
            java.lang.Object[] r12 = new java.lang.Object[r0]
            r13 = 0
            r12[r13] = r7
            r13 = 1
            r12[r13] = r8
            java.lang.String r0 = java.lang.String.format(r9, r12)
            android.text.SpannableStringBuilder r12 = android.text.SpannableStringBuilder.valueOf(r0)
            org.telegram.ui.Components.ForegroundColorSpanThemable r0 = new org.telegram.ui.Components.ForegroundColorSpanThemable     // Catch:{ Exception -> 0x0484 }
            java.lang.String r13 = "chats_attachMessage"
            r0.<init>(r13)     // Catch:{ Exception -> 0x0484 }
            int r13 = r8.length()     // Catch:{ Exception -> 0x0484 }
            r22 = 2
            int r13 = r13 + 2
            r22 = r7
            int r7 = r12.length()     // Catch:{ Exception -> 0x0480 }
            r23 = r11
            r11 = 33
            r12.setSpan(r0, r13, r7, r11)     // Catch:{ Exception -> 0x047e }
            goto L_0x048c
        L_0x047e:
            r0 = move-exception
            goto L_0x0489
        L_0x0480:
            r0 = move-exception
            r23 = r11
            goto L_0x0489
        L_0x0484:
            r0 = move-exception
            r22 = r7
            r23 = r11
        L_0x0489:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x048c:
            r7 = r23
            goto L_0x04cd
        L_0x048f:
            org.telegram.tgnet.TLRPC$Message r7 = r4.messageOwner
            java.lang.String r7 = r7.message
            if (r7 == 0) goto L_0x04c6
            org.telegram.tgnet.TLRPC$Message r0 = r4.messageOwner
            java.lang.String r0 = r0.message
            int r7 = r0.length()
            if (r7 <= r12) goto L_0x04a5
            r7 = 0
            java.lang.String r0 = r0.substring(r7, r12)
            goto L_0x04a6
        L_0x04a5:
            r7 = 0
        L_0x04a6:
            r11 = 32
            r12 = 10
            java.lang.String r11 = r0.replace(r12, r11)
            java.lang.String r0 = r11.trim()
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]
            r11[r7] = r0
            r7 = 1
            r11[r7] = r8
            java.lang.String r7 = java.lang.String.format(r9, r11)
            android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r7)
            r12 = r0
            r7 = r22
            goto L_0x04cd
        L_0x04c6:
            android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r0)
            r12 = r0
            r7 = r22
        L_0x04cd:
            org.telegram.ui.Components.ForegroundColorSpanThemable r0 = new org.telegram.ui.Components.ForegroundColorSpanThemable     // Catch:{ Exception -> 0x04e5 }
            java.lang.String r11 = "chats_nameMessage"
            r0.<init>(r11)     // Catch:{ Exception -> 0x04e5 }
            int r11 = r8.length()     // Catch:{ Exception -> 0x04e5 }
            r13 = 1
            int r11 = r11 + r13
            r16 = r7
            r7 = 0
            r13 = 33
            r12.setSpan(r0, r7, r11, r13)     // Catch:{ Exception -> 0x04e3 }
            goto L_0x04eb
        L_0x04e3:
            r0 = move-exception
            goto L_0x04e8
        L_0x04e5:
            r0 = move-exception
            r16 = r7
        L_0x04e8:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x04eb:
            r0 = r12
            r7 = r16
            goto L_0x060b
        L_0x04f0:
            r26 = r9
            r27 = r11
            r28 = r12
            r29 = r13
        L_0x04f8:
            org.telegram.tgnet.TLRPC$Message r0 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto
            if (r0 == 0) goto L_0x051f
            org.telegram.tgnet.TLRPC$Message r0 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            org.telegram.tgnet.TLRPC$Photo r0 = r0.photo
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_photoEmpty
            if (r0 == 0) goto L_0x051f
            org.telegram.tgnet.TLRPC$Message r0 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            int r0 = r0.ttl_seconds
            if (r0 == 0) goto L_0x051f
            r0 = 2131624444(0x7f0e01fc, float:1.8876068E38)
            java.lang.String r7 = "AttachPhotoExpired"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r7, r0)
            r7 = r22
            goto L_0x060b
        L_0x051f:
            org.telegram.tgnet.TLRPC$Message r0 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument
            if (r0 == 0) goto L_0x0546
            org.telegram.tgnet.TLRPC$Message r0 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            org.telegram.tgnet.TLRPC$Document r0 = r0.document
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_documentEmpty
            if (r0 == 0) goto L_0x0546
            org.telegram.tgnet.TLRPC$Message r0 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            int r0 = r0.ttl_seconds
            if (r0 == 0) goto L_0x0546
            r0 = 2131624450(0x7f0e0202, float:1.887608E38)
            java.lang.String r7 = "AttachVideoExpired"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r7, r0)
            r7 = r22
            goto L_0x060b
        L_0x0546:
            java.lang.CharSequence r0 = r4.caption
            if (r0 == 0) goto L_0x0585
            boolean r0 = r4.isVideo()
            if (r0 == 0) goto L_0x0553
            java.lang.String r0 = "📹 "
            goto L_0x0570
        L_0x0553:
            boolean r0 = r4.isVoice()
            if (r0 == 0) goto L_0x055c
            java.lang.String r0 = "🎤 "
            goto L_0x0570
        L_0x055c:
            boolean r0 = r4.isMusic()
            if (r0 == 0) goto L_0x0565
            java.lang.String r0 = "🎧 "
            goto L_0x0570
        L_0x0565:
            boolean r0 = r4.isPhoto()
            if (r0 == 0) goto L_0x056e
            java.lang.String r0 = "🖼 "
            goto L_0x0570
        L_0x056e:
            java.lang.String r0 = "📎 "
        L_0x0570:
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            r7.append(r0)
            java.lang.CharSequence r8 = r4.caption
            r7.append(r8)
            java.lang.String r0 = r7.toString()
            r7 = r22
            goto L_0x060b
        L_0x0585:
            org.telegram.tgnet.TLRPC$Message r0 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll
            if (r0 == 0) goto L_0x05a9
            org.telegram.tgnet.TLRPC$Message r0 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r0 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r0
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r8 = "📊 "
            r7.append(r8)
            org.telegram.tgnet.TLRPC$Poll r8 = r0.poll
            java.lang.String r8 = r8.question
            r7.append(r8)
            java.lang.String r0 = r7.toString()
            goto L_0x05ef
        L_0x05a9:
            org.telegram.tgnet.TLRPC$Message r0 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame
            if (r0 == 0) goto L_0x05cb
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r7 = "🎮 "
            r0.append(r7)
            org.telegram.tgnet.TLRPC$Message r7 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r7 = r7.media
            org.telegram.tgnet.TLRPC$TL_game r7 = r7.game
            java.lang.String r7 = r7.title
            r0.append(r7)
            java.lang.String r0 = r0.toString()
            goto L_0x05ef
        L_0x05cb:
            int r0 = r4.type
            r8 = 14
            if (r0 != r8) goto L_0x05e7
            r0 = 2
            java.lang.Object[] r0 = new java.lang.Object[r0]
            java.lang.String r8 = r4.getMusicAuthor()
            r9 = 0
            r0[r9] = r8
            java.lang.String r8 = r4.getMusicTitle()
            r9 = 1
            r0[r9] = r8
            java.lang.String r0 = java.lang.String.format(r7, r0)
            goto L_0x05ef
        L_0x05e7:
            java.lang.CharSequence r0 = r4.messageText
            java.util.ArrayList<java.lang.String> r7 = r4.highlightedWords
            r8 = 0
            org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r0, (java.util.ArrayList<java.lang.String>) r7, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r8)
        L_0x05ef:
            org.telegram.tgnet.TLRPC$Message r7 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r7 = r7.media
            if (r7 == 0) goto L_0x0609
            boolean r7 = r4.isMediaEmpty()
            if (r7 != 0) goto L_0x0609
            android.content.Context r7 = r1.mContext
            android.content.res.Resources r7 = r7.getResources()
            r8 = 2131034141(0x7var_d, float:1.7678791E38)
            int r7 = r7.getColor(r8)
            goto L_0x060b
        L_0x0609:
            r7 = r22
        L_0x060b:
            org.telegram.tgnet.TLRPC$Message r8 = r4.messageOwner
            int r8 = r8.date
            long r8 = (long) r8
            java.lang.String r8 = org.telegram.messenger.LocaleController.stringForMessageListDate(r8)
            r9 = 2131230915(0x7var_c3, float:1.8077896E38)
            r10.setTextViewText(r9, r8)
            java.lang.String r8 = r0.toString()
            r9 = 2131230913(0x7var_c1, float:1.8077892E38)
            r10.setTextViewText(r9, r8)
            r10.setTextColor(r9, r7)
            goto L_0x064c
        L_0x0628:
            r24 = r8
            r26 = r9
            if (r6 == 0) goto L_0x0640
            int r7 = r6.last_message_date
            if (r7 == 0) goto L_0x0640
            int r7 = r6.last_message_date
            long r7 = (long) r7
            java.lang.String r7 = org.telegram.messenger.LocaleController.stringForMessageListDate(r7)
            r8 = 2131230915(0x7var_c3, float:1.8077896E38)
            r10.setTextViewText(r8, r7)
            goto L_0x0646
        L_0x0640:
            r8 = 2131230915(0x7var_c3, float:1.8077896E38)
            r10.setTextViewText(r8, r0)
        L_0x0646:
            r7 = 2131230913(0x7var_c1, float:1.8077892E38)
            r10.setTextViewText(r7, r0)
        L_0x064c:
            r0 = 8
            r7 = 2131230911(0x7var_bf, float:1.8077888E38)
            if (r6 == 0) goto L_0x0696
            int r8 = r6.unread_count
            if (r8 <= 0) goto L_0x0696
            r8 = 1
            java.lang.Object[] r9 = new java.lang.Object[r8]
            int r8 = r6.unread_count
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            r11 = 0
            r9[r11] = r8
            java.lang.String r8 = "%d"
            java.lang.String r8 = java.lang.String.format(r8, r9)
            r10.setTextViewText(r7, r8)
            r10.setViewVisibility(r7, r11)
            org.telegram.messenger.AccountInstance r8 = r1.accountInstance
            org.telegram.messenger.MessagesController r8 = r8.getMessagesController()
            long r12 = r6.id
            boolean r8 = r8.isDialogMuted(r12)
            java.lang.String r9 = "setBackgroundResource"
            java.lang.String r12 = "setEnabled"
            if (r8 == 0) goto L_0x068b
            r10.setBoolean(r7, r12, r11)
            r8 = 2131166262(0x7var_, float:1.7946764E38)
            r10.setInt(r7, r9, r8)
            goto L_0x069a
        L_0x068b:
            r8 = 1
            r10.setBoolean(r7, r12, r8)
            r8 = 2131166261(0x7var_, float:1.7946762E38)
            r10.setInt(r7, r9, r8)
            goto L_0x069a
        L_0x0696:
            r11 = 0
            r10.setViewVisibility(r7, r0)
        L_0x069a:
            android.os.Bundle r7 = new android.os.Bundle
            r7.<init>()
            long r8 = r5.longValue()
            boolean r8 = org.telegram.messenger.DialogObject.isUserDialog(r8)
            if (r8 == 0) goto L_0x06b3
            long r8 = r5.longValue()
            java.lang.String r12 = "userId"
            r7.putLong(r12, r8)
            goto L_0x06bd
        L_0x06b3:
            long r8 = r5.longValue()
            long r8 = -r8
            java.lang.String r12 = "chatId"
            r7.putLong(r12, r8)
        L_0x06bd:
            org.telegram.messenger.AccountInstance r8 = r1.accountInstance
            int r8 = r8.getCurrentAccount()
            r7.putInt(r3, r8)
            android.content.Intent r3 = new android.content.Intent
            r3.<init>()
            r3.putExtras(r7)
            r8 = 2131230909(0x7var_bd, float:1.8077884E38)
            r10.setOnClickFillInIntent(r8, r3)
            r8 = 2131230912(0x7var_c0, float:1.807789E38)
            int r9 = r30.getCount()
            if (r2 != r9) goto L_0x06de
            goto L_0x06df
        L_0x06de:
            r0 = 0
        L_0x06df:
            r10.setViewVisibility(r8, r0)
            return r10
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ChatsRemoteViewsFactory.getViewAt(int):android.widget.RemoteViews");
    }

    public RemoteViews getLoadingView() {
        return null;
    }

    public int getViewTypeCount() {
        return 2;
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public boolean hasStableIds() {
        return true;
    }

    public void onDataSetChanged() {
        this.dids.clear();
        this.messageObjects.clear();
        AccountInstance accountInstance2 = this.accountInstance;
        if (accountInstance2 != null && accountInstance2.getUserConfig().isClientActivated()) {
            ArrayList<TLRPC.User> users = new ArrayList<>();
            ArrayList<TLRPC.Chat> chats = new ArrayList<>();
            LongSparseArray<TLRPC.Message> messages = new LongSparseArray<>();
            this.accountInstance.getMessagesStorage().getWidgetDialogs(this.appWidgetId, 0, this.dids, this.dialogs, messages, users, chats);
            this.accountInstance.getMessagesController().putUsers(users, true);
            this.accountInstance.getMessagesController().putChats(chats, true);
            this.messageObjects.clear();
            int N = messages.size();
            for (int a = 0; a < N; a++) {
                this.messageObjects.put(messages.keyAt(a), new MessageObject(this.accountInstance.getCurrentAccount(), messages.valueAt(a), (LongSparseArray<TLRPC.User>) null, (LongSparseArray<TLRPC.Chat>) null, false, true));
            }
        }
    }
}
