package org.telegram.ui.Components;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
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

    public void clear() {
        if (this.uploadingAvatar != null) {
            this.clearAfterUpdate = true;
            return;
        }
        this.parentFragment = null;
        this.delegate = null;
    }

    public void openCamera() {
        if (this.parentFragment != null && this.parentFragment.getParentActivity() != null) {
            try {
                if (VERSION.SDK_INT < 23 || this.parentFragment.getParentActivity().checkSelfPermission("android.permission.CAMERA") == 0) {
                    Intent takePictureIntent = new Intent("android.media.action.IMAGE_CAPTURE");
                    File image = AndroidUtilities.generatePicturePath();
                    if (image != null) {
                        if (VERSION.SDK_INT >= 24) {
                            takePictureIntent.putExtra("output", FileProvider.getUriForFile(this.parentFragment.getParentActivity(), "org.telegram.messenger.beta.provider", image));
                            takePictureIntent.addFlags(2);
                            takePictureIntent.addFlags(1);
                        } else {
                            takePictureIntent.putExtra("output", Uri.fromFile(image));
                        }
                        this.currentPicturePath = image.getAbsolutePath();
                    }
                    this.parentFragment.startActivityForResult(takePictureIntent, 13);
                    return;
                }
                this.parentFragment.getParentActivity().requestPermissions(new String[]{"android.permission.CAMERA"}, 19);
            } catch (Throwable e) {
                FileLog.e(e);
            }
        }
    }

    public void openGallery() {
        if (VERSION.SDK_INT < 23 || this.parentFragment == null || this.parentFragment.getParentActivity() == null || this.parentFragment.getParentActivity().checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == 0) {
            PhotoAlbumPickerActivity fragment = new PhotoAlbumPickerActivity(true, false, false, null);
            fragment.setDelegate(new PhotoAlbumPickerActivityDelegate() {
                public void didSelectPhotos(ArrayList<SendingMediaInfo> photos) {
                    if (!photos.isEmpty()) {
                        AvatarUpdater.this.processBitmap(ImageLoader.loadBitmap(((SendingMediaInfo) photos.get(0)).path, null, 800.0f, 800.0f, true));
                    }
                }

                public void startPhotoSelectActivity() {
                    try {
                        Intent photoPickerIntent = new Intent("android.intent.action.GET_CONTENT");
                        photoPickerIntent.setType("image/*");
                        AvatarUpdater.this.parentFragment.startActivityForResult(photoPickerIntent, 14);
                    } catch (Throwable e) {
                        FileLog.e(e);
                    }
                }
            });
            this.parentFragment.presentFragment(fragment);
            return;
        }
        this.parentFragment.getParentActivity().requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 4);
    }

    private void startCrop(String path, Uri uri) {
        try {
            LaunchActivity activity = (LaunchActivity) this.parentFragment.getParentActivity();
            if (activity != null) {
                Bundle args = new Bundle();
                if (path != null) {
                    args.putString("photoPath", path);
                } else if (uri != null) {
                    args.putParcelable("photoUri", uri);
                }
                PhotoCropActivity photoCropActivity = new PhotoCropActivity(args);
                photoCropActivity.setDelegate(this);
                activity.presentFragment(photoCropActivity);
            }
        } catch (Throwable e) {
            FileLog.e(e);
            processBitmap(ImageLoader.loadBitmap(path, uri, 800.0f, 800.0f, true));
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != -1) {
            return;
        }
        if (requestCode == 13) {
            PhotoViewer.getInstance().setParentActivity(this.parentFragment.getParentActivity());
            int orientation = 0;
            try {
                switch (new ExifInterface(this.currentPicturePath).getAttributeInt("Orientation", 1)) {
                    case 3:
                        orientation = 180;
                        break;
                    case 6:
                        orientation = 90;
                        break;
                    case 8:
                        orientation = 270;
                        break;
                }
            } catch (Throwable e) {
                FileLog.e(e);
            }
            final ArrayList<Object> arrayList = new ArrayList();
            arrayList.add(new PhotoEntry(0, 0, 0, this.currentPicturePath, orientation, false));
            PhotoViewer.getInstance().openPhotoForSelect(arrayList, 0, 1, new EmptyPhotoViewerProvider() {
                public void sendButtonPressed(int index, VideoEditedInfo videoEditedInfo) {
                    String path = null;
                    PhotoEntry photoEntry = (PhotoEntry) arrayList.get(0);
                    if (photoEntry.imagePath != null) {
                        path = photoEntry.imagePath;
                    } else if (photoEntry.path != null) {
                        path = photoEntry.path;
                    }
                    AvatarUpdater.this.processBitmap(ImageLoader.loadBitmap(path, null, 800.0f, 800.0f, true));
                }

                public boolean allowCaption() {
                    return false;
                }

                public boolean canScrollAway() {
                    return false;
                }
            }, null);
            AndroidUtilities.addMediaToGallery(this.currentPicturePath);
            this.currentPicturePath = null;
        } else if (requestCode == 14 && data != null && data.getData() != null) {
            startCrop(null, data.getData());
        }
    }

    private void processBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            this.smallPhoto = ImageLoader.scaleAndSaveImage(bitmap, 100.0f, 100.0f, 80, false);
            this.bigPhoto = ImageLoader.scaleAndSaveImage(bitmap, 800.0f, 800.0f, 80, false, 320, 320);
            bitmap.recycle();
            if (this.bigPhoto != null && this.smallPhoto != null) {
                if (!this.returnOnly) {
                    UserConfig.getInstance(this.currentAccount).saveConfig(false);
                    this.uploadingAvatar = FileLoader.getDirectory(4) + "/" + this.bigPhoto.location.volume_id + "_" + this.bigPhoto.location.local_id + ".jpg";
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

    public void didReceivedNotification(int id, int account, Object... args) {
        String location;
        if (id == NotificationCenter.FileDidUpload) {
            location = args[0];
            if (this.uploadingAvatar != null && location.equals(this.uploadingAvatar)) {
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.FileDidUpload);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.FileDidFailUpload);
                if (this.delegate != null) {
                    this.delegate.didUploadedPhoto((InputFile) args[1], this.smallPhoto, this.bigPhoto);
                }
                this.uploadingAvatar = null;
                if (this.clearAfterUpdate) {
                    this.parentFragment = null;
                    this.delegate = null;
                }
            }
        } else if (id == NotificationCenter.FileDidFailUpload) {
            location = (String) args[0];
            if (this.uploadingAvatar != null && location.equals(this.uploadingAvatar)) {
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.FileDidUpload);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.FileDidFailUpload);
                this.uploadingAvatar = null;
                if (this.clearAfterUpdate) {
                    this.parentFragment = null;
                    this.delegate = null;
                }
            }
        }
    }
}
