package org.telegram.ui;

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
        Utilities.globalQueue.postRunnable(new CacheControlActivity$$ExternalSyntheticLambda3(this));
        this.fragmentCreateTime = System.currentTimeMillis();
        updateRows();
        return true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onFragmentCreate$1() {
        File file;
        long j;
        long j2;
        long j3;
        int i = 0;
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
                    long directorySize3 = getDirectorySize(FileLoader.checkDirectory(3), 1);
                    this.documentsSize = directorySize3;
                    this.documentsSize = directorySize3 + getDirectorySize(FileLoader.checkDirectory(5), 1);
                    if (!this.canceled) {
                        long directorySize4 = getDirectorySize(FileLoader.checkDirectory(3), 2);
                        this.musicSize = directorySize4;
                        this.musicSize = directorySize4 + getDirectorySize(FileLoader.checkDirectory(5), 2);
                        if (!this.canceled) {
                            this.stickersSize = getDirectorySize(new File(FileLoader.checkDirectory(4), "acache"), 0);
                            if (!this.canceled) {
                                long directorySize5 = getDirectorySize(FileLoader.checkDirectory(1), 0);
                                this.audioSize = directorySize5;
                                this.totalSize = this.cacheSize + this.videoSize + directorySize5 + this.photoSize + this.documentsSize + this.musicSize + this.stickersSize;
                                if (Build.VERSION.SDK_INT >= 19) {
                                    ArrayList<File> rootDirs = AndroidUtilities.getRootDirs();
                                    file = rootDirs.get(0);
                                    file.getAbsolutePath();
                                    if (!TextUtils.isEmpty(SharedConfig.storageCacheDir)) {
                                        int size = rootDirs.size();
                                        while (true) {
                                            if (i < size) {
                                                File file2 = rootDirs.get(i);
                                                if (file2.getAbsolutePath().startsWith(SharedConfig.storageCacheDir)) {
                                                    file = file2;
                                                    break;
                                                }
                                                i++;
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
                                        j = statFs.getBlockSizeLong();
                                    } else {
                                        j = (long) statFs.getBlockSize();
                                    }
                                    if (i2 >= 18) {
                                        j2 = statFs.getAvailableBlocksLong();
                                    } else {
                                        j2 = (long) statFs.getAvailableBlocks();
                                    }
                                    if (i2 >= 18) {
                                        j3 = statFs.getBlockCountLong();
                                    } else {
                                        j3 = (long) statFs.getBlockCount();
                                    }
                                    this.totalDeviceSize = j3 * j;
                                    this.totalDeviceFreeSize = j2 * j;
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

    /* access modifiers changed from: private */
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
                changeBounds.setDuration(250);
                changeBounds.excludeTarget(stroageUsageView.legendLayout, true);
                Fade fade = new Fade(1);
                fade.setDuration(290);
                transitionSet.addTransition(new Fade(2).setDuration(250)).addTransition(changeBounds).addTransition(fade);
                transitionSet.setOrdering(0);
                transitionSet.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                TransitionManager.beginDelayedTransition(this.listView, transitionSet);
            }
            stroageUsageView.setStorageUsage(this.calculating, this.databaseSize, this.totalSize, this.totalDeviceFreeSize, this.totalDeviceSize);
            RecyclerView.ViewHolder findViewHolderForAdapterPosition = this.listView.findViewHolderForAdapterPosition(this.storageUsageRow);
            if (findViewHolderForAdapterPosition != null) {
                stroageUsageView.setEnabled(this.listAdapter.isEnabled(findViewHolderForAdapterPosition));
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
        } catch (Exception unused) {
        }
        this.progressDialog = null;
        this.canceled = true;
    }

    private long getDirectorySize(File file, int i) {
        if (file == null || this.canceled) {
            return 0;
        }
        if (file.isDirectory()) {
            return Utilities.getDirSize(file.getAbsolutePath(), i, false);
        }
        if (file.isFile()) {
            return 0 + file.length();
        }
        return 0;
    }

    private void cleanupFolders() {
        AlertDialog alertDialog = new AlertDialog(getParentActivity(), 3);
        alertDialog.setCanCancel(false);
        alertDialog.showDelayed(500);
        Utilities.globalQueue.postRunnable(new CacheControlActivity$$ExternalSyntheticLambda5(this, alertDialog));
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x005d  */
    /* JADX WARNING: Removed duplicated region for block: B:84:0x013b A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$cleanupFolders$3(org.telegram.ui.ActionBar.AlertDialog r18) {
        /*
            r17 = this;
            r6 = r17
            r0 = 0
            r1 = 0
            r4 = r1
            r1 = 0
            r2 = 0
        L_0x0008:
            r3 = 7
            if (r1 >= r3) goto L_0x013f
            org.telegram.ui.Components.StorageDiagramView$ClearViewData[] r3 = r6.clearViewData
            r7 = r3[r1]
            r8 = 1
            if (r7 == 0) goto L_0x013b
            r3 = r3[r1]
            boolean r3 = r3.clear
            if (r3 != 0) goto L_0x001a
            goto L_0x013b
        L_0x001a:
            r3 = -1
            r7 = 5
            r9 = 100
            r10 = 4
            r11 = 2
            r12 = 3
            if (r1 != 0) goto L_0x0029
            long r13 = r6.photoSize
            long r4 = r4 + r13
            r13 = 0
        L_0x0027:
            r14 = 0
            goto L_0x0059
        L_0x0029:
            if (r1 != r8) goto L_0x0030
            long r13 = r6.videoSize
            long r4 = r4 + r13
            r13 = 2
            goto L_0x0027
        L_0x0030:
            if (r1 != r11) goto L_0x0038
            long r13 = r6.documentsSize
            long r4 = r4 + r13
            r13 = 3
            r14 = 1
            goto L_0x0059
        L_0x0038:
            if (r1 != r12) goto L_0x0040
            long r13 = r6.musicSize
            long r4 = r4 + r13
            r13 = 3
            r14 = 2
            goto L_0x0059
        L_0x0040:
            if (r1 != r10) goto L_0x0047
            long r13 = r6.audioSize
            long r4 = r4 + r13
            r13 = 1
            goto L_0x0027
        L_0x0047:
            if (r1 != r7) goto L_0x004f
            long r13 = r6.stickersSize
            long r4 = r4 + r13
            r13 = 100
            goto L_0x0027
        L_0x004f:
            r13 = 6
            if (r1 != r13) goto L_0x0057
            long r13 = r6.cacheSize
            long r4 = r4 + r13
            r13 = 4
            goto L_0x0027
        L_0x0057:
            r13 = -1
            goto L_0x0027
        L_0x0059:
            if (r13 != r3) goto L_0x005d
            goto L_0x013b
        L_0x005d:
            java.lang.String r3 = "acache"
            if (r13 != r9) goto L_0x006b
            java.io.File r15 = new java.io.File
            java.io.File r9 = org.telegram.messenger.FileLoader.checkDirectory(r10)
            r15.<init>(r9, r3)
            goto L_0x006f
        L_0x006b:
            java.io.File r15 = org.telegram.messenger.FileLoader.checkDirectory(r13)
        L_0x006f:
            r8 = 9223372036854775807(0x7fffffffffffffff, double:NaN)
            if (r15 == 0) goto L_0x007d
            java.lang.String r15 = r15.getAbsolutePath()
            org.telegram.messenger.Utilities.clearDir(r15, r14, r8, r0)
        L_0x007d:
            if (r13 == 0) goto L_0x0081
            if (r13 != r11) goto L_0x0095
        L_0x0081:
            if (r13 != 0) goto L_0x0086
            r16 = 100
            goto L_0x0088
        L_0x0086:
            r16 = 101(0x65, float:1.42E-43)
        L_0x0088:
            java.io.File r16 = org.telegram.messenger.FileLoader.checkDirectory(r16)
            if (r16 == 0) goto L_0x0095
            java.lang.String r15 = r16.getAbsolutePath()
            org.telegram.messenger.Utilities.clearDir(r15, r14, r8, r0)
        L_0x0095:
            if (r13 != r12) goto L_0x00a4
            java.io.File r15 = org.telegram.messenger.FileLoader.checkDirectory(r7)
            if (r15 == 0) goto L_0x00a4
            java.lang.String r15 = r15.getAbsolutePath()
            org.telegram.messenger.Utilities.clearDir(r15, r14, r8, r0)
        L_0x00a4:
            if (r13 != r10) goto L_0x00b3
            java.io.File r2 = org.telegram.messenger.FileLoader.checkDirectory(r10)
            long r2 = r6.getDirectorySize(r2, r14)
            r6.cacheSize = r2
        L_0x00b0:
            r2 = 1
            goto L_0x013b
        L_0x00b3:
            r8 = 1
            if (r13 != r8) goto L_0x00c2
            java.io.File r3 = org.telegram.messenger.FileLoader.checkDirectory(r8)
            long r7 = r6.getDirectorySize(r3, r14)
            r6.audioSize = r7
            goto L_0x013b
        L_0x00c2:
            if (r13 != r12) goto L_0x00f2
            if (r14 != r8) goto L_0x00dc
            java.io.File r3 = org.telegram.messenger.FileLoader.checkDirectory(r12)
            long r8 = r6.getDirectorySize(r3, r14)
            r6.documentsSize = r8
            java.io.File r3 = org.telegram.messenger.FileLoader.checkDirectory(r7)
            long r10 = r6.getDirectorySize(r3, r14)
            long r8 = r8 + r10
            r6.documentsSize = r8
            goto L_0x013b
        L_0x00dc:
            java.io.File r3 = org.telegram.messenger.FileLoader.checkDirectory(r12)
            long r8 = r6.getDirectorySize(r3, r14)
            r6.musicSize = r8
            java.io.File r3 = org.telegram.messenger.FileLoader.checkDirectory(r7)
            long r10 = r6.getDirectorySize(r3, r14)
            long r8 = r8 + r10
            r6.musicSize = r8
            goto L_0x013b
        L_0x00f2:
            if (r13 != 0) goto L_0x010c
            java.io.File r2 = org.telegram.messenger.FileLoader.checkDirectory(r0)
            long r2 = r6.getDirectorySize(r2, r14)
            r6.photoSize = r2
            r7 = 100
            java.io.File r7 = org.telegram.messenger.FileLoader.checkDirectory(r7)
            long r9 = r6.getDirectorySize(r7, r14)
            long r2 = r2 + r9
            r6.photoSize = r2
            goto L_0x00b0
        L_0x010c:
            if (r13 != r11) goto L_0x0126
            java.io.File r3 = org.telegram.messenger.FileLoader.checkDirectory(r11)
            long r7 = r6.getDirectorySize(r3, r14)
            r6.videoSize = r7
            r3 = 101(0x65, float:1.42E-43)
            java.io.File r3 = org.telegram.messenger.FileLoader.checkDirectory(r3)
            long r9 = r6.getDirectorySize(r3, r14)
            long r7 = r7 + r9
            r6.videoSize = r7
            goto L_0x013b
        L_0x0126:
            r7 = 100
            if (r13 != r7) goto L_0x013b
            java.io.File r2 = new java.io.File
            java.io.File r7 = org.telegram.messenger.FileLoader.checkDirectory(r10)
            r2.<init>(r7, r3)
            long r2 = r6.getDirectorySize(r2, r14)
            r6.stickersSize = r2
            goto L_0x00b0
        L_0x013b:
            int r1 = r1 + 1
            goto L_0x0008
        L_0x013f:
            long r0 = r6.cacheSize
            long r7 = r6.videoSize
            long r0 = r0 + r7
            long r7 = r6.audioSize
            long r0 = r0 + r7
            long r7 = r6.photoSize
            long r0 = r0 + r7
            long r7 = r6.documentsSize
            long r0 = r0 + r7
            long r7 = r6.musicSize
            long r0 = r0 + r7
            long r7 = r6.stickersSize
            long r0 = r0 + r7
            r6.totalSize = r0
            java.io.File r0 = android.os.Environment.getDataDirectory()
            android.os.StatFs r1 = new android.os.StatFs
            java.lang.String r0 = r0.getPath()
            r1.<init>(r0)
            int r0 = android.os.Build.VERSION.SDK_INT
            r3 = 18
            if (r0 < r3) goto L_0x016d
            long r7 = r1.getBlockSizeLong()
            goto L_0x0172
        L_0x016d:
            int r7 = r1.getBlockSize()
            long r7 = (long) r7
        L_0x0172:
            if (r0 < r3) goto L_0x0179
            long r9 = r1.getAvailableBlocksLong()
            goto L_0x017e
        L_0x0179:
            int r9 = r1.getAvailableBlocks()
            long r9 = (long) r9
        L_0x017e:
            if (r0 < r3) goto L_0x0185
            long r0 = r1.getBlockCountLong()
            goto L_0x018a
        L_0x0185:
            int r0 = r1.getBlockCount()
            long r0 = (long) r0
        L_0x018a:
            long r0 = r0 * r7
            r6.totalDeviceSize = r0
            long r9 = r9 * r7
            r6.totalDeviceFreeSize = r9
            int r0 = r6.currentAccount
            org.telegram.messenger.FileLoader r0 = org.telegram.messenger.FileLoader.getInstance(r0)
            r0.checkCurrentDownloadsFiles()
            org.telegram.ui.CacheControlActivity$$ExternalSyntheticLambda6 r7 = new org.telegram.ui.CacheControlActivity$$ExternalSyntheticLambda6
            r0 = r7
            r1 = r17
            r3 = r18
            r0.<init>(r1, r2, r3, r4)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r7)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.CacheControlActivity.lambda$cleanupFolders$3(org.telegram.ui.ActionBar.AlertDialog):void");
    }

    /* access modifiers changed from: private */
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
            FileLog.e((Throwable) e);
        }
        getMediaDataController().ringtoneDataStore.checkRingtoneSoundsLoaded();
        this.cacheRemovedTooltip.setInfoText(LocaleController.formatString("CacheWasCleared", NUM, AndroidUtilities.formatFileSize(j)));
        this.cacheRemovedTooltip.showWithAction(0, 19, (Runnable) null, (Runnable) null);
        MediaDataController.getInstance(this.currentAccount).chekAllMedia(true);
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("StorageUsage", NUM));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
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
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new CacheControlActivity$$ExternalSyntheticLambda8(this, context));
        UndoView undoView = new UndoView(context);
        this.cacheRemovedTooltip = undoView;
        frameLayout2.addView(undoView, LayoutHelper.createFrame(-1, -2.0f, 83, 8.0f, 0.0f, 8.0f, 8.0f));
        return this.fragmentView;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$6(Context context, View view, int i) {
        long j;
        String str;
        String str2;
        Context context2 = context;
        int i2 = i;
        if (getParentActivity() != null) {
            if (((long) i2) == this.migrateOldFolderRow) {
                if (Build.VERSION.SDK_INT >= 30) {
                    migrateOldFolder();
                }
            } else if (i2 == this.databaseRow) {
                clearDatabase();
            } else if (i2 == this.storageUsageRow) {
                long j2 = 0;
                if (this.totalSize > 0 && getParentActivity() != null) {
                    AnonymousClass2 r2 = new BottomSheet(this, getParentActivity(), false) {
                        /* access modifiers changed from: protected */
                        public boolean canDismissWithSwipe() {
                            return false;
                        }
                    };
                    this.bottomSheet = r2;
                    r2.fixNavigationBar();
                    this.bottomSheet.setAllowNestedScroll(true);
                    this.bottomSheet.setApplyBottomPadding(false);
                    LinearLayout linearLayout = new LinearLayout(getParentActivity());
                    this.bottomSheetView = linearLayout;
                    linearLayout.setOrientation(1);
                    StorageDiagramView storageDiagramView = new StorageDiagramView(context2);
                    linearLayout.addView(storageDiagramView, LayoutHelper.createLinear(-2, -2, 1, 0, 16, 0, 16));
                    CheckBoxCell checkBoxCell = null;
                    int i3 = 0;
                    while (i3 < 7) {
                        if (i3 == 0) {
                            j = this.photoSize;
                            str2 = LocaleController.getString("LocalPhotoCache", NUM);
                            str = "statisticChartLine_blue";
                        } else if (i3 == 1) {
                            j = this.videoSize;
                            str2 = LocaleController.getString("LocalVideoCache", NUM);
                            str = "statisticChartLine_golden";
                        } else if (i3 == 2) {
                            j = this.documentsSize;
                            str2 = LocaleController.getString("LocalDocumentCache", NUM);
                            str = "statisticChartLine_green";
                        } else if (i3 == 3) {
                            j = this.musicSize;
                            str2 = LocaleController.getString("LocalMusicCache", NUM);
                            str = "statisticChartLine_indigo";
                        } else if (i3 == 4) {
                            j = this.audioSize;
                            str2 = LocaleController.getString("LocalAudioCache", NUM);
                            str = "statisticChartLine_red";
                        } else if (i3 == 5) {
                            j = this.stickersSize;
                            str2 = LocaleController.getString("AnimatedStickers", NUM);
                            str = "statisticChartLine_lightgreen";
                        } else {
                            j = this.cacheSize;
                            str2 = LocaleController.getString("LocalCache", NUM);
                            str = "statisticChartLine_lightblue";
                        }
                        if (j > j2) {
                            this.clearViewData[i3] = new StorageDiagramView.ClearViewData(storageDiagramView);
                            StorageDiagramView.ClearViewData[] clearViewDataArr = this.clearViewData;
                            clearViewDataArr[i3].size = j;
                            clearViewDataArr[i3].color = str;
                            checkBoxCell = new CheckBoxCell(getParentActivity(), 4, 21, (Theme.ResourcesProvider) null);
                            checkBoxCell.setTag(Integer.valueOf(i3));
                            checkBoxCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                            linearLayout.addView(checkBoxCell, LayoutHelper.createLinear(-1, 50));
                            checkBoxCell.setText(str2, AndroidUtilities.formatFileSize(j), true, true);
                            checkBoxCell.setTextColor(Theme.getColor("dialogTextBlack"));
                            checkBoxCell.setCheckBoxColor(str, "windowBackgroundWhiteGrayIcon", "checkboxCheck");
                            checkBoxCell.setOnClickListener(new CacheControlActivity$$ExternalSyntheticLambda1(this));
                        } else {
                            this.clearViewData[i3] = null;
                        }
                        i3++;
                        j2 = 0;
                    }
                    if (checkBoxCell != null) {
                        checkBoxCell.setNeedDivider(false);
                    }
                    storageDiagramView.setData(this.clearViewData);
                    BottomSheet.BottomSheetCell bottomSheetCell = new BottomSheet.BottomSheetCell(getParentActivity(), 2);
                    bottomSheetCell.setTextAndIcon((CharSequence) LocaleController.getString("ClearMediaCache", NUM), 0);
                    this.actionTextView = bottomSheetCell.getTextView();
                    bottomSheetCell.getTextView().setOnClickListener(new CacheControlActivity$$ExternalSyntheticLambda2(this));
                    linearLayout.addView(bottomSheetCell, LayoutHelper.createLinear(-1, 50));
                    NestedScrollView nestedScrollView = new NestedScrollView(context2);
                    nestedScrollView.setVerticalScrollBarEnabled(false);
                    nestedScrollView.addView(linearLayout);
                    this.bottomSheet.setCustomView(nestedScrollView);
                    showDialog(this.bottomSheet);
                }
            }
        }
    }

    /* access modifiers changed from: private */
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
        if (i2 != 1 || !this.clearViewData[intValue].clear) {
            StorageDiagramView.ClearViewData[] clearViewDataArr2 = this.clearViewData;
            clearViewDataArr2[intValue].setClear(!clearViewDataArr2[intValue].clear);
            checkBoxCell.setChecked(this.clearViewData[intValue].clear, true);
            return;
        }
        AndroidUtilities.shakeView(checkBoxCell.getCheckBoxView(), 2.0f, 0);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$5(View view) {
        try {
            Dialog dialog = this.visibleDialog;
            if (dialog != null) {
                dialog.dismiss();
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
        AlertDialog create = builder.create();
        showDialog(create);
        TextView textView = (TextView) create.getButton(-1);
        if (textView != null) {
            textView.setTextColor(Theme.getColor("dialogTextRed2"));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$clearDatabase$7(DialogInterface dialogInterface, int i) {
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

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.didClearDatabase) {
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

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            int adapterPosition = viewHolder.getAdapterPosition();
            return ((long) adapterPosition) == CacheControlActivity.this.migrateOldFolderRow || adapterPosition == CacheControlActivity.this.databaseRow || (adapterPosition == CacheControlActivity.this.storageUsageRow && CacheControlActivity.this.totalSize > 0 && !CacheControlActivity.this.calculating);
        }

        public int getItemCount() {
            return CacheControlActivity.this.rowCount;
        }

        /* access modifiers changed from: private */
        public static /* synthetic */ void lambda$onCreateViewHolder$0(int i) {
            if (i == 0) {
                SharedConfig.setKeepMedia(3);
            } else if (i == 1) {
                SharedConfig.setKeepMedia(0);
            } else if (i == 2) {
                SharedConfig.setKeepMedia(1);
            } else if (i == 3) {
                SharedConfig.setKeepMedia(2);
            }
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            SlideChooseView slideChooseView;
            if (i == 0) {
                TextSettingsCell textSettingsCell = new TextSettingsCell(this.mContext);
                textSettingsCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                slideChooseView = textSettingsCell;
            } else if (i == 2) {
                StroageUsageView stroageUsageView = new StroageUsageView(this.mContext);
                stroageUsageView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                slideChooseView = stroageUsageView;
            } else if (i == 3) {
                HeaderCell headerCell = new HeaderCell(this.mContext);
                headerCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                slideChooseView = headerCell;
            } else if (i != 4) {
                view = new TextInfoPrivacyCell(this.mContext);
                return new RecyclerListView.Holder(view);
            } else {
                SlideChooseView slideChooseView2 = new SlideChooseView(this.mContext);
                slideChooseView2.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                MessagesController.getGlobalMainSettings();
                slideChooseView2.setCallback(CacheControlActivity$ListAdapter$$ExternalSyntheticLambda0.INSTANCE);
                int i2 = SharedConfig.keepMedia;
                slideChooseView2.setOptions(i2 == 3 ? 0 : i2 + 1, LocaleController.formatPluralString("Days", 3, new Object[0]), LocaleController.formatPluralString("Weeks", 1, new Object[0]), LocaleController.formatPluralString("Months", 1, new Object[0]), LocaleController.getString("KeepMediaForever", NUM));
                slideChooseView = slideChooseView2;
            }
            view = slideChooseView;
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType == 0) {
                TextSettingsCell textSettingsCell = (TextSettingsCell) viewHolder.itemView;
                if (i == CacheControlActivity.this.databaseRow) {
                    textSettingsCell.setTextAndValue(LocaleController.getString("ClearLocalDatabase", NUM), AndroidUtilities.formatFileSize(CacheControlActivity.this.databaseSize), false);
                } else if (((long) i) == CacheControlActivity.this.migrateOldFolderRow) {
                    textSettingsCell.setTextAndValue(LocaleController.getString("MigrateOldFolder", NUM), (CharSequence) null, false);
                }
            } else if (itemViewType == 1) {
                TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
                if (i == CacheControlActivity.this.databaseInfoRow) {
                    textInfoPrivacyCell.setText(LocaleController.getString("LocalDatabaseInfo", NUM));
                    textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                } else if (i == CacheControlActivity.this.cacheInfoRow) {
                    textInfoPrivacyCell.setText("");
                    textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                } else if (i == CacheControlActivity.this.keepMediaInfoRow) {
                    textInfoPrivacyCell.setText(AndroidUtilities.replaceTags(LocaleController.getString("KeepMediaInfo", NUM)));
                    textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                }
            } else if (itemViewType == 2) {
                ((StroageUsageView) viewHolder.itemView).setStorageUsage(CacheControlActivity.this.calculating, CacheControlActivity.this.databaseSize, CacheControlActivity.this.totalSize, CacheControlActivity.this.totalDeviceFreeSize, CacheControlActivity.this.totalDeviceSize);
            } else if (itemViewType == 3) {
                HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
                if (i == CacheControlActivity.this.keepMediaHeaderRow) {
                    headerCell.setText(LocaleController.getString("KeepMedia", NUM));
                } else if (i == CacheControlActivity.this.deviseStorageHeaderRow) {
                    headerCell.setText(LocaleController.getString("DeviceStorage", NUM));
                }
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
            return i == CacheControlActivity.this.keepMediaChooserRow ? 4 : 0;
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        CacheControlActivity$$ExternalSyntheticLambda7 cacheControlActivity$$ExternalSyntheticLambda7 = new CacheControlActivity$$ExternalSyntheticLambda7(this);
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
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, cacheControlActivity$$ExternalSyntheticLambda7, "dialogBackground"));
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

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$getThemeDescriptions$8() {
        BottomSheet bottomSheet2 = this.bottomSheet;
        if (bottomSheet2 != null) {
            bottomSheet2.setBackgroundColor(Theme.getColor("dialogBackground"));
        }
        View view = this.actionTextView;
        if (view != null) {
            view.setBackground(Theme.AdaptiveRipple.filledRect("featuredStickers_addButton", 4.0f));
        }
    }

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
            if (z && Build.VERSION.SDK_INT >= 30 && (filesMigrationBottomSheet = FilesMigrationService.filesMigrationBottomSheet) != null) {
                filesMigrationBottomSheet.migrateOldFolder();
            }
        }
    }
}
