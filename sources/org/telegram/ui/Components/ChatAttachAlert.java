package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
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
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DataQuery;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver.BitmapHolder;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaController.PhotoEntry;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.messenger.camera.CameraController;
import org.telegram.messenger.camera.CameraController.VideoTakeCallback;
import org.telegram.messenger.camera.CameraView;
import org.telegram.messenger.camera.CameraView.CameraViewDelegate;
import org.telegram.messenger.exoplayer2.trackselection.AdaptiveTrackSelection;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.ItemDecoration;
import org.telegram.messenger.support.widget.RecyclerView.LayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.State;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.TL_topPeer;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.BottomSheet.BottomSheetDelegateInterface;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.PhotoAttachCameraCell;
import org.telegram.ui.Cells.PhotoAttachPhotoCell;
import org.telegram.ui.Cells.PhotoAttachPhotoCell.PhotoAttachPhotoCellDelegate;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
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
    private int[] animateCameraValues = new int[5];
    private ArrayList<AttachButton> attachButtons = new ArrayList();
    private LinearLayoutManager attachPhotoLayoutManager;
    private RecyclerListView attachPhotoRecyclerView;
    private ViewGroup attachView;
    private ChatActivity baseFragment;
    private boolean buttonPressed;
    private boolean cameraAnimationInProgress;
    private PhotoAttachAdapter cameraAttachAdapter;
    private FrameLayout cameraIcon;
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
    private boolean maybeStartDraging;
    private CorrectlyMeasuringTextView mediaBanTooltip;
    private boolean mediaCaptured;
    private boolean mediaEnabled = true;
    private boolean paused;
    private PhotoAttachAdapter photoAttachAdapter;
    private PhotoViewerProvider photoViewerProvider = new C23541();
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

        /* renamed from: org.telegram.ui.Components.ChatAttachAlert$AttachBotButton$1 */
        class C11121 implements OnClickListener {
            C11121() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                DataQuery.getInstance(ChatAttachAlert.this.currentAccount).removeInline(AttachBotButton.this.currentUser.id);
            }
        }

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

            public void run() {
                if (AttachBotButton.this.pendingCheckForLongPress == null) {
                    AttachBotButton.this.pendingCheckForLongPress = new CheckForLongPress();
                }
                AttachBotButton.this.pendingCheckForLongPress.currentPressCount = AttachBotButton.access$1204(AttachBotButton.this);
                AttachBotButton.this.postDelayed(AttachBotButton.this.pendingCheckForLongPress, (long) (ViewConfiguration.getLongPressTimeout() - ViewConfiguration.getTapTimeout()));
            }
        }

        static /* synthetic */ int access$1204(AttachBotButton attachBotButton) {
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
            this.nameTextView.setTextColor(Theme.getColor(Theme.key_dialogTextGray2));
            this.nameTextView.setTextSize(1, 12.0f);
            this.nameTextView.setMaxLines(2);
            this.nameTextView.setGravity(49);
            this.nameTextView.setLines(2);
            this.nameTextView.setEllipsize(TruncateAt.END);
            addView(this.nameTextView, LayoutHelper.createFrame(-1, -2.0f, 51, 6.0f, 65.0f, 6.0f, 0.0f));
        }

        protected void onMeasure(int i, int i2) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(NUM), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(100.0f), NUM));
        }

        private void onLongPress() {
            if (ChatAttachAlert.this.baseFragment != null) {
                if (this.currentUser != null) {
                    Builder builder = new Builder(getContext());
                    builder.setTitle(LocaleController.getString("AppName", C0446R.string.AppName));
                    builder.setMessage(LocaleController.formatString("ChatHintsDelete", C0446R.string.ChatHintsDelete, ContactsController.formatName(this.currentUser.first_name, this.currentUser.last_name)));
                    builder.setPositiveButton(LocaleController.getString("OK", C0446R.string.OK), new C11121());
                    builder.setNegativeButton(LocaleController.getString("Cancel", C0446R.string.Cancel), null);
                    builder.show();
                }
            }
        }

        public void setUser(User user) {
            if (user != null) {
                this.currentUser = user;
                TLObject tLObject = null;
                this.nameTextView.setText(ContactsController.formatName(user.first_name, user.last_name));
                this.avatarDrawable.setInfo(user);
                if (!(user == null || user.photo == null)) {
                    tLObject = user.photo.photo_small;
                }
                this.imageView.setImage(tLObject, "50_50", this.avatarDrawable);
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

        protected void startCheckLongPress() {
            if (!this.checkingForLongPress) {
                this.checkingForLongPress = true;
                if (this.pendingCheckForTap == null) {
                    this.pendingCheckForTap = new CheckForTap();
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
            addView(this.imageView, LayoutHelper.createFrame(64, 64, 49));
            this.textView = new TextView(context);
            this.textView.setLines(1);
            this.textView.setSingleLine(true);
            this.textView.setGravity(1);
            this.textView.setEllipsize(TruncateAt.END);
            this.textView.setTextColor(Theme.getColor(Theme.key_dialogTextGray2));
            this.textView.setTextSize(1, 12.0f);
            addView(this.textView, LayoutHelper.createFrame(-1, -2.0f, 51, 0.0f, 64.0f, 0.0f, 0.0f));
        }

        protected void onMeasure(int i, int i2) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(NUM), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(90.0f), NUM));
        }

        public void setTextAndIcon(CharSequence charSequence, Drawable drawable) {
            this.textView.setText(charSequence);
            this.imageView.setBackgroundDrawable(drawable);
        }
    }

    public interface ChatAttachViewDelegate {
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
    }

    /* renamed from: org.telegram.ui.Components.ChatAttachAlert$3 */
    class C20463 extends ItemDecoration {
        C20463() {
        }

        public void getItemOffsets(Rect rect, View view, RecyclerView recyclerView, State state) {
            rect.left = 0;
            rect.right = 0;
            rect.top = 0;
            rect.bottom = 0;
        }
    }

    /* renamed from: org.telegram.ui.Components.ChatAttachAlert$4 */
    class C20474 extends OnScrollListener {
        C20474() {
        }

        public void onScrolled(RecyclerView recyclerView, int i, int i2) {
            if (ChatAttachAlert.this.listView.getChildCount() > null) {
                if (ChatAttachAlert.this.hintShowed != null && ChatAttachAlert.this.layoutManager.findLastVisibleItemPosition() > 1) {
                    ChatAttachAlert.this.hideHint();
                    ChatAttachAlert.this.hintShowed = 0;
                    MessagesController.getGlobalMainSettings().edit().putBoolean("bothint", true).commit();
                }
                ChatAttachAlert.this.updateLayout();
                ChatAttachAlert.this.checkCameraViewPosition();
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.ChatAttachAlert$7 */
    class C20487 implements OnItemClickListener {
        C20487() {
        }

        public void onItemClick(View view, int i) {
            if (ChatAttachAlert.this.baseFragment != null) {
                if (ChatAttachAlert.this.baseFragment.getParentActivity() != null) {
                    if (ChatAttachAlert.this.deviceHasGoodCamera != null) {
                        if (i == 0) {
                            ChatAttachAlert.this.openCamera(1);
                        }
                    }
                    if (ChatAttachAlert.this.deviceHasGoodCamera != null) {
                        i--;
                    }
                    int i2 = i;
                    ArrayList access$5200 = ChatAttachAlert.this.getAllPhotosArray();
                    if (i2 >= 0) {
                        if (i2 < access$5200.size()) {
                            PhotoViewer.getInstance().setParentActivity(ChatAttachAlert.this.baseFragment.getParentActivity());
                            PhotoViewer.getInstance().setParentAlert(ChatAttachAlert.this);
                            PhotoViewer.getInstance().openPhotoForSelect(access$5200, i2, 0, ChatAttachAlert.this.photoViewerProvider, ChatAttachAlert.this.baseFragment);
                            AndroidUtilities.hideKeyboard(ChatAttachAlert.this.baseFragment.getFragmentView().findFocus());
                        }
                    }
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.ChatAttachAlert$8 */
    class C20498 extends OnScrollListener {
        C20498() {
        }

        public void onScrolled(RecyclerView recyclerView, int i, int i2) {
            ChatAttachAlert.this.checkCameraViewPosition();
        }
    }

    private class BasePhotoProvider extends EmptyPhotoViewerProvider {
        private BasePhotoProvider() {
        }

        public boolean isPhotoChecked(int i) {
            i = ChatAttachAlert.this.getPhotoEntryAtPosition(i);
            return (i == 0 || ChatAttachAlert.selectedPhotos.containsKey(Integer.valueOf(i.imageId)) == 0) ? false : true;
        }

        public int setPhotoChecked(int i, VideoEditedInfo videoEditedInfo) {
            PhotoEntry access$000 = ChatAttachAlert.this.getPhotoEntryAtPosition(i);
            if (access$000 == null) {
                return -1;
            }
            boolean z;
            int i2;
            int access$200 = ChatAttachAlert.this.addToSelectedPhotos(access$000, -1);
            if (access$200 == -1) {
                access$200 = ChatAttachAlert.selectedPhotosOrder.indexOf(Integer.valueOf(access$000.imageId));
                z = true;
            } else {
                access$000.editedInfo = null;
                z = false;
            }
            access$000.editedInfo = videoEditedInfo;
            videoEditedInfo = ChatAttachAlert.this.attachPhotoRecyclerView.getChildCount();
            for (i2 = 0; i2 < videoEditedInfo; i2++) {
                View childAt = ChatAttachAlert.this.attachPhotoRecyclerView.getChildAt(i2);
                if ((childAt instanceof PhotoAttachPhotoCell) && ((Integer) childAt.getTag()).intValue() == i) {
                    ((PhotoAttachPhotoCell) childAt).setChecked(access$200, z, false);
                    break;
                }
            }
            videoEditedInfo = ChatAttachAlert.this.cameraPhotoRecyclerView.getChildCount();
            for (i2 = 0; i2 < videoEditedInfo; i2++) {
                childAt = ChatAttachAlert.this.cameraPhotoRecyclerView.getChildAt(i2);
                if ((childAt instanceof PhotoAttachPhotoCell) && ((Integer) childAt.getTag()).intValue() == i) {
                    ((PhotoAttachPhotoCell) childAt).setChecked(access$200, z, false);
                    break;
                }
            }
            ChatAttachAlert.this.updatePhotosButton();
            return access$200;
        }

        public boolean allowGroupPhotos() {
            return ChatAttachAlert.this.baseFragment != null && ChatAttachAlert.this.baseFragment.allowGroupPhotos();
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
            i = ChatAttachAlert.this.getPhotoEntryAtPosition(i);
            if (i == 0) {
                return -1;
            }
            return ChatAttachAlert.selectedPhotosOrder.indexOf(Integer.valueOf(i.imageId));
        }
    }

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public int getItemViewType(int i) {
            switch (i) {
                case 0:
                    return 0;
                case 1:
                    return 1;
                default:
                    return 2;
            }
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            return false;
        }

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            switch (i) {
                case 0:
                    i = ChatAttachAlert.this.attachView;
                    break;
                case 1:
                    i = new FrameLayout(this.mContext);
                    i.setBackgroundColor(-986896);
                    i.addView(new ShadowSectionCell(this.mContext), LayoutHelper.createFrame(-1, -1.0f));
                    break;
                default:
                    i = new FrameLayout(this.mContext) {
                        protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
                            i3 = ((i3 - i) - AndroidUtilities.dp(true)) / 3;
                            for (i = 0; i < 4; i++) {
                                i2 = AndroidUtilities.dp(NUM) + ((i % 4) * (AndroidUtilities.dp(85.0f) + i3));
                                i4 = getChildAt(i);
                                i4.layout(i2, 0, i4.getMeasuredWidth() + i2, i4.getMeasuredHeight());
                            }
                        }
                    };
                    for (int i2 = 0; i2 < 4; i2++) {
                        i.addView(new AttachBotButton(this.mContext));
                    }
                    i.setLayoutParams(new LayoutParams(-1, AndroidUtilities.dp(100.0f)));
                    break;
            }
            return new Holder(i);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            if (i > 1) {
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
            return 1 + (!DataQuery.getInstance(ChatAttachAlert.this.currentAccount).inlineBots.isEmpty() ? ((int) Math.ceil((double) (((float) DataQuery.getInstance(ChatAttachAlert.this.currentAccount).inlineBots.size()) / 4.0f))) + 1 : 0);
        }
    }

    private class PhotoAttachAdapter extends SelectionAdapter {
        private Context mContext;
        private boolean needCamera;
        private ArrayList<Holder> viewsCache = new ArrayList(8);

        /* renamed from: org.telegram.ui.Components.ChatAttachAlert$PhotoAttachAdapter$1 */
        class C20501 implements PhotoAttachPhotoCellDelegate {
            C20501() {
            }

            public void onCheckClick(PhotoAttachPhotoCell photoAttachPhotoCell) {
                if (ChatAttachAlert.this.mediaEnabled) {
                    int intValue = ((Integer) photoAttachPhotoCell.getTag()).intValue();
                    PhotoEntry photoEntry = photoAttachPhotoCell.getPhotoEntry();
                    boolean containsKey = ChatAttachAlert.selectedPhotos.containsKey(Integer.valueOf(photoEntry.imageId)) ^ true;
                    photoAttachPhotoCell.setChecked(containsKey ? ChatAttachAlert.selectedPhotosOrder.size() : -1, containsKey, true);
                    ChatAttachAlert.this.addToSelectedPhotos(photoEntry, intValue);
                    if (PhotoAttachAdapter.this == ChatAttachAlert.this.cameraAttachAdapter) {
                        if (!(ChatAttachAlert.this.photoAttachAdapter.needCamera == null || ChatAttachAlert.this.deviceHasGoodCamera == null)) {
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

        public boolean isEnabled(ViewHolder viewHolder) {
            return false;
        }

        public PhotoAttachAdapter(Context context, boolean z) {
            this.mContext = context;
            this.needCamera = z;
            for (ChatAttachAlert chatAttachAlert = null; chatAttachAlert < 8; chatAttachAlert++) {
                this.viewsCache.add(createHolder());
            }
        }

        public Holder createHolder() {
            View photoAttachPhotoCell = new PhotoAttachPhotoCell(this.mContext);
            photoAttachPhotoCell.setDelegate(new C20501());
            return new Holder(photoAttachPhotoCell);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            boolean z = false;
            if (this.needCamera && ChatAttachAlert.this.deviceHasGoodCamera) {
                if (i == 0) {
                    if (!this.needCamera || !ChatAttachAlert.this.deviceHasGoodCamera || i != 0) {
                        return;
                    }
                    if (ChatAttachAlert.this.cameraView == 0 || ChatAttachAlert.this.cameraView.isInitied() == 0) {
                        viewHolder.itemView.setVisibility(0);
                        return;
                    } else {
                        viewHolder.itemView.setVisibility(4);
                        return;
                    }
                }
            }
            if (this.needCamera && ChatAttachAlert.this.deviceHasGoodCamera) {
                i--;
            }
            PhotoAttachPhotoCell photoAttachPhotoCell = (PhotoAttachPhotoCell) viewHolder.itemView;
            PhotoEntry access$000 = ChatAttachAlert.this.getPhotoEntryAtPosition(i);
            photoAttachPhotoCell.setPhotoEntry(access$000, this.needCamera, i == getItemCount() - 1);
            photoAttachPhotoCell.setChecked(ChatAttachAlert.selectedPhotosOrder.indexOf(Integer.valueOf(access$000.imageId)), ChatAttachAlert.selectedPhotos.containsKey(Integer.valueOf(access$000.imageId)), false);
            photoAttachPhotoCell.getImageView().setTag(Integer.valueOf(i));
            photoAttachPhotoCell.setTag(Integer.valueOf(i));
            if (this == ChatAttachAlert.this.cameraAttachAdapter && ChatAttachAlert.this.cameraPhotoLayoutManager.getOrientation() == 1) {
                z = true;
            }
            photoAttachPhotoCell.setIsVertical(z);
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            if (i == 1) {
                return new Holder(new PhotoAttachCameraCell(this.mContext));
            }
            if (this.viewsCache.isEmpty() != null) {
                return createHolder();
            }
            Holder holder = (Holder) this.viewsCache.get(0);
            this.viewsCache.remove(0);
            return holder;
        }

        public int getItemCount() {
            int i = (this.needCamera && ChatAttachAlert.this.deviceHasGoodCamera) ? 1 : 0;
            i += ChatAttachAlert.cameraPhotos.size();
            return MediaController.allMediaAlbumEntry != null ? i + MediaController.allMediaAlbumEntry.photos.size() : i;
        }

        public int getItemViewType(int i) {
            return (this.needCamera && ChatAttachAlert.this.deviceHasGoodCamera && i == 0) ? 1 : 0;
        }
    }

    /* renamed from: org.telegram.ui.Components.ChatAttachAlert$1 */
    class C23541 extends BasePhotoProvider {
        public boolean cancelButtonPressed() {
            return false;
        }

        C23541() {
            super();
        }

        public PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, FileLocation fileLocation, int i) {
            messageObject = ChatAttachAlert.this.getCellForIndex(i);
            if (messageObject == null) {
                return null;
            }
            fileLocation = new int[2];
            messageObject.getImageView().getLocationInWindow(fileLocation);
            fileLocation[0] = fileLocation[0] - ChatAttachAlert.this.getLeftInset();
            PlaceProviderObject placeProviderObject = new PlaceProviderObject();
            placeProviderObject.viewX = fileLocation[0];
            placeProviderObject.viewY = fileLocation[1];
            placeProviderObject.parentView = ChatAttachAlert.this.attachPhotoRecyclerView;
            placeProviderObject.imageReceiver = messageObject.getImageView().getImageReceiver();
            placeProviderObject.thumb = placeProviderObject.imageReceiver.getBitmapSafe();
            placeProviderObject.scale = messageObject.getImageView().getScaleX();
            messageObject.showCheck(false);
            return placeProviderObject;
        }

        public void updatePhotoAtIndex(int i) {
            PhotoAttachPhotoCell access$800 = ChatAttachAlert.this.getCellForIndex(i);
            if (access$800 != null) {
                access$800.getImageView().setOrientation(0, true);
                i = ChatAttachAlert.this.getPhotoEntryAtPosition(i);
                if (i != 0) {
                    if (i.thumbPath != null) {
                        access$800.getImageView().setImage(i.thumbPath, null, access$800.getContext().getResources().getDrawable(C0446R.drawable.nophotos));
                    } else if (i.path != null) {
                        access$800.getImageView().setOrientation(i.orientation, true);
                        BackupImageView imageView;
                        StringBuilder stringBuilder;
                        if (i.isVideo) {
                            imageView = access$800.getImageView();
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("vthumb://");
                            stringBuilder.append(i.imageId);
                            stringBuilder.append(":");
                            stringBuilder.append(i.path);
                            imageView.setImage(stringBuilder.toString(), null, access$800.getContext().getResources().getDrawable(C0446R.drawable.nophotos));
                        } else {
                            imageView = access$800.getImageView();
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("thumb://");
                            stringBuilder.append(i.imageId);
                            stringBuilder.append(":");
                            stringBuilder.append(i.path);
                            imageView.setImage(stringBuilder.toString(), null, access$800.getContext().getResources().getDrawable(C0446R.drawable.nophotos));
                        }
                    } else {
                        access$800.getImageView().setImageResource(C0446R.drawable.nophotos);
                    }
                }
            }
        }

        public BitmapHolder getThumbForPhoto(MessageObject messageObject, FileLocation fileLocation, int i) {
            messageObject = ChatAttachAlert.this.getCellForIndex(i);
            return messageObject != null ? messageObject.getImageView().getImageReceiver().getBitmapSafe() : null;
        }

        public void willSwitchFromPhoto(MessageObject messageObject, FileLocation fileLocation, int i) {
            messageObject = ChatAttachAlert.this.getCellForIndex(i);
            if (messageObject != null) {
                messageObject.showCheck(true);
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
            i = ChatAttachAlert.this.getPhotoEntryAtPosition(i);
            if (i != 0) {
                i.editedInfo = videoEditedInfo;
            }
            if (!(ChatAttachAlert.selectedPhotos.isEmpty() == null || i == 0)) {
                ChatAttachAlert.this.addToSelectedPhotos(i, -1);
            }
            ChatAttachAlert.this.delegate.didPressedButton(7);
        }
    }

    public boolean canDismiss() {
        return true;
    }

    protected boolean canDismissWithSwipe() {
        return false;
    }

    public void onOpenAnimationStart() {
    }

    static /* synthetic */ int access$7010() {
        int i = lastImageId;
        lastImageId = i - 1;
        return i;
    }

    private void updateCheckedPhotoIndices() {
        int childCount = this.attachPhotoRecyclerView.getChildCount();
        int i = 0;
        for (int i2 = 0; i2 < childCount; i2++) {
            View childAt = this.attachPhotoRecyclerView.getChildAt(i2);
            if (childAt instanceof PhotoAttachPhotoCell) {
                PhotoAttachPhotoCell photoAttachPhotoCell = (PhotoAttachPhotoCell) childAt;
                PhotoEntry photoEntryAtPosition = getPhotoEntryAtPosition(((Integer) photoAttachPhotoCell.getTag()).intValue());
                if (photoEntryAtPosition != null) {
                    photoAttachPhotoCell.setNum(selectedPhotosOrder.indexOf(Integer.valueOf(photoEntryAtPosition.imageId)));
                }
            }
        }
        childCount = this.cameraPhotoRecyclerView.getChildCount();
        while (i < childCount) {
            View childAt2 = this.cameraPhotoRecyclerView.getChildAt(i);
            if (childAt2 instanceof PhotoAttachPhotoCell) {
                PhotoAttachPhotoCell photoAttachPhotoCell2 = (PhotoAttachPhotoCell) childAt2;
                PhotoEntry photoEntryAtPosition2 = getPhotoEntryAtPosition(((Integer) photoAttachPhotoCell2.getTag()).intValue());
                if (photoEntryAtPosition2 != null) {
                    photoAttachPhotoCell2.setNum(selectedPhotosOrder.indexOf(Integer.valueOf(photoEntryAtPosition2.imageId)));
                }
            }
            i++;
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
        if (i < MediaController.allMediaAlbumEntry.photos.size()) {
            return (PhotoEntry) MediaController.allMediaAlbumEntry.photos.get(i);
        }
        return null;
    }

    private ArrayList<Object> getAllPhotosArray() {
        if (MediaController.allMediaAlbumEntry != null) {
            if (cameraPhotos.isEmpty()) {
                return MediaController.allMediaAlbumEntry.photos;
            }
            ArrayList<Object> arrayList = new ArrayList(MediaController.allMediaAlbumEntry.photos.size() + cameraPhotos.size());
            arrayList.addAll(cameraPhotos);
            arrayList.addAll(MediaController.allMediaAlbumEntry.photos);
            return arrayList;
        } else if (cameraPhotos.isEmpty()) {
            return new ArrayList(0);
        } else {
            return cameraPhotos;
        }
    }

    public ChatAttachAlert(Context context, ChatActivity chatActivity) {
        Context context2 = context;
        final ChatActivity chatActivity2 = chatActivity;
        super(context2, false);
        this.baseFragment = chatActivity2;
        this.ciclePaint.setColor(Theme.getColor(Theme.key_dialogBackground));
        setDelegate(this);
        setUseRevealAnimation(true);
        checkCamera(false);
        if (this.deviceHasGoodCamera) {
            CameraController.getInstance().initCamera();
        }
        NotificationCenter.getInstance(r0.currentAccount).addObserver(r0, NotificationCenter.albumsDidLoaded);
        NotificationCenter.getInstance(r0.currentAccount).addObserver(r0, NotificationCenter.reloadInlineHints);
        NotificationCenter.getGlobalInstance().addObserver(r0, NotificationCenter.cameraInitied);
        r0.shadowDrawable = context.getResources().getDrawable(C0446R.drawable.sheet_shadow).mutate();
        ViewGroup c23622 = new RecyclerListView(context2) {
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
                if (ChatAttachAlert.this.isDismissed() || super.onTouchEvent(motionEvent) == null) {
                    z = false;
                }
                return z;
            }

            protected void onMeasure(int i, int i2) {
                i2 = MeasureSpec.getSize(i2);
                if (VERSION.SDK_INT >= 21) {
                    i2 -= AndroidUtilities.statusBarHeight;
                }
                int access$2400 = (ChatAttachAlert.backgroundPaddingTop + AndroidUtilities.dp(294.0f)) + (DataQuery.getInstance(ChatAttachAlert.this.currentAccount).inlineBots.isEmpty() ? 0 : (((int) Math.ceil((double) (((float) DataQuery.getInstance(ChatAttachAlert.this.currentAccount).inlineBots.size()) / 4.0f))) * AndroidUtilities.dp(100.0f)) + AndroidUtilities.dp(12.0f));
                int max = access$2400 == AndroidUtilities.dp(294.0f) ? 0 : Math.max(0, i2 - AndroidUtilities.dp(294.0f));
                if (max != 0 && access$2400 < i2) {
                    max -= i2 - access$2400;
                }
                if (max == 0) {
                    max = ChatAttachAlert.backgroundPaddingTop;
                }
                if (getPaddingTop() != max) {
                    ChatAttachAlert.this.ignoreLayout = true;
                    setPadding(ChatAttachAlert.backgroundPaddingLeft, max, ChatAttachAlert.backgroundPaddingLeft, 0);
                    ChatAttachAlert.this.ignoreLayout = false;
                }
                super.onMeasure(i, MeasureSpec.makeMeasureSpec(Math.min(access$2400, i2), NUM));
            }

            protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
                int adapterPosition;
                int top;
                int i5 = i3 - i;
                int i6 = i4 - i2;
                if (ChatAttachAlert.this.listView.getChildCount() > 0) {
                    View childAt = ChatAttachAlert.this.listView.getChildAt(ChatAttachAlert.this.listView.getChildCount() - 1);
                    Holder holder = (Holder) ChatAttachAlert.this.listView.findContainingViewHolder(childAt);
                    if (holder != null) {
                        adapterPosition = holder.getAdapterPosition();
                        top = childAt.getTop();
                        if (adapterPosition >= 0 || i6 - r6.lastHeight == 0) {
                            top = 0;
                            adapterPosition = -1;
                        } else {
                            top = ((top + i6) - r6.lastHeight) - getPaddingTop();
                        }
                        super.onLayout(z, i, i2, i3, i4);
                        if (adapterPosition != -1) {
                            ChatAttachAlert.this.ignoreLayout = true;
                            ChatAttachAlert.this.layoutManager.scrollToPositionWithOffset(adapterPosition, top);
                            super.onLayout(false, i, i2, i3, i4);
                            ChatAttachAlert.this.ignoreLayout = false;
                        }
                        r6.lastHeight = i6;
                        r6.lastWidth = i5;
                        ChatAttachAlert.this.updateLayout();
                        ChatAttachAlert.this.checkCameraViewPosition();
                    }
                }
                top = 0;
                adapterPosition = -1;
                if (adapterPosition >= 0) {
                }
                top = 0;
                adapterPosition = -1;
                super.onLayout(z, i, i2, i3, i4);
                if (adapterPosition != -1) {
                    ChatAttachAlert.this.ignoreLayout = true;
                    ChatAttachAlert.this.layoutManager.scrollToPositionWithOffset(adapterPosition, top);
                    super.onLayout(false, i, i2, i3, i4);
                    ChatAttachAlert.this.ignoreLayout = false;
                }
                r6.lastHeight = i6;
                r6.lastWidth = i5;
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

            public void setTranslationY(float f) {
                super.setTranslationY(f);
                ChatAttachAlert.this.checkCameraViewPosition();
            }
        };
        r0.listView = c23622;
        r0.containerView = c23622;
        r0.nestedScrollChild = r0.listView;
        r0.listView.setWillNotDraw(false);
        r0.listView.setClipToPadding(false);
        RecyclerListView recyclerListView = r0.listView;
        LayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        r0.layoutManager = linearLayoutManager;
        recyclerListView.setLayoutManager(linearLayoutManager);
        r0.layoutManager.setOrientation(1);
        recyclerListView = r0.listView;
        Adapter listAdapter = new ListAdapter(context2);
        r0.adapter = listAdapter;
        recyclerListView.setAdapter(listAdapter);
        r0.listView.setVerticalScrollBarEnabled(false);
        r0.listView.setEnabled(true);
        r0.listView.setGlowColor(Theme.getColor(Theme.key_dialogScrollGlow));
        r0.listView.addItemDecoration(new C20463());
        r0.listView.setOnScrollListener(new C20474());
        r0.containerView.setPadding(backgroundPaddingLeft, 0, backgroundPaddingLeft, 0);
        r0.attachView = new FrameLayout(context2) {
            protected void onMeasure(int i, int i2) {
                super.onMeasure(i, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(NUM), NUM));
            }

            protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
                i3 -= i;
                i4 -= i2;
                z = AndroidUtilities.dp(true);
                boolean z2 = false;
                ChatAttachAlert.this.attachPhotoRecyclerView.layout(0, z, i3, ChatAttachAlert.this.attachPhotoRecyclerView.getMeasuredHeight() + z);
                ChatAttachAlert.this.progressView.layout(0, z, i3, ChatAttachAlert.this.progressView.getMeasuredHeight() + z);
                ChatAttachAlert.this.lineView.layout(0, AndroidUtilities.dp(96.0f), i3, AndroidUtilities.dp(96.0f) + ChatAttachAlert.this.lineView.getMeasuredHeight());
                ChatAttachAlert.this.hintTextView.layout((i3 - ChatAttachAlert.this.hintTextView.getMeasuredWidth()) - AndroidUtilities.dp(5.0f), (i4 - ChatAttachAlert.this.hintTextView.getMeasuredHeight()) - AndroidUtilities.dp(5.0f), i3 - AndroidUtilities.dp(5.0f), i4 - AndroidUtilities.dp(5.0f));
                i = (i3 - ChatAttachAlert.this.mediaBanTooltip.getMeasuredWidth()) / 2;
                z += (ChatAttachAlert.this.attachPhotoRecyclerView.getMeasuredHeight() - ChatAttachAlert.this.mediaBanTooltip.getMeasuredHeight()) / 2;
                ChatAttachAlert.this.mediaBanTooltip.layout(i, z, ChatAttachAlert.this.mediaBanTooltip.getMeasuredWidth() + i, ChatAttachAlert.this.mediaBanTooltip.getMeasuredHeight() + z);
                i3 = (i3 - AndroidUtilities.dp(true)) / 3;
                while (z2 < true) {
                    z = AndroidUtilities.dp((float) (true + (95 * (z2 / 4))));
                    i = AndroidUtilities.dp(NUM) + ((z2 % 4) * (AndroidUtilities.dp(NUM) + i3));
                    ChatAttachAlert.this.views[z2].layout(i, z, ChatAttachAlert.this.views[z2].getMeasuredWidth() + i, ChatAttachAlert.this.views[z2].getMeasuredHeight() + z);
                    z2++;
                }
            }
        };
        View[] viewArr = r0.views;
        RecyclerListView recyclerListView2 = new RecyclerListView(context2);
        r0.attachPhotoRecyclerView = recyclerListView2;
        int i = 8;
        viewArr[8] = recyclerListView2;
        r0.attachPhotoRecyclerView.setVerticalScrollBarEnabled(true);
        recyclerListView = r0.attachPhotoRecyclerView;
        listAdapter = new PhotoAttachAdapter(context2, true);
        r0.photoAttachAdapter = listAdapter;
        recyclerListView.setAdapter(listAdapter);
        r0.attachPhotoRecyclerView.setClipToPadding(false);
        r0.attachPhotoRecyclerView.setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f), 0);
        r0.attachPhotoRecyclerView.setItemAnimator(null);
        r0.attachPhotoRecyclerView.setLayoutAnimation(null);
        r0.attachPhotoRecyclerView.setOverScrollMode(2);
        r0.attachView.addView(r0.attachPhotoRecyclerView, LayoutHelper.createFrame(-1, 80.0f));
        r0.attachPhotoLayoutManager = new LinearLayoutManager(context2) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        r0.attachPhotoLayoutManager.setOrientation(0);
        r0.attachPhotoRecyclerView.setLayoutManager(r0.attachPhotoLayoutManager);
        r0.attachPhotoRecyclerView.setOnItemClickListener(new C20487());
        r0.attachPhotoRecyclerView.setOnScrollListener(new C20498());
        viewArr = r0.views;
        CorrectlyMeasuringTextView correctlyMeasuringTextView = new CorrectlyMeasuringTextView(context2);
        r0.mediaBanTooltip = correctlyMeasuringTextView;
        viewArr[11] = correctlyMeasuringTextView;
        r0.mediaBanTooltip.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(3.0f), -12171706));
        r0.mediaBanTooltip.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f));
        r0.mediaBanTooltip.setGravity(16);
        r0.mediaBanTooltip.setTextSize(1, 14.0f);
        r0.mediaBanTooltip.setTextColor(-1);
        r0.mediaBanTooltip.setVisibility(4);
        r0.attachView.addView(r0.mediaBanTooltip, LayoutHelper.createFrame(-2, -2.0f, 51, 14.0f, 0.0f, 14.0f, 0.0f));
        viewArr = r0.views;
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context2);
        r0.progressView = emptyTextProgressView;
        viewArr[9] = emptyTextProgressView;
        if (VERSION.SDK_INT < 23 || getContext().checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == 0) {
            r0.progressView.setText(LocaleController.getString("NoPhotos", C0446R.string.NoPhotos));
            r0.progressView.setTextSize(20);
        } else {
            r0.progressView.setText(LocaleController.getString("PermissionStorage", C0446R.string.PermissionStorage));
            r0.progressView.setTextSize(16);
        }
        r0.attachView.addView(r0.progressView, LayoutHelper.createFrame(-1, 80.0f));
        r0.attachPhotoRecyclerView.setEmptyView(r0.progressView);
        View[] viewArr2 = r0.views;
        View c11119 = new View(getContext()) {
            public boolean hasOverlappingRendering() {
                return false;
            }
        };
        r0.lineView = c11119;
        viewArr2[10] = c11119;
        r0.lineView.setBackgroundColor(Theme.getColor(Theme.key_dialogGrayLine));
        r0.attachView.addView(r0.lineView, new FrameLayout.LayoutParams(-1, 1, 51));
        CharSequence[] charSequenceArr = new CharSequence[]{LocaleController.getString("ChatCamera", C0446R.string.ChatCamera), LocaleController.getString("ChatGallery", C0446R.string.ChatGallery), LocaleController.getString("ChatVideo", C0446R.string.ChatVideo), LocaleController.getString("AttachMusic", C0446R.string.AttachMusic), LocaleController.getString("ChatDocument", C0446R.string.ChatDocument), LocaleController.getString("AttachContact", C0446R.string.AttachContact), LocaleController.getString("ChatLocation", C0446R.string.ChatLocation), TtmlNode.ANONYMOUS_REGION_ID};
        int i2 = 0;
        while (i2 < i) {
            View attachButton = new AttachButton(context2);
            r0.attachButtons.add(attachButton);
            attachButton.setTextAndIcon(charSequenceArr[i2], Theme.chat_attachButtonDrawables[i2]);
            r0.attachView.addView(attachButton, LayoutHelper.createFrame(85, 90, 51));
            attachButton.setTag(Integer.valueOf(i2));
            r0.views[i2] = attachButton;
            if (i2 == 7) {
                r0.sendPhotosButton = attachButton;
                r0.sendPhotosButton.imageView.setPadding(0, AndroidUtilities.dp(4.0f), 0, 0);
            } else if (i2 == 4) {
                r0.sendDocumentsButton = attachButton;
            }
            attachButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    if (!ChatAttachAlert.this.buttonPressed) {
                        ChatAttachAlert.this.buttonPressed = true;
                        ChatAttachAlert.this.delegate.didPressedButton(((Integer) view.getTag()).intValue());
                    }
                }
            });
            i2++;
            i = 8;
        }
        r0.hintTextView = new TextView(context2);
        r0.hintTextView.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(3.0f), Theme.getColor(Theme.key_chat_gifSaveHintBackground)));
        r0.hintTextView.setTextColor(Theme.getColor(Theme.key_chat_gifSaveHintText));
        r0.hintTextView.setTextSize(1, 14.0f);
        r0.hintTextView.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
        r0.hintTextView.setText(LocaleController.getString("AttachBotsHelp", C0446R.string.AttachBotsHelp));
        r0.hintTextView.setGravity(16);
        r0.hintTextView.setVisibility(4);
        r0.hintTextView.setCompoundDrawablesWithIntrinsicBounds(C0446R.drawable.scroll_tip, 0, 0, 0);
        r0.hintTextView.setCompoundDrawablePadding(AndroidUtilities.dp(8.0f));
        r0.attachView.addView(r0.hintTextView, LayoutHelper.createFrame(-2, 32.0f, 85, 5.0f, 0.0f, 5.0f, 5.0f));
        if (r0.loading) {
            r0.progressView.showProgress();
        } else {
            r0.progressView.showTextView();
        }
        r0.recordTime = new TextView(context2);
        r0.recordTime.setBackgroundResource(C0446R.drawable.system);
        r0.recordTime.getBackground().setColorFilter(new PorterDuffColorFilter(NUM, Mode.MULTIPLY));
        r0.recordTime.setTextSize(1, 15.0f);
        r0.recordTime.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        r0.recordTime.setAlpha(0.0f);
        r0.recordTime.setTextColor(-1);
        r0.recordTime.setPadding(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(5.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(5.0f));
        r0.container.addView(r0.recordTime, LayoutHelper.createFrame(-2, -2.0f, 49, 0.0f, 16.0f, 0.0f, 0.0f));
        r0.cameraPanel = new FrameLayout(context2) {
            protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
                z = getMeasuredWidth() / true;
                i2 = getMeasuredHeight() / 2;
                ChatAttachAlert.this.shutterButton.layout(z - (ChatAttachAlert.this.shutterButton.getMeasuredWidth() / 2), i2 - (ChatAttachAlert.this.shutterButton.getMeasuredHeight() / 2), (ChatAttachAlert.this.shutterButton.getMeasuredWidth() / 2) + z, (ChatAttachAlert.this.shutterButton.getMeasuredHeight() / 2) + i2);
                if (getMeasuredWidth() == AndroidUtilities.dp(NUM)) {
                    z = getMeasuredWidth() / true;
                    i3 = i2 / 2;
                    i2 = (i2 + i3) + AndroidUtilities.dp(17.0f);
                    i4 = i3 - AndroidUtilities.dp(17.0f);
                    i3 = z;
                } else {
                    i2 = z / 2;
                    z = (z + i2) + AndroidUtilities.dp(17.0f);
                    i4 = getMeasuredHeight() / 2;
                    i3 = i2 - AndroidUtilities.dp(17.0f);
                    i2 = i4;
                }
                ChatAttachAlert.this.switchCameraButton.layout(z - (ChatAttachAlert.this.switchCameraButton.getMeasuredWidth() / 2), i2 - (ChatAttachAlert.this.switchCameraButton.getMeasuredHeight() / 2), z + (ChatAttachAlert.this.switchCameraButton.getMeasuredWidth() / 2), i2 + (ChatAttachAlert.this.switchCameraButton.getMeasuredHeight() / 2));
                for (z = false; z < true; z++) {
                    ChatAttachAlert.this.flashModeButton[z].layout(i3 - (ChatAttachAlert.this.flashModeButton[z].getMeasuredWidth() / 2), i4 - (ChatAttachAlert.this.flashModeButton[z].getMeasuredHeight() / 2), (ChatAttachAlert.this.flashModeButton[z].getMeasuredWidth() / 2) + i3, (ChatAttachAlert.this.flashModeButton[z].getMeasuredHeight() / 2) + i4);
                }
            }
        };
        r0.cameraPanel.setVisibility(8);
        r0.cameraPanel.setAlpha(0.0f);
        r0.container.addView(r0.cameraPanel, LayoutHelper.createFrame(-1, 100, 83));
        r0.counterTextView = new TextView(context2);
        r0.counterTextView.setBackgroundResource(C0446R.drawable.photos_rounded);
        r0.counterTextView.setVisibility(8);
        r0.counterTextView.setTextColor(-1);
        r0.counterTextView.setGravity(17);
        r0.counterTextView.setPivotX(0.0f);
        r0.counterTextView.setPivotY(0.0f);
        r0.counterTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        r0.counterTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, C0446R.drawable.photos_arrow, 0);
        r0.counterTextView.setCompoundDrawablePadding(AndroidUtilities.dp(4.0f));
        r0.counterTextView.setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f), 0);
        r0.container.addView(r0.counterTextView, LayoutHelper.createFrame(-2, 38.0f, 51, 0.0f, 0.0f, 0.0f, 116.0f));
        r0.counterTextView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (ChatAttachAlert.this.cameraView != null) {
                    ChatAttachAlert.this.openPhotoViewer(null, false, false);
                    CameraController.getInstance().stopPreview(ChatAttachAlert.this.cameraView.getCameraSession());
                }
            }
        });
        r0.shutterButton = new ShutterButton(context2);
        r0.cameraPanel.addView(r0.shutterButton, LayoutHelper.createFrame(84, 84, 17));
        r0.shutterButton.setDelegate(new ShutterButtonDelegate() {
            private File outputFile;

            /* renamed from: org.telegram.ui.Components.ChatAttachAlert$13$1 */
            class C11031 implements Runnable {
                C11031() {
                }

                public void run() {
                    if (ChatAttachAlert.this.videoRecordRunnable != null) {
                        ChatAttachAlert.this.videoRecordTime = ChatAttachAlert.this.videoRecordTime + 1;
                        ChatAttachAlert.this.recordTime.setText(String.format("%02d:%02d", new Object[]{Integer.valueOf(ChatAttachAlert.this.videoRecordTime / 60), Integer.valueOf(ChatAttachAlert.this.videoRecordTime % 60)}));
                        AndroidUtilities.runOnUIThread(ChatAttachAlert.this.videoRecordRunnable, 1000);
                    }
                }
            }

            /* renamed from: org.telegram.ui.Components.ChatAttachAlert$13$3 */
            class C11043 implements Runnable {
                C11043() {
                }

                public void run() {
                    AndroidUtilities.runOnUIThread(ChatAttachAlert.this.videoRecordRunnable, 1000);
                }
            }

            /* renamed from: org.telegram.ui.Components.ChatAttachAlert$13$2 */
            class C20452 implements VideoTakeCallback {
                C20452() {
                }

                public void onFinishVideoRecording(String str, long j) {
                    if (AnonymousClass13.this.outputFile != null) {
                        if (ChatAttachAlert.this.baseFragment != null) {
                            ChatAttachAlert.mediaFromExternalCamera = false;
                            PhotoEntry photoEntry = new PhotoEntry(0, ChatAttachAlert.access$7010(), 0, AnonymousClass13.this.outputFile.getAbsolutePath(), 0, true);
                            photoEntry.duration = (int) j;
                            photoEntry.thumbPath = str;
                            ChatAttachAlert.this.openPhotoViewer(photoEntry, false, false);
                        }
                    }
                }
            }

            public boolean shutterLongPressed() {
                if (!(ChatAttachAlert.this.mediaCaptured || ChatAttachAlert.this.takingPhoto || ChatAttachAlert.this.baseFragment == null || ChatAttachAlert.this.baseFragment.getParentActivity() == null)) {
                    if (ChatAttachAlert.this.cameraView != null) {
                        if (VERSION.SDK_INT < 23 || ChatAttachAlert.this.baseFragment.getParentActivity().checkSelfPermission("android.permission.RECORD_AUDIO") == 0) {
                            for (int i = 0; i < 2; i++) {
                                ChatAttachAlert.this.flashModeButton[i].setAlpha(0.0f);
                            }
                            ChatAttachAlert.this.switchCameraButton.setAlpha(0.0f);
                            this.outputFile = AndroidUtilities.generateVideoPath();
                            ChatAttachAlert.this.recordTime.setAlpha(1.0f);
                            ChatAttachAlert.this.recordTime.setText(String.format("%02d:%02d", new Object[]{Integer.valueOf(0), Integer.valueOf(0)}));
                            ChatAttachAlert.this.videoRecordTime = 0;
                            ChatAttachAlert.this.videoRecordRunnable = new C11031();
                            AndroidUtilities.lockOrientation(chatActivity2.getParentActivity());
                            CameraController.getInstance().recordVideo(ChatAttachAlert.this.cameraView.getCameraSession(), this.outputFile, new C20452(), new C11043());
                            ChatAttachAlert.this.shutterButton.setState(ShutterButton.State.RECORDING, true);
                            return true;
                        }
                        ChatAttachAlert.this.requestingPermissions = true;
                        ChatAttachAlert.this.baseFragment.getParentActivity().requestPermissions(new String[]{"android.permission.RECORD_AUDIO"}, 21);
                        return false;
                    }
                }
                return false;
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
                if (!(ChatAttachAlert.this.takingPhoto || ChatAttachAlert.this.cameraView == null || ChatAttachAlert.this.mediaCaptured)) {
                    if (ChatAttachAlert.this.cameraView.getCameraSession() != null) {
                        ChatAttachAlert.this.mediaCaptured = true;
                        if (ChatAttachAlert.this.shutterButton.getState() == ShutterButton.State.RECORDING) {
                            ChatAttachAlert.this.resetRecordState();
                            CameraController.getInstance().stopVideoRecording(ChatAttachAlert.this.cameraView.getCameraSession(), false);
                            ChatAttachAlert.this.shutterButton.setState(ShutterButton.State.DEFAULT, true);
                            return;
                        }
                        final File generatePicturePath = AndroidUtilities.generatePicturePath();
                        final boolean isSameTakePictureOrientation = ChatAttachAlert.this.cameraView.getCameraSession().isSameTakePictureOrientation();
                        ChatAttachAlert.this.takingPhoto = CameraController.getInstance().takePicture(generatePicturePath, ChatAttachAlert.this.cameraView.getCameraSession(), new Runnable() {
                            public void run() {
                                ChatAttachAlert.this.takingPhoto = false;
                                if (generatePicturePath != null) {
                                    if (ChatAttachAlert.this.baseFragment != null) {
                                        int i;
                                        try {
                                            int attributeInt = new ExifInterface(generatePicturePath.getAbsolutePath()).getAttributeInt("Orientation", 1);
                                            boolean z = attributeInt != 3 ? attributeInt != 6 ? attributeInt != 8 ? false : true : true : true;
                                            i = z;
                                        } catch (Throwable e) {
                                            FileLog.m3e(e);
                                            i = 0;
                                        }
                                        ChatAttachAlert.mediaFromExternalCamera = false;
                                        ChatAttachAlert.this.openPhotoViewer(new PhotoEntry(0, ChatAttachAlert.access$7010(), 0, generatePicturePath.getAbsolutePath(), i, false), isSameTakePictureOrientation, false);
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });
        r0.switchCameraButton = new ImageView(context2);
        r0.switchCameraButton.setScaleType(ScaleType.CENTER);
        r0.cameraPanel.addView(r0.switchCameraButton, LayoutHelper.createFrame(48, 48, 21));
        r0.switchCameraButton.setOnClickListener(new View.OnClickListener() {

            /* renamed from: org.telegram.ui.Components.ChatAttachAlert$14$1 */
            class C11061 extends AnimatorListenerAdapter {
                C11061() {
                }

                public void onAnimationEnd(Animator animator) {
                    animator = ChatAttachAlert.this.switchCameraButton;
                    int i = (ChatAttachAlert.this.cameraView == null || !ChatAttachAlert.this.cameraView.isFrontface()) ? C0446R.drawable.camera_revert2 : C0446R.drawable.camera_revert1;
                    animator.setImageResource(i);
                    ObjectAnimator.ofFloat(ChatAttachAlert.this.switchCameraButton, "scaleX", new float[]{1.0f}).setDuration(100).start();
                }
            }

            public void onClick(View view) {
                if (ChatAttachAlert.this.takingPhoto == null && ChatAttachAlert.this.cameraView != null) {
                    if (ChatAttachAlert.this.cameraView.isInitied() != null) {
                        ChatAttachAlert.this.cameraInitied = false;
                        ChatAttachAlert.this.cameraView.switchCamera();
                        view = ObjectAnimator.ofFloat(ChatAttachAlert.this.switchCameraButton, "scaleX", new float[]{0.0f}).setDuration(100);
                        view.addListener(new C11061());
                        view.start();
                    }
                }
            }
        });
        for (int i3 = 0; i3 < 2; i3++) {
            r0.flashModeButton[i3] = new ImageView(context2);
            r0.flashModeButton[i3].setScaleType(ScaleType.CENTER);
            r0.flashModeButton[i3].setVisibility(4);
            r0.cameraPanel.addView(r0.flashModeButton[i3], LayoutHelper.createFrame(48, 48, 51));
            r0.flashModeButton[i3].setOnClickListener(new View.OnClickListener() {
                public void onClick(final View view) {
                    if (!(ChatAttachAlert.this.flashAnimationInProgress || ChatAttachAlert.this.cameraView == null || !ChatAttachAlert.this.cameraView.isInitied())) {
                        if (ChatAttachAlert.this.cameraOpened) {
                            String currentFlashMode = ChatAttachAlert.this.cameraView.getCameraSession().getCurrentFlashMode();
                            String nextFlashMode = ChatAttachAlert.this.cameraView.getCameraSession().getNextFlashMode();
                            if (!currentFlashMode.equals(nextFlashMode)) {
                                ChatAttachAlert.this.cameraView.getCameraSession().setCurrentFlashMode(nextFlashMode);
                                ChatAttachAlert.this.flashAnimationInProgress = true;
                                ImageView imageView = ChatAttachAlert.this.flashModeButton[0] == view ? ChatAttachAlert.this.flashModeButton[1] : ChatAttachAlert.this.flashModeButton[0];
                                imageView.setVisibility(0);
                                ChatAttachAlert.this.setCameraFlashModeIcon(imageView, nextFlashMode);
                                AnimatorSet animatorSet = new AnimatorSet();
                                r4 = new Animator[4];
                                r4[0] = ObjectAnimator.ofFloat(view, "translationY", new float[]{0.0f, (float) AndroidUtilities.dp(48.0f)});
                                r4[1] = ObjectAnimator.ofFloat(imageView, "translationY", new float[]{(float) (-AndroidUtilities.dp(48.0f)), 0.0f});
                                r4[2] = ObjectAnimator.ofFloat(view, "alpha", new float[]{1.0f, 0.0f});
                                r4[3] = ObjectAnimator.ofFloat(imageView, "alpha", new float[]{0.0f, 1.0f});
                                animatorSet.playTogether(r4);
                                animatorSet.setDuration(200);
                                animatorSet.addListener(new AnimatorListenerAdapter() {
                                    public void onAnimationEnd(Animator animator) {
                                        ChatAttachAlert.this.flashAnimationInProgress = false;
                                        view.setVisibility(4);
                                    }
                                });
                                animatorSet.start();
                            }
                        }
                    }
                }
            });
        }
        r0.cameraPhotoRecyclerView = new RecyclerListView(context2) {
            public void requestLayout() {
                if (!ChatAttachAlert.this.cameraPhotoRecyclerViewIgnoreLayout) {
                    super.requestLayout();
                }
            }
        };
        r0.cameraPhotoRecyclerView.setVerticalScrollBarEnabled(true);
        RecyclerListView recyclerListView3 = r0.cameraPhotoRecyclerView;
        Adapter photoAttachAdapter = new PhotoAttachAdapter(context2, false);
        r0.cameraAttachAdapter = photoAttachAdapter;
        recyclerListView3.setAdapter(photoAttachAdapter);
        r0.cameraPhotoRecyclerView.setClipToPadding(false);
        r0.cameraPhotoRecyclerView.setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f), 0);
        r0.cameraPhotoRecyclerView.setItemAnimator(null);
        r0.cameraPhotoRecyclerView.setLayoutAnimation(null);
        r0.cameraPhotoRecyclerView.setOverScrollMode(2);
        r0.cameraPhotoRecyclerView.setVisibility(4);
        r0.cameraPhotoRecyclerView.setAlpha(0.0f);
        r0.container.addView(r0.cameraPhotoRecyclerView, LayoutHelper.createFrame(-1, 80.0f));
        r0.cameraPhotoLayoutManager = new LinearLayoutManager(context2, 0, false) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        r0.cameraPhotoRecyclerView.setLayoutManager(r0.cameraPhotoLayoutManager);
        r0.cameraPhotoRecyclerView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(View view, int i) {
                if ((view instanceof PhotoAttachPhotoCell) != 0) {
                    ((PhotoAttachPhotoCell) view).callDelegate();
                }
            }
        });
    }

    public void show() {
        super.show();
        this.buttonPressed = false;
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
            CameraController.getInstance().startPreview(this.cameraView.getCameraSession());
            this.mediaCaptured = null;
        } else if (cameraPhotos.isEmpty() == null) {
            this.cancelTakingPhotos = true;
            PhotoViewer.getInstance().setParentActivity(this.baseFragment.getParentActivity());
            PhotoViewer.getInstance().setParentAlert(this);
            PhotoViewer.getInstance().openPhotoForSelect(getAllPhotosArray(), cameraPhotos.size() - 1, 2, new BasePhotoProvider() {

                /* renamed from: org.telegram.ui.Components.ChatAttachAlert$19$1 */
                class C11081 implements Runnable {
                    C11081() {
                    }

                    public void run() {
                        if (ChatAttachAlert.this.cameraView != null && !ChatAttachAlert.this.isDismissed() && VERSION.SDK_INT >= 21) {
                            ChatAttachAlert.this.cameraView.setSystemUiVisibility(1028);
                        }
                    }
                }

                public boolean canScrollAway() {
                    return false;
                }

                public BitmapHolder getThumbForPhoto(MessageObject messageObject, FileLocation fileLocation, int i) {
                    return null;
                }

                public boolean cancelButtonPressed() {
                    if (ChatAttachAlert.this.cameraOpened && ChatAttachAlert.this.cameraView != null) {
                        AndroidUtilities.runOnUIThread(new C11081(), 1000);
                        CameraController.getInstance().startPreview(ChatAttachAlert.this.cameraView.getCameraSession());
                    }
                    if (ChatAttachAlert.this.cancelTakingPhotos && ChatAttachAlert.cameraPhotos.size() == 1) {
                        int size = ChatAttachAlert.cameraPhotos.size();
                        for (int i = 0; i < size; i++) {
                            PhotoEntry photoEntry = (PhotoEntry) ChatAttachAlert.cameraPhotos.get(i);
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
                    if (!ChatAttachAlert.cameraPhotos.isEmpty()) {
                        if (ChatAttachAlert.this.baseFragment != null) {
                            if (videoEditedInfo != null && i >= 0 && i < ChatAttachAlert.cameraPhotos.size()) {
                                ((PhotoEntry) ChatAttachAlert.cameraPhotos.get(i)).editedInfo = videoEditedInfo;
                            }
                            i = ChatAttachAlert.cameraPhotos.size();
                            for (int i2 = 0; i2 < i; i2++) {
                                AndroidUtilities.addMediaToGallery(((PhotoEntry) ChatAttachAlert.cameraPhotos.get(i2)).path);
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
                }

                public boolean scaleToFill() {
                    boolean z = false;
                    if (ChatAttachAlert.this.baseFragment != null) {
                        if (ChatAttachAlert.this.baseFragment.getParentActivity() != null) {
                            int i = System.getInt(ChatAttachAlert.this.baseFragment.getParentActivity().getContentResolver(), "accelerometer_rotation", 0);
                            if (z || i == 1) {
                                z = true;
                            }
                            return z;
                        }
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
            }, this.baseFragment);
        }
    }

    private boolean processTouchEvent(MotionEvent motionEvent) {
        if (motionEvent == null) {
            return false;
        }
        if ((this.pressed || motionEvent.getActionMasked() != 0) && motionEvent.getActionMasked() != 5) {
            if (this.pressed) {
                Animator[] animatorArr;
                if (motionEvent.getActionMasked() == 2) {
                    motionEvent = motionEvent.getY();
                    float f = motionEvent - this.lastY;
                    if (this.maybeStartDraging) {
                        if (Math.abs(f) > AndroidUtilities.getPixelsInCM(0.4f, false)) {
                            this.maybeStartDraging = false;
                            this.dragging = true;
                        }
                    } else if (this.dragging && this.cameraView != null) {
                        this.cameraView.setTranslationY(this.cameraView.getTranslationY() + f);
                        this.lastY = motionEvent;
                        if (this.cameraPanel.getTag() == null) {
                            this.cameraPanel.setTag(Integer.valueOf(1));
                            motionEvent = new AnimatorSet();
                            animatorArr = new Animator[5];
                            animatorArr[0] = ObjectAnimator.ofFloat(this.cameraPanel, "alpha", new float[]{0.0f});
                            animatorArr[1] = ObjectAnimator.ofFloat(this.counterTextView, "alpha", new float[]{0.0f});
                            animatorArr[2] = ObjectAnimator.ofFloat(this.flashModeButton[0], "alpha", new float[]{0.0f});
                            animatorArr[3] = ObjectAnimator.ofFloat(this.flashModeButton[1], "alpha", new float[]{0.0f});
                            animatorArr[4] = ObjectAnimator.ofFloat(this.cameraPhotoRecyclerView, "alpha", new float[]{0.0f});
                            motionEvent.playTogether(animatorArr);
                            motionEvent.setDuration(200);
                            motionEvent.start();
                        }
                    }
                } else if (motionEvent.getActionMasked() == 3 || motionEvent.getActionMasked() == 1 || motionEvent.getActionMasked() == 6) {
                    this.pressed = false;
                    if (this.dragging) {
                        this.dragging = false;
                        if (this.cameraView != null) {
                            if (Math.abs(this.cameraView.getTranslationY()) > ((float) this.cameraView.getMeasuredHeight()) / 6.0f) {
                                closeCamera(true);
                            } else {
                                motionEvent = new AnimatorSet();
                                animatorArr = new Animator[6];
                                animatorArr[0] = ObjectAnimator.ofFloat(this.cameraView, "translationY", new float[]{0.0f});
                                animatorArr[1] = ObjectAnimator.ofFloat(this.cameraPanel, "alpha", new float[]{1.0f});
                                animatorArr[2] = ObjectAnimator.ofFloat(this.counterTextView, "alpha", new float[]{1.0f});
                                animatorArr[3] = ObjectAnimator.ofFloat(this.flashModeButton[0], "alpha", new float[]{1.0f});
                                animatorArr[4] = ObjectAnimator.ofFloat(this.flashModeButton[1], "alpha", new float[]{1.0f});
                                animatorArr[5] = ObjectAnimator.ofFloat(this.cameraPhotoRecyclerView, "alpha", new float[]{1.0f});
                                motionEvent.playTogether(animatorArr);
                                motionEvent.setDuration(250);
                                motionEvent.setInterpolator(this.interpolator);
                                motionEvent.start();
                                this.cameraPanel.setTag(null);
                            }
                        }
                    } else if (this.cameraView != null) {
                        this.cameraView.getLocationOnScreen(this.viewPosition);
                        this.cameraView.focusToPoint((int) (motionEvent.getRawX() - ((float) this.viewPosition[0])), (int) (motionEvent.getRawY() - ((float) this.viewPosition[1])));
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

    protected boolean onContainerTouchEvent(MotionEvent motionEvent) {
        return (!this.cameraOpened || processTouchEvent(motionEvent) == null) ? null : true;
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

    private void setCameraFlashModeIcon(ImageView imageView, String str) {
        int hashCode = str.hashCode();
        if (hashCode != 3551) {
            if (hashCode != 109935) {
                if (hashCode == 3005871) {
                    if (str.equals("auto") != null) {
                        str = 2;
                        switch (str) {
                            case null:
                                imageView.setImageResource(C0446R.drawable.flash_off);
                                return;
                            case 1:
                                imageView.setImageResource(C0446R.drawable.flash_on);
                                return;
                            case 2:
                                imageView.setImageResource(C0446R.drawable.flash_auto);
                                return;
                            default:
                                return;
                        }
                    }
                }
            } else if (str.equals("off") != null) {
                str = null;
                switch (str) {
                    case null:
                        imageView.setImageResource(C0446R.drawable.flash_off);
                        return;
                    case 1:
                        imageView.setImageResource(C0446R.drawable.flash_on);
                        return;
                    case 2:
                        imageView.setImageResource(C0446R.drawable.flash_auto);
                        return;
                    default:
                        return;
                }
            }
        } else if (str.equals("on") != null) {
            str = true;
            switch (str) {
                case null:
                    imageView.setImageResource(C0446R.drawable.flash_off);
                    return;
                case 1:
                    imageView.setImageResource(C0446R.drawable.flash_on);
                    return;
                case 2:
                    imageView.setImageResource(C0446R.drawable.flash_auto);
                    return;
                default:
                    return;
            }
        }
        str = -1;
        switch (str) {
            case null:
                imageView.setImageResource(C0446R.drawable.flash_off);
                return;
            case 1:
                imageView.setImageResource(C0446R.drawable.flash_on);
                return;
            case 2:
                imageView.setImageResource(C0446R.drawable.flash_auto);
                return;
            default:
                return;
        }
    }

    protected boolean onCustomMeasure(View view, int i, int i2) {
        boolean z = i < i2;
        if (view == this.cameraView) {
            if (this.cameraOpened != null && this.cameraAnimationInProgress == null) {
                this.cameraView.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec(i2, NUM));
                return true;
            }
        } else if (view == this.cameraPanel) {
            if (z) {
                this.cameraPanel.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(100.0f), NUM));
            } else {
                this.cameraPanel.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(100.0f), NUM), MeasureSpec.makeMeasureSpec(i2, NUM));
            }
            return true;
        } else if (view == this.cameraPhotoRecyclerView) {
            this.cameraPhotoRecyclerViewIgnoreLayout = true;
            if (z) {
                this.cameraPhotoRecyclerView.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(80.0f), NUM));
                if (this.cameraPhotoLayoutManager.getOrientation() != null) {
                    this.cameraPhotoRecyclerView.setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f), 0);
                    this.cameraPhotoLayoutManager.setOrientation(0);
                    this.cameraAttachAdapter.notifyDataSetChanged();
                }
            } else {
                this.cameraPhotoRecyclerView.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(80.0f), NUM), MeasureSpec.makeMeasureSpec(i2, NUM));
                if (this.cameraPhotoLayoutManager.getOrientation() != 1) {
                    this.cameraPhotoRecyclerView.setPadding(0, AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f));
                    this.cameraPhotoLayoutManager.setOrientation(1);
                    this.cameraAttachAdapter.notifyDataSetChanged();
                }
            }
            this.cameraPhotoRecyclerViewIgnoreLayout = false;
            return true;
        }
        return false;
    }

    protected boolean onCustomLayout(View view, int i, int i2, int i3, int i4) {
        int i5 = i3 - i;
        i2 = i4 - i2;
        boolean z = i5 < i2;
        if (view == this.cameraPanel) {
            if (z) {
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
        } else if (view == this.counterTextView) {
            if (z) {
                i5 = (i5 - this.counterTextView.getMeasuredWidth()) / 2;
                i4 -= AndroidUtilities.dp(154.0f);
                this.counterTextView.setRotation(0);
                if (this.cameraPhotoRecyclerView.getVisibility() == null) {
                    i4 -= AndroidUtilities.dp(96.0f);
                }
            } else {
                i5 = i3 - AndroidUtilities.dp(154.0f);
                i4 = (i2 / 2) + (this.counterTextView.getMeasuredWidth() / 2);
                this.counterTextView.setRotation(-NUM);
                if (this.cameraPhotoRecyclerView.getVisibility() == null) {
                    i5 -= AndroidUtilities.dp(96.0f);
                }
            }
            this.counterTextView.layout(i5, i4, this.counterTextView.getMeasuredWidth() + i5, this.counterTextView.getMeasuredHeight() + i4);
            return true;
        } else if (view != this.cameraPhotoRecyclerView) {
            return false;
        } else {
            if (z) {
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
            this.currentHintAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (ChatAttachAlert.this.currentHintAnimation != null) {
                        if (ChatAttachAlert.this.currentHintAnimation.equals(animator) != null) {
                            ChatAttachAlert.this.currentHintAnimation = null;
                            if (ChatAttachAlert.this.hintTextView != null) {
                                ChatAttachAlert.this.hintTextView.setVisibility(4);
                            }
                        }
                    }
                }

                public void onAnimationCancel(Animator animator) {
                    if (ChatAttachAlert.this.currentHintAnimation != null && ChatAttachAlert.this.currentHintAnimation.equals(animator) != null) {
                        ChatAttachAlert.this.currentHintAnimation = null;
                    }
                }
            });
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
            this.animateCameraValues[0] = 0;
            this.animateCameraValues[1] = AndroidUtilities.dp(80.0f) - this.cameraViewOffsetX;
            this.animateCameraValues[2] = AndroidUtilities.dp(80.0f) - this.cameraViewOffsetY;
            if (z) {
                this.cameraAnimationInProgress = true;
                z = new ArrayList();
                z.add(ObjectAnimator.ofFloat(this, "cameraOpenProgress", new float[]{0.0f, 1.0f}));
                z.add(ObjectAnimator.ofFloat(this.cameraPanel, "alpha", new float[]{1.0f}));
                z.add(ObjectAnimator.ofFloat(this.counterTextView, "alpha", new float[]{1.0f}));
                z.add(ObjectAnimator.ofFloat(this.cameraPhotoRecyclerView, "alpha", new float[]{1.0f}));
                for (int i2 = 0; i2 < 2; i2++) {
                    if (this.flashModeButton[i2].getVisibility() == 0) {
                        z.add(ObjectAnimator.ofFloat(this.flashModeButton[i2], "alpha", new float[]{1.0f}));
                        break;
                    }
                }
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(z);
                animatorSet.setDuration(200);
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        ChatAttachAlert.this.cameraAnimationInProgress = false;
                        if (ChatAttachAlert.this.cameraOpened != null) {
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
                    if (!this.flashModeButton[i].getVisibility()) {
                        this.flashModeButton[i].setAlpha(1.0f);
                        break;
                    }
                    i++;
                }
                this.delegate.onCameraOpened();
            }
            if (VERSION.SDK_INT >= true) {
                this.cameraView.setSystemUiVisibility(1028);
            }
            this.cameraOpened = true;
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onActivityResultFragment(int i, Intent intent, String str) {
        Throwable e;
        Bitmap createVideoThumbnail;
        StringBuilder stringBuilder;
        File file;
        int i2;
        PhotoEntry photoEntry;
        Throwable th;
        int i3 = i;
        String str2 = str;
        if (this.baseFragment != null) {
            if (r1.baseFragment.getParentActivity() != null) {
                mediaFromExternalCamera = true;
                if (i3 == 0) {
                    int i4;
                    PhotoViewer.getInstance().setParentActivity(r1.baseFragment.getParentActivity());
                    ArrayList arrayList = new ArrayList();
                    try {
                        i3 = new ExifInterface(str2).getAttributeInt("Orientation", 1);
                        boolean z = i3 != 3 ? i3 != 6 ? i3 != 8 ? false : true : true : true;
                        i4 = z;
                    } catch (Throwable e2) {
                        FileLog.m3e(e2);
                        i4 = 0;
                    }
                    int i5 = lastImageId;
                    lastImageId = i5 - 1;
                    openPhotoViewer(new PhotoEntry(0, i5, 0, str2, i4, false), false, true);
                } else if (i3 == 2) {
                    String path;
                    if (BuildVars.LOGS_ENABLED) {
                        StringBuilder stringBuilder2 = new StringBuilder();
                        stringBuilder2.append("pic path ");
                        stringBuilder2.append(str2);
                        FileLog.m0d(stringBuilder2.toString());
                    }
                    Intent intent2 = (intent == null || str2 == null || !new File(str2).exists()) ? intent : null;
                    if (intent2 != null) {
                        Uri data = intent2.getData();
                        if (data != null) {
                            StringBuilder stringBuilder3;
                            if (BuildVars.LOGS_ENABLED) {
                                stringBuilder3 = new StringBuilder();
                                stringBuilder3.append("video record uri ");
                                stringBuilder3.append(data.toString());
                                FileLog.m0d(stringBuilder3.toString());
                            }
                            path = AndroidUtilities.getPath(data);
                            if (BuildVars.LOGS_ENABLED) {
                                stringBuilder3 = new StringBuilder();
                                stringBuilder3.append("resolved path = ");
                                stringBuilder3.append(path);
                                FileLog.m0d(stringBuilder3.toString());
                            }
                            if (path != null) {
                            }
                        }
                        path = str2;
                        AndroidUtilities.addMediaToGallery(str);
                        str2 = null;
                    } else {
                        path = null;
                    }
                    if (path == null && str2 != null && new File(str2).exists()) {
                        path = str2;
                    }
                    long j = 0;
                    MediaMetadataRetriever mediaMetadataRetriever;
                    try {
                        mediaMetadataRetriever = new MediaMetadataRetriever();
                        try {
                            mediaMetadataRetriever.setDataSource(path);
                            String extractMetadata = mediaMetadataRetriever.extractMetadata(9);
                            if (extractMetadata != null) {
                                j = (long) ((int) Math.ceil((double) (((float) Long.parseLong(extractMetadata)) / 1000.0f)));
                            }
                            if (mediaMetadataRetriever != null) {
                                try {
                                    mediaMetadataRetriever.release();
                                } catch (Throwable e22) {
                                    FileLog.m3e(e22);
                                }
                            }
                        } catch (Exception e3) {
                            e22 = e3;
                            try {
                                FileLog.m3e(e22);
                                if (mediaMetadataRetriever != null) {
                                    mediaMetadataRetriever.release();
                                }
                                createVideoThumbnail = ThumbnailUtils.createVideoThumbnail(path, 1);
                                stringBuilder = new StringBuilder();
                                stringBuilder.append("-2147483648_");
                                stringBuilder.append(SharedConfig.getLastLocalId());
                                stringBuilder.append(".jpg");
                                file = new File(FileLoader.getDirectory(4), stringBuilder.toString());
                                createVideoThumbnail.compress(CompressFormat.JPEG, 55, new FileOutputStream(file));
                                SharedConfig.saveConfig();
                                i2 = lastImageId;
                                lastImageId = i2 - 1;
                                photoEntry = new PhotoEntry(0, i2, 0, path, 0, true);
                                photoEntry.duration = (int) j;
                                photoEntry.thumbPath = file.getAbsolutePath();
                                openPhotoViewer(photoEntry, false, true);
                            } catch (Throwable th2) {
                                e22 = th2;
                                th = e22;
                                if (mediaMetadataRetriever != null) {
                                    try {
                                        mediaMetadataRetriever.release();
                                    } catch (Throwable e222) {
                                        FileLog.m3e(e222);
                                    }
                                }
                                throw th;
                            }
                        }
                    } catch (Exception e4) {
                        e222 = e4;
                        mediaMetadataRetriever = null;
                        FileLog.m3e(e222);
                        if (mediaMetadataRetriever != null) {
                            mediaMetadataRetriever.release();
                        }
                        createVideoThumbnail = ThumbnailUtils.createVideoThumbnail(path, 1);
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("-2147483648_");
                        stringBuilder.append(SharedConfig.getLastLocalId());
                        stringBuilder.append(".jpg");
                        file = new File(FileLoader.getDirectory(4), stringBuilder.toString());
                        createVideoThumbnail.compress(CompressFormat.JPEG, 55, new FileOutputStream(file));
                        SharedConfig.saveConfig();
                        i2 = lastImageId;
                        lastImageId = i2 - 1;
                        photoEntry = new PhotoEntry(0, i2, 0, path, 0, true);
                        photoEntry.duration = (int) j;
                        photoEntry.thumbPath = file.getAbsolutePath();
                        openPhotoViewer(photoEntry, false, true);
                    } catch (Throwable th3) {
                        e222 = th3;
                        mediaMetadataRetriever = null;
                        th = e222;
                        if (mediaMetadataRetriever != null) {
                            mediaMetadataRetriever.release();
                        }
                        throw th;
                    }
                    createVideoThumbnail = ThumbnailUtils.createVideoThumbnail(path, 1);
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("-2147483648_");
                    stringBuilder.append(SharedConfig.getLastLocalId());
                    stringBuilder.append(".jpg");
                    file = new File(FileLoader.getDirectory(4), stringBuilder.toString());
                    try {
                        createVideoThumbnail.compress(CompressFormat.JPEG, 55, new FileOutputStream(file));
                    } catch (Throwable e2222) {
                        FileLog.m3e(e2222);
                    }
                    SharedConfig.saveConfig();
                    i2 = lastImageId;
                    lastImageId = i2 - 1;
                    photoEntry = new PhotoEntry(0, i2, 0, path, 0, true);
                    photoEntry.duration = (int) j;
                    photoEntry.thumbPath = file.getAbsolutePath();
                    openPhotoViewer(photoEntry, false, true);
                }
            }
        }
    }

    public void closeCamera(boolean z) {
        if (!this.takingPhoto) {
            if (this.cameraView != null) {
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
                    z = new ArrayList();
                    z.add(ObjectAnimator.ofFloat(this, "cameraOpenProgress", new float[]{0.0f}));
                    z.add(ObjectAnimator.ofFloat(this.cameraPanel, "alpha", new float[]{0.0f}));
                    z.add(ObjectAnimator.ofFloat(this.counterTextView, "alpha", new float[]{0.0f}));
                    z.add(ObjectAnimator.ofFloat(this.cameraPhotoRecyclerView, "alpha", new float[]{0.0f}));
                    for (int i = 0; i < 2; i++) {
                        if (this.flashModeButton[i].getVisibility() == 0) {
                            z.add(ObjectAnimator.ofFloat(this.flashModeButton[i], "alpha", new float[]{0.0f}));
                            break;
                        }
                    }
                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.playTogether(z);
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
                    this.animateCameraValues[0] = null;
                    setCameraOpenProgress(0.0f);
                    this.cameraPanel.setAlpha(0.0f);
                    this.cameraPhotoRecyclerView.setAlpha(0.0f);
                    this.counterTextView.setAlpha(0.0f);
                    this.cameraPanel.setVisibility(8);
                    this.cameraPhotoRecyclerView.setVisibility(8);
                    for (z = false; z < true; z++) {
                        if (this.flashModeButton[z].getVisibility() == 0) {
                            this.flashModeButton[z].setAlpha(0.0f);
                            break;
                        }
                    }
                    this.cameraOpened = false;
                    if (VERSION.SDK_INT >= true) {
                        this.cameraView.setSystemUiVisibility(1024);
                    }
                }
            }
        }
    }

    @Keep
    public void setCameraOpenProgress(float f) {
        if (this.cameraView != null) {
            float width;
            float height;
            this.cameraOpenProgress = f;
            float f2 = (float) this.animateCameraValues[1];
            float f3 = (float) this.animateCameraValues[2];
            if ((AndroidUtilities.displaySize.x < AndroidUtilities.displaySize.y ? 1 : 0) != 0) {
                width = (float) this.container.getWidth();
                height = (float) this.container.getHeight();
            } else {
                width = (float) this.container.getWidth();
                height = (float) this.container.getHeight();
            }
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
            layoutParams.height = (int) (f3 + ((height - f3) * f));
            if (f != 0.0f) {
                height = 1.0f - f;
                this.cameraView.setClipLeft((int) (((float) this.cameraViewOffsetX) * height));
                this.cameraView.setClipTop((int) (((float) this.cameraViewOffsetY) * height));
                layoutParams.leftMargin = (int) (((float) this.cameraViewLocation[0]) * height);
                layoutParams.topMargin = (int) (((float) this.animateCameraValues[0]) + (((float) (this.cameraViewLocation[1] - this.animateCameraValues[0])) * height));
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
            int i = 0;
            while (i < childCount) {
                View childAt = this.attachPhotoRecyclerView.getChildAt(i);
                if (!(childAt instanceof PhotoAttachCameraCell)) {
                    i++;
                } else if (VERSION.SDK_INT < 19 || childAt.isAttachedToWindow()) {
                    childAt.getLocationInWindow(this.cameraViewLocation);
                    int[] iArr = this.cameraViewLocation;
                    iArr[0] = iArr[0] - getLeftInset();
                    float x = (this.listView.getX() + ((float) backgroundPaddingLeft)) - ((float) getLeftInset());
                    if (((float) this.cameraViewLocation[0]) < x) {
                        this.cameraViewOffsetX = (int) (x - ((float) this.cameraViewLocation[0]));
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
                    if (VERSION.SDK_INT < 21 || this.cameraViewLocation[1] >= AndroidUtilities.statusBarHeight) {
                        this.cameraViewOffsetY = 0;
                    } else {
                        this.cameraViewOffsetY = AndroidUtilities.statusBarHeight - this.cameraViewLocation[1];
                        if (this.cameraViewOffsetY >= AndroidUtilities.dp(80.0f)) {
                            this.cameraViewOffsetY = 0;
                            this.cameraViewLocation[0] = AndroidUtilities.dp(-150.0f);
                            this.cameraViewLocation[1] = 0;
                        } else {
                            iArr = this.cameraViewLocation;
                            iArr[1] = iArr[1] + this.cameraViewOffsetY;
                        }
                    }
                    applyCameraViewPosition();
                    return;
                } else {
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
        if (this.cameraView != null) {
            final FrameLayout.LayoutParams layoutParams;
            if (!this.cameraOpened) {
                this.cameraView.setTranslationX((float) this.cameraViewLocation[0]);
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
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            if (ChatAttachAlert.this.cameraView != null) {
                                ChatAttachAlert.this.cameraView.setLayoutParams(layoutParams);
                            }
                        }
                    });
                }
            }
            layoutParams = (FrameLayout.LayoutParams) this.cameraIcon.getLayoutParams();
            if (layoutParams.height != dp2 || layoutParams.width != dp) {
                layoutParams.width = dp;
                layoutParams.height = dp2;
                this.cameraIcon.setLayoutParams(layoutParams);
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        if (ChatAttachAlert.this.cameraIcon != null) {
                            ChatAttachAlert.this.cameraIcon.setLayoutParams(layoutParams);
                        }
                    }
                });
            }
        }
    }

    public void showCamera() {
        if (!this.paused) {
            if (this.mediaEnabled) {
                if (this.cameraView == null) {
                    this.cameraView = new CameraView(this.baseFragment.getParentActivity(), false);
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
                                ChatAttachAlert.this.setCameraFlashModeIcon(ChatAttachAlert.this.flashModeButton[0], ChatAttachAlert.this.cameraView.getCameraSession().getCurrentFlashMode());
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
                            ChatAttachAlert.this.switchCameraButton.setImageResource(ChatAttachAlert.this.cameraView.isFrontface() ? C0446R.drawable.camera_revert1 : C0446R.drawable.camera_revert2);
                            ImageView access$5800 = ChatAttachAlert.this.switchCameraButton;
                            if (!ChatAttachAlert.this.cameraView.hasFrontFaceCamera()) {
                                i = 4;
                            }
                            access$5800.setVisibility(i);
                        }
                    });
                    if (this.cameraIcon == null) {
                        this.cameraIcon = new FrameLayout(this.baseFragment.getParentActivity());
                        View imageView = new ImageView(this.baseFragment.getParentActivity());
                        imageView.setScaleType(ScaleType.CENTER);
                        imageView.setImageResource(C0446R.drawable.instant_camera);
                        this.cameraIcon.addView(imageView, LayoutHelper.createFrame(80, 80, 85));
                    }
                    this.container.addView(this.cameraIcon, 2, LayoutHelper.createFrame(80, 80.0f));
                    float f = 0.2f;
                    this.cameraView.setAlpha(this.mediaEnabled ? 1.0f : 0.2f);
                    this.cameraView.setEnabled(this.mediaEnabled);
                    FrameLayout frameLayout = this.cameraIcon;
                    if (this.mediaEnabled) {
                        f = 1.0f;
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
    }

    public void hideCamera(boolean z) {
        if (this.deviceHasGoodCamera) {
            if (this.cameraView != null) {
                this.cameraView.destroy(z, null);
                this.container.removeView(this.cameraView);
                this.container.removeView(this.cameraIcon);
                this.cameraView = null;
                this.cameraIcon = null;
                z = this.attachPhotoRecyclerView.getChildCount();
                for (boolean z2 = false; z2 < z; z2++) {
                    View childAt = this.attachPhotoRecyclerView.getChildAt(z2);
                    if (childAt instanceof PhotoAttachCameraCell) {
                        childAt.setVisibility(0);
                        return;
                    }
                }
            }
        }
    }

    private void showHint() {
        if (!DataQuery.getInstance(this.currentAccount).inlineBots.isEmpty() && !MessagesController.getGlobalMainSettings().getBoolean("bothint", false)) {
            this.hintShowed = true;
            this.hintTextView.setVisibility(0);
            this.currentHintAnimation = new AnimatorSet();
            this.currentHintAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.hintTextView, "alpha", new float[]{0.0f, 1.0f})});
            this.currentHintAnimation.setInterpolator(this.decelerateInterpolator);
            this.currentHintAnimation.addListener(new AnimatorListenerAdapter() {

                /* renamed from: org.telegram.ui.Components.ChatAttachAlert$26$1 */
                class C11091 implements Runnable {
                    C11091() {
                    }

                    public void run() {
                        if (ChatAttachAlert.this.hideHintRunnable == this) {
                            ChatAttachAlert.this.hideHintRunnable = null;
                            ChatAttachAlert.this.hideHint();
                        }
                    }
                }

                public void onAnimationEnd(Animator animator) {
                    if (ChatAttachAlert.this.currentHintAnimation != null) {
                        if (ChatAttachAlert.this.currentHintAnimation.equals(animator) != null) {
                            ChatAttachAlert.this.currentHintAnimation = null;
                            AndroidUtilities.runOnUIThread(ChatAttachAlert.this.hideHintRunnable = new C11091(), AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
                        }
                    }
                }

                public void onAnimationCancel(Animator animator) {
                    if (ChatAttachAlert.this.currentHintAnimation != null && ChatAttachAlert.this.currentHintAnimation.equals(animator) != null) {
                        ChatAttachAlert.this.currentHintAnimation = null;
                    }
                }
            });
            this.currentHintAnimation.setDuration(300);
            this.currentHintAnimation.start();
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.albumsDidLoaded) {
            if (this.photoAttachAdapter != 0) {
                this.loading = false;
                this.progressView.showTextView();
                this.photoAttachAdapter.notifyDataSetChanged();
                this.cameraAttachAdapter.notifyDataSetChanged();
            }
        } else if (i == NotificationCenter.reloadInlineHints) {
            if (this.adapter != 0) {
                this.adapter.notifyDataSetChanged();
            }
        } else if (i == NotificationCenter.cameraInitied) {
            checkCamera(false);
        }
    }

    @SuppressLint({"NewApi"})
    private void updateLayout() {
        if (this.listView.getChildCount() <= 0) {
            RecyclerListView recyclerListView = this.listView;
            int paddingTop = this.listView.getPaddingTop();
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
            this.sendPhotosButton.imageView.setPadding(0, AndroidUtilities.dp(4.0f), 0, 0);
            this.sendPhotosButton.imageView.setBackgroundResource(C0446R.drawable.attach_hide_states);
            this.sendPhotosButton.imageView.setImageResource(C0446R.drawable.attach_hide2);
            this.sendPhotosButton.textView.setText(TtmlNode.ANONYMOUS_REGION_ID);
            this.sendDocumentsButton.textView.setText(LocaleController.getString("ChatDocument", C0446R.string.ChatDocument));
        } else {
            String str;
            int i;
            this.sendPhotosButton.imageView.setPadding(AndroidUtilities.dp(2.0f), 0, 0, 0);
            this.sendPhotosButton.imageView.setBackgroundResource(C0446R.drawable.attach_send_states);
            this.sendPhotosButton.imageView.setImageResource(C0446R.drawable.attach_send2);
            TextView access$8600 = this.sendPhotosButton.textView;
            Object[] objArr = new Object[1];
            objArr[0] = String.format("(%d)", new Object[]{Integer.valueOf(size)});
            access$8600.setText(LocaleController.formatString("SendItems", C0446R.string.SendItems, objArr));
            TextView access$86002 = this.sendDocumentsButton.textView;
            if (size == 1) {
                str = "SendAsFile";
                i = C0446R.string.SendAsFile;
            } else {
                str = "SendAsFiles";
                i = C0446R.string.SendAsFiles;
            }
            access$86002.setText(LocaleController.getString(str, i));
        }
        if (VERSION.SDK_INT < 23 || getContext().checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == 0) {
            this.progressView.setText(LocaleController.getString("NoPhotos", C0446R.string.NoPhotos));
            this.progressView.setTextSize(20);
            return;
        }
        this.progressView.setText(LocaleController.getString("PermissionStorage", C0446R.string.PermissionStorage));
        this.progressView.setTextSize(16);
    }

    public void setDelegate(ChatAttachViewDelegate chatAttachViewDelegate) {
        this.delegate = chatAttachViewDelegate;
    }

    public void loadGalleryPhotos() {
        if (MediaController.allMediaAlbumEntry == null && VERSION.SDK_INT >= 21) {
            MediaController.loadGalleryPhotosAlbums(0);
        }
    }

    public void init() {
        if (MediaController.allMediaAlbumEntry != null) {
            for (int i = 0; i < Math.min(100, MediaController.allMediaAlbumEntry.photos.size()); i++) {
                ((PhotoEntry) MediaController.allMediaAlbumEntry.photos.get(i)).reset();
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
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.albumsDidLoaded);
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
        return 0;
    }

    private void onRevealAnimationEnd(boolean z) {
        NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(false);
        this.revealAnimationInProgress = false;
        if (z && VERSION.SDK_INT <= 19 && MediaController.allMediaAlbumEntry == null) {
            MediaController.loadGalleryPhotosAlbums(0);
        }
        if (z) {
            checkCamera(true);
            showHint();
        }
    }

    public void checkCamera(boolean z) {
        if (this.baseFragment != null) {
            boolean z2 = this.deviceHasGoodCamera;
            if (!SharedConfig.inappCamera) {
                this.deviceHasGoodCamera = false;
            } else if (VERSION.SDK_INT < 23) {
                CameraController.getInstance().initCamera();
                this.deviceHasGoodCamera = CameraController.getInstance().isCameraInitied();
            } else if (this.baseFragment.getParentActivity().checkSelfPermission("android.permission.CAMERA") != 0) {
                if (z) {
                    this.baseFragment.getParentActivity().requestPermissions(new String[]{"android.permission.CAMERA"}, 17);
                }
                this.deviceHasGoodCamera = false;
            } else {
                CameraController.getInstance().initCamera();
                this.deviceHasGoodCamera = CameraController.getInstance().isCameraInitied();
            }
            if (z2 != this.deviceHasGoodCamera && this.photoAttachAdapter) {
                this.photoAttachAdapter.notifyDataSetChanged();
            }
            if (isShowing() && this.deviceHasGoodCamera && this.baseFragment && this.backDrawable.getAlpha() && !this.revealAnimationInProgress && !this.cameraOpened) {
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
        int i;
        int i2 = 0;
        if (selectedPhotos.isEmpty()) {
            i = 0;
        } else {
            for (Entry value : selectedPhotos.entrySet()) {
                ((PhotoEntry) value.getValue()).reset();
            }
            selectedPhotos.clear();
            selectedPhotosOrder.clear();
            updatePhotosButton();
            i = 1;
        }
        if (!cameraPhotos.isEmpty()) {
            i = cameraPhotos.size();
            while (i2 < i) {
                PhotoEntry photoEntry = (PhotoEntry) cameraPhotos.get(i2);
                new File(photoEntry.path).delete();
                if (photoEntry.imagePath != null) {
                    new File(photoEntry.imagePath).delete();
                }
                if (photoEntry.thumbPath != null) {
                    new File(photoEntry.thumbPath).delete();
                }
                i2++;
            }
            cameraPhotos.clear();
            i = 1;
        }
        if (i != 0) {
            this.photoAttachAdapter.notifyDataSetChanged();
            this.cameraAttachAdapter.notifyDataSetChanged();
        }
    }

    private void setUseRevealAnimation(boolean z) {
        if (!z || (z && VERSION.SDK_INT >= 18 && !AndroidUtilities.isTablet() && VERSION.SDK_INT < 26)) {
            this.useRevealAnimation = z;
        }
    }

    @Keep
    @SuppressLint({"NewApi"})
    protected void setRevealRadius(float f) {
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

    @Keep
    protected float getRevealRadius() {
        return this.revealRadius;
    }

    @SuppressLint({"NewApi"})
    private void startRevealAnimation(boolean z) {
        float measuredHeight;
        final boolean z2 = z;
        this.containerView.setTranslationY(0.0f);
        final AnimatorSet animatorSet = new AnimatorSet();
        View revealView = this.delegate.getRevealView();
        int i = 19;
        int i2 = 2;
        int i3 = 1;
        if (revealView.getVisibility() == 0 && ((ViewGroup) revealView.getParent()).getVisibility() == 0) {
            int[] iArr = new int[2];
            revealView.getLocationInWindow(iArr);
            if (VERSION.SDK_INT <= 19) {
                measuredHeight = (float) ((AndroidUtilities.displaySize.y - r1.containerView.getMeasuredHeight()) - AndroidUtilities.statusBarHeight);
            } else {
                measuredHeight = r1.containerView.getY();
            }
            r1.revealX = iArr[0] + (revealView.getMeasuredWidth() / 2);
            r1.revealY = (int) (((float) (iArr[1] + (revealView.getMeasuredHeight() / 2))) - measuredHeight);
            if (VERSION.SDK_INT <= 19) {
                r1.revealY -= AndroidUtilities.statusBarHeight;
            }
        } else {
            r1.revealX = (AndroidUtilities.displaySize.x / 2) + backgroundPaddingLeft;
            r1.revealY = (int) (((float) AndroidUtilities.displaySize.y) - r1.containerView.getY());
        }
        int i4 = 4;
        r6 = new int[4][];
        r6[1] = new int[]{0, AndroidUtilities.dp(304.0f)};
        r6[2] = new int[]{r1.containerView.getMeasuredWidth(), 0};
        r6[3] = new int[]{r1.containerView.getMeasuredWidth(), AndroidUtilities.dp(304.0f)};
        int i5 = (r1.revealY - r1.scrollOffsetY) + backgroundPaddingTop;
        int i6 = 0;
        int i7 = i6;
        while (i6 < i4) {
            i7 = Math.max(i7, (int) Math.ceil(Math.sqrt((double) (((r1.revealX - r6[i6][0]) * (r1.revealX - r6[i6][0])) + ((i5 - r6[i6][1]) * (i5 - r6[i6][1]))))));
            i6++;
            i4 = 4;
        }
        int measuredWidth = r1.revealX <= r1.containerView.getMeasuredWidth() ? r1.revealX : r1.containerView.getMeasuredWidth();
        Collection arrayList = new ArrayList(3);
        String str = "revealRadius";
        float[] fArr = new float[2];
        fArr[0] = z2 ? 0.0f : (float) i7;
        fArr[1] = z2 ? (float) i7 : 0.0f;
        arrayList.add(ObjectAnimator.ofFloat(r1, str, fArr));
        ColorDrawable colorDrawable = r1.backDrawable;
        String str2 = "alpha";
        int[] iArr2 = new int[1];
        iArr2[0] = z2 ? 51 : 0;
        arrayList.add(ObjectAnimator.ofInt(colorDrawable, str2, iArr2));
        if (VERSION.SDK_INT >= 21) {
            try {
                arrayList.add(ViewAnimationUtils.createCircularReveal(r1.containerView, measuredWidth, r1.revealY, z2 ? 0.0f : (float) i7, z2 ? (float) i7 : 0.0f));
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
            animatorSet.setDuration(320);
        } else if (z2) {
            animatorSet.setDuration(250);
            r1.containerView.setScaleX(1.0f);
            r1.containerView.setScaleY(1.0f);
            r1.containerView.setAlpha(1.0f);
            if (VERSION.SDK_INT <= 19) {
                animatorSet.setStartDelay(20);
            }
        } else {
            animatorSet.setDuration(200);
            r1.containerView.setPivotX((float) (r1.revealX <= r1.containerView.getMeasuredWidth() ? r1.revealX : r1.containerView.getMeasuredWidth()));
            r1.containerView.setPivotY((float) r1.revealY);
            arrayList.add(ObjectAnimator.ofFloat(r1.containerView, "scaleX", new float[]{0.0f}));
            arrayList.add(ObjectAnimator.ofFloat(r1.containerView, "scaleY", new float[]{0.0f}));
            arrayList.add(ObjectAnimator.ofFloat(r1.containerView, "alpha", new float[]{0.0f}));
        }
        animatorSet.playTogether(arrayList);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                if (ChatAttachAlert.this.currentSheetAnimation != null && ChatAttachAlert.this.currentSheetAnimation.equals(animator) != null) {
                    ChatAttachAlert.this.currentSheetAnimation = null;
                    ChatAttachAlert.this.onRevealAnimationEnd(z2);
                    ChatAttachAlert.this.containerView.invalidate();
                    ChatAttachAlert.this.containerView.setLayerType(0, null);
                    if (z2 == null) {
                        try {
                            ChatAttachAlert.this.dismissInternal();
                        } catch (Throwable e) {
                            FileLog.m3e(e);
                        }
                    }
                }
            }

            public void onAnimationCancel(Animator animator) {
                if (ChatAttachAlert.this.currentSheetAnimation != null && animatorSet.equals(animator) != null) {
                    ChatAttachAlert.this.currentSheetAnimation = null;
                }
            }
        });
        if (z2) {
            r1.innerAnimators.clear();
            NotificationCenter.getInstance(r1.currentAccount).setAllowedNotificationsDutingAnimation(new int[]{NotificationCenter.dialogsNeedReload});
            NotificationCenter.getInstance(r1.currentAccount).setAnimationInProgress(true);
            r1.revealAnimationInProgress = true;
            int i8 = VERSION.SDK_INT <= 19 ? 12 : 8;
            i4 = 0;
            while (i4 < i8) {
                AnimatorSet animatorSet2;
                float[] fArr2;
                float f;
                if (VERSION.SDK_INT <= i) {
                    if (i4 < 8) {
                        r1.views[i4].setScaleX(0.1f);
                        r1.views[i4].setScaleY(0.1f);
                    }
                    r1.views[i4].setAlpha(0.0f);
                } else {
                    r1.views[i4].setScaleX(0.7f);
                    r1.views[i4].setScaleY(0.7f);
                }
                InnerAnimator innerAnimator = new InnerAnimator();
                i7 = r1.views[i4].getLeft() + (r1.views[i4].getMeasuredWidth() / i2);
                int top = (r1.views[i4].getTop() + r1.attachView.getTop()) + (r1.views[i4].getMeasuredHeight() / i2);
                float sqrt = (float) Math.sqrt((double) (((r1.revealX - i7) * (r1.revealX - i7)) + ((r1.revealY - top) * (r1.revealY - top))));
                measuredHeight = ((float) (r1.revealY - top)) / sqrt;
                r1.views[i4].setPivotX(((float) (r1.views[i4].getMeasuredWidth() / i2)) + ((((float) (r1.revealX - i7)) / sqrt) * ((float) AndroidUtilities.dp(20.0f))));
                r1.views[i4].setPivotY(((float) (r1.views[i4].getMeasuredHeight() / i2)) + (measuredHeight * ((float) AndroidUtilities.dp(20.0f))));
                innerAnimator.startRadius = sqrt - ((float) AndroidUtilities.dp(81.0f));
                r1.views[i4].setTag(C0446R.string.AppName, Integer.valueOf(i3));
                Collection arrayList2 = new ArrayList();
                if (i4 < 8) {
                    arrayList2.add(ObjectAnimator.ofFloat(r1.views[i4], "scaleX", new float[]{0.7f, 1.05f}));
                    arrayList2.add(ObjectAnimator.ofFloat(r1.views[i4], "scaleY", new float[]{0.7f, 1.05f}));
                    animatorSet2 = new AnimatorSet();
                    Animator[] animatorArr = new Animator[i2];
                    fArr2 = new float[i3];
                    fArr2[0] = 1.0f;
                    animatorArr[0] = ObjectAnimator.ofFloat(r1.views[i4], "scaleX", fArr2);
                    fArr2 = new float[i3];
                    fArr2[0] = 1.0f;
                    animatorArr[i3] = ObjectAnimator.ofFloat(r1.views[i4], "scaleY", fArr2);
                    animatorSet2.playTogether(animatorArr);
                    animatorSet2.setDuration(100);
                    animatorSet2.setInterpolator(r1.decelerateInterpolator);
                } else {
                    animatorSet2 = null;
                }
                if (VERSION.SDK_INT <= 19) {
                    fArr2 = new float[i3];
                    top = 0;
                    f = 1.0f;
                    fArr2[0] = 1.0f;
                    arrayList2.add(ObjectAnimator.ofFloat(r1.views[i4], "alpha", fArr2));
                } else {
                    top = 0;
                    f = 1.0f;
                }
                innerAnimator.animatorSet = new AnimatorSet();
                innerAnimator.animatorSet.playTogether(arrayList2);
                innerAnimator.animatorSet.setDuration(150);
                innerAnimator.animatorSet.setInterpolator(r1.decelerateInterpolator);
                innerAnimator.animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        if (animatorSet2 != null) {
                            animatorSet2.start();
                        }
                    }
                });
                r1.innerAnimators.add(innerAnimator);
                i4++;
                i = 19;
                int i9 = top;
                float f2 = f;
                i2 = 2;
                i3 = 1;
            }
        }
        r1.currentSheetAnimation = animatorSet;
        animatorSet.start();
    }

    public void dismissInternal() {
        if (this.containerView != null) {
            this.containerView.setVisibility(4);
        }
        super.dismissInternal();
    }

    protected boolean onCustomOpenAnimation() {
        if (this.baseFragment != null) {
            Chat currentChat = this.baseFragment.getCurrentChat();
            if (ChatObject.isChannel(currentChat)) {
                boolean z;
                int i;
                float f;
                AttachButton attachButton;
                FrameLayout frameLayout;
                if (currentChat.banned_rights != null) {
                    if (currentChat.banned_rights.send_media) {
                        z = false;
                        this.mediaEnabled = z;
                        i = 0;
                        while (true) {
                            f = 0.2f;
                            if (i < 5) {
                                break;
                            }
                            attachButton = (AttachButton) this.attachButtons.get(i);
                            if (this.mediaEnabled) {
                                f = 1.0f;
                            }
                            attachButton.setAlpha(f);
                            ((AttachButton) this.attachButtons.get(i)).setEnabled(this.mediaEnabled);
                            i++;
                        }
                        this.attachPhotoRecyclerView.setAlpha(this.mediaEnabled ? 1.0f : 0.2f);
                        this.attachPhotoRecyclerView.setEnabled(this.mediaEnabled);
                        if (!this.mediaEnabled) {
                            if (AndroidUtilities.isBannedForever(currentChat.banned_rights.until_date)) {
                                this.mediaBanTooltip.setText(LocaleController.formatString("AttachMediaRestricted", C0446R.string.AttachMediaRestricted, LocaleController.formatDateForBan((long) currentChat.banned_rights.until_date)));
                            } else {
                                this.mediaBanTooltip.setText(LocaleController.formatString("AttachMediaRestrictedForever", C0446R.string.AttachMediaRestrictedForever, new Object[0]));
                            }
                        }
                        this.mediaBanTooltip.setVisibility(this.mediaEnabled ? 4 : 0);
                        if (this.cameraView != null) {
                            this.cameraView.setAlpha(this.mediaEnabled ? 1.0f : 0.2f);
                            this.cameraView.setEnabled(this.mediaEnabled);
                        }
                        if (this.cameraIcon != null) {
                            frameLayout = this.cameraIcon;
                            if (this.mediaEnabled) {
                                f = 1.0f;
                            }
                            frameLayout.setAlpha(f);
                            this.cameraIcon.setEnabled(this.mediaEnabled);
                        }
                    }
                }
                z = true;
                this.mediaEnabled = z;
                i = 0;
                while (true) {
                    f = 0.2f;
                    if (i < 5) {
                        break;
                    }
                    attachButton = (AttachButton) this.attachButtons.get(i);
                    if (this.mediaEnabled) {
                        f = 1.0f;
                    }
                    attachButton.setAlpha(f);
                    ((AttachButton) this.attachButtons.get(i)).setEnabled(this.mediaEnabled);
                    i++;
                }
                if (this.mediaEnabled) {
                }
                this.attachPhotoRecyclerView.setAlpha(this.mediaEnabled ? 1.0f : 0.2f);
                this.attachPhotoRecyclerView.setEnabled(this.mediaEnabled);
                if (this.mediaEnabled) {
                    if (AndroidUtilities.isBannedForever(currentChat.banned_rights.until_date)) {
                        this.mediaBanTooltip.setText(LocaleController.formatString("AttachMediaRestricted", C0446R.string.AttachMediaRestricted, LocaleController.formatDateForBan((long) currentChat.banned_rights.until_date)));
                    } else {
                        this.mediaBanTooltip.setText(LocaleController.formatString("AttachMediaRestrictedForever", C0446R.string.AttachMediaRestrictedForever, new Object[0]));
                    }
                }
                if (this.mediaEnabled) {
                }
                this.mediaBanTooltip.setVisibility(this.mediaEnabled ? 4 : 0);
                if (this.cameraView != null) {
                    if (this.mediaEnabled) {
                    }
                    this.cameraView.setAlpha(this.mediaEnabled ? 1.0f : 0.2f);
                    this.cameraView.setEnabled(this.mediaEnabled);
                }
                if (this.cameraIcon != null) {
                    frameLayout = this.cameraIcon;
                    if (this.mediaEnabled) {
                        f = 1.0f;
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

    protected boolean onCustomCloseAnimation() {
        if (!this.useRevealAnimation) {
            return false;
        }
        this.backDrawable.setAlpha(51);
        startRevealAnimation(false);
        return true;
    }

    public void dismissWithButtonClick(int i) {
        super.dismissWithButtonClick(i);
        i = (i == 0 || i == 2) ? 0 : 1;
        hideCamera(i);
    }

    protected boolean canDismissWithTouchOutside() {
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
