package org.telegram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LayoutAnimationController;
import android.widget.FrameLayout;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.NotificationsCheckCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextDetailSettingsCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

public class NotificationsSettingsActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    /* access modifiers changed from: private */
    public int accountsAllRow;
    /* access modifiers changed from: private */
    public int accountsInfoRow;
    /* access modifiers changed from: private */
    public int accountsSectionRow;
    private ListAdapter adapter;
    /* access modifiers changed from: private */
    public int androidAutoAlertRow;
    /* access modifiers changed from: private */
    public int badgeNumberMessagesRow;
    /* access modifiers changed from: private */
    public int badgeNumberMutedRow;
    /* access modifiers changed from: private */
    public int badgeNumberSection;
    /* access modifiers changed from: private */
    public int badgeNumberSection2Row;
    /* access modifiers changed from: private */
    public int badgeNumberShowRow;
    /* access modifiers changed from: private */
    public int callsRingtoneRow;
    /* access modifiers changed from: private */
    public int callsSection2Row;
    /* access modifiers changed from: private */
    public int callsSectionRow;
    /* access modifiers changed from: private */
    public int callsVibrateRow;
    /* access modifiers changed from: private */
    public int channelsRow;
    /* access modifiers changed from: private */
    public int contactJoinedRow;
    /* access modifiers changed from: private */
    public int eventsSection2Row;
    /* access modifiers changed from: private */
    public int eventsSectionRow;
    /* access modifiers changed from: private */
    public ArrayList<NotificationException> exceptionChannels = null;
    /* access modifiers changed from: private */
    public ArrayList<NotificationException> exceptionChats = null;
    /* access modifiers changed from: private */
    public ArrayList<NotificationException> exceptionUsers = null;
    /* access modifiers changed from: private */
    public int groupRow;
    /* access modifiers changed from: private */
    public int inappPreviewRow;
    /* access modifiers changed from: private */
    public int inappPriorityRow;
    /* access modifiers changed from: private */
    public int inappSectionRow;
    /* access modifiers changed from: private */
    public int inappSoundRow;
    /* access modifiers changed from: private */
    public int inappVibrateRow;
    /* access modifiers changed from: private */
    public int inchatSoundRow;
    private LinearLayoutManager layoutManager;
    private RecyclerListView listView;
    /* access modifiers changed from: private */
    public int notificationsSection2Row;
    /* access modifiers changed from: private */
    public int notificationsSectionRow;
    /* access modifiers changed from: private */
    public int notificationsServiceConnectionRow;
    /* access modifiers changed from: private */
    public int notificationsServiceRow;
    /* access modifiers changed from: private */
    public int otherSection2Row;
    /* access modifiers changed from: private */
    public int otherSectionRow;
    /* access modifiers changed from: private */
    public int pinnedMessageRow;
    /* access modifiers changed from: private */
    public int privateRow;
    /* access modifiers changed from: private */
    public int repeatRow;
    /* access modifiers changed from: private */
    public int resetNotificationsRow;
    /* access modifiers changed from: private */
    public int resetNotificationsSectionRow;
    /* access modifiers changed from: private */
    public int resetSection2Row;
    /* access modifiers changed from: private */
    public int resetSectionRow;
    private boolean reseting = false;
    /* access modifiers changed from: private */
    public int rowCount = 0;

    public static class NotificationException {
        public long did;
        public boolean hasCustom;
        public int muteUntil;
        public int notify;
    }

    static /* synthetic */ void lambda$null$5(TLObject tLObject, TLRPC.TL_error tL_error) {
    }

    public boolean onFragmentCreate() {
        MessagesController.getInstance(this.currentAccount).loadSignUpNotificationsSettings();
        loadExceptions();
        if (UserConfig.getActivatedAccountsCount() > 1) {
            int i = this.rowCount;
            this.rowCount = i + 1;
            this.accountsSectionRow = i;
            int i2 = this.rowCount;
            this.rowCount = i2 + 1;
            this.accountsAllRow = i2;
            int i3 = this.rowCount;
            this.rowCount = i3 + 1;
            this.accountsInfoRow = i3;
        } else {
            this.accountsSectionRow = -1;
            this.accountsAllRow = -1;
            this.accountsInfoRow = -1;
        }
        int i4 = this.rowCount;
        this.rowCount = i4 + 1;
        this.notificationsSectionRow = i4;
        int i5 = this.rowCount;
        this.rowCount = i5 + 1;
        this.privateRow = i5;
        int i6 = this.rowCount;
        this.rowCount = i6 + 1;
        this.groupRow = i6;
        int i7 = this.rowCount;
        this.rowCount = i7 + 1;
        this.channelsRow = i7;
        int i8 = this.rowCount;
        this.rowCount = i8 + 1;
        this.notificationsSection2Row = i8;
        int i9 = this.rowCount;
        this.rowCount = i9 + 1;
        this.callsSectionRow = i9;
        int i10 = this.rowCount;
        this.rowCount = i10 + 1;
        this.callsVibrateRow = i10;
        int i11 = this.rowCount;
        this.rowCount = i11 + 1;
        this.callsRingtoneRow = i11;
        int i12 = this.rowCount;
        this.rowCount = i12 + 1;
        this.eventsSection2Row = i12;
        int i13 = this.rowCount;
        this.rowCount = i13 + 1;
        this.badgeNumberSection = i13;
        int i14 = this.rowCount;
        this.rowCount = i14 + 1;
        this.badgeNumberShowRow = i14;
        int i15 = this.rowCount;
        this.rowCount = i15 + 1;
        this.badgeNumberMutedRow = i15;
        int i16 = this.rowCount;
        this.rowCount = i16 + 1;
        this.badgeNumberMessagesRow = i16;
        int i17 = this.rowCount;
        this.rowCount = i17 + 1;
        this.badgeNumberSection2Row = i17;
        int i18 = this.rowCount;
        this.rowCount = i18 + 1;
        this.inappSectionRow = i18;
        int i19 = this.rowCount;
        this.rowCount = i19 + 1;
        this.inappSoundRow = i19;
        int i20 = this.rowCount;
        this.rowCount = i20 + 1;
        this.inappVibrateRow = i20;
        int i21 = this.rowCount;
        this.rowCount = i21 + 1;
        this.inappPreviewRow = i21;
        int i22 = this.rowCount;
        this.rowCount = i22 + 1;
        this.inchatSoundRow = i22;
        if (Build.VERSION.SDK_INT >= 21) {
            int i23 = this.rowCount;
            this.rowCount = i23 + 1;
            this.inappPriorityRow = i23;
        } else {
            this.inappPriorityRow = -1;
        }
        int i24 = this.rowCount;
        this.rowCount = i24 + 1;
        this.callsSection2Row = i24;
        int i25 = this.rowCount;
        this.rowCount = i25 + 1;
        this.eventsSectionRow = i25;
        int i26 = this.rowCount;
        this.rowCount = i26 + 1;
        this.contactJoinedRow = i26;
        int i27 = this.rowCount;
        this.rowCount = i27 + 1;
        this.pinnedMessageRow = i27;
        int i28 = this.rowCount;
        this.rowCount = i28 + 1;
        this.otherSection2Row = i28;
        int i29 = this.rowCount;
        this.rowCount = i29 + 1;
        this.otherSectionRow = i29;
        int i30 = this.rowCount;
        this.rowCount = i30 + 1;
        this.notificationsServiceRow = i30;
        int i31 = this.rowCount;
        this.rowCount = i31 + 1;
        this.notificationsServiceConnectionRow = i31;
        this.androidAutoAlertRow = -1;
        int i32 = this.rowCount;
        this.rowCount = i32 + 1;
        this.repeatRow = i32;
        int i33 = this.rowCount;
        this.rowCount = i33 + 1;
        this.resetSection2Row = i33;
        int i34 = this.rowCount;
        this.rowCount = i34 + 1;
        this.resetSectionRow = i34;
        int i35 = this.rowCount;
        this.rowCount = i35 + 1;
        this.resetNotificationsRow = i35;
        int i36 = this.rowCount;
        this.rowCount = i36 + 1;
        this.resetNotificationsSectionRow = i36;
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.notificationsSettingsUpdated);
        return super.onFragmentCreate();
    }

    private void loadExceptions() {
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
            public final void run() {
                NotificationsSettingsActivity.this.lambda$loadExceptions$1$NotificationsSettingsActivity();
            }
        });
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v8, resolved type: java.util.ArrayList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v15, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v34, resolved type: java.util.ArrayList} */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0107, code lost:
        if (r11.deleted != false) goto L_0x0150;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:0x01a8, code lost:
        if (r7.deleted != false) goto L_0x01c7;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:107:0x0283  */
    /* JADX WARNING: Removed duplicated region for block: B:113:0x029e A[LOOP:3: B:112:0x029c->B:113:0x029e, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:116:0x02b5  */
    /* JADX WARNING: Removed duplicated region for block: B:90:0x0242  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$loadExceptions$1$NotificationsSettingsActivity() {
        /*
            r23 = this;
            r9 = r23
            java.util.ArrayList r6 = new java.util.ArrayList
            r6.<init>()
            java.util.ArrayList r7 = new java.util.ArrayList
            r7.<init>()
            java.util.ArrayList r8 = new java.util.ArrayList
            r8.<init>()
            android.util.LongSparseArray r1 = new android.util.LongSparseArray
            r1.<init>()
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            java.util.ArrayList r4 = new java.util.ArrayList
            r4.<init>()
            java.util.ArrayList r5 = new java.util.ArrayList
            r5.<init>()
            java.util.ArrayList r10 = new java.util.ArrayList
            r10.<init>()
            int r11 = r9.currentAccount
            org.telegram.messenger.UserConfig r11 = org.telegram.messenger.UserConfig.getInstance(r11)
            int r11 = r11.clientUserId
            int r12 = r9.currentAccount
            android.content.SharedPreferences r12 = org.telegram.messenger.MessagesController.getNotificationsSettings(r12)
            java.util.Map r13 = r12.getAll()
            java.util.Set r14 = r13.entrySet()
            java.util.Iterator r14 = r14.iterator()
        L_0x004e:
            boolean r15 = r14.hasNext()
            r16 = 32
            r17 = r5
            if (r15 == 0) goto L_0x01d6
            java.lang.Object r15 = r14.next()
            java.util.Map$Entry r15 = (java.util.Map.Entry) r15
            java.lang.Object r18 = r15.getKey()
            r5 = r18
            java.lang.String r5 = (java.lang.String) r5
            r18 = r14
            java.lang.String r14 = "notify2_"
            boolean r19 = r5.startsWith(r14)
            if (r19 == 0) goto L_0x01be
            r19 = r4
            java.lang.String r4 = ""
            java.lang.String r4 = r5.replace(r14, r4)
            java.lang.Long r5 = org.telegram.messenger.Utilities.parseLong(r4)
            r14 = r7
            r20 = r8
            long r7 = r5.longValue()
            r21 = 0
            int r5 = (r7 > r21 ? 1 : (r7 == r21 ? 0 : -1))
            if (r5 == 0) goto L_0x01b7
            r5 = r2
            r21 = r3
            long r2 = (long) r11
            int r22 = (r7 > r2 ? 1 : (r7 == r2 ? 0 : -1))
            if (r22 == 0) goto L_0x01af
            org.telegram.ui.NotificationsSettingsActivity$NotificationException r2 = new org.telegram.ui.NotificationsSettingsActivity$NotificationException
            r2.<init>()
            r2.did = r7
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r22 = r11
            java.lang.String r11 = "custom_"
            r3.append(r11)
            r3.append(r7)
            java.lang.String r3 = r3.toString()
            r11 = 0
            boolean r3 = r12.getBoolean(r3, r11)
            r2.hasCustom = r3
            java.lang.Object r3 = r15.getValue()
            java.lang.Integer r3 = (java.lang.Integer) r3
            int r3 = r3.intValue()
            r2.notify = r3
            int r3 = r2.notify
            if (r3 == 0) goto L_0x00e1
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r11 = "notifyuntil_"
            r3.append(r11)
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            java.lang.Object r3 = r13.get(r3)
            java.lang.Integer r3 = (java.lang.Integer) r3
            if (r3 == 0) goto L_0x00e1
            int r3 = r3.intValue()
            r2.muteUntil = r3
        L_0x00e1:
            int r3 = (int) r7
            r4 = r12
            long r11 = r7 << r16
            int r12 = (int) r11
            if (r3 == 0) goto L_0x0161
            if (r3 <= 0) goto L_0x010f
            int r11 = r9.currentAccount
            org.telegram.messenger.MessagesController r11 = org.telegram.messenger.MessagesController.getInstance(r11)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r3)
            org.telegram.tgnet.TLRPC$User r11 = r11.getUser(r12)
            if (r11 != 0) goto L_0x0105
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            r0.add(r3)
            r1.put(r7, r2)
            goto L_0x010a
        L_0x0105:
            boolean r3 = r11.deleted
            if (r3 == 0) goto L_0x010a
            goto L_0x0150
        L_0x010a:
            r6.add(r2)
            goto L_0x01b2
        L_0x010f:
            int r11 = r9.currentAccount
            org.telegram.messenger.MessagesController r11 = org.telegram.messenger.MessagesController.getInstance(r11)
            int r3 = -r3
            java.lang.Integer r12 = java.lang.Integer.valueOf(r3)
            org.telegram.tgnet.TLRPC$Chat r11 = r11.getChat(r12)
            if (r11 != 0) goto L_0x012b
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            r5.add(r3)
            r1.put(r7, r2)
            goto L_0x0150
        L_0x012b:
            boolean r3 = r11.left
            if (r3 != 0) goto L_0x0150
            boolean r3 = r11.kicked
            if (r3 != 0) goto L_0x0150
            org.telegram.tgnet.TLRPC$InputChannel r3 = r11.migrated_to
            if (r3 == 0) goto L_0x0138
            goto L_0x0150
        L_0x0138:
            boolean r3 = org.telegram.messenger.ChatObject.isChannel(r11)
            if (r3 == 0) goto L_0x0149
            boolean r3 = r11.megagroup
            if (r3 != 0) goto L_0x0149
            r15 = r20
            r15.add(r2)
            goto L_0x01b4
        L_0x0149:
            r15 = r20
            r14.add(r2)
            goto L_0x01b4
        L_0x0150:
            r12 = r4
            r2 = r5
            r7 = r14
            r5 = r17
            r14 = r18
            r4 = r19
            r8 = r20
            r3 = r21
            r11 = r22
            goto L_0x004e
        L_0x0161:
            r15 = r20
            if (r12 == 0) goto L_0x01b4
            int r3 = r9.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            java.lang.Integer r11 = java.lang.Integer.valueOf(r12)
            org.telegram.tgnet.TLRPC$EncryptedChat r3 = r3.getEncryptedChat(r11)
            if (r3 != 0) goto L_0x0182
            java.lang.Integer r3 = java.lang.Integer.valueOf(r12)
            r12 = r21
            r12.add(r3)
            r1.put(r7, r2)
            goto L_0x01ab
        L_0x0182:
            r12 = r21
            int r7 = r9.currentAccount
            org.telegram.messenger.MessagesController r7 = org.telegram.messenger.MessagesController.getInstance(r7)
            int r8 = r3.user_id
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            org.telegram.tgnet.TLRPC$User r7 = r7.getUser(r8)
            if (r7 != 0) goto L_0x01a6
            int r7 = r3.user_id
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            r0.add(r7)
            int r3 = r3.user_id
            long r7 = (long) r3
            r1.put(r7, r2)
            goto L_0x01ab
        L_0x01a6:
            boolean r3 = r7.deleted
            if (r3 == 0) goto L_0x01ab
            goto L_0x01c7
        L_0x01ab:
            r6.add(r2)
            goto L_0x01c7
        L_0x01af:
            r22 = r11
            r4 = r12
        L_0x01b2:
            r15 = r20
        L_0x01b4:
            r12 = r21
            goto L_0x01c7
        L_0x01b7:
            r5 = r2
            r22 = r11
            r4 = r12
            r15 = r20
            goto L_0x01c6
        L_0x01be:
            r5 = r2
            r19 = r4
            r14 = r7
            r15 = r8
            r22 = r11
            r4 = r12
        L_0x01c6:
            r12 = r3
        L_0x01c7:
            r2 = r5
            r3 = r12
            r7 = r14
            r8 = r15
            r5 = r17
            r14 = r18
            r11 = r22
            r12 = r4
            r4 = r19
            goto L_0x004e
        L_0x01d6:
            r5 = r2
            r12 = r3
            r19 = r4
            r14 = r7
            r15 = r8
            r11 = 0
            int r2 = r1.size()
            if (r2 == 0) goto L_0x02d5
            boolean r2 = r12.isEmpty()     // Catch:{ Exception -> 0x0233 }
            java.lang.String r3 = ","
            if (r2 != 0) goto L_0x01f8
            int r2 = r9.currentAccount     // Catch:{ Exception -> 0x0233 }
            org.telegram.messenger.MessagesStorage r2 = org.telegram.messenger.MessagesStorage.getInstance(r2)     // Catch:{ Exception -> 0x0233 }
            java.lang.String r4 = android.text.TextUtils.join(r3, r12)     // Catch:{ Exception -> 0x0233 }
            r2.getEncryptedChatsInternal(r4, r10, r0)     // Catch:{ Exception -> 0x0233 }
        L_0x01f8:
            boolean r2 = r0.isEmpty()     // Catch:{ Exception -> 0x0233 }
            if (r2 != 0) goto L_0x0212
            int r2 = r9.currentAccount     // Catch:{ Exception -> 0x020e }
            org.telegram.messenger.MessagesStorage r2 = org.telegram.messenger.MessagesStorage.getInstance(r2)     // Catch:{ Exception -> 0x020e }
            java.lang.String r0 = android.text.TextUtils.join(r3, r0)     // Catch:{ Exception -> 0x020e }
            r4 = r19
            r2.getUsersInternal(r0, r4)     // Catch:{ Exception -> 0x022f }
            goto L_0x0214
        L_0x020e:
            r0 = move-exception
            r4 = r19
            goto L_0x0230
        L_0x0212:
            r4 = r19
        L_0x0214:
            boolean r0 = r5.isEmpty()     // Catch:{ Exception -> 0x022f }
            if (r0 != 0) goto L_0x022c
            int r0 = r9.currentAccount     // Catch:{ Exception -> 0x022f }
            org.telegram.messenger.MessagesStorage r0 = org.telegram.messenger.MessagesStorage.getInstance(r0)     // Catch:{ Exception -> 0x022f }
            java.lang.String r2 = android.text.TextUtils.join(r3, r5)     // Catch:{ Exception -> 0x022f }
            r5 = r17
            r0.getChatsInternal(r2, r5)     // Catch:{ Exception -> 0x022a }
            goto L_0x023b
        L_0x022a:
            r0 = move-exception
            goto L_0x0238
        L_0x022c:
            r5 = r17
            goto L_0x023b
        L_0x022f:
            r0 = move-exception
        L_0x0230:
            r5 = r17
            goto L_0x0238
        L_0x0233:
            r0 = move-exception
            r5 = r17
            r4 = r19
        L_0x0238:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x023b:
            int r0 = r5.size()
            r2 = 0
        L_0x0240:
            if (r2 >= r0) goto L_0x027c
            java.lang.Object r3 = r5.get(r2)
            org.telegram.tgnet.TLRPC$Chat r3 = (org.telegram.tgnet.TLRPC.Chat) r3
            boolean r7 = r3.left
            if (r7 != 0) goto L_0x0279
            boolean r7 = r3.kicked
            if (r7 != 0) goto L_0x0279
            org.telegram.tgnet.TLRPC$InputChannel r7 = r3.migrated_to
            if (r7 == 0) goto L_0x0255
            goto L_0x0279
        L_0x0255:
            int r7 = r3.id
            int r7 = -r7
            long r7 = (long) r7
            java.lang.Object r7 = r1.get(r7)
            org.telegram.ui.NotificationsSettingsActivity$NotificationException r7 = (org.telegram.ui.NotificationsSettingsActivity.NotificationException) r7
            int r8 = r3.id
            int r8 = -r8
            long r12 = (long) r8
            r1.remove(r12)
            if (r7 == 0) goto L_0x0279
            boolean r8 = org.telegram.messenger.ChatObject.isChannel(r3)
            if (r8 == 0) goto L_0x0276
            boolean r3 = r3.megagroup
            if (r3 != 0) goto L_0x0276
            r15.add(r7)
            goto L_0x0279
        L_0x0276:
            r14.add(r7)
        L_0x0279:
            int r2 = r2 + 1
            goto L_0x0240
        L_0x027c:
            int r0 = r4.size()
            r2 = 0
        L_0x0281:
            if (r2 >= r0) goto L_0x0297
            java.lang.Object r3 = r4.get(r2)
            org.telegram.tgnet.TLRPC$User r3 = (org.telegram.tgnet.TLRPC.User) r3
            boolean r7 = r3.deleted
            if (r7 == 0) goto L_0x028e
            goto L_0x0294
        L_0x028e:
            int r3 = r3.id
            long r7 = (long) r3
            r1.remove(r7)
        L_0x0294:
            int r2 = r2 + 1
            goto L_0x0281
        L_0x0297:
            int r0 = r10.size()
            r2 = 0
        L_0x029c:
            if (r2 >= r0) goto L_0x02af
            java.lang.Object r3 = r10.get(r2)
            org.telegram.tgnet.TLRPC$EncryptedChat r3 = (org.telegram.tgnet.TLRPC.EncryptedChat) r3
            int r3 = r3.id
            long r7 = (long) r3
            long r7 = r7 << r16
            r1.remove(r7)
            int r2 = r2 + 1
            goto L_0x029c
        L_0x02af:
            int r0 = r1.size()
        L_0x02b3:
            if (r11 >= r0) goto L_0x02d9
            long r2 = r1.keyAt(r11)
            int r3 = (int) r2
            if (r3 >= 0) goto L_0x02cb
            java.lang.Object r2 = r1.valueAt(r11)
            r14.remove(r2)
            java.lang.Object r2 = r1.valueAt(r11)
            r15.remove(r2)
            goto L_0x02d2
        L_0x02cb:
            java.lang.Object r2 = r1.valueAt(r11)
            r6.remove(r2)
        L_0x02d2:
            int r11 = r11 + 1
            goto L_0x02b3
        L_0x02d5:
            r5 = r17
            r4 = r19
        L_0x02d9:
            org.telegram.ui.-$$Lambda$NotificationsSettingsActivity$tpC7zsNM6CXmGWZ2kbXQvv8Lbxg r0 = new org.telegram.ui.-$$Lambda$NotificationsSettingsActivity$tpC7zsNM6CXmGWZ2kbXQvv8Lbxg
            r1 = r0
            r2 = r23
            r3 = r4
            r4 = r5
            r5 = r10
            r7 = r14
            r8 = r15
            r1.<init>(r3, r4, r5, r6, r7, r8)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.NotificationsSettingsActivity.lambda$loadExceptions$1$NotificationsSettingsActivity():void");
    }

    public /* synthetic */ void lambda$null$0$NotificationsSettingsActivity(ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3, ArrayList arrayList4, ArrayList arrayList5, ArrayList arrayList6) {
        MessagesController.getInstance(this.currentAccount).putUsers(arrayList, true);
        MessagesController.getInstance(this.currentAccount).putChats(arrayList2, true);
        MessagesController.getInstance(this.currentAccount).putEncryptedChats(arrayList3, true);
        this.exceptionUsers = arrayList4;
        this.exceptionChats = arrayList5;
        this.exceptionChannels = arrayList6;
        this.adapter.notifyItemChanged(this.privateRow);
        this.adapter.notifyItemChanged(this.groupRow);
        this.adapter.notifyItemChanged(this.channelsRow);
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.notificationsSettingsUpdated);
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("NotificationsAndSounds", NUM));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    NotificationsSettingsActivity.this.finishFragment();
                }
            }
        });
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        this.listView = new RecyclerListView(context);
        this.listView.setItemAnimator((RecyclerView.ItemAnimator) null);
        this.listView.setLayoutAnimation((LayoutAnimationController) null);
        RecyclerListView recyclerListView = this.listView;
        AnonymousClass2 r3 = new LinearLayoutManager(context, 1, false) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        this.layoutManager = r3;
        recyclerListView.setLayoutManager(r3);
        this.listView.setVerticalScrollBarEnabled(false);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        RecyclerListView recyclerListView2 = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.adapter = listAdapter;
        recyclerListView2.setAdapter(listAdapter);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListenerExtended) new RecyclerListView.OnItemClickListenerExtended() {
            public final void onItemClick(View view, int i, float f, float f2) {
                NotificationsSettingsActivity.this.lambda$createView$8$NotificationsSettingsActivity(view, i, f, f2);
            }
        });
        return this.fragmentView;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v15, resolved type: java.lang.String} */
    /* JADX WARNING: type inference failed for: r13v14 */
    /* JADX WARNING: type inference failed for: r13v39, types: [android.os.Parcelable] */
    /* JADX WARNING: type inference failed for: r13v42 */
    /* JADX WARNING: type inference failed for: r13v43 */
    /* JADX WARNING: type inference failed for: r13v44 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$createView$8$NotificationsSettingsActivity(android.view.View r10, int r11, float r12, float r13) {
        /*
            r9 = this;
            android.app.Activity r13 = r9.getParentActivity()
            if (r13 != 0) goto L_0x0007
            return
        L_0x0007:
            int r13 = r9.privateRow
            r0 = 2
            r1 = 0
            r2 = 1
            if (r11 == r13) goto L_0x03e1
            int r13 = r9.groupRow
            if (r11 == r13) goto L_0x03e1
            int r13 = r9.channelsRow
            if (r11 != r13) goto L_0x0018
            goto L_0x03e1
        L_0x0018:
            int r12 = r9.callsRingtoneRow
            r13 = 0
            if (r11 != r12) goto L_0x0078
            int r12 = r9.currentAccount     // Catch:{ Exception -> 0x0072 }
            android.content.SharedPreferences r12 = org.telegram.messenger.MessagesController.getNotificationsSettings(r12)     // Catch:{ Exception -> 0x0072 }
            android.content.Intent r0 = new android.content.Intent     // Catch:{ Exception -> 0x0072 }
            java.lang.String r3 = "android.intent.action.RINGTONE_PICKER"
            r0.<init>(r3)     // Catch:{ Exception -> 0x0072 }
            java.lang.String r3 = "android.intent.extra.ringtone.TYPE"
            r0.putExtra(r3, r2)     // Catch:{ Exception -> 0x0072 }
            java.lang.String r3 = "android.intent.extra.ringtone.SHOW_DEFAULT"
            r0.putExtra(r3, r2)     // Catch:{ Exception -> 0x0072 }
            java.lang.String r3 = "android.intent.extra.ringtone.SHOW_SILENT"
            r0.putExtra(r3, r2)     // Catch:{ Exception -> 0x0072 }
            java.lang.String r3 = "android.intent.extra.ringtone.DEFAULT_URI"
            android.net.Uri r4 = android.media.RingtoneManager.getDefaultUri(r2)     // Catch:{ Exception -> 0x0072 }
            r0.putExtra(r3, r4)     // Catch:{ Exception -> 0x0072 }
            android.net.Uri r3 = android.provider.Settings.System.DEFAULT_RINGTONE_URI     // Catch:{ Exception -> 0x0072 }
            if (r3 == 0) goto L_0x004b
            java.lang.String r4 = r3.getPath()     // Catch:{ Exception -> 0x0072 }
            goto L_0x004c
        L_0x004b:
            r4 = r13
        L_0x004c:
            java.lang.String r5 = "CallsRingtonePath"
            java.lang.String r12 = r12.getString(r5, r4)     // Catch:{ Exception -> 0x0072 }
            if (r12 == 0) goto L_0x0068
            java.lang.String r5 = "NoSound"
            boolean r5 = r12.equals(r5)     // Catch:{ Exception -> 0x0072 }
            if (r5 != 0) goto L_0x0068
            boolean r13 = r12.equals(r4)     // Catch:{ Exception -> 0x0072 }
            if (r13 == 0) goto L_0x0064
            r13 = r3
            goto L_0x0068
        L_0x0064:
            android.net.Uri r13 = android.net.Uri.parse(r12)     // Catch:{ Exception -> 0x0072 }
        L_0x0068:
            java.lang.String r12 = "android.intent.extra.ringtone.EXISTING_URI"
            r0.putExtra(r12, r13)     // Catch:{ Exception -> 0x0072 }
            r9.startActivityForResult(r0, r11)     // Catch:{ Exception -> 0x0072 }
            goto L_0x044b
        L_0x0072:
            r11 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r11)
            goto L_0x044b
        L_0x0078:
            int r12 = r9.resetNotificationsRow
            r3 = 2131624479(0x7f0e021f, float:1.8876139E38)
            java.lang.String r4 = "Cancel"
            if (r11 != r12) goto L_0x00d5
            org.telegram.ui.ActionBar.AlertDialog$Builder r11 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            android.app.Activity r12 = r9.getParentActivity()
            r11.<init>((android.content.Context) r12)
            r12 = 2131626455(0x7f0e09d7, float:1.8880147E38)
            java.lang.String r0 = "ResetNotificationsAlertTitle"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
            r11.setTitle(r12)
            r12 = 2131626454(0x7f0e09d6, float:1.8880145E38)
            java.lang.String r0 = "ResetNotificationsAlert"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
            r11.setMessage(r12)
            r12 = 2131626435(0x7f0e09c3, float:1.8880106E38)
            java.lang.String r0 = "Reset"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
            org.telegram.ui.-$$Lambda$NotificationsSettingsActivity$yCH91Gy9ARU8yn1KTl14GsaHDf4 r0 = new org.telegram.ui.-$$Lambda$NotificationsSettingsActivity$yCH91Gy9ARU8yn1KTl14GsaHDf4
            r0.<init>()
            r11.setPositiveButton(r12, r0)
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r11.setNegativeButton(r12, r13)
            org.telegram.ui.ActionBar.AlertDialog r11 = r11.create()
            r9.showDialog(r11)
            r12 = -1
            android.view.View r11 = r11.getButton(r12)
            android.widget.TextView r11 = (android.widget.TextView) r11
            if (r11 == 0) goto L_0x044b
            java.lang.String r12 = "dialogTextRed2"
            int r12 = org.telegram.ui.ActionBar.Theme.getColor(r12)
            r11.setTextColor(r12)
            goto L_0x044b
        L_0x00d5:
            int r12 = r9.inappSoundRow
            if (r11 != r12) goto L_0x00f3
            int r11 = r9.currentAccount
            android.content.SharedPreferences r11 = org.telegram.messenger.MessagesController.getNotificationsSettings(r11)
            android.content.SharedPreferences$Editor r12 = r11.edit()
            java.lang.String r13 = "EnableInAppSounds"
            boolean r1 = r11.getBoolean(r13, r2)
            r11 = r1 ^ 1
            r12.putBoolean(r13, r11)
            r12.commit()
            goto L_0x044b
        L_0x00f3:
            int r12 = r9.inappVibrateRow
            if (r11 != r12) goto L_0x0111
            int r11 = r9.currentAccount
            android.content.SharedPreferences r11 = org.telegram.messenger.MessagesController.getNotificationsSettings(r11)
            android.content.SharedPreferences$Editor r12 = r11.edit()
            java.lang.String r13 = "EnableInAppVibrate"
            boolean r1 = r11.getBoolean(r13, r2)
            r11 = r1 ^ 1
            r12.putBoolean(r13, r11)
            r12.commit()
            goto L_0x044b
        L_0x0111:
            int r12 = r9.inappPreviewRow
            if (r11 != r12) goto L_0x012f
            int r11 = r9.currentAccount
            android.content.SharedPreferences r11 = org.telegram.messenger.MessagesController.getNotificationsSettings(r11)
            android.content.SharedPreferences$Editor r12 = r11.edit()
            java.lang.String r13 = "EnableInAppPreview"
            boolean r1 = r11.getBoolean(r13, r2)
            r11 = r1 ^ 1
            r12.putBoolean(r13, r11)
            r12.commit()
            goto L_0x044b
        L_0x012f:
            int r12 = r9.inchatSoundRow
            if (r11 != r12) goto L_0x0158
            int r11 = r9.currentAccount
            android.content.SharedPreferences r11 = org.telegram.messenger.MessagesController.getNotificationsSettings(r11)
            android.content.SharedPreferences$Editor r12 = r11.edit()
            java.lang.String r13 = "EnableInChatSound"
            boolean r1 = r11.getBoolean(r13, r2)
            r11 = r1 ^ 1
            r12.putBoolean(r13, r11)
            r12.commit()
            int r11 = r9.currentAccount
            org.telegram.messenger.NotificationsController r11 = org.telegram.messenger.NotificationsController.getInstance(r11)
            r12 = r1 ^ 1
            r11.setInChatSoundEnabled(r12)
            goto L_0x044b
        L_0x0158:
            int r12 = r9.inappPriorityRow
            if (r11 != r12) goto L_0x0176
            int r11 = r9.currentAccount
            android.content.SharedPreferences r11 = org.telegram.messenger.MessagesController.getNotificationsSettings(r11)
            android.content.SharedPreferences$Editor r12 = r11.edit()
            java.lang.String r13 = "EnableInAppPriority"
            boolean r1 = r11.getBoolean(r13, r1)
            r11 = r1 ^ 1
            r12.putBoolean(r13, r11)
            r12.commit()
            goto L_0x044b
        L_0x0176:
            int r12 = r9.contactJoinedRow
            if (r11 != r12) goto L_0x01b0
            int r11 = r9.currentAccount
            android.content.SharedPreferences r11 = org.telegram.messenger.MessagesController.getNotificationsSettings(r11)
            android.content.SharedPreferences$Editor r12 = r11.edit()
            java.lang.String r13 = "EnableContactJoined"
            boolean r1 = r11.getBoolean(r13, r2)
            int r11 = r9.currentAccount
            org.telegram.messenger.MessagesController r11 = org.telegram.messenger.MessagesController.getInstance(r11)
            r0 = r1 ^ 1
            r11.enableJoined = r0
            r11 = r1 ^ 1
            r12.putBoolean(r13, r11)
            r12.commit()
            org.telegram.tgnet.TLRPC$TL_account_setContactSignUpNotification r11 = new org.telegram.tgnet.TLRPC$TL_account_setContactSignUpNotification
            r11.<init>()
            r11.silent = r1
            int r12 = r9.currentAccount
            org.telegram.tgnet.ConnectionsManager r12 = org.telegram.tgnet.ConnectionsManager.getInstance(r12)
            org.telegram.ui.-$$Lambda$NotificationsSettingsActivity$viFpXODmAg-Q4M-X6ggvpEc5GAg r13 = org.telegram.ui.$$Lambda$NotificationsSettingsActivity$viFpXODmAgQ4MX6ggvpEc5GAg.INSTANCE
            r12.sendRequest(r11, r13)
            goto L_0x044b
        L_0x01b0:
            int r12 = r9.pinnedMessageRow
            if (r11 != r12) goto L_0x01d0
            int r11 = r9.currentAccount
            android.content.SharedPreferences r11 = org.telegram.messenger.MessagesController.getNotificationsSettings(r11)
            android.content.SharedPreferences$Editor r12 = r11.edit()
            java.lang.String r13 = "PinnedMessages"
            boolean r1 = r11.getBoolean(r13, r2)
            r11 = r1 ^ 1
            java.lang.String r13 = "PinnedMessages"
            r12.putBoolean(r13, r11)
            r12.commit()
            goto L_0x044b
        L_0x01d0:
            int r12 = r9.androidAutoAlertRow
            if (r11 != r12) goto L_0x01f0
            int r11 = r9.currentAccount
            android.content.SharedPreferences r11 = org.telegram.messenger.MessagesController.getNotificationsSettings(r11)
            android.content.SharedPreferences$Editor r12 = r11.edit()
            java.lang.String r13 = "EnableAutoNotifications"
            boolean r1 = r11.getBoolean(r13, r1)
            r11 = r1 ^ 1
            java.lang.String r13 = "EnableAutoNotifications"
            r12.putBoolean(r13, r11)
            r12.commit()
            goto L_0x044b
        L_0x01f0:
            int r12 = r9.badgeNumberShowRow
            if (r11 != r12) goto L_0x022b
            int r11 = r9.currentAccount
            android.content.SharedPreferences r11 = org.telegram.messenger.MessagesController.getNotificationsSettings(r11)
            android.content.SharedPreferences$Editor r11 = r11.edit()
            int r12 = r9.currentAccount
            org.telegram.messenger.NotificationsController r12 = org.telegram.messenger.NotificationsController.getInstance(r12)
            boolean r1 = r12.showBadgeNumber
            int r12 = r9.currentAccount
            org.telegram.messenger.NotificationsController r12 = org.telegram.messenger.NotificationsController.getInstance(r12)
            r13 = r1 ^ 1
            r12.showBadgeNumber = r13
            int r12 = r9.currentAccount
            org.telegram.messenger.NotificationsController r12 = org.telegram.messenger.NotificationsController.getInstance(r12)
            boolean r12 = r12.showBadgeNumber
            java.lang.String r13 = "badgeNumber"
            r11.putBoolean(r13, r12)
            r11.commit()
            int r11 = r9.currentAccount
            org.telegram.messenger.NotificationsController r11 = org.telegram.messenger.NotificationsController.getInstance(r11)
            r11.updateBadge()
            goto L_0x044b
        L_0x022b:
            int r12 = r9.badgeNumberMutedRow
            if (r11 != r12) goto L_0x0266
            int r11 = r9.currentAccount
            android.content.SharedPreferences r11 = org.telegram.messenger.MessagesController.getNotificationsSettings(r11)
            android.content.SharedPreferences$Editor r11 = r11.edit()
            int r12 = r9.currentAccount
            org.telegram.messenger.NotificationsController r12 = org.telegram.messenger.NotificationsController.getInstance(r12)
            boolean r1 = r12.showBadgeMuted
            int r12 = r9.currentAccount
            org.telegram.messenger.NotificationsController r12 = org.telegram.messenger.NotificationsController.getInstance(r12)
            r13 = r1 ^ 1
            r12.showBadgeMuted = r13
            int r12 = r9.currentAccount
            org.telegram.messenger.NotificationsController r12 = org.telegram.messenger.NotificationsController.getInstance(r12)
            boolean r12 = r12.showBadgeMuted
            java.lang.String r13 = "badgeNumberMuted"
            r11.putBoolean(r13, r12)
            r11.commit()
            int r11 = r9.currentAccount
            org.telegram.messenger.NotificationsController r11 = org.telegram.messenger.NotificationsController.getInstance(r11)
            r11.updateBadge()
            goto L_0x044b
        L_0x0266:
            int r12 = r9.badgeNumberMessagesRow
            if (r11 != r12) goto L_0x02a1
            int r11 = r9.currentAccount
            android.content.SharedPreferences r11 = org.telegram.messenger.MessagesController.getNotificationsSettings(r11)
            android.content.SharedPreferences$Editor r11 = r11.edit()
            int r12 = r9.currentAccount
            org.telegram.messenger.NotificationsController r12 = org.telegram.messenger.NotificationsController.getInstance(r12)
            boolean r1 = r12.showBadgeMessages
            int r12 = r9.currentAccount
            org.telegram.messenger.NotificationsController r12 = org.telegram.messenger.NotificationsController.getInstance(r12)
            r13 = r1 ^ 1
            r12.showBadgeMessages = r13
            int r12 = r9.currentAccount
            org.telegram.messenger.NotificationsController r12 = org.telegram.messenger.NotificationsController.getInstance(r12)
            boolean r12 = r12.showBadgeMessages
            java.lang.String r13 = "badgeNumberMessages"
            r11.putBoolean(r13, r12)
            r11.commit()
            int r11 = r9.currentAccount
            org.telegram.messenger.NotificationsController r11 = org.telegram.messenger.NotificationsController.getInstance(r11)
            r11.updateBadge()
            goto L_0x044b
        L_0x02a1:
            int r12 = r9.notificationsServiceConnectionRow
            if (r11 != r12) goto L_0x02dd
            int r11 = r9.currentAccount
            android.content.SharedPreferences r11 = org.telegram.messenger.MessagesController.getNotificationsSettings(r11)
            org.telegram.messenger.MessagesController r12 = r9.getMessagesController()
            boolean r12 = r12.backgroundConnection
            java.lang.String r13 = "pushConnection"
            boolean r12 = r11.getBoolean(r13, r12)
            android.content.SharedPreferences$Editor r11 = r11.edit()
            r13 = r12 ^ 1
            java.lang.String r0 = "pushConnection"
            r11.putBoolean(r0, r13)
            r11.commit()
            if (r12 != 0) goto L_0x02d1
            int r11 = r9.currentAccount
            org.telegram.tgnet.ConnectionsManager r11 = org.telegram.tgnet.ConnectionsManager.getInstance(r11)
            r11.setPushConnectionEnabled(r2)
            goto L_0x02da
        L_0x02d1:
            int r11 = r9.currentAccount
            org.telegram.tgnet.ConnectionsManager r11 = org.telegram.tgnet.ConnectionsManager.getInstance(r11)
            r11.setPushConnectionEnabled(r1)
        L_0x02da:
            r1 = r12
            goto L_0x044b
        L_0x02dd:
            int r12 = r9.accountsAllRow
            if (r11 != r12) goto L_0x0322
            android.content.SharedPreferences r11 = org.telegram.messenger.MessagesController.getGlobalNotificationsSettings()
            java.lang.String r12 = "AllAccounts"
            boolean r12 = r11.getBoolean(r12, r2)
            android.content.SharedPreferences$Editor r11 = r11.edit()
            r13 = r12 ^ 1
            java.lang.String r0 = "AllAccounts"
            r11.putBoolean(r0, r13)
            r11.commit()
            r11 = r12 ^ 1
            org.telegram.messenger.SharedConfig.showNotificationsForAllAccounts = r11
        L_0x02fd:
            r11 = 3
            if (r1 >= r11) goto L_0x02da
            boolean r11 = org.telegram.messenger.SharedConfig.showNotificationsForAllAccounts
            if (r11 == 0) goto L_0x030c
            org.telegram.messenger.NotificationsController r11 = org.telegram.messenger.NotificationsController.getInstance(r1)
            r11.showNotifications()
            goto L_0x031f
        L_0x030c:
            int r11 = r9.currentAccount
            if (r1 != r11) goto L_0x0318
            org.telegram.messenger.NotificationsController r11 = org.telegram.messenger.NotificationsController.getInstance(r1)
            r11.showNotifications()
            goto L_0x031f
        L_0x0318:
            org.telegram.messenger.NotificationsController r11 = org.telegram.messenger.NotificationsController.getInstance(r1)
            r11.hideNotifications()
        L_0x031f:
            int r1 = r1 + 1
            goto L_0x02fd
        L_0x0322:
            int r12 = r9.notificationsServiceRow
            if (r11 != r12) goto L_0x034b
            int r11 = r9.currentAccount
            android.content.SharedPreferences r11 = org.telegram.messenger.MessagesController.getNotificationsSettings(r11)
            org.telegram.messenger.MessagesController r12 = r9.getMessagesController()
            boolean r12 = r12.keepAliveService
            java.lang.String r13 = "pushService"
            boolean r1 = r11.getBoolean(r13, r12)
            android.content.SharedPreferences$Editor r11 = r11.edit()
            r12 = r1 ^ 1
            java.lang.String r13 = "pushService"
            r11.putBoolean(r13, r12)
            r11.commit()
            org.telegram.messenger.ApplicationLoader.startPushService()
            goto L_0x044b
        L_0x034b:
            int r12 = r9.callsVibrateRow
            if (r11 != r12) goto L_0x0371
            android.app.Activity r12 = r9.getParentActivity()
            if (r12 != 0) goto L_0x0356
            return
        L_0x0356:
            int r12 = r9.callsVibrateRow
            if (r11 != r12) goto L_0x035d
            java.lang.String r13 = "vibrate_calls"
        L_0x035d:
            android.app.Activity r12 = r9.getParentActivity()
            r3 = 0
            org.telegram.ui.-$$Lambda$NotificationsSettingsActivity$7IXN7L8E_cyofxGsKJruA7N2DeY r0 = new org.telegram.ui.-$$Lambda$NotificationsSettingsActivity$7IXN7L8E_cyofxGsKJruA7N2DeY
            r0.<init>(r11)
            android.app.Dialog r11 = org.telegram.ui.Components.AlertsCreator.createVibrationSelectDialog(r12, r3, r13, r0)
            r9.showDialog(r11)
            goto L_0x044b
        L_0x0371:
            int r12 = r9.repeatRow
            if (r11 != r12) goto L_0x044b
            org.telegram.ui.ActionBar.AlertDialog$Builder r12 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            android.app.Activity r5 = r9.getParentActivity()
            r12.<init>((android.content.Context) r5)
            r5 = 2131626408(0x7f0e09a8, float:1.8880051E38)
            java.lang.String r6 = "RepeatNotifications"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r12.setTitle(r5)
            r5 = 7
            java.lang.CharSequence[] r5 = new java.lang.CharSequence[r5]
            r6 = 2131626407(0x7f0e09a7, float:1.888005E38)
            java.lang.String r7 = "RepeatDisabled"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            r5[r1] = r6
            r6 = 5
            java.lang.String r7 = "Minutes"
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatPluralString(r7, r6)
            r5[r2] = r6
            r6 = 10
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatPluralString(r7, r6)
            r5[r0] = r6
            r6 = 3
            r8 = 30
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatPluralString(r7, r8)
            r5[r6] = r7
            r6 = 4
            java.lang.String r7 = "Hours"
            java.lang.String r8 = org.telegram.messenger.LocaleController.formatPluralString(r7, r2)
            r5[r6] = r8
            r6 = 5
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r7, r0)
            r5[r6] = r0
            r0 = 6
            r6 = 4
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatPluralString(r7, r6)
            r5[r0] = r6
            org.telegram.ui.-$$Lambda$NotificationsSettingsActivity$KVxXWyv-zLmmyeu95JQljLmRuOE r0 = new org.telegram.ui.-$$Lambda$NotificationsSettingsActivity$KVxXWyv-zLmmyeu95JQljLmRuOE
            r0.<init>(r11)
            r12.setItems(r5, r0)
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r12.setNegativeButton(r11, r13)
            org.telegram.ui.ActionBar.AlertDialog r11 = r12.create()
            r9.showDialog(r11)
            goto L_0x044b
        L_0x03e1:
            int r13 = r9.privateRow
            if (r11 != r13) goto L_0x03e9
            java.util.ArrayList<org.telegram.ui.NotificationsSettingsActivity$NotificationException> r13 = r9.exceptionUsers
            r0 = 1
            goto L_0x03f3
        L_0x03e9:
            int r13 = r9.groupRow
            if (r11 != r13) goto L_0x03f1
            java.util.ArrayList<org.telegram.ui.NotificationsSettingsActivity$NotificationException> r13 = r9.exceptionChats
            r0 = 0
            goto L_0x03f3
        L_0x03f1:
            java.util.ArrayList<org.telegram.ui.NotificationsSettingsActivity$NotificationException> r13 = r9.exceptionChannels
        L_0x03f3:
            if (r13 != 0) goto L_0x03f6
            return
        L_0x03f6:
            r3 = r10
            org.telegram.ui.Cells.NotificationsCheckCell r3 = (org.telegram.ui.Cells.NotificationsCheckCell) r3
            int r4 = r9.currentAccount
            org.telegram.messenger.NotificationsController r4 = org.telegram.messenger.NotificationsController.getInstance(r4)
            boolean r4 = r4.isGlobalNotificationsEnabled((int) r0)
            boolean r5 = org.telegram.messenger.LocaleController.isRTL
            r6 = 1117257728(0x42980000, float:76.0)
            if (r5 == 0) goto L_0x0412
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r5 = (float) r5
            int r5 = (r12 > r5 ? 1 : (r12 == r5 ? 0 : -1))
            if (r5 <= 0) goto L_0x0424
        L_0x0412:
            boolean r5 = org.telegram.messenger.LocaleController.isRTL
            if (r5 != 0) goto L_0x0442
            int r5 = r10.getMeasuredWidth()
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r5 = r5 - r6
            float r5 = (float) r5
            int r12 = (r12 > r5 ? 1 : (r12 == r5 ? 0 : -1))
            if (r12 < 0) goto L_0x0442
        L_0x0424:
            int r12 = r9.currentAccount
            org.telegram.messenger.NotificationsController r12 = org.telegram.messenger.NotificationsController.getInstance(r12)
            if (r4 != 0) goto L_0x042e
            r13 = 0
            goto L_0x0431
        L_0x042e:
            r13 = 2147483647(0x7fffffff, float:NaN)
        L_0x0431:
            r12.setGlobalNotificationsEnabled(r0, r13)
            r9.showExceptionsAlert(r11)
            r12 = r4 ^ 1
            r3.setChecked(r12, r1)
            org.telegram.ui.NotificationsSettingsActivity$ListAdapter r12 = r9.adapter
            r12.notifyItemChanged(r11)
            goto L_0x044a
        L_0x0442:
            org.telegram.ui.NotificationsCustomSettingsActivity r11 = new org.telegram.ui.NotificationsCustomSettingsActivity
            r11.<init>(r0, r13)
            r9.presentFragment(r11)
        L_0x044a:
            r1 = r4
        L_0x044b:
            boolean r11 = r10 instanceof org.telegram.ui.Cells.TextCheckCell
            if (r11 == 0) goto L_0x0456
            org.telegram.ui.Cells.TextCheckCell r10 = (org.telegram.ui.Cells.TextCheckCell) r10
            r11 = r1 ^ 1
            r10.setChecked(r11)
        L_0x0456:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.NotificationsSettingsActivity.lambda$createView$8$NotificationsSettingsActivity(android.view.View, int, float, float):void");
    }

    public /* synthetic */ void lambda$null$4$NotificationsSettingsActivity(DialogInterface dialogInterface, int i) {
        if (!this.reseting) {
            this.reseting = true;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC.TL_account_resetNotifySettings(), new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    NotificationsSettingsActivity.this.lambda$null$3$NotificationsSettingsActivity(tLObject, tL_error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$3$NotificationsSettingsActivity(TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                NotificationsSettingsActivity.this.lambda$null$2$NotificationsSettingsActivity();
            }
        });
    }

    public /* synthetic */ void lambda$null$2$NotificationsSettingsActivity() {
        MessagesController.getInstance(this.currentAccount).enableJoined = true;
        this.reseting = false;
        SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(this.currentAccount).edit();
        edit.clear();
        edit.commit();
        this.exceptionChats.clear();
        this.exceptionUsers.clear();
        this.adapter.notifyDataSetChanged();
        if (getParentActivity() != null) {
            Toast.makeText(getParentActivity(), LocaleController.getString("ResetNotificationsText", NUM), 0).show();
        }
    }

    public /* synthetic */ void lambda$null$6$NotificationsSettingsActivity(int i) {
        this.adapter.notifyItemChanged(i);
    }

    public /* synthetic */ void lambda$null$7$NotificationsSettingsActivity(int i, DialogInterface dialogInterface, int i2) {
        int i3 = 5;
        if (i2 != 1) {
            i3 = i2 == 2 ? 10 : i2 == 3 ? 30 : i2 == 4 ? 60 : i2 == 5 ? 120 : i2 == 6 ? 240 : 0;
        }
        MessagesController.getNotificationsSettings(this.currentAccount).edit().putInt("repeat_messages", i3).commit();
        this.adapter.notifyItemChanged(i);
    }

    public void onActivityResultFragment(int i, int i2, Intent intent) {
        Ringtone ringtone;
        if (i2 == -1) {
            Uri uri = (Uri) intent.getParcelableExtra("android.intent.extra.ringtone.PICKED_URI");
            String str = null;
            if (!(uri == null || (ringtone = RingtoneManager.getRingtone(getParentActivity(), uri)) == null)) {
                if (i == this.callsRingtoneRow) {
                    if (uri.equals(Settings.System.DEFAULT_RINGTONE_URI)) {
                        str = LocaleController.getString("DefaultRingtone", NUM);
                    } else {
                        str = ringtone.getTitle(getParentActivity());
                    }
                } else if (uri.equals(Settings.System.DEFAULT_NOTIFICATION_URI)) {
                    str = LocaleController.getString("SoundDefault", NUM);
                } else {
                    str = ringtone.getTitle(getParentActivity());
                }
                ringtone.stop();
            }
            SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(this.currentAccount).edit();
            if (i == this.callsRingtoneRow) {
                if (str == null || uri == null) {
                    edit.putString("CallsRingtone", "NoSound");
                    edit.putString("CallsRingtonePath", "NoSound");
                } else {
                    edit.putString("CallsRingtone", str);
                    edit.putString("CallsRingtonePath", uri.toString());
                }
            }
            edit.commit();
            this.adapter.notifyItemChanged(i);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:21:0x004b A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x004c  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void showExceptionsAlert(int r7) {
        /*
            r6 = this;
            int r0 = r6.privateRow
            r1 = 0
            if (r7 != r0) goto L_0x001c
            java.util.ArrayList<org.telegram.ui.NotificationsSettingsActivity$NotificationException> r7 = r6.exceptionUsers
            if (r7 == 0) goto L_0x001a
            boolean r0 = r7.isEmpty()
            if (r0 != 0) goto L_0x001a
            int r0 = r7.size()
            java.lang.String r2 = "ChatsException"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r2, r0)
            goto L_0x0049
        L_0x001a:
            r0 = r1
            goto L_0x0049
        L_0x001c:
            int r0 = r6.groupRow
            if (r7 != r0) goto L_0x0035
            java.util.ArrayList<org.telegram.ui.NotificationsSettingsActivity$NotificationException> r7 = r6.exceptionChats
            if (r7 == 0) goto L_0x001a
            boolean r0 = r7.isEmpty()
            if (r0 != 0) goto L_0x001a
            int r0 = r7.size()
            java.lang.String r2 = "Groups"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r2, r0)
            goto L_0x0049
        L_0x0035:
            java.util.ArrayList<org.telegram.ui.NotificationsSettingsActivity$NotificationException> r7 = r6.exceptionChannels
            if (r7 == 0) goto L_0x001a
            boolean r0 = r7.isEmpty()
            if (r0 != 0) goto L_0x001a
            int r0 = r7.size()
            java.lang.String r2 = "Channels"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r2, r0)
        L_0x0049:
            if (r0 != 0) goto L_0x004c
            return
        L_0x004c:
            org.telegram.ui.ActionBar.AlertDialog$Builder r2 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            android.app.Activity r3 = r6.getParentActivity()
            r2.<init>((android.content.Context) r3)
            int r3 = r7.size()
            r4 = 0
            r5 = 1
            if (r3 != r5) goto L_0x0072
            r3 = 2131625856(0x7f0e0780, float:1.8878932E38)
            java.lang.Object[] r5 = new java.lang.Object[r5]
            r5[r4] = r0
            java.lang.String r0 = "NotificationsExceptionsSingleAlert"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r3, r5)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2.setMessage(r0)
            goto L_0x0086
        L_0x0072:
            r3 = 2131625855(0x7f0e077f, float:1.887893E38)
            java.lang.Object[] r5 = new java.lang.Object[r5]
            r5[r4] = r0
            java.lang.String r0 = "NotificationsExceptionsAlert"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r3, r5)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2.setMessage(r0)
        L_0x0086:
            r0 = 2131625854(0x7f0e077e, float:1.8878928E38)
            java.lang.String r3 = "NotificationsExceptions"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r0)
            r2.setTitle(r0)
            r0 = 2131627055(0x7f0e0c2f, float:1.8881364E38)
            java.lang.String r3 = "ViewExceptions"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r0)
            org.telegram.ui.-$$Lambda$NotificationsSettingsActivity$9FhV71oy8_vyXyR3LWFGjX-RReE r3 = new org.telegram.ui.-$$Lambda$NotificationsSettingsActivity$9FhV71oy8_vyXyR3LWFGjX-RReE
            r3.<init>(r7)
            r2.setNeutralButton(r0, r3)
            r7 = 2131625888(0x7f0e07a0, float:1.8878997E38)
            java.lang.String r0 = "OK"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r0, r7)
            r2.setNegativeButton(r7, r1)
            org.telegram.ui.ActionBar.AlertDialog r7 = r2.create()
            r6.showDialog(r7)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.NotificationsSettingsActivity.showExceptionsAlert(int):void");
    }

    public /* synthetic */ void lambda$showExceptionsAlert$9$NotificationsSettingsActivity(ArrayList arrayList, DialogInterface dialogInterface, int i) {
        presentFragment(new NotificationsCustomSettingsActivity(-1, arrayList));
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.adapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.notificationsSettingsUpdated) {
            this.adapter.notifyDataSetChanged();
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            int adapterPosition = viewHolder.getAdapterPosition();
            return (adapterPosition == NotificationsSettingsActivity.this.notificationsSectionRow || adapterPosition == NotificationsSettingsActivity.this.notificationsSection2Row || adapterPosition == NotificationsSettingsActivity.this.inappSectionRow || adapterPosition == NotificationsSettingsActivity.this.eventsSectionRow || adapterPosition == NotificationsSettingsActivity.this.otherSectionRow || adapterPosition == NotificationsSettingsActivity.this.resetSectionRow || adapterPosition == NotificationsSettingsActivity.this.badgeNumberSection || adapterPosition == NotificationsSettingsActivity.this.otherSection2Row || adapterPosition == NotificationsSettingsActivity.this.resetSection2Row || adapterPosition == NotificationsSettingsActivity.this.callsSection2Row || adapterPosition == NotificationsSettingsActivity.this.callsSectionRow || adapterPosition == NotificationsSettingsActivity.this.badgeNumberSection2Row || adapterPosition == NotificationsSettingsActivity.this.accountsSectionRow || adapterPosition == NotificationsSettingsActivity.this.accountsInfoRow || adapterPosition == NotificationsSettingsActivity.this.resetNotificationsSectionRow) ? false : true;
        }

        public int getItemCount() {
            return NotificationsSettingsActivity.this.rowCount;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            TextSettingsCell textSettingsCell;
            View shadowSectionCell;
            if (i == 0) {
                HeaderCell headerCell = new HeaderCell(this.mContext);
                headerCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                textSettingsCell = headerCell;
            } else if (i == 1) {
                TextCheckCell textCheckCell = new TextCheckCell(this.mContext);
                textCheckCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                textSettingsCell = textCheckCell;
            } else if (i == 2) {
                TextDetailSettingsCell textDetailSettingsCell = new TextDetailSettingsCell(this.mContext);
                textDetailSettingsCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                textSettingsCell = textDetailSettingsCell;
            } else if (i != 3) {
                if (i == 4) {
                    shadowSectionCell = new ShadowSectionCell(this.mContext);
                } else if (i != 5) {
                    shadowSectionCell = new TextInfoPrivacyCell(this.mContext);
                    shadowSectionCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                } else {
                    TextSettingsCell textSettingsCell2 = new TextSettingsCell(this.mContext);
                    textSettingsCell2.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    textSettingsCell = textSettingsCell2;
                }
                textSettingsCell = shadowSectionCell;
            } else {
                NotificationsCheckCell notificationsCheckCell = new NotificationsCheckCell(this.mContext);
                notificationsCheckCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                textSettingsCell = notificationsCheckCell;
            }
            return new RecyclerListView.Holder(textSettingsCell);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            String string;
            ArrayList access$4200;
            int i2;
            String str;
            int i3 = 0;
            switch (viewHolder.getItemViewType()) {
                case 0:
                    HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
                    if (i == NotificationsSettingsActivity.this.notificationsSectionRow) {
                        headerCell.setText(LocaleController.getString("NotificationsForChats", NUM));
                        return;
                    } else if (i == NotificationsSettingsActivity.this.inappSectionRow) {
                        headerCell.setText(LocaleController.getString("InAppNotifications", NUM));
                        return;
                    } else if (i == NotificationsSettingsActivity.this.eventsSectionRow) {
                        headerCell.setText(LocaleController.getString("Events", NUM));
                        return;
                    } else if (i == NotificationsSettingsActivity.this.otherSectionRow) {
                        headerCell.setText(LocaleController.getString("NotificationsOther", NUM));
                        return;
                    } else if (i == NotificationsSettingsActivity.this.resetSectionRow) {
                        headerCell.setText(LocaleController.getString("Reset", NUM));
                        return;
                    } else if (i == NotificationsSettingsActivity.this.callsSectionRow) {
                        headerCell.setText(LocaleController.getString("VoipNotificationSettings", NUM));
                        return;
                    } else if (i == NotificationsSettingsActivity.this.badgeNumberSection) {
                        headerCell.setText(LocaleController.getString("BadgeNumber", NUM));
                        return;
                    } else if (i == NotificationsSettingsActivity.this.accountsSectionRow) {
                        headerCell.setText(LocaleController.getString("ShowNotificationsFor", NUM));
                        return;
                    } else {
                        return;
                    }
                case 1:
                    TextCheckCell textCheckCell = (TextCheckCell) viewHolder.itemView;
                    SharedPreferences notificationsSettings = MessagesController.getNotificationsSettings(NotificationsSettingsActivity.this.currentAccount);
                    if (i == NotificationsSettingsActivity.this.inappSoundRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("InAppSounds", NUM), notificationsSettings.getBoolean("EnableInAppSounds", true), true);
                        return;
                    } else if (i == NotificationsSettingsActivity.this.inappVibrateRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("InAppVibrate", NUM), notificationsSettings.getBoolean("EnableInAppVibrate", true), true);
                        return;
                    } else if (i == NotificationsSettingsActivity.this.inappPreviewRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("InAppPreview", NUM), notificationsSettings.getBoolean("EnableInAppPreview", true), true);
                        return;
                    } else if (i == NotificationsSettingsActivity.this.inappPriorityRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("NotificationsImportance", NUM), notificationsSettings.getBoolean("EnableInAppPriority", false), false);
                        return;
                    } else if (i == NotificationsSettingsActivity.this.contactJoinedRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("ContactJoined", NUM), notificationsSettings.getBoolean("EnableContactJoined", true), true);
                        return;
                    } else if (i == NotificationsSettingsActivity.this.pinnedMessageRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("PinnedMessages", NUM), notificationsSettings.getBoolean("PinnedMessages", true), false);
                        return;
                    } else if (i == NotificationsSettingsActivity.this.androidAutoAlertRow) {
                        textCheckCell.setTextAndCheck("Android Auto", notificationsSettings.getBoolean("EnableAutoNotifications", false), true);
                        return;
                    } else if (i == NotificationsSettingsActivity.this.notificationsServiceRow) {
                        textCheckCell.setTextAndValueAndCheck(LocaleController.getString("NotificationsService", NUM), LocaleController.getString("NotificationsServiceInfo", NUM), notificationsSettings.getBoolean("pushService", NotificationsSettingsActivity.this.getMessagesController().keepAliveService), true, true);
                        return;
                    } else if (i == NotificationsSettingsActivity.this.notificationsServiceConnectionRow) {
                        textCheckCell.setTextAndValueAndCheck(LocaleController.getString("NotificationsServiceConnection", NUM), LocaleController.getString("NotificationsServiceConnectionInfo", NUM), notificationsSettings.getBoolean("pushConnection", NotificationsSettingsActivity.this.getMessagesController().backgroundConnection), true, true);
                        return;
                    } else if (i == NotificationsSettingsActivity.this.badgeNumberShowRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("BadgeNumberShow", NUM), NotificationsController.getInstance(NotificationsSettingsActivity.this.currentAccount).showBadgeNumber, true);
                        return;
                    } else if (i == NotificationsSettingsActivity.this.badgeNumberMutedRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("BadgeNumberMutedChats", NUM), NotificationsController.getInstance(NotificationsSettingsActivity.this.currentAccount).showBadgeMuted, true);
                        return;
                    } else if (i == NotificationsSettingsActivity.this.badgeNumberMessagesRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("BadgeNumberUnread", NUM), NotificationsController.getInstance(NotificationsSettingsActivity.this.currentAccount).showBadgeMessages, false);
                        return;
                    } else if (i == NotificationsSettingsActivity.this.inchatSoundRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("InChatSound", NUM), notificationsSettings.getBoolean("EnableInChatSound", true), true);
                        return;
                    } else if (i == NotificationsSettingsActivity.this.callsVibrateRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("Vibrate", NUM), notificationsSettings.getBoolean("EnableCallVibrate", true), true);
                        return;
                    } else if (i == NotificationsSettingsActivity.this.accountsAllRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("AllAccounts", NUM), MessagesController.getGlobalNotificationsSettings().getBoolean("AllAccounts", true), false);
                        return;
                    } else {
                        return;
                    }
                case 2:
                    TextDetailSettingsCell textDetailSettingsCell = (TextDetailSettingsCell) viewHolder.itemView;
                    textDetailSettingsCell.setMultilineDetail(true);
                    if (i == NotificationsSettingsActivity.this.resetNotificationsRow) {
                        textDetailSettingsCell.setTextAndValue(LocaleController.getString("ResetAllNotifications", NUM), LocaleController.getString("UndoAllCustom", NUM), false);
                        return;
                    }
                    return;
                case 3:
                    NotificationsCheckCell notificationsCheckCell = (NotificationsCheckCell) viewHolder.itemView;
                    SharedPreferences notificationsSettings2 = MessagesController.getNotificationsSettings(NotificationsSettingsActivity.this.currentAccount);
                    int currentTime = ConnectionsManager.getInstance(NotificationsSettingsActivity.this.currentAccount).getCurrentTime();
                    if (i == NotificationsSettingsActivity.this.privateRow) {
                        string = LocaleController.getString("NotificationsPrivateChats", NUM);
                        access$4200 = NotificationsSettingsActivity.this.exceptionUsers;
                        i2 = notificationsSettings2.getInt("EnableAll2", 0);
                    } else if (i == NotificationsSettingsActivity.this.groupRow) {
                        string = LocaleController.getString("NotificationsGroups", NUM);
                        access$4200 = NotificationsSettingsActivity.this.exceptionChats;
                        i2 = notificationsSettings2.getInt("EnableGroup2", 0);
                    } else {
                        string = LocaleController.getString("NotificationsChannels", NUM);
                        access$4200 = NotificationsSettingsActivity.this.exceptionChannels;
                        i2 = notificationsSettings2.getInt("EnableChannel2", 0);
                    }
                    String str2 = string;
                    boolean z = i2 < currentTime;
                    int i4 = (!z && i2 - 31536000 < currentTime) ? 2 : 0;
                    StringBuilder sb = new StringBuilder();
                    if (access$4200 == null || access$4200.isEmpty()) {
                        sb.append(LocaleController.getString("TapToChange", NUM));
                    } else {
                        z = i2 < currentTime;
                        if (z) {
                            sb.append(LocaleController.getString("NotificationsOn", NUM));
                        } else if (i2 - 31536000 >= currentTime) {
                            sb.append(LocaleController.getString("NotificationsOff", NUM));
                        } else {
                            sb.append(LocaleController.formatString("NotificationsOffUntil", NUM, LocaleController.stringForMessageListDate((long) i2)));
                        }
                        if (sb.length() != 0) {
                            sb.append(", ");
                        }
                        sb.append(LocaleController.formatPluralString("Exception", access$4200.size()));
                    }
                    notificationsCheckCell.setTextAndValueAndCheck(str2, sb, z, i4, i != NotificationsSettingsActivity.this.channelsRow);
                    return;
                case 4:
                    if (i == NotificationsSettingsActivity.this.resetNotificationsSectionRow) {
                        viewHolder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                        return;
                    } else {
                        viewHolder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                        return;
                    }
                case 5:
                    TextSettingsCell textSettingsCell = (TextSettingsCell) viewHolder.itemView;
                    SharedPreferences notificationsSettings3 = MessagesController.getNotificationsSettings(NotificationsSettingsActivity.this.currentAccount);
                    if (i == NotificationsSettingsActivity.this.callsRingtoneRow) {
                        String string2 = notificationsSettings3.getString("CallsRingtone", LocaleController.getString("DefaultRingtone", NUM));
                        if (string2.equals("NoSound")) {
                            string2 = LocaleController.getString("NoSound", NUM);
                        }
                        textSettingsCell.setTextAndValue(LocaleController.getString("VoipSettingsRingtone", NUM), string2, false);
                        return;
                    } else if (i == NotificationsSettingsActivity.this.callsVibrateRow) {
                        if (i == NotificationsSettingsActivity.this.callsVibrateRow) {
                            i3 = notificationsSettings3.getInt("vibrate_calls", 0);
                        }
                        if (i3 == 0) {
                            textSettingsCell.setTextAndValue(LocaleController.getString("Vibrate", NUM), LocaleController.getString("VibrationDefault", NUM), true);
                            return;
                        } else if (i3 == 1) {
                            textSettingsCell.setTextAndValue(LocaleController.getString("Vibrate", NUM), LocaleController.getString("Short", NUM), true);
                            return;
                        } else if (i3 == 2) {
                            textSettingsCell.setTextAndValue(LocaleController.getString("Vibrate", NUM), LocaleController.getString("VibrationDisabled", NUM), true);
                            return;
                        } else if (i3 == 3) {
                            textSettingsCell.setTextAndValue(LocaleController.getString("Vibrate", NUM), LocaleController.getString("Long", NUM), true);
                            return;
                        } else if (i3 == 4) {
                            textSettingsCell.setTextAndValue(LocaleController.getString("Vibrate", NUM), LocaleController.getString("OnlyIfSilent", NUM), true);
                            return;
                        } else {
                            return;
                        }
                    } else if (i == NotificationsSettingsActivity.this.repeatRow) {
                        int i5 = notificationsSettings3.getInt("repeat_messages", 60);
                        if (i5 == 0) {
                            str = LocaleController.getString("RepeatNotificationsNever", NUM);
                        } else if (i5 < 60) {
                            str = LocaleController.formatPluralString("Minutes", i5);
                        } else {
                            str = LocaleController.formatPluralString("Hours", i5 / 60);
                        }
                        textSettingsCell.setTextAndValue(LocaleController.getString("RepeatNotifications", NUM), str, false);
                        return;
                    } else {
                        return;
                    }
                case 6:
                    TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
                    if (i == NotificationsSettingsActivity.this.accountsInfoRow) {
                        textInfoPrivacyCell.setText(LocaleController.getString("ShowNotificationsForInfo", NUM));
                        return;
                    }
                    return;
                default:
                    return;
            }
        }

        public int getItemViewType(int i) {
            if (i == NotificationsSettingsActivity.this.eventsSectionRow || i == NotificationsSettingsActivity.this.otherSectionRow || i == NotificationsSettingsActivity.this.resetSectionRow || i == NotificationsSettingsActivity.this.callsSectionRow || i == NotificationsSettingsActivity.this.badgeNumberSection || i == NotificationsSettingsActivity.this.inappSectionRow || i == NotificationsSettingsActivity.this.notificationsSectionRow || i == NotificationsSettingsActivity.this.accountsSectionRow) {
                return 0;
            }
            if (i == NotificationsSettingsActivity.this.inappSoundRow || i == NotificationsSettingsActivity.this.inappVibrateRow || i == NotificationsSettingsActivity.this.notificationsServiceConnectionRow || i == NotificationsSettingsActivity.this.inappPreviewRow || i == NotificationsSettingsActivity.this.contactJoinedRow || i == NotificationsSettingsActivity.this.pinnedMessageRow || i == NotificationsSettingsActivity.this.notificationsServiceRow || i == NotificationsSettingsActivity.this.badgeNumberMutedRow || i == NotificationsSettingsActivity.this.badgeNumberMessagesRow || i == NotificationsSettingsActivity.this.badgeNumberShowRow || i == NotificationsSettingsActivity.this.inappPriorityRow || i == NotificationsSettingsActivity.this.inchatSoundRow || i == NotificationsSettingsActivity.this.androidAutoAlertRow || i == NotificationsSettingsActivity.this.accountsAllRow) {
                return 1;
            }
            if (i == NotificationsSettingsActivity.this.resetNotificationsRow) {
                return 2;
            }
            if (i == NotificationsSettingsActivity.this.privateRow || i == NotificationsSettingsActivity.this.groupRow || i == NotificationsSettingsActivity.this.channelsRow) {
                return 3;
            }
            if (i == NotificationsSettingsActivity.this.eventsSection2Row || i == NotificationsSettingsActivity.this.notificationsSection2Row || i == NotificationsSettingsActivity.this.otherSection2Row || i == NotificationsSettingsActivity.this.resetSection2Row || i == NotificationsSettingsActivity.this.callsSection2Row || i == NotificationsSettingsActivity.this.badgeNumberSection2Row || i == NotificationsSettingsActivity.this.resetNotificationsSectionRow) {
                return 4;
            }
            return i == NotificationsSettingsActivity.this.accountsInfoRow ? 6 : 5;
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        return new ThemeDescription[]{new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{HeaderCell.class, TextCheckCell.class, TextDetailSettingsCell.class, TextSettingsCell.class, NotificationsCheckCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"), new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"), new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"), new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"), new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"), new ThemeDescription((View) this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"), new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"), new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"), new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrack"), new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackChecked"), new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"), new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"), new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrack"), new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackChecked"), new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"), new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteValueText"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"), new ThemeDescription((View) this.listView, 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"), new ThemeDescription((View) this.listView, 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"), new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_LINKCOLOR, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteLinkText")};
    }
}
