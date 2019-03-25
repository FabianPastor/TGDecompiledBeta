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
    }

    public void showAlert(boolean fromTheme) {
        CharSequence[] items;
        int[] icons;
        Builder builder = new Builder(this.parentActivity);
        builder.setTitle(LocaleController.getString("ChoosePhoto", NUM));
        if (fromTheme) {
            items = new CharSequence[]{LocaleController.getString("ChooseTakePhoto", NUM), LocaleController.getString("SelectFromGallery", NUM), LocaleController.getString("SelectColor", NUM), LocaleController.getString("Default", NUM)};
            icons = null;
        } else {
            items = new CharSequence[]{LocaleController.getString("ChooseTakePhoto", NUM), LocaleController.getString("SelectFromGallery", NUM)};
            icons = new int[]{NUM, NUM};
        }
        builder.setItems(items, icons, new WallpaperUpdater$$Lambda$0(this, fromTheme));
        builder.show();
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$showAlert$0$WallpaperUpdater(boolean fromTheme, DialogInterface dialogInterface, int i) {
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
            } catch (Exception e) {
                try {
                    FileLog.e(e);
                } catch (Exception e2) {
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
                    } catch (Exception e) {
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
                    this.currentWallpaperPath = new File(FileLoader.getDirectory(4), Utilities.random.nextInt() + ".jpg");
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
    }

    public String getCurrentPicturePath() {
        return this.currentPicturePath;
    }

    public void setCurrentPicturePath(String value) {
        this.currentPicturePath = value;
    }

    /* JADX WARNING: Removed duplicated region for block: B:20:0x0071 A:{SYNTHETIC, Splitter:B:20:0x0071} */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x007d A:{SYNTHETIC, Splitter:B:26:0x007d} */
    public void onActivityResult(int r12, int r13, android.content.Intent r14) {
        /*
        r11 = this;
        r10 = 0;
        r5 = -1;
        if (r13 != r5) goto L_0x0064;
    L_0x0004:
        r5 = 10;
        if (r12 != r5) goto L_0x0086;
    L_0x0008:
        r5 = r11.currentPicturePath;
        org.telegram.messenger.AndroidUtilities.addMediaToGallery(r5);
        r3 = 0;
        r5 = new java.io.File;	 Catch:{ Exception -> 0x006b }
        r6 = 4;
        r6 = org.telegram.messenger.FileLoader.getDirectory(r6);	 Catch:{ Exception -> 0x006b }
        r7 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x006b }
        r7.<init>();	 Catch:{ Exception -> 0x006b }
        r8 = org.telegram.messenger.Utilities.random;	 Catch:{ Exception -> 0x006b }
        r8 = r8.nextInt();	 Catch:{ Exception -> 0x006b }
        r7 = r7.append(r8);	 Catch:{ Exception -> 0x006b }
        r8 = ".jpg";
        r7 = r7.append(r8);	 Catch:{ Exception -> 0x006b }
        r7 = r7.toString();	 Catch:{ Exception -> 0x006b }
        r5.<init>(r6, r7);	 Catch:{ Exception -> 0x006b }
        r11.currentWallpaperPath = r5;	 Catch:{ Exception -> 0x006b }
        r2 = org.telegram.messenger.AndroidUtilities.getRealScreenSize();	 Catch:{ Exception -> 0x006b }
        r5 = r11.currentPicturePath;	 Catch:{ Exception -> 0x006b }
        r6 = 0;
        r7 = r2.x;	 Catch:{ Exception -> 0x006b }
        r7 = (float) r7;	 Catch:{ Exception -> 0x006b }
        r8 = r2.y;	 Catch:{ Exception -> 0x006b }
        r8 = (float) r8;	 Catch:{ Exception -> 0x006b }
        r9 = 1;
        r0 = org.telegram.messenger.ImageLoader.loadBitmap(r5, r6, r7, r8, r9);	 Catch:{ Exception -> 0x006b }
        r4 = new java.io.FileOutputStream;	 Catch:{ Exception -> 0x006b }
        r5 = r11.currentWallpaperPath;	 Catch:{ Exception -> 0x006b }
        r4.<init>(r5);	 Catch:{ Exception -> 0x006b }
        r5 = android.graphics.Bitmap.CompressFormat.JPEG;	 Catch:{ Exception -> 0x00ec, all -> 0x00e9 }
        r6 = 87;
        r0.compress(r5, r6, r4);	 Catch:{ Exception -> 0x00ec, all -> 0x00e9 }
        r5 = r11.delegate;	 Catch:{ Exception -> 0x00ec, all -> 0x00e9 }
        r6 = r11.currentWallpaperPath;	 Catch:{ Exception -> 0x00ec, all -> 0x00e9 }
        r7 = 0;
        r5.didSelectWallpaper(r6, r0, r7);	 Catch:{ Exception -> 0x00ec, all -> 0x00e9 }
        if (r4 == 0) goto L_0x0061;
    L_0x005e:
        r4.close();	 Catch:{ Exception -> 0x0065 }
    L_0x0061:
        r3 = r4;
    L_0x0062:
        r11.currentPicturePath = r10;
    L_0x0064:
        return;
    L_0x0065:
        r1 = move-exception;
        org.telegram.messenger.FileLog.e(r1);
        r3 = r4;
        goto L_0x0062;
    L_0x006b:
        r1 = move-exception;
    L_0x006c:
        org.telegram.messenger.FileLog.e(r1);	 Catch:{ all -> 0x007a }
        if (r3 == 0) goto L_0x0062;
    L_0x0071:
        r3.close();	 Catch:{ Exception -> 0x0075 }
        goto L_0x0062;
    L_0x0075:
        r1 = move-exception;
        org.telegram.messenger.FileLog.e(r1);
        goto L_0x0062;
    L_0x007a:
        r5 = move-exception;
    L_0x007b:
        if (r3 == 0) goto L_0x0080;
    L_0x007d:
        r3.close();	 Catch:{ Exception -> 0x0081 }
    L_0x0080:
        throw r5;
    L_0x0081:
        r1 = move-exception;
        org.telegram.messenger.FileLog.e(r1);
        goto L_0x0080;
    L_0x0086:
        r5 = 11;
        if (r12 != r5) goto L_0x0064;
    L_0x008a:
        if (r14 == 0) goto L_0x0064;
    L_0x008c:
        r5 = r14.getData();
        if (r5 == 0) goto L_0x0064;
    L_0x0092:
        r5 = new java.io.File;	 Catch:{ Exception -> 0x00e3 }
        r6 = 4;
        r6 = org.telegram.messenger.FileLoader.getDirectory(r6);	 Catch:{ Exception -> 0x00e3 }
        r7 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x00e3 }
        r7.<init>();	 Catch:{ Exception -> 0x00e3 }
        r8 = org.telegram.messenger.Utilities.random;	 Catch:{ Exception -> 0x00e3 }
        r8 = r8.nextInt();	 Catch:{ Exception -> 0x00e3 }
        r7 = r7.append(r8);	 Catch:{ Exception -> 0x00e3 }
        r8 = ".jpg";
        r7 = r7.append(r8);	 Catch:{ Exception -> 0x00e3 }
        r7 = r7.toString();	 Catch:{ Exception -> 0x00e3 }
        r5.<init>(r6, r7);	 Catch:{ Exception -> 0x00e3 }
        r11.currentWallpaperPath = r5;	 Catch:{ Exception -> 0x00e3 }
        r2 = org.telegram.messenger.AndroidUtilities.getRealScreenSize();	 Catch:{ Exception -> 0x00e3 }
        r5 = 0;
        r6 = r14.getData();	 Catch:{ Exception -> 0x00e3 }
        r7 = r2.x;	 Catch:{ Exception -> 0x00e3 }
        r7 = (float) r7;	 Catch:{ Exception -> 0x00e3 }
        r8 = r2.y;	 Catch:{ Exception -> 0x00e3 }
        r8 = (float) r8;	 Catch:{ Exception -> 0x00e3 }
        r9 = 1;
        r0 = org.telegram.messenger.ImageLoader.loadBitmap(r5, r6, r7, r8, r9);	 Catch:{ Exception -> 0x00e3 }
        r3 = new java.io.FileOutputStream;	 Catch:{ Exception -> 0x00e3 }
        r5 = r11.currentWallpaperPath;	 Catch:{ Exception -> 0x00e3 }
        r3.<init>(r5);	 Catch:{ Exception -> 0x00e3 }
        r5 = android.graphics.Bitmap.CompressFormat.JPEG;	 Catch:{ Exception -> 0x00e3 }
        r6 = 87;
        r0.compress(r5, r6, r3);	 Catch:{ Exception -> 0x00e3 }
        r5 = r11.delegate;	 Catch:{ Exception -> 0x00e3 }
        r6 = r11.currentWallpaperPath;	 Catch:{ Exception -> 0x00e3 }
        r7 = 0;
        r5.didSelectWallpaper(r6, r0, r7);	 Catch:{ Exception -> 0x00e3 }
        goto L_0x0064;
    L_0x00e3:
        r1 = move-exception;
        org.telegram.messenger.FileLog.e(r1);
        goto L_0x0064;
    L_0x00e9:
        r5 = move-exception;
        r3 = r4;
        goto L_0x007b;
    L_0x00ec:
        r1 = move-exception;
        r3 = r4;
        goto L_0x006c;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.WallpaperUpdater.onActivityResult(int, int, android.content.Intent):void");
    }
}
