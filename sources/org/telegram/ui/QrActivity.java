package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.collection.ArrayMap;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatThemeController;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SvgHelper;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.ResultCallback;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$WallPaper;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.EmojiThemes;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.ChatThemeBottomSheet;
import org.telegram.ui.Components.Easings;
import org.telegram.ui.Components.FlickerLoadingView;
import org.telegram.ui.Components.HideViewAfterAnimation;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.MotionBackgroundDrawable;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ThemeSmallPreviewView;

public class QrActivity extends BaseFragment {
    /* access modifiers changed from: private */
    public static List<EmojiThemes> cachedThemes;
    private static final ArrayMap<String, int[]> qrColorsMap;
    /* access modifiers changed from: private */
    public BackupImageView avatarImageView;
    /* access modifiers changed from: private */
    public View backgroundView;
    private long chatId;
    /* access modifiers changed from: private */
    public ImageView closeImageView;
    /* access modifiers changed from: private */
    public MotionBackgroundDrawable currMotionDrawable;
    /* access modifiers changed from: private */
    public EmojiThemes currentTheme;
    private final ArrayMap<String, Bitmap> emojiThemeDarkIcons;
    private Bitmap emojiThemeIcon;
    private final EmojiThemes homeTheme;
    /* access modifiers changed from: private */
    public boolean isCurrentThemeDark;
    /* access modifiers changed from: private */
    public RLottieImageView logoImageView;
    /* access modifiers changed from: private */
    public final Rect logoRect;
    /* access modifiers changed from: private */
    public ValueAnimator patternAlphaAnimator;
    private ValueAnimator patternIntensityAnimator;
    /* access modifiers changed from: private */
    public MotionBackgroundDrawable prevMotionDrawable;
    /* access modifiers changed from: private */
    public final int[] prevQrColors;
    private int prevSystemUiVisibility;
    /* access modifiers changed from: private */
    public QrView qrView;
    /* access modifiers changed from: private */
    public final ThemeResourcesProvider resourcesProvider = new ThemeResourcesProvider();
    /* access modifiers changed from: private */
    public int selectedPosition;
    /* access modifiers changed from: private */
    public MotionBackgroundDrawable tempMotionDrawable;
    /* access modifiers changed from: private */
    public FrameLayout themeLayout;
    private ThemeListViewController themesViewController;
    private long userId;

    interface OnItemSelectedListener {
        void onItemSelected(EmojiThemes emojiThemes, int i);
    }

    static {
        ArrayMap<String, int[]> arrayMap = new ArrayMap<>();
        qrColorsMap = arrayMap;
        arrayMap.put("🏠d", new int[]{-9324972, -13856649, -6636738, -9915042});
        arrayMap.put("🐥d", new int[]{-12344463, -7684788, -6442695, -8013488});
        arrayMap.put("⛄d", new int[]{-10051073, -10897938, -12469550, -7694337});
        arrayMap.put("💎d", new int[]{-11429643, -11814958, -5408261, -2128185});
        arrayMap.put("👨‍🏫d", new int[]{-6637227, -12015466, -13198627, -10631557});
        arrayMap.put("🌷d", new int[]{-1146812, -1991901, -1745517, -3443241});
        arrayMap.put("💜d", new int[]{-1156738, -1876046, -5412366, -28073});
        arrayMap.put("🎄d", new int[]{-1281978, -551386, -1870308, -742870});
        arrayMap.put("🎮d", new int[]{-15092782, -2333964, -1684365, -1269214});
        arrayMap.put("🏠n", new int[]{-15368239, -11899662, -15173939, -13850930});
        arrayMap.put("🐥n", new int[]{-11033320, -14780848, -9594089, -12604587});
        arrayMap.put("⛄n", new int[]{-13930790, -13665098, -14833975, -9732865});
        arrayMap.put("💎n", new int[]{-5089608, -9481473, -14378302, -13337899});
        arrayMap.put("👨‍🏫n", new int[]{-14447768, -9199261, -15356801, -15823723});
        arrayMap.put("🌷n", new int[]{-2534316, -2984177, -3258783, -5480504});
        arrayMap.put("💜n", new int[]{-3123030, -2067394, -2599576, -6067757});
        arrayMap.put("🎄n", new int[]{-2725857, -3242459, -3248848, -3569123});
        arrayMap.put("🎮n", new int[]{-3718333, -1278154, -16338695, -6076417});
    }

    public QrActivity(Bundle bundle) {
        super(bundle);
        EmojiThemes createHomeQrTheme = EmojiThemes.createHomeQrTheme();
        this.homeTheme = createHomeQrTheme;
        this.logoRect = new Rect();
        this.emojiThemeDarkIcons = new ArrayMap<>();
        this.prevQrColors = new int[4];
        this.currMotionDrawable = new MotionBackgroundDrawable();
        this.currentTheme = createHomeQrTheme;
        this.selectedPosition = -1;
    }

    public boolean onFragmentCreate() {
        this.userId = this.arguments.getLong("user_id");
        this.chatId = this.arguments.getLong("chat_id");
        return super.onFragmentCreate();
    }

    public View createView(Context context) {
        AvatarDrawable avatarDrawable;
        ImageLocation imageLocation;
        ImageLocation imageLocation2;
        boolean z;
        String str;
        String str2;
        String str3;
        String str4;
        TLRPC$Chat chat;
        ImageLocation imageLocation3;
        AvatarDrawable avatarDrawable2;
        ImageLocation imageLocation4;
        Context context2 = context;
        this.homeTheme.loadPreviewColors(this.currentAccount);
        this.isCurrentThemeDark = Theme.getActiveTheme().isDark();
        this.actionBar.setAddToContainer(false);
        this.actionBar.setBackground((Drawable) null);
        this.actionBar.setItemsColor(-1, false);
        AnonymousClass1 r2 = new FrameLayout(context2) {
            private boolean prevIsPortrait;

            public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                super.dispatchTouchEvent(motionEvent);
                return true;
            }

            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                int size = View.MeasureSpec.getSize(i);
                int size2 = View.MeasureSpec.getSize(i2);
                boolean z = size < size2;
                QrActivity.this.avatarImageView.setVisibility(z ? 0 : 8);
                super.onMeasure(i, i2);
                if (z) {
                    QrActivity.this.themeLayout.measure(View.MeasureSpec.makeMeasureSpec(size, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(size2, Integer.MIN_VALUE));
                    QrActivity.this.qrView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(260.0f), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(330.0f), NUM));
                } else {
                    QrActivity.this.themeLayout.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(256.0f), NUM), i2);
                    QrActivity.this.qrView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(260.0f), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(310.0f), NUM));
                }
                if (this.prevIsPortrait != z) {
                    QrActivity.this.qrView.onSizeChanged(QrActivity.this.qrView.getMeasuredWidth(), QrActivity.this.qrView.getMeasuredHeight(), 0, 0);
                }
                this.prevIsPortrait = z;
            }

            /* access modifiers changed from: protected */
            public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                int i5;
                int i6;
                int i7 = 0;
                boolean z2 = getWidth() < getHeight();
                QrActivity.this.backgroundView.layout(0, 0, getWidth(), getHeight());
                if (QrActivity.this.themeLayout.getVisibility() == 0) {
                    i7 = QrActivity.this.themeLayout.getMeasuredHeight();
                }
                if (z2) {
                    i5 = (getWidth() - QrActivity.this.qrView.getMeasuredWidth()) / 2;
                } else {
                    i5 = ((getWidth() - QrActivity.this.themeLayout.getMeasuredWidth()) - QrActivity.this.qrView.getMeasuredWidth()) / 2;
                }
                if (z2) {
                    i6 = ((((getHeight() - i7) - QrActivity.this.qrView.getMeasuredHeight()) - AndroidUtilities.dp(48.0f)) / 2) + AndroidUtilities.dp(52.0f);
                } else {
                    i6 = (getHeight() - QrActivity.this.qrView.getMeasuredHeight()) / 2;
                }
                QrActivity.this.qrView.layout(i5, i6, QrActivity.this.qrView.getMeasuredWidth() + i5, QrActivity.this.qrView.getMeasuredHeight() + i6);
                if (z2) {
                    int width = (getWidth() - QrActivity.this.avatarImageView.getMeasuredWidth()) / 2;
                    int dp = i6 - AndroidUtilities.dp(48.0f);
                    QrActivity.this.avatarImageView.layout(width, dp, QrActivity.this.avatarImageView.getMeasuredWidth() + width, QrActivity.this.avatarImageView.getMeasuredHeight() + dp);
                }
                if (QrActivity.this.themeLayout.getVisibility() == 0) {
                    if (z2) {
                        int width2 = (getWidth() - QrActivity.this.themeLayout.getMeasuredWidth()) / 2;
                        QrActivity.this.themeLayout.layout(width2, i4 - i7, QrActivity.this.themeLayout.getMeasuredWidth() + width2, i4);
                    } else {
                        int height = (getHeight() - QrActivity.this.themeLayout.getMeasuredHeight()) / 2;
                        QrActivity.this.themeLayout.layout(i3 - QrActivity.this.themeLayout.getMeasuredWidth(), height, i3, QrActivity.this.themeLayout.getMeasuredHeight() + height);
                    }
                }
                QrActivity.this.logoImageView.layout(QrActivity.this.logoRect.left + i5, QrActivity.this.logoRect.top + i6, i5 + QrActivity.this.logoRect.right, i6 + QrActivity.this.logoRect.bottom);
                int dp2 = AndroidUtilities.dp(z2 ? 14.0f : 17.0f);
                int dp3 = AndroidUtilities.statusBarHeight + AndroidUtilities.dp(z2 ? 10.0f : 5.0f);
                QrActivity.this.closeImageView.layout(dp2, dp3, QrActivity.this.closeImageView.getMeasuredWidth() + dp2, QrActivity.this.closeImageView.getMeasuredHeight() + dp3);
            }
        };
        AnonymousClass2 r6 = new View(context2) {
            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                if (QrActivity.this.prevMotionDrawable != null) {
                    QrActivity.this.prevMotionDrawable.setBounds(0, 0, getWidth(), getHeight());
                }
                QrActivity.this.currMotionDrawable.setBounds(0, 0, getWidth(), getHeight());
                if (QrActivity.this.prevMotionDrawable != null) {
                    QrActivity.this.prevMotionDrawable.drawBackground(canvas);
                }
                QrActivity.this.currMotionDrawable.drawBackground(canvas);
                if (QrActivity.this.prevMotionDrawable != null) {
                    QrActivity.this.prevMotionDrawable.drawPattern(canvas);
                }
                QrActivity.this.currMotionDrawable.drawPattern(canvas);
                super.onDraw(canvas);
            }
        };
        this.backgroundView = r6;
        r2.addView(r6);
        if (this.userId != 0) {
            TLRPC$User user = getMessagesController().getUser(Long.valueOf(this.userId));
            if (user != null) {
                str2 = user.username;
                if (str2 == null) {
                    z = true;
                    str = UserObject.getUserName(user);
                    str2 = user.phone;
                } else {
                    str = null;
                    z = false;
                }
                avatarDrawable2 = new AvatarDrawable(user);
                imageLocation3 = ImageLocation.getForUser(user, 1);
                imageLocation4 = ImageLocation.getForUser(user, 0);
            } else {
                imageLocation4 = null;
                str2 = null;
                str = null;
                avatarDrawable2 = null;
                imageLocation3 = null;
                z = false;
            }
            imageLocation2 = imageLocation4;
            avatarDrawable = avatarDrawable2;
            imageLocation = imageLocation3;
        } else {
            if (this.chatId == 0 || (chat = getMessagesController().getChat(Long.valueOf(this.chatId))) == null) {
                str3 = null;
                str4 = null;
                imageLocation2 = null;
                imageLocation = null;
                avatarDrawable = null;
            } else {
                str3 = chat.username;
                AvatarDrawable avatarDrawable3 = new AvatarDrawable(chat);
                ImageLocation forChat = ImageLocation.getForChat(chat, 1);
                str4 = null;
                imageLocation2 = ImageLocation.getForChat(chat, 0);
                avatarDrawable = avatarDrawable3;
                imageLocation = forChat;
            }
            z = false;
        }
        String str5 = "https://" + MessagesController.getInstance(this.currentAccount).linkPrefix + "/" + str2;
        QrView qrView2 = new QrView(context2);
        this.qrView = qrView2;
        qrView2.setColors(-9324972, -13856649, -6636738, -9915042);
        QrView qrView3 = this.qrView;
        if (str != null) {
            str2 = str;
        }
        qrView3.setData(str5, str2, z);
        this.qrView.setCenterChangedListener(new QrActivity$$ExternalSyntheticLambda11(this));
        r2.addView(this.qrView);
        RLottieImageView rLottieImageView = new RLottieImageView(context2);
        this.logoImageView = rLottieImageView;
        rLottieImageView.setAutoRepeat(true);
        this.logoImageView.setAnimation(NUM, 60, 60);
        this.logoImageView.playAnimation();
        r2.addView(this.logoImageView);
        BackupImageView backupImageView = new BackupImageView(context2);
        this.avatarImageView = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(42.0f));
        this.avatarImageView.setSize(AndroidUtilities.dp(84.0f), AndroidUtilities.dp(84.0f));
        r2.addView(this.avatarImageView, LayoutHelper.createFrame(84, 84, 51));
        this.avatarImageView.setImage(imageLocation2, "84_84", imageLocation, "50_50", avatarDrawable, (Bitmap) null, (String) null, 0, (Object) null);
        ImageView imageView = new ImageView(context2);
        this.closeImageView = imageView;
        imageView.setBackground(Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(34.0f), NUM, NUM));
        this.closeImageView.setImageResource(NUM);
        this.closeImageView.setScaleType(ImageView.ScaleType.CENTER);
        this.closeImageView.setOnClickListener(new QrActivity$$ExternalSyntheticLambda2(this));
        r2.addView(this.closeImageView, LayoutHelper.createFrame(34, 34.0f));
        this.emojiThemeIcon = Bitmap.createBitmap(AndroidUtilities.dp(32.0f), AndroidUtilities.dp(32.0f), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(this.emojiThemeIcon);
        RectF rectF = AndroidUtilities.rectTmp;
        rectF.set(0.0f, 0.0f, (float) this.emojiThemeIcon.getWidth(), (float) this.emojiThemeIcon.getHeight());
        Paint paint = new Paint(1);
        paint.setColor(-1);
        canvas.drawRoundRect(rectF, (float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        Bitmap decodeResource = BitmapFactory.decodeResource(ApplicationLoader.applicationContext.getResources(), NUM);
        canvas.drawBitmap(decodeResource, ((float) (this.emojiThemeIcon.getWidth() - decodeResource.getWidth())) * 0.5f, ((float) (this.emojiThemeIcon.getHeight() - decodeResource.getHeight())) * 0.5f, paint);
        canvas.setBitmap((Bitmap) null);
        AnonymousClass3 r1 = new ThemeListViewController(this, getParentActivity().getWindow()) {
            /* access modifiers changed from: protected */
            public void setDarkTheme(boolean z) {
                super.setDarkTheme(z);
                boolean unused = QrActivity.this.isCurrentThemeDark = z;
                QrActivity qrActivity = QrActivity.this;
                qrActivity.onItemSelected(qrActivity.currentTheme, QrActivity.this.selectedPosition, false);
            }
        };
        this.themesViewController = r1;
        this.themeLayout = r1.rootLayout;
        r1.onCreate();
        this.themesViewController.setItemSelectedListener(new QrActivity$$ExternalSyntheticLambda10(this));
        this.themesViewController.titleView.setText(LocaleController.getString("QrCode", NUM));
        this.themesViewController.progressView.setViewType(17);
        this.themesViewController.shareButton.setOnClickListener(new QrActivity$$ExternalSyntheticLambda3(this));
        r2.addView(this.themeLayout, LayoutHelper.createFrame(-1, -2, 80));
        this.currMotionDrawable.setIndeterminateAnimation(true);
        this.fragmentView = r2;
        onItemSelected(this.currentTheme, 0, false);
        List<EmojiThemes> list = cachedThemes;
        if (list == null || list.isEmpty()) {
            ChatThemeController.requestAllChatThemes(new ResultCallback<List<EmojiThemes>>() {
                public void onComplete(List<EmojiThemes> list) {
                    QrActivity.this.onDataLoaded(list);
                    List unused = QrActivity.cachedThemes = list;
                }

                public void onError(TLRPC$TL_error tLRPC$TL_error) {
                    Toast.makeText(QrActivity.this.getParentActivity(), tLRPC$TL_error.text, 0).show();
                }
            }, true);
        } else {
            onDataLoaded(cachedThemes);
        }
        this.prevSystemUiVisibility = getParentActivity().getWindow().getDecorView().getSystemUiVisibility();
        applyScreenSettings();
        return this.fragmentView;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$0(int i, int i2, int i3, int i4) {
        this.logoRect.set(i, i2, i3, i4);
        this.qrView.requestLayout();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$1(View view) {
        finishFragment();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$2(EmojiThemes emojiThemes, int i) {
        onItemSelected(emojiThemes, i, true);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$3(View view) {
        this.themesViewController.shareButton.setClickable(false);
        performShare();
    }

    @SuppressLint({"SourceLockedOrientationActivity"})
    public void onResume() {
        super.onResume();
        applyScreenSettings();
    }

    public void onPause() {
        restoreScreenSettings();
        super.onPause();
    }

    public void onFragmentDestroy() {
        this.themesViewController.onDestroy();
        this.themesViewController = null;
        this.emojiThemeIcon.recycle();
        this.emojiThemeIcon = null;
        for (int i = 0; i < this.emojiThemeDarkIcons.size(); i++) {
            Bitmap valueAt = this.emojiThemeDarkIcons.valueAt(i);
            if (valueAt != null) {
                valueAt.recycle();
            }
        }
        this.emojiThemeDarkIcons.clear();
        restoreScreenSettings();
        super.onFragmentDestroy();
    }

    private void applyScreenSettings() {
        if (getParentActivity() != null) {
            getParentActivity().getWindow().getDecorView().setSystemUiVisibility(this.prevSystemUiVisibility | 1024 | 4);
        }
    }

    private void restoreScreenSettings() {
        if (getParentActivity() != null) {
            getParentActivity().getWindow().getDecorView().setSystemUiVisibility(this.prevSystemUiVisibility);
        }
    }

    public int getNavigationBarColor() {
        return getThemedColor("windowBackgroundGray");
    }

    public Theme.ResourcesProvider getResourceProvider() {
        return this.resourcesProvider;
    }

    /* access modifiers changed from: private */
    public void onDataLoaded(List<EmojiThemes> list) {
        if (list != null && !list.isEmpty() && this.themesViewController != null) {
            int i = 0;
            list.set(0, this.homeTheme);
            ArrayList arrayList = new ArrayList(list.size());
            for (int i2 = 0; i2 < list.size(); i2++) {
                EmojiThemes emojiThemes = list.get(i2);
                emojiThemes.loadPreviewColors(this.currentAccount);
                ChatThemeBottomSheet.ChatThemeItem chatThemeItem = new ChatThemeBottomSheet.ChatThemeItem(emojiThemes);
                boolean z = this.isCurrentThemeDark;
                chatThemeItem.themeIndex = z ? 1 : 0;
                chatThemeItem.icon = getEmojiThemeIcon(emojiThemes, z);
                arrayList.add(chatThemeItem);
            }
            this.themesViewController.adapter.setItems(arrayList);
            while (true) {
                if (i == arrayList.size()) {
                    i = -1;
                    break;
                } else if (((ChatThemeBottomSheet.ChatThemeItem) arrayList.get(i)).chatTheme.getEmoticon().equals(this.currentTheme.getEmoticon())) {
                    this.themesViewController.selectedItem = (ChatThemeBottomSheet.ChatThemeItem) arrayList.get(i);
                    break;
                } else {
                    i++;
                }
            }
            if (i != -1) {
                this.themesViewController.setSelectedPosition(i);
            }
            this.themesViewController.onDataLoaded();
        }
    }

    /* access modifiers changed from: private */
    public Bitmap getEmojiThemeIcon(EmojiThemes emojiThemes, boolean z) {
        if (!z) {
            return this.emojiThemeIcon;
        }
        Bitmap bitmap = this.emojiThemeDarkIcons.get(emojiThemes.emoji);
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(this.emojiThemeIcon.getWidth(), this.emojiThemeIcon.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            ArrayMap<String, int[]> arrayMap = qrColorsMap;
            int[] iArr = arrayMap.get(emojiThemes.emoji + "n");
            if (iArr != null) {
                if (this.tempMotionDrawable == null) {
                    this.tempMotionDrawable = new MotionBackgroundDrawable(0, 0, 0, 0, true);
                }
                this.tempMotionDrawable.setColors(iArr[0], iArr[1], iArr[2], iArr[3]);
                this.tempMotionDrawable.setBounds(AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f), canvas.getWidth() - AndroidUtilities.dp(6.0f), canvas.getHeight() - AndroidUtilities.dp(6.0f));
                this.tempMotionDrawable.draw(canvas);
            }
            canvas.drawBitmap(this.emojiThemeIcon, 0.0f, 0.0f, (Paint) null);
            canvas.setBitmap((Bitmap) null);
            this.emojiThemeDarkIcons.put(emojiThemes.emoji, bitmap);
        }
        return bitmap;
    }

    private void onPatternLoaded(Bitmap bitmap, int i, boolean z) {
        if (bitmap != null) {
            this.currMotionDrawable.setPatternBitmap(i, bitmap, true);
            ValueAnimator valueAnimator = this.patternIntensityAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            if (z) {
                ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                this.patternIntensityAnimator = ofFloat;
                ofFloat.addUpdateListener(new QrActivity$$ExternalSyntheticLambda0(this));
                this.patternIntensityAnimator.setDuration(250);
                this.patternIntensityAnimator.start();
                return;
            }
            this.currMotionDrawable.setPatternAlpha(1.0f);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onPatternLoaded$4(ValueAnimator valueAnimator) {
        this.currMotionDrawable.setPatternAlpha(((Float) valueAnimator.getAnimatedValue()).floatValue());
    }

    /* access modifiers changed from: private */
    public void onItemSelected(EmojiThemes emojiThemes, int i, boolean z) {
        float f;
        this.selectedPosition = i;
        EmojiThemes emojiThemes2 = this.currentTheme;
        boolean z2 = this.isCurrentThemeDark;
        this.currentTheme = emojiThemes;
        EmojiThemes.ThemeItem themeItem = emojiThemes.getThemeItem(z2 ? 1 : 0);
        ValueAnimator valueAnimator = this.patternAlphaAnimator;
        if (valueAnimator != null) {
            f = Math.max(0.5f, 1.0f - ((Float) valueAnimator.getAnimatedValue()).floatValue()) * 1.0f;
            this.patternAlphaAnimator.cancel();
        } else {
            f = 1.0f;
        }
        MotionBackgroundDrawable motionBackgroundDrawable = this.currMotionDrawable;
        this.prevMotionDrawable = motionBackgroundDrawable;
        motionBackgroundDrawable.setIndeterminateAnimation(false);
        this.prevMotionDrawable.setAlpha(255);
        MotionBackgroundDrawable motionBackgroundDrawable2 = new MotionBackgroundDrawable();
        this.currMotionDrawable = motionBackgroundDrawable2;
        motionBackgroundDrawable2.setCallback(this.backgroundView);
        this.currMotionDrawable.setColors(themeItem.patternBgColor, themeItem.patternBgGradientColor1, themeItem.patternBgGradientColor2, themeItem.patternBgGradientColor3);
        this.currMotionDrawable.setParentView(this.backgroundView);
        this.currMotionDrawable.setPatternAlpha(1.0f);
        this.currMotionDrawable.setIndeterminateAnimation(true);
        MotionBackgroundDrawable motionBackgroundDrawable3 = this.prevMotionDrawable;
        if (motionBackgroundDrawable3 != null) {
            this.currMotionDrawable.posAnimationProgress = motionBackgroundDrawable3.posAnimationProgress;
        }
        this.qrView.setPosAnimationProgress(this.currMotionDrawable.posAnimationProgress);
        TLRPC$WallPaper wallpaper = this.currentTheme.getWallpaper(z2);
        if (wallpaper != null) {
            this.currMotionDrawable.setPatternBitmap(wallpaper.settings.intensity);
            this.currentTheme.loadWallpaper(z2, new QrActivity$$ExternalSyntheticLambda8(this, z2, SystemClock.elapsedRealtime()));
        } else {
            ChatThemeController.chatThemeQueue.postRunnable(new QrActivity$$ExternalSyntheticLambda4(this));
        }
        MotionBackgroundDrawable motionBackgroundDrawable4 = this.currMotionDrawable;
        motionBackgroundDrawable4.setPatternColorFilter(motionBackgroundDrawable4.getPatternColor());
        ArrayMap<String, int[]> arrayMap = qrColorsMap;
        StringBuilder sb = new StringBuilder();
        sb.append(emojiThemes.emoji);
        sb.append(z2 ? "n" : "d");
        final int[] iArr = arrayMap.get(sb.toString());
        if (z) {
            this.currMotionDrawable.setAlpha(255);
            this.currMotionDrawable.setBackgroundAlpha(0.0f);
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            this.patternAlphaAnimator = ofFloat;
            ofFloat.addUpdateListener(new QrActivity$$ExternalSyntheticLambda1(this, iArr));
            this.patternAlphaAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    super.onAnimationEnd(animator);
                    int[] iArr = iArr;
                    if (iArr != null) {
                        System.arraycopy(iArr, 0, QrActivity.this.prevQrColors, 0, 4);
                    }
                    MotionBackgroundDrawable unused = QrActivity.this.prevMotionDrawable = null;
                    ValueAnimator unused2 = QrActivity.this.patternAlphaAnimator = null;
                    QrActivity.this.currMotionDrawable.setBackgroundAlpha(1.0f);
                    QrActivity.this.currMotionDrawable.setPatternAlpha(1.0f);
                }

                public void onAnimationCancel(Animator animator) {
                    super.onAnimationCancel(animator);
                    float floatValue = ((Float) ((ValueAnimator) animator).getAnimatedValue()).floatValue();
                    if (iArr != null) {
                        System.arraycopy(new int[]{ColorUtils.blendARGB(QrActivity.this.prevQrColors[0], iArr[0], floatValue), ColorUtils.blendARGB(QrActivity.this.prevQrColors[1], iArr[1], floatValue), ColorUtils.blendARGB(QrActivity.this.prevQrColors[2], iArr[2], floatValue), ColorUtils.blendARGB(QrActivity.this.prevQrColors[3], iArr[3], floatValue)}, 0, QrActivity.this.prevQrColors, 0, 4);
                    }
                }
            });
            this.patternAlphaAnimator.setDuration((long) ((int) (f * 250.0f)));
            this.patternAlphaAnimator.start();
        } else {
            if (iArr != null) {
                this.qrView.setColors(iArr[0], iArr[1], iArr[2], iArr[3]);
                System.arraycopy(iArr, 0, this.prevQrColors, 0, 4);
            }
            this.prevMotionDrawable = null;
            this.backgroundView.invalidate();
        }
        ActionBarLayout.ThemeAnimationSettings themeAnimationSettings = new ActionBarLayout.ThemeAnimationSettings((Theme.ThemeInfo) null, (this.isCurrentThemeDark ? Theme.getCurrentNightTheme() : Theme.getCurrentTheme()).currentAccentId, this.isCurrentThemeDark, !z);
        themeAnimationSettings.applyTheme = false;
        themeAnimationSettings.onlyTopFragment = true;
        themeAnimationSettings.resourcesProvider = getResourceProvider();
        themeAnimationSettings.duration = (long) ((int) (f * 250.0f));
        if (z) {
            this.resourcesProvider.initColors(emojiThemes2, this.isCurrentThemeDark);
        } else {
            this.resourcesProvider.initColors(this.currentTheme, this.isCurrentThemeDark);
        }
        themeAnimationSettings.afterStartDescriptionsAddedRunnable = new QrActivity$$ExternalSyntheticLambda6(this);
        this.parentLayout.animateThemedValues(themeAnimationSettings);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onItemSelected$5(boolean z, long j, Pair pair) {
        if (pair != null && this.currentTheme.getTlTheme(z ? 1 : 0) != null) {
            long longValue = ((Long) pair.first).longValue();
            Bitmap bitmap = (Bitmap) pair.second;
            if (longValue == this.currentTheme.getTlTheme(z).id && bitmap != null) {
                onPatternLoaded(bitmap, this.currMotionDrawable.getIntensity(), SystemClock.elapsedRealtime() - j > 150);
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onItemSelected$7() {
        AndroidUtilities.runOnUIThread(new QrActivity$$ExternalSyntheticLambda7(this, SvgHelper.getBitmap(NUM, this.backgroundView.getWidth(), this.backgroundView.getHeight(), -16777216)));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onItemSelected$6(Bitmap bitmap) {
        onPatternLoaded(bitmap, 34, true);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onItemSelected$8(int[] iArr, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        MotionBackgroundDrawable motionBackgroundDrawable = this.prevMotionDrawable;
        if (motionBackgroundDrawable != null) {
            motionBackgroundDrawable.setBackgroundAlpha(1.0f);
            this.prevMotionDrawable.setPatternAlpha(1.0f - floatValue);
        }
        this.currMotionDrawable.setBackgroundAlpha(floatValue);
        this.currMotionDrawable.setPatternAlpha(floatValue);
        if (iArr != null) {
            this.qrView.setColors(ColorUtils.blendARGB(this.prevQrColors[0], iArr[0], floatValue), ColorUtils.blendARGB(this.prevQrColors[1], iArr[1], floatValue), ColorUtils.blendARGB(this.prevQrColors[2], iArr[2], floatValue), ColorUtils.blendARGB(this.prevQrColors[3], iArr[3], floatValue));
        }
        this.backgroundView.invalidate();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onItemSelected$9() {
        this.resourcesProvider.initColors(this.currentTheme, this.isCurrentThemeDark);
    }

    private void performShare() {
        Point point = AndroidUtilities.displaySize;
        int min = Math.min(point.x, point.y);
        Point point2 = AndroidUtilities.displaySize;
        int max = Math.max(point2.x, point2.y);
        float f = (float) min;
        if ((((float) max) * 1.0f) / f > 1.92f) {
            max = (int) (f * 1.92f);
        }
        Bitmap createBitmap = Bitmap.createBitmap(min, max, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        this.themeLayout.setVisibility(8);
        this.closeImageView.setVisibility(8);
        this.logoImageView.stopAnimation();
        RLottieDrawable animatedDrawable = this.logoImageView.getAnimatedDrawable();
        int currentFrame = animatedDrawable.getCurrentFrame();
        animatedDrawable.setCurrentFrame(33, false);
        this.fragmentView.measure(View.MeasureSpec.makeMeasureSpec(min, NUM), View.MeasureSpec.makeMeasureSpec(max, NUM));
        this.fragmentView.layout(0, 0, min, max);
        this.fragmentView.draw(canvas);
        canvas.setBitmap((Bitmap) null);
        this.themeLayout.setVisibility(0);
        this.closeImageView.setVisibility(0);
        animatedDrawable.setCurrentFrame(currentFrame, false);
        this.logoImageView.playAnimation();
        ViewGroup viewGroup = (ViewGroup) this.fragmentView.getParent();
        this.fragmentView.layout(0, 0, viewGroup.getWidth(), viewGroup.getHeight());
        Uri bitmapShareUri = AndroidUtilities.getBitmapShareUri(createBitmap, "qr_tmp.jpg", Bitmap.CompressFormat.JPEG);
        if (bitmapShareUri != null) {
            try {
                getParentActivity().startActivityForResult(Intent.createChooser(new Intent("android.intent.action.SEND").setType("image/*").putExtra("android.intent.extra.STREAM", bitmapShareUri), LocaleController.getString("InviteByQRCode", NUM)), 500);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }
        }
        AndroidUtilities.runOnUIThread(new QrActivity$$ExternalSyntheticLambda5(this), 500);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$performShare$10() {
        ThemeListViewController themeListViewController = this.themesViewController;
        if (themeListViewController != null) {
            themeListViewController.shareButton.setClickable(true);
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = super.getThemeDescriptions();
        themeDescriptions.addAll(this.themesViewController.getThemeDescriptions());
        themeDescriptions.add(new ThemeDescription(this.themesViewController.shareButton, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, new QrActivity$$ExternalSyntheticLambda9(this), "featuredStickers_addButton"));
        themeDescriptions.add(new ThemeDescription(this.themesViewController.shareButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "featuredStickers_addButtonPressed"));
        Iterator<ThemeDescription> it = themeDescriptions.iterator();
        while (it.hasNext()) {
            it.next().resourcesProvider = getResourceProvider();
        }
        return themeDescriptions;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$getThemeDescriptions$11() {
        setNavigationBarColor(getThemedColor("windowBackgroundGray"));
    }

    private class ThemeResourcesProvider implements Theme.ResourcesProvider {
        private HashMap<String, Integer> colors;

        public /* synthetic */ void applyServiceShaderMatrix(int i, int i2, float f, float f2) {
            Theme.ResourcesProvider.CC.$default$applyServiceShaderMatrix(this, i, i2, f, f2);
        }

        public /* synthetic */ int getColorOrDefault(String str) {
            return Theme.ResourcesProvider.CC.$default$getColorOrDefault(this, str);
        }

        public /* synthetic */ Integer getCurrentColor(String str) {
            return Theme.ResourcesProvider.CC.$default$getCurrentColor(this, str);
        }

        public /* synthetic */ Drawable getDrawable(String str) {
            return Theme.ResourcesProvider.CC.$default$getDrawable(this, str);
        }

        public /* synthetic */ Paint getPaint(String str) {
            return Theme.ResourcesProvider.CC.$default$getPaint(this, str);
        }

        public /* synthetic */ boolean hasGradientService() {
            return Theme.ResourcesProvider.CC.$default$hasGradientService(this);
        }

        public /* synthetic */ void setAnimatedColor(String str, int i) {
            Theme.ResourcesProvider.CC.$default$setAnimatedColor(this, str, i);
        }

        private ThemeResourcesProvider() {
        }

        /* access modifiers changed from: package-private */
        public void initColors(EmojiThemes emojiThemes, boolean z) {
            this.colors = emojiThemes.createColors(QrActivity.this.currentAccount, z ? 1 : 0);
        }

        public Integer getColor(String str) {
            HashMap<String, Integer> hashMap = this.colors;
            if (hashMap != null) {
                return hashMap.get(str);
            }
            return null;
        }
    }

    private static class QrView extends View {
        private static final float RADIUS = ((float) AndroidUtilities.dp(20.0f));
        private static final float SHADOW_SIZE = ((float) AndroidUtilities.dp(2.0f));
        private Bitmap backgroundBitmap;
        private final Paint bitmapGradientPaint;
        private QrCenterChangedListener centerChangedListener;
        private Bitmap contentBitmap;
        private final MotionBackgroundDrawable gradientDrawable;
        private final BitmapShader gradientShader;
        private boolean isPhone;
        private String link;
        private String username;

        public interface QrCenterChangedListener {
            void onCenterChanged(int i, int i2, int i3, int i4);
        }

        QrView(Context context) {
            super(context);
            MotionBackgroundDrawable motionBackgroundDrawable = new MotionBackgroundDrawable();
            this.gradientDrawable = motionBackgroundDrawable;
            Paint paint = new Paint(1);
            this.bitmapGradientPaint = paint;
            motionBackgroundDrawable.setIndeterminateAnimation(true);
            motionBackgroundDrawable.setParentView(this);
            Bitmap bitmap = motionBackgroundDrawable.getBitmap();
            Shader.TileMode tileMode = Shader.TileMode.MIRROR;
            BitmapShader bitmapShader = new BitmapShader(bitmap, tileMode, tileMode);
            this.gradientShader = bitmapShader;
            paint.setShader(bitmapShader);
        }

        /* access modifiers changed from: protected */
        public void onSizeChanged(int i, int i2, int i3, int i4) {
            super.onSizeChanged(i, i2, i3, i4);
            if (i != i3 || i2 != i4) {
                Bitmap bitmap = this.backgroundBitmap;
                if (bitmap != null) {
                    bitmap.recycle();
                    this.backgroundBitmap = null;
                }
                Paint paint = new Paint(1);
                paint.setColor(-1);
                float f = SHADOW_SIZE;
                paint.setShadowLayer((float) AndroidUtilities.dp(4.0f), 0.0f, f, NUM);
                this.backgroundBitmap = Bitmap.createBitmap(i, i2, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(this.backgroundBitmap);
                RectF rectF = new RectF(f, f, ((float) i) - f, ((float) getHeight()) - f);
                float f2 = RADIUS;
                canvas.drawRoundRect(rectF, f2, f2, paint);
                prepareContent(i, i2);
                float max = Math.max((((float) getWidth()) * 1.0f) / ((float) this.gradientDrawable.getBitmap().getWidth()), (((float) getHeight()) * 1.0f) / ((float) this.gradientDrawable.getBitmap().getHeight()));
                Matrix matrix = new Matrix();
                matrix.setScale(max, max);
                this.gradientShader.setLocalMatrix(matrix);
            }
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            Bitmap bitmap = this.backgroundBitmap;
            if (bitmap != null) {
                canvas.drawBitmap(bitmap, 0.0f, 0.0f, (Paint) null);
            }
            Bitmap bitmap2 = this.contentBitmap;
            if (bitmap2 != null) {
                canvas.drawBitmap(bitmap2, 0.0f, 0.0f, this.bitmapGradientPaint);
                this.gradientDrawable.updateAnimation(true);
            }
        }

        /* access modifiers changed from: package-private */
        public void setCenterChangedListener(QrCenterChangedListener qrCenterChangedListener) {
            this.centerChangedListener = qrCenterChangedListener;
        }

        /* access modifiers changed from: package-private */
        public void setData(String str, String str2, boolean z) {
            this.username = str2;
            this.isPhone = z;
            this.link = str;
            prepareContent(getWidth(), getHeight());
            invalidate();
        }

        /* access modifiers changed from: package-private */
        public void setColors(int i, int i2, int i3, int i4) {
            this.gradientDrawable.setColors(i, i2, i3, i4);
            invalidate();
        }

        /* access modifiers changed from: package-private */
        public void setPosAnimationProgress(float f) {
            this.gradientDrawable.posAnimationProgress = f;
        }

        /* JADX WARNING: Code restructure failed: missing block: B:32:0x010c, code lost:
            if (r9 <= ((float) r5)) goto L_0x0110;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:33:0x010e, code lost:
            r6 = 2;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:34:0x0110, code lost:
            r6 = 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:35:0x0111, code lost:
            if (r6 <= 1) goto L_0x0126;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:36:0x0113, code lost:
            r10 = (((int) (((float) r7.getBounds().width()) + r9)) / 2) + org.telegram.messenger.AndroidUtilities.dp(2.0f);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:37:0x0126, code lost:
            r10 = r5;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:38:0x0127, code lost:
            if (r10 <= r5) goto L_0x013e;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:39:0x0129, code lost:
            r7 = (((int) (r9 + ((float) r7.getBounds().width()))) / 3) + org.telegram.messenger.AndroidUtilities.dp(4.0f);
            r20 = 3;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:40:0x013e, code lost:
            r20 = r6;
            r7 = r10;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:41:0x0141, code lost:
            r3 = 3;
            r23 = 0;
            r5 = org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(r8, r15, r7, android.text.Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false, (android.text.TextUtils.TruncateAt) null, java.lang.Math.min(org.telegram.messenger.AndroidUtilities.dp(10.0f) + r7, r0.contentBitmap.getWidth()), r20);
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private void prepareContent(int r34, int r35) {
            /*
                r33 = this;
                r0 = r33
                r1 = r34
                r2 = r35
                java.lang.String r3 = r0.username
                boolean r3 = android.text.TextUtils.isEmpty(r3)
                if (r3 != 0) goto L_0x02a7
                java.lang.String r3 = r0.link
                boolean r3 = android.text.TextUtils.isEmpty(r3)
                if (r3 != 0) goto L_0x02a7
                if (r1 == 0) goto L_0x02a7
                if (r2 != 0) goto L_0x001c
                goto L_0x02a7
            L_0x001c:
                android.graphics.Bitmap r3 = r0.contentBitmap
                if (r3 == 0) goto L_0x0023
                r3.recycle()
            L_0x0023:
                android.graphics.Bitmap$Config r3 = android.graphics.Bitmap.Config.ARGB_8888
                android.graphics.Bitmap r3 = android.graphics.Bitmap.createBitmap(r1, r2, r3)
                r0.contentBitmap = r3
                r3 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
                r4 = 16777215(0xffffff, float:2.3509886E-38)
                android.text.TextPaint r15 = new android.text.TextPaint
                r5 = 65
                r15.<init>(r5)
                r15.setColor(r3)
                java.lang.String r5 = "fonts/rcondensedbold.ttf"
                android.graphics.Typeface r5 = org.telegram.messenger.AndroidUtilities.getTypeface(r5)
                r15.setTypeface(r5)
                android.graphics.Bitmap r5 = r0.contentBitmap
                int r5 = r5.getWidth()
                r6 = 1101004800(0x41a00000, float:20.0)
                int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
                r14 = 2
                int r6 = r6 * 2
                int r5 = r5 - r6
                r13 = 0
                r6 = 0
            L_0x0055:
                r16 = 1082130432(0x40800000, float:4.0)
                r17 = 1073741824(0x40000000, float:2.0)
                r18 = 1106247680(0x41var_, float:30.0)
                r19 = 0
                r12 = 3
                r11 = 1
                if (r6 > r14) goto L_0x0175
                if (r6 != 0) goto L_0x0077
                android.content.Context r7 = r33.getContext()
                r8 = 2131166106(0x7var_a, float:1.7946448E38)
                android.graphics.drawable.Drawable r7 = androidx.core.content.ContextCompat.getDrawable(r7, r8)
                int r8 = org.telegram.messenger.AndroidUtilities.dp(r18)
                float r8 = (float) r8
                r15.setTextSize(r8)
                goto L_0x00a4
            L_0x0077:
                if (r6 != r11) goto L_0x008f
                android.content.Context r7 = r33.getContext()
                r8 = 2131166107(0x7var_b, float:1.794645E38)
                android.graphics.drawable.Drawable r7 = androidx.core.content.ContextCompat.getDrawable(r7, r8)
                r8 = 1103626240(0x41CLASSNAME, float:25.0)
                int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
                float r8 = (float) r8
                r15.setTextSize(r8)
                goto L_0x00a4
            L_0x008f:
                android.content.Context r7 = r33.getContext()
                r8 = 2131166108(0x7var_c, float:1.7946452E38)
                android.graphics.drawable.Drawable r7 = androidx.core.content.ContextCompat.getDrawable(r7, r8)
                r8 = 1100480512(0x41980000, float:19.0)
                int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
                float r8 = (float) r8
                r15.setTextSize(r8)
            L_0x00a4:
                if (r7 == 0) goto L_0x00bb
                int r8 = r7.getIntrinsicWidth()
                int r9 = r7.getIntrinsicHeight()
                r7.setBounds(r13, r13, r8, r9)
                android.graphics.PorterDuffColorFilter r8 = new android.graphics.PorterDuffColorFilter
                android.graphics.PorterDuff$Mode r9 = android.graphics.PorterDuff.Mode.SRC_IN
                r8.<init>(r3, r9)
                r7.setColorFilter(r8)
            L_0x00bb:
                android.text.SpannableStringBuilder r8 = new android.text.SpannableStringBuilder
                java.lang.StringBuilder r9 = new java.lang.StringBuilder
                r9.<init>()
                java.lang.String r10 = " "
                r9.append(r10)
                boolean r10 = r0.isPhone
                if (r10 == 0) goto L_0x00ce
                java.lang.String r10 = r0.username
                goto L_0x00d4
            L_0x00ce:
                java.lang.String r10 = r0.username
                java.lang.String r10 = r10.toUpperCase()
            L_0x00d4:
                r9.append(r10)
                java.lang.String r9 = r9.toString()
                r8.<init>(r9)
                boolean r9 = r0.isPhone
                if (r9 != 0) goto L_0x00ec
                org.telegram.ui.Cells.SettingsSearchCell$VerticalImageSpan r9 = new org.telegram.ui.Cells.SettingsSearchCell$VerticalImageSpan
                r9.<init>(r7)
                r10 = 33
                r8.setSpan(r9, r13, r11, r10)
            L_0x00ec:
                int r9 = r8.length()
                float r9 = r15.measureText(r8, r11, r9)
                android.graphics.Rect r10 = r7.getBounds()
                int r10 = r10.width()
                float r10 = (float) r10
                float r9 = r9 + r10
                if (r6 > r11) goto L_0x0109
                float r10 = (float) r5
                int r10 = (r9 > r10 ? 1 : (r9 == r10 ? 0 : -1))
                if (r10 <= 0) goto L_0x0109
                int r6 = r6 + 1
                goto L_0x0055
            L_0x0109:
                float r6 = (float) r5
                int r6 = (r9 > r6 ? 1 : (r9 == r6 ? 0 : -1))
                if (r6 <= 0) goto L_0x0110
                r6 = 2
                goto L_0x0111
            L_0x0110:
                r6 = 1
            L_0x0111:
                if (r6 <= r11) goto L_0x0126
                android.graphics.Rect r10 = r7.getBounds()
                int r10 = r10.width()
                float r10 = (float) r10
                float r10 = r10 + r9
                int r10 = (int) r10
                int r10 = r10 / r14
                int r20 = org.telegram.messenger.AndroidUtilities.dp(r17)
                int r10 = r10 + r20
                goto L_0x0127
            L_0x0126:
                r10 = r5
            L_0x0127:
                if (r10 <= r5) goto L_0x013e
                android.graphics.Rect r5 = r7.getBounds()
                int r5 = r5.width()
                float r5 = (float) r5
                float r9 = r9 + r5
                int r5 = (int) r9
                int r5 = r5 / r12
                int r6 = org.telegram.messenger.AndroidUtilities.dp(r16)
                int r5 = r5 + r6
                r7 = r5
                r20 = 3
                goto L_0x0141
            L_0x013e:
                r20 = r6
                r7 = r10
            L_0x0141:
                android.text.Layout$Alignment r9 = android.text.Layout.Alignment.ALIGN_CENTER
                r10 = 1065353216(0x3var_, float:1.0)
                r21 = 0
                r22 = 0
                r23 = 0
                r5 = 1092616192(0x41200000, float:10.0)
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
                int r5 = r5 + r7
                android.graphics.Bitmap r6 = r0.contentBitmap
                int r6 = r6.getWidth()
                int r24 = java.lang.Math.min(r5, r6)
                r5 = r8
                r6 = r15
                r8 = r9
                r9 = r10
                r10 = r21
                r3 = 1
                r11 = r22
                r3 = 3
                r12 = r23
                r23 = 0
                r13 = r24
                r24 = 2
                r14 = r20
                android.text.StaticLayout r5 = org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(r5, r6, r7, r8, r9, r10, r11, r12, r13, r14)
                goto L_0x017c
            L_0x0175:
                r3 = 3
                r23 = 0
                r24 = 2
                r5 = r19
            L_0x017c:
                float r6 = r15.descent()
                float r7 = r15.ascent()
                float r6 = r6 - r7
                int r7 = r5.getLineCount()
                float r7 = (float) r7
                float r6 = r6 * r7
                int r7 = org.telegram.messenger.AndroidUtilities.dp(r18)
                int r7 = r7 * 2
                int r7 = r1 - r7
                java.util.HashMap r8 = new java.util.HashMap
                r8.<init>()
                com.google.zxing.EncodeHintType r9 = com.google.zxing.EncodeHintType.ERROR_CORRECTION
                com.google.zxing.qrcode.decoder.ErrorCorrectionLevel r10 = com.google.zxing.qrcode.decoder.ErrorCorrectionLevel.M
                r8.put(r9, r10)
                com.google.zxing.EncodeHintType r9 = com.google.zxing.EncodeHintType.MARGIN
                java.lang.Integer r10 = java.lang.Integer.valueOf(r23)
                r8.put(r9, r10)
                com.google.zxing.qrcode.QRCodeWriter r9 = new com.google.zxing.qrcode.QRCodeWriter
                r9.<init>()
                r12 = 3
                r13 = 0
            L_0x01b0:
                r10 = 5
                if (r12 >= r10) goto L_0x01e2
                com.google.zxing.EncodeHintType r10 = com.google.zxing.EncodeHintType.QR_VERSION     // Catch:{ Exception -> 0x01db }
                java.lang.Integer r11 = java.lang.Integer.valueOf(r12)     // Catch:{ Exception -> 0x01db }
                r8.put(r10, r11)     // Catch:{ Exception -> 0x01db }
                java.lang.String r10 = r0.link     // Catch:{ Exception -> 0x01db }
                r29 = 0
                r30 = 1061158912(0x3var_, float:0.75)
                r31 = 16777215(0xffffff, float:2.3509886E-38)
                r32 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
                r24 = r9
                r25 = r10
                r26 = r7
                r27 = r7
                r28 = r8
                android.graphics.Bitmap r19 = r24.encode(r25, r26, r27, r28, r29, r30, r31, r32)     // Catch:{ Exception -> 0x01db }
                int r10 = r9.getImageSize()     // Catch:{ Exception -> 0x01db }
                r13 = r10
                goto L_0x01dc
            L_0x01db:
            L_0x01dc:
                if (r19 == 0) goto L_0x01df
                goto L_0x01e2
            L_0x01df:
                int r12 = r12 + 1
                goto L_0x01b0
            L_0x01e2:
                r7 = r19
                if (r7 != 0) goto L_0x01e7
                return
            L_0x01e7:
                android.graphics.Canvas r8 = new android.graphics.Canvas
                android.graphics.Bitmap r9 = r0.contentBitmap
                r8.<init>(r9)
                r8.drawColor(r4)
                int r4 = r7.getWidth()
                int r1 = r1 - r4
                float r1 = (float) r1
                float r1 = r1 / r17
                float r2 = (float) r2
                r4 = 1041865114(0x3e19999a, float:0.15)
                float r4 = r4 * r2
                int r9 = r5.getLineCount()
                if (r9 != r3) goto L_0x020a
                r4 = 1040522936(0x3e051eb8, float:0.13)
                float r4 = r4 * r2
            L_0x020a:
                android.view.ViewParent r9 = r33.getParent()
                android.view.ViewGroup r9 = (android.view.ViewGroup) r9
                int r9 = r9.getMeasuredWidth()
                android.view.ViewParent r10 = r33.getParent()
                android.view.ViewGroup r10 = (android.view.ViewGroup) r10
                int r10 = r10.getMeasuredHeight()
                if (r9 >= r10) goto L_0x0222
                r23 = 1
            L_0x0222:
                if (r23 != 0) goto L_0x0229
                r4 = 1035489772(0x3db851ec, float:0.09)
                float r4 = r4 * r2
            L_0x0229:
                android.graphics.Paint r2 = new android.graphics.Paint
                r2.<init>(r3)
                r8.drawBitmap(r7, r1, r4, r2)
                android.graphics.Paint r2 = new android.graphics.Paint
                r3 = 1
                r2.<init>(r3)
                r3 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
                r2.setColor(r3)
                int r3 = r7.getWidth()
                float r3 = (float) r3
                r9 = 1056964608(0x3var_, float:0.5)
                float r3 = r3 * r9
                float r1 = r1 + r3
                int r3 = r7.getWidth()
                float r3 = (float) r3
                float r3 = r3 * r9
                float r3 = r3 + r4
                float r10 = (float) r13
                float r10 = r10 * r9
                r8.drawCircle(r1, r3, r10, r2)
                org.telegram.ui.QrActivity$QrView$QrCenterChangedListener r2 = r0.centerChangedListener
                if (r2 == 0) goto L_0x0265
                float r11 = r1 - r10
                int r11 = (int) r11
                float r12 = r3 - r10
                int r12 = (int) r12
                float r1 = r1 + r10
                int r1 = (int) r1
                float r3 = r3 + r10
                int r3 = (int) r3
                r2.onCenterChanged(r11, r12, r1, r3)
            L_0x0265:
                int r1 = r8.getWidth()
                int r2 = r5.getWidth()
                int r1 = r1 - r2
                float r1 = (float) r1
                float r1 = r1 * r9
                int r2 = r7.getHeight()
                float r2 = (float) r2
                float r2 = r2 + r4
                int r3 = r8.getHeight()
                float r3 = (float) r3
                int r10 = r7.getHeight()
                float r10 = (float) r10
                float r4 = r4 + r10
                float r3 = r3 - r4
                float r3 = r3 - r6
                float r3 = r3 * r9
                float r2 = r2 + r3
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r16)
                float r3 = (float) r3
                float r2 = r2 - r3
                r8.save()
                r8.translate(r1, r2)
                r5.draw(r8)
                r8.restore()
                r7.recycle()
                android.graphics.Bitmap r1 = r0.contentBitmap
                android.graphics.Bitmap r2 = r1.extractAlpha()
                r0.contentBitmap = r2
                r1.recycle()
            L_0x02a7:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.QrActivity.QrView.prepareContent(int, int):void");
        }
    }

    private class ThemeListViewController implements NotificationCenter.NotificationCenterDelegate {
        public final ChatThemeBottomSheet.Adapter adapter;
        /* access modifiers changed from: private */
        public final Drawable backgroundDrawable;
        /* access modifiers changed from: private */
        public final Paint backgroundPaint = new Paint(1);
        /* access modifiers changed from: private */
        public final View bottomShadow;
        /* access modifiers changed from: private */
        public View changeDayNightView;
        /* access modifiers changed from: private */
        public ValueAnimator changeDayNightViewAnimator;
        /* access modifiers changed from: private */
        public float changeDayNightViewProgress;
        /* access modifiers changed from: private */
        public final RLottieDrawable darkThemeDrawable;
        /* access modifiers changed from: private */
        public final RLottieImageView darkThemeView;
        private boolean forceDark;
        /* access modifiers changed from: private */
        public final BaseFragment fragment;
        protected boolean isLightDarkChangeAnimation;
        private OnItemSelectedListener itemSelectedListener;
        /* access modifiers changed from: private */
        public LinearLayoutManager layoutManager;
        /* access modifiers changed from: private */
        public boolean prevIsPortrait;
        public int prevSelectedPosition = -1;
        public final FlickerLoadingView progressView;
        /* access modifiers changed from: private */
        public final RecyclerListView recyclerView;
        public final FrameLayout rootLayout;
        private final LinearSmoothScroller scroller;
        public ChatThemeBottomSheet.ChatThemeItem selectedItem;
        public final TextView shareButton;
        final /* synthetic */ QrActivity this$0;
        public final TextView titleView;
        /* access modifiers changed from: private */
        public final View topShadow;
        private final Window window;

        /* access modifiers changed from: protected */
        public void setDarkTheme(boolean z) {
        }

        public ThemeListViewController(QrActivity qrActivity, BaseFragment baseFragment, Window window2) {
            QrActivity qrActivity2 = qrActivity;
            BaseFragment baseFragment2 = baseFragment;
            this.this$0 = qrActivity2;
            this.fragment = baseFragment2;
            this.window = window2;
            Activity parentActivity = baseFragment.getParentActivity();
            this.scroller = new LinearSmoothScroller(this, parentActivity, qrActivity2) {
                /* access modifiers changed from: protected */
                public int calculateTimeForScrolling(int i) {
                    return super.calculateTimeForScrolling(i) * 6;
                }
            };
            Drawable mutate = parentActivity.getResources().getDrawable(NUM).mutate();
            this.backgroundDrawable = mutate;
            mutate.setColorFilter(new PorterDuffColorFilter(baseFragment2.getThemedColor("dialogBackground"), PorterDuff.Mode.MULTIPLY));
            AnonymousClass2 r5 = new FrameLayout(parentActivity, qrActivity2, baseFragment2) {
                private final Rect backgroundPadding;
                final /* synthetic */ BaseFragment val$fragment;

                {
                    this.val$fragment = r5;
                    Rect rect = new Rect();
                    this.backgroundPadding = rect;
                    ThemeListViewController.this.backgroundPaint.setColor(r5.getThemedColor("windowBackgroundWhite"));
                    ThemeListViewController.this.backgroundDrawable.setCallback(this);
                    ThemeListViewController.this.backgroundDrawable.getPadding(rect);
                    setPadding(0, rect.top + AndroidUtilities.dp(8.0f), 0, rect.bottom);
                }

                /* access modifiers changed from: protected */
                public void onMeasure(int i, int i2) {
                    Point point = AndroidUtilities.displaySize;
                    boolean z = point.x < point.y;
                    int dp = AndroidUtilities.dp(12.0f);
                    if (z) {
                        ThemeListViewController.this.recyclerView.setLayoutParams(LayoutHelper.createFrame(-1, 104.0f, 8388611, 0.0f, 44.0f, 0.0f, 0.0f));
                        ThemeListViewController.this.recyclerView.setPadding(dp, 0, dp, 0);
                        ThemeListViewController.this.shareButton.setLayoutParams(LayoutHelper.createFrame(-1, 48.0f, 8388611, 16.0f, 162.0f, 16.0f, 16.0f));
                    } else {
                        ThemeListViewController.this.recyclerView.setLayoutParams(LayoutHelper.createFrame(-1, -1.0f, 8388611, 0.0f, 44.0f, 0.0f, 80.0f));
                        ThemeListViewController.this.recyclerView.setPadding(dp, dp / 2, dp, dp);
                        ThemeListViewController.this.shareButton.setLayoutParams(LayoutHelper.createFrame(-1, 48.0f, 80, 16.0f, 0.0f, 16.0f, 16.0f));
                    }
                    if (z) {
                        ThemeListViewController.this.bottomShadow.setVisibility(8);
                        ThemeListViewController.this.topShadow.setVisibility(8);
                    } else {
                        ThemeListViewController.this.bottomShadow.setVisibility(0);
                        ThemeListViewController.this.bottomShadow.setLayoutParams(LayoutHelper.createFrame(-1, (float) AndroidUtilities.dp(2.0f), 80, 0.0f, 0.0f, 0.0f, 80.0f));
                        ThemeListViewController.this.topShadow.setVisibility(0);
                        ThemeListViewController.this.topShadow.setLayoutParams(LayoutHelper.createFrame(-1, (float) AndroidUtilities.dp(2.0f), 48, 0.0f, 44.0f, 0.0f, 0.0f));
                    }
                    if (ThemeListViewController.this.prevIsPortrait != z) {
                        RecyclerListView access$2100 = ThemeListViewController.this.recyclerView;
                        ThemeListViewController themeListViewController = ThemeListViewController.this;
                        access$2100.setLayoutManager(themeListViewController.layoutManager = themeListViewController.getLayoutManager(z));
                        ThemeListViewController.this.recyclerView.requestLayout();
                        ThemeListViewController themeListViewController2 = ThemeListViewController.this;
                        int i3 = themeListViewController2.prevSelectedPosition;
                        if (i3 != -1) {
                            themeListViewController2.setSelectedPosition(i3);
                        }
                        boolean unused = ThemeListViewController.this.prevIsPortrait = z;
                    }
                    super.onMeasure(i, i2);
                }

                /* access modifiers changed from: protected */
                public void dispatchDraw(Canvas canvas) {
                    if (ThemeListViewController.this.prevIsPortrait) {
                        ThemeListViewController.this.backgroundDrawable.setBounds(-this.backgroundPadding.left, 0, getWidth() + this.backgroundPadding.right, getHeight());
                        ThemeListViewController.this.backgroundDrawable.draw(canvas);
                    } else {
                        RectF rectF = AndroidUtilities.rectTmp;
                        rectF.set(0.0f, 0.0f, (float) (getWidth() + AndroidUtilities.dp(14.0f)), (float) getHeight());
                        canvas.drawRoundRect(rectF, (float) AndroidUtilities.dp(14.0f), (float) AndroidUtilities.dp(14.0f), ThemeListViewController.this.backgroundPaint);
                    }
                    super.dispatchDraw(canvas);
                }

                /* access modifiers changed from: protected */
                public boolean verifyDrawable(Drawable drawable) {
                    return drawable == ThemeListViewController.this.backgroundDrawable || super.verifyDrawable(drawable);
                }
            };
            this.rootLayout = r5;
            TextView textView = new TextView(parentActivity);
            this.titleView = textView;
            textView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
            textView.setLines(1);
            textView.setSingleLine(true);
            textView.setTextColor(baseFragment2.getThemedColor("dialogTextBlack"));
            textView.setTextSize(1, 20.0f);
            textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            textView.setPadding(AndroidUtilities.dp(21.0f), AndroidUtilities.dp(6.0f), AndroidUtilities.dp(21.0f), AndroidUtilities.dp(8.0f));
            r5.addView(textView, LayoutHelper.createFrame(-1, -2.0f, 8388659, 0.0f, 0.0f, 62.0f, 0.0f));
            int themedColor = baseFragment2.getThemedColor("featuredStickers_addButton");
            int dp = AndroidUtilities.dp(28.0f);
            RLottieDrawable rLottieDrawable = new RLottieDrawable(NUM, "NUM", dp, dp, false, (int[]) null);
            this.darkThemeDrawable = rLottieDrawable;
            this.forceDark = !Theme.getActiveTheme().isDark();
            setForceDark(Theme.getActiveTheme().isDark(), false);
            rLottieDrawable.setPlayInDirectionOfCustomEndFrame(true);
            rLottieDrawable.setColorFilter(new PorterDuffColorFilter(themedColor, PorterDuff.Mode.MULTIPLY));
            AnonymousClass3 r8 = new RLottieImageView(parentActivity, qrActivity2) {
                public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
                    super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
                    if (ThemeListViewController.this.this$0.isCurrentThemeDark) {
                        accessibilityNodeInfo.setText(LocaleController.getString("AccDescrSwitchToDayTheme", NUM));
                    } else {
                        accessibilityNodeInfo.setText(LocaleController.getString("AccDescrSwitchToNightTheme", NUM));
                    }
                }
            };
            this.darkThemeView = r8;
            r8.setAnimation(rLottieDrawable);
            r8.setScaleType(ImageView.ScaleType.CENTER);
            r8.setOnClickListener(new QrActivity$ThemeListViewController$$ExternalSyntheticLambda1(this));
            r8.setAlpha(0.0f);
            r8.setVisibility(4);
            r5.addView(r8, LayoutHelper.createFrame(44, 44.0f, 8388661, 0.0f, -2.0f, 7.0f, 0.0f));
            FlickerLoadingView flickerLoadingView = new FlickerLoadingView(parentActivity, baseFragment.getResourceProvider());
            this.progressView = flickerLoadingView;
            flickerLoadingView.setVisibility(0);
            r5.addView(flickerLoadingView, LayoutHelper.createFrame(-1, 104.0f, 8388611, 0.0f, 44.0f, 0.0f, 0.0f));
            Point point = AndroidUtilities.displaySize;
            this.prevIsPortrait = point.x < point.y;
            RecyclerListView recyclerListView = new RecyclerListView(parentActivity);
            this.recyclerView = recyclerListView;
            ChatThemeBottomSheet.Adapter adapter2 = new ChatThemeBottomSheet.Adapter(qrActivity.currentAccount, qrActivity.resourcesProvider, 2);
            this.adapter = adapter2;
            recyclerListView.setAdapter(adapter2);
            recyclerListView.setClipChildren(false);
            recyclerListView.setClipToPadding(false);
            recyclerListView.setItemAnimator((RecyclerView.ItemAnimator) null);
            recyclerListView.setNestedScrollingEnabled(false);
            LinearLayoutManager layoutManager2 = getLayoutManager(this.prevIsPortrait);
            this.layoutManager = layoutManager2;
            recyclerListView.setLayoutManager(layoutManager2);
            recyclerListView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new QrActivity$ThemeListViewController$$ExternalSyntheticLambda4(this));
            recyclerListView.setOnScrollListener(new RecyclerView.OnScrollListener(qrActivity2) {
                private int yScroll = 0;

                public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                    super.onScrolled(recyclerView, i, i2);
                    this.yScroll += i2;
                    ThemeListViewController.this.topShadow.setAlpha((((float) this.yScroll) * 1.0f) / ((float) AndroidUtilities.dp(6.0f)));
                }
            });
            r5.addView(recyclerListView);
            View view = new View(parentActivity);
            this.topShadow = view;
            view.setAlpha(0.0f);
            view.setBackground(ContextCompat.getDrawable(parentActivity, NUM));
            view.setRotation(180.0f);
            r5.addView(view);
            View view2 = new View(parentActivity);
            this.bottomShadow = view2;
            view2.setBackground(ContextCompat.getDrawable(parentActivity, NUM));
            r5.addView(view2);
            TextView textView2 = new TextView(parentActivity);
            this.shareButton = textView2;
            textView2.setBackground(Theme.AdaptiveRipple.filledRect(baseFragment2.getThemedColor("featuredStickers_addButton"), 6.0f));
            textView2.setEllipsize(TextUtils.TruncateAt.END);
            textView2.setGravity(17);
            textView2.setLines(1);
            textView2.setSingleLine(true);
            textView2.setText(LocaleController.getString("ShareQrCode", NUM));
            textView2.setTextColor(baseFragment2.getThemedColor("featuredStickers_buttonText"));
            textView2.setTextSize(1, 15.0f);
            textView2.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            r5.addView(textView2);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0(View view) {
            if (this.changeDayNightViewAnimator == null) {
                setupLightDarkTheme(!this.forceDark);
            }
        }

        public void onCreate() {
            ChatThemeController.preloadAllWallpaperThumbs(true);
            ChatThemeController.preloadAllWallpaperThumbs(false);
            ChatThemeController.preloadAllWallpaperImages(true);
            ChatThemeController.preloadAllWallpaperImages(false);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
        }

        @SuppressLint({"NotifyDataSetChanged"})
        public void didReceivedNotification(int i, int i2, Object... objArr) {
            if (i == NotificationCenter.emojiLoaded) {
                this.adapter.notifyDataSetChanged();
            }
        }

        public void onDestroy() {
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
        }

        public void setItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
            this.itemSelectedListener = onItemSelectedListener;
        }

        public void onDataLoaded() {
            this.darkThemeView.setAlpha(0.0f);
            this.darkThemeView.animate().alpha(1.0f).setDuration(150).start();
            this.darkThemeView.setVisibility(0);
            this.progressView.animate().alpha(0.0f).setListener(new HideViewAfterAnimation(this.progressView)).setDuration(150).start();
            this.recyclerView.setAlpha(0.0f);
            this.recyclerView.animate().alpha(1.0f).setDuration(150).start();
        }

        public void setSelectedPosition(int i) {
            this.prevSelectedPosition = i;
            this.adapter.setSelectedItem(i);
            if (i > 0 && i < this.adapter.items.size() / 2) {
                i--;
            }
            this.layoutManager.scrollToPositionWithOffset(Math.min(i, this.adapter.items.size() - 1), 0);
        }

        /* access modifiers changed from: protected */
        public void onItemClicked(View view, int i) {
            if (this.adapter.items.get(i) != this.selectedItem && this.changeDayNightView == null) {
                this.isLightDarkChangeAnimation = false;
                this.selectedItem = this.adapter.items.get(i);
                this.adapter.setSelectedItem(i);
                this.rootLayout.postDelayed(new QrActivity$ThemeListViewController$$ExternalSyntheticLambda2(this, i), 100);
                for (int i2 = 0; i2 < this.recyclerView.getChildCount(); i2++) {
                    ThemeSmallPreviewView themeSmallPreviewView = (ThemeSmallPreviewView) this.recyclerView.getChildAt(i2);
                    if (themeSmallPreviewView != view) {
                        themeSmallPreviewView.cancelAnimation();
                    }
                }
                if (!this.adapter.items.get(i).chatTheme.showAsDefaultStub) {
                    ((ThemeSmallPreviewView) view).playEmojiAnimation();
                }
                OnItemSelectedListener onItemSelectedListener = this.itemSelectedListener;
                if (onItemSelectedListener != null) {
                    onItemSelectedListener.onItemSelected(this.selectedItem.chatTheme, i);
                }
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onItemClicked$1(int i) {
            int i2;
            RecyclerView.LayoutManager layoutManager2 = this.recyclerView.getLayoutManager();
            if (layoutManager2 != null) {
                if (i > this.prevSelectedPosition) {
                    i2 = Math.min(i + 1, this.adapter.items.size() - 1);
                } else {
                    i2 = Math.max(i - 1, 0);
                }
                this.scroller.setTargetPosition(i2);
                layoutManager2.startSmoothScroll(this.scroller);
            }
            this.prevSelectedPosition = i;
        }

        @SuppressLint({"NotifyDataSetChanged"})
        private void setupLightDarkTheme(boolean z) {
            ValueAnimator valueAnimator = this.changeDayNightViewAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            FrameLayout frameLayout = (FrameLayout) this.window.getDecorView();
            final Bitmap createBitmap = Bitmap.createBitmap(frameLayout.getWidth(), frameLayout.getHeight(), Bitmap.Config.ARGB_8888);
            final Canvas canvas = new Canvas(createBitmap);
            this.darkThemeView.setAlpha(0.0f);
            ((FrameLayout) this.fragment.getParentActivity().getWindow().getDecorView()).draw(canvas);
            frameLayout.draw(canvas);
            this.darkThemeView.setAlpha(1.0f);
            final Paint paint = new Paint(1);
            paint.setColor(-16777216);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            final Paint paint2 = new Paint(1);
            paint2.setFilterBitmap(true);
            int[] iArr = new int[2];
            this.darkThemeView.getLocationInWindow(iArr);
            final float f = (float) iArr[0];
            float f2 = (float) iArr[1];
            final float measuredWidth = f + (((float) this.darkThemeView.getMeasuredWidth()) / 2.0f);
            final float measuredHeight = f2 + (((float) this.darkThemeView.getMeasuredHeight()) / 2.0f);
            float max = ((float) Math.max(createBitmap.getHeight(), createBitmap.getWidth())) * 0.9f;
            Shader.TileMode tileMode = Shader.TileMode.CLAMP;
            paint2.setShader(new BitmapShader(createBitmap, tileMode, tileMode));
            AnonymousClass5 r15 = r0;
            final boolean z2 = z;
            float f3 = f2;
            final float f4 = max;
            final float f5 = f3;
            AnonymousClass5 r0 = new View(this.fragment.getParentActivity()) {
                /* access modifiers changed from: protected */
                public void onDraw(Canvas canvas) {
                    super.onDraw(canvas);
                    if (z2) {
                        if (ThemeListViewController.this.changeDayNightViewProgress > 0.0f) {
                            canvas.drawCircle(measuredWidth, measuredHeight, f4 * ThemeListViewController.this.changeDayNightViewProgress, paint);
                        }
                        canvas.drawBitmap(createBitmap, 0.0f, 0.0f, paint2);
                    } else {
                        canvas.drawCircle(measuredWidth, measuredHeight, f4 * (1.0f - ThemeListViewController.this.changeDayNightViewProgress), paint2);
                    }
                    canvas.save();
                    canvas.translate(f, f5);
                    ThemeListViewController.this.darkThemeView.draw(canvas);
                    canvas.restore();
                }
            };
            this.changeDayNightView = r15;
            this.changeDayNightViewProgress = 0.0f;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            this.changeDayNightViewAnimator = ofFloat;
            ofFloat.addUpdateListener(new QrActivity$ThemeListViewController$$ExternalSyntheticLambda0(this));
            this.changeDayNightViewAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (ThemeListViewController.this.changeDayNightView != null) {
                        if (ThemeListViewController.this.changeDayNightView.getParent() != null) {
                            ((ViewGroup) ThemeListViewController.this.changeDayNightView.getParent()).removeView(ThemeListViewController.this.changeDayNightView);
                        }
                        View unused = ThemeListViewController.this.changeDayNightView = null;
                    }
                    ValueAnimator unused2 = ThemeListViewController.this.changeDayNightViewAnimator = null;
                    super.onAnimationEnd(animator);
                }
            });
            this.changeDayNightViewAnimator.setDuration(400);
            this.changeDayNightViewAnimator.setInterpolator(Easings.easeInOutQuad);
            this.changeDayNightViewAnimator.start();
            frameLayout.addView(this.changeDayNightView, new ViewGroup.LayoutParams(-1, -1));
            AndroidUtilities.runOnUIThread(new QrActivity$ThemeListViewController$$ExternalSyntheticLambda3(this, z));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$setupLightDarkTheme$2(ValueAnimator valueAnimator) {
            this.changeDayNightViewProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            this.changeDayNightView.invalidate();
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$setupLightDarkTheme$3(boolean z) {
            ChatThemeBottomSheet.Adapter adapter2 = this.adapter;
            if (adapter2 != null && adapter2.items != null) {
                setForceDark(z, true);
                if (this.selectedItem != null) {
                    this.isLightDarkChangeAnimation = true;
                    setDarkTheme(z);
                }
                if (this.adapter.items != null) {
                    for (int i = 0; i < this.adapter.items.size(); i++) {
                        this.adapter.items.get(i).themeIndex = z ? 1 : 0;
                        this.adapter.items.get(i).icon = this.this$0.getEmojiThemeIcon(this.adapter.items.get(i).chatTheme, z);
                    }
                    MotionBackgroundDrawable unused = this.this$0.tempMotionDrawable = null;
                    this.adapter.notifyDataSetChanged();
                }
            }
        }

        public void setForceDark(boolean z, boolean z2) {
            if (this.forceDark != z) {
                this.forceDark = z;
                int framesCount = z ? this.darkThemeDrawable.getFramesCount() - 1 : 0;
                if (z2) {
                    this.darkThemeDrawable.setCustomEndFrame(framesCount);
                    RLottieImageView rLottieImageView = this.darkThemeView;
                    if (rLottieImageView != null) {
                        rLottieImageView.playAnimation();
                        return;
                    }
                    return;
                }
                this.darkThemeDrawable.setCustomEndFrame(framesCount);
                this.darkThemeDrawable.setCurrentFrame(framesCount, false, true);
                RLottieImageView rLottieImageView2 = this.darkThemeView;
                if (rLottieImageView2 != null) {
                    rLottieImageView2.invalidate();
                }
            }
        }

        /* access modifiers changed from: private */
        public LinearLayoutManager getLayoutManager(boolean z) {
            if (z) {
                return new LinearLayoutManager(this.fragment.getParentActivity(), 0, false);
            }
            return new GridLayoutManager(this.fragment.getParentActivity(), 3, 1, false);
        }

        /* access modifiers changed from: private */
        public void onAnimationStart() {
            List<ChatThemeBottomSheet.ChatThemeItem> list;
            ChatThemeBottomSheet.Adapter adapter2 = this.adapter;
            if (!(adapter2 == null || (list = adapter2.items) == null)) {
                for (ChatThemeBottomSheet.ChatThemeItem chatThemeItem : list) {
                    chatThemeItem.themeIndex = this.forceDark ? 1 : 0;
                }
            }
            if (!this.isLightDarkChangeAnimation) {
                setItemsAnimationProgress(1.0f);
            }
        }

        /* access modifiers changed from: private */
        public void setItemsAnimationProgress(float f) {
            for (int i = 0; i < this.adapter.getItemCount(); i++) {
                this.adapter.items.get(i).animationProgress = f;
            }
        }

        /* access modifiers changed from: private */
        public void onAnimationEnd() {
            this.isLightDarkChangeAnimation = false;
        }

        public ArrayList<ThemeDescription> getThemeDescriptions() {
            AnonymousClass7 r7 = new ThemeDescription.ThemeDescriptionDelegate() {
                private boolean isAnimationStarted = false;

                public void didSetColor() {
                }

                public void onAnimationProgress(float f) {
                    if (f == 0.0f && !this.isAnimationStarted) {
                        ThemeListViewController.this.onAnimationStart();
                        this.isAnimationStarted = true;
                    }
                    ThemeListViewController.this.darkThemeDrawable.setColorFilter(new PorterDuffColorFilter(ThemeListViewController.this.fragment.getThemedColor("featuredStickers_addButton"), PorterDuff.Mode.MULTIPLY));
                    ThemeListViewController themeListViewController = ThemeListViewController.this;
                    if (themeListViewController.isLightDarkChangeAnimation) {
                        themeListViewController.setItemsAnimationProgress(f);
                    }
                    if (f == 1.0f && this.isAnimationStarted) {
                        ThemeListViewController themeListViewController2 = ThemeListViewController.this;
                        themeListViewController2.isLightDarkChangeAnimation = false;
                        themeListViewController2.onAnimationEnd();
                        this.isAnimationStarted = false;
                    }
                }
            };
            ArrayList<ThemeDescription> arrayList = new ArrayList<>();
            arrayList.add(new ThemeDescription((View) null, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, this.backgroundPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogBackground"));
            arrayList.add(new ThemeDescription((View) null, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, new Drawable[]{this.backgroundDrawable}, r7, "dialogBackground"));
            arrayList.add(new ThemeDescription(this.titleView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextBlack"));
            arrayList.add(new ThemeDescription(this.recyclerView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ThemeSmallPreviewView.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogBackgroundGray"));
            Iterator<ThemeDescription> it = arrayList.iterator();
            while (it.hasNext()) {
                it.next().resourcesProvider = this.fragment.getResourceProvider();
            }
            return arrayList;
        }
    }
}
