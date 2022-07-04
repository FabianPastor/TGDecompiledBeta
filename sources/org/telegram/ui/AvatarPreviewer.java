package org.telegram.ui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import androidx.core.content.ContextCompat;
import androidx.core.util.Consumer;
import androidx.core.util.Preconditions;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.Components.RadialProgress2;

public class AvatarPreviewer {
    private static AvatarPreviewer INSTANCE;
    private Callback callback;
    private Context context;
    private Layout layout;
    private ViewGroup view;
    private boolean visible;
    private WindowManager windowManager;

    public interface Callback {
        void onMenuClick(MenuItem menuItem);
    }

    public static AvatarPreviewer getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AvatarPreviewer();
        }
        return INSTANCE;
    }

    public static boolean hasVisibleInstance() {
        AvatarPreviewer avatarPreviewer = INSTANCE;
        return avatarPreviewer != null && avatarPreviewer.visible;
    }

    public static boolean canPreview(Data data) {
        return (data == null || (data.imageLocation == null && data.thumbImageLocation == null)) ? false : true;
    }

    public void show(ViewGroup parentContainer, Data data, Callback callback2) {
        Preconditions.checkNotNull(parentContainer);
        Preconditions.checkNotNull(data);
        Preconditions.checkNotNull(callback2);
        Context context2 = parentContainer.getContext();
        if (this.view != parentContainer) {
            close();
            this.view = parentContainer;
            this.context = context2;
            this.windowManager = (WindowManager) ContextCompat.getSystemService(context2, WindowManager.class);
            this.layout = new Layout(context2, callback2) {
                /* access modifiers changed from: protected */
                public void onHide() {
                    AvatarPreviewer.this.close();
                }
            };
        }
        this.layout.setData(data);
        if (!this.visible) {
            if (this.layout.getParent() != null) {
                this.windowManager.removeView(this.layout);
            }
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(-1, -1, 99, 0, -3);
            if (Build.VERSION.SDK_INT >= 21) {
                layoutParams.flags = -NUM;
            }
            this.windowManager.addView(this.layout, layoutParams);
            parentContainer.requestDisallowInterceptTouchEvent(true);
            this.visible = true;
        }
    }

    public void close() {
        if (this.visible) {
            this.visible = false;
            if (this.layout.getParent() != null) {
                this.windowManager.removeView(this.layout);
            }
            this.layout.recycle();
            this.layout = null;
            this.view.requestDisallowInterceptTouchEvent(false);
            this.view = null;
            this.context = null;
            this.windowManager = null;
            this.callback = null;
        }
    }

    public boolean isVisible() {
        return this.visible;
    }

    public void onTouchEvent(MotionEvent event) {
        Layout layout2 = this.layout;
        if (layout2 != null) {
            layout2.onTouchEvent(event);
        }
    }

    public enum MenuItem {
        OPEN_PROFILE("OpenProfile", NUM, NUM),
        OPEN_CHANNEL("OpenChannel2", NUM, NUM),
        OPEN_GROUP("OpenGroup2", NUM, NUM),
        SEND_MESSAGE("SendMessage", NUM, NUM),
        MENTION("Mention", NUM, NUM);
        
        /* access modifiers changed from: private */
        public final int iconResId;
        /* access modifiers changed from: private */
        public final String labelKey;
        /* access modifiers changed from: private */
        public final int labelResId;

        private MenuItem(String labelKey2, int labelResId2, int iconResId2) {
            this.labelKey = labelKey2;
            this.labelResId = labelResId2;
            this.iconResId = iconResId2;
        }
    }

    public static class Data {
        /* access modifiers changed from: private */
        public final String imageFilter;
        /* access modifiers changed from: private */
        public final ImageLocation imageLocation;
        /* access modifiers changed from: private */
        public final InfoLoadTask<?, ?> infoLoadTask;
        /* access modifiers changed from: private */
        public final MenuItem[] menuItems;
        /* access modifiers changed from: private */
        public final Object parentObject;
        /* access modifiers changed from: private */
        public final String thumbImageFilter;
        /* access modifiers changed from: private */
        public final ImageLocation thumbImageLocation;
        /* access modifiers changed from: private */
        public final String videoFileName;
        /* access modifiers changed from: private */
        public final String videoFilter;
        /* access modifiers changed from: private */
        public final ImageLocation videoLocation;

        public static Data of(TLRPC.User user, int classGuid, MenuItem... menuItems2) {
            TLRPC.User user2 = user;
            ImageLocation imageLocation2 = ImageLocation.getForUserOrChat(user2, 0);
            ImageLocation thumbImageLocation2 = ImageLocation.getForUserOrChat(user2, 1);
            return new Data(imageLocation2, thumbImageLocation2, (ImageLocation) null, (String) null, (thumbImageLocation2 == null || !(thumbImageLocation2.photoSize instanceof TLRPC.TL_photoStrippedSize)) ? null : "b", (String) null, (String) null, user, menuItems2, new UserInfoLoadTask(user2, classGuid));
        }

        public static Data of(TLRPC.UserFull userFull, MenuItem... menuItems2) {
            ImageLocation videoLocation2;
            String videoFileName2;
            TLRPC.UserFull userFull2 = userFull;
            ImageLocation imageLocation2 = ImageLocation.getForUserOrChat(userFull2.user, 0);
            ImageLocation thumbImageLocation2 = ImageLocation.getForUserOrChat(userFull2.user, 1);
            String videoFilter2 = null;
            String thumbFilter = (thumbImageLocation2 == null || !(thumbImageLocation2.photoSize instanceof TLRPC.TL_photoStrippedSize)) ? null : "b";
            if (userFull2.profile_photo == null || userFull2.profile_photo.video_sizes.isEmpty()) {
                videoFileName2 = null;
                videoLocation2 = null;
            } else {
                TLRPC.VideoSize videoSize = userFull2.profile_photo.video_sizes.get(0);
                ImageLocation videoLocation3 = ImageLocation.getForPhoto(videoSize, userFull2.profile_photo);
                videoFileName2 = FileLoader.getAttachFileName(videoSize);
                videoLocation2 = videoLocation3;
            }
            if (videoLocation2 != null && videoLocation2.imageType == 2) {
                videoFilter2 = "g";
            }
            return new Data(imageLocation2, thumbImageLocation2, videoLocation2, (String) null, thumbFilter, videoFilter2, videoFileName2, userFull2.user, menuItems2, (InfoLoadTask<?, ?>) null);
        }

        public static Data of(TLRPC.Chat chat, int classGuid, MenuItem... menuItems2) {
            TLRPC.Chat chat2 = chat;
            ImageLocation imageLocation2 = ImageLocation.getForUserOrChat(chat2, 0);
            ImageLocation thumbImageLocation2 = ImageLocation.getForUserOrChat(chat2, 1);
            return new Data(imageLocation2, thumbImageLocation2, (ImageLocation) null, (String) null, (thumbImageLocation2 == null || !(thumbImageLocation2.photoSize instanceof TLRPC.TL_photoStrippedSize)) ? null : "b", (String) null, (String) null, chat, menuItems2, new ChatInfoLoadTask(chat2, classGuid));
        }

        public static Data of(TLRPC.Chat chat, TLRPC.ChatFull chatFull, MenuItem... menuItems2) {
            String videoFileName2;
            ImageLocation videoLocation2;
            TLRPC.Chat chat2 = chat;
            TLRPC.ChatFull chatFull2 = chatFull;
            ImageLocation imageLocation2 = ImageLocation.getForUserOrChat(chat2, 0);
            ImageLocation thumbImageLocation2 = ImageLocation.getForUserOrChat(chat2, 1);
            String thumbFilter = (thumbImageLocation2 == null || !(thumbImageLocation2.photoSize instanceof TLRPC.TL_photoStrippedSize)) ? null : "b";
            if (chatFull2.chat_photo == null || chatFull2.chat_photo.video_sizes.isEmpty()) {
                videoFileName2 = null;
                videoLocation2 = null;
            } else {
                TLRPC.VideoSize videoSize = chatFull2.chat_photo.video_sizes.get(0);
                ImageLocation videoLocation3 = ImageLocation.getForPhoto(videoSize, chatFull2.chat_photo);
                videoFileName2 = FileLoader.getAttachFileName(videoSize);
                videoLocation2 = videoLocation3;
            }
            ImageLocation imageLocation3 = videoLocation2;
            return new Data(imageLocation2, thumbImageLocation2, videoLocation2, (String) null, thumbFilter, (videoLocation2 == null || videoLocation2.imageType != 2) ? null : "g", videoFileName2, chat, menuItems2, (InfoLoadTask<?, ?>) null);
        }

        private Data(ImageLocation imageLocation2, ImageLocation thumbImageLocation2, ImageLocation videoLocation2, String imageFilter2, String thumbImageFilter2, String videoFilter2, String videoFileName2, Object parentObject2, MenuItem[] menuItems2, InfoLoadTask<?, ?> infoLoadTask2) {
            this.imageLocation = imageLocation2;
            this.thumbImageLocation = thumbImageLocation2;
            this.videoLocation = videoLocation2;
            this.imageFilter = imageFilter2;
            this.thumbImageFilter = thumbImageFilter2;
            this.videoFilter = videoFilter2;
            this.videoFileName = videoFileName2;
            this.parentObject = parentObject2;
            this.menuItems = menuItems2;
            this.infoLoadTask = infoLoadTask2;
        }
    }

    private static class UserInfoLoadTask extends InfoLoadTask<TLRPC.User, TLRPC.UserFull> {
        public UserInfoLoadTask(TLRPC.User argument, int classGuid) {
            super(argument, classGuid, NotificationCenter.userInfoDidLoad);
        }

        /* access modifiers changed from: protected */
        public void load() {
            MessagesController.getInstance(UserConfig.selectedAccount).loadUserInfo((TLRPC.User) this.argument, false, this.classGuid);
        }

        /* access modifiers changed from: protected */
        public void onReceiveNotification(Object... args) {
            if (args[0].longValue() == ((TLRPC.User) this.argument).id) {
                onResult(args[1]);
            }
        }
    }

    private static class ChatInfoLoadTask extends InfoLoadTask<TLRPC.Chat, TLRPC.ChatFull> {
        public ChatInfoLoadTask(TLRPC.Chat argument, int classGuid) {
            super(argument, classGuid, NotificationCenter.chatInfoDidLoad);
        }

        /* access modifiers changed from: protected */
        public void load() {
            MessagesController.getInstance(UserConfig.selectedAccount).loadFullChat(((TLRPC.Chat) this.argument).id, this.classGuid, false);
        }

        /* access modifiers changed from: protected */
        public void onReceiveNotification(Object... args) {
            TLRPC.ChatFull chatFull = args[0];
            if (chatFull != null && chatFull.id == ((TLRPC.Chat) this.argument).id) {
                onResult(chatFull);
            }
        }
    }

    private static abstract class InfoLoadTask<A, B> {
        protected final A argument;
        protected final int classGuid;
        /* access modifiers changed from: private */
        public boolean loading;
        private final NotificationCenter notificationCenter;
        /* access modifiers changed from: private */
        public final int notificationId;
        private final NotificationCenter.NotificationCenterDelegate observer = new NotificationCenter.NotificationCenterDelegate() {
            public void didReceivedNotification(int id, int account, Object... args) {
                if (InfoLoadTask.this.loading && id == InfoLoadTask.this.notificationId) {
                    InfoLoadTask.this.onReceiveNotification(args);
                }
            }
        };
        private Consumer<B> onResult;

        /* access modifiers changed from: protected */
        public abstract void load();

        /* access modifiers changed from: protected */
        public abstract void onReceiveNotification(Object... objArr);

        public InfoLoadTask(A argument2, int classGuid2, int notificationId2) {
            this.argument = argument2;
            this.classGuid = classGuid2;
            this.notificationId = notificationId2;
            this.notificationCenter = NotificationCenter.getInstance(UserConfig.selectedAccount);
        }

        public final void load(Consumer<B> onResult2) {
            if (!this.loading) {
                this.loading = true;
                this.onResult = onResult2;
                this.notificationCenter.addObserver(this.observer, this.notificationId);
                load();
            }
        }

        public final void cancel() {
            if (this.loading) {
                this.loading = false;
                this.notificationCenter.removeObserver(this.observer, this.notificationId);
            }
        }

        /* access modifiers changed from: protected */
        public final void onResult(B result) {
            if (this.loading) {
                cancel();
                this.onResult.accept(result);
            }
        }
    }

    private static abstract class Layout extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
        private static final float ANIM_DURATION = 150.0f;
        private final Drawable arrowDrawable;
        private final ColorDrawable backgroundDrawable = new ColorDrawable(NUM);
        private final Callback callback;
        private final int[] coords = new int[2];
        private float downY;
        private final ImageReceiver imageReceiver;
        private InfoLoadTask<?, ?> infoLoadTask;
        private WindowInsets insets;
        private final Interpolator interpolator = new AccelerateDecelerateInterpolator();
        private long lastUpdateTime;
        private MenuItem[] menuItems;
        private ValueAnimator moveAnimator;
        private float moveProgress;
        private float progress;
        private ValueAnimator progressHideAnimator;
        private ValueAnimator progressShowAnimator;
        private final RadialProgress2 radialProgress;
        private final int radialProgressSize = AndroidUtilities.dp(64.0f);
        private final Rect rect = new Rect();
        private boolean recycled;
        /* access modifiers changed from: private */
        public boolean showProgress;
        private boolean showing;
        private String videoFileName;
        private BottomSheet visibleSheet;

        /* access modifiers changed from: protected */
        public abstract void onHide();

        public Layout(Context context, Callback callback2) {
            super(context);
            ImageReceiver imageReceiver2 = new ImageReceiver();
            this.imageReceiver = imageReceiver2;
            this.downY = -1.0f;
            this.callback = callback2;
            setWillNotDraw(false);
            setFitsSystemWindows(true);
            imageReceiver2.setAspectFit(true);
            imageReceiver2.setInvalidateAll(true);
            imageReceiver2.setRoundRadius(AndroidUtilities.dp(6.0f));
            imageReceiver2.setParentView(this);
            RadialProgress2 radialProgress2 = new RadialProgress2(this);
            this.radialProgress = radialProgress2;
            radialProgress2.setOverrideAlpha(0.0f);
            radialProgress2.setIcon(10, false, false);
            radialProgress2.setColors(NUM, NUM, -1, -1);
            this.arrowDrawable = ContextCompat.getDrawable(context, NUM);
        }

        /* access modifiers changed from: protected */
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            this.imageReceiver.onAttachedToWindow();
            NotificationCenter.getInstance(UserConfig.selectedAccount).addObserver(this, NotificationCenter.fileLoaded);
            NotificationCenter.getInstance(UserConfig.selectedAccount).addObserver(this, NotificationCenter.fileLoadProgressChanged);
        }

        /* access modifiers changed from: protected */
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            this.imageReceiver.onDetachedFromWindow();
            NotificationCenter.getInstance(UserConfig.selectedAccount).removeObserver(this, NotificationCenter.fileLoaded);
            NotificationCenter.getInstance(UserConfig.selectedAccount).removeObserver(this, NotificationCenter.fileLoadProgressChanged);
        }

        public void didReceivedNotification(int id, int account, Object... args) {
            if (this.showProgress && !TextUtils.isEmpty(this.videoFileName)) {
                if (id == NotificationCenter.fileLoaded) {
                    if (TextUtils.equals(args[0], this.videoFileName)) {
                        this.radialProgress.setProgress(1.0f, true);
                    }
                } else if (id == NotificationCenter.fileLoadProgressChanged && TextUtils.equals(args[0], this.videoFileName) && this.radialProgress != null) {
                    this.radialProgress.setProgress(Math.min(1.0f, ((float) args[1].longValue()) / ((float) args[2].longValue())), true);
                }
            }
        }

        public boolean onTouchEvent(MotionEvent event) {
            if (!this.showing) {
                return false;
            }
            if (this.moveAnimator == null) {
                if (event.getActionMasked() == 1) {
                    this.downY = -1.0f;
                    setShowing(false);
                } else if (event.getActionMasked() == 2) {
                    if (this.downY < 0.0f) {
                        this.downY = event.getY();
                    } else {
                        float max = Math.max(-1.0f, Math.min(0.0f, (event.getY() - this.downY) / ((float) AndroidUtilities.dp(56.0f))));
                        this.moveProgress = max;
                        if (max == -1.0f) {
                            performHapticFeedback(0);
                            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{this.moveProgress, 0.0f});
                            this.moveAnimator = ofFloat;
                            ofFloat.setDuration(200);
                            this.moveAnimator.addUpdateListener(new AvatarPreviewer$Layout$$ExternalSyntheticLambda2(this));
                            this.moveAnimator.start();
                            showBottomSheet();
                        }
                        invalidate();
                    }
                }
            }
            return true;
        }

        /* renamed from: lambda$onTouchEvent$0$org-telegram-ui-AvatarPreviewer$Layout  reason: not valid java name */
        public /* synthetic */ void m2716lambda$onTouchEvent$0$orgtelegramuiAvatarPreviewer$Layout(ValueAnimator a) {
            this.moveProgress = ((Float) a.getAnimatedValue()).floatValue();
            invalidate();
        }

        private void showBottomSheet() {
            MenuItem[] menuItemArr = this.menuItems;
            CharSequence[] labels = new CharSequence[menuItemArr.length];
            int[] icons = new int[menuItemArr.length];
            int i = 0;
            while (true) {
                MenuItem[] menuItemArr2 = this.menuItems;
                if (i < menuItemArr2.length) {
                    labels[i] = LocaleController.getString(menuItemArr2[i].labelKey, this.menuItems[i].labelResId);
                    icons[i] = this.menuItems[i].iconResId;
                    i++;
                } else {
                    BottomSheet dimBehind = new BottomSheet.Builder(getContext()).setItems(labels, icons, new AvatarPreviewer$Layout$$ExternalSyntheticLambda3(this)).setDimBehind(false);
                    this.visibleSheet = dimBehind;
                    dimBehind.setOnDismissListener(new AvatarPreviewer$Layout$$ExternalSyntheticLambda4(this));
                    this.visibleSheet.show();
                    return;
                }
            }
        }

        /* renamed from: lambda$showBottomSheet$1$org-telegram-ui-AvatarPreviewer$Layout  reason: not valid java name */
        public /* synthetic */ void m2718lambda$showBottomSheet$1$orgtelegramuiAvatarPreviewer$Layout(DialogInterface dialog, int which) {
            this.callback.onMenuClick(this.menuItems[which]);
            setShowing(false);
        }

        /* renamed from: lambda$showBottomSheet$2$org-telegram-ui-AvatarPreviewer$Layout  reason: not valid java name */
        public /* synthetic */ void m2719lambda$showBottomSheet$2$orgtelegramuiAvatarPreviewer$Layout(DialogInterface dialog) {
            this.visibleSheet = null;
            setShowing(false);
        }

        public WindowInsets onApplyWindowInsets(WindowInsets insets2) {
            this.insets = insets2;
            invalidateSize();
            return insets2.consumeStableInsets();
        }

        /* access modifiers changed from: protected */
        public void onSizeChanged(int w, int h, int oldw, int oldh) {
            invalidateSize();
        }

        public void invalidateSize() {
            int width = getWidth();
            int height = getHeight();
            if (width == 0) {
                int i = height;
            } else if (height == 0) {
                int i2 = width;
                int i3 = height;
            } else {
                this.backgroundDrawable.setBounds(0, 0, width, height);
                int padding = AndroidUtilities.dp(8.0f);
                int lPadding = padding;
                int rPadding = padding;
                int vPadding = padding;
                if (Build.VERSION.SDK_INT >= 21) {
                    lPadding += this.insets.getStableInsetLeft();
                    rPadding += this.insets.getStableInsetRight();
                    vPadding += Math.max(this.insets.getStableInsetTop(), this.insets.getStableInsetBottom());
                }
                int arrowWidth = this.arrowDrawable.getIntrinsicWidth();
                int arrowHeight = this.arrowDrawable.getIntrinsicHeight();
                int arrowPadding = AndroidUtilities.dp(24.0f);
                int w = width - (lPadding + rPadding);
                int h = height - (vPadding * 2);
                int size = Math.min(w, h);
                int vOffset = (arrowHeight / 2) + arrowPadding;
                int x = ((w - size) / 2) + lPadding;
                int y = ((h - size) / 2) + vPadding + (w > h ? vOffset : 0);
                int i4 = width;
                int i5 = height;
                int i6 = padding;
                int i7 = lPadding;
                int i8 = rPadding;
                this.imageReceiver.setImageCoords((float) x, (float) y, (float) size, (float) (size - (w > h ? vOffset : 0)));
                int cx = (int) this.imageReceiver.getCenterX();
                int cy = (int) this.imageReceiver.getCenterY();
                RadialProgress2 radialProgress2 = this.radialProgress;
                int i9 = this.radialProgressSize;
                int i10 = vPadding;
                int i11 = w;
                radialProgress2.setProgressRect(cx - (i9 / 2), cy - (i9 / 2), cx + (i9 / 2), (i9 / 2) + cy);
                int arrowX = (size / 2) + x;
                int arrowY = y - arrowPadding;
                int i12 = cx;
                this.arrowDrawable.setBounds(arrowX - (arrowWidth / 2), arrowY - (arrowHeight / 2), arrowX + (arrowWidth / 2), arrowY + (arrowHeight / 2));
            }
        }

        /* access modifiers changed from: protected */
        /* JADX WARNING: Removed duplicated region for block: B:17:0x0062  */
        /* JADX WARNING: Removed duplicated region for block: B:20:0x0082  */
        /* JADX WARNING: Removed duplicated region for block: B:21:0x0085  */
        /* JADX WARNING: Removed duplicated region for block: B:24:0x008a  */
        /* JADX WARNING: Removed duplicated region for block: B:25:0x0091  */
        /* JADX WARNING: Removed duplicated region for block: B:28:0x00ca  */
        /* JADX WARNING: Removed duplicated region for block: B:29:0x00d7  */
        /* JADX WARNING: Removed duplicated region for block: B:32:0x00f2  */
        /* JADX WARNING: Removed duplicated region for block: B:52:0x01b9  */
        /* JADX WARNING: Removed duplicated region for block: B:55:0x01c1  */
        /* JADX WARNING: Removed duplicated region for block: B:56:0x01d2  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onDraw(android.graphics.Canvas r19) {
            /*
                r18 = this;
                r0 = r18
                r1 = r19
                long r2 = android.view.animation.AnimationUtils.currentAnimationTimeMillis()
                long r4 = r0.lastUpdateTime
                long r4 = r2 - r4
                r0.lastUpdateTime = r2
                boolean r6 = r0.showing
                r7 = 1125515264(0x43160000, float:150.0)
                r8 = 0
                r9 = 1065353216(0x3var_, float:1.0)
                if (r6 == 0) goto L_0x002d
                float r10 = r0.progress
                int r11 = (r10 > r9 ? 1 : (r10 == r9 ? 0 : -1))
                if (r11 >= 0) goto L_0x002d
                float r6 = (float) r4
                float r6 = r6 / r7
                float r10 = r10 + r6
                r0.progress = r10
                int r6 = (r10 > r9 ? 1 : (r10 == r9 ? 0 : -1))
                if (r6 >= 0) goto L_0x002a
                r18.postInvalidateOnAnimation()
                goto L_0x0047
            L_0x002a:
                r0.progress = r9
                goto L_0x0047
            L_0x002d:
                if (r6 != 0) goto L_0x0047
                float r6 = r0.progress
                int r10 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
                if (r10 <= 0) goto L_0x0047
                float r10 = (float) r4
                float r10 = r10 / r7
                float r6 = r6 - r10
                r0.progress = r6
                int r6 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
                if (r6 <= 0) goto L_0x0042
                r18.postInvalidateOnAnimation()
                goto L_0x0047
            L_0x0042:
                r0.progress = r8
                r18.onHide()
            L_0x0047:
                android.view.animation.Interpolator r6 = r0.interpolator
                float r7 = r0.progress
                float r6 = r6.getInterpolation(r7)
                android.graphics.drawable.ColorDrawable r7 = r0.backgroundDrawable
                r10 = 1127481344(0x43340000, float:180.0)
                float r10 = r10 * r6
                int r10 = (int) r10
                r7.setAlpha(r10)
                android.graphics.drawable.ColorDrawable r7 = r0.backgroundDrawable
                r7.draw(r1)
                int r7 = (r6 > r9 ? 1 : (r6 == r9 ? 0 : -1))
                if (r7 >= 0) goto L_0x007c
                r7 = 1064514355(0x3var_, float:0.95)
                float r10 = org.telegram.messenger.AndroidUtilities.lerp((float) r7, (float) r9, (float) r6)
                float r7 = org.telegram.messenger.AndroidUtilities.lerp((float) r7, (float) r9, (float) r6)
                org.telegram.messenger.ImageReceiver r11 = r0.imageReceiver
                float r11 = r11.getCenterX()
                org.telegram.messenger.ImageReceiver r12 = r0.imageReceiver
                float r12 = r12.getCenterY()
                r1.scale(r10, r7, r11, r12)
            L_0x007c:
                int r7 = android.os.Build.VERSION.SDK_INT
                r10 = 21
                if (r7 < r10) goto L_0x0085
                int r7 = org.telegram.messenger.AndroidUtilities.statusBarHeight
                goto L_0x0086
            L_0x0085:
                r7 = 0
            L_0x0086:
                int r12 = android.os.Build.VERSION.SDK_INT
                if (r12 < r10) goto L_0x0091
                android.view.WindowInsets r10 = r0.insets
                int r10 = r10.getStableInsetBottom()
                goto L_0x0092
            L_0x0091:
                r10 = 0
            L_0x0092:
                org.telegram.ui.AvatarPreviewer$MenuItem[] r12 = r0.menuItems
                int r12 = r12.length
                r13 = 1111490560(0x42400000, float:48.0)
                int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
                int r12 = r12 * r13
                r13 = 1098907648(0x41800000, float:16.0)
                int r14 = org.telegram.messenger.AndroidUtilities.dp(r13)
                int r12 = r12 + r14
                int r14 = r18.getHeight()
                int r15 = r10 + r12
                int r16 = org.telegram.messenger.AndroidUtilities.dp(r13)
                int r15 = r15 + r16
                int r14 = r14 - r15
                float r14 = (float) r14
                org.telegram.messenger.ImageReceiver r15 = r0.imageReceiver
                float r15 = r15.getImageY2()
                float r15 = r14 - r15
                float r15 = java.lang.Math.min(r8, r15)
                org.telegram.messenger.ImageReceiver r11 = r0.imageReceiver
                float r11 = r11.getImageY()
                float r11 = r11 + r15
                float r9 = (float) r7
                int r9 = (r11 > r9 ? 1 : (r11 == r9 ? 0 : -1))
                if (r9 >= 0) goto L_0x00d7
                float r9 = r0.moveProgress
                int r11 = org.telegram.messenger.AndroidUtilities.dp(r13)
                float r11 = (float) r11
                float r9 = r9 * r11
                r1.translate(r8, r9)
                goto L_0x00e4
            L_0x00d7:
                float r9 = r0.moveProgress
                int r11 = org.telegram.messenger.AndroidUtilities.dp(r13)
                float r11 = (float) r11
                float r9 = r9 * r11
                float r9 = r9 + r15
                r1.translate(r8, r9)
            L_0x00e4:
                org.telegram.messenger.ImageReceiver r9 = r0.imageReceiver
                r9.setAlpha(r6)
                org.telegram.messenger.ImageReceiver r9 = r0.imageReceiver
                r9.draw(r1)
                boolean r9 = r0.showProgress
                if (r9 == 0) goto L_0x01b9
                org.telegram.messenger.ImageReceiver r9 = r0.imageReceiver
                android.graphics.drawable.Drawable r9 = r9.getDrawable()
                boolean r11 = r9 instanceof org.telegram.ui.Components.AnimatedFileDrawable
                r17 = r14
                if (r11 == 0) goto L_0x015e
                r11 = r9
                org.telegram.ui.Components.AnimatedFileDrawable r11 = (org.telegram.ui.Components.AnimatedFileDrawable) r11
                int r11 = r11.getDurationMs()
                if (r11 <= 0) goto L_0x015e
                android.animation.ValueAnimator r11 = r0.progressShowAnimator
                if (r11 == 0) goto L_0x015a
                r11.cancel()
                org.telegram.ui.Components.RadialProgress2 r11 = r0.radialProgress
                float r11 = r11.getProgress()
                r13 = 1
                r14 = 1065353216(0x3var_, float:1.0)
                int r11 = (r11 > r14 ? 1 : (r11 == r14 ? 0 : -1))
                if (r11 >= 0) goto L_0x0120
                org.telegram.ui.Components.RadialProgress2 r11 = r0.radialProgress
                r11.setProgress(r14, r13)
            L_0x0120:
                r11 = 2
                float[] r11 = new float[r11]
                android.animation.ValueAnimator r14 = r0.progressShowAnimator
                java.lang.Object r14 = r14.getAnimatedValue()
                java.lang.Float r14 = (java.lang.Float) r14
                float r14 = r14.floatValue()
                r16 = 0
                r11[r16] = r14
                r11[r13] = r8
                android.animation.ValueAnimator r8 = android.animation.ValueAnimator.ofFloat(r11)
                r0.progressHideAnimator = r8
                org.telegram.ui.AvatarPreviewer$Layout$1 r11 = new org.telegram.ui.AvatarPreviewer$Layout$1
                r11.<init>()
                r8.addListener(r11)
                android.animation.ValueAnimator r8 = r0.progressHideAnimator
                org.telegram.ui.AvatarPreviewer$Layout$$ExternalSyntheticLambda0 r11 = new org.telegram.ui.AvatarPreviewer$Layout$$ExternalSyntheticLambda0
                r11.<init>(r0)
                r8.addUpdateListener(r11)
                android.animation.ValueAnimator r8 = r0.progressHideAnimator
                r13 = 250(0xfa, double:1.235E-321)
                r8.setDuration(r13)
                android.animation.ValueAnimator r8 = r0.progressHideAnimator
                r8.start()
                goto L_0x0187
            L_0x015a:
                r8 = 0
                r0.showProgress = r8
                goto L_0x0187
            L_0x015e:
                android.animation.ValueAnimator r8 = r0.progressShowAnimator
                if (r8 != 0) goto L_0x0187
                r8 = 2
                float[] r8 = new float[r8]
                r8 = {0, NUM} // fill-array
                android.animation.ValueAnimator r8 = android.animation.ValueAnimator.ofFloat(r8)
                r0.progressShowAnimator = r8
                org.telegram.ui.AvatarPreviewer$Layout$$ExternalSyntheticLambda1 r11 = new org.telegram.ui.AvatarPreviewer$Layout$$ExternalSyntheticLambda1
                r11.<init>(r0)
                r8.addUpdateListener(r11)
                android.animation.ValueAnimator r8 = r0.progressShowAnimator
                r13 = 250(0xfa, double:1.235E-321)
                r8.setStartDelay(r13)
                android.animation.ValueAnimator r8 = r0.progressShowAnimator
                r8.setDuration(r13)
                android.animation.ValueAnimator r8 = r0.progressShowAnimator
                r8.start()
            L_0x0187:
                android.animation.ValueAnimator r8 = r0.progressHideAnimator
                if (r8 == 0) goto L_0x01a0
                org.telegram.ui.Components.RadialProgress2 r11 = r0.radialProgress
                java.lang.Object r8 = r8.getAnimatedValue()
                java.lang.Float r8 = (java.lang.Float) r8
                float r8 = r8.floatValue()
                r11.setOverrideAlpha(r8)
                org.telegram.ui.Components.RadialProgress2 r8 = r0.radialProgress
                r8.draw(r1)
                goto L_0x01bb
            L_0x01a0:
                android.animation.ValueAnimator r8 = r0.progressShowAnimator
                if (r8 == 0) goto L_0x01bb
                org.telegram.ui.Components.RadialProgress2 r11 = r0.radialProgress
                java.lang.Object r8 = r8.getAnimatedValue()
                java.lang.Float r8 = (java.lang.Float) r8
                float r8 = r8.floatValue()
                r11.setOverrideAlpha(r8)
                org.telegram.ui.Components.RadialProgress2 r8 = r0.radialProgress
                r8.draw(r1)
                goto L_0x01bb
            L_0x01b9:
                r17 = r14
            L_0x01bb:
                android.animation.ValueAnimator r8 = r0.moveAnimator
                r9 = 1132396544(0x437var_, float:255.0)
                if (r8 == 0) goto L_0x01d2
                android.graphics.drawable.Drawable r11 = r0.arrowDrawable
                float r8 = r8.getAnimatedFraction()
                r13 = 1065353216(0x3var_, float:1.0)
                float r8 = r13 - r8
                float r8 = r8 * r9
                int r8 = (int) r8
                r11.setAlpha(r8)
                goto L_0x01da
            L_0x01d2:
                android.graphics.drawable.Drawable r8 = r0.arrowDrawable
                float r9 = r9 * r6
                int r9 = (int) r9
                r8.setAlpha(r9)
            L_0x01da:
                android.graphics.drawable.Drawable r8 = r0.arrowDrawable
                r8.draw(r1)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.AvatarPreviewer.Layout.onDraw(android.graphics.Canvas):void");
        }

        /* renamed from: lambda$onDraw$3$org-telegram-ui-AvatarPreviewer$Layout  reason: not valid java name */
        public /* synthetic */ void m2714lambda$onDraw$3$orgtelegramuiAvatarPreviewer$Layout(ValueAnimator a) {
            invalidate();
        }

        /* renamed from: lambda$onDraw$4$org-telegram-ui-AvatarPreviewer$Layout  reason: not valid java name */
        public /* synthetic */ void m2715lambda$onDraw$4$orgtelegramuiAvatarPreviewer$Layout(ValueAnimator a) {
            invalidate();
        }

        public void setData(Data data) {
            this.menuItems = data.menuItems;
            this.showProgress = data.videoLocation != null;
            this.videoFileName = data.videoFileName;
            recycleInfoLoadTask();
            if (data.infoLoadTask != null) {
                InfoLoadTask<?, ?> access$1100 = data.infoLoadTask;
                this.infoLoadTask = access$1100;
                access$1100.load(new AvatarPreviewer$Layout$$ExternalSyntheticLambda5(this, data));
            } else {
                Data data2 = data;
            }
            this.imageReceiver.setCurrentAccount(UserConfig.selectedAccount);
            this.imageReceiver.setImage(data.videoLocation, data.videoFilter, data.imageLocation, data.imageFilter, data.thumbImageLocation, data.thumbImageFilter, (Drawable) null, 0, (String) null, data.parentObject, 1);
            setShowing(true);
        }

        /* renamed from: lambda$setData$5$org-telegram-ui-AvatarPreviewer$Layout  reason: not valid java name */
        public /* synthetic */ void m2717lambda$setData$5$orgtelegramuiAvatarPreviewer$Layout(Data data, Object result) {
            if (this.recycled) {
                return;
            }
            if (result instanceof TLRPC.UserFull) {
                setData(Data.of((TLRPC.UserFull) result, data.menuItems));
            } else if (result instanceof TLRPC.ChatFull) {
                setData(Data.of((TLRPC.Chat) data.infoLoadTask.argument, (TLRPC.ChatFull) result, data.menuItems));
            }
        }

        private void setShowing(boolean showing2) {
            if (this.showing != showing2) {
                this.showing = showing2;
                this.lastUpdateTime = AnimationUtils.currentAnimationTimeMillis();
                invalidate();
            }
        }

        public void recycle() {
            this.recycled = true;
            ValueAnimator valueAnimator = this.moveAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            BottomSheet bottomSheet = this.visibleSheet;
            if (bottomSheet != null) {
                bottomSheet.cancel();
            }
            recycleInfoLoadTask();
        }

        private void recycleInfoLoadTask() {
            InfoLoadTask<?, ?> infoLoadTask2 = this.infoLoadTask;
            if (infoLoadTask2 != null) {
                infoLoadTask2.cancel();
                this.infoLoadTask = null;
            }
        }
    }
}
