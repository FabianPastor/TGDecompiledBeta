package org.telegram.ui.Components;

import android.app.Activity;
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
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
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
import org.telegram.ui.Components.ChatAttachAlert;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.PhotoAlbumPickerActivity;
import org.telegram.ui.PhotoCropActivity;
import org.telegram.ui.PhotoPickerActivity;
import org.telegram.ui.PhotoViewer;
/* loaded from: classes3.dex */
public class ImageUpdater implements NotificationCenter.NotificationCenterDelegate, PhotoCropActivity.PhotoEditActivityDelegate {
    private TLRPC$PhotoSize bigPhoto;
    private boolean canSelectVideo;
    private ChatAttachAlert chatAttachAlert;
    private boolean clearAfterUpdate;
    private MessageObject convertingVideo;
    public String currentPicturePath;
    private ImageUpdaterDelegate delegate;
    private String finalPath;
    private boolean forceDarkTheme;
    private boolean openWithFrontfaceCamera;
    public BaseFragment parentFragment;
    private boolean showingFromDialog;
    private TLRPC$PhotoSize smallPhoto;
    private TLRPC$InputFile uploadedPhoto;
    private TLRPC$InputFile uploadedVideo;
    private String uploadingImage;
    private String uploadingVideo;
    private String videoPath;
    private double videoTimestamp;
    private int currentAccount = UserConfig.selectedAccount;
    private boolean useAttachMenu = true;
    private boolean searchAvailable = true;
    private boolean uploadAfterSelect = true;
    private ImageReceiver imageReceiver = new ImageReceiver(null);

    /* loaded from: classes3.dex */
    public interface ImageUpdaterDelegate {

        /* renamed from: org.telegram.ui.Components.ImageUpdater$ImageUpdaterDelegate$-CC */
        /* loaded from: classes3.dex */
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
        if (this.uploadingImage != null || this.uploadingVideo != null || this.convertingVideo != null) {
            this.clearAfterUpdate = true;
        } else {
            this.parentFragment = null;
            this.delegate = null;
        }
        ChatAttachAlert chatAttachAlert = this.chatAttachAlert;
        if (chatAttachAlert != null) {
            chatAttachAlert.dismissInternal();
            this.chatAttachAlert.onDestroy();
        }
    }

    public void setOpenWithFrontfaceCamera(boolean z) {
        this.openWithFrontfaceCamera = z;
    }

    public ImageUpdater(boolean z) {
        boolean z2 = true;
        this.canSelectVideo = (!z || Build.VERSION.SDK_INT <= 18) ? false : z2;
    }

    public void setDelegate(ImageUpdaterDelegate imageUpdaterDelegate) {
        this.delegate = imageUpdaterDelegate;
    }

    public void openMenu(boolean z, final Runnable runnable, DialogInterface.OnDismissListener onDismissListener) {
        BaseFragment baseFragment = this.parentFragment;
        if (baseFragment == null || baseFragment.getParentActivity() == null) {
            return;
        }
        if (this.useAttachMenu) {
            openAttachMenu(onDismissListener);
            return;
        }
        BottomSheet.Builder builder = new BottomSheet.Builder(this.parentFragment.getParentActivity());
        builder.setTitle(LocaleController.getString("ChoosePhoto", R.string.ChoosePhoto), true);
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        final ArrayList arrayList3 = new ArrayList();
        arrayList.add(LocaleController.getString("ChooseTakePhoto", R.string.ChooseTakePhoto));
        arrayList2.add(Integer.valueOf(R.drawable.msg_camera));
        arrayList3.add(0);
        if (this.canSelectVideo) {
            arrayList.add(LocaleController.getString("ChooseRecordVideo", R.string.ChooseRecordVideo));
            arrayList2.add(Integer.valueOf(R.drawable.msg_video));
            arrayList3.add(4);
        }
        arrayList.add(LocaleController.getString("ChooseFromGallery", R.string.ChooseFromGallery));
        arrayList2.add(Integer.valueOf(R.drawable.msg_photos));
        arrayList3.add(1);
        if (this.searchAvailable) {
            arrayList.add(LocaleController.getString("ChooseFromSearch", R.string.ChooseFromSearch));
            arrayList2.add(Integer.valueOf(R.drawable.msg_search));
            arrayList3.add(2);
        }
        if (z) {
            arrayList.add(LocaleController.getString("DeletePhoto", R.string.DeletePhoto));
            arrayList2.add(Integer.valueOf(R.drawable.msg_delete));
            arrayList3.add(3);
        }
        int[] iArr = new int[arrayList2.size()];
        int size = arrayList2.size();
        for (int i = 0; i < size; i++) {
            iArr[i] = ((Integer) arrayList2.get(i)).intValue();
        }
        builder.setItems((CharSequence[]) arrayList.toArray(new CharSequence[0]), iArr, new DialogInterface.OnClickListener() { // from class: org.telegram.ui.Components.ImageUpdater$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i2) {
                ImageUpdater.this.lambda$openMenu$0(arrayList3, runnable, dialogInterface, i2);
            }
        });
        BottomSheet create = builder.create();
        create.setOnHideListener(onDismissListener);
        this.parentFragment.showDialog(create);
        if (!z) {
            return;
        }
        create.setItemColor(arrayList.size() - 1, Theme.getColor("dialogTextRed2"), Theme.getColor("dialogRedIcon"));
    }

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
        } else if (intValue != 4) {
        } else {
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
        ChatAttachAlert chatAttachAlert = this.chatAttachAlert;
        if (chatAttachAlert != null) {
            chatAttachAlert.onResume();
        }
    }

    public void onPause() {
        ChatAttachAlert chatAttachAlert = this.chatAttachAlert;
        if (chatAttachAlert != null) {
            chatAttachAlert.onPause();
        }
    }

    public boolean dismissDialogOnPause(Dialog dialog) {
        return dialog != this.chatAttachAlert;
    }

    public boolean dismissCurrentDialog(Dialog dialog) {
        ChatAttachAlert chatAttachAlert = this.chatAttachAlert;
        if (chatAttachAlert == null || dialog != chatAttachAlert) {
            return false;
        }
        chatAttachAlert.getPhotoLayout().closeCamera(false);
        this.chatAttachAlert.dismissInternal();
        this.chatAttachAlert.getPhotoLayout().hideCamera(true);
        return true;
    }

    public void openSearch() {
        if (this.parentFragment == null) {
            return;
        }
        final HashMap hashMap = new HashMap();
        final ArrayList arrayList = new ArrayList();
        PhotoPickerActivity photoPickerActivity = new PhotoPickerActivity(0, null, hashMap, arrayList, 1, false, null, this.forceDarkTheme);
        photoPickerActivity.setDelegate(new PhotoPickerActivity.PhotoPickerActivityDelegate() { // from class: org.telegram.ui.Components.ImageUpdater.1
            private boolean sendPressed;

            @Override // org.telegram.ui.PhotoPickerActivity.PhotoPickerActivityDelegate
            public void onCaptionChanged(CharSequence charSequence) {
            }

            @Override // org.telegram.ui.PhotoPickerActivity.PhotoPickerActivityDelegate
            public /* synthetic */ void onOpenInPressed() {
                PhotoPickerActivity.PhotoPickerActivityDelegate.CC.$default$onOpenInPressed(this);
            }

            @Override // org.telegram.ui.PhotoPickerActivity.PhotoPickerActivityDelegate
            public void selectedPhotosChanged() {
            }

            {
                ImageUpdater.this = this;
            }

            @Override // org.telegram.ui.PhotoPickerActivity.PhotoPickerActivityDelegate
            public void actionButtonPressed(boolean z, boolean z2, int i) {
                if (hashMap.isEmpty() || ImageUpdater.this.delegate == null || this.sendPressed || z) {
                    return;
                }
                this.sendPressed = true;
                ArrayList arrayList2 = new ArrayList();
                for (int i2 = 0; i2 < arrayList.size(); i2++) {
                    Object obj = hashMap.get(arrayList.get(i2));
                    SendMessagesHelper.SendingMediaInfo sendingMediaInfo = new SendMessagesHelper.SendingMediaInfo();
                    arrayList2.add(sendingMediaInfo);
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
                ImageUpdater.this.didSelectPhotos(arrayList2);
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

    private void openAttachMenu(DialogInterface.OnDismissListener onDismissListener) {
        BaseFragment baseFragment = this.parentFragment;
        if (baseFragment == null || baseFragment.getParentActivity() == null) {
            return;
        }
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

    private void createChatAttachView() {
        BaseFragment baseFragment = this.parentFragment;
        if (baseFragment == null || baseFragment.getParentActivity() == null || this.chatAttachAlert != null) {
            return;
        }
        ChatAttachAlert chatAttachAlert = new ChatAttachAlert(this.parentFragment.getParentActivity(), this.parentFragment, this.forceDarkTheme, this.showingFromDialog);
        this.chatAttachAlert = chatAttachAlert;
        chatAttachAlert.setAvatarPicker(this.canSelectVideo ? 2 : 1, this.searchAvailable);
        this.chatAttachAlert.setDelegate(new ChatAttachAlert.ChatAttachViewDelegate() { // from class: org.telegram.ui.Components.ImageUpdater.2
            @Override // org.telegram.ui.Components.ChatAttachAlert.ChatAttachViewDelegate
            public void didSelectBot(TLRPC$User tLRPC$User) {
            }

            @Override // org.telegram.ui.Components.ChatAttachAlert.ChatAttachViewDelegate
            public boolean needEnterComment() {
                return false;
            }

            {
                ImageUpdater.this = this;
            }

            @Override // org.telegram.ui.Components.ChatAttachAlert.ChatAttachViewDelegate
            public void didPressedButton(int i, boolean z, boolean z2, int i2, boolean z3) {
                BaseFragment baseFragment2 = ImageUpdater.this.parentFragment;
                if (baseFragment2 == null || baseFragment2.getParentActivity() == null || ImageUpdater.this.chatAttachAlert == null) {
                    return;
                }
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
                    if (i == 8) {
                        return;
                    }
                    ImageUpdater.this.chatAttachAlert.dismiss(true);
                    return;
                }
                ImageUpdater.this.chatAttachAlert.dismissWithButtonClick(i);
                processSelectedAttach(i);
            }

            @Override // org.telegram.ui.Components.ChatAttachAlert.ChatAttachViewDelegate
            public void onCameraOpened() {
                AndroidUtilities.hideKeyboard(ImageUpdater.this.parentFragment.getFragmentView().findFocus());
            }

            @Override // org.telegram.ui.Components.ChatAttachAlert.ChatAttachViewDelegate
            public void doOnIdle(Runnable runnable) {
                runnable.run();
            }

            private void processSelectedAttach(int i) {
                if (i == 0) {
                    ImageUpdater.this.openCamera();
                }
            }

            @Override // org.telegram.ui.Components.ChatAttachAlert.ChatAttachViewDelegate
            public void openAvatarsSearch() {
                ImageUpdater.this.openSearch();
            }
        });
    }

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
                tLRPC$TL_message.dialog_id = 0L;
                messageObject = new MessageObject(UserConfig.selectedAccount, tLRPC$TL_message, false, false);
                messageObject.messageOwner.attachPath = new File(FileLoader.getDirectory(4), SharedConfig.getLastLocalId() + "_avatar.mp4").getAbsolutePath();
                messageObject.videoEditedInfo = sendingMediaInfo.videoEditedInfo;
                bitmap = ImageLoader.loadBitmap(sendingMediaInfo.thumbPath, null, 800.0f, 800.0f, true);
            } else {
                String str = sendingMediaInfo.path;
                if (str != null) {
                    loadBitmap = ImageLoader.loadBitmap(str, null, 800.0f, 800.0f, true);
                } else {
                    MediaController.SearchImage searchImage = sendingMediaInfo.searchImage;
                    if (searchImage != null) {
                        TLRPC$Photo tLRPC$Photo = searchImage.photo;
                        if (tLRPC$Photo != null) {
                            TLRPC$PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(tLRPC$Photo.sizes, AndroidUtilities.getPhotoSize());
                            if (closestPhotoSizeWithSize != null) {
                                File pathToAttach = FileLoader.getInstance(this.currentAccount).getPathToAttach(closestPhotoSizeWithSize, true);
                                this.finalPath = pathToAttach.getAbsolutePath();
                                if (!pathToAttach.exists()) {
                                    pathToAttach = FileLoader.getInstance(this.currentAccount).getPathToAttach(closestPhotoSizeWithSize, false);
                                    if (!pathToAttach.exists()) {
                                        pathToAttach = null;
                                    }
                                }
                                if (pathToAttach != null) {
                                    loadBitmap = ImageLoader.loadBitmap(pathToAttach.getAbsolutePath(), null, 800.0f, 800.0f, true);
                                } else {
                                    NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileLoaded);
                                    NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileLoadFailed);
                                    this.uploadingImage = FileLoader.getAttachFileName(closestPhotoSizeWithSize.location);
                                    this.imageReceiver.setImage(ImageLocation.getForPhoto(closestPhotoSizeWithSize, sendingMediaInfo.searchImage.photo), null, null, "jpg", null, 1);
                                }
                            }
                            loadBitmap = null;
                        } else if (searchImage.imageUrl != null) {
                            File file = new File(FileLoader.getDirectory(4), Utilities.MD5(sendingMediaInfo.searchImage.imageUrl) + "." + ImageLoader.getHttpUrlExtension(sendingMediaInfo.searchImage.imageUrl, "jpg"));
                            this.finalPath = file.getAbsolutePath();
                            if (file.exists() && file.length() != 0) {
                                loadBitmap = ImageLoader.loadBitmap(file.getAbsolutePath(), null, 800.0f, 800.0f, true);
                            } else {
                                this.uploadingImage = sendingMediaInfo.searchImage.imageUrl;
                                NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.httpFileDidLoad);
                                NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.httpFileDidFailedLoad);
                                this.imageReceiver.setImage(sendingMediaInfo.searchImage.imageUrl, null, null, "jpg", 1L);
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
        if (baseFragment == null || baseFragment.getParentActivity() == null) {
            return;
        }
        try {
            int i = Build.VERSION.SDK_INT;
            if (i >= 23 && this.parentFragment.getParentActivity().checkSelfPermission("android.permission.CAMERA") != 0) {
                this.parentFragment.getParentActivity().requestPermissions(new String[]{"android.permission.CAMERA"}, 20);
                return;
            }
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            File generatePicturePath = AndroidUtilities.generatePicturePath();
            if (generatePicturePath != null) {
                if (i >= 24) {
                    Activity parentActivity = this.parentFragment.getParentActivity();
                    intent.putExtra("output", FileProvider.getUriForFile(parentActivity, ApplicationLoader.getApplicationId() + ".provider", generatePicturePath));
                    intent.addFlags(2);
                    intent.addFlags(1);
                } else {
                    intent.putExtra("output", Uri.fromFile(generatePicturePath));
                }
                this.currentPicturePath = generatePicturePath.getAbsolutePath();
            }
            this.parentFragment.startActivityForResult(intent, 13);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void openVideoCamera() {
        BaseFragment baseFragment = this.parentFragment;
        if (baseFragment == null || baseFragment.getParentActivity() == null) {
            return;
        }
        try {
            int i = Build.VERSION.SDK_INT;
            if (i >= 23 && this.parentFragment.getParentActivity().checkSelfPermission("android.permission.CAMERA") != 0) {
                this.parentFragment.getParentActivity().requestPermissions(new String[]{"android.permission.CAMERA"}, 19);
                return;
            }
            Intent intent = new Intent("android.media.action.VIDEO_CAPTURE");
            File generateVideoPath = AndroidUtilities.generateVideoPath();
            if (generateVideoPath != null) {
                if (i >= 24) {
                    Activity parentActivity = this.parentFragment.getParentActivity();
                    intent.putExtra("output", FileProvider.getUriForFile(parentActivity, ApplicationLoader.getApplicationId() + ".provider", generateVideoPath));
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
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void onRequestPermissionsResultFragment(int i, String[] strArr, int[] iArr) {
        ChatAttachAlert chatAttachAlert = this.chatAttachAlert;
        if (chatAttachAlert != null) {
            if (i == 17) {
                chatAttachAlert.getPhotoLayout().checkCamera(false);
                this.chatAttachAlert.getPhotoLayout().checkStorage();
            } else if (i != 4) {
            } else {
                chatAttachAlert.getPhotoLayout().checkStorage();
            }
        }
    }

    public void openGallery() {
        BaseFragment baseFragment = this.parentFragment;
        if (baseFragment == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= 23 && baseFragment.getParentActivity() != null && this.parentFragment.getParentActivity().checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != 0) {
            this.parentFragment.getParentActivity().requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 151);
            return;
        }
        PhotoAlbumPickerActivity photoAlbumPickerActivity = new PhotoAlbumPickerActivity(this.canSelectVideo ? PhotoAlbumPickerActivity.SELECT_TYPE_AVATAR_VIDEO : PhotoAlbumPickerActivity.SELECT_TYPE_AVATAR, false, false, null);
        photoAlbumPickerActivity.setAllowSearchImages(this.searchAvailable);
        photoAlbumPickerActivity.setDelegate(new PhotoAlbumPickerActivity.PhotoAlbumPickerActivityDelegate() { // from class: org.telegram.ui.Components.ImageUpdater.3
            {
                ImageUpdater.this = this;
            }

            @Override // org.telegram.ui.PhotoAlbumPickerActivity.PhotoAlbumPickerActivityDelegate
            public void didSelectPhotos(ArrayList<SendMessagesHelper.SendingMediaInfo> arrayList, boolean z, int i) {
                ImageUpdater.this.didSelectPhotos(arrayList);
            }

            @Override // org.telegram.ui.PhotoAlbumPickerActivity.PhotoAlbumPickerActivityDelegate
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

    private void startCrop(final String str, final Uri uri) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ImageUpdater$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                ImageUpdater.this.lambda$startCrop$1(str, uri);
            }
        });
    }

    public /* synthetic */ void lambda$startCrop$1(String str, Uri uri) {
        try {
            LaunchActivity launchActivity = (LaunchActivity) this.parentFragment.getParentActivity();
            if (launchActivity == null) {
                return;
            }
            Bundle bundle = new Bundle();
            if (str != null) {
                bundle.putString("photoPath", str);
            } else if (uri != null) {
                bundle.putParcelable("photoUri", uri);
            }
            PhotoCropActivity photoCropActivity = new PhotoCropActivity(bundle);
            photoCropActivity.setDelegate(this);
            launchActivity.lambda$runLinkRequest$66(photoCropActivity);
        } catch (Exception e) {
            FileLog.e(e);
            processBitmap(ImageLoader.loadBitmap(str, uri, 800.0f, 800.0f, true), null);
        }
    }

    public void openPhotoForEdit(String str, String str2, int i, boolean z) {
        final ArrayList<Object> arrayList = new ArrayList<>();
        MediaController.PhotoEntry photoEntry = new MediaController.PhotoEntry(0, 0, 0L, str, i, false, 0, 0, 0L);
        photoEntry.isVideo = z;
        photoEntry.thumbPath = str2;
        arrayList.add(photoEntry);
        PhotoViewer.getInstance().setParentActivity(this.parentFragment);
        PhotoViewer.getInstance().openPhotoForSelect(arrayList, 0, 1, false, new PhotoViewer.EmptyPhotoViewerProvider() { // from class: org.telegram.ui.Components.ImageUpdater.4
            @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
            public boolean allowCaption() {
                return false;
            }

            @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
            public boolean canScrollAway() {
                return false;
            }

            {
                ImageUpdater.this = this;
            }

            @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
            public void sendButtonPressed(int i2, VideoEditedInfo videoEditedInfo, boolean z2, int i3, boolean z3) {
                Bitmap loadBitmap;
                MediaController.PhotoEntry photoEntry2 = (MediaController.PhotoEntry) arrayList.get(0);
                String str3 = photoEntry2.imagePath;
                MessageObject messageObject = null;
                if (str3 == null && (str3 = photoEntry2.path) == null) {
                    str3 = null;
                }
                if (photoEntry2.isVideo || photoEntry2.editedInfo != null) {
                    TLRPC$TL_message tLRPC$TL_message = new TLRPC$TL_message();
                    tLRPC$TL_message.id = 0;
                    tLRPC$TL_message.message = "";
                    tLRPC$TL_message.media = new TLRPC$TL_messageMediaEmpty();
                    tLRPC$TL_message.action = new TLRPC$TL_messageActionEmpty();
                    tLRPC$TL_message.dialog_id = 0L;
                    MessageObject messageObject2 = new MessageObject(UserConfig.selectedAccount, tLRPC$TL_message, false, false);
                    messageObject2.messageOwner.attachPath = new File(FileLoader.getDirectory(4), SharedConfig.getLastLocalId() + "_avatar.mp4").getAbsolutePath();
                    messageObject2.videoEditedInfo = photoEntry2.editedInfo;
                    loadBitmap = ImageLoader.loadBitmap(photoEntry2.thumbPath, null, 800.0f, 800.0f, true);
                    messageObject = messageObject2;
                } else {
                    loadBitmap = ImageLoader.loadBitmap(str3, null, 800.0f, 800.0f, true);
                }
                ImageUpdater.this.processBitmap(loadBitmap, messageObject);
            }
        }, null);
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        int i3;
        int attributeInt;
        if (i2 == -1) {
            if (i == 0 || i == 2) {
                createChatAttachView();
                ChatAttachAlert chatAttachAlert = this.chatAttachAlert;
                if (chatAttachAlert != null) {
                    chatAttachAlert.onActivityResultFragment(i, intent, this.currentPicturePath);
                }
                this.currentPicturePath = null;
            } else if (i != 13) {
                if (i == 14) {
                    if (intent == null || intent.getData() == null) {
                        return;
                    }
                    startCrop(null, intent.getData());
                } else if (i != 15) {
                } else {
                    openPhotoForEdit(this.currentPicturePath, null, 0, true);
                    AndroidUtilities.addMediaToGallery(this.currentPicturePath);
                    this.currentPicturePath = null;
                }
            } else {
                this.parentFragment.getParentActivity().overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
                PhotoViewer.getInstance().setParentActivity(this.parentFragment);
                try {
                    attributeInt = new ExifInterface(this.currentPicturePath).getAttributeInt("Orientation", 1);
                } catch (Exception e) {
                    FileLog.e(e);
                }
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
                openPhotoForEdit(this.currentPicturePath, null, i3, false);
                AndroidUtilities.addMediaToGallery(this.currentPicturePath);
                this.currentPicturePath = null;
            }
        }
    }

    public void processBitmap(Bitmap bitmap, MessageObject messageObject) {
        VideoEditedInfo videoEditedInfo;
        if (bitmap == null) {
            return;
        }
        this.uploadedVideo = null;
        this.uploadedPhoto = null;
        this.convertingVideo = null;
        this.videoPath = null;
        this.bigPhoto = ImageLoader.scaleAndSaveImage(bitmap, 800.0f, 800.0f, 80, false, 320, 320);
        TLRPC$PhotoSize scaleAndSaveImage = ImageLoader.scaleAndSaveImage(bitmap, 150.0f, 150.0f, 80, false, 150, 150);
        this.smallPhoto = scaleAndSaveImage;
        if (scaleAndSaveImage != null) {
            try {
                ImageLoader.getInstance().putImageToCache(new BitmapDrawable(BitmapFactory.decodeFile(FileLoader.getInstance(this.currentAccount).getPathToAttach(this.smallPhoto, true).getAbsolutePath())), this.smallPhoto.location.volume_id + "_" + this.smallPhoto.location.local_id + "@50_50", true);
            } catch (Throwable unused) {
            }
        }
        bitmap.recycle();
        if (this.bigPhoto == null) {
            return;
        }
        UserConfig.getInstance(this.currentAccount).saveConfig(false);
        this.uploadingImage = FileLoader.getDirectory(4) + "/" + this.bigPhoto.location.volume_id + "_" + this.bigPhoto.location.local_id + ".jpg";
        if (this.uploadAfterSelect) {
            if (messageObject != null && (videoEditedInfo = messageObject.videoEditedInfo) != null) {
                this.convertingVideo = messageObject;
                long j = videoEditedInfo.startTime;
                if (j < 0) {
                    j = 0;
                }
                double d = videoEditedInfo.avatarStartTime - j;
                Double.isNaN(d);
                this.videoTimestamp = d / 1000000.0d;
                videoEditedInfo.shouldLimitFps = false;
                NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.filePreparingStarted);
                NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.filePreparingFailed);
                NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileNewChunkAvailable);
                MediaController.getInstance().scheduleVideoConvert(messageObject, true);
                this.uploadingImage = null;
                ImageUpdaterDelegate imageUpdaterDelegate = this.delegate;
                if (imageUpdaterDelegate != null) {
                    imageUpdaterDelegate.didStartUpload(true);
                }
            } else {
                ImageUpdaterDelegate imageUpdaterDelegate2 = this.delegate;
                if (imageUpdaterDelegate2 != null) {
                    imageUpdaterDelegate2.didStartUpload(false);
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
        if (imageUpdaterDelegate3 == null) {
            return;
        }
        imageUpdaterDelegate3.didUploadPhoto(null, null, 0.0d, null, this.bigPhoto, this.smallPhoto);
    }

    @Override // org.telegram.ui.PhotoCropActivity.PhotoEditActivityDelegate
    public void didFinishEdit(Bitmap bitmap) {
        processBitmap(bitmap, null);
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

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        ImageUpdaterDelegate imageUpdaterDelegate;
        BaseFragment baseFragment;
        BaseFragment baseFragment2;
        int i3 = NotificationCenter.fileUploaded;
        if (i == i3 || i == NotificationCenter.fileUploadFailed) {
            String str = (String) objArr[0];
            if (str.equals(this.uploadingImage)) {
                this.uploadingImage = null;
                if (i == i3) {
                    this.uploadedPhoto = (TLRPC$InputFile) objArr[1];
                }
            } else if (!str.equals(this.uploadingVideo)) {
                return;
            } else {
                this.uploadingVideo = null;
                if (i == i3) {
                    this.uploadedVideo = (TLRPC$InputFile) objArr[1];
                }
            }
            if (this.uploadingImage != null || this.uploadingVideo != null || this.convertingVideo != null) {
                return;
            }
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, i3);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileUploadProgressChanged);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileUploadFailed);
            if (i == i3 && (imageUpdaterDelegate = this.delegate) != null) {
                imageUpdaterDelegate.didUploadPhoto(this.uploadedPhoto, this.uploadedVideo, this.videoTimestamp, this.videoPath, this.bigPhoto, this.smallPhoto);
            }
            cleanup();
        } else if (i == NotificationCenter.fileUploadProgressChanged) {
            String str2 = (String) objArr[0];
            String str3 = this.convertingVideo != null ? this.uploadingVideo : this.uploadingImage;
            if (this.delegate == null || !str2.equals(str3)) {
                return;
            }
            this.delegate.onUploadProgressChanged(Math.min(1.0f, ((float) ((Long) objArr[1]).longValue()) / ((float) ((Long) objArr[2]).longValue())));
        } else {
            int i4 = NotificationCenter.fileLoaded;
            if (i == i4 || i == NotificationCenter.fileLoadFailed || i == NotificationCenter.httpFileDidLoad || i == NotificationCenter.httpFileDidFailedLoad) {
                if (!((String) objArr[0]).equals(this.uploadingImage)) {
                    return;
                }
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, i4);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileLoadFailed);
                NotificationCenter notificationCenter = NotificationCenter.getInstance(this.currentAccount);
                int i5 = NotificationCenter.httpFileDidLoad;
                notificationCenter.removeObserver(this, i5);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.httpFileDidFailedLoad);
                this.uploadingImage = null;
                if (i == i4 || i == i5) {
                    processBitmap(ImageLoader.loadBitmap(this.finalPath, null, 800.0f, 800.0f, true), null);
                    return;
                } else {
                    this.imageReceiver.setImageBitmap((Drawable) null);
                    return;
                }
            }
            int i6 = NotificationCenter.filePreparingFailed;
            if (i == i6) {
                MessageObject messageObject = (MessageObject) objArr[0];
                if (messageObject != this.convertingVideo || (baseFragment2 = this.parentFragment) == null) {
                    return;
                }
                baseFragment2.getSendMessagesHelper().stopVideoService(messageObject.messageOwner.attachPath);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.filePreparingStarted);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, i6);
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileNewChunkAvailable);
                cleanup();
            } else if (i == NotificationCenter.fileNewChunkAvailable) {
                MessageObject messageObject2 = (MessageObject) objArr[0];
                if (messageObject2 != this.convertingVideo || this.parentFragment == null) {
                    return;
                }
                String str4 = (String) objArr[1];
                long longValue = ((Long) objArr[2]).longValue();
                long longValue2 = ((Long) objArr[3]).longValue();
                this.parentFragment.getFileLoader().checkUploadNewDataAvailable(str4, false, longValue, longValue2);
                if (longValue2 == 0) {
                    return;
                }
                double longValue3 = ((Long) objArr[5]).longValue();
                Double.isNaN(longValue3);
                double d = longValue3 / 1000000.0d;
                if (this.videoTimestamp > d) {
                    this.videoTimestamp = d;
                }
                Bitmap createVideoThumbnailAtTime = SendMessagesHelper.createVideoThumbnailAtTime(str4, (long) (this.videoTimestamp * 1000.0d), null, true);
                if (createVideoThumbnailAtTime != null) {
                    File pathToAttach = FileLoader.getInstance(this.currentAccount).getPathToAttach(this.smallPhoto, true);
                    if (pathToAttach != null) {
                        pathToAttach.delete();
                    }
                    File pathToAttach2 = FileLoader.getInstance(this.currentAccount).getPathToAttach(this.bigPhoto, true);
                    if (pathToAttach2 != null) {
                        pathToAttach2.delete();
                    }
                    this.bigPhoto = ImageLoader.scaleAndSaveImage(createVideoThumbnailAtTime, 800.0f, 800.0f, 80, false, 320, 320);
                    TLRPC$PhotoSize scaleAndSaveImage = ImageLoader.scaleAndSaveImage(createVideoThumbnailAtTime, 150.0f, 150.0f, 80, false, 150, 150);
                    this.smallPhoto = scaleAndSaveImage;
                    if (scaleAndSaveImage != null) {
                        try {
                            ImageLoader.getInstance().putImageToCache(new BitmapDrawable(BitmapFactory.decodeFile(FileLoader.getInstance(this.currentAccount).getPathToAttach(this.smallPhoto, true).getAbsolutePath())), this.smallPhoto.location.volume_id + "_" + this.smallPhoto.location.local_id + "@50_50", true);
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
            } else if (i != NotificationCenter.filePreparingStarted || ((MessageObject) objArr[0]) != this.convertingVideo || (baseFragment = this.parentFragment) == null) {
            } else {
                this.uploadingVideo = (String) objArr[1];
                baseFragment.getFileLoader().uploadFile(this.uploadingVideo, false, false, (int) this.convertingVideo.videoEditedInfo.estimatedSize, 33554432, false);
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
