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
import org.telegram.ui.Components.UndoView;
import org.telegram.ui.StorageDiagramView;

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
        this.databaseSize = MessagesStorage.getInstance(this.currentAccount).getDatabaseSize();
        Utilities.globalQueue.postRunnable(new Runnable() {
            public final void run() {
                CacheControlActivity.this.lambda$onFragmentCreate$1$CacheControlActivity();
            }
        });
        this.fragmentCreateTime = System.currentTimeMillis();
        return true;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onFragmentCreate$1 */
    public /* synthetic */ void lambda$onFragmentCreate$1$CacheControlActivity() {
        File file;
        long j;
        long j2;
        long j3;
        int i = 0;
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
                                AndroidUtilities.runOnUIThread(new Runnable() {
                                    public final void run() {
                                        CacheControlActivity.this.lambda$null$0$CacheControlActivity();
                                    }
                                });
                            }
                        }
                    }
                }
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$0 */
    public /* synthetic */ void lambda$null$0$CacheControlActivity() {
        this.calculating = false;
        updateStorageUsageRow();
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
        alertDialog.setCanCacnel(false);
        alertDialog.showDelayed(500);
        Utilities.globalQueue.postRunnable(new Runnable(alertDialog) {
            public final /* synthetic */ AlertDialog f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                CacheControlActivity.this.lambda$cleanupFolders$3$CacheControlActivity(this.f$1);
            }
        });
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x005d  */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x00e1 A[SYNTHETIC] */
    /* renamed from: lambda$cleanupFolders$3 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$cleanupFolders$3$CacheControlActivity(org.telegram.ui.ActionBar.AlertDialog r17) {
        /*
            r16 = this;
            r6 = r16
            r0 = 0
            r1 = 0
            r4 = r1
            r1 = 0
            r2 = 0
        L_0x0008:
            r3 = 7
            if (r1 >= r3) goto L_0x00e5
            org.telegram.ui.StorageDiagramView$ClearViewData[] r3 = r6.clearViewData
            r7 = r3[r1]
            r8 = 1
            if (r7 == 0) goto L_0x00e1
            r3 = r3[r1]
            boolean r3 = r3.clear
            if (r3 != 0) goto L_0x001a
            goto L_0x00e1
        L_0x001a:
            r3 = -1
            r7 = 100
            r9 = 2
            r10 = 4
            r11 = 3
            if (r1 != 0) goto L_0x0028
            long r12 = r6.photoSize
            long r4 = r4 + r12
            r12 = 0
        L_0x0026:
            r13 = 0
            goto L_0x0059
        L_0x0028:
            if (r1 != r8) goto L_0x002f
            long r12 = r6.videoSize
            long r4 = r4 + r12
            r12 = 2
            goto L_0x0026
        L_0x002f:
            if (r1 != r9) goto L_0x0037
            long r12 = r6.documentsSize
            long r4 = r4 + r12
            r12 = 3
            r13 = 1
            goto L_0x0059
        L_0x0037:
            if (r1 != r11) goto L_0x003f
            long r12 = r6.musicSize
            long r4 = r4 + r12
            r12 = 3
            r13 = 2
            goto L_0x0059
        L_0x003f:
            if (r1 != r10) goto L_0x0046
            long r12 = r6.audioSize
            long r4 = r4 + r12
            r12 = 1
            goto L_0x0026
        L_0x0046:
            r12 = 5
            if (r1 != r12) goto L_0x004f
            long r12 = r6.stickersSize
            long r4 = r4 + r12
            r12 = 100
            goto L_0x0026
        L_0x004f:
            r12 = 6
            if (r1 != r12) goto L_0x0057
            long r12 = r6.cacheSize
            long r4 = r4 + r12
            r12 = 4
            goto L_0x0026
        L_0x0057:
            r12 = -1
            goto L_0x0026
        L_0x0059:
            if (r12 != r3) goto L_0x005d
            goto L_0x00e1
        L_0x005d:
            java.lang.String r3 = "acache"
            if (r12 != r7) goto L_0x006b
            java.io.File r14 = new java.io.File
            java.io.File r15 = org.telegram.messenger.FileLoader.checkDirectory(r10)
            r14.<init>(r15, r3)
            goto L_0x006f
        L_0x006b:
            java.io.File r14 = org.telegram.messenger.FileLoader.checkDirectory(r12)
        L_0x006f:
            if (r14 == 0) goto L_0x007d
            java.lang.String r14 = r14.getAbsolutePath()
            r7 = 9223372036854775807(0x7fffffffffffffff, double:NaN)
            org.telegram.messenger.Utilities.clearDir(r14, r13, r7, r0)
        L_0x007d:
            if (r12 != r10) goto L_0x008b
            java.io.File r2 = org.telegram.messenger.FileLoader.checkDirectory(r10)
            long r2 = r6.getDirectorySize(r2, r13)
            r6.cacheSize = r2
        L_0x0089:
            r2 = 1
            goto L_0x00e1
        L_0x008b:
            r7 = 1
            if (r12 != r7) goto L_0x0099
            java.io.File r3 = org.telegram.messenger.FileLoader.checkDirectory(r7)
            long r7 = r6.getDirectorySize(r3, r13)
            r6.audioSize = r7
            goto L_0x00e1
        L_0x0099:
            if (r12 != r11) goto L_0x00b3
            if (r13 != r7) goto L_0x00a8
            java.io.File r3 = org.telegram.messenger.FileLoader.checkDirectory(r11)
            long r7 = r6.getDirectorySize(r3, r13)
            r6.documentsSize = r7
            goto L_0x00e1
        L_0x00a8:
            java.io.File r3 = org.telegram.messenger.FileLoader.checkDirectory(r11)
            long r7 = r6.getDirectorySize(r3, r13)
            r6.musicSize = r7
            goto L_0x00e1
        L_0x00b3:
            if (r12 != 0) goto L_0x00c0
            java.io.File r2 = org.telegram.messenger.FileLoader.checkDirectory(r0)
            long r2 = r6.getDirectorySize(r2, r13)
            r6.photoSize = r2
            goto L_0x0089
        L_0x00c0:
            if (r12 != r9) goto L_0x00cd
            java.io.File r3 = org.telegram.messenger.FileLoader.checkDirectory(r9)
            long r7 = r6.getDirectorySize(r3, r13)
            r6.videoSize = r7
            goto L_0x00e1
        L_0x00cd:
            r8 = 100
            if (r12 != r8) goto L_0x00e1
            java.io.File r2 = new java.io.File
            java.io.File r8 = org.telegram.messenger.FileLoader.checkDirectory(r10)
            r2.<init>(r8, r3)
            long r2 = r6.getDirectorySize(r2, r13)
            r6.stickersSize = r2
            goto L_0x0089
        L_0x00e1:
            int r1 = r1 + 1
            goto L_0x0008
        L_0x00e5:
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
            if (r0 < r3) goto L_0x0113
            long r7 = r1.getBlockSizeLong()
            goto L_0x0118
        L_0x0113:
            int r7 = r1.getBlockSize()
            long r7 = (long) r7
        L_0x0118:
            if (r0 < r3) goto L_0x011f
            long r9 = r1.getAvailableBlocksLong()
            goto L_0x0124
        L_0x011f:
            int r9 = r1.getAvailableBlocks()
            long r9 = (long) r9
        L_0x0124:
            if (r0 < r3) goto L_0x012b
            long r0 = r1.getBlockCountLong()
            goto L_0x0130
        L_0x012b:
            int r0 = r1.getBlockCount()
            long r0 = (long) r0
        L_0x0130:
            long r0 = r0 * r7
            r6.totalDeviceSize = r0
            long r9 = r9 * r7
            r6.totalDeviceFreeSize = r9
            org.telegram.ui.-$$Lambda$CacheControlActivity$PihvShX3GD1hRhfBIqWIR5RSh38 r7 = new org.telegram.ui.-$$Lambda$CacheControlActivity$PihvShX3GD1hRhfBIqWIR5RSh38
            r0 = r7
            r1 = r16
            r3 = r17
            r0.<init>(r2, r3, r4)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r7)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.CacheControlActivity.lambda$cleanupFolders$3$CacheControlActivity(org.telegram.ui.ActionBar.AlertDialog):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$2 */
    public /* synthetic */ void lambda$null$2$CacheControlActivity(boolean z, AlertDialog alertDialog, long j) {
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
        this.cacheRemovedTooltip.setInfoText(LocaleController.formatString("CacheWasCleared", NUM, AndroidUtilities.formatFileSize(j)));
        this.cacheRemovedTooltip.showWithAction(0, 19, (Runnable) null, (Runnable) null);
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
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener(context) {
            public final /* synthetic */ Context f$1;

            {
                this.f$1 = r2;
            }

            public final void onItemClick(View view, int i) {
                CacheControlActivity.this.lambda$createView$6$CacheControlActivity(this.f$1, view, i);
            }
        });
        UndoView undoView = new UndoView(context);
        this.cacheRemovedTooltip = undoView;
        frameLayout2.addView(undoView, LayoutHelper.createFrame(-1, -2.0f, 83, 8.0f, 0.0f, 8.0f, 8.0f));
        return this.fragmentView;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$6 */
    public /* synthetic */ void lambda$createView$6$CacheControlActivity(Context context, View view, int i) {
        long j;
        String str;
        String str2;
        Context context2 = context;
        int i2 = i;
        if (getParentActivity() != null) {
            if (i2 == this.databaseRow) {
                clearDatabase();
            } else if (i2 == this.storageUsageRow) {
                long j2 = 0;
                if (this.totalSize > 0 && getParentActivity() != null) {
                    AnonymousClass2 r2 = new BottomSheet(getParentActivity(), false) {
                        /* access modifiers changed from: protected */
                        public boolean canDismissWithSwipe() {
                            return false;
                        }
                    };
                    this.bottomSheet = r2;
                    r2.setAllowNestedScroll(true);
                    this.bottomSheet.setApplyBottomPadding(false);
                    LinearLayout linearLayout = new LinearLayout(getParentActivity());
                    this.bottomSheetView = linearLayout;
                    linearLayout.setOrientation(1);
                    StorageDiagramView storageDiagramView = new StorageDiagramView(context2);
                    linearLayout.addView(storageDiagramView, LayoutHelper.createLinear(-2, -2, 1, 0, 16, 0, 16));
                    int i3 = 0;
                    CheckBoxCell checkBoxCell = null;
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
                        } else if (i3 == 6) {
                            j = this.cacheSize;
                            str2 = LocaleController.getString("LocalCache", NUM);
                            str = "statisticChartLine_lightblue";
                        } else {
                            j = j2;
                            str2 = null;
                            str = null;
                        }
                        if (j > j2) {
                            this.clearViewData[i3] = new StorageDiagramView.ClearViewData(storageDiagramView);
                            StorageDiagramView.ClearViewData[] clearViewDataArr = this.clearViewData;
                            clearViewDataArr[i3].size = j;
                            clearViewDataArr[i3].color = str;
                            checkBoxCell = new CheckBoxCell(getParentActivity(), 4, 21);
                            checkBoxCell.setTag(Integer.valueOf(i3));
                            checkBoxCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                            linearLayout.addView(checkBoxCell, LayoutHelper.createLinear(-1, 50));
                            checkBoxCell.setText(str2, AndroidUtilities.formatFileSize(j), true, true);
                            checkBoxCell.setTextColor(Theme.getColor("dialogTextBlack"));
                            checkBoxCell.setCheckBoxColor(str, "windowBackgroundWhiteGrayIcon", "checkboxCheck");
                            checkBoxCell.setOnClickListener(new View.OnClickListener() {
                                public final void onClick(View view) {
                                    CacheControlActivity.this.lambda$null$4$CacheControlActivity(view);
                                }
                            });
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
                    bottomSheetCell.getTextView().setOnClickListener(new View.OnClickListener() {
                        public final void onClick(View view) {
                            CacheControlActivity.this.lambda$null$5$CacheControlActivity(view);
                        }
                    });
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
    /* renamed from: lambda$null$4 */
    public /* synthetic */ void lambda$null$4$CacheControlActivity(View view) {
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
    /* renamed from: lambda$null$5 */
    public /* synthetic */ void lambda$null$5$CacheControlActivity(View view) {
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

    private void clearDatabase() {
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        builder.setTitle(LocaleController.getString("LocalDatabaseClearTextTitle", NUM));
        builder.setMessage(LocaleController.getString("LocalDatabaseClearText", NUM));
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        builder.setPositiveButton(LocaleController.getString("CacheClear", NUM), new DialogInterface.OnClickListener() {
            public final void onClick(DialogInterface dialogInterface, int i) {
                CacheControlActivity.this.lambda$clearDatabase$9$CacheControlActivity(dialogInterface, i);
            }
        });
        AlertDialog create = builder.create();
        showDialog(create);
        TextView textView = (TextView) create.getButton(-1);
        if (textView != null) {
            textView.setTextColor(Theme.getColor("dialogTextRed2"));
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$clearDatabase$9 */
    public /* synthetic */ void lambda$clearDatabase$9$CacheControlActivity(DialogInterface dialogInterface, int i) {
        if (getParentActivity() != null) {
            AlertDialog alertDialog = new AlertDialog(getParentActivity(), 3);
            alertDialog.setCanCacnel(false);
            alertDialog.showDelayed(500);
            MessagesController.getInstance(this.currentAccount).clearQueryTime();
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable(alertDialog) {
                public final /* synthetic */ AlertDialog f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    CacheControlActivity.this.lambda$null$8$CacheControlActivity(this.f$1);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:50:0x0234, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:51:0x0235, code lost:
        r2 = r19;
     */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x01dd A[Catch:{ Exception -> 0x0238, all -> 0x0234 }] */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x01e7 A[Catch:{ Exception -> 0x0238, all -> 0x0234 }] */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x0234 A[ExcHandler: all (th java.lang.Throwable), Splitter:B:39:0x0112] */
    /* renamed from: lambda$null$8 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$null$8$CacheControlActivity(org.telegram.ui.ActionBar.AlertDialog r19) {
        /*
            r18 = this;
            r1 = r18
            r2 = r19
            java.lang.String r3 = " AND mid != "
            int r0 = r1.currentAccount     // Catch:{ Exception -> 0x023e }
            org.telegram.messenger.MessagesStorage r0 = org.telegram.messenger.MessagesStorage.getInstance(r0)     // Catch:{ Exception -> 0x023e }
            org.telegram.SQLite.SQLiteDatabase r4 = r0.getDatabase()     // Catch:{ Exception -> 0x023e }
            java.util.ArrayList r5 = new java.util.ArrayList     // Catch:{ Exception -> 0x023e }
            r5.<init>()     // Catch:{ Exception -> 0x023e }
            java.lang.String r0 = "SELECT did FROM dialogs WHERE 1"
            r6 = 0
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ Exception -> 0x023e }
            org.telegram.SQLite.SQLiteCursor r0 = r4.queryFinalized(r0, r7)     // Catch:{ Exception -> 0x023e }
        L_0x001e:
            boolean r7 = r0.next()     // Catch:{ Exception -> 0x023e }
            r8 = 1
            if (r7 == 0) goto L_0x003b
            long r9 = r0.longValue(r6)     // Catch:{ Exception -> 0x023e }
            int r7 = (int) r9     // Catch:{ Exception -> 0x023e }
            r11 = 32
            long r11 = r9 >> r11
            int r12 = (int) r11     // Catch:{ Exception -> 0x023e }
            if (r7 == 0) goto L_0x001e
            if (r12 == r8) goto L_0x001e
            java.lang.Long r7 = java.lang.Long.valueOf(r9)     // Catch:{ Exception -> 0x023e }
            r5.add(r7)     // Catch:{ Exception -> 0x023e }
            goto L_0x001e
        L_0x003b:
            r0.dispose()     // Catch:{ Exception -> 0x023e }
            java.lang.String r0 = "REPLACE INTO messages_holes VALUES(?, ?, ?)"
            org.telegram.SQLite.SQLitePreparedStatement r7 = r4.executeFast(r0)     // Catch:{ Exception -> 0x023e }
            java.lang.String r0 = "REPLACE INTO media_holes_v2 VALUES(?, ?, ?, ?)"
            org.telegram.SQLite.SQLitePreparedStatement r9 = r4.executeFast(r0)     // Catch:{ Exception -> 0x023e }
            r4.beginTransaction()     // Catch:{ Exception -> 0x023e }
            r10 = 0
        L_0x004e:
            int r0 = r5.size()     // Catch:{ Exception -> 0x023e }
            if (r10 >= r0) goto L_0x01fb
            java.lang.Object r0 = r5.get(r10)     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            r11 = r0
            java.lang.Long r11 = (java.lang.Long) r11     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            r0.<init>()     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            java.lang.String r12 = "SELECT COUNT(mid) FROM messages WHERE uid = "
            r0.append(r12)     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            r0.append(r11)     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            java.lang.Object[] r12 = new java.lang.Object[r6]     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            org.telegram.SQLite.SQLiteCursor r0 = r4.queryFinalized(r0, r12)     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            boolean r12 = r0.next()     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            if (r12 == 0) goto L_0x007d
            int r12 = r0.intValue(r6)     // Catch:{ Exception -> 0x023e }
            goto L_0x007e
        L_0x007d:
            r12 = 0
        L_0x007e:
            r0.dispose()     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            r0 = 2
            if (r12 > r0) goto L_0x0089
            r17 = r5
            r2 = r7
            goto L_0x01f0
        L_0x0089:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            r0.<init>()     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            java.lang.String r12 = "SELECT last_mid_i, last_mid FROM dialogs WHERE did = "
            r0.append(r12)     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            r0.append(r11)     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            java.lang.Object[] r12 = new java.lang.Object[r6]     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            org.telegram.SQLite.SQLiteCursor r12 = r4.queryFinalized(r0, r12)     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            boolean r0 = r12.next()     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            if (r0 == 0) goto L_0x01ea
            long r13 = r12.longValue(r6)     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            r16 = r7
            long r6 = r12.longValue(r8)     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            r0.<init>()     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            java.lang.String r8 = "SELECT data FROM messages WHERE uid = "
            r0.append(r8)     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            r0.append(r11)     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            java.lang.String r8 = " AND mid IN ("
            r0.append(r8)     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            r0.append(r13)     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            java.lang.String r8 = ","
            r0.append(r8)     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            r0.append(r6)     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            java.lang.String r8 = ")"
            r0.append(r8)     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            r8 = 0
            java.lang.Object[] r15 = new java.lang.Object[r8]     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            org.telegram.SQLite.SQLiteCursor r15 = r4.queryFinalized(r0, r15)     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            r8 = -1
        L_0x00de:
            boolean r0 = r15.next()     // Catch:{ Exception -> 0x010f, all -> 0x0234 }
            if (r0 == 0) goto L_0x010c
            r17 = r5
            r5 = 0
            org.telegram.tgnet.NativeByteBuffer r0 = r15.byteBufferValue(r5)     // Catch:{ Exception -> 0x010a, all -> 0x0234 }
            if (r0 == 0) goto L_0x0105
            int r2 = r0.readInt32(r5)     // Catch:{ Exception -> 0x010a, all -> 0x0234 }
            org.telegram.tgnet.TLRPC$Message r2 = org.telegram.tgnet.TLRPC$Message.TLdeserialize(r0, r2, r5)     // Catch:{ Exception -> 0x010a, all -> 0x0234 }
            int r5 = r1.currentAccount     // Catch:{ Exception -> 0x010a, all -> 0x0234 }
            org.telegram.messenger.UserConfig r5 = org.telegram.messenger.UserConfig.getInstance(r5)     // Catch:{ Exception -> 0x010a, all -> 0x0234 }
            int r5 = r5.clientUserId     // Catch:{ Exception -> 0x010a, all -> 0x0234 }
            r2.readAttachPath(r0, r5)     // Catch:{ Exception -> 0x010a, all -> 0x0234 }
            r0.reuse()     // Catch:{ Exception -> 0x010a, all -> 0x0234 }
            int r8 = r2.id     // Catch:{ Exception -> 0x010a, all -> 0x0234 }
        L_0x0105:
            r2 = r19
            r5 = r17
            goto L_0x00de
        L_0x010a:
            r0 = move-exception
            goto L_0x0112
        L_0x010c:
            r17 = r5
            goto L_0x0115
        L_0x010f:
            r0 = move-exception
            r17 = r5
        L_0x0112:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
        L_0x0115:
            r15.dispose()     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            r0.<init>()     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            java.lang.String r2 = "DELETE FROM messages WHERE uid = "
            r0.append(r2)     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            r0.append(r11)     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            r0.append(r3)     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            r0.append(r13)     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            r0.append(r3)     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            r0.append(r6)     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            org.telegram.SQLite.SQLitePreparedStatement r0 = r4.executeFast(r0)     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            org.telegram.SQLite.SQLitePreparedStatement r0 = r0.stepThis()     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            r0.dispose()     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            r0.<init>()     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            java.lang.String r2 = "DELETE FROM messages_holes WHERE uid = "
            r0.append(r2)     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            r0.append(r11)     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            org.telegram.SQLite.SQLitePreparedStatement r0 = r4.executeFast(r0)     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            org.telegram.SQLite.SQLitePreparedStatement r0 = r0.stepThis()     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            r0.dispose()     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            r0.<init>()     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            java.lang.String r2 = "DELETE FROM bot_keyboard WHERE uid = "
            r0.append(r2)     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            r0.append(r11)     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            org.telegram.SQLite.SQLitePreparedStatement r0 = r4.executeFast(r0)     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            org.telegram.SQLite.SQLitePreparedStatement r0 = r0.stepThis()     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            r0.dispose()     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            r0.<init>()     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            java.lang.String r2 = "DELETE FROM media_counts_v2 WHERE uid = "
            r0.append(r2)     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            r0.append(r11)     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            org.telegram.SQLite.SQLitePreparedStatement r0 = r4.executeFast(r0)     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            org.telegram.SQLite.SQLitePreparedStatement r0 = r0.stepThis()     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            r0.dispose()     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            r0.<init>()     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            java.lang.String r2 = "DELETE FROM media_v2 WHERE uid = "
            r0.append(r2)     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            r0.append(r11)     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            org.telegram.SQLite.SQLitePreparedStatement r0 = r4.executeFast(r0)     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            org.telegram.SQLite.SQLitePreparedStatement r0 = r0.stepThis()     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            r0.dispose()     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            r0.<init>()     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            java.lang.String r2 = "DELETE FROM media_holes_v2 WHERE uid = "
            r0.append(r2)     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            r0.append(r11)     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            org.telegram.SQLite.SQLitePreparedStatement r0 = r4.executeFast(r0)     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            org.telegram.SQLite.SQLitePreparedStatement r0 = r0.stepThis()     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            r0.dispose()     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            int r0 = r1.currentAccount     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            org.telegram.messenger.MediaDataController r0 = org.telegram.messenger.MediaDataController.getInstance(r0)     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            long r5 = r11.longValue()     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            r2 = 0
            r0.clearBotKeyboard(r5, r2)     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            r2 = -1
            if (r8 == r2) goto L_0x01e7
            long r5 = r11.longValue()     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            r2 = r16
            org.telegram.messenger.MessagesStorage.createFirstHoles(r5, r2, r9, r8)     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            goto L_0x01ed
        L_0x01e7:
            r2 = r16
            goto L_0x01ed
        L_0x01ea:
            r17 = r5
            r2 = r7
        L_0x01ed:
            r12.dispose()     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
        L_0x01f0:
            int r10 = r10 + 1
            r7 = r2
            r5 = r17
            r6 = 0
            r8 = 1
            r2 = r19
            goto L_0x004e
        L_0x01fb:
            r2 = r7
            r2.dispose()     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            r9.dispose()     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            r4.commitTransaction()     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            java.lang.String r0 = "PRAGMA journal_size_limit = 0"
            org.telegram.SQLite.SQLitePreparedStatement r0 = r4.executeFast(r0)     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            org.telegram.SQLite.SQLitePreparedStatement r0 = r0.stepThis()     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            r0.dispose()     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            java.lang.String r0 = "VACUUM"
            org.telegram.SQLite.SQLitePreparedStatement r0 = r4.executeFast(r0)     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            org.telegram.SQLite.SQLitePreparedStatement r0 = r0.stepThis()     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            r0.dispose()     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            java.lang.String r0 = "PRAGMA journal_size_limit = -1"
            org.telegram.SQLite.SQLitePreparedStatement r0 = r4.executeFast(r0)     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            org.telegram.SQLite.SQLitePreparedStatement r0 = r0.stepThis()     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            r0.dispose()     // Catch:{ Exception -> 0x0238, all -> 0x0234 }
            org.telegram.ui.-$$Lambda$CacheControlActivity$mlNUfuWbRTrIcFdtv0usqOMuRfs r0 = new org.telegram.ui.-$$Lambda$CacheControlActivity$mlNUfuWbRTrIcFdtv0usqOMuRfs
            r2 = r19
            r0.<init>(r2)
            goto L_0x0247
        L_0x0234:
            r0 = move-exception
            r2 = r19
            goto L_0x024b
        L_0x0238:
            r0 = move-exception
            r2 = r19
            goto L_0x023f
        L_0x023c:
            r0 = move-exception
            goto L_0x024b
        L_0x023e:
            r0 = move-exception
        L_0x023f:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x023c }
            org.telegram.ui.-$$Lambda$CacheControlActivity$mlNUfuWbRTrIcFdtv0usqOMuRfs r0 = new org.telegram.ui.-$$Lambda$CacheControlActivity$mlNUfuWbRTrIcFdtv0usqOMuRfs
            r0.<init>(r2)
        L_0x0247:
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
            return
        L_0x024b:
            org.telegram.ui.-$$Lambda$CacheControlActivity$mlNUfuWbRTrIcFdtv0usqOMuRfs r3 = new org.telegram.ui.-$$Lambda$CacheControlActivity$mlNUfuWbRTrIcFdtv0usqOMuRfs
            r3.<init>(r2)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r3)
            goto L_0x0255
        L_0x0254:
            throw r0
        L_0x0255:
            goto L_0x0254
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.CacheControlActivity.lambda$null$8$CacheControlActivity(org.telegram.ui.ActionBar.AlertDialog):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$7 */
    public /* synthetic */ void lambda$null$7$CacheControlActivity(AlertDialog alertDialog) {
        try {
            alertDialog.dismiss();
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

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            int adapterPosition = viewHolder.getAdapterPosition();
            return adapterPosition == CacheControlActivity.this.databaseRow || (adapterPosition == CacheControlActivity.this.storageUsageRow && CacheControlActivity.this.totalSize > 0 && !CacheControlActivity.this.calculating);
        }

        public int getItemCount() {
            return CacheControlActivity.this.rowCount;
        }

        static /* synthetic */ void lambda$onCreateViewHolder$0(int i) {
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
                slideChooseView2.setCallback($$Lambda$CacheControlActivity$ListAdapter$w8jhfR6WU0k1_4Jjlofrf6Tvvo8.INSTANCE);
                int i2 = SharedConfig.keepMedia;
                slideChooseView2.setOptions(i2 == 3 ? 0 : i2 + 1, LocaleController.formatPluralString("Days", 3), LocaleController.formatPluralString("Weeks", 1), LocaleController.formatPluralString("Months", 1), LocaleController.getString("KeepMediaForever", NUM));
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
        $$Lambda$CacheControlActivity$FH8r25MnnTaFx0MjW715_8N5vaM r7 = new ThemeDescription.ThemeDescriptionDelegate() {
            public final void didSetColor() {
                CacheControlActivity.this.lambda$getThemeDescriptions$10$CacheControlActivity();
            }
        };
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
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "dialogBackground"));
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
    /* renamed from: lambda$getThemeDescriptions$10 */
    public /* synthetic */ void lambda$getThemeDescriptions$10$CacheControlActivity() {
        BottomSheet bottomSheet2 = this.bottomSheet;
        if (bottomSheet2 != null) {
            bottomSheet2.setBackgroundColor(Theme.getColor("dialogBackground"));
        }
        View view = this.actionTextView;
        if (view != null) {
            view.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), Theme.getColor("featuredStickers_addButton"), Theme.getColor("featuredStickers_addButtonPressed")));
        }
    }
}
