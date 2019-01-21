package org.telegram.ui.Components;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build.VERSION;
import android.support.v4.content.FileProvider;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.SendMessagesHelper.SendingMediaInfo;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet.Builder;
import org.telegram.ui.PhotoAlbumPickerActivity;
import org.telegram.ui.PhotoAlbumPickerActivity.PhotoAlbumPickerActivityDelegate;

public class WallpaperUpdater {
    private String currentPicturePath;
    private File currentWallpaperPath;
    private WallpaperUpdaterDelegate delegate;
    private Activity parentActivity;
    private BaseFragment parentFragment;
    private File picturePath = null;

    public interface WallpaperUpdaterDelegate {
        void didSelectWallpaper(File file, Bitmap bitmap, boolean z);

        void needOpenColorPicker();
    }

    public WallpaperUpdater(Activity activity, BaseFragment fragment, WallpaperUpdaterDelegate wallpaperUpdaterDelegate) {
        this.parentActivity = activity;
        this.parentFragment = fragment;
        this.delegate = wallpaperUpdaterDelegate;
        this.currentWallpaperPath = new File(FileLoader.getDirectory(4), Utilities.random.nextInt() + ".jpg");
    }

    public void showAlert(boolean fromTheme) {
        CharSequence[] items;
        int[] icons;
        Builder builder = new Builder(this.parentActivity);
        builder.setTitle(LocaleController.getString("ChoosePhoto", R.string.ChoosePhoto));
        if (fromTheme) {
            items = new CharSequence[]{LocaleController.getString("ChooseTakePhoto", R.string.ChooseTakePhoto), LocaleController.getString("SelectFromGallery", R.string.SelectFromGallery), LocaleController.getString("SelectColor", R.string.SelectColor), LocaleController.getString("Default", R.string.Default)};
            icons = null;
        } else {
            items = new CharSequence[]{LocaleController.getString("ChooseTakePhoto", R.string.ChooseTakePhoto), LocaleController.getString("SelectFromGallery", R.string.SelectFromGallery)};
            icons = new int[]{R.drawable.menu_camera, R.drawable.profile_photos};
        }
        builder.setItems(items, icons, new WallpaperUpdater$$Lambda$0(this, fromTheme));
        builder.show();
    }

    final /* synthetic */ void lambda$showAlert$0$WallpaperUpdater(boolean fromTheme, DialogInterface dialogInterface, int i) {
        if (i == 0) {
            try {
                Intent takePictureIntent = new Intent("android.media.action.IMAGE_CAPTURE");
                File image = AndroidUtilities.generatePicturePath();
                if (image != null) {
                    if (VERSION.SDK_INT >= 24) {
                        takePictureIntent.putExtra("output", FileProvider.getUriForFile(this.parentActivity, "org.telegram.messenger.beta.provider", image));
                        takePictureIntent.addFlags(2);
                        takePictureIntent.addFlags(1);
                    } else {
                        takePictureIntent.putExtra("output", Uri.fromFile(image));
                    }
                    this.currentPicturePath = image.getAbsolutePath();
                }
                this.parentActivity.startActivityForResult(takePictureIntent, 10);
            } catch (Throwable e) {
                try {
                    FileLog.e(e);
                } catch (Throwable e2) {
                    FileLog.e(e2);
                }
            }
        } else if (i == 1) {
            openGallery();
        } else if (!fromTheme) {
        } else {
            if (i == 2) {
                this.delegate.needOpenColorPicker();
            } else if (i == 3) {
                this.delegate.didSelectWallpaper(null, null, false);
            }
        }
    }

    public void openGallery() {
        if (this.parentFragment == null) {
            Intent photoPickerIntent = new Intent("android.intent.action.PICK");
            photoPickerIntent.setType("image/*");
            this.parentActivity.startActivityForResult(photoPickerIntent, 11);
        } else if (VERSION.SDK_INT < 23 || this.parentFragment.getParentActivity() == null || this.parentFragment.getParentActivity().checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == 0) {
            PhotoAlbumPickerActivity fragment = new PhotoAlbumPickerActivity(2, false, false, null);
            fragment.setAllowSearchImages(false);
            fragment.setDelegate(new PhotoAlbumPickerActivityDelegate() {
                public void didSelectPhotos(ArrayList<SendingMediaInfo> photos) {
                    WallpaperUpdater.this.didSelectPhotos(photos);
                }

                public void startPhotoSelectActivity() {
                    try {
                        Intent photoPickerIntent = new Intent("android.intent.action.PICK");
                        photoPickerIntent.setType("image/*");
                        WallpaperUpdater.this.parentActivity.startActivityForResult(photoPickerIntent, 11);
                    } catch (Throwable e) {
                        FileLog.e(e);
                    }
                }
            });
            this.parentFragment.presentFragment(fragment);
        } else {
            this.parentFragment.getParentActivity().requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 4);
        }
    }

    private void didSelectPhotos(ArrayList<SendingMediaInfo> photos) {
        try {
            if (!photos.isEmpty()) {
                SendingMediaInfo info = (SendingMediaInfo) photos.get(0);
                if (info.path != null) {
                    Point screenSize = AndroidUtilities.getRealScreenSize();
                    Bitmap bitmap = ImageLoader.loadBitmap(info.path, null, (float) screenSize.x, (float) screenSize.y, true);
                    bitmap.compress(CompressFormat.JPEG, 87, new FileOutputStream(this.currentWallpaperPath));
                    this.delegate.didSelectWallpaper(this.currentWallpaperPath, bitmap, true);
                }
            }
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    public void cleanup() {
        this.currentWallpaperPath.delete();
    }

    public File getCurrentWallpaperPath() {
        return this.currentWallpaperPath;
    }

    public String getCurrentPicturePath() {
        return this.currentPicturePath;
    }

    public void setCurrentPicturePath(String value) {
        this.currentPicturePath = value;
    }

    /* JADX WARNING: Removed duplicated region for block: B:26:0x0057 A:{SYNTHETIC, Splitter: B:26:0x0057} */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x004b A:{SYNTHETIC, Splitter: B:20:0x004b} */
    public void onActivityResult(int r12, int r13, android.content.Intent r14) {
        /*
        r11 = this;
        r10 = 0;
        r5 = -1;
        if (r13 != r5) goto L_0x003e;
    L_0x0004:
        r5 = 10;
        if (r12 != r5) goto L_0x0060;
    L_0x0008:
        r5 = r11.currentPicturePath;
        org.telegram.messenger.AndroidUtilities.addMediaToGallery(r5);
        r3 = 0;
        r2 = org.telegram.messenger.AndroidUtilities.getRealScreenSize();	 Catch:{ Exception -> 0x0045 }
        r5 = r11.currentPicturePath;	 Catch:{ Exception -> 0x0045 }
        r6 = 0;
        r7 = r2.x;	 Catch:{ Exception -> 0x0045 }
        r7 = (float) r7;	 Catch:{ Exception -> 0x0045 }
        r8 = r2.y;	 Catch:{ Exception -> 0x0045 }
        r8 = (float) r8;	 Catch:{ Exception -> 0x0045 }
        r9 = 1;
        r0 = org.telegram.messenger.ImageLoader.loadBitmap(r5, r6, r7, r8, r9);	 Catch:{ Exception -> 0x0045 }
        r4 = new java.io.FileOutputStream;	 Catch:{ Exception -> 0x0045 }
        r5 = r11.currentWallpaperPath;	 Catch:{ Exception -> 0x0045 }
        r4.<init>(r5);	 Catch:{ Exception -> 0x0045 }
        r5 = android.graphics.Bitmap.CompressFormat.JPEG;	 Catch:{ Exception -> 0x009f, all -> 0x009c }
        r6 = 87;
        r0.compress(r5, r6, r4);	 Catch:{ Exception -> 0x009f, all -> 0x009c }
        r5 = r11.delegate;	 Catch:{ Exception -> 0x009f, all -> 0x009c }
        r6 = r11.currentWallpaperPath;	 Catch:{ Exception -> 0x009f, all -> 0x009c }
        r7 = 0;
        r5.didSelectWallpaper(r6, r0, r7);	 Catch:{ Exception -> 0x009f, all -> 0x009c }
        if (r4 == 0) goto L_0x003b;
    L_0x0038:
        r4.close();	 Catch:{ Exception -> 0x003f }
    L_0x003b:
        r3 = r4;
    L_0x003c:
        r11.currentPicturePath = r10;
    L_0x003e:
        return;
    L_0x003f:
        r1 = move-exception;
        org.telegram.messenger.FileLog.e(r1);
        r3 = r4;
        goto L_0x003c;
    L_0x0045:
        r1 = move-exception;
    L_0x0046:
        org.telegram.messenger.FileLog.e(r1);	 Catch:{ all -> 0x0054 }
        if (r3 == 0) goto L_0x003c;
    L_0x004b:
        r3.close();	 Catch:{ Exception -> 0x004f }
        goto L_0x003c;
    L_0x004f:
        r1 = move-exception;
        org.telegram.messenger.FileLog.e(r1);
        goto L_0x003c;
    L_0x0054:
        r5 = move-exception;
    L_0x0055:
        if (r3 == 0) goto L_0x005a;
    L_0x0057:
        r3.close();	 Catch:{ Exception -> 0x005b }
    L_0x005a:
        throw r5;
    L_0x005b:
        r1 = move-exception;
        org.telegram.messenger.FileLog.e(r1);
        goto L_0x005a;
    L_0x0060:
        r5 = 11;
        if (r12 != r5) goto L_0x003e;
    L_0x0064:
        if (r14 == 0) goto L_0x003e;
    L_0x0066:
        r5 = r14.getData();
        if (r5 == 0) goto L_0x003e;
    L_0x006c:
        r2 = org.telegram.messenger.AndroidUtilities.getRealScreenSize();	 Catch:{ Exception -> 0x0097 }
        r5 = 0;
        r6 = r14.getData();	 Catch:{ Exception -> 0x0097 }
        r7 = r2.x;	 Catch:{ Exception -> 0x0097 }
        r7 = (float) r7;	 Catch:{ Exception -> 0x0097 }
        r8 = r2.y;	 Catch:{ Exception -> 0x0097 }
        r8 = (float) r8;	 Catch:{ Exception -> 0x0097 }
        r9 = 1;
        r0 = org.telegram.messenger.ImageLoader.loadBitmap(r5, r6, r7, r8, r9);	 Catch:{ Exception -> 0x0097 }
        r3 = new java.io.FileOutputStream;	 Catch:{ Exception -> 0x0097 }
        r5 = r11.currentWallpaperPath;	 Catch:{ Exception -> 0x0097 }
        r3.<init>(r5);	 Catch:{ Exception -> 0x0097 }
        r5 = android.graphics.Bitmap.CompressFormat.JPEG;	 Catch:{ Exception -> 0x0097 }
        r6 = 87;
        r0.compress(r5, r6, r3);	 Catch:{ Exception -> 0x0097 }
        r5 = r11.delegate;	 Catch:{ Exception -> 0x0097 }
        r6 = r11.currentWallpaperPath;	 Catch:{ Exception -> 0x0097 }
        r7 = 0;
        r5.didSelectWallpaper(r6, r0, r7);	 Catch:{ Exception -> 0x0097 }
        goto L_0x003e;
    L_0x0097:
        r1 = move-exception;
        org.telegram.messenger.FileLog.e(r1);
        goto L_0x003e;
    L_0x009c:
        r5 = move-exception;
        r3 = r4;
        goto L_0x0055;
    L_0x009f:
        r1 = move-exception;
        r3 = r4;
        goto L_0x0046;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.WallpaperUpdater.onActivityResult(int, int, android.content.Intent):void");
    }
}
