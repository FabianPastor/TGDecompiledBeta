package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.os.Build.VERSION;
import android.provider.Settings.System;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.TextPaint;
import android.text.TextUtils.TruncateAt;
import android.util.Property;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import androidx.annotation.Keep;
import androidx.exifinterface.media.ExifInterface;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.OnScrollListener;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver.BitmapHolder;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaController.AlbumEntry;
import org.telegram.messenger.MediaController.PhotoEntry;
import org.telegram.messenger.MediaController.SearchImage;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.messenger.camera.CameraController;
import org.telegram.messenger.camera.CameraView;
import org.telegram.messenger.camera.CameraView.CameraViewDelegate;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.TL_topPeer;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.ActionBarPopupWindow.ActionBarPopupWindowLayout;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.BottomSheet.BottomSheetDelegateInterface;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.PhotoAttachCameraCell;
import org.telegram.ui.Cells.PhotoAttachPermissionCell;
import org.telegram.ui.Cells.PhotoAttachPhotoCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AnimationProperties.FloatProperty;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.Components.ShutterButton.ShutterButtonDelegate;
import org.telegram.ui.Components.ShutterButton.State;
import org.telegram.ui.PhotoViewer;
import org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider;
import org.telegram.ui.PhotoViewer.PhotoViewerProvider;
import org.telegram.ui.PhotoViewer.PlaceProviderObject;

public class ChatAttachAlert extends BottomSheet implements NotificationCenterDelegate, BottomSheetDelegateInterface {
    private static ArrayList<Object> cameraPhotos = new ArrayList();
    private static final int compress = 1;
    private static final int group = 0;
    private static int lastImageId = -1;
    private static boolean mediaFromExternalCamera;
    private static HashMap<Object, Object> selectedPhotos = new HashMap();
    private static ArrayList<Object> selectedPhotosOrder = new ArrayList();
    private final Property<ChatAttachAlert, Float> ATTACH_ALERT_PROGRESS = new FloatProperty<ChatAttachAlert>("openProgress") {
        private float openProgress;

        public void setValue(ChatAttachAlert chatAttachAlert, float f) {
            int childCount = ChatAttachAlert.this.buttonsRecyclerView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                float f2;
                float f3 = ((float) (3 - i)) * 32.0f;
                View childAt = ChatAttachAlert.this.buttonsRecyclerView.getChildAt(i);
                if (f > f3) {
                    f3 = f - f3;
                    f2 = 1.0f;
                    if (f3 <= 200.0f) {
                        f3 /= 200.0f;
                        f2 = CubicBezierInterpolator.EASE_OUT.getInterpolation(f3) * 1.1f;
                        childAt.setAlpha(CubicBezierInterpolator.EASE_BOTH.getInterpolation(f3));
                    } else {
                        childAt.setAlpha(1.0f);
                        f3 -= 200.0f;
                        if (f3 <= 100.0f) {
                            f2 = 1.1f - (CubicBezierInterpolator.EASE_IN.getInterpolation(f3 / 100.0f) * 0.1f);
                        }
                    }
                } else {
                    f2 = 0.0f;
                }
                if (childAt instanceof AttachButton) {
                    AttachButton attachButton = (AttachButton) childAt;
                    attachButton.textView.setScaleX(f2);
                    attachButton.textView.setScaleY(f2);
                    attachButton.imageView.setScaleX(f2);
                    attachButton.imageView.setScaleY(f2);
                } else if (childAt instanceof AttachBotButton) {
                    AttachBotButton attachBotButton = (AttachBotButton) childAt;
                    attachBotButton.nameTextView.setScaleX(f2);
                    attachBotButton.nameTextView.setScaleY(f2);
                    attachBotButton.imageView.setScaleX(f2);
                    attachBotButton.imageView.setScaleY(f2);
                }
            }
        }

        public Float get(ChatAttachAlert chatAttachAlert) {
            return Float.valueOf(this.openProgress);
        }
    };
    private ActionBar actionBar;
    private AnimatorSet actionBarAnimation;
    private View actionBarShadow;
    private PhotoAttachAdapter adapter;
    private boolean allowOrder = true;
    private int[] animateCameraValues = new int[5];
    private AnimatorSet animatorSet;
    private int attachItemSize = AndroidUtilities.dp(85.0f);
    private BaseFragment baseFragment;
    private boolean buttonPressed;
    private ButtonsAdapter buttonsAdapter;
    private LinearLayoutManager buttonsLayoutManager;
    private RecyclerListView buttonsRecyclerView;
    private boolean cameraAnimationInProgress;
    private PhotoAttachAdapter cameraAttachAdapter;
    private Drawable cameraDrawable;
    private FrameLayout cameraIcon;
    private AnimatorSet cameraInitAnimation;
    private boolean cameraInitied;
    private float cameraOpenProgress;
    private boolean cameraOpened;
    private FrameLayout cameraPanel;
    private LinearLayoutManager cameraPhotoLayoutManager;
    private RecyclerListView cameraPhotoRecyclerView;
    private boolean cameraPhotoRecyclerViewIgnoreLayout;
    private CameraView cameraView;
    private int[] cameraViewLocation = new int[2];
    private int cameraViewOffsetBottomY;
    private int cameraViewOffsetX;
    private int cameraViewOffsetY;
    private float cameraZoom;
    private boolean cancelTakingPhotos;
    private EditTextEmoji commentTextView;
    private float cornerRadius = 1.0f;
    private TextView counterTextView;
    private int currentAccount = UserConfig.selectedAccount;
    private int currentSelectedCount;
    private DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();
    private ChatAttachViewDelegate delegate;
    private boolean deviceHasGoodCamera;
    private boolean dragging;
    private TextView dropDown;
    private ArrayList<AlbumEntry> dropDownAlbums;
    private ActionBarMenuItem dropDownContainer;
    private Drawable dropDownDrawable;
    private MessageObject editingMessageObject;
    private boolean enterCommentEventSent;
    private boolean flashAnimationInProgress;
    private ImageView[] flashModeButton = new ImageView[2];
    private FrameLayout frameLayout2;
    private AlbumEntry galleryAlbumEntry;
    private int gridExtraSpace;
    private RecyclerListView gridView;
    private Rect hitRect = new Rect();
    private DecelerateInterpolator interpolator = new DecelerateInterpolator(1.5f);
    private ActionBarMenuSubItem itemCell;
    private int itemSize = AndroidUtilities.dp(80.0f);
    private int itemsPerRow = 3;
    private int lastItemSize = this.itemSize;
    private float lastY;
    private GridLayoutManager layoutManager;
    private boolean loading = true;
    private int maxSelectedPhotos = -1;
    private boolean maybeStartDraging;
    private boolean mediaCaptured;
    private boolean mediaEnabled = true;
    private AnimatorSet menuAnimator;
    private boolean menuShowed;
    private boolean noCameraPermissions;
    private boolean noGalleryPermissions;
    private boolean openWithFrontFaceCamera;
    private Paint paint = new Paint(1);
    private boolean paused;
    private PhotoViewerProvider photoViewerProvider = new BasePhotoProvider() {
        public boolean cancelButtonPressed() {
            return false;
        }

        public PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, FileLocation fileLocation, int i, boolean z) {
            PhotoAttachPhotoCell access$1000 = ChatAttachAlert.this.getCellForIndex(i);
            if (access$1000 == null) {
                return null;
            }
            int[] iArr = new int[2];
            access$1000.getImageView().getLocationInWindow(iArr);
            if (VERSION.SDK_INT < 26) {
                iArr[0] = iArr[0] - ChatAttachAlert.this.getLeftInset();
            }
            PlaceProviderObject placeProviderObject = new PlaceProviderObject();
            placeProviderObject.viewX = iArr[0];
            placeProviderObject.viewY = iArr[1];
            placeProviderObject.parentView = ChatAttachAlert.this.gridView;
            placeProviderObject.imageReceiver = access$1000.getImageView().getImageReceiver();
            placeProviderObject.thumb = placeProviderObject.imageReceiver.getBitmapSafe();
            placeProviderObject.scale = access$1000.getScale();
            access$1000.showCheck(false);
            return placeProviderObject;
        }

        public void updatePhotoAtIndex(int i) {
            PhotoAttachPhotoCell access$1000 = ChatAttachAlert.this.getCellForIndex(i);
            if (access$1000 != null) {
                access$1000.getImageView().setOrientation(0, true);
                PhotoEntry access$000 = ChatAttachAlert.this.getPhotoEntryAtPosition(i);
                if (access$000 != null) {
                    if (access$000.thumbPath != null) {
                        access$1000.getImageView().setImage(access$000.thumbPath, null, Theme.chat_attachEmptyDrawable);
                    } else if (access$000.path != null) {
                        access$1000.getImageView().setOrientation(access$000.orientation, true);
                        String str = ":";
                        BackupImageView imageView;
                        StringBuilder stringBuilder;
                        if (access$000.isVideo) {
                            imageView = access$1000.getImageView();
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("vthumb://");
                            stringBuilder.append(access$000.imageId);
                            stringBuilder.append(str);
                            stringBuilder.append(access$000.path);
                            imageView.setImage(stringBuilder.toString(), null, Theme.chat_attachEmptyDrawable);
                        } else {
                            imageView = access$1000.getImageView();
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("thumb://");
                            stringBuilder.append(access$000.imageId);
                            stringBuilder.append(str);
                            stringBuilder.append(access$000.path);
                            imageView.setImage(stringBuilder.toString(), null, Theme.chat_attachEmptyDrawable);
                        }
                    } else {
                        access$1000.getImageView().setImageDrawable(Theme.chat_attachEmptyDrawable);
                    }
                }
            }
        }

        public BitmapHolder getThumbForPhoto(MessageObject messageObject, FileLocation fileLocation, int i) {
            PhotoAttachPhotoCell access$1000 = ChatAttachAlert.this.getCellForIndex(i);
            return access$1000 != null ? access$1000.getImageView().getImageReceiver().getBitmapSafe() : null;
        }

        public void willSwitchFromPhoto(MessageObject messageObject, FileLocation fileLocation, int i) {
            PhotoAttachPhotoCell access$1000 = ChatAttachAlert.this.getCellForIndex(i);
            if (access$1000 != null) {
                access$1000.showCheck(true);
            }
        }

        public void willHidePhotoViewer() {
            int childCount = ChatAttachAlert.this.gridView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = ChatAttachAlert.this.gridView.getChildAt(i);
                if (childAt instanceof PhotoAttachPhotoCell) {
                    ((PhotoAttachPhotoCell) childAt).showCheck(true);
                }
            }
        }

        public void sendButtonPressed(int i, VideoEditedInfo videoEditedInfo) {
            PhotoEntry access$000 = ChatAttachAlert.this.getPhotoEntryAtPosition(i);
            if (access$000 != null) {
                access$000.editedInfo = videoEditedInfo;
            }
            if (ChatAttachAlert.selectedPhotos.isEmpty() && access$000 != null) {
                ChatAttachAlert.this.addToSelectedPhotos(access$000, -1);
            }
            ChatAttachAlert.this.applyCaption();
            ChatAttachAlert.this.delegate.didPressedButton(7, true);
        }
    };
    private float pinchStartDistance;
    private boolean pollsEnabled = true;
    private boolean pressed;
    private EmptyTextProgressView progressView;
    private TextView recordTime;
    private RectF rect = new RectF();
    private boolean requestingPermissions;
    private int scrollOffsetY;
    private AlbumEntry selectedAlbumEntry;
    private View selectedCountView;
    private ActionBarMenuItem selectedMenuItem;
    private TextView selectedTextView;
    private ActionBarPopupWindowLayout sendPopupLayout;
    private ActionBarPopupWindow sendPopupWindow;
    private View shadow;
    private ShutterButton shutterButton;
    private SizeNotifierFrameLayout sizeNotifierFrameLayout;
    private ImageView switchCameraButton;
    private boolean takingPhoto;
    private TextPaint textPaint = new TextPaint(1);
    private TextView tooltipTextView;
    private Runnable videoRecordRunnable;
    private int videoRecordTime;
    private int[] viewPosition = new int[2];
    private ImageView writeButton;
    private FrameLayout writeButtonContainer;
    private Drawable writeButtonDrawable;
    private ZoomControlView zoomControlView;
    private boolean zoomWas;
    private boolean zooming;

    private class AttachBotButton extends FrameLayout {
        private AvatarDrawable avatarDrawable = new AvatarDrawable();
        private User currentUser;
        private BackupImageView imageView;
        private TextView nameTextView;

        public AttachBotButton(Context context) {
            super(context);
            this.imageView = new BackupImageView(context);
            this.imageView.setRoundRadius(AndroidUtilities.dp(25.0f));
            addView(this.imageView, LayoutHelper.createFrame(50, 50.0f, 49, 0.0f, 12.0f, 0.0f, 0.0f));
            if (VERSION.SDK_INT >= 21) {
                View view = new View(context);
                view.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("dialogButtonSelector"), 1, AndroidUtilities.dp(25.0f)));
                addView(view, LayoutHelper.createFrame(50, 50.0f, 49, 0.0f, 12.0f, 0.0f, 0.0f));
            }
            this.nameTextView = new TextView(context);
            this.nameTextView.setTextSize(1, 12.0f);
            this.nameTextView.setGravity(49);
            this.nameTextView.setLines(1);
            this.nameTextView.setSingleLine(true);
            this.nameTextView.setEllipsize(TruncateAt.END);
            addView(this.nameTextView, LayoutHelper.createFrame(-1, -2.0f, 51, 6.0f, 66.0f, 6.0f, 0.0f));
        }

        /* Access modifiers changed, original: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(ChatAttachAlert.this.attachItemSize, NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(100.0f), NUM));
        }

        public void setUser(User user) {
            if (user != null) {
                this.nameTextView.setTextColor(Theme.getColor("dialogTextGray2"));
                this.currentUser = user;
                this.nameTextView.setText(ContactsController.formatName(user.first_name, user.last_name));
                this.avatarDrawable.setInfo(user);
                this.imageView.setImage(ImageLocation.getForUser(user, false), "50_50", this.avatarDrawable, (Object) user);
                requestLayout();
            }
        }
    }

    private class AttachButton extends FrameLayout {
        private ImageView imageView;
        private TextView textView;

        public boolean hasOverlappingRendering() {
            return false;
        }

        public AttachButton(Context context) {
            super(context);
            this.imageView = new ImageView(context);
            this.imageView.setScaleType(ScaleType.CENTER);
            if (VERSION.SDK_INT >= 21) {
                this.imageView.setImageDrawable(Theme.createSelectorDrawable(Theme.getColor("dialogButtonSelector"), 1, AndroidUtilities.dp(25.0f)));
            }
            addView(this.imageView, LayoutHelper.createFrame(50, 50.0f, 49, 0.0f, 12.0f, 0.0f, 0.0f));
            this.textView = new TextView(context);
            this.textView.setMaxLines(2);
            this.textView.setGravity(1);
            this.textView.setEllipsize(TruncateAt.END);
            this.textView.setTextColor(Theme.getColor("dialogTextGray2"));
            this.textView.setTextSize(1, 12.0f);
            this.textView.setLineSpacing((float) (-AndroidUtilities.dp(2.0f)), 1.0f);
            addView(this.textView, LayoutHelper.createFrame(-1, -2.0f, 51, 0.0f, 66.0f, 0.0f, 0.0f));
        }

        /* Access modifiers changed, original: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(ChatAttachAlert.this.attachItemSize, NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(92.0f), NUM));
        }

        public void setTextAndIcon(CharSequence charSequence, Drawable drawable) {
            this.textView.setText(charSequence);
            this.imageView.setBackgroundDrawable(drawable);
        }
    }

    public interface ChatAttachViewDelegate {
        void didPressedButton(int i, boolean z);

        void didSelectBot(User user);

        View getRevealView();

        void needEnterComment();

        void onCameraOpened();
    }

    private class InnerAnimator {
        private AnimatorSet animatorSet;
        private float startRadius;

        private InnerAnimator() {
        }
    }

    private class BasePhotoProvider extends EmptyPhotoViewerProvider {
        private BasePhotoProvider() {
        }

        /* synthetic */ BasePhotoProvider(ChatAttachAlert chatAttachAlert, AnonymousClass1 anonymousClass1) {
            this();
        }

        public boolean isPhotoChecked(int i) {
            PhotoEntry access$000 = ChatAttachAlert.this.getPhotoEntryAtPosition(i);
            return access$000 != null && ChatAttachAlert.selectedPhotos.containsKey(Integer.valueOf(access$000.imageId));
        }

        /* JADX WARNING: Removed duplicated region for block: B:42:0x00e0  */
        /* JADX WARNING: Removed duplicated region for block: B:29:0x00a0  */
        /* JADX WARNING: Removed duplicated region for block: B:42:0x00e0  */
        public int setPhotoChecked(int r9, org.telegram.messenger.VideoEditedInfo r10) {
            /*
            r8 = this;
            r0 = org.telegram.ui.Components.ChatAttachAlert.this;
            r0 = r0.maxSelectedPhotos;
            r1 = -1;
            if (r0 < 0) goto L_0x0020;
        L_0x0009:
            r0 = org.telegram.ui.Components.ChatAttachAlert.selectedPhotos;
            r0 = r0.size();
            r2 = org.telegram.ui.Components.ChatAttachAlert.this;
            r2 = r2.maxSelectedPhotos;
            if (r0 < r2) goto L_0x0020;
        L_0x0019:
            r0 = r8.isPhotoChecked(r9);
            if (r0 != 0) goto L_0x0020;
        L_0x001f:
            return r1;
        L_0x0020:
            r0 = org.telegram.ui.Components.ChatAttachAlert.this;
            r0 = r0.getPhotoEntryAtPosition(r9);
            if (r0 != 0) goto L_0x0029;
        L_0x0028:
            return r1;
        L_0x0029:
            r2 = org.telegram.ui.Components.ChatAttachAlert.this;
            r2 = r2.addToSelectedPhotos(r0, r1);
            r3 = 1;
            r4 = 0;
            if (r2 != r1) goto L_0x0044;
        L_0x0033:
            r2 = org.telegram.ui.Components.ChatAttachAlert.selectedPhotosOrder;
            r5 = r0.imageId;
            r5 = java.lang.Integer.valueOf(r5);
            r2 = r2.indexOf(r5);
            r5 = r2;
            r2 = 1;
            goto L_0x0049;
        L_0x0044:
            r5 = 0;
            r0.editedInfo = r5;
            r5 = r2;
            r2 = 0;
        L_0x0049:
            r0.editedInfo = r10;
            r10 = org.telegram.ui.Components.ChatAttachAlert.this;
            r10 = r10.gridView;
            r10 = r10.getChildCount();
            r0 = 0;
        L_0x0056:
            if (r0 >= r10) goto L_0x0093;
        L_0x0058:
            r6 = org.telegram.ui.Components.ChatAttachAlert.this;
            r6 = r6.gridView;
            r6 = r6.getChildAt(r0);
            r7 = r6 instanceof org.telegram.ui.Cells.PhotoAttachPhotoCell;
            if (r7 == 0) goto L_0x0090;
        L_0x0066:
            r7 = r6.getTag();
            r7 = (java.lang.Integer) r7;
            r7 = r7.intValue();
            if (r7 != r9) goto L_0x0090;
        L_0x0072:
            r10 = org.telegram.ui.Components.ChatAttachAlert.this;
            r10 = r10.baseFragment;
            r10 = r10 instanceof org.telegram.ui.ChatActivity;
            if (r10 == 0) goto L_0x008a;
        L_0x007c:
            r10 = org.telegram.ui.Components.ChatAttachAlert.this;
            r10 = r10.allowOrder;
            if (r10 == 0) goto L_0x008a;
        L_0x0084:
            r6 = (org.telegram.ui.Cells.PhotoAttachPhotoCell) r6;
            r6.setChecked(r5, r2, r4);
            goto L_0x0093;
        L_0x008a:
            r6 = (org.telegram.ui.Cells.PhotoAttachPhotoCell) r6;
            r6.setChecked(r1, r2, r4);
            goto L_0x0093;
        L_0x0090:
            r0 = r0 + 1;
            goto L_0x0056;
        L_0x0093:
            r10 = org.telegram.ui.Components.ChatAttachAlert.this;
            r10 = r10.cameraPhotoRecyclerView;
            r10 = r10.getChildCount();
            r0 = 0;
        L_0x009e:
            if (r0 >= r10) goto L_0x00db;
        L_0x00a0:
            r6 = org.telegram.ui.Components.ChatAttachAlert.this;
            r6 = r6.cameraPhotoRecyclerView;
            r6 = r6.getChildAt(r0);
            r7 = r6 instanceof org.telegram.ui.Cells.PhotoAttachPhotoCell;
            if (r7 == 0) goto L_0x00d8;
        L_0x00ae:
            r7 = r6.getTag();
            r7 = (java.lang.Integer) r7;
            r7 = r7.intValue();
            if (r7 != r9) goto L_0x00d8;
        L_0x00ba:
            r9 = org.telegram.ui.Components.ChatAttachAlert.this;
            r9 = r9.baseFragment;
            r9 = r9 instanceof org.telegram.ui.ChatActivity;
            if (r9 == 0) goto L_0x00d2;
        L_0x00c4:
            r9 = org.telegram.ui.Components.ChatAttachAlert.this;
            r9 = r9.allowOrder;
            if (r9 == 0) goto L_0x00d2;
        L_0x00cc:
            r6 = (org.telegram.ui.Cells.PhotoAttachPhotoCell) r6;
            r6.setChecked(r5, r2, r4);
            goto L_0x00db;
        L_0x00d2:
            r6 = (org.telegram.ui.Cells.PhotoAttachPhotoCell) r6;
            r6.setChecked(r1, r2, r4);
            goto L_0x00db;
        L_0x00d8:
            r0 = r0 + 1;
            goto L_0x009e;
        L_0x00db:
            r9 = org.telegram.ui.Components.ChatAttachAlert.this;
            if (r2 == 0) goto L_0x00e0;
        L_0x00df:
            goto L_0x00e1;
        L_0x00e0:
            r3 = 2;
        L_0x00e1:
            r9.updatePhotosButton(r3);
            return r5;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlert$BasePhotoProvider.setPhotoChecked(int, org.telegram.messenger.VideoEditedInfo):int");
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

        public int getPhotoIndex(int i) {
            PhotoEntry access$000 = ChatAttachAlert.this.getPhotoEntryAtPosition(i);
            if (access$000 == null) {
                return -1;
            }
            return ChatAttachAlert.selectedPhotosOrder.indexOf(Integer.valueOf(access$000.imageId));
        }
    }

    private class ButtonsAdapter extends SelectionAdapter {
        private int buttonsCount;
        private int contactButton;
        private int documentButton;
        private int galleryButton;
        private int locationButton;
        private Context mContext;
        private int musicButton;
        private int pollButton;

        public boolean isEnabled(ViewHolder viewHolder) {
            return false;
        }

        public ButtonsAdapter(Context context) {
            this.mContext = context;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View attachBotButton;
            if (i != 0) {
                attachBotButton = new AttachBotButton(this.mContext);
            } else {
                attachBotButton = new AttachButton(this.mContext);
            }
            return new Holder(attachBotButton);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType == 0) {
                AttachButton attachButton = (AttachButton) viewHolder.itemView;
                if (i == this.galleryButton) {
                    attachButton.setTextAndIcon(LocaleController.getString("ChatGallery", NUM), Theme.chat_attachButtonDrawables[0]);
                    attachButton.setTag(Integer.valueOf(1));
                } else if (i == this.documentButton) {
                    attachButton.setTextAndIcon(LocaleController.getString("ChatDocument", NUM), Theme.chat_attachButtonDrawables[2]);
                    attachButton.setTag(Integer.valueOf(4));
                } else if (i == this.locationButton) {
                    attachButton.setTextAndIcon(LocaleController.getString("ChatLocation", NUM), Theme.chat_attachButtonDrawables[4]);
                    attachButton.setTag(Integer.valueOf(6));
                } else if (i == this.musicButton) {
                    attachButton.setTextAndIcon(LocaleController.getString("AttachMusic", NUM), Theme.chat_attachButtonDrawables[1]);
                    attachButton.setTag(Integer.valueOf(3));
                } else if (i == this.pollButton) {
                    attachButton.setTextAndIcon(LocaleController.getString("Poll", NUM), Theme.chat_attachButtonDrawables[5]);
                    attachButton.setTag(Integer.valueOf(9));
                } else if (i == this.contactButton) {
                    attachButton.setTextAndIcon(LocaleController.getString("AttachContact", NUM), Theme.chat_attachButtonDrawables[3]);
                    attachButton.setTag(Integer.valueOf(5));
                }
            } else if (itemViewType == 1) {
                i -= this.buttonsCount;
                AttachBotButton attachBotButton = (AttachBotButton) viewHolder.itemView;
                attachBotButton.setTag(Integer.valueOf(i));
                attachBotButton.setUser(MessagesController.getInstance(ChatAttachAlert.this.currentAccount).getUser(Integer.valueOf(((TL_topPeer) MediaDataController.getInstance(ChatAttachAlert.this.currentAccount).inlineBots.get(i)).peer.user_id)));
            }
        }

        public void onViewAttachedToWindow(ViewHolder viewHolder) {
            ChatAttachAlert.this.applyAttachButtonColors(viewHolder.itemView);
        }

        public int getItemCount() {
            int i = this.buttonsCount;
            return (ChatAttachAlert.this.editingMessageObject == null && (ChatAttachAlert.this.baseFragment instanceof ChatActivity)) ? i + MediaDataController.getInstance(ChatAttachAlert.this.currentAccount).inlineBots.size() : i;
        }

        public void notifyDataSetChanged() {
            this.buttonsCount = 0;
            this.galleryButton = -1;
            this.documentButton = -1;
            this.musicButton = -1;
            this.pollButton = -1;
            this.contactButton = -1;
            this.locationButton = -1;
            int i;
            if (!(ChatAttachAlert.this.baseFragment instanceof ChatActivity)) {
                i = this.buttonsCount;
                this.buttonsCount = i + 1;
                this.galleryButton = i;
                i = this.buttonsCount;
                this.buttonsCount = i + 1;
                this.documentButton = i;
            } else if (ChatAttachAlert.this.editingMessageObject != null) {
                i = this.buttonsCount;
                this.buttonsCount = i + 1;
                this.galleryButton = i;
                i = this.buttonsCount;
                this.buttonsCount = i + 1;
                this.documentButton = i;
                i = this.buttonsCount;
                this.buttonsCount = i + 1;
                this.musicButton = i;
            } else {
                if (ChatAttachAlert.this.mediaEnabled) {
                    i = this.buttonsCount;
                    this.buttonsCount = i + 1;
                    this.galleryButton = i;
                    i = this.buttonsCount;
                    this.buttonsCount = i + 1;
                    this.documentButton = i;
                }
                i = this.buttonsCount;
                this.buttonsCount = i + 1;
                this.locationButton = i;
                if (ChatAttachAlert.this.pollsEnabled) {
                    i = this.buttonsCount;
                    this.buttonsCount = i + 1;
                    this.pollButton = i;
                } else {
                    i = this.buttonsCount;
                    this.buttonsCount = i + 1;
                    this.contactButton = i;
                }
                if (ChatAttachAlert.this.mediaEnabled) {
                    i = this.buttonsCount;
                    this.buttonsCount = i + 1;
                    this.musicButton = i;
                }
            }
            super.notifyDataSetChanged();
        }

        public int getButtonsCount() {
            return this.buttonsCount;
        }

        public int getItemViewType(int i) {
            return i < this.buttonsCount ? 0 : 1;
        }
    }

    private class PhotoAttachAdapter extends SelectionAdapter {
        private int itemsCount;
        private Context mContext;
        private boolean needCamera;
        private ArrayList<Holder> viewsCache = new ArrayList(8);

        public boolean isEnabled(ViewHolder viewHolder) {
            return false;
        }

        public PhotoAttachAdapter(Context context, boolean z) {
            this.mContext = context;
            this.needCamera = z;
            for (int i = 0; i < 8; i++) {
                this.viewsCache.add(createHolder());
            }
        }

        public Holder createHolder() {
            PhotoAttachPhotoCell photoAttachPhotoCell = new PhotoAttachPhotoCell(this.mContext);
            if (VERSION.SDK_INT >= 21) {
                photoAttachPhotoCell.setOutlineProvider(new ViewOutlineProvider() {
                    public void getOutline(View view, Outline outline) {
                        int intValue = ((Integer) ((PhotoAttachPhotoCell) view).getTag()).intValue();
                        if (PhotoAttachAdapter.this.needCamera && ((ChatAttachAlert.this.deviceHasGoodCamera || ChatAttachAlert.this.noCameraPermissions) && ChatAttachAlert.this.selectedAlbumEntry == ChatAttachAlert.this.galleryAlbumEntry)) {
                            intValue++;
                        }
                        if (intValue == 0) {
                            intValue = AndroidUtilities.dp(ChatAttachAlert.this.cornerRadius * 8.0f);
                            outline.setRoundRect(0, 0, view.getMeasuredWidth() + intValue, view.getMeasuredHeight() + intValue, (float) intValue);
                        } else if (intValue == ChatAttachAlert.this.itemsPerRow - 1) {
                            intValue = AndroidUtilities.dp(ChatAttachAlert.this.cornerRadius * 8.0f);
                            outline.setRoundRect(-intValue, 0, view.getMeasuredWidth(), view.getMeasuredHeight() + intValue, (float) intValue);
                        } else {
                            outline.setRect(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
                        }
                    }
                });
                photoAttachPhotoCell.setClipToOutline(true);
            }
            photoAttachPhotoCell.setDelegate(new -$$Lambda$ChatAttachAlert$PhotoAttachAdapter$K_1guAmOmM0zF5Rks69-v23fQoI(this));
            return new Holder(photoAttachPhotoCell);
        }

        public /* synthetic */ void lambda$createHolder$0$ChatAttachAlert$PhotoAttachAdapter(PhotoAttachPhotoCell photoAttachPhotoCell) {
            if (ChatAttachAlert.this.mediaEnabled) {
                int intValue = ((Integer) photoAttachPhotoCell.getTag()).intValue();
                PhotoEntry photoEntry = photoAttachPhotoCell.getPhotoEntry();
                int i = 1;
                int containsKey = ChatAttachAlert.selectedPhotos.containsKey(Integer.valueOf(photoEntry.imageId)) ^ 1;
                if (containsKey == 0 || ChatAttachAlert.this.maxSelectedPhotos < 0 || ChatAttachAlert.selectedPhotos.size() < ChatAttachAlert.this.maxSelectedPhotos) {
                    int size = containsKey != 0 ? ChatAttachAlert.selectedPhotosOrder.size() : -1;
                    if ((ChatAttachAlert.this.baseFragment instanceof ChatActivity) && ChatAttachAlert.this.allowOrder) {
                        photoAttachPhotoCell.setChecked(size, containsKey, true);
                    } else {
                        photoAttachPhotoCell.setChecked(-1, containsKey, true);
                    }
                    ChatAttachAlert.this.addToSelectedPhotos(photoEntry, intValue);
                    if (this == ChatAttachAlert.this.cameraAttachAdapter) {
                        if (ChatAttachAlert.this.adapter.needCamera && ChatAttachAlert.this.deviceHasGoodCamera && ChatAttachAlert.this.selectedAlbumEntry == ChatAttachAlert.this.galleryAlbumEntry) {
                            intValue++;
                        }
                        ChatAttachAlert.this.adapter.notifyItemChanged(intValue);
                    } else {
                        ChatAttachAlert.this.cameraAttachAdapter.notifyItemChanged(intValue);
                    }
                    ChatAttachAlert chatAttachAlert = ChatAttachAlert.this;
                    if (containsKey == 0) {
                        i = 2;
                    }
                    chatAttachAlert.updatePhotosButton(i);
                    return;
                }
                if (ChatAttachAlert.this.allowOrder && (ChatAttachAlert.this.baseFragment instanceof ChatActivity)) {
                    Chat currentChat = ((ChatActivity) ChatAttachAlert.this.baseFragment).getCurrentChat();
                    if (!(currentChat == null || ChatObject.hasAdminRights(currentChat) || !currentChat.slowmode_enabled)) {
                        AlertsCreator.createSimpleAlert(ChatAttachAlert.this.getContext(), LocaleController.getString("Slowmode", NUM), LocaleController.getString("SlowmodeSelectSendError", NUM)).show();
                    }
                }
            }
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            int itemViewType = viewHolder.getItemViewType();
            int i2 = 0;
            boolean z = true;
            if (itemViewType == 0) {
                if (this.needCamera && ((ChatAttachAlert.this.deviceHasGoodCamera || ChatAttachAlert.this.noCameraPermissions) && ChatAttachAlert.this.selectedAlbumEntry == ChatAttachAlert.this.galleryAlbumEntry)) {
                    i--;
                }
                PhotoAttachPhotoCell photoAttachPhotoCell = (PhotoAttachPhotoCell) viewHolder.itemView;
                if (this == ChatAttachAlert.this.adapter) {
                    photoAttachPhotoCell.setItemSize(ChatAttachAlert.this.itemSize);
                } else {
                    photoAttachPhotoCell.setIsVertical(ChatAttachAlert.this.cameraPhotoLayoutManager.getOrientation() == 1);
                }
                PhotoEntry access$000 = ChatAttachAlert.this.getPhotoEntryAtPosition(i);
                boolean z2 = this.needCamera && ChatAttachAlert.this.selectedAlbumEntry == ChatAttachAlert.this.galleryAlbumEntry;
                if (i != getItemCount() - 1) {
                    z = false;
                }
                photoAttachPhotoCell.setPhotoEntry(access$000, z2, z);
                if ((ChatAttachAlert.this.baseFragment instanceof ChatActivity) && ChatAttachAlert.this.allowOrder) {
                    photoAttachPhotoCell.setChecked(ChatAttachAlert.selectedPhotosOrder.indexOf(Integer.valueOf(access$000.imageId)), ChatAttachAlert.selectedPhotos.containsKey(Integer.valueOf(access$000.imageId)), false);
                } else {
                    photoAttachPhotoCell.setChecked(-1, ChatAttachAlert.selectedPhotos.containsKey(Integer.valueOf(access$000.imageId)), false);
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
                if (!(this.needCamera && ChatAttachAlert.this.noCameraPermissions && i == 0)) {
                    i2 = 1;
                }
                photoAttachPermissionCell.setType(i2);
            }
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            if (i != 0) {
                if (i == 1) {
                    PhotoAttachCameraCell photoAttachCameraCell = new PhotoAttachCameraCell(this.mContext);
                    if (VERSION.SDK_INT >= 21) {
                        photoAttachCameraCell.setOutlineProvider(new ViewOutlineProvider() {
                            public void getOutline(View view, Outline outline) {
                                int dp = AndroidUtilities.dp(ChatAttachAlert.this.cornerRadius * 8.0f);
                                outline.setRoundRect(0, 0, view.getMeasuredWidth() + dp, view.getMeasuredHeight() + dp, (float) dp);
                            }
                        });
                        photoAttachCameraCell.setClipToOutline(true);
                    }
                    return new Holder(photoAttachCameraCell);
                } else if (i != 2) {
                    return new Holder(new PhotoAttachPermissionCell(this.mContext));
                } else {
                    return new Holder(new View(this.mContext) {
                        /* Access modifiers changed, original: protected */
                        public void onMeasure(int i, int i2) {
                            super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(ChatAttachAlert.this.gridExtraSpace, NUM));
                        }
                    });
                }
            } else if (this.viewsCache.isEmpty()) {
                return createHolder();
            } else {
                Holder holder = (Holder) this.viewsCache.get(0);
                this.viewsCache.remove(0);
                return holder;
            }
        }

        public void onViewAttachedToWindow(ViewHolder viewHolder) {
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
            if (!(this.needCamera && ChatAttachAlert.this.selectedAlbumEntry == ChatAttachAlert.this.galleryAlbumEntry && (ChatAttachAlert.this.deviceHasGoodCamera || ChatAttachAlert.this.noCameraPermissions))) {
                i = 0;
            }
            if (ChatAttachAlert.this.noGalleryPermissions && this == ChatAttachAlert.this.adapter) {
                i++;
            }
            i += ChatAttachAlert.cameraPhotos.size();
            if (ChatAttachAlert.this.selectedAlbumEntry != null) {
                i += ChatAttachAlert.this.selectedAlbumEntry.photos.size();
            }
            if (this == ChatAttachAlert.this.adapter) {
                i++;
            }
            this.itemsCount = i;
            return i;
        }

        public int getItemViewType(int i) {
            if (!ChatAttachAlert.this.mediaEnabled) {
                return 2;
            }
            if (this.needCamera && i == 0 && ChatAttachAlert.this.selectedAlbumEntry == ChatAttachAlert.this.galleryAlbumEntry) {
                if (ChatAttachAlert.this.deviceHasGoodCamera) {
                    return 1;
                }
                if (ChatAttachAlert.this.noCameraPermissions) {
                    return 3;
                }
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
                EmptyTextProgressView access$12200 = ChatAttachAlert.this.progressView;
                int i = (!(getItemCount() == 1 && ChatAttachAlert.this.selectedAlbumEntry == null) && ChatAttachAlert.this.mediaEnabled) ? 4 : 0;
                access$12200.setVisibility(i);
            }
        }
    }

    public boolean canDismiss() {
        return true;
    }

    /* Access modifiers changed, original: protected */
    public boolean canDismissWithSwipe() {
        return false;
    }

    public void onOpenAnimationStart() {
    }

    static /* synthetic */ int access$8510() {
        int i = lastImageId;
        lastImageId = i - 1;
        return i;
    }

    private void updateCheckedPhotoIndices() {
        if (this.baseFragment instanceof ChatActivity) {
            int childCount = this.gridView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = this.gridView.getChildAt(i);
                if (childAt instanceof PhotoAttachPhotoCell) {
                    PhotoAttachPhotoCell photoAttachPhotoCell = (PhotoAttachPhotoCell) childAt;
                    PhotoEntry photoEntryAtPosition = getPhotoEntryAtPosition(((Integer) photoAttachPhotoCell.getTag()).intValue());
                    if (photoEntryAtPosition != null) {
                        photoAttachPhotoCell.setNum(selectedPhotosOrder.indexOf(Integer.valueOf(photoEntryAtPosition.imageId)));
                    }
                }
            }
            childCount = this.cameraPhotoRecyclerView.getChildCount();
            for (int i2 = 0; i2 < childCount; i2++) {
                View childAt2 = this.cameraPhotoRecyclerView.getChildAt(i2);
                if (childAt2 instanceof PhotoAttachPhotoCell) {
                    PhotoAttachPhotoCell photoAttachPhotoCell2 = (PhotoAttachPhotoCell) childAt2;
                    PhotoEntry photoEntryAtPosition2 = getPhotoEntryAtPosition(((Integer) photoAttachPhotoCell2.getTag()).intValue());
                    if (photoEntryAtPosition2 != null) {
                        photoAttachPhotoCell2.setNum(selectedPhotosOrder.indexOf(Integer.valueOf(photoEntryAtPosition2.imageId)));
                    }
                }
            }
        }
    }

    private PhotoEntry getPhotoEntryAtPosition(int i) {
        if (i < 0) {
            return null;
        }
        int size = cameraPhotos.size();
        if (i < size) {
            return (PhotoEntry) cameraPhotos.get(i);
        }
        i -= size;
        if (i < this.selectedAlbumEntry.photos.size()) {
            return (PhotoEntry) this.selectedAlbumEntry.photos.get(i);
        }
        return null;
    }

    private ArrayList<Object> getAllPhotosArray() {
        if (this.selectedAlbumEntry != null) {
            if (cameraPhotos.isEmpty()) {
                return this.selectedAlbumEntry.photos;
            }
            ArrayList<Object> arrayList = new ArrayList(this.selectedAlbumEntry.photos.size() + cameraPhotos.size());
            arrayList.addAll(cameraPhotos);
            arrayList.addAll(this.selectedAlbumEntry.photos);
            return arrayList;
        } else if (cameraPhotos.isEmpty()) {
            return new ArrayList(0);
        } else {
            return cameraPhotos;
        }
    }

    public ChatAttachAlert(Context context, BaseFragment baseFragment) {
        Context context2 = context;
        final BaseFragment baseFragment2 = baseFragment;
        super(context2, false, 1);
        this.baseFragment = baseFragment2;
        setDelegate(this);
        checkCamera(false);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.albumsDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.reloadInlineHints);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.cameraInitied);
        this.cameraDrawable = context.getResources().getDrawable(NUM).mutate();
        this.sizeNotifierFrameLayout = new SizeNotifierFrameLayout(context2) {
            private boolean ignoreLayout;
            private float initialTranslationY;
            private int lastNotifyWidth;
            private RectF rect = new RectF();

            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                if (ChatAttachAlert.this.cameraAnimationInProgress) {
                    return true;
                }
                if (ChatAttachAlert.this.cameraOpened) {
                    return ChatAttachAlert.this.processTouchEvent(motionEvent);
                }
                if (motionEvent.getAction() != 0 || ChatAttachAlert.this.scrollOffsetY == 0 || motionEvent.getY() >= ((float) (ChatAttachAlert.this.scrollOffsetY - AndroidUtilities.dp(36.0f))) || ChatAttachAlert.this.actionBar.getAlpha() != 0.0f) {
                    return super.onInterceptTouchEvent(motionEvent);
                }
                ChatAttachAlert.this.dismiss();
                return true;
            }

            public boolean onTouchEvent(MotionEvent motionEvent) {
                boolean z = true;
                if (ChatAttachAlert.this.cameraAnimationInProgress) {
                    return true;
                }
                if (ChatAttachAlert.this.cameraOpened) {
                    return ChatAttachAlert.this.processTouchEvent(motionEvent);
                }
                if (ChatAttachAlert.this.isDismissed() || !super.onTouchEvent(motionEvent)) {
                    z = false;
                }
                return z;
            }

            /* Access modifiers changed, original: protected */
            /* JADX WARNING: Removed duplicated region for block: B:37:0x01ce  */
            public void onMeasure(int r10, int r11) {
                /*
                r9 = this;
                r11 = android.view.View.MeasureSpec.getSize(r11);
                r0 = android.os.Build.VERSION.SDK_INT;
                r1 = 0;
                r2 = 1;
                r3 = 21;
                if (r0 < r3) goto L_0x0021;
            L_0x000c:
                r9.ignoreLayout = r2;
                r0 = org.telegram.ui.Components.ChatAttachAlert.this;
                r0 = r0.backgroundPaddingLeft;
                r3 = org.telegram.messenger.AndroidUtilities.statusBarHeight;
                r4 = org.telegram.ui.Components.ChatAttachAlert.this;
                r4 = r4.backgroundPaddingLeft;
                r9.setPadding(r0, r3, r4, r1);
                r9.ignoreLayout = r1;
            L_0x0021:
                r0 = r9.getPaddingTop();
                r0 = r11 - r0;
                r3 = r9.getKeyboardHeight();
                r4 = org.telegram.messenger.AndroidUtilities.isInMultiwindow;
                r5 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
                if (r4 != 0) goto L_0x0042;
            L_0x0031:
                r4 = org.telegram.messenger.AndroidUtilities.dp(r5);
                if (r3 > r4) goto L_0x0042;
            L_0x0037:
                r3 = org.telegram.ui.Components.ChatAttachAlert.this;
                r3 = r3.commentTextView;
                r3 = r3.getEmojiPadding();
                r0 = r0 - r3;
            L_0x0042:
                r3 = android.view.View.MeasureSpec.getSize(r10);
                r4 = org.telegram.ui.Components.ChatAttachAlert.this;
                r4 = r4.backgroundPaddingLeft;
                r4 = r4 * 2;
                r3 = r3 - r4;
                r4 = org.telegram.messenger.AndroidUtilities.isTablet();
                r6 = NUM; // 0x40400000 float:3.0 double:5.325712093E-315;
                r7 = 4;
                if (r4 == 0) goto L_0x006c;
            L_0x0058:
                r4 = org.telegram.ui.Components.ChatAttachAlert.this;
                r4.itemsPerRow = r7;
                r4 = org.telegram.ui.Components.ChatAttachAlert.this;
                r4 = r4.selectedMenuItem;
                r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
                r6 = -r6;
                r4.setAdditionalYOffset(r6);
                goto L_0x0097;
            L_0x006c:
                r4 = org.telegram.messenger.AndroidUtilities.displaySize;
                r8 = r4.x;
                r4 = r4.y;
                if (r8 <= r4) goto L_0x0083;
            L_0x0074:
                r4 = org.telegram.ui.Components.ChatAttachAlert.this;
                r4.itemsPerRow = r7;
                r4 = org.telegram.ui.Components.ChatAttachAlert.this;
                r4 = r4.selectedMenuItem;
                r4.setAdditionalYOffset(r1);
                goto L_0x0097;
            L_0x0083:
                r4 = org.telegram.ui.Components.ChatAttachAlert.this;
                r8 = 3;
                r4.itemsPerRow = r8;
                r4 = org.telegram.ui.Components.ChatAttachAlert.this;
                r4 = r4.selectedMenuItem;
                r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
                r6 = -r6;
                r4.setAdditionalYOffset(r6);
            L_0x0097:
                r4 = org.telegram.ui.Components.ChatAttachAlert.this;
                r4 = r4.gridView;
                r4 = r4.getLayoutParams();
                r4 = (android.widget.FrameLayout.LayoutParams) r4;
                r6 = org.telegram.ui.ActionBar.ActionBar.getCurrentActionBarHeight();
                r4.topMargin = r6;
                r4 = org.telegram.ui.Components.ChatAttachAlert.this;
                r4 = r4.actionBarShadow;
                r4 = r4.getLayoutParams();
                r4 = (android.widget.FrameLayout.LayoutParams) r4;
                r6 = org.telegram.ui.ActionBar.ActionBar.getCurrentActionBarHeight();
                r4.topMargin = r6;
                r9.ignoreLayout = r2;
                r4 = org.telegram.ui.Components.ChatAttachAlert.this;
                r6 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
                r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
                r6 = r3 - r6;
                r8 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
                r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
                r6 = r6 - r8;
                r8 = org.telegram.ui.Components.ChatAttachAlert.this;
                r8 = r8.itemsPerRow;
                r6 = r6 / r8;
                r4.itemSize = r6;
                r4 = org.telegram.ui.Components.ChatAttachAlert.this;
                r4 = r4.buttonsAdapter;
                r4 = r4.getItemCount();
                r4 = java.lang.Math.min(r7, r4);
                r3 = r3 / r4;
                r4 = org.telegram.ui.Components.ChatAttachAlert.this;
                r4 = r4.attachItemSize;
                if (r4 == r3) goto L_0x00fc;
            L_0x00ef:
                r4 = org.telegram.ui.Components.ChatAttachAlert.this;
                r4.attachItemSize = r3;
                r3 = new org.telegram.ui.Components.-$$Lambda$ChatAttachAlert$2$NtN5UTLrUKYYcv9eBtYcAg10XAI;
                r3.<init>(r9);
                org.telegram.messenger.AndroidUtilities.runOnUIThread(r3);
            L_0x00fc:
                r3 = org.telegram.ui.Components.ChatAttachAlert.this;
                r3 = r3.lastItemSize;
                r4 = org.telegram.ui.Components.ChatAttachAlert.this;
                r4 = r4.itemSize;
                if (r3 == r4) goto L_0x011b;
            L_0x010a:
                r3 = org.telegram.ui.Components.ChatAttachAlert.this;
                r4 = r3.itemSize;
                r3.lastItemSize = r4;
                r3 = new org.telegram.ui.Components.-$$Lambda$ChatAttachAlert$2$PVWOcvgp00CLASSNAMEphcQ5dnpaXawfg;
                r3.<init>(r9);
                org.telegram.messenger.AndroidUtilities.runOnUIThread(r3);
            L_0x011b:
                r3 = org.telegram.ui.Components.ChatAttachAlert.this;
                r3 = r3.dropDown;
                r4 = org.telegram.messenger.AndroidUtilities.isTablet();
                if (r4 != 0) goto L_0x0131;
            L_0x0127:
                r4 = org.telegram.messenger.AndroidUtilities.displaySize;
                r6 = r4.x;
                r4 = r4.y;
                if (r6 <= r4) goto L_0x0131;
            L_0x012f:
                r5 = NUM; // 0x41900000 float:18.0 double:5.43450582E-315;
            L_0x0131:
                r3.setTextSize(r5);
                r3 = org.telegram.ui.Components.ChatAttachAlert.this;
                r3 = r3.layoutManager;
                r4 = org.telegram.ui.Components.ChatAttachAlert.this;
                r4 = r4.itemSize;
                r5 = org.telegram.ui.Components.ChatAttachAlert.this;
                r5 = r5.itemsPerRow;
                r4 = r4 * r5;
                r5 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
                r6 = org.telegram.messenger.AndroidUtilities.dp(r5);
                r7 = org.telegram.ui.Components.ChatAttachAlert.this;
                r7 = r7.itemsPerRow;
                r7 = r7 - r2;
                r6 = r6 * r7;
                r4 = r4 + r6;
                r3.setSpanCount(r4);
                r3 = org.telegram.ui.Components.ChatAttachAlert.this;
                r3 = r3.adapter;
                r3 = r3.getItemCount();
                r3 = r3 - r2;
                r3 = (float) r3;
                r4 = org.telegram.ui.Components.ChatAttachAlert.this;
                r4 = r4.itemsPerRow;
                r4 = (float) r4;
                r3 = r3 / r4;
                r3 = (double) r3;
                r3 = java.lang.Math.ceil(r3);
                r3 = (int) r3;
                r4 = org.telegram.ui.Components.ChatAttachAlert.this;
                r4 = r4.itemSize;
                r4 = r4 * r3;
                r3 = r3 - r2;
                r2 = org.telegram.messenger.AndroidUtilities.dp(r5);
                r3 = r3 * r2;
                r4 = r4 + r3;
                r2 = r0 - r4;
                r3 = org.telegram.ui.ActionBar.ActionBar.getCurrentActionBarHeight();
                r2 = r2 - r3;
                r3 = NUM; // 0x42700000 float:60.0 double:5.507034975E-315;
                r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
                r2 = r2 - r3;
                r2 = java.lang.Math.max(r1, r2);
                r3 = org.telegram.ui.Components.ChatAttachAlert.this;
                r3 = r3.gridExtraSpace;
                if (r3 == r2) goto L_0x01ad;
            L_0x019f:
                r3 = org.telegram.ui.Components.ChatAttachAlert.this;
                r3.gridExtraSpace = r2;
                r2 = org.telegram.ui.Components.ChatAttachAlert.this;
                r2 = r2.adapter;
                r2.notifyDataSetChanged();
            L_0x01ad:
                r2 = org.telegram.messenger.AndroidUtilities.isTablet();
                if (r2 != 0) goto L_0x01be;
            L_0x01b3:
                r2 = org.telegram.messenger.AndroidUtilities.displaySize;
                r3 = r2.x;
                r2 = r2.y;
                if (r3 <= r2) goto L_0x01be;
            L_0x01bb:
                r0 = r0 / 6;
                goto L_0x01c2;
            L_0x01be:
                r0 = r0 / 5;
                r0 = r0 * 2;
            L_0x01c2:
                r2 = org.telegram.ui.Components.ChatAttachAlert.this;
                r2 = r2.gridView;
                r2 = r2.getPaddingTop();
                if (r2 == r0) goto L_0x01e7;
            L_0x01ce:
                r2 = org.telegram.ui.Components.ChatAttachAlert.this;
                r2 = r2.gridView;
                r3 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
                r4 = org.telegram.messenger.AndroidUtilities.dp(r3);
                r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
                r5 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
                r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
                r2.setPadding(r4, r0, r3, r5);
            L_0x01e7:
                r9.ignoreLayout = r1;
                r0 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
                r11 = android.view.View.MeasureSpec.makeMeasureSpec(r11, r0);
                r9.onMeasureInternal(r10, r11);
                return;
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlert$AnonymousClass2.onMeasure(int, int):void");
            }

            public /* synthetic */ void lambda$onMeasure$0$ChatAttachAlert$2() {
                ChatAttachAlert.this.buttonsAdapter.notifyDataSetChanged();
            }

            public /* synthetic */ void lambda$onMeasure$1$ChatAttachAlert$2() {
                ChatAttachAlert.this.adapter.notifyDataSetChanged();
            }

            private void onMeasureInternal(int i, int i2) {
                int size = MeasureSpec.getSize(i);
                int size2 = MeasureSpec.getSize(i2);
                setMeasuredDimension(size, size2);
                size -= ChatAttachAlert.this.backgroundPaddingLeft * 2;
                int i3 = 0;
                if (getKeyboardHeight() > AndroidUtilities.dp(20.0f)) {
                    this.ignoreLayout = true;
                    ChatAttachAlert.this.commentTextView.hideEmojiView();
                    this.ignoreLayout = false;
                } else if (!AndroidUtilities.isInMultiwindow) {
                    size2 -= ChatAttachAlert.this.commentTextView.getEmojiPadding();
                    i2 = MeasureSpec.makeMeasureSpec(size2, NUM);
                }
                int childCount = getChildCount();
                while (i3 < childCount) {
                    View childAt = getChildAt(i3);
                    if (!(childAt == null || childAt.getVisibility() == 8)) {
                        if (ChatAttachAlert.this.commentTextView == null || !ChatAttachAlert.this.commentTextView.isPopupView(childAt)) {
                            measureChildWithMargins(childAt, i, 0, i2, 0);
                        } else if (!AndroidUtilities.isInMultiwindow && !AndroidUtilities.isTablet()) {
                            childAt.measure(MeasureSpec.makeMeasureSpec(size, NUM), MeasureSpec.makeMeasureSpec(childAt.getLayoutParams().height, NUM));
                        } else if (AndroidUtilities.isTablet()) {
                            childAt.measure(MeasureSpec.makeMeasureSpec(size, NUM), MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(AndroidUtilities.isTablet() ? 200.0f : 320.0f), (size2 - AndroidUtilities.statusBarHeight) + getPaddingTop()), NUM));
                        } else {
                            childAt.measure(MeasureSpec.makeMeasureSpec(size, NUM), MeasureSpec.makeMeasureSpec((size2 - AndroidUtilities.statusBarHeight) + getPaddingTop(), NUM));
                        }
                    }
                    i3++;
                }
            }

            /* Access modifiers changed, original: protected */
            /* JADX WARNING: Removed duplicated region for block: B:51:0x0102  */
            /* JADX WARNING: Removed duplicated region for block: B:50:0x00f9  */
            /* JADX WARNING: Removed duplicated region for block: B:42:0x00d2  */
            /* JADX WARNING: Removed duplicated region for block: B:35:0x00b8  */
            /* JADX WARNING: Removed duplicated region for block: B:50:0x00f9  */
            /* JADX WARNING: Removed duplicated region for block: B:51:0x0102  */
            public void onLayout(boolean r10, int r11, int r12, int r13, int r14) {
                /*
                r9 = this;
                r10 = r9.lastNotifyWidth;
                r13 = r13 - r11;
                if (r10 == r13) goto L_0x0035;
            L_0x0005:
                r9.lastNotifyWidth = r13;
                r10 = org.telegram.ui.Components.ChatAttachAlert.this;
                r10 = r10.adapter;
                if (r10 == 0) goto L_0x0018;
            L_0x000f:
                r10 = org.telegram.ui.Components.ChatAttachAlert.this;
                r10 = r10.adapter;
                r10.notifyDataSetChanged();
            L_0x0018:
                r10 = org.telegram.ui.Components.ChatAttachAlert.this;
                r10 = r10.sendPopupWindow;
                if (r10 == 0) goto L_0x0035;
            L_0x0020:
                r10 = org.telegram.ui.Components.ChatAttachAlert.this;
                r10 = r10.sendPopupWindow;
                r10 = r10.isShowing();
                if (r10 == 0) goto L_0x0035;
            L_0x002c:
                r10 = org.telegram.ui.Components.ChatAttachAlert.this;
                r10 = r10.sendPopupWindow;
                r10.dismiss();
            L_0x0035:
                r10 = r9.getChildCount();
                r11 = r9.getKeyboardHeight();
                r0 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
                r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
                r1 = 0;
                if (r11 > r0) goto L_0x005b;
            L_0x0046:
                r11 = org.telegram.messenger.AndroidUtilities.isInMultiwindow;
                if (r11 != 0) goto L_0x005b;
            L_0x004a:
                r11 = org.telegram.messenger.AndroidUtilities.isTablet();
                if (r11 != 0) goto L_0x005b;
            L_0x0050:
                r11 = org.telegram.ui.Components.ChatAttachAlert.this;
                r11 = r11.commentTextView;
                r11 = r11.getEmojiPadding();
                goto L_0x005c;
            L_0x005b:
                r11 = 0;
            L_0x005c:
                r9.setBottomClip(r11);
                r0 = 0;
            L_0x0060:
                if (r0 >= r10) goto L_0x0119;
            L_0x0062:
                r2 = r9.getChildAt(r0);
                r3 = r2.getVisibility();
                r4 = 8;
                if (r3 != r4) goto L_0x0070;
            L_0x006e:
                goto L_0x0115;
            L_0x0070:
                r3 = r2.getLayoutParams();
                r3 = (android.widget.FrameLayout.LayoutParams) r3;
                r4 = r2.getMeasuredWidth();
                r5 = r2.getMeasuredHeight();
                r6 = r3.gravity;
                r7 = -1;
                if (r6 != r7) goto L_0x0085;
            L_0x0083:
                r6 = 51;
            L_0x0085:
                r7 = r6 & 7;
                r6 = r6 & 112;
                r7 = r7 & 7;
                r8 = 1;
                if (r7 == r8) goto L_0x00aa;
            L_0x008e:
                r8 = 5;
                if (r7 == r8) goto L_0x0099;
            L_0x0091:
                r7 = r3.leftMargin;
                r8 = r9.getPaddingLeft();
                r7 = r7 + r8;
                goto L_0x00b4;
            L_0x0099:
                r7 = r13 - r4;
                r8 = r3.rightMargin;
                r7 = r7 - r8;
                r8 = r9.getPaddingRight();
                r7 = r7 - r8;
                r8 = org.telegram.ui.Components.ChatAttachAlert.this;
                r8 = r8.backgroundPaddingLeft;
                goto L_0x00b3;
            L_0x00aa:
                r7 = r13 - r4;
                r7 = r7 / 2;
                r8 = r3.leftMargin;
                r7 = r7 + r8;
                r8 = r3.rightMargin;
            L_0x00b3:
                r7 = r7 - r8;
            L_0x00b4:
                r8 = 16;
                if (r6 == r8) goto L_0x00d2;
            L_0x00b8:
                r8 = 48;
                if (r6 == r8) goto L_0x00ca;
            L_0x00bc:
                r8 = 80;
                if (r6 == r8) goto L_0x00c3;
            L_0x00c0:
                r3 = r3.topMargin;
                goto L_0x00df;
            L_0x00c3:
                r6 = r14 - r11;
                r6 = r6 - r12;
                r6 = r6 - r5;
                r3 = r3.bottomMargin;
                goto L_0x00dd;
            L_0x00ca:
                r3 = r3.topMargin;
                r6 = r9.getPaddingTop();
                r3 = r3 + r6;
                goto L_0x00df;
            L_0x00d2:
                r6 = r14 - r11;
                r6 = r6 - r12;
                r6 = r6 - r5;
                r6 = r6 / 2;
                r8 = r3.topMargin;
                r6 = r6 + r8;
                r3 = r3.bottomMargin;
            L_0x00dd:
                r3 = r6 - r3;
            L_0x00df:
                r6 = org.telegram.ui.Components.ChatAttachAlert.this;
                r6 = r6.commentTextView;
                if (r6 == 0) goto L_0x0110;
            L_0x00e7:
                r6 = org.telegram.ui.Components.ChatAttachAlert.this;
                r6 = r6.commentTextView;
                r6 = r6.isPopupView(r2);
                if (r6 == 0) goto L_0x0110;
            L_0x00f3:
                r3 = org.telegram.messenger.AndroidUtilities.isTablet();
                if (r3 == 0) goto L_0x0102;
            L_0x00f9:
                r3 = r9.getMeasuredHeight();
                r6 = r2.getMeasuredHeight();
                goto L_0x010f;
            L_0x0102:
                r3 = r9.getMeasuredHeight();
                r6 = r9.getKeyboardHeight();
                r3 = r3 + r6;
                r6 = r2.getMeasuredHeight();
            L_0x010f:
                r3 = r3 - r6;
            L_0x0110:
                r4 = r4 + r7;
                r5 = r5 + r3;
                r2.layout(r7, r3, r4, r5);
            L_0x0115:
                r0 = r0 + 1;
                goto L_0x0060;
            L_0x0119:
                r9.notifyHeightChanged();
                r10 = org.telegram.ui.Components.ChatAttachAlert.this;
                r10.updateLayout(r1);
                r10 = org.telegram.ui.Components.ChatAttachAlert.this;
                r10.checkCameraViewPosition();
                return;
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlert$AnonymousClass2.onLayout(boolean, int, int, int, int):void");
            }

            public void requestLayout() {
                if (!this.ignoreLayout) {
                    super.requestLayout();
                }
            }

            /* Access modifiers changed, original: protected */
            public void onDraw(Canvas canvas) {
                float dp;
                int dp2 = AndroidUtilities.dp(13.0f) + (ChatAttachAlert.this.selectedMenuItem != null ? AndroidUtilities.dp(ChatAttachAlert.this.selectedMenuItem.getAlpha() * 26.0f) : 0);
                int access$1800 = (ChatAttachAlert.this.scrollOffsetY - ChatAttachAlert.this.backgroundPaddingTop) - dp2;
                if (ChatAttachAlert.this.currentSheetAnimationType == 1) {
                    access$1800 = (int) (((float) access$1800) + ChatAttachAlert.this.gridView.getTranslationY());
                }
                int dp3 = AndroidUtilities.dp(20.0f) + access$1800;
                int measuredHeight = (getMeasuredHeight() + AndroidUtilities.dp(15.0f)) + ChatAttachAlert.this.backgroundPaddingTop;
                float f = 1.0f;
                if (ChatAttachAlert.this.backgroundPaddingTop + access$1800 < ActionBar.getCurrentActionBarHeight()) {
                    dp = (float) (dp2 + AndroidUtilities.dp(4.0f));
                    float min = Math.min(1.0f, ((float) ((ActionBar.getCurrentActionBarHeight() - access$1800) - ChatAttachAlert.this.backgroundPaddingTop)) / dp);
                    dp2 = (int) ((((float) ActionBar.getCurrentActionBarHeight()) - dp) * min);
                    access$1800 -= dp2;
                    dp3 -= dp2;
                    measuredHeight += dp2;
                    dp = 1.0f - min;
                } else {
                    dp = 1.0f;
                }
                if (VERSION.SDK_INT >= 21) {
                    int i = AndroidUtilities.statusBarHeight;
                    access$1800 += i;
                    dp3 += i;
                    measuredHeight -= i;
                }
                ChatAttachAlert.this.shadowDrawable.setBounds(0, access$1800, getMeasuredWidth(), measuredHeight);
                ChatAttachAlert.this.shadowDrawable.draw(canvas);
                String str = "dialogBackground";
                if (dp != 1.0f) {
                    Theme.dialogs_onlineCirclePaint.setColor(Theme.getColor(str));
                    this.rect.set((float) ChatAttachAlert.this.backgroundPaddingLeft, (float) (ChatAttachAlert.this.backgroundPaddingTop + access$1800), (float) (getMeasuredWidth() - ChatAttachAlert.this.backgroundPaddingLeft), (float) ((ChatAttachAlert.this.backgroundPaddingTop + access$1800) + AndroidUtilities.dp(24.0f)));
                    canvas.drawRoundRect(this.rect, ((float) AndroidUtilities.dp(12.0f)) * dp, ((float) AndroidUtilities.dp(12.0f)) * dp, Theme.dialogs_onlineCirclePaint);
                }
                if ((ChatAttachAlert.this.selectedMenuItem == null || ChatAttachAlert.this.selectedMenuItem.getAlpha() != 1.0f) && dp != 0.0f) {
                    if (ChatAttachAlert.this.selectedMenuItem != null) {
                        f = 1.0f - ChatAttachAlert.this.selectedMenuItem.getAlpha();
                    }
                    access$1800 = AndroidUtilities.dp(36.0f);
                    this.rect.set((float) ((getMeasuredWidth() - access$1800) / 2), (float) dp3, (float) ((getMeasuredWidth() + access$1800) / 2), (float) (dp3 + AndroidUtilities.dp(4.0f)));
                    access$1800 = Theme.getColor("key_sheet_scrollUp");
                    dp3 = Color.alpha(access$1800);
                    Theme.dialogs_onlineCirclePaint.setColor(access$1800);
                    Theme.dialogs_onlineCirclePaint.setAlpha((int) ((((float) dp3) * f) * dp));
                    canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(2.0f), Theme.dialogs_onlineCirclePaint);
                }
                dp2 = Theme.getColor(str);
                Theme.dialogs_onlineCirclePaint.setColor(Color.argb((int) (ChatAttachAlert.this.actionBar.getAlpha() * 255.0f), (int) (((float) Color.red(dp2)) * 0.8f), (int) (((float) Color.green(dp2)) * 0.8f), (int) (((float) Color.blue(dp2)) * 0.8f)));
                canvas.drawRect((float) ChatAttachAlert.this.backgroundPaddingLeft, 0.0f, (float) (getMeasuredWidth() - ChatAttachAlert.this.backgroundPaddingLeft), (float) AndroidUtilities.statusBarHeight, Theme.dialogs_onlineCirclePaint);
            }

            public void setTranslationY(float f) {
                if (ChatAttachAlert.this.currentSheetAnimationType == 0) {
                    this.initialTranslationY = f;
                }
                if (ChatAttachAlert.this.currentSheetAnimationType == 1) {
                    if (f < 0.0f) {
                        ChatAttachAlert.this.gridView.setTranslationY(f);
                        f = (f / 40.0f) * -0.1f;
                        int childCount = ChatAttachAlert.this.gridView.getChildCount();
                        for (int i = 0; i < childCount; i++) {
                            View childAt = ChatAttachAlert.this.gridView.getChildAt(i);
                            float f2;
                            if (childAt instanceof PhotoAttachCameraCell) {
                                PhotoAttachCameraCell photoAttachCameraCell = (PhotoAttachCameraCell) childAt;
                                f2 = 1.0f + f;
                                photoAttachCameraCell.getImageView().setScaleX(f2);
                                photoAttachCameraCell.getImageView().setScaleY(f2);
                            } else if (childAt instanceof PhotoAttachPhotoCell) {
                                PhotoAttachPhotoCell photoAttachPhotoCell = (PhotoAttachPhotoCell) childAt;
                                f2 = 1.0f + f;
                                photoAttachPhotoCell.getCheckBox().setScaleX(f2);
                                photoAttachPhotoCell.getCheckBox().setScaleY(f2);
                            }
                        }
                        ChatAttachAlert.this.buttonsRecyclerView.setTranslationY(0.0f);
                        f = 0.0f;
                    } else {
                        ChatAttachAlert.this.gridView.setTranslationY(0.0f);
                        ChatAttachAlert.this.buttonsRecyclerView.setTranslationY((-f) + (((float) ChatAttachAlert.this.buttonsRecyclerView.getMeasuredHeight()) * (f / this.initialTranslationY)));
                    }
                }
                super.setTranslationY(f);
                ChatAttachAlert.this.checkCameraViewPosition();
            }
        };
        this.containerView = this.sizeNotifierFrameLayout;
        this.containerView.setWillNotDraw(false);
        ViewGroup viewGroup = this.containerView;
        int i = this.backgroundPaddingLeft;
        viewGroup.setPadding(i, 0, i, 0);
        this.selectedTextView = new TextView(context2);
        String str = "dialogTextBlack";
        this.selectedTextView.setTextColor(Theme.getColor(str));
        this.selectedTextView.setTextSize(1, 16.0f);
        String str2 = "fonts/rmedium.ttf";
        this.selectedTextView.setTypeface(AndroidUtilities.getTypeface(str2));
        this.selectedTextView.setGravity(51);
        this.selectedTextView.setVisibility(4);
        this.selectedTextView.setAlpha(0.0f);
        this.containerView.addView(this.selectedTextView, LayoutHelper.createFrame(-1, -2.0f, 51, 23.0f, 0.0f, 48.0f, 0.0f));
        this.actionBar = new ActionBar(context2) {
            public void setAlpha(float f) {
                super.setAlpha(f);
                ChatAttachAlert.this.containerView.invalidate();
                if (ChatAttachAlert.this.frameLayout2 != null && ChatAttachAlert.this.buttonsRecyclerView != null) {
                    if (ChatAttachAlert.this.frameLayout2.getTag() == null) {
                        float f2 = 1.0f - f;
                        ChatAttachAlert.this.buttonsRecyclerView.setAlpha(f2);
                        ChatAttachAlert.this.shadow.setAlpha(f2);
                        ChatAttachAlert.this.buttonsRecyclerView.setTranslationY(((float) AndroidUtilities.dp(44.0f)) * f);
                        ChatAttachAlert.this.frameLayout2.setTranslationY(((float) AndroidUtilities.dp(48.0f)) * f);
                        ChatAttachAlert.this.shadow.setTranslationY(((float) AndroidUtilities.dp(92.0f)) * f);
                        return;
                    }
                    float f3 = 0.0f;
                    if (f == 0.0f) {
                        f3 = 1.0f;
                    }
                    if (ChatAttachAlert.this.buttonsRecyclerView.getAlpha() != f3) {
                        ChatAttachAlert.this.buttonsRecyclerView.setAlpha(f3);
                    }
                }
            }
        };
        this.actionBar.setBackgroundColor(Theme.getColor("dialogBackground"));
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setItemsColor(Theme.getColor(str), false);
        this.actionBar.setItemsBackgroundColor(Theme.getColor("dialogButtonSelector"), false);
        this.actionBar.setTitleColor(Theme.getColor(str));
        this.actionBar.setOccupyStatusBar(false);
        this.actionBar.setAlpha(0.0f);
        this.containerView.addView(this.actionBar, LayoutHelper.createFrame(-1, -2.0f));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    ChatAttachAlert.this.dismiss();
                } else {
                    if ((i == 0 || i == 1) && ChatAttachAlert.this.maxSelectedPhotos > 0 && ChatAttachAlert.selectedPhotosOrder.size() > 1 && (ChatAttachAlert.this.baseFragment instanceof ChatActivity)) {
                        Chat currentChat = ((ChatActivity) ChatAttachAlert.this.baseFragment).getCurrentChat();
                        if (!(currentChat == null || ChatObject.hasAdminRights(currentChat) || !currentChat.slowmode_enabled)) {
                            AlertsCreator.createSimpleAlert(ChatAttachAlert.this.getContext(), LocaleController.getString("Slowmode", NUM), LocaleController.getString("SlowmodeSendError", NUM)).show();
                            return;
                        }
                    }
                    if (i == 0) {
                        ChatAttachAlert.this.applyCaption();
                        ChatAttachAlert.this.delegate.didPressedButton(7, false);
                    } else if (i == 1) {
                        ChatAttachAlert.this.applyCaption();
                        ChatAttachAlert.this.delegate.didPressedButton(4, true);
                    } else if (i >= 10) {
                        ChatAttachAlert chatAttachAlert = ChatAttachAlert.this;
                        chatAttachAlert.selectedAlbumEntry = (AlbumEntry) chatAttachAlert.dropDownAlbums.get(i - 10);
                        if (ChatAttachAlert.this.selectedAlbumEntry == ChatAttachAlert.this.galleryAlbumEntry) {
                            ChatAttachAlert.this.dropDown.setText(LocaleController.getString("ChatGallery", NUM));
                        } else {
                            ChatAttachAlert.this.dropDown.setText(ChatAttachAlert.this.selectedAlbumEntry.bucketName);
                        }
                        ChatAttachAlert.this.adapter.notifyDataSetChanged();
                        ChatAttachAlert.this.cameraAttachAdapter.notifyDataSetChanged();
                        ChatAttachAlert.this.layoutManager.scrollToPositionWithOffset(0, (-ChatAttachAlert.this.gridView.getPaddingTop()) + AndroidUtilities.dp(7.0f));
                    }
                }
            }
        });
        ActionBarMenuItem actionBarMenuItem = r0;
        ActionBarMenuItem anonymousClass5 = new ActionBarMenuItem(context, null, 0, Theme.getColor(str)) {
            public void setAlpha(float f) {
                super.setAlpha(f);
                ChatAttachAlert.this.updateSelectedPosition();
                ChatAttachAlert.this.containerView.invalidate();
            }
        };
        this.selectedMenuItem = actionBarMenuItem;
        this.selectedMenuItem.setLongClickEnabled(false);
        this.selectedMenuItem.setIcon(NUM);
        this.selectedMenuItem.setContentDescription(LocaleController.getString("AccDescrMoreOptions", NUM));
        this.selectedMenuItem.addSubItem(0, LocaleController.getString("SendWithoutGrouping", NUM));
        this.selectedMenuItem.addSubItem(1, LocaleController.getString("SendWithoutCompression", NUM));
        this.selectedMenuItem.setVisibility(4);
        this.selectedMenuItem.setAlpha(0.0f);
        this.selectedMenuItem.setSubMenuOpenSide(2);
        this.selectedMenuItem.setDelegate(new -$$Lambda$ChatAttachAlert$Nt32ReYKj24zlB7vHT-vQyIS3fQ(this));
        this.selectedMenuItem.setAdditionalYOffset(AndroidUtilities.dp(72.0f));
        this.selectedMenuItem.setTranslationX((float) AndroidUtilities.dp(6.0f));
        this.selectedMenuItem.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("dialogButtonSelector"), 6));
        this.containerView.addView(this.selectedMenuItem, LayoutHelper.createFrame(48, 48, 53));
        this.selectedMenuItem.setOnClickListener(new -$$Lambda$ChatAttachAlert$ub7wHJjWI52pBpaKjvNcIp8HBK8(this));
        this.gridView = new RecyclerListView(context2) {
            public void setTranslationY(float f) {
                super.setTranslationY(f);
                ChatAttachAlert.this.containerView.invalidate();
            }

            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (motionEvent.getAction() != 0 || motionEvent.getY() >= ((float) (ChatAttachAlert.this.scrollOffsetY - AndroidUtilities.dp(44.0f)))) {
                    return super.onTouchEvent(motionEvent);
                }
                return false;
            }

            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                if (motionEvent.getAction() != 0 || motionEvent.getY() >= ((float) (ChatAttachAlert.this.scrollOffsetY - AndroidUtilities.dp(44.0f)))) {
                    return super.onInterceptTouchEvent(motionEvent);
                }
                return false;
            }
        };
        RecyclerListView recyclerListView = this.gridView;
        PhotoAttachAdapter photoAttachAdapter = new PhotoAttachAdapter(context2, SharedConfig.inappCamera);
        this.adapter = photoAttachAdapter;
        recyclerListView.setAdapter(photoAttachAdapter);
        this.gridView.setClipToPadding(false);
        this.gridView.setItemAnimator(null);
        this.gridView.setLayoutAnimation(null);
        this.gridView.setVerticalScrollBarEnabled(false);
        this.gridView.setGlowColor(Theme.getColor("dialogScrollGlow"));
        this.containerView.addView(this.gridView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 11.0f, 0.0f, 0.0f));
        this.gridView.setOnScrollListener(new OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                if (ChatAttachAlert.this.gridView.getChildCount() > 0) {
                    ChatAttachAlert.this.updateLayout(true);
                    ChatAttachAlert.this.checkCameraViewPosition();
                }
            }

            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                if (i == 0) {
                    if (((ChatAttachAlert.this.scrollOffsetY - ChatAttachAlert.this.backgroundPaddingTop) - (AndroidUtilities.dp(13.0f) + (ChatAttachAlert.this.selectedMenuItem != null ? AndroidUtilities.dp(ChatAttachAlert.this.selectedMenuItem.getAlpha() * 26.0f) : 0))) + ChatAttachAlert.this.backgroundPaddingTop < ActionBar.getCurrentActionBarHeight()) {
                        ChatAttachAlert.this.gridView.getChildAt(0);
                        Holder holder = (Holder) ChatAttachAlert.this.gridView.findViewHolderForAdapterPosition(0);
                        if (holder != null && holder.itemView.getTop() > AndroidUtilities.dp(7.0f)) {
                            ChatAttachAlert.this.gridView.smoothScrollBy(0, holder.itemView.getTop() - AndroidUtilities.dp(7.0f));
                        }
                    }
                }
            }
        });
        this.layoutManager = new GridLayoutManager(context2, this.itemSize) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        this.layoutManager.setSpanSizeLookup(new SpanSizeLookup() {
            public int getSpanSize(int i) {
                if (i == ChatAttachAlert.this.adapter.itemsCount - 1) {
                    return ChatAttachAlert.this.layoutManager.getSpanCount();
                }
                return ChatAttachAlert.this.itemSize + (i % ChatAttachAlert.this.itemsPerRow != ChatAttachAlert.this.itemsPerRow + -1 ? AndroidUtilities.dp(5.0f) : 0);
            }
        });
        this.gridView.setLayoutManager(this.layoutManager);
        this.gridView.setOnItemClickListener(new -$$Lambda$ChatAttachAlert$xPFtpf_jFJqlrSbDDT55lEsjbs8(this));
        this.dropDownContainer = new ActionBarMenuItem(context2, this.actionBar.createMenu(), 0, 0);
        this.dropDownContainer.setSubMenuOpenSide(1);
        this.actionBar.addView(this.dropDownContainer, 0, LayoutHelper.createFrame(-2, -1.0f, 51, AndroidUtilities.isTablet() ? 64.0f : 56.0f, 0.0f, 40.0f, 0.0f));
        this.dropDownContainer.setOnClickListener(new -$$Lambda$ChatAttachAlert$ZqUzbu7OWYZNWU7ujZFdDlGIODQ(this));
        this.dropDown = new TextView(context2);
        this.dropDown.setGravity(3);
        this.dropDown.setSingleLine(true);
        this.dropDown.setLines(1);
        this.dropDown.setMaxLines(1);
        this.dropDown.setEllipsize(TruncateAt.END);
        this.dropDown.setTextColor(Theme.getColor(str));
        this.dropDown.setText(LocaleController.getString("ChatGallery", NUM));
        this.dropDown.setTypeface(AndroidUtilities.getTypeface(str2));
        this.dropDownDrawable = context.getResources().getDrawable(NUM).mutate();
        this.dropDownDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str), Mode.MULTIPLY));
        this.dropDown.setCompoundDrawablePadding(AndroidUtilities.dp(4.0f));
        this.dropDown.setPadding(0, 0, AndroidUtilities.dp(10.0f), 0);
        this.dropDownContainer.addView(this.dropDown, LayoutHelper.createFrame(-2, -2.0f, 16, 16.0f, 0.0f, 0.0f, 0.0f));
        this.actionBarShadow = new View(context2);
        this.actionBarShadow.setAlpha(0.0f);
        this.actionBarShadow.setBackgroundColor(Theme.getColor("dialogShadowLine"));
        this.containerView.addView(this.actionBarShadow, LayoutHelper.createFrame(-1, 1.0f));
        this.progressView = new EmptyTextProgressView(context2);
        this.progressView.setText(LocaleController.getString("NoPhotos", NUM));
        this.progressView.setOnTouchListener(null);
        this.progressView.setTextSize(20);
        this.containerView.addView(this.progressView, LayoutHelper.createFrame(-1, 80.0f));
        if (this.loading) {
            this.progressView.showProgress();
        } else {
            this.progressView.showTextView();
        }
        this.shadow = new View(context2);
        this.shadow.setBackgroundResource(NUM);
        this.containerView.addView(this.shadow, LayoutHelper.createFrame(-1, 3.0f, 83, 0.0f, 0.0f, 0.0f, 92.0f));
        this.buttonsRecyclerView = new RecyclerListView(context2) {
            public void setTranslationY(float f) {
                super.setTranslationY(f);
                ChatAttachAlert.this.checkCameraViewPosition();
            }
        };
        recyclerListView = this.buttonsRecyclerView;
        ButtonsAdapter buttonsAdapter = new ButtonsAdapter(context2);
        this.buttonsAdapter = buttonsAdapter;
        recyclerListView.setAdapter(buttonsAdapter);
        recyclerListView = this.buttonsRecyclerView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context2, 0, false);
        this.buttonsLayoutManager = linearLayoutManager;
        recyclerListView.setLayoutManager(linearLayoutManager);
        this.buttonsRecyclerView.setVerticalScrollBarEnabled(false);
        this.buttonsRecyclerView.setHorizontalScrollBarEnabled(false);
        this.buttonsRecyclerView.setItemAnimator(null);
        this.buttonsRecyclerView.setLayoutAnimation(null);
        this.buttonsRecyclerView.setGlowColor(Theme.getColor("dialogScrollGlow"));
        this.buttonsRecyclerView.setBackgroundColor(Theme.getColor("dialogBackground"));
        this.containerView.addView(this.buttonsRecyclerView, LayoutHelper.createFrame(-1, 92, 83));
        this.buttonsRecyclerView.setOnItemClickListener(new -$$Lambda$ChatAttachAlert$CGRFhKZ328DxCRoZspB6OAhHo70(this));
        this.buttonsRecyclerView.setOnItemLongClickListener(new -$$Lambda$ChatAttachAlert$Cxcgri32QOWerr8y1zoUoscGeOs(this));
        this.frameLayout2 = new FrameLayout(context2);
        this.frameLayout2.setBackgroundColor(Theme.getColor("dialogBackground"));
        this.frameLayout2.setVisibility(4);
        this.frameLayout2.setAlpha(0.0f);
        this.containerView.addView(this.frameLayout2, LayoutHelper.createFrame(-1, 48, 83));
        this.frameLayout2.setOnTouchListener(-$$Lambda$ChatAttachAlert$XuQGCObYg6tWZXAsYQJ4wQvzZL8.INSTANCE);
        this.commentTextView = new EditTextEmoji(context, this.sizeNotifierFrameLayout, null, 1) {
            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                if (!ChatAttachAlert.this.enterCommentEventSent) {
                    ChatAttachAlert.this.delegate.needEnterComment();
                    ChatAttachAlert.this.setFocusable(true);
                    ChatAttachAlert.this.enterCommentEventSent = true;
                    AndroidUtilities.runOnUIThread(new -$$Lambda$ChatAttachAlert$11$XA9gz03UfMVQtNeGArcDNlrz3mE(this));
                }
                return super.onInterceptTouchEvent(motionEvent);
            }

            public /* synthetic */ void lambda$onInterceptTouchEvent$0$ChatAttachAlert$11() {
                ChatAttachAlert.this.commentTextView.openKeyboard();
            }
        };
        this.commentTextView.setFilters(new InputFilter[]{new LengthFilter(MessagesController.getInstance(UserConfig.selectedAccount).maxCaptionLength)});
        this.commentTextView.setHint(LocaleController.getString("AddCaption", NUM));
        this.commentTextView.onResume();
        EditTextBoldCursor editText = this.commentTextView.getEditText();
        editText.setMaxLines(1);
        editText.setSingleLine(true);
        this.frameLayout2.addView(this.commentTextView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 84.0f, 0.0f));
        this.writeButtonContainer = new FrameLayout(context2);
        this.writeButtonContainer.setVisibility(4);
        this.writeButtonContainer.setScaleX(0.2f);
        this.writeButtonContainer.setScaleY(0.2f);
        this.writeButtonContainer.setAlpha(0.0f);
        this.writeButtonContainer.setContentDescription(LocaleController.getString("Send", NUM));
        this.containerView.addView(this.writeButtonContainer, LayoutHelper.createFrame(60, 60.0f, 85, 0.0f, 0.0f, 6.0f, 10.0f));
        this.writeButtonContainer.setOnClickListener(new -$$Lambda$ChatAttachAlert$JW_NkXSvetrexkt88sbSD0ER4t0(this));
        this.writeButtonContainer.setOnLongClickListener(new -$$Lambda$ChatAttachAlert$_zx-_sYLXckhRhP7jZ2D6X1mbRc(this));
        this.writeButton = new ImageView(context2);
        this.writeButtonDrawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor("dialogFloatingButton"), Theme.getColor("dialogFloatingButtonPressed"));
        if (VERSION.SDK_INT < 21) {
            Drawable mutate = context.getResources().getDrawable(NUM).mutate();
            mutate.setColorFilter(new PorterDuffColorFilter(-16777216, Mode.MULTIPLY));
            CombinedDrawable combinedDrawable = new CombinedDrawable(mutate, this.writeButtonDrawable, 0, 0);
            combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
            this.writeButtonDrawable = combinedDrawable;
        }
        this.writeButton.setBackgroundDrawable(this.writeButtonDrawable);
        this.writeButton.setImageResource(NUM);
        this.writeButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogFloatingIcon"), Mode.MULTIPLY));
        this.writeButton.setScaleType(ScaleType.CENTER);
        if (VERSION.SDK_INT >= 21) {
            this.writeButton.setOutlineProvider(new ViewOutlineProvider() {
                @SuppressLint({"NewApi"})
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                }
            });
        }
        this.writeButtonContainer.addView(this.writeButton, LayoutHelper.createFrame(VERSION.SDK_INT >= 21 ? 56 : 60, VERSION.SDK_INT >= 21 ? 56.0f : 60.0f, 51, VERSION.SDK_INT >= 21 ? 2.0f : 0.0f, 0.0f, 0.0f, 0.0f));
        this.textPaint.setTextSize((float) AndroidUtilities.dp(12.0f));
        this.textPaint.setTypeface(AndroidUtilities.getTypeface(str2));
        this.selectedCountView = new View(context2) {
            /* Access modifiers changed, original: protected */
            public void onDraw(Canvas canvas) {
                String format = String.format("%d", new Object[]{Integer.valueOf(Math.max(1, ChatAttachAlert.selectedPhotosOrder.size()))});
                int ceil = (int) Math.ceil((double) ChatAttachAlert.this.textPaint.measureText(format));
                int max = Math.max(AndroidUtilities.dp(16.0f) + ceil, AndroidUtilities.dp(24.0f));
                int measuredWidth = getMeasuredWidth() / 2;
                int measuredHeight = getMeasuredHeight() / 2;
                ChatAttachAlert.this.textPaint.setColor(Theme.getColor("dialogRoundCheckBoxCheck"));
                ChatAttachAlert.this.paint.setColor(Theme.getColor("dialogBackground"));
                max /= 2;
                int i = measuredWidth - max;
                max += measuredWidth;
                ChatAttachAlert.this.rect.set((float) i, 0.0f, (float) max, (float) getMeasuredHeight());
                canvas.drawRoundRect(ChatAttachAlert.this.rect, (float) AndroidUtilities.dp(12.0f), (float) AndroidUtilities.dp(12.0f), ChatAttachAlert.this.paint);
                ChatAttachAlert.this.paint.setColor(Theme.getColor("dialogRoundCheckBox"));
                ChatAttachAlert.this.rect.set((float) (i + AndroidUtilities.dp(2.0f)), (float) AndroidUtilities.dp(2.0f), (float) (max - AndroidUtilities.dp(2.0f)), (float) (getMeasuredHeight() - AndroidUtilities.dp(2.0f)));
                canvas.drawRoundRect(ChatAttachAlert.this.rect, (float) AndroidUtilities.dp(10.0f), (float) AndroidUtilities.dp(10.0f), ChatAttachAlert.this.paint);
                canvas.drawText(format, (float) (measuredWidth - (ceil / 2)), (float) AndroidUtilities.dp(16.2f), ChatAttachAlert.this.textPaint);
            }
        };
        this.selectedCountView.setAlpha(0.0f);
        this.selectedCountView.setScaleX(0.2f);
        this.selectedCountView.setScaleY(0.2f);
        this.containerView.addView(this.selectedCountView, LayoutHelper.createFrame(42, 24.0f, 85, 0.0f, 0.0f, -8.0f, 9.0f));
        this.recordTime = new TextView(context2);
        this.recordTime.setBackgroundResource(NUM);
        this.recordTime.getBackground().setColorFilter(new PorterDuffColorFilter(NUM, Mode.MULTIPLY));
        this.recordTime.setTextSize(1, 15.0f);
        this.recordTime.setTypeface(AndroidUtilities.getTypeface(str2));
        this.recordTime.setAlpha(0.0f);
        this.recordTime.setTextColor(-1);
        this.recordTime.setPadding(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(5.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(5.0f));
        this.container.addView(this.recordTime, LayoutHelper.createFrame(-2, -2.0f, 49, 0.0f, 16.0f, 0.0f, 0.0f));
        this.cameraPanel = new FrameLayout(context2) {
            /* Access modifiers changed, original: protected */
            public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                int measuredWidth;
                int i5;
                int dp;
                if (getMeasuredWidth() == AndroidUtilities.dp(126.0f)) {
                    measuredWidth = getMeasuredWidth() / 2;
                    i = getMeasuredHeight() / 2;
                    i4 = getMeasuredWidth() / 2;
                    i5 = i / 2;
                    dp = (i + i5) + AndroidUtilities.dp(17.0f);
                    i5 -= AndroidUtilities.dp(17.0f);
                    ChatAttachAlert.this.tooltipTextView.setAlpha(0.0f);
                    i2 = i4;
                } else {
                    measuredWidth = getMeasuredWidth() / 2;
                    i = (getMeasuredHeight() / 2) - AndroidUtilities.dp(13.0f);
                    i5 = measuredWidth / 2;
                    dp = (measuredWidth + i5) + AndroidUtilities.dp(17.0f);
                    i2 = i5 - AndroidUtilities.dp(17.0f);
                    i4 = (getMeasuredHeight() / 2) - AndroidUtilities.dp(13.0f);
                    ChatAttachAlert.this.tooltipTextView.setAlpha(1.0f);
                    i5 = i4;
                    i4 = dp;
                    dp = i5;
                }
                int measuredHeight = (getMeasuredHeight() - ChatAttachAlert.this.tooltipTextView.getMeasuredHeight()) - AndroidUtilities.dp(12.0f);
                ChatAttachAlert.this.tooltipTextView.layout(measuredWidth - (ChatAttachAlert.this.tooltipTextView.getMeasuredWidth() / 2), measuredHeight, (ChatAttachAlert.this.tooltipTextView.getMeasuredWidth() / 2) + measuredWidth, ChatAttachAlert.this.tooltipTextView.getMeasuredHeight() + measuredHeight);
                ChatAttachAlert.this.shutterButton.layout(measuredWidth - (ChatAttachAlert.this.shutterButton.getMeasuredWidth() / 2), i - (ChatAttachAlert.this.shutterButton.getMeasuredHeight() / 2), measuredWidth + (ChatAttachAlert.this.shutterButton.getMeasuredWidth() / 2), i + (ChatAttachAlert.this.shutterButton.getMeasuredHeight() / 2));
                ChatAttachAlert.this.switchCameraButton.layout(i4 - (ChatAttachAlert.this.switchCameraButton.getMeasuredWidth() / 2), dp - (ChatAttachAlert.this.switchCameraButton.getMeasuredHeight() / 2), i4 + (ChatAttachAlert.this.switchCameraButton.getMeasuredWidth() / 2), dp + (ChatAttachAlert.this.switchCameraButton.getMeasuredHeight() / 2));
                for (measuredWidth = 0; measuredWidth < 2; measuredWidth++) {
                    ChatAttachAlert.this.flashModeButton[measuredWidth].layout(i2 - (ChatAttachAlert.this.flashModeButton[measuredWidth].getMeasuredWidth() / 2), i5 - (ChatAttachAlert.this.flashModeButton[measuredWidth].getMeasuredHeight() / 2), (ChatAttachAlert.this.flashModeButton[measuredWidth].getMeasuredWidth() / 2) + i2, (ChatAttachAlert.this.flashModeButton[measuredWidth].getMeasuredHeight() / 2) + i5);
                }
            }
        };
        this.cameraPanel.setVisibility(8);
        this.cameraPanel.setAlpha(0.0f);
        this.container.addView(this.cameraPanel, LayoutHelper.createFrame(-1, 126, 83));
        this.counterTextView = new TextView(context2);
        this.counterTextView.setBackgroundResource(NUM);
        this.counterTextView.setVisibility(8);
        this.counterTextView.setTextColor(-1);
        this.counterTextView.setGravity(17);
        this.counterTextView.setPivotX(0.0f);
        this.counterTextView.setPivotY(0.0f);
        this.counterTextView.setTypeface(AndroidUtilities.getTypeface(str2));
        this.counterTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, NUM, 0);
        this.counterTextView.setCompoundDrawablePadding(AndroidUtilities.dp(4.0f));
        this.counterTextView.setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f), 0);
        this.container.addView(this.counterTextView, LayoutHelper.createFrame(-2, 38.0f, 51, 0.0f, 0.0f, 0.0f, 116.0f));
        this.counterTextView.setOnClickListener(new -$$Lambda$ChatAttachAlert$NzZN_cBHTLgbSc_e476thcVnIYM(this));
        this.zoomControlView = new ZoomControlView(context2);
        this.zoomControlView.setVisibility(8);
        this.zoomControlView.setAlpha(0.0f);
        this.container.addView(this.zoomControlView, LayoutHelper.createFrame(-2, 50.0f, 51, 0.0f, 0.0f, 0.0f, 116.0f));
        this.zoomControlView.setDelegate(new -$$Lambda$ChatAttachAlert$3BfCW5Hi3NkjcR3GS7tv_GOnYQQ(this));
        this.shutterButton = new ShutterButton(context2);
        this.cameraPanel.addView(this.shutterButton, LayoutHelper.createFrame(84, 84, 17));
        this.shutterButton.setDelegate(new ShutterButtonDelegate() {
            private File outputFile;

            public boolean shutterLongPressed() {
                boolean z = ChatAttachAlert.this.baseFragment instanceof ChatActivity;
                Integer valueOf = Integer.valueOf(0);
                if (!z || ChatAttachAlert.this.mediaCaptured || ChatAttachAlert.this.takingPhoto || ChatAttachAlert.this.baseFragment == null || ChatAttachAlert.this.baseFragment.getParentActivity() == null || ChatAttachAlert.this.cameraView == null) {
                    return false;
                }
                if (VERSION.SDK_INT >= 23) {
                    if (ChatAttachAlert.this.baseFragment.getParentActivity().checkSelfPermission("android.permission.RECORD_AUDIO") != 0) {
                        ChatAttachAlert.this.requestingPermissions = true;
                        ChatAttachAlert.this.baseFragment.getParentActivity().requestPermissions(new String[]{r3}, 21);
                        return false;
                    }
                }
                for (int i = 0; i < 2; i++) {
                    ChatAttachAlert.this.flashModeButton[i].setAlpha(0.0f);
                }
                ChatAttachAlert.this.switchCameraButton.setAlpha(0.0f);
                z = (ChatAttachAlert.this.baseFragment instanceof ChatActivity) && ((ChatActivity) ChatAttachAlert.this.baseFragment).isSecretChat();
                this.outputFile = AndroidUtilities.generateVideoPath(z);
                ChatAttachAlert.this.recordTime.setAlpha(1.0f);
                ChatAttachAlert.this.recordTime.setText(String.format("%02d:%02d", new Object[]{valueOf, valueOf}));
                ChatAttachAlert.this.videoRecordTime = 0;
                ChatAttachAlert.this.videoRecordRunnable = new -$$Lambda$ChatAttachAlert$16$c-e4TsgTqUuFgEbKMHYz-O5k6jY(this);
                AndroidUtilities.lockOrientation(baseFragment2.getParentActivity());
                CameraController.getInstance().recordVideo(ChatAttachAlert.this.cameraView.getCameraSession(), this.outputFile, new -$$Lambda$ChatAttachAlert$16$U1efNvK3inSha-yFjdxGSptLp0Q(this), new -$$Lambda$ChatAttachAlert$16$Nz0oIYwfmLhYecMGK0500Tpmr2o(this));
                ChatAttachAlert.this.shutterButton.setState(State.RECORDING, true);
                return true;
            }

            public /* synthetic */ void lambda$shutterLongPressed$0$ChatAttachAlert$16() {
                if (ChatAttachAlert.this.videoRecordRunnable != null) {
                    ChatAttachAlert.this.videoRecordTime = ChatAttachAlert.this.videoRecordTime + 1;
                    ChatAttachAlert.this.recordTime.setText(String.format("%02d:%02d", new Object[]{Integer.valueOf(ChatAttachAlert.this.videoRecordTime / 60), Integer.valueOf(ChatAttachAlert.this.videoRecordTime % 60)}));
                    AndroidUtilities.runOnUIThread(ChatAttachAlert.this.videoRecordRunnable, 1000);
                }
            }

            public /* synthetic */ void lambda$shutterLongPressed$1$ChatAttachAlert$16(String str, long j) {
                if (this.outputFile != null && ChatAttachAlert.this.baseFragment != null) {
                    ChatAttachAlert.mediaFromExternalCamera = false;
                    PhotoEntry photoEntry = new PhotoEntry(0, ChatAttachAlert.access$8510(), 0, this.outputFile.getAbsolutePath(), 0, true);
                    photoEntry.duration = (int) j;
                    photoEntry.thumbPath = str;
                    ChatAttachAlert.this.openPhotoViewer(photoEntry, false, false);
                }
            }

            public /* synthetic */ void lambda$shutterLongPressed$2$ChatAttachAlert$16() {
                AndroidUtilities.runOnUIThread(ChatAttachAlert.this.videoRecordRunnable, 1000);
            }

            public void shutterCancel() {
                if (!ChatAttachAlert.this.mediaCaptured) {
                    File file = this.outputFile;
                    if (file != null) {
                        file.delete();
                        this.outputFile = null;
                    }
                    ChatAttachAlert.this.resetRecordState();
                    CameraController.getInstance().stopVideoRecording(ChatAttachAlert.this.cameraView.getCameraSession(), true);
                }
            }

            public void shutterReleased() {
                if (!(ChatAttachAlert.this.takingPhoto || ChatAttachAlert.this.cameraView == null || ChatAttachAlert.this.mediaCaptured || ChatAttachAlert.this.cameraView.getCameraSession() == null)) {
                    boolean z = true;
                    ChatAttachAlert.this.mediaCaptured = true;
                    if (ChatAttachAlert.this.shutterButton.getState() == State.RECORDING) {
                        ChatAttachAlert.this.resetRecordState();
                        CameraController.getInstance().stopVideoRecording(ChatAttachAlert.this.cameraView.getCameraSession(), false);
                        ChatAttachAlert.this.shutterButton.setState(State.DEFAULT, true);
                        return;
                    }
                    if (!((ChatAttachAlert.this.baseFragment instanceof ChatActivity) && ((ChatActivity) ChatAttachAlert.this.baseFragment).isSecretChat())) {
                        z = false;
                    }
                    File generatePicturePath = AndroidUtilities.generatePicturePath(z);
                    z = ChatAttachAlert.this.cameraView.getCameraSession().isSameTakePictureOrientation();
                    ChatAttachAlert.this.cameraView.getCameraSession().setFlipFront(baseFragment2 instanceof ChatActivity);
                    ChatAttachAlert.this.takingPhoto = CameraController.getInstance().takePicture(generatePicturePath, ChatAttachAlert.this.cameraView.getCameraSession(), new -$$Lambda$ChatAttachAlert$16$oKQFI_KRAVbQh5Hv5qDDRnquI7s(this, generatePicturePath, z));
                }
            }

            public /* synthetic */ void lambda$shutterReleased$3$ChatAttachAlert$16(File file, boolean z) {
                ChatAttachAlert.this.takingPhoto = false;
                if (file != null && ChatAttachAlert.this.baseFragment != null) {
                    int i;
                    try {
                        int attributeInt = new ExifInterface(file.getAbsolutePath()).getAttributeInt("Orientation", 1);
                        attributeInt = attributeInt != 3 ? attributeInt != 6 ? attributeInt != 8 ? 0 : 270 : 90 : 180;
                        i = attributeInt;
                    } catch (Exception e) {
                        FileLog.e(e);
                        i = 0;
                    }
                    ChatAttachAlert.mediaFromExternalCamera = false;
                    PhotoEntry photoEntry = new PhotoEntry(0, ChatAttachAlert.access$8510(), 0, file.getAbsolutePath(), i, false);
                    photoEntry.canDeleteAfter = true;
                    ChatAttachAlert.this.openPhotoViewer(photoEntry, z, false);
                }
            }
        });
        this.shutterButton.setFocusable(true);
        this.shutterButton.setContentDescription(LocaleController.getString("AccDescrShutter", NUM));
        this.switchCameraButton = new ImageView(context2);
        this.switchCameraButton.setScaleType(ScaleType.CENTER);
        this.cameraPanel.addView(this.switchCameraButton, LayoutHelper.createFrame(48, 48, 21));
        this.switchCameraButton.setOnClickListener(new -$$Lambda$ChatAttachAlert$GVq7vHOXKmoC0QXxaiFtf8tym24(this));
        this.switchCameraButton.setContentDescription(LocaleController.getString("AccDescrSwitchCamera", NUM));
        for (int i2 = 0; i2 < 2; i2++) {
            this.flashModeButton[i2] = new ImageView(context2);
            this.flashModeButton[i2].setScaleType(ScaleType.CENTER);
            this.flashModeButton[i2].setVisibility(4);
            this.cameraPanel.addView(this.flashModeButton[i2], LayoutHelper.createFrame(48, 48, 51));
            this.flashModeButton[i2].setOnClickListener(new -$$Lambda$ChatAttachAlert$GmiOLhg2Nufzy6t_kr2LXAC4J90(this));
            ImageView imageView = this.flashModeButton[i2];
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("flash mode ");
            stringBuilder.append(i2);
            imageView.setContentDescription(stringBuilder.toString());
        }
        this.tooltipTextView = new TextView(context2);
        this.tooltipTextView.setTextSize(1, 15.0f);
        this.tooltipTextView.setTextColor(-1);
        this.tooltipTextView.setText(LocaleController.getString("TapForVideo", NUM));
        this.tooltipTextView.setShadowLayer((float) AndroidUtilities.dp(3.33333f), 0.0f, (float) AndroidUtilities.dp(0.666f), NUM);
        this.tooltipTextView.setPadding(AndroidUtilities.dp(6.0f), 0, AndroidUtilities.dp(6.0f), 0);
        this.cameraPanel.addView(this.tooltipTextView, LayoutHelper.createFrame(-2, -2.0f, 81, 0.0f, 0.0f, 0.0f, 16.0f));
        this.cameraPhotoRecyclerView = new RecyclerListView(context2) {
            public void requestLayout() {
                if (!ChatAttachAlert.this.cameraPhotoRecyclerViewIgnoreLayout) {
                    super.requestLayout();
                }
            }
        };
        this.cameraPhotoRecyclerView.setVerticalScrollBarEnabled(true);
        recyclerListView = this.cameraPhotoRecyclerView;
        photoAttachAdapter = new PhotoAttachAdapter(context2, false);
        this.cameraAttachAdapter = photoAttachAdapter;
        recyclerListView.setAdapter(photoAttachAdapter);
        this.cameraPhotoRecyclerView.setClipToPadding(false);
        this.cameraPhotoRecyclerView.setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f), 0);
        this.cameraPhotoRecyclerView.setItemAnimator(null);
        this.cameraPhotoRecyclerView.setLayoutAnimation(null);
        this.cameraPhotoRecyclerView.setOverScrollMode(2);
        this.cameraPhotoRecyclerView.setVisibility(4);
        this.cameraPhotoRecyclerView.setAlpha(0.0f);
        this.container.addView(this.cameraPhotoRecyclerView, LayoutHelper.createFrame(-1, 80.0f));
        this.cameraPhotoLayoutManager = new LinearLayoutManager(context2, 0, false) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        this.cameraPhotoRecyclerView.setLayoutManager(this.cameraPhotoLayoutManager);
        this.cameraPhotoRecyclerView.setOnItemClickListener(-$$Lambda$ChatAttachAlert$s38dCpJHgXH2VJoFh4IUQTng7cA.INSTANCE);
    }

    public /* synthetic */ void lambda$new$0$ChatAttachAlert(int i) {
        this.actionBar.getActionBarMenuOnItemClick().onItemClick(i);
    }

    public /* synthetic */ void lambda$new$1$ChatAttachAlert(View view) {
        this.selectedMenuItem.toggleSubMenu();
    }

    public /* synthetic */ void lambda$new$2$ChatAttachAlert(View view, int i) {
        if (this.mediaEnabled) {
            BaseFragment baseFragment = this.baseFragment;
            if (!(baseFragment == null || baseFragment.getParentActivity() == null)) {
                if (VERSION.SDK_INT >= 23) {
                    if (i == 0 && this.noCameraPermissions) {
                        try {
                            this.baseFragment.getParentActivity().requestPermissions(new String[]{"android.permission.CAMERA"}, 18);
                        } catch (Exception unused) {
                        }
                        return;
                    } else if (this.noGalleryPermissions) {
                        try {
                            this.baseFragment.getParentActivity().requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 4);
                        } catch (Exception unused2) {
                        }
                        return;
                    }
                }
                if (this.deviceHasGoodCamera && i == 0 && this.selectedAlbumEntry == this.galleryAlbumEntry) {
                    openCamera(true);
                } else {
                    if ((this.deviceHasGoodCamera || this.noCameraPermissions) && this.selectedAlbumEntry == this.galleryAlbumEntry) {
                        i--;
                    }
                    int i2 = i;
                    ArrayList allPhotosArray = getAllPhotosArray();
                    if (i2 >= 0 && i2 < allPhotosArray.size()) {
                        ChatActivity chatActivity;
                        int i3;
                        PhotoViewer.getInstance().setParentActivity(this.baseFragment.getParentActivity());
                        PhotoViewer.getInstance().setParentAlert(this);
                        PhotoViewer.getInstance().setMaxSelectedPhotos(this.maxSelectedPhotos, this.allowOrder);
                        baseFragment = this.baseFragment;
                        if (baseFragment instanceof ChatActivity) {
                            chatActivity = (ChatActivity) baseFragment;
                            i3 = 0;
                        } else {
                            chatActivity = null;
                            i3 = 4;
                        }
                        PhotoViewer.getInstance().openPhotoForSelect(allPhotosArray, i2, i3, this.photoViewerProvider, chatActivity);
                        AndroidUtilities.hideKeyboard(this.baseFragment.getFragmentView().findFocus());
                    }
                }
            }
        }
    }

    public /* synthetic */ void lambda$new$3$ChatAttachAlert(View view) {
        this.dropDownContainer.toggleSubMenu();
    }

    public /* synthetic */ void lambda$new$4$ChatAttachAlert(View view, int i) {
        if (view instanceof AttachButton) {
            this.delegate.didPressedButton(((Integer) ((AttachButton) view).getTag()).intValue(), true);
        } else if (view instanceof AttachBotButton) {
            this.delegate.didSelectBot(((AttachBotButton) view).currentUser);
            dismiss();
        }
    }

    public /* synthetic */ boolean lambda$new$6$ChatAttachAlert(View view, int i) {
        if (view instanceof AttachBotButton) {
            AttachBotButton attachBotButton = (AttachBotButton) view;
            if (!(this.baseFragment == null || attachBotButton.currentUser == null)) {
                Builder builder = new Builder(getContext());
                builder.setTitle(LocaleController.getString("AppName", NUM));
                builder.setMessage(LocaleController.formatString("ChatHintsDelete", NUM, ContactsController.formatName(attachBotButton.currentUser.first_name, attachBotButton.currentUser.last_name)));
                builder.setPositiveButton(LocaleController.getString("OK", NUM), new -$$Lambda$ChatAttachAlert$MCQi1Z8Zj7emo3cU425Y68XmS88(this, attachBotButton));
                builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
                builder.show();
                return true;
            }
        }
        return false;
    }

    public /* synthetic */ void lambda$null$5$ChatAttachAlert(AttachBotButton attachBotButton, DialogInterface dialogInterface, int i) {
        MediaDataController.getInstance(this.currentAccount).removeInline(attachBotButton.currentUser.id);
    }

    public /* synthetic */ void lambda$new$8$ChatAttachAlert(View view) {
        sendPressed(false);
    }

    public /* synthetic */ boolean lambda$new$11$ChatAttachAlert(View view) {
        BaseFragment baseFragment = this.baseFragment;
        if (!(baseFragment instanceof ChatActivity)) {
            return false;
        }
        ChatActivity chatActivity = (ChatActivity) baseFragment;
        Chat currentChat = chatActivity.getCurrentChat();
        User currentUser = chatActivity.getCurrentUser();
        if (chatActivity.getCurrentEncryptedChat() == null && ((!ChatObject.isChannel(currentChat) || currentChat.megagroup) && !UserObject.isUserSelf(currentUser))) {
            if (this.sendPopupLayout == null) {
                this.sendPopupLayout = new ActionBarPopupWindowLayout(getContext());
                this.sendPopupLayout.setAnimationEnabled(false);
                this.sendPopupLayout.setOnTouchListener(new OnTouchListener() {
                    private Rect popupRect = new Rect();

                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        if (motionEvent.getActionMasked() == 0 && ChatAttachAlert.this.sendPopupWindow != null && ChatAttachAlert.this.sendPopupWindow.isShowing()) {
                            view.getHitRect(this.popupRect);
                            if (!this.popupRect.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
                                ChatAttachAlert.this.sendPopupWindow.dismiss();
                            }
                        }
                        return false;
                    }
                });
                this.sendPopupLayout.setDispatchKeyEventListener(new -$$Lambda$ChatAttachAlert$XQpLvgfyGRaPx8CDJ5e0e1Ft3v4(this));
                this.sendPopupLayout.setShowedFromBotton(false);
                this.itemCell = new ActionBarMenuSubItem(getContext());
                this.itemCell.setTextAndIcon(LocaleController.getString("SendWithoutSound", NUM), NUM);
                this.itemCell.setMinimumWidth(AndroidUtilities.dp(196.0f));
                this.sendPopupLayout.addView(this.itemCell, LayoutHelper.createFrame(-1, 48, LocaleController.isRTL ? 5 : 3));
                this.itemCell.setOnClickListener(new -$$Lambda$ChatAttachAlert$XtqQ-vWZtsHF5eNqHUdYC3gRBMU(this));
                this.sendPopupWindow = new ActionBarPopupWindow(this.sendPopupLayout, -2, -2);
                this.sendPopupWindow.setAnimationEnabled(false);
                this.sendPopupWindow.setAnimationStyle(NUM);
                this.sendPopupWindow.setOutsideTouchable(true);
                this.sendPopupWindow.setClippingEnabled(true);
                this.sendPopupWindow.setInputMethodMode(2);
                this.sendPopupWindow.setSoftInputMode(0);
                this.sendPopupWindow.getContentView().setFocusableInTouchMode(true);
            }
            this.sendPopupLayout.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE));
            this.sendPopupWindow.setFocusable(true);
            int[] iArr = new int[2];
            view.getLocationInWindow(iArr);
            this.sendPopupWindow.showAtLocation(view, 51, ((iArr[0] + view.getMeasuredWidth()) - this.sendPopupLayout.getMeasuredWidth()) + AndroidUtilities.dp(8.0f), (iArr[1] - this.sendPopupLayout.getMeasuredHeight()) - AndroidUtilities.dp(2.0f));
            this.sendPopupWindow.dimBehind();
            view.performHapticFeedback(3, 2);
        }
        return false;
    }

    public /* synthetic */ void lambda$null$9$ChatAttachAlert(KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0) {
            ActionBarPopupWindow actionBarPopupWindow = this.sendPopupWindow;
            if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
                this.sendPopupWindow.dismiss();
            }
        }
    }

    public /* synthetic */ void lambda$null$10$ChatAttachAlert(View view) {
        ActionBarPopupWindow actionBarPopupWindow = this.sendPopupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
        sendPressed(true);
    }

    public /* synthetic */ void lambda$new$12$ChatAttachAlert(View view) {
        if (this.cameraView != null) {
            openPhotoViewer(null, false, false);
            CameraController.getInstance().stopPreview(this.cameraView.getCameraSession());
        }
    }

    public /* synthetic */ void lambda$new$13$ChatAttachAlert(float f) {
        CameraView cameraView = this.cameraView;
        this.cameraZoom = f;
        cameraView.setZoom(f);
    }

    public /* synthetic */ void lambda$new$14$ChatAttachAlert(View view) {
        if (!this.takingPhoto) {
            CameraView cameraView = this.cameraView;
            if (cameraView != null && cameraView.isInitied()) {
                this.cameraInitied = false;
                this.cameraView.switchCamera();
                ObjectAnimator duration = ObjectAnimator.ofFloat(this.switchCameraButton, View.SCALE_X, new float[]{0.0f}).setDuration(100);
                duration.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        ImageView access$7400 = ChatAttachAlert.this.switchCameraButton;
                        int i = (ChatAttachAlert.this.cameraView == null || !ChatAttachAlert.this.cameraView.isFrontface()) ? NUM : NUM;
                        access$7400.setImageResource(i);
                        ObjectAnimator.ofFloat(ChatAttachAlert.this.switchCameraButton, View.SCALE_X, new float[]{1.0f}).setDuration(100).start();
                    }
                });
                duration.start();
            }
        }
    }

    public /* synthetic */ void lambda$new$15$ChatAttachAlert(final View view) {
        if (!this.flashAnimationInProgress) {
            CameraView cameraView = this.cameraView;
            if (cameraView != null && cameraView.isInitied() && this.cameraOpened) {
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
                    r4 = new Animator[4];
                    r4[0] = ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, new float[]{0.0f, (float) AndroidUtilities.dp(48.0f)});
                    r4[1] = ObjectAnimator.ofFloat(imageView, View.TRANSLATION_Y, new float[]{(float) (-AndroidUtilities.dp(48.0f)), 0.0f});
                    r4[2] = ObjectAnimator.ofFloat(view, View.ALPHA, new float[]{1.0f, 0.0f});
                    r4[3] = ObjectAnimator.ofFloat(imageView, View.ALPHA, new float[]{0.0f, 1.0f});
                    animatorSet.playTogether(r4);
                    animatorSet.setDuration(200);
                    animatorSet.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            ChatAttachAlert.this.flashAnimationInProgress = false;
                            view.setVisibility(4);
                            imageView.sendAccessibilityEvent(8);
                        }
                    });
                    animatorSet.start();
                }
            }
        }
    }

    static /* synthetic */ void lambda$new$16(View view, int i) {
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

    private void applyCaption() {
        if (this.commentTextView.length() > 0) {
            Object obj = selectedPhotos.get(Integer.valueOf(((Integer) selectedPhotosOrder.get(0)).intValue()));
            if (obj instanceof PhotoEntry) {
                ((PhotoEntry) obj).caption = this.commentTextView.getText().toString();
            } else if (obj instanceof SearchImage) {
                ((SearchImage) obj).caption = this.commentTextView.getText().toString();
            }
        }
    }

    private void sendPressed(boolean z) {
        if (!this.buttonPressed) {
            BaseFragment baseFragment = this.baseFragment;
            if (baseFragment instanceof ChatActivity) {
                ChatActivity chatActivity = (ChatActivity) baseFragment;
                Chat currentChat = chatActivity.getCurrentChat();
                if (chatActivity.getCurrentUser() != null || ((ChatObject.isChannel(currentChat) && currentChat.megagroup) || !ChatObject.isChannel(currentChat))) {
                    Editor edit = MessagesController.getNotificationsSettings(this.currentAccount).edit();
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("silent_");
                    stringBuilder.append(chatActivity.getDialogId());
                    edit.putBoolean(stringBuilder.toString(), z).commit();
                }
            }
            applyCaption();
            this.buttonPressed = true;
            this.delegate.didPressedButton(7, true);
        }
    }

    private void updatePhotosCounter() {
        if (this.counterTextView != null) {
            Object obj = null;
            Object obj2 = null;
            for (Entry value : selectedPhotos.entrySet()) {
                if (((PhotoEntry) value.getValue()).isVideo) {
                    obj = 1;
                } else {
                    obj2 = 1;
                }
                if (obj != null && r2 != null) {
                    break;
                }
            }
            int max = Math.max(1, selectedPhotos.size());
            if (obj != null && r2 != null) {
                this.counterTextView.setText(LocaleController.formatPluralString("Media", selectedPhotos.size()).toUpperCase());
                if (max != this.currentSelectedCount) {
                    this.selectedTextView.setText(LocaleController.formatPluralString("MediaSelected", max));
                }
            } else if (obj != null) {
                this.counterTextView.setText(LocaleController.formatPluralString("Videos", selectedPhotos.size()).toUpperCase());
                if (max != this.currentSelectedCount) {
                    this.selectedTextView.setText(LocaleController.formatPluralString("VideosSelected", max));
                }
            } else {
                this.counterTextView.setText(LocaleController.formatPluralString("Photos", selectedPhotos.size()).toUpperCase());
                if (max != this.currentSelectedCount) {
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
        AnimatorSet animatorSet = this.animatorSet;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        this.frameLayout2.setTag(z ? Integer.valueOf(1) : null);
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
            frameLayout = this.writeButtonContainer;
            property = View.SCALE_X;
            fArr = new float[1];
            fArr[0] = z ? 1.0f : 0.2f;
            arrayList.add(ObjectAnimator.ofFloat(frameLayout, property, fArr));
            frameLayout = this.writeButtonContainer;
            property = View.SCALE_Y;
            fArr = new float[1];
            fArr[0] = z ? 1.0f : 0.2f;
            arrayList.add(ObjectAnimator.ofFloat(frameLayout, property, fArr));
            frameLayout = this.writeButtonContainer;
            property = View.ALPHA;
            fArr = new float[1];
            fArr[0] = z ? 1.0f : 0.0f;
            arrayList.add(ObjectAnimator.ofFloat(frameLayout, property, fArr));
            View view = this.selectedCountView;
            property = View.SCALE_X;
            fArr = new float[1];
            fArr[0] = z ? 1.0f : 0.2f;
            arrayList.add(ObjectAnimator.ofFloat(view, property, fArr));
            view = this.selectedCountView;
            property = View.SCALE_Y;
            fArr = new float[1];
            if (z) {
                f = 1.0f;
            }
            fArr[0] = f;
            arrayList.add(ObjectAnimator.ofFloat(view, property, fArr));
            View view2 = this.selectedCountView;
            Property property2 = View.ALPHA;
            float[] fArr2 = new float[1];
            fArr2[0] = z ? 1.0f : 0.0f;
            arrayList.add(ObjectAnimator.ofFloat(view2, property2, fArr2));
            View view3;
            Property property3;
            if (this.actionBar.getTag() != null) {
                FrameLayout frameLayout2 = this.frameLayout2;
                property2 = View.TRANSLATION_Y;
                fArr2 = new float[1];
                fArr2[0] = z ? 0.0f : (float) AndroidUtilities.dp(48.0f);
                arrayList.add(ObjectAnimator.ofFloat(frameLayout2, property2, fArr2));
                View view4 = this.shadow;
                Property property4 = View.TRANSLATION_Y;
                float[] fArr3 = new float[1];
                fArr3[0] = (float) (z ? AndroidUtilities.dp(44.0f) : AndroidUtilities.dp(92.0f));
                arrayList.add(ObjectAnimator.ofFloat(view4, property4, fArr3));
                view3 = this.shadow;
                property3 = View.ALPHA;
                float[] fArr4 = new float[1];
                if (z) {
                    f2 = 1.0f;
                }
                fArr4[0] = f2;
                arrayList.add(ObjectAnimator.ofFloat(view3, property3, fArr4));
            } else {
                RecyclerListView recyclerListView = this.buttonsRecyclerView;
                property3 = View.TRANSLATION_Y;
                float[] fArr5 = new float[1];
                fArr5[0] = z ? (float) AndroidUtilities.dp(44.0f) : 0.0f;
                arrayList.add(ObjectAnimator.ofFloat(recyclerListView, property3, fArr5));
                view3 = this.shadow;
                property3 = View.TRANSLATION_Y;
                fArr5 = new float[1];
                if (z) {
                    f2 = (float) AndroidUtilities.dp(44.0f);
                }
                fArr5[0] = f2;
                arrayList.add(ObjectAnimator.ofFloat(view3, property3, fArr5));
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
                        ChatAttachAlert.this.animatorSet = null;
                    }
                }

                public void onAnimationCancel(Animator animator) {
                    if (animator.equals(ChatAttachAlert.this.animatorSet)) {
                        ChatAttachAlert.this.animatorSet = null;
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
            View view5 = this.selectedCountView;
            if (z) {
                f = 1.0f;
            }
            view5.setScaleY(f);
            this.selectedCountView.setAlpha(z ? 1.0f : 0.0f);
            if (this.actionBar.getTag() != null) {
                this.frameLayout2.setTranslationY(z ? 0.0f : (float) AndroidUtilities.dp(48.0f));
                this.shadow.setTranslationY((float) (z ? AndroidUtilities.dp(44.0f) : AndroidUtilities.dp(92.0f)));
                view5 = this.shadow;
                if (z) {
                    f2 = 1.0f;
                }
                view5.setAlpha(f2);
            } else {
                this.buttonsRecyclerView.setTranslationY(z ? (float) AndroidUtilities.dp(44.0f) : 0.0f);
                view5 = this.shadow;
                if (z) {
                    f2 = (float) AndroidUtilities.dp(44.0f);
                }
                view5.setTranslationY(f2);
            }
            if (!z) {
                this.frameLayout2.setVisibility(4);
                this.writeButtonContainer.setVisibility(4);
            }
        }
        return true;
    }

    /* Access modifiers changed, original: protected */
    public boolean onCustomOpenAnimation() {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this, this.ATTACH_ALERT_PROGRESS, new float[]{0.0f, 400.0f})});
        animatorSet.setDuration(400);
        animatorSet.setStartDelay(20);
        animatorSet.start();
        return false;
    }

    private void openPhotoViewer(PhotoEntry photoEntry, final boolean z, boolean z2) {
        if (photoEntry != null) {
            cameraPhotos.add(photoEntry);
            selectedPhotos.put(Integer.valueOf(photoEntry.imageId), photoEntry);
            selectedPhotosOrder.add(Integer.valueOf(photoEntry.imageId));
            updatePhotosButton(0);
            this.adapter.notifyDataSetChanged();
            this.cameraAttachAdapter.notifyDataSetChanged();
        }
        if (photoEntry != null && !z2 && cameraPhotos.size() > 1) {
            updatePhotosCounter();
            if (this.cameraView != null) {
                CameraController.getInstance().startPreview(this.cameraView.getCameraSession());
            }
            this.mediaCaptured = false;
        } else if (!cameraPhotos.isEmpty()) {
            ChatActivity chatActivity;
            int i;
            this.cancelTakingPhotos = true;
            PhotoViewer.getInstance().setParentActivity(this.baseFragment.getParentActivity());
            PhotoViewer.getInstance().setParentAlert(this);
            PhotoViewer.getInstance().setMaxSelectedPhotos(this.maxSelectedPhotos, this.allowOrder);
            BaseFragment baseFragment = this.baseFragment;
            if (baseFragment instanceof ChatActivity) {
                chatActivity = (ChatActivity) baseFragment;
                i = 2;
            } else {
                chatActivity = null;
                i = 5;
            }
            PhotoViewer.getInstance().openPhotoForSelect(getAllPhotosArray(), cameraPhotos.size() - 1, i, new BasePhotoProvider() {
                public boolean canScrollAway() {
                    return false;
                }

                public BitmapHolder getThumbForPhoto(MessageObject messageObject, FileLocation fileLocation, int i) {
                    return null;
                }

                public boolean cancelButtonPressed() {
                    if (ChatAttachAlert.this.cameraOpened && ChatAttachAlert.this.cameraView != null) {
                        AndroidUtilities.runOnUIThread(new -$$Lambda$ChatAttachAlert$23$Bz9T3nbGV3K_aZB3yOisVBdqZCA(this), 1000);
                        CameraController.getInstance().startPreview(ChatAttachAlert.this.cameraView.getCameraSession());
                    }
                    if (ChatAttachAlert.this.cancelTakingPhotos && ChatAttachAlert.cameraPhotos.size() == 1) {
                        int size = ChatAttachAlert.cameraPhotos.size();
                        for (int i = 0; i < size; i++) {
                            PhotoEntry photoEntry = (PhotoEntry) ChatAttachAlert.cameraPhotos.get(i);
                            new File(photoEntry.path).delete();
                            String str = photoEntry.imagePath;
                            if (str != null) {
                                new File(str).delete();
                            }
                            String str2 = photoEntry.thumbPath;
                            if (str2 != null) {
                                new File(str2).delete();
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

                public /* synthetic */ void lambda$cancelButtonPressed$0$ChatAttachAlert$23() {
                    if (ChatAttachAlert.this.cameraView != null && !ChatAttachAlert.this.isDismissed() && VERSION.SDK_INT >= 21) {
                        ChatAttachAlert.this.cameraView.setSystemUiVisibility(1028);
                    }
                }

                public void needAddMorePhotos() {
                    ChatAttachAlert.this.cancelTakingPhotos = false;
                    if (ChatAttachAlert.mediaFromExternalCamera) {
                        ChatAttachAlert.this.delegate.didPressedButton(0, true);
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

                public void sendButtonPressed(int i, VideoEditedInfo videoEditedInfo) {
                    if (!ChatAttachAlert.cameraPhotos.isEmpty() && ChatAttachAlert.this.baseFragment != null) {
                        if (videoEditedInfo != null && i >= 0 && i < ChatAttachAlert.cameraPhotos.size()) {
                            ((PhotoEntry) ChatAttachAlert.cameraPhotos.get(i)).editedInfo = videoEditedInfo;
                        }
                        if (!((ChatAttachAlert.this.baseFragment instanceof ChatActivity) && ((ChatActivity) ChatAttachAlert.this.baseFragment).isSecretChat())) {
                            i = ChatAttachAlert.cameraPhotos.size();
                            for (int i2 = 0; i2 < i; i2++) {
                                AndroidUtilities.addMediaToGallery(((PhotoEntry) ChatAttachAlert.cameraPhotos.get(i2)).path);
                            }
                        }
                        ChatAttachAlert.this.applyCaption();
                        ChatAttachAlert.this.delegate.didPressedButton(8, true);
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
                    int i = System.getInt(ChatAttachAlert.this.baseFragment.getParentActivity().getContentResolver(), "accelerometer_rotation", 0);
                    if (z || i == 1) {
                        return true;
                    }
                    return false;
                }

                public void willHidePhotoViewer() {
                    int i = 0;
                    ChatAttachAlert.this.mediaCaptured = false;
                    int childCount = ChatAttachAlert.this.gridView.getChildCount();
                    while (i < childCount) {
                        View childAt = ChatAttachAlert.this.gridView.getChildAt(i);
                        if (childAt instanceof PhotoAttachPhotoCell) {
                            PhotoAttachPhotoCell photoAttachPhotoCell = (PhotoAttachPhotoCell) childAt;
                            photoAttachPhotoCell.showImage();
                            photoAttachPhotoCell.showCheck(true);
                        }
                        i++;
                    }
                }

                public boolean canCaptureMorePhotos() {
                    return ChatAttachAlert.this.maxSelectedPhotos != 1;
                }
            }, chatActivity);
        }
    }

    private boolean processTouchEvent(MotionEvent motionEvent) {
        if (motionEvent == null) {
            return false;
        }
        if ((!this.pressed && motionEvent.getActionMasked() == 0) || motionEvent.getActionMasked() == 5) {
            this.zoomControlView.getHitRect(this.hitRect);
            if (this.hitRect.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
                return false;
            }
            if (!(this.takingPhoto || this.dragging)) {
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
            AnimatorSet animatorSet;
            Animator[] animatorArr;
            if (motionEvent.getActionMasked() == 2) {
                float hypot;
                if (this.zooming && motionEvent.getPointerCount() == 2 && !this.dragging) {
                    hypot = (float) Math.hypot((double) (motionEvent.getX(1) - motionEvent.getX(0)), (double) (motionEvent.getY(1) - motionEvent.getY(0)));
                    if (this.zoomWas) {
                        float dp = (hypot - this.pinchStartDistance) / ((float) AndroidUtilities.dp(100.0f));
                        this.pinchStartDistance = hypot;
                        this.cameraZoom += dp;
                        hypot = this.cameraZoom;
                        if (hypot < 0.0f) {
                            this.cameraZoom = 0.0f;
                        } else if (hypot > 1.0f) {
                            this.cameraZoom = 1.0f;
                        }
                        this.zoomControlView.setZoom(this.cameraZoom);
                        this.containerView.invalidate();
                        this.cameraView.setZoom(this.cameraZoom);
                    } else if (Math.abs(hypot - this.pinchStartDistance) >= AndroidUtilities.getPixelsInCM(0.4f, false)) {
                        this.pinchStartDistance = hypot;
                        this.zoomWas = true;
                    }
                } else {
                    hypot = motionEvent.getY();
                    float f = hypot - this.lastY;
                    if (this.maybeStartDraging) {
                        if (Math.abs(f) > AndroidUtilities.getPixelsInCM(0.4f, false)) {
                            this.maybeStartDraging = false;
                            this.dragging = true;
                        }
                    } else if (this.dragging) {
                        CameraView cameraView = this.cameraView;
                        if (cameraView != null) {
                            cameraView.setTranslationY(cameraView.getTranslationY() + f);
                            this.lastY = hypot;
                            if (this.cameraPanel.getTag() == null) {
                                this.cameraPanel.setTag(Integer.valueOf(1));
                                animatorSet = new AnimatorSet();
                                animatorArr = new Animator[6];
                                animatorArr[0] = ObjectAnimator.ofFloat(this.cameraPanel, View.ALPHA, new float[]{0.0f});
                                animatorArr[1] = ObjectAnimator.ofFloat(this.zoomControlView, View.ALPHA, new float[]{0.0f});
                                animatorArr[2] = ObjectAnimator.ofFloat(this.counterTextView, View.ALPHA, new float[]{0.0f});
                                animatorArr[3] = ObjectAnimator.ofFloat(this.flashModeButton[0], View.ALPHA, new float[]{0.0f});
                                animatorArr[4] = ObjectAnimator.ofFloat(this.flashModeButton[1], View.ALPHA, new float[]{0.0f});
                                animatorArr[5] = ObjectAnimator.ofFloat(this.cameraPhotoRecyclerView, View.ALPHA, new float[]{0.0f});
                                animatorSet.playTogether(animatorArr);
                                animatorSet.setDuration(200);
                                animatorSet.start();
                            }
                        }
                    }
                }
            } else if (motionEvent.getActionMasked() == 3 || motionEvent.getActionMasked() == 1 || motionEvent.getActionMasked() == 6) {
                this.pressed = false;
                this.zooming = false;
                if (this.zooming) {
                    this.zooming = false;
                } else if (this.dragging) {
                    this.dragging = false;
                    CameraView cameraView2 = this.cameraView;
                    if (cameraView2 != null) {
                        if (Math.abs(cameraView2.getTranslationY()) > ((float) this.cameraView.getMeasuredHeight()) / 6.0f) {
                            closeCamera(true);
                        } else {
                            animatorSet = new AnimatorSet();
                            animatorArr = new Animator[7];
                            animatorArr[0] = ObjectAnimator.ofFloat(this.cameraView, View.TRANSLATION_Y, new float[]{0.0f});
                            animatorArr[1] = ObjectAnimator.ofFloat(this.cameraPanel, View.ALPHA, new float[]{1.0f});
                            animatorArr[2] = ObjectAnimator.ofFloat(this.zoomControlView, View.ALPHA, new float[]{1.0f});
                            animatorArr[3] = ObjectAnimator.ofFloat(this.counterTextView, View.ALPHA, new float[]{1.0f});
                            animatorArr[4] = ObjectAnimator.ofFloat(this.flashModeButton[0], View.ALPHA, new float[]{1.0f});
                            animatorArr[5] = ObjectAnimator.ofFloat(this.flashModeButton[1], View.ALPHA, new float[]{1.0f});
                            animatorArr[6] = ObjectAnimator.ofFloat(this.cameraPhotoRecyclerView, View.ALPHA, new float[]{1.0f});
                            animatorSet.playTogether(animatorArr);
                            animatorSet.setDuration(250);
                            animatorSet.setInterpolator(this.interpolator);
                            animatorSet.start();
                            this.cameraPanel.setTag(null);
                        }
                    }
                } else {
                    CameraView cameraView3 = this.cameraView;
                    if (!(cameraView3 == null || this.zoomWas)) {
                        cameraView3.getLocationOnScreen(this.viewPosition);
                        this.cameraView.focusToPoint((int) (motionEvent.getRawX() - ((float) this.viewPosition[0])), (int) (motionEvent.getRawY() - ((float) this.viewPosition[1])));
                    }
                }
            }
        }
        return true;
    }

    /* Access modifiers changed, original: protected */
    public boolean onContainerTouchEvent(MotionEvent motionEvent) {
        return this.cameraOpened && processTouchEvent(motionEvent);
    }

    private void applyAttachButtonColors(View view) {
        String str = "dialogTextGray2";
        if (view instanceof AttachButton) {
            ((AttachButton) view).textView.setTextColor(Theme.getColor(str));
        } else if (view instanceof AttachBotButton) {
            ((AttachBotButton) view).nameTextView.setTextColor(Theme.getColor(str));
        }
    }

    public void checkColors() {
        RecyclerListView recyclerListView = this.buttonsRecyclerView;
        if (recyclerListView != null) {
            int childCount = recyclerListView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                applyAttachButtonColors(this.buttonsRecyclerView.getChildAt(i));
            }
            String str = "dialogTextBlack";
            this.selectedTextView.setTextColor(Theme.getColor(str));
            this.selectedMenuItem.setIconColor(Theme.getColor(str));
            String str2 = "dialogButtonSelector";
            Theme.setDrawableColor(this.selectedMenuItem.getBackground(), Theme.getColor(str2));
            String str3 = "actionBarDefaultSubmenuItem";
            this.selectedMenuItem.setPopupItemsColor(Theme.getColor(str3), false);
            this.selectedMenuItem.setPopupItemsColor(Theme.getColor(str3), true);
            String str4 = "actionBarDefaultSubmenuBackground";
            this.selectedMenuItem.redrawPopup(Theme.getColor(str4));
            this.commentTextView.updateColors();
            ActionBarPopupWindowLayout actionBarPopupWindowLayout = this.sendPopupLayout;
            if (actionBarPopupWindowLayout != null) {
                actionBarPopupWindowLayout.getBackgroundDrawable().setColorFilter(new PorterDuffColorFilter(Theme.getColor(str4), Mode.MULTIPLY));
                this.itemCell.setColors(Theme.getColor(str3), Theme.getColor(str3));
            }
            Theme.setSelectorDrawableColor(this.writeButtonDrawable, Theme.getColor("dialogFloatingButton"), false);
            Theme.setSelectorDrawableColor(this.writeButtonDrawable, Theme.getColor("dialogFloatingButtonPressed"), true);
            this.writeButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogFloatingIcon"), Mode.MULTIPLY));
            this.dropDown.setTextColor(Theme.getColor(str));
            this.dropDownContainer.setPopupItemsColor(Theme.getColor(str3), false);
            this.dropDownContainer.setPopupItemsColor(Theme.getColor(str3), true);
            this.dropDownContainer.redrawPopup(Theme.getColor(str4));
            this.actionBarShadow.setBackgroundColor(Theme.getColor("dialogShadowLine"));
            this.progressView.setTextColor(Theme.getColor("emptyListPlaceholder"));
            str3 = "dialogScrollGlow";
            this.buttonsRecyclerView.setGlowColor(Theme.getColor(str3));
            str4 = "dialogBackground";
            this.buttonsRecyclerView.setBackgroundColor(Theme.getColor(str4));
            this.frameLayout2.setBackgroundColor(Theme.getColor(str4));
            this.selectedCountView.invalidate();
            Theme.setDrawableColor(this.dropDownDrawable, Theme.getColor(str));
            this.actionBar.setBackgroundColor(Theme.getColor(str4));
            this.actionBar.setItemsColor(Theme.getColor(str), false);
            this.actionBar.setItemsBackgroundColor(Theme.getColor(str2), false);
            this.actionBar.setTitleColor(Theme.getColor(str));
            Theme.setDrawableColor(this.shadowDrawable, Theme.getColor(str4));
            str = "dialogCameraIcon";
            Theme.setDrawableColor(this.cameraDrawable, Theme.getColor(str));
            FrameLayout frameLayout = this.cameraIcon;
            if (frameLayout != null) {
                frameLayout.invalidate();
            }
            this.gridView.setGlowColor(Theme.getColor(str3));
            ViewHolder findViewHolderForAdapterPosition = this.gridView.findViewHolderForAdapterPosition(0);
            if (findViewHolderForAdapterPosition != null) {
                View view = findViewHolderForAdapterPosition.itemView;
                if (view instanceof PhotoAttachCameraCell) {
                    ((PhotoAttachCameraCell) view).getImageView().setColorFilter(new PorterDuffColorFilter(Theme.getColor(str), Mode.MULTIPLY));
                }
            }
            this.containerView.invalidate();
        }
    }

    private void resetRecordState() {
        if (this.baseFragment != null) {
            for (int i = 0; i < 2; i++) {
                this.flashModeButton[i].setAlpha(1.0f);
            }
            this.switchCameraButton.setAlpha(1.0f);
            this.recordTime.setAlpha(0.0f);
            AndroidUtilities.cancelRunOnUIThread(this.videoRecordRunnable);
            this.videoRecordRunnable = null;
            AndroidUtilities.unlockOrientation(this.baseFragment.getParentActivity());
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:21:0x0061  */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x0036  */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x0036  */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x0061  */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x0061  */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x0036  */
    private void setCameraFlashModeIcon(android.widget.ImageView r5, java.lang.String r6) {
        /*
        r4 = this;
        r0 = r6.hashCode();
        r1 = 3551; // 0xddf float:4.976E-42 double:1.7544E-320;
        r2 = 2;
        r3 = 1;
        if (r0 == r1) goto L_0x0029;
    L_0x000a:
        r1 = 109935; // 0x1ad6f float:1.54052E-40 double:5.4315E-319;
        if (r0 == r1) goto L_0x001f;
    L_0x000f:
        r1 = 3005871; // 0x2dddaf float:4.212122E-39 double:1.4850976E-317;
        if (r0 == r1) goto L_0x0015;
    L_0x0014:
        goto L_0x0033;
    L_0x0015:
        r0 = "auto";
        r6 = r6.equals(r0);
        if (r6 == 0) goto L_0x0033;
    L_0x001d:
        r6 = 2;
        goto L_0x0034;
    L_0x001f:
        r0 = "off";
        r6 = r6.equals(r0);
        if (r6 == 0) goto L_0x0033;
    L_0x0027:
        r6 = 0;
        goto L_0x0034;
    L_0x0029:
        r0 = "on";
        r6 = r6.equals(r0);
        if (r6 == 0) goto L_0x0033;
    L_0x0031:
        r6 = 1;
        goto L_0x0034;
    L_0x0033:
        r6 = -1;
    L_0x0034:
        if (r6 == 0) goto L_0x0061;
    L_0x0036:
        if (r6 == r3) goto L_0x004e;
    L_0x0038:
        if (r6 == r2) goto L_0x003b;
    L_0x003a:
        goto L_0x0073;
    L_0x003b:
        r6 = NUM; // 0x7var_ float:1.794484E38 double:1.052935567E-314;
        r5.setImageResource(r6);
        r6 = NUM; // 0x7f0d0011 float:1.874215E38 double:1.053129786E-314;
        r0 = "AccDescrCameraFlashAuto";
        r6 = org.telegram.messenger.LocaleController.getString(r0, r6);
        r5.setContentDescription(r6);
        goto L_0x0073;
    L_0x004e:
        r6 = NUM; // 0x7var_ float:1.7944844E38 double:1.0529355677E-314;
        r5.setImageResource(r6);
        r6 = NUM; // 0x7f0d0013 float:1.8742153E38 double:1.053129787E-314;
        r0 = "AccDescrCameraFlashOn";
        r6 = org.telegram.messenger.LocaleController.getString(r0, r6);
        r5.setContentDescription(r6);
        goto L_0x0073;
    L_0x0061:
        r6 = NUM; // 0x7var_ float:1.7944842E38 double:1.0529355673E-314;
        r5.setImageResource(r6);
        r6 = NUM; // 0x7f0d0012 float:1.8742151E38 double:1.0531297864E-314;
        r0 = "AccDescrCameraFlashOff";
        r6 = org.telegram.messenger.LocaleController.getString(r0, r6);
        r5.setContentDescription(r6);
    L_0x0073:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlert.setCameraFlashModeIcon(android.widget.ImageView, java.lang.String):void");
    }

    /* Access modifiers changed, original: protected */
    public boolean onCustomMeasure(View view, int i, int i2) {
        Object obj = i < i2 ? 1 : null;
        View view2 = this.cameraIcon;
        if (view == view2) {
            view2.measure(MeasureSpec.makeMeasureSpec(this.itemSize, NUM), MeasureSpec.makeMeasureSpec((this.itemSize - this.cameraViewOffsetBottomY) - this.cameraViewOffsetY, NUM));
            return true;
        }
        view2 = this.cameraView;
        if (view != view2) {
            view2 = this.cameraPanel;
            if (view == view2) {
                if (obj != null) {
                    view2.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(126.0f), NUM));
                } else {
                    view2.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(126.0f), NUM), MeasureSpec.makeMeasureSpec(i2, NUM));
                }
                return true;
            }
            view2 = this.zoomControlView;
            if (view == view2) {
                if (obj != null) {
                    view2.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f), NUM));
                } else {
                    view2.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f), NUM), MeasureSpec.makeMeasureSpec(i2, NUM));
                }
                return true;
            }
            view2 = this.cameraPhotoRecyclerView;
            if (view == view2) {
                this.cameraPhotoRecyclerViewIgnoreLayout = true;
                if (obj != null) {
                    view2.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(80.0f), NUM));
                    if (this.cameraPhotoLayoutManager.getOrientation() != 0) {
                        this.cameraPhotoRecyclerView.setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f), 0);
                        this.cameraPhotoLayoutManager.setOrientation(0);
                        this.cameraAttachAdapter.notifyDataSetChanged();
                    }
                } else {
                    view2.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(80.0f), NUM), MeasureSpec.makeMeasureSpec(i2, NUM));
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
            view2.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec(i2, NUM));
            return true;
        }
        return false;
    }

    /* Access modifiers changed, original: protected */
    public boolean onCustomLayout(View view, int i, int i2, int i3, int i4) {
        int i5 = i3 - i;
        i2 = i4 - i2;
        Object obj = i5 < i2 ? 1 : null;
        if (view == this.cameraPanel) {
            if (obj != null) {
                if (this.cameraPhotoRecyclerView.getVisibility() == 0) {
                    this.cameraPanel.layout(0, i4 - AndroidUtilities.dp(222.0f), i5, i4 - AndroidUtilities.dp(96.0f));
                } else {
                    this.cameraPanel.layout(0, i4 - AndroidUtilities.dp(126.0f), i5, i4);
                }
            } else if (this.cameraPhotoRecyclerView.getVisibility() == 0) {
                this.cameraPanel.layout(i3 - AndroidUtilities.dp(222.0f), 0, i3 - AndroidUtilities.dp(96.0f), i2);
            } else {
                this.cameraPanel.layout(i3 - AndroidUtilities.dp(126.0f), 0, i3, i2);
            }
            return true;
        } else if (view == this.zoomControlView) {
            if (obj != null) {
                if (this.cameraPhotoRecyclerView.getVisibility() == 0) {
                    this.zoomControlView.layout(0, i4 - AndroidUtilities.dp(272.0f), i5, i4 - AndroidUtilities.dp(222.0f));
                } else {
                    this.zoomControlView.layout(0, i4 - AndroidUtilities.dp(176.0f), i5, i4 - AndroidUtilities.dp(126.0f));
                }
            } else if (this.cameraPhotoRecyclerView.getVisibility() == 0) {
                this.zoomControlView.layout(i3 - AndroidUtilities.dp(272.0f), 0, i3 - AndroidUtilities.dp(222.0f), i2);
            } else {
                this.zoomControlView.layout(i3 - AndroidUtilities.dp(176.0f), 0, i3 - AndroidUtilities.dp(126.0f), i2);
            }
            return true;
        } else {
            View view2 = this.counterTextView;
            if (view == view2) {
                if (obj != null) {
                    i5 = (i5 - view2.getMeasuredWidth()) / 2;
                    i4 -= AndroidUtilities.dp(167.0f);
                    this.counterTextView.setRotation(0.0f);
                    if (this.cameraPhotoRecyclerView.getVisibility() == 0) {
                        i4 -= AndroidUtilities.dp(96.0f);
                    }
                } else {
                    i5 = i3 - AndroidUtilities.dp(167.0f);
                    i4 = (i2 / 2) + (this.counterTextView.getMeasuredWidth() / 2);
                    this.counterTextView.setRotation(-90.0f);
                    if (this.cameraPhotoRecyclerView.getVisibility() == 0) {
                        i5 -= AndroidUtilities.dp(96.0f);
                    }
                }
                TextView textView = this.counterTextView;
                textView.layout(i5, i4, textView.getMeasuredWidth() + i5, this.counterTextView.getMeasuredHeight() + i4);
                return true;
            } else if (view != this.cameraPhotoRecyclerView) {
                return false;
            } else {
                if (obj != null) {
                    i2 -= AndroidUtilities.dp(88.0f);
                    view.layout(0, i2, view.getMeasuredWidth(), view.getMeasuredHeight() + i2);
                } else {
                    i = (i + i5) - AndroidUtilities.dp(88.0f);
                    view.layout(i, 0, view.getMeasuredWidth() + i, view.getMeasuredHeight());
                }
                return true;
            }
        }
    }

    public void onPause() {
        ShutterButton shutterButton = this.shutterButton;
        if (shutterButton != null) {
            if (this.requestingPermissions) {
                if (this.cameraView != null && shutterButton.getState() == State.RECORDING) {
                    this.shutterButton.setState(State.DEFAULT, true);
                }
                this.requestingPermissions = false;
            } else {
                if (this.cameraView != null && shutterButton.getState() == State.RECORDING) {
                    resetRecordState();
                    CameraController.getInstance().stopVideoRecording(this.cameraView.getCameraSession(), false);
                    this.shutterButton.setState(State.DEFAULT, true);
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

    private void openCamera(boolean z) {
        CameraView cameraView = this.cameraView;
        if (cameraView != null && this.cameraInitAnimation == null && cameraView.isInitied()) {
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
            this.cameraPanel.setVisibility(0);
            this.cameraPanel.setTag(null);
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
                arrayList.add(ObjectAnimator.ofFloat(this.zoomControlView, View.ALPHA, new float[]{1.0f}));
                arrayList.add(ObjectAnimator.ofFloat(this.counterTextView, View.ALPHA, new float[]{1.0f}));
                arrayList.add(ObjectAnimator.ofFloat(this.cameraPhotoRecyclerView, View.ALPHA, new float[]{1.0f}));
                for (i2 = 0; i2 < 2; i2++) {
                    if (this.flashModeButton[i2].getVisibility() == 0) {
                        arrayList.add(ObjectAnimator.ofFloat(this.flashModeButton[i2], View.ALPHA, new float[]{1.0f}));
                        break;
                    }
                }
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(arrayList);
                animatorSet.setDuration(200);
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        ChatAttachAlert.this.cameraAnimationInProgress = false;
                        if (VERSION.SDK_INT >= 21) {
                            ChatAttachAlert.this.cameraView.invalidateOutline();
                        }
                        if (ChatAttachAlert.this.cameraOpened) {
                            ChatAttachAlert.this.delegate.onCameraOpened();
                        }
                    }
                });
                animatorSet.start();
            } else {
                setCameraOpenProgress(1.0f);
                this.cameraPanel.setAlpha(1.0f);
                this.zoomControlView.setAlpha(1.0f);
                this.counterTextView.setAlpha(1.0f);
                this.cameraPhotoRecyclerView.setAlpha(1.0f);
                while (i < 2) {
                    if (this.flashModeButton[i].getVisibility() == 0) {
                        this.flashModeButton[i].setAlpha(1.0f);
                        break;
                    }
                    i++;
                }
                this.delegate.onCameraOpened();
            }
            if (VERSION.SDK_INT >= 21) {
                this.cameraView.setSystemUiVisibility(1028);
            }
            this.cameraOpened = true;
            this.cameraView.setImportantForAccessibility(2);
            if (VERSION.SDK_INT >= 19) {
                this.gridView.setImportantForAccessibility(4);
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:82:0x0150 A:{SYNTHETIC, Splitter:B:82:0x0150} */
    /* JADX WARNING: Removed duplicated region for block: B:91:0x01b3 A:{SYNTHETIC, Splitter:B:91:0x01b3} */
    /* JADX WARNING: Missing block: B:46:0x00ee, code skipped:
            if (new java.io.File(r0).exists() != false) goto L_0x00f1;
     */
    public void onActivityResultFragment(int r21, android.content.Intent r22, java.lang.String r23) {
        /*
        r20 = this;
        r1 = r20;
        r0 = r21;
        r7 = r23;
        r2 = r1.baseFragment;
        if (r2 == 0) goto L_0x01bd;
    L_0x000a:
        r2 = r2.getParentActivity();
        if (r2 != 0) goto L_0x0012;
    L_0x0010:
        goto L_0x01bd;
    L_0x0012:
        r10 = 1;
        mediaFromExternalCamera = r10;
        r11 = 0;
        if (r0 != 0) goto L_0x0074;
    L_0x0018:
        r0 = org.telegram.ui.PhotoViewer.getInstance();
        r2 = r1.baseFragment;
        r2 = r2.getParentActivity();
        r0.setParentActivity(r2);
        r0 = org.telegram.ui.PhotoViewer.getInstance();
        r2 = r1.maxSelectedPhotos;
        r3 = r1.allowOrder;
        r0.setMaxSelectedPhotos(r2, r3);
        r0 = new java.util.ArrayList;
        r0.<init>();
        r0 = new androidx.exifinterface.media.ExifInterface;	 Catch:{ Exception -> 0x0056 }
        r0.<init>(r7);	 Catch:{ Exception -> 0x0056 }
        r2 = "Orientation";
        r0 = r0.getAttributeInt(r2, r10);	 Catch:{ Exception -> 0x0056 }
        r2 = 3;
        if (r0 == r2) goto L_0x0052;
    L_0x0043:
        r2 = 6;
        if (r0 == r2) goto L_0x004f;
    L_0x0046:
        r2 = 8;
        if (r0 == r2) goto L_0x004c;
    L_0x004a:
        r0 = 0;
        goto L_0x0054;
    L_0x004c:
        r0 = 270; // 0x10e float:3.78E-43 double:1.334E-321;
        goto L_0x0054;
    L_0x004f:
        r0 = 90;
        goto L_0x0054;
    L_0x0052:
        r0 = 180; // 0xb4 float:2.52E-43 double:8.9E-322;
    L_0x0054:
        r8 = r0;
        goto L_0x005b;
    L_0x0056:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
        r8 = 0;
    L_0x005b:
        r0 = new org.telegram.messenger.MediaController$PhotoEntry;
        r3 = 0;
        r4 = lastImageId;
        r2 = r4 + -1;
        lastImageId = r2;
        r5 = 0;
        r9 = 0;
        r2 = r0;
        r7 = r23;
        r2.<init>(r3, r4, r5, r7, r8, r9);
        r0.canDeleteAfter = r10;
        r1.openPhotoViewer(r0, r11, r10);
        goto L_0x01bd;
    L_0x0074:
        r2 = 2;
        if (r0 != r2) goto L_0x01bd;
    L_0x0077:
        r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r0 == 0) goto L_0x008f;
    L_0x007b:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r2 = "pic path ";
        r0.append(r2);
        r0.append(r7);
        r0 = r0.toString();
        org.telegram.messenger.FileLog.d(r0);
    L_0x008f:
        r2 = 0;
        if (r22 == 0) goto L_0x00a1;
    L_0x0092:
        if (r7 == 0) goto L_0x00a1;
    L_0x0094:
        r0 = new java.io.File;
        r0.<init>(r7);
        r0 = r0.exists();
        if (r0 == 0) goto L_0x00a1;
    L_0x009f:
        r0 = r2;
        goto L_0x00a3;
    L_0x00a1:
        r0 = r22;
    L_0x00a3:
        if (r0 == 0) goto L_0x0104;
    L_0x00a5:
        r0 = r0.getData();
        if (r0 == 0) goto L_0x00f0;
    L_0x00ab:
        r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r3 == 0) goto L_0x00c7;
    L_0x00af:
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = "video record uri ";
        r3.append(r4);
        r4 = r0.toString();
        r3.append(r4);
        r3 = r3.toString();
        org.telegram.messenger.FileLog.d(r3);
    L_0x00c7:
        r0 = org.telegram.messenger.AndroidUtilities.getPath(r0);
        r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r3 == 0) goto L_0x00e3;
    L_0x00cf:
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = "resolved path = ";
        r3.append(r4);
        r3.append(r0);
        r3 = r3.toString();
        org.telegram.messenger.FileLog.d(r3);
    L_0x00e3:
        if (r0 == 0) goto L_0x00f0;
    L_0x00e5:
        r3 = new java.io.File;
        r3.<init>(r0);
        r3 = r3.exists();
        if (r3 != 0) goto L_0x00f1;
    L_0x00f0:
        r0 = r7;
    L_0x00f1:
        r3 = r1.baseFragment;
        r4 = r3 instanceof org.telegram.ui.ChatActivity;
        if (r4 == 0) goto L_0x00ff;
    L_0x00f7:
        r3 = (org.telegram.ui.ChatActivity) r3;
        r3 = r3.isSecretChat();
        if (r3 != 0) goto L_0x0102;
    L_0x00ff:
        org.telegram.messenger.AndroidUtilities.addMediaToGallery(r23);
    L_0x0102:
        r7 = r2;
        goto L_0x0105;
    L_0x0104:
        r0 = r2;
    L_0x0105:
        if (r0 != 0) goto L_0x0115;
    L_0x0107:
        if (r7 == 0) goto L_0x0115;
    L_0x0109:
        r3 = new java.io.File;
        r3.<init>(r7);
        r3 = r3.exists();
        if (r3 == 0) goto L_0x0115;
    L_0x0114:
        goto L_0x0116;
    L_0x0115:
        r7 = r0;
    L_0x0116:
        r3 = 0;
        r5 = new android.media.MediaMetadataRetriever;	 Catch:{ Exception -> 0x014a }
        r5.<init>();	 Catch:{ Exception -> 0x014a }
        r5.setDataSource(r7);	 Catch:{ Exception -> 0x0143, all -> 0x0141 }
        r0 = 9;
        r0 = r5.extractMetadata(r0);	 Catch:{ Exception -> 0x0143, all -> 0x0141 }
        if (r0 == 0) goto L_0x0137;
    L_0x0128:
        r8 = java.lang.Long.parseLong(r0);	 Catch:{ Exception -> 0x0143, all -> 0x0141 }
        r0 = (float) r8;	 Catch:{ Exception -> 0x0143, all -> 0x0141 }
        r2 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        r0 = r0 / r2;
        r8 = (double) r0;	 Catch:{ Exception -> 0x0143, all -> 0x0141 }
        r2 = java.lang.Math.ceil(r8);	 Catch:{ Exception -> 0x0143, all -> 0x0141 }
        r0 = (int) r2;
        r3 = (long) r0;
    L_0x0137:
        r5.release();	 Catch:{ Exception -> 0x013b }
        goto L_0x0153;
    L_0x013b:
        r0 = move-exception;
        r2 = r0;
        org.telegram.messenger.FileLog.e(r2);
        goto L_0x0153;
    L_0x0141:
        r0 = move-exception;
        goto L_0x0148;
    L_0x0143:
        r0 = move-exception;
        r2 = r5;
        goto L_0x014b;
    L_0x0146:
        r0 = move-exception;
        r5 = r2;
    L_0x0148:
        r2 = r0;
        goto L_0x01b1;
    L_0x014a:
        r0 = move-exception;
    L_0x014b:
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ all -> 0x0146 }
        if (r2 == 0) goto L_0x0153;
    L_0x0150:
        r2.release();	 Catch:{ Exception -> 0x013b }
    L_0x0153:
        r0 = android.media.ThumbnailUtils.createVideoThumbnail(r7, r10);
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r5 = "-2147483648_";
        r2.append(r5);
        r5 = org.telegram.messenger.SharedConfig.getLastLocalId();
        r2.append(r5);
        r5 = ".jpg";
        r2.append(r5);
        r2 = r2.toString();
        r5 = new java.io.File;
        r6 = 4;
        r6 = org.telegram.messenger.FileLoader.getDirectory(r6);
        r5.<init>(r6, r2);
        r2 = new java.io.FileOutputStream;	 Catch:{ Throwable -> 0x0188 }
        r2.<init>(r5);	 Catch:{ Throwable -> 0x0188 }
        r6 = android.graphics.Bitmap.CompressFormat.JPEG;	 Catch:{ Throwable -> 0x0188 }
        r8 = 55;
        r0.compress(r6, r8, r2);	 Catch:{ Throwable -> 0x0188 }
        goto L_0x018c;
    L_0x0188:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x018c:
        org.telegram.messenger.SharedConfig.saveConfig();
        r0 = new org.telegram.messenger.MediaController$PhotoEntry;
        r13 = 0;
        r14 = lastImageId;
        r2 = r14 + -1;
        lastImageId = r2;
        r15 = 0;
        r18 = 0;
        r19 = 1;
        r12 = r0;
        r17 = r7;
        r12.<init>(r13, r14, r15, r17, r18, r19);
        r2 = (int) r3;
        r0.duration = r2;
        r2 = r5.getAbsolutePath();
        r0.thumbPath = r2;
        r1.openPhotoViewer(r0, r11, r10);
        goto L_0x01bd;
    L_0x01b1:
        if (r5 == 0) goto L_0x01bc;
    L_0x01b3:
        r5.release();	 Catch:{ Exception -> 0x01b7 }
        goto L_0x01bc;
    L_0x01b7:
        r0 = move-exception;
        r3 = r0;
        org.telegram.messenger.FileLog.e(r3);
    L_0x01bc:
        throw r2;
    L_0x01bd:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlert.onActivityResultFragment(int, android.content.Intent, java.lang.String):void");
    }

    public void closeCamera(boolean z) {
        if (!this.takingPhoto) {
            CameraView cameraView = this.cameraView;
            if (cameraView != null) {
                int[] iArr = this.animateCameraValues;
                int i = this.itemSize;
                iArr[1] = i - this.cameraViewOffsetX;
                iArr[2] = (i - this.cameraViewOffsetY) - this.cameraViewOffsetBottomY;
                if (z) {
                    LayoutParams layoutParams = (LayoutParams) cameraView.getLayoutParams();
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
                    for (int i2 = 0; i2 < 2; i2++) {
                        if (this.flashModeButton[i2].getVisibility() == 0) {
                            arrayList.add(ObjectAnimator.ofFloat(this.flashModeButton[i2], View.ALPHA, new float[]{0.0f}));
                            break;
                        }
                    }
                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.playTogether(arrayList);
                    animatorSet.setDuration(200);
                    animatorSet.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            ChatAttachAlert.this.cameraAnimationInProgress = false;
                            if (VERSION.SDK_INT >= 21 && ChatAttachAlert.this.cameraView != null) {
                                ChatAttachAlert.this.cameraView.invalidateOutline();
                            }
                            ChatAttachAlert.this.cameraOpened = false;
                            if (ChatAttachAlert.this.cameraPanel != null) {
                                ChatAttachAlert.this.cameraPanel.setVisibility(8);
                            }
                            if (ChatAttachAlert.this.zoomControlView != null) {
                                ChatAttachAlert.this.zoomControlView.setVisibility(8);
                            }
                            if (ChatAttachAlert.this.cameraPhotoRecyclerView != null) {
                                ChatAttachAlert.this.cameraPhotoRecyclerView.setVisibility(8);
                            }
                            if (VERSION.SDK_INT >= 21 && ChatAttachAlert.this.cameraView != null) {
                                ChatAttachAlert.this.cameraView.setSystemUiVisibility(1024);
                            }
                        }
                    });
                    animatorSet.start();
                } else {
                    iArr[0] = 0;
                    setCameraOpenProgress(0.0f);
                    this.cameraPanel.setAlpha(0.0f);
                    this.cameraPanel.setVisibility(8);
                    this.zoomControlView.setAlpha(0.0f);
                    this.zoomControlView.setVisibility(8);
                    this.cameraPhotoRecyclerView.setAlpha(0.0f);
                    this.counterTextView.setAlpha(0.0f);
                    this.cameraPhotoRecyclerView.setVisibility(8);
                    for (int i3 = 0; i3 < 2; i3++) {
                        if (this.flashModeButton[i3].getVisibility() == 0) {
                            this.flashModeButton[i3].setAlpha(0.0f);
                            break;
                        }
                    }
                    this.cameraOpened = false;
                    if (VERSION.SDK_INT >= 21) {
                        this.cameraView.setSystemUiVisibility(1024);
                    }
                }
                this.cameraView.setImportantForAccessibility(0);
                if (VERSION.SDK_INT >= 19) {
                    this.gridView.setImportantForAccessibility(0);
                }
            }
        }
    }

    @Keep
    public void setCameraOpenProgress(float f) {
        if (this.cameraView != null) {
            float width;
            int height;
            this.cameraOpenProgress = f;
            int[] iArr = this.animateCameraValues;
            float f2 = (float) iArr[1];
            float f3 = (float) iArr[2];
            Point point = AndroidUtilities.displaySize;
            if ((point.x < point.y ? 1 : null) != null) {
                width = (float) ((this.container.getWidth() - getLeftInset()) - getRightInset());
                height = this.container.getHeight();
            } else {
                width = (float) ((this.container.getWidth() - getLeftInset()) - getRightInset());
                height = this.container.getHeight();
            }
            float f4 = (float) height;
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
            LayoutParams layoutParams = (LayoutParams) this.cameraView.getLayoutParams();
            layoutParams.width = (int) (f2 + ((width - f2) * f));
            layoutParams.height = (int) (f3 + ((f4 - f3) * f));
            if (f != 0.0f) {
                f4 = 1.0f - f;
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
            if (VERSION.SDK_INT >= 21) {
                this.cameraView.invalidateOutline();
            }
        }
    }

    @Keep
    public float getCameraOpenProgress() {
        return this.cameraOpenProgress;
    }

    private void checkCameraViewPosition() {
        if (VERSION.SDK_INT >= 21) {
            CameraView cameraView = this.cameraView;
            if (cameraView != null) {
                cameraView.invalidateOutline();
            }
            ViewHolder findViewHolderForAdapterPosition = this.gridView.findViewHolderForAdapterPosition(this.itemsPerRow - 1);
            if (findViewHolderForAdapterPosition != null) {
                findViewHolderForAdapterPosition.itemView.invalidateOutline();
            }
            if (!(this.adapter.needCamera && this.deviceHasGoodCamera && this.selectedAlbumEntry == this.galleryAlbumEntry)) {
                findViewHolderForAdapterPosition = this.gridView.findViewHolderForAdapterPosition(0);
                if (findViewHolderForAdapterPosition != null) {
                    findViewHolderForAdapterPosition.itemView.invalidateOutline();
                }
            }
        }
        if (this.deviceHasGoodCamera) {
            int childCount = this.gridView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = this.gridView.getChildAt(i);
                if (childAt instanceof PhotoAttachCameraCell) {
                    if (VERSION.SDK_INT < 19 || childAt.isAttachedToWindow()) {
                        childAt.getLocationInWindow(this.cameraViewLocation);
                        int[] iArr = this.cameraViewLocation;
                        iArr[0] = iArr[0] - getLeftInset();
                        float x = this.gridView.getX() - ((float) getLeftInset());
                        int[] iArr2 = this.cameraViewLocation;
                        if (((float) iArr2[0]) < x) {
                            this.cameraViewOffsetX = (int) (x - ((float) iArr2[0]));
                            childCount = this.cameraViewOffsetX;
                            if (childCount >= this.itemSize) {
                                this.cameraViewOffsetX = 0;
                                iArr2[0] = AndroidUtilities.dp(-400.0f);
                                this.cameraViewLocation[1] = 0;
                            } else {
                                iArr2[0] = iArr2[0] + childCount;
                            }
                        } else {
                            this.cameraViewOffsetX = 0;
                        }
                        childCount = (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight();
                        int[] iArr3 = this.cameraViewLocation;
                        if (iArr3[1] < childCount) {
                            this.cameraViewOffsetY = childCount - iArr3[1];
                            childCount = this.cameraViewOffsetY;
                            if (childCount >= this.itemSize) {
                                this.cameraViewOffsetY = 0;
                                iArr3[0] = AndroidUtilities.dp(-400.0f);
                                this.cameraViewLocation[1] = 0;
                            } else {
                                iArr3[1] = iArr3[1] + childCount;
                            }
                        } else {
                            this.cameraViewOffsetY = 0;
                        }
                        childCount = this.containerView.getMeasuredHeight();
                        int keyboardHeight = this.sizeNotifierFrameLayout.getKeyboardHeight();
                        if (!AndroidUtilities.isInMultiwindow && keyboardHeight <= AndroidUtilities.dp(20.0f)) {
                            childCount -= this.commentTextView.getEmojiPadding();
                        }
                        childCount = (int) ((((float) (childCount - this.buttonsRecyclerView.getMeasuredHeight())) + this.buttonsRecyclerView.getTranslationY()) + this.containerView.getTranslationY());
                        iArr3 = this.cameraViewLocation;
                        i = iArr3[1];
                        int i2 = this.itemSize;
                        if (i + i2 > childCount) {
                            this.cameraViewOffsetBottomY = (iArr3[1] + i2) - childCount;
                            if (this.cameraViewOffsetBottomY >= i2) {
                                this.cameraViewOffsetBottomY = 0;
                                iArr3[0] = AndroidUtilities.dp(-400.0f);
                                this.cameraViewLocation[1] = 0;
                            }
                        } else {
                            this.cameraViewOffsetBottomY = 0;
                        }
                        applyCameraViewPosition();
                        return;
                    }
                    this.cameraViewOffsetX = 0;
                    this.cameraViewOffsetY = 0;
                    this.cameraViewLocation[0] = AndroidUtilities.dp(-400.0f);
                    this.cameraViewLocation[1] = 0;
                    applyCameraViewPosition();
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
        CameraView cameraView = this.cameraView;
        if (cameraView != null) {
            LayoutParams layoutParams;
            if (!this.cameraOpened) {
                cameraView.setTranslationX((float) this.cameraViewLocation[0]);
                this.cameraView.setTranslationY((float) this.cameraViewLocation[1]);
            }
            this.cameraIcon.setTranslationX((float) this.cameraViewLocation[0]);
            this.cameraIcon.setTranslationY((float) this.cameraViewLocation[1]);
            int i = this.itemSize;
            int i2 = i - this.cameraViewOffsetX;
            int i3 = this.cameraViewOffsetY;
            i = (i - i3) - this.cameraViewOffsetBottomY;
            if (!this.cameraOpened) {
                this.cameraView.setClipTop(i3);
                this.cameraView.setClipBottom(this.cameraViewOffsetBottomY);
                layoutParams = (LayoutParams) this.cameraView.getLayoutParams();
                if (!(layoutParams.height == i && layoutParams.width == i2)) {
                    layoutParams.width = i2;
                    layoutParams.height = i;
                    this.cameraView.setLayoutParams(layoutParams);
                    AndroidUtilities.runOnUIThread(new -$$Lambda$ChatAttachAlert$Z85qju10h-t3PjzkSIZxS_W2apI(this, layoutParams));
                }
            }
            layoutParams = (LayoutParams) this.cameraIcon.getLayoutParams();
            if (layoutParams.height != i || layoutParams.width != i2) {
                layoutParams.width = i2;
                layoutParams.height = i;
                this.cameraIcon.setLayoutParams(layoutParams);
                AndroidUtilities.runOnUIThread(new -$$Lambda$ChatAttachAlert$VO-pRrRQwHQDTrFw4oW7HPAK6X8(this, layoutParams));
            }
        }
    }

    public /* synthetic */ void lambda$applyCameraViewPosition$17$ChatAttachAlert(LayoutParams layoutParams) {
        CameraView cameraView = this.cameraView;
        if (cameraView != null) {
            cameraView.setLayoutParams(layoutParams);
        }
    }

    public /* synthetic */ void lambda$applyCameraViewPosition$18$ChatAttachAlert(LayoutParams layoutParams) {
        FrameLayout frameLayout = this.cameraIcon;
        if (frameLayout != null) {
            frameLayout.setLayoutParams(layoutParams);
        }
    }

    public void showCamera() {
        if (!this.paused && this.mediaEnabled) {
            if (this.cameraView == null) {
                this.cameraView = new CameraView(this.baseFragment.getParentActivity(), this.openWithFrontFaceCamera);
                this.cameraView.setFocusable(true);
                if (VERSION.SDK_INT >= 21) {
                    this.cameraView.setOutlineProvider(new ViewOutlineProvider() {
                        public void getOutline(View view, Outline outline) {
                            int dp;
                            if (ChatAttachAlert.this.cameraAnimationInProgress) {
                                dp = AndroidUtilities.dp((ChatAttachAlert.this.cornerRadius * 8.0f) * ChatAttachAlert.this.cameraOpenProgress);
                                outline.setRoundRect(0, 0, view.getMeasuredWidth() + dp, view.getMeasuredHeight() + dp, (float) dp);
                            } else if (ChatAttachAlert.this.cameraAnimationInProgress || ChatAttachAlert.this.cameraOpened) {
                                outline.setRect(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
                            } else {
                                dp = AndroidUtilities.dp(ChatAttachAlert.this.cornerRadius * 8.0f);
                                outline.setRoundRect(0, 0, view.getMeasuredWidth() + dp, view.getMeasuredHeight() + dp, (float) dp);
                            }
                        }
                    });
                    this.cameraView.setClipToOutline(true);
                }
                this.cameraView.setContentDescription(LocaleController.getString("AccDescrInstantCamera", NUM));
                ContainerView containerView = this.container;
                CameraView cameraView = this.cameraView;
                int i = this.itemSize;
                containerView.addView(cameraView, 1, new LayoutParams(i, i));
                this.cameraView.setDelegate(new CameraViewDelegate() {
                    public void onCameraCreated(Camera camera) {
                    }

                    public void onCameraInit() {
                        int i = 4;
                        int i2;
                        if (ChatAttachAlert.this.cameraView.getCameraSession().getCurrentFlashMode().equals(ChatAttachAlert.this.cameraView.getCameraSession().getNextFlashMode())) {
                            for (i2 = 0; i2 < 2; i2++) {
                                ChatAttachAlert.this.flashModeButton[i2].setVisibility(4);
                                ChatAttachAlert.this.flashModeButton[i2].setAlpha(0.0f);
                                ChatAttachAlert.this.flashModeButton[i2].setTranslationY(0.0f);
                            }
                        } else {
                            ChatAttachAlert chatAttachAlert = ChatAttachAlert.this;
                            chatAttachAlert.setCameraFlashModeIcon(chatAttachAlert.flashModeButton[0], ChatAttachAlert.this.cameraView.getCameraSession().getCurrentFlashMode());
                            i2 = 0;
                            while (i2 < 2) {
                                ChatAttachAlert.this.flashModeButton[i2].setVisibility(i2 == 0 ? 0 : 4);
                                ImageView imageView = ChatAttachAlert.this.flashModeButton[i2];
                                float f = (i2 == 0 && ChatAttachAlert.this.cameraOpened) ? 1.0f : 0.0f;
                                imageView.setAlpha(f);
                                ChatAttachAlert.this.flashModeButton[i2].setTranslationY(0.0f);
                                i2++;
                            }
                        }
                        ChatAttachAlert.this.switchCameraButton.setImageResource(ChatAttachAlert.this.cameraView.isFrontface() ? NUM : NUM);
                        ImageView access$7400 = ChatAttachAlert.this.switchCameraButton;
                        if (ChatAttachAlert.this.cameraView.hasFrontFaceCamera()) {
                            i = 0;
                        }
                        access$7400.setVisibility(i);
                        if (!ChatAttachAlert.this.cameraOpened) {
                            ChatAttachAlert.this.cameraInitAnimation = new AnimatorSet();
                            ChatAttachAlert.this.cameraInitAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(ChatAttachAlert.this.cameraView, View.ALPHA, new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(ChatAttachAlert.this.cameraIcon, View.ALPHA, new float[]{0.0f, 1.0f})});
                            ChatAttachAlert.this.cameraInitAnimation.setDuration(180);
                            ChatAttachAlert.this.cameraInitAnimation.addListener(new AnimatorListenerAdapter() {
                                public void onAnimationEnd(Animator animator) {
                                    if (animator.equals(ChatAttachAlert.this.cameraInitAnimation)) {
                                        ChatAttachAlert.this.cameraInitAnimation = null;
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
                                    ChatAttachAlert.this.cameraInitAnimation = null;
                                }
                            });
                            ChatAttachAlert.this.cameraInitAnimation.start();
                        }
                    }
                });
                if (this.cameraIcon == null) {
                    this.cameraIcon = new FrameLayout(this.baseFragment.getParentActivity()) {
                        /* Access modifiers changed, original: protected */
                        public void onDraw(Canvas canvas) {
                            int intrinsicWidth = ChatAttachAlert.this.cameraDrawable.getIntrinsicWidth();
                            int intrinsicHeight = ChatAttachAlert.this.cameraDrawable.getIntrinsicHeight();
                            int access$2700 = (ChatAttachAlert.this.itemSize - intrinsicWidth) / 2;
                            int access$27002 = (ChatAttachAlert.this.itemSize - intrinsicHeight) / 2;
                            if (ChatAttachAlert.this.cameraViewOffsetY != 0) {
                                access$27002 -= ChatAttachAlert.this.cameraViewOffsetY;
                            }
                            ChatAttachAlert.this.cameraDrawable.setBounds(access$2700, access$27002, intrinsicWidth + access$2700, intrinsicHeight + access$27002);
                            ChatAttachAlert.this.cameraDrawable.draw(canvas);
                        }
                    };
                    this.cameraIcon.setWillNotDraw(false);
                    this.cameraIcon.setClipChildren(true);
                }
                containerView = this.container;
                FrameLayout frameLayout = this.cameraIcon;
                int i2 = this.itemSize;
                containerView.addView(frameLayout, 2, new LayoutParams(i2, i2));
                float f = 1.0f;
                this.cameraView.setAlpha(this.mediaEnabled ? 1.0f : 0.2f);
                this.cameraView.setEnabled(this.mediaEnabled);
                FrameLayout frameLayout2 = this.cameraIcon;
                if (!this.mediaEnabled) {
                    f = 0.2f;
                }
                frameLayout2.setAlpha(f);
                this.cameraIcon.setEnabled(this.mediaEnabled);
                checkCameraViewPosition();
            }
            ZoomControlView zoomControlView = this.zoomControlView;
            if (zoomControlView != null) {
                zoomControlView.setZoom(0.0f);
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
            this.cameraView.destroy(z, null);
            AnimatorSet animatorSet = this.cameraInitAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.cameraInitAnimation = null;
            }
            this.container.removeView(this.cameraView);
            this.container.removeView(this.cameraIcon);
            this.cameraView = null;
            this.cameraIcon = null;
            int childCount = this.gridView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = this.gridView.getChildAt(i);
                if (childAt instanceof PhotoAttachCameraCell) {
                    childAt.setVisibility(0);
                    return;
                }
            }
        }
    }

    private void saveLastCameraBitmap() {
        try {
            Bitmap bitmap = this.cameraView.getTextureView().getBitmap();
            if (bitmap != null) {
                Bitmap createBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), this.cameraView.getMatrix(), true);
                bitmap.recycle();
                bitmap = Bitmap.createScaledBitmap(createBitmap, 80, (int) (((float) createBitmap.getHeight()) / (((float) createBitmap.getWidth()) / 80.0f)), true);
                if (bitmap != null) {
                    if (bitmap != createBitmap) {
                        createBitmap.recycle();
                    }
                    Utilities.blurBitmap(bitmap, 7, 1, bitmap.getWidth(), bitmap.getHeight(), bitmap.getRowBytes());
                    bitmap.compress(CompressFormat.JPEG, 87, new FileOutputStream(new File(ApplicationLoader.getFilesDirFixed(), "cthumb.jpg")));
                    bitmap.recycle();
                }
            }
        } catch (Throwable unused) {
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        int i3 = 0;
        if (i == NotificationCenter.albumsDidLoad) {
            if (this.adapter != null) {
                if (this.baseFragment instanceof ChatActivity) {
                    this.galleryAlbumEntry = MediaController.allMediaAlbumEntry;
                } else {
                    this.galleryAlbumEntry = MediaController.allPhotosAlbumEntry;
                }
                if (this.selectedAlbumEntry != null) {
                    for (i = 0; i < MediaController.allMediaAlbums.size(); i++) {
                        AlbumEntry albumEntry = (AlbumEntry) MediaController.allMediaAlbums.get(i);
                        int i4 = albumEntry.bucketId;
                        AlbumEntry albumEntry2 = this.selectedAlbumEntry;
                        if (i4 == albumEntry2.bucketId && albumEntry.videoOnly == albumEntry2.videoOnly) {
                            this.selectedAlbumEntry = albumEntry;
                            break;
                        }
                    }
                } else {
                    this.selectedAlbumEntry = this.galleryAlbumEntry;
                }
                this.loading = false;
                this.progressView.showTextView();
                this.adapter.notifyDataSetChanged();
                this.cameraAttachAdapter.notifyDataSetChanged();
                if (!(selectedPhotosOrder.isEmpty() || this.galleryAlbumEntry == null)) {
                    i = selectedPhotosOrder.size();
                    while (i3 < i) {
                        i2 = ((Integer) selectedPhotosOrder.get(i3)).intValue();
                        PhotoEntry photoEntry = (PhotoEntry) this.galleryAlbumEntry.photosByIds.get(i2);
                        if (photoEntry != null) {
                            selectedPhotos.put(Integer.valueOf(i2), photoEntry);
                        }
                        i3++;
                    }
                }
                updateAlbumsDropDown();
            }
        } else if (i == NotificationCenter.reloadInlineHints) {
            ButtonsAdapter buttonsAdapter = this.buttonsAdapter;
            if (buttonsAdapter != null) {
                buttonsAdapter.notifyDataSetChanged();
            }
        } else if (i == NotificationCenter.cameraInitied) {
            checkCamera(false);
        }
    }

    private void updateAlbumsDropDown() {
        this.dropDownContainer.removeAllSubItems();
        if (this.mediaEnabled) {
            Collection collection;
            if (this.baseFragment instanceof ChatActivity) {
                collection = MediaController.allMediaAlbums;
            } else {
                collection = MediaController.allPhotoAlbums;
            }
            this.dropDownAlbums = new ArrayList(collection);
            Collections.sort(this.dropDownAlbums, new -$$Lambda$ChatAttachAlert$rVZEYwbzmmg-AwHB2cx3WFhCcZY(collection));
        } else {
            this.dropDownAlbums = new ArrayList();
        }
        if (this.dropDownAlbums.isEmpty()) {
            this.dropDown.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            return;
        }
        this.dropDown.setCompoundDrawablesWithIntrinsicBounds(null, null, this.dropDownDrawable, null);
        int size = this.dropDownAlbums.size();
        for (int i = 0; i < size; i++) {
            this.dropDownContainer.addSubItem(i + 10, ((AlbumEntry) this.dropDownAlbums.get(i)).bucketName);
        }
    }

    static /* synthetic */ int lambda$updateAlbumsDropDown$19(ArrayList arrayList, AlbumEntry albumEntry, AlbumEntry albumEntry2) {
        if (albumEntry.bucketId == 0 && albumEntry2.bucketId != 0) {
            return -1;
        }
        if (albumEntry.bucketId != 0 && albumEntry2.bucketId == 0) {
            return 1;
        }
        int indexOf = arrayList.indexOf(albumEntry);
        int indexOf2 = arrayList.indexOf(albumEntry2);
        if (indexOf > indexOf2) {
            return 1;
        }
        if (indexOf < indexOf2) {
            return -1;
        }
        return 0;
    }

    private void updateSelectedPosition() {
        float min;
        int i;
        int dp = (this.scrollOffsetY - this.backgroundPaddingTop) - AndroidUtilities.dp(39.0f);
        float f = 0.0f;
        if (this.backgroundPaddingTop + dp < ActionBar.getCurrentActionBarHeight()) {
            min = Math.min(1.0f, ((float) ((ActionBar.getCurrentActionBarHeight() - dp) - this.backgroundPaddingTop)) / ((float) AndroidUtilities.dp(43.0f)));
            this.cornerRadius = 1.0f - min;
        } else {
            this.cornerRadius = 1.0f;
            min = 0.0f;
        }
        if (AndroidUtilities.isTablet()) {
            i = 16;
        } else {
            Point point = AndroidUtilities.displaySize;
            i = point.x > point.y ? 6 : 12;
        }
        if (this.actionBar.getAlpha() == 0.0f) {
            f = (float) AndroidUtilities.dp((1.0f - this.selectedMenuItem.getAlpha()) * 26.0f);
        }
        float f2 = ((float) i) * min;
        this.selectedMenuItem.setTranslationY(((float) (this.scrollOffsetY - AndroidUtilities.dp(37.0f + f2))) + f);
        this.selectedTextView.setTranslationY(((float) (this.scrollOffsetY - AndroidUtilities.dp(f2 + 25.0f))) + f);
    }

    @SuppressLint({"NewApi"})
    private void updateLayout(boolean z) {
        if (this.gridView.getChildCount() <= 0) {
            RecyclerListView recyclerListView = this.gridView;
            int paddingTop = recyclerListView.getPaddingTop();
            this.scrollOffsetY = paddingTop;
            recyclerListView.setTopGlowOffset(paddingTop);
            this.containerView.invalidate();
            return;
        }
        View childAt = this.gridView.getChildAt(0);
        Holder holder = (Holder) this.gridView.findContainingViewHolder(childAt);
        int top = childAt.getTop();
        int dp = AndroidUtilities.dp(7.0f);
        if (top < AndroidUtilities.dp(7.0f) || holder == null || holder.getAdapterPosition() != 0) {
            top = dp;
        }
        Object obj = top <= AndroidUtilities.dp(12.0f) ? 1 : null;
        if ((obj != null && this.actionBar.getTag() == null) || (obj == null && this.actionBar.getTag() != null)) {
            this.actionBar.setTag(obj != null ? Integer.valueOf(1) : null);
            AnimatorSet animatorSet = this.actionBarAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.actionBarAnimation = null;
            }
            this.actionBarAnimation = new AnimatorSet();
            this.actionBarAnimation.setDuration(180);
            animatorSet = this.actionBarAnimation;
            Animator[] animatorArr = new Animator[2];
            ActionBar actionBar = this.actionBar;
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            float f = 1.0f;
            fArr[0] = obj != null ? 1.0f : 0.0f;
            animatorArr[0] = ObjectAnimator.ofFloat(actionBar, property, fArr);
            View view = this.actionBarShadow;
            property = View.ALPHA;
            fArr = new float[1];
            if (obj == null) {
                f = 0.0f;
            }
            fArr[0] = f;
            animatorArr[1] = ObjectAnimator.ofFloat(view, property, fArr);
            animatorSet.playTogether(animatorArr);
            this.actionBarAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    ChatAttachAlert.this.actionBarAnimation = null;
                }
            });
            this.actionBarAnimation.start();
        }
        LayoutParams layoutParams = (LayoutParams) this.gridView.getLayoutParams();
        top += layoutParams.topMargin - AndroidUtilities.dp(11.0f);
        if (this.scrollOffsetY != top) {
            RecyclerListView recyclerListView2 = this.gridView;
            this.scrollOffsetY = top;
            recyclerListView2.setTopGlowOffset(top - layoutParams.topMargin);
            updateSelectedPosition();
            this.containerView.invalidate();
        }
        this.progressView.setTranslationY((float) (this.scrollOffsetY + ((((this.gridView.getMeasuredHeight() - this.scrollOffsetY) - AndroidUtilities.dp(50.0f)) - this.progressView.getMeasuredHeight()) / 2)));
    }

    /* JADX WARNING: Removed duplicated region for block: B:25:0x00b2  */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x00ac  */
    public void updatePhotosButton(int r19) {
        /*
        r18 = this;
        r0 = r18;
        r1 = r19;
        r2 = selectedPhotos;
        r2 = r2.size();
        r3 = 180; // 0xb4 float:2.52E-43 double:8.9E-322;
        r5 = 2;
        r6 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r7 = 0;
        r8 = 1;
        r9 = 0;
        if (r2 != 0) goto L_0x0028;
    L_0x0014:
        r10 = r0.selectedCountView;
        r10.setPivotX(r7);
        r10 = r0.selectedCountView;
        r10.setPivotY(r7);
        if (r1 == 0) goto L_0x0022;
    L_0x0020:
        r10 = 1;
        goto L_0x0023;
    L_0x0022:
        r10 = 0;
    L_0x0023:
        r0.showCommentTextView(r9, r10);
        goto L_0x00b7;
    L_0x0028:
        r10 = r0.selectedCountView;
        r10.invalidate();
        if (r1 == 0) goto L_0x00a0;
    L_0x002f:
        if (r1 == 0) goto L_0x0033;
    L_0x0031:
        r10 = 1;
        goto L_0x0034;
    L_0x0033:
        r10 = 0;
    L_0x0034:
        r10 = r0.showCommentTextView(r8, r10);
        if (r10 != 0) goto L_0x00a0;
    L_0x003a:
        r10 = r0.selectedCountView;
        r11 = NUM; // 0x41a80000 float:21.0 double:5.442276803E-315;
        r11 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r11 = (float) r11;
        r10.setPivotX(r11);
        r10 = r0.selectedCountView;
        r11 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r11 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r11 = (float) r11;
        r10.setPivotY(r11);
        r10 = new android.animation.AnimatorSet;
        r10.<init>();
        r11 = new android.animation.Animator[r5];
        r12 = r0.selectedCountView;
        r13 = android.view.View.SCALE_X;
        r14 = new float[r5];
        r15 = NUM; // 0x3f8ccccd float:1.1 double:5.26768877E-315;
        r16 = NUM; // 0x3var_ float:0.9 double:5.2552552E-315;
        if (r1 != r8) goto L_0x006b;
    L_0x0067:
        r17 = NUM; // 0x3f8ccccd float:1.1 double:5.26768877E-315;
        goto L_0x006e;
    L_0x006b:
        r17 = NUM; // 0x3var_ float:0.9 double:5.2552552E-315;
    L_0x006e:
        r14[r9] = r17;
        r14[r8] = r6;
        r12 = android.animation.ObjectAnimator.ofFloat(r12, r13, r14);
        r11[r9] = r12;
        r12 = r0.selectedCountView;
        r13 = android.view.View.SCALE_Y;
        r14 = new float[r5];
        if (r1 != r8) goto L_0x0081;
    L_0x0080:
        goto L_0x0084;
    L_0x0081:
        r15 = NUM; // 0x3var_ float:0.9 double:5.2552552E-315;
    L_0x0084:
        r14[r9] = r15;
        r14[r8] = r6;
        r12 = android.animation.ObjectAnimator.ofFloat(r12, r13, r14);
        r11[r8] = r12;
        r10.playTogether(r11);
        r11 = new android.view.animation.OvershootInterpolator;
        r11.<init>();
        r10.setInterpolator(r11);
        r10.setDuration(r3);
        r10.start();
        goto L_0x00aa;
    L_0x00a0:
        r10 = r0.selectedCountView;
        r10.setPivotX(r7);
        r10 = r0.selectedCountView;
        r10.setPivotY(r7);
    L_0x00aa:
        if (r2 != r8) goto L_0x00b2;
    L_0x00ac:
        r10 = r0.selectedMenuItem;
        r10.hideSubItem(r9);
        goto L_0x00b7;
    L_0x00b2:
        r10 = r0.selectedMenuItem;
        r10.showSubItem(r9);
    L_0x00b7:
        r10 = r0.baseFragment;
        r10 = r10 instanceof org.telegram.ui.ChatActivity;
        if (r10 == 0) goto L_0x0153;
    L_0x00bd:
        if (r2 != 0) goto L_0x00c3;
    L_0x00bf:
        r10 = r0.menuShowed;
        if (r10 != 0) goto L_0x00c9;
    L_0x00c3:
        if (r2 == 0) goto L_0x0153;
    L_0x00c5:
        r10 = r0.menuShowed;
        if (r10 != 0) goto L_0x0153;
    L_0x00c9:
        if (r2 == 0) goto L_0x00d1;
    L_0x00cb:
        r2 = r0.editingMessageObject;
        if (r2 != 0) goto L_0x00d1;
    L_0x00cf:
        r2 = 1;
        goto L_0x00d2;
    L_0x00d1:
        r2 = 0;
    L_0x00d2:
        r0.menuShowed = r2;
        r2 = r0.menuAnimator;
        if (r2 == 0) goto L_0x00de;
    L_0x00d8:
        r2.cancel();
        r2 = 0;
        r0.menuAnimator = r2;
    L_0x00de:
        r2 = r0.menuShowed;
        if (r2 == 0) goto L_0x00ec;
    L_0x00e2:
        r2 = r0.selectedMenuItem;
        r2.setVisibility(r9);
        r2 = r0.selectedTextView;
        r2.setVisibility(r9);
    L_0x00ec:
        if (r1 != 0) goto L_0x0107;
    L_0x00ee:
        r1 = r0.selectedMenuItem;
        r2 = r0.menuShowed;
        if (r2 == 0) goto L_0x00f7;
    L_0x00f4:
        r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        goto L_0x00f8;
    L_0x00f7:
        r2 = 0;
    L_0x00f8:
        r1.setAlpha(r2);
        r1 = r0.selectedTextView;
        r2 = r0.menuShowed;
        if (r2 == 0) goto L_0x0102;
    L_0x0101:
        goto L_0x0103;
    L_0x0102:
        r6 = 0;
    L_0x0103:
        r1.setAlpha(r6);
        goto L_0x0153;
    L_0x0107:
        r1 = new android.animation.AnimatorSet;
        r1.<init>();
        r0.menuAnimator = r1;
        r1 = r0.menuAnimator;
        r2 = new android.animation.Animator[r5];
        r5 = r0.selectedMenuItem;
        r10 = android.view.View.ALPHA;
        r11 = new float[r8];
        r12 = r0.menuShowed;
        if (r12 == 0) goto L_0x011f;
    L_0x011c:
        r12 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        goto L_0x0120;
    L_0x011f:
        r12 = 0;
    L_0x0120:
        r11[r9] = r12;
        r5 = android.animation.ObjectAnimator.ofFloat(r5, r10, r11);
        r2[r9] = r5;
        r5 = r0.selectedTextView;
        r10 = android.view.View.ALPHA;
        r11 = new float[r8];
        r12 = r0.menuShowed;
        if (r12 == 0) goto L_0x0133;
    L_0x0132:
        goto L_0x0134;
    L_0x0133:
        r6 = 0;
    L_0x0134:
        r11[r9] = r6;
        r5 = android.animation.ObjectAnimator.ofFloat(r5, r10, r11);
        r2[r8] = r5;
        r1.playTogether(r2);
        r1 = r0.menuAnimator;
        r2 = new org.telegram.ui.Components.ChatAttachAlert$30;
        r2.<init>();
        r1.addListener(r2);
        r1 = r0.menuAnimator;
        r1.setDuration(r3);
        r1 = r0.menuAnimator;
        r1.start();
    L_0x0153:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlert.updatePhotosButton(int):void");
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
        BaseFragment baseFragment = this.baseFragment;
        if (baseFragment instanceof ChatActivity) {
            this.galleryAlbumEntry = MediaController.allMediaAlbumEntry;
            Chat currentChat = ((ChatActivity) baseFragment).getCurrentChat();
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
                CameraView cameraView = this.cameraView;
                float f = 1.0f;
                if (cameraView != null) {
                    cameraView.setAlpha(this.mediaEnabled ? 1.0f : 0.2f);
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
                this.pollsEnabled = false;
            }
        } else {
            this.galleryAlbumEntry = MediaController.allPhotosAlbumEntry;
            this.commentTextView.setVisibility(4);
        }
        if (VERSION.SDK_INT >= 23) {
            this.noGalleryPermissions = this.baseFragment.getParentActivity().checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != 0;
        }
        if (this.galleryAlbumEntry != null) {
            for (int i = 0; i < Math.min(100, this.galleryAlbumEntry.photos.size()); i++) {
                ((PhotoEntry) this.galleryAlbumEntry.photos.get(i)).reset();
            }
        }
        this.commentTextView.hidePopup(true);
        this.enterCommentEventSent = false;
        setFocusable(false);
        this.selectedAlbumEntry = this.galleryAlbumEntry;
        if (this.selectedAlbumEntry != null) {
            this.loading = false;
            EmptyTextProgressView emptyTextProgressView = this.progressView;
            if (emptyTextProgressView != null) {
                emptyTextProgressView.showTextView();
            }
        }
        this.dropDown.setText(LocaleController.getString("ChatGallery", NUM));
        clearSelectedPhotos();
        updatePhotosCounter();
        this.buttonsAdapter.notifyDataSetChanged();
        this.commentTextView.setText("");
        this.cameraPhotoLayoutManager.scrollToPositionWithOffset(0, 1000000);
        this.buttonsLayoutManager.scrollToPositionWithOffset(0, 1000000);
        this.layoutManager.scrollToPositionWithOffset(0, 1000000);
        updateAlbumsDropDown();
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

    private PhotoAttachPhotoCell getCellForIndex(int i) {
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
        if (this.noGalleryPermissions && VERSION.SDK_INT >= 23) {
            this.noGalleryPermissions = this.baseFragment.getParentActivity().checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != 0;
            if (!this.noGalleryPermissions) {
                loadGalleryPhotos();
            }
            this.adapter.notifyDataSetChanged();
            this.cameraAttachAdapter.notifyDataSetChanged();
        }
    }

    public void checkCamera(boolean z) {
        BaseFragment baseFragment = this.baseFragment;
        if (baseFragment != null) {
            boolean z2 = this.deviceHasGoodCamera;
            boolean z3 = this.noCameraPermissions;
            if (!SharedConfig.inappCamera) {
                this.deviceHasGoodCamera = false;
            } else if (VERSION.SDK_INT >= 23) {
                boolean z4 = baseFragment.getParentActivity().checkSelfPermission("android.permission.CAMERA") != 0;
                this.noCameraPermissions = z4;
                if (z4) {
                    if (z) {
                        try {
                            this.baseFragment.getParentActivity().requestPermissions(new String[]{r3}, 17);
                        } catch (Exception unused) {
                        }
                    }
                    this.deviceHasGoodCamera = false;
                } else {
                    if (z || SharedConfig.hasCameraCache) {
                        CameraController.getInstance().initCamera(null);
                    }
                    this.deviceHasGoodCamera = CameraController.getInstance().isCameraInitied();
                }
            } else {
                if (z || SharedConfig.hasCameraCache) {
                    CameraController.getInstance().initCamera(null);
                }
                this.deviceHasGoodCamera = CameraController.getInstance().isCameraInitied();
            }
            if (!(z2 == this.deviceHasGoodCamera && z3 == this.noCameraPermissions)) {
                PhotoAttachAdapter photoAttachAdapter = this.adapter;
                if (photoAttachAdapter != null) {
                    photoAttachAdapter.notifyDataSetChanged();
                }
            }
            if (!(!isShowing() || !this.deviceHasGoodCamera || this.baseFragment == null || this.backDrawable.getAlpha() == 0 || this.cameraOpened)) {
                showCamera();
            }
        }
    }

    public void onOpenAnimationEnd() {
        NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(false);
        AlbumEntry albumEntry;
        if (this.baseFragment instanceof ChatActivity) {
            albumEntry = MediaController.allMediaAlbumEntry;
        } else {
            albumEntry = MediaController.allPhotosAlbumEntry;
        }
        if (VERSION.SDK_INT <= 19 && albumEntry == null) {
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

    private int addToSelectedPhotos(PhotoEntry photoEntry, int i) {
        Integer valueOf = Integer.valueOf(photoEntry.imageId);
        if (selectedPhotos.containsKey(valueOf)) {
            selectedPhotos.remove(valueOf);
            int indexOf = selectedPhotosOrder.indexOf(valueOf);
            if (indexOf >= 0) {
                selectedPhotosOrder.remove(indexOf);
            }
            updatePhotosCounter();
            updateCheckedPhotoIndices();
            if (i >= 0) {
                photoEntry.reset();
                this.photoViewerProvider.updatePhotoAtIndex(i);
            }
            return indexOf;
        }
        selectedPhotos.put(valueOf, photoEntry);
        selectedPhotosOrder.add(valueOf);
        updatePhotosCounter();
        return -1;
    }

    private void clearSelectedPhotos() {
        if (!selectedPhotos.isEmpty()) {
            for (Entry value : selectedPhotos.entrySet()) {
                ((PhotoEntry) value.getValue()).reset();
            }
            selectedPhotos.clear();
            selectedPhotosOrder.clear();
        }
        if (!cameraPhotos.isEmpty()) {
            int size = cameraPhotos.size();
            for (int i = 0; i < size; i++) {
                PhotoEntry photoEntry = (PhotoEntry) cameraPhotos.get(i);
                new File(photoEntry.path).delete();
                String str = photoEntry.imagePath;
                if (str != null) {
                    new File(str).delete();
                }
                String str2 = photoEntry.thumbPath;
                if (str2 != null) {
                    new File(str2).delete();
                }
            }
            cameraPhotos.clear();
        }
        updatePhotosButton(0);
        this.adapter.notifyDataSetChanged();
        this.cameraAttachAdapter.notifyDataSetChanged();
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
        boolean z = (i == 0 || i == 2) ? false : true;
        hideCamera(z);
    }

    /* Access modifiers changed, original: protected */
    public boolean canDismissWithTouchOutside() {
        return this.cameraOpened ^ 1;
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
