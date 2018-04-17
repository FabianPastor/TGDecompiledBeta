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
import java.util.HashMap;
import java.util.Map.Entry;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
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
import org.telegram.messenger.beta.R;
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
                    MotionEvent event = MotionEvent.obtain(0, 0, 3, 0.0f, 0.0f, 0);
                    AttachBotButton.this.onTouchEvent(event);
                    event.recycle();
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

        static /* synthetic */ int access$1204(AttachBotButton x0) {
            int i = x0.pressCount + 1;
            x0.pressCount = i;
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

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(85.0f), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(100.0f), NUM));
        }

        private void onLongPress() {
            if (ChatAttachAlert.this.baseFragment != null) {
                if (this.currentUser != null) {
                    Builder builder = new Builder(getContext());
                    builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                    builder.setMessage(LocaleController.formatString("ChatHintsDelete", R.string.ChatHintsDelete, ContactsController.formatName(this.currentUser.first_name, this.currentUser.last_name)));
                    builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new C11121());
                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                    builder.show();
                }
            }
        }

        public void setUser(User user) {
            if (user != null) {
                this.currentUser = user;
                TLObject photo = null;
                this.nameTextView.setText(ContactsController.formatName(user.first_name, user.last_name));
                this.avatarDrawable.setInfo(user);
                if (!(user == null || user.photo == null)) {
                    photo = user.photo.photo_small;
                }
                this.imageView.setImage(photo, "50_50", this.avatarDrawable);
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

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(85.0f), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(90.0f), NUM));
        }

        public void setTextAndIcon(CharSequence text, Drawable drawable) {
            this.textView.setText(text);
            this.imageView.setBackgroundDrawable(drawable);
        }

        public boolean hasOverlappingRendering() {
            return false;
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

        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
            outRect.left = 0;
            outRect.right = 0;
            outRect.top = 0;
            outRect.bottom = 0;
        }
    }

    /* renamed from: org.telegram.ui.Components.ChatAttachAlert$4 */
    class C20474 extends OnScrollListener {
        C20474() {
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
    class C20487 implements OnItemClickListener {
        C20487() {
        }

        public void onItemClick(View view, int position) {
            if (ChatAttachAlert.this.baseFragment != null) {
                if (ChatAttachAlert.this.baseFragment.getParentActivity() != null) {
                    if (ChatAttachAlert.this.deviceHasGoodCamera) {
                        if (position == 0) {
                            ChatAttachAlert.this.openCamera(true);
                        }
                    }
                    if (ChatAttachAlert.this.deviceHasGoodCamera) {
                        position--;
                    }
                    ArrayList<Object> arrayList = ChatAttachAlert.this.getAllPhotosArray();
                    if (position >= 0) {
                        if (position < arrayList.size()) {
                            PhotoViewer.getInstance().setParentActivity(ChatAttachAlert.this.baseFragment.getParentActivity());
                            PhotoViewer.getInstance().setParentAlert(ChatAttachAlert.this);
                            PhotoViewer.getInstance().openPhotoForSelect(arrayList, position, 0, ChatAttachAlert.this.photoViewerProvider, ChatAttachAlert.this.baseFragment);
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

        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            ChatAttachAlert.this.checkCameraViewPosition();
        }
    }

    private class BasePhotoProvider extends EmptyPhotoViewerProvider {
        private BasePhotoProvider() {
        }

        public boolean isPhotoChecked(int index) {
            PhotoEntry photoEntry = ChatAttachAlert.this.getPhotoEntryAtPosition(index);
            return photoEntry != null && ChatAttachAlert.selectedPhotos.containsKey(Integer.valueOf(photoEntry.imageId));
        }

        public int setPhotoChecked(int index, VideoEditedInfo videoEditedInfo) {
            PhotoEntry photoEntry = ChatAttachAlert.this.getPhotoEntryAtPosition(index);
            if (photoEntry == null) {
                return -1;
            }
            int a;
            boolean add = true;
            int access$200 = ChatAttachAlert.this.addToSelectedPhotos(photoEntry, -1);
            int num = access$200;
            if (access$200 == -1) {
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
                    ((PhotoAttachPhotoCell) view).setChecked(num, add, false);
                    break;
                }
            }
            count = ChatAttachAlert.this.cameraPhotoRecyclerView.getChildCount();
            for (a = 0; a < count; a++) {
                view = ChatAttachAlert.this.cameraPhotoRecyclerView.getChildAt(a);
                if ((view instanceof PhotoAttachPhotoCell) && ((Integer) view.getTag()).intValue() == index) {
                    ((PhotoAttachPhotoCell) view).setChecked(num, add, false);
                    break;
                }
            }
            ChatAttachAlert.this.updatePhotosButton();
            return num;
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

        public int getPhotoIndex(int index) {
            PhotoEntry photoEntry = ChatAttachAlert.this.getPhotoEntryAtPosition(index);
            if (photoEntry == null) {
                return -1;
            }
            return ChatAttachAlert.selectedPhotosOrder.indexOf(Integer.valueOf(photoEntry.imageId));
        }
    }

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
                    frameLayout.setBackgroundColor(-986896);
                    frameLayout.addView(new ShadowSectionCell(this.mContext), LayoutHelper.createFrame(-1, -1.0f));
                    view = frameLayout;
                    break;
                default:
                    frameLayout = new FrameLayout(this.mContext) {
                        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                            int diff = ((right - left) - AndroidUtilities.dp(360.0f)) / 3;
                            for (int a = 0; a < 4; a++) {
                                int x = AndroidUtilities.dp(10.0f) + ((a % 4) * (AndroidUtilities.dp(85.0f) + diff));
                                View child = getChildAt(a);
                                child.layout(x, 0, child.getMeasuredWidth() + x, child.getMeasuredHeight());
                            }
                        }
                    };
                    for (int a = 0; a < 4; a++) {
                        frameLayout.addView(new AttachBotButton(this.mContext));
                    }
                    View view2 = frameLayout;
                    frameLayout.setLayoutParams(new LayoutParams(-1, AndroidUtilities.dp(100.0f)));
                    view = view2;
                    break;
            }
            return new Holder(view);
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            if (position > 1) {
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
            return 1 + (!DataQuery.getInstance(ChatAttachAlert.this.currentAccount).inlineBots.isEmpty() ? ((int) Math.ceil((double) (((float) DataQuery.getInstance(ChatAttachAlert.this.currentAccount).inlineBots.size()) / 4.0f))) + 1 : 0);
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

    private class PhotoAttachAdapter extends SelectionAdapter {
        private Context mContext;
        private boolean needCamera;
        private ArrayList<Holder> viewsCache = new ArrayList(8);

        /* renamed from: org.telegram.ui.Components.ChatAttachAlert$PhotoAttachAdapter$1 */
        class C20501 implements PhotoAttachPhotoCellDelegate {
            C20501() {
            }

            public void onCheckClick(PhotoAttachPhotoCell v) {
                if (ChatAttachAlert.this.mediaEnabled) {
                    int index = ((Integer) v.getTag()).intValue();
                    PhotoEntry photoEntry = v.getPhotoEntry();
                    boolean added = ChatAttachAlert.selectedPhotos.containsKey(Integer.valueOf(photoEntry.imageId)) ^ true;
                    v.setChecked(added ? ChatAttachAlert.selectedPhotosOrder.size() : -1, added, true);
                    ChatAttachAlert.this.addToSelectedPhotos(photoEntry, index);
                    int updateIndex = index;
                    if (PhotoAttachAdapter.this == ChatAttachAlert.this.cameraAttachAdapter) {
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

        public PhotoAttachAdapter(Context context, boolean camera) {
            this.mContext = context;
            this.needCamera = camera;
            for (int a = 0; a < 8; a++) {
                this.viewsCache.add(createHolder());
            }
        }

        public Holder createHolder() {
            PhotoAttachPhotoCell cell = new PhotoAttachPhotoCell(this.mContext);
            cell.setDelegate(new C20501());
            return new Holder(cell);
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            boolean z = false;
            if (this.needCamera && ChatAttachAlert.this.deviceHasGoodCamera) {
                if (position == 0) {
                    if (!this.needCamera || !ChatAttachAlert.this.deviceHasGoodCamera || position != 0) {
                        return;
                    }
                    if (ChatAttachAlert.this.cameraView == null || !ChatAttachAlert.this.cameraView.isInitied()) {
                        holder.itemView.setVisibility(0);
                        return;
                    } else {
                        holder.itemView.setVisibility(4);
                        return;
                    }
                }
            }
            if (this.needCamera && ChatAttachAlert.this.deviceHasGoodCamera) {
                position--;
            }
            PhotoAttachPhotoCell cell = holder.itemView;
            PhotoEntry photoEntry = ChatAttachAlert.this.getPhotoEntryAtPosition(position);
            cell.setPhotoEntry(photoEntry, this.needCamera, position == getItemCount() - 1);
            cell.setChecked(ChatAttachAlert.selectedPhotosOrder.indexOf(Integer.valueOf(photoEntry.imageId)), ChatAttachAlert.selectedPhotos.containsKey(Integer.valueOf(photoEntry.imageId)), false);
            cell.getImageView().setTag(Integer.valueOf(position));
            cell.setTag(Integer.valueOf(position));
            if (this == ChatAttachAlert.this.cameraAttachAdapter && ChatAttachAlert.this.cameraPhotoLayoutManager.getOrientation() == 1) {
                z = true;
            }
            cell.setIsVertical(z);
        }

        public boolean isEnabled(ViewHolder holder) {
            return false;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Holder holder;
            if (viewType == 1) {
                holder = new Holder(new PhotoAttachCameraCell(this.mContext));
            } else if (this.viewsCache.isEmpty()) {
                holder = createHolder();
            } else {
                holder = (Holder) this.viewsCache.get(0);
                this.viewsCache.remove(0);
            }
            return holder;
        }

        public int getItemCount() {
            int count = 0;
            if (this.needCamera && ChatAttachAlert.this.deviceHasGoodCamera) {
                count = 0 + 1;
            }
            count += ChatAttachAlert.cameraPhotos.size();
            if (MediaController.allMediaAlbumEntry != null) {
                return count + MediaController.allMediaAlbumEntry.photos.size();
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

    /* renamed from: org.telegram.ui.Components.ChatAttachAlert$1 */
    class C23541 extends BasePhotoProvider {
        C23541() {
            super();
        }

        public PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, FileLocation fileLocation, int index) {
            PhotoAttachPhotoCell cell = ChatAttachAlert.this.getCellForIndex(index);
            if (cell == null) {
                return null;
            }
            coords = new int[2];
            cell.getImageView().getLocationInWindow(coords);
            coords[0] = coords[0] - ChatAttachAlert.this.getLeftInset();
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
                        cell.getImageView().setImage(photoEntry.thumbPath, null, cell.getContext().getResources().getDrawable(R.drawable.nophotos));
                    } else if (photoEntry.path != null) {
                        cell.getImageView().setOrientation(photoEntry.orientation, true);
                        BackupImageView imageView;
                        StringBuilder stringBuilder;
                        if (photoEntry.isVideo) {
                            imageView = cell.getImageView();
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("vthumb://");
                            stringBuilder.append(photoEntry.imageId);
                            stringBuilder.append(":");
                            stringBuilder.append(photoEntry.path);
                            imageView.setImage(stringBuilder.toString(), null, cell.getContext().getResources().getDrawable(R.drawable.nophotos));
                        } else {
                            imageView = cell.getImageView();
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("thumb://");
                            stringBuilder.append(photoEntry.imageId);
                            stringBuilder.append(":");
                            stringBuilder.append(photoEntry.path);
                            imageView.setImage(stringBuilder.toString(), null, cell.getContext().getResources().getDrawable(R.drawable.nophotos));
                        }
                    } else {
                        cell.getImageView().setImageResource(R.drawable.nophotos);
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

    static /* synthetic */ int access$7010() {
        int i = lastImageId;
        lastImageId = i - 1;
        return i;
    }

    private void updateCheckedPhotoIndices() {
        int count = this.attachPhotoRecyclerView.getChildCount();
        int a = 0;
        for (int a2 = 0; a2 < count; a2++) {
            View view = this.attachPhotoRecyclerView.getChildAt(a2);
            if (view instanceof PhotoAttachPhotoCell) {
                PhotoAttachPhotoCell cell = (PhotoAttachPhotoCell) view;
                PhotoEntry photoEntry = getPhotoEntryAtPosition(((Integer) cell.getTag()).intValue());
                if (photoEntry != null) {
                    cell.setNum(selectedPhotosOrder.indexOf(Integer.valueOf(photoEntry.imageId)));
                }
            }
        }
        count = this.cameraPhotoRecyclerView.getChildCount();
        while (a < count) {
            View view2 = this.cameraPhotoRecyclerView.getChildAt(a);
            if (view2 instanceof PhotoAttachPhotoCell) {
                PhotoAttachPhotoCell cell2 = (PhotoAttachPhotoCell) view2;
                PhotoEntry photoEntry2 = getPhotoEntryAtPosition(((Integer) cell2.getTag()).intValue());
                if (photoEntry2 != null) {
                    cell2.setNum(selectedPhotosOrder.indexOf(Integer.valueOf(photoEntry2.imageId)));
                }
            }
            a++;
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
        position -= cameraCount;
        if (position < MediaController.allMediaAlbumEntry.photos.size()) {
            return (PhotoEntry) MediaController.allMediaAlbumEntry.photos.get(position);
        }
        return null;
    }

    private ArrayList<Object> getAllPhotosArray() {
        ArrayList<Object> arrayList;
        if (MediaController.allMediaAlbumEntry != null) {
            if (cameraPhotos.isEmpty()) {
                arrayList = MediaController.allMediaAlbumEntry.photos;
            } else {
                arrayList = new ArrayList(MediaController.allMediaAlbumEntry.photos.size() + cameraPhotos.size());
                arrayList.addAll(cameraPhotos);
                arrayList.addAll(MediaController.allMediaAlbumEntry.photos);
                return arrayList;
            }
        } else if (cameraPhotos.isEmpty()) {
            return new ArrayList(0);
        } else {
            arrayList = cameraPhotos;
        }
        return arrayList;
    }

    public ChatAttachAlert(Context context, ChatActivity parentFragment) {
        Context context2 = context;
        final ChatActivity chatActivity = parentFragment;
        super(context2, false);
        this.baseFragment = chatActivity;
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
        r0.shadowDrawable = context.getResources().getDrawable(R.drawable.sheet_shadow).mutate();
        ViewGroup c23622 = new RecyclerListView(context2) {
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
                boolean z = true;
                if (ChatAttachAlert.this.cameraAnimationInProgress) {
                    return true;
                }
                if (ChatAttachAlert.this.cameraOpened) {
                    return ChatAttachAlert.this.processTouchEvent(event);
                }
                if (ChatAttachAlert.this.isDismissed() || !super.onTouchEvent(event)) {
                    z = false;
                }
                return z;
            }

            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int height = MeasureSpec.getSize(heightMeasureSpec);
                if (VERSION.SDK_INT >= 21) {
                    height -= AndroidUtilities.statusBarHeight;
                }
                int contentSize = (ChatAttachAlert.backgroundPaddingTop + AndroidUtilities.dp(294.0f)) + (DataQuery.getInstance(ChatAttachAlert.this.currentAccount).inlineBots.isEmpty() ? 0 : (((int) Math.ceil((double) (((float) DataQuery.getInstance(ChatAttachAlert.this.currentAccount).inlineBots.size()) / 4.0f))) * AndroidUtilities.dp(100.0f)) + AndroidUtilities.dp(12.0f));
                int padding = contentSize == AndroidUtilities.dp(294.0f) ? 0 : Math.max(0, height - AndroidUtilities.dp(294.0f));
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
                int lastVisibleItemPosition2 = lastVisibleItemPosition;
                int lastVisibleItemPositionTop2 = lastVisibleItemPositionTop;
                if (lastVisibleItemPosition2 >= 0 && height - r6.lastHeight != 0) {
                    newPosition = lastVisibleItemPosition2;
                    newTop = ((lastVisibleItemPositionTop2 + height) - r6.lastHeight) - getPaddingTop();
                }
                int newPosition2 = newPosition;
                lastVisibleItemPositionTop = newTop;
                super.onLayout(changed, left, top, right, bottom);
                if (newPosition2 != -1) {
                    ChatAttachAlert.this.ignoreLayout = true;
                    ChatAttachAlert.this.layoutManager.scrollToPositionWithOffset(newPosition2, lastVisibleItemPositionTop);
                    super.onLayout(false, left, top, right, bottom);
                    ChatAttachAlert.this.ignoreLayout = false;
                } else {
                    int i = newPosition2;
                }
                r6.lastHeight = height;
                r6.lastWidth = width;
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
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(294.0f), NUM));
            }

            protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                int width = right - left;
                int height = bottom - top;
                int t = AndroidUtilities.dp(NUM);
                int a = 0;
                ChatAttachAlert.this.attachPhotoRecyclerView.layout(0, t, width, ChatAttachAlert.this.attachPhotoRecyclerView.getMeasuredHeight() + t);
                ChatAttachAlert.this.progressView.layout(0, t, width, ChatAttachAlert.this.progressView.getMeasuredHeight() + t);
                ChatAttachAlert.this.lineView.layout(0, AndroidUtilities.dp(96.0f), width, AndroidUtilities.dp(96.0f) + ChatAttachAlert.this.lineView.getMeasuredHeight());
                ChatAttachAlert.this.hintTextView.layout((width - ChatAttachAlert.this.hintTextView.getMeasuredWidth()) - AndroidUtilities.dp(5.0f), (height - ChatAttachAlert.this.hintTextView.getMeasuredHeight()) - AndroidUtilities.dp(5.0f), width - AndroidUtilities.dp(5.0f), height - AndroidUtilities.dp(5.0f));
                int x = (width - ChatAttachAlert.this.mediaBanTooltip.getMeasuredWidth()) / 2;
                int y = ((ChatAttachAlert.this.attachPhotoRecyclerView.getMeasuredHeight() - ChatAttachAlert.this.mediaBanTooltip.getMeasuredHeight()) / 2) + t;
                ChatAttachAlert.this.mediaBanTooltip.layout(x, y, ChatAttachAlert.this.mediaBanTooltip.getMeasuredWidth() + x, ChatAttachAlert.this.mediaBanTooltip.getMeasuredHeight() + y);
                int diff = (width - AndroidUtilities.dp(360.0f)) / 3;
                while (a < 8) {
                    y = AndroidUtilities.dp((float) (105 + (95 * (a / 4))));
                    x = AndroidUtilities.dp(10.0f) + ((a % 4) * (AndroidUtilities.dp(85.0f) + diff));
                    ChatAttachAlert.this.views[a].layout(x, y, ChatAttachAlert.this.views[a].getMeasuredWidth() + x, ChatAttachAlert.this.views[a].getMeasuredHeight() + y);
                    a++;
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
            r0.progressView.setText(LocaleController.getString("NoPhotos", R.string.NoPhotos));
            r0.progressView.setTextSize(20);
        } else {
            r0.progressView.setText(LocaleController.getString("PermissionStorage", R.string.PermissionStorage));
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
        CharSequence[] items = new CharSequence[]{LocaleController.getString("ChatCamera", R.string.ChatCamera), LocaleController.getString("ChatGallery", R.string.ChatGallery), LocaleController.getString("ChatVideo", R.string.ChatVideo), LocaleController.getString("AttachMusic", R.string.AttachMusic), LocaleController.getString("ChatDocument", R.string.ChatDocument), LocaleController.getString("AttachContact", R.string.AttachContact), LocaleController.getString("ChatLocation", R.string.ChatLocation), TtmlNode.ANONYMOUS_REGION_ID};
        int a = 0;
        while (a < i) {
            AttachButton attachButton = new AttachButton(context2);
            r0.attachButtons.add(attachButton);
            attachButton.setTextAndIcon(items[a], Theme.chat_attachButtonDrawables[a]);
            r0.attachView.addView(attachButton, LayoutHelper.createFrame(85, 90, 51));
            attachButton.setTag(Integer.valueOf(a));
            r0.views[a] = attachButton;
            if (a == 7) {
                r0.sendPhotosButton = attachButton;
                r0.sendPhotosButton.imageView.setPadding(0, AndroidUtilities.dp(4.0f), 0, 0);
            } else if (a == 4) {
                r0.sendDocumentsButton = attachButton;
            }
            attachButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (!ChatAttachAlert.this.buttonPressed) {
                        ChatAttachAlert.this.buttonPressed = true;
                        ChatAttachAlert.this.delegate.didPressedButton(((Integer) v.getTag()).intValue());
                    }
                }
            });
            a++;
            i = 8;
        }
        r0.hintTextView = new TextView(context2);
        r0.hintTextView.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(3.0f), Theme.getColor(Theme.key_chat_gifSaveHintBackground)));
        r0.hintTextView.setTextColor(Theme.getColor(Theme.key_chat_gifSaveHintText));
        r0.hintTextView.setTextSize(1, 14.0f);
        r0.hintTextView.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
        r0.hintTextView.setText(LocaleController.getString("AttachBotsHelp", R.string.AttachBotsHelp));
        r0.hintTextView.setGravity(16);
        r0.hintTextView.setVisibility(4);
        r0.hintTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.scroll_tip, 0, 0, 0);
        r0.hintTextView.setCompoundDrawablePadding(AndroidUtilities.dp(8.0f));
        r0.attachView.addView(r0.hintTextView, LayoutHelper.createFrame(-2, 32.0f, 85, 5.0f, 0.0f, 5.0f, 5.0f));
        if (r0.loading) {
            r0.progressView.showProgress();
        } else {
            r0.progressView.showTextView();
        }
        r0.recordTime = new TextView(context2);
        r0.recordTime.setBackgroundResource(R.drawable.system);
        r0.recordTime.getBackground().setColorFilter(new PorterDuffColorFilter(NUM, Mode.MULTIPLY));
        r0.recordTime.setTextSize(1, 15.0f);
        r0.recordTime.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        r0.recordTime.setAlpha(0.0f);
        r0.recordTime.setTextColor(-1);
        r0.recordTime.setPadding(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(5.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(5.0f));
        r0.container.addView(r0.recordTime, LayoutHelper.createFrame(-2, -2.0f, 49, 0.0f, 16.0f, 0.0f, 0.0f));
        r0.cameraPanel = new FrameLayout(context2) {
            protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                int measuredWidth;
                int cx2;
                int cy;
                int cx = getMeasuredWidth() / 2;
                int cy2 = getMeasuredHeight() / 2;
                ChatAttachAlert.this.shutterButton.layout(cx - (ChatAttachAlert.this.shutterButton.getMeasuredWidth() / 2), cy2 - (ChatAttachAlert.this.shutterButton.getMeasuredHeight() / 2), (ChatAttachAlert.this.shutterButton.getMeasuredWidth() / 2) + cx, (ChatAttachAlert.this.shutterButton.getMeasuredHeight() / 2) + cy2);
                if (getMeasuredWidth() == AndroidUtilities.dp(100.0f)) {
                    measuredWidth = getMeasuredWidth() / 2;
                    cx2 = measuredWidth;
                    cx = measuredWidth;
                    measuredWidth = ((cy2 / 2) + cy2) + AndroidUtilities.dp(17.0f);
                    cy = (cy2 / 2) - AndroidUtilities.dp(17.0f);
                } else {
                    cx2 = AndroidUtilities.dp(17.0f) + ((cx / 2) + cx);
                    cx = (cx / 2) - AndroidUtilities.dp(17.0f);
                    measuredWidth = getMeasuredHeight() / 2;
                    cy = measuredWidth;
                }
                ChatAttachAlert.this.switchCameraButton.layout(cx2 - (ChatAttachAlert.this.switchCameraButton.getMeasuredWidth() / 2), measuredWidth - (ChatAttachAlert.this.switchCameraButton.getMeasuredHeight() / 2), (ChatAttachAlert.this.switchCameraButton.getMeasuredWidth() / 2) + cx2, (ChatAttachAlert.this.switchCameraButton.getMeasuredHeight() / 2) + measuredWidth);
                for (cy2 = 0; cy2 < 2; cy2++) {
                    ChatAttachAlert.this.flashModeButton[cy2].layout(cx - (ChatAttachAlert.this.flashModeButton[cy2].getMeasuredWidth() / 2), cy - (ChatAttachAlert.this.flashModeButton[cy2].getMeasuredHeight() / 2), (ChatAttachAlert.this.flashModeButton[cy2].getMeasuredWidth() / 2) + cx, (ChatAttachAlert.this.flashModeButton[cy2].getMeasuredHeight() / 2) + cy);
                }
            }
        };
        r0.cameraPanel.setVisibility(8);
        r0.cameraPanel.setAlpha(0.0f);
        r0.container.addView(r0.cameraPanel, LayoutHelper.createFrame(-1, 100, 83));
        r0.counterTextView = new TextView(context2);
        r0.counterTextView.setBackgroundResource(R.drawable.photos_rounded);
        r0.counterTextView.setVisibility(8);
        r0.counterTextView.setTextColor(-1);
        r0.counterTextView.setGravity(17);
        r0.counterTextView.setPivotX(0.0f);
        r0.counterTextView.setPivotY(0.0f);
        r0.counterTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        r0.counterTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.photos_arrow, 0);
        r0.counterTextView.setCompoundDrawablePadding(AndroidUtilities.dp(4.0f));
        r0.counterTextView.setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f), 0);
        r0.container.addView(r0.counterTextView, LayoutHelper.createFrame(-2, 38.0f, 51, 0.0f, 0.0f, 0.0f, 116.0f));
        r0.counterTextView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
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

                public void onFinishVideoRecording(String thumbPath, long duration) {
                    if (AnonymousClass13.this.outputFile != null) {
                        if (ChatAttachAlert.this.baseFragment != null) {
                            ChatAttachAlert.mediaFromExternalCamera = false;
                            PhotoEntry photoEntry = new PhotoEntry(0, ChatAttachAlert.access$7010(), 0, AnonymousClass13.this.outputFile.getAbsolutePath(), 0, true);
                            photoEntry.duration = (int) duration;
                            photoEntry.thumbPath = thumbPath;
                            ChatAttachAlert.this.openPhotoViewer(photoEntry, false, false);
                        }
                    }
                }
            }

            public boolean shutterLongPressed() {
                if (!(ChatAttachAlert.this.mediaCaptured || ChatAttachAlert.this.takingPhoto || ChatAttachAlert.this.baseFragment == null || ChatAttachAlert.this.baseFragment.getParentActivity() == null)) {
                    if (ChatAttachAlert.this.cameraView != null) {
                        if (VERSION.SDK_INT < 23 || ChatAttachAlert.this.baseFragment.getParentActivity().checkSelfPermission("android.permission.RECORD_AUDIO") == 0) {
                            for (int a = 0; a < 2; a++) {
                                ChatAttachAlert.this.flashModeButton[a].setAlpha(0.0f);
                            }
                            ChatAttachAlert.this.switchCameraButton.setAlpha(0.0f);
                            this.outputFile = AndroidUtilities.generateVideoPath();
                            ChatAttachAlert.this.recordTime.setAlpha(1.0f);
                            ChatAttachAlert.this.recordTime.setText(String.format("%02d:%02d", new Object[]{Integer.valueOf(0), Integer.valueOf(0)}));
                            ChatAttachAlert.this.videoRecordTime = 0;
                            ChatAttachAlert.this.videoRecordRunnable = new C11031();
                            AndroidUtilities.lockOrientation(chatActivity.getParentActivity());
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
                        final File cameraFile = AndroidUtilities.generatePicturePath();
                        final boolean sameTakePictureOrientation = ChatAttachAlert.this.cameraView.getCameraSession().isSameTakePictureOrientation();
                        ChatAttachAlert.this.takingPhoto = CameraController.getInstance().takePicture(cameraFile, ChatAttachAlert.this.cameraView.getCameraSession(), new Runnable() {
                            public void run() {
                                ChatAttachAlert.this.takingPhoto = false;
                                if (cameraFile != null) {
                                    if (ChatAttachAlert.this.baseFragment != null) {
                                        int orientation = 0;
                                        try {
                                            int exif = new ExifInterface(cameraFile.getAbsolutePath()).getAttributeInt("Orientation", 1);
                                            if (exif == 3) {
                                                orientation = 180;
                                            } else if (exif == 6) {
                                                orientation = 90;
                                            } else if (exif == 8) {
                                                orientation = 270;
                                            }
                                        } catch (Throwable e) {
                                            FileLog.m3e(e);
                                        }
                                        ChatAttachAlert.mediaFromExternalCamera = false;
                                        ChatAttachAlert.this.openPhotoViewer(new PhotoEntry(0, ChatAttachAlert.access$7010(), 0, cameraFile.getAbsolutePath(), orientation, false), sameTakePictureOrientation, false);
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
                    ImageView access$5800 = ChatAttachAlert.this.switchCameraButton;
                    int i = (ChatAttachAlert.this.cameraView == null || !ChatAttachAlert.this.cameraView.isFrontface()) ? R.drawable.camera_revert2 : R.drawable.camera_revert1;
                    access$5800.setImageResource(i);
                    ObjectAnimator.ofFloat(ChatAttachAlert.this.switchCameraButton, "scaleX", new float[]{1.0f}).setDuration(100).start();
                }
            }

            public void onClick(View v) {
                if (!(ChatAttachAlert.this.takingPhoto || ChatAttachAlert.this.cameraView == null)) {
                    if (ChatAttachAlert.this.cameraView.isInitied()) {
                        ChatAttachAlert.this.cameraInitied = false;
                        ChatAttachAlert.this.cameraView.switchCamera();
                        ObjectAnimator animator = ObjectAnimator.ofFloat(ChatAttachAlert.this.switchCameraButton, "scaleX", new float[]{0.0f}).setDuration(100);
                        animator.addListener(new C11061());
                        animator.start();
                    }
                }
            }
        });
        for (int a2 = 0; a2 < 2; a2++) {
            r0.flashModeButton[a2] = new ImageView(context2);
            r0.flashModeButton[a2].setScaleType(ScaleType.CENTER);
            r0.flashModeButton[a2].setVisibility(4);
            r0.cameraPanel.addView(r0.flashModeButton[a2], LayoutHelper.createFrame(48, 48, 51));
            r0.flashModeButton[a2].setOnClickListener(new View.OnClickListener() {
                public void onClick(final View currentImage) {
                    if (!(ChatAttachAlert.this.flashAnimationInProgress || ChatAttachAlert.this.cameraView == null || !ChatAttachAlert.this.cameraView.isInitied())) {
                        if (ChatAttachAlert.this.cameraOpened) {
                            String current = ChatAttachAlert.this.cameraView.getCameraSession().getCurrentFlashMode();
                            String next = ChatAttachAlert.this.cameraView.getCameraSession().getNextFlashMode();
                            if (!current.equals(next)) {
                                ChatAttachAlert.this.cameraView.getCameraSession().setCurrentFlashMode(next);
                                ChatAttachAlert.this.flashAnimationInProgress = true;
                                ImageView nextImage = ChatAttachAlert.this.flashModeButton[0] == currentImage ? ChatAttachAlert.this.flashModeButton[1] : ChatAttachAlert.this.flashModeButton[0];
                                nextImage.setVisibility(0);
                                ChatAttachAlert.this.setCameraFlashModeIcon(nextImage, next);
                                AnimatorSet animatorSet = new AnimatorSet();
                                r6 = new Animator[4];
                                r6[0] = ObjectAnimator.ofFloat(currentImage, "translationY", new float[]{0.0f, (float) AndroidUtilities.dp(48.0f)});
                                r6[1] = ObjectAnimator.ofFloat(nextImage, "translationY", new float[]{(float) (-AndroidUtilities.dp(48.0f)), 0.0f});
                                r6[2] = ObjectAnimator.ofFloat(currentImage, "alpha", new float[]{1.0f, 0.0f});
                                r6[3] = ObjectAnimator.ofFloat(nextImage, "alpha", new float[]{0.0f, 1.0f});
                                animatorSet.playTogether(r6);
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
            public void onItemClick(View view, int position) {
                if (view instanceof PhotoAttachPhotoCell) {
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
            CameraController.getInstance().startPreview(this.cameraView.getCameraSession());
            this.mediaCaptured = false;
        } else if (!cameraPhotos.isEmpty()) {
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

                public BitmapHolder getThumbForPhoto(MessageObject messageObject, FileLocation fileLocation, int index) {
                    return null;
                }

                public boolean cancelButtonPressed() {
                    if (ChatAttachAlert.this.cameraOpened && ChatAttachAlert.this.cameraView != null) {
                        AndroidUtilities.runOnUIThread(new C11081(), 1000);
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
                    if (!ChatAttachAlert.cameraPhotos.isEmpty()) {
                        if (ChatAttachAlert.this.baseFragment != null) {
                            if (videoEditedInfo != null && index >= 0 && index < ChatAttachAlert.cameraPhotos.size()) {
                                ((PhotoEntry) ChatAttachAlert.cameraPhotos.get(index)).editedInfo = videoEditedInfo;
                            }
                            int size = ChatAttachAlert.cameraPhotos.size();
                            for (int a = 0; a < size; a++) {
                                AndroidUtilities.addMediaToGallery(((PhotoEntry) ChatAttachAlert.cameraPhotos.get(a)).path);
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
                            int locked = System.getInt(ChatAttachAlert.this.baseFragment.getParentActivity().getContentResolver(), "accelerometer_rotation", 0);
                            if (!sameTakePictureOrientation) {
                                if (locked != 1) {
                                    return z;
                                }
                            }
                            z = true;
                            return z;
                        }
                    }
                    return false;
                }

                public void willHidePhotoViewer() {
                    int a = 0;
                    ChatAttachAlert.this.mediaCaptured = false;
                    int count = ChatAttachAlert.this.attachPhotoRecyclerView.getChildCount();
                    while (a < count) {
                        View view = ChatAttachAlert.this.attachPhotoRecyclerView.getChildAt(a);
                        if (view instanceof PhotoAttachPhotoCell) {
                            PhotoAttachPhotoCell cell = (PhotoAttachPhotoCell) view;
                            cell.showImage();
                            cell.showCheck(true);
                        }
                        a++;
                    }
                }

                public boolean canScrollAway() {
                    return false;
                }
            }, this.baseFragment);
        }
    }

    private boolean processTouchEvent(MotionEvent event) {
        if (event == null) {
            return false;
        }
        if ((this.pressed || event.getActionMasked() != 0) && event.getActionMasked() != 5) {
            if (this.pressed) {
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
                            AnimatorSet animatorSet = new AnimatorSet();
                            r3 = new Animator[5];
                            r3[0] = ObjectAnimator.ofFloat(this.cameraPanel, "alpha", new float[]{0.0f});
                            r3[1] = ObjectAnimator.ofFloat(this.counterTextView, "alpha", new float[]{0.0f});
                            r3[2] = ObjectAnimator.ofFloat(this.flashModeButton[0], "alpha", new float[]{0.0f});
                            r3[3] = ObjectAnimator.ofFloat(this.flashModeButton[1], "alpha", new float[]{0.0f});
                            r3[4] = ObjectAnimator.ofFloat(this.cameraPhotoRecyclerView, "alpha", new float[]{0.0f});
                            animatorSet.playTogether(r3);
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
                                AnimatorSet animatorSet2 = new AnimatorSet();
                                r8 = new Animator[6];
                                r8[0] = ObjectAnimator.ofFloat(this.cameraView, "translationY", new float[]{0.0f});
                                r8[1] = ObjectAnimator.ofFloat(this.cameraPanel, "alpha", new float[]{1.0f});
                                r8[2] = ObjectAnimator.ofFloat(this.counterTextView, "alpha", new float[]{1.0f});
                                r8[3] = ObjectAnimator.ofFloat(this.flashModeButton[0], "alpha", new float[]{1.0f});
                                r8[4] = ObjectAnimator.ofFloat(this.flashModeButton[1], "alpha", new float[]{1.0f});
                                r8[5] = ObjectAnimator.ofFloat(this.cameraPhotoRecyclerView, "alpha", new float[]{1.0f});
                                animatorSet2.playTogether(r8);
                                animatorSet2.setDuration(250);
                                animatorSet2.setInterpolator(this.interpolator);
                                animatorSet2.start();
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
        Object obj;
        int hashCode = mode.hashCode();
        if (hashCode != 3551) {
            if (hashCode != 109935) {
                if (hashCode == 3005871) {
                    if (mode.equals("auto")) {
                        obj = 2;
                        switch (obj) {
                            case null:
                                imageView.setImageResource(R.drawable.flash_off);
                                return;
                            case 1:
                                imageView.setImageResource(R.drawable.flash_on);
                                return;
                            case 2:
                                imageView.setImageResource(R.drawable.flash_auto);
                                return;
                            default:
                                return;
                        }
                    }
                }
            } else if (mode.equals("off")) {
                obj = null;
                switch (obj) {
                    case null:
                        imageView.setImageResource(R.drawable.flash_off);
                        return;
                    case 1:
                        imageView.setImageResource(R.drawable.flash_on);
                        return;
                    case 2:
                        imageView.setImageResource(R.drawable.flash_auto);
                        return;
                    default:
                        return;
                }
            }
        } else if (mode.equals("on")) {
            obj = 1;
            switch (obj) {
                case null:
                    imageView.setImageResource(R.drawable.flash_off);
                    return;
                case 1:
                    imageView.setImageResource(R.drawable.flash_on);
                    return;
                case 2:
                    imageView.setImageResource(R.drawable.flash_auto);
                    return;
                default:
                    return;
            }
        }
        obj = -1;
        switch (obj) {
            case null:
                imageView.setImageResource(R.drawable.flash_off);
                return;
            case 1:
                imageView.setImageResource(R.drawable.flash_on);
                return;
            case 2:
                imageView.setImageResource(R.drawable.flash_auto);
                return;
            default:
                return;
        }
    }

    protected boolean onCustomMeasure(View view, int width, int height) {
        boolean isPortrait = width < height;
        if (view == this.cameraView) {
            if (this.cameraOpened && !this.cameraAnimationInProgress) {
                this.cameraView.measure(MeasureSpec.makeMeasureSpec(width, NUM), MeasureSpec.makeMeasureSpec(height, NUM));
                return true;
            }
        } else if (view == this.cameraPanel) {
            if (isPortrait) {
                this.cameraPanel.measure(MeasureSpec.makeMeasureSpec(width, NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(100.0f), NUM));
            } else {
                this.cameraPanel.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(100.0f), NUM), MeasureSpec.makeMeasureSpec(height, NUM));
            }
            return true;
        } else if (view == this.cameraPhotoRecyclerView) {
            this.cameraPhotoRecyclerViewIgnoreLayout = true;
            if (isPortrait) {
                this.cameraPhotoRecyclerView.measure(MeasureSpec.makeMeasureSpec(width, NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(80.0f), NUM));
                if (this.cameraPhotoLayoutManager.getOrientation() != 0) {
                    this.cameraPhotoRecyclerView.setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f), 0);
                    this.cameraPhotoLayoutManager.setOrientation(0);
                    this.cameraAttachAdapter.notifyDataSetChanged();
                }
            } else {
                this.cameraPhotoRecyclerView.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(80.0f), NUM), MeasureSpec.makeMeasureSpec(height, NUM));
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

    protected boolean onCustomLayout(View view, int left, int top, int right, int bottom) {
        int width = right - left;
        int height = bottom - top;
        boolean isPortrait = width < height;
        if (view == this.cameraPanel) {
            if (isPortrait) {
                if (this.cameraPhotoRecyclerView.getVisibility() == 0) {
                    this.cameraPanel.layout(0, bottom - AndroidUtilities.dp(196.0f), width, bottom - AndroidUtilities.dp(96.0f));
                } else {
                    this.cameraPanel.layout(0, bottom - AndroidUtilities.dp(100.0f), width, bottom);
                }
            } else if (this.cameraPhotoRecyclerView.getVisibility() == 0) {
                this.cameraPanel.layout(right - AndroidUtilities.dp(196.0f), 0, right - AndroidUtilities.dp(96.0f), height);
            } else {
                this.cameraPanel.layout(right - AndroidUtilities.dp(100.0f), 0, right, height);
            }
            return true;
        } else if (view == this.counterTextView) {
            int cy;
            if (isPortrait) {
                cx = (width - this.counterTextView.getMeasuredWidth()) / 2;
                cy = bottom - AndroidUtilities.dp(154.0f);
                this.counterTextView.setRotation(0.0f);
                if (this.cameraPhotoRecyclerView.getVisibility() == 0) {
                    cy -= AndroidUtilities.dp(96.0f);
                }
            } else {
                cx = right - AndroidUtilities.dp(154.0f);
                cy = (height / 2) + (this.counterTextView.getMeasuredWidth() / 2);
                this.counterTextView.setRotation(-90.0f);
                if (this.cameraPhotoRecyclerView.getVisibility() == 0) {
                    cx -= AndroidUtilities.dp(96.0f);
                }
            }
            this.counterTextView.layout(cx, cy, this.counterTextView.getMeasuredWidth() + cx, this.counterTextView.getMeasuredHeight() + cy);
            return true;
        } else if (view != this.cameraPhotoRecyclerView) {
            return false;
        } else {
            if (isPortrait) {
                cx = height - AndroidUtilities.dp(88.0f);
                view.layout(0, cx, view.getMeasuredWidth(), view.getMeasuredHeight() + cx);
            } else {
                int cx = (left + width) - AndroidUtilities.dp(88.0f);
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
            this.currentHintAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (ChatAttachAlert.this.currentHintAnimation != null) {
                        if (ChatAttachAlert.this.currentHintAnimation.equals(animation)) {
                            ChatAttachAlert.this.currentHintAnimation = null;
                            if (ChatAttachAlert.this.hintTextView != null) {
                                ChatAttachAlert.this.hintTextView.setVisibility(4);
                            }
                        }
                    }
                }

                public void onAnimationCancel(Animator animation) {
                    if (ChatAttachAlert.this.currentHintAnimation != null && ChatAttachAlert.this.currentHintAnimation.equals(animation)) {
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

    private void openCamera(boolean animated) {
        if (this.cameraView != null) {
            int a = 0;
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
            if (animated) {
                this.cameraAnimationInProgress = true;
                ArrayList<Animator> animators = new ArrayList();
                animators.add(ObjectAnimator.ofFloat(this, "cameraOpenProgress", new float[]{0.0f, 1.0f}));
                animators.add(ObjectAnimator.ofFloat(this.cameraPanel, "alpha", new float[]{1.0f}));
                animators.add(ObjectAnimator.ofFloat(this.counterTextView, "alpha", new float[]{1.0f}));
                animators.add(ObjectAnimator.ofFloat(this.cameraPhotoRecyclerView, "alpha", new float[]{1.0f}));
                for (int a2 = 0; a2 < 2; a2++) {
                    if (this.flashModeButton[a2].getVisibility() == 0) {
                        animators.add(ObjectAnimator.ofFloat(this.flashModeButton[a2], "alpha", new float[]{1.0f}));
                        break;
                    }
                }
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(animators);
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
                while (a < 2) {
                    if (this.flashModeButton[a].getVisibility() == 0) {
                        this.flashModeButton[a].setAlpha(1.0f);
                        break;
                    }
                    a++;
                }
                this.delegate.onCameraOpened();
            }
            if (VERSION.SDK_INT >= 21) {
                this.cameraView.setSystemUiVisibility(1028);
            }
            this.cameraOpened = true;
        }
    }

    public void onActivityResultFragment(int requestCode, Intent data, String currentPicturePath) {
        Throwable e;
        MediaMetadataRetriever mediaMetadataRetriever;
        Bitmap bitmap;
        File cacheFile;
        int i;
        PhotoEntry photoEntry;
        Throwable th;
        int i2 = requestCode;
        String currentPicturePath2 = currentPicturePath;
        if (this.baseFragment != null) {
            if (r1.baseFragment.getParentActivity() != null) {
                mediaFromExternalCamera = true;
                if (i2 == 0) {
                    int exif;
                    PhotoViewer.getInstance().setParentActivity(r1.baseFragment.getParentActivity());
                    ArrayList<Object> arrayList = new ArrayList();
                    int orientation = 0;
                    try {
                        exif = new ExifInterface(currentPicturePath2).getAttributeInt("Orientation", 1);
                        if (exif == 3) {
                            orientation = 180;
                        } else if (exif == 6) {
                            orientation = 90;
                        } else if (exif == 8) {
                            orientation = 270;
                        }
                    } catch (Throwable e2) {
                        FileLog.m3e(e2);
                    }
                    int orientation2 = orientation;
                    exif = lastImageId;
                    lastImageId = exif - 1;
                    PhotoEntry photoEntry2 = r3;
                    PhotoEntry photoEntry3 = new PhotoEntry(0, exif, 0, currentPicturePath2, orientation2, false);
                    openPhotoViewer(photoEntry2, false, true);
                } else if (i2 == 2) {
                    Intent data2;
                    String videoPath = null;
                    if (BuildVars.LOGS_ENABLED) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("pic path ");
                        stringBuilder.append(currentPicturePath2);
                        FileLog.m0d(stringBuilder.toString());
                    }
                    if (data == null || currentPicturePath2 == null || !new File(currentPicturePath2).exists()) {
                        data2 = data;
                    } else {
                        data2 = null;
                    }
                    if (data2 != null) {
                        Uri uri = data2.getData();
                        if (uri != null) {
                            StringBuilder stringBuilder2;
                            if (BuildVars.LOGS_ENABLED) {
                                stringBuilder2 = new StringBuilder();
                                stringBuilder2.append("video record uri ");
                                stringBuilder2.append(uri.toString());
                                FileLog.m0d(stringBuilder2.toString());
                            }
                            videoPath = AndroidUtilities.getPath(uri);
                            if (BuildVars.LOGS_ENABLED) {
                                stringBuilder2 = new StringBuilder();
                                stringBuilder2.append("resolved path = ");
                                stringBuilder2.append(videoPath);
                                FileLog.m0d(stringBuilder2.toString());
                            }
                            if (videoPath == null || !new File(videoPath).exists()) {
                                videoPath = currentPicturePath2;
                            }
                        } else {
                            videoPath = currentPicturePath2;
                        }
                        AndroidUtilities.addMediaToGallery(currentPicturePath);
                        currentPicturePath2 = null;
                    }
                    if (videoPath == null && currentPicturePath2 != null && new File(currentPicturePath2).exists()) {
                        videoPath = currentPicturePath2;
                    }
                    long duration = 0;
                    try {
                        MediaMetadataRetriever mediaMetadataRetriever2 = new MediaMetadataRetriever();
                        mediaMetadataRetriever2.setDataSource(videoPath);
                        String d = mediaMetadataRetriever2.extractMetadata(9);
                        if (d != null) {
                            duration = (long) ((int) Math.ceil((double) (((float) Long.parseLong(d)) / 1000.0f)));
                        }
                        if (mediaMetadataRetriever2 != null) {
                            try {
                                mediaMetadataRetriever2.release();
                            } catch (Throwable e22) {
                                FileLog.m3e(e22);
                            }
                        }
                    } catch (Throwable e222) {
                        mediaMetadataRetriever = null;
                        FileLog.m3e(e222);
                        if (mediaMetadataRetriever != null) {
                            try {
                                mediaMetadataRetriever.release();
                            } catch (Throwable e2222) {
                                FileLog.m3e(e2222);
                                bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, 1);
                                mediaMetadataRetriever = new StringBuilder();
                                mediaMetadataRetriever.append("-2147483648_");
                                mediaMetadataRetriever.append(SharedConfig.getLastLocalId());
                                mediaMetadataRetriever.append(".jpg");
                                cacheFile = new File(FileLoader.getDirectory(4), mediaMetadataRetriever.toString());
                                bitmap.compress(CompressFormat.JPEG, 55, new FileOutputStream(cacheFile));
                                SharedConfig.saveConfig();
                                i = lastImageId;
                                lastImageId = i - 1;
                                photoEntry = new PhotoEntry(0, i, 0, videoPath, 0, true);
                                photoEntry.duration = (int) duration;
                                photoEntry.thumbPath = cacheFile.getAbsolutePath();
                                openPhotoViewer(photoEntry, false, true);
                            }
                        }
                    } catch (Throwable th2) {
                        e2222 = th2;
                        th = e2222;
                        if (mediaMetadataRetriever != null) {
                            mediaMetadataRetriever.release();
                        }
                        throw th;
                    }
                    bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, 1);
                    mediaMetadataRetriever = new StringBuilder();
                    mediaMetadataRetriever.append("-2147483648_");
                    mediaMetadataRetriever.append(SharedConfig.getLastLocalId());
                    mediaMetadataRetriever.append(".jpg");
                    cacheFile = new File(FileLoader.getDirectory(4), mediaMetadataRetriever.toString());
                    try {
                        bitmap.compress(CompressFormat.JPEG, 55, new FileOutputStream(cacheFile));
                    } catch (Throwable e22222) {
                        FileLog.m3e(e22222);
                    }
                    SharedConfig.saveConfig();
                    i = lastImageId;
                    lastImageId = i - 1;
                    photoEntry = new PhotoEntry(0, i, 0, videoPath, 0, true);
                    photoEntry.duration = (int) duration;
                    photoEntry.thumbPath = cacheFile.getAbsolutePath();
                    openPhotoViewer(photoEntry, false, true);
                }
            }
        }
    }

    public void closeCamera(boolean animated) {
        if (!this.takingPhoto) {
            if (this.cameraView != null) {
                this.animateCameraValues[1] = AndroidUtilities.dp(80.0f) - this.cameraViewOffsetX;
                this.animateCameraValues[2] = AndroidUtilities.dp(80.0f) - this.cameraViewOffsetY;
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
                    for (translationY = 0; translationY < 2; translationY++) {
                        if (this.flashModeButton[translationY].getVisibility() == 0) {
                            animators.add(ObjectAnimator.ofFloat(this.flashModeButton[translationY], "alpha", new float[]{0.0f}));
                            break;
                        }
                    }
                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.playTogether(animators);
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
                    for (int a = 0; a < 2; a++) {
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
        }
    }

    @Keep
    public void setCameraOpenProgress(float value) {
        if (this.cameraView != null) {
            float endWidth;
            float endHeight;
            this.cameraOpenProgress = value;
            float startWidth = (float) this.animateCameraValues[1];
            float startHeight = (float) this.animateCameraValues[2];
            if (AndroidUtilities.displaySize.x < AndroidUtilities.displaySize.y) {
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
                layoutParams.topMargin = (int) (((float) this.animateCameraValues[0]) + (((float) (this.cameraViewLocation[1] - this.animateCameraValues[0])) * (1.0f - value)));
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
            int a = 0;
            while (a < count) {
                View child = this.attachPhotoRecyclerView.getChildAt(a);
                if (!(child instanceof PhotoAttachCameraCell)) {
                    a++;
                } else if (VERSION.SDK_INT < 19 || child.isAttachedToWindow()) {
                    child.getLocationInWindow(this.cameraViewLocation);
                    int[] iArr = this.cameraViewLocation;
                    iArr[0] = iArr[0] - getLeftInset();
                    float listViewX = (this.listView.getX() + ((float) backgroundPaddingLeft)) - ((float) getLeftInset());
                    if (((float) this.cameraViewLocation[0]) < listViewX) {
                        this.cameraViewOffsetX = (int) (listViewX - ((float) this.cameraViewLocation[0]));
                        if (this.cameraViewOffsetX >= AndroidUtilities.dp(80.0f)) {
                            this.cameraViewOffsetX = 0;
                            this.cameraViewLocation[0] = AndroidUtilities.dp(-150.0f);
                            this.cameraViewLocation[1] = 0;
                        } else {
                            int[] iArr2 = this.cameraViewLocation;
                            iArr2[0] = iArr2[0] + this.cameraViewOffsetX;
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
                            int[] iArr3 = this.cameraViewLocation;
                            iArr3[1] = iArr3[1] + this.cameraViewOffsetY;
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
            FrameLayout.LayoutParams layoutParams;
            final FrameLayout.LayoutParams layoutParamsFinal;
            if (!this.cameraOpened) {
                this.cameraView.setTranslationX((float) this.cameraViewLocation[0]);
                this.cameraView.setTranslationY((float) this.cameraViewLocation[1]);
            }
            this.cameraIcon.setTranslationX((float) this.cameraViewLocation[0]);
            this.cameraIcon.setTranslationY((float) this.cameraViewLocation[1]);
            int finalWidth = AndroidUtilities.dp(80.0f) - this.cameraViewOffsetX;
            int finalHeight = AndroidUtilities.dp(80.0f) - this.cameraViewOffsetY;
            if (!this.cameraOpened) {
                this.cameraView.setClipLeft(this.cameraViewOffsetX);
                this.cameraView.setClipTop(this.cameraViewOffsetY);
                layoutParams = (FrameLayout.LayoutParams) this.cameraView.getLayoutParams();
                if (!(layoutParams.height == finalHeight && layoutParams.width == finalWidth)) {
                    layoutParams.width = finalWidth;
                    layoutParams.height = finalHeight;
                    this.cameraView.setLayoutParams(layoutParams);
                    layoutParamsFinal = layoutParams;
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            if (ChatAttachAlert.this.cameraView != null) {
                                ChatAttachAlert.this.cameraView.setLayoutParams(layoutParamsFinal);
                            }
                        }
                    });
                }
            }
            layoutParams = (FrameLayout.LayoutParams) this.cameraIcon.getLayoutParams();
            if (layoutParams.height != finalHeight || layoutParams.width != finalWidth) {
                layoutParams.width = finalWidth;
                layoutParams.height = finalHeight;
                this.cameraIcon.setLayoutParams(layoutParams);
                layoutParamsFinal = layoutParams;
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        if (ChatAttachAlert.this.cameraIcon != null) {
                            ChatAttachAlert.this.cameraIcon.setLayoutParams(layoutParamsFinal);
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
                            int count = ChatAttachAlert.this.attachPhotoRecyclerView.getChildCount();
                            int i = 0;
                            for (int a = 0; a < count; a++) {
                                View child = ChatAttachAlert.this.attachPhotoRecyclerView.getChildAt(a);
                                if (child instanceof PhotoAttachCameraCell) {
                                    child.setVisibility(4);
                                    break;
                                }
                            }
                            int a2;
                            if (ChatAttachAlert.this.cameraView.getCameraSession().getCurrentFlashMode().equals(ChatAttachAlert.this.cameraView.getCameraSession().getNextFlashMode())) {
                                for (a2 = 0; a2 < 2; a2++) {
                                    ChatAttachAlert.this.flashModeButton[a2].setVisibility(4);
                                    ChatAttachAlert.this.flashModeButton[a2].setAlpha(0.0f);
                                    ChatAttachAlert.this.flashModeButton[a2].setTranslationY(0.0f);
                                }
                            } else {
                                ChatAttachAlert.this.setCameraFlashModeIcon(ChatAttachAlert.this.flashModeButton[0], ChatAttachAlert.this.cameraView.getCameraSession().getCurrentFlashMode());
                                a2 = 0;
                                while (a2 < 2) {
                                    ChatAttachAlert.this.flashModeButton[a2].setVisibility(a2 == 0 ? 0 : 4);
                                    ImageView imageView = ChatAttachAlert.this.flashModeButton[a2];
                                    float f = (a2 == 0 && ChatAttachAlert.this.cameraOpened) ? 1.0f : 0.0f;
                                    imageView.setAlpha(f);
                                    ChatAttachAlert.this.flashModeButton[a2].setTranslationY(0.0f);
                                    a2++;
                                }
                            }
                            ChatAttachAlert.this.switchCameraButton.setImageResource(ChatAttachAlert.this.cameraView.isFrontface() ? R.drawable.camera_revert1 : R.drawable.camera_revert2);
                            ImageView access$5800 = ChatAttachAlert.this.switchCameraButton;
                            if (!ChatAttachAlert.this.cameraView.hasFrontFaceCamera()) {
                                i = 4;
                            }
                            access$5800.setVisibility(i);
                        }
                    });
                    if (this.cameraIcon == null) {
                        this.cameraIcon = new FrameLayout(this.baseFragment.getParentActivity());
                        ImageView cameraImageView = new ImageView(this.baseFragment.getParentActivity());
                        cameraImageView.setScaleType(ScaleType.CENTER);
                        cameraImageView.setImageResource(R.drawable.instant_camera);
                        this.cameraIcon.addView(cameraImageView, LayoutHelper.createFrame(80, 80, 85));
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

    public void hideCamera(boolean async) {
        if (this.deviceHasGoodCamera) {
            if (this.cameraView != null) {
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

                public void onAnimationEnd(Animator animation) {
                    if (ChatAttachAlert.this.currentHintAnimation != null) {
                        if (ChatAttachAlert.this.currentHintAnimation.equals(animation)) {
                            ChatAttachAlert.this.currentHintAnimation = null;
                            AndroidUtilities.runOnUIThread(ChatAttachAlert.this.hideHintRunnable = new C11091(), AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
                        }
                    }
                }

                public void onAnimationCancel(Animator animation) {
                    if (ChatAttachAlert.this.currentHintAnimation != null && ChatAttachAlert.this.currentHintAnimation.equals(animation)) {
                        ChatAttachAlert.this.currentHintAnimation = null;
                    }
                }
            });
            this.currentHintAnimation.setDuration(300);
            this.currentHintAnimation.start();
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.albumsDidLoaded) {
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
        if (this.listView.getChildCount() <= 0) {
            RecyclerListView recyclerListView = this.listView;
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
            RecyclerListView recyclerListView2 = this.listView;
            this.scrollOffsetY = newOffset;
            recyclerListView2.setTopGlowOffset(newOffset);
            this.listView.invalidate();
        }
    }

    protected boolean canDismissWithSwipe() {
        return false;
    }

    public void updatePhotosButton() {
        int count = selectedPhotos.size();
        if (count == 0) {
            this.sendPhotosButton.imageView.setPadding(0, AndroidUtilities.dp(4.0f), 0, 0);
            this.sendPhotosButton.imageView.setBackgroundResource(R.drawable.attach_hide_states);
            this.sendPhotosButton.imageView.setImageResource(R.drawable.attach_hide2);
            this.sendPhotosButton.textView.setText(TtmlNode.ANONYMOUS_REGION_ID);
            this.sendDocumentsButton.textView.setText(LocaleController.getString("ChatDocument", R.string.ChatDocument));
        } else {
            String str;
            int i;
            this.sendPhotosButton.imageView.setPadding(AndroidUtilities.dp(2.0f), 0, 0, 0);
            this.sendPhotosButton.imageView.setBackgroundResource(R.drawable.attach_send_states);
            this.sendPhotosButton.imageView.setImageResource(R.drawable.attach_send2);
            TextView access$8600 = this.sendPhotosButton.textView;
            Object[] objArr = new Object[1];
            objArr[0] = String.format("(%d)", new Object[]{Integer.valueOf(count)});
            access$8600.setText(LocaleController.formatString("SendItems", R.string.SendItems, objArr));
            TextView access$86002 = this.sendDocumentsButton.textView;
            if (count == 1) {
                str = "SendAsFile";
                i = R.string.SendAsFile;
            } else {
                str = "SendAsFiles";
                i = R.string.SendAsFiles;
            }
            access$86002.setText(LocaleController.getString(str, i));
        }
        if (VERSION.SDK_INT < 23 || getContext().checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == 0) {
            this.progressView.setText(LocaleController.getString("NoPhotos", R.string.NoPhotos));
            this.progressView.setTextSize(20);
            return;
        }
        this.progressView.setText(LocaleController.getString("PermissionStorage", R.string.PermissionStorage));
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
            for (int a = 0; a < Math.min(100, MediaController.allMediaAlbumEntry.photos.size()); a++) {
                ((PhotoEntry) MediaController.allMediaAlbumEntry.photos.get(a)).reset();
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
        if (open && VERSION.SDK_INT <= 19 && MediaController.allMediaAlbumEntry == null) {
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
                CameraController.getInstance().initCamera();
                this.deviceHasGoodCamera = CameraController.getInstance().isCameraInitied();
            } else if (this.baseFragment.getParentActivity().checkSelfPermission("android.permission.CAMERA") != 0) {
                if (request) {
                    this.baseFragment.getParentActivity().requestPermissions(new String[]{"android.permission.CAMERA"}, 17);
                }
                this.deviceHasGoodCamera = false;
            } else {
                CameraController.getInstance().initCamera();
                this.deviceHasGoodCamera = CameraController.getInstance().isCameraInitied();
            }
            if (!(old == this.deviceHasGoodCamera || this.photoAttachAdapter == null)) {
                this.photoAttachAdapter.notifyDataSetChanged();
            }
            if (!(!isShowing() || !this.deviceHasGoodCamera || this.baseFragment == null || this.backDrawable.getAlpha() == 0 || this.revealAnimationInProgress || this.cameraOpened)) {
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

    private int addToSelectedPhotos(PhotoEntry object, int index) {
        Object key = Integer.valueOf(object.imageId);
        if (selectedPhotos.containsKey(key)) {
            selectedPhotos.remove(key);
            int position = selectedPhotosOrder.indexOf(key);
            if (position >= 0) {
                selectedPhotosOrder.remove(position);
            }
            updatePhotosCounter();
            updateCheckedPhotoIndices();
            if (index >= 0) {
                object.reset();
                this.photoViewerProvider.updatePhotoAtIndex(index);
            }
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
        if (!value || (value && VERSION.SDK_INT >= 18 && !AndroidUtilities.isTablet() && VERSION.SDK_INT < 26)) {
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
        final boolean z = open;
        this.containerView.setTranslationY(0.0f);
        final AnimatorSet animatorSet = new AnimatorSet();
        View view = this.delegate.getRevealView();
        if (view.getVisibility() == 0 && ((ViewGroup) view.getParent()).getVisibility() == 0) {
            float top;
            int[] coords = new int[2];
            view.getLocationInWindow(coords);
            if (VERSION.SDK_INT <= 19) {
                top = (float) ((AndroidUtilities.displaySize.y - r1.containerView.getMeasuredHeight()) - AndroidUtilities.statusBarHeight);
            } else {
                top = r1.containerView.getY();
            }
            r1.revealX = coords[0] + (view.getMeasuredWidth() / 2);
            r1.revealY = (int) (((float) (coords[1] + (view.getMeasuredHeight() / 2))) - top);
            if (VERSION.SDK_INT <= 19) {
                r1.revealY -= AndroidUtilities.statusBarHeight;
            }
        } else {
            r1.revealX = (AndroidUtilities.displaySize.x / 2) + backgroundPaddingLeft;
            r1.revealY = (int) (((float) AndroidUtilities.displaySize.y) - r1.containerView.getY());
        }
        int i = 4;
        corners = new int[4][];
        corners[1] = new int[]{0, AndroidUtilities.dp(304.0f)};
        corners[2] = new int[]{r1.containerView.getMeasuredWidth(), 0};
        corners[3] = new int[]{r1.containerView.getMeasuredWidth(), AndroidUtilities.dp(304.0f)};
        int y = (r1.revealY - r1.scrollOffsetY) + backgroundPaddingTop;
        int finalRevealRadius = 0;
        int a = 0;
        while (a < i) {
            finalRevealRadius = Math.max(finalRevealRadius, (int) Math.ceil(Math.sqrt((double) (((r1.revealX - corners[a][0]) * (r1.revealX - corners[a][0])) + ((y - corners[a][1]) * (y - corners[a][1]))))));
            a++;
            i = 4;
        }
        i = r1.revealX <= r1.containerView.getMeasuredWidth() ? r1.revealX : r1.containerView.getMeasuredWidth();
        ArrayList<Animator> animators = new ArrayList(3);
        String str = "revealRadius";
        float[] fArr = new float[2];
        fArr[0] = z ? 0.0f : (float) finalRevealRadius;
        fArr[1] = z ? (float) finalRevealRadius : 0.0f;
        animators.add(ObjectAnimator.ofFloat(r1, str, fArr));
        ColorDrawable colorDrawable = r1.backDrawable;
        str = "alpha";
        int[] iArr = new int[1];
        iArr[0] = z ? 51 : 0;
        animators.add(ObjectAnimator.ofInt(colorDrawable, str, iArr));
        if (VERSION.SDK_INT >= 21) {
            try {
                animators.add(ViewAnimationUtils.createCircularReveal(r1.containerView, i, r1.revealY, z ? 0.0f : (float) finalRevealRadius, z ? (float) finalRevealRadius : 0.0f));
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
            animatorSet.setDuration(320);
        } else if (z) {
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
            animators.add(ObjectAnimator.ofFloat(r1.containerView, "scaleX", new float[]{0.0f}));
            animators.add(ObjectAnimator.ofFloat(r1.containerView, "scaleY", new float[]{0.0f}));
            animators.add(ObjectAnimator.ofFloat(r1.containerView, "alpha", new float[]{0.0f}));
        }
        animatorSet.playTogether(animators);
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
                            FileLog.m3e(e);
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
        View view2;
        int finalRevealX;
        int[][] corners;
        ArrayList<Animator> animators2;
        if (z) {
            r1.innerAnimators.clear();
            NotificationCenter.getInstance(r1.currentAccount).setAllowedNotificationsDutingAnimation(new int[]{NotificationCenter.dialogsNeedReload});
            NotificationCenter.getInstance(r1.currentAccount).setAnimationInProgress(true);
            r1.revealAnimationInProgress = true;
            int i2 = 8;
            int count = VERSION.SDK_INT <= 19 ? 12 : 8;
            int a2 = 0;
            while (a2 < count) {
                AnimatorSet animatorSetInner;
                int i3;
                if (VERSION.SDK_INT <= 19) {
                    if (a2 < i2) {
                        r1.views[a2].setScaleX(0.1f);
                        r1.views[a2].setScaleY(0.1f);
                    }
                    r1.views[a2].setAlpha(0.0f);
                } else {
                    r1.views[a2].setScaleX(0.7f);
                    r1.views[a2].setScaleY(0.7f);
                }
                InnerAnimator innerAnimator = new InnerAnimator();
                int buttonX = r1.views[a2].getLeft() + (r1.views[a2].getMeasuredWidth() / 2);
                i2 = (r1.views[a2].getTop() + r1.attachView.getTop()) + (r1.views[a2].getMeasuredHeight() / 2);
                int count2 = count;
                view2 = view;
                float dist = (float) Math.sqrt((double) (((r1.revealX - buttonX) * (r1.revealX - buttonX)) + ((r1.revealY - i2) * (r1.revealY - i2))));
                float vecX = ((float) (r1.revealX - buttonX)) / dist;
                view = ((float) (r1.revealY - i2)) / dist;
                finalRevealX = i;
                corners = corners;
                r1.views[a2].setPivotX(((float) (r1.views[a2].getMeasuredWidth() / 2)) + (((float) AndroidUtilities.dp(20.0f)) * vecX));
                r1.views[a2].setPivotY(((float) (r1.views[a2].getMeasuredHeight() / 2)) + (((float) AndroidUtilities.dp(20.0f)) * view));
                innerAnimator.startRadius = dist - ((float) AndroidUtilities.dp(81.0f));
                r1.views[a2].setTag(R.string.AppName, Integer.valueOf(1));
                animators2 = new ArrayList();
                if (a2 < 8) {
                    animators2.add(ObjectAnimator.ofFloat(r1.views[a2], "scaleX", new float[]{0.7f, 1.05f}));
                    animators2.add(ObjectAnimator.ofFloat(r1.views[a2], "scaleY", new float[]{0.7f, 1.05f}));
                    animatorSetInner = new AnimatorSet();
                    Animator[] animatorArr = new Animator[2];
                    animatorArr[0] = ObjectAnimator.ofFloat(r1.views[a2], "scaleX", new float[]{1.0f});
                    animatorArr[1] = ObjectAnimator.ofFloat(r1.views[a2], "scaleY", new float[]{1.0f});
                    animatorSetInner.playTogether(animatorArr);
                    animatorSetInner.setDuration(100);
                    animatorSetInner.setInterpolator(r1.decelerateInterpolator);
                } else {
                    float f = vecX;
                    animatorSetInner = null;
                }
                final AnimatorSet animatorSetInner2 = animatorSetInner;
                if (VERSION.SDK_INT <= 19) {
                    float[] fArr2 = new float[1];
                    i3 = 0;
                    fArr2[0] = 1.0f;
                    animators2.add(ObjectAnimator.ofFloat(r1.views[a2], "alpha", fArr2));
                } else {
                    i3 = 0;
                }
                innerAnimator.animatorSet = new AnimatorSet();
                innerAnimator.animatorSet.playTogether(animators2);
                int a3 = a2;
                innerAnimator.animatorSet.setDuration(150);
                innerAnimator.animatorSet.setInterpolator(r1.decelerateInterpolator);
                innerAnimator.animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (animatorSetInner2 != null) {
                            animatorSetInner2.start();
                        }
                    }
                });
                r1.innerAnimators.add(innerAnimator);
                a2 = a3 + 1;
                int i4 = i3;
                count = count2;
                view = view2;
                i = finalRevealX;
                corners = corners;
                z = open;
                i2 = 8;
            }
            finalRevealX = i;
            corners = corners;
        } else {
            view2 = view;
            finalRevealX = i;
            corners = corners;
            animators2 = animators;
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
            Chat chat = this.baseFragment.getCurrentChat();
            if (ChatObject.isChannel(chat)) {
                boolean z;
                int a;
                float f;
                AttachButton attachButton;
                FrameLayout frameLayout;
                if (chat.banned_rights != null) {
                    if (chat.banned_rights.send_media) {
                        z = false;
                        this.mediaEnabled = z;
                        a = 0;
                        while (true) {
                            f = 0.2f;
                            if (a < 5) {
                                break;
                            }
                            attachButton = (AttachButton) this.attachButtons.get(a);
                            if (this.mediaEnabled) {
                                f = 1.0f;
                            }
                            attachButton.setAlpha(f);
                            ((AttachButton) this.attachButtons.get(a)).setEnabled(this.mediaEnabled);
                            a++;
                        }
                        this.attachPhotoRecyclerView.setAlpha(this.mediaEnabled ? 1.0f : 0.2f);
                        this.attachPhotoRecyclerView.setEnabled(this.mediaEnabled);
                        if (!this.mediaEnabled) {
                            if (AndroidUtilities.isBannedForever(chat.banned_rights.until_date)) {
                                this.mediaBanTooltip.setText(LocaleController.formatString("AttachMediaRestricted", R.string.AttachMediaRestricted, LocaleController.formatDateForBan((long) chat.banned_rights.until_date)));
                            } else {
                                this.mediaBanTooltip.setText(LocaleController.formatString("AttachMediaRestrictedForever", R.string.AttachMediaRestrictedForever, new Object[0]));
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
                a = 0;
                while (true) {
                    f = 0.2f;
                    if (a < 5) {
                        break;
                    }
                    attachButton = (AttachButton) this.attachButtons.get(a);
                    if (this.mediaEnabled) {
                        f = 1.0f;
                    }
                    attachButton.setAlpha(f);
                    ((AttachButton) this.attachButtons.get(a)).setEnabled(this.mediaEnabled);
                    a++;
                }
                if (this.mediaEnabled) {
                }
                this.attachPhotoRecyclerView.setAlpha(this.mediaEnabled ? 1.0f : 0.2f);
                this.attachPhotoRecyclerView.setEnabled(this.mediaEnabled);
                if (this.mediaEnabled) {
                    if (AndroidUtilities.isBannedForever(chat.banned_rights.until_date)) {
                        this.mediaBanTooltip.setText(LocaleController.formatString("AttachMediaRestricted", R.string.AttachMediaRestricted, LocaleController.formatDateForBan((long) chat.banned_rights.until_date)));
                    } else {
                        this.mediaBanTooltip.setText(LocaleController.formatString("AttachMediaRestrictedForever", R.string.AttachMediaRestrictedForever, new Object[0]));
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

    public void dismissWithButtonClick(int item) {
        super.dismissWithButtonClick(item);
        boolean z = (item == 0 || item == 2) ? false : true;
        hideCamera(z);
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

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (!this.cameraOpened || (keyCode != 24 && keyCode != 25)) {
            return super.onKeyDown(keyCode, event);
        }
        this.shutterButton.getDelegate().shutterReleased();
        return true;
    }
}
