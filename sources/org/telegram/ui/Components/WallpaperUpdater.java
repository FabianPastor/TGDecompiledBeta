package org.telegram.ui.Components;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build.VERSION;
import androidx.core.content.FileProvider;
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

    public void cleanup() {
    }

    public WallpaperUpdater(Activity activity, BaseFragment baseFragment, WallpaperUpdaterDelegate wallpaperUpdaterDelegate) {
        this.parentActivity = activity;
        this.parentFragment = baseFragment;
        this.delegate = wallpaperUpdaterDelegate;
    }

    public void showAlert(boolean z) {
        CharSequence[] charSequenceArr;
        int[] iArr;
        Builder builder = new Builder(this.parentActivity);
        builder.setTitle(LocaleController.getString("ChoosePhoto", NUM));
        String str = "SelectFromGallery";
        String str2 = "ChooseTakePhoto";
        if (z) {
            charSequenceArr = new CharSequence[]{LocaleController.getString(str2, NUM), LocaleController.getString(str, NUM), LocaleController.getString("SelectColor", NUM), LocaleController.getString("Default", NUM)};
            iArr = null;
        } else {
            charSequenceArr = new CharSequence[]{LocaleController.getString(str2, NUM), LocaleController.getString(str, NUM)};
            iArr = new int[]{NUM, NUM};
        }
        builder.setItems(charSequenceArr, iArr, new -$$Lambda$WallpaperUpdater$vK87huFk9jAjs7SXqPz2knBtx88(this, z));
        builder.show();
    }

    public /* synthetic */ void lambda$showAlert$0$WallpaperUpdater(boolean z, DialogInterface dialogInterface, int i) {
        if (i == 0) {
            try {
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                File generatePicturePath = AndroidUtilities.generatePicturePath();
                if (generatePicturePath != null) {
                    String str = "output";
                    if (VERSION.SDK_INT >= 24) {
                        intent.putExtra(str, FileProvider.getUriForFile(this.parentActivity, "org.telegram.messenger.beta.provider", generatePicturePath));
                        intent.addFlags(2);
                        intent.addFlags(1);
                    } else {
                        intent.putExtra(str, Uri.fromFile(generatePicturePath));
                    }
                    this.currentPicturePath = generatePicturePath.getAbsolutePath();
                }
                this.parentActivity.startActivityForResult(intent, 10);
            } catch (Exception e) {
                try {
                    FileLog.e(e);
                } catch (Exception e2) {
                    FileLog.e(e2);
                }
            }
        } else if (i == 1) {
            openGallery();
        } else if (!z) {
        } else {
            if (i == 2) {
                this.delegate.needOpenColorPicker();
            } else if (i == 3) {
                this.delegate.didSelectWallpaper(null, null, false);
            }
        }
    }

    public void openGallery() {
        BaseFragment baseFragment = this.parentFragment;
        if (baseFragment != null) {
            if (VERSION.SDK_INT >= 23 && baseFragment.getParentActivity() != null) {
                if (this.parentFragment.getParentActivity().checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != 0) {
                    this.parentFragment.getParentActivity().requestPermissions(new String[]{r1}, 4);
                    return;
                }
            }
            PhotoAlbumPickerActivity photoAlbumPickerActivity = new PhotoAlbumPickerActivity(2, false, false, null);
            photoAlbumPickerActivity.setAllowSearchImages(false);
            photoAlbumPickerActivity.setDelegate(new PhotoAlbumPickerActivityDelegate() {
                public void didSelectPhotos(ArrayList<SendingMediaInfo> arrayList, boolean z, int i) {
                    WallpaperUpdater.this.didSelectPhotos(arrayList);
                }

                public void startPhotoSelectActivity() {
                    try {
                        Intent intent = new Intent("android.intent.action.PICK");
                        intent.setType("image/*");
                        WallpaperUpdater.this.parentActivity.startActivityForResult(intent, 11);
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                }
            });
            this.parentFragment.presentFragment(photoAlbumPickerActivity);
        } else {
            Intent intent = new Intent("android.intent.action.PICK");
            intent.setType("image/*");
            this.parentActivity.startActivityForResult(intent, 11);
        }
    }

    private void didSelectPhotos(ArrayList<SendingMediaInfo> arrayList) {
        try {
            if (!arrayList.isEmpty()) {
                SendingMediaInfo sendingMediaInfo = (SendingMediaInfo) arrayList.get(0);
                if (sendingMediaInfo.path != null) {
                    File directory = FileLoader.getDirectory(4);
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(Utilities.random.nextInt());
                    stringBuilder.append(".jpg");
                    this.currentWallpaperPath = new File(directory, stringBuilder.toString());
                    Point realScreenSize = AndroidUtilities.getRealScreenSize();
                    Bitmap loadBitmap = ImageLoader.loadBitmap(sendingMediaInfo.path, null, (float) realScreenSize.x, (float) realScreenSize.y, true);
                    loadBitmap.compress(CompressFormat.JPEG, 87, new FileOutputStream(this.currentWallpaperPath));
                    this.delegate.didSelectWallpaper(this.currentWallpaperPath, loadBitmap, true);
                }
            }
        } catch (Throwable th) {
            FileLog.e(th);
        }
    }

    public String getCurrentPicturePath() {
        return this.currentPicturePath;
    }

    public void setCurrentPicturePath(String str) {
        this.currentPicturePath = str;
    }

    /* JADX WARNING: Removed duplicated region for block: B:18:0x0066 A:{SYNTHETIC, Splitter:B:18:0x0066} */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x0075 A:{SYNTHETIC, Splitter:B:26:0x0075} */
    public void onActivityResult(int r8, int r9, android.content.Intent r10) {
        /*
        r7 = this;
        r0 = -1;
        if (r9 != r0) goto L_0x00d6;
    L_0x0003:
        r9 = 10;
        r0 = 0;
        r1 = 87;
        r2 = 1;
        r3 = ".jpg";
        r4 = 4;
        r5 = 0;
        if (r8 != r9) goto L_0x007e;
    L_0x000f:
        r8 = r7.currentPicturePath;
        org.telegram.messenger.AndroidUtilities.addMediaToGallery(r8);
        r8 = new java.io.File;	 Catch:{ Exception -> 0x005f, all -> 0x005d }
        r9 = org.telegram.messenger.FileLoader.getDirectory(r4);	 Catch:{ Exception -> 0x005f, all -> 0x005d }
        r10 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x005f, all -> 0x005d }
        r10.<init>();	 Catch:{ Exception -> 0x005f, all -> 0x005d }
        r4 = org.telegram.messenger.Utilities.random;	 Catch:{ Exception -> 0x005f, all -> 0x005d }
        r4 = r4.nextInt();	 Catch:{ Exception -> 0x005f, all -> 0x005d }
        r10.append(r4);	 Catch:{ Exception -> 0x005f, all -> 0x005d }
        r10.append(r3);	 Catch:{ Exception -> 0x005f, all -> 0x005d }
        r10 = r10.toString();	 Catch:{ Exception -> 0x005f, all -> 0x005d }
        r8.<init>(r9, r10);	 Catch:{ Exception -> 0x005f, all -> 0x005d }
        r7.currentWallpaperPath = r8;	 Catch:{ Exception -> 0x005f, all -> 0x005d }
        r8 = org.telegram.messenger.AndroidUtilities.getRealScreenSize();	 Catch:{ Exception -> 0x005f, all -> 0x005d }
        r9 = r7.currentPicturePath;	 Catch:{ Exception -> 0x005f, all -> 0x005d }
        r10 = r8.x;	 Catch:{ Exception -> 0x005f, all -> 0x005d }
        r10 = (float) r10;	 Catch:{ Exception -> 0x005f, all -> 0x005d }
        r8 = r8.y;	 Catch:{ Exception -> 0x005f, all -> 0x005d }
        r8 = (float) r8;	 Catch:{ Exception -> 0x005f, all -> 0x005d }
        r8 = org.telegram.messenger.ImageLoader.loadBitmap(r9, r5, r10, r8, r2);	 Catch:{ Exception -> 0x005f, all -> 0x005d }
        r9 = new java.io.FileOutputStream;	 Catch:{ Exception -> 0x005f, all -> 0x005d }
        r10 = r7.currentWallpaperPath;	 Catch:{ Exception -> 0x005f, all -> 0x005d }
        r9.<init>(r10);	 Catch:{ Exception -> 0x005f, all -> 0x005d }
        r10 = android.graphics.Bitmap.CompressFormat.JPEG;	 Catch:{ Exception -> 0x005b }
        r8.compress(r10, r1, r9);	 Catch:{ Exception -> 0x005b }
        r10 = r7.delegate;	 Catch:{ Exception -> 0x005b }
        r1 = r7.currentWallpaperPath;	 Catch:{ Exception -> 0x005b }
        r10.didSelectWallpaper(r1, r8, r0);	 Catch:{ Exception -> 0x005b }
        r9.close();	 Catch:{ Exception -> 0x006a }
        goto L_0x006e;
    L_0x005b:
        r8 = move-exception;
        goto L_0x0061;
    L_0x005d:
        r8 = move-exception;
        goto L_0x0073;
    L_0x005f:
        r8 = move-exception;
        r9 = r5;
    L_0x0061:
        org.telegram.messenger.FileLog.e(r8);	 Catch:{ all -> 0x0071 }
        if (r9 == 0) goto L_0x006e;
    L_0x0066:
        r9.close();	 Catch:{ Exception -> 0x006a }
        goto L_0x006e;
    L_0x006a:
        r8 = move-exception;
        org.telegram.messenger.FileLog.e(r8);
    L_0x006e:
        r7.currentPicturePath = r5;
        goto L_0x00d6;
    L_0x0071:
        r8 = move-exception;
        r5 = r9;
    L_0x0073:
        if (r5 == 0) goto L_0x007d;
    L_0x0075:
        r5.close();	 Catch:{ Exception -> 0x0079 }
        goto L_0x007d;
    L_0x0079:
        r9 = move-exception;
        org.telegram.messenger.FileLog.e(r9);
    L_0x007d:
        throw r8;
    L_0x007e:
        r9 = 11;
        if (r8 != r9) goto L_0x00d6;
    L_0x0082:
        if (r10 == 0) goto L_0x00d6;
    L_0x0084:
        r8 = r10.getData();
        if (r8 != 0) goto L_0x008b;
    L_0x008a:
        goto L_0x00d6;
    L_0x008b:
        r8 = new java.io.File;	 Catch:{ Exception -> 0x00d1 }
        r9 = org.telegram.messenger.FileLoader.getDirectory(r4);	 Catch:{ Exception -> 0x00d1 }
        r4 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x00d1 }
        r4.<init>();	 Catch:{ Exception -> 0x00d1 }
        r6 = org.telegram.messenger.Utilities.random;	 Catch:{ Exception -> 0x00d1 }
        r6 = r6.nextInt();	 Catch:{ Exception -> 0x00d1 }
        r4.append(r6);	 Catch:{ Exception -> 0x00d1 }
        r4.append(r3);	 Catch:{ Exception -> 0x00d1 }
        r3 = r4.toString();	 Catch:{ Exception -> 0x00d1 }
        r8.<init>(r9, r3);	 Catch:{ Exception -> 0x00d1 }
        r7.currentWallpaperPath = r8;	 Catch:{ Exception -> 0x00d1 }
        r8 = org.telegram.messenger.AndroidUtilities.getRealScreenSize();	 Catch:{ Exception -> 0x00d1 }
        r9 = r10.getData();	 Catch:{ Exception -> 0x00d1 }
        r10 = r8.x;	 Catch:{ Exception -> 0x00d1 }
        r10 = (float) r10;	 Catch:{ Exception -> 0x00d1 }
        r8 = r8.y;	 Catch:{ Exception -> 0x00d1 }
        r8 = (float) r8;	 Catch:{ Exception -> 0x00d1 }
        r8 = org.telegram.messenger.ImageLoader.loadBitmap(r5, r9, r10, r8, r2);	 Catch:{ Exception -> 0x00d1 }
        r9 = new java.io.FileOutputStream;	 Catch:{ Exception -> 0x00d1 }
        r10 = r7.currentWallpaperPath;	 Catch:{ Exception -> 0x00d1 }
        r9.<init>(r10);	 Catch:{ Exception -> 0x00d1 }
        r10 = android.graphics.Bitmap.CompressFormat.JPEG;	 Catch:{ Exception -> 0x00d1 }
        r8.compress(r10, r1, r9);	 Catch:{ Exception -> 0x00d1 }
        r9 = r7.delegate;	 Catch:{ Exception -> 0x00d1 }
        r10 = r7.currentWallpaperPath;	 Catch:{ Exception -> 0x00d1 }
        r9.didSelectWallpaper(r10, r8, r0);	 Catch:{ Exception -> 0x00d1 }
        goto L_0x00d6;
    L_0x00d1:
        r8 = move-exception;
        org.telegram.messenger.FileLog.e(r8);
    L_0x00d6:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.WallpaperUpdater.onActivityResult(int, int, android.content.Intent):void");
    }
}
