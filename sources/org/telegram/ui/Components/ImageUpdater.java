package org.telegram.ui.Components;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageLocation;
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
import org.telegram.tgnet.TLRPC.InputFile;
import org.telegram.tgnet.TLRPC.Photo;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.BottomSheet.Builder;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.PhotoAlbumPickerActivity;
import org.telegram.ui.PhotoAlbumPickerActivity.PhotoAlbumPickerActivityDelegate;
import org.telegram.ui.PhotoCropActivity;
import org.telegram.ui.PhotoCropActivity.PhotoEditActivityDelegate;
import org.telegram.ui.PhotoPickerActivity;
import org.telegram.ui.PhotoPickerActivity.PhotoPickerActivityDelegate;
import org.telegram.ui.PhotoPickerActivity.PhotoPickerActivityDelegate.-CC;
import org.telegram.ui.PhotoViewer;
import org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider;

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
    private boolean searchAvailable = true;
    private PhotoSize smallPhoto;
    private boolean uploadAfterSelect = true;
    public String uploadingImage;

    public interface ImageUpdaterDelegate {

        public final /* synthetic */ class -CC {
            public static String $default$getInitialSearchString(ImageUpdaterDelegate imageUpdaterDelegate) {
                return null;
            }
        }

        void didUploadPhoto(InputFile inputFile, PhotoSize photoSize, PhotoSize photoSize2);

        String getInitialSearchString();
    }

    public void clear() {
        if (this.uploadingImage != null) {
            this.clearAfterUpdate = true;
            return;
        }
        this.parentFragment = null;
        this.delegate = null;
    }

    public void openMenu(boolean z, Runnable runnable) {
        BaseFragment baseFragment = this.parentFragment;
        if (baseFragment != null && baseFragment.getParentActivity() != null) {
            CharSequence[] charSequenceArr;
            int[] iArr;
            Builder builder = new Builder(this.parentFragment.getParentActivity());
            builder.setTitle(LocaleController.getString("ChoosePhoto", NUM), true);
            String str = "DeletePhoto";
            String str2 = "ChooseFromGallery";
            String str3 = "ChooseTakePhoto";
            int i = 3;
            if (this.searchAvailable) {
                String str4 = "ChooseFromSearch";
                if (z) {
                    charSequenceArr = new CharSequence[]{LocaleController.getString(str3, NUM), LocaleController.getString(str2, NUM), LocaleController.getString(str4, NUM), LocaleController.getString(str, NUM)};
                    iArr = new int[]{NUM, NUM, NUM, NUM};
                } else {
                    charSequenceArr = new CharSequence[]{LocaleController.getString(str3, NUM), LocaleController.getString(str2, NUM), LocaleController.getString(str4, NUM)};
                    iArr = new int[]{NUM, NUM, NUM};
                }
            } else if (z) {
                charSequenceArr = new CharSequence[]{LocaleController.getString(str3, NUM), LocaleController.getString(str2, NUM), LocaleController.getString(str, NUM)};
                iArr = new int[]{NUM, NUM, NUM};
            } else {
                charSequenceArr = new CharSequence[]{LocaleController.getString(str3, NUM), LocaleController.getString(str2, NUM)};
                iArr = new int[]{NUM, NUM};
            }
            builder.setItems(charSequenceArr, iArr, new -$$Lambda$ImageUpdater$ZfeU_OSr8fUwIgo0j9MadtpPCLASSNAME(this, runnable));
            BottomSheet create = builder.create();
            this.parentFragment.showDialog(create);
            if (!this.searchAvailable) {
                i = 2;
            }
            create.setItemColor(i, Theme.getColor("dialogTextRed2"), Theme.getColor("dialogRedIcon"));
        }
    }

    public /* synthetic */ void lambda$openMenu$0$ImageUpdater(Runnable runnable, DialogInterface dialogInterface, int i) {
        if (i == 0) {
            openCamera();
        } else if (i == 1) {
            openGallery();
        } else if (this.searchAvailable && i == 2) {
            openSearch();
        } else if ((this.searchAvailable && i == 3) || i == 2) {
            runnable.run();
        }
    }

    public void setSearchAvailable(boolean z) {
        this.searchAvailable = z;
    }

    public void setUploadAfterSelect(boolean z) {
        this.uploadAfterSelect = z;
    }

    public void openSearch() {
        if (this.parentFragment != null) {
            final HashMap hashMap = new HashMap();
            final ArrayList arrayList = new ArrayList();
            PhotoPickerActivity photoPickerActivity = new PhotoPickerActivity(0, null, hashMap, arrayList, 1, false, null);
            photoPickerActivity.setDelegate(new PhotoPickerActivityDelegate() {
                private boolean sendPressed;

                private void sendSelectedPhotos(HashMap<Object, Object> hashMap, ArrayList<Object> arrayList, boolean z, int i) {
                }

                public void onCaptionChanged(CharSequence charSequence) {
                }

                public /* synthetic */ void onOpenInPressed() {
                    -CC.$default$onOpenInPressed(this);
                }

                public void selectedPhotosChanged() {
                }

                public void actionButtonPressed(boolean z, boolean z2, int i) {
                    if (!hashMap.isEmpty() && ImageUpdater.this.delegate != null && !this.sendPressed && !z) {
                        this.sendPressed = true;
                        ArrayList arrayList = new ArrayList();
                        for (int i2 = 0; i2 < arrayList.size(); i2++) {
                            Object obj = hashMap.get(arrayList.get(i2));
                            SendingMediaInfo sendingMediaInfo = new SendingMediaInfo();
                            arrayList.add(sendingMediaInfo);
                            if (obj instanceof SearchImage) {
                                SearchImage searchImage = (SearchImage) obj;
                                String str = searchImage.imagePath;
                                if (str != null) {
                                    sendingMediaInfo.path = str;
                                } else {
                                    sendingMediaInfo.searchImage = searchImage;
                                }
                                CharSequence charSequence = searchImage.caption;
                                ArrayList arrayList2 = null;
                                sendingMediaInfo.caption = charSequence != null ? charSequence.toString() : null;
                                sendingMediaInfo.entities = searchImage.entities;
                                if (!searchImage.stickers.isEmpty()) {
                                    arrayList2 = new ArrayList(searchImage.stickers);
                                }
                                sendingMediaInfo.masks = arrayList2;
                                sendingMediaInfo.ttl = searchImage.ttl;
                            }
                        }
                        ImageUpdater.this.didSelectPhotos(arrayList);
                    }
                }
            });
            photoPickerActivity.setInitialSearchString(this.delegate.getInitialSearchString());
            this.parentFragment.presentFragment(photoPickerActivity);
        }
    }

    private void didSelectPhotos(ArrayList<SendingMediaInfo> arrayList) {
        if (!arrayList.isEmpty()) {
            SendingMediaInfo sendingMediaInfo = (SendingMediaInfo) arrayList.get(0);
            String str = sendingMediaInfo.path;
            Bitmap bitmap = null;
            if (str != null) {
                bitmap = ImageLoader.loadBitmap(str, null, 800.0f, 800.0f, true);
            } else {
                SearchImage searchImage = sendingMediaInfo.searchImage;
                if (searchImage != null) {
                    Bitmap loadBitmap;
                    Photo photo = searchImage.photo;
                    if (photo != null) {
                        PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, AndroidUtilities.getPhotoSize());
                        if (closestPhotoSizeWithSize != null) {
                            File file;
                            File pathToAttach = FileLoader.getPathToAttach(closestPhotoSizeWithSize, true);
                            this.finalPath = pathToAttach.getAbsolutePath();
                            if (pathToAttach.exists()) {
                                file = pathToAttach;
                            } else {
                                file = FileLoader.getPathToAttach(closestPhotoSizeWithSize, false);
                                if (!file.exists()) {
                                    file = null;
                                }
                            }
                            if (file != null) {
                                loadBitmap = ImageLoader.loadBitmap(file.getAbsolutePath(), null, 800.0f, 800.0f, true);
                            } else {
                                NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileDidLoad);
                                NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileDidFailToLoad);
                                this.uploadingImage = FileLoader.getAttachFileName(closestPhotoSizeWithSize.location);
                                this.imageReceiver.setImage(ImageLocation.getForPhoto(closestPhotoSizeWithSize, sendingMediaInfo.searchImage.photo), null, null, "jpg", null, 1);
                            }
                        }
                    } else if (searchImage.imageUrl != null) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(Utilities.MD5(sendingMediaInfo.searchImage.imageUrl));
                        stringBuilder.append(".");
                        stringBuilder.append(ImageLoader.getHttpUrlExtension(sendingMediaInfo.searchImage.imageUrl, "jpg"));
                        File file2 = new File(FileLoader.getDirectory(4), stringBuilder.toString());
                        this.finalPath = file2.getAbsolutePath();
                        if (!file2.exists() || file2.length() == 0) {
                            this.uploadingImage = sendingMediaInfo.searchImage.imageUrl;
                            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.httpFileDidLoad);
                            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.httpFileDidFailedLoad);
                            this.imageReceiver.setImage(sendingMediaInfo.searchImage.imageUrl, null, null, "jpg", 1);
                        } else {
                            loadBitmap = ImageLoader.loadBitmap(file2.getAbsolutePath(), null, 800.0f, 800.0f, true);
                        }
                    }
                    bitmap = loadBitmap;
                }
            }
            processBitmap(bitmap);
        }
    }

    public void openCamera() {
        String str = "android.permission.CAMERA";
        BaseFragment baseFragment = this.parentFragment;
        if (!(baseFragment == null || baseFragment.getParentActivity() == null)) {
            try {
                if (VERSION.SDK_INT < 23 || this.parentFragment.getParentActivity().checkSelfPermission(str) == 0) {
                    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                    File generatePicturePath = AndroidUtilities.generatePicturePath();
                    if (generatePicturePath != null) {
                        String str2 = "output";
                        if (VERSION.SDK_INT >= 24) {
                            intent.putExtra(str2, FileProvider.getUriForFile(this.parentFragment.getParentActivity(), "org.telegram.messenger.beta.provider", generatePicturePath));
                            intent.addFlags(2);
                            intent.addFlags(1);
                        } else {
                            intent.putExtra(str2, Uri.fromFile(generatePicturePath));
                        }
                        this.currentPicturePath = generatePicturePath.getAbsolutePath();
                    }
                    this.parentFragment.startActivityForResult(intent, 13);
                } else {
                    this.parentFragment.getParentActivity().requestPermissions(new String[]{str}, 19);
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public void openGallery() {
        BaseFragment baseFragment = this.parentFragment;
        if (baseFragment != null) {
            if (!(VERSION.SDK_INT < 23 || baseFragment == null || baseFragment.getParentActivity() == null)) {
                if (this.parentFragment.getParentActivity().checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != 0) {
                    this.parentFragment.getParentActivity().requestPermissions(new String[]{r1}, 4);
                    return;
                }
            }
            PhotoAlbumPickerActivity photoAlbumPickerActivity = new PhotoAlbumPickerActivity(1, false, false, null);
            photoAlbumPickerActivity.setAllowSearchImages(this.searchAvailable);
            photoAlbumPickerActivity.setDelegate(new PhotoAlbumPickerActivityDelegate() {
                public void didSelectPhotos(ArrayList<SendingMediaInfo> arrayList, boolean z, int i) {
                    ImageUpdater.this.didSelectPhotos(arrayList);
                }

                public void startPhotoSelectActivity() {
                    try {
                        Intent intent = new Intent("android.intent.action.GET_CONTENT");
                        intent.setType("image/*");
                        ImageUpdater.this.parentFragment.startActivityForResult(intent, 14);
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                }
            });
            this.parentFragment.presentFragment(photoAlbumPickerActivity);
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
                PhotoCropActivity photoCropActivity = new PhotoCropActivity(bundle);
                photoCropActivity.setDelegate(this);
                launchActivity.lambda$runLinkRequest$31$LaunchActivity(photoCropActivity);
            }
        } catch (Exception e) {
            FileLog.e(e);
            processBitmap(ImageLoader.loadBitmap(str, uri, 800.0f, 800.0f, true));
        }
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        int i3 = i;
        if (i2 != -1) {
            return;
        }
        if (i3 == 13) {
            int i4;
            PhotoViewer.getInstance().setParentActivity(this.parentFragment.getParentActivity());
            int i5 = 0;
            try {
                i3 = new ExifInterface(this.currentPicturePath).getAttributeInt("Orientation", 1);
                if (i3 == 3) {
                    i5 = 180;
                } else if (i3 == 6) {
                    i5 = 90;
                } else if (i3 == 8) {
                    i5 = 270;
                }
                i4 = i5;
            } catch (Exception e) {
                FileLog.e(e);
                i4 = 0;
            }
            final ArrayList arrayList = new ArrayList();
            arrayList.add(new PhotoEntry(0, 0, 0, this.currentPicturePath, i4, false, 0, 0, 0));
            PhotoViewer.getInstance().openPhotoForSelect(arrayList, 0, 1, false, new EmptyPhotoViewerProvider() {
                public boolean allowCaption() {
                    return false;
                }

                public boolean canScrollAway() {
                    return false;
                }

                public void sendButtonPressed(int i, VideoEditedInfo videoEditedInfo, boolean z, int i2) {
                    PhotoEntry photoEntry = (PhotoEntry) arrayList.get(0);
                    String str = photoEntry.imagePath;
                    if (str == null) {
                        str = photoEntry.path;
                        if (str == null) {
                            str = null;
                        }
                    }
                    ImageUpdater.this.processBitmap(ImageLoader.loadBitmap(str, null, 800.0f, 800.0f, true));
                }
            }, null);
            AndroidUtilities.addMediaToGallery(this.currentPicturePath);
            this.currentPicturePath = null;
        } else if (i3 == 14 && intent != null && intent.getData() != null) {
            startCrop(null, intent.getData());
        }
    }

    private void processBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            this.bigPhoto = ImageLoader.scaleAndSaveImage(bitmap, 800.0f, 800.0f, 80, false, 320, 320);
            this.smallPhoto = ImageLoader.scaleAndSaveImage(bitmap, 150.0f, 150.0f, 80, false, 150, 150);
            PhotoSize photoSize = this.smallPhoto;
            String str = "_";
            if (photoSize != null) {
                try {
                    Bitmap decodeFile = BitmapFactory.decodeFile(FileLoader.getPathToAttach(photoSize, true).getAbsolutePath());
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(this.smallPhoto.location.volume_id);
                    stringBuilder.append(str);
                    stringBuilder.append(this.smallPhoto.location.local_id);
                    stringBuilder.append("@50_50");
                    ImageLoader.getInstance().putImageToCache(new BitmapDrawable(decodeFile), stringBuilder.toString());
                } catch (Throwable unused) {
                }
            }
            bitmap.recycle();
            if (this.bigPhoto != null) {
                UserConfig.getInstance(this.currentAccount).saveConfig(false);
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append(FileLoader.getDirectory(4));
                stringBuilder2.append("/");
                stringBuilder2.append(this.bigPhoto.location.volume_id);
                stringBuilder2.append(str);
                stringBuilder2.append(this.bigPhoto.location.local_id);
                stringBuilder2.append(".jpg");
                this.uploadingImage = stringBuilder2.toString();
                if (this.uploadAfterSelect) {
                    NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.FileDidUpload);
                    NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.FileDidFailUpload);
                    FileLoader.getInstance(this.currentAccount).uploadFile(this.uploadingImage, false, true, 16777216);
                }
                ImageUpdaterDelegate imageUpdaterDelegate = this.delegate;
                if (imageUpdaterDelegate != null) {
                    imageUpdaterDelegate.didUploadPhoto(null, this.bigPhoto, this.smallPhoto);
                }
            }
        }
    }

    public void didFinishEdit(Bitmap bitmap) {
        processBitmap(bitmap);
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.FileDidUpload) {
            if (((String) objArr[0]).equals(this.uploadingImage)) {
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.FileDidUpload);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.FileDidFailUpload);
                ImageUpdaterDelegate imageUpdaterDelegate = this.delegate;
                if (imageUpdaterDelegate != null) {
                    imageUpdaterDelegate.didUploadPhoto((InputFile) objArr[1], this.bigPhoto, this.smallPhoto);
                }
                this.uploadingImage = null;
                if (this.clearAfterUpdate) {
                    this.imageReceiver.setImageBitmap(null);
                    this.parentFragment = null;
                    this.delegate = null;
                }
            }
        } else if (i == NotificationCenter.FileDidFailUpload) {
            if (((String) objArr[0]).equals(this.uploadingImage)) {
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.FileDidUpload);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.FileDidFailUpload);
                this.uploadingImage = null;
                if (this.clearAfterUpdate) {
                    this.imageReceiver.setImageBitmap(null);
                    this.parentFragment = null;
                    this.delegate = null;
                }
            }
        } else if ((i == NotificationCenter.fileDidLoad || i == NotificationCenter.fileDidFailToLoad || i == NotificationCenter.httpFileDidLoad || i == NotificationCenter.httpFileDidFailedLoad) && ((String) objArr[0]).equals(this.uploadingImage)) {
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileDidLoad);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileDidFailToLoad);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.httpFileDidLoad);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.httpFileDidFailedLoad);
            this.uploadingImage = null;
            if (i == NotificationCenter.fileDidLoad || i == NotificationCenter.httpFileDidLoad) {
                processBitmap(ImageLoader.loadBitmap(this.finalPath, null, 800.0f, 800.0f, true));
            } else {
                this.imageReceiver.setImageBitmap(null);
            }
        }
    }
}
