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

/* compiled from: ContactsWidgetService */
class ContactsRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private AccountInstance accountInstance;
    private int appWidgetId;
    private RectF bitmapRect;
    private boolean deleted;
    private LongSparseArray<TLRPC.Dialog> dialogs = new LongSparseArray<>();
    private ArrayList<Long> dids = new ArrayList<>();
    private Context mContext;
    private Paint roundPaint;

    public ContactsRemoteViewsFactory(Context context, Intent intent) {
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
        return ((int) Math.ceil((double) (((float) this.dids.size()) / 2.0f))) + 1;
    }

    /* JADX WARNING: Removed duplicated region for block: B:102:0x024e A[Catch:{ all -> 0x0259 }] */
    /* JADX WARNING: Removed duplicated region for block: B:103:0x0252 A[Catch:{ all -> 0x0259 }] */
    /* JADX WARNING: Removed duplicated region for block: B:115:0x027f  */
    /* JADX WARNING: Removed duplicated region for block: B:126:0x02bf  */
    /* JADX WARNING: Removed duplicated region for block: B:132:0x02da  */
    /* JADX WARNING: Removed duplicated region for block: B:133:0x02e5  */
    /* JADX WARNING: Removed duplicated region for block: B:136:0x0302  */
    /* JADX WARNING: Removed duplicated region for block: B:137:0x0306  */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x0180  */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x0184  */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x018d A[SYNTHETIC, Splitter:B:64:0x018d] */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x01b9  */
    /* JADX WARNING: Removed duplicated region for block: B:84:0x01e9 A[SYNTHETIC, Splitter:B:84:0x01e9] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.widget.RemoteViews getViewAt(int r22) {
        /*
            r21 = this;
            r1 = r21
            r2 = r22
            boolean r0 = r1.deleted
            if (r0 == 0) goto L_0x0026
            android.widget.RemoteViews r0 = new android.widget.RemoteViews
            android.content.Context r3 = r1.mContext
            java.lang.String r3 = r3.getPackageName()
            r4 = 2131427356(0x7f0b001c, float:1.8476326E38)
            r0.<init>(r3, r4)
            r3 = 2131230941(0x7var_dd, float:1.8077949E38)
            r4 = 2131628697(0x7f0e1299, float:1.8884694E38)
            java.lang.String r5 = "WidgetLoggedOff"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r0.setTextViewText(r3, r4)
            return r0
        L_0x0026:
            int r0 = r21.getCount()
            r3 = 1
            int r0 = r0 - r3
            java.lang.String r4 = "currentAccount"
            if (r2 < r0) goto L_0x0076
            android.widget.RemoteViews r0 = new android.widget.RemoteViews
            android.content.Context r5 = r1.mContext
            java.lang.String r5 = r5.getPackageName()
            r6 = 2131427357(0x7f0b001d, float:1.8476328E38)
            r0.<init>(r5, r6)
            r5 = 2131230943(0x7var_df, float:1.8077953E38)
            r6 = 2131628046(0x7f0e100e, float:1.8883374E38)
            java.lang.String r7 = "TapToEditWidgetShort"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            r0.setTextViewText(r5, r6)
            android.os.Bundle r5 = new android.os.Bundle
            r5.<init>()
            int r6 = r1.appWidgetId
            java.lang.String r7 = "appWidgetId"
            r5.putInt(r7, r6)
            java.lang.String r6 = "appWidgetType"
            r5.putInt(r6, r3)
            org.telegram.messenger.AccountInstance r3 = r1.accountInstance
            int r3 = r3.getCurrentAccount()
            r5.putInt(r4, r3)
            android.content.Intent r3 = new android.content.Intent
            r3.<init>()
            r3.putExtras(r5)
            r4 = 2131230942(0x7var_de, float:1.807795E38)
            r0.setOnClickFillInIntent(r4, r3)
            return r0
        L_0x0076:
            android.widget.RemoteViews r0 = new android.widget.RemoteViews
            android.content.Context r5 = r1.mContext
            java.lang.String r5 = r5.getPackageName()
            r6 = 2131427330(0x7f0b0002, float:1.8476273E38)
            r0.<init>(r5, r6)
            r5 = r0
            r0 = 0
            r6 = r0
        L_0x0087:
            r0 = 2
            if (r6 >= r0) goto L_0x0313
            int r0 = r2 * 2
            int r7 = r0 + r6
            java.util.ArrayList<java.lang.Long> r0 = r1.dids
            int r0 = r0.size()
            if (r7 < r0) goto L_0x00a6
            if (r6 != 0) goto L_0x009c
            r8 = 2131230789(0x7var_, float:1.807764E38)
            goto L_0x009f
        L_0x009c:
            r8 = 2131230790(0x7var_, float:1.8077643E38)
        L_0x009f:
            r0 = 4
            r5.setViewVisibility(r8, r0)
            r7 = 1
            goto L_0x030c
        L_0x00a6:
            if (r6 != 0) goto L_0x00ac
            r0 = 2131230789(0x7var_, float:1.807764E38)
            goto L_0x00af
        L_0x00ac:
            r0 = 2131230790(0x7var_, float:1.8077643E38)
        L_0x00af:
            r10 = 0
            r5.setViewVisibility(r0, r10)
            java.util.ArrayList<java.lang.Long> r0 = r1.dids
            int r11 = r2 * 2
            int r11 = r11 + r6
            java.lang.Object r0 = r0.get(r11)
            r11 = r0
            java.lang.Long r11 = (java.lang.Long) r11
            r0 = 0
            r12 = 0
            r13 = 0
            long r14 = r11.longValue()
            boolean r14 = org.telegram.messenger.DialogObject.isUserDialog(r14)
            r15 = 0
            if (r14 == 0) goto L_0x013d
            org.telegram.messenger.AccountInstance r14 = r1.accountInstance
            org.telegram.messenger.MessagesController r14 = r14.getMessagesController()
            org.telegram.tgnet.TLRPC$User r12 = r14.getUser(r11)
            boolean r14 = org.telegram.messenger.UserObject.isUserSelf(r12)
            if (r14 == 0) goto L_0x00e8
            r14 = 2131627603(0x7f0e0e53, float:1.8882475E38)
            java.lang.String r8 = "SavedMessages"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r8, r14)
            goto L_0x010c
        L_0x00e8:
            boolean r8 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC.User) r12)
            if (r8 == 0) goto L_0x00f8
            r8 = 2131627470(0x7f0e0dce, float:1.8882205E38)
            java.lang.String r14 = "RepliesTitle"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r14, r8)
            goto L_0x010c
        L_0x00f8:
            boolean r8 = org.telegram.messenger.UserObject.isDeleted(r12)
            if (r8 == 0) goto L_0x0108
            r8 = 2131625886(0x7f0e079e, float:1.8878993E38)
            java.lang.String r14 = "HiddenName"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r14, r8)
            goto L_0x010c
        L_0x0108:
            java.lang.String r8 = org.telegram.messenger.UserObject.getFirstName(r12)
        L_0x010c:
            boolean r14 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC.User) r12)
            if (r14 != 0) goto L_0x0177
            boolean r14 = org.telegram.messenger.UserObject.isUserSelf(r12)
            if (r14 != 0) goto L_0x0177
            if (r12 == 0) goto L_0x0177
            org.telegram.tgnet.TLRPC$UserProfilePhoto r14 = r12.photo
            if (r14 == 0) goto L_0x0177
            org.telegram.tgnet.TLRPC$UserProfilePhoto r14 = r12.photo
            org.telegram.tgnet.TLRPC$FileLocation r14 = r14.photo_small
            if (r14 == 0) goto L_0x0177
            org.telegram.tgnet.TLRPC$UserProfilePhoto r14 = r12.photo
            org.telegram.tgnet.TLRPC$FileLocation r14 = r14.photo_small
            long r9 = r14.volume_id
            int r14 = (r9 > r15 ? 1 : (r9 == r15 ? 0 : -1))
            if (r14 == 0) goto L_0x0177
            org.telegram.tgnet.TLRPC$UserProfilePhoto r9 = r12.photo
            org.telegram.tgnet.TLRPC$FileLocation r9 = r9.photo_small
            int r9 = r9.local_id
            if (r9 == 0) goto L_0x0177
            org.telegram.tgnet.TLRPC$UserProfilePhoto r9 = r12.photo
            org.telegram.tgnet.TLRPC$FileLocation r0 = r9.photo_small
            r9 = r8
            r8 = r0
            goto L_0x017e
        L_0x013d:
            org.telegram.messenger.AccountInstance r8 = r1.accountInstance
            org.telegram.messenger.MessagesController r8 = r8.getMessagesController()
            long r9 = r11.longValue()
            long r9 = -r9
            java.lang.Long r9 = java.lang.Long.valueOf(r9)
            org.telegram.tgnet.TLRPC$Chat r13 = r8.getChat(r9)
            if (r13 == 0) goto L_0x017a
            java.lang.String r8 = r13.title
            org.telegram.tgnet.TLRPC$ChatPhoto r9 = r13.photo
            if (r9 == 0) goto L_0x0177
            org.telegram.tgnet.TLRPC$ChatPhoto r9 = r13.photo
            org.telegram.tgnet.TLRPC$FileLocation r9 = r9.photo_small
            if (r9 == 0) goto L_0x0177
            org.telegram.tgnet.TLRPC$ChatPhoto r9 = r13.photo
            org.telegram.tgnet.TLRPC$FileLocation r9 = r9.photo_small
            long r9 = r9.volume_id
            int r14 = (r9 > r15 ? 1 : (r9 == r15 ? 0 : -1))
            if (r14 == 0) goto L_0x0177
            org.telegram.tgnet.TLRPC$ChatPhoto r9 = r13.photo
            org.telegram.tgnet.TLRPC$FileLocation r9 = r9.photo_small
            int r9 = r9.local_id
            if (r9 == 0) goto L_0x0177
            org.telegram.tgnet.TLRPC$ChatPhoto r9 = r13.photo
            org.telegram.tgnet.TLRPC$FileLocation r0 = r9.photo_small
            r9 = r8
            r8 = r0
            goto L_0x017e
        L_0x0177:
            r9 = r8
            r8 = r0
            goto L_0x017e
        L_0x017a:
            java.lang.String r8 = ""
            r9 = r8
            r8 = r0
        L_0x017e:
            if (r6 != 0) goto L_0x0184
            r0 = 2131230797(0x7var_d, float:1.8077657E38)
            goto L_0x0187
        L_0x0184:
            r0 = 2131230798(0x7var_e, float:1.8077659E38)
        L_0x0187:
            r5.setTextViewText(r0, r9)
            r0 = 0
            if (r8 == 0) goto L_0x01a2
            java.io.File r10 = org.telegram.messenger.FileLoader.getPathToAttach(r8, r3)     // Catch:{ all -> 0x019b }
            java.lang.String r14 = r10.toString()     // Catch:{ all -> 0x019b }
            android.graphics.Bitmap r14 = android.graphics.BitmapFactory.decodeFile(r14)     // Catch:{ all -> 0x019b }
            r0 = r14
            goto L_0x01a2
        L_0x019b:
            r0 = move-exception
            r17 = r7
            r20 = r8
            goto L_0x0264
        L_0x01a2:
            r10 = 1111490560(0x42400000, float:48.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)     // Catch:{ all -> 0x025f }
            android.graphics.Bitmap$Config r14 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x025f }
            android.graphics.Bitmap r14 = android.graphics.Bitmap.createBitmap(r10, r10, r14)     // Catch:{ all -> 0x025f }
            r15 = 0
            r14.eraseColor(r15)     // Catch:{ all -> 0x025f }
            android.graphics.Canvas r15 = new android.graphics.Canvas     // Catch:{ all -> 0x025f }
            r15.<init>(r14)     // Catch:{ all -> 0x025f }
            if (r0 != 0) goto L_0x01e9
            if (r12 == 0) goto L_0x01d7
            org.telegram.ui.Components.AvatarDrawable r3 = new org.telegram.ui.Components.AvatarDrawable     // Catch:{ all -> 0x019b }
            r3.<init>((org.telegram.tgnet.TLRPC.User) r12)     // Catch:{ all -> 0x019b }
            boolean r17 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC.User) r12)     // Catch:{ all -> 0x019b }
            if (r17 == 0) goto L_0x01cc
            r2 = 12
            r3.setAvatarType(r2)     // Catch:{ all -> 0x019b }
            goto L_0x01dd
        L_0x01cc:
            boolean r2 = org.telegram.messenger.UserObject.isUserSelf(r12)     // Catch:{ all -> 0x019b }
            if (r2 == 0) goto L_0x01dd
            r2 = 1
            r3.setAvatarType(r2)     // Catch:{ all -> 0x019b }
            goto L_0x01dd
        L_0x01d7:
            org.telegram.ui.Components.AvatarDrawable r2 = new org.telegram.ui.Components.AvatarDrawable     // Catch:{ all -> 0x019b }
            r2.<init>((org.telegram.tgnet.TLRPC.Chat) r13)     // Catch:{ all -> 0x019b }
            r3 = r2
        L_0x01dd:
            r2 = 0
            r3.setBounds(r2, r2, r10, r10)     // Catch:{ all -> 0x019b }
            r3.draw(r15)     // Catch:{ all -> 0x019b }
            r17 = r7
            r20 = r8
            goto L_0x0248
        L_0x01e9:
            android.graphics.BitmapShader r2 = new android.graphics.BitmapShader     // Catch:{ all -> 0x025f }
            android.graphics.Shader$TileMode r3 = android.graphics.Shader.TileMode.CLAMP     // Catch:{ all -> 0x025f }
            r17 = r7
            android.graphics.Shader$TileMode r7 = android.graphics.Shader.TileMode.CLAMP     // Catch:{ all -> 0x025b }
            r2.<init>(r0, r3, r7)     // Catch:{ all -> 0x025b }
            android.graphics.Paint r3 = r1.roundPaint     // Catch:{ all -> 0x025b }
            if (r3 != 0) goto L_0x020c
            android.graphics.Paint r3 = new android.graphics.Paint     // Catch:{ all -> 0x0208 }
            r7 = 1
            r3.<init>(r7)     // Catch:{ all -> 0x0208 }
            r1.roundPaint = r3     // Catch:{ all -> 0x0208 }
            android.graphics.RectF r3 = new android.graphics.RectF     // Catch:{ all -> 0x0208 }
            r3.<init>()     // Catch:{ all -> 0x0208 }
            r1.bitmapRect = r3     // Catch:{ all -> 0x0208 }
            goto L_0x020c
        L_0x0208:
            r0 = move-exception
            r20 = r8
            goto L_0x0264
        L_0x020c:
            float r3 = (float) r10
            int r7 = r0.getWidth()     // Catch:{ all -> 0x025b }
            float r7 = (float) r7     // Catch:{ all -> 0x025b }
            float r3 = r3 / r7
            r15.save()     // Catch:{ all -> 0x025b }
            r15.scale(r3, r3)     // Catch:{ all -> 0x025b }
            android.graphics.Paint r7 = r1.roundPaint     // Catch:{ all -> 0x025b }
            r7.setShader(r2)     // Catch:{ all -> 0x025b }
            android.graphics.RectF r7 = r1.bitmapRect     // Catch:{ all -> 0x025b }
            r18 = r2
            int r2 = r0.getWidth()     // Catch:{ all -> 0x025b }
            float r2 = (float) r2     // Catch:{ all -> 0x025b }
            r19 = r3
            int r3 = r0.getHeight()     // Catch:{ all -> 0x025b }
            float r3 = (float) r3
            r20 = r8
            r8 = 0
            r7.set(r8, r8, r2, r3)     // Catch:{ all -> 0x0259 }
            android.graphics.RectF r2 = r1.bitmapRect     // Catch:{ all -> 0x0259 }
            int r3 = r0.getWidth()     // Catch:{ all -> 0x0259 }
            float r3 = (float) r3     // Catch:{ all -> 0x0259 }
            int r7 = r0.getHeight()     // Catch:{ all -> 0x0259 }
            float r7 = (float) r7     // Catch:{ all -> 0x0259 }
            android.graphics.Paint r8 = r1.roundPaint     // Catch:{ all -> 0x0259 }
            r15.drawRoundRect(r2, r3, r7, r8)     // Catch:{ all -> 0x0259 }
            r15.restore()     // Catch:{ all -> 0x0259 }
        L_0x0248:
            r2 = 0
            r15.setBitmap(r2)     // Catch:{ all -> 0x0259 }
            if (r6 != 0) goto L_0x0252
            r2 = 2131230791(0x7var_, float:1.8077645E38)
            goto L_0x0255
        L_0x0252:
            r2 = 2131230792(0x7var_, float:1.8077647E38)
        L_0x0255:
            r5.setImageViewBitmap(r2, r14)     // Catch:{ all -> 0x0259 }
            goto L_0x0267
        L_0x0259:
            r0 = move-exception
            goto L_0x0264
        L_0x025b:
            r0 = move-exception
            r20 = r8
            goto L_0x0264
        L_0x025f:
            r0 = move-exception
            r17 = r7
            r20 = r8
        L_0x0264:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0267:
            androidx.collection.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r0 = r1.dialogs
            long r2 = r11.longValue()
            java.lang.Object r0 = r0.get(r2)
            org.telegram.tgnet.TLRPC$Dialog r0 = (org.telegram.tgnet.TLRPC.Dialog) r0
            r2 = 2131230795(0x7var_b, float:1.8077653E38)
            r3 = 2131230796(0x7var_c, float:1.8077655E38)
            if (r0 == 0) goto L_0x02bf
            int r7 = r0.unread_count
            if (r7 <= 0) goto L_0x02bf
            int r7 = r0.unread_count
            r8 = 99
            if (r7 <= r8) goto L_0x0296
            r7 = 1
            java.lang.Object[] r10 = new java.lang.Object[r7]
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            r14 = 0
            r10[r14] = r8
            java.lang.String r8 = "%d+"
            java.lang.String r8 = java.lang.String.format(r8, r10)
            goto L_0x02a8
        L_0x0296:
            r7 = 1
            r14 = 0
            java.lang.Object[] r8 = new java.lang.Object[r7]
            int r10 = r0.unread_count
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            r8[r14] = r10
            java.lang.String r10 = "%d"
            java.lang.String r8 = java.lang.String.format(r10, r8)
        L_0x02a8:
            if (r6 != 0) goto L_0x02ae
            r10 = 2131230793(0x7var_, float:1.8077649E38)
            goto L_0x02b1
        L_0x02ae:
            r10 = 2131230794(0x7var_a, float:1.807765E38)
        L_0x02b1:
            r5.setTextViewText(r10, r8)
            if (r6 != 0) goto L_0x02b7
            goto L_0x02ba
        L_0x02b7:
            r2 = 2131230796(0x7var_c, float:1.8077655E38)
        L_0x02ba:
            r3 = 0
            r5.setViewVisibility(r2, r3)
            goto L_0x02cb
        L_0x02bf:
            r7 = 1
            if (r6 != 0) goto L_0x02c3
            goto L_0x02c6
        L_0x02c3:
            r2 = 2131230796(0x7var_c, float:1.8077655E38)
        L_0x02c6:
            r3 = 8
            r5.setViewVisibility(r2, r3)
        L_0x02cb:
            android.os.Bundle r2 = new android.os.Bundle
            r2.<init>()
            long r14 = r11.longValue()
            boolean r3 = org.telegram.messenger.DialogObject.isUserDialog(r14)
            if (r3 == 0) goto L_0x02e5
            long r14 = r11.longValue()
            java.lang.String r3 = "userId"
            r2.putLong(r3, r14)
            goto L_0x02ef
        L_0x02e5:
            long r14 = r11.longValue()
            long r14 = -r14
            java.lang.String r3 = "chatId"
            r2.putLong(r3, r14)
        L_0x02ef:
            org.telegram.messenger.AccountInstance r3 = r1.accountInstance
            int r3 = r3.getCurrentAccount()
            r2.putInt(r4, r3)
            android.content.Intent r3 = new android.content.Intent
            r3.<init>()
            r3.putExtras(r2)
            if (r6 != 0) goto L_0x0306
            r8 = 2131230789(0x7var_, float:1.807764E38)
            goto L_0x0309
        L_0x0306:
            r8 = 2131230790(0x7var_, float:1.8077643E38)
        L_0x0309:
            r5.setOnClickFillInIntent(r8, r3)
        L_0x030c:
            int r6 = r6 + 1
            r2 = r22
            r3 = 1
            goto L_0x0087
        L_0x0313:
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ContactsRemoteViewsFactory.getViewAt(int):android.widget.RemoteViews");
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
        AccountInstance accountInstance2 = this.accountInstance;
        if (accountInstance2 != null && accountInstance2.getUserConfig().isClientActivated()) {
            ArrayList<TLRPC.User> users = new ArrayList<>();
            ArrayList<TLRPC.Chat> chats = new ArrayList<>();
            this.accountInstance.getMessagesStorage().getWidgetDialogs(this.appWidgetId, 1, this.dids, this.dialogs, new LongSparseArray<>(), users, chats);
            this.accountInstance.getMessagesController().putUsers(users, true);
            this.accountInstance.getMessagesController().putChats(chats, true);
        }
    }
}
