package org.telegram.p005ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build.VERSION;
import android.provider.Settings.System;
import android.support.annotation.Keep;
import android.support.media.ExifInterface;
import android.text.TextUtils.TruncateAt;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewAnimationUtils;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.CLASSNAMER;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DataQuery;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver.BitmapHolder;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaController.AlbumEntry;
import org.telegram.messenger.MediaController.PhotoEntry;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.messenger.camera.CameraController;
import org.telegram.messenger.camera.CameraView;
import org.telegram.messenger.camera.CameraView.CameraViewDelegate;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.ItemDecoration;
import org.telegram.messenger.support.widget.RecyclerView.LayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.State;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.p005ui.ActionBar.AlertDialog.Builder;
import org.telegram.p005ui.ActionBar.BaseFragment;
import org.telegram.p005ui.ActionBar.BottomSheet;
import org.telegram.p005ui.ActionBar.BottomSheet.BottomSheetDelegateInterface;
import org.telegram.p005ui.ActionBar.Theme;
import org.telegram.p005ui.Cells.PhotoAttachCameraCell;
import org.telegram.p005ui.Cells.PhotoAttachPhotoCell;
import org.telegram.p005ui.Cells.ShadowSectionCell;
import org.telegram.p005ui.ChatActivity;
import org.telegram.p005ui.Components.RecyclerListView.Holder;
import org.telegram.p005ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.p005ui.Components.ShutterButton.ShutterButtonDelegate;
import org.telegram.p005ui.PhotoViewer;
import org.telegram.p005ui.PhotoViewer.EmptyPhotoViewerProvider;
import org.telegram.p005ui.PhotoViewer.PhotoViewerProvider;
import org.telegram.p005ui.PhotoViewer.PlaceProviderObject;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.TL_topPeer;
import org.telegram.tgnet.TLRPC.User;

/* renamed from: org.telegram.ui.Components.ChatAttachAlert */
public class ChatAttachAlert extends BottomSheet implements NotificationCenterDelegate, BottomSheetDelegateInterface {
    private static ArrayList<Object> cameraPhotos = new ArrayList();
    private static int lastImageId = -1;
    private static boolean mediaFromExternalCamera;
    private static HashMap<Object, Object> selectedPhotos = new HashMap();
    private static ArrayList<Object> selectedPhotosOrder = new ArrayList();
    private ListAdapter adapter;
    private int[] animateCameraValues = new int[5];
    private ArrayList<AttachButton> attachButtons = new ArrayList();
    private LinearLayoutManager attachPhotoLayoutManager;
    private RecyclerListView attachPhotoRecyclerView;
    private ViewGroup attachView;
    private BaseFragment baseFragment;
    private boolean buttonPressed;
    private boolean cameraAnimationInProgress;
    private PhotoAttachAdapter cameraAttachAdapter;
    private FrameLayout cameraIcon;
    private ImageView cameraImageView;
    private boolean cameraInitied;
    private float cameraOpenProgress;
    private boolean cameraOpened;
    private FrameLayout cameraPanel;
    private LinearLayoutManager cameraPhotoLayoutManager;
    private RecyclerListView cameraPhotoRecyclerView;
    private boolean cameraPhotoRecyclerViewIgnoreLayout;
    private CameraView cameraView;
    private int[] cameraViewLocation = new int[2];
    private int cameraViewOffsetX;
    private int cameraViewOffsetY;
    private boolean cancelTakingPhotos;
    private Paint ciclePaint = new Paint(1);
    private TextView counterTextView;
    private int currentAccount = UserConfig.selectedAccount;
    private AnimatorSet currentHintAnimation;
    private DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();
    private ChatAttachViewDelegate delegate;
    private boolean deviceHasGoodCamera;
    private boolean dragging;
    private MessageObject editingMessageObject;
    private boolean flashAnimationInProgress;
    private ImageView[] flashModeButton = new ImageView[2];
    private Runnable hideHintRunnable;
    private boolean hintShowed;
    private TextView hintTextView;
    private boolean ignoreLayout;
    private ArrayList<InnerAnimator> innerAnimators = new ArrayList();
    private DecelerateInterpolator interpolator = new DecelerateInterpolator(1.5f);
    private float lastY;
    private LinearLayoutManager layoutManager;
    private View lineView;
    private RecyclerListView listView;
    private boolean loading = true;
    private int maxSelectedPhotos = -1;
    private boolean maybeStartDraging;
    private CorrectlyMeasuringTextView mediaBanTooltip;
    private boolean mediaCaptured;
    private boolean mediaEnabled = true;
    private boolean openWithFrontFaceCamera;
    private boolean paused;
    private PhotoAttachAdapter photoAttachAdapter;
    private PhotoViewerProvider photoViewerProvider = new CLASSNAME();
    private boolean pressed;
    private EmptyTextProgressView progressView;
    private TextView recordTime;
    private boolean requestingPermissions;
    private boolean revealAnimationInProgress;
    private float revealRadius;
    private int revealX;
    private int revealY;
    private int scrollOffsetY;
    private AttachButton sendDocumentsButton;
    private AttachButton sendPhotosButton;
    private Drawable shadowDrawable;
    private ShutterButton shutterButton;
    private ImageView switchCameraButton;
    private boolean takingPhoto;
    private boolean useRevealAnimation;
    private Runnable videoRecordRunnable;
    private int videoRecordTime;
    private int[] viewPosition = new int[2];
    private View[] views = new View[20];

    /* renamed from: org.telegram.ui.Components.ChatAttachAlert$ChatAttachViewDelegate */
    public interface ChatAttachViewDelegate {
        boolean allowGroupPhotos();

        void didPressedButton(int i);

        void didSelectBot(User user);

        View getRevealView();

        void onCameraOpened();
    }

    /* renamed from: org.telegram.ui.Components.ChatAttachAlert$11 */
    class CLASSNAME extends AnimatorListenerAdapter {
        CLASSNAME() {
        }

        public void onAnimationEnd(Animator animator) {
            ImageView access$5300 = ChatAttachAlert.this.switchCameraButton;
            int i = (ChatAttachAlert.this.cameraView == null || !ChatAttachAlert.this.cameraView.isFrontface()) ? CLASSNAMER.drawable.camera_revert2 : CLASSNAMER.drawable.camera_revert1;
            access$5300.setImageResource(i);
            ObjectAnimator.ofFloat(ChatAttachAlert.this.switchCameraButton, "scaleX", new float[]{1.0f}).setDuration(100).start();
        }
    }

    /* renamed from: org.telegram.ui.Components.ChatAttachAlert$BasePhotoProvider */
    private class BasePhotoProvider extends EmptyPhotoViewerProvider {
        private BasePhotoProvider() {
        }

        /* synthetic */ BasePhotoProvider(ChatAttachAlert x0, CLASSNAME x1) {
            this();
        }

        public boolean isPhotoChecked(int index) {
            PhotoEntry photoEntry = ChatAttachAlert.this.getPhotoEntryAtPosition(index);
            return photoEntry != null && ChatAttachAlert.selectedPhotos.containsKey(Integer.valueOf(photoEntry.imageId));
        }

        /* JADX WARNING: Removed duplicated region for block: B:26:0x0090  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public int setPhotoChecked(int index, VideoEditedInfo videoEditedInfo) {
            if (ChatAttachAlert.this.maxSelectedPhotos >= 0 && ChatAttachAlert.selectedPhotos.size() >= ChatAttachAlert.this.maxSelectedPhotos && !isPhotoChecked(index)) {
                return -1;
            }
            PhotoEntry photoEntry = ChatAttachAlert.this.getPhotoEntryAtPosition(index);
            if (photoEntry == null) {
                return -1;
            }
            int a;
            boolean add = true;
            int num = ChatAttachAlert.this.addToSelectedPhotos(photoEntry, -1);
            if (num == -1) {
                num = ChatAttachAlert.selectedPhotosOrder.indexOf(Integer.valueOf(photoEntry.imageId));
            } else {
                add = false;
                photoEntry.editedInfo = null;
            }
            photoEntry.editedInfo = videoEditedInfo;
            int count = ChatAttachAlert.this.attachPhotoRecyclerView.getChildCount();
            for (a = 0; a < count; a++) {
                View view = ChatAttachAlert.this.attachPhotoRecyclerView.getChildAt(a);
                if ((view instanceof PhotoAttachPhotoCell) && ((Integer) view.getTag()).intValue() == index) {
                    if (!(ChatAttachAlert.this.baseFragment instanceof ChatActivity) || ChatAttachAlert.this.maxSelectedPhotos >= 0) {
                        ((PhotoAttachPhotoCell) view).setChecked(-1, add, false);
                    } else {
                        ((PhotoAttachPhotoCell) view).setChecked(num, add, false);
                    }
                    count = ChatAttachAlert.this.cameraPhotoRecyclerView.getChildCount();
                    for (a = 0; a < count; a++) {
                        view = ChatAttachAlert.this.cameraPhotoRecyclerView.getChildAt(a);
                        if ((view instanceof PhotoAttachPhotoCell) && ((Integer) view.getTag()).intValue() == index) {
                            if (!(ChatAttachAlert.this.baseFragment instanceof ChatActivity) || ChatAttachAlert.this.maxSelectedPhotos >= 0) {
                                ((PhotoAttachPhotoCell) view).setChecked(-1, add, false);
                            } else {
                                ((PhotoAttachPhotoCell) view).setChecked(num, add, false);
                            }
                            ChatAttachAlert.this.updatePhotosButton();
                            return num;
                        }
                    }
                    ChatAttachAlert.this.updatePhotosButton();
                    return num;
                }
            }
            count = ChatAttachAlert.this.cameraPhotoRecyclerView.getChildCount();
            while (a < count) {
            }
            ChatAttachAlert.this.updatePhotosButton();
            return num;
        }

        public boolean allowGroupPhotos() {
            return ChatAttachAlert.this.delegate.allowGroupPhotos();
        }

        public int getSelectedCount() {
            return ChatAttachAlert.selectedPhotos.size();
        }

        public ArrayList<Object> getSelectedPhotosOrder() {
            return ChatAttachAlert.selectedPhotosOrder;
        }

        public HashMap<Object, Object> getSelectedPhotos() {
            return ChatAttachAlert.selectedPhotos;
        }

        public int getPhotoIndex(int index) {
            PhotoEntry photoEntry = ChatAttachAlert.this.getPhotoEntryAtPosition(index);
            if (photoEntry == null) {
                return -1;
            }
            return ChatAttachAlert.selectedPhotosOrder.indexOf(Integer.valueOf(photoEntry.imageId));
        }
    }

    /* renamed from: org.telegram.ui.Components.ChatAttachAlert$16 */
    class CLASSNAME extends AnimatorListenerAdapter {
        CLASSNAME() {
        }

        public void onAnimationEnd(Animator animation) {
            if (ChatAttachAlert.this.currentHintAnimation != null && ChatAttachAlert.this.currentHintAnimation.equals(animation)) {
                ChatAttachAlert.this.currentHintAnimation = null;
                if (ChatAttachAlert.this.hintTextView != null) {
                    ChatAttachAlert.this.hintTextView.setVisibility(4);
                }
            }
        }

        public void onAnimationCancel(Animator animation) {
            if (ChatAttachAlert.this.currentHintAnimation != null && ChatAttachAlert.this.currentHintAnimation.equals(animation)) {
                ChatAttachAlert.this.currentHintAnimation = null;
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.ChatAttachAlert$17 */
    class CLASSNAME extends AnimatorListenerAdapter {
        CLASSNAME() {
        }

        public void onAnimationEnd(Animator animator) {
            ChatAttachAlert.this.cameraAnimationInProgress = false;
            if (ChatAttachAlert.this.cameraOpened) {
                ChatAttachAlert.this.delegate.onCameraOpened();
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.ChatAttachAlert$18 */
    class CLASSNAME extends AnimatorListenerAdapter {
        CLASSNAME() {
        }

        public void onAnimationEnd(Animator animator) {
            ChatAttachAlert.this.cameraAnimationInProgress = false;
            ChatAttachAlert.this.cameraOpened = false;
            if (ChatAttachAlert.this.cameraPanel != null) {
                ChatAttachAlert.this.cameraPanel.setVisibility(8);
            }
            if (ChatAttachAlert.this.cameraPhotoRecyclerView != null) {
                ChatAttachAlert.this.cameraPhotoRecyclerView.setVisibility(8);
            }
            if (VERSION.SDK_INT >= 21 && ChatAttachAlert.this.cameraView != null) {
                ChatAttachAlert.this.cameraView.setSystemUiVisibility(1024);
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.ChatAttachAlert$19 */
    class CLASSNAME implements CameraViewDelegate {
        CLASSNAME() {
        }

        public void onCameraCreated(Camera camera) {
        }

        public void onCameraInit() {
            int a;
            int i = 0;
            int count = ChatAttachAlert.this.attachPhotoRecyclerView.getChildCount();
            for (a = 0; a < count; a++) {
                View child = ChatAttachAlert.this.attachPhotoRecyclerView.getChildAt(a);
                if (child instanceof PhotoAttachCameraCell) {
                    child.setVisibility(4);
                    break;
                }
            }
            if (ChatAttachAlert.this.cameraView.getCameraSession().getCurrentFlashMode().equals(ChatAttachAlert.this.cameraView.getCameraSession().getNextFlashMode())) {
                for (a = 0; a < 2; a++) {
                    ChatAttachAlert.this.flashModeButton[a].setVisibility(4);
                    ChatAttachAlert.this.flashModeButton[a].setAlpha(0.0f);
                    ChatAttachAlert.this.flashModeButton[a].setTranslationY(0.0f);
                }
            } else {
                ChatAttachAlert.this.setCameraFlashModeIcon(ChatAttachAlert.this.flashModeButton[0], ChatAttachAlert.this.cameraView.getCameraSession().getCurrentFlashMode());
                for (a = 0; a < 2; a++) {
                    int i2;
                    float f;
                    ImageView imageView = ChatAttachAlert.this.flashModeButton[a];
                    if (a == 0) {
                        i2 = 0;
                    } else {
                        i2 = 4;
                    }
                    imageView.setVisibility(i2);
                    imageView = ChatAttachAlert.this.flashModeButton[a];
                    if (a == 0 && ChatAttachAlert.this.cameraOpened) {
                        f = 1.0f;
                    } else {
                        f = 0.0f;
                    }
                    imageView.setAlpha(f);
                    ChatAttachAlert.this.flashModeButton[a].setTranslationY(0.0f);
                }
            }
            ChatAttachAlert.this.switchCameraButton.setImageResource(ChatAttachAlert.this.cameraView.isFrontface() ? CLASSNAMER.drawable.camera_revert1 : CLASSNAMER.drawable.camera_revert2);
            ImageView access$5300 = ChatAttachAlert.this.switchCameraButton;
            if (!ChatAttachAlert.this.cameraView.hasFrontFaceCamera()) {
                i = 4;
            }
            access$5300.setVisibility(i);
        }
    }

    /* renamed from: org.telegram.ui.Components.ChatAttachAlert$1 */
    class CLASSNAME extends BasePhotoProvider {
        CLASSNAME() {
            super(ChatAttachAlert.this, null);
        }

        public PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, FileLocation fileLocation, int index) {
            PhotoAttachPhotoCell cell = ChatAttachAlert.this.getCellForIndex(index);
            if (cell == null) {
                return null;
            }
            int[] coords = new int[2];
            cell.getImageView().getLocationInWindow(coords);
            if (VERSION.SDK_INT < 26) {
                coords[0] = coords[0] - ChatAttachAlert.this.access$1100();
            }
            PlaceProviderObject object = new PlaceProviderObject();
            object.viewX = coords[0];
            object.viewY = coords[1];
            object.parentView = ChatAttachAlert.this.attachPhotoRecyclerView;
            object.imageReceiver = cell.getImageView().getImageReceiver();
            object.thumb = object.imageReceiver.getBitmapSafe();
            object.scale = cell.getImageView().getScaleX();
            cell.showCheck(false);
            return object;
        }

        public void updatePhotoAtIndex(int index) {
            PhotoAttachPhotoCell cell = ChatAttachAlert.this.getCellForIndex(index);
            if (cell != null) {
                cell.getImageView().setOrientation(0, true);
                PhotoEntry photoEntry = ChatAttachAlert.this.getPhotoEntryAtPosition(index);
                if (photoEntry != null) {
                    if (photoEntry.thumbPath != null) {
                        cell.getImageView().setImage(photoEntry.thumbPath, null, cell.getContext().getResources().getDrawable(CLASSNAMER.drawable.nophotos));
                    } else if (photoEntry.path != null) {
                        cell.getImageView().setOrientation(photoEntry.orientation, true);
                        if (photoEntry.isVideo) {
                            cell.getImageView().setImage("vthumb://" + photoEntry.imageId + ":" + photoEntry.path, null, cell.getContext().getResources().getDrawable(CLASSNAMER.drawable.nophotos));
                        } else {
                            cell.getImageView().setImage("thumb://" + photoEntry.imageId + ":" + photoEntry.path, null, cell.getContext().getResources().getDrawable(CLASSNAMER.drawable.nophotos));
                        }
                    } else {
                        cell.getImageView().setImageResource(CLASSNAMER.drawable.nophotos);
                    }
                }
            }
        }

        public BitmapHolder getThumbForPhoto(MessageObject messageObject, FileLocation fileLocation, int index) {
            PhotoAttachPhotoCell cell = ChatAttachAlert.this.getCellForIndex(index);
            if (cell != null) {
                return cell.getImageView().getImageReceiver().getBitmapSafe();
            }
            return null;
        }

        public void willSwitchFromPhoto(MessageObject messageObject, FileLocation fileLocation, int index) {
            PhotoAttachPhotoCell cell = ChatAttachAlert.this.getCellForIndex(index);
            if (cell != null) {
                cell.showCheck(true);
            }
        }

        public void willHidePhotoViewer() {
            int count = ChatAttachAlert.this.attachPhotoRecyclerView.getChildCount();
            for (int a = 0; a < count; a++) {
                View view = ChatAttachAlert.this.attachPhotoRecyclerView.getChildAt(a);
                if (view instanceof PhotoAttachPhotoCell) {
                    ((PhotoAttachPhotoCell) view).showCheck(true);
                }
            }
        }

        public boolean cancelButtonPressed() {
            return false;
        }

        public void sendButtonPressed(int index, VideoEditedInfo videoEditedInfo) {
            PhotoEntry photoEntry = ChatAttachAlert.this.getPhotoEntryAtPosition(index);
            if (photoEntry != null) {
                photoEntry.editedInfo = videoEditedInfo;
            }
            if (ChatAttachAlert.selectedPhotos.isEmpty() && photoEntry != null) {
                ChatAttachAlert.this.addToSelectedPhotos(photoEntry, -1);
            }
            ChatAttachAlert.this.delegate.didPressedButton(7);
        }
    }

    /* renamed from: org.telegram.ui.Components.ChatAttachAlert$20 */
    class CLASSNAME extends AnimatorListenerAdapter {

        /* renamed from: org.telegram.ui.Components.ChatAttachAlert$20$1 */
        class CLASSNAME implements Runnable {
            CLASSNAME() {
            }

            public void run() {
                if (ChatAttachAlert.this.hideHintRunnable == this) {
                    ChatAttachAlert.this.hideHintRunnable = null;
                    ChatAttachAlert.this.hideHint();
                }
            }
        }

        CLASSNAME() {
        }

        public void onAnimationEnd(Animator animation) {
            if (ChatAttachAlert.this.currentHintAnimation != null && ChatAttachAlert.this.currentHintAnimation.equals(animation)) {
                ChatAttachAlert.this.currentHintAnimation = null;
                AndroidUtilities.runOnUIThread(ChatAttachAlert.this.hideHintRunnable = new CLASSNAME(), AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
            }
        }

        public void onAnimationCancel(Animator animation) {
            if (ChatAttachAlert.this.currentHintAnimation != null && ChatAttachAlert.this.currentHintAnimation.equals(animation)) {
                ChatAttachAlert.this.currentHintAnimation = null;
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.ChatAttachAlert$3 */
    class CLASSNAME extends ItemDecoration {
        CLASSNAME() {
        }

        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
            outRect.left = 0;
            outRect.right = 0;
            outRect.top = 0;
            outRect.bottom = 0;
        }
    }

    /* renamed from: org.telegram.ui.Components.ChatAttachAlert$4 */
    class CLASSNAME extends OnScrollListener {
        CLASSNAME() {
        }

        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (ChatAttachAlert.this.listView.getChildCount() > 0) {
                if (ChatAttachAlert.this.hintShowed && ChatAttachAlert.this.layoutManager.findLastVisibleItemPosition() > 1) {
                    ChatAttachAlert.this.hideHint();
                    ChatAttachAlert.this.hintShowed = false;
                    MessagesController.getGlobalMainSettings().edit().putBoolean("bothint", true).commit();
                }
                ChatAttachAlert.this.updateLayout();
                ChatAttachAlert.this.checkCameraViewPosition();
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.ChatAttachAlert$7 */
    class CLASSNAME extends OnScrollListener {
        CLASSNAME() {
        }

        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            ChatAttachAlert.this.checkCameraViewPosition();
        }
    }

    /* renamed from: org.telegram.ui.Components.ChatAttachAlert$AttachBotButton */
    private class AttachBotButton extends FrameLayout {
        private AvatarDrawable avatarDrawable = new AvatarDrawable();
        private boolean checkingForLongPress = false;
        private User currentUser;
        private BackupImageView imageView;
        private TextView nameTextView;
        private CheckForLongPress pendingCheckForLongPress = null;
        private CheckForTap pendingCheckForTap = null;
        private int pressCount = 0;
        private boolean pressed;

        /* renamed from: org.telegram.ui.Components.ChatAttachAlert$AttachBotButton$CheckForLongPress */
        class CheckForLongPress implements Runnable {
            public int currentPressCount;

            CheckForLongPress() {
            }

            public void run() {
                if (AttachBotButton.this.checkingForLongPress && AttachBotButton.this.getParent() != null && this.currentPressCount == AttachBotButton.this.pressCount) {
                    AttachBotButton.this.checkingForLongPress = false;
                    AttachBotButton.this.performHapticFeedback(0);
                    AttachBotButton.this.onLongPress();
                    MotionEvent event = MotionEvent.obtain(0, 0, 3, 0.0f, 0.0f, 0);
                    AttachBotButton.this.onTouchEvent(event);
                    event.recycle();
                }
            }
        }

        /* renamed from: org.telegram.ui.Components.ChatAttachAlert$AttachBotButton$CheckForTap */
        private final class CheckForTap implements Runnable {
            private CheckForTap() {
            }

            /* synthetic */ CheckForTap(AttachBotButton x0, CLASSNAME x1) {
                this();
            }

            public void run() {
                if (AttachBotButton.this.pendingCheckForLongPress == null) {
                    AttachBotButton.this.pendingCheckForLongPress = new CheckForLongPress();
                }
                AttachBotButton.this.pendingCheckForLongPress.currentPressCount = AttachBotButton.access$1304(AttachBotButton.this);
                AttachBotButton.this.postDelayed(AttachBotButton.this.pendingCheckForLongPress, (long) (ViewConfiguration.getLongPressTimeout() - ViewConfiguration.getTapTimeout()));
            }
        }

        static /* synthetic */ int access$1304(AttachBotButton x0) {
            int i = x0.pressCount + 1;
            x0.pressCount = i;
            return i;
        }

        public AttachBotButton(Context context) {
            super(context);
            this.imageView = new BackupImageView(context);
            this.imageView.setRoundRadius(AndroidUtilities.m9dp(27.0f));
            addView(this.imageView, LayoutHelper.createFrame(54, 54.0f, 49, 0.0f, 7.0f, 0.0f, 0.0f));
            this.nameTextView = new TextView(context);
            this.nameTextView.setTextSize(1, 12.0f);
            this.nameTextView.setMaxLines(2);
            this.nameTextView.setGravity(49);
            this.nameTextView.setLines(2);
            this.nameTextView.setEllipsize(TruncateAt.END);
            addView(this.nameTextView, LayoutHelper.createFrame(-1, -2.0f, 51, 6.0f, 65.0f, 6.0f, 0.0f));
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(AndroidUtilities.m9dp(85.0f), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.m9dp(100.0f), NUM));
        }

        private void onLongPress() {
            if (ChatAttachAlert.this.baseFragment != null && this.currentUser != null) {
                Builder builder = new Builder(getContext());
                builder.setTitle(LocaleController.getString("AppName", CLASSNAMER.string.AppName));
                builder.setMessage(LocaleController.formatString("ChatHintsDelete", CLASSNAMER.string.ChatHintsDelete, ContactsController.formatName(this.currentUser.first_name, this.currentUser.last_name)));
                builder.setPositiveButton(LocaleController.getString("OK", CLASSNAMER.string.OK), new ChatAttachAlert$AttachBotButton$$Lambda$0(this));
                builder.setNegativeButton(LocaleController.getString("Cancel", CLASSNAMER.string.Cancel), null);
                builder.show();
            }
        }

        final /* synthetic */ void lambda$onLongPress$0$ChatAttachAlert$AttachBotButton(DialogInterface dialogInterface, int i) {
            DataQuery.getInstance(ChatAttachAlert.this.currentAccount).removeInline(this.currentUser.var_id);
        }

        public void setUser(User user) {
            if (user != null) {
                this.nameTextView.setTextColor(Theme.getColor(Theme.key_dialogTextGray2));
                this.currentUser = user;
                TLObject photo = null;
                this.nameTextView.setText(ContactsController.formatName(user.first_name, user.last_name));
                this.avatarDrawable.setInfo(user);
                if (!(user == null || user.photo == null)) {
                    photo = user.photo.photo_small;
                }
                this.imageView.setImage(photo, "50_50", this.avatarDrawable, (Object) user);
                requestLayout();
            }
        }

        public boolean onTouchEvent(MotionEvent event) {
            boolean result = false;
            if (event.getAction() == 0) {
                this.pressed = true;
                invalidate();
                result = true;
            } else if (this.pressed) {
                if (event.getAction() == 1) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                    this.pressed = false;
                    playSoundEffect(0);
                    ChatAttachAlert.this.delegate.didSelectBot(MessagesController.getInstance(ChatAttachAlert.this.currentAccount).getUser(Integer.valueOf(((TL_topPeer) DataQuery.getInstance(ChatAttachAlert.this.currentAccount).inlineBots.get(((Integer) getTag()).intValue())).peer.user_id)));
                    ChatAttachAlert.this.setUseRevealAnimation(false);
                    ChatAttachAlert.this.dismiss();
                    ChatAttachAlert.this.setUseRevealAnimation(true);
                    invalidate();
                } else if (event.getAction() == 3) {
                    this.pressed = false;
                    invalidate();
                }
            }
            if (!result) {
                result = super.onTouchEvent(event);
            } else if (event.getAction() == 0) {
                startCheckLongPress();
            }
            if (!(event.getAction() == 0 || event.getAction() == 2)) {
                cancelCheckLongPress();
            }
            return result;
        }

        protected void startCheckLongPress() {
            if (!this.checkingForLongPress) {
                this.checkingForLongPress = true;
                if (this.pendingCheckForTap == null) {
                    this.pendingCheckForTap = new CheckForTap(this, null);
                }
                postDelayed(this.pendingCheckForTap, (long) ViewConfiguration.getTapTimeout());
            }
        }

        protected void cancelCheckLongPress() {
            this.checkingForLongPress = false;
            if (this.pendingCheckForLongPress != null) {
                removeCallbacks(this.pendingCheckForLongPress);
            }
            if (this.pendingCheckForTap != null) {
                removeCallbacks(this.pendingCheckForTap);
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.ChatAttachAlert$AttachButton */
    private class AttachButton extends FrameLayout {
        private ImageView imageView;
        private TextView textView;

        public AttachButton(Context context) {
            super(context);
            this.imageView = new ImageView(context);
            this.imageView.setScaleType(ScaleType.CENTER);
            addView(this.imageView, LayoutHelper.createFrame(54, 54.0f, 49, 0.0f, 5.0f, 0.0f, 0.0f));
            this.textView = new TextView(context);
            this.textView.setMaxLines(2);
            this.textView.setGravity(1);
            this.textView.setEllipsize(TruncateAt.END);
            this.textView.setTextColor(Theme.getColor(Theme.key_dialogTextGray2));
            this.textView.setTextSize(1, 12.0f);
            this.textView.setLineSpacing((float) (-AndroidUtilities.m9dp(2.0f)), 1.0f);
            addView(this.textView, LayoutHelper.createFrame(-1, -2.0f, 51, 0.0f, 64.0f, 0.0f, 0.0f));
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(AndroidUtilities.m9dp(85.0f), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.m9dp(92.0f), NUM));
        }

        public void setTextAndIcon(CharSequence text, Drawable drawable) {
            this.textView.setText(text);
            this.imageView.setBackgroundDrawable(drawable);
        }

        public boolean hasOverlappingRendering() {
            return false;
        }
    }

    /* renamed from: org.telegram.ui.Components.ChatAttachAlert$InnerAnimator */
    private class InnerAnimator {
        private AnimatorSet animatorSet;
        private float startRadius;

        private InnerAnimator() {
        }

        /* synthetic */ InnerAnimator(ChatAttachAlert x0, CLASSNAME x1) {
            this();
        }
    }

    /* renamed from: org.telegram.ui.Components.ChatAttachAlert$ListAdapter */
    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            View frameLayout;
            switch (viewType) {
                case 0:
                    view = ChatAttachAlert.this.attachView;
                    break;
                case 1:
                    frameLayout = new FrameLayout(this.mContext);
                    frameLayout.addView(new ShadowSectionCell(this.mContext), LayoutHelper.createFrame(-1, -1.0f));
                    view = frameLayout;
                    break;
                default:
                    frameLayout = new FrameLayout(this.mContext) {
                        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                            int diff = ((right - left) - AndroidUtilities.m9dp(360.0f)) / 3;
                            for (int a = 0; a < 4; a++) {
                                int x = AndroidUtilities.m9dp(10.0f) + ((a % 4) * (AndroidUtilities.m9dp(85.0f) + diff));
                                View child = getChildAt(a);
                                child.layout(x, 0, child.getMeasuredWidth() + x, child.getMeasuredHeight());
                            }
                        }
                    };
                    for (int a = 0; a < 4; a++) {
                        frameLayout.addView(new AttachBotButton(this.mContext));
                    }
                    view = frameLayout;
                    frameLayout.setLayoutParams(new LayoutParams(-1, AndroidUtilities.m9dp(100.0f)));
                    break;
            }
            return new Holder(view);
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            if (position == 1) {
                holder.itemView.setBackgroundColor(Theme.getColor(Theme.key_dialogBackgroundGray));
            } else if (position > 1) {
                position = (position - 2) * 4;
                FrameLayout frameLayout = holder.itemView;
                for (int a = 0; a < 4; a++) {
                    AttachBotButton child = (AttachBotButton) frameLayout.getChildAt(a);
                    if (position + a >= DataQuery.getInstance(ChatAttachAlert.this.currentAccount).inlineBots.size()) {
                        child.setVisibility(4);
                    } else {
                        child.setVisibility(0);
                        child.setTag(Integer.valueOf(position + a));
                        child.setUser(MessagesController.getInstance(ChatAttachAlert.this.currentAccount).getUser(Integer.valueOf(((TL_topPeer) DataQuery.getInstance(ChatAttachAlert.this.currentAccount).inlineBots.get(position + a)).peer.user_id)));
                    }
                }
            }
        }

        public boolean isEnabled(ViewHolder holder) {
            return false;
        }

        public int getItemCount() {
            if (ChatAttachAlert.this.editingMessageObject != null || !(ChatAttachAlert.this.baseFragment instanceof ChatActivity)) {
                return 1;
            }
            return (!DataQuery.getInstance(ChatAttachAlert.this.currentAccount).inlineBots.isEmpty() ? ((int) Math.ceil((double) (((float) DataQuery.getInstance(ChatAttachAlert.this.currentAccount).inlineBots.size()) / 4.0f))) + 1 : 0) + 1;
        }

        public int getItemViewType(int position) {
            switch (position) {
                case 0:
                    return 0;
                case 1:
                    return 1;
                default:
                    return 2;
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.ChatAttachAlert$PhotoAttachAdapter */
    private class PhotoAttachAdapter extends SelectionAdapter {
        private Context mContext;
        private boolean needCamera;
        private ArrayList<Holder> viewsCache = new ArrayList(8);

        public PhotoAttachAdapter(Context context, boolean camera) {
            this.mContext = context;
            this.needCamera = camera;
            for (int a = 0; a < 8; a++) {
                this.viewsCache.add(createHolder());
            }
        }

        public Holder createHolder() {
            PhotoAttachPhotoCell cell = new PhotoAttachPhotoCell(this.mContext);
            cell.setDelegate(new ChatAttachAlert$PhotoAttachAdapter$$Lambda$0(this));
            return new Holder(cell);
        }

        final /* synthetic */ void lambda$createHolder$0$ChatAttachAlert$PhotoAttachAdapter(PhotoAttachPhotoCell v) {
            if (ChatAttachAlert.this.mediaEnabled) {
                int index = ((Integer) v.getTag()).intValue();
                PhotoEntry photoEntry = v.getPhotoEntry();
                boolean added = !ChatAttachAlert.selectedPhotos.containsKey(Integer.valueOf(photoEntry.imageId));
                if (!added || ChatAttachAlert.this.maxSelectedPhotos < 0 || ChatAttachAlert.selectedPhotos.size() < ChatAttachAlert.this.maxSelectedPhotos) {
                    int num;
                    if (added) {
                        num = ChatAttachAlert.selectedPhotosOrder.size();
                    } else {
                        num = -1;
                    }
                    if (!(ChatAttachAlert.this.baseFragment instanceof ChatActivity) || ChatAttachAlert.this.maxSelectedPhotos >= 0) {
                        v.setChecked(-1, added, true);
                    } else {
                        v.setChecked(num, added, true);
                    }
                    ChatAttachAlert.this.addToSelectedPhotos(photoEntry, index);
                    int updateIndex = index;
                    if (this == ChatAttachAlert.this.cameraAttachAdapter) {
                        if (ChatAttachAlert.this.photoAttachAdapter.needCamera && ChatAttachAlert.this.deviceHasGoodCamera) {
                            updateIndex++;
                        }
                        ChatAttachAlert.this.photoAttachAdapter.notifyItemChanged(updateIndex);
                    } else {
                        ChatAttachAlert.this.cameraAttachAdapter.notifyItemChanged(updateIndex);
                    }
                    ChatAttachAlert.this.updatePhotosButton();
                }
            }
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            boolean z = true;
            if (!this.needCamera || !ChatAttachAlert.this.deviceHasGoodCamera || position != 0) {
                boolean z2;
                if (this.needCamera && ChatAttachAlert.this.deviceHasGoodCamera) {
                    position--;
                }
                PhotoAttachPhotoCell cell = holder.itemView;
                PhotoEntry photoEntry = ChatAttachAlert.this.getPhotoEntryAtPosition(position);
                boolean z3 = this.needCamera;
                if (position == getItemCount() - 1) {
                    z2 = true;
                } else {
                    z2 = false;
                }
                cell.setPhotoEntry(photoEntry, z3, z2);
                if (!(ChatAttachAlert.this.baseFragment instanceof ChatActivity) || ChatAttachAlert.this.maxSelectedPhotos >= 0) {
                    cell.setChecked(-1, ChatAttachAlert.selectedPhotos.containsKey(Integer.valueOf(photoEntry.imageId)), false);
                } else {
                    cell.setChecked(ChatAttachAlert.selectedPhotosOrder.indexOf(Integer.valueOf(photoEntry.imageId)), ChatAttachAlert.selectedPhotos.containsKey(Integer.valueOf(photoEntry.imageId)), false);
                }
                cell.getImageView().setTag(Integer.valueOf(position));
                cell.setTag(Integer.valueOf(position));
                if (!(this == ChatAttachAlert.this.cameraAttachAdapter && ChatAttachAlert.this.cameraPhotoLayoutManager.getOrientation() == 1)) {
                    z = false;
                }
                cell.setIsVertical(z);
            } else if (!this.needCamera || !ChatAttachAlert.this.deviceHasGoodCamera || position != 0) {
            } else {
                if (ChatAttachAlert.this.cameraView == null || !ChatAttachAlert.this.cameraView.isInitied()) {
                    holder.itemView.setVisibility(0);
                } else {
                    holder.itemView.setVisibility(4);
                }
            }
        }

        public boolean isEnabled(ViewHolder holder) {
            return false;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case 1:
                    return new Holder(new PhotoAttachCameraCell(this.mContext));
                default:
                    if (this.viewsCache.isEmpty()) {
                        return createHolder();
                    }
                    Holder holder = (Holder) this.viewsCache.get(0);
                    this.viewsCache.remove(0);
                    return holder;
            }
        }

        public int getItemCount() {
            AlbumEntry albumEntry;
            int count = 0;
            if (this.needCamera && ChatAttachAlert.this.deviceHasGoodCamera) {
                count = 0 + 1;
            }
            count += ChatAttachAlert.cameraPhotos.size();
            if (ChatAttachAlert.this.baseFragment instanceof ChatActivity) {
                albumEntry = MediaController.allMediaAlbumEntry;
            } else {
                albumEntry = MediaController.allPhotosAlbumEntry;
            }
            if (albumEntry != null) {
                return count + albumEntry.photos.size();
            }
            return count;
        }

        public int getItemViewType(int position) {
            if (this.needCamera && ChatAttachAlert.this.deviceHasGoodCamera && position == 0) {
                return 1;
            }
            return 0;
        }
    }

    static /* synthetic */ int access$6410() {
        int i = lastImageId;
        lastImageId = i - 1;
        return i;
    }

    private void updateCheckedPhotoIndices() {
        if (this.baseFragment instanceof ChatActivity) {
            int a;
            View view;
            PhotoAttachPhotoCell cell;
            PhotoEntry photoEntry;
            int count = this.attachPhotoRecyclerView.getChildCount();
            for (a = 0; a < count; a++) {
                view = this.attachPhotoRecyclerView.getChildAt(a);
                if (view instanceof PhotoAttachPhotoCell) {
                    cell = (PhotoAttachPhotoCell) view;
                    photoEntry = getPhotoEntryAtPosition(((Integer) cell.getTag()).intValue());
                    if (photoEntry != null) {
                        cell.setNum(selectedPhotosOrder.indexOf(Integer.valueOf(photoEntry.imageId)));
                    }
                }
            }
            count = this.cameraPhotoRecyclerView.getChildCount();
            for (a = 0; a < count; a++) {
                view = this.cameraPhotoRecyclerView.getChildAt(a);
                if (view instanceof PhotoAttachPhotoCell) {
                    cell = (PhotoAttachPhotoCell) view;
                    photoEntry = getPhotoEntryAtPosition(((Integer) cell.getTag()).intValue());
                    if (photoEntry != null) {
                        cell.setNum(selectedPhotosOrder.indexOf(Integer.valueOf(photoEntry.imageId)));
                    }
                }
            }
        }
    }

    private PhotoEntry getPhotoEntryAtPosition(int position) {
        if (position < 0) {
            return null;
        }
        int cameraCount = cameraPhotos.size();
        if (position < cameraCount) {
            return (PhotoEntry) cameraPhotos.get(position);
        }
        AlbumEntry albumEntry;
        position -= cameraCount;
        if (this.baseFragment instanceof ChatActivity) {
            albumEntry = MediaController.allMediaAlbumEntry;
        } else {
            albumEntry = MediaController.allPhotosAlbumEntry;
        }
        if (position < albumEntry.photos.size()) {
            return (PhotoEntry) albumEntry.photos.get(position);
        }
        return null;
    }

    private ArrayList<Object> getAllPhotosArray() {
        AlbumEntry albumEntry;
        if (this.baseFragment instanceof ChatActivity) {
            albumEntry = MediaController.allMediaAlbumEntry;
        } else {
            albumEntry = MediaController.allPhotosAlbumEntry;
        }
        if (albumEntry != null) {
            if (cameraPhotos.isEmpty()) {
                return albumEntry.photos;
            }
            ArrayList<Object> arrayList = new ArrayList(albumEntry.photos.size() + cameraPhotos.size());
            arrayList.addAll(cameraPhotos);
            arrayList.addAll(albumEntry.photos);
            return arrayList;
        } else if (cameraPhotos.isEmpty()) {
            return new ArrayList(0);
        } else {
            return cameraPhotos;
        }
    }

    public ChatAttachAlert(Context context, final BaseFragment parentFragment) {
        super(context, false);
        this.baseFragment = parentFragment;
        this.ciclePaint.setColor(Theme.getColor(Theme.key_dialogBackground));
        setDelegate(this);
        setUseRevealAnimation(true);
        checkCamera(false);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.albumsDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.reloadInlineHints);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.cameraInitied);
        this.shadowDrawable = context.getResources().getDrawable(CLASSNAMER.drawable.sheet_shadow).mutate();
        Theme.setDrawableColor(this.shadowDrawable, Theme.getColor(Theme.key_dialogBackground));
        ViewGroup CLASSNAME = new RecyclerListView(context) {
            private int lastHeight;
            private int lastWidth;

            public void requestLayout() {
                if (!ChatAttachAlert.this.ignoreLayout) {
                    super.requestLayout();
                }
            }

            public boolean onInterceptTouchEvent(MotionEvent ev) {
                if (ChatAttachAlert.this.cameraAnimationInProgress) {
                    return true;
                }
                if (ChatAttachAlert.this.cameraOpened) {
                    return ChatAttachAlert.this.processTouchEvent(ev);
                }
                if (ev.getAction() != 0 || ChatAttachAlert.this.scrollOffsetY == 0 || ev.getY() >= ((float) ChatAttachAlert.this.scrollOffsetY)) {
                    return super.onInterceptTouchEvent(ev);
                }
                ChatAttachAlert.this.dismiss();
                return true;
            }

            public boolean onTouchEvent(MotionEvent event) {
                if (ChatAttachAlert.this.cameraAnimationInProgress) {
                    return true;
                }
                if (ChatAttachAlert.this.cameraOpened) {
                    return ChatAttachAlert.this.processTouchEvent(event);
                }
                if (ChatAttachAlert.this.isDismissed() || !super.onTouchEvent(event)) {
                    return false;
                }
                return true;
            }

            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int h;
                int height = MeasureSpec.getSize(heightMeasureSpec);
                if (VERSION.SDK_INT >= 21) {
                    height -= AndroidUtilities.statusBarHeight;
                }
                if (ChatAttachAlert.this.baseFragment instanceof ChatActivity) {
                    h = 298;
                } else {
                    h = 203;
                }
                int contentSize = (AndroidUtilities.m9dp((float) h) + ChatAttachAlert.backgroundPaddingTop) + (DataQuery.getInstance(ChatAttachAlert.this.currentAccount).inlineBots.isEmpty() ? 0 : (((int) Math.ceil((double) (((float) DataQuery.getInstance(ChatAttachAlert.this.currentAccount).inlineBots.size()) / 4.0f))) * AndroidUtilities.m9dp(100.0f)) + AndroidUtilities.m9dp(12.0f));
                int padding = contentSize == AndroidUtilities.m9dp((float) h) ? 0 : Math.max(0, height - AndroidUtilities.m9dp((float) h));
                if (padding != 0 && contentSize < height) {
                    padding -= height - contentSize;
                }
                if (padding == 0) {
                    padding = ChatAttachAlert.backgroundPaddingTop;
                }
                if (getPaddingTop() != padding) {
                    ChatAttachAlert.this.ignoreLayout = true;
                    setPadding(ChatAttachAlert.backgroundPaddingLeft, padding, ChatAttachAlert.backgroundPaddingLeft, 0);
                    ChatAttachAlert.this.ignoreLayout = false;
                }
                super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(Math.min(contentSize, height), NUM));
            }

            protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                int width = right - left;
                int height = bottom - top;
                int newPosition = -1;
                int newTop = 0;
                int lastVisibleItemPosition = -1;
                int lastVisibleItemPositionTop = 0;
                if (ChatAttachAlert.this.listView.getChildCount() > 0) {
                    View child = ChatAttachAlert.this.listView.getChildAt(ChatAttachAlert.this.listView.getChildCount() - 1);
                    Holder holder = (Holder) ChatAttachAlert.this.listView.findContainingViewHolder(child);
                    if (holder != null) {
                        lastVisibleItemPosition = holder.getAdapterPosition();
                        lastVisibleItemPositionTop = child.getTop();
                    }
                }
                if (lastVisibleItemPosition >= 0 && height - this.lastHeight != 0) {
                    newPosition = lastVisibleItemPosition;
                    newTop = ((lastVisibleItemPositionTop + height) - this.lastHeight) - getPaddingTop();
                }
                super.onLayout(changed, left, top, right, bottom);
                if (newPosition != -1) {
                    ChatAttachAlert.this.ignoreLayout = true;
                    ChatAttachAlert.this.layoutManager.scrollToPositionWithOffset(newPosition, newTop);
                    super.onLayout(false, left, top, right, bottom);
                    ChatAttachAlert.this.ignoreLayout = false;
                }
                this.lastHeight = height;
                this.lastWidth = width;
                ChatAttachAlert.this.updateLayout();
                ChatAttachAlert.this.checkCameraViewPosition();
            }

            public void onDraw(Canvas canvas) {
                if (!ChatAttachAlert.this.useRevealAnimation || VERSION.SDK_INT > 19) {
                    ChatAttachAlert.this.shadowDrawable.setBounds(0, ChatAttachAlert.this.scrollOffsetY - ChatAttachAlert.backgroundPaddingTop, getMeasuredWidth(), getMeasuredHeight());
                    ChatAttachAlert.this.shadowDrawable.draw(canvas);
                    return;
                }
                canvas.save();
                canvas.clipRect(ChatAttachAlert.backgroundPaddingLeft, ChatAttachAlert.this.scrollOffsetY, getMeasuredWidth() - ChatAttachAlert.backgroundPaddingLeft, getMeasuredHeight());
                if (ChatAttachAlert.this.revealAnimationInProgress) {
                    canvas.drawCircle((float) ChatAttachAlert.this.revealX, (float) ChatAttachAlert.this.revealY, ChatAttachAlert.this.revealRadius, ChatAttachAlert.this.ciclePaint);
                } else {
                    canvas.drawRect((float) ChatAttachAlert.backgroundPaddingLeft, (float) ChatAttachAlert.this.scrollOffsetY, (float) (getMeasuredWidth() - ChatAttachAlert.backgroundPaddingLeft), (float) getMeasuredHeight(), ChatAttachAlert.this.ciclePaint);
                }
                canvas.restore();
            }

            public void setTranslationY(float translationY) {
                super.setTranslationY(translationY);
                ChatAttachAlert.this.checkCameraViewPosition();
            }
        };
        this.listView = CLASSNAME;
        this.containerView = CLASSNAME;
        this.nestedScrollChild = this.listView;
        this.listView.setWillNotDraw(false);
        this.listView.setClipToPadding(false);
        RecyclerListView recyclerListView = this.listView;
        LayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        this.layoutManager = linearLayoutManager;
        recyclerListView.setLayoutManager(linearLayoutManager);
        this.layoutManager.setOrientation(1);
        recyclerListView = this.listView;
        Adapter listAdapter = new ListAdapter(context);
        this.adapter = listAdapter;
        recyclerListView.setAdapter(listAdapter);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setEnabled(true);
        this.listView.setGlowColor(Theme.getColor(Theme.key_dialogScrollGlow));
        this.listView.addItemDecoration(new CLASSNAME());
        this.listView.setOnScrollListener(new CLASSNAME());
        this.containerView.setPadding(backgroundPaddingLeft, 0, backgroundPaddingLeft, 0);
        this.attachView = new FrameLayout(context) {
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                if (ChatAttachAlert.this.baseFragment instanceof ChatActivity) {
                    super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(AndroidUtilities.m9dp(298.0f), NUM));
                } else {
                    super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(AndroidUtilities.m9dp(203.0f), NUM));
                }
            }

            protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                int width = right - left;
                int height = bottom - top;
                int t = AndroidUtilities.m9dp(8.0f);
                ChatAttachAlert.this.attachPhotoRecyclerView.layout(0, t, width, ChatAttachAlert.this.attachPhotoRecyclerView.getMeasuredHeight() + t);
                ChatAttachAlert.this.progressView.layout(0, t, width, ChatAttachAlert.this.progressView.getMeasuredHeight() + t);
                ChatAttachAlert.this.lineView.layout(0, AndroidUtilities.m9dp(96.0f), width, AndroidUtilities.m9dp(96.0f) + ChatAttachAlert.this.lineView.getMeasuredHeight());
                ChatAttachAlert.this.hintTextView.layout((width - ChatAttachAlert.this.hintTextView.getMeasuredWidth()) - AndroidUtilities.m9dp(5.0f), (height - ChatAttachAlert.this.hintTextView.getMeasuredHeight()) - AndroidUtilities.m9dp(5.0f), width - AndroidUtilities.m9dp(5.0f), height - AndroidUtilities.m9dp(5.0f));
                int x = (width - ChatAttachAlert.this.mediaBanTooltip.getMeasuredWidth()) / 2;
                int y = t + ((ChatAttachAlert.this.attachPhotoRecyclerView.getMeasuredHeight() - ChatAttachAlert.this.mediaBanTooltip.getMeasuredHeight()) / 2);
                ChatAttachAlert.this.mediaBanTooltip.layout(x, y, ChatAttachAlert.this.mediaBanTooltip.getMeasuredWidth() + x, ChatAttachAlert.this.mediaBanTooltip.getMeasuredHeight() + y);
                int diff = (width - AndroidUtilities.m9dp(360.0f)) / 3;
                int num = 0;
                for (int a = 0; a < 8; a++) {
                    if (ChatAttachAlert.this.views[a] != null) {
                        y = AndroidUtilities.m9dp((float) (((num / 4) * 97) + 105));
                        x = AndroidUtilities.m9dp(10.0f) + ((num % 4) * (AndroidUtilities.m9dp(85.0f) + diff));
                        ChatAttachAlert.this.views[a].layout(x, y, ChatAttachAlert.this.views[a].getMeasuredWidth() + x, ChatAttachAlert.this.views[a].getMeasuredHeight() + y);
                        num++;
                    }
                }
            }
        };
        View[] viewArr = this.views;
        RecyclerListView recyclerListView2 = new RecyclerListView(context);
        this.attachPhotoRecyclerView = recyclerListView2;
        viewArr[8] = recyclerListView2;
        this.attachPhotoRecyclerView.setVerticalScrollBarEnabled(true);
        recyclerListView = this.attachPhotoRecyclerView;
        listAdapter = new PhotoAttachAdapter(context, true);
        this.photoAttachAdapter = listAdapter;
        recyclerListView.setAdapter(listAdapter);
        this.attachPhotoRecyclerView.setClipToPadding(false);
        this.attachPhotoRecyclerView.setPadding(AndroidUtilities.m9dp(8.0f), 0, AndroidUtilities.m9dp(8.0f), 0);
        this.attachPhotoRecyclerView.setItemAnimator(null);
        this.attachPhotoRecyclerView.setLayoutAnimation(null);
        this.attachPhotoRecyclerView.setOverScrollMode(2);
        this.attachView.addView(this.attachPhotoRecyclerView, LayoutHelper.createFrame(-1, 80.0f));
        this.attachPhotoLayoutManager = new LinearLayoutManager(context) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        this.attachPhotoLayoutManager.setOrientation(0);
        this.attachPhotoRecyclerView.setLayoutManager(this.attachPhotoLayoutManager);
        this.attachPhotoRecyclerView.setOnItemClickListener(new ChatAttachAlert$$Lambda$0(this));
        this.attachPhotoRecyclerView.setOnScrollListener(new CLASSNAME());
        viewArr = this.views;
        CorrectlyMeasuringTextView correctlyMeasuringTextView = new CorrectlyMeasuringTextView(context);
        this.mediaBanTooltip = correctlyMeasuringTextView;
        viewArr[11] = correctlyMeasuringTextView;
        this.mediaBanTooltip.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.m9dp(3.0f), -12171706));
        this.mediaBanTooltip.setPadding(AndroidUtilities.m9dp(8.0f), AndroidUtilities.m9dp(8.0f), AndroidUtilities.m9dp(8.0f), AndroidUtilities.m9dp(8.0f));
        this.mediaBanTooltip.setGravity(16);
        this.mediaBanTooltip.setTextSize(1, 14.0f);
        this.mediaBanTooltip.setTextColor(-1);
        this.mediaBanTooltip.setVisibility(4);
        this.attachView.addView(this.mediaBanTooltip, LayoutHelper.createFrame(-2, -2.0f, 51, 14.0f, 0.0f, 14.0f, 0.0f));
        viewArr = this.views;
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context);
        this.progressView = emptyTextProgressView;
        viewArr[9] = emptyTextProgressView;
        if (VERSION.SDK_INT < 23 || getContext().checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == 0) {
            this.progressView.setText(LocaleController.getString("NoPhotos", CLASSNAMER.string.NoPhotos));
            this.progressView.setTextSize(20);
        } else {
            this.progressView.setText(LocaleController.getString("PermissionStorage", CLASSNAMER.string.PermissionStorage));
            this.progressView.setTextSize(16);
        }
        this.attachView.addView(this.progressView, LayoutHelper.createFrame(-1, 80.0f));
        this.attachPhotoRecyclerView.setEmptyView(this.progressView);
        viewArr = this.views;
        View CLASSNAME = new View(getContext()) {
            public boolean hasOverlappingRendering() {
                return false;
            }
        };
        this.lineView = CLASSNAME;
        viewArr[10] = CLASSNAME;
        this.lineView.setBackgroundColor(Theme.getColor(Theme.key_dialogGrayLine));
        this.attachView.addView(this.lineView, new FrameLayout.LayoutParams(-1, 1, 51));
        CharSequence[] items = new CharSequence[]{LocaleController.getString("ChatCamera", CLASSNAMER.string.ChatCamera), LocaleController.getString("ChatGallery", CLASSNAMER.string.ChatGallery), LocaleController.getString("ChatVideo", CLASSNAMER.string.ChatVideo), LocaleController.getString("AttachMusic", CLASSNAMER.string.AttachMusic), LocaleController.getString("ChatDocument", CLASSNAMER.string.ChatDocument), LocaleController.getString("AttachContact", CLASSNAMER.string.AttachContact), LocaleController.getString("ChatLocation", CLASSNAMER.string.ChatLocation), TtmlNode.ANONYMOUS_REGION_ID};
        int a = 0;
        while (a < 8) {
            if ((this.baseFragment instanceof ChatActivity) || !(a == 2 || a == 3 || a == 5 || a == 6)) {
                AttachButton attachButton = new AttachButton(context);
                this.attachButtons.add(attachButton);
                attachButton.setTextAndIcon(items[a], Theme.chat_attachButtonDrawables[a]);
                this.attachView.addView(attachButton, LayoutHelper.createFrame(85, 91, 51));
                attachButton.setTag(Integer.valueOf(a));
                this.views[a] = attachButton;
                if (a == 7) {
                    this.sendPhotosButton = attachButton;
                    this.sendPhotosButton.imageView.setPadding(0, AndroidUtilities.m9dp(4.0f), 0, 0);
                } else if (a == 4) {
                    this.sendDocumentsButton = attachButton;
                }
                attachButton.setOnClickListener(new ChatAttachAlert$$Lambda$1(this, parentFragment));
            }
            a++;
        }
        this.hintTextView = new TextView(context);
        this.hintTextView.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.m9dp(3.0f), Theme.getColor(Theme.key_chat_gifSaveHintBackground)));
        this.hintTextView.setTextColor(Theme.getColor(Theme.key_chat_gifSaveHintText));
        this.hintTextView.setTextSize(1, 14.0f);
        this.hintTextView.setPadding(AndroidUtilities.m9dp(10.0f), 0, AndroidUtilities.m9dp(10.0f), 0);
        this.hintTextView.setText(LocaleController.getString("AttachBotsHelp", CLASSNAMER.string.AttachBotsHelp));
        this.hintTextView.setGravity(16);
        this.hintTextView.setVisibility(4);
        this.hintTextView.setCompoundDrawablesWithIntrinsicBounds(CLASSNAMER.drawable.scroll_tip, 0, 0, 0);
        this.hintTextView.setCompoundDrawablePadding(AndroidUtilities.m9dp(8.0f));
        this.attachView.addView(this.hintTextView, LayoutHelper.createFrame(-2, 32.0f, 85, 5.0f, 0.0f, 5.0f, 5.0f));
        if (this.loading) {
            this.progressView.showProgress();
        } else {
            this.progressView.showTextView();
        }
        this.recordTime = new TextView(context);
        this.recordTime.setBackgroundResource(CLASSNAMER.drawable.system);
        this.recordTime.getBackground().setColorFilter(new PorterDuffColorFilter(NUM, Mode.MULTIPLY));
        this.recordTime.setTextSize(1, 15.0f);
        this.recordTime.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.recordTime.setAlpha(0.0f);
        this.recordTime.setTextColor(-1);
        this.recordTime.setPadding(AndroidUtilities.m9dp(10.0f), AndroidUtilities.m9dp(5.0f), AndroidUtilities.m9dp(10.0f), AndroidUtilities.m9dp(5.0f));
        this.container.addView(this.recordTime, LayoutHelper.createFrame(-2, -2.0f, 49, 0.0f, 16.0f, 0.0f, 0.0f));
        this.cameraPanel = new FrameLayout(context) {
            protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                int cx2;
                int cy2;
                int cx = getMeasuredWidth() / 2;
                int cy = getMeasuredHeight() / 2;
                ChatAttachAlert.this.shutterButton.layout(cx - (ChatAttachAlert.this.shutterButton.getMeasuredWidth() / 2), cy - (ChatAttachAlert.this.shutterButton.getMeasuredHeight() / 2), (ChatAttachAlert.this.shutterButton.getMeasuredWidth() / 2) + cx, (ChatAttachAlert.this.shutterButton.getMeasuredHeight() / 2) + cy);
                if (getMeasuredWidth() == AndroidUtilities.m9dp(100.0f)) {
                    cx2 = getMeasuredWidth() / 2;
                    cx = cx2;
                    cy2 = ((cy / 2) + cy) + AndroidUtilities.m9dp(17.0f);
                    cy = (cy / 2) - AndroidUtilities.m9dp(17.0f);
                } else {
                    cx2 = ((cx / 2) + cx) + AndroidUtilities.m9dp(17.0f);
                    cx = (cx / 2) - AndroidUtilities.m9dp(17.0f);
                    cy2 = getMeasuredHeight() / 2;
                    cy = cy2;
                }
                ChatAttachAlert.this.switchCameraButton.layout(cx2 - (ChatAttachAlert.this.switchCameraButton.getMeasuredWidth() / 2), cy2 - (ChatAttachAlert.this.switchCameraButton.getMeasuredHeight() / 2), (ChatAttachAlert.this.switchCameraButton.getMeasuredWidth() / 2) + cx2, (ChatAttachAlert.this.switchCameraButton.getMeasuredHeight() / 2) + cy2);
                for (int a = 0; a < 2; a++) {
                    ChatAttachAlert.this.flashModeButton[a].layout(cx - (ChatAttachAlert.this.flashModeButton[a].getMeasuredWidth() / 2), cy - (ChatAttachAlert.this.flashModeButton[a].getMeasuredHeight() / 2), (ChatAttachAlert.this.flashModeButton[a].getMeasuredWidth() / 2) + cx, (ChatAttachAlert.this.flashModeButton[a].getMeasuredHeight() / 2) + cy);
                }
            }
        };
        this.cameraPanel.setVisibility(8);
        this.cameraPanel.setAlpha(0.0f);
        this.container.addView(this.cameraPanel, LayoutHelper.createFrame(-1, 100, 83));
        this.counterTextView = new TextView(context);
        this.counterTextView.setBackgroundResource(CLASSNAMER.drawable.photos_rounded);
        this.counterTextView.setVisibility(8);
        this.counterTextView.setTextColor(-1);
        this.counterTextView.setGravity(17);
        this.counterTextView.setPivotX(0.0f);
        this.counterTextView.setPivotY(0.0f);
        this.counterTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.counterTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, CLASSNAMER.drawable.photos_arrow, 0);
        this.counterTextView.setCompoundDrawablePadding(AndroidUtilities.m9dp(4.0f));
        this.counterTextView.setPadding(AndroidUtilities.m9dp(16.0f), 0, AndroidUtilities.m9dp(16.0f), 0);
        this.container.addView(this.counterTextView, LayoutHelper.createFrame(-2, 38.0f, 51, 0.0f, 0.0f, 0.0f, 116.0f));
        this.counterTextView.setOnClickListener(new ChatAttachAlert$$Lambda$2(this));
        this.shutterButton = new ShutterButton(context);
        this.cameraPanel.addView(this.shutterButton, LayoutHelper.createFrame(84, 84, 17));
        this.shutterButton.setDelegate(new ShutterButtonDelegate() {
            private File outputFile;

            public boolean shutterLongPressed() {
                if (!(ChatAttachAlert.this.baseFragment instanceof ChatActivity) || ChatAttachAlert.this.mediaCaptured || ChatAttachAlert.this.takingPhoto || ChatAttachAlert.this.baseFragment == null || ChatAttachAlert.this.baseFragment.getParentActivity() == null || ChatAttachAlert.this.cameraView == null) {
                    return false;
                }
                if (VERSION.SDK_INT < 23 || ChatAttachAlert.this.baseFragment.getParentActivity().checkSelfPermission("android.permission.RECORD_AUDIO") == 0) {
                    for (int a = 0; a < 2; a++) {
                        ChatAttachAlert.this.flashModeButton[a].setAlpha(0.0f);
                    }
                    ChatAttachAlert.this.switchCameraButton.setAlpha(0.0f);
                    boolean z = (ChatAttachAlert.this.baseFragment instanceof ChatActivity) && ((ChatActivity) ChatAttachAlert.this.baseFragment).isSecretChat();
                    this.outputFile = AndroidUtilities.generateVideoPath(z);
                    ChatAttachAlert.this.recordTime.setAlpha(1.0f);
                    ChatAttachAlert.this.recordTime.setText(String.format("%02d:%02d", new Object[]{Integer.valueOf(0), Integer.valueOf(0)}));
                    ChatAttachAlert.this.videoRecordTime = 0;
                    ChatAttachAlert.this.videoRecordRunnable = new ChatAttachAlert$10$$Lambda$0(this);
                    AndroidUtilities.lockOrientation(parentFragment.getParentActivity());
                    CameraController.getInstance().recordVideo(ChatAttachAlert.this.cameraView.getCameraSession(), this.outputFile, new ChatAttachAlert$10$$Lambda$1(this), new ChatAttachAlert$10$$Lambda$2(this));
                    ChatAttachAlert.this.shutterButton.setState(ShutterButton.State.RECORDING, true);
                    return true;
                }
                ChatAttachAlert.this.requestingPermissions = true;
                ChatAttachAlert.this.baseFragment.getParentActivity().requestPermissions(new String[]{"android.permission.RECORD_AUDIO"}, 21);
                return false;
            }

            final /* synthetic */ void lambda$shutterLongPressed$0$ChatAttachAlert$10() {
                if (ChatAttachAlert.this.videoRecordRunnable != null) {
                    ChatAttachAlert.this.videoRecordTime = ChatAttachAlert.this.videoRecordTime + 1;
                    ChatAttachAlert.this.recordTime.setText(String.format("%02d:%02d", new Object[]{Integer.valueOf(ChatAttachAlert.this.videoRecordTime / 60), Integer.valueOf(ChatAttachAlert.this.videoRecordTime % 60)}));
                    AndroidUtilities.runOnUIThread(ChatAttachAlert.this.videoRecordRunnable, 1000);
                }
            }

            final /* synthetic */ void lambda$shutterLongPressed$1$ChatAttachAlert$10(String thumbPath, long duration) {
                if (this.outputFile != null && ChatAttachAlert.this.baseFragment != null) {
                    ChatAttachAlert.mediaFromExternalCamera = false;
                    PhotoEntry photoEntry = new PhotoEntry(0, ChatAttachAlert.access$6410(), 0, this.outputFile.getAbsolutePath(), 0, true);
                    photoEntry.duration = (int) duration;
                    photoEntry.thumbPath = thumbPath;
                    ChatAttachAlert.this.openPhotoViewer(photoEntry, false, false);
                }
            }

            final /* synthetic */ void lambda$shutterLongPressed$2$ChatAttachAlert$10() {
                AndroidUtilities.runOnUIThread(ChatAttachAlert.this.videoRecordRunnable, 1000);
            }

            public void shutterCancel() {
                if (!ChatAttachAlert.this.mediaCaptured) {
                    if (this.outputFile != null) {
                        this.outputFile.delete();
                        this.outputFile = null;
                    }
                    ChatAttachAlert.this.resetRecordState();
                    CameraController.getInstance().stopVideoRecording(ChatAttachAlert.this.cameraView.getCameraSession(), true);
                }
            }

            public void shutterReleased() {
                if (!ChatAttachAlert.this.takingPhoto && ChatAttachAlert.this.cameraView != null && !ChatAttachAlert.this.mediaCaptured && ChatAttachAlert.this.cameraView.getCameraSession() != null) {
                    ChatAttachAlert.this.mediaCaptured = true;
                    if (ChatAttachAlert.this.shutterButton.getState() == ShutterButton.State.RECORDING) {
                        ChatAttachAlert.this.resetRecordState();
                        CameraController.getInstance().stopVideoRecording(ChatAttachAlert.this.cameraView.getCameraSession(), false);
                        ChatAttachAlert.this.shutterButton.setState(ShutterButton.State.DEFAULT, true);
                        return;
                    }
                    boolean z;
                    if ((ChatAttachAlert.this.baseFragment instanceof ChatActivity) && ((ChatActivity) ChatAttachAlert.this.baseFragment).isSecretChat()) {
                        z = true;
                    } else {
                        z = false;
                    }
                    File cameraFile = AndroidUtilities.generatePicturePath(z);
                    boolean sameTakePictureOrientation = ChatAttachAlert.this.cameraView.getCameraSession().isSameTakePictureOrientation();
                    ChatAttachAlert.this.cameraView.getCameraSession().setFlipFront(parentFragment instanceof ChatActivity);
                    ChatAttachAlert.this.takingPhoto = CameraController.getInstance().takePicture(cameraFile, ChatAttachAlert.this.cameraView.getCameraSession(), new ChatAttachAlert$10$$Lambda$3(this, cameraFile, sameTakePictureOrientation));
                }
            }

            final /* synthetic */ void lambda$shutterReleased$3$ChatAttachAlert$10(File cameraFile, boolean sameTakePictureOrientation) {
                ChatAttachAlert.this.takingPhoto = false;
                if (cameraFile != null && ChatAttachAlert.this.baseFragment != null) {
                    int orientation = 0;
                    try {
                        switch (new ExifInterface(cameraFile.getAbsolutePath()).getAttributeInt("Orientation", 1)) {
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
                    ChatAttachAlert.mediaFromExternalCamera = false;
                    PhotoEntry photoEntry = new PhotoEntry(0, ChatAttachAlert.access$6410(), 0, cameraFile.getAbsolutePath(), orientation, false);
                    photoEntry.canDeleteAfter = true;
                    ChatAttachAlert.this.openPhotoViewer(photoEntry, sameTakePictureOrientation, false);
                }
            }
        });
        this.switchCameraButton = new ImageView(context);
        this.switchCameraButton.setScaleType(ScaleType.CENTER);
        this.cameraPanel.addView(this.switchCameraButton, LayoutHelper.createFrame(48, 48, 21));
        this.switchCameraButton.setOnClickListener(new ChatAttachAlert$$Lambda$3(this));
        for (a = 0; a < 2; a++) {
            this.flashModeButton[a] = new ImageView(context);
            this.flashModeButton[a].setScaleType(ScaleType.CENTER);
            this.flashModeButton[a].setVisibility(4);
            this.cameraPanel.addView(this.flashModeButton[a], LayoutHelper.createFrame(48, 48, 51));
            this.flashModeButton[a].setOnClickListener(new ChatAttachAlert$$Lambda$4(this));
        }
        this.cameraPhotoRecyclerView = new RecyclerListView(context) {
            public void requestLayout() {
                if (!ChatAttachAlert.this.cameraPhotoRecyclerViewIgnoreLayout) {
                    super.requestLayout();
                }
            }
        };
        this.cameraPhotoRecyclerView.setVerticalScrollBarEnabled(true);
        recyclerListView = this.cameraPhotoRecyclerView;
        listAdapter = new PhotoAttachAdapter(context, false);
        this.cameraAttachAdapter = listAdapter;
        recyclerListView.setAdapter(listAdapter);
        this.cameraPhotoRecyclerView.setClipToPadding(false);
        this.cameraPhotoRecyclerView.setPadding(AndroidUtilities.m9dp(8.0f), 0, AndroidUtilities.m9dp(8.0f), 0);
        this.cameraPhotoRecyclerView.setItemAnimator(null);
        this.cameraPhotoRecyclerView.setLayoutAnimation(null);
        this.cameraPhotoRecyclerView.setOverScrollMode(2);
        this.cameraPhotoRecyclerView.setVisibility(4);
        this.cameraPhotoRecyclerView.setAlpha(0.0f);
        this.container.addView(this.cameraPhotoRecyclerView, LayoutHelper.createFrame(-1, 80.0f));
        this.cameraPhotoLayoutManager = new LinearLayoutManager(context, 0, false) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        this.cameraPhotoRecyclerView.setLayoutManager(this.cameraPhotoLayoutManager);
        this.cameraPhotoRecyclerView.setOnItemClickListener(ChatAttachAlert$$Lambda$5.$instance);
    }

    final /* synthetic */ void lambda$new$0$ChatAttachAlert(View view, int position) {
        if (this.baseFragment != null && this.baseFragment.getParentActivity() != null) {
            if (this.deviceHasGoodCamera && position == 0) {
                openCamera(true);
                return;
            }
            if (this.deviceHasGoodCamera) {
                position--;
            }
            ArrayList<Object> arrayList = getAllPhotosArray();
            if (position >= 0 && position < arrayList.size()) {
                ChatActivity chatActivity;
                int type;
                PhotoViewer.getInstance().setParentActivity(this.baseFragment.getParentActivity());
                PhotoViewer.getInstance().setParentAlert(this);
                PhotoViewer.getInstance().setMaxSelectedPhotos(this.maxSelectedPhotos);
                if (this.baseFragment instanceof ChatActivity) {
                    chatActivity = this.baseFragment;
                    type = 0;
                } else {
                    type = 4;
                    chatActivity = null;
                }
                PhotoViewer.getInstance().openPhotoForSelect(arrayList, position, type, this.photoViewerProvider, chatActivity);
                AndroidUtilities.hideKeyboard(this.baseFragment.getFragmentView().findFocus());
            }
        }
    }

    final /* synthetic */ void lambda$new$1$ChatAttachAlert(BaseFragment parentFragment, View v) {
        if (!this.buttonPressed) {
            Integer num = (Integer) v.getTag();
            if (this.deviceHasGoodCamera && num.intValue() == 0 && (this.baseFragment instanceof ChatActivity) && ((ChatActivity) parentFragment).isSecretChat()) {
                openCamera(true);
                return;
            }
            this.buttonPressed = true;
            this.delegate.didPressedButton(num.intValue());
        }
    }

    final /* synthetic */ void lambda$new$2$ChatAttachAlert(View v) {
        if (this.cameraView != null) {
            openPhotoViewer(null, false, false);
            CameraController.getInstance().stopPreview(this.cameraView.getCameraSession());
        }
    }

    final /* synthetic */ void lambda$new$3$ChatAttachAlert(View v) {
        if (!this.takingPhoto && this.cameraView != null && this.cameraView.isInitied()) {
            this.cameraInitied = false;
            this.cameraView.switchCamera();
            ObjectAnimator animator = ObjectAnimator.ofFloat(this.switchCameraButton, "scaleX", new float[]{0.0f}).setDuration(100);
            animator.addListener(new CLASSNAME());
            animator.start();
        }
    }

    final /* synthetic */ void lambda$new$4$ChatAttachAlert(final View currentImage) {
        if (!this.flashAnimationInProgress && this.cameraView != null && this.cameraView.isInitied() && this.cameraOpened) {
            String current = this.cameraView.getCameraSession().getCurrentFlashMode();
            String next = this.cameraView.getCameraSession().getNextFlashMode();
            if (!current.equals(next)) {
                this.cameraView.getCameraSession().setCurrentFlashMode(next);
                this.flashAnimationInProgress = true;
                ImageView nextImage = this.flashModeButton[0] == currentImage ? this.flashModeButton[1] : this.flashModeButton[0];
                nextImage.setVisibility(0);
                setCameraFlashModeIcon(nextImage, next);
                AnimatorSet animatorSet = new AnimatorSet();
                r4 = new Animator[4];
                r4[0] = ObjectAnimator.ofFloat(currentImage, "translationY", new float[]{0.0f, (float) AndroidUtilities.m9dp(48.0f)});
                r4[1] = ObjectAnimator.ofFloat(nextImage, "translationY", new float[]{(float) (-AndroidUtilities.m9dp(48.0f)), 0.0f});
                r4[2] = ObjectAnimator.ofFloat(currentImage, "alpha", new float[]{1.0f, 0.0f});
                r4[3] = ObjectAnimator.ofFloat(nextImage, "alpha", new float[]{0.0f, 1.0f});
                animatorSet.playTogether(r4);
                animatorSet.setDuration(200);
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        ChatAttachAlert.this.flashAnimationInProgress = false;
                        currentImage.setVisibility(4);
                    }
                });
                animatorSet.start();
            }
        }
    }

    static final /* synthetic */ void lambda$new$5$ChatAttachAlert(View view, int position) {
        if (view instanceof PhotoAttachPhotoCell) {
            ((PhotoAttachPhotoCell) view).callDelegate();
        }
    }

    public void show() {
        super.show();
        this.buttonPressed = false;
    }

    public void setEditingMessageObject(MessageObject messageObject) {
        if (this.editingMessageObject != messageObject) {
            this.editingMessageObject = messageObject;
            if (this.editingMessageObject != null) {
                this.maxSelectedPhotos = 1;
            } else {
                this.maxSelectedPhotos = -1;
            }
            this.adapter.notifyDataSetChanged();
            int a = 0;
            while (a < 4) {
                float f;
                boolean enabled = a < 2 ? this.editingMessageObject == null || !this.editingMessageObject.hasValidGroupId() : this.editingMessageObject == null;
                ((AttachButton) this.attachButtons.get(a + 3)).setEnabled(enabled);
                AttachButton attachButton = (AttachButton) this.attachButtons.get(a + 3);
                if (enabled) {
                    f = 1.0f;
                } else {
                    f = 0.2f;
                }
                attachButton.setAlpha(f);
                a++;
            }
            updatePollMusicButton();
        }
    }

    public MessageObject getEditingMessageObject() {
        return this.editingMessageObject;
    }

    private void updatePollMusicButton() {
        int i = 3;
        if ((this.baseFragment instanceof ChatActivity) && !this.attachButtons.isEmpty()) {
            boolean allowPoll = false;
            if (this.editingMessageObject != null) {
                allowPoll = false;
            } else {
                Chat currentChat = ((ChatActivity) this.baseFragment).getCurrentChat();
                if (ChatObject.isChannel(currentChat) && !currentChat.megagroup) {
                    allowPoll = ChatObject.isChannel(currentChat) && (currentChat.creator || currentChat.admin_rights != null);
                } else if (currentChat != null) {
                    allowPoll = true;
                }
            }
            String text = allowPoll ? LocaleController.getString("Poll", CLASSNAMER.string.Poll) : LocaleController.getString("AttachMusic", CLASSNAMER.string.AttachMusic);
            AttachButton attachButton = (AttachButton) this.attachButtons.get(3);
            Drawable[] drawableArr = Theme.chat_attachButtonDrawables;
            if (allowPoll) {
                i = 9;
            }
            attachButton.setTextAndIcon(text, drawableArr[i]);
        }
    }

    private void updatePhotosCounter() {
        if (this.counterTextView != null) {
            boolean hasVideo = false;
            for (Entry<Object, Object> entry : selectedPhotos.entrySet()) {
                if (((PhotoEntry) entry.getValue()).isVideo) {
                    hasVideo = true;
                    break;
                }
            }
            if (hasVideo) {
                this.counterTextView.setText(LocaleController.formatPluralString("Media", selectedPhotos.size()).toUpperCase());
            } else {
                this.counterTextView.setText(LocaleController.formatPluralString("Photos", selectedPhotos.size()).toUpperCase());
            }
        }
    }

    private void openPhotoViewer(PhotoEntry entry, final boolean sameTakePictureOrientation, boolean external) {
        if (entry != null) {
            cameraPhotos.add(entry);
            selectedPhotos.put(Integer.valueOf(entry.imageId), entry);
            selectedPhotosOrder.add(Integer.valueOf(entry.imageId));
            updatePhotosButton();
            this.photoAttachAdapter.notifyDataSetChanged();
            this.cameraAttachAdapter.notifyDataSetChanged();
        }
        if (entry != null && !external && cameraPhotos.size() > 1) {
            updatePhotosCounter();
            if (this.cameraView != null) {
                CameraController.getInstance().startPreview(this.cameraView.getCameraSession());
            }
            this.mediaCaptured = false;
        } else if (!cameraPhotos.isEmpty()) {
            ChatActivity chatActivity;
            int type;
            this.cancelTakingPhotos = true;
            PhotoViewer.getInstance().setParentActivity(this.baseFragment.getParentActivity());
            PhotoViewer.getInstance().setParentAlert(this);
            PhotoViewer.getInstance().setMaxSelectedPhotos(this.maxSelectedPhotos);
            if (this.baseFragment instanceof ChatActivity) {
                chatActivity = this.baseFragment;
                type = 2;
            } else {
                chatActivity = null;
                type = 5;
            }
            PhotoViewer.getInstance().openPhotoForSelect(getAllPhotosArray(), cameraPhotos.size() - 1, type, new BasePhotoProvider() {
                public BitmapHolder getThumbForPhoto(MessageObject messageObject, FileLocation fileLocation, int index) {
                    return null;
                }

                public boolean cancelButtonPressed() {
                    if (ChatAttachAlert.this.cameraOpened && ChatAttachAlert.this.cameraView != null) {
                        AndroidUtilities.runOnUIThread(new ChatAttachAlert$15$$Lambda$0(this), 1000);
                        CameraController.getInstance().startPreview(ChatAttachAlert.this.cameraView.getCameraSession());
                    }
                    if (ChatAttachAlert.this.cancelTakingPhotos && ChatAttachAlert.cameraPhotos.size() == 1) {
                        int size = ChatAttachAlert.cameraPhotos.size();
                        for (int a = 0; a < size; a++) {
                            PhotoEntry photoEntry = (PhotoEntry) ChatAttachAlert.cameraPhotos.get(a);
                            new File(photoEntry.path).delete();
                            if (photoEntry.imagePath != null) {
                                new File(photoEntry.imagePath).delete();
                            }
                            if (photoEntry.thumbPath != null) {
                                new File(photoEntry.thumbPath).delete();
                            }
                        }
                        ChatAttachAlert.cameraPhotos.clear();
                        ChatAttachAlert.selectedPhotosOrder.clear();
                        ChatAttachAlert.selectedPhotos.clear();
                        ChatAttachAlert.this.counterTextView.setVisibility(4);
                        ChatAttachAlert.this.cameraPhotoRecyclerView.setVisibility(8);
                        ChatAttachAlert.this.photoAttachAdapter.notifyDataSetChanged();
                        ChatAttachAlert.this.cameraAttachAdapter.notifyDataSetChanged();
                        ChatAttachAlert.this.updatePhotosButton();
                    }
                    return true;
                }

                final /* synthetic */ void lambda$cancelButtonPressed$0$ChatAttachAlert$15() {
                    if (ChatAttachAlert.this.cameraView != null && !ChatAttachAlert.this.isDismissed() && VERSION.SDK_INT >= 21) {
                        ChatAttachAlert.this.cameraView.setSystemUiVisibility(1028);
                    }
                }

                public void needAddMorePhotos() {
                    ChatAttachAlert.this.cancelTakingPhotos = false;
                    if (ChatAttachAlert.mediaFromExternalCamera) {
                        ChatAttachAlert.this.delegate.didPressedButton(0);
                        return;
                    }
                    if (!ChatAttachAlert.this.cameraOpened) {
                        ChatAttachAlert.this.openCamera(false);
                    }
                    ChatAttachAlert.this.counterTextView.setVisibility(0);
                    ChatAttachAlert.this.cameraPhotoRecyclerView.setVisibility(0);
                    ChatAttachAlert.this.counterTextView.setAlpha(1.0f);
                    ChatAttachAlert.this.updatePhotosCounter();
                }

                public void sendButtonPressed(int index, VideoEditedInfo videoEditedInfo) {
                    if (!ChatAttachAlert.cameraPhotos.isEmpty() && ChatAttachAlert.this.baseFragment != null) {
                        if (videoEditedInfo != null && index >= 0 && index < ChatAttachAlert.cameraPhotos.size()) {
                            ((PhotoEntry) ChatAttachAlert.cameraPhotos.get(index)).editedInfo = videoEditedInfo;
                        }
                        if (!((ChatAttachAlert.this.baseFragment instanceof ChatActivity) && ((ChatActivity) ChatAttachAlert.this.baseFragment).isSecretChat())) {
                            int size = ChatAttachAlert.cameraPhotos.size();
                            for (int a = 0; a < size; a++) {
                                AndroidUtilities.addMediaToGallery(((PhotoEntry) ChatAttachAlert.cameraPhotos.get(a)).path);
                            }
                        }
                        ChatAttachAlert.this.delegate.didPressedButton(8);
                        ChatAttachAlert.cameraPhotos.clear();
                        ChatAttachAlert.selectedPhotosOrder.clear();
                        ChatAttachAlert.selectedPhotos.clear();
                        ChatAttachAlert.this.photoAttachAdapter.notifyDataSetChanged();
                        ChatAttachAlert.this.cameraAttachAdapter.notifyDataSetChanged();
                        ChatAttachAlert.this.closeCamera(false);
                        ChatAttachAlert.this.dismiss();
                    }
                }

                public boolean scaleToFill() {
                    if (ChatAttachAlert.this.baseFragment == null || ChatAttachAlert.this.baseFragment.getParentActivity() == null) {
                        return false;
                    }
                    int locked = System.getInt(ChatAttachAlert.this.baseFragment.getParentActivity().getContentResolver(), "accelerometer_rotation", 0);
                    if (sameTakePictureOrientation || locked == 1) {
                        return true;
                    }
                    return false;
                }

                public void willHidePhotoViewer() {
                    ChatAttachAlert.this.mediaCaptured = false;
                    int count = ChatAttachAlert.this.attachPhotoRecyclerView.getChildCount();
                    for (int a = 0; a < count; a++) {
                        View view = ChatAttachAlert.this.attachPhotoRecyclerView.getChildAt(a);
                        if (view instanceof PhotoAttachPhotoCell) {
                            PhotoAttachPhotoCell cell = (PhotoAttachPhotoCell) view;
                            cell.showImage();
                            cell.showCheck(true);
                        }
                    }
                }

                public boolean canScrollAway() {
                    return false;
                }

                public boolean canCaptureMorePhotos() {
                    return ChatAttachAlert.this.maxSelectedPhotos != 1;
                }
            }, chatActivity);
        }
    }

    private boolean processTouchEvent(MotionEvent event) {
        if (event == null) {
            return false;
        }
        if ((this.pressed || event.getActionMasked() != 0) && event.getActionMasked() != 5) {
            if (this.pressed) {
                AnimatorSet animatorSet;
                Animator[] animatorArr;
                if (event.getActionMasked() == 2) {
                    float newY = event.getY();
                    float dy = newY - this.lastY;
                    if (this.maybeStartDraging) {
                        if (Math.abs(dy) > AndroidUtilities.getPixelsInCM(0.4f, false)) {
                            this.maybeStartDraging = false;
                            this.dragging = true;
                        }
                    } else if (this.dragging && this.cameraView != null) {
                        this.cameraView.setTranslationY(this.cameraView.getTranslationY() + dy);
                        this.lastY = newY;
                        if (this.cameraPanel.getTag() == null) {
                            this.cameraPanel.setTag(Integer.valueOf(1));
                            animatorSet = new AnimatorSet();
                            animatorArr = new Animator[5];
                            animatorArr[0] = ObjectAnimator.ofFloat(this.cameraPanel, "alpha", new float[]{0.0f});
                            animatorArr[1] = ObjectAnimator.ofFloat(this.counterTextView, "alpha", new float[]{0.0f});
                            animatorArr[2] = ObjectAnimator.ofFloat(this.flashModeButton[0], "alpha", new float[]{0.0f});
                            animatorArr[3] = ObjectAnimator.ofFloat(this.flashModeButton[1], "alpha", new float[]{0.0f});
                            animatorArr[4] = ObjectAnimator.ofFloat(this.cameraPhotoRecyclerView, "alpha", new float[]{0.0f});
                            animatorSet.playTogether(animatorArr);
                            animatorSet.setDuration(200);
                            animatorSet.start();
                        }
                    }
                } else if (event.getActionMasked() == 3 || event.getActionMasked() == 1 || event.getActionMasked() == 6) {
                    this.pressed = false;
                    if (this.dragging) {
                        this.dragging = false;
                        if (this.cameraView != null) {
                            if (Math.abs(this.cameraView.getTranslationY()) > ((float) this.cameraView.getMeasuredHeight()) / 6.0f) {
                                closeCamera(true);
                            } else {
                                animatorSet = new AnimatorSet();
                                animatorArr = new Animator[6];
                                animatorArr[0] = ObjectAnimator.ofFloat(this.cameraView, "translationY", new float[]{0.0f});
                                animatorArr[1] = ObjectAnimator.ofFloat(this.cameraPanel, "alpha", new float[]{1.0f});
                                animatorArr[2] = ObjectAnimator.ofFloat(this.counterTextView, "alpha", new float[]{1.0f});
                                animatorArr[3] = ObjectAnimator.ofFloat(this.flashModeButton[0], "alpha", new float[]{1.0f});
                                animatorArr[4] = ObjectAnimator.ofFloat(this.flashModeButton[1], "alpha", new float[]{1.0f});
                                animatorArr[5] = ObjectAnimator.ofFloat(this.cameraPhotoRecyclerView, "alpha", new float[]{1.0f});
                                animatorSet.playTogether(animatorArr);
                                animatorSet.setDuration(250);
                                animatorSet.setInterpolator(this.interpolator);
                                animatorSet.start();
                                this.cameraPanel.setTag(null);
                            }
                        }
                    } else if (this.cameraView != null) {
                        this.cameraView.getLocationOnScreen(this.viewPosition);
                        this.cameraView.focusToPoint((int) (event.getRawX() - ((float) this.viewPosition[0])), (int) (event.getRawY() - ((float) this.viewPosition[1])));
                    }
                }
            }
        } else if (!this.takingPhoto) {
            this.pressed = true;
            this.maybeStartDraging = true;
            this.lastY = event.getY();
        }
        return true;
    }

    protected boolean onContainerTouchEvent(MotionEvent event) {
        return this.cameraOpened && processTouchEvent(event);
    }

    public void checkColors() {
        ViewHolder holder;
        int count = this.attachButtons.size();
        for (int a = 0; a < count; a++) {
            ((AttachButton) this.attachButtons.get(a)).textView.setTextColor(Theme.getColor(Theme.key_dialogTextGray2));
        }
        this.lineView.setBackgroundColor(Theme.getColor(Theme.key_dialogGrayLine));
        Theme.setDrawableColor(this.hintTextView.getBackground(), Theme.getColor(Theme.key_chat_gifSaveHintBackground));
        if (this.hintTextView != null) {
            this.hintTextView.setTextColor(Theme.getColor(Theme.key_chat_gifSaveHintText));
        }
        if (this.listView != null) {
            this.listView.setGlowColor(Theme.getColor(Theme.key_dialogScrollGlow));
            holder = this.listView.findViewHolderForAdapterPosition(1);
            if (holder != null) {
                holder.itemView.setBackgroundColor(Theme.getColor(Theme.key_dialogBackgroundGray));
            }
        }
        if (this.ciclePaint != null) {
            this.ciclePaint.setColor(Theme.getColor(Theme.key_dialogBackground));
        }
        Theme.setDrawableColor(this.shadowDrawable, Theme.getColor(Theme.key_dialogBackground));
        if (this.cameraImageView != null) {
            this.cameraImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogCameraIcon), Mode.MULTIPLY));
        }
        if (this.attachPhotoRecyclerView != null) {
            holder = this.attachPhotoRecyclerView.findViewHolderForAdapterPosition(0);
            if (holder != null && (holder.itemView instanceof PhotoAttachCameraCell)) {
                ((PhotoAttachCameraCell) holder.itemView).getImageView().setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogCameraIcon), Mode.MULTIPLY));
            }
        }
    }

    private void resetRecordState() {
        if (this.baseFragment != null) {
            for (int a = 0; a < 2; a++) {
                this.flashModeButton[a].setAlpha(1.0f);
            }
            this.switchCameraButton.setAlpha(1.0f);
            this.recordTime.setAlpha(0.0f);
            AndroidUtilities.cancelRunOnUIThread(this.videoRecordRunnable);
            this.videoRecordRunnable = null;
            AndroidUtilities.unlockOrientation(this.baseFragment.getParentActivity());
        }
    }

    private void setCameraFlashModeIcon(ImageView imageView, String mode) {
        Object obj = -1;
        switch (mode.hashCode()) {
            case 3551:
                if (mode.equals("on")) {
                    obj = 1;
                    break;
                }
                break;
            case 109935:
                if (mode.equals("off")) {
                    obj = null;
                    break;
                }
                break;
            case 3005871:
                if (mode.equals("auto")) {
                    obj = 2;
                    break;
                }
                break;
        }
        switch (obj) {
            case null:
                imageView.setImageResource(CLASSNAMER.drawable.flash_off);
                return;
            case 1:
                imageView.setImageResource(CLASSNAMER.drawable.flash_on);
                return;
            case 2:
                imageView.setImageResource(CLASSNAMER.drawable.flash_auto);
                return;
            default:
                return;
        }
    }

    protected boolean onCustomMeasure(View view, int width, int height) {
        boolean isPortrait;
        if (width < height) {
            isPortrait = true;
        } else {
            isPortrait = false;
        }
        if (view == this.cameraView) {
            if (this.cameraOpened && !this.cameraAnimationInProgress) {
                this.cameraView.measure(MeasureSpec.makeMeasureSpec(width, NUM), MeasureSpec.makeMeasureSpec(height, NUM));
                return true;
            }
        } else if (view == this.cameraPanel) {
            if (isPortrait) {
                this.cameraPanel.measure(MeasureSpec.makeMeasureSpec(width, NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.m9dp(100.0f), NUM));
                return true;
            }
            this.cameraPanel.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.m9dp(100.0f), NUM), MeasureSpec.makeMeasureSpec(height, NUM));
            return true;
        } else if (view == this.cameraPhotoRecyclerView) {
            this.cameraPhotoRecyclerViewIgnoreLayout = true;
            if (isPortrait) {
                this.cameraPhotoRecyclerView.measure(MeasureSpec.makeMeasureSpec(width, NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.m9dp(80.0f), NUM));
                if (this.cameraPhotoLayoutManager.getOrientation() != 0) {
                    this.cameraPhotoRecyclerView.setPadding(AndroidUtilities.m9dp(8.0f), 0, AndroidUtilities.m9dp(8.0f), 0);
                    this.cameraPhotoLayoutManager.setOrientation(0);
                    this.cameraAttachAdapter.notifyDataSetChanged();
                }
            } else {
                this.cameraPhotoRecyclerView.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.m9dp(80.0f), NUM), MeasureSpec.makeMeasureSpec(height, NUM));
                if (this.cameraPhotoLayoutManager.getOrientation() != 1) {
                    this.cameraPhotoRecyclerView.setPadding(0, AndroidUtilities.m9dp(8.0f), 0, AndroidUtilities.m9dp(8.0f));
                    this.cameraPhotoLayoutManager.setOrientation(1);
                    this.cameraAttachAdapter.notifyDataSetChanged();
                }
            }
            this.cameraPhotoRecyclerViewIgnoreLayout = false;
            return true;
        }
        return false;
    }

    protected boolean onCustomLayout(View view, int left, int top, int right, int bottom) {
        int width = right - left;
        int height = bottom - top;
        boolean isPortrait = width < height;
        int cx;
        int cy;
        if (view == this.cameraPanel) {
            if (isPortrait) {
                if (this.cameraPhotoRecyclerView.getVisibility() == 0) {
                    this.cameraPanel.layout(0, bottom - AndroidUtilities.m9dp(196.0f), width, bottom - AndroidUtilities.m9dp(96.0f));
                } else {
                    this.cameraPanel.layout(0, bottom - AndroidUtilities.m9dp(100.0f), width, bottom);
                }
            } else if (this.cameraPhotoRecyclerView.getVisibility() == 0) {
                this.cameraPanel.layout(right - AndroidUtilities.m9dp(196.0f), 0, right - AndroidUtilities.m9dp(96.0f), height);
            } else {
                this.cameraPanel.layout(right - AndroidUtilities.m9dp(100.0f), 0, right, height);
            }
            return true;
        } else if (view == this.counterTextView) {
            if (isPortrait) {
                cx = (width - this.counterTextView.getMeasuredWidth()) / 2;
                cy = bottom - AndroidUtilities.m9dp(154.0f);
                this.counterTextView.setRotation(0.0f);
                if (this.cameraPhotoRecyclerView.getVisibility() == 0) {
                    cy -= AndroidUtilities.m9dp(96.0f);
                }
            } else {
                cx = right - AndroidUtilities.m9dp(154.0f);
                cy = (height / 2) + (this.counterTextView.getMeasuredWidth() / 2);
                this.counterTextView.setRotation(-90.0f);
                if (this.cameraPhotoRecyclerView.getVisibility() == 0) {
                    cx -= AndroidUtilities.m9dp(96.0f);
                }
            }
            this.counterTextView.layout(cx, cy, this.counterTextView.getMeasuredWidth() + cx, this.counterTextView.getMeasuredHeight() + cy);
            return true;
        } else if (view != this.cameraPhotoRecyclerView) {
            return false;
        } else {
            if (isPortrait) {
                cy = height - AndroidUtilities.m9dp(88.0f);
                view.layout(0, cy, view.getMeasuredWidth(), view.getMeasuredHeight() + cy);
            } else {
                cx = (left + width) - AndroidUtilities.m9dp(88.0f);
                view.layout(cx, 0, view.getMeasuredWidth() + cx, view.getMeasuredHeight());
            }
            return true;
        }
    }

    private void hideHint() {
        if (this.hideHintRunnable != null) {
            AndroidUtilities.cancelRunOnUIThread(this.hideHintRunnable);
            this.hideHintRunnable = null;
        }
        if (this.hintTextView != null) {
            this.currentHintAnimation = new AnimatorSet();
            AnimatorSet animatorSet = this.currentHintAnimation;
            Animator[] animatorArr = new Animator[1];
            animatorArr[0] = ObjectAnimator.ofFloat(this.hintTextView, "alpha", new float[]{0.0f});
            animatorSet.playTogether(animatorArr);
            this.currentHintAnimation.setInterpolator(this.decelerateInterpolator);
            this.currentHintAnimation.addListener(new CLASSNAME());
            this.currentHintAnimation.setDuration(300);
            this.currentHintAnimation.start();
        }
    }

    public void onPause() {
        if (this.shutterButton != null) {
            if (this.requestingPermissions) {
                if (this.cameraView != null && this.shutterButton.getState() == ShutterButton.State.RECORDING) {
                    this.shutterButton.setState(ShutterButton.State.DEFAULT, true);
                }
                this.requestingPermissions = false;
            } else {
                if (this.cameraView != null && this.shutterButton.getState() == ShutterButton.State.RECORDING) {
                    resetRecordState();
                    CameraController.getInstance().stopVideoRecording(this.cameraView.getCameraSession(), false);
                    this.shutterButton.setState(ShutterButton.State.DEFAULT, true);
                }
                if (this.cameraOpened) {
                    closeCamera(false);
                }
                hideCamera(true);
            }
            this.paused = true;
        }
    }

    public void onResume() {
        this.paused = false;
        if (isShowing() && !isDismissed()) {
            checkCamera(false);
        }
    }

    private void openCamera(boolean animated) {
        if (this.cameraView != null) {
            if (cameraPhotos.isEmpty()) {
                this.counterTextView.setVisibility(4);
                this.cameraPhotoRecyclerView.setVisibility(8);
            } else {
                this.counterTextView.setVisibility(0);
                this.cameraPhotoRecyclerView.setVisibility(0);
            }
            this.cameraPanel.setVisibility(0);
            this.cameraPanel.setTag(null);
            this.animateCameraValues[0] = 0;
            this.animateCameraValues[1] = AndroidUtilities.m9dp(80.0f) - this.cameraViewOffsetX;
            this.animateCameraValues[2] = AndroidUtilities.m9dp(80.0f) - this.cameraViewOffsetY;
            int a;
            if (animated) {
                this.cameraAnimationInProgress = true;
                ArrayList<Animator> animators = new ArrayList();
                animators.add(ObjectAnimator.ofFloat(this, "cameraOpenProgress", new float[]{0.0f, 1.0f}));
                animators.add(ObjectAnimator.ofFloat(this.cameraPanel, "alpha", new float[]{1.0f}));
                animators.add(ObjectAnimator.ofFloat(this.counterTextView, "alpha", new float[]{1.0f}));
                animators.add(ObjectAnimator.ofFloat(this.cameraPhotoRecyclerView, "alpha", new float[]{1.0f}));
                for (a = 0; a < 2; a++) {
                    if (this.flashModeButton[a].getVisibility() == 0) {
                        animators.add(ObjectAnimator.ofFloat(this.flashModeButton[a], "alpha", new float[]{1.0f}));
                        break;
                    }
                }
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(animators);
                animatorSet.setDuration(200);
                animatorSet.addListener(new CLASSNAME());
                animatorSet.start();
            } else {
                setCameraOpenProgress(1.0f);
                this.cameraPanel.setAlpha(1.0f);
                this.counterTextView.setAlpha(1.0f);
                this.cameraPhotoRecyclerView.setAlpha(1.0f);
                for (a = 0; a < 2; a++) {
                    if (this.flashModeButton[a].getVisibility() == 0) {
                        this.flashModeButton[a].setAlpha(1.0f);
                        break;
                    }
                }
                this.delegate.onCameraOpened();
            }
            if (VERSION.SDK_INT >= 21) {
                this.cameraView.setSystemUiVisibility(1028);
            }
            this.cameraOpened = true;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:73:0x01e8 A:{SYNTHETIC, Splitter: B:73:0x01e8} */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x01f6 A:{SYNTHETIC, Splitter: B:79:0x01f6} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onActivityResultFragment(int requestCode, Intent data, String currentPicturePath) {
        Throwable e;
        Bitmap bitmap;
        File file;
        OutputStream fileOutputStream;
        int i;
        PhotoEntry entry;
        Throwable th;
        if (this.baseFragment != null && this.baseFragment.getParentActivity() != null) {
            mediaFromExternalCamera = true;
            if (requestCode == 0) {
                PhotoViewer.getInstance().setParentActivity(this.baseFragment.getParentActivity());
                PhotoViewer.getInstance().setMaxSelectedPhotos(this.maxSelectedPhotos);
                ArrayList<Object> arrayList = new ArrayList();
                int orientation = 0;
                try {
                    switch (new ExifInterface(currentPicturePath).getAttributeInt("Orientation", 1)) {
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
                } catch (Throwable e2) {
                    FileLog.m13e(e2);
                }
                int i2 = lastImageId;
                lastImageId = i2 - 1;
                PhotoEntry photoEntry = new PhotoEntry(0, i2, 0, currentPicturePath, orientation, false);
                photoEntry.canDeleteAfter = true;
                openPhotoViewer(photoEntry, false, true);
            } else if (requestCode == 2) {
                String videoPath = null;
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.m10d("pic path " + currentPicturePath);
                }
                if (!(data == null || currentPicturePath == null || !new File(currentPicturePath).exists())) {
                    data = null;
                }
                if (data != null) {
                    Uri uri = data.getData();
                    if (uri != null) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.m10d("video record uri " + uri.toString());
                        }
                        videoPath = AndroidUtilities.getPath(uri);
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.m10d("resolved path = " + videoPath);
                        }
                        if (videoPath == null || !new File(videoPath).exists()) {
                            videoPath = currentPicturePath;
                        }
                    } else {
                        videoPath = currentPicturePath;
                    }
                    if (!((this.baseFragment instanceof ChatActivity) && ((ChatActivity) this.baseFragment).isSecretChat())) {
                        AndroidUtilities.addMediaToGallery(currentPicturePath);
                    }
                    currentPicturePath = null;
                }
                if (videoPath == null && currentPicturePath != null && new File(currentPicturePath).exists()) {
                    videoPath = currentPicturePath;
                }
                MediaMetadataRetriever mediaMetadataRetriever = null;
                long duration = 0;
                try {
                    MediaMetadataRetriever mediaMetadataRetriever2 = new MediaMetadataRetriever();
                    try {
                        mediaMetadataRetriever2.setDataSource(videoPath);
                        String d = mediaMetadataRetriever2.extractMetadata(9);
                        if (d != null) {
                            duration = (long) ((int) Math.ceil((double) (((float) Long.parseLong(d)) / 1000.0f)));
                        }
                        if (mediaMetadataRetriever2 != null) {
                            try {
                                mediaMetadataRetriever2.release();
                            } catch (Throwable e22) {
                                FileLog.m13e(e22);
                                mediaMetadataRetriever = mediaMetadataRetriever2;
                            }
                        }
                        mediaMetadataRetriever = mediaMetadataRetriever2;
                    } catch (Exception e3) {
                        e22 = e3;
                        mediaMetadataRetriever = mediaMetadataRetriever2;
                        try {
                            FileLog.m13e(e22);
                            if (mediaMetadataRetriever != null) {
                            }
                            bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, 1);
                            file = new File(FileLoader.getDirectory(4), "-2147483648_" + SharedConfig.getLastLocalId() + ".jpg");
                            fileOutputStream = new FileOutputStream(file);
                            bitmap.compress(CompressFormat.JPEG, 55, fileOutputStream);
                            SharedConfig.saveConfig();
                            i = lastImageId;
                            lastImageId = i - 1;
                            entry = new PhotoEntry(0, i, 0, videoPath, 0, true);
                            entry.duration = (int) duration;
                            entry.thumbPath = file.getAbsolutePath();
                            openPhotoViewer(entry, false, true);
                        } catch (Throwable th2) {
                            th = th2;
                            if (mediaMetadataRetriever != null) {
                                try {
                                    mediaMetadataRetriever.release();
                                } catch (Throwable e222) {
                                    FileLog.m13e(e222);
                                }
                            }
                            throw th;
                        }
                    } catch (Throwable th3) {
                        th = th3;
                        mediaMetadataRetriever = mediaMetadataRetriever2;
                        if (mediaMetadataRetriever != null) {
                        }
                        throw th;
                    }
                } catch (Exception e4) {
                    e222 = e4;
                    FileLog.m13e(e222);
                    if (mediaMetadataRetriever != null) {
                        try {
                            mediaMetadataRetriever.release();
                        } catch (Throwable e2222) {
                            FileLog.m13e(e2222);
                        }
                    }
                    bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, 1);
                    file = new File(FileLoader.getDirectory(4), "-2147483648_" + SharedConfig.getLastLocalId() + ".jpg");
                    fileOutputStream = new FileOutputStream(file);
                    bitmap.compress(CompressFormat.JPEG, 55, fileOutputStream);
                    SharedConfig.saveConfig();
                    i = lastImageId;
                    lastImageId = i - 1;
                    entry = new PhotoEntry(0, i, 0, videoPath, 0, true);
                    entry.duration = (int) duration;
                    entry.thumbPath = file.getAbsolutePath();
                    openPhotoViewer(entry, false, true);
                }
                bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, 1);
                file = new File(FileLoader.getDirectory(4), "-2147483648_" + SharedConfig.getLastLocalId() + ".jpg");
                try {
                    fileOutputStream = new FileOutputStream(file);
                    bitmap.compress(CompressFormat.JPEG, 55, fileOutputStream);
                } catch (Throwable e22222) {
                    FileLog.m13e(e22222);
                }
                SharedConfig.saveConfig();
                i = lastImageId;
                lastImageId = i - 1;
                entry = new PhotoEntry(0, i, 0, videoPath, 0, true);
                entry.duration = (int) duration;
                entry.thumbPath = file.getAbsolutePath();
                openPhotoViewer(entry, false, true);
            }
        }
    }

    public void closeCamera(boolean animated) {
        if (!this.takingPhoto && this.cameraView != null) {
            this.animateCameraValues[1] = AndroidUtilities.m9dp(80.0f) - this.cameraViewOffsetX;
            this.animateCameraValues[2] = AndroidUtilities.m9dp(80.0f) - this.cameraViewOffsetY;
            int a;
            if (animated) {
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.cameraView.getLayoutParams();
                int[] iArr = this.animateCameraValues;
                int translationY = (int) this.cameraView.getTranslationY();
                layoutParams.topMargin = translationY;
                iArr[0] = translationY;
                this.cameraView.setLayoutParams(layoutParams);
                this.cameraView.setTranslationY(0.0f);
                this.cameraAnimationInProgress = true;
                ArrayList<Animator> animators = new ArrayList();
                animators.add(ObjectAnimator.ofFloat(this, "cameraOpenProgress", new float[]{0.0f}));
                animators.add(ObjectAnimator.ofFloat(this.cameraPanel, "alpha", new float[]{0.0f}));
                animators.add(ObjectAnimator.ofFloat(this.counterTextView, "alpha", new float[]{0.0f}));
                animators.add(ObjectAnimator.ofFloat(this.cameraPhotoRecyclerView, "alpha", new float[]{0.0f}));
                for (a = 0; a < 2; a++) {
                    if (this.flashModeButton[a].getVisibility() == 0) {
                        animators.add(ObjectAnimator.ofFloat(this.flashModeButton[a], "alpha", new float[]{0.0f}));
                        break;
                    }
                }
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(animators);
                animatorSet.setDuration(200);
                animatorSet.addListener(new CLASSNAME());
                animatorSet.start();
                return;
            }
            this.animateCameraValues[0] = 0;
            setCameraOpenProgress(0.0f);
            this.cameraPanel.setAlpha(0.0f);
            this.cameraPhotoRecyclerView.setAlpha(0.0f);
            this.counterTextView.setAlpha(0.0f);
            this.cameraPanel.setVisibility(8);
            this.cameraPhotoRecyclerView.setVisibility(8);
            for (a = 0; a < 2; a++) {
                if (this.flashModeButton[a].getVisibility() == 0) {
                    this.flashModeButton[a].setAlpha(0.0f);
                    break;
                }
            }
            this.cameraOpened = false;
            if (VERSION.SDK_INT >= 21) {
                this.cameraView.setSystemUiVisibility(1024);
            }
        }
    }

    @Keep
    public void setCameraOpenProgress(float value) {
        if (this.cameraView != null) {
            boolean isPortrait;
            float endWidth;
            float endHeight;
            this.cameraOpenProgress = value;
            float startWidth = (float) this.animateCameraValues[1];
            float startHeight = (float) this.animateCameraValues[2];
            if (AndroidUtilities.displaySize.x < AndroidUtilities.displaySize.y) {
                isPortrait = true;
            } else {
                isPortrait = false;
            }
            if (isPortrait) {
                endWidth = (float) this.container.getWidth();
                endHeight = (float) this.container.getHeight();
            } else {
                endWidth = (float) this.container.getWidth();
                endHeight = (float) this.container.getHeight();
            }
            if (value == 0.0f) {
                this.cameraView.setClipLeft(this.cameraViewOffsetX);
                this.cameraView.setClipTop(this.cameraViewOffsetY);
                this.cameraView.setTranslationX((float) this.cameraViewLocation[0]);
                this.cameraView.setTranslationY((float) this.cameraViewLocation[1]);
                this.cameraIcon.setTranslationX((float) this.cameraViewLocation[0]);
                this.cameraIcon.setTranslationY((float) this.cameraViewLocation[1]);
            } else if (!(this.cameraView.getTranslationX() == 0.0f && this.cameraView.getTranslationY() == 0.0f)) {
                this.cameraView.setTranslationX(0.0f);
                this.cameraView.setTranslationY(0.0f);
            }
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.cameraView.getLayoutParams();
            layoutParams.width = (int) (((endWidth - startWidth) * value) + startWidth);
            layoutParams.height = (int) (((endHeight - startHeight) * value) + startHeight);
            if (value != 0.0f) {
                this.cameraView.setClipLeft((int) (((float) this.cameraViewOffsetX) * (1.0f - value)));
                this.cameraView.setClipTop((int) (((float) this.cameraViewOffsetY) * (1.0f - value)));
                layoutParams.leftMargin = (int) (((float) this.cameraViewLocation[0]) * (1.0f - value));
                layoutParams.topMargin = (int) ((((float) (this.cameraViewLocation[1] - this.animateCameraValues[0])) * (1.0f - value)) + ((float) this.animateCameraValues[0]));
            } else {
                layoutParams.leftMargin = 0;
                layoutParams.topMargin = 0;
            }
            this.cameraView.setLayoutParams(layoutParams);
            if (value <= 0.5f) {
                this.cameraIcon.setAlpha(1.0f - (value / 0.5f));
            } else {
                this.cameraIcon.setAlpha(0.0f);
            }
        }
    }

    @Keep
    public float getCameraOpenProgress() {
        return this.cameraOpenProgress;
    }

    private void checkCameraViewPosition() {
        if (this.deviceHasGoodCamera) {
            int count = this.attachPhotoRecyclerView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.attachPhotoRecyclerView.getChildAt(a);
                if (child instanceof PhotoAttachCameraCell) {
                    if (VERSION.SDK_INT < 19 || child.isAttachedToWindow()) {
                        child.getLocationInWindow(this.cameraViewLocation);
                        int[] iArr = this.cameraViewLocation;
                        iArr[0] = iArr[0] - access$1100();
                        float listViewX = (this.listView.getX() + ((float) backgroundPaddingLeft)) - ((float) access$1100());
                        if (((float) this.cameraViewLocation[0]) < listViewX) {
                            this.cameraViewOffsetX = (int) (listViewX - ((float) this.cameraViewLocation[0]));
                            if (this.cameraViewOffsetX >= AndroidUtilities.m9dp(80.0f)) {
                                this.cameraViewOffsetX = 0;
                                this.cameraViewLocation[0] = AndroidUtilities.m9dp(-150.0f);
                                this.cameraViewLocation[1] = 0;
                            } else {
                                iArr = this.cameraViewLocation;
                                iArr[0] = iArr[0] + this.cameraViewOffsetX;
                            }
                        } else {
                            this.cameraViewOffsetX = 0;
                        }
                        if (VERSION.SDK_INT < 21 || this.cameraViewLocation[1] >= AndroidUtilities.statusBarHeight) {
                            this.cameraViewOffsetY = 0;
                        } else {
                            this.cameraViewOffsetY = AndroidUtilities.statusBarHeight - this.cameraViewLocation[1];
                            if (this.cameraViewOffsetY >= AndroidUtilities.m9dp(80.0f)) {
                                this.cameraViewOffsetY = 0;
                                this.cameraViewLocation[0] = AndroidUtilities.m9dp(-150.0f);
                                this.cameraViewLocation[1] = 0;
                            } else {
                                iArr = this.cameraViewLocation;
                                iArr[1] = iArr[1] + this.cameraViewOffsetY;
                            }
                        }
                        applyCameraViewPosition();
                        return;
                    }
                    this.cameraViewOffsetX = 0;
                    this.cameraViewOffsetY = 0;
                    this.cameraViewLocation[0] = AndroidUtilities.m9dp(-150.0f);
                    this.cameraViewLocation[1] = 0;
                    applyCameraViewPosition();
                }
            }
            this.cameraViewOffsetX = 0;
            this.cameraViewOffsetY = 0;
            this.cameraViewLocation[0] = AndroidUtilities.m9dp(-150.0f);
            this.cameraViewLocation[1] = 0;
            applyCameraViewPosition();
        }
    }

    private void applyCameraViewPosition() {
        if (this.cameraView != null) {
            FrameLayout.LayoutParams layoutParams;
            if (!this.cameraOpened) {
                this.cameraView.setTranslationX((float) this.cameraViewLocation[0]);
                this.cameraView.setTranslationY((float) this.cameraViewLocation[1]);
            }
            this.cameraIcon.setTranslationX((float) this.cameraViewLocation[0]);
            this.cameraIcon.setTranslationY((float) this.cameraViewLocation[1]);
            int finalWidth = AndroidUtilities.m9dp(80.0f) - this.cameraViewOffsetX;
            int finalHeight = AndroidUtilities.m9dp(80.0f) - this.cameraViewOffsetY;
            if (!this.cameraOpened) {
                this.cameraView.setClipLeft(this.cameraViewOffsetX);
                this.cameraView.setClipTop(this.cameraViewOffsetY);
                layoutParams = (FrameLayout.LayoutParams) this.cameraView.getLayoutParams();
                if (!(layoutParams.height == finalHeight && layoutParams.width == finalWidth)) {
                    layoutParams.width = finalWidth;
                    layoutParams.height = finalHeight;
                    this.cameraView.setLayoutParams(layoutParams);
                    AndroidUtilities.runOnUIThread(new ChatAttachAlert$$Lambda$6(this, layoutParams));
                }
            }
            layoutParams = (FrameLayout.LayoutParams) this.cameraIcon.getLayoutParams();
            if (layoutParams.height != finalHeight || layoutParams.width != finalWidth) {
                layoutParams.width = finalWidth;
                layoutParams.height = finalHeight;
                this.cameraIcon.setLayoutParams(layoutParams);
                AndroidUtilities.runOnUIThread(new ChatAttachAlert$$Lambda$7(this, layoutParams));
            }
        }
    }

    final /* synthetic */ void lambda$applyCameraViewPosition$6$ChatAttachAlert(FrameLayout.LayoutParams layoutParamsFinal) {
        if (this.cameraView != null) {
            this.cameraView.setLayoutParams(layoutParamsFinal);
        }
    }

    final /* synthetic */ void lambda$applyCameraViewPosition$7$ChatAttachAlert(FrameLayout.LayoutParams layoutParamsFinal) {
        if (this.cameraIcon != null) {
            this.cameraIcon.setLayoutParams(layoutParamsFinal);
        }
    }

    public void showCamera() {
        float f = 1.0f;
        if (!this.paused && this.mediaEnabled) {
            if (this.cameraView == null) {
                this.cameraView = new CameraView(this.baseFragment.getParentActivity(), this.openWithFrontFaceCamera);
                this.container.addView(this.cameraView, 1, LayoutHelper.createFrame(80, 80.0f));
                this.cameraView.setDelegate(new CLASSNAME());
                if (this.cameraIcon == null) {
                    this.cameraIcon = new FrameLayout(this.baseFragment.getParentActivity());
                    this.cameraImageView = new ImageView(this.baseFragment.getParentActivity());
                    this.cameraImageView.setScaleType(ScaleType.CENTER);
                    this.cameraImageView.setImageResource(CLASSNAMER.drawable.instant_camera);
                    this.cameraImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogCameraIcon), Mode.MULTIPLY));
                    this.cameraIcon.addView(this.cameraImageView, LayoutHelper.createFrame(80, 80, 85));
                }
                this.container.addView(this.cameraIcon, 2, LayoutHelper.createFrame(80, 80.0f));
                this.cameraView.setAlpha(this.mediaEnabled ? 1.0f : 0.2f);
                this.cameraView.setEnabled(this.mediaEnabled);
                FrameLayout frameLayout = this.cameraIcon;
                if (!this.mediaEnabled) {
                    f = 0.2f;
                }
                frameLayout.setAlpha(f);
                this.cameraIcon.setEnabled(this.mediaEnabled);
            }
            this.cameraView.setTranslationX((float) this.cameraViewLocation[0]);
            this.cameraView.setTranslationY((float) this.cameraViewLocation[1]);
            this.cameraIcon.setTranslationX((float) this.cameraViewLocation[0]);
            this.cameraIcon.setTranslationY((float) this.cameraViewLocation[1]);
        }
    }

    public void hideCamera(boolean async) {
        if (this.deviceHasGoodCamera && this.cameraView != null) {
            this.cameraView.destroy(async, null);
            this.container.removeView(this.cameraView);
            this.container.removeView(this.cameraIcon);
            this.cameraView = null;
            this.cameraIcon = null;
            int count = this.attachPhotoRecyclerView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.attachPhotoRecyclerView.getChildAt(a);
                if (child instanceof PhotoAttachCameraCell) {
                    child.setVisibility(0);
                    return;
                }
            }
        }
    }

    private void showHint() {
        if (this.editingMessageObject == null && (this.baseFragment instanceof ChatActivity) && !DataQuery.getInstance(this.currentAccount).inlineBots.isEmpty() && !MessagesController.getGlobalMainSettings().getBoolean("bothint", false)) {
            this.hintShowed = true;
            this.hintTextView.setVisibility(0);
            this.currentHintAnimation = new AnimatorSet();
            this.currentHintAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.hintTextView, "alpha", new float[]{0.0f, 1.0f})});
            this.currentHintAnimation.setInterpolator(this.decelerateInterpolator);
            this.currentHintAnimation.addListener(new CLASSNAME());
            this.currentHintAnimation.setDuration(300);
            this.currentHintAnimation.start();
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.albumsDidLoad) {
            if (this.photoAttachAdapter != null) {
                this.loading = false;
                this.progressView.showTextView();
                this.photoAttachAdapter.notifyDataSetChanged();
                this.cameraAttachAdapter.notifyDataSetChanged();
            }
        } else if (id == NotificationCenter.reloadInlineHints) {
            if (this.adapter != null) {
                this.adapter.notifyDataSetChanged();
            }
        } else if (id == NotificationCenter.cameraInitied) {
            checkCamera(false);
        }
    }

    @SuppressLint({"NewApi"})
    private void updateLayout() {
        RecyclerListView recyclerListView;
        if (this.listView.getChildCount() <= 0) {
            recyclerListView = this.listView;
            int paddingTop = this.listView.getPaddingTop();
            this.scrollOffsetY = paddingTop;
            recyclerListView.setTopGlowOffset(paddingTop);
            this.listView.invalidate();
            return;
        }
        View child = this.listView.getChildAt(0);
        Holder holder = (Holder) this.listView.findContainingViewHolder(child);
        int top = child.getTop();
        int newOffset = 0;
        if (top >= 0 && holder != null && holder.getAdapterPosition() == 0) {
            newOffset = top;
        }
        if (this.scrollOffsetY != newOffset) {
            recyclerListView = this.listView;
            this.scrollOffsetY = newOffset;
            recyclerListView.setTopGlowOffset(newOffset);
            this.listView.invalidate();
        }
    }

    protected boolean canDismissWithSwipe() {
        return false;
    }

    public void updatePhotosButton() {
        int count = selectedPhotos.size();
        if (count == 0) {
            this.sendPhotosButton.imageView.setBackgroundDrawable(Theme.chat_attachButtonDrawables[7]);
            this.sendPhotosButton.textView.setText(TtmlNode.ANONYMOUS_REGION_ID);
            if (this.baseFragment instanceof ChatActivity) {
                this.sendDocumentsButton.textView.setText(LocaleController.getString("ChatDocument", CLASSNAMER.string.ChatDocument));
            }
        } else {
            this.sendPhotosButton.imageView.setBackgroundDrawable(Theme.chat_attachButtonDrawables[8]);
            TextView access$7400;
            Object[] objArr;
            if (this.baseFragment instanceof ChatActivity) {
                access$7400 = this.sendPhotosButton.textView;
                objArr = new Object[1];
                objArr[0] = String.format("(%d)", new Object[]{Integer.valueOf(count)});
                access$7400.setText(LocaleController.formatString("SendItems", CLASSNAMER.string.SendItems, objArr));
                if (this.editingMessageObject == null || !this.editingMessageObject.hasValidGroupId()) {
                    this.sendDocumentsButton.textView.setText(count == 1 ? LocaleController.getString("SendAsFile", CLASSNAMER.string.SendAsFile) : LocaleController.getString("SendAsFiles", CLASSNAMER.string.SendAsFiles));
                }
            } else {
                access$7400 = this.sendPhotosButton.textView;
                objArr = new Object[1];
                objArr[0] = String.format("(%d)", new Object[]{Integer.valueOf(count)});
                access$7400.setText(LocaleController.formatString("UploadItems", CLASSNAMER.string.UploadItems, objArr));
            }
        }
        if (VERSION.SDK_INT < 23 || getContext().checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == 0) {
            this.progressView.setText(LocaleController.getString("NoPhotos", CLASSNAMER.string.NoPhotos));
            this.progressView.setTextSize(20);
            return;
        }
        this.progressView.setText(LocaleController.getString("PermissionStorage", CLASSNAMER.string.PermissionStorage));
        this.progressView.setTextSize(16);
    }

    public void setDelegate(ChatAttachViewDelegate chatAttachViewDelegate) {
        this.delegate = chatAttachViewDelegate;
    }

    public void loadGalleryPhotos() {
        AlbumEntry albumEntry;
        if (this.baseFragment instanceof ChatActivity) {
            albumEntry = MediaController.allMediaAlbumEntry;
        } else {
            albumEntry = MediaController.allPhotosAlbumEntry;
        }
        if (albumEntry == null && VERSION.SDK_INT >= 21) {
            MediaController.loadGalleryPhotosAlbums(0);
        }
    }

    public void init() {
        AlbumEntry albumEntry;
        if (this.baseFragment instanceof ChatActivity) {
            albumEntry = MediaController.allMediaAlbumEntry;
        } else {
            albumEntry = MediaController.allPhotosAlbumEntry;
        }
        if (albumEntry != null) {
            for (int a = 0; a < Math.min(100, albumEntry.photos.size()); a++) {
                ((PhotoEntry) albumEntry.photos.get(a)).reset();
            }
        }
        if (this.currentHintAnimation != null) {
            this.currentHintAnimation.cancel();
            this.currentHintAnimation = null;
        }
        this.hintTextView.setAlpha(0.0f);
        this.hintTextView.setVisibility(4);
        this.attachPhotoLayoutManager.scrollToPositionWithOffset(0, 1000000);
        this.cameraPhotoLayoutManager.scrollToPositionWithOffset(0, 1000000);
        clearSelectedPhotos();
        this.layoutManager.scrollToPositionWithOffset(0, 1000000);
        updatePhotosButton();
    }

    public HashMap<Object, Object> getSelectedPhotos() {
        return selectedPhotos;
    }

    public ArrayList<Object> getSelectedPhotosOrder() {
        return selectedPhotosOrder;
    }

    public void onDestroy() {
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.albumsDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.reloadInlineHints);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.cameraInitied);
        this.baseFragment = null;
    }

    private PhotoAttachPhotoCell getCellForIndex(int index) {
        int count = this.attachPhotoRecyclerView.getChildCount();
        for (int a = 0; a < count; a++) {
            View view = this.attachPhotoRecyclerView.getChildAt(a);
            if (view instanceof PhotoAttachPhotoCell) {
                PhotoAttachPhotoCell cell = (PhotoAttachPhotoCell) view;
                if (((Integer) cell.getImageView().getTag()).intValue() == index) {
                    return cell;
                }
            }
        }
        return null;
    }

    private void onRevealAnimationEnd(boolean open) {
        NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(false);
        this.revealAnimationInProgress = false;
        AlbumEntry albumEntry;
        if (this.baseFragment instanceof ChatActivity) {
            albumEntry = MediaController.allMediaAlbumEntry;
        } else {
            albumEntry = MediaController.allPhotosAlbumEntry;
        }
        if (open && VERSION.SDK_INT <= 19 && albumEntry == null) {
            MediaController.loadGalleryPhotosAlbums(0);
        }
        if (open) {
            checkCamera(true);
            showHint();
        }
    }

    public void checkCamera(boolean request) {
        if (this.baseFragment != null) {
            boolean old = this.deviceHasGoodCamera;
            if (!SharedConfig.inappCamera) {
                this.deviceHasGoodCamera = false;
            } else if (VERSION.SDK_INT < 23) {
                if (request || SharedConfig.hasCameraCache) {
                    CameraController.getInstance().initCamera(null);
                }
                this.deviceHasGoodCamera = CameraController.getInstance().isCameraInitied();
            } else if (this.baseFragment.getParentActivity().checkSelfPermission("android.permission.CAMERA") != 0) {
                if (request) {
                    try {
                        this.baseFragment.getParentActivity().requestPermissions(new String[]{"android.permission.CAMERA"}, 17);
                    } catch (Exception e) {
                    }
                }
                this.deviceHasGoodCamera = false;
            } else {
                if (request || SharedConfig.hasCameraCache) {
                    CameraController.getInstance().initCamera(null);
                }
                this.deviceHasGoodCamera = CameraController.getInstance().isCameraInitied();
            }
            if (!(old == this.deviceHasGoodCamera || this.photoAttachAdapter == null)) {
                this.photoAttachAdapter.notifyDataSetChanged();
            }
            if (isShowing() && this.deviceHasGoodCamera && this.baseFragment != null && this.backDrawable.getAlpha() != 0 && !this.revealAnimationInProgress && !this.cameraOpened) {
                showCamera();
            }
        }
    }

    public void onOpenAnimationEnd() {
        onRevealAnimationEnd(true);
    }

    public void onOpenAnimationStart() {
    }

    public boolean canDismiss() {
        return true;
    }

    public void setAllowDrawContent(boolean value) {
        super.setAllowDrawContent(value);
        checkCameraViewPosition();
    }

    public void setMaxSelectedPhotos(int value) {
        this.maxSelectedPhotos = value;
    }

    public void setOpenWithFrontFaceCamera(boolean value) {
        this.openWithFrontFaceCamera = value;
    }

    private int addToSelectedPhotos(PhotoEntry object, int index) {
        Integer key = Integer.valueOf(object.imageId);
        if (selectedPhotos.containsKey(key)) {
            selectedPhotos.remove(key);
            int position = selectedPhotosOrder.indexOf(key);
            if (position >= 0) {
                selectedPhotosOrder.remove(position);
            }
            updatePhotosCounter();
            updateCheckedPhotoIndices();
            if (index < 0) {
                return position;
            }
            object.reset();
            this.photoViewerProvider.updatePhotoAtIndex(index);
            return position;
        }
        selectedPhotos.put(key, object);
        selectedPhotosOrder.add(key);
        updatePhotosCounter();
        return -1;
    }

    private void clearSelectedPhotos() {
        boolean changed = false;
        if (!selectedPhotos.isEmpty()) {
            for (Entry<Object, Object> entry : selectedPhotos.entrySet()) {
                ((PhotoEntry) entry.getValue()).reset();
            }
            selectedPhotos.clear();
            selectedPhotosOrder.clear();
            updatePhotosButton();
            changed = true;
        }
        if (!cameraPhotos.isEmpty()) {
            int size = cameraPhotos.size();
            for (int a = 0; a < size; a++) {
                PhotoEntry photoEntry = (PhotoEntry) cameraPhotos.get(a);
                new File(photoEntry.path).delete();
                if (photoEntry.imagePath != null) {
                    new File(photoEntry.imagePath).delete();
                }
                if (photoEntry.thumbPath != null) {
                    new File(photoEntry.thumbPath).delete();
                }
            }
            cameraPhotos.clear();
            changed = true;
        }
        if (changed) {
            this.photoAttachAdapter.notifyDataSetChanged();
            this.cameraAttachAdapter.notifyDataSetChanged();
        }
    }

    private void setUseRevealAnimation(boolean value) {
        if (!value || (value && VERSION.SDK_INT >= 18 && !AndroidUtilities.isTablet() && AndroidUtilities.shouldEnableAnimation() && (this.baseFragment instanceof ChatActivity))) {
            this.useRevealAnimation = value;
        }
    }

    @Keep
    @SuppressLint({"NewApi"})
    protected void setRevealRadius(float radius) {
        this.revealRadius = radius;
        if (VERSION.SDK_INT <= 19) {
            this.listView.invalidate();
        }
        if (!isDismissed()) {
            int a = 0;
            while (a < this.innerAnimators.size()) {
                InnerAnimator innerAnimator = (InnerAnimator) this.innerAnimators.get(a);
                if (innerAnimator.startRadius <= radius) {
                    innerAnimator.animatorSet.start();
                    this.innerAnimators.remove(a);
                    a--;
                }
                a++;
            }
        }
    }

    @Keep
    protected float getRevealRadius() {
        return this.revealRadius;
    }

    @SuppressLint({"NewApi"})
    private void startRevealAnimation(boolean open) {
        int a;
        this.containerView.setTranslationY(0.0f);
        final AnimatorSet animatorSet = new AnimatorSet();
        View view = this.delegate.getRevealView();
        if (view.getVisibility() == 0 && ((ViewGroup) view.getParent()).getVisibility() == 0) {
            float top;
            int[] coords = new int[2];
            view.getLocationInWindow(coords);
            if (VERSION.SDK_INT <= 19) {
                top = (float) ((AndroidUtilities.displaySize.y - this.containerView.getMeasuredHeight()) - AndroidUtilities.statusBarHeight);
            } else {
                top = this.containerView.getY();
            }
            this.revealX = coords[0] + (view.getMeasuredWidth() / 2);
            this.revealY = (int) (((float) (coords[1] + (view.getMeasuredHeight() / 2))) - top);
            if (VERSION.SDK_INT <= 19) {
                this.revealY -= AndroidUtilities.statusBarHeight;
            }
        } else {
            this.revealX = (AndroidUtilities.displaySize.x / 2) + backgroundPaddingLeft;
            this.revealY = (int) (((float) AndroidUtilities.displaySize.y) - this.containerView.getY());
        }
        corners = new int[4][];
        int[] iArr = new int[2];
        corners[0] = new int[]{0, 0};
        corners[1] = new int[]{0, AndroidUtilities.m9dp(304.0f)};
        corners[2] = new int[]{this.containerView.getMeasuredWidth(), 0};
        corners[3] = new int[]{this.containerView.getMeasuredWidth(), AndroidUtilities.m9dp(304.0f)};
        int finalRevealRadius = 0;
        int y = (this.revealY - this.scrollOffsetY) + backgroundPaddingTop;
        for (a = 0; a < 4; a++) {
            finalRevealRadius = Math.max(finalRevealRadius, (int) Math.ceil(Math.sqrt((double) (((this.revealX - corners[a][0]) * (this.revealX - corners[a][0])) + ((y - corners[a][1]) * (y - corners[a][1]))))));
        }
        int finalRevealX = this.revealX <= this.containerView.getMeasuredWidth() ? this.revealX : this.containerView.getMeasuredWidth();
        ArrayList<Animator> animators = new ArrayList(3);
        String str = "revealRadius";
        float[] fArr = new float[2];
        fArr[0] = open ? 0.0f : (float) finalRevealRadius;
        fArr[1] = open ? (float) finalRevealRadius : 0.0f;
        animators.add(ObjectAnimator.ofFloat(this, str, fArr));
        ColorDrawable colorDrawable = this.backDrawable;
        String str2 = "alpha";
        int[] iArr2 = new int[1];
        iArr2[0] = open ? 51 : 0;
        animators.add(ObjectAnimator.ofInt(colorDrawable, str2, iArr2));
        if (VERSION.SDK_INT >= 21) {
            try {
                animators.add(ViewAnimationUtils.createCircularReveal(this.containerView, finalRevealX, this.revealY, open ? 0.0f : (float) finalRevealRadius, open ? (float) finalRevealRadius : 0.0f));
            } catch (Throwable e) {
                FileLog.m13e(e);
            }
            animatorSet.setDuration(320);
        } else if (open) {
            animatorSet.setDuration(250);
            this.containerView.setScaleX(1.0f);
            this.containerView.setScaleY(1.0f);
            this.containerView.setAlpha(1.0f);
            if (VERSION.SDK_INT <= 19) {
                animatorSet.setStartDelay(20);
            }
        } else {
            animatorSet.setDuration(200);
            this.containerView.setPivotX(this.revealX <= this.containerView.getMeasuredWidth() ? (float) this.revealX : (float) this.containerView.getMeasuredWidth());
            this.containerView.setPivotY((float) this.revealY);
            animators.add(ObjectAnimator.ofFloat(this.containerView, "scaleX", new float[]{0.0f}));
            animators.add(ObjectAnimator.ofFloat(this.containerView, "scaleY", new float[]{0.0f}));
            animators.add(ObjectAnimator.ofFloat(this.containerView, "alpha", new float[]{0.0f}));
        }
        animatorSet.playTogether(animators);
        final boolean z = open;
        animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                if (ChatAttachAlert.this.currentSheetAnimation != null && ChatAttachAlert.this.currentSheetAnimation.equals(animation)) {
                    ChatAttachAlert.this.currentSheetAnimation = null;
                    ChatAttachAlert.this.onRevealAnimationEnd(z);
                    ChatAttachAlert.this.containerView.invalidate();
                    ChatAttachAlert.this.containerView.setLayerType(0, null);
                    if (!z) {
                        try {
                            ChatAttachAlert.this.dismissInternal();
                        } catch (Throwable e) {
                            FileLog.m13e(e);
                        }
                    }
                }
            }

            public void onAnimationCancel(Animator animation) {
                if (ChatAttachAlert.this.currentSheetAnimation != null && animatorSet.equals(animation)) {
                    ChatAttachAlert.this.currentSheetAnimation = null;
                }
            }
        });
        if (open) {
            this.innerAnimators.clear();
            NotificationCenter.getInstance(this.currentAccount).setAllowedNotificationsDutingAnimation(new int[]{NotificationCenter.dialogsNeedReload});
            NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(true);
            this.revealAnimationInProgress = true;
            int count = VERSION.SDK_INT <= 19 ? 12 : 8;
            for (a = 0; a < count; a++) {
                if (this.views[a] != null) {
                    AnimatorSet animatorSetInner;
                    if (VERSION.SDK_INT <= 19) {
                        if (a < 8) {
                            this.views[a].setScaleX(0.1f);
                            this.views[a].setScaleY(0.1f);
                        }
                        this.views[a].setAlpha(0.0f);
                    } else {
                        this.views[a].setScaleX(0.7f);
                        this.views[a].setScaleY(0.7f);
                    }
                    InnerAnimator innerAnimator = new InnerAnimator(this, null);
                    int buttonX = this.views[a].getLeft() + (this.views[a].getMeasuredWidth() / 2);
                    int buttonY = (this.views[a].getTop() + this.attachView.getTop()) + (this.views[a].getMeasuredHeight() / 2);
                    float dist = (float) Math.sqrt((double) (((this.revealX - buttonX) * (this.revealX - buttonX)) + ((this.revealY - buttonY) * (this.revealY - buttonY))));
                    float vecY = ((float) (this.revealY - buttonY)) / dist;
                    this.views[a].setPivotX(((float) (this.views[a].getMeasuredWidth() / 2)) + (((float) AndroidUtilities.m9dp(20.0f)) * (((float) (this.revealX - buttonX)) / dist)));
                    this.views[a].setPivotY(((float) (this.views[a].getMeasuredHeight() / 2)) + (((float) AndroidUtilities.m9dp(20.0f)) * vecY));
                    innerAnimator.startRadius = dist - ((float) AndroidUtilities.m9dp(81.0f));
                    this.views[a].setTag(CLASSNAMER.string.AppName, Integer.valueOf(1));
                    animators = new ArrayList();
                    if (a < 8) {
                        fArr = new float[2];
                        animators.add(ObjectAnimator.ofFloat(this.views[a], "scaleX", new float[]{0.7f, 1.05f}));
                        fArr = new float[2];
                        animators.add(ObjectAnimator.ofFloat(this.views[a], "scaleY", new float[]{0.7f, 1.05f}));
                        animatorSetInner = new AnimatorSet();
                        r25 = new Animator[2];
                        r25[0] = ObjectAnimator.ofFloat(this.views[a], "scaleX", new float[]{1.0f});
                        r25[1] = ObjectAnimator.ofFloat(this.views[a], "scaleY", new float[]{1.0f});
                        animatorSetInner.playTogether(r25);
                        animatorSetInner.setDuration(100);
                        animatorSetInner.setInterpolator(this.decelerateInterpolator);
                    } else {
                        animatorSetInner = null;
                    }
                    if (VERSION.SDK_INT <= 19) {
                        animators.add(ObjectAnimator.ofFloat(this.views[a], "alpha", new float[]{1.0f}));
                    }
                    innerAnimator.animatorSet = new AnimatorSet();
                    innerAnimator.animatorSet.playTogether(animators);
                    innerAnimator.animatorSet.setDuration(150);
                    innerAnimator.animatorSet.setInterpolator(this.decelerateInterpolator);
                    innerAnimator.animatorSet.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            if (animatorSetInner != null) {
                                animatorSetInner.start();
                            }
                        }
                    });
                    this.innerAnimators.add(innerAnimator);
                }
            }
        }
        this.currentSheetAnimation = animatorSet;
        animatorSet.start();
    }

    public void dismissInternal() {
        if (this.containerView != null) {
            this.containerView.setVisibility(4);
        }
        super.dismissInternal();
    }

    protected boolean onCustomOpenAnimation() {
        float f = 1.0f;
        if (this.useRevealAnimation) {
            setUseRevealAnimation(true);
        }
        if (this.baseFragment instanceof ChatActivity) {
            Chat chat = ((ChatActivity) this.baseFragment).getCurrentChat();
            if (ChatObject.isChannel(chat)) {
                boolean z;
                float f2;
                int i;
                if (chat.banned_rights == null || !chat.banned_rights.send_media) {
                    z = true;
                } else {
                    z = false;
                }
                this.mediaEnabled = z;
                for (int a = 0; a < 5; a++) {
                    if (a <= 2 || this.editingMessageObject == null || !this.editingMessageObject.hasValidGroupId()) {
                        float f3;
                        AttachButton attachButton = (AttachButton) this.attachButtons.get(a);
                        if (this.mediaEnabled) {
                            f3 = 1.0f;
                        } else {
                            f3 = 0.2f;
                        }
                        attachButton.setAlpha(f3);
                        ((AttachButton) this.attachButtons.get(a)).setEnabled(this.mediaEnabled);
                    } else {
                        ((AttachButton) this.attachButtons.get(a + 3)).setEnabled(false);
                        ((AttachButton) this.attachButtons.get(a + 3)).setAlpha(0.2f);
                    }
                }
                RecyclerListView recyclerListView = this.attachPhotoRecyclerView;
                if (this.mediaEnabled) {
                    f2 = 1.0f;
                } else {
                    f2 = 0.2f;
                }
                recyclerListView.setAlpha(f2);
                this.attachPhotoRecyclerView.setEnabled(this.mediaEnabled);
                if (!this.mediaEnabled) {
                    if (AndroidUtilities.isBannedForever(chat.banned_rights.until_date)) {
                        this.mediaBanTooltip.setText(LocaleController.formatString("AttachMediaRestrictedForever", CLASSNAMER.string.AttachMediaRestrictedForever, new Object[0]));
                    } else {
                        this.mediaBanTooltip.setText(LocaleController.formatString("AttachMediaRestricted", CLASSNAMER.string.AttachMediaRestricted, LocaleController.formatDateForBan((long) chat.banned_rights.until_date)));
                    }
                }
                CorrectlyMeasuringTextView correctlyMeasuringTextView = this.mediaBanTooltip;
                if (this.mediaEnabled) {
                    i = 4;
                } else {
                    i = 0;
                }
                correctlyMeasuringTextView.setVisibility(i);
                if (this.cameraView != null) {
                    CameraView cameraView = this.cameraView;
                    if (this.mediaEnabled) {
                        f2 = 1.0f;
                    } else {
                        f2 = 0.2f;
                    }
                    cameraView.setAlpha(f2);
                    this.cameraView.setEnabled(this.mediaEnabled);
                }
                if (this.cameraIcon != null) {
                    FrameLayout frameLayout = this.cameraIcon;
                    if (!this.mediaEnabled) {
                        f = 0.2f;
                    }
                    frameLayout.setAlpha(f);
                    this.cameraIcon.setEnabled(this.mediaEnabled);
                }
            }
            updatePollMusicButton();
        }
        if (!this.useRevealAnimation) {
            return false;
        }
        startRevealAnimation(true);
        return true;
    }

    protected boolean onCustomCloseAnimation() {
        if (this.useRevealAnimation) {
            setUseRevealAnimation(true);
        }
        if (!this.useRevealAnimation) {
            return false;
        }
        this.backDrawable.setAlpha(51);
        startRevealAnimation(false);
        return true;
    }

    public void dismissWithButtonClick(int item) {
        super.dismissWithButtonClick(item);
        boolean z = (item == 0 || item == 2) ? false : true;
        hideCamera(z);
    }

    protected boolean canDismissWithTouchOutside() {
        return !this.cameraOpened;
    }

    public void dismiss() {
        if (!this.cameraAnimationInProgress) {
            if (this.cameraOpened) {
                closeCamera(true);
                return;
            }
            hideCamera(true);
            super.lambda$new$4$EmbedBottomSheet();
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (!this.cameraOpened || (keyCode != 24 && keyCode != 25)) {
            return super.onKeyDown(keyCode, event);
        }
        this.shutterButton.getDelegate().shutterReleased();
        return true;
    }
}
