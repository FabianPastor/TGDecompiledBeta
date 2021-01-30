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

/* compiled from: ShortcutWidgetService */
class ShortcutRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private AccountInstance accountInstance;
    private int appWidgetId;
    private RectF bitmapRect;
    private LongSparseArray<TLRPC$Dialog> dialogs = new LongSparseArray<>();
    private ArrayList<Integer> dids = new ArrayList<>();
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
        return 1;
    }

    public boolean hasStableIds() {
        return true;
    }

    public void onDestroy() {
    }

    public ShortcutRemoteViewsFactory(Context context, Intent intent) {
        this.mContext = context;
        this.appWidgetId = intent.getIntExtra("appWidgetId", 0);
        SharedPreferences sharedPreferences = context.getSharedPreferences("shortcut_widget", 0);
        int i = sharedPreferences.getInt("account" + this.appWidgetId, -1);
        if (i >= 0) {
            this.accountInstance = AccountInstance.getInstance(i);
        }
    }

    public void onCreate() {
        ApplicationLoader.postInitApplication();
    }

    public int getCount() {
        return this.dids.size();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:190:0x046e, code lost:
        if (r8.isMediaEmpty() == false) goto L_0x01c1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:69:0x01bc, code lost:
        if ((r0 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelMigrateFrom) != false) goto L_0x01c1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.widget.RemoteViews getViewAt(int r21) {
        /*
            r20 = this;
            r1 = r20
            r2 = r21
            int r3 = android.os.Build.VERSION.SDK_INT
            java.util.ArrayList<java.lang.Integer> r0 = r1.dids
            java.lang.Object r0 = r0.get(r2)
            r4 = r0
            java.lang.Integer r4 = (java.lang.Integer) r4
            int r0 = r4.intValue()
            r5 = 0
            r7 = 0
            if (r0 <= 0) goto L_0x006d
            org.telegram.messenger.AccountInstance r0 = r1.accountInstance
            org.telegram.messenger.MessagesController r0 = r0.getMessagesController()
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r4)
            boolean r8 = org.telegram.messenger.UserObject.isUserSelf(r0)
            if (r8 == 0) goto L_0x0032
            r8 = 2131627094(0x7f0e0CLASSNAME, float:1.8881443E38)
            java.lang.String r9 = "SavedMessages"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
            goto L_0x004a
        L_0x0032:
            boolean r8 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC$User) r0)
            if (r8 == 0) goto L_0x0042
            r8 = 2131626995(0x7f0e0bf3, float:1.8881242E38)
            java.lang.String r9 = "RepliesTitle"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
            goto L_0x004a
        L_0x0042:
            java.lang.String r8 = r0.first_name
            java.lang.String r9 = r0.last_name
            java.lang.String r8 = org.telegram.messenger.ContactsController.formatName(r8, r9)
        L_0x004a:
            boolean r9 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC$User) r0)
            if (r9 != 0) goto L_0x006a
            boolean r9 = org.telegram.messenger.UserObject.isUserSelf(r0)
            if (r9 != 0) goto L_0x006a
            org.telegram.tgnet.TLRPC$UserProfilePhoto r9 = r0.photo
            if (r9 == 0) goto L_0x006a
            org.telegram.tgnet.TLRPC$FileLocation r9 = r9.photo_small
            if (r9 == 0) goto L_0x006a
            long r10 = r9.volume_id
            int r12 = (r10 > r5 ? 1 : (r10 == r5 ? 0 : -1))
            if (r12 == 0) goto L_0x006a
            int r5 = r9.local_id
            if (r5 == 0) goto L_0x006a
            r5 = r7
            goto L_0x009a
        L_0x006a:
            r5 = r7
            r9 = r5
            goto L_0x009a
        L_0x006d:
            org.telegram.messenger.AccountInstance r0 = r1.accountInstance
            org.telegram.messenger.MessagesController r0 = r0.getMessagesController()
            int r8 = r4.intValue()
            int r8 = -r8
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r8)
            java.lang.String r8 = r0.title
            org.telegram.tgnet.TLRPC$ChatPhoto r9 = r0.photo
            if (r9 == 0) goto L_0x0097
            org.telegram.tgnet.TLRPC$FileLocation r9 = r9.photo_small
            if (r9 == 0) goto L_0x0097
            long r10 = r9.volume_id
            int r12 = (r10 > r5 ? 1 : (r10 == r5 ? 0 : -1))
            if (r12 == 0) goto L_0x0097
            int r5 = r9.local_id
            if (r5 == 0) goto L_0x0097
            r5 = r0
            r0 = r7
            goto L_0x009a
        L_0x0097:
            r5 = r0
            r0 = r7
            r9 = r0
        L_0x009a:
            android.widget.RemoteViews r6 = new android.widget.RemoteViews
            android.content.Context r10 = r1.mContext
            java.lang.String r10 = r10.getPackageName()
            r11 = 2131427341(0x7f0b000d, float:1.8476296E38)
            r6.<init>(r10, r11)
            r10 = 2131230896(0x7var_b0, float:1.8077858E38)
            r6.setTextViewText(r10, r8)
            r8 = 1
            r10 = 0
            if (r9 == 0) goto L_0x00bf
            java.io.File r9 = org.telegram.messenger.FileLoader.getPathToAttach(r9, r8)     // Catch:{ all -> 0x0159 }
            java.lang.String r9 = r9.toString()     // Catch:{ all -> 0x0159 }
            android.graphics.Bitmap r9 = android.graphics.BitmapFactory.decodeFile(r9)     // Catch:{ all -> 0x0159 }
            goto L_0x00c0
        L_0x00bf:
            r9 = r7
        L_0x00c0:
            r11 = 1111490560(0x42400000, float:48.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)     // Catch:{ all -> 0x0159 }
            android.graphics.Bitmap$Config r12 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x0159 }
            android.graphics.Bitmap r12 = android.graphics.Bitmap.createBitmap(r11, r11, r12)     // Catch:{ all -> 0x0159 }
            r12.eraseColor(r10)     // Catch:{ all -> 0x0159 }
            android.graphics.Canvas r13 = new android.graphics.Canvas     // Catch:{ all -> 0x0159 }
            r13.<init>(r12)     // Catch:{ all -> 0x0159 }
            if (r9 != 0) goto L_0x00ff
            if (r0 == 0) goto L_0x00f3
            org.telegram.ui.Components.AvatarDrawable r9 = new org.telegram.ui.Components.AvatarDrawable     // Catch:{ all -> 0x0159 }
            r9.<init>((org.telegram.tgnet.TLRPC$User) r0)     // Catch:{ all -> 0x0159 }
            boolean r14 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC$User) r0)     // Catch:{ all -> 0x0159 }
            if (r14 == 0) goto L_0x00e9
            r0 = 12
            r9.setAvatarType(r0)     // Catch:{ all -> 0x0159 }
            goto L_0x00f8
        L_0x00e9:
            boolean r0 = org.telegram.messenger.UserObject.isUserSelf(r0)     // Catch:{ all -> 0x0159 }
            if (r0 == 0) goto L_0x00f8
            r9.setAvatarType(r8)     // Catch:{ all -> 0x0159 }
            goto L_0x00f8
        L_0x00f3:
            org.telegram.ui.Components.AvatarDrawable r9 = new org.telegram.ui.Components.AvatarDrawable     // Catch:{ all -> 0x0159 }
            r9.<init>((org.telegram.tgnet.TLRPC$Chat) r5)     // Catch:{ all -> 0x0159 }
        L_0x00f8:
            r9.setBounds(r10, r10, r11, r11)     // Catch:{ all -> 0x0159 }
            r9.draw(r13)     // Catch:{ all -> 0x0159 }
            goto L_0x014f
        L_0x00ff:
            android.graphics.BitmapShader r0 = new android.graphics.BitmapShader     // Catch:{ all -> 0x0159 }
            android.graphics.Shader$TileMode r14 = android.graphics.Shader.TileMode.CLAMP     // Catch:{ all -> 0x0159 }
            r0.<init>(r9, r14, r14)     // Catch:{ all -> 0x0159 }
            android.graphics.Paint r14 = r1.roundPaint     // Catch:{ all -> 0x0159 }
            if (r14 != 0) goto L_0x0118
            android.graphics.Paint r14 = new android.graphics.Paint     // Catch:{ all -> 0x0159 }
            r14.<init>(r8)     // Catch:{ all -> 0x0159 }
            r1.roundPaint = r14     // Catch:{ all -> 0x0159 }
            android.graphics.RectF r14 = new android.graphics.RectF     // Catch:{ all -> 0x0159 }
            r14.<init>()     // Catch:{ all -> 0x0159 }
            r1.bitmapRect = r14     // Catch:{ all -> 0x0159 }
        L_0x0118:
            float r11 = (float) r11     // Catch:{ all -> 0x0159 }
            int r14 = r9.getWidth()     // Catch:{ all -> 0x0159 }
            float r14 = (float) r14     // Catch:{ all -> 0x0159 }
            float r11 = r11 / r14
            r13.save()     // Catch:{ all -> 0x0159 }
            r13.scale(r11, r11)     // Catch:{ all -> 0x0159 }
            android.graphics.Paint r11 = r1.roundPaint     // Catch:{ all -> 0x0159 }
            r11.setShader(r0)     // Catch:{ all -> 0x0159 }
            android.graphics.RectF r0 = r1.bitmapRect     // Catch:{ all -> 0x0159 }
            r11 = 0
            r14 = 0
            int r15 = r9.getWidth()     // Catch:{ all -> 0x0159 }
            float r15 = (float) r15     // Catch:{ all -> 0x0159 }
            int r8 = r9.getHeight()     // Catch:{ all -> 0x0159 }
            float r8 = (float) r8     // Catch:{ all -> 0x0159 }
            r0.set(r11, r14, r15, r8)     // Catch:{ all -> 0x0159 }
            android.graphics.RectF r0 = r1.bitmapRect     // Catch:{ all -> 0x0159 }
            int r8 = r9.getWidth()     // Catch:{ all -> 0x0159 }
            float r8 = (float) r8     // Catch:{ all -> 0x0159 }
            int r9 = r9.getHeight()     // Catch:{ all -> 0x0159 }
            float r9 = (float) r9     // Catch:{ all -> 0x0159 }
            android.graphics.Paint r11 = r1.roundPaint     // Catch:{ all -> 0x0159 }
            r13.drawRoundRect(r0, r8, r9, r11)     // Catch:{ all -> 0x0159 }
            r13.restore()     // Catch:{ all -> 0x0159 }
        L_0x014f:
            r13.setBitmap(r7)     // Catch:{ all -> 0x0159 }
            r0 = 2131230892(0x7var_ac, float:1.807785E38)
            r6.setImageViewBitmap(r0, r12)     // Catch:{ all -> 0x0159 }
            goto L_0x015d
        L_0x0159:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x015d:
            android.util.LongSparseArray<org.telegram.messenger.MessageObject> r0 = r1.messageObjects
            int r8 = r4.intValue()
            long r8 = (long) r8
            java.lang.Object r0 = r0.get(r8)
            r8 = r0
            org.telegram.messenger.MessageObject r8 = (org.telegram.messenger.MessageObject) r8
            android.util.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r0 = r1.dialogs
            int r9 = r4.intValue()
            long r11 = (long) r9
            java.lang.Object r0 = r0.get(r11)
            r9 = r0
            org.telegram.tgnet.TLRPC$Dialog r9 = (org.telegram.tgnet.TLRPC$Dialog) r9
            if (r8 == 0) goto L_0x048b
            int r0 = r8.getFromChatId()
            if (r0 <= 0) goto L_0x0190
            org.telegram.messenger.AccountInstance r12 = r1.accountInstance
            org.telegram.messenger.MessagesController r12 = r12.getMessagesController()
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$User r0 = r12.getUser(r0)
            goto L_0x01a4
        L_0x0190:
            org.telegram.messenger.AccountInstance r12 = r1.accountInstance
            org.telegram.messenger.MessagesController r12 = r12.getMessagesController()
            int r0 = -r0
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$Chat r0 = r12.getChat(r0)
            r19 = r7
            r7 = r0
            r0 = r19
        L_0x01a4:
            org.telegram.tgnet.TLRPC$Message r13 = r8.messageOwner
            boolean r13 = r13 instanceof org.telegram.tgnet.TLRPC$TL_messageService
            java.lang.String r14 = ""
            if (r13 == 0) goto L_0x01c6
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r5)
            if (r0 == 0) goto L_0x01bf
            org.telegram.tgnet.TLRPC$Message r0 = r8.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r0 = r0.action
            boolean r3 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageActionHistoryClear
            if (r3 != 0) goto L_0x01c1
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelMigrateFrom
            if (r0 == 0) goto L_0x01bf
            goto L_0x01c1
        L_0x01bf:
            java.lang.CharSequence r14 = r8.messageText
        L_0x01c1:
            r12 = -12812624(0xffffffffff3c7eb0, float:-2.5055266E38)
            goto L_0x0472
        L_0x01c6:
            java.lang.String r13 = "📎 "
            java.lang.String r16 = "🎧 "
            java.lang.String r12 = "🎧 %s - %s"
            java.lang.String r17 = "🎤 "
            java.lang.String r18 = "📹 "
            r11 = 2
            if (r5 == 0) goto L_0x0393
            int r15 = r5.id
            if (r15 <= 0) goto L_0x0393
            if (r7 != 0) goto L_0x0393
            boolean r7 = org.telegram.messenger.ChatObject.isChannel(r5)
            if (r7 == 0) goto L_0x01ea
            boolean r5 = org.telegram.messenger.ChatObject.isMegagroup(r5)
            if (r5 == 0) goto L_0x0393
        L_0x01ea:
            boolean r5 = r8.isOutOwner()
            if (r5 == 0) goto L_0x01fb
            r0 = 2131625582(0x7f0e066e, float:1.8878376E38)
            java.lang.String r5 = "FromYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r5, r0)
        L_0x01f9:
            r5 = r0
            goto L_0x020b
        L_0x01fb:
            if (r0 == 0) goto L_0x0208
            java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r0)
            java.lang.String r5 = "\n"
            java.lang.String r0 = r0.replace(r5, r14)
            goto L_0x01f9
        L_0x0208:
            java.lang.String r0 = "DELETED"
            goto L_0x01f9
        L_0x020b:
            java.lang.String r0 = "%2$s: ⁨%1$s⁩"
            java.lang.CharSequence r7 = r8.caption
            r15 = 150(0x96, float:2.1E-43)
            if (r7 == 0) goto L_0x026d
            java.lang.String r3 = r7.toString()
            int r7 = r3.length()
            if (r7 <= r15) goto L_0x0221
            java.lang.String r3 = r3.substring(r10, r15)
        L_0x0221:
            boolean r7 = r8.isVideo()
            if (r7 == 0) goto L_0x022a
            r13 = r18
            goto L_0x0245
        L_0x022a:
            boolean r7 = r8.isVoice()
            if (r7 == 0) goto L_0x0233
            r13 = r17
            goto L_0x0245
        L_0x0233:
            boolean r7 = r8.isMusic()
            if (r7 == 0) goto L_0x023c
            r13 = r16
            goto L_0x0245
        L_0x023c:
            boolean r7 = r8.isPhoto()
            if (r7 == 0) goto L_0x0245
            java.lang.String r13 = "🖼 "
        L_0x0245:
            java.lang.Object[] r7 = new java.lang.Object[r11]
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            r11.append(r13)
            r12 = 32
            r13 = 10
            java.lang.String r3 = r3.replace(r13, r12)
            r11.append(r3)
            java.lang.String r3 = r11.toString()
            r7[r10] = r3
            r3 = 1
            r7[r3] = r5
            java.lang.String r0 = java.lang.String.format(r0, r7)
            android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r0)
            goto L_0x0375
        L_0x026d:
            org.telegram.tgnet.TLRPC$Message r7 = r8.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r7 = r7.media
            if (r7 == 0) goto L_0x0345
            boolean r7 = r8.isMediaEmpty()
            if (r7 != 0) goto L_0x0345
            org.telegram.tgnet.TLRPC$Message r7 = r8.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r7 = r7.media
            boolean r13 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            r14 = 18
            if (r13 == 0) goto L_0x02aa
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r7 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r7
            if (r3 < r14) goto L_0x0299
            r13 = 1
            java.lang.Object[] r3 = new java.lang.Object[r13]
            org.telegram.tgnet.TLRPC$Poll r7 = r7.poll
            java.lang.String r7 = r7.question
            r3[r10] = r7
            java.lang.String r7 = "📊 ⁨%s⁩"
            java.lang.String r3 = java.lang.String.format(r7, r3)
            goto L_0x0310
        L_0x0299:
            r13 = 1
            java.lang.Object[] r3 = new java.lang.Object[r13]
            org.telegram.tgnet.TLRPC$Poll r7 = r7.poll
            java.lang.String r7 = r7.question
            r3[r10] = r7
            java.lang.String r7 = "📊 %s"
            java.lang.String r3 = java.lang.String.format(r7, r3)
            goto L_0x0310
        L_0x02aa:
            r13 = 1
            boolean r15 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r15 == 0) goto L_0x02d6
            if (r3 < r14) goto L_0x02c1
            java.lang.Object[] r3 = new java.lang.Object[r13]
            org.telegram.tgnet.TLRPC$TL_game r7 = r7.game
            java.lang.String r7 = r7.title
            r3[r10] = r7
            java.lang.String r7 = "🎮 ⁨%s⁩"
            java.lang.String r3 = java.lang.String.format(r7, r3)
            goto L_0x0310
        L_0x02c1:
            java.lang.Object[] r3 = new java.lang.Object[r13]
            org.telegram.tgnet.TLRPC$TL_game r7 = r7.game
            java.lang.String r7 = r7.title
            r3[r10] = r7
            java.lang.String r7 = "🎮 %s"
            java.lang.String r3 = java.lang.String.format(r7, r3)
            r7 = 32
            r12 = 10
            r13 = 1
            goto L_0x0314
        L_0x02d6:
            int r7 = r8.type
            r13 = 14
            if (r7 != r13) goto L_0x0309
            if (r3 < r14) goto L_0x02f5
            java.lang.Object[] r3 = new java.lang.Object[r11]
            java.lang.String r7 = r8.getMusicAuthor()
            r3[r10] = r7
            java.lang.String r7 = r8.getMusicTitle()
            r13 = 1
            r3[r13] = r7
            java.lang.String r7 = "🎧 ⁨%s - %s⁩"
            java.lang.String r3 = java.lang.String.format(r7, r3)
            goto L_0x0310
        L_0x02f5:
            r13 = 1
            java.lang.Object[] r3 = new java.lang.Object[r11]
            java.lang.String r7 = r8.getMusicAuthor()
            r3[r10] = r7
            java.lang.String r7 = r8.getMusicTitle()
            r3[r13] = r7
            java.lang.String r3 = java.lang.String.format(r12, r3)
            goto L_0x0310
        L_0x0309:
            r13 = 1
            java.lang.CharSequence r3 = r8.messageText
            java.lang.String r3 = r3.toString()
        L_0x0310:
            r7 = 32
            r12 = 10
        L_0x0314:
            java.lang.String r3 = r3.replace(r12, r7)
            java.lang.Object[] r7 = new java.lang.Object[r11]
            r7[r10] = r3
            r7[r13] = r5
            java.lang.String r0 = java.lang.String.format(r0, r7)
            android.text.SpannableStringBuilder r3 = android.text.SpannableStringBuilder.valueOf(r0)
            org.telegram.ui.Components.ForegroundColorSpanThemable r0 = new org.telegram.ui.Components.ForegroundColorSpanThemable     // Catch:{ Exception -> 0x033c }
            java.lang.String r7 = "chats_attachMessage"
            r0.<init>(r7)     // Catch:{ Exception -> 0x033c }
            int r7 = r5.length()     // Catch:{ Exception -> 0x033c }
            int r7 = r7 + r11
            int r11 = r3.length()     // Catch:{ Exception -> 0x033c }
            r12 = 33
            r3.setSpan(r0, r7, r11, r12)     // Catch:{ Exception -> 0x033c }
            goto L_0x0340
        L_0x033c:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0340:
            r14 = r3
            r12 = -12812624(0xffffffffff3c7eb0, float:-2.5055266E38)
            goto L_0x0379
        L_0x0345:
            org.telegram.tgnet.TLRPC$Message r3 = r8.messageOwner
            java.lang.String r3 = r3.message
            if (r3 == 0) goto L_0x0371
            int r7 = r3.length()
            if (r7 <= r15) goto L_0x0355
            java.lang.String r3 = r3.substring(r10, r15)
        L_0x0355:
            r7 = 32
            r12 = 10
            java.lang.String r3 = r3.replace(r12, r7)
            java.lang.String r3 = r3.trim()
            java.lang.Object[] r7 = new java.lang.Object[r11]
            r7[r10] = r3
            r3 = 1
            r7[r3] = r5
            java.lang.String r0 = java.lang.String.format(r0, r7)
            android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r0)
            goto L_0x0375
        L_0x0371:
            android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r14)
        L_0x0375:
            r14 = r0
            r12 = -14606047(0xfffffffffvar_, float:-2.1417772E38)
        L_0x0379:
            org.telegram.ui.Components.ForegroundColorSpanThemable r0 = new org.telegram.ui.Components.ForegroundColorSpanThemable     // Catch:{ Exception -> 0x038d }
            java.lang.String r3 = "chats_nameMessage"
            r0.<init>(r3)     // Catch:{ Exception -> 0x038d }
            int r3 = r5.length()     // Catch:{ Exception -> 0x038d }
            r5 = 1
            int r3 = r3 + r5
            r5 = 33
            r14.setSpan(r0, r10, r3, r5)     // Catch:{ Exception -> 0x038d }
            goto L_0x0472
        L_0x038d:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0472
        L_0x0393:
            org.telegram.tgnet.TLRPC$Message r0 = r8.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r3 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r3 == 0) goto L_0x03b3
            org.telegram.tgnet.TLRPC$Photo r3 = r0.photo
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC$TL_photoEmpty
            if (r3 == 0) goto L_0x03b3
            int r3 = r0.ttl_seconds
            if (r3 == 0) goto L_0x03b3
            r0 = 2131624377(0x7f0e01b9, float:1.8875932E38)
            java.lang.String r3 = "AttachPhotoExpired"
            java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r3, r0)
        L_0x03ae:
            r12 = -14606047(0xfffffffffvar_, float:-2.1417772E38)
            goto L_0x0472
        L_0x03b3:
            boolean r3 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r3 == 0) goto L_0x03cb
            org.telegram.tgnet.TLRPC$Document r3 = r0.document
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC$TL_documentEmpty
            if (r3 == 0) goto L_0x03cb
            int r3 = r0.ttl_seconds
            if (r3 == 0) goto L_0x03cb
            r0 = 2131624383(0x7f0e01bf, float:1.8875944E38)
            java.lang.String r3 = "AttachVideoExpired"
            java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r3, r0)
            goto L_0x03ae
        L_0x03cb:
            java.lang.CharSequence r3 = r8.caption
            if (r3 == 0) goto L_0x0405
            boolean r0 = r8.isVideo()
            if (r0 == 0) goto L_0x03d8
            r13 = r18
            goto L_0x03f3
        L_0x03d8:
            boolean r0 = r8.isVoice()
            if (r0 == 0) goto L_0x03e1
            r13 = r17
            goto L_0x03f3
        L_0x03e1:
            boolean r0 = r8.isMusic()
            if (r0 == 0) goto L_0x03ea
            r13 = r16
            goto L_0x03f3
        L_0x03ea:
            boolean r0 = r8.isPhoto()
            if (r0 == 0) goto L_0x03f3
            java.lang.String r13 = "🖼 "
        L_0x03f3:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r13)
            java.lang.CharSequence r3 = r8.caption
            r0.append(r3)
            java.lang.String r14 = r0.toString()
            goto L_0x03ae
        L_0x0405:
            boolean r3 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r3 == 0) goto L_0x0423
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r0 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r0
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r5 = "📊 "
            r3.append(r5)
            org.telegram.tgnet.TLRPC$Poll r0 = r0.poll
            java.lang.String r0 = r0.question
            r3.append(r0)
            java.lang.String r0 = r3.toString()
        L_0x0421:
            r14 = r0
            goto L_0x0464
        L_0x0423:
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r0 == 0) goto L_0x0442
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r3 = "🎮 "
            r0.append(r3)
            org.telegram.tgnet.TLRPC$Message r3 = r8.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            org.telegram.tgnet.TLRPC$TL_game r3 = r3.game
            java.lang.String r3 = r3.title
            r0.append(r3)
            java.lang.String r0 = r0.toString()
            goto L_0x0421
        L_0x0442:
            int r0 = r8.type
            r3 = 14
            if (r0 != r3) goto L_0x045c
            java.lang.Object[] r0 = new java.lang.Object[r11]
            java.lang.String r3 = r8.getMusicAuthor()
            r0[r10] = r3
            java.lang.String r3 = r8.getMusicTitle()
            r5 = 1
            r0[r5] = r3
            java.lang.String r0 = java.lang.String.format(r12, r0)
            goto L_0x0421
        L_0x045c:
            java.lang.CharSequence r0 = r8.messageText
            java.util.ArrayList<java.lang.String> r3 = r8.highlightedWords
            org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r0, (java.util.ArrayList<java.lang.String>) r3)
            goto L_0x0421
        L_0x0464:
            org.telegram.tgnet.TLRPC$Message r0 = r8.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            if (r0 == 0) goto L_0x03ae
            boolean r0 = r8.isMediaEmpty()
            if (r0 != 0) goto L_0x03ae
            goto L_0x01c1
        L_0x0472:
            org.telegram.tgnet.TLRPC$Message r0 = r8.messageOwner
            int r0 = r0.date
            long r7 = (long) r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.stringForMessageListDate(r7)
            r3 = 2131230897(0x7var_b1, float:1.807786E38)
            r6.setTextViewText(r3, r0)
            r0 = 2131230895(0x7var_af, float:1.8077856E38)
            r6.setTextViewText(r0, r14)
            r6.setTextColor(r0, r12)
            goto L_0x049a
        L_0x048b:
            r3 = 2131230897(0x7var_b1, float:1.807786E38)
            if (r9 == 0) goto L_0x049a
            int r0 = r9.last_message_date
            long r7 = (long) r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.stringForMessageListDate(r7)
            r6.setTextViewText(r3, r0)
        L_0x049a:
            r0 = 8
            r3 = 2131230893(0x7var_ad, float:1.8077852E38)
            if (r9 == 0) goto L_0x04bb
            int r5 = r9.unread_count
            if (r5 <= 0) goto L_0x04bb
            r7 = 1
            java.lang.Object[] r7 = new java.lang.Object[r7]
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r7[r10] = r5
            java.lang.String r5 = "%d"
            java.lang.String r5 = java.lang.String.format(r5, r7)
            r6.setTextViewText(r3, r5)
            r6.setViewVisibility(r3, r10)
            goto L_0x04be
        L_0x04bb:
            r6.setViewVisibility(r3, r0)
        L_0x04be:
            android.os.Bundle r3 = new android.os.Bundle
            r3.<init>()
            int r5 = r4.intValue()
            if (r5 <= 0) goto L_0x04d4
            int r4 = r4.intValue()
            java.lang.String r5 = "userId"
            r3.putInt(r5, r4)
            goto L_0x04de
        L_0x04d4:
            int r4 = r4.intValue()
            int r4 = -r4
            java.lang.String r5 = "chatId"
            r3.putInt(r5, r4)
        L_0x04de:
            org.telegram.messenger.AccountInstance r4 = r1.accountInstance
            int r4 = r4.getCurrentAccount()
            java.lang.String r5 = "currentAccount"
            r3.putInt(r5, r4)
            android.content.Intent r4 = new android.content.Intent
            r4.<init>()
            r4.putExtras(r3)
            r3 = 2131230891(0x7var_ab, float:1.8077848E38)
            r6.setOnClickFillInIntent(r3, r4)
            r3 = 2131230894(0x7var_ae, float:1.8077854E38)
            int r4 = r20.getCount()
            if (r2 != r4) goto L_0x0502
            r10 = 8
        L_0x0502:
            r6.setViewVisibility(r3, r10)
            return r6
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ShortcutRemoteViewsFactory.getViewAt(int):android.widget.RemoteViews");
    }

    public void onDataSetChanged() {
        this.dids.clear();
        this.messageObjects.clear();
        AccountInstance accountInstance2 = this.accountInstance;
        if (accountInstance2 != null && accountInstance2.getUserConfig().isClientActivated()) {
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            LongSparseArray longSparseArray = new LongSparseArray();
            this.accountInstance.getMessagesStorage().getWidgetDialogs(this.appWidgetId, this.dids, this.dialogs, longSparseArray, arrayList, arrayList2);
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
