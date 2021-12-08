package org.telegram.messenger;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
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
import androidx.core.graphics.ColorUtils;
import j$.util.stream.Stream;
import j$.wrappers.C$r8$wrapper$java$util$stream$Stream$VWRP;
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
    public static FilesMigrationBottomSheet filesMigrationBottomSheet;
    public static boolean hasOldFolder;
    public static boolean isRunning;
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

    public int onStartCommand(Intent intent, int flags, int startId) {
        NotificationsController.checkOtherNotificationsChannel();
        Notification notification = new Notification.Builder(this, NotificationsController.OTHER_NOTIFICATIONS_CHANNEL).setContentTitle(getText(NUM)).setAutoCancel(false).setSmallIcon(NUM).build();
        isRunning = true;
        new Thread() {
            public void run() {
                FilesMigrationService.this.migrateOldFolder();
                AndroidUtilities.runOnUIThread(new FilesMigrationService$1$$ExternalSyntheticLambda0(this));
            }

            /* renamed from: lambda$run$0$org-telegram-messenger-FilesMigrationService$1  reason: not valid java name */
            public /* synthetic */ void m683lambda$run$0$orgtelegrammessengerFilesMigrationService$1() {
                FilesMigrationService.isRunning = false;
                FilesMigrationService.this.stopForeground(true);
                FilesMigrationService.this.stopSelf();
            }
        }.start();
        startForeground(301, notification);
        return super.onStartCommand(intent, flags, startId);
    }

    public void migrateOldFolder() {
        ArrayList<File> dirs;
        File path = Environment.getExternalStorageDirectory();
        if (Build.VERSION.SDK_INT >= 19 && !TextUtils.isEmpty(SharedConfig.storageCacheDir) && (dirs = AndroidUtilities.getRootDirs()) != null) {
            int a = 0;
            int N = dirs.size();
            while (true) {
                if (a >= N) {
                    break;
                }
                File dir = dirs.get(a);
                if (dir.getAbsolutePath().startsWith(SharedConfig.storageCacheDir)) {
                    path = dir;
                    break;
                }
                a++;
            }
        }
        File telegramPath = new File(ApplicationLoader.applicationContext.getExternalFilesDir((String) null), "Telegram");
        File oldPath = new File(path, "Telegram");
        this.totalFilesCount = getFilesCount(oldPath);
        long moveStart = System.currentTimeMillis();
        if (oldPath.canRead() && oldPath.canWrite()) {
            moveDirectory(oldPath, telegramPath);
        }
        FileLog.d("move time = " + (System.currentTimeMillis() - moveStart));
        ApplicationLoader.applicationContext.getSharedPreferences("systemConfig", 0).edit().putBoolean("migration_to_scoped_storage_finished", true).apply();
    }

    private int getFilesCount(File source) {
        if (!source.exists()) {
            return 0;
        }
        int count = 0;
        File[] fileList = source.listFiles();
        if (fileList != null) {
            for (int i = 0; i < fileList.length; i++) {
                if (fileList[i].isDirectory()) {
                    count += getFilesCount(fileList[i]);
                } else {
                    count++;
                }
            }
        }
        return count;
    }

    private void moveDirectory(File source, File target) {
        Stream convert;
        if (!source.exists()) {
            return;
        }
        if (target.exists() || target.mkdir()) {
            try {
                convert = C$r8$wrapper$java$util$stream$Stream$VWRP.convert(Files.list(source.toPath()));
                convert.forEach(new FilesMigrationService$$ExternalSyntheticLambda1(this, target));
                if (convert != null) {
                    convert.close();
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            } catch (Throwable th) {
            }
            try {
                source.delete();
                return;
            } catch (Exception e2) {
                FileLog.e((Throwable) e2);
                return;
            }
        } else {
            return;
        }
        throw th;
    }

    /* renamed from: lambda$moveDirectory$0$org-telegram-messenger-FilesMigrationService  reason: not valid java name */
    public /* synthetic */ void m681x55fd53fa(File target, Path path) {
        File dest = new File(target, path.getFileName().toString());
        if (Files.isDirectory(path, new LinkOption[0])) {
            moveDirectory(path.toFile(), dest);
            return;
        }
        try {
            Files.move(path, dest.toPath(), new CopyOption[0]);
        } catch (Exception e) {
            FileLog.e((Throwable) e, false);
            try {
                path.toFile().delete();
            } catch (Exception e1) {
                FileLog.e((Throwable) e1);
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

    /* renamed from: lambda$updateProgress$1$org-telegram-messenger-FilesMigrationService  reason: not valid java name */
    public /* synthetic */ void m682x13efee05(int currentCount) {
        ((NotificationManager) getSystemService("notification")).notify(301, new Notification.Builder(this, NotificationsController.OTHER_NOTIFICATIONS_CHANNEL).setContentTitle(getText(NUM)).setContentText(String.format("%s/%s", new Object[]{Integer.valueOf(currentCount), Integer.valueOf(this.totalFilesCount)})).setSmallIcon(NUM).setAutoCancel(false).setProgress(this.totalFilesCount, currentCount, false).build());
    }

    public static void checkBottomSheet(BaseFragment fragment) {
        ArrayList<File> dirs;
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("systemConfig", 0);
        if (Environment.isExternalStorageLegacy() && !sharedPreferences.getBoolean("migration_to_scoped_storage_finished", false) && sharedPreferences.getInt("migration_to_scoped_storage_count", 0) < 5 && !wasShown && filesMigrationBottomSheet == null && !isRunning) {
            if (Build.VERSION.SDK_INT >= 30) {
                File path = Environment.getExternalStorageDirectory();
                if (!TextUtils.isEmpty(SharedConfig.storageCacheDir) && (dirs = AndroidUtilities.getRootDirs()) != null) {
                    int a = 0;
                    int N = dirs.size();
                    while (true) {
                        if (a >= N) {
                            break;
                        }
                        File dir = dirs.get(a);
                        if (dir.getAbsolutePath().startsWith(SharedConfig.storageCacheDir)) {
                            path = dir;
                            break;
                        }
                        a++;
                    }
                }
                hasOldFolder = new File(path, "Telegram").exists();
            }
            if (hasOldFolder) {
                FilesMigrationBottomSheet filesMigrationBottomSheet2 = new FilesMigrationBottomSheet(fragment);
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
        public FilesMigrationBottomSheet(BaseFragment fragment2) {
            super(fragment2.getParentActivity(), false);
            this.fragment = fragment2;
            setCanceledOnTouchOutside(false);
            Context context = fragment2.getParentActivity();
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(1);
            StickerImageView imageView = new StickerImageView(context, this.currentAccount);
            imageView.setStickerNum(7);
            imageView.getImageReceiver().setAutoRepeat(1);
            linearLayout.addView(imageView, LayoutHelper.createLinear(144, 144, 1, 0, 16, 0, 0));
            TextView title = new TextView(context);
            title.setGravity(8388611);
            title.setTextColor(Theme.getColor("dialogTextBlack"));
            title.setTextSize(1, 20.0f);
            title.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            title.setText(LocaleController.getString("MigrateOldFolderTitle", NUM));
            linearLayout.addView(title, LayoutHelper.createFrame(-1, -2.0f, 0, 21.0f, 30.0f, 21.0f, 0.0f));
            TextView description = new TextView(context);
            description.setGravity(8388611);
            description.setTextSize(1, 15.0f);
            description.setTextColor(Theme.getColor("dialogTextBlack"));
            description.setText(AndroidUtilities.replaceTags(LocaleController.getString("MigrateOldFolderDescription", NUM)));
            linearLayout.addView(description, LayoutHelper.createFrame(-1, -2.0f, 0, 21.0f, 15.0f, 21.0f, 16.0f));
            TextView buttonTextView = new TextView(context);
            buttonTextView.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
            buttonTextView.setGravity(17);
            buttonTextView.setTextSize(1, 14.0f);
            buttonTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            buttonTextView.setText(LocaleController.getString("MigrateOldFolderButton", NUM));
            buttonTextView.setTextColor(Theme.getColor("featuredStickers_buttonText"));
            buttonTextView.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(6.0f), Theme.getColor("featuredStickers_addButton"), ColorUtils.setAlphaComponent(Theme.getColor("windowBackgroundWhite"), 120)));
            linearLayout.addView(buttonTextView, LayoutHelper.createFrame(-1, 48.0f, 0, 16.0f, 15.0f, 16.0f, 16.0f));
            buttonTextView.setOnClickListener(new FilesMigrationService$FilesMigrationBottomSheet$$ExternalSyntheticLambda0(this));
            ScrollView scrollView = new ScrollView(context);
            scrollView.addView(linearLayout);
            setCustomView(scrollView);
        }

        /* renamed from: lambda$new$0$org-telegram-messenger-FilesMigrationService$FilesMigrationBottomSheet  reason: not valid java name */
        public /* synthetic */ void m684xavar_eacf(View view) {
            migrateOldFolder();
        }

        public void migrateOldFolder() {
            Activity activity = this.fragment.getParentActivity();
            boolean canRead = true;
            boolean canWrite = activity.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == 0;
            if (activity.checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != 0) {
                canRead = false;
            }
            if (!canRead || !canWrite) {
                ArrayList<String> permissions = new ArrayList<>();
                if (!canRead) {
                    permissions.add("android.permission.READ_EXTERNAL_STORAGE");
                }
                if (!canWrite) {
                    permissions.add("android.permission.WRITE_EXTERNAL_STORAGE");
                }
                activity.requestPermissions((String[]) permissions.toArray(new String[permissions.size()]), 4);
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
