package org.telegram.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.SharedConfig;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_clearAllDrafts;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.NotificationsCheckCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.voip.VoIPHelper;

public class DataSettingsActivity extends BaseFragment {
    /* access modifiers changed from: private */
    public int autoplayGifsRow;
    /* access modifiers changed from: private */
    public int autoplayHeaderRow;
    /* access modifiers changed from: private */
    public int autoplaySectionRow;
    /* access modifiers changed from: private */
    public int autoplayVideoRow;
    /* access modifiers changed from: private */
    public int callsSection2Row;
    /* access modifiers changed from: private */
    public int callsSectionRow;
    /* access modifiers changed from: private */
    public int clearDraftsRow;
    /* access modifiers changed from: private */
    public int clearDraftsSectionRow;
    /* access modifiers changed from: private */
    public int dataUsageRow;
    /* access modifiers changed from: private */
    public int enableAllStreamInfoRow;
    /* access modifiers changed from: private */
    public int enableAllStreamRow;
    /* access modifiers changed from: private */
    public int enableCacheStreamRow;
    /* access modifiers changed from: private */
    public int enableMkvRow;
    /* access modifiers changed from: private */
    public int enableStreamRow;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    /* access modifiers changed from: private */
    public int mediaDownloadSection2Row;
    /* access modifiers changed from: private */
    public int mediaDownloadSectionRow;
    /* access modifiers changed from: private */
    public int mobileRow;
    /* access modifiers changed from: private */
    public int proxyRow;
    /* access modifiers changed from: private */
    public int proxySection2Row;
    /* access modifiers changed from: private */
    public int proxySectionRow;
    /* access modifiers changed from: private */
    public int quickRepliesRow;
    /* access modifiers changed from: private */
    public int resetDownloadRow;
    /* access modifiers changed from: private */
    public int roamingRow;
    /* access modifiers changed from: private */
    public int rowCount;
    /* access modifiers changed from: private */
    public int saveToGalleryChannelsRow;
    /* access modifiers changed from: private */
    public int saveToGalleryDividerRow;
    /* access modifiers changed from: private */
    public int saveToGalleryGroupsRow;
    /* access modifiers changed from: private */
    public int saveToGalleryPeerRow;
    /* access modifiers changed from: private */
    public int saveToGallerySectionRow;
    /* access modifiers changed from: private */
    public ArrayList<File> storageDirs;
    /* access modifiers changed from: private */
    public int storageNumRow;
    /* access modifiers changed from: private */
    public int storageUsageRow;
    /* access modifiers changed from: private */
    public int streamSectionRow;
    /* access modifiers changed from: private */
    public int usageSection2Row;
    /* access modifiers changed from: private */
    public int usageSectionRow;
    /* access modifiers changed from: private */
    public int useLessDataForCallsRow;
    /* access modifiers changed from: private */
    public int wifiRow;

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        DownloadController.getInstance(this.currentAccount).loadAutoDownloadConfig(true);
        this.rowCount = 0;
        int i = 0 + 1;
        this.rowCount = i;
        this.usageSectionRow = 0;
        int i2 = i + 1;
        this.rowCount = i2;
        this.storageUsageRow = i;
        this.rowCount = i2 + 1;
        this.dataUsageRow = i2;
        this.storageNumRow = -1;
        if (Build.VERSION.SDK_INT >= 19) {
            ArrayList<File> rootDirs = AndroidUtilities.getRootDirs();
            this.storageDirs = rootDirs;
            if (rootDirs.size() > 1) {
                int i3 = this.rowCount;
                this.rowCount = i3 + 1;
                this.storageNumRow = i3;
            }
        }
        int i4 = this.rowCount;
        int i5 = i4 + 1;
        this.rowCount = i5;
        this.usageSection2Row = i4;
        int i6 = i5 + 1;
        this.rowCount = i6;
        this.mediaDownloadSectionRow = i5;
        int i7 = i6 + 1;
        this.rowCount = i7;
        this.mobileRow = i6;
        int i8 = i7 + 1;
        this.rowCount = i8;
        this.wifiRow = i7;
        int i9 = i8 + 1;
        this.rowCount = i9;
        this.roamingRow = i8;
        int i10 = i9 + 1;
        this.rowCount = i10;
        this.resetDownloadRow = i9;
        int i11 = i10 + 1;
        this.rowCount = i11;
        this.mediaDownloadSection2Row = i10;
        int i12 = i11 + 1;
        this.rowCount = i12;
        this.saveToGallerySectionRow = i11;
        int i13 = i12 + 1;
        this.rowCount = i13;
        this.saveToGalleryPeerRow = i12;
        int i14 = i13 + 1;
        this.rowCount = i14;
        this.saveToGalleryGroupsRow = i13;
        int i15 = i14 + 1;
        this.rowCount = i15;
        this.saveToGalleryChannelsRow = i14;
        int i16 = i15 + 1;
        this.rowCount = i16;
        this.saveToGalleryDividerRow = i15;
        int i17 = i16 + 1;
        this.rowCount = i17;
        this.autoplayHeaderRow = i16;
        int i18 = i17 + 1;
        this.rowCount = i18;
        this.autoplayGifsRow = i17;
        int i19 = i18 + 1;
        this.rowCount = i19;
        this.autoplayVideoRow = i18;
        int i20 = i19 + 1;
        this.rowCount = i20;
        this.autoplaySectionRow = i19;
        int i21 = i20 + 1;
        this.rowCount = i21;
        this.streamSectionRow = i20;
        int i22 = i21 + 1;
        this.rowCount = i22;
        this.enableStreamRow = i21;
        if (BuildVars.DEBUG_VERSION) {
            int i23 = i22 + 1;
            this.rowCount = i23;
            this.enableMkvRow = i22;
            this.rowCount = i23 + 1;
            this.enableAllStreamRow = i23;
        } else {
            this.enableAllStreamRow = -1;
            this.enableMkvRow = -1;
        }
        int i24 = this.rowCount;
        int i25 = i24 + 1;
        this.rowCount = i25;
        this.enableAllStreamInfoRow = i24;
        this.enableCacheStreamRow = -1;
        int i26 = i25 + 1;
        this.rowCount = i26;
        this.callsSectionRow = i25;
        int i27 = i26 + 1;
        this.rowCount = i27;
        this.useLessDataForCallsRow = i26;
        int i28 = i27 + 1;
        this.rowCount = i28;
        this.quickRepliesRow = i27;
        int i29 = i28 + 1;
        this.rowCount = i29;
        this.callsSection2Row = i28;
        int i30 = i29 + 1;
        this.rowCount = i30;
        this.proxySectionRow = i29;
        int i31 = i30 + 1;
        this.rowCount = i31;
        this.proxyRow = i30;
        int i32 = i31 + 1;
        this.rowCount = i32;
        this.proxySection2Row = i31;
        int i33 = i32 + 1;
        this.rowCount = i33;
        this.clearDraftsRow = i32;
        this.rowCount = i33 + 1;
        this.clearDraftsSectionRow = i33;
        return true;
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setTitle(LocaleController.getString("DataSettings", NUM));
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    DataSettingsActivity.this.finishFragment();
                }
            }
        });
        this.listAdapter = new ListAdapter(context);
        FrameLayout frameLayout = new FrameLayout(context);
        this.fragmentView = frameLayout;
        frameLayout.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setVerticalScrollBarEnabled(false);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        ((FrameLayout) this.fragmentView).addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListenerExtended) new DataSettingsActivity$$ExternalSyntheticLambda6(this, context));
        return this.fragmentView;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v1, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v3, resolved type: int} */
    /* JADX WARNING: type inference failed for: r2v0 */
    /* JADX WARNING: type inference failed for: r2v2 */
    /* JADX WARNING: type inference failed for: r2v7 */
    /* JADX WARNING: type inference failed for: r2v8 */
    /* access modifiers changed from: private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$createView$6(android.content.Context r10, android.view.View r11, int r12, float r13, float r14) {
        /*
            r9 = this;
            int r14 = r9.saveToGalleryGroupsRow
            r0 = 4
            r1 = 2
            r2 = 0
            r3 = 1
            if (r12 == r14) goto L_0x0397
            int r4 = r9.saveToGalleryChannelsRow
            if (r12 == r4) goto L_0x0397
            int r4 = r9.saveToGalleryPeerRow
            if (r12 != r4) goto L_0x0012
            goto L_0x0397
        L_0x0012:
            int r14 = r9.mobileRow
            r4 = 3
            if (r12 == r14) goto L_0x02af
            int r14 = r9.roamingRow
            if (r12 == r14) goto L_0x02af
            int r14 = r9.wifiRow
            if (r12 != r14) goto L_0x0021
            goto L_0x02af
        L_0x0021:
            int r13 = r9.resetDownloadRow
            java.lang.String r14 = "dialogTextRed2"
            r5 = -1
            r6 = 0
            r7 = 2131624817(0x7f0e0371, float:1.8876824E38)
            java.lang.String r8 = "Cancel"
            if (r12 != r13) goto L_0x008d
            android.app.Activity r10 = r9.getParentActivity()
            if (r10 == 0) goto L_0x008c
            boolean r10 = r11.isEnabled()
            if (r10 != 0) goto L_0x003b
            goto L_0x008c
        L_0x003b:
            org.telegram.ui.ActionBar.AlertDialog$Builder r10 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            android.app.Activity r11 = r9.getParentActivity()
            r10.<init>((android.content.Context) r11)
            r11 = 2131627986(0x7f0e0fd2, float:1.8883252E38)
            java.lang.String r12 = "ResetAutomaticMediaDownloadAlertTitle"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r12, r11)
            r10.setTitle(r11)
            r11 = 2131627985(0x7f0e0fd1, float:1.888325E38)
            java.lang.String r12 = "ResetAutomaticMediaDownloadAlert"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r12, r11)
            r10.setMessage(r11)
            r11 = 2131627977(0x7f0e0fc9, float:1.8883234E38)
            java.lang.String r12 = "Reset"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r12, r11)
            org.telegram.ui.DataSettingsActivity$$ExternalSyntheticLambda0 r12 = new org.telegram.ui.DataSettingsActivity$$ExternalSyntheticLambda0
            r12.<init>(r9)
            r10.setPositiveButton(r11, r12)
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r8, r7)
            r10.setNegativeButton(r11, r6)
            org.telegram.ui.ActionBar.AlertDialog r10 = r10.create()
            r9.showDialog(r10)
            android.view.View r10 = r10.getButton(r5)
            android.widget.TextView r10 = (android.widget.TextView) r10
            if (r10 == 0) goto L_0x03af
            int r11 = org.telegram.ui.ActionBar.Theme.getColor(r14)
            r10.setTextColor(r11)
            goto L_0x03af
        L_0x008c:
            return
        L_0x008d:
            int r13 = r9.storageUsageRow
            if (r12 != r13) goto L_0x009b
            org.telegram.ui.CacheControlActivity r10 = new org.telegram.ui.CacheControlActivity
            r10.<init>()
            r9.presentFragment(r10)
            goto L_0x03af
        L_0x009b:
            int r13 = r9.useLessDataForCallsRow
            if (r12 != r13) goto L_0x0108
            android.content.SharedPreferences r10 = org.telegram.messenger.MessagesController.getGlobalMainSettings()
            int r11 = org.telegram.ui.Components.voip.VoIPHelper.getDataSavingDefault()
            java.lang.String r13 = "VoipDataSaving"
            int r11 = r10.getInt(r13, r11)
            if (r11 == 0) goto L_0x00b5
            if (r11 == r3) goto L_0x00bb
            if (r11 == r1) goto L_0x00b9
            if (r11 == r4) goto L_0x00b7
        L_0x00b5:
            r11 = 0
            goto L_0x00bc
        L_0x00b7:
            r11 = 1
            goto L_0x00bc
        L_0x00b9:
            r11 = 3
            goto L_0x00bc
        L_0x00bb:
            r11 = 2
        L_0x00bc:
            android.app.Activity r13 = r9.getParentActivity()
            java.lang.String[] r14 = new java.lang.String[r0]
            r0 = 2131628776(0x7f0e12e8, float:1.8884854E38)
            java.lang.String r5 = "UseLessDataNever"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r5, r0)
            r14[r2] = r0
            r0 = 2131628778(0x7f0e12ea, float:1.8884858E38)
            java.lang.String r2 = "UseLessDataOnRoaming"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r14[r3] = r0
            r0 = 2131628777(0x7f0e12e9, float:1.8884856E38)
            java.lang.String r2 = "UseLessDataOnMobile"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r14[r1] = r0
            r0 = 2131628775(0x7f0e12e7, float:1.8884852E38)
            java.lang.String r1 = "UseLessDataAlways"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r14[r4] = r0
            r0 = 2131629198(0x7f0e148e, float:1.888571E38)
            java.lang.String r1 = "VoipUseLessData"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            org.telegram.ui.DataSettingsActivity$$ExternalSyntheticLambda2 r1 = new org.telegram.ui.DataSettingsActivity$$ExternalSyntheticLambda2
            r1.<init>(r9, r10, r12)
            android.app.Dialog r10 = org.telegram.ui.Components.AlertsCreator.createSingleChoiceDialog(r13, r14, r0, r11, r1)
            r9.setVisibleDialog(r10)
            r10.show()
            goto L_0x03af
        L_0x0108:
            int r13 = r9.dataUsageRow
            if (r12 != r13) goto L_0x0116
            org.telegram.ui.DataUsageActivity r10 = new org.telegram.ui.DataUsageActivity
            r10.<init>()
            r9.presentFragment(r10)
            goto L_0x03af
        L_0x0116:
            int r13 = r9.storageNumRow
            if (r12 != r13) goto L_0x01d6
            org.telegram.ui.ActionBar.AlertDialog$Builder r11 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            android.app.Activity r12 = r9.getParentActivity()
            r11.<init>((android.content.Context) r12)
            r12 = 2131628477(0x7f0e11bd, float:1.8884248E38)
            java.lang.String r13 = "StoragePath"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r13, r12)
            r11.setTitle(r12)
            android.widget.LinearLayout r12 = new android.widget.LinearLayout
            android.app.Activity r13 = r9.getParentActivity()
            r12.<init>(r13)
            r12.setOrientation(r3)
            r11.setView(r12)
            java.util.ArrayList<java.io.File> r13 = r9.storageDirs
            java.lang.Object r13 = r13.get(r2)
            java.io.File r13 = (java.io.File) r13
            java.lang.String r13 = r13.getAbsolutePath()
            java.lang.String r14 = org.telegram.messenger.SharedConfig.storageCacheDir
            boolean r14 = android.text.TextUtils.isEmpty(r14)
            if (r14 != 0) goto L_0x0174
            java.util.ArrayList<java.io.File> r14 = r9.storageDirs
            int r14 = r14.size()
            r0 = 0
        L_0x0159:
            if (r0 >= r14) goto L_0x0174
            java.util.ArrayList<java.io.File> r1 = r9.storageDirs
            java.lang.Object r1 = r1.get(r0)
            java.io.File r1 = (java.io.File) r1
            java.lang.String r1 = r1.getAbsolutePath()
            java.lang.String r3 = org.telegram.messenger.SharedConfig.storageCacheDir
            boolean r3 = r1.startsWith(r3)
            if (r3 == 0) goto L_0x0171
            r13 = r1
            goto L_0x0174
        L_0x0171:
            int r0 = r0 + 1
            goto L_0x0159
        L_0x0174:
            java.util.ArrayList<java.io.File> r14 = r9.storageDirs
            int r14 = r14.size()
            r0 = 0
        L_0x017b:
            if (r0 >= r14) goto L_0x01c6
            java.util.ArrayList<java.io.File> r1 = r9.storageDirs
            java.lang.Object r1 = r1.get(r0)
            java.io.File r1 = (java.io.File) r1
            java.lang.String r1 = r1.getAbsolutePath()
            org.telegram.ui.Cells.RadioColorCell r3 = new org.telegram.ui.Cells.RadioColorCell
            r3.<init>(r10)
            r4 = 1082130432(0x40800000, float:4.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r3.setPadding(r5, r2, r4, r2)
            java.lang.Integer r4 = java.lang.Integer.valueOf(r0)
            r3.setTag(r4)
            java.lang.String r4 = "radioBackground"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            java.lang.String r5 = "dialogRadioBackgroundChecked"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r3.setCheckColor(r4, r5)
            boolean r4 = r1.startsWith(r13)
            r3.setTextAndValue(r1, r4)
            r12.addView(r3)
            org.telegram.ui.DataSettingsActivity$$ExternalSyntheticLambda3 r4 = new org.telegram.ui.DataSettingsActivity$$ExternalSyntheticLambda3
            r4.<init>(r9, r1, r11)
            r3.setOnClickListener(r4)
            int r0 = r0 + 1
            goto L_0x017b
        L_0x01c6:
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r8, r7)
            r11.setNegativeButton(r10, r6)
            org.telegram.ui.ActionBar.AlertDialog r10 = r11.create()
            r9.showDialog(r10)
            goto L_0x03af
        L_0x01d6:
            int r10 = r9.proxyRow
            if (r12 != r10) goto L_0x01e4
            org.telegram.ui.ProxyListActivity r10 = new org.telegram.ui.ProxyListActivity
            r10.<init>()
            r9.presentFragment(r10)
            goto L_0x03af
        L_0x01e4:
            int r10 = r9.enableStreamRow
            if (r12 != r10) goto L_0x01f4
            org.telegram.messenger.SharedConfig.toggleStreamMedia()
            org.telegram.ui.Cells.TextCheckCell r11 = (org.telegram.ui.Cells.TextCheckCell) r11
            boolean r10 = org.telegram.messenger.SharedConfig.streamMedia
            r11.setChecked(r10)
            goto L_0x03af
        L_0x01f4:
            int r10 = r9.enableAllStreamRow
            if (r12 != r10) goto L_0x0204
            org.telegram.messenger.SharedConfig.toggleStreamAllVideo()
            org.telegram.ui.Cells.TextCheckCell r11 = (org.telegram.ui.Cells.TextCheckCell) r11
            boolean r10 = org.telegram.messenger.SharedConfig.streamAllVideo
            r11.setChecked(r10)
            goto L_0x03af
        L_0x0204:
            int r10 = r9.enableMkvRow
            if (r12 != r10) goto L_0x0214
            org.telegram.messenger.SharedConfig.toggleStreamMkv()
            org.telegram.ui.Cells.TextCheckCell r11 = (org.telegram.ui.Cells.TextCheckCell) r11
            boolean r10 = org.telegram.messenger.SharedConfig.streamMkv
            r11.setChecked(r10)
            goto L_0x03af
        L_0x0214:
            int r10 = r9.enableCacheStreamRow
            if (r12 != r10) goto L_0x0224
            org.telegram.messenger.SharedConfig.toggleSaveStreamMedia()
            org.telegram.ui.Cells.TextCheckCell r11 = (org.telegram.ui.Cells.TextCheckCell) r11
            boolean r10 = org.telegram.messenger.SharedConfig.saveStreamMedia
            r11.setChecked(r10)
            goto L_0x03af
        L_0x0224:
            int r10 = r9.quickRepliesRow
            if (r12 != r10) goto L_0x0232
            org.telegram.ui.QuickRepliesSettingsActivity r10 = new org.telegram.ui.QuickRepliesSettingsActivity
            r10.<init>()
            r9.presentFragment(r10)
            goto L_0x03af
        L_0x0232:
            int r10 = r9.autoplayGifsRow
            if (r12 != r10) goto L_0x0246
            org.telegram.messenger.SharedConfig.toggleAutoplayGifs()
            boolean r10 = r11 instanceof org.telegram.ui.Cells.TextCheckCell
            if (r10 == 0) goto L_0x03af
            org.telegram.ui.Cells.TextCheckCell r11 = (org.telegram.ui.Cells.TextCheckCell) r11
            boolean r10 = org.telegram.messenger.SharedConfig.autoplayGifs
            r11.setChecked(r10)
            goto L_0x03af
        L_0x0246:
            int r10 = r9.autoplayVideoRow
            if (r12 != r10) goto L_0x025a
            org.telegram.messenger.SharedConfig.toggleAutoplayVideo()
            boolean r10 = r11 instanceof org.telegram.ui.Cells.TextCheckCell
            if (r10 == 0) goto L_0x03af
            org.telegram.ui.Cells.TextCheckCell r11 = (org.telegram.ui.Cells.TextCheckCell) r11
            boolean r10 = org.telegram.messenger.SharedConfig.autoplayVideo
            r11.setChecked(r10)
            goto L_0x03af
        L_0x025a:
            int r10 = r9.clearDraftsRow
            if (r12 != r10) goto L_0x03af
            org.telegram.ui.ActionBar.AlertDialog$Builder r10 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            android.app.Activity r11 = r9.getParentActivity()
            r10.<init>((android.content.Context) r11)
            r11 = 2131624422(0x7f0e01e6, float:1.8876023E38)
            java.lang.String r12 = "AreYouSureClearDraftsTitle"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r12, r11)
            r10.setTitle(r11)
            r11 = 2131624421(0x7f0e01e5, float:1.8876021E38)
            java.lang.String r12 = "AreYouSureClearDrafts"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r12, r11)
            r10.setMessage(r11)
            r11 = 2131625366(0x7f0e0596, float:1.8877938E38)
            java.lang.String r12 = "Delete"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r12, r11)
            org.telegram.ui.DataSettingsActivity$$ExternalSyntheticLambda1 r12 = new org.telegram.ui.DataSettingsActivity$$ExternalSyntheticLambda1
            r12.<init>(r9)
            r10.setPositiveButton(r11, r12)
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r8, r7)
            r10.setNegativeButton(r11, r6)
            org.telegram.ui.ActionBar.AlertDialog r10 = r10.create()
            r9.showDialog(r10)
            android.view.View r10 = r10.getButton(r5)
            android.widget.TextView r10 = (android.widget.TextView) r10
            if (r10 == 0) goto L_0x03af
            int r11 = org.telegram.ui.ActionBar.Theme.getColor(r14)
            r10.setTextColor(r11)
            goto L_0x03af
        L_0x02af:
            boolean r10 = org.telegram.messenger.LocaleController.isRTL
            r14 = 1117257728(0x42980000, float:76.0)
            if (r10 == 0) goto L_0x02be
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r14)
            float r10 = (float) r10
            int r10 = (r13 > r10 ? 1 : (r13 == r10 ? 0 : -1))
            if (r10 <= 0) goto L_0x02d0
        L_0x02be:
            boolean r10 = org.telegram.messenger.LocaleController.isRTL
            if (r10 != 0) goto L_0x0383
            int r10 = r11.getMeasuredWidth()
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            int r10 = r10 - r14
            float r10 = (float) r10
            int r10 = (r13 > r10 ? 1 : (r13 == r10 ? 0 : -1))
            if (r10 < 0) goto L_0x0383
        L_0x02d0:
            org.telegram.ui.DataSettingsActivity$ListAdapter r10 = r9.listAdapter
            int r13 = r9.resetDownloadRow
            boolean r10 = r10.isRowEnabled(r13)
            r13 = r11
            org.telegram.ui.Cells.NotificationsCheckCell r13 = (org.telegram.ui.Cells.NotificationsCheckCell) r13
            boolean r14 = r13.isChecked()
            int r0 = r9.mobileRow
            if (r12 != r0) goto L_0x02f8
            int r0 = r9.currentAccount
            org.telegram.messenger.DownloadController r0 = org.telegram.messenger.DownloadController.getInstance(r0)
            org.telegram.messenger.DownloadController$Preset r0 = r0.mobilePreset
            int r1 = r9.currentAccount
            org.telegram.messenger.DownloadController r1 = org.telegram.messenger.DownloadController.getInstance(r1)
            org.telegram.messenger.DownloadController$Preset r1 = r1.mediumPreset
            java.lang.String r5 = "mobilePreset"
            java.lang.String r6 = "currentMobilePreset"
            goto L_0x0328
        L_0x02f8:
            int r0 = r9.wifiRow
            if (r12 != r0) goto L_0x0312
            int r0 = r9.currentAccount
            org.telegram.messenger.DownloadController r0 = org.telegram.messenger.DownloadController.getInstance(r0)
            org.telegram.messenger.DownloadController$Preset r0 = r0.wifiPreset
            int r1 = r9.currentAccount
            org.telegram.messenger.DownloadController r1 = org.telegram.messenger.DownloadController.getInstance(r1)
            org.telegram.messenger.DownloadController$Preset r1 = r1.highPreset
            java.lang.String r5 = "wifiPreset"
            java.lang.String r6 = "currentWifiPreset"
            r2 = 1
            goto L_0x0328
        L_0x0312:
            int r0 = r9.currentAccount
            org.telegram.messenger.DownloadController r0 = org.telegram.messenger.DownloadController.getInstance(r0)
            org.telegram.messenger.DownloadController$Preset r0 = r0.roamingPreset
            int r2 = r9.currentAccount
            org.telegram.messenger.DownloadController r2 = org.telegram.messenger.DownloadController.getInstance(r2)
            org.telegram.messenger.DownloadController$Preset r2 = r2.lowPreset
            java.lang.String r5 = "roamingPreset"
            java.lang.String r6 = "currentRoamingPreset"
            r1 = r2
            r2 = 2
        L_0x0328:
            if (r14 != 0) goto L_0x0332
            boolean r7 = r0.enabled
            if (r7 == 0) goto L_0x0332
            r0.set((org.telegram.messenger.DownloadController.Preset) r1)
            goto L_0x0337
        L_0x0332:
            boolean r1 = r0.enabled
            r1 = r1 ^ r3
            r0.enabled = r1
        L_0x0337:
            int r1 = r9.currentAccount
            android.content.SharedPreferences r1 = org.telegram.messenger.MessagesController.getMainSettings(r1)
            android.content.SharedPreferences$Editor r1 = r1.edit()
            java.lang.String r0 = r0.toString()
            r1.putString(r5, r0)
            r1.putInt(r6, r4)
            r1.commit()
            r14 = r14 ^ r3
            r13.setChecked(r14)
            org.telegram.ui.Components.RecyclerListView r13 = r9.listView
            androidx.recyclerview.widget.RecyclerView$ViewHolder r11 = r13.findContainingViewHolder(r11)
            if (r11 == 0) goto L_0x035f
            org.telegram.ui.DataSettingsActivity$ListAdapter r13 = r9.listAdapter
            r13.onBindViewHolder(r11, r12)
        L_0x035f:
            int r11 = r9.currentAccount
            org.telegram.messenger.DownloadController r11 = org.telegram.messenger.DownloadController.getInstance(r11)
            r11.checkAutodownloadSettings()
            int r11 = r9.currentAccount
            org.telegram.messenger.DownloadController r11 = org.telegram.messenger.DownloadController.getInstance(r11)
            r11.savePresetToServer(r2)
            org.telegram.ui.DataSettingsActivity$ListAdapter r11 = r9.listAdapter
            int r12 = r9.resetDownloadRow
            boolean r11 = r11.isRowEnabled(r12)
            if (r10 == r11) goto L_0x03af
            org.telegram.ui.DataSettingsActivity$ListAdapter r10 = r9.listAdapter
            int r11 = r9.resetDownloadRow
            r10.notifyItemChanged(r11)
            goto L_0x03af
        L_0x0383:
            int r10 = r9.mobileRow
            if (r12 != r10) goto L_0x0389
            r1 = 0
            goto L_0x038e
        L_0x0389:
            int r10 = r9.wifiRow
            if (r12 != r10) goto L_0x038e
            r1 = 1
        L_0x038e:
            org.telegram.ui.DataAutoDownloadActivity r10 = new org.telegram.ui.DataAutoDownloadActivity
            r10.<init>(r1)
            r9.presentFragment(r10)
            goto L_0x03af
        L_0x0397:
            if (r12 != r14) goto L_0x039b
            r0 = 2
            goto L_0x03a1
        L_0x039b:
            int r10 = r9.saveToGalleryChannelsRow
            if (r12 != r10) goto L_0x03a0
            goto L_0x03a1
        L_0x03a0:
            r0 = 1
        L_0x03a1:
            org.telegram.messenger.SharedConfig.toggleSaveToGalleryFlag(r0)
            org.telegram.ui.Cells.TextCheckCell r11 = (org.telegram.ui.Cells.TextCheckCell) r11
            int r10 = org.telegram.messenger.SharedConfig.saveToGalleryFlags
            r10 = r10 & r0
            if (r10 == 0) goto L_0x03ac
            r2 = 1
        L_0x03ac:
            r11.setChecked(r2)
        L_0x03af:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DataSettingsActivity.lambda$createView$6(android.content.Context, android.view.View, int, float, float):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$0(DialogInterface dialogInterface, int i) {
        String str;
        DownloadController.Preset preset;
        DownloadController.Preset preset2;
        SharedPreferences.Editor edit = MessagesController.getMainSettings(this.currentAccount).edit();
        for (int i2 = 0; i2 < 3; i2++) {
            if (i2 == 0) {
                preset2 = DownloadController.getInstance(this.currentAccount).mobilePreset;
                preset = DownloadController.getInstance(this.currentAccount).mediumPreset;
                str = "mobilePreset";
            } else if (i2 == 1) {
                preset2 = DownloadController.getInstance(this.currentAccount).wifiPreset;
                preset = DownloadController.getInstance(this.currentAccount).highPreset;
                str = "wifiPreset";
            } else {
                preset2 = DownloadController.getInstance(this.currentAccount).roamingPreset;
                preset = DownloadController.getInstance(this.currentAccount).lowPreset;
                str = "roamingPreset";
            }
            preset2.set(preset);
            preset2.enabled = preset.isEnabled();
            DownloadController.getInstance(this.currentAccount).currentMobilePreset = 3;
            edit.putInt("currentMobilePreset", 3);
            DownloadController.getInstance(this.currentAccount).currentWifiPreset = 3;
            edit.putInt("currentWifiPreset", 3);
            DownloadController.getInstance(this.currentAccount).currentRoamingPreset = 3;
            edit.putInt("currentRoamingPreset", 3);
            edit.putString(str, preset2.toString());
        }
        edit.commit();
        DownloadController.getInstance(this.currentAccount).checkAutodownloadSettings();
        for (int i3 = 0; i3 < 3; i3++) {
            DownloadController.getInstance(this.currentAccount).savePresetToServer(i3);
        }
        this.listAdapter.notifyItemRangeChanged(this.mobileRow, 4);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$1(SharedPreferences sharedPreferences, int i, DialogInterface dialogInterface, int i2) {
        int i3 = 3;
        if (i2 == 0) {
            i3 = 0;
        } else if (i2 != 1) {
            i3 = i2 != 2 ? i2 != 3 ? -1 : 2 : 1;
        }
        if (i3 != -1) {
            sharedPreferences.edit().putInt("VoipDataSaving", i3).commit();
        }
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyItemChanged(i);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$2(String str, AlertDialog.Builder builder, View view) {
        SharedConfig.storageCacheDir = str;
        SharedConfig.saveConfig();
        ImageLoader.getInstance().checkMediaPaths();
        builder.getDismissRunnable().run();
        this.listAdapter.notifyItemChanged(this.storageNumRow);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$5(DialogInterface dialogInterface, int i) {
        getConnectionsManager().sendRequest(new TLRPC$TL_messages_clearAllDrafts(), new DataSettingsActivity$$ExternalSyntheticLambda5(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$3() {
        getMediaDataController().clearAllDrafts(true);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$4(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new DataSettingsActivity$$ExternalSyntheticLambda4(this));
    }

    /* access modifiers changed from: protected */
    public void onDialogDismiss(Dialog dialog) {
        DownloadController.getInstance(this.currentAccount).checkAutodownloadSettings();
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return DataSettingsActivity.this.rowCount;
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            String string;
            boolean z;
            DownloadController.Preset currentRoamingPreset;
            NotificationsCheckCell notificationsCheckCell;
            RecyclerView.ViewHolder viewHolder2 = viewHolder;
            int i2 = i;
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType != 0) {
                boolean z2 = false;
                boolean z3 = true;
                if (itemViewType == 1) {
                    TextSettingsCell textSettingsCell = (TextSettingsCell) viewHolder2.itemView;
                    textSettingsCell.setCanDisable(false);
                    textSettingsCell.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
                    if (i2 == DataSettingsActivity.this.storageUsageRow) {
                        textSettingsCell.setText(LocaleController.getString("StorageUsage", NUM), true);
                    } else if (i2 == DataSettingsActivity.this.useLessDataForCallsRow) {
                        String str = null;
                        int i3 = MessagesController.getGlobalMainSettings().getInt("VoipDataSaving", VoIPHelper.getDataSavingDefault());
                        if (i3 == 0) {
                            str = LocaleController.getString("UseLessDataNever", NUM);
                        } else if (i3 == 1) {
                            str = LocaleController.getString("UseLessDataOnMobile", NUM);
                        } else if (i3 == 2) {
                            str = LocaleController.getString("UseLessDataAlways", NUM);
                        } else if (i3 == 3) {
                            str = LocaleController.getString("UseLessDataOnRoaming", NUM);
                        }
                        textSettingsCell.setTextAndValue(LocaleController.getString("VoipUseLessData", NUM), str, true);
                    } else if (i2 == DataSettingsActivity.this.dataUsageRow) {
                        String string2 = LocaleController.getString("NetworkUsage", NUM);
                        if (DataSettingsActivity.this.storageNumRow != -1) {
                            z2 = true;
                        }
                        textSettingsCell.setText(string2, z2);
                    } else if (i2 == DataSettingsActivity.this.storageNumRow) {
                        String absolutePath = ((File) DataSettingsActivity.this.storageDirs.get(0)).getAbsolutePath();
                        if (!TextUtils.isEmpty(SharedConfig.storageCacheDir)) {
                            int size = DataSettingsActivity.this.storageDirs.size();
                            int i4 = 0;
                            while (true) {
                                if (i4 >= size) {
                                    break;
                                }
                                String absolutePath2 = ((File) DataSettingsActivity.this.storageDirs.get(i4)).getAbsolutePath();
                                if (absolutePath2.startsWith(SharedConfig.storageCacheDir)) {
                                    absolutePath = absolutePath2;
                                    break;
                                }
                                i4++;
                            }
                        }
                        textSettingsCell.setTextAndValue(LocaleController.getString("StoragePath", NUM), absolutePath, false);
                    } else if (i2 == DataSettingsActivity.this.proxyRow) {
                        textSettingsCell.setText(LocaleController.getString("ProxySettings", NUM), false);
                    } else if (i2 == DataSettingsActivity.this.resetDownloadRow) {
                        textSettingsCell.setCanDisable(true);
                        textSettingsCell.setTextColor(Theme.getColor("windowBackgroundWhiteRedText"));
                        textSettingsCell.setText(LocaleController.getString("ResetAutomaticMediaDownload", NUM), false);
                    } else if (i2 == DataSettingsActivity.this.quickRepliesRow) {
                        textSettingsCell.setText(LocaleController.getString("VoipQuickReplies", NUM), false);
                    } else if (i2 == DataSettingsActivity.this.clearDraftsRow) {
                        textSettingsCell.setText(LocaleController.getString("PrivacyDeleteCloudDrafts", NUM), false);
                    }
                } else if (itemViewType == 2) {
                    HeaderCell headerCell = (HeaderCell) viewHolder2.itemView;
                    if (i2 == DataSettingsActivity.this.mediaDownloadSectionRow) {
                        headerCell.setText(LocaleController.getString("AutomaticMediaDownload", NUM));
                    } else if (i2 == DataSettingsActivity.this.usageSectionRow) {
                        headerCell.setText(LocaleController.getString("DataUsage", NUM));
                    } else if (i2 == DataSettingsActivity.this.callsSectionRow) {
                        headerCell.setText(LocaleController.getString("Calls", NUM));
                    } else if (i2 == DataSettingsActivity.this.proxySectionRow) {
                        headerCell.setText(LocaleController.getString("Proxy", NUM));
                    } else if (i2 == DataSettingsActivity.this.streamSectionRow) {
                        headerCell.setText(LocaleController.getString("Streaming", NUM));
                    } else if (i2 == DataSettingsActivity.this.autoplayHeaderRow) {
                        headerCell.setText(LocaleController.getString("AutoplayMedia", NUM));
                    } else if (i2 == DataSettingsActivity.this.saveToGallerySectionRow) {
                        headerCell.setText(LocaleController.getString("SaveToGallery", NUM));
                    }
                } else if (itemViewType == 3) {
                    TextCheckCell textCheckCell = (TextCheckCell) viewHolder2.itemView;
                    if (i2 == DataSettingsActivity.this.enableStreamRow) {
                        String string3 = LocaleController.getString("EnableStreaming", NUM);
                        boolean z4 = SharedConfig.streamMedia;
                        if (DataSettingsActivity.this.enableAllStreamRow != -1) {
                            z2 = true;
                        }
                        textCheckCell.setTextAndCheck(string3, z4, z2);
                    } else if (i2 != DataSettingsActivity.this.enableCacheStreamRow) {
                        if (i2 == DataSettingsActivity.this.enableMkvRow) {
                            textCheckCell.setTextAndCheck("(beta only) Show MKV as Video", SharedConfig.streamMkv, true);
                        } else if (i2 == DataSettingsActivity.this.enableAllStreamRow) {
                            textCheckCell.setTextAndCheck("(beta only) Stream All Videos", SharedConfig.streamAllVideo, false);
                        } else if (i2 == DataSettingsActivity.this.autoplayGifsRow) {
                            textCheckCell.setTextAndCheck(LocaleController.getString("AutoplayGIF", NUM), SharedConfig.autoplayGifs, true);
                        } else if (i2 == DataSettingsActivity.this.autoplayVideoRow) {
                            textCheckCell.setTextAndCheck(LocaleController.getString("AutoplayVideo", NUM), SharedConfig.autoplayVideo, false);
                        } else if (i2 == DataSettingsActivity.this.saveToGalleryPeerRow) {
                            String string4 = LocaleController.getString("SaveToGalleryPrivate", NUM);
                            if ((SharedConfig.saveToGalleryFlags & 1) != 0) {
                                z2 = true;
                            }
                            textCheckCell.setTextAndCheck(string4, z2, true);
                        } else if (i2 == DataSettingsActivity.this.saveToGalleryGroupsRow) {
                            String string5 = LocaleController.getString("SaveToGalleryGroups", NUM);
                            if ((SharedConfig.saveToGalleryFlags & 2) != 0) {
                                z2 = true;
                            }
                            textCheckCell.setTextAndCheck(string5, z2, true);
                        } else if (i2 == DataSettingsActivity.this.saveToGalleryChannelsRow) {
                            String string6 = LocaleController.getString("SaveToGalleryChannels", NUM);
                            if ((SharedConfig.saveToGalleryFlags & 4) == 0) {
                                z3 = false;
                            }
                            textCheckCell.setTextAndCheck(string6, z3, false);
                        }
                    }
                } else if (itemViewType == 4) {
                    TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder2.itemView;
                    if (i2 == DataSettingsActivity.this.enableAllStreamInfoRow) {
                        textInfoPrivacyCell.setText(LocaleController.getString("EnableAllStreamingInfo", NUM));
                    }
                } else if (itemViewType == 5) {
                    NotificationsCheckCell notificationsCheckCell2 = (NotificationsCheckCell) viewHolder2.itemView;
                    StringBuilder sb = new StringBuilder();
                    if (i2 == DataSettingsActivity.this.mobileRow) {
                        string = LocaleController.getString("WhenUsingMobileData", NUM);
                        z = DownloadController.getInstance(DataSettingsActivity.this.currentAccount).mobilePreset.enabled;
                        currentRoamingPreset = DownloadController.getInstance(DataSettingsActivity.this.currentAccount).getCurrentMobilePreset();
                    } else if (i2 == DataSettingsActivity.this.wifiRow) {
                        string = LocaleController.getString("WhenConnectedOnWiFi", NUM);
                        z = DownloadController.getInstance(DataSettingsActivity.this.currentAccount).wifiPreset.enabled;
                        currentRoamingPreset = DownloadController.getInstance(DataSettingsActivity.this.currentAccount).getCurrentWiFiPreset();
                    } else {
                        string = LocaleController.getString("WhenRoaming", NUM);
                        z = DownloadController.getInstance(DataSettingsActivity.this.currentAccount).roamingPreset.enabled;
                        currentRoamingPreset = DownloadController.getInstance(DataSettingsActivity.this.currentAccount).getCurrentRoamingPreset();
                    }
                    String str2 = string;
                    int i5 = 0;
                    boolean z5 = false;
                    int i6 = 0;
                    boolean z6 = false;
                    boolean z7 = false;
                    while (true) {
                        int[] iArr = currentRoamingPreset.mask;
                        if (i5 >= iArr.length) {
                            break;
                        }
                        if (!z5 && (iArr[i5] & 1) != 0) {
                            i6++;
                            z5 = true;
                        }
                        if (!z6 && (iArr[i5] & 4) != 0) {
                            i6++;
                            z6 = true;
                        }
                        if (!z7 && (iArr[i5] & 8) != 0) {
                            i6++;
                            z7 = true;
                        }
                        i5++;
                    }
                    if (!currentRoamingPreset.enabled || i6 == 0) {
                        notificationsCheckCell = notificationsCheckCell2;
                        sb.append(LocaleController.getString("NoMediaAutoDownload", NUM));
                    } else {
                        if (z5) {
                            sb.append(LocaleController.getString("AutoDownloadPhotosOn", NUM));
                        }
                        if (z6) {
                            if (sb.length() > 0) {
                                sb.append(", ");
                            }
                            sb.append(LocaleController.getString("AutoDownloadVideosOn", NUM));
                            notificationsCheckCell = notificationsCheckCell2;
                            sb.append(String.format(" (%1$s)", new Object[]{AndroidUtilities.formatFileSize(currentRoamingPreset.sizes[DownloadController.typeToIndex(4)], true)}));
                        } else {
                            notificationsCheckCell = notificationsCheckCell2;
                        }
                        if (z7) {
                            if (sb.length() > 0) {
                                sb.append(", ");
                            }
                            sb.append(LocaleController.getString("AutoDownloadFilesOn", NUM));
                            sb.append(String.format(" (%1$s)", new Object[]{AndroidUtilities.formatFileSize(currentRoamingPreset.sizes[DownloadController.typeToIndex(8)], true)}));
                        }
                    }
                    notificationsCheckCell.setTextAndValueAndCheck(str2, sb, (z5 || z6 || z7) && z, 0, true, true);
                }
            } else if (i2 == DataSettingsActivity.this.clearDraftsSectionRow) {
                viewHolder2.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
            } else {
                viewHolder2.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
            }
        }

        public void onViewAttachedToWindow(RecyclerView.ViewHolder viewHolder) {
            if (viewHolder.getItemViewType() == 3) {
                TextCheckCell textCheckCell = (TextCheckCell) viewHolder.itemView;
                int adapterPosition = viewHolder.getAdapterPosition();
                if (adapterPosition == DataSettingsActivity.this.enableCacheStreamRow) {
                    textCheckCell.setChecked(SharedConfig.saveStreamMedia);
                } else if (adapterPosition == DataSettingsActivity.this.enableStreamRow) {
                    textCheckCell.setChecked(SharedConfig.streamMedia);
                } else if (adapterPosition == DataSettingsActivity.this.enableAllStreamRow) {
                    textCheckCell.setChecked(SharedConfig.streamAllVideo);
                } else if (adapterPosition == DataSettingsActivity.this.enableMkvRow) {
                    textCheckCell.setChecked(SharedConfig.streamMkv);
                } else if (adapterPosition == DataSettingsActivity.this.autoplayGifsRow) {
                    textCheckCell.setChecked(SharedConfig.autoplayGifs);
                } else if (adapterPosition == DataSettingsActivity.this.autoplayVideoRow) {
                    textCheckCell.setChecked(SharedConfig.autoplayVideo);
                }
            }
        }

        public boolean isRowEnabled(int i) {
            if (i == DataSettingsActivity.this.resetDownloadRow) {
                DownloadController instance = DownloadController.getInstance(DataSettingsActivity.this.currentAccount);
                if (!instance.lowPreset.equals(instance.getCurrentRoamingPreset()) || instance.lowPreset.isEnabled() != instance.roamingPreset.enabled || !instance.mediumPreset.equals(instance.getCurrentMobilePreset()) || instance.mediumPreset.isEnabled() != instance.mobilePreset.enabled || !instance.highPreset.equals(instance.getCurrentWiFiPreset()) || instance.highPreset.isEnabled() != instance.wifiPreset.enabled) {
                    return true;
                }
                return false;
            } else if (i == DataSettingsActivity.this.mobileRow || i == DataSettingsActivity.this.roamingRow || i == DataSettingsActivity.this.wifiRow || i == DataSettingsActivity.this.storageUsageRow || i == DataSettingsActivity.this.useLessDataForCallsRow || i == DataSettingsActivity.this.dataUsageRow || i == DataSettingsActivity.this.proxyRow || i == DataSettingsActivity.this.clearDraftsRow || i == DataSettingsActivity.this.enableCacheStreamRow || i == DataSettingsActivity.this.enableStreamRow || i == DataSettingsActivity.this.enableAllStreamRow || i == DataSettingsActivity.this.enableMkvRow || i == DataSettingsActivity.this.quickRepliesRow || i == DataSettingsActivity.this.autoplayVideoRow || i == DataSettingsActivity.this.autoplayGifsRow || i == DataSettingsActivity.this.storageNumRow || i == DataSettingsActivity.this.saveToGalleryGroupsRow || i == DataSettingsActivity.this.saveToGalleryPeerRow || i == DataSettingsActivity.this.saveToGalleryChannelsRow) {
                return true;
            } else {
                return false;
            }
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return isRowEnabled(viewHolder.getAdapterPosition());
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            if (i == 0) {
                view = new ShadowSectionCell(this.mContext);
            } else if (i == 1) {
                view = new TextSettingsCell(this.mContext);
                view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            } else if (i == 2) {
                view = new HeaderCell(this.mContext);
                view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            } else if (i == 3) {
                view = new TextCheckCell(this.mContext);
                view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            } else if (i != 4) {
                view = new NotificationsCheckCell(this.mContext);
                view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            } else {
                view = new TextInfoPrivacyCell(this.mContext);
                view.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        public int getItemViewType(int i) {
            if (i == DataSettingsActivity.this.mediaDownloadSection2Row || i == DataSettingsActivity.this.usageSection2Row || i == DataSettingsActivity.this.callsSection2Row || i == DataSettingsActivity.this.proxySection2Row || i == DataSettingsActivity.this.autoplaySectionRow || i == DataSettingsActivity.this.clearDraftsSectionRow || i == DataSettingsActivity.this.saveToGalleryDividerRow) {
                return 0;
            }
            if (i == DataSettingsActivity.this.mediaDownloadSectionRow || i == DataSettingsActivity.this.streamSectionRow || i == DataSettingsActivity.this.callsSectionRow || i == DataSettingsActivity.this.usageSectionRow || i == DataSettingsActivity.this.proxySectionRow || i == DataSettingsActivity.this.autoplayHeaderRow || i == DataSettingsActivity.this.saveToGallerySectionRow) {
                return 2;
            }
            if (i == DataSettingsActivity.this.enableCacheStreamRow || i == DataSettingsActivity.this.enableStreamRow || i == DataSettingsActivity.this.enableAllStreamRow || i == DataSettingsActivity.this.enableMkvRow || i == DataSettingsActivity.this.autoplayGifsRow || i == DataSettingsActivity.this.autoplayVideoRow || i == DataSettingsActivity.this.saveToGalleryGroupsRow || i == DataSettingsActivity.this.saveToGalleryPeerRow || i == DataSettingsActivity.this.saveToGalleryChannelsRow) {
                return 3;
            }
            if (i == DataSettingsActivity.this.enableAllStreamInfoRow) {
                return 4;
            }
            return (i == DataSettingsActivity.this.mobileRow || i == DataSettingsActivity.this.wifiRow || i == DataSettingsActivity.this.roamingRow) ? 5 : 1;
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, TextCheckCell.class, HeaderCell.class, NotificationsCheckCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrack"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackChecked"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteValueText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrack"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackChecked"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        return arrayList;
    }
}
