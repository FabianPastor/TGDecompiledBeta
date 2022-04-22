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
import android.util.SparseArray;
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
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$TL_theme;
import org.telegram.tgnet.TLRPC$WallPaper;
import org.telegram.tgnet.TLRPC$WallPaperSettings;
import org.telegram.ui.ActionBar.EmojiThemes;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.ChatThemeBottomSheet;

public class ThemeSmallPreviewView extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
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

    public ThemeSmallPreviewView(Context context, int i, Theme.ResourcesProvider resourcesProvider2, int i2) {
        super(context);
        this.currentType = i2;
        this.currentAccount = i;
        this.resourcesProvider = resourcesProvider2;
        setBackgroundColor(getThemedColor("dialogBackgroundGray"));
        BackupImageView backupImageView2 = new BackupImageView(context);
        this.backupImageView = backupImageView2;
        backupImageView2.getImageReceiver().setCrossfadeWithOldImage(true);
        this.backupImageView.getImageReceiver().setAllowStartLottieAnimation(false);
        this.backupImageView.getImageReceiver().setAutoRepeat(0);
        if (i2 == 0 || i2 == 2) {
            addView(this.backupImageView, LayoutHelper.createFrame(28, 28.0f, 81, 0.0f, 0.0f, 0.0f, 12.0f));
        } else {
            addView(this.backupImageView, LayoutHelper.createFrame(36, 36.0f, 81, 0.0f, 0.0f, 0.0f, 12.0f));
        }
        this.outlineBackgroundPaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
        this.outlineBackgroundPaint.setStyle(Paint.Style.STROKE);
        this.outlineBackgroundPaint.setColor(-1842205);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        if (this.currentType == 1) {
            int size = View.MeasureSpec.getSize(i);
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec((int) (((float) size) * 1.2f), NUM));
        } else {
            int dp = AndroidUtilities.dp(77.0f);
            int size2 = View.MeasureSpec.getSize(i2);
            if (size2 == 0) {
                size2 = (int) (((float) dp) * 1.35f);
            }
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(dp, NUM), View.MeasureSpec.makeMeasureSpec(size2, NUM));
        }
        BackupImageView backupImageView2 = this.backupImageView;
        backupImageView2.setPivotY((float) backupImageView2.getMeasuredHeight());
        BackupImageView backupImageView3 = this.backupImageView;
        backupImageView3.setPivotX(((float) backupImageView3.getMeasuredWidth()) / 2.0f);
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        if (i != i3 || i2 != i4) {
            RectF rectF2 = this.rectF;
            float f = this.INNER_RECT_SPACE;
            rectF2.set(f, f, ((float) i) - f, ((float) i2) - f);
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

    public void setItem(ChatThemeBottomSheet.ChatThemeItem chatThemeItem2, boolean z) {
        TLRPC$TL_theme tLRPC$TL_theme;
        TLRPC$Document tLRPC$Document;
        boolean z2 = true;
        boolean z3 = this.chatThemeItem != chatThemeItem2;
        int i = this.lastThemeIndex;
        int i2 = chatThemeItem2.themeIndex;
        if (i == i2) {
            z2 = false;
        }
        this.lastThemeIndex = i2;
        this.chatThemeItem = chatThemeItem2;
        Theme.ThemeAccent themeAccent = null;
        TLRPC$Document emojiAnimatedSticker = chatThemeItem2.chatTheme.getEmoticon() != null ? MediaDataController.getInstance(this.currentAccount).getEmojiAnimatedSticker(chatThemeItem2.chatTheme.getEmoticon()) : null;
        if (z3) {
            Runnable runnable = this.animationCancelRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.animationCancelRunnable = null;
            }
            this.backupImageView.animate().cancel();
            this.backupImageView.setScaleX(1.0f);
            this.backupImageView.setScaleY(1.0f);
        }
        if (z3) {
            Drawable svgThumb = emojiAnimatedSticker != null ? DocumentObject.getSvgThumb(emojiAnimatedSticker, "emptyListPlaceholder", 0.2f) : null;
            if (svgThumb == null) {
                Emoji.preloadEmoji(chatThemeItem2.chatTheme.getEmoticon());
                svgThumb = Emoji.getEmojiDrawable(chatThemeItem2.chatTheme.getEmoticon());
            }
            this.backupImageView.setImage(ImageLocation.getForDocument(emojiAnimatedSticker), "50_50", svgThumb, (Object) null);
        }
        if (z3 || z2) {
            if (z) {
                this.changeThemeProgress = 0.0f;
                this.animateOutThemeDrawable = this.themeDrawable;
                this.themeDrawable = new ThemeDrawable();
                invalidate();
            } else {
                this.changeThemeProgress = 1.0f;
            }
            updatePreviewBackground(this.themeDrawable);
            TLRPC$TL_theme tlTheme = chatThemeItem2.chatTheme.getTlTheme(this.lastThemeIndex);
            if (tlTheme != null) {
                long j = tlTheme.id;
                TLRPC$WallPaper wallpaper = chatThemeItem2.chatTheme.getWallpaper(this.lastThemeIndex);
                if (wallpaper != null) {
                    chatThemeItem2.chatTheme.loadWallpaperThumb(this.lastThemeIndex, new ThemeSmallPreviewView$$ExternalSyntheticLambda5(this, j, chatThemeItem2, wallpaper.settings.intensity));
                }
            } else {
                SparseArray<Theme.ThemeAccent> sparseArray = chatThemeItem2.chatTheme.getThemeInfo(this.lastThemeIndex).themeAccentsMap;
                if (sparseArray != null) {
                    themeAccent = sparseArray.get(chatThemeItem2.chatTheme.getAccentId(this.lastThemeIndex));
                }
                if (themeAccent != null && (tLRPC$TL_theme = themeAccent.info) != null && tLRPC$TL_theme.settings.size() > 0) {
                    TLRPC$WallPaper tLRPC$WallPaper = themeAccent.info.settings.get(0).wallpaper;
                    if (!(tLRPC$WallPaper == null || (tLRPC$Document = tLRPC$WallPaper.document) == null)) {
                        ImageLocation forDocument = ImageLocation.getForDocument(FileLoader.getClosestPhotoSizeWithSize(tLRPC$Document.thumbs, 120), tLRPC$Document);
                        ImageReceiver imageReceiver = new ImageReceiver();
                        imageReceiver.setImage(forDocument, "120_80", (Drawable) null, (String) null, (Object) null, 1);
                        imageReceiver.setDelegate(new ThemeSmallPreviewView$$ExternalSyntheticLambda4(this, chatThemeItem2, tLRPC$WallPaper));
                        ImageLoader.getInstance().loadImageForImageReceiver(imageReceiver);
                    }
                } else if (themeAccent != null && themeAccent.info == null) {
                    ChatThemeController.chatThemeQueue.postRunnable(new ThemeSmallPreviewView$$ExternalSyntheticLambda2(this, chatThemeItem2));
                }
            }
        }
        if (!z) {
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

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setItem$0(long j, ChatThemeBottomSheet.ChatThemeItem chatThemeItem2, int i, Pair pair) {
        if (pair != null && ((Long) pair.first).longValue() == j) {
            Drawable drawable = chatThemeItem2.previewDrawable;
            if (drawable instanceof MotionBackgroundDrawable) {
                MotionBackgroundDrawable motionBackgroundDrawable = (MotionBackgroundDrawable) drawable;
                motionBackgroundDrawable.setPatternBitmap(i >= 0 ? 100 : -100, (Bitmap) pair.second);
                motionBackgroundDrawable.setPatternColorFilter(this.patternColor);
            }
            invalidate();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setItem$1(ChatThemeBottomSheet.ChatThemeItem chatThemeItem2, TLRPC$WallPaper tLRPC$WallPaper, ImageReceiver imageReceiver, boolean z, boolean z2, boolean z3) {
        Bitmap bitmap;
        ImageReceiver.BitmapHolder bitmapSafe = imageReceiver.getBitmapSafe();
        if (z && bitmapSafe != null && (bitmap = bitmapSafe.bitmap) != null) {
            Drawable drawable = chatThemeItem2.previewDrawable;
            if (drawable instanceof MotionBackgroundDrawable) {
                MotionBackgroundDrawable motionBackgroundDrawable = (MotionBackgroundDrawable) drawable;
                TLRPC$WallPaperSettings tLRPC$WallPaperSettings = tLRPC$WallPaper.settings;
                motionBackgroundDrawable.setPatternBitmap((tLRPC$WallPaperSettings == null || tLRPC$WallPaperSettings.intensity >= 0) ? 100 : -100, bitmap);
                motionBackgroundDrawable.setPatternColorFilter(this.patternColor);
                invalidate();
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setItem$3(ChatThemeBottomSheet.ChatThemeItem chatThemeItem2) {
        AndroidUtilities.runOnUIThread(new ThemeSmallPreviewView$$ExternalSyntheticLambda3(this, chatThemeItem2, SvgHelper.getBitmap(NUM, AndroidUtilities.dp(80.0f), AndroidUtilities.dp(120.0f), -16777216, 3.0f)));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setItem$2(ChatThemeBottomSheet.ChatThemeItem chatThemeItem2, Bitmap bitmap) {
        Drawable drawable = chatThemeItem2.previewDrawable;
        if (drawable instanceof MotionBackgroundDrawable) {
            MotionBackgroundDrawable motionBackgroundDrawable = (MotionBackgroundDrawable) drawable;
            motionBackgroundDrawable.setPatternBitmap(100, bitmap);
            motionBackgroundDrawable.setPatternColorFilter(this.patternColor);
            invalidate();
        }
    }

    public void setSelected(final boolean z, boolean z2) {
        float f = 1.0f;
        if (!z2) {
            ValueAnimator valueAnimator = this.strokeAlphaAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            this.isSelected = z;
            if (!z) {
                f = 0.0f;
            }
            this.selectionProgress = f;
            invalidate();
            return;
        }
        if (this.isSelected != z) {
            float f2 = this.selectionProgress;
            ValueAnimator valueAnimator2 = this.strokeAlphaAnimator;
            if (valueAnimator2 != null) {
                valueAnimator2.cancel();
            }
            float[] fArr = new float[2];
            fArr[0] = f2;
            if (!z) {
                f = 0.0f;
            }
            fArr[1] = f;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
            this.strokeAlphaAnimator = ofFloat;
            ofFloat.addUpdateListener(new ThemeSmallPreviewView$$ExternalSyntheticLambda0(this));
            this.strokeAlphaAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    super.onAnimationEnd(animator);
                    float unused = ThemeSmallPreviewView.this.selectionProgress = z ? 1.0f : 0.0f;
                    ThemeSmallPreviewView.this.invalidate();
                }
            });
            this.strokeAlphaAnimator.setDuration(250);
            this.strokeAlphaAnimator.start();
        }
        this.isSelected = z;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setSelected$4(ValueAnimator valueAnimator) {
        this.selectionProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
    }

    public void setBackgroundColor(int i) {
        this.backgroundFillPaint.setColor(getThemedColor("dialogBackgroundGray"));
        TextPaint textPaint = this.noThemeTextPaint;
        if (textPaint != null) {
            textPaint.setColor(getThemedColor("chat_emojiPanelTrendingDescription"));
        }
        invalidate();
    }

    private void fillOutBubblePaint(Paint paint, List<Integer> list) {
        if (list.size() > 1) {
            int[] iArr = new int[list.size()];
            for (int i = 0; i != list.size(); i++) {
                iArr[i] = list.get(i).intValue();
            }
            float dp = this.INNER_RECT_SPACE + ((float) AndroidUtilities.dp(8.0f));
            paint.setShader(new LinearGradient(0.0f, dp, 0.0f, dp + this.BUBBLE_HEIGHT, iArr, (float[]) null, Shader.TileMode.CLAMP));
            return;
        }
        paint.setShader((Shader) null);
    }

    public void updatePreviewBackground(ThemeDrawable themeDrawable2) {
        EmojiThemes emojiThemes;
        int i;
        ChatThemeBottomSheet.ChatThemeItem chatThemeItem2 = this.chatThemeItem;
        if (chatThemeItem2 != null && (emojiThemes = chatThemeItem2.chatTheme) != null) {
            EmojiThemes.ThemeItem themeItem = emojiThemes.getThemeItem(chatThemeItem2.themeIndex);
            themeDrawable2.inBubblePaint.setColor(themeItem.inBubbleColor);
            themeDrawable2.outBubblePaintSecond.setColor(themeItem.outBubbleColor);
            if (this.chatThemeItem.chatTheme.showAsDefaultStub) {
                i = getThemedColor("featuredStickers_addButton");
            } else {
                i = themeItem.outLineColor;
            }
            int alpha = themeDrawable2.strokePaint.getAlpha();
            themeDrawable2.strokePaint.setColor(i);
            themeDrawable2.strokePaint.setAlpha(alpha);
            ChatThemeBottomSheet.ChatThemeItem chatThemeItem3 = this.chatThemeItem;
            TLRPC$TL_theme tlTheme = chatThemeItem3.chatTheme.getTlTheme(chatThemeItem3.themeIndex);
            if (tlTheme != null) {
                ChatThemeBottomSheet.ChatThemeItem chatThemeItem4 = this.chatThemeItem;
                int settingsIndex = chatThemeItem4.chatTheme.getSettingsIndex(chatThemeItem4.themeIndex);
                fillOutBubblePaint(themeDrawable2.outBubblePaintSecond, tlTheme.settings.get(settingsIndex).message_colors);
                themeDrawable2.outBubblePaintSecond.setAlpha(255);
                getPreviewDrawable(tlTheme, settingsIndex);
            } else {
                ChatThemeBottomSheet.ChatThemeItem chatThemeItem5 = this.chatThemeItem;
                getPreviewDrawable(chatThemeItem5.chatTheme.getThemeItem(chatThemeItem5.themeIndex));
            }
            themeDrawable2.previewDrawable = this.chatThemeItem.previewDrawable;
            invalidate();
        }
    }

    private Drawable getPreviewDrawable(TLRPC$TL_theme tLRPC$TL_theme, int i) {
        int i2;
        int i3;
        int i4;
        int i5;
        MotionBackgroundDrawable motionBackgroundDrawable;
        if (this.chatThemeItem == null) {
            return null;
        }
        if (i >= 0) {
            TLRPC$WallPaperSettings tLRPC$WallPaperSettings = tLRPC$TL_theme.settings.get(i).wallpaper.settings;
            int i6 = tLRPC$WallPaperSettings.background_color;
            int i7 = tLRPC$WallPaperSettings.second_background_color;
            int i8 = tLRPC$WallPaperSettings.third_background_color;
            i3 = tLRPC$WallPaperSettings.fourth_background_color;
            i5 = i7;
            i2 = i6;
            i4 = i8;
        } else {
            i5 = 0;
            i4 = 0;
            i3 = 0;
            i2 = 0;
        }
        if (i5 != 0) {
            motionBackgroundDrawable = new MotionBackgroundDrawable(i2, i5, i4, i3, true);
            this.patternColor = motionBackgroundDrawable.getPatternColor();
        } else {
            motionBackgroundDrawable = new MotionBackgroundDrawable(i2, i2, i2, i2, true);
            this.patternColor = -16777216;
        }
        this.chatThemeItem.previewDrawable = motionBackgroundDrawable;
        return motionBackgroundDrawable;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v0, resolved type: android.graphics.drawable.BitmapDrawable} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v1, resolved type: android.graphics.drawable.BitmapDrawable} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v6, resolved type: org.telegram.ui.Components.MotionBackgroundDrawable} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v2, resolved type: android.graphics.drawable.BitmapDrawable} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v7, resolved type: android.graphics.drawable.ColorDrawable} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v10, resolved type: org.telegram.ui.Components.MotionBackgroundDrawable} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v8, resolved type: org.telegram.ui.Components.MotionBackgroundDrawable} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v11, resolved type: org.telegram.ui.Components.MotionBackgroundDrawable} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v12, resolved type: org.telegram.ui.Components.MotionBackgroundDrawable} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v13, resolved type: org.telegram.ui.Components.MotionBackgroundDrawable} */
    /* JADX WARNING: type inference failed for: r10v1, types: [android.graphics.drawable.Drawable] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private android.graphics.drawable.Drawable getPreviewDrawable(org.telegram.ui.ActionBar.EmojiThemes.ThemeItem r10) {
        /*
            r9 = this;
            org.telegram.ui.Components.ChatThemeBottomSheet$ChatThemeItem r0 = r9.chatThemeItem
            r1 = 0
            if (r0 != 0) goto L_0x0006
            return r1
        L_0x0006:
            int r0 = r10.patternBgColor
            int r4 = r10.patternBgGradientColor1
            int r5 = r10.patternBgGradientColor2
            int r6 = r10.patternBgGradientColor3
            int r7 = r10.patternBgRotation
            org.telegram.ui.ActionBar.Theme$ThemeInfo r2 = r10.themeInfo
            r3 = 0
            org.telegram.ui.ActionBar.Theme$ThemeAccent r2 = r2.getAccent(r3)
            if (r2 == 0) goto L_0x003b
            if (r4 == 0) goto L_0x002b
            org.telegram.ui.Components.MotionBackgroundDrawable r10 = new org.telegram.ui.Components.MotionBackgroundDrawable
            r8 = 1
            r2 = r10
            r3 = r0
            r2.<init>(r3, r4, r5, r6, r7, r8)
            int r0 = r10.getPatternColor()
            r9.patternColor = r0
            goto L_0x0092
        L_0x002b:
            org.telegram.ui.Components.MotionBackgroundDrawable r10 = new org.telegram.ui.Components.MotionBackgroundDrawable
            r8 = 1
            r2 = r10
            r3 = r0
            r4 = r0
            r5 = r0
            r6 = r0
            r2.<init>(r3, r4, r5, r6, r7, r8)
            r0 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r9.patternColor = r0
            goto L_0x0092
        L_0x003b:
            if (r0 == 0) goto L_0x0048
            if (r4 == 0) goto L_0x0048
            org.telegram.ui.Components.MotionBackgroundDrawable r10 = new org.telegram.ui.Components.MotionBackgroundDrawable
            r8 = 1
            r2 = r10
            r3 = r0
            r2.<init>(r3, r4, r5, r6, r7, r8)
            goto L_0x0092
        L_0x0048:
            if (r0 == 0) goto L_0x0050
            android.graphics.drawable.ColorDrawable r10 = new android.graphics.drawable.ColorDrawable
            r10.<init>(r0)
            goto L_0x0092
        L_0x0050:
            org.telegram.ui.ActionBar.Theme$ThemeInfo r0 = r10.themeInfo
            if (r0 == 0) goto L_0x007f
            int r2 = r0.previewWallpaperOffset
            if (r2 > 0) goto L_0x005c
            java.lang.String r0 = r0.pathToWallpaper
            if (r0 == 0) goto L_0x007f
        L_0x005c:
            r0 = 1117257728(0x42980000, float:76.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            float r0 = (float) r0
            r2 = 1120010240(0x42CLASSNAME, float:97.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            org.telegram.ui.ActionBar.Theme$ThemeInfo r10 = r10.themeInfo
            java.lang.String r3 = r10.pathToWallpaper
            java.lang.String r4 = r10.pathToFile
            int r10 = r10.previewWallpaperOffset
            android.graphics.Bitmap r10 = org.telegram.messenger.AndroidUtilities.getScaledBitmap(r0, r2, r3, r4, r10)
            if (r10 == 0) goto L_0x007d
            android.graphics.drawable.BitmapDrawable r1 = new android.graphics.drawable.BitmapDrawable
            r1.<init>(r10)
        L_0x007d:
            r10 = r1
            goto L_0x0092
        L_0x007f:
            org.telegram.ui.Components.MotionBackgroundDrawable r10 = new org.telegram.ui.Components.MotionBackgroundDrawable
            r3 = -2368069(0xffffffffffdbddbb, float:NaN)
            r4 = -9722489(0xffffffffff6ba587, float:-3.1322805E38)
            r5 = -2762611(0xffffffffffd5d88d, float:NaN)
            r6 = -7817084(0xfffffffffvar_b884, float:NaN)
            r7 = 1
            r2 = r10
            r2.<init>(r3, r4, r5, r6, r7)
        L_0x0092:
            org.telegram.ui.Components.ChatThemeBottomSheet$ChatThemeItem r0 = r9.chatThemeItem
            r0.previewDrawable = r10
            return r10
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ThemeSmallPreviewView.getPreviewDrawable(org.telegram.ui.ActionBar.EmojiThemes$ThemeItem):android.graphics.drawable.Drawable");
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
    public int getThemedColor(String str) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(str) : null;
        return color != null ? color.intValue() : Theme.getColor(str);
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

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$playEmojiAnimation$5() {
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

        public void drawBackground(Canvas canvas, float f) {
            if (this.previewDrawable != null) {
                canvas.save();
                canvas.clipPath(ThemeSmallPreviewView.this.clipPath);
                Drawable drawable = this.previewDrawable;
                if (drawable instanceof BitmapDrawable) {
                    float intrinsicWidth = (float) drawable.getIntrinsicWidth();
                    float intrinsicHeight = (float) this.previewDrawable.getIntrinsicHeight();
                    if (intrinsicWidth / intrinsicHeight > ((float) ThemeSmallPreviewView.this.getWidth()) / ((float) ThemeSmallPreviewView.this.getHeight())) {
                        int width = (int) ((((float) ThemeSmallPreviewView.this.getWidth()) * intrinsicHeight) / intrinsicWidth);
                        int width2 = (width - ThemeSmallPreviewView.this.getWidth()) / 2;
                        this.previewDrawable.setBounds(width2, 0, width + width2, ThemeSmallPreviewView.this.getHeight());
                    } else {
                        int height = (int) ((((float) ThemeSmallPreviewView.this.getHeight()) * intrinsicHeight) / intrinsicWidth);
                        int height2 = (ThemeSmallPreviewView.this.getHeight() - height) / 2;
                        this.previewDrawable.setBounds(0, height2, ThemeSmallPreviewView.this.getWidth(), height + height2);
                    }
                } else {
                    drawable.setBounds(0, 0, ThemeSmallPreviewView.this.getWidth(), ThemeSmallPreviewView.this.getHeight());
                }
                int i = (int) (f * 255.0f);
                this.previewDrawable.setAlpha(i);
                this.previewDrawable.draw(canvas);
                Drawable drawable2 = this.previewDrawable;
                if ((drawable2 instanceof ColorDrawable) || ((drawable2 instanceof MotionBackgroundDrawable) && ((MotionBackgroundDrawable) drawable2).isOneColor())) {
                    ThemeSmallPreviewView.this.outlineBackgroundPaint.setAlpha(i);
                    float access$500 = ThemeSmallPreviewView.this.INNER_RECT_SPACE;
                    RectF rectF = AndroidUtilities.rectTmp;
                    rectF.set(access$500, access$500, ((float) ThemeSmallPreviewView.this.getWidth()) - access$500, ((float) ThemeSmallPreviewView.this.getHeight()) - access$500);
                    canvas.drawRoundRect(rectF, ThemeSmallPreviewView.this.INNER_RADIUS, ThemeSmallPreviewView.this.INNER_RADIUS, ThemeSmallPreviewView.this.outlineBackgroundPaint);
                }
                canvas.restore();
                return;
            }
            canvas.drawRoundRect(ThemeSmallPreviewView.this.rectF, ThemeSmallPreviewView.this.INNER_RADIUS, ThemeSmallPreviewView.this.INNER_RADIUS, ThemeSmallPreviewView.this.backgroundFillPaint);
        }

        public void draw(Canvas canvas, float f) {
            int i;
            ThemeSmallPreviewView themeSmallPreviewView = ThemeSmallPreviewView.this;
            if (themeSmallPreviewView.isSelected || themeSmallPreviewView.strokeAlphaAnimator != null) {
                ChatThemeBottomSheet.ChatThemeItem chatThemeItem = ThemeSmallPreviewView.this.chatThemeItem;
                EmojiThemes.ThemeItem themeItem = chatThemeItem.chatTheme.getThemeItem(chatThemeItem.themeIndex);
                ThemeSmallPreviewView themeSmallPreviewView2 = ThemeSmallPreviewView.this;
                if (themeSmallPreviewView2.chatThemeItem.chatTheme.showAsDefaultStub) {
                    i = themeSmallPreviewView2.getThemedColor("featuredStickers_addButton");
                } else {
                    i = themeItem.outLineColor;
                }
                this.strokePaint.setColor(i);
                this.strokePaint.setAlpha((int) (ThemeSmallPreviewView.this.selectionProgress * f * 255.0f));
                float strokeWidth = (this.strokePaint.getStrokeWidth() * 0.5f) + (((float) AndroidUtilities.dp(4.0f)) * (1.0f - ThemeSmallPreviewView.this.selectionProgress));
                ThemeSmallPreviewView.this.rectF.set(strokeWidth, strokeWidth, ((float) ThemeSmallPreviewView.this.getWidth()) - strokeWidth, ((float) ThemeSmallPreviewView.this.getHeight()) - strokeWidth);
                canvas.drawRoundRect(ThemeSmallPreviewView.this.rectF, ThemeSmallPreviewView.this.STROKE_RADIUS, ThemeSmallPreviewView.this.STROKE_RADIUS, this.strokePaint);
            }
            int i2 = (int) (f * 255.0f);
            this.outBubblePaintSecond.setAlpha(i2);
            this.inBubblePaint.setAlpha(i2);
            ThemeSmallPreviewView.this.rectF.set(ThemeSmallPreviewView.this.INNER_RECT_SPACE, ThemeSmallPreviewView.this.INNER_RECT_SPACE, ((float) ThemeSmallPreviewView.this.getWidth()) - ThemeSmallPreviewView.this.INNER_RECT_SPACE, ((float) ThemeSmallPreviewView.this.getHeight()) - ThemeSmallPreviewView.this.INNER_RECT_SPACE);
            ThemeSmallPreviewView themeSmallPreviewView3 = ThemeSmallPreviewView.this;
            EmojiThemes emojiThemes = themeSmallPreviewView3.chatThemeItem.chatTheme;
            if (emojiThemes == null || emojiThemes.showAsDefaultStub) {
                canvas.drawRoundRect(themeSmallPreviewView3.rectF, ThemeSmallPreviewView.this.INNER_RADIUS, ThemeSmallPreviewView.this.INNER_RADIUS, ThemeSmallPreviewView.this.backgroundFillPaint);
                canvas.save();
                StaticLayout access$1200 = ThemeSmallPreviewView.this.getNoThemeStaticLayout();
                canvas.translate(((float) (ThemeSmallPreviewView.this.getWidth() - access$1200.getWidth())) * 0.5f, (float) AndroidUtilities.dp(18.0f));
                access$1200.draw(canvas);
                canvas.restore();
            } else if (themeSmallPreviewView3.currentType == 2) {
                ThemeSmallPreviewView themeSmallPreviewView4 = ThemeSmallPreviewView.this;
                if (themeSmallPreviewView4.chatThemeItem.icon != null) {
                    canvas.drawBitmap(ThemeSmallPreviewView.this.chatThemeItem.icon, ((float) (themeSmallPreviewView4.getWidth() - ThemeSmallPreviewView.this.chatThemeItem.icon.getWidth())) * 0.5f, (float) AndroidUtilities.dp(21.0f), (Paint) null);
                }
            } else {
                float access$500 = ThemeSmallPreviewView.this.INNER_RECT_SPACE + ((float) AndroidUtilities.dp(8.0f));
                float access$5002 = ThemeSmallPreviewView.this.INNER_RECT_SPACE + ((float) AndroidUtilities.dp(22.0f));
                if (ThemeSmallPreviewView.this.currentType == 0) {
                    ThemeSmallPreviewView.this.rectF.set(access$5002, access$500, ThemeSmallPreviewView.this.BUBBLE_WIDTH + access$5002, ThemeSmallPreviewView.this.BUBBLE_HEIGHT + access$500);
                } else {
                    access$500 = ((float) ThemeSmallPreviewView.this.getMeasuredHeight()) * 0.12f;
                    ThemeSmallPreviewView.this.rectF.set(((float) ThemeSmallPreviewView.this.getMeasuredWidth()) - (((float) ThemeSmallPreviewView.this.getMeasuredWidth()) * 0.65f), access$500, ((float) ThemeSmallPreviewView.this.getMeasuredWidth()) - (((float) ThemeSmallPreviewView.this.getMeasuredWidth()) * 0.1f), ((float) ThemeSmallPreviewView.this.getMeasuredHeight()) * 0.32f);
                }
                Paint paint = this.outBubblePaintSecond;
                if (ThemeSmallPreviewView.this.currentType == 0) {
                    canvas.drawRoundRect(ThemeSmallPreviewView.this.rectF, ThemeSmallPreviewView.this.rectF.height() * 0.5f, ThemeSmallPreviewView.this.rectF.height() * 0.5f, paint);
                } else {
                    ThemeSmallPreviewView themeSmallPreviewView5 = ThemeSmallPreviewView.this;
                    themeSmallPreviewView5.messageDrawableOut.setBounds((int) themeSmallPreviewView5.rectF.left, ((int) ThemeSmallPreviewView.this.rectF.top) - AndroidUtilities.dp(2.0f), ((int) ThemeSmallPreviewView.this.rectF.right) + AndroidUtilities.dp(4.0f), ((int) ThemeSmallPreviewView.this.rectF.bottom) + AndroidUtilities.dp(2.0f));
                    ThemeSmallPreviewView themeSmallPreviewView6 = ThemeSmallPreviewView.this;
                    themeSmallPreviewView6.messageDrawableOut.setRoundRadius((int) (themeSmallPreviewView6.rectF.height() * 0.5f));
                    ThemeSmallPreviewView.this.messageDrawableOut.draw(canvas, paint);
                }
                if (ThemeSmallPreviewView.this.currentType == 0) {
                    float access$5003 = ThemeSmallPreviewView.this.INNER_RECT_SPACE + ((float) AndroidUtilities.dp(5.0f));
                    float access$1500 = access$500 + ThemeSmallPreviewView.this.BUBBLE_HEIGHT + ((float) AndroidUtilities.dp(4.0f));
                    ThemeSmallPreviewView.this.rectF.set(access$5003, access$1500, ThemeSmallPreviewView.this.BUBBLE_WIDTH + access$5003, ThemeSmallPreviewView.this.BUBBLE_HEIGHT + access$1500);
                } else {
                    float measuredWidth = ((float) ThemeSmallPreviewView.this.getMeasuredWidth()) * 0.1f;
                    ThemeSmallPreviewView.this.rectF.set(measuredWidth, ((float) ThemeSmallPreviewView.this.getMeasuredHeight()) * 0.35f, ((float) ThemeSmallPreviewView.this.getMeasuredWidth()) * 0.65f, ((float) ThemeSmallPreviewView.this.getMeasuredHeight()) * 0.55f);
                }
                if (ThemeSmallPreviewView.this.currentType == 0) {
                    canvas.drawRoundRect(ThemeSmallPreviewView.this.rectF, ThemeSmallPreviewView.this.rectF.height() * 0.5f, ThemeSmallPreviewView.this.rectF.height() * 0.5f, this.inBubblePaint);
                    return;
                }
                ThemeSmallPreviewView themeSmallPreviewView7 = ThemeSmallPreviewView.this;
                themeSmallPreviewView7.messageDrawableIn.setBounds(((int) themeSmallPreviewView7.rectF.left) - AndroidUtilities.dp(4.0f), ((int) ThemeSmallPreviewView.this.rectF.top) - AndroidUtilities.dp(2.0f), (int) ThemeSmallPreviewView.this.rectF.right, ((int) ThemeSmallPreviewView.this.rectF.bottom) + AndroidUtilities.dp(2.0f));
                ThemeSmallPreviewView themeSmallPreviewView8 = ThemeSmallPreviewView.this;
                themeSmallPreviewView8.messageDrawableIn.setRoundRadius((int) (themeSmallPreviewView8.rectF.height() * 0.5f));
                ThemeSmallPreviewView.this.messageDrawableIn.draw(canvas, this.inBubblePaint);
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

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.emojiLoaded) {
            invalidate();
        }
    }
}
