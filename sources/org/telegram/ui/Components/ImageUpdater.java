package org.telegram.ui.Components;

import android.app.Dialog;
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
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.ChatAttachAlert;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.PhotoAlbumPickerActivity;
import org.telegram.ui.PhotoCropActivity;
import org.telegram.ui.PhotoPickerActivity;
import org.telegram.ui.PhotoViewer;

public class ImageUpdater implements NotificationCenter.NotificationCenterDelegate, PhotoCropActivity.PhotoEditActivityDelegate {
    private static final int ID_RECORD_VIDEO = 4;
    private static final int ID_REMOVE_PHOTO = 3;
    private static final int ID_SEARCH_WEB = 2;
    private static final int ID_TAKE_PHOTO = 0;
    private static final int ID_UPLOAD_FROM_GALLERY = 1;
    private static final int attach_photo = 0;
    private TLRPC.PhotoSize bigPhoto;
    private boolean canSelectVideo;
    /* access modifiers changed from: private */
    public ChatAttachAlert chatAttachAlert;
    private boolean clearAfterUpdate;
    private MessageObject convertingVideo;
    private int currentAccount = UserConfig.selectedAccount;
    public String currentPicturePath;
    /* access modifiers changed from: private */
    public ImageUpdaterDelegate delegate;
    private String finalPath;
    private boolean forceDarkTheme;
    private ImageReceiver imageReceiver;
    private boolean openWithFrontfaceCamera;
    public BaseFragment parentFragment;
    private File picturePath = null;
    private boolean searchAvailable;
    private boolean showingFromDialog;
    private TLRPC.PhotoSize smallPhoto;
    private boolean uploadAfterSelect;
    private TLRPC.InputFile uploadedPhoto;
    private TLRPC.InputFile uploadedVideo;
    private String uploadingImage;
    private String uploadingVideo;
    private boolean useAttachMenu;
    private String videoPath;
    private double videoTimestamp;

    public interface ImageUpdaterDelegate {
        void didStartUpload(boolean z);

        void didUploadPhoto(TLRPC.InputFile inputFile, TLRPC.InputFile inputFile2, double d, String str, TLRPC.PhotoSize photoSize, TLRPC.PhotoSize photoSize2);

        String getInitialSearchString();

        void onUploadProgressChanged(float f);

        /* renamed from: org.telegram.ui.Components.ImageUpdater$ImageUpdaterDelegate$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static String $default$getInitialSearchString(ImageUpdaterDelegate _this) {
                return null;
            }

            public static void $default$onUploadProgressChanged(ImageUpdaterDelegate _this, float progress) {
            }

            public static void $default$didStartUpload(ImageUpdaterDelegate _this, boolean isVideo) {
            }
        }
    }

    public boolean isUploadingImage() {
        return (this.uploadingImage == null && this.uploadingVideo == null && this.convertingVideo == null) ? false : true;
    }

    public void clear() {
        if (this.uploadingImage == null && this.uploadingVideo == null && this.convertingVideo == null) {
            this.parentFragment = null;
            this.delegate = null;
        } else {
            this.clearAfterUpdate = true;
        }
        ChatAttachAlert chatAttachAlert2 = this.chatAttachAlert;
        if (chatAttachAlert2 != null) {
            chatAttachAlert2.dismissInternal();
            this.chatAttachAlert.onDestroy();
        }
    }

    public void setOpenWithFrontfaceCamera(boolean value) {
        this.openWithFrontfaceCamera = value;
    }

    public ImageUpdater(boolean allowVideo) {
        boolean z = true;
        this.useAttachMenu = true;
        this.searchAvailable = true;
        this.uploadAfterSelect = true;
        this.imageReceiver = new ImageReceiver((View) null);
        this.canSelectVideo = (!allowVideo || Build.VERSION.SDK_INT <= 18) ? false : z;
    }

    public void setDelegate(ImageUpdaterDelegate imageUpdaterDelegate) {
        this.delegate = imageUpdaterDelegate;
    }

    public void openMenu(boolean hasAvatar, Runnable onDeleteAvatar, DialogInterface.OnDismissListener onDismiss) {
        BaseFragment baseFragment = this.parentFragment;
        if (baseFragment != null && baseFragment.getParentActivity() != null) {
            if (this.useAttachMenu) {
                openAttachMenu(onDismiss);
                return;
            }
            BottomSheet.Builder builder = new BottomSheet.Builder(this.parentFragment.getParentActivity());
            builder.setTitle(LocaleController.getString("ChoosePhoto", NUM), true);
            ArrayList<CharSequence> items = new ArrayList<>();
            ArrayList<Integer> icons = new ArrayList<>();
            ArrayList<Integer> ids = new ArrayList<>();
            items.add(LocaleController.getString("ChooseTakePhoto", NUM));
            icons.add(NUM);
            ids.add(0);
            if (this.canSelectVideo) {
                items.add(LocaleController.getString("ChooseRecordVideo", NUM));
                icons.add(NUM);
                ids.add(4);
            }
            items.add(LocaleController.getString("ChooseFromGallery", NUM));
            icons.add(NUM);
            ids.add(1);
            if (this.searchAvailable) {
                items.add(LocaleController.getString("ChooseFromSearch", NUM));
                icons.add(NUM);
                ids.add(2);
            }
            if (hasAvatar) {
                items.add(LocaleController.getString("DeletePhoto", NUM));
                icons.add(NUM);
                ids.add(3);
            }
            int[] iconsRes = new int[icons.size()];
            int N = icons.size();
            for (int i = 0; i < N; i++) {
                iconsRes[i] = icons.get(i).intValue();
            }
            builder.setItems((CharSequence[]) items.toArray(new CharSequence[0]), iconsRes, new ImageUpdater$$ExternalSyntheticLambda0(this, ids, onDeleteAvatar));
            BottomSheet sheet = builder.create();
            sheet.setOnHideListener(onDismiss);
            this.parentFragment.showDialog(sheet);
            if (hasAvatar) {
                sheet.setItemColor(items.size() - 1, Theme.getColor("dialogTextRed2"), Theme.getColor("dialogRedIcon"));
            }
        }
    }

    /* renamed from: lambda$openMenu$0$org-telegram-ui-Components-ImageUpdater  reason: not valid java name */
    public /* synthetic */ void m1027lambda$openMenu$0$orgtelegramuiComponentsImageUpdater(ArrayList ids, Runnable onDeleteAvatar, DialogInterface dialogInterface, int i) {
        switch (((Integer) ids.get(i)).intValue()) {
            case 0:
                openCamera();
                return;
            case 1:
                openGallery();
                return;
            case 2:
                openSearch();
                return;
            case 3:
                onDeleteAvatar.run();
                return;
            case 4:
                openVideoCamera();
                return;
            default:
                return;
        }
    }

    public void setSearchAvailable(boolean value) {
        this.searchAvailable = value;
        this.useAttachMenu = value;
    }

    public void setSearchAvailable(boolean value, boolean useAttachMenu2) {
        this.useAttachMenu = useAttachMenu2;
        this.searchAvailable = value;
    }

    public void setUploadAfterSelect(boolean value) {
        this.uploadAfterSelect = value;
    }

    public void onResume() {
        ChatAttachAlert chatAttachAlert2 = this.chatAttachAlert;
        if (chatAttachAlert2 != null) {
            chatAttachAlert2.onResume();
        }
    }

    public void onPause() {
        ChatAttachAlert chatAttachAlert2 = this.chatAttachAlert;
        if (chatAttachAlert2 != null) {
            chatAttachAlert2.onPause();
        }
    }

    public boolean dismissDialogOnPause(Dialog dialog) {
        return dialog != this.chatAttachAlert;
    }

    public boolean dismissCurrentDialog(Dialog dialog) {
        ChatAttachAlert chatAttachAlert2 = this.chatAttachAlert;
        if (chatAttachAlert2 == null || dialog != chatAttachAlert2) {
            return false;
        }
        chatAttachAlert2.getPhotoLayout().closeCamera(false);
        this.chatAttachAlert.dismissInternal();
        this.chatAttachAlert.getPhotoLayout().hideCamera(true);
        return true;
    }

    public void openSearch() {
        if (this.parentFragment != null) {
            final HashMap<Object, Object> photos = new HashMap<>();
            final ArrayList<Object> order = new ArrayList<>();
            PhotoPickerActivity fragment = new PhotoPickerActivity(0, (MediaController.AlbumEntry) null, photos, order, 1, false, (ChatActivity) null, this.forceDarkTheme);
            fragment.setDelegate(new PhotoPickerActivity.PhotoPickerActivityDelegate() {
                private boolean sendPressed;

                public /* synthetic */ void onOpenInPressed() {
                    PhotoPickerActivity.PhotoPickerActivityDelegate.CC.$default$onOpenInPressed(this);
                }

                public void selectedPhotosChanged() {
                }

                private void sendSelectedPhotos(HashMap<Object, Object> hashMap, ArrayList<Object> arrayList, boolean notify, int scheduleDate) {
                }

                public void actionButtonPressed(boolean canceled, boolean notify, int scheduleDate) {
                    if (!photos.isEmpty() && ImageUpdater.this.delegate != null && !this.sendPressed && !canceled) {
                        this.sendPressed = true;
                        ArrayList<SendMessagesHelper.SendingMediaInfo> media = new ArrayList<>();
                        for (int a = 0; a < order.size(); a++) {
                            Object object = photos.get(order.get(a));
                            SendMessagesHelper.SendingMediaInfo info = new SendMessagesHelper.SendingMediaInfo();
                            media.add(info);
                            if (object instanceof MediaController.SearchImage) {
                                MediaController.SearchImage searchImage = (MediaController.SearchImage) object;
                                if (searchImage.imagePath != null) {
                                    info.path = searchImage.imagePath;
                                } else {
                                    info.searchImage = searchImage;
                                }
                                info.videoEditedInfo = searchImage.editedInfo;
                                info.thumbPath = searchImage.thumbPath;
                                info.caption = searchImage.caption != null ? searchImage.caption.toString() : null;
                                info.entities = searchImage.entities;
                                info.masks = searchImage.stickers;
                                info.ttl = searchImage.ttl;
                            }
                        }
                        ImageUpdater.this.didSelectPhotos(media);
                    }
                }

                public void onCaptionChanged(CharSequence caption) {
                }
            });
            fragment.setMaxSelectedPhotos(1, false);
            fragment.setInitialSearchString(this.delegate.getInitialSearchString());
            if (this.showingFromDialog) {
                this.parentFragment.showAsSheet(fragment);
            } else {
                this.parentFragment.presentFragment(fragment);
            }
        }
    }

    private void openAttachMenu(DialogInterface.OnDismissListener onDismissListener) {
        BaseFragment baseFragment = this.parentFragment;
        if (baseFragment != null && baseFragment.getParentActivity() != null) {
            createChatAttachView();
            this.chatAttachAlert.setOpenWithFrontFaceCamera(this.openWithFrontfaceCamera);
            this.chatAttachAlert.setMaxSelectedPhotos(1, false);
            this.chatAttachAlert.getPhotoLayout().loadGalleryPhotos();
            if (Build.VERSION.SDK_INT == 21 || Build.VERSION.SDK_INT == 22) {
                AndroidUtilities.hideKeyboard(this.parentFragment.getFragmentView().findFocus());
            }
            this.chatAttachAlert.init();
            this.chatAttachAlert.setOnHideListener(onDismissListener);
            this.parentFragment.showDialog(this.chatAttachAlert);
        }
    }

    private void createChatAttachView() {
        BaseFragment baseFragment = this.parentFragment;
        if (baseFragment != null && baseFragment.getParentActivity() != null && this.chatAttachAlert == null) {
            ChatAttachAlert chatAttachAlert2 = new ChatAttachAlert(this.parentFragment.getParentActivity(), this.parentFragment, this.forceDarkTheme, this.showingFromDialog);
            this.chatAttachAlert = chatAttachAlert2;
            chatAttachAlert2.setAvatarPicker(this.canSelectVideo ? 2 : 1, this.searchAvailable);
            this.chatAttachAlert.setDelegate(new ChatAttachAlert.ChatAttachViewDelegate() {
                public void didPressedButton(int button, boolean arg, boolean notify, int scheduleDate, boolean forceDocument) {
                    int i = button;
                    if (ImageUpdater.this.parentFragment != null && ImageUpdater.this.parentFragment.getParentActivity() != null && ImageUpdater.this.chatAttachAlert != null) {
                        if (i == 8 || i == 7) {
                            HashMap<Object, Object> photos = ImageUpdater.this.chatAttachAlert.getPhotoLayout().getSelectedPhotos();
                            ArrayList<Object> order = ImageUpdater.this.chatAttachAlert.getPhotoLayout().getSelectedPhotosOrder();
                            ArrayList<SendMessagesHelper.SendingMediaInfo> media = new ArrayList<>();
                            for (int a = 0; a < order.size(); a++) {
                                Object object = photos.get(order.get(a));
                                SendMessagesHelper.SendingMediaInfo info = new SendMessagesHelper.SendingMediaInfo();
                                media.add(info);
                                String str = null;
                                if (object instanceof MediaController.PhotoEntry) {
                                    MediaController.PhotoEntry photoEntry = (MediaController.PhotoEntry) object;
                                    if (photoEntry.imagePath != null) {
                                        info.path = photoEntry.imagePath;
                                    } else {
                                        info.path = photoEntry.path;
                                    }
                                    info.thumbPath = photoEntry.thumbPath;
                                    info.videoEditedInfo = photoEntry.editedInfo;
                                    info.isVideo = photoEntry.isVideo;
                                    if (photoEntry.caption != null) {
                                        str = photoEntry.caption.toString();
                                    }
                                    info.caption = str;
                                    info.entities = photoEntry.entities;
                                    info.masks = photoEntry.stickers;
                                    info.ttl = photoEntry.ttl;
                                } else if (object instanceof MediaController.SearchImage) {
                                    MediaController.SearchImage searchImage = (MediaController.SearchImage) object;
                                    if (searchImage.imagePath != null) {
                                        info.path = searchImage.imagePath;
                                    } else {
                                        info.searchImage = searchImage;
                                    }
                                    info.thumbPath = searchImage.thumbPath;
                                    info.videoEditedInfo = searchImage.editedInfo;
                                    if (searchImage.caption != null) {
                                        str = searchImage.caption.toString();
                                    }
                                    info.caption = str;
                                    info.entities = searchImage.entities;
                                    info.masks = searchImage.stickers;
                                    info.ttl = searchImage.ttl;
                                    if (searchImage.inlineResult != null && searchImage.type == 1) {
                                        info.inlineResult = searchImage.inlineResult;
                                        info.params = searchImage.params;
                                    }
                                    searchImage.date = (int) (System.currentTimeMillis() / 1000);
                                }
                            }
                            ImageUpdater.this.didSelectPhotos(media);
                            if (i != 8) {
                                ImageUpdater.this.chatAttachAlert.dismiss(true);
                                return;
                            }
                            return;
                        }
                        ImageUpdater.this.chatAttachAlert.dismissWithButtonClick(i);
                        processSelectedAttach(button);
                    }
                }

                public View getRevealView() {
                    return null;
                }

                public void didSelectBot(TLRPC.User user) {
                }

                public void onCameraOpened() {
                    AndroidUtilities.hideKeyboard(ImageUpdater.this.parentFragment.getFragmentView().findFocus());
                }

                public boolean needEnterComment() {
                    return false;
                }

                public void doOnIdle(Runnable runnable) {
                    runnable.run();
                }

                private void processSelectedAttach(int which) {
                    if (which == 0) {
                        ImageUpdater.this.openCamera();
                    }
                }

                public void openAvatarsSearch() {
                    ImageUpdater.this.openSearch();
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public void didSelectPhotos(ArrayList<SendMessagesHelper.SendingMediaInfo> photos) {
        if (!photos.isEmpty()) {
            SendMessagesHelper.SendingMediaInfo info = photos.get(0);
            Bitmap bitmap = null;
            MessageObject avatarObject = null;
            if (info.isVideo || info.videoEditedInfo != null) {
                TLRPC.TL_message message = new TLRPC.TL_message();
                message.id = 0;
                message.message = "";
                message.media = new TLRPC.TL_messageMediaEmpty();
                message.action = new TLRPC.TL_messageActionEmpty();
                message.dialog_id = 0;
                avatarObject = new MessageObject(UserConfig.selectedAccount, message, false, false);
                TLRPC.Message message2 = avatarObject.messageOwner;
                File directory = FileLoader.getDirectory(4);
                message2.attachPath = new File(directory, SharedConfig.getLastLocalId() + "_avatar.mp4").getAbsolutePath();
                avatarObject.videoEditedInfo = info.videoEditedInfo;
                bitmap = ImageLoader.loadBitmap(info.thumbPath, (Uri) null, 800.0f, 800.0f, true);
            } else if (info.path != null) {
                bitmap = ImageLoader.loadBitmap(info.path, (Uri) null, 800.0f, 800.0f, true);
            } else if (info.searchImage != null) {
                if (info.searchImage.photo != null) {
                    TLRPC.PhotoSize photoSize = FileLoader.getClosestPhotoSizeWithSize(info.searchImage.photo.sizes, AndroidUtilities.getPhotoSize());
                    if (photoSize != null) {
                        File path = FileLoader.getInstance(this.currentAccount).getPathToAttach(photoSize, true);
                        this.finalPath = path.getAbsolutePath();
                        if (!path.exists()) {
                            path = FileLoader.getInstance(this.currentAccount).getPathToAttach(photoSize, false);
                            if (!path.exists()) {
                                path = null;
                            }
                        }
                        if (path != null) {
                            bitmap = ImageLoader.loadBitmap(path.getAbsolutePath(), (Uri) null, 800.0f, 800.0f, true);
                        } else {
                            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileLoaded);
                            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileLoadFailed);
                            this.uploadingImage = FileLoader.getAttachFileName(photoSize.location);
                            this.imageReceiver.setImage(ImageLocation.getForPhoto(photoSize, info.searchImage.photo), (String) null, (Drawable) null, "jpg", (Object) null, 1);
                        }
                    }
                } else if (info.searchImage.imageUrl != null) {
                    File cacheFile = new File(FileLoader.getDirectory(4), Utilities.MD5(info.searchImage.imageUrl) + "." + ImageLoader.getHttpUrlExtension(info.searchImage.imageUrl, "jpg"));
                    this.finalPath = cacheFile.getAbsolutePath();
                    if (!cacheFile.exists() || cacheFile.length() == 0) {
                        this.uploadingImage = info.searchImage.imageUrl;
                        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.httpFileDidLoad);
                        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.httpFileDidFailedLoad);
                        this.imageReceiver.setImage(info.searchImage.imageUrl, (String) null, (Drawable) null, "jpg", 1);
                    } else {
                        bitmap = ImageLoader.loadBitmap(cacheFile.getAbsolutePath(), (Uri) null, 800.0f, 800.0f, true);
                    }
                }
            }
            processBitmap(bitmap, avatarObject);
        }
    }

    public void openCamera() {
        BaseFragment baseFragment = this.parentFragment;
        if (baseFragment != null && baseFragment.getParentActivity() != null) {
            try {
                if (Build.VERSION.SDK_INT < 23 || this.parentFragment.getParentActivity().checkSelfPermission("android.permission.CAMERA") == 0) {
                    Intent takePictureIntent = new Intent("android.media.action.IMAGE_CAPTURE");
                    File image = AndroidUtilities.generatePicturePath();
                    if (image != null) {
                        if (Build.VERSION.SDK_INT >= 24) {
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
                this.parentFragment.getParentActivity().requestPermissions(new String[]{"android.permission.CAMERA"}, 20);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    public void openVideoCamera() {
        BaseFragment baseFragment = this.parentFragment;
        if (baseFragment != null && baseFragment.getParentActivity() != null) {
            try {
                if (Build.VERSION.SDK_INT < 23 || this.parentFragment.getParentActivity().checkSelfPermission("android.permission.CAMERA") == 0) {
                    Intent takeVideoIntent = new Intent("android.media.action.VIDEO_CAPTURE");
                    File video = AndroidUtilities.generateVideoPath();
                    if (video != null) {
                        if (Build.VERSION.SDK_INT >= 24) {
                            takeVideoIntent.putExtra("output", FileProvider.getUriForFile(this.parentFragment.getParentActivity(), "org.telegram.messenger.beta.provider", video));
                            takeVideoIntent.addFlags(2);
                            takeVideoIntent.addFlags(1);
                        } else if (Build.VERSION.SDK_INT >= 18) {
                            takeVideoIntent.putExtra("output", Uri.fromFile(video));
                        }
                        takeVideoIntent.putExtra("android.intent.extras.CAMERA_FACING", 1);
                        takeVideoIntent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1);
                        takeVideoIntent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true);
                        takeVideoIntent.putExtra("android.intent.extra.durationLimit", 10);
                        this.currentPicturePath = video.getAbsolutePath();
                    }
                    this.parentFragment.startActivityForResult(takeVideoIntent, 15);
                    return;
                }
                this.parentFragment.getParentActivity().requestPermissions(new String[]{"android.permission.CAMERA"}, 19);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    public void onRequestPermissionsResultFragment(int requestCode, String[] permissions, int[] grantResults) {
        ChatAttachAlert chatAttachAlert2 = this.chatAttachAlert;
        if (chatAttachAlert2 == null) {
            return;
        }
        if (requestCode == 17) {
            chatAttachAlert2.getPhotoLayout().checkCamera(false);
            this.chatAttachAlert.getPhotoLayout().checkStorage();
        } else if (requestCode == 4) {
            chatAttachAlert2.getPhotoLayout().checkStorage();
        }
    }

    public void openGallery() {
        if (this.parentFragment != null) {
            if (Build.VERSION.SDK_INT < 23 || this.parentFragment.getParentActivity() == null || this.parentFragment.getParentActivity().checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == 0) {
                PhotoAlbumPickerActivity fragment = new PhotoAlbumPickerActivity(this.canSelectVideo ? PhotoAlbumPickerActivity.SELECT_TYPE_AVATAR_VIDEO : PhotoAlbumPickerActivity.SELECT_TYPE_AVATAR, false, false, (ChatActivity) null);
                fragment.setAllowSearchImages(this.searchAvailable);
                fragment.setDelegate(new PhotoAlbumPickerActivity.PhotoAlbumPickerActivityDelegate() {
                    public void didSelectPhotos(ArrayList<SendMessagesHelper.SendingMediaInfo> photos, boolean notify, int scheduleDate) {
                        ImageUpdater.this.didSelectPhotos(photos);
                    }

                    public void startPhotoSelectActivity() {
                        try {
                            Intent photoPickerIntent = new Intent("android.intent.action.GET_CONTENT");
                            photoPickerIntent.setType("image/*");
                            ImageUpdater.this.parentFragment.startActivityForResult(photoPickerIntent, 14);
                        } catch (Exception e) {
                            FileLog.e((Throwable) e);
                        }
                    }
                });
                this.parentFragment.presentFragment(fragment);
                return;
            }
            this.parentFragment.getParentActivity().requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 151);
        }
    }

    private void startCrop(String path, Uri uri) {
        AndroidUtilities.runOnUIThread(new ImageUpdater$$ExternalSyntheticLambda1(this, path, uri));
    }

    /* renamed from: lambda$startCrop$1$org-telegram-ui-Components-ImageUpdater  reason: not valid java name */
    public /* synthetic */ void m1028lambda$startCrop$1$orgtelegramuiComponentsImageUpdater(String path, Uri uri) {
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
                activity.m3690lambda$runLinkRequest$59$orgtelegramuiLaunchActivity(photoCropActivity);
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            processBitmap(ImageLoader.loadBitmap(path, uri, 800.0f, 800.0f, true), (MessageObject) null);
        }
    }

    public void openPhotoForEdit(String path, String thumb, int orientation, boolean isVideo) {
        final ArrayList<Object> arrayList = new ArrayList<>();
        MediaController.PhotoEntry photoEntry = new MediaController.PhotoEntry(0, 0, 0, path, orientation, false, 0, 0, 0);
        photoEntry.isVideo = isVideo;
        photoEntry.thumbPath = thumb;
        arrayList.add(photoEntry);
        PhotoViewer.getInstance().setParentActivity(this.parentFragment.getParentActivity());
        PhotoViewer.getInstance().openPhotoForSelect(arrayList, 0, 1, false, new PhotoViewer.EmptyPhotoViewerProvider() {
            public void sendButtonPressed(int index, VideoEditedInfo videoEditedInfo, boolean notify, int scheduleDate, boolean forceDocument) {
                Bitmap bitmap;
                String path = null;
                MediaController.PhotoEntry photoEntry = (MediaController.PhotoEntry) arrayList.get(0);
                if (photoEntry.imagePath != null) {
                    path = photoEntry.imagePath;
                } else if (photoEntry.path != null) {
                    path = photoEntry.path;
                }
                MessageObject avatarObject = null;
                if (photoEntry.isVideo || photoEntry.editedInfo != null) {
                    TLRPC.TL_message message = new TLRPC.TL_message();
                    message.id = 0;
                    message.message = "";
                    message.media = new TLRPC.TL_messageMediaEmpty();
                    message.action = new TLRPC.TL_messageActionEmpty();
                    message.dialog_id = 0;
                    avatarObject = new MessageObject(UserConfig.selectedAccount, message, false, false);
                    TLRPC.Message message2 = avatarObject.messageOwner;
                    File directory = FileLoader.getDirectory(4);
                    message2.attachPath = new File(directory, SharedConfig.getLastLocalId() + "_avatar.mp4").getAbsolutePath();
                    avatarObject.videoEditedInfo = photoEntry.editedInfo;
                    bitmap = ImageLoader.loadBitmap(photoEntry.thumbPath, (Uri) null, 800.0f, 800.0f, true);
                } else {
                    bitmap = ImageLoader.loadBitmap(path, (Uri) null, 800.0f, 800.0f, true);
                }
                ImageUpdater.this.processBitmap(bitmap, avatarObject);
            }

            public boolean allowCaption() {
                return false;
            }

            public boolean canScrollAway() {
                return false;
            }
        }, (ChatActivity) null);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != -1) {
            return;
        }
        if (requestCode == 0 || requestCode == 2) {
            createChatAttachView();
            ChatAttachAlert chatAttachAlert2 = this.chatAttachAlert;
            if (chatAttachAlert2 != null) {
                chatAttachAlert2.onActivityResultFragment(requestCode, data, this.currentPicturePath);
            }
            this.currentPicturePath = null;
        } else if (requestCode == 13) {
            this.parentFragment.getParentActivity().overridePendingTransition(NUM, NUM);
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
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            openPhotoForEdit(this.currentPicturePath, (String) null, orientation, false);
            AndroidUtilities.addMediaToGallery(this.currentPicturePath);
            this.currentPicturePath = null;
        } else if (requestCode == 14) {
            if (data != null && data.getData() != null) {
                startCrop((String) null, data.getData());
            }
        } else if (requestCode == 15) {
            openPhotoForEdit(this.currentPicturePath, (String) null, 0, true);
            AndroidUtilities.addMediaToGallery(this.currentPicturePath);
            this.currentPicturePath = null;
        }
    }

    /* access modifiers changed from: private */
    public void processBitmap(Bitmap bitmap, MessageObject avatarObject) {
        if (bitmap != null) {
            this.uploadedVideo = null;
            this.uploadedPhoto = null;
            this.convertingVideo = null;
            this.videoPath = null;
            this.bigPhoto = ImageLoader.scaleAndSaveImage(bitmap, 800.0f, 800.0f, 80, false, 320, 320);
            TLRPC.PhotoSize scaleAndSaveImage = ImageLoader.scaleAndSaveImage(bitmap, 150.0f, 150.0f, 80, false, 150, 150);
            this.smallPhoto = scaleAndSaveImage;
            if (scaleAndSaveImage != null) {
                try {
                    Bitmap b = BitmapFactory.decodeFile(FileLoader.getInstance(this.currentAccount).getPathToAttach(this.smallPhoto, true).getAbsolutePath());
                    ImageLoader.getInstance().putImageToCache(new BitmapDrawable(b), this.smallPhoto.location.volume_id + "_" + this.smallPhoto.location.local_id + "@50_50", true);
                } catch (Throwable th) {
                }
            }
            bitmap.recycle();
            if (this.bigPhoto != null) {
                UserConfig.getInstance(this.currentAccount).saveConfig(false);
                this.uploadingImage = FileLoader.getDirectory(4) + "/" + this.bigPhoto.location.volume_id + "_" + this.bigPhoto.location.local_id + ".jpg";
                if (this.uploadAfterSelect) {
                    if (avatarObject == null || avatarObject.videoEditedInfo == null) {
                        ImageUpdaterDelegate imageUpdaterDelegate = this.delegate;
                        if (imageUpdaterDelegate != null) {
                            imageUpdaterDelegate.didStartUpload(false);
                        }
                    } else {
                        this.convertingVideo = avatarObject;
                        long startTime = 0;
                        if (avatarObject.videoEditedInfo.startTime >= 0) {
                            startTime = avatarObject.videoEditedInfo.startTime;
                        }
                        double d = (double) (avatarObject.videoEditedInfo.avatarStartTime - startTime);
                        Double.isNaN(d);
                        this.videoTimestamp = d / 1000000.0d;
                        avatarObject.videoEditedInfo.shouldLimitFps = false;
                        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.filePreparingStarted);
                        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.filePreparingFailed);
                        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileNewChunkAvailable);
                        MediaController.getInstance().scheduleVideoConvert(avatarObject, true);
                        this.uploadingImage = null;
                        ImageUpdaterDelegate imageUpdaterDelegate2 = this.delegate;
                        if (imageUpdaterDelegate2 != null) {
                            imageUpdaterDelegate2.didStartUpload(true);
                        }
                    }
                    NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileUploaded);
                    NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileUploadProgressChanged);
                    NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileUploadFailed);
                    if (this.uploadingImage != null) {
                        FileLoader.getInstance(this.currentAccount).uploadFile(this.uploadingImage, false, true, 16777216);
                    }
                }
                ImageUpdaterDelegate imageUpdaterDelegate3 = this.delegate;
                if (imageUpdaterDelegate3 != null) {
                    imageUpdaterDelegate3.didUploadPhoto((TLRPC.InputFile) null, (TLRPC.InputFile) null, 0.0d, (String) null, this.bigPhoto, this.smallPhoto);
                }
            }
        }
    }

    public void didFinishEdit(Bitmap bitmap) {
        processBitmap(bitmap, (MessageObject) null);
    }

    private void cleanup() {
        this.uploadingImage = null;
        this.uploadingVideo = null;
        this.videoPath = null;
        this.convertingVideo = null;
        if (this.clearAfterUpdate) {
            this.imageReceiver.setImageBitmap((Drawable) null);
            this.parentFragment = null;
            this.delegate = null;
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        ImageUpdaterDelegate imageUpdaterDelegate;
        BaseFragment baseFragment;
        BaseFragment baseFragment2;
        int i = id;
        if (i == NotificationCenter.fileUploaded || i == NotificationCenter.fileUploadFailed) {
            String location = args[0];
            if (location.equals(this.uploadingImage)) {
                this.uploadingImage = null;
                if (i == NotificationCenter.fileUploaded) {
                    this.uploadedPhoto = args[1];
                }
            } else if (location.equals(this.uploadingVideo)) {
                this.uploadingVideo = null;
                if (i == NotificationCenter.fileUploaded) {
                    this.uploadedVideo = args[1];
                }
            } else {
                return;
            }
            if (this.uploadingImage == null && this.uploadingVideo == null && this.convertingVideo == null) {
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileUploaded);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileUploadProgressChanged);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileUploadFailed);
                if (i == NotificationCenter.fileUploaded && (imageUpdaterDelegate = this.delegate) != null) {
                    imageUpdaterDelegate.didUploadPhoto(this.uploadedPhoto, this.uploadedVideo, this.videoTimestamp, this.videoPath, this.bigPhoto, this.smallPhoto);
                }
                cleanup();
            }
        } else if (i == NotificationCenter.fileUploadProgressChanged) {
            String location2 = args[0];
            String path = this.convertingVideo != null ? this.uploadingVideo : this.uploadingImage;
            if (this.delegate != null && location2.equals(path)) {
                this.delegate.onUploadProgressChanged(Math.min(1.0f, ((float) args[1].longValue()) / ((float) args[2].longValue())));
            }
        } else if (i == NotificationCenter.fileLoaded || i == NotificationCenter.fileLoadFailed || i == NotificationCenter.httpFileDidLoad || i == NotificationCenter.httpFileDidFailedLoad) {
            if (args[0].equals(this.uploadingImage)) {
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileLoaded);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileLoadFailed);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.httpFileDidLoad);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.httpFileDidFailedLoad);
                this.uploadingImage = null;
                if (i == NotificationCenter.fileLoaded || i == NotificationCenter.httpFileDidLoad) {
                    processBitmap(ImageLoader.loadBitmap(this.finalPath, (Uri) null, 800.0f, 800.0f, true), (MessageObject) null);
                } else {
                    this.imageReceiver.setImageBitmap((Drawable) null);
                }
            }
        } else if (i == NotificationCenter.filePreparingFailed) {
            MessageObject messageObject = args[0];
            if (messageObject == this.convertingVideo && (baseFragment2 = this.parentFragment) != null) {
                baseFragment2.getSendMessagesHelper().stopVideoService(messageObject.messageOwner.attachPath);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.filePreparingStarted);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.filePreparingFailed);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileNewChunkAvailable);
                cleanup();
            }
        } else if (i == NotificationCenter.fileNewChunkAvailable) {
            MessageObject messageObject2 = args[0];
            if (messageObject2 == this.convertingVideo && this.parentFragment != null) {
                String finalPath2 = args[1];
                long availableSize = args[2].longValue();
                long finalSize = args[3].longValue();
                this.parentFragment.getFileLoader().checkUploadNewDataAvailable(finalPath2, false, availableSize, finalSize);
                if (finalSize != 0) {
                    double longValue = (double) args[5].longValue();
                    Double.isNaN(longValue);
                    double lastFrameTimestamp = longValue / 1000000.0d;
                    if (this.videoTimestamp > lastFrameTimestamp) {
                        this.videoTimestamp = lastFrameTimestamp;
                    }
                    Bitmap bitmap = SendMessagesHelper.createVideoThumbnailAtTime(finalPath2, (long) (this.videoTimestamp * 1000.0d), (int[]) null, true);
                    if (bitmap != null) {
                        File path2 = FileLoader.getInstance(this.currentAccount).getPathToAttach(this.smallPhoto, true);
                        if (path2 != null) {
                            path2.delete();
                        }
                        File path3 = FileLoader.getInstance(this.currentAccount).getPathToAttach(this.bigPhoto, true);
                        if (path3 != null) {
                            path3.delete();
                        }
                        Bitmap bitmap2 = bitmap;
                        this.bigPhoto = ImageLoader.scaleAndSaveImage(bitmap2, 800.0f, 800.0f, 80, false, 320, 320);
                        TLRPC.PhotoSize scaleAndSaveImage = ImageLoader.scaleAndSaveImage(bitmap2, 150.0f, 150.0f, 80, false, 150, 150);
                        this.smallPhoto = scaleAndSaveImage;
                        if (scaleAndSaveImage != null) {
                            try {
                                Bitmap b = BitmapFactory.decodeFile(FileLoader.getInstance(this.currentAccount).getPathToAttach(this.smallPhoto, true).getAbsolutePath());
                                ImageLoader.getInstance().putImageToCache(new BitmapDrawable(b), this.smallPhoto.location.volume_id + "_" + this.smallPhoto.location.local_id + "@50_50", true);
                            } catch (Throwable th) {
                            }
                        }
                    }
                    NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.filePreparingStarted);
                    NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.filePreparingFailed);
                    NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileNewChunkAvailable);
                    this.parentFragment.getSendMessagesHelper().stopVideoService(messageObject2.messageOwner.attachPath);
                    this.videoPath = finalPath2;
                    this.uploadingVideo = finalPath2;
                    this.convertingVideo = null;
                }
            }
        } else if (i == NotificationCenter.filePreparingStarted && args[0] == this.convertingVideo && (baseFragment = this.parentFragment) != null) {
            this.uploadingVideo = args[1];
            baseFragment.getFileLoader().uploadFile(this.uploadingVideo, false, false, (long) ((int) this.convertingVideo.videoEditedInfo.estimatedSize), 33554432, false);
        }
    }

    public void setForceDarkTheme(boolean forceDarkTheme2) {
        this.forceDarkTheme = forceDarkTheme2;
    }

    public void setShowingFromDialog(boolean b) {
        this.showingFromDialog = b;
    }
}
