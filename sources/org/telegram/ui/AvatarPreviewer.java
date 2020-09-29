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
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.tgnet.TLRPC$Photo;
import org.telegram.tgnet.TLRPC$TL_photoStrippedSize;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$UserFull;
import org.telegram.tgnet.TLRPC$VideoSize;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.AvatarPreviewer;
import org.telegram.ui.Components.RadialProgress2;

public class AvatarPreviewer {
    private static AvatarPreviewer INSTANCE;
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

    public void show(ViewGroup viewGroup, Data data, Callback callback) {
        Preconditions.checkNotNull(viewGroup);
        Preconditions.checkNotNull(data);
        Preconditions.checkNotNull(callback);
        Context context = viewGroup.getContext();
        if (this.view != viewGroup) {
            close();
            this.view = viewGroup;
            this.windowManager = (WindowManager) ContextCompat.getSystemService(context, WindowManager.class);
            this.layout = new Layout(context, callback) {
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
            viewGroup.requestDisallowInterceptTouchEvent(true);
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
            this.windowManager = null;
        }
    }

    public void onTouchEvent(MotionEvent motionEvent) {
        Layout layout2 = this.layout;
        if (layout2 != null) {
            layout2.onTouchEvent(motionEvent);
        }
    }

    public enum MenuItem {
        OPEN_PROFILE("OpenProfile", NUM, NUM),
        OPEN_CHANNEL("OpenChannel2", NUM, NUM),
        SEND_MESSAGE("SendMessage", NUM, NUM),
        MENTION("Mention", NUM, NUM);
        
        /* access modifiers changed from: private */
        public final int iconResId;
        /* access modifiers changed from: private */
        public final String labelKey;
        /* access modifiers changed from: private */
        public final int labelResId;

        private MenuItem(String str, int i, int i2) {
            this.labelKey = str;
            this.labelResId = i;
            this.iconResId = i2;
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

        public static Data of(TLRPC$User tLRPC$User, int i, MenuItem... menuItemArr) {
            ImageLocation forUser = ImageLocation.getForUser(tLRPC$User, true);
            ImageLocation forUser2 = ImageLocation.getForUser(tLRPC$User, false);
            return new Data(forUser, forUser2, (ImageLocation) null, (String) null, (forUser2 == null || !(forUser2.photoSize instanceof TLRPC$TL_photoStrippedSize)) ? null : "b", (String) null, (String) null, tLRPC$User, menuItemArr, new UserInfoLoadTask(tLRPC$User, i));
        }

        public static Data of(TLRPC$UserFull tLRPC$UserFull, MenuItem... menuItemArr) {
            String str;
            ImageLocation imageLocation2;
            ImageLocation forUser = ImageLocation.getForUser(tLRPC$UserFull.user, true);
            ImageLocation forUser2 = ImageLocation.getForUser(tLRPC$UserFull.user, false);
            String str2 = null;
            String str3 = (forUser2 == null || !(forUser2.photoSize instanceof TLRPC$TL_photoStrippedSize)) ? null : "b";
            TLRPC$Photo tLRPC$Photo = tLRPC$UserFull.profile_photo;
            if (tLRPC$Photo == null || tLRPC$Photo.video_sizes.isEmpty()) {
                imageLocation2 = null;
                str = null;
            } else {
                TLRPC$VideoSize tLRPC$VideoSize = tLRPC$UserFull.profile_photo.video_sizes.get(0);
                ImageLocation forPhoto = ImageLocation.getForPhoto(tLRPC$VideoSize, tLRPC$UserFull.profile_photo);
                str = FileLoader.getAttachFileName(tLRPC$VideoSize);
                imageLocation2 = forPhoto;
            }
            if (imageLocation2 != null && imageLocation2.imageType == 2) {
                str2 = "g";
            }
            return new Data(forUser, forUser2, imageLocation2, (String) null, str3, str2, str, tLRPC$UserFull.user, menuItemArr, (InfoLoadTask<?, ?>) null);
        }

        public static Data of(TLRPC$Chat tLRPC$Chat, int i, MenuItem... menuItemArr) {
            ImageLocation forChat = ImageLocation.getForChat(tLRPC$Chat, true);
            ImageLocation forChat2 = ImageLocation.getForChat(tLRPC$Chat, false);
            return new Data(forChat, forChat2, (ImageLocation) null, (String) null, (forChat2 == null || !(forChat2.photoSize instanceof TLRPC$TL_photoStrippedSize)) ? null : "b", (String) null, (String) null, tLRPC$Chat, menuItemArr, new ChatInfoLoadTask(tLRPC$Chat, i));
        }

        public static Data of(TLRPC$Chat tLRPC$Chat, TLRPC$ChatFull tLRPC$ChatFull, MenuItem... menuItemArr) {
            String str;
            ImageLocation imageLocation2;
            ImageLocation forChat = ImageLocation.getForChat(tLRPC$Chat, true);
            ImageLocation forChat2 = ImageLocation.getForChat(tLRPC$Chat, false);
            String str2 = (forChat2 == null || !(forChat2.photoSize instanceof TLRPC$TL_photoStrippedSize)) ? null : "b";
            TLRPC$Photo tLRPC$Photo = tLRPC$ChatFull.chat_photo;
            if (tLRPC$Photo == null || tLRPC$Photo.video_sizes.isEmpty()) {
                imageLocation2 = null;
                str = null;
            } else {
                TLRPC$VideoSize tLRPC$VideoSize = tLRPC$ChatFull.chat_photo.video_sizes.get(0);
                imageLocation2 = ImageLocation.getForPhoto(tLRPC$VideoSize, tLRPC$ChatFull.chat_photo);
                str = FileLoader.getAttachFileName(tLRPC$VideoSize);
            }
            return new Data(forChat, forChat2, imageLocation2, (String) null, str2, (imageLocation2 == null || imageLocation2.imageType != 2) ? null : "g", str, tLRPC$Chat, menuItemArr, (InfoLoadTask<?, ?>) null);
        }

        private Data(ImageLocation imageLocation2, ImageLocation imageLocation3, ImageLocation imageLocation4, String str, String str2, String str3, String str4, Object obj, MenuItem[] menuItemArr, InfoLoadTask<?, ?> infoLoadTask2) {
            this.imageLocation = imageLocation2;
            this.thumbImageLocation = imageLocation3;
            this.videoLocation = imageLocation4;
            this.imageFilter = str;
            this.thumbImageFilter = str2;
            this.videoFilter = str3;
            this.videoFileName = str4;
            this.parentObject = obj;
            this.menuItems = menuItemArr;
            this.infoLoadTask = infoLoadTask2;
        }
    }

    private static class UserInfoLoadTask extends InfoLoadTask<TLRPC$User, TLRPC$UserFull> {
        public UserInfoLoadTask(TLRPC$User tLRPC$User, int i) {
            super(tLRPC$User, i, NotificationCenter.userInfoDidLoad);
        }

        /* access modifiers changed from: protected */
        public void load() {
            MessagesController.getInstance(UserConfig.selectedAccount).loadUserInfo((TLRPC$User) this.argument, false, this.classGuid);
        }

        /* access modifiers changed from: protected */
        public void onReceiveNotification(Object... objArr) {
            if (objArr[0].intValue() == ((TLRPC$User) this.argument).id) {
                onResult(objArr[1]);
            }
        }
    }

    private static class ChatInfoLoadTask extends InfoLoadTask<TLRPC$Chat, TLRPC$ChatFull> {
        public ChatInfoLoadTask(TLRPC$Chat tLRPC$Chat, int i) {
            super(tLRPC$Chat, i, NotificationCenter.chatInfoDidLoad);
        }

        /* access modifiers changed from: protected */
        public void load() {
            MessagesController.getInstance(UserConfig.selectedAccount).loadFullChat(((TLRPC$Chat) this.argument).id, this.classGuid, false);
        }

        /* access modifiers changed from: protected */
        public void onReceiveNotification(Object... objArr) {
            TLRPC$ChatFull tLRPC$ChatFull = objArr[0];
            if (tLRPC$ChatFull != null && tLRPC$ChatFull.id == ((TLRPC$Chat) this.argument).id) {
                onResult(tLRPC$ChatFull);
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
            public void didReceivedNotification(int i, int i2, Object... objArr) {
                if (InfoLoadTask.this.loading && i == InfoLoadTask.this.notificationId) {
                    InfoLoadTask.this.onReceiveNotification(objArr);
                }
            }
        };
        private Consumer<B> onResult;

        /* access modifiers changed from: protected */
        public abstract void load();

        /* access modifiers changed from: protected */
        public abstract void onReceiveNotification(Object... objArr);

        public InfoLoadTask(A a, int i, int i2) {
            this.argument = a;
            this.classGuid = i;
            this.notificationId = i2;
            this.notificationCenter = NotificationCenter.getInstance(UserConfig.selectedAccount);
        }

        public final void load(Consumer<B> consumer) {
            if (!this.loading) {
                this.loading = true;
                this.onResult = consumer;
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
        public final void onResult(B b) {
            if (this.loading) {
                cancel();
                this.onResult.accept(b);
            }
        }
    }

    private static abstract class Layout extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
        private final Drawable arrowDrawable;
        private final ColorDrawable backgroundDrawable;
        private final Callback callback;
        private float downY;
        private final ImageReceiver imageReceiver;
        private InfoLoadTask<?, ?> infoLoadTask;
        private WindowInsets insets;
        private final Interpolator interpolator;
        private long lastUpdateTime;
        private MenuItem[] menuItems;
        private ValueAnimator moveAnimator;
        private float moveProgress;
        private float progress;
        private ValueAnimator progressHideAnimator;
        private ValueAnimator progressShowAnimator;
        private final RadialProgress2 radialProgress;
        private final int radialProgressSize = AndroidUtilities.dp(64.0f);
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
            new Rect();
            this.interpolator = new AccelerateDecelerateInterpolator();
            this.backgroundDrawable = new ColorDrawable(NUM);
            this.imageReceiver = new ImageReceiver();
            this.downY = -1.0f;
            this.callback = callback2;
            setWillNotDraw(false);
            setFitsSystemWindows(true);
            this.imageReceiver.setAspectFit(true);
            this.imageReceiver.setInvalidateAll(true);
            this.imageReceiver.setRoundRadius(AndroidUtilities.dp(6.0f));
            this.imageReceiver.setParentView(this);
            RadialProgress2 radialProgress2 = new RadialProgress2(this);
            this.radialProgress = radialProgress2;
            radialProgress2.setOverrideAlpha(0.0f);
            this.radialProgress.setIcon(10, false, false);
            this.radialProgress.setColors(NUM, NUM, -1, -1);
            this.arrowDrawable = ContextCompat.getDrawable(context, NUM);
        }

        /* access modifiers changed from: protected */
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            NotificationCenter.getInstance(UserConfig.selectedAccount).addObserver(this, NotificationCenter.fileDidLoad);
            NotificationCenter.getInstance(UserConfig.selectedAccount).addObserver(this, NotificationCenter.FileLoadProgressChanged);
        }

        /* access modifiers changed from: protected */
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            NotificationCenter.getInstance(UserConfig.selectedAccount).removeObserver(this, NotificationCenter.fileDidLoad);
            NotificationCenter.getInstance(UserConfig.selectedAccount).removeObserver(this, NotificationCenter.FileLoadProgressChanged);
        }

        public void didReceivedNotification(int i, int i2, Object... objArr) {
            if (this.showProgress && !TextUtils.isEmpty(this.videoFileName)) {
                if (i == NotificationCenter.fileDidLoad) {
                    if (TextUtils.equals(objArr[0], this.videoFileName)) {
                        this.radialProgress.setProgress(1.0f, true);
                    }
                } else if (i == NotificationCenter.FileLoadProgressChanged && TextUtils.equals(objArr[0], this.videoFileName) && this.radialProgress != null) {
                    this.radialProgress.setProgress(Math.min(1.0f, ((float) objArr[1].longValue()) / ((float) objArr[2].longValue())), true);
                }
            }
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (!this.showing) {
                return false;
            }
            if (this.moveAnimator == null) {
                if (motionEvent.getActionMasked() == 1) {
                    this.downY = -1.0f;
                    setShowing(false);
                } else if (motionEvent.getActionMasked() == 2) {
                    if (this.downY < 0.0f) {
                        this.downY = motionEvent.getY();
                    } else {
                        float max = Math.max(-1.0f, Math.min(0.0f, (motionEvent.getY() - this.downY) / ((float) AndroidUtilities.dp(56.0f))));
                        this.moveProgress = max;
                        if (max == -1.0f) {
                            performHapticFeedback(0);
                            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{this.moveProgress, 0.0f});
                            this.moveAnimator = ofFloat;
                            ofFloat.setDuration(200);
                            this.moveAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                                    AvatarPreviewer.Layout.this.lambda$onTouchEvent$0$AvatarPreviewer$Layout(valueAnimator);
                                }
                            });
                            this.moveAnimator.start();
                            showBottomSheet();
                        }
                        invalidate();
                    }
                }
            }
            return true;
        }

        public /* synthetic */ void lambda$onTouchEvent$0$AvatarPreviewer$Layout(ValueAnimator valueAnimator) {
            this.moveProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            invalidate();
        }

        private void showBottomSheet() {
            MenuItem[] menuItemArr = this.menuItems;
            CharSequence[] charSequenceArr = new CharSequence[menuItemArr.length];
            int[] iArr = new int[menuItemArr.length];
            int i = 0;
            while (true) {
                MenuItem[] menuItemArr2 = this.menuItems;
                if (i < menuItemArr2.length) {
                    charSequenceArr[i] = LocaleController.getString(menuItemArr2[i].labelKey, this.menuItems[i].labelResId);
                    iArr[i] = this.menuItems[i].iconResId;
                    i++;
                } else {
                    BottomSheet.Builder builder = new BottomSheet.Builder(getContext());
                    builder.setItems(charSequenceArr, iArr, new DialogInterface.OnClickListener() {
                        public final void onClick(DialogInterface dialogInterface, int i) {
                            AvatarPreviewer.Layout.this.lambda$showBottomSheet$1$AvatarPreviewer$Layout(dialogInterface, i);
                        }
                    });
                    BottomSheet dimBehind = builder.setDimBehind(false);
                    this.visibleSheet = dimBehind;
                    dimBehind.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        public final void onDismiss(DialogInterface dialogInterface) {
                            AvatarPreviewer.Layout.this.lambda$showBottomSheet$2$AvatarPreviewer$Layout(dialogInterface);
                        }
                    });
                    this.visibleSheet.show();
                    return;
                }
            }
        }

        public /* synthetic */ void lambda$showBottomSheet$1$AvatarPreviewer$Layout(DialogInterface dialogInterface, int i) {
            this.callback.onMenuClick(this.menuItems[i]);
            setShowing(false);
        }

        public /* synthetic */ void lambda$showBottomSheet$2$AvatarPreviewer$Layout(DialogInterface dialogInterface) {
            this.visibleSheet = null;
            setShowing(false);
        }

        public WindowInsets onApplyWindowInsets(WindowInsets windowInsets) {
            this.insets = windowInsets;
            invalidateSize();
            return windowInsets.consumeStableInsets();
        }

        /* access modifiers changed from: protected */
        public void onSizeChanged(int i, int i2, int i3, int i4) {
            invalidateSize();
        }

        public void invalidateSize() {
            int i;
            int i2;
            int width = getWidth();
            int height = getHeight();
            if (width != 0 && height != 0) {
                int i3 = 0;
                this.backgroundDrawable.setBounds(0, 0, width, height);
                int dp = AndroidUtilities.dp(8.0f);
                if (Build.VERSION.SDK_INT >= 21) {
                    int stableInsetLeft = this.insets.getStableInsetLeft() + dp;
                    i = this.insets.getStableInsetRight() + dp;
                    int i4 = stableInsetLeft;
                    i2 = dp + Math.max(this.insets.getStableInsetTop(), this.insets.getStableInsetBottom());
                    dp = i4;
                } else {
                    i2 = dp;
                    i = i2;
                }
                int intrinsicWidth = this.arrowDrawable.getIntrinsicWidth();
                int intrinsicHeight = this.arrowDrawable.getIntrinsicHeight();
                int dp2 = AndroidUtilities.dp(24.0f);
                int i5 = width - (i + dp);
                int i6 = height - (i2 * 2);
                int min = Math.min(i5, i6);
                int i7 = intrinsicHeight / 2;
                int i8 = dp2 + i7;
                int i9 = ((i5 - min) / 2) + dp;
                int i10 = ((i6 - min) / 2) + i2 + (i5 > i6 ? i8 : 0);
                ImageReceiver imageReceiver2 = this.imageReceiver;
                float f = (float) i9;
                float f2 = (float) i10;
                float f3 = (float) min;
                if (i5 > i6) {
                    i3 = i8;
                }
                imageReceiver2.setImageCoords(f, f2, f3, (float) (min - i3));
                int centerX = (int) this.imageReceiver.getCenterX();
                int centerY = (int) this.imageReceiver.getCenterY();
                RadialProgress2 radialProgress2 = this.radialProgress;
                int i11 = this.radialProgressSize;
                radialProgress2.setProgressRect(centerX - (i11 / 2), centerY - (i11 / 2), centerX + (i11 / 2), centerY + (i11 / 2));
                int i12 = i9 + (min / 2);
                int i13 = i10 - dp2;
                int i14 = intrinsicWidth / 2;
                this.arrowDrawable.setBounds(i12 - i14, i13 - i7, i12 + i14, i13 + i7);
            }
        }

        /* access modifiers changed from: protected */
        /* JADX WARNING: Removed duplicated region for block: B:18:0x0060  */
        /* JADX WARNING: Removed duplicated region for block: B:21:0x0081  */
        /* JADX WARNING: Removed duplicated region for block: B:22:0x0084  */
        /* JADX WARNING: Removed duplicated region for block: B:25:0x0089  */
        /* JADX WARNING: Removed duplicated region for block: B:26:0x0090  */
        /* JADX WARNING: Removed duplicated region for block: B:29:0x00c6  */
        /* JADX WARNING: Removed duplicated region for block: B:30:0x00d3  */
        /* JADX WARNING: Removed duplicated region for block: B:33:0x00ee  */
        /* JADX WARNING: Removed duplicated region for block: B:55:0x01af  */
        /* JADX WARNING: Removed duplicated region for block: B:56:0x01bd  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onDraw(android.graphics.Canvas r10) {
            /*
                r9 = this;
                long r0 = android.view.animation.AnimationUtils.currentAnimationTimeMillis()
                long r2 = r9.lastUpdateTime
                long r2 = r0 - r2
                r9.lastUpdateTime = r0
                boolean r0 = r9.showing
                r1 = 1125515264(0x43160000, float:150.0)
                r4 = 0
                r5 = 1065353216(0x3var_, float:1.0)
                if (r0 == 0) goto L_0x0029
                float r0 = r9.progress
                int r6 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1))
                if (r6 >= 0) goto L_0x0029
                float r2 = (float) r2
                float r2 = r2 / r1
                float r0 = r0 + r2
                r9.progress = r0
                int r0 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1))
                if (r0 >= 0) goto L_0x0026
                r9.postInvalidateOnAnimation()
                goto L_0x0045
            L_0x0026:
                r9.progress = r5
                goto L_0x0045
            L_0x0029:
                boolean r0 = r9.showing
                if (r0 != 0) goto L_0x0045
                float r0 = r9.progress
                int r6 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
                if (r6 <= 0) goto L_0x0045
                float r2 = (float) r2
                float r2 = r2 / r1
                float r0 = r0 - r2
                r9.progress = r0
                int r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
                if (r0 <= 0) goto L_0x0040
                r9.postInvalidateOnAnimation()
                goto L_0x0045
            L_0x0040:
                r9.progress = r4
                r9.onHide()
            L_0x0045:
                android.view.animation.Interpolator r0 = r9.interpolator
                float r1 = r9.progress
                float r0 = r0.getInterpolation(r1)
                android.graphics.drawable.ColorDrawable r1 = r9.backgroundDrawable
                r2 = 1127481344(0x43340000, float:180.0)
                float r2 = r2 * r0
                int r2 = (int) r2
                r1.setAlpha(r2)
                android.graphics.drawable.ColorDrawable r1 = r9.backgroundDrawable
                r1.draw(r10)
                int r1 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1))
                if (r1 >= 0) goto L_0x007a
                r1 = 1064514355(0x3var_, float:0.95)
                float r2 = org.telegram.messenger.AndroidUtilities.lerp(r1, r5, r0)
                float r1 = org.telegram.messenger.AndroidUtilities.lerp(r1, r5, r0)
                org.telegram.messenger.ImageReceiver r3 = r9.imageReceiver
                float r3 = r3.getCenterX()
                org.telegram.messenger.ImageReceiver r6 = r9.imageReceiver
                float r6 = r6.getCenterY()
                r10.scale(r2, r1, r3, r6)
            L_0x007a:
                int r1 = android.os.Build.VERSION.SDK_INT
                r2 = 21
                r3 = 0
                if (r1 < r2) goto L_0x0084
                int r1 = org.telegram.messenger.AndroidUtilities.statusBarHeight
                goto L_0x0085
            L_0x0084:
                r1 = 0
            L_0x0085:
                int r6 = android.os.Build.VERSION.SDK_INT
                if (r6 < r2) goto L_0x0090
                android.view.WindowInsets r2 = r9.insets
                int r2 = r2.getStableInsetBottom()
                goto L_0x0091
            L_0x0090:
                r2 = 0
            L_0x0091:
                org.telegram.ui.AvatarPreviewer$MenuItem[] r6 = r9.menuItems
                int r6 = r6.length
                r7 = 1111490560(0x42400000, float:48.0)
                int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
                int r6 = r6 * r7
                r7 = 1098907648(0x41800000, float:16.0)
                int r8 = org.telegram.messenger.AndroidUtilities.dp(r7)
                int r6 = r6 + r8
                int r8 = r9.getHeight()
                int r2 = r2 + r6
                int r6 = org.telegram.messenger.AndroidUtilities.dp(r7)
                int r2 = r2 + r6
                int r8 = r8 - r2
                float r2 = (float) r8
                org.telegram.messenger.ImageReceiver r6 = r9.imageReceiver
                float r6 = r6.getImageY2()
                float r2 = r2 - r6
                float r2 = java.lang.Math.min(r4, r2)
                org.telegram.messenger.ImageReceiver r6 = r9.imageReceiver
                float r6 = r6.getImageY()
                float r6 = r6 + r2
                float r1 = (float) r1
                int r1 = (r6 > r1 ? 1 : (r6 == r1 ? 0 : -1))
                if (r1 >= 0) goto L_0x00d3
                float r1 = r9.moveProgress
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r7)
                float r2 = (float) r2
                float r1 = r1 * r2
                r10.translate(r4, r1)
                goto L_0x00e0
            L_0x00d3:
                float r1 = r9.moveProgress
                int r6 = org.telegram.messenger.AndroidUtilities.dp(r7)
                float r6 = (float) r6
                float r1 = r1 * r6
                float r2 = r2 + r1
                r10.translate(r4, r2)
            L_0x00e0:
                org.telegram.messenger.ImageReceiver r1 = r9.imageReceiver
                r1.setAlpha(r0)
                org.telegram.messenger.ImageReceiver r1 = r9.imageReceiver
                r1.draw(r10)
                boolean r1 = r9.showProgress
                if (r1 == 0) goto L_0x01a9
                org.telegram.messenger.ImageReceiver r1 = r9.imageReceiver
                android.graphics.drawable.Drawable r1 = r1.getDrawable()
                boolean r2 = r1 instanceof org.telegram.ui.Components.AnimatedFileDrawable
                r6 = 2
                r7 = 250(0xfa, double:1.235E-321)
                if (r2 == 0) goto L_0x0152
                org.telegram.ui.Components.AnimatedFileDrawable r1 = (org.telegram.ui.Components.AnimatedFileDrawable) r1
                int r1 = r1.getDurationMs()
                if (r1 <= 0) goto L_0x0152
                android.animation.ValueAnimator r1 = r9.progressShowAnimator
                if (r1 == 0) goto L_0x014f
                r1.cancel()
                org.telegram.ui.Components.RadialProgress2 r1 = r9.radialProgress
                float r1 = r1.getProgress()
                r2 = 1
                int r1 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1))
                if (r1 >= 0) goto L_0x011a
                org.telegram.ui.Components.RadialProgress2 r1 = r9.radialProgress
                r1.setProgress(r5, r2)
            L_0x011a:
                float[] r1 = new float[r6]
                android.animation.ValueAnimator r6 = r9.progressShowAnimator
                java.lang.Object r6 = r6.getAnimatedValue()
                java.lang.Float r6 = (java.lang.Float) r6
                float r6 = r6.floatValue()
                r1[r3] = r6
                r1[r2] = r4
                android.animation.ValueAnimator r1 = android.animation.ValueAnimator.ofFloat(r1)
                r9.progressHideAnimator = r1
                org.telegram.ui.AvatarPreviewer$Layout$1 r2 = new org.telegram.ui.AvatarPreviewer$Layout$1
                r2.<init>()
                r1.addListener(r2)
                android.animation.ValueAnimator r1 = r9.progressHideAnimator
                org.telegram.ui.-$$Lambda$AvatarPreviewer$Layout$Vh3PHQnEnnTrxyomcT0MNdke4bY r2 = new org.telegram.ui.-$$Lambda$AvatarPreviewer$Layout$Vh3PHQnEnnTrxyomcT0MNdke4bY
                r2.<init>()
                r1.addUpdateListener(r2)
                android.animation.ValueAnimator r1 = r9.progressHideAnimator
                r1.setDuration(r7)
                android.animation.ValueAnimator r1 = r9.progressHideAnimator
                r1.start()
                goto L_0x0178
            L_0x014f:
                r9.showProgress = r3
                goto L_0x0178
            L_0x0152:
                android.animation.ValueAnimator r1 = r9.progressShowAnimator
                if (r1 != 0) goto L_0x0178
                float[] r1 = new float[r6]
                r1 = {0, NUM} // fill-array
                android.animation.ValueAnimator r1 = android.animation.ValueAnimator.ofFloat(r1)
                r9.progressShowAnimator = r1
                org.telegram.ui.-$$Lambda$AvatarPreviewer$Layout$thqDrKb7HJ7ZqZxQif1qK5QFbTg r2 = new org.telegram.ui.-$$Lambda$AvatarPreviewer$Layout$thqDrKb7HJ7ZqZxQif1qK5QFbTg
                r2.<init>()
                r1.addUpdateListener(r2)
                android.animation.ValueAnimator r1 = r9.progressShowAnimator
                r1.setStartDelay(r7)
                android.animation.ValueAnimator r1 = r9.progressShowAnimator
                r1.setDuration(r7)
                android.animation.ValueAnimator r1 = r9.progressShowAnimator
                r1.start()
            L_0x0178:
                android.animation.ValueAnimator r1 = r9.progressHideAnimator
                if (r1 == 0) goto L_0x0191
                org.telegram.ui.Components.RadialProgress2 r2 = r9.radialProgress
                java.lang.Object r1 = r1.getAnimatedValue()
                java.lang.Float r1 = (java.lang.Float) r1
                float r1 = r1.floatValue()
                r2.setOverrideAlpha(r1)
                org.telegram.ui.Components.RadialProgress2 r1 = r9.radialProgress
                r1.draw(r10)
                goto L_0x01a9
            L_0x0191:
                android.animation.ValueAnimator r1 = r9.progressShowAnimator
                if (r1 == 0) goto L_0x01a9
                org.telegram.ui.Components.RadialProgress2 r2 = r9.radialProgress
                java.lang.Object r1 = r1.getAnimatedValue()
                java.lang.Float r1 = (java.lang.Float) r1
                float r1 = r1.floatValue()
                r2.setOverrideAlpha(r1)
                org.telegram.ui.Components.RadialProgress2 r1 = r9.radialProgress
                r1.draw(r10)
            L_0x01a9:
                android.animation.ValueAnimator r1 = r9.moveAnimator
                r2 = 1132396544(0x437var_, float:255.0)
                if (r1 == 0) goto L_0x01bd
                android.graphics.drawable.Drawable r0 = r9.arrowDrawable
                float r1 = r1.getAnimatedFraction()
                float r5 = r5 - r1
                float r5 = r5 * r2
                int r1 = (int) r5
                r0.setAlpha(r1)
                goto L_0x01c5
            L_0x01bd:
                android.graphics.drawable.Drawable r1 = r9.arrowDrawable
                float r0 = r0 * r2
                int r0 = (int) r0
                r1.setAlpha(r0)
            L_0x01c5:
                android.graphics.drawable.Drawable r0 = r9.arrowDrawable
                r0.draw(r10)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.AvatarPreviewer.Layout.onDraw(android.graphics.Canvas):void");
        }

        public /* synthetic */ void lambda$onDraw$3$AvatarPreviewer$Layout(ValueAnimator valueAnimator) {
            invalidate();
        }

        public /* synthetic */ void lambda$onDraw$4$AvatarPreviewer$Layout(ValueAnimator valueAnimator) {
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
                access$1100.load(new Consumer(data) {
                    public final /* synthetic */ AvatarPreviewer.Data f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void accept(Object obj) {
                        AvatarPreviewer.Layout.this.lambda$setData$5$AvatarPreviewer$Layout(this.f$1, obj);
                    }
                });
            } else {
                Data data2 = data;
            }
            this.imageReceiver.setCurrentAccount(UserConfig.selectedAccount);
            this.imageReceiver.setImage(data.videoLocation, data.videoFilter, data.imageLocation, data.imageFilter, data.thumbImageLocation, data.thumbImageFilter, (Drawable) null, 0, (String) null, data.parentObject, 1);
            setShowing(true);
        }

        public /* synthetic */ void lambda$setData$5$AvatarPreviewer$Layout(Data data, Object obj) {
            if (this.recycled) {
                return;
            }
            if (obj instanceof TLRPC$UserFull) {
                setData(Data.of((TLRPC$UserFull) obj, data.menuItems));
            } else if (obj instanceof TLRPC$ChatFull) {
                setData(Data.of((TLRPC$Chat) data.infoLoadTask.argument, (TLRPC$ChatFull) obj, data.menuItems));
            }
        }

        private void setShowing(boolean z) {
            if (this.showing != z) {
                this.showing = z;
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
