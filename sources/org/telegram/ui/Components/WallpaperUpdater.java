package org.telegram.ui.Components;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import androidx.core.content.FileProvider;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.PhotoAlbumPickerActivity;
/* loaded from: classes3.dex */
public class WallpaperUpdater {
    private String currentPicturePath;
    private File currentWallpaperPath;
    private WallpaperUpdaterDelegate delegate;
    private Activity parentActivity;
    private BaseFragment parentFragment;

    /* loaded from: classes3.dex */
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

    public void showAlert(final boolean z) {
        CharSequence[] charSequenceArr;
        int[] iArr;
        BottomSheet.Builder builder = new BottomSheet.Builder(this.parentActivity);
        builder.setTitle(LocaleController.getString("ChoosePhoto", R.string.ChoosePhoto), true);
        if (z) {
            charSequenceArr = new CharSequence[]{LocaleController.getString("ChooseTakePhoto", R.string.ChooseTakePhoto), LocaleController.getString("SelectFromGallery", R.string.SelectFromGallery), LocaleController.getString("SelectColor", R.string.SelectColor), LocaleController.getString("Default", R.string.Default)};
            iArr = null;
        } else {
            charSequenceArr = new CharSequence[]{LocaleController.getString("ChooseTakePhoto", R.string.ChooseTakePhoto), LocaleController.getString("SelectFromGallery", R.string.SelectFromGallery)};
            iArr = new int[]{R.drawable.msg_camera, R.drawable.msg_photos};
        }
        builder.setItems(charSequenceArr, iArr, new DialogInterface.OnClickListener() { // from class: org.telegram.ui.Components.WallpaperUpdater$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                WallpaperUpdater.this.lambda$showAlert$0(z, dialogInterface, i);
            }
        });
        builder.show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showAlert$0(boolean z, DialogInterface dialogInterface, int i) {
        try {
            if (i != 0) {
                if (i == 1) {
                    openGallery();
                    return;
                } else if (!z) {
                    return;
                } else {
                    if (i == 2) {
                        this.delegate.needOpenColorPicker();
                        return;
                    } else if (i != 3) {
                        return;
                    } else {
                        this.delegate.didSelectWallpaper(null, null, false);
                        return;
                    }
                }
            }
            try {
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                File generatePicturePath = AndroidUtilities.generatePicturePath();
                if (generatePicturePath != null) {
                    if (Build.VERSION.SDK_INT >= 24) {
                        Activity activity = this.parentActivity;
                        intent.putExtra("output", FileProvider.getUriForFile(activity, ApplicationLoader.getApplicationId() + ".provider", generatePicturePath));
                        intent.addFlags(2);
                        intent.addFlags(1);
                    } else {
                        intent.putExtra("output", Uri.fromFile(generatePicturePath));
                    }
                    this.currentPicturePath = generatePicturePath.getAbsolutePath();
                }
                this.parentActivity.startActivityForResult(intent, 10);
            } catch (Exception e) {
                FileLog.e(e);
            }
        } catch (Exception e2) {
            FileLog.e(e2);
        }
    }

    public void openGallery() {
        BaseFragment baseFragment = this.parentFragment;
        if (baseFragment != null) {
            if (Build.VERSION.SDK_INT >= 23 && baseFragment.getParentActivity() != null && this.parentFragment.getParentActivity().checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != 0) {
                this.parentFragment.getParentActivity().requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 4);
                return;
            }
            PhotoAlbumPickerActivity photoAlbumPickerActivity = new PhotoAlbumPickerActivity(PhotoAlbumPickerActivity.SELECT_TYPE_WALLPAPER, false, false, null);
            photoAlbumPickerActivity.setAllowSearchImages(false);
            photoAlbumPickerActivity.setDelegate(new PhotoAlbumPickerActivity.PhotoAlbumPickerActivityDelegate() { // from class: org.telegram.ui.Components.WallpaperUpdater.1
                @Override // org.telegram.ui.PhotoAlbumPickerActivity.PhotoAlbumPickerActivityDelegate
                public void didSelectPhotos(ArrayList<SendMessagesHelper.SendingMediaInfo> arrayList, boolean z, int i) {
                    WallpaperUpdater.this.didSelectPhotos(arrayList);
                }

                @Override // org.telegram.ui.PhotoAlbumPickerActivity.PhotoAlbumPickerActivityDelegate
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
            return;
        }
        Intent intent = new Intent("android.intent.action.PICK");
        intent.setType("image/*");
        this.parentActivity.startActivityForResult(intent, 11);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void didSelectPhotos(ArrayList<SendMessagesHelper.SendingMediaInfo> arrayList) {
        try {
            if (arrayList.isEmpty()) {
                return;
            }
            SendMessagesHelper.SendingMediaInfo sendingMediaInfo = arrayList.get(0);
            if (sendingMediaInfo.path == null) {
                return;
            }
            File directory = FileLoader.getDirectory(4);
            this.currentWallpaperPath = new File(directory, Utilities.random.nextInt() + ".jpg");
            android.graphics.Point realScreenSize = AndroidUtilities.getRealScreenSize();
            Bitmap loadBitmap = ImageLoader.loadBitmap(sendingMediaInfo.path, null, (float) realScreenSize.x, (float) realScreenSize.y, true);
            loadBitmap.compress(Bitmap.CompressFormat.JPEG, 87, new FileOutputStream(this.currentWallpaperPath));
            this.delegate.didSelectWallpaper(this.currentWallpaperPath, loadBitmap, true);
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

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:50:0x0075 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Type inference failed for: r10v12, types: [org.telegram.ui.Components.WallpaperUpdater$WallpaperUpdaterDelegate] */
    /* JADX WARN: Type inference failed for: r8v19, types: [android.graphics.Bitmap] */
    /* JADX WARN: Type inference failed for: r9v1 */
    /* JADX WARN: Type inference failed for: r9v10 */
    /* JADX WARN: Type inference failed for: r9v11 */
    /* JADX WARN: Type inference failed for: r9v12, types: [java.io.FileOutputStream] */
    /* JADX WARN: Type inference failed for: r9v13 */
    /* JADX WARN: Type inference failed for: r9v16, types: [java.io.OutputStream, java.io.FileOutputStream] */
    /* JADX WARN: Type inference failed for: r9v17 */
    /* JADX WARN: Type inference failed for: r9v18 */
    /* JADX WARN: Type inference failed for: r9v19 */
    /* JADX WARN: Type inference failed for: r9v7 */
    /* JADX WARN: Type inference failed for: r9v9 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void onActivityResult(int r8, int r9, android.content.Intent r10) {
        /*
            r7 = this;
            r0 = -1
            if (r9 != r0) goto Ld6
            r9 = 10
            r0 = 0
            r1 = 87
            r2 = 1
            java.lang.String r3 = ".jpg"
            r4 = 4
            r5 = 0
            if (r8 != r9) goto L7e
            java.lang.String r8 = r7.currentPicturePath
            org.telegram.messenger.AndroidUtilities.addMediaToGallery(r8)
            java.io.File r8 = new java.io.File     // Catch: java.lang.Throwable -> L5d java.lang.Exception -> L5f
            java.io.File r9 = org.telegram.messenger.FileLoader.getDirectory(r4)     // Catch: java.lang.Throwable -> L5d java.lang.Exception -> L5f
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L5d java.lang.Exception -> L5f
            r10.<init>()     // Catch: java.lang.Throwable -> L5d java.lang.Exception -> L5f
            java.security.SecureRandom r4 = org.telegram.messenger.Utilities.random     // Catch: java.lang.Throwable -> L5d java.lang.Exception -> L5f
            int r4 = r4.nextInt()     // Catch: java.lang.Throwable -> L5d java.lang.Exception -> L5f
            r10.append(r4)     // Catch: java.lang.Throwable -> L5d java.lang.Exception -> L5f
            r10.append(r3)     // Catch: java.lang.Throwable -> L5d java.lang.Exception -> L5f
            java.lang.String r10 = r10.toString()     // Catch: java.lang.Throwable -> L5d java.lang.Exception -> L5f
            r8.<init>(r9, r10)     // Catch: java.lang.Throwable -> L5d java.lang.Exception -> L5f
            r7.currentWallpaperPath = r8     // Catch: java.lang.Throwable -> L5d java.lang.Exception -> L5f
            android.graphics.Point r8 = org.telegram.messenger.AndroidUtilities.getRealScreenSize()     // Catch: java.lang.Throwable -> L5d java.lang.Exception -> L5f
            java.lang.String r9 = r7.currentPicturePath     // Catch: java.lang.Throwable -> L5d java.lang.Exception -> L5f
            int r10 = r8.x     // Catch: java.lang.Throwable -> L5d java.lang.Exception -> L5f
            float r10 = (float) r10     // Catch: java.lang.Throwable -> L5d java.lang.Exception -> L5f
            int r8 = r8.y     // Catch: java.lang.Throwable -> L5d java.lang.Exception -> L5f
            float r8 = (float) r8     // Catch: java.lang.Throwable -> L5d java.lang.Exception -> L5f
            android.graphics.Bitmap r8 = org.telegram.messenger.ImageLoader.loadBitmap(r9, r5, r10, r8, r2)     // Catch: java.lang.Throwable -> L5d java.lang.Exception -> L5f
            java.io.FileOutputStream r9 = new java.io.FileOutputStream     // Catch: java.lang.Throwable -> L5d java.lang.Exception -> L5f
            java.io.File r10 = r7.currentWallpaperPath     // Catch: java.lang.Throwable -> L5d java.lang.Exception -> L5f
            r9.<init>(r10)     // Catch: java.lang.Throwable -> L5d java.lang.Exception -> L5f
            android.graphics.Bitmap$CompressFormat r10 = android.graphics.Bitmap.CompressFormat.JPEG     // Catch: java.lang.Exception -> L5b java.lang.Throwable -> L71
            r8.compress(r10, r1, r9)     // Catch: java.lang.Exception -> L5b java.lang.Throwable -> L71
            org.telegram.ui.Components.WallpaperUpdater$WallpaperUpdaterDelegate r10 = r7.delegate     // Catch: java.lang.Exception -> L5b java.lang.Throwable -> L71
            java.io.File r1 = r7.currentWallpaperPath     // Catch: java.lang.Exception -> L5b java.lang.Throwable -> L71
            r10.didSelectWallpaper(r1, r8, r0)     // Catch: java.lang.Exception -> L5b java.lang.Throwable -> L71
            r9.close()     // Catch: java.lang.Exception -> L6a
            goto L6e
        L5b:
            r8 = move-exception
            goto L61
        L5d:
            r8 = move-exception
            goto L73
        L5f:
            r8 = move-exception
            r9 = r5
        L61:
            org.telegram.messenger.FileLog.e(r8)     // Catch: java.lang.Throwable -> L71
            if (r9 == 0) goto L6e
            r9.close()     // Catch: java.lang.Exception -> L6a
            goto L6e
        L6a:
            r8 = move-exception
            org.telegram.messenger.FileLog.e(r8)
        L6e:
            r7.currentPicturePath = r5
            goto Ld6
        L71:
            r8 = move-exception
            r5 = r9
        L73:
            if (r5 == 0) goto L7d
            r5.close()     // Catch: java.lang.Exception -> L79
            goto L7d
        L79:
            r9 = move-exception
            org.telegram.messenger.FileLog.e(r9)
        L7d:
            throw r8
        L7e:
            r9 = 11
            if (r8 != r9) goto Ld6
            if (r10 == 0) goto Ld6
            android.net.Uri r8 = r10.getData()
            if (r8 != 0) goto L8b
            goto Ld6
        L8b:
            java.io.File r8 = new java.io.File     // Catch: java.lang.Exception -> Ld1
            java.io.File r9 = org.telegram.messenger.FileLoader.getDirectory(r4)     // Catch: java.lang.Exception -> Ld1
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch: java.lang.Exception -> Ld1
            r4.<init>()     // Catch: java.lang.Exception -> Ld1
            java.security.SecureRandom r6 = org.telegram.messenger.Utilities.random     // Catch: java.lang.Exception -> Ld1
            int r6 = r6.nextInt()     // Catch: java.lang.Exception -> Ld1
            r4.append(r6)     // Catch: java.lang.Exception -> Ld1
            r4.append(r3)     // Catch: java.lang.Exception -> Ld1
            java.lang.String r3 = r4.toString()     // Catch: java.lang.Exception -> Ld1
            r8.<init>(r9, r3)     // Catch: java.lang.Exception -> Ld1
            r7.currentWallpaperPath = r8     // Catch: java.lang.Exception -> Ld1
            android.graphics.Point r8 = org.telegram.messenger.AndroidUtilities.getRealScreenSize()     // Catch: java.lang.Exception -> Ld1
            android.net.Uri r9 = r10.getData()     // Catch: java.lang.Exception -> Ld1
            int r10 = r8.x     // Catch: java.lang.Exception -> Ld1
            float r10 = (float) r10     // Catch: java.lang.Exception -> Ld1
            int r8 = r8.y     // Catch: java.lang.Exception -> Ld1
            float r8 = (float) r8     // Catch: java.lang.Exception -> Ld1
            android.graphics.Bitmap r8 = org.telegram.messenger.ImageLoader.loadBitmap(r5, r9, r10, r8, r2)     // Catch: java.lang.Exception -> Ld1
            java.io.FileOutputStream r9 = new java.io.FileOutputStream     // Catch: java.lang.Exception -> Ld1
            java.io.File r10 = r7.currentWallpaperPath     // Catch: java.lang.Exception -> Ld1
            r9.<init>(r10)     // Catch: java.lang.Exception -> Ld1
            android.graphics.Bitmap$CompressFormat r10 = android.graphics.Bitmap.CompressFormat.JPEG     // Catch: java.lang.Exception -> Ld1
            r8.compress(r10, r1, r9)     // Catch: java.lang.Exception -> Ld1
            org.telegram.ui.Components.WallpaperUpdater$WallpaperUpdaterDelegate r9 = r7.delegate     // Catch: java.lang.Exception -> Ld1
            java.io.File r10 = r7.currentWallpaperPath     // Catch: java.lang.Exception -> Ld1
            r9.didSelectWallpaper(r10, r8, r0)     // Catch: java.lang.Exception -> Ld1
            goto Ld6
        Ld1:
            r8 = move-exception
            org.telegram.messenger.FileLog.e(r8)
        Ld6:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.WallpaperUpdater.onActivityResult(int, int, android.content.Intent):void");
    }
}
