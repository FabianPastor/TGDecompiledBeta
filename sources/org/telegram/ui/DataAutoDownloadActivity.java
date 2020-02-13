package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
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
    /* access modifiers changed from: private */
    public int selectedPreset = 1;
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

    private class PresetChooseView extends View {
        private int circleSize;
        private String custom;
        private int customSize;
        private int gapSize;
        private String high;
        private int highSize;
        private int lineSize;
        private String low;
        private int lowSize;
        private String medium;
        private int mediumSize;
        private boolean moving;
        private Paint paint = new Paint(1);
        private int sideSide;
        private boolean startMoving;
        private int startMovingPreset;
        private float startX;
        private TextPaint textPaint = new TextPaint(1);

        public PresetChooseView(Context context) {
            super(context);
            this.textPaint.setTextSize((float) AndroidUtilities.dp(13.0f));
            this.low = LocaleController.getString("AutoDownloadLow", NUM);
            this.lowSize = (int) Math.ceil((double) this.textPaint.measureText(this.low));
            this.medium = LocaleController.getString("AutoDownloadMedium", NUM);
            this.mediumSize = (int) Math.ceil((double) this.textPaint.measureText(this.medium));
            this.high = LocaleController.getString("AutoDownloadHigh", NUM);
            this.highSize = (int) Math.ceil((double) this.textPaint.measureText(this.high));
            this.custom = LocaleController.getString("AutoDownloadCustom", NUM);
            this.customSize = (int) Math.ceil((double) this.textPaint.measureText(this.custom));
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v3, resolved type: boolean} */
        /* JADX WARNING: type inference failed for: r3v0 */
        /* JADX WARNING: type inference failed for: r3v1, types: [int] */
        /* JADX WARNING: type inference failed for: r3v4 */
        /* JADX WARNING: type inference failed for: r3v5 */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean onTouchEvent(android.view.MotionEvent r9) {
            /*
                r8 = this;
                float r0 = r9.getX()
                int r1 = r9.getAction()
                r2 = 1097859072(0x41700000, float:15.0)
                r3 = 0
                r4 = 1
                r5 = 2
                if (r1 != 0) goto L_0x0063
                android.view.ViewParent r9 = r8.getParent()
                r9.requestDisallowInterceptTouchEvent(r4)
                r9 = 0
            L_0x0017:
                org.telegram.ui.DataAutoDownloadActivity r1 = org.telegram.ui.DataAutoDownloadActivity.this
                java.util.ArrayList r1 = r1.presets
                int r1 = r1.size()
                if (r9 >= r1) goto L_0x0127
                int r1 = r8.sideSide
                int r6 = r8.lineSize
                int r7 = r8.gapSize
                int r7 = r7 * 2
                int r6 = r6 + r7
                int r7 = r8.circleSize
                int r6 = r6 + r7
                int r6 = r6 * r9
                int r1 = r1 + r6
                int r7 = r7 / r5
                int r1 = r1 + r7
                int r6 = org.telegram.messenger.AndroidUtilities.dp(r2)
                int r6 = r1 - r6
                float r6 = (float) r6
                int r6 = (r0 > r6 ? 1 : (r0 == r6 ? 0 : -1))
                if (r6 <= 0) goto L_0x0060
                int r6 = org.telegram.messenger.AndroidUtilities.dp(r2)
                int r1 = r1 + r6
                float r1 = (float) r1
                int r1 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
                if (r1 >= 0) goto L_0x0060
                org.telegram.ui.DataAutoDownloadActivity r1 = org.telegram.ui.DataAutoDownloadActivity.this
                int r1 = r1.selectedPreset
                if (r9 != r1) goto L_0x0052
                r3 = 1
            L_0x0052:
                r8.startMoving = r3
                r8.startX = r0
                org.telegram.ui.DataAutoDownloadActivity r9 = org.telegram.ui.DataAutoDownloadActivity.this
                int r9 = r9.selectedPreset
                r8.startMovingPreset = r9
                goto L_0x0127
            L_0x0060:
                int r9 = r9 + 1
                goto L_0x0017
            L_0x0063:
                int r1 = r9.getAction()
                if (r1 != r5) goto L_0x00c6
                boolean r9 = r8.startMoving
                if (r9 == 0) goto L_0x0084
                float r9 = r8.startX
                float r9 = r9 - r0
                float r9 = java.lang.Math.abs(r9)
                r0 = 1056964608(0x3var_, float:0.5)
                float r0 = org.telegram.messenger.AndroidUtilities.getPixelsInCM(r0, r4)
                int r9 = (r9 > r0 ? 1 : (r9 == r0 ? 0 : -1))
                if (r9 < 0) goto L_0x0127
                r8.moving = r4
                r8.startMoving = r3
                goto L_0x0127
            L_0x0084:
                boolean r9 = r8.moving
                if (r9 == 0) goto L_0x0127
            L_0x0088:
                org.telegram.ui.DataAutoDownloadActivity r9 = org.telegram.ui.DataAutoDownloadActivity.this
                java.util.ArrayList r9 = r9.presets
                int r9 = r9.size()
                if (r3 >= r9) goto L_0x0127
                int r9 = r8.sideSide
                int r1 = r8.lineSize
                int r2 = r8.gapSize
                int r6 = r2 * 2
                int r6 = r6 + r1
                int r7 = r8.circleSize
                int r6 = r6 + r7
                int r6 = r6 * r3
                int r9 = r9 + r6
                int r6 = r7 / 2
                int r9 = r9 + r6
                int r1 = r1 / r5
                int r7 = r7 / r5
                int r1 = r1 + r7
                int r1 = r1 + r2
                int r2 = r9 - r1
                float r2 = (float) r2
                int r2 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
                if (r2 <= 0) goto L_0x00c3
                int r9 = r9 + r1
                float r9 = (float) r9
                int r9 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
                if (r9 >= 0) goto L_0x00c3
                org.telegram.ui.DataAutoDownloadActivity r9 = org.telegram.ui.DataAutoDownloadActivity.this
                int r9 = r9.selectedPreset
                if (r9 == r3) goto L_0x0127
                r8.setPreset(r3)
                goto L_0x0127
            L_0x00c3:
                int r3 = r3 + 1
                goto L_0x0088
            L_0x00c6:
                int r1 = r9.getAction()
                if (r1 == r4) goto L_0x00d3
                int r9 = r9.getAction()
                r1 = 3
                if (r9 != r1) goto L_0x0127
            L_0x00d3:
                boolean r9 = r8.moving
                if (r9 != 0) goto L_0x0110
                r9 = 0
            L_0x00d8:
                r1 = 5
                if (r9 >= r1) goto L_0x0123
                int r1 = r8.sideSide
                int r6 = r8.lineSize
                int r7 = r8.gapSize
                int r7 = r7 * 2
                int r6 = r6 + r7
                int r7 = r8.circleSize
                int r6 = r6 + r7
                int r6 = r6 * r9
                int r1 = r1 + r6
                int r7 = r7 / r5
                int r1 = r1 + r7
                int r6 = org.telegram.messenger.AndroidUtilities.dp(r2)
                int r6 = r1 - r6
                float r6 = (float) r6
                int r6 = (r0 > r6 ? 1 : (r0 == r6 ? 0 : -1))
                if (r6 <= 0) goto L_0x010d
                int r6 = org.telegram.messenger.AndroidUtilities.dp(r2)
                int r1 = r1 + r6
                float r1 = (float) r1
                int r1 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
                if (r1 >= 0) goto L_0x010d
                org.telegram.ui.DataAutoDownloadActivity r0 = org.telegram.ui.DataAutoDownloadActivity.this
                int r0 = r0.selectedPreset
                if (r0 == r9) goto L_0x0123
                r8.setPreset(r9)
                goto L_0x0123
            L_0x010d:
                int r9 = r9 + 1
                goto L_0x00d8
            L_0x0110:
                org.telegram.ui.DataAutoDownloadActivity r9 = org.telegram.ui.DataAutoDownloadActivity.this
                int r9 = r9.selectedPreset
                int r0 = r8.startMovingPreset
                if (r9 == r0) goto L_0x0123
                org.telegram.ui.DataAutoDownloadActivity r9 = org.telegram.ui.DataAutoDownloadActivity.this
                int r9 = r9.selectedPreset
                r8.setPreset(r9)
            L_0x0123:
                r8.startMoving = r3
                r8.moving = r3
            L_0x0127:
                return r4
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DataAutoDownloadActivity.PresetChooseView.onTouchEvent(android.view.MotionEvent):boolean");
        }

        private void setPreset(int i) {
            int unused = DataAutoDownloadActivity.this.selectedPreset = i;
            DownloadController.Preset preset = (DownloadController.Preset) DataAutoDownloadActivity.this.presets.get(DataAutoDownloadActivity.this.selectedPreset);
            if (preset == DataAutoDownloadActivity.this.lowPreset) {
                int unused2 = DataAutoDownloadActivity.this.currentPresetNum = 0;
            } else if (preset == DataAutoDownloadActivity.this.mediumPreset) {
                int unused3 = DataAutoDownloadActivity.this.currentPresetNum = 1;
            } else if (preset == DataAutoDownloadActivity.this.highPreset) {
                int unused4 = DataAutoDownloadActivity.this.currentPresetNum = 2;
            } else {
                int unused5 = DataAutoDownloadActivity.this.currentPresetNum = 3;
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
            boolean unused6 = DataAutoDownloadActivity.this.wereAnyChanges = true;
            invalidate();
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(74.0f), NUM));
            View.MeasureSpec.getSize(i);
            this.circleSize = AndroidUtilities.dp(6.0f);
            this.gapSize = AndroidUtilities.dp(2.0f);
            this.sideSide = AndroidUtilities.dp(22.0f);
            this.lineSize = (((getMeasuredWidth() - (this.circleSize * DataAutoDownloadActivity.this.presets.size())) - ((this.gapSize * 2) * (DataAutoDownloadActivity.this.presets.size() - 1))) - (this.sideSide * 2)) / (DataAutoDownloadActivity.this.presets.size() - 1);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            int i;
            String str;
            this.textPaint.setColor(Theme.getColor("windowBackgroundWhiteGrayText"));
            int measuredHeight = (getMeasuredHeight() / 2) + AndroidUtilities.dp(11.0f);
            int i2 = 0;
            while (i2 < DataAutoDownloadActivity.this.presets.size()) {
                int i3 = this.sideSide;
                int i4 = this.lineSize + (this.gapSize * 2);
                int i5 = this.circleSize;
                int i6 = i3 + ((i4 + i5) * i2) + (i5 / 2);
                if (i2 <= DataAutoDownloadActivity.this.selectedPreset) {
                    this.paint.setColor(Theme.getColor("switchTrackChecked"));
                } else {
                    this.paint.setColor(Theme.getColor("switchTrack"));
                }
                canvas.drawCircle((float) i6, (float) measuredHeight, (float) (i2 == DataAutoDownloadActivity.this.selectedPreset ? AndroidUtilities.dp(6.0f) : this.circleSize / 2), this.paint);
                if (i2 != 0) {
                    int i7 = (i6 - (this.circleSize / 2)) - this.gapSize;
                    int i8 = this.lineSize;
                    int i9 = i7 - i8;
                    if (i2 == DataAutoDownloadActivity.this.selectedPreset || i2 == DataAutoDownloadActivity.this.selectedPreset + 1) {
                        i8 -= AndroidUtilities.dp(3.0f);
                    }
                    if (i2 == DataAutoDownloadActivity.this.selectedPreset + 1) {
                        i9 += AndroidUtilities.dp(3.0f);
                    }
                    canvas.drawRect((float) i9, (float) (measuredHeight - AndroidUtilities.dp(1.0f)), (float) (i9 + i8), (float) (AndroidUtilities.dp(1.0f) + measuredHeight), this.paint);
                }
                DownloadController.Preset preset = (DownloadController.Preset) DataAutoDownloadActivity.this.presets.get(i2);
                if (preset == DataAutoDownloadActivity.this.lowPreset) {
                    str = this.low;
                    i = this.lowSize;
                } else if (preset == DataAutoDownloadActivity.this.mediumPreset) {
                    str = this.medium;
                    i = this.mediumSize;
                } else if (preset == DataAutoDownloadActivity.this.highPreset) {
                    str = this.high;
                    i = this.highSize;
                } else {
                    str = this.custom;
                    i = this.customSize;
                }
                if (i2 == 0) {
                    canvas.drawText(str, (float) AndroidUtilities.dp(22.0f), (float) AndroidUtilities.dp(28.0f), this.textPaint);
                } else if (i2 == DataAutoDownloadActivity.this.presets.size() - 1) {
                    canvas.drawText(str, (float) ((getMeasuredWidth() - i) - AndroidUtilities.dp(22.0f)), (float) AndroidUtilities.dp(28.0f), this.textPaint);
                } else {
                    canvas.drawText(str, (float) (i6 - (i / 2)), (float) AndroidUtilities.dp(28.0f), this.textPaint);
                }
                i2++;
            }
        }
    }

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
        this.fragmentView = new FrameLayout(context);
        this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        this.listView = new RecyclerListView(context);
        this.listView.setVerticalScrollBarEnabled(false);
        ((DefaultItemAnimator) this.listView.getItemAnimator()).setDelayAnimations(false);
        RecyclerListView recyclerListView = this.listView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false);
        this.layoutManager = linearLayoutManager;
        recyclerListView.setLayoutManager(linearLayoutManager);
        ((FrameLayout) this.fragmentView).addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListenerExtended) new RecyclerListView.OnItemClickListenerExtended() {
            public final void onItemClick(View view, int i, float f, float f2) {
                DataAutoDownloadActivity.this.lambda$createView$4$DataAutoDownloadActivity(view, i, f, f2);
            }
        });
        return this.fragmentView;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v42, resolved type: org.telegram.ui.Components.ThemeEditorView$1} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x005e  */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x0060  */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x006d  */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x007e  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x0087  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x00b6  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x00c1  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$createView$4$DataAutoDownloadActivity(android.view.View r30, int r31, float r32, float r33) {
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
            r14 = 0
            r15 = 1
            if (r8 != r0) goto L_0x00ed
            int r0 = r12.currentPresetNum
            if (r0 == r13) goto L_0x0032
            if (r0 != 0) goto L_0x001f
            org.telegram.messenger.DownloadController$Preset r0 = r12.typePreset
            org.telegram.messenger.DownloadController$Preset r2 = r12.lowPreset
            r0.set((org.telegram.messenger.DownloadController.Preset) r2)
            goto L_0x0032
        L_0x001f:
            if (r0 != r15) goto L_0x0029
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
            java.lang.System.arraycopy(r4, r14, r3, r14, r10)
            goto L_0x0052
        L_0x004b:
            org.telegram.messenger.DownloadController$Preset r3 = r12.typePreset
            boolean r4 = r3.enabled
            r4 = r4 ^ r15
            r3.enabled = r4
        L_0x0052:
            org.telegram.messenger.DownloadController$Preset r3 = r12.typePreset
            boolean r3 = r3.enabled
            java.lang.String r4 = "windowBackgroundChecked"
            java.lang.String r5 = "windowBackgroundUnchecked"
            if (r3 == 0) goto L_0x0060
            r3 = r4
            goto L_0x0061
        L_0x0060:
            r3 = r5
        L_0x0061:
            r11.setTag(r3)
            r3 = r2 ^ 1
            org.telegram.messenger.DownloadController$Preset r6 = r12.typePreset
            boolean r6 = r6.enabled
            if (r6 == 0) goto L_0x006d
            goto L_0x006e
        L_0x006d:
            r4 = r5
        L_0x006e:
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            r0.setBackgroundColorAnimated(r3, r4)
            r29.updateRows()
            org.telegram.messenger.DownloadController$Preset r3 = r12.typePreset
            boolean r3 = r3.enabled
            if (r3 == 0) goto L_0x0087
            org.telegram.ui.DataAutoDownloadActivity$ListAdapter r3 = r12.listAdapter
            int r4 = r12.autoDownloadSectionRow
            int r4 = r4 + r15
            r3.notifyItemRangeInserted(r4, r1)
            goto L_0x008f
        L_0x0087:
            org.telegram.ui.DataAutoDownloadActivity$ListAdapter r3 = r12.listAdapter
            int r4 = r12.autoDownloadSectionRow
            int r4 = r4 + r15
            r3.notifyItemRangeRemoved(r4, r1)
        L_0x008f:
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
            r1.commit()
            r1 = r2 ^ 1
            r0.setChecked(r1)
            int r0 = r12.currentAccount
            org.telegram.messenger.DownloadController r0 = org.telegram.messenger.DownloadController.getInstance(r0)
            r0.checkAutodownloadSettings()
            r12.wereAnyChanges = r15
            goto L_0x05d0
        L_0x00ed:
            int r0 = r12.photosRow
            if (r8 == r0) goto L_0x00f9
            int r0 = r12.videosRow
            if (r8 == r0) goto L_0x00f9
            int r0 = r12.filesRow
            if (r8 != r0) goto L_0x05d0
        L_0x00f9:
            boolean r0 = r30.isEnabled()
            if (r0 != 0) goto L_0x0100
            return
        L_0x0100:
            int r0 = r12.photosRow
            if (r8 != r0) goto L_0x0107
            r16 = 1
            goto L_0x0110
        L_0x0107:
            int r0 = r12.videosRow
            if (r8 != r0) goto L_0x010e
            r16 = 4
            goto L_0x0110
        L_0x010e:
            r16 = 8
        L_0x0110:
            int r17 = org.telegram.messenger.DownloadController.typeToIndex(r16)
            int r0 = r12.currentType
            if (r0 != 0) goto L_0x012a
            int r0 = r12.currentAccount
            org.telegram.messenger.DownloadController r0 = org.telegram.messenger.DownloadController.getInstance(r0)
            org.telegram.messenger.DownloadController$Preset r0 = r0.getCurrentMobilePreset()
            java.lang.String r1 = "mobilePreset"
            java.lang.String r2 = "currentMobilePreset"
        L_0x0126:
            r7 = r0
            r6 = r1
            r5 = r2
            goto L_0x014b
        L_0x012a:
            if (r0 != r15) goto L_0x013c
            int r0 = r12.currentAccount
            org.telegram.messenger.DownloadController r0 = org.telegram.messenger.DownloadController.getInstance(r0)
            org.telegram.messenger.DownloadController$Preset r0 = r0.getCurrentWiFiPreset()
            java.lang.String r1 = "wifiPreset"
            java.lang.String r2 = "currentWifiPreset"
            goto L_0x0126
        L_0x013c:
            int r0 = r12.currentAccount
            org.telegram.messenger.DownloadController r0 = org.telegram.messenger.DownloadController.getInstance(r0)
            org.telegram.messenger.DownloadController$Preset r0 = r0.getCurrentRoamingPreset()
            java.lang.String r1 = "roamingPreset"
            java.lang.String r2 = "currentRoamingPreset"
            goto L_0x0126
        L_0x014b:
            r0 = r11
            org.telegram.ui.Cells.NotificationsCheckCell r0 = (org.telegram.ui.Cells.NotificationsCheckCell) r0
            boolean r1 = r0.isChecked()
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            r3 = 1117257728(0x42980000, float:76.0)
            if (r2 == 0) goto L_0x0161
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r2 = (float) r2
            int r2 = (r32 > r2 ? 1 : (r32 == r2 ? 0 : -1))
            if (r2 <= 0) goto L_0x0173
        L_0x0161:
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 != 0) goto L_0x0228
            int r2 = r30.getMeasuredWidth()
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r2 = r2 - r3
            float r2 = (float) r2
            int r2 = (r32 > r2 ? 1 : (r32 == r2 ? 0 : -1))
            if (r2 < 0) goto L_0x0228
        L_0x0173:
            int r2 = r12.currentPresetNum
            if (r2 == r13) goto L_0x0194
            if (r2 != 0) goto L_0x0181
            org.telegram.messenger.DownloadController$Preset r2 = r12.typePreset
            org.telegram.messenger.DownloadController$Preset r3 = r12.lowPreset
            r2.set((org.telegram.messenger.DownloadController.Preset) r3)
            goto L_0x0194
        L_0x0181:
            if (r2 != r15) goto L_0x018b
            org.telegram.messenger.DownloadController$Preset r2 = r12.typePreset
            org.telegram.messenger.DownloadController$Preset r3 = r12.mediumPreset
            r2.set((org.telegram.messenger.DownloadController.Preset) r3)
            goto L_0x0194
        L_0x018b:
            if (r2 != r9) goto L_0x0194
            org.telegram.messenger.DownloadController$Preset r2 = r12.typePreset
            org.telegram.messenger.DownloadController$Preset r3 = r12.highPreset
            r2.set((org.telegram.messenger.DownloadController.Preset) r3)
        L_0x0194:
            r2 = 0
        L_0x0195:
            org.telegram.messenger.DownloadController$Preset r3 = r12.typePreset
            int[] r3 = r3.mask
            int r3 = r3.length
            if (r2 >= r3) goto L_0x01a9
            int[] r3 = r7.mask
            r3 = r3[r2]
            r3 = r3 & r16
            if (r3 == 0) goto L_0x01a6
            r2 = 1
            goto L_0x01aa
        L_0x01a6:
            int r2 = r2 + 1
            goto L_0x0195
        L_0x01a9:
            r2 = 0
        L_0x01aa:
            org.telegram.messenger.DownloadController$Preset r3 = r12.typePreset
            int[] r3 = r3.mask
            int r4 = r3.length
            if (r14 >= r4) goto L_0x01c6
            if (r1 == 0) goto L_0x01bb
            r4 = r3[r14]
            r7 = r16 ^ -1
            r4 = r4 & r7
            r3[r14] = r4
            goto L_0x01c3
        L_0x01bb:
            if (r2 != 0) goto L_0x01c3
            r4 = r3[r14]
            r4 = r4 | r16
            r3[r14] = r4
        L_0x01c3:
            int r14 = r14 + 1
            goto L_0x01aa
        L_0x01c6:
            int r2 = r12.currentAccount
            android.content.SharedPreferences r2 = org.telegram.messenger.MessagesController.getMainSettings(r2)
            android.content.SharedPreferences$Editor r2 = r2.edit()
            org.telegram.messenger.DownloadController$Preset r3 = r12.typePreset
            java.lang.String r3 = r3.toString()
            r2.putString(r6, r3)
            r12.currentPresetNum = r13
            r2.putInt(r5, r13)
            int r3 = r12.currentType
            if (r3 != 0) goto L_0x01ed
            int r3 = r12.currentAccount
            org.telegram.messenger.DownloadController r3 = org.telegram.messenger.DownloadController.getInstance(r3)
            int r4 = r12.currentPresetNum
            r3.currentMobilePreset = r4
            goto L_0x0204
        L_0x01ed:
            if (r3 != r15) goto L_0x01fa
            int r3 = r12.currentAccount
            org.telegram.messenger.DownloadController r3 = org.telegram.messenger.DownloadController.getInstance(r3)
            int r4 = r12.currentPresetNum
            r3.currentWifiPreset = r4
            goto L_0x0204
        L_0x01fa:
            int r3 = r12.currentAccount
            org.telegram.messenger.DownloadController r3 = org.telegram.messenger.DownloadController.getInstance(r3)
            int r4 = r12.currentPresetNum
            r3.currentRoamingPreset = r4
        L_0x0204:
            r2.commit()
            r1 = r1 ^ r15
            r0.setChecked(r1)
            org.telegram.ui.Components.RecyclerListView r0 = r12.listView
            androidx.recyclerview.widget.RecyclerView$ViewHolder r0 = r0.findContainingViewHolder(r11)
            if (r0 == 0) goto L_0x0218
            org.telegram.ui.DataAutoDownloadActivity$ListAdapter r1 = r12.listAdapter
            r1.onBindViewHolder(r0, r8)
        L_0x0218:
            int r0 = r12.currentAccount
            org.telegram.messenger.DownloadController r0 = org.telegram.messenger.DownloadController.getInstance(r0)
            r0.checkAutodownloadSettings()
            r12.wereAnyChanges = r15
            r29.fillPresets()
            goto L_0x05d0
        L_0x0228:
            android.app.Activity r0 = r29.getParentActivity()
            if (r0 != 0) goto L_0x022f
            return
        L_0x022f:
            org.telegram.ui.ActionBar.BottomSheet$Builder r4 = new org.telegram.ui.ActionBar.BottomSheet$Builder
            android.app.Activity r0 = r29.getParentActivity()
            r4.<init>(r0)
            r4.setApplyTopPadding(r14)
            r4.setApplyBottomPadding(r14)
            android.widget.LinearLayout r3 = new android.widget.LinearLayout
            android.app.Activity r0 = r29.getParentActivity()
            r3.<init>(r0)
            r3.setOrientation(r15)
            r4.setCustomView(r3)
            org.telegram.ui.Cells.HeaderCell r0 = new org.telegram.ui.Cells.HeaderCell
            android.app.Activity r19 = r29.getParentActivity()
            r20 = 1
            r21 = 21
            r22 = 15
            r23 = 0
            r18 = r0
            r18.<init>(r19, r20, r21, r22, r23)
            int r1 = r12.photosRow
            if (r8 != r1) goto L_0x0271
            r1 = 2131624340(0x7f0e0194, float:1.8875857E38)
            java.lang.String r2 = "AutoDownloadPhotosTitle"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setText(r1)
            goto L_0x028e
        L_0x0271:
            int r1 = r12.videosRow
            if (r8 != r1) goto L_0x0282
            r1 = 2131624350(0x7f0e019e, float:1.8875877E38)
            java.lang.String r2 = "AutoDownloadVideosTitle"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setText(r1)
            goto L_0x028e
        L_0x0282:
            r1 = 2131624320(0x7f0e0180, float:1.8875816E38)
            java.lang.String r2 = "AutoDownloadFilesTitle"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setText(r1)
        L_0x028e:
            r1 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r2 = -1
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r2, r1)
            r3.addView(r0, r1)
            org.telegram.ui.Cells.MaxFileSizeCell[] r1 = new org.telegram.ui.Cells.MaxFileSizeCell[r15]
            org.telegram.ui.Cells.TextCheckCell[] r0 = new org.telegram.ui.Cells.TextCheckCell[r15]
            android.animation.AnimatorSet[] r13 = new android.animation.AnimatorSet[r15]
            org.telegram.ui.Cells.TextCheckBoxCell[] r9 = new org.telegram.ui.Cells.TextCheckBoxCell[r10]
        L_0x02a0:
            if (r14 >= r10) goto L_0x0385
            org.telegram.ui.Cells.TextCheckBoxCell r2 = new org.telegram.ui.Cells.TextCheckBoxCell
            android.app.Activity r10 = r29.getParentActivity()
            r2.<init>(r10, r15)
            r9[r14] = r2
            if (r14 != 0) goto L_0x02d0
            r10 = r9[r14]
            r15 = 2131624372(0x7f0e01b4, float:1.8875922E38)
            r22 = r0
            java.lang.String r0 = "AutodownloadContacts"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r0, r15)
            int[] r15 = r7.mask
            r19 = 0
            r15 = r15[r19]
            r15 = r15 & r16
            r23 = r1
            r1 = 1
            if (r15 == 0) goto L_0x02cb
            r15 = 1
            goto L_0x02cc
        L_0x02cb:
            r15 = 0
        L_0x02cc:
            r10.setTextAndCheck(r0, r15, r1)
            goto L_0x0332
        L_0x02d0:
            r22 = r0
            r23 = r1
            r1 = 1
            if (r14 != r1) goto L_0x02f1
            r0 = r9[r14]
            r10 = 2131624374(0x7f0e01b6, float:1.8875926E38)
            java.lang.String r15 = "AutodownloadPrivateChats"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r15, r10)
            int[] r15 = r7.mask
            r15 = r15[r1]
            r15 = r15 & r16
            if (r15 == 0) goto L_0x02ec
            r15 = 1
            goto L_0x02ed
        L_0x02ec:
            r15 = 0
        L_0x02ed:
            r0.setTextAndCheck(r10, r15, r1)
            goto L_0x0332
        L_0x02f1:
            r10 = 2
            if (r14 != r10) goto L_0x030f
            r0 = r9[r14]
            r1 = 2131624373(0x7f0e01b5, float:1.8875924E38)
            java.lang.String r15 = "AutodownloadGroupChats"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r15, r1)
            int[] r15 = r7.mask
            r15 = r15[r10]
            r15 = r15 & r16
            r10 = 1
            if (r15 == 0) goto L_0x030a
            r15 = 1
            goto L_0x030b
        L_0x030a:
            r15 = 0
        L_0x030b:
            r0.setTextAndCheck(r1, r15, r10)
            goto L_0x0332
        L_0x030f:
            r10 = 3
            if (r14 != r10) goto L_0x0332
            r0 = r9[r14]
            r1 = 2131624371(0x7f0e01b3, float:1.887592E38)
            java.lang.String r15 = "AutodownloadChannels"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r15, r1)
            int[] r15 = r7.mask
            r15 = r15[r10]
            r15 = r15 & r16
            if (r15 == 0) goto L_0x0327
            r15 = 1
            goto L_0x0328
        L_0x0327:
            r15 = 0
        L_0x0328:
            int r10 = r12.photosRow
            if (r8 == r10) goto L_0x032e
            r10 = 1
            goto L_0x032f
        L_0x032e:
            r10 = 0
        L_0x032f:
            r0.setTextAndCheck(r1, r15, r10)
        L_0x0332:
            r0 = r9[r14]
            r1 = 0
            android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r1)
            r0.setBackgroundDrawable(r10)
            r10 = r9[r14]
            org.telegram.ui.-$$Lambda$DataAutoDownloadActivity$GvlKaEmbPmrPjkZgpelfWkoLNuU r15 = new org.telegram.ui.-$$Lambda$DataAutoDownloadActivity$GvlKaEmbPmrPjkZgpelfWkoLNuU
            r1 = r22
            r0 = r15
            r22 = r23
            r23 = r1
            r1 = r29
            r11 = -1
            r24 = r3
            r3 = r9
            r25 = r4
            r4 = r31
            r26 = r5
            r5 = r22
            r27 = r6
            r6 = r23
            r28 = r7
            r7 = r13
            r0.<init>(r2, r3, r4, r5, r6, r7)
            r10.setOnClickListener(r15)
            r0 = r9[r14]
            r1 = 1112014848(0x42480000, float:50.0)
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r1)
            r7 = r24
            r7.addView(r0, r1)
            int r14 = r14 + 1
            r11 = r30
            r3 = r7
            r1 = r22
            r0 = r23
            r4 = r25
            r5 = r26
            r6 = r27
            r7 = r28
            r2 = -1
            r10 = 4
            r15 = 1
            goto L_0x02a0
        L_0x0385:
            r23 = r0
            r22 = r1
            r25 = r4
            r26 = r5
            r27 = r6
            r28 = r7
            r11 = -1
            r7 = r3
            int r0 = r12.photosRow
            r10 = -2
            r14 = 0
            if (r8 == r0) goto L_0x048b
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
            r22[r0] = r18
            r1 = r22[r0]
            r2 = r28
            int[] r3 = r2.sizes
            r3 = r3[r17]
            long r3 = (long) r3
            r1.setSize(r3)
            r1 = r22[r0]
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
            org.telegram.ui.-$$Lambda$DataAutoDownloadActivity$m0gzCqbXGWHGQEKzinvKQh1h1B0 r0 = new org.telegram.ui.-$$Lambda$DataAutoDownloadActivity$m0gzCqbXGWHGQEKzinvKQh1h1B0
            r0.<init>(r6)
            r1.setOnClickListener(r0)
            android.app.Activity r0 = r29.getParentActivity()
            r1 = 2131165409(0x7var_e1, float:1.7945034E38)
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
            if (r8 != r0) goto L_0x045f
            r0 = 0
            r1 = r22[r0]
            r3 = 2131624325(0x7f0e0185, float:1.8875827E38)
            java.lang.String r4 = "AutoDownloadMaxVideoSize"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r1.setText(r3)
            r1 = r6[r0]
            r3 = 2131624344(0x7f0e0198, float:1.8875865E38)
            java.lang.String r4 = "AutoDownloadPreloadVideo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            boolean r4 = r2.preloadVideo
            r1.setTextAndCheck(r3, r4, r0)
            r1 = 2131624345(0x7f0e0199, float:1.8875867E38)
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
            goto L_0x04b0
        L_0x045f:
            r0 = 0
            r1 = r22[r0]
            r3 = 2131624324(0x7f0e0184, float:1.8875824E38)
            java.lang.String r4 = "AutoDownloadMaxFileSize"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r1.setText(r3)
            r1 = r6[r0]
            r3 = 2131624342(0x7f0e0196, float:1.887586E38)
            java.lang.String r4 = "AutoDownloadPreloadMusic"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            boolean r4 = r2.preloadMusic
            r1.setTextAndCheck(r3, r4, r0)
            r1 = 2131624343(0x7f0e0197, float:1.8875863E38)
            java.lang.String r3 = "AutoDownloadPreloadMusicInfo"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
            r15.setText(r1)
            goto L_0x04b0
        L_0x048b:
            r6 = r23
            r2 = r28
            r0 = 0
            r22[r0] = r14
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
        L_0x04b0:
            int r0 = r12.videosRow
            if (r8 != r0) goto L_0x04e0
            r0 = 0
        L_0x04b5:
            int r1 = r9.length
            if (r0 >= r1) goto L_0x04c5
            r1 = r9[r0]
            boolean r1 = r1.isChecked()
            if (r1 == 0) goto L_0x04c2
            r0 = 1
            goto L_0x04c6
        L_0x04c2:
            int r0 = r0 + 1
            goto L_0x04b5
        L_0x04c5:
            r0 = 0
        L_0x04c6:
            r1 = 0
            if (r0 != 0) goto L_0x04d3
            r3 = r22[r1]
            r3.setEnabled(r0, r14)
            r3 = r6[r1]
            r3.setEnabled(r0, r14)
        L_0x04d3:
            int[] r0 = r2.sizes
            r0 = r0[r17]
            r2 = 2097152(0x200000, float:2.938736E-39)
            if (r0 > r2) goto L_0x04e0
            r0 = r6[r1]
            r0.setEnabled(r1, r14)
        L_0x04e0:
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
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r1.setTextColor(r3)
            r3 = 17
            r1.setGravity(r3)
            java.lang.String r3 = "fonts/rmedium.ttf"
            android.graphics.Typeface r3 = org.telegram.messenger.AndroidUtilities.getTypeface(r3)
            r1.setTypeface(r3)
            r3 = 2131624479(0x7f0e021f, float:1.8876139E38)
            java.lang.String r4 = "Cancel"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            java.lang.String r3 = r3.toUpperCase()
            r1.setText(r3)
            r3 = 1092616192(0x41200000, float:10.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r7 = 0
            r1.setPadding(r4, r7, r5, r7)
            r4 = 36
            r5 = 51
            r7 = -2
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r7, (int) r4, (int) r5)
            r0.addView(r1, r4)
            org.telegram.ui.-$$Lambda$DataAutoDownloadActivity$p8_fIkB1xZiV8Kgpv8cx4j3wbxY r4 = new org.telegram.ui.-$$Lambda$DataAutoDownloadActivity$p8_fIkB1xZiV8Kgpv8cx4j3wbxY
            r13 = r25
            r4.<init>()
            r1.setOnClickListener(r4)
            android.widget.TextView r14 = new android.widget.TextView
            android.app.Activity r1 = r29.getParentActivity()
            r14.<init>(r1)
            r1 = 1
            r14.setTextSize(r1, r2)
            java.lang.String r1 = "dialogTextBlue2"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r14.setTextColor(r1)
            r1 = 17
            r14.setGravity(r1)
            java.lang.String r1 = "fonts/rmedium.ttf"
            android.graphics.Typeface r1 = org.telegram.messenger.AndroidUtilities.getTypeface(r1)
            r14.setTypeface(r1)
            r1 = 2131626473(0x7f0e09e9, float:1.8880183E38)
            java.lang.String r2 = "Save"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.String r1 = r1.toUpperCase()
            r14.setText(r1)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r3 = 0
            r14.setPadding(r1, r3, r2, r3)
            r1 = 36
            r2 = 53
            r3 = -2
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r3, (int) r1, (int) r2)
            r0.addView(r14, r1)
            org.telegram.ui.-$$Lambda$DataAutoDownloadActivity$bStK8LWlLSXpGpzkzlpaQV4c9zo r15 = new org.telegram.ui.-$$Lambda$DataAutoDownloadActivity$bStK8LWlLSXpGpzkzlpaQV4c9zo
            r0 = r15
            r1 = r29
            r2 = r9
            r3 = r16
            r4 = r22
            r5 = r17
            r7 = r31
            r8 = r27
            r9 = r26
            r10 = r13
            r11 = r30
            r0.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)
            r14.setOnClickListener(r15)
            org.telegram.ui.ActionBar.BottomSheet r0 = r13.create()
            r12.showDialog(r0)
        L_0x05d0:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DataAutoDownloadActivity.lambda$createView$4$DataAutoDownloadActivity(android.view.View, int, float, float):void");
    }

    public /* synthetic */ void lambda$null$0$DataAutoDownloadActivity(TextCheckBoxCell textCheckBoxCell, TextCheckBoxCell[] textCheckBoxCellArr, int i, MaxFileSizeCell[] maxFileSizeCellArr, TextCheckCell[] textCheckCellArr, final AnimatorSet[] animatorSetArr, View view) {
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
                animatorSetArr[0].addListener(new AnimatorListenerAdapter() {
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

    public /* synthetic */ void lambda$null$3$DataAutoDownloadActivity(TextCheckBoxCell[] textCheckBoxCellArr, int i, MaxFileSizeCell[] maxFileSizeCellArr, int i2, TextCheckCell[] textCheckCellArr, int i3, String str, String str2, BottomSheet.Builder builder, View view, View view2) {
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
        Collections.sort(this.presets, $$Lambda$DataAutoDownloadActivity$E0PVxdOLHPC3ZjO_nDkt8nGBYVw.INSTANCE);
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            RecyclerView.ViewHolder findViewHolderForAdapterPosition = recyclerListView.findViewHolderForAdapterPosition(this.usageProgressRow);
            if (findViewHolderForAdapterPosition != null) {
                findViewHolderForAdapterPosition.itemView.requestLayout();
            } else {
                this.listAdapter.notifyItemChanged(this.usageProgressRow);
            }
        }
        int i = this.currentPresetNum;
        if (i == 0 || (i == 3 && this.typePreset.equals(this.lowPreset))) {
            this.selectedPreset = this.presets.indexOf(this.lowPreset);
            return;
        }
        int i2 = this.currentPresetNum;
        if (i2 == 1 || (i2 == 3 && this.typePreset.equals(this.mediumPreset))) {
            this.selectedPreset = this.presets.indexOf(this.mediumPreset);
            return;
        }
        int i3 = this.currentPresetNum;
        if (i3 == 2 || (i3 == 3 && this.typePreset.equals(this.highPreset))) {
            this.selectedPreset = this.presets.indexOf(this.highPreset);
        } else {
            this.selectedPreset = this.presets.indexOf(this.typePreset);
        }
    }

    static /* synthetic */ int lambda$fillPresets$5(DownloadController.Preset preset, DownloadController.Preset preset2) {
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
                if ((preset.mask[i] & 8) != 0) {
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
                if ((preset2.mask[i2] & 8) != 0) {
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
        int i = this.rowCount;
        this.rowCount = i + 1;
        this.autoDownloadRow = i;
        int i2 = this.rowCount;
        this.rowCount = i2 + 1;
        this.autoDownloadSectionRow = i2;
        if (this.typePreset.enabled) {
            int i3 = this.rowCount;
            this.rowCount = i3 + 1;
            this.usageHeaderRow = i3;
            int i4 = this.rowCount;
            this.rowCount = i4 + 1;
            this.usageProgressRow = i4;
            int i5 = this.rowCount;
            this.rowCount = i5 + 1;
            this.usageSectionRow = i5;
            int i6 = this.rowCount;
            this.rowCount = i6 + 1;
            this.typeHeaderRow = i6;
            int i7 = this.rowCount;
            this.rowCount = i7 + 1;
            this.photosRow = i7;
            int i8 = this.rowCount;
            this.rowCount = i8 + 1;
            this.videosRow = i8;
            int i9 = this.rowCount;
            this.rowCount = i9 + 1;
            this.filesRow = i9;
            int i10 = this.rowCount;
            this.rowCount = i10 + 1;
            this.typeSectionRow = i10;
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

        /* JADX WARNING: Removed duplicated region for block: B:73:0x0212  */
        /* JADX WARNING: Removed duplicated region for block: B:78:0x021c  */
        /* JADX WARNING: Removed duplicated region for block: B:79:0x021e  */
        /* JADX WARNING: Removed duplicated region for block: B:82:0x0229  */
        /* JADX WARNING: Removed duplicated region for block: B:83:0x022b  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r14, int r15) {
            /*
                r13 = this;
                int r0 = r14.getItemViewType()
                r1 = 0
                r2 = 1
                if (r0 == 0) goto L_0x025f
                r3 = 2
                if (r0 == r3) goto L_0x0231
                r4 = 4
                if (r0 == r4) goto L_0x00b2
                r4 = 5
                if (r0 == r4) goto L_0x0013
                goto L_0x02ab
            L_0x0013:
                android.view.View r14 = r14.itemView
                org.telegram.ui.Cells.TextInfoPrivacyCell r14 = (org.telegram.ui.Cells.TextInfoPrivacyCell) r14
                org.telegram.ui.DataAutoDownloadActivity r0 = org.telegram.ui.DataAutoDownloadActivity.this
                int r0 = r0.typeSectionRow
                r4 = 2131165409(0x7var_e1, float:1.7945034E38)
                java.lang.String r5 = "windowBackgroundGrayShadow"
                if (r15 != r0) goto L_0x003f
                r15 = 2131624313(0x7f0e0179, float:1.8875802E38)
                java.lang.String r0 = "AutoDownloadAudioInfo"
                java.lang.String r15 = org.telegram.messenger.LocaleController.getString(r0, r15)
                r14.setText(r15)
                android.content.Context r15 = r13.mContext
                android.graphics.drawable.Drawable r15 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r15, (int) r4, (java.lang.String) r5)
                r14.setBackgroundDrawable(r15)
                r14.setFixedSize(r1)
                goto L_0x02ab
            L_0x003f:
                org.telegram.ui.DataAutoDownloadActivity r0 = org.telegram.ui.DataAutoDownloadActivity.this
                int r0 = r0.autoDownloadSectionRow
                if (r15 != r0) goto L_0x02ab
                org.telegram.ui.DataAutoDownloadActivity r15 = org.telegram.ui.DataAutoDownloadActivity.this
                int r15 = r15.usageHeaderRow
                r0 = -1
                if (r15 != r0) goto L_0x009e
                android.content.Context r15 = r13.mContext
                r0 = 2131165410(0x7var_e2, float:1.7945036E38)
                android.graphics.drawable.Drawable r15 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r15, (int) r0, (java.lang.String) r5)
                r14.setBackgroundDrawable(r15)
                org.telegram.ui.DataAutoDownloadActivity r15 = org.telegram.ui.DataAutoDownloadActivity.this
                int r15 = r15.currentType
                if (r15 != 0) goto L_0x0072
                r15 = 2131624332(0x7f0e018c, float:1.887584E38)
                java.lang.String r0 = "AutoDownloadOnMobileDataInfo"
                java.lang.String r15 = org.telegram.messenger.LocaleController.getString(r0, r15)
                r14.setText(r15)
                goto L_0x02ab
            L_0x0072:
                org.telegram.ui.DataAutoDownloadActivity r15 = org.telegram.ui.DataAutoDownloadActivity.this
                int r15 = r15.currentType
                if (r15 != r2) goto L_0x0088
                r15 = 2131624337(0x7f0e0191, float:1.887585E38)
                java.lang.String r0 = "AutoDownloadOnWiFiDataInfo"
                java.lang.String r15 = org.telegram.messenger.LocaleController.getString(r0, r15)
                r14.setText(r15)
                goto L_0x02ab
            L_0x0088:
                org.telegram.ui.DataAutoDownloadActivity r15 = org.telegram.ui.DataAutoDownloadActivity.this
                int r15 = r15.currentType
                if (r15 != r3) goto L_0x02ab
                r15 = 2131624334(0x7f0e018e, float:1.8875845E38)
                java.lang.String r0 = "AutoDownloadOnRoamingDataInfo"
                java.lang.String r15 = org.telegram.messenger.LocaleController.getString(r0, r15)
                r14.setText(r15)
                goto L_0x02ab
            L_0x009e:
                android.content.Context r15 = r13.mContext
                android.graphics.drawable.Drawable r15 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r15, (int) r4, (java.lang.String) r5)
                r14.setBackgroundDrawable(r15)
                r15 = 0
                r14.setText(r15)
                r15 = 12
                r14.setFixedSize(r15)
                goto L_0x02ab
            L_0x00b2:
                android.view.View r14 = r14.itemView
                r5 = r14
                org.telegram.ui.Cells.NotificationsCheckCell r5 = (org.telegram.ui.Cells.NotificationsCheckCell) r5
                org.telegram.ui.DataAutoDownloadActivity r14 = org.telegram.ui.DataAutoDownloadActivity.this
                int r14 = r14.photosRow
                if (r15 != r14) goto L_0x00cb
                r14 = 2131624338(0x7f0e0192, float:1.8875853E38)
                java.lang.String r0 = "AutoDownloadPhotos"
                java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r0, r14)
                r6 = r14
                r0 = 1
                goto L_0x00eb
            L_0x00cb:
                org.telegram.ui.DataAutoDownloadActivity r14 = org.telegram.ui.DataAutoDownloadActivity.this
                int r14 = r14.videosRow
                if (r15 != r14) goto L_0x00df
                r14 = 2131624348(0x7f0e019c, float:1.8875873E38)
                java.lang.String r0 = "AutoDownloadVideos"
                java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r0, r14)
                r6 = r14
                r0 = 4
                goto L_0x00eb
            L_0x00df:
                r14 = 2131624318(0x7f0e017e, float:1.8875812E38)
                java.lang.String r0 = "AutoDownloadFiles"
                java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r0, r14)
                r0 = 8
                r6 = r14
            L_0x00eb:
                org.telegram.ui.DataAutoDownloadActivity r14 = org.telegram.ui.DataAutoDownloadActivity.this
                int r14 = r14.currentType
                if (r14 != 0) goto L_0x0102
                org.telegram.ui.DataAutoDownloadActivity r14 = org.telegram.ui.DataAutoDownloadActivity.this
                int r14 = r14.currentAccount
                org.telegram.messenger.DownloadController r14 = org.telegram.messenger.DownloadController.getInstance(r14)
                org.telegram.messenger.DownloadController$Preset r14 = r14.getCurrentMobilePreset()
                goto L_0x0127
            L_0x0102:
                org.telegram.ui.DataAutoDownloadActivity r14 = org.telegram.ui.DataAutoDownloadActivity.this
                int r14 = r14.currentType
                if (r14 != r2) goto L_0x0119
                org.telegram.ui.DataAutoDownloadActivity r14 = org.telegram.ui.DataAutoDownloadActivity.this
                int r14 = r14.currentAccount
                org.telegram.messenger.DownloadController r14 = org.telegram.messenger.DownloadController.getInstance(r14)
                org.telegram.messenger.DownloadController$Preset r14 = r14.getCurrentWiFiPreset()
                goto L_0x0127
            L_0x0119:
                org.telegram.ui.DataAutoDownloadActivity r14 = org.telegram.ui.DataAutoDownloadActivity.this
                int r14 = r14.currentAccount
                org.telegram.messenger.DownloadController r14 = org.telegram.messenger.DownloadController.getInstance(r14)
                org.telegram.messenger.DownloadController$Preset r14 = r14.getCurrentRoamingPreset()
            L_0x0127:
                int[] r7 = r14.sizes
                int r8 = org.telegram.messenger.DownloadController.typeToIndex(r0)
                r7 = r7[r8]
                java.lang.StringBuilder r8 = new java.lang.StringBuilder
                r8.<init>()
                r9 = 0
                r10 = 0
            L_0x0136:
                int[] r11 = r14.mask
                int r12 = r11.length
                if (r9 >= r12) goto L_0x018d
                r11 = r11[r9]
                r11 = r11 & r0
                if (r11 == 0) goto L_0x018a
                int r11 = r8.length()
                if (r11 == 0) goto L_0x014b
                java.lang.String r11 = ", "
                r8.append(r11)
            L_0x014b:
                if (r9 == 0) goto L_0x017c
                if (r9 == r2) goto L_0x016f
                if (r9 == r3) goto L_0x0162
                r11 = 3
                if (r9 == r11) goto L_0x0155
                goto L_0x0188
            L_0x0155:
                r11 = 2131624314(0x7f0e017a, float:1.8875804E38)
                java.lang.String r12 = "AutoDownloadChannels"
                java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r12, r11)
                r8.append(r11)
                goto L_0x0188
            L_0x0162:
                r11 = 2131624321(0x7f0e0181, float:1.8875818E38)
                java.lang.String r12 = "AutoDownloadGroups"
                java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r12, r11)
                r8.append(r11)
                goto L_0x0188
            L_0x016f:
                r11 = 2131624341(0x7f0e0195, float:1.8875859E38)
                java.lang.String r12 = "AutoDownloadPm"
                java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r12, r11)
                r8.append(r11)
                goto L_0x0188
            L_0x017c:
                r11 = 2131624315(0x7f0e017b, float:1.8875806E38)
                java.lang.String r12 = "AutoDownloadContacts"
                java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r12, r11)
                r8.append(r11)
            L_0x0188:
                int r10 = r10 + 1
            L_0x018a:
                int r9 = r9 + 1
                goto L_0x0136
            L_0x018d:
                if (r10 != r4) goto L_0x01bd
                r8.setLength(r1)
                org.telegram.ui.DataAutoDownloadActivity r14 = org.telegram.ui.DataAutoDownloadActivity.this
                int r14 = r14.photosRow
                if (r15 != r14) goto L_0x01a7
                r14 = 2131624329(0x7f0e0189, float:1.8875835E38)
                java.lang.String r0 = "AutoDownloadOnAllChats"
                java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r0, r14)
                r8.append(r14)
                goto L_0x01cb
            L_0x01a7:
                r14 = 2131624347(0x7f0e019b, float:1.8875871E38)
                java.lang.Object[] r0 = new java.lang.Object[r2]
                long r3 = (long) r7
                java.lang.String r3 = org.telegram.messenger.AndroidUtilities.formatFileSize(r3)
                r0[r1] = r3
                java.lang.String r3 = "AutoDownloadUpToOnAllChats"
                java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r3, r14, r0)
                r8.append(r14)
                goto L_0x01cb
            L_0x01bd:
                if (r10 != 0) goto L_0x01cd
                r14 = 2131624328(0x7f0e0188, float:1.8875833E38)
                java.lang.String r0 = "AutoDownloadOff"
                java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r0, r14)
                r8.append(r14)
            L_0x01cb:
                r7 = r8
                goto L_0x020a
            L_0x01cd:
                org.telegram.ui.DataAutoDownloadActivity r14 = org.telegram.ui.DataAutoDownloadActivity.this
                int r14 = r14.photosRow
                if (r15 != r14) goto L_0x01ec
                java.lang.StringBuilder r14 = new java.lang.StringBuilder
                r0 = 2131624330(0x7f0e018a, float:1.8875837E38)
                java.lang.Object[] r3 = new java.lang.Object[r2]
                java.lang.String r4 = r8.toString()
                r3[r1] = r4
                java.lang.String r4 = "AutoDownloadOnFor"
                java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r4, r0, r3)
                r14.<init>(r0)
                goto L_0x0209
            L_0x01ec:
                java.lang.StringBuilder r14 = new java.lang.StringBuilder
                r0 = 2131624335(0x7f0e018f, float:1.8875847E38)
                java.lang.Object[] r3 = new java.lang.Object[r3]
                long r11 = (long) r7
                java.lang.String r4 = org.telegram.messenger.AndroidUtilities.formatFileSize(r11)
                r3[r1] = r4
                java.lang.String r4 = r8.toString()
                r3[r2] = r4
                java.lang.String r4 = "AutoDownloadOnUpToFor"
                java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r4, r0, r3)
                r14.<init>(r0)
            L_0x0209:
                r7 = r14
            L_0x020a:
                org.telegram.ui.DataAutoDownloadActivity r14 = org.telegram.ui.DataAutoDownloadActivity.this
                boolean r14 = r14.animateChecked
                if (r14 == 0) goto L_0x021a
                if (r10 == 0) goto L_0x0216
                r14 = 1
                goto L_0x0217
            L_0x0216:
                r14 = 0
            L_0x0217:
                r5.setChecked(r14)
            L_0x021a:
                if (r10 == 0) goto L_0x021e
                r8 = 1
                goto L_0x021f
            L_0x021e:
                r8 = 0
            L_0x021f:
                r9 = 0
                r10 = 1
                org.telegram.ui.DataAutoDownloadActivity r14 = org.telegram.ui.DataAutoDownloadActivity.this
                int r14 = r14.filesRow
                if (r15 == r14) goto L_0x022b
                r11 = 1
                goto L_0x022c
            L_0x022b:
                r11 = 0
            L_0x022c:
                r5.setTextAndValueAndCheck(r6, r7, r8, r9, r10, r11)
                goto L_0x02ab
            L_0x0231:
                android.view.View r14 = r14.itemView
                org.telegram.ui.Cells.HeaderCell r14 = (org.telegram.ui.Cells.HeaderCell) r14
                org.telegram.ui.DataAutoDownloadActivity r0 = org.telegram.ui.DataAutoDownloadActivity.this
                int r0 = r0.usageHeaderRow
                if (r15 != r0) goto L_0x024a
                r15 = 2131624317(0x7f0e017d, float:1.887581E38)
                java.lang.String r0 = "AutoDownloadDataUsage"
                java.lang.String r15 = org.telegram.messenger.LocaleController.getString(r0, r15)
                r14.setText(r15)
                goto L_0x02ab
            L_0x024a:
                org.telegram.ui.DataAutoDownloadActivity r0 = org.telegram.ui.DataAutoDownloadActivity.this
                int r0 = r0.typeHeaderRow
                if (r15 != r0) goto L_0x02ab
                r15 = 2131624346(0x7f0e019a, float:1.887587E38)
                java.lang.String r0 = "AutoDownloadTypes"
                java.lang.String r15 = org.telegram.messenger.LocaleController.getString(r0, r15)
                r14.setText(r15)
                goto L_0x02ab
            L_0x025f:
                android.view.View r14 = r14.itemView
                org.telegram.ui.Cells.TextCheckCell r14 = (org.telegram.ui.Cells.TextCheckCell) r14
                org.telegram.ui.DataAutoDownloadActivity r0 = org.telegram.ui.DataAutoDownloadActivity.this
                int r0 = r0.autoDownloadRow
                if (r15 != r0) goto L_0x02ab
                r14.setDrawCheckRipple(r2)
                r15 = 2131624326(0x7f0e0186, float:1.8875829E38)
                java.lang.String r0 = "AutoDownloadMedia"
                java.lang.String r15 = org.telegram.messenger.LocaleController.getString(r0, r15)
                org.telegram.ui.DataAutoDownloadActivity r0 = org.telegram.ui.DataAutoDownloadActivity.this
                org.telegram.messenger.DownloadController$Preset r0 = r0.typePreset
                boolean r0 = r0.enabled
                r14.setTextAndCheck(r15, r0, r1)
                org.telegram.ui.DataAutoDownloadActivity r15 = org.telegram.ui.DataAutoDownloadActivity.this
                org.telegram.messenger.DownloadController$Preset r15 = r15.typePreset
                boolean r15 = r15.enabled
                java.lang.String r0 = "windowBackgroundChecked"
                java.lang.String r1 = "windowBackgroundUnchecked"
                if (r15 == 0) goto L_0x0294
                r15 = r0
                goto L_0x0295
            L_0x0294:
                r15 = r1
            L_0x0295:
                r14.setTag(r15)
                org.telegram.ui.DataAutoDownloadActivity r15 = org.telegram.ui.DataAutoDownloadActivity.this
                org.telegram.messenger.DownloadController$Preset r15 = r15.typePreset
                boolean r15 = r15.enabled
                if (r15 == 0) goto L_0x02a3
                goto L_0x02a4
            L_0x02a3:
                r0 = r1
            L_0x02a4:
                int r15 = org.telegram.ui.ActionBar.Theme.getColor(r0)
                r14.setBackgroundColor(r15)
            L_0x02ab:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DataAutoDownloadActivity.ListAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            int adapterPosition = viewHolder.getAdapterPosition();
            return adapterPosition == DataAutoDownloadActivity.this.photosRow || adapterPosition == DataAutoDownloadActivity.this.videosRow || adapterPosition == DataAutoDownloadActivity.this.filesRow;
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
                PresetChooseView presetChooseView = new PresetChooseView(this.mContext);
                presetChooseView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                textInfoPrivacyCell = presetChooseView;
            } else if (i == 4) {
                NotificationsCheckCell notificationsCheckCell = new NotificationsCheckCell(this.mContext);
                notificationsCheckCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                textInfoPrivacyCell = notificationsCheckCell;
            } else if (i != 5) {
                textInfoPrivacyCell = null;
            } else {
                TextInfoPrivacyCell textInfoPrivacyCell2 = new TextInfoPrivacyCell(this.mContext);
                textInfoPrivacyCell2.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                textInfoPrivacyCell = textInfoPrivacyCell2;
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

    public ThemeDescription[] getThemeDescriptions() {
        return new ThemeDescription[]{new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{HeaderCell.class, NotificationsCheckCell.class, PresetChooseView.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"), new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"), new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"), new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"), new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"), new ThemeDescription((View) this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"), new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCheckCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundChecked"), new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCheckCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundUnchecked"), new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundCheckText"), new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackBlue"), new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackBlueChecked"), new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackBlueThumb"), new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackBlueThumbChecked"), new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackBlueSelector"), new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackBlueSelectorChecked"), new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"), new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"), new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrack"), new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackChecked"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"), new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"), new ThemeDescription(this.listView, 0, new Class[]{PresetChooseView.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrack"), new ThemeDescription(this.listView, 0, new Class[]{PresetChooseView.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackChecked"), new ThemeDescription(this.listView, 0, new Class[]{PresetChooseView.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText")};
    }
}
