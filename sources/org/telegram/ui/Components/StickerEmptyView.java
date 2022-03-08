package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SvgHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSet;
import org.telegram.ui.ActionBar.Theme;

public class StickerEmptyView extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    private boolean animateLayoutChange;
    String colorKey1;
    int currentAccount;
    int keyboardSize;
    private int lastH;
    private LinearLayout linearLayout;
    boolean preventMoving;
    /* access modifiers changed from: private */
    public RadialProgressView progressBar;
    private boolean progressShowing;
    public final View progressView;
    private final Theme.ResourcesProvider resourcesProvider;
    Runnable showProgressRunnable;
    private int stickerType;
    public BackupImageView stickerView;
    public final TextView subtitle;
    public final TextView title;

    public StickerEmptyView(Context context, View view, int i) {
        this(context, view, i, (Theme.ResourcesProvider) null);
    }

    public StickerEmptyView(Context context, View view, int i, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        this.currentAccount = UserConfig.selectedAccount;
        this.showProgressRunnable = new Runnable() {
            public void run() {
                StickerEmptyView stickerEmptyView = StickerEmptyView.this;
                View view = stickerEmptyView.progressView;
                if (view != null) {
                    if (view.getVisibility() != 0) {
                        StickerEmptyView.this.progressView.setVisibility(0);
                        StickerEmptyView.this.progressView.setAlpha(0.0f);
                    }
                    StickerEmptyView.this.progressView.animate().setListener((Animator.AnimatorListener) null).cancel();
                    StickerEmptyView.this.progressView.animate().alpha(1.0f).setDuration(150).start();
                    return;
                }
                stickerEmptyView.progressBar.animate().alpha(1.0f).scaleY(1.0f).scaleX(1.0f).setDuration(150).start();
            }
        };
        this.colorKey1 = "emptyListPlaceholder";
        this.resourcesProvider = resourcesProvider2;
        this.progressView = view;
        this.stickerType = i;
        AnonymousClass2 r13 = new LinearLayout(context) {
            public void setVisibility(int i) {
                if (getVisibility() == 8 && i == 0) {
                    StickerEmptyView.this.setSticker();
                    StickerEmptyView.this.stickerView.getImageReceiver().startAnimation();
                } else if (i == 8) {
                    StickerEmptyView.this.stickerView.getImageReceiver().clearImage();
                }
                super.setVisibility(i);
            }
        };
        this.linearLayout = r13;
        r13.setOrientation(1);
        BackupImageView backupImageView = new BackupImageView(context);
        this.stickerView = backupImageView;
        backupImageView.setOnClickListener(new StickerEmptyView$$ExternalSyntheticLambda0(this));
        TextView textView = new TextView(context);
        this.title = textView;
        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        textView.setTag("windowBackgroundWhiteBlackText");
        textView.setTextColor(getThemedColor("windowBackgroundWhiteBlackText"));
        textView.setTextSize(1, 20.0f);
        textView.setGravity(17);
        TextView textView2 = new TextView(context);
        this.subtitle = textView2;
        textView2.setTag("windowBackgroundWhiteGrayText");
        textView2.setTextColor(getThemedColor("windowBackgroundWhiteGrayText"));
        textView2.setTextSize(1, 14.0f);
        textView2.setGravity(17);
        this.linearLayout.addView(this.stickerView, LayoutHelper.createLinear(130, 130, 1));
        this.linearLayout.addView(textView, LayoutHelper.createLinear(-2, -2, 1, 0, 12, 0, 0));
        this.linearLayout.addView(textView2, LayoutHelper.createLinear(-2, -2, 1, 0, 8, 0, 0));
        addView(this.linearLayout, LayoutHelper.createFrame(-2, -2.0f, 17, 56.0f, 0.0f, 56.0f, 30.0f));
        if (view == null) {
            RadialProgressView radialProgressView = new RadialProgressView(context, resourcesProvider2);
            this.progressBar = radialProgressView;
            radialProgressView.setAlpha(0.0f);
            this.progressBar.setScaleY(0.5f);
            this.progressBar.setScaleX(0.5f);
            addView(this.progressBar, LayoutHelper.createFrame(-2, -2, 17));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(View view) {
        this.stickerView.getImageReceiver().startAnimation();
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int i5;
        super.onLayout(z, i, i2, i3, i4);
        if ((this.animateLayoutChange || this.preventMoving) && (i5 = this.lastH) > 0 && i5 != getMeasuredHeight()) {
            float measuredHeight = ((float) (this.lastH - getMeasuredHeight())) / 2.0f;
            LinearLayout linearLayout2 = this.linearLayout;
            linearLayout2.setTranslationY(linearLayout2.getTranslationY() + measuredHeight);
            if (!this.preventMoving) {
                this.linearLayout.animate().translationY(0.0f).setInterpolator(CubicBezierInterpolator.DEFAULT).setDuration(250);
            }
            RadialProgressView radialProgressView = this.progressBar;
            if (radialProgressView != null) {
                radialProgressView.setTranslationY(radialProgressView.getTranslationY() + measuredHeight);
                if (!this.preventMoving) {
                    this.progressBar.animate().translationY(0.0f).setInterpolator(CubicBezierInterpolator.DEFAULT).setDuration(250);
                }
            }
        }
        this.lastH = getMeasuredHeight();
    }

    public void setColors(String str, String str2, String str3, String str4) {
        this.title.setTag(str);
        this.title.setTextColor(getThemedColor(str));
        this.subtitle.setTag(str2);
        this.subtitle.setTextColor(getThemedColor(str2));
        this.colorKey1 = str3;
    }

    public void setVisibility(int i) {
        if (getVisibility() != i && i == 0) {
            if (this.progressShowing) {
                this.linearLayout.animate().alpha(0.0f).scaleY(0.8f).scaleX(0.8f).setDuration(150).start();
                this.progressView.setVisibility(0);
                this.progressView.setAlpha(1.0f);
            } else {
                this.linearLayout.animate().alpha(1.0f).scaleY(1.0f).scaleX(1.0f).setDuration(150).start();
                View view = this.progressView;
                if (view != null) {
                    view.animate().setListener((Animator.AnimatorListener) null).cancel();
                    this.progressView.animate().setListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            StickerEmptyView.this.progressView.setVisibility(8);
                        }
                    }).alpha(0.0f).setDuration(150).start();
                } else {
                    this.progressBar.animate().alpha(0.0f).scaleY(0.5f).scaleX(0.5f).setDuration(150).start();
                }
                this.stickerView.getImageReceiver().startAnimation();
            }
        }
        super.setVisibility(i);
        if (getVisibility() == 0) {
            setSticker();
            return;
        }
        this.lastH = 0;
        this.linearLayout.setAlpha(0.0f);
        this.linearLayout.setScaleX(0.8f);
        this.linearLayout.setScaleY(0.8f);
        View view2 = this.progressView;
        if (view2 != null) {
            view2.animate().setListener((Animator.AnimatorListener) null).cancel();
            this.progressView.animate().setListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    StickerEmptyView.this.progressView.setVisibility(8);
                }
            }).alpha(0.0f).setDuration(150).start();
        } else {
            this.progressBar.setAlpha(0.0f);
            this.progressBar.setScaleX(0.5f);
            this.progressBar.setScaleY(0.5f);
        }
        this.stickerView.getImageReceiver().stopAnimation();
        this.stickerView.getImageReceiver().clearImage();
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (getVisibility() == 0) {
            setSticker();
        }
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.diceStickersDidLoad);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.diceStickersDidLoad);
    }

    /* access modifiers changed from: private */
    public void setSticker() {
        TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet;
        String str;
        TLRPC$Document tLRPC$Document;
        TLRPC$Document tLRPC$Document2 = null;
        if (this.stickerType == 2) {
            tLRPC$Document = MediaDataController.getInstance(this.currentAccount).getEmojiAnimatedSticker("👍");
            str = null;
            tLRPC$TL_messages_stickerSet = null;
        } else {
            TLRPC$TL_messages_stickerSet stickerSetByName = MediaDataController.getInstance(this.currentAccount).getStickerSetByName("tg_placeholders_android");
            if (stickerSetByName == null) {
                stickerSetByName = MediaDataController.getInstance(this.currentAccount).getStickerSetByEmojiOrName("tg_placeholders_android");
            }
            if (stickerSetByName != null && stickerSetByName.documents.size() >= 2) {
                tLRPC$Document2 = stickerSetByName.documents.get(this.stickerType);
            }
            tLRPC$TL_messages_stickerSet = stickerSetByName;
            tLRPC$Document = tLRPC$Document2;
            str = "130_130";
        }
        if (tLRPC$Document != null) {
            SvgHelper.SvgDrawable svgThumb = DocumentObject.getSvgThumb(tLRPC$Document.thumbs, this.colorKey1, 0.2f);
            if (svgThumb != null) {
                svgThumb.overrideWidthAndHeight(512, 512);
            }
            this.stickerView.setImage(ImageLocation.getForDocument(tLRPC$Document), str, "tgs", (Drawable) svgThumb, (Object) tLRPC$TL_messages_stickerSet);
            this.stickerView.getImageReceiver().setAutoRepeat(2);
            return;
        }
        MediaDataController.getInstance(this.currentAccount).loadStickersByEmojiOrName("tg_placeholders_android", false, tLRPC$TL_messages_stickerSet == null);
        this.stickerView.getImageReceiver().clearImage();
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.diceStickersDidLoad && "tg_placeholders_android".equals(objArr[0]) && getVisibility() == 0) {
            setSticker();
        }
    }

    public void setKeyboardHeight(int i, boolean z) {
        if (this.keyboardSize != i) {
            int i2 = 0;
            if (getVisibility() != 0) {
                z = false;
            }
            this.keyboardSize = i;
            int i3 = -(i >> 1);
            if (i > 0) {
                i2 = AndroidUtilities.dp(20.0f);
            }
            float f = (float) (i3 + i2);
            if (z) {
                ViewPropertyAnimator translationY = this.linearLayout.animate().translationY(f);
                CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.DEFAULT;
                translationY.setInterpolator(cubicBezierInterpolator).setDuration(250);
                RadialProgressView radialProgressView = this.progressBar;
                if (radialProgressView != null) {
                    radialProgressView.animate().translationY(f).setInterpolator(cubicBezierInterpolator).setDuration(250);
                    return;
                }
                return;
            }
            this.linearLayout.setTranslationY(f);
            RadialProgressView radialProgressView2 = this.progressBar;
            if (radialProgressView2 != null) {
                radialProgressView2.setTranslationY(f);
            }
        }
    }

    public void showProgress(boolean z) {
        showProgress(z, true);
    }

    public void showProgress(boolean z, boolean z2) {
        if (this.progressShowing != z) {
            this.progressShowing = z;
            if (getVisibility() == 0) {
                if (z2) {
                    if (z) {
                        this.linearLayout.animate().alpha(0.0f).scaleY(0.8f).scaleX(0.8f).setDuration(150).start();
                        this.showProgressRunnable.run();
                        return;
                    }
                    this.linearLayout.animate().alpha(1.0f).scaleY(1.0f).scaleX(1.0f).setDuration(150).start();
                    View view = this.progressView;
                    if (view != null) {
                        view.animate().setListener((Animator.AnimatorListener) null).cancel();
                        this.progressView.animate().setListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animator) {
                                StickerEmptyView.this.progressView.setVisibility(8);
                            }
                        }).alpha(0.0f).setDuration(150).start();
                    } else {
                        this.progressBar.animate().alpha(0.0f).scaleY(0.5f).scaleX(0.5f).setDuration(150).start();
                    }
                    this.stickerView.getImageReceiver().startAnimation();
                } else if (z) {
                    this.linearLayout.animate().cancel();
                    this.linearLayout.setAlpha(0.0f);
                    this.linearLayout.setScaleX(0.8f);
                    this.linearLayout.setScaleY(0.8f);
                    View view2 = this.progressView;
                    if (view2 != null) {
                        view2.animate().setListener((Animator.AnimatorListener) null).cancel();
                        this.progressView.setAlpha(1.0f);
                        this.progressView.setVisibility(0);
                        return;
                    }
                    this.progressBar.setAlpha(1.0f);
                    this.progressBar.setScaleX(1.0f);
                    this.progressBar.setScaleY(1.0f);
                } else {
                    this.linearLayout.animate().cancel();
                    this.linearLayout.setAlpha(1.0f);
                    this.linearLayout.setScaleX(1.0f);
                    this.linearLayout.setScaleY(1.0f);
                    View view3 = this.progressView;
                    if (view3 != null) {
                        view3.animate().setListener((Animator.AnimatorListener) null).cancel();
                        this.progressView.setVisibility(8);
                        return;
                    }
                    this.progressBar.setAlpha(0.0f);
                    this.progressBar.setScaleX(0.5f);
                    this.progressBar.setScaleY(0.5f);
                }
            }
        }
    }

    public void setAnimateLayoutChange(boolean z) {
        this.animateLayoutChange = z;
    }

    public void setPreventMoving(boolean z) {
        this.preventMoving = z;
        if (!z) {
            this.linearLayout.setTranslationY(0.0f);
            RadialProgressView radialProgressView = this.progressBar;
            if (radialProgressView != null) {
                radialProgressView.setTranslationY(0.0f);
            }
        }
    }

    private int getThemedColor(String str) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(str) : null;
        return color != null ? color.intValue() : Theme.getColor(str);
    }

    public void setStickerType(int i) {
        if (this.stickerType != i) {
            this.stickerType = i;
            setSticker();
        }
    }
}
