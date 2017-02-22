package org.telegram.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet.BottomSheetCell;
import org.telegram.ui.ActionBar.BottomSheet.Builder;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextDetailSettingsCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public class DataSettingsActivity extends BaseFragment {
    private int callsSection2Row;
    private int callsSectionRow;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private int mediaDownloadSection2Row;
    private int mediaDownloadSectionRow;
    private int mobileDownloadRow;
    private int mobileUsageRow;
    private int roamingDownloadRow;
    private int roamingUsageRow;
    private int rowCount;
    private int storageUsageRow;
    private int usageSection2Row;
    private int usageSectionRow;
    private int useLessDataForCallsRow;
    private int wifiDownloadRow;
    private int wifiUsageRow;

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return DataSettingsActivity.this.rowCount;
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            String value;
            switch (holder.getItemViewType()) {
                case 0:
                    if (position == DataSettingsActivity.this.callsSection2Row || (position == DataSettingsActivity.this.usageSection2Row && DataSettingsActivity.this.usageSection2Row == -1)) {
                        holder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else {
                        holder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                        return;
                    }
                case 1:
                    TextSettingsCell textCell = holder.itemView;
                    if (position == DataSettingsActivity.this.storageUsageRow) {
                        textCell.setText(LocaleController.getString("StorageUsage", R.string.StorageUsage), true);
                        return;
                    } else if (position == DataSettingsActivity.this.useLessDataForCallsRow) {
                        value = null;
                        switch (ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).getInt("VoipDataSaving", 0)) {
                            case 0:
                                value = LocaleController.getString("UseLessDataNever", R.string.UseLessDataNever);
                                break;
                            case 1:
                                value = LocaleController.getString("UseLessDataOnMobile", R.string.UseLessDataOnMobile);
                                break;
                            case 2:
                                value = LocaleController.getString("UseLessDataAlways", R.string.UseLessDataAlways);
                                break;
                        }
                        textCell.setTextAndValue(LocaleController.getString("VoipUseLessData", R.string.VoipUseLessData), value, false);
                        return;
                    } else if (position == DataSettingsActivity.this.mobileUsageRow) {
                        textCell.setText(LocaleController.getString("MobileUsage", R.string.MobileUsage), true);
                        return;
                    } else if (position == DataSettingsActivity.this.roamingUsageRow) {
                        textCell.setText(LocaleController.getString("RoamingUsage", R.string.RoamingUsage), false);
                        return;
                    } else if (position == DataSettingsActivity.this.wifiUsageRow) {
                        textCell.setText(LocaleController.getString("WiFiUsage", R.string.WiFiUsage), true);
                        return;
                    } else {
                        return;
                    }
                case 2:
                    HeaderCell headerCell = holder.itemView;
                    if (position == DataSettingsActivity.this.mediaDownloadSectionRow) {
                        headerCell.setText(LocaleController.getString("AutomaticMediaDownload", R.string.AutomaticMediaDownload));
                        return;
                    } else if (position == DataSettingsActivity.this.usageSectionRow) {
                        headerCell.setText(LocaleController.getString("DataUsage", R.string.DataUsage));
                        return;
                    } else if (position == DataSettingsActivity.this.callsSectionRow) {
                        headerCell.setText(LocaleController.getString("Calls", R.string.Calls));
                        return;
                    } else {
                        return;
                    }
                case 3:
                    TextDetailSettingsCell textCell2 = holder.itemView;
                    if (position == DataSettingsActivity.this.mobileDownloadRow || position == DataSettingsActivity.this.wifiDownloadRow || position == DataSettingsActivity.this.roamingDownloadRow) {
                        int mask;
                        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
                        if (position == DataSettingsActivity.this.mobileDownloadRow) {
                            value = LocaleController.getString("WhenUsingMobileData", R.string.WhenUsingMobileData);
                            mask = MediaController.getInstance().mobileDataDownloadMask;
                        } else if (position == DataSettingsActivity.this.wifiDownloadRow) {
                            value = LocaleController.getString("WhenConnectedOnWiFi", R.string.WhenConnectedOnWiFi);
                            mask = MediaController.getInstance().wifiDownloadMask;
                        } else {
                            value = LocaleController.getString("WhenRoaming", R.string.WhenRoaming);
                            mask = MediaController.getInstance().roamingDownloadMask;
                        }
                        String text = "";
                        if ((mask & 1) != 0) {
                            text = text + LocaleController.getString("LocalPhotoCache", R.string.LocalPhotoCache);
                        }
                        if ((mask & 2) != 0) {
                            if (text.length() != 0) {
                                text = text + ", ";
                            }
                            text = text + LocaleController.getString("LocalAudioCache", R.string.LocalAudioCache);
                        }
                        if ((mask & 4) != 0) {
                            if (text.length() != 0) {
                                text = text + ", ";
                            }
                            text = text + LocaleController.getString("LocalVideoCache", R.string.LocalVideoCache);
                        }
                        if ((mask & 8) != 0) {
                            if (text.length() != 0) {
                                text = text + ", ";
                            }
                            text = text + LocaleController.getString("FilesDataUsage", R.string.FilesDataUsage);
                        }
                        if ((mask & 16) != 0) {
                            if (text.length() != 0) {
                                text = text + ", ";
                            }
                            text = text + LocaleController.getString("AttachMusic", R.string.AttachMusic);
                        }
                        if ((mask & 32) != 0) {
                            if (text.length() != 0) {
                                text = text + ", ";
                            }
                            text = text + LocaleController.getString("LocalGifCache", R.string.LocalGifCache);
                        }
                        if (text.length() == 0) {
                            text = LocaleController.getString("NoMediaAutoDownload", R.string.NoMediaAutoDownload);
                        }
                        textCell2.setTextAndValue(value, text, true);
                        return;
                    }
                    return;
                default:
                    return;
            }
        }

        public boolean isEnabled(ViewHolder holder) {
            int position = holder.getAdapterPosition();
            return position == DataSettingsActivity.this.wifiDownloadRow || position == DataSettingsActivity.this.mobileDownloadRow || position == DataSettingsActivity.this.roamingDownloadRow || position == DataSettingsActivity.this.storageUsageRow || position == DataSettingsActivity.this.useLessDataForCallsRow || position == DataSettingsActivity.this.mobileUsageRow || position == DataSettingsActivity.this.roamingUsageRow || position == DataSettingsActivity.this.wifiUsageRow;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            switch (viewType) {
                case 0:
                    view = new ShadowSectionCell(this.mContext);
                    break;
                case 1:
                    view = new TextSettingsCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 2:
                    view = new HeaderCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 3:
                    view = new TextDetailSettingsCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
            }
            view.setLayoutParams(new LayoutParams(-1, -2));
            return new Holder(view);
        }

        public int getItemViewType(int position) {
            if (position == DataSettingsActivity.this.mediaDownloadSection2Row || position == DataSettingsActivity.this.usageSection2Row || position == DataSettingsActivity.this.callsSection2Row) {
                return 0;
            }
            if (position == DataSettingsActivity.this.storageUsageRow || position == DataSettingsActivity.this.useLessDataForCallsRow || position == DataSettingsActivity.this.roamingUsageRow || position == DataSettingsActivity.this.wifiUsageRow || position == DataSettingsActivity.this.mobileUsageRow) {
                return 1;
            }
            if (position == DataSettingsActivity.this.wifiDownloadRow || position == DataSettingsActivity.this.mobileDownloadRow || position == DataSettingsActivity.this.roamingDownloadRow) {
                return 3;
            }
            if (position == DataSettingsActivity.this.mediaDownloadSectionRow || position == DataSettingsActivity.this.callsSectionRow || position == DataSettingsActivity.this.usageSectionRow) {
                return 2;
            }
            return 1;
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        this.rowCount = 0;
        int i = this.rowCount;
        this.rowCount = i + 1;
        this.mediaDownloadSectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.mobileDownloadRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.wifiDownloadRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.roamingDownloadRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.mediaDownloadSection2Row = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.usageSectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.storageUsageRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.mobileUsageRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.wifiUsageRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.roamingUsageRow = i;
        if (MessagesController.getInstance().callsEnabled) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.usageSection2Row = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.callsSectionRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.useLessDataForCallsRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.callsSection2Row = i;
        } else {
            this.usageSection2Row = -1;
            this.callsSectionRow = -1;
            this.useLessDataForCallsRow = -1;
            this.callsSection2Row = -1;
        }
        return true;
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setTitle(LocaleController.getString("DataSettings", R.string.DataSettings));
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    DataSettingsActivity.this.finishFragment();
                }
            }
        });
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        FrameLayout frameLayout = this.fragmentView;
        this.listView = new RecyclerListView(context);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(View view, int position) {
                final int i;
                if (position == DataSettingsActivity.this.wifiDownloadRow || position == DataSettingsActivity.this.mobileDownloadRow || position == DataSettingsActivity.this.roamingDownloadRow) {
                    if (DataSettingsActivity.this.getParentActivity() != null) {
                        final boolean[] zArr;
                        boolean[] maskValues = new boolean[6];
                        Builder builder = new Builder(DataSettingsActivity.this.getParentActivity());
                        int mask = 0;
                        if (position == DataSettingsActivity.this.mobileDownloadRow) {
                            mask = MediaController.getInstance().mobileDataDownloadMask;
                        } else if (position == DataSettingsActivity.this.wifiDownloadRow) {
                            mask = MediaController.getInstance().wifiDownloadMask;
                        } else if (position == DataSettingsActivity.this.roamingDownloadRow) {
                            mask = MediaController.getInstance().roamingDownloadMask;
                        }
                        builder.setApplyTopPadding(false);
                        builder.setApplyBottomPadding(false);
                        LinearLayout linearLayout = new LinearLayout(DataSettingsActivity.this.getParentActivity());
                        linearLayout.setOrientation(1);
                        for (int a = 0; a < 6; a++) {
                            String name = null;
                            if (a == 0) {
                                maskValues[a] = (mask & 1) != 0;
                                name = LocaleController.getString("LocalPhotoCache", R.string.LocalPhotoCache);
                            } else if (a == 1) {
                                maskValues[a] = (mask & 2) != 0;
                                name = LocaleController.getString("LocalAudioCache", R.string.LocalAudioCache);
                            } else if (a == 2) {
                                maskValues[a] = (mask & 4) != 0;
                                name = LocaleController.getString("LocalVideoCache", R.string.LocalVideoCache);
                            } else if (a == 3) {
                                maskValues[a] = (mask & 8) != 0;
                                name = LocaleController.getString("FilesDataUsage", R.string.FilesDataUsage);
                            } else if (a == 4) {
                                maskValues[a] = (mask & 16) != 0;
                                name = LocaleController.getString("AttachMusic", R.string.AttachMusic);
                            } else if (a == 5) {
                                maskValues[a] = (mask & 32) != 0;
                                name = LocaleController.getString("LocalGifCache", R.string.LocalGifCache);
                            }
                            CheckBoxCell checkBoxCell = new CheckBoxCell(DataSettingsActivity.this.getParentActivity(), true);
                            checkBoxCell.setTag(Integer.valueOf(a));
                            checkBoxCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                            linearLayout.addView(checkBoxCell, LayoutHelper.createLinear(-1, 48));
                            checkBoxCell.setText(name, "", maskValues[a], true);
                            checkBoxCell.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
                            zArr = maskValues;
                            checkBoxCell.setOnClickListener(new OnClickListener() {
                                public void onClick(View v) {
                                    CheckBoxCell cell = (CheckBoxCell) v;
                                    int num = ((Integer) cell.getTag()).intValue();
                                    zArr[num] = !zArr[num];
                                    cell.setChecked(zArr[num], true);
                                }
                            });
                        }
                        BottomSheetCell cell = new BottomSheetCell(DataSettingsActivity.this.getParentActivity(), 1);
                        cell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                        cell.setTextAndIcon(LocaleController.getString("Save", R.string.Save).toUpperCase(), 0);
                        cell.setTextColor(-12940081);
                        zArr = maskValues;
                        i = position;
                        cell.setOnClickListener(new OnClickListener() {
                            public void onClick(View v) {
                                try {
                                    if (DataSettingsActivity.this.visibleDialog != null) {
                                        DataSettingsActivity.this.visibleDialog.dismiss();
                                    }
                                } catch (Throwable e) {
                                    FileLog.e(e);
                                }
                                int newMask = 0;
                                for (int a = 0; a < 6; a++) {
                                    if (zArr[a]) {
                                        if (a == 0) {
                                            newMask |= 1;
                                        } else if (a == 1) {
                                            newMask |= 2;
                                        } else if (a == 2) {
                                            newMask |= 4;
                                        } else if (a == 3) {
                                            newMask |= 8;
                                        } else if (a == 4) {
                                            newMask |= 16;
                                        } else if (a == 5) {
                                            newMask |= 32;
                                        }
                                    }
                                }
                                Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
                                if (i == DataSettingsActivity.this.mobileDownloadRow) {
                                    editor.putInt("mobileDataDownloadMask", newMask);
                                    MediaController.getInstance().mobileDataDownloadMask = newMask;
                                } else if (i == DataSettingsActivity.this.wifiDownloadRow) {
                                    editor.putInt("wifiDownloadMask", newMask);
                                    MediaController.getInstance().wifiDownloadMask = newMask;
                                } else if (i == DataSettingsActivity.this.roamingDownloadRow) {
                                    editor.putInt("roamingDownloadMask", newMask);
                                    MediaController.getInstance().roamingDownloadMask = newMask;
                                }
                                editor.commit();
                                if (DataSettingsActivity.this.listAdapter != null) {
                                    DataSettingsActivity.this.listAdapter.notifyItemChanged(i);
                                }
                            }
                        });
                        linearLayout.addView(cell, LayoutHelper.createLinear(-1, 48));
                        builder.setCustomView(linearLayout);
                        DataSettingsActivity.this.showDialog(builder.create());
                    }
                } else if (position == DataSettingsActivity.this.storageUsageRow) {
                    DataSettingsActivity.this.presentFragment(new CacheControlActivity());
                } else if (position == DataSettingsActivity.this.useLessDataForCallsRow) {
                    SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
                    final SharedPreferences sharedPreferences = preferences;
                    i = position;
                    Dialog dlg = AlertsCreator.createSingleChoiceDialog(DataSettingsActivity.this.getParentActivity(), DataSettingsActivity.this, new String[]{LocaleController.getString("UseLessDataNever", R.string.UseLessDataNever), LocaleController.getString("UseLessDataOnMobile", R.string.UseLessDataOnMobile), LocaleController.getString("UseLessDataAlways", R.string.UseLessDataAlways)}, LocaleController.getString("VoipUseLessData", R.string.VoipUseLessData), preferences.getInt("VoipDataSaving", 0), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            int val = -1;
                            switch (which) {
                                case 0:
                                    val = 0;
                                    break;
                                case 1:
                                    val = 1;
                                    break;
                                case 2:
                                    val = 2;
                                    break;
                            }
                            if (val != -1) {
                                sharedPreferences.edit().putInt("VoipDataSaving", val).commit();
                            }
                            if (DataSettingsActivity.this.listAdapter != null) {
                                DataSettingsActivity.this.listAdapter.notifyItemChanged(i);
                            }
                        }
                    });
                    DataSettingsActivity.this.setVisibleDialog(dlg);
                    dlg.show();
                } else if (position == DataSettingsActivity.this.mobileUsageRow) {
                    DataSettingsActivity.this.presentFragment(new DataUsageActivity(0));
                } else if (position == DataSettingsActivity.this.roamingUsageRow) {
                    DataSettingsActivity.this.presentFragment(new DataUsageActivity(2));
                } else if (position == DataSettingsActivity.this.wifiUsageRow) {
                    DataSettingsActivity.this.presentFragment(new DataUsageActivity(1));
                }
            }
        });
        frameLayout.addView(this.actionBar);
        return this.fragmentView;
    }

    protected void onDialogDismiss(Dialog dialog) {
        MediaController.getInstance().checkAutodownloadSettings();
    }

    public void onResume() {
        super.onResume();
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[16];
        themeDescriptionArr[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, TextSettingsCell.class, TextDetailSettingsCell.class}, null, null, null, Theme.key_windowBackgroundWhite);
        themeDescriptionArr[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray);
        themeDescriptionArr[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault);
        themeDescriptionArr[3] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault);
        themeDescriptionArr[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon);
        themeDescriptionArr[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle);
        themeDescriptionArr[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector);
        themeDescriptionArr[7] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        themeDescriptionArr[8] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelectorSDK21);
        themeDescriptionArr[9] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider);
        themeDescriptionArr[10] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        themeDescriptionArr[11] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[12] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteValueText);
        themeDescriptionArr[13] = new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlueHeader);
        themeDescriptionArr[14] = new ThemeDescription(this.listView, 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[15] = new ThemeDescription(this.listView, 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2);
        return themeDescriptionArr;
    }
}