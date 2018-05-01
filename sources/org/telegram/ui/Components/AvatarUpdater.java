package org.telegram.ui.Components;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.media.ExifInterface;
import android.support.v4.content.FileProvider;
import java.io.File;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.MediaController.PhotoEntry;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.SendMessagesHelper.SendingMediaInfo;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.tgnet.TLRPC.InputFile;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.PhotoAlbumPickerActivity;
import org.telegram.ui.PhotoAlbumPickerActivity.PhotoAlbumPickerActivityDelegate;
import org.telegram.ui.PhotoCropActivity;
import org.telegram.ui.PhotoCropActivity.PhotoEditActivityDelegate;
import org.telegram.ui.PhotoViewer;
import org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider;

public class AvatarUpdater implements NotificationCenterDelegate, PhotoEditActivityDelegate {
    private PhotoSize bigPhoto;
    private boolean clearAfterUpdate = false;
    private int currentAccount = UserConfig.selectedAccount;
    public String currentPicturePath;
    public AvatarUpdaterDelegate delegate;
    public BaseFragment parentFragment = null;
    File picturePath = null;
    public boolean returnOnly = false;
    private PhotoSize smallPhoto;
    public String uploadingAvatar = null;

    public interface AvatarUpdaterDelegate {
        void didUploadedPhoto(InputFile inputFile, PhotoSize photoSize, PhotoSize photoSize2);
    }

    /* renamed from: org.telegram.ui.Components.AvatarUpdater$1 */
    class C20421 implements PhotoAlbumPickerActivityDelegate {
        C20421() {
        }

        public void didSelectPhotos(ArrayList<SendingMediaInfo> arrayList) {
            if (!arrayList.isEmpty()) {
                AvatarUpdater.this.processBitmap(ImageLoader.loadBitmap(((SendingMediaInfo) arrayList.get(0)).path, null, 800.0f, 800.0f, true));
            }
        }

        public void startPhotoSelectActivity() {
            try {
                Intent intent = new Intent("android.intent.action.GET_CONTENT");
                intent.setType("image/*");
                AvatarUpdater.this.parentFragment.startActivityForResult(intent, 14);
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
        }
    }

    public void clear() {
        if (this.uploadingAvatar != null) {
            this.clearAfterUpdate = true;
            return;
        }
        this.parentFragment = null;
        this.delegate = null;
    }

    public void openCamera() {
        if (this.parentFragment != null) {
            if (this.parentFragment.getParentActivity() != null) {
                try {
                    if (VERSION.SDK_INT < 23 || this.parentFragment.getParentActivity().checkSelfPermission("android.permission.CAMERA") == 0) {
                        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                        File generatePicturePath = AndroidUtilities.generatePicturePath();
                        if (generatePicturePath != null) {
                            if (VERSION.SDK_INT >= 24) {
                                intent.putExtra("output", FileProvider.getUriForFile(this.parentFragment.getParentActivity(), "org.telegram.messenger.provider", generatePicturePath));
                                intent.addFlags(2);
                                intent.addFlags(1);
                            } else {
                                intent.putExtra("output", Uri.fromFile(generatePicturePath));
                            }
                            this.currentPicturePath = generatePicturePath.getAbsolutePath();
                        }
                        this.parentFragment.startActivityForResult(intent, 13);
                        return;
                    }
                    this.parentFragment.getParentActivity().requestPermissions(new String[]{"android.permission.CAMERA"}, 19);
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        }
    }

    public void openGallery() {
        if (this.parentFragment != null) {
            if (VERSION.SDK_INT < 23 || this.parentFragment == null || this.parentFragment.getParentActivity() == null || this.parentFragment.getParentActivity().checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == 0) {
                BaseFragment photoAlbumPickerActivity = new PhotoAlbumPickerActivity(true, false, false, null);
                photoAlbumPickerActivity.setDelegate(new C20421());
                this.parentFragment.presentFragment(photoAlbumPickerActivity);
                return;
            }
            this.parentFragment.getParentActivity().requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 4);
        }
    }

    private void startCrop(String str, Uri uri) {
        try {
            LaunchActivity launchActivity = (LaunchActivity) this.parentFragment.getParentActivity();
            if (launchActivity != null) {
                Bundle bundle = new Bundle();
                if (str != null) {
                    bundle.putString("photoPath", str);
                } else if (uri != null) {
                    bundle.putParcelable("photoUri", uri);
                }
                BaseFragment photoCropActivity = new PhotoCropActivity(bundle);
                photoCropActivity.setDelegate(this);
                launchActivity.presentFragment(photoCropActivity);
            }
        } catch (Throwable e) {
            FileLog.m3e(e);
            processBitmap(ImageLoader.loadBitmap(str, uri, 800.0f, 800.0f, true));
        }
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        if (i2 == -1) {
            if (i == 13) {
                PhotoViewer.getInstance().setParentActivity(this.parentFragment.getParentActivity());
                i = 0;
                try {
                    i2 = new ExifInterface(this.currentPicturePath).getAttributeInt("Orientation", 1);
                    if (i2 == 3) {
                        i = 180;
                    } else if (i2 == 6) {
                        i = 90;
                    } else if (i2 == 8) {
                        i = 270;
                    }
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
                int i3 = i;
                i = new ArrayList();
                i.add(new PhotoEntry(0, 0, 0, this.currentPicturePath, i3, false));
                PhotoViewer.getInstance().openPhotoForSelect(i, 0, 1, new EmptyPhotoViewerProvider() {
                    public boolean allowCaption() {
                        return false;
                    }

                    public boolean canScrollAway() {
                        return false;
                    }

                    public void sendButtonPressed(int i, VideoEditedInfo videoEditedInfo) {
                        PhotoEntry photoEntry = (PhotoEntry) i.get(null);
                        i = photoEntry.imagePath != null ? photoEntry.imagePath : photoEntry.path != null ? photoEntry.path : 0;
                        AvatarUpdater.this.processBitmap(ImageLoader.loadBitmap(i, null, 800.0f, 800.0f, true));
                    }
                }, null);
                AndroidUtilities.addMediaToGallery(this.currentPicturePath);
                this.currentPicturePath = null;
            } else {
                if (i == 14 && intent != null) {
                    if (intent.getData() != 0) {
                        startCrop(null, intent.getData());
                    }
                }
            }
        }
    }

    private void processBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            this.smallPhoto = ImageLoader.scaleAndSaveImage(bitmap, 100.0f, 100.0f, 80, false);
            this.bigPhoto = ImageLoader.scaleAndSaveImage(bitmap, 800.0f, 800.0f, 80, false, 320, 320);
            bitmap.recycle();
            if (!(this.bigPhoto == null || this.smallPhoto == null)) {
                if (this.returnOnly == null) {
                    UserConfig.getInstance(this.currentAccount).saveConfig(false);
                    bitmap = new StringBuilder();
                    bitmap.append(FileLoader.getDirectory(4));
                    bitmap.append("/");
                    bitmap.append(this.bigPhoto.location.volume_id);
                    bitmap.append("_");
                    bitmap.append(this.bigPhoto.location.local_id);
                    bitmap.append(".jpg");
                    this.uploadingAvatar = bitmap.toString();
                    NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.FileDidUpload);
                    NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.FileDidFailUpload);
                    FileLoader.getInstance(this.currentAccount).uploadFile(this.uploadingAvatar, false, true, 16777216);
                } else if (this.delegate != null) {
                    this.delegate.didUploadedPhoto(null, this.smallPhoto, this.bigPhoto);
                }
            }
        }
    }

    public void didFinishEdit(Bitmap bitmap) {
        processBitmap(bitmap);
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        String str;
        if (i == NotificationCenter.FileDidUpload) {
            str = (String) objArr[0];
            if (this.uploadingAvatar != 0 && str.equals(this.uploadingAvatar) != 0) {
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.FileDidUpload);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.FileDidFailUpload);
                if (this.delegate != 0) {
                    this.delegate.didUploadedPhoto((InputFile) objArr[1], this.smallPhoto, this.bigPhoto);
                }
                this.uploadingAvatar = null;
                if (this.clearAfterUpdate != 0) {
                    this.parentFragment = null;
                    this.delegate = null;
                }
            }
        } else if (i == NotificationCenter.FileDidFailUpload) {
            str = (String) objArr[0];
            if (this.uploadingAvatar != 0 && str.equals(this.uploadingAvatar) != 0) {
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.FileDidUpload);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.FileDidFailUpload);
                this.uploadingAvatar = null;
                if (this.clearAfterUpdate != 0) {
                    this.parentFragment = null;
                    this.delegate = null;
                }
            }
        }
    }
}
