package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView.LayoutParams;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import java.util.ArrayList;
import java.util.Collections;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.DownloadController.Preset;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet.Builder;
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
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public class DataAutoDownloadActivity extends BaseFragment {
    private boolean animateChecked;
    private int autoDownloadRow;
    private int autoDownloadSectionRow;
    private int currentPresetNum;
    private int currentType;
    private Preset defaultPreset;
    private int filesRow;
    private Preset highPreset;
    private String key;
    private String key2;
    private LinearLayoutManager layoutManager;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private Preset lowPreset;
    private Preset mediumPreset;
    private int photosRow;
    private ArrayList<Preset> presets = new ArrayList();
    private int rowCount;
    private int selectedPreset = 1;
    private int typeHeaderRow;
    private Preset typePreset;
    private int typeSectionRow;
    private int usageHeaderRow;
    private int usageProgressRow;
    private int usageSectionRow;
    private int videosRow;
    private boolean wereAnyChanges;

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

        public boolean onTouchEvent(MotionEvent motionEvent) {
            float x = motionEvent.getX();
            int i = 0;
            int i2;
            int i3;
            int i4;
            int i5;
            if (motionEvent.getAction() == 0) {
                getParent().requestDisallowInterceptTouchEvent(true);
                i2 = 0;
                while (i2 < DataAutoDownloadActivity.this.presets.size()) {
                    i3 = this.sideSide;
                    i4 = this.lineSize + (this.gapSize * 2);
                    i5 = this.circleSize;
                    i3 = (i3 + ((i4 + i5) * i2)) + (i5 / 2);
                    if (x <= ((float) (i3 - AndroidUtilities.dp(15.0f))) || x >= ((float) (i3 + AndroidUtilities.dp(15.0f)))) {
                        i2++;
                    } else {
                        boolean z;
                        if (i2 == DataAutoDownloadActivity.this.selectedPreset) {
                            z = true;
                        }
                        this.startMoving = z;
                        this.startX = x;
                        this.startMovingPreset = DataAutoDownloadActivity.this.selectedPreset;
                    }
                }
            } else if (motionEvent.getAction() == 2) {
                if (this.startMoving) {
                    if (Math.abs(this.startX - x) >= AndroidUtilities.getPixelsInCM(0.5f, true)) {
                        this.moving = true;
                        this.startMoving = false;
                    }
                } else if (this.moving) {
                    while (i < DataAutoDownloadActivity.this.presets.size()) {
                        i2 = this.sideSide;
                        i3 = this.lineSize;
                        int i6 = this.gapSize;
                        i4 = (i6 * 2) + i3;
                        i5 = this.circleSize;
                        i2 = (i2 + ((i4 + i5) * i)) + (i5 / 2);
                        i3 = ((i3 / 2) + (i5 / 2)) + i6;
                        if (x <= ((float) (i2 - i3)) || x >= ((float) (i2 + i3))) {
                            i++;
                        } else if (DataAutoDownloadActivity.this.selectedPreset != i) {
                            setPreset(i);
                        }
                    }
                }
            } else if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                if (!this.moving) {
                    i2 = 0;
                    while (i2 < 5) {
                        i3 = this.sideSide;
                        i4 = this.lineSize + (this.gapSize * 2);
                        i5 = this.circleSize;
                        i3 = (i3 + ((i4 + i5) * i2)) + (i5 / 2);
                        if (x <= ((float) (i3 - AndroidUtilities.dp(15.0f))) || x >= ((float) (i3 + AndroidUtilities.dp(15.0f)))) {
                            i2++;
                        } else if (DataAutoDownloadActivity.this.selectedPreset != i2) {
                            setPreset(i2);
                        }
                    }
                } else if (DataAutoDownloadActivity.this.selectedPreset != this.startMovingPreset) {
                    setPreset(DataAutoDownloadActivity.this.selectedPreset);
                }
                this.startMoving = false;
                this.moving = false;
            }
            return true;
        }

        private void setPreset(int i) {
            DataAutoDownloadActivity.this.selectedPreset = i;
            Preset preset = (Preset) DataAutoDownloadActivity.this.presets.get(DataAutoDownloadActivity.this.selectedPreset);
            int i2 = 0;
            if (preset == DataAutoDownloadActivity.this.lowPreset) {
                DataAutoDownloadActivity.this.currentPresetNum = 0;
            } else if (preset == DataAutoDownloadActivity.this.mediumPreset) {
                DataAutoDownloadActivity.this.currentPresetNum = 1;
            } else if (preset == DataAutoDownloadActivity.this.highPreset) {
                DataAutoDownloadActivity.this.currentPresetNum = 2;
            } else {
                DataAutoDownloadActivity.this.currentPresetNum = 3;
            }
            if (DataAutoDownloadActivity.this.currentType == 0) {
                DownloadController.getInstance(DataAutoDownloadActivity.this.currentAccount).currentMobilePreset = DataAutoDownloadActivity.this.currentPresetNum;
            } else if (DataAutoDownloadActivity.this.currentType == 1) {
                DownloadController.getInstance(DataAutoDownloadActivity.this.currentAccount).currentWifiPreset = DataAutoDownloadActivity.this.currentPresetNum;
            } else {
                DownloadController.getInstance(DataAutoDownloadActivity.this.currentAccount).currentRoamingPreset = DataAutoDownloadActivity.this.currentPresetNum;
            }
            Editor edit = MessagesController.getMainSettings(DataAutoDownloadActivity.this.currentAccount).edit();
            edit.putInt(DataAutoDownloadActivity.this.key2, DataAutoDownloadActivity.this.currentPresetNum);
            edit.commit();
            DownloadController.getInstance(DataAutoDownloadActivity.this.currentAccount).checkAutodownloadSettings();
            while (i2 < 3) {
                ViewHolder findViewHolderForAdapterPosition = DataAutoDownloadActivity.this.listView.findViewHolderForAdapterPosition(DataAutoDownloadActivity.this.photosRow + i2);
                if (findViewHolderForAdapterPosition != null) {
                    DataAutoDownloadActivity.this.listAdapter.onBindViewHolder(findViewHolderForAdapterPosition, DataAutoDownloadActivity.this.photosRow + i2);
                }
                i2++;
            }
            DataAutoDownloadActivity.this.wereAnyChanges = true;
            invalidate();
        }

        /* Access modifiers changed, original: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(i, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(74.0f), NUM));
            MeasureSpec.getSize(i);
            this.circleSize = AndroidUtilities.dp(6.0f);
            this.gapSize = AndroidUtilities.dp(2.0f);
            this.sideSide = AndroidUtilities.dp(22.0f);
            this.lineSize = (((getMeasuredWidth() - (this.circleSize * DataAutoDownloadActivity.this.presets.size())) - ((this.gapSize * 2) * (DataAutoDownloadActivity.this.presets.size() - 1))) - (this.sideSide * 2)) / (DataAutoDownloadActivity.this.presets.size() - 1);
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
            this.textPaint.setColor(Theme.getColor("windowBackgroundWhiteGrayText"));
            int measuredHeight = (getMeasuredHeight() / 2) + AndroidUtilities.dp(11.0f);
            int i = 0;
            while (i < DataAutoDownloadActivity.this.presets.size()) {
                String str;
                int i2 = this.sideSide;
                int i3 = this.lineSize + (this.gapSize * 2);
                int i4 = this.circleSize;
                i2 = (i2 + ((i3 + i4) * i)) + (i4 / 2);
                if (i <= DataAutoDownloadActivity.this.selectedPreset) {
                    this.paint.setColor(Theme.getColor("switchTrackChecked"));
                } else {
                    this.paint.setColor(Theme.getColor("switchTrack"));
                }
                canvas.drawCircle((float) i2, (float) measuredHeight, (float) (i == DataAutoDownloadActivity.this.selectedPreset ? AndroidUtilities.dp(6.0f) : this.circleSize / 2), this.paint);
                if (i != 0) {
                    i3 = (i2 - (this.circleSize / 2)) - this.gapSize;
                    i4 = this.lineSize;
                    i3 -= i4;
                    if (i == DataAutoDownloadActivity.this.selectedPreset || i == DataAutoDownloadActivity.this.selectedPreset + 1) {
                        i4 -= AndroidUtilities.dp(3.0f);
                    }
                    if (i == DataAutoDownloadActivity.this.selectedPreset + 1) {
                        i3 += AndroidUtilities.dp(3.0f);
                    }
                    canvas.drawRect((float) i3, (float) (measuredHeight - AndroidUtilities.dp(1.0f)), (float) (i3 + i4), (float) (AndroidUtilities.dp(1.0f) + measuredHeight), this.paint);
                }
                Preset preset = (Preset) DataAutoDownloadActivity.this.presets.get(i);
                if (preset == DataAutoDownloadActivity.this.lowPreset) {
                    str = this.low;
                    i4 = this.lowSize;
                } else if (preset == DataAutoDownloadActivity.this.mediumPreset) {
                    str = this.medium;
                    i4 = this.mediumSize;
                } else if (preset == DataAutoDownloadActivity.this.highPreset) {
                    str = this.high;
                    i4 = this.highSize;
                } else {
                    str = this.custom;
                    i4 = this.customSize;
                }
                if (i == 0) {
                    canvas.drawText(str, (float) AndroidUtilities.dp(22.0f), (float) AndroidUtilities.dp(28.0f), this.textPaint);
                } else if (i == DataAutoDownloadActivity.this.presets.size() - 1) {
                    canvas.drawText(str, (float) ((getMeasuredWidth() - i4) - AndroidUtilities.dp(22.0f)), (float) AndroidUtilities.dp(28.0f), this.textPaint);
                } else {
                    canvas.drawText(str, (float) (i2 - (i4 / 2)), (float) AndroidUtilities.dp(28.0f), this.textPaint);
                }
                i++;
            }
        }
    }

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return DataAutoDownloadActivity.this.rowCount;
        }

        /* JADX WARNING: Removed duplicated region for block: B:73:0x0211  */
        /* JADX WARNING: Removed duplicated region for block: B:79:0x021d  */
        /* JADX WARNING: Removed duplicated region for block: B:78:0x021b  */
        /* JADX WARNING: Removed duplicated region for block: B:83:0x022a  */
        /* JADX WARNING: Removed duplicated region for block: B:82:0x0228  */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r14, int r15) {
            /*
            r13 = this;
            r0 = r14.getItemViewType();
            r1 = 0;
            r2 = 1;
            if (r0 == 0) goto L_0x025e;
        L_0x0008:
            r3 = 2;
            if (r0 == r3) goto L_0x0230;
        L_0x000b:
            r4 = 4;
            if (r0 == r4) goto L_0x00b1;
        L_0x000e:
            r4 = 5;
            if (r0 == r4) goto L_0x0013;
        L_0x0011:
            goto L_0x02a8;
        L_0x0013:
            r14 = r14.itemView;
            r14 = (org.telegram.ui.Cells.TextInfoPrivacyCell) r14;
            r0 = org.telegram.ui.DataAutoDownloadActivity.this;
            r0 = r0.typeSectionRow;
            r4 = NUM; // 0x7var_e4 float:1.794504E38 double:1.0529356157E-314;
            r5 = "windowBackgroundGrayShadow";
            if (r15 != r0) goto L_0x003e;
        L_0x0024:
            r15 = NUM; // 0x7f0d014f float:1.8742794E38 double:1.053129943E-314;
            r0 = "AutoDownloadAudioInfo";
            r15 = org.telegram.messenger.LocaleController.getString(r0, r15);
            r14.setText(r15);
            r15 = r13.mContext;
            r15 = org.telegram.ui.ActionBar.Theme.getThemedDrawable(r15, r4, r5);
            r14.setBackgroundDrawable(r15);
            r14.setFixedSize(r1);
            goto L_0x02a8;
        L_0x003e:
            r0 = org.telegram.ui.DataAutoDownloadActivity.this;
            r0 = r0.autoDownloadSectionRow;
            if (r15 != r0) goto L_0x02a8;
        L_0x0046:
            r15 = org.telegram.ui.DataAutoDownloadActivity.this;
            r15 = r15.usageHeaderRow;
            r0 = -1;
            if (r15 != r0) goto L_0x009d;
        L_0x004f:
            r15 = r13.mContext;
            r0 = NUM; // 0x7var_e5 float:1.7945042E38 double:1.052935616E-314;
            r15 = org.telegram.ui.ActionBar.Theme.getThemedDrawable(r15, r0, r5);
            r14.setBackgroundDrawable(r15);
            r15 = org.telegram.ui.DataAutoDownloadActivity.this;
            r15 = r15.currentType;
            if (r15 != 0) goto L_0x0071;
        L_0x0063:
            r15 = NUM; // 0x7f0d0162 float:1.8742833E38 double:1.0531299524E-314;
            r0 = "AutoDownloadOnMobileDataInfo";
            r15 = org.telegram.messenger.LocaleController.getString(r0, r15);
            r14.setText(r15);
            goto L_0x02a8;
        L_0x0071:
            r15 = org.telegram.ui.DataAutoDownloadActivity.this;
            r15 = r15.currentType;
            if (r15 != r2) goto L_0x0087;
        L_0x0079:
            r15 = NUM; // 0x7f0d0167 float:1.8742843E38 double:1.053129955E-314;
            r0 = "AutoDownloadOnWiFiDataInfo";
            r15 = org.telegram.messenger.LocaleController.getString(r0, r15);
            r14.setText(r15);
            goto L_0x02a8;
        L_0x0087:
            r15 = org.telegram.ui.DataAutoDownloadActivity.this;
            r15 = r15.currentType;
            if (r15 != r3) goto L_0x02a8;
        L_0x008f:
            r15 = NUM; // 0x7f0d0164 float:1.8742837E38 double:1.0531299534E-314;
            r0 = "AutoDownloadOnRoamingDataInfo";
            r15 = org.telegram.messenger.LocaleController.getString(r0, r15);
            r14.setText(r15);
            goto L_0x02a8;
        L_0x009d:
            r15 = r13.mContext;
            r15 = org.telegram.ui.ActionBar.Theme.getThemedDrawable(r15, r4, r5);
            r14.setBackgroundDrawable(r15);
            r15 = 0;
            r14.setText(r15);
            r15 = 12;
            r14.setFixedSize(r15);
            goto L_0x02a8;
        L_0x00b1:
            r14 = r14.itemView;
            r5 = r14;
            r5 = (org.telegram.ui.Cells.NotificationsCheckCell) r5;
            r14 = org.telegram.ui.DataAutoDownloadActivity.this;
            r14 = r14.photosRow;
            if (r15 != r14) goto L_0x00ca;
        L_0x00be:
            r14 = NUM; // 0x7f0d0168 float:1.8742845E38 double:1.0531299554E-314;
            r0 = "AutoDownloadPhotos";
            r14 = org.telegram.messenger.LocaleController.getString(r0, r14);
            r6 = r14;
            r0 = 1;
            goto L_0x00ea;
        L_0x00ca:
            r14 = org.telegram.ui.DataAutoDownloadActivity.this;
            r14 = r14.videosRow;
            if (r15 != r14) goto L_0x00de;
        L_0x00d2:
            r14 = NUM; // 0x7f0d0172 float:1.8742865E38 double:1.0531299603E-314;
            r0 = "AutoDownloadVideos";
            r14 = org.telegram.messenger.LocaleController.getString(r0, r14);
            r6 = r14;
            r0 = 4;
            goto L_0x00ea;
        L_0x00de:
            r14 = NUM; // 0x7f0d0154 float:1.8742804E38 double:1.0531299455E-314;
            r0 = "AutoDownloadFiles";
            r14 = org.telegram.messenger.LocaleController.getString(r0, r14);
            r0 = 8;
            r6 = r14;
        L_0x00ea:
            r14 = org.telegram.ui.DataAutoDownloadActivity.this;
            r14 = r14.currentType;
            if (r14 != 0) goto L_0x0101;
        L_0x00f2:
            r14 = org.telegram.ui.DataAutoDownloadActivity.this;
            r14 = r14.currentAccount;
            r14 = org.telegram.messenger.DownloadController.getInstance(r14);
            r14 = r14.getCurrentMobilePreset();
            goto L_0x0126;
        L_0x0101:
            r14 = org.telegram.ui.DataAutoDownloadActivity.this;
            r14 = r14.currentType;
            if (r14 != r2) goto L_0x0118;
        L_0x0109:
            r14 = org.telegram.ui.DataAutoDownloadActivity.this;
            r14 = r14.currentAccount;
            r14 = org.telegram.messenger.DownloadController.getInstance(r14);
            r14 = r14.getCurrentWiFiPreset();
            goto L_0x0126;
        L_0x0118:
            r14 = org.telegram.ui.DataAutoDownloadActivity.this;
            r14 = r14.currentAccount;
            r14 = org.telegram.messenger.DownloadController.getInstance(r14);
            r14 = r14.getCurrentRoamingPreset();
        L_0x0126:
            r7 = r14.sizes;
            r8 = org.telegram.messenger.DownloadController.typeToIndex(r0);
            r7 = r7[r8];
            r8 = new java.lang.StringBuilder;
            r8.<init>();
            r9 = 0;
            r10 = 0;
        L_0x0135:
            r11 = r14.mask;
            r12 = r11.length;
            if (r9 >= r12) goto L_0x018c;
        L_0x013a:
            r11 = r11[r9];
            r11 = r11 & r0;
            if (r11 == 0) goto L_0x0189;
        L_0x013f:
            r11 = r8.length();
            if (r11 == 0) goto L_0x014a;
        L_0x0145:
            r11 = ", ";
            r8.append(r11);
        L_0x014a:
            if (r9 == 0) goto L_0x017b;
        L_0x014c:
            if (r9 == r2) goto L_0x016e;
        L_0x014e:
            if (r9 == r3) goto L_0x0161;
        L_0x0150:
            r11 = 3;
            if (r9 == r11) goto L_0x0154;
        L_0x0153:
            goto L_0x0187;
        L_0x0154:
            r11 = NUM; // 0x7f0d0150 float:1.8742796E38 double:1.0531299436E-314;
            r12 = "AutoDownloadChannels";
            r11 = org.telegram.messenger.LocaleController.getString(r12, r11);
            r8.append(r11);
            goto L_0x0187;
        L_0x0161:
            r11 = NUM; // 0x7f0d0157 float:1.874281E38 double:1.053129947E-314;
            r12 = "AutoDownloadGroups";
            r11 = org.telegram.messenger.LocaleController.getString(r12, r11);
            r8.append(r11);
            goto L_0x0187;
        L_0x016e:
            r11 = NUM; // 0x7f0d016b float:1.874285E38 double:1.053129957E-314;
            r12 = "AutoDownloadPm";
            r11 = org.telegram.messenger.LocaleController.getString(r12, r11);
            r8.append(r11);
            goto L_0x0187;
        L_0x017b:
            r11 = NUM; // 0x7f0d0151 float:1.8742798E38 double:1.053129944E-314;
            r12 = "AutoDownloadContacts";
            r11 = org.telegram.messenger.LocaleController.getString(r12, r11);
            r8.append(r11);
        L_0x0187:
            r10 = r10 + 1;
        L_0x0189:
            r9 = r9 + 1;
            goto L_0x0135;
        L_0x018c:
            if (r10 != r4) goto L_0x01bc;
        L_0x018e:
            r8.setLength(r1);
            r14 = org.telegram.ui.DataAutoDownloadActivity.this;
            r14 = r14.photosRow;
            if (r15 != r14) goto L_0x01a6;
        L_0x0199:
            r14 = NUM; // 0x7f0d015f float:1.8742827E38 double:1.053129951E-314;
            r0 = "AutoDownloadOnAllChats";
            r14 = org.telegram.messenger.LocaleController.getString(r0, r14);
            r8.append(r14);
            goto L_0x01ca;
        L_0x01a6:
            r14 = NUM; // 0x7f0d0171 float:1.8742863E38 double:1.05312996E-314;
            r0 = new java.lang.Object[r2];
            r3 = (long) r7;
            r3 = org.telegram.messenger.AndroidUtilities.formatFileSize(r3);
            r0[r1] = r3;
            r3 = "AutoDownloadUpToOnAllChats";
            r14 = org.telegram.messenger.LocaleController.formatString(r3, r14, r0);
            r8.append(r14);
            goto L_0x01ca;
        L_0x01bc:
            if (r10 != 0) goto L_0x01cc;
        L_0x01be:
            r14 = NUM; // 0x7f0d015e float:1.8742825E38 double:1.0531299505E-314;
            r0 = "AutoDownloadOff";
            r14 = org.telegram.messenger.LocaleController.getString(r0, r14);
            r8.append(r14);
        L_0x01ca:
            r7 = r8;
            goto L_0x0209;
        L_0x01cc:
            r14 = org.telegram.ui.DataAutoDownloadActivity.this;
            r14 = r14.photosRow;
            if (r15 != r14) goto L_0x01eb;
        L_0x01d4:
            r14 = new java.lang.StringBuilder;
            r0 = NUM; // 0x7f0d0160 float:1.8742829E38 double:1.0531299515E-314;
            r3 = new java.lang.Object[r2];
            r4 = r8.toString();
            r3[r1] = r4;
            r4 = "AutoDownloadOnFor";
            r0 = org.telegram.messenger.LocaleController.formatString(r4, r0, r3);
            r14.<init>(r0);
            goto L_0x0208;
        L_0x01eb:
            r14 = new java.lang.StringBuilder;
            r0 = NUM; // 0x7f0d0165 float:1.8742839E38 double:1.053129954E-314;
            r3 = new java.lang.Object[r3];
            r11 = (long) r7;
            r4 = org.telegram.messenger.AndroidUtilities.formatFileSize(r11);
            r3[r1] = r4;
            r4 = r8.toString();
            r3[r2] = r4;
            r4 = "AutoDownloadOnUpToFor";
            r0 = org.telegram.messenger.LocaleController.formatString(r4, r0, r3);
            r14.<init>(r0);
        L_0x0208:
            r7 = r14;
        L_0x0209:
            r14 = org.telegram.ui.DataAutoDownloadActivity.this;
            r14 = r14.animateChecked;
            if (r14 == 0) goto L_0x0219;
        L_0x0211:
            if (r10 == 0) goto L_0x0215;
        L_0x0213:
            r14 = 1;
            goto L_0x0216;
        L_0x0215:
            r14 = 0;
        L_0x0216:
            r5.setChecked(r14);
        L_0x0219:
            if (r10 == 0) goto L_0x021d;
        L_0x021b:
            r8 = 1;
            goto L_0x021e;
        L_0x021d:
            r8 = 0;
        L_0x021e:
            r9 = 0;
            r10 = 1;
            r14 = org.telegram.ui.DataAutoDownloadActivity.this;
            r14 = r14.filesRow;
            if (r15 == r14) goto L_0x022a;
        L_0x0228:
            r11 = 1;
            goto L_0x022b;
        L_0x022a:
            r11 = 0;
        L_0x022b:
            r5.setTextAndValueAndCheck(r6, r7, r8, r9, r10, r11);
            goto L_0x02a8;
        L_0x0230:
            r14 = r14.itemView;
            r14 = (org.telegram.ui.Cells.HeaderCell) r14;
            r0 = org.telegram.ui.DataAutoDownloadActivity.this;
            r0 = r0.usageHeaderRow;
            if (r15 != r0) goto L_0x0249;
        L_0x023c:
            r15 = NUM; // 0x7f0d0153 float:1.8742802E38 double:1.053129945E-314;
            r0 = "AutoDownloadDataUsage";
            r15 = org.telegram.messenger.LocaleController.getString(r0, r15);
            r14.setText(r15);
            goto L_0x02a8;
        L_0x0249:
            r0 = org.telegram.ui.DataAutoDownloadActivity.this;
            r0 = r0.typeHeaderRow;
            if (r15 != r0) goto L_0x02a8;
        L_0x0251:
            r15 = NUM; // 0x7f0d0170 float:1.8742861E38 double:1.0531299594E-314;
            r0 = "AutoDownloadTypes";
            r15 = org.telegram.messenger.LocaleController.getString(r0, r15);
            r14.setText(r15);
            goto L_0x02a8;
        L_0x025e:
            r14 = r14.itemView;
            r14 = (org.telegram.ui.Cells.TextCheckCell) r14;
            r0 = org.telegram.ui.DataAutoDownloadActivity.this;
            r0 = r0.autoDownloadRow;
            if (r15 != r0) goto L_0x02a8;
        L_0x026a:
            r14.setDrawCheckRipple(r2);
            r15 = NUM; // 0x7f0d015c float:1.874282E38 double:1.0531299495E-314;
            r0 = "AutoDownloadMedia";
            r15 = org.telegram.messenger.LocaleController.getString(r0, r15);
            r0 = org.telegram.ui.DataAutoDownloadActivity.this;
            r0 = r0.typePreset;
            r0 = r0.enabled;
            r14.setTextAndCheck(r15, r0, r1);
            r15 = org.telegram.ui.DataAutoDownloadActivity.this;
            r15 = r15.typePreset;
            r15 = r15.enabled;
            r0 = "windowBackgroundChecked";
            r1 = "windowBackgroundUnchecked";
            if (r15 == 0) goto L_0x0291;
        L_0x028f:
            r15 = r0;
            goto L_0x0292;
        L_0x0291:
            r15 = r1;
        L_0x0292:
            r14.setTag(r15);
            r15 = org.telegram.ui.DataAutoDownloadActivity.this;
            r15 = r15.typePreset;
            r15 = r15.enabled;
            if (r15 == 0) goto L_0x02a0;
        L_0x029f:
            goto L_0x02a1;
        L_0x02a0:
            r0 = r1;
        L_0x02a1:
            r15 = org.telegram.ui.ActionBar.Theme.getColor(r0);
            r14.setBackgroundColor(r15);
        L_0x02a8:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DataAutoDownloadActivity$ListAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            int adapterPosition = viewHolder.getAdapterPosition();
            return adapterPosition == DataAutoDownloadActivity.this.photosRow || adapterPosition == DataAutoDownloadActivity.this.videosRow || adapterPosition == DataAutoDownloadActivity.this.filesRow;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View textCheckCell;
            if (i == 0) {
                textCheckCell = new TextCheckCell(this.mContext);
                textCheckCell.setColors("windowBackgroundCheckText", "switchTrackBlue", "switchTrackBlueChecked", "switchTrackBlueThumb", "switchTrackBlueThumbChecked");
                textCheckCell.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                textCheckCell.setHeight(56);
            } else if (i != 1) {
                String str = "windowBackgroundWhite";
                if (i == 2) {
                    textCheckCell = new HeaderCell(this.mContext);
                    textCheckCell.setBackgroundColor(Theme.getColor(str));
                } else if (i == 3) {
                    textCheckCell = new PresetChooseView(this.mContext);
                    textCheckCell.setBackgroundColor(Theme.getColor(str));
                } else if (i == 4) {
                    textCheckCell = new NotificationsCheckCell(this.mContext);
                    textCheckCell.setBackgroundColor(Theme.getColor(str));
                } else if (i != 5) {
                    textCheckCell = null;
                } else {
                    textCheckCell = new TextInfoPrivacyCell(this.mContext);
                    textCheckCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                }
            } else {
                textCheckCell = new ShadowSectionCell(this.mContext);
            }
            textCheckCell.setLayoutParams(new LayoutParams(-1, -2));
            return new Holder(textCheckCell);
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

    public DataAutoDownloadActivity(int i) {
        this.currentType = i;
        this.lowPreset = DownloadController.getInstance(this.currentAccount).lowPreset;
        this.mediumPreset = DownloadController.getInstance(this.currentAccount).mediumPreset;
        this.highPreset = DownloadController.getInstance(this.currentAccount).highPreset;
        i = this.currentType;
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
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    DataAutoDownloadActivity.this.finishFragment();
                }
            }
        });
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        this.listView = new RecyclerListView(context);
        this.listView.setVerticalScrollBarEnabled(false);
        ((DefaultItemAnimator) this.listView.getItemAnimator()).setDelayAnimations(false);
        RecyclerListView recyclerListView = this.listView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false);
        this.layoutManager = linearLayoutManager;
        recyclerListView.setLayoutManager(linearLayoutManager);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener(new -$$Lambda$DataAutoDownloadActivity$z-MZui0AcXTnHUC_YO0Sopv_aSo(this));
        return this.fragmentView;
    }

    /* JADX WARNING: Removed duplicated region for block: B:19:0x005e  */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x005c  */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x006b  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x0085  */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x007c  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x00bf  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x00b4  */
    public /* synthetic */ void lambda$createView$4$DataAutoDownloadActivity(android.view.View r30, int r31, float r32, float r33) {
        /*
        r29 = this;
        r12 = r29;
        r11 = r30;
        r8 = r31;
        r0 = r12.autoDownloadRow;
        r1 = 8;
        r9 = 2;
        r10 = 4;
        r13 = 3;
        r14 = 0;
        r15 = 1;
        if (r8 != r0) goto L_0x00eb;
    L_0x0011:
        r0 = r12.currentPresetNum;
        if (r0 == r13) goto L_0x0032;
    L_0x0015:
        if (r0 != 0) goto L_0x001f;
    L_0x0017:
        r0 = r12.typePreset;
        r2 = r12.lowPreset;
        r0.set(r2);
        goto L_0x0032;
    L_0x001f:
        if (r0 != r15) goto L_0x0029;
    L_0x0021:
        r0 = r12.typePreset;
        r2 = r12.mediumPreset;
        r0.set(r2);
        goto L_0x0032;
    L_0x0029:
        if (r0 != r9) goto L_0x0032;
    L_0x002b:
        r0 = r12.typePreset;
        r2 = r12.highPreset;
        r0.set(r2);
    L_0x0032:
        r0 = r11;
        r0 = (org.telegram.ui.Cells.TextCheckCell) r0;
        r2 = r0.isChecked();
        if (r2 != 0) goto L_0x004b;
    L_0x003b:
        r3 = r12.typePreset;
        r4 = r3.enabled;
        if (r4 == 0) goto L_0x004b;
    L_0x0041:
        r4 = r12.defaultPreset;
        r4 = r4.mask;
        r3 = r3.mask;
        java.lang.System.arraycopy(r4, r14, r3, r14, r10);
        goto L_0x0052;
    L_0x004b:
        r3 = r12.typePreset;
        r4 = r3.enabled;
        r4 = r4 ^ r15;
        r3.enabled = r4;
    L_0x0052:
        r3 = r12.typePreset;
        r3 = r3.enabled;
        r4 = "windowBackgroundChecked";
        r5 = "windowBackgroundUnchecked";
        if (r3 == 0) goto L_0x005e;
    L_0x005c:
        r3 = r4;
        goto L_0x005f;
    L_0x005e:
        r3 = r5;
    L_0x005f:
        r11.setTag(r3);
        r3 = r2 ^ 1;
        r6 = r12.typePreset;
        r6 = r6.enabled;
        if (r6 == 0) goto L_0x006b;
    L_0x006a:
        goto L_0x006c;
    L_0x006b:
        r4 = r5;
    L_0x006c:
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r4);
        r0.setBackgroundColorAnimated(r3, r4);
        r29.updateRows();
        r3 = r12.typePreset;
        r3 = r3.enabled;
        if (r3 == 0) goto L_0x0085;
    L_0x007c:
        r3 = r12.listAdapter;
        r4 = r12.autoDownloadSectionRow;
        r4 = r4 + r15;
        r3.notifyItemRangeInserted(r4, r1);
        goto L_0x008d;
    L_0x0085:
        r3 = r12.listAdapter;
        r4 = r12.autoDownloadSectionRow;
        r4 = r4 + r15;
        r3.notifyItemRangeRemoved(r4, r1);
    L_0x008d:
        r1 = r12.listAdapter;
        r3 = r12.autoDownloadSectionRow;
        r1.notifyItemChanged(r3);
        r1 = r12.currentAccount;
        r1 = org.telegram.messenger.MessagesController.getMainSettings(r1);
        r1 = r1.edit();
        r3 = r12.key;
        r4 = r12.typePreset;
        r4 = r4.toString();
        r1.putString(r3, r4);
        r3 = r12.key2;
        r12.currentPresetNum = r13;
        r1.putInt(r3, r13);
        r3 = r12.currentType;
        if (r3 != 0) goto L_0x00bf;
    L_0x00b4:
        r3 = r12.currentAccount;
        r3 = org.telegram.messenger.DownloadController.getInstance(r3);
        r4 = r12.currentPresetNum;
        r3.currentMobilePreset = r4;
        goto L_0x00d6;
    L_0x00bf:
        if (r3 != r15) goto L_0x00cc;
    L_0x00c1:
        r3 = r12.currentAccount;
        r3 = org.telegram.messenger.DownloadController.getInstance(r3);
        r4 = r12.currentPresetNum;
        r3.currentWifiPreset = r4;
        goto L_0x00d6;
    L_0x00cc:
        r3 = r12.currentAccount;
        r3 = org.telegram.messenger.DownloadController.getInstance(r3);
        r4 = r12.currentPresetNum;
        r3.currentRoamingPreset = r4;
    L_0x00d6:
        r1.commit();
        r1 = r2 ^ 1;
        r0.setChecked(r1);
        r0 = r12.currentAccount;
        r0 = org.telegram.messenger.DownloadController.getInstance(r0);
        r0.checkAutodownloadSettings();
        r12.wereAnyChanges = r15;
        goto L_0x05d1;
    L_0x00eb:
        r0 = r12.photosRow;
        if (r8 == r0) goto L_0x00f7;
    L_0x00ef:
        r0 = r12.videosRow;
        if (r8 == r0) goto L_0x00f7;
    L_0x00f3:
        r0 = r12.filesRow;
        if (r8 != r0) goto L_0x05d1;
    L_0x00f7:
        r0 = r30.isEnabled();
        if (r0 != 0) goto L_0x00fe;
    L_0x00fd:
        return;
    L_0x00fe:
        r0 = r12.photosRow;
        if (r8 != r0) goto L_0x0105;
    L_0x0102:
        r16 = 1;
        goto L_0x010e;
    L_0x0105:
        r0 = r12.videosRow;
        if (r8 != r0) goto L_0x010c;
    L_0x0109:
        r16 = 4;
        goto L_0x010e;
    L_0x010c:
        r16 = 8;
    L_0x010e:
        r17 = org.telegram.messenger.DownloadController.typeToIndex(r16);
        r0 = r12.currentType;
        if (r0 != 0) goto L_0x0128;
    L_0x0116:
        r0 = r12.currentAccount;
        r0 = org.telegram.messenger.DownloadController.getInstance(r0);
        r0 = r0.getCurrentMobilePreset();
        r1 = "mobilePreset";
        r2 = "currentMobilePreset";
    L_0x0124:
        r7 = r0;
        r6 = r1;
        r5 = r2;
        goto L_0x0148;
    L_0x0128:
        if (r0 != r15) goto L_0x0139;
    L_0x012a:
        r0 = r12.currentAccount;
        r0 = org.telegram.messenger.DownloadController.getInstance(r0);
        r0 = r0.getCurrentWiFiPreset();
        r1 = "wifiPreset";
        r2 = "currentWifiPreset";
        goto L_0x0124;
    L_0x0139:
        r0 = r12.currentAccount;
        r0 = org.telegram.messenger.DownloadController.getInstance(r0);
        r0 = r0.getCurrentRoamingPreset();
        r1 = "roamingPreset";
        r2 = "currentRoamingPreset";
        goto L_0x0124;
    L_0x0148:
        r0 = r11;
        r0 = (org.telegram.ui.Cells.NotificationsCheckCell) r0;
        r1 = r0.isChecked();
        r2 = org.telegram.messenger.LocaleController.isRTL;
        r3 = NUM; // 0x42980000 float:76.0 double:5.51998661E-315;
        if (r2 == 0) goto L_0x015e;
    L_0x0155:
        r2 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r2 = (float) r2;
        r2 = (r32 > r2 ? 1 : (r32 == r2 ? 0 : -1));
        if (r2 <= 0) goto L_0x0170;
    L_0x015e:
        r2 = org.telegram.messenger.LocaleController.isRTL;
        if (r2 != 0) goto L_0x0225;
    L_0x0162:
        r2 = r30.getMeasuredWidth();
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r2 = r2 - r3;
        r2 = (float) r2;
        r2 = (r32 > r2 ? 1 : (r32 == r2 ? 0 : -1));
        if (r2 < 0) goto L_0x0225;
    L_0x0170:
        r2 = r12.currentPresetNum;
        if (r2 == r13) goto L_0x0191;
    L_0x0174:
        if (r2 != 0) goto L_0x017e;
    L_0x0176:
        r2 = r12.typePreset;
        r3 = r12.lowPreset;
        r2.set(r3);
        goto L_0x0191;
    L_0x017e:
        if (r2 != r15) goto L_0x0188;
    L_0x0180:
        r2 = r12.typePreset;
        r3 = r12.mediumPreset;
        r2.set(r3);
        goto L_0x0191;
    L_0x0188:
        if (r2 != r9) goto L_0x0191;
    L_0x018a:
        r2 = r12.typePreset;
        r3 = r12.highPreset;
        r2.set(r3);
    L_0x0191:
        r2 = 0;
    L_0x0192:
        r3 = r12.typePreset;
        r3 = r3.mask;
        r3 = r3.length;
        if (r2 >= r3) goto L_0x01a6;
    L_0x0199:
        r3 = r7.mask;
        r3 = r3[r2];
        r3 = r3 & r16;
        if (r3 == 0) goto L_0x01a3;
    L_0x01a1:
        r2 = 1;
        goto L_0x01a7;
    L_0x01a3:
        r2 = r2 + 1;
        goto L_0x0192;
    L_0x01a6:
        r2 = 0;
    L_0x01a7:
        r3 = r12.typePreset;
        r3 = r3.mask;
        r4 = r3.length;
        if (r14 >= r4) goto L_0x01c3;
    L_0x01ae:
        if (r1 == 0) goto L_0x01b8;
    L_0x01b0:
        r4 = r3[r14];
        r7 = r16 ^ -1;
        r4 = r4 & r7;
        r3[r14] = r4;
        goto L_0x01c0;
    L_0x01b8:
        if (r2 != 0) goto L_0x01c0;
    L_0x01ba:
        r4 = r3[r14];
        r4 = r4 | r16;
        r3[r14] = r4;
    L_0x01c0:
        r14 = r14 + 1;
        goto L_0x01a7;
    L_0x01c3:
        r2 = r12.currentAccount;
        r2 = org.telegram.messenger.MessagesController.getMainSettings(r2);
        r2 = r2.edit();
        r3 = r12.typePreset;
        r3 = r3.toString();
        r2.putString(r6, r3);
        r12.currentPresetNum = r13;
        r2.putInt(r5, r13);
        r3 = r12.currentType;
        if (r3 != 0) goto L_0x01ea;
    L_0x01df:
        r3 = r12.currentAccount;
        r3 = org.telegram.messenger.DownloadController.getInstance(r3);
        r4 = r12.currentPresetNum;
        r3.currentMobilePreset = r4;
        goto L_0x0201;
    L_0x01ea:
        if (r3 != r15) goto L_0x01f7;
    L_0x01ec:
        r3 = r12.currentAccount;
        r3 = org.telegram.messenger.DownloadController.getInstance(r3);
        r4 = r12.currentPresetNum;
        r3.currentWifiPreset = r4;
        goto L_0x0201;
    L_0x01f7:
        r3 = r12.currentAccount;
        r3 = org.telegram.messenger.DownloadController.getInstance(r3);
        r4 = r12.currentPresetNum;
        r3.currentRoamingPreset = r4;
    L_0x0201:
        r2.commit();
        r1 = r1 ^ r15;
        r0.setChecked(r1);
        r0 = r12.listView;
        r0 = r0.findContainingViewHolder(r11);
        if (r0 == 0) goto L_0x0215;
    L_0x0210:
        r1 = r12.listAdapter;
        r1.onBindViewHolder(r0, r8);
    L_0x0215:
        r0 = r12.currentAccount;
        r0 = org.telegram.messenger.DownloadController.getInstance(r0);
        r0.checkAutodownloadSettings();
        r12.wereAnyChanges = r15;
        r29.fillPresets();
        goto L_0x05d1;
    L_0x0225:
        r0 = r29.getParentActivity();
        if (r0 != 0) goto L_0x022c;
    L_0x022b:
        return;
    L_0x022c:
        r4 = new org.telegram.ui.ActionBar.BottomSheet$Builder;
        r0 = r29.getParentActivity();
        r4.<init>(r0);
        r4.setApplyTopPadding(r14);
        r4.setApplyBottomPadding(r14);
        r3 = new android.widget.LinearLayout;
        r0 = r29.getParentActivity();
        r3.<init>(r0);
        r3.setOrientation(r15);
        r4.setCustomView(r3);
        r0 = new org.telegram.ui.Cells.HeaderCell;
        r19 = r29.getParentActivity();
        r20 = 1;
        r21 = 21;
        r22 = 15;
        r23 = 0;
        r18 = r0;
        r18.<init>(r19, r20, r21, r22, r23);
        r1 = r12.photosRow;
        if (r8 != r1) goto L_0x026e;
    L_0x0261:
        r1 = NUM; // 0x7f0d016a float:1.8742849E38 double:1.0531299564E-314;
        r2 = "AutoDownloadPhotosTitle";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        r0.setText(r1);
        goto L_0x028b;
    L_0x026e:
        r1 = r12.videosRow;
        if (r8 != r1) goto L_0x027f;
    L_0x0272:
        r1 = NUM; // 0x7f0d0174 float:1.874287E38 double:1.0531299613E-314;
        r2 = "AutoDownloadVideosTitle";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        r0.setText(r1);
        goto L_0x028b;
    L_0x027f:
        r1 = NUM; // 0x7f0d0156 float:1.8742808E38 double:1.0531299465E-314;
        r2 = "AutoDownloadFilesTitle";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        r0.setText(r1);
    L_0x028b:
        r1 = -NUM; // 0xffffffffCLASSNAME float:-2.0 double:NaN;
        r2 = -1;
        r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r2, r1);
        r3.addView(r0, r1);
        r1 = new org.telegram.ui.Cells.MaxFileSizeCell[r15];
        r0 = new org.telegram.ui.Cells.TextCheckCell[r15];
        r13 = new android.animation.AnimatorSet[r15];
        r9 = new org.telegram.ui.Cells.TextCheckBoxCell[r10];
    L_0x029d:
        if (r14 >= r10) goto L_0x0386;
    L_0x029f:
        r2 = new org.telegram.ui.Cells.TextCheckBoxCell;
        r10 = r29.getParentActivity();
        r2.<init>(r10, r15);
        r9[r14] = r2;
        if (r14 != 0) goto L_0x02d0;
    L_0x02ac:
        r10 = r9[r14];
        r15 = NUM; // 0x7f0d0188 float:1.874291E38 double:1.053129971E-314;
        r22 = r0;
        r0 = "AutodownloadContacts";
        r0 = org.telegram.messenger.LocaleController.getString(r0, r15);
        r15 = r7.mask;
        r19 = 0;
        r15 = r15[r19];
        r15 = r15 & r16;
        if (r15 == 0) goto L_0x02c8;
    L_0x02c3:
        r23 = r1;
        r1 = 1;
        r15 = 1;
        goto L_0x02cc;
    L_0x02c8:
        r23 = r1;
        r1 = 1;
        r15 = 0;
    L_0x02cc:
        r10.setTextAndCheck(r0, r15, r1);
        goto L_0x0333;
    L_0x02d0:
        r22 = r0;
        r23 = r1;
        r1 = 1;
        if (r14 != r1) goto L_0x02f1;
    L_0x02d7:
        r0 = r9[r14];
        r10 = NUM; // 0x7f0d018a float:1.8742914E38 double:1.053129972E-314;
        r15 = "AutodownloadPrivateChats";
        r10 = org.telegram.messenger.LocaleController.getString(r15, r10);
        r15 = r7.mask;
        r15 = r15[r1];
        r15 = r15 & r16;
        if (r15 == 0) goto L_0x02ec;
    L_0x02ea:
        r15 = 1;
        goto L_0x02ed;
    L_0x02ec:
        r15 = 0;
    L_0x02ed:
        r0.setTextAndCheck(r10, r15, r1);
        goto L_0x0333;
    L_0x02f1:
        r10 = 2;
        if (r14 != r10) goto L_0x0310;
    L_0x02f4:
        r0 = r9[r14];
        r1 = NUM; // 0x7f0d0189 float:1.8742912E38 double:1.0531299717E-314;
        r15 = "AutodownloadGroupChats";
        r1 = org.telegram.messenger.LocaleController.getString(r15, r1);
        r15 = r7.mask;
        r15 = r15[r10];
        r15 = r15 & r16;
        if (r15 == 0) goto L_0x030a;
    L_0x0307:
        r10 = 1;
        r15 = 1;
        goto L_0x030c;
    L_0x030a:
        r10 = 1;
        r15 = 0;
    L_0x030c:
        r0.setTextAndCheck(r1, r15, r10);
        goto L_0x0333;
    L_0x0310:
        r10 = 3;
        if (r14 != r10) goto L_0x0333;
    L_0x0313:
        r0 = r9[r14];
        r1 = NUM; // 0x7f0d0187 float:1.8742908E38 double:1.0531299707E-314;
        r15 = "AutodownloadChannels";
        r1 = org.telegram.messenger.LocaleController.getString(r15, r1);
        r15 = r7.mask;
        r15 = r15[r10];
        r15 = r15 & r16;
        if (r15 == 0) goto L_0x0328;
    L_0x0326:
        r15 = 1;
        goto L_0x0329;
    L_0x0328:
        r15 = 0;
    L_0x0329:
        r10 = r12.photosRow;
        if (r8 == r10) goto L_0x032f;
    L_0x032d:
        r10 = 1;
        goto L_0x0330;
    L_0x032f:
        r10 = 0;
    L_0x0330:
        r0.setTextAndCheck(r1, r15, r10);
    L_0x0333:
        r0 = r9[r14];
        r1 = 0;
        r10 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r1);
        r0.setBackgroundDrawable(r10);
        r10 = r9[r14];
        r15 = new org.telegram.ui.-$$Lambda$DataAutoDownloadActivity$GvlKaEmbPmrPjkZgpelfWkoLNuU;
        r1 = r22;
        r0 = r15;
        r22 = r23;
        r23 = r1;
        r1 = r29;
        r11 = -1;
        r24 = r3;
        r3 = r9;
        r25 = r4;
        r4 = r31;
        r26 = r5;
        r5 = r22;
        r27 = r6;
        r6 = r23;
        r28 = r7;
        r7 = r13;
        r0.<init>(r1, r2, r3, r4, r5, r6, r7);
        r10.setOnClickListener(r15);
        r0 = r9[r14];
        r1 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r1);
        r7 = r24;
        r7.addView(r0, r1);
        r14 = r14 + 1;
        r11 = r30;
        r3 = r7;
        r1 = r22;
        r0 = r23;
        r4 = r25;
        r5 = r26;
        r6 = r27;
        r7 = r28;
        r2 = -1;
        r10 = 4;
        r15 = 1;
        goto L_0x029d;
    L_0x0386:
        r23 = r0;
        r22 = r1;
        r25 = r4;
        r26 = r5;
        r27 = r6;
        r28 = r7;
        r11 = -1;
        r7 = r3;
        r0 = r12.photosRow;
        r10 = -2;
        r14 = 0;
        if (r8 == r0) goto L_0x048a;
    L_0x039a:
        r15 = new org.telegram.ui.Cells.TextInfoPrivacyCell;
        r0 = r29.getParentActivity();
        r15.<init>(r0);
        r18 = new org.telegram.ui.DataAutoDownloadActivity$3;
        r2 = r29.getParentActivity();
        r0 = r18;
        r1 = r29;
        r3 = r31;
        r4 = r15;
        r5 = r23;
        r6 = r13;
        r0.<init>(r2, r3, r4, r5, r6);
        r0 = 0;
        r22[r0] = r18;
        r1 = r22[r0];
        r2 = r28;
        r3 = r2.sizes;
        r3 = r3[r17];
        r3 = (long) r3;
        r1.setSize(r3);
        r1 = r22[r0];
        r3 = 50;
        r3 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r3);
        r7.addView(r1, r3);
        r1 = new org.telegram.ui.Cells.TextCheckCell;
        r3 = r29.getParentActivity();
        r4 = 21;
        r5 = 1;
        r1.<init>(r3, r4, r5);
        r6 = r23;
        r6[r0] = r1;
        r1 = r6[r0];
        r3 = 48;
        r3 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r3);
        r7.addView(r1, r3);
        r1 = r6[r0];
        r0 = new org.telegram.ui.-$$Lambda$DataAutoDownloadActivity$m0gzCqbXGWHGQEKzinvKQh1h1B0;
        r0.<init>(r6);
        r1.setOnClickListener(r0);
        r0 = r29.getParentActivity();
        r1 = NUM; // 0x7var_e4 float:1.794504E38 double:1.0529356157E-314;
        r3 = "windowBackgroundGrayShadow";
        r0 = org.telegram.ui.ActionBar.Theme.getThemedDrawable(r0, r1, r3);
        r1 = new org.telegram.ui.Components.CombinedDrawable;
        r3 = new android.graphics.drawable.ColorDrawable;
        r4 = "windowBackgroundGray";
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r4);
        r3.<init>(r4);
        r1.<init>(r3, r0);
        r0 = 1;
        r1.setFullsize(r0);
        r15.setBackgroundDrawable(r1);
        r0 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r10);
        r7.addView(r15, r0);
        r0 = r12.videosRow;
        if (r8 != r0) goto L_0x045e;
    L_0x0424:
        r0 = 0;
        r1 = r22[r0];
        r3 = NUM; // 0x7f0d015b float:1.8742819E38 double:1.053129949E-314;
        r4 = "AutoDownloadMaxVideoSize";
        r3 = org.telegram.messenger.LocaleController.getString(r4, r3);
        r1.setText(r3);
        r1 = r6[r0];
        r3 = NUM; // 0x7f0d016e float:1.8742857E38 double:1.0531299584E-314;
        r4 = "AutoDownloadPreloadVideo";
        r3 = org.telegram.messenger.LocaleController.getString(r4, r3);
        r4 = r2.preloadVideo;
        r1.setTextAndCheck(r3, r4, r0);
        r1 = NUM; // 0x7f0d016f float:1.874286E38 double:1.053129959E-314;
        r3 = 1;
        r4 = new java.lang.Object[r3];
        r3 = r2.sizes;
        r3 = r3[r17];
        r10 = (long) r3;
        r3 = org.telegram.messenger.AndroidUtilities.formatFileSize(r10);
        r4[r0] = r3;
        r3 = "AutoDownloadPreloadVideoInfo";
        r1 = org.telegram.messenger.LocaleController.formatString(r3, r1, r4);
        r15.setText(r1);
        goto L_0x04af;
    L_0x045e:
        r0 = 0;
        r1 = r22[r0];
        r3 = NUM; // 0x7f0d015a float:1.8742817E38 double:1.0531299485E-314;
        r4 = "AutoDownloadMaxFileSize";
        r3 = org.telegram.messenger.LocaleController.getString(r4, r3);
        r1.setText(r3);
        r1 = r6[r0];
        r3 = NUM; // 0x7f0d016c float:1.8742853E38 double:1.0531299574E-314;
        r4 = "AutoDownloadPreloadMusic";
        r3 = org.telegram.messenger.LocaleController.getString(r4, r3);
        r4 = r2.preloadMusic;
        r1.setTextAndCheck(r3, r4, r0);
        r1 = NUM; // 0x7f0d016d float:1.8742855E38 double:1.053129958E-314;
        r3 = "AutoDownloadPreloadMusicInfo";
        r1 = org.telegram.messenger.LocaleController.getString(r3, r1);
        r15.setText(r1);
        goto L_0x04af;
    L_0x048a:
        r6 = r23;
        r2 = r28;
        r0 = 0;
        r22[r0] = r14;
        r6[r0] = r14;
        r0 = new android.view.View;
        r1 = r29.getParentActivity();
        r0.<init>(r1);
        r1 = "divider";
        r1 = org.telegram.ui.ActionBar.Theme.getColor(r1);
        r0.setBackgroundColor(r1);
        r1 = new android.widget.LinearLayout$LayoutParams;
        r3 = -1;
        r4 = 1;
        r1.<init>(r3, r4);
        r7.addView(r0, r1);
    L_0x04af:
        r0 = r12.videosRow;
        if (r8 != r0) goto L_0x04e1;
    L_0x04b3:
        r0 = 0;
    L_0x04b4:
        r1 = r9.length;
        if (r0 >= r1) goto L_0x04c4;
    L_0x04b7:
        r1 = r9[r0];
        r1 = r1.isChecked();
        if (r1 == 0) goto L_0x04c1;
    L_0x04bf:
        r0 = 1;
        goto L_0x04c5;
    L_0x04c1:
        r0 = r0 + 1;
        goto L_0x04b4;
    L_0x04c4:
        r0 = 0;
    L_0x04c5:
        if (r0 != 0) goto L_0x04d3;
    L_0x04c7:
        r1 = 0;
        r3 = r22[r1];
        r3.setEnabled(r0, r14);
        r3 = r6[r1];
        r3.setEnabled(r0, r14);
        goto L_0x04d4;
    L_0x04d3:
        r1 = 0;
    L_0x04d4:
        r0 = r2.sizes;
        r0 = r0[r17];
        r2 = 2097152; // 0x200000 float:2.938736E-39 double:1.0361308E-317;
        if (r0 > r2) goto L_0x04e1;
    L_0x04dc:
        r0 = r6[r1];
        r0.setEnabled(r1, r14);
    L_0x04e1:
        r0 = new android.widget.FrameLayout;
        r1 = r29.getParentActivity();
        r0.<init>(r1);
        r1 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r3 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r4 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r0.setPadding(r2, r3, r4, r1);
        r1 = 52;
        r2 = -1;
        r1 = org.telegram.ui.Components.LayoutHelper.createLinear(r2, r1);
        r7.addView(r0, r1);
        r1 = new android.widget.TextView;
        r2 = r29.getParentActivity();
        r1.<init>(r2);
        r2 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r3 = 1;
        r1.setTextSize(r3, r2);
        r3 = "dialogTextBlue2";
        r3 = org.telegram.ui.ActionBar.Theme.getColor(r3);
        r1.setTextColor(r3);
        r3 = 17;
        r1.setGravity(r3);
        r3 = "fonts/rmedium.ttf";
        r3 = org.telegram.messenger.AndroidUtilities.getTypeface(r3);
        r1.setTypeface(r3);
        r3 = NUM; // 0x7f0d01dd float:1.8743082E38 double:1.053130013E-314;
        r4 = "Cancel";
        r3 = org.telegram.messenger.LocaleController.getString(r4, r3);
        r3 = r3.toUpperCase();
        r1.setText(r3);
        r3 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r5 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r7 = 0;
        r1.setPadding(r4, r7, r5, r7);
        r4 = 36;
        r5 = 51;
        r7 = -2;
        r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r4, r5);
        r0.addView(r1, r4);
        r4 = new org.telegram.ui.-$$Lambda$DataAutoDownloadActivity$p8_fIkB1xZiV8Kgpv8cx4j3wbxY;
        r13 = r25;
        r4.<init>(r13);
        r1.setOnClickListener(r4);
        r14 = new android.widget.TextView;
        r1 = r29.getParentActivity();
        r14.<init>(r1);
        r1 = 1;
        r14.setTextSize(r1, r2);
        r1 = "dialogTextBlue2";
        r1 = org.telegram.ui.ActionBar.Theme.getColor(r1);
        r14.setTextColor(r1);
        r1 = 17;
        r14.setGravity(r1);
        r1 = "fonts/rmedium.ttf";
        r1 = org.telegram.messenger.AndroidUtilities.getTypeface(r1);
        r14.setTypeface(r1);
        r1 = NUM; // 0x7f0d0876 float:1.8746508E38 double:1.0531308477E-314;
        r2 = "Save";
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        r1 = r1.toUpperCase();
        r14.setText(r1);
        r1 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r2 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r3 = 0;
        r14.setPadding(r1, r3, r2, r3);
        r1 = 36;
        r2 = 53;
        r3 = -2;
        r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r3, r1, r2);
        r0.addView(r14, r1);
        r15 = new org.telegram.ui.-$$Lambda$DataAutoDownloadActivity$bStK8LWlLSXpGpzkzlpaQV4c9zo;
        r0 = r15;
        r1 = r29;
        r2 = r9;
        r3 = r16;
        r4 = r22;
        r5 = r17;
        r7 = r31;
        r8 = r27;
        r9 = r26;
        r10 = r13;
        r11 = r30;
        r0.<init>(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11);
        r14.setOnClickListener(r15);
        r0 = r13.create();
        r12.showDialog(r0);
    L_0x05d1:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DataAutoDownloadActivity.lambda$createView$4$DataAutoDownloadActivity(android.view.View, int, float, float):void");
    }

    public /* synthetic */ void lambda$null$0$DataAutoDownloadActivity(TextCheckBoxCell textCheckBoxCell, TextCheckBoxCell[] textCheckBoxCellArr, int i, MaxFileSizeCell[] maxFileSizeCellArr, TextCheckCell[] textCheckCellArr, final AnimatorSet[] animatorSetArr, View view) {
        if (view.isEnabled()) {
            boolean z = true;
            textCheckBoxCell.setChecked(textCheckBoxCell.isChecked() ^ 1);
            for (TextCheckBoxCell isChecked : textCheckBoxCellArr) {
                if (isChecked.isChecked()) {
                    break;
                }
            }
            z = false;
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

    public /* synthetic */ void lambda$null$3$DataAutoDownloadActivity(TextCheckBoxCell[] textCheckBoxCellArr, int i, MaxFileSizeCell[] maxFileSizeCellArr, int i2, TextCheckCell[] textCheckCellArr, int i3, String str, String str2, Builder builder, View view, View view2) {
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
            int[] iArr;
            if (textCheckBoxCellArr[i6].isChecked()) {
                iArr = this.typePreset.mask;
                iArr[i6] = iArr[i6] | i;
            } else {
                iArr = this.typePreset.mask;
                iArr[i6] = iArr[i6] & (i ^ -1);
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
        Editor edit = MessagesController.getMainSettings(this.currentAccount).edit();
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
        ViewHolder findContainingViewHolder = this.listView.findContainingViewHolder(view);
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
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
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
        if (!(this.typePreset.equals(this.lowPreset) || this.typePreset.equals(this.mediumPreset) || this.typePreset.equals(this.highPreset))) {
            this.presets.add(this.typePreset);
        }
        Collections.sort(this.presets, -$$Lambda$DataAutoDownloadActivity$E0PVxdOLHPC3ZjO_nDkt8nGBYVw.INSTANCE);
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            ViewHolder findViewHolderForAdapterPosition = recyclerListView.findViewHolderForAdapterPosition(this.usageProgressRow);
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
        i = this.currentPresetNum;
        if (i == 1 || (i == 3 && this.typePreset.equals(this.mediumPreset))) {
            this.selectedPreset = this.presets.indexOf(this.mediumPreset);
            return;
        }
        i = this.currentPresetNum;
        if (i == 2 || (i == 3 && this.typePreset.equals(this.highPreset))) {
            this.selectedPreset = this.presets.indexOf(this.highPreset);
        } else {
            this.selectedPreset = this.presets.indexOf(this.typePreset);
        }
    }

    static /* synthetic */ int lambda$fillPresets$5(Preset preset, Preset preset2) {
        int typeToIndex = DownloadController.typeToIndex(4);
        int typeToIndex2 = DownloadController.typeToIndex(8);
        int i = 0;
        Object obj = null;
        Object obj2 = null;
        while (true) {
            int[] iArr = preset.mask;
            if (i < iArr.length) {
                if ((iArr[i] & 4) != 0) {
                    obj = 1;
                }
                if ((preset.mask[i] & 8) != 0) {
                    obj2 = 1;
                }
                if (obj != null && r7 != null) {
                    break;
                }
                i++;
            } else {
                break;
            }
        }
        i = 0;
        Object obj3 = null;
        Object obj4 = null;
        while (true) {
            int[] iArr2 = preset2.mask;
            if (i < iArr2.length) {
                if ((iArr2[i] & 4) != 0) {
                    obj3 = 1;
                }
                if ((preset2.mask[i] & 8) != 0) {
                    obj4 = 1;
                }
                if (obj3 != null && r9 != null) {
                    break;
                }
                i++;
            } else {
                break;
            }
        }
        int i2 = (obj != null ? preset.sizes[typeToIndex] : 0) + (obj2 != null ? preset.sizes[typeToIndex2] : 0);
        int i3 = (obj3 != null ? preset2.sizes[typeToIndex] : 0) + (obj4 != null ? preset2.sizes[typeToIndex2] : 0);
        if (i2 > i3) {
            return 1;
        }
        if (i2 < i3) {
            return -1;
        }
        return 0;
    }

    private void updateRows() {
        this.rowCount = 0;
        int i = this.rowCount;
        this.rowCount = i + 1;
        this.autoDownloadRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.autoDownloadSectionRow = i;
        if (this.typePreset.enabled) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.usageHeaderRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.usageProgressRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.usageSectionRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.typeHeaderRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.photosRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.videosRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.filesRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.typeSectionRow = i;
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

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[29];
        themeDescriptionArr[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{HeaderCell.class, NotificationsCheckCell.class, PresetChooseView.class}, null, null, null, "windowBackgroundWhite");
        themeDescriptionArr[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray");
        themeDescriptionArr[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
        themeDescriptionArr[3] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
        themeDescriptionArr[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
        themeDescriptionArr[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
        themeDescriptionArr[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
        themeDescriptionArr[7] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
        themeDescriptionArr[8] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, "divider");
        themeDescriptionArr[9] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, "windowBackgroundGrayShadow");
        View view = this.listView;
        Class[] clsArr = new Class[]{HeaderCell.class};
        String[] strArr = new String[1];
        strArr[0] = "textView";
        themeDescriptionArr[10] = new ThemeDescription(view, 0, clsArr, strArr, null, null, null, "windowBackgroundWhiteBlueHeader");
        themeDescriptionArr[11] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCheckCell.class}, null, null, null, "windowBackgroundChecked");
        themeDescriptionArr[12] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCheckCell.class}, null, null, null, "windowBackgroundUnchecked");
        themeDescriptionArr[13] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundCheckText");
        view = this.listView;
        clsArr = new Class[]{TextCheckCell.class};
        strArr = new String[1];
        strArr[0] = "checkBox";
        themeDescriptionArr[14] = new ThemeDescription(view, 0, clsArr, strArr, null, null, null, "switchTrackBlue");
        themeDescriptionArr[15] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, "switchTrackBlueChecked");
        themeDescriptionArr[16] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, "switchTrackBlueThumb");
        themeDescriptionArr[17] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, "switchTrackBlueThumbChecked");
        themeDescriptionArr[18] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, "switchTrackBlueSelector");
        themeDescriptionArr[19] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, "switchTrackBlueSelectorChecked");
        themeDescriptionArr[20] = new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[21] = new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"valueTextView"}, null, null, null, "windowBackgroundWhiteGrayText2");
        themeDescriptionArr[22] = new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, null, null, null, "switchTrack");
        themeDescriptionArr[23] = new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, null, null, null, "switchTrackChecked");
        themeDescriptionArr[24] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, "windowBackgroundGrayShadow");
        themeDescriptionArr[25] = new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteGrayText4");
        themeDescriptionArr[26] = new ThemeDescription(this.listView, 0, new Class[]{PresetChooseView.class}, null, null, null, "switchTrack");
        themeDescriptionArr[27] = new ThemeDescription(this.listView, 0, new Class[]{PresetChooseView.class}, null, null, null, "switchTrackChecked");
        themeDescriptionArr[28] = new ThemeDescription(this.listView, 0, new Class[]{PresetChooseView.class}, null, null, null, "windowBackgroundWhiteGrayText");
        return themeDescriptionArr;
    }
}
