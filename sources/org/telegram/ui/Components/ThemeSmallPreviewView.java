package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.widget.FrameLayout;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatThemeController;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SvgHelper;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.EmojiThemes;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.ChatThemeBottomSheet;

public class ThemeSmallPreviewView extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    public static final int TYPE_DEFAULT = 0;
    public static final int TYPE_GRID = 1;
    public static final int TYPE_QR = 2;
    /* access modifiers changed from: private */
    public final float BUBBLE_HEIGHT = ((float) AndroidUtilities.dp(21.0f));
    /* access modifiers changed from: private */
    public final float BUBBLE_WIDTH = ((float) AndroidUtilities.dp(41.0f));
    /* access modifiers changed from: private */
    public final float INNER_RADIUS = ((float) AndroidUtilities.dp(6.0f));
    /* access modifiers changed from: private */
    public final float INNER_RECT_SPACE = ((float) AndroidUtilities.dp(4.0f));
    /* access modifiers changed from: private */
    public final float STROKE_RADIUS = ((float) AndroidUtilities.dp(8.0f));
    ThemeDrawable animateOutThemeDrawable;
    Runnable animationCancelRunnable;
    /* access modifiers changed from: private */
    public final Paint backgroundFillPaint = new Paint(1);
    private BackupImageView backupImageView;
    private float changeThemeProgress = 1.0f;
    public ChatThemeBottomSheet.ChatThemeItem chatThemeItem;
    /* access modifiers changed from: private */
    public final Path clipPath = new Path();
    private final int currentAccount;
    /* access modifiers changed from: private */
    public int currentType;
    private boolean hasAnimatedEmoji;
    boolean isSelected;
    public int lastThemeIndex;
    Theme.MessageDrawable messageDrawableIn = new Theme.MessageDrawable(0, false, false);
    Theme.MessageDrawable messageDrawableOut = new Theme.MessageDrawable(0, true, false);
    private TextPaint noThemeTextPaint;
    Paint outlineBackgroundPaint = new Paint(1);
    int patternColor;
    /* access modifiers changed from: private */
    public final RectF rectF = new RectF();
    private final Theme.ResourcesProvider resourcesProvider;
    /* access modifiers changed from: private */
    public float selectionProgress;
    /* access modifiers changed from: private */
    public ValueAnimator strokeAlphaAnimator;
    private StaticLayout textLayout;
    ThemeDrawable themeDrawable = new ThemeDrawable();

    public ThemeSmallPreviewView(Context context, int currentAccount2, Theme.ResourcesProvider resourcesProvider2, int currentType2) {
        super(context);
        this.currentType = currentType2;
        this.currentAccount = currentAccount2;
        this.resourcesProvider = resourcesProvider2;
        setBackgroundColor(getThemedColor("dialogBackgroundGray"));
        BackupImageView backupImageView2 = new BackupImageView(context);
        this.backupImageView = backupImageView2;
        backupImageView2.getImageReceiver().setCrossfadeWithOldImage(true);
        this.backupImageView.getImageReceiver().setAllowStartLottieAnimation(false);
        this.backupImageView.getImageReceiver().setAutoRepeat(0);
        if (currentType2 == 0 || currentType2 == 2) {
            addView(this.backupImageView, LayoutHelper.createFrame(28, 28.0f, 81, 0.0f, 0.0f, 0.0f, 12.0f));
        } else {
            addView(this.backupImageView, LayoutHelper.createFrame(36, 36.0f, 81, 0.0f, 0.0f, 0.0f, 12.0f));
        }
        this.outlineBackgroundPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.outlineBackgroundPaint.setStyle(Paint.Style.STROKE);
        this.outlineBackgroundPaint.setColor(-1842205);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (this.currentType == 1) {
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(width, NUM), View.MeasureSpec.makeMeasureSpec((int) (((float) width) * 1.2f), NUM));
        } else {
            int width2 = AndroidUtilities.dp(77.0f);
            int height = View.MeasureSpec.getSize(heightMeasureSpec);
            if (height == 0) {
                height = (int) (((float) width2) * 1.35f);
            }
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(width2, NUM), View.MeasureSpec.makeMeasureSpec(height, NUM));
        }
        BackupImageView backupImageView2 = this.backupImageView;
        backupImageView2.setPivotY((float) backupImageView2.getMeasuredHeight());
        BackupImageView backupImageView3 = this.backupImageView;
        backupImageView3.setPivotX(((float) backupImageView3.getMeasuredWidth()) / 2.0f);
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w != oldw || h != oldh) {
            RectF rectF2 = this.rectF;
            float f = this.INNER_RECT_SPACE;
            rectF2.set(f, f, ((float) w) - f, ((float) h) - f);
            this.clipPath.reset();
            Path path = this.clipPath;
            RectF rectF3 = this.rectF;
            float f2 = this.INNER_RADIUS;
            path.addRoundRect(rectF3, f2, f2, Path.Direction.CW);
        }
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        ThemeDrawable themeDrawable2;
        ThemeDrawable themeDrawable3;
        if (this.chatThemeItem == null) {
            super.dispatchDraw(canvas);
            return;
        }
        if (!(this.changeThemeProgress == 1.0f || (themeDrawable3 = this.animateOutThemeDrawable) == null)) {
            themeDrawable3.drawBackground(canvas, 1.0f);
        }
        float f = this.changeThemeProgress;
        if (f != 0.0f) {
            this.themeDrawable.drawBackground(canvas, f);
        }
        if (!(this.changeThemeProgress == 1.0f || (themeDrawable2 = this.animateOutThemeDrawable) == null)) {
            themeDrawable2.draw(canvas, 1.0f);
        }
        float f2 = this.changeThemeProgress;
        if (f2 != 0.0f) {
            this.themeDrawable.draw(canvas, f2);
        }
        float f3 = this.changeThemeProgress;
        if (f3 != 1.0f) {
            float f4 = f3 + 0.10666667f;
            this.changeThemeProgress = f4;
            if (f4 >= 1.0f) {
                this.changeThemeProgress = 1.0f;
            }
            invalidate();
        }
        super.dispatchDraw(canvas);
    }

    public void setItem(ChatThemeBottomSheet.ChatThemeItem item, boolean animated) {
        TLRPC.Document document;
        ChatThemeBottomSheet.ChatThemeItem chatThemeItem2 = item;
        boolean z = true;
        boolean itemChanged = this.chatThemeItem != chatThemeItem2;
        if (this.lastThemeIndex == chatThemeItem2.themeIndex) {
            z = false;
        }
        boolean darkModeChanged = z;
        this.lastThemeIndex = chatThemeItem2.themeIndex;
        this.chatThemeItem = chatThemeItem2;
        this.hasAnimatedEmoji = false;
        if (chatThemeItem2.chatTheme.getEmoticon() != null) {
            document = MediaDataController.getInstance(this.currentAccount).getEmojiAnimatedSticker(chatThemeItem2.chatTheme.getEmoticon());
        } else {
            document = null;
        }
        if (itemChanged) {
            Runnable runnable = this.animationCancelRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.animationCancelRunnable = null;
            }
            this.backupImageView.animate().cancel();
            this.backupImageView.setScaleX(1.0f);
            this.backupImageView.setScaleY(1.0f);
        }
        if (itemChanged) {
            Drawable thumb = null;
            if (document != null) {
                thumb = DocumentObject.getSvgThumb(document, "emptyListPlaceholder", 0.2f);
            }
            if (thumb == null) {
                Emoji.preloadEmoji(chatThemeItem2.chatTheme.getEmoticon());
                thumb = Emoji.getEmojiDrawable(chatThemeItem2.chatTheme.getEmoticon());
            }
            this.backupImageView.setImage(ImageLocation.getForDocument(document), "50_50", thumb, (Object) null);
        }
        if (itemChanged || darkModeChanged) {
            if (animated) {
                this.changeThemeProgress = 0.0f;
                this.animateOutThemeDrawable = this.themeDrawable;
                this.themeDrawable = new ThemeDrawable();
                invalidate();
            } else {
                this.changeThemeProgress = 1.0f;
            }
            updatePreviewBackground(this.themeDrawable);
            TLRPC.TL_theme theme = chatThemeItem2.chatTheme.getTlTheme(this.lastThemeIndex);
            if (theme != null) {
                long themeId = theme.id;
                TLRPC.WallPaper wallPaper = chatThemeItem2.chatTheme.getWallpaper(this.lastThemeIndex);
                if (wallPaper != null) {
                    int intensity = wallPaper.settings.intensity;
                    EmojiThemes emojiThemes = chatThemeItem2.chatTheme;
                    ThemeSmallPreviewView$$ExternalSyntheticLambda5 themeSmallPreviewView$$ExternalSyntheticLambda5 = r0;
                    boolean z2 = itemChanged;
                    TLRPC.WallPaper wallPaper2 = wallPaper;
                    ThemeSmallPreviewView$$ExternalSyntheticLambda5 themeSmallPreviewView$$ExternalSyntheticLambda52 = new ThemeSmallPreviewView$$ExternalSyntheticLambda5(this, themeId, item, intensity);
                    emojiThemes.loadWallpaperThumb(this.lastThemeIndex, themeSmallPreviewView$$ExternalSyntheticLambda5);
                } else {
                    boolean z3 = itemChanged;
                }
            } else {
                Theme.ThemeInfo themeInfo = chatThemeItem2.chatTheme.getThemeInfo(this.lastThemeIndex);
                Theme.ThemeAccent accent = null;
                if (themeInfo.themeAccentsMap != null) {
                    accent = themeInfo.themeAccentsMap.get(chatThemeItem2.chatTheme.getAccentId(this.lastThemeIndex));
                }
                if (accent != null && accent.info != null && accent.info.settings.size() > 0) {
                    TLRPC.WallPaper wallPaper3 = accent.info.settings.get(0).wallpaper;
                    if (!(wallPaper3 == null || wallPaper3.document == null)) {
                        TLRPC.Document wallpaperDocument = wallPaper3.document;
                        ImageLocation imageLocation = ImageLocation.getForDocument(FileLoader.getClosestPhotoSizeWithSize(wallpaperDocument.thumbs, 120), wallpaperDocument);
                        ImageReceiver imageReceiver = new ImageReceiver();
                        imageReceiver.setImage(imageLocation, "120_80", (Drawable) null, (String) null, (Object) null, 1);
                        imageReceiver.setDelegate(new ThemeSmallPreviewView$$ExternalSyntheticLambda4(this, chatThemeItem2, wallPaper3));
                        ImageLoader.getInstance().loadImageForImageReceiver(imageReceiver);
                    }
                } else if (accent != null && accent.info == null) {
                    ChatThemeController.chatThemeQueue.postRunnable(new ThemeSmallPreviewView$$ExternalSyntheticLambda2(this, chatThemeItem2));
                }
            }
        } else {
            boolean z4 = itemChanged;
        }
        if (!animated) {
            this.backupImageView.animate().cancel();
            this.backupImageView.setScaleX(1.0f);
            this.backupImageView.setScaleY(1.0f);
            AndroidUtilities.cancelRunOnUIThread(this.animationCancelRunnable);
            if (this.backupImageView.getImageReceiver().getLottieAnimation() != null) {
                this.backupImageView.getImageReceiver().getLottieAnimation().stop();
                this.backupImageView.getImageReceiver().getLottieAnimation().setCurrentFrame(0, false);
            }
        }
    }

    /* renamed from: lambda$setItem$0$org-telegram-ui-Components-ThemeSmallPreviewView  reason: not valid java name */
    public /* synthetic */ void m4477x81a3CLASSNAMEe(long themeId, ChatThemeBottomSheet.ChatThemeItem item, int intensity, Pair result) {
        if (result != null && ((Long) result.first).longValue() == themeId) {
            if (item.previewDrawable instanceof MotionBackgroundDrawable) {
                MotionBackgroundDrawable motionBackgroundDrawable = (MotionBackgroundDrawable) item.previewDrawable;
                motionBackgroundDrawable.setPatternBitmap(intensity >= 0 ? 100 : -100, (Bitmap) result.second);
                motionBackgroundDrawable.setPatternColorFilter(this.patternColor);
            }
            invalidate();
        }
    }

    /* renamed from: lambda$setItem$1$org-telegram-ui-Components-ThemeSmallPreviewView  reason: not valid java name */
    public /* synthetic */ void m4478x734d6a8d(ChatThemeBottomSheet.ChatThemeItem item, TLRPC.WallPaper wallPaper, ImageReceiver receiver, boolean set, boolean thumb, boolean memCache) {
        Bitmap resultBitmap;
        ImageReceiver.BitmapHolder holder = receiver.getBitmapSafe();
        if (set && holder != null && (resultBitmap = holder.bitmap) != null && (item.previewDrawable instanceof MotionBackgroundDrawable)) {
            MotionBackgroundDrawable motionBackgroundDrawable = (MotionBackgroundDrawable) item.previewDrawable;
            motionBackgroundDrawable.setPatternBitmap((wallPaper.settings == null || wallPaper.settings.intensity >= 0) ? 100 : -100, resultBitmap);
            motionBackgroundDrawable.setPatternColorFilter(this.patternColor);
            invalidate();
        }
    }

    /* renamed from: lambda$setItem$3$org-telegram-ui-Components-ThemeSmallPreviewView  reason: not valid java name */
    public /* synthetic */ void m4480x56a0b6cb(ChatThemeBottomSheet.ChatThemeItem item) {
        AndroidUtilities.runOnUIThread(new ThemeSmallPreviewView$$ExternalSyntheticLambda3(this, item, SvgHelper.getBitmap(NUM, AndroidUtilities.dp(80.0f), AndroidUtilities.dp(120.0f), -16777216, 3.0f)));
    }

    /* renamed from: lambda$setItem$2$org-telegram-ui-Components-ThemeSmallPreviewView  reason: not valid java name */
    public /* synthetic */ void m4479x64var_ac(ChatThemeBottomSheet.ChatThemeItem item, Bitmap bitmap) {
        if (item.previewDrawable instanceof MotionBackgroundDrawable) {
            MotionBackgroundDrawable motionBackgroundDrawable = (MotionBackgroundDrawable) item.previewDrawable;
            motionBackgroundDrawable.setPatternBitmap(100, bitmap);
            motionBackgroundDrawable.setPatternColorFilter(this.patternColor);
            invalidate();
        }
    }

    public void setSelected(final boolean selected, boolean animated) {
        float f = 1.0f;
        if (!animated) {
            ValueAnimator valueAnimator = this.strokeAlphaAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            this.isSelected = selected;
            if (!selected) {
                f = 0.0f;
            }
            this.selectionProgress = f;
            invalidate();
            return;
        }
        if (this.isSelected != selected) {
            float currentProgress = this.selectionProgress;
            ValueAnimator valueAnimator2 = this.strokeAlphaAnimator;
            if (valueAnimator2 != null) {
                valueAnimator2.cancel();
            }
            float[] fArr = new float[2];
            fArr[0] = currentProgress;
            if (!selected) {
                f = 0.0f;
            }
            fArr[1] = f;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
            this.strokeAlphaAnimator = ofFloat;
            ofFloat.addUpdateListener(new ThemeSmallPreviewView$$ExternalSyntheticLambda0(this));
            this.strokeAlphaAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    float unused = ThemeSmallPreviewView.this.selectionProgress = selected ? 1.0f : 0.0f;
                    ThemeSmallPreviewView.this.invalidate();
                }
            });
            this.strokeAlphaAnimator.setDuration(250);
            this.strokeAlphaAnimator.start();
        }
        this.isSelected = selected;
    }

    /* renamed from: lambda$setSelected$4$org-telegram-ui-Components-ThemeSmallPreviewView  reason: not valid java name */
    public /* synthetic */ void m4481x9e27f2e2(ValueAnimator valueAnimator) {
        this.selectionProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
    }

    public void setBackgroundColor(int color) {
        this.backgroundFillPaint.setColor(getThemedColor("dialogBackgroundGray"));
        TextPaint textPaint = this.noThemeTextPaint;
        if (textPaint != null) {
            textPaint.setColor(getThemedColor("chat_emojiPanelTrendingDescription"));
        }
        invalidate();
    }

    private void fillOutBubblePaint(Paint paint, List<Integer> messageColors) {
        if (messageColors.size() > 1) {
            int[] colors = new int[messageColors.size()];
            for (int i = 0; i != messageColors.size(); i++) {
                colors[i] = messageColors.get(i).intValue();
            }
            float top = this.INNER_RECT_SPACE + ((float) AndroidUtilities.dp(8.0f));
            paint.setShader(new LinearGradient(0.0f, top, 0.0f, top + this.BUBBLE_HEIGHT, colors, (float[]) null, Shader.TileMode.CLAMP));
            return;
        }
        paint.setShader((Shader) null);
    }

    public void updatePreviewBackground(ThemeDrawable themeDrawable2) {
        int strokeColor;
        ChatThemeBottomSheet.ChatThemeItem chatThemeItem2 = this.chatThemeItem;
        if (chatThemeItem2 != null && chatThemeItem2.chatTheme != null) {
            EmojiThemes.ThemeItem themeItem = this.chatThemeItem.chatTheme.getThemeItem(this.chatThemeItem.themeIndex);
            themeDrawable2.inBubblePaint.setColor(themeItem.inBubbleColor);
            themeDrawable2.outBubblePaintSecond.setColor(themeItem.outBubbleColor);
            if (this.chatThemeItem.chatTheme.showAsDefaultStub) {
                strokeColor = getThemedColor("featuredStickers_addButton");
            } else {
                strokeColor = themeItem.outLineColor;
            }
            int strokeAlpha = themeDrawable2.strokePaint.getAlpha();
            themeDrawable2.strokePaint.setColor(strokeColor);
            themeDrawable2.strokePaint.setAlpha(strokeAlpha);
            TLRPC.TL_theme tlTheme = this.chatThemeItem.chatTheme.getTlTheme(this.chatThemeItem.themeIndex);
            if (tlTheme != null) {
                int index = this.chatThemeItem.chatTheme.getSettingsIndex(this.chatThemeItem.themeIndex);
                fillOutBubblePaint(themeDrawable2.outBubblePaintSecond, tlTheme.settings.get(index).message_colors);
                themeDrawable2.outBubblePaintSecond.setAlpha(255);
                getPreviewDrawable(tlTheme, index);
            } else {
                getPreviewDrawable(this.chatThemeItem.chatTheme.getThemeItem(this.chatThemeItem.themeIndex));
            }
            themeDrawable2.previewDrawable = this.chatThemeItem.previewDrawable;
            invalidate();
        }
    }

    private Drawable getPreviewDrawable(TLRPC.TL_theme theme, int settingsIndex) {
        MotionBackgroundDrawable motionBackgroundDrawable;
        if (this.chatThemeItem == null) {
            return null;
        }
        int color1 = 0;
        int color2 = 0;
        int color3 = 0;
        int color4 = 0;
        if (settingsIndex >= 0) {
            TLRPC.WallPaperSettings wallPaperSettings = theme.settings.get(settingsIndex).wallpaper.settings;
            color1 = wallPaperSettings.background_color;
            color2 = wallPaperSettings.second_background_color;
            color3 = wallPaperSettings.third_background_color;
            color4 = wallPaperSettings.fourth_background_color;
        }
        if (color2 != 0) {
            motionBackgroundDrawable = new MotionBackgroundDrawable(color1, color2, color3, color4, true);
            this.patternColor = motionBackgroundDrawable.getPatternColor();
        } else {
            motionBackgroundDrawable = new MotionBackgroundDrawable(color1, color1, color1, color1, true);
            this.patternColor = -16777216;
        }
        this.chatThemeItem.previewDrawable = motionBackgroundDrawable;
        return motionBackgroundDrawable;
    }

    private Drawable getPreviewDrawable(EmojiThemes.ThemeItem item) {
        if (this.chatThemeItem == null) {
            return null;
        }
        Drawable drawable = null;
        int color1 = item.patternBgColor;
        int color2 = item.patternBgGradientColor1;
        int color3 = item.patternBgGradientColor2;
        int color4 = item.patternBgGradientColor3;
        int rotation = item.patternBgRotation;
        if (item.themeInfo.getAccent(false) != null) {
            if (color2 != 0) {
                MotionBackgroundDrawable motionBackgroundDrawable = new MotionBackgroundDrawable(color1, color2, color3, color4, rotation, true);
                this.patternColor = motionBackgroundDrawable.getPatternColor();
                drawable = motionBackgroundDrawable;
            } else {
                drawable = new MotionBackgroundDrawable(color1, color1, color1, color1, rotation, true);
                this.patternColor = -16777216;
            }
        } else if (color1 != 0 && color2 != 0) {
            drawable = new MotionBackgroundDrawable(color1, color2, color3, color4, rotation, true);
        } else if (color1 != 0) {
            drawable = new ColorDrawable(color1);
        } else if (item.themeInfo == null || (item.themeInfo.previewWallpaperOffset <= 0 && item.themeInfo.pathToWallpaper == null)) {
            drawable = new MotionBackgroundDrawable(-2368069, -9722489, -2762611, -7817084, true);
        } else {
            Bitmap wallpaper = AndroidUtilities.getScaledBitmap((float) AndroidUtilities.dp(76.0f), (float) AndroidUtilities.dp(97.0f), item.themeInfo.pathToWallpaper, item.themeInfo.pathToFile, item.themeInfo.previewWallpaperOffset);
            if (wallpaper != null) {
                drawable = new BitmapDrawable(wallpaper);
            }
        }
        this.chatThemeItem.previewDrawable = drawable;
        return drawable;
    }

    /* access modifiers changed from: private */
    public StaticLayout getNoThemeStaticLayout() {
        StaticLayout staticLayout = this.textLayout;
        if (staticLayout != null) {
            return staticLayout;
        }
        TextPaint textPaint = new TextPaint(129);
        this.noThemeTextPaint = textPaint;
        textPaint.setColor(getThemedColor("chat_emojiPanelTrendingDescription"));
        this.noThemeTextPaint.setTextSize((float) AndroidUtilities.dp(14.0f));
        this.noThemeTextPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        StaticLayout createStaticLayout2 = StaticLayoutEx.createStaticLayout2(LocaleController.getString("ChatNoTheme", NUM), this.noThemeTextPaint, AndroidUtilities.dp(52.0f), Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, true, TextUtils.TruncateAt.END, AndroidUtilities.dp(52.0f), 3);
        this.textLayout = createStaticLayout2;
        return createStaticLayout2;
    }

    /* access modifiers changed from: private */
    public int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }

    public void playEmojiAnimation() {
        if (this.backupImageView.getImageReceiver().getLottieAnimation() != null) {
            AndroidUtilities.cancelRunOnUIThread(this.animationCancelRunnable);
            this.backupImageView.setVisibility(0);
            if (!this.backupImageView.getImageReceiver().getLottieAnimation().isRunning) {
                this.backupImageView.getImageReceiver().getLottieAnimation().setCurrentFrame(0, true);
                this.backupImageView.getImageReceiver().getLottieAnimation().start();
            }
            this.backupImageView.animate().scaleX(2.0f).scaleY(2.0f).setDuration(300).setInterpolator(AndroidUtilities.overshootInterpolator).start();
            ThemeSmallPreviewView$$ExternalSyntheticLambda1 themeSmallPreviewView$$ExternalSyntheticLambda1 = new ThemeSmallPreviewView$$ExternalSyntheticLambda1(this);
            this.animationCancelRunnable = themeSmallPreviewView$$ExternalSyntheticLambda1;
            AndroidUtilities.runOnUIThread(themeSmallPreviewView$$ExternalSyntheticLambda1, 2500);
        }
    }

    /* renamed from: lambda$playEmojiAnimation$5$org-telegram-ui-Components-ThemeSmallPreviewView  reason: not valid java name */
    public /* synthetic */ void m4476x92d17632() {
        this.animationCancelRunnable = null;
        this.backupImageView.animate().scaleX(1.0f).scaleY(1.0f).setDuration(150).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
    }

    public void cancelAnimation() {
        Runnable runnable = this.animationCancelRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.animationCancelRunnable.run();
        }
    }

    private class ThemeDrawable {
        /* access modifiers changed from: private */
        public final Paint inBubblePaint = new Paint(1);
        /* access modifiers changed from: private */
        public final Paint outBubblePaintSecond = new Paint(1);
        Drawable previewDrawable;
        /* access modifiers changed from: private */
        public final Paint strokePaint;

        ThemeDrawable() {
            Paint paint = new Paint(1);
            this.strokePaint = paint;
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        }

        public void drawBackground(Canvas canvas, float alpha) {
            if (this.previewDrawable != null) {
                canvas.save();
                canvas.clipPath(ThemeSmallPreviewView.this.clipPath);
                Drawable drawable = this.previewDrawable;
                if (drawable instanceof BitmapDrawable) {
                    int drawableW = drawable.getIntrinsicWidth();
                    int drawableH = this.previewDrawable.getIntrinsicHeight();
                    if (((float) drawableW) / ((float) drawableH) > ((float) ThemeSmallPreviewView.this.getWidth()) / ((float) ThemeSmallPreviewView.this.getHeight())) {
                        int w = (int) ((((float) ThemeSmallPreviewView.this.getWidth()) * ((float) drawableH)) / ((float) drawableW));
                        int padding = (w - ThemeSmallPreviewView.this.getWidth()) / 2;
                        this.previewDrawable.setBounds(padding, 0, padding + w, ThemeSmallPreviewView.this.getHeight());
                    } else {
                        int h = (int) ((((float) ThemeSmallPreviewView.this.getHeight()) * ((float) drawableH)) / ((float) drawableW));
                        int padding2 = (ThemeSmallPreviewView.this.getHeight() - h) / 2;
                        this.previewDrawable.setBounds(0, padding2, ThemeSmallPreviewView.this.getWidth(), padding2 + h);
                    }
                } else {
                    drawable.setBounds(0, 0, ThemeSmallPreviewView.this.getWidth(), ThemeSmallPreviewView.this.getHeight());
                }
                this.previewDrawable.setAlpha((int) (alpha * 255.0f));
                this.previewDrawable.draw(canvas);
                Drawable drawable2 = this.previewDrawable;
                if ((drawable2 instanceof ColorDrawable) || ((drawable2 instanceof MotionBackgroundDrawable) && ((MotionBackgroundDrawable) drawable2).isOneColor())) {
                    ThemeSmallPreviewView.this.outlineBackgroundPaint.setAlpha((int) (255.0f * alpha));
                    float padding3 = ThemeSmallPreviewView.this.INNER_RECT_SPACE;
                    AndroidUtilities.rectTmp.set(padding3, padding3, ((float) ThemeSmallPreviewView.this.getWidth()) - padding3, ((float) ThemeSmallPreviewView.this.getHeight()) - padding3);
                    canvas.drawRoundRect(AndroidUtilities.rectTmp, ThemeSmallPreviewView.this.INNER_RADIUS, ThemeSmallPreviewView.this.INNER_RADIUS, ThemeSmallPreviewView.this.outlineBackgroundPaint);
                }
                canvas.restore();
                return;
            }
            canvas.drawRoundRect(ThemeSmallPreviewView.this.rectF, ThemeSmallPreviewView.this.INNER_RADIUS, ThemeSmallPreviewView.this.INNER_RADIUS, ThemeSmallPreviewView.this.backgroundFillPaint);
        }

        public void draw(Canvas canvas, float alpha) {
            int strokeColor;
            Canvas canvas2 = canvas;
            if (ThemeSmallPreviewView.this.isSelected || ThemeSmallPreviewView.this.strokeAlphaAnimator != null) {
                EmojiThemes.ThemeItem themeItem = ThemeSmallPreviewView.this.chatThemeItem.chatTheme.getThemeItem(ThemeSmallPreviewView.this.chatThemeItem.themeIndex);
                if (ThemeSmallPreviewView.this.chatThemeItem.chatTheme.showAsDefaultStub) {
                    strokeColor = ThemeSmallPreviewView.this.getThemedColor("featuredStickers_addButton");
                } else {
                    strokeColor = themeItem.outLineColor;
                }
                this.strokePaint.setColor(strokeColor);
                this.strokePaint.setAlpha((int) (ThemeSmallPreviewView.this.selectionProgress * alpha * 255.0f));
                float rectSpace = (this.strokePaint.getStrokeWidth() * 0.5f) + (((float) AndroidUtilities.dp(4.0f)) * (1.0f - ThemeSmallPreviewView.this.selectionProgress));
                ThemeSmallPreviewView.this.rectF.set(rectSpace, rectSpace, ((float) ThemeSmallPreviewView.this.getWidth()) - rectSpace, ((float) ThemeSmallPreviewView.this.getHeight()) - rectSpace);
                canvas2.drawRoundRect(ThemeSmallPreviewView.this.rectF, ThemeSmallPreviewView.this.STROKE_RADIUS, ThemeSmallPreviewView.this.STROKE_RADIUS, this.strokePaint);
            }
            this.outBubblePaintSecond.setAlpha((int) (alpha * 255.0f));
            this.inBubblePaint.setAlpha((int) (255.0f * alpha));
            ThemeSmallPreviewView.this.rectF.set(ThemeSmallPreviewView.this.INNER_RECT_SPACE, ThemeSmallPreviewView.this.INNER_RECT_SPACE, ((float) ThemeSmallPreviewView.this.getWidth()) - ThemeSmallPreviewView.this.INNER_RECT_SPACE, ((float) ThemeSmallPreviewView.this.getHeight()) - ThemeSmallPreviewView.this.INNER_RECT_SPACE);
            if (ThemeSmallPreviewView.this.chatThemeItem.chatTheme == null || ThemeSmallPreviewView.this.chatThemeItem.chatTheme.showAsDefaultStub) {
                canvas2.drawRoundRect(ThemeSmallPreviewView.this.rectF, ThemeSmallPreviewView.this.INNER_RADIUS, ThemeSmallPreviewView.this.INNER_RADIUS, ThemeSmallPreviewView.this.backgroundFillPaint);
                canvas.save();
                StaticLayout textLayout = ThemeSmallPreviewView.this.getNoThemeStaticLayout();
                canvas2.translate(((float) (ThemeSmallPreviewView.this.getWidth() - textLayout.getWidth())) * 0.5f, (float) AndroidUtilities.dp(18.0f));
                textLayout.draw(canvas2);
                canvas.restore();
            } else if (ThemeSmallPreviewView.this.currentType != 2) {
                float bubbleTop = ThemeSmallPreviewView.this.INNER_RECT_SPACE + ((float) AndroidUtilities.dp(8.0f));
                float bubbleLeft = ThemeSmallPreviewView.this.INNER_RECT_SPACE + ((float) AndroidUtilities.dp(22.0f));
                if (ThemeSmallPreviewView.this.currentType == 0) {
                    ThemeSmallPreviewView.this.rectF.set(bubbleLeft, bubbleTop, ThemeSmallPreviewView.this.BUBBLE_WIDTH + bubbleLeft, ThemeSmallPreviewView.this.BUBBLE_HEIGHT + bubbleTop);
                } else {
                    bubbleTop = ((float) ThemeSmallPreviewView.this.getMeasuredHeight()) * 0.12f;
                    ThemeSmallPreviewView.this.rectF.set(((float) ThemeSmallPreviewView.this.getMeasuredWidth()) - (((float) ThemeSmallPreviewView.this.getMeasuredWidth()) * 0.65f), bubbleTop, ((float) ThemeSmallPreviewView.this.getMeasuredWidth()) - (((float) ThemeSmallPreviewView.this.getMeasuredWidth()) * 0.1f), ((float) ThemeSmallPreviewView.this.getMeasuredHeight()) * 0.32f);
                }
                Paint paint = this.outBubblePaintSecond;
                if (ThemeSmallPreviewView.this.currentType == 0) {
                    canvas2.drawRoundRect(ThemeSmallPreviewView.this.rectF, ThemeSmallPreviewView.this.rectF.height() * 0.5f, ThemeSmallPreviewView.this.rectF.height() * 0.5f, paint);
                } else {
                    ThemeSmallPreviewView.this.messageDrawableOut.setBounds((int) ThemeSmallPreviewView.this.rectF.left, ((int) ThemeSmallPreviewView.this.rectF.top) - AndroidUtilities.dp(2.0f), ((int) ThemeSmallPreviewView.this.rectF.right) + AndroidUtilities.dp(4.0f), ((int) ThemeSmallPreviewView.this.rectF.bottom) + AndroidUtilities.dp(2.0f));
                    ThemeSmallPreviewView.this.messageDrawableOut.setRoundRadius((int) (ThemeSmallPreviewView.this.rectF.height() * 0.5f));
                    ThemeSmallPreviewView.this.messageDrawableOut.draw(canvas2, paint);
                }
                if (ThemeSmallPreviewView.this.currentType == 0) {
                    float bubbleLeft2 = ThemeSmallPreviewView.this.INNER_RECT_SPACE + ((float) AndroidUtilities.dp(5.0f));
                    float bubbleTop2 = bubbleTop + ThemeSmallPreviewView.this.BUBBLE_HEIGHT + ((float) AndroidUtilities.dp(4.0f));
                    ThemeSmallPreviewView.this.rectF.set(bubbleLeft2, bubbleTop2, ThemeSmallPreviewView.this.BUBBLE_WIDTH + bubbleLeft2, ThemeSmallPreviewView.this.BUBBLE_HEIGHT + bubbleTop2);
                } else {
                    float bubbleTop3 = ((float) ThemeSmallPreviewView.this.getMeasuredHeight()) * 0.35f;
                    ThemeSmallPreviewView.this.rectF.set(0.1f * ((float) ThemeSmallPreviewView.this.getMeasuredWidth()), bubbleTop3, ((float) ThemeSmallPreviewView.this.getMeasuredWidth()) * 0.65f, ((float) ThemeSmallPreviewView.this.getMeasuredHeight()) * 0.55f);
                }
                if (ThemeSmallPreviewView.this.currentType == 0) {
                    canvas2.drawRoundRect(ThemeSmallPreviewView.this.rectF, ThemeSmallPreviewView.this.rectF.height() * 0.5f, ThemeSmallPreviewView.this.rectF.height() * 0.5f, this.inBubblePaint);
                    return;
                }
                ThemeSmallPreviewView.this.messageDrawableIn.setBounds(((int) ThemeSmallPreviewView.this.rectF.left) - AndroidUtilities.dp(4.0f), ((int) ThemeSmallPreviewView.this.rectF.top) - AndroidUtilities.dp(2.0f), (int) ThemeSmallPreviewView.this.rectF.right, ((int) ThemeSmallPreviewView.this.rectF.bottom) + AndroidUtilities.dp(2.0f));
                ThemeSmallPreviewView.this.messageDrawableIn.setRoundRadius((int) (ThemeSmallPreviewView.this.rectF.height() * 0.5f));
                ThemeSmallPreviewView.this.messageDrawableIn.draw(canvas2, this.inBubblePaint);
            } else if (ThemeSmallPreviewView.this.chatThemeItem.icon != null) {
                canvas2.drawBitmap(ThemeSmallPreviewView.this.chatThemeItem.icon, ((float) (ThemeSmallPreviewView.this.getWidth() - ThemeSmallPreviewView.this.chatThemeItem.icon.getWidth())) * 0.5f, (float) AndroidUtilities.dp(21.0f), (Paint) null);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.emojiLoaded) {
            invalidate();
        }
    }
}
