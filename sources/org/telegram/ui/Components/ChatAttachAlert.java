package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.os.Build.VERSION;
import android.provider.Settings.System;
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
import androidx.annotation.Keep;
import androidx.exifinterface.media.ExifInterface;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ItemDecoration;
import androidx.recyclerview.widget.RecyclerView.LayoutParams;
import androidx.recyclerview.widget.RecyclerView.OnScrollListener;
import androidx.recyclerview.widget.RecyclerView.State;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DataQuery;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
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
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.TL_topPeer;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.BottomSheet.BottomSheetDelegateInterface;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.PhotoAttachCameraCell;
import org.telegram.ui.Cells.PhotoAttachPhotoCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.Components.ShutterButton.ShutterButtonDelegate;
import org.telegram.ui.PhotoViewer;
import org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider;
import org.telegram.ui.PhotoViewer.PhotoViewerProvider;
import org.telegram.ui.PhotoViewer.PlaceProviderObject;

public class ChatAttachAlert extends BottomSheet implements NotificationCenterDelegate, BottomSheetDelegateInterface {
    private static ArrayList<Object> cameraPhotos = new ArrayList();
    private static int lastImageId = -1;
    private static boolean mediaFromExternalCamera;
    private static HashMap<Object, Object> selectedPhotos = new HashMap();
    private static ArrayList<Object> selectedPhotosOrder = new ArrayList();
    private ListAdapter adapter;
    private int[] animateCameraValues;
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
    private int[] cameraViewLocation;
    private int cameraViewOffsetX;
    private int cameraViewOffsetY;
    private boolean cancelTakingPhotos;
    private Paint ciclePaint = new Paint(1);
    private TextView counterTextView;
    private int currentAccount = UserConfig.selectedAccount;
    private AnimatorSet currentHintAnimation;
    private DecelerateInterpolator decelerateInterpolator;
    private ChatAttachViewDelegate delegate;
    private boolean deviceHasGoodCamera;
    private boolean dragging;
    private MessageObject editingMessageObject;
    private boolean flashAnimationInProgress;
    private ImageView[] flashModeButton;
    private Runnable hideHintRunnable;
    private boolean hintShowed;
    private TextView hintTextView;
    private boolean ignoreLayout;
    private ArrayList<InnerAnimator> innerAnimators = new ArrayList();
    private DecelerateInterpolator interpolator;
    private float lastY;
    private LinearLayoutManager layoutManager;
    private View lineView;
    private RecyclerListView listView;
    private boolean loading;
    private int maxSelectedPhotos;
    private boolean maybeStartDraging;
    private CorrectlyMeasuringTextView mediaBanTooltip;
    private boolean mediaCaptured;
    private boolean mediaEnabled = true;
    private boolean openWithFrontFaceCamera;
    private boolean paused;
    private PhotoAttachAdapter photoAttachAdapter;
    private PhotoViewerProvider photoViewerProvider;
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
    private int[] viewPosition;
    private View[] views = new View[20];

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

        class CheckForLongPress implements Runnable {
            public int currentPressCount;

            CheckForLongPress() {
            }

            public void run() {
                if (AttachBotButton.this.checkingForLongPress && AttachBotButton.this.getParent() != null && this.currentPressCount == AttachBotButton.this.pressCount) {
                    AttachBotButton.this.checkingForLongPress = false;
                    AttachBotButton.this.performHapticFeedback(0);
                    AttachBotButton.this.onLongPress();
                    MotionEvent obtain = MotionEvent.obtain(0, 0, 3, 0.0f, 0.0f, 0);
                    AttachBotButton.this.onTouchEvent(obtain);
                    obtain.recycle();
                }
            }
        }

        private final class CheckForTap implements Runnable {
            private CheckForTap() {
            }

            /* synthetic */ CheckForTap(AttachBotButton attachBotButton, AnonymousClass1 anonymousClass1) {
                this();
            }

            public void run() {
                AttachBotButton attachBotButton;
                if (AttachBotButton.this.pendingCheckForLongPress == null) {
                    attachBotButton = AttachBotButton.this;
                    attachBotButton.pendingCheckForLongPress = new CheckForLongPress();
                }
                AttachBotButton.this.pendingCheckForLongPress.currentPressCount = AttachBotButton.access$1304(AttachBotButton.this);
                attachBotButton = AttachBotButton.this;
                attachBotButton.postDelayed(attachBotButton.pendingCheckForLongPress, (long) (ViewConfiguration.getLongPressTimeout() - ViewConfiguration.getTapTimeout()));
            }
        }

        static /* synthetic */ int access$1304(AttachBotButton attachBotButton) {
            int i = attachBotButton.pressCount + 1;
            attachBotButton.pressCount = i;
            return i;
        }

        public AttachBotButton(Context context) {
            super(context);
            this.imageView = new BackupImageView(context);
            this.imageView.setRoundRadius(AndroidUtilities.dp(27.0f));
            addView(this.imageView, LayoutHelper.createFrame(54, 54.0f, 49, 0.0f, 7.0f, 0.0f, 0.0f));
            this.nameTextView = new TextView(context);
            this.nameTextView.setTextSize(1, 12.0f);
            this.nameTextView.setMaxLines(2);
            this.nameTextView.setGravity(49);
            this.nameTextView.setLines(2);
            this.nameTextView.setEllipsize(TruncateAt.END);
            addView(this.nameTextView, LayoutHelper.createFrame(-1, -2.0f, 51, 6.0f, 65.0f, 6.0f, 0.0f));
        }

        /* Access modifiers changed, original: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(85.0f), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(100.0f), NUM));
        }

        private void onLongPress() {
            if (ChatAttachAlert.this.baseFragment != null && this.currentUser != null) {
                Builder builder = new Builder(getContext());
                builder.setTitle(LocaleController.getString("AppName", NUM));
                Object[] objArr = new Object[1];
                User user = this.currentUser;
                objArr[0] = ContactsController.formatName(user.first_name, user.last_name);
                builder.setMessage(LocaleController.formatString("ChatHintsDelete", NUM, objArr));
                builder.setPositiveButton(LocaleController.getString("OK", NUM), new -$$Lambda$ChatAttachAlert$AttachBotButton$gsYaOE0UOZvar_zKXNIpWWEDBs1k(this));
                builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
                builder.show();
            }
        }

        public /* synthetic */ void lambda$onLongPress$0$ChatAttachAlert$AttachBotButton(DialogInterface dialogInterface, int i) {
            DataQuery.getInstance(ChatAttachAlert.this.currentAccount).removeInline(this.currentUser.id);
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

        public boolean onTouchEvent(MotionEvent motionEvent) {
            boolean z = true;
            if (motionEvent.getAction() == 0) {
                this.pressed = true;
                invalidate();
            } else {
                if (this.pressed) {
                    if (motionEvent.getAction() == 1) {
                        getParent().requestDisallowInterceptTouchEvent(true);
                        this.pressed = false;
                        playSoundEffect(0);
                        ChatAttachAlert.this.delegate.didSelectBot(MessagesController.getInstance(ChatAttachAlert.this.currentAccount).getUser(Integer.valueOf(((TL_topPeer) DataQuery.getInstance(ChatAttachAlert.this.currentAccount).inlineBots.get(((Integer) getTag()).intValue())).peer.user_id)));
                        ChatAttachAlert.this.setUseRevealAnimation(false);
                        ChatAttachAlert.this.dismiss();
                        ChatAttachAlert.this.setUseRevealAnimation(true);
                        invalidate();
                    } else if (motionEvent.getAction() == 3) {
                        this.pressed = false;
                        invalidate();
                    }
                }
                z = false;
            }
            if (!z) {
                z = super.onTouchEvent(motionEvent);
            } else if (motionEvent.getAction() == 0) {
                startCheckLongPress();
            }
            if (!(motionEvent.getAction() == 0 || motionEvent.getAction() == 2)) {
                cancelCheckLongPress();
            }
            return z;
        }

        /* Access modifiers changed, original: protected */
        public void startCheckLongPress() {
            if (!this.checkingForLongPress) {
                this.checkingForLongPress = true;
                if (this.pendingCheckForTap == null) {
                    this.pendingCheckForTap = new CheckForTap(this, null);
                }
                postDelayed(this.pendingCheckForTap, (long) ViewConfiguration.getTapTimeout());
            }
        }

        /* Access modifiers changed, original: protected */
        public void cancelCheckLongPress() {
            this.checkingForLongPress = false;
            CheckForLongPress checkForLongPress = this.pendingCheckForLongPress;
            if (checkForLongPress != null) {
                removeCallbacks(checkForLongPress);
            }
            CheckForTap checkForTap = this.pendingCheckForTap;
            if (checkForTap != null) {
                removeCallbacks(checkForTap);
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
            addView(this.imageView, LayoutHelper.createFrame(54, 54.0f, 49, 0.0f, 5.0f, 0.0f, 0.0f));
            this.textView = new TextView(context);
            this.textView.setMaxLines(2);
            this.textView.setGravity(1);
            this.textView.setEllipsize(TruncateAt.END);
            this.textView.setTextColor(Theme.getColor("dialogTextGray2"));
            this.textView.setTextSize(1, 12.0f);
            this.textView.setLineSpacing((float) (-AndroidUtilities.dp(2.0f)), 1.0f);
            addView(this.textView, LayoutHelper.createFrame(-1, -2.0f, 51, 0.0f, 64.0f, 0.0f, 0.0f));
        }

        /* Access modifiers changed, original: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(85.0f), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(92.0f), NUM));
        }

        public void setTextAndIcon(CharSequence charSequence, Drawable drawable) {
            this.textView.setText(charSequence);
            this.imageView.setBackgroundDrawable(drawable);
        }
    }

    public interface ChatAttachViewDelegate {
        boolean allowGroupPhotos();

        void didPressedButton(int i);

        void didSelectBot(User user);

        View getRevealView();

        void onCameraOpened();
    }

    private class InnerAnimator {
        private AnimatorSet animatorSet;
        private float startRadius;

        private InnerAnimator() {
        }

        /* synthetic */ InnerAnimator(ChatAttachAlert chatAttachAlert, AnonymousClass1 anonymousClass1) {
            this();
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

        /* JADX WARNING: Removed duplicated region for block: B:29:0x009d  */
        public int setPhotoChecked(int r8, org.telegram.messenger.VideoEditedInfo r9) {
            /*
            r7 = this;
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
            r0 = r7.isPhotoChecked(r8);
            if (r0 != 0) goto L_0x0020;
        L_0x001f:
            return r1;
        L_0x0020:
            r0 = org.telegram.ui.Components.ChatAttachAlert.this;
            r0 = r0.getPhotoEntryAtPosition(r8);
            if (r0 != 0) goto L_0x0029;
        L_0x0028:
            return r1;
        L_0x0029:
            r2 = org.telegram.ui.Components.ChatAttachAlert.this;
            r2 = r2.addToSelectedPhotos(r0, r1);
            r3 = 0;
            if (r2 != r1) goto L_0x0042;
        L_0x0032:
            r2 = org.telegram.ui.Components.ChatAttachAlert.selectedPhotosOrder;
            r4 = r0.imageId;
            r4 = java.lang.Integer.valueOf(r4);
            r2 = r2.indexOf(r4);
            r4 = 1;
            goto L_0x0046;
        L_0x0042:
            r4 = 0;
            r0.editedInfo = r4;
            r4 = 0;
        L_0x0046:
            r0.editedInfo = r9;
            r9 = org.telegram.ui.Components.ChatAttachAlert.this;
            r9 = r9.attachPhotoRecyclerView;
            r9 = r9.getChildCount();
            r0 = 0;
        L_0x0053:
            if (r0 >= r9) goto L_0x0090;
        L_0x0055:
            r5 = org.telegram.ui.Components.ChatAttachAlert.this;
            r5 = r5.attachPhotoRecyclerView;
            r5 = r5.getChildAt(r0);
            r6 = r5 instanceof org.telegram.ui.Cells.PhotoAttachPhotoCell;
            if (r6 == 0) goto L_0x008d;
        L_0x0063:
            r6 = r5.getTag();
            r6 = (java.lang.Integer) r6;
            r6 = r6.intValue();
            if (r6 != r8) goto L_0x008d;
        L_0x006f:
            r9 = org.telegram.ui.Components.ChatAttachAlert.this;
            r9 = r9.baseFragment;
            r9 = r9 instanceof org.telegram.ui.ChatActivity;
            if (r9 == 0) goto L_0x0087;
        L_0x0079:
            r9 = org.telegram.ui.Components.ChatAttachAlert.this;
            r9 = r9.maxSelectedPhotos;
            if (r9 >= 0) goto L_0x0087;
        L_0x0081:
            r5 = (org.telegram.ui.Cells.PhotoAttachPhotoCell) r5;
            r5.setChecked(r2, r4, r3);
            goto L_0x0090;
        L_0x0087:
            r5 = (org.telegram.ui.Cells.PhotoAttachPhotoCell) r5;
            r5.setChecked(r1, r4, r3);
            goto L_0x0090;
        L_0x008d:
            r0 = r0 + 1;
            goto L_0x0053;
        L_0x0090:
            r9 = org.telegram.ui.Components.ChatAttachAlert.this;
            r9 = r9.cameraPhotoRecyclerView;
            r9 = r9.getChildCount();
            r0 = 0;
        L_0x009b:
            if (r0 >= r9) goto L_0x00d8;
        L_0x009d:
            r5 = org.telegram.ui.Components.ChatAttachAlert.this;
            r5 = r5.cameraPhotoRecyclerView;
            r5 = r5.getChildAt(r0);
            r6 = r5 instanceof org.telegram.ui.Cells.PhotoAttachPhotoCell;
            if (r6 == 0) goto L_0x00d5;
        L_0x00ab:
            r6 = r5.getTag();
            r6 = (java.lang.Integer) r6;
            r6 = r6.intValue();
            if (r6 != r8) goto L_0x00d5;
        L_0x00b7:
            r8 = org.telegram.ui.Components.ChatAttachAlert.this;
            r8 = r8.baseFragment;
            r8 = r8 instanceof org.telegram.ui.ChatActivity;
            if (r8 == 0) goto L_0x00cf;
        L_0x00c1:
            r8 = org.telegram.ui.Components.ChatAttachAlert.this;
            r8 = r8.maxSelectedPhotos;
            if (r8 >= 0) goto L_0x00cf;
        L_0x00c9:
            r5 = (org.telegram.ui.Cells.PhotoAttachPhotoCell) r5;
            r5.setChecked(r2, r4, r3);
            goto L_0x00d8;
        L_0x00cf:
            r5 = (org.telegram.ui.Cells.PhotoAttachPhotoCell) r5;
            r5.setChecked(r1, r4, r3);
            goto L_0x00d8;
        L_0x00d5:
            r0 = r0 + 1;
            goto L_0x009b;
        L_0x00d8:
            r8 = org.telegram.ui.Components.ChatAttachAlert.this;
            r8.updatePhotosButton();
            return r2;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlert$BasePhotoProvider.setPhotoChecked(int, org.telegram.messenger.VideoEditedInfo):int");
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

        public int getPhotoIndex(int i) {
            PhotoEntry access$000 = ChatAttachAlert.this.getPhotoEntryAtPosition(i);
            if (access$000 == null) {
                return -1;
            }
            return ChatAttachAlert.selectedPhotosOrder.indexOf(Integer.valueOf(access$000.imageId));
        }
    }

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public int getItemViewType(int i) {
            return i != 0 ? i != 1 ? 2 : 1 : 0;
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            return false;
        }

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View access$7900;
            if (i == 0) {
                access$7900 = ChatAttachAlert.this.attachView;
            } else if (i != 1) {
                access$7900 = new FrameLayout(this.mContext) {
                    /* Access modifiers changed, original: protected */
                    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                        i3 = ((i3 - i) - AndroidUtilities.dp(360.0f)) / 3;
                        for (i = 0; i < 4; i++) {
                            i2 = AndroidUtilities.dp(10.0f) + ((i % 4) * (AndroidUtilities.dp(85.0f) + i3));
                            View childAt = getChildAt(i);
                            childAt.layout(i2, 0, childAt.getMeasuredWidth() + i2, childAt.getMeasuredHeight());
                        }
                    }
                };
                for (int i2 = 0; i2 < 4; i2++) {
                    access$7900.addView(new AttachBotButton(this.mContext));
                }
                access$7900.setLayoutParams(new LayoutParams(-1, AndroidUtilities.dp(100.0f)));
            } else {
                access$7900 = new FrameLayout(this.mContext);
                access$7900.addView(new ShadowSectionCell(this.mContext), LayoutHelper.createFrame(-1, -1.0f));
            }
            return new Holder(access$7900);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            if (i == 1) {
                viewHolder.itemView.setBackgroundColor(Theme.getColor("dialogBackgroundGray"));
            } else if (i > 1) {
                i = (i - 2) * 4;
                FrameLayout frameLayout = (FrameLayout) viewHolder.itemView;
                for (int i2 = 0; i2 < 4; i2++) {
                    AttachBotButton attachBotButton = (AttachBotButton) frameLayout.getChildAt(i2);
                    int i3 = i + i2;
                    if (i3 >= DataQuery.getInstance(ChatAttachAlert.this.currentAccount).inlineBots.size()) {
                        attachBotButton.setVisibility(4);
                    } else {
                        attachBotButton.setVisibility(0);
                        attachBotButton.setTag(Integer.valueOf(i3));
                        attachBotButton.setUser(MessagesController.getInstance(ChatAttachAlert.this.currentAccount).getUser(Integer.valueOf(((TL_topPeer) DataQuery.getInstance(ChatAttachAlert.this.currentAccount).inlineBots.get(i3)).peer.user_id)));
                    }
                }
            }
        }

        public int getItemCount() {
            if (ChatAttachAlert.this.editingMessageObject != null || !(ChatAttachAlert.this.baseFragment instanceof ChatActivity)) {
                return 1;
            }
            return (!DataQuery.getInstance(ChatAttachAlert.this.currentAccount).inlineBots.isEmpty() ? ((int) Math.ceil((double) (((float) DataQuery.getInstance(ChatAttachAlert.this.currentAccount).inlineBots.size()) / 4.0f))) + 1 : 0) + 1;
        }
    }

    private class PhotoAttachAdapter extends SelectionAdapter {
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
            photoAttachPhotoCell.setDelegate(new -$$Lambda$ChatAttachAlert$PhotoAttachAdapter$K_1guAmOmM0zF5Rks69-v23fQoI(this));
            return new Holder(photoAttachPhotoCell);
        }

        public /* synthetic */ void lambda$createHolder$0$ChatAttachAlert$PhotoAttachAdapter(PhotoAttachPhotoCell photoAttachPhotoCell) {
            if (ChatAttachAlert.this.mediaEnabled) {
                int intValue = ((Integer) photoAttachPhotoCell.getTag()).intValue();
                PhotoEntry photoEntry = photoAttachPhotoCell.getPhotoEntry();
                int containsKey = ChatAttachAlert.selectedPhotos.containsKey(Integer.valueOf(photoEntry.imageId)) ^ 1;
                if (containsKey == 0 || ChatAttachAlert.this.maxSelectedPhotos < 0 || ChatAttachAlert.selectedPhotos.size() < ChatAttachAlert.this.maxSelectedPhotos) {
                    int size = containsKey != 0 ? ChatAttachAlert.selectedPhotosOrder.size() : -1;
                    if (!(ChatAttachAlert.this.baseFragment instanceof ChatActivity) || ChatAttachAlert.this.maxSelectedPhotos >= 0) {
                        photoAttachPhotoCell.setChecked(-1, containsKey, true);
                    } else {
                        photoAttachPhotoCell.setChecked(size, containsKey, true);
                    }
                    ChatAttachAlert.this.addToSelectedPhotos(photoEntry, intValue);
                    if (this == ChatAttachAlert.this.cameraAttachAdapter) {
                        if (ChatAttachAlert.this.photoAttachAdapter.needCamera && ChatAttachAlert.this.deviceHasGoodCamera) {
                            intValue++;
                        }
                        ChatAttachAlert.this.photoAttachAdapter.notifyItemChanged(intValue);
                    } else {
                        ChatAttachAlert.this.cameraAttachAdapter.notifyItemChanged(intValue);
                    }
                    ChatAttachAlert.this.updatePhotosButton();
                }
            }
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            boolean z = false;
            if (!this.needCamera || !ChatAttachAlert.this.deviceHasGoodCamera || i != 0) {
                if (this.needCamera && ChatAttachAlert.this.deviceHasGoodCamera) {
                    i--;
                }
                PhotoAttachPhotoCell photoAttachPhotoCell = (PhotoAttachPhotoCell) viewHolder.itemView;
                PhotoEntry access$000 = ChatAttachAlert.this.getPhotoEntryAtPosition(i);
                photoAttachPhotoCell.setPhotoEntry(access$000, this.needCamera, i == getItemCount() - 1);
                if (!(ChatAttachAlert.this.baseFragment instanceof ChatActivity) || ChatAttachAlert.this.maxSelectedPhotos >= 0) {
                    photoAttachPhotoCell.setChecked(-1, ChatAttachAlert.selectedPhotos.containsKey(Integer.valueOf(access$000.imageId)), false);
                } else {
                    photoAttachPhotoCell.setChecked(ChatAttachAlert.selectedPhotosOrder.indexOf(Integer.valueOf(access$000.imageId)), ChatAttachAlert.selectedPhotos.containsKey(Integer.valueOf(access$000.imageId)), false);
                }
                photoAttachPhotoCell.getImageView().setTag(Integer.valueOf(i));
                photoAttachPhotoCell.setTag(Integer.valueOf(i));
                if (this == ChatAttachAlert.this.cameraAttachAdapter && ChatAttachAlert.this.cameraPhotoLayoutManager.getOrientation() == 1) {
                    z = true;
                }
                photoAttachPhotoCell.setIsVertical(z);
            } else if (!this.needCamera || !ChatAttachAlert.this.deviceHasGoodCamera || i != 0) {
            } else {
                if (ChatAttachAlert.this.cameraView == null || !ChatAttachAlert.this.cameraView.isInitied()) {
                    viewHolder.itemView.setVisibility(0);
                } else {
                    viewHolder.itemView.setVisibility(4);
                }
            }
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            if (i == 1) {
                return new Holder(new PhotoAttachCameraCell(this.mContext));
            }
            if (this.viewsCache.isEmpty()) {
                return createHolder();
            }
            Holder holder = (Holder) this.viewsCache.get(0);
            this.viewsCache.remove(0);
            return holder;
        }

        public int getItemCount() {
            AlbumEntry albumEntry;
            int i = (this.needCamera && ChatAttachAlert.this.deviceHasGoodCamera) ? 1 : 0;
            i += ChatAttachAlert.cameraPhotos.size();
            if (ChatAttachAlert.this.baseFragment instanceof ChatActivity) {
                albumEntry = MediaController.allMediaAlbumEntry;
            } else {
                albumEntry = MediaController.allPhotosAlbumEntry;
            }
            return albumEntry != null ? i + albumEntry.photos.size() : i;
        }

        public int getItemViewType(int i) {
            return (this.needCamera && ChatAttachAlert.this.deviceHasGoodCamera && i == 0) ? 1 : 0;
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

    static /* synthetic */ int access$6410() {
        int i = lastImageId;
        lastImageId = i - 1;
        return i;
    }

    private void updateCheckedPhotoIndices() {
        if (this.baseFragment instanceof ChatActivity) {
            int childCount = this.attachPhotoRecyclerView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = this.attachPhotoRecyclerView.getChildAt(i);
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
        AlbumEntry albumEntry;
        i -= size;
        if (this.baseFragment instanceof ChatActivity) {
            albumEntry = MediaController.allMediaAlbumEntry;
        } else {
            albumEntry = MediaController.allPhotosAlbumEntry;
        }
        if (i < albumEntry.photos.size()) {
            return (PhotoEntry) albumEntry.photos.get(i);
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

    public ChatAttachAlert(Context context, BaseFragment baseFragment) {
        Context context2 = context;
        final BaseFragment baseFragment2 = baseFragment;
        super(context2, false, 0);
        int i = 2;
        this.flashModeButton = new ImageView[2];
        this.cameraViewLocation = new int[2];
        this.viewPosition = new int[2];
        int i2 = 5;
        this.animateCameraValues = new int[5];
        this.interpolator = new DecelerateInterpolator(1.5f);
        this.maxSelectedPhotos = -1;
        this.decelerateInterpolator = new DecelerateInterpolator();
        this.loading = true;
        this.photoViewerProvider = new BasePhotoProvider() {
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
                placeProviderObject.parentView = ChatAttachAlert.this.attachPhotoRecyclerView;
                placeProviderObject.imageReceiver = access$1000.getImageView().getImageReceiver();
                placeProviderObject.thumb = placeProviderObject.imageReceiver.getBitmapSafe();
                placeProviderObject.scale = access$1000.getImageView().getScaleX();
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
                            access$1000.getImageView().setImage(access$000.thumbPath, null, access$1000.getContext().getResources().getDrawable(NUM));
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
                                imageView.setImage(stringBuilder.toString(), null, access$1000.getContext().getResources().getDrawable(NUM));
                            } else {
                                imageView = access$1000.getImageView();
                                stringBuilder = new StringBuilder();
                                stringBuilder.append("thumb://");
                                stringBuilder.append(access$000.imageId);
                                stringBuilder.append(str);
                                stringBuilder.append(access$000.path);
                                imageView.setImage(stringBuilder.toString(), null, access$1000.getContext().getResources().getDrawable(NUM));
                            }
                        } else {
                            access$1000.getImageView().setImageResource(NUM);
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
                int childCount = ChatAttachAlert.this.attachPhotoRecyclerView.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View childAt = ChatAttachAlert.this.attachPhotoRecyclerView.getChildAt(i);
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
                ChatAttachAlert.this.delegate.didPressedButton(7);
            }
        };
        this.baseFragment = baseFragment2;
        this.ciclePaint.setColor(Theme.getColor("dialogBackground"));
        setDelegate(this);
        setUseRevealAnimation(true);
        checkCamera(false);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.albumsDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.reloadInlineHints);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.cameraInitied);
        this.shadowDrawable = context.getResources().getDrawable(NUM).mutate();
        Theme.setDrawableColor(this.shadowDrawable, Theme.getColor("dialogBackground"));
        AnonymousClass2 anonymousClass2 = new RecyclerListView(context2) {
            private int lastHeight;
            private int lastWidth;

            public void requestLayout() {
                if (!ChatAttachAlert.this.ignoreLayout) {
                    super.requestLayout();
                }
            }

            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                if (ChatAttachAlert.this.cameraAnimationInProgress) {
                    return true;
                }
                if (ChatAttachAlert.this.cameraOpened) {
                    return ChatAttachAlert.this.processTouchEvent(motionEvent);
                }
                if (motionEvent.getAction() != 0 || ChatAttachAlert.this.scrollOffsetY == 0 || motionEvent.getY() >= ((float) ChatAttachAlert.this.scrollOffsetY)) {
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
            public void onMeasure(int i, int i2) {
                i2 = MeasureSpec.getSize(i2);
                if (VERSION.SDK_INT >= 21) {
                    i2 -= AndroidUtilities.statusBarHeight;
                }
                float f = (float) (ChatAttachAlert.this.baseFragment instanceof ChatActivity ? 298 : 203);
                int access$2400 = (ChatAttachAlert.this.backgroundPaddingTop + AndroidUtilities.dp(f)) + (DataQuery.getInstance(ChatAttachAlert.this.currentAccount).inlineBots.isEmpty() ? 0 : (((int) Math.ceil((double) (((float) DataQuery.getInstance(ChatAttachAlert.this.currentAccount).inlineBots.size()) / 4.0f))) * AndroidUtilities.dp(100.0f)) + AndroidUtilities.dp(12.0f));
                int max = access$2400 == AndroidUtilities.dp(f) ? 0 : Math.max(0, i2 - AndroidUtilities.dp(f));
                if (max != 0 && access$2400 < i2) {
                    max -= i2 - access$2400;
                }
                if (max == 0) {
                    max = ChatAttachAlert.this.backgroundPaddingTop;
                }
                if (getPaddingTop() != max) {
                    ChatAttachAlert.this.ignoreLayout = true;
                    setPadding(ChatAttachAlert.this.backgroundPaddingLeft, max, ChatAttachAlert.this.backgroundPaddingLeft, 0);
                    ChatAttachAlert.this.ignoreLayout = false;
                }
                super.onMeasure(i, MeasureSpec.makeMeasureSpec(Math.min(access$2400, i2), NUM));
            }

            /* Access modifiers changed, original: protected */
            /* JADX WARNING: Removed duplicated region for block: B:13:0x0058  */
            /* JADX WARNING: Removed duplicated region for block: B:7:0x0043  */
            /* JADX WARNING: Removed duplicated region for block: B:13:0x0058  */
            public void onLayout(boolean r10, int r11, int r12, int r13, int r14) {
                /*
                r9 = this;
                r6 = r13 - r11;
                r7 = r14 - r12;
                r0 = org.telegram.ui.Components.ChatAttachAlert.this;
                r0 = r0.listView;
                r0 = r0.getChildCount();
                r1 = 1;
                r8 = 0;
                r2 = -1;
                if (r0 <= 0) goto L_0x003f;
            L_0x0013:
                r0 = org.telegram.ui.Components.ChatAttachAlert.this;
                r0 = r0.listView;
                r3 = org.telegram.ui.Components.ChatAttachAlert.this;
                r3 = r3.listView;
                r3 = r3.getChildCount();
                r3 = r3 - r1;
                r0 = r0.getChildAt(r3);
                r3 = org.telegram.ui.Components.ChatAttachAlert.this;
                r3 = r3.listView;
                r3 = r3.findContainingViewHolder(r0);
                r3 = (org.telegram.ui.Components.RecyclerListView.Holder) r3;
                if (r3 == 0) goto L_0x003f;
            L_0x0036:
                r3 = r3.getAdapterPosition();
                r0 = r0.getTop();
                goto L_0x0041;
            L_0x003f:
                r0 = 0;
                r3 = -1;
            L_0x0041:
                if (r3 < 0) goto L_0x0051;
            L_0x0043:
                r4 = r9.lastHeight;
                r5 = r7 - r4;
                if (r5 == 0) goto L_0x0051;
            L_0x0049:
                r0 = r0 + r7;
                r0 = r0 - r4;
                r4 = r9.getPaddingTop();
                r0 = r0 - r4;
                goto L_0x0053;
            L_0x0051:
                r0 = 0;
                r3 = -1;
            L_0x0053:
                super.onLayout(r10, r11, r12, r13, r14);
                if (r3 == r2) goto L_0x0074;
            L_0x0058:
                r2 = org.telegram.ui.Components.ChatAttachAlert.this;
                r2.ignoreLayout = r1;
                r1 = org.telegram.ui.Components.ChatAttachAlert.this;
                r1 = r1.layoutManager;
                r1.scrollToPositionWithOffset(r3, r0);
                r1 = 0;
                r0 = r9;
                r2 = r11;
                r3 = r12;
                r4 = r13;
                r5 = r14;
                super.onLayout(r1, r2, r3, r4, r5);
                r0 = org.telegram.ui.Components.ChatAttachAlert.this;
                r0.ignoreLayout = r8;
            L_0x0074:
                r9.lastHeight = r7;
                r9.lastWidth = r6;
                r0 = org.telegram.ui.Components.ChatAttachAlert.this;
                r0.updateLayout();
                r0 = org.telegram.ui.Components.ChatAttachAlert.this;
                r0.checkCameraViewPosition();
                return;
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlert$AnonymousClass2.onLayout(boolean, int, int, int, int):void");
            }

            public void onDraw(Canvas canvas) {
                if (!ChatAttachAlert.this.useRevealAnimation || VERSION.SDK_INT > 19) {
                    ChatAttachAlert.this.shadowDrawable.setBounds(0, ChatAttachAlert.this.scrollOffsetY - ChatAttachAlert.this.backgroundPaddingTop, getMeasuredWidth(), getMeasuredHeight());
                    ChatAttachAlert.this.shadowDrawable.draw(canvas);
                    return;
                }
                canvas.save();
                canvas.clipRect(ChatAttachAlert.this.backgroundPaddingLeft, ChatAttachAlert.this.scrollOffsetY, getMeasuredWidth() - ChatAttachAlert.this.backgroundPaddingLeft, getMeasuredHeight());
                if (ChatAttachAlert.this.revealAnimationInProgress) {
                    canvas.drawCircle((float) ChatAttachAlert.this.revealX, (float) ChatAttachAlert.this.revealY, ChatAttachAlert.this.revealRadius, ChatAttachAlert.this.ciclePaint);
                } else {
                    canvas.drawRect((float) ChatAttachAlert.this.backgroundPaddingLeft, (float) ChatAttachAlert.this.scrollOffsetY, (float) (getMeasuredWidth() - ChatAttachAlert.this.backgroundPaddingLeft), (float) getMeasuredHeight(), ChatAttachAlert.this.ciclePaint);
                }
                canvas.restore();
            }

            public void setTranslationY(float f) {
                super.setTranslationY(f);
                ChatAttachAlert.this.checkCameraViewPosition();
            }
        };
        this.listView = anonymousClass2;
        this.containerView = anonymousClass2;
        RecyclerListView recyclerListView = this.listView;
        this.nestedScrollChild = recyclerListView;
        recyclerListView.setWillNotDraw(false);
        this.listView.setClipToPadding(false);
        recyclerListView = this.listView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        this.layoutManager = linearLayoutManager;
        recyclerListView.setLayoutManager(linearLayoutManager);
        this.layoutManager.setOrientation(1);
        recyclerListView = this.listView;
        ListAdapter listAdapter = new ListAdapter(context2);
        this.adapter = listAdapter;
        recyclerListView.setAdapter(listAdapter);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setEnabled(true);
        this.listView.setGlowColor(Theme.getColor("dialogScrollGlow"));
        this.listView.addItemDecoration(new ItemDecoration() {
            public void getItemOffsets(Rect rect, View view, RecyclerView recyclerView, State state) {
                rect.left = 0;
                rect.right = 0;
                rect.top = 0;
                rect.bottom = 0;
            }
        });
        this.listView.setOnScrollListener(new OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
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
        });
        ViewGroup viewGroup = this.containerView;
        int i3 = this.backgroundPaddingLeft;
        viewGroup.setPadding(i3, 0, i3, 0);
        this.containerView.setImportantForAccessibility(2);
        this.attachView = new FrameLayout(context2) {
            /* Access modifiers changed, original: protected */
            public void onMeasure(int i, int i2) {
                if (ChatAttachAlert.this.baseFragment instanceof ChatActivity) {
                    super.onMeasure(i, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(298.0f), NUM));
                } else {
                    super.onMeasure(i, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(203.0f), NUM));
                }
            }

            /* Access modifiers changed, original: protected */
            public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                i3 -= i;
                i4 -= i2;
                int dp = AndroidUtilities.dp(8.0f);
                int i5 = 0;
                ChatAttachAlert.this.attachPhotoRecyclerView.layout(0, dp, i3, ChatAttachAlert.this.attachPhotoRecyclerView.getMeasuredHeight() + dp);
                ChatAttachAlert.this.progressView.layout(0, dp, i3, ChatAttachAlert.this.progressView.getMeasuredHeight() + dp);
                ChatAttachAlert.this.lineView.layout(0, AndroidUtilities.dp(96.0f), i3, AndroidUtilities.dp(96.0f) + ChatAttachAlert.this.lineView.getMeasuredHeight());
                ChatAttachAlert.this.hintTextView.layout((i3 - ChatAttachAlert.this.hintTextView.getMeasuredWidth()) - AndroidUtilities.dp(5.0f), (i4 - ChatAttachAlert.this.hintTextView.getMeasuredHeight()) - AndroidUtilities.dp(5.0f), i3 - AndroidUtilities.dp(5.0f), i4 - AndroidUtilities.dp(5.0f));
                i = (i3 - ChatAttachAlert.this.mediaBanTooltip.getMeasuredWidth()) / 2;
                dp += (ChatAttachAlert.this.attachPhotoRecyclerView.getMeasuredHeight() - ChatAttachAlert.this.mediaBanTooltip.getMeasuredHeight()) / 2;
                ChatAttachAlert.this.mediaBanTooltip.layout(i, dp, ChatAttachAlert.this.mediaBanTooltip.getMeasuredWidth() + i, ChatAttachAlert.this.mediaBanTooltip.getMeasuredHeight() + dp);
                i3 = (i3 - AndroidUtilities.dp(360.0f)) / 3;
                dp = 0;
                while (i5 < 8) {
                    if (ChatAttachAlert.this.views[i5] != null) {
                        i = AndroidUtilities.dp((float) (((dp / 4) * 97) + 105));
                        i2 = AndroidUtilities.dp(10.0f) + ((dp % 4) * (AndroidUtilities.dp(85.0f) + i3));
                        ChatAttachAlert.this.views[i5].layout(i2, i, ChatAttachAlert.this.views[i5].getMeasuredWidth() + i2, ChatAttachAlert.this.views[i5].getMeasuredHeight() + i);
                        dp++;
                    }
                    i5++;
                }
            }
        };
        View[] viewArr = this.views;
        RecyclerListView recyclerListView2 = new RecyclerListView(context2);
        this.attachPhotoRecyclerView = recyclerListView2;
        viewArr[8] = recyclerListView2;
        this.attachPhotoRecyclerView.setVerticalScrollBarEnabled(true);
        recyclerListView = this.attachPhotoRecyclerView;
        PhotoAttachAdapter photoAttachAdapter = new PhotoAttachAdapter(context2, true);
        this.photoAttachAdapter = photoAttachAdapter;
        recyclerListView.setAdapter(photoAttachAdapter);
        this.attachPhotoRecyclerView.setClipToPadding(false);
        this.attachPhotoRecyclerView.setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f), 0);
        this.attachPhotoRecyclerView.setItemAnimator(null);
        this.attachPhotoRecyclerView.setLayoutAnimation(null);
        this.attachPhotoRecyclerView.setOverScrollMode(2);
        this.attachView.addView(this.attachPhotoRecyclerView, LayoutHelper.createFrame(-1, 80.0f));
        this.attachPhotoLayoutManager = new LinearLayoutManager(context2) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        this.attachPhotoLayoutManager.setOrientation(0);
        this.attachPhotoRecyclerView.setLayoutManager(this.attachPhotoLayoutManager);
        this.attachPhotoRecyclerView.setOnItemClickListener(new -$$Lambda$ChatAttachAlert$gSlgqD7A9IVmfNd3sjxwTiZ76jE(this));
        this.attachPhotoRecyclerView.setOnScrollListener(new OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                ChatAttachAlert.this.checkCameraViewPosition();
            }
        });
        viewArr = this.views;
        CorrectlyMeasuringTextView correctlyMeasuringTextView = new CorrectlyMeasuringTextView(context2);
        this.mediaBanTooltip = correctlyMeasuringTextView;
        viewArr[11] = correctlyMeasuringTextView;
        this.mediaBanTooltip.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(3.0f), Theme.getColor("chat_attachMediaBanBackground")));
        this.mediaBanTooltip.setTextColor(Theme.getColor("chat_attachMediaBanText"));
        this.mediaBanTooltip.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f));
        this.mediaBanTooltip.setGravity(16);
        this.mediaBanTooltip.setTextSize(1, 14.0f);
        this.mediaBanTooltip.setVisibility(4);
        this.attachView.addView(this.mediaBanTooltip, LayoutHelper.createFrame(-2, -2.0f, 51, 14.0f, 0.0f, 14.0f, 0.0f));
        viewArr = this.views;
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context2);
        this.progressView = emptyTextProgressView;
        viewArr[9] = emptyTextProgressView;
        if (VERSION.SDK_INT < 23 || getContext().checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == 0) {
            this.progressView.setText(LocaleController.getString("NoPhotos", NUM));
            this.progressView.setTextSize(20);
        } else {
            this.progressView.setText(LocaleController.getString("PermissionStorage", NUM));
            this.progressView.setTextSize(16);
        }
        this.attachView.addView(this.progressView, LayoutHelper.createFrame(-1, 80.0f));
        this.attachPhotoRecyclerView.setEmptyView(this.progressView);
        viewArr = this.views;
        AnonymousClass8 anonymousClass8 = new View(getContext()) {
            public boolean hasOverlappingRendering() {
                return false;
            }
        };
        this.lineView = anonymousClass8;
        viewArr[10] = anonymousClass8;
        this.lineView.setBackgroundColor(Theme.getColor("dialogGrayLine"));
        this.attachView.addView(this.lineView, new FrameLayout.LayoutParams(-1, 1, 51));
        CharSequence[] charSequenceArr = new CharSequence[]{LocaleController.getString("ChatCamera", NUM), LocaleController.getString("ChatGallery", NUM), LocaleController.getString("ChatVideo", NUM), LocaleController.getString("AttachMusic", NUM), LocaleController.getString("ChatDocument", NUM), LocaleController.getString("AttachContact", NUM), LocaleController.getString("ChatLocation", NUM), ""};
        int i4 = 0;
        while (i4 < 8) {
            if ((this.baseFragment instanceof ChatActivity) || !(i4 == i || i4 == 3 || i4 == i2 || i4 == 6)) {
                AttachButton attachButton = new AttachButton(context2);
                this.attachButtons.add(attachButton);
                attachButton.setTextAndIcon(charSequenceArr[i4], Theme.chat_attachButtonDrawables[i4]);
                this.attachView.addView(attachButton, LayoutHelper.createFrame(85, 91, 51));
                attachButton.setTag(Integer.valueOf(i4));
                this.views[i4] = attachButton;
                if (i4 == 7) {
                    this.sendPhotosButton = attachButton;
                    this.sendPhotosButton.imageView.setPadding(0, AndroidUtilities.dp(4.0f), 0, 0);
                } else if (i4 == 4) {
                    this.sendDocumentsButton = attachButton;
                }
                attachButton.setOnClickListener(new -$$Lambda$ChatAttachAlert$0NyE8I5jQF6-Q8wSGheAU5ZO3eo(this, baseFragment2));
            }
            i4++;
            i = 2;
            i2 = 5;
        }
        this.hintTextView = new TextView(context2);
        this.hintTextView.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(3.0f), Theme.getColor("chat_gifSaveHintBackground")));
        this.hintTextView.setTextColor(Theme.getColor("chat_gifSaveHintText"));
        this.hintTextView.setTextSize(1, 14.0f);
        this.hintTextView.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
        this.hintTextView.setText(LocaleController.getString("AttachBotsHelp", NUM));
        this.hintTextView.setGravity(16);
        this.hintTextView.setVisibility(4);
        this.hintTextView.setCompoundDrawablesWithIntrinsicBounds(NUM, 0, 0, 0);
        this.hintTextView.setCompoundDrawablePadding(AndroidUtilities.dp(8.0f));
        this.attachView.addView(this.hintTextView, LayoutHelper.createFrame(-2, 32.0f, 85, 5.0f, 0.0f, 5.0f, 5.0f));
        if (this.loading) {
            this.progressView.showProgress();
        } else {
            this.progressView.showTextView();
        }
        this.recordTime = new TextView(context2);
        this.recordTime.setBackgroundResource(NUM);
        this.recordTime.getBackground().setColorFilter(new PorterDuffColorFilter(NUM, Mode.MULTIPLY));
        this.recordTime.setTextSize(1, 15.0f);
        this.recordTime.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.recordTime.setAlpha(0.0f);
        this.recordTime.setTextColor(-1);
        this.recordTime.setPadding(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(5.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(5.0f));
        this.container.addView(this.recordTime, LayoutHelper.createFrame(-2, -2.0f, 49, 0.0f, 16.0f, 0.0f, 0.0f));
        this.cameraPanel = new FrameLayout(context2) {
            /* Access modifiers changed, original: protected */
            public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                int measuredWidth = getMeasuredWidth() / 2;
                i2 = getMeasuredHeight() / 2;
                ChatAttachAlert.this.shutterButton.layout(measuredWidth - (ChatAttachAlert.this.shutterButton.getMeasuredWidth() / 2), i2 - (ChatAttachAlert.this.shutterButton.getMeasuredHeight() / 2), (ChatAttachAlert.this.shutterButton.getMeasuredWidth() / 2) + measuredWidth, (ChatAttachAlert.this.shutterButton.getMeasuredHeight() / 2) + i2);
                if (getMeasuredWidth() == AndroidUtilities.dp(100.0f)) {
                    measuredWidth = getMeasuredWidth() / 2;
                    i3 = i2 / 2;
                    i2 = (i2 + i3) + AndroidUtilities.dp(17.0f);
                    i4 = i3 - AndroidUtilities.dp(17.0f);
                    i3 = measuredWidth;
                } else {
                    i2 = measuredWidth / 2;
                    measuredWidth = (measuredWidth + i2) + AndroidUtilities.dp(17.0f);
                    i4 = getMeasuredHeight() / 2;
                    i3 = i2 - AndroidUtilities.dp(17.0f);
                    i2 = i4;
                }
                ChatAttachAlert.this.switchCameraButton.layout(measuredWidth - (ChatAttachAlert.this.switchCameraButton.getMeasuredWidth() / 2), i2 - (ChatAttachAlert.this.switchCameraButton.getMeasuredHeight() / 2), measuredWidth + (ChatAttachAlert.this.switchCameraButton.getMeasuredWidth() / 2), i2 + (ChatAttachAlert.this.switchCameraButton.getMeasuredHeight() / 2));
                for (measuredWidth = 0; measuredWidth < 2; measuredWidth++) {
                    ChatAttachAlert.this.flashModeButton[measuredWidth].layout(i3 - (ChatAttachAlert.this.flashModeButton[measuredWidth].getMeasuredWidth() / 2), i4 - (ChatAttachAlert.this.flashModeButton[measuredWidth].getMeasuredHeight() / 2), (ChatAttachAlert.this.flashModeButton[measuredWidth].getMeasuredWidth() / 2) + i3, (ChatAttachAlert.this.flashModeButton[measuredWidth].getMeasuredHeight() / 2) + i4);
                }
            }
        };
        this.cameraPanel.setVisibility(8);
        this.cameraPanel.setAlpha(0.0f);
        this.container.addView(this.cameraPanel, LayoutHelper.createFrame(-1, 100, 83));
        this.counterTextView = new TextView(context2);
        this.counterTextView.setBackgroundResource(NUM);
        this.counterTextView.setVisibility(8);
        this.counterTextView.setTextColor(-1);
        this.counterTextView.setGravity(17);
        this.counterTextView.setPivotX(0.0f);
        this.counterTextView.setPivotY(0.0f);
        this.counterTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.counterTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, NUM, 0);
        this.counterTextView.setCompoundDrawablePadding(AndroidUtilities.dp(4.0f));
        this.counterTextView.setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f), 0);
        this.container.addView(this.counterTextView, LayoutHelper.createFrame(-2, 38.0f, 51, 0.0f, 0.0f, 0.0f, 116.0f));
        this.counterTextView.setOnClickListener(new -$$Lambda$ChatAttachAlert$70irb8b3j6E6JPtp7E_VZic7gT0(this));
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
                ChatAttachAlert.this.videoRecordRunnable = new -$$Lambda$ChatAttachAlert$10$ZeL0eTEJEk-Sg8NguIkwCCYWIW0(this);
                AndroidUtilities.lockOrientation(baseFragment2.getParentActivity());
                CameraController.getInstance().recordVideo(ChatAttachAlert.this.cameraView.getCameraSession(), this.outputFile, new -$$Lambda$ChatAttachAlert$10$uU9sWMugjD7x3FTSol0pBdhggvw(this), new -$$Lambda$ChatAttachAlert$10$yg9RMHiEdmJNgfGykAnz6vx1P-Q(this));
                ChatAttachAlert.this.shutterButton.setState(ShutterButton.State.RECORDING, true);
                return true;
            }

            public /* synthetic */ void lambda$shutterLongPressed$0$ChatAttachAlert$10() {
                if (ChatAttachAlert.this.videoRecordRunnable != null) {
                    ChatAttachAlert.this.videoRecordTime = ChatAttachAlert.this.videoRecordTime + 1;
                    ChatAttachAlert.this.recordTime.setText(String.format("%02d:%02d", new Object[]{Integer.valueOf(ChatAttachAlert.this.videoRecordTime / 60), Integer.valueOf(ChatAttachAlert.this.videoRecordTime % 60)}));
                    AndroidUtilities.runOnUIThread(ChatAttachAlert.this.videoRecordRunnable, 1000);
                }
            }

            public /* synthetic */ void lambda$shutterLongPressed$1$ChatAttachAlert$10(String str, long j) {
                if (this.outputFile != null && ChatAttachAlert.this.baseFragment != null) {
                    ChatAttachAlert.mediaFromExternalCamera = false;
                    PhotoEntry photoEntry = new PhotoEntry(0, ChatAttachAlert.access$6410(), 0, this.outputFile.getAbsolutePath(), 0, true);
                    photoEntry.duration = (int) j;
                    photoEntry.thumbPath = str;
                    ChatAttachAlert.this.openPhotoViewer(photoEntry, false, false);
                }
            }

            public /* synthetic */ void lambda$shutterLongPressed$2$ChatAttachAlert$10() {
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
                    if (ChatAttachAlert.this.shutterButton.getState() == ShutterButton.State.RECORDING) {
                        ChatAttachAlert.this.resetRecordState();
                        CameraController.getInstance().stopVideoRecording(ChatAttachAlert.this.cameraView.getCameraSession(), false);
                        ChatAttachAlert.this.shutterButton.setState(ShutterButton.State.DEFAULT, true);
                        return;
                    }
                    if (!((ChatAttachAlert.this.baseFragment instanceof ChatActivity) && ((ChatActivity) ChatAttachAlert.this.baseFragment).isSecretChat())) {
                        z = false;
                    }
                    File generatePicturePath = AndroidUtilities.generatePicturePath(z);
                    z = ChatAttachAlert.this.cameraView.getCameraSession().isSameTakePictureOrientation();
                    ChatAttachAlert.this.cameraView.getCameraSession().setFlipFront(baseFragment2 instanceof ChatActivity);
                    ChatAttachAlert.this.takingPhoto = CameraController.getInstance().takePicture(generatePicturePath, ChatAttachAlert.this.cameraView.getCameraSession(), new -$$Lambda$ChatAttachAlert$10$spC8SeqMVNyiHnBN2HSRZFkemxQ(this, generatePicturePath, z));
                }
            }

            public /* synthetic */ void lambda$shutterReleased$3$ChatAttachAlert$10(File file, boolean z) {
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
                    PhotoEntry photoEntry = new PhotoEntry(0, ChatAttachAlert.access$6410(), 0, file.getAbsolutePath(), i, false);
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
        this.switchCameraButton.setOnClickListener(new -$$Lambda$ChatAttachAlert$ZqUzbu7OWYZNWU7ujZFdDlGIODQ(this));
        this.switchCameraButton.setContentDescription(LocaleController.getString("AccDescrSwitchCamera", NUM));
        for (int i5 = 0; i5 < 2; i5++) {
            this.flashModeButton[i5] = new ImageView(context2);
            this.flashModeButton[i5].setScaleType(ScaleType.CENTER);
            this.flashModeButton[i5].setVisibility(4);
            this.cameraPanel.addView(this.flashModeButton[i5], LayoutHelper.createFrame(48, 48, 51));
            this.flashModeButton[i5].setOnClickListener(new -$$Lambda$ChatAttachAlert$N7eKeWXf4VQLw62DCiXcluGuuq0(this));
            ImageView imageView = this.flashModeButton[i5];
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("flash mode ");
            stringBuilder.append(i5);
            imageView.setContentDescription(stringBuilder.toString());
        }
        this.cameraPhotoRecyclerView = new RecyclerListView(context2) {
            public void requestLayout() {
                if (!ChatAttachAlert.this.cameraPhotoRecyclerViewIgnoreLayout) {
                    super.requestLayout();
                }
            }
        };
        this.cameraPhotoRecyclerView.setVerticalScrollBarEnabled(true);
        RecyclerListView recyclerListView3 = this.cameraPhotoRecyclerView;
        PhotoAttachAdapter photoAttachAdapter2 = new PhotoAttachAdapter(context2, false);
        this.cameraAttachAdapter = photoAttachAdapter2;
        recyclerListView3.setAdapter(photoAttachAdapter2);
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
        this.cameraPhotoRecyclerView.setOnItemClickListener(-$$Lambda$ChatAttachAlert$GA9KqfMlT5-LL6qJw22nnzE_Xs8.INSTANCE);
    }

    public /* synthetic */ void lambda$new$0$ChatAttachAlert(View view, int i) {
        BaseFragment baseFragment = this.baseFragment;
        if (baseFragment != null && baseFragment.getParentActivity() != null) {
            if (this.deviceHasGoodCamera && i == 0) {
                openCamera(true);
                return;
            }
            if (this.deviceHasGoodCamera) {
                i--;
            }
            int i2 = i;
            ArrayList allPhotosArray = getAllPhotosArray();
            if (i2 >= 0 && i2 < allPhotosArray.size()) {
                ChatActivity chatActivity;
                int i3;
                PhotoViewer.getInstance().setParentActivity(this.baseFragment.getParentActivity());
                PhotoViewer.getInstance().setParentAlert(this);
                PhotoViewer.getInstance().setMaxSelectedPhotos(this.maxSelectedPhotos);
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

    public /* synthetic */ void lambda$new$1$ChatAttachAlert(BaseFragment baseFragment, View view) {
        if (!this.buttonPressed) {
            Integer num = (Integer) view.getTag();
            if (this.deviceHasGoodCamera && num.intValue() == 0 && (this.baseFragment instanceof ChatActivity) && ((ChatActivity) baseFragment).isSecretChat()) {
                openCamera(true);
            } else {
                this.buttonPressed = true;
                this.delegate.didPressedButton(num.intValue());
            }
        }
    }

    public /* synthetic */ void lambda$new$2$ChatAttachAlert(View view) {
        if (this.cameraView != null) {
            openPhotoViewer(null, false, false);
            CameraController.getInstance().stopPreview(this.cameraView.getCameraSession());
        }
    }

    public /* synthetic */ void lambda$new$3$ChatAttachAlert(View view) {
        if (!this.takingPhoto) {
            CameraView cameraView = this.cameraView;
            if (cameraView != null && cameraView.isInitied()) {
                this.cameraInitied = false;
                this.cameraView.switchCamera();
                ObjectAnimator duration = ObjectAnimator.ofFloat(this.switchCameraButton, "scaleX", new float[]{0.0f}).setDuration(100);
                duration.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        ImageView access$5300 = ChatAttachAlert.this.switchCameraButton;
                        int i = (ChatAttachAlert.this.cameraView == null || !ChatAttachAlert.this.cameraView.isFrontface()) ? NUM : NUM;
                        access$5300.setImageResource(i);
                        ObjectAnimator.ofFloat(ChatAttachAlert.this.switchCameraButton, "scaleX", new float[]{1.0f}).setDuration(100).start();
                    }
                });
                duration.start();
            }
        }
    }

    public /* synthetic */ void lambda$new$4$ChatAttachAlert(final View view) {
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
                    Animator[] animatorArr = new Animator[4];
                    String str = "translationY";
                    animatorArr[0] = ObjectAnimator.ofFloat(view, str, new float[]{0.0f, (float) AndroidUtilities.dp(48.0f)});
                    animatorArr[1] = ObjectAnimator.ofFloat(imageView, str, new float[]{(float) (-AndroidUtilities.dp(48.0f)), 0.0f});
                    String str2 = "alpha";
                    animatorArr[2] = ObjectAnimator.ofFloat(view, str2, new float[]{1.0f, 0.0f});
                    animatorArr[3] = ObjectAnimator.ofFloat(imageView, str2, new float[]{0.0f, 1.0f});
                    animatorSet.playTogether(animatorArr);
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

    static /* synthetic */ void lambda$new$5(View view, int i) {
        if (view instanceof PhotoAttachPhotoCell) {
            ((PhotoAttachPhotoCell) view).callDelegate();
        }
    }

    public void show() {
        super.show();
        this.buttonPressed = false;
    }

    /* JADX WARNING: Removed duplicated region for block: B:23:0x0045  */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x0042  */
    /* JADX WARNING: Missing block: B:15:0x0031, code skipped:
            if (r3.hasValidGroupId() != false) goto L_0x0034;
     */
    /* JADX WARNING: Missing block: B:19:0x003a, code skipped:
            if (r4.editingMessageObject == null) goto L_0x0036;
     */
    public void setEditingMessageObject(org.telegram.messenger.MessageObject r5) {
        /*
        r4 = this;
        r0 = r4.editingMessageObject;
        if (r0 != r5) goto L_0x0005;
    L_0x0004:
        return;
    L_0x0005:
        r4.editingMessageObject = r5;
        r5 = r4.editingMessageObject;
        r0 = 1;
        if (r5 == 0) goto L_0x000f;
    L_0x000c:
        r4.maxSelectedPhotos = r0;
        goto L_0x0012;
    L_0x000f:
        r5 = -1;
        r4.maxSelectedPhotos = r5;
    L_0x0012:
        r5 = r4.adapter;
        r5.notifyDataSetChanged();
        r5 = 0;
        r1 = 0;
    L_0x0019:
        r2 = 4;
        if (r1 >= r2) goto L_0x004e;
    L_0x001c:
        r2 = r4.attachButtons;
        r3 = r1 + 3;
        r2 = r2.get(r3);
        r2 = (org.telegram.ui.Components.ChatAttachAlert.AttachButton) r2;
        r3 = 2;
        if (r1 >= r3) goto L_0x0038;
    L_0x0029:
        r3 = r4.editingMessageObject;
        if (r3 == 0) goto L_0x0036;
    L_0x002d:
        r3 = r3.hasValidGroupId();
        if (r3 != 0) goto L_0x0034;
    L_0x0033:
        goto L_0x0036;
    L_0x0034:
        r3 = 0;
        goto L_0x003d;
    L_0x0036:
        r3 = 1;
        goto L_0x003d;
    L_0x0038:
        r3 = r4.editingMessageObject;
        if (r3 != 0) goto L_0x0034;
    L_0x003c:
        goto L_0x0036;
    L_0x003d:
        r2.setEnabled(r3);
        if (r3 == 0) goto L_0x0045;
    L_0x0042:
        r3 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        goto L_0x0048;
    L_0x0045:
        r3 = NUM; // 0x3e4ccccd float:0.2 double:5.164075695E-315;
    L_0x0048:
        r2.setAlpha(r3);
        r1 = r1 + 1;
        goto L_0x0019;
    L_0x004e:
        r4.updatePollMusicButton();
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlert.setEditingMessageObject(org.telegram.messenger.MessageObject):void");
    }

    public MessageObject getEditingMessageObject() {
        return this.editingMessageObject;
    }

    private void updatePollMusicButton() {
        if ((this.baseFragment instanceof ChatActivity) && !this.attachButtons.isEmpty()) {
            int i;
            String str;
            Object obj = null;
            if (this.editingMessageObject == null) {
                Chat currentChat = ((ChatActivity) this.baseFragment).getCurrentChat();
                if (currentChat != null && ChatObject.canSendPolls(currentChat)) {
                    obj = 1;
                }
            }
            if (obj != null) {
                i = NUM;
                str = "Poll";
            } else {
                i = NUM;
                str = "AttachMusic";
            }
            String string = LocaleController.getString(str, i);
            int i2 = 3;
            AttachButton attachButton = (AttachButton) this.attachButtons.get(3);
            attachButton.setTag(Integer.valueOf(obj != null ? 9 : 3));
            Drawable[] drawableArr = Theme.chat_attachButtonDrawables;
            if (obj != null) {
                i2 = 9;
            }
            attachButton.setTextAndIcon(string, drawableArr[i2]);
        }
    }

    private void updatePhotosCounter() {
        if (this.counterTextView != null) {
            Object obj = null;
            for (Entry value : selectedPhotos.entrySet()) {
                if (((PhotoEntry) value.getValue()).isVideo) {
                    obj = 1;
                    break;
                }
            }
            if (obj != null) {
                this.counterTextView.setText(LocaleController.formatPluralString("Media", selectedPhotos.size()).toUpperCase());
            } else {
                this.counterTextView.setText(LocaleController.formatPluralString("Photos", selectedPhotos.size()).toUpperCase());
            }
        }
    }

    private void openPhotoViewer(PhotoEntry photoEntry, final boolean z, boolean z2) {
        if (photoEntry != null) {
            cameraPhotos.add(photoEntry);
            selectedPhotos.put(Integer.valueOf(photoEntry.imageId), photoEntry);
            selectedPhotosOrder.add(Integer.valueOf(photoEntry.imageId));
            updatePhotosButton();
            this.photoAttachAdapter.notifyDataSetChanged();
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
            PhotoViewer.getInstance().setMaxSelectedPhotos(this.maxSelectedPhotos);
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
                        AndroidUtilities.runOnUIThread(new -$$Lambda$ChatAttachAlert$15$XovEDj9DHAwUNoLPMWuRb54fRhw(this), 1000);
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
                        ChatAttachAlert.this.photoAttachAdapter.notifyDataSetChanged();
                        ChatAttachAlert.this.cameraAttachAdapter.notifyDataSetChanged();
                        ChatAttachAlert.this.updatePhotosButton();
                    }
                    return true;
                }

                public /* synthetic */ void lambda$cancelButtonPressed$0$ChatAttachAlert$15() {
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
                    int i = System.getInt(ChatAttachAlert.this.baseFragment.getParentActivity().getContentResolver(), "accelerometer_rotation", 0);
                    if (z || i == 1) {
                        return true;
                    }
                    return false;
                }

                public void willHidePhotoViewer() {
                    int i = 0;
                    ChatAttachAlert.this.mediaCaptured = false;
                    int childCount = ChatAttachAlert.this.attachPhotoRecyclerView.getChildCount();
                    while (i < childCount) {
                        View childAt = ChatAttachAlert.this.attachPhotoRecyclerView.getChildAt(i);
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
        if ((this.pressed || motionEvent.getActionMasked() != 0) && motionEvent.getActionMasked() != 5) {
            if (this.pressed) {
                String str = "alpha";
                AnimatorSet animatorSet;
                Animator[] animatorArr;
                if (motionEvent.getActionMasked() == 2) {
                    float y = motionEvent.getY();
                    float f = y - this.lastY;
                    if (this.maybeStartDraging) {
                        if (Math.abs(f) > AndroidUtilities.getPixelsInCM(0.4f, false)) {
                            this.maybeStartDraging = false;
                            this.dragging = true;
                        }
                    } else if (this.dragging) {
                        CameraView cameraView = this.cameraView;
                        if (cameraView != null) {
                            cameraView.setTranslationY(cameraView.getTranslationY() + f);
                            this.lastY = y;
                            if (this.cameraPanel.getTag() == null) {
                                this.cameraPanel.setTag(Integer.valueOf(1));
                                animatorSet = new AnimatorSet();
                                animatorArr = new Animator[5];
                                animatorArr[0] = ObjectAnimator.ofFloat(this.cameraPanel, str, new float[]{0.0f});
                                animatorArr[1] = ObjectAnimator.ofFloat(this.counterTextView, str, new float[]{0.0f});
                                animatorArr[2] = ObjectAnimator.ofFloat(this.flashModeButton[0], str, new float[]{0.0f});
                                animatorArr[3] = ObjectAnimator.ofFloat(this.flashModeButton[1], str, new float[]{0.0f});
                                animatorArr[4] = ObjectAnimator.ofFloat(this.cameraPhotoRecyclerView, str, new float[]{0.0f});
                                animatorSet.playTogether(animatorArr);
                                animatorSet.setDuration(200);
                                animatorSet.start();
                            }
                        }
                    }
                } else if (motionEvent.getActionMasked() == 3 || motionEvent.getActionMasked() == 1 || motionEvent.getActionMasked() == 6) {
                    this.pressed = false;
                    if (this.dragging) {
                        this.dragging = false;
                        CameraView cameraView2 = this.cameraView;
                        if (cameraView2 != null) {
                            if (Math.abs(cameraView2.getTranslationY()) > ((float) this.cameraView.getMeasuredHeight()) / 6.0f) {
                                closeCamera(true);
                            } else {
                                animatorSet = new AnimatorSet();
                                animatorArr = new Animator[6];
                                animatorArr[0] = ObjectAnimator.ofFloat(this.cameraView, "translationY", new float[]{0.0f});
                                animatorArr[1] = ObjectAnimator.ofFloat(this.cameraPanel, str, new float[]{1.0f});
                                animatorArr[2] = ObjectAnimator.ofFloat(this.counterTextView, str, new float[]{1.0f});
                                animatorArr[3] = ObjectAnimator.ofFloat(this.flashModeButton[0], str, new float[]{1.0f});
                                animatorArr[4] = ObjectAnimator.ofFloat(this.flashModeButton[1], str, new float[]{1.0f});
                                animatorArr[5] = ObjectAnimator.ofFloat(this.cameraPhotoRecyclerView, str, new float[]{1.0f});
                                animatorSet.playTogether(animatorArr);
                                animatorSet.setDuration(250);
                                animatorSet.setInterpolator(this.interpolator);
                                animatorSet.start();
                                this.cameraPanel.setTag(null);
                            }
                        }
                    } else {
                        CameraView cameraView3 = this.cameraView;
                        if (cameraView3 != null) {
                            cameraView3.getLocationOnScreen(this.viewPosition);
                            this.cameraView.focusToPoint((int) (motionEvent.getRawX() - ((float) this.viewPosition[0])), (int) (motionEvent.getRawY() - ((float) this.viewPosition[1])));
                        }
                    }
                }
            }
        } else if (!this.takingPhoto) {
            this.pressed = true;
            this.maybeStartDraging = true;
            this.lastY = motionEvent.getY();
        }
        return true;
    }

    /* Access modifiers changed, original: protected */
    public boolean onContainerTouchEvent(MotionEvent motionEvent) {
        return this.cameraOpened && processTouchEvent(motionEvent);
    }

    public void checkColors() {
        ViewHolder findViewHolderForAdapterPosition;
        int size = this.attachButtons.size();
        for (int i = 0; i < size; i++) {
            ((AttachButton) this.attachButtons.get(i)).textView.setTextColor(Theme.getColor("dialogTextGray2"));
        }
        this.lineView.setBackgroundColor(Theme.getColor("dialogGrayLine"));
        TextView textView = this.hintTextView;
        if (textView != null) {
            Theme.setDrawableColor(textView.getBackground(), Theme.getColor("chat_gifSaveHintBackground"));
            this.hintTextView.setTextColor(Theme.getColor("chat_gifSaveHintText"));
        }
        CorrectlyMeasuringTextView correctlyMeasuringTextView = this.mediaBanTooltip;
        if (correctlyMeasuringTextView != null) {
            Theme.setDrawableColor(correctlyMeasuringTextView.getBackground(), Theme.getColor("chat_attachMediaBanBackground"));
            this.mediaBanTooltip.setTextColor(Theme.getColor("chat_attachMediaBanText"));
        }
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            recyclerListView.setGlowColor(Theme.getColor("dialogScrollGlow"));
            findViewHolderForAdapterPosition = this.listView.findViewHolderForAdapterPosition(1);
            if (findViewHolderForAdapterPosition != null) {
                findViewHolderForAdapterPosition.itemView.setBackgroundColor(Theme.getColor("dialogBackgroundGray"));
            } else {
                this.adapter.notifyItemChanged(1);
            }
        }
        Paint paint = this.ciclePaint;
        String str = "dialogBackground";
        if (paint != null) {
            paint.setColor(Theme.getColor(str));
        }
        Theme.setDrawableColor(this.shadowDrawable, Theme.getColor(str));
        ImageView imageView = this.cameraImageView;
        str = "dialogCameraIcon";
        if (imageView != null) {
            imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str), Mode.MULTIPLY));
        }
        recyclerListView = this.attachPhotoRecyclerView;
        if (recyclerListView != null) {
            findViewHolderForAdapterPosition = recyclerListView.findViewHolderForAdapterPosition(0);
            if (findViewHolderForAdapterPosition != null) {
                View view = findViewHolderForAdapterPosition.itemView;
                if (view instanceof PhotoAttachCameraCell) {
                    ((PhotoAttachCameraCell) view).getImageView().setColorFilter(new PorterDuffColorFilter(Theme.getColor(str), Mode.MULTIPLY));
                }
            }
        }
        this.containerView.invalidate();
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
        r6 = NUM; // 0x7var_d5 float:1.794501E38 double:1.0529356083E-314;
        r5.setImageResource(r6);
        r6 = NUM; // 0x7f0d0011 float:1.874215E38 double:1.053129786E-314;
        r0 = "AccDescrCameraFlashAuto";
        r6 = org.telegram.messenger.LocaleController.getString(r0, r6);
        r5.setContentDescription(r6);
        goto L_0x0073;
    L_0x004e:
        r6 = NUM; // 0x7var_d7 float:1.7945014E38 double:1.0529356093E-314;
        r5.setImageResource(r6);
        r6 = NUM; // 0x7f0d0013 float:1.8742153E38 double:1.053129787E-314;
        r0 = "AccDescrCameraFlashOn";
        r6 = org.telegram.messenger.LocaleController.getString(r0, r6);
        r5.setContentDescription(r6);
        goto L_0x0073;
    L_0x0061:
        r6 = NUM; // 0x7var_d6 float:1.7945012E38 double:1.052935609E-314;
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
        View view2 = this.cameraView;
        if (view != view2) {
            view2 = this.cameraPanel;
            if (view == view2) {
                if (obj != null) {
                    view2.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(100.0f), NUM));
                } else {
                    view2.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(100.0f), NUM), MeasureSpec.makeMeasureSpec(i2, NUM));
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
                    this.cameraPanel.layout(0, i4 - AndroidUtilities.dp(196.0f), i5, i4 - AndroidUtilities.dp(96.0f));
                } else {
                    this.cameraPanel.layout(0, i4 - AndroidUtilities.dp(100.0f), i5, i4);
                }
            } else if (this.cameraPhotoRecyclerView.getVisibility() == 0) {
                this.cameraPanel.layout(i3 - AndroidUtilities.dp(196.0f), 0, i3 - AndroidUtilities.dp(96.0f), i2);
            } else {
                this.cameraPanel.layout(i3 - AndroidUtilities.dp(100.0f), 0, i3, i2);
            }
            return true;
        }
        View view2 = this.counterTextView;
        if (view == view2) {
            if (obj != null) {
                i5 = (i5 - view2.getMeasuredWidth()) / 2;
                i4 -= AndroidUtilities.dp(154.0f);
                this.counterTextView.setRotation(0.0f);
                if (this.cameraPhotoRecyclerView.getVisibility() == 0) {
                    i4 -= AndroidUtilities.dp(96.0f);
                }
            } else {
                i5 = i3 - AndroidUtilities.dp(154.0f);
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

    private void hideHint() {
        Runnable runnable = this.hideHintRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.hideHintRunnable = null;
        }
        if (this.hintTextView != null) {
            this.currentHintAnimation = new AnimatorSet();
            AnimatorSet animatorSet = this.currentHintAnimation;
            Animator[] animatorArr = new Animator[1];
            animatorArr[0] = ObjectAnimator.ofFloat(this.hintTextView, "alpha", new float[]{0.0f});
            animatorSet.playTogether(animatorArr);
            this.currentHintAnimation.setInterpolator(this.decelerateInterpolator);
            this.currentHintAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (ChatAttachAlert.this.currentHintAnimation != null && ChatAttachAlert.this.currentHintAnimation.equals(animator)) {
                        ChatAttachAlert.this.currentHintAnimation = null;
                        if (ChatAttachAlert.this.hintTextView != null) {
                            ChatAttachAlert.this.hintTextView.setVisibility(4);
                        }
                    }
                }

                public void onAnimationCancel(Animator animator) {
                    if (ChatAttachAlert.this.currentHintAnimation != null && ChatAttachAlert.this.currentHintAnimation.equals(animator)) {
                        ChatAttachAlert.this.currentHintAnimation = null;
                    }
                }
            });
            this.currentHintAnimation.setDuration(300);
            this.currentHintAnimation.start();
        }
    }

    public void onPause() {
        ShutterButton shutterButton = this.shutterButton;
        if (shutterButton != null) {
            if (this.requestingPermissions) {
                if (this.cameraView != null && shutterButton.getState() == ShutterButton.State.RECORDING) {
                    this.shutterButton.setState(ShutterButton.State.DEFAULT, true);
                }
                this.requestingPermissions = false;
            } else {
                if (this.cameraView != null && shutterButton.getState() == ShutterButton.State.RECORDING) {
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

    private void openCamera(boolean z) {
        if (this.cameraView != null) {
            int i = 0;
            if (cameraPhotos.isEmpty()) {
                this.counterTextView.setVisibility(4);
                this.cameraPhotoRecyclerView.setVisibility(8);
            } else {
                this.counterTextView.setVisibility(0);
                this.cameraPhotoRecyclerView.setVisibility(0);
            }
            this.cameraPanel.setVisibility(0);
            this.cameraPanel.setTag(null);
            int[] iArr = this.animateCameraValues;
            iArr[0] = 0;
            iArr[1] = AndroidUtilities.dp(80.0f) - this.cameraViewOffsetX;
            this.animateCameraValues[2] = AndroidUtilities.dp(80.0f) - this.cameraViewOffsetY;
            if (z) {
                this.cameraAnimationInProgress = true;
                ArrayList arrayList = new ArrayList();
                arrayList.add(ObjectAnimator.ofFloat(this, "cameraOpenProgress", new float[]{0.0f, 1.0f}));
                arrayList.add(ObjectAnimator.ofFloat(this.cameraPanel, View.ALPHA, new float[]{1.0f}));
                arrayList.add(ObjectAnimator.ofFloat(this.counterTextView, View.ALPHA, new float[]{1.0f}));
                arrayList.add(ObjectAnimator.ofFloat(this.cameraPhotoRecyclerView, View.ALPHA, new float[]{1.0f}));
                for (int i2 = 0; i2 < 2; i2++) {
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
                        if (ChatAttachAlert.this.cameraOpened) {
                            ChatAttachAlert.this.delegate.onCameraOpened();
                        }
                    }
                });
                animatorSet.start();
            } else {
                setCameraOpenProgress(1.0f);
                this.cameraPanel.setAlpha(1.0f);
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
                this.attachPhotoRecyclerView.setImportantForAccessibility(4);
                Iterator it = this.attachButtons.iterator();
                while (it.hasNext()) {
                    ((AttachButton) it.next()).setImportantForAccessibility(4);
                }
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:82:0x014e A:{SYNTHETIC, Splitter:B:82:0x014e} */
    /* JADX WARNING: Removed duplicated region for block: B:91:0x01b1 A:{SYNTHETIC, Splitter:B:91:0x01b1} */
    /* JADX WARNING: Missing block: B:46:0x00ec, code skipped:
            if (new java.io.File(r0).exists() != false) goto L_0x00ef;
     */
    public void onActivityResultFragment(int r21, android.content.Intent r22, java.lang.String r23) {
        /*
        r20 = this;
        r1 = r20;
        r0 = r21;
        r7 = r23;
        r2 = r1.baseFragment;
        if (r2 == 0) goto L_0x01bb;
    L_0x000a:
        r2 = r2.getParentActivity();
        if (r2 != 0) goto L_0x0012;
    L_0x0010:
        goto L_0x01bb;
    L_0x0012:
        r10 = 1;
        mediaFromExternalCamera = r10;
        r11 = 0;
        if (r0 != 0) goto L_0x0072;
    L_0x0018:
        r0 = org.telegram.ui.PhotoViewer.getInstance();
        r2 = r1.baseFragment;
        r2 = r2.getParentActivity();
        r0.setParentActivity(r2);
        r0 = org.telegram.ui.PhotoViewer.getInstance();
        r2 = r1.maxSelectedPhotos;
        r0.setMaxSelectedPhotos(r2);
        r0 = new java.util.ArrayList;
        r0.<init>();
        r0 = new androidx.exifinterface.media.ExifInterface;	 Catch:{ Exception -> 0x0054 }
        r0.<init>(r7);	 Catch:{ Exception -> 0x0054 }
        r2 = "Orientation";
        r0 = r0.getAttributeInt(r2, r10);	 Catch:{ Exception -> 0x0054 }
        r2 = 3;
        if (r0 == r2) goto L_0x0050;
    L_0x0041:
        r2 = 6;
        if (r0 == r2) goto L_0x004d;
    L_0x0044:
        r2 = 8;
        if (r0 == r2) goto L_0x004a;
    L_0x0048:
        r0 = 0;
        goto L_0x0052;
    L_0x004a:
        r0 = 270; // 0x10e float:3.78E-43 double:1.334E-321;
        goto L_0x0052;
    L_0x004d:
        r0 = 90;
        goto L_0x0052;
    L_0x0050:
        r0 = 180; // 0xb4 float:2.52E-43 double:8.9E-322;
    L_0x0052:
        r8 = r0;
        goto L_0x0059;
    L_0x0054:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
        r8 = 0;
    L_0x0059:
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
        goto L_0x01bb;
    L_0x0072:
        r2 = 2;
        if (r0 != r2) goto L_0x01bb;
    L_0x0075:
        r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r0 == 0) goto L_0x008d;
    L_0x0079:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r2 = "pic path ";
        r0.append(r2);
        r0.append(r7);
        r0 = r0.toString();
        org.telegram.messenger.FileLog.d(r0);
    L_0x008d:
        r2 = 0;
        if (r22 == 0) goto L_0x009f;
    L_0x0090:
        if (r7 == 0) goto L_0x009f;
    L_0x0092:
        r0 = new java.io.File;
        r0.<init>(r7);
        r0 = r0.exists();
        if (r0 == 0) goto L_0x009f;
    L_0x009d:
        r0 = r2;
        goto L_0x00a1;
    L_0x009f:
        r0 = r22;
    L_0x00a1:
        if (r0 == 0) goto L_0x0102;
    L_0x00a3:
        r0 = r0.getData();
        if (r0 == 0) goto L_0x00ee;
    L_0x00a9:
        r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r3 == 0) goto L_0x00c5;
    L_0x00ad:
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = "video record uri ";
        r3.append(r4);
        r4 = r0.toString();
        r3.append(r4);
        r3 = r3.toString();
        org.telegram.messenger.FileLog.d(r3);
    L_0x00c5:
        r0 = org.telegram.messenger.AndroidUtilities.getPath(r0);
        r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r3 == 0) goto L_0x00e1;
    L_0x00cd:
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = "resolved path = ";
        r3.append(r4);
        r3.append(r0);
        r3 = r3.toString();
        org.telegram.messenger.FileLog.d(r3);
    L_0x00e1:
        if (r0 == 0) goto L_0x00ee;
    L_0x00e3:
        r3 = new java.io.File;
        r3.<init>(r0);
        r3 = r3.exists();
        if (r3 != 0) goto L_0x00ef;
    L_0x00ee:
        r0 = r7;
    L_0x00ef:
        r3 = r1.baseFragment;
        r4 = r3 instanceof org.telegram.ui.ChatActivity;
        if (r4 == 0) goto L_0x00fd;
    L_0x00f5:
        r3 = (org.telegram.ui.ChatActivity) r3;
        r3 = r3.isSecretChat();
        if (r3 != 0) goto L_0x0100;
    L_0x00fd:
        org.telegram.messenger.AndroidUtilities.addMediaToGallery(r23);
    L_0x0100:
        r7 = r2;
        goto L_0x0103;
    L_0x0102:
        r0 = r2;
    L_0x0103:
        if (r0 != 0) goto L_0x0113;
    L_0x0105:
        if (r7 == 0) goto L_0x0113;
    L_0x0107:
        r3 = new java.io.File;
        r3.<init>(r7);
        r3 = r3.exists();
        if (r3 == 0) goto L_0x0113;
    L_0x0112:
        goto L_0x0114;
    L_0x0113:
        r7 = r0;
    L_0x0114:
        r3 = 0;
        r5 = new android.media.MediaMetadataRetriever;	 Catch:{ Exception -> 0x0148 }
        r5.<init>();	 Catch:{ Exception -> 0x0148 }
        r5.setDataSource(r7);	 Catch:{ Exception -> 0x0141, all -> 0x013f }
        r0 = 9;
        r0 = r5.extractMetadata(r0);	 Catch:{ Exception -> 0x0141, all -> 0x013f }
        if (r0 == 0) goto L_0x0135;
    L_0x0126:
        r8 = java.lang.Long.parseLong(r0);	 Catch:{ Exception -> 0x0141, all -> 0x013f }
        r0 = (float) r8;	 Catch:{ Exception -> 0x0141, all -> 0x013f }
        r2 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        r0 = r0 / r2;
        r8 = (double) r0;	 Catch:{ Exception -> 0x0141, all -> 0x013f }
        r2 = java.lang.Math.ceil(r8);	 Catch:{ Exception -> 0x0141, all -> 0x013f }
        r0 = (int) r2;
        r3 = (long) r0;
    L_0x0135:
        r5.release();	 Catch:{ Exception -> 0x0139 }
        goto L_0x0151;
    L_0x0139:
        r0 = move-exception;
        r2 = r0;
        org.telegram.messenger.FileLog.e(r2);
        goto L_0x0151;
    L_0x013f:
        r0 = move-exception;
        goto L_0x0146;
    L_0x0141:
        r0 = move-exception;
        r2 = r5;
        goto L_0x0149;
    L_0x0144:
        r0 = move-exception;
        r5 = r2;
    L_0x0146:
        r2 = r0;
        goto L_0x01af;
    L_0x0148:
        r0 = move-exception;
    L_0x0149:
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ all -> 0x0144 }
        if (r2 == 0) goto L_0x0151;
    L_0x014e:
        r2.release();	 Catch:{ Exception -> 0x0139 }
    L_0x0151:
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
        r2 = new java.io.FileOutputStream;	 Catch:{ Throwable -> 0x0186 }
        r2.<init>(r5);	 Catch:{ Throwable -> 0x0186 }
        r6 = android.graphics.Bitmap.CompressFormat.JPEG;	 Catch:{ Throwable -> 0x0186 }
        r8 = 55;
        r0.compress(r6, r8, r2);	 Catch:{ Throwable -> 0x0186 }
        goto L_0x018a;
    L_0x0186:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x018a:
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
        goto L_0x01bb;
    L_0x01af:
        if (r5 == 0) goto L_0x01ba;
    L_0x01b1:
        r5.release();	 Catch:{ Exception -> 0x01b5 }
        goto L_0x01ba;
    L_0x01b5:
        r0 = move-exception;
        r3 = r0;
        org.telegram.messenger.FileLog.e(r3);
    L_0x01ba:
        throw r2;
    L_0x01bb:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlert.onActivityResultFragment(int, android.content.Intent, java.lang.String):void");
    }

    public void closeCamera(boolean z) {
        if (!this.takingPhoto && this.cameraView != null) {
            this.animateCameraValues[1] = AndroidUtilities.dp(80.0f) - this.cameraViewOffsetX;
            this.animateCameraValues[2] = AndroidUtilities.dp(80.0f) - this.cameraViewOffsetY;
            if (z) {
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.cameraView.getLayoutParams();
                int[] iArr = this.animateCameraValues;
                int translationY = (int) this.cameraView.getTranslationY();
                layoutParams.topMargin = translationY;
                iArr[0] = translationY;
                this.cameraView.setLayoutParams(layoutParams);
                this.cameraView.setTranslationY(0.0f);
                this.cameraAnimationInProgress = true;
                ArrayList arrayList = new ArrayList();
                arrayList.add(ObjectAnimator.ofFloat(this, "cameraOpenProgress", new float[]{0.0f}));
                String str = "alpha";
                arrayList.add(ObjectAnimator.ofFloat(this.cameraPanel, str, new float[]{0.0f}));
                arrayList.add(ObjectAnimator.ofFloat(this.counterTextView, str, new float[]{0.0f}));
                arrayList.add(ObjectAnimator.ofFloat(this.cameraPhotoRecyclerView, str, new float[]{0.0f}));
                for (int i = 0; i < 2; i++) {
                    if (this.flashModeButton[i].getVisibility() == 0) {
                        arrayList.add(ObjectAnimator.ofFloat(this.flashModeButton[i], str, new float[]{0.0f}));
                        break;
                    }
                }
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(arrayList);
                animatorSet.setDuration(200);
                animatorSet.addListener(new AnimatorListenerAdapter() {
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
                });
                animatorSet.start();
            } else {
                this.animateCameraValues[0] = 0;
                setCameraOpenProgress(0.0f);
                this.cameraPanel.setAlpha(0.0f);
                this.cameraPhotoRecyclerView.setAlpha(0.0f);
                this.counterTextView.setAlpha(0.0f);
                this.cameraPanel.setVisibility(8);
                this.cameraPhotoRecyclerView.setVisibility(8);
                for (int i2 = 0; i2 < 2; i2++) {
                    if (this.flashModeButton[i2].getVisibility() == 0) {
                        this.flashModeButton[i2].setAlpha(0.0f);
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
                this.attachPhotoRecyclerView.setImportantForAccessibility(0);
                Iterator it = this.attachButtons.iterator();
                while (it.hasNext()) {
                    ((AttachButton) it.next()).setImportantForAccessibility(0);
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
            layoutParams.width = (int) (f2 + ((width - f2) * f));
            layoutParams.height = (int) (f3 + ((f4 - f3) * f));
            if (f != 0.0f) {
                f4 = 1.0f - f;
                this.cameraView.setClipLeft((int) (((float) this.cameraViewOffsetX) * f4));
                this.cameraView.setClipTop((int) (((float) this.cameraViewOffsetY) * f4));
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
        }
    }

    @Keep
    public float getCameraOpenProgress() {
        return this.cameraOpenProgress;
    }

    private void checkCameraViewPosition() {
        if (this.deviceHasGoodCamera) {
            int childCount = this.attachPhotoRecyclerView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = this.attachPhotoRecyclerView.getChildAt(i);
                if (childAt instanceof PhotoAttachCameraCell) {
                    if (VERSION.SDK_INT < 19 || childAt.isAttachedToWindow()) {
                        childAt.getLocationInWindow(this.cameraViewLocation);
                        int[] iArr = this.cameraViewLocation;
                        iArr[0] = iArr[0] - getLeftInset();
                        float x = (this.listView.getX() + ((float) this.backgroundPaddingLeft)) - ((float) getLeftInset());
                        int[] iArr2 = this.cameraViewLocation;
                        if (((float) iArr2[0]) < x) {
                            this.cameraViewOffsetX = (int) (x - ((float) iArr2[0]));
                            if (this.cameraViewOffsetX >= AndroidUtilities.dp(80.0f)) {
                                this.cameraViewOffsetX = 0;
                                this.cameraViewLocation[0] = AndroidUtilities.dp(-150.0f);
                                this.cameraViewLocation[1] = 0;
                            } else {
                                iArr = this.cameraViewLocation;
                                iArr[0] = iArr[0] + this.cameraViewOffsetX;
                            }
                        } else {
                            this.cameraViewOffsetX = 0;
                        }
                        if (VERSION.SDK_INT >= 21) {
                            iArr = this.cameraViewLocation;
                            i = iArr[1];
                            int i2 = AndroidUtilities.statusBarHeight;
                            if (i < i2) {
                                this.cameraViewOffsetY = i2 - iArr[1];
                                if (this.cameraViewOffsetY >= AndroidUtilities.dp(80.0f)) {
                                    this.cameraViewOffsetY = 0;
                                    this.cameraViewLocation[0] = AndroidUtilities.dp(-150.0f);
                                    this.cameraViewLocation[1] = 0;
                                } else {
                                    iArr = this.cameraViewLocation;
                                    iArr[1] = iArr[1] + this.cameraViewOffsetY;
                                }
                                applyCameraViewPosition();
                                return;
                            }
                        }
                        this.cameraViewOffsetY = 0;
                        applyCameraViewPosition();
                        return;
                    }
                    this.cameraViewOffsetX = 0;
                    this.cameraViewOffsetY = 0;
                    this.cameraViewLocation[0] = AndroidUtilities.dp(-150.0f);
                    this.cameraViewLocation[1] = 0;
                    applyCameraViewPosition();
                }
            }
            this.cameraViewOffsetX = 0;
            this.cameraViewOffsetY = 0;
            this.cameraViewLocation[0] = AndroidUtilities.dp(-150.0f);
            this.cameraViewLocation[1] = 0;
            applyCameraViewPosition();
        }
    }

    private void applyCameraViewPosition() {
        CameraView cameraView = this.cameraView;
        if (cameraView != null) {
            FrameLayout.LayoutParams layoutParams;
            if (!this.cameraOpened) {
                cameraView.setTranslationX((float) this.cameraViewLocation[0]);
                this.cameraView.setTranslationY((float) this.cameraViewLocation[1]);
            }
            this.cameraIcon.setTranslationX((float) this.cameraViewLocation[0]);
            this.cameraIcon.setTranslationY((float) this.cameraViewLocation[1]);
            int dp = AndroidUtilities.dp(80.0f) - this.cameraViewOffsetX;
            int dp2 = AndroidUtilities.dp(80.0f) - this.cameraViewOffsetY;
            if (!this.cameraOpened) {
                this.cameraView.setClipLeft(this.cameraViewOffsetX);
                this.cameraView.setClipTop(this.cameraViewOffsetY);
                layoutParams = (FrameLayout.LayoutParams) this.cameraView.getLayoutParams();
                if (!(layoutParams.height == dp2 && layoutParams.width == dp)) {
                    layoutParams.width = dp;
                    layoutParams.height = dp2;
                    this.cameraView.setLayoutParams(layoutParams);
                    AndroidUtilities.runOnUIThread(new -$$Lambda$ChatAttachAlert$9zWNRomA9shsKBOcxwcrmIWHw2o(this, layoutParams));
                }
            }
            layoutParams = (FrameLayout.LayoutParams) this.cameraIcon.getLayoutParams();
            if (layoutParams.height != dp2 || layoutParams.width != dp) {
                layoutParams.width = dp;
                layoutParams.height = dp2;
                this.cameraIcon.setLayoutParams(layoutParams);
                AndroidUtilities.runOnUIThread(new -$$Lambda$ChatAttachAlert$7zXlVZ2DjXYZ5PDcPaHgdzTaY_Q(this, layoutParams));
            }
        }
    }

    public /* synthetic */ void lambda$applyCameraViewPosition$6$ChatAttachAlert(FrameLayout.LayoutParams layoutParams) {
        CameraView cameraView = this.cameraView;
        if (cameraView != null) {
            cameraView.setLayoutParams(layoutParams);
        }
    }

    public /* synthetic */ void lambda$applyCameraViewPosition$7$ChatAttachAlert(FrameLayout.LayoutParams layoutParams) {
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
                this.cameraView.setContentDescription(LocaleController.getString("AccDescrInstantCamera", NUM));
                this.container.addView(this.cameraView, 1, LayoutHelper.createFrame(80, 80.0f));
                this.cameraView.setDelegate(new CameraViewDelegate() {
                    public void onCameraCreated(Camera camera) {
                    }

                    public void onCameraInit() {
                        int childCount = ChatAttachAlert.this.attachPhotoRecyclerView.getChildCount();
                        int i = 0;
                        for (int i2 = 0; i2 < childCount; i2++) {
                            View childAt = ChatAttachAlert.this.attachPhotoRecyclerView.getChildAt(i2);
                            if (childAt instanceof PhotoAttachCameraCell) {
                                childAt.setVisibility(4);
                                break;
                            }
                        }
                        if (ChatAttachAlert.this.cameraView.getCameraSession().getCurrentFlashMode().equals(ChatAttachAlert.this.cameraView.getCameraSession().getNextFlashMode())) {
                            for (childCount = 0; childCount < 2; childCount++) {
                                ChatAttachAlert.this.flashModeButton[childCount].setVisibility(4);
                                ChatAttachAlert.this.flashModeButton[childCount].setAlpha(0.0f);
                                ChatAttachAlert.this.flashModeButton[childCount].setTranslationY(0.0f);
                            }
                        } else {
                            ChatAttachAlert chatAttachAlert = ChatAttachAlert.this;
                            chatAttachAlert.setCameraFlashModeIcon(chatAttachAlert.flashModeButton[0], ChatAttachAlert.this.cameraView.getCameraSession().getCurrentFlashMode());
                            childCount = 0;
                            while (childCount < 2) {
                                ChatAttachAlert.this.flashModeButton[childCount].setVisibility(childCount == 0 ? 0 : 4);
                                ImageView imageView = ChatAttachAlert.this.flashModeButton[childCount];
                                float f = (childCount == 0 && ChatAttachAlert.this.cameraOpened) ? 1.0f : 0.0f;
                                imageView.setAlpha(f);
                                ChatAttachAlert.this.flashModeButton[childCount].setTranslationY(0.0f);
                                childCount++;
                            }
                        }
                        ChatAttachAlert.this.switchCameraButton.setImageResource(ChatAttachAlert.this.cameraView.isFrontface() ? NUM : NUM);
                        ImageView access$5300 = ChatAttachAlert.this.switchCameraButton;
                        if (!ChatAttachAlert.this.cameraView.hasFrontFaceCamera()) {
                            i = 4;
                        }
                        access$5300.setVisibility(i);
                    }
                });
                if (this.cameraIcon == null) {
                    this.cameraIcon = new FrameLayout(this.baseFragment.getParentActivity());
                    this.cameraImageView = new ImageView(this.baseFragment.getParentActivity());
                    this.cameraImageView.setScaleType(ScaleType.CENTER);
                    this.cameraImageView.setImageResource(NUM);
                    this.cameraImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogCameraIcon"), Mode.MULTIPLY));
                    this.cameraIcon.addView(this.cameraImageView, LayoutHelper.createFrame(80, 80, 85));
                }
                this.container.addView(this.cameraIcon, 2, LayoutHelper.createFrame(80, 80.0f));
                float f = 1.0f;
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

    public void hideCamera(boolean z) {
        if (this.deviceHasGoodCamera) {
            CameraView cameraView = this.cameraView;
            if (cameraView != null) {
                cameraView.destroy(z, null);
                this.container.removeView(this.cameraView);
                this.container.removeView(this.cameraIcon);
                this.cameraView = null;
                this.cameraIcon = null;
                int childCount = this.attachPhotoRecyclerView.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View childAt = this.attachPhotoRecyclerView.getChildAt(i);
                    if (childAt instanceof PhotoAttachCameraCell) {
                        childAt.setVisibility(0);
                        return;
                    }
                }
            }
        }
    }

    private void showHint() {
        if (this.editingMessageObject == null && (this.baseFragment instanceof ChatActivity) && !DataQuery.getInstance(this.currentAccount).inlineBots.isEmpty() && !MessagesController.getGlobalMainSettings().getBoolean("bothint", false)) {
            this.hintShowed = true;
            this.hintTextView.setVisibility(0);
            this.currentHintAnimation = new AnimatorSet();
            AnimatorSet animatorSet = this.currentHintAnimation;
            Animator[] animatorArr = new Animator[1];
            animatorArr[0] = ObjectAnimator.ofFloat(this.hintTextView, "alpha", new float[]{0.0f, 1.0f});
            animatorSet.playTogether(animatorArr);
            this.currentHintAnimation.setInterpolator(this.decelerateInterpolator);
            this.currentHintAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (ChatAttachAlert.this.currentHintAnimation != null && ChatAttachAlert.this.currentHintAnimation.equals(animator)) {
                        ChatAttachAlert.this.currentHintAnimation = null;
                        ChatAttachAlert chatAttachAlert = ChatAttachAlert.this;
                        AnonymousClass1 anonymousClass1 = new Runnable() {
                            public void run() {
                                if (ChatAttachAlert.this.hideHintRunnable == this) {
                                    ChatAttachAlert.this.hideHintRunnable = null;
                                    ChatAttachAlert.this.hideHint();
                                }
                            }
                        };
                        chatAttachAlert.hideHintRunnable = anonymousClass1;
                        AndroidUtilities.runOnUIThread(anonymousClass1, 2000);
                    }
                }

                public void onAnimationCancel(Animator animator) {
                    if (ChatAttachAlert.this.currentHintAnimation != null && ChatAttachAlert.this.currentHintAnimation.equals(animator)) {
                        ChatAttachAlert.this.currentHintAnimation = null;
                    }
                }
            });
            this.currentHintAnimation.setDuration(300);
            this.currentHintAnimation.start();
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        int i3 = 0;
        if (i == NotificationCenter.albumsDidLoad) {
            if (this.photoAttachAdapter != null) {
                this.loading = false;
                this.progressView.showTextView();
                this.photoAttachAdapter.notifyDataSetChanged();
                this.cameraAttachAdapter.notifyDataSetChanged();
                if (!selectedPhotosOrder.isEmpty()) {
                    AlbumEntry albumEntry;
                    if (this.baseFragment instanceof ChatActivity) {
                        albumEntry = MediaController.allMediaAlbumEntry;
                    } else {
                        albumEntry = MediaController.allPhotosAlbumEntry;
                    }
                    if (albumEntry != null) {
                        i2 = selectedPhotosOrder.size();
                        while (i3 < i2) {
                            int intValue = ((Integer) selectedPhotosOrder.get(i3)).intValue();
                            PhotoEntry photoEntry = (PhotoEntry) albumEntry.photosByIds.get(intValue);
                            if (photoEntry != null) {
                                selectedPhotos.put(Integer.valueOf(intValue), photoEntry);
                            }
                            i3++;
                        }
                    }
                }
            }
        } else if (i == NotificationCenter.reloadInlineHints) {
            ListAdapter listAdapter = this.adapter;
            if (listAdapter != null) {
                listAdapter.notifyDataSetChanged();
            }
        } else if (i == NotificationCenter.cameraInitied) {
            checkCamera(false);
        }
    }

    @SuppressLint({"NewApi"})
    private void updateLayout() {
        if (this.listView.getChildCount() <= 0) {
            RecyclerListView recyclerListView = this.listView;
            int paddingTop = recyclerListView.getPaddingTop();
            this.scrollOffsetY = paddingTop;
            recyclerListView.setTopGlowOffset(paddingTop);
            this.listView.invalidate();
            return;
        }
        View childAt = this.listView.getChildAt(0);
        Holder holder = (Holder) this.listView.findContainingViewHolder(childAt);
        int top = childAt.getTop();
        if (top < 0 || holder == null || holder.getAdapterPosition() != 0) {
            top = 0;
        }
        if (this.scrollOffsetY != top) {
            RecyclerListView recyclerListView2 = this.listView;
            this.scrollOffsetY = top;
            recyclerListView2.setTopGlowOffset(top);
            this.listView.invalidate();
        }
    }

    public void updatePhotosButton() {
        int size = selectedPhotos.size();
        if (size == 0) {
            this.sendPhotosButton.imageView.setBackgroundDrawable(Theme.chat_attachButtonDrawables[7]);
            this.sendPhotosButton.textView.setText("");
            this.sendPhotosButton.textView.setContentDescription(LocaleController.getString("Close", NUM));
            if (this.baseFragment instanceof ChatActivity) {
                this.sendDocumentsButton.textView.setText(LocaleController.getString("ChatDocument", NUM));
            }
        } else {
            this.sendPhotosButton.imageView.setBackgroundDrawable(Theme.chat_attachButtonDrawables[8]);
            this.sendPhotosButton.textView.setContentDescription(null);
            String str = "(%d)";
            TextView access$7400;
            Object[] objArr;
            if (this.baseFragment instanceof ChatActivity) {
                access$7400 = this.sendPhotosButton.textView;
                objArr = new Object[1];
                objArr[0] = String.format(str, new Object[]{Integer.valueOf(size)});
                access$7400.setText(LocaleController.formatString("SendItems", NUM, objArr));
                MessageObject messageObject = this.editingMessageObject;
                if (messageObject == null || !messageObject.hasValidGroupId()) {
                    access$7400 = this.sendDocumentsButton.textView;
                    if (size == 1) {
                        size = NUM;
                        str = "SendAsFile";
                    } else {
                        size = NUM;
                        str = "SendAsFiles";
                    }
                    access$7400.setText(LocaleController.getString(str, size));
                }
            } else {
                access$7400 = this.sendPhotosButton.textView;
                objArr = new Object[1];
                objArr[0] = String.format(str, new Object[]{Integer.valueOf(size)});
                access$7400.setText(LocaleController.formatString("UploadItems", NUM, objArr));
            }
        }
        if (VERSION.SDK_INT < 23 || getContext().checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == 0) {
            this.progressView.setText(LocaleController.getString("NoPhotos", NUM));
            this.progressView.setTextSize(20);
            return;
        }
        this.progressView.setText(LocaleController.getString("PermissionStorage", NUM));
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
            for (int i = 0; i < Math.min(100, albumEntry.photos.size()); i++) {
                ((PhotoEntry) albumEntry.photos.get(i)).reset();
            }
        }
        AnimatorSet animatorSet = this.currentHintAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
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

    private PhotoAttachPhotoCell getCellForIndex(int i) {
        int childCount = this.attachPhotoRecyclerView.getChildCount();
        for (int i2 = 0; i2 < childCount; i2++) {
            View childAt = this.attachPhotoRecyclerView.getChildAt(i2);
            if (childAt instanceof PhotoAttachPhotoCell) {
                PhotoAttachPhotoCell photoAttachPhotoCell = (PhotoAttachPhotoCell) childAt;
                if (((Integer) photoAttachPhotoCell.getImageView().getTag()).intValue() == i) {
                    return photoAttachPhotoCell;
                }
            }
        }
        return null;
    }

    private void onRevealAnimationEnd(boolean z) {
        NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(false);
        this.revealAnimationInProgress = false;
        AlbumEntry albumEntry;
        if (this.baseFragment instanceof ChatActivity) {
            albumEntry = MediaController.allMediaAlbumEntry;
        } else {
            albumEntry = MediaController.allPhotosAlbumEntry;
        }
        if (z && VERSION.SDK_INT <= 19 && albumEntry == null) {
            MediaController.loadGalleryPhotosAlbums(0);
        }
        if (z) {
            checkCamera(true);
            showHint();
            AndroidUtilities.makeAccessibilityAnnouncement(LocaleController.getString("AccDescrAttachButton", NUM));
        }
    }

    public void checkCamera(boolean z) {
        BaseFragment baseFragment = this.baseFragment;
        if (baseFragment != null) {
            boolean z2 = this.deviceHasGoodCamera;
            if (!SharedConfig.inappCamera) {
                this.deviceHasGoodCamera = false;
            } else if (VERSION.SDK_INT >= 23) {
                if (baseFragment.getParentActivity().checkSelfPermission("android.permission.CAMERA") != 0) {
                    if (z) {
                        try {
                            this.baseFragment.getParentActivity().requestPermissions(new String[]{r2}, 17);
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
            if (z2 != this.deviceHasGoodCamera) {
                PhotoAttachAdapter photoAttachAdapter = this.photoAttachAdapter;
                if (photoAttachAdapter != null) {
                    photoAttachAdapter.notifyDataSetChanged();
                }
            }
            if (!(!isShowing() || !this.deviceHasGoodCamera || this.baseFragment == null || this.backDrawable.getAlpha() == 0 || this.revealAnimationInProgress || this.cameraOpened)) {
                showCamera();
            }
        }
    }

    public void onOpenAnimationEnd() {
        onRevealAnimationEnd(true);
    }

    public void setAllowDrawContent(boolean z) {
        super.setAllowDrawContent(z);
        checkCameraViewPosition();
    }

    public void setMaxSelectedPhotos(int i) {
        this.maxSelectedPhotos = i;
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
        Object obj;
        if (selectedPhotos.isEmpty()) {
            obj = null;
        } else {
            for (Entry value : selectedPhotos.entrySet()) {
                ((PhotoEntry) value.getValue()).reset();
            }
            selectedPhotos.clear();
            selectedPhotosOrder.clear();
            updatePhotosButton();
            obj = 1;
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
            obj = 1;
        }
        if (obj != null) {
            this.photoAttachAdapter.notifyDataSetChanged();
            this.cameraAttachAdapter.notifyDataSetChanged();
        }
    }

    private void setUseRevealAnimation(boolean z) {
        if (!z || (z && VERSION.SDK_INT >= 18 && !AndroidUtilities.isTablet() && AndroidUtilities.shouldEnableAnimation() && (this.baseFragment instanceof ChatActivity))) {
            this.useRevealAnimation = z;
        }
    }

    /* Access modifiers changed, original: protected */
    @SuppressLint({"NewApi"})
    @Keep
    public void setRevealRadius(float f) {
        this.revealRadius = f;
        if (VERSION.SDK_INT <= 19) {
            this.listView.invalidate();
        }
        if (!isDismissed()) {
            int i = 0;
            while (i < this.innerAnimators.size()) {
                InnerAnimator innerAnimator = (InnerAnimator) this.innerAnimators.get(i);
                if (innerAnimator.startRadius <= f) {
                    innerAnimator.animatorSet.start();
                    this.innerAnimators.remove(i);
                    i--;
                }
                i++;
            }
        }
    }

    /* Access modifiers changed, original: protected */
    @Keep
    public float getRevealRadius() {
        return this.revealRadius;
    }

    @SuppressLint({"NewApi"})
    private void startRevealAnimation(boolean z) {
        int i;
        final boolean z2 = z;
        float f = 0.0f;
        this.containerView.setTranslationY(0.0f);
        final AnimatorSet animatorSet = new AnimatorSet();
        View revealView = this.delegate.getRevealView();
        if (revealView.getVisibility() == 0 && ((ViewGroup) revealView.getParent()).getVisibility() == 0) {
            float measuredHeight;
            int[] iArr = new int[2];
            revealView.getLocationInWindow(iArr);
            if (VERSION.SDK_INT <= 19) {
                measuredHeight = (float) ((AndroidUtilities.displaySize.y - this.containerView.getMeasuredHeight()) - AndroidUtilities.statusBarHeight);
            } else {
                measuredHeight = this.containerView.getY();
            }
            this.revealX = iArr[0] + (revealView.getMeasuredWidth() / 2);
            this.revealY = (int) (((float) (iArr[1] + (revealView.getMeasuredHeight() / 2))) - measuredHeight);
            if (VERSION.SDK_INT <= 19) {
                this.revealY -= AndroidUtilities.statusBarHeight;
            }
        } else {
            Point point = AndroidUtilities.displaySize;
            this.revealX = (point.x / 2) + this.backgroundPaddingLeft;
            this.revealY = (int) (((float) point.y) - this.containerView.getY());
        }
        r5 = new int[4][];
        r5[1] = new int[]{0, AndroidUtilities.dp(304.0f)};
        r5[2] = new int[]{this.containerView.getMeasuredWidth(), 0};
        r5[3] = new int[]{this.containerView.getMeasuredWidth(), AndroidUtilities.dp(304.0f)};
        int i2 = (this.revealY - this.scrollOffsetY) + this.backgroundPaddingTop;
        int i3 = 0;
        for (int i4 = 0; i4 < 4; i4++) {
            i = this.revealX;
            i3 = Math.max(i3, (int) Math.ceil(Math.sqrt((double) (((i - r5[i4][0]) * (i - r5[i4][0])) + ((i2 - r5[i4][1]) * (i2 - r5[i4][1]))))));
        }
        int measuredWidth = this.revealX <= this.containerView.getMeasuredWidth() ? this.revealX : this.containerView.getMeasuredWidth();
        ArrayList arrayList = new ArrayList(3);
        float[] fArr = new float[2];
        fArr[0] = z2 ? 0.0f : (float) i3;
        fArr[1] = z2 ? (float) i3 : 0.0f;
        arrayList.add(ObjectAnimator.ofFloat(this, "revealRadius", fArr));
        ColorDrawable colorDrawable = this.backDrawable;
        int[] iArr2 = new int[1];
        iArr2[0] = z2 ? 51 : 0;
        arrayList.add(ObjectAnimator.ofInt(colorDrawable, "alpha", iArr2));
        if (VERSION.SDK_INT >= 21) {
            try {
                arrayList.add(ViewAnimationUtils.createCircularReveal(this.containerView, measuredWidth, this.revealY, z2 ? 0.0f : (float) i3, z2 ? (float) i3 : 0.0f));
            } catch (Exception e) {
                FileLog.e(e);
            }
            animatorSet.setDuration(320);
        } else if (z2) {
            animatorSet.setDuration(250);
            this.containerView.setScaleX(1.0f);
            this.containerView.setScaleY(1.0f);
            this.containerView.setAlpha(1.0f);
            if (VERSION.SDK_INT <= 19) {
                animatorSet.setStartDelay(20);
            }
        } else {
            animatorSet.setDuration(200);
            ViewGroup viewGroup = this.containerView;
            viewGroup.setPivotX((float) (this.revealX <= viewGroup.getMeasuredWidth() ? this.revealX : this.containerView.getMeasuredWidth()));
            this.containerView.setPivotY((float) this.revealY);
            arrayList.add(ObjectAnimator.ofFloat(this.containerView, View.SCALE_X, new float[]{0.0f}));
            arrayList.add(ObjectAnimator.ofFloat(this.containerView, View.SCALE_Y, new float[]{0.0f}));
            arrayList.add(ObjectAnimator.ofFloat(this.containerView, View.ALPHA, new float[]{0.0f}));
        }
        animatorSet.playTogether(arrayList);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                if (ChatAttachAlert.this.currentSheetAnimation != null && ChatAttachAlert.this.currentSheetAnimation.equals(animator)) {
                    ChatAttachAlert.this.currentSheetAnimation = null;
                    ChatAttachAlert.this.onRevealAnimationEnd(z2);
                    ChatAttachAlert.this.containerView.invalidate();
                    ChatAttachAlert.this.containerView.setLayerType(0, null);
                    if (!z2) {
                        try {
                            ChatAttachAlert.this.dismissInternal();
                        } catch (Exception e) {
                            FileLog.e(e);
                        }
                    }
                }
            }

            public void onAnimationCancel(Animator animator) {
                if (ChatAttachAlert.this.currentSheetAnimation != null && animatorSet.equals(animator)) {
                    ChatAttachAlert.this.currentSheetAnimation = null;
                }
            }
        });
        if (z2) {
            this.innerAnimators.clear();
            NotificationCenter.getInstance(this.currentAccount).setAllowedNotificationsDutingAnimation(new int[]{NotificationCenter.dialogsNeedReload});
            NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(true);
            this.revealAnimationInProgress = true;
            measuredWidth = VERSION.SDK_INT <= 19 ? 12 : 8;
            int i5 = 0;
            while (i5 < measuredWidth) {
                AnimatorSet animatorSet2;
                View[] viewArr = this.views;
                if (viewArr[i5] == null) {
                    animatorSet2 = animatorSet;
                } else {
                    AnimatorSet animatorSet3;
                    if (VERSION.SDK_INT <= 19) {
                        if (i5 < 8) {
                            viewArr[i5].setScaleX(0.1f);
                            this.views[i5].setScaleY(0.1f);
                        }
                        this.views[i5].setAlpha(f);
                    } else {
                        viewArr[i5].setScaleX(0.7f);
                        this.views[i5].setScaleY(0.7f);
                    }
                    InnerAnimator innerAnimator = new InnerAnimator(this, null);
                    i3 = this.views[i5].getLeft() + (this.views[i5].getMeasuredWidth() / 2);
                    i = (this.views[i5].getTop() + this.attachView.getTop()) + (this.views[i5].getMeasuredHeight() / 2);
                    int i6 = this.revealX;
                    int i7 = (i6 - i3) * (i6 - i3);
                    i6 = this.revealY;
                    animatorSet2 = animatorSet;
                    f = (float) Math.sqrt((double) (i7 + ((i6 - i) * (i6 - i))));
                    float f2 = ((float) (this.revealX - i3)) / f;
                    float f3 = ((float) (this.revealY - i)) / f;
                    View[] viewArr2 = this.views;
                    viewArr2[i5].setPivotX(((float) (viewArr2[i5].getMeasuredWidth() / 2)) + (f2 * ((float) AndroidUtilities.dp(20.0f))));
                    View[] viewArr3 = this.views;
                    viewArr3[i5].setPivotY(((float) (viewArr3[i5].getMeasuredHeight() / 2)) + (f3 * ((float) AndroidUtilities.dp(20.0f))));
                    innerAnimator.startRadius = f - ((float) AndroidUtilities.dp(81.0f));
                    this.views[i5].setTag(NUM, Integer.valueOf(1));
                    ArrayList arrayList2 = new ArrayList();
                    if (i5 < 8) {
                        arrayList2.add(ObjectAnimator.ofFloat(this.views[i5], View.SCALE_X, new float[]{0.7f, 1.05f}));
                        arrayList2.add(ObjectAnimator.ofFloat(this.views[i5], View.SCALE_Y, new float[]{0.7f, 1.05f}));
                        animatorSet3 = new AnimatorSet();
                        Animator[] animatorArr = new Animator[2];
                        animatorArr[0] = ObjectAnimator.ofFloat(this.views[i5], View.SCALE_X, new float[]{1.0f});
                        animatorArr[1] = ObjectAnimator.ofFloat(this.views[i5], View.SCALE_Y, new float[]{1.0f});
                        animatorSet3.playTogether(animatorArr);
                        animatorSet3.setDuration(100);
                        animatorSet3.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                    } else {
                        animatorSet3 = null;
                    }
                    if (VERSION.SDK_INT <= 19) {
                        arrayList2.add(ObjectAnimator.ofFloat(this.views[i5], View.ALPHA, new float[]{1.0f}));
                    }
                    innerAnimator.animatorSet = new AnimatorSet();
                    innerAnimator.animatorSet.playTogether(arrayList2);
                    innerAnimator.animatorSet.setDuration(150);
                    innerAnimator.animatorSet.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                    innerAnimator.animatorSet.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            AnimatorSet animatorSet = animatorSet3;
                            if (animatorSet != null) {
                                animatorSet.start();
                            }
                        }
                    });
                    this.innerAnimators.add(innerAnimator);
                }
                i5++;
                animatorSet = animatorSet2;
                f = 0.0f;
            }
        }
        AnimatorSet animatorSet4 = animatorSet;
        this.currentSheetAnimation = animatorSet4;
        animatorSet4.start();
    }

    public void dismissInternal() {
        ViewGroup viewGroup = this.containerView;
        if (viewGroup != null) {
            viewGroup.setVisibility(4);
        }
        super.dismissInternal();
    }

    /* Access modifiers changed, original: protected */
    public boolean onCustomOpenAnimation() {
        if (this.useRevealAnimation) {
            setUseRevealAnimation(true);
        }
        if (this.baseFragment instanceof ChatActivity) {
            updatePollMusicButton();
            Chat currentChat = ((ChatActivity) this.baseFragment).getCurrentChat();
            if (currentChat != null) {
                float f;
                this.mediaEnabled = ChatObject.canSendMedia(currentChat);
                int i = 0;
                while (true) {
                    f = 1.0f;
                    if (i >= 5) {
                        break;
                    }
                    if (i > 2) {
                        MessageObject messageObject = this.editingMessageObject;
                        if (messageObject != null && messageObject.hasValidGroupId()) {
                            int i2 = i + 3;
                            ((AttachButton) this.attachButtons.get(i2)).setEnabled(false);
                            ((AttachButton) this.attachButtons.get(i2)).setAlpha(0.2f);
                            i++;
                        }
                    }
                    AttachButton attachButton = (AttachButton) this.attachButtons.get(i);
                    Integer num = (Integer) attachButton.getTag();
                    if (!(this.mediaEnabled || num.intValue() == 9)) {
                        f = 0.2f;
                    }
                    attachButton.setAlpha(f);
                    boolean z = this.mediaEnabled || num.intValue() == 9;
                    attachButton.setEnabled(z);
                    i++;
                }
                this.attachPhotoRecyclerView.setAlpha(this.mediaEnabled ? 1.0f : 0.2f);
                this.attachPhotoRecyclerView.setEnabled(this.mediaEnabled);
                if (!this.mediaEnabled) {
                    if (ChatObject.isActionBannedByDefault(currentChat, 7)) {
                        this.mediaBanTooltip.setText(LocaleController.getString("GlobalAttachMediaRestricted", NUM));
                    } else if (AndroidUtilities.isBannedForever(currentChat.banned_rights)) {
                        this.mediaBanTooltip.setText(LocaleController.formatString("AttachMediaRestrictedForever", NUM, new Object[0]));
                    } else {
                        this.mediaBanTooltip.setText(LocaleController.formatString("AttachMediaRestricted", NUM, LocaleController.formatDateForBan((long) currentChat.banned_rights.until_date)));
                    }
                }
                this.mediaBanTooltip.setVisibility(this.mediaEnabled ? 4 : 0);
                CameraView cameraView = this.cameraView;
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
            }
        }
        if (!this.useRevealAnimation) {
            return false;
        }
        startRevealAnimation(true);
        return true;
    }

    /* Access modifiers changed, original: protected */
    public boolean onCustomCloseAnimation() {
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
