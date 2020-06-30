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
    private ImageReceiver imageReceiver;
    public BaseFragment parentFragment;
    private boolean searchAvailable;
    private TLRPC$PhotoSize smallPhoto;
    private boolean uploadAfterSelect;
    private TLRPC$InputFile uploadedPhoto;
    private TLRPC$InputFile uploadedVideo;
    public String uploadingImage;
    private String uploadingVideo;
    private boolean useAttachMenu;
    private String videoPath;

    public interface ImageUpdaterDelegate {

        /* renamed from: org.telegram.ui.Components.ImageUpdater$ImageUpdaterDelegate$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static String $default$getInitialSearchString(ImageUpdaterDelegate imageUpdaterDelegate) {
                return null;
            }
        }

        void didUploadPhoto(TLRPC$InputFile tLRPC$InputFile, TLRPC$InputFile tLRPC$InputFile2, String str, TLRPC$PhotoSize tLRPC$PhotoSize, TLRPC$PhotoSize tLRPC$PhotoSize2);

        String getInitialSearchString();
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

    public void openMenu(boolean z, Runnable runnable) {
        BaseFragment baseFragment = this.parentFragment;
        if (baseFragment != null && baseFragment.getParentActivity() != null) {
            if (this.useAttachMenu) {
                openAttachMenu();
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
            builder.setItems((CharSequence[]) arrayList.toArray(new CharSequence[0]), iArr, new DialogInterface.OnClickListener(arrayList3, runnable) {
                public final /* synthetic */ ArrayList f$1;
                public final /* synthetic */ Runnable f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    ImageUpdater.this.lambda$openMenu$0$ImageUpdater(this.f$1, this.f$2, dialogInterface, i);
                }
            });
            BottomSheet create = builder.create();
            this.parentFragment.showDialog(create);
            if (z) {
                create.setItemColor(arrayList.size() - 1, Theme.getColor("dialogTextRed2"), Theme.getColor("dialogRedIcon"));
            }
        }
    }

    public /* synthetic */ void lambda$openMenu$0$ImageUpdater(ArrayList arrayList, Runnable runnable, DialogInterface dialogInterface, int i) {
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
            PhotoPickerActivity photoPickerActivity = new PhotoPickerActivity(0, (MediaController.AlbumEntry) null, hashMap, arrayList, 1, false, (ChatActivity) null);
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
            photoPickerActivity.setInitialSearchString(this.delegate.getInitialSearchString());
            this.parentFragment.presentFragment(photoPickerActivity);
        }
    }

    private void openAttachMenu() {
        BaseFragment baseFragment = this.parentFragment;
        if (baseFragment != null && baseFragment.getParentActivity() != null) {
            createChatAttachView();
            this.chatAttachAlert.setOpenWithFrontFaceCamera(true);
            this.chatAttachAlert.setMaxSelectedPhotos(1, false);
            this.chatAttachAlert.getPhotoLayout().loadGalleryPhotos();
            int i = Build.VERSION.SDK_INT;
            if (i == 21 || i == 22) {
                AndroidUtilities.hideKeyboard(this.parentFragment.getFragmentView().findFocus());
            }
            this.chatAttachAlert.init();
            this.parentFragment.showDialog(this.chatAttachAlert);
        }
    }

    private void createChatAttachView() {
        BaseFragment baseFragment = this.parentFragment;
        if (baseFragment != null && baseFragment.getParentActivity() != null && this.chatAttachAlert == null) {
            ChatAttachAlert chatAttachAlert2 = new ChatAttachAlert(this.parentFragment.getParentActivity(), this.parentFragment);
            this.chatAttachAlert = chatAttachAlert2;
            chatAttachAlert2.setAvatarPicker(this.canSelectVideo ? 2 : 1, this.searchAvailable);
            this.chatAttachAlert.setDelegate(new ChatAttachAlert.ChatAttachViewDelegate() {
                public void didSelectBot(TLRPC$User tLRPC$User) {
                }

                public void needEnterComment() {
                }

                public void didPressedButton(int i, boolean z, boolean z2, int i2) {
                    BaseFragment baseFragment = ImageUpdater.this.parentFragment;
                    if (baseFragment != null && baseFragment.getParentActivity() != null && ImageUpdater.this.chatAttachAlert != null) {
                        if (i == 8 || i == 7) {
                            if (i != 8) {
                                ImageUpdater.this.chatAttachAlert.dismiss();
                            }
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
                            return;
                        }
                        if (ImageUpdater.this.chatAttachAlert != null) {
                            ImageUpdater.this.chatAttachAlert.dismissWithButtonClick(i);
                        }
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
                messageObject = new MessageObject(UserConfig.selectedAccount, tLRPC$TL_message, false);
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
                                    NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileDidLoad);
                                    NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileDidFailToLoad);
                                    this.uploadingImage = FileLoader.getAttachFileName(closestPhotoSizeWithSize.location);
                                    this.imageReceiver.setImage(ImageLocation.getForPhoto(closestPhotoSizeWithSize, sendingMediaInfo.searchImage.photo), (String) null, (Drawable) null, "jpg", (Object) null, 1);
                                }
                            }
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
                        loadBitmap = null;
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
                if (Build.VERSION.SDK_INT < 23 || this.parentFragment.getParentActivity().checkSelfPermission("android.permission.CAMERA") == 0) {
                    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                    File generatePicturePath = AndroidUtilities.generatePicturePath();
                    if (generatePicturePath != null) {
                        if (Build.VERSION.SDK_INT >= 24) {
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
                if (Build.VERSION.SDK_INT < 23 || this.parentFragment.getParentActivity().checkSelfPermission("android.permission.CAMERA") == 0) {
                    Intent intent = new Intent("android.media.action.VIDEO_CAPTURE");
                    File generateVideoPath = AndroidUtilities.generateVideoPath();
                    if (generateVideoPath != null) {
                        if (Build.VERSION.SDK_INT >= 24) {
                            intent.putExtra("output", FileProvider.getUriForFile(this.parentFragment.getParentActivity(), "org.telegram.messenger.beta.provider", generateVideoPath));
                            intent.addFlags(2);
                            intent.addFlags(1);
                        } else if (Build.VERSION.SDK_INT >= 18) {
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
        ChatAttachAlert chatAttachAlert2;
        if (i == 17 && (chatAttachAlert2 = this.chatAttachAlert) != null) {
            chatAttachAlert2.getPhotoLayout().checkCamera(false);
        }
    }

    public void openGallery() {
        BaseFragment baseFragment = this.parentFragment;
        if (baseFragment != null) {
            if (Build.VERSION.SDK_INT < 23 || baseFragment == null || baseFragment.getParentActivity() == null || this.parentFragment.getParentActivity().checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == 0) {
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
                launchActivity.lambda$runLinkRequest$32$LaunchActivity(photoCropActivity);
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
        PhotoViewer.getInstance().openPhotoForSelect(arrayList, 0, 1, false, new PhotoViewer.EmptyPhotoViewerProvider() {
            public boolean allowCaption() {
                return false;
            }

            public boolean canScrollAway() {
                return false;
            }

            public void sendButtonPressed(int i, VideoEditedInfo videoEditedInfo, boolean z, int i2) {
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
                    MessageObject messageObject2 = new MessageObject(UserConfig.selectedAccount, tLRPC$TL_message, false);
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
        if (bitmap != null) {
            this.bigPhoto = ImageLoader.scaleAndSaveImage(bitmap, 800.0f, 800.0f, 80, false, 320, 320);
            TLRPC$PhotoSize scaleAndSaveImage = ImageLoader.scaleAndSaveImage(bitmap, 150.0f, 150.0f, 80, false, 150, 150);
            this.smallPhoto = scaleAndSaveImage;
            if (scaleAndSaveImage != null) {
                try {
                    Bitmap decodeFile = BitmapFactory.decodeFile(FileLoader.getPathToAttach(scaleAndSaveImage, true).getAbsolutePath());
                    ImageLoader.getInstance().putImageToCache(new BitmapDrawable(decodeFile), this.smallPhoto.location.volume_id + "_" + this.smallPhoto.location.local_id + "@50_50");
                } catch (Throwable unused) {
                }
            }
            bitmap.recycle();
            if (this.bigPhoto != null) {
                UserConfig.getInstance(this.currentAccount).saveConfig(false);
                this.uploadingImage = FileLoader.getDirectory(4) + "/" + this.bigPhoto.location.volume_id + "_" + this.bigPhoto.location.local_id + ".jpg";
                if (this.uploadAfterSelect) {
                    if (messageObject != null) {
                        this.convertingVideo = messageObject;
                        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.filePreparingStarted);
                        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.filePreparingFailed);
                        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileNewChunkAvailable);
                        MediaController.getInstance().scheduleVideoConvert(messageObject, true);
                    }
                    NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.FileDidUpload);
                    NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.FileDidFailUpload);
                    FileLoader.getInstance(this.currentAccount).uploadFile(this.uploadingImage, false, true, 16777216);
                }
                ImageUpdaterDelegate imageUpdaterDelegate = this.delegate;
                if (imageUpdaterDelegate != null) {
                    imageUpdaterDelegate.didUploadPhoto((TLRPC$InputFile) null, (TLRPC$InputFile) null, (String) null, this.bigPhoto, this.smallPhoto);
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
        if (i == NotificationCenter.FileDidUpload || i == NotificationCenter.FileDidFailUpload) {
            String str = objArr[0];
            if (str.equals(this.uploadingImage)) {
                this.uploadingImage = null;
                this.uploadedPhoto = objArr[1];
            } else if (str.equals(this.uploadingVideo)) {
                this.uploadingVideo = null;
                this.uploadedVideo = objArr[1];
            } else {
                return;
            }
            if (this.uploadingImage == null && this.uploadingVideo == null && this.convertingVideo == null) {
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.FileDidUpload);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.FileDidFailUpload);
                if (i == NotificationCenter.FileDidUpload && (imageUpdaterDelegate = this.delegate) != null) {
                    imageUpdaterDelegate.didUploadPhoto(this.uploadedPhoto, this.uploadedVideo, this.videoPath, this.bigPhoto, this.smallPhoto);
                }
                cleanup();
            }
        } else if (i == NotificationCenter.fileDidLoad || i == NotificationCenter.fileDidFailToLoad || i == NotificationCenter.httpFileDidLoad || i == NotificationCenter.httpFileDidFailedLoad) {
            if (objArr[0].equals(this.uploadingImage)) {
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileDidLoad);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileDidFailToLoad);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.httpFileDidLoad);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.httpFileDidFailedLoad);
                this.uploadingImage = null;
                if (i == NotificationCenter.fileDidLoad || i == NotificationCenter.httpFileDidLoad) {
                    processBitmap(ImageLoader.loadBitmap(this.finalPath, (Uri) null, 800.0f, 800.0f, true), (MessageObject) null);
                } else {
                    this.imageReceiver.setImageBitmap((Drawable) null);
                }
            }
        } else if (i == NotificationCenter.filePreparingFailed) {
            MessageObject messageObject = objArr[0];
            if (messageObject == this.convertingVideo && (baseFragment2 = this.parentFragment) != null) {
                baseFragment2.getSendMessagesHelper().stopVideoService(messageObject.messageOwner.attachPath);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.filePreparingStarted);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.filePreparingFailed);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileNewChunkAvailable);
                cleanup();
            }
        } else if (i == NotificationCenter.fileNewChunkAvailable) {
            MessageObject messageObject2 = objArr[0];
            if (messageObject2 == this.convertingVideo && this.parentFragment != null) {
                String str2 = objArr[1];
                long longValue = objArr[2].longValue();
                long longValue2 = objArr[3].longValue();
                this.parentFragment.getFileLoader().checkUploadNewDataAvailable(str2, false, longValue, longValue2);
                if (longValue2 != 0) {
                    NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.filePreparingStarted);
                    NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.filePreparingFailed);
                    NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileNewChunkAvailable);
                    this.parentFragment.getSendMessagesHelper().stopVideoService(messageObject2.messageOwner.attachPath);
                    this.videoPath = str2;
                    this.uploadingVideo = str2;
                    this.convertingVideo = null;
                }
            }
        } else if (i == NotificationCenter.filePreparingStarted && objArr[0] == this.convertingVideo && (baseFragment = this.parentFragment) != null) {
            baseFragment.getFileLoader().uploadFile(objArr[1], false, false, (int) this.convertingVideo.videoEditedInfo.estimatedSize, 33554432);
        }
    }
}
