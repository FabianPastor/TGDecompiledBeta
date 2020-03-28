package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.os.Build;
import android.provider.Settings;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Property;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Keep;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.messenger.camera.CameraController;
import org.telegram.messenger.camera.CameraView;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$FileLocation;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.PhotoAttachCameraCell;
import org.telegram.ui.Cells.PhotoAttachPermissionCell;
import org.telegram.ui.Cells.PhotoAttachPhotoCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.ChatAttachAlert;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ShutterButton;
import org.telegram.ui.PhotoViewer;

public class ChatAttachAlert extends BottomSheet implements NotificationCenter.NotificationCenterDelegate, BottomSheet.BottomSheetDelegateInterface {
    /* access modifiers changed from: private */
    public static ArrayList<Object> cameraPhotos = new ArrayList<>();
    private static int lastImageId = -1;
    /* access modifiers changed from: private */
    public static boolean mediaFromExternalCamera;
    /* access modifiers changed from: private */
    public static HashMap<Object, Object> selectedPhotos = new HashMap<>();
    /* access modifiers changed from: private */
    public static ArrayList<Object> selectedPhotosOrder = new ArrayList<>();
    private final Property<ChatAttachAlert, Float> ATTACH_ALERT_PROGRESS;
    /* access modifiers changed from: private */
    public ActionBar actionBar;
    /* access modifiers changed from: private */
    public AnimatorSet actionBarAnimation;
    /* access modifiers changed from: private */
    public View actionBarShadow;
    /* access modifiers changed from: private */
    public PhotoAttachAdapter adapter;
    /* access modifiers changed from: private */
    public int alertOnlyOnce;
    /* access modifiers changed from: private */
    public boolean allowOrder = true;
    private int[] animateCameraValues = new int[5];
    /* access modifiers changed from: private */
    public AnimatorSet animatorSet;
    /* access modifiers changed from: private */
    public int attachItemSize;
    /* access modifiers changed from: private */
    public BaseFragment baseFragment;
    private boolean buttonPressed;
    /* access modifiers changed from: private */
    public ButtonsAdapter buttonsAdapter;
    private LinearLayoutManager buttonsLayoutManager;
    /* access modifiers changed from: private */
    public RecyclerListView buttonsRecyclerView;
    /* access modifiers changed from: private */
    public boolean cameraAnimationInProgress;
    /* access modifiers changed from: private */
    public PhotoAttachAdapter cameraAttachAdapter;
    /* access modifiers changed from: private */
    public Drawable cameraDrawable;
    /* access modifiers changed from: private */
    public FrameLayout cameraIcon;
    /* access modifiers changed from: private */
    public AnimatorSet cameraInitAnimation;
    /* access modifiers changed from: private */
    public float cameraOpenProgress;
    /* access modifiers changed from: private */
    public boolean cameraOpened;
    /* access modifiers changed from: private */
    public FrameLayout cameraPanel;
    /* access modifiers changed from: private */
    public LinearLayoutManager cameraPhotoLayoutManager;
    /* access modifiers changed from: private */
    public RecyclerListView cameraPhotoRecyclerView;
    /* access modifiers changed from: private */
    public boolean cameraPhotoRecyclerViewIgnoreLayout;
    /* access modifiers changed from: private */
    public CameraView cameraView;
    private int[] cameraViewLocation = new int[2];
    private int cameraViewOffsetBottomY;
    private int cameraViewOffsetX;
    /* access modifiers changed from: private */
    public int cameraViewOffsetY;
    /* access modifiers changed from: private */
    public float cameraZoom;
    /* access modifiers changed from: private */
    public boolean canSaveCameraPreview;
    /* access modifiers changed from: private */
    public boolean cancelTakingPhotos;
    /* access modifiers changed from: private */
    public EditTextEmoji commentTextView;
    /* access modifiers changed from: private */
    public float cornerRadius = 1.0f;
    /* access modifiers changed from: private */
    public TextView counterTextView;
    /* access modifiers changed from: private */
    public int currentAccount = UserConfig.selectedAccount;
    /* access modifiers changed from: private */
    public int currentPanTranslationY;
    private int currentSelectedCount;
    /* access modifiers changed from: private */
    public ChatAttachViewDelegate delegate;
    private boolean deviceHasGoodCamera;
    private boolean dragging;
    /* access modifiers changed from: private */
    public TextView dropDown;
    /* access modifiers changed from: private */
    public ArrayList<MediaController.AlbumEntry> dropDownAlbums;
    private ActionBarMenuItem dropDownContainer;
    private Drawable dropDownDrawable;
    /* access modifiers changed from: private */
    public MessageObject editingMessageObject;
    /* access modifiers changed from: private */
    public boolean enterCommentEventSent;
    /* access modifiers changed from: private */
    public boolean flashAnimationInProgress;
    /* access modifiers changed from: private */
    public ImageView[] flashModeButton = new ImageView[2];
    /* access modifiers changed from: private */
    public FrameLayout frameLayout2;
    /* access modifiers changed from: private */
    public MediaController.AlbumEntry galleryAlbumEntry;
    /* access modifiers changed from: private */
    public int gridExtraSpace;
    /* access modifiers changed from: private */
    public RecyclerListView gridView;
    private Rect hitRect = new Rect();
    private DecelerateInterpolator interpolator = new DecelerateInterpolator(1.5f);
    private ActionBarMenuSubItem[] itemCells;
    private RecyclerViewItemRangeSelector itemRangeSelector;
    /* access modifiers changed from: private */
    public int itemSize;
    /* access modifiers changed from: private */
    public int itemsPerRow;
    /* access modifiers changed from: private */
    public int lastItemSize;
    private float lastY;
    /* access modifiers changed from: private */
    public GridLayoutManager layoutManager;
    private boolean loading;
    /* access modifiers changed from: private */
    public int maxSelectedPhotos = -1;
    private boolean maybeStartDraging;
    /* access modifiers changed from: private */
    public boolean mediaEnabled = true;
    /* access modifiers changed from: private */
    public AnimatorSet menuAnimator;
    /* access modifiers changed from: private */
    public boolean menuShowed;
    /* access modifiers changed from: private */
    public boolean noCameraPermissions;
    /* access modifiers changed from: private */
    public boolean noGalleryPermissions;
    private boolean openWithFrontFaceCamera;
    /* access modifiers changed from: private */
    public Paint paint = new Paint(1);
    private boolean paused;
    private PhotoViewer.PhotoViewerProvider photoViewerProvider;
    private float pinchStartDistance;
    /* access modifiers changed from: private */
    public boolean pollsEnabled = true;
    private boolean pressed;
    /* access modifiers changed from: private */
    public EmptyTextProgressView progressView;
    /* access modifiers changed from: private */
    public TextView recordTime;
    /* access modifiers changed from: private */
    public RectF rect = new RectF();
    /* access modifiers changed from: private */
    public boolean requestingPermissions;
    /* access modifiers changed from: private */
    public int scrollOffsetY;
    /* access modifiers changed from: private */
    public MediaController.AlbumEntry selectedAlbumEntry;
    private View selectedCountView;
    /* access modifiers changed from: private */
    public ActionBarMenuItem selectedMenuItem;
    /* access modifiers changed from: private */
    public TextView selectedTextView;
    private ActionBarPopupWindow.ActionBarPopupWindowLayout sendPopupLayout;
    /* access modifiers changed from: private */
    public ActionBarPopupWindow sendPopupWindow;
    /* access modifiers changed from: private */
    public View shadow;
    /* access modifiers changed from: private */
    public boolean shouldSelect;
    /* access modifiers changed from: private */
    public ShutterButton shutterButton;
    private SizeNotifierFrameLayout sizeNotifierFrameLayout;
    /* access modifiers changed from: private */
    public ImageView switchCameraButton;
    /* access modifiers changed from: private */
    public boolean takingPhoto;
    /* access modifiers changed from: private */
    public TextPaint textPaint = new TextPaint(1);
    /* access modifiers changed from: private */
    public TextView tooltipTextView;
    /* access modifiers changed from: private */
    public Runnable videoRecordRunnable;
    /* access modifiers changed from: private */
    public int videoRecordTime;
    private int[] viewPosition = new int[2];
    private ImageView writeButton;
    /* access modifiers changed from: private */
    public FrameLayout writeButtonContainer;
    private Drawable writeButtonDrawable;
    /* access modifiers changed from: private */
    public AnimatorSet zoomControlAnimation;
    private Runnable zoomControlHideRunnable;
    /* access modifiers changed from: private */
    public ZoomControlView zoomControlView;
    private boolean zoomWas;
    private boolean zooming;

    public interface ChatAttachViewDelegate {
        void didPressedButton(int i, boolean z, boolean z2, int i2);

        void didSelectBot(TLRPC$User tLRPC$User);

        void needEnterComment();

        void onCameraOpened();
    }

    static /* synthetic */ boolean lambda$new$8(View view, MotionEvent motionEvent) {
        return true;
    }

    public boolean canDismiss() {
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean canDismissWithSwipe() {
        return false;
    }

    static /* synthetic */ int access$8908(ChatAttachAlert chatAttachAlert) {
        int i = chatAttachAlert.videoRecordTime;
        chatAttachAlert.videoRecordTime = i + 1;
        return i;
    }

    static /* synthetic */ int access$9710() {
        int i = lastImageId;
        lastImageId = i - 1;
        return i;
    }

    private class BasePhotoProvider extends PhotoViewer.EmptyPhotoViewerProvider {
        private BasePhotoProvider() {
        }

        public boolean isPhotoChecked(int i) {
            MediaController.PhotoEntry access$000 = ChatAttachAlert.this.getPhotoEntryAtPosition(i);
            return access$000 != null && ChatAttachAlert.selectedPhotos.containsKey(Integer.valueOf(access$000.imageId));
        }

        public int setPhotoChecked(int i, VideoEditedInfo videoEditedInfo) {
            MediaController.PhotoEntry access$000;
            boolean z;
            if ((ChatAttachAlert.this.maxSelectedPhotos >= 0 && ChatAttachAlert.selectedPhotos.size() >= ChatAttachAlert.this.maxSelectedPhotos && !isPhotoChecked(i)) || (access$000 = ChatAttachAlert.this.getPhotoEntryAtPosition(i)) == null) {
                return -1;
            }
            int access$300 = ChatAttachAlert.this.addToSelectedPhotos(access$000, -1);
            int i2 = 1;
            if (access$300 == -1) {
                access$300 = ChatAttachAlert.selectedPhotosOrder.indexOf(Integer.valueOf(access$000.imageId));
                z = true;
            } else {
                access$000.editedInfo = null;
                z = false;
            }
            access$000.editedInfo = videoEditedInfo;
            int childCount = ChatAttachAlert.this.gridView.getChildCount();
            int i3 = 0;
            while (true) {
                if (i3 >= childCount) {
                    break;
                }
                View childAt = ChatAttachAlert.this.gridView.getChildAt(i3);
                if (!(childAt instanceof PhotoAttachPhotoCell) || ((Integer) childAt.getTag()).intValue() != i) {
                    i3++;
                } else if (!(ChatAttachAlert.this.baseFragment instanceof ChatActivity) || !ChatAttachAlert.this.allowOrder) {
                    ((PhotoAttachPhotoCell) childAt).setChecked(-1, z, false);
                } else {
                    ((PhotoAttachPhotoCell) childAt).setChecked(access$300, z, false);
                }
            }
            int childCount2 = ChatAttachAlert.this.cameraPhotoRecyclerView.getChildCount();
            int i4 = 0;
            while (true) {
                if (i4 >= childCount2) {
                    break;
                }
                View childAt2 = ChatAttachAlert.this.cameraPhotoRecyclerView.getChildAt(i4);
                if (!(childAt2 instanceof PhotoAttachPhotoCell) || ((Integer) childAt2.getTag()).intValue() != i) {
                    i4++;
                } else if (!(ChatAttachAlert.this.baseFragment instanceof ChatActivity) || !ChatAttachAlert.this.allowOrder) {
                    ((PhotoAttachPhotoCell) childAt2).setChecked(-1, z, false);
                } else {
                    ((PhotoAttachPhotoCell) childAt2).setChecked(access$300, z, false);
                }
            }
            ChatAttachAlert chatAttachAlert = ChatAttachAlert.this;
            if (!z) {
                i2 = 2;
            }
            chatAttachAlert.updatePhotosButton(i2);
            return access$300;
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
    }

    private void updateCheckedPhotoIndices() {
        if (this.baseFragment instanceof ChatActivity) {
            int childCount = this.gridView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = this.gridView.getChildAt(i);
                if (childAt instanceof PhotoAttachPhotoCell) {
                    PhotoAttachPhotoCell photoAttachPhotoCell = (PhotoAttachPhotoCell) childAt;
                    MediaController.PhotoEntry photoEntryAtPosition = getPhotoEntryAtPosition(((Integer) photoAttachPhotoCell.getTag()).intValue());
                    if (photoEntryAtPosition != null) {
                        photoAttachPhotoCell.setNum(selectedPhotosOrder.indexOf(Integer.valueOf(photoEntryAtPosition.imageId)));
                    }
                }
            }
            int childCount2 = this.cameraPhotoRecyclerView.getChildCount();
            for (int i2 = 0; i2 < childCount2; i2++) {
                View childAt2 = this.cameraPhotoRecyclerView.getChildAt(i2);
                if (childAt2 instanceof PhotoAttachPhotoCell) {
                    PhotoAttachPhotoCell photoAttachPhotoCell2 = (PhotoAttachPhotoCell) childAt2;
                    MediaController.PhotoEntry photoEntryAtPosition2 = getPhotoEntryAtPosition(((Integer) photoAttachPhotoCell2.getTag()).intValue());
                    if (photoEntryAtPosition2 != null) {
                        photoAttachPhotoCell2.setNum(selectedPhotosOrder.indexOf(Integer.valueOf(photoEntryAtPosition2.imageId)));
                    }
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public MediaController.PhotoEntry getPhotoEntryAtPosition(int i) {
        if (i < 0) {
            return null;
        }
        int size = cameraPhotos.size();
        if (i < size) {
            return (MediaController.PhotoEntry) cameraPhotos.get(i);
        }
        int i2 = i - size;
        if (i2 < this.selectedAlbumEntry.photos.size()) {
            return this.selectedAlbumEntry.photos.get(i2);
        }
        return null;
    }

    private ArrayList<Object> getAllPhotosArray() {
        if (this.selectedAlbumEntry != null) {
            if (cameraPhotos.isEmpty()) {
                return this.selectedAlbumEntry.photos;
            }
            ArrayList<Object> arrayList = new ArrayList<>(this.selectedAlbumEntry.photos.size() + cameraPhotos.size());
            arrayList.addAll(cameraPhotos);
            arrayList.addAll(this.selectedAlbumEntry.photos);
            return arrayList;
        } else if (!cameraPhotos.isEmpty()) {
            return cameraPhotos;
        } else {
            return new ArrayList<>(0);
        }
    }

    private class AttachButton extends FrameLayout {
        /* access modifiers changed from: private */
        public ImageView imageView;
        /* access modifiers changed from: private */
        public TextView textView;

        public boolean hasOverlappingRendering() {
            return false;
        }

        public AttachButton(Context context) {
            super(context);
            ImageView imageView2 = new ImageView(context);
            this.imageView = imageView2;
            imageView2.setScaleType(ImageView.ScaleType.CENTER);
            if (Build.VERSION.SDK_INT >= 21) {
                this.imageView.setImageDrawable(Theme.createSelectorDrawable(Theme.getColor("dialogButtonSelector"), 1, AndroidUtilities.dp(25.0f)));
            }
            addView(this.imageView, LayoutHelper.createFrame(50, 50.0f, 49, 0.0f, 12.0f, 0.0f, 0.0f));
            TextView textView2 = new TextView(context);
            this.textView = textView2;
            textView2.setMaxLines(2);
            this.textView.setGravity(1);
            this.textView.setEllipsize(TextUtils.TruncateAt.END);
            this.textView.setTextColor(Theme.getColor("dialogTextGray2"));
            this.textView.setTextSize(1, 12.0f);
            this.textView.setLineSpacing((float) (-AndroidUtilities.dp(2.0f)), 1.0f);
            addView(this.textView, LayoutHelper.createFrame(-1, -2.0f, 51, 0.0f, 66.0f, 0.0f, 0.0f));
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(ChatAttachAlert.this.attachItemSize, NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(92.0f), NUM));
        }

        public void setTextAndIcon(CharSequence charSequence, Drawable drawable) {
            this.textView.setText(charSequence);
            this.imageView.setBackgroundDrawable(drawable);
        }
    }

    private class AttachBotButton extends FrameLayout {
        private AvatarDrawable avatarDrawable = new AvatarDrawable();
        /* access modifiers changed from: private */
        public TLRPC$User currentUser;
        /* access modifiers changed from: private */
        public BackupImageView imageView;
        /* access modifiers changed from: private */
        public TextView nameTextView;

        public AttachBotButton(Context context) {
            super(context);
            BackupImageView backupImageView = new BackupImageView(context);
            this.imageView = backupImageView;
            backupImageView.setRoundRadius(AndroidUtilities.dp(25.0f));
            addView(this.imageView, LayoutHelper.createFrame(50, 50.0f, 49, 0.0f, 12.0f, 0.0f, 0.0f));
            if (Build.VERSION.SDK_INT >= 21) {
                View view = new View(context);
                view.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("dialogButtonSelector"), 1, AndroidUtilities.dp(25.0f)));
                addView(view, LayoutHelper.createFrame(50, 50.0f, 49, 0.0f, 12.0f, 0.0f, 0.0f));
            }
            TextView textView = new TextView(context);
            this.nameTextView = textView;
            textView.setTextSize(1, 12.0f);
            this.nameTextView.setGravity(49);
            this.nameTextView.setLines(1);
            this.nameTextView.setSingleLine(true);
            this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
            addView(this.nameTextView, LayoutHelper.createFrame(-1, -2.0f, 51, 6.0f, 66.0f, 6.0f, 0.0f));
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(ChatAttachAlert.this.attachItemSize, NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(100.0f), NUM));
        }

        public void setUser(TLRPC$User tLRPC$User) {
            if (tLRPC$User != null) {
                this.nameTextView.setTextColor(Theme.getColor("dialogTextGray2"));
                this.currentUser = tLRPC$User;
                this.nameTextView.setText(ContactsController.formatName(tLRPC$User.first_name, tLRPC$User.last_name));
                this.avatarDrawable.setInfo(tLRPC$User);
                this.imageView.setImage(ImageLocation.getForUser(tLRPC$User, false), "50_50", (Drawable) this.avatarDrawable, (Object) tLRPC$User);
                requestLayout();
            }
        }
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public ChatAttachAlert(android.content.Context r40, org.telegram.ui.ActionBar.BaseFragment r41) {
        /*
            r39 = this;
            r6 = r39
            r7 = r40
            r8 = r41
            r9 = 0
            r6.<init>(r7, r9)
            android.text.TextPaint r0 = new android.text.TextPaint
            r10 = 1
            r0.<init>(r10)
            r6.textPaint = r0
            android.graphics.RectF r0 = new android.graphics.RectF
            r0.<init>()
            r6.rect = r0
            android.graphics.Paint r0 = new android.graphics.Paint
            r0.<init>(r10)
            r6.paint = r0
            r11 = 1065353216(0x3var_, float:1.0)
            r6.cornerRadius = r11
            int r0 = org.telegram.messenger.UserConfig.selectedAccount
            r6.currentAccount = r0
            r6.mediaEnabled = r10
            r6.pollsEnabled = r10
            r12 = 2
            android.widget.ImageView[] r0 = new android.widget.ImageView[r12]
            r6.flashModeButton = r0
            int[] r0 = new int[r12]
            r6.cameraViewLocation = r0
            int[] r0 = new int[r12]
            r6.viewPosition = r0
            r0 = 5
            int[] r0 = new int[r0]
            r6.animateCameraValues = r0
            android.view.animation.DecelerateInterpolator r0 = new android.view.animation.DecelerateInterpolator
            r1 = 1069547520(0x3fCLASSNAME, float:1.5)
            r0.<init>(r1)
            r6.interpolator = r0
            r13 = -1
            r6.maxSelectedPhotos = r13
            r6.allowOrder = r10
            android.graphics.Rect r0 = new android.graphics.Rect
            r0.<init>()
            r6.hitRect = r0
            r14 = 1117782016(0x42a00000, float:80.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r14)
            r6.itemSize = r0
            r6.lastItemSize = r0
            r0 = 1118437376(0x42aa0000, float:85.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r6.attachItemSize = r0
            r15 = 3
            r6.itemsPerRow = r15
            android.view.animation.DecelerateInterpolator r0 = new android.view.animation.DecelerateInterpolator
            r0.<init>()
            r6.loading = r10
            org.telegram.ui.Components.ChatAttachAlert$1 r0 = new org.telegram.ui.Components.ChatAttachAlert$1
            r0.<init>()
            r6.photoViewerProvider = r0
            org.telegram.ui.Components.ChatAttachAlert$23 r0 = new org.telegram.ui.Components.ChatAttachAlert$23
            java.lang.String r1 = "openProgress"
            r0.<init>(r1)
            r6.ATTACH_ALERT_PROGRESS = r0
            android.view.animation.OvershootInterpolator r0 = new android.view.animation.OvershootInterpolator
            r1 = 1060320051(0x3var_, float:0.7)
            r0.<init>(r1)
            r6.openInterpolator = r0
            r6.baseFragment = r8
            r6.setDelegate(r6)
            r6.checkCamera(r9)
            int r0 = r6.currentAccount
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getInstance(r0)
            int r1 = org.telegram.messenger.NotificationCenter.albumsDidLoad
            r0.addObserver(r6, r1)
            int r0 = r6.currentAccount
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getInstance(r0)
            int r1 = org.telegram.messenger.NotificationCenter.reloadInlineHints
            r0.addObserver(r6, r1)
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r1 = org.telegram.messenger.NotificationCenter.cameraInitied
            r0.addObserver(r6, r1)
            android.content.res.Resources r0 = r40.getResources()
            r1 = 2131165520(0x7var_, float:1.794526E38)
            android.graphics.drawable.Drawable r0 = r0.getDrawable(r1)
            android.graphics.drawable.Drawable r0 = r0.mutate()
            r6.cameraDrawable = r0
            org.telegram.ui.Components.ChatAttachAlert$2 r0 = new org.telegram.ui.Components.ChatAttachAlert$2
            r0.<init>(r7, r9)
            r6.sizeNotifierFrameLayout = r0
            r6.containerView = r0
            r0.setWillNotDraw(r9)
            android.view.ViewGroup r0 = r6.containerView
            int r1 = r6.backgroundPaddingLeft
            r0.setPadding(r1, r9, r1, r9)
            android.widget.TextView r0 = new android.widget.TextView
            r0.<init>(r7)
            r6.selectedTextView = r0
            java.lang.String r16 = "dialogTextBlack"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r16)
            r0.setTextColor(r1)
            android.widget.TextView r0 = r6.selectedTextView
            r5 = 1098907648(0x41800000, float:16.0)
            r0.setTextSize(r10, r5)
            android.widget.TextView r0 = r6.selectedTextView
            java.lang.String r17 = "fonts/rmedium.ttf"
            android.graphics.Typeface r1 = org.telegram.messenger.AndroidUtilities.getTypeface(r17)
            r0.setTypeface(r1)
            android.widget.TextView r0 = r6.selectedTextView
            r1 = 51
            r0.setGravity(r1)
            android.widget.TextView r0 = r6.selectedTextView
            r4 = 4
            r0.setVisibility(r4)
            android.widget.TextView r0 = r6.selectedTextView
            r3 = 0
            r0.setAlpha(r3)
            android.view.ViewGroup r0 = r6.containerView
            android.widget.TextView r1 = r6.selectedTextView
            r18 = -1
            r19 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r20 = 51
            r21 = 1102577664(0x41b80000, float:23.0)
            r22 = 0
            r23 = 1111490560(0x42400000, float:48.0)
            r24 = 0
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r18, r19, r20, r21, r22, r23, r24)
            r0.addView(r1, r2)
            org.telegram.ui.Components.ChatAttachAlert$3 r0 = new org.telegram.ui.Components.ChatAttachAlert$3
            r0.<init>(r7)
            r6.actionBar = r0
            java.lang.String r18 = "dialogBackground"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r18)
            r0.setBackgroundColor(r1)
            org.telegram.ui.ActionBar.ActionBar r0 = r6.actionBar
            r1 = 2131165437(0x7var_fd, float:1.7945091E38)
            r0.setBackButtonImage(r1)
            org.telegram.ui.ActionBar.ActionBar r0 = r6.actionBar
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r16)
            r0.setItemsColor(r1, r9)
            org.telegram.ui.ActionBar.ActionBar r0 = r6.actionBar
            java.lang.String r1 = "dialogButtonSelector"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setItemsBackgroundColor(r1, r9)
            org.telegram.ui.ActionBar.ActionBar r0 = r6.actionBar
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r16)
            r0.setTitleColor(r1)
            org.telegram.ui.ActionBar.ActionBar r0 = r6.actionBar
            r0.setOccupyStatusBar(r9)
            org.telegram.ui.ActionBar.ActionBar r0 = r6.actionBar
            r0.setAlpha(r3)
            android.view.ViewGroup r0 = r6.containerView
            org.telegram.ui.ActionBar.ActionBar r1 = r6.actionBar
            r2 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r13, r2)
            r0.addView(r1, r2)
            org.telegram.ui.ActionBar.ActionBar r0 = r6.actionBar
            org.telegram.ui.Components.ChatAttachAlert$4 r1 = new org.telegram.ui.Components.ChatAttachAlert$4
            r1.<init>(r8)
            r0.setActionBarMenuOnItemClick(r1)
            org.telegram.ui.Components.ChatAttachAlert$5 r2 = new org.telegram.ui.Components.ChatAttachAlert$5
            int r19 = org.telegram.ui.ActionBar.Theme.getColor(r16)
            r20 = 0
            r21 = 0
            r0 = r2
            r1 = r39
            r14 = r2
            r2 = r40
            r11 = 0
            r3 = r20
            r13 = 4
            r4 = r21
            r21 = 1098907648(0x41800000, float:16.0)
            r5 = r19
            r0.<init>(r2, r3, r4, r5)
            r6.selectedMenuItem = r14
            r14.setLongClickEnabled(r9)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r6.selectedMenuItem
            r1 = 2131165444(0x7var_, float:1.7945105E38)
            r0.setIcon((int) r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r6.selectedMenuItem
            java.lang.String r1 = "AccDescrMoreOptions"
            r2 = 2131623980(0x7f0e002c, float:1.8875127E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)
            r0.setContentDescription(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r6.selectedMenuItem
            java.lang.String r1 = "SendWithoutGrouping"
            r2 = 2131626679(0x7f0e0ab7, float:1.88806E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)
            r0.addSubItem(r9, r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r6.selectedMenuItem
            java.lang.String r1 = "SendWithoutCompression"
            r2 = 2131626678(0x7f0e0ab6, float:1.8880599E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)
            r0.addSubItem(r10, r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r6.selectedMenuItem
            r0.setVisibility(r13)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r6.selectedMenuItem
            r0.setAlpha(r11)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r6.selectedMenuItem
            r0.setSubMenuOpenSide(r12)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r6.selectedMenuItem
            org.telegram.ui.Components.-$$Lambda$ChatAttachAlert$Nt32ReYKj24zlB7vHT-vQyIS3fQ r1 = new org.telegram.ui.Components.-$$Lambda$ChatAttachAlert$Nt32ReYKj24zlB7vHT-vQyIS3fQ
            r1.<init>()
            r0.setDelegate(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r6.selectedMenuItem
            r1 = 1116733440(0x42900000, float:72.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r0.setAdditionalYOffset(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r6.selectedMenuItem
            r14 = 1086324736(0x40CLASSNAME, float:6.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r14)
            float r1 = (float) r1
            r0.setTranslationX(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r6.selectedMenuItem
            java.lang.String r1 = "dialogButtonSelector"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r2 = 6
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r1, r2)
            r0.setBackgroundDrawable(r1)
            android.view.ViewGroup r0 = r6.containerView
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r6.selectedMenuItem
            r5 = 48
            r2 = 53
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r5, r5, r2)
            r0.addView(r1, r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r6.selectedMenuItem
            org.telegram.ui.Components.-$$Lambda$ChatAttachAlert$ub7wHJjWI52pBpaKjvNcIp8HBK8 r1 = new org.telegram.ui.Components.-$$Lambda$ChatAttachAlert$ub7wHJjWI52pBpaKjvNcIp8HBK8
            r1.<init>()
            r0.setOnClickListener(r1)
            org.telegram.ui.Components.ChatAttachAlert$6 r0 = new org.telegram.ui.Components.ChatAttachAlert$6
            r0.<init>(r7)
            r6.gridView = r0
            org.telegram.ui.Components.ChatAttachAlert$PhotoAttachAdapter r1 = new org.telegram.ui.Components.ChatAttachAlert$PhotoAttachAdapter
            r1.<init>(r7, r10)
            r6.adapter = r1
            r0.setAdapter(r1)
            org.telegram.ui.Components.ChatAttachAlert$PhotoAttachAdapter r0 = r6.adapter
            r0.createCache()
            org.telegram.ui.Components.RecyclerListView r0 = r6.gridView
            r0.setClipToPadding(r9)
            org.telegram.ui.Components.RecyclerListView r0 = r6.gridView
            r4 = 0
            r0.setItemAnimator(r4)
            org.telegram.ui.Components.RecyclerListView r0 = r6.gridView
            r0.setLayoutAnimation(r4)
            org.telegram.ui.Components.RecyclerListView r0 = r6.gridView
            r0.setVerticalScrollBarEnabled(r9)
            org.telegram.ui.Components.RecyclerListView r0 = r6.gridView
            java.lang.String r1 = "dialogScrollGlow"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setGlowColor(r1)
            android.view.ViewGroup r0 = r6.containerView
            org.telegram.ui.Components.RecyclerListView r1 = r6.gridView
            r24 = -1
            r25 = -1082130432(0xffffffffbvar_, float:-1.0)
            r26 = 51
            r27 = 0
            r28 = 1093664768(0x41300000, float:11.0)
            r29 = 0
            r30 = 0
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r24, r25, r26, r27, r28, r29, r30)
            r0.addView(r1, r2)
            org.telegram.ui.Components.RecyclerListView r0 = r6.gridView
            org.telegram.ui.Components.ChatAttachAlert$7 r1 = new org.telegram.ui.Components.ChatAttachAlert$7
            r1.<init>()
            r0.setOnScrollListener(r1)
            org.telegram.ui.Components.ChatAttachAlert$8 r0 = new org.telegram.ui.Components.ChatAttachAlert$8
            int r1 = r6.itemSize
            r0.<init>(r6, r7, r1)
            r6.layoutManager = r0
            org.telegram.ui.Components.ChatAttachAlert$9 r1 = new org.telegram.ui.Components.ChatAttachAlert$9
            r1.<init>()
            r0.setSpanSizeLookup(r1)
            org.telegram.ui.Components.RecyclerListView r0 = r6.gridView
            androidx.recyclerview.widget.GridLayoutManager r1 = r6.layoutManager
            r0.setLayoutManager(r1)
            org.telegram.ui.Components.RecyclerListView r0 = r6.gridView
            org.telegram.ui.Components.-$$Lambda$ChatAttachAlert$xPFtpf_jFJqlrSbDDT55lEsjbs8 r1 = new org.telegram.ui.Components.-$$Lambda$ChatAttachAlert$xPFtpf_jFJqlrSbDDT55lEsjbs8
            r1.<init>()
            r0.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r1)
            org.telegram.ui.Components.RecyclerListView r0 = r6.gridView
            org.telegram.ui.Components.-$$Lambda$ChatAttachAlert$03FRJLRiDOnTyOuDJhdHzThxHhc r1 = new org.telegram.ui.Components.-$$Lambda$ChatAttachAlert$03FRJLRiDOnTyOuDJhdHzThxHhc
            r1.<init>()
            r0.setOnItemLongClickListener((org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener) r1)
            org.telegram.ui.Components.RecyclerViewItemRangeSelector r0 = new org.telegram.ui.Components.RecyclerViewItemRangeSelector
            org.telegram.ui.Components.ChatAttachAlert$10 r1 = new org.telegram.ui.Components.ChatAttachAlert$10
            r1.<init>()
            r0.<init>(r1)
            r6.itemRangeSelector = r0
            org.telegram.ui.Components.RecyclerListView r1 = r6.gridView
            r1.addOnItemTouchListener(r0)
            org.telegram.ui.ActionBar.ActionBar r0 = r6.actionBar
            org.telegram.ui.ActionBar.ActionBarMenu r0 = r0.createMenu()
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = new org.telegram.ui.ActionBar.ActionBarMenuItem
            r1.<init>(r7, r0, r9, r9)
            r6.dropDownContainer = r1
            r1.setSubMenuOpenSide(r10)
            org.telegram.ui.ActionBar.ActionBar r0 = r6.actionBar
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r6.dropDownContainer
            boolean r2 = org.telegram.messenger.AndroidUtilities.isTablet()
            r19 = 1113587712(0x42600000, float:56.0)
            if (r2 == 0) goto L_0x02dc
            r2 = 1115684864(0x42800000, float:64.0)
            r27 = 1115684864(0x42800000, float:64.0)
            goto L_0x02de
        L_0x02dc:
            r27 = 1113587712(0x42600000, float:56.0)
        L_0x02de:
            r28 = 0
            r29 = 1109393408(0x42200000, float:40.0)
            r30 = 0
            r24 = -2
            r25 = -1082130432(0xffffffffbvar_, float:-1.0)
            r26 = 51
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r24, r25, r26, r27, r28, r29, r30)
            r0.addView(r1, r9, r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r6.dropDownContainer
            org.telegram.ui.Components.-$$Lambda$ChatAttachAlert$N7eKeWXf4VQLw62DCiXcluGuuq0 r1 = new org.telegram.ui.Components.-$$Lambda$ChatAttachAlert$N7eKeWXf4VQLw62DCiXcluGuuq0
            r1.<init>()
            r0.setOnClickListener(r1)
            android.widget.TextView r0 = new android.widget.TextView
            r0.<init>(r7)
            r6.dropDown = r0
            r0.setGravity(r15)
            android.widget.TextView r0 = r6.dropDown
            r0.setSingleLine(r10)
            android.widget.TextView r0 = r6.dropDown
            r0.setLines(r10)
            android.widget.TextView r0 = r6.dropDown
            r0.setMaxLines(r10)
            android.widget.TextView r0 = r6.dropDown
            android.text.TextUtils$TruncateAt r1 = android.text.TextUtils.TruncateAt.END
            r0.setEllipsize(r1)
            android.widget.TextView r0 = r6.dropDown
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r16)
            r0.setTextColor(r1)
            android.widget.TextView r0 = r6.dropDown
            r1 = 2131624632(0x7f0e02b8, float:1.887645E38)
            java.lang.String r2 = "ChatGallery"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setText(r1)
            android.widget.TextView r0 = r6.dropDown
            android.graphics.Typeface r1 = org.telegram.messenger.AndroidUtilities.getTypeface(r17)
            r0.setTypeface(r1)
            android.content.res.Resources r0 = r40.getResources()
            r1 = 2131165455(0x7var_f, float:1.7945128E38)
            android.graphics.drawable.Drawable r0 = r0.getDrawable(r1)
            android.graphics.drawable.Drawable r0 = r0.mutate()
            r6.dropDownDrawable = r0
            android.graphics.PorterDuffColorFilter r1 = new android.graphics.PorterDuffColorFilter
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r16)
            android.graphics.PorterDuff$Mode r3 = android.graphics.PorterDuff.Mode.MULTIPLY
            r1.<init>(r2, r3)
            r0.setColorFilter(r1)
            android.widget.TextView r0 = r6.dropDown
            r1 = 1082130432(0x40800000, float:4.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r0.setCompoundDrawablePadding(r1)
            android.widget.TextView r0 = r6.dropDown
            r15 = 1092616192(0x41200000, float:10.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r15)
            r0.setPadding(r9, r9, r1, r9)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r6.dropDownContainer
            android.widget.TextView r1 = r6.dropDown
            r25 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r26 = 16
            r27 = 1098907648(0x41800000, float:16.0)
            r29 = 0
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r24, r25, r26, r27, r28, r29, r30)
            r0.addView(r1, r2)
            android.view.View r0 = new android.view.View
            r0.<init>(r7)
            r6.actionBarShadow = r0
            r0.setAlpha(r11)
            android.view.View r0 = r6.actionBarShadow
            java.lang.String r1 = "dialogShadowLine"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setBackgroundColor(r1)
            android.view.ViewGroup r0 = r6.containerView
            android.view.View r1 = r6.actionBarShadow
            r2 = 1065353216(0x3var_, float:1.0)
            r3 = -1
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r3, r2)
            r0.addView(r1, r2)
            org.telegram.ui.Components.EmptyTextProgressView r0 = new org.telegram.ui.Components.EmptyTextProgressView
            r0.<init>(r7)
            r6.progressView = r0
            r1 = 2131625808(0x7f0e0750, float:1.8878834E38)
            java.lang.String r2 = "NoPhotos"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setText(r1)
            org.telegram.ui.Components.EmptyTextProgressView r0 = r6.progressView
            r0.setOnTouchListener(r4)
            org.telegram.ui.Components.EmptyTextProgressView r0 = r6.progressView
            r1 = 20
            r0.setTextSize(r1)
            android.view.ViewGroup r0 = r6.containerView
            org.telegram.ui.Components.EmptyTextProgressView r1 = r6.progressView
            r2 = 1117782016(0x42a00000, float:80.0)
            r3 = -1
            android.widget.FrameLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createFrame(r3, r2)
            r0.addView(r1, r14)
            boolean r0 = r6.loading
            if (r0 == 0) goto L_0x03dd
            org.telegram.ui.Components.EmptyTextProgressView r0 = r6.progressView
            r0.showProgress()
            goto L_0x03e2
        L_0x03dd:
            org.telegram.ui.Components.EmptyTextProgressView r0 = r6.progressView
            r0.showTextView()
        L_0x03e2:
            android.view.View r0 = new android.view.View
            r0.<init>(r7)
            r6.shadow = r0
            r1 = 2131165435(0x7var_fb, float:1.7945087E38)
            r0.setBackgroundResource(r1)
            android.view.ViewGroup r0 = r6.containerView
            android.view.View r1 = r6.shadow
            r23 = -1
            r24 = 1077936128(0x40400000, float:3.0)
            r25 = 83
            r26 = 0
            r27 = 0
            r28 = 0
            r29 = 1119354880(0x42b80000, float:92.0)
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r23, r24, r25, r26, r27, r28, r29)
            r0.addView(r1, r2)
            org.telegram.ui.Components.ChatAttachAlert$11 r0 = new org.telegram.ui.Components.ChatAttachAlert$11
            r0.<init>(r7)
            r6.buttonsRecyclerView = r0
            org.telegram.ui.Components.ChatAttachAlert$ButtonsAdapter r1 = new org.telegram.ui.Components.ChatAttachAlert$ButtonsAdapter
            r1.<init>(r7)
            r6.buttonsAdapter = r1
            r0.setAdapter(r1)
            org.telegram.ui.Components.RecyclerListView r0 = r6.buttonsRecyclerView
            androidx.recyclerview.widget.LinearLayoutManager r1 = new androidx.recyclerview.widget.LinearLayoutManager
            r1.<init>(r7, r9, r9)
            r6.buttonsLayoutManager = r1
            r0.setLayoutManager(r1)
            org.telegram.ui.Components.RecyclerListView r0 = r6.buttonsRecyclerView
            r0.setVerticalScrollBarEnabled(r9)
            org.telegram.ui.Components.RecyclerListView r0 = r6.buttonsRecyclerView
            r0.setHorizontalScrollBarEnabled(r9)
            org.telegram.ui.Components.RecyclerListView r0 = r6.buttonsRecyclerView
            r0.setItemAnimator(r4)
            org.telegram.ui.Components.RecyclerListView r0 = r6.buttonsRecyclerView
            r0.setLayoutAnimation(r4)
            org.telegram.ui.Components.RecyclerListView r0 = r6.buttonsRecyclerView
            java.lang.String r1 = "dialogScrollGlow"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setGlowColor(r1)
            org.telegram.ui.Components.RecyclerListView r0 = r6.buttonsRecyclerView
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r18)
            r0.setBackgroundColor(r1)
            android.view.ViewGroup r0 = r6.containerView
            org.telegram.ui.Components.RecyclerListView r1 = r6.buttonsRecyclerView
            r2 = 92
            r14 = 83
            r3 = -1
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r3, r2, r14)
            r0.addView(r1, r2)
            org.telegram.ui.Components.RecyclerListView r0 = r6.buttonsRecyclerView
            org.telegram.ui.Components.-$$Lambda$ChatAttachAlert$vMucsWl4dIksDMe1Lym3-gvIee8 r1 = new org.telegram.ui.Components.-$$Lambda$ChatAttachAlert$vMucsWl4dIksDMe1Lym3-gvIee8
            r1.<init>()
            r0.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r1)
            org.telegram.ui.Components.RecyclerListView r0 = r6.buttonsRecyclerView
            org.telegram.ui.Components.-$$Lambda$ChatAttachAlert$JYtDGovD-uFwIS0ZuDMdxXJYMPA r1 = new org.telegram.ui.Components.-$$Lambda$ChatAttachAlert$JYtDGovD-uFwIS0ZuDMdxXJYMPA
            r1.<init>()
            r0.setOnItemLongClickListener((org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener) r1)
            android.widget.FrameLayout r0 = new android.widget.FrameLayout
            r0.<init>(r7)
            r6.frameLayout2 = r0
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r18)
            r0.setBackgroundColor(r1)
            android.widget.FrameLayout r0 = r6.frameLayout2
            r0.setVisibility(r13)
            android.widget.FrameLayout r0 = r6.frameLayout2
            r0.setAlpha(r11)
            android.view.ViewGroup r0 = r6.containerView
            android.widget.FrameLayout r1 = r6.frameLayout2
            r2 = -1
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r2, r5, r14)
            r0.addView(r1, r3)
            android.widget.FrameLayout r0 = r6.frameLayout2
            org.telegram.ui.Components.-$$Lambda$ChatAttachAlert$NM-6Qyb21GrXWLvtWtAT8l7MnJs r1 = org.telegram.ui.Components.$$Lambda$ChatAttachAlert$NM6Qyb21GrXWLvtWtAT8l7MnJs.INSTANCE
            r0.setOnTouchListener(r1)
            org.telegram.ui.Components.ChatAttachAlert$12 r3 = new org.telegram.ui.Components.ChatAttachAlert$12
            org.telegram.ui.Components.SizeNotifierFrameLayout r2 = r6.sizeNotifierFrameLayout
            r18 = 0
            r23 = 1
            r0 = r3
            r1 = r39
            r24 = r2
            r2 = r40
            r12 = r3
            r3 = r24
            r4 = r18
            r5 = r23
            r0.<init>(r2, r3, r4, r5)
            r6.commentTextView = r12
            android.text.InputFilter[] r0 = new android.text.InputFilter[r10]
            android.text.InputFilter$LengthFilter r1 = new android.text.InputFilter$LengthFilter
            int r2 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            int r2 = r2.maxCaptionLength
            r1.<init>(r2)
            r0[r9] = r1
            org.telegram.ui.Components.EditTextEmoji r1 = r6.commentTextView
            r1.setFilters(r0)
            org.telegram.ui.Components.EditTextEmoji r0 = r6.commentTextView
            r1 = 2131624108(0x7f0e00ac, float:1.8875386E38)
            java.lang.String r2 = "AddCaption"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setHint(r1)
            org.telegram.ui.Components.EditTextEmoji r0 = r6.commentTextView
            r0.onResume()
            org.telegram.ui.Components.EditTextEmoji r0 = r6.commentTextView
            org.telegram.ui.Components.EditTextBoldCursor r0 = r0.getEditText()
            r0.setMaxLines(r10)
            r0.setSingleLine(r10)
            android.widget.FrameLayout r0 = r6.frameLayout2
            org.telegram.ui.Components.EditTextEmoji r1 = r6.commentTextView
            r32 = -1
            r33 = -1082130432(0xffffffffbvar_, float:-1.0)
            r34 = 51
            r35 = 0
            r36 = 0
            r37 = 1118306304(0x42a80000, float:84.0)
            r38 = 0
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r32, r33, r34, r35, r36, r37, r38)
            r0.addView(r1, r2)
            android.widget.FrameLayout r0 = new android.widget.FrameLayout
            r0.<init>(r7)
            r6.writeButtonContainer = r0
            r0.setVisibility(r13)
            android.widget.FrameLayout r0 = r6.writeButtonContainer
            r1 = 1045220557(0x3e4ccccd, float:0.2)
            r0.setScaleX(r1)
            android.widget.FrameLayout r0 = r6.writeButtonContainer
            r0.setScaleY(r1)
            android.widget.FrameLayout r0 = r6.writeButtonContainer
            r0.setAlpha(r11)
            android.widget.FrameLayout r0 = r6.writeButtonContainer
            r2 = 2131626648(0x7f0e0a98, float:1.8880538E38)
            java.lang.String r3 = "Send"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setContentDescription(r2)
            android.view.ViewGroup r0 = r6.containerView
            android.widget.FrameLayout r2 = r6.writeButtonContainer
            r32 = 60
            r33 = 1114636288(0x42700000, float:60.0)
            r34 = 85
            r37 = 1086324736(0x40CLASSNAME, float:6.0)
            r38 = 1092616192(0x41200000, float:10.0)
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r32, r33, r34, r35, r36, r37, r38)
            r0.addView(r2, r3)
            android.widget.ImageView r0 = new android.widget.ImageView
            r0.<init>(r7)
            r6.writeButton = r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r19)
            java.lang.String r2 = "dialogFloatingButton"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            int r3 = android.os.Build.VERSION.SDK_INT
            r4 = 21
            if (r3 < r4) goto L_0x055e
            java.lang.String r3 = "dialogFloatingButtonPressed"
            goto L_0x0560
        L_0x055e:
            java.lang.String r3 = "dialogFloatingButton"
        L_0x0560:
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.createSimpleSelectorCircleDrawable(r0, r2, r3)
            r6.writeButtonDrawable = r0
            int r0 = android.os.Build.VERSION.SDK_INT
            if (r0 >= r4) goto L_0x059d
            android.content.res.Resources r0 = r40.getResources()
            r2 = 2131165393(0x7var_d1, float:1.7945002E38)
            android.graphics.drawable.Drawable r0 = r0.getDrawable(r2)
            android.graphics.drawable.Drawable r0 = r0.mutate()
            android.graphics.PorterDuffColorFilter r2 = new android.graphics.PorterDuffColorFilter
            r3 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            android.graphics.PorterDuff$Mode r5 = android.graphics.PorterDuff.Mode.MULTIPLY
            r2.<init>(r3, r5)
            r0.setColorFilter(r2)
            org.telegram.ui.Components.CombinedDrawable r2 = new org.telegram.ui.Components.CombinedDrawable
            android.graphics.drawable.Drawable r3 = r6.writeButtonDrawable
            r2.<init>(r0, r3, r9, r9)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r19)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r19)
            r2.setIconSize(r0, r3)
            r6.writeButtonDrawable = r2
        L_0x059d:
            android.widget.ImageView r0 = r6.writeButton
            android.graphics.drawable.Drawable r2 = r6.writeButtonDrawable
            r0.setBackgroundDrawable(r2)
            android.widget.ImageView r0 = r6.writeButton
            r2 = 2131165273(0x7var_, float:1.7944758E38)
            r0.setImageResource(r2)
            android.widget.ImageView r0 = r6.writeButton
            android.graphics.PorterDuffColorFilter r2 = new android.graphics.PorterDuffColorFilter
            java.lang.String r3 = "dialogFloatingIcon"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            android.graphics.PorterDuff$Mode r5 = android.graphics.PorterDuff.Mode.MULTIPLY
            r2.<init>(r3, r5)
            r0.setColorFilter(r2)
            android.widget.ImageView r0 = r6.writeButton
            android.widget.ImageView$ScaleType r2 = android.widget.ImageView.ScaleType.CENTER
            r0.setScaleType(r2)
            int r0 = android.os.Build.VERSION.SDK_INT
            if (r0 < r4) goto L_0x05d3
            android.widget.ImageView r0 = r6.writeButton
            org.telegram.ui.Components.ChatAttachAlert$13 r2 = new org.telegram.ui.Components.ChatAttachAlert$13
            r2.<init>(r6)
            r0.setOutlineProvider(r2)
        L_0x05d3:
            android.widget.FrameLayout r0 = r6.writeButtonContainer
            android.widget.ImageView r2 = r6.writeButton
            int r3 = android.os.Build.VERSION.SDK_INT
            if (r3 < r4) goto L_0x05e0
            r3 = 56
            r32 = 56
            goto L_0x05e4
        L_0x05e0:
            r3 = 60
            r32 = 60
        L_0x05e4:
            int r3 = android.os.Build.VERSION.SDK_INT
            if (r3 < r4) goto L_0x05eb
            r33 = 1113587712(0x42600000, float:56.0)
            goto L_0x05ef
        L_0x05eb:
            r19 = 1114636288(0x42700000, float:60.0)
            r33 = 1114636288(0x42700000, float:60.0)
        L_0x05ef:
            r34 = 51
            int r3 = android.os.Build.VERSION.SDK_INT
            if (r3 < r4) goto L_0x05fa
            r3 = 1073741824(0x40000000, float:2.0)
            r35 = 1073741824(0x40000000, float:2.0)
            goto L_0x05fc
        L_0x05fa:
            r35 = 0
        L_0x05fc:
            r36 = 0
            r37 = 0
            r38 = 0
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r32, r33, r34, r35, r36, r37, r38)
            r0.addView(r2, r3)
            android.widget.ImageView r0 = r6.writeButton
            org.telegram.ui.Components.-$$Lambda$ChatAttachAlert$oB8CYoYcB7iIWh1dkLqBzyIkh8Y r2 = new org.telegram.ui.Components.-$$Lambda$ChatAttachAlert$oB8CYoYcB7iIWh1dkLqBzyIkh8Y
            r2.<init>(r8)
            r0.setOnClickListener(r2)
            android.widget.ImageView r0 = r6.writeButton
            org.telegram.ui.Components.-$$Lambda$ChatAttachAlert$QKyi0HLLA-qrKnznWIASX4wQREg r2 = new org.telegram.ui.Components.-$$Lambda$ChatAttachAlert$QKyi0HLLA-qrKnznWIASX4wQREg
            r2.<init>()
            r0.setOnLongClickListener(r2)
            android.text.TextPaint r0 = r6.textPaint
            r2 = 1094713344(0x41400000, float:12.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            r0.setTextSize(r2)
            android.text.TextPaint r0 = r6.textPaint
            android.graphics.Typeface r2 = org.telegram.messenger.AndroidUtilities.getTypeface(r17)
            r0.setTypeface(r2)
            org.telegram.ui.Components.ChatAttachAlert$15 r0 = new org.telegram.ui.Components.ChatAttachAlert$15
            r0.<init>(r7)
            r6.selectedCountView = r0
            r0.setAlpha(r11)
            android.view.View r0 = r6.selectedCountView
            r0.setScaleX(r1)
            android.view.View r0 = r6.selectedCountView
            r0.setScaleY(r1)
            android.view.ViewGroup r0 = r6.containerView
            android.view.View r1 = r6.selectedCountView
            r32 = 42
            r33 = 1103101952(0x41CLASSNAME, float:24.0)
            r34 = 85
            r35 = 0
            r37 = -1056964608(0xffffffffCLASSNAME, float:-8.0)
            r38 = 1091567616(0x41100000, float:9.0)
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r32, r33, r34, r35, r36, r37, r38)
            r0.addView(r1, r2)
            android.widget.TextView r0 = new android.widget.TextView
            r0.<init>(r7)
            r6.recordTime = r0
            r1 = 2131165927(0x7var_e7, float:1.7946085E38)
            r0.setBackgroundResource(r1)
            android.widget.TextView r0 = r6.recordTime
            android.graphics.drawable.Drawable r0 = r0.getBackground()
            android.graphics.PorterDuffColorFilter r1 = new android.graphics.PorterDuffColorFilter
            r2 = 1711276032(0x66000000, float:1.5111573E23)
            android.graphics.PorterDuff$Mode r3 = android.graphics.PorterDuff.Mode.MULTIPLY
            r1.<init>(r2, r3)
            r0.setColorFilter(r1)
            android.widget.TextView r0 = r6.recordTime
            r1 = 1097859072(0x41700000, float:15.0)
            r0.setTextSize(r10, r1)
            android.widget.TextView r0 = r6.recordTime
            android.graphics.Typeface r1 = org.telegram.messenger.AndroidUtilities.getTypeface(r17)
            r0.setTypeface(r1)
            android.widget.TextView r0 = r6.recordTime
            r0.setAlpha(r11)
            android.widget.TextView r0 = r6.recordTime
            r1 = -1
            r0.setTextColor(r1)
            android.widget.TextView r0 = r6.recordTime
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r15)
            r2 = 1084227584(0x40a00000, float:5.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r15)
            r5 = 1084227584(0x40a00000, float:5.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r0.setPadding(r1, r2, r3, r5)
            org.telegram.ui.ActionBar.BottomSheet$ContainerView r0 = r6.container
            android.widget.TextView r1 = r6.recordTime
            r32 = -2
            r33 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r34 = 49
            r36 = 1098907648(0x41800000, float:16.0)
            r37 = 0
            r38 = 0
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r32, r33, r34, r35, r36, r37, r38)
            r0.addView(r1, r2)
            org.telegram.ui.Components.ChatAttachAlert$16 r0 = new org.telegram.ui.Components.ChatAttachAlert$16
            r0.<init>(r7)
            r6.cameraPanel = r0
            r1 = 8
            r0.setVisibility(r1)
            android.widget.FrameLayout r0 = r6.cameraPanel
            r0.setAlpha(r11)
            org.telegram.ui.ActionBar.BottomSheet$ContainerView r0 = r6.container
            android.widget.FrameLayout r2 = r6.cameraPanel
            r3 = 126(0x7e, float:1.77E-43)
            r5 = -1
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r5, r3, r14)
            r0.addView(r2, r3)
            android.widget.TextView r0 = new android.widget.TextView
            r0.<init>(r7)
            r6.counterTextView = r0
            r2 = 2131165806(0x7var_e, float:1.794584E38)
            r0.setBackgroundResource(r2)
            android.widget.TextView r0 = r6.counterTextView
            r0.setVisibility(r1)
            android.widget.TextView r0 = r6.counterTextView
            r0.setTextColor(r5)
            android.widget.TextView r0 = r6.counterTextView
            r2 = 17
            r0.setGravity(r2)
            android.widget.TextView r0 = r6.counterTextView
            r0.setPivotX(r11)
            android.widget.TextView r0 = r6.counterTextView
            r0.setPivotY(r11)
            android.widget.TextView r0 = r6.counterTextView
            android.graphics.Typeface r2 = org.telegram.messenger.AndroidUtilities.getTypeface(r17)
            r0.setTypeface(r2)
            android.widget.TextView r0 = r6.counterTextView
            r2 = 2131165804(0x7var_c, float:1.7945835E38)
            r0.setCompoundDrawablesWithIntrinsicBounds(r9, r9, r2, r9)
            android.widget.TextView r0 = r6.counterTextView
            r2 = 1082130432(0x40800000, float:4.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r0.setCompoundDrawablePadding(r2)
            android.widget.TextView r0 = r6.counterTextView
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r21)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r21)
            r0.setPadding(r2, r9, r3, r9)
            org.telegram.ui.ActionBar.BottomSheet$ContainerView r0 = r6.container
            android.widget.TextView r2 = r6.counterTextView
            r33 = 1108869120(0x42180000, float:38.0)
            r34 = 51
            r36 = 0
            r38 = 1122500608(0x42e80000, float:116.0)
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r32, r33, r34, r35, r36, r37, r38)
            r0.addView(r2, r3)
            android.widget.TextView r0 = r6.counterTextView
            org.telegram.ui.Components.-$$Lambda$ChatAttachAlert$Fon025q1rIkFw-nUZVlZFdAEfLM r2 = new org.telegram.ui.Components.-$$Lambda$ChatAttachAlert$Fon025q1rIkFw-nUZVlZFdAEfLM
            r2.<init>()
            r0.setOnClickListener(r2)
            org.telegram.ui.Components.ZoomControlView r0 = new org.telegram.ui.Components.ZoomControlView
            r0.<init>(r7)
            r6.zoomControlView = r0
            r0.setVisibility(r1)
            org.telegram.ui.Components.ZoomControlView r0 = r6.zoomControlView
            r0.setAlpha(r11)
            org.telegram.ui.ActionBar.BottomSheet$ContainerView r0 = r6.container
            org.telegram.ui.Components.ZoomControlView r1 = r6.zoomControlView
            r33 = 1112014848(0x42480000, float:50.0)
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r32, r33, r34, r35, r36, r37, r38)
            r0.addView(r1, r2)
            org.telegram.ui.Components.ZoomControlView r0 = r6.zoomControlView
            org.telegram.ui.Components.-$$Lambda$ChatAttachAlert$eQtgJJNFjdWPp1e-e-DcSIpULYQ r1 = new org.telegram.ui.Components.-$$Lambda$ChatAttachAlert$eQtgJJNFjdWPp1e-e-DcSIpULYQ
            r1.<init>()
            r0.setDelegate(r1)
            org.telegram.ui.Components.ShutterButton r0 = new org.telegram.ui.Components.ShutterButton
            r0.<init>(r7)
            r6.shutterButton = r0
            android.widget.FrameLayout r1 = r6.cameraPanel
            r2 = 84
            r3 = 84
            r5 = 17
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r2, r3, r5)
            r1.addView(r0, r2)
            org.telegram.ui.Components.ShutterButton r0 = r6.shutterButton
            org.telegram.ui.Components.ChatAttachAlert$17 r1 = new org.telegram.ui.Components.ChatAttachAlert$17
            r1.<init>(r8)
            r0.setDelegate(r1)
            org.telegram.ui.Components.ShutterButton r0 = r6.shutterButton
            r0.setFocusable(r10)
            org.telegram.ui.Components.ShutterButton r0 = r6.shutterButton
            r1 = 2131624011(0x7f0e004b, float:1.887519E38)
            java.lang.String r2 = "AccDescrShutter"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setContentDescription(r1)
            android.widget.ImageView r0 = new android.widget.ImageView
            r0.<init>(r7)
            r6.switchCameraButton = r0
            android.widget.ImageView$ScaleType r1 = android.widget.ImageView.ScaleType.CENTER
            r0.setScaleType(r1)
            android.widget.FrameLayout r0 = r6.cameraPanel
            android.widget.ImageView r1 = r6.switchCameraButton
            r2 = 48
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r2, r2, r4)
            r0.addView(r1, r3)
            android.widget.ImageView r0 = r6.switchCameraButton
            org.telegram.ui.Components.-$$Lambda$ChatAttachAlert$GmiOLhg2Nufzy6t_kr2LXAC4J90 r1 = new org.telegram.ui.Components.-$$Lambda$ChatAttachAlert$GmiOLhg2Nufzy6t_kr2LXAC4J90
            r1.<init>()
            r0.setOnClickListener(r1)
            android.widget.ImageView r0 = r6.switchCameraButton
            r1 = 2131624015(0x7f0e004f, float:1.8875198E38)
            java.lang.String r3 = "AccDescrSwitchCamera"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
            r0.setContentDescription(r1)
            r0 = 0
        L_0x07df:
            r1 = 2
            if (r0 >= r1) goto L_0x0831
            android.widget.ImageView[] r1 = r6.flashModeButton
            android.widget.ImageView r3 = new android.widget.ImageView
            r3.<init>(r7)
            r1[r0] = r3
            android.widget.ImageView[] r1 = r6.flashModeButton
            r1 = r1[r0]
            android.widget.ImageView$ScaleType r3 = android.widget.ImageView.ScaleType.CENTER
            r1.setScaleType(r3)
            android.widget.ImageView[] r1 = r6.flashModeButton
            r1 = r1[r0]
            r1.setVisibility(r13)
            android.widget.FrameLayout r1 = r6.cameraPanel
            android.widget.ImageView[] r3 = r6.flashModeButton
            r3 = r3[r0]
            r4 = 51
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r2, r2, r4)
            r1.addView(r3, r4)
            android.widget.ImageView[] r1 = r6.flashModeButton
            r1 = r1[r0]
            org.telegram.ui.Components.-$$Lambda$ChatAttachAlert$U7zXuSA7-35WFD5WliOoSKocj8I r3 = new org.telegram.ui.Components.-$$Lambda$ChatAttachAlert$U7zXuSA7-35WFD5WliOoSKocj8I
            r3.<init>()
            r1.setOnClickListener(r3)
            android.widget.ImageView[] r1 = r6.flashModeButton
            r1 = r1[r0]
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "flash mode "
            r3.append(r4)
            r3.append(r0)
            java.lang.String r3 = r3.toString()
            r1.setContentDescription(r3)
            int r0 = r0 + 1
            goto L_0x07df
        L_0x0831:
            android.widget.TextView r0 = new android.widget.TextView
            r0.<init>(r7)
            r6.tooltipTextView = r0
            r1 = 1097859072(0x41700000, float:15.0)
            r0.setTextSize(r10, r1)
            android.widget.TextView r0 = r6.tooltipTextView
            r1 = -1
            r0.setTextColor(r1)
            android.widget.TextView r0 = r6.tooltipTextView
            r1 = 2131626893(0x7f0e0b8d, float:1.8881035E38)
            java.lang.String r2 = "TapForVideo"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.setText(r1)
            android.widget.TextView r0 = r6.tooltipTextView
            r1 = 1079334215(0x40555547, float:3.33333)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            r2 = 1059749626(0x3f2a7efa, float:0.666)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            r3 = 1275068416(0x4CLASSNAME, float:3.3554432E7)
            r0.setShadowLayer(r1, r11, r2, r3)
            android.widget.TextView r0 = r6.tooltipTextView
            r1 = 1086324736(0x40CLASSNAME, float:6.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r0.setPadding(r2, r9, r1, r9)
            android.widget.FrameLayout r0 = r6.cameraPanel
            android.widget.TextView r1 = r6.tooltipTextView
            r31 = -2
            r32 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r33 = 81
            r34 = 0
            r35 = 0
            r36 = 0
            r37 = 1098907648(0x41800000, float:16.0)
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r31, r32, r33, r34, r35, r36, r37)
            r0.addView(r1, r2)
            org.telegram.ui.Components.ChatAttachAlert$20 r0 = new org.telegram.ui.Components.ChatAttachAlert$20
            r0.<init>(r7)
            r6.cameraPhotoRecyclerView = r0
            r0.setVerticalScrollBarEnabled(r10)
            org.telegram.ui.Components.RecyclerListView r0 = r6.cameraPhotoRecyclerView
            org.telegram.ui.Components.ChatAttachAlert$PhotoAttachAdapter r1 = new org.telegram.ui.Components.ChatAttachAlert$PhotoAttachAdapter
            r1.<init>(r7, r9)
            r6.cameraAttachAdapter = r1
            r0.setAdapter(r1)
            org.telegram.ui.Components.ChatAttachAlert$PhotoAttachAdapter r0 = r6.cameraAttachAdapter
            r0.createCache()
            org.telegram.ui.Components.RecyclerListView r0 = r6.cameraPhotoRecyclerView
            r0.setClipToPadding(r9)
            org.telegram.ui.Components.RecyclerListView r0 = r6.cameraPhotoRecyclerView
            r1 = 1090519040(0x41000000, float:8.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r2 = 1090519040(0x41000000, float:8.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r0.setPadding(r1, r9, r2, r9)
            org.telegram.ui.Components.RecyclerListView r0 = r6.cameraPhotoRecyclerView
            r1 = 0
            r0.setItemAnimator(r1)
            org.telegram.ui.Components.RecyclerListView r0 = r6.cameraPhotoRecyclerView
            r0.setLayoutAnimation(r1)
            org.telegram.ui.Components.RecyclerListView r0 = r6.cameraPhotoRecyclerView
            r1 = 2
            r0.setOverScrollMode(r1)
            org.telegram.ui.Components.RecyclerListView r0 = r6.cameraPhotoRecyclerView
            r0.setVisibility(r13)
            org.telegram.ui.Components.RecyclerListView r0 = r6.cameraPhotoRecyclerView
            r0.setAlpha(r11)
            org.telegram.ui.ActionBar.BottomSheet$ContainerView r0 = r6.container
            org.telegram.ui.Components.RecyclerListView r1 = r6.cameraPhotoRecyclerView
            r2 = 1117782016(0x42a00000, float:80.0)
            r3 = -1
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r3, r2)
            r0.addView(r1, r2)
            org.telegram.ui.Components.ChatAttachAlert$21 r0 = new org.telegram.ui.Components.ChatAttachAlert$21
            r0.<init>(r6, r7, r9, r9)
            r6.cameraPhotoLayoutManager = r0
            org.telegram.ui.Components.RecyclerListView r1 = r6.cameraPhotoRecyclerView
            r1.setLayoutManager(r0)
            org.telegram.ui.Components.RecyclerListView r0 = r6.cameraPhotoRecyclerView
            org.telegram.ui.Components.-$$Lambda$ChatAttachAlert$OLYYKCWVdnhAkqGI5jqHDclcjRc r1 = org.telegram.ui.Components.$$Lambda$ChatAttachAlert$OLYYKCWVdnhAkqGI5jqHDclcjRc.INSTANCE
            r0.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlert.<init>(android.content.Context, org.telegram.ui.ActionBar.BaseFragment):void");
    }

    public /* synthetic */ void lambda$new$0$ChatAttachAlert(int i) {
        this.actionBar.getActionBarMenuOnItemClick().onItemClick(i);
    }

    public /* synthetic */ void lambda$new$1$ChatAttachAlert(View view) {
        this.selectedMenuItem.toggleSubMenu();
    }

    public /* synthetic */ void lambda$new$2$ChatAttachAlert(View view, int i) {
        BaseFragment baseFragment2;
        ChatActivity chatActivity;
        int i2;
        if (this.mediaEnabled && (baseFragment2 = this.baseFragment) != null && baseFragment2.getParentActivity() != null) {
            if (Build.VERSION.SDK_INT >= 23) {
                if (i == 0 && this.noCameraPermissions) {
                    try {
                        this.baseFragment.getParentActivity().requestPermissions(new String[]{"android.permission.CAMERA"}, 18);
                        return;
                    } catch (Exception unused) {
                        return;
                    }
                } else if (this.noGalleryPermissions) {
                    try {
                        this.baseFragment.getParentActivity().requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 4);
                        return;
                    } catch (Exception unused2) {
                        return;
                    }
                }
            }
            if (i != 0 || this.selectedAlbumEntry != this.galleryAlbumEntry) {
                if (this.selectedAlbumEntry == this.galleryAlbumEntry) {
                    i--;
                }
                int i3 = i;
                ArrayList<Object> allPhotosArray = getAllPhotosArray();
                if (i3 >= 0 && i3 < allPhotosArray.size()) {
                    PhotoViewer.getInstance().setParentActivity(this.baseFragment.getParentActivity());
                    PhotoViewer.getInstance().setParentAlert(this);
                    PhotoViewer.getInstance().setMaxSelectedPhotos(this.maxSelectedPhotos, this.allowOrder);
                    BaseFragment baseFragment3 = this.baseFragment;
                    if (baseFragment3 instanceof ChatActivity) {
                        chatActivity = (ChatActivity) baseFragment3;
                        i2 = 0;
                    } else {
                        chatActivity = null;
                        i2 = 4;
                    }
                    PhotoViewer.getInstance().openPhotoForSelect(allPhotosArray, i3, i2, false, this.photoViewerProvider, chatActivity);
                    AndroidUtilities.hideKeyboard(this.baseFragment.getFragmentView().findFocus());
                }
            } else if (SharedConfig.inappCamera) {
                openCamera(true);
            } else {
                ChatAttachViewDelegate chatAttachViewDelegate = this.delegate;
                if (chatAttachViewDelegate != null) {
                    chatAttachViewDelegate.didPressedButton(0, false, true, 0);
                }
            }
        }
    }

    public /* synthetic */ boolean lambda$new$3$ChatAttachAlert(View view, int i) {
        if (i == 0 && this.selectedAlbumEntry == this.galleryAlbumEntry) {
            ChatAttachViewDelegate chatAttachViewDelegate = this.delegate;
            if (chatAttachViewDelegate != null) {
                chatAttachViewDelegate.didPressedButton(0, false, true, 0);
            }
            return true;
        }
        if (view instanceof PhotoAttachPhotoCell) {
            RecyclerViewItemRangeSelector recyclerViewItemRangeSelector = this.itemRangeSelector;
            boolean z = !((PhotoAttachPhotoCell) view).isChecked();
            this.shouldSelect = z;
            recyclerViewItemRangeSelector.setIsActive(view, true, i, z);
        }
        return false;
    }

    public /* synthetic */ void lambda$new$4$ChatAttachAlert(View view) {
        this.dropDownContainer.toggleSubMenu();
    }

    public /* synthetic */ void lambda$new$5$ChatAttachAlert(View view, int i) {
        if (view instanceof AttachButton) {
            this.delegate.didPressedButton(((Integer) ((AttachButton) view).getTag()).intValue(), true, true, 0);
        } else if (view instanceof AttachBotButton) {
            this.delegate.didSelectBot(((AttachBotButton) view).currentUser);
            dismiss();
        }
    }

    public /* synthetic */ boolean lambda$new$7$ChatAttachAlert(View view, int i) {
        if (view instanceof AttachBotButton) {
            AttachBotButton attachBotButton = (AttachBotButton) view;
            if (!(this.baseFragment == null || attachBotButton.currentUser == null)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(LocaleController.getString("AppName", NUM));
                builder.setMessage(LocaleController.formatString("ChatHintsDelete", NUM, ContactsController.formatName(attachBotButton.currentUser.first_name, attachBotButton.currentUser.last_name)));
                builder.setPositiveButton(LocaleController.getString("OK", NUM), new DialogInterface.OnClickListener(attachBotButton) {
                    private final /* synthetic */ ChatAttachAlert.AttachBotButton f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onClick(DialogInterface dialogInterface, int i) {
                        ChatAttachAlert.this.lambda$null$6$ChatAttachAlert(this.f$1, dialogInterface, i);
                    }
                });
                builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                builder.show();
                return true;
            }
        }
        return false;
    }

    public /* synthetic */ void lambda$null$6$ChatAttachAlert(AttachBotButton attachBotButton, DialogInterface dialogInterface, int i) {
        MediaDataController.getInstance(this.currentAccount).removeInline(attachBotButton.currentUser.id);
    }

    public /* synthetic */ void lambda$new$9$ChatAttachAlert(BaseFragment baseFragment2, View view) {
        if (this.editingMessageObject == null && (baseFragment2 instanceof ChatActivity)) {
            ChatActivity chatActivity = (ChatActivity) baseFragment2;
            if (chatActivity.isInScheduleMode()) {
                AlertsCreator.createScheduleDatePickerDialog(getContext(), chatActivity.getDialogId(), new AlertsCreator.ScheduleDatePickerDelegate() {
                    public final void didSelectDate(boolean z, int i) {
                        ChatAttachAlert.this.sendPressed(z, i);
                    }
                });
                return;
            }
        }
        sendPressed(true, 0);
    }

    public /* synthetic */ boolean lambda$new$12$ChatAttachAlert(View view) {
        boolean z;
        View view2 = view;
        BaseFragment baseFragment2 = this.baseFragment;
        if ((baseFragment2 instanceof ChatActivity) && this.editingMessageObject == null) {
            ChatActivity chatActivity = (ChatActivity) baseFragment2;
            chatActivity.getCurrentChat();
            TLRPC$User currentUser = chatActivity.getCurrentUser();
            if (chatActivity.getCurrentEncryptedChat() == null && !chatActivity.isInScheduleMode()) {
                ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(getContext());
                this.sendPopupLayout = actionBarPopupWindowLayout;
                actionBarPopupWindowLayout.setAnimationEnabled(false);
                this.sendPopupLayout.setOnTouchListener(new View.OnTouchListener() {
                    private Rect popupRect = new Rect();

                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        if (motionEvent.getActionMasked() != 0 || ChatAttachAlert.this.sendPopupWindow == null || !ChatAttachAlert.this.sendPopupWindow.isShowing()) {
                            return false;
                        }
                        view.getHitRect(this.popupRect);
                        if (this.popupRect.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
                            return false;
                        }
                        ChatAttachAlert.this.sendPopupWindow.dismiss();
                        return false;
                    }
                });
                this.sendPopupLayout.setDispatchKeyEventListener(new ActionBarPopupWindow.OnDispatchKeyEventListener() {
                    public final void onDispatchKeyEvent(KeyEvent keyEvent) {
                        ChatAttachAlert.this.lambda$null$10$ChatAttachAlert(keyEvent);
                    }
                });
                this.sendPopupLayout.setShowedFromBotton(false);
                this.itemCells = new ActionBarMenuSubItem[2];
                int i = 0;
                for (int i2 = 0; i2 < 2; i2++) {
                    if (i2 == 0) {
                        Iterator<Map.Entry<Object, Object>> it = selectedPhotos.entrySet().iterator();
                        while (true) {
                            if (!it.hasNext()) {
                                z = false;
                                break;
                            }
                            Object value = it.next().getValue();
                            if (!(value instanceof MediaController.PhotoEntry)) {
                                if ((value instanceof MediaController.SearchImage) && ((MediaController.SearchImage) value).ttl != 0) {
                                    break;
                                }
                            } else if (((MediaController.PhotoEntry) value).ttl != 0) {
                                break;
                            }
                        }
                        z = true;
                        if (z) {
                        }
                    } else if (i2 == 1 && UserObject.isUserSelf(currentUser)) {
                    }
                    this.itemCells[i2] = new ActionBarMenuSubItem(getContext());
                    if (i2 == 0) {
                        if (UserObject.isUserSelf(currentUser)) {
                            this.itemCells[i2].setTextAndIcon(LocaleController.getString("SetReminder", NUM), NUM);
                        } else {
                            this.itemCells[i2].setTextAndIcon(LocaleController.getString("ScheduleMessage", NUM), NUM);
                        }
                    } else if (i2 == 1) {
                        this.itemCells[i2].setTextAndIcon(LocaleController.getString("SendWithoutSound", NUM), NUM);
                    }
                    this.itemCells[i2].setMinimumWidth(AndroidUtilities.dp(196.0f));
                    this.sendPopupLayout.addView(this.itemCells[i2], LayoutHelper.createFrame(-1, 48.0f, LocaleController.isRTL ? 5 : 3, 0.0f, (float) (i * 48), 0.0f, 0.0f));
                    this.itemCells[i2].setOnClickListener(new View.OnClickListener(i2, chatActivity) {
                        private final /* synthetic */ int f$1;
                        private final /* synthetic */ ChatActivity f$2;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                        }

                        public final void onClick(View view) {
                            ChatAttachAlert.this.lambda$null$11$ChatAttachAlert(this.f$1, this.f$2, view);
                        }
                    });
                    i++;
                }
                ActionBarPopupWindow actionBarPopupWindow = new ActionBarPopupWindow(this.sendPopupLayout, -2, -2);
                this.sendPopupWindow = actionBarPopupWindow;
                actionBarPopupWindow.setAnimationEnabled(false);
                this.sendPopupWindow.setAnimationStyle(NUM);
                this.sendPopupWindow.setOutsideTouchable(true);
                this.sendPopupWindow.setClippingEnabled(true);
                this.sendPopupWindow.setInputMethodMode(2);
                this.sendPopupWindow.setSoftInputMode(0);
                this.sendPopupWindow.getContentView().setFocusableInTouchMode(true);
                this.sendPopupLayout.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE));
                this.sendPopupWindow.setFocusable(true);
                int[] iArr = new int[2];
                view2.getLocationInWindow(iArr);
                this.sendPopupWindow.showAtLocation(view2, 51, ((iArr[0] + view.getMeasuredWidth()) - this.sendPopupLayout.getMeasuredWidth()) + AndroidUtilities.dp(8.0f), (iArr[1] - this.sendPopupLayout.getMeasuredHeight()) - AndroidUtilities.dp(2.0f));
                this.sendPopupWindow.dimBehind();
                view2.performHapticFeedback(3, 2);
            }
        }
        return false;
    }

    public /* synthetic */ void lambda$null$10$ChatAttachAlert(KeyEvent keyEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0 && (actionBarPopupWindow = this.sendPopupWindow) != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
    }

    public /* synthetic */ void lambda$null$11$ChatAttachAlert(int i, ChatActivity chatActivity, View view) {
        ActionBarPopupWindow actionBarPopupWindow = this.sendPopupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
        if (i == 0) {
            AlertsCreator.createScheduleDatePickerDialog(getContext(), chatActivity.getDialogId(), new AlertsCreator.ScheduleDatePickerDelegate() {
                public final void didSelectDate(boolean z, int i) {
                    ChatAttachAlert.this.sendPressed(z, i);
                }
            });
        } else if (i == 1) {
            sendPressed(false, 0);
        }
    }

    public /* synthetic */ void lambda$new$13$ChatAttachAlert(View view) {
        if (this.cameraView != null) {
            openPhotoViewer((MediaController.PhotoEntry) null, false, false);
            CameraController.getInstance().stopPreview(this.cameraView.getCameraSession());
        }
    }

    public /* synthetic */ void lambda$new$14$ChatAttachAlert(float f) {
        CameraView cameraView2 = this.cameraView;
        if (cameraView2 != null) {
            this.cameraZoom = f;
            cameraView2.setZoom(f);
        }
        showZoomControls(true, true);
    }

    public /* synthetic */ void lambda$new$15$ChatAttachAlert(View view) {
        CameraView cameraView2;
        if (!this.takingPhoto && (cameraView2 = this.cameraView) != null && cameraView2.isInitied()) {
            this.canSaveCameraPreview = false;
            this.cameraView.switchCamera();
            ObjectAnimator duration = ObjectAnimator.ofFloat(this.switchCameraButton, View.SCALE_X, new float[]{0.0f}).setDuration(100);
            duration.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    ChatAttachAlert.this.switchCameraButton.setImageResource((ChatAttachAlert.this.cameraView == null || !ChatAttachAlert.this.cameraView.isFrontface()) ? NUM : NUM);
                    ObjectAnimator.ofFloat(ChatAttachAlert.this.switchCameraButton, View.SCALE_X, new float[]{1.0f}).setDuration(100).start();
                }
            });
            duration.start();
        }
    }

    public /* synthetic */ void lambda$new$16$ChatAttachAlert(final View view) {
        CameraView cameraView2;
        if (!this.flashAnimationInProgress && (cameraView2 = this.cameraView) != null && cameraView2.isInitied() && this.cameraOpened) {
            String currentFlashMode = this.cameraView.getCameraSession().getCurrentFlashMode();
            String nextFlashMode = this.cameraView.getCameraSession().getNextFlashMode();
            if (!currentFlashMode.equals(nextFlashMode)) {
                this.cameraView.getCameraSession().setCurrentFlashMode(nextFlashMode);
                this.flashAnimationInProgress = true;
                ImageView[] imageViewArr = this.flashModeButton;
                final ImageView imageView = imageViewArr[0] == view ? imageViewArr[1] : imageViewArr[0];
                imageView.setVisibility(0);
                setCameraFlashModeIcon(imageView, nextFlashMode);
                AnimatorSet animatorSet2 = new AnimatorSet();
                animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, new float[]{0.0f, (float) AndroidUtilities.dp(48.0f)}), ObjectAnimator.ofFloat(imageView, View.TRANSLATION_Y, new float[]{(float) (-AndroidUtilities.dp(48.0f)), 0.0f}), ObjectAnimator.ofFloat(view, View.ALPHA, new float[]{1.0f, 0.0f}), ObjectAnimator.ofFloat(imageView, View.ALPHA, new float[]{0.0f, 1.0f})});
                animatorSet2.setDuration(200);
                animatorSet2.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        boolean unused = ChatAttachAlert.this.flashAnimationInProgress = false;
                        view.setVisibility(4);
                        imageView.sendAccessibilityEvent(8);
                    }
                });
                animatorSet2.start();
            }
        }
    }

    static /* synthetic */ void lambda$new$17(View view, int i) {
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
            if (messageObject != null) {
                this.maxSelectedPhotos = 1;
                this.allowOrder = false;
            } else {
                this.maxSelectedPhotos = -1;
                this.allowOrder = true;
            }
            this.buttonsAdapter.notifyDataSetChanged();
        }
    }

    public MessageObject getEditingMessageObject() {
        return this.editingMessageObject;
    }

    /* access modifiers changed from: private */
    public void applyCaption() {
        if (this.commentTextView.length() > 0) {
            Object obj = selectedPhotos.get(Integer.valueOf(((Integer) selectedPhotosOrder.get(0)).intValue()));
            if (obj instanceof MediaController.PhotoEntry) {
                ((MediaController.PhotoEntry) obj).caption = this.commentTextView.getText().toString();
            } else if (obj instanceof MediaController.SearchImage) {
                ((MediaController.SearchImage) obj).caption = this.commentTextView.getText().toString();
            }
        }
    }

    /* access modifiers changed from: private */
    public void sendPressed(boolean z, int i) {
        if (!this.buttonPressed) {
            BaseFragment baseFragment2 = this.baseFragment;
            if (baseFragment2 instanceof ChatActivity) {
                ChatActivity chatActivity = (ChatActivity) baseFragment2;
                TLRPC$Chat currentChat = chatActivity.getCurrentChat();
                if (chatActivity.getCurrentUser() != null || ((ChatObject.isChannel(currentChat) && currentChat.megagroup) || !ChatObject.isChannel(currentChat))) {
                    SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(this.currentAccount).edit();
                    edit.putBoolean("silent_" + chatActivity.getDialogId(), !z).commit();
                }
            }
            applyCaption();
            this.buttonPressed = true;
            this.delegate.didPressedButton(7, true, z, i);
        }
    }

    /* access modifiers changed from: private */
    public void updatePhotosCounter(boolean z) {
        if (this.counterTextView != null) {
            boolean z2 = false;
            boolean z3 = false;
            for (Map.Entry<Object, Object> value : selectedPhotos.entrySet()) {
                if (((MediaController.PhotoEntry) value.getValue()).isVideo) {
                    z2 = true;
                } else {
                    z3 = true;
                }
                if (z2 && z3) {
                    break;
                }
            }
            int max = Math.max(1, selectedPhotos.size());
            if (z2 && z3) {
                this.counterTextView.setText(LocaleController.formatPluralString("Media", selectedPhotos.size()).toUpperCase());
                if (max != this.currentSelectedCount || z) {
                    this.selectedTextView.setText(LocaleController.formatPluralString("MediaSelected", max));
                }
            } else if (z2) {
                this.counterTextView.setText(LocaleController.formatPluralString("Videos", selectedPhotos.size()).toUpperCase());
                if (max != this.currentSelectedCount || z) {
                    this.selectedTextView.setText(LocaleController.formatPluralString("VideosSelected", max));
                }
            } else {
                this.counterTextView.setText(LocaleController.formatPluralString("Photos", selectedPhotos.size()).toUpperCase());
                if (max != this.currentSelectedCount || z) {
                    this.selectedTextView.setText(LocaleController.formatPluralString("PhotosSelected", max));
                }
            }
            this.currentSelectedCount = max;
        }
    }

    private boolean showCommentTextView(final boolean z, boolean z2) {
        if (z == (this.frameLayout2.getTag() != null)) {
            return false;
        }
        AnimatorSet animatorSet2 = this.animatorSet;
        if (animatorSet2 != null) {
            animatorSet2.cancel();
        }
        this.frameLayout2.setTag(z ? 1 : null);
        if (this.commentTextView.getEditText().isFocused()) {
            AndroidUtilities.hideKeyboard(this.commentTextView.getEditText());
        }
        this.commentTextView.hidePopup(true);
        if (z) {
            this.frameLayout2.setVisibility(0);
            this.writeButtonContainer.setVisibility(0);
        }
        float f = 0.2f;
        float f2 = 0.0f;
        if (z2) {
            this.animatorSet = new AnimatorSet();
            ArrayList arrayList = new ArrayList();
            FrameLayout frameLayout = this.frameLayout2;
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            fArr[0] = z ? 1.0f : 0.0f;
            arrayList.add(ObjectAnimator.ofFloat(frameLayout, property, fArr));
            FrameLayout frameLayout3 = this.writeButtonContainer;
            Property property2 = View.SCALE_X;
            float[] fArr2 = new float[1];
            fArr2[0] = z ? 1.0f : 0.2f;
            arrayList.add(ObjectAnimator.ofFloat(frameLayout3, property2, fArr2));
            FrameLayout frameLayout4 = this.writeButtonContainer;
            Property property3 = View.SCALE_Y;
            float[] fArr3 = new float[1];
            fArr3[0] = z ? 1.0f : 0.2f;
            arrayList.add(ObjectAnimator.ofFloat(frameLayout4, property3, fArr3));
            FrameLayout frameLayout5 = this.writeButtonContainer;
            Property property4 = View.ALPHA;
            float[] fArr4 = new float[1];
            fArr4[0] = z ? 1.0f : 0.0f;
            arrayList.add(ObjectAnimator.ofFloat(frameLayout5, property4, fArr4));
            View view = this.selectedCountView;
            Property property5 = View.SCALE_X;
            float[] fArr5 = new float[1];
            fArr5[0] = z ? 1.0f : 0.2f;
            arrayList.add(ObjectAnimator.ofFloat(view, property5, fArr5));
            View view2 = this.selectedCountView;
            Property property6 = View.SCALE_Y;
            float[] fArr6 = new float[1];
            if (z) {
                f = 1.0f;
            }
            fArr6[0] = f;
            arrayList.add(ObjectAnimator.ofFloat(view2, property6, fArr6));
            View view3 = this.selectedCountView;
            Property property7 = View.ALPHA;
            float[] fArr7 = new float[1];
            fArr7[0] = z ? 1.0f : 0.0f;
            arrayList.add(ObjectAnimator.ofFloat(view3, property7, fArr7));
            if (this.actionBar.getTag() != null) {
                FrameLayout frameLayout6 = this.frameLayout2;
                Property property8 = View.TRANSLATION_Y;
                float[] fArr8 = new float[1];
                fArr8[0] = z ? 0.0f : (float) AndroidUtilities.dp(48.0f);
                arrayList.add(ObjectAnimator.ofFloat(frameLayout6, property8, fArr8));
                View view4 = this.shadow;
                Property property9 = View.TRANSLATION_Y;
                float[] fArr9 = new float[1];
                fArr9[0] = (float) (z ? AndroidUtilities.dp(44.0f) : AndroidUtilities.dp(92.0f));
                arrayList.add(ObjectAnimator.ofFloat(view4, property9, fArr9));
                View view5 = this.shadow;
                Property property10 = View.ALPHA;
                float[] fArr10 = new float[1];
                if (z) {
                    f2 = 1.0f;
                }
                fArr10[0] = f2;
                arrayList.add(ObjectAnimator.ofFloat(view5, property10, fArr10));
            } else {
                RecyclerListView recyclerListView = this.buttonsRecyclerView;
                Property property11 = View.TRANSLATION_Y;
                float[] fArr11 = new float[1];
                fArr11[0] = z ? (float) AndroidUtilities.dp(44.0f) : 0.0f;
                arrayList.add(ObjectAnimator.ofFloat(recyclerListView, property11, fArr11));
                View view6 = this.shadow;
                Property property12 = View.TRANSLATION_Y;
                float[] fArr12 = new float[1];
                if (z) {
                    f2 = (float) AndroidUtilities.dp(44.0f);
                }
                fArr12[0] = f2;
                arrayList.add(ObjectAnimator.ofFloat(view6, property12, fArr12));
            }
            this.animatorSet.playTogether(arrayList);
            this.animatorSet.setInterpolator(new DecelerateInterpolator());
            this.animatorSet.setDuration(180);
            this.animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (animator.equals(ChatAttachAlert.this.animatorSet)) {
                        if (!z) {
                            ChatAttachAlert.this.frameLayout2.setVisibility(4);
                            ChatAttachAlert.this.writeButtonContainer.setVisibility(4);
                        }
                        AnimatorSet unused = ChatAttachAlert.this.animatorSet = null;
                    }
                }

                public void onAnimationCancel(Animator animator) {
                    if (animator.equals(ChatAttachAlert.this.animatorSet)) {
                        AnimatorSet unused = ChatAttachAlert.this.animatorSet = null;
                    }
                }
            });
            this.animatorSet.start();
        } else {
            this.frameLayout2.setAlpha(z ? 1.0f : 0.0f);
            this.writeButtonContainer.setScaleX(z ? 1.0f : 0.2f);
            this.writeButtonContainer.setScaleY(z ? 1.0f : 0.2f);
            this.writeButtonContainer.setAlpha(z ? 1.0f : 0.0f);
            this.selectedCountView.setScaleX(z ? 1.0f : 0.2f);
            View view7 = this.selectedCountView;
            if (z) {
                f = 1.0f;
            }
            view7.setScaleY(f);
            this.selectedCountView.setAlpha(z ? 1.0f : 0.0f);
            if (this.actionBar.getTag() != null) {
                this.frameLayout2.setTranslationY(z ? 0.0f : (float) AndroidUtilities.dp(48.0f));
                this.shadow.setTranslationY((float) (z ? AndroidUtilities.dp(44.0f) : AndroidUtilities.dp(92.0f)));
                View view8 = this.shadow;
                if (z) {
                    f2 = 1.0f;
                }
                view8.setAlpha(f2);
            } else {
                this.buttonsRecyclerView.setTranslationY(z ? (float) AndroidUtilities.dp(44.0f) : 0.0f);
                View view9 = this.shadow;
                if (z) {
                    f2 = (float) AndroidUtilities.dp(44.0f);
                }
                view9.setTranslationY(f2);
            }
            if (!z) {
                this.frameLayout2.setVisibility(4);
                this.writeButtonContainer.setVisibility(4);
            }
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean onCustomOpenAnimation() {
        AnimatorSet animatorSet2 = new AnimatorSet();
        animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, this.ATTACH_ALERT_PROGRESS, new float[]{0.0f, 400.0f})});
        animatorSet2.setDuration(400);
        animatorSet2.setStartDelay(20);
        animatorSet2.start();
        return false;
    }

    /* access modifiers changed from: private */
    public void openPhotoViewer(MediaController.PhotoEntry photoEntry, final boolean z, boolean z2) {
        ChatActivity chatActivity;
        int i;
        if (photoEntry != null) {
            cameraPhotos.add(photoEntry);
            selectedPhotos.put(Integer.valueOf(photoEntry.imageId), photoEntry);
            selectedPhotosOrder.add(Integer.valueOf(photoEntry.imageId));
            updatePhotosButton(0);
            this.adapter.notifyDataSetChanged();
            this.cameraAttachAdapter.notifyDataSetChanged();
        }
        if (photoEntry != null && !z2 && cameraPhotos.size() > 1) {
            updatePhotosCounter(false);
            if (this.cameraView != null) {
                this.zoomControlView.setZoom(0.0f, false);
                this.cameraZoom = 0.0f;
                this.cameraView.setZoom(0.0f);
                CameraController.getInstance().startPreview(this.cameraView.getCameraSession());
            }
        } else if (!cameraPhotos.isEmpty()) {
            this.cancelTakingPhotos = true;
            PhotoViewer.getInstance().setParentActivity(this.baseFragment.getParentActivity());
            PhotoViewer.getInstance().setParentAlert(this);
            PhotoViewer.getInstance().setMaxSelectedPhotos(this.maxSelectedPhotos, this.allowOrder);
            BaseFragment baseFragment2 = this.baseFragment;
            if (baseFragment2 instanceof ChatActivity) {
                chatActivity = (ChatActivity) baseFragment2;
                i = 2;
            } else {
                chatActivity = null;
                i = 5;
            }
            PhotoViewer.getInstance().openPhotoForSelect(getAllPhotosArray(), cameraPhotos.size() - 1, i, false, new BasePhotoProvider() {
                public boolean canScrollAway() {
                    return false;
                }

                public ImageReceiver.BitmapHolder getThumbForPhoto(MessageObject messageObject, TLRPC$FileLocation tLRPC$FileLocation, int i) {
                    return null;
                }

                public boolean cancelButtonPressed() {
                    if (ChatAttachAlert.this.cameraOpened && ChatAttachAlert.this.cameraView != null) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public final void run() {
                                ChatAttachAlert.AnonymousClass24.this.lambda$cancelButtonPressed$0$ChatAttachAlert$24();
                            }
                        }, 1000);
                        ChatAttachAlert.this.zoomControlView.setZoom(0.0f, false);
                        float unused = ChatAttachAlert.this.cameraZoom = 0.0f;
                        ChatAttachAlert.this.cameraView.setZoom(0.0f);
                        CameraController.getInstance().startPreview(ChatAttachAlert.this.cameraView.getCameraSession());
                    }
                    if (ChatAttachAlert.this.cancelTakingPhotos && ChatAttachAlert.cameraPhotos.size() == 1) {
                        int size = ChatAttachAlert.cameraPhotos.size();
                        for (int i = 0; i < size; i++) {
                            MediaController.PhotoEntry photoEntry = (MediaController.PhotoEntry) ChatAttachAlert.cameraPhotos.get(i);
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
                        ChatAttachAlert.this.adapter.notifyDataSetChanged();
                        ChatAttachAlert.this.cameraAttachAdapter.notifyDataSetChanged();
                        ChatAttachAlert.this.updatePhotosButton(0);
                    }
                    return true;
                }

                public /* synthetic */ void lambda$cancelButtonPressed$0$ChatAttachAlert$24() {
                    if (ChatAttachAlert.this.cameraView != null && !ChatAttachAlert.this.isDismissed() && Build.VERSION.SDK_INT >= 21) {
                        ChatAttachAlert.this.cameraView.setSystemUiVisibility(1028);
                    }
                }

                public void needAddMorePhotos() {
                    boolean unused = ChatAttachAlert.this.cancelTakingPhotos = false;
                    if (ChatAttachAlert.mediaFromExternalCamera) {
                        ChatAttachAlert.this.delegate.didPressedButton(0, true, true, 0);
                        return;
                    }
                    if (!ChatAttachAlert.this.cameraOpened) {
                        ChatAttachAlert.this.openCamera(false);
                    }
                    ChatAttachAlert.this.counterTextView.setVisibility(0);
                    ChatAttachAlert.this.cameraPhotoRecyclerView.setVisibility(0);
                    ChatAttachAlert.this.counterTextView.setAlpha(1.0f);
                    ChatAttachAlert.this.updatePhotosCounter(false);
                }

                public void sendButtonPressed(int i, VideoEditedInfo videoEditedInfo, boolean z, int i2) {
                    if (!ChatAttachAlert.cameraPhotos.isEmpty() && ChatAttachAlert.this.baseFragment != null) {
                        if (videoEditedInfo != null && i >= 0 && i < ChatAttachAlert.cameraPhotos.size()) {
                            ((MediaController.PhotoEntry) ChatAttachAlert.cameraPhotos.get(i)).editedInfo = videoEditedInfo;
                        }
                        if (!(ChatAttachAlert.this.baseFragment instanceof ChatActivity) || !((ChatActivity) ChatAttachAlert.this.baseFragment).isSecretChat()) {
                            int size = ChatAttachAlert.cameraPhotos.size();
                            for (int i3 = 0; i3 < size; i3++) {
                                AndroidUtilities.addMediaToGallery(((MediaController.PhotoEntry) ChatAttachAlert.cameraPhotos.get(i3)).path);
                            }
                        }
                        ChatAttachAlert.this.applyCaption();
                        ChatAttachAlert.this.delegate.didPressedButton(8, true, z, i2);
                        ChatAttachAlert.cameraPhotos.clear();
                        ChatAttachAlert.selectedPhotosOrder.clear();
                        ChatAttachAlert.selectedPhotos.clear();
                        ChatAttachAlert.this.adapter.notifyDataSetChanged();
                        ChatAttachAlert.this.cameraAttachAdapter.notifyDataSetChanged();
                        ChatAttachAlert.this.closeCamera(false);
                        ChatAttachAlert.this.dismiss();
                    }
                }

                public boolean scaleToFill() {
                    if (ChatAttachAlert.this.baseFragment == null || ChatAttachAlert.this.baseFragment.getParentActivity() == null) {
                        return false;
                    }
                    int i = Settings.System.getInt(ChatAttachAlert.this.baseFragment.getParentActivity().getContentResolver(), "accelerometer_rotation", 0);
                    if (z || i == 1) {
                        return true;
                    }
                    return false;
                }

                public void willHidePhotoViewer() {
                    int childCount = ChatAttachAlert.this.gridView.getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        View childAt = ChatAttachAlert.this.gridView.getChildAt(i);
                        if (childAt instanceof PhotoAttachPhotoCell) {
                            PhotoAttachPhotoCell photoAttachPhotoCell = (PhotoAttachPhotoCell) childAt;
                            photoAttachPhotoCell.showImage();
                            photoAttachPhotoCell.showCheck(true);
                        }
                    }
                }

                public boolean canCaptureMorePhotos() {
                    return ChatAttachAlert.this.maxSelectedPhotos != 1;
                }
            }, chatActivity);
        }
    }

    /* access modifiers changed from: private */
    public void showZoomControls(boolean z, boolean z2) {
        if ((this.zoomControlView.getTag() == null || !z) && (this.zoomControlView.getTag() != null || z)) {
            AnimatorSet animatorSet2 = this.zoomControlAnimation;
            if (animatorSet2 != null) {
                animatorSet2.cancel();
            }
            this.zoomControlView.setTag(z ? 1 : null);
            AnimatorSet animatorSet3 = new AnimatorSet();
            this.zoomControlAnimation = animatorSet3;
            animatorSet3.setDuration(180);
            AnimatorSet animatorSet4 = this.zoomControlAnimation;
            Animator[] animatorArr = new Animator[1];
            ZoomControlView zoomControlView2 = this.zoomControlView;
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            fArr[0] = z ? 1.0f : 0.0f;
            animatorArr[0] = ObjectAnimator.ofFloat(zoomControlView2, property, fArr);
            animatorSet4.playTogether(animatorArr);
            this.zoomControlAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    AnimatorSet unused = ChatAttachAlert.this.zoomControlAnimation = null;
                }
            });
            this.zoomControlAnimation.start();
            if (z) {
                $$Lambda$ChatAttachAlert$mV_BEXMPLX1sbq26UDe3z87sEc r9 = new Runnable() {
                    public final void run() {
                        ChatAttachAlert.this.lambda$showZoomControls$19$ChatAttachAlert();
                    }
                };
                this.zoomControlHideRunnable = r9;
                AndroidUtilities.runOnUIThread(r9, 2000);
            }
        } else if (z) {
            Runnable runnable = this.zoomControlHideRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
            }
            $$Lambda$ChatAttachAlert$_l7PQyk8jaRFbCZvFg6m2VtIR4I r92 = new Runnable() {
                public final void run() {
                    ChatAttachAlert.this.lambda$showZoomControls$18$ChatAttachAlert();
                }
            };
            this.zoomControlHideRunnable = r92;
            AndroidUtilities.runOnUIThread(r92, 2000);
        }
    }

    public /* synthetic */ void lambda$showZoomControls$18$ChatAttachAlert() {
        showZoomControls(false, true);
        this.zoomControlHideRunnable = null;
    }

    public /* synthetic */ void lambda$showZoomControls$19$ChatAttachAlert() {
        showZoomControls(false, true);
        this.zoomControlHideRunnable = null;
    }

    /* access modifiers changed from: private */
    public boolean processTouchEvent(MotionEvent motionEvent) {
        CameraView cameraView2;
        if (motionEvent == null) {
            return false;
        }
        if ((!this.pressed && motionEvent.getActionMasked() == 0) || motionEvent.getActionMasked() == 5) {
            this.zoomControlView.getHitRect(this.hitRect);
            if (this.zoomControlView.getTag() != null && this.hitRect.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
                return false;
            }
            if (!this.takingPhoto && !this.dragging) {
                if (motionEvent.getPointerCount() == 2) {
                    this.pinchStartDistance = (float) Math.hypot((double) (motionEvent.getX(1) - motionEvent.getX(0)), (double) (motionEvent.getY(1) - motionEvent.getY(0)));
                    this.zooming = true;
                } else {
                    this.maybeStartDraging = true;
                    this.lastY = motionEvent.getY();
                    this.zooming = false;
                }
                this.zoomWas = false;
                this.pressed = true;
            }
        } else if (this.pressed) {
            if (motionEvent.getActionMasked() == 2) {
                if (!this.zooming || motionEvent.getPointerCount() != 2 || this.dragging) {
                    float y = motionEvent.getY();
                    float f = y - this.lastY;
                    if (this.maybeStartDraging) {
                        if (Math.abs(f) > AndroidUtilities.getPixelsInCM(0.4f, false)) {
                            this.maybeStartDraging = false;
                            this.dragging = true;
                        }
                    } else if (this.dragging && (cameraView2 = this.cameraView) != null) {
                        cameraView2.setTranslationY(cameraView2.getTranslationY() + f);
                        this.lastY = y;
                        this.zoomControlView.setTag((Object) null);
                        Runnable runnable = this.zoomControlHideRunnable;
                        if (runnable != null) {
                            AndroidUtilities.cancelRunOnUIThread(runnable);
                            this.zoomControlHideRunnable = null;
                        }
                        if (this.cameraPanel.getTag() == null) {
                            this.cameraPanel.setTag(1);
                            AnimatorSet animatorSet2 = new AnimatorSet();
                            animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.cameraPanel, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.zoomControlView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.counterTextView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.flashModeButton[0], View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.flashModeButton[1], View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.cameraPhotoRecyclerView, View.ALPHA, new float[]{0.0f})});
                            animatorSet2.setDuration(200);
                            animatorSet2.start();
                        }
                    }
                } else {
                    float hypot = (float) Math.hypot((double) (motionEvent.getX(1) - motionEvent.getX(0)), (double) (motionEvent.getY(1) - motionEvent.getY(0)));
                    if (!this.zoomWas) {
                        if (Math.abs(hypot - this.pinchStartDistance) >= AndroidUtilities.getPixelsInCM(0.4f, false)) {
                            this.pinchStartDistance = hypot;
                            this.zoomWas = true;
                        }
                    } else if (this.cameraView != null) {
                        this.pinchStartDistance = hypot;
                        float dp = this.cameraZoom + ((hypot - this.pinchStartDistance) / ((float) AndroidUtilities.dp(100.0f)));
                        this.cameraZoom = dp;
                        if (dp < 0.0f) {
                            this.cameraZoom = 0.0f;
                        } else if (dp > 1.0f) {
                            this.cameraZoom = 1.0f;
                        }
                        this.zoomControlView.setZoom(this.cameraZoom, false);
                        this.containerView.invalidate();
                        this.cameraView.setZoom(this.cameraZoom);
                        showZoomControls(true, true);
                    }
                }
            } else if (motionEvent.getActionMasked() == 3 || motionEvent.getActionMasked() == 1 || motionEvent.getActionMasked() == 6) {
                this.pressed = false;
                this.zooming = false;
                if (0 != 0) {
                    this.zooming = false;
                } else if (this.dragging) {
                    this.dragging = false;
                    CameraView cameraView3 = this.cameraView;
                    if (cameraView3 != null) {
                        if (Math.abs(cameraView3.getTranslationY()) > ((float) this.cameraView.getMeasuredHeight()) / 6.0f) {
                            closeCamera(true);
                        } else {
                            AnimatorSet animatorSet3 = new AnimatorSet();
                            animatorSet3.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.cameraView, View.TRANSLATION_Y, new float[]{0.0f}), ObjectAnimator.ofFloat(this.cameraPanel, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.counterTextView, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.flashModeButton[0], View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.flashModeButton[1], View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.cameraPhotoRecyclerView, View.ALPHA, new float[]{1.0f})});
                            animatorSet3.setDuration(250);
                            animatorSet3.setInterpolator(this.interpolator);
                            animatorSet3.start();
                            this.cameraPanel.setTag((Object) null);
                        }
                    }
                } else {
                    CameraView cameraView4 = this.cameraView;
                    if (cameraView4 != null && !this.zoomWas) {
                        cameraView4.getLocationOnScreen(this.viewPosition);
                        this.cameraView.focusToPoint((int) (motionEvent.getRawX() - ((float) this.viewPosition[0])), (int) (motionEvent.getRawY() - ((float) this.viewPosition[1])));
                    }
                }
            }
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean onContainerTouchEvent(MotionEvent motionEvent) {
        return this.cameraOpened && processTouchEvent(motionEvent);
    }

    /* access modifiers changed from: private */
    public void applyAttachButtonColors(View view) {
        if (view instanceof AttachButton) {
            ((AttachButton) view).textView.setTextColor(Theme.getColor("dialogTextGray2"));
        } else if (view instanceof AttachBotButton) {
            ((AttachBotButton) view).nameTextView.setTextColor(Theme.getColor("dialogTextGray2"));
        }
    }

    public void checkColors() {
        RecyclerListView recyclerListView = this.buttonsRecyclerView;
        if (recyclerListView != null) {
            int childCount = recyclerListView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                applyAttachButtonColors(this.buttonsRecyclerView.getChildAt(i));
            }
            this.selectedTextView.setTextColor(Theme.getColor("dialogTextBlack"));
            this.selectedMenuItem.setIconColor(Theme.getColor("dialogTextBlack"));
            Theme.setDrawableColor(this.selectedMenuItem.getBackground(), Theme.getColor("dialogButtonSelector"));
            this.selectedMenuItem.setPopupItemsColor(Theme.getColor("actionBarDefaultSubmenuItem"), false);
            this.selectedMenuItem.setPopupItemsColor(Theme.getColor("actionBarDefaultSubmenuItem"), true);
            this.selectedMenuItem.redrawPopup(Theme.getColor("actionBarDefaultSubmenuBackground"));
            this.commentTextView.updateColors();
            if (this.sendPopupLayout != null) {
                int i2 = 0;
                while (true) {
                    ActionBarMenuSubItem[] actionBarMenuSubItemArr = this.itemCells;
                    if (i2 >= actionBarMenuSubItemArr.length) {
                        break;
                    }
                    if (actionBarMenuSubItemArr[i2] != null) {
                        actionBarMenuSubItemArr[i2].setColors(Theme.getColor("actionBarDefaultSubmenuItem"), Theme.getColor("actionBarDefaultSubmenuItemIcon"));
                        this.itemCells[i2].setSelectorColor(Theme.getColor("dialogButtonSelector"));
                    }
                    i2++;
                }
                this.sendPopupLayout.setBackgroundColor(Theme.getColor("actionBarDefaultSubmenuBackground"));
                ActionBarPopupWindow actionBarPopupWindow = this.sendPopupWindow;
                if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
                    this.sendPopupLayout.invalidate();
                }
            }
            String str = "dialogFloatingButton";
            Theme.setSelectorDrawableColor(this.writeButtonDrawable, Theme.getColor(str), false);
            Drawable drawable = this.writeButtonDrawable;
            if (Build.VERSION.SDK_INT >= 21) {
                str = "dialogFloatingButtonPressed";
            }
            Theme.setSelectorDrawableColor(drawable, Theme.getColor(str), true);
            this.writeButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogFloatingIcon"), PorterDuff.Mode.MULTIPLY));
            this.dropDown.setTextColor(Theme.getColor("dialogTextBlack"));
            this.dropDownContainer.setPopupItemsColor(Theme.getColor("actionBarDefaultSubmenuItem"), false);
            this.dropDownContainer.setPopupItemsColor(Theme.getColor("actionBarDefaultSubmenuItem"), true);
            this.dropDownContainer.redrawPopup(Theme.getColor("actionBarDefaultSubmenuBackground"));
            this.actionBarShadow.setBackgroundColor(Theme.getColor("dialogShadowLine"));
            this.progressView.setTextColor(Theme.getColor("emptyListPlaceholder"));
            this.buttonsRecyclerView.setGlowColor(Theme.getColor("dialogScrollGlow"));
            this.buttonsRecyclerView.setBackgroundColor(Theme.getColor("dialogBackground"));
            this.frameLayout2.setBackgroundColor(Theme.getColor("dialogBackground"));
            this.selectedCountView.invalidate();
            Theme.setDrawableColor(this.dropDownDrawable, Theme.getColor("dialogTextBlack"));
            this.actionBar.setBackgroundColor(Theme.getColor("dialogBackground"));
            this.actionBar.setItemsColor(Theme.getColor("dialogTextBlack"), false);
            this.actionBar.setItemsBackgroundColor(Theme.getColor("dialogButtonSelector"), false);
            this.actionBar.setTitleColor(Theme.getColor("dialogTextBlack"));
            Theme.setDrawableColor(this.shadowDrawable, Theme.getColor("dialogBackground"));
            Theme.setDrawableColor(this.cameraDrawable, Theme.getColor("dialogCameraIcon"));
            FrameLayout frameLayout = this.cameraIcon;
            if (frameLayout != null) {
                frameLayout.invalidate();
            }
            this.gridView.setGlowColor(Theme.getColor("dialogScrollGlow"));
            RecyclerView.ViewHolder findViewHolderForAdapterPosition = this.gridView.findViewHolderForAdapterPosition(0);
            if (findViewHolderForAdapterPosition != null) {
                View view = findViewHolderForAdapterPosition.itemView;
                if (view instanceof PhotoAttachCameraCell) {
                    ((PhotoAttachCameraCell) view).getImageView().setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogCameraIcon"), PorterDuff.Mode.MULTIPLY));
                }
            }
            this.containerView.invalidate();
        }
    }

    /* access modifiers changed from: private */
    public void resetRecordState() {
        if (this.baseFragment != null) {
            for (int i = 0; i < 2; i++) {
                this.flashModeButton[i].setAlpha(1.0f);
            }
            this.switchCameraButton.setAlpha(1.0f);
            this.tooltipTextView.setAlpha(1.0f);
            this.recordTime.setAlpha(0.0f);
            AndroidUtilities.cancelRunOnUIThread(this.videoRecordRunnable);
            this.videoRecordRunnable = null;
            AndroidUtilities.unlockOrientation(this.baseFragment.getParentActivity());
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x0036  */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x0061  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setCameraFlashModeIcon(android.widget.ImageView r5, java.lang.String r6) {
        /*
            r4 = this;
            int r0 = r6.hashCode()
            r1 = 3551(0xddf, float:4.976E-42)
            r2 = 2
            r3 = 1
            if (r0 == r1) goto L_0x0029
            r1 = 109935(0x1ad6f, float:1.54052E-40)
            if (r0 == r1) goto L_0x001f
            r1 = 3005871(0x2dddaf, float:4.212122E-39)
            if (r0 == r1) goto L_0x0015
            goto L_0x0033
        L_0x0015:
            java.lang.String r0 = "auto"
            boolean r6 = r6.equals(r0)
            if (r6 == 0) goto L_0x0033
            r6 = 2
            goto L_0x0034
        L_0x001f:
            java.lang.String r0 = "off"
            boolean r6 = r6.equals(r0)
            if (r6 == 0) goto L_0x0033
            r6 = 0
            goto L_0x0034
        L_0x0029:
            java.lang.String r0 = "on"
            boolean r6 = r6.equals(r0)
            if (r6 == 0) goto L_0x0033
            r6 = 1
            goto L_0x0034
        L_0x0033:
            r6 = -1
        L_0x0034:
            if (r6 == 0) goto L_0x0061
            if (r6 == r3) goto L_0x004e
            if (r6 == r2) goto L_0x003b
            goto L_0x0073
        L_0x003b:
            r6 = 2131165385(0x7var_c9, float:1.7944986E38)
            r5.setImageResource(r6)
            r6 = 2131623954(0x7f0e0012, float:1.8875074E38)
            java.lang.String r0 = "AccDescrCameraFlashAuto"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r0, r6)
            r5.setContentDescription(r6)
            goto L_0x0073
        L_0x004e:
            r6 = 2131165387(0x7var_cb, float:1.794499E38)
            r5.setImageResource(r6)
            r6 = 2131623956(0x7f0e0014, float:1.8875078E38)
            java.lang.String r0 = "AccDescrCameraFlashOn"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r0, r6)
            r5.setContentDescription(r6)
            goto L_0x0073
        L_0x0061:
            r6 = 2131165386(0x7var_ca, float:1.7944988E38)
            r5.setImageResource(r6)
            r6 = 2131623955(0x7f0e0013, float:1.8875076E38)
            java.lang.String r0 = "AccDescrCameraFlashOff"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r0, r6)
            r5.setContentDescription(r6)
        L_0x0073:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlert.setCameraFlashModeIcon(android.widget.ImageView, java.lang.String):void");
    }

    /* access modifiers changed from: protected */
    public boolean onCustomMeasure(View view, int i, int i2) {
        boolean z = i < i2;
        FrameLayout frameLayout = this.cameraIcon;
        if (view == frameLayout) {
            frameLayout.measure(View.MeasureSpec.makeMeasureSpec(this.itemSize, NUM), View.MeasureSpec.makeMeasureSpec((this.itemSize - this.cameraViewOffsetBottomY) - this.cameraViewOffsetY, NUM));
            return true;
        }
        CameraView cameraView2 = this.cameraView;
        if (view != cameraView2) {
            FrameLayout frameLayout3 = this.cameraPanel;
            if (view == frameLayout3) {
                if (z) {
                    frameLayout3.measure(View.MeasureSpec.makeMeasureSpec(i, NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(126.0f), NUM));
                } else {
                    frameLayout3.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(126.0f), NUM), View.MeasureSpec.makeMeasureSpec(i2, NUM));
                }
                return true;
            }
            ZoomControlView zoomControlView2 = this.zoomControlView;
            if (view == zoomControlView2) {
                if (z) {
                    zoomControlView2.measure(View.MeasureSpec.makeMeasureSpec(i, NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f), NUM));
                } else {
                    zoomControlView2.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f), NUM), View.MeasureSpec.makeMeasureSpec(i2, NUM));
                }
                return true;
            }
            RecyclerListView recyclerListView = this.cameraPhotoRecyclerView;
            if (view == recyclerListView) {
                this.cameraPhotoRecyclerViewIgnoreLayout = true;
                if (z) {
                    recyclerListView.measure(View.MeasureSpec.makeMeasureSpec(i, NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(80.0f), NUM));
                    if (this.cameraPhotoLayoutManager.getOrientation() != 0) {
                        this.cameraPhotoRecyclerView.setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f), 0);
                        this.cameraPhotoLayoutManager.setOrientation(0);
                        this.cameraAttachAdapter.notifyDataSetChanged();
                    }
                } else {
                    recyclerListView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(80.0f), NUM), View.MeasureSpec.makeMeasureSpec(i2, NUM));
                    if (this.cameraPhotoLayoutManager.getOrientation() != 1) {
                        this.cameraPhotoRecyclerView.setPadding(0, AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f));
                        this.cameraPhotoLayoutManager.setOrientation(1);
                        this.cameraAttachAdapter.notifyDataSetChanged();
                    }
                }
                this.cameraPhotoRecyclerViewIgnoreLayout = false;
                return true;
            }
        } else if (this.cameraOpened && !this.cameraAnimationInProgress) {
            cameraView2.measure(View.MeasureSpec.makeMeasureSpec(i, NUM), View.MeasureSpec.makeMeasureSpec(i2, NUM));
            return true;
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public boolean onCustomLayout(View view, int i, int i2, int i3, int i4) {
        int i5;
        int i6;
        int i7 = i3 - i;
        int i8 = i4 - i2;
        boolean z = i7 < i8;
        if (view == this.cameraPanel) {
            if (z) {
                if (this.cameraPhotoRecyclerView.getVisibility() == 0) {
                    this.cameraPanel.layout(0, i4 - AndroidUtilities.dp(222.0f), i7, i4 - AndroidUtilities.dp(96.0f));
                } else {
                    this.cameraPanel.layout(0, i4 - AndroidUtilities.dp(126.0f), i7, i4);
                }
            } else if (this.cameraPhotoRecyclerView.getVisibility() == 0) {
                this.cameraPanel.layout(i3 - AndroidUtilities.dp(222.0f), 0, i3 - AndroidUtilities.dp(96.0f), i8);
            } else {
                this.cameraPanel.layout(i3 - AndroidUtilities.dp(126.0f), 0, i3, i8);
            }
            return true;
        } else if (view == this.zoomControlView) {
            if (z) {
                if (this.cameraPhotoRecyclerView.getVisibility() == 0) {
                    this.zoomControlView.layout(0, i4 - AndroidUtilities.dp(310.0f), i7, i4 - AndroidUtilities.dp(260.0f));
                } else {
                    this.zoomControlView.layout(0, i4 - AndroidUtilities.dp(176.0f), i7, i4 - AndroidUtilities.dp(126.0f));
                }
            } else if (this.cameraPhotoRecyclerView.getVisibility() == 0) {
                this.zoomControlView.layout(i3 - AndroidUtilities.dp(310.0f), 0, i3 - AndroidUtilities.dp(260.0f), i8);
            } else {
                this.zoomControlView.layout(i3 - AndroidUtilities.dp(176.0f), 0, i3 - AndroidUtilities.dp(126.0f), i8);
            }
            return true;
        } else {
            TextView textView = this.counterTextView;
            if (view == textView) {
                if (z) {
                    i6 = (i7 - textView.getMeasuredWidth()) / 2;
                    i5 = i4 - AndroidUtilities.dp(167.0f);
                    this.counterTextView.setRotation(0.0f);
                    if (this.cameraPhotoRecyclerView.getVisibility() == 0) {
                        i5 -= AndroidUtilities.dp(96.0f);
                    }
                } else {
                    i6 = i3 - AndroidUtilities.dp(167.0f);
                    i5 = (i8 / 2) + (this.counterTextView.getMeasuredWidth() / 2);
                    this.counterTextView.setRotation(-90.0f);
                    if (this.cameraPhotoRecyclerView.getVisibility() == 0) {
                        i6 -= AndroidUtilities.dp(96.0f);
                    }
                }
                TextView textView2 = this.counterTextView;
                textView2.layout(i6, i5, textView2.getMeasuredWidth() + i6, this.counterTextView.getMeasuredHeight() + i5);
                return true;
            } else if (view != this.cameraPhotoRecyclerView) {
                return false;
            } else {
                if (z) {
                    int dp = i8 - AndroidUtilities.dp(88.0f);
                    view.layout(0, dp, view.getMeasuredWidth(), view.getMeasuredHeight() + dp);
                } else {
                    int dp2 = (i + i7) - AndroidUtilities.dp(88.0f);
                    view.layout(dp2, 0, view.getMeasuredWidth() + dp2, view.getMeasuredHeight());
                }
                return true;
            }
        }
    }

    public void onPause() {
        ShutterButton shutterButton2 = this.shutterButton;
        if (shutterButton2 != null) {
            if (!this.requestingPermissions) {
                if (this.cameraView != null && shutterButton2.getState() == ShutterButton.State.RECORDING) {
                    resetRecordState();
                    CameraController.getInstance().stopVideoRecording(this.cameraView.getCameraSession(), false);
                    this.shutterButton.setState(ShutterButton.State.DEFAULT, true);
                }
                if (this.cameraOpened) {
                    closeCamera(false);
                }
                hideCamera(true);
            } else {
                if (this.cameraView != null && shutterButton2.getState() == ShutterButton.State.RECORDING) {
                    this.shutterButton.setState(ShutterButton.State.DEFAULT, true);
                }
                this.requestingPermissions = false;
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

    /* access modifiers changed from: private */
    public void openCamera(boolean z) {
        CameraView cameraView2 = this.cameraView;
        if (cameraView2 != null && this.cameraInitAnimation == null && cameraView2.isInitied()) {
            int i = 0;
            if (cameraPhotos.isEmpty()) {
                this.counterTextView.setVisibility(4);
                this.cameraPhotoRecyclerView.setVisibility(8);
            } else {
                this.counterTextView.setVisibility(0);
                this.cameraPhotoRecyclerView.setVisibility(0);
            }
            if (this.commentTextView.isKeyboardVisible() && isFocusable()) {
                this.commentTextView.closeKeyboard();
            }
            this.zoomControlView.setVisibility(0);
            this.zoomControlView.setAlpha(0.0f);
            this.cameraPanel.setVisibility(0);
            this.cameraPanel.setTag((Object) null);
            int[] iArr = this.animateCameraValues;
            iArr[0] = 0;
            int i2 = this.itemSize;
            iArr[1] = i2 - this.cameraViewOffsetX;
            iArr[2] = (i2 - this.cameraViewOffsetY) - this.cameraViewOffsetBottomY;
            if (z) {
                this.cameraAnimationInProgress = true;
                ArrayList arrayList = new ArrayList();
                arrayList.add(ObjectAnimator.ofFloat(this, "cameraOpenProgress", new float[]{0.0f, 1.0f}));
                arrayList.add(ObjectAnimator.ofFloat(this.cameraPanel, View.ALPHA, new float[]{1.0f}));
                arrayList.add(ObjectAnimator.ofFloat(this.counterTextView, View.ALPHA, new float[]{1.0f}));
                arrayList.add(ObjectAnimator.ofFloat(this.cameraPhotoRecyclerView, View.ALPHA, new float[]{1.0f}));
                int i3 = 0;
                while (true) {
                    if (i3 >= 2) {
                        break;
                    } else if (this.flashModeButton[i3].getVisibility() == 0) {
                        arrayList.add(ObjectAnimator.ofFloat(this.flashModeButton[i3], View.ALPHA, new float[]{1.0f}));
                        break;
                    } else {
                        i3++;
                    }
                }
                AnimatorSet animatorSet2 = new AnimatorSet();
                animatorSet2.playTogether(arrayList);
                animatorSet2.setDuration(200);
                animatorSet2.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        boolean unused = ChatAttachAlert.this.cameraAnimationInProgress = false;
                        if (Build.VERSION.SDK_INT >= 21 && ChatAttachAlert.this.cameraView != null) {
                            ChatAttachAlert.this.cameraView.invalidateOutline();
                        }
                        if (ChatAttachAlert.this.cameraOpened) {
                            ChatAttachAlert.this.delegate.onCameraOpened();
                        }
                    }
                });
                animatorSet2.start();
            } else {
                setCameraOpenProgress(1.0f);
                this.cameraPanel.setAlpha(1.0f);
                this.counterTextView.setAlpha(1.0f);
                this.cameraPhotoRecyclerView.setAlpha(1.0f);
                while (true) {
                    if (i >= 2) {
                        break;
                    } else if (this.flashModeButton[i].getVisibility() == 0) {
                        this.flashModeButton[i].setAlpha(1.0f);
                        break;
                    } else {
                        i++;
                    }
                }
                this.delegate.onCameraOpened();
            }
            if (Build.VERSION.SDK_INT >= 21) {
                this.cameraView.setSystemUiVisibility(1028);
            }
            this.cameraOpened = true;
            this.cameraView.setImportantForAccessibility(2);
            if (Build.VERSION.SDK_INT >= 19) {
                this.gridView.setImportantForAccessibility(4);
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:46:0x00f2, code lost:
        if (new java.io.File(r0).exists() != false) goto L_0x00f5;
     */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x0156 A[SYNTHETIC, Splitter:B:82:0x0156] */
    /* JADX WARNING: Removed duplicated region for block: B:91:0x01c1 A[SYNTHETIC, Splitter:B:91:0x01c1] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onActivityResultFragment(int r29, android.content.Intent r30, java.lang.String r31) {
        /*
            r28 = this;
            r1 = r28
            r0 = r29
            r7 = r31
            org.telegram.ui.ActionBar.BaseFragment r2 = r1.baseFragment
            if (r2 == 0) goto L_0x01cb
            android.app.Activity r2 = r2.getParentActivity()
            if (r2 != 0) goto L_0x0012
            goto L_0x01cb
        L_0x0012:
            r14 = 1
            mediaFromExternalCamera = r14
            r15 = 0
            if (r0 != 0) goto L_0x0078
            org.telegram.ui.PhotoViewer r0 = org.telegram.ui.PhotoViewer.getInstance()
            org.telegram.ui.ActionBar.BaseFragment r2 = r1.baseFragment
            android.app.Activity r2 = r2.getParentActivity()
            r0.setParentActivity(r2)
            org.telegram.ui.PhotoViewer r0 = org.telegram.ui.PhotoViewer.getInstance()
            int r2 = r1.maxSelectedPhotos
            boolean r3 = r1.allowOrder
            r0.setMaxSelectedPhotos(r2, r3)
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            androidx.exifinterface.media.ExifInterface r0 = new androidx.exifinterface.media.ExifInterface     // Catch:{ Exception -> 0x0056 }
            r0.<init>((java.lang.String) r7)     // Catch:{ Exception -> 0x0056 }
            java.lang.String r2 = "Orientation"
            int r0 = r0.getAttributeInt(r2, r14)     // Catch:{ Exception -> 0x0056 }
            r2 = 3
            if (r0 == r2) goto L_0x0052
            r2 = 6
            if (r0 == r2) goto L_0x004f
            r2 = 8
            if (r0 == r2) goto L_0x004c
            r0 = 0
            goto L_0x0054
        L_0x004c:
            r0 = 270(0x10e, float:3.78E-43)
            goto L_0x0054
        L_0x004f:
            r0 = 90
            goto L_0x0054
        L_0x0052:
            r0 = 180(0xb4, float:2.52E-43)
        L_0x0054:
            r8 = r0
            goto L_0x005b
        L_0x0056:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r8 = 0
        L_0x005b:
            org.telegram.messenger.MediaController$PhotoEntry r0 = new org.telegram.messenger.MediaController$PhotoEntry
            r3 = 0
            int r4 = lastImageId
            int r2 = r4 + -1
            lastImageId = r2
            r5 = 0
            r9 = 0
            r10 = 0
            r11 = 0
            r12 = 0
            r2 = r0
            r7 = r31
            r2.<init>(r3, r4, r5, r7, r8, r9, r10, r11, r12)
            r0.canDeleteAfter = r14
            r1.openPhotoViewer(r0, r15, r14)
            goto L_0x01cb
        L_0x0078:
            r2 = 2
            if (r0 != r2) goto L_0x01cb
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x0093
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "pic path "
            r0.append(r2)
            r0.append(r7)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.d(r0)
        L_0x0093:
            r2 = 0
            if (r30 == 0) goto L_0x00a5
            if (r7 == 0) goto L_0x00a5
            java.io.File r0 = new java.io.File
            r0.<init>(r7)
            boolean r0 = r0.exists()
            if (r0 == 0) goto L_0x00a5
            r0 = r2
            goto L_0x00a7
        L_0x00a5:
            r0 = r30
        L_0x00a7:
            if (r0 == 0) goto L_0x0108
            android.net.Uri r0 = r0.getData()
            if (r0 == 0) goto L_0x00f4
            boolean r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r3 == 0) goto L_0x00cb
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "video record uri "
            r3.append(r4)
            java.lang.String r4 = r0.toString()
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            org.telegram.messenger.FileLog.d(r3)
        L_0x00cb:
            java.lang.String r0 = org.telegram.messenger.AndroidUtilities.getPath(r0)
            boolean r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r3 == 0) goto L_0x00e7
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "resolved path = "
            r3.append(r4)
            r3.append(r0)
            java.lang.String r3 = r3.toString()
            org.telegram.messenger.FileLog.d(r3)
        L_0x00e7:
            if (r0 == 0) goto L_0x00f4
            java.io.File r3 = new java.io.File
            r3.<init>(r0)
            boolean r3 = r3.exists()
            if (r3 != 0) goto L_0x00f5
        L_0x00f4:
            r0 = r7
        L_0x00f5:
            org.telegram.ui.ActionBar.BaseFragment r3 = r1.baseFragment
            boolean r4 = r3 instanceof org.telegram.ui.ChatActivity
            if (r4 == 0) goto L_0x0103
            org.telegram.ui.ChatActivity r3 = (org.telegram.ui.ChatActivity) r3
            boolean r3 = r3.isSecretChat()
            if (r3 != 0) goto L_0x0106
        L_0x0103:
            org.telegram.messenger.AndroidUtilities.addMediaToGallery((java.lang.String) r31)
        L_0x0106:
            r7 = r2
            goto L_0x0109
        L_0x0108:
            r0 = r2
        L_0x0109:
            if (r0 != 0) goto L_0x0119
            if (r7 == 0) goto L_0x0119
            java.io.File r3 = new java.io.File
            r3.<init>(r7)
            boolean r3 = r3.exists()
            if (r3 == 0) goto L_0x0119
            goto L_0x011a
        L_0x0119:
            r7 = r0
        L_0x011a:
            r3 = 0
            android.media.MediaMetadataRetriever r5 = new android.media.MediaMetadataRetriever     // Catch:{ Exception -> 0x0150 }
            r5.<init>()     // Catch:{ Exception -> 0x0150 }
            r5.setDataSource(r7)     // Catch:{ Exception -> 0x014a, all -> 0x0145 }
            r0 = 9
            java.lang.String r0 = r5.extractMetadata(r0)     // Catch:{ Exception -> 0x014a, all -> 0x0145 }
            if (r0 == 0) goto L_0x013b
            long r8 = java.lang.Long.parseLong(r0)     // Catch:{ Exception -> 0x014a, all -> 0x0145 }
            float r0 = (float) r8     // Catch:{ Exception -> 0x014a, all -> 0x0145 }
            r2 = 1148846080(0x447a0000, float:1000.0)
            float r0 = r0 / r2
            double r8 = (double) r0     // Catch:{ Exception -> 0x014a, all -> 0x0145 }
            double r2 = java.lang.Math.ceil(r8)     // Catch:{ Exception -> 0x014a, all -> 0x0145 }
            int r0 = (int) r2
            long r3 = (long) r0
        L_0x013b:
            r5.release()     // Catch:{ Exception -> 0x013f }
            goto L_0x0159
        L_0x013f:
            r0 = move-exception
            r2 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)
            goto L_0x0159
        L_0x0145:
            r0 = move-exception
            r3 = r0
            r2 = r5
            goto L_0x01bf
        L_0x014a:
            r0 = move-exception
            r2 = r5
            goto L_0x0151
        L_0x014d:
            r0 = move-exception
            r3 = r0
            goto L_0x01bf
        L_0x0150:
            r0 = move-exception
        L_0x0151:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x014d }
            if (r2 == 0) goto L_0x0159
            r2.release()     // Catch:{ Exception -> 0x013f }
        L_0x0159:
            android.graphics.Bitmap r0 = android.media.ThumbnailUtils.createVideoThumbnail(r7, r14)
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r5 = "-2147483648_"
            r2.append(r5)
            int r5 = org.telegram.messenger.SharedConfig.getLastLocalId()
            r2.append(r5)
            java.lang.String r5 = ".jpg"
            r2.append(r5)
            java.lang.String r2 = r2.toString()
            java.io.File r5 = new java.io.File
            r6 = 4
            java.io.File r6 = org.telegram.messenger.FileLoader.getDirectory(r6)
            r5.<init>(r6, r2)
            java.io.FileOutputStream r2 = new java.io.FileOutputStream     // Catch:{ all -> 0x018e }
            r2.<init>(r5)     // Catch:{ all -> 0x018e }
            android.graphics.Bitmap$CompressFormat r6 = android.graphics.Bitmap.CompressFormat.JPEG     // Catch:{ all -> 0x018e }
            r8 = 55
            r0.compress(r6, r8, r2)     // Catch:{ all -> 0x018e }
            goto L_0x0192
        L_0x018e:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0192:
            org.telegram.messenger.SharedConfig.saveConfig()
            org.telegram.messenger.MediaController$PhotoEntry r0 = new org.telegram.messenger.MediaController$PhotoEntry
            r17 = 0
            int r18 = lastImageId
            int r2 = r18 + -1
            lastImageId = r2
            r19 = 0
            r22 = 0
            r23 = 1
            r24 = 0
            r25 = 0
            r26 = 0
            r16 = r0
            r21 = r7
            r16.<init>(r17, r18, r19, r21, r22, r23, r24, r25, r26)
            int r2 = (int) r3
            r0.duration = r2
            java.lang.String r2 = r5.getAbsolutePath()
            r0.thumbPath = r2
            r1.openPhotoViewer(r0, r15, r14)
            goto L_0x01cb
        L_0x01bf:
            if (r2 == 0) goto L_0x01ca
            r2.release()     // Catch:{ Exception -> 0x01c5 }
            goto L_0x01ca
        L_0x01c5:
            r0 = move-exception
            r2 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)
        L_0x01ca:
            throw r3
        L_0x01cb:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlert.onActivityResultFragment(int, android.content.Intent, java.lang.String):void");
    }

    public void closeCamera(boolean z) {
        if (!this.takingPhoto && this.cameraView != null) {
            int[] iArr = this.animateCameraValues;
            int i = this.itemSize;
            iArr[1] = i - this.cameraViewOffsetX;
            iArr[2] = (i - this.cameraViewOffsetY) - this.cameraViewOffsetBottomY;
            Runnable runnable = this.zoomControlHideRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.zoomControlHideRunnable = null;
            }
            if (z) {
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.cameraView.getLayoutParams();
                int[] iArr2 = this.animateCameraValues;
                int translationY = (int) this.cameraView.getTranslationY();
                layoutParams.topMargin = translationY;
                iArr2[0] = translationY;
                this.cameraView.setLayoutParams(layoutParams);
                this.cameraView.setTranslationY(0.0f);
                this.cameraAnimationInProgress = true;
                ArrayList arrayList = new ArrayList();
                arrayList.add(ObjectAnimator.ofFloat(this, "cameraOpenProgress", new float[]{0.0f}));
                arrayList.add(ObjectAnimator.ofFloat(this.cameraPanel, View.ALPHA, new float[]{0.0f}));
                arrayList.add(ObjectAnimator.ofFloat(this.zoomControlView, View.ALPHA, new float[]{0.0f}));
                arrayList.add(ObjectAnimator.ofFloat(this.counterTextView, View.ALPHA, new float[]{0.0f}));
                arrayList.add(ObjectAnimator.ofFloat(this.cameraPhotoRecyclerView, View.ALPHA, new float[]{0.0f}));
                int i2 = 0;
                while (true) {
                    if (i2 >= 2) {
                        break;
                    } else if (this.flashModeButton[i2].getVisibility() == 0) {
                        arrayList.add(ObjectAnimator.ofFloat(this.flashModeButton[i2], View.ALPHA, new float[]{0.0f}));
                        break;
                    } else {
                        i2++;
                    }
                }
                AnimatorSet animatorSet2 = new AnimatorSet();
                animatorSet2.playTogether(arrayList);
                animatorSet2.setDuration(200);
                animatorSet2.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        boolean unused = ChatAttachAlert.this.cameraAnimationInProgress = false;
                        if (Build.VERSION.SDK_INT >= 21 && ChatAttachAlert.this.cameraView != null) {
                            ChatAttachAlert.this.cameraView.invalidateOutline();
                        }
                        boolean unused2 = ChatAttachAlert.this.cameraOpened = false;
                        if (ChatAttachAlert.this.cameraPanel != null) {
                            ChatAttachAlert.this.cameraPanel.setVisibility(8);
                        }
                        if (ChatAttachAlert.this.zoomControlView != null) {
                            ChatAttachAlert.this.zoomControlView.setVisibility(8);
                            ChatAttachAlert.this.zoomControlView.setTag((Object) null);
                        }
                        if (ChatAttachAlert.this.cameraPhotoRecyclerView != null) {
                            ChatAttachAlert.this.cameraPhotoRecyclerView.setVisibility(8);
                        }
                        if (Build.VERSION.SDK_INT >= 21 && ChatAttachAlert.this.cameraView != null) {
                            ChatAttachAlert.this.cameraView.setSystemUiVisibility(1024);
                        }
                    }
                });
                animatorSet2.start();
            } else {
                this.animateCameraValues[0] = 0;
                setCameraOpenProgress(0.0f);
                this.cameraPanel.setAlpha(0.0f);
                this.cameraPanel.setVisibility(8);
                this.zoomControlView.setAlpha(0.0f);
                this.zoomControlView.setTag((Object) null);
                this.zoomControlView.setVisibility(8);
                this.cameraPhotoRecyclerView.setAlpha(0.0f);
                this.counterTextView.setAlpha(0.0f);
                this.cameraPhotoRecyclerView.setVisibility(8);
                int i3 = 0;
                while (true) {
                    if (i3 >= 2) {
                        break;
                    } else if (this.flashModeButton[i3].getVisibility() == 0) {
                        this.flashModeButton[i3].setAlpha(0.0f);
                        break;
                    } else {
                        i3++;
                    }
                }
                this.cameraOpened = false;
                if (Build.VERSION.SDK_INT >= 21) {
                    this.cameraView.setSystemUiVisibility(1024);
                }
            }
            this.cameraView.setImportantForAccessibility(0);
            if (Build.VERSION.SDK_INT >= 19) {
                this.gridView.setImportantForAccessibility(0);
            }
        }
    }

    @Keep
    public void setCameraOpenProgress(float f) {
        int i;
        float f2;
        if (this.cameraView != null) {
            this.cameraOpenProgress = f;
            int[] iArr = this.animateCameraValues;
            float f3 = (float) iArr[1];
            float f4 = (float) iArr[2];
            Point point = AndroidUtilities.displaySize;
            if (point.x < point.y) {
                f2 = (float) ((this.container.getWidth() - getLeftInset()) - getRightInset());
                i = this.container.getHeight();
            } else {
                f2 = (float) ((this.container.getWidth() - getLeftInset()) - getRightInset());
                i = this.container.getHeight();
            }
            float f5 = (float) i;
            if (f == 0.0f) {
                this.cameraView.setClipTop(this.cameraViewOffsetY);
                this.cameraView.setClipBottom(this.cameraViewOffsetBottomY);
                this.cameraView.setTranslationX((float) this.cameraViewLocation[0]);
                this.cameraView.setTranslationY((float) this.cameraViewLocation[1]);
                this.cameraIcon.setTranslationX((float) this.cameraViewLocation[0]);
                this.cameraIcon.setTranslationY((float) this.cameraViewLocation[1]);
            } else if (!(this.cameraView.getTranslationX() == 0.0f && this.cameraView.getTranslationY() == 0.0f)) {
                this.cameraView.setTranslationX(0.0f);
                this.cameraView.setTranslationY(0.0f);
            }
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.cameraView.getLayoutParams();
            layoutParams.width = (int) (f3 + ((f2 - f3) * f));
            layoutParams.height = (int) (f4 + ((f5 - f4) * f));
            if (f != 0.0f) {
                float f6 = 1.0f - f;
                this.cameraView.setClipTop((int) (((float) this.cameraViewOffsetY) * f6));
                this.cameraView.setClipBottom((int) (((float) this.cameraViewOffsetBottomY) * f6));
                int[] iArr2 = this.cameraViewLocation;
                layoutParams.leftMargin = (int) (((float) iArr2[0]) * f6);
                int[] iArr3 = this.animateCameraValues;
                layoutParams.topMargin = (int) (((float) iArr3[0]) + (((float) (iArr2[1] - iArr3[0])) * f6));
            } else {
                layoutParams.leftMargin = 0;
                layoutParams.topMargin = 0;
            }
            this.cameraView.setLayoutParams(layoutParams);
            if (f <= 0.5f) {
                this.cameraIcon.setAlpha(1.0f - (f / 0.5f));
            } else {
                this.cameraIcon.setAlpha(0.0f);
            }
            if (Build.VERSION.SDK_INT >= 21) {
                this.cameraView.invalidateOutline();
            }
        }
    }

    @Keep
    public float getCameraOpenProgress() {
        return this.cameraOpenProgress;
    }

    /* access modifiers changed from: private */
    public void checkCameraViewPosition() {
        RecyclerView.ViewHolder findViewHolderForAdapterPosition;
        if (Build.VERSION.SDK_INT >= 21) {
            CameraView cameraView2 = this.cameraView;
            if (cameraView2 != null) {
                cameraView2.invalidateOutline();
            }
            RecyclerView.ViewHolder findViewHolderForAdapterPosition2 = this.gridView.findViewHolderForAdapterPosition(this.itemsPerRow - 1);
            if (findViewHolderForAdapterPosition2 != null) {
                findViewHolderForAdapterPosition2.itemView.invalidateOutline();
            }
            if ((!this.adapter.needCamera || !this.deviceHasGoodCamera || this.selectedAlbumEntry != this.galleryAlbumEntry) && (findViewHolderForAdapterPosition = this.gridView.findViewHolderForAdapterPosition(0)) != null) {
                findViewHolderForAdapterPosition.itemView.invalidateOutline();
            }
        }
        if (this.deviceHasGoodCamera) {
            int childCount = this.gridView.getChildCount();
            int i = 0;
            while (true) {
                if (i >= childCount) {
                    break;
                }
                View childAt = this.gridView.getChildAt(i);
                if (!(childAt instanceof PhotoAttachCameraCell)) {
                    i++;
                } else if (Build.VERSION.SDK_INT < 19 || childAt.isAttachedToWindow()) {
                    childAt.getLocationInWindow(this.cameraViewLocation);
                    int[] iArr = this.cameraViewLocation;
                    iArr[0] = iArr[0] - getLeftInset();
                    float x = this.gridView.getX() - ((float) getLeftInset());
                    int[] iArr2 = this.cameraViewLocation;
                    if (((float) iArr2[0]) < x) {
                        int i2 = (int) (x - ((float) iArr2[0]));
                        this.cameraViewOffsetX = i2;
                        if (i2 >= this.itemSize) {
                            this.cameraViewOffsetX = 0;
                            iArr2[0] = AndroidUtilities.dp(-400.0f);
                            this.cameraViewLocation[1] = 0;
                        } else {
                            iArr2[0] = iArr2[0] + i2;
                        }
                    } else {
                        this.cameraViewOffsetX = 0;
                    }
                    int currentActionBarHeight = (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight();
                    int[] iArr3 = this.cameraViewLocation;
                    if (iArr3[1] < currentActionBarHeight) {
                        int i3 = currentActionBarHeight - iArr3[1];
                        this.cameraViewOffsetY = i3;
                        if (i3 >= this.itemSize) {
                            this.cameraViewOffsetY = 0;
                            iArr3[0] = AndroidUtilities.dp(-400.0f);
                            this.cameraViewLocation[1] = 0;
                        } else {
                            iArr3[1] = iArr3[1] + i3;
                        }
                    } else {
                        this.cameraViewOffsetY = 0;
                    }
                    int measuredHeight = this.containerView.getMeasuredHeight();
                    int keyboardHeight = this.useSmoothKeyboard ? 0 : this.sizeNotifierFrameLayout.getKeyboardHeight();
                    if (!AndroidUtilities.isInMultiwindow && keyboardHeight <= AndroidUtilities.dp(20.0f)) {
                        measuredHeight -= this.commentTextView.getEmojiPadding();
                    }
                    int measuredHeight2 = (int) (((float) (measuredHeight - this.buttonsRecyclerView.getMeasuredHeight())) + this.buttonsRecyclerView.getTranslationY() + this.containerView.getTranslationY());
                    int[] iArr4 = this.cameraViewLocation;
                    int i4 = iArr4[1];
                    int i5 = this.itemSize;
                    if (i4 + i5 > measuredHeight2) {
                        int i6 = (iArr4[1] + i5) - measuredHeight2;
                        this.cameraViewOffsetBottomY = i6;
                        if (i6 >= i5) {
                            this.cameraViewOffsetBottomY = 0;
                            iArr4[0] = AndroidUtilities.dp(-400.0f);
                            this.cameraViewLocation[1] = 0;
                        }
                    } else {
                        this.cameraViewOffsetBottomY = 0;
                    }
                    applyCameraViewPosition();
                    return;
                }
            }
            this.cameraViewOffsetX = 0;
            this.cameraViewOffsetY = 0;
            this.cameraViewLocation[0] = AndroidUtilities.dp(-400.0f);
            this.cameraViewLocation[1] = 0;
            applyCameraViewPosition();
        }
    }

    private void applyCameraViewPosition() {
        CameraView cameraView2 = this.cameraView;
        if (cameraView2 != null) {
            if (!this.cameraOpened) {
                cameraView2.setTranslationX((float) this.cameraViewLocation[0]);
                this.cameraView.setTranslationY((float) this.cameraViewLocation[1]);
            }
            this.cameraIcon.setTranslationX((float) this.cameraViewLocation[0]);
            this.cameraIcon.setTranslationY((float) this.cameraViewLocation[1]);
            int i = this.itemSize;
            int i2 = i - this.cameraViewOffsetX;
            int i3 = this.cameraViewOffsetY;
            int i4 = (i - i3) - this.cameraViewOffsetBottomY;
            if (!this.cameraOpened) {
                this.cameraView.setClipTop(i3);
                this.cameraView.setClipBottom(this.cameraViewOffsetBottomY);
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.cameraView.getLayoutParams();
                if (!(layoutParams.height == i4 && layoutParams.width == i2)) {
                    layoutParams.width = i2;
                    layoutParams.height = i4;
                    this.cameraView.setLayoutParams(layoutParams);
                    AndroidUtilities.runOnUIThread(new Runnable(layoutParams) {
                        private final /* synthetic */ FrameLayout.LayoutParams f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run() {
                            ChatAttachAlert.this.lambda$applyCameraViewPosition$20$ChatAttachAlert(this.f$1);
                        }
                    });
                }
            }
            FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.cameraIcon.getLayoutParams();
            if (layoutParams2.height != i4 || layoutParams2.width != i2) {
                layoutParams2.width = i2;
                layoutParams2.height = i4;
                this.cameraIcon.setLayoutParams(layoutParams2);
                AndroidUtilities.runOnUIThread(new Runnable(layoutParams2) {
                    private final /* synthetic */ FrameLayout.LayoutParams f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        ChatAttachAlert.this.lambda$applyCameraViewPosition$21$ChatAttachAlert(this.f$1);
                    }
                });
            }
        }
    }

    public /* synthetic */ void lambda$applyCameraViewPosition$20$ChatAttachAlert(FrameLayout.LayoutParams layoutParams) {
        CameraView cameraView2 = this.cameraView;
        if (cameraView2 != null) {
            cameraView2.setLayoutParams(layoutParams);
        }
    }

    public /* synthetic */ void lambda$applyCameraViewPosition$21$ChatAttachAlert(FrameLayout.LayoutParams layoutParams) {
        FrameLayout frameLayout = this.cameraIcon;
        if (frameLayout != null) {
            frameLayout.setLayoutParams(layoutParams);
        }
    }

    public void showCamera() {
        if (!this.paused && this.mediaEnabled) {
            if (this.cameraView == null) {
                CameraView cameraView2 = new CameraView(this.baseFragment.getParentActivity(), this.openWithFrontFaceCamera);
                this.cameraView = cameraView2;
                cameraView2.setFocusable(true);
                if (Build.VERSION.SDK_INT >= 21) {
                    this.cameraView.setOutlineProvider(new ViewOutlineProvider() {
                        public void getOutline(View view, Outline outline) {
                            if (ChatAttachAlert.this.cameraAnimationInProgress) {
                                int dp = AndroidUtilities.dp(ChatAttachAlert.this.cornerRadius * 8.0f * ChatAttachAlert.this.cameraOpenProgress);
                                outline.setRoundRect(0, 0, view.getMeasuredWidth() + dp, view.getMeasuredHeight() + dp, (float) dp);
                            } else if (ChatAttachAlert.this.cameraAnimationInProgress || ChatAttachAlert.this.cameraOpened) {
                                outline.setRect(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
                            } else {
                                int dp2 = AndroidUtilities.dp(ChatAttachAlert.this.cornerRadius * 8.0f);
                                outline.setRoundRect(0, 0, view.getMeasuredWidth() + dp2, view.getMeasuredHeight() + dp2, (float) dp2);
                            }
                        }
                    });
                    this.cameraView.setClipToOutline(true);
                }
                this.cameraView.setContentDescription(LocaleController.getString("AccDescrInstantCamera", NUM));
                BottomSheet.ContainerView containerView = this.container;
                CameraView cameraView3 = this.cameraView;
                int i = this.itemSize;
                containerView.addView(cameraView3, 1, new FrameLayout.LayoutParams(i, i));
                this.cameraView.setDelegate(new CameraView.CameraViewDelegate() {
                    public void onCameraCreated(Camera camera) {
                    }

                    public void onCameraInit() {
                        int i = 4;
                        if (ChatAttachAlert.this.cameraView.getCameraSession().getCurrentFlashMode().equals(ChatAttachAlert.this.cameraView.getCameraSession().getNextFlashMode())) {
                            for (int i2 = 0; i2 < 2; i2++) {
                                ChatAttachAlert.this.flashModeButton[i2].setVisibility(4);
                                ChatAttachAlert.this.flashModeButton[i2].setAlpha(0.0f);
                                ChatAttachAlert.this.flashModeButton[i2].setTranslationY(0.0f);
                            }
                        } else {
                            ChatAttachAlert chatAttachAlert = ChatAttachAlert.this;
                            chatAttachAlert.setCameraFlashModeIcon(chatAttachAlert.flashModeButton[0], ChatAttachAlert.this.cameraView.getCameraSession().getCurrentFlashMode());
                            int i3 = 0;
                            while (i3 < 2) {
                                ChatAttachAlert.this.flashModeButton[i3].setVisibility(i3 == 0 ? 0 : 4);
                                ChatAttachAlert.this.flashModeButton[i3].setAlpha((i3 != 0 || !ChatAttachAlert.this.cameraOpened) ? 0.0f : 1.0f);
                                ChatAttachAlert.this.flashModeButton[i3].setTranslationY(0.0f);
                                i3++;
                            }
                        }
                        ChatAttachAlert.this.switchCameraButton.setImageResource(ChatAttachAlert.this.cameraView.isFrontface() ? NUM : NUM);
                        ImageView access$8300 = ChatAttachAlert.this.switchCameraButton;
                        if (ChatAttachAlert.this.cameraView.hasFrontFaceCamera()) {
                            i = 0;
                        }
                        access$8300.setVisibility(i);
                        if (!ChatAttachAlert.this.cameraOpened) {
                            AnimatorSet unused = ChatAttachAlert.this.cameraInitAnimation = new AnimatorSet();
                            ChatAttachAlert.this.cameraInitAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(ChatAttachAlert.this.cameraView, View.ALPHA, new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(ChatAttachAlert.this.cameraIcon, View.ALPHA, new float[]{0.0f, 1.0f})});
                            ChatAttachAlert.this.cameraInitAnimation.setDuration(180);
                            ChatAttachAlert.this.cameraInitAnimation.addListener(new AnimatorListenerAdapter() {
                                public void onAnimationEnd(Animator animator) {
                                    if (animator.equals(ChatAttachAlert.this.cameraInitAnimation)) {
                                        boolean unused = ChatAttachAlert.this.canSaveCameraPreview = true;
                                        AnimatorSet unused2 = ChatAttachAlert.this.cameraInitAnimation = null;
                                        int childCount = ChatAttachAlert.this.gridView.getChildCount();
                                        for (int i = 0; i < childCount; i++) {
                                            View childAt = ChatAttachAlert.this.gridView.getChildAt(i);
                                            if (childAt instanceof PhotoAttachCameraCell) {
                                                childAt.setVisibility(4);
                                                return;
                                            }
                                        }
                                    }
                                }

                                public void onAnimationCancel(Animator animator) {
                                    AnimatorSet unused = ChatAttachAlert.this.cameraInitAnimation = null;
                                }
                            });
                            ChatAttachAlert.this.cameraInitAnimation.start();
                        }
                    }
                });
                if (this.cameraIcon == null) {
                    AnonymousClass30 r0 = new FrameLayout(this.baseFragment.getParentActivity()) {
                        /* access modifiers changed from: protected */
                        public void onDraw(Canvas canvas) {
                            int intrinsicWidth = ChatAttachAlert.this.cameraDrawable.getIntrinsicWidth();
                            int intrinsicHeight = ChatAttachAlert.this.cameraDrawable.getIntrinsicHeight();
                            int access$2800 = (ChatAttachAlert.this.itemSize - intrinsicWidth) / 2;
                            int access$28002 = (ChatAttachAlert.this.itemSize - intrinsicHeight) / 2;
                            if (ChatAttachAlert.this.cameraViewOffsetY != 0) {
                                access$28002 -= ChatAttachAlert.this.cameraViewOffsetY;
                            }
                            ChatAttachAlert.this.cameraDrawable.setBounds(access$2800, access$28002, intrinsicWidth + access$2800, intrinsicHeight + access$28002);
                            ChatAttachAlert.this.cameraDrawable.draw(canvas);
                        }
                    };
                    this.cameraIcon = r0;
                    r0.setWillNotDraw(false);
                    this.cameraIcon.setClipChildren(true);
                }
                BottomSheet.ContainerView containerView2 = this.container;
                FrameLayout frameLayout = this.cameraIcon;
                int i2 = this.itemSize;
                containerView2.addView(frameLayout, 2, new FrameLayout.LayoutParams(i2, i2));
                float f = 1.0f;
                this.cameraView.setAlpha(this.mediaEnabled ? 1.0f : 0.2f);
                this.cameraView.setEnabled(this.mediaEnabled);
                FrameLayout frameLayout3 = this.cameraIcon;
                if (!this.mediaEnabled) {
                    f = 0.2f;
                }
                frameLayout3.setAlpha(f);
                this.cameraIcon.setEnabled(this.mediaEnabled);
                checkCameraViewPosition();
            }
            ZoomControlView zoomControlView2 = this.zoomControlView;
            if (zoomControlView2 != null) {
                zoomControlView2.setZoom(0.0f, false);
                this.cameraZoom = 0.0f;
            }
            this.cameraView.setTranslationX((float) this.cameraViewLocation[0]);
            this.cameraView.setTranslationY((float) this.cameraViewLocation[1]);
            this.cameraIcon.setTranslationX((float) this.cameraViewLocation[0]);
            this.cameraIcon.setTranslationY((float) this.cameraViewLocation[1]);
        }
    }

    public void hideCamera(boolean z) {
        if (this.deviceHasGoodCamera && this.cameraView != null) {
            saveLastCameraBitmap();
            int childCount = this.gridView.getChildCount();
            int i = 0;
            while (true) {
                if (i >= childCount) {
                    break;
                }
                View childAt = this.gridView.getChildAt(i);
                if (childAt instanceof PhotoAttachCameraCell) {
                    childAt.setVisibility(0);
                    ((PhotoAttachCameraCell) childAt).updateBitmap();
                    break;
                }
                i++;
            }
            this.cameraView.destroy(z, (Runnable) null);
            AnimatorSet animatorSet2 = this.cameraInitAnimation;
            if (animatorSet2 != null) {
                animatorSet2.cancel();
                this.cameraInitAnimation = null;
            }
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    ChatAttachAlert.this.lambda$hideCamera$22$ChatAttachAlert();
                }
            }, 300);
            this.canSaveCameraPreview = false;
        }
    }

    public /* synthetic */ void lambda$hideCamera$22$ChatAttachAlert() {
        this.container.removeView(this.cameraView);
        this.container.removeView(this.cameraIcon);
        this.cameraView = null;
        this.cameraIcon = null;
    }

    private void saveLastCameraBitmap() {
        if (this.canSaveCameraPreview) {
            try {
                Bitmap bitmap = this.cameraView.getTextureView().getBitmap();
                if (bitmap != null) {
                    Bitmap createBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), this.cameraView.getMatrix(), true);
                    bitmap.recycle();
                    Bitmap createScaledBitmap = Bitmap.createScaledBitmap(createBitmap, 80, (int) (((float) createBitmap.getHeight()) / (((float) createBitmap.getWidth()) / 80.0f)), true);
                    if (createScaledBitmap != null) {
                        if (createScaledBitmap != createBitmap) {
                            createBitmap.recycle();
                        }
                        Utilities.blurBitmap(createScaledBitmap, 7, 1, createScaledBitmap.getWidth(), createScaledBitmap.getHeight(), createScaledBitmap.getRowBytes());
                        createScaledBitmap.compress(Bitmap.CompressFormat.JPEG, 87, new FileOutputStream(new File(ApplicationLoader.getFilesDirFixed(), "cthumb.jpg")));
                        createScaledBitmap.recycle();
                    }
                }
            } catch (Throwable unused) {
            }
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.albumsDidLoad) {
            if (this.adapter != null) {
                if (this.baseFragment instanceof ChatActivity) {
                    this.galleryAlbumEntry = MediaController.allMediaAlbumEntry;
                } else {
                    this.galleryAlbumEntry = MediaController.allPhotosAlbumEntry;
                }
                if (this.selectedAlbumEntry != null) {
                    int i3 = 0;
                    while (true) {
                        if (i3 >= MediaController.allMediaAlbums.size()) {
                            break;
                        }
                        MediaController.AlbumEntry albumEntry = MediaController.allMediaAlbums.get(i3);
                        int i4 = albumEntry.bucketId;
                        MediaController.AlbumEntry albumEntry2 = this.selectedAlbumEntry;
                        if (i4 == albumEntry2.bucketId && albumEntry.videoOnly == albumEntry2.videoOnly) {
                            this.selectedAlbumEntry = albumEntry;
                            break;
                        }
                        i3++;
                    }
                } else {
                    this.selectedAlbumEntry = this.galleryAlbumEntry;
                }
                this.loading = false;
                this.progressView.showTextView();
                this.adapter.notifyDataSetChanged();
                this.cameraAttachAdapter.notifyDataSetChanged();
                if (!selectedPhotosOrder.isEmpty() && this.galleryAlbumEntry != null) {
                    int size = selectedPhotosOrder.size();
                    for (int i5 = 0; i5 < size; i5++) {
                        int intValue = ((Integer) selectedPhotosOrder.get(i5)).intValue();
                        MediaController.PhotoEntry photoEntry = this.galleryAlbumEntry.photosByIds.get(intValue);
                        if (photoEntry != null) {
                            selectedPhotos.put(Integer.valueOf(intValue), photoEntry);
                        }
                    }
                }
                updateAlbumsDropDown();
            }
        } else if (i == NotificationCenter.reloadInlineHints) {
            ButtonsAdapter buttonsAdapter2 = this.buttonsAdapter;
            if (buttonsAdapter2 != null) {
                buttonsAdapter2.notifyDataSetChanged();
            }
        } else if (i == NotificationCenter.cameraInitied) {
            checkCamera(false);
        }
    }

    private void updateAlbumsDropDown() {
        ArrayList<MediaController.AlbumEntry> arrayList;
        this.dropDownContainer.removeAllSubItems();
        if (this.mediaEnabled) {
            if (this.baseFragment instanceof ChatActivity) {
                arrayList = MediaController.allMediaAlbums;
            } else {
                arrayList = MediaController.allPhotoAlbums;
            }
            ArrayList<MediaController.AlbumEntry> arrayList2 = new ArrayList<>(arrayList);
            this.dropDownAlbums = arrayList2;
            Collections.sort(arrayList2, new Comparator(arrayList) {
                private final /* synthetic */ ArrayList f$0;

                {
                    this.f$0 = r1;
                }

                public final int compare(Object obj, Object obj2) {
                    return ChatAttachAlert.lambda$updateAlbumsDropDown$23(this.f$0, (MediaController.AlbumEntry) obj, (MediaController.AlbumEntry) obj2);
                }
            });
        } else {
            this.dropDownAlbums = new ArrayList<>();
        }
        if (this.dropDownAlbums.isEmpty()) {
            this.dropDown.setCompoundDrawablesWithIntrinsicBounds((Drawable) null, (Drawable) null, (Drawable) null, (Drawable) null);
            return;
        }
        this.dropDown.setCompoundDrawablesWithIntrinsicBounds((Drawable) null, (Drawable) null, this.dropDownDrawable, (Drawable) null);
        int size = this.dropDownAlbums.size();
        for (int i = 0; i < size; i++) {
            this.dropDownContainer.addSubItem(i + 10, this.dropDownAlbums.get(i).bucketName);
        }
    }

    static /* synthetic */ int lambda$updateAlbumsDropDown$23(ArrayList arrayList, MediaController.AlbumEntry albumEntry, MediaController.AlbumEntry albumEntry2) {
        int indexOf;
        int indexOf2;
        if (albumEntry.bucketId == 0 && albumEntry2.bucketId != 0) {
            return -1;
        }
        if ((albumEntry.bucketId != 0 && albumEntry2.bucketId == 0) || (indexOf = arrayList.indexOf(albumEntry)) > (indexOf2 = arrayList.indexOf(albumEntry2))) {
            return 1;
        }
        if (indexOf < indexOf2) {
            return -1;
        }
        return 0;
    }

    /* access modifiers changed from: private */
    public void updateSelectedPosition() {
        float f;
        int i;
        int dp = (this.scrollOffsetY - this.backgroundPaddingTop) - AndroidUtilities.dp(39.0f);
        float f2 = 0.0f;
        if (this.backgroundPaddingTop + dp < ActionBar.getCurrentActionBarHeight()) {
            f = Math.min(1.0f, ((float) ((ActionBar.getCurrentActionBarHeight() - dp) - this.backgroundPaddingTop)) / ((float) AndroidUtilities.dp(43.0f)));
            this.cornerRadius = 1.0f - f;
        } else {
            this.cornerRadius = 1.0f;
            f = 0.0f;
        }
        if (AndroidUtilities.isTablet()) {
            i = 16;
        } else {
            Point point = AndroidUtilities.displaySize;
            i = point.x > point.y ? 6 : 12;
        }
        if (this.actionBar.getAlpha() == 0.0f) {
            f2 = (float) AndroidUtilities.dp((1.0f - this.selectedMenuItem.getAlpha()) * 26.0f);
        }
        float f3 = ((float) i) * f;
        this.selectedMenuItem.setTranslationY(((float) (this.scrollOffsetY - AndroidUtilities.dp(37.0f + f3))) + f2);
        this.selectedTextView.setTranslationY(((float) (this.scrollOffsetY - AndroidUtilities.dp(f3 + 25.0f))) + f2);
    }

    /* access modifiers changed from: private */
    @SuppressLint({"NewApi"})
    public void updateLayout(boolean z) {
        if (this.gridView.getChildCount() <= 0) {
            RecyclerListView recyclerListView = this.gridView;
            int paddingTop = recyclerListView.getPaddingTop();
            this.scrollOffsetY = paddingTop;
            recyclerListView.setTopGlowOffset(paddingTop);
            this.containerView.invalidate();
            return;
        }
        View childAt = this.gridView.getChildAt(0);
        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.gridView.findContainingViewHolder(childAt);
        int top = childAt.getTop();
        int dp = AndroidUtilities.dp(7.0f);
        if (top < AndroidUtilities.dp(7.0f) || holder == null || holder.getAdapterPosition() != 0) {
            top = dp;
        }
        final boolean z2 = top <= AndroidUtilities.dp(12.0f);
        if ((z2 && this.actionBar.getTag() == null) || (!z2 && this.actionBar.getTag() != null)) {
            this.actionBar.setTag(z2 ? 1 : null);
            AnimatorSet animatorSet2 = this.actionBarAnimation;
            if (animatorSet2 != null) {
                animatorSet2.cancel();
                this.actionBarAnimation = null;
            }
            if (!z2) {
                this.buttonsRecyclerView.setVisibility(0);
            }
            AnimatorSet animatorSet3 = new AnimatorSet();
            this.actionBarAnimation = animatorSet3;
            animatorSet3.setDuration(180);
            AnimatorSet animatorSet4 = this.actionBarAnimation;
            Animator[] animatorArr = new Animator[2];
            ActionBar actionBar2 = this.actionBar;
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            float f = 1.0f;
            fArr[0] = z2 ? 1.0f : 0.0f;
            animatorArr[0] = ObjectAnimator.ofFloat(actionBar2, property, fArr);
            View view = this.actionBarShadow;
            Property property2 = View.ALPHA;
            float[] fArr2 = new float[1];
            if (!z2) {
                f = 0.0f;
            }
            fArr2[0] = f;
            animatorArr[1] = ObjectAnimator.ofFloat(view, property2, fArr2);
            animatorSet4.playTogether(animatorArr);
            this.actionBarAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (ChatAttachAlert.this.actionBarAnimation != null && z2) {
                        ChatAttachAlert.this.buttonsRecyclerView.setVisibility(4);
                    }
                }

                public void onAnimationCancel(Animator animator) {
                    AnimatorSet unused = ChatAttachAlert.this.actionBarAnimation = null;
                }
            });
            this.actionBarAnimation.start();
        }
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.gridView.getLayoutParams();
        int dp2 = top + (layoutParams.topMargin - AndroidUtilities.dp(11.0f));
        if (this.scrollOffsetY != dp2) {
            RecyclerListView recyclerListView2 = this.gridView;
            this.scrollOffsetY = dp2;
            recyclerListView2.setTopGlowOffset(dp2 - layoutParams.topMargin);
            updateSelectedPosition();
            this.containerView.invalidate();
        }
        this.progressView.setTranslationY((float) (this.scrollOffsetY + ((((this.gridView.getMeasuredHeight() - this.scrollOffsetY) - AndroidUtilities.dp(50.0f)) - this.progressView.getMeasuredHeight()) / 2)));
    }

    public void updatePhotosButton(int i) {
        int i2 = i;
        int size = selectedPhotos.size();
        float f = 1.0f;
        if (size == 0) {
            this.selectedCountView.setPivotX(0.0f);
            this.selectedCountView.setPivotY(0.0f);
            showCommentTextView(false, i2 != 0);
        } else {
            this.selectedCountView.invalidate();
            if (showCommentTextView(true, i2 != 0) || i2 == 0) {
                this.selectedCountView.setPivotX(0.0f);
                this.selectedCountView.setPivotY(0.0f);
            } else {
                this.selectedCountView.setPivotX((float) AndroidUtilities.dp(21.0f));
                this.selectedCountView.setPivotY((float) AndroidUtilities.dp(12.0f));
                AnimatorSet animatorSet2 = new AnimatorSet();
                Animator[] animatorArr = new Animator[2];
                View view = this.selectedCountView;
                Property property = View.SCALE_X;
                float[] fArr = new float[2];
                float f2 = 1.1f;
                fArr[0] = i2 == 1 ? 1.1f : 0.9f;
                fArr[1] = 1.0f;
                animatorArr[0] = ObjectAnimator.ofFloat(view, property, fArr);
                View view2 = this.selectedCountView;
                Property property2 = View.SCALE_Y;
                float[] fArr2 = new float[2];
                if (i2 != 1) {
                    f2 = 0.9f;
                }
                fArr2[0] = f2;
                fArr2[1] = 1.0f;
                animatorArr[1] = ObjectAnimator.ofFloat(view2, property2, fArr2);
                animatorSet2.playTogether(animatorArr);
                animatorSet2.setInterpolator(new OvershootInterpolator());
                animatorSet2.setDuration(180);
                animatorSet2.start();
            }
            if (size == 1 || this.editingMessageObject != null) {
                this.selectedMenuItem.hideSubItem(0);
            } else {
                this.selectedMenuItem.showSubItem(0);
            }
        }
        if (!(this.baseFragment instanceof ChatActivity)) {
            return;
        }
        if ((size == 0 && this.menuShowed) || (size != 0 && !this.menuShowed)) {
            this.menuShowed = size != 0;
            AnimatorSet animatorSet3 = this.menuAnimator;
            if (animatorSet3 != null) {
                animatorSet3.cancel();
                this.menuAnimator = null;
            }
            if (this.menuShowed) {
                this.selectedMenuItem.setVisibility(0);
                this.selectedTextView.setVisibility(0);
            }
            if (i2 == 0) {
                this.selectedMenuItem.setAlpha(this.menuShowed ? 1.0f : 0.0f);
                TextView textView = this.selectedTextView;
                if (!this.menuShowed) {
                    f = 0.0f;
                }
                textView.setAlpha(f);
                return;
            }
            AnimatorSet animatorSet4 = new AnimatorSet();
            this.menuAnimator = animatorSet4;
            Animator[] animatorArr2 = new Animator[2];
            ActionBarMenuItem actionBarMenuItem = this.selectedMenuItem;
            Property property3 = View.ALPHA;
            float[] fArr3 = new float[1];
            fArr3[0] = this.menuShowed ? 1.0f : 0.0f;
            animatorArr2[0] = ObjectAnimator.ofFloat(actionBarMenuItem, property3, fArr3);
            TextView textView2 = this.selectedTextView;
            Property property4 = View.ALPHA;
            float[] fArr4 = new float[1];
            if (!this.menuShowed) {
                f = 0.0f;
            }
            fArr4[0] = f;
            animatorArr2[1] = ObjectAnimator.ofFloat(textView2, property4, fArr4);
            animatorSet4.playTogether(animatorArr2);
            this.menuAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    AnimatorSet unused = ChatAttachAlert.this.menuAnimator = null;
                    if (!ChatAttachAlert.this.menuShowed) {
                        ChatAttachAlert.this.selectedMenuItem.setVisibility(4);
                        ChatAttachAlert.this.selectedTextView.setVisibility(4);
                    }
                }
            });
            this.menuAnimator.setDuration(180);
            this.menuAnimator.start();
        }
    }

    public void setDelegate(ChatAttachViewDelegate chatAttachViewDelegate) {
        this.delegate = chatAttachViewDelegate;
    }

    public void loadGalleryPhotos() {
        MediaController.AlbumEntry albumEntry;
        if (this.baseFragment instanceof ChatActivity) {
            albumEntry = MediaController.allMediaAlbumEntry;
        } else {
            albumEntry = MediaController.allPhotosAlbumEntry;
        }
        if (albumEntry == null && Build.VERSION.SDK_INT >= 21) {
            MediaController.loadGalleryPhotosAlbums(0);
        }
    }

    public void init() {
        BaseFragment baseFragment2 = this.baseFragment;
        if (baseFragment2 != null) {
            if (baseFragment2 instanceof ChatActivity) {
                this.galleryAlbumEntry = MediaController.allMediaAlbumEntry;
                TLRPC$Chat currentChat = ((ChatActivity) baseFragment2).getCurrentChat();
                TLRPC$User currentUser = ((ChatActivity) this.baseFragment).getCurrentUser();
                if (currentChat != null) {
                    this.mediaEnabled = ChatObject.canSendMedia(currentChat);
                    this.pollsEnabled = ChatObject.canSendPolls(currentChat);
                    if (this.mediaEnabled) {
                        this.progressView.setText(LocaleController.getString("NoPhotos", NUM));
                    } else if (ChatObject.isActionBannedByDefault(currentChat, 7)) {
                        this.progressView.setText(LocaleController.getString("GlobalAttachMediaRestricted", NUM));
                    } else if (AndroidUtilities.isBannedForever(currentChat.banned_rights)) {
                        this.progressView.setText(LocaleController.formatString("AttachMediaRestrictedForever", NUM, new Object[0]));
                    } else {
                        this.progressView.setText(LocaleController.formatString("AttachMediaRestricted", NUM, LocaleController.formatDateForBan((long) currentChat.banned_rights.until_date)));
                    }
                    CameraView cameraView2 = this.cameraView;
                    float f = 1.0f;
                    if (cameraView2 != null) {
                        cameraView2.setAlpha(this.mediaEnabled ? 1.0f : 0.2f);
                        this.cameraView.setEnabled(this.mediaEnabled);
                    }
                    FrameLayout frameLayout = this.cameraIcon;
                    if (frameLayout != null) {
                        if (!this.mediaEnabled) {
                            f = 0.2f;
                        }
                        frameLayout.setAlpha(f);
                        this.cameraIcon.setEnabled(this.mediaEnabled);
                    }
                } else {
                    this.pollsEnabled = currentUser != null && currentUser.bot;
                }
            } else {
                this.galleryAlbumEntry = MediaController.allPhotosAlbumEntry;
                this.commentTextView.setVisibility(4);
            }
            if (Build.VERSION.SDK_INT >= 23) {
                this.noGalleryPermissions = this.baseFragment.getParentActivity().checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != 0;
            }
            if (this.galleryAlbumEntry != null) {
                for (int i = 0; i < Math.min(100, this.galleryAlbumEntry.photos.size()); i++) {
                    this.galleryAlbumEntry.photos.get(i).reset();
                }
            }
            this.commentTextView.hidePopup(true);
            this.enterCommentEventSent = false;
            setFocusable(false);
            MediaController.AlbumEntry albumEntry = this.galleryAlbumEntry;
            this.selectedAlbumEntry = albumEntry;
            if (albumEntry != null) {
                this.loading = false;
                EmptyTextProgressView emptyTextProgressView = this.progressView;
                if (emptyTextProgressView != null) {
                    emptyTextProgressView.showTextView();
                }
            }
            this.dropDown.setText(LocaleController.getString("ChatGallery", NUM));
            clearSelectedPhotos();
            updatePhotosCounter(false);
            this.buttonsAdapter.notifyDataSetChanged();
            this.commentTextView.setText("");
            this.cameraPhotoLayoutManager.scrollToPositionWithOffset(0, 1000000);
            this.buttonsLayoutManager.scrollToPositionWithOffset(0, 1000000);
            this.layoutManager.scrollToPositionWithOffset(0, 1000000);
            updateAlbumsDropDown();
        }
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
        EditTextEmoji editTextEmoji = this.commentTextView;
        if (editTextEmoji != null) {
            editTextEmoji.onDestroy();
        }
    }

    /* access modifiers changed from: private */
    public PhotoAttachPhotoCell getCellForIndex(int i) {
        int childCount = this.gridView.getChildCount();
        for (int i2 = 0; i2 < childCount; i2++) {
            View childAt = this.gridView.getChildAt(i2);
            if (childAt instanceof PhotoAttachPhotoCell) {
                PhotoAttachPhotoCell photoAttachPhotoCell = (PhotoAttachPhotoCell) childAt;
                if (((Integer) photoAttachPhotoCell.getImageView().getTag()).intValue() == i) {
                    return photoAttachPhotoCell;
                }
            }
        }
        return null;
    }

    public void checkStorage() {
        if (this.noGalleryPermissions && Build.VERSION.SDK_INT >= 23) {
            boolean z = this.baseFragment.getParentActivity().checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != 0;
            this.noGalleryPermissions = z;
            if (!z) {
                loadGalleryPhotos();
            }
            this.adapter.notifyDataSetChanged();
            this.cameraAttachAdapter.notifyDataSetChanged();
        }
    }

    public void checkCamera(boolean z) {
        PhotoAttachAdapter photoAttachAdapter;
        BaseFragment baseFragment2 = this.baseFragment;
        if (baseFragment2 != null) {
            boolean z2 = this.deviceHasGoodCamera;
            boolean z3 = this.noCameraPermissions;
            if (!SharedConfig.inappCamera) {
                this.deviceHasGoodCamera = false;
            } else if (Build.VERSION.SDK_INT >= 23) {
                boolean z4 = baseFragment2.getParentActivity().checkSelfPermission("android.permission.CAMERA") != 0;
                this.noCameraPermissions = z4;
                if (z4) {
                    if (z) {
                        try {
                            this.baseFragment.getParentActivity().requestPermissions(new String[]{"android.permission.CAMERA"}, 17);
                        } catch (Exception unused) {
                        }
                    }
                    this.deviceHasGoodCamera = false;
                } else {
                    if (z || SharedConfig.hasCameraCache) {
                        CameraController.getInstance().initCamera((Runnable) null);
                    }
                    this.deviceHasGoodCamera = CameraController.getInstance().isCameraInitied();
                }
            } else {
                if (z || SharedConfig.hasCameraCache) {
                    CameraController.getInstance().initCamera((Runnable) null);
                }
                this.deviceHasGoodCamera = CameraController.getInstance().isCameraInitied();
            }
            if (!((z2 == this.deviceHasGoodCamera && z3 == this.noCameraPermissions) || (photoAttachAdapter = this.adapter) == null)) {
                photoAttachAdapter.notifyDataSetChanged();
            }
            if (isShowing() && this.deviceHasGoodCamera && this.baseFragment != null && this.backDrawable.getAlpha() != 0 && !this.cameraOpened) {
                showCamera();
            }
        }
    }

    public void onOpenAnimationEnd() {
        MediaController.AlbumEntry albumEntry;
        NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(false);
        if (this.baseFragment instanceof ChatActivity) {
            albumEntry = MediaController.allMediaAlbumEntry;
        } else {
            albumEntry = MediaController.allPhotosAlbumEntry;
        }
        if (Build.VERSION.SDK_INT <= 19 && albumEntry == null) {
            MediaController.loadGalleryPhotosAlbums(0);
        }
        checkCamera(true);
        AndroidUtilities.makeAccessibilityAnnouncement(LocaleController.getString("AccDescrAttachButton", NUM));
    }

    public void setAllowDrawContent(boolean z) {
        super.setAllowDrawContent(z);
        checkCameraViewPosition();
    }

    public void setMaxSelectedPhotos(int i, boolean z) {
        if (this.editingMessageObject == null) {
            this.maxSelectedPhotos = i;
            this.allowOrder = z;
        }
    }

    public void setOpenWithFrontFaceCamera(boolean z) {
        this.openWithFrontFaceCamera = z;
    }

    /* access modifiers changed from: private */
    public int addToSelectedPhotos(MediaController.PhotoEntry photoEntry, int i) {
        Integer valueOf = Integer.valueOf(photoEntry.imageId);
        if (selectedPhotos.containsKey(valueOf)) {
            selectedPhotos.remove(valueOf);
            int indexOf = selectedPhotosOrder.indexOf(valueOf);
            if (indexOf >= 0) {
                selectedPhotosOrder.remove(indexOf);
            }
            updatePhotosCounter(false);
            updateCheckedPhotoIndices();
            if (i >= 0) {
                photoEntry.reset();
                this.photoViewerProvider.updatePhotoAtIndex(i);
            }
            return indexOf;
        }
        selectedPhotos.put(valueOf, photoEntry);
        selectedPhotosOrder.add(valueOf);
        updatePhotosCounter(true);
        return -1;
    }

    private void clearSelectedPhotos() {
        if (!selectedPhotos.isEmpty()) {
            for (Map.Entry<Object, Object> value : selectedPhotos.entrySet()) {
                ((MediaController.PhotoEntry) value.getValue()).reset();
            }
            selectedPhotos.clear();
            selectedPhotosOrder.clear();
        }
        if (!cameraPhotos.isEmpty()) {
            int size = cameraPhotos.size();
            for (int i = 0; i < size; i++) {
                MediaController.PhotoEntry photoEntry = (MediaController.PhotoEntry) cameraPhotos.get(i);
                new File(photoEntry.path).delete();
                if (photoEntry.imagePath != null) {
                    new File(photoEntry.imagePath).delete();
                }
                if (photoEntry.thumbPath != null) {
                    new File(photoEntry.thumbPath).delete();
                }
            }
            cameraPhotos.clear();
        }
        updatePhotosButton(0);
        this.adapter.notifyDataSetChanged();
        this.cameraAttachAdapter.notifyDataSetChanged();
    }

    private class ButtonsAdapter extends RecyclerListView.SelectionAdapter {
        private int buttonsCount;
        private int contactButton;
        private int documentButton;
        private int galleryButton;
        private int locationButton;
        private Context mContext;
        private int musicButton;
        private int pollButton;

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return false;
        }

        public ButtonsAdapter(Context context) {
            this.mContext = context;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            if (i != 0) {
                view = new AttachBotButton(this.mContext);
            } else {
                view = new AttachButton(this.mContext);
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType == 0) {
                AttachButton attachButton = (AttachButton) viewHolder.itemView;
                if (i == this.galleryButton) {
                    attachButton.setTextAndIcon(LocaleController.getString("ChatGallery", NUM), Theme.chat_attachButtonDrawables[0]);
                    attachButton.setTag(1);
                } else if (i == this.documentButton) {
                    attachButton.setTextAndIcon(LocaleController.getString("ChatDocument", NUM), Theme.chat_attachButtonDrawables[2]);
                    attachButton.setTag(4);
                } else if (i == this.locationButton) {
                    attachButton.setTextAndIcon(LocaleController.getString("ChatLocation", NUM), Theme.chat_attachButtonDrawables[4]);
                    attachButton.setTag(6);
                } else if (i == this.musicButton) {
                    attachButton.setTextAndIcon(LocaleController.getString("AttachMusic", NUM), Theme.chat_attachButtonDrawables[1]);
                    attachButton.setTag(3);
                } else if (i == this.pollButton) {
                    attachButton.setTextAndIcon(LocaleController.getString("Poll", NUM), Theme.chat_attachButtonDrawables[5]);
                    attachButton.setTag(9);
                } else if (i == this.contactButton) {
                    attachButton.setTextAndIcon(LocaleController.getString("AttachContact", NUM), Theme.chat_attachButtonDrawables[3]);
                    attachButton.setTag(5);
                }
            } else if (itemViewType == 1) {
                int i2 = i - this.buttonsCount;
                AttachBotButton attachBotButton = (AttachBotButton) viewHolder.itemView;
                attachBotButton.setTag(Integer.valueOf(i2));
                attachBotButton.setUser(MessagesController.getInstance(ChatAttachAlert.this.currentAccount).getUser(Integer.valueOf(MediaDataController.getInstance(ChatAttachAlert.this.currentAccount).inlineBots.get(i2).peer.user_id)));
            }
        }

        public void onViewAttachedToWindow(RecyclerView.ViewHolder viewHolder) {
            ChatAttachAlert.this.applyAttachButtonColors(viewHolder.itemView);
        }

        public int getItemCount() {
            int i = this.buttonsCount;
            return (ChatAttachAlert.this.editingMessageObject != null || !(ChatAttachAlert.this.baseFragment instanceof ChatActivity)) ? i : i + MediaDataController.getInstance(ChatAttachAlert.this.currentAccount).inlineBots.size();
        }

        public void notifyDataSetChanged() {
            this.buttonsCount = 0;
            this.galleryButton = -1;
            this.documentButton = -1;
            this.musicButton = -1;
            this.pollButton = -1;
            this.contactButton = -1;
            this.locationButton = -1;
            if (!(ChatAttachAlert.this.baseFragment instanceof ChatActivity)) {
                int i = this.buttonsCount;
                int i2 = i + 1;
                this.buttonsCount = i2;
                this.galleryButton = i;
                this.buttonsCount = i2 + 1;
                this.documentButton = i2;
            } else if (ChatAttachAlert.this.editingMessageObject != null) {
                int i3 = this.buttonsCount;
                int i4 = i3 + 1;
                this.buttonsCount = i4;
                this.galleryButton = i3;
                int i5 = i4 + 1;
                this.buttonsCount = i5;
                this.documentButton = i4;
                this.buttonsCount = i5 + 1;
                this.musicButton = i5;
            } else {
                if (ChatAttachAlert.this.mediaEnabled) {
                    int i6 = this.buttonsCount;
                    int i7 = i6 + 1;
                    this.buttonsCount = i7;
                    this.galleryButton = i6;
                    this.buttonsCount = i7 + 1;
                    this.documentButton = i7;
                }
                int i8 = this.buttonsCount;
                this.buttonsCount = i8 + 1;
                this.locationButton = i8;
                if (ChatAttachAlert.this.pollsEnabled) {
                    int i9 = this.buttonsCount;
                    this.buttonsCount = i9 + 1;
                    this.pollButton = i9;
                } else {
                    int i10 = this.buttonsCount;
                    this.buttonsCount = i10 + 1;
                    this.contactButton = i10;
                }
                if (ChatAttachAlert.this.mediaEnabled) {
                    int i11 = this.buttonsCount;
                    this.buttonsCount = i11 + 1;
                    this.musicButton = i11;
                }
                TLRPC$User currentUser = ChatAttachAlert.this.baseFragment instanceof ChatActivity ? ((ChatActivity) ChatAttachAlert.this.baseFragment).getCurrentUser() : null;
                if (currentUser != null && currentUser.bot) {
                    int i12 = this.buttonsCount;
                    this.buttonsCount = i12 + 1;
                    this.contactButton = i12;
                }
            }
            super.notifyDataSetChanged();
        }

        public int getItemViewType(int i) {
            return i < this.buttonsCount ? 0 : 1;
        }
    }

    private class PhotoAttachAdapter extends RecyclerListView.SelectionAdapter {
        /* access modifiers changed from: private */
        public int itemsCount;
        private Context mContext;
        /* access modifiers changed from: private */
        public boolean needCamera;
        private ArrayList<RecyclerListView.Holder> viewsCache = new ArrayList<>(8);

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return false;
        }

        public PhotoAttachAdapter(Context context, boolean z) {
            this.mContext = context;
            this.needCamera = z;
        }

        public void createCache() {
            for (int i = 0; i < 8; i++) {
                this.viewsCache.add(createHolder());
            }
        }

        public RecyclerListView.Holder createHolder() {
            PhotoAttachPhotoCell photoAttachPhotoCell = new PhotoAttachPhotoCell(this.mContext);
            if (Build.VERSION.SDK_INT >= 21 && this == ChatAttachAlert.this.adapter) {
                photoAttachPhotoCell.setOutlineProvider(new ViewOutlineProvider() {
                    public void getOutline(View view, Outline outline) {
                        int intValue = ((Integer) ((PhotoAttachPhotoCell) view).getTag()).intValue();
                        if (PhotoAttachAdapter.this.needCamera && ChatAttachAlert.this.selectedAlbumEntry == ChatAttachAlert.this.galleryAlbumEntry) {
                            intValue++;
                        }
                        if (intValue == 0) {
                            int dp = AndroidUtilities.dp(ChatAttachAlert.this.cornerRadius * 8.0f);
                            outline.setRoundRect(0, 0, view.getMeasuredWidth() + dp, view.getMeasuredHeight() + dp, (float) dp);
                        } else if (intValue == ChatAttachAlert.this.itemsPerRow - 1) {
                            int dp2 = AndroidUtilities.dp(ChatAttachAlert.this.cornerRadius * 8.0f);
                            outline.setRoundRect(-dp2, 0, view.getMeasuredWidth(), view.getMeasuredHeight() + dp2, (float) dp2);
                        } else {
                            outline.setRect(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
                        }
                    }
                });
                photoAttachPhotoCell.setClipToOutline(true);
            }
            photoAttachPhotoCell.setDelegate(new PhotoAttachPhotoCell.PhotoAttachPhotoCellDelegate() {
                public final void onCheckClick(PhotoAttachPhotoCell photoAttachPhotoCell) {
                    ChatAttachAlert.PhotoAttachAdapter.this.lambda$createHolder$0$ChatAttachAlert$PhotoAttachAdapter(photoAttachPhotoCell);
                }
            });
            return new RecyclerListView.Holder(photoAttachPhotoCell);
        }

        public /* synthetic */ void lambda$createHolder$0$ChatAttachAlert$PhotoAttachAdapter(PhotoAttachPhotoCell photoAttachPhotoCell) {
            TLRPC$Chat currentChat;
            if (ChatAttachAlert.this.mediaEnabled) {
                int intValue = ((Integer) photoAttachPhotoCell.getTag()).intValue();
                MediaController.PhotoEntry photoEntry = photoAttachPhotoCell.getPhotoEntry();
                int i = 1;
                boolean z = !ChatAttachAlert.selectedPhotos.containsKey(Integer.valueOf(photoEntry.imageId));
                if (!z || ChatAttachAlert.this.maxSelectedPhotos < 0 || ChatAttachAlert.selectedPhotos.size() < ChatAttachAlert.this.maxSelectedPhotos) {
                    int size = z ? ChatAttachAlert.selectedPhotosOrder.size() : -1;
                    if (!(ChatAttachAlert.this.baseFragment instanceof ChatActivity) || !ChatAttachAlert.this.allowOrder) {
                        photoAttachPhotoCell.setChecked(-1, z, true);
                    } else {
                        photoAttachPhotoCell.setChecked(size, z, true);
                    }
                    int unused = ChatAttachAlert.this.addToSelectedPhotos(photoEntry, intValue);
                    if (this == ChatAttachAlert.this.cameraAttachAdapter) {
                        if (ChatAttachAlert.this.adapter.needCamera && ChatAttachAlert.this.selectedAlbumEntry == ChatAttachAlert.this.galleryAlbumEntry) {
                            intValue++;
                        }
                        ChatAttachAlert.this.adapter.notifyItemChanged(intValue);
                    } else {
                        ChatAttachAlert.this.cameraAttachAdapter.notifyItemChanged(intValue);
                    }
                    ChatAttachAlert chatAttachAlert = ChatAttachAlert.this;
                    if (!z) {
                        i = 2;
                    }
                    chatAttachAlert.updatePhotosButton(i);
                } else if (ChatAttachAlert.this.allowOrder && (ChatAttachAlert.this.baseFragment instanceof ChatActivity) && (currentChat = ((ChatActivity) ChatAttachAlert.this.baseFragment).getCurrentChat()) != null && !ChatObject.hasAdminRights(currentChat) && currentChat.slowmode_enabled && ChatAttachAlert.this.alertOnlyOnce != 2) {
                    AlertsCreator.createSimpleAlert(ChatAttachAlert.this.getContext(), LocaleController.getString("Slowmode", NUM), LocaleController.getString("SlowmodeSelectSendError", NUM)).show();
                    if (ChatAttachAlert.this.alertOnlyOnce == 1) {
                        int unused2 = ChatAttachAlert.this.alertOnlyOnce = 2;
                    }
                }
            }
        }

        /* access modifiers changed from: private */
        public MediaController.PhotoEntry getPhoto(int i) {
            if (this.needCamera && ChatAttachAlert.this.selectedAlbumEntry == ChatAttachAlert.this.galleryAlbumEntry) {
                i--;
            }
            return ChatAttachAlert.this.getPhotoEntryAtPosition(i);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            int itemViewType = viewHolder.getItemViewType();
            int i2 = 0;
            boolean z = true;
            if (itemViewType == 0) {
                if (this.needCamera && ChatAttachAlert.this.selectedAlbumEntry == ChatAttachAlert.this.galleryAlbumEntry) {
                    i--;
                }
                PhotoAttachPhotoCell photoAttachPhotoCell = (PhotoAttachPhotoCell) viewHolder.itemView;
                if (this == ChatAttachAlert.this.adapter) {
                    photoAttachPhotoCell.setItemSize(ChatAttachAlert.this.itemSize);
                } else {
                    photoAttachPhotoCell.setIsVertical(ChatAttachAlert.this.cameraPhotoLayoutManager.getOrientation() == 1);
                }
                MediaController.PhotoEntry access$000 = ChatAttachAlert.this.getPhotoEntryAtPosition(i);
                boolean z2 = this.needCamera && ChatAttachAlert.this.selectedAlbumEntry == ChatAttachAlert.this.galleryAlbumEntry;
                if (i != getItemCount() - 1) {
                    z = false;
                }
                photoAttachPhotoCell.setPhotoEntry(access$000, z2, z);
                if (!(ChatAttachAlert.this.baseFragment instanceof ChatActivity) || !ChatAttachAlert.this.allowOrder) {
                    photoAttachPhotoCell.setChecked(-1, ChatAttachAlert.selectedPhotos.containsKey(Integer.valueOf(access$000.imageId)), false);
                } else {
                    photoAttachPhotoCell.setChecked(ChatAttachAlert.selectedPhotosOrder.indexOf(Integer.valueOf(access$000.imageId)), ChatAttachAlert.selectedPhotos.containsKey(Integer.valueOf(access$000.imageId)), false);
                }
                photoAttachPhotoCell.getImageView().setTag(Integer.valueOf(i));
                photoAttachPhotoCell.setTag(Integer.valueOf(i));
            } else if (itemViewType == 1) {
                PhotoAttachCameraCell photoAttachCameraCell = (PhotoAttachCameraCell) viewHolder.itemView;
                if (ChatAttachAlert.this.cameraView == null || !ChatAttachAlert.this.cameraView.isInitied()) {
                    photoAttachCameraCell.setVisibility(0);
                } else {
                    photoAttachCameraCell.setVisibility(4);
                }
                photoAttachCameraCell.setItemSize(ChatAttachAlert.this.itemSize);
            } else if (itemViewType == 3) {
                PhotoAttachPermissionCell photoAttachPermissionCell = (PhotoAttachPermissionCell) viewHolder.itemView;
                photoAttachPermissionCell.setItemSize(ChatAttachAlert.this.itemSize);
                if (!this.needCamera || !ChatAttachAlert.this.noCameraPermissions || i != 0) {
                    i2 = 1;
                }
                photoAttachPermissionCell.setType(i2);
            }
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            if (i != 0) {
                if (i == 1) {
                    PhotoAttachCameraCell photoAttachCameraCell = new PhotoAttachCameraCell(this.mContext);
                    if (Build.VERSION.SDK_INT >= 21) {
                        photoAttachCameraCell.setOutlineProvider(new ViewOutlineProvider() {
                            public void getOutline(View view, Outline outline) {
                                int dp = AndroidUtilities.dp(ChatAttachAlert.this.cornerRadius * 8.0f);
                                outline.setRoundRect(0, 0, view.getMeasuredWidth() + dp, view.getMeasuredHeight() + dp, (float) dp);
                            }
                        });
                        photoAttachCameraCell.setClipToOutline(true);
                    }
                    return new RecyclerListView.Holder(photoAttachCameraCell);
                } else if (i != 2) {
                    return new RecyclerListView.Holder(new PhotoAttachPermissionCell(this.mContext));
                } else {
                    return new RecyclerListView.Holder(new View(this.mContext) {
                        /* access modifiers changed from: protected */
                        public void onMeasure(int i, int i2) {
                            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(ChatAttachAlert.this.gridExtraSpace, NUM));
                        }
                    });
                }
            } else if (this.viewsCache.isEmpty()) {
                return createHolder();
            } else {
                RecyclerListView.Holder holder = this.viewsCache.get(0);
                this.viewsCache.remove(0);
                return holder;
            }
        }

        public void onViewAttachedToWindow(RecyclerView.ViewHolder viewHolder) {
            View view = viewHolder.itemView;
            if (view instanceof PhotoAttachCameraCell) {
                ((PhotoAttachCameraCell) view).updateBitmap();
            }
        }

        public int getItemCount() {
            int i = 1;
            if (!ChatAttachAlert.this.mediaEnabled) {
                return 1;
            }
            if (!this.needCamera || ChatAttachAlert.this.selectedAlbumEntry != ChatAttachAlert.this.galleryAlbumEntry) {
                i = 0;
            }
            if (ChatAttachAlert.this.noGalleryPermissions && this == ChatAttachAlert.this.adapter) {
                i++;
            }
            int size = i + ChatAttachAlert.cameraPhotos.size();
            if (ChatAttachAlert.this.selectedAlbumEntry != null) {
                size += ChatAttachAlert.this.selectedAlbumEntry.photos.size();
            }
            if (this == ChatAttachAlert.this.adapter) {
                size++;
            }
            this.itemsCount = size;
            return size;
        }

        public int getItemViewType(int i) {
            if (!ChatAttachAlert.this.mediaEnabled) {
                return 2;
            }
            if (this.needCamera && i == 0 && ChatAttachAlert.this.selectedAlbumEntry == ChatAttachAlert.this.galleryAlbumEntry) {
                return ChatAttachAlert.this.noCameraPermissions ? 3 : 1;
            }
            if (this == ChatAttachAlert.this.adapter && i == this.itemsCount - 1) {
                return 2;
            }
            if (ChatAttachAlert.this.noGalleryPermissions) {
                return 3;
            }
            return 0;
        }

        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            if (this == ChatAttachAlert.this.adapter) {
                ChatAttachAlert.this.progressView.setVisibility((!(getItemCount() == 1 && ChatAttachAlert.this.selectedAlbumEntry == null) && ChatAttachAlert.this.mediaEnabled) ? 4 : 0);
            }
        }
    }

    public void dismissInternal() {
        ViewGroup viewGroup = this.containerView;
        if (viewGroup != null) {
            viewGroup.setVisibility(4);
        }
        super.dismissInternal();
    }

    public void onBackPressed() {
        EditTextEmoji editTextEmoji = this.commentTextView;
        if (editTextEmoji == null || !editTextEmoji.isPopupShowing()) {
            super.onBackPressed();
        } else {
            this.commentTextView.hidePopup(true);
        }
    }

    public void dismissWithButtonClick(int i) {
        super.dismissWithButtonClick(i);
        hideCamera((i == 0 || i == 2) ? false : true);
    }

    /* access modifiers changed from: protected */
    public boolean canDismissWithTouchOutside() {
        return !this.cameraOpened;
    }

    public void dismiss() {
        if (!this.cameraAnimationInProgress) {
            if (this.cameraOpened) {
                closeCamera(true);
                return;
            }
            EditTextEmoji editTextEmoji = this.commentTextView;
            if (editTextEmoji != null) {
                AndroidUtilities.hideKeyboard(editTextEmoji.getEditText());
            }
            hideCamera(true);
            super.dismiss();
        }
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (!this.cameraOpened || (i != 24 && i != 25)) {
            return super.onKeyDown(i, keyEvent);
        }
        this.shutterButton.getDelegate().shutterReleased();
        return true;
    }
}
