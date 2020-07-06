package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Property;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LayoutAnimationController;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Keep;
import androidx.exifinterface.media.ExifInterface;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
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
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.messenger.camera.CameraController;
import org.telegram.messenger.camera.CameraSession;
import org.telegram.messenger.camera.CameraView;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$FileLocation;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.PhotoAttachCameraCell;
import org.telegram.ui.Cells.PhotoAttachPermissionCell;
import org.telegram.ui.Cells.PhotoAttachPhotoCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.ChatAttachAlert;
import org.telegram.ui.Components.ChatAttachAlertPhotoLayout;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerViewItemRangeSelector;
import org.telegram.ui.Components.ShutterButton;
import org.telegram.ui.Components.ZoomControlView;
import org.telegram.ui.PhotoViewer;

public class ChatAttachAlertPhotoLayout extends ChatAttachAlert.AttachAlertLayout implements NotificationCenter.NotificationCenterDelegate {
    /* access modifiers changed from: private */
    public static ArrayList<Object> cameraPhotos = new ArrayList<>();
    private static int lastImageId = -1;
    /* access modifiers changed from: private */
    public static boolean mediaFromExternalCamera;
    /* access modifiers changed from: private */
    public static HashMap<Object, Object> selectedPhotos = new HashMap<>();
    /* access modifiers changed from: private */
    public static ArrayList<Object> selectedPhotosOrder = new ArrayList<>();
    /* access modifiers changed from: private */
    public PhotoAttachAdapter adapter;
    /* access modifiers changed from: private */
    public int alertOnlyOnce;
    private int[] animateCameraValues = new int[5];
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
    public TextView counterTextView;
    private int currentSelectedCount;
    private boolean deviceHasGoodCamera;
    private boolean dragging;
    private TextView dropDown;
    private ArrayList<MediaController.AlbumEntry> dropDownAlbums;
    private ActionBarMenuItem dropDownContainer;
    private Drawable dropDownDrawable;
    /* access modifiers changed from: private */
    public boolean flashAnimationInProgress;
    /* access modifiers changed from: private */
    public ImageView[] flashModeButton = new ImageView[2];
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

    /* access modifiers changed from: package-private */
    public int needsActionBar() {
        return 1;
    }

    static /* synthetic */ int access$2508(ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout) {
        int i = chatAttachAlertPhotoLayout.videoRecordTime;
        chatAttachAlertPhotoLayout.videoRecordTime = i + 1;
        return i;
    }

    static /* synthetic */ int access$3110() {
        int i = lastImageId;
        lastImageId = i - 1;
        return i;
    }

    private class BasePhotoProvider extends PhotoViewer.EmptyPhotoViewerProvider {
        private BasePhotoProvider() {
        }

        public boolean isPhotoChecked(int i) {
            MediaController.PhotoEntry access$000 = ChatAttachAlertPhotoLayout.this.getPhotoEntryAtPosition(i);
            return access$000 != null && ChatAttachAlertPhotoLayout.selectedPhotos.containsKey(Integer.valueOf(access$000.imageId));
        }

        public int setPhotoChecked(int i, VideoEditedInfo videoEditedInfo) {
            MediaController.PhotoEntry access$000;
            boolean z;
            if ((ChatAttachAlertPhotoLayout.this.parentAlert.maxSelectedPhotos >= 0 && ChatAttachAlertPhotoLayout.selectedPhotos.size() >= ChatAttachAlertPhotoLayout.this.parentAlert.maxSelectedPhotos && !isPhotoChecked(i)) || (access$000 = ChatAttachAlertPhotoLayout.this.getPhotoEntryAtPosition(i)) == null) {
                return -1;
            }
            int access$200 = ChatAttachAlertPhotoLayout.this.addToSelectedPhotos(access$000, -1);
            int i2 = 1;
            if (access$200 == -1) {
                access$200 = ChatAttachAlertPhotoLayout.selectedPhotosOrder.indexOf(Integer.valueOf(access$000.imageId));
                z = true;
            } else {
                access$000.editedInfo = null;
                z = false;
            }
            access$000.editedInfo = videoEditedInfo;
            int childCount = ChatAttachAlertPhotoLayout.this.gridView.getChildCount();
            int i3 = 0;
            while (true) {
                if (i3 >= childCount) {
                    break;
                }
                View childAt = ChatAttachAlertPhotoLayout.this.gridView.getChildAt(i3);
                if (!(childAt instanceof PhotoAttachPhotoCell) || ((Integer) childAt.getTag()).intValue() != i) {
                    i3++;
                } else {
                    ChatAttachAlert chatAttachAlert = ChatAttachAlertPhotoLayout.this.parentAlert;
                    if (!(chatAttachAlert.baseFragment instanceof ChatActivity) || !chatAttachAlert.allowOrder) {
                        ((PhotoAttachPhotoCell) childAt).setChecked(-1, z, false);
                    } else {
                        ((PhotoAttachPhotoCell) childAt).setChecked(access$200, z, false);
                    }
                }
            }
            int childCount2 = ChatAttachAlertPhotoLayout.this.cameraPhotoRecyclerView.getChildCount();
            int i4 = 0;
            while (true) {
                if (i4 >= childCount2) {
                    break;
                }
                View childAt2 = ChatAttachAlertPhotoLayout.this.cameraPhotoRecyclerView.getChildAt(i4);
                if (!(childAt2 instanceof PhotoAttachPhotoCell) || ((Integer) childAt2.getTag()).intValue() != i) {
                    i4++;
                } else {
                    ChatAttachAlert chatAttachAlert2 = ChatAttachAlertPhotoLayout.this.parentAlert;
                    if (!(chatAttachAlert2.baseFragment instanceof ChatActivity) || !chatAttachAlert2.allowOrder) {
                        ((PhotoAttachPhotoCell) childAt2).setChecked(-1, z, false);
                    } else {
                        ((PhotoAttachPhotoCell) childAt2).setChecked(access$200, z, false);
                    }
                }
            }
            ChatAttachAlert chatAttachAlert3 = ChatAttachAlertPhotoLayout.this.parentAlert;
            if (!z) {
                i2 = 2;
            }
            chatAttachAlert3.updateCountButton(i2);
            return access$200;
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
    }

    private void updateCheckedPhotoIndices() {
        if (this.parentAlert.baseFragment instanceof ChatActivity) {
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

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public ChatAttachAlertPhotoLayout(ChatAttachAlert chatAttachAlert, Context context) {
        super(chatAttachAlert, context);
        Context context2 = context;
        int dp = AndroidUtilities.dp(80.0f);
        this.itemSize = dp;
        this.lastItemSize = dp;
        this.itemsPerRow = 3;
        this.loading = true;
        this.photoViewerProvider = new BasePhotoProvider() {
            public boolean cancelButtonPressed() {
                return false;
            }

            public PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, TLRPC$FileLocation tLRPC$FileLocation, int i, boolean z) {
                PhotoAttachPhotoCell access$700 = ChatAttachAlertPhotoLayout.this.getCellForIndex(i);
                if (access$700 == null) {
                    return null;
                }
                int[] iArr = new int[2];
                access$700.getImageView().getLocationInWindow(iArr);
                if (Build.VERSION.SDK_INT < 26) {
                    iArr[0] = iArr[0] - ChatAttachAlertPhotoLayout.this.parentAlert.getLeftInset();
                }
                PhotoViewer.PlaceProviderObject placeProviderObject = new PhotoViewer.PlaceProviderObject();
                placeProviderObject.viewX = iArr[0];
                placeProviderObject.viewY = iArr[1];
                placeProviderObject.parentView = ChatAttachAlertPhotoLayout.this.gridView;
                ImageReceiver imageReceiver = access$700.getImageView().getImageReceiver();
                placeProviderObject.imageReceiver = imageReceiver;
                placeProviderObject.thumb = imageReceiver.getBitmapSafe();
                placeProviderObject.scale = access$700.getScale();
                access$700.showCheck(false);
                return placeProviderObject;
            }

            public void updatePhotoAtIndex(int i) {
                PhotoAttachPhotoCell access$700 = ChatAttachAlertPhotoLayout.this.getCellForIndex(i);
                if (access$700 != null) {
                    access$700.getImageView().setOrientation(0, true);
                    MediaController.PhotoEntry access$000 = ChatAttachAlertPhotoLayout.this.getPhotoEntryAtPosition(i);
                    if (access$000 != null) {
                        if (access$000.thumbPath != null) {
                            access$700.getImageView().setImage(access$000.thumbPath, (String) null, Theme.chat_attachEmptyDrawable);
                        } else if (access$000.path != null) {
                            access$700.getImageView().setOrientation(access$000.orientation, true);
                            if (access$000.isVideo) {
                                BackupImageView imageView = access$700.getImageView();
                                imageView.setImage("vthumb://" + access$000.imageId + ":" + access$000.path, (String) null, Theme.chat_attachEmptyDrawable);
                                return;
                            }
                            BackupImageView imageView2 = access$700.getImageView();
                            imageView2.setImage("thumb://" + access$000.imageId + ":" + access$000.path, (String) null, Theme.chat_attachEmptyDrawable);
                        } else {
                            access$700.getImageView().setImageDrawable(Theme.chat_attachEmptyDrawable);
                        }
                    }
                }
            }

            public ImageReceiver.BitmapHolder getThumbForPhoto(MessageObject messageObject, TLRPC$FileLocation tLRPC$FileLocation, int i) {
                PhotoAttachPhotoCell access$700 = ChatAttachAlertPhotoLayout.this.getCellForIndex(i);
                if (access$700 != null) {
                    return access$700.getImageView().getImageReceiver().getBitmapSafe();
                }
                return null;
            }

            public void willSwitchFromPhoto(MessageObject messageObject, TLRPC$FileLocation tLRPC$FileLocation, int i) {
                PhotoAttachPhotoCell access$700 = ChatAttachAlertPhotoLayout.this.getCellForIndex(i);
                if (access$700 != null) {
                    access$700.showCheck(true);
                }
            }

            public void willHidePhotoViewer() {
                int childCount = ChatAttachAlertPhotoLayout.this.gridView.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View childAt = ChatAttachAlertPhotoLayout.this.gridView.getChildAt(i);
                    if (childAt instanceof PhotoAttachPhotoCell) {
                        ((PhotoAttachPhotoCell) childAt).showCheck(true);
                    }
                }
            }

            public void sendButtonPressed(int i, VideoEditedInfo videoEditedInfo, boolean z, int i2) {
                MediaController.PhotoEntry access$000 = ChatAttachAlertPhotoLayout.this.getPhotoEntryAtPosition(i);
                if (access$000 != null) {
                    access$000.editedInfo = videoEditedInfo;
                }
                if (ChatAttachAlertPhotoLayout.selectedPhotos.isEmpty() && access$000 != null) {
                    int unused = ChatAttachAlertPhotoLayout.this.addToSelectedPhotos(access$000, -1);
                }
                ChatAttachAlertPhotoLayout.this.parentAlert.applyCaption();
                ChatAttachAlertPhotoLayout.this.parentAlert.delegate.didPressedButton(7, true, z, i2);
            }
        };
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.albumsDidLoad);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.cameraInitied);
        final FrameLayout container = chatAttachAlert.getContainer();
        this.cameraDrawable = context.getResources().getDrawable(NUM).mutate();
        ActionBarMenuItem actionBarMenuItem = new ActionBarMenuItem(context2, this.parentAlert.actionBar.createMenu(), 0, 0);
        this.dropDownContainer = actionBarMenuItem;
        actionBarMenuItem.setSubMenuOpenSide(1);
        this.parentAlert.actionBar.addView(this.dropDownContainer, 0, LayoutHelper.createFrame(-2, -1.0f, 51, AndroidUtilities.isTablet() ? 64.0f : 56.0f, 0.0f, 40.0f, 0.0f));
        this.dropDownContainer.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ChatAttachAlertPhotoLayout.this.lambda$new$0$ChatAttachAlertPhotoLayout(view);
            }
        });
        TextView textView = new TextView(context2);
        this.dropDown = textView;
        textView.setGravity(3);
        this.dropDown.setSingleLine(true);
        this.dropDown.setLines(1);
        this.dropDown.setMaxLines(1);
        this.dropDown.setEllipsize(TextUtils.TruncateAt.END);
        this.dropDown.setTextColor(Theme.getColor("dialogTextBlack"));
        this.dropDown.setText(LocaleController.getString("ChatGallery", NUM));
        this.dropDown.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        Drawable mutate = context.getResources().getDrawable(NUM).mutate();
        this.dropDownDrawable = mutate;
        mutate.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogTextBlack"), PorterDuff.Mode.MULTIPLY));
        this.dropDown.setCompoundDrawablePadding(AndroidUtilities.dp(4.0f));
        this.dropDown.setPadding(0, 0, AndroidUtilities.dp(10.0f), 0);
        this.dropDownContainer.addView(this.dropDown, LayoutHelper.createFrame(-2, -2.0f, 16, 16.0f, 0.0f, 0.0f, 0.0f));
        checkCamera(false);
        this.parentAlert.selectedMenuItem.addSubItem(0, LocaleController.getString("SendWithoutGrouping", NUM));
        this.parentAlert.selectedMenuItem.addSubItem(1, LocaleController.getString("SendWithoutCompression", NUM));
        this.parentAlert.selectedMenuItem.addSubItem(2, NUM, LocaleController.getString("OpenInExternalApp", NUM));
        AnonymousClass2 r4 = new RecyclerListView(context2) {
            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (motionEvent.getAction() != 0 || motionEvent.getY() >= ((float) (ChatAttachAlertPhotoLayout.this.parentAlert.scrollOffsetY[0] - AndroidUtilities.dp(80.0f)))) {
                    return super.onTouchEvent(motionEvent);
                }
                return false;
            }

            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                if (motionEvent.getAction() != 0 || motionEvent.getY() >= ((float) (ChatAttachAlertPhotoLayout.this.parentAlert.scrollOffsetY[0] - AndroidUtilities.dp(80.0f)))) {
                    return super.onInterceptTouchEvent(motionEvent);
                }
                return false;
            }
        };
        this.gridView = r4;
        PhotoAttachAdapter photoAttachAdapter = new PhotoAttachAdapter(context2, true);
        this.adapter = photoAttachAdapter;
        r4.setAdapter(photoAttachAdapter);
        this.adapter.createCache();
        this.gridView.setClipToPadding(false);
        this.gridView.setItemAnimator((RecyclerView.ItemAnimator) null);
        this.gridView.setLayoutAnimation((LayoutAnimationController) null);
        this.gridView.setVerticalScrollBarEnabled(false);
        this.gridView.setGlowColor(Theme.getColor("dialogScrollGlow"));
        addView(this.gridView, LayoutHelper.createFrame(-1, -1.0f));
        this.gridView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                if (ChatAttachAlertPhotoLayout.this.gridView.getChildCount() > 0) {
                    ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout = ChatAttachAlertPhotoLayout.this;
                    chatAttachAlertPhotoLayout.parentAlert.updateLayout(chatAttachAlertPhotoLayout, true);
                    ChatAttachAlertPhotoLayout.this.checkCameraViewPosition();
                }
            }

            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                RecyclerListView.Holder holder;
                if (i == 0) {
                    int dp = AndroidUtilities.dp(13.0f);
                    ActionBarMenuItem actionBarMenuItem = ChatAttachAlertPhotoLayout.this.parentAlert.selectedMenuItem;
                    int dp2 = dp + (actionBarMenuItem != null ? AndroidUtilities.dp(actionBarMenuItem.getAlpha() * 26.0f) : 0);
                    int backgroundPaddingTop = ChatAttachAlertPhotoLayout.this.parentAlert.getBackgroundPaddingTop();
                    if (((ChatAttachAlertPhotoLayout.this.parentAlert.scrollOffsetY[0] - backgroundPaddingTop) - dp2) + backgroundPaddingTop < ActionBar.getCurrentActionBarHeight() && (holder = (RecyclerListView.Holder) ChatAttachAlertPhotoLayout.this.gridView.findViewHolderForAdapterPosition(0)) != null && holder.itemView.getTop() > AndroidUtilities.dp(7.0f)) {
                        ChatAttachAlertPhotoLayout.this.gridView.smoothScrollBy(0, holder.itemView.getTop() - AndroidUtilities.dp(7.0f));
                    }
                }
            }
        });
        AnonymousClass4 r42 = new GridLayoutManager(context2, this.itemSize) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }

            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int i) {
                AnonymousClass1 r2 = new LinearSmoothScroller(recyclerView.getContext()) {
                    public int calculateDyToMakeVisible(View view, int i) {
                        return super.calculateDyToMakeVisible(view, i) - (ChatAttachAlertPhotoLayout.this.gridView.getPaddingTop() - AndroidUtilities.dp(7.0f));
                    }

                    /* access modifiers changed from: protected */
                    public int calculateTimeForDeceleration(int i) {
                        return super.calculateTimeForDeceleration(i) * 2;
                    }
                };
                r2.setTargetPosition(i);
                startSmoothScroll(r2);
            }
        };
        this.layoutManager = r42;
        r42.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            public int getSpanSize(int i) {
                if (i == ChatAttachAlertPhotoLayout.this.adapter.itemsCount - 1) {
                    return ChatAttachAlertPhotoLayout.this.layoutManager.getSpanCount();
                }
                return ChatAttachAlertPhotoLayout.this.itemSize + (i % ChatAttachAlertPhotoLayout.this.itemsPerRow != ChatAttachAlertPhotoLayout.this.itemsPerRow + -1 ? AndroidUtilities.dp(5.0f) : 0);
            }
        });
        this.gridView.setLayoutManager(this.layoutManager);
        this.gridView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                ChatAttachAlertPhotoLayout.this.lambda$new$1$ChatAttachAlertPhotoLayout(view, i);
            }
        });
        this.gridView.setOnItemLongClickListener((RecyclerListView.OnItemLongClickListener) new RecyclerListView.OnItemLongClickListener() {
            public final boolean onItemClick(View view, int i) {
                return ChatAttachAlertPhotoLayout.this.lambda$new$2$ChatAttachAlertPhotoLayout(view, i);
            }
        });
        RecyclerViewItemRangeSelector recyclerViewItemRangeSelector = new RecyclerViewItemRangeSelector(new RecyclerViewItemRangeSelector.RecyclerViewItemRangeSelectorDelegate() {
            public void setSelected(View view, int i, boolean z) {
                if (z == ChatAttachAlertPhotoLayout.this.shouldSelect && (view instanceof PhotoAttachPhotoCell)) {
                    ((PhotoAttachPhotoCell) view).callDelegate();
                }
            }

            public boolean isSelected(int i) {
                MediaController.PhotoEntry access$1500 = ChatAttachAlertPhotoLayout.this.adapter.getPhoto(i);
                return access$1500 != null && ChatAttachAlertPhotoLayout.selectedPhotos.containsKey(Integer.valueOf(access$1500.imageId));
            }

            public boolean isIndexSelectable(int i) {
                return ChatAttachAlertPhotoLayout.this.adapter.getItemViewType(i) == 0;
            }

            public void onStartStopSelection(boolean z) {
                int unused = ChatAttachAlertPhotoLayout.this.alertOnlyOnce = z ? 1 : 0;
                ChatAttachAlertPhotoLayout.this.gridView.hideSelector(true);
            }
        });
        this.itemRangeSelector = recyclerViewItemRangeSelector;
        this.gridView.addOnItemTouchListener(recyclerViewItemRangeSelector);
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context2);
        this.progressView = emptyTextProgressView;
        emptyTextProgressView.setText(LocaleController.getString("NoPhotos", NUM));
        this.progressView.setOnTouchListener((View.OnTouchListener) null);
        this.progressView.setTextSize(20);
        addView(this.progressView, LayoutHelper.createFrame(-1, 80.0f));
        if (this.loading) {
            this.progressView.showProgress();
        } else {
            this.progressView.showTextView();
        }
        TextView textView2 = new TextView(context2);
        this.recordTime = textView2;
        textView2.setBackgroundResource(NUM);
        this.recordTime.getBackground().setColorFilter(new PorterDuffColorFilter(NUM, PorterDuff.Mode.MULTIPLY));
        this.recordTime.setTextSize(1, 15.0f);
        this.recordTime.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.recordTime.setAlpha(0.0f);
        this.recordTime.setTextColor(-1);
        this.recordTime.setPadding(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(5.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(5.0f));
        container.addView(this.recordTime, LayoutHelper.createFrame(-2, -2.0f, 49, 0.0f, 16.0f, 0.0f, 0.0f));
        AnonymousClass7 r3 = new FrameLayout(context2) {
            /* access modifiers changed from: protected */
            public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                int i5;
                int i6;
                int i7;
                int i8;
                int i9;
                int i10;
                if (getMeasuredWidth() == AndroidUtilities.dp(126.0f)) {
                    i7 = getMeasuredWidth() / 2;
                    i6 = getMeasuredHeight() / 2;
                    i10 = getMeasuredWidth() / 2;
                    int i11 = i6 / 2;
                    i8 = i6 + i11 + AndroidUtilities.dp(17.0f);
                    i9 = i11 - AndroidUtilities.dp(17.0f);
                    i5 = i10;
                } else {
                    i7 = getMeasuredWidth() / 2;
                    i6 = (getMeasuredHeight() / 2) - AndroidUtilities.dp(13.0f);
                    int i12 = i7 / 2;
                    i5 = i12 - AndroidUtilities.dp(17.0f);
                    i9 = (getMeasuredHeight() / 2) - AndroidUtilities.dp(13.0f);
                    i10 = i7 + i12 + AndroidUtilities.dp(17.0f);
                    i8 = i9;
                }
                int measuredHeight = (getMeasuredHeight() - ChatAttachAlertPhotoLayout.this.tooltipTextView.getMeasuredHeight()) - AndroidUtilities.dp(12.0f);
                if (getMeasuredWidth() == AndroidUtilities.dp(126.0f)) {
                    ChatAttachAlertPhotoLayout.this.tooltipTextView.layout(i7 - (ChatAttachAlertPhotoLayout.this.tooltipTextView.getMeasuredWidth() / 2), getMeasuredHeight(), (ChatAttachAlertPhotoLayout.this.tooltipTextView.getMeasuredWidth() / 2) + i7, getMeasuredHeight() + ChatAttachAlertPhotoLayout.this.tooltipTextView.getMeasuredHeight());
                } else {
                    ChatAttachAlertPhotoLayout.this.tooltipTextView.layout(i7 - (ChatAttachAlertPhotoLayout.this.tooltipTextView.getMeasuredWidth() / 2), measuredHeight, (ChatAttachAlertPhotoLayout.this.tooltipTextView.getMeasuredWidth() / 2) + i7, ChatAttachAlertPhotoLayout.this.tooltipTextView.getMeasuredHeight() + measuredHeight);
                }
                ChatAttachAlertPhotoLayout.this.shutterButton.layout(i7 - (ChatAttachAlertPhotoLayout.this.shutterButton.getMeasuredWidth() / 2), i6 - (ChatAttachAlertPhotoLayout.this.shutterButton.getMeasuredHeight() / 2), i7 + (ChatAttachAlertPhotoLayout.this.shutterButton.getMeasuredWidth() / 2), i6 + (ChatAttachAlertPhotoLayout.this.shutterButton.getMeasuredHeight() / 2));
                ChatAttachAlertPhotoLayout.this.switchCameraButton.layout(i10 - (ChatAttachAlertPhotoLayout.this.switchCameraButton.getMeasuredWidth() / 2), i8 - (ChatAttachAlertPhotoLayout.this.switchCameraButton.getMeasuredHeight() / 2), i10 + (ChatAttachAlertPhotoLayout.this.switchCameraButton.getMeasuredWidth() / 2), i8 + (ChatAttachAlertPhotoLayout.this.switchCameraButton.getMeasuredHeight() / 2));
                for (int i13 = 0; i13 < 2; i13++) {
                    ChatAttachAlertPhotoLayout.this.flashModeButton[i13].layout(i5 - (ChatAttachAlertPhotoLayout.this.flashModeButton[i13].getMeasuredWidth() / 2), i9 - (ChatAttachAlertPhotoLayout.this.flashModeButton[i13].getMeasuredHeight() / 2), (ChatAttachAlertPhotoLayout.this.flashModeButton[i13].getMeasuredWidth() / 2) + i5, (ChatAttachAlertPhotoLayout.this.flashModeButton[i13].getMeasuredHeight() / 2) + i9);
                }
            }
        };
        this.cameraPanel = r3;
        r3.setVisibility(8);
        this.cameraPanel.setAlpha(0.0f);
        container.addView(this.cameraPanel, LayoutHelper.createFrame(-1, 126, 83));
        TextView textView3 = new TextView(context2);
        this.counterTextView = textView3;
        textView3.setBackgroundResource(NUM);
        this.counterTextView.setVisibility(8);
        this.counterTextView.setTextColor(-1);
        this.counterTextView.setGravity(17);
        this.counterTextView.setPivotX(0.0f);
        this.counterTextView.setPivotY(0.0f);
        this.counterTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.counterTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, NUM, 0);
        this.counterTextView.setCompoundDrawablePadding(AndroidUtilities.dp(4.0f));
        this.counterTextView.setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f), 0);
        container.addView(this.counterTextView, LayoutHelper.createFrame(-2, 38.0f, 51, 0.0f, 0.0f, 0.0f, 116.0f));
        this.counterTextView.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ChatAttachAlertPhotoLayout.this.lambda$new$3$ChatAttachAlertPhotoLayout(view);
            }
        });
        ZoomControlView zoomControlView2 = new ZoomControlView(context2);
        this.zoomControlView = zoomControlView2;
        zoomControlView2.setVisibility(8);
        this.zoomControlView.setAlpha(0.0f);
        container.addView(this.zoomControlView, LayoutHelper.createFrame(-2, 50.0f, 51, 0.0f, 0.0f, 0.0f, 116.0f));
        this.zoomControlView.setDelegate(new ZoomControlView.ZoomControlViewDelegate() {
            public final void didSetZoom(float f) {
                ChatAttachAlertPhotoLayout.this.lambda$new$4$ChatAttachAlertPhotoLayout(f);
            }
        });
        ShutterButton shutterButton2 = new ShutterButton(context2);
        this.shutterButton = shutterButton2;
        this.cameraPanel.addView(shutterButton2, LayoutHelper.createFrame(84, 84, 17));
        this.shutterButton.setDelegate(new ShutterButton.ShutterButtonDelegate() {
            private File outputFile;
            private boolean zoomingWas;

            public boolean shutterLongPressed() {
                BaseFragment baseFragment;
                ChatAttachAlert chatAttachAlert = ChatAttachAlertPhotoLayout.this.parentAlert;
                if ((chatAttachAlert.avatarPicker != 2 && !(chatAttachAlert.baseFragment instanceof ChatActivity)) || ChatAttachAlertPhotoLayout.this.takingPhoto || (baseFragment = ChatAttachAlertPhotoLayout.this.parentAlert.baseFragment) == null || baseFragment.getParentActivity() == null || ChatAttachAlertPhotoLayout.this.cameraView == null) {
                    return false;
                }
                if (Build.VERSION.SDK_INT < 23 || ChatAttachAlertPhotoLayout.this.parentAlert.baseFragment.getParentActivity().checkSelfPermission("android.permission.RECORD_AUDIO") == 0) {
                    for (int i = 0; i < 2; i++) {
                        ChatAttachAlertPhotoLayout.this.flashModeButton[i].setAlpha(0.0f);
                    }
                    ChatAttachAlertPhotoLayout.this.switchCameraButton.setAlpha(0.0f);
                    ChatAttachAlertPhotoLayout.this.tooltipTextView.setAlpha(0.0f);
                    BaseFragment baseFragment2 = ChatAttachAlertPhotoLayout.this.parentAlert.baseFragment;
                    this.outputFile = AndroidUtilities.generateVideoPath((baseFragment2 instanceof ChatActivity) && ((ChatActivity) baseFragment2).isSecretChat());
                    ChatAttachAlertPhotoLayout.this.recordTime.setAlpha(1.0f);
                    ChatAttachAlertPhotoLayout.this.recordTime.setText(AndroidUtilities.formatLongDuration(0));
                    int unused = ChatAttachAlertPhotoLayout.this.videoRecordTime = 0;
                    Runnable unused2 = ChatAttachAlertPhotoLayout.this.videoRecordRunnable = new Runnable() {
                        public final void run() {
                            ChatAttachAlertPhotoLayout.AnonymousClass8.this.lambda$shutterLongPressed$0$ChatAttachAlertPhotoLayout$8();
                        }
                    };
                    AndroidUtilities.lockOrientation(ChatAttachAlertPhotoLayout.this.parentAlert.baseFragment.getParentActivity());
                    CameraController.getInstance().recordVideo(ChatAttachAlertPhotoLayout.this.cameraView.getCameraSession(), this.outputFile, new CameraController.VideoTakeCallback() {
                        public final void onFinishVideoRecording(String str, long j) {
                            ChatAttachAlertPhotoLayout.AnonymousClass8.this.lambda$shutterLongPressed$1$ChatAttachAlertPhotoLayout$8(str, j);
                        }
                    }, new Runnable() {
                        public final void run() {
                            ChatAttachAlertPhotoLayout.AnonymousClass8.this.lambda$shutterLongPressed$2$ChatAttachAlertPhotoLayout$8();
                        }
                    });
                    ChatAttachAlertPhotoLayout.this.shutterButton.setState(ShutterButton.State.RECORDING, true);
                    return true;
                }
                boolean unused3 = ChatAttachAlertPhotoLayout.this.requestingPermissions = true;
                ChatAttachAlertPhotoLayout.this.parentAlert.baseFragment.getParentActivity().requestPermissions(new String[]{"android.permission.RECORD_AUDIO"}, 21);
                return false;
            }

            public /* synthetic */ void lambda$shutterLongPressed$0$ChatAttachAlertPhotoLayout$8() {
                if (ChatAttachAlertPhotoLayout.this.videoRecordRunnable != null) {
                    ChatAttachAlertPhotoLayout.access$2508(ChatAttachAlertPhotoLayout.this);
                    ChatAttachAlertPhotoLayout.this.recordTime.setText(AndroidUtilities.formatLongDuration(ChatAttachAlertPhotoLayout.this.videoRecordTime));
                    AndroidUtilities.runOnUIThread(ChatAttachAlertPhotoLayout.this.videoRecordRunnable, 1000);
                }
            }

            public /* synthetic */ void lambda$shutterLongPressed$1$ChatAttachAlertPhotoLayout$8(String str, long j) {
                if (this.outputFile != null && ChatAttachAlertPhotoLayout.this.parentAlert.baseFragment != null) {
                    boolean unused = ChatAttachAlertPhotoLayout.mediaFromExternalCamera = false;
                    MediaController.PhotoEntry photoEntry = new MediaController.PhotoEntry(0, ChatAttachAlertPhotoLayout.access$3110(), 0, this.outputFile.getAbsolutePath(), 0, true, 0, 0, 0);
                    photoEntry.duration = (int) j;
                    photoEntry.thumbPath = str;
                    ChatAttachAlertPhotoLayout.this.openPhotoViewer(photoEntry, false, false);
                }
            }

            public /* synthetic */ void lambda$shutterLongPressed$2$ChatAttachAlertPhotoLayout$8() {
                AndroidUtilities.runOnUIThread(ChatAttachAlertPhotoLayout.this.videoRecordRunnable, 1000);
            }

            public void shutterCancel() {
                File file = this.outputFile;
                if (file != null) {
                    file.delete();
                    this.outputFile = null;
                }
                ChatAttachAlertPhotoLayout.this.resetRecordState();
                CameraController.getInstance().stopVideoRecording(ChatAttachAlertPhotoLayout.this.cameraView.getCameraSession(), true);
            }

            public void shutterReleased() {
                if (!ChatAttachAlertPhotoLayout.this.takingPhoto && ChatAttachAlertPhotoLayout.this.cameraView != null && ChatAttachAlertPhotoLayout.this.cameraView.getCameraSession() != null) {
                    boolean z = true;
                    if (ChatAttachAlertPhotoLayout.this.shutterButton.getState() == ShutterButton.State.RECORDING) {
                        ChatAttachAlertPhotoLayout.this.resetRecordState();
                        CameraController.getInstance().stopVideoRecording(ChatAttachAlertPhotoLayout.this.cameraView.getCameraSession(), false);
                        ChatAttachAlertPhotoLayout.this.shutterButton.setState(ShutterButton.State.DEFAULT, true);
                        return;
                    }
                    BaseFragment baseFragment = ChatAttachAlertPhotoLayout.this.parentAlert.baseFragment;
                    File generatePicturePath = AndroidUtilities.generatePicturePath((baseFragment instanceof ChatActivity) && ((ChatActivity) baseFragment).isSecretChat(), (String) null);
                    boolean isSameTakePictureOrientation = ChatAttachAlertPhotoLayout.this.cameraView.getCameraSession().isSameTakePictureOrientation();
                    CameraSession cameraSession = ChatAttachAlertPhotoLayout.this.cameraView.getCameraSession();
                    ChatAttachAlert chatAttachAlert = ChatAttachAlertPhotoLayout.this.parentAlert;
                    if (!(chatAttachAlert.baseFragment instanceof ChatActivity) && chatAttachAlert.avatarPicker != 2) {
                        z = false;
                    }
                    cameraSession.setFlipFront(z);
                    boolean unused = ChatAttachAlertPhotoLayout.this.takingPhoto = CameraController.getInstance().takePicture(generatePicturePath, ChatAttachAlertPhotoLayout.this.cameraView.getCameraSession(), new Runnable(generatePicturePath, isSameTakePictureOrientation) {
                        public final /* synthetic */ File f$1;
                        public final /* synthetic */ boolean f$2;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                        }

                        public final void run() {
                            ChatAttachAlertPhotoLayout.AnonymousClass8.this.lambda$shutterReleased$3$ChatAttachAlertPhotoLayout$8(this.f$1, this.f$2);
                        }
                    });
                }
            }

            public /* synthetic */ void lambda$shutterReleased$3$ChatAttachAlertPhotoLayout$8(File file, boolean z) {
                int i;
                boolean unused = ChatAttachAlertPhotoLayout.this.takingPhoto = false;
                if (file != null && ChatAttachAlertPhotoLayout.this.parentAlert.baseFragment != null) {
                    try {
                        int attributeInt = new ExifInterface(file.getAbsolutePath()).getAttributeInt("Orientation", 1);
                        i = attributeInt != 3 ? attributeInt != 6 ? attributeInt != 8 ? 0 : 270 : 90 : 180;
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                        i = 0;
                    }
                    boolean unused2 = ChatAttachAlertPhotoLayout.mediaFromExternalCamera = false;
                    MediaController.PhotoEntry photoEntry = new MediaController.PhotoEntry(0, ChatAttachAlertPhotoLayout.access$3110(), 0, file.getAbsolutePath(), i, false, 0, 0, 0);
                    photoEntry.canDeleteAfter = true;
                    ChatAttachAlertPhotoLayout.this.openPhotoViewer(photoEntry, z, false);
                }
            }

            public boolean onTranslationChanged(float f, float f2) {
                boolean z = container.getWidth() < container.getHeight();
                float f3 = z ? f : f2;
                float f4 = z ? f2 : f;
                if (this.zoomingWas || Math.abs(f3) <= Math.abs(f4)) {
                    if (f4 < 0.0f) {
                        ChatAttachAlertPhotoLayout.this.showZoomControls(true, true);
                        ChatAttachAlertPhotoLayout.this.zoomControlView.setZoom((-f4) / ((float) AndroidUtilities.dp(200.0f)), true);
                        this.zoomingWas = true;
                        return false;
                    }
                    if (this.zoomingWas) {
                        ChatAttachAlertPhotoLayout.this.zoomControlView.setZoom(0.0f, true);
                    }
                    if (f == 0.0f && f2 == 0.0f) {
                        this.zoomingWas = false;
                    }
                    if (this.zoomingWas) {
                        return false;
                    }
                    if (f == 0.0f && f2 == 0.0f) {
                        return false;
                    }
                    return true;
                } else if (ChatAttachAlertPhotoLayout.this.zoomControlView.getTag() == null) {
                    return true;
                } else {
                    return false;
                }
            }
        });
        this.shutterButton.setFocusable(true);
        this.shutterButton.setContentDescription(LocaleController.getString("AccDescrShutter", NUM));
        ImageView imageView = new ImageView(context2);
        this.switchCameraButton = imageView;
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        this.cameraPanel.addView(this.switchCameraButton, LayoutHelper.createFrame(48, 48, 21));
        this.switchCameraButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ChatAttachAlertPhotoLayout.this.lambda$new$5$ChatAttachAlertPhotoLayout(view);
            }
        });
        this.switchCameraButton.setContentDescription(LocaleController.getString("AccDescrSwitchCamera", NUM));
        for (int i = 0; i < 2; i++) {
            this.flashModeButton[i] = new ImageView(context2);
            this.flashModeButton[i].setScaleType(ImageView.ScaleType.CENTER);
            this.flashModeButton[i].setVisibility(4);
            this.cameraPanel.addView(this.flashModeButton[i], LayoutHelper.createFrame(48, 48, 51));
            this.flashModeButton[i].setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    ChatAttachAlertPhotoLayout.this.lambda$new$6$ChatAttachAlertPhotoLayout(view);
                }
            });
            ImageView imageView2 = this.flashModeButton[i];
            imageView2.setContentDescription("flash mode " + i);
        }
        TextView textView4 = new TextView(context2);
        this.tooltipTextView = textView4;
        textView4.setTextSize(1, 15.0f);
        this.tooltipTextView.setTextColor(-1);
        this.tooltipTextView.setText(LocaleController.getString("TapForVideo", NUM));
        this.tooltipTextView.setShadowLayer((float) AndroidUtilities.dp(3.33333f), 0.0f, (float) AndroidUtilities.dp(0.666f), NUM);
        this.tooltipTextView.setPadding(AndroidUtilities.dp(6.0f), 0, AndroidUtilities.dp(6.0f), 0);
        this.cameraPanel.addView(this.tooltipTextView, LayoutHelper.createFrame(-2, -2.0f, 81, 0.0f, 0.0f, 0.0f, 16.0f));
        AnonymousClass11 r32 = new RecyclerListView(context2) {
            public void requestLayout() {
                if (!ChatAttachAlertPhotoLayout.this.cameraPhotoRecyclerViewIgnoreLayout) {
                    super.requestLayout();
                }
            }
        };
        this.cameraPhotoRecyclerView = r32;
        r32.setVerticalScrollBarEnabled(true);
        RecyclerListView recyclerListView = this.cameraPhotoRecyclerView;
        PhotoAttachAdapter photoAttachAdapter2 = new PhotoAttachAdapter(context2, false);
        this.cameraAttachAdapter = photoAttachAdapter2;
        recyclerListView.setAdapter(photoAttachAdapter2);
        this.cameraAttachAdapter.createCache();
        this.cameraPhotoRecyclerView.setClipToPadding(false);
        this.cameraPhotoRecyclerView.setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f), 0);
        this.cameraPhotoRecyclerView.setItemAnimator((RecyclerView.ItemAnimator) null);
        this.cameraPhotoRecyclerView.setLayoutAnimation((LayoutAnimationController) null);
        this.cameraPhotoRecyclerView.setOverScrollMode(2);
        this.cameraPhotoRecyclerView.setVisibility(4);
        this.cameraPhotoRecyclerView.setAlpha(0.0f);
        container.addView(this.cameraPhotoRecyclerView, LayoutHelper.createFrame(-1, 80.0f));
        AnonymousClass12 r2 = new LinearLayoutManager(this, context2, 0, false) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        this.cameraPhotoLayoutManager = r2;
        this.cameraPhotoRecyclerView.setLayoutManager(r2);
        this.cameraPhotoRecyclerView.setOnItemClickListener((RecyclerListView.OnItemClickListener) $$Lambda$ChatAttachAlertPhotoLayout$haIlfDi46jg2x3uQIFo8lt0oBQ.INSTANCE);
    }

    public /* synthetic */ void lambda$new$0$ChatAttachAlertPhotoLayout(View view) {
        this.dropDownContainer.toggleSubMenu();
    }

    public /* synthetic */ void lambda$new$1$ChatAttachAlertPhotoLayout(View view, int i) {
        BaseFragment baseFragment;
        ChatActivity chatActivity;
        int i2;
        if (this.mediaEnabled && (baseFragment = this.parentAlert.baseFragment) != null && baseFragment.getParentActivity() != null) {
            if (Build.VERSION.SDK_INT >= 23) {
                if (i == 0 && this.noCameraPermissions) {
                    try {
                        this.parentAlert.baseFragment.getParentActivity().requestPermissions(new String[]{"android.permission.CAMERA"}, 18);
                        return;
                    } catch (Exception unused) {
                        return;
                    }
                } else if (this.noGalleryPermissions) {
                    try {
                        this.parentAlert.baseFragment.getParentActivity().requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 4);
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
                    PhotoViewer.getInstance().setParentActivity(this.parentAlert.baseFragment.getParentActivity());
                    PhotoViewer.getInstance().setParentAlert(this.parentAlert);
                    PhotoViewer instance = PhotoViewer.getInstance();
                    ChatAttachAlert chatAttachAlert = this.parentAlert;
                    instance.setMaxSelectedPhotos(chatAttachAlert.maxSelectedPhotos, chatAttachAlert.allowOrder);
                    ChatAttachAlert chatAttachAlert2 = this.parentAlert;
                    BaseFragment baseFragment2 = chatAttachAlert2.baseFragment;
                    if (baseFragment2 instanceof ChatActivity) {
                        chatActivity = (ChatActivity) baseFragment2;
                        i2 = 0;
                    } else if (chatAttachAlert2.avatarPicker != 0) {
                        chatActivity = null;
                        i2 = 1;
                    } else {
                        chatActivity = null;
                        i2 = 4;
                    }
                    PhotoViewer.getInstance().openPhotoForSelect(allPhotosArray, i3, i2, false, this.photoViewerProvider, chatActivity);
                    AndroidUtilities.hideKeyboard(this.parentAlert.baseFragment.getFragmentView().findFocus());
                }
            } else if (SharedConfig.inappCamera) {
                openCamera(true);
            } else {
                ChatAttachAlert.ChatAttachViewDelegate chatAttachViewDelegate = this.parentAlert.delegate;
                if (chatAttachViewDelegate != null) {
                    chatAttachViewDelegate.didPressedButton(0, false, true, 0);
                }
            }
        }
    }

    public /* synthetic */ boolean lambda$new$2$ChatAttachAlertPhotoLayout(View view, int i) {
        if (i == 0 && this.selectedAlbumEntry == this.galleryAlbumEntry) {
            ChatAttachAlert.ChatAttachViewDelegate chatAttachViewDelegate = this.parentAlert.delegate;
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

    public /* synthetic */ void lambda$new$3$ChatAttachAlertPhotoLayout(View view) {
        if (this.cameraView != null) {
            openPhotoViewer((MediaController.PhotoEntry) null, false, false);
            CameraController.getInstance().stopPreview(this.cameraView.getCameraSession());
        }
    }

    public /* synthetic */ void lambda$new$4$ChatAttachAlertPhotoLayout(float f) {
        CameraView cameraView2 = this.cameraView;
        if (cameraView2 != null) {
            this.cameraZoom = f;
            cameraView2.setZoom(f);
        }
        showZoomControls(true, true);
    }

    public /* synthetic */ void lambda$new$5$ChatAttachAlertPhotoLayout(View view) {
        CameraView cameraView2;
        if (!this.takingPhoto && (cameraView2 = this.cameraView) != null && cameraView2.isInitied()) {
            this.canSaveCameraPreview = false;
            this.cameraView.switchCamera();
            ObjectAnimator duration = ObjectAnimator.ofFloat(this.switchCameraButton, View.SCALE_X, new float[]{0.0f}).setDuration(100);
            duration.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    ChatAttachAlertPhotoLayout.this.switchCameraButton.setImageResource((ChatAttachAlertPhotoLayout.this.cameraView == null || !ChatAttachAlertPhotoLayout.this.cameraView.isFrontface()) ? NUM : NUM);
                    ObjectAnimator.ofFloat(ChatAttachAlertPhotoLayout.this.switchCameraButton, View.SCALE_X, new float[]{1.0f}).setDuration(100).start();
                }
            });
            duration.start();
        }
    }

    public /* synthetic */ void lambda$new$6$ChatAttachAlertPhotoLayout(final View view) {
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
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, new float[]{0.0f, (float) AndroidUtilities.dp(48.0f)}), ObjectAnimator.ofFloat(imageView, View.TRANSLATION_Y, new float[]{(float) (-AndroidUtilities.dp(48.0f)), 0.0f}), ObjectAnimator.ofFloat(view, View.ALPHA, new float[]{1.0f, 0.0f}), ObjectAnimator.ofFloat(imageView, View.ALPHA, new float[]{0.0f, 1.0f})});
                animatorSet.setDuration(200);
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        boolean unused = ChatAttachAlertPhotoLayout.this.flashAnimationInProgress = false;
                        view.setVisibility(4);
                        imageView.sendAccessibilityEvent(8);
                    }
                });
                animatorSet.start();
            }
        }
    }

    static /* synthetic */ void lambda$new$7(View view, int i) {
        if (view instanceof PhotoAttachPhotoCell) {
            ((PhotoAttachPhotoCell) view).callDelegate();
        }
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
        this.adapter.notifyDataSetChanged();
        this.cameraAttachAdapter.notifyDataSetChanged();
    }

    private void updateAlbumsDropDown() {
        ArrayList<MediaController.AlbumEntry> arrayList;
        this.dropDownContainer.removeAllSubItems();
        if (this.mediaEnabled) {
            ChatAttachAlert chatAttachAlert = this.parentAlert;
            if ((chatAttachAlert.baseFragment instanceof ChatActivity) || chatAttachAlert.avatarPicker == 2) {
                arrayList = MediaController.allMediaAlbums;
            } else {
                arrayList = MediaController.allPhotoAlbums;
            }
            ArrayList<MediaController.AlbumEntry> arrayList2 = new ArrayList<>(arrayList);
            this.dropDownAlbums = arrayList2;
            Collections.sort(arrayList2, new Comparator(arrayList) {
                public final /* synthetic */ ArrayList f$0;

                {
                    this.f$0 = r1;
                }

                public final int compare(Object obj, Object obj2) {
                    return ChatAttachAlertPhotoLayout.lambda$updateAlbumsDropDown$8(this.f$0, (MediaController.AlbumEntry) obj, (MediaController.AlbumEntry) obj2);
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

    static /* synthetic */ int lambda$updateAlbumsDropDown$8(ArrayList arrayList, MediaController.AlbumEntry albumEntry, MediaController.AlbumEntry albumEntry2) {
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

    private boolean processTouchEvent(MotionEvent motionEvent) {
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
                            AnimatorSet animatorSet = new AnimatorSet();
                            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.cameraPanel, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.zoomControlView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.counterTextView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.flashModeButton[0], View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.flashModeButton[1], View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.cameraPhotoRecyclerView, View.ALPHA, new float[]{0.0f})});
                            animatorSet.setDuration(200);
                            animatorSet.start();
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
                        this.parentAlert.getSheetContainer().invalidate();
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
                        this.cameraView.focusToPoint((int) (motionEvent.getRawX() - ((float) this.viewPosition[0])), (int) (motionEvent.getRawY() - ((float) this.viewPosition[1])));
                    }
                }
            }
        }
        return true;
    }

    /* access modifiers changed from: private */
    public void resetRecordState() {
        if (this.parentAlert.baseFragment != null) {
            for (int i = 0; i < 2; i++) {
                this.flashModeButton[i].setAlpha(1.0f);
            }
            this.switchCameraButton.setAlpha(1.0f);
            this.tooltipTextView.setAlpha(1.0f);
            this.recordTime.setAlpha(0.0f);
            AndroidUtilities.cancelRunOnUIThread(this.videoRecordRunnable);
            this.videoRecordRunnable = null;
            AndroidUtilities.unlockOrientation(this.parentAlert.baseFragment.getParentActivity());
        }
    }

    /* access modifiers changed from: private */
    public void openPhotoViewer(MediaController.PhotoEntry photoEntry, final boolean z, boolean z2) {
        ChatActivity chatActivity;
        int i;
        if (photoEntry != null) {
            cameraPhotos.add(photoEntry);
            selectedPhotos.put(Integer.valueOf(photoEntry.imageId), photoEntry);
            selectedPhotosOrder.add(Integer.valueOf(photoEntry.imageId));
            this.parentAlert.updateCountButton(0);
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
            PhotoViewer.getInstance().setParentActivity(this.parentAlert.baseFragment.getParentActivity());
            PhotoViewer.getInstance().setParentAlert(this.parentAlert);
            PhotoViewer instance = PhotoViewer.getInstance();
            ChatAttachAlert chatAttachAlert = this.parentAlert;
            instance.setMaxSelectedPhotos(chatAttachAlert.maxSelectedPhotos, chatAttachAlert.allowOrder);
            ChatAttachAlert chatAttachAlert2 = this.parentAlert;
            BaseFragment baseFragment = chatAttachAlert2.baseFragment;
            if (baseFragment instanceof ChatActivity) {
                chatActivity = (ChatActivity) baseFragment;
                i = 2;
            } else if (chatAttachAlert2.avatarPicker != 0) {
                chatActivity = null;
                i = 1;
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
                    if (ChatAttachAlertPhotoLayout.this.cameraOpened && ChatAttachAlertPhotoLayout.this.cameraView != null) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public final void run() {
                                ChatAttachAlertPhotoLayout.AnonymousClass13.this.lambda$cancelButtonPressed$0$ChatAttachAlertPhotoLayout$13();
                            }
                        }, 1000);
                        ChatAttachAlertPhotoLayout.this.zoomControlView.setZoom(0.0f, false);
                        float unused = ChatAttachAlertPhotoLayout.this.cameraZoom = 0.0f;
                        ChatAttachAlertPhotoLayout.this.cameraView.setZoom(0.0f);
                        CameraController.getInstance().startPreview(ChatAttachAlertPhotoLayout.this.cameraView.getCameraSession());
                    }
                    if (ChatAttachAlertPhotoLayout.this.cancelTakingPhotos && ChatAttachAlertPhotoLayout.cameraPhotos.size() == 1) {
                        int size = ChatAttachAlertPhotoLayout.cameraPhotos.size();
                        for (int i = 0; i < size; i++) {
                            MediaController.PhotoEntry photoEntry = (MediaController.PhotoEntry) ChatAttachAlertPhotoLayout.cameraPhotos.get(i);
                            new File(photoEntry.path).delete();
                            if (photoEntry.imagePath != null) {
                                new File(photoEntry.imagePath).delete();
                            }
                            if (photoEntry.thumbPath != null) {
                                new File(photoEntry.thumbPath).delete();
                            }
                        }
                        ChatAttachAlertPhotoLayout.cameraPhotos.clear();
                        ChatAttachAlertPhotoLayout.selectedPhotosOrder.clear();
                        ChatAttachAlertPhotoLayout.selectedPhotos.clear();
                        ChatAttachAlertPhotoLayout.this.counterTextView.setVisibility(4);
                        ChatAttachAlertPhotoLayout.this.cameraPhotoRecyclerView.setVisibility(8);
                        ChatAttachAlertPhotoLayout.this.adapter.notifyDataSetChanged();
                        ChatAttachAlertPhotoLayout.this.cameraAttachAdapter.notifyDataSetChanged();
                        ChatAttachAlertPhotoLayout.this.parentAlert.updateCountButton(0);
                    }
                    return true;
                }

                public /* synthetic */ void lambda$cancelButtonPressed$0$ChatAttachAlertPhotoLayout$13() {
                    if (ChatAttachAlertPhotoLayout.this.cameraView != null && !ChatAttachAlertPhotoLayout.this.parentAlert.isDismissed() && Build.VERSION.SDK_INT >= 21) {
                        ChatAttachAlertPhotoLayout.this.cameraView.setSystemUiVisibility(1028);
                    }
                }

                public void needAddMorePhotos() {
                    boolean unused = ChatAttachAlertPhotoLayout.this.cancelTakingPhotos = false;
                    if (ChatAttachAlertPhotoLayout.mediaFromExternalCamera) {
                        ChatAttachAlertPhotoLayout.this.parentAlert.delegate.didPressedButton(0, true, true, 0);
                        return;
                    }
                    if (!ChatAttachAlertPhotoLayout.this.cameraOpened) {
                        ChatAttachAlertPhotoLayout.this.openCamera(false);
                    }
                    ChatAttachAlertPhotoLayout.this.counterTextView.setVisibility(0);
                    ChatAttachAlertPhotoLayout.this.cameraPhotoRecyclerView.setVisibility(0);
                    ChatAttachAlertPhotoLayout.this.counterTextView.setAlpha(1.0f);
                    ChatAttachAlertPhotoLayout.this.updatePhotosCounter(false);
                }

                public void sendButtonPressed(int i, VideoEditedInfo videoEditedInfo, boolean z, int i2) {
                    if (!ChatAttachAlertPhotoLayout.cameraPhotos.isEmpty() && ChatAttachAlertPhotoLayout.this.parentAlert.baseFragment != null) {
                        if (videoEditedInfo != null && i >= 0 && i < ChatAttachAlertPhotoLayout.cameraPhotos.size()) {
                            ((MediaController.PhotoEntry) ChatAttachAlertPhotoLayout.cameraPhotos.get(i)).editedInfo = videoEditedInfo;
                        }
                        BaseFragment baseFragment = ChatAttachAlertPhotoLayout.this.parentAlert.baseFragment;
                        if (!(baseFragment instanceof ChatActivity) || !((ChatActivity) baseFragment).isSecretChat()) {
                            int size = ChatAttachAlertPhotoLayout.cameraPhotos.size();
                            for (int i3 = 0; i3 < size; i3++) {
                                AndroidUtilities.addMediaToGallery(((MediaController.PhotoEntry) ChatAttachAlertPhotoLayout.cameraPhotos.get(i3)).path);
                            }
                        }
                        ChatAttachAlertPhotoLayout.this.parentAlert.applyCaption();
                        ChatAttachAlertPhotoLayout.this.parentAlert.delegate.didPressedButton(8, true, z, i2);
                        ChatAttachAlertPhotoLayout.cameraPhotos.clear();
                        ChatAttachAlertPhotoLayout.selectedPhotosOrder.clear();
                        ChatAttachAlertPhotoLayout.selectedPhotos.clear();
                        ChatAttachAlertPhotoLayout.this.adapter.notifyDataSetChanged();
                        ChatAttachAlertPhotoLayout.this.cameraAttachAdapter.notifyDataSetChanged();
                        ChatAttachAlertPhotoLayout.this.closeCamera(false);
                        ChatAttachAlertPhotoLayout.this.parentAlert.dismiss();
                    }
                }

                public boolean scaleToFill() {
                    BaseFragment baseFragment = ChatAttachAlertPhotoLayout.this.parentAlert.baseFragment;
                    if (baseFragment == null || baseFragment.getParentActivity() == null) {
                        return false;
                    }
                    int i = Settings.System.getInt(ChatAttachAlertPhotoLayout.this.parentAlert.baseFragment.getParentActivity().getContentResolver(), "accelerometer_rotation", 0);
                    if (z || i == 1) {
                        return true;
                    }
                    return false;
                }

                public void willHidePhotoViewer() {
                    int childCount = ChatAttachAlertPhotoLayout.this.gridView.getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        View childAt = ChatAttachAlertPhotoLayout.this.gridView.getChildAt(i);
                        if (childAt instanceof PhotoAttachPhotoCell) {
                            PhotoAttachPhotoCell photoAttachPhotoCell = (PhotoAttachPhotoCell) childAt;
                            photoAttachPhotoCell.showImage();
                            photoAttachPhotoCell.showCheck(true);
                        }
                    }
                }

                public boolean canCaptureMorePhotos() {
                    return ChatAttachAlertPhotoLayout.this.parentAlert.maxSelectedPhotos != 1;
                }
            }, chatActivity);
        }
    }

    /* access modifiers changed from: private */
    public void showZoomControls(boolean z, boolean z2) {
        if ((this.zoomControlView.getTag() == null || !z) && (this.zoomControlView.getTag() != null || z)) {
            AnimatorSet animatorSet = this.zoomControlAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            this.zoomControlView.setTag(z ? 1 : null);
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.zoomControlAnimation = animatorSet2;
            animatorSet2.setDuration(180);
            AnimatorSet animatorSet3 = this.zoomControlAnimation;
            Animator[] animatorArr = new Animator[1];
            ZoomControlView zoomControlView2 = this.zoomControlView;
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            fArr[0] = z ? 1.0f : 0.0f;
            animatorArr[0] = ObjectAnimator.ofFloat(zoomControlView2, property, fArr);
            animatorSet3.playTogether(animatorArr);
            this.zoomControlAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    AnimatorSet unused = ChatAttachAlertPhotoLayout.this.zoomControlAnimation = null;
                }
            });
            this.zoomControlAnimation.start();
            if (z) {
                $$Lambda$ChatAttachAlertPhotoLayout$6kNyWryBs1ctWsDKfi5QoDDIewo r9 = new Runnable() {
                    public final void run() {
                        ChatAttachAlertPhotoLayout.this.lambda$showZoomControls$10$ChatAttachAlertPhotoLayout();
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
            $$Lambda$ChatAttachAlertPhotoLayout$H9ye6zjMYLHj0_v9I1dkQnpOPSc r92 = new Runnable() {
                public final void run() {
                    ChatAttachAlertPhotoLayout.this.lambda$showZoomControls$9$ChatAttachAlertPhotoLayout();
                }
            };
            this.zoomControlHideRunnable = r92;
            AndroidUtilities.runOnUIThread(r92, 2000);
        }
    }

    public /* synthetic */ void lambda$showZoomControls$9$ChatAttachAlertPhotoLayout() {
        showZoomControls(false, true);
        this.zoomControlHideRunnable = null;
    }

    public /* synthetic */ void lambda$showZoomControls$10$ChatAttachAlertPhotoLayout() {
        showZoomControls(false, true);
        this.zoomControlHideRunnable = null;
    }

    /* access modifiers changed from: private */
    public void updatePhotosCounter(boolean z) {
        if (this.counterTextView != null && this.parentAlert.avatarPicker == 0) {
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
                    this.parentAlert.selectedTextView.setText(LocaleController.formatPluralString("MediaSelected", max));
                }
            } else if (z2) {
                this.counterTextView.setText(LocaleController.formatPluralString("Videos", selectedPhotos.size()).toUpperCase());
                if (max != this.currentSelectedCount || z) {
                    this.parentAlert.selectedTextView.setText(LocaleController.formatPluralString("VideosSelected", max));
                }
            } else {
                this.counterTextView.setText(LocaleController.formatPluralString("Photos", selectedPhotos.size()).toUpperCase());
                if (max != this.currentSelectedCount || z) {
                    this.parentAlert.selectedTextView.setText(LocaleController.formatPluralString("PhotosSelected", max));
                }
            }
            this.currentSelectedCount = max;
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
            r6 = 2131165380(0x7var_c4, float:1.7944975E38)
            r5.setImageResource(r6)
            r6 = 2131623955(0x7f0e0013, float:1.8875076E38)
            java.lang.String r0 = "AccDescrCameraFlashAuto"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r0, r6)
            r5.setContentDescription(r6)
            goto L_0x0073
        L_0x004e:
            r6 = 2131165382(0x7var_c6, float:1.794498E38)
            r5.setImageResource(r6)
            r6 = 2131623957(0x7f0e0015, float:1.887508E38)
            java.lang.String r0 = "AccDescrCameraFlashOn"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r0, r6)
            r5.setContentDescription(r6)
            goto L_0x0073
        L_0x0061:
            r6 = 2131165381(0x7var_c5, float:1.7944978E38)
            r5.setImageResource(r6)
            r6 = 2131623956(0x7f0e0014, float:1.8875078E38)
            java.lang.String r0 = "AccDescrCameraFlashOff"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r0, r6)
            r5.setContentDescription(r6)
        L_0x0073:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlertPhotoLayout.setCameraFlashModeIcon(android.widget.ImageView, java.lang.String):void");
    }

    public void checkCamera(boolean z) {
        PhotoAttachAdapter photoAttachAdapter;
        BaseFragment baseFragment = this.parentAlert.baseFragment;
        if (baseFragment != null) {
            boolean z2 = this.deviceHasGoodCamera;
            boolean z3 = this.noCameraPermissions;
            if (!SharedConfig.inappCamera) {
                this.deviceHasGoodCamera = false;
            } else if (Build.VERSION.SDK_INT >= 23) {
                boolean z4 = baseFragment.getParentActivity().checkSelfPermission("android.permission.CAMERA") != 0;
                this.noCameraPermissions = z4;
                if (z4) {
                    if (z) {
                        try {
                            this.parentAlert.baseFragment.getParentActivity().requestPermissions(new String[]{"android.permission.CAMERA"}, 17);
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
            if (this.parentAlert.isShowing() && this.deviceHasGoodCamera) {
                ChatAttachAlert chatAttachAlert = this.parentAlert;
                if (chatAttachAlert.baseFragment != null && chatAttachAlert.getBackDrawable().getAlpha() != 0 && !this.cameraOpened) {
                    showCamera();
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void openCamera(boolean z) {
        CameraView cameraView2 = this.cameraView;
        if (cameraView2 != null && this.cameraInitAnimation == null && cameraView2.isInitied()) {
            ChatAttachAlert chatAttachAlert = this.parentAlert;
            int i = 0;
            if (chatAttachAlert.avatarPicker == 2 || (chatAttachAlert.baseFragment instanceof ChatActivity)) {
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
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(arrayList);
                animatorSet.setDuration(200);
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        boolean unused = ChatAttachAlertPhotoLayout.this.cameraAnimationInProgress = false;
                        if (Build.VERSION.SDK_INT >= 21 && ChatAttachAlertPhotoLayout.this.cameraView != null) {
                            ChatAttachAlertPhotoLayout.this.cameraView.invalidateOutline();
                        }
                        if (ChatAttachAlertPhotoLayout.this.cameraOpened) {
                            ChatAttachAlertPhotoLayout.this.parentAlert.delegate.onCameraOpened();
                        }
                    }
                });
                animatorSet.start();
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
                this.parentAlert.delegate.onCameraOpened();
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

    public void loadGalleryPhotos() {
        MediaController.AlbumEntry albumEntry;
        ChatAttachAlert chatAttachAlert = this.parentAlert;
        if ((chatAttachAlert.baseFragment instanceof ChatActivity) || chatAttachAlert.avatarPicker == 2) {
            albumEntry = MediaController.allMediaAlbumEntry;
        } else {
            albumEntry = MediaController.allPhotosAlbumEntry;
        }
        if (albumEntry == null && Build.VERSION.SDK_INT >= 21) {
            MediaController.loadGalleryPhotosAlbums(0);
        }
    }

    public void showCamera() {
        ChatAttachAlert chatAttachAlert = this.parentAlert;
        if (!chatAttachAlert.paused && this.mediaEnabled) {
            if (this.cameraView == null) {
                CameraView cameraView2 = new CameraView(chatAttachAlert.baseFragment.getParentActivity(), this.parentAlert.openWithFrontFaceCamera);
                this.cameraView = cameraView2;
                cameraView2.setFocusable(true);
                if (Build.VERSION.SDK_INT >= 21) {
                    this.cameraView.setOutlineProvider(new ViewOutlineProvider() {
                        public void getOutline(View view, Outline outline) {
                            if (ChatAttachAlertPhotoLayout.this.cameraAnimationInProgress) {
                                ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout = ChatAttachAlertPhotoLayout.this;
                                int dp = AndroidUtilities.dp(chatAttachAlertPhotoLayout.parentAlert.cornerRadius * 8.0f * chatAttachAlertPhotoLayout.cameraOpenProgress);
                                outline.setRoundRect(0, 0, view.getMeasuredWidth() + dp, view.getMeasuredHeight() + dp, (float) dp);
                            } else if (ChatAttachAlertPhotoLayout.this.cameraAnimationInProgress || ChatAttachAlertPhotoLayout.this.cameraOpened) {
                                outline.setRect(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
                            } else {
                                int dp2 = AndroidUtilities.dp(ChatAttachAlertPhotoLayout.this.parentAlert.cornerRadius * 8.0f);
                                outline.setRoundRect(0, 0, view.getMeasuredWidth() + dp2, view.getMeasuredHeight() + dp2, (float) dp2);
                            }
                        }
                    });
                    this.cameraView.setClipToOutline(true);
                }
                this.cameraView.setContentDescription(LocaleController.getString("AccDescrInstantCamera", NUM));
                FrameLayout container = this.parentAlert.getContainer();
                CameraView cameraView3 = this.cameraView;
                int i = this.itemSize;
                container.addView(cameraView3, 1, new FrameLayout.LayoutParams(i, i));
                this.cameraView.setDelegate(new CameraView.CameraViewDelegate() {
                    public void onCameraCreated(Camera camera) {
                    }

                    public void onCameraInit() {
                        int i = 4;
                        if (ChatAttachAlertPhotoLayout.this.cameraView.getCameraSession().getCurrentFlashMode().equals(ChatAttachAlertPhotoLayout.this.cameraView.getCameraSession().getNextFlashMode())) {
                            for (int i2 = 0; i2 < 2; i2++) {
                                ChatAttachAlertPhotoLayout.this.flashModeButton[i2].setVisibility(4);
                                ChatAttachAlertPhotoLayout.this.flashModeButton[i2].setAlpha(0.0f);
                                ChatAttachAlertPhotoLayout.this.flashModeButton[i2].setTranslationY(0.0f);
                            }
                        } else {
                            ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout = ChatAttachAlertPhotoLayout.this;
                            chatAttachAlertPhotoLayout.setCameraFlashModeIcon(chatAttachAlertPhotoLayout.flashModeButton[0], ChatAttachAlertPhotoLayout.this.cameraView.getCameraSession().getCurrentFlashMode());
                            int i3 = 0;
                            while (i3 < 2) {
                                ChatAttachAlertPhotoLayout.this.flashModeButton[i3].setVisibility(i3 == 0 ? 0 : 4);
                                ChatAttachAlertPhotoLayout.this.flashModeButton[i3].setAlpha((i3 != 0 || !ChatAttachAlertPhotoLayout.this.cameraOpened) ? 0.0f : 1.0f);
                                ChatAttachAlertPhotoLayout.this.flashModeButton[i3].setTranslationY(0.0f);
                                i3++;
                            }
                        }
                        ChatAttachAlertPhotoLayout.this.switchCameraButton.setImageResource(ChatAttachAlertPhotoLayout.this.cameraView.isFrontface() ? NUM : NUM);
                        ImageView access$1900 = ChatAttachAlertPhotoLayout.this.switchCameraButton;
                        if (ChatAttachAlertPhotoLayout.this.cameraView.hasFrontFaceCamera()) {
                            i = 0;
                        }
                        access$1900.setVisibility(i);
                        if (!ChatAttachAlertPhotoLayout.this.cameraOpened) {
                            AnimatorSet unused = ChatAttachAlertPhotoLayout.this.cameraInitAnimation = new AnimatorSet();
                            ChatAttachAlertPhotoLayout.this.cameraInitAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(ChatAttachAlertPhotoLayout.this.cameraView, View.ALPHA, new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(ChatAttachAlertPhotoLayout.this.cameraIcon, View.ALPHA, new float[]{0.0f, 1.0f})});
                            ChatAttachAlertPhotoLayout.this.cameraInitAnimation.setDuration(180);
                            ChatAttachAlertPhotoLayout.this.cameraInitAnimation.addListener(new AnimatorListenerAdapter() {
                                public void onAnimationEnd(Animator animator) {
                                    if (animator.equals(ChatAttachAlertPhotoLayout.this.cameraInitAnimation)) {
                                        boolean unused = ChatAttachAlertPhotoLayout.this.canSaveCameraPreview = true;
                                        AnimatorSet unused2 = ChatAttachAlertPhotoLayout.this.cameraInitAnimation = null;
                                        if (!ChatAttachAlertPhotoLayout.this.isHidden) {
                                            int childCount = ChatAttachAlertPhotoLayout.this.gridView.getChildCount();
                                            for (int i = 0; i < childCount; i++) {
                                                View childAt = ChatAttachAlertPhotoLayout.this.gridView.getChildAt(i);
                                                if (childAt instanceof PhotoAttachCameraCell) {
                                                    childAt.setVisibility(4);
                                                    return;
                                                }
                                            }
                                        }
                                    }
                                }

                                public void onAnimationCancel(Animator animator) {
                                    AnimatorSet unused = ChatAttachAlertPhotoLayout.this.cameraInitAnimation = null;
                                }
                            });
                            ChatAttachAlertPhotoLayout.this.cameraInitAnimation.start();
                        }
                    }
                });
                if (this.cameraIcon == null) {
                    AnonymousClass18 r0 = new FrameLayout(this.parentAlert.baseFragment.getParentActivity()) {
                        /* access modifiers changed from: protected */
                        public void onDraw(Canvas canvas) {
                            int intrinsicWidth = ChatAttachAlertPhotoLayout.this.cameraDrawable.getIntrinsicWidth();
                            int intrinsicHeight = ChatAttachAlertPhotoLayout.this.cameraDrawable.getIntrinsicHeight();
                            int access$1200 = (ChatAttachAlertPhotoLayout.this.itemSize - intrinsicWidth) / 2;
                            int access$12002 = (ChatAttachAlertPhotoLayout.this.itemSize - intrinsicHeight) / 2;
                            if (ChatAttachAlertPhotoLayout.this.cameraViewOffsetY != 0) {
                                access$12002 -= ChatAttachAlertPhotoLayout.this.cameraViewOffsetY;
                            }
                            ChatAttachAlertPhotoLayout.this.cameraDrawable.setBounds(access$1200, access$12002, intrinsicWidth + access$1200, intrinsicHeight + access$12002);
                            ChatAttachAlertPhotoLayout.this.cameraDrawable.draw(canvas);
                        }
                    };
                    this.cameraIcon = r0;
                    r0.setWillNotDraw(false);
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
            AnimatorSet animatorSet = this.cameraInitAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.cameraInitAnimation = null;
            }
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    ChatAttachAlertPhotoLayout.this.lambda$hideCamera$11$ChatAttachAlertPhotoLayout();
                }
            }, 300);
            this.canSaveCameraPreview = false;
        }
    }

    public /* synthetic */ void lambda$hideCamera$11$ChatAttachAlertPhotoLayout() {
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

    /* JADX WARNING: Code restructure failed: missing block: B:46:0x00f8, code lost:
        if (new java.io.File(r0).exists() != false) goto L_0x00fb;
     */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x015e A[SYNTHETIC, Splitter:B:82:0x015e] */
    /* JADX WARNING: Removed duplicated region for block: B:91:0x01c9 A[SYNTHETIC, Splitter:B:91:0x01c9] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onActivityResultFragment(int r29, android.content.Intent r30, java.lang.String r31) {
        /*
            r28 = this;
            r1 = r28
            r0 = r29
            r7 = r31
            org.telegram.ui.Components.ChatAttachAlert r2 = r1.parentAlert
            org.telegram.ui.ActionBar.BaseFragment r2 = r2.baseFragment
            if (r2 == 0) goto L_0x01d3
            android.app.Activity r2 = r2.getParentActivity()
            if (r2 != 0) goto L_0x0014
            goto L_0x01d3
        L_0x0014:
            r14 = 1
            mediaFromExternalCamera = r14
            r15 = 0
            if (r0 != 0) goto L_0x007e
            org.telegram.ui.PhotoViewer r0 = org.telegram.ui.PhotoViewer.getInstance()
            org.telegram.ui.Components.ChatAttachAlert r2 = r1.parentAlert
            org.telegram.ui.ActionBar.BaseFragment r2 = r2.baseFragment
            android.app.Activity r2 = r2.getParentActivity()
            r0.setParentActivity(r2)
            org.telegram.ui.PhotoViewer r0 = org.telegram.ui.PhotoViewer.getInstance()
            org.telegram.ui.Components.ChatAttachAlert r2 = r1.parentAlert
            int r3 = r2.maxSelectedPhotos
            boolean r2 = r2.allowOrder
            r0.setMaxSelectedPhotos(r3, r2)
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            androidx.exifinterface.media.ExifInterface r0 = new androidx.exifinterface.media.ExifInterface     // Catch:{ Exception -> 0x005c }
            r0.<init>((java.lang.String) r7)     // Catch:{ Exception -> 0x005c }
            java.lang.String r2 = "Orientation"
            int r0 = r0.getAttributeInt(r2, r14)     // Catch:{ Exception -> 0x005c }
            r2 = 3
            if (r0 == r2) goto L_0x0058
            r2 = 6
            if (r0 == r2) goto L_0x0055
            r2 = 8
            if (r0 == r2) goto L_0x0052
            r0 = 0
            goto L_0x005a
        L_0x0052:
            r0 = 270(0x10e, float:3.78E-43)
            goto L_0x005a
        L_0x0055:
            r0 = 90
            goto L_0x005a
        L_0x0058:
            r0 = 180(0xb4, float:2.52E-43)
        L_0x005a:
            r8 = r0
            goto L_0x0061
        L_0x005c:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r8 = 0
        L_0x0061:
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
            goto L_0x01d3
        L_0x007e:
            r2 = 2
            if (r0 != r2) goto L_0x01d3
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x0099
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "pic path "
            r0.append(r2)
            r0.append(r7)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.d(r0)
        L_0x0099:
            r2 = 0
            if (r30 == 0) goto L_0x00ab
            if (r7 == 0) goto L_0x00ab
            java.io.File r0 = new java.io.File
            r0.<init>(r7)
            boolean r0 = r0.exists()
            if (r0 == 0) goto L_0x00ab
            r0 = r2
            goto L_0x00ad
        L_0x00ab:
            r0 = r30
        L_0x00ad:
            if (r0 == 0) goto L_0x0110
            android.net.Uri r0 = r0.getData()
            if (r0 == 0) goto L_0x00fa
            boolean r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r3 == 0) goto L_0x00d1
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "video record uri "
            r3.append(r4)
            java.lang.String r4 = r0.toString()
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            org.telegram.messenger.FileLog.d(r3)
        L_0x00d1:
            java.lang.String r0 = org.telegram.messenger.AndroidUtilities.getPath(r0)
            boolean r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r3 == 0) goto L_0x00ed
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "resolved path = "
            r3.append(r4)
            r3.append(r0)
            java.lang.String r3 = r3.toString()
            org.telegram.messenger.FileLog.d(r3)
        L_0x00ed:
            if (r0 == 0) goto L_0x00fa
            java.io.File r3 = new java.io.File
            r3.<init>(r0)
            boolean r3 = r3.exists()
            if (r3 != 0) goto L_0x00fb
        L_0x00fa:
            r0 = r7
        L_0x00fb:
            org.telegram.ui.Components.ChatAttachAlert r3 = r1.parentAlert
            org.telegram.ui.ActionBar.BaseFragment r3 = r3.baseFragment
            boolean r4 = r3 instanceof org.telegram.ui.ChatActivity
            if (r4 == 0) goto L_0x010b
            org.telegram.ui.ChatActivity r3 = (org.telegram.ui.ChatActivity) r3
            boolean r3 = r3.isSecretChat()
            if (r3 != 0) goto L_0x010e
        L_0x010b:
            org.telegram.messenger.AndroidUtilities.addMediaToGallery((java.lang.String) r31)
        L_0x010e:
            r7 = r2
            goto L_0x0111
        L_0x0110:
            r0 = r2
        L_0x0111:
            if (r0 != 0) goto L_0x0121
            if (r7 == 0) goto L_0x0121
            java.io.File r3 = new java.io.File
            r3.<init>(r7)
            boolean r3 = r3.exists()
            if (r3 == 0) goto L_0x0121
            goto L_0x0122
        L_0x0121:
            r7 = r0
        L_0x0122:
            r3 = 0
            android.media.MediaMetadataRetriever r5 = new android.media.MediaMetadataRetriever     // Catch:{ Exception -> 0x0158 }
            r5.<init>()     // Catch:{ Exception -> 0x0158 }
            r5.setDataSource(r7)     // Catch:{ Exception -> 0x0152, all -> 0x014d }
            r0 = 9
            java.lang.String r0 = r5.extractMetadata(r0)     // Catch:{ Exception -> 0x0152, all -> 0x014d }
            if (r0 == 0) goto L_0x0143
            long r8 = java.lang.Long.parseLong(r0)     // Catch:{ Exception -> 0x0152, all -> 0x014d }
            float r0 = (float) r8     // Catch:{ Exception -> 0x0152, all -> 0x014d }
            r2 = 1148846080(0x447a0000, float:1000.0)
            float r0 = r0 / r2
            double r8 = (double) r0     // Catch:{ Exception -> 0x0152, all -> 0x014d }
            double r2 = java.lang.Math.ceil(r8)     // Catch:{ Exception -> 0x0152, all -> 0x014d }
            int r0 = (int) r2
            long r3 = (long) r0
        L_0x0143:
            r5.release()     // Catch:{ Exception -> 0x0147 }
            goto L_0x0161
        L_0x0147:
            r0 = move-exception
            r2 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)
            goto L_0x0161
        L_0x014d:
            r0 = move-exception
            r3 = r0
            r2 = r5
            goto L_0x01c7
        L_0x0152:
            r0 = move-exception
            r2 = r5
            goto L_0x0159
        L_0x0155:
            r0 = move-exception
            r3 = r0
            goto L_0x01c7
        L_0x0158:
            r0 = move-exception
        L_0x0159:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x0155 }
            if (r2 == 0) goto L_0x0161
            r2.release()     // Catch:{ Exception -> 0x0147 }
        L_0x0161:
            android.graphics.Bitmap r0 = org.telegram.messenger.SendMessagesHelper.createVideoThumbnail(r7, r14)
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
            java.io.FileOutputStream r2 = new java.io.FileOutputStream     // Catch:{ all -> 0x0196 }
            r2.<init>(r5)     // Catch:{ all -> 0x0196 }
            android.graphics.Bitmap$CompressFormat r6 = android.graphics.Bitmap.CompressFormat.JPEG     // Catch:{ all -> 0x0196 }
            r8 = 55
            r0.compress(r6, r8, r2)     // Catch:{ all -> 0x0196 }
            goto L_0x019a
        L_0x0196:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x019a:
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
            goto L_0x01d3
        L_0x01c7:
            if (r2 == 0) goto L_0x01d2
            r2.release()     // Catch:{ Exception -> 0x01cd }
            goto L_0x01d2
        L_0x01cd:
            r0 = move-exception
            r2 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)
        L_0x01d2:
            throw r3
        L_0x01d3:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlertPhotoLayout.onActivityResultFragment(int, android.content.Intent, java.lang.String):void");
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
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(arrayList);
                animatorSet.setDuration(200);
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        boolean unused = ChatAttachAlertPhotoLayout.this.cameraAnimationInProgress = false;
                        if (Build.VERSION.SDK_INT >= 21 && ChatAttachAlertPhotoLayout.this.cameraView != null) {
                            ChatAttachAlertPhotoLayout.this.cameraView.invalidateOutline();
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
                        if (Build.VERSION.SDK_INT >= 21 && ChatAttachAlertPhotoLayout.this.cameraView != null) {
                            ChatAttachAlertPhotoLayout.this.cameraView.setSystemUiVisibility(1024);
                        }
                    }
                });
                animatorSet.start();
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
        if (this.cameraView != null) {
            this.cameraOpenProgress = f;
            int[] iArr = this.animateCameraValues;
            float f2 = (float) iArr[1];
            float f3 = (float) iArr[2];
            Point point = AndroidUtilities.displaySize;
            int i = point.x;
            int i2 = point.y;
            float width = (float) ((this.parentAlert.getContainer().getWidth() - this.parentAlert.getLeftInset()) - this.parentAlert.getRightInset());
            float height = (float) (this.parentAlert.getContainer().getHeight() - this.parentAlert.getBottomInset());
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
            layoutParams.width = (int) (f2 + ((width - f2) * f));
            layoutParams.height = (int) (f3 + ((height - f3) * f));
            if (f != 0.0f) {
                float f4 = 1.0f - f;
                this.cameraView.setClipTop((int) (((float) this.cameraViewOffsetY) * f4));
                this.cameraView.setClipBottom((int) (((float) this.cameraViewOffsetBottomY) * f4));
                int[] iArr2 = this.cameraViewLocation;
                layoutParams.leftMargin = (int) (((float) iArr2[0]) * f4);
                int[] iArr3 = this.animateCameraValues;
                layoutParams.topMargin = (int) (((float) iArr3[0]) + (((float) (iArr2[1] - iArr3[0])) * f4));
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
                    iArr[0] = iArr[0] - this.parentAlert.getLeftInset();
                    float x = this.gridView.getX() - ((float) this.parentAlert.getLeftInset());
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
                    int measuredHeight = this.parentAlert.getSheetContainer().getMeasuredHeight();
                    int keyboardHeight = this.parentAlert.sizeNotifierFrameLayout.getKeyboardHeight();
                    if (!AndroidUtilities.isInMultiwindow && keyboardHeight <= AndroidUtilities.dp(20.0f)) {
                        measuredHeight -= this.parentAlert.commentTextView.getEmojiPadding();
                    }
                    int measuredHeight2 = (int) (((float) (measuredHeight - this.parentAlert.buttonsRecyclerView.getMeasuredHeight())) + this.parentAlert.buttonsRecyclerView.getTranslationY() + this.parentAlert.getSheetContainer().getTranslationY());
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
                        public final /* synthetic */ FrameLayout.LayoutParams f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run() {
                            ChatAttachAlertPhotoLayout.this.lambda$applyCameraViewPosition$12$ChatAttachAlertPhotoLayout(this.f$1);
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
                    public final /* synthetic */ FrameLayout.LayoutParams f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        ChatAttachAlertPhotoLayout.this.lambda$applyCameraViewPosition$13$ChatAttachAlertPhotoLayout(this.f$1);
                    }
                });
            }
        }
    }

    public /* synthetic */ void lambda$applyCameraViewPosition$12$ChatAttachAlertPhotoLayout(FrameLayout.LayoutParams layoutParams) {
        CameraView cameraView2 = this.cameraView;
        if (cameraView2 != null) {
            cameraView2.setLayoutParams(layoutParams);
        }
    }

    public /* synthetic */ void lambda$applyCameraViewPosition$13$ChatAttachAlertPhotoLayout(FrameLayout.LayoutParams layoutParams) {
        FrameLayout frameLayout = this.cameraIcon;
        if (frameLayout != null) {
            frameLayout.setLayoutParams(layoutParams);
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
    public void onMenuItemClick(int i) {
        TLRPC$Chat currentChat;
        if ((i == 0 || i == 1) && this.parentAlert.maxSelectedPhotos > 0 && selectedPhotosOrder.size() > 1) {
            BaseFragment baseFragment = this.parentAlert.baseFragment;
            if ((baseFragment instanceof ChatActivity) && (currentChat = ((ChatActivity) baseFragment).getCurrentChat()) != null && !ChatObject.hasAdminRights(currentChat) && currentChat.slowmode_enabled) {
                AlertsCreator.createSimpleAlert(getContext(), LocaleController.getString("Slowmode", NUM), LocaleController.getString("SlowmodeSendError", NUM)).show();
                return;
            }
        }
        if (i == 0) {
            ChatAttachAlert chatAttachAlert = this.parentAlert;
            if (chatAttachAlert.editingMessageObject == null) {
                BaseFragment baseFragment2 = chatAttachAlert.baseFragment;
                if ((baseFragment2 instanceof ChatActivity) && ((ChatActivity) baseFragment2).isInScheduleMode()) {
                    AlertsCreator.createScheduleDatePickerDialog(getContext(), ((ChatActivity) this.parentAlert.baseFragment).getDialogId(), new AlertsCreator.ScheduleDatePickerDelegate() {
                        public final void didSelectDate(boolean z, int i) {
                            ChatAttachAlertPhotoLayout.this.lambda$onMenuItemClick$14$ChatAttachAlertPhotoLayout(z, i);
                        }
                    });
                    return;
                }
            }
            this.parentAlert.applyCaption();
            this.parentAlert.delegate.didPressedButton(7, false, true, 0);
        } else if (i == 1) {
            ChatAttachAlert chatAttachAlert2 = this.parentAlert;
            if (chatAttachAlert2.editingMessageObject == null) {
                BaseFragment baseFragment3 = chatAttachAlert2.baseFragment;
                if ((baseFragment3 instanceof ChatActivity) && ((ChatActivity) baseFragment3).isInScheduleMode()) {
                    AlertsCreator.createScheduleDatePickerDialog(getContext(), ((ChatActivity) this.parentAlert.baseFragment).getDialogId(), new AlertsCreator.ScheduleDatePickerDelegate() {
                        public final void didSelectDate(boolean z, int i) {
                            ChatAttachAlertPhotoLayout.this.lambda$onMenuItemClick$15$ChatAttachAlertPhotoLayout(z, i);
                        }
                    });
                    return;
                }
            }
            this.parentAlert.applyCaption();
            this.parentAlert.delegate.didPressedButton(4, true, true, 0);
        } else if (i == 2) {
            try {
                if (!(this.parentAlert.baseFragment instanceof ChatActivity)) {
                    if (this.parentAlert.avatarPicker != 2) {
                        Intent intent = new Intent("android.intent.action.PICK");
                        intent.setType("image/*");
                        if (this.parentAlert.avatarPicker != 0) {
                            this.parentAlert.baseFragment.startActivityForResult(intent, 14);
                        } else {
                            this.parentAlert.baseFragment.startActivityForResult(intent, 1);
                        }
                        this.parentAlert.dismiss();
                    }
                }
                Intent intent2 = new Intent();
                intent2.setType("video/*");
                intent2.setAction("android.intent.action.GET_CONTENT");
                intent2.putExtra("android.intent.extra.sizeLimit", NUM);
                Intent intent3 = new Intent("android.intent.action.PICK");
                intent3.setType("image/*");
                Intent createChooser = Intent.createChooser(intent3, (CharSequence) null);
                createChooser.putExtra("android.intent.extra.INITIAL_INTENTS", new Intent[]{intent2});
                if (this.parentAlert.avatarPicker != 0) {
                    this.parentAlert.baseFragment.startActivityForResult(createChooser, 14);
                } else {
                    this.parentAlert.baseFragment.startActivityForResult(createChooser, 1);
                }
                this.parentAlert.dismiss();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        } else if (i >= 10) {
            MediaController.AlbumEntry albumEntry = this.dropDownAlbums.get(i - 10);
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

    public /* synthetic */ void lambda$onMenuItemClick$14$ChatAttachAlertPhotoLayout(boolean z, int i) {
        this.parentAlert.applyCaption();
        this.parentAlert.delegate.didPressedButton(7, false, z, i);
    }

    public /* synthetic */ void lambda$onMenuItemClick$15$ChatAttachAlertPhotoLayout(boolean z, int i) {
        this.parentAlert.applyCaption();
        this.parentAlert.delegate.didPressedButton(4, true, z, i);
    }

    /* access modifiers changed from: package-private */
    public int getSelectedItemsCount() {
        return selectedPhotosOrder.size();
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:10:0x0033  */
    /* JADX WARNING: Removed duplicated region for block: B:12:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onSelectedItemsCountChanged(int r6) {
        /*
            r5 = this;
            r0 = 2
            r1 = 0
            r2 = 1
            if (r6 <= r2) goto L_0x0012
            org.telegram.ui.Components.ChatAttachAlert r3 = r5.parentAlert
            org.telegram.messenger.MessageObject r4 = r3.editingMessageObject
            if (r4 == 0) goto L_0x000c
            goto L_0x0012
        L_0x000c:
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r3.selectedMenuItem
            r2.showSubItem(r1)
            goto L_0x0031
        L_0x0012:
            org.telegram.ui.Components.ChatAttachAlert r3 = r5.parentAlert
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r3.selectedMenuItem
            r3.hideSubItem(r1)
            if (r6 != 0) goto L_0x002a
            org.telegram.ui.Components.ChatAttachAlert r1 = r5.parentAlert
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r1.selectedMenuItem
            r1.showSubItem(r0)
            org.telegram.ui.Components.ChatAttachAlert r1 = r5.parentAlert
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r1.selectedMenuItem
            r1.hideSubItem(r2)
            goto L_0x0031
        L_0x002a:
            org.telegram.ui.Components.ChatAttachAlert r1 = r5.parentAlert
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r1.selectedMenuItem
            r1.showSubItem(r2)
        L_0x0031:
            if (r6 == 0) goto L_0x003a
            org.telegram.ui.Components.ChatAttachAlert r6 = r5.parentAlert
            org.telegram.ui.ActionBar.ActionBarMenuItem r6 = r6.selectedMenuItem
            r6.hideSubItem(r0)
        L_0x003a:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlertPhotoLayout.onSelectedItemsCountChanged(int):void");
    }

    /* access modifiers changed from: package-private */
    public void applyCaption(String str) {
        Object obj = selectedPhotos.get(Integer.valueOf(((Integer) selectedPhotosOrder.get(0)).intValue()));
        if (obj instanceof MediaController.PhotoEntry) {
            ((MediaController.PhotoEntry) obj).caption = str;
        } else if (obj instanceof MediaController.SearchImage) {
            ((MediaController.SearchImage) obj).caption = str;
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
        View childAt = this.gridView.getChildAt(0);
        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.gridView.findContainingViewHolder(childAt);
        int top = childAt.getTop();
        int dp = AndroidUtilities.dp(7.0f);
        if (top < AndroidUtilities.dp(7.0f) || holder == null || holder.getAdapterPosition() != 0) {
            top = dp;
        }
        this.progressView.setTranslationY((float) (((((getMeasuredHeight() - top) - AndroidUtilities.dp(50.0f)) - this.progressView.getMeasuredHeight()) / 2) + top));
        this.gridView.setTopGlowOffset(top);
        return top;
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
        Theme.setDrawableColor(this.cameraDrawable, Theme.getColor("dialogCameraIcon"));
        this.progressView.setTextColor(Theme.getColor("emptyListPlaceholder"));
        this.gridView.setGlowColor(Theme.getColor("dialogScrollGlow"));
        RecyclerView.ViewHolder findViewHolderForAdapterPosition = this.gridView.findViewHolderForAdapterPosition(0);
        if (findViewHolderForAdapterPosition != null) {
            View view = findViewHolderForAdapterPosition.itemView;
            if (view instanceof PhotoAttachCameraCell) {
                ((PhotoAttachCameraCell) view).getImageView().setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogCameraIcon"), PorterDuff.Mode.MULTIPLY));
            }
        }
        this.dropDown.setTextColor(Theme.getColor("dialogTextBlack"));
        this.dropDownContainer.setPopupItemsColor(Theme.getColor("actionBarDefaultSubmenuItem"), false);
        this.dropDownContainer.setPopupItemsColor(Theme.getColor("actionBarDefaultSubmenuItem"), true);
        this.dropDownContainer.redrawPopup(Theme.getColor("actionBarDefaultSubmenuBackground"));
        Theme.setDrawableColor(this.dropDownDrawable, Theme.getColor("dialogTextBlack"));
    }

    /* access modifiers changed from: package-private */
    public void onInit(boolean z) {
        this.mediaEnabled = z;
        CameraView cameraView2 = this.cameraView;
        float f = 1.0f;
        if (cameraView2 != null) {
            cameraView2.setAlpha(z ? 1.0f : 0.2f);
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
        ChatAttachAlert chatAttachAlert = this.parentAlert;
        BaseFragment baseFragment = chatAttachAlert.baseFragment;
        boolean z2 = true;
        if (baseFragment instanceof ChatActivity) {
            this.galleryAlbumEntry = MediaController.allMediaAlbumEntry;
            if (this.mediaEnabled) {
                this.progressView.setText(LocaleController.getString("NoPhotos", NUM));
            } else {
                TLRPC$Chat currentChat = ((ChatActivity) baseFragment).getCurrentChat();
                if (ChatObject.isActionBannedByDefault(currentChat, 7)) {
                    this.progressView.setText(LocaleController.getString("GlobalAttachMediaRestricted", NUM));
                } else if (AndroidUtilities.isBannedForever(currentChat.banned_rights)) {
                    this.progressView.setText(LocaleController.formatString("AttachMediaRestrictedForever", NUM, new Object[0]));
                } else {
                    this.progressView.setText(LocaleController.formatString("AttachMediaRestricted", NUM, LocaleController.formatDateForBan((long) currentChat.banned_rights.until_date)));
                }
            }
        } else if (chatAttachAlert.avatarPicker == 2) {
            this.galleryAlbumEntry = MediaController.allMediaAlbumEntry;
        } else {
            this.galleryAlbumEntry = MediaController.allPhotosAlbumEntry;
        }
        if (Build.VERSION.SDK_INT >= 23) {
            if (this.parentAlert.baseFragment.getParentActivity().checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == 0) {
                z2 = false;
            }
            this.noGalleryPermissions = z2;
        }
        if (this.galleryAlbumEntry != null) {
            for (int i = 0; i < Math.min(100, this.galleryAlbumEntry.photos.size()); i++) {
                this.galleryAlbumEntry.photos.get(i).reset();
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
        boolean z;
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
        return !z;
    }

    /* access modifiers changed from: package-private */
    public void onButtonsTranslationYUpdated() {
        checkCameraViewPosition();
    }

    public void setTranslationY(float f) {
        if (this.parentAlert.getSheetAnimationType() == 1) {
            float f2 = (f / 40.0f) * -0.1f;
            int childCount = this.gridView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = this.gridView.getChildAt(i);
                if (childAt instanceof PhotoAttachCameraCell) {
                    PhotoAttachCameraCell photoAttachCameraCell = (PhotoAttachCameraCell) childAt;
                    float f3 = 1.0f + f2;
                    photoAttachCameraCell.getImageView().setScaleX(f3);
                    photoAttachCameraCell.getImageView().setScaleY(f3);
                } else if (childAt instanceof PhotoAttachPhotoCell) {
                    PhotoAttachPhotoCell photoAttachPhotoCell = (PhotoAttachPhotoCell) childAt;
                    float f4 = 1.0f + f2;
                    photoAttachPhotoCell.getCheckBox().setScaleX(f4);
                    photoAttachPhotoCell.getCheckBox().setScaleY(f4);
                }
            }
        }
        super.setTranslationY(f);
        this.parentAlert.getSheetContainer().invalidate();
        checkCameraViewPosition();
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
        int childCount = this.gridView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = this.gridView.getChildAt(i);
            if (childAt instanceof PhotoAttachCameraCell) {
                childAt.setVisibility(4);
                return;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void onHideShowProgress(float f) {
        CameraView cameraView2 = this.cameraView;
        if (cameraView2 != null) {
            cameraView2.setAlpha(f);
            this.cameraIcon.setAlpha(f);
            if (f != 0.0f && this.cameraView.getVisibility() != 0) {
                this.cameraView.setVisibility(0);
                this.cameraIcon.setVisibility(0);
            } else if (f == 0.0f && this.cameraView.getVisibility() != 4) {
                this.cameraView.setVisibility(4);
                this.cameraIcon.setVisibility(4);
            }
        }
    }

    public void onHide() {
        this.isHidden = true;
        this.dropDownContainer.setVisibility(8);
        int childCount = this.gridView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = this.gridView.getChildAt(i);
            if (childAt instanceof PhotoAttachCameraCell) {
                childAt.setVisibility(0);
                saveLastCameraBitmap();
                ((PhotoAttachCameraCell) childAt).updateBitmap();
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
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int i5 = i3 - i;
        if (this.lastNotifyWidth != i5) {
            this.lastNotifyWidth = i5;
            PhotoAttachAdapter photoAttachAdapter = this.adapter;
            if (photoAttachAdapter != null) {
                photoAttachAdapter.notifyDataSetChanged();
            }
        }
        super.onLayout(z, i, i2, i3, i4);
        checkCameraViewPosition();
    }

    /* JADX WARNING: Removed duplicated region for block: B:21:0x00c0  */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x00c9  */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x00e6  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onPreMeasure(int r6, int r7) {
        /*
            r5 = this;
            r0 = 1
            r5.ignoreLayout = r0
            boolean r1 = org.telegram.messenger.AndroidUtilities.isTablet()
            r2 = 4
            if (r1 == 0) goto L_0x000d
            r5.itemsPerRow = r2
            goto L_0x001b
        L_0x000d:
            android.graphics.Point r1 = org.telegram.messenger.AndroidUtilities.displaySize
            int r3 = r1.x
            int r1 = r1.y
            if (r3 <= r1) goto L_0x0018
            r5.itemsPerRow = r2
            goto L_0x001b
        L_0x0018:
            r1 = 3
            r5.itemsPerRow = r1
        L_0x001b:
            android.view.ViewGroup$LayoutParams r1 = r5.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r1 = (android.widget.FrameLayout.LayoutParams) r1
            int r2 = org.telegram.ui.ActionBar.ActionBar.getCurrentActionBarHeight()
            r1.topMargin = r2
            r1 = 1094713344(0x41400000, float:12.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r6 = r6 - r1
            r1 = 1092616192(0x41200000, float:10.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r6 = r6 - r1
            int r1 = r5.itemsPerRow
            int r6 = r6 / r1
            r5.itemSize = r6
            int r1 = r5.lastItemSize
            if (r1 == r6) goto L_0x0048
            r5.lastItemSize = r6
            org.telegram.ui.Components.-$$Lambda$ChatAttachAlertPhotoLayout$rujWuz-UdhOjg08Yq2qLrxcsJNY r6 = new org.telegram.ui.Components.-$$Lambda$ChatAttachAlertPhotoLayout$rujWuz-UdhOjg08Yq2qLrxcsJNY
            r6.<init>()
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r6)
        L_0x0048:
            androidx.recyclerview.widget.GridLayoutManager r6 = r5.layoutManager
            int r1 = r5.itemSize
            int r2 = r5.itemsPerRow
            int r1 = r1 * r2
            r2 = 1084227584(0x40a00000, float:5.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r4 = r5.itemsPerRow
            int r4 = r4 - r0
            int r3 = r3 * r4
            int r1 = r1 + r3
            int r1 = java.lang.Math.max(r0, r1)
            r6.setSpanCount(r1)
            org.telegram.ui.Components.ChatAttachAlertPhotoLayout$PhotoAttachAdapter r6 = r5.adapter
            int r6 = r6.getItemCount()
            int r6 = r6 - r0
            float r6 = (float) r6
            int r1 = r5.itemsPerRow
            float r1 = (float) r1
            float r6 = r6 / r1
            double r3 = (double) r6
            double r3 = java.lang.Math.ceil(r3)
            int r6 = (int) r3
            int r1 = r5.itemSize
            int r1 = r1 * r6
            int r6 = r6 - r0
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r6 = r6 * r0
            int r1 = r1 + r6
            int r6 = r7 - r1
            int r0 = org.telegram.ui.ActionBar.ActionBar.getCurrentActionBarHeight()
            int r6 = r6 - r0
            r0 = 1114636288(0x42700000, float:60.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r6 = r6 - r0
            r0 = 0
            int r6 = java.lang.Math.max(r0, r6)
            int r1 = r5.gridExtraSpace
            if (r1 == r6) goto L_0x009f
            r5.gridExtraSpace = r6
            org.telegram.ui.Components.ChatAttachAlertPhotoLayout$PhotoAttachAdapter r6 = r5.adapter
            r6.notifyDataSetChanged()
        L_0x009f:
            boolean r6 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r6 != 0) goto L_0x00b3
            android.graphics.Point r6 = org.telegram.messenger.AndroidUtilities.displaySize
            int r1 = r6.x
            int r6 = r6.y
            if (r1 <= r6) goto L_0x00b3
            float r6 = (float) r7
            r7 = 1080033280(0x40600000, float:3.5)
            float r6 = r6 / r7
            int r6 = (int) r6
            goto L_0x00b7
        L_0x00b3:
            int r7 = r7 / 5
            int r6 = r7 * 2
        L_0x00b7:
            r7 = 1112539136(0x42500000, float:52.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r6 = r6 - r7
            if (r6 >= 0) goto L_0x00c1
            r6 = 0
        L_0x00c1:
            org.telegram.ui.Components.RecyclerListView r7 = r5.gridView
            int r7 = r7.getPaddingTop()
            if (r7 == r6) goto L_0x00de
            org.telegram.ui.Components.RecyclerListView r7 = r5.gridView
            r1 = 1086324736(0x40CLASSNAME, float:6.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r3 = 1111490560(0x42400000, float:48.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r7.setPadding(r2, r6, r1, r3)
        L_0x00de:
            android.widget.TextView r6 = r5.dropDown
            boolean r7 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r7 != 0) goto L_0x00f1
            android.graphics.Point r7 = org.telegram.messenger.AndroidUtilities.displaySize
            int r1 = r7.x
            int r7 = r7.y
            if (r1 <= r7) goto L_0x00f1
            r7 = 1099956224(0x41900000, float:18.0)
            goto L_0x00f3
        L_0x00f1:
            r7 = 1101004800(0x41a00000, float:20.0)
        L_0x00f3:
            r6.setTextSize(r7)
            r5.ignoreLayout = r0
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlertPhotoLayout.onPreMeasure(int, int):void");
    }

    public /* synthetic */ void lambda$onPreMeasure$16$ChatAttachAlertPhotoLayout() {
        this.adapter.notifyDataSetChanged();
    }

    /* access modifiers changed from: package-private */
    public boolean canDismissWithTouchOutside() {
        return !this.cameraOpened;
    }

    /* access modifiers changed from: package-private */
    public void onContainerTranslationUpdated() {
        checkCameraViewPosition();
    }

    /* access modifiers changed from: package-private */
    public void onOpenAnimationEnd() {
        checkCamera(true);
    }

    /* access modifiers changed from: package-private */
    public void onDismissWithButtonClick(int i) {
        hideCamera((i == 0 || i == 2) ? false : true);
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

    public boolean onSheetKeyDown(int i, KeyEvent keyEvent) {
        if (!this.cameraOpened) {
            return false;
        }
        if (i != 24 && i != 25) {
            return false;
        }
        this.shutterButton.getDelegate().shutterReleased();
        return true;
    }

    public boolean onContainerViewTouchEvent(MotionEvent motionEvent) {
        if (this.cameraAnimationInProgress) {
            return true;
        }
        if (this.cameraOpened) {
            return processTouchEvent(motionEvent);
        }
        return false;
    }

    public boolean onCustomMeasure(View view, int i, int i2) {
        boolean z = i < i2;
        FrameLayout frameLayout = this.cameraIcon;
        if (view == frameLayout) {
            frameLayout.measure(View.MeasureSpec.makeMeasureSpec(this.itemSize, NUM), View.MeasureSpec.makeMeasureSpec((this.itemSize - this.cameraViewOffsetBottomY) - this.cameraViewOffsetY, NUM));
            return true;
        }
        CameraView cameraView2 = this.cameraView;
        if (view != cameraView2) {
            FrameLayout frameLayout2 = this.cameraPanel;
            if (view == frameLayout2) {
                if (z) {
                    frameLayout2.measure(View.MeasureSpec.makeMeasureSpec(i, NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(126.0f), NUM));
                } else {
                    frameLayout2.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(126.0f), NUM), View.MeasureSpec.makeMeasureSpec(i2, NUM));
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

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.albumsDidLoad) {
            if (this.adapter != null) {
                ChatAttachAlert chatAttachAlert = this.parentAlert;
                if ((chatAttachAlert.baseFragment instanceof ChatActivity) || chatAttachAlert.avatarPicker == 2) {
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
        } else if (i == NotificationCenter.cameraInitied) {
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
            if (Build.VERSION.SDK_INT >= 21 && this == ChatAttachAlertPhotoLayout.this.adapter) {
                photoAttachPhotoCell.setOutlineProvider(new ViewOutlineProvider() {
                    public void getOutline(View view, Outline outline) {
                        int intValue = ((Integer) ((PhotoAttachPhotoCell) view).getTag()).intValue();
                        if (PhotoAttachAdapter.this.needCamera && ChatAttachAlertPhotoLayout.this.selectedAlbumEntry == ChatAttachAlertPhotoLayout.this.galleryAlbumEntry) {
                            intValue++;
                        }
                        if (intValue == 0) {
                            int dp = AndroidUtilities.dp(ChatAttachAlertPhotoLayout.this.parentAlert.cornerRadius * 8.0f);
                            outline.setRoundRect(0, 0, view.getMeasuredWidth() + dp, view.getMeasuredHeight() + dp, (float) dp);
                        } else if (intValue == ChatAttachAlertPhotoLayout.this.itemsPerRow - 1) {
                            int dp2 = AndroidUtilities.dp(ChatAttachAlertPhotoLayout.this.parentAlert.cornerRadius * 8.0f);
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
                    ChatAttachAlertPhotoLayout.PhotoAttachAdapter.this.lambda$createHolder$0$ChatAttachAlertPhotoLayout$PhotoAttachAdapter(photoAttachPhotoCell);
                }
            });
            return new RecyclerListView.Holder(photoAttachPhotoCell);
        }

        public /* synthetic */ void lambda$createHolder$0$ChatAttachAlertPhotoLayout$PhotoAttachAdapter(PhotoAttachPhotoCell photoAttachPhotoCell) {
            TLRPC$Chat currentChat;
            if (ChatAttachAlertPhotoLayout.this.mediaEnabled && ChatAttachAlertPhotoLayout.this.parentAlert.avatarPicker == 0) {
                int intValue = ((Integer) photoAttachPhotoCell.getTag()).intValue();
                MediaController.PhotoEntry photoEntry = photoAttachPhotoCell.getPhotoEntry();
                int i = 1;
                boolean z = !ChatAttachAlertPhotoLayout.selectedPhotos.containsKey(Integer.valueOf(photoEntry.imageId));
                if (z && ChatAttachAlertPhotoLayout.this.parentAlert.maxSelectedPhotos >= 0) {
                    int size = ChatAttachAlertPhotoLayout.selectedPhotos.size();
                    ChatAttachAlert chatAttachAlert = ChatAttachAlertPhotoLayout.this.parentAlert;
                    if (size >= chatAttachAlert.maxSelectedPhotos) {
                        if (chatAttachAlert.allowOrder) {
                            BaseFragment baseFragment = chatAttachAlert.baseFragment;
                            if ((baseFragment instanceof ChatActivity) && (currentChat = ((ChatActivity) baseFragment).getCurrentChat()) != null && !ChatObject.hasAdminRights(currentChat) && currentChat.slowmode_enabled && ChatAttachAlertPhotoLayout.this.alertOnlyOnce != 2) {
                                AlertsCreator.createSimpleAlert(ChatAttachAlertPhotoLayout.this.getContext(), LocaleController.getString("Slowmode", NUM), LocaleController.getString("SlowmodeSelectSendError", NUM)).show();
                                if (ChatAttachAlertPhotoLayout.this.alertOnlyOnce == 1) {
                                    int unused = ChatAttachAlertPhotoLayout.this.alertOnlyOnce = 2;
                                    return;
                                }
                                return;
                            }
                            return;
                        }
                        return;
                    }
                }
                int size2 = z ? ChatAttachAlertPhotoLayout.selectedPhotosOrder.size() : -1;
                ChatAttachAlert chatAttachAlert2 = ChatAttachAlertPhotoLayout.this.parentAlert;
                if (!(chatAttachAlert2.baseFragment instanceof ChatActivity) || !chatAttachAlert2.allowOrder) {
                    photoAttachPhotoCell.setChecked(-1, z, true);
                } else {
                    photoAttachPhotoCell.setChecked(size2, z, true);
                }
                int unused2 = ChatAttachAlertPhotoLayout.this.addToSelectedPhotos(photoEntry, intValue);
                if (this == ChatAttachAlertPhotoLayout.this.cameraAttachAdapter) {
                    if (ChatAttachAlertPhotoLayout.this.adapter.needCamera && ChatAttachAlertPhotoLayout.this.selectedAlbumEntry == ChatAttachAlertPhotoLayout.this.galleryAlbumEntry) {
                        intValue++;
                    }
                    ChatAttachAlertPhotoLayout.this.adapter.notifyItemChanged(intValue);
                } else {
                    ChatAttachAlertPhotoLayout.this.cameraAttachAdapter.notifyItemChanged(intValue);
                }
                ChatAttachAlert chatAttachAlert3 = ChatAttachAlertPhotoLayout.this.parentAlert;
                if (!z) {
                    i = 2;
                }
                chatAttachAlert3.updateCountButton(i);
            }
        }

        /* access modifiers changed from: private */
        public MediaController.PhotoEntry getPhoto(int i) {
            if (this.needCamera && ChatAttachAlertPhotoLayout.this.selectedAlbumEntry == ChatAttachAlertPhotoLayout.this.galleryAlbumEntry) {
                i--;
            }
            return ChatAttachAlertPhotoLayout.this.getPhotoEntryAtPosition(i);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            int itemViewType = viewHolder.getItemViewType();
            int i2 = 0;
            boolean z = true;
            if (itemViewType == 0) {
                if (this.needCamera && ChatAttachAlertPhotoLayout.this.selectedAlbumEntry == ChatAttachAlertPhotoLayout.this.galleryAlbumEntry) {
                    i--;
                }
                PhotoAttachPhotoCell photoAttachPhotoCell = (PhotoAttachPhotoCell) viewHolder.itemView;
                if (this == ChatAttachAlertPhotoLayout.this.adapter) {
                    photoAttachPhotoCell.setItemSize(ChatAttachAlertPhotoLayout.this.itemSize);
                } else {
                    photoAttachPhotoCell.setIsVertical(ChatAttachAlertPhotoLayout.this.cameraPhotoLayoutManager.getOrientation() == 1);
                }
                if (ChatAttachAlertPhotoLayout.this.parentAlert.avatarPicker != 0) {
                    photoAttachPhotoCell.getCheckBox().setVisibility(8);
                }
                MediaController.PhotoEntry access$000 = ChatAttachAlertPhotoLayout.this.getPhotoEntryAtPosition(i);
                boolean z2 = this.needCamera && ChatAttachAlertPhotoLayout.this.selectedAlbumEntry == ChatAttachAlertPhotoLayout.this.galleryAlbumEntry;
                if (i != getItemCount() - 1) {
                    z = false;
                }
                photoAttachPhotoCell.setPhotoEntry(access$000, z2, z);
                ChatAttachAlert chatAttachAlert = ChatAttachAlertPhotoLayout.this.parentAlert;
                if (!(chatAttachAlert.baseFragment instanceof ChatActivity) || !chatAttachAlert.allowOrder) {
                    photoAttachPhotoCell.setChecked(-1, ChatAttachAlertPhotoLayout.selectedPhotos.containsKey(Integer.valueOf(access$000.imageId)), false);
                } else {
                    photoAttachPhotoCell.setChecked(ChatAttachAlertPhotoLayout.selectedPhotosOrder.indexOf(Integer.valueOf(access$000.imageId)), ChatAttachAlertPhotoLayout.selectedPhotos.containsKey(Integer.valueOf(access$000.imageId)), false);
                }
                photoAttachPhotoCell.getImageView().setTag(Integer.valueOf(i));
                photoAttachPhotoCell.setTag(Integer.valueOf(i));
            } else if (itemViewType == 1) {
                PhotoAttachCameraCell photoAttachCameraCell = (PhotoAttachCameraCell) viewHolder.itemView;
                if (ChatAttachAlertPhotoLayout.this.cameraView == null || !ChatAttachAlertPhotoLayout.this.cameraView.isInitied() || ChatAttachAlertPhotoLayout.this.isHidden) {
                    photoAttachCameraCell.setVisibility(0);
                } else {
                    photoAttachCameraCell.setVisibility(4);
                }
                photoAttachCameraCell.setItemSize(ChatAttachAlertPhotoLayout.this.itemSize);
            } else if (itemViewType == 3) {
                PhotoAttachPermissionCell photoAttachPermissionCell = (PhotoAttachPermissionCell) viewHolder.itemView;
                photoAttachPermissionCell.setItemSize(ChatAttachAlertPhotoLayout.this.itemSize);
                if (!this.needCamera || !ChatAttachAlertPhotoLayout.this.noCameraPermissions || i != 0) {
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
                                int dp = AndroidUtilities.dp(ChatAttachAlertPhotoLayout.this.parentAlert.cornerRadius * 8.0f);
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
                            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(ChatAttachAlertPhotoLayout.this.gridExtraSpace, NUM));
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
            if (!ChatAttachAlertPhotoLayout.this.mediaEnabled) {
                return 1;
            }
            if (!this.needCamera || ChatAttachAlertPhotoLayout.this.selectedAlbumEntry != ChatAttachAlertPhotoLayout.this.galleryAlbumEntry) {
                i = 0;
            }
            if (ChatAttachAlertPhotoLayout.this.noGalleryPermissions && this == ChatAttachAlertPhotoLayout.this.adapter) {
                i++;
            }
            int size = i + ChatAttachAlertPhotoLayout.cameraPhotos.size();
            if (ChatAttachAlertPhotoLayout.this.selectedAlbumEntry != null) {
                size += ChatAttachAlertPhotoLayout.this.selectedAlbumEntry.photos.size();
            }
            if (this == ChatAttachAlertPhotoLayout.this.adapter) {
                size++;
            }
            this.itemsCount = size;
            return size;
        }

        public int getItemViewType(int i) {
            if (!ChatAttachAlertPhotoLayout.this.mediaEnabled) {
                return 2;
            }
            if (this.needCamera && i == 0 && ChatAttachAlertPhotoLayout.this.selectedAlbumEntry == ChatAttachAlertPhotoLayout.this.galleryAlbumEntry) {
                return ChatAttachAlertPhotoLayout.this.noCameraPermissions ? 3 : 1;
            }
            if (this == ChatAttachAlertPhotoLayout.this.adapter && i == this.itemsCount - 1) {
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
