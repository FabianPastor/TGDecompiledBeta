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
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
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
import org.telegram.ui.Components.AlertsCreator;
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

    public boolean onFragmentCreate() {
        MessagesController.getInstance(this.currentAccount).loadSignUpNotificationsSettings();
        loadExceptions();
        if (UserConfig.getActivatedAccountsCount() > 1) {
            int i = this.rowCount;
            int i2 = i + 1;
            this.rowCount = i2;
            this.accountsSectionRow = i;
            int i3 = i2 + 1;
            this.rowCount = i3;
            this.accountsAllRow = i2;
            this.rowCount = i3 + 1;
            this.accountsInfoRow = i3;
        } else {
            this.accountsSectionRow = -1;
            this.accountsAllRow = -1;
            this.accountsInfoRow = -1;
        }
        int i4 = this.rowCount;
        int i5 = i4 + 1;
        this.rowCount = i5;
        this.notificationsSectionRow = i4;
        int i6 = i5 + 1;
        this.rowCount = i6;
        this.privateRow = i5;
        int i7 = i6 + 1;
        this.rowCount = i7;
        this.groupRow = i6;
        int i8 = i7 + 1;
        this.rowCount = i8;
        this.channelsRow = i7;
        int i9 = i8 + 1;
        this.rowCount = i9;
        this.notificationsSection2Row = i8;
        int i10 = i9 + 1;
        this.rowCount = i10;
        this.callsSectionRow = i9;
        int i11 = i10 + 1;
        this.rowCount = i11;
        this.callsVibrateRow = i10;
        int i12 = i11 + 1;
        this.rowCount = i12;
        this.callsRingtoneRow = i11;
        int i13 = i12 + 1;
        this.rowCount = i13;
        this.eventsSection2Row = i12;
        int i14 = i13 + 1;
        this.rowCount = i14;
        this.badgeNumberSection = i13;
        int i15 = i14 + 1;
        this.rowCount = i15;
        this.badgeNumberShowRow = i14;
        int i16 = i15 + 1;
        this.rowCount = i16;
        this.badgeNumberMutedRow = i15;
        int i17 = i16 + 1;
        this.rowCount = i17;
        this.badgeNumberMessagesRow = i16;
        int i18 = i17 + 1;
        this.rowCount = i18;
        this.badgeNumberSection2Row = i17;
        int i19 = i18 + 1;
        this.rowCount = i19;
        this.inappSectionRow = i18;
        int i20 = i19 + 1;
        this.rowCount = i20;
        this.inappSoundRow = i19;
        int i21 = i20 + 1;
        this.rowCount = i21;
        this.inappVibrateRow = i20;
        int i22 = i21 + 1;
        this.rowCount = i22;
        this.inappPreviewRow = i21;
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
        int i25 = i24 + 1;
        this.rowCount = i25;
        this.callsSection2Row = i24;
        int i26 = i25 + 1;
        this.rowCount = i26;
        this.eventsSectionRow = i25;
        int i27 = i26 + 1;
        this.rowCount = i27;
        this.contactJoinedRow = i26;
        int i28 = i27 + 1;
        this.rowCount = i28;
        this.pinnedMessageRow = i27;
        int i29 = i28 + 1;
        this.rowCount = i29;
        this.otherSection2Row = i28;
        int i30 = i29 + 1;
        this.rowCount = i30;
        this.otherSectionRow = i29;
        int i31 = i30 + 1;
        this.rowCount = i31;
        this.notificationsServiceRow = i30;
        int i32 = i31 + 1;
        this.rowCount = i32;
        this.notificationsServiceConnectionRow = i31;
        this.androidAutoAlertRow = -1;
        int i33 = i32 + 1;
        this.rowCount = i33;
        this.repeatRow = i32;
        int i34 = i33 + 1;
        this.rowCount = i34;
        this.resetSection2Row = i33;
        int i35 = i34 + 1;
        this.rowCount = i35;
        this.resetSectionRow = i34;
        int i36 = i35 + 1;
        this.rowCount = i36;
        this.resetNotificationsRow = i35;
        this.rowCount = i36 + 1;
        this.resetNotificationsSectionRow = i36;
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.notificationsSettingsUpdated);
        return super.onFragmentCreate();
    }

    private void loadExceptions() {
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new NotificationsSettingsActivity$$ExternalSyntheticLambda4(this));
    }

    /* JADX WARNING: Removed duplicated region for block: B:102:0x02b6  */
    /* JADX WARNING: Removed duplicated region for block: B:121:0x0302  */
    /* JADX WARNING: Removed duplicated region for block: B:127:0x031c A[LOOP:3: B:126:0x031a->B:127:0x031c, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:130:0x0336  */
    /* renamed from: lambda$loadExceptions$1$org-telegram-ui-NotificationsSettingsActivity  reason: not valid java name */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void m2670x4800CLASSNAME() {
        /*
            r26 = this;
            r9 = r26
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r10 = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r11 = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r12 = r0
            android.util.LongSparseArray r0 = new android.util.LongSparseArray
            r0.<init>()
            r13 = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r14 = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r15 = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r8 = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r7 = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r6 = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r5 = r0
            int r0 = r9.currentAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            long r3 = r0.clientUserId
            int r0 = r9.currentAccount
            android.content.SharedPreferences r2 = org.telegram.messenger.MessagesController.getNotificationsSettings(r0)
            java.util.Map r1 = r2.getAll()
            java.util.Set r0 = r1.entrySet()
            java.util.Iterator r0 = r0.iterator()
        L_0x0058:
            boolean r16 = r0.hasNext()
            if (r16 == 0) goto L_0x0223
            java.lang.Object r16 = r0.next()
            java.util.Map$Entry r16 = (java.util.Map.Entry) r16
            java.lang.Object r17 = r16.getKey()
            r18 = r0
            r0 = r17
            java.lang.String r0 = (java.lang.String) r0
            r17 = r6
            java.lang.String r6 = "notify2_"
            boolean r19 = r0.startsWith(r6)
            if (r19 == 0) goto L_0x0209
            r19 = r7
            java.lang.String r7 = ""
            java.lang.String r0 = r0.replace(r6, r7)
            java.lang.Long r6 = org.telegram.messenger.Utilities.parseLong(r0)
            long r6 = r6.longValue()
            r20 = 0
            int r22 = (r6 > r20 ? 1 : (r6 == r20 ? 0 : -1))
            if (r22 == 0) goto L_0x01fe
            int r20 = (r6 > r3 ? 1 : (r6 == r3 ? 0 : -1))
            if (r20 == 0) goto L_0x01fe
            org.telegram.ui.NotificationsSettingsActivity$NotificationException r20 = new org.telegram.ui.NotificationsSettingsActivity$NotificationException
            r20.<init>()
            r21 = r20
            r22 = r3
            r3 = r21
            r3.did = r6
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r20 = r5
            java.lang.String r5 = "custom_"
            r4.append(r5)
            r4.append(r6)
            java.lang.String r4 = r4.toString()
            r5 = 0
            boolean r4 = r2.getBoolean(r4, r5)
            r3.hasCustom = r4
            java.lang.Object r4 = r16.getValue()
            java.lang.Integer r4 = (java.lang.Integer) r4
            int r4 = r4.intValue()
            r3.notify = r4
            int r4 = r3.notify
            if (r4 == 0) goto L_0x00e8
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "notifyuntil_"
            r4.append(r5)
            r4.append(r0)
            java.lang.String r4 = r4.toString()
            java.lang.Object r4 = r1.get(r4)
            java.lang.Integer r4 = (java.lang.Integer) r4
            if (r4 == 0) goto L_0x00e8
            int r5 = r4.intValue()
            r3.muteUntil = r5
        L_0x00e8:
            boolean r4 = org.telegram.messenger.DialogObject.isEncryptedDialog(r6)
            if (r4 == 0) goto L_0x0154
            int r4 = org.telegram.messenger.DialogObject.getEncryptedChatId(r6)
            int r5 = r9.currentAccount
            org.telegram.messenger.MessagesController r5 = org.telegram.messenger.MessagesController.getInstance(r5)
            r21 = r0
            java.lang.Integer r0 = java.lang.Integer.valueOf(r4)
            org.telegram.tgnet.TLRPC$EncryptedChat r0 = r5.getEncryptedChat(r0)
            if (r0 != 0) goto L_0x0114
            java.lang.Integer r5 = java.lang.Integer.valueOf(r4)
            r8.add(r5)
            r13.put(r6, r3)
            r25 = r1
            r24 = r2
            r2 = r4
            goto L_0x014f
        L_0x0114:
            int r5 = r9.currentAccount
            org.telegram.messenger.MessagesController r5 = org.telegram.messenger.MessagesController.getInstance(r5)
            r25 = r1
            r24 = r2
            long r1 = r0.user_id
            java.lang.Long r1 = java.lang.Long.valueOf(r1)
            org.telegram.tgnet.TLRPC$User r1 = r5.getUser(r1)
            if (r1 != 0) goto L_0x013a
            r2 = r4
            long r4 = r0.user_id
            java.lang.Long r4 = java.lang.Long.valueOf(r4)
            r14.add(r4)
            long r4 = r0.user_id
            r13.put(r4, r3)
            goto L_0x014f
        L_0x013a:
            r2 = r4
            boolean r4 = r1.deleted
            if (r4 == 0) goto L_0x014f
            r6 = r17
            r0 = r18
            r7 = r19
            r5 = r20
            r3 = r22
            r2 = r24
            r1 = r25
            goto L_0x0058
        L_0x014f:
            r10.add(r3)
            goto L_0x0213
        L_0x0154:
            r21 = r0
            r25 = r1
            r24 = r2
            boolean r0 = org.telegram.messenger.DialogObject.isUserDialog(r6)
            if (r0 == 0) goto L_0x0194
            int r0 = r9.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            java.lang.Long r1 = java.lang.Long.valueOf(r6)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r1)
            if (r0 != 0) goto L_0x017b
            java.lang.Long r1 = java.lang.Long.valueOf(r6)
            r14.add(r1)
            r13.put(r6, r3)
            goto L_0x018f
        L_0x017b:
            boolean r1 = r0.deleted
            if (r1 == 0) goto L_0x018f
            r6 = r17
            r0 = r18
            r7 = r19
            r5 = r20
            r3 = r22
            r2 = r24
            r1 = r25
            goto L_0x0058
        L_0x018f:
            r10.add(r3)
            goto L_0x0213
        L_0x0194:
            int r0 = r9.currentAccount
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
            long r1 = -r6
            java.lang.Long r1 = java.lang.Long.valueOf(r1)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r1)
            if (r0 != 0) goto L_0x01c0
            long r1 = -r6
            java.lang.Long r1 = java.lang.Long.valueOf(r1)
            r15.add(r1)
            r13.put(r6, r3)
            r6 = r17
            r0 = r18
            r7 = r19
            r5 = r20
            r3 = r22
            r2 = r24
            r1 = r25
            goto L_0x0058
        L_0x01c0:
            boolean r1 = r0.left
            if (r1 != 0) goto L_0x01ee
            boolean r1 = r0.kicked
            if (r1 != 0) goto L_0x01ee
            org.telegram.tgnet.TLRPC$InputChannel r1 = r0.migrated_to
            if (r1 == 0) goto L_0x01dc
            r6 = r17
            r0 = r18
            r7 = r19
            r5 = r20
            r3 = r22
            r2 = r24
            r1 = r25
            goto L_0x0058
        L_0x01dc:
            boolean r1 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r1 == 0) goto L_0x01ea
            boolean r1 = r0.megagroup
            if (r1 != 0) goto L_0x01ea
            r12.add(r3)
            goto L_0x0213
        L_0x01ea:
            r11.add(r3)
            goto L_0x0213
        L_0x01ee:
            r6 = r17
            r0 = r18
            r7 = r19
            r5 = r20
            r3 = r22
            r2 = r24
            r1 = r25
            goto L_0x0058
        L_0x01fe:
            r21 = r0
            r25 = r1
            r24 = r2
            r22 = r3
            r20 = r5
            goto L_0x0213
        L_0x0209:
            r25 = r1
            r24 = r2
            r22 = r3
            r20 = r5
            r19 = r7
        L_0x0213:
            r6 = r17
            r0 = r18
            r7 = r19
            r5 = r20
            r3 = r22
            r2 = r24
            r1 = r25
            goto L_0x0058
        L_0x0223:
            r25 = r1
            r24 = r2
            r22 = r3
            r20 = r5
            r17 = r6
            r19 = r7
            int r0 = r13.size()
            if (r0 == 0) goto L_0x0359
            boolean r0 = r8.isEmpty()     // Catch:{ Exception -> 0x02a5 }
            java.lang.String r1 = ","
            if (r0 != 0) goto L_0x025c
            int r0 = r9.currentAccount     // Catch:{ Exception -> 0x0254 }
            org.telegram.messenger.MessagesStorage r0 = org.telegram.messenger.MessagesStorage.getInstance(r0)     // Catch:{ Exception -> 0x0254 }
            java.lang.String r2 = android.text.TextUtils.join(r1, r8)     // Catch:{ Exception -> 0x0254 }
            r5 = r20
            r0.getEncryptedChatsInternal(r2, r5, r14)     // Catch:{ Exception -> 0x024d }
            goto L_0x025e
        L_0x024d:
            r0 = move-exception
            r6 = r17
            r7 = r19
            goto L_0x02ac
        L_0x0254:
            r0 = move-exception
            r5 = r20
            r6 = r17
            r7 = r19
            goto L_0x02ac
        L_0x025c:
            r5 = r20
        L_0x025e:
            boolean r0 = r14.isEmpty()     // Catch:{ Exception -> 0x029f }
            if (r0 != 0) goto L_0x027e
            int r0 = r9.currentAccount     // Catch:{ Exception -> 0x0278 }
            org.telegram.messenger.MessagesStorage r0 = org.telegram.messenger.MessagesStorage.getInstance(r0)     // Catch:{ Exception -> 0x0278 }
            java.lang.String r2 = android.text.TextUtils.join(r1, r14)     // Catch:{ Exception -> 0x0278 }
            r7 = r19
            r0.getUsersInternal(r2, r7)     // Catch:{ Exception -> 0x0274 }
            goto L_0x0280
        L_0x0274:
            r0 = move-exception
            r6 = r17
            goto L_0x02ac
        L_0x0278:
            r0 = move-exception
            r7 = r19
            r6 = r17
            goto L_0x02ac
        L_0x027e:
            r7 = r19
        L_0x0280:
            boolean r0 = r15.isEmpty()     // Catch:{ Exception -> 0x029b }
            if (r0 != 0) goto L_0x0298
            int r0 = r9.currentAccount     // Catch:{ Exception -> 0x029b }
            org.telegram.messenger.MessagesStorage r0 = org.telegram.messenger.MessagesStorage.getInstance(r0)     // Catch:{ Exception -> 0x029b }
            java.lang.String r1 = android.text.TextUtils.join(r1, r15)     // Catch:{ Exception -> 0x029b }
            r6 = r17
            r0.getChatsInternal(r1, r6)     // Catch:{ Exception -> 0x0296 }
            goto L_0x029a
        L_0x0296:
            r0 = move-exception
            goto L_0x02ac
        L_0x0298:
            r6 = r17
        L_0x029a:
            goto L_0x02af
        L_0x029b:
            r0 = move-exception
            r6 = r17
            goto L_0x02ac
        L_0x029f:
            r0 = move-exception
            r6 = r17
            r7 = r19
            goto L_0x02ac
        L_0x02a5:
            r0 = move-exception
            r6 = r17
            r7 = r19
            r5 = r20
        L_0x02ac:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x02af:
            r0 = 0
            int r1 = r6.size()
        L_0x02b4:
            if (r0 >= r1) goto L_0x02f9
            java.lang.Object r2 = r6.get(r0)
            org.telegram.tgnet.TLRPC$Chat r2 = (org.telegram.tgnet.TLRPC.Chat) r2
            boolean r3 = r2.left
            if (r3 != 0) goto L_0x02f0
            boolean r3 = r2.kicked
            if (r3 != 0) goto L_0x02f0
            org.telegram.tgnet.TLRPC$InputChannel r3 = r2.migrated_to
            if (r3 == 0) goto L_0x02cb
            r16 = r8
            goto L_0x02f2
        L_0x02cb:
            long r3 = r2.id
            long r3 = -r3
            java.lang.Object r3 = r13.get(r3)
            org.telegram.ui.NotificationsSettingsActivity$NotificationException r3 = (org.telegram.ui.NotificationsSettingsActivity.NotificationException) r3
            r16 = r8
            long r8 = r2.id
            long r8 = -r8
            r13.remove(r8)
            if (r3 == 0) goto L_0x02f2
            boolean r4 = org.telegram.messenger.ChatObject.isChannel(r2)
            if (r4 == 0) goto L_0x02ec
            boolean r4 = r2.megagroup
            if (r4 != 0) goto L_0x02ec
            r12.add(r3)
            goto L_0x02f2
        L_0x02ec:
            r11.add(r3)
            goto L_0x02f2
        L_0x02f0:
            r16 = r8
        L_0x02f2:
            int r0 = r0 + 1
            r9 = r26
            r8 = r16
            goto L_0x02b4
        L_0x02f9:
            r16 = r8
            r0 = 0
            int r1 = r7.size()
        L_0x0300:
            if (r0 >= r1) goto L_0x0315
            java.lang.Object r2 = r7.get(r0)
            org.telegram.tgnet.TLRPC$User r2 = (org.telegram.tgnet.TLRPC.User) r2
            boolean r3 = r2.deleted
            if (r3 == 0) goto L_0x030d
            goto L_0x0312
        L_0x030d:
            long r3 = r2.id
            r13.remove(r3)
        L_0x0312:
            int r0 = r0 + 1
            goto L_0x0300
        L_0x0315:
            r0 = 0
            int r1 = r5.size()
        L_0x031a:
            if (r0 >= r1) goto L_0x032f
            java.lang.Object r2 = r5.get(r0)
            org.telegram.tgnet.TLRPC$EncryptedChat r2 = (org.telegram.tgnet.TLRPC.EncryptedChat) r2
            int r3 = r2.id
            long r3 = (long) r3
            long r3 = org.telegram.messenger.DialogObject.makeEncryptedDialogId(r3)
            r13.remove(r3)
            int r0 = r0 + 1
            goto L_0x031a
        L_0x032f:
            r0 = 0
            int r1 = r13.size()
        L_0x0334:
            if (r0 >= r1) goto L_0x0361
            long r2 = r13.keyAt(r0)
            boolean r4 = org.telegram.messenger.DialogObject.isChatDialog(r2)
            if (r4 == 0) goto L_0x034f
            java.lang.Object r4 = r13.valueAt(r0)
            r11.remove(r4)
            java.lang.Object r4 = r13.valueAt(r0)
            r12.remove(r4)
            goto L_0x0356
        L_0x034f:
            java.lang.Object r4 = r13.valueAt(r0)
            r10.remove(r4)
        L_0x0356:
            int r0 = r0 + 1
            goto L_0x0334
        L_0x0359:
            r16 = r8
            r6 = r17
            r7 = r19
            r5 = r20
        L_0x0361:
            org.telegram.ui.NotificationsSettingsActivity$$ExternalSyntheticLambda6 r0 = new org.telegram.ui.NotificationsSettingsActivity$$ExternalSyntheticLambda6
            r9 = r25
            r1 = r0
            r17 = r24
            r2 = r26
            r18 = r22
            r3 = r7
            r4 = r6
            r20 = r5
            r21 = r6
            r6 = r10
            r22 = r7
            r7 = r11
            r8 = r12
            r1.<init>(r2, r3, r4, r5, r6, r7, r8)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.NotificationsSettingsActivity.m2670x4800CLASSNAME():void");
    }

    /* renamed from: lambda$loadExceptions$0$org-telegram-ui-NotificationsSettingsActivity  reason: not valid java name */
    public /* synthetic */ void m2669xd286a153(ArrayList users, ArrayList chats, ArrayList encryptedChats, ArrayList usersResult, ArrayList chatsResult, ArrayList channelsResult) {
        MessagesController.getInstance(this.currentAccount).putUsers(users, true);
        MessagesController.getInstance(this.currentAccount).putChats(chats, true);
        MessagesController.getInstance(this.currentAccount).putEncryptedChats(encryptedChats, true);
        this.exceptionUsers = usersResult;
        this.exceptionChats = chatsResult;
        this.exceptionChannels = channelsResult;
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
            public void onItemClick(int id) {
                if (id == -1) {
                    NotificationsSettingsActivity.this.finishFragment();
                }
            }
        });
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setItemAnimator((RecyclerView.ItemAnimator) null);
        this.listView.setLayoutAnimation((LayoutAnimationController) null);
        RecyclerListView recyclerListView2 = this.listView;
        AnonymousClass2 r3 = new LinearLayoutManager(context, 1, false) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        this.layoutManager = r3;
        recyclerListView2.setLayoutManager(r3);
        this.listView.setVerticalScrollBarEnabled(false);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        RecyclerListView recyclerListView3 = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.adapter = listAdapter;
        recyclerListView3.setAdapter(listAdapter);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListenerExtended) new NotificationsSettingsActivity$$ExternalSyntheticLambda9(this));
        return this.fragmentView;
    }

    /* renamed from: lambda$createView$8$org-telegram-ui-NotificationsSettingsActivity  reason: not valid java name */
    public /* synthetic */ void m2668xbde8ae12(View view, int position, float x, float y) {
        ArrayList<NotificationException> exceptions;
        int type;
        View view2 = view;
        int i = position;
        boolean enabled = false;
        if (getParentActivity() != null) {
            int i2 = this.privateRow;
            if (i == i2 || i == this.groupRow || i == this.channelsRow) {
                if (i == i2) {
                    type = 1;
                    exceptions = this.exceptionUsers;
                } else if (i == this.groupRow) {
                    type = 0;
                    exceptions = this.exceptionChats;
                } else {
                    type = 2;
                    exceptions = this.exceptionChannels;
                }
                if (exceptions != null) {
                    NotificationsCheckCell checkCell = (NotificationsCheckCell) view2;
                    enabled = getNotificationsController().isGlobalNotificationsEnabled(type);
                    if ((!LocaleController.isRTL || x > ((float) AndroidUtilities.dp(76.0f))) && (LocaleController.isRTL || x < ((float) (view.getMeasuredWidth() - AndroidUtilities.dp(76.0f))))) {
                        presentFragment(new NotificationsCustomSettingsActivity(type, exceptions));
                    } else {
                        getNotificationsController().setGlobalNotificationsEnabled(type, !enabled ? 0 : Integer.MAX_VALUE);
                        showExceptionsAlert(i);
                        checkCell.setChecked(!enabled, 0);
                        this.adapter.notifyItemChanged(i);
                    }
                } else {
                    return;
                }
            } else if (i == this.callsRingtoneRow) {
                try {
                    SharedPreferences preferences = MessagesController.getNotificationsSettings(this.currentAccount);
                    Intent tmpIntent = new Intent("android.intent.action.RINGTONE_PICKER");
                    tmpIntent.putExtra("android.intent.extra.ringtone.TYPE", 1);
                    tmpIntent.putExtra("android.intent.extra.ringtone.SHOW_DEFAULT", true);
                    tmpIntent.putExtra("android.intent.extra.ringtone.SHOW_SILENT", true);
                    tmpIntent.putExtra("android.intent.extra.ringtone.DEFAULT_URI", RingtoneManager.getDefaultUri(1));
                    Uri currentSound = null;
                    String defaultPath = null;
                    Uri defaultUri = Settings.System.DEFAULT_RINGTONE_URI;
                    if (defaultUri != null) {
                        defaultPath = defaultUri.getPath();
                    }
                    String path = preferences.getString("CallsRingtonePath", defaultPath);
                    if (path != null && !path.equals("NoSound")) {
                        currentSound = path.equals(defaultPath) ? defaultUri : Uri.parse(path);
                    }
                    tmpIntent.putExtra("android.intent.extra.ringtone.EXISTING_URI", currentSound);
                    startActivityForResult(tmpIntent, i);
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            } else if (i == this.resetNotificationsRow) {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                builder.setTitle(LocaleController.getString("ResetNotificationsAlertTitle", NUM));
                builder.setMessage(LocaleController.getString("ResetNotificationsAlert", NUM));
                builder.setPositiveButton(LocaleController.getString("Reset", NUM), new NotificationsSettingsActivity$$ExternalSyntheticLambda0(this));
                builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                AlertDialog alertDialog = builder.create();
                showDialog(alertDialog);
                TextView button = (TextView) alertDialog.getButton(-1);
                if (button != null) {
                    button.setTextColor(Theme.getColor("dialogTextRed2"));
                }
            } else if (i == this.inappSoundRow) {
                SharedPreferences preferences2 = MessagesController.getNotificationsSettings(this.currentAccount);
                SharedPreferences.Editor editor = preferences2.edit();
                enabled = preferences2.getBoolean("EnableInAppSounds", true);
                editor.putBoolean("EnableInAppSounds", !enabled);
                editor.commit();
            } else if (i == this.inappVibrateRow) {
                SharedPreferences preferences3 = MessagesController.getNotificationsSettings(this.currentAccount);
                SharedPreferences.Editor editor2 = preferences3.edit();
                enabled = preferences3.getBoolean("EnableInAppVibrate", true);
                editor2.putBoolean("EnableInAppVibrate", !enabled);
                editor2.commit();
            } else if (i == this.inappPreviewRow) {
                SharedPreferences preferences4 = MessagesController.getNotificationsSettings(this.currentAccount);
                SharedPreferences.Editor editor3 = preferences4.edit();
                enabled = preferences4.getBoolean("EnableInAppPreview", true);
                editor3.putBoolean("EnableInAppPreview", !enabled);
                editor3.commit();
            } else if (i == this.inchatSoundRow) {
                SharedPreferences preferences5 = MessagesController.getNotificationsSettings(this.currentAccount);
                SharedPreferences.Editor editor4 = preferences5.edit();
                enabled = preferences5.getBoolean("EnableInChatSound", true);
                editor4.putBoolean("EnableInChatSound", !enabled);
                editor4.commit();
                getNotificationsController().setInChatSoundEnabled(!enabled);
            } else if (i == this.inappPriorityRow) {
                SharedPreferences preferences6 = MessagesController.getNotificationsSettings(this.currentAccount);
                SharedPreferences.Editor editor5 = preferences6.edit();
                enabled = preferences6.getBoolean("EnableInAppPriority", false);
                editor5.putBoolean("EnableInAppPriority", !enabled);
                editor5.commit();
            } else if (i == this.contactJoinedRow) {
                SharedPreferences preferences7 = MessagesController.getNotificationsSettings(this.currentAccount);
                SharedPreferences.Editor editor6 = preferences7.edit();
                enabled = preferences7.getBoolean("EnableContactJoined", true);
                MessagesController.getInstance(this.currentAccount).enableJoined = !enabled;
                editor6.putBoolean("EnableContactJoined", !enabled);
                editor6.commit();
                TLRPC.TL_account_setContactSignUpNotification req = new TLRPC.TL_account_setContactSignUpNotification();
                req.silent = enabled;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, NotificationsSettingsActivity$$ExternalSyntheticLambda8.INSTANCE);
            } else if (i == this.pinnedMessageRow) {
                SharedPreferences preferences8 = MessagesController.getNotificationsSettings(this.currentAccount);
                SharedPreferences.Editor editor7 = preferences8.edit();
                enabled = preferences8.getBoolean("PinnedMessages", true);
                editor7.putBoolean("PinnedMessages", !enabled);
                editor7.commit();
            } else if (i == this.androidAutoAlertRow) {
                SharedPreferences preferences9 = MessagesController.getNotificationsSettings(this.currentAccount);
                SharedPreferences.Editor editor8 = preferences9.edit();
                enabled = preferences9.getBoolean("EnableAutoNotifications", false);
                editor8.putBoolean("EnableAutoNotifications", !enabled);
                editor8.commit();
            } else if (i == this.badgeNumberShowRow) {
                SharedPreferences.Editor editor9 = MessagesController.getNotificationsSettings(this.currentAccount).edit();
                enabled = getNotificationsController().showBadgeNumber;
                getNotificationsController().showBadgeNumber = !enabled;
                editor9.putBoolean("badgeNumber", getNotificationsController().showBadgeNumber);
                editor9.commit();
                getNotificationsController().updateBadge();
            } else if (i == this.badgeNumberMutedRow) {
                SharedPreferences.Editor editor10 = MessagesController.getNotificationsSettings(this.currentAccount).edit();
                enabled = getNotificationsController().showBadgeMuted;
                getNotificationsController().showBadgeMuted = !enabled;
                editor10.putBoolean("badgeNumberMuted", getNotificationsController().showBadgeMuted);
                editor10.commit();
                getNotificationsController().updateBadge();
                getMessagesStorage().updateMutedDialogsFiltersCounters();
            } else if (i == this.badgeNumberMessagesRow) {
                SharedPreferences.Editor editor11 = MessagesController.getNotificationsSettings(this.currentAccount).edit();
                enabled = getNotificationsController().showBadgeMessages;
                getNotificationsController().showBadgeMessages = !enabled;
                editor11.putBoolean("badgeNumberMessages", getNotificationsController().showBadgeMessages);
                editor11.commit();
                getNotificationsController().updateBadge();
            } else if (i == this.notificationsServiceConnectionRow) {
                SharedPreferences preferences10 = MessagesController.getNotificationsSettings(this.currentAccount);
                enabled = preferences10.getBoolean("pushConnection", getMessagesController().backgroundConnection);
                SharedPreferences.Editor editor12 = preferences10.edit();
                editor12.putBoolean("pushConnection", !enabled);
                editor12.commit();
                if (!enabled) {
                    ConnectionsManager.getInstance(this.currentAccount).setPushConnectionEnabled(true);
                } else {
                    ConnectionsManager.getInstance(this.currentAccount).setPushConnectionEnabled(false);
                }
            } else if (i == this.accountsAllRow) {
                SharedPreferences preferences11 = MessagesController.getGlobalNotificationsSettings();
                enabled = preferences11.getBoolean("AllAccounts", true);
                SharedPreferences.Editor editor13 = preferences11.edit();
                editor13.putBoolean("AllAccounts", !enabled);
                editor13.commit();
                SharedConfig.showNotificationsForAllAccounts = !enabled;
                for (int a = 0; a < 3; a++) {
                    if (SharedConfig.showNotificationsForAllAccounts) {
                        NotificationsController.getInstance(a).showNotifications();
                    } else if (a == this.currentAccount) {
                        NotificationsController.getInstance(a).showNotifications();
                    } else {
                        NotificationsController.getInstance(a).hideNotifications();
                    }
                }
            } else if (i == this.notificationsServiceRow) {
                SharedPreferences preferences12 = MessagesController.getNotificationsSettings(this.currentAccount);
                enabled = preferences12.getBoolean("pushService", getMessagesController().keepAliveService);
                SharedPreferences.Editor editor14 = preferences12.edit();
                editor14.putBoolean("pushService", !enabled);
                editor14.commit();
                ApplicationLoader.startPushService();
            } else if (i == this.callsVibrateRow) {
                if (getParentActivity() != null) {
                    String key = null;
                    if (i == this.callsVibrateRow) {
                        key = "vibrate_calls";
                    }
                    showDialog(AlertsCreator.createVibrationSelectDialog(getParentActivity(), 0, key, new NotificationsSettingsActivity$$ExternalSyntheticLambda5(this, i)));
                } else {
                    return;
                }
            } else if (i == this.repeatRow) {
                AlertDialog.Builder builder2 = new AlertDialog.Builder((Context) getParentActivity());
                builder2.setTitle(LocaleController.getString("RepeatNotifications", NUM));
                builder2.setItems(new CharSequence[]{LocaleController.getString("RepeatDisabled", NUM), LocaleController.formatPluralString("Minutes", 5), LocaleController.formatPluralString("Minutes", 10), LocaleController.formatPluralString("Minutes", 30), LocaleController.formatPluralString("Hours", 1), LocaleController.formatPluralString("Hours", 2), LocaleController.formatPluralString("Hours", 4)}, new NotificationsSettingsActivity$$ExternalSyntheticLambda1(this, i));
                builder2.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                showDialog(builder2.create());
            }
            if (view2 instanceof TextCheckCell) {
                ((TextCheckCell) view2).setChecked(!enabled);
            }
        }
    }

    /* renamed from: lambda$createView$4$org-telegram-ui-NotificationsSettingsActivity  reason: not valid java name */
    public /* synthetic */ void m2665xe800150e(DialogInterface dialogInterface, int i) {
        if (!this.reseting) {
            this.reseting = true;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC.TL_account_resetNotifySettings(), new NotificationsSettingsActivity$$ExternalSyntheticLambda7(this));
        }
    }

    /* renamed from: lambda$createView$3$org-telegram-ui-NotificationsSettingsActivity  reason: not valid java name */
    public /* synthetic */ void m2664x7285eecd(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new NotificationsSettingsActivity$$ExternalSyntheticLambda3(this));
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-NotificationsSettingsActivity  reason: not valid java name */
    public /* synthetic */ void m2663xfd0bCLASSNAMEc() {
        getMessagesController().enableJoined = true;
        this.reseting = false;
        SharedPreferences.Editor editor = MessagesController.getNotificationsSettings(this.currentAccount).edit();
        editor.clear();
        editor.commit();
        this.exceptionChats.clear();
        this.exceptionUsers.clear();
        this.adapter.notifyDataSetChanged();
        if (getParentActivity() != null) {
            Toast.makeText(getParentActivity(), LocaleController.getString("ResetNotificationsText", NUM), 0).show();
        }
        getMessagesStorage().updateMutedDialogsFiltersCounters();
    }

    static /* synthetic */ void lambda$createView$5(TLObject response, TLRPC.TL_error error) {
    }

    /* renamed from: lambda$createView$6$org-telegram-ui-NotificationsSettingsActivity  reason: not valid java name */
    public /* synthetic */ void m2666xd2var_(int position) {
        this.adapter.notifyItemChanged(position);
    }

    /* renamed from: lambda$createView$7$org-telegram-ui-NotificationsSettingsActivity  reason: not valid java name */
    public /* synthetic */ void m2667x486e87d1(int position, DialogInterface dialog, int which) {
        int minutes = 0;
        if (which == 1) {
            minutes = 5;
        } else if (which == 2) {
            minutes = 10;
        } else if (which == 3) {
            minutes = 30;
        } else if (which == 4) {
            minutes = 60;
        } else if (which == 5) {
            minutes = 120;
        } else if (which == 6) {
            minutes = 240;
        }
        MessagesController.getNotificationsSettings(this.currentAccount).edit().putInt("repeat_messages", minutes).commit();
        this.adapter.notifyItemChanged(position);
    }

    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        Ringtone rng;
        if (resultCode == -1) {
            Uri ringtone = (Uri) data.getParcelableExtra("android.intent.extra.ringtone.PICKED_URI");
            String name = null;
            if (!(ringtone == null || (rng = RingtoneManager.getRingtone(getParentActivity(), ringtone)) == null)) {
                if (requestCode == this.callsRingtoneRow) {
                    if (ringtone.equals(Settings.System.DEFAULT_RINGTONE_URI)) {
                        name = LocaleController.getString("DefaultRingtone", NUM);
                    } else {
                        name = rng.getTitle(getParentActivity());
                    }
                } else if (ringtone.equals(Settings.System.DEFAULT_NOTIFICATION_URI)) {
                    name = LocaleController.getString("SoundDefault", NUM);
                } else {
                    name = rng.getTitle(getParentActivity());
                }
                rng.stop();
            }
            SharedPreferences.Editor editor = MessagesController.getNotificationsSettings(this.currentAccount).edit();
            if (requestCode == this.callsRingtoneRow) {
                if (name == null || ringtone == null) {
                    editor.putString("CallsRingtone", "NoSound");
                    editor.putString("CallsRingtonePath", "NoSound");
                } else {
                    editor.putString("CallsRingtone", name);
                    editor.putString("CallsRingtonePath", ringtone.toString());
                }
            }
            editor.commit();
            this.adapter.notifyItemChanged(requestCode);
        }
    }

    private void showExceptionsAlert(int position) {
        ArrayList<NotificationException> exceptions;
        String alertText = null;
        if (position == this.privateRow) {
            exceptions = this.exceptionUsers;
            if (exceptions != null && !exceptions.isEmpty()) {
                alertText = LocaleController.formatPluralString("ChatsException", exceptions.size());
            }
        } else if (position == this.groupRow) {
            exceptions = this.exceptionChats;
            if (exceptions != null && !exceptions.isEmpty()) {
                alertText = LocaleController.formatPluralString("Groups", exceptions.size());
            }
        } else {
            exceptions = this.exceptionChannels;
            if (exceptions != null && !exceptions.isEmpty()) {
                alertText = LocaleController.formatPluralString("Channels", exceptions.size());
            }
        }
        if (alertText != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            if (exceptions.size() == 1) {
                builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("NotificationsExceptionsSingleAlert", NUM, alertText)));
            } else {
                builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("NotificationsExceptionsAlert", NUM, alertText)));
            }
            builder.setTitle(LocaleController.getString("NotificationsExceptions", NUM));
            builder.setNeutralButton(LocaleController.getString("ViewExceptions", NUM), new NotificationsSettingsActivity$$ExternalSyntheticLambda2(this, exceptions));
            builder.setNegativeButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
            showDialog(builder.create());
        }
    }

    /* renamed from: lambda$showExceptionsAlert$9$org-telegram-ui-NotificationsSettingsActivity  reason: not valid java name */
    public /* synthetic */ void m2671xe3755a33(ArrayList exceptions, DialogInterface dialogInterface, int i) {
        presentFragment(new NotificationsCustomSettingsActivity(-1, exceptions));
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.adapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.notificationsSettingsUpdated) {
            this.adapter.notifyDataSetChanged();
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int position = holder.getAdapterPosition();
            return (position == NotificationsSettingsActivity.this.notificationsSectionRow || position == NotificationsSettingsActivity.this.notificationsSection2Row || position == NotificationsSettingsActivity.this.inappSectionRow || position == NotificationsSettingsActivity.this.eventsSectionRow || position == NotificationsSettingsActivity.this.otherSectionRow || position == NotificationsSettingsActivity.this.resetSectionRow || position == NotificationsSettingsActivity.this.badgeNumberSection || position == NotificationsSettingsActivity.this.otherSection2Row || position == NotificationsSettingsActivity.this.resetSection2Row || position == NotificationsSettingsActivity.this.callsSection2Row || position == NotificationsSettingsActivity.this.callsSectionRow || position == NotificationsSettingsActivity.this.badgeNumberSection2Row || position == NotificationsSettingsActivity.this.accountsSectionRow || position == NotificationsSettingsActivity.this.accountsInfoRow || position == NotificationsSettingsActivity.this.resetNotificationsSectionRow) ? false : true;
        }

        public int getItemCount() {
            return NotificationsSettingsActivity.this.rowCount;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new HeaderCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                case 1:
                    view = new TextCheckCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                case 2:
                    view = new TextDetailSettingsCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                case 3:
                    view = new NotificationsCheckCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                case 4:
                    view = new ShadowSectionCell(this.mContext);
                    break;
                case 5:
                    view = new TextSettingsCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                default:
                    view = new TextInfoPrivacyCell(this.mContext);
                    view.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ArrayList<NotificationException> exceptions;
            String text;
            int offUntil;
            int iconType;
            boolean enabled;
            String value;
            RecyclerView.ViewHolder viewHolder = holder;
            int i = position;
            boolean z = false;
            switch (holder.getItemViewType()) {
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
                    TextCheckCell checkCell = (TextCheckCell) viewHolder.itemView;
                    SharedPreferences preferences = MessagesController.getNotificationsSettings(NotificationsSettingsActivity.this.currentAccount);
                    if (i == NotificationsSettingsActivity.this.inappSoundRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("InAppSounds", NUM), preferences.getBoolean("EnableInAppSounds", true), true);
                        return;
                    } else if (i == NotificationsSettingsActivity.this.inappVibrateRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("InAppVibrate", NUM), preferences.getBoolean("EnableInAppVibrate", true), true);
                        return;
                    } else if (i == NotificationsSettingsActivity.this.inappPreviewRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("InAppPreview", NUM), preferences.getBoolean("EnableInAppPreview", true), true);
                        return;
                    } else if (i == NotificationsSettingsActivity.this.inappPriorityRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("NotificationsImportance", NUM), preferences.getBoolean("EnableInAppPriority", false), false);
                        return;
                    } else if (i == NotificationsSettingsActivity.this.contactJoinedRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("ContactJoined", NUM), preferences.getBoolean("EnableContactJoined", true), true);
                        return;
                    } else if (i == NotificationsSettingsActivity.this.pinnedMessageRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("PinnedMessages", NUM), preferences.getBoolean("PinnedMessages", true), false);
                        return;
                    } else if (i == NotificationsSettingsActivity.this.androidAutoAlertRow) {
                        checkCell.setTextAndCheck("Android Auto", preferences.getBoolean("EnableAutoNotifications", false), true);
                        return;
                    } else if (i == NotificationsSettingsActivity.this.notificationsServiceRow) {
                        checkCell.setTextAndValueAndCheck(LocaleController.getString("NotificationsService", NUM), LocaleController.getString("NotificationsServiceInfo", NUM), preferences.getBoolean("pushService", NotificationsSettingsActivity.this.getMessagesController().keepAliveService), true, true);
                        return;
                    } else if (i == NotificationsSettingsActivity.this.notificationsServiceConnectionRow) {
                        checkCell.setTextAndValueAndCheck(LocaleController.getString("NotificationsServiceConnection", NUM), LocaleController.getString("NotificationsServiceConnectionInfo", NUM), preferences.getBoolean("pushConnection", NotificationsSettingsActivity.this.getMessagesController().backgroundConnection), true, true);
                        return;
                    } else if (i == NotificationsSettingsActivity.this.badgeNumberShowRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("BadgeNumberShow", NUM), NotificationsSettingsActivity.this.getNotificationsController().showBadgeNumber, true);
                        return;
                    } else if (i == NotificationsSettingsActivity.this.badgeNumberMutedRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("BadgeNumberMutedChats", NUM), NotificationsSettingsActivity.this.getNotificationsController().showBadgeMuted, true);
                        return;
                    } else if (i == NotificationsSettingsActivity.this.badgeNumberMessagesRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("BadgeNumberUnread", NUM), NotificationsSettingsActivity.this.getNotificationsController().showBadgeMessages, false);
                        return;
                    } else if (i == NotificationsSettingsActivity.this.inchatSoundRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("InChatSound", NUM), preferences.getBoolean("EnableInChatSound", true), true);
                        return;
                    } else if (i == NotificationsSettingsActivity.this.callsVibrateRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("Vibrate", NUM), preferences.getBoolean("EnableCallVibrate", true), true);
                        return;
                    } else if (i == NotificationsSettingsActivity.this.accountsAllRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("AllAccounts", NUM), MessagesController.getGlobalNotificationsSettings().getBoolean("AllAccounts", true), false);
                        return;
                    } else {
                        return;
                    }
                case 2:
                    TextDetailSettingsCell settingsCell = (TextDetailSettingsCell) viewHolder.itemView;
                    settingsCell.setMultilineDetail(true);
                    if (i == NotificationsSettingsActivity.this.resetNotificationsRow) {
                        settingsCell.setTextAndValue(LocaleController.getString("ResetAllNotifications", NUM), LocaleController.getString("UndoAllCustom", NUM), false);
                        return;
                    }
                    return;
                case 3:
                    NotificationsCheckCell checkCell2 = (NotificationsCheckCell) viewHolder.itemView;
                    SharedPreferences preferences2 = MessagesController.getNotificationsSettings(NotificationsSettingsActivity.this.currentAccount);
                    int currentTime = ConnectionsManager.getInstance(NotificationsSettingsActivity.this.currentAccount).getCurrentTime();
                    if (i == NotificationsSettingsActivity.this.privateRow) {
                        text = LocaleController.getString("NotificationsPrivateChats", NUM);
                        exceptions = NotificationsSettingsActivity.this.exceptionUsers;
                        offUntil = preferences2.getInt("EnableAll2", 0);
                    } else if (i == NotificationsSettingsActivity.this.groupRow) {
                        text = LocaleController.getString("NotificationsGroups", NUM);
                        exceptions = NotificationsSettingsActivity.this.exceptionChats;
                        offUntil = preferences2.getInt("EnableGroup2", 0);
                    } else {
                        text = LocaleController.getString("NotificationsChannels", NUM);
                        exceptions = NotificationsSettingsActivity.this.exceptionChannels;
                        offUntil = preferences2.getInt("EnableChannel2", 0);
                    }
                    boolean z2 = offUntil < currentTime;
                    boolean enabled2 = z2;
                    if (z2) {
                        iconType = 0;
                    } else if (offUntil - 31536000 >= currentTime) {
                        iconType = 0;
                    } else {
                        iconType = 2;
                    }
                    StringBuilder builder = new StringBuilder();
                    if (exceptions == null || exceptions.isEmpty()) {
                        builder.append(LocaleController.getString("TapToChange", NUM));
                        enabled = enabled2;
                    } else {
                        boolean z3 = offUntil < currentTime;
                        boolean enabled3 = z3;
                        if (z3) {
                            builder.append(LocaleController.getString("NotificationsOn", NUM));
                        } else if (offUntil - 31536000 >= currentTime) {
                            builder.append(LocaleController.getString("NotificationsOff", NUM));
                        } else {
                            builder.append(LocaleController.formatString("NotificationsOffUntil", NUM, LocaleController.stringForMessageListDate((long) offUntil)));
                        }
                        if (builder.length() != 0) {
                            builder.append(", ");
                        }
                        builder.append(LocaleController.formatPluralString("Exception", exceptions.size()));
                        enabled = enabled3;
                    }
                    if (i != NotificationsSettingsActivity.this.channelsRow) {
                        z = true;
                    }
                    StringBuilder sb = builder;
                    int i2 = offUntil;
                    checkCell2.setTextAndValueAndCheck(text, builder, enabled, iconType, z);
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
                    TextSettingsCell textCell = (TextSettingsCell) viewHolder.itemView;
                    SharedPreferences preferences3 = MessagesController.getNotificationsSettings(NotificationsSettingsActivity.this.currentAccount);
                    if (i == NotificationsSettingsActivity.this.callsRingtoneRow) {
                        String value2 = preferences3.getString("CallsRingtone", LocaleController.getString("DefaultRingtone", NUM));
                        if (value2.equals("NoSound")) {
                            value2 = LocaleController.getString("NoSound", NUM);
                        }
                        textCell.setTextAndValue(LocaleController.getString("VoipSettingsRingtone", NUM), value2, false);
                        return;
                    } else if (i == NotificationsSettingsActivity.this.callsVibrateRow) {
                        int value3 = preferences3.getInt("vibrate_calls", 0);
                        if (value3 == 0) {
                            textCell.setTextAndValue(LocaleController.getString("Vibrate", NUM), LocaleController.getString("VibrationDefault", NUM), true);
                            return;
                        } else if (value3 == 1) {
                            textCell.setTextAndValue(LocaleController.getString("Vibrate", NUM), LocaleController.getString("Short", NUM), true);
                            return;
                        } else if (value3 == 2) {
                            textCell.setTextAndValue(LocaleController.getString("Vibrate", NUM), LocaleController.getString("VibrationDisabled", NUM), true);
                            return;
                        } else if (value3 == 3) {
                            textCell.setTextAndValue(LocaleController.getString("Vibrate", NUM), LocaleController.getString("Long", NUM), true);
                            return;
                        } else if (value3 == 4) {
                            textCell.setTextAndValue(LocaleController.getString("Vibrate", NUM), LocaleController.getString("OnlyIfSilent", NUM), true);
                            return;
                        } else {
                            return;
                        }
                    } else if (i == NotificationsSettingsActivity.this.repeatRow) {
                        int minutes = preferences3.getInt("repeat_messages", 60);
                        if (minutes == 0) {
                            value = LocaleController.getString("RepeatNotificationsNever", NUM);
                        } else if (minutes < 60) {
                            value = LocaleController.formatPluralString("Minutes", minutes);
                        } else {
                            value = LocaleController.formatPluralString("Hours", minutes / 60);
                        }
                        textCell.setTextAndValue(LocaleController.getString("RepeatNotifications", NUM), value, false);
                        return;
                    } else {
                        return;
                    }
                case 6:
                    TextInfoPrivacyCell textCell2 = (TextInfoPrivacyCell) viewHolder.itemView;
                    if (i == NotificationsSettingsActivity.this.accountsInfoRow) {
                        textCell2.setText(LocaleController.getString("ShowNotificationsForInfo", NUM));
                        return;
                    }
                    return;
                default:
                    return;
            }
        }

        public int getItemViewType(int position) {
            if (position == NotificationsSettingsActivity.this.eventsSectionRow || position == NotificationsSettingsActivity.this.otherSectionRow || position == NotificationsSettingsActivity.this.resetSectionRow || position == NotificationsSettingsActivity.this.callsSectionRow || position == NotificationsSettingsActivity.this.badgeNumberSection || position == NotificationsSettingsActivity.this.inappSectionRow || position == NotificationsSettingsActivity.this.notificationsSectionRow || position == NotificationsSettingsActivity.this.accountsSectionRow) {
                return 0;
            }
            if (position == NotificationsSettingsActivity.this.inappSoundRow || position == NotificationsSettingsActivity.this.inappVibrateRow || position == NotificationsSettingsActivity.this.notificationsServiceConnectionRow || position == NotificationsSettingsActivity.this.inappPreviewRow || position == NotificationsSettingsActivity.this.contactJoinedRow || position == NotificationsSettingsActivity.this.pinnedMessageRow || position == NotificationsSettingsActivity.this.notificationsServiceRow || position == NotificationsSettingsActivity.this.badgeNumberMutedRow || position == NotificationsSettingsActivity.this.badgeNumberMessagesRow || position == NotificationsSettingsActivity.this.badgeNumberShowRow || position == NotificationsSettingsActivity.this.inappPriorityRow || position == NotificationsSettingsActivity.this.inchatSoundRow || position == NotificationsSettingsActivity.this.androidAutoAlertRow || position == NotificationsSettingsActivity.this.accountsAllRow) {
                return 1;
            }
            if (position == NotificationsSettingsActivity.this.resetNotificationsRow) {
                return 2;
            }
            if (position == NotificationsSettingsActivity.this.privateRow || position == NotificationsSettingsActivity.this.groupRow || position == NotificationsSettingsActivity.this.channelsRow) {
                return 3;
            }
            if (position == NotificationsSettingsActivity.this.eventsSection2Row || position == NotificationsSettingsActivity.this.notificationsSection2Row || position == NotificationsSettingsActivity.this.otherSection2Row || position == NotificationsSettingsActivity.this.resetSection2Row || position == NotificationsSettingsActivity.this.callsSection2Row || position == NotificationsSettingsActivity.this.badgeNumberSection2Row || position == NotificationsSettingsActivity.this.resetNotificationsSectionRow) {
                return 4;
            }
            if (position == NotificationsSettingsActivity.this.accountsInfoRow) {
                return 6;
            }
            return 5;
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{HeaderCell.class, TextCheckCell.class, TextDetailSettingsCell.class, TextSettingsCell.class, NotificationsCheckCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrack"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackChecked"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrack"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackChecked"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteValueText"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_LINKCOLOR, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteLinkText"));
        return themeDescriptions;
    }
}
