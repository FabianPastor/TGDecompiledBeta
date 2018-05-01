package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import java.util.ArrayList;
import java.util.Collection;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.DownloadController;
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
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public class DataSettingsActivity extends BaseFragment {
    private AnimatorSet animatorSet;
    private int autoDownloadMediaRow;
    private int callsSection2Row;
    private int callsSectionRow;
    private int enableAllStreamInfoRow;
    private int enableAllStreamRow;
    private int enableCacheStreamRow;
    private int enableStreamRow;
    private int filesRow;
    private int gifsRow;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private int mediaDownloadSection2Row;
    private int mediaDownloadSectionRow;
    private int mobileUsageRow;
    private int musicRow;
    private int photosRow;
    private int proxyRow;
    private int proxySection2Row;
    private int proxySectionRow;
    private int quickRepliesRow;
    private int resetDownloadRow;
    private int roamingUsageRow;
    private int rowCount;
    private int storageUsageRow;
    private int streamSectionRow;
    private int usageSection2Row;
    private int usageSectionRow;
    private int useLessDataForCallsRow;
    private int videoMessagesRow;
    private int videosRow;
    private int voiceMessagesRow;
    private int wifiUsageRow;

    /* renamed from: org.telegram.ui.DataSettingsActivity$3 */
    class C13743 extends AnimatorListenerAdapter {
        C13743() {
        }

        public void onAnimationEnd(Animator animator) {
            if (animator.equals(DataSettingsActivity.this.animatorSet) != null) {
                DataSettingsActivity.this.animatorSet = null;
            }
        }
    }

    /* renamed from: org.telegram.ui.DataSettingsActivity$1 */
    class C21231 extends ActionBarMenuOnItemClick {
        C21231() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                DataSettingsActivity.this.finishFragment();
            }
        }
    }

    /* renamed from: org.telegram.ui.DataSettingsActivity$2 */
    class C21242 implements OnItemClickListener {

        /* renamed from: org.telegram.ui.DataSettingsActivity$2$1 */
        class C13721 implements OnClickListener {
            C13721() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface = MessagesController.getMainSettings(DataSettingsActivity.this.currentAccount).edit();
                i = DownloadController.getInstance(DataSettingsActivity.this.currentAccount);
                int i2 = 0;
                int i3 = 0;
                while (i3 < 4) {
                    i.mobileDataDownloadMask[i3] = 115;
                    i.wifiDownloadMask[i3] = 115;
                    i.roamingDownloadMask[i3] = 0;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("mobileDataDownloadMask");
                    stringBuilder.append(i3 != 0 ? Integer.valueOf(i3) : TtmlNode.ANONYMOUS_REGION_ID);
                    dialogInterface.putInt(stringBuilder.toString(), i.mobileDataDownloadMask[i3]);
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("wifiDownloadMask");
                    stringBuilder.append(i3 != 0 ? Integer.valueOf(i3) : TtmlNode.ANONYMOUS_REGION_ID);
                    dialogInterface.putInt(stringBuilder.toString(), i.wifiDownloadMask[i3]);
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("roamingDownloadMask");
                    stringBuilder.append(i3 != 0 ? Integer.valueOf(i3) : TtmlNode.ANONYMOUS_REGION_ID);
                    dialogInterface.putInt(stringBuilder.toString(), i.roamingDownloadMask[i3]);
                    i3++;
                }
                while (i2 < 7) {
                    i3 = i2 == 1 ? 2097152 : i2 == 6 ? 5242880 : 10485760;
                    i.mobileMaxFileSize[i2] = i3;
                    i.wifiMaxFileSize[i2] = i3;
                    i.roamingMaxFileSize[i2] = i3;
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("mobileMaxDownloadSize");
                    stringBuilder.append(i2);
                    dialogInterface.putInt(stringBuilder.toString(), i3);
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("wifiMaxDownloadSize");
                    stringBuilder.append(i2);
                    dialogInterface.putInt(stringBuilder.toString(), i3);
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("roamingMaxDownloadSize");
                    stringBuilder.append(i2);
                    dialogInterface.putInt(stringBuilder.toString(), i3);
                    i2++;
                }
                if (DownloadController.getInstance(DataSettingsActivity.this.currentAccount).globalAutodownloadEnabled == 0) {
                    DownloadController.getInstance(DataSettingsActivity.this.currentAccount).globalAutodownloadEnabled = true;
                    dialogInterface.putBoolean("globalAutodownloadEnabled", DownloadController.getInstance(DataSettingsActivity.this.currentAccount).globalAutodownloadEnabled);
                    DataSettingsActivity.this.updateAutodownloadRows(true);
                }
                dialogInterface.commit();
                DownloadController.getInstance(DataSettingsActivity.this.currentAccount).checkAutodownloadSettings();
            }
        }

        C21242() {
        }

        public void onItemClick(View view, final int i) {
            if (!(i == DataSettingsActivity.this.photosRow || i == DataSettingsActivity.this.voiceMessagesRow || i == DataSettingsActivity.this.videoMessagesRow || i == DataSettingsActivity.this.videosRow || i == DataSettingsActivity.this.filesRow || i == DataSettingsActivity.this.musicRow)) {
                if (i != DataSettingsActivity.this.gifsRow) {
                    if (i == DataSettingsActivity.this.resetDownloadRow) {
                        if (DataSettingsActivity.this.getParentActivity() != null) {
                            view = new Builder(DataSettingsActivity.this.getParentActivity());
                            view.setTitle(LocaleController.getString("AppName", C0446R.string.AppName));
                            view.setMessage(LocaleController.getString("ResetAutomaticMediaDownloadAlert", C0446R.string.ResetAutomaticMediaDownloadAlert));
                            view.setPositiveButton(LocaleController.getString("OK", C0446R.string.OK), new C13721());
                            view.setNegativeButton(LocaleController.getString("Cancel", C0446R.string.Cancel), null);
                            view.show();
                        } else {
                            return;
                        }
                    } else if (i == DataSettingsActivity.this.autoDownloadMediaRow) {
                        DownloadController.getInstance(DataSettingsActivity.this.currentAccount).globalAutodownloadEnabled = DownloadController.getInstance(DataSettingsActivity.this.currentAccount).globalAutodownloadEnabled ^ true;
                        MessagesController.getMainSettings(DataSettingsActivity.this.currentAccount).edit().putBoolean("globalAutodownloadEnabled", DownloadController.getInstance(DataSettingsActivity.this.currentAccount).globalAutodownloadEnabled).commit();
                        ((TextCheckCell) view).setChecked(DownloadController.getInstance(DataSettingsActivity.this.currentAccount).globalAutodownloadEnabled);
                        DataSettingsActivity.this.updateAutodownloadRows(false);
                    } else if (i == DataSettingsActivity.this.storageUsageRow) {
                        DataSettingsActivity.this.presentFragment(new CacheControlActivity());
                    } else if (i == DataSettingsActivity.this.useLessDataForCallsRow) {
                        view = MessagesController.getGlobalMainSettings();
                        view = AlertsCreator.createSingleChoiceDialog(DataSettingsActivity.this.getParentActivity(), DataSettingsActivity.this, new String[]{LocaleController.getString("UseLessDataNever", C0446R.string.UseLessDataNever), LocaleController.getString("UseLessDataOnMobile", C0446R.string.UseLessDataOnMobile), LocaleController.getString("UseLessDataAlways", C0446R.string.UseLessDataAlways)}, LocaleController.getString("VoipUseLessData", C0446R.string.VoipUseLessData), view.getInt("VoipDataSaving", 0), new OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                switch (i) {
                                    case 0:
                                        i = 0;
                                        break;
                                    case 1:
                                        i = 1;
                                        break;
                                    case 2:
                                        i = 2;
                                        break;
                                    default:
                                        i = -1;
                                        break;
                                }
                                if (i != -1) {
                                    view.edit().putInt("VoipDataSaving", i).commit();
                                }
                                if (DataSettingsActivity.this.listAdapter != null) {
                                    DataSettingsActivity.this.listAdapter.notifyItemChanged(i);
                                }
                            }
                        });
                        DataSettingsActivity.this.setVisibleDialog(view);
                        view.show();
                    } else if (i == DataSettingsActivity.this.mobileUsageRow) {
                        DataSettingsActivity.this.presentFragment(new DataUsageActivity(0));
                    } else if (i == DataSettingsActivity.this.roamingUsageRow) {
                        DataSettingsActivity.this.presentFragment(new DataUsageActivity(2));
                    } else if (i == DataSettingsActivity.this.wifiUsageRow) {
                        DataSettingsActivity.this.presentFragment(new DataUsageActivity(1));
                    } else if (i == DataSettingsActivity.this.proxyRow) {
                        DataSettingsActivity.this.presentFragment(new ProxySettingsActivity());
                    } else if (i == DataSettingsActivity.this.enableStreamRow) {
                        SharedConfig.toggleStreamMedia();
                        ((TextCheckCell) view).setChecked(SharedConfig.streamMedia);
                    } else if (i == DataSettingsActivity.this.enableAllStreamRow) {
                        SharedConfig.toggleStreamAllVideo();
                        ((TextCheckCell) view).setChecked(SharedConfig.streamAllVideo);
                    } else if (i == DataSettingsActivity.this.enableCacheStreamRow) {
                        SharedConfig.toggleSaveStreamMedia();
                        ((TextCheckCell) view).setChecked(SharedConfig.saveStreamMedia);
                    } else if (i == DataSettingsActivity.this.quickRepliesRow) {
                        DataSettingsActivity.this.presentFragment(new QuickRepliesSettingsActivity());
                    }
                }
            }
            if (DownloadController.getInstance(DataSettingsActivity.this.currentAccount).globalAutodownloadEnabled != null) {
                if (i == DataSettingsActivity.this.photosRow) {
                    DataSettingsActivity.this.presentFragment(new DataAutoDownloadActivity(1));
                } else if (i == DataSettingsActivity.this.voiceMessagesRow) {
                    DataSettingsActivity.this.presentFragment(new DataAutoDownloadActivity(2));
                } else if (i == DataSettingsActivity.this.videoMessagesRow) {
                    DataSettingsActivity.this.presentFragment(new DataAutoDownloadActivity(64));
                } else if (i == DataSettingsActivity.this.videosRow) {
                    DataSettingsActivity.this.presentFragment(new DataAutoDownloadActivity(4));
                } else if (i == DataSettingsActivity.this.filesRow) {
                    DataSettingsActivity.this.presentFragment(new DataAutoDownloadActivity(8));
                } else if (i == DataSettingsActivity.this.musicRow) {
                    DataSettingsActivity.this.presentFragment(new DataAutoDownloadActivity(16));
                } else if (i == DataSettingsActivity.this.gifsRow) {
                    DataSettingsActivity.this.presentFragment(new DataAutoDownloadActivity(32));
                }
            }
        }
    }

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return DataSettingsActivity.this.rowCount;
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            boolean z = false;
            switch (viewHolder.getItemViewType()) {
                case 0:
                    if (i == DataSettingsActivity.this.proxySection2Row) {
                        viewHolder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, C0446R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else {
                        viewHolder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, C0446R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                        return;
                    }
                case 1:
                    TextSettingsCell textSettingsCell = (TextSettingsCell) viewHolder.itemView;
                    textSettingsCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                    if (i == DataSettingsActivity.this.storageUsageRow) {
                        textSettingsCell.setText(LocaleController.getString("StorageUsage", C0446R.string.StorageUsage), true);
                        return;
                    } else if (i == DataSettingsActivity.this.useLessDataForCallsRow) {
                        String str = null;
                        switch (MessagesController.getGlobalMainSettings().getInt("VoipDataSaving", 0)) {
                            case 0:
                                str = LocaleController.getString("UseLessDataNever", C0446R.string.UseLessDataNever);
                                break;
                            case 1:
                                str = LocaleController.getString("UseLessDataOnMobile", C0446R.string.UseLessDataOnMobile);
                                break;
                            case 2:
                                str = LocaleController.getString("UseLessDataAlways", C0446R.string.UseLessDataAlways);
                                break;
                            default:
                                break;
                        }
                        textSettingsCell.setTextAndValue(LocaleController.getString("VoipUseLessData", C0446R.string.VoipUseLessData), str, true);
                        return;
                    } else if (i == DataSettingsActivity.this.mobileUsageRow) {
                        textSettingsCell.setText(LocaleController.getString("MobileUsage", C0446R.string.MobileUsage), true);
                        return;
                    } else if (i == DataSettingsActivity.this.roamingUsageRow) {
                        textSettingsCell.setText(LocaleController.getString("RoamingUsage", C0446R.string.RoamingUsage), false);
                        return;
                    } else if (i == DataSettingsActivity.this.wifiUsageRow) {
                        textSettingsCell.setText(LocaleController.getString("WiFiUsage", C0446R.string.WiFiUsage), true);
                        return;
                    } else if (i == DataSettingsActivity.this.proxyRow) {
                        textSettingsCell.setText(LocaleController.getString("ProxySettings", C0446R.string.ProxySettings), true);
                        return;
                    } else if (i == DataSettingsActivity.this.resetDownloadRow) {
                        textSettingsCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText));
                        textSettingsCell.setText(LocaleController.getString("ResetAutomaticMediaDownload", C0446R.string.ResetAutomaticMediaDownload), false);
                        return;
                    } else if (i == DataSettingsActivity.this.photosRow) {
                        textSettingsCell.setText(LocaleController.getString("LocalPhotoCache", C0446R.string.LocalPhotoCache), true);
                        return;
                    } else if (i == DataSettingsActivity.this.voiceMessagesRow) {
                        textSettingsCell.setText(LocaleController.getString("AudioAutodownload", C0446R.string.AudioAutodownload), true);
                        return;
                    } else if (i == DataSettingsActivity.this.videoMessagesRow) {
                        textSettingsCell.setText(LocaleController.getString("VideoMessagesAutodownload", C0446R.string.VideoMessagesAutodownload), true);
                        return;
                    } else if (i == DataSettingsActivity.this.videosRow) {
                        textSettingsCell.setText(LocaleController.getString("LocalVideoCache", C0446R.string.LocalVideoCache), true);
                        return;
                    } else if (i == DataSettingsActivity.this.filesRow) {
                        textSettingsCell.setText(LocaleController.getString("FilesDataUsage", C0446R.string.FilesDataUsage), true);
                        return;
                    } else if (i == DataSettingsActivity.this.musicRow) {
                        textSettingsCell.setText(LocaleController.getString("AttachMusic", C0446R.string.AttachMusic), true);
                        return;
                    } else if (i == DataSettingsActivity.this.gifsRow) {
                        textSettingsCell.setText(LocaleController.getString("LocalGifCache", C0446R.string.LocalGifCache), true);
                        return;
                    } else if (i == DataSettingsActivity.this.quickRepliesRow) {
                        textSettingsCell.setText(LocaleController.getString("VoipQuickReplies", C0446R.string.VoipQuickReplies), false);
                        return;
                    } else {
                        return;
                    }
                case 2:
                    HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
                    if (i == DataSettingsActivity.this.mediaDownloadSectionRow) {
                        headerCell.setText(LocaleController.getString("AutomaticMediaDownload", C0446R.string.AutomaticMediaDownload));
                        return;
                    } else if (i == DataSettingsActivity.this.usageSectionRow) {
                        headerCell.setText(LocaleController.getString("DataUsage", C0446R.string.DataUsage));
                        return;
                    } else if (i == DataSettingsActivity.this.callsSectionRow) {
                        headerCell.setText(LocaleController.getString("Calls", C0446R.string.Calls));
                        return;
                    } else if (i == DataSettingsActivity.this.proxySectionRow) {
                        headerCell.setText(LocaleController.getString("Proxy", C0446R.string.Proxy));
                        return;
                    } else if (i == DataSettingsActivity.this.streamSectionRow) {
                        headerCell.setText(LocaleController.getString("Streaming", C0446R.string.Streaming));
                        return;
                    } else {
                        return;
                    }
                case 3:
                    TextCheckCell textCheckCell = (TextCheckCell) viewHolder.itemView;
                    if (i == DataSettingsActivity.this.autoDownloadMediaRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("AutoDownloadMedia", C0446R.string.AutoDownloadMedia), DownloadController.getInstance(DataSettingsActivity.this.currentAccount).globalAutodownloadEnabled, true);
                        return;
                    } else if (i == DataSettingsActivity.this.enableStreamRow) {
                        i = LocaleController.getString("EnableStreaming", C0446R.string.EnableStreaming);
                        boolean z2 = SharedConfig.streamMedia;
                        if (DataSettingsActivity.this.enableAllStreamRow != -1) {
                            z = true;
                        }
                        textCheckCell.setTextAndCheck(i, z2, z);
                        return;
                    } else if (i != DataSettingsActivity.this.enableCacheStreamRow) {
                        if (i == DataSettingsActivity.this.enableAllStreamRow) {
                            textCheckCell.setTextAndCheck("Try to Stream All Videos", SharedConfig.streamAllVideo, false);
                            return;
                        }
                        return;
                    } else {
                        return;
                    }
                case 4:
                    TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
                    if (i == DataSettingsActivity.this.enableAllStreamInfoRow) {
                        textInfoPrivacyCell.setText(LocaleController.getString("EnableAllStreamingInfo", C0446R.string.EnableAllStreamingInfo));
                        return;
                    }
                    return;
                default:
                    return;
            }
        }

        public void onViewAttachedToWindow(ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType == 1) {
                itemViewType = viewHolder.getAdapterPosition();
                TextSettingsCell textSettingsCell = (TextSettingsCell) viewHolder.itemView;
                if (itemViewType < DataSettingsActivity.this.photosRow || itemViewType > DataSettingsActivity.this.gifsRow) {
                    textSettingsCell.setEnabled(true, null);
                } else {
                    textSettingsCell.setEnabled(DownloadController.getInstance(DataSettingsActivity.this.currentAccount).globalAutodownloadEnabled, null);
                }
            } else if (itemViewType == 3) {
                TextCheckCell textCheckCell = (TextCheckCell) viewHolder.itemView;
                viewHolder = viewHolder.getAdapterPosition();
                if (viewHolder == DataSettingsActivity.this.enableCacheStreamRow) {
                    textCheckCell.setChecked(SharedConfig.saveStreamMedia);
                } else if (viewHolder == DataSettingsActivity.this.enableStreamRow) {
                    textCheckCell.setChecked(SharedConfig.streamMedia);
                } else if (viewHolder == DataSettingsActivity.this.autoDownloadMediaRow) {
                    textCheckCell.setChecked(DownloadController.getInstance(DataSettingsActivity.this.currentAccount).globalAutodownloadEnabled);
                } else if (viewHolder == DataSettingsActivity.this.enableAllStreamRow) {
                    textCheckCell.setChecked(SharedConfig.streamAllVideo);
                }
            }
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            viewHolder = viewHolder.getAdapterPosition();
            if (!(viewHolder == DataSettingsActivity.this.photosRow || viewHolder == DataSettingsActivity.this.voiceMessagesRow || viewHolder == DataSettingsActivity.this.videoMessagesRow || viewHolder == DataSettingsActivity.this.videosRow || viewHolder == DataSettingsActivity.this.filesRow || viewHolder == DataSettingsActivity.this.musicRow)) {
                if (viewHolder != DataSettingsActivity.this.gifsRow) {
                    if (!(viewHolder == DataSettingsActivity.this.storageUsageRow || viewHolder == DataSettingsActivity.this.useLessDataForCallsRow || viewHolder == DataSettingsActivity.this.mobileUsageRow || viewHolder == DataSettingsActivity.this.roamingUsageRow || viewHolder == DataSettingsActivity.this.wifiUsageRow || viewHolder == DataSettingsActivity.this.proxyRow || viewHolder == DataSettingsActivity.this.resetDownloadRow || viewHolder == DataSettingsActivity.this.autoDownloadMediaRow || viewHolder == DataSettingsActivity.this.enableCacheStreamRow || viewHolder == DataSettingsActivity.this.enableStreamRow || viewHolder == DataSettingsActivity.this.enableAllStreamRow)) {
                        if (viewHolder != DataSettingsActivity.this.quickRepliesRow) {
                            viewHolder = null;
                            return viewHolder;
                        }
                    }
                    viewHolder = true;
                    return viewHolder;
                }
            }
            return DownloadController.getInstance(DataSettingsActivity.this.currentAccount).globalAutodownloadEnabled;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            switch (i) {
                case 0:
                    viewGroup = new ShadowSectionCell(this.mContext);
                    break;
                case 1:
                    viewGroup = new TextSettingsCell(this.mContext);
                    viewGroup.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 2:
                    viewGroup = new HeaderCell(this.mContext);
                    viewGroup.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 3:
                    viewGroup = new TextCheckCell(this.mContext);
                    viewGroup.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 4:
                    viewGroup = new TextInfoPrivacyCell(this.mContext);
                    viewGroup.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, C0446R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                    break;
                default:
                    viewGroup = null;
                    break;
            }
            viewGroup.setLayoutParams(new LayoutParams(-1, -2));
            return new Holder(viewGroup);
        }

        public int getItemViewType(int i) {
            if (!(i == DataSettingsActivity.this.mediaDownloadSection2Row || i == DataSettingsActivity.this.usageSection2Row || i == DataSettingsActivity.this.callsSection2Row)) {
                if (i != DataSettingsActivity.this.proxySection2Row) {
                    if (!(i == DataSettingsActivity.this.mediaDownloadSectionRow || i == DataSettingsActivity.this.streamSectionRow || i == DataSettingsActivity.this.callsSectionRow || i == DataSettingsActivity.this.usageSectionRow)) {
                        if (i != DataSettingsActivity.this.proxySectionRow) {
                            if (!(i == DataSettingsActivity.this.autoDownloadMediaRow || i == DataSettingsActivity.this.enableCacheStreamRow || i == DataSettingsActivity.this.enableStreamRow)) {
                                if (i != DataSettingsActivity.this.enableAllStreamRow) {
                                    return i == DataSettingsActivity.this.enableAllStreamInfoRow ? 4 : 1;
                                }
                            }
                            return 3;
                        }
                    }
                    return 2;
                }
            }
            return 0;
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        this.rowCount = 0;
        int i = this.rowCount;
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
        i = this.rowCount;
        this.rowCount = i + 1;
        this.usageSection2Row = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.mediaDownloadSectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.autoDownloadMediaRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.photosRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.voiceMessagesRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.videoMessagesRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.videosRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.filesRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.musicRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.gifsRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.resetDownloadRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.mediaDownloadSection2Row = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.streamSectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.enableStreamRow = i;
        if (BuildVars.DEBUG_VERSION) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.enableAllStreamRow = i;
        } else {
            this.enableAllStreamRow = -1;
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
        this.actionBar.setBackButtonImage(C0446R.drawable.ic_ab_back);
        this.actionBar.setTitle(LocaleController.getString("DataSettings", C0446R.string.DataSettings));
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new C21231());
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        this.listView = new RecyclerListView(context);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener(new C21242());
        frameLayout.addView(this.actionBar);
        return this.fragmentView;
    }

    protected void onDialogDismiss(Dialog dialog) {
        DownloadController.getInstance(this.currentAccount).checkAutodownloadSettings();
    }

    public void onResume() {
        super.onResume();
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
    }

    private void updateAutodownloadRows(boolean z) {
        int childCount = this.listView.getChildCount();
        Collection arrayList = new ArrayList();
        for (int i = 0; i < childCount; i++) {
            Holder holder = (Holder) this.listView.getChildViewHolder(this.listView.getChildAt(i));
            holder.getItemViewType();
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition >= this.photosRow && adapterPosition <= this.gifsRow) {
                ((TextSettingsCell) holder.itemView).setEnabled(DownloadController.getInstance(this.currentAccount).globalAutodownloadEnabled, arrayList);
            } else if (z && adapterPosition == this.autoDownloadMediaRow) {
                ((TextCheckCell) holder.itemView).setChecked(true);
            }
        }
        if (!arrayList.isEmpty()) {
            if (this.animatorSet) {
                this.animatorSet.cancel();
            }
            this.animatorSet = new AnimatorSet();
            this.animatorSet.playTogether(arrayList);
            this.animatorSet.addListener(new C13743());
            this.animatorSet.setDuration(150);
            this.animatorSet.start();
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        r1 = new ThemeDescription[21];
        r1[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, TextCheckCell.class, HeaderCell.class}, null, null, null, Theme.key_windowBackgroundWhite);
        r1[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray);
        r1[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault);
        r1[3] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault);
        r1[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon);
        r1[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle);
        r1[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector);
        r1[7] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        r1[8] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider);
        r1[9] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        r1[10] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r1[11] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteValueText);
        r1[12] = new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlueHeader);
        r1[13] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r1[14] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2);
        r1[15] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchThumb);
        r1[16] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrack);
        r1[17] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchThumbChecked);
        r1[18] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrackChecked);
        r1[19] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        r1[20] = new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText4);
        return r1;
    }
}
