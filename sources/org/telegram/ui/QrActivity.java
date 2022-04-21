package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
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
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Pair;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
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
import com.google.zxing.EncodeHintType;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
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
import org.telegram.tgnet.ResultCallback;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.EmojiThemes;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.SettingsSearchCell;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.ChatThemeBottomSheet;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.Easings;
import org.telegram.ui.Components.FlickerLoadingView;
import org.telegram.ui.Components.HideViewAfterAnimation;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.MotionBackgroundDrawable;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.StaticLayoutEx;
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

    public QrActivity(Bundle args) {
        super(args);
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
        TLRPC.Chat chat;
        Context context2 = context;
        this.homeTheme.loadPreviewColors(this.currentAccount);
        this.isCurrentThemeDark = Theme.getActiveTheme().isDark();
        this.actionBar.setAddToContainer(false);
        this.actionBar.setBackground((Drawable) null);
        this.actionBar.setItemsColor(-1, false);
        FrameLayout rootLayout = new FrameLayout(context2) {
            private boolean prevIsPortrait;

            public boolean dispatchTouchEvent(MotionEvent ev) {
                super.dispatchTouchEvent(ev);
                return true;
            }

            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int width = View.MeasureSpec.getSize(widthMeasureSpec);
                int height = View.MeasureSpec.getSize(heightMeasureSpec);
                boolean isPortrait = width < height;
                QrActivity.this.avatarImageView.setVisibility(isPortrait ? 0 : 8);
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                if (isPortrait) {
                    QrActivity.this.themeLayout.measure(View.MeasureSpec.makeMeasureSpec(width, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(height, Integer.MIN_VALUE));
                    QrActivity.this.qrView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(260.0f), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(330.0f), NUM));
                } else {
                    QrActivity.this.themeLayout.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(256.0f), NUM), heightMeasureSpec);
                    QrActivity.this.qrView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(260.0f), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(310.0f), NUM));
                }
                if (this.prevIsPortrait != isPortrait) {
                    QrActivity.this.qrView.onSizeChanged(QrActivity.this.qrView.getMeasuredWidth(), QrActivity.this.qrView.getMeasuredHeight(), 0, 0);
                }
                this.prevIsPortrait = isPortrait;
            }

            /* access modifiers changed from: protected */
            public void onLayout(boolean changed, int left, int top, int right, int bottom) {
                int qrLeft;
                int qrTop;
                boolean isPortrait = getWidth() < getHeight();
                QrActivity.this.backgroundView.layout(0, 0, getWidth(), getHeight());
                int themeLayoutHeight = 0;
                if (QrActivity.this.themeLayout.getVisibility() == 0) {
                    themeLayoutHeight = QrActivity.this.themeLayout.getMeasuredHeight();
                }
                if (isPortrait) {
                    qrLeft = (getWidth() - QrActivity.this.qrView.getMeasuredWidth()) / 2;
                } else {
                    qrLeft = ((getWidth() - QrActivity.this.themeLayout.getMeasuredWidth()) - QrActivity.this.qrView.getMeasuredWidth()) / 2;
                }
                if (isPortrait) {
                    qrTop = ((((getHeight() - themeLayoutHeight) - QrActivity.this.qrView.getMeasuredHeight()) - AndroidUtilities.dp(48.0f)) / 2) + AndroidUtilities.dp(52.0f);
                } else {
                    qrTop = (getHeight() - QrActivity.this.qrView.getMeasuredHeight()) / 2;
                }
                QrActivity.this.qrView.layout(qrLeft, qrTop, QrActivity.this.qrView.getMeasuredWidth() + qrLeft, QrActivity.this.qrView.getMeasuredHeight() + qrTop);
                if (isPortrait) {
                    int avatarLeft = (getWidth() - QrActivity.this.avatarImageView.getMeasuredWidth()) / 2;
                    int avatarTop = qrTop - AndroidUtilities.dp(48.0f);
                    QrActivity.this.avatarImageView.layout(avatarLeft, avatarTop, QrActivity.this.avatarImageView.getMeasuredWidth() + avatarLeft, QrActivity.this.avatarImageView.getMeasuredHeight() + avatarTop);
                }
                if (QrActivity.this.themeLayout.getVisibility() == 0) {
                    if (isPortrait) {
                        int themeLayoutLeft = (getWidth() - QrActivity.this.themeLayout.getMeasuredWidth()) / 2;
                        QrActivity.this.themeLayout.layout(themeLayoutLeft, bottom - themeLayoutHeight, QrActivity.this.themeLayout.getMeasuredWidth() + themeLayoutLeft, bottom);
                    } else {
                        int themeLayoutTop = (getHeight() - QrActivity.this.themeLayout.getMeasuredHeight()) / 2;
                        QrActivity.this.themeLayout.layout(right - QrActivity.this.themeLayout.getMeasuredWidth(), themeLayoutTop, right, QrActivity.this.themeLayout.getMeasuredHeight() + themeLayoutTop);
                    }
                }
                QrActivity.this.logoImageView.layout(QrActivity.this.logoRect.left + qrLeft, QrActivity.this.logoRect.top + qrTop, QrActivity.this.logoRect.right + qrLeft, QrActivity.this.logoRect.bottom + qrTop);
                int closeLeft = AndroidUtilities.dp(isPortrait ? 14.0f : 17.0f);
                int closeTop = AndroidUtilities.statusBarHeight + AndroidUtilities.dp(isPortrait ? 10.0f : 5.0f);
                QrActivity.this.closeImageView.layout(closeLeft, closeTop, QrActivity.this.closeImageView.getMeasuredWidth() + closeLeft, QrActivity.this.closeImageView.getMeasuredHeight() + closeTop);
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
        rootLayout.addView(r6);
        AvatarDrawable avatarDrawable = null;
        String username = null;
        ImageLocation imageLocationSmall = null;
        ImageLocation imageLocation = null;
        if (this.userId != 0) {
            TLRPC.User user = getMessagesController().getUser(Long.valueOf(this.userId));
            if (user != null) {
                username = user.username;
                avatarDrawable = new AvatarDrawable(user);
                imageLocationSmall = ImageLocation.getForUser(user, 1);
                imageLocation = ImageLocation.getForUser(user, 0);
            }
        } else if (!(this.chatId == 0 || (chat = getMessagesController().getChat(Long.valueOf(this.chatId))) == null)) {
            username = chat.username;
            avatarDrawable = new AvatarDrawable(chat);
            imageLocationSmall = ImageLocation.getForChat(chat, 1);
            imageLocation = ImageLocation.getForChat(chat, 0);
        }
        QrView qrView2 = new QrView(context2);
        this.qrView = qrView2;
        qrView2.setColors(-9324972, -13856649, -6636738, -9915042);
        this.qrView.setData("https://" + MessagesController.getInstance(this.currentAccount).linkPrefix + "/" + username, username);
        this.qrView.setCenterChangedListener(new QrActivity$$ExternalSyntheticLambda9(this));
        rootLayout.addView(this.qrView);
        RLottieImageView rLottieImageView = new RLottieImageView(context2);
        this.logoImageView = rLottieImageView;
        rLottieImageView.setAutoRepeat(true);
        this.logoImageView.setAnimation(NUM, 60, 60);
        this.logoImageView.playAnimation();
        rootLayout.addView(this.logoImageView);
        BackupImageView backupImageView = new BackupImageView(context2);
        this.avatarImageView = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(42.0f));
        this.avatarImageView.setSize(AndroidUtilities.dp(84.0f), AndroidUtilities.dp(84.0f));
        rootLayout.addView(this.avatarImageView, LayoutHelper.createFrame(84, 84, 51));
        this.avatarImageView.setImage(imageLocation, "84_84", imageLocationSmall, "50_50", avatarDrawable, (Bitmap) null, (String) null, 0, (Object) null);
        ImageView imageView = new ImageView(context2);
        this.closeImageView = imageView;
        imageView.setBackground(Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(34.0f), NUM, NUM));
        this.closeImageView.setImageResource(NUM);
        this.closeImageView.setScaleType(ImageView.ScaleType.CENTER);
        this.closeImageView.setOnClickListener(new QrActivity$$ExternalSyntheticLambda2(this));
        rootLayout.addView(this.closeImageView, LayoutHelper.createFrame(34, 34.0f));
        this.emojiThemeIcon = Bitmap.createBitmap(AndroidUtilities.dp(32.0f), AndroidUtilities.dp(32.0f), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(this.emojiThemeIcon);
        AndroidUtilities.rectTmp.set(0.0f, 0.0f, (float) this.emojiThemeIcon.getWidth(), (float) this.emojiThemeIcon.getHeight());
        Paint paint = new Paint(1);
        paint.setColor(-1);
        canvas.drawRoundRect(AndroidUtilities.rectTmp, (float) AndroidUtilities.dp(5.0f), (float) AndroidUtilities.dp(5.0f), paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        Bitmap bitmap = BitmapFactory.decodeResource(ApplicationLoader.applicationContext.getResources(), NUM);
        canvas.drawBitmap(bitmap, ((float) (this.emojiThemeIcon.getWidth() - bitmap.getWidth())) * 0.5f, ((float) (this.emojiThemeIcon.getHeight() - bitmap.getHeight())) * 0.5f, paint);
        canvas.setBitmap((Bitmap) null);
        AnonymousClass3 r4 = new ThemeListViewController(this, getParentActivity().getWindow()) {
            /* access modifiers changed from: protected */
            public void setDarkTheme(boolean isDark) {
                super.setDarkTheme(isDark);
                boolean unused = QrActivity.this.isCurrentThemeDark = isDark;
                QrActivity qrActivity = QrActivity.this;
                qrActivity.onItemSelected(qrActivity.currentTheme, QrActivity.this.selectedPosition, false);
            }
        };
        this.themesViewController = r4;
        this.themeLayout = r4.rootLayout;
        this.themesViewController.onCreate();
        this.themesViewController.setItemSelectedListener(new QrActivity$$ExternalSyntheticLambda8(this));
        this.themesViewController.titleView.setText(LocaleController.getString("QrCode", NUM));
        this.themesViewController.progressView.setViewType(17);
        this.themesViewController.shareButton.setOnClickListener(new QrActivity$$ExternalSyntheticLambda3(this));
        rootLayout.addView(this.themeLayout, LayoutHelper.createFrame(-1, -2, 80));
        this.currMotionDrawable.setIndeterminateAnimation(true);
        this.fragmentView = rootLayout;
        onItemSelected(this.currentTheme, 0, false);
        List<EmojiThemes> list = cachedThemes;
        if (list == null || list.isEmpty()) {
            ChatThemeController.requestAllChatThemes(new ResultCallback<List<EmojiThemes>>() {
                public /* synthetic */ void onError(Throwable th) {
                    ResultCallback.CC.$default$onError((ResultCallback) this, th);
                }

                public void onComplete(List<EmojiThemes> result) {
                    QrActivity.this.onDataLoaded(result);
                    List unused = QrActivity.cachedThemes = result;
                }

                public void onError(TLRPC.TL_error error) {
                    Toast.makeText(QrActivity.this.getParentActivity(), error.text, 0).show();
                }
            }, true);
        } else {
            onDataLoaded(cachedThemes);
        }
        this.prevSystemUiVisibility = getParentActivity().getWindow().getDecorView().getSystemUiVisibility();
        applyScreenSettings();
        return this.fragmentView;
    }

    /* renamed from: lambda$createView$0$org-telegram-ui-QrActivity  reason: not valid java name */
    public /* synthetic */ void m3195lambda$createView$0$orgtelegramuiQrActivity(int left, int top, int right, int bottom) {
        this.logoRect.set(left, top, right, bottom);
        this.qrView.requestLayout();
    }

    /* renamed from: lambda$createView$1$org-telegram-ui-QrActivity  reason: not valid java name */
    public /* synthetic */ void m3196lambda$createView$1$orgtelegramuiQrActivity(View v) {
        finishFragment();
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-QrActivity  reason: not valid java name */
    public /* synthetic */ void m3197lambda$createView$2$orgtelegramuiQrActivity(EmojiThemes theme, int position) {
        onItemSelected(theme, position, true);
    }

    /* renamed from: lambda$createView$3$org-telegram-ui-QrActivity  reason: not valid java name */
    public /* synthetic */ void m3198lambda$createView$3$orgtelegramuiQrActivity(View v) {
        this.themesViewController.shareButton.setClickable(false);
        performShare();
    }

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
            Bitmap bitmap = this.emojiThemeDarkIcons.valueAt(i);
            if (bitmap != null) {
                bitmap.recycle();
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
    public void onDataLoaded(List<EmojiThemes> result) {
        if (result != null && !result.isEmpty() && this.themesViewController != null) {
            result.set(0, this.homeTheme);
            List<ChatThemeBottomSheet.ChatThemeItem> items = new ArrayList<>(result.size());
            for (int i = 0; i < result.size(); i++) {
                EmojiThemes chatTheme = result.get(i);
                chatTheme.loadPreviewColors(this.currentAccount);
                ChatThemeBottomSheet.ChatThemeItem item = new ChatThemeBottomSheet.ChatThemeItem(chatTheme);
                item.themeIndex = this.isCurrentThemeDark ? 1 : 0;
                item.icon = getEmojiThemeIcon(chatTheme, this.isCurrentThemeDark);
                items.add(item);
            }
            this.themesViewController.adapter.setItems(items);
            int selectedPosition2 = -1;
            int i2 = 0;
            while (true) {
                if (i2 == items.size()) {
                    break;
                } else if (items.get(i2).chatTheme.getEmoticon().equals(this.currentTheme.getEmoticon())) {
                    this.themesViewController.selectedItem = items.get(i2);
                    selectedPosition2 = i2;
                    break;
                } else {
                    i2++;
                }
            }
            if (selectedPosition2 != -1) {
                this.themesViewController.setSelectedPosition(selectedPosition2);
            }
            this.themesViewController.onDataLoaded();
        }
    }

    /* access modifiers changed from: private */
    public Bitmap getEmojiThemeIcon(EmojiThemes theme, boolean isDark) {
        if (!isDark) {
            return this.emojiThemeIcon;
        }
        Bitmap bitmap = this.emojiThemeDarkIcons.get(theme.emoji);
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(this.emojiThemeIcon.getWidth(), this.emojiThemeIcon.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            ArrayMap<String, int[]> arrayMap = qrColorsMap;
            int[] colors = arrayMap.get(theme.emoji + "n");
            if (colors != null) {
                if (this.tempMotionDrawable == null) {
                    this.tempMotionDrawable = new MotionBackgroundDrawable(0, 0, 0, 0, true);
                }
                this.tempMotionDrawable.setColors(colors[0], colors[1], colors[2], colors[3]);
                this.tempMotionDrawable.setBounds(AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f), canvas.getWidth() - AndroidUtilities.dp(6.0f), canvas.getHeight() - AndroidUtilities.dp(6.0f));
                this.tempMotionDrawable.draw(canvas);
            }
            canvas.drawBitmap(this.emojiThemeIcon, 0.0f, 0.0f, (Paint) null);
            canvas.setBitmap((Bitmap) null);
            this.emojiThemeDarkIcons.put(theme.emoji, bitmap);
        }
        return bitmap;
    }

    private void onPatternLoaded(Bitmap bitmap, int intensity, boolean withAnimation) {
        if (bitmap != null) {
            this.currMotionDrawable.setPatternBitmap(intensity, bitmap);
            ValueAnimator valueAnimator = this.patternIntensityAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            if (withAnimation) {
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

    /* renamed from: lambda$onPatternLoaded$4$org-telegram-ui-QrActivity  reason: not valid java name */
    public /* synthetic */ void m3203lambda$onPatternLoaded$4$orgtelegramuiQrActivity(ValueAnimator animator) {
        this.currMotionDrawable.setPatternAlpha(((Float) animator.getAnimatedValue()).floatValue());
    }

    /* access modifiers changed from: protected */
    public AnimatorSet onCustomTransitionAnimation(boolean isOpen, final Runnable callback) {
        float f = 0.0f;
        if (isOpen) {
            this.fragmentView.setAlpha(0.0f);
            this.fragmentView.setTranslationX((float) AndroidUtilities.dp(48.0f));
        }
        AnimatorSet animator = new AnimatorSet();
        Animator[] animatorArr = new Animator[2];
        View view = this.fragmentView;
        Property property = View.TRANSLATION_X;
        float[] fArr = new float[1];
        fArr[0] = isOpen ? 0.0f : (float) AndroidUtilities.dp(48.0f);
        animatorArr[0] = ObjectAnimator.ofFloat(view, property, fArr);
        View view2 = this.fragmentView;
        Property property2 = View.ALPHA;
        float[] fArr2 = new float[1];
        if (isOpen) {
            f = 1.0f;
        }
        fArr2[0] = f;
        animatorArr[1] = ObjectAnimator.ofFloat(view2, property2, fArr2);
        animator.playTogether(animatorArr);
        if (!isOpen) {
            animator.setInterpolator(new DecelerateInterpolator(1.5f));
        } else {
            animator.setInterpolator(CubicBezierInterpolator.EASE_IN);
        }
        animator.setDuration(isOpen ? 200 : 150);
        animator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                Runnable runnable = callback;
                if (runnable != null) {
                    runnable.run();
                }
            }
        });
        animator.start();
        return animator;
    }

    /* access modifiers changed from: private */
    public void onItemSelected(EmojiThemes newTheme, int position, boolean withAnimation) {
        EmojiThemes emojiThemes = newTheme;
        this.selectedPosition = position;
        EmojiThemes prevTheme = this.currentTheme;
        boolean isDarkTheme = this.isCurrentThemeDark;
        this.currentTheme = emojiThemes;
        EmojiThemes.ThemeItem themeItem = emojiThemes.getThemeItem(isDarkTheme);
        float duration = 1.0f;
        ValueAnimator valueAnimator = this.patternAlphaAnimator;
        if (valueAnimator != null) {
            duration = 1.0f * Math.max(0.5f, 1.0f - ((Float) valueAnimator.getAnimatedValue()).floatValue());
            this.patternAlphaAnimator.cancel();
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
        TLRPC.WallPaper wallPaper = this.currentTheme.getWallpaper(isDarkTheme);
        if (wallPaper != null) {
            this.currMotionDrawable.setPatternBitmap(wallPaper.settings.intensity);
            this.currentTheme.loadWallpaper(isDarkTheme, new QrActivity$$ExternalSyntheticLambda6(this, isDarkTheme, SystemClock.elapsedRealtime()));
        } else {
            this.currMotionDrawable.setPatternBitmap(34, SvgHelper.getBitmap(NUM, this.backgroundView.getWidth(), this.backgroundView.getHeight(), -16777216));
        }
        MotionBackgroundDrawable motionBackgroundDrawable4 = this.currMotionDrawable;
        motionBackgroundDrawable4.setPatternColorFilter(motionBackgroundDrawable4.getPatternColor());
        ArrayMap<String, int[]> arrayMap = qrColorsMap;
        StringBuilder sb = new StringBuilder();
        sb.append(emojiThemes.emoji);
        sb.append(isDarkTheme ? "n" : "d");
        final int[] newQrColors = arrayMap.get(sb.toString());
        if (withAnimation) {
            this.currMotionDrawable.setAlpha(255);
            this.currMotionDrawable.setBackgroundAlpha(0.0f);
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            this.patternAlphaAnimator = ofFloat;
            ofFloat.addUpdateListener(new QrActivity$$ExternalSyntheticLambda1(this, newQrColors));
            this.patternAlphaAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    int[] iArr = newQrColors;
                    if (iArr != null) {
                        System.arraycopy(iArr, 0, QrActivity.this.prevQrColors, 0, 4);
                    }
                    MotionBackgroundDrawable unused = QrActivity.this.prevMotionDrawable = null;
                    ValueAnimator unused2 = QrActivity.this.patternAlphaAnimator = null;
                    QrActivity.this.currMotionDrawable.setBackgroundAlpha(1.0f);
                    QrActivity.this.currMotionDrawable.setPatternAlpha(1.0f);
                }

                public void onAnimationCancel(Animator animation) {
                    super.onAnimationCancel(animation);
                    float progress = ((Float) ((ValueAnimator) animation).getAnimatedValue()).floatValue();
                    if (newQrColors != null) {
                        System.arraycopy(new int[]{ColorUtils.blendARGB(QrActivity.this.prevQrColors[0], newQrColors[0], progress), ColorUtils.blendARGB(QrActivity.this.prevQrColors[1], newQrColors[1], progress), ColorUtils.blendARGB(QrActivity.this.prevQrColors[2], newQrColors[2], progress), ColorUtils.blendARGB(QrActivity.this.prevQrColors[3], newQrColors[3], progress)}, 0, QrActivity.this.prevQrColors, 0, 4);
                    }
                }
            });
            this.patternAlphaAnimator.setDuration((long) ((int) (duration * 250.0f)));
            this.patternAlphaAnimator.start();
        } else {
            if (newQrColors != null) {
                this.qrView.setColors(newQrColors[0], newQrColors[1], newQrColors[2], newQrColors[3]);
                System.arraycopy(newQrColors, 0, this.prevQrColors, 0, 4);
            }
            this.prevMotionDrawable = null;
            this.backgroundView.invalidate();
        }
        ActionBarLayout.ThemeAnimationSettings animationSettings = new ActionBarLayout.ThemeAnimationSettings((Theme.ThemeInfo) null, (this.isCurrentThemeDark ? Theme.getCurrentNightTheme() : Theme.getCurrentTheme()).currentAccentId, this.isCurrentThemeDark, !withAnimation);
        animationSettings.applyTheme = false;
        animationSettings.onlyTopFragment = true;
        animationSettings.resourcesProvider = getResourceProvider();
        animationSettings.duration = (long) ((int) (duration * 250.0f));
        if (withAnimation) {
            this.resourcesProvider.initColors(prevTheme, this.isCurrentThemeDark);
        } else {
            this.resourcesProvider.initColors(this.currentTheme, this.isCurrentThemeDark);
        }
        animationSettings.afterStartDescriptionsAddedRunnable = new QrActivity$$ExternalSyntheticLambda4(this);
        this.parentLayout.animateThemedValues(animationSettings);
    }

    /* renamed from: lambda$onItemSelected$5$org-telegram-ui-QrActivity  reason: not valid java name */
    public /* synthetic */ void m3200lambda$onItemSelected$5$orgtelegramuiQrActivity(boolean isDarkTheme, long startedLoading, Pair pair) {
        if (pair != null && this.currentTheme.getTlTheme(isDarkTheme) != null) {
            long themeId = ((Long) pair.first).longValue();
            Bitmap bitmap = (Bitmap) pair.second;
            if (themeId == this.currentTheme.getTlTheme(isDarkTheme).id && bitmap != null) {
                onPatternLoaded(bitmap, this.currMotionDrawable.getIntensity(), SystemClock.elapsedRealtime() - startedLoading > 150);
            }
        }
    }

    /* renamed from: lambda$onItemSelected$6$org-telegram-ui-QrActivity  reason: not valid java name */
    public /* synthetic */ void m3201lambda$onItemSelected$6$orgtelegramuiQrActivity(int[] newQrColors, ValueAnimator animation) {
        float progress = ((Float) animation.getAnimatedValue()).floatValue();
        MotionBackgroundDrawable motionBackgroundDrawable = this.prevMotionDrawable;
        if (motionBackgroundDrawable != null) {
            motionBackgroundDrawable.setBackgroundAlpha(1.0f);
            this.prevMotionDrawable.setPatternAlpha(1.0f - progress);
        }
        this.currMotionDrawable.setBackgroundAlpha(progress);
        this.currMotionDrawable.setPatternAlpha(progress);
        if (newQrColors != null) {
            this.qrView.setColors(ColorUtils.blendARGB(this.prevQrColors[0], newQrColors[0], progress), ColorUtils.blendARGB(this.prevQrColors[1], newQrColors[1], progress), ColorUtils.blendARGB(this.prevQrColors[2], newQrColors[2], progress), ColorUtils.blendARGB(this.prevQrColors[3], newQrColors[3], progress));
        }
        this.backgroundView.invalidate();
    }

    /* renamed from: lambda$onItemSelected$7$org-telegram-ui-QrActivity  reason: not valid java name */
    public /* synthetic */ void m3202lambda$onItemSelected$7$orgtelegramuiQrActivity() {
        this.resourcesProvider.initColors(this.currentTheme, this.isCurrentThemeDark);
    }

    private void performShare() {
        int width = Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y);
        int height = Math.max(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y);
        if ((((float) height) * 1.0f) / ((float) width) > 1.92f) {
            height = (int) (((float) width) * 1.92f);
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        this.themeLayout.setVisibility(8);
        this.closeImageView.setVisibility(8);
        this.logoImageView.stopAnimation();
        RLottieDrawable drawable = this.logoImageView.getAnimatedDrawable();
        int currentFrame = drawable.getCurrentFrame();
        drawable.setCurrentFrame(33, false);
        this.fragmentView.measure(View.MeasureSpec.makeMeasureSpec(width, NUM), View.MeasureSpec.makeMeasureSpec(height, NUM));
        this.fragmentView.layout(0, 0, width, height);
        this.fragmentView.draw(canvas);
        canvas.setBitmap((Bitmap) null);
        this.themeLayout.setVisibility(0);
        this.closeImageView.setVisibility(0);
        drawable.setCurrentFrame(currentFrame, false);
        this.logoImageView.playAnimation();
        ViewGroup parent = (ViewGroup) this.fragmentView.getParent();
        this.fragmentView.layout(0, 0, parent.getWidth(), parent.getHeight());
        Uri uri = AndroidUtilities.getBitmapShareUri(bitmap, "qr_tmp.jpg", Bitmap.CompressFormat.JPEG);
        if (uri != null) {
            try {
                getParentActivity().startActivityForResult(Intent.createChooser(new Intent("android.intent.action.SEND").setType("image/*").putExtra("android.intent.extra.STREAM", uri), LocaleController.getString("InviteByQRCode", NUM)), 500);
            } catch (ActivityNotFoundException ex) {
                ex.printStackTrace();
            }
        }
        AndroidUtilities.runOnUIThread(new QrActivity$$ExternalSyntheticLambda5(this), 500);
    }

    /* renamed from: lambda$performShare$8$org-telegram-ui-QrActivity  reason: not valid java name */
    public /* synthetic */ void m3204lambda$performShare$8$orgtelegramuiQrActivity() {
        this.themesViewController.shareButton.setClickable(true);
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = super.getThemeDescriptions();
        themeDescriptions.addAll(this.themesViewController.getThemeDescriptions());
        themeDescriptions.add(new ThemeDescription(this.themesViewController.shareButton, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, new QrActivity$$ExternalSyntheticLambda7(this), "featuredStickers_addButton"));
        themeDescriptions.add(new ThemeDescription(this.themesViewController.shareButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "featuredStickers_addButtonPressed"));
        Iterator<ThemeDescription> it = themeDescriptions.iterator();
        while (it.hasNext()) {
            it.next().resourcesProvider = getResourceProvider();
        }
        return themeDescriptions;
    }

    /* renamed from: lambda$getThemeDescriptions$9$org-telegram-ui-QrActivity  reason: not valid java name */
    public /* synthetic */ void m3199lambda$getThemeDescriptions$9$orgtelegramuiQrActivity() {
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
        public void initColors(EmojiThemes theme, boolean isDark) {
            this.colors = theme.createColors(QrActivity.this.currentAccount, isDark);
        }

        public Integer getColor(String key) {
            HashMap<String, Integer> hashMap = this.colors;
            if (hashMap != null) {
                return hashMap.get(key);
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
            BitmapShader bitmapShader = new BitmapShader(motionBackgroundDrawable.getBitmap(), Shader.TileMode.MIRROR, Shader.TileMode.MIRROR);
            this.gradientShader = bitmapShader;
            paint.setShader(bitmapShader);
        }

        /* access modifiers changed from: protected */
        public void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            if (w != oldw || h != oldh) {
                Bitmap bitmap = this.backgroundBitmap;
                if (bitmap != null) {
                    bitmap.recycle();
                    this.backgroundBitmap = null;
                }
                Paint backgroundPaint = new Paint(1);
                backgroundPaint.setColor(-1);
                float f = SHADOW_SIZE;
                backgroundPaint.setShadowLayer((float) AndroidUtilities.dp(4.0f), 0.0f, f, NUM);
                this.backgroundBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(this.backgroundBitmap);
                RectF rect = new RectF(f, f, ((float) w) - f, ((float) getHeight()) - f);
                float f2 = RADIUS;
                canvas.drawRoundRect(rect, f2, f2, backgroundPaint);
                prepareContent(w, h);
                float maxScale = Math.max((((float) getWidth()) * 1.0f) / ((float) this.gradientDrawable.getBitmap().getWidth()), (((float) getHeight()) * 1.0f) / ((float) this.gradientDrawable.getBitmap().getHeight()));
                Matrix matrix = new Matrix();
                matrix.setScale(maxScale, maxScale);
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
        public void setCenterChangedListener(QrCenterChangedListener centerChangedListener2) {
            this.centerChangedListener = centerChangedListener2;
        }

        /* access modifiers changed from: package-private */
        public void setData(String link2, String username2) {
            this.username = username2;
            this.link = link2;
            prepareContent(getWidth(), getHeight());
            invalidate();
        }

        /* access modifiers changed from: package-private */
        public void setColors(int c1, int c2, int c3, int c4) {
            this.gradientDrawable.setColors(c1, c2, c3, c4);
            invalidate();
        }

        /* access modifiers changed from: package-private */
        public void setPosAnimationProgress(float progress) {
            this.gradientDrawable.posAnimationProgress = progress;
        }

        private void prepareContent(int w, int h) {
            StaticLayout staticLayout;
            boolean z;
            Bitmap qrBitmap;
            int imageSize;
            TextPaint textPaint;
            int textMaxWidth;
            HashMap<EncodeHintType, Object> hints;
            int version;
            int attemptsCount;
            Drawable drawable;
            SpannableStringBuilder string;
            float textWidth;
            int layoutWidth;
            int linesCount;
            int i = w;
            int i2 = h;
            if (!TextUtils.isEmpty(this.username) && !TextUtils.isEmpty(this.link) && i != 0 && i2 != 0) {
                Bitmap bitmap = this.contentBitmap;
                if (bitmap != null) {
                    bitmap.recycle();
                }
                this.contentBitmap = Bitmap.createBitmap(i, i2, Bitmap.Config.ARGB_8888);
                TextPaint textPaint2 = new TextPaint(65);
                textPaint2.setColor(-16777216);
                textPaint2.setTypeface(AndroidUtilities.getTypeface("fonts/rcondensedbold.ttf"));
                int attemptsCount2 = 2;
                int textMaxWidth2 = this.contentBitmap.getWidth() - (AndroidUtilities.dp(20.0f) * 2);
                int i3 = 0;
                while (true) {
                    if (i3 > 2) {
                        staticLayout = null;
                        break;
                    }
                    if (i3 == 0) {
                        drawable = ContextCompat.getDrawable(getContext(), NUM);
                        textPaint2.setTextSize((float) AndroidUtilities.dp(30.0f));
                    } else if (i3 == 1) {
                        drawable = ContextCompat.getDrawable(getContext(), NUM);
                        textPaint2.setTextSize((float) AndroidUtilities.dp(25.0f));
                    } else {
                        drawable = ContextCompat.getDrawable(getContext(), NUM);
                        textPaint2.setTextSize((float) AndroidUtilities.dp(19.0f));
                    }
                    if (drawable != null) {
                        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                        drawable.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.SRC_IN));
                    }
                    string = new SpannableStringBuilder(" " + this.username.toUpperCase());
                    string.setSpan(new SettingsSearchCell.VerticalImageSpan(drawable), 0, 1, 33);
                    textWidth = textPaint2.measureText(string, 1, string.length()) + ((float) drawable.getBounds().width());
                    if (i3 <= 1 && textWidth > ((float) textMaxWidth2)) {
                        i3++;
                    }
                }
                int linesCount2 = textWidth > ((float) textMaxWidth2) ? 2 : 1;
                int layoutWidth2 = textMaxWidth2;
                if (linesCount2 > 1) {
                    layoutWidth = (((int) (((float) drawable.getBounds().width()) + textWidth)) / 2) + AndroidUtilities.dp(2.0f);
                } else {
                    layoutWidth = layoutWidth2;
                }
                if (layoutWidth > textMaxWidth2) {
                    layoutWidth = (((int) (((float) drawable.getBounds().width()) + textWidth)) / 3) + AndroidUtilities.dp(4.0f);
                    linesCount = 3;
                } else {
                    linesCount = linesCount2;
                }
                staticLayout = StaticLayoutEx.createStaticLayout(string, textPaint2, layoutWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false, (TextUtils.TruncateAt) null, Math.min(AndroidUtilities.dp(10.0f) + layoutWidth, this.contentBitmap.getWidth()), linesCount);
                float textHeight = (textPaint2.descent() - textPaint2.ascent()) * ((float) staticLayout.getLineCount());
                int qrBitmapSize = i - (AndroidUtilities.dp(30.0f) * 2);
                HashMap<EncodeHintType, Object> hints2 = new HashMap<>();
                hints2.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
                hints2.put(EncodeHintType.MARGIN, 0);
                QRCodeWriter writer = new QRCodeWriter();
                Bitmap qrBitmap2 = null;
                int imageSize2 = 0;
                int version2 = 3;
                while (true) {
                    if (version2 >= 5) {
                        int i4 = version2;
                        HashMap<EncodeHintType, Object> hashMap = hints2;
                        int i5 = textMaxWidth2;
                        int i6 = attemptsCount2;
                        TextPaint textPaint3 = textPaint2;
                        z = false;
                        qrBitmap = qrBitmap2;
                        imageSize = imageSize2;
                        break;
                    }
                    try {
                        hints2.put(EncodeHintType.QR_VERSION, Integer.valueOf(version2));
                        version = version2;
                        z = false;
                        hints = hints2;
                        textMaxWidth = textMaxWidth2;
                        attemptsCount = attemptsCount2;
                        textPaint = textPaint2;
                        try {
                            qrBitmap2 = writer.encode(this.link, qrBitmapSize, qrBitmapSize, hints2, (Bitmap) null, 0.75f, 16777215, -16777216);
                            imageSize2 = writer.getImageSize();
                        } catch (Exception e) {
                        }
                    } catch (Exception e2) {
                        version = version2;
                        hints = hints2;
                        textMaxWidth = textMaxWidth2;
                        attemptsCount = attemptsCount2;
                        textPaint = textPaint2;
                        z = false;
                    }
                    if (qrBitmap2 != null) {
                        qrBitmap = qrBitmap2;
                        imageSize = imageSize2;
                        break;
                    }
                    version2 = version + 1;
                    attemptsCount2 = attemptsCount;
                    hints2 = hints;
                    textMaxWidth2 = textMaxWidth;
                    textPaint2 = textPaint;
                }
                if (qrBitmap != null) {
                    Canvas canvas = new Canvas(this.contentBitmap);
                    canvas.drawColor(16777215);
                    float left = ((float) (i - qrBitmap.getWidth())) / 2.0f;
                    float qrTop = ((float) i2) * 0.15f;
                    if (staticLayout.getLineCount() == 3) {
                        qrTop = ((float) i2) * 0.13f;
                    }
                    if (((ViewGroup) getParent()).getMeasuredWidth() < ((ViewGroup) getParent()).getMeasuredHeight()) {
                        z = true;
                    }
                    boolean isPortrait = z;
                    if (!isPortrait) {
                        qrTop = ((float) i2) * 0.09f;
                    }
                    canvas.drawBitmap(qrBitmap, left, qrTop, new Paint(3));
                    Paint circlePaint = new Paint(1);
                    circlePaint.setColor(-16777216);
                    float xCenter = (((float) qrBitmap.getWidth()) * 0.5f) + left;
                    float yCenter = (((float) qrBitmap.getWidth()) * 0.5f) + qrTop;
                    canvas.drawCircle(xCenter, yCenter, ((float) imageSize) * 0.5f, circlePaint);
                    QrCenterChangedListener qrCenterChangedListener = this.centerChangedListener;
                    if (qrCenterChangedListener != null) {
                        QRCodeWriter qRCodeWriter = writer;
                        float f = left;
                        boolean z2 = isPortrait;
                        Paint paint = circlePaint;
                        qrCenterChangedListener.onCenterChanged((int) (xCenter - (((float) imageSize) * 0.5f)), (int) (yCenter - (((float) imageSize) * 0.5f)), (int) ((((float) imageSize) * 0.5f) + xCenter), (int) ((((float) imageSize) * 0.5f) + yCenter));
                    } else {
                        float f2 = left;
                        boolean z3 = isPortrait;
                        Paint paint2 = circlePaint;
                    }
                    canvas.save();
                    canvas.translate(((float) (canvas.getWidth() - staticLayout.getWidth())) * 0.5f, ((((float) qrBitmap.getHeight()) + qrTop) + (((((float) canvas.getHeight()) - (((float) qrBitmap.getHeight()) + qrTop)) - textHeight) * 0.5f)) - ((float) AndroidUtilities.dp(4.0f)));
                    staticLayout.draw(canvas);
                    canvas.restore();
                    qrBitmap.recycle();
                    Bitmap oldBitmap = this.contentBitmap;
                    this.contentBitmap = this.contentBitmap.extractAlpha();
                    oldBitmap.recycle();
                }
            }
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

        public ThemeListViewController(QrActivity qrActivity, BaseFragment fragment2, Window window2) {
            final QrActivity qrActivity2 = qrActivity;
            BaseFragment baseFragment = fragment2;
            this.this$0 = qrActivity2;
            this.fragment = baseFragment;
            this.window = window2;
            Context context = fragment2.getParentActivity();
            this.scroller = new LinearSmoothScroller(context) {
                /* access modifiers changed from: protected */
                public int calculateTimeForScrolling(int dx) {
                    return super.calculateTimeForScrolling(dx) * 6;
                }
            };
            Drawable mutate = context.getResources().getDrawable(NUM).mutate();
            this.backgroundDrawable = mutate;
            mutate.setColorFilter(new PorterDuffColorFilter(baseFragment.getThemedColor("dialogBackground"), PorterDuff.Mode.MULTIPLY));
            AnonymousClass2 r6 = new FrameLayout(context, qrActivity2, baseFragment) {
                private final Rect backgroundPadding;
                final /* synthetic */ BaseFragment val$fragment;
                final /* synthetic */ QrActivity val$this$0;

                {
                    this.val$this$0 = r5;
                    this.val$fragment = r6;
                    Rect rect = new Rect();
                    this.backgroundPadding = rect;
                    ThemeListViewController.this.backgroundPaint.setColor(r6.getThemedColor("windowBackgroundWhite"));
                    ThemeListViewController.this.backgroundDrawable.setCallback(this);
                    ThemeListViewController.this.backgroundDrawable.getPadding(rect);
                    setPadding(0, rect.top + AndroidUtilities.dp(8.0f), 0, rect.bottom);
                }

                /* access modifiers changed from: protected */
                public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    boolean isPortrait = AndroidUtilities.displaySize.x < AndroidUtilities.displaySize.y;
                    int recyclerPadding = AndroidUtilities.dp(12.0f);
                    if (isPortrait) {
                        ThemeListViewController.this.recyclerView.setLayoutParams(LayoutHelper.createFrame(-1, 104.0f, 8388611, 0.0f, 44.0f, 0.0f, 0.0f));
                        ThemeListViewController.this.recyclerView.setPadding(recyclerPadding, 0, recyclerPadding, 0);
                        ThemeListViewController.this.shareButton.setLayoutParams(LayoutHelper.createFrame(-1, 48.0f, 8388611, 16.0f, 162.0f, 16.0f, 16.0f));
                    } else {
                        ThemeListViewController.this.recyclerView.setLayoutParams(LayoutHelper.createFrame(-1, -1.0f, 8388611, 0.0f, 44.0f, 0.0f, 80.0f));
                        ThemeListViewController.this.recyclerView.setPadding(recyclerPadding, recyclerPadding / 2, recyclerPadding, recyclerPadding);
                        ThemeListViewController.this.shareButton.setLayoutParams(LayoutHelper.createFrame(-1, 48.0f, 80, 16.0f, 0.0f, 16.0f, 16.0f));
                    }
                    if (isPortrait) {
                        ThemeListViewController.this.bottomShadow.setVisibility(8);
                        ThemeListViewController.this.topShadow.setVisibility(8);
                    } else {
                        ThemeListViewController.this.bottomShadow.setVisibility(0);
                        ThemeListViewController.this.bottomShadow.setLayoutParams(LayoutHelper.createFrame(-1, (float) AndroidUtilities.dp(2.0f), 80, 0.0f, 0.0f, 0.0f, 80.0f));
                        ThemeListViewController.this.topShadow.setVisibility(0);
                        ThemeListViewController.this.topShadow.setLayoutParams(LayoutHelper.createFrame(-1, (float) AndroidUtilities.dp(2.0f), 48, 0.0f, 44.0f, 0.0f, 0.0f));
                    }
                    if (ThemeListViewController.this.prevIsPortrait != isPortrait) {
                        RecyclerListView access$2100 = ThemeListViewController.this.recyclerView;
                        ThemeListViewController themeListViewController = ThemeListViewController.this;
                        access$2100.setLayoutManager(themeListViewController.layoutManager = themeListViewController.getLayoutManager(isPortrait));
                        ThemeListViewController.this.recyclerView.requestLayout();
                        if (ThemeListViewController.this.prevSelectedPosition != -1) {
                            ThemeListViewController themeListViewController2 = ThemeListViewController.this;
                            themeListViewController2.setSelectedPosition(themeListViewController2.prevSelectedPosition);
                        }
                        boolean unused = ThemeListViewController.this.prevIsPortrait = isPortrait;
                    }
                    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                }

                /* access modifiers changed from: protected */
                public void dispatchDraw(Canvas canvas) {
                    if (ThemeListViewController.this.prevIsPortrait) {
                        ThemeListViewController.this.backgroundDrawable.setBounds(-this.backgroundPadding.left, 0, getWidth() + this.backgroundPadding.right, getHeight());
                        ThemeListViewController.this.backgroundDrawable.draw(canvas);
                    } else {
                        AndroidUtilities.rectTmp.set(0.0f, 0.0f, (float) (getWidth() + AndroidUtilities.dp(14.0f)), (float) getHeight());
                        canvas.drawRoundRect(AndroidUtilities.rectTmp, (float) AndroidUtilities.dp(14.0f), (float) AndroidUtilities.dp(14.0f), ThemeListViewController.this.backgroundPaint);
                    }
                    super.dispatchDraw(canvas);
                }

                /* access modifiers changed from: protected */
                public boolean verifyDrawable(Drawable who) {
                    return who == ThemeListViewController.this.backgroundDrawable || super.verifyDrawable(who);
                }
            };
            this.rootLayout = r6;
            TextView textView = new TextView(context);
            this.titleView = textView;
            textView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
            textView.setLines(1);
            textView.setSingleLine(true);
            textView.setTextColor(baseFragment.getThemedColor("dialogTextBlack"));
            textView.setTextSize(1, 20.0f);
            textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            textView.setPadding(AndroidUtilities.dp(21.0f), AndroidUtilities.dp(6.0f), AndroidUtilities.dp(21.0f), AndroidUtilities.dp(8.0f));
            r6.addView(textView, LayoutHelper.createFrame(-1, -2.0f, 8388659, 0.0f, 0.0f, 62.0f, 0.0f));
            int drawableColor = baseFragment.getThemedColor("featuredStickers_addButton");
            int drawableSize = AndroidUtilities.dp(28.0f);
            RLottieDrawable rLottieDrawable = r12;
            RLottieDrawable rLottieDrawable2 = new RLottieDrawable(NUM, "NUM", drawableSize, drawableSize, false, (int[]) null);
            this.darkThemeDrawable = rLottieDrawable;
            this.forceDark = !Theme.getActiveTheme().isDark();
            setForceDark(Theme.getActiveTheme().isDark(), false);
            rLottieDrawable.setPlayInDirectionOfCustomEndFrame(true);
            rLottieDrawable.setColorFilter(new PorterDuffColorFilter(drawableColor, PorterDuff.Mode.MULTIPLY));
            RLottieImageView rLottieImageView = new RLottieImageView(context);
            this.darkThemeView = rLottieImageView;
            rLottieImageView.setAnimation(rLottieDrawable);
            rLottieImageView.setScaleType(ImageView.ScaleType.CENTER);
            rLottieImageView.setOnClickListener(new QrActivity$ThemeListViewController$$ExternalSyntheticLambda1(this));
            rLottieImageView.setAlpha(0.0f);
            rLottieImageView.setVisibility(4);
            r6.addView(rLottieImageView, LayoutHelper.createFrame(44, 44.0f, 8388661, 0.0f, -2.0f, 7.0f, 0.0f));
            FlickerLoadingView flickerLoadingView = new FlickerLoadingView(context, fragment2.getResourceProvider());
            this.progressView = flickerLoadingView;
            flickerLoadingView.setVisibility(0);
            r6.addView(flickerLoadingView, LayoutHelper.createFrame(-1, 104.0f, 8388611, 0.0f, 44.0f, 0.0f, 0.0f));
            this.prevIsPortrait = AndroidUtilities.displaySize.x < AndroidUtilities.displaySize.y;
            RecyclerListView recyclerListView = new RecyclerListView(context);
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
            recyclerListView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                private int yScroll = 0;

                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    this.yScroll += dy;
                    ThemeListViewController.this.topShadow.setAlpha((((float) this.yScroll) * 1.0f) / ((float) AndroidUtilities.dp(6.0f)));
                }
            });
            r6.addView(recyclerListView);
            View view = new View(context);
            this.topShadow = view;
            view.setAlpha(0.0f);
            view.setBackground(ContextCompat.getDrawable(context, NUM));
            view.setRotation(180.0f);
            r6.addView(view);
            View view2 = new View(context);
            this.bottomShadow = view2;
            view2.setBackground(ContextCompat.getDrawable(context, NUM));
            r6.addView(view2);
            TextView textView2 = new TextView(context);
            this.shareButton = textView2;
            textView2.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(6.0f), baseFragment.getThemedColor("featuredStickers_addButton"), baseFragment.getThemedColor("featuredStickers_addButtonPressed")));
            textView2.setEllipsize(TextUtils.TruncateAt.END);
            textView2.setGravity(17);
            textView2.setLines(1);
            textView2.setSingleLine(true);
            textView2.setText(LocaleController.getString("ShareQrCode", NUM));
            textView2.setTextColor(baseFragment.getThemedColor("featuredStickers_buttonText"));
            textView2.setTextSize(1, 15.0f);
            textView2.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            r6.addView(textView2);
        }

        /* renamed from: lambda$new$0$org-telegram-ui-QrActivity$ThemeListViewController  reason: not valid java name */
        public /* synthetic */ void m3205lambda$new$0$orgtelegramuiQrActivity$ThemeListViewController(View view) {
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

        public void didReceivedNotification(int id, int account, Object... args) {
            if (id == NotificationCenter.emojiLoaded) {
                this.adapter.notifyDataSetChanged();
            }
        }

        public void onDestroy() {
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
        }

        public void setItemSelectedListener(OnItemSelectedListener itemSelectedListener2) {
            this.itemSelectedListener = itemSelectedListener2;
        }

        public void onDataLoaded() {
            this.darkThemeView.setAlpha(0.0f);
            this.darkThemeView.animate().alpha(1.0f).setDuration(150).start();
            this.darkThemeView.setVisibility(0);
            this.progressView.animate().alpha(0.0f).setListener(new HideViewAfterAnimation(this.progressView)).setDuration(150).start();
            this.recyclerView.setAlpha(0.0f);
            this.recyclerView.animate().alpha(1.0f).setDuration(150).start();
        }

        public void setSelectedPosition(int selectedPosition) {
            this.prevSelectedPosition = selectedPosition;
            this.adapter.setSelectedItem(selectedPosition);
            if (selectedPosition > 0 && selectedPosition < this.adapter.items.size() / 2) {
                selectedPosition--;
            }
            this.layoutManager.scrollToPositionWithOffset(Math.min(selectedPosition, this.adapter.items.size() - 1), 0);
        }

        /* access modifiers changed from: protected */
        public void onItemClicked(View view, int position) {
            if (this.adapter.items.get(position) != this.selectedItem && this.changeDayNightView == null) {
                this.isLightDarkChangeAnimation = false;
                this.selectedItem = this.adapter.items.get(position);
                this.adapter.setSelectedItem(position);
                this.rootLayout.postDelayed(new QrActivity$ThemeListViewController$$ExternalSyntheticLambda2(this, position), 100);
                for (int i = 0; i < this.recyclerView.getChildCount(); i++) {
                    ThemeSmallPreviewView child = (ThemeSmallPreviewView) this.recyclerView.getChildAt(i);
                    if (child != view) {
                        child.cancelAnimation();
                    }
                }
                if (!this.adapter.items.get(position).chatTheme.showAsDefaultStub) {
                    ((ThemeSmallPreviewView) view).playEmojiAnimation();
                }
                OnItemSelectedListener onItemSelectedListener = this.itemSelectedListener;
                if (onItemSelectedListener != null) {
                    onItemSelectedListener.onItemSelected(this.selectedItem.chatTheme, position);
                }
            }
        }

        /* renamed from: lambda$onItemClicked$1$org-telegram-ui-QrActivity$ThemeListViewController  reason: not valid java name */
        public /* synthetic */ void m3206xb3111e29(int position) {
            int targetPosition;
            RecyclerView.LayoutManager layoutManager2 = this.recyclerView.getLayoutManager();
            if (layoutManager2 != null) {
                if (position > this.prevSelectedPosition) {
                    targetPosition = Math.min(position + 1, this.adapter.items.size() - 1);
                } else {
                    targetPosition = Math.max(position - 1, 0);
                }
                this.scroller.setTargetPosition(targetPosition);
                layoutManager2.startSmoothScroll(this.scroller);
            }
            this.prevSelectedPosition = position;
        }

        private void setupLightDarkTheme(boolean isDark) {
            ValueAnimator valueAnimator = this.changeDayNightViewAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            FrameLayout decorView1 = (FrameLayout) this.fragment.getParentActivity().getWindow().getDecorView();
            FrameLayout decorView2 = (FrameLayout) this.window.getDecorView();
            Bitmap bitmap = Bitmap.createBitmap(decorView2.getWidth(), decorView2.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas bitmapCanvas = new Canvas(bitmap);
            this.darkThemeView.setAlpha(0.0f);
            decorView1.draw(bitmapCanvas);
            decorView2.draw(bitmapCanvas);
            this.darkThemeView.setAlpha(1.0f);
            Paint xRefPaint = new Paint(1);
            xRefPaint.setColor(-16777216);
            xRefPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            Paint bitmapPaint = new Paint(1);
            bitmapPaint.setFilterBitmap(true);
            int[] position = new int[2];
            this.darkThemeView.getLocationInWindow(position);
            float x = (float) position[0];
            float y = (float) position[1];
            float cx = x + (((float) this.darkThemeView.getMeasuredWidth()) / 2.0f);
            float cy = y + (((float) this.darkThemeView.getMeasuredHeight()) / 2.0f);
            float r = ((float) Math.max(bitmap.getHeight(), bitmap.getWidth())) * 0.9f;
            BitmapShader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            bitmapPaint.setShader(bitmapShader);
            FrameLayout frameLayout = decorView1;
            AnonymousClass4 r14 = r0;
            BitmapShader bitmapShader2 = bitmapShader;
            final boolean z = isDark;
            float y2 = y;
            final Canvas canvas = bitmapCanvas;
            float x2 = x;
            final float x3 = cx;
            int[] iArr = position;
            final float f = cy;
            final float f2 = r;
            Paint bitmapPaint2 = bitmapPaint;
            final Paint bitmapPaint3 = xRefPaint;
            Paint paint = xRefPaint;
            final Bitmap bitmap2 = bitmap;
            final Paint paint2 = bitmapPaint2;
            Canvas canvas2 = bitmapCanvas;
            final float f3 = x2;
            Bitmap bitmap3 = bitmap;
            final float f4 = y2;
            AnonymousClass4 r0 = new View(this.fragment.getParentActivity()) {
                /* access modifiers changed from: protected */
                public void onDraw(Canvas canvas) {
                    super.onDraw(canvas);
                    if (z) {
                        if (ThemeListViewController.this.changeDayNightViewProgress > 0.0f) {
                            canvas.drawCircle(x3, f, f2 * ThemeListViewController.this.changeDayNightViewProgress, bitmapPaint3);
                        }
                        canvas.drawBitmap(bitmap2, 0.0f, 0.0f, paint2);
                    } else {
                        canvas.drawCircle(x3, f, f2 * (1.0f - ThemeListViewController.this.changeDayNightViewProgress), paint2);
                    }
                    canvas.save();
                    canvas.translate(f3, f4);
                    ThemeListViewController.this.darkThemeView.draw(canvas);
                    canvas.restore();
                }
            };
            this.changeDayNightView = r14;
            this.changeDayNightViewProgress = 0.0f;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            this.changeDayNightViewAnimator = ofFloat;
            ofFloat.addUpdateListener(new QrActivity$ThemeListViewController$$ExternalSyntheticLambda0(this));
            this.changeDayNightViewAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (ThemeListViewController.this.changeDayNightView != null) {
                        if (ThemeListViewController.this.changeDayNightView.getParent() != null) {
                            ((ViewGroup) ThemeListViewController.this.changeDayNightView.getParent()).removeView(ThemeListViewController.this.changeDayNightView);
                        }
                        View unused = ThemeListViewController.this.changeDayNightView = null;
                    }
                    ValueAnimator unused2 = ThemeListViewController.this.changeDayNightViewAnimator = null;
                    super.onAnimationEnd(animation);
                }
            });
            this.changeDayNightViewAnimator.setDuration(400);
            this.changeDayNightViewAnimator.setInterpolator(Easings.easeInOutQuad);
            this.changeDayNightViewAnimator.start();
            decorView2.addView(this.changeDayNightView, new ViewGroup.LayoutParams(-1, -1));
            AndroidUtilities.runOnUIThread(new QrActivity$ThemeListViewController$$ExternalSyntheticLambda3(this, isDark));
        }

        /* renamed from: lambda$setupLightDarkTheme$2$org-telegram-ui-QrActivity$ThemeListViewController  reason: not valid java name */
        public /* synthetic */ void m3207x3var_c3(ValueAnimator valueAnimator) {
            this.changeDayNightViewProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            this.changeDayNightView.invalidate();
        }

        /* renamed from: lambda$setupLightDarkTheme$3$org-telegram-ui-QrActivity$ThemeListViewController  reason: not valid java name */
        public /* synthetic */ void m3208x6d48bd22(boolean isDark) {
            ChatThemeBottomSheet.Adapter adapter2 = this.adapter;
            if (adapter2 != null && adapter2.items != null) {
                setForceDark(isDark, true);
                if (this.selectedItem != null) {
                    this.isLightDarkChangeAnimation = true;
                    setDarkTheme(isDark);
                }
                if (this.adapter.items != null) {
                    for (int i = 0; i < this.adapter.items.size(); i++) {
                        this.adapter.items.get(i).themeIndex = isDark;
                        this.adapter.items.get(i).icon = this.this$0.getEmojiThemeIcon(this.adapter.items.get(i).chatTheme, isDark);
                    }
                    MotionBackgroundDrawable unused = this.this$0.tempMotionDrawable = null;
                    this.adapter.notifyDataSetChanged();
                }
            }
        }

        /* access modifiers changed from: protected */
        public void setDarkTheme(boolean isDark) {
        }

        public void setForceDark(boolean isDark, boolean playAnimation) {
            if (this.forceDark != isDark) {
                this.forceDark = isDark;
                int frame = isDark ? this.darkThemeDrawable.getFramesCount() - 1 : 0;
                if (playAnimation) {
                    this.darkThemeDrawable.setCustomEndFrame(frame);
                    RLottieImageView rLottieImageView = this.darkThemeView;
                    if (rLottieImageView != null) {
                        rLottieImageView.playAnimation();
                        return;
                    }
                    return;
                }
                this.darkThemeDrawable.setCustomEndFrame(frame);
                this.darkThemeDrawable.setCurrentFrame(frame, false, true);
                RLottieImageView rLottieImageView2 = this.darkThemeView;
                if (rLottieImageView2 != null) {
                    rLottieImageView2.invalidate();
                }
            }
        }

        /* access modifiers changed from: private */
        public LinearLayoutManager getLayoutManager(boolean isPortrait) {
            if (isPortrait) {
                return new LinearLayoutManager(this.fragment.getParentActivity(), 0, false);
            }
            return new GridLayoutManager(this.fragment.getParentActivity(), 3, 1, false);
        }

        /* access modifiers changed from: private */
        public void onAnimationStart() {
            ChatThemeBottomSheet.Adapter adapter2 = this.adapter;
            if (!(adapter2 == null || adapter2.items == null)) {
                for (ChatThemeBottomSheet.ChatThemeItem item : this.adapter.items) {
                    item.themeIndex = this.forceDark ? 1 : 0;
                }
            }
            if (!this.isLightDarkChangeAnimation) {
                setItemsAnimationProgress(1.0f);
            }
        }

        /* access modifiers changed from: private */
        public void setItemsAnimationProgress(float progress) {
            for (int i = 0; i < this.adapter.getItemCount(); i++) {
                this.adapter.items.get(i).animationProgress = progress;
            }
        }

        /* access modifiers changed from: private */
        public void onAnimationEnd() {
            this.isLightDarkChangeAnimation = false;
        }

        public ArrayList<ThemeDescription> getThemeDescriptions() {
            ThemeDescription.ThemeDescriptionDelegate descriptionDelegate = new ThemeDescription.ThemeDescriptionDelegate() {
                private boolean isAnimationStarted = false;

                public void onAnimationProgress(float progress) {
                    if (progress == 0.0f && !this.isAnimationStarted) {
                        ThemeListViewController.this.onAnimationStart();
                        this.isAnimationStarted = true;
                    }
                    ThemeListViewController.this.darkThemeDrawable.setColorFilter(new PorterDuffColorFilter(ThemeListViewController.this.fragment.getThemedColor("featuredStickers_addButton"), PorterDuff.Mode.MULTIPLY));
                    if (ThemeListViewController.this.isLightDarkChangeAnimation) {
                        ThemeListViewController.this.setItemsAnimationProgress(progress);
                    }
                    if (progress == 1.0f && this.isAnimationStarted) {
                        ThemeListViewController.this.isLightDarkChangeAnimation = false;
                        ThemeListViewController.this.onAnimationEnd();
                        this.isAnimationStarted = false;
                    }
                }

                public void didSetColor() {
                }
            };
            ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
            themeDescriptions.add(new ThemeDescription((View) null, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, this.backgroundPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogBackground"));
            themeDescriptions.add(new ThemeDescription((View) null, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, new Drawable[]{this.backgroundDrawable}, descriptionDelegate, "dialogBackground"));
            themeDescriptions.add(new ThemeDescription(this.titleView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextBlack"));
            themeDescriptions.add(new ThemeDescription(this.recyclerView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ThemeSmallPreviewView.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogBackgroundGray"));
            Iterator<ThemeDescription> it = themeDescriptions.iterator();
            while (it.hasNext()) {
                it.next().resourcesProvider = this.fragment.getResourceProvider();
            }
            return themeDescriptions;
        }
    }
}
