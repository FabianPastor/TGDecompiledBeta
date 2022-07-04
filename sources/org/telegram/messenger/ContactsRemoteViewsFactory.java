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

    /* JADX WARNING: Removed duplicated region for block: B:102:0x0254 A[Catch:{ all -> 0x025f }] */
    /* JADX WARNING: Removed duplicated region for block: B:103:0x0258 A[Catch:{ all -> 0x025f }] */
    /* JADX WARNING: Removed duplicated region for block: B:115:0x0285  */
    /* JADX WARNING: Removed duplicated region for block: B:126:0x02c5  */
    /* JADX WARNING: Removed duplicated region for block: B:132:0x02e0  */
    /* JADX WARNING: Removed duplicated region for block: B:133:0x02ea  */
    /* JADX WARNING: Removed duplicated region for block: B:136:0x0307  */
    /* JADX WARNING: Removed duplicated region for block: B:137:0x030b  */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x0180  */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x0184  */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x018d A[SYNTHETIC, Splitter:B:64:0x018d] */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x01bf  */
    /* JADX WARNING: Removed duplicated region for block: B:84:0x01ef A[SYNTHETIC, Splitter:B:84:0x01ef] */
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
            r3 = 2131230951(0x7var_e7, float:1.807797E38)
            r4 = 2131629244(0x7f0e14bc, float:1.8885803E38)
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
            r5 = 2131230953(0x7var_e9, float:1.8077973E38)
            r6 = 2131628549(0x7f0e1205, float:1.8884394E38)
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
            r4 = 2131230952(0x7var_e8, float:1.8077971E38)
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
            if (r6 >= r0) goto L_0x0318
            int r0 = r2 * 2
            int r7 = r0 + r6
            java.util.ArrayList<java.lang.Long> r0 = r1.dids
            int r0 = r0.size()
            if (r7 < r0) goto L_0x00a6
            if (r6 != 0) goto L_0x009c
            r8 = 2131230792(0x7var_, float:1.8077647E38)
            goto L_0x009f
        L_0x009c:
            r8 = 2131230793(0x7var_, float:1.8077649E38)
        L_0x009f:
            r0 = 4
            r5.setViewVisibility(r8, r0)
            r7 = 1
            goto L_0x0311
        L_0x00a6:
            if (r6 != 0) goto L_0x00ac
            r0 = 2131230792(0x7var_, float:1.8077647E38)
            goto L_0x00af
        L_0x00ac:
            r0 = 2131230793(0x7var_, float:1.8077649E38)
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
            r14 = 2131628077(0x7f0e102d, float:1.8883436E38)
            java.lang.String r8 = "SavedMessages"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r8, r14)
            goto L_0x010c
        L_0x00e8:
            boolean r8 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC.User) r12)
            if (r8 == 0) goto L_0x00f8
            r8 = 2131627920(0x7f0e0var_, float:1.8883118E38)
            java.lang.String r14 = "RepliesTitle"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r14, r8)
            goto L_0x010c
        L_0x00f8:
            boolean r8 = org.telegram.messenger.UserObject.isDeleted(r12)
            if (r8 == 0) goto L_0x0108
            r8 = 2131626131(0x7f0e0893, float:1.887949E38)
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
            r0 = 2131230800(0x7var_, float:1.8077663E38)
            goto L_0x0187
        L_0x0184:
            r0 = 2131230801(0x7var_, float:1.8077665E38)
        L_0x0187:
            r5.setTextViewText(r0, r9)
            r0 = 0
            if (r8 == 0) goto L_0x01a8
            int r10 = org.telegram.messenger.UserConfig.selectedAccount     // Catch:{ all -> 0x01a1 }
            org.telegram.messenger.FileLoader r10 = org.telegram.messenger.FileLoader.getInstance(r10)     // Catch:{ all -> 0x01a1 }
            java.io.File r10 = r10.getPathToAttach(r8, r3)     // Catch:{ all -> 0x01a1 }
            java.lang.String r14 = r10.toString()     // Catch:{ all -> 0x01a1 }
            android.graphics.Bitmap r14 = android.graphics.BitmapFactory.decodeFile(r14)     // Catch:{ all -> 0x01a1 }
            r0 = r14
            goto L_0x01a8
        L_0x01a1:
            r0 = move-exception
            r17 = r7
            r20 = r8
            goto L_0x026a
        L_0x01a8:
            r10 = 1111490560(0x42400000, float:48.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)     // Catch:{ all -> 0x0265 }
            android.graphics.Bitmap$Config r14 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x0265 }
            android.graphics.Bitmap r14 = android.graphics.Bitmap.createBitmap(r10, r10, r14)     // Catch:{ all -> 0x0265 }
            r15 = 0
            r14.eraseColor(r15)     // Catch:{ all -> 0x0265 }
            android.graphics.Canvas r15 = new android.graphics.Canvas     // Catch:{ all -> 0x0265 }
            r15.<init>(r14)     // Catch:{ all -> 0x0265 }
            if (r0 != 0) goto L_0x01ef
            if (r12 == 0) goto L_0x01dd
            org.telegram.ui.Components.AvatarDrawable r3 = new org.telegram.ui.Components.AvatarDrawable     // Catch:{ all -> 0x01a1 }
            r3.<init>((org.telegram.tgnet.TLRPC.User) r12)     // Catch:{ all -> 0x01a1 }
            boolean r17 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC.User) r12)     // Catch:{ all -> 0x01a1 }
            if (r17 == 0) goto L_0x01d2
            r2 = 12
            r3.setAvatarType(r2)     // Catch:{ all -> 0x01a1 }
            goto L_0x01e3
        L_0x01d2:
            boolean r2 = org.telegram.messenger.UserObject.isUserSelf(r12)     // Catch:{ all -> 0x01a1 }
            if (r2 == 0) goto L_0x01e3
            r2 = 1
            r3.setAvatarType(r2)     // Catch:{ all -> 0x01a1 }
            goto L_0x01e3
        L_0x01dd:
            org.telegram.ui.Components.AvatarDrawable r2 = new org.telegram.ui.Components.AvatarDrawable     // Catch:{ all -> 0x01a1 }
            r2.<init>((org.telegram.tgnet.TLRPC.Chat) r13)     // Catch:{ all -> 0x01a1 }
            r3 = r2
        L_0x01e3:
            r2 = 0
            r3.setBounds(r2, r2, r10, r10)     // Catch:{ all -> 0x01a1 }
            r3.draw(r15)     // Catch:{ all -> 0x01a1 }
            r17 = r7
            r20 = r8
            goto L_0x024e
        L_0x01ef:
            android.graphics.BitmapShader r2 = new android.graphics.BitmapShader     // Catch:{ all -> 0x0265 }
            android.graphics.Shader$TileMode r3 = android.graphics.Shader.TileMode.CLAMP     // Catch:{ all -> 0x0265 }
            r17 = r7
            android.graphics.Shader$TileMode r7 = android.graphics.Shader.TileMode.CLAMP     // Catch:{ all -> 0x0261 }
            r2.<init>(r0, r3, r7)     // Catch:{ all -> 0x0261 }
            android.graphics.Paint r3 = r1.roundPaint     // Catch:{ all -> 0x0261 }
            if (r3 != 0) goto L_0x0212
            android.graphics.Paint r3 = new android.graphics.Paint     // Catch:{ all -> 0x020e }
            r7 = 1
            r3.<init>(r7)     // Catch:{ all -> 0x020e }
            r1.roundPaint = r3     // Catch:{ all -> 0x020e }
            android.graphics.RectF r3 = new android.graphics.RectF     // Catch:{ all -> 0x020e }
            r3.<init>()     // Catch:{ all -> 0x020e }
            r1.bitmapRect = r3     // Catch:{ all -> 0x020e }
            goto L_0x0212
        L_0x020e:
            r0 = move-exception
            r20 = r8
            goto L_0x026a
        L_0x0212:
            float r3 = (float) r10
            int r7 = r0.getWidth()     // Catch:{ all -> 0x0261 }
            float r7 = (float) r7     // Catch:{ all -> 0x0261 }
            float r3 = r3 / r7
            r15.save()     // Catch:{ all -> 0x0261 }
            r15.scale(r3, r3)     // Catch:{ all -> 0x0261 }
            android.graphics.Paint r7 = r1.roundPaint     // Catch:{ all -> 0x0261 }
            r7.setShader(r2)     // Catch:{ all -> 0x0261 }
            android.graphics.RectF r7 = r1.bitmapRect     // Catch:{ all -> 0x0261 }
            r18 = r2
            int r2 = r0.getWidth()     // Catch:{ all -> 0x0261 }
            float r2 = (float) r2     // Catch:{ all -> 0x0261 }
            r19 = r3
            int r3 = r0.getHeight()     // Catch:{ all -> 0x0261 }
            float r3 = (float) r3
            r20 = r8
            r8 = 0
            r7.set(r8, r8, r2, r3)     // Catch:{ all -> 0x025f }
            android.graphics.RectF r2 = r1.bitmapRect     // Catch:{ all -> 0x025f }
            int r3 = r0.getWidth()     // Catch:{ all -> 0x025f }
            float r3 = (float) r3     // Catch:{ all -> 0x025f }
            int r7 = r0.getHeight()     // Catch:{ all -> 0x025f }
            float r7 = (float) r7     // Catch:{ all -> 0x025f }
            android.graphics.Paint r8 = r1.roundPaint     // Catch:{ all -> 0x025f }
            r15.drawRoundRect(r2, r3, r7, r8)     // Catch:{ all -> 0x025f }
            r15.restore()     // Catch:{ all -> 0x025f }
        L_0x024e:
            r2 = 0
            r15.setBitmap(r2)     // Catch:{ all -> 0x025f }
            if (r6 != 0) goto L_0x0258
            r2 = 2131230794(0x7var_a, float:1.807765E38)
            goto L_0x025b
        L_0x0258:
            r2 = 2131230795(0x7var_b, float:1.8077653E38)
        L_0x025b:
            r5.setImageViewBitmap(r2, r14)     // Catch:{ all -> 0x025f }
            goto L_0x026d
        L_0x025f:
            r0 = move-exception
            goto L_0x026a
        L_0x0261:
            r0 = move-exception
            r20 = r8
            goto L_0x026a
        L_0x0265:
            r0 = move-exception
            r17 = r7
            r20 = r8
        L_0x026a:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x026d:
            androidx.collection.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r0 = r1.dialogs
            long r2 = r11.longValue()
            java.lang.Object r0 = r0.get(r2)
            org.telegram.tgnet.TLRPC$Dialog r0 = (org.telegram.tgnet.TLRPC.Dialog) r0
            r2 = 2131230798(0x7var_e, float:1.8077659E38)
            r3 = 2131230799(0x7var_f, float:1.807766E38)
            if (r0 == 0) goto L_0x02c5
            int r7 = r0.unread_count
            if (r7 <= 0) goto L_0x02c5
            int r7 = r0.unread_count
            r8 = 99
            if (r7 <= r8) goto L_0x029c
            r7 = 1
            java.lang.Object[] r10 = new java.lang.Object[r7]
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            r14 = 0
            r10[r14] = r8
            java.lang.String r8 = "%d+"
            java.lang.String r8 = java.lang.String.format(r8, r10)
            goto L_0x02ae
        L_0x029c:
            r7 = 1
            r14 = 0
            java.lang.Object[] r8 = new java.lang.Object[r7]
            int r10 = r0.unread_count
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            r8[r14] = r10
            java.lang.String r10 = "%d"
            java.lang.String r8 = java.lang.String.format(r10, r8)
        L_0x02ae:
            if (r6 != 0) goto L_0x02b4
            r10 = 2131230796(0x7var_c, float:1.8077655E38)
            goto L_0x02b7
        L_0x02b4:
            r10 = 2131230797(0x7var_d, float:1.8077657E38)
        L_0x02b7:
            r5.setTextViewText(r10, r8)
            if (r6 != 0) goto L_0x02bd
            goto L_0x02c0
        L_0x02bd:
            r2 = 2131230799(0x7var_f, float:1.807766E38)
        L_0x02c0:
            r3 = 0
            r5.setViewVisibility(r2, r3)
            goto L_0x02d1
        L_0x02c5:
            r7 = 1
            if (r6 != 0) goto L_0x02c9
            goto L_0x02cc
        L_0x02c9:
            r2 = 2131230799(0x7var_f, float:1.807766E38)
        L_0x02cc:
            r3 = 8
            r5.setViewVisibility(r2, r3)
        L_0x02d1:
            android.os.Bundle r2 = new android.os.Bundle
            r2.<init>()
            long r14 = r11.longValue()
            boolean r3 = org.telegram.messenger.DialogObject.isUserDialog(r14)
            if (r3 == 0) goto L_0x02ea
            long r14 = r11.longValue()
            java.lang.String r3 = "userId"
            r2.putLong(r3, r14)
            goto L_0x02f4
        L_0x02ea:
            long r14 = r11.longValue()
            long r14 = -r14
            java.lang.String r3 = "chatId"
            r2.putLong(r3, r14)
        L_0x02f4:
            org.telegram.messenger.AccountInstance r3 = r1.accountInstance
            int r3 = r3.getCurrentAccount()
            r2.putInt(r4, r3)
            android.content.Intent r3 = new android.content.Intent
            r3.<init>()
            r3.putExtras(r2)
            if (r6 != 0) goto L_0x030b
            r8 = 2131230792(0x7var_, float:1.8077647E38)
            goto L_0x030e
        L_0x030b:
            r8 = 2131230793(0x7var_, float:1.8077649E38)
        L_0x030e:
            r5.setOnClickFillInIntent(r8, r3)
        L_0x0311:
            int r6 = r6 + 1
            r2 = r22
            r3 = 1
            goto L_0x0087
        L_0x0318:
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
