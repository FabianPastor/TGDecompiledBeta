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

public class CacheControlActivity extends BaseFragment {
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
        this.databaseSize = MessagesStorage.getInstance(this.currentAccount).getDatabaseSize();
        Utilities.globalQueue.postRunnable(new CacheControlActivity$$ExternalSyntheticLambda5(this));
        this.fragmentCreateTime = System.currentTimeMillis();
        updateRows();
        return true;
    }

    /* renamed from: lambda$onFragmentCreate$1$org-telegram-ui-CacheControlActivity  reason: not valid java name */
    public /* synthetic */ void m1476lambda$onFragmentCreate$1$orgtelegramuiCacheControlActivity() {
        File path;
        long blockSize;
        long availableBlocks;
        long blocksTotal;
        this.cacheSize = getDirectorySize(FileLoader.checkDirectory(4), 0);
        if (!this.canceled) {
            this.photoSize = getDirectorySize(FileLoader.checkDirectory(0), 0);
            if (!this.canceled) {
                this.videoSize = getDirectorySize(FileLoader.checkDirectory(2), 0);
                if (!this.canceled) {
                    this.documentsSize = getDirectorySize(FileLoader.checkDirectory(3), 1);
                    if (!this.canceled) {
                        this.musicSize = getDirectorySize(FileLoader.checkDirectory(3), 2);
                        if (!this.canceled) {
                            this.stickersSize = getDirectorySize(new File(FileLoader.checkDirectory(4), "acache"), 0);
                            if (!this.canceled) {
                                long directorySize = getDirectorySize(FileLoader.checkDirectory(1), 0);
                                this.audioSize = directorySize;
                                this.totalSize = this.cacheSize + this.videoSize + directorySize + this.photoSize + this.documentsSize + this.musicSize + this.stickersSize;
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
                                AndroidUtilities.runOnUIThread(new CacheControlActivity$$ExternalSyntheticLambda4(this));
                            }
                        }
                    }
                }
            }
        }
    }

    /* renamed from: lambda$onFragmentCreate$0$org-telegram-ui-CacheControlActivity  reason: not valid java name */
    public /* synthetic */ void m1475lambda$onFragmentCreate$0$orgtelegramuiCacheControlActivity() {
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
        AlertDialog progressDialog = new AlertDialog(getParentActivity(), 3);
        progressDialog.setCanCacnel(false);
        progressDialog.showDelayed(500);
        Utilities.globalQueue.postRunnable(new CacheControlActivity$$ExternalSyntheticLambda6(this, progressDialog));
    }

    /* renamed from: lambda$cleanupFolders$3$org-telegram-ui-CacheControlActivity  reason: not valid java name */
    public /* synthetic */ void m1467lambda$cleanupFolders$3$orgtelegramuiCacheControlActivity(AlertDialog progressDialog) {
        long blockSize;
        long availableBlocks;
        long blocksTotal;
        File file;
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
                        File file2 = file;
                        Utilities.clearDir(file.getAbsolutePath(), documentsMusicType, Long.MAX_VALUE, false);
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
                        this.photoSize = getDirectorySize(FileLoader.checkDirectory(0), documentsMusicType);
                        imagesCleared = true;
                    } else if (type == 2) {
                        this.videoSize = getDirectorySize(FileLoader.checkDirectory(2), documentsMusicType);
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
        AndroidUtilities.runOnUIThread(new CacheControlActivity$$ExternalSyntheticLambda9(this, imagesClearedFinal, progressDialog, clearedSize));
    }

    /* renamed from: lambda$cleanupFolders$2$org-telegram-ui-CacheControlActivity  reason: not valid java name */
    public /* synthetic */ void m1466lambda$cleanupFolders$2$orgtelegramuiCacheControlActivity(boolean imagesClearedFinal, AlertDialog progressDialog, long finalClearedSize) {
        if (imagesClearedFinal) {
            ImageLoader.getInstance().clearMemory();
        }
        if (this.listAdapter != null) {
            updateStorageUsageRow();
        }
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        this.cacheRemovedTooltip.setInfoText(LocaleController.formatString("CacheWasCleared", NUM, AndroidUtilities.formatFileSize(finalClearedSize)));
        this.cacheRemovedTooltip.showWithAction(0, 19, (Runnable) null, (Runnable) null);
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
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new CacheControlActivity$$ExternalSyntheticLambda1(this, context));
        UndoView undoView = new UndoView(context);
        this.cacheRemovedTooltip = undoView;
        frameLayout.addView(undoView, LayoutHelper.createFrame(-1, -2.0f, 83, 8.0f, 0.0f, 8.0f, 8.0f));
        return this.fragmentView;
    }

    /* renamed from: lambda$createView$6$org-telegram-ui-CacheControlActivity  reason: not valid java name */
    public /* synthetic */ void m1473lambda$createView$6$orgtelegramuiCacheControlActivity(Context context, View view, int position) {
        long size;
        String color;
        String name;
        Context context2 = context;
        int i = position;
        if (getParentActivity() != null) {
            if (((long) i) == this.migrateOldFolderRow) {
                migrateOldFolder();
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
                            checkBoxCell.setOnClickListener(new CacheControlActivity$$ExternalSyntheticLambda2(this));
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
                    cell.getTextView().setOnClickListener(new CacheControlActivity$$ExternalSyntheticLambda3(this));
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
    public /* synthetic */ void m1471lambda$createView$4$orgtelegramuiCacheControlActivity(View v) {
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
    public /* synthetic */ void m1472lambda$createView$5$orgtelegramuiCacheControlActivity(View v) {
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

    /* renamed from: lambda$clearDatabase$9$org-telegram-ui-CacheControlActivity  reason: not valid java name */
    public /* synthetic */ void m1470lambda$clearDatabase$9$orgtelegramuiCacheControlActivity(DialogInterface dialogInterface, int i) {
        if (getParentActivity() != null) {
            AlertDialog progressDialog = new AlertDialog(getParentActivity(), 3);
            progressDialog.setCanCacnel(false);
            progressDialog.showDelayed(500);
            MessagesController.getInstance(this.currentAccount).clearQueryTime();
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new CacheControlActivity$$ExternalSyntheticLambda8(this, progressDialog));
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:49:0x0214 A[Catch:{ Exception -> 0x0279, all -> 0x0277 }] */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x0220 A[Catch:{ Exception -> 0x0279, all -> 0x0277 }] */
    /* renamed from: lambda$clearDatabase$8$org-telegram-ui-CacheControlActivity  reason: not valid java name */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void m1469lambda$clearDatabase$8$orgtelegramuiCacheControlActivity(org.telegram.ui.ActionBar.AlertDialog r25) {
        /*
            r24 = this;
            r1 = r24
            r2 = r25
            java.lang.String r3 = " AND mid != "
            int r0 = r1.currentAccount     // Catch:{ Exception -> 0x0279 }
            org.telegram.messenger.MessagesStorage r0 = org.telegram.messenger.MessagesStorage.getInstance(r0)     // Catch:{ Exception -> 0x0279 }
            org.telegram.SQLite.SQLiteDatabase r0 = r0.getDatabase()     // Catch:{ Exception -> 0x0279 }
            r4 = r0
            java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ Exception -> 0x0279 }
            r0.<init>()     // Catch:{ Exception -> 0x0279 }
            r5 = r0
            java.lang.String r0 = "SELECT did FROM dialogs WHERE 1"
            r6 = 0
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ Exception -> 0x0279 }
            org.telegram.SQLite.SQLiteCursor r0 = r4.queryFinalized(r0, r7)     // Catch:{ Exception -> 0x0279 }
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0279 }
            r7.<init>()     // Catch:{ Exception -> 0x0279 }
        L_0x0025:
            boolean r8 = r0.next()     // Catch:{ Exception -> 0x0279 }
            if (r8 == 0) goto L_0x003d
            long r8 = r0.longValue(r6)     // Catch:{ Exception -> 0x0279 }
            boolean r10 = org.telegram.messenger.DialogObject.isEncryptedDialog(r8)     // Catch:{ Exception -> 0x0279 }
            if (r10 != 0) goto L_0x003c
            java.lang.Long r10 = java.lang.Long.valueOf(r8)     // Catch:{ Exception -> 0x0279 }
            r5.add(r10)     // Catch:{ Exception -> 0x0279 }
        L_0x003c:
            goto L_0x0025
        L_0x003d:
            r0.dispose()     // Catch:{ Exception -> 0x0279 }
            java.lang.String r8 = "REPLACE INTO messages_holes VALUES(?, ?, ?)"
            org.telegram.SQLite.SQLitePreparedStatement r8 = r4.executeFast(r8)     // Catch:{ Exception -> 0x0279 }
            java.lang.String r9 = "REPLACE INTO media_holes_v2 VALUES(?, ?, ?, ?)"
            org.telegram.SQLite.SQLitePreparedStatement r9 = r4.executeFast(r9)     // Catch:{ Exception -> 0x0279 }
            r4.beginTransaction()     // Catch:{ Exception -> 0x0279 }
            r10 = 0
        L_0x0050:
            int r11 = r5.size()     // Catch:{ Exception -> 0x0279 }
            if (r10 >= r11) goto L_0x023d
            java.lang.Object r11 = r5.get(r10)     // Catch:{ Exception -> 0x0279 }
            java.lang.Long r11 = (java.lang.Long) r11     // Catch:{ Exception -> 0x0279 }
            r12 = 0
            java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0279 }
            r13.<init>()     // Catch:{ Exception -> 0x0279 }
            java.lang.String r14 = "SELECT COUNT(mid) FROM messages_v2 WHERE uid = "
            r13.append(r14)     // Catch:{ Exception -> 0x0279 }
            r13.append(r11)     // Catch:{ Exception -> 0x0279 }
            java.lang.String r13 = r13.toString()     // Catch:{ Exception -> 0x0279 }
            java.lang.Object[] r14 = new java.lang.Object[r6]     // Catch:{ Exception -> 0x0279 }
            org.telegram.SQLite.SQLiteCursor r13 = r4.queryFinalized(r13, r14)     // Catch:{ Exception -> 0x0279 }
            r0 = r13
            boolean r13 = r0.next()     // Catch:{ Exception -> 0x0279 }
            if (r13 == 0) goto L_0x0080
            int r13 = r0.intValue(r6)     // Catch:{ Exception -> 0x0279 }
            r12 = r13
        L_0x0080:
            r0.dispose()     // Catch:{ Exception -> 0x0279 }
            r13 = 2
            if (r12 > r13) goto L_0x008c
            r19 = r5
            r16 = r7
            goto L_0x0234
        L_0x008c:
            java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0279 }
            r13.<init>()     // Catch:{ Exception -> 0x0279 }
            java.lang.String r14 = "SELECT last_mid_i, last_mid FROM dialogs WHERE did = "
            r13.append(r14)     // Catch:{ Exception -> 0x0279 }
            r13.append(r11)     // Catch:{ Exception -> 0x0279 }
            java.lang.String r13 = r13.toString()     // Catch:{ Exception -> 0x0279 }
            java.lang.Object[] r14 = new java.lang.Object[r6]     // Catch:{ Exception -> 0x0279 }
            org.telegram.SQLite.SQLiteCursor r13 = r4.queryFinalized(r13, r14)     // Catch:{ Exception -> 0x0279 }
            r0 = -1
            boolean r14 = r13.next()     // Catch:{ Exception -> 0x0279 }
            if (r14 == 0) goto L_0x0225
            long r14 = r13.longValue(r6)     // Catch:{ Exception -> 0x0279 }
            r6 = 1
            long r17 = r13.longValue(r6)     // Catch:{ Exception -> 0x0279 }
            r19 = r17
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0279 }
            r6.<init>()     // Catch:{ Exception -> 0x0279 }
            r17 = r0
            java.lang.String r0 = "SELECT data FROM messages_v2 WHERE uid = "
            r6.append(r0)     // Catch:{ Exception -> 0x0279 }
            r6.append(r11)     // Catch:{ Exception -> 0x0279 }
            java.lang.String r0 = " AND mid IN ("
            r6.append(r0)     // Catch:{ Exception -> 0x0279 }
            r6.append(r14)     // Catch:{ Exception -> 0x0279 }
            java.lang.String r0 = ","
            r6.append(r0)     // Catch:{ Exception -> 0x0279 }
            r18 = r12
            r21 = r13
            r12 = r19
            r6.append(r12)     // Catch:{ Exception -> 0x0279 }
            java.lang.String r0 = ")"
            r6.append(r0)     // Catch:{ Exception -> 0x0279 }
            java.lang.String r0 = r6.toString()     // Catch:{ Exception -> 0x0279 }
            r19 = r5
            r6 = 0
            java.lang.Object[] r5 = new java.lang.Object[r6]     // Catch:{ Exception -> 0x0279 }
            org.telegram.SQLite.SQLiteCursor r0 = r4.queryFinalized(r0, r5)     // Catch:{ Exception -> 0x0279 }
            r5 = r0
        L_0x00ed:
            boolean r0 = r5.next()     // Catch:{ Exception -> 0x0140 }
            if (r0 == 0) goto L_0x0137
            r6 = 0
            org.telegram.tgnet.NativeByteBuffer r0 = r5.byteBufferValue(r6)     // Catch:{ Exception -> 0x0140 }
            if (r0 == 0) goto L_0x012a
            r16 = r7
            int r7 = r0.readInt32(r6)     // Catch:{ Exception -> 0x0124 }
            org.telegram.tgnet.TLRPC$Message r7 = org.telegram.tgnet.TLRPC.Message.TLdeserialize(r0, r7, r6)     // Catch:{ Exception -> 0x0124 }
            if (r7 == 0) goto L_0x011a
            int r6 = r7.id     // Catch:{ Exception -> 0x0124 }
            r17 = r6
            int r6 = r1.currentAccount     // Catch:{ Exception -> 0x0124 }
            org.telegram.messenger.UserConfig r6 = org.telegram.messenger.UserConfig.getInstance(r6)     // Catch:{ Exception -> 0x0124 }
            r22 = r8
            r23 = r9
            long r8 = r6.clientUserId     // Catch:{ Exception -> 0x0122 }
            r7.readAttachPath(r0, r8)     // Catch:{ Exception -> 0x0122 }
            goto L_0x011e
        L_0x011a:
            r22 = r8
            r23 = r9
        L_0x011e:
            r0.reuse()     // Catch:{ Exception -> 0x0122 }
            goto L_0x0130
        L_0x0122:
            r0 = move-exception
            goto L_0x0147
        L_0x0124:
            r0 = move-exception
            r22 = r8
            r23 = r9
            goto L_0x0147
        L_0x012a:
            r16 = r7
            r22 = r8
            r23 = r9
        L_0x0130:
            r7 = r16
            r8 = r22
            r9 = r23
            goto L_0x00ed
        L_0x0137:
            r16 = r7
            r22 = r8
            r23 = r9
            r0 = r17
            goto L_0x014c
        L_0x0140:
            r0 = move-exception
            r16 = r7
            r22 = r8
            r23 = r9
        L_0x0147:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ Exception -> 0x0279 }
            r0 = r17
        L_0x014c:
            r5.dispose()     // Catch:{ Exception -> 0x0279 }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0279 }
            r6.<init>()     // Catch:{ Exception -> 0x0279 }
            java.lang.String r7 = "DELETE FROM messages_v2 WHERE uid = "
            r6.append(r7)     // Catch:{ Exception -> 0x0279 }
            r6.append(r11)     // Catch:{ Exception -> 0x0279 }
            r6.append(r3)     // Catch:{ Exception -> 0x0279 }
            r6.append(r14)     // Catch:{ Exception -> 0x0279 }
            r6.append(r3)     // Catch:{ Exception -> 0x0279 }
            r6.append(r12)     // Catch:{ Exception -> 0x0279 }
            java.lang.String r6 = r6.toString()     // Catch:{ Exception -> 0x0279 }
            org.telegram.SQLite.SQLitePreparedStatement r6 = r4.executeFast(r6)     // Catch:{ Exception -> 0x0279 }
            org.telegram.SQLite.SQLitePreparedStatement r6 = r6.stepThis()     // Catch:{ Exception -> 0x0279 }
            r6.dispose()     // Catch:{ Exception -> 0x0279 }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0279 }
            r6.<init>()     // Catch:{ Exception -> 0x0279 }
            java.lang.String r7 = "DELETE FROM messages_holes WHERE uid = "
            r6.append(r7)     // Catch:{ Exception -> 0x0279 }
            r6.append(r11)     // Catch:{ Exception -> 0x0279 }
            java.lang.String r6 = r6.toString()     // Catch:{ Exception -> 0x0279 }
            org.telegram.SQLite.SQLitePreparedStatement r6 = r4.executeFast(r6)     // Catch:{ Exception -> 0x0279 }
            org.telegram.SQLite.SQLitePreparedStatement r6 = r6.stepThis()     // Catch:{ Exception -> 0x0279 }
            r6.dispose()     // Catch:{ Exception -> 0x0279 }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0279 }
            r6.<init>()     // Catch:{ Exception -> 0x0279 }
            java.lang.String r7 = "DELETE FROM bot_keyboard WHERE uid = "
            r6.append(r7)     // Catch:{ Exception -> 0x0279 }
            r6.append(r11)     // Catch:{ Exception -> 0x0279 }
            java.lang.String r6 = r6.toString()     // Catch:{ Exception -> 0x0279 }
            org.telegram.SQLite.SQLitePreparedStatement r6 = r4.executeFast(r6)     // Catch:{ Exception -> 0x0279 }
            org.telegram.SQLite.SQLitePreparedStatement r6 = r6.stepThis()     // Catch:{ Exception -> 0x0279 }
            r6.dispose()     // Catch:{ Exception -> 0x0279 }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0279 }
            r6.<init>()     // Catch:{ Exception -> 0x0279 }
            java.lang.String r7 = "DELETE FROM media_counts_v2 WHERE uid = "
            r6.append(r7)     // Catch:{ Exception -> 0x0279 }
            r6.append(r11)     // Catch:{ Exception -> 0x0279 }
            java.lang.String r6 = r6.toString()     // Catch:{ Exception -> 0x0279 }
            org.telegram.SQLite.SQLitePreparedStatement r6 = r4.executeFast(r6)     // Catch:{ Exception -> 0x0279 }
            org.telegram.SQLite.SQLitePreparedStatement r6 = r6.stepThis()     // Catch:{ Exception -> 0x0279 }
            r6.dispose()     // Catch:{ Exception -> 0x0279 }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0279 }
            r6.<init>()     // Catch:{ Exception -> 0x0279 }
            java.lang.String r7 = "DELETE FROM media_v4 WHERE uid = "
            r6.append(r7)     // Catch:{ Exception -> 0x0279 }
            r6.append(r11)     // Catch:{ Exception -> 0x0279 }
            java.lang.String r6 = r6.toString()     // Catch:{ Exception -> 0x0279 }
            org.telegram.SQLite.SQLitePreparedStatement r6 = r4.executeFast(r6)     // Catch:{ Exception -> 0x0279 }
            org.telegram.SQLite.SQLitePreparedStatement r6 = r6.stepThis()     // Catch:{ Exception -> 0x0279 }
            r6.dispose()     // Catch:{ Exception -> 0x0279 }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0279 }
            r6.<init>()     // Catch:{ Exception -> 0x0279 }
            java.lang.String r7 = "DELETE FROM media_holes_v2 WHERE uid = "
            r6.append(r7)     // Catch:{ Exception -> 0x0279 }
            r6.append(r11)     // Catch:{ Exception -> 0x0279 }
            java.lang.String r6 = r6.toString()     // Catch:{ Exception -> 0x0279 }
            org.telegram.SQLite.SQLitePreparedStatement r6 = r4.executeFast(r6)     // Catch:{ Exception -> 0x0279 }
            org.telegram.SQLite.SQLitePreparedStatement r6 = r6.stepThis()     // Catch:{ Exception -> 0x0279 }
            r6.dispose()     // Catch:{ Exception -> 0x0279 }
            int r6 = r1.currentAccount     // Catch:{ Exception -> 0x0279 }
            org.telegram.messenger.MediaDataController r6 = org.telegram.messenger.MediaDataController.getInstance(r6)     // Catch:{ Exception -> 0x0279 }
            long r7 = r11.longValue()     // Catch:{ Exception -> 0x0279 }
            r9 = 0
            r6.clearBotKeyboard(r7, r9)     // Catch:{ Exception -> 0x0279 }
            r6 = -1
            if (r0 == r6) goto L_0x0220
            long r6 = r11.longValue()     // Catch:{ Exception -> 0x0279 }
            r8 = r22
            r9 = r23
            org.telegram.messenger.MessagesStorage.createFirstHoles(r6, r8, r9, r0)     // Catch:{ Exception -> 0x0279 }
            goto L_0x022f
        L_0x0220:
            r8 = r22
            r9 = r23
            goto L_0x022f
        L_0x0225:
            r17 = r0
            r19 = r5
            r16 = r7
            r18 = r12
            r21 = r13
        L_0x022f:
            r21.dispose()     // Catch:{ Exception -> 0x0279 }
            r0 = r21
        L_0x0234:
            int r10 = r10 + 1
            r7 = r16
            r5 = r19
            r6 = 0
            goto L_0x0050
        L_0x023d:
            r19 = r5
            r16 = r7
            r8.dispose()     // Catch:{ Exception -> 0x0279 }
            r9.dispose()     // Catch:{ Exception -> 0x0279 }
            r4.commitTransaction()     // Catch:{ Exception -> 0x0279 }
            java.lang.String r3 = "PRAGMA journal_size_limit = 0"
            org.telegram.SQLite.SQLitePreparedStatement r3 = r4.executeFast(r3)     // Catch:{ Exception -> 0x0279 }
            org.telegram.SQLite.SQLitePreparedStatement r3 = r3.stepThis()     // Catch:{ Exception -> 0x0279 }
            r3.dispose()     // Catch:{ Exception -> 0x0279 }
            java.lang.String r3 = "VACUUM"
            org.telegram.SQLite.SQLitePreparedStatement r3 = r4.executeFast(r3)     // Catch:{ Exception -> 0x0279 }
            org.telegram.SQLite.SQLitePreparedStatement r3 = r3.stepThis()     // Catch:{ Exception -> 0x0279 }
            r3.dispose()     // Catch:{ Exception -> 0x0279 }
            java.lang.String r3 = "PRAGMA journal_size_limit = -1"
            org.telegram.SQLite.SQLitePreparedStatement r3 = r4.executeFast(r3)     // Catch:{ Exception -> 0x0279 }
            org.telegram.SQLite.SQLitePreparedStatement r3 = r3.stepThis()     // Catch:{ Exception -> 0x0279 }
            r3.dispose()     // Catch:{ Exception -> 0x0279 }
            org.telegram.ui.CacheControlActivity$$ExternalSyntheticLambda7 r0 = new org.telegram.ui.CacheControlActivity$$ExternalSyntheticLambda7
            r0.<init>(r1, r2)
            goto L_0x0282
        L_0x0277:
            r0 = move-exception
            goto L_0x0287
        L_0x0279:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x0277 }
            org.telegram.ui.CacheControlActivity$$ExternalSyntheticLambda7 r0 = new org.telegram.ui.CacheControlActivity$$ExternalSyntheticLambda7
            r0.<init>(r1, r2)
        L_0x0282:
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
            return
        L_0x0287:
            org.telegram.ui.CacheControlActivity$$ExternalSyntheticLambda7 r3 = new org.telegram.ui.CacheControlActivity$$ExternalSyntheticLambda7
            r3.<init>(r1, r2)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r3)
            goto L_0x0291
        L_0x0290:
            throw r0
        L_0x0291:
            goto L_0x0290
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.CacheControlActivity.m1469lambda$clearDatabase$8$orgtelegramuiCacheControlActivity(org.telegram.ui.ActionBar.AlertDialog):void");
    }

    /* renamed from: lambda$clearDatabase$7$org-telegram-ui-CacheControlActivity  reason: not valid java name */
    public /* synthetic */ void m1468lambda$clearDatabase$7$orgtelegramuiCacheControlActivity(AlertDialog progressDialog) {
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        if (this.listAdapter != null) {
            this.databaseSize = MessagesStorage.getInstance(this.currentAccount).getDatabaseSize();
            this.listAdapter.notifyDataSetChanged();
        }
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didClearDatabase, new Object[0]);
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
                        textCell.setTextAndValue(LocaleController.getString("MigrateOldFolder", NUM), (String) null, false);
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
        ThemeDescription.ThemeDescriptionDelegate deldegagte = new CacheControlActivity$$ExternalSyntheticLambda10(this);
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

    /* renamed from: lambda$getThemeDescriptions$10$org-telegram-ui-CacheControlActivity  reason: not valid java name */
    public /* synthetic */ void m1474x2e159a88() {
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
