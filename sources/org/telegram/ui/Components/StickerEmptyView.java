package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.ActionBar.Theme;

public class StickerEmptyView extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    public static final int STICKER_TYPE_DONE = 2;
    public static final int STICKER_TYPE_NO_CONTACTS = 0;
    public static final int STICKER_TYPE_SEARCH = 1;
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

    public StickerEmptyView(Context context, View progressView2, int type) {
        this(context, progressView2, type, (Theme.ResourcesProvider) null);
    }

    public StickerEmptyView(Context context, View progressView2, int type, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        this.currentAccount = UserConfig.selectedAccount;
        this.showProgressRunnable = new Runnable() {
            public void run() {
                if (StickerEmptyView.this.progressView != null) {
                    if (StickerEmptyView.this.progressView.getVisibility() != 0) {
                        StickerEmptyView.this.progressView.setVisibility(0);
                        StickerEmptyView.this.progressView.setAlpha(0.0f);
                    }
                    StickerEmptyView.this.progressView.animate().setListener((Animator.AnimatorListener) null).cancel();
                    StickerEmptyView.this.progressView.animate().alpha(1.0f).setDuration(150).start();
                    return;
                }
                StickerEmptyView.this.progressBar.animate().alpha(1.0f).scaleY(1.0f).scaleX(1.0f).setDuration(150).start();
            }
        };
        this.colorKey1 = "emptyListPlaceholder";
        this.resourcesProvider = resourcesProvider2;
        this.progressView = progressView2;
        this.stickerType = type;
        AnonymousClass2 r0 = new LinearLayout(context) {
            public void setVisibility(int visibility) {
                if (getVisibility() == 8 && visibility == 0) {
                    StickerEmptyView.this.setSticker();
                    StickerEmptyView.this.stickerView.getImageReceiver().startAnimation();
                } else if (visibility == 8) {
                    StickerEmptyView.this.stickerView.getImageReceiver().clearImage();
                }
                super.setVisibility(visibility);
            }
        };
        this.linearLayout = r0;
        r0.setOrientation(1);
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
        if (progressView2 == null) {
            RadialProgressView radialProgressView = new RadialProgressView(context, resourcesProvider2);
            this.progressBar = radialProgressView;
            radialProgressView.setAlpha(0.0f);
            this.progressBar.setScaleY(0.5f);
            this.progressBar.setScaleX(0.5f);
            addView(this.progressBar, LayoutHelper.createFrame(-2, -2, 17));
        }
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-StickerEmptyView  reason: not valid java name */
    public /* synthetic */ void m4407lambda$new$0$orgtelegramuiComponentsStickerEmptyView(View view) {
        this.stickerView.getImageReceiver().startAnimation();
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int i;
        super.onLayout(changed, left, top, right, bottom);
        if ((this.animateLayoutChange || this.preventMoving) && (i = this.lastH) > 0 && i != getMeasuredHeight()) {
            float y = ((float) (this.lastH - getMeasuredHeight())) / 2.0f;
            LinearLayout linearLayout2 = this.linearLayout;
            linearLayout2.setTranslationY(linearLayout2.getTranslationY() + y);
            if (!this.preventMoving) {
                this.linearLayout.animate().translationY(0.0f).setInterpolator(CubicBezierInterpolator.DEFAULT).setDuration(250);
            }
            RadialProgressView radialProgressView = this.progressBar;
            if (radialProgressView != null) {
                radialProgressView.setTranslationY(radialProgressView.getTranslationY() + y);
                if (!this.preventMoving) {
                    this.progressBar.animate().translationY(0.0f).setInterpolator(CubicBezierInterpolator.DEFAULT).setDuration(250);
                }
            }
        }
        this.lastH = getMeasuredHeight();
    }

    public void setColors(String titleKey, String subtitleKey, String key1, String key2) {
        this.title.setTag(titleKey);
        this.title.setTextColor(getThemedColor(titleKey));
        this.subtitle.setTag(subtitleKey);
        this.subtitle.setTextColor(getThemedColor(subtitleKey));
        this.colorKey1 = key1;
    }

    public void setVisibility(int visibility) {
        if (getVisibility() != visibility && visibility == 0) {
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
                        public void onAnimationEnd(Animator animation) {
                            StickerEmptyView.this.progressView.setVisibility(8);
                        }
                    }).alpha(0.0f).setDuration(150).start();
                } else {
                    this.progressBar.animate().alpha(0.0f).scaleY(0.5f).scaleX(0.5f).setDuration(150).start();
                }
                this.stickerView.getImageReceiver().startAnimation();
            }
        }
        super.setVisibility(visibility);
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
                public void onAnimationEnd(Animator animation) {
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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v12, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v3, resolved type: org.telegram.tgnet.TLRPC$Document} */
    /* access modifiers changed from: private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setSticker() {
        /*
            r13 = this;
            r0 = 0
            r1 = 0
            r2 = 0
            int r3 = r13.stickerType
            r4 = 2
            java.lang.String r5 = "tg_placeholders_android"
            if (r3 != r4) goto L_0x0017
            int r3 = r13.currentAccount
            org.telegram.messenger.MediaDataController r3 = org.telegram.messenger.MediaDataController.getInstance(r3)
            java.lang.String r6 = "👍"
            org.telegram.tgnet.TLRPC$Document r1 = r3.getEmojiAnimatedSticker(r6)
            goto L_0x0048
        L_0x0017:
            int r3 = r13.currentAccount
            org.telegram.messenger.MediaDataController r3 = org.telegram.messenger.MediaDataController.getInstance(r3)
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r2 = r3.getStickerSetByName(r5)
            if (r2 != 0) goto L_0x002d
            int r3 = r13.currentAccount
            org.telegram.messenger.MediaDataController r3 = org.telegram.messenger.MediaDataController.getInstance(r3)
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r2 = r3.getStickerSetByEmojiOrName(r5)
        L_0x002d:
            if (r2 == 0) goto L_0x0046
            int r3 = r13.stickerType
            if (r3 < 0) goto L_0x0046
            java.util.ArrayList r6 = r2.documents
            int r6 = r6.size()
            if (r3 >= r6) goto L_0x0046
            java.util.ArrayList r3 = r2.documents
            int r6 = r13.stickerType
            java.lang.Object r3 = r3.get(r6)
            r1 = r3
            org.telegram.tgnet.TLRPC$Document r1 = (org.telegram.tgnet.TLRPC.Document) r1
        L_0x0046:
            java.lang.String r0 = "130_130"
        L_0x0048:
            r3 = 1
            if (r1 == 0) goto L_0x0086
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r5 = r1.thumbs
            java.lang.String r6 = r13.colorKey1
            r7 = 1045220557(0x3e4ccccd, float:0.2)
            org.telegram.messenger.SvgHelper$SvgDrawable r5 = org.telegram.messenger.DocumentObject.getSvgThumb((java.util.ArrayList<org.telegram.tgnet.TLRPC.PhotoSize>) r5, (java.lang.String) r6, (float) r7)
            if (r5 == 0) goto L_0x005d
            r6 = 512(0x200, float:7.175E-43)
            r5.overrideWidthAndHeight(r6, r6)
        L_0x005d:
            org.telegram.messenger.ImageLocation r12 = org.telegram.messenger.ImageLocation.getForDocument(r1)
            org.telegram.ui.Components.BackupImageView r6 = r13.stickerView
            java.lang.String r9 = "tgs"
            r7 = r12
            r8 = r0
            r10 = r5
            r11 = r2
            r6.setImage((org.telegram.messenger.ImageLocation) r7, (java.lang.String) r8, (java.lang.String) r9, (android.graphics.drawable.Drawable) r10, (java.lang.Object) r11)
            int r6 = r13.stickerType
            r7 = 9
            if (r6 != r7) goto L_0x007c
            org.telegram.ui.Components.BackupImageView r4 = r13.stickerView
            org.telegram.messenger.ImageReceiver r4 = r4.getImageReceiver()
            r4.setAutoRepeat(r3)
            goto L_0x0085
        L_0x007c:
            org.telegram.ui.Components.BackupImageView r3 = r13.stickerView
            org.telegram.messenger.ImageReceiver r3 = r3.getImageReceiver()
            r3.setAutoRepeat(r4)
        L_0x0085:
            goto L_0x009d
        L_0x0086:
            int r4 = r13.currentAccount
            org.telegram.messenger.MediaDataController r4 = org.telegram.messenger.MediaDataController.getInstance(r4)
            r6 = 0
            if (r2 != 0) goto L_0x0090
            goto L_0x0091
        L_0x0090:
            r3 = 0
        L_0x0091:
            r4.loadStickersByEmojiOrName(r5, r6, r3)
            org.telegram.ui.Components.BackupImageView r3 = r13.stickerView
            org.telegram.messenger.ImageReceiver r3 = r3.getImageReceiver()
            r3.clearImage()
        L_0x009d:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.StickerEmptyView.setSticker():void");
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.diceStickersDidLoad && "tg_placeholders_android".equals(args[0]) && getVisibility() == 0) {
            setSticker();
        }
    }

    public void setKeyboardHeight(int keyboardSize2, boolean animated) {
        if (this.keyboardSize != keyboardSize2) {
            if (getVisibility() != 0) {
                animated = false;
            }
            this.keyboardSize = keyboardSize2;
            float y = (float) ((-(keyboardSize2 >> 1)) + (keyboardSize2 > 0 ? AndroidUtilities.dp(20.0f) : 0));
            if (animated) {
                this.linearLayout.animate().translationY(y).setInterpolator(CubicBezierInterpolator.DEFAULT).setDuration(250);
                RadialProgressView radialProgressView = this.progressBar;
                if (radialProgressView != null) {
                    radialProgressView.animate().translationY(y).setInterpolator(CubicBezierInterpolator.DEFAULT).setDuration(250);
                    return;
                }
                return;
            }
            this.linearLayout.setTranslationY(y);
            RadialProgressView radialProgressView2 = this.progressBar;
            if (radialProgressView2 != null) {
                radialProgressView2.setTranslationY(y);
            }
        }
    }

    public void showProgress(boolean show) {
        showProgress(show, true);
    }

    public void showProgress(boolean show, boolean animated) {
        if (this.progressShowing != show) {
            this.progressShowing = show;
            if (getVisibility() == 0) {
                if (animated) {
                    if (show) {
                        this.linearLayout.animate().alpha(0.0f).scaleY(0.8f).scaleX(0.8f).setDuration(150).start();
                        this.showProgressRunnable.run();
                        return;
                    }
                    this.linearLayout.animate().alpha(1.0f).scaleY(1.0f).scaleX(1.0f).setDuration(150).start();
                    View view = this.progressView;
                    if (view != null) {
                        view.animate().setListener((Animator.AnimatorListener) null).cancel();
                        this.progressView.animate().setListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animation) {
                                StickerEmptyView.this.progressView.setVisibility(8);
                            }
                        }).alpha(0.0f).setDuration(150).start();
                    } else {
                        this.progressBar.animate().alpha(0.0f).scaleY(0.5f).scaleX(0.5f).setDuration(150).start();
                    }
                    this.stickerView.getImageReceiver().startAnimation();
                } else if (show) {
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

    public void setAnimateLayoutChange(boolean animate) {
        this.animateLayoutChange = animate;
    }

    public void setPreventMoving(boolean preventMoving2) {
        this.preventMoving = preventMoving2;
        if (!preventMoving2) {
            this.linearLayout.setTranslationY(0.0f);
            RadialProgressView radialProgressView = this.progressBar;
            if (radialProgressView != null) {
                radialProgressView.setTranslationY(0.0f);
            }
        }
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }

    public void setStickerType(int stickerType2) {
        if (this.stickerType != stickerType2) {
            this.stickerType = stickerType2;
            setSticker();
        }
    }
}
