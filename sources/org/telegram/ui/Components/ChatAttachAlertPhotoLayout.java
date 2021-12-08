package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.util.Property;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.exifinterface.media.ExifInterface;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.messenger.camera.CameraController;
import org.telegram.messenger.camera.CameraView;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
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

public class ChatAttachAlertPhotoLayout extends ChatAttachAlert.AttachAlertLayout implements NotificationCenter.NotificationCenterDelegate {
    /* access modifiers changed from: private */
    public static ArrayList<Object> cameraPhotos = new ArrayList<>();
    private static final int compress = 1;
    private static final int group = 0;
    private static int lastImageId = -1;
    /* access modifiers changed from: private */
    public static boolean mediaFromExternalCamera = false;
    private static final int open_in = 2;
    /* access modifiers changed from: private */
    public static HashMap<Object, Object> selectedPhotos = new HashMap<>();
    /* access modifiers changed from: private */
    public static ArrayList<Object> selectedPhotosOrder = new ArrayList<>();
    /* access modifiers changed from: private */
    public PhotoAttachAdapter adapter;
    float additionCloseCameraY;
    /* access modifiers changed from: private */
    public int alertOnlyOnce;
    private int[] animateCameraValues = new int[5];
    private int animateToPadding;
    float animationClipBottom;
    float animationClipLeft;
    float animationClipRight;
    float animationClipTop;
    /* access modifiers changed from: private */
    public int animationIndex;
    /* access modifiers changed from: private */
    public boolean cameraAnimationInProgress;
    /* access modifiers changed from: private */
    public PhotoAttachAdapter cameraAttachAdapter;
    /* access modifiers changed from: private */
    public Drawable cameraDrawable;
    boolean cameraExpanded;
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
    private float[] cameraViewLocation = new float[2];
    private float cameraViewOffsetBottomY;
    /* access modifiers changed from: private */
    public float cameraViewOffsetX;
    /* access modifiers changed from: private */
    public float cameraViewOffsetY;
    /* access modifiers changed from: private */
    public float cameraZoom;
    /* access modifiers changed from: private */
    public boolean canSaveCameraPreview;
    /* access modifiers changed from: private */
    public boolean cancelTakingPhotos;
    private boolean checkCameraWhenShown;
    /* access modifiers changed from: private */
    public TextView counterTextView;
    private float currentPanTranslationY;
    private int currentSelectedCount;
    private boolean deviceHasGoodCamera;
    private boolean dragging;
    /* access modifiers changed from: private */
    public TextView dropDown;
    private ArrayList<MediaController.AlbumEntry> dropDownAlbums;
    private ActionBarMenuItem dropDownContainer;
    private Drawable dropDownDrawable;
    /* access modifiers changed from: private */
    public boolean flashAnimationInProgress;
    /* access modifiers changed from: private */
    public ImageView[] flashModeButton = new ImageView[2];
    boolean forceDarkTheme;
    /* access modifiers changed from: private */
    public MediaController.AlbumEntry galleryAlbumEntry;
    /* access modifiers changed from: private */
    public int gridExtraSpace;
    /* access modifiers changed from: private */
    public RecyclerListView gridView;
    private Rect hitRect = new Rect();
    private boolean ignoreLayout;
    private DecelerateInterpolator interpolator = new DecelerateInterpolator(1.5f);
    /* access modifiers changed from: private */
    public boolean isHidden;
    private RecyclerViewItemRangeSelector itemRangeSelector;
    /* access modifiers changed from: private */
    public int itemSize;
    /* access modifiers changed from: private */
    public int itemsPerRow;
    private int lastItemSize;
    private int lastNotifyWidth;
    private float lastY;
    /* access modifiers changed from: private */
    public GridLayoutManager layoutManager;
    private boolean loading;
    private boolean maybeStartDraging;
    /* access modifiers changed from: private */
    public boolean mediaEnabled;
    /* access modifiers changed from: private */
    public boolean noCameraPermissions;
    /* access modifiers changed from: private */
    public boolean noGalleryPermissions;
    ValueAnimator paddingAnimator;
    private PhotoViewer.PhotoViewerProvider photoViewerProvider;
    private float pinchStartDistance;
    private boolean pressed;
    /* access modifiers changed from: private */
    public EmptyTextProgressView progressView;
    /* access modifiers changed from: private */
    public TextView recordTime;
    /* access modifiers changed from: private */
    public boolean requestingPermissions;
    /* access modifiers changed from: private */
    public MediaController.AlbumEntry selectedAlbumEntry;
    /* access modifiers changed from: private */
    public boolean shouldSelect;
    /* access modifiers changed from: private */
    public ShutterButton shutterButton;
    /* access modifiers changed from: private */
    public ImageView switchCameraButton;
    /* access modifiers changed from: private */
    public boolean takingPhoto;
    /* access modifiers changed from: private */
    public TextView tooltipTextView;
    /* access modifiers changed from: private */
    public Runnable videoRecordRunnable;
    /* access modifiers changed from: private */
    public int videoRecordTime;
    private int[] viewPosition = new int[2];
    /* access modifiers changed from: private */
    public AnimatorSet zoomControlAnimation;
    private Runnable zoomControlHideRunnable;
    /* access modifiers changed from: private */
    public ZoomControlView zoomControlView;
    private boolean zoomWas;
    private boolean zooming;

    static /* synthetic */ int access$2608(ChatAttachAlertPhotoLayout x0) {
        int i = x0.videoRecordTime;
        x0.videoRecordTime = i + 1;
        return i;
    }

    static /* synthetic */ int access$3210() {
        int i = lastImageId;
        lastImageId = i - 1;
        return i;
    }

    private class BasePhotoProvider extends PhotoViewer.EmptyPhotoViewerProvider {
        private BasePhotoProvider() {
        }

        public boolean isPhotoChecked(int index) {
            MediaController.PhotoEntry photoEntry = ChatAttachAlertPhotoLayout.this.getPhotoEntryAtPosition(index);
            return photoEntry != null && ChatAttachAlertPhotoLayout.selectedPhotos.containsKey(Integer.valueOf(photoEntry.imageId));
        }

        public int setPhotoChecked(int index, VideoEditedInfo videoEditedInfo) {
            MediaController.PhotoEntry photoEntry;
            if ((ChatAttachAlertPhotoLayout.this.parentAlert.maxSelectedPhotos >= 0 && ChatAttachAlertPhotoLayout.selectedPhotos.size() >= ChatAttachAlertPhotoLayout.this.parentAlert.maxSelectedPhotos && !isPhotoChecked(index)) || (photoEntry = ChatAttachAlertPhotoLayout.this.getPhotoEntryAtPosition(index)) == null) {
                return -1;
            }
            boolean add = true;
            int access$200 = ChatAttachAlertPhotoLayout.this.addToSelectedPhotos(photoEntry, -1);
            int num = access$200;
            if (access$200 == -1) {
                num = ChatAttachAlertPhotoLayout.selectedPhotosOrder.indexOf(Integer.valueOf(photoEntry.imageId));
            } else {
                add = false;
                photoEntry.editedInfo = null;
            }
            photoEntry.editedInfo = videoEditedInfo;
            int count = ChatAttachAlertPhotoLayout.this.gridView.getChildCount();
            int a = 0;
            while (true) {
                if (a >= count) {
                    break;
                }
                View view = ChatAttachAlertPhotoLayout.this.gridView.getChildAt(a);
                if (!(view instanceof PhotoAttachPhotoCell) || ((Integer) view.getTag()).intValue() != index) {
                    a++;
                } else if (!(ChatAttachAlertPhotoLayout.this.parentAlert.baseFragment instanceof ChatActivity) || !ChatAttachAlertPhotoLayout.this.parentAlert.allowOrder) {
                    ((PhotoAttachPhotoCell) view).setChecked(-1, add, false);
                } else {
                    ((PhotoAttachPhotoCell) view).setChecked(num, add, false);
                }
            }
            int count2 = ChatAttachAlertPhotoLayout.this.cameraPhotoRecyclerView.getChildCount();
            int a2 = 0;
            while (true) {
                if (a2 >= count2) {
                    break;
                }
                View view2 = ChatAttachAlertPhotoLayout.this.cameraPhotoRecyclerView.getChildAt(a2);
                if (!(view2 instanceof PhotoAttachPhotoCell) || ((Integer) view2.getTag()).intValue() != index) {
                    a2++;
                } else if (!(ChatAttachAlertPhotoLayout.this.parentAlert.baseFragment instanceof ChatActivity) || !ChatAttachAlertPhotoLayout.this.parentAlert.allowOrder) {
                    ((PhotoAttachPhotoCell) view2).setChecked(-1, add, false);
                } else {
                    ((PhotoAttachPhotoCell) view2).setChecked(num, add, false);
                }
            }
            ChatAttachAlertPhotoLayout.this.parentAlert.updateCountButton(add ? 1 : 2);
            return num;
        }

        public int getSelectedCount() {
            return ChatAttachAlertPhotoLayout.selectedPhotos.size();
        }

        public ArrayList<Object> getSelectedPhotosOrder() {
            return ChatAttachAlertPhotoLayout.selectedPhotosOrder;
        }

        public HashMap<Object, Object> getSelectedPhotos() {
            return ChatAttachAlertPhotoLayout.selectedPhotos;
        }

        public int getPhotoIndex(int index) {
            MediaController.PhotoEntry photoEntry = ChatAttachAlertPhotoLayout.this.getPhotoEntryAtPosition(index);
            if (photoEntry == null) {
                return -1;
            }
            return ChatAttachAlertPhotoLayout.selectedPhotosOrder.indexOf(Integer.valueOf(photoEntry.imageId));
        }
    }

    private void updateCheckedPhotoIndices() {
        if (this.parentAlert.baseFragment instanceof ChatActivity) {
            int count = this.gridView.getChildCount();
            for (int a = 0; a < count; a++) {
                View view = this.gridView.getChildAt(a);
                if (view instanceof PhotoAttachPhotoCell) {
                    PhotoAttachPhotoCell cell = (PhotoAttachPhotoCell) view;
                    MediaController.PhotoEntry photoEntry = getPhotoEntryAtPosition(((Integer) cell.getTag()).intValue());
                    if (photoEntry != null) {
                        cell.setNum(selectedPhotosOrder.indexOf(Integer.valueOf(photoEntry.imageId)));
                    }
                }
            }
            int count2 = this.cameraPhotoRecyclerView.getChildCount();
            for (int a2 = 0; a2 < count2; a2++) {
                View view2 = this.cameraPhotoRecyclerView.getChildAt(a2);
                if (view2 instanceof PhotoAttachPhotoCell) {
                    PhotoAttachPhotoCell cell2 = (PhotoAttachPhotoCell) view2;
                    MediaController.PhotoEntry photoEntry2 = getPhotoEntryAtPosition(((Integer) cell2.getTag()).intValue());
                    if (photoEntry2 != null) {
                        cell2.setNum(selectedPhotosOrder.indexOf(Integer.valueOf(photoEntry2.imageId)));
                    }
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public MediaController.PhotoEntry getPhotoEntryAtPosition(int position) {
        if (position < 0) {
            return null;
        }
        int cameraCount = cameraPhotos.size();
        if (position < cameraCount) {
            return (MediaController.PhotoEntry) cameraPhotos.get(position);
        }
        int position2 = position - cameraCount;
        if (position2 < this.selectedAlbumEntry.photos.size()) {
            return this.selectedAlbumEntry.photos.get(position2);
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

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public ChatAttachAlertPhotoLayout(org.telegram.ui.Components.ChatAttachAlert r30, android.content.Context r31, boolean r32, org.telegram.ui.ActionBar.Theme.ResourcesProvider r33) {
        /*
            r29 = this;
            r7 = r29
            r8 = r31
            r9 = r33
            r10 = r30
            r7.<init>(r10, r8, r9)
            r11 = 2
            android.widget.ImageView[] r0 = new android.widget.ImageView[r11]
            r7.flashModeButton = r0
            float[] r0 = new float[r11]
            r7.cameraViewLocation = r0
            int[] r0 = new int[r11]
            r7.viewPosition = r0
            r0 = 5
            int[] r0 = new int[r0]
            r7.animateCameraValues = r0
            android.view.animation.DecelerateInterpolator r0 = new android.view.animation.DecelerateInterpolator
            r1 = 1069547520(0x3fCLASSNAME, float:1.5)
            r0.<init>(r1)
            r7.interpolator = r0
            android.graphics.Rect r0 = new android.graphics.Rect
            r0.<init>()
            r7.hitRect = r0
            r12 = 1117782016(0x42a00000, float:80.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r12)
            r7.itemSize = r0
            r7.lastItemSize = r0
            r13 = 3
            r7.itemsPerRow = r13
            r14 = 1
            r7.loading = r14
            r15 = -1
            r7.animationIndex = r15
            org.telegram.ui.Components.ChatAttachAlertPhotoLayout$1 r0 = new org.telegram.ui.Components.ChatAttachAlertPhotoLayout$1
            r0.<init>()
            r7.photoViewerProvider = r0
            r6 = r32
            r7.forceDarkTheme = r6
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r1 = org.telegram.messenger.NotificationCenter.albumsDidLoad
            r0.addObserver(r7, r1)
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r1 = org.telegram.messenger.NotificationCenter.cameraInitied
            r0.addObserver(r7, r1)
            android.widget.FrameLayout r5 = r30.getContainer()
            android.content.res.Resources r0 = r31.getResources()
            r1 = 2131165569(0x7var_, float:1.7945359E38)
            android.graphics.drawable.Drawable r0 = r0.getDrawable(r1)
            android.graphics.drawable.Drawable r0 = r0.mutate()
            r7.cameraDrawable = r0
            org.telegram.ui.Components.ChatAttachAlert r0 = r7.parentAlert
            org.telegram.ui.ActionBar.ActionBar r0 = r0.actionBar
            org.telegram.ui.ActionBar.ActionBarMenu r16 = r0.createMenu()
            org.telegram.ui.Components.ChatAttachAlertPhotoLayout$2 r4 = new org.telegram.ui.Components.ChatAttachAlertPhotoLayout$2
            r17 = 0
            r18 = 0
            r0 = r4
            r1 = r29
            r2 = r31
            r3 = r16
            r12 = r4
            r4 = r17
            r19 = r5
            r5 = r18
            r6 = r33
            r0.<init>(r2, r3, r4, r5, r6)
            r7.dropDownContainer = r12
            r12.setSubMenuOpenSide(r14)
            org.telegram.ui.Components.ChatAttachAlert r0 = r7.parentAlert
            org.telegram.ui.ActionBar.ActionBar r0 = r0.actionBar
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r7.dropDownContainer
            boolean r2 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r2 == 0) goto L_0x00a9
            r2 = 1115684864(0x42800000, float:64.0)
            r23 = 1115684864(0x42800000, float:64.0)
            goto L_0x00ad
        L_0x00a9:
            r2 = 1113587712(0x42600000, float:56.0)
            r23 = 1113587712(0x42600000, float:56.0)
        L_0x00ad:
            r24 = 0
            r25 = 1109393408(0x42200000, float:40.0)
            r26 = 0
            r20 = -2
            r21 = -1082130432(0xffffffffbvar_, float:-1.0)
            r22 = 51
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r20, r21, r22, r23, r24, r25, r26)
            r3 = 0
            r0.addView(r1, r3, r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r7.dropDownContainer
            org.telegram.ui.Components.ChatAttachAlertPhotoLayout$$ExternalSyntheticLambda0 r1 = new org.telegram.ui.Components.ChatAttachAlertPhotoLayout$$ExternalSyntheticLambda0
            r1.<init>(r7)
            r0.setOnClickListener(r1)
            android.widget.TextView r0 = new android.widget.TextView
            r0.<init>(r8)
            r7.dropDown = r0
            r0.setImportantForAccessibility(r11)
            android.widget.TextView r0 = r7.dropDown
            r0.setGravity(r13)
            android.widget.TextView r0 = r7.dropDown
            r0.setSingleLine(r14)
            android.widget.TextView r0 = r7.dropDown
            r0.setLines(r14)
            android.widget.TextView r0 = r7.dropDown
            r0.setMaxLines(r14)
            android.widget.TextView r0 = r7.dropDown
            android.text.TextUtils$TruncateAt r1 = android.text.TextUtils.TruncateAt.END
            r0.setEllipsize(r1)
            android.widget.TextView r0 = r7.dropDown
            java.lang.String r1 = "dialogTextBlack"
            int r2 = r7.getThemedColor(r1)
            r0.setTextColor(r2)
            android.widget.TextView r0 = r7.dropDown
            r2 = 2131624867(0x7f0e03a3, float:1.8876926E38)
            java.lang.String r4 = "ChatGallery"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r4, r2)
            r0.setText(r2)
            android.widget.TextView r0 = r7.dropDown
            java.lang.String r2 = "fonts/rmedium.ttf"
            android.graphics.Typeface r4 = org.telegram.messenger.AndroidUtilities.getTypeface(r2)
            r0.setTypeface(r4)
            android.content.res.Resources r0 = r31.getResources()
            r4 = 2131165503(0x7var_f, float:1.7945225E38)
            android.graphics.drawable.Drawable r0 = r0.getDrawable(r4)
            android.graphics.drawable.Drawable r0 = r0.mutate()
            r7.dropDownDrawable = r0
            android.graphics.PorterDuffColorFilter r4 = new android.graphics.PorterDuffColorFilter
            int r1 = r7.getThemedColor(r1)
            android.graphics.PorterDuff$Mode r5 = android.graphics.PorterDuff.Mode.MULTIPLY
            r4.<init>(r1, r5)
            r0.setColorFilter(r4)
            android.widget.TextView r0 = r7.dropDown
            r1 = 1082130432(0x40800000, float:4.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r0.setCompoundDrawablePadding(r4)
            android.widget.TextView r0 = r7.dropDown
            r4 = 1092616192(0x41200000, float:10.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r0.setPadding(r3, r3, r5, r3)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r7.dropDownContainer
            android.widget.TextView r5 = r7.dropDown
            r21 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r22 = 16
            r23 = 1098907648(0x41800000, float:16.0)
            r25 = 0
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r20, r21, r22, r23, r24, r25, r26)
            r0.addView(r5, r6)
            r7.checkCamera(r3)
            org.telegram.ui.Components.ChatAttachAlert r0 = r7.parentAlert
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r0.selectedMenuItem
            r5 = 2131627733(0x7f0e0ed5, float:1.8882739E38)
            java.lang.String r6 = "SendWithoutGrouping"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r0.addSubItem(r3, r5)
            org.telegram.ui.Components.ChatAttachAlert r0 = r7.parentAlert
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r0.selectedMenuItem
            r5 = 2131627732(0x7f0e0ed4, float:1.8882737E38)
            java.lang.String r6 = "SendWithoutCompression"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r0.addSubItem(r14, r5)
            org.telegram.ui.Components.ChatAttachAlert r0 = r7.parentAlert
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r0.selectedMenuItem
            r5 = 2131165814(0x7var_, float:1.7945856E38)
            r6 = 2131626777(0x7f0e0b19, float:1.88808E38)
            java.lang.String r12 = "OpenInExternalApp"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r12, r6)
            r0.addSubItem((int) r11, (int) r5, (java.lang.CharSequence) r6)
            org.telegram.ui.Components.ChatAttachAlertPhotoLayout$3 r0 = new org.telegram.ui.Components.ChatAttachAlertPhotoLayout$3
            r0.<init>(r8, r9)
            r7.gridView = r0
            org.telegram.ui.Components.ChatAttachAlertPhotoLayout$PhotoAttachAdapter r5 = new org.telegram.ui.Components.ChatAttachAlertPhotoLayout$PhotoAttachAdapter
            r5.<init>(r8, r14)
            r7.adapter = r5
            r0.setAdapter(r5)
            org.telegram.ui.Components.ChatAttachAlertPhotoLayout$PhotoAttachAdapter r0 = r7.adapter
            r0.createCache()
            org.telegram.ui.Components.RecyclerListView r0 = r7.gridView
            r0.setClipToPadding(r3)
            org.telegram.ui.Components.RecyclerListView r0 = r7.gridView
            r5 = 0
            r0.setItemAnimator(r5)
            org.telegram.ui.Components.RecyclerListView r0 = r7.gridView
            r0.setLayoutAnimation(r5)
            org.telegram.ui.Components.RecyclerListView r0 = r7.gridView
            r0.setVerticalScrollBarEnabled(r3)
            org.telegram.ui.Components.RecyclerListView r0 = r7.gridView
            java.lang.String r6 = "dialogScrollGlow"
            int r6 = r7.getThemedColor(r6)
            r0.setGlowColor(r6)
            org.telegram.ui.Components.RecyclerListView r0 = r7.gridView
            r6 = -1082130432(0xffffffffbvar_, float:-1.0)
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r15, r6)
            r7.addView(r0, r6)
            org.telegram.ui.Components.RecyclerListView r0 = r7.gridView
            org.telegram.ui.Components.ChatAttachAlertPhotoLayout$4 r6 = new org.telegram.ui.Components.ChatAttachAlertPhotoLayout$4
            r6.<init>()
            r0.setOnScrollListener(r6)
            org.telegram.ui.Components.ChatAttachAlertPhotoLayout$5 r0 = new org.telegram.ui.Components.ChatAttachAlertPhotoLayout$5
            int r6 = r7.itemSize
            r0.<init>(r8, r6)
            r7.layoutManager = r0
            org.telegram.ui.Components.ChatAttachAlertPhotoLayout$6 r6 = new org.telegram.ui.Components.ChatAttachAlertPhotoLayout$6
            r6.<init>()
            r0.setSpanSizeLookup(r6)
            org.telegram.ui.Components.RecyclerListView r0 = r7.gridView
            androidx.recyclerview.widget.GridLayoutManager r6 = r7.layoutManager
            r0.setLayoutManager(r6)
            org.telegram.ui.Components.RecyclerListView r0 = r7.gridView
            org.telegram.ui.Components.ChatAttachAlertPhotoLayout$$ExternalSyntheticLambda4 r6 = new org.telegram.ui.Components.ChatAttachAlertPhotoLayout$$ExternalSyntheticLambda4
            r6.<init>(r7, r9)
            r0.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r6)
            org.telegram.ui.Components.RecyclerListView r0 = r7.gridView
            org.telegram.ui.Components.ChatAttachAlertPhotoLayout$$ExternalSyntheticLambda6 r6 = new org.telegram.ui.Components.ChatAttachAlertPhotoLayout$$ExternalSyntheticLambda6
            r6.<init>(r7)
            r0.setOnItemLongClickListener((org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener) r6)
            org.telegram.ui.Components.RecyclerViewItemRangeSelector r0 = new org.telegram.ui.Components.RecyclerViewItemRangeSelector
            org.telegram.ui.Components.ChatAttachAlertPhotoLayout$7 r6 = new org.telegram.ui.Components.ChatAttachAlertPhotoLayout$7
            r6.<init>()
            r0.<init>(r6)
            r7.itemRangeSelector = r0
            org.telegram.ui.Components.RecyclerListView r6 = r7.gridView
            r6.addOnItemTouchListener(r0)
            org.telegram.ui.Components.EmptyTextProgressView r0 = new org.telegram.ui.Components.EmptyTextProgressView
            r0.<init>(r8, r5, r9)
            r7.progressView = r0
            r6 = 2131626535(0x7f0e0a27, float:1.8880309E38)
            java.lang.String r12 = "NoPhotos"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r12, r6)
            r0.setText(r6)
            org.telegram.ui.Components.EmptyTextProgressView r0 = r7.progressView
            r0.setOnTouchListener(r5)
            org.telegram.ui.Components.EmptyTextProgressView r0 = r7.progressView
            r6 = 20
            r0.setTextSize(r6)
            org.telegram.ui.Components.EmptyTextProgressView r0 = r7.progressView
            r6 = 1117782016(0x42a00000, float:80.0)
            android.widget.FrameLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createFrame(r15, r6)
            r7.addView(r0, r12)
            boolean r0 = r7.loading
            if (r0 == 0) goto L_0x024e
            org.telegram.ui.Components.EmptyTextProgressView r0 = r7.progressView
            r0.showProgress()
            goto L_0x0253
        L_0x024e:
            org.telegram.ui.Components.EmptyTextProgressView r0 = r7.progressView
            r0.showTextView()
        L_0x0253:
            android.graphics.Paint r0 = new android.graphics.Paint
            r0.<init>(r14)
            r6 = -2468275(0xffffffffffda564d, float:NaN)
            r0.setColor(r6)
            org.telegram.ui.Components.ChatAttachAlertPhotoLayout$8 r6 = new org.telegram.ui.Components.ChatAttachAlertPhotoLayout$8
            r6.<init>(r8, r0)
            r7.recordTime = r6
            r12 = 1065353216(0x3var_, float:1.0)
            org.telegram.messenger.AndroidUtilities.updateViewVisibilityAnimated(r6, r3, r12, r3)
            android.widget.TextView r6 = r7.recordTime
            r12 = 2131166128(0x7var_b0, float:1.7946493E38)
            r6.setBackgroundResource(r12)
            android.widget.TextView r6 = r7.recordTime
            android.graphics.drawable.Drawable r6 = r6.getBackground()
            android.graphics.PorterDuffColorFilter r12 = new android.graphics.PorterDuffColorFilter
            r13 = 1711276032(0x66000000, float:1.5111573E23)
            android.graphics.PorterDuff$Mode r5 = android.graphics.PorterDuff.Mode.MULTIPLY
            r12.<init>(r13, r5)
            r6.setColorFilter(r12)
            android.widget.TextView r5 = r7.recordTime
            r6 = 1097859072(0x41700000, float:15.0)
            r5.setTextSize(r14, r6)
            android.widget.TextView r5 = r7.recordTime
            android.graphics.Typeface r12 = org.telegram.messenger.AndroidUtilities.getTypeface(r2)
            r5.setTypeface(r12)
            android.widget.TextView r5 = r7.recordTime
            r12 = 0
            r5.setAlpha(r12)
            android.widget.TextView r5 = r7.recordTime
            r5.setTextColor(r15)
            android.widget.TextView r5 = r7.recordTime
            r13 = 1103101952(0x41CLASSNAME, float:24.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            r18 = 1084227584(0x40a00000, float:5.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r18)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r5.setPadding(r13, r6, r4, r11)
            android.widget.TextView r4 = r7.recordTime
            r22 = -2
            r23 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r24 = 49
            r25 = 0
            r26 = 1098907648(0x41800000, float:16.0)
            r27 = 0
            r28 = 0
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r22, r23, r24, r25, r26, r27, r28)
            r6 = r19
            r6.addView(r4, r5)
            org.telegram.ui.Components.ChatAttachAlertPhotoLayout$9 r4 = new org.telegram.ui.Components.ChatAttachAlertPhotoLayout$9
            r4.<init>(r8)
            r7.cameraPanel = r4
            r5 = 8
            r4.setVisibility(r5)
            android.widget.FrameLayout r4 = r7.cameraPanel
            r4.setAlpha(r12)
            android.widget.FrameLayout r4 = r7.cameraPanel
            r11 = 126(0x7e, float:1.77E-43)
            r13 = 83
            android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r15, (int) r11, (int) r13)
            r6.addView(r4, r11)
            android.widget.TextView r4 = new android.widget.TextView
            r4.<init>(r8)
            r7.counterTextView = r4
            r11 = 2131165978(0x7var_a, float:1.7946188E38)
            r4.setBackgroundResource(r11)
            android.widget.TextView r4 = r7.counterTextView
            r4.setVisibility(r5)
            android.widget.TextView r4 = r7.counterTextView
            r4.setTextColor(r15)
            android.widget.TextView r4 = r7.counterTextView
            r11 = 17
            r4.setGravity(r11)
            android.widget.TextView r4 = r7.counterTextView
            r4.setPivotX(r12)
            android.widget.TextView r4 = r7.counterTextView
            r4.setPivotY(r12)
            android.widget.TextView r4 = r7.counterTextView
            android.graphics.Typeface r2 = org.telegram.messenger.AndroidUtilities.getTypeface(r2)
            r4.setTypeface(r2)
            android.widget.TextView r2 = r7.counterTextView
            r4 = 2131165976(0x7var_, float:1.7946184E38)
            r2.setCompoundDrawablesWithIntrinsicBounds(r3, r3, r4, r3)
            android.widget.TextView r2 = r7.counterTextView
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r2.setCompoundDrawablePadding(r1)
            android.widget.TextView r1 = r7.counterTextView
            r2 = 1098907648(0x41800000, float:16.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.setPadding(r4, r3, r2, r3)
            android.widget.TextView r1 = r7.counterTextView
            r23 = 1108869120(0x42180000, float:38.0)
            r24 = 51
            r26 = 0
            r28 = 1122500608(0x42e80000, float:116.0)
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r22, r23, r24, r25, r26, r27, r28)
            r6.addView(r1, r2)
            android.widget.TextView r1 = r7.counterTextView
            org.telegram.ui.Components.ChatAttachAlertPhotoLayout$$ExternalSyntheticLambda8 r2 = new org.telegram.ui.Components.ChatAttachAlertPhotoLayout$$ExternalSyntheticLambda8
            r2.<init>(r7)
            r1.setOnClickListener(r2)
            org.telegram.ui.Components.ZoomControlView r1 = new org.telegram.ui.Components.ZoomControlView
            r1.<init>(r8)
            r7.zoomControlView = r1
            r1.setVisibility(r5)
            org.telegram.ui.Components.ZoomControlView r1 = r7.zoomControlView
            r1.setAlpha(r12)
            org.telegram.ui.Components.ZoomControlView r1 = r7.zoomControlView
            r23 = 1112014848(0x42480000, float:50.0)
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r22, r23, r24, r25, r26, r27, r28)
            r6.addView(r1, r2)
            org.telegram.ui.Components.ZoomControlView r1 = r7.zoomControlView
            org.telegram.ui.Components.ChatAttachAlertPhotoLayout$$ExternalSyntheticLambda7 r2 = new org.telegram.ui.Components.ChatAttachAlertPhotoLayout$$ExternalSyntheticLambda7
            r2.<init>(r7)
            r1.setDelegate(r2)
            org.telegram.ui.Components.ShutterButton r1 = new org.telegram.ui.Components.ShutterButton
            r1.<init>(r8)
            r7.shutterButton = r1
            android.widget.FrameLayout r2 = r7.cameraPanel
            r4 = 84
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r4, (int) r4, (int) r11)
            r2.addView(r1, r4)
            org.telegram.ui.Components.ShutterButton r1 = r7.shutterButton
            org.telegram.ui.Components.ChatAttachAlertPhotoLayout$10 r2 = new org.telegram.ui.Components.ChatAttachAlertPhotoLayout$10
            r2.<init>(r6)
            r1.setDelegate(r2)
            org.telegram.ui.Components.ShutterButton r1 = r7.shutterButton
            r1.setFocusable(r14)
            org.telegram.ui.Components.ShutterButton r1 = r7.shutterButton
            r2 = 2131624052(0x7f0e0074, float:1.8875273E38)
            java.lang.String r4 = "AccDescrShutter"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r4, r2)
            r1.setContentDescription(r2)
            android.widget.ImageView r1 = new android.widget.ImageView
            r1.<init>(r8)
            r7.switchCameraButton = r1
            android.widget.ImageView$ScaleType r2 = android.widget.ImageView.ScaleType.CENTER
            r1.setScaleType(r2)
            android.widget.FrameLayout r1 = r7.cameraPanel
            android.widget.ImageView r2 = r7.switchCameraButton
            r4 = 21
            r5 = 48
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r5, (int) r5, (int) r4)
            r1.addView(r2, r4)
            android.widget.ImageView r1 = r7.switchCameraButton
            org.telegram.ui.Components.ChatAttachAlertPhotoLayout$$ExternalSyntheticLambda9 r2 = new org.telegram.ui.Components.ChatAttachAlertPhotoLayout$$ExternalSyntheticLambda9
            r2.<init>(r7)
            r1.setOnClickListener(r2)
            android.widget.ImageView r1 = r7.switchCameraButton
            r2 = 2131624056(0x7f0e0078, float:1.887528E38)
            java.lang.String r4 = "AccDescrSwitchCamera"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r4, r2)
            r1.setContentDescription(r2)
            r1 = 0
        L_0x03e2:
            r2 = 4
            r4 = 2
            if (r1 >= r4) goto L_0x0435
            android.widget.ImageView[] r4 = r7.flashModeButton
            android.widget.ImageView r11 = new android.widget.ImageView
            r11.<init>(r8)
            r4[r1] = r11
            android.widget.ImageView[] r4 = r7.flashModeButton
            r4 = r4[r1]
            android.widget.ImageView$ScaleType r11 = android.widget.ImageView.ScaleType.CENTER
            r4.setScaleType(r11)
            android.widget.ImageView[] r4 = r7.flashModeButton
            r4 = r4[r1]
            r4.setVisibility(r2)
            android.widget.FrameLayout r2 = r7.cameraPanel
            android.widget.ImageView[] r4 = r7.flashModeButton
            r4 = r4[r1]
            r11 = 51
            android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r5, (int) r5, (int) r11)
            r2.addView(r4, r11)
            android.widget.ImageView[] r2 = r7.flashModeButton
            r2 = r2[r1]
            org.telegram.ui.Components.ChatAttachAlertPhotoLayout$$ExternalSyntheticLambda10 r4 = new org.telegram.ui.Components.ChatAttachAlertPhotoLayout$$ExternalSyntheticLambda10
            r4.<init>(r7)
            r2.setOnClickListener(r4)
            android.widget.ImageView[] r2 = r7.flashModeButton
            r2 = r2[r1]
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r11 = "flash mode "
            r4.append(r11)
            r4.append(r1)
            java.lang.String r4 = r4.toString()
            r2.setContentDescription(r4)
            int r1 = r1 + 1
            goto L_0x03e2
        L_0x0435:
            android.widget.TextView r1 = new android.widget.TextView
            r1.<init>(r8)
            r7.tooltipTextView = r1
            r4 = 1097859072(0x41700000, float:15.0)
            r1.setTextSize(r14, r4)
            android.widget.TextView r1 = r7.tooltipTextView
            r1.setTextColor(r15)
            android.widget.TextView r1 = r7.tooltipTextView
            r4 = 2131628034(0x7f0e1002, float:1.888335E38)
            java.lang.String r5 = "TapForVideo"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r1.setText(r4)
            android.widget.TextView r1 = r7.tooltipTextView
            r4 = 1079334215(0x40555547, float:3.33333)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            r5 = 1059749626(0x3f2a7efa, float:0.666)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r5
            r11 = 1275068416(0x4CLASSNAME, float:3.3554432E7)
            r1.setShadowLayer(r4, r12, r5, r11)
            android.widget.TextView r1 = r7.tooltipTextView
            r4 = 1086324736(0x40CLASSNAME, float:6.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r1.setPadding(r5, r3, r4, r3)
            android.widget.FrameLayout r1 = r7.cameraPanel
            android.widget.TextView r4 = r7.tooltipTextView
            r22 = -2
            r23 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r24 = 81
            r25 = 0
            r26 = 0
            r27 = 0
            r28 = 1098907648(0x41800000, float:16.0)
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r22, r23, r24, r25, r26, r27, r28)
            r1.addView(r4, r5)
            org.telegram.ui.Components.ChatAttachAlertPhotoLayout$13 r1 = new org.telegram.ui.Components.ChatAttachAlertPhotoLayout$13
            r1.<init>(r8, r9)
            r7.cameraPhotoRecyclerView = r1
            r1.setVerticalScrollBarEnabled(r14)
            org.telegram.ui.Components.RecyclerListView r1 = r7.cameraPhotoRecyclerView
            org.telegram.ui.Components.ChatAttachAlertPhotoLayout$PhotoAttachAdapter r4 = new org.telegram.ui.Components.ChatAttachAlertPhotoLayout$PhotoAttachAdapter
            r4.<init>(r8, r3)
            r7.cameraAttachAdapter = r4
            r1.setAdapter(r4)
            org.telegram.ui.Components.ChatAttachAlertPhotoLayout$PhotoAttachAdapter r1 = r7.cameraAttachAdapter
            r1.createCache()
            org.telegram.ui.Components.RecyclerListView r1 = r7.cameraPhotoRecyclerView
            r1.setClipToPadding(r3)
            org.telegram.ui.Components.RecyclerListView r1 = r7.cameraPhotoRecyclerView
            r4 = 1090519040(0x41000000, float:8.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r1.setPadding(r5, r3, r4, r3)
            org.telegram.ui.Components.RecyclerListView r1 = r7.cameraPhotoRecyclerView
            r4 = 0
            r1.setItemAnimator(r4)
            org.telegram.ui.Components.RecyclerListView r1 = r7.cameraPhotoRecyclerView
            r1.setLayoutAnimation(r4)
            org.telegram.ui.Components.RecyclerListView r1 = r7.cameraPhotoRecyclerView
            r4 = 2
            r1.setOverScrollMode(r4)
            org.telegram.ui.Components.RecyclerListView r1 = r7.cameraPhotoRecyclerView
            r1.setVisibility(r2)
            org.telegram.ui.Components.RecyclerListView r1 = r7.cameraPhotoRecyclerView
            r1.setAlpha(r12)
            org.telegram.ui.Components.RecyclerListView r1 = r7.cameraPhotoRecyclerView
            r2 = 1117782016(0x42a00000, float:80.0)
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r15, r2)
            r6.addView(r1, r2)
            org.telegram.ui.Components.ChatAttachAlertPhotoLayout$14 r1 = new org.telegram.ui.Components.ChatAttachAlertPhotoLayout$14
            r1.<init>(r8, r3, r3)
            r7.cameraPhotoLayoutManager = r1
            org.telegram.ui.Components.RecyclerListView r2 = r7.cameraPhotoRecyclerView
            r2.setLayoutManager(r1)
            org.telegram.ui.Components.RecyclerListView r1 = r7.cameraPhotoRecyclerView
            org.telegram.ui.Components.ChatAttachAlertPhotoLayout$$ExternalSyntheticLambda5 r2 = org.telegram.ui.Components.ChatAttachAlertPhotoLayout$$ExternalSyntheticLambda5.INSTANCE
            r1.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r2)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlertPhotoLayout.<init>(org.telegram.ui.Components.ChatAttachAlert, android.content.Context, boolean, org.telegram.ui.ActionBar.Theme$ResourcesProvider):void");
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-ChatAttachAlertPhotoLayout  reason: not valid java name */
    public /* synthetic */ void m2172x62CLASSNAME(View view) {
        this.dropDownContainer.toggleSubMenu();
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-ChatAttachAlertPhotoLayout  reason: not valid java name */
    public /* synthetic */ void m2173x8c1b5d8a(Theme.ResourcesProvider resourcesProvider, View view, int position) {
        int type;
        ChatActivity chatActivity;
        if (this.mediaEnabled && this.parentAlert.baseFragment != null && this.parentAlert.baseFragment.getParentActivity() != null) {
            if (Build.VERSION.SDK_INT >= 23) {
                if (this.adapter.needCamera && this.selectedAlbumEntry == this.galleryAlbumEntry && position == 0 && this.noCameraPermissions) {
                    try {
                        this.parentAlert.baseFragment.getParentActivity().requestPermissions(new String[]{"android.permission.CAMERA"}, 18);
                        return;
                    } catch (Exception e) {
                        return;
                    }
                } else if (this.noGalleryPermissions) {
                    try {
                        this.parentAlert.baseFragment.getParentActivity().requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 4);
                        return;
                    } catch (Exception e2) {
                        return;
                    }
                }
            }
            if (position != 0 || this.selectedAlbumEntry != this.galleryAlbumEntry) {
                if (this.selectedAlbumEntry == this.galleryAlbumEntry) {
                    position--;
                }
                ArrayList<Object> arrayList = getAllPhotosArray();
                if (position >= 0 && position < arrayList.size()) {
                    PhotoViewer.getInstance().setParentActivity(this.parentAlert.baseFragment.getParentActivity(), resourcesProvider);
                    PhotoViewer.getInstance().setParentAlert(this.parentAlert);
                    PhotoViewer.getInstance().setMaxSelectedPhotos(this.parentAlert.maxSelectedPhotos, this.parentAlert.allowOrder);
                    if (this.parentAlert.avatarPicker != 0) {
                        chatActivity = null;
                        type = 1;
                    } else if (this.parentAlert.baseFragment instanceof ChatActivity) {
                        chatActivity = (ChatActivity) this.parentAlert.baseFragment;
                        type = 0;
                    } else {
                        chatActivity = null;
                        type = 4;
                    }
                    if (!this.parentAlert.delegate.needEnterComment()) {
                        AndroidUtilities.hideKeyboard(this.parentAlert.baseFragment.getFragmentView().findFocus());
                        AndroidUtilities.hideKeyboard(this.parentAlert.getContainer().findFocus());
                    }
                    ((MediaController.PhotoEntry) arrayList.get(position)).caption = this.parentAlert.getCommentTextView().getText();
                    PhotoViewer.getInstance().openPhotoForSelect(arrayList, position, type, false, this.photoViewerProvider, chatActivity);
                    PhotoViewer.getInstance().setCaption(this.parentAlert.getCommentTextView().getText());
                }
            } else if (SharedConfig.inappCamera) {
                openCamera(true);
            } else if (this.parentAlert.delegate != null) {
                this.parentAlert.delegate.didPressedButton(0, false, true, 0, false);
            }
        }
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-ChatAttachAlertPhotoLayout  reason: not valid java name */
    public /* synthetic */ boolean m2174xb56fb2cb(View view, int position) {
        if (position == 0 && this.selectedAlbumEntry == this.galleryAlbumEntry) {
            if (this.parentAlert.delegate != null) {
                this.parentAlert.delegate.didPressedButton(0, false, true, 0, false);
            }
            return true;
        } else if (!(view instanceof PhotoAttachPhotoCell)) {
            return false;
        } else {
            RecyclerViewItemRangeSelector recyclerViewItemRangeSelector = this.itemRangeSelector;
            boolean z = !((PhotoAttachPhotoCell) view).isChecked();
            this.shouldSelect = z;
            recyclerViewItemRangeSelector.setIsActive(view, true, position, z);
            return false;
        }
    }

    /* renamed from: lambda$new$3$org-telegram-ui-Components-ChatAttachAlertPhotoLayout  reason: not valid java name */
    public /* synthetic */ void m2175xdeCLASSNAMEc(View v) {
        if (this.cameraView != null) {
            openPhotoViewer((MediaController.PhotoEntry) null, false, false);
            CameraController.getInstance().stopPreview(this.cameraView.getCameraSession());
        }
    }

    /* renamed from: lambda$new$4$org-telegram-ui-Components-ChatAttachAlertPhotoLayout  reason: not valid java name */
    public /* synthetic */ void m2176x8185d4d(float zoom) {
        CameraView cameraView2 = this.cameraView;
        if (cameraView2 != null) {
            this.cameraZoom = zoom;
            cameraView2.setZoom(zoom);
        }
        showZoomControls(true, true);
    }

    /* renamed from: lambda$new$5$org-telegram-ui-Components-ChatAttachAlertPhotoLayout  reason: not valid java name */
    public /* synthetic */ void m2177x316cb28e(View v) {
        CameraView cameraView2;
        if (!this.takingPhoto && (cameraView2 = this.cameraView) != null && cameraView2.isInitied()) {
            this.canSaveCameraPreview = false;
            this.cameraView.switchCamera();
            this.cameraView.startSwitchingAnimation();
            ObjectAnimator animator = ObjectAnimator.ofFloat(this.switchCameraButton, View.SCALE_X, new float[]{0.0f}).setDuration(100);
            animator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    ChatAttachAlertPhotoLayout.this.switchCameraButton.setImageResource((ChatAttachAlertPhotoLayout.this.cameraView == null || !ChatAttachAlertPhotoLayout.this.cameraView.isFrontface()) ? NUM : NUM);
                    ObjectAnimator.ofFloat(ChatAttachAlertPhotoLayout.this.switchCameraButton, View.SCALE_X, new float[]{1.0f}).setDuration(100).start();
                }
            });
            animator.start();
        }
    }

    /* renamed from: lambda$new$6$org-telegram-ui-Components-ChatAttachAlertPhotoLayout  reason: not valid java name */
    public /* synthetic */ void m2178x5aCLASSNAMEcf(final View currentImage) {
        CameraView cameraView2;
        if (!this.flashAnimationInProgress && (cameraView2 = this.cameraView) != null && cameraView2.isInitied() && this.cameraOpened) {
            String current = this.cameraView.getCameraSession().getCurrentFlashMode();
            String next = this.cameraView.getCameraSession().getNextFlashMode();
            if (!current.equals(next)) {
                this.cameraView.getCameraSession().setCurrentFlashMode(next);
                this.flashAnimationInProgress = true;
                ImageView[] imageViewArr = this.flashModeButton;
                final ImageView nextImage = imageViewArr[0] == currentImage ? imageViewArr[1] : imageViewArr[0];
                nextImage.setVisibility(0);
                setCameraFlashModeIcon(nextImage, next);
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(currentImage, View.TRANSLATION_Y, new float[]{0.0f, (float) AndroidUtilities.dp(48.0f)}), ObjectAnimator.ofFloat(nextImage, View.TRANSLATION_Y, new float[]{(float) (-AndroidUtilities.dp(48.0f)), 0.0f}), ObjectAnimator.ofFloat(currentImage, View.ALPHA, new float[]{1.0f, 0.0f}), ObjectAnimator.ofFloat(nextImage, View.ALPHA, new float[]{0.0f, 1.0f})});
                animatorSet.setDuration(220);
                animatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        boolean unused = ChatAttachAlertPhotoLayout.this.flashAnimationInProgress = false;
                        currentImage.setVisibility(4);
                        nextImage.sendAccessibilityEvent(8);
                    }
                });
                animatorSet.start();
            }
        }
    }

    static /* synthetic */ void lambda$new$7(View view, int position) {
        if (view instanceof PhotoAttachPhotoCell) {
            ((PhotoAttachPhotoCell) view).callDelegate();
        }
    }

    /* access modifiers changed from: private */
    public int addToSelectedPhotos(MediaController.PhotoEntry object, int index) {
        Object key = Integer.valueOf(object.imageId);
        if (selectedPhotos.containsKey(key)) {
            selectedPhotos.remove(key);
            int position = selectedPhotosOrder.indexOf(key);
            if (position >= 0) {
                selectedPhotosOrder.remove(position);
            }
            updatePhotosCounter(false);
            updateCheckedPhotoIndices();
            if (index >= 0) {
                object.reset();
                this.photoViewerProvider.updatePhotoAtIndex(index);
            }
            return position;
        }
        selectedPhotos.put(key, object);
        selectedPhotosOrder.add(key);
        updatePhotosCounter(true);
        return -1;
    }

    private void clearSelectedPhotos() {
        if (!selectedPhotos.isEmpty()) {
            for (Map.Entry<Object, Object> entry : selectedPhotos.entrySet()) {
                ((MediaController.PhotoEntry) entry.getValue()).reset();
            }
            selectedPhotos.clear();
            selectedPhotosOrder.clear();
        }
        if (!cameraPhotos.isEmpty()) {
            int size = cameraPhotos.size();
            for (int a = 0; a < size; a++) {
                MediaController.PhotoEntry photoEntry = (MediaController.PhotoEntry) cameraPhotos.get(a);
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
        this.adapter.notifyDataSetChanged();
        this.cameraAttachAdapter.notifyDataSetChanged();
    }

    private void updateAlbumsDropDown() {
        ArrayList<MediaController.AlbumEntry> albums;
        this.dropDownContainer.removeAllSubItems();
        if (this.mediaEnabled) {
            if ((this.parentAlert.baseFragment instanceof ChatActivity) || this.parentAlert.avatarPicker == 2) {
                albums = MediaController.allMediaAlbums;
            } else {
                albums = MediaController.allPhotoAlbums;
            }
            ArrayList<MediaController.AlbumEntry> arrayList = new ArrayList<>(albums);
            this.dropDownAlbums = arrayList;
            Collections.sort(arrayList, new ChatAttachAlertPhotoLayout$$ExternalSyntheticLambda1(albums));
        } else {
            this.dropDownAlbums = new ArrayList<>();
        }
        if (this.dropDownAlbums.isEmpty()) {
            this.dropDown.setCompoundDrawablesWithIntrinsicBounds((Drawable) null, (Drawable) null, (Drawable) null, (Drawable) null);
            return;
        }
        this.dropDown.setCompoundDrawablesWithIntrinsicBounds((Drawable) null, (Drawable) null, this.dropDownDrawable, (Drawable) null);
        int N = this.dropDownAlbums.size();
        for (int a = 0; a < N; a++) {
            this.dropDownContainer.addSubItem(a + 10, this.dropDownAlbums.get(a).bucketName);
        }
    }

    static /* synthetic */ int lambda$updateAlbumsDropDown$8(ArrayList albums, MediaController.AlbumEntry o1, MediaController.AlbumEntry o2) {
        int index1;
        int index2;
        if (o1.bucketId == 0 && o2.bucketId != 0) {
            return -1;
        }
        if ((o1.bucketId != 0 && o2.bucketId == 0) || (index1 = albums.indexOf(o1)) > (index2 = albums.indexOf(o2))) {
            return 1;
        }
        if (index1 < index2) {
            return -1;
        }
        return 0;
    }

    private boolean processTouchEvent(MotionEvent event) {
        CameraView cameraView2;
        if (event == null) {
            return false;
        }
        if ((!this.pressed && event.getActionMasked() == 0) || event.getActionMasked() == 5) {
            this.zoomControlView.getHitRect(this.hitRect);
            if (this.zoomControlView.getTag() != null && this.hitRect.contains((int) event.getX(), (int) event.getY())) {
                return false;
            }
            if (!this.takingPhoto && !this.dragging) {
                if (event.getPointerCount() == 2) {
                    this.pinchStartDistance = (float) Math.hypot((double) (event.getX(1) - event.getX(0)), (double) (event.getY(1) - event.getY(0)));
                    this.zooming = true;
                } else {
                    this.maybeStartDraging = true;
                    this.lastY = event.getY();
                    this.zooming = false;
                }
                this.zoomWas = false;
                this.pressed = true;
            }
        } else if (this.pressed) {
            if (event.getActionMasked() == 2) {
                if (!this.zooming || event.getPointerCount() != 2 || this.dragging) {
                    float newY = event.getY();
                    float dy = newY - this.lastY;
                    if (this.maybeStartDraging) {
                        if (Math.abs(dy) > AndroidUtilities.getPixelsInCM(0.4f, false)) {
                            this.maybeStartDraging = false;
                            this.dragging = true;
                        }
                    } else if (this.dragging && (cameraView2 = this.cameraView) != null) {
                        cameraView2.setTranslationY(cameraView2.getTranslationY() + dy);
                        this.lastY = newY;
                        this.zoomControlView.setTag((Object) null);
                        Runnable runnable = this.zoomControlHideRunnable;
                        if (runnable != null) {
                            AndroidUtilities.cancelRunOnUIThread(runnable);
                            this.zoomControlHideRunnable = null;
                        }
                        if (this.cameraPanel.getTag() == null) {
                            this.cameraPanel.setTag(1);
                            AnimatorSet animatorSet = new AnimatorSet();
                            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.cameraPanel, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.zoomControlView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.counterTextView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.flashModeButton[0], View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.flashModeButton[1], View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.cameraPhotoRecyclerView, View.ALPHA, new float[]{0.0f})});
                            animatorSet.setDuration(220);
                            animatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
                            animatorSet.start();
                        }
                    }
                } else {
                    float newDistance = (float) Math.hypot((double) (event.getX(1) - event.getX(0)), (double) (event.getY(1) - event.getY(0)));
                    if (!this.zoomWas) {
                        if (Math.abs(newDistance - this.pinchStartDistance) >= AndroidUtilities.getPixelsInCM(0.4f, false)) {
                            this.pinchStartDistance = newDistance;
                            this.zoomWas = true;
                        }
                    } else if (this.cameraView != null) {
                        float diff = (newDistance - this.pinchStartDistance) / ((float) AndroidUtilities.dp(100.0f));
                        this.pinchStartDistance = newDistance;
                        float f = this.cameraZoom + diff;
                        this.cameraZoom = f;
                        if (f < 0.0f) {
                            this.cameraZoom = 0.0f;
                        } else if (f > 1.0f) {
                            this.cameraZoom = 1.0f;
                        }
                        this.zoomControlView.setZoom(this.cameraZoom, false);
                        this.parentAlert.getSheetContainer().invalidate();
                        this.cameraView.setZoom(this.cameraZoom);
                        showZoomControls(true, true);
                    }
                }
            } else if (event.getActionMasked() == 3 || event.getActionMasked() == 1 || event.getActionMasked() == 6) {
                this.pressed = false;
                this.zooming = false;
                if (this.dragging) {
                    this.dragging = false;
                    CameraView cameraView3 = this.cameraView;
                    if (cameraView3 != null) {
                        if (Math.abs(cameraView3.getTranslationY()) > ((float) this.cameraView.getMeasuredHeight()) / 6.0f) {
                            closeCamera(true);
                        } else {
                            AnimatorSet animatorSet2 = new AnimatorSet();
                            animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.cameraView, View.TRANSLATION_Y, new float[]{0.0f}), ObjectAnimator.ofFloat(this.cameraPanel, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.counterTextView, View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.flashModeButton[0], View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.flashModeButton[1], View.ALPHA, new float[]{1.0f}), ObjectAnimator.ofFloat(this.cameraPhotoRecyclerView, View.ALPHA, new float[]{1.0f})});
                            animatorSet2.setDuration(250);
                            animatorSet2.setInterpolator(this.interpolator);
                            animatorSet2.start();
                            this.cameraPanel.setTag((Object) null);
                        }
                    }
                } else {
                    CameraView cameraView4 = this.cameraView;
                    if (cameraView4 != null && !this.zoomWas) {
                        cameraView4.getLocationOnScreen(this.viewPosition);
                        this.cameraView.focusToPoint((int) (event.getRawX() - ((float) this.viewPosition[0])), (int) (event.getRawY() - ((float) this.viewPosition[1])));
                    }
                }
            }
        }
        return true;
    }

    /* access modifiers changed from: private */
    public void resetRecordState() {
        if (this.parentAlert.baseFragment != null) {
            for (int a = 0; a < 2; a++) {
                this.flashModeButton[a].animate().alpha(1.0f).translationX(0.0f).setDuration(150).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
            }
            this.switchCameraButton.animate().alpha(1.0f).translationX(0.0f).setDuration(150).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
            this.tooltipTextView.animate().alpha(1.0f).setDuration(150).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
            AndroidUtilities.updateViewVisibilityAnimated(this.recordTime, false);
            AndroidUtilities.cancelRunOnUIThread(this.videoRecordRunnable);
            this.videoRecordRunnable = null;
            AndroidUtilities.unlockOrientation(this.parentAlert.baseFragment.getParentActivity());
        }
    }

    /* JADX WARNING: type inference failed for: r0v14, types: [org.telegram.ui.ActionBar.BaseFragment] */
    /* access modifiers changed from: private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void openPhotoViewer(org.telegram.messenger.MediaController.PhotoEntry r12, final boolean r13, boolean r14) {
        /*
            r11 = this;
            r0 = 0
            if (r12 == 0) goto L_0x002d
            java.util.ArrayList<java.lang.Object> r1 = cameraPhotos
            r1.add(r12)
            java.util.HashMap<java.lang.Object, java.lang.Object> r1 = selectedPhotos
            int r2 = r12.imageId
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            r1.put(r2, r12)
            java.util.ArrayList<java.lang.Object> r1 = selectedPhotosOrder
            int r2 = r12.imageId
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            r1.add(r2)
            org.telegram.ui.Components.ChatAttachAlert r1 = r11.parentAlert
            r1.updateCountButton(r0)
            org.telegram.ui.Components.ChatAttachAlertPhotoLayout$PhotoAttachAdapter r1 = r11.adapter
            r1.notifyDataSetChanged()
            org.telegram.ui.Components.ChatAttachAlertPhotoLayout$PhotoAttachAdapter r1 = r11.cameraAttachAdapter
            r1.notifyDataSetChanged()
        L_0x002d:
            r1 = 1
            if (r12 == 0) goto L_0x005c
            if (r14 != 0) goto L_0x005c
            java.util.ArrayList<java.lang.Object> r2 = cameraPhotos
            int r2 = r2.size()
            if (r2 <= r1) goto L_0x005c
            r11.updatePhotosCounter(r0)
            org.telegram.messenger.camera.CameraView r1 = r11.cameraView
            if (r1 == 0) goto L_0x005b
            org.telegram.ui.Components.ZoomControlView r1 = r11.zoomControlView
            r2 = 0
            r1.setZoom(r2, r0)
            r11.cameraZoom = r2
            org.telegram.messenger.camera.CameraView r0 = r11.cameraView
            r0.setZoom(r2)
            org.telegram.messenger.camera.CameraController r0 = org.telegram.messenger.camera.CameraController.getInstance()
            org.telegram.messenger.camera.CameraView r1 = r11.cameraView
            org.telegram.messenger.camera.CameraSession r1 = r1.getCameraSession()
            r0.startPreview(r1)
        L_0x005b:
            return
        L_0x005c:
            java.util.ArrayList<java.lang.Object> r0 = cameraPhotos
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x0065
            return
        L_0x0065:
            r11.cancelTakingPhotos = r1
            org.telegram.ui.PhotoViewer r0 = org.telegram.ui.PhotoViewer.getInstance()
            org.telegram.ui.Components.ChatAttachAlert r2 = r11.parentAlert
            org.telegram.ui.ActionBar.BaseFragment r2 = r2.baseFragment
            android.app.Activity r2 = r2.getParentActivity()
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r3 = r11.resourcesProvider
            r0.setParentActivity(r2, r3)
            org.telegram.ui.PhotoViewer r0 = org.telegram.ui.PhotoViewer.getInstance()
            org.telegram.ui.Components.ChatAttachAlert r2 = r11.parentAlert
            r0.setParentAlert(r2)
            org.telegram.ui.PhotoViewer r0 = org.telegram.ui.PhotoViewer.getInstance()
            org.telegram.ui.Components.ChatAttachAlert r2 = r11.parentAlert
            int r2 = r2.maxSelectedPhotos
            org.telegram.ui.Components.ChatAttachAlert r3 = r11.parentAlert
            boolean r3 = r3.allowOrder
            r0.setMaxSelectedPhotos(r2, r3)
            org.telegram.ui.Components.ChatAttachAlert r0 = r11.parentAlert
            int r0 = r0.avatarPicker
            if (r0 == 0) goto L_0x0099
            r0 = 1
            r2 = 0
            goto L_0x00ac
        L_0x0099:
            org.telegram.ui.Components.ChatAttachAlert r0 = r11.parentAlert
            org.telegram.ui.ActionBar.BaseFragment r0 = r0.baseFragment
            boolean r0 = r0 instanceof org.telegram.ui.ChatActivity
            if (r0 == 0) goto L_0x00aa
            org.telegram.ui.Components.ChatAttachAlert r0 = r11.parentAlert
            org.telegram.ui.ActionBar.BaseFragment r0 = r0.baseFragment
            r2 = r0
            org.telegram.ui.ChatActivity r2 = (org.telegram.ui.ChatActivity) r2
            r0 = 2
            goto L_0x00ac
        L_0x00aa:
            r2 = 0
            r0 = 5
        L_0x00ac:
            org.telegram.ui.Components.ChatAttachAlert r3 = r11.parentAlert
            int r3 = r3.avatarPicker
            if (r3 == 0) goto L_0x00bd
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            r1.add(r12)
            r3 = 0
            r10 = r3
            goto L_0x00cb
        L_0x00bd:
            java.util.ArrayList r3 = r11.getAllPhotosArray()
            java.util.ArrayList<java.lang.Object> r4 = cameraPhotos
            int r4 = r4.size()
            int r1 = r4 + -1
            r10 = r1
            r1 = r3
        L_0x00cb:
            org.telegram.ui.PhotoViewer r3 = org.telegram.ui.PhotoViewer.getInstance()
            r7 = 0
            org.telegram.ui.Components.ChatAttachAlertPhotoLayout$15 r8 = new org.telegram.ui.Components.ChatAttachAlertPhotoLayout$15
            r8.<init>(r13)
            r4 = r1
            r5 = r10
            r6 = r0
            r9 = r2
            r3.openPhotoForSelect(r4, r5, r6, r7, r8, r9)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlertPhotoLayout.openPhotoViewer(org.telegram.messenger.MediaController$PhotoEntry, boolean, boolean):void");
    }

    /* access modifiers changed from: private */
    public void showZoomControls(boolean show, boolean animated) {
        if ((this.zoomControlView.getTag() == null || !show) && (this.zoomControlView.getTag() != null || show)) {
            AnimatorSet animatorSet = this.zoomControlAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            this.zoomControlView.setTag(show ? 1 : null);
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.zoomControlAnimation = animatorSet2;
            animatorSet2.setDuration(180);
            AnimatorSet animatorSet3 = this.zoomControlAnimation;
            Animator[] animatorArr = new Animator[1];
            ZoomControlView zoomControlView2 = this.zoomControlView;
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            fArr[0] = show ? 1.0f : 0.0f;
            animatorArr[0] = ObjectAnimator.ofFloat(zoomControlView2, property, fArr);
            animatorSet3.playTogether(animatorArr);
            this.zoomControlAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    AnimatorSet unused = ChatAttachAlertPhotoLayout.this.zoomControlAnimation = null;
                }
            });
            this.zoomControlAnimation.start();
            if (show) {
                ChatAttachAlertPhotoLayout$$ExternalSyntheticLambda13 chatAttachAlertPhotoLayout$$ExternalSyntheticLambda13 = new ChatAttachAlertPhotoLayout$$ExternalSyntheticLambda13(this);
                this.zoomControlHideRunnable = chatAttachAlertPhotoLayout$$ExternalSyntheticLambda13;
                AndroidUtilities.runOnUIThread(chatAttachAlertPhotoLayout$$ExternalSyntheticLambda13, 2000);
            }
        } else if (show) {
            Runnable runnable = this.zoomControlHideRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
            }
            ChatAttachAlertPhotoLayout$$ExternalSyntheticLambda14 chatAttachAlertPhotoLayout$$ExternalSyntheticLambda14 = new ChatAttachAlertPhotoLayout$$ExternalSyntheticLambda14(this);
            this.zoomControlHideRunnable = chatAttachAlertPhotoLayout$$ExternalSyntheticLambda14;
            AndroidUtilities.runOnUIThread(chatAttachAlertPhotoLayout$$ExternalSyntheticLambda14, 2000);
        }
    }

    /* renamed from: lambda$showZoomControls$9$org-telegram-ui-Components-ChatAttachAlertPhotoLayout  reason: not valid java name */
    public /* synthetic */ void m2183x6cfb1ad2() {
        showZoomControls(false, true);
        this.zoomControlHideRunnable = null;
    }

    /* renamed from: lambda$showZoomControls$10$org-telegram-ui-Components-ChatAttachAlertPhotoLayout  reason: not valid java name */
    public /* synthetic */ void m2182x9d102a5a() {
        showZoomControls(false, true);
        this.zoomControlHideRunnable = null;
    }

    /* access modifiers changed from: private */
    public void updatePhotosCounter(boolean added) {
        if (this.counterTextView != null && this.parentAlert.avatarPicker == 0) {
            boolean hasVideo = false;
            boolean hasPhotos = false;
            for (Map.Entry<Object, Object> entry : selectedPhotos.entrySet()) {
                if (((MediaController.PhotoEntry) entry.getValue()).isVideo) {
                    hasVideo = true;
                } else {
                    hasPhotos = true;
                }
                if (hasVideo && hasPhotos) {
                    break;
                }
            }
            int newSelectedCount = Math.max(1, selectedPhotos.size());
            if (hasVideo && hasPhotos) {
                this.counterTextView.setText(LocaleController.formatPluralString("Media", selectedPhotos.size()).toUpperCase());
                if (newSelectedCount != this.currentSelectedCount || added) {
                    this.parentAlert.selectedTextView.setText(LocaleController.formatPluralString("MediaSelected", newSelectedCount));
                }
            } else if (hasVideo) {
                this.counterTextView.setText(LocaleController.formatPluralString("Videos", selectedPhotos.size()).toUpperCase());
                if (newSelectedCount != this.currentSelectedCount || added) {
                    this.parentAlert.selectedTextView.setText(LocaleController.formatPluralString("VideosSelected", newSelectedCount));
                }
            } else {
                this.counterTextView.setText(LocaleController.formatPluralString("Photos", selectedPhotos.size()).toUpperCase());
                if (newSelectedCount != this.currentSelectedCount || added) {
                    this.parentAlert.selectedTextView.setText(LocaleController.formatPluralString("PhotosSelected", newSelectedCount));
                }
            }
            this.currentSelectedCount = newSelectedCount;
        }
    }

    /* access modifiers changed from: private */
    public PhotoAttachPhotoCell getCellForIndex(int index) {
        int count = this.gridView.getChildCount();
        for (int a = 0; a < count; a++) {
            View view = this.gridView.getChildAt(a);
            if (((float) view.getTop()) < ((float) this.gridView.getMeasuredHeight()) - this.parentAlert.getClipLayoutBottom() && (view instanceof PhotoAttachPhotoCell)) {
                PhotoAttachPhotoCell cell = (PhotoAttachPhotoCell) view;
                if (((Integer) cell.getImageView().getTag()).intValue() == index) {
                    return cell;
                }
            }
        }
        return null;
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setCameraFlashModeIcon(android.widget.ImageView r3, java.lang.String r4) {
        /*
            r2 = this;
            int r0 = r4.hashCode()
            switch(r0) {
                case 3551: goto L_0x001c;
                case 109935: goto L_0x0012;
                case 3005871: goto L_0x0008;
                default: goto L_0x0007;
            }
        L_0x0007:
            goto L_0x0026
        L_0x0008:
            java.lang.String r0 = "auto"
            boolean r0 = r4.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 2
            goto L_0x0027
        L_0x0012:
            java.lang.String r0 = "off"
            boolean r0 = r4.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 0
            goto L_0x0027
        L_0x001c:
            java.lang.String r0 = "on"
            boolean r0 = r4.equals(r0)
            if (r0 == 0) goto L_0x0007
            r0 = 1
            goto L_0x0027
        L_0x0026:
            r0 = -1
        L_0x0027:
            switch(r0) {
                case 0: goto L_0x0051;
                case 1: goto L_0x003e;
                case 2: goto L_0x002b;
                default: goto L_0x002a;
            }
        L_0x002a:
            goto L_0x0064
        L_0x002b:
            r0 = 2131165428(0x7var_f4, float:1.7945073E38)
            r3.setImageResource(r0)
            r0 = 2131623956(0x7f0e0014, float:1.8875078E38)
            java.lang.String r1 = "AccDescrCameraFlashAuto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r3.setContentDescription(r0)
            goto L_0x0064
        L_0x003e:
            r0 = 2131165430(0x7var_f6, float:1.7945077E38)
            r3.setImageResource(r0)
            r0 = 2131623958(0x7f0e0016, float:1.8875082E38)
            java.lang.String r1 = "AccDescrCameraFlashOn"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r3.setContentDescription(r0)
            goto L_0x0064
        L_0x0051:
            r0 = 2131165429(0x7var_f5, float:1.7945075E38)
            r3.setImageResource(r0)
            r0 = 2131623957(0x7f0e0015, float:1.887508E38)
            java.lang.String r1 = "AccDescrCameraFlashOff"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r3.setContentDescription(r0)
        L_0x0064:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlertPhotoLayout.setCameraFlashModeIcon(android.widget.ImageView, java.lang.String):void");
    }

    public void checkCamera(boolean request) {
        PhotoAttachAdapter photoAttachAdapter;
        if (this.parentAlert.baseFragment != null && this.parentAlert.baseFragment.getParentActivity() != null) {
            boolean old = this.deviceHasGoodCamera;
            boolean old2 = this.noCameraPermissions;
            if (!SharedConfig.inappCamera) {
                this.deviceHasGoodCamera = false;
            } else if (Build.VERSION.SDK_INT >= 23) {
                boolean z = this.parentAlert.baseFragment.getParentActivity().checkSelfPermission("android.permission.CAMERA") != 0;
                this.noCameraPermissions = z;
                if (z) {
                    if (request) {
                        try {
                            this.parentAlert.baseFragment.getParentActivity().requestPermissions(new String[]{"android.permission.CAMERA"}, 17);
                        } catch (Exception e) {
                        }
                    }
                    this.deviceHasGoodCamera = false;
                } else {
                    if (request || SharedConfig.hasCameraCache) {
                        CameraController.getInstance().initCamera((Runnable) null);
                    }
                    this.deviceHasGoodCamera = CameraController.getInstance().isCameraInitied();
                }
            } else {
                if (request || SharedConfig.hasCameraCache) {
                    CameraController.getInstance().initCamera((Runnable) null);
                }
                this.deviceHasGoodCamera = CameraController.getInstance().isCameraInitied();
            }
            if (!((old == this.deviceHasGoodCamera && old2 == this.noCameraPermissions) || (photoAttachAdapter = this.adapter) == null)) {
                photoAttachAdapter.notifyDataSetChanged();
            }
            if (this.parentAlert.isShowing() && this.deviceHasGoodCamera && this.parentAlert.baseFragment != null && this.parentAlert.getBackDrawable().getAlpha() != 0 && !this.cameraOpened) {
                showCamera();
            }
        }
    }

    /* access modifiers changed from: private */
    public void openCamera(boolean animated) {
        CameraView cameraView2 = this.cameraView;
        if (cameraView2 != null && this.cameraInitAnimation == null && cameraView2.isInitied() && !this.parentAlert.isDismissed()) {
            if (this.parentAlert.avatarPicker == 2 || (this.parentAlert.baseFragment instanceof ChatActivity)) {
                this.tooltipTextView.setVisibility(0);
            } else {
                this.tooltipTextView.setVisibility(8);
            }
            if (cameraPhotos.isEmpty()) {
                this.counterTextView.setVisibility(4);
                this.cameraPhotoRecyclerView.setVisibility(8);
            } else {
                this.counterTextView.setVisibility(0);
                this.cameraPhotoRecyclerView.setVisibility(0);
            }
            if (this.parentAlert.commentTextView.isKeyboardVisible() && isFocusable()) {
                this.parentAlert.commentTextView.closeKeyboard();
            }
            this.zoomControlView.setVisibility(0);
            this.zoomControlView.setAlpha(0.0f);
            this.cameraPanel.setVisibility(0);
            this.cameraPanel.setTag((Object) null);
            int[] iArr = this.animateCameraValues;
            iArr[0] = 0;
            int i = this.itemSize;
            iArr[1] = i;
            iArr[2] = i;
            this.additionCloseCameraY = 0.0f;
            this.cameraExpanded = true;
            this.cameraView.setFpsLimit(-1);
            if (animated) {
                setCameraOpenProgress(0.0f);
                this.cameraAnimationInProgress = true;
                this.animationIndex = NotificationCenter.getInstance(this.parentAlert.currentAccount).setAnimationInProgress(this.animationIndex, (int[]) null);
                ArrayList<Animator> animators = new ArrayList<>();
                animators.add(ObjectAnimator.ofFloat(this, "cameraOpenProgress", new float[]{0.0f, 1.0f}));
                animators.add(ObjectAnimator.ofFloat(this.cameraPanel, View.ALPHA, new float[]{1.0f}));
                animators.add(ObjectAnimator.ofFloat(this.counterTextView, View.ALPHA, new float[]{1.0f}));
                animators.add(ObjectAnimator.ofFloat(this.cameraPhotoRecyclerView, View.ALPHA, new float[]{1.0f}));
                int a = 0;
                while (true) {
                    if (a >= 2) {
                        break;
                    } else if (this.flashModeButton[a].getVisibility() == 0) {
                        animators.add(ObjectAnimator.ofFloat(this.flashModeButton[a], View.ALPHA, new float[]{1.0f}));
                        break;
                    } else {
                        a++;
                    }
                }
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(animators);
                animatorSet.setDuration(350);
                animatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        NotificationCenter.getInstance(ChatAttachAlertPhotoLayout.this.parentAlert.currentAccount).onAnimationFinish(ChatAttachAlertPhotoLayout.this.animationIndex);
                        boolean unused = ChatAttachAlertPhotoLayout.this.cameraAnimationInProgress = false;
                        if (Build.VERSION.SDK_INT >= 21 && ChatAttachAlertPhotoLayout.this.cameraView != null) {
                            ChatAttachAlertPhotoLayout.this.cameraView.invalidateOutline();
                        } else if (ChatAttachAlertPhotoLayout.this.cameraView != null) {
                            ChatAttachAlertPhotoLayout.this.cameraView.invalidate();
                        }
                        if (ChatAttachAlertPhotoLayout.this.cameraOpened) {
                            ChatAttachAlertPhotoLayout.this.parentAlert.delegate.onCameraOpened();
                        }
                        if (Build.VERSION.SDK_INT >= 21 && ChatAttachAlertPhotoLayout.this.cameraView != null) {
                            ChatAttachAlertPhotoLayout.this.cameraView.setSystemUiVisibility(1028);
                        }
                    }
                });
                animatorSet.start();
            } else {
                setCameraOpenProgress(1.0f);
                this.cameraPanel.setAlpha(1.0f);
                this.counterTextView.setAlpha(1.0f);
                this.cameraPhotoRecyclerView.setAlpha(1.0f);
                int a2 = 0;
                while (true) {
                    if (a2 >= 2) {
                        break;
                    } else if (this.flashModeButton[a2].getVisibility() == 0) {
                        this.flashModeButton[a2].setAlpha(1.0f);
                        break;
                    } else {
                        a2++;
                    }
                }
                this.parentAlert.delegate.onCameraOpened();
                if (Build.VERSION.SDK_INT >= 21) {
                    this.cameraView.setSystemUiVisibility(1028);
                }
            }
            this.cameraOpened = true;
            this.cameraView.setImportantForAccessibility(2);
            if (Build.VERSION.SDK_INT >= 19) {
                this.gridView.setImportantForAccessibility(4);
            }
        }
    }

    public void loadGalleryPhotos() {
        MediaController.AlbumEntry albumEntry;
        if ((this.parentAlert.baseFragment instanceof ChatActivity) || this.parentAlert.avatarPicker == 2) {
            albumEntry = MediaController.allMediaAlbumEntry;
        } else {
            albumEntry = MediaController.allPhotosAlbumEntry;
        }
        if (albumEntry == null && Build.VERSION.SDK_INT >= 21) {
            MediaController.loadGalleryPhotosAlbums(0);
        }
    }

    public void showCamera() {
        if (!this.parentAlert.paused && this.mediaEnabled) {
            if (this.cameraView == null) {
                AnonymousClass18 r0 = new CameraView(this.parentAlert.baseFragment.getParentActivity(), this.parentAlert.openWithFrontFaceCamera) {
                    /* access modifiers changed from: protected */
                    public void dispatchDraw(Canvas canvas) {
                        if (Build.VERSION.SDK_INT >= 21) {
                            super.dispatchDraw(canvas);
                            return;
                        }
                        if (ChatAttachAlertPhotoLayout.this.cameraAnimationInProgress) {
                            AndroidUtilities.rectTmp.set(ChatAttachAlertPhotoLayout.this.animationClipLeft + (ChatAttachAlertPhotoLayout.this.cameraViewOffsetX * (1.0f - ChatAttachAlertPhotoLayout.this.cameraOpenProgress)), ChatAttachAlertPhotoLayout.this.animationClipTop + (ChatAttachAlertPhotoLayout.this.cameraViewOffsetY * (1.0f - ChatAttachAlertPhotoLayout.this.cameraOpenProgress)), ChatAttachAlertPhotoLayout.this.animationClipRight, ChatAttachAlertPhotoLayout.this.animationClipBottom);
                        } else if (ChatAttachAlertPhotoLayout.this.cameraAnimationInProgress || ChatAttachAlertPhotoLayout.this.cameraOpened) {
                            AndroidUtilities.rectTmp.set(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight());
                        } else {
                            AndroidUtilities.rectTmp.set(ChatAttachAlertPhotoLayout.this.cameraViewOffsetX, ChatAttachAlertPhotoLayout.this.cameraViewOffsetY, (float) getMeasuredWidth(), (float) getMeasuredHeight());
                        }
                        canvas.save();
                        canvas.clipRect(AndroidUtilities.rectTmp);
                        super.dispatchDraw(canvas);
                        canvas.restore();
                    }
                };
                this.cameraView = r0;
                r0.setRecordFile(AndroidUtilities.generateVideoPath((this.parentAlert.baseFragment instanceof ChatActivity) && ((ChatActivity) this.parentAlert.baseFragment).isSecretChat()));
                this.cameraView.setFocusable(true);
                this.cameraView.setFpsLimit(30);
                if (Build.VERSION.SDK_INT >= 21) {
                    new Path();
                    float[] fArr = new float[8];
                    this.cameraView.setOutlineProvider(new ViewOutlineProvider() {
                        public void getOutline(View view, Outline outline) {
                            if (ChatAttachAlertPhotoLayout.this.cameraAnimationInProgress) {
                                AndroidUtilities.rectTmp.set(ChatAttachAlertPhotoLayout.this.animationClipLeft + (ChatAttachAlertPhotoLayout.this.cameraViewOffsetX * (1.0f - ChatAttachAlertPhotoLayout.this.cameraOpenProgress)), ChatAttachAlertPhotoLayout.this.animationClipTop + (ChatAttachAlertPhotoLayout.this.cameraViewOffsetY * (1.0f - ChatAttachAlertPhotoLayout.this.cameraOpenProgress)), ChatAttachAlertPhotoLayout.this.animationClipRight, ChatAttachAlertPhotoLayout.this.animationClipBottom);
                                outline.setRect((int) AndroidUtilities.rectTmp.left, (int) AndroidUtilities.rectTmp.top, (int) AndroidUtilities.rectTmp.right, (int) AndroidUtilities.rectTmp.bottom);
                            } else if (ChatAttachAlertPhotoLayout.this.cameraAnimationInProgress || ChatAttachAlertPhotoLayout.this.cameraOpened) {
                                outline.setRect(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
                            } else {
                                int rad = AndroidUtilities.dp(ChatAttachAlertPhotoLayout.this.parentAlert.cornerRadius * 8.0f);
                                outline.setRoundRect((int) ChatAttachAlertPhotoLayout.this.cameraViewOffsetX, (int) ChatAttachAlertPhotoLayout.this.cameraViewOffsetY, view.getMeasuredWidth() + rad, view.getMeasuredHeight() + rad, (float) rad);
                            }
                        }
                    });
                    this.cameraView.setClipToOutline(true);
                }
                this.cameraView.setContentDescription(LocaleController.getString("AccDescrInstantCamera", NUM));
                FrameLayout container = this.parentAlert.getContainer();
                CameraView cameraView2 = this.cameraView;
                int i = this.itemSize;
                container.addView(cameraView2, 1, new FrameLayout.LayoutParams(i, i));
                this.cameraView.setDelegate(new CameraView.CameraViewDelegate() {
                    public void onCameraCreated(Camera camera) {
                    }

                    public void onCameraInit() {
                        int i = 4;
                        if (ChatAttachAlertPhotoLayout.this.cameraView.getCameraSession().getCurrentFlashMode().equals(ChatAttachAlertPhotoLayout.this.cameraView.getCameraSession().getNextFlashMode())) {
                            for (int a = 0; a < 2; a++) {
                                ChatAttachAlertPhotoLayout.this.flashModeButton[a].setVisibility(4);
                                ChatAttachAlertPhotoLayout.this.flashModeButton[a].setAlpha(0.0f);
                                ChatAttachAlertPhotoLayout.this.flashModeButton[a].setTranslationY(0.0f);
                            }
                        } else {
                            ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout = ChatAttachAlertPhotoLayout.this;
                            chatAttachAlertPhotoLayout.setCameraFlashModeIcon(chatAttachAlertPhotoLayout.flashModeButton[0], ChatAttachAlertPhotoLayout.this.cameraView.getCameraSession().getCurrentFlashMode());
                            int a2 = 0;
                            while (a2 < 2) {
                                ChatAttachAlertPhotoLayout.this.flashModeButton[a2].setVisibility(a2 == 0 ? 0 : 4);
                                ChatAttachAlertPhotoLayout.this.flashModeButton[a2].setAlpha((a2 != 0 || !ChatAttachAlertPhotoLayout.this.cameraOpened) ? 0.0f : 1.0f);
                                ChatAttachAlertPhotoLayout.this.flashModeButton[a2].setTranslationY(0.0f);
                                a2++;
                            }
                        }
                        ChatAttachAlertPhotoLayout.this.switchCameraButton.setImageResource(ChatAttachAlertPhotoLayout.this.cameraView.isFrontface() ? NUM : NUM);
                        ImageView access$2000 = ChatAttachAlertPhotoLayout.this.switchCameraButton;
                        if (ChatAttachAlertPhotoLayout.this.cameraView.hasFrontFaceCamera()) {
                            i = 0;
                        }
                        access$2000.setVisibility(i);
                        if (!ChatAttachAlertPhotoLayout.this.cameraOpened) {
                            AnimatorSet unused = ChatAttachAlertPhotoLayout.this.cameraInitAnimation = new AnimatorSet();
                            ChatAttachAlertPhotoLayout.this.cameraInitAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(ChatAttachAlertPhotoLayout.this.cameraView, View.ALPHA, new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(ChatAttachAlertPhotoLayout.this.cameraIcon, View.ALPHA, new float[]{0.0f, 1.0f})});
                            ChatAttachAlertPhotoLayout.this.cameraInitAnimation.setDuration(180);
                            ChatAttachAlertPhotoLayout.this.cameraInitAnimation.addListener(new AnimatorListenerAdapter() {
                                public void onAnimationEnd(Animator animation) {
                                    if (animation.equals(ChatAttachAlertPhotoLayout.this.cameraInitAnimation)) {
                                        boolean unused = ChatAttachAlertPhotoLayout.this.canSaveCameraPreview = true;
                                        AnimatorSet unused2 = ChatAttachAlertPhotoLayout.this.cameraInitAnimation = null;
                                        if (!ChatAttachAlertPhotoLayout.this.isHidden) {
                                            int count = ChatAttachAlertPhotoLayout.this.gridView.getChildCount();
                                            for (int a = 0; a < count; a++) {
                                                View child = ChatAttachAlertPhotoLayout.this.gridView.getChildAt(a);
                                                if (child instanceof PhotoAttachCameraCell) {
                                                    child.setVisibility(4);
                                                    return;
                                                }
                                            }
                                        }
                                    }
                                }

                                public void onAnimationCancel(Animator animation) {
                                    AnimatorSet unused = ChatAttachAlertPhotoLayout.this.cameraInitAnimation = null;
                                }
                            });
                            ChatAttachAlertPhotoLayout.this.cameraInitAnimation.start();
                        }
                    }
                });
                if (this.cameraIcon == null) {
                    AnonymousClass21 r02 = new FrameLayout(this.parentAlert.baseFragment.getParentActivity()) {
                        /* access modifiers changed from: protected */
                        public void onDraw(Canvas canvas) {
                            int w = ChatAttachAlertPhotoLayout.this.cameraDrawable.getIntrinsicWidth();
                            int h = ChatAttachAlertPhotoLayout.this.cameraDrawable.getIntrinsicHeight();
                            int x = (ChatAttachAlertPhotoLayout.this.itemSize - w) / 2;
                            int y = (ChatAttachAlertPhotoLayout.this.itemSize - h) / 2;
                            if (ChatAttachAlertPhotoLayout.this.cameraViewOffsetY != 0.0f) {
                                y = (int) (((float) y) - ChatAttachAlertPhotoLayout.this.cameraViewOffsetY);
                            }
                            ChatAttachAlertPhotoLayout.this.cameraDrawable.setBounds(x, y, x + w, y + h);
                            ChatAttachAlertPhotoLayout.this.cameraDrawable.draw(canvas);
                        }
                    };
                    this.cameraIcon = r02;
                    r02.setWillNotDraw(false);
                    this.cameraIcon.setClipChildren(true);
                }
                FrameLayout container2 = this.parentAlert.getContainer();
                FrameLayout frameLayout = this.cameraIcon;
                int i2 = this.itemSize;
                container2.addView(frameLayout, 2, new FrameLayout.LayoutParams(i2, i2));
                float f = 1.0f;
                this.cameraView.setAlpha(this.mediaEnabled ? 1.0f : 0.2f);
                this.cameraView.setEnabled(this.mediaEnabled);
                FrameLayout frameLayout2 = this.cameraIcon;
                if (!this.mediaEnabled) {
                    f = 0.2f;
                }
                frameLayout2.setAlpha(f);
                this.cameraIcon.setEnabled(this.mediaEnabled);
                if (this.isHidden) {
                    this.cameraView.setVisibility(8);
                    this.cameraIcon.setVisibility(8);
                }
                checkCameraViewPosition();
                invalidate();
            }
            ZoomControlView zoomControlView2 = this.zoomControlView;
            if (zoomControlView2 != null) {
                zoomControlView2.setZoom(0.0f, false);
                this.cameraZoom = 0.0f;
            }
            this.cameraView.setTranslationX(this.cameraViewLocation[0]);
            this.cameraView.setTranslationY(this.cameraViewLocation[1] + this.currentPanTranslationY);
            this.cameraIcon.setTranslationX(this.cameraViewLocation[0]);
            this.cameraIcon.setTranslationY(this.cameraViewLocation[1] + this.cameraViewOffsetY + this.currentPanTranslationY);
        }
    }

    public void hideCamera(boolean async) {
        if (this.deviceHasGoodCamera && this.cameraView != null) {
            saveLastCameraBitmap();
            int count = this.gridView.getChildCount();
            int a = 0;
            while (true) {
                if (a >= count) {
                    break;
                }
                View child = this.gridView.getChildAt(a);
                if (child instanceof PhotoAttachCameraCell) {
                    child.setVisibility(0);
                    ((PhotoAttachCameraCell) child).updateBitmap();
                    break;
                }
                a++;
            }
            this.cameraView.destroy(async, (Runnable) null);
            AnimatorSet animatorSet = this.cameraInitAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.cameraInitAnimation = null;
            }
            AndroidUtilities.runOnUIThread(new ChatAttachAlertPhotoLayout$$ExternalSyntheticLambda11(this), 300);
            this.canSaveCameraPreview = false;
        }
    }

    /* renamed from: lambda$hideCamera$11$org-telegram-ui-Components-ChatAttachAlertPhotoLayout  reason: not valid java name */
    public /* synthetic */ void m2171x2e5b293a() {
        this.parentAlert.getContainer().removeView(this.cameraView);
        this.parentAlert.getContainer().removeView(this.cameraIcon);
        this.cameraView = null;
        this.cameraIcon = null;
    }

    private void saveLastCameraBitmap() {
        if (this.canSaveCameraPreview) {
            try {
                Bitmap bitmap = this.cameraView.getTextureView().getBitmap();
                if (bitmap != null) {
                    Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), this.cameraView.getMatrix(), true);
                    bitmap.recycle();
                    Bitmap bitmap2 = newBitmap;
                    Bitmap lastBitmap = Bitmap.createScaledBitmap(bitmap2, 80, (int) (((float) bitmap2.getHeight()) / (((float) bitmap2.getWidth()) / 80.0f)), true);
                    if (lastBitmap != null) {
                        if (lastBitmap != bitmap2) {
                            bitmap2.recycle();
                        }
                        Utilities.blurBitmap(lastBitmap, 7, 1, lastBitmap.getWidth(), lastBitmap.getHeight(), lastBitmap.getRowBytes());
                        lastBitmap.compress(Bitmap.CompressFormat.JPEG, 87, new FileOutputStream(new File(ApplicationLoader.getFilesDirFixed(), "cthumb.jpg")));
                        lastBitmap.recycle();
                    }
                }
            } catch (Throwable th) {
            }
        }
    }

    public void onActivityResultFragment(int requestCode, Intent data, String currentPicturePath) {
        boolean z;
        String currentPicturePath2;
        Intent data2;
        String videoPath;
        int orientation;
        int i = requestCode;
        String str = currentPicturePath;
        if (this.parentAlert.baseFragment == null) {
            String currentPicturePath3 = str;
        } else if (this.parentAlert.baseFragment.getParentActivity() == null) {
            String str2 = str;
        } else {
            mediaFromExternalCamera = true;
            if (i == 0) {
                PhotoViewer.getInstance().setParentActivity(this.parentAlert.baseFragment.getParentActivity(), this.resourcesProvider);
                PhotoViewer.getInstance().setMaxSelectedPhotos(this.parentAlert.maxSelectedPhotos, this.parentAlert.allowOrder);
                new ArrayList();
                int orientation2 = 0;
                try {
                    switch (new ExifInterface(str).getAttributeInt("Orientation", 1)) {
                        case 3:
                            orientation2 = 180;
                            break;
                        case 6:
                            orientation2 = 90;
                            break;
                        case 8:
                            orientation2 = 270;
                            break;
                    }
                    orientation = orientation2;
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                    orientation = 0;
                }
                int i2 = lastImageId;
                lastImageId = i2 - 1;
                MediaController.PhotoEntry photoEntry = new MediaController.PhotoEntry(0, i2, 0, currentPicturePath, orientation, false, 0, 0, 0);
                photoEntry.canDeleteAfter = true;
                openPhotoViewer(photoEntry, false, true);
                String str3 = currentPicturePath;
            } else if (i == 2) {
                String videoPath2 = null;
                if (BuildVars.LOGS_ENABLED) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("pic path ");
                    currentPicturePath2 = currentPicturePath;
                    z = false;
                    sb.append(currentPicturePath2);
                    FileLog.d(sb.toString());
                } else {
                    currentPicturePath2 = currentPicturePath;
                    z = false;
                }
                if (data == null || currentPicturePath2 == null || !new File(currentPicturePath2).exists()) {
                    data2 = data;
                } else {
                    data2 = null;
                }
                if (data2 != null) {
                    Uri uri = data2.getData();
                    if (uri != null) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("video record uri " + uri.toString());
                        }
                        videoPath2 = AndroidUtilities.getPath(uri);
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("resolved path = " + videoPath2);
                        }
                        if (videoPath2 == null || !new File(videoPath2).exists()) {
                            videoPath2 = currentPicturePath;
                        }
                    } else {
                        videoPath2 = currentPicturePath;
                    }
                    if (!(this.parentAlert.baseFragment instanceof ChatActivity) || !((ChatActivity) this.parentAlert.baseFragment).isSecretChat()) {
                        AndroidUtilities.addMediaToGallery(currentPicturePath);
                    }
                    currentPicturePath2 = null;
                }
                if (videoPath2 != null || currentPicturePath2 == null || !new File(currentPicturePath2).exists()) {
                    videoPath = videoPath2;
                } else {
                    videoPath = currentPicturePath2;
                }
                MediaMetadataRetriever mediaMetadataRetriever = null;
                long duration = 0;
                try {
                    MediaMetadataRetriever mediaMetadataRetriever2 = new MediaMetadataRetriever();
                    mediaMetadataRetriever2.setDataSource(videoPath);
                    String d = mediaMetadataRetriever2.extractMetadata(9);
                    if (d != null) {
                        duration = (long) ((int) Math.ceil((double) (((float) Long.parseLong(d)) / 1000.0f)));
                    }
                    try {
                        mediaMetadataRetriever2.release();
                    } catch (Exception e2) {
                        FileLog.e((Throwable) e2);
                    }
                } catch (Exception e3) {
                    FileLog.e((Throwable) e3);
                    if (mediaMetadataRetriever != null) {
                        mediaMetadataRetriever.release();
                    }
                } catch (Throwable th) {
                    Throwable th2 = th;
                    if (mediaMetadataRetriever != null) {
                        try {
                            mediaMetadataRetriever.release();
                        } catch (Exception e4) {
                            FileLog.e((Throwable) e4);
                        }
                    }
                    throw th2;
                }
                Bitmap bitmap = SendMessagesHelper.createVideoThumbnail(videoPath, 1);
                File cacheFile = new File(FileLoader.getDirectory(4), "-2147483648_" + SharedConfig.getLastLocalId() + ".jpg");
                try {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 55, new FileOutputStream(cacheFile));
                } catch (Throwable e5) {
                    FileLog.e(e5);
                }
                SharedConfig.saveConfig();
                int i3 = lastImageId;
                lastImageId = i3 - 1;
                MediaController.PhotoEntry photoEntry2 = new MediaController.PhotoEntry(0, i3, 0, videoPath, 0, true, 0, 0, 0);
                photoEntry2.duration = (int) duration;
                photoEntry2.thumbPath = cacheFile.getAbsolutePath();
                openPhotoViewer(photoEntry2, z, true);
                return;
            }
            Intent intent = data;
        }
    }

    public void closeCamera(boolean animated) {
        if (!this.takingPhoto && this.cameraView != null) {
            int[] iArr = this.animateCameraValues;
            int i = this.itemSize;
            iArr[1] = i;
            iArr[2] = i;
            Runnable runnable = this.zoomControlHideRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.zoomControlHideRunnable = null;
            }
            if (animated) {
                this.additionCloseCameraY = this.cameraView.getTranslationY();
                this.cameraAnimationInProgress = true;
                ArrayList<Animator> animators = new ArrayList<>();
                animators.add(ObjectAnimator.ofFloat(this, "cameraOpenProgress", new float[]{0.0f}));
                animators.add(ObjectAnimator.ofFloat(this.cameraPanel, View.ALPHA, new float[]{0.0f}));
                animators.add(ObjectAnimator.ofFloat(this.zoomControlView, View.ALPHA, new float[]{0.0f}));
                animators.add(ObjectAnimator.ofFloat(this.counterTextView, View.ALPHA, new float[]{0.0f}));
                animators.add(ObjectAnimator.ofFloat(this.cameraPhotoRecyclerView, View.ALPHA, new float[]{0.0f}));
                int a = 0;
                while (true) {
                    if (a >= 2) {
                        break;
                    } else if (this.flashModeButton[a].getVisibility() == 0) {
                        animators.add(ObjectAnimator.ofFloat(this.flashModeButton[a], View.ALPHA, new float[]{0.0f}));
                        break;
                    } else {
                        a++;
                    }
                }
                this.animationIndex = NotificationCenter.getInstance(this.parentAlert.currentAccount).setAnimationInProgress(this.animationIndex, (int[]) null);
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(animators);
                animatorSet.setDuration(220);
                animatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        NotificationCenter.getInstance(ChatAttachAlertPhotoLayout.this.parentAlert.currentAccount).onAnimationFinish(ChatAttachAlertPhotoLayout.this.animationIndex);
                        ChatAttachAlertPhotoLayout.this.cameraExpanded = false;
                        ChatAttachAlertPhotoLayout.this.setCameraOpenProgress(0.0f);
                        boolean unused = ChatAttachAlertPhotoLayout.this.cameraAnimationInProgress = false;
                        if (Build.VERSION.SDK_INT >= 21 && ChatAttachAlertPhotoLayout.this.cameraView != null) {
                            ChatAttachAlertPhotoLayout.this.cameraView.invalidateOutline();
                        } else if (ChatAttachAlertPhotoLayout.this.cameraView != null) {
                            ChatAttachAlertPhotoLayout.this.cameraView.invalidate();
                        }
                        boolean unused2 = ChatAttachAlertPhotoLayout.this.cameraOpened = false;
                        if (ChatAttachAlertPhotoLayout.this.cameraPanel != null) {
                            ChatAttachAlertPhotoLayout.this.cameraPanel.setVisibility(8);
                        }
                        if (ChatAttachAlertPhotoLayout.this.zoomControlView != null) {
                            ChatAttachAlertPhotoLayout.this.zoomControlView.setVisibility(8);
                            ChatAttachAlertPhotoLayout.this.zoomControlView.setTag((Object) null);
                        }
                        if (ChatAttachAlertPhotoLayout.this.cameraPhotoRecyclerView != null) {
                            ChatAttachAlertPhotoLayout.this.cameraPhotoRecyclerView.setVisibility(8);
                        }
                        if (ChatAttachAlertPhotoLayout.this.cameraView != null) {
                            ChatAttachAlertPhotoLayout.this.cameraView.setFpsLimit(30);
                            if (Build.VERSION.SDK_INT >= 21) {
                                ChatAttachAlertPhotoLayout.this.cameraView.setSystemUiVisibility(1024);
                            }
                        }
                    }
                });
                animatorSet.start();
            } else {
                this.cameraExpanded = false;
                setCameraOpenProgress(0.0f);
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
                int a2 = 0;
                while (true) {
                    if (a2 >= 2) {
                        break;
                    } else if (this.flashModeButton[a2].getVisibility() == 0) {
                        this.flashModeButton[a2].setAlpha(0.0f);
                        break;
                    } else {
                        a2++;
                    }
                }
                this.cameraOpened = false;
                this.cameraView.setFpsLimit(30);
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

    public void setCameraOpenProgress(float value) {
        int cameraViewW;
        int cameraViewH;
        float f = value;
        if (this.cameraView != null) {
            this.cameraOpenProgress = f;
            int[] iArr = this.animateCameraValues;
            float startWidth = (float) iArr[1];
            float startHeight = (float) iArr[2];
            boolean isPortrait = AndroidUtilities.displaySize.x < AndroidUtilities.displaySize.y;
            float endWidth = (float) ((this.parentAlert.getContainer().getWidth() - this.parentAlert.getLeftInset()) - this.parentAlert.getRightInset());
            float endHeight = (float) (this.parentAlert.getContainer().getHeight() - this.parentAlert.getBottomInset());
            float[] fArr = this.cameraViewLocation;
            float fromX = fArr[0];
            float fromY = fArr[1];
            float toY = this.additionCloseCameraY;
            if (f == 0.0f) {
                this.cameraIcon.setTranslationX(fArr[0]);
                this.cameraIcon.setTranslationY(this.cameraViewLocation[1] + this.cameraViewOffsetY);
            }
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.cameraView.getLayoutParams();
            float textureStartHeight = this.cameraView.getTextureHeight(startWidth, startHeight);
            float fromScale = textureStartHeight / this.cameraView.getTextureHeight(endWidth, endHeight);
            float fromScaleY = startHeight / endHeight;
            float fromScaleX = startWidth / endWidth;
            boolean z = isPortrait;
            if (this.cameraExpanded) {
                cameraViewW = (int) endWidth;
                float f2 = textureStartHeight;
                float s = ((1.0f - f) * fromScale) + f;
                this.cameraView.getTextureView().setScaleX(s);
                this.cameraView.getTextureView().setScaleY(s);
                float f3 = s;
                this.cameraView.setTranslationX((((1.0f - f) * fromX) + (0.0f * f)) - (((1.0f - (((1.0f - f) * fromScaleX) + f)) * endWidth) / 2.0f));
                this.cameraView.setTranslationY((((1.0f - f) * fromY) + (toY * f)) - (((1.0f - (((1.0f - f) * fromScaleY) + f)) * endHeight) / 2.0f));
                this.animationClipTop = ((1.0f - f) * fromY) - this.cameraView.getTranslationY();
                this.animationClipBottom = (((fromY + startHeight) * (1.0f - f)) - this.cameraView.getTranslationY()) + (endHeight * f);
                this.animationClipLeft = ((1.0f - f) * fromX) - this.cameraView.getTranslationX();
                this.animationClipRight = (((fromX + startWidth) * (1.0f - f)) - this.cameraView.getTranslationX()) + (endWidth * f);
                cameraViewH = (int) endHeight;
                float f4 = startHeight;
            } else {
                cameraViewW = (int) startWidth;
                cameraViewH = (int) startHeight;
                float f5 = startHeight;
                this.cameraView.getTextureView().setScaleX(1.0f);
                this.cameraView.getTextureView().setScaleY(1.0f);
                this.animationClipTop = 0.0f;
                this.animationClipBottom = endHeight;
                this.animationClipLeft = 0.0f;
                this.animationClipRight = endWidth;
                this.cameraView.setTranslationX(fromX);
                this.cameraView.setTranslationY(fromY);
            }
            if (f <= 0.5f) {
                this.cameraIcon.setAlpha(1.0f - (f / 0.5f));
            } else {
                this.cameraIcon.setAlpha(0.0f);
            }
            if (!(layoutParams.width == cameraViewW && layoutParams.height == cameraViewH)) {
                layoutParams.width = cameraViewW;
                layoutParams.height = cameraViewH;
                this.cameraView.requestLayout();
            }
            if (Build.VERSION.SDK_INT >= 21) {
                this.cameraView.invalidateOutline();
            } else {
                this.cameraView.invalidate();
            }
        }
    }

    public float getCameraOpenProgress() {
        return this.cameraOpenProgress;
    }

    /* access modifiers changed from: private */
    public void checkCameraViewPosition() {
        float newCameraViewOffsetY;
        TextView textView;
        RecyclerView.ViewHolder holder;
        if (Build.VERSION.SDK_INT >= 21) {
            CameraView cameraView2 = this.cameraView;
            if (cameraView2 != null) {
                cameraView2.invalidateOutline();
            }
            RecyclerView.ViewHolder holder2 = this.gridView.findViewHolderForAdapterPosition(this.itemsPerRow - 1);
            if (holder2 != null) {
                holder2.itemView.invalidateOutline();
            }
            if ((!this.adapter.needCamera || !this.deviceHasGoodCamera || this.selectedAlbumEntry != this.galleryAlbumEntry) && (holder = this.gridView.findViewHolderForAdapterPosition(0)) != null) {
                holder.itemView.invalidateOutline();
            }
        }
        CameraView cameraView3 = this.cameraView;
        if (cameraView3 != null) {
            cameraView3.invalidate();
        }
        if (Build.VERSION.SDK_INT >= 23 && (textView = this.recordTime) != null) {
            ((ViewGroup.MarginLayoutParams) textView.getLayoutParams()).topMargin = getRootWindowInsets() == null ? AndroidUtilities.dp(16.0f) : getRootWindowInsets().getSystemWindowInsetTop() + AndroidUtilities.dp(2.0f);
        }
        if (this.deviceHasGoodCamera) {
            int count = this.gridView.getChildCount();
            int a = 0;
            while (true) {
                if (a >= count) {
                    break;
                }
                View child = this.gridView.getChildAt(a);
                if (!(child instanceof PhotoAttachCameraCell)) {
                    a++;
                } else if (Build.VERSION.SDK_INT < 19 || child.isAttachedToWindow()) {
                    float topLocal = child.getY() + this.gridView.getY() + getY();
                    float top = this.parentAlert.getSheetContainer().getY() + topLocal;
                    float left = child.getX() + this.gridView.getX() + getX() + this.parentAlert.getSheetContainer().getX();
                    if (Build.VERSION.SDK_INT >= 23) {
                        left -= (float) getRootWindowInsets().getSystemWindowInsetLeft();
                    }
                    float maxY = (float) (((Build.VERSION.SDK_INT < 21 || this.parentAlert.inBubbleMode) ? 0 : AndroidUtilities.statusBarHeight) + ActionBar.getCurrentActionBarHeight());
                    if (topLocal < maxY) {
                        newCameraViewOffsetY = maxY - topLocal;
                    } else {
                        newCameraViewOffsetY = 0.0f;
                    }
                    if (newCameraViewOffsetY != this.cameraViewOffsetY) {
                        this.cameraViewOffsetY = newCameraViewOffsetY;
                        if (this.cameraView != null) {
                            if (Build.VERSION.SDK_INT >= 21) {
                                this.cameraView.invalidateOutline();
                            } else {
                                this.cameraView.invalidate();
                            }
                        }
                        FrameLayout frameLayout = this.cameraIcon;
                        if (frameLayout != null) {
                            frameLayout.invalidate();
                        }
                    }
                    float maxY2 = (float) ((int) (((float) (this.parentAlert.getSheetContainer().getMeasuredHeight() - this.parentAlert.buttonsRecyclerView.getMeasuredHeight())) + this.parentAlert.buttonsRecyclerView.getTranslationY()));
                    if (((float) child.getMeasuredHeight()) + topLocal > maxY2) {
                        this.cameraViewOffsetBottomY = (((float) child.getMeasuredHeight()) + topLocal) - maxY2;
                    } else {
                        this.cameraViewOffsetBottomY = 0.0f;
                    }
                    float[] fArr = this.cameraViewLocation;
                    fArr[0] = left;
                    fArr[1] = top;
                    applyCameraViewPosition();
                    return;
                }
            }
            if (!(this.cameraViewOffsetY == 0.0f && this.cameraViewOffsetX == 0.0f)) {
                this.cameraViewOffsetX = 0.0f;
                this.cameraViewOffsetY = 0.0f;
                if (this.cameraView != null) {
                    if (Build.VERSION.SDK_INT >= 21) {
                        this.cameraView.invalidateOutline();
                    } else {
                        this.cameraView.invalidate();
                    }
                }
                FrameLayout frameLayout2 = this.cameraIcon;
                if (frameLayout2 != null) {
                    frameLayout2.invalidate();
                }
            }
            this.cameraViewLocation[0] = (float) AndroidUtilities.dp(-400.0f);
            this.cameraViewLocation[1] = 0.0f;
            applyCameraViewPosition();
        }
    }

    private void applyCameraViewPosition() {
        CameraView cameraView2 = this.cameraView;
        if (cameraView2 != null) {
            if (!this.cameraOpened) {
                cameraView2.setTranslationX(this.cameraViewLocation[0]);
                this.cameraView.setTranslationY(this.cameraViewLocation[1] + this.currentPanTranslationY);
            }
            this.cameraIcon.setTranslationX(this.cameraViewLocation[0]);
            this.cameraIcon.setTranslationY(this.cameraViewLocation[1] + this.cameraViewOffsetY + this.currentPanTranslationY);
            int finalWidth = this.itemSize;
            int finalHeight = this.itemSize;
            if (!this.cameraOpened) {
                this.cameraView.setClipTop((int) this.cameraViewOffsetY);
                this.cameraView.setClipBottom((int) this.cameraViewOffsetBottomY);
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.cameraView.getLayoutParams();
                if (!(layoutParams.height == finalHeight && layoutParams.width == finalWidth)) {
                    layoutParams.width = finalWidth;
                    layoutParams.height = finalHeight;
                    this.cameraView.setLayoutParams(layoutParams);
                    AndroidUtilities.runOnUIThread(new ChatAttachAlertPhotoLayout$$ExternalSyntheticLambda15(this, layoutParams));
                }
            }
            int i = this.itemSize;
            int finalWidth2 = (int) (((float) i) - this.cameraViewOffsetX);
            int finalHeight2 = (int) ((((float) i) - this.cameraViewOffsetY) - this.cameraViewOffsetBottomY);
            FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.cameraIcon.getLayoutParams();
            if (layoutParams2.height != finalHeight2 || layoutParams2.width != finalWidth2) {
                layoutParams2.width = finalWidth2;
                layoutParams2.height = finalHeight2;
                this.cameraIcon.setLayoutParams(layoutParams2);
                AndroidUtilities.runOnUIThread(new ChatAttachAlertPhotoLayout$$ExternalSyntheticLambda16(this, layoutParams2));
            }
        }
    }

    /* renamed from: lambda$applyCameraViewPosition$12$org-telegram-ui-Components-ChatAttachAlertPhotoLayout  reason: not valid java name */
    public /* synthetic */ void m2169x2e05399b(FrameLayout.LayoutParams layoutParamsFinal) {
        CameraView cameraView2 = this.cameraView;
        if (cameraView2 != null) {
            cameraView2.setLayoutParams(layoutParamsFinal);
        }
    }

    /* renamed from: lambda$applyCameraViewPosition$13$org-telegram-ui-Components-ChatAttachAlertPhotoLayout  reason: not valid java name */
    public /* synthetic */ void m2170x57598edc(FrameLayout.LayoutParams layoutParamsFinal) {
        FrameLayout frameLayout = this.cameraIcon;
        if (frameLayout != null) {
            frameLayout.setLayoutParams(layoutParamsFinal);
        }
    }

    public HashMap<Object, Object> getSelectedPhotos() {
        return selectedPhotos;
    }

    public ArrayList<Object> getSelectedPhotosOrder() {
        return selectedPhotosOrder;
    }

    public void checkStorage() {
        if (this.noGalleryPermissions && Build.VERSION.SDK_INT >= 23) {
            boolean z = this.parentAlert.baseFragment.getParentActivity().checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != 0;
            this.noGalleryPermissions = z;
            if (!z) {
                loadGalleryPhotos();
            }
            this.adapter.notifyDataSetChanged();
            this.cameraAttachAdapter.notifyDataSetChanged();
        }
    }

    /* access modifiers changed from: package-private */
    public void scrollToTop() {
        this.gridView.smoothScrollToPosition(0);
    }

    /* access modifiers changed from: package-private */
    public int needsActionBar() {
        return 1;
    }

    /* access modifiers changed from: package-private */
    public void onMenuItemClick(int id) {
        TLRPC.Chat chat;
        if ((id == 0 || id == 1) && this.parentAlert.maxSelectedPhotos > 0 && selectedPhotosOrder.size() > 1 && (this.parentAlert.baseFragment instanceof ChatActivity) && (chat = ((ChatActivity) this.parentAlert.baseFragment).getCurrentChat()) != null && !ChatObject.hasAdminRights(chat) && chat.slowmode_enabled) {
            AlertsCreator.createSimpleAlert(getContext(), LocaleController.getString("Slowmode", NUM), LocaleController.getString("SlowmodeSendError", NUM), this.resourcesProvider).show();
        } else if (id == 0) {
            if (this.parentAlert.editingMessageObject != null || !(this.parentAlert.baseFragment instanceof ChatActivity) || !((ChatActivity) this.parentAlert.baseFragment).isInScheduleMode()) {
                this.parentAlert.applyCaption();
                this.parentAlert.delegate.didPressedButton(7, false, true, 0, false);
                return;
            }
            AlertsCreator.createScheduleDatePickerDialog(getContext(), ((ChatActivity) this.parentAlert.baseFragment).getDialogId(), (AlertsCreator.ScheduleDatePickerDelegate) new ChatAttachAlertPhotoLayout$$ExternalSyntheticLambda2(this), this.resourcesProvider);
        } else if (id == 1) {
            if (this.parentAlert.editingMessageObject != null || !(this.parentAlert.baseFragment instanceof ChatActivity) || !((ChatActivity) this.parentAlert.baseFragment).isInScheduleMode()) {
                this.parentAlert.applyCaption();
                this.parentAlert.delegate.didPressedButton(4, true, true, 0, false);
                return;
            }
            AlertsCreator.createScheduleDatePickerDialog(getContext(), ((ChatActivity) this.parentAlert.baseFragment).getDialogId(), (AlertsCreator.ScheduleDatePickerDelegate) new ChatAttachAlertPhotoLayout$$ExternalSyntheticLambda3(this), this.resourcesProvider);
        } else if (id == 2) {
            try {
                if (!(this.parentAlert.baseFragment instanceof ChatActivity)) {
                    if (this.parentAlert.avatarPicker != 2) {
                        Intent photoPickerIntent = new Intent("android.intent.action.PICK");
                        photoPickerIntent.setType("image/*");
                        if (this.parentAlert.avatarPicker != 0) {
                            this.parentAlert.baseFragment.startActivityForResult(photoPickerIntent, 14);
                        } else {
                            this.parentAlert.baseFragment.startActivityForResult(photoPickerIntent, 1);
                        }
                        this.parentAlert.dismiss();
                    }
                }
                Intent videoPickerIntent = new Intent();
                videoPickerIntent.setType("video/*");
                videoPickerIntent.setAction("android.intent.action.GET_CONTENT");
                videoPickerIntent.putExtra("android.intent.extra.sizeLimit", NUM);
                Intent photoPickerIntent2 = new Intent("android.intent.action.PICK");
                photoPickerIntent2.setType("image/*");
                Intent chooserIntent = Intent.createChooser(photoPickerIntent2, (CharSequence) null);
                chooserIntent.putExtra("android.intent.extra.INITIAL_INTENTS", new Intent[]{videoPickerIntent});
                if (this.parentAlert.avatarPicker != 0) {
                    this.parentAlert.baseFragment.startActivityForResult(chooserIntent, 14);
                } else {
                    this.parentAlert.baseFragment.startActivityForResult(chooserIntent, 1);
                }
                this.parentAlert.dismiss();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        } else if (id >= 10) {
            MediaController.AlbumEntry albumEntry = this.dropDownAlbums.get(id - 10);
            this.selectedAlbumEntry = albumEntry;
            if (albumEntry == this.galleryAlbumEntry) {
                this.dropDown.setText(LocaleController.getString("ChatGallery", NUM));
            } else {
                this.dropDown.setText(albumEntry.bucketName);
            }
            this.adapter.notifyDataSetChanged();
            this.cameraAttachAdapter.notifyDataSetChanged();
            this.layoutManager.scrollToPositionWithOffset(0, (-this.gridView.getPaddingTop()) + AndroidUtilities.dp(7.0f));
        }
    }

    /* renamed from: lambda$onMenuItemClick$14$org-telegram-ui-Components-ChatAttachAlertPhotoLayout  reason: not valid java name */
    public /* synthetic */ void m2179x320ee4a7(boolean notify, int scheduleDate) {
        this.parentAlert.applyCaption();
        this.parentAlert.delegate.didPressedButton(7, false, notify, scheduleDate, false);
    }

    /* renamed from: lambda$onMenuItemClick$15$org-telegram-ui-Components-ChatAttachAlertPhotoLayout  reason: not valid java name */
    public /* synthetic */ void m2180x5b6339e8(boolean notify, int scheduleDate) {
        this.parentAlert.applyCaption();
        this.parentAlert.delegate.didPressedButton(4, true, notify, scheduleDate, false);
    }

    /* access modifiers changed from: package-private */
    public int getSelectedItemsCount() {
        return selectedPhotosOrder.size();
    }

    /* access modifiers changed from: package-private */
    public void onSelectedItemsCountChanged(int count) {
        if (count <= 1 || this.parentAlert.editingMessageObject != null) {
            this.parentAlert.selectedMenuItem.hideSubItem(0);
            if (count == 0) {
                this.parentAlert.selectedMenuItem.showSubItem(2);
                this.parentAlert.selectedMenuItem.hideSubItem(1);
            } else {
                this.parentAlert.selectedMenuItem.showSubItem(1);
            }
        } else {
            this.parentAlert.selectedMenuItem.showSubItem(0);
        }
        if (count != 0) {
            this.parentAlert.selectedMenuItem.hideSubItem(2);
        }
    }

    /* access modifiers changed from: package-private */
    public void applyCaption(String text) {
        Object entry = selectedPhotos.get(Integer.valueOf(((Integer) selectedPhotosOrder.get(0)).intValue()));
        if (entry instanceof MediaController.PhotoEntry) {
            ((MediaController.PhotoEntry) entry).caption = text;
        } else if (entry instanceof MediaController.SearchImage) {
            ((MediaController.SearchImage) entry).caption = text;
        }
    }

    /* access modifiers changed from: package-private */
    public void onDestroy() {
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.cameraInitied);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.albumsDidLoad);
    }

    /* access modifiers changed from: package-private */
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
                return;
            }
            if (this.cameraView != null && shutterButton2.getState() == ShutterButton.State.RECORDING) {
                this.shutterButton.setState(ShutterButton.State.DEFAULT, true);
            }
            this.requestingPermissions = false;
        }
    }

    /* access modifiers changed from: package-private */
    public void onResume() {
        if (this.parentAlert.isShowing() && !this.parentAlert.isDismissed()) {
            checkCamera(false);
        }
    }

    /* access modifiers changed from: package-private */
    public int getListTopPadding() {
        return this.gridView.getPaddingTop();
    }

    /* access modifiers changed from: package-private */
    public int getCurrentItemTop() {
        if (this.gridView.getChildCount() <= 0) {
            RecyclerListView recyclerListView = this.gridView;
            recyclerListView.setTopGlowOffset(recyclerListView.getPaddingTop());
            this.progressView.setTranslationY(0.0f);
            return Integer.MAX_VALUE;
        }
        View child = this.gridView.getChildAt(0);
        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.gridView.findContainingViewHolder(child);
        int top = child.getTop();
        int newOffset = AndroidUtilities.dp(7.0f);
        if (top >= AndroidUtilities.dp(7.0f) && holder != null && holder.getAdapterPosition() == 0) {
            newOffset = top;
        }
        this.progressView.setTranslationY((float) (((((getMeasuredHeight() - newOffset) - AndroidUtilities.dp(50.0f)) - this.progressView.getMeasuredHeight()) / 2) + newOffset));
        this.gridView.setTopGlowOffset(newOffset);
        return newOffset;
    }

    /* access modifiers changed from: package-private */
    public int getFirstOffset() {
        return getListTopPadding() + AndroidUtilities.dp(56.0f);
    }

    /* access modifiers changed from: package-private */
    public void checkColors() {
        FrameLayout frameLayout = this.cameraIcon;
        if (frameLayout != null) {
            frameLayout.invalidate();
        }
        String str = "voipgroup_actionBarItems";
        String textColor = this.forceDarkTheme ? str : "dialogTextBlack";
        Theme.setDrawableColor(this.cameraDrawable, getThemedColor("dialogCameraIcon"));
        this.progressView.setTextColor(getThemedColor("emptyListPlaceholder"));
        this.gridView.setGlowColor(getThemedColor("dialogScrollGlow"));
        RecyclerView.ViewHolder holder = this.gridView.findViewHolderForAdapterPosition(0);
        if (holder != null && (holder.itemView instanceof PhotoAttachCameraCell)) {
            ((PhotoAttachCameraCell) holder.itemView).getImageView().setColorFilter(new PorterDuffColorFilter(getThemedColor("dialogCameraIcon"), PorterDuff.Mode.MULTIPLY));
        }
        this.dropDown.setTextColor(getThemedColor(textColor));
        this.dropDownContainer.setPopupItemsColor(getThemedColor(this.forceDarkTheme ? str : "actionBarDefaultSubmenuItem"), false);
        ActionBarMenuItem actionBarMenuItem = this.dropDownContainer;
        if (!this.forceDarkTheme) {
            str = "actionBarDefaultSubmenuItem";
        }
        actionBarMenuItem.setPopupItemsColor(getThemedColor(str), true);
        this.dropDownContainer.redrawPopup(getThemedColor(this.forceDarkTheme ? "voipgroup_actionBarUnscrolled" : "actionBarDefaultSubmenuBackground"));
        Theme.setDrawableColor(this.dropDownDrawable, getThemedColor(textColor));
    }

    /* access modifiers changed from: package-private */
    public void onInit(boolean hasMedia) {
        this.mediaEnabled = hasMedia;
        CameraView cameraView2 = this.cameraView;
        float f = 1.0f;
        if (cameraView2 != null) {
            cameraView2.setAlpha(hasMedia ? 1.0f : 0.2f);
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
        boolean z = true;
        if ((this.parentAlert.baseFragment instanceof ChatActivity) && this.parentAlert.avatarPicker == 0) {
            this.galleryAlbumEntry = MediaController.allMediaAlbumEntry;
            if (this.mediaEnabled) {
                this.progressView.setText(LocaleController.getString("NoPhotos", NUM));
            } else {
                TLRPC.Chat chat = ((ChatActivity) this.parentAlert.baseFragment).getCurrentChat();
                if (ChatObject.isActionBannedByDefault(chat, 7)) {
                    this.progressView.setText(LocaleController.getString("GlobalAttachMediaRestricted", NUM));
                } else if (AndroidUtilities.isBannedForever(chat.banned_rights)) {
                    this.progressView.setText(LocaleController.formatString("AttachMediaRestrictedForever", NUM, new Object[0]));
                } else {
                    this.progressView.setText(LocaleController.formatString("AttachMediaRestricted", NUM, LocaleController.formatDateForBan((long) chat.banned_rights.until_date)));
                }
            }
        } else if (this.parentAlert.avatarPicker == 2) {
            this.galleryAlbumEntry = MediaController.allMediaAlbumEntry;
        } else {
            this.galleryAlbumEntry = MediaController.allPhotosAlbumEntry;
        }
        if (Build.VERSION.SDK_INT >= 23) {
            if (this.parentAlert.baseFragment.getParentActivity().checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == 0) {
                z = false;
            }
            this.noGalleryPermissions = z;
        }
        if (this.galleryAlbumEntry != null) {
            for (int a = 0; a < Math.min(100, this.galleryAlbumEntry.photos.size()); a++) {
                this.galleryAlbumEntry.photos.get(a).reset();
            }
        }
        clearSelectedPhotos();
        updatePhotosCounter(false);
        this.cameraPhotoLayoutManager.scrollToPositionWithOffset(0, 1000000);
        this.layoutManager.scrollToPositionWithOffset(0, 1000000);
        this.dropDown.setText(LocaleController.getString("ChatGallery", NUM));
        MediaController.AlbumEntry albumEntry = this.galleryAlbumEntry;
        this.selectedAlbumEntry = albumEntry;
        if (albumEntry != null) {
            this.loading = false;
            EmptyTextProgressView emptyTextProgressView = this.progressView;
            if (emptyTextProgressView != null) {
                emptyTextProgressView.showTextView();
            }
        }
        updateAlbumsDropDown();
    }

    /* access modifiers changed from: package-private */
    public boolean canScheduleMessages() {
        boolean hasTtl = false;
        Iterator<Map.Entry<Object, Object>> it = selectedPhotos.entrySet().iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            Object object = it.next().getValue();
            if (!(object instanceof MediaController.PhotoEntry)) {
                if ((object instanceof MediaController.SearchImage) && ((MediaController.SearchImage) object).ttl != 0) {
                    hasTtl = true;
                    break;
                }
            } else if (((MediaController.PhotoEntry) object).ttl != 0) {
                hasTtl = true;
                break;
            }
        }
        if (hasTtl) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    public void onButtonsTranslationYUpdated() {
        checkCameraViewPosition();
        invalidate();
    }

    public void setTranslationY(float translationY) {
        if (this.parentAlert.getSheetAnimationType() == 1) {
            float scale = (translationY / 40.0f) * -0.1f;
            int N = this.gridView.getChildCount();
            for (int a = 0; a < N; a++) {
                View child = this.gridView.getChildAt(a);
                if (child instanceof PhotoAttachCameraCell) {
                    PhotoAttachCameraCell cell = (PhotoAttachCameraCell) child;
                    cell.getImageView().setScaleX(scale + 1.0f);
                    cell.getImageView().setScaleY(1.0f + scale);
                } else if (child instanceof PhotoAttachPhotoCell) {
                    PhotoAttachPhotoCell cell2 = (PhotoAttachPhotoCell) child;
                    cell2.getCheckBox().setScaleX(scale + 1.0f);
                    cell2.getCheckBox().setScaleY(1.0f + scale);
                }
            }
        }
        super.setTranslationY(translationY);
        this.parentAlert.getSheetContainer().invalidate();
        invalidate();
    }

    public void requestLayout() {
        if (!this.ignoreLayout) {
            super.requestLayout();
        }
    }

    /* access modifiers changed from: package-private */
    public void onShow() {
        clearSelectedPhotos();
        this.parentAlert.actionBar.setTitle("");
        this.layoutManager.scrollToPositionWithOffset(0, 0);
        this.dropDownContainer.setVisibility(0);
    }

    /* access modifiers changed from: package-private */
    public void onShown() {
        this.isHidden = false;
        CameraView cameraView2 = this.cameraView;
        if (cameraView2 != null) {
            cameraView2.setVisibility(0);
        }
        FrameLayout frameLayout = this.cameraIcon;
        if (frameLayout != null) {
            frameLayout.setVisibility(0);
        }
        if (this.cameraView != null) {
            int count = this.gridView.getChildCount();
            int a = 0;
            while (true) {
                if (a >= count) {
                    break;
                }
                View child = this.gridView.getChildAt(a);
                if (child instanceof PhotoAttachCameraCell) {
                    child.setVisibility(4);
                    break;
                }
                a++;
            }
        }
        if (this.checkCameraWhenShown != 0) {
            this.checkCameraWhenShown = false;
            checkCamera(true);
        }
    }

    public void setCheckCameraWhenShown(boolean checkCameraWhenShown2) {
        this.checkCameraWhenShown = checkCameraWhenShown2;
    }

    /* access modifiers changed from: package-private */
    public void onHideShowProgress(float progress) {
        CameraView cameraView2 = this.cameraView;
        if (cameraView2 != null) {
            cameraView2.setAlpha(progress);
            this.cameraIcon.setAlpha(progress);
            if (progress != 0.0f && this.cameraView.getVisibility() != 0) {
                this.cameraView.setVisibility(0);
                this.cameraIcon.setVisibility(0);
            } else if (progress == 0.0f && this.cameraView.getVisibility() != 4) {
                this.cameraView.setVisibility(4);
                this.cameraIcon.setVisibility(4);
            }
        }
    }

    public void onHide() {
        this.isHidden = true;
        this.dropDownContainer.setVisibility(8);
        int count = this.gridView.getChildCount();
        for (int a = 0; a < count; a++) {
            View child = this.gridView.getChildAt(a);
            if (child instanceof PhotoAttachCameraCell) {
                child.setVisibility(0);
                saveLastCameraBitmap();
                ((PhotoAttachCameraCell) child).updateBitmap();
                return;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void onHidden() {
        CameraView cameraView2 = this.cameraView;
        if (cameraView2 != null) {
            cameraView2.setVisibility(8);
            this.cameraIcon.setVisibility(8);
        }
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (this.lastNotifyWidth != right - left) {
            this.lastNotifyWidth = right - left;
            PhotoAttachAdapter photoAttachAdapter = this.adapter;
            if (photoAttachAdapter != null) {
                photoAttachAdapter.notifyDataSetChanged();
            }
        }
        super.onLayout(changed, left, top, right, bottom);
        checkCameraViewPosition();
    }

    public void onPreMeasure(int availableWidth, int availableHeight) {
        int padding;
        this.ignoreLayout = true;
        if (AndroidUtilities.isTablet()) {
            this.itemsPerRow = 4;
        } else if (AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y) {
            this.itemsPerRow = 4;
        } else {
            this.itemsPerRow = 3;
        }
        ((FrameLayout.LayoutParams) getLayoutParams()).topMargin = ActionBar.getCurrentActionBarHeight();
        int dp = ((availableWidth - AndroidUtilities.dp(12.0f)) - AndroidUtilities.dp(10.0f)) / this.itemsPerRow;
        this.itemSize = dp;
        if (this.lastItemSize != dp) {
            this.lastItemSize = dp;
            AndroidUtilities.runOnUIThread(new ChatAttachAlertPhotoLayout$$ExternalSyntheticLambda12(this));
        }
        this.layoutManager.setSpanCount(Math.max(1, (this.itemSize * this.itemsPerRow) + (AndroidUtilities.dp(5.0f) * (this.itemsPerRow - 1))));
        int rows = (int) Math.ceil((double) (((float) (this.adapter.getItemCount() - 1)) / ((float) this.itemsPerRow)));
        int newSize = Math.max(0, ((availableHeight - ((this.itemSize * rows) + ((rows - 1) * AndroidUtilities.dp(5.0f)))) - ActionBar.getCurrentActionBarHeight()) - AndroidUtilities.dp(60.0f));
        if (this.gridExtraSpace != newSize) {
            this.gridExtraSpace = newSize;
            this.adapter.notifyDataSetChanged();
        }
        if (AndroidUtilities.isTablet() || AndroidUtilities.displaySize.x <= AndroidUtilities.displaySize.y) {
            padding = (availableHeight / 5) * 2;
        } else {
            padding = (int) (((float) availableHeight) / 3.5f);
        }
        int padding2 = padding - AndroidUtilities.dp(52.0f);
        if (padding2 < 0) {
            padding2 = 0;
        }
        if (this.gridView.getPaddingTop() != padding2) {
            this.gridView.setPadding(AndroidUtilities.dp(6.0f), padding2, AndroidUtilities.dp(6.0f), AndroidUtilities.dp(48.0f));
        }
        this.dropDown.setTextSize((AndroidUtilities.isTablet() || AndroidUtilities.displaySize.x <= AndroidUtilities.displaySize.y) ? 20.0f : 18.0f);
        this.ignoreLayout = false;
    }

    /* renamed from: lambda$onPreMeasure$16$org-telegram-ui-Components-ChatAttachAlertPhotoLayout  reason: not valid java name */
    public /* synthetic */ void m2181xvar_ac() {
        this.adapter.notifyDataSetChanged();
    }

    /* access modifiers changed from: package-private */
    public boolean canDismissWithTouchOutside() {
        return !this.cameraOpened;
    }

    /* access modifiers changed from: package-private */
    public void onContainerTranslationUpdated(float currentPanTranslationY2) {
        this.currentPanTranslationY = currentPanTranslationY2;
        checkCameraViewPosition();
        invalidate();
    }

    /* access modifiers changed from: package-private */
    public void onOpenAnimationEnd() {
        checkCamera(true);
    }

    /* access modifiers changed from: package-private */
    public void onDismissWithButtonClick(int item) {
        hideCamera((item == 0 || item == 2) ? false : true);
    }

    public boolean onDismiss() {
        if (this.cameraAnimationInProgress) {
            return true;
        }
        if (this.cameraOpened) {
            closeCamera(true);
            return true;
        }
        hideCamera(true);
        return false;
    }

    public boolean onSheetKeyDown(int keyCode, KeyEvent event) {
        if (!this.cameraOpened) {
            return false;
        }
        if (keyCode != 24 && keyCode != 25 && keyCode != 79 && keyCode != 85) {
            return false;
        }
        this.shutterButton.getDelegate().shutterReleased();
        return true;
    }

    public boolean onContainerViewTouchEvent(MotionEvent event) {
        if (this.cameraAnimationInProgress) {
            return true;
        }
        if (this.cameraOpened) {
            return processTouchEvent(event);
        }
        return false;
    }

    public boolean onCustomMeasure(View view, int width, int height) {
        boolean isPortrait = width < height;
        FrameLayout frameLayout = this.cameraIcon;
        if (view == frameLayout) {
            frameLayout.measure(View.MeasureSpec.makeMeasureSpec(this.itemSize, NUM), View.MeasureSpec.makeMeasureSpec((int) ((((float) this.itemSize) - this.cameraViewOffsetBottomY) - this.cameraViewOffsetY), NUM));
            return true;
        }
        CameraView cameraView2 = this.cameraView;
        if (view != cameraView2) {
            FrameLayout frameLayout2 = this.cameraPanel;
            if (view == frameLayout2) {
                if (isPortrait) {
                    frameLayout2.measure(View.MeasureSpec.makeMeasureSpec(width, NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(126.0f), NUM));
                } else {
                    frameLayout2.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(126.0f), NUM), View.MeasureSpec.makeMeasureSpec(height, NUM));
                }
                return true;
            }
            ZoomControlView zoomControlView2 = this.zoomControlView;
            if (view == zoomControlView2) {
                if (isPortrait) {
                    zoomControlView2.measure(View.MeasureSpec.makeMeasureSpec(width, NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f), NUM));
                } else {
                    zoomControlView2.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f), NUM), View.MeasureSpec.makeMeasureSpec(height, NUM));
                }
                return true;
            }
            RecyclerListView recyclerListView = this.cameraPhotoRecyclerView;
            if (view == recyclerListView) {
                this.cameraPhotoRecyclerViewIgnoreLayout = true;
                if (isPortrait) {
                    recyclerListView.measure(View.MeasureSpec.makeMeasureSpec(width, NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(80.0f), NUM));
                    if (this.cameraPhotoLayoutManager.getOrientation() != 0) {
                        this.cameraPhotoRecyclerView.setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f), 0);
                        this.cameraPhotoLayoutManager.setOrientation(0);
                        this.cameraAttachAdapter.notifyDataSetChanged();
                    }
                } else {
                    recyclerListView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(80.0f), NUM), View.MeasureSpec.makeMeasureSpec(height, NUM));
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
            cameraView2.measure(View.MeasureSpec.makeMeasureSpec(width, NUM), View.MeasureSpec.makeMeasureSpec(height, NUM));
            return true;
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public boolean onCustomLayout(View view, int left, int top, int right, int bottom) {
        int cx;
        int cy;
        int width = right - left;
        int height = bottom - top;
        boolean isPortrait = width < height;
        if (view == this.cameraPanel) {
            if (isPortrait) {
                if (this.cameraPhotoRecyclerView.getVisibility() == 0) {
                    this.cameraPanel.layout(0, bottom - AndroidUtilities.dp(222.0f), width, bottom - AndroidUtilities.dp(96.0f));
                } else {
                    this.cameraPanel.layout(0, bottom - AndroidUtilities.dp(126.0f), width, bottom);
                }
            } else if (this.cameraPhotoRecyclerView.getVisibility() == 0) {
                this.cameraPanel.layout(right - AndroidUtilities.dp(222.0f), 0, right - AndroidUtilities.dp(96.0f), height);
            } else {
                this.cameraPanel.layout(right - AndroidUtilities.dp(126.0f), 0, right, height);
            }
            return true;
        } else if (view == this.zoomControlView) {
            if (isPortrait) {
                if (this.cameraPhotoRecyclerView.getVisibility() == 0) {
                    this.zoomControlView.layout(0, bottom - AndroidUtilities.dp(310.0f), width, bottom - AndroidUtilities.dp(260.0f));
                } else {
                    this.zoomControlView.layout(0, bottom - AndroidUtilities.dp(176.0f), width, bottom - AndroidUtilities.dp(126.0f));
                }
            } else if (this.cameraPhotoRecyclerView.getVisibility() == 0) {
                this.zoomControlView.layout(right - AndroidUtilities.dp(310.0f), 0, right - AndroidUtilities.dp(260.0f), height);
            } else {
                this.zoomControlView.layout(right - AndroidUtilities.dp(176.0f), 0, right - AndroidUtilities.dp(126.0f), height);
            }
            return true;
        } else {
            TextView textView = this.counterTextView;
            if (view == textView) {
                if (isPortrait) {
                    cx = (width - textView.getMeasuredWidth()) / 2;
                    cy = bottom - AndroidUtilities.dp(167.0f);
                    this.counterTextView.setRotation(0.0f);
                    if (this.cameraPhotoRecyclerView.getVisibility() == 0) {
                        cy -= AndroidUtilities.dp(96.0f);
                    }
                } else {
                    cx = right - AndroidUtilities.dp(167.0f);
                    cy = (height / 2) + (this.counterTextView.getMeasuredWidth() / 2);
                    this.counterTextView.setRotation(-90.0f);
                    if (this.cameraPhotoRecyclerView.getVisibility() == 0) {
                        cx -= AndroidUtilities.dp(96.0f);
                    }
                }
                TextView textView2 = this.counterTextView;
                textView2.layout(cx, cy, textView2.getMeasuredWidth() + cx, this.counterTextView.getMeasuredHeight() + cy);
                return true;
            } else if (view != this.cameraPhotoRecyclerView) {
                return false;
            } else {
                if (isPortrait) {
                    int cy2 = height - AndroidUtilities.dp(88.0f);
                    view.layout(0, cy2, view.getMeasuredWidth(), view.getMeasuredHeight() + cy2);
                } else {
                    int cx2 = (left + width) - AndroidUtilities.dp(88.0f);
                    view.layout(cx2, 0, view.getMeasuredWidth() + cx2, view.getMeasuredHeight());
                }
                return true;
            }
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.albumsDidLoad) {
            if (this.adapter != null) {
                if ((this.parentAlert.baseFragment instanceof ChatActivity) || this.parentAlert.avatarPicker == 2) {
                    this.galleryAlbumEntry = MediaController.allMediaAlbumEntry;
                } else {
                    this.galleryAlbumEntry = MediaController.allPhotosAlbumEntry;
                }
                if (this.selectedAlbumEntry != null) {
                    int a = 0;
                    while (true) {
                        if (a >= MediaController.allMediaAlbums.size()) {
                            break;
                        }
                        MediaController.AlbumEntry entry = MediaController.allMediaAlbums.get(a);
                        if (entry.bucketId == this.selectedAlbumEntry.bucketId && entry.videoOnly == this.selectedAlbumEntry.videoOnly) {
                            this.selectedAlbumEntry = entry;
                            break;
                        }
                        a++;
                    }
                } else {
                    this.selectedAlbumEntry = this.galleryAlbumEntry;
                }
                this.loading = false;
                this.progressView.showTextView();
                this.adapter.notifyDataSetChanged();
                this.cameraAttachAdapter.notifyDataSetChanged();
                if (!selectedPhotosOrder.isEmpty() && this.galleryAlbumEntry != null) {
                    int N = selectedPhotosOrder.size();
                    for (int a2 = 0; a2 < N; a2++) {
                        Integer imageId = (Integer) selectedPhotosOrder.get(a2);
                        Object currentEntry = selectedPhotos.get(imageId);
                        MediaController.PhotoEntry entry2 = this.galleryAlbumEntry.photosByIds.get(imageId.intValue());
                        if (entry2 != null) {
                            if (currentEntry instanceof MediaController.PhotoEntry) {
                                entry2.copyFrom((MediaController.PhotoEntry) currentEntry);
                            }
                            selectedPhotos.put(imageId, entry2);
                        }
                    }
                }
                updateAlbumsDropDown();
            }
        } else if (id == NotificationCenter.cameraInitied) {
            checkCamera(false);
        }
    }

    private class PhotoAttachAdapter extends RecyclerListView.SelectionAdapter {
        /* access modifiers changed from: private */
        public int itemsCount;
        private Context mContext;
        /* access modifiers changed from: private */
        public boolean needCamera;
        private ArrayList<RecyclerListView.Holder> viewsCache = new ArrayList<>(8);

        public PhotoAttachAdapter(Context context, boolean camera) {
            this.mContext = context;
            this.needCamera = camera;
        }

        public void createCache() {
            for (int a = 0; a < 8; a++) {
                this.viewsCache.add(createHolder());
            }
        }

        public RecyclerListView.Holder createHolder() {
            PhotoAttachPhotoCell cell = new PhotoAttachPhotoCell(this.mContext, ChatAttachAlertPhotoLayout.this.resourcesProvider);
            if (Build.VERSION.SDK_INT >= 21 && this == ChatAttachAlertPhotoLayout.this.adapter) {
                cell.setOutlineProvider(new ViewOutlineProvider() {
                    public void getOutline(View view, Outline outline) {
                        int position = ((Integer) ((PhotoAttachPhotoCell) view).getTag()).intValue();
                        if (PhotoAttachAdapter.this.needCamera && ChatAttachAlertPhotoLayout.this.selectedAlbumEntry == ChatAttachAlertPhotoLayout.this.galleryAlbumEntry) {
                            position++;
                        }
                        if (position == 0) {
                            int rad = AndroidUtilities.dp(ChatAttachAlertPhotoLayout.this.parentAlert.cornerRadius * 8.0f);
                            outline.setRoundRect(0, 0, view.getMeasuredWidth() + rad, view.getMeasuredHeight() + rad, (float) rad);
                        } else if (position == ChatAttachAlertPhotoLayout.this.itemsPerRow - 1) {
                            int rad2 = AndroidUtilities.dp(ChatAttachAlertPhotoLayout.this.parentAlert.cornerRadius * 8.0f);
                            outline.setRoundRect(-rad2, 0, view.getMeasuredWidth(), view.getMeasuredHeight() + rad2, (float) rad2);
                        } else {
                            outline.setRect(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
                        }
                    }
                });
                cell.setClipToOutline(true);
            }
            cell.setDelegate(new ChatAttachAlertPhotoLayout$PhotoAttachAdapter$$ExternalSyntheticLambda0(this));
            return new RecyclerListView.Holder(cell);
        }

        /* renamed from: lambda$createHolder$0$org-telegram-ui-Components-ChatAttachAlertPhotoLayout$PhotoAttachAdapter  reason: not valid java name */
        public /* synthetic */ void m2189xCLASSNAMEvar_(PhotoAttachPhotoCell v) {
            TLRPC.Chat chat;
            if (ChatAttachAlertPhotoLayout.this.mediaEnabled && ChatAttachAlertPhotoLayout.this.parentAlert.avatarPicker == 0) {
                int index = ((Integer) v.getTag()).intValue();
                MediaController.PhotoEntry photoEntry = v.getPhotoEntry();
                int i = 1;
                boolean added = !ChatAttachAlertPhotoLayout.selectedPhotos.containsKey(Integer.valueOf(photoEntry.imageId));
                if (!added || ChatAttachAlertPhotoLayout.this.parentAlert.maxSelectedPhotos < 0 || ChatAttachAlertPhotoLayout.selectedPhotos.size() < ChatAttachAlertPhotoLayout.this.parentAlert.maxSelectedPhotos) {
                    int num = added ? ChatAttachAlertPhotoLayout.selectedPhotosOrder.size() : -1;
                    if (!(ChatAttachAlertPhotoLayout.this.parentAlert.baseFragment instanceof ChatActivity) || !ChatAttachAlertPhotoLayout.this.parentAlert.allowOrder) {
                        v.setChecked(-1, added, true);
                    } else {
                        v.setChecked(num, added, true);
                    }
                    int unused = ChatAttachAlertPhotoLayout.this.addToSelectedPhotos(photoEntry, index);
                    int updateIndex = index;
                    if (this == ChatAttachAlertPhotoLayout.this.cameraAttachAdapter) {
                        if (ChatAttachAlertPhotoLayout.this.adapter.needCamera && ChatAttachAlertPhotoLayout.this.selectedAlbumEntry == ChatAttachAlertPhotoLayout.this.galleryAlbumEntry) {
                            updateIndex++;
                        }
                        ChatAttachAlertPhotoLayout.this.adapter.notifyItemChanged(updateIndex);
                    } else {
                        ChatAttachAlertPhotoLayout.this.cameraAttachAdapter.notifyItemChanged(updateIndex);
                    }
                    ChatAttachAlert chatAttachAlert = ChatAttachAlertPhotoLayout.this.parentAlert;
                    if (!added) {
                        i = 2;
                    }
                    chatAttachAlert.updateCountButton(i);
                } else if (ChatAttachAlertPhotoLayout.this.parentAlert.allowOrder && (ChatAttachAlertPhotoLayout.this.parentAlert.baseFragment instanceof ChatActivity) && (chat = ((ChatActivity) ChatAttachAlertPhotoLayout.this.parentAlert.baseFragment).getCurrentChat()) != null && !ChatObject.hasAdminRights(chat) && chat.slowmode_enabled && ChatAttachAlertPhotoLayout.this.alertOnlyOnce != 2) {
                    AlertsCreator.createSimpleAlert(ChatAttachAlertPhotoLayout.this.getContext(), LocaleController.getString("Slowmode", NUM), LocaleController.getString("SlowmodeSelectSendError", NUM), ChatAttachAlertPhotoLayout.this.resourcesProvider).show();
                    if (ChatAttachAlertPhotoLayout.this.alertOnlyOnce == 1) {
                        int unused2 = ChatAttachAlertPhotoLayout.this.alertOnlyOnce = 2;
                    }
                }
            }
        }

        /* access modifiers changed from: private */
        public MediaController.PhotoEntry getPhoto(int position) {
            if (this.needCamera && ChatAttachAlertPhotoLayout.this.selectedAlbumEntry == ChatAttachAlertPhotoLayout.this.galleryAlbumEntry) {
                position--;
            }
            return ChatAttachAlertPhotoLayout.this.getPhotoEntryAtPosition(position);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            boolean z = true;
            switch (holder.getItemViewType()) {
                case 0:
                    if (this.needCamera && ChatAttachAlertPhotoLayout.this.selectedAlbumEntry == ChatAttachAlertPhotoLayout.this.galleryAlbumEntry) {
                        position--;
                    }
                    PhotoAttachPhotoCell cell = (PhotoAttachPhotoCell) holder.itemView;
                    if (this == ChatAttachAlertPhotoLayout.this.adapter) {
                        cell.setItemSize(ChatAttachAlertPhotoLayout.this.itemSize);
                    } else {
                        cell.setIsVertical(ChatAttachAlertPhotoLayout.this.cameraPhotoLayoutManager.getOrientation() == 1);
                    }
                    if (ChatAttachAlertPhotoLayout.this.parentAlert.avatarPicker != 0) {
                        cell.getCheckBox().setVisibility(8);
                    }
                    MediaController.PhotoEntry photoEntry = ChatAttachAlertPhotoLayout.this.getPhotoEntryAtPosition(position);
                    boolean z2 = this.needCamera && ChatAttachAlertPhotoLayout.this.selectedAlbumEntry == ChatAttachAlertPhotoLayout.this.galleryAlbumEntry;
                    if (position != getItemCount() - 1) {
                        z = false;
                    }
                    cell.setPhotoEntry(photoEntry, z2, z);
                    if (!(ChatAttachAlertPhotoLayout.this.parentAlert.baseFragment instanceof ChatActivity) || !ChatAttachAlertPhotoLayout.this.parentAlert.allowOrder) {
                        cell.setChecked(-1, ChatAttachAlertPhotoLayout.selectedPhotos.containsKey(Integer.valueOf(photoEntry.imageId)), false);
                    } else {
                        cell.setChecked(ChatAttachAlertPhotoLayout.selectedPhotosOrder.indexOf(Integer.valueOf(photoEntry.imageId)), ChatAttachAlertPhotoLayout.selectedPhotos.containsKey(Integer.valueOf(photoEntry.imageId)), false);
                    }
                    cell.getImageView().setTag(Integer.valueOf(position));
                    cell.setTag(Integer.valueOf(position));
                    return;
                case 1:
                    PhotoAttachCameraCell cameraCell = (PhotoAttachCameraCell) holder.itemView;
                    if (ChatAttachAlertPhotoLayout.this.cameraView == null || !ChatAttachAlertPhotoLayout.this.cameraView.isInitied() || ChatAttachAlertPhotoLayout.this.isHidden) {
                        cameraCell.setVisibility(0);
                    } else {
                        cameraCell.setVisibility(4);
                    }
                    cameraCell.setItemSize(ChatAttachAlertPhotoLayout.this.itemSize);
                    return;
                case 3:
                    PhotoAttachPermissionCell cell2 = (PhotoAttachPermissionCell) holder.itemView;
                    cell2.setItemSize(ChatAttachAlertPhotoLayout.this.itemSize);
                    if (this.needCamera && ChatAttachAlertPhotoLayout.this.noCameraPermissions && position == 0) {
                        z = false;
                    }
                    cell2.setType(z ? 1 : 0);
                    return;
                default:
                    return;
            }
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return false;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case 0:
                    if (this.viewsCache.isEmpty()) {
                        return createHolder();
                    }
                    RecyclerListView.Holder holder = this.viewsCache.get(0);
                    this.viewsCache.remove(0);
                    return holder;
                case 1:
                    PhotoAttachCameraCell cameraCell = new PhotoAttachCameraCell(this.mContext, ChatAttachAlertPhotoLayout.this.resourcesProvider);
                    if (Build.VERSION.SDK_INT >= 21) {
                        cameraCell.setOutlineProvider(new ViewOutlineProvider() {
                            public void getOutline(View view, Outline outline) {
                                int rad = AndroidUtilities.dp(ChatAttachAlertPhotoLayout.this.parentAlert.cornerRadius * 8.0f);
                                outline.setRoundRect(0, 0, view.getMeasuredWidth() + rad, view.getMeasuredHeight() + rad, (float) rad);
                            }
                        });
                        cameraCell.setClipToOutline(true);
                    }
                    return new RecyclerListView.Holder(cameraCell);
                case 2:
                    return new RecyclerListView.Holder(new View(this.mContext) {
                        /* access modifiers changed from: protected */
                        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM), View.MeasureSpec.makeMeasureSpec(ChatAttachAlertPhotoLayout.this.gridExtraSpace, NUM));
                        }
                    });
                default:
                    return new RecyclerListView.Holder(new PhotoAttachPermissionCell(this.mContext, ChatAttachAlertPhotoLayout.this.resourcesProvider));
            }
        }

        public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
            if (holder.itemView instanceof PhotoAttachCameraCell) {
                ((PhotoAttachCameraCell) holder.itemView).updateBitmap();
            }
        }

        public int getItemCount() {
            if (!ChatAttachAlertPhotoLayout.this.mediaEnabled) {
                return 1;
            }
            int count = 0;
            if (this.needCamera && ChatAttachAlertPhotoLayout.this.selectedAlbumEntry == ChatAttachAlertPhotoLayout.this.galleryAlbumEntry) {
                count = 0 + 1;
            }
            if (ChatAttachAlertPhotoLayout.this.noGalleryPermissions && this == ChatAttachAlertPhotoLayout.this.adapter) {
                count++;
            }
            int count2 = count + ChatAttachAlertPhotoLayout.cameraPhotos.size();
            if (ChatAttachAlertPhotoLayout.this.selectedAlbumEntry != null) {
                count2 += ChatAttachAlertPhotoLayout.this.selectedAlbumEntry.photos.size();
            }
            if (this == ChatAttachAlertPhotoLayout.this.adapter) {
                count2++;
            }
            this.itemsCount = count2;
            return count2;
        }

        public int getItemViewType(int position) {
            if (!ChatAttachAlertPhotoLayout.this.mediaEnabled) {
                return 2;
            }
            if (this.needCamera && position == 0 && ChatAttachAlertPhotoLayout.this.selectedAlbumEntry == ChatAttachAlertPhotoLayout.this.galleryAlbumEntry) {
                return ChatAttachAlertPhotoLayout.this.noCameraPermissions ? 3 : 1;
            }
            if (this == ChatAttachAlertPhotoLayout.this.adapter && position == this.itemsCount - 1) {
                return 2;
            }
            if (ChatAttachAlertPhotoLayout.this.noGalleryPermissions) {
                return 3;
            }
            return 0;
        }

        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            if (this == ChatAttachAlertPhotoLayout.this.adapter) {
                ChatAttachAlertPhotoLayout.this.progressView.setVisibility((!(getItemCount() == 1 && ChatAttachAlertPhotoLayout.this.selectedAlbumEntry == null) && ChatAttachAlertPhotoLayout.this.mediaEnabled) ? 4 : 0);
            }
        }
    }
}
