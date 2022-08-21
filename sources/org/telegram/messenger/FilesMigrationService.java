package org.telegram.messenger;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.io.File;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.StickerImageView;

public class FilesMigrationService extends Service {
    public static FilesMigrationBottomSheet filesMigrationBottomSheet = null;
    public static boolean hasOldFolder = false;
    public static boolean isRunning = false;
    private static boolean wasShown = false;
    long lastUpdateTime;
    private int movedFilesCount;
    private int totalFilesCount;

    public IBinder onBind(Intent intent) {
        return null;
    }

    public static void start() {
        ApplicationLoader.applicationContext.startService(new Intent(ApplicationLoader.applicationContext, FilesMigrationService.class));
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        NotificationsController.checkOtherNotificationsChannel();
        Notification build = new Notification.Builder(this, NotificationsController.OTHER_NOTIFICATIONS_CHANNEL).setContentTitle(getText(R.string.MigratingFiles)).setAutoCancel(false).setSmallIcon(R.drawable.notification).build();
        isRunning = true;
        new Thread() {
            public void run() {
                FilesMigrationService.this.migrateOldFolder();
                AndroidUtilities.runOnUIThread(new FilesMigrationService$1$$ExternalSyntheticLambda0(this));
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$run$0() {
                FilesMigrationService.isRunning = false;
                FilesMigrationService.this.stopForeground(true);
                FilesMigrationService.this.stopSelf();
            }
        }.start();
        startForeground(301, build);
        return super.onStartCommand(intent, i, i2);
    }

    public void migrateOldFolder() {
        ArrayList<File> rootDirs;
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        if (Build.VERSION.SDK_INT >= 19 && !TextUtils.isEmpty(SharedConfig.storageCacheDir) && (rootDirs = AndroidUtilities.getRootDirs()) != null) {
            int size = rootDirs.size();
            int i = 0;
            while (true) {
                if (i >= size) {
                    break;
                }
                File file = rootDirs.get(i);
                if (file.getAbsolutePath().startsWith(SharedConfig.storageCacheDir)) {
                    externalStorageDirectory = file;
                    break;
                }
                i++;
            }
        }
        File file2 = new File(ApplicationLoader.applicationContext.getExternalFilesDir((String) null), "Telegram");
        File file3 = new File(externalStorageDirectory, "Telegram");
        this.totalFilesCount = getFilesCount(file3);
        long currentTimeMillis = System.currentTimeMillis();
        if (file3.canRead() && file3.canWrite()) {
            moveDirectory(file3, file2);
        }
        FileLog.d("move time = " + (System.currentTimeMillis() - currentTimeMillis));
        ApplicationLoader.applicationContext.getSharedPreferences("systemConfig", 0).edit().putBoolean("migration_to_scoped_storage_finished", true).apply();
    }

    private int getFilesCount(File file) {
        File[] listFiles;
        if (!file.exists() || (listFiles = file.listFiles()) == null) {
            return 0;
        }
        int i = 0;
        for (int i2 = 0; i2 < listFiles.length; i2++) {
            i = listFiles[i2].isDirectory() ? i + getFilesCount(listFiles[i2]) : i + 1;
        }
        return i;
    }

    /* JADX WARNING: Missing exception handler attribute for start block: B:16:0x0031 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void moveDirectory(java.io.File r3, java.io.File r4) {
        /*
            r2 = this;
            boolean r0 = r3.exists()
            if (r0 == 0) goto L_0x003e
            boolean r0 = r4.exists()
            if (r0 != 0) goto L_0x0013
            boolean r0 = r4.mkdir()
            if (r0 != 0) goto L_0x0013
            goto L_0x003e
        L_0x0013:
            java.nio.file.Path r0 = r3.toPath()     // Catch:{ Exception -> 0x0032 }
            java.util.stream.Stream r0 = java.nio.file.Files.list(r0)     // Catch:{ Exception -> 0x0032 }
            j$.util.stream.Stream r0 = j$.wrappers.C$r8$wrapper$java$util$stream$Stream$VWRP.convert(r0)     // Catch:{ Exception -> 0x0032 }
            org.telegram.messenger.FilesMigrationService$$ExternalSyntheticLambda1 r1 = new org.telegram.messenger.FilesMigrationService$$ExternalSyntheticLambda1     // Catch:{ all -> 0x002b }
            r1.<init>(r2, r4)     // Catch:{ all -> 0x002b }
            r0.forEach(r1)     // Catch:{ all -> 0x002b }
            r0.close()     // Catch:{ Exception -> 0x0032 }
            goto L_0x0036
        L_0x002b:
            r4 = move-exception
            if (r0 == 0) goto L_0x0031
            r0.close()     // Catch:{ all -> 0x0031 }
        L_0x0031:
            throw r4     // Catch:{ Exception -> 0x0032 }
        L_0x0032:
            r4 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r4)
        L_0x0036:
            r3.delete()     // Catch:{ Exception -> 0x003a }
            goto L_0x003e
        L_0x003a:
            r3 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r3)
        L_0x003e:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FilesMigrationService.moveDirectory(java.io.File, java.io.File):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$moveDirectory$0(File file, Path path) {
        File file2 = new File(file, path.getFileName().toString());
        if (Files.isDirectory(path, new LinkOption[0])) {
            moveDirectory(path.toFile(), file2);
            return;
        }
        try {
            Files.move(path, file2.toPath(), new CopyOption[0]);
        } catch (Exception e) {
            FileLog.e((Throwable) e, false);
            try {
                path.toFile().delete();
            } catch (Exception e2) {
                FileLog.e((Throwable) e2);
            }
        }
        this.movedFilesCount++;
        updateProgress();
    }

    private void updateProgress() {
        if (System.currentTimeMillis() - this.lastUpdateTime > 20 || this.movedFilesCount >= this.totalFilesCount - 1) {
            AndroidUtilities.runOnUIThread(new FilesMigrationService$$ExternalSyntheticLambda0(this, this.movedFilesCount));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateProgress$1(int i) {
        ((NotificationManager) getSystemService("notification")).notify(301, new Notification.Builder(this, NotificationsController.OTHER_NOTIFICATIONS_CHANNEL).setContentTitle(getText(R.string.MigratingFiles)).setContentText(String.format("%s/%s", new Object[]{Integer.valueOf(i), Integer.valueOf(this.totalFilesCount)})).setSmallIcon(R.drawable.notification).setAutoCancel(false).setProgress(this.totalFilesCount, i, false).build());
    }

    public static void checkBottomSheet(BaseFragment baseFragment) {
        ArrayList<File> rootDirs;
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("systemConfig", 0);
        if (Environment.isExternalStorageLegacy() && !sharedPreferences.getBoolean("migration_to_scoped_storage_finished", false) && sharedPreferences.getInt("migration_to_scoped_storage_count", 0) < 5 && !wasShown && filesMigrationBottomSheet == null && !isRunning) {
            if (Build.VERSION.SDK_INT >= 30) {
                File externalStorageDirectory = Environment.getExternalStorageDirectory();
                if (!TextUtils.isEmpty(SharedConfig.storageCacheDir) && (rootDirs = AndroidUtilities.getRootDirs()) != null) {
                    int size = rootDirs.size();
                    int i = 0;
                    while (true) {
                        if (i >= size) {
                            break;
                        }
                        File file = rootDirs.get(i);
                        if (file.getAbsolutePath().startsWith(SharedConfig.storageCacheDir)) {
                            externalStorageDirectory = file;
                            break;
                        }
                        i++;
                    }
                }
                hasOldFolder = new File(externalStorageDirectory, "Telegram").exists();
            }
            if (hasOldFolder) {
                FilesMigrationBottomSheet filesMigrationBottomSheet2 = new FilesMigrationBottomSheet(baseFragment);
                filesMigrationBottomSheet = filesMigrationBottomSheet2;
                filesMigrationBottomSheet2.show();
                wasShown = true;
                sharedPreferences.edit().putInt("migration_to_scoped_storage_count", sharedPreferences.getInt("migration_to_scoped_storage_count", 0) + 1).apply();
                return;
            }
            sharedPreferences.edit().putBoolean("migration_to_scoped_storage_finished", true).apply();
        }
    }

    public static class FilesMigrationBottomSheet extends BottomSheet {
        BaseFragment fragment;

        /* access modifiers changed from: protected */
        public boolean canDismissWithSwipe() {
            return false;
        }

        /* access modifiers changed from: protected */
        public boolean canDismissWithTouchOutside() {
            return false;
        }

        /* JADX INFO: super call moved to the top of the method (can break code semantics) */
        public FilesMigrationBottomSheet(BaseFragment baseFragment) {
            super(baseFragment.getParentActivity(), false);
            this.fragment = baseFragment;
            setCanceledOnTouchOutside(false);
            Activity parentActivity = baseFragment.getParentActivity();
            LinearLayout linearLayout = new LinearLayout(parentActivity);
            linearLayout.setOrientation(1);
            StickerImageView stickerImageView = new StickerImageView(parentActivity, this.currentAccount);
            stickerImageView.setStickerNum(7);
            stickerImageView.getImageReceiver().setAutoRepeat(1);
            linearLayout.addView(stickerImageView, LayoutHelper.createLinear(144, 144, 1, 0, 16, 0, 0));
            TextView textView = new TextView(parentActivity);
            textView.setGravity(8388611);
            textView.setTextColor(Theme.getColor("dialogTextBlack"));
            textView.setTextSize(1, 20.0f);
            textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            textView.setText(LocaleController.getString("MigrateOldFolderTitle", R.string.MigrateOldFolderTitle));
            linearLayout.addView(textView, LayoutHelper.createFrame(-1, -2.0f, 0, 21.0f, 30.0f, 21.0f, 0.0f));
            TextView textView2 = new TextView(parentActivity);
            textView2.setGravity(8388611);
            textView2.setTextSize(1, 15.0f);
            textView2.setTextColor(Theme.getColor("dialogTextBlack"));
            textView2.setText(AndroidUtilities.replaceTags(LocaleController.getString("MigrateOldFolderDescription", R.string.MigrateOldFolderDescription)));
            linearLayout.addView(textView2, LayoutHelper.createFrame(-1, -2.0f, 0, 21.0f, 15.0f, 21.0f, 16.0f));
            TextView textView3 = new TextView(parentActivity);
            textView3.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
            textView3.setGravity(17);
            textView3.setTextSize(1, 14.0f);
            textView3.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            textView3.setText(LocaleController.getString("MigrateOldFolderButton", R.string.MigrateOldFolderButton));
            textView3.setTextColor(Theme.getColor("featuredStickers_buttonText"));
            textView3.setBackground(Theme.AdaptiveRipple.filledRect("featuredStickers_addButton", 6.0f));
            linearLayout.addView(textView3, LayoutHelper.createFrame(-1, 48.0f, 0, 16.0f, 15.0f, 16.0f, 16.0f));
            textView3.setOnClickListener(new FilesMigrationService$FilesMigrationBottomSheet$$ExternalSyntheticLambda0(this));
            ScrollView scrollView = new ScrollView(parentActivity);
            scrollView.addView(linearLayout);
            setCustomView(scrollView);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0(View view) {
            migrateOldFolder();
        }

        public void migrateOldFolder() {
            Activity parentActivity = this.fragment.getParentActivity();
            boolean z = true;
            boolean z2 = parentActivity.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == 0;
            if (parentActivity.checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != 0) {
                z = false;
            }
            if (!z || !z2) {
                ArrayList arrayList = new ArrayList();
                if (!z) {
                    arrayList.add("android.permission.READ_EXTERNAL_STORAGE");
                }
                if (!z2) {
                    arrayList.add("android.permission.WRITE_EXTERNAL_STORAGE");
                }
                parentActivity.requestPermissions((String[]) arrayList.toArray(new String[arrayList.size()]), 4);
                return;
            }
            FilesMigrationService.start();
            dismiss();
        }

        public void dismiss() {
            super.dismiss();
            FilesMigrationService.filesMigrationBottomSheet = null;
        }
    }
}
