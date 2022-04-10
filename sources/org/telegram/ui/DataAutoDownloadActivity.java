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

    public DataAutoDownloadActivity(int i) {
        this.currentType = i;
        this.lowPreset = DownloadController.getInstance(this.currentAccount).lowPreset;
        this.mediumPreset = DownloadController.getInstance(this.currentAccount).mediumPreset;
        this.highPreset = DownloadController.getInstance(this.currentAccount).highPreset;
        int i2 = this.currentType;
        if (i2 == 0) {
            this.currentPresetNum = DownloadController.getInstance(this.currentAccount).currentMobilePreset;
            this.typePreset = DownloadController.getInstance(this.currentAccount).mobilePreset;
            this.defaultPreset = this.mediumPreset;
            this.key = "mobilePreset";
            this.key2 = "currentMobilePreset";
        } else if (i2 == 1) {
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
            public void onItemClick(int i) {
                if (i == -1) {
                    DataAutoDownloadActivity.this.finishFragment();
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
        ((DefaultItemAnimator) this.listView.getItemAnimator()).setDelayAnimations(false);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        ((FrameLayout) this.fragmentView).addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListenerExtended) new DataAutoDownloadActivity$$ExternalSyntheticLambda5(this));
        return this.fragmentView;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v3, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v41, resolved type: org.telegram.ui.ArticleViewer$BlockBlockquoteCell} */
    /* JADX WARNING: type inference failed for: r15v0 */
    /* JADX WARNING: type inference failed for: r15v1, types: [int] */
    /* JADX WARNING: type inference failed for: r0v33, types: [boolean] */
    /* JADX WARNING: type inference failed for: r0v34 */
    /* JADX WARNING: type inference failed for: r0v35 */
    /* JADX WARNING: type inference failed for: r15v7 */
    /* JADX WARNING: type inference failed for: r15v34 */
    /* access modifiers changed from: private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x005c  */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x005e  */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x006b  */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x007c  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x0085  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x00b4  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x00bf  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$createView$4(android.view.View r30, int r31, float r32, float r33) {
        /*
            r29 = this;
            r12 = r29
            r11 = r30
            r8 = r31
            int r0 = r12.autoDownloadRow
            r1 = 8
            r9 = 2
            r10 = 4
            r13 = 3
            r14 = 1
            r15 = 0
            if (r8 != r0) goto L_0x00eb
            int r0 = r12.currentPresetNum
            if (r0 == r13) goto L_0x0032
            if (r0 != 0) goto L_0x001f
            org.telegram.messenger.DownloadController$Preset r0 = r12.typePreset
            org.telegram.messenger.DownloadController$Preset r2 = r12.lowPreset
            r0.set((org.telegram.messenger.DownloadController.Preset) r2)
            goto L_0x0032
        L_0x001f:
            if (r0 != r14) goto L_0x0029
            org.telegram.messenger.DownloadController$Preset r0 = r12.typePreset
            org.telegram.messenger.DownloadController$Preset r2 = r12.mediumPreset
            r0.set((org.telegram.messenger.DownloadController.Preset) r2)
            goto L_0x0032
        L_0x0029:
            if (r0 != r9) goto L_0x0032
            org.telegram.messenger.DownloadController$Preset r0 = r12.typePreset
            org.telegram.messenger.DownloadController$Preset r2 = r12.highPreset
            r0.set((org.telegram.messenger.DownloadController.Preset) r2)
        L_0x0032:
            r0 = r11
            org.telegram.ui.Cells.TextCheckCell r0 = (org.telegram.ui.Cells.TextCheckCell) r0
            boolean r2 = r0.isChecked()
            if (r2 != 0) goto L_0x004b
            org.telegram.messenger.DownloadController$Preset r3 = r12.typePreset
            boolean r4 = r3.enabled
            if (r4 == 0) goto L_0x004b
            org.telegram.messenger.DownloadController$Preset r4 = r12.defaultPreset
            int[] r4 = r4.mask
            int[] r3 = r3.mask
            java.lang.System.arraycopy(r4, r15, r3, r15, r10)
            goto L_0x0052
        L_0x004b:
            org.telegram.messenger.DownloadController$Preset r3 = r12.typePreset
            boolean r4 = r3.enabled
            r4 = r4 ^ r14
            r3.enabled = r4
        L_0x0052:
            org.telegram.messenger.DownloadController$Preset r3 = r12.typePreset
            boolean r3 = r3.enabled
            java.lang.String r4 = "windowBackgroundChecked"
            java.lang.String r5 = "windowBackgroundUnchecked"
            if (r3 == 0) goto L_0x005e
            r3 = r4
            goto L_0x005f
        L_0x005e:
            r3 = r5
        L_0x005f:
            r11.setTag(r3)
            r3 = r2 ^ 1
            org.telegram.messenger.DownloadController$Preset r6 = r12.typePreset
            boolean r6 = r6.enabled
            if (r6 == 0) goto L_0x006b
            goto L_0x006c
        L_0x006b:
            r4 = r5
        L_0x006c:
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            r0.setBackgroundColorAnimated(r3, r4)
            r29.updateRows()
            org.telegram.messenger.DownloadController$Preset r3 = r12.typePreset
            boolean r3 = r3.enabled
            if (r3 == 0) goto L_0x0085
            org.telegram.ui.DataAutoDownloadActivity$ListAdapter r3 = r12.listAdapter
            int r4 = r12.autoDownloadSectionRow
            int r4 = r4 + r14
            r3.notifyItemRangeInserted(r4, r1)
            goto L_0x008d
        L_0x0085:
            org.telegram.ui.DataAutoDownloadActivity$ListAdapter r3 = r12.listAdapter
            int r4 = r12.autoDownloadSectionRow
            int r4 = r4 + r14
            r3.notifyItemRangeRemoved(r4, r1)
        L_0x008d:
            org.telegram.ui.DataAutoDownloadActivity$ListAdapter r1 = r12.listAdapter
            int r3 = r12.autoDownloadSectionRow
            r1.notifyItemChanged(r3)
            int r1 = r12.currentAccount
            android.content.SharedPreferences r1 = org.telegram.messenger.MessagesController.getMainSettings(r1)
            android.content.SharedPreferences$Editor r1 = r1.edit()
            java.lang.String r3 = r12.key
            org.telegram.messenger.DownloadController$Preset r4 = r12.typePreset
            java.lang.String r4 = r4.toString()
            r1.putString(r3, r4)
            java.lang.String r3 = r12.key2
            r12.currentPresetNum = r13
            r1.putInt(r3, r13)
            int r3 = r12.currentType
            if (r3 != 0) goto L_0x00bf
            int r3 = r12.currentAccount
            org.telegram.messenger.DownloadController r3 = org.telegram.messenger.DownloadController.getInstance(r3)
            int r4 = r12.currentPresetNum
            r3.currentMobilePreset = r4
            goto L_0x00d6
        L_0x00bf:
            if (r3 != r14) goto L_0x00cc
            int r3 = r12.currentAccount
            org.telegram.messenger.DownloadController r3 = org.telegram.messenger.DownloadController.getInstance(r3)
            int r4 = r12.currentPresetNum
            r3.currentWifiPreset = r4
            goto L_0x00d6
        L_0x00cc:
            int r3 = r12.currentAccount
            org.telegram.messenger.DownloadController r3 = org.telegram.messenger.DownloadController.getInstance(r3)
            int r4 = r12.currentPresetNum
            r3.currentRoamingPreset = r4
        L_0x00d6:
            r1.commit()
            r1 = r2 ^ 1
            r0.setChecked(r1)
            int r0 = r12.currentAccount
            org.telegram.messenger.DownloadController r0 = org.telegram.messenger.DownloadController.getInstance(r0)
            r0.checkAutodownloadSettings()
            r12.wereAnyChanges = r14
            goto L_0x05c3
        L_0x00eb:
            int r0 = r12.photosRow
            if (r8 == r0) goto L_0x00f7
            int r0 = r12.videosRow
            if (r8 == r0) goto L_0x00f7
            int r0 = r12.filesRow
            if (r8 != r0) goto L_0x05c3
        L_0x00f7:
            boolean r0 = r30.isEnabled()
            if (r0 != 0) goto L_0x00fe
            return
        L_0x00fe:
            int r0 = r12.photosRow
            if (r8 != r0) goto L_0x0105
            r16 = 1
            goto L_0x010e
        L_0x0105:
            int r0 = r12.videosRow
            if (r8 != r0) goto L_0x010c
            r16 = 4
            goto L_0x010e
        L_0x010c:
            r16 = 8
        L_0x010e:
            int r17 = org.telegram.messenger.DownloadController.typeToIndex(r16)
            int r0 = r12.currentType
            if (r0 != 0) goto L_0x0128
            int r0 = r12.currentAccount
            org.telegram.messenger.DownloadController r0 = org.telegram.messenger.DownloadController.getInstance(r0)
            org.telegram.messenger.DownloadController$Preset r0 = r0.getCurrentMobilePreset()
            java.lang.String r1 = "mobilePreset"
            java.lang.String r2 = "currentMobilePreset"
        L_0x0124:
            r7 = r0
            r6 = r1
            r5 = r2
            goto L_0x0148
        L_0x0128:
            if (r0 != r14) goto L_0x0139
            int r0 = r12.currentAccount
            org.telegram.messenger.DownloadController r0 = org.telegram.messenger.DownloadController.getInstance(r0)
            org.telegram.messenger.DownloadController$Preset r0 = r0.getCurrentWiFiPreset()
            java.lang.String r1 = "wifiPreset"
            java.lang.String r2 = "currentWifiPreset"
            goto L_0x0124
        L_0x0139:
            int r0 = r12.currentAccount
            org.telegram.messenger.DownloadController r0 = org.telegram.messenger.DownloadController.getInstance(r0)
            org.telegram.messenger.DownloadController$Preset r0 = r0.getCurrentRoamingPreset()
            java.lang.String r1 = "roamingPreset"
            java.lang.String r2 = "currentRoamingPreset"
            goto L_0x0124
        L_0x0148:
            r0 = r11
            org.telegram.ui.Cells.NotificationsCheckCell r0 = (org.telegram.ui.Cells.NotificationsCheckCell) r0
            boolean r1 = r0.isChecked()
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            r3 = 1117257728(0x42980000, float:76.0)
            if (r2 == 0) goto L_0x015e
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r2 = (float) r2
            int r2 = (r32 > r2 ? 1 : (r32 == r2 ? 0 : -1))
            if (r2 <= 0) goto L_0x0170
        L_0x015e:
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 != 0) goto L_0x0225
            int r2 = r30.getMeasuredWidth()
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r2 = r2 - r3
            float r2 = (float) r2
            int r2 = (r32 > r2 ? 1 : (r32 == r2 ? 0 : -1))
            if (r2 < 0) goto L_0x0225
        L_0x0170:
            int r2 = r12.currentPresetNum
            if (r2 == r13) goto L_0x0191
            if (r2 != 0) goto L_0x017e
            org.telegram.messenger.DownloadController$Preset r2 = r12.typePreset
            org.telegram.messenger.DownloadController$Preset r3 = r12.lowPreset
            r2.set((org.telegram.messenger.DownloadController.Preset) r3)
            goto L_0x0191
        L_0x017e:
            if (r2 != r14) goto L_0x0188
            org.telegram.messenger.DownloadController$Preset r2 = r12.typePreset
            org.telegram.messenger.DownloadController$Preset r3 = r12.mediumPreset
            r2.set((org.telegram.messenger.DownloadController.Preset) r3)
            goto L_0x0191
        L_0x0188:
            if (r2 != r9) goto L_0x0191
            org.telegram.messenger.DownloadController$Preset r2 = r12.typePreset
            org.telegram.messenger.DownloadController$Preset r3 = r12.highPreset
            r2.set((org.telegram.messenger.DownloadController.Preset) r3)
        L_0x0191:
            r2 = 0
        L_0x0192:
            org.telegram.messenger.DownloadController$Preset r3 = r12.typePreset
            int[] r3 = r3.mask
            int r3 = r3.length
            if (r2 >= r3) goto L_0x01a6
            int[] r3 = r7.mask
            r3 = r3[r2]
            r3 = r3 & r16
            if (r3 == 0) goto L_0x01a3
            r2 = 1
            goto L_0x01a7
        L_0x01a3:
            int r2 = r2 + 1
            goto L_0x0192
        L_0x01a6:
            r2 = 0
        L_0x01a7:
            org.telegram.messenger.DownloadController$Preset r3 = r12.typePreset
            int[] r3 = r3.mask
            int r4 = r3.length
            if (r15 >= r4) goto L_0x01c3
            if (r1 == 0) goto L_0x01b8
            r4 = r3[r15]
            r7 = r16 ^ -1
            r4 = r4 & r7
            r3[r15] = r4
            goto L_0x01c0
        L_0x01b8:
            if (r2 != 0) goto L_0x01c0
            r4 = r3[r15]
            r4 = r4 | r16
            r3[r15] = r4
        L_0x01c0:
            int r15 = r15 + 1
            goto L_0x01a7
        L_0x01c3:
            int r2 = r12.currentAccount
            android.content.SharedPreferences r2 = org.telegram.messenger.MessagesController.getMainSettings(r2)
            android.content.SharedPreferences$Editor r2 = r2.edit()
            org.telegram.messenger.DownloadController$Preset r3 = r12.typePreset
            java.lang.String r3 = r3.toString()
            r2.putString(r6, r3)
            r12.currentPresetNum = r13
            r2.putInt(r5, r13)
            int r3 = r12.currentType
            if (r3 != 0) goto L_0x01ea
            int r3 = r12.currentAccount
            org.telegram.messenger.DownloadController r3 = org.telegram.messenger.DownloadController.getInstance(r3)
            int r4 = r12.currentPresetNum
            r3.currentMobilePreset = r4
            goto L_0x0201
        L_0x01ea:
            if (r3 != r14) goto L_0x01f7
            int r3 = r12.currentAccount
            org.telegram.messenger.DownloadController r3 = org.telegram.messenger.DownloadController.getInstance(r3)
            int r4 = r12.currentPresetNum
            r3.currentWifiPreset = r4
            goto L_0x0201
        L_0x01f7:
            int r3 = r12.currentAccount
            org.telegram.messenger.DownloadController r3 = org.telegram.messenger.DownloadController.getInstance(r3)
            int r4 = r12.currentPresetNum
            r3.currentRoamingPreset = r4
        L_0x0201:
            r2.commit()
            r1 = r1 ^ r14
            r0.setChecked(r1)
            org.telegram.ui.Components.RecyclerListView r0 = r12.listView
            androidx.recyclerview.widget.RecyclerView$ViewHolder r0 = r0.findContainingViewHolder(r11)
            if (r0 == 0) goto L_0x0215
            org.telegram.ui.DataAutoDownloadActivity$ListAdapter r1 = r12.listAdapter
            r1.onBindViewHolder(r0, r8)
        L_0x0215:
            int r0 = r12.currentAccount
            org.telegram.messenger.DownloadController r0 = org.telegram.messenger.DownloadController.getInstance(r0)
            r0.checkAutodownloadSettings()
            r12.wereAnyChanges = r14
            r29.fillPresets()
            goto L_0x05c3
        L_0x0225:
            android.app.Activity r0 = r29.getParentActivity()
            if (r0 != 0) goto L_0x022c
            return
        L_0x022c:
            org.telegram.ui.ActionBar.BottomSheet$Builder r4 = new org.telegram.ui.ActionBar.BottomSheet$Builder
            android.app.Activity r0 = r29.getParentActivity()
            r4.<init>(r0)
            r4.setApplyTopPadding(r15)
            r4.setApplyBottomPadding(r15)
            android.widget.LinearLayout r3 = new android.widget.LinearLayout
            android.app.Activity r0 = r29.getParentActivity()
            r3.<init>(r0)
            r3.setOrientation(r14)
            r4.setCustomView(r3)
            org.telegram.ui.Cells.HeaderCell r0 = new org.telegram.ui.Cells.HeaderCell
            android.app.Activity r19 = r29.getParentActivity()
            r21 = 21
            r22 = 15
            r23 = 0
            java.lang.String r20 = "dialogTextBlue2"
            r18 = r0
            r18.<init>(r19, r20, r21, r22, r23)
            int r1 = r12.photosRow
            if (r8 != r1) goto L_0x026e
            r1 = 2131624529(0x7f0e0251, float:1.887624E38)
            java.lang.String r2 = "AutoDownloadPhotosTitle"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setText(r1)
            goto L_0x028b
        L_0x026e:
            int r1 = r12.videosRow
            if (r8 != r1) goto L_0x027f
            r1 = 2131624539(0x7f0e025b, float:1.887626E38)
            java.lang.String r2 = "AutoDownloadVideosTitle"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setText(r1)
            goto L_0x028b
        L_0x027f:
            r1 = 2131624509(0x7f0e023d, float:1.88762E38)
            java.lang.String r2 = "AutoDownloadFilesTitle"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setText(r1)
        L_0x028b:
            r1 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r2 = -1
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r2, r1)
            r3.addView(r0, r1)
            org.telegram.ui.Cells.MaxFileSizeCell[] r1 = new org.telegram.ui.Cells.MaxFileSizeCell[r14]
            org.telegram.ui.Cells.TextCheckCell[] r0 = new org.telegram.ui.Cells.TextCheckCell[r14]
            android.animation.AnimatorSet[] r13 = new android.animation.AnimatorSet[r14]
            org.telegram.ui.Cells.TextCheckBoxCell[] r9 = new org.telegram.ui.Cells.TextCheckBoxCell[r10]
            r14 = 0
        L_0x029e:
            if (r14 >= r10) goto L_0x0380
            org.telegram.ui.Cells.TextCheckBoxCell r2 = new org.telegram.ui.Cells.TextCheckBoxCell
            android.app.Activity r10 = r29.getParentActivity()
            r21 = r0
            r0 = 1
            r2.<init>(r10, r0, r15)
            r9[r14] = r2
            if (r14 != 0) goto L_0x02cf
            r0 = r9[r14]
            r10 = 2131624561(0x7f0e0271, float:1.8876305E38)
            java.lang.String r15 = "AutodownloadContacts"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r15, r10)
            int[] r15 = r7.mask
            r22 = 0
            r15 = r15[r22]
            r15 = r15 & r16
            r23 = r1
            r1 = 1
            if (r15 == 0) goto L_0x02ca
            r15 = 1
            goto L_0x02cb
        L_0x02ca:
            r15 = 0
        L_0x02cb:
            r0.setTextAndCheck(r10, r15, r1)
            goto L_0x032d
        L_0x02cf:
            r23 = r1
            r1 = 1
            if (r14 != r1) goto L_0x02ee
            r0 = r9[r14]
            r10 = 2131624563(0x7f0e0273, float:1.887631E38)
            java.lang.String r15 = "AutodownloadPrivateChats"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r15, r10)
            int[] r15 = r7.mask
            r15 = r15[r1]
            r15 = r15 & r16
            if (r15 == 0) goto L_0x02e9
            r15 = 1
            goto L_0x02ea
        L_0x02e9:
            r15 = 0
        L_0x02ea:
            r0.setTextAndCheck(r10, r15, r1)
            goto L_0x032d
        L_0x02ee:
            r10 = 2
            if (r14 != r10) goto L_0x030c
            r0 = r9[r14]
            r1 = 2131624562(0x7f0e0272, float:1.8876307E38)
            java.lang.String r15 = "AutodownloadGroupChats"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r15, r1)
            int[] r15 = r7.mask
            r15 = r15[r10]
            r15 = r15 & r16
            r10 = 1
            if (r15 == 0) goto L_0x0307
            r15 = 1
            goto L_0x0308
        L_0x0307:
            r15 = 0
        L_0x0308:
            r0.setTextAndCheck(r1, r15, r10)
            goto L_0x032d
        L_0x030c:
            r0 = r9[r14]
            r1 = 2131624560(0x7f0e0270, float:1.8876303E38)
            java.lang.String r10 = "AutodownloadChannels"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r10, r1)
            int[] r10 = r7.mask
            r15 = 3
            r10 = r10[r15]
            r10 = r10 & r16
            if (r10 == 0) goto L_0x0322
            r10 = 1
            goto L_0x0323
        L_0x0322:
            r10 = 0
        L_0x0323:
            int r15 = r12.photosRow
            if (r8 == r15) goto L_0x0329
            r15 = 1
            goto L_0x032a
        L_0x0329:
            r15 = 0
        L_0x032a:
            r0.setTextAndCheck(r1, r10, r15)
        L_0x032d:
            r0 = r9[r14]
            r1 = 0
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r1)
            r0.setBackgroundDrawable(r10)
            r10 = r9[r14]
            org.telegram.ui.DataAutoDownloadActivity$$ExternalSyntheticLambda1 r15 = new org.telegram.ui.DataAutoDownloadActivity$$ExternalSyntheticLambda1
            r1 = r21
            r0 = r15
            r21 = r23
            r23 = r1
            r1 = r29
            r11 = -1
            r24 = r3
            r3 = r9
            r25 = r4
            r4 = r31
            r26 = r5
            r5 = r21
            r27 = r6
            r6 = r23
            r28 = r7
            r7 = r13
            r0.<init>(r1, r2, r3, r4, r5, r6, r7)
            r10.setOnClickListener(r15)
            r0 = r9[r14]
            r1 = 1112014848(0x42480000, float:50.0)
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r1)
            r7 = r24
            r7.addView(r0, r1)
            int r14 = r14 + 1
            r11 = r30
            r3 = r7
            r1 = r21
            r0 = r23
            r4 = r25
            r5 = r26
            r6 = r27
            r7 = r28
            r2 = -1
            r10 = 4
            r15 = 0
            goto L_0x029e
        L_0x0380:
            r23 = r0
            r21 = r1
            r25 = r4
            r26 = r5
            r27 = r6
            r28 = r7
            r11 = -1
            r7 = r3
            int r0 = r12.photosRow
            r10 = -2
            r14 = 0
            if (r8 == r0) goto L_0x0484
            org.telegram.ui.Cells.TextInfoPrivacyCell r15 = new org.telegram.ui.Cells.TextInfoPrivacyCell
            android.app.Activity r0 = r29.getParentActivity()
            r15.<init>(r0)
            org.telegram.ui.DataAutoDownloadActivity$3 r18 = new org.telegram.ui.DataAutoDownloadActivity$3
            android.app.Activity r2 = r29.getParentActivity()
            r0 = r18
            r1 = r29
            r3 = r31
            r4 = r15
            r5 = r23
            r6 = r13
            r0.<init>(r2, r3, r4, r5, r6)
            r0 = 0
            r21[r0] = r18
            r1 = r21[r0]
            r2 = r28
            int[] r3 = r2.sizes
            r3 = r3[r17]
            long r3 = (long) r3
            r1.setSize(r3)
            r1 = r21[r0]
            r3 = 50
            android.widget.LinearLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r3)
            r7.addView(r1, r3)
            org.telegram.ui.Cells.TextCheckCell r1 = new org.telegram.ui.Cells.TextCheckCell
            android.app.Activity r3 = r29.getParentActivity()
            r4 = 21
            r5 = 1
            r1.<init>(r3, r4, r5)
            r6 = r23
            r6[r0] = r1
            r1 = r6[r0]
            r3 = 48
            android.widget.LinearLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r3)
            r7.addView(r1, r3)
            r1 = r6[r0]
            org.telegram.ui.DataAutoDownloadActivity$$ExternalSyntheticLambda3 r0 = new org.telegram.ui.DataAutoDownloadActivity$$ExternalSyntheticLambda3
            r0.<init>(r6)
            r1.setOnClickListener(r0)
            android.app.Activity r0 = r29.getParentActivity()
            r1 = 2131165483(0x7var_b, float:1.7945184E38)
            java.lang.String r3 = "windowBackgroundGrayShadow"
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r0, (int) r1, (java.lang.String) r3)
            org.telegram.ui.Components.CombinedDrawable r1 = new org.telegram.ui.Components.CombinedDrawable
            android.graphics.drawable.ColorDrawable r3 = new android.graphics.drawable.ColorDrawable
            java.lang.String r4 = "windowBackgroundGray"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            r3.<init>(r4)
            r1.<init>(r3, r0)
            r0 = 1
            r1.setFullsize(r0)
            r15.setBackgroundDrawable(r1)
            android.widget.LinearLayout$LayoutParams r0 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r10)
            r7.addView(r15, r0)
            int r0 = r12.videosRow
            if (r8 != r0) goto L_0x0458
            r0 = 0
            r1 = r21[r0]
            r3 = 2131624514(0x7f0e0242, float:1.887621E38)
            java.lang.String r4 = "AutoDownloadMaxVideoSize"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r1.setText(r3)
            r1 = r6[r0]
            r3 = 2131624533(0x7f0e0255, float:1.8876248E38)
            java.lang.String r4 = "AutoDownloadPreloadVideo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            boolean r4 = r2.preloadVideo
            r1.setTextAndCheck(r3, r4, r0)
            r1 = 2131624534(0x7f0e0256, float:1.887625E38)
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]
            int[] r3 = r2.sizes
            r3 = r3[r17]
            long r10 = (long) r3
            java.lang.String r3 = org.telegram.messenger.AndroidUtilities.formatFileSize(r10)
            r4[r0] = r3
            java.lang.String r3 = "AutoDownloadPreloadVideoInfo"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r3, r1, r4)
            r15.setText(r1)
            goto L_0x04a9
        L_0x0458:
            r0 = 0
            r1 = r21[r0]
            r3 = 2131624513(0x7f0e0241, float:1.8876208E38)
            java.lang.String r4 = "AutoDownloadMaxFileSize"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r1.setText(r3)
            r1 = r6[r0]
            r3 = 2131624531(0x7f0e0253, float:1.8876244E38)
            java.lang.String r4 = "AutoDownloadPreloadMusic"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            boolean r4 = r2.preloadMusic
            r1.setTextAndCheck(r3, r4, r0)
            r1 = 2131624532(0x7f0e0254, float:1.8876246E38)
            java.lang.String r3 = "AutoDownloadPreloadMusicInfo"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
            r15.setText(r1)
            goto L_0x04a9
        L_0x0484:
            r6 = r23
            r2 = r28
            r0 = 0
            r21[r0] = r14
            r6[r0] = r14
            android.view.View r0 = new android.view.View
            android.app.Activity r1 = r29.getParentActivity()
            r0.<init>(r1)
            java.lang.String r1 = "divider"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setBackgroundColor(r1)
            android.widget.LinearLayout$LayoutParams r1 = new android.widget.LinearLayout$LayoutParams
            r3 = -1
            r4 = 1
            r1.<init>(r3, r4)
            r7.addView(r0, r1)
        L_0x04a9:
            int r0 = r12.videosRow
            if (r8 != r0) goto L_0x04db
            r0 = 0
            r1 = 4
        L_0x04af:
            if (r0 >= r1) goto L_0x04be
            r3 = r9[r0]
            boolean r3 = r3.isChecked()
            if (r3 == 0) goto L_0x04bb
            r0 = 1
            goto L_0x04bf
        L_0x04bb:
            int r0 = r0 + 1
            goto L_0x04af
        L_0x04be:
            r0 = 0
        L_0x04bf:
            if (r0 != 0) goto L_0x04cd
            r0 = 0
            r1 = r21[r0]
            r1.setEnabled(r0, r14)
            r1 = r6[r0]
            r1.setEnabled(r0, r14)
            goto L_0x04ce
        L_0x04cd:
            r0 = 0
        L_0x04ce:
            int[] r1 = r2.sizes
            r1 = r1[r17]
            r2 = 2097152(0x200000, float:2.938736E-39)
            if (r1 > r2) goto L_0x04db
            r1 = r6[r0]
            r1.setEnabled(r0, r14)
        L_0x04db:
            android.widget.FrameLayout r0 = new android.widget.FrameLayout
            android.app.Activity r1 = r29.getParentActivity()
            r0.<init>(r1)
            r1 = 1090519040(0x41000000, float:8.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r0.setPadding(r2, r3, r4, r1)
            r1 = 52
            r2 = -1
            android.widget.LinearLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createLinear(r2, r1)
            r7.addView(r0, r1)
            android.widget.TextView r1 = new android.widget.TextView
            android.app.Activity r2 = r29.getParentActivity()
            r1.<init>(r2)
            r2 = 1096810496(0x41600000, float:14.0)
            r3 = 1
            r1.setTextSize(r3, r2)
            java.lang.String r3 = "dialogTextBlue2"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r1.setTextColor(r4)
            r4 = 17
            r1.setGravity(r4)
            java.lang.String r5 = "fonts/rmedium.ttf"
            android.graphics.Typeface r7 = org.telegram.messenger.AndroidUtilities.getTypeface(r5)
            r1.setTypeface(r7)
            r7 = 2131624750(0x7f0e032e, float:1.8876689E38)
            java.lang.String r10 = "Cancel"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r10, r7)
            java.lang.String r7 = r7.toUpperCase()
            r1.setText(r7)
            r7 = 1092616192(0x41200000, float:10.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r13 = 0
            r1.setPadding(r10, r13, r11, r13)
            r10 = 51
            r11 = 36
            r13 = -2
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r13, (int) r11, (int) r10)
            r0.addView(r1, r10)
            org.telegram.ui.DataAutoDownloadActivity$$ExternalSyntheticLambda0 r10 = new org.telegram.ui.DataAutoDownloadActivity$$ExternalSyntheticLambda0
            r13 = r25
            r10.<init>(r13)
            r1.setOnClickListener(r10)
            android.widget.TextView r14 = new android.widget.TextView
            android.app.Activity r1 = r29.getParentActivity()
            r14.<init>(r1)
            r1 = 1
            r14.setTextSize(r1, r2)
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r14.setTextColor(r1)
            r14.setGravity(r4)
            android.graphics.Typeface r1 = org.telegram.messenger.AndroidUtilities.getTypeface(r5)
            r14.setTypeface(r1)
            r1 = 2131627844(0x7f0e0var_, float:1.8882964E38)
            java.lang.String r2 = "Save"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.String r1 = r1.toUpperCase()
            r14.setText(r1)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r3 = 0
            r14.setPadding(r1, r3, r2, r3)
            r1 = 53
            r2 = -2
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r2, (int) r11, (int) r1)
            r0.addView(r14, r1)
            org.telegram.ui.DataAutoDownloadActivity$$ExternalSyntheticLambda2 r15 = new org.telegram.ui.DataAutoDownloadActivity$$ExternalSyntheticLambda2
            r0 = r15
            r1 = r29
            r2 = r9
            r3 = r16
            r4 = r21
            r5 = r17
            r7 = r31
            r8 = r27
            r9 = r26
            r10 = r13
            r11 = r30
            r0.<init>(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)
            r14.setOnClickListener(r15)
            org.telegram.ui.ActionBar.BottomSheet r0 = r13.create()
            r12.showDialog(r0)
        L_0x05c3:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DataAutoDownloadActivity.lambda$createView$4(android.view.View, int, float, float):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$0(TextCheckBoxCell textCheckBoxCell, TextCheckBoxCell[] textCheckBoxCellArr, int i, MaxFileSizeCell[] maxFileSizeCellArr, TextCheckCell[] textCheckCellArr, final AnimatorSet[] animatorSetArr, View view) {
        if (view.isEnabled()) {
            boolean z = true;
            textCheckBoxCell.setChecked(!textCheckBoxCell.isChecked());
            int i2 = 0;
            while (true) {
                if (i2 >= textCheckBoxCellArr.length) {
                    z = false;
                    break;
                } else if (textCheckBoxCellArr[i2].isChecked()) {
                    break;
                } else {
                    i2++;
                }
            }
            if (i == this.videosRow && maxFileSizeCellArr[0].isEnabled() != z) {
                ArrayList arrayList = new ArrayList();
                maxFileSizeCellArr[0].setEnabled(z, arrayList);
                if (maxFileSizeCellArr[0].getSize() > 2097152) {
                    textCheckCellArr[0].setEnabled(z, arrayList);
                }
                if (animatorSetArr[0] != null) {
                    animatorSetArr[0].cancel();
                    animatorSetArr[0] = null;
                }
                animatorSetArr[0] = new AnimatorSet();
                animatorSetArr[0].playTogether(arrayList);
                animatorSetArr[0].addListener(new AnimatorListenerAdapter(this) {
                    public void onAnimationEnd(Animator animator) {
                        if (animator.equals(animatorSetArr[0])) {
                            animatorSetArr[0] = null;
                        }
                    }
                });
                animatorSetArr[0].setDuration(150);
                animatorSetArr[0].start();
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$3(TextCheckBoxCell[] textCheckBoxCellArr, int i, MaxFileSizeCell[] maxFileSizeCellArr, int i2, TextCheckCell[] textCheckCellArr, int i3, String str, String str2, BottomSheet.Builder builder, View view, View view2) {
        int i4 = i3;
        int i5 = this.currentPresetNum;
        if (i5 != 3) {
            if (i5 == 0) {
                this.typePreset.set(this.lowPreset);
            } else if (i5 == 1) {
                this.typePreset.set(this.mediumPreset);
            } else if (i5 == 2) {
                this.typePreset.set(this.highPreset);
            }
        }
        for (int i6 = 0; i6 < 4; i6++) {
            if (textCheckBoxCellArr[i6].isChecked()) {
                int[] iArr = this.typePreset.mask;
                iArr[i6] = iArr[i6] | i;
            } else {
                int[] iArr2 = this.typePreset.mask;
                iArr2[i6] = iArr2[i6] & (i ^ -1);
            }
        }
        if (maxFileSizeCellArr[0] != null) {
            maxFileSizeCellArr[0].getSize();
            this.typePreset.sizes[i2] = (int) maxFileSizeCellArr[0].getSize();
        }
        if (textCheckCellArr[0] != null) {
            if (i4 == this.videosRow) {
                this.typePreset.preloadVideo = textCheckCellArr[0].isChecked();
            } else {
                this.typePreset.preloadMusic = textCheckCellArr[0].isChecked();
            }
        }
        SharedPreferences.Editor edit = MessagesController.getMainSettings(this.currentAccount).edit();
        edit.putString(str, this.typePreset.toString());
        this.currentPresetNum = 3;
        edit.putInt(str2, 3);
        int i7 = this.currentType;
        if (i7 == 0) {
            DownloadController.getInstance(this.currentAccount).currentMobilePreset = this.currentPresetNum;
        } else if (i7 == 1) {
            DownloadController.getInstance(this.currentAccount).currentWifiPreset = this.currentPresetNum;
        } else {
            DownloadController.getInstance(this.currentAccount).currentRoamingPreset = this.currentPresetNum;
        }
        edit.commit();
        builder.getDismissRunnable().run();
        RecyclerView.ViewHolder findContainingViewHolder = this.listView.findContainingViewHolder(view);
        if (findContainingViewHolder != null) {
            this.animateChecked = true;
            this.listAdapter.onBindViewHolder(findContainingViewHolder, i3);
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
            RecyclerView.ViewHolder findViewHolderForAdapterPosition = recyclerListView.findViewHolderForAdapterPosition(this.usageProgressRow);
            if (findViewHolderForAdapterPosition != null) {
                View view = findViewHolderForAdapterPosition.itemView;
                if (view instanceof SlideChooseView) {
                    updatePresetChoseView((SlideChooseView) view);
                    return;
                }
            }
            this.listAdapter.notifyItemChanged(this.usageProgressRow);
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ int lambda$fillPresets$5(DownloadController.Preset preset, DownloadController.Preset preset2) {
        int typeToIndex = DownloadController.typeToIndex(4);
        int typeToIndex2 = DownloadController.typeToIndex(8);
        int i = 0;
        boolean z = false;
        boolean z2 = false;
        while (true) {
            int[] iArr = preset.mask;
            if (i < iArr.length) {
                if ((iArr[i] & 4) != 0) {
                    z = true;
                }
                if ((iArr[i] & 8) != 0) {
                    z2 = true;
                }
                if (z && z2) {
                    break;
                }
                i++;
            } else {
                break;
            }
        }
        int i2 = 0;
        boolean z3 = false;
        boolean z4 = false;
        while (true) {
            int[] iArr2 = preset2.mask;
            if (i2 < iArr2.length) {
                if ((iArr2[i2] & 4) != 0) {
                    z3 = true;
                }
                if ((iArr2[i2] & 8) != 0) {
                    z4 = true;
                }
                if (z3 && z4) {
                    break;
                }
                i2++;
            } else {
                break;
            }
        }
        int i3 = (z ? preset.sizes[typeToIndex] : 0) + (z2 ? preset.sizes[typeToIndex2] : 0);
        int i4 = (z3 ? preset2.sizes[typeToIndex] : 0) + (z4 ? preset2.sizes[typeToIndex2] : 0);
        if (i3 > i4) {
            return 1;
        }
        if (i3 < i4) {
            return -1;
        }
        return 0;
    }

    private void updateRows() {
        this.rowCount = 0;
        int i = 0 + 1;
        this.rowCount = i;
        this.autoDownloadRow = 0;
        int i2 = i + 1;
        this.rowCount = i2;
        this.autoDownloadSectionRow = i;
        if (this.typePreset.enabled) {
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

        /* JADX WARNING: Removed duplicated region for block: B:78:0x0231  */
        /* JADX WARNING: Removed duplicated region for block: B:83:0x023b  */
        /* JADX WARNING: Removed duplicated region for block: B:84:0x023d  */
        /* JADX WARNING: Removed duplicated region for block: B:87:0x0248  */
        /* JADX WARNING: Removed duplicated region for block: B:88:0x024a  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r17, int r18) {
            /*
                r16 = this;
                r0 = r16
                r1 = r17
                r2 = r18
                int r3 = r17.getItemViewType()
                r4 = 0
                r5 = 1
                if (r3 == 0) goto L_0x0289
                r6 = 2
                if (r3 == r6) goto L_0x025b
                r7 = 3
                if (r3 == r7) goto L_0x0250
                r8 = 4
                if (r3 == r8) goto L_0x00cc
                r7 = 5
                if (r3 == r7) goto L_0x001c
                goto L_0x02d5
            L_0x001c:
                android.view.View r1 = r1.itemView
                org.telegram.ui.Cells.TextInfoPrivacyCell r1 = (org.telegram.ui.Cells.TextInfoPrivacyCell) r1
                org.telegram.ui.DataAutoDownloadActivity r3 = org.telegram.ui.DataAutoDownloadActivity.this
                int r3 = r3.typeSectionRow
                r7 = 2131165483(0x7var_b, float:1.7945184E38)
                java.lang.String r9 = "windowBackgroundGrayShadow"
                if (r2 != r3) goto L_0x004a
                r2 = 2131624502(0x7f0e0236, float:1.8876186E38)
                java.lang.String r3 = "AutoDownloadAudioInfo"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r1.setText(r2)
                android.content.Context r2 = r0.mContext
                android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r2, (int) r7, (java.lang.String) r9)
                r1.setBackgroundDrawable(r2)
                r1.setFixedSize(r4)
                r1.setImportantForAccessibility(r5)
                goto L_0x02d5
            L_0x004a:
                org.telegram.ui.DataAutoDownloadActivity r3 = org.telegram.ui.DataAutoDownloadActivity.this
                int r3 = r3.autoDownloadSectionRow
                if (r2 != r3) goto L_0x02d5
                org.telegram.ui.DataAutoDownloadActivity r2 = org.telegram.ui.DataAutoDownloadActivity.this
                int r2 = r2.usageHeaderRow
                r3 = -1
                if (r2 != r3) goto L_0x00aa
                android.content.Context r2 = r0.mContext
                r3 = 2131165484(0x7var_c, float:1.7945186E38)
                android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r2, (int) r3, (java.lang.String) r9)
                r1.setBackgroundDrawable(r2)
                org.telegram.ui.DataAutoDownloadActivity r2 = org.telegram.ui.DataAutoDownloadActivity.this
                int r2 = r2.currentType
                if (r2 != 0) goto L_0x007c
                r2 = 2131624521(0x7f0e0249, float:1.8876224E38)
                java.lang.String r3 = "AutoDownloadOnMobileDataInfo"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r1.setText(r2)
                goto L_0x00a5
            L_0x007c:
                org.telegram.ui.DataAutoDownloadActivity r2 = org.telegram.ui.DataAutoDownloadActivity.this
                int r2 = r2.currentType
                if (r2 != r5) goto L_0x0091
                r2 = 2131624526(0x7f0e024e, float:1.8876234E38)
                java.lang.String r3 = "AutoDownloadOnWiFiDataInfo"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r1.setText(r2)
                goto L_0x00a5
            L_0x0091:
                org.telegram.ui.DataAutoDownloadActivity r2 = org.telegram.ui.DataAutoDownloadActivity.this
                int r2 = r2.currentType
                if (r2 != r6) goto L_0x00a5
                r2 = 2131624523(0x7f0e024b, float:1.8876228E38)
                java.lang.String r3 = "AutoDownloadOnRoamingDataInfo"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r1.setText(r2)
            L_0x00a5:
                r1.setImportantForAccessibility(r5)
                goto L_0x02d5
            L_0x00aa:
                android.content.Context r2 = r0.mContext
                android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r2, (int) r7, (java.lang.String) r9)
                r1.setBackgroundDrawable(r2)
                r2 = 0
                r1.setText(r2)
                r2 = 12
                r1.setFixedSize(r2)
                int r2 = android.os.Build.VERSION.SDK_INT
                r3 = 19
                if (r2 < r3) goto L_0x00c7
                r1.setImportantForAccessibility(r8)
                goto L_0x02d5
            L_0x00c7:
                r1.setImportantForAccessibility(r6)
                goto L_0x02d5
            L_0x00cc:
                android.view.View r1 = r1.itemView
                r9 = r1
                org.telegram.ui.Cells.NotificationsCheckCell r9 = (org.telegram.ui.Cells.NotificationsCheckCell) r9
                org.telegram.ui.DataAutoDownloadActivity r1 = org.telegram.ui.DataAutoDownloadActivity.this
                int r1 = r1.photosRow
                if (r2 != r1) goto L_0x00e5
                r1 = 2131624527(0x7f0e024f, float:1.8876236E38)
                java.lang.String r3 = "AutoDownloadPhotos"
                java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
                r10 = r1
                r3 = 1
                goto L_0x0105
            L_0x00e5:
                org.telegram.ui.DataAutoDownloadActivity r1 = org.telegram.ui.DataAutoDownloadActivity.this
                int r1 = r1.videosRow
                if (r2 != r1) goto L_0x00f9
                r1 = 2131624537(0x7f0e0259, float:1.8876257E38)
                java.lang.String r3 = "AutoDownloadVideos"
                java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
                r10 = r1
                r3 = 4
                goto L_0x0105
            L_0x00f9:
                r1 = 2131624507(0x7f0e023b, float:1.8876196E38)
                java.lang.String r3 = "AutoDownloadFiles"
                java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
                r3 = 8
                r10 = r1
            L_0x0105:
                org.telegram.ui.DataAutoDownloadActivity r1 = org.telegram.ui.DataAutoDownloadActivity.this
                int r1 = r1.currentType
                if (r1 != 0) goto L_0x011c
                org.telegram.ui.DataAutoDownloadActivity r1 = org.telegram.ui.DataAutoDownloadActivity.this
                int r1 = r1.currentAccount
                org.telegram.messenger.DownloadController r1 = org.telegram.messenger.DownloadController.getInstance(r1)
                org.telegram.messenger.DownloadController$Preset r1 = r1.getCurrentMobilePreset()
                goto L_0x0141
            L_0x011c:
                org.telegram.ui.DataAutoDownloadActivity r1 = org.telegram.ui.DataAutoDownloadActivity.this
                int r1 = r1.currentType
                if (r1 != r5) goto L_0x0133
                org.telegram.ui.DataAutoDownloadActivity r1 = org.telegram.ui.DataAutoDownloadActivity.this
                int r1 = r1.currentAccount
                org.telegram.messenger.DownloadController r1 = org.telegram.messenger.DownloadController.getInstance(r1)
                org.telegram.messenger.DownloadController$Preset r1 = r1.getCurrentWiFiPreset()
                goto L_0x0141
            L_0x0133:
                org.telegram.ui.DataAutoDownloadActivity r1 = org.telegram.ui.DataAutoDownloadActivity.this
                int r1 = r1.currentAccount
                org.telegram.messenger.DownloadController r1 = org.telegram.messenger.DownloadController.getInstance(r1)
                org.telegram.messenger.DownloadController$Preset r1 = r1.getCurrentRoamingPreset()
            L_0x0141:
                int[] r11 = r1.sizes
                int r12 = org.telegram.messenger.DownloadController.typeToIndex(r3)
                r11 = r11[r12]
                java.lang.StringBuilder r12 = new java.lang.StringBuilder
                r12.<init>()
                r13 = 0
                r14 = 0
            L_0x0150:
                int[] r15 = r1.mask
                int r4 = r15.length
                if (r13 >= r4) goto L_0x01a7
                r4 = r15[r13]
                r4 = r4 & r3
                if (r4 == 0) goto L_0x01a3
                int r4 = r12.length()
                if (r4 == 0) goto L_0x0165
                java.lang.String r4 = ", "
                r12.append(r4)
            L_0x0165:
                if (r13 == 0) goto L_0x0195
                if (r13 == r5) goto L_0x0188
                if (r13 == r6) goto L_0x017b
                if (r13 == r7) goto L_0x016e
                goto L_0x01a1
            L_0x016e:
                r4 = 2131624503(0x7f0e0237, float:1.8876188E38)
                java.lang.String r15 = "AutoDownloadChannels"
                java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r15, r4)
                r12.append(r4)
                goto L_0x01a1
            L_0x017b:
                r4 = 2131624510(0x7f0e023e, float:1.8876202E38)
                java.lang.String r15 = "AutoDownloadGroups"
                java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r15, r4)
                r12.append(r4)
                goto L_0x01a1
            L_0x0188:
                r4 = 2131624530(0x7f0e0252, float:1.8876242E38)
                java.lang.String r15 = "AutoDownloadPm"
                java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r15, r4)
                r12.append(r4)
                goto L_0x01a1
            L_0x0195:
                r4 = 2131624504(0x7f0e0238, float:1.887619E38)
                java.lang.String r15 = "AutoDownloadContacts"
                java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r15, r4)
                r12.append(r4)
            L_0x01a1:
                int r14 = r14 + 1
            L_0x01a3:
                int r13 = r13 + 1
                r4 = 0
                goto L_0x0150
            L_0x01a7:
                if (r14 != r8) goto L_0x01d9
                r1 = 0
                r12.setLength(r1)
                org.telegram.ui.DataAutoDownloadActivity r1 = org.telegram.ui.DataAutoDownloadActivity.this
                int r1 = r1.photosRow
                if (r2 != r1) goto L_0x01c2
                r1 = 2131624518(0x7f0e0246, float:1.8876218E38)
                java.lang.String r3 = "AutoDownloadOnAllChats"
                java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
                r12.append(r1)
                goto L_0x01e7
            L_0x01c2:
                r1 = 2131624536(0x7f0e0258, float:1.8876254E38)
                java.lang.Object[] r3 = new java.lang.Object[r5]
                long r6 = (long) r11
                java.lang.String r4 = org.telegram.messenger.AndroidUtilities.formatFileSize(r6)
                r6 = 0
                r3[r6] = r4
                java.lang.String r4 = "AutoDownloadUpToOnAllChats"
                java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r4, r1, r3)
                r12.append(r1)
                goto L_0x01e7
            L_0x01d9:
                if (r14 != 0) goto L_0x01e9
                r1 = 2131624517(0x7f0e0245, float:1.8876216E38)
                java.lang.String r3 = "AutoDownloadOff"
                java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
                r12.append(r1)
            L_0x01e7:
                r11 = r12
                goto L_0x0229
            L_0x01e9:
                org.telegram.ui.DataAutoDownloadActivity r1 = org.telegram.ui.DataAutoDownloadActivity.this
                int r1 = r1.photosRow
                if (r2 != r1) goto L_0x0209
                java.lang.StringBuilder r1 = new java.lang.StringBuilder
                r3 = 2131624519(0x7f0e0247, float:1.887622E38)
                java.lang.Object[] r4 = new java.lang.Object[r5]
                java.lang.String r6 = r12.toString()
                r7 = 0
                r4[r7] = r6
                java.lang.String r6 = "AutoDownloadOnFor"
                java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
                r1.<init>(r3)
                goto L_0x0228
            L_0x0209:
                r7 = 0
                java.lang.StringBuilder r1 = new java.lang.StringBuilder
                r3 = 2131624524(0x7f0e024c, float:1.887623E38)
                java.lang.Object[] r4 = new java.lang.Object[r6]
                long r5 = (long) r11
                java.lang.String r5 = org.telegram.messenger.AndroidUtilities.formatFileSize(r5)
                r4[r7] = r5
                java.lang.String r5 = r12.toString()
                r6 = 1
                r4[r6] = r5
                java.lang.String r5 = "AutoDownloadOnUpToFor"
                java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r5, r3, r4)
                r1.<init>(r3)
            L_0x0228:
                r11 = r1
            L_0x0229:
                org.telegram.ui.DataAutoDownloadActivity r1 = org.telegram.ui.DataAutoDownloadActivity.this
                boolean r1 = r1.animateChecked
                if (r1 == 0) goto L_0x0239
                if (r14 == 0) goto L_0x0235
                r1 = 1
                goto L_0x0236
            L_0x0235:
                r1 = 0
            L_0x0236:
                r9.setChecked(r1)
            L_0x0239:
                if (r14 == 0) goto L_0x023d
                r12 = 1
                goto L_0x023e
            L_0x023d:
                r12 = 0
            L_0x023e:
                r13 = 0
                r14 = 1
                org.telegram.ui.DataAutoDownloadActivity r1 = org.telegram.ui.DataAutoDownloadActivity.this
                int r1 = r1.filesRow
                if (r2 == r1) goto L_0x024a
                r15 = 1
                goto L_0x024b
            L_0x024a:
                r15 = 0
            L_0x024b:
                r9.setTextAndValueAndCheck(r10, r11, r12, r13, r14, r15)
                goto L_0x02d5
            L_0x0250:
                android.view.View r1 = r1.itemView
                org.telegram.ui.Components.SlideChooseView r1 = (org.telegram.ui.Components.SlideChooseView) r1
                org.telegram.ui.DataAutoDownloadActivity r2 = org.telegram.ui.DataAutoDownloadActivity.this
                r2.updatePresetChoseView(r1)
                goto L_0x02d5
            L_0x025b:
                android.view.View r1 = r1.itemView
                org.telegram.ui.Cells.HeaderCell r1 = (org.telegram.ui.Cells.HeaderCell) r1
                org.telegram.ui.DataAutoDownloadActivity r3 = org.telegram.ui.DataAutoDownloadActivity.this
                int r3 = r3.usageHeaderRow
                if (r2 != r3) goto L_0x0274
                r2 = 2131624506(0x7f0e023a, float:1.8876194E38)
                java.lang.String r3 = "AutoDownloadDataUsage"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r1.setText(r2)
                goto L_0x02d5
            L_0x0274:
                org.telegram.ui.DataAutoDownloadActivity r3 = org.telegram.ui.DataAutoDownloadActivity.this
                int r3 = r3.typeHeaderRow
                if (r2 != r3) goto L_0x02d5
                r2 = 2131624535(0x7f0e0257, float:1.8876252E38)
                java.lang.String r3 = "AutoDownloadTypes"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                r1.setText(r2)
                goto L_0x02d5
            L_0x0289:
                android.view.View r1 = r1.itemView
                org.telegram.ui.Cells.TextCheckCell r1 = (org.telegram.ui.Cells.TextCheckCell) r1
                org.telegram.ui.DataAutoDownloadActivity r3 = org.telegram.ui.DataAutoDownloadActivity.this
                int r3 = r3.autoDownloadRow
                if (r2 != r3) goto L_0x02d5
                r2 = 1
                r1.setDrawCheckRipple(r2)
                r2 = 2131624515(0x7f0e0243, float:1.8876212E38)
                java.lang.String r3 = "AutoDownloadMedia"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                org.telegram.ui.DataAutoDownloadActivity r3 = org.telegram.ui.DataAutoDownloadActivity.this
                org.telegram.messenger.DownloadController$Preset r3 = r3.typePreset
                boolean r3 = r3.enabled
                r4 = 0
                r1.setTextAndCheck(r2, r3, r4)
                org.telegram.ui.DataAutoDownloadActivity r2 = org.telegram.ui.DataAutoDownloadActivity.this
                org.telegram.messenger.DownloadController$Preset r2 = r2.typePreset
                boolean r2 = r2.enabled
                java.lang.String r3 = "windowBackgroundChecked"
                java.lang.String r4 = "windowBackgroundUnchecked"
                if (r2 == 0) goto L_0x02be
                r2 = r3
                goto L_0x02bf
            L_0x02be:
                r2 = r4
            L_0x02bf:
                r1.setTag(r2)
                org.telegram.ui.DataAutoDownloadActivity r2 = org.telegram.ui.DataAutoDownloadActivity.this
                org.telegram.messenger.DownloadController$Preset r2 = r2.typePreset
                boolean r2 = r2.enabled
                if (r2 == 0) goto L_0x02cd
                goto L_0x02ce
            L_0x02cd:
                r3 = r4
            L_0x02ce:
                int r2 = org.telegram.ui.ActionBar.Theme.getColor(r3)
                r1.setBackgroundColor(r2)
            L_0x02d5:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DataAutoDownloadActivity.ListAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            int adapterPosition = viewHolder.getAdapterPosition();
            return adapterPosition == DataAutoDownloadActivity.this.photosRow || adapterPosition == DataAutoDownloadActivity.this.videosRow || adapterPosition == DataAutoDownloadActivity.this.filesRow;
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onCreateViewHolder$0(int i) {
            DownloadController.Preset preset = (DownloadController.Preset) DataAutoDownloadActivity.this.presets.get(i);
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
            SharedPreferences.Editor edit = MessagesController.getMainSettings(DataAutoDownloadActivity.this.currentAccount).edit();
            edit.putInt(DataAutoDownloadActivity.this.key2, DataAutoDownloadActivity.this.currentPresetNum);
            edit.commit();
            DownloadController.getInstance(DataAutoDownloadActivity.this.currentAccount).checkAutodownloadSettings();
            for (int i2 = 0; i2 < 3; i2++) {
                RecyclerView.ViewHolder findViewHolderForAdapterPosition = DataAutoDownloadActivity.this.listView.findViewHolderForAdapterPosition(DataAutoDownloadActivity.this.photosRow + i2);
                if (findViewHolderForAdapterPosition != null) {
                    DataAutoDownloadActivity.this.listAdapter.onBindViewHolder(findViewHolderForAdapterPosition, DataAutoDownloadActivity.this.photosRow + i2);
                }
            }
            boolean unused5 = DataAutoDownloadActivity.this.wereAnyChanges = true;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            TextInfoPrivacyCell textInfoPrivacyCell;
            if (i == 0) {
                TextCheckCell textCheckCell = new TextCheckCell(this.mContext);
                textCheckCell.setColors("windowBackgroundCheckText", "switchTrackBlue", "switchTrackBlueChecked", "switchTrackBlueThumb", "switchTrackBlueThumbChecked");
                textCheckCell.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                textCheckCell.setHeight(56);
                textInfoPrivacyCell = textCheckCell;
            } else if (i == 1) {
                textInfoPrivacyCell = new ShadowSectionCell(this.mContext);
            } else if (i == 2) {
                HeaderCell headerCell = new HeaderCell(this.mContext);
                headerCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                textInfoPrivacyCell = headerCell;
            } else if (i == 3) {
                SlideChooseView slideChooseView = new SlideChooseView(this.mContext);
                slideChooseView.setCallback(new DataAutoDownloadActivity$ListAdapter$$ExternalSyntheticLambda0(this));
                slideChooseView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                textInfoPrivacyCell = slideChooseView;
            } else if (i != 4) {
                TextInfoPrivacyCell textInfoPrivacyCell2 = new TextInfoPrivacyCell(this.mContext);
                textInfoPrivacyCell2.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                textInfoPrivacyCell = textInfoPrivacyCell2;
            } else {
                NotificationsCheckCell notificationsCheckCell = new NotificationsCheckCell(this.mContext);
                notificationsCheckCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                textInfoPrivacyCell = notificationsCheckCell;
            }
            textInfoPrivacyCell.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(textInfoPrivacyCell);
        }

        public int getItemViewType(int i) {
            if (i == DataAutoDownloadActivity.this.autoDownloadRow) {
                return 0;
            }
            if (i == DataAutoDownloadActivity.this.usageSectionRow) {
                return 1;
            }
            if (i == DataAutoDownloadActivity.this.usageHeaderRow || i == DataAutoDownloadActivity.this.typeHeaderRow) {
                return 2;
            }
            if (i == DataAutoDownloadActivity.this.usageProgressRow) {
                return 3;
            }
            return (i == DataAutoDownloadActivity.this.photosRow || i == DataAutoDownloadActivity.this.videosRow || i == DataAutoDownloadActivity.this.filesRow) ? 4 : 5;
        }
    }

    /* access modifiers changed from: private */
    public void updatePresetChoseView(SlideChooseView slideChooseView) {
        String[] strArr = new String[this.presets.size()];
        for (int i = 0; i < this.presets.size(); i++) {
            DownloadController.Preset preset = this.presets.get(i);
            if (preset == this.lowPreset) {
                strArr[i] = LocaleController.getString("AutoDownloadLow", NUM);
            } else if (preset == this.mediumPreset) {
                strArr[i] = LocaleController.getString("AutoDownloadMedium", NUM);
            } else if (preset == this.highPreset) {
                strArr[i] = LocaleController.getString("AutoDownloadHigh", NUM);
            } else {
                strArr[i] = LocaleController.getString("AutoDownloadCustom", NUM);
            }
        }
        slideChooseView.setOptions(this.selectedPreset, strArr);
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{HeaderCell.class, NotificationsCheckCell.class, SlideChooseView.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCheckCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundChecked"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCheckCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundUnchecked"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundCheckText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackBlue"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackBlueChecked"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackBlueThumb"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackBlueThumbChecked"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackBlueSelector"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackBlueSelectorChecked"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrack"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackChecked"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{SlideChooseView.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrack"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{SlideChooseView.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackChecked"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{SlideChooseView.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText"));
        return arrayList;
    }
}
