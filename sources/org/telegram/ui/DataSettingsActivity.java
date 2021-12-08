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
import android.widget.LinearLayout;
import android.widget.TextView;
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
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.NotificationsCheckCell;
import org.telegram.ui.Cells.RadioColorCell;
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
    private LinearLayoutManager layoutManager;
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
        this.autoplayHeaderRow = i11;
        int i13 = i12 + 1;
        this.rowCount = i13;
        this.autoplayGifsRow = i12;
        int i14 = i13 + 1;
        this.rowCount = i14;
        this.autoplayVideoRow = i13;
        int i15 = i14 + 1;
        this.rowCount = i15;
        this.autoplaySectionRow = i14;
        int i16 = i15 + 1;
        this.rowCount = i16;
        this.streamSectionRow = i15;
        this.rowCount = i16 + 1;
        this.enableStreamRow = i16;
        if (BuildVars.DEBUG_VERSION) {
            int i17 = this.rowCount;
            int i18 = i17 + 1;
            this.rowCount = i18;
            this.enableMkvRow = i17;
            this.rowCount = i18 + 1;
            this.enableAllStreamRow = i18;
        } else {
            this.enableAllStreamRow = -1;
            this.enableMkvRow = -1;
        }
        int i19 = this.rowCount;
        int i20 = i19 + 1;
        this.rowCount = i20;
        this.enableAllStreamInfoRow = i19;
        this.enableCacheStreamRow = -1;
        int i21 = i20 + 1;
        this.rowCount = i21;
        this.callsSectionRow = i20;
        int i22 = i21 + 1;
        this.rowCount = i22;
        this.useLessDataForCallsRow = i21;
        int i23 = i22 + 1;
        this.rowCount = i23;
        this.quickRepliesRow = i22;
        int i24 = i23 + 1;
        this.rowCount = i24;
        this.callsSection2Row = i23;
        int i25 = i24 + 1;
        this.rowCount = i25;
        this.proxySectionRow = i24;
        int i26 = i25 + 1;
        this.rowCount = i26;
        this.proxyRow = i25;
        int i27 = i26 + 1;
        this.rowCount = i27;
        this.proxySection2Row = i26;
        int i28 = i27 + 1;
        this.rowCount = i28;
        this.clearDraftsRow = i27;
        this.rowCount = i28 + 1;
        this.clearDraftsSectionRow = i28;
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
            public void onItemClick(int id) {
                if (id == -1) {
                    DataSettingsActivity.this.finishFragment();
                }
            }
        });
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setVerticalScrollBarEnabled(false);
        RecyclerListView recyclerListView2 = this.listView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false);
        this.layoutManager = linearLayoutManager;
        recyclerListView2.setLayoutManager(linearLayoutManager);
        ((FrameLayout) this.fragmentView).addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListenerExtended) new DataSettingsActivity$$ExternalSyntheticLambda6(this, context));
        return this.fragmentView;
    }

    /* renamed from: lambda$createView$6$org-telegram-ui-DataSettingsActivity  reason: not valid java name */
    public /* synthetic */ void m2813lambda$createView$6$orgtelegramuiDataSettingsActivity(Context context, View view, int position, float x, float y) {
        int num;
        String key2;
        String key;
        DownloadController.Preset defaultPreset;
        DownloadController.Preset preset;
        int type;
        View view2 = view;
        int i = position;
        if (i == this.mobileRow || i == this.roamingRow) {
            Context context2 = context;
        } else if (i == this.wifiRow) {
            Context context3 = context;
        } else if (i == this.resetDownloadRow) {
            if (getParentActivity() != null && view.isEnabled()) {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                builder.setTitle(LocaleController.getString("ResetAutomaticMediaDownloadAlertTitle", NUM));
                builder.setMessage(LocaleController.getString("ResetAutomaticMediaDownloadAlert", NUM));
                builder.setPositiveButton(LocaleController.getString("Reset", NUM), new DataSettingsActivity$$ExternalSyntheticLambda0(this));
                builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                AlertDialog dialog = builder.create();
                showDialog(dialog);
                TextView button = (TextView) dialog.getButton(-1);
                if (button != null) {
                    button.setTextColor(Theme.getColor("dialogTextRed2"));
                }
                Context context4 = context;
                return;
            }
            return;
        } else if (i == this.storageUsageRow) {
            presentFragment(new CacheControlActivity());
            Context context5 = context;
            return;
        } else if (i == this.useLessDataForCallsRow) {
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
            Dialog dlg = AlertsCreator.createSingleChoiceDialog(getParentActivity(), new String[]{LocaleController.getString("UseLessDataNever", NUM), LocaleController.getString("UseLessDataOnRoaming", NUM), LocaleController.getString("UseLessDataOnMobile", NUM), LocaleController.getString("UseLessDataAlways", NUM)}, LocaleController.getString("VoipUseLessData", NUM), selected, new DataSettingsActivity$$ExternalSyntheticLambda2(this, preferences, i));
            setVisibleDialog(dlg);
            dlg.show();
            Context context6 = context;
            return;
        } else if (i == this.dataUsageRow) {
            presentFragment(new DataUsageActivity());
            Context context7 = context;
            return;
        } else if (i == this.storageNumRow) {
            AlertDialog.Builder builder2 = new AlertDialog.Builder((Context) getParentActivity());
            builder2.setTitle(LocaleController.getString("StoragePath", NUM));
            LinearLayout linearLayout = new LinearLayout(getParentActivity());
            linearLayout.setOrientation(1);
            builder2.setView(linearLayout);
            String dir = this.storageDirs.get(0).getAbsolutePath();
            if (!TextUtils.isEmpty(SharedConfig.storageCacheDir)) {
                int a = 0;
                int N = this.storageDirs.size();
                while (true) {
                    if (a >= N) {
                        break;
                    }
                    String path = this.storageDirs.get(a).getAbsolutePath();
                    if (path.startsWith(SharedConfig.storageCacheDir)) {
                        dir = path;
                        break;
                    }
                    a++;
                }
            }
            int N2 = this.storageDirs.size();
            for (int a2 = 0; a2 < N2; a2++) {
                String storageDir = this.storageDirs.get(a2).getAbsolutePath();
                RadioColorCell cell = new RadioColorCell(context);
                cell.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
                cell.setTag(Integer.valueOf(a2));
                cell.setCheckColor(Theme.getColor("radioBackground"), Theme.getColor("dialogRadioBackgroundChecked"));
                cell.setTextAndValue(storageDir, storageDir.startsWith(dir));
                linearLayout.addView(cell);
                cell.setOnClickListener(new DataSettingsActivity$$ExternalSyntheticLambda3(this, storageDir, builder2));
            }
            Context context8 = context;
            builder2.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
            showDialog(builder2.create());
            return;
        } else {
            Context context9 = context;
            if (i == this.proxyRow) {
                presentFragment(new ProxyListActivity());
                return;
            } else if (i == this.enableStreamRow) {
                SharedConfig.toggleStreamMedia();
                ((TextCheckCell) view2).setChecked(SharedConfig.streamMedia);
                return;
            } else if (i == this.enableAllStreamRow) {
                SharedConfig.toggleStreamAllVideo();
                ((TextCheckCell) view2).setChecked(SharedConfig.streamAllVideo);
                return;
            } else if (i == this.enableMkvRow) {
                SharedConfig.toggleStreamMkv();
                ((TextCheckCell) view2).setChecked(SharedConfig.streamMkv);
                return;
            } else if (i == this.enableCacheStreamRow) {
                SharedConfig.toggleSaveStreamMedia();
                ((TextCheckCell) view2).setChecked(SharedConfig.saveStreamMedia);
                return;
            } else if (i == this.quickRepliesRow) {
                presentFragment(new QuickRepliesSettingsActivity());
                return;
            } else if (i == this.autoplayGifsRow) {
                SharedConfig.toggleAutoplayGifs();
                if (view2 instanceof TextCheckCell) {
                    ((TextCheckCell) view2).setChecked(SharedConfig.autoplayGifs);
                    return;
                }
                return;
            } else if (i == this.autoplayVideoRow) {
                SharedConfig.toggleAutoplayVideo();
                if (view2 instanceof TextCheckCell) {
                    ((TextCheckCell) view2).setChecked(SharedConfig.autoplayVideo);
                    return;
                }
                return;
            } else if (i == this.clearDraftsRow) {
                AlertDialog.Builder builder3 = new AlertDialog.Builder((Context) getParentActivity());
                builder3.setTitle(LocaleController.getString("AreYouSureClearDraftsTitle", NUM));
                builder3.setMessage(LocaleController.getString("AreYouSureClearDrafts", NUM));
                builder3.setPositiveButton(LocaleController.getString("Delete", NUM), new DataSettingsActivity$$ExternalSyntheticLambda1(this));
                builder3.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                AlertDialog alertDialog = builder3.create();
                showDialog(alertDialog);
                TextView button2 = (TextView) alertDialog.getButton(-1);
                if (button2 != null) {
                    button2.setTextColor(Theme.getColor("dialogTextRed2"));
                    return;
                }
                return;
            } else {
                return;
            }
        }
        if ((!LocaleController.isRTL || x > ((float) AndroidUtilities.dp(76.0f))) && (LocaleController.isRTL || x < ((float) (view.getMeasuredWidth() - AndroidUtilities.dp(76.0f))))) {
            if (i == this.mobileRow) {
                type = 0;
            } else if (i == this.wifiRow) {
                type = 1;
            } else {
                type = 2;
            }
            presentFragment(new DataAutoDownloadActivity(type));
            return;
        }
        boolean wasEnabled = this.listAdapter.isRowEnabled(this.resetDownloadRow);
        NotificationsCheckCell cell2 = (NotificationsCheckCell) view2;
        boolean checked = cell2.isChecked();
        if (i == this.mobileRow) {
            preset = DownloadController.getInstance(this.currentAccount).mobilePreset;
            defaultPreset = DownloadController.getInstance(this.currentAccount).mediumPreset;
            key = "mobilePreset";
            key2 = "currentMobilePreset";
            num = 0;
        } else if (i == this.wifiRow) {
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
        if (checked || !preset.enabled) {
            preset.enabled = true ^ preset.enabled;
        } else {
            preset.set(defaultPreset);
        }
        SharedPreferences.Editor editor = MessagesController.getMainSettings(this.currentAccount).edit();
        editor.putString(key, preset.toString());
        editor.putInt(key2, 3);
        editor.commit();
        cell2.setChecked(!checked);
        RecyclerView.ViewHolder holder = this.listView.findContainingViewHolder(view2);
        if (holder != null) {
            this.listAdapter.onBindViewHolder(holder, i);
        }
        DownloadController.getInstance(this.currentAccount).checkAutodownloadSettings();
        DownloadController.getInstance(this.currentAccount).savePresetToServer(num);
        if (wasEnabled != this.listAdapter.isRowEnabled(this.resetDownloadRow)) {
            this.listAdapter.notifyItemChanged(this.resetDownloadRow);
        }
    }

    /* renamed from: lambda$createView$0$org-telegram-ui-DataSettingsActivity  reason: not valid java name */
    public /* synthetic */ void m2807lambda$createView$0$orgtelegramuiDataSettingsActivity(DialogInterface dialogInterface, int i) {
        String key;
        DownloadController.Preset defaultPreset;
        DownloadController.Preset preset;
        SharedPreferences.Editor editor = MessagesController.getMainSettings(this.currentAccount).edit();
        for (int a = 0; a < 3; a++) {
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
        for (int a2 = 0; a2 < 3; a2++) {
            DownloadController.getInstance(this.currentAccount).savePresetToServer(a2);
        }
        this.listAdapter.notifyItemRangeChanged(this.mobileRow, 4);
    }

    /* renamed from: lambda$createView$1$org-telegram-ui-DataSettingsActivity  reason: not valid java name */
    public /* synthetic */ void m2808lambda$createView$1$orgtelegramuiDataSettingsActivity(SharedPreferences preferences, int position, DialogInterface dialog, int which) {
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
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyItemChanged(position);
        }
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-DataSettingsActivity  reason: not valid java name */
    public /* synthetic */ void m2809lambda$createView$2$orgtelegramuiDataSettingsActivity(String storageDir, AlertDialog.Builder builder, View v) {
        SharedConfig.storageCacheDir = storageDir;
        SharedConfig.saveConfig();
        ImageLoader.getInstance().checkMediaPaths();
        builder.getDismissRunnable().run();
        this.listAdapter.notifyItemChanged(this.storageNumRow);
    }

    /* renamed from: lambda$createView$5$org-telegram-ui-DataSettingsActivity  reason: not valid java name */
    public /* synthetic */ void m2812lambda$createView$5$orgtelegramuiDataSettingsActivity(DialogInterface dialogInterface, int i) {
        getConnectionsManager().sendRequest(new TLRPC.TL_messages_clearAllDrafts(), new DataSettingsActivity$$ExternalSyntheticLambda5(this));
    }

    /* renamed from: lambda$createView$3$org-telegram-ui-DataSettingsActivity  reason: not valid java name */
    public /* synthetic */ void m2810lambda$createView$3$orgtelegramuiDataSettingsActivity() {
        getMediaDataController().clearAllDrafts(true);
    }

    /* renamed from: lambda$createView$4$org-telegram-ui-DataSettingsActivity  reason: not valid java name */
    public /* synthetic */ void m2811lambda$createView$4$orgtelegramuiDataSettingsActivity(TLObject response, TLRPC.TL_error error) {
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

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            boolean enabled;
            String text;
            DownloadController.Preset preset;
            RecyclerView.ViewHolder viewHolder = holder;
            int i = position;
            boolean z = false;
            switch (holder.getItemViewType()) {
                case 0:
                    if (i == DataSettingsActivity.this.clearDraftsSectionRow) {
                        viewHolder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                        return;
                    } else {
                        viewHolder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                        return;
                    }
                case 1:
                    TextSettingsCell textCell = (TextSettingsCell) viewHolder.itemView;
                    textCell.setCanDisable(false);
                    textCell.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
                    if (i == DataSettingsActivity.this.storageUsageRow) {
                        textCell.setText(LocaleController.getString("StorageUsage", NUM), true);
                        return;
                    } else if (i == DataSettingsActivity.this.useLessDataForCallsRow) {
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
                    } else if (i == DataSettingsActivity.this.dataUsageRow) {
                        String string = LocaleController.getString("NetworkUsage", NUM);
                        if (DataSettingsActivity.this.storageNumRow != -1) {
                            z = true;
                        }
                        textCell.setText(string, z);
                        return;
                    } else if (i == DataSettingsActivity.this.storageNumRow) {
                        String dir = ((File) DataSettingsActivity.this.storageDirs.get(0)).getAbsolutePath();
                        if (!TextUtils.isEmpty(SharedConfig.storageCacheDir)) {
                            int a = 0;
                            int N = DataSettingsActivity.this.storageDirs.size();
                            while (true) {
                                if (a < N) {
                                    String path = ((File) DataSettingsActivity.this.storageDirs.get(a)).getAbsolutePath();
                                    if (path.startsWith(SharedConfig.storageCacheDir)) {
                                        dir = path;
                                    } else {
                                        a++;
                                    }
                                }
                            }
                        }
                        textCell.setTextAndValue(LocaleController.getString("StoragePath", NUM), dir, false);
                        return;
                    } else if (i == DataSettingsActivity.this.proxyRow) {
                        textCell.setText(LocaleController.getString("ProxySettings", NUM), false);
                        return;
                    } else if (i == DataSettingsActivity.this.resetDownloadRow) {
                        textCell.setCanDisable(true);
                        textCell.setTextColor(Theme.getColor("windowBackgroundWhiteRedText"));
                        textCell.setText(LocaleController.getString("ResetAutomaticMediaDownload", NUM), false);
                        return;
                    } else if (i == DataSettingsActivity.this.quickRepliesRow) {
                        textCell.setText(LocaleController.getString("VoipQuickReplies", NUM), false);
                        return;
                    } else if (i == DataSettingsActivity.this.clearDraftsRow) {
                        textCell.setText(LocaleController.getString("PrivacyDeleteCloudDrafts", NUM), false);
                        return;
                    } else {
                        return;
                    }
                case 2:
                    HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
                    if (i == DataSettingsActivity.this.mediaDownloadSectionRow) {
                        headerCell.setText(LocaleController.getString("AutomaticMediaDownload", NUM));
                        return;
                    } else if (i == DataSettingsActivity.this.usageSectionRow) {
                        headerCell.setText(LocaleController.getString("DataUsage", NUM));
                        return;
                    } else if (i == DataSettingsActivity.this.callsSectionRow) {
                        headerCell.setText(LocaleController.getString("Calls", NUM));
                        return;
                    } else if (i == DataSettingsActivity.this.proxySectionRow) {
                        headerCell.setText(LocaleController.getString("Proxy", NUM));
                        return;
                    } else if (i == DataSettingsActivity.this.streamSectionRow) {
                        headerCell.setText(LocaleController.getString("Streaming", NUM));
                        return;
                    } else if (i == DataSettingsActivity.this.autoplayHeaderRow) {
                        headerCell.setText(LocaleController.getString("AutoplayMedia", NUM));
                        return;
                    } else {
                        return;
                    }
                case 3:
                    TextCheckCell checkCell = (TextCheckCell) viewHolder.itemView;
                    if (i == DataSettingsActivity.this.enableStreamRow) {
                        String string2 = LocaleController.getString("EnableStreaming", NUM);
                        boolean z2 = SharedConfig.streamMedia;
                        if (DataSettingsActivity.this.enableAllStreamRow != -1) {
                            z = true;
                        }
                        checkCell.setTextAndCheck(string2, z2, z);
                        return;
                    } else if (i != DataSettingsActivity.this.enableCacheStreamRow) {
                        if (i == DataSettingsActivity.this.enableMkvRow) {
                            checkCell.setTextAndCheck("(beta only) Show MKV as Video", SharedConfig.streamMkv, true);
                            return;
                        } else if (i == DataSettingsActivity.this.enableAllStreamRow) {
                            checkCell.setTextAndCheck("(beta only) Stream All Videos", SharedConfig.streamAllVideo, false);
                            return;
                        } else if (i == DataSettingsActivity.this.autoplayGifsRow) {
                            checkCell.setTextAndCheck(LocaleController.getString("AutoplayGIF", NUM), SharedConfig.autoplayGifs, true);
                            return;
                        } else if (i == DataSettingsActivity.this.autoplayVideoRow) {
                            checkCell.setTextAndCheck(LocaleController.getString("AutoplayVideo", NUM), SharedConfig.autoplayVideo, false);
                            return;
                        } else {
                            return;
                        }
                    } else {
                        return;
                    }
                case 4:
                    TextInfoPrivacyCell cell = (TextInfoPrivacyCell) viewHolder.itemView;
                    if (i == DataSettingsActivity.this.enableAllStreamInfoRow) {
                        cell.setText(LocaleController.getString("EnableAllStreamingInfo", NUM));
                        return;
                    }
                    return;
                case 5:
                    NotificationsCheckCell checkCell2 = (NotificationsCheckCell) viewHolder.itemView;
                    StringBuilder builder = new StringBuilder();
                    if (i == DataSettingsActivity.this.mobileRow) {
                        text = LocaleController.getString("WhenUsingMobileData", NUM);
                        enabled = DownloadController.getInstance(DataSettingsActivity.this.currentAccount).mobilePreset.enabled;
                        preset = DownloadController.getInstance(DataSettingsActivity.this.currentAccount).getCurrentMobilePreset();
                    } else if (i == DataSettingsActivity.this.wifiRow) {
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
                    for (int a2 = 0; a2 < preset.mask.length; a2++) {
                        if (!photos && (preset.mask[a2] & 1) != 0) {
                            count++;
                            photos = true;
                        }
                        if (!videos && (preset.mask[a2] & 4) != 0) {
                            count++;
                            videos = true;
                        }
                        if (!files && (preset.mask[a2] & 8) != 0) {
                            count++;
                            files = true;
                        }
                    }
                    if (!preset.enabled || count == 0) {
                        builder.append(LocaleController.getString("NoMediaAutoDownload", NUM));
                    } else {
                        if (photos) {
                            builder.append(LocaleController.getString("AutoDownloadPhotosOn", NUM));
                        }
                        if (videos) {
                            if (builder.length() > 0) {
                                builder.append(", ");
                            }
                            builder.append(LocaleController.getString("AutoDownloadVideosOn", NUM));
                            builder.append(String.format(" (%1$s)", new Object[]{AndroidUtilities.formatFileSize((long) preset.sizes[DownloadController.typeToIndex(4)], true)}));
                        }
                        if (files) {
                            if (builder.length() > 0) {
                                builder.append(", ");
                            }
                            builder.append(LocaleController.getString("AutoDownloadFilesOn", NUM));
                            builder.append(String.format(" (%1$s)", new Object[]{AndroidUtilities.formatFileSize((long) preset.sizes[DownloadController.typeToIndex(8)], true)}));
                        }
                    }
                    DownloadController.Preset preset2 = preset;
                    checkCell2.setTextAndValueAndCheck(text, builder, (photos || videos || files) && enabled, 0, true, true);
                    return;
                default:
                    return;
            }
        }

        public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
            if (holder.getItemViewType() == 3) {
                TextCheckCell checkCell = (TextCheckCell) holder.itemView;
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
                if (!controller.lowPreset.equals(controller.getCurrentRoamingPreset()) || controller.lowPreset.isEnabled() != controller.roamingPreset.enabled || !controller.mediumPreset.equals(controller.getCurrentMobilePreset()) || controller.mediumPreset.isEnabled() != controller.mobilePreset.enabled || !controller.highPreset.equals(controller.getCurrentWiFiPreset()) || controller.highPreset.isEnabled() != controller.wifiPreset.enabled) {
                    return true;
                }
                return false;
            } else if (position == DataSettingsActivity.this.mobileRow || position == DataSettingsActivity.this.roamingRow || position == DataSettingsActivity.this.wifiRow || position == DataSettingsActivity.this.storageUsageRow || position == DataSettingsActivity.this.useLessDataForCallsRow || position == DataSettingsActivity.this.dataUsageRow || position == DataSettingsActivity.this.proxyRow || position == DataSettingsActivity.this.clearDraftsRow || position == DataSettingsActivity.this.enableCacheStreamRow || position == DataSettingsActivity.this.enableStreamRow || position == DataSettingsActivity.this.enableAllStreamRow || position == DataSettingsActivity.this.enableMkvRow || position == DataSettingsActivity.this.quickRepliesRow || position == DataSettingsActivity.this.autoplayVideoRow || position == DataSettingsActivity.this.autoplayGifsRow || position == DataSettingsActivity.this.storageNumRow) {
                return true;
            } else {
                return false;
            }
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return isRowEnabled(holder.getAdapterPosition());
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new ShadowSectionCell(this.mContext);
                    break;
                case 1:
                    View view2 = new TextSettingsCell(this.mContext);
                    view2.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    view = view2;
                    break;
                case 2:
                    View view3 = new HeaderCell(this.mContext);
                    view3.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    view = view3;
                    break;
                case 3:
                    View view4 = new TextCheckCell(this.mContext);
                    view4.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    view = view4;
                    break;
                case 4:
                    view = new TextInfoPrivacyCell(this.mContext);
                    view.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                    break;
                default:
                    View view5 = new NotificationsCheckCell(this.mContext);
                    view5.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    view = view5;
                    break;
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        public int getItemViewType(int position) {
            if (position == DataSettingsActivity.this.mediaDownloadSection2Row || position == DataSettingsActivity.this.usageSection2Row || position == DataSettingsActivity.this.callsSection2Row || position == DataSettingsActivity.this.proxySection2Row || position == DataSettingsActivity.this.autoplaySectionRow || position == DataSettingsActivity.this.clearDraftsSectionRow) {
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

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, TextCheckCell.class, HeaderCell.class, NotificationsCheckCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrack"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackChecked"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteValueText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrack"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackChecked"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        return themeDescriptions;
    }
}
