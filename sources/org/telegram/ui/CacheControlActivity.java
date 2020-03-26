package org.telegram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

public class CacheControlActivity extends BaseFragment {
    private long audioSize = -1;
    /* access modifiers changed from: private */
    public int cacheInfoRow;
    /* access modifiers changed from: private */
    public int cacheRow;
    private long cacheSize = -1;
    /* access modifiers changed from: private */
    public boolean calculating = true;
    private volatile boolean canceled = false;
    private boolean[] clear = new boolean[7];
    /* access modifiers changed from: private */
    public int databaseInfoRow;
    /* access modifiers changed from: private */
    public int databaseRow;
    /* access modifiers changed from: private */
    public long databaseSize = -1;
    private long documentsSize = -1;
    /* access modifiers changed from: private */
    public int keepMediaInfoRow;
    /* access modifiers changed from: private */
    public int keepMediaRow;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private long musicSize = -1;
    private long photoSize = -1;
    /* access modifiers changed from: private */
    public int rowCount;
    private long stickersSize = -1;
    /* access modifiers changed from: private */
    public long totalSize = -1;
    private long videoSize = -1;

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        this.rowCount = 0;
        int i = 0 + 1;
        this.rowCount = i;
        this.keepMediaRow = 0;
        int i2 = i + 1;
        this.rowCount = i2;
        this.keepMediaInfoRow = i;
        int i3 = i2 + 1;
        this.rowCount = i3;
        this.cacheRow = i2;
        int i4 = i3 + 1;
        this.rowCount = i4;
        this.cacheInfoRow = i3;
        int i5 = i4 + 1;
        this.rowCount = i5;
        this.databaseRow = i4;
        this.rowCount = i5 + 1;
        this.databaseInfoRow = i5;
        this.databaseSize = MessagesStorage.getInstance(this.currentAccount).getDatabaseSize();
        Utilities.globalQueue.postRunnable(new Runnable() {
            public final void run() {
                CacheControlActivity.this.lambda$onFragmentCreate$1$CacheControlActivity();
            }
        });
        return true;
    }

    public /* synthetic */ void lambda$onFragmentCreate$1$CacheControlActivity() {
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

    public /* synthetic */ void lambda$null$0$CacheControlActivity() {
        this.calculating = false;
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
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
        alertDialog.show();
        Utilities.globalQueue.postRunnable(new Runnable(alertDialog) {
            private final /* synthetic */ AlertDialog f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                CacheControlActivity.this.lambda$cleanupFolders$3$CacheControlActivity(this.f$1);
            }
        });
    }

    /* JADX WARNING: Removed duplicated region for block: B:25:0x003d  */
    /* JADX WARNING: Removed duplicated region for block: B:57:0x00be A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$cleanupFolders$3$CacheControlActivity(org.telegram.ui.ActionBar.AlertDialog r15) {
        /*
            r14 = this;
            r0 = 0
            r1 = 0
            r2 = 0
        L_0x0003:
            r3 = 7
            if (r1 >= r3) goto L_0x00c2
            boolean[] r3 = r14.clear
            boolean r3 = r3[r1]
            r4 = 1
            if (r3 != 0) goto L_0x000f
            goto L_0x00be
        L_0x000f:
            r3 = -1
            r5 = 100
            r6 = 2
            r7 = 4
            r8 = 3
            if (r1 != 0) goto L_0x001a
            r9 = 0
        L_0x0018:
            r10 = 0
            goto L_0x0039
        L_0x001a:
            if (r1 != r4) goto L_0x001e
            r9 = 2
            goto L_0x0018
        L_0x001e:
            if (r1 != r6) goto L_0x0023
            r9 = 3
            r10 = 1
            goto L_0x0039
        L_0x0023:
            if (r1 != r8) goto L_0x0028
            r9 = 3
            r10 = 2
            goto L_0x0039
        L_0x0028:
            if (r1 != r7) goto L_0x002c
            r9 = 1
            goto L_0x0018
        L_0x002c:
            r9 = 5
            if (r1 != r9) goto L_0x0032
            r9 = 100
            goto L_0x0018
        L_0x0032:
            r9 = 6
            if (r1 != r9) goto L_0x0037
            r9 = 4
            goto L_0x0018
        L_0x0037:
            r9 = -1
            goto L_0x0018
        L_0x0039:
            if (r9 != r3) goto L_0x003d
            goto L_0x00be
        L_0x003d:
            java.lang.String r3 = "acache"
            if (r9 != r5) goto L_0x004b
            java.io.File r11 = new java.io.File
            java.io.File r12 = org.telegram.messenger.FileLoader.checkDirectory(r7)
            r11.<init>(r12, r3)
            goto L_0x004f
        L_0x004b:
            java.io.File r11 = org.telegram.messenger.FileLoader.checkDirectory(r9)
        L_0x004f:
            if (r11 == 0) goto L_0x005d
            java.lang.String r11 = r11.getAbsolutePath()
            r12 = 9223372036854775807(0x7fffffffffffffff, double:NaN)
            org.telegram.messenger.Utilities.clearDir(r11, r10, r12, r0)
        L_0x005d:
            if (r9 != r7) goto L_0x006b
            java.io.File r2 = org.telegram.messenger.FileLoader.checkDirectory(r7)
            long r2 = r14.getDirectorySize(r2, r10)
            r14.cacheSize = r2
        L_0x0069:
            r2 = 1
            goto L_0x00be
        L_0x006b:
            if (r9 != r4) goto L_0x0078
            java.io.File r3 = org.telegram.messenger.FileLoader.checkDirectory(r4)
            long r3 = r14.getDirectorySize(r3, r10)
            r14.audioSize = r3
            goto L_0x00be
        L_0x0078:
            if (r9 != r8) goto L_0x0092
            if (r10 != r4) goto L_0x0087
            java.io.File r3 = org.telegram.messenger.FileLoader.checkDirectory(r8)
            long r3 = r14.getDirectorySize(r3, r10)
            r14.documentsSize = r3
            goto L_0x00be
        L_0x0087:
            java.io.File r3 = org.telegram.messenger.FileLoader.checkDirectory(r8)
            long r3 = r14.getDirectorySize(r3, r10)
            r14.musicSize = r3
            goto L_0x00be
        L_0x0092:
            if (r9 != 0) goto L_0x009f
            java.io.File r2 = org.telegram.messenger.FileLoader.checkDirectory(r0)
            long r2 = r14.getDirectorySize(r2, r10)
            r14.photoSize = r2
            goto L_0x0069
        L_0x009f:
            if (r9 != r6) goto L_0x00ac
            java.io.File r3 = org.telegram.messenger.FileLoader.checkDirectory(r6)
            long r3 = r14.getDirectorySize(r3, r10)
            r14.videoSize = r3
            goto L_0x00be
        L_0x00ac:
            if (r9 != r5) goto L_0x00be
            java.io.File r2 = new java.io.File
            java.io.File r5 = org.telegram.messenger.FileLoader.checkDirectory(r7)
            r2.<init>(r5, r3)
            long r2 = r14.getDirectorySize(r2, r10)
            r14.stickersSize = r2
            goto L_0x0069
        L_0x00be:
            int r1 = r1 + 1
            goto L_0x0003
        L_0x00c2:
            long r0 = r14.cacheSize
            long r3 = r14.videoSize
            long r0 = r0 + r3
            long r3 = r14.audioSize
            long r0 = r0 + r3
            long r3 = r14.photoSize
            long r0 = r0 + r3
            long r3 = r14.documentsSize
            long r0 = r0 + r3
            long r3 = r14.musicSize
            long r0 = r0 + r3
            long r3 = r14.stickersSize
            long r0 = r0 + r3
            r14.totalSize = r0
            org.telegram.ui.-$$Lambda$CacheControlActivity$Lvpblwm67qF5Tz21D4W9HaTE1WA r0 = new org.telegram.ui.-$$Lambda$CacheControlActivity$Lvpblwm67qF5Tz21D4W9HaTE1WA
            r0.<init>(r2, r15)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.CacheControlActivity.lambda$cleanupFolders$3$CacheControlActivity(org.telegram.ui.ActionBar.AlertDialog):void");
    }

    public /* synthetic */ void lambda$null$2$CacheControlActivity(boolean z, AlertDialog alertDialog) {
        if (z) {
            ImageLoader.getInstance().clearMemory();
        }
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
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
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        frameLayout2.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                CacheControlActivity.this.lambda$createView$10$CacheControlActivity(view, i);
            }
        });
        return this.fragmentView;
    }

    /* JADX WARNING: Removed duplicated region for block: B:40:0x014e  */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x018d  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$createView$10$CacheControlActivity(android.view.View r17, int r18) {
        /*
            r16 = this;
            r0 = r16
            r1 = r18
            android.app.Activity r2 = r16.getParentActivity()
            if (r2 != 0) goto L_0x000b
            return
        L_0x000b:
            int r2 = r0.keepMediaRow
            r3 = 2
            r4 = 4
            r5 = 3
            r6 = 0
            r7 = 1
            if (r1 != r2) goto L_0x0053
            org.telegram.ui.ActionBar.BottomSheet$Builder r1 = new org.telegram.ui.ActionBar.BottomSheet$Builder
            android.app.Activity r2 = r16.getParentActivity()
            r1.<init>(r2)
            java.lang.CharSequence[] r2 = new java.lang.CharSequence[r4]
            java.lang.String r4 = "Days"
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatPluralString(r4, r5)
            r2[r6] = r4
            java.lang.String r4 = "Weeks"
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatPluralString(r4, r7)
            r2[r7] = r4
            java.lang.String r4 = "Months"
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatPluralString(r4, r7)
            r2[r3] = r4
            r3 = 2131625500(0x7f0e061c, float:1.887821E38)
            java.lang.String r4 = "KeepMediaForever"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2[r5] = r3
            org.telegram.ui.-$$Lambda$CacheControlActivity$Z3kCWEOqHs90j5WRi-5avKV3MH4 r3 = new org.telegram.ui.-$$Lambda$CacheControlActivity$Z3kCWEOqHs90j5WRi-5avKV3MH4
            r3.<init>()
            r1.setItems(r2, r3)
            org.telegram.ui.ActionBar.BottomSheet r1 = r1.create()
            r0.showDialog(r1)
            goto L_0x01de
        L_0x0053:
            int r2 = r0.databaseRow
            r8 = 0
            r9 = -1
            if (r1 != r2) goto L_0x00b1
            org.telegram.ui.ActionBar.AlertDialog$Builder r1 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            android.app.Activity r2 = r16.getParentActivity()
            r1.<init>((android.content.Context) r2)
            r2 = 2131625576(0x7f0e0668, float:1.8878364E38)
            java.lang.String r3 = "LocalDatabaseClearTextTitle"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setTitle(r2)
            r2 = 2131625575(0x7f0e0667, float:1.8878362E38)
            java.lang.String r3 = "LocalDatabaseClearText"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setMessage(r2)
            r2 = 2131624483(0x7f0e0223, float:1.8876147E38)
            java.lang.String r3 = "Cancel"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setNegativeButton(r2, r8)
            r2 = 2131624457(0x7f0e0209, float:1.8876094E38)
            java.lang.String r3 = "CacheClear"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            org.telegram.ui.-$$Lambda$CacheControlActivity$OxPoOxpLG_g1G1jasfpkphw5fag r3 = new org.telegram.ui.-$$Lambda$CacheControlActivity$OxPoOxpLG_g1G1jasfpkphw5fag
            r3.<init>()
            r1.setPositiveButton(r2, r3)
            org.telegram.ui.ActionBar.AlertDialog r1 = r1.create()
            r0.showDialog(r1)
            android.view.View r1 = r1.getButton(r9)
            android.widget.TextView r1 = (android.widget.TextView) r1
            if (r1 == 0) goto L_0x01de
            java.lang.String r2 = "dialogTextRed2"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r1.setTextColor(r2)
            goto L_0x01de
        L_0x00b1:
            int r2 = r0.cacheRow
            if (r1 != r2) goto L_0x01de
            long r1 = r0.totalSize
            r10 = 0
            int r12 = (r1 > r10 ? 1 : (r1 == r10 ? 0 : -1))
            if (r12 <= 0) goto L_0x01de
            android.app.Activity r1 = r16.getParentActivity()
            if (r1 != 0) goto L_0x00c5
            goto L_0x01de
        L_0x00c5:
            org.telegram.ui.ActionBar.BottomSheet$Builder r1 = new org.telegram.ui.ActionBar.BottomSheet$Builder
            android.app.Activity r2 = r16.getParentActivity()
            r1.<init>(r2)
            r1.setApplyBottomPadding(r6)
            android.widget.LinearLayout r2 = new android.widget.LinearLayout
            android.app.Activity r12 = r16.getParentActivity()
            r2.<init>(r12)
            r2.setOrientation(r7)
            r12 = 0
        L_0x00de:
            r13 = 7
            r14 = 50
            if (r12 >= r13) goto L_0x0199
            if (r12 != 0) goto L_0x00f2
            long r8 = r0.photoSize
            r15 = 2131625581(0x7f0e066d, float:1.8878374E38)
            java.lang.String r13 = "LocalPhotoCache"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r13, r15)
        L_0x00f0:
            r15 = r13
            goto L_0x014a
        L_0x00f2:
            if (r12 != r7) goto L_0x0100
            long r8 = r0.videoSize
            r13 = 2131625582(0x7f0e066e, float:1.8878376E38)
            java.lang.String r15 = "LocalVideoCache"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r15, r13)
            goto L_0x00f0
        L_0x0100:
            if (r12 != r3) goto L_0x010e
            long r8 = r0.documentsSize
            r13 = 2131625578(0x7f0e066a, float:1.8878368E38)
            java.lang.String r15 = "LocalDocumentCache"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r15, r13)
            goto L_0x00f0
        L_0x010e:
            if (r12 != r5) goto L_0x011c
            long r8 = r0.musicSize
            r13 = 2131625580(0x7f0e066c, float:1.8878372E38)
            java.lang.String r15 = "LocalMusicCache"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r15, r13)
            goto L_0x00f0
        L_0x011c:
            if (r12 != r4) goto L_0x012a
            long r8 = r0.audioSize
            r13 = 2131625572(0x7f0e0664, float:1.8878356E38)
            java.lang.String r15 = "LocalAudioCache"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r15, r13)
            goto L_0x00f0
        L_0x012a:
            r8 = 5
            if (r12 != r8) goto L_0x0139
            long r8 = r0.stickersSize
            r13 = 2131624183(0x7f0e00f7, float:1.8875539E38)
            java.lang.String r15 = "AnimatedStickers"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r15, r13)
            goto L_0x00f0
        L_0x0139:
            r8 = 6
            if (r12 != r8) goto L_0x0148
            long r8 = r0.cacheSize
            r13 = 2131625573(0x7f0e0665, float:1.8878358E38)
            java.lang.String r15 = "LocalCache"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r15, r13)
            goto L_0x00f0
        L_0x0148:
            r8 = r10
            r15 = 0
        L_0x014a:
            int r13 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1))
            if (r13 <= 0) goto L_0x018d
            boolean[] r13 = r0.clear
            r13[r12] = r7
            org.telegram.ui.Cells.CheckBoxCell r13 = new org.telegram.ui.Cells.CheckBoxCell
            android.app.Activity r3 = r16.getParentActivity()
            r4 = 21
            r13.<init>(r3, r7, r4)
            java.lang.Integer r3 = java.lang.Integer.valueOf(r12)
            r13.setTag(r3)
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r6)
            r13.setBackgroundDrawable(r3)
            r3 = -1
            android.widget.LinearLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createLinear(r3, r14)
            r3 = r13
            r2.addView(r3, r4)
            java.lang.String r4 = org.telegram.messenger.AndroidUtilities.formatFileSize(r8)
            r3.setText(r15, r4, r7, r7)
            java.lang.String r4 = "dialogTextBlack"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            r3.setTextColor(r4)
            org.telegram.ui.-$$Lambda$CacheControlActivity$nToR5mmUsDX6DDZcwMPXsRZbyZs r4 = new org.telegram.ui.-$$Lambda$CacheControlActivity$nToR5mmUsDX6DDZcwMPXsRZbyZs
            r4.<init>()
            r3.setOnClickListener(r4)
            goto L_0x0191
        L_0x018d:
            boolean[] r3 = r0.clear
            r3[r12] = r6
        L_0x0191:
            int r12 = r12 + 1
            r3 = 2
            r4 = 4
            r8 = 0
            r9 = -1
            goto L_0x00de
        L_0x0199:
            org.telegram.ui.ActionBar.BottomSheet$BottomSheetCell r3 = new org.telegram.ui.ActionBar.BottomSheet$BottomSheetCell
            android.app.Activity r4 = r16.getParentActivity()
            r3.<init>(r4, r7)
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r6)
            r3.setBackgroundDrawable(r4)
            r4 = 2131624717(0x7f0e030d, float:1.8876622E38)
            java.lang.String r5 = "ClearMediaCache"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            java.lang.String r4 = r4.toUpperCase()
            r3.setTextAndIcon(r4, r6)
            java.lang.String r4 = "windowBackgroundWhiteRedText"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            r3.setTextColor(r4)
            org.telegram.ui.-$$Lambda$CacheControlActivity$xjCzZWiHEv1HCRKFRUbJfrKMn_g r4 = new org.telegram.ui.-$$Lambda$CacheControlActivity$xjCzZWiHEv1HCRKFRUbJfrKMn_g
            r4.<init>()
            r3.setOnClickListener(r4)
            r4 = -1
            android.widget.LinearLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createLinear(r4, r14)
            r2.addView(r3, r4)
            r1.setCustomView(r2)
            org.telegram.ui.ActionBar.BottomSheet r1 = r1.create()
            r0.showDialog(r1)
        L_0x01de:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.CacheControlActivity.lambda$createView$10$CacheControlActivity(android.view.View, int):void");
    }

    public /* synthetic */ void lambda$null$4$CacheControlActivity(DialogInterface dialogInterface, int i) {
        if (i == 0) {
            SharedConfig.setKeepMedia(3);
        } else if (i == 1) {
            SharedConfig.setKeepMedia(0);
        } else if (i == 2) {
            SharedConfig.setKeepMedia(1);
        } else if (i == 3) {
            SharedConfig.setKeepMedia(2);
        }
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
        SharedConfig.checkKeepMedia();
    }

    public /* synthetic */ void lambda$null$7$CacheControlActivity(DialogInterface dialogInterface, int i) {
        if (getParentActivity() != null) {
            AlertDialog alertDialog = new AlertDialog(getParentActivity(), 3);
            alertDialog.setCanCacnel(false);
            alertDialog.show();
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable(alertDialog) {
                private final /* synthetic */ AlertDialog f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    CacheControlActivity.this.lambda$null$6$CacheControlActivity(this.f$1);
                }
            });
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:52:0x0236, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:53:0x0237, code lost:
        r2 = r19;
     */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x01df A[Catch:{ Exception -> 0x023a, all -> 0x0236 }] */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x01e9 A[Catch:{ Exception -> 0x023a, all -> 0x0236 }] */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x0236 A[ExcHandler: all (th java.lang.Throwable), Splitter:B:41:0x0114] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$null$6$CacheControlActivity(org.telegram.ui.ActionBar.AlertDialog r19) {
        /*
            r18 = this;
            r1 = r18
            r2 = r19
            java.lang.String r3 = " AND mid != "
            int r0 = r1.currentAccount     // Catch:{ Exception -> 0x0240 }
            org.telegram.messenger.MessagesStorage r0 = org.telegram.messenger.MessagesStorage.getInstance(r0)     // Catch:{ Exception -> 0x0240 }
            org.telegram.SQLite.SQLiteDatabase r4 = r0.getDatabase()     // Catch:{ Exception -> 0x0240 }
            java.util.ArrayList r5 = new java.util.ArrayList     // Catch:{ Exception -> 0x0240 }
            r5.<init>()     // Catch:{ Exception -> 0x0240 }
            java.lang.String r0 = "SELECT did FROM dialogs WHERE 1"
            r6 = 0
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ Exception -> 0x0240 }
            org.telegram.SQLite.SQLiteCursor r0 = r4.queryFinalized(r0, r7)     // Catch:{ Exception -> 0x0240 }
        L_0x001e:
            boolean r7 = r0.next()     // Catch:{ Exception -> 0x0240 }
            r8 = 1
            if (r7 == 0) goto L_0x003b
            long r9 = r0.longValue(r6)     // Catch:{ Exception -> 0x0240 }
            int r7 = (int) r9     // Catch:{ Exception -> 0x0240 }
            r11 = 32
            long r11 = r9 >> r11
            int r12 = (int) r11     // Catch:{ Exception -> 0x0240 }
            if (r7 == 0) goto L_0x001e
            if (r12 == r8) goto L_0x001e
            java.lang.Long r7 = java.lang.Long.valueOf(r9)     // Catch:{ Exception -> 0x0240 }
            r5.add(r7)     // Catch:{ Exception -> 0x0240 }
            goto L_0x001e
        L_0x003b:
            r0.dispose()     // Catch:{ Exception -> 0x0240 }
            java.lang.String r0 = "REPLACE INTO messages_holes VALUES(?, ?, ?)"
            org.telegram.SQLite.SQLitePreparedStatement r7 = r4.executeFast(r0)     // Catch:{ Exception -> 0x0240 }
            java.lang.String r0 = "REPLACE INTO media_holes_v2 VALUES(?, ?, ?, ?)"
            org.telegram.SQLite.SQLitePreparedStatement r9 = r4.executeFast(r0)     // Catch:{ Exception -> 0x0240 }
            r4.beginTransaction()     // Catch:{ Exception -> 0x0240 }
            r10 = 0
        L_0x004e:
            int r0 = r5.size()     // Catch:{ Exception -> 0x0240 }
            if (r10 >= r0) goto L_0x01fd
            java.lang.Object r0 = r5.get(r10)     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            r11 = r0
            java.lang.Long r11 = (java.lang.Long) r11     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            r0.<init>()     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            java.lang.String r12 = "SELECT COUNT(mid) FROM messages WHERE uid = "
            r0.append(r12)     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            r0.append(r11)     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            java.lang.Object[] r12 = new java.lang.Object[r6]     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            org.telegram.SQLite.SQLiteCursor r0 = r4.queryFinalized(r0, r12)     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            boolean r12 = r0.next()     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            if (r12 == 0) goto L_0x007d
            int r12 = r0.intValue(r6)     // Catch:{ Exception -> 0x0240 }
            goto L_0x007e
        L_0x007d:
            r12 = 0
        L_0x007e:
            r0.dispose()     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            r0 = 2
            if (r12 > r0) goto L_0x0089
            r17 = r5
            r2 = r7
            goto L_0x01f2
        L_0x0089:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            r0.<init>()     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            java.lang.String r12 = "SELECT last_mid_i, last_mid FROM dialogs WHERE did = "
            r0.append(r12)     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            r0.append(r11)     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            java.lang.Object[] r12 = new java.lang.Object[r6]     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            org.telegram.SQLite.SQLiteCursor r12 = r4.queryFinalized(r0, r12)     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            boolean r0 = r12.next()     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            if (r0 == 0) goto L_0x01ec
            long r13 = r12.longValue(r6)     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            r16 = r7
            long r6 = r12.longValue(r8)     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            r0.<init>()     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            java.lang.String r8 = "SELECT data FROM messages WHERE uid = "
            r0.append(r8)     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            r0.append(r11)     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            java.lang.String r8 = " AND mid IN ("
            r0.append(r8)     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            r0.append(r13)     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            java.lang.String r8 = ","
            r0.append(r8)     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            r0.append(r6)     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            java.lang.String r8 = ")"
            r0.append(r8)     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            r8 = 0
            java.lang.Object[] r15 = new java.lang.Object[r8]     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            org.telegram.SQLite.SQLiteCursor r15 = r4.queryFinalized(r0, r15)     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            r8 = -1
        L_0x00de:
            boolean r0 = r15.next()     // Catch:{ Exception -> 0x0111, all -> 0x0236 }
            if (r0 == 0) goto L_0x010e
            r17 = r5
            r5 = 0
            org.telegram.tgnet.NativeByteBuffer r0 = r15.byteBufferValue(r5)     // Catch:{ Exception -> 0x010c, all -> 0x0236 }
            if (r0 == 0) goto L_0x0107
            int r2 = r0.readInt32(r5)     // Catch:{ Exception -> 0x010c, all -> 0x0236 }
            org.telegram.tgnet.TLRPC$Message r2 = org.telegram.tgnet.TLRPC$Message.TLdeserialize(r0, r2, r5)     // Catch:{ Exception -> 0x010c, all -> 0x0236 }
            int r5 = r1.currentAccount     // Catch:{ Exception -> 0x010c, all -> 0x0236 }
            org.telegram.messenger.UserConfig r5 = org.telegram.messenger.UserConfig.getInstance(r5)     // Catch:{ Exception -> 0x010c, all -> 0x0236 }
            int r5 = r5.clientUserId     // Catch:{ Exception -> 0x010c, all -> 0x0236 }
            r2.readAttachPath(r0, r5)     // Catch:{ Exception -> 0x010c, all -> 0x0236 }
            r0.reuse()     // Catch:{ Exception -> 0x010c, all -> 0x0236 }
            if (r2 == 0) goto L_0x0107
            int r8 = r2.id     // Catch:{ Exception -> 0x010c, all -> 0x0236 }
        L_0x0107:
            r2 = r19
            r5 = r17
            goto L_0x00de
        L_0x010c:
            r0 = move-exception
            goto L_0x0114
        L_0x010e:
            r17 = r5
            goto L_0x0117
        L_0x0111:
            r0 = move-exception
            r17 = r5
        L_0x0114:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
        L_0x0117:
            r15.dispose()     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            r0.<init>()     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            java.lang.String r2 = "DELETE FROM messages WHERE uid = "
            r0.append(r2)     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            r0.append(r11)     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            r0.append(r3)     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            r0.append(r13)     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            r0.append(r3)     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            r0.append(r6)     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            org.telegram.SQLite.SQLitePreparedStatement r0 = r4.executeFast(r0)     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            org.telegram.SQLite.SQLitePreparedStatement r0 = r0.stepThis()     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            r0.dispose()     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            r0.<init>()     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            java.lang.String r2 = "DELETE FROM messages_holes WHERE uid = "
            r0.append(r2)     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            r0.append(r11)     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            org.telegram.SQLite.SQLitePreparedStatement r0 = r4.executeFast(r0)     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            org.telegram.SQLite.SQLitePreparedStatement r0 = r0.stepThis()     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            r0.dispose()     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            r0.<init>()     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            java.lang.String r2 = "DELETE FROM bot_keyboard WHERE uid = "
            r0.append(r2)     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            r0.append(r11)     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            org.telegram.SQLite.SQLitePreparedStatement r0 = r4.executeFast(r0)     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            org.telegram.SQLite.SQLitePreparedStatement r0 = r0.stepThis()     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            r0.dispose()     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            r0.<init>()     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            java.lang.String r2 = "DELETE FROM media_counts_v2 WHERE uid = "
            r0.append(r2)     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            r0.append(r11)     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            org.telegram.SQLite.SQLitePreparedStatement r0 = r4.executeFast(r0)     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            org.telegram.SQLite.SQLitePreparedStatement r0 = r0.stepThis()     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            r0.dispose()     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            r0.<init>()     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            java.lang.String r2 = "DELETE FROM media_v2 WHERE uid = "
            r0.append(r2)     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            r0.append(r11)     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            org.telegram.SQLite.SQLitePreparedStatement r0 = r4.executeFast(r0)     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            org.telegram.SQLite.SQLitePreparedStatement r0 = r0.stepThis()     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            r0.dispose()     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            r0.<init>()     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            java.lang.String r2 = "DELETE FROM media_holes_v2 WHERE uid = "
            r0.append(r2)     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            r0.append(r11)     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            org.telegram.SQLite.SQLitePreparedStatement r0 = r4.executeFast(r0)     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            org.telegram.SQLite.SQLitePreparedStatement r0 = r0.stepThis()     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            r0.dispose()     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            int r0 = r1.currentAccount     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            org.telegram.messenger.MediaDataController r0 = org.telegram.messenger.MediaDataController.getInstance(r0)     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            long r5 = r11.longValue()     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            r2 = 0
            r0.clearBotKeyboard(r5, r2)     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            r2 = -1
            if (r8 == r2) goto L_0x01e9
            long r5 = r11.longValue()     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            r2 = r16
            org.telegram.messenger.MessagesStorage.createFirstHoles(r5, r2, r9, r8)     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            goto L_0x01ef
        L_0x01e9:
            r2 = r16
            goto L_0x01ef
        L_0x01ec:
            r17 = r5
            r2 = r7
        L_0x01ef:
            r12.dispose()     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
        L_0x01f2:
            int r10 = r10 + 1
            r7 = r2
            r5 = r17
            r6 = 0
            r8 = 1
            r2 = r19
            goto L_0x004e
        L_0x01fd:
            r2 = r7
            r2.dispose()     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            r9.dispose()     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            r4.commitTransaction()     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            java.lang.String r0 = "PRAGMA journal_size_limit = 0"
            org.telegram.SQLite.SQLitePreparedStatement r0 = r4.executeFast(r0)     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            org.telegram.SQLite.SQLitePreparedStatement r0 = r0.stepThis()     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            r0.dispose()     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            java.lang.String r0 = "VACUUM"
            org.telegram.SQLite.SQLitePreparedStatement r0 = r4.executeFast(r0)     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            org.telegram.SQLite.SQLitePreparedStatement r0 = r0.stepThis()     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            r0.dispose()     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            java.lang.String r0 = "PRAGMA journal_size_limit = -1"
            org.telegram.SQLite.SQLitePreparedStatement r0 = r4.executeFast(r0)     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            org.telegram.SQLite.SQLitePreparedStatement r0 = r0.stepThis()     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            r0.dispose()     // Catch:{ Exception -> 0x023a, all -> 0x0236 }
            org.telegram.ui.-$$Lambda$CacheControlActivity$Vg0mI-k3yibTxFQ18Xoo27lsi5o r0 = new org.telegram.ui.-$$Lambda$CacheControlActivity$Vg0mI-k3yibTxFQ18Xoo27lsi5o
            r2 = r19
            r0.<init>(r2)
            goto L_0x0249
        L_0x0236:
            r0 = move-exception
            r2 = r19
            goto L_0x024d
        L_0x023a:
            r0 = move-exception
            r2 = r19
            goto L_0x0241
        L_0x023e:
            r0 = move-exception
            goto L_0x024d
        L_0x0240:
            r0 = move-exception
        L_0x0241:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x023e }
            org.telegram.ui.-$$Lambda$CacheControlActivity$Vg0mI-k3yibTxFQ18Xoo27lsi5o r0 = new org.telegram.ui.-$$Lambda$CacheControlActivity$Vg0mI-k3yibTxFQ18Xoo27lsi5o
            r0.<init>(r2)
        L_0x0249:
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
            return
        L_0x024d:
            org.telegram.ui.-$$Lambda$CacheControlActivity$Vg0mI-k3yibTxFQ18Xoo27lsi5o r3 = new org.telegram.ui.-$$Lambda$CacheControlActivity$Vg0mI-k3yibTxFQ18Xoo27lsi5o
            r3.<init>(r2)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r3)
            goto L_0x0257
        L_0x0256:
            throw r0
        L_0x0257:
            goto L_0x0256
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.CacheControlActivity.lambda$null$6$CacheControlActivity(org.telegram.ui.ActionBar.AlertDialog):void");
    }

    public /* synthetic */ void lambda$null$5$CacheControlActivity(AlertDialog alertDialog) {
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        if (this.listAdapter != null) {
            this.databaseSize = MessagesStorage.getInstance(this.currentAccount).getDatabaseSize();
            this.listAdapter.notifyDataSetChanged();
        }
    }

    public /* synthetic */ void lambda$null$8$CacheControlActivity(View view) {
        CheckBoxCell checkBoxCell = (CheckBoxCell) view;
        int intValue = ((Integer) checkBoxCell.getTag()).intValue();
        boolean[] zArr = this.clear;
        zArr[intValue] = !zArr[intValue];
        checkBoxCell.setChecked(zArr[intValue], true);
    }

    public /* synthetic */ void lambda$null$9$CacheControlActivity(View view) {
        try {
            if (this.visibleDialog != null) {
                this.visibleDialog.dismiss();
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        cleanupFolders();
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
            return adapterPosition == CacheControlActivity.this.databaseRow || (adapterPosition == CacheControlActivity.this.cacheRow && CacheControlActivity.this.totalSize > 0) || adapterPosition == CacheControlActivity.this.keepMediaRow;
        }

        public int getItemCount() {
            return CacheControlActivity.this.rowCount;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            if (i != 0) {
                view = new TextInfoPrivacyCell(this.mContext);
            } else {
                view = new TextSettingsCell(this.mContext);
                view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            String str;
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType == 0) {
                TextSettingsCell textSettingsCell = (TextSettingsCell) viewHolder.itemView;
                if (i == CacheControlActivity.this.databaseRow) {
                    textSettingsCell.setTextAndValue(LocaleController.getString("LocalDatabase", NUM), AndroidUtilities.formatFileSize(CacheControlActivity.this.databaseSize), false);
                } else if (i == CacheControlActivity.this.cacheRow) {
                    if (CacheControlActivity.this.calculating) {
                        textSettingsCell.setTextAndValue(LocaleController.getString("ClearMediaCache", NUM), LocaleController.getString("CalculatingSize", NUM), false);
                    } else {
                        textSettingsCell.setTextAndValue(LocaleController.getString("ClearMediaCache", NUM), CacheControlActivity.this.totalSize == 0 ? LocaleController.getString("CacheEmpty", NUM) : AndroidUtilities.formatFileSize(CacheControlActivity.this.totalSize), false);
                    }
                } else if (i == CacheControlActivity.this.keepMediaRow) {
                    MessagesController.getGlobalMainSettings();
                    int i2 = SharedConfig.keepMedia;
                    if (i2 == 0) {
                        str = LocaleController.formatPluralString("Weeks", 1);
                    } else if (i2 == 1) {
                        str = LocaleController.formatPluralString("Months", 1);
                    } else if (i2 == 3) {
                        str = LocaleController.formatPluralString("Days", 3);
                    } else {
                        str = LocaleController.getString("KeepMediaForever", NUM);
                    }
                    textSettingsCell.setTextAndValue(LocaleController.getString("KeepMedia", NUM), str, false);
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
            }
        }

        public int getItemViewType(int i) {
            return (i == CacheControlActivity.this.databaseInfoRow || i == CacheControlActivity.this.cacheInfoRow || i == CacheControlActivity.this.keepMediaInfoRow) ? 1 : 0;
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        return new ThemeDescription[]{new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"), new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"), new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"), new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"), new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"), new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteValueText"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"), new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4")};
    }
}
