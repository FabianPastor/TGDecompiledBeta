package org.telegram.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.SharedConfig;
import org.telegram.tgnet.RequestDelegate;
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
import org.telegram.ui.Components.AlertsCreator;
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
        int i3 = i2 + 1;
        this.rowCount = i3;
        this.dataUsageRow = i2;
        int i4 = i3 + 1;
        this.rowCount = i4;
        this.usageSection2Row = i3;
        int i5 = i4 + 1;
        this.rowCount = i5;
        this.mediaDownloadSectionRow = i4;
        int i6 = i5 + 1;
        this.rowCount = i6;
        this.mobileRow = i5;
        int i7 = i6 + 1;
        this.rowCount = i7;
        this.wifiRow = i6;
        int i8 = i7 + 1;
        this.rowCount = i8;
        this.roamingRow = i7;
        int i9 = i8 + 1;
        this.rowCount = i9;
        this.resetDownloadRow = i8;
        int i10 = i9 + 1;
        this.rowCount = i10;
        this.mediaDownloadSection2Row = i9;
        int i11 = i10 + 1;
        this.rowCount = i11;
        this.autoplayHeaderRow = i10;
        int i12 = i11 + 1;
        this.rowCount = i12;
        this.autoplayGifsRow = i11;
        int i13 = i12 + 1;
        this.rowCount = i13;
        this.autoplayVideoRow = i12;
        int i14 = i13 + 1;
        this.rowCount = i14;
        this.autoplaySectionRow = i13;
        int i15 = i14 + 1;
        this.rowCount = i15;
        this.streamSectionRow = i14;
        int i16 = i15 + 1;
        this.rowCount = i16;
        this.enableStreamRow = i15;
        if (BuildVars.DEBUG_VERSION) {
            int i17 = i16 + 1;
            this.rowCount = i17;
            this.enableMkvRow = i16;
            this.rowCount = i17 + 1;
            this.enableAllStreamRow = i17;
        } else {
            this.enableAllStreamRow = -1;
            this.enableMkvRow = -1;
        }
        int i18 = this.rowCount;
        int i19 = i18 + 1;
        this.rowCount = i19;
        this.enableAllStreamInfoRow = i18;
        this.enableCacheStreamRow = -1;
        int i20 = i19 + 1;
        this.rowCount = i20;
        this.callsSectionRow = i19;
        int i21 = i20 + 1;
        this.rowCount = i21;
        this.useLessDataForCallsRow = i20;
        int i22 = i21 + 1;
        this.rowCount = i22;
        this.quickRepliesRow = i21;
        int i23 = i22 + 1;
        this.rowCount = i23;
        this.callsSection2Row = i22;
        int i24 = i23 + 1;
        this.rowCount = i24;
        this.proxySectionRow = i23;
        int i25 = i24 + 1;
        this.rowCount = i25;
        this.proxyRow = i24;
        int i26 = i25 + 1;
        this.rowCount = i26;
        this.proxySection2Row = i25;
        int i27 = i26 + 1;
        this.rowCount = i27;
        this.clearDraftsRow = i26;
        this.rowCount = i27 + 1;
        this.clearDraftsSectionRow = i27;
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
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListenerExtended) new RecyclerListView.OnItemClickListenerExtended() {
            public final void onItemClick(View view, int i, float f, float f2) {
                DataSettingsActivity.this.lambda$createView$5$DataSettingsActivity(view, i, f, f2);
            }
        });
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$5$DataSettingsActivity(View view, int i, float f, float f2) {
        String str;
        String str2;
        DownloadController.Preset preset;
        DownloadController.Preset preset2;
        int i2;
        int i3 = 2;
        int i4 = 0;
        if (i == this.mobileRow || i == this.roamingRow || i == this.wifiRow) {
            if ((!LocaleController.isRTL || f > ((float) AndroidUtilities.dp(76.0f))) && (LocaleController.isRTL || f < ((float) (view.getMeasuredWidth() - AndroidUtilities.dp(76.0f))))) {
                if (i == this.mobileRow) {
                    i3 = 0;
                } else if (i == this.wifiRow) {
                    i3 = 1;
                }
                presentFragment(new DataAutoDownloadActivity(i3));
                return;
            }
            boolean isRowEnabled = this.listAdapter.isRowEnabled(this.resetDownloadRow);
            NotificationsCheckCell notificationsCheckCell = (NotificationsCheckCell) view;
            boolean isChecked = notificationsCheckCell.isChecked();
            if (i == this.mobileRow) {
                preset2 = DownloadController.getInstance(this.currentAccount).mobilePreset;
                preset = DownloadController.getInstance(this.currentAccount).mediumPreset;
                str2 = "mobilePreset";
                str = "currentMobilePreset";
            } else if (i == this.wifiRow) {
                preset2 = DownloadController.getInstance(this.currentAccount).wifiPreset;
                preset = DownloadController.getInstance(this.currentAccount).highPreset;
                str2 = "wifiPreset";
                str = "currentWifiPreset";
                i4 = 1;
            } else {
                DownloadController.Preset preset3 = DownloadController.getInstance(this.currentAccount).roamingPreset;
                preset = DownloadController.getInstance(this.currentAccount).lowPreset;
                str2 = "roamingPreset";
                str = "currentRoamingPreset";
                preset2 = preset3;
                i4 = 2;
            }
            if (isChecked || !preset2.enabled) {
                preset2.enabled = !preset2.enabled;
            } else {
                preset2.set(preset);
            }
            SharedPreferences.Editor edit = MessagesController.getMainSettings(this.currentAccount).edit();
            edit.putString(str2, preset2.toString());
            edit.putInt(str, 3);
            edit.commit();
            notificationsCheckCell.setChecked(!isChecked);
            RecyclerView.ViewHolder findContainingViewHolder = this.listView.findContainingViewHolder(view);
            if (findContainingViewHolder != null) {
                this.listAdapter.onBindViewHolder(findContainingViewHolder, i);
            }
            DownloadController.getInstance(this.currentAccount).checkAutodownloadSettings();
            DownloadController.getInstance(this.currentAccount).savePresetToServer(i4);
            if (isRowEnabled != this.listAdapter.isRowEnabled(this.resetDownloadRow)) {
                this.listAdapter.notifyItemChanged(this.resetDownloadRow);
            }
        } else if (i == this.resetDownloadRow) {
            if (getParentActivity() != null && view.isEnabled()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                builder.setTitle(LocaleController.getString("ResetAutomaticMediaDownloadAlertTitle", NUM));
                builder.setMessage(LocaleController.getString("ResetAutomaticMediaDownloadAlert", NUM));
                builder.setPositiveButton(LocaleController.getString("Reset", NUM), new DialogInterface.OnClickListener() {
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        DataSettingsActivity.this.lambda$null$0$DataSettingsActivity(dialogInterface, i);
                    }
                });
                builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                AlertDialog create = builder.create();
                showDialog(create);
                TextView textView = (TextView) create.getButton(-1);
                if (textView != null) {
                    textView.setTextColor(Theme.getColor("dialogTextRed2"));
                }
            }
        } else if (i == this.storageUsageRow) {
            presentFragment(new CacheControlActivity());
        } else if (i == this.useLessDataForCallsRow) {
            SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
            int i5 = globalMainSettings.getInt("VoipDataSaving", VoIPHelper.getDataSavingDefault());
            if (i5 != 0) {
                if (i5 == 1) {
                    i2 = 2;
                } else if (i5 == 2) {
                    i2 = 3;
                } else if (i5 == 3) {
                    i2 = 1;
                }
                Dialog createSingleChoiceDialog = AlertsCreator.createSingleChoiceDialog(getParentActivity(), new String[]{LocaleController.getString("UseLessDataNever", NUM), LocaleController.getString("UseLessDataOnRoaming", NUM), LocaleController.getString("UseLessDataOnMobile", NUM), LocaleController.getString("UseLessDataAlways", NUM)}, LocaleController.getString("VoipUseLessData", NUM), i2, new DialogInterface.OnClickListener(globalMainSettings, i) {
                    public final /* synthetic */ SharedPreferences f$1;
                    public final /* synthetic */ int f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void onClick(DialogInterface dialogInterface, int i) {
                        DataSettingsActivity.this.lambda$null$1$DataSettingsActivity(this.f$1, this.f$2, dialogInterface, i);
                    }
                });
                setVisibleDialog(createSingleChoiceDialog);
                createSingleChoiceDialog.show();
            }
            i2 = 0;
            Dialog createSingleChoiceDialog2 = AlertsCreator.createSingleChoiceDialog(getParentActivity(), new String[]{LocaleController.getString("UseLessDataNever", NUM), LocaleController.getString("UseLessDataOnRoaming", NUM), LocaleController.getString("UseLessDataOnMobile", NUM), LocaleController.getString("UseLessDataAlways", NUM)}, LocaleController.getString("VoipUseLessData", NUM), i2, new DialogInterface.OnClickListener(globalMainSettings, i) {
                public final /* synthetic */ SharedPreferences f$1;
                public final /* synthetic */ int f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    DataSettingsActivity.this.lambda$null$1$DataSettingsActivity(this.f$1, this.f$2, dialogInterface, i);
                }
            });
            setVisibleDialog(createSingleChoiceDialog2);
            createSingleChoiceDialog2.show();
        } else if (i == this.dataUsageRow) {
            presentFragment(new DataUsageActivity());
        } else if (i == this.proxyRow) {
            presentFragment(new ProxyListActivity());
        } else if (i == this.enableStreamRow) {
            SharedConfig.toggleStreamMedia();
            ((TextCheckCell) view).setChecked(SharedConfig.streamMedia);
        } else if (i == this.enableAllStreamRow) {
            SharedConfig.toggleStreamAllVideo();
            ((TextCheckCell) view).setChecked(SharedConfig.streamAllVideo);
        } else if (i == this.enableMkvRow) {
            SharedConfig.toggleStreamMkv();
            ((TextCheckCell) view).setChecked(SharedConfig.streamMkv);
        } else if (i == this.enableCacheStreamRow) {
            SharedConfig.toggleSaveStreamMedia();
            ((TextCheckCell) view).setChecked(SharedConfig.saveStreamMedia);
        } else if (i == this.quickRepliesRow) {
            presentFragment(new QuickRepliesSettingsActivity());
        } else if (i == this.autoplayGifsRow) {
            SharedConfig.toggleAutoplayGifs();
            if (view instanceof TextCheckCell) {
                ((TextCheckCell) view).setChecked(SharedConfig.autoplayGifs);
            }
        } else if (i == this.autoplayVideoRow) {
            SharedConfig.toggleAutoplayVideo();
            if (view instanceof TextCheckCell) {
                ((TextCheckCell) view).setChecked(SharedConfig.autoplayVideo);
            }
        } else if (i == this.clearDraftsRow) {
            AlertDialog.Builder builder2 = new AlertDialog.Builder(getParentActivity());
            builder2.setTitle(LocaleController.getString("AreYouSureClearDraftsTitle", NUM));
            builder2.setMessage(LocaleController.getString("AreYouSureClearDrafts", NUM));
            builder2.setPositiveButton(LocaleController.getString("Delete", NUM), new DialogInterface.OnClickListener() {
                public final void onClick(DialogInterface dialogInterface, int i) {
                    DataSettingsActivity.this.lambda$null$4$DataSettingsActivity(dialogInterface, i);
                }
            });
            builder2.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
            AlertDialog create2 = builder2.create();
            showDialog(create2);
            TextView textView2 = (TextView) create2.getButton(-1);
            if (textView2 != null) {
                textView2.setTextColor(Theme.getColor("dialogTextRed2"));
            }
        }
    }

    public /* synthetic */ void lambda$null$0$DataSettingsActivity(DialogInterface dialogInterface, int i) {
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

    public /* synthetic */ void lambda$null$1$DataSettingsActivity(SharedPreferences sharedPreferences, int i, DialogInterface dialogInterface, int i2) {
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

    public /* synthetic */ void lambda$null$4$DataSettingsActivity(DialogInterface dialogInterface, int i) {
        getConnectionsManager().sendRequest(new TLRPC$TL_messages_clearAllDrafts(), new RequestDelegate() {
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                DataSettingsActivity.this.lambda$null$3$DataSettingsActivity(tLObject, tLRPC$TL_error);
            }
        });
    }

    public /* synthetic */ void lambda$null$2$DataSettingsActivity() {
        getMediaDataController().clearAllDrafts(true);
    }

    public /* synthetic */ void lambda$null$3$DataSettingsActivity(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                DataSettingsActivity.this.lambda$null$2$DataSettingsActivity();
            }
        });
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
            String str;
            NotificationsCheckCell notificationsCheckCell;
            RecyclerView.ViewHolder viewHolder2 = viewHolder;
            int i2 = i;
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType != 0) {
                boolean z2 = false;
                if (itemViewType == 1) {
                    TextSettingsCell textSettingsCell = (TextSettingsCell) viewHolder2.itemView;
                    textSettingsCell.setCanDisable(false);
                    textSettingsCell.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
                    if (i2 == DataSettingsActivity.this.storageUsageRow) {
                        textSettingsCell.setText(LocaleController.getString("StorageUsage", NUM), true);
                    } else if (i2 == DataSettingsActivity.this.useLessDataForCallsRow) {
                        String str2 = null;
                        int i3 = MessagesController.getGlobalMainSettings().getInt("VoipDataSaving", VoIPHelper.getDataSavingDefault());
                        if (i3 == 0) {
                            str2 = LocaleController.getString("UseLessDataNever", NUM);
                        } else if (i3 == 1) {
                            str2 = LocaleController.getString("UseLessDataOnMobile", NUM);
                        } else if (i3 == 2) {
                            str2 = LocaleController.getString("UseLessDataAlways", NUM);
                        } else if (i3 == 3) {
                            str2 = LocaleController.getString("UseLessDataOnRoaming", NUM);
                        }
                        textSettingsCell.setTextAndValue(LocaleController.getString("VoipUseLessData", NUM), str2, true);
                    } else if (i2 == DataSettingsActivity.this.dataUsageRow) {
                        textSettingsCell.setText(LocaleController.getString("NetworkUsage", NUM), false);
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
                    }
                } else if (itemViewType == 3) {
                    TextCheckCell textCheckCell = (TextCheckCell) viewHolder2.itemView;
                    if (i2 == DataSettingsActivity.this.enableStreamRow) {
                        String string2 = LocaleController.getString("EnableStreaming", NUM);
                        boolean z3 = SharedConfig.streamMedia;
                        if (DataSettingsActivity.this.enableAllStreamRow != -1) {
                            z2 = true;
                        }
                        textCheckCell.setTextAndCheck(string2, z3, z2);
                    } else if (i2 != DataSettingsActivity.this.enableCacheStreamRow) {
                        if (i2 == DataSettingsActivity.this.enableMkvRow) {
                            textCheckCell.setTextAndCheck("(beta only) Show MKV as Video", SharedConfig.streamMkv, true);
                        } else if (i2 == DataSettingsActivity.this.enableAllStreamRow) {
                            textCheckCell.setTextAndCheck("(beta only) Stream All Videos", SharedConfig.streamAllVideo, false);
                        } else if (i2 == DataSettingsActivity.this.autoplayGifsRow) {
                            textCheckCell.setTextAndCheck(LocaleController.getString("AutoplayGIF", NUM), SharedConfig.autoplayGifs, true);
                        } else if (i2 == DataSettingsActivity.this.autoplayVideoRow) {
                            textCheckCell.setTextAndCheck(LocaleController.getString("AutoplayVideo", NUM), SharedConfig.autoplayVideo, false);
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
                    String str3 = string;
                    int i4 = 0;
                    boolean z4 = false;
                    int i5 = 0;
                    boolean z5 = false;
                    boolean z6 = false;
                    while (true) {
                        int[] iArr = currentRoamingPreset.mask;
                        if (i4 >= iArr.length) {
                            break;
                        }
                        if (!z4 && (iArr[i4] & 1) != 0) {
                            i5++;
                            z4 = true;
                        }
                        if (!z5 && (currentRoamingPreset.mask[i4] & 4) != 0) {
                            i5++;
                            z5 = true;
                        }
                        if (!z6 && (currentRoamingPreset.mask[i4] & 8) != 0) {
                            i5++;
                            z6 = true;
                        }
                        i4++;
                    }
                    if (!currentRoamingPreset.enabled || i5 == 0) {
                        notificationsCheckCell = notificationsCheckCell2;
                        str = str3;
                        sb.append(LocaleController.getString("NoMediaAutoDownload", NUM));
                    } else {
                        if (z4) {
                            sb.append(LocaleController.getString("AutoDownloadPhotosOn", NUM));
                        }
                        if (z5) {
                            if (sb.length() > 0) {
                                sb.append(", ");
                            }
                            sb.append(LocaleController.getString("AutoDownloadVideosOn", NUM));
                            notificationsCheckCell = notificationsCheckCell2;
                            str = str3;
                            sb.append(String.format(" (%1$s)", new Object[]{AndroidUtilities.formatFileSize((long) currentRoamingPreset.sizes[DownloadController.typeToIndex(4)], true)}));
                        } else {
                            notificationsCheckCell = notificationsCheckCell2;
                            str = str3;
                        }
                        if (z6) {
                            if (sb.length() > 0) {
                                sb.append(", ");
                            }
                            sb.append(LocaleController.getString("AutoDownloadFilesOn", NUM));
                            sb.append(String.format(" (%1$s)", new Object[]{AndroidUtilities.formatFileSize((long) currentRoamingPreset.sizes[DownloadController.typeToIndex(8)], true)}));
                        }
                    }
                    notificationsCheckCell.setTextAndValueAndCheck(str, sb, (z4 || z5 || z6) && z, 0, true, true);
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
            } else if (i == DataSettingsActivity.this.mobileRow || i == DataSettingsActivity.this.roamingRow || i == DataSettingsActivity.this.wifiRow || i == DataSettingsActivity.this.storageUsageRow || i == DataSettingsActivity.this.useLessDataForCallsRow || i == DataSettingsActivity.this.dataUsageRow || i == DataSettingsActivity.this.proxyRow || i == DataSettingsActivity.this.clearDraftsRow || i == DataSettingsActivity.this.enableCacheStreamRow || i == DataSettingsActivity.this.enableStreamRow || i == DataSettingsActivity.this.enableAllStreamRow || i == DataSettingsActivity.this.enableMkvRow || i == DataSettingsActivity.this.quickRepliesRow || i == DataSettingsActivity.this.autoplayVideoRow || i == DataSettingsActivity.this.autoplayGifsRow) {
                return true;
            } else {
                return false;
            }
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return isRowEnabled(viewHolder.getAdapterPosition());
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            NotificationsCheckCell notificationsCheckCell;
            if (i == 0) {
                notificationsCheckCell = new ShadowSectionCell(this.mContext);
            } else if (i == 1) {
                TextSettingsCell textSettingsCell = new TextSettingsCell(this.mContext);
                textSettingsCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                notificationsCheckCell = textSettingsCell;
            } else if (i == 2) {
                HeaderCell headerCell = new HeaderCell(this.mContext);
                headerCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                notificationsCheckCell = headerCell;
            } else if (i == 3) {
                TextCheckCell textCheckCell = new TextCheckCell(this.mContext);
                textCheckCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                notificationsCheckCell = textCheckCell;
            } else if (i == 4) {
                TextInfoPrivacyCell textInfoPrivacyCell = new TextInfoPrivacyCell(this.mContext);
                textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                notificationsCheckCell = textInfoPrivacyCell;
            } else if (i != 5) {
                notificationsCheckCell = null;
            } else {
                NotificationsCheckCell notificationsCheckCell2 = new NotificationsCheckCell(this.mContext);
                notificationsCheckCell2.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                notificationsCheckCell = notificationsCheckCell2;
            }
            notificationsCheckCell.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(notificationsCheckCell);
        }

        public int getItemViewType(int i) {
            if (i == DataSettingsActivity.this.mediaDownloadSection2Row || i == DataSettingsActivity.this.usageSection2Row || i == DataSettingsActivity.this.callsSection2Row || i == DataSettingsActivity.this.proxySection2Row || i == DataSettingsActivity.this.autoplaySectionRow || i == DataSettingsActivity.this.clearDraftsSectionRow) {
                return 0;
            }
            if (i == DataSettingsActivity.this.mediaDownloadSectionRow || i == DataSettingsActivity.this.streamSectionRow || i == DataSettingsActivity.this.callsSectionRow || i == DataSettingsActivity.this.usageSectionRow || i == DataSettingsActivity.this.proxySectionRow || i == DataSettingsActivity.this.autoplayHeaderRow) {
                return 2;
            }
            if (i == DataSettingsActivity.this.enableCacheStreamRow || i == DataSettingsActivity.this.enableStreamRow || i == DataSettingsActivity.this.enableAllStreamRow || i == DataSettingsActivity.this.enableMkvRow || i == DataSettingsActivity.this.autoplayGifsRow || i == DataSettingsActivity.this.autoplayVideoRow) {
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
