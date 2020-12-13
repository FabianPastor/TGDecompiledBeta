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
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSet;
import org.telegram.ui.ActionBar.Theme;

public class StickerEmptyView extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    private boolean animateLayoutChange;
    int currentAccount = UserConfig.selectedAccount;
    int keyboardSize;
    private int lastH;
    private LinearLayout linearLayout;
    /* access modifiers changed from: private */
    public RadialProgressView progressBar;
    private boolean progressShowing;
    public final View progressView;
    Runnable showProgressRunnable = new Runnable() {
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
    private int stickerType;
    public BackupImageView stickerView;
    private LoadingStickerDrawable stubDrawable;
    public final TextView subtitle;
    public final TextView title;

    public StickerEmptyView(Context context, View view, int i) {
        super(context);
        this.progressView = view;
        this.stickerType = i;
        AnonymousClass2 r0 = new LinearLayout(context) {
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
        this.linearLayout = r0;
        r0.setOrientation(1);
        BackupImageView backupImageView = new BackupImageView(context);
        this.stickerView = backupImageView;
        backupImageView.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                StickerEmptyView.this.lambda$new$0$StickerEmptyView(view);
            }
        });
        LoadingStickerDrawable loadingStickerDrawable = new LoadingStickerDrawable(this.stickerView, i == 1 ? "M503.1,302.3c-2-20-21.4-29.8-42.4-30.7CLASSNAME.8-56.8-8.2-121-52.8-164.1CLASSNAME.6,24,190,51.3,131.7,146.2\n\tc-21.2-30.5-65-34.3-91.1-7.6c-30,30.6-18.4,82.7,22.5,97.3c-4.7,2.4-6.4,7.6-5.7,12.4c-14.2,10.5-19,28.5-5.1,42.4\n\tc-5.4,15,13.2,28.8,26.9,18.8CLASSNAME.5,6.9,21,15,27.8,28.8c-17.1,55.3-8.5,79.4,8.5,98.7v0CLASSNAME.5,53.8,235.6,45.3,292.2,11.5\n\tCLASSNAME.6-13.5,39.5-34.6,30.4-96.8CLASSNAME.1,322.1,505.7,328.5,503.1,302.3z M107.4,234c0.1,2.8,0.2,5.8,0.4,8.8c-7-2.5-14-3.6-20.5-3.6\n\tCLASSNAME.4,238.6,101.2,236.9,107.4,234z" : "m418 282.6CLASSNAME.4-21.1 20.2-44.9 20.2-70.8 0-88.3-79.8-175.3-178.9-175.3-100.1 0-178.9 88-178.9 175.3 0 46.6 16.9 73.1 29.1 86.1-19.3 23.4-30.9 52.3-34.6 86.1-2.5 22.7 3.2 41.4 17.4 57.3 14.3 16 51.7 35 148.1 35 41.2 0 119.9-5.3 156.7-18.3 49.5-17.4 59.2-41.1 59.2-76.2 0-41.5-12.9-74.8-38.3-99.2z", AndroidUtilities.dp(130.0f), AndroidUtilities.dp(130.0f));
        this.stubDrawable = loadingStickerDrawable;
        this.stickerView.setImageDrawable(loadingStickerDrawable);
        TextView textView = new TextView(context);
        this.title = textView;
        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        textView.setTag("windowBackgroundWhiteBlackText");
        textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        textView.setTextSize(1, 20.0f);
        textView.setGravity(17);
        TextView textView2 = new TextView(context);
        this.subtitle = textView2;
        textView2.setTag("windowBackgroundWhiteGrayText");
        textView2.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText"));
        textView2.setTextSize(1, 14.0f);
        textView2.setGravity(17);
        this.linearLayout.addView(this.stickerView, LayoutHelper.createLinear(130, 130, 1));
        this.linearLayout.addView(textView, LayoutHelper.createLinear(-2, -2, 1, 0, 12, 0, 0));
        this.linearLayout.addView(textView2, LayoutHelper.createLinear(-2, -2, 1, 0, 8, 0, 0));
        addView(this.linearLayout, LayoutHelper.createFrame(-2, -2.0f, 17, 56.0f, 0.0f, 56.0f, 30.0f));
        if (view == null) {
            RadialProgressView radialProgressView = new RadialProgressView(context);
            this.progressBar = radialProgressView;
            radialProgressView.setAlpha(0.0f);
            this.progressBar.setScaleY(0.5f);
            this.progressBar.setScaleX(0.5f);
            addView(this.progressBar, LayoutHelper.createFrame(-2, -2, 17));
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$StickerEmptyView(View view) {
        this.stickerView.getImageReceiver().startAnimation();
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int i5;
        super.onLayout(z, i, i2, i3, i4);
        if (this.animateLayoutChange && (i5 = this.lastH) > 0 && i5 != getMeasuredHeight()) {
            float measuredHeight = ((float) (this.lastH - getMeasuredHeight())) / 2.0f;
            LinearLayout linearLayout2 = this.linearLayout;
            linearLayout2.setTranslationY(linearLayout2.getTranslationY() + measuredHeight);
            ViewPropertyAnimator translationY = this.linearLayout.animate().translationY(0.0f);
            CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.DEFAULT;
            translationY.setInterpolator(cubicBezierInterpolator).setDuration(250);
            RadialProgressView radialProgressView = this.progressBar;
            if (radialProgressView != null) {
                radialProgressView.setTranslationY(radialProgressView.getTranslationY() + measuredHeight);
                this.progressBar.animate().translationY(0.0f).setInterpolator(cubicBezierInterpolator).setDuration(250);
            }
        }
        this.lastH = getMeasuredHeight();
    }

    public void setColors(String str, String str2, String str3, String str4) {
        this.title.setTag(str);
        this.title.setTextColor(Theme.getColor(str));
        this.subtitle.setTag(str2);
        this.subtitle.setTextColor(Theme.getColor(str2));
        this.stubDrawable.setColors(str3, str4);
    }

    public void setVisibility(int i) {
        if (getVisibility() != i && i == 0) {
            if (this.progressShowing) {
                this.linearLayout.animate().alpha(0.0f).scaleY(0.8f).scaleX(0.8f).setDuration(150).start();
                this.showProgressRunnable.run();
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
        TLRPC$TL_messages_stickerSet stickerSetByName = MediaDataController.getInstance(this.currentAccount).getStickerSetByName("tg_placeholders");
        if (stickerSetByName == null) {
            stickerSetByName = MediaDataController.getInstance(this.currentAccount).getStickerSetByEmojiOrName("tg_placeholders");
        }
        TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = stickerSetByName;
        if (tLRPC$TL_messages_stickerSet == null || tLRPC$TL_messages_stickerSet.documents.size() < 2) {
            MediaDataController.getInstance(this.currentAccount).loadStickersByEmojiOrName("tg_placeholders", false, tLRPC$TL_messages_stickerSet == null);
            this.stickerView.setImageDrawable(this.stubDrawable);
            return;
        }
        this.stickerView.setImage(ImageLocation.getForDocument(tLRPC$TL_messages_stickerSet.documents.get(this.stickerType)), "130_130", "tgs", (Drawable) this.stubDrawable, (Object) tLRPC$TL_messages_stickerSet);
        this.stickerView.getImageReceiver().setAutoRepeat(2);
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.diceStickersDidLoad && "tg_placeholders".equals(objArr[0]) && getVisibility() == 0) {
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
}
