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
import org.telegram.tgnet.TLRPC$BotInlineResult;
import org.telegram.tgnet.TLRPC$InputFile;
import org.telegram.tgnet.TLRPC$Photo;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$TL_message;
import org.telegram.tgnet.TLRPC$TL_messageActionEmpty;
import org.telegram.tgnet.TLRPC$TL_messageMediaEmpty;
import org.telegram.tgnet.TLRPC$User;
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
    private TLRPC$PhotoSize bigPhoto;
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
    private boolean searchAvailable;
    private boolean showingFromDialog;
    private TLRPC$PhotoSize smallPhoto;
    private boolean uploadAfterSelect;
    private TLRPC$InputFile uploadedPhoto;
    private TLRPC$InputFile uploadedVideo;
    private String uploadingImage;
    private String uploadingVideo;
    private boolean useAttachMenu;
    private String videoPath;
    private double videoTimestamp;

    public interface ImageUpdaterDelegate {

        /* renamed from: org.telegram.ui.Components.ImageUpdater$ImageUpdaterDelegate$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static void $default$didStartUpload(ImageUpdaterDelegate imageUpdaterDelegate, boolean z) {
            }

            public static String $default$getInitialSearchString(ImageUpdaterDelegate imageUpdaterDelegate) {
                return null;
            }

            public static void $default$onUploadProgressChanged(ImageUpdaterDelegate imageUpdaterDelegate, float f) {
            }
        }

        void didStartUpload(boolean z);

        void didUploadPhoto(TLRPC$InputFile tLRPC$InputFile, TLRPC$InputFile tLRPC$InputFile2, double d, String str, TLRPC$PhotoSize tLRPC$PhotoSize, TLRPC$PhotoSize tLRPC$PhotoSize2);

        String getInitialSearchString();

        void onUploadProgressChanged(float f);
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

    public void setOpenWithFrontfaceCamera(boolean z) {
        this.openWithFrontfaceCamera = z;
    }

    public ImageUpdater(boolean z) {
        boolean z2 = true;
        this.useAttachMenu = true;
        this.searchAvailable = true;
        this.uploadAfterSelect = true;
        this.imageReceiver = new ImageReceiver((View) null);
        this.canSelectVideo = (!z || Build.VERSION.SDK_INT <= 18) ? false : z2;
    }

    public void setDelegate(ImageUpdaterDelegate imageUpdaterDelegate) {
        this.delegate = imageUpdaterDelegate;
    }

    public void openMenu(boolean z, Runnable runnable, DialogInterface.OnDismissListener onDismissListener) {
        BaseFragment baseFragment = this.parentFragment;
        if (baseFragment != null && baseFragment.getParentActivity() != null) {
            if (this.useAttachMenu) {
                openAttachMenu(onDismissListener);
                return;
            }
            BottomSheet.Builder builder = new BottomSheet.Builder(this.parentFragment.getParentActivity());
            builder.setTitle(LocaleController.getString("ChoosePhoto", NUM), true);
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            ArrayList arrayList3 = new ArrayList();
            arrayList.add(LocaleController.getString("ChooseTakePhoto", NUM));
            arrayList2.add(NUM);
            arrayList3.add(0);
            if (this.canSelectVideo) {
                arrayList.add(LocaleController.getString("ChooseRecordVideo", NUM));
                arrayList2.add(NUM);
                arrayList3.add(4);
            }
            arrayList.add(LocaleController.getString("ChooseFromGallery", NUM));
            arrayList2.add(NUM);
            arrayList3.add(1);
            if (this.searchAvailable) {
                arrayList.add(LocaleController.getString("ChooseFromSearch", NUM));
                arrayList2.add(NUM);
                arrayList3.add(2);
            }
            if (z) {
                arrayList.add(LocaleController.getString("DeletePhoto", NUM));
                arrayList2.add(NUM);
                arrayList3.add(3);
            }
            int[] iArr = new int[arrayList2.size()];
            int size = arrayList2.size();
            for (int i = 0; i < size; i++) {
                iArr[i] = ((Integer) arrayList2.get(i)).intValue();
            }
            builder.setItems((CharSequence[]) arrayList.toArray(new CharSequence[0]), iArr, new ImageUpdater$$ExternalSyntheticLambda0(this, arrayList3, runnable));
            BottomSheet create = builder.create();
            create.setOnHideListener(onDismissListener);
            this.parentFragment.showDialog(create);
            if (z) {
                create.setItemColor(arrayList.size() - 1, Theme.getColor("dialogTextRed2"), Theme.getColor("dialogRedIcon"));
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$openMenu$0(ArrayList arrayList, Runnable runnable, DialogInterface dialogInterface, int i) {
        int intValue = ((Integer) arrayList.get(i)).intValue();
        if (intValue == 0) {
            openCamera();
        } else if (intValue == 1) {
            openGallery();
        } else if (intValue == 2) {
            openSearch();
        } else if (intValue == 3) {
            runnable.run();
        } else if (intValue == 4) {
            openVideoCamera();
        }
    }

    public void setSearchAvailable(boolean z) {
        this.searchAvailable = z;
        this.useAttachMenu = z;
    }

    public void setSearchAvailable(boolean z, boolean z2) {
        this.useAttachMenu = z2;
        this.searchAvailable = z;
    }

    public void setUploadAfterSelect(boolean z) {
        this.uploadAfterSelect = z;
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
            final HashMap hashMap = new HashMap();
            final ArrayList arrayList = new ArrayList();
            PhotoPickerActivity photoPickerActivity = new PhotoPickerActivity(0, (MediaController.AlbumEntry) null, hashMap, arrayList, 1, false, (ChatActivity) null, this.forceDarkTheme);
            photoPickerActivity.setDelegate(new PhotoPickerActivity.PhotoPickerActivityDelegate() {
                private boolean sendPressed;

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
                                sendingMediaInfo.videoEditedInfo = searchImage.editedInfo;
                                sendingMediaInfo.thumbPath = searchImage.thumbPath;
                                CharSequence charSequence = searchImage.caption;
                                sendingMediaInfo.caption = charSequence != null ? charSequence.toString() : null;
                                sendingMediaInfo.entities = searchImage.entities;
                                sendingMediaInfo.masks = searchImage.stickers;
                                sendingMediaInfo.ttl = searchImage.ttl;
                            }
                        }
                        ImageUpdater.this.didSelectPhotos(arrayList);
                    }
                }
            });
            photoPickerActivity.setMaxSelectedPhotos(1, false);
            photoPickerActivity.setInitialSearchString(this.delegate.getInitialSearchString());
            if (this.showingFromDialog) {
                this.parentFragment.showAsSheet(photoPickerActivity);
            } else {
                this.parentFragment.presentFragment(photoPickerActivity);
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
            int i = Build.VERSION.SDK_INT;
            if (i == 21 || i == 22) {
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
                public void didSelectBot(TLRPC$User tLRPC$User) {
                }

                public boolean needEnterComment() {
                    return false;
                }

                public void didPressedButton(int i, boolean z, boolean z2, int i2, boolean z3) {
                    BaseFragment baseFragment = ImageUpdater.this.parentFragment;
                    if (baseFragment != null && baseFragment.getParentActivity() != null && ImageUpdater.this.chatAttachAlert != null) {
                        if (i == 8 || i == 7) {
                            HashMap<Object, Object> selectedPhotos = ImageUpdater.this.chatAttachAlert.getPhotoLayout().getSelectedPhotos();
                            ArrayList<Object> selectedPhotosOrder = ImageUpdater.this.chatAttachAlert.getPhotoLayout().getSelectedPhotosOrder();
                            ArrayList arrayList = new ArrayList();
                            for (int i3 = 0; i3 < selectedPhotosOrder.size(); i3++) {
                                Object obj = selectedPhotos.get(selectedPhotosOrder.get(i3));
                                SendMessagesHelper.SendingMediaInfo sendingMediaInfo = new SendMessagesHelper.SendingMediaInfo();
                                arrayList.add(sendingMediaInfo);
                                String str = null;
                                if (obj instanceof MediaController.PhotoEntry) {
                                    MediaController.PhotoEntry photoEntry = (MediaController.PhotoEntry) obj;
                                    String str2 = photoEntry.imagePath;
                                    if (str2 != null) {
                                        sendingMediaInfo.path = str2;
                                    } else {
                                        sendingMediaInfo.path = photoEntry.path;
                                    }
                                    sendingMediaInfo.thumbPath = photoEntry.thumbPath;
                                    sendingMediaInfo.videoEditedInfo = photoEntry.editedInfo;
                                    sendingMediaInfo.isVideo = photoEntry.isVideo;
                                    CharSequence charSequence = photoEntry.caption;
                                    if (charSequence != null) {
                                        str = charSequence.toString();
                                    }
                                    sendingMediaInfo.caption = str;
                                    sendingMediaInfo.entities = photoEntry.entities;
                                    sendingMediaInfo.masks = photoEntry.stickers;
                                    sendingMediaInfo.ttl = photoEntry.ttl;
                                } else if (obj instanceof MediaController.SearchImage) {
                                    MediaController.SearchImage searchImage = (MediaController.SearchImage) obj;
                                    String str3 = searchImage.imagePath;
                                    if (str3 != null) {
                                        sendingMediaInfo.path = str3;
                                    } else {
                                        sendingMediaInfo.searchImage = searchImage;
                                    }
                                    sendingMediaInfo.thumbPath = searchImage.thumbPath;
                                    sendingMediaInfo.videoEditedInfo = searchImage.editedInfo;
                                    CharSequence charSequence2 = searchImage.caption;
                                    if (charSequence2 != null) {
                                        str = charSequence2.toString();
                                    }
                                    sendingMediaInfo.caption = str;
                                    sendingMediaInfo.entities = searchImage.entities;
                                    sendingMediaInfo.masks = searchImage.stickers;
                                    sendingMediaInfo.ttl = searchImage.ttl;
                                    TLRPC$BotInlineResult tLRPC$BotInlineResult = searchImage.inlineResult;
                                    if (tLRPC$BotInlineResult != null && searchImage.type == 1) {
                                        sendingMediaInfo.inlineResult = tLRPC$BotInlineResult;
                                        sendingMediaInfo.params = searchImage.params;
                                    }
                                    searchImage.date = (int) (System.currentTimeMillis() / 1000);
                                }
                            }
                            ImageUpdater.this.didSelectPhotos(arrayList);
                            if (i != 8) {
                                ImageUpdater.this.chatAttachAlert.dismiss(true);
                                return;
                            }
                            return;
                        }
                        ImageUpdater.this.chatAttachAlert.dismissWithButtonClick(i);
                        processSelectedAttach(i);
                    }
                }

                public void onCameraOpened() {
                    AndroidUtilities.hideKeyboard(ImageUpdater.this.parentFragment.getFragmentView().findFocus());
                }

                public void doOnIdle(Runnable runnable) {
                    runnable.run();
                }

                private void processSelectedAttach(int i) {
                    if (i == 0) {
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
    public void didSelectPhotos(ArrayList<SendMessagesHelper.SendingMediaInfo> arrayList) {
        MessageObject messageObject;
        Bitmap loadBitmap;
        if (!arrayList.isEmpty()) {
            SendMessagesHelper.SendingMediaInfo sendingMediaInfo = arrayList.get(0);
            Bitmap bitmap = null;
            if (sendingMediaInfo.isVideo || sendingMediaInfo.videoEditedInfo != null) {
                TLRPC$TL_message tLRPC$TL_message = new TLRPC$TL_message();
                tLRPC$TL_message.id = 0;
                tLRPC$TL_message.message = "";
                tLRPC$TL_message.media = new TLRPC$TL_messageMediaEmpty();
                tLRPC$TL_message.action = new TLRPC$TL_messageActionEmpty();
                tLRPC$TL_message.dialog_id = 0;
                messageObject = new MessageObject(UserConfig.selectedAccount, tLRPC$TL_message, false, false);
                messageObject.messageOwner.attachPath = new File(FileLoader.getDirectory(4), SharedConfig.getLastLocalId() + "_avatar.mp4").getAbsolutePath();
                messageObject.videoEditedInfo = sendingMediaInfo.videoEditedInfo;
                bitmap = ImageLoader.loadBitmap(sendingMediaInfo.thumbPath, (Uri) null, 800.0f, 800.0f, true);
            } else {
                String str = sendingMediaInfo.path;
                if (str != null) {
                    loadBitmap = ImageLoader.loadBitmap(str, (Uri) null, 800.0f, 800.0f, true);
                } else {
                    MediaController.SearchImage searchImage = sendingMediaInfo.searchImage;
                    if (searchImage != null) {
                        TLRPC$Photo tLRPC$Photo = searchImage.photo;
                        if (tLRPC$Photo != null) {
                            TLRPC$PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(tLRPC$Photo.sizes, AndroidUtilities.getPhotoSize());
                            if (closestPhotoSizeWithSize != null) {
                                File pathToAttach = FileLoader.getPathToAttach(closestPhotoSizeWithSize, true);
                                this.finalPath = pathToAttach.getAbsolutePath();
                                if (!pathToAttach.exists()) {
                                    pathToAttach = FileLoader.getPathToAttach(closestPhotoSizeWithSize, false);
                                    if (!pathToAttach.exists()) {
                                        pathToAttach = null;
                                    }
                                }
                                if (pathToAttach != null) {
                                    loadBitmap = ImageLoader.loadBitmap(pathToAttach.getAbsolutePath(), (Uri) null, 800.0f, 800.0f, true);
                                } else {
                                    NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileLoaded);
                                    NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileLoadFailed);
                                    this.uploadingImage = FileLoader.getAttachFileName(closestPhotoSizeWithSize.location);
                                    this.imageReceiver.setImage(ImageLocation.getForPhoto(closestPhotoSizeWithSize, sendingMediaInfo.searchImage.photo), (String) null, (Drawable) null, "jpg", (Object) null, 1);
                                }
                            }
                            loadBitmap = null;
                        } else if (searchImage.imageUrl != null) {
                            File file = new File(FileLoader.getDirectory(4), Utilities.MD5(sendingMediaInfo.searchImage.imageUrl) + "." + ImageLoader.getHttpUrlExtension(sendingMediaInfo.searchImage.imageUrl, "jpg"));
                            this.finalPath = file.getAbsolutePath();
                            if (!file.exists() || file.length() == 0) {
                                this.uploadingImage = sendingMediaInfo.searchImage.imageUrl;
                                NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.httpFileDidLoad);
                                NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.httpFileDidFailedLoad);
                                this.imageReceiver.setImage(sendingMediaInfo.searchImage.imageUrl, (String) null, (Drawable) null, "jpg", 1);
                            } else {
                                loadBitmap = ImageLoader.loadBitmap(file.getAbsolutePath(), (Uri) null, 800.0f, 800.0f, true);
                            }
                        }
                    }
                    messageObject = null;
                }
                messageObject = null;
                bitmap = loadBitmap;
            }
            processBitmap(bitmap, messageObject);
        }
    }

    public void openCamera() {
        BaseFragment baseFragment = this.parentFragment;
        if (baseFragment != null && baseFragment.getParentActivity() != null) {
            try {
                int i = Build.VERSION.SDK_INT;
                if (i < 23 || this.parentFragment.getParentActivity().checkSelfPermission("android.permission.CAMERA") == 0) {
                    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                    File generatePicturePath = AndroidUtilities.generatePicturePath();
                    if (generatePicturePath != null) {
                        if (i >= 24) {
                            intent.putExtra("output", FileProvider.getUriForFile(this.parentFragment.getParentActivity(), "org.telegram.messenger.beta.provider", generatePicturePath));
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
                int i = Build.VERSION.SDK_INT;
                if (i < 23 || this.parentFragment.getParentActivity().checkSelfPermission("android.permission.CAMERA") == 0) {
                    Intent intent = new Intent("android.media.action.VIDEO_CAPTURE");
                    File generateVideoPath = AndroidUtilities.generateVideoPath();
                    if (generateVideoPath != null) {
                        if (i >= 24) {
                            intent.putExtra("output", FileProvider.getUriForFile(this.parentFragment.getParentActivity(), "org.telegram.messenger.beta.provider", generateVideoPath));
                            intent.addFlags(2);
                            intent.addFlags(1);
                        } else if (i >= 18) {
                            intent.putExtra("output", Uri.fromFile(generateVideoPath));
                        }
                        intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
                        intent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1);
                        intent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true);
                        intent.putExtra("android.intent.extra.durationLimit", 10);
                        this.currentPicturePath = generateVideoPath.getAbsolutePath();
                    }
                    this.parentFragment.startActivityForResult(intent, 15);
                    return;
                }
                this.parentFragment.getParentActivity().requestPermissions(new String[]{"android.permission.CAMERA"}, 19);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    public void onRequestPermissionsResultFragment(int i, String[] strArr, int[] iArr) {
        ChatAttachAlert chatAttachAlert2 = this.chatAttachAlert;
        if (chatAttachAlert2 == null) {
            return;
        }
        if (i == 17) {
            chatAttachAlert2.getPhotoLayout().checkCamera(false);
            this.chatAttachAlert.getPhotoLayout().checkStorage();
        } else if (i == 4) {
            chatAttachAlert2.getPhotoLayout().checkStorage();
        }
    }

    public void openGallery() {
        BaseFragment baseFragment = this.parentFragment;
        if (baseFragment != null) {
            if (Build.VERSION.SDK_INT < 23 || baseFragment.getParentActivity() == null || this.parentFragment.getParentActivity().checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == 0) {
                PhotoAlbumPickerActivity photoAlbumPickerActivity = new PhotoAlbumPickerActivity(this.canSelectVideo ? PhotoAlbumPickerActivity.SELECT_TYPE_AVATAR_VIDEO : PhotoAlbumPickerActivity.SELECT_TYPE_AVATAR, false, false, (ChatActivity) null);
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
            this.parentFragment.getParentActivity().requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 151);
        }
    }

    private void startCrop(String str, Uri uri) {
        AndroidUtilities.runOnUIThread(new ImageUpdater$$ExternalSyntheticLambda1(this, str, uri));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$startCrop$1(String str, Uri uri) {
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
                launchActivity.lambda$runLinkRequest$54(photoCropActivity);
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            processBitmap(ImageLoader.loadBitmap(str, uri, 800.0f, 800.0f, true), (MessageObject) null);
        }
    }

    public void openPhotoForEdit(String str, String str2, int i, boolean z) {
        final ArrayList arrayList = new ArrayList();
        MediaController.PhotoEntry photoEntry = new MediaController.PhotoEntry(0, 0, 0, str, i, false, 0, 0, 0);
        photoEntry.isVideo = z;
        photoEntry.thumbPath = str2;
        arrayList.add(photoEntry);
        PhotoViewer.getInstance().setParentActivity(this.parentFragment.getParentActivity());
        PhotoViewer.getInstance().openPhotoForSelect(arrayList, 0, 1, false, new PhotoViewer.EmptyPhotoViewerProvider() {
            public boolean allowCaption() {
                return false;
            }

            public boolean canScrollAway() {
                return false;
            }

            public void sendButtonPressed(int i, VideoEditedInfo videoEditedInfo, boolean z, int i2, boolean z2) {
                Bitmap bitmap;
                MediaController.PhotoEntry photoEntry = (MediaController.PhotoEntry) arrayList.get(0);
                String str = photoEntry.imagePath;
                MessageObject messageObject = null;
                if (str == null && (str = photoEntry.path) == null) {
                    str = null;
                }
                if (photoEntry.isVideo || photoEntry.editedInfo != null) {
                    TLRPC$TL_message tLRPC$TL_message = new TLRPC$TL_message();
                    tLRPC$TL_message.id = 0;
                    tLRPC$TL_message.message = "";
                    tLRPC$TL_message.media = new TLRPC$TL_messageMediaEmpty();
                    tLRPC$TL_message.action = new TLRPC$TL_messageActionEmpty();
                    tLRPC$TL_message.dialog_id = 0;
                    MessageObject messageObject2 = new MessageObject(UserConfig.selectedAccount, tLRPC$TL_message, false, false);
                    messageObject2.messageOwner.attachPath = new File(FileLoader.getDirectory(4), SharedConfig.getLastLocalId() + "_avatar.mp4").getAbsolutePath();
                    messageObject2.videoEditedInfo = photoEntry.editedInfo;
                    bitmap = ImageLoader.loadBitmap(photoEntry.thumbPath, (Uri) null, 800.0f, 800.0f, true);
                    messageObject = messageObject2;
                } else {
                    bitmap = ImageLoader.loadBitmap(str, (Uri) null, 800.0f, 800.0f, true);
                }
                ImageUpdater.this.processBitmap(bitmap, messageObject);
            }
        }, (ChatActivity) null);
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        int i3;
        if (i2 != -1) {
            return;
        }
        if (i == 0 || i == 2) {
            createChatAttachView();
            ChatAttachAlert chatAttachAlert2 = this.chatAttachAlert;
            if (chatAttachAlert2 != null) {
                chatAttachAlert2.onActivityResultFragment(i, intent, this.currentPicturePath);
            }
            this.currentPicturePath = null;
        } else if (i == 13) {
            this.parentFragment.getParentActivity().overridePendingTransition(NUM, NUM);
            PhotoViewer.getInstance().setParentActivity(this.parentFragment.getParentActivity());
            try {
                int attributeInt = new ExifInterface(this.currentPicturePath).getAttributeInt("Orientation", 1);
                if (attributeInt == 3) {
                    i3 = 180;
                } else if (attributeInt != 6) {
                    if (attributeInt == 8) {
                        i3 = 270;
                    }
                    i3 = 0;
                } else {
                    i3 = 90;
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            openPhotoForEdit(this.currentPicturePath, (String) null, i3, false);
            AndroidUtilities.addMediaToGallery(this.currentPicturePath);
            this.currentPicturePath = null;
        } else if (i == 14) {
            if (intent != null && intent.getData() != null) {
                startCrop((String) null, intent.getData());
            }
        } else if (i == 15) {
            openPhotoForEdit(this.currentPicturePath, (String) null, 0, true);
            AndroidUtilities.addMediaToGallery(this.currentPicturePath);
            this.currentPicturePath = null;
        }
    }

    /* access modifiers changed from: private */
    public void processBitmap(Bitmap bitmap, MessageObject messageObject) {
        VideoEditedInfo videoEditedInfo;
        if (bitmap != null) {
            this.uploadedVideo = null;
            this.uploadedPhoto = null;
            this.convertingVideo = null;
            this.videoPath = null;
            this.bigPhoto = ImageLoader.scaleAndSaveImage(bitmap, 800.0f, 800.0f, 80, false, 320, 320);
            TLRPC$PhotoSize scaleAndSaveImage = ImageLoader.scaleAndSaveImage(bitmap, 150.0f, 150.0f, 80, false, 150, 150);
            this.smallPhoto = scaleAndSaveImage;
            if (scaleAndSaveImage != null) {
                try {
                    ImageLoader.getInstance().putImageToCache(new BitmapDrawable(BitmapFactory.decodeFile(FileLoader.getPathToAttach(scaleAndSaveImage, true).getAbsolutePath())), this.smallPhoto.location.volume_id + "_" + this.smallPhoto.location.local_id + "@50_50", true);
                } catch (Throwable unused) {
                }
            }
            bitmap.recycle();
            if (this.bigPhoto != null) {
                UserConfig.getInstance(this.currentAccount).saveConfig(false);
                this.uploadingImage = FileLoader.getDirectory(4) + "/" + this.bigPhoto.location.volume_id + "_" + this.bigPhoto.location.local_id + ".jpg";
                if (this.uploadAfterSelect) {
                    if (messageObject == null || (videoEditedInfo = messageObject.videoEditedInfo) == null) {
                        ImageUpdaterDelegate imageUpdaterDelegate = this.delegate;
                        if (imageUpdaterDelegate != null) {
                            imageUpdaterDelegate.didStartUpload(false);
                        }
                    } else {
                        this.convertingVideo = messageObject;
                        long j = videoEditedInfo.startTime;
                        if (j < 0) {
                            j = 0;
                        }
                        double d = (double) (videoEditedInfo.avatarStartTime - j);
                        Double.isNaN(d);
                        this.videoTimestamp = d / 1000000.0d;
                        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.filePreparingStarted);
                        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.filePreparingFailed);
                        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileNewChunkAvailable);
                        MediaController.getInstance().scheduleVideoConvert(messageObject, true);
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
                    imageUpdaterDelegate3.didUploadPhoto((TLRPC$InputFile) null, (TLRPC$InputFile) null, 0.0d, (String) null, this.bigPhoto, this.smallPhoto);
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

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        ImageUpdaterDelegate imageUpdaterDelegate;
        BaseFragment baseFragment;
        BaseFragment baseFragment2;
        int i3 = i;
        int i4 = NotificationCenter.fileUploaded;
        if (i3 == i4 || i3 == NotificationCenter.fileUploadFailed) {
            String str = objArr[0];
            if (str.equals(this.uploadingImage)) {
                this.uploadingImage = null;
                if (i3 == i4) {
                    this.uploadedPhoto = objArr[1];
                }
            } else if (str.equals(this.uploadingVideo)) {
                this.uploadingVideo = null;
                if (i3 == i4) {
                    this.uploadedVideo = objArr[1];
                }
            } else {
                return;
            }
            if (this.uploadingImage == null && this.uploadingVideo == null && this.convertingVideo == null) {
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, i4);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileUploadProgressChanged);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileUploadFailed);
                if (i3 == i4 && (imageUpdaterDelegate = this.delegate) != null) {
                    imageUpdaterDelegate.didUploadPhoto(this.uploadedPhoto, this.uploadedVideo, this.videoTimestamp, this.videoPath, this.bigPhoto, this.smallPhoto);
                }
                cleanup();
            }
        } else if (i3 == NotificationCenter.fileUploadProgressChanged) {
            String str2 = objArr[0];
            String str3 = this.convertingVideo != null ? this.uploadingVideo : this.uploadingImage;
            if (this.delegate != null && str2.equals(str3)) {
                this.delegate.onUploadProgressChanged(Math.min(1.0f, ((float) objArr[1].longValue()) / ((float) objArr[2].longValue())));
            }
        } else {
            int i5 = NotificationCenter.fileLoaded;
            if (i3 != i5 && i3 != NotificationCenter.fileLoadFailed && i3 != NotificationCenter.httpFileDidLoad && i3 != NotificationCenter.httpFileDidFailedLoad) {
                int i6 = NotificationCenter.filePreparingFailed;
                if (i3 == i6) {
                    MessageObject messageObject = objArr[0];
                    if (messageObject == this.convertingVideo && (baseFragment2 = this.parentFragment) != null) {
                        baseFragment2.getSendMessagesHelper().stopVideoService(messageObject.messageOwner.attachPath);
                        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.filePreparingStarted);
                        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, i6);
                        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileNewChunkAvailable);
                        cleanup();
                    }
                } else if (i3 == NotificationCenter.fileNewChunkAvailable) {
                    MessageObject messageObject2 = objArr[0];
                    if (messageObject2 == this.convertingVideo && this.parentFragment != null) {
                        String str4 = objArr[1];
                        long longValue = objArr[2].longValue();
                        long longValue2 = objArr[3].longValue();
                        this.parentFragment.getFileLoader().checkUploadNewDataAvailable(str4, false, longValue, longValue2);
                        if (longValue2 != 0) {
                            double longValue3 = (double) objArr[5].longValue();
                            Double.isNaN(longValue3);
                            double d = longValue3 / 1000000.0d;
                            if (this.videoTimestamp > d) {
                                this.videoTimestamp = d;
                            }
                            Bitmap createVideoThumbnailAtTime = SendMessagesHelper.createVideoThumbnailAtTime(str4, (long) (this.videoTimestamp * 1000.0d), (int[]) null, true);
                            if (createVideoThumbnailAtTime != null) {
                                File pathToAttach = FileLoader.getPathToAttach(this.smallPhoto, true);
                                if (pathToAttach != null) {
                                    pathToAttach.delete();
                                }
                                File pathToAttach2 = FileLoader.getPathToAttach(this.bigPhoto, true);
                                if (pathToAttach2 != null) {
                                    pathToAttach2.delete();
                                }
                                Bitmap bitmap = createVideoThumbnailAtTime;
                                this.bigPhoto = ImageLoader.scaleAndSaveImage(bitmap, 800.0f, 800.0f, 80, false, 320, 320);
                                TLRPC$PhotoSize scaleAndSaveImage = ImageLoader.scaleAndSaveImage(bitmap, 150.0f, 150.0f, 80, false, 150, 150);
                                this.smallPhoto = scaleAndSaveImage;
                                if (scaleAndSaveImage != null) {
                                    try {
                                        Bitmap decodeFile = BitmapFactory.decodeFile(FileLoader.getPathToAttach(scaleAndSaveImage, true).getAbsolutePath());
                                        ImageLoader.getInstance().putImageToCache(new BitmapDrawable(decodeFile), this.smallPhoto.location.volume_id + "_" + this.smallPhoto.location.local_id + "@50_50", true);
                                    } catch (Throwable unused) {
                                    }
                                }
                            }
                            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.filePreparingStarted);
                            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.filePreparingFailed);
                            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileNewChunkAvailable);
                            this.parentFragment.getSendMessagesHelper().stopVideoService(messageObject2.messageOwner.attachPath);
                            this.videoPath = str4;
                            this.uploadingVideo = str4;
                            this.convertingVideo = null;
                        }
                    }
                } else if (i3 == NotificationCenter.filePreparingStarted && objArr[0] == this.convertingVideo && (baseFragment = this.parentFragment) != null) {
                    this.uploadingVideo = objArr[1];
                    baseFragment.getFileLoader().uploadFile(this.uploadingVideo, false, false, (int) this.convertingVideo.videoEditedInfo.estimatedSize, 33554432, false);
                }
            } else if (objArr[0].equals(this.uploadingImage)) {
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, i5);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileLoadFailed);
                NotificationCenter instance = NotificationCenter.getInstance(this.currentAccount);
                int i7 = NotificationCenter.httpFileDidLoad;
                instance.removeObserver(this, i7);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.httpFileDidFailedLoad);
                this.uploadingImage = null;
                if (i3 == i5 || i3 == i7) {
                    processBitmap(ImageLoader.loadBitmap(this.finalPath, (Uri) null, 800.0f, 800.0f, true), (MessageObject) null);
                } else {
                    this.imageReceiver.setImageBitmap((Drawable) null);
                }
            }
        }
    }

    public void setForceDarkTheme(boolean z) {
        this.forceDarkTheme = z;
    }

    public void setShowingFromDialog(boolean z) {
        this.showingFromDialog = z;
    }
}
