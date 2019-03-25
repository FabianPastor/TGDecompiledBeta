package org.telegram.ui;

import android.animation.AnimatorSet;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.DownloadController.Preset;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.NotificationsCheckCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.Components.voip.VoIPHelper;

public class DataSettingsActivity extends BaseFragment {
    private AnimatorSet animatorSet;
    private int autoplayGifsRow;
    private int autoplayHeaderRow;
    private int autoplaySectionRow;
    private int autoplayVideoRow;
    private int callsSection2Row;
    private int callsSectionRow;
    private int dataUsageRow;
    private int enableAllStreamInfoRow;
    private int enableAllStreamRow;
    private int enableCacheStreamRow;
    private int enableMkvRow;
    private int enableStreamRow;
    private LinearLayoutManager layoutManager;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private int mediaDownloadSection2Row;
    private int mediaDownloadSectionRow;
    private int mobileRow;
    private int proxyRow;
    private int proxySection2Row;
    private int proxySectionRow;
    private int quickRepliesRow;
    private int resetDownloadRow;
    private int roamingRow;
    private int rowCount;
    private int storageUsageRow;
    private int streamSectionRow;
    private int usageSection2Row;
    private int usageSectionRow;
    private int useLessDataForCallsRow;
    private int wifiRow;

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return DataSettingsActivity.this.rowCount;
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 0:
                    if (position == DataSettingsActivity.this.proxySection2Row) {
                        holder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                        return;
                    } else {
                        holder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                        return;
                    }
                case 1:
                    TextSettingsCell textCell = (TextSettingsCell) holder.itemView;
                    textCell.setCanDisable(false);
                    textCell.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
                    if (position == DataSettingsActivity.this.storageUsageRow) {
                        textCell.setText(LocaleController.getString("StorageUsage", NUM), true);
                        return;
                    } else if (position == DataSettingsActivity.this.useLessDataForCallsRow) {
                        String value = null;
                        switch (MessagesController.getGlobalMainSettings().getInt("VoipDataSaving", VoIPHelper.getDataSavingDefault())) {
                            case 0:
                                value = LocaleController.getString("UseLessDataNever", NUM);
                                break;
                            case 1:
                                value = LocaleController.getString("UseLessDataOnMobile", NUM);
                                break;
                            case 2:
                                value = LocaleController.getString("UseLessDataAlways", NUM);
                                break;
                            case 3:
                                value = LocaleController.getString("UseLessDataOnRoaming", NUM);
                                break;
                        }
                        textCell.setTextAndValue(LocaleController.getString("VoipUseLessData", NUM), value, true);
                        return;
                    } else if (position == DataSettingsActivity.this.dataUsageRow) {
                        textCell.setText(LocaleController.getString("NetworkUsage", NUM), false);
                        return;
                    } else if (position == DataSettingsActivity.this.proxyRow) {
                        textCell.setText(LocaleController.getString("ProxySettings", NUM), false);
                        return;
                    } else if (position == DataSettingsActivity.this.resetDownloadRow) {
                        textCell.setCanDisable(true);
                        textCell.setTextColor(Theme.getColor("windowBackgroundWhiteRedText"));
                        textCell.setText(LocaleController.getString("ResetAutomaticMediaDownload", NUM), false);
                        return;
                    } else if (position == DataSettingsActivity.this.quickRepliesRow) {
                        textCell.setText(LocaleController.getString("VoipQuickReplies", NUM), false);
                        return;
                    } else {
                        return;
                    }
                case 2:
                    HeaderCell headerCell = holder.itemView;
                    if (position == DataSettingsActivity.this.mediaDownloadSectionRow) {
                        headerCell.setText(LocaleController.getString("AutomaticMediaDownload", NUM));
                        return;
                    } else if (position == DataSettingsActivity.this.usageSectionRow) {
                        headerCell.setText(LocaleController.getString("DataUsage", NUM));
                        return;
                    } else if (position == DataSettingsActivity.this.callsSectionRow) {
                        headerCell.setText(LocaleController.getString("Calls", NUM));
                        return;
                    } else if (position == DataSettingsActivity.this.proxySectionRow) {
                        headerCell.setText(LocaleController.getString("Proxy", NUM));
                        return;
                    } else if (position == DataSettingsActivity.this.streamSectionRow) {
                        headerCell.setText(LocaleController.getString("Streaming", NUM));
                        return;
                    } else if (position == DataSettingsActivity.this.autoplayHeaderRow) {
                        headerCell.setText(LocaleController.getString("AutoplayMedia", NUM));
                        return;
                    } else {
                        return;
                    }
                case 3:
                    TextCheckCell checkCell = holder.itemView;
                    if (position == DataSettingsActivity.this.enableStreamRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("EnableStreaming", NUM), SharedConfig.streamMedia, DataSettingsActivity.this.enableAllStreamRow != -1);
                        return;
                    } else if (position == DataSettingsActivity.this.enableCacheStreamRow) {
                        return;
                    } else {
                        if (position == DataSettingsActivity.this.enableMkvRow) {
                            checkCell.setTextAndCheck("(beta only) Show MKV as Video", SharedConfig.streamMkv, true);
                            return;
                        } else if (position == DataSettingsActivity.this.enableAllStreamRow) {
                            checkCell.setTextAndCheck("(beta only) Stream All Videos", SharedConfig.streamAllVideo, false);
                            return;
                        } else if (position == DataSettingsActivity.this.autoplayGifsRow) {
                            checkCell.setTextAndCheck(LocaleController.getString("AutoplayGIF", NUM), SharedConfig.autoplayGifs, true);
                            return;
                        } else if (position == DataSettingsActivity.this.autoplayVideoRow) {
                            checkCell.setTextAndCheck(LocaleController.getString("AutoplayVideo", NUM), SharedConfig.autoplayVideo, false);
                            return;
                        } else {
                            return;
                        }
                    }
                case 4:
                    TextInfoPrivacyCell cell = holder.itemView;
                    if (position == DataSettingsActivity.this.enableAllStreamInfoRow) {
                        cell.setText(LocaleController.getString("EnableAllStreamingInfo", NUM));
                        return;
                    }
                    return;
                case 5:
                    String text;
                    boolean enabled;
                    Preset preset;
                    NotificationsCheckCell checkCell2 = holder.itemView;
                    StringBuilder builder = new StringBuilder();
                    if (position == DataSettingsActivity.this.mobileRow) {
                        text = LocaleController.getString("WhenUsingMobileData", NUM);
                        enabled = DownloadController.getInstance(DataSettingsActivity.this.currentAccount).mobilePreset.enabled;
                        preset = DownloadController.getInstance(DataSettingsActivity.this.currentAccount).getCurrentMobilePreset();
                    } else if (position == DataSettingsActivity.this.wifiRow) {
                        text = LocaleController.getString("WhenConnectedOnWiFi", NUM);
                        enabled = DownloadController.getInstance(DataSettingsActivity.this.currentAccount).wifiPreset.enabled;
                        preset = DownloadController.getInstance(DataSettingsActivity.this.currentAccount).getCurrentWiFiPreset();
                    } else {
                        text = LocaleController.getString("WhenRoaming", NUM);
                        enabled = DownloadController.getInstance(DataSettingsActivity.this.currentAccount).roamingPreset.enabled;
                        preset = DownloadController.getInstance(DataSettingsActivity.this.currentAccount).getCurrentRoamingPreset();
                    }
                    boolean photos = false;
                    boolean videos = false;
                    boolean files = false;
                    int count = 0;
                    int a = 0;
                    while (a < preset.mask.length) {
                        if (!(photos || (preset.mask[a] & 1) == 0)) {
                            photos = true;
                            count++;
                        }
                        if (!(videos || (preset.mask[a] & 4) == 0)) {
                            videos = true;
                            count++;
                        }
                        if (!(files || (preset.mask[a] & 8) == 0)) {
                            files = true;
                            count++;
                        }
                        a++;
                    }
                    if (!preset.enabled || count == 0) {
                        builder.append(LocaleController.getString("NoMediaAutoDownload", NUM));
                    } else {
                        if (photos) {
                            builder.append(LocaleController.getString("AutoDownloadPhotos", NUM));
                        }
                        if (videos) {
                            if (builder.length() > 0) {
                                builder.append(", ");
                            }
                            builder.append(LocaleController.getString("AutoDownloadVideos", NUM));
                            builder.append(String.format(" (%1$s)", new Object[]{AndroidUtilities.formatFileSize((long) preset.sizes[DownloadController.typeToIndex(4)], true)}));
                        }
                        if (files) {
                            if (builder.length() > 0) {
                                builder.append(", ");
                            }
                            builder.append(LocaleController.getString("AutoDownloadFiles", NUM));
                            builder.append(String.format(" (%1$s)", new Object[]{AndroidUtilities.formatFileSize((long) preset.sizes[DownloadController.typeToIndex(8)], true)}));
                        }
                    }
                    boolean z = (photos || videos || files) && enabled;
                    checkCell2.setTextAndValueAndCheck(text, builder, z, 0, true, true);
                    return;
                default:
                    return;
            }
        }

        public void onViewAttachedToWindow(ViewHolder holder) {
            if (holder.getItemViewType() == 3) {
                TextCheckCell checkCell = holder.itemView;
                int position = holder.getAdapterPosition();
                if (position == DataSettingsActivity.this.enableCacheStreamRow) {
                    checkCell.setChecked(SharedConfig.saveStreamMedia);
                } else if (position == DataSettingsActivity.this.enableStreamRow) {
                    checkCell.setChecked(SharedConfig.streamMedia);
                } else if (position == DataSettingsActivity.this.enableAllStreamRow) {
                    checkCell.setChecked(SharedConfig.streamAllVideo);
                } else if (position == DataSettingsActivity.this.enableMkvRow) {
                    checkCell.setChecked(SharedConfig.streamMkv);
                } else if (position == DataSettingsActivity.this.autoplayGifsRow) {
                    checkCell.setChecked(SharedConfig.autoplayGifs);
                } else if (position == DataSettingsActivity.this.autoplayVideoRow) {
                    checkCell.setChecked(SharedConfig.autoplayVideo);
                }
            }
        }

        public boolean isRowEnabled(int position) {
            if (position == DataSettingsActivity.this.resetDownloadRow) {
                DownloadController controller = DownloadController.getInstance(DataSettingsActivity.this.currentAccount);
                if (controller.lowPreset.equals(controller.getCurrentRoamingPreset()) && controller.lowPreset.isEnabled() == controller.roamingPreset.enabled && controller.mediumPreset.equals(controller.getCurrentMobilePreset()) && controller.mediumPreset.isEnabled() == controller.mobilePreset.enabled && controller.highPreset.equals(controller.getCurrentWiFiPreset()) && controller.highPreset.isEnabled() == controller.wifiPreset.enabled) {
                    return false;
                }
                return true;
            } else if (position == DataSettingsActivity.this.mobileRow || position == DataSettingsActivity.this.roamingRow || position == DataSettingsActivity.this.wifiRow || position == DataSettingsActivity.this.storageUsageRow || position == DataSettingsActivity.this.useLessDataForCallsRow || position == DataSettingsActivity.this.dataUsageRow || position == DataSettingsActivity.this.proxyRow || position == DataSettingsActivity.this.enableCacheStreamRow || position == DataSettingsActivity.this.enableStreamRow || position == DataSettingsActivity.this.enableAllStreamRow || position == DataSettingsActivity.this.enableMkvRow || position == DataSettingsActivity.this.quickRepliesRow || position == DataSettingsActivity.this.autoplayVideoRow || position == DataSettingsActivity.this.autoplayGifsRow) {
                return true;
            } else {
                return false;
            }
        }

        public boolean isEnabled(ViewHolder holder) {
            return isRowEnabled(holder.getAdapterPosition());
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            switch (viewType) {
                case 0:
                    view = new ShadowSectionCell(this.mContext);
                    break;
                case 1:
                    view = new TextSettingsCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                case 2:
                    view = new HeaderCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                case 3:
                    view = new TextCheckCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                case 4:
                    view = new TextInfoPrivacyCell(this.mContext);
                    view.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                    break;
                case 5:
                    view = new NotificationsCheckCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
            }
            view.setLayoutParams(new LayoutParams(-1, -2));
            return new Holder(view);
        }

        public int getItemViewType(int position) {
            if (position == DataSettingsActivity.this.mediaDownloadSection2Row || position == DataSettingsActivity.this.usageSection2Row || position == DataSettingsActivity.this.callsSection2Row || position == DataSettingsActivity.this.proxySection2Row || position == DataSettingsActivity.this.autoplaySectionRow) {
                return 0;
            }
            if (position == DataSettingsActivity.this.mediaDownloadSectionRow || position == DataSettingsActivity.this.streamSectionRow || position == DataSettingsActivity.this.callsSectionRow || position == DataSettingsActivity.this.usageSectionRow || position == DataSettingsActivity.this.proxySectionRow || position == DataSettingsActivity.this.autoplayHeaderRow) {
                return 2;
            }
            if (position == DataSettingsActivity.this.enableCacheStreamRow || position == DataSettingsActivity.this.enableStreamRow || position == DataSettingsActivity.this.enableAllStreamRow || position == DataSettingsActivity.this.enableMkvRow || position == DataSettingsActivity.this.autoplayGifsRow || position == DataSettingsActivity.this.autoplayVideoRow) {
                return 3;
            }
            if (position == DataSettingsActivity.this.enableAllStreamInfoRow) {
                return 4;
            }
            if (position == DataSettingsActivity.this.mobileRow || position == DataSettingsActivity.this.wifiRow || position == DataSettingsActivity.this.roamingRow) {
                return 5;
            }
            return 1;
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        DownloadController.getInstance(this.currentAccount).loadAutoDownloadConfig(true);
        this.rowCount = 0;
        int i = this.rowCount;
        this.rowCount = i + 1;
        this.usageSectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.storageUsageRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.dataUsageRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.usageSection2Row = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.mediaDownloadSectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.mobileRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.wifiRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.roamingRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.resetDownloadRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.mediaDownloadSection2Row = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.autoplayHeaderRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.autoplayGifsRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.autoplayVideoRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.autoplaySectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.streamSectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.enableStreamRow = i;
        if (BuildVars.DEBUG_VERSION) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.enableMkvRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.enableAllStreamRow = i;
        } else {
            this.enableAllStreamRow = -1;
            this.enableMkvRow = -1;
        }
        i = this.rowCount;
        this.rowCount = i + 1;
        this.enableAllStreamInfoRow = i;
        this.enableCacheStreamRow = -1;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.callsSectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.useLessDataForCallsRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.quickRepliesRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.callsSection2Row = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.proxySectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.proxyRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.proxySection2Row = i;
        return true;
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setTitle(LocaleController.getString("DataSettings", NUM));
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    DataSettingsActivity.this.finishFragment();
                }
            }
        });
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        FrameLayout frameLayout = this.fragmentView;
        this.listView = new RecyclerListView(context);
        this.listView.setVerticalScrollBarEnabled(false);
        RecyclerListView recyclerListView = this.listView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false);
        this.layoutManager = linearLayoutManager;
        recyclerListView.setLayoutManager(linearLayoutManager);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener(new DataSettingsActivity$$Lambda$0(this));
        return this.fragmentView;
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$createView$2$DataSettingsActivity(View view, int position, float x, float y) {
        if (position != this.mobileRow && position != this.roamingRow && position != this.wifiRow) {
            if (position != this.resetDownloadRow) {
                if (position == this.storageUsageRow) {
                    presentFragment(new CacheControlActivity());
                    return;
                }
                if (position == this.useLessDataForCallsRow) {
                    SharedPreferences preferences = MessagesController.getGlobalMainSettings();
                    int selected = 0;
                    switch (preferences.getInt("VoipDataSaving", VoIPHelper.getDataSavingDefault())) {
                        case 0:
                            selected = 0;
                            break;
                        case 1:
                            selected = 2;
                            break;
                        case 2:
                            selected = 3;
                            break;
                        case 3:
                            selected = 1;
                            break;
                    }
                    Dialog dlg = AlertsCreator.createSingleChoiceDialog(getParentActivity(), new String[]{LocaleController.getString("UseLessDataNever", NUM), LocaleController.getString("UseLessDataOnRoaming", NUM), LocaleController.getString("UseLessDataOnMobile", NUM), LocaleController.getString("UseLessDataAlways", NUM)}, LocaleController.getString("VoipUseLessData", NUM), selected, new DataSettingsActivity$$Lambda$2(this, preferences, position));
                    setVisibleDialog(dlg);
                    dlg.show();
                    return;
                }
                if (position == this.dataUsageRow) {
                    presentFragment(new DataUsageActivity());
                    return;
                }
                if (position == this.proxyRow) {
                    presentFragment(new ProxyListActivity());
                    return;
                }
                if (position == this.enableStreamRow) {
                    SharedConfig.toggleStreamMedia();
                    ((TextCheckCell) view).setChecked(SharedConfig.streamMedia);
                    return;
                }
                if (position == this.enableAllStreamRow) {
                    SharedConfig.toggleStreamAllVideo();
                    ((TextCheckCell) view).setChecked(SharedConfig.streamAllVideo);
                    return;
                }
                if (position == this.enableMkvRow) {
                    SharedConfig.toggleStreamMkv();
                    ((TextCheckCell) view).setChecked(SharedConfig.streamMkv);
                    return;
                }
                if (position == this.enableCacheStreamRow) {
                    SharedConfig.toggleSaveStreamMedia();
                    ((TextCheckCell) view).setChecked(SharedConfig.saveStreamMedia);
                    return;
                }
                if (position == this.quickRepliesRow) {
                    presentFragment(new QuickRepliesSettingsActivity());
                    return;
                }
                if (position == this.autoplayGifsRow) {
                    SharedConfig.toggleAutoplayGifs();
                    if (view instanceof TextCheckCell) {
                        ((TextCheckCell) view).setChecked(SharedConfig.autoplayGifs);
                        return;
                    }
                    return;
                }
                if (position == this.autoplayVideoRow) {
                    SharedConfig.toggleAutoplayVideo();
                    if (view instanceof TextCheckCell) {
                        ((TextCheckCell) view).setChecked(SharedConfig.autoplayVideo);
                    }
                }
            } else if (getParentActivity() != null && view.isEnabled()) {
                Builder builder = new Builder(getParentActivity());
                builder.setTitle(LocaleController.getString("AppName", NUM));
                builder.setMessage(LocaleController.getString("ResetAutomaticMediaDownloadAlert", NUM));
                builder.setPositiveButton(LocaleController.getString("OK", NUM), new DataSettingsActivity$$Lambda$1(this));
                builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
                builder.show();
            }
        } else if ((!LocaleController.isRTL || x > ((float) AndroidUtilities.dp(76.0f))) && (LocaleController.isRTL || x < ((float) (view.getMeasuredWidth() - AndroidUtilities.dp(76.0f))))) {
            int type;
            if (position == this.mobileRow) {
                type = 0;
            } else {
                if (position == this.wifiRow) {
                    type = 1;
                } else {
                    type = 2;
                }
            }
            presentFragment(new DataAutoDownloadActivity(type));
        } else {
            Preset preset;
            Preset defaultPreset;
            String key;
            String key2;
            int num;
            boolean wasEnabled = this.listAdapter.isRowEnabled(this.resetDownloadRow);
            NotificationsCheckCell cell = (NotificationsCheckCell) view;
            boolean checked = cell.isChecked();
            if (position == this.mobileRow) {
                preset = DownloadController.getInstance(this.currentAccount).mobilePreset;
                defaultPreset = DownloadController.getInstance(this.currentAccount).mediumPreset;
                key = "mobilePreset";
                key2 = "currentMobilePreset";
                num = 0;
            } else {
                if (position == this.wifiRow) {
                    preset = DownloadController.getInstance(this.currentAccount).wifiPreset;
                    defaultPreset = DownloadController.getInstance(this.currentAccount).highPreset;
                    key = "wifiPreset";
                    key2 = "currentWifiPreset";
                    num = 1;
                } else {
                    preset = DownloadController.getInstance(this.currentAccount).roamingPreset;
                    defaultPreset = DownloadController.getInstance(this.currentAccount).lowPreset;
                    key = "roamingPreset";
                    key2 = "currentRoamingPreset";
                    num = 2;
                }
            }
            if (checked || !preset.enabled) {
                preset.enabled = !preset.enabled;
            } else {
                preset.set(defaultPreset);
            }
            Editor editor = MessagesController.getMainSettings(this.currentAccount).edit();
            editor.putString(key, preset.toString());
            editor.putInt(key2, 3);
            editor.commit();
            cell.setChecked(!checked);
            ViewHolder holder = this.listView.findContainingViewHolder(view);
            if (holder != null) {
                this.listAdapter.onBindViewHolder(holder, position);
            }
            DownloadController.getInstance(this.currentAccount).checkAutodownloadSettings();
            DownloadController.getInstance(this.currentAccount).savePresetToServer(num);
            if (wasEnabled != this.listAdapter.isRowEnabled(this.resetDownloadRow)) {
                this.listAdapter.notifyItemChanged(this.resetDownloadRow);
            }
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$0$DataSettingsActivity(DialogInterface dialogInterface, int i) {
        int a;
        Editor editor = MessagesController.getMainSettings(this.currentAccount).edit();
        for (a = 0; a < 3; a++) {
            Preset preset;
            Preset defaultPreset;
            String key;
            if (a == 0) {
                preset = DownloadController.getInstance(this.currentAccount).mobilePreset;
                defaultPreset = DownloadController.getInstance(this.currentAccount).mediumPreset;
                key = "mobilePreset";
            } else if (a == 1) {
                preset = DownloadController.getInstance(this.currentAccount).wifiPreset;
                defaultPreset = DownloadController.getInstance(this.currentAccount).highPreset;
                key = "wifiPreset";
            } else {
                preset = DownloadController.getInstance(this.currentAccount).roamingPreset;
                defaultPreset = DownloadController.getInstance(this.currentAccount).lowPreset;
                key = "roamingPreset";
            }
            preset.set(defaultPreset);
            preset.enabled = defaultPreset.isEnabled();
            DownloadController.getInstance(this.currentAccount).currentMobilePreset = 3;
            editor.putInt("currentMobilePreset", 3);
            DownloadController.getInstance(this.currentAccount).currentWifiPreset = 3;
            editor.putInt("currentWifiPreset", 3);
            DownloadController.getInstance(this.currentAccount).currentRoamingPreset = 3;
            editor.putInt("currentRoamingPreset", 3);
            editor.putString(key, preset.toString());
        }
        editor.commit();
        DownloadController.getInstance(this.currentAccount).checkAutodownloadSettings();
        for (a = 0; a < 3; a++) {
            DownloadController.getInstance(this.currentAccount).savePresetToServer(a);
        }
        this.listAdapter.notifyItemRangeChanged(this.mobileRow, 4);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$1$DataSettingsActivity(SharedPreferences preferences, int position, DialogInterface dialog, int which) {
        int val = -1;
        switch (which) {
            case 0:
                val = 0;
                break;
            case 1:
                val = 3;
                break;
            case 2:
                val = 1;
                break;
            case 3:
                val = 2;
                break;
        }
        if (val != -1) {
            preferences.edit().putInt("VoipDataSaving", val).commit();
        }
        if (this.listAdapter != null) {
            this.listAdapter.notifyItemChanged(position);
        }
    }

    /* Access modifiers changed, original: protected */
    public void onDialogDismiss(Dialog dialog) {
        DownloadController.getInstance(this.currentAccount).checkAutodownloadSettings();
    }

    public void onResume() {
        super.onResume();
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        r9 = new ThemeDescription[23];
        r9[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, TextCheckCell.class, HeaderCell.class, NotificationsCheckCell.class}, null, null, null, "windowBackgroundWhite");
        r9[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray");
        r9[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
        r9[3] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
        r9[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
        r9[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
        r9[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
        r9[7] = new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        r9[8] = new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"valueTextView"}, null, null, null, "windowBackgroundWhiteGrayText2");
        r9[9] = new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, null, null, null, "switchTrack");
        r9[10] = new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, null, null, null, "switchTrackChecked");
        r9[11] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
        r9[12] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, "divider");
        r9[13] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, "windowBackgroundGrayShadow");
        r9[14] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        r9[15] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, "windowBackgroundWhiteValueText");
        r9[16] = new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlueHeader");
        r9[17] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        r9[18] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"valueTextView"}, null, null, null, "windowBackgroundWhiteGrayText2");
        r9[19] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, "switchTrack");
        r9[20] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, "switchTrackChecked");
        r9[21] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, "windowBackgroundGrayShadow");
        r9[22] = new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteGrayText4");
        return r9;
    }
}
