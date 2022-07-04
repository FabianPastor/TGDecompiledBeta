package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Collections;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.MaxFileSizeCell;
import org.telegram.ui.Cells.NotificationsCheckCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCheckBoxCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SlideChooseView;

public class DataAutoDownloadActivity extends BaseFragment {
    /* access modifiers changed from: private */
    public boolean animateChecked;
    /* access modifiers changed from: private */
    public int autoDownloadRow;
    /* access modifiers changed from: private */
    public int autoDownloadSectionRow;
    /* access modifiers changed from: private */
    public int currentPresetNum;
    /* access modifiers changed from: private */
    public int currentType;
    private DownloadController.Preset defaultPreset;
    /* access modifiers changed from: private */
    public int filesRow;
    /* access modifiers changed from: private */
    public DownloadController.Preset highPreset;
    private String key;
    /* access modifiers changed from: private */
    public String key2;
    private LinearLayoutManager layoutManager;
    /* access modifiers changed from: private */
    public ListAdapter listAdapter;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    /* access modifiers changed from: private */
    public DownloadController.Preset lowPreset;
    /* access modifiers changed from: private */
    public DownloadController.Preset mediumPreset;
    /* access modifiers changed from: private */
    public int photosRow;
    /* access modifiers changed from: private */
    public ArrayList<DownloadController.Preset> presets = new ArrayList<>();
    /* access modifiers changed from: private */
    public int rowCount;
    private int selectedPreset = 1;
    /* access modifiers changed from: private */
    public int typeHeaderRow;
    /* access modifiers changed from: private */
    public DownloadController.Preset typePreset;
    /* access modifiers changed from: private */
    public int typeSectionRow;
    /* access modifiers changed from: private */
    public int usageHeaderRow;
    /* access modifiers changed from: private */
    public int usageProgressRow;
    /* access modifiers changed from: private */
    public int usageSectionRow;
    /* access modifiers changed from: private */
    public int videosRow;
    /* access modifiers changed from: private */
    public boolean wereAnyChanges;

    public DataAutoDownloadActivity(int type) {
        this.currentType = type;
        this.lowPreset = DownloadController.getInstance(this.currentAccount).lowPreset;
        this.mediumPreset = DownloadController.getInstance(this.currentAccount).mediumPreset;
        this.highPreset = DownloadController.getInstance(this.currentAccount).highPreset;
        int i = this.currentType;
        if (i == 0) {
            this.currentPresetNum = DownloadController.getInstance(this.currentAccount).currentMobilePreset;
            this.typePreset = DownloadController.getInstance(this.currentAccount).mobilePreset;
            this.defaultPreset = this.mediumPreset;
            this.key = "mobilePreset";
            this.key2 = "currentMobilePreset";
        } else if (i == 1) {
            this.currentPresetNum = DownloadController.getInstance(this.currentAccount).currentWifiPreset;
            this.typePreset = DownloadController.getInstance(this.currentAccount).wifiPreset;
            this.defaultPreset = this.highPreset;
            this.key = "wifiPreset";
            this.key2 = "currentWifiPreset";
        } else {
            this.currentPresetNum = DownloadController.getInstance(this.currentAccount).currentRoamingPreset;
            this.typePreset = DownloadController.getInstance(this.currentAccount).roamingPreset;
            this.defaultPreset = this.lowPreset;
            this.key = "roamingPreset";
            this.key2 = "currentRoamingPreset";
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        fillPresets();
        updateRows();
        return true;
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(NUM);
        int i = this.currentType;
        if (i == 0) {
            this.actionBar.setTitle(LocaleController.getString("AutoDownloadOnMobileData", NUM));
        } else if (i == 1) {
            this.actionBar.setTitle(LocaleController.getString("AutoDownloadOnWiFiData", NUM));
        } else if (i == 2) {
            this.actionBar.setTitle(LocaleController.getString("AutoDownloadOnRoamingData", NUM));
        }
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    DataAutoDownloadActivity.this.finishFragment();
                }
            }
        });
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setVerticalScrollBarEnabled(false);
        ((DefaultItemAnimator) this.listView.getItemAnimator()).setDelayAnimations(false);
        RecyclerListView recyclerListView2 = this.listView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false);
        this.layoutManager = linearLayoutManager;
        recyclerListView2.setLayoutManager(linearLayoutManager);
        ((FrameLayout) this.fragmentView).addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListenerExtended) new DataAutoDownloadActivity$$ExternalSyntheticLambda5(this));
        return this.fragmentView;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v39, resolved type: android.widget.TextClock} */
    /* JADX WARNING: type inference failed for: r1v28, types: [boolean] */
    /* JADX WARNING: type inference failed for: r1v29 */
    /* JADX WARNING: type inference failed for: r1v30 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* renamed from: lambda$createView$4$org-telegram-ui-DataAutoDownloadActivity  reason: not valid java name */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void m3355lambda$createView$4$orgtelegramuiDataAutoDownloadActivity(android.view.View r33, int r34, float r35, float r36) {
        /*
            r32 = this;
            r12 = r32
            r13 = r33
            r14 = r34
            int r0 = r12.autoDownloadRow
            r8 = 4
            r9 = 2
            r10 = 3
            r11 = 0
            r15 = 1
            if (r14 != r0) goto L_0x00ec
            int r0 = r12.currentPresetNum
            if (r0 == r10) goto L_0x0030
            if (r0 != 0) goto L_0x001d
            org.telegram.messenger.DownloadController$Preset r0 = r12.typePreset
            org.telegram.messenger.DownloadController$Preset r1 = r12.lowPreset
            r0.set((org.telegram.messenger.DownloadController.Preset) r1)
            goto L_0x0030
        L_0x001d:
            if (r0 != r15) goto L_0x0027
            org.telegram.messenger.DownloadController$Preset r0 = r12.typePreset
            org.telegram.messenger.DownloadController$Preset r1 = r12.mediumPreset
            r0.set((org.telegram.messenger.DownloadController.Preset) r1)
            goto L_0x0030
        L_0x0027:
            if (r0 != r9) goto L_0x0030
            org.telegram.messenger.DownloadController$Preset r0 = r12.typePreset
            org.telegram.messenger.DownloadController$Preset r1 = r12.highPreset
            r0.set((org.telegram.messenger.DownloadController.Preset) r1)
        L_0x0030:
            r0 = r13
            org.telegram.ui.Cells.TextCheckCell r0 = (org.telegram.ui.Cells.TextCheckCell) r0
            boolean r1 = r0.isChecked()
            if (r1 != 0) goto L_0x004b
            org.telegram.messenger.DownloadController$Preset r2 = r12.typePreset
            boolean r2 = r2.enabled
            if (r2 == 0) goto L_0x004b
            org.telegram.messenger.DownloadController$Preset r2 = r12.defaultPreset
            int[] r2 = r2.mask
            org.telegram.messenger.DownloadController$Preset r3 = r12.typePreset
            int[] r3 = r3.mask
            java.lang.System.arraycopy(r2, r11, r3, r11, r8)
            goto L_0x0052
        L_0x004b:
            org.telegram.messenger.DownloadController$Preset r2 = r12.typePreset
            boolean r3 = r2.enabled
            r3 = r3 ^ r15
            r2.enabled = r3
        L_0x0052:
            org.telegram.messenger.DownloadController$Preset r2 = r12.typePreset
            boolean r2 = r2.enabled
            java.lang.String r3 = "windowBackgroundChecked"
            java.lang.String r4 = "windowBackgroundUnchecked"
            if (r2 == 0) goto L_0x005e
            r2 = r3
            goto L_0x005f
        L_0x005e:
            r2 = r4
        L_0x005f:
            r13.setTag(r2)
            r2 = r1 ^ 1
            org.telegram.messenger.DownloadController$Preset r5 = r12.typePreset
            boolean r5 = r5.enabled
            if (r5 == 0) goto L_0x006b
            goto L_0x006c
        L_0x006b:
            r3 = r4
        L_0x006c:
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r0.setBackgroundColorAnimated(r2, r3)
            r32.updateRows()
            org.telegram.messenger.DownloadController$Preset r2 = r12.typePreset
            boolean r2 = r2.enabled
            r3 = 8
            if (r2 == 0) goto L_0x0087
            org.telegram.ui.DataAutoDownloadActivity$ListAdapter r2 = r12.listAdapter
            int r4 = r12.autoDownloadSectionRow
            int r4 = r4 + r15
            r2.notifyItemRangeInserted(r4, r3)
            goto L_0x008f
        L_0x0087:
            org.telegram.ui.DataAutoDownloadActivity$ListAdapter r2 = r12.listAdapter
            int r4 = r12.autoDownloadSectionRow
            int r4 = r4 + r15
            r2.notifyItemRangeRemoved(r4, r3)
        L_0x008f:
            org.telegram.ui.DataAutoDownloadActivity$ListAdapter r2 = r12.listAdapter
            int r3 = r12.autoDownloadSectionRow
            r2.notifyItemChanged(r3)
            int r2 = r12.currentAccount
            android.content.SharedPreferences r2 = org.telegram.messenger.MessagesController.getMainSettings(r2)
            android.content.SharedPreferences$Editor r2 = r2.edit()
            java.lang.String r3 = r12.key
            org.telegram.messenger.DownloadController$Preset r4 = r12.typePreset
            java.lang.String r4 = r4.toString()
            r2.putString(r3, r4)
            java.lang.String r3 = r12.key2
            r12.currentPresetNum = r10
            r2.putInt(r3, r10)
            int r3 = r12.currentType
            if (r3 != 0) goto L_0x00c1
            int r3 = r12.currentAccount
            org.telegram.messenger.DownloadController r3 = org.telegram.messenger.DownloadController.getInstance(r3)
            int r4 = r12.currentPresetNum
            r3.currentMobilePreset = r4
            goto L_0x00d8
        L_0x00c1:
            if (r3 != r15) goto L_0x00ce
            int r3 = r12.currentAccount
            org.telegram.messenger.DownloadController r3 = org.telegram.messenger.DownloadController.getInstance(r3)
            int r4 = r12.currentPresetNum
            r3.currentWifiPreset = r4
            goto L_0x00d8
        L_0x00ce:
            int r3 = r12.currentAccount
            org.telegram.messenger.DownloadController r3 = org.telegram.messenger.DownloadController.getInstance(r3)
            int r4 = r12.currentPresetNum
            r3.currentRoamingPreset = r4
        L_0x00d8:
            r2.commit()
            r3 = r1 ^ 1
            r0.setChecked(r3)
            int r3 = r12.currentAccount
            org.telegram.messenger.DownloadController r3 = org.telegram.messenger.DownloadController.getInstance(r3)
            r3.checkAutodownloadSettings()
            r12.wereAnyChanges = r15
            goto L_0x00f9
        L_0x00ec:
            int r0 = r12.photosRow
            if (r14 == r0) goto L_0x00fb
            int r0 = r12.videosRow
            if (r14 == r0) goto L_0x00fb
            int r0 = r12.filesRow
            if (r14 != r0) goto L_0x00f9
            goto L_0x00fb
        L_0x00f9:
            goto L_0x05fb
        L_0x00fb:
            boolean r0 = r33.isEnabled()
            if (r0 != 0) goto L_0x0102
            return
        L_0x0102:
            int r0 = r12.photosRow
            if (r14 != r0) goto L_0x010a
            r0 = 1
            r16 = r0
            goto L_0x0116
        L_0x010a:
            int r0 = r12.videosRow
            if (r14 != r0) goto L_0x0112
            r0 = 4
            r16 = r0
            goto L_0x0116
        L_0x0112:
            r0 = 8
            r16 = r0
        L_0x0116:
            int r17 = org.telegram.messenger.DownloadController.typeToIndex(r16)
            int r0 = r12.currentType
            if (r0 != 0) goto L_0x0130
            int r0 = r12.currentAccount
            org.telegram.messenger.DownloadController r0 = org.telegram.messenger.DownloadController.getInstance(r0)
            org.telegram.messenger.DownloadController$Preset r0 = r0.getCurrentMobilePreset()
            java.lang.String r1 = "mobilePreset"
            java.lang.String r2 = "currentMobilePreset"
            r7 = r0
            r6 = r1
            r5 = r2
            goto L_0x0155
        L_0x0130:
            if (r0 != r15) goto L_0x0144
            int r0 = r12.currentAccount
            org.telegram.messenger.DownloadController r0 = org.telegram.messenger.DownloadController.getInstance(r0)
            org.telegram.messenger.DownloadController$Preset r0 = r0.getCurrentWiFiPreset()
            java.lang.String r1 = "wifiPreset"
            java.lang.String r2 = "currentWifiPreset"
            r7 = r0
            r6 = r1
            r5 = r2
            goto L_0x0155
        L_0x0144:
            int r0 = r12.currentAccount
            org.telegram.messenger.DownloadController r0 = org.telegram.messenger.DownloadController.getInstance(r0)
            org.telegram.messenger.DownloadController$Preset r0 = r0.getCurrentRoamingPreset()
            java.lang.String r1 = "roamingPreset"
            java.lang.String r2 = "currentRoamingPreset"
            r7 = r0
            r6 = r1
            r5 = r2
        L_0x0155:
            r4 = r13
            org.telegram.ui.Cells.NotificationsCheckCell r4 = (org.telegram.ui.Cells.NotificationsCheckCell) r4
            boolean r18 = r4.isChecked()
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            r1 = 1117257728(0x42980000, float:76.0)
            if (r0 == 0) goto L_0x016b
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r0 = (float) r0
            int r0 = (r35 > r0 ? 1 : (r35 == r0 ? 0 : -1))
            if (r0 <= 0) goto L_0x017d
        L_0x016b:
            boolean r0 = org.telegram.messenger.LocaleController.isRTL
            if (r0 != 0) goto L_0x023c
            int r0 = r33.getMeasuredWidth()
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r0 = r0 - r1
            float r0 = (float) r0
            int r0 = (r35 > r0 ? 1 : (r35 == r0 ? 0 : -1))
            if (r0 < 0) goto L_0x023c
        L_0x017d:
            int r0 = r12.currentPresetNum
            if (r0 == r10) goto L_0x019e
            if (r0 != 0) goto L_0x018b
            org.telegram.messenger.DownloadController$Preset r0 = r12.typePreset
            org.telegram.messenger.DownloadController$Preset r1 = r12.lowPreset
            r0.set((org.telegram.messenger.DownloadController.Preset) r1)
            goto L_0x019e
        L_0x018b:
            if (r0 != r15) goto L_0x0195
            org.telegram.messenger.DownloadController$Preset r0 = r12.typePreset
            org.telegram.messenger.DownloadController$Preset r1 = r12.mediumPreset
            r0.set((org.telegram.messenger.DownloadController.Preset) r1)
            goto L_0x019e
        L_0x0195:
            if (r0 != r9) goto L_0x019e
            org.telegram.messenger.DownloadController$Preset r0 = r12.typePreset
            org.telegram.messenger.DownloadController$Preset r1 = r12.highPreset
            r0.set((org.telegram.messenger.DownloadController.Preset) r1)
        L_0x019e:
            r0 = 0
            r1 = 0
        L_0x01a0:
            org.telegram.messenger.DownloadController$Preset r2 = r12.typePreset
            int[] r2 = r2.mask
            int r2 = r2.length
            if (r1 >= r2) goto L_0x01b4
            int[] r2 = r7.mask
            r2 = r2[r1]
            r2 = r2 & r16
            if (r2 == 0) goto L_0x01b1
            r0 = 1
            goto L_0x01b4
        L_0x01b1:
            int r1 = r1 + 1
            goto L_0x01a0
        L_0x01b4:
            r1 = 0
        L_0x01b5:
            org.telegram.messenger.DownloadController$Preset r2 = r12.typePreset
            int[] r2 = r2.mask
            int r2 = r2.length
            if (r1 >= r2) goto L_0x01d9
            if (r18 == 0) goto L_0x01ca
            org.telegram.messenger.DownloadController$Preset r2 = r12.typePreset
            int[] r2 = r2.mask
            r3 = r2[r1]
            r8 = r16 ^ -1
            r3 = r3 & r8
            r2[r1] = r3
            goto L_0x01d6
        L_0x01ca:
            if (r0 != 0) goto L_0x01d6
            org.telegram.messenger.DownloadController$Preset r2 = r12.typePreset
            int[] r2 = r2.mask
            r3 = r2[r1]
            r3 = r3 | r16
            r2[r1] = r3
        L_0x01d6:
            int r1 = r1 + 1
            goto L_0x01b5
        L_0x01d9:
            int r1 = r12.currentAccount
            android.content.SharedPreferences r1 = org.telegram.messenger.MessagesController.getMainSettings(r1)
            android.content.SharedPreferences$Editor r1 = r1.edit()
            org.telegram.messenger.DownloadController$Preset r2 = r12.typePreset
            java.lang.String r2 = r2.toString()
            r1.putString(r6, r2)
            r12.currentPresetNum = r10
            r1.putInt(r5, r10)
            int r2 = r12.currentType
            if (r2 != 0) goto L_0x0200
            int r2 = r12.currentAccount
            org.telegram.messenger.DownloadController r2 = org.telegram.messenger.DownloadController.getInstance(r2)
            int r3 = r12.currentPresetNum
            r2.currentMobilePreset = r3
            goto L_0x0217
        L_0x0200:
            if (r2 != r15) goto L_0x020d
            int r2 = r12.currentAccount
            org.telegram.messenger.DownloadController r2 = org.telegram.messenger.DownloadController.getInstance(r2)
            int r3 = r12.currentPresetNum
            r2.currentWifiPreset = r3
            goto L_0x0217
        L_0x020d:
            int r2 = r12.currentAccount
            org.telegram.messenger.DownloadController r2 = org.telegram.messenger.DownloadController.getInstance(r2)
            int r3 = r12.currentPresetNum
            r2.currentRoamingPreset = r3
        L_0x0217:
            r1.commit()
            r2 = r18 ^ 1
            r4.setChecked(r2)
            org.telegram.ui.Components.RecyclerListView r2 = r12.listView
            androidx.recyclerview.widget.RecyclerView$ViewHolder r2 = r2.findContainingViewHolder(r13)
            if (r2 == 0) goto L_0x022c
            org.telegram.ui.DataAutoDownloadActivity$ListAdapter r3 = r12.listAdapter
            r3.onBindViewHolder(r2, r14)
        L_0x022c:
            int r3 = r12.currentAccount
            org.telegram.messenger.DownloadController r3 = org.telegram.messenger.DownloadController.getInstance(r3)
            r3.checkAutodownloadSettings()
            r12.wereAnyChanges = r15
            r32.fillPresets()
            goto L_0x05fb
        L_0x023c:
            android.app.Activity r0 = r32.getParentActivity()
            if (r0 != 0) goto L_0x0243
            return
        L_0x0243:
            org.telegram.ui.ActionBar.BottomSheet$Builder r0 = new org.telegram.ui.ActionBar.BottomSheet$Builder
            android.app.Activity r1 = r32.getParentActivity()
            r0.<init>(r1)
            r3 = r0
            r3.setApplyTopPadding(r11)
            r3.setApplyBottomPadding(r11)
            android.widget.LinearLayout r0 = new android.widget.LinearLayout
            android.app.Activity r1 = r32.getParentActivity()
            r0.<init>(r1)
            r1 = r0
            r1.setOrientation(r15)
            r3.setCustomView(r1)
            org.telegram.ui.Cells.HeaderCell r0 = new org.telegram.ui.Cells.HeaderCell
            android.app.Activity r20 = r32.getParentActivity()
            r22 = 21
            r23 = 15
            r24 = 0
            java.lang.String r21 = "dialogTextBlue2"
            r19 = r0
            r19.<init>(r20, r21, r22, r23, r24)
            int r2 = r12.photosRow
            if (r14 != r2) goto L_0x0287
            r2 = 2131624589(0x7f0e028d, float:1.8876362E38)
            java.lang.String r10 = "AutoDownloadPhotosTitle"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r10, r2)
            r0.setText(r2)
            goto L_0x02a4
        L_0x0287:
            int r2 = r12.videosRow
            if (r14 != r2) goto L_0x0298
            r2 = 2131624599(0x7f0e0297, float:1.8876382E38)
            java.lang.String r10 = "AutoDownloadVideosTitle"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r10, r2)
            r0.setText(r2)
            goto L_0x02a4
        L_0x0298:
            r2 = 2131624569(0x7f0e0279, float:1.8876321E38)
            java.lang.String r10 = "AutoDownloadFilesTitle"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r10, r2)
            r0.setText(r2)
        L_0x02a4:
            r2 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r10 = -1
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r2)
            r1.addView(r0, r2)
            org.telegram.ui.Cells.MaxFileSizeCell[] r2 = new org.telegram.ui.Cells.MaxFileSizeCell[r15]
            org.telegram.ui.Cells.TextCheckCell[] r10 = new org.telegram.ui.Cells.TextCheckCell[r15]
            android.animation.AnimatorSet[] r9 = new android.animation.AnimatorSet[r15]
            org.telegram.ui.Cells.TextCheckBoxCell[] r11 = new org.telegram.ui.Cells.TextCheckBoxCell[r8]
            r23 = 0
            r15 = r23
        L_0x02ba:
            if (r15 >= r8) goto L_0x03a0
            org.telegram.ui.Cells.TextCheckBoxCell r8 = new org.telegram.ui.Cells.TextCheckBoxCell
            r25 = r0
            android.app.Activity r0 = r32.getParentActivity()
            r26 = r1
            r22 = r2
            r1 = 0
            r2 = 1
            r8.<init>(r0, r2, r1)
            r11[r15] = r8
            r27 = r22
            r2 = r8
            if (r15 != 0) goto L_0x02f3
            r0 = r11[r15]
            r8 = 2131624621(0x7f0e02ad, float:1.8876427E38)
            java.lang.String r1 = "AutodownloadContacts"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r8)
            int[] r8 = r7.mask
            r22 = 0
            r8 = r8[r22]
            r8 = r8 & r16
            if (r8 == 0) goto L_0x02eb
            r8 = 1
            goto L_0x02ec
        L_0x02eb:
            r8 = 0
        L_0x02ec:
            r28 = r3
            r3 = 1
            r0.setTextAndCheck(r1, r8, r3)
            goto L_0x0351
        L_0x02f3:
            r28 = r3
            r3 = 1
            if (r15 != r3) goto L_0x0312
            r0 = r11[r15]
            r1 = 2131624623(0x7f0e02af, float:1.887643E38)
            java.lang.String r8 = "AutodownloadPrivateChats"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r8, r1)
            int[] r8 = r7.mask
            r8 = r8[r3]
            r8 = r8 & r16
            if (r8 == 0) goto L_0x030d
            r8 = 1
            goto L_0x030e
        L_0x030d:
            r8 = 0
        L_0x030e:
            r0.setTextAndCheck(r1, r8, r3)
            goto L_0x0351
        L_0x0312:
            r8 = 2
            if (r15 != r8) goto L_0x0330
            r0 = r11[r15]
            r1 = 2131624622(0x7f0e02ae, float:1.8876429E38)
            java.lang.String r3 = "AutodownloadGroupChats"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
            int[] r3 = r7.mask
            r3 = r3[r8]
            r3 = r3 & r16
            if (r3 == 0) goto L_0x032a
            r3 = 1
            goto L_0x032b
        L_0x032a:
            r3 = 0
        L_0x032b:
            r8 = 1
            r0.setTextAndCheck(r1, r3, r8)
            goto L_0x0351
        L_0x0330:
            r0 = r11[r15]
            r1 = 2131624620(0x7f0e02ac, float:1.8876425E38)
            java.lang.String r3 = "AutodownloadChannels"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
            int[] r3 = r7.mask
            r8 = 3
            r3 = r3[r8]
            r3 = r3 & r16
            if (r3 == 0) goto L_0x0346
            r3 = 1
            goto L_0x0347
        L_0x0346:
            r3 = 0
        L_0x0347:
            int r8 = r12.photosRow
            if (r14 == r8) goto L_0x034d
            r8 = 1
            goto L_0x034e
        L_0x034d:
            r8 = 0
        L_0x034e:
            r0.setTextAndCheck(r1, r3, r8)
        L_0x0351:
            r0 = r11[r15]
            r1 = 0
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r1)
            r0.setBackgroundDrawable(r3)
            r8 = r11[r15]
            org.telegram.ui.DataAutoDownloadActivity$$ExternalSyntheticLambda1 r3 = new org.telegram.ui.DataAutoDownloadActivity$$ExternalSyntheticLambda1
            r0 = r3
            r13 = r26
            r1 = r32
            r14 = r3
            r29 = r28
            r3 = r11
            r26 = r4
            r4 = r34
            r28 = r5
            r5 = r27
            r30 = r6
            r6 = r10
            r31 = r7
            r7 = r9
            r0.<init>(r1, r2, r3, r4, r5, r6, r7)
            r8.setOnClickListener(r14)
            r0 = r11[r15]
            r1 = 1112014848(0x42480000, float:50.0)
            r3 = -1
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r3, r1)
            r13.addView(r0, r1)
            int r15 = r15 + 1
            r14 = r34
            r1 = r13
            r0 = r25
            r4 = r26
            r2 = r27
            r5 = r28
            r3 = r29
            r6 = r30
            r7 = r31
            r8 = 4
            r13 = r33
            goto L_0x02ba
        L_0x03a0:
            r25 = r0
            r13 = r1
            r27 = r2
            r29 = r3
            r26 = r4
            r28 = r5
            r30 = r6
            r31 = r7
            int r0 = r12.photosRow
            r7 = -2
            r8 = 0
            r14 = r34
            if (r14 == r0) goto L_0x04a5
            org.telegram.ui.Cells.TextInfoPrivacyCell r0 = new org.telegram.ui.Cells.TextInfoPrivacyCell
            android.app.Activity r1 = r32.getParentActivity()
            r0.<init>(r1)
            r15 = r0
            org.telegram.ui.DataAutoDownloadActivity$3 r19 = new org.telegram.ui.DataAutoDownloadActivity$3
            android.app.Activity r2 = r32.getParentActivity()
            r0 = r19
            r1 = r32
            r3 = r34
            r4 = r15
            r5 = r10
            r6 = r9
            r0.<init>(r2, r3, r4, r5, r6)
            r0 = 0
            r27[r0] = r19
            r1 = r27[r0]
            r6 = r31
            long[] r2 = r6.sizes
            r3 = r2[r17]
            r1.setSize(r3)
            r1 = r27[r0]
            r2 = 50
            r3 = -1
            android.widget.LinearLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createLinear(r3, r2)
            r13.addView(r1, r2)
            org.telegram.ui.Cells.TextCheckCell r1 = new org.telegram.ui.Cells.TextCheckCell
            android.app.Activity r2 = r32.getParentActivity()
            r4 = 21
            r5 = 1
            r1.<init>(r2, r4, r5)
            r10[r0] = r1
            r1 = r10[r0]
            r2 = 48
            android.widget.LinearLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createLinear(r3, r2)
            r13.addView(r1, r2)
            r1 = r10[r0]
            org.telegram.ui.DataAutoDownloadActivity$$ExternalSyntheticLambda3 r0 = new org.telegram.ui.DataAutoDownloadActivity$$ExternalSyntheticLambda3
            r0.<init>(r10)
            r1.setOnClickListener(r0)
            android.app.Activity r0 = r32.getParentActivity()
            r1 = 2131165435(0x7var_fb, float:1.7945087E38)
            java.lang.String r2 = "windowBackgroundGrayShadow"
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r0, (int) r1, (java.lang.String) r2)
            org.telegram.ui.Components.CombinedDrawable r1 = new org.telegram.ui.Components.CombinedDrawable
            android.graphics.drawable.ColorDrawable r2 = new android.graphics.drawable.ColorDrawable
            java.lang.String r3 = "windowBackgroundGray"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r2.<init>(r3)
            r1.<init>(r2, r0)
            r2 = 1
            r1.setFullsize(r2)
            r15.setBackgroundDrawable(r1)
            r2 = -1
            android.widget.LinearLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createLinear(r2, r7)
            r13.addView(r15, r3)
            int r2 = r12.videosRow
            if (r14 != r2) goto L_0x0479
            r2 = 0
            r3 = r27[r2]
            r4 = 2131624574(0x7f0e027e, float:1.8876332E38)
            java.lang.String r5 = "AutoDownloadMaxVideoSize"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r3.setText(r4)
            r3 = r10[r2]
            r4 = 2131624593(0x7f0e0291, float:1.887637E38)
            java.lang.String r5 = "AutoDownloadPreloadVideo"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            boolean r5 = r6.preloadVideo
            r3.setTextAndCheck(r4, r5, r2)
            r3 = 2131624594(0x7f0e0292, float:1.8876372E38)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]
            long[] r4 = r6.sizes
            r21 = r4[r17]
            java.lang.String r4 = org.telegram.messenger.AndroidUtilities.formatFileSize(r21)
            r5[r2] = r4
            java.lang.String r4 = "AutoDownloadPreloadVideoInfo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r4, r3, r5)
            r15.setText(r3)
            goto L_0x04a4
        L_0x0479:
            r2 = 0
            r3 = r27[r2]
            r4 = 2131624573(0x7f0e027d, float:1.887633E38)
            java.lang.String r5 = "AutoDownloadMaxFileSize"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r3.setText(r4)
            r3 = r10[r2]
            r4 = 2131624591(0x7f0e028f, float:1.8876366E38)
            java.lang.String r5 = "AutoDownloadPreloadMusic"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            boolean r5 = r6.preloadMusic
            r3.setTextAndCheck(r4, r5, r2)
            r3 = 2131624592(0x7f0e0290, float:1.8876368E38)
            java.lang.String r4 = "AutoDownloadPreloadMusicInfo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r15.setText(r3)
        L_0x04a4:
            goto L_0x04c8
        L_0x04a5:
            r6 = r31
            r2 = 0
            r27[r2] = r8
            r10[r2] = r8
            android.view.View r0 = new android.view.View
            android.app.Activity r1 = r32.getParentActivity()
            r0.<init>(r1)
            java.lang.String r1 = "divider"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setBackgroundColor(r1)
            android.widget.LinearLayout$LayoutParams r1 = new android.widget.LinearLayout$LayoutParams
            r2 = -1
            r3 = 1
            r1.<init>(r2, r3)
            r13.addView(r0, r1)
        L_0x04c8:
            int r0 = r12.videosRow
            if (r14 != r0) goto L_0x04fd
            r0 = 0
            r1 = 0
        L_0x04ce:
            int r2 = r11.length
            if (r1 >= r2) goto L_0x04de
            r2 = r11[r1]
            boolean r2 = r2.isChecked()
            if (r2 == 0) goto L_0x04db
            r0 = 1
            goto L_0x04de
        L_0x04db:
            int r1 = r1 + 1
            goto L_0x04ce
        L_0x04de:
            if (r0 != 0) goto L_0x04ec
            r1 = 0
            r2 = r27[r1]
            r2.setEnabled(r1, r8)
            r2 = r10[r1]
            r2.setEnabled(r1, r8)
            goto L_0x04ed
        L_0x04ec:
            r1 = 0
        L_0x04ed:
            long[] r2 = r6.sizes
            r3 = r2[r17]
            r21 = 2097152(0x200000, double:1.0361308E-317)
            int r2 = (r3 > r21 ? 1 : (r3 == r21 ? 0 : -1))
            if (r2 > 0) goto L_0x04fd
            r2 = r10[r1]
            r2.setEnabled(r1, r8)
        L_0x04fd:
            android.widget.FrameLayout r0 = new android.widget.FrameLayout
            android.app.Activity r1 = r32.getParentActivity()
            r0.<init>(r1)
            r15 = r0
            r0 = 1090519040(0x41000000, float:8.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r15.setPadding(r1, r2, r3, r0)
            r0 = 52
            r1 = -1
            android.widget.LinearLayout$LayoutParams r0 = org.telegram.ui.Components.LayoutHelper.createLinear(r1, r0)
            r13.addView(r15, r0)
            android.widget.TextView r0 = new android.widget.TextView
            android.app.Activity r1 = r32.getParentActivity()
            r0.<init>(r1)
            r1 = 1096810496(0x41600000, float:14.0)
            r2 = 1
            r0.setTextSize(r2, r1)
            java.lang.String r2 = "dialogTextBlue2"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r0.setTextColor(r3)
            r3 = 17
            r0.setGravity(r3)
            java.lang.String r4 = "fonts/rmedium.ttf"
            android.graphics.Typeface r5 = org.telegram.messenger.AndroidUtilities.getTypeface(r4)
            r0.setTypeface(r5)
            r5 = 2131624819(0x7f0e0373, float:1.8876828E38)
            java.lang.String r8 = "Cancel"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r8, r5)
            java.lang.String r5 = r5.toUpperCase()
            r0.setText(r5)
            r5 = 1092616192(0x41200000, float:10.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r5 = 0
            r0.setPadding(r8, r5, r3, r5)
            r3 = 51
            r5 = 36
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r7, (int) r5, (int) r3)
            r15.addView(r0, r3)
            org.telegram.ui.DataAutoDownloadActivity$$ExternalSyntheticLambda0 r3 = new org.telegram.ui.DataAutoDownloadActivity$$ExternalSyntheticLambda0
            r8 = r29
            r3.<init>(r8)
            r0.setOnClickListener(r3)
            android.widget.TextView r3 = new android.widget.TextView
            android.app.Activity r5 = r32.getParentActivity()
            r3.<init>(r5)
            r5 = r3
            r0 = 1
            r5.setTextSize(r0, r1)
            int r0 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r5.setTextColor(r0)
            r0 = 17
            r5.setGravity(r0)
            android.graphics.Typeface r0 = org.telegram.messenger.AndroidUtilities.getTypeface(r4)
            r5.setTypeface(r0)
            r0 = 2131628060(0x7f0e101c, float:1.8883402E38)
            java.lang.String r1 = "Save"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            java.lang.String r0 = r0.toUpperCase()
            r5.setText(r0)
            r0 = 1092616192(0x41200000, float:10.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r2 = 0
            r5.setPadding(r1, r2, r0, r2)
            r0 = 53
            r1 = 36
            android.widget.FrameLayout$LayoutParams r0 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r7, (int) r1, (int) r0)
            r15.addView(r5, r0)
            org.telegram.ui.DataAutoDownloadActivity$$ExternalSyntheticLambda2 r7 = new org.telegram.ui.DataAutoDownloadActivity$$ExternalSyntheticLambda2
            r0 = r7
            r1 = r32
            r2 = r11
            r3 = r16
            r4 = r27
            r19 = r13
            r13 = r5
            r5 = r17
            r20 = r6
            r6 = r10
            r14 = r7
            r7 = r34
            r21 = r8
            r8 = r30
            r22 = r9
            r9 = r28
            r23 = r10
            r10 = r21
            r24 = r11
            r11 = r33
            r0.<init>(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)
            r13.setOnClickListener(r14)
            org.telegram.ui.ActionBar.BottomSheet r0 = r21.create()
            r12.showDialog(r0)
        L_0x05fb:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DataAutoDownloadActivity.m3355lambda$createView$4$orgtelegramuiDataAutoDownloadActivity(android.view.View, int, float, float):void");
    }

    /* renamed from: lambda$createView$0$org-telegram-ui-DataAutoDownloadActivity  reason: not valid java name */
    public /* synthetic */ void m3353lambda$createView$0$orgtelegramuiDataAutoDownloadActivity(TextCheckBoxCell checkBoxCell, TextCheckBoxCell[] cells, int position, MaxFileSizeCell[] sizeCell, TextCheckCell[] checkCell, final AnimatorSet[] animatorSet, View v) {
        if (v.isEnabled()) {
            checkBoxCell.setChecked(!checkBoxCell.isChecked());
            boolean hasAny = false;
            int b = 0;
            while (true) {
                if (b >= cells.length) {
                    break;
                } else if (cells[b].isChecked()) {
                    hasAny = true;
                    break;
                } else {
                    b++;
                }
            }
            if (position == this.videosRow && sizeCell[0].isEnabled() != hasAny) {
                ArrayList<Animator> animators = new ArrayList<>();
                sizeCell[0].setEnabled(hasAny, animators);
                if (sizeCell[0].getSize() > 2097152) {
                    checkCell[0].setEnabled(hasAny, animators);
                }
                if (animatorSet[0] != null) {
                    animatorSet[0].cancel();
                    animatorSet[0] = null;
                }
                animatorSet[0] = new AnimatorSet();
                animatorSet[0].playTogether(animators);
                animatorSet[0].addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        if (animator.equals(animatorSet[0])) {
                            animatorSet[0] = null;
                        }
                    }
                });
                animatorSet[0].setDuration(150);
                animatorSet[0].start();
            }
        }
    }

    /* renamed from: lambda$createView$3$org-telegram-ui-DataAutoDownloadActivity  reason: not valid java name */
    public /* synthetic */ void m3354lambda$createView$3$orgtelegramuiDataAutoDownloadActivity(TextCheckBoxCell[] cells, int type, MaxFileSizeCell[] sizeCell, int index, TextCheckCell[] checkCell, int position, String key3, String key22, BottomSheet.Builder builder, View view, View v1) {
        int i = position;
        int i2 = this.currentPresetNum;
        if (i2 != 3) {
            if (i2 == 0) {
                this.typePreset.set(this.lowPreset);
            } else if (i2 == 1) {
                this.typePreset.set(this.mediumPreset);
            } else if (i2 == 2) {
                this.typePreset.set(this.highPreset);
            }
        }
        for (int a = 0; a < 4; a++) {
            if (cells[a].isChecked()) {
                int[] iArr = this.typePreset.mask;
                iArr[a] = iArr[a] | type;
            } else {
                int[] iArr2 = this.typePreset.mask;
                iArr2[a] = iArr2[a] & (type ^ -1);
            }
        }
        if (sizeCell[0] != null) {
            int size = (int) sizeCell[0].getSize();
            this.typePreset.sizes[index] = (long) ((int) sizeCell[0].getSize());
        }
        if (checkCell[0] != null) {
            if (i == this.videosRow) {
                this.typePreset.preloadVideo = checkCell[0].isChecked();
            } else {
                this.typePreset.preloadMusic = checkCell[0].isChecked();
            }
        }
        SharedPreferences.Editor editor = MessagesController.getMainSettings(this.currentAccount).edit();
        editor.putString(key3, this.typePreset.toString());
        this.currentPresetNum = 3;
        editor.putInt(key22, 3);
        int i3 = this.currentType;
        if (i3 == 0) {
            DownloadController.getInstance(this.currentAccount).currentMobilePreset = this.currentPresetNum;
        } else if (i3 == 1) {
            DownloadController.getInstance(this.currentAccount).currentWifiPreset = this.currentPresetNum;
        } else {
            DownloadController.getInstance(this.currentAccount).currentRoamingPreset = this.currentPresetNum;
        }
        editor.commit();
        builder.getDismissRunnable().run();
        RecyclerView.ViewHolder holder = this.listView.findContainingViewHolder(view);
        if (holder != null) {
            this.animateChecked = true;
            this.listAdapter.onBindViewHolder(holder, i);
            this.animateChecked = false;
        }
        DownloadController.getInstance(this.currentAccount).checkAutodownloadSettings();
        this.wereAnyChanges = true;
        fillPresets();
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
    }

    public void onPause() {
        super.onPause();
        if (this.wereAnyChanges) {
            DownloadController.getInstance(this.currentAccount).savePresetToServer(this.currentType);
            this.wereAnyChanges = false;
        }
    }

    private void fillPresets() {
        this.presets.clear();
        this.presets.add(this.lowPreset);
        this.presets.add(this.mediumPreset);
        this.presets.add(this.highPreset);
        if (!this.typePreset.equals(this.lowPreset) && !this.typePreset.equals(this.mediumPreset) && !this.typePreset.equals(this.highPreset)) {
            this.presets.add(this.typePreset);
        }
        Collections.sort(this.presets, DataAutoDownloadActivity$$ExternalSyntheticLambda4.INSTANCE);
        int i = this.currentPresetNum;
        if (i == 0 || (i == 3 && this.typePreset.equals(this.lowPreset))) {
            this.selectedPreset = this.presets.indexOf(this.lowPreset);
        } else {
            int i2 = this.currentPresetNum;
            if (i2 == 1 || (i2 == 3 && this.typePreset.equals(this.mediumPreset))) {
                this.selectedPreset = this.presets.indexOf(this.mediumPreset);
            } else {
                int i3 = this.currentPresetNum;
                if (i3 == 2 || (i3 == 3 && this.typePreset.equals(this.highPreset))) {
                    this.selectedPreset = this.presets.indexOf(this.highPreset);
                } else {
                    this.selectedPreset = this.presets.indexOf(this.typePreset);
                }
            }
        }
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            RecyclerView.ViewHolder holder = recyclerListView.findViewHolderForAdapterPosition(this.usageProgressRow);
            if (holder == null || !(holder.itemView instanceof SlideChooseView)) {
                this.listAdapter.notifyItemChanged(this.usageProgressRow);
            } else {
                updatePresetChoseView((SlideChooseView) holder.itemView);
            }
        }
    }

    static /* synthetic */ int lambda$fillPresets$5(DownloadController.Preset o1, DownloadController.Preset o2) {
        int index1 = DownloadController.typeToIndex(4);
        int index2 = DownloadController.typeToIndex(8);
        boolean video1 = false;
        boolean doc1 = false;
        for (int a = 0; a < o1.mask.length; a++) {
            if ((o1.mask[a] & 4) != 0) {
                video1 = true;
            }
            if ((o1.mask[a] & 8) != 0) {
                doc1 = true;
            }
            if (video1 && doc1) {
                break;
            }
        }
        boolean video2 = false;
        boolean doc2 = false;
        for (int a2 = 0; a2 < o2.mask.length; a2++) {
            if ((o2.mask[a2] & 4) != 0) {
                video2 = true;
            }
            if ((o2.mask[a2] & 8) != 0) {
                doc2 = true;
            }
            if (video2 && doc2) {
                break;
            }
        }
        long j = 0;
        long size1 = (video1 ? o1.sizes[index1] : 0) + (doc1 ? o1.sizes[index2] : 0);
        long j2 = video2 ? o2.sizes[index1] : 0;
        if (doc2) {
            j = o2.sizes[index2];
        }
        long size2 = j2 + j;
        if (size1 > size2) {
            return 1;
        }
        if (size1 < size2) {
            return -1;
        }
        return 0;
    }

    private void updateRows() {
        this.rowCount = 0;
        int i = 0 + 1;
        this.rowCount = i;
        this.autoDownloadRow = 0;
        this.rowCount = i + 1;
        this.autoDownloadSectionRow = i;
        if (this.typePreset.enabled) {
            int i2 = this.rowCount;
            int i3 = i2 + 1;
            this.rowCount = i3;
            this.usageHeaderRow = i2;
            int i4 = i3 + 1;
            this.rowCount = i4;
            this.usageProgressRow = i3;
            int i5 = i4 + 1;
            this.rowCount = i5;
            this.usageSectionRow = i4;
            int i6 = i5 + 1;
            this.rowCount = i6;
            this.typeHeaderRow = i5;
            int i7 = i6 + 1;
            this.rowCount = i7;
            this.photosRow = i6;
            int i8 = i7 + 1;
            this.rowCount = i8;
            this.videosRow = i7;
            int i9 = i8 + 1;
            this.rowCount = i9;
            this.filesRow = i8;
            this.rowCount = i9 + 1;
            this.typeSectionRow = i9;
            return;
        }
        this.usageHeaderRow = -1;
        this.usageProgressRow = -1;
        this.usageSectionRow = -1;
        this.typeHeaderRow = -1;
        this.photosRow = -1;
        this.videosRow = -1;
        this.filesRow = -1;
        this.typeSectionRow = -1;
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return DataAutoDownloadActivity.this.rowCount;
        }

        /* JADX WARNING: Removed duplicated region for block: B:66:0x0229  */
        /* JADX WARNING: Removed duplicated region for block: B:71:0x0233  */
        /* JADX WARNING: Removed duplicated region for block: B:72:0x0235  */
        /* JADX WARNING: Removed duplicated region for block: B:75:0x0240  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r20, int r21) {
            /*
                r19 = this;
                r0 = r19
                r1 = r20
                r2 = r21
                int r3 = r20.getItemViewType()
                r4 = 4
                r5 = 2
                r6 = 0
                r7 = 1
                switch(r3) {
                    case 0: goto L_0x0286;
                    case 1: goto L_0x0011;
                    case 2: goto L_0x0258;
                    case 3: goto L_0x024d;
                    case 4: goto L_0x00c3;
                    case 5: goto L_0x0013;
                    default: goto L_0x0011;
                }
            L_0x0011:
                goto L_0x02d0
            L_0x0013:
                android.view.View r3 = r1.itemView
                org.telegram.ui.Cells.TextInfoPrivacyCell r3 = (org.telegram.ui.Cells.TextInfoPrivacyCell) r3
                org.telegram.ui.DataAutoDownloadActivity r8 = org.telegram.ui.DataAutoDownloadActivity.this
                int r8 = r8.typeSectionRow
                r9 = 2131165435(0x7var_fb, float:1.7945087E38)
                java.lang.String r10 = "windowBackgroundGrayShadow"
                if (r2 != r8) goto L_0x0041
                r4 = 2131624562(0x7f0e0272, float:1.8876307E38)
                java.lang.String r5 = "AutoDownloadAudioInfo"
                java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
                r3.setText(r4)
                android.content.Context r4 = r0.mContext
                android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r4, (int) r9, (java.lang.String) r10)
                r3.setBackgroundDrawable(r4)
                r3.setFixedSize(r6)
                r3.setImportantForAccessibility(r7)
                goto L_0x02d0
            L_0x0041:
                org.telegram.ui.DataAutoDownloadActivity r6 = org.telegram.ui.DataAutoDownloadActivity.this
                int r6 = r6.autoDownloadSectionRow
                if (r2 != r6) goto L_0x02d0
                org.telegram.ui.DataAutoDownloadActivity r6 = org.telegram.ui.DataAutoDownloadActivity.this
                int r6 = r6.usageHeaderRow
                r8 = -1
                if (r6 != r8) goto L_0x00a1
                android.content.Context r4 = r0.mContext
                r6 = 2131165436(0x7var_fc, float:1.794509E38)
                android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r4, (int) r6, (java.lang.String) r10)
                r3.setBackgroundDrawable(r4)
                org.telegram.ui.DataAutoDownloadActivity r4 = org.telegram.ui.DataAutoDownloadActivity.this
                int r4 = r4.currentType
                if (r4 != 0) goto L_0x0073
                r4 = 2131624581(0x7f0e0285, float:1.8876346E38)
                java.lang.String r5 = "AutoDownloadOnMobileDataInfo"
                java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
                r3.setText(r4)
                goto L_0x009c
            L_0x0073:
                org.telegram.ui.DataAutoDownloadActivity r4 = org.telegram.ui.DataAutoDownloadActivity.this
                int r4 = r4.currentType
                if (r4 != r7) goto L_0x0088
                r4 = 2131624586(0x7f0e028a, float:1.8876356E38)
                java.lang.String r5 = "AutoDownloadOnWiFiDataInfo"
                java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
                r3.setText(r4)
                goto L_0x009c
            L_0x0088:
                org.telegram.ui.DataAutoDownloadActivity r4 = org.telegram.ui.DataAutoDownloadActivity.this
                int r4 = r4.currentType
                if (r4 != r5) goto L_0x009c
                r4 = 2131624583(0x7f0e0287, float:1.887635E38)
                java.lang.String r5 = "AutoDownloadOnRoamingDataInfo"
                java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
                r3.setText(r4)
            L_0x009c:
                r3.setImportantForAccessibility(r7)
                goto L_0x02d0
            L_0x00a1:
                android.content.Context r6 = r0.mContext
                android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r6, (int) r9, (java.lang.String) r10)
                r3.setBackgroundDrawable(r6)
                r6 = 0
                r3.setText(r6)
                r6 = 12
                r3.setFixedSize(r6)
                int r6 = android.os.Build.VERSION.SDK_INT
                r7 = 19
                if (r6 < r7) goto L_0x00be
                r3.setImportantForAccessibility(r4)
                goto L_0x02d0
            L_0x00be:
                r3.setImportantForAccessibility(r5)
                goto L_0x02d0
            L_0x00c3:
                android.view.View r3 = r1.itemView
                org.telegram.ui.Cells.NotificationsCheckCell r3 = (org.telegram.ui.Cells.NotificationsCheckCell) r3
                org.telegram.ui.DataAutoDownloadActivity r8 = org.telegram.ui.DataAutoDownloadActivity.this
                int r8 = r8.photosRow
                if (r2 != r8) goto L_0x00dd
                r8 = 2131624587(0x7f0e028b, float:1.8876358E38)
                java.lang.String r9 = "AutoDownloadPhotos"
                java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
                r9 = 1
                r15 = r8
                r16 = r9
                goto L_0x0101
            L_0x00dd:
                org.telegram.ui.DataAutoDownloadActivity r8 = org.telegram.ui.DataAutoDownloadActivity.this
                int r8 = r8.videosRow
                if (r2 != r8) goto L_0x00f3
                r8 = 2131624597(0x7f0e0295, float:1.8876378E38)
                java.lang.String r9 = "AutoDownloadVideos"
                java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
                r9 = 4
                r15 = r8
                r16 = r9
                goto L_0x0101
            L_0x00f3:
                r8 = 2131624567(0x7f0e0277, float:1.8876317E38)
                java.lang.String r9 = "AutoDownloadFiles"
                java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
                r9 = 8
                r15 = r8
                r16 = r9
            L_0x0101:
                org.telegram.ui.DataAutoDownloadActivity r8 = org.telegram.ui.DataAutoDownloadActivity.this
                int r8 = r8.currentType
                if (r8 != 0) goto L_0x0119
                org.telegram.ui.DataAutoDownloadActivity r8 = org.telegram.ui.DataAutoDownloadActivity.this
                int r8 = r8.currentAccount
                org.telegram.messenger.DownloadController r8 = org.telegram.messenger.DownloadController.getInstance(r8)
                org.telegram.messenger.DownloadController$Preset r8 = r8.getCurrentMobilePreset()
                r14 = r8
                goto L_0x0140
            L_0x0119:
                org.telegram.ui.DataAutoDownloadActivity r8 = org.telegram.ui.DataAutoDownloadActivity.this
                int r8 = r8.currentType
                if (r8 != r7) goto L_0x0131
                org.telegram.ui.DataAutoDownloadActivity r8 = org.telegram.ui.DataAutoDownloadActivity.this
                int r8 = r8.currentAccount
                org.telegram.messenger.DownloadController r8 = org.telegram.messenger.DownloadController.getInstance(r8)
                org.telegram.messenger.DownloadController$Preset r8 = r8.getCurrentWiFiPreset()
                r14 = r8
                goto L_0x0140
            L_0x0131:
                org.telegram.ui.DataAutoDownloadActivity r8 = org.telegram.ui.DataAutoDownloadActivity.this
                int r8 = r8.currentAccount
                org.telegram.messenger.DownloadController r8 = org.telegram.messenger.DownloadController.getInstance(r8)
                org.telegram.messenger.DownloadController$Preset r8 = r8.getCurrentRoamingPreset()
                r14 = r8
            L_0x0140:
                long[] r8 = r14.sizes
                int r9 = org.telegram.messenger.DownloadController.typeToIndex(r16)
                r17 = r8[r9]
                r8 = 0
                java.lang.StringBuilder r9 = new java.lang.StringBuilder
                r9.<init>()
                r10 = 0
                r13 = r8
            L_0x0150:
                int[] r8 = r14.mask
                int r8 = r8.length
                if (r10 >= r8) goto L_0x01a5
                int[] r8 = r14.mask
                r8 = r8[r10]
                r8 = r8 & r16
                if (r8 == 0) goto L_0x01a2
                int r8 = r9.length()
                if (r8 == 0) goto L_0x0168
                java.lang.String r8 = ", "
                r9.append(r8)
            L_0x0168:
                switch(r10) {
                    case 0: goto L_0x0193;
                    case 1: goto L_0x0186;
                    case 2: goto L_0x0179;
                    case 3: goto L_0x016c;
                    default: goto L_0x016b;
                }
            L_0x016b:
                goto L_0x01a0
            L_0x016c:
                r8 = 2131624563(0x7f0e0273, float:1.887631E38)
                java.lang.String r11 = "AutoDownloadChannels"
                java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r11, r8)
                r9.append(r8)
                goto L_0x01a0
            L_0x0179:
                r8 = 2131624570(0x7f0e027a, float:1.8876323E38)
                java.lang.String r11 = "AutoDownloadGroups"
                java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r11, r8)
                r9.append(r8)
                goto L_0x01a0
            L_0x0186:
                r8 = 2131624590(0x7f0e028e, float:1.8876364E38)
                java.lang.String r11 = "AutoDownloadPm"
                java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r11, r8)
                r9.append(r8)
                goto L_0x01a0
            L_0x0193:
                r8 = 2131624564(0x7f0e0274, float:1.8876311E38)
                java.lang.String r11 = "AutoDownloadContacts"
                java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r11, r8)
                r9.append(r8)
            L_0x01a0:
                int r13 = r13 + 1
            L_0x01a2:
                int r10 = r10 + 1
                goto L_0x0150
            L_0x01a5:
                if (r13 != r4) goto L_0x01d4
                r9.setLength(r6)
                org.telegram.ui.DataAutoDownloadActivity r4 = org.telegram.ui.DataAutoDownloadActivity.this
                int r4 = r4.photosRow
                if (r2 != r4) goto L_0x01bf
                r4 = 2131624578(0x7f0e0282, float:1.887634E38)
                java.lang.String r5 = "AutoDownloadOnAllChats"
                java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
                r9.append(r4)
                goto L_0x01e2
            L_0x01bf:
                r4 = 2131624596(0x7f0e0294, float:1.8876376E38)
                java.lang.Object[] r5 = new java.lang.Object[r7]
                java.lang.String r8 = org.telegram.messenger.AndroidUtilities.formatFileSize(r17)
                r5[r6] = r8
                java.lang.String r8 = "AutoDownloadUpToOnAllChats"
                java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r8, r4, r5)
                r9.append(r4)
                goto L_0x01e2
            L_0x01d4:
                if (r13 != 0) goto L_0x01e4
                r4 = 2131624577(0x7f0e0281, float:1.8876338E38)
                java.lang.String r5 = "AutoDownloadOff"
                java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
                r9.append(r4)
            L_0x01e2:
                r4 = r9
                goto L_0x0221
            L_0x01e4:
                org.telegram.ui.DataAutoDownloadActivity r4 = org.telegram.ui.DataAutoDownloadActivity.this
                int r4 = r4.photosRow
                if (r2 != r4) goto L_0x0204
                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                r5 = 2131624579(0x7f0e0283, float:1.8876342E38)
                java.lang.Object[] r8 = new java.lang.Object[r7]
                java.lang.String r10 = r9.toString()
                r8[r6] = r10
                java.lang.String r10 = "AutoDownloadOnFor"
                java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r10, r5, r8)
                r4.<init>(r5)
                r9 = r4
                goto L_0x0221
            L_0x0204:
                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                r8 = 2131624584(0x7f0e0288, float:1.8876352E38)
                java.lang.Object[] r5 = new java.lang.Object[r5]
                java.lang.String r10 = org.telegram.messenger.AndroidUtilities.formatFileSize(r17)
                r5[r6] = r10
                java.lang.String r10 = r9.toString()
                r5[r7] = r10
                java.lang.String r10 = "AutoDownloadOnUpToFor"
                java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r10, r8, r5)
                r4.<init>(r5)
                r9 = r4
            L_0x0221:
                org.telegram.ui.DataAutoDownloadActivity r5 = org.telegram.ui.DataAutoDownloadActivity.this
                boolean r5 = r5.animateChecked
                if (r5 == 0) goto L_0x0231
                if (r13 == 0) goto L_0x022d
                r5 = 1
                goto L_0x022e
            L_0x022d:
                r5 = 0
            L_0x022e:
                r3.setChecked(r5)
            L_0x0231:
                if (r13 == 0) goto L_0x0235
                r11 = 1
                goto L_0x0236
            L_0x0235:
                r11 = 0
            L_0x0236:
                r12 = 0
                r5 = 1
                org.telegram.ui.DataAutoDownloadActivity r8 = org.telegram.ui.DataAutoDownloadActivity.this
                int r8 = r8.filesRow
                if (r2 == r8) goto L_0x0241
                r6 = 1
            L_0x0241:
                r8 = r3
                r9 = r15
                r10 = r4
                r7 = r13
                r13 = r5
                r5 = r14
                r14 = r6
                r8.setTextAndValueAndCheck(r9, r10, r11, r12, r13, r14)
                goto L_0x02d0
            L_0x024d:
                android.view.View r3 = r1.itemView
                org.telegram.ui.Components.SlideChooseView r3 = (org.telegram.ui.Components.SlideChooseView) r3
                org.telegram.ui.DataAutoDownloadActivity r4 = org.telegram.ui.DataAutoDownloadActivity.this
                r4.updatePresetChoseView(r3)
                goto L_0x02d0
            L_0x0258:
                android.view.View r3 = r1.itemView
                org.telegram.ui.Cells.HeaderCell r3 = (org.telegram.ui.Cells.HeaderCell) r3
                org.telegram.ui.DataAutoDownloadActivity r4 = org.telegram.ui.DataAutoDownloadActivity.this
                int r4 = r4.usageHeaderRow
                if (r2 != r4) goto L_0x0271
                r4 = 2131624566(0x7f0e0276, float:1.8876315E38)
                java.lang.String r5 = "AutoDownloadDataUsage"
                java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
                r3.setText(r4)
                goto L_0x02d0
            L_0x0271:
                org.telegram.ui.DataAutoDownloadActivity r4 = org.telegram.ui.DataAutoDownloadActivity.this
                int r4 = r4.typeHeaderRow
                if (r2 != r4) goto L_0x02d0
                r4 = 2131624595(0x7f0e0293, float:1.8876374E38)
                java.lang.String r5 = "AutoDownloadTypes"
                java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
                r3.setText(r4)
                goto L_0x02d0
            L_0x0286:
                android.view.View r3 = r1.itemView
                org.telegram.ui.Cells.TextCheckCell r3 = (org.telegram.ui.Cells.TextCheckCell) r3
                org.telegram.ui.DataAutoDownloadActivity r4 = org.telegram.ui.DataAutoDownloadActivity.this
                int r4 = r4.autoDownloadRow
                if (r2 != r4) goto L_0x02d0
                r3.setDrawCheckRipple(r7)
                r4 = 2131624575(0x7f0e027f, float:1.8876334E38)
                java.lang.String r5 = "AutoDownloadMedia"
                java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
                org.telegram.ui.DataAutoDownloadActivity r5 = org.telegram.ui.DataAutoDownloadActivity.this
                org.telegram.messenger.DownloadController$Preset r5 = r5.typePreset
                boolean r5 = r5.enabled
                r3.setTextAndCheck(r4, r5, r6)
                org.telegram.ui.DataAutoDownloadActivity r4 = org.telegram.ui.DataAutoDownloadActivity.this
                org.telegram.messenger.DownloadController$Preset r4 = r4.typePreset
                boolean r4 = r4.enabled
                java.lang.String r5 = "windowBackgroundChecked"
                java.lang.String r6 = "windowBackgroundUnchecked"
                if (r4 == 0) goto L_0x02b9
                r4 = r5
                goto L_0x02ba
            L_0x02b9:
                r4 = r6
            L_0x02ba:
                r3.setTag(r4)
                org.telegram.ui.DataAutoDownloadActivity r4 = org.telegram.ui.DataAutoDownloadActivity.this
                org.telegram.messenger.DownloadController$Preset r4 = r4.typePreset
                boolean r4 = r4.enabled
                if (r4 == 0) goto L_0x02c8
                goto L_0x02c9
            L_0x02c8:
                r5 = r6
            L_0x02c9:
                int r4 = org.telegram.ui.ActionBar.Theme.getColor(r5)
                r3.setBackgroundColor(r4)
            L_0x02d0:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DataAutoDownloadActivity.ListAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int position = holder.getAdapterPosition();
            return position == DataAutoDownloadActivity.this.photosRow || position == DataAutoDownloadActivity.this.videosRow || position == DataAutoDownloadActivity.this.filesRow;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    TextCheckCell cell = new TextCheckCell(this.mContext);
                    cell.setColors("windowBackgroundCheckText", "switchTrackBlue", "switchTrackBlueChecked", "switchTrackBlueThumb", "switchTrackBlueThumbChecked");
                    cell.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                    cell.setHeight(56);
                    view = cell;
                    break;
                case 1:
                    view = new ShadowSectionCell(this.mContext);
                    break;
                case 2:
                    View headerCell = new HeaderCell(this.mContext);
                    headerCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    view = headerCell;
                    break;
                case 3:
                    SlideChooseView slideChooseView = new SlideChooseView(this.mContext);
                    slideChooseView.setCallback(new DataAutoDownloadActivity$ListAdapter$$ExternalSyntheticLambda0(this));
                    slideChooseView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    view = slideChooseView;
                    break;
                case 4:
                    View notificationsCheckCell = new NotificationsCheckCell(this.mContext);
                    notificationsCheckCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    view = notificationsCheckCell;
                    break;
                default:
                    View view2 = new TextInfoPrivacyCell(this.mContext);
                    view2.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                    view = view2;
                    break;
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        /* renamed from: lambda$onCreateViewHolder$0$org-telegram-ui-DataAutoDownloadActivity$ListAdapter  reason: not valid java name */
        public /* synthetic */ void m3356x3dca3var_(int index) {
            DownloadController.Preset preset = (DownloadController.Preset) DataAutoDownloadActivity.this.presets.get(index);
            if (preset == DataAutoDownloadActivity.this.lowPreset) {
                int unused = DataAutoDownloadActivity.this.currentPresetNum = 0;
            } else if (preset == DataAutoDownloadActivity.this.mediumPreset) {
                int unused2 = DataAutoDownloadActivity.this.currentPresetNum = 1;
            } else if (preset == DataAutoDownloadActivity.this.highPreset) {
                int unused3 = DataAutoDownloadActivity.this.currentPresetNum = 2;
            } else {
                int unused4 = DataAutoDownloadActivity.this.currentPresetNum = 3;
            }
            if (DataAutoDownloadActivity.this.currentType == 0) {
                DownloadController.getInstance(DataAutoDownloadActivity.this.currentAccount).currentMobilePreset = DataAutoDownloadActivity.this.currentPresetNum;
            } else if (DataAutoDownloadActivity.this.currentType == 1) {
                DownloadController.getInstance(DataAutoDownloadActivity.this.currentAccount).currentWifiPreset = DataAutoDownloadActivity.this.currentPresetNum;
            } else {
                DownloadController.getInstance(DataAutoDownloadActivity.this.currentAccount).currentRoamingPreset = DataAutoDownloadActivity.this.currentPresetNum;
            }
            SharedPreferences.Editor editor = MessagesController.getMainSettings(DataAutoDownloadActivity.this.currentAccount).edit();
            editor.putInt(DataAutoDownloadActivity.this.key2, DataAutoDownloadActivity.this.currentPresetNum);
            editor.commit();
            DownloadController.getInstance(DataAutoDownloadActivity.this.currentAccount).checkAutodownloadSettings();
            for (int a = 0; a < 3; a++) {
                RecyclerView.ViewHolder holder = DataAutoDownloadActivity.this.listView.findViewHolderForAdapterPosition(DataAutoDownloadActivity.this.photosRow + a);
                if (holder != null) {
                    DataAutoDownloadActivity.this.listAdapter.onBindViewHolder(holder, DataAutoDownloadActivity.this.photosRow + a);
                }
            }
            boolean unused5 = DataAutoDownloadActivity.this.wereAnyChanges = true;
        }

        public int getItemViewType(int position) {
            if (position == DataAutoDownloadActivity.this.autoDownloadRow) {
                return 0;
            }
            if (position == DataAutoDownloadActivity.this.usageSectionRow) {
                return 1;
            }
            if (position == DataAutoDownloadActivity.this.usageHeaderRow || position == DataAutoDownloadActivity.this.typeHeaderRow) {
                return 2;
            }
            if (position == DataAutoDownloadActivity.this.usageProgressRow) {
                return 3;
            }
            if (position == DataAutoDownloadActivity.this.photosRow || position == DataAutoDownloadActivity.this.videosRow || position == DataAutoDownloadActivity.this.filesRow) {
                return 4;
            }
            return 5;
        }
    }

    /* access modifiers changed from: private */
    public void updatePresetChoseView(SlideChooseView slideChooseView) {
        String[] presetsStr = new String[this.presets.size()];
        for (int i = 0; i < this.presets.size(); i++) {
            DownloadController.Preset preset = this.presets.get(i);
            if (preset == this.lowPreset) {
                presetsStr[i] = LocaleController.getString("AutoDownloadLow", NUM);
            } else if (preset == this.mediumPreset) {
                presetsStr[i] = LocaleController.getString("AutoDownloadMedium", NUM);
            } else if (preset == this.highPreset) {
                presetsStr[i] = LocaleController.getString("AutoDownloadHigh", NUM);
            } else {
                presetsStr[i] = LocaleController.getString("AutoDownloadCustom", NUM);
            }
        }
        slideChooseView.setOptions(this.selectedPreset, presetsStr);
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{HeaderCell.class, NotificationsCheckCell.class, SlideChooseView.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCheckCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundChecked"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCheckCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundUnchecked"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundCheckText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackBlue"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackBlueChecked"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackBlueThumb"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackBlueThumbChecked"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackBlueSelector"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackBlueSelectorChecked"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrack"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackChecked"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{SlideChooseView.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrack"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{SlideChooseView.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackChecked"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{SlideChooseView.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText"));
        return themeDescriptions;
    }
}
