package org.telegram.messenger;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.LongSparseArray;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import java.util.ArrayList;
import org.telegram.tgnet.TLRPC$Dialog;
import org.telegram.ui.ActionBar.Theme;

/* compiled from: ContactsWidgetService */
class ContactsRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private AccountInstance accountInstance;
    private int appWidgetId;
    private RectF bitmapRect;
    private boolean deleted;
    private LongSparseArray<TLRPC$Dialog> dialogs = new LongSparseArray<>();
    private ArrayList<Long> dids = new ArrayList<>();
    private Context mContext;
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

    public ContactsRemoteViewsFactory(Context context, Intent intent) {
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
        return ((int) Math.ceil((double) (((float) this.dids.size()) / 2.0f))) + 1;
    }

    /* JADX WARNING: Removed duplicated region for block: B:102:0x0258  */
    /* JADX WARNING: Removed duplicated region for block: B:108:0x0271  */
    /* JADX WARNING: Removed duplicated region for block: B:109:0x027d  */
    /* JADX WARNING: Removed duplicated region for block: B:112:0x029d  */
    /* JADX WARNING: Removed duplicated region for block: B:113:0x02a1  */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x0152  */
    /* JADX WARNING: Removed duplicated region for block: B:57:0x0156  */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x015e  */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x016c A[Catch:{ all -> 0x0215 }] */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x0183 A[Catch:{ all -> 0x0215 }] */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x01b0 A[Catch:{ all -> 0x0215 }] */
    /* JADX WARNING: Removed duplicated region for block: B:84:0x0208 A[Catch:{ all -> 0x0213 }] */
    /* JADX WARNING: Removed duplicated region for block: B:85:0x020c A[Catch:{ all -> 0x0213 }] */
    /* JADX WARNING: Removed duplicated region for block: B:95:0x0233  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.widget.RemoteViews getViewAt(int r19) {
        /*
            r18 = this;
            r1 = r18
            r2 = r19
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
            int r0 = r18.getCount()
            r3 = 1
            int r0 = r0 - r3
            java.lang.String r4 = "currentAccount"
            if (r2 < r0) goto L_0x0076
            android.widget.RemoteViews r0 = new android.widget.RemoteViews
            android.content.Context r2 = r1.mContext
            java.lang.String r2 = r2.getPackageName()
            r5 = 2131427353(0x7f0b0019, float:1.847632E38)
            r0.<init>(r2, r5)
            r2 = 2131230937(0x7var_d9, float:1.807794E38)
            r5 = 2131627561(0x7f0e0e29, float:1.888239E38)
            java.lang.String r6 = "TapToEditWidgetShort"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r0.setTextViewText(r2, r5)
            android.os.Bundle r2 = new android.os.Bundle
            r2.<init>()
            int r5 = r1.appWidgetId
            java.lang.String r6 = "appWidgetId"
            r2.putInt(r6, r5)
            java.lang.String r5 = "appWidgetType"
            r2.putInt(r5, r3)
            org.telegram.messenger.AccountInstance r3 = r1.accountInstance
            int r3 = r3.getCurrentAccount()
            r2.putInt(r4, r3)
            android.content.Intent r3 = new android.content.Intent
            r3.<init>()
            r3.putExtras(r2)
            r2 = 2131230936(0x7var_d8, float:1.8077939E38)
            r0.setOnClickFillInIntent(r2, r3)
            return r0
        L_0x0076:
            android.widget.RemoteViews r5 = new android.widget.RemoteViews
            android.content.Context r0 = r1.mContext
            java.lang.String r0 = r0.getPackageName()
            r6 = 2131427330(0x7f0b0002, float:1.8476273E38)
            r5.<init>(r0, r6)
            r6 = 0
            r7 = 0
        L_0x0086:
            r0 = 2
            if (r7 >= r0) goto L_0x02ae
            int r0 = r2 * 2
            int r0 = r0 + r7
            java.util.ArrayList<java.lang.Long> r8 = r1.dids
            int r8 = r8.size()
            if (r0 < r8) goto L_0x00a6
            if (r7 != 0) goto L_0x009a
            r9 = 2131230789(0x7var_, float:1.807764E38)
            goto L_0x009d
        L_0x009a:
            r9 = 2131230790(0x7var_, float:1.8077643E38)
        L_0x009d:
            r0 = 4
            r5.setViewVisibility(r9, r0)
            r9 = r1
            r3 = r4
            r10 = 1
            goto L_0x02a7
        L_0x00a6:
            if (r7 != 0) goto L_0x00ac
            r8 = 2131230789(0x7var_, float:1.807764E38)
            goto L_0x00af
        L_0x00ac:
            r8 = 2131230790(0x7var_, float:1.8077643E38)
        L_0x00af:
            r5.setViewVisibility(r8, r6)
            java.util.ArrayList<java.lang.Long> r8 = r1.dids
            java.lang.Object r0 = r8.get(r0)
            r8 = r0
            java.lang.Long r8 = (java.lang.Long) r8
            long r11 = r8.longValue()
            r13 = 0
            int r15 = (r11 > r13 ? 1 : (r11 == r13 ? 0 : -1))
            if (r15 <= 0) goto L_0x0122
            org.telegram.messenger.AccountInstance r11 = r1.accountInstance
            org.telegram.messenger.MessagesController r11 = r11.getMessagesController()
            long r9 = r8.longValue()
            int r10 = (int) r9
            java.lang.Integer r9 = java.lang.Integer.valueOf(r10)
            org.telegram.tgnet.TLRPC$User r9 = r11.getUser(r9)
            boolean r10 = org.telegram.messenger.UserObject.isUserSelf(r9)
            if (r10 == 0) goto L_0x00e8
            r10 = 2131627199(0x7f0e0cbf, float:1.8881656E38)
            java.lang.String r11 = "SavedMessages"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)
            goto L_0x00fc
        L_0x00e8:
            boolean r10 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC$User) r9)
            if (r10 == 0) goto L_0x00f8
            r10 = 2131627089(0x7f0e0CLASSNAME, float:1.8881433E38)
            java.lang.String r11 = "RepliesTitle"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)
            goto L_0x00fc
        L_0x00f8:
            java.lang.String r10 = org.telegram.messenger.UserObject.getFirstName(r9)
        L_0x00fc:
            boolean r11 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC$User) r9)
            if (r11 != 0) goto L_0x011e
            boolean r11 = org.telegram.messenger.UserObject.isUserSelf(r9)
            if (r11 != 0) goto L_0x011e
            org.telegram.tgnet.TLRPC$UserProfilePhoto r11 = r9.photo
            if (r11 == 0) goto L_0x011e
            org.telegram.tgnet.TLRPC$FileLocation r11 = r11.photo_small
            if (r11 == 0) goto L_0x011e
            r16 = r4
            long r3 = r11.volume_id
            int r17 = (r3 > r13 ? 1 : (r3 == r13 ? 0 : -1))
            if (r17 == 0) goto L_0x0120
            int r3 = r11.local_id
            if (r3 == 0) goto L_0x0120
            r3 = 0
            goto L_0x0150
        L_0x011e:
            r16 = r4
        L_0x0120:
            r3 = 0
            goto L_0x014f
        L_0x0122:
            r16 = r4
            org.telegram.messenger.AccountInstance r3 = r1.accountInstance
            org.telegram.messenger.MessagesController r3 = r3.getMessagesController()
            long r9 = r8.longValue()
            int r4 = (int) r9
            int r4 = -r4
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            org.telegram.tgnet.TLRPC$Chat r3 = r3.getChat(r4)
            java.lang.String r10 = r3.title
            org.telegram.tgnet.TLRPC$ChatPhoto r4 = r3.photo
            if (r4 == 0) goto L_0x014e
            org.telegram.tgnet.TLRPC$FileLocation r11 = r4.photo_small
            if (r11 == 0) goto L_0x014e
            long r0 = r11.volume_id
            int r9 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
            if (r9 == 0) goto L_0x014e
            int r0 = r11.local_id
            if (r0 == 0) goto L_0x014e
            r9 = 0
            goto L_0x0150
        L_0x014e:
            r9 = 0
        L_0x014f:
            r11 = 0
        L_0x0150:
            if (r7 != 0) goto L_0x0156
            r0 = 2131230797(0x7var_d, float:1.8077657E38)
            goto L_0x0159
        L_0x0156:
            r0 = 2131230798(0x7var_e, float:1.8077659E38)
        L_0x0159:
            r5.setTextViewText(r0, r10)
            if (r11 == 0) goto L_0x016c
            r1 = 1
            java.io.File r0 = org.telegram.messenger.FileLoader.getPathToAttach(r11, r1)     // Catch:{ all -> 0x0215 }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x0215 }
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeFile(r0)     // Catch:{ all -> 0x0215 }
            goto L_0x016d
        L_0x016c:
            r0 = 0
        L_0x016d:
            r1 = 1111490560(0x42400000, float:48.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)     // Catch:{ all -> 0x0215 }
            android.graphics.Bitmap$Config r10 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x0215 }
            android.graphics.Bitmap r10 = android.graphics.Bitmap.createBitmap(r1, r1, r10)     // Catch:{ all -> 0x0215 }
            r10.eraseColor(r6)     // Catch:{ all -> 0x0215 }
            android.graphics.Canvas r11 = new android.graphics.Canvas     // Catch:{ all -> 0x0215 }
            r11.<init>(r10)     // Catch:{ all -> 0x0215 }
            if (r0 != 0) goto L_0x01b0
            if (r9 == 0) goto L_0x01a1
            org.telegram.ui.Components.AvatarDrawable r0 = new org.telegram.ui.Components.AvatarDrawable     // Catch:{ all -> 0x0215 }
            r0.<init>((org.telegram.tgnet.TLRPC$User) r9)     // Catch:{ all -> 0x0215 }
            boolean r3 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC$User) r9)     // Catch:{ all -> 0x0215 }
            if (r3 == 0) goto L_0x0196
            r3 = 12
            r0.setAvatarType(r3)     // Catch:{ all -> 0x0215 }
            goto L_0x01a6
        L_0x0196:
            boolean r3 = org.telegram.messenger.UserObject.isUserSelf(r9)     // Catch:{ all -> 0x0215 }
            if (r3 == 0) goto L_0x01a6
            r3 = 1
            r0.setAvatarType(r3)     // Catch:{ all -> 0x0215 }
            goto L_0x01a6
        L_0x01a1:
            org.telegram.ui.Components.AvatarDrawable r0 = new org.telegram.ui.Components.AvatarDrawable     // Catch:{ all -> 0x0215 }
            r0.<init>((org.telegram.tgnet.TLRPC$Chat) r3)     // Catch:{ all -> 0x0215 }
        L_0x01a6:
            r0.setBounds(r6, r6, r1, r1)     // Catch:{ all -> 0x0215 }
            r0.draw(r11)     // Catch:{ all -> 0x0215 }
            r0 = 0
            r9 = r18
            goto L_0x0203
        L_0x01b0:
            android.graphics.BitmapShader r3 = new android.graphics.BitmapShader     // Catch:{ all -> 0x0215 }
            android.graphics.Shader$TileMode r9 = android.graphics.Shader.TileMode.CLAMP     // Catch:{ all -> 0x0215 }
            r3.<init>(r0, r9, r9)     // Catch:{ all -> 0x0215 }
            r9 = r18
            android.graphics.Paint r4 = r9.roundPaint     // Catch:{ all -> 0x0213 }
            if (r4 != 0) goto L_0x01cc
            android.graphics.Paint r4 = new android.graphics.Paint     // Catch:{ all -> 0x0213 }
            r12 = 1
            r4.<init>(r12)     // Catch:{ all -> 0x0213 }
            r9.roundPaint = r4     // Catch:{ all -> 0x0213 }
            android.graphics.RectF r4 = new android.graphics.RectF     // Catch:{ all -> 0x0213 }
            r4.<init>()     // Catch:{ all -> 0x0213 }
            r9.bitmapRect = r4     // Catch:{ all -> 0x0213 }
        L_0x01cc:
            float r1 = (float) r1     // Catch:{ all -> 0x0213 }
            int r4 = r0.getWidth()     // Catch:{ all -> 0x0213 }
            float r4 = (float) r4     // Catch:{ all -> 0x0213 }
            float r1 = r1 / r4
            r11.save()     // Catch:{ all -> 0x0213 }
            r11.scale(r1, r1)     // Catch:{ all -> 0x0213 }
            android.graphics.Paint r1 = r9.roundPaint     // Catch:{ all -> 0x0213 }
            r1.setShader(r3)     // Catch:{ all -> 0x0213 }
            android.graphics.RectF r1 = r9.bitmapRect     // Catch:{ all -> 0x0213 }
            int r3 = r0.getWidth()     // Catch:{ all -> 0x0213 }
            float r3 = (float) r3     // Catch:{ all -> 0x0213 }
            int r4 = r0.getHeight()     // Catch:{ all -> 0x0213 }
            float r4 = (float) r4     // Catch:{ all -> 0x0213 }
            r12 = 0
            r1.set(r12, r12, r3, r4)     // Catch:{ all -> 0x0213 }
            android.graphics.RectF r1 = r9.bitmapRect     // Catch:{ all -> 0x0213 }
            int r3 = r0.getWidth()     // Catch:{ all -> 0x0213 }
            float r3 = (float) r3     // Catch:{ all -> 0x0213 }
            int r0 = r0.getHeight()     // Catch:{ all -> 0x0213 }
            float r0 = (float) r0     // Catch:{ all -> 0x0213 }
            android.graphics.Paint r4 = r9.roundPaint     // Catch:{ all -> 0x0213 }
            r11.drawRoundRect(r1, r3, r0, r4)     // Catch:{ all -> 0x0213 }
            r11.restore()     // Catch:{ all -> 0x0213 }
            r0 = 0
        L_0x0203:
            r11.setBitmap(r0)     // Catch:{ all -> 0x0213 }
            if (r7 != 0) goto L_0x020c
            r0 = 2131230791(0x7var_, float:1.8077645E38)
            goto L_0x020f
        L_0x020c:
            r0 = 2131230792(0x7var_, float:1.8077647E38)
        L_0x020f:
            r5.setImageViewBitmap(r0, r10)     // Catch:{ all -> 0x0213 }
            goto L_0x021b
        L_0x0213:
            r0 = move-exception
            goto L_0x0218
        L_0x0215:
            r0 = move-exception
            r9 = r18
        L_0x0218:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x021b:
            android.util.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r0 = r9.dialogs
            long r3 = r8.longValue()
            java.lang.Object r0 = r0.get(r3)
            org.telegram.tgnet.TLRPC$Dialog r0 = (org.telegram.tgnet.TLRPC$Dialog) r0
            r1 = 2131230795(0x7var_b, float:1.8077653E38)
            r3 = 2131230796(0x7var_c, float:1.8077655E38)
            if (r0 == 0) goto L_0x0258
            int r0 = r0.unread_count
            if (r0 <= 0) goto L_0x0258
            if (r7 != 0) goto L_0x0239
            r4 = 2131230793(0x7var_, float:1.8077649E38)
            goto L_0x023c
        L_0x0239:
            r4 = 2131230794(0x7var_a, float:1.807765E38)
        L_0x023c:
            r10 = 1
            java.lang.Object[] r11 = new java.lang.Object[r10]
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            r11[r6] = r0
            java.lang.String r0 = "%d"
            java.lang.String r0 = java.lang.String.format(r0, r11)
            r5.setTextViewText(r4, r0)
            if (r7 != 0) goto L_0x0251
            goto L_0x0254
        L_0x0251:
            r1 = 2131230796(0x7var_c, float:1.8077655E38)
        L_0x0254:
            r5.setViewVisibility(r1, r6)
            goto L_0x0264
        L_0x0258:
            r10 = 1
            if (r7 != 0) goto L_0x025c
            goto L_0x025f
        L_0x025c:
            r1 = 2131230796(0x7var_c, float:1.8077655E38)
        L_0x025f:
            r0 = 8
            r5.setViewVisibility(r1, r0)
        L_0x0264:
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            long r3 = r8.longValue()
            int r1 = (r3 > r13 ? 1 : (r3 == r13 ? 0 : -1))
            if (r1 <= 0) goto L_0x027d
            long r3 = r8.longValue()
            int r1 = (int) r3
            java.lang.String r3 = "userId"
            r0.putInt(r3, r1)
            goto L_0x0288
        L_0x027d:
            long r3 = r8.longValue()
            int r1 = (int) r3
            int r1 = -r1
            java.lang.String r3 = "chatId"
            r0.putInt(r3, r1)
        L_0x0288:
            org.telegram.messenger.AccountInstance r1 = r9.accountInstance
            int r1 = r1.getCurrentAccount()
            r3 = r16
            r0.putInt(r3, r1)
            android.content.Intent r1 = new android.content.Intent
            r1.<init>()
            r1.putExtras(r0)
            if (r7 != 0) goto L_0x02a1
            r15 = 2131230789(0x7var_, float:1.807764E38)
            goto L_0x02a4
        L_0x02a1:
            r15 = 2131230790(0x7var_, float:1.8077643E38)
        L_0x02a4:
            r5.setOnClickFillInIntent(r15, r1)
        L_0x02a7:
            int r7 = r7 + 1
            r4 = r3
            r1 = r9
            r3 = 1
            goto L_0x0086
        L_0x02ae:
            r9 = r1
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ContactsRemoteViewsFactory.getViewAt(int):android.widget.RemoteViews");
    }

    public void onDataSetChanged() {
        this.dids.clear();
        AccountInstance accountInstance2 = this.accountInstance;
        if (accountInstance2 != null && accountInstance2.getUserConfig().isClientActivated()) {
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            this.accountInstance.getMessagesStorage().getWidgetDialogs(this.appWidgetId, 1, this.dids, this.dialogs, new LongSparseArray(), arrayList, arrayList2);
            this.accountInstance.getMessagesController().putUsers(arrayList, true);
            this.accountInstance.getMessagesController().putChats(arrayList2, true);
        }
    }
}
