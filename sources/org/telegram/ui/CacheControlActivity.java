package org.telegram.ui;

import android.animation.TimeInterpolator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.StatFs;
import android.text.TextUtils;
import android.transition.ChangeBounds;
import android.transition.Fade;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.FilesMigrationService;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.TextCheckBoxCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SlideChooseView;
import org.telegram.ui.Components.StorageDiagramView;
import org.telegram.ui.Components.StroageUsageView;
import org.telegram.ui.Components.UndoView;
/* loaded from: classes3.dex */
public class CacheControlActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private View actionTextView;
    private BottomSheet bottomSheet;
    private View bottomSheetView;
    private int cacheInfoRow;
    private UndoView cacheRemovedTooltip;
    private int databaseInfoRow;
    private int databaseRow;
    private int deviseStorageHeaderRow;
    long fragmentCreateTime;
    private int keepMediaChooserRow;
    private int keepMediaHeaderRow;
    private int keepMediaInfoRow;
    private LinearLayoutManager layoutManager;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    AlertDialog progressDialog;
    private int rowCount;
    private int storageUsageRow;
    private long databaseSize = -1;
    private long cacheSize = -1;
    private long documentsSize = -1;
    private long audioSize = -1;
    private long musicSize = -1;
    private long photoSize = -1;
    private long videoSize = -1;
    private long stickersSize = -1;
    private long totalSize = -1;
    private long totalDeviceSize = -1;
    private long totalDeviceFreeSize = -1;
    private long migrateOldFolderRow = -1;
    private StorageDiagramView.ClearViewData[] clearViewData = new StorageDiagramView.ClearViewData[7];
    private boolean calculating = true;
    private volatile boolean canceled = false;

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        getNotificationCenter().addObserver(this, NotificationCenter.didClearDatabase);
        this.databaseSize = MessagesStorage.getInstance(this.currentAccount).getDatabaseSize();
        Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.CacheControlActivity$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                CacheControlActivity.this.lambda$onFragmentCreate$1();
            }
        });
        this.fragmentCreateTime = System.currentTimeMillis();
        updateRows();
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onFragmentCreate$1() {
        File file;
        long blockSize;
        long availableBlocks;
        long blockCount;
        this.cacheSize = getDirectorySize(FileLoader.checkDirectory(4), 0);
        if (this.canceled) {
            return;
        }
        long directorySize = getDirectorySize(FileLoader.checkDirectory(0), 0);
        this.photoSize = directorySize;
        this.photoSize = directorySize + getDirectorySize(FileLoader.checkDirectory(100), 0);
        if (this.canceled) {
            return;
        }
        long directorySize2 = getDirectorySize(FileLoader.checkDirectory(2), 0);
        this.videoSize = directorySize2;
        this.videoSize = directorySize2 + getDirectorySize(FileLoader.checkDirectory(101), 0);
        if (this.canceled) {
            return;
        }
        long directorySize3 = getDirectorySize(FileLoader.checkDirectory(3), 1);
        this.documentsSize = directorySize3;
        this.documentsSize = directorySize3 + getDirectorySize(FileLoader.checkDirectory(5), 1);
        if (this.canceled) {
            return;
        }
        long directorySize4 = getDirectorySize(FileLoader.checkDirectory(3), 2);
        this.musicSize = directorySize4;
        this.musicSize = directorySize4 + getDirectorySize(FileLoader.checkDirectory(5), 2);
        if (this.canceled) {
            return;
        }
        this.stickersSize = getDirectorySize(new File(FileLoader.checkDirectory(4), "acache"), 0);
        if (this.canceled) {
            return;
        }
        long directorySize5 = getDirectorySize(FileLoader.checkDirectory(1), 0);
        this.audioSize = directorySize5;
        this.totalSize = this.cacheSize + this.videoSize + directorySize5 + this.photoSize + this.documentsSize + this.musicSize + this.stickersSize;
        if (Build.VERSION.SDK_INT >= 19) {
            ArrayList<File> rootDirs = AndroidUtilities.getRootDirs();
            file = rootDirs.get(0);
            file.getAbsolutePath();
            if (!TextUtils.isEmpty(SharedConfig.storageCacheDir)) {
                int size = rootDirs.size();
                for (int i = 0; i < size; i++) {
                    File file2 = rootDirs.get(i);
                    if (file2.getAbsolutePath().startsWith(SharedConfig.storageCacheDir)) {
                        file = file2;
                        break;
                    }
                }
            }
        } else {
            file = new File(SharedConfig.storageCacheDir);
        }
        try {
            StatFs statFs = new StatFs(file.getPath());
            int i2 = Build.VERSION.SDK_INT;
            if (i2 >= 18) {
                blockSize = statFs.getBlockSizeLong();
            } else {
                blockSize = statFs.getBlockSize();
            }
            if (i2 >= 18) {
                availableBlocks = statFs.getAvailableBlocksLong();
            } else {
                availableBlocks = statFs.getAvailableBlocks();
            }
            if (i2 >= 18) {
                blockCount = statFs.getBlockCountLong();
            } else {
                blockCount = statFs.getBlockCount();
            }
            this.totalDeviceSize = blockCount * blockSize;
            this.totalDeviceFreeSize = availableBlocks * blockSize;
        } catch (Exception e) {
            FileLog.e(e);
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.CacheControlActivity$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                CacheControlActivity.this.lambda$onFragmentCreate$0();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onFragmentCreate$0() {
        this.calculating = false;
        updateStorageUsageRow();
    }

    private void updateRows() {
        this.rowCount = 0;
        int i = 0 + 1;
        this.rowCount = i;
        this.keepMediaHeaderRow = 0;
        int i2 = i + 1;
        this.rowCount = i2;
        this.keepMediaChooserRow = i;
        int i3 = i2 + 1;
        this.rowCount = i3;
        this.keepMediaInfoRow = i2;
        int i4 = i3 + 1;
        this.rowCount = i4;
        this.deviseStorageHeaderRow = i3;
        int i5 = i4 + 1;
        this.rowCount = i5;
        this.storageUsageRow = i4;
        int i6 = i5 + 1;
        this.rowCount = i6;
        this.cacheInfoRow = i5;
        int i7 = i6 + 1;
        this.rowCount = i7;
        this.databaseRow = i6;
        this.rowCount = i7 + 1;
        this.databaseInfoRow = i7;
    }

    private void updateStorageUsageRow() {
        View findViewByPosition = this.layoutManager.findViewByPosition(this.storageUsageRow);
        if (findViewByPosition instanceof StroageUsageView) {
            StroageUsageView stroageUsageView = (StroageUsageView) findViewByPosition;
            long currentTimeMillis = System.currentTimeMillis();
            if (Build.VERSION.SDK_INT >= 19 && currentTimeMillis - this.fragmentCreateTime > 250) {
                TransitionSet transitionSet = new TransitionSet();
                ChangeBounds changeBounds = new ChangeBounds();
                changeBounds.setDuration(250L);
                changeBounds.excludeTarget((View) stroageUsageView.legendLayout, true);
                Fade fade = new Fade(1);
                fade.setDuration(290L);
                transitionSet.addTransition(new Fade(2).setDuration(250L)).addTransition(changeBounds).addTransition(fade);
                transitionSet.setOrdering(0);
                transitionSet.setInterpolator((TimeInterpolator) CubicBezierInterpolator.EASE_OUT);
                TransitionManager.beginDelayedTransition(this.listView, transitionSet);
            }
            stroageUsageView.setStorageUsage(this.calculating, this.databaseSize, this.totalSize, this.totalDeviceFreeSize, this.totalDeviceSize);
            RecyclerView.ViewHolder findViewHolderForAdapterPosition = this.listView.findViewHolderForAdapterPosition(this.storageUsageRow);
            if (findViewHolderForAdapterPosition == null) {
                return;
            }
            stroageUsageView.setEnabled(this.listAdapter.isEnabled(findViewHolderForAdapterPosition));
            return;
        }
        this.listAdapter.notifyDataSetChanged();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        getNotificationCenter().removeObserver(this, NotificationCenter.didClearDatabase);
        try {
            AlertDialog alertDialog = this.progressDialog;
            if (alertDialog != null) {
                alertDialog.dismiss();
            }
        } catch (Exception unused) {
        }
        this.progressDialog = null;
        this.canceled = true;
    }

    private long getDirectorySize(File file, int i) {
        if (file == null || this.canceled) {
            return 0L;
        }
        if (file.isDirectory()) {
            return Utilities.getDirSize(file.getAbsolutePath(), i, false);
        }
        if (!file.isFile()) {
            return 0L;
        }
        return 0 + file.length();
    }

    private void cleanupFolders() {
        final AlertDialog alertDialog = new AlertDialog(getParentActivity(), 3);
        alertDialog.setCanCancel(false);
        alertDialog.showDelayed(500L);
        Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.CacheControlActivity$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                CacheControlActivity.this.lambda$cleanupFolders$3(alertDialog);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:30:0x005d  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$cleanupFolders$3(final org.telegram.ui.ActionBar.AlertDialog r17) {
        /*
            Method dump skipped, instructions count: 448
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.CacheControlActivity.lambda$cleanupFolders$3(org.telegram.ui.ActionBar.AlertDialog):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$cleanupFolders$2(boolean z, AlertDialog alertDialog, long j) {
        if (z) {
            ImageLoader.getInstance().clearMemory();
        }
        if (this.listAdapter != null) {
            updateStorageUsageRow();
        }
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e(e);
        }
        getMediaDataController().ringtoneDataStore.checkRingtoneSoundsLoaded();
        this.cacheRemovedTooltip.setInfoText(LocaleController.formatString("CacheWasCleared", R.string.CacheWasCleared, AndroidUtilities.formatFileSize(j)));
        this.cacheRemovedTooltip.showWithAction(0L, 19, null, null);
        MediaDataController.getInstance(this.currentAccount).chekAllMedia(true);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(final Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("StorageUsage", R.string.StorageUsage));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.CacheControlActivity.1
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int i) {
                if (i == -1) {
                    CacheControlActivity.this.finishFragment();
                }
            }
        });
        this.listAdapter = new ListAdapter(context);
        FrameLayout frameLayout = new FrameLayout(context);
        this.fragmentView = frameLayout;
        FrameLayout frameLayout2 = frameLayout;
        frameLayout2.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setVerticalScrollBarEnabled(false);
        RecyclerListView recyclerListView2 = this.listView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false);
        this.layoutManager = linearLayoutManager;
        recyclerListView2.setLayoutManager(linearLayoutManager);
        frameLayout2.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.CacheControlActivity$$ExternalSyntheticLambda8
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view, int i) {
                CacheControlActivity.this.lambda$createView$6(context, view, i);
            }
        });
        UndoView undoView = new UndoView(context);
        this.cacheRemovedTooltip = undoView;
        frameLayout2.addView(undoView, LayoutHelper.createFrame(-1, -2.0f, 83, 8.0f, 0.0f, 8.0f, 8.0f));
        return this.fragmentView;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$6(Context context, View view, int i) {
        long j;
        String string;
        String str;
        if (getParentActivity() == null) {
            return;
        }
        if (i == this.migrateOldFolderRow) {
            if (Build.VERSION.SDK_INT < 30) {
                return;
            }
            migrateOldFolder();
        } else if (i == this.databaseRow) {
            clearDatabase();
        } else if (i != this.storageUsageRow) {
        } else {
            long j2 = 0;
            if (this.totalSize <= 0 || getParentActivity() == null) {
                return;
            }
            BottomSheet bottomSheet = new BottomSheet(this, getParentActivity(), false) { // from class: org.telegram.ui.CacheControlActivity.2
                @Override // org.telegram.ui.ActionBar.BottomSheet
                protected boolean canDismissWithSwipe() {
                    return false;
                }
            };
            this.bottomSheet = bottomSheet;
            bottomSheet.fixNavigationBar();
            this.bottomSheet.setAllowNestedScroll(true);
            this.bottomSheet.setApplyBottomPadding(false);
            LinearLayout linearLayout = new LinearLayout(getParentActivity());
            this.bottomSheetView = linearLayout;
            linearLayout.setOrientation(1);
            StorageDiagramView storageDiagramView = new StorageDiagramView(context);
            linearLayout.addView(storageDiagramView, LayoutHelper.createLinear(-2, -2, 1, 0, 16, 0, 16));
            CheckBoxCell checkBoxCell = null;
            int i2 = 0;
            while (i2 < 7) {
                if (i2 == 0) {
                    j = this.photoSize;
                    string = LocaleController.getString("LocalPhotoCache", R.string.LocalPhotoCache);
                    str = "statisticChartLine_blue";
                } else if (i2 == 1) {
                    j = this.videoSize;
                    string = LocaleController.getString("LocalVideoCache", R.string.LocalVideoCache);
                    str = "statisticChartLine_golden";
                } else if (i2 == 2) {
                    j = this.documentsSize;
                    string = LocaleController.getString("LocalDocumentCache", R.string.LocalDocumentCache);
                    str = "statisticChartLine_green";
                } else if (i2 == 3) {
                    j = this.musicSize;
                    string = LocaleController.getString("LocalMusicCache", R.string.LocalMusicCache);
                    str = "statisticChartLine_indigo";
                } else if (i2 == 4) {
                    j = this.audioSize;
                    string = LocaleController.getString("LocalAudioCache", R.string.LocalAudioCache);
                    str = "statisticChartLine_red";
                } else if (i2 == 5) {
                    j = this.stickersSize;
                    string = LocaleController.getString("AnimatedStickers", R.string.AnimatedStickers);
                    str = "statisticChartLine_lightgreen";
                } else {
                    j = this.cacheSize;
                    string = LocaleController.getString("LocalCache", R.string.LocalCache);
                    str = "statisticChartLine_lightblue";
                }
                if (j > j2) {
                    this.clearViewData[i2] = new StorageDiagramView.ClearViewData(storageDiagramView);
                    StorageDiagramView.ClearViewData[] clearViewDataArr = this.clearViewData;
                    clearViewDataArr[i2].size = j;
                    clearViewDataArr[i2].color = str;
                    checkBoxCell = new CheckBoxCell(getParentActivity(), 4, 21, null);
                    checkBoxCell.setTag(Integer.valueOf(i2));
                    checkBoxCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                    linearLayout.addView(checkBoxCell, LayoutHelper.createLinear(-1, 50));
                    checkBoxCell.setText(string, AndroidUtilities.formatFileSize(j), true, true);
                    checkBoxCell.setTextColor(Theme.getColor("dialogTextBlack"));
                    checkBoxCell.setCheckBoxColor(str, "windowBackgroundWhiteGrayIcon", "checkboxCheck");
                    checkBoxCell.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.CacheControlActivity$$ExternalSyntheticLambda1
                        @Override // android.view.View.OnClickListener
                        public final void onClick(View view2) {
                            CacheControlActivity.this.lambda$createView$4(view2);
                        }
                    });
                } else {
                    this.clearViewData[i2] = null;
                }
                i2++;
                j2 = 0;
            }
            if (checkBoxCell != null) {
                checkBoxCell.setNeedDivider(false);
            }
            storageDiagramView.setData(this.clearViewData);
            BottomSheet.BottomSheetCell bottomSheetCell = new BottomSheet.BottomSheetCell(getParentActivity(), 2);
            bottomSheetCell.setTextAndIcon(LocaleController.getString("ClearMediaCache", R.string.ClearMediaCache), 0);
            this.actionTextView = bottomSheetCell.getTextView();
            bottomSheetCell.getTextView().setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.CacheControlActivity$$ExternalSyntheticLambda2
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    CacheControlActivity.this.lambda$createView$5(view2);
                }
            });
            linearLayout.addView(bottomSheetCell, LayoutHelper.createLinear(-1, 50));
            NestedScrollView nestedScrollView = new NestedScrollView(context);
            nestedScrollView.setVerticalScrollBarEnabled(false);
            nestedScrollView.addView(linearLayout);
            this.bottomSheet.setCustomView(nestedScrollView);
            showDialog(this.bottomSheet);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$4(View view) {
        int i = 0;
        int i2 = 0;
        while (true) {
            StorageDiagramView.ClearViewData[] clearViewDataArr = this.clearViewData;
            if (i >= clearViewDataArr.length) {
                break;
            }
            if (clearViewDataArr[i] != null && clearViewDataArr[i].clear) {
                i2++;
            }
            i++;
        }
        CheckBoxCell checkBoxCell = (CheckBoxCell) view;
        int intValue = ((Integer) checkBoxCell.getTag()).intValue();
        if (i2 == 1 && this.clearViewData[intValue].clear) {
            AndroidUtilities.shakeView(checkBoxCell.getCheckBoxView());
            return;
        }
        StorageDiagramView.ClearViewData[] clearViewDataArr2 = this.clearViewData;
        clearViewDataArr2[intValue].setClear(!clearViewDataArr2[intValue].clear);
        checkBoxCell.setChecked(this.clearViewData[intValue].clear, true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$5(View view) {
        try {
            Dialog dialog = this.visibleDialog;
            if (dialog != null) {
                dialog.dismiss();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        cleanupFolders();
    }

    private void migrateOldFolder() {
        FilesMigrationService.checkBottomSheet(this);
    }

    private void clearDatabase() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("LocalDatabaseClearTextTitle", R.string.LocalDatabaseClearTextTitle));
        builder.setMessage(LocaleController.getString("LocalDatabaseClearText", R.string.LocalDatabaseClearText));
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        builder.setPositiveButton(LocaleController.getString("CacheClear", R.string.CacheClear), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.CacheControlActivity$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                CacheControlActivity.this.lambda$clearDatabase$7(dialogInterface, i);
            }
        });
        AlertDialog create = builder.create();
        showDialog(create);
        TextView textView = (TextView) create.getButton(-1);
        if (textView != null) {
            textView.setTextColor(Theme.getColor("dialogTextRed2"));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$clearDatabase$7(DialogInterface dialogInterface, int i) {
        if (getParentActivity() == null) {
            return;
        }
        AlertDialog alertDialog = new AlertDialog(getParentActivity(), 3);
        this.progressDialog = alertDialog;
        alertDialog.setCanCancel(false);
        this.progressDialog.showDelayed(500L);
        MessagesController.getInstance(this.currentAccount).clearQueryTime();
        getMessagesStorage().clearLocalDatabase();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.didClearDatabase) {
            try {
                AlertDialog alertDialog = this.progressDialog;
                if (alertDialog != null) {
                    alertDialog.dismiss();
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
            this.progressDialog = null;
            if (this.listAdapter == null) {
                return;
            }
            this.databaseSize = MessagesStorage.getInstance(this.currentAccount).getDatabaseSize();
            this.listAdapter.notifyDataSetChanged();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            int adapterPosition = viewHolder.getAdapterPosition();
            return ((long) adapterPosition) == CacheControlActivity.this.migrateOldFolderRow || adapterPosition == CacheControlActivity.this.databaseRow || (adapterPosition == CacheControlActivity.this.storageUsageRow && CacheControlActivity.this.totalSize > 0 && !CacheControlActivity.this.calculating);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return CacheControlActivity.this.rowCount;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ void lambda$onCreateViewHolder$0(int i) {
            if (i == 0) {
                SharedConfig.setKeepMedia(3);
            } else if (i == 1) {
                SharedConfig.setKeepMedia(0);
            } else if (i == 2) {
                SharedConfig.setKeepMedia(1);
            } else if (i != 3) {
            } else {
                SharedConfig.setKeepMedia(2);
            }
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        /* renamed from: onCreateViewHolder */
        public RecyclerView.ViewHolder mo1788onCreateViewHolder(ViewGroup viewGroup, int i) {
            FrameLayout frameLayout;
            FrameLayout frameLayout2;
            if (i == 0) {
                FrameLayout textSettingsCell = new TextSettingsCell(this.mContext);
                textSettingsCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                frameLayout = textSettingsCell;
            } else if (i == 2) {
                FrameLayout stroageUsageView = new StroageUsageView(this.mContext);
                stroageUsageView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                frameLayout = stroageUsageView;
            } else if (i == 3) {
                FrameLayout headerCell = new HeaderCell(this.mContext);
                headerCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                frameLayout = headerCell;
            } else if (i == 4) {
                SlideChooseView slideChooseView = new SlideChooseView(this.mContext);
                slideChooseView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                MessagesController.getGlobalMainSettings();
                slideChooseView.setCallback(CacheControlActivity$ListAdapter$$ExternalSyntheticLambda0.INSTANCE);
                int i2 = SharedConfig.keepMedia;
                slideChooseView.setOptions(i2 == 3 ? 0 : i2 + 1, LocaleController.formatPluralString("Days", 3, new Object[0]), LocaleController.formatPluralString("Weeks", 1, new Object[0]), LocaleController.formatPluralString("Months", 1, new Object[0]), LocaleController.getString("KeepMediaForever", R.string.KeepMediaForever));
                frameLayout = slideChooseView;
            } else {
                frameLayout2 = new TextInfoPrivacyCell(this.mContext);
                return new RecyclerListView.Holder(frameLayout2);
            }
            frameLayout2 = frameLayout;
            return new RecyclerListView.Holder(frameLayout2);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType == 0) {
                TextSettingsCell textSettingsCell = (TextSettingsCell) viewHolder.itemView;
                if (i == CacheControlActivity.this.databaseRow) {
                    textSettingsCell.setTextAndValue(LocaleController.getString("ClearLocalDatabase", R.string.ClearLocalDatabase), AndroidUtilities.formatFileSize(CacheControlActivity.this.databaseSize), false);
                } else if (i != CacheControlActivity.this.migrateOldFolderRow) {
                } else {
                    textSettingsCell.setTextAndValue(LocaleController.getString("MigrateOldFolder", R.string.MigrateOldFolder), null, false);
                }
            } else if (itemViewType != 1) {
                if (itemViewType == 2) {
                    ((StroageUsageView) viewHolder.itemView).setStorageUsage(CacheControlActivity.this.calculating, CacheControlActivity.this.databaseSize, CacheControlActivity.this.totalSize, CacheControlActivity.this.totalDeviceFreeSize, CacheControlActivity.this.totalDeviceSize);
                } else if (itemViewType != 3) {
                } else {
                    HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
                    if (i != CacheControlActivity.this.keepMediaHeaderRow) {
                        if (i != CacheControlActivity.this.deviseStorageHeaderRow) {
                            return;
                        }
                        headerCell.setText(LocaleController.getString("DeviceStorage", R.string.DeviceStorage));
                        return;
                    }
                    headerCell.setText(LocaleController.getString("KeepMedia", R.string.KeepMedia));
                }
            } else {
                TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
                if (i != CacheControlActivity.this.databaseInfoRow) {
                    if (i != CacheControlActivity.this.cacheInfoRow) {
                        if (i != CacheControlActivity.this.keepMediaInfoRow) {
                            return;
                        }
                        textInfoPrivacyCell.setText(AndroidUtilities.replaceTags(LocaleController.getString("KeepMediaInfo", R.string.KeepMediaInfo)));
                        textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, R.drawable.greydivider, "windowBackgroundGrayShadow"));
                        return;
                    }
                    textInfoPrivacyCell.setText("");
                    textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, R.drawable.greydivider, "windowBackgroundGrayShadow"));
                    return;
                }
                textInfoPrivacyCell.setText(LocaleController.getString("LocalDatabaseInfo", R.string.LocalDatabaseInfo));
                textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, R.drawable.greydivider_bottom, "windowBackgroundGrayShadow"));
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int i) {
            if (i == CacheControlActivity.this.databaseInfoRow || i == CacheControlActivity.this.cacheInfoRow || i == CacheControlActivity.this.keepMediaInfoRow) {
                return 1;
            }
            if (i == CacheControlActivity.this.storageUsageRow) {
                return 2;
            }
            if (i == CacheControlActivity.this.keepMediaHeaderRow || i == CacheControlActivity.this.deviseStorageHeaderRow) {
                return 3;
            }
            return i == CacheControlActivity.this.keepMediaChooserRow ? 4 : 0;
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = new ThemeDescription.ThemeDescriptionDelegate() { // from class: org.telegram.ui.CacheControlActivity$$ExternalSyntheticLambda7
            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public final void didSetColor() {
                CacheControlActivity.this.lambda$getThemeDescriptions$8();
            }

            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public /* synthetic */ void onAnimationProgress(float f) {
                ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
            }
        };
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, SlideChooseView.class, StroageUsageView.class, HeaderCell.class}, null, null, null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteValueText"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{StroageUsageView.class}, new String[]{"paintFill"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_progressBackground"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{StroageUsageView.class}, new String[]{"paintProgress"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_progress"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{StroageUsageView.class}, new String[]{"telegramCacheTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{StroageUsageView.class}, new String[]{"freeSizeTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{StroageUsageView.class}, new String[]{"calculationgTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{StroageUsageView.class}, new String[]{"paintProgress2"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_progressBackground2"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{SlideChooseView.class}, null, null, null, "switchTrack"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{SlideChooseView.class}, null, null, null, "switchTrackChecked"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{SlideChooseView.class}, null, null, null, "windowBackgroundWhiteGrayText"));
        arrayList.add(new ThemeDescription(this.bottomSheetView, 0, null, null, null, null, "windowBackgroundWhiteGrayText"));
        arrayList.add(new ThemeDescription(this.bottomSheetView, 0, new Class[]{CheckBoxCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.bottomSheetView, 0, new Class[]{CheckBoxCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteValueText"));
        arrayList.add(new ThemeDescription(this.bottomSheetView, 0, new Class[]{CheckBoxCell.class}, Theme.dividerPaint, null, null, "divider"));
        arrayList.add(new ThemeDescription(this.bottomSheetView, 0, new Class[]{StorageDiagramView.class}, null, null, null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) null, 0, new Class[]{TextCheckBoxCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, "dialogBackground"));
        arrayList.add(new ThemeDescription(this.bottomSheetView, 0, null, null, null, null, "statisticChartLine_blue"));
        arrayList.add(new ThemeDescription(this.bottomSheetView, 0, null, null, null, null, "statisticChartLine_green"));
        arrayList.add(new ThemeDescription(this.bottomSheetView, 0, null, null, null, null, "statisticChartLine_red"));
        arrayList.add(new ThemeDescription(this.bottomSheetView, 0, null, null, null, null, "statisticChartLine_golden"));
        arrayList.add(new ThemeDescription(this.bottomSheetView, 0, null, null, null, null, "statisticChartLine_lightblue"));
        arrayList.add(new ThemeDescription(this.bottomSheetView, 0, null, null, null, null, "statisticChartLine_lightgreen"));
        arrayList.add(new ThemeDescription(this.bottomSheetView, 0, null, null, null, null, "statisticChartLine_orange"));
        arrayList.add(new ThemeDescription(this.bottomSheetView, 0, null, null, null, null, "statisticChartLine_indigo"));
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getThemeDescriptions$8() {
        BottomSheet bottomSheet = this.bottomSheet;
        if (bottomSheet != null) {
            bottomSheet.setBackgroundColor(Theme.getColor("dialogBackground"));
        }
        View view = this.actionTextView;
        if (view != null) {
            view.setBackground(Theme.AdaptiveRipple.filledRect("featuredStickers_addButton", 4.0f));
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onRequestPermissionsResultFragment(int i, String[] strArr, int[] iArr) {
        FilesMigrationService.FilesMigrationBottomSheet filesMigrationBottomSheet;
        if (i == 4) {
            boolean z = false;
            int i2 = 0;
            while (true) {
                if (i2 >= iArr.length) {
                    z = true;
                    break;
                } else if (iArr[i2] != 0) {
                    break;
                } else {
                    i2++;
                }
            }
            if (!z || Build.VERSION.SDK_INT < 30 || (filesMigrationBottomSheet = FilesMigrationService.filesMigrationBottomSheet) == null) {
                return;
            }
            filesMigrationBottomSheet.migrateOldFolder();
        }
    }
}
