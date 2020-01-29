package org.telegram.ui.Components;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
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
import org.telegram.messenger.MediaController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.PhotoAlbumPickerActivity;
import org.telegram.ui.PhotoCropActivity;
import org.telegram.ui.PhotoPickerActivity;
import org.telegram.ui.PhotoViewer;

public class ImageUpdater implements NotificationCenter.NotificationCenterDelegate, PhotoCropActivity.PhotoEditActivityDelegate {
    private TLRPC.PhotoSize bigPhoto;
    private boolean clearAfterUpdate;
    private int currentAccount = UserConfig.selectedAccount;
    public String currentPicturePath;
    public ImageUpdaterDelegate delegate;
    private String finalPath;
    private ImageReceiver imageReceiver = new ImageReceiver((View) null);
    public BaseFragment parentFragment;
    private File picturePath = null;
    private boolean searchAvailable = true;
    private TLRPC.PhotoSize smallPhoto;
    private boolean uploadAfterSelect = true;
    public String uploadingImage;

    public interface ImageUpdaterDelegate {

        /* renamed from: org.telegram.ui.Components.ImageUpdater$ImageUpdaterDelegate$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static String $default$getInitialSearchString(ImageUpdaterDelegate imageUpdaterDelegate) {
                return null;
            }
        }

        void didUploadPhoto(TLRPC.InputFile inputFile, TLRPC.PhotoSize photoSize, TLRPC.PhotoSize photoSize2);

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
        CharSequence[] charSequenceArr;
        int[] iArr;
        BaseFragment baseFragment = this.parentFragment;
        if (baseFragment != null && baseFragment.getParentActivity() != null) {
            BottomSheet.Builder builder = new BottomSheet.Builder(this.parentFragment.getParentActivity());
            builder.setTitle(LocaleController.getString("ChoosePhoto", NUM), true);
            int i = 3;
            if (this.searchAvailable) {
                if (z) {
                    charSequenceArr = new CharSequence[]{LocaleController.getString("ChooseTakePhoto", NUM), LocaleController.getString("ChooseFromGallery", NUM), LocaleController.getString("ChooseFromSearch", NUM), LocaleController.getString("DeletePhoto", NUM)};
                    iArr = new int[]{NUM, NUM, NUM, NUM};
                } else {
                    charSequenceArr = new CharSequence[]{LocaleController.getString("ChooseTakePhoto", NUM), LocaleController.getString("ChooseFromGallery", NUM), LocaleController.getString("ChooseFromSearch", NUM)};
                    iArr = new int[]{NUM, NUM, NUM};
                }
            } else if (z) {
                charSequenceArr = new CharSequence[]{LocaleController.getString("ChooseTakePhoto", NUM), LocaleController.getString("ChooseFromGallery", NUM), LocaleController.getString("DeletePhoto", NUM)};
                iArr = new int[]{NUM, NUM, NUM};
            } else {
                charSequenceArr = new CharSequence[]{LocaleController.getString("ChooseTakePhoto", NUM), LocaleController.getString("ChooseFromGallery", NUM)};
                iArr = new int[]{NUM, NUM};
            }
            builder.setItems(charSequenceArr, iArr, new DialogInterface.OnClickListener(runnable) {
                private final /* synthetic */ Runnable f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    ImageUpdater.this.lambda$openMenu$0$ImageUpdater(this.f$1, dialogInterface, i);
                }
            });
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
            PhotoPickerActivity photoPickerActivity = new PhotoPickerActivity(0, (MediaController.AlbumEntry) null, hashMap, arrayList, 1, false, (ChatActivity) null);
            photoPickerActivity.setDelegate(new PhotoPickerActivity.PhotoPickerActivityDelegate() {
                private boolean sendPressed;

                private void sendSelectedPhotos(HashMap<Object, Object> hashMap, ArrayList<Object> arrayList, boolean z, int i) {
                }

                public void onCaptionChanged(CharSequence charSequence) {
                }

                public /* synthetic */ void onOpenInPressed() {
                    PhotoPickerActivity.PhotoPickerActivityDelegate.CC.$default$onOpenInPressed(this);
                }

                public void selectedPhotosChanged() {
                }

                public void actionButtonPressed(boolean z, boolean z2, int i) {
                    if (!hashMap.isEmpty() && ImageUpdater.this.delegate != null && !this.sendPressed && !z) {
                        this.sendPressed = true;
                        ArrayList arrayList = new ArrayList();
                        for (int i2 = 0; i2 < arrayList.size(); i2++) {
                            Object obj = hashMap.get(arrayList.get(i2));
                            SendMessagesHelper.SendingMediaInfo sendingMediaInfo = new SendMessagesHelper.SendingMediaInfo();
                            arrayList.add(sendingMediaInfo);
                            if (obj instanceof MediaController.SearchImage) {
                                MediaController.SearchImage searchImage = (MediaController.SearchImage) obj;
                                String str = searchImage.imagePath;
                                if (str != null) {
                                    sendingMediaInfo.path = str;
                                } else {
                                    sendingMediaInfo.searchImage = searchImage;
                                }
                                CharSequence charSequence = searchImage.caption;
                                ArrayList<TLRPC.InputDocument> arrayList2 = null;
                                sendingMediaInfo.caption = charSequence != null ? charSequence.toString() : null;
                                sendingMediaInfo.entities = searchImage.entities;
                                if (!searchImage.stickers.isEmpty()) {
                                    arrayList2 = new ArrayList<>(searchImage.stickers);
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

    /* access modifiers changed from: private */
    public void didSelectPhotos(ArrayList<SendMessagesHelper.SendingMediaInfo> arrayList) {
        Bitmap loadBitmap;
        File file;
        if (!arrayList.isEmpty()) {
            SendMessagesHelper.SendingMediaInfo sendingMediaInfo = arrayList.get(0);
            String str = sendingMediaInfo.path;
            Bitmap bitmap = null;
            if (str != null) {
                bitmap = ImageLoader.loadBitmap(str, (Uri) null, 800.0f, 800.0f, true);
            } else {
                MediaController.SearchImage searchImage = sendingMediaInfo.searchImage;
                if (searchImage != null) {
                    TLRPC.Photo photo = searchImage.photo;
                    if (photo != null) {
                        TLRPC.PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, AndroidUtilities.getPhotoSize());
                        if (closestPhotoSizeWithSize != null) {
                            File pathToAttach = FileLoader.getPathToAttach(closestPhotoSizeWithSize, true);
                            this.finalPath = pathToAttach.getAbsolutePath();
                            if (!pathToAttach.exists()) {
                                file = FileLoader.getPathToAttach(closestPhotoSizeWithSize, false);
                                if (!file.exists()) {
                                    file = null;
                                }
                            } else {
                                file = pathToAttach;
                            }
                            if (file != null) {
                                loadBitmap = ImageLoader.loadBitmap(file.getAbsolutePath(), (Uri) null, 800.0f, 800.0f, true);
                            } else {
                                NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileDidLoad);
                                NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileDidFailToLoad);
                                this.uploadingImage = FileLoader.getAttachFileName(closestPhotoSizeWithSize.location);
                                this.imageReceiver.setImage(ImageLocation.getForPhoto(closestPhotoSizeWithSize, sendingMediaInfo.searchImage.photo), (String) null, (Drawable) null, "jpg", (Object) null, 1);
                            }
                        }
                    } else if (searchImage.imageUrl != null) {
                        File file2 = new File(FileLoader.getDirectory(4), Utilities.MD5(sendingMediaInfo.searchImage.imageUrl) + "." + ImageLoader.getHttpUrlExtension(sendingMediaInfo.searchImage.imageUrl, "jpg"));
                        this.finalPath = file2.getAbsolutePath();
                        if (!file2.exists() || file2.length() == 0) {
                            this.uploadingImage = sendingMediaInfo.searchImage.imageUrl;
                            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.httpFileDidLoad);
                            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.httpFileDidFailedLoad);
                            this.imageReceiver.setImage(sendingMediaInfo.searchImage.imageUrl, (String) null, (Drawable) null, "jpg", 1);
                        } else {
                            loadBitmap = ImageLoader.loadBitmap(file2.getAbsolutePath(), (Uri) null, 800.0f, 800.0f, true);
                        }
                    }
                    bitmap = loadBitmap;
                }
            }
            processBitmap(bitmap);
        }
    }

    public void openCamera() {
        BaseFragment baseFragment = this.parentFragment;
        if (baseFragment != null && baseFragment.getParentActivity() != null) {
            try {
                if (Build.VERSION.SDK_INT < 23 || this.parentFragment.getParentActivity().checkSelfPermission("android.permission.CAMERA") == 0) {
                    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                    File generatePicturePath = AndroidUtilities.generatePicturePath();
                    if (generatePicturePath != null) {
                        if (Build.VERSION.SDK_INT >= 24) {
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
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    public void openGallery() {
        BaseFragment baseFragment = this.parentFragment;
        if (baseFragment != null) {
            if (Build.VERSION.SDK_INT < 23 || baseFragment == null || baseFragment.getParentActivity() == null || this.parentFragment.getParentActivity().checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == 0) {
                PhotoAlbumPickerActivity photoAlbumPickerActivity = new PhotoAlbumPickerActivity(1, false, false, (ChatActivity) null);
                photoAlbumPickerActivity.setAllowSearchImages(this.searchAvailable);
                photoAlbumPickerActivity.setDelegate(new PhotoAlbumPickerActivity.PhotoAlbumPickerActivityDelegate() {
                    public void didSelectPhotos(ArrayList<SendMessagesHelper.SendingMediaInfo> arrayList, boolean z, int i) {
                        ImageUpdater.this.didSelectPhotos(arrayList);
                    }

                    public void startPhotoSelectActivity() {
                        try {
                            Intent intent = new Intent("android.intent.action.GET_CONTENT");
                            intent.setType("image/*");
                            ImageUpdater.this.parentFragment.startActivityForResult(intent, 14);
                        } catch (Exception e) {
                            FileLog.e((Throwable) e);
                        }
                    }
                });
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
                PhotoCropActivity photoCropActivity = new PhotoCropActivity(bundle);
                photoCropActivity.setDelegate(this);
                launchActivity.lambda$runLinkRequest$30$LaunchActivity(photoCropActivity);
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            processBitmap(ImageLoader.loadBitmap(str, uri, 800.0f, 800.0f, true));
        }
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        int i3;
        int i4 = i;
        if (i2 != -1) {
            return;
        }
        if (i4 == 13) {
            PhotoViewer.getInstance().setParentActivity(this.parentFragment.getParentActivity());
            int i5 = 0;
            try {
                int attributeInt = new ExifInterface(this.currentPicturePath).getAttributeInt("Orientation", 1);
                if (attributeInt == 3) {
                    i5 = 180;
                } else if (attributeInt == 6) {
                    i5 = 90;
                } else if (attributeInt == 8) {
                    i5 = 270;
                }
                i3 = i5;
            } catch (Exception e) {
                FileLog.e((Throwable) e);
                i3 = 0;
            }
            final ArrayList arrayList = new ArrayList();
            arrayList.add(new MediaController.PhotoEntry(0, 0, 0, this.currentPicturePath, i3, false, 0, 0, 0));
            PhotoViewer.getInstance().openPhotoForSelect(arrayList, 0, 1, false, new PhotoViewer.EmptyPhotoViewerProvider() {
                public boolean allowCaption() {
                    return false;
                }

                public boolean canScrollAway() {
                    return false;
                }

                public void sendButtonPressed(int i, VideoEditedInfo videoEditedInfo, boolean z, int i2) {
                    MediaController.PhotoEntry photoEntry = (MediaController.PhotoEntry) arrayList.get(0);
                    String str = photoEntry.imagePath;
                    if (str == null && (str = photoEntry.path) == null) {
                        str = null;
                    }
                    ImageUpdater.this.processBitmap(ImageLoader.loadBitmap(str, (Uri) null, 800.0f, 800.0f, true));
                }
            }, (ChatActivity) null);
            AndroidUtilities.addMediaToGallery(this.currentPicturePath);
            this.currentPicturePath = null;
        } else if (i4 == 14 && intent != null && intent.getData() != null) {
            startCrop((String) null, intent.getData());
        }
    }

    /* access modifiers changed from: private */
    public void processBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            this.bigPhoto = ImageLoader.scaleAndSaveImage(bitmap, 800.0f, 800.0f, 80, false, 320, 320);
            this.smallPhoto = ImageLoader.scaleAndSaveImage(bitmap, 150.0f, 150.0f, 80, false, 150, 150);
            TLRPC.PhotoSize photoSize = this.smallPhoto;
            if (photoSize != null) {
                try {
                    Bitmap decodeFile = BitmapFactory.decodeFile(FileLoader.getPathToAttach(photoSize, true).getAbsolutePath());
                    ImageLoader.getInstance().putImageToCache(new BitmapDrawable(decodeFile), this.smallPhoto.location.volume_id + "_" + this.smallPhoto.location.local_id + "@50_50");
                } catch (Throwable unused) {
                }
            }
            bitmap.recycle();
            if (this.bigPhoto != null) {
                UserConfig.getInstance(this.currentAccount).saveConfig(false);
                this.uploadingImage = FileLoader.getDirectory(4) + "/" + this.bigPhoto.location.volume_id + "_" + this.bigPhoto.location.local_id + ".jpg";
                if (this.uploadAfterSelect) {
                    NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.FileDidUpload);
                    NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.FileDidFailUpload);
                    FileLoader.getInstance(this.currentAccount).uploadFile(this.uploadingImage, false, true, 16777216);
                }
                ImageUpdaterDelegate imageUpdaterDelegate = this.delegate;
                if (imageUpdaterDelegate != null) {
                    imageUpdaterDelegate.didUploadPhoto((TLRPC.InputFile) null, this.bigPhoto, this.smallPhoto);
                }
            }
        }
    }

    public void didFinishEdit(Bitmap bitmap) {
        processBitmap(bitmap);
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.FileDidUpload) {
            if (objArr[0].equals(this.uploadingImage)) {
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.FileDidUpload);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.FileDidFailUpload);
                ImageUpdaterDelegate imageUpdaterDelegate = this.delegate;
                if (imageUpdaterDelegate != null) {
                    imageUpdaterDelegate.didUploadPhoto(objArr[1], this.bigPhoto, this.smallPhoto);
                }
                this.uploadingImage = null;
                if (this.clearAfterUpdate) {
                    this.imageReceiver.setImageBitmap((Drawable) null);
                    this.parentFragment = null;
                    this.delegate = null;
                }
            }
        } else if (i == NotificationCenter.FileDidFailUpload) {
            if (objArr[0].equals(this.uploadingImage)) {
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.FileDidUpload);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.FileDidFailUpload);
                this.uploadingImage = null;
                if (this.clearAfterUpdate) {
                    this.imageReceiver.setImageBitmap((Drawable) null);
                    this.parentFragment = null;
                    this.delegate = null;
                }
            }
        } else if ((i == NotificationCenter.fileDidLoad || i == NotificationCenter.fileDidFailToLoad || i == NotificationCenter.httpFileDidLoad || i == NotificationCenter.httpFileDidFailedLoad) && objArr[0].equals(this.uploadingImage)) {
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileDidLoad);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileDidFailToLoad);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.httpFileDidLoad);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.httpFileDidFailedLoad);
            this.uploadingImage = null;
            if (i == NotificationCenter.fileDidLoad || i == NotificationCenter.httpFileDidLoad) {
                processBitmap(ImageLoader.loadBitmap(this.finalPath, (Uri) null, 800.0f, 800.0f, true));
            } else {
                this.imageReceiver.setImageBitmap((Drawable) null);
            }
        }
    }
}
