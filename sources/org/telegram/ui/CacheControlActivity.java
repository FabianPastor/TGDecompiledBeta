package org.telegram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
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
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
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

public class CacheControlActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private View actionTextView;
    private long audioSize = -1;
    private BottomSheet bottomSheet;
    private View bottomSheetView;
    /* access modifiers changed from: private */
    public int cacheInfoRow;
    private UndoView cacheRemovedTooltip;
    private long cacheSize = -1;
    /* access modifiers changed from: private */
    public boolean calculating = true;
    private volatile boolean canceled = false;
    private StorageDiagramView.ClearViewData[] clearViewData = new StorageDiagramView.ClearViewData[7];
    /* access modifiers changed from: private */
    public int databaseInfoRow;
    /* access modifiers changed from: private */
    public int databaseRow;
    /* access modifiers changed from: private */
    public long databaseSize = -1;
    /* access modifiers changed from: private */
    public int deviseStorageHeaderRow;
    private long documentsSize = -1;
    long fragmentCreateTime;
    /* access modifiers changed from: private */
    public int keepMediaChooserRow;
    /* access modifiers changed from: private */
    public int keepMediaHeaderRow;
    /* access modifiers changed from: private */
    public int keepMediaInfoRow;
    private LinearLayoutManager layoutManager;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    /* access modifiers changed from: private */
    public long migrateOldFolderRow = -1;
    private long musicSize = -1;
    private long photoSize = -1;
    AlertDialog progressDialog;
    /* access modifiers changed from: private */
    public int rowCount;
    private long stickersSize = -1;
    /* access modifiers changed from: private */
    public int storageUsageRow;
    /* access modifiers changed from: private */
    public long totalDeviceFreeSize = -1;
    /* access modifiers changed from: private */
    public long totalDeviceSize = -1;
    /* access modifiers changed from: private */
    public long totalSize = -1;
    private long videoSize = -1;

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        getNotificationCenter().addObserver(this, NotificationCenter.didClearDatabase);
        this.databaseSize = MessagesStorage.getInstance(this.currentAccount).getDatabaseSize();
        Utilities.globalQueue.postRunnable(new CacheControlActivity$$ExternalSyntheticLambda4(this));
        this.fragmentCreateTime = System.currentTimeMillis();
        updateRows();
        return true;
    }

    /* renamed from: lambda$onFragmentCreate$1$org-telegram-ui-CacheControlActivity  reason: not valid java name */
    public /* synthetic */ void m1436lambda$onFragmentCreate$1$orgtelegramuiCacheControlActivity() {
        File path;
        long blockSize;
        long availableBlocks;
        long blocksTotal;
        this.cacheSize = getDirectorySize(FileLoader.checkDirectory(4), 0);
        if (!this.canceled) {
            long directorySize = getDirectorySize(FileLoader.checkDirectory(0), 0);
            this.photoSize = directorySize;
            this.photoSize = directorySize + getDirectorySize(FileLoader.checkDirectory(100), 0);
            if (!this.canceled) {
                long directorySize2 = getDirectorySize(FileLoader.checkDirectory(2), 0);
                this.videoSize = directorySize2;
                this.videoSize = directorySize2 + getDirectorySize(FileLoader.checkDirectory(101), 0);
                if (!this.canceled) {
                    this.documentsSize = getDirectorySize(FileLoader.checkDirectory(3), 1);
                    if (!this.canceled) {
                        this.musicSize = getDirectorySize(FileLoader.checkDirectory(3), 2);
                        if (!this.canceled) {
                            this.stickersSize = getDirectorySize(new File(FileLoader.checkDirectory(4), "acache"), 0);
                            if (!this.canceled) {
                                long directorySize3 = getDirectorySize(FileLoader.checkDirectory(1), 0);
                                this.audioSize = directorySize3;
                                this.totalSize = this.cacheSize + this.videoSize + directorySize3 + this.photoSize + this.documentsSize + this.musicSize + this.stickersSize;
                                if (Build.VERSION.SDK_INT >= 19) {
                                    ArrayList<File> storageDirs = AndroidUtilities.getRootDirs();
                                    File file = storageDirs.get(0);
                                    path = file;
                                    String absolutePath = file.getAbsolutePath();
                                    if (!TextUtils.isEmpty(SharedConfig.storageCacheDir)) {
                                        int a = 0;
                                        int N = storageDirs.size();
                                        while (true) {
                                            if (a < N) {
                                                File file2 = storageDirs.get(a);
                                                if (file2.getAbsolutePath().startsWith(SharedConfig.storageCacheDir)) {
                                                    path = file2;
                                                    break;
                                                }
                                                a++;
                                            }
                                        }
                                    }
                                } else {
                                    path = new File(SharedConfig.storageCacheDir);
                                }
                                try {
                                    StatFs stat = new StatFs(path.getPath());
                                    if (Build.VERSION.SDK_INT >= 18) {
                                        blockSize = stat.getBlockSizeLong();
                                    } else {
                                        blockSize = (long) stat.getBlockSize();
                                    }
                                    if (Build.VERSION.SDK_INT >= 18) {
                                        availableBlocks = stat.getAvailableBlocksLong();
                                    } else {
                                        availableBlocks = (long) stat.getAvailableBlocks();
                                    }
                                    if (Build.VERSION.SDK_INT >= 18) {
                                        blocksTotal = stat.getBlockCountLong();
                                    } else {
                                        blocksTotal = (long) stat.getBlockCount();
                                    }
                                    this.totalDeviceSize = blocksTotal * blockSize;
                                    this.totalDeviceFreeSize = availableBlocks * blockSize;
                                } catch (Exception e) {
                                    FileLog.e((Throwable) e);
                                }
                                AndroidUtilities.runOnUIThread(new CacheControlActivity$$ExternalSyntheticLambda3(this));
                            }
                        }
                    }
                }
            }
        }
    }

    /* renamed from: lambda$onFragmentCreate$0$org-telegram-ui-CacheControlActivity  reason: not valid java name */
    public /* synthetic */ void m1435lambda$onFragmentCreate$0$orgtelegramuiCacheControlActivity() {
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
        View view = this.layoutManager.findViewByPosition(this.storageUsageRow);
        if (view instanceof StroageUsageView) {
            StroageUsageView stroageUsageView = (StroageUsageView) view;
            long currentTime = System.currentTimeMillis();
            if (Build.VERSION.SDK_INT >= 19 && currentTime - this.fragmentCreateTime > 250) {
                TransitionSet transition = new TransitionSet();
                ChangeBounds changeBounds = new ChangeBounds();
                changeBounds.setDuration(250);
                changeBounds.excludeTarget(stroageUsageView.legendLayout, true);
                Fade in = new Fade(1);
                in.setDuration(290);
                transition.addTransition(new Fade(2).setDuration(250)).addTransition(changeBounds).addTransition(in);
                transition.setOrdering(0);
                transition.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                TransitionManager.beginDelayedTransition(this.listView, transition);
            }
            stroageUsageView.setStorageUsage(this.calculating, this.databaseSize, this.totalSize, this.totalDeviceFreeSize, this.totalDeviceSize);
            RecyclerView.ViewHolder holder = this.listView.findViewHolderForAdapterPosition(this.storageUsageRow);
            if (holder != null) {
                stroageUsageView.setEnabled(this.listAdapter.isEnabled(holder));
                return;
            }
            return;
        }
        this.listAdapter.notifyDataSetChanged();
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        getNotificationCenter().removeObserver(this, NotificationCenter.didClearDatabase);
        try {
            AlertDialog alertDialog = this.progressDialog;
            if (alertDialog != null) {
                alertDialog.dismiss();
            }
        } catch (Exception e) {
        }
        this.progressDialog = null;
        this.canceled = true;
    }

    private long getDirectorySize(File dir, int documentsMusicType) {
        if (dir == null || this.canceled) {
            return 0;
        }
        if (dir.isDirectory()) {
            return Utilities.getDirSize(dir.getAbsolutePath(), documentsMusicType, false);
        }
        if (dir.isFile()) {
            return 0 + dir.length();
        }
        return 0;
    }

    private void cleanupFolders() {
        AlertDialog progressDialog2 = new AlertDialog(getParentActivity(), 3);
        progressDialog2.setCanCancel(false);
        progressDialog2.showDelayed(500);
        Utilities.globalQueue.postRunnable(new CacheControlActivity$$ExternalSyntheticLambda5(this, progressDialog2));
    }

    /* renamed from: lambda$cleanupFolders$3$org-telegram-ui-CacheControlActivity  reason: not valid java name */
    public /* synthetic */ void m1429lambda$cleanupFolders$3$orgtelegramuiCacheControlActivity(AlertDialog progressDialog2) {
        long blockSize;
        long availableBlocks;
        long blocksTotal;
        File file;
        int publicDirectoryType;
        boolean imagesCleared = false;
        long clearedSize = 0;
        for (int a = 0; a < 7; a++) {
            StorageDiagramView.ClearViewData[] clearViewDataArr = this.clearViewData;
            if (clearViewDataArr[a] != null && clearViewDataArr[a].clear) {
                int type = -1;
                int documentsMusicType = 0;
                if (a == 0) {
                    type = 0;
                    clearedSize += this.photoSize;
                } else if (a == 1) {
                    type = 2;
                    clearedSize += this.videoSize;
                } else if (a == 2) {
                    type = 3;
                    documentsMusicType = 1;
                    clearedSize += this.documentsSize;
                } else if (a == 3) {
                    type = 3;
                    documentsMusicType = 2;
                    clearedSize += this.musicSize;
                } else if (a == 4) {
                    type = 1;
                    clearedSize += this.audioSize;
                } else if (a == 5) {
                    type = 100;
                    clearedSize += this.stickersSize;
                } else if (a == 6) {
                    clearedSize += this.cacheSize;
                    type = 4;
                }
                if (type != -1) {
                    if (type == 100) {
                        file = new File(FileLoader.checkDirectory(4), "acache");
                    } else {
                        file = FileLoader.checkDirectory(type);
                    }
                    if (file != null) {
                        Utilities.clearDir(file.getAbsolutePath(), documentsMusicType, Long.MAX_VALUE, false);
                    }
                    if (type == 0 || type == 2) {
                        if (type == 0) {
                            publicDirectoryType = 100;
                        } else {
                            publicDirectoryType = 101;
                        }
                        File file2 = FileLoader.checkDirectory(publicDirectoryType);
                        if (file2 != null) {
                            Utilities.clearDir(file2.getAbsolutePath(), documentsMusicType, Long.MAX_VALUE, false);
                        }
                    }
                    if (type == 4) {
                        this.cacheSize = getDirectorySize(FileLoader.checkDirectory(4), documentsMusicType);
                        imagesCleared = true;
                    } else if (type == 1) {
                        this.audioSize = getDirectorySize(FileLoader.checkDirectory(1), documentsMusicType);
                    } else if (type == 3) {
                        if (documentsMusicType == 1) {
                            this.documentsSize = getDirectorySize(FileLoader.checkDirectory(3), documentsMusicType);
                        } else {
                            this.musicSize = getDirectorySize(FileLoader.checkDirectory(3), documentsMusicType);
                        }
                    } else if (type == 0) {
                        long directorySize = getDirectorySize(FileLoader.checkDirectory(0), documentsMusicType);
                        this.photoSize = directorySize;
                        this.photoSize = directorySize + getDirectorySize(FileLoader.checkDirectory(100), documentsMusicType);
                        imagesCleared = true;
                    } else if (type == 2) {
                        long directorySize2 = getDirectorySize(FileLoader.checkDirectory(2), documentsMusicType);
                        this.videoSize = directorySize2;
                        this.videoSize = directorySize2 + getDirectorySize(FileLoader.checkDirectory(101), documentsMusicType);
                    } else if (type == 100) {
                        this.stickersSize = getDirectorySize(new File(FileLoader.checkDirectory(4), "acache"), documentsMusicType);
                        imagesCleared = true;
                    }
                }
            }
        }
        boolean imagesClearedFinal = imagesCleared;
        this.totalSize = this.cacheSize + this.videoSize + this.audioSize + this.photoSize + this.documentsSize + this.musicSize + this.stickersSize;
        StatFs stat = new StatFs(Environment.getDataDirectory().getPath());
        if (Build.VERSION.SDK_INT >= 18) {
            blockSize = stat.getBlockSizeLong();
        } else {
            blockSize = (long) stat.getBlockSize();
        }
        if (Build.VERSION.SDK_INT >= 18) {
            availableBlocks = stat.getAvailableBlocksLong();
        } else {
            availableBlocks = (long) stat.getAvailableBlocks();
        }
        if (Build.VERSION.SDK_INT >= 18) {
            blocksTotal = stat.getBlockCountLong();
        } else {
            blocksTotal = (long) stat.getBlockCount();
        }
        this.totalDeviceSize = blocksTotal * blockSize;
        this.totalDeviceFreeSize = availableBlocks * blockSize;
        FileLoader.getInstance(this.currentAccount).checkCurrentDownloadsFiles();
        AndroidUtilities.runOnUIThread(new CacheControlActivity$$ExternalSyntheticLambda6(this, imagesClearedFinal, progressDialog2, clearedSize));
    }

    /* renamed from: lambda$cleanupFolders$2$org-telegram-ui-CacheControlActivity  reason: not valid java name */
    public /* synthetic */ void m1428lambda$cleanupFolders$2$orgtelegramuiCacheControlActivity(boolean imagesClearedFinal, AlertDialog progressDialog2, long finalClearedSize) {
        if (imagesClearedFinal) {
            ImageLoader.getInstance().clearMemory();
        }
        if (this.listAdapter != null) {
            updateStorageUsageRow();
        }
        try {
            progressDialog2.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        getMediaDataController().ringtoneDataStore.checkRingtoneSoundsLoaded();
        this.cacheRemovedTooltip.setInfoText(LocaleController.formatString("CacheWasCleared", NUM, AndroidUtilities.formatFileSize(finalClearedSize)));
        this.cacheRemovedTooltip.showWithAction(0, 19, (Runnable) null, (Runnable) null);
        getMediaDataController().loadAttachMenuBots(false, true);
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("StorageUsage", NUM));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    CacheControlActivity.this.finishFragment();
                }
            }
        });
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setVerticalScrollBarEnabled(false);
        RecyclerListView recyclerListView2 = this.listView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false);
        this.layoutManager = linearLayoutManager;
        recyclerListView2.setLayoutManager(linearLayoutManager);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new CacheControlActivity$$ExternalSyntheticLambda8(this, context));
        UndoView undoView = new UndoView(context);
        this.cacheRemovedTooltip = undoView;
        frameLayout.addView(undoView, LayoutHelper.createFrame(-1, -2.0f, 83, 8.0f, 0.0f, 8.0f, 8.0f));
        return this.fragmentView;
    }

    /* renamed from: lambda$createView$6$org-telegram-ui-CacheControlActivity  reason: not valid java name */
    public /* synthetic */ void m1433lambda$createView$6$orgtelegramuiCacheControlActivity(Context context, View view, int position) {
        long size;
        String color;
        String name;
        Context context2 = context;
        int i = position;
        if (getParentActivity() != null) {
            if (((long) i) == this.migrateOldFolderRow) {
                if (Build.VERSION.SDK_INT >= 30) {
                    migrateOldFolder();
                }
            } else if (i == this.databaseRow) {
                clearDatabase();
            } else if (i == this.storageUsageRow) {
                long j = 0;
                if (this.totalSize > 0 && getParentActivity() != null) {
                    AnonymousClass2 r3 = new BottomSheet(getParentActivity(), false) {
                        /* access modifiers changed from: protected */
                        public boolean canDismissWithSwipe() {
                            return false;
                        }
                    };
                    this.bottomSheet = r3;
                    int i2 = 1;
                    r3.setAllowNestedScroll(true);
                    this.bottomSheet.setApplyBottomPadding(false);
                    LinearLayout linearLayout = new LinearLayout(getParentActivity());
                    this.bottomSheetView = linearLayout;
                    linearLayout.setOrientation(1);
                    StorageDiagramView circleDiagramView = new StorageDiagramView(context2);
                    linearLayout.addView(circleDiagramView, LayoutHelper.createLinear(-2, -2, 1, 0, 16, 0, 16));
                    CheckBoxCell lastCreatedCheckbox = null;
                    int a = 0;
                    while (a < 7) {
                        if (a == 0) {
                            size = this.photoSize;
                            name = LocaleController.getString("LocalPhotoCache", NUM);
                            color = "statisticChartLine_blue";
                        } else if (a == i2) {
                            size = this.videoSize;
                            name = LocaleController.getString("LocalVideoCache", NUM);
                            color = "statisticChartLine_golden";
                        } else if (a == 2) {
                            size = this.documentsSize;
                            name = LocaleController.getString("LocalDocumentCache", NUM);
                            color = "statisticChartLine_green";
                        } else if (a == 3) {
                            size = this.musicSize;
                            name = LocaleController.getString("LocalMusicCache", NUM);
                            color = "statisticChartLine_indigo";
                        } else if (a == 4) {
                            size = this.audioSize;
                            name = LocaleController.getString("LocalAudioCache", NUM);
                            color = "statisticChartLine_red";
                        } else if (a == 5) {
                            size = this.stickersSize;
                            name = LocaleController.getString("AnimatedStickers", NUM);
                            color = "statisticChartLine_lightgreen";
                        } else {
                            size = this.cacheSize;
                            name = LocaleController.getString("LocalCache", NUM);
                            color = "statisticChartLine_lightblue";
                        }
                        if (size > j) {
                            this.clearViewData[a] = new StorageDiagramView.ClearViewData(circleDiagramView);
                            this.clearViewData[a].size = size;
                            this.clearViewData[a].color = color;
                            CheckBoxCell lastCreatedCheckbox2 = new CheckBoxCell(getParentActivity(), 4, 21, (Theme.ResourcesProvider) null);
                            CheckBoxCell checkBoxCell = lastCreatedCheckbox2;
                            checkBoxCell.setTag(Integer.valueOf(a));
                            checkBoxCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                            linearLayout.addView(checkBoxCell, LayoutHelper.createLinear(-1, 50));
                            checkBoxCell.setText(name, AndroidUtilities.formatFileSize(size), true, true);
                            checkBoxCell.setTextColor(Theme.getColor("dialogTextBlack"));
                            checkBoxCell.setCheckBoxColor(color, "windowBackgroundWhiteGrayIcon", "checkboxCheck");
                            checkBoxCell.setOnClickListener(new CacheControlActivity$$ExternalSyntheticLambda1(this));
                            lastCreatedCheckbox = lastCreatedCheckbox2;
                        } else {
                            this.clearViewData[a] = null;
                        }
                        a++;
                        i2 = 1;
                        j = 0;
                    }
                    if (lastCreatedCheckbox != null) {
                        lastCreatedCheckbox.setNeedDivider(false);
                    }
                    circleDiagramView.setData(this.clearViewData);
                    BottomSheet.BottomSheetCell cell = new BottomSheet.BottomSheetCell(getParentActivity(), 2);
                    cell.setTextAndIcon((CharSequence) LocaleController.getString("ClearMediaCache", NUM), 0);
                    this.actionTextView = cell.getTextView();
                    cell.getTextView().setOnClickListener(new CacheControlActivity$$ExternalSyntheticLambda2(this));
                    linearLayout.addView(cell, LayoutHelper.createLinear(-1, 50));
                    NestedScrollView scrollView = new NestedScrollView(context2);
                    scrollView.setVerticalScrollBarEnabled(false);
                    scrollView.addView(linearLayout);
                    this.bottomSheet.setCustomView(scrollView);
                    showDialog(this.bottomSheet);
                }
            }
        }
    }

    /* renamed from: lambda$createView$4$org-telegram-ui-CacheControlActivity  reason: not valid java name */
    public /* synthetic */ void m1431lambda$createView$4$orgtelegramuiCacheControlActivity(View v) {
        int enabledCount = 0;
        int i = 0;
        while (true) {
            StorageDiagramView.ClearViewData[] clearViewDataArr = this.clearViewData;
            if (i >= clearViewDataArr.length) {
                break;
            }
            if (clearViewDataArr[i] != null && clearViewDataArr[i].clear) {
                enabledCount++;
            }
            i++;
        }
        CheckBoxCell cell = (CheckBoxCell) v;
        int num = ((Integer) cell.getTag()).intValue();
        if (enabledCount != 1 || !this.clearViewData[num].clear) {
            StorageDiagramView.ClearViewData[] clearViewDataArr2 = this.clearViewData;
            clearViewDataArr2[num].setClear(!clearViewDataArr2[num].clear);
            cell.setChecked(this.clearViewData[num].clear, true);
            return;
        }
        AndroidUtilities.shakeView(((CheckBoxCell) v).getCheckBoxView(), 2.0f, 0);
    }

    /* renamed from: lambda$createView$5$org-telegram-ui-CacheControlActivity  reason: not valid java name */
    public /* synthetic */ void m1432lambda$createView$5$orgtelegramuiCacheControlActivity(View v) {
        try {
            if (this.visibleDialog != null) {
                this.visibleDialog.dismiss();
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        cleanupFolders();
    }

    private void migrateOldFolder() {
        FilesMigrationService.checkBottomSheet(this);
    }

    private void clearDatabase() {
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        builder.setTitle(LocaleController.getString("LocalDatabaseClearTextTitle", NUM));
        builder.setMessage(LocaleController.getString("LocalDatabaseClearText", NUM));
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        builder.setPositiveButton(LocaleController.getString("CacheClear", NUM), new CacheControlActivity$$ExternalSyntheticLambda0(this));
        AlertDialog alertDialog = builder.create();
        showDialog(alertDialog);
        TextView button = (TextView) alertDialog.getButton(-1);
        if (button != null) {
            button.setTextColor(Theme.getColor("dialogTextRed2"));
        }
    }

    /* renamed from: lambda$clearDatabase$7$org-telegram-ui-CacheControlActivity  reason: not valid java name */
    public /* synthetic */ void m1430lambda$clearDatabase$7$orgtelegramuiCacheControlActivity(DialogInterface dialogInterface, int i) {
        if (getParentActivity() != null) {
            AlertDialog alertDialog = new AlertDialog(getParentActivity(), 3);
            this.progressDialog = alertDialog;
            alertDialog.setCanCancel(false);
            this.progressDialog.showDelayed(500);
            MessagesController.getInstance(this.currentAccount).clearQueryTime();
            getMessagesStorage().clearLocalDatabase();
        }
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.didClearDatabase) {
            try {
                AlertDialog alertDialog = this.progressDialog;
                if (alertDialog != null) {
                    alertDialog.dismiss();
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            this.progressDialog = null;
            if (this.listAdapter != null) {
                this.databaseSize = MessagesStorage.getInstance(this.currentAccount).getDatabaseSize();
                this.listAdapter.notifyDataSetChanged();
            }
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int position = holder.getAdapterPosition();
            return ((long) position) == CacheControlActivity.this.migrateOldFolderRow || position == CacheControlActivity.this.databaseRow || (position == CacheControlActivity.this.storageUsageRow && CacheControlActivity.this.totalSize > 0 && !CacheControlActivity.this.calculating);
        }

        public int getItemCount() {
            return CacheControlActivity.this.rowCount;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            int index;
            switch (viewType) {
                case 0:
                    view = new TextSettingsCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                case 2:
                    view = new StroageUsageView(this.mContext);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                case 3:
                    view = new HeaderCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                case 4:
                    SlideChooseView slideChooseView = new SlideChooseView(this.mContext);
                    view = slideChooseView;
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
                    slideChooseView.setCallback(CacheControlActivity$ListAdapter$$ExternalSyntheticLambda0.INSTANCE);
                    int keepMedia = SharedConfig.keepMedia;
                    if (keepMedia == 3) {
                        index = 0;
                    } else {
                        index = keepMedia + 1;
                    }
                    slideChooseView.setOptions(index, LocaleController.formatPluralString("Days", 3), LocaleController.formatPluralString("Weeks", 1), LocaleController.formatPluralString("Months", 1), LocaleController.getString("KeepMediaForever", NUM));
                    break;
                default:
                    view = new TextInfoPrivacyCell(this.mContext);
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        static /* synthetic */ void lambda$onCreateViewHolder$0(int index) {
            if (index == 0) {
                SharedConfig.setKeepMedia(3);
            } else if (index == 1) {
                SharedConfig.setKeepMedia(0);
            } else if (index == 2) {
                SharedConfig.setKeepMedia(1);
            } else if (index == 3) {
                SharedConfig.setKeepMedia(2);
            }
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 0:
                    TextSettingsCell textCell = (TextSettingsCell) holder.itemView;
                    if (position == CacheControlActivity.this.databaseRow) {
                        textCell.setTextAndValue(LocaleController.getString("ClearLocalDatabase", NUM), AndroidUtilities.formatFileSize(CacheControlActivity.this.databaseSize), false);
                        return;
                    } else if (((long) position) == CacheControlActivity.this.migrateOldFolderRow) {
                        textCell.setTextAndValue(LocaleController.getString("MigrateOldFolder", NUM), (CharSequence) null, false);
                        return;
                    } else {
                        return;
                    }
                case 1:
                    TextInfoPrivacyCell privacyCell = (TextInfoPrivacyCell) holder.itemView;
                    if (position == CacheControlActivity.this.databaseInfoRow) {
                        privacyCell.setText(LocaleController.getString("LocalDatabaseInfo", NUM));
                        privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                        return;
                    } else if (position == CacheControlActivity.this.cacheInfoRow) {
                        privacyCell.setText("");
                        privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                        return;
                    } else if (position == CacheControlActivity.this.keepMediaInfoRow) {
                        privacyCell.setText(AndroidUtilities.replaceTags(LocaleController.getString("KeepMediaInfo", NUM)));
                        privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                        return;
                    } else {
                        return;
                    }
                case 2:
                    ((StroageUsageView) holder.itemView).setStorageUsage(CacheControlActivity.this.calculating, CacheControlActivity.this.databaseSize, CacheControlActivity.this.totalSize, CacheControlActivity.this.totalDeviceFreeSize, CacheControlActivity.this.totalDeviceSize);
                    return;
                case 3:
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (position == CacheControlActivity.this.keepMediaHeaderRow) {
                        headerCell.setText(LocaleController.getString("KeepMedia", NUM));
                        return;
                    } else if (position == CacheControlActivity.this.deviseStorageHeaderRow) {
                        headerCell.setText(LocaleController.getString("DeviceStorage", NUM));
                        return;
                    } else {
                        return;
                    }
                default:
                    return;
            }
        }

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
            if (i == CacheControlActivity.this.keepMediaChooserRow) {
                return 4;
            }
            return 0;
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ThemeDescription.ThemeDescriptionDelegate deldegagte = new CacheControlActivity$$ExternalSyntheticLambda7(this);
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, SlideChooseView.class, StroageUsageView.class, HeaderCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteValueText"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{StroageUsageView.class}, new String[]{"paintFill"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_progressBackground"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{StroageUsageView.class}, new String[]{"paintProgress"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_progress"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{StroageUsageView.class}, new String[]{"telegramCacheTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{StroageUsageView.class}, new String[]{"freeSizeTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{StroageUsageView.class}, new String[]{"calculationgTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{StroageUsageView.class}, new String[]{"paintProgress2"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_progressBackground2"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{SlideChooseView.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrack"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{SlideChooseView.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackChecked"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{SlideChooseView.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText"));
        arrayList.add(new ThemeDescription(this.bottomSheetView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText"));
        arrayList.add(new ThemeDescription(this.bottomSheetView, 0, new Class[]{CheckBoxCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.bottomSheetView, 0, new Class[]{CheckBoxCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteValueText"));
        arrayList.add(new ThemeDescription(this.bottomSheetView, 0, new Class[]{CheckBoxCell.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        arrayList.add(new ThemeDescription(this.bottomSheetView, 0, new Class[]{StorageDiagramView.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) null, 0, new Class[]{TextCheckBoxCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, deldegagte, "dialogBackground"));
        arrayList.add(new ThemeDescription(this.bottomSheetView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "statisticChartLine_blue"));
        arrayList.add(new ThemeDescription(this.bottomSheetView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "statisticChartLine_green"));
        arrayList.add(new ThemeDescription(this.bottomSheetView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "statisticChartLine_red"));
        arrayList.add(new ThemeDescription(this.bottomSheetView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "statisticChartLine_golden"));
        arrayList.add(new ThemeDescription(this.bottomSheetView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "statisticChartLine_lightblue"));
        arrayList.add(new ThemeDescription(this.bottomSheetView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "statisticChartLine_lightgreen"));
        arrayList.add(new ThemeDescription(this.bottomSheetView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "statisticChartLine_orange"));
        arrayList.add(new ThemeDescription(this.bottomSheetView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "statisticChartLine_indigo"));
        return arrayList;
    }

    /* renamed from: lambda$getThemeDescriptions$8$org-telegram-ui-CacheControlActivity  reason: not valid java name */
    public /* synthetic */ void m1434x48b51629() {
        BottomSheet bottomSheet2 = this.bottomSheet;
        if (bottomSheet2 != null) {
            bottomSheet2.setBackgroundColor(Theme.getColor("dialogBackground"));
        }
        View view = this.actionTextView;
        if (view != null) {
            view.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), Theme.getColor("featuredStickers_addButton"), Theme.getColor("featuredStickers_addButtonPressed")));
        }
    }

    public void onRequestPermissionsResultFragment(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 4) {
            boolean allGranted = true;
            int a = 0;
            while (true) {
                if (a >= grantResults.length) {
                    break;
                } else if (grantResults[a] != 0) {
                    allGranted = false;
                    break;
                } else {
                    a++;
                }
            }
            if (allGranted && Build.VERSION.SDK_INT >= 30 && FilesMigrationService.filesMigrationBottomSheet != null) {
                FilesMigrationService.filesMigrationBottomSheet.migrateOldFolder();
            }
        }
    }
}
