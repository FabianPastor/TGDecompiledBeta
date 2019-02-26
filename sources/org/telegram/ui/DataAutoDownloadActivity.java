package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Collections;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.DownloadController.Preset;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.support.widget.DefaultItemAnimator;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
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
import org.telegram.ui.Components.CombinedDrawable;
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

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return DataAutoDownloadActivity.this.rowCount;
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 0:
                    TextCheckCell view = holder.itemView;
                    if (position == DataAutoDownloadActivity.this.autoDownloadRow) {
                        view.setDrawCheckRipple(true);
                        view.setTextAndCheck(LocaleController.getString("AutoDownloadMedia", R.string.AutoDownloadMedia), DataAutoDownloadActivity.this.typePreset.enabled, false);
                        view.setTag(DataAutoDownloadActivity.this.typePreset.enabled ? "windowBackgroundChecked" : "windowBackgroundUnchecked");
                        view.setBackgroundColor(Theme.getColor(DataAutoDownloadActivity.this.typePreset.enabled ? "windowBackgroundChecked" : "windowBackgroundUnchecked"));
                        return;
                    }
                    return;
                case 2:
                    HeaderCell view2 = holder.itemView;
                    if (position == DataAutoDownloadActivity.this.usageHeaderRow) {
                        view2.setText(LocaleController.getString("AutoDownloadDataUsage", R.string.AutoDownloadDataUsage));
                        return;
                    } else if (position == DataAutoDownloadActivity.this.typeHeaderRow) {
                        view2.setText(LocaleController.getString("AutoDownloadTypes", R.string.AutoDownloadTypes));
                        return;
                    } else {
                        return;
                    }
                case 4:
                    String text;
                    int type;
                    Preset preset;
                    boolean z;
                    boolean z2;
                    NotificationsCheckCell view3 = holder.itemView;
                    if (position == DataAutoDownloadActivity.this.photosRow) {
                        text = LocaleController.getString("AutoDownloadPhotos", R.string.AutoDownloadPhotos);
                        type = 1;
                    } else if (position == DataAutoDownloadActivity.this.videosRow) {
                        text = LocaleController.getString("AutoDownloadVideos", R.string.AutoDownloadVideos);
                        type = 4;
                    } else {
                        text = LocaleController.getString("AutoDownloadFiles", R.string.AutoDownloadFiles);
                        type = 8;
                    }
                    if (DataAutoDownloadActivity.this.currentType == 0) {
                        preset = DownloadController.getInstance(DataAutoDownloadActivity.this.currentAccount).getCurrentMobilePreset();
                    } else if (DataAutoDownloadActivity.this.currentType == 1) {
                        preset = DownloadController.getInstance(DataAutoDownloadActivity.this.currentAccount).getCurrentWiFiPreset();
                    } else {
                        preset = DownloadController.getInstance(DataAutoDownloadActivity.this.currentAccount).getCurrentRoamingPreset();
                    }
                    int maxSize = preset.sizes[DownloadController.typeToIndex(type)];
                    int count = 0;
                    StringBuilder builder = new StringBuilder();
                    for (int a = 0; a < preset.mask.length; a++) {
                        if ((preset.mask[a] & type) != 0) {
                            if (builder.length() != 0) {
                                builder.append(", ");
                            }
                            switch (a) {
                                case 0:
                                    builder.append(LocaleController.getString("AutoDownloadContacts", R.string.AutoDownloadContacts));
                                    break;
                                case 1:
                                    builder.append(LocaleController.getString("AutoDownloadPm", R.string.AutoDownloadPm));
                                    break;
                                case 2:
                                    builder.append(LocaleController.getString("AutoDownloadGroups", R.string.AutoDownloadGroups));
                                    break;
                                case 3:
                                    builder.append(LocaleController.getString("AutoDownloadChannels", R.string.AutoDownloadChannels));
                                    break;
                            }
                            count++;
                        }
                    }
                    if (count == 4) {
                        builder.setLength(0);
                        if (position == DataAutoDownloadActivity.this.photosRow) {
                            builder.append(LocaleController.getString("AutoDownloadOnAllChats", R.string.AutoDownloadOnAllChats));
                        } else {
                            builder.append(LocaleController.formatString("AutoDownloadUpToOnAllChats", R.string.AutoDownloadUpToOnAllChats, AndroidUtilities.formatFileSize((long) maxSize)));
                        }
                    } else if (count == 0) {
                        builder.append(LocaleController.getString("AutoDownloadOff", R.string.AutoDownloadOff));
                    } else {
                        builder = position == DataAutoDownloadActivity.this.photosRow ? new StringBuilder(LocaleController.formatString("AutoDownloadOnFor", R.string.AutoDownloadOnFor, builder.toString())) : new StringBuilder(LocaleController.formatString("AutoDownloadOnUpToFor", R.string.AutoDownloadOnUpToFor, AndroidUtilities.formatFileSize((long) maxSize), builder.toString()));
                    }
                    if (DataAutoDownloadActivity.this.animateChecked) {
                        view3.setChecked(count != 0);
                    }
                    if (count != 0) {
                        z = true;
                    } else {
                        z = false;
                    }
                    if (position != DataAutoDownloadActivity.this.filesRow) {
                        z2 = true;
                    } else {
                        z2 = false;
                    }
                    view3.setTextAndValueAndCheck(text, builder, z, 0, true, z2);
                    return;
                case 5:
                    TextInfoPrivacyCell view4 = holder.itemView;
                    if (position == DataAutoDownloadActivity.this.typeSectionRow) {
                        view4.setText(LocaleController.getString("AutoDownloadAudioInfo", R.string.AutoDownloadAudioInfo));
                        view4.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider, "windowBackgroundGrayShadow"));
                        view4.setFixedSize(0);
                        return;
                    } else if (position != DataAutoDownloadActivity.this.autoDownloadSectionRow) {
                        return;
                    } else {
                        if (DataAutoDownloadActivity.this.usageHeaderRow == -1) {
                            view4.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, "windowBackgroundGrayShadow"));
                            if (DataAutoDownloadActivity.this.currentType == 0) {
                                view4.setText(LocaleController.getString("AutoDownloadOnMobileDataInfo", R.string.AutoDownloadOnMobileDataInfo));
                                return;
                            } else if (DataAutoDownloadActivity.this.currentType == 1) {
                                view4.setText(LocaleController.getString("AutoDownloadOnWiFiDataInfo", R.string.AutoDownloadOnWiFiDataInfo));
                                return;
                            } else if (DataAutoDownloadActivity.this.currentType == 2) {
                                view4.setText(LocaleController.getString("AutoDownloadOnRoamingDataInfo", R.string.AutoDownloadOnRoamingDataInfo));
                                return;
                            } else {
                                return;
                            }
                        }
                        view4.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider, "windowBackgroundGrayShadow"));
                        view4.setText(null);
                        view4.setFixedSize(12);
                        return;
                    }
                default:
                    return;
            }
        }

        public boolean isEnabled(ViewHolder holder) {
            int position = holder.getAdapterPosition();
            return position == DataAutoDownloadActivity.this.photosRow || position == DataAutoDownloadActivity.this.videosRow || position == DataAutoDownloadActivity.this.filesRow;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            switch (viewType) {
                case 0:
                    View cell = new TextCheckCell(this.mContext);
                    cell.setColors("windowBackgroundCheckText", "switchTrackBlue", "switchTrackBlueChecked", "switchTrackBlueThumb", "switchTrackBlueThumbChecked");
                    cell.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                    cell.setHeight(56);
                    view = cell;
                    break;
                case 1:
                    view = new ShadowSectionCell(this.mContext);
                    break;
                case 2:
                    view = new HeaderCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                case 3:
                    view = new PresetChooseView(this.mContext);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                case 4:
                    view = new NotificationsCheckCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                case 5:
                    view = new TextInfoPrivacyCell(this.mContext);
                    view.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, "windowBackgroundGrayShadow"));
                    break;
            }
            view.setLayoutParams(new LayoutParams(-1, -2));
            return new Holder(view);
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
            this.low = LocaleController.getString("AutoDownloadLow", R.string.AutoDownloadLow);
            this.lowSize = (int) Math.ceil((double) this.textPaint.measureText(this.low));
            this.medium = LocaleController.getString("AutoDownloadMedium", R.string.AutoDownloadMedium);
            this.mediumSize = (int) Math.ceil((double) this.textPaint.measureText(this.medium));
            this.high = LocaleController.getString("AutoDownloadHigh", R.string.AutoDownloadHigh);
            this.highSize = (int) Math.ceil((double) this.textPaint.measureText(this.high));
            this.custom = LocaleController.getString("AutoDownloadCustom", R.string.AutoDownloadCustom);
            this.customSize = (int) Math.ceil((double) this.textPaint.measureText(this.custom));
        }

        public boolean onTouchEvent(MotionEvent event) {
            boolean z = false;
            float x = event.getX();
            int a;
            int cx;
            if (event.getAction() == 0) {
                getParent().requestDisallowInterceptTouchEvent(true);
                a = 0;
                while (a < DataAutoDownloadActivity.this.presets.size()) {
                    cx = (this.sideSide + (((this.lineSize + (this.gapSize * 2)) + this.circleSize) * a)) + (this.circleSize / 2);
                    if (x <= ((float) (cx - AndroidUtilities.dp(15.0f))) || x >= ((float) (AndroidUtilities.dp(15.0f) + cx))) {
                        a++;
                    } else {
                        if (a == DataAutoDownloadActivity.this.selectedPreset) {
                            z = true;
                        }
                        this.startMoving = z;
                        this.startX = x;
                        this.startMovingPreset = DataAutoDownloadActivity.this.selectedPreset;
                    }
                }
            } else if (event.getAction() == 2) {
                if (this.startMoving) {
                    if (Math.abs(this.startX - x) >= AndroidUtilities.getPixelsInCM(0.5f, true)) {
                        this.moving = true;
                        this.startMoving = false;
                    }
                } else if (this.moving) {
                    a = 0;
                    while (a < DataAutoDownloadActivity.this.presets.size()) {
                        cx = (this.sideSide + (((this.lineSize + (this.gapSize * 2)) + this.circleSize) * a)) + (this.circleSize / 2);
                        int diff = ((this.lineSize / 2) + (this.circleSize / 2)) + this.gapSize;
                        if (x <= ((float) (cx - diff)) || x >= ((float) (cx + diff))) {
                            a++;
                        } else if (DataAutoDownloadActivity.this.selectedPreset != a) {
                            setPreset(a);
                        }
                    }
                }
            } else if (event.getAction() == 1 || event.getAction() == 3) {
                if (!this.moving) {
                    a = 0;
                    while (a < 5) {
                        cx = (this.sideSide + (((this.lineSize + (this.gapSize * 2)) + this.circleSize) * a)) + (this.circleSize / 2);
                        if (x <= ((float) (cx - AndroidUtilities.dp(15.0f))) || x >= ((float) (AndroidUtilities.dp(15.0f) + cx))) {
                            a++;
                        } else if (DataAutoDownloadActivity.this.selectedPreset != a) {
                            setPreset(a);
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

        private void setPreset(int index) {
            DataAutoDownloadActivity.this.selectedPreset = index;
            Preset preset = (Preset) DataAutoDownloadActivity.this.presets.get(DataAutoDownloadActivity.this.selectedPreset);
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
            Editor editor = MessagesController.getMainSettings(DataAutoDownloadActivity.this.currentAccount).edit();
            editor.putInt(DataAutoDownloadActivity.this.key2, DataAutoDownloadActivity.this.currentPresetNum);
            editor.commit();
            DownloadController.getInstance(DataAutoDownloadActivity.this.currentAccount).checkAutodownloadSettings();
            for (int a = 0; a < 3; a++) {
                ViewHolder holder = DataAutoDownloadActivity.this.listView.findViewHolderForAdapterPosition(DataAutoDownloadActivity.this.photosRow + a);
                if (holder != null) {
                    DataAutoDownloadActivity.this.listAdapter.onBindViewHolder(holder, DataAutoDownloadActivity.this.photosRow + a);
                }
            }
            DataAutoDownloadActivity.this.wereAnyChanges = true;
            invalidate();
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(74.0f), NUM));
            int width = MeasureSpec.getSize(widthMeasureSpec);
            this.circleSize = AndroidUtilities.dp(6.0f);
            this.gapSize = AndroidUtilities.dp(2.0f);
            this.sideSide = AndroidUtilities.dp(22.0f);
            this.lineSize = (((getMeasuredWidth() - (this.circleSize * DataAutoDownloadActivity.this.presets.size())) - ((this.gapSize * 2) * (DataAutoDownloadActivity.this.presets.size() - 1))) - (this.sideSide * 2)) / (DataAutoDownloadActivity.this.presets.size() - 1);
        }

        protected void onDraw(Canvas canvas) {
            this.textPaint.setColor(Theme.getColor("windowBackgroundWhiteGrayText"));
            int cy = (getMeasuredHeight() / 2) + AndroidUtilities.dp(11.0f);
            int a = 0;
            while (a < DataAutoDownloadActivity.this.presets.size()) {
                float dp;
                String text;
                int size;
                int cx = (this.sideSide + (((this.lineSize + (this.gapSize * 2)) + this.circleSize) * a)) + (this.circleSize / 2);
                if (a <= DataAutoDownloadActivity.this.selectedPreset) {
                    this.paint.setColor(Theme.getColor("switchTrackChecked"));
                } else {
                    this.paint.setColor(Theme.getColor("switchTrack"));
                }
                float f = (float) cx;
                float f2 = (float) cy;
                if (a == DataAutoDownloadActivity.this.selectedPreset) {
                    dp = (float) AndroidUtilities.dp(6.0f);
                } else {
                    dp = (float) (this.circleSize / 2);
                }
                canvas.drawCircle(f, f2, dp, this.paint);
                if (a != 0) {
                    int x = ((cx - (this.circleSize / 2)) - this.gapSize) - this.lineSize;
                    int width = this.lineSize;
                    if (a == DataAutoDownloadActivity.this.selectedPreset || a == DataAutoDownloadActivity.this.selectedPreset + 1) {
                        width -= AndroidUtilities.dp(3.0f);
                    }
                    if (a == DataAutoDownloadActivity.this.selectedPreset + 1) {
                        x += AndroidUtilities.dp(3.0f);
                    }
                    canvas.drawRect((float) x, (float) (cy - AndroidUtilities.dp(1.0f)), (float) (x + width), (float) (AndroidUtilities.dp(1.0f) + cy), this.paint);
                }
                Preset preset = (Preset) DataAutoDownloadActivity.this.presets.get(a);
                if (preset == DataAutoDownloadActivity.this.lowPreset) {
                    text = this.low;
                    size = this.lowSize;
                } else if (preset == DataAutoDownloadActivity.this.mediumPreset) {
                    text = this.medium;
                    size = this.mediumSize;
                } else if (preset == DataAutoDownloadActivity.this.highPreset) {
                    text = this.high;
                    size = this.highSize;
                } else {
                    text = this.custom;
                    size = this.customSize;
                }
                if (a == 0) {
                    canvas.drawText(text, (float) AndroidUtilities.dp(22.0f), (float) AndroidUtilities.dp(28.0f), this.textPaint);
                } else if (a == DataAutoDownloadActivity.this.presets.size() - 1) {
                    canvas.drawText(text, (float) ((getMeasuredWidth() - size) - AndroidUtilities.dp(22.0f)), (float) AndroidUtilities.dp(28.0f), this.textPaint);
                } else {
                    canvas.drawText(text, (float) (cx - (this.customSize / 2)), (float) AndroidUtilities.dp(28.0f), this.textPaint);
                }
                a++;
            }
        }
    }

    public DataAutoDownloadActivity(int type) {
        this.currentType = type;
        this.lowPreset = DownloadController.getInstance(this.currentAccount).lowPreset;
        this.mediumPreset = DownloadController.getInstance(this.currentAccount).mediumPreset;
        this.highPreset = DownloadController.getInstance(this.currentAccount).highPreset;
        if (this.currentType == 0) {
            this.currentPresetNum = DownloadController.getInstance(this.currentAccount).currentMobilePreset;
            this.typePreset = DownloadController.getInstance(this.currentAccount).mobilePreset;
            this.defaultPreset = this.mediumPreset;
            this.key = "mobilePreset";
            this.key2 = "currentMobilePreset";
        } else if (this.currentType == 1) {
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
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        if (this.currentType == 0) {
            this.actionBar.setTitle(LocaleController.getString("AutoDownloadOnMobileData", R.string.AutoDownloadOnMobileData));
        } else if (this.currentType == 1) {
            this.actionBar.setTitle(LocaleController.getString("AutoDownloadOnWiFiData", R.string.AutoDownloadOnWiFiData));
        } else if (this.currentType == 2) {
            this.actionBar.setTitle(LocaleController.getString("AutoDownloadOnRoamingData", R.string.AutoDownloadOnRoamingData));
        }
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    DataAutoDownloadActivity.this.lambda$createView$1$PhotoAlbumPickerActivity();
                }
            }
        });
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        FrameLayout frameLayout = this.fragmentView;
        this.listView = new RecyclerListView(context);
        this.listView.setVerticalScrollBarEnabled(false);
        ((DefaultItemAnimator) this.listView.getItemAnimator()).setDelayAnimations(false);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener(new DataAutoDownloadActivity$$Lambda$0(this));
        return this.fragmentView;
    }

    final /* synthetic */ void lambda$createView$4$DataAutoDownloadActivity(View view, int position, float x, float y) {
        boolean checked;
        Editor editor;
        if (position == this.autoDownloadRow) {
            if (this.currentPresetNum != 3) {
                if (this.currentPresetNum == 0) {
                    this.typePreset.set(this.lowPreset);
                } else if (this.currentPresetNum == 1) {
                    this.typePreset.set(this.mediumPreset);
                } else if (this.currentPresetNum == 2) {
                    this.typePreset.set(this.highPreset);
                }
            }
            TextCheckCell cell = (TextCheckCell) view;
            checked = cell.isChecked();
            if (checked || !this.typePreset.enabled) {
                this.typePreset.enabled = !this.typePreset.enabled;
            } else {
                System.arraycopy(this.defaultPreset.mask, 0, this.typePreset.mask, 0, 4);
            }
            view.setTag(this.typePreset.enabled ? "windowBackgroundChecked" : "windowBackgroundUnchecked");
            cell.setBackgroundColorAnimated(!checked, Theme.getColor(this.typePreset.enabled ? "windowBackgroundChecked" : "windowBackgroundUnchecked"));
            updateRows();
            if (this.typePreset.enabled) {
                this.listAdapter.notifyItemRangeInserted(this.autoDownloadSectionRow + 1, 8);
            } else {
                this.listAdapter.notifyItemRangeRemoved(this.autoDownloadSectionRow + 1, 8);
            }
            this.listAdapter.notifyItemChanged(this.autoDownloadSectionRow);
            editor = MessagesController.getMainSettings(this.currentAccount).edit();
            editor.putString(this.key, this.typePreset.toString());
            String str = this.key2;
            this.currentPresetNum = 3;
            editor.putInt(str, 3);
            if (this.currentType == 0) {
                DownloadController.getInstance(this.currentAccount).currentMobilePreset = this.currentPresetNum;
            } else if (this.currentType == 1) {
                DownloadController.getInstance(this.currentAccount).currentWifiPreset = this.currentPresetNum;
            } else {
                DownloadController.getInstance(this.currentAccount).currentRoamingPreset = this.currentPresetNum;
            }
            editor.commit();
            cell.setChecked(!checked);
            DownloadController.getInstance(this.currentAccount).checkAutodownloadSettings();
            this.wereAnyChanges = true;
        } else if ((position == this.photosRow || position == this.videosRow || position == this.filesRow) && view.isEnabled()) {
            int type;
            Preset currentPreset;
            String key;
            String key2;
            if (position == this.photosRow) {
                type = 1;
            } else if (position == this.videosRow) {
                type = 4;
            } else {
                type = 8;
            }
            int index = DownloadController.typeToIndex(type);
            if (this.currentType == 0) {
                currentPreset = DownloadController.getInstance(this.currentAccount).getCurrentMobilePreset();
                key = "mobilePreset";
                key2 = "currentMobilePreset";
            } else if (this.currentType == 1) {
                currentPreset = DownloadController.getInstance(this.currentAccount).getCurrentWiFiPreset();
                key = "wifiPreset";
                key2 = "currentWifiPreset";
            } else {
                currentPreset = DownloadController.getInstance(this.currentAccount).getCurrentRoamingPreset();
                key = "roamingPreset";
                key2 = "currentRoamingPreset";
            }
            NotificationsCheckCell cell2 = (NotificationsCheckCell) view;
            checked = cell2.isChecked();
            boolean hasAny;
            int a;
            if ((LocaleController.isRTL && x <= ((float) AndroidUtilities.dp(76.0f))) || (!LocaleController.isRTL && x >= ((float) (view.getMeasuredWidth() - AndroidUtilities.dp(76.0f))))) {
                if (this.currentPresetNum != 3) {
                    if (this.currentPresetNum == 0) {
                        this.typePreset.set(this.lowPreset);
                    } else if (this.currentPresetNum == 1) {
                        this.typePreset.set(this.mediumPreset);
                    } else if (this.currentPresetNum == 2) {
                        this.typePreset.set(this.highPreset);
                    }
                }
                hasAny = false;
                for (a = 0; a < this.typePreset.mask.length; a++) {
                    if ((currentPreset.mask[a] & type) != 0) {
                        hasAny = true;
                        break;
                    }
                }
                for (a = 0; a < this.typePreset.mask.length; a++) {
                    int[] iArr;
                    if (checked) {
                        iArr = this.typePreset.mask;
                        iArr[a] = iArr[a] & (type ^ -1);
                    } else if (!hasAny) {
                        iArr = this.typePreset.mask;
                        iArr[a] = iArr[a] | type;
                    }
                }
                editor = MessagesController.getMainSettings(this.currentAccount).edit();
                editor.putString(key, this.typePreset.toString());
                this.currentPresetNum = 3;
                editor.putInt(key2, 3);
                if (this.currentType == 0) {
                    DownloadController.getInstance(this.currentAccount).currentMobilePreset = this.currentPresetNum;
                } else if (this.currentType == 1) {
                    DownloadController.getInstance(this.currentAccount).currentWifiPreset = this.currentPresetNum;
                } else {
                    DownloadController.getInstance(this.currentAccount).currentRoamingPreset = this.currentPresetNum;
                }
                editor.commit();
                cell2.setChecked(!checked);
                ViewHolder holder = this.listView.findContainingViewHolder(view);
                if (holder != null) {
                    this.listAdapter.onBindViewHolder(holder, position);
                }
                DownloadController.getInstance(this.currentAccount).checkAutodownloadSettings();
                this.wereAnyChanges = true;
                fillPresets();
            } else if (getParentActivity() != null) {
                Builder builder = new Builder(getParentActivity());
                builder.setApplyTopPadding(false);
                builder.setApplyBottomPadding(false);
                View linearLayout = new LinearLayout(getParentActivity());
                linearLayout.setOrientation(1);
                builder.setCustomView(linearLayout);
                HeaderCell headerCell = new HeaderCell(getParentActivity(), true, 21, 15, false);
                if (position == this.photosRow) {
                    headerCell.setText(LocaleController.getString("AutoDownloadPhotosTitle", R.string.AutoDownloadPhotosTitle));
                } else if (position == this.videosRow) {
                    headerCell.setText(LocaleController.getString("AutoDownloadVideosTitle", R.string.AutoDownloadVideosTitle));
                } else {
                    headerCell.setText(LocaleController.getString("AutoDownloadFilesTitle", R.string.AutoDownloadFilesTitle));
                }
                linearLayout.addView(headerCell, LayoutHelper.createFrame(-1, -2.0f));
                MaxFileSizeCell[] sizeCell = new MaxFileSizeCell[1];
                TextCheckCell[] checkCell = new TextCheckCell[1];
                AnimatorSet[] animatorSet = new AnimatorSet[1];
                TextCheckBoxCell[] cells = new TextCheckBoxCell[4];
                for (a = 0; a < 4; a++) {
                    TextCheckBoxCell checkBoxCell = new TextCheckBoxCell(getParentActivity(), true);
                    cells[a] = checkBoxCell;
                    if (a == 0) {
                        cells[a].setTextAndCheck(LocaleController.getString("AutodownloadContacts", R.string.AutodownloadContacts), (currentPreset.mask[0] & type) != 0, true);
                    } else if (a == 1) {
                        cells[a].setTextAndCheck(LocaleController.getString("AutodownloadPrivateChats", R.string.AutodownloadPrivateChats), (currentPreset.mask[1] & type) != 0, true);
                    } else if (a == 2) {
                        cells[a].setTextAndCheck(LocaleController.getString("AutodownloadGroupChats", R.string.AutodownloadGroupChats), (currentPreset.mask[2] & type) != 0, true);
                    } else if (a == 3) {
                        cells[a].setTextAndCheck(LocaleController.getString("AutodownloadChannels", R.string.AutodownloadChannels), (currentPreset.mask[3] & type) != 0, position != this.photosRow);
                    }
                    cells[a].setBackgroundDrawable(Theme.getSelectorDrawable(false));
                    cells[a].setOnClickListener(new DataAutoDownloadActivity$$Lambda$2(this, checkBoxCell, cells, position, sizeCell, checkCell, animatorSet));
                    linearLayout.addView(cells[a], LayoutHelper.createFrame(-1, 50.0f));
                }
                if (position != this.photosRow) {
                    final TextInfoPrivacyCell infoCell = new TextInfoPrivacyCell(getParentActivity());
                    final int i = position;
                    final TextCheckCell[] textCheckCellArr = checkCell;
                    final AnimatorSet[] animatorSetArr = animatorSet;
                    sizeCell[0] = new MaxFileSizeCell(getParentActivity()) {
                        protected void didChangedSizeValue(int value) {
                            boolean enabled = true;
                            if (i == DataAutoDownloadActivity.this.videosRow) {
                                infoCell.setText(LocaleController.formatString("AutoDownloadPreloadVideoInfo", R.string.AutoDownloadPreloadVideoInfo, AndroidUtilities.formatFileSize((long) value)));
                                if (value <= 2097152) {
                                    enabled = false;
                                }
                                if (enabled != textCheckCellArr[0].isEnabled()) {
                                    ArrayList animators = new ArrayList();
                                    textCheckCellArr[0].setEnabled(enabled, animators);
                                    if (animatorSetArr[0] != null) {
                                        animatorSetArr[0].cancel();
                                        animatorSetArr[0] = null;
                                    }
                                    animatorSetArr[0] = new AnimatorSet();
                                    animatorSetArr[0].playTogether(animators);
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
                    };
                    sizeCell[0].setSize((long) currentPreset.sizes[index]);
                    linearLayout.addView(sizeCell[0], LayoutHelper.createLinear(-1, 50));
                    checkCell[0] = new TextCheckCell(getParentActivity(), 21, true);
                    linearLayout.addView(checkCell[0], LayoutHelper.createLinear(-1, 48));
                    checkCell[0].setOnClickListener(new DataAutoDownloadActivity$$Lambda$3(checkCell));
                    Drawable combinedDrawable = new CombinedDrawable(new ColorDrawable(Theme.getColor("windowBackgroundGray")), Theme.getThemedDrawable(getParentActivity(), (int) R.drawable.greydivider, "windowBackgroundGrayShadow"));
                    combinedDrawable.setFullsize(true);
                    infoCell.setBackgroundDrawable(combinedDrawable);
                    linearLayout.addView(infoCell, LayoutHelper.createLinear(-1, -2));
                    if (position == this.videosRow) {
                        sizeCell[0].setText(LocaleController.getString("AutoDownloadMaxVideoSize", R.string.AutoDownloadMaxVideoSize));
                        checkCell[0].setTextAndCheck(LocaleController.getString("AutoDownloadPreloadVideo", R.string.AutoDownloadPreloadVideo), currentPreset.preloadVideo, false);
                        infoCell.setText(LocaleController.formatString("AutoDownloadPreloadVideoInfo", R.string.AutoDownloadPreloadVideoInfo, AndroidUtilities.formatFileSize((long) currentPreset.sizes[index])));
                    } else {
                        sizeCell[0].setText(LocaleController.getString("AutoDownloadMaxFileSize", R.string.AutoDownloadMaxFileSize));
                        checkCell[0].setTextAndCheck(LocaleController.getString("AutoDownloadPreloadMusic", R.string.AutoDownloadPreloadMusic), currentPreset.preloadMusic, false);
                        infoCell.setText(LocaleController.getString("AutoDownloadPreloadMusicInfo", R.string.AutoDownloadPreloadMusicInfo));
                    }
                } else {
                    sizeCell[0] = null;
                    checkCell[0] = null;
                    linearLayout = new View(getParentActivity());
                    linearLayout.setBackgroundColor(Theme.getColor("divider"));
                    linearLayout.addView(linearLayout, new LinearLayout.LayoutParams(-1, 1));
                }
                if (position == this.videosRow) {
                    hasAny = false;
                    for (TextCheckBoxCell isChecked : cells) {
                        if (isChecked.isChecked()) {
                            hasAny = true;
                            break;
                        }
                    }
                    if (!hasAny) {
                        sizeCell[0].setEnabled(hasAny, null);
                        checkCell[0].setEnabled(hasAny, null);
                    }
                    if (currentPreset.sizes[index] <= 2097152) {
                        checkCell[0].setEnabled(false, null);
                    }
                }
                linearLayout = new FrameLayout(getParentActivity());
                linearLayout.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f));
                linearLayout.addView(linearLayout, LayoutHelper.createLinear(-1, 52));
                linearLayout = new TextView(getParentActivity());
                linearLayout.setTextSize(1, 14.0f);
                linearLayout.setTextColor(Theme.getColor("dialogTextBlue2"));
                linearLayout.setGravity(17);
                linearLayout.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                linearLayout.setText(LocaleController.getString("Cancel", R.string.Cancel).toUpperCase());
                linearLayout.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
                linearLayout.addView(linearLayout, LayoutHelper.createFrame(-2, 36, 51));
                linearLayout.setOnClickListener(new DataAutoDownloadActivity$$Lambda$4(builder));
                linearLayout = new TextView(getParentActivity());
                linearLayout.setTextSize(1, 14.0f);
                linearLayout.setTextColor(Theme.getColor("dialogTextBlue2"));
                linearLayout.setGravity(17);
                linearLayout.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                linearLayout.setText(LocaleController.getString("Save", R.string.Save).toUpperCase());
                linearLayout.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
                linearLayout.addView(linearLayout, LayoutHelper.createFrame(-2, 36, 53));
                linearLayout.setOnClickListener(new DataAutoDownloadActivity$$Lambda$5(this, cells, type, sizeCell, index, checkCell, position, key, key2, builder, view));
                showDialog(builder.create());
            }
        }
    }

    final /* synthetic */ void lambda$null$0$DataAutoDownloadActivity(TextCheckBoxCell checkBoxCell, TextCheckBoxCell[] cells, int position, MaxFileSizeCell[] sizeCell, TextCheckCell[] checkCell, final AnimatorSet[] animatorSet, View v) {
        if (v.isEnabled()) {
            checkBoxCell.setChecked(!checkBoxCell.isChecked());
            boolean hasAny = false;
            for (TextCheckBoxCell isChecked : cells) {
                if (isChecked.isChecked()) {
                    hasAny = true;
                    break;
                }
            }
            if (position == this.videosRow && sizeCell[0].isEnabled() != hasAny) {
                ArrayList<Animator> animators = new ArrayList();
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

    static final /* synthetic */ void lambda$null$1$DataAutoDownloadActivity(TextCheckCell[] checkCell, View v) {
        boolean z = false;
        TextCheckCell textCheckCell = checkCell[0];
        if (!checkCell[0].isChecked()) {
            z = true;
        }
        textCheckCell.setChecked(z);
    }

    final /* synthetic */ void lambda$null$3$DataAutoDownloadActivity(TextCheckBoxCell[] cells, int type, MaxFileSizeCell[] sizeCell, int index, TextCheckCell[] checkCell, int position, String key, String key2, Builder builder, View view, View v1) {
        if (this.currentPresetNum != 3) {
            if (this.currentPresetNum == 0) {
                this.typePreset.set(this.lowPreset);
            } else if (this.currentPresetNum == 1) {
                this.typePreset.set(this.mediumPreset);
            } else if (this.currentPresetNum == 2) {
                this.typePreset.set(this.highPreset);
            }
        }
        for (int a = 0; a < 4; a++) {
            int[] iArr;
            if (cells[a].isChecked()) {
                iArr = this.typePreset.mask;
                iArr[a] = iArr[a] | type;
            } else {
                iArr = this.typePreset.mask;
                iArr[a] = iArr[a] & (type ^ -1);
            }
        }
        if (sizeCell[0] != null) {
            int size = (int) sizeCell[0].getSize();
            this.typePreset.sizes[index] = (int) sizeCell[0].getSize();
        }
        if (checkCell[0] != null) {
            if (position == this.videosRow) {
                this.typePreset.preloadVideo = checkCell[0].isChecked();
            } else {
                this.typePreset.preloadMusic = checkCell[0].isChecked();
            }
        }
        Editor editor = MessagesController.getMainSettings(this.currentAccount).edit();
        editor.putString(key, this.typePreset.toString());
        this.currentPresetNum = 3;
        editor.putInt(key2, 3);
        if (this.currentType == 0) {
            DownloadController.getInstance(this.currentAccount).currentMobilePreset = this.currentPresetNum;
        } else if (this.currentType == 1) {
            DownloadController.getInstance(this.currentAccount).currentWifiPreset = this.currentPresetNum;
        } else {
            DownloadController.getInstance(this.currentAccount).currentRoamingPreset = this.currentPresetNum;
        }
        editor.commit();
        builder.getDismissRunnable().run();
        ViewHolder holder = this.listView.findContainingViewHolder(view);
        if (holder != null) {
            this.animateChecked = true;
            this.listAdapter.onBindViewHolder(holder, position);
            this.animateChecked = false;
        }
        DownloadController.getInstance(this.currentAccount).checkAutodownloadSettings();
        this.wereAnyChanges = true;
        fillPresets();
    }

    public void onResume() {
        super.onResume();
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
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
        Collections.sort(this.presets, DataAutoDownloadActivity$$Lambda$1.$instance);
        if (this.listView != null) {
            ViewHolder holder = this.listView.findViewHolderForAdapterPosition(this.usageProgressRow);
            if (holder != null) {
                holder.itemView.requestLayout();
            } else {
                this.listAdapter.notifyItemChanged(this.usageProgressRow);
            }
        }
        if (this.currentPresetNum == 0 || (this.currentPresetNum == 3 && this.typePreset.equals(this.lowPreset))) {
            this.selectedPreset = this.presets.indexOf(this.lowPreset);
        } else if (this.currentPresetNum == 1 || (this.currentPresetNum == 3 && this.typePreset.equals(this.mediumPreset))) {
            this.selectedPreset = this.presets.indexOf(this.mediumPreset);
        } else if (this.currentPresetNum == 2 || (this.currentPresetNum == 3 && this.typePreset.equals(this.highPreset))) {
            this.selectedPreset = this.presets.indexOf(this.highPreset);
        } else {
            this.selectedPreset = this.presets.indexOf(this.typePreset);
        }
    }

    static final /* synthetic */ int lambda$fillPresets$5$DataAutoDownloadActivity(Preset o1, Preset o2) {
        int a;
        int i;
        int index1 = DownloadController.typeToIndex(4);
        int index2 = DownloadController.typeToIndex(8);
        boolean video1 = false;
        boolean doc1 = false;
        for (a = 0; a < o1.mask.length; a++) {
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
        for (a = 0; a < o2.mask.length; a++) {
            if ((o2.mask[a] & 4) != 0) {
                video2 = true;
            }
            if ((o2.mask[a] & 8) != 0) {
                doc2 = true;
            }
            if (video2 && doc2) {
                break;
            }
        }
        int size1 = (video1 ? o1.sizes[index1] : 0) + (doc1 ? o1.sizes[index2] : 0);
        if (video2) {
            i = o2.sizes[index1];
        } else {
            i = 0;
        }
        int size2 = i + (doc2 ? o2.sizes[index2] : 0);
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
        r9 = new ThemeDescription[29];
        r9[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{HeaderCell.class, NotificationsCheckCell.class, PresetChooseView.class}, null, null, null, "windowBackgroundWhite");
        r9[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray");
        r9[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
        r9[3] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
        r9[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
        r9[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
        r9[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
        r9[7] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
        r9[8] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, "divider");
        r9[9] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, "windowBackgroundGrayShadow");
        r9[10] = new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlueHeader");
        r9[11] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCheckCell.class}, null, null, null, "windowBackgroundChecked");
        r9[12] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCheckCell.class}, null, null, null, "windowBackgroundUnchecked");
        r9[13] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundCheckText");
        r9[14] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, "switchTrackBlue");
        r9[15] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, "switchTrackBlueChecked");
        r9[16] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, "switchTrackBlueThumb");
        r9[17] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, "switchTrackBlueThumbChecked");
        r9[18] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, "switchTrackBlueSelector");
        r9[19] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, "switchTrackBlueSelectorChecked");
        r9[20] = new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        r9[21] = new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"valueTextView"}, null, null, null, "windowBackgroundWhiteGrayText2");
        r9[22] = new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, null, null, null, "switchTrack");
        r9[23] = new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, null, null, null, "switchTrackChecked");
        r9[24] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, "windowBackgroundGrayShadow");
        r9[25] = new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteGrayText4");
        r9[26] = new ThemeDescription(this.listView, 0, new Class[]{PresetChooseView.class}, null, null, null, "switchTrack");
        r9[27] = new ThemeDescription(this.listView, 0, new Class[]{PresetChooseView.class}, null, null, null, "switchTrackChecked");
        r9[28] = new ThemeDescription(this.listView, 0, new Class[]{PresetChooseView.class}, null, null, null, "windowBackgroundWhiteGrayText");
        return r9;
    }
}
