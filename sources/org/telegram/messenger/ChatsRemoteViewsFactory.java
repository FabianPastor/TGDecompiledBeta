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

    /* JADX WARNING: Code restructure failed: missing block: B:87:0x0267, code lost:
        if ((r0 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelMigrateFrom) == false) goto L_0x026a;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.widget.RemoteViews getViewAt(int r21) {
        /*
            r20 = this;
            r1 = r20
            r2 = r21
            boolean r0 = r1.deleted
            if (r0 == 0) goto L_0x0026
            android.widget.RemoteViews r0 = new android.widget.RemoteViews
            android.content.Context r2 = r1.mContext
            java.lang.String r2 = r2.getPackageName()
            r3 = 2131427356(0x7f0b001c, float:1.8476326E38)
            r0.<init>(r2, r3)
            r2 = 2131230941(0x7var_dd, float:1.8077949E38)
            r3 = 2131628463(0x7f0e11af, float:1.888422E38)
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
            r2 = 2131230943(0x7var_df, float:1.8077953E38)
            r5 = 2131627856(0x7f0e0var_, float:1.8882988E38)
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
            r2 = 2131230942(0x7var_de, float:1.807795E38)
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
            if (r0 <= 0) goto L_0x0102
            org.telegram.messenger.AccountInstance r0 = r1.accountInstance
            org.telegram.messenger.MessagesController r0 = r0.getMessagesController()
            long r6 = r5.longValue()
            int r7 = (int) r6
            java.lang.Integer r6 = java.lang.Integer.valueOf(r7)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r6)
            if (r0 == 0) goto L_0x00fe
            boolean r6 = org.telegram.messenger.UserObject.isUserSelf(r0)
            if (r6 == 0) goto L_0x00b2
            r6 = 2131627438(0x7f0e0dae, float:1.888214E38)
            java.lang.String r7 = "SavedMessages"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            goto L_0x00da
        L_0x00b2:
            boolean r6 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC$User) r0)
            if (r6 == 0) goto L_0x00c2
            r6 = 2131627319(0x7f0e0d37, float:1.88819E38)
            java.lang.String r7 = "RepliesTitle"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            goto L_0x00da
        L_0x00c2:
            boolean r6 = org.telegram.messenger.UserObject.isDeleted(r0)
            if (r6 == 0) goto L_0x00d2
            r6 = 2131625800(0x7f0e0748, float:1.8878818E38)
            java.lang.String r7 = "HiddenName"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            goto L_0x00da
        L_0x00d2:
            java.lang.String r6 = r0.first_name
            java.lang.String r7 = r0.last_name
            java.lang.String r6 = org.telegram.messenger.ContactsController.formatName(r6, r7)
        L_0x00da:
            boolean r7 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC$User) r0)
            if (r7 != 0) goto L_0x00fc
            boolean r7 = org.telegram.messenger.UserObject.isUserSelf(r0)
            if (r7 != 0) goto L_0x00fc
            org.telegram.tgnet.TLRPC$UserProfilePhoto r7 = r0.photo
            if (r7 == 0) goto L_0x00fc
            org.telegram.tgnet.TLRPC$FileLocation r7 = r7.photo_small
            if (r7 == 0) goto L_0x00fc
            long r12 = r7.volume_id
            int r14 = (r12 > r8 ? 1 : (r12 == r8 ? 0 : -1))
            if (r14 == 0) goto L_0x00fc
            int r12 = r7.local_id
            if (r12 == 0) goto L_0x00fc
            r12 = r7
            r7 = r6
            r6 = r11
            goto L_0x013a
        L_0x00fc:
            r7 = r6
            goto L_0x00ff
        L_0x00fe:
            r7 = r10
        L_0x00ff:
            r6 = r11
            r12 = r6
            goto L_0x013a
        L_0x0102:
            org.telegram.messenger.AccountInstance r0 = r1.accountInstance
            org.telegram.messenger.MessagesController r0 = r0.getMessagesController()
            long r6 = r5.longValue()
            int r7 = (int) r6
            int r6 = -r7
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r6)
            if (r0 == 0) goto L_0x0136
            java.lang.String r6 = r0.title
            org.telegram.tgnet.TLRPC$ChatPhoto r7 = r0.photo
            if (r7 == 0) goto L_0x0131
            org.telegram.tgnet.TLRPC$FileLocation r7 = r7.photo_small
            if (r7 == 0) goto L_0x0131
            long r12 = r7.volume_id
            int r14 = (r12 > r8 ? 1 : (r12 == r8 ? 0 : -1))
            if (r14 == 0) goto L_0x0131
            int r12 = r7.local_id
            if (r12 == 0) goto L_0x0131
            r12 = r7
            r7 = r6
            r6 = r0
            r0 = r11
            goto L_0x013a
        L_0x0131:
            r7 = r6
            r12 = r11
            r6 = r0
            r0 = r12
            goto L_0x013a
        L_0x0136:
            r6 = r0
            r7 = r10
            r0 = r11
            r12 = r0
        L_0x013a:
            android.widget.RemoteViews r13 = new android.widget.RemoteViews
            android.content.Context r14 = r1.mContext
            java.lang.String r14 = r14.getPackageName()
            r15 = 2131427349(0x7f0b0015, float:1.8476312E38)
            r13.<init>(r14, r15)
            r14 = 2131230908(0x7var_bc, float:1.8077882E38)
            r13.setTextViewText(r14, r7)
            r7 = 1
            if (r12 == 0) goto L_0x015e
            java.io.File r12 = org.telegram.messenger.FileLoader.getPathToAttach(r12, r7)     // Catch:{ all -> 0x01f8 }
            java.lang.String r12 = r12.toString()     // Catch:{ all -> 0x01f8 }
            android.graphics.Bitmap r12 = android.graphics.BitmapFactory.decodeFile(r12)     // Catch:{ all -> 0x01f8 }
            goto L_0x015f
        L_0x015e:
            r12 = r11
        L_0x015f:
            r14 = 1111490560(0x42400000, float:48.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)     // Catch:{ all -> 0x01f8 }
            android.graphics.Bitmap$Config r15 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x01f8 }
            android.graphics.Bitmap r15 = android.graphics.Bitmap.createBitmap(r14, r14, r15)     // Catch:{ all -> 0x01f8 }
            r15.eraseColor(r4)     // Catch:{ all -> 0x01f8 }
            android.graphics.Canvas r8 = new android.graphics.Canvas     // Catch:{ all -> 0x01f8 }
            r8.<init>(r15)     // Catch:{ all -> 0x01f8 }
            if (r12 != 0) goto L_0x019e
            if (r0 == 0) goto L_0x0192
            org.telegram.ui.Components.AvatarDrawable r9 = new org.telegram.ui.Components.AvatarDrawable     // Catch:{ all -> 0x01f8 }
            r9.<init>((org.telegram.tgnet.TLRPC$User) r0)     // Catch:{ all -> 0x01f8 }
            boolean r12 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC$User) r0)     // Catch:{ all -> 0x01f8 }
            if (r12 == 0) goto L_0x0188
            r0 = 12
            r9.setAvatarType(r0)     // Catch:{ all -> 0x01f8 }
            goto L_0x0197
        L_0x0188:
            boolean r0 = org.telegram.messenger.UserObject.isUserSelf(r0)     // Catch:{ all -> 0x01f8 }
            if (r0 == 0) goto L_0x0197
            r9.setAvatarType(r7)     // Catch:{ all -> 0x01f8 }
            goto L_0x0197
        L_0x0192:
            org.telegram.ui.Components.AvatarDrawable r9 = new org.telegram.ui.Components.AvatarDrawable     // Catch:{ all -> 0x01f8 }
            r9.<init>((org.telegram.tgnet.TLRPC$Chat) r6)     // Catch:{ all -> 0x01f8 }
        L_0x0197:
            r9.setBounds(r4, r4, r14, r14)     // Catch:{ all -> 0x01f8 }
            r9.draw(r8)     // Catch:{ all -> 0x01f8 }
            goto L_0x01ee
        L_0x019e:
            android.graphics.BitmapShader r0 = new android.graphics.BitmapShader     // Catch:{ all -> 0x01f8 }
            android.graphics.Shader$TileMode r9 = android.graphics.Shader.TileMode.CLAMP     // Catch:{ all -> 0x01f8 }
            r0.<init>(r12, r9, r9)     // Catch:{ all -> 0x01f8 }
            android.graphics.Paint r9 = r1.roundPaint     // Catch:{ all -> 0x01f8 }
            if (r9 != 0) goto L_0x01b7
            android.graphics.Paint r9 = new android.graphics.Paint     // Catch:{ all -> 0x01f8 }
            r9.<init>(r7)     // Catch:{ all -> 0x01f8 }
            r1.roundPaint = r9     // Catch:{ all -> 0x01f8 }
            android.graphics.RectF r9 = new android.graphics.RectF     // Catch:{ all -> 0x01f8 }
            r9.<init>()     // Catch:{ all -> 0x01f8 }
            r1.bitmapRect = r9     // Catch:{ all -> 0x01f8 }
        L_0x01b7:
            float r9 = (float) r14     // Catch:{ all -> 0x01f8 }
            int r14 = r12.getWidth()     // Catch:{ all -> 0x01f8 }
            float r14 = (float) r14     // Catch:{ all -> 0x01f8 }
            float r9 = r9 / r14
            r8.save()     // Catch:{ all -> 0x01f8 }
            r8.scale(r9, r9)     // Catch:{ all -> 0x01f8 }
            android.graphics.Paint r9 = r1.roundPaint     // Catch:{ all -> 0x01f8 }
            r9.setShader(r0)     // Catch:{ all -> 0x01f8 }
            android.graphics.RectF r0 = r1.bitmapRect     // Catch:{ all -> 0x01f8 }
            r9 = 0
            r14 = 0
            int r7 = r12.getWidth()     // Catch:{ all -> 0x01f8 }
            float r7 = (float) r7     // Catch:{ all -> 0x01f8 }
            int r4 = r12.getHeight()     // Catch:{ all -> 0x01f8 }
            float r4 = (float) r4     // Catch:{ all -> 0x01f8 }
            r0.set(r9, r14, r7, r4)     // Catch:{ all -> 0x01f8 }
            android.graphics.RectF r0 = r1.bitmapRect     // Catch:{ all -> 0x01f8 }
            int r4 = r12.getWidth()     // Catch:{ all -> 0x01f8 }
            float r4 = (float) r4     // Catch:{ all -> 0x01f8 }
            int r7 = r12.getHeight()     // Catch:{ all -> 0x01f8 }
            float r7 = (float) r7     // Catch:{ all -> 0x01f8 }
            android.graphics.Paint r9 = r1.roundPaint     // Catch:{ all -> 0x01f8 }
            r8.drawRoundRect(r0, r4, r7, r9)     // Catch:{ all -> 0x01f8 }
            r8.restore()     // Catch:{ all -> 0x01f8 }
        L_0x01ee:
            r8.setBitmap(r11)     // Catch:{ all -> 0x01f8 }
            r0 = 2131230904(0x7var_b8, float:1.8077874E38)
            r13.setImageViewBitmap(r0, r15)     // Catch:{ all -> 0x01f8 }
            goto L_0x01fc
        L_0x01f8:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x01fc:
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
            if (r4 == 0) goto L_0x055c
            int r0 = r4.getFromChatId()
            if (r0 <= 0) goto L_0x022d
            org.telegram.messenger.AccountInstance r12 = r1.accountInstance
            org.telegram.messenger.MessagesController r12 = r12.getMessagesController()
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$User r0 = r12.getUser(r0)
            goto L_0x0241
        L_0x022d:
            org.telegram.messenger.AccountInstance r12 = r1.accountInstance
            org.telegram.messenger.MessagesController r12 = r12.getMessagesController()
            int r0 = -r0
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$Chat r0 = r12.getChat(r0)
            r19 = r11
            r11 = r0
            r0 = r19
        L_0x0241:
            android.content.Context r12 = r1.mContext
            android.content.res.Resources r12 = r12.getResources()
            r14 = 2131034146(0x7var_, float:1.7678801E38)
            int r12 = r12.getColor(r14)
            org.telegram.tgnet.TLRPC$Message r14 = r4.messageOwner
            boolean r14 = r14 instanceof org.telegram.tgnet.TLRPC$TL_messageService
            r15 = 2131034141(0x7var_d, float:1.7678791E38)
            if (r14 == 0) goto L_0x0278
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r6)
            if (r0 == 0) goto L_0x026a
            org.telegram.tgnet.TLRPC$Message r0 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r0 = r0.action
            boolean r6 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageActionHistoryClear
            if (r6 != 0) goto L_0x026c
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelMigrateFrom
            if (r0 == 0) goto L_0x026a
            goto L_0x026c
        L_0x026a:
            java.lang.CharSequence r10 = r4.messageText
        L_0x026c:
            android.content.Context r0 = r1.mContext
            android.content.res.Resources r0 = r0.getResources()
            int r12 = r0.getColor(r15)
            goto L_0x053f
        L_0x0278:
            java.lang.String r14 = "🎧 %s - %s"
            java.lang.String r17 = "🎤 "
            java.lang.String r18 = "📹 "
            r9 = 2
            if (r6 == 0) goto L_0x0454
            int r8 = r6.id
            if (r8 <= 0) goto L_0x0454
            if (r11 != 0) goto L_0x0454
            boolean r8 = org.telegram.messenger.ChatObject.isChannel(r6)
            if (r8 == 0) goto L_0x0293
            boolean r6 = org.telegram.messenger.ChatObject.isMegagroup(r6)
            if (r6 == 0) goto L_0x0454
        L_0x0293:
            boolean r6 = r4.isOutOwner()
            if (r6 == 0) goto L_0x02a4
            r0 = 2131625710(0x7f0e06ee, float:1.8878636E38)
            java.lang.String r6 = "FromYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r6, r0)
        L_0x02a2:
            r6 = r0
            goto L_0x02b4
        L_0x02a4:
            if (r0 == 0) goto L_0x02b1
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            java.lang.String r6 = "\n"
            java.lang.String r0 = r0.replace(r6, r10)
            goto L_0x02a2
        L_0x02b1:
            java.lang.String r0 = "DELETED"
            goto L_0x02a2
        L_0x02b4:
            java.lang.String r0 = "%2$s: ⁨%1$s⁩"
            java.lang.CharSequence r8 = r4.caption
            r15 = 10
            r11 = 150(0x96, float:2.1E-43)
            if (r8 == 0) goto L_0x031b
            java.lang.String r8 = r8.toString()
            int r10 = r8.length()
            if (r10 <= r11) goto L_0x02cd
            r10 = 0
            java.lang.String r8 = r8.substring(r10, r11)
        L_0x02cd:
            boolean r10 = r4.isVideo()
            if (r10 == 0) goto L_0x02d6
            r10 = r18
            goto L_0x02f4
        L_0x02d6:
            boolean r10 = r4.isVoice()
            if (r10 == 0) goto L_0x02df
        L_0x02dc:
            r10 = r17
            goto L_0x02f4
        L_0x02df:
            boolean r10 = r4.isMusic()
            if (r10 == 0) goto L_0x02e8
            java.lang.String r17 = "🎧 "
            goto L_0x02dc
        L_0x02e8:
            boolean r10 = r4.isPhoto()
            if (r10 == 0) goto L_0x02f1
            java.lang.String r17 = "🖼 "
            goto L_0x02dc
        L_0x02f1:
            java.lang.String r17 = "📎 "
            goto L_0x02dc
        L_0x02f4:
            java.lang.Object[] r9 = new java.lang.Object[r9]
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            r11.append(r10)
            r10 = 32
            java.lang.String r8 = r8.replace(r15, r10)
            r11.append(r8)
            java.lang.String r8 = r11.toString()
            r10 = 0
            r9[r10] = r8
            r8 = 1
            r9[r8] = r6
            java.lang.String r0 = java.lang.String.format(r0, r9)
            android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r0)
            goto L_0x0438
        L_0x031b:
            org.telegram.tgnet.TLRPC$Message r8 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r8 = r8.media
            if (r8 == 0) goto L_0x0407
            boolean r8 = r4.isMediaEmpty()
            if (r8 != 0) goto L_0x0407
            android.content.Context r8 = r1.mContext
            android.content.res.Resources r8 = r8.getResources()
            r10 = 2131034141(0x7var_d, float:1.7678791E38)
            int r8 = r8.getColor(r10)
            org.telegram.tgnet.TLRPC$Message r10 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r10 = r10.media
            boolean r11 = r10 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            r12 = 18
            if (r11 == 0) goto L_0x036b
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r10 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r10
            int r11 = android.os.Build.VERSION.SDK_INT
            if (r11 < r12) goto L_0x0355
            r11 = 1
            java.lang.Object[] r12 = new java.lang.Object[r11]
            org.telegram.tgnet.TLRPC$Poll r10 = r10.poll
            java.lang.String r10 = r10.question
            r14 = 0
            r12[r14] = r10
            java.lang.String r10 = "📊 ⁨%s⁩"
            java.lang.String r10 = java.lang.String.format(r10, r12)
            goto L_0x0365
        L_0x0355:
            r11 = 1
            r14 = 0
            java.lang.Object[] r12 = new java.lang.Object[r11]
            org.telegram.tgnet.TLRPC$Poll r10 = r10.poll
            java.lang.String r10 = r10.question
            r12[r14] = r10
            java.lang.String r10 = "📊 %s"
            java.lang.String r10 = java.lang.String.format(r10, r12)
        L_0x0365:
            r11 = 32
            r12 = 1
            r16 = 0
            goto L_0x03d9
        L_0x036b:
            boolean r11 = r10 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r11 == 0) goto L_0x039a
            int r11 = android.os.Build.VERSION.SDK_INT
            if (r11 < r12) goto L_0x0385
            r11 = 1
            java.lang.Object[] r12 = new java.lang.Object[r11]
            org.telegram.tgnet.TLRPC$TL_game r10 = r10.game
            java.lang.String r10 = r10.title
            r16 = 0
            r12[r16] = r10
            java.lang.String r10 = "🎮 ⁨%s⁩"
            java.lang.String r10 = java.lang.String.format(r10, r12)
            goto L_0x0396
        L_0x0385:
            r11 = 1
            r16 = 0
            java.lang.Object[] r12 = new java.lang.Object[r11]
            org.telegram.tgnet.TLRPC$TL_game r10 = r10.game
            java.lang.String r10 = r10.title
            r12[r16] = r10
            java.lang.String r10 = "🎮 %s"
            java.lang.String r10 = java.lang.String.format(r10, r12)
        L_0x0396:
            r11 = 32
            r12 = 1
            goto L_0x03d9
        L_0x039a:
            r16 = 0
            int r10 = r4.type
            r11 = 14
            if (r10 != r11) goto L_0x03d0
            int r10 = android.os.Build.VERSION.SDK_INT
            if (r10 < r12) goto L_0x03bc
            java.lang.Object[] r10 = new java.lang.Object[r9]
            java.lang.String r11 = r4.getMusicAuthor()
            r10[r16] = r11
            java.lang.String r11 = r4.getMusicTitle()
            r12 = 1
            r10[r12] = r11
            java.lang.String r11 = "🎧 ⁨%s - %s⁩"
            java.lang.String r10 = java.lang.String.format(r11, r10)
            goto L_0x03d7
        L_0x03bc:
            r12 = 1
            java.lang.Object[] r10 = new java.lang.Object[r9]
            java.lang.String r11 = r4.getMusicAuthor()
            r10[r16] = r11
            java.lang.String r11 = r4.getMusicTitle()
            r10[r12] = r11
            java.lang.String r10 = java.lang.String.format(r14, r10)
            goto L_0x03d7
        L_0x03d0:
            r12 = 1
            java.lang.CharSequence r10 = r4.messageText
            java.lang.String r10 = r10.toString()
        L_0x03d7:
            r11 = 32
        L_0x03d9:
            java.lang.String r10 = r10.replace(r15, r11)
            java.lang.Object[] r11 = new java.lang.Object[r9]
            r11[r16] = r10
            r11[r12] = r6
            java.lang.String r0 = java.lang.String.format(r0, r11)
            android.text.SpannableStringBuilder r10 = android.text.SpannableStringBuilder.valueOf(r0)
            org.telegram.ui.Components.ForegroundColorSpanThemable r0 = new org.telegram.ui.Components.ForegroundColorSpanThemable     // Catch:{ Exception -> 0x0401 }
            java.lang.String r11 = "chats_attachMessage"
            r0.<init>(r11)     // Catch:{ Exception -> 0x0401 }
            int r11 = r6.length()     // Catch:{ Exception -> 0x0401 }
            int r11 = r11 + r9
            int r9 = r10.length()     // Catch:{ Exception -> 0x0401 }
            r12 = 33
            r10.setSpan(r0, r11, r9, r12)     // Catch:{ Exception -> 0x0401 }
            goto L_0x0405
        L_0x0401:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0405:
            r12 = r8
            goto L_0x0439
        L_0x0407:
            org.telegram.tgnet.TLRPC$Message r8 = r4.messageOwner
            java.lang.String r8 = r8.message
            if (r8 == 0) goto L_0x0434
            int r10 = r8.length()
            if (r10 <= r11) goto L_0x0419
            r10 = 0
            java.lang.String r8 = r8.substring(r10, r11)
            goto L_0x041a
        L_0x0419:
            r10 = 0
        L_0x041a:
            r11 = 32
            java.lang.String r8 = r8.replace(r15, r11)
            java.lang.String r8 = r8.trim()
            java.lang.Object[] r9 = new java.lang.Object[r9]
            r9[r10] = r8
            r8 = 1
            r9[r8] = r6
            java.lang.String r0 = java.lang.String.format(r0, r9)
            android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r0)
            goto L_0x0438
        L_0x0434:
            android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r10)
        L_0x0438:
            r10 = r0
        L_0x0439:
            org.telegram.ui.Components.ForegroundColorSpanThemable r0 = new org.telegram.ui.Components.ForegroundColorSpanThemable     // Catch:{ Exception -> 0x044e }
            java.lang.String r8 = "chats_nameMessage"
            r0.<init>(r8)     // Catch:{ Exception -> 0x044e }
            int r6 = r6.length()     // Catch:{ Exception -> 0x044e }
            r8 = 1
            int r6 = r6 + r8
            r8 = 33
            r9 = 0
            r10.setSpan(r0, r9, r6, r8)     // Catch:{ Exception -> 0x044e }
            goto L_0x053f
        L_0x044e:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x053f
        L_0x0454:
            org.telegram.tgnet.TLRPC$Message r0 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r6 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r6 == 0) goto L_0x0471
            org.telegram.tgnet.TLRPC$Photo r6 = r0.photo
            boolean r6 = r6 instanceof org.telegram.tgnet.TLRPC$TL_photoEmpty
            if (r6 == 0) goto L_0x0471
            int r6 = r0.ttl_seconds
            if (r6 == 0) goto L_0x0471
            r0 = 2131624403(0x7f0e01d3, float:1.8875985E38)
            java.lang.String r6 = "AttachPhotoExpired"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r6, r0)
            goto L_0x053f
        L_0x0471:
            boolean r6 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r6 == 0) goto L_0x048a
            org.telegram.tgnet.TLRPC$Document r6 = r0.document
            boolean r6 = r6 instanceof org.telegram.tgnet.TLRPC$TL_documentEmpty
            if (r6 == 0) goto L_0x048a
            int r6 = r0.ttl_seconds
            if (r6 == 0) goto L_0x048a
            r0 = 2131624409(0x7f0e01d9, float:1.8875997E38)
            java.lang.String r6 = "AttachVideoExpired"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r6, r0)
            goto L_0x053f
        L_0x048a:
            java.lang.CharSequence r6 = r4.caption
            if (r6 == 0) goto L_0x04c8
            boolean r0 = r4.isVideo()
            if (r0 == 0) goto L_0x0497
            r0 = r18
            goto L_0x04b5
        L_0x0497:
            boolean r0 = r4.isVoice()
            if (r0 == 0) goto L_0x04a0
        L_0x049d:
            r0 = r17
            goto L_0x04b5
        L_0x04a0:
            boolean r0 = r4.isMusic()
            if (r0 == 0) goto L_0x04a9
            java.lang.String r17 = "🎧 "
            goto L_0x049d
        L_0x04a9:
            boolean r0 = r4.isPhoto()
            if (r0 == 0) goto L_0x04b2
            java.lang.String r17 = "🖼 "
            goto L_0x049d
        L_0x04b2:
            java.lang.String r17 = "📎 "
            goto L_0x049d
        L_0x04b5:
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r0)
            java.lang.CharSequence r0 = r4.caption
            r6.append(r0)
            java.lang.String r10 = r6.toString()
            goto L_0x053f
        L_0x04c8:
            boolean r6 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r6 == 0) goto L_0x04e5
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r0 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r0
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r8 = "📊 "
            r6.append(r8)
            org.telegram.tgnet.TLRPC$Poll r0 = r0.poll
            java.lang.String r0 = r0.question
            r6.append(r0)
            java.lang.String r0 = r6.toString()
        L_0x04e3:
            r10 = r0
            goto L_0x0526
        L_0x04e5:
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r0 == 0) goto L_0x0503
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
            goto L_0x04e3
        L_0x0503:
            int r0 = r4.type
            r6 = 14
            if (r0 != r6) goto L_0x051e
            java.lang.Object[] r0 = new java.lang.Object[r9]
            java.lang.String r6 = r4.getMusicAuthor()
            r8 = 0
            r0[r8] = r6
            java.lang.String r6 = r4.getMusicTitle()
            r8 = 1
            r0[r8] = r6
            java.lang.String r0 = java.lang.String.format(r14, r0)
            goto L_0x04e3
        L_0x051e:
            java.lang.CharSequence r0 = r4.messageText
            java.util.ArrayList<java.lang.String> r6 = r4.highlightedWords
            org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r0, (java.util.ArrayList<java.lang.String>) r6)
            goto L_0x04e3
        L_0x0526:
            org.telegram.tgnet.TLRPC$Message r0 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            if (r0 == 0) goto L_0x053f
            boolean r0 = r4.isMediaEmpty()
            if (r0 != 0) goto L_0x053f
            android.content.Context r0 = r1.mContext
            android.content.res.Resources r0 = r0.getResources()
            r6 = 2131034141(0x7var_d, float:1.7678791E38)
            int r12 = r0.getColor(r6)
        L_0x053f:
            org.telegram.tgnet.TLRPC$Message r0 = r4.messageOwner
            int r0 = r0.date
            long r8 = (long) r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.stringForMessageListDate(r8)
            r4 = 2131230909(0x7var_bd, float:1.8077884E38)
            r13.setTextViewText(r4, r0)
            java.lang.String r0 = r10.toString()
            r4 = 2131230907(0x7var_bb, float:1.807788E38)
            r13.setTextViewText(r4, r0)
            r13.setTextColor(r4, r12)
            goto L_0x0577
        L_0x055c:
            r4 = 2131230909(0x7var_bd, float:1.8077884E38)
            if (r7 == 0) goto L_0x056e
            int r0 = r7.last_message_date
            if (r0 == 0) goto L_0x056e
            long r8 = (long) r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.stringForMessageListDate(r8)
            r13.setTextViewText(r4, r0)
            goto L_0x0571
        L_0x056e:
            r13.setTextViewText(r4, r10)
        L_0x0571:
            r4 = 2131230907(0x7var_bb, float:1.807788E38)
            r13.setTextViewText(r4, r10)
        L_0x0577:
            r0 = 8
            r4 = 2131230905(0x7var_b9, float:1.8077876E38)
            if (r7 == 0) goto L_0x05bf
            int r6 = r7.unread_count
            if (r6 <= 0) goto L_0x05bf
            r8 = 1
            java.lang.Object[] r9 = new java.lang.Object[r8]
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            r8 = 0
            r9[r8] = r6
            java.lang.String r6 = "%d"
            java.lang.String r6 = java.lang.String.format(r6, r9)
            r13.setTextViewText(r4, r6)
            r13.setViewVisibility(r4, r8)
            org.telegram.messenger.AccountInstance r6 = r1.accountInstance
            org.telegram.messenger.MessagesController r6 = r6.getMessagesController()
            long r9 = r7.id
            boolean r6 = r6.isDialogMuted(r9)
            java.lang.String r7 = "setBackgroundResource"
            java.lang.String r9 = "setEnabled"
            if (r6 == 0) goto L_0x05b4
            r13.setBoolean(r4, r9, r8)
            r6 = 2131166173(0x7var_dd, float:1.7946584E38)
            r13.setInt(r4, r7, r6)
            goto L_0x05c3
        L_0x05b4:
            r6 = 1
            r13.setBoolean(r4, r9, r6)
            r6 = 2131166172(0x7var_dc, float:1.7946582E38)
            r13.setInt(r4, r7, r6)
            goto L_0x05c3
        L_0x05bf:
            r8 = 0
            r13.setViewVisibility(r4, r0)
        L_0x05c3:
            android.os.Bundle r4 = new android.os.Bundle
            r4.<init>()
            long r6 = r5.longValue()
            r9 = 0
            int r11 = (r6 > r9 ? 1 : (r6 == r9 ? 0 : -1))
            if (r11 <= 0) goto L_0x05dd
            long r5 = r5.longValue()
            int r6 = (int) r5
            java.lang.String r5 = "userId"
            r4.putInt(r5, r6)
            goto L_0x05e8
        L_0x05dd:
            long r5 = r5.longValue()
            int r6 = (int) r5
            int r5 = -r6
            java.lang.String r6 = "chatId"
            r4.putInt(r6, r5)
        L_0x05e8:
            org.telegram.messenger.AccountInstance r5 = r1.accountInstance
            int r5 = r5.getCurrentAccount()
            r4.putInt(r3, r5)
            android.content.Intent r3 = new android.content.Intent
            r3.<init>()
            r3.putExtras(r4)
            r4 = 2131230903(0x7var_b7, float:1.8077872E38)
            r13.setOnClickFillInIntent(r4, r3)
            r3 = 2131230906(0x7var_ba, float:1.8077878E38)
            int r4 = r20.getCount()
            if (r2 != r4) goto L_0x060b
            r4 = 8
            goto L_0x060c
        L_0x060b:
            r4 = 0
        L_0x060c:
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
