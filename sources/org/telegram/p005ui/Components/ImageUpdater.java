package org.telegram.p005ui.Components;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.media.ExifInterface;
import android.support.p000v4.content.FileProvider;
import android.widget.TextView;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.CLASSNAMER;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController.PhotoEntry;
import org.telegram.messenger.MediaController.SearchImage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.SendMessagesHelper.SendingMediaInfo;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.p005ui.ActionBar.BaseFragment;
import org.telegram.p005ui.ActionBar.BottomSheet;
import org.telegram.p005ui.ActionBar.BottomSheet.Builder;
import org.telegram.p005ui.ActionBar.Theme;
import org.telegram.p005ui.LaunchActivity;
import org.telegram.p005ui.PhotoAlbumPickerActivity;
import org.telegram.p005ui.PhotoAlbumPickerActivity.PhotoAlbumPickerActivityDelegate;
import org.telegram.p005ui.PhotoCropActivity;
import org.telegram.p005ui.PhotoCropActivity.PhotoEditActivityDelegate;
import org.telegram.p005ui.PhotoPickerActivity;
import org.telegram.p005ui.PhotoPickerActivity.PhotoPickerActivityDelegate;
import org.telegram.p005ui.PhotoViewer;
import org.telegram.p005ui.PhotoViewer.EmptyPhotoViewerProvider;
import org.telegram.tgnet.TLRPC.InputFile;
import org.telegram.tgnet.TLRPC.PhotoSize;

/* renamed from: org.telegram.ui.Components.ImageUpdater */
public class ImageUpdater implements NotificationCenterDelegate, PhotoEditActivityDelegate {
    private PhotoSize bigPhoto;
    private boolean clearAfterUpdate;
    private int currentAccount = UserConfig.selectedAccount;
    public String currentPicturePath;
    public ImageUpdaterDelegate delegate;
    private String finalPath;
    private ImageReceiver imageReceiver = new ImageReceiver(null);
    public BaseFragment parentFragment;
    private File picturePath = null;
    private PhotoSize smallPhoto;
    public String uploadingImage;

    /* renamed from: org.telegram.ui.Components.ImageUpdater$ImageUpdaterDelegate */
    public interface ImageUpdaterDelegate {
        void didUploadPhoto(InputFile inputFile, PhotoSize photoSize, PhotoSize photoSize2);
    }

    /* renamed from: org.telegram.ui.Components.ImageUpdater$2 */
    class CLASSNAME implements PhotoAlbumPickerActivityDelegate {
        CLASSNAME() {
        }

        public void didSelectPhotos(ArrayList<SendingMediaInfo> photos) {
            ImageUpdater.this.didSelectPhotos(photos);
        }

        public void startPhotoSelectActivity() {
            try {
                Intent photoPickerIntent = new Intent("android.intent.action.GET_CONTENT");
                photoPickerIntent.setType("image/*");
                ImageUpdater.this.parentFragment.startActivityForResult(photoPickerIntent, 14);
            } catch (Throwable e) {
                FileLog.m13e(e);
            }
        }
    }

    public void clear() {
        if (this.uploadingImage != null) {
            this.clearAfterUpdate = true;
            return;
        }
        this.parentFragment = null;
        this.delegate = null;
    }

    public void openMenu(boolean hasAvatar, Runnable onDeleteAvatar) {
        if (this.parentFragment != null && this.parentFragment.getParentActivity() != null) {
            CharSequence[] items;
            int[] icons;
            Builder builder = new Builder(this.parentFragment.getParentActivity());
            builder.setTitle(LocaleController.getString("ChoosePhoto", CLASSNAMER.string.ChoosePhoto));
            if (hasAvatar) {
                items = new CharSequence[]{LocaleController.getString("ChooseTakePhoto", CLASSNAMER.string.FromCamera), LocaleController.getString("ChooseFromGallery", CLASSNAMER.string.FromGalley), LocaleController.getString("ChooseFromSearch", CLASSNAMER.string.ChooseFromSearch), LocaleController.getString("DeletePhoto", CLASSNAMER.string.DeletePhoto)};
                icons = new int[]{CLASSNAMER.drawable.menu_camera, CLASSNAMER.drawable.profile_photos, CLASSNAMER.drawable.menu_search, CLASSNAMER.drawable.chats_delete};
            } else {
                items = new CharSequence[]{LocaleController.getString("ChooseTakePhoto", CLASSNAMER.string.FromCamera), LocaleController.getString("ChooseFromGallery", CLASSNAMER.string.FromGalley), LocaleController.getString("ChooseFromSearch", CLASSNAMER.string.ChooseFromSearch)};
                icons = new int[]{CLASSNAMER.drawable.menu_camera, CLASSNAMER.drawable.profile_photos, CLASSNAMER.drawable.menu_search};
            }
            builder.setItems(items, icons, new ImageUpdater$$Lambda$0(this, onDeleteAvatar));
            BottomSheet sheet = builder.create();
            this.parentFragment.showDialog(sheet);
            TextView titleView = sheet.getTitleView();
            titleView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            titleView.setTextSize(1, 18.0f);
            titleView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
            sheet.setItemColor(3, Theme.getColor(Theme.key_dialogTextRed2), Theme.getColor(Theme.key_dialogRedIcon));
        }
    }

    final /* synthetic */ void lambda$openMenu$0$ImageUpdater(Runnable onDeleteAvatar, DialogInterface dialogInterface, int i) {
        if (i == 0) {
            openCamera();
        } else if (i == 1) {
            openGallery();
        } else if (i == 2) {
            openSearch();
        } else if (i == 3) {
            onDeleteAvatar.run();
        }
    }

    public void openSearch() {
        if (this.parentFragment != null) {
            final HashMap<Object, Object> photos = new HashMap();
            final ArrayList<Object> order = new ArrayList();
            PhotoPickerActivity fragment = new PhotoPickerActivity(0, null, photos, order, new ArrayList(), true, false, null);
            fragment.setDelegate(new PhotoPickerActivityDelegate() {
                private boolean sendPressed;

                public void selectedPhotosChanged() {
                }

                private void sendSelectedPhotos(HashMap<Object, Object> hashMap, ArrayList<Object> arrayList) {
                }

                public void actionButtonPressed(boolean canceled) {
                    if (!photos.isEmpty() && ImageUpdater.this.delegate != null && !this.sendPressed && !canceled) {
                        this.sendPressed = true;
                        ArrayList<SendingMediaInfo> media = new ArrayList();
                        for (int a = 0; a < order.size(); a++) {
                            SearchImage object = photos.get(order.get(a));
                            SendingMediaInfo info = new SendingMediaInfo();
                            media.add(info);
                            if (object instanceof SearchImage) {
                                String charSequence;
                                ArrayList arrayList;
                                SearchImage searchImage = object;
                                if (searchImage.imagePath != null) {
                                    info.path = searchImage.imagePath;
                                } else {
                                    info.searchImage = searchImage;
                                }
                                if (searchImage.caption != null) {
                                    charSequence = searchImage.caption.toString();
                                } else {
                                    charSequence = null;
                                }
                                info.caption = charSequence;
                                info.entities = searchImage.entities;
                                if (searchImage.stickers.isEmpty()) {
                                    arrayList = null;
                                } else {
                                    arrayList = new ArrayList(searchImage.stickers);
                                }
                                info.masks = arrayList;
                                info.ttl = searchImage.ttl;
                            }
                        }
                        ImageUpdater.this.didSelectPhotos(media);
                    }
                }
            });
            this.parentFragment.presentFragment(fragment);
        }
    }

    private void didSelectPhotos(ArrayList<SendingMediaInfo> photos) {
        if (!photos.isEmpty()) {
            SendingMediaInfo info = (SendingMediaInfo) photos.get(0);
            Bitmap bitmap = null;
            if (info.path != null) {
                bitmap = ImageLoader.loadBitmap(((SendingMediaInfo) photos.get(0)).path, null, 800.0f, 800.0f, true);
            } else if (info.searchImage != null) {
                if (info.searchImage.photo != null) {
                    PhotoSize photoSize = FileLoader.getClosestPhotoSizeWithSize(info.searchImage.photo.sizes, AndroidUtilities.getPhotoSize());
                    if (photoSize != null) {
                        File path = FileLoader.getPathToAttach(photoSize, true);
                        this.finalPath = path.getAbsolutePath();
                        if (!path.exists()) {
                            path = FileLoader.getPathToAttach(photoSize, false);
                            if (!path.exists()) {
                                path = null;
                            }
                        }
                        if (path != null) {
                            bitmap = ImageLoader.loadBitmap(path.getAbsolutePath(), null, 800.0f, 800.0f, true);
                        } else {
                            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileDidLoad);
                            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileDidFailedLoad);
                            this.uploadingImage = FileLoader.getAttachFileName(photoSize.location);
                            this.imageReceiver.setImage(photoSize.location, null, null, "jpg", null, 1);
                        }
                    }
                } else if (info.searchImage.imageUrl != null) {
                    File cacheFile = new File(FileLoader.getDirectory(4), Utilities.MD5(info.searchImage.imageUrl) + "." + ImageLoader.getHttpUrlExtension(info.searchImage.imageUrl, "jpg"));
                    this.finalPath = cacheFile.getAbsolutePath();
                    if (!cacheFile.exists() || cacheFile.length() == 0) {
                        this.uploadingImage = info.searchImage.imageUrl;
                        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.httpFileDidLoad);
                        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.httpFileDidFailedLoad);
                        this.imageReceiver.setImage(info.searchImage.imageUrl, null, null, "jpg", 1);
                    } else {
                        bitmap = ImageLoader.loadBitmap(cacheFile.getAbsolutePath(), null, 800.0f, 800.0f, true);
                    }
                } else {
                    bitmap = null;
                }
            }
            processBitmap(bitmap);
        }
    }

    public void openCamera() {
        if (this.parentFragment != null && this.parentFragment.getParentActivity() != null) {
            try {
                if (VERSION.SDK_INT < 23 || this.parentFragment.getParentActivity().checkSelfPermission("android.permission.CAMERA") == 0) {
                    Intent takePictureIntent = new Intent("android.media.action.IMAGE_CAPTURE");
                    File image = AndroidUtilities.generatePicturePath();
                    if (image != null) {
                        if (VERSION.SDK_INT >= 24) {
                            takePictureIntent.putExtra("output", FileProvider.getUriForFile(this.parentFragment.getParentActivity(), "org.telegram.messenger.provider", image));
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
                FileLog.m13e(e);
            }
        }
    }

    public void openGallery() {
        if (this.parentFragment != null) {
            if (VERSION.SDK_INT < 23 || this.parentFragment == null || this.parentFragment.getParentActivity() == null || this.parentFragment.getParentActivity().checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == 0) {
                PhotoAlbumPickerActivity fragment = new PhotoAlbumPickerActivity(true, false, false, null);
                fragment.setDelegate(new CLASSNAME());
                this.parentFragment.presentFragment(fragment);
                return;
            }
            this.parentFragment.getParentActivity().requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 4);
        }
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
            FileLog.m13e(e);
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
                FileLog.m13e(e);
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
                    ImageUpdater.this.processBitmap(ImageLoader.loadBitmap(path, null, 800.0f, 800.0f, true));
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
            this.bigPhoto = ImageLoader.scaleAndSaveImage(bitmap, 800.0f, 800.0f, 80, false, 320, 320);
            this.smallPhoto = ImageLoader.scaleAndSaveImage(bitmap, 150.0f, 150.0f, 80, false, 150, 150);
            if (this.smallPhoto != null) {
                try {
                    Bitmap b = BitmapFactory.decodeFile(FileLoader.getPathToAttach(this.smallPhoto, true).getAbsolutePath());
                    ImageLoader.getInstance().putImageToCache(new BitmapDrawable(b), this.smallPhoto.location.volume_id + "_" + this.smallPhoto.location.local_id + "@50_50");
                } catch (Throwable th) {
                }
            }
            bitmap.recycle();
            if (this.bigPhoto != null) {
                UserConfig.getInstance(this.currentAccount).saveConfig(false);
                this.uploadingImage = FileLoader.getDirectory(4) + "/" + this.bigPhoto.location.volume_id + "_" + this.bigPhoto.location.local_id + ".jpg";
                NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.FileDidUpload);
                NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.FileDidFailUpload);
                FileLoader.getInstance(this.currentAccount).uploadFile(this.uploadingImage, false, true, 16777216);
                if (this.delegate != null) {
                    this.delegate.didUploadPhoto(null, this.bigPhoto, this.smallPhoto);
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
            if (this.uploadingImage != null && location.equals(this.uploadingImage)) {
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.FileDidUpload);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.FileDidFailUpload);
                if (this.delegate != null) {
                    this.delegate.didUploadPhoto((InputFile) args[1], this.bigPhoto, this.smallPhoto);
                }
                this.uploadingImage = null;
                if (this.clearAfterUpdate) {
                    this.imageReceiver.setImageBitmap((Drawable) null);
                    this.parentFragment = null;
                    this.delegate = null;
                }
            }
        } else if (id == NotificationCenter.FileDidFailUpload) {
            location = (String) args[0];
            if (this.uploadingImage != null && location.equals(this.uploadingImage)) {
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.FileDidUpload);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.FileDidFailUpload);
                this.uploadingImage = null;
                if (this.clearAfterUpdate) {
                    this.imageReceiver.setImageBitmap((Drawable) null);
                    this.parentFragment = null;
                    this.delegate = null;
                }
            }
        } else if (id == NotificationCenter.fileDidLoad || id == NotificationCenter.fileDidFailedLoad || id == NotificationCenter.httpFileDidLoad || id == NotificationCenter.httpFileDidFailedLoad) {
            String path = args[0];
            if (this.uploadingImage != null && path.equals(this.uploadingImage)) {
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileDidLoad);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileDidFailedLoad);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.httpFileDidLoad);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.httpFileDidFailedLoad);
                this.uploadingImage = null;
                if (id == NotificationCenter.fileDidLoad || id == NotificationCenter.httpFileDidLoad) {
                    processBitmap(ImageLoader.loadBitmap(this.finalPath, null, 800.0f, 800.0f, true));
                } else {
                    this.imageReceiver.setImageBitmap((Drawable) null);
                }
            }
        }
    }
}
