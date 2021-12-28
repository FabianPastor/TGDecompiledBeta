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
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_account_resetNotifySettings;
import org.telegram.tgnet.TLRPC$TL_error;
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

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$createView$5(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
        int i23 = i22 + 1;
        this.rowCount = i23;
        this.inchatSoundRow = i22;
        if (Build.VERSION.SDK_INT >= 21) {
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
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new NotificationsSettingsActivity$$ExternalSyntheticLambda3(this));
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v10, resolved type: java.util.ArrayList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v18, resolved type: org.telegram.messenger.MessagesStorage} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v47, resolved type: java.util.ArrayList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v51, resolved type: java.util.ArrayList} */
    /* JADX WARNING: type inference failed for: r4v9, types: [boolean] */
    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0121, code lost:
        if (r4.deleted != false) goto L_0x014e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x014c, code lost:
        if (r4.deleted != false) goto L_0x014e;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:106:0x0264  */
    /* JADX WARNING: Removed duplicated region for block: B:112:0x027e A[LOOP:3: B:111:0x027c->B:112:0x027e, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:115:0x0297  */
    /* JADX WARNING: Removed duplicated region for block: B:89:0x0225  */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$loadExceptions$1() {
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
            long r11 = r11.clientUserId
            int r13 = r9.currentAccount
            android.content.SharedPreferences r13 = org.telegram.messenger.MessagesController.getNotificationsSettings(r13)
            java.util.Map r14 = r13.getAll()
            java.util.Set r15 = r14.entrySet()
            java.util.Iterator r15 = r15.iterator()
        L_0x004e:
            boolean r16 = r15.hasNext()
            r17 = r5
            if (r16 == 0) goto L_0x01bd
            java.lang.Object r16 = r15.next()
            java.util.Map$Entry r16 = (java.util.Map.Entry) r16
            java.lang.Object r18 = r16.getKey()
            r5 = r18
            java.lang.String r5 = (java.lang.String) r5
            r18 = r15
            java.lang.String r15 = "notify2_"
            boolean r19 = r5.startsWith(r15)
            if (r19 == 0) goto L_0x01ab
            r19 = r4
            java.lang.String r4 = ""
            java.lang.String r4 = r5.replace(r15, r4)
            java.lang.Long r5 = org.telegram.messenger.Utilities.parseLong(r4)
            r15 = r7
            r20 = r8
            long r7 = r5.longValue()
            r21 = 0
            int r5 = (r7 > r21 ? 1 : (r7 == r21 ? 0 : -1))
            if (r5 == 0) goto L_0x01a3
            int r5 = (r7 > r11 ? 1 : (r7 == r11 ? 0 : -1))
            if (r5 == 0) goto L_0x01a3
            org.telegram.ui.NotificationsSettingsActivity$NotificationException r5 = new org.telegram.ui.NotificationsSettingsActivity$NotificationException
            r5.<init>()
            r5.did = r7
            r21 = r11
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            java.lang.String r12 = "custom_"
            r11.append(r12)
            r11.append(r7)
            java.lang.String r11 = r11.toString()
            r12 = 0
            boolean r11 = r13.getBoolean(r11, r12)
            r5.hasCustom = r11
            java.lang.Object r11 = r16.getValue()
            java.lang.Integer r11 = (java.lang.Integer) r11
            int r11 = r11.intValue()
            r5.notify = r11
            if (r11 == 0) goto L_0x00d9
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            java.lang.String r12 = "notifyuntil_"
            r11.append(r12)
            r11.append(r4)
            java.lang.String r4 = r11.toString()
            java.lang.Object r4 = r14.get(r4)
            java.lang.Integer r4 = (java.lang.Integer) r4
            if (r4 == 0) goto L_0x00d9
            int r4 = r4.intValue()
            r5.muteUntil = r4
        L_0x00d9:
            boolean r4 = org.telegram.messenger.DialogObject.isEncryptedDialog(r7)
            if (r4 == 0) goto L_0x0129
            int r4 = org.telegram.messenger.DialogObject.getEncryptedChatId(r7)
            int r11 = r9.currentAccount
            org.telegram.messenger.MessagesController r11 = org.telegram.messenger.MessagesController.getInstance(r11)
            java.lang.Integer r12 = java.lang.Integer.valueOf(r4)
            org.telegram.tgnet.TLRPC$EncryptedChat r11 = r11.getEncryptedChat(r12)
            if (r11 != 0) goto L_0x00fe
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            r3.add(r4)
            r1.put(r7, r5)
            goto L_0x0124
        L_0x00fe:
            int r4 = r9.currentAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            long r7 = r11.user_id
            java.lang.Long r7 = java.lang.Long.valueOf(r7)
            org.telegram.tgnet.TLRPC$User r4 = r4.getUser(r7)
            if (r4 != 0) goto L_0x011f
            long r7 = r11.user_id
            java.lang.Long r4 = java.lang.Long.valueOf(r7)
            r0.add(r4)
            long r7 = r11.user_id
            r1.put(r7, r5)
            goto L_0x0124
        L_0x011f:
            boolean r4 = r4.deleted
            if (r4 == 0) goto L_0x0124
            goto L_0x014e
        L_0x0124:
            r6.add(r5)
            goto L_0x01a5
        L_0x0129:
            boolean r4 = org.telegram.messenger.DialogObject.isUserDialog(r7)
            if (r4 == 0) goto L_0x0154
            int r4 = r9.currentAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            java.lang.Long r11 = java.lang.Long.valueOf(r7)
            org.telegram.tgnet.TLRPC$User r4 = r4.getUser(r11)
            if (r4 != 0) goto L_0x014a
            java.lang.Long r4 = java.lang.Long.valueOf(r7)
            r0.add(r4)
            r1.put(r7, r5)
            goto L_0x0150
        L_0x014a:
            boolean r4 = r4.deleted
            if (r4 == 0) goto L_0x0150
        L_0x014e:
            r7 = r15
            goto L_0x019a
        L_0x0150:
            r6.add(r5)
            goto L_0x01a5
        L_0x0154:
            int r4 = r9.currentAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            long r11 = -r7
            r16 = r13
            java.lang.Long r13 = java.lang.Long.valueOf(r11)
            org.telegram.tgnet.TLRPC$Chat r4 = r4.getChat(r13)
            if (r4 != 0) goto L_0x0172
            java.lang.Long r4 = java.lang.Long.valueOf(r11)
            r2.add(r4)
            r1.put(r7, r5)
            goto L_0x0197
        L_0x0172:
            boolean r7 = r4.left
            if (r7 != 0) goto L_0x0197
            boolean r7 = r4.kicked
            if (r7 != 0) goto L_0x0197
            org.telegram.tgnet.TLRPC$InputChannel r7 = r4.migrated_to
            if (r7 == 0) goto L_0x017f
            goto L_0x0197
        L_0x017f:
            boolean r7 = org.telegram.messenger.ChatObject.isChannel(r4)
            if (r7 == 0) goto L_0x0190
            boolean r4 = r4.megagroup
            if (r4 != 0) goto L_0x0190
            r8 = r20
            r8.add(r5)
            r7 = r15
            goto L_0x01b1
        L_0x0190:
            r8 = r20
            r7 = r15
            r7.add(r5)
            goto L_0x01b1
        L_0x0197:
            r7 = r15
            r13 = r16
        L_0x019a:
            r5 = r17
            r15 = r18
            r4 = r19
            r8 = r20
            goto L_0x01b9
        L_0x01a3:
            r21 = r11
        L_0x01a5:
            r16 = r13
            r7 = r15
            r8 = r20
            goto L_0x01b1
        L_0x01ab:
            r19 = r4
            r21 = r11
            r16 = r13
        L_0x01b1:
            r13 = r16
            r5 = r17
            r15 = r18
            r4 = r19
        L_0x01b9:
            r11 = r21
            goto L_0x004e
        L_0x01bd:
            r19 = r4
            r12 = 0
            int r4 = r1.size()
            if (r4 == 0) goto L_0x02ba
            boolean r4 = r3.isEmpty()     // Catch:{ Exception -> 0x0216 }
            java.lang.String r5 = ","
            if (r4 != 0) goto L_0x01db
            int r4 = r9.currentAccount     // Catch:{ Exception -> 0x0216 }
            org.telegram.messenger.MessagesStorage r4 = org.telegram.messenger.MessagesStorage.getInstance(r4)     // Catch:{ Exception -> 0x0216 }
            java.lang.String r3 = android.text.TextUtils.join(r5, r3)     // Catch:{ Exception -> 0x0216 }
            r4.getEncryptedChatsInternal(r3, r10, r0)     // Catch:{ Exception -> 0x0216 }
        L_0x01db:
            boolean r3 = r0.isEmpty()     // Catch:{ Exception -> 0x0216 }
            if (r3 != 0) goto L_0x01f5
            int r3 = r9.currentAccount     // Catch:{ Exception -> 0x01f1 }
            org.telegram.messenger.MessagesStorage r3 = org.telegram.messenger.MessagesStorage.getInstance(r3)     // Catch:{ Exception -> 0x01f1 }
            java.lang.String r0 = android.text.TextUtils.join(r5, r0)     // Catch:{ Exception -> 0x01f1 }
            r4 = r19
            r3.getUsersInternal(r0, r4)     // Catch:{ Exception -> 0x0212 }
            goto L_0x01f7
        L_0x01f1:
            r0 = move-exception
            r4 = r19
            goto L_0x0213
        L_0x01f5:
            r4 = r19
        L_0x01f7:
            boolean r0 = r2.isEmpty()     // Catch:{ Exception -> 0x0212 }
            if (r0 != 0) goto L_0x020f
            int r0 = r9.currentAccount     // Catch:{ Exception -> 0x0212 }
            org.telegram.messenger.MessagesStorage r0 = org.telegram.messenger.MessagesStorage.getInstance(r0)     // Catch:{ Exception -> 0x0212 }
            java.lang.String r2 = android.text.TextUtils.join(r5, r2)     // Catch:{ Exception -> 0x0212 }
            r5 = r17
            r0.getChatsInternal(r2, r5)     // Catch:{ Exception -> 0x020d }
            goto L_0x021e
        L_0x020d:
            r0 = move-exception
            goto L_0x021b
        L_0x020f:
            r5 = r17
            goto L_0x021e
        L_0x0212:
            r0 = move-exception
        L_0x0213:
            r5 = r17
            goto L_0x021b
        L_0x0216:
            r0 = move-exception
            r5 = r17
            r4 = r19
        L_0x021b:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x021e:
            int r0 = r5.size()
            r2 = 0
        L_0x0223:
            if (r2 >= r0) goto L_0x025d
            java.lang.Object r3 = r5.get(r2)
            org.telegram.tgnet.TLRPC$Chat r3 = (org.telegram.tgnet.TLRPC$Chat) r3
            boolean r11 = r3.left
            if (r11 != 0) goto L_0x025a
            boolean r11 = r3.kicked
            if (r11 != 0) goto L_0x025a
            org.telegram.tgnet.TLRPC$InputChannel r11 = r3.migrated_to
            if (r11 == 0) goto L_0x0238
            goto L_0x025a
        L_0x0238:
            long r13 = r3.id
            long r13 = -r13
            java.lang.Object r11 = r1.get(r13)
            org.telegram.ui.NotificationsSettingsActivity$NotificationException r11 = (org.telegram.ui.NotificationsSettingsActivity.NotificationException) r11
            long r13 = r3.id
            long r13 = -r13
            r1.remove(r13)
            if (r11 == 0) goto L_0x025a
            boolean r13 = org.telegram.messenger.ChatObject.isChannel(r3)
            if (r13 == 0) goto L_0x0257
            boolean r3 = r3.megagroup
            if (r3 != 0) goto L_0x0257
            r8.add(r11)
            goto L_0x025a
        L_0x0257:
            r7.add(r11)
        L_0x025a:
            int r2 = r2 + 1
            goto L_0x0223
        L_0x025d:
            int r0 = r4.size()
            r2 = 0
        L_0x0262:
            if (r2 >= r0) goto L_0x0277
            java.lang.Object r3 = r4.get(r2)
            org.telegram.tgnet.TLRPC$User r3 = (org.telegram.tgnet.TLRPC$User) r3
            boolean r11 = r3.deleted
            if (r11 == 0) goto L_0x026f
            goto L_0x0274
        L_0x026f:
            long r13 = r3.id
            r1.remove(r13)
        L_0x0274:
            int r2 = r2 + 1
            goto L_0x0262
        L_0x0277:
            int r0 = r10.size()
            r2 = 0
        L_0x027c:
            if (r2 >= r0) goto L_0x0291
            java.lang.Object r3 = r10.get(r2)
            org.telegram.tgnet.TLRPC$EncryptedChat r3 = (org.telegram.tgnet.TLRPC$EncryptedChat) r3
            int r3 = r3.id
            long r13 = (long) r3
            long r13 = org.telegram.messenger.DialogObject.makeEncryptedDialogId(r13)
            r1.remove(r13)
            int r2 = r2 + 1
            goto L_0x027c
        L_0x0291:
            int r0 = r1.size()
        L_0x0295:
            if (r12 >= r0) goto L_0x02be
            long r2 = r1.keyAt(r12)
            boolean r2 = org.telegram.messenger.DialogObject.isChatDialog(r2)
            if (r2 == 0) goto L_0x02b0
            java.lang.Object r2 = r1.valueAt(r12)
            r7.remove(r2)
            java.lang.Object r2 = r1.valueAt(r12)
            r8.remove(r2)
            goto L_0x02b7
        L_0x02b0:
            java.lang.Object r2 = r1.valueAt(r12)
            r6.remove(r2)
        L_0x02b7:
            int r12 = r12 + 1
            goto L_0x0295
        L_0x02ba:
            r5 = r17
            r4 = r19
        L_0x02be:
            org.telegram.ui.NotificationsSettingsActivity$$ExternalSyntheticLambda6 r0 = new org.telegram.ui.NotificationsSettingsActivity$$ExternalSyntheticLambda6
            r1 = r0
            r2 = r23
            r3 = r4
            r4 = r5
            r5 = r10
            r1.<init>(r2, r3, r4, r5, r6, r7, r8)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.NotificationsSettingsActivity.lambda$loadExceptions$1():void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadExceptions$0(ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3, ArrayList arrayList4, ArrayList arrayList5, ArrayList arrayList6) {
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
        FrameLayout frameLayout = new FrameLayout(context);
        this.fragmentView = frameLayout;
        FrameLayout frameLayout2 = frameLayout;
        frameLayout2.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setItemAnimator((RecyclerView.ItemAnimator) null);
        this.listView.setLayoutAnimation((LayoutAnimationController) null);
        this.listView.setLayoutManager(new LinearLayoutManager(this, context, 1, false) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        });
        this.listView.setVerticalScrollBarEnabled(false);
        frameLayout2.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        RecyclerListView recyclerListView2 = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.adapter = listAdapter;
        recyclerListView2.setAdapter(listAdapter);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListenerExtended) new NotificationsSettingsActivity$$ExternalSyntheticLambda9(this));
        return this.fragmentView;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v12, resolved type: java.lang.String} */
    /* JADX WARNING: type inference failed for: r14v11 */
    /* JADX WARNING: type inference failed for: r14v32, types: [android.os.Parcelable] */
    /* JADX WARNING: type inference failed for: r14v35 */
    /* JADX WARNING: type inference failed for: r14v36 */
    /* JADX WARNING: type inference failed for: r14v37 */
    /* access modifiers changed from: private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$createView$8(android.view.View r11, int r12, float r13, float r14) {
        /*
            r10 = this;
            android.app.Activity r14 = r10.getParentActivity()
            if (r14 != 0) goto L_0x0007
            return
        L_0x0007:
            int r14 = r10.privateRow
            r0 = 2
            r1 = 0
            r2 = 1
            if (r12 == r14) goto L_0x03c3
            int r3 = r10.groupRow
            if (r12 == r3) goto L_0x03c3
            int r3 = r10.channelsRow
            if (r12 != r3) goto L_0x0018
            goto L_0x03c3
        L_0x0018:
            int r13 = r10.callsRingtoneRow
            r14 = 0
            if (r12 != r13) goto L_0x0078
            int r13 = r10.currentAccount     // Catch:{ Exception -> 0x0072 }
            android.content.SharedPreferences r13 = org.telegram.messenger.MessagesController.getNotificationsSettings(r13)     // Catch:{ Exception -> 0x0072 }
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
            r4 = r14
        L_0x004c:
            java.lang.String r5 = "CallsRingtonePath"
            java.lang.String r13 = r13.getString(r5, r4)     // Catch:{ Exception -> 0x0072 }
            if (r13 == 0) goto L_0x0068
            java.lang.String r5 = "NoSound"
            boolean r5 = r13.equals(r5)     // Catch:{ Exception -> 0x0072 }
            if (r5 != 0) goto L_0x0068
            boolean r14 = r13.equals(r4)     // Catch:{ Exception -> 0x0072 }
            if (r14 == 0) goto L_0x0064
            r14 = r3
            goto L_0x0068
        L_0x0064:
            android.net.Uri r14 = android.net.Uri.parse(r13)     // Catch:{ Exception -> 0x0072 }
        L_0x0068:
            java.lang.String r13 = "android.intent.extra.ringtone.EXISTING_URI"
            r0.putExtra(r13, r14)     // Catch:{ Exception -> 0x0072 }
            r10.startActivityForResult(r0, r12)     // Catch:{ Exception -> 0x0072 }
            goto L_0x0427
        L_0x0072:
            r12 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r12)
            goto L_0x0427
        L_0x0078:
            int r13 = r10.resetNotificationsRow
            r3 = 2131624695(0x7f0e02f7, float:1.8876577E38)
            java.lang.String r4 = "Cancel"
            if (r12 != r13) goto L_0x00d5
            org.telegram.ui.ActionBar.AlertDialog$Builder r12 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            android.app.Activity r13 = r10.getParentActivity()
            r12.<init>((android.content.Context) r13)
            r13 = 2131627583(0x7f0e0e3f, float:1.8882435E38)
            java.lang.String r0 = "ResetNotificationsAlertTitle"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r0, r13)
            r12.setTitle(r13)
            r13 = 2131627582(0x7f0e0e3e, float:1.8882433E38)
            java.lang.String r0 = "ResetNotificationsAlert"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r0, r13)
            r12.setMessage(r13)
            r13 = 2131627563(0x7f0e0e2b, float:1.8882394E38)
            java.lang.String r0 = "Reset"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r0, r13)
            org.telegram.ui.NotificationsSettingsActivity$$ExternalSyntheticLambda0 r0 = new org.telegram.ui.NotificationsSettingsActivity$$ExternalSyntheticLambda0
            r0.<init>(r10)
            r12.setPositiveButton(r13, r0)
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r12.setNegativeButton(r13, r14)
            org.telegram.ui.ActionBar.AlertDialog r12 = r12.create()
            r10.showDialog(r12)
            r13 = -1
            android.view.View r12 = r12.getButton(r13)
            android.widget.TextView r12 = (android.widget.TextView) r12
            if (r12 == 0) goto L_0x0427
            java.lang.String r13 = "dialogTextRed2"
            int r13 = org.telegram.ui.ActionBar.Theme.getColor(r13)
            r12.setTextColor(r13)
            goto L_0x0427
        L_0x00d5:
            int r13 = r10.inappSoundRow
            if (r12 != r13) goto L_0x00f3
            int r12 = r10.currentAccount
            android.content.SharedPreferences r12 = org.telegram.messenger.MessagesController.getNotificationsSettings(r12)
            android.content.SharedPreferences$Editor r13 = r12.edit()
            java.lang.String r14 = "EnableInAppSounds"
            boolean r1 = r12.getBoolean(r14, r2)
            r12 = r1 ^ 1
            r13.putBoolean(r14, r12)
            r13.commit()
            goto L_0x0427
        L_0x00f3:
            int r13 = r10.inappVibrateRow
            if (r12 != r13) goto L_0x0111
            int r12 = r10.currentAccount
            android.content.SharedPreferences r12 = org.telegram.messenger.MessagesController.getNotificationsSettings(r12)
            android.content.SharedPreferences$Editor r13 = r12.edit()
            java.lang.String r14 = "EnableInAppVibrate"
            boolean r1 = r12.getBoolean(r14, r2)
            r12 = r1 ^ 1
            r13.putBoolean(r14, r12)
            r13.commit()
            goto L_0x0427
        L_0x0111:
            int r13 = r10.inappPreviewRow
            if (r12 != r13) goto L_0x012f
            int r12 = r10.currentAccount
            android.content.SharedPreferences r12 = org.telegram.messenger.MessagesController.getNotificationsSettings(r12)
            android.content.SharedPreferences$Editor r13 = r12.edit()
            java.lang.String r14 = "EnableInAppPreview"
            boolean r1 = r12.getBoolean(r14, r2)
            r12 = r1 ^ 1
            r13.putBoolean(r14, r12)
            r13.commit()
            goto L_0x0427
        L_0x012f:
            int r13 = r10.inchatSoundRow
            if (r12 != r13) goto L_0x0156
            int r12 = r10.currentAccount
            android.content.SharedPreferences r12 = org.telegram.messenger.MessagesController.getNotificationsSettings(r12)
            android.content.SharedPreferences$Editor r13 = r12.edit()
            java.lang.String r14 = "EnableInChatSound"
            boolean r1 = r12.getBoolean(r14, r2)
            r12 = r1 ^ 1
            r13.putBoolean(r14, r12)
            r13.commit()
            org.telegram.messenger.NotificationsController r12 = r10.getNotificationsController()
            r13 = r1 ^ 1
            r12.setInChatSoundEnabled(r13)
            goto L_0x0427
        L_0x0156:
            int r13 = r10.inappPriorityRow
            if (r12 != r13) goto L_0x0174
            int r12 = r10.currentAccount
            android.content.SharedPreferences r12 = org.telegram.messenger.MessagesController.getNotificationsSettings(r12)
            android.content.SharedPreferences$Editor r13 = r12.edit()
            java.lang.String r14 = "EnableInAppPriority"
            boolean r1 = r12.getBoolean(r14, r1)
            r12 = r1 ^ 1
            r13.putBoolean(r14, r12)
            r13.commit()
            goto L_0x0427
        L_0x0174:
            int r13 = r10.contactJoinedRow
            if (r12 != r13) goto L_0x01ae
            int r12 = r10.currentAccount
            android.content.SharedPreferences r12 = org.telegram.messenger.MessagesController.getNotificationsSettings(r12)
            android.content.SharedPreferences$Editor r13 = r12.edit()
            java.lang.String r14 = "EnableContactJoined"
            boolean r1 = r12.getBoolean(r14, r2)
            int r12 = r10.currentAccount
            org.telegram.messenger.MessagesController r12 = org.telegram.messenger.MessagesController.getInstance(r12)
            r0 = r1 ^ 1
            r12.enableJoined = r0
            r12 = r1 ^ 1
            r13.putBoolean(r14, r12)
            r13.commit()
            org.telegram.tgnet.TLRPC$TL_account_setContactSignUpNotification r12 = new org.telegram.tgnet.TLRPC$TL_account_setContactSignUpNotification
            r12.<init>()
            r12.silent = r1
            int r13 = r10.currentAccount
            org.telegram.tgnet.ConnectionsManager r13 = org.telegram.tgnet.ConnectionsManager.getInstance(r13)
            org.telegram.ui.NotificationsSettingsActivity$$ExternalSyntheticLambda8 r14 = org.telegram.ui.NotificationsSettingsActivity$$ExternalSyntheticLambda8.INSTANCE
            r13.sendRequest(r12, r14)
            goto L_0x0427
        L_0x01ae:
            int r13 = r10.pinnedMessageRow
            if (r12 != r13) goto L_0x01cc
            int r12 = r10.currentAccount
            android.content.SharedPreferences r12 = org.telegram.messenger.MessagesController.getNotificationsSettings(r12)
            android.content.SharedPreferences$Editor r13 = r12.edit()
            java.lang.String r14 = "PinnedMessages"
            boolean r1 = r12.getBoolean(r14, r2)
            r12 = r1 ^ 1
            r13.putBoolean(r14, r12)
            r13.commit()
            goto L_0x0427
        L_0x01cc:
            int r13 = r10.androidAutoAlertRow
            if (r12 != r13) goto L_0x01ea
            int r12 = r10.currentAccount
            android.content.SharedPreferences r12 = org.telegram.messenger.MessagesController.getNotificationsSettings(r12)
            android.content.SharedPreferences$Editor r13 = r12.edit()
            java.lang.String r14 = "EnableAutoNotifications"
            boolean r1 = r12.getBoolean(r14, r1)
            r12 = r1 ^ 1
            r13.putBoolean(r14, r12)
            r13.commit()
            goto L_0x0427
        L_0x01ea:
            int r13 = r10.badgeNumberShowRow
            if (r12 != r13) goto L_0x021d
            int r12 = r10.currentAccount
            android.content.SharedPreferences r12 = org.telegram.messenger.MessagesController.getNotificationsSettings(r12)
            android.content.SharedPreferences$Editor r12 = r12.edit()
            org.telegram.messenger.NotificationsController r13 = r10.getNotificationsController()
            boolean r1 = r13.showBadgeNumber
            org.telegram.messenger.NotificationsController r13 = r10.getNotificationsController()
            r14 = r1 ^ 1
            r13.showBadgeNumber = r14
            org.telegram.messenger.NotificationsController r13 = r10.getNotificationsController()
            boolean r13 = r13.showBadgeNumber
            java.lang.String r14 = "badgeNumber"
            r12.putBoolean(r14, r13)
            r12.commit()
            org.telegram.messenger.NotificationsController r12 = r10.getNotificationsController()
            r12.updateBadge()
            goto L_0x0427
        L_0x021d:
            int r13 = r10.badgeNumberMutedRow
            if (r12 != r13) goto L_0x0257
            int r12 = r10.currentAccount
            android.content.SharedPreferences r12 = org.telegram.messenger.MessagesController.getNotificationsSettings(r12)
            android.content.SharedPreferences$Editor r12 = r12.edit()
            org.telegram.messenger.NotificationsController r13 = r10.getNotificationsController()
            boolean r1 = r13.showBadgeMuted
            org.telegram.messenger.NotificationsController r13 = r10.getNotificationsController()
            r14 = r1 ^ 1
            r13.showBadgeMuted = r14
            org.telegram.messenger.NotificationsController r13 = r10.getNotificationsController()
            boolean r13 = r13.showBadgeMuted
            java.lang.String r14 = "badgeNumberMuted"
            r12.putBoolean(r14, r13)
            r12.commit()
            org.telegram.messenger.NotificationsController r12 = r10.getNotificationsController()
            r12.updateBadge()
            org.telegram.messenger.MessagesStorage r12 = r10.getMessagesStorage()
            r12.updateMutedDialogsFiltersCounters()
            goto L_0x0427
        L_0x0257:
            int r13 = r10.badgeNumberMessagesRow
            if (r12 != r13) goto L_0x028a
            int r12 = r10.currentAccount
            android.content.SharedPreferences r12 = org.telegram.messenger.MessagesController.getNotificationsSettings(r12)
            android.content.SharedPreferences$Editor r12 = r12.edit()
            org.telegram.messenger.NotificationsController r13 = r10.getNotificationsController()
            boolean r1 = r13.showBadgeMessages
            org.telegram.messenger.NotificationsController r13 = r10.getNotificationsController()
            r14 = r1 ^ 1
            r13.showBadgeMessages = r14
            org.telegram.messenger.NotificationsController r13 = r10.getNotificationsController()
            boolean r13 = r13.showBadgeMessages
            java.lang.String r14 = "badgeNumberMessages"
            r12.putBoolean(r14, r13)
            r12.commit()
            org.telegram.messenger.NotificationsController r12 = r10.getNotificationsController()
            r12.updateBadge()
            goto L_0x0427
        L_0x028a:
            int r13 = r10.notificationsServiceConnectionRow
            if (r12 != r13) goto L_0x02c4
            int r12 = r10.currentAccount
            android.content.SharedPreferences r12 = org.telegram.messenger.MessagesController.getNotificationsSettings(r12)
            org.telegram.messenger.MessagesController r13 = r10.getMessagesController()
            boolean r13 = r13.backgroundConnection
            java.lang.String r14 = "pushConnection"
            boolean r13 = r12.getBoolean(r14, r13)
            android.content.SharedPreferences$Editor r12 = r12.edit()
            r0 = r13 ^ 1
            r12.putBoolean(r14, r0)
            r12.commit()
            if (r13 != 0) goto L_0x02b8
            int r12 = r10.currentAccount
            org.telegram.tgnet.ConnectionsManager r12 = org.telegram.tgnet.ConnectionsManager.getInstance(r12)
            r12.setPushConnectionEnabled(r2)
            goto L_0x02c1
        L_0x02b8:
            int r12 = r10.currentAccount
            org.telegram.tgnet.ConnectionsManager r12 = org.telegram.tgnet.ConnectionsManager.getInstance(r12)
            r12.setPushConnectionEnabled(r1)
        L_0x02c1:
            r1 = r13
            goto L_0x0427
        L_0x02c4:
            int r13 = r10.accountsAllRow
            r5 = 3
            if (r12 != r13) goto L_0x030a
            android.content.SharedPreferences r12 = org.telegram.messenger.MessagesController.getGlobalNotificationsSettings()
            java.lang.String r13 = "AllAccounts"
            boolean r14 = r12.getBoolean(r13, r2)
            android.content.SharedPreferences$Editor r12 = r12.edit()
            r0 = r14 ^ 1
            r12.putBoolean(r13, r0)
            r12.commit()
            r12 = r14 ^ 1
            org.telegram.messenger.SharedConfig.showNotificationsForAllAccounts = r12
        L_0x02e3:
            if (r1 >= r5) goto L_0x0307
            boolean r12 = org.telegram.messenger.SharedConfig.showNotificationsForAllAccounts
            if (r12 == 0) goto L_0x02f1
            org.telegram.messenger.NotificationsController r12 = org.telegram.messenger.NotificationsController.getInstance(r1)
            r12.showNotifications()
            goto L_0x0304
        L_0x02f1:
            int r12 = r10.currentAccount
            if (r1 != r12) goto L_0x02fd
            org.telegram.messenger.NotificationsController r12 = org.telegram.messenger.NotificationsController.getInstance(r1)
            r12.showNotifications()
            goto L_0x0304
        L_0x02fd:
            org.telegram.messenger.NotificationsController r12 = org.telegram.messenger.NotificationsController.getInstance(r1)
            r12.hideNotifications()
        L_0x0304:
            int r1 = r1 + 1
            goto L_0x02e3
        L_0x0307:
            r1 = r14
            goto L_0x0427
        L_0x030a:
            int r13 = r10.notificationsServiceRow
            if (r12 != r13) goto L_0x0331
            int r12 = r10.currentAccount
            android.content.SharedPreferences r12 = org.telegram.messenger.MessagesController.getNotificationsSettings(r12)
            org.telegram.messenger.MessagesController r13 = r10.getMessagesController()
            boolean r13 = r13.keepAliveService
            java.lang.String r14 = "pushService"
            boolean r1 = r12.getBoolean(r14, r13)
            android.content.SharedPreferences$Editor r12 = r12.edit()
            r13 = r1 ^ 1
            r12.putBoolean(r14, r13)
            r12.commit()
            org.telegram.messenger.ApplicationLoader.startPushService()
            goto L_0x0427
        L_0x0331:
            int r13 = r10.callsVibrateRow
            if (r12 != r13) goto L_0x0356
            android.app.Activity r13 = r10.getParentActivity()
            if (r13 != 0) goto L_0x033c
            return
        L_0x033c:
            int r13 = r10.callsVibrateRow
            if (r12 != r13) goto L_0x0342
            java.lang.String r14 = "vibrate_calls"
        L_0x0342:
            android.app.Activity r13 = r10.getParentActivity()
            r3 = 0
            org.telegram.ui.NotificationsSettingsActivity$$ExternalSyntheticLambda5 r0 = new org.telegram.ui.NotificationsSettingsActivity$$ExternalSyntheticLambda5
            r0.<init>(r10, r12)
            android.app.Dialog r12 = org.telegram.ui.Components.AlertsCreator.createVibrationSelectDialog(r13, r3, r14, r0)
            r10.showDialog(r12)
            goto L_0x0427
        L_0x0356:
            int r13 = r10.repeatRow
            if (r12 != r13) goto L_0x0427
            org.telegram.ui.ActionBar.AlertDialog$Builder r13 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            android.app.Activity r6 = r10.getParentActivity()
            r13.<init>((android.content.Context) r6)
            r6 = 2131627503(0x7f0e0def, float:1.8882272E38)
            java.lang.String r7 = "RepeatNotifications"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            r13.setTitle(r6)
            r6 = 7
            java.lang.CharSequence[] r6 = new java.lang.CharSequence[r6]
            r7 = 2131627501(0x7f0e0ded, float:1.8882268E38)
            java.lang.String r8 = "RepeatDisabled"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r8, r7)
            r6[r1] = r7
            java.lang.String r7 = "Minutes"
            r8 = 5
            java.lang.String r9 = org.telegram.messenger.LocaleController.formatPluralString(r7, r8)
            r6[r2] = r9
            r9 = 10
            java.lang.String r9 = org.telegram.messenger.LocaleController.formatPluralString(r7, r9)
            r6[r0] = r9
            r9 = 30
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatPluralString(r7, r9)
            r6[r5] = r7
            r5 = 4
            java.lang.String r7 = "Hours"
            java.lang.String r9 = org.telegram.messenger.LocaleController.formatPluralString(r7, r2)
            r6[r5] = r9
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r7, r0)
            r6[r8] = r0
            r0 = 6
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatPluralString(r7, r5)
            r6[r0] = r5
            org.telegram.ui.NotificationsSettingsActivity$$ExternalSyntheticLambda1 r0 = new org.telegram.ui.NotificationsSettingsActivity$$ExternalSyntheticLambda1
            r0.<init>(r10, r12)
            r13.setItems(r6, r0)
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r13.setNegativeButton(r12, r14)
            org.telegram.ui.ActionBar.AlertDialog r12 = r13.create()
            r10.showDialog(r12)
            goto L_0x0427
        L_0x03c3:
            if (r12 != r14) goto L_0x03c9
            java.util.ArrayList<org.telegram.ui.NotificationsSettingsActivity$NotificationException> r14 = r10.exceptionUsers
            r0 = 1
            goto L_0x03d3
        L_0x03c9:
            int r14 = r10.groupRow
            if (r12 != r14) goto L_0x03d1
            java.util.ArrayList<org.telegram.ui.NotificationsSettingsActivity$NotificationException> r14 = r10.exceptionChats
            r0 = 0
            goto L_0x03d3
        L_0x03d1:
            java.util.ArrayList<org.telegram.ui.NotificationsSettingsActivity$NotificationException> r14 = r10.exceptionChannels
        L_0x03d3:
            if (r14 != 0) goto L_0x03d6
            return
        L_0x03d6:
            r3 = r11
            org.telegram.ui.Cells.NotificationsCheckCell r3 = (org.telegram.ui.Cells.NotificationsCheckCell) r3
            org.telegram.messenger.NotificationsController r4 = r10.getNotificationsController()
            boolean r4 = r4.isGlobalNotificationsEnabled((int) r0)
            boolean r5 = org.telegram.messenger.LocaleController.isRTL
            r6 = 1117257728(0x42980000, float:76.0)
            if (r5 == 0) goto L_0x03f0
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r5 = (float) r5
            int r5 = (r13 > r5 ? 1 : (r13 == r5 ? 0 : -1))
            if (r5 <= 0) goto L_0x0402
        L_0x03f0:
            boolean r5 = org.telegram.messenger.LocaleController.isRTL
            if (r5 != 0) goto L_0x041e
            int r5 = r11.getMeasuredWidth()
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r5 = r5 - r6
            float r5 = (float) r5
            int r13 = (r13 > r5 ? 1 : (r13 == r5 ? 0 : -1))
            if (r13 < 0) goto L_0x041e
        L_0x0402:
            org.telegram.messenger.NotificationsController r13 = r10.getNotificationsController()
            if (r4 != 0) goto L_0x040a
            r14 = 0
            goto L_0x040d
        L_0x040a:
            r14 = 2147483647(0x7fffffff, float:NaN)
        L_0x040d:
            r13.setGlobalNotificationsEnabled(r0, r14)
            r10.showExceptionsAlert(r12)
            r13 = r4 ^ 1
            r3.setChecked(r13, r1)
            org.telegram.ui.NotificationsSettingsActivity$ListAdapter r13 = r10.adapter
            r13.notifyItemChanged(r12)
            goto L_0x0426
        L_0x041e:
            org.telegram.ui.NotificationsCustomSettingsActivity r12 = new org.telegram.ui.NotificationsCustomSettingsActivity
            r12.<init>(r0, r14)
            r10.presentFragment(r12)
        L_0x0426:
            r1 = r4
        L_0x0427:
            boolean r12 = r11 instanceof org.telegram.ui.Cells.TextCheckCell
            if (r12 == 0) goto L_0x0432
            org.telegram.ui.Cells.TextCheckCell r11 = (org.telegram.ui.Cells.TextCheckCell) r11
            r12 = r1 ^ 1
            r11.setChecked(r12)
        L_0x0432:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.NotificationsSettingsActivity.lambda$createView$8(android.view.View, int, float, float):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$4(DialogInterface dialogInterface, int i) {
        if (!this.reseting) {
            this.reseting = true;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC$TL_account_resetNotifySettings(), new NotificationsSettingsActivity$$ExternalSyntheticLambda7(this));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$3(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new NotificationsSettingsActivity$$ExternalSyntheticLambda4(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$2() {
        getMessagesController().enableJoined = true;
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
        getMessagesStorage().updateMutedDialogsFiltersCounters();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$6(int i) {
        this.adapter.notifyItemChanged(i);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$7(int i, DialogInterface dialogInterface, int i2) {
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
            r3 = 2131626736(0x7f0e0af0, float:1.8880717E38)
            java.lang.Object[] r5 = new java.lang.Object[r5]
            r5[r4] = r0
            java.lang.String r0 = "NotificationsExceptionsSingleAlert"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r3, r5)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2.setMessage(r0)
            goto L_0x0086
        L_0x0072:
            r3 = 2131626735(0x7f0e0aef, float:1.8880715E38)
            java.lang.Object[] r5 = new java.lang.Object[r5]
            r5[r4] = r0
            java.lang.String r0 = "NotificationsExceptionsAlert"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r3, r5)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            r2.setMessage(r0)
        L_0x0086:
            r0 = 2131626734(0x7f0e0aee, float:1.8880713E38)
            java.lang.String r3 = "NotificationsExceptions"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r0)
            r2.setTitle(r0)
            r0 = 2131628429(0x7f0e118d, float:1.888415E38)
            java.lang.String r3 = "ViewExceptions"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r0)
            org.telegram.ui.NotificationsSettingsActivity$$ExternalSyntheticLambda2 r3 = new org.telegram.ui.NotificationsSettingsActivity$$ExternalSyntheticLambda2
            r3.<init>(r6, r7)
            r2.setNeutralButton(r0, r3)
            r7 = 2131626773(0x7f0e0b15, float:1.8880792E38)
            java.lang.String r0 = "OK"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r0, r7)
            r2.setNegativeButton(r7, r1)
            org.telegram.ui.ActionBar.AlertDialog r7 = r2.create()
            r6.showDialog(r7)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.NotificationsSettingsActivity.showExceptionsAlert(int):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showExceptionsAlert$9(ArrayList arrayList, DialogInterface dialogInterface, int i) {
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
            View view;
            View view2;
            if (i == 0) {
                view2 = new HeaderCell(this.mContext);
                view2.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            } else if (i == 1) {
                view2 = new TextCheckCell(this.mContext);
                view2.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            } else if (i == 2) {
                view2 = new TextDetailSettingsCell(this.mContext);
                view2.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            } else if (i != 3) {
                if (i == 4) {
                    view = new ShadowSectionCell(this.mContext);
                } else if (i != 5) {
                    view = new TextInfoPrivacyCell(this.mContext);
                    view.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                } else {
                    view2 = new TextSettingsCell(this.mContext);
                    view2.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                }
                return new RecyclerListView.Holder(view);
            } else {
                view2 = new NotificationsCheckCell(this.mContext);
                view2.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            }
            view = view2;
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            String string;
            ArrayList access$4200;
            int i2;
            String str;
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
                        textCheckCell.setTextAndCheck(LocaleController.getString("BadgeNumberShow", NUM), NotificationsSettingsActivity.this.getNotificationsController().showBadgeNumber, true);
                        return;
                    } else if (i == NotificationsSettingsActivity.this.badgeNumberMutedRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("BadgeNumberMutedChats", NUM), NotificationsSettingsActivity.this.getNotificationsController().showBadgeMuted, true);
                        return;
                    } else if (i == NotificationsSettingsActivity.this.badgeNumberMessagesRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("BadgeNumberUnread", NUM), NotificationsSettingsActivity.this.getNotificationsController().showBadgeMessages, false);
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
                    int i3 = (!z && i2 - 31536000 < currentTime) ? 2 : 0;
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
                    notificationsCheckCell.setTextAndValueAndCheck(str2, sb, z, i3, i != NotificationsSettingsActivity.this.channelsRow);
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
                        int i4 = notificationsSettings3.getInt("vibrate_calls", 0);
                        if (i4 == 0) {
                            textSettingsCell.setTextAndValue(LocaleController.getString("Vibrate", NUM), LocaleController.getString("VibrationDefault", NUM), true);
                            return;
                        } else if (i4 == 1) {
                            textSettingsCell.setTextAndValue(LocaleController.getString("Vibrate", NUM), LocaleController.getString("Short", NUM), true);
                            return;
                        } else if (i4 == 2) {
                            textSettingsCell.setTextAndValue(LocaleController.getString("Vibrate", NUM), LocaleController.getString("VibrationDisabled", NUM), true);
                            return;
                        } else if (i4 == 3) {
                            textSettingsCell.setTextAndValue(LocaleController.getString("Vibrate", NUM), LocaleController.getString("Long", NUM), true);
                            return;
                        } else if (i4 == 4) {
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

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{HeaderCell.class, TextCheckCell.class, TextDetailSettingsCell.class, TextSettingsCell.class, NotificationsCheckCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrack"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackChecked"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrack"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackChecked"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteValueText"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_LINKCOLOR, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteLinkText"));
        return arrayList;
    }
}
