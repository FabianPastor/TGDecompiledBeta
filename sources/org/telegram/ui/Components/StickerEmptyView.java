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
    private LoadingStickerDrawable stubDrawable;
    public final TextView subtitle;
    public final TextView title;

    public StickerEmptyView(Context context, View progressView2, int type) {
        this(context, progressView2, type, (Theme.ResourcesProvider) null);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public StickerEmptyView(Context context, View progressView2, int type, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        String svg;
        Context context2 = context;
        View view = progressView2;
        int i = type;
        Theme.ResourcesProvider resourcesProvider3 = resourcesProvider2;
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
        this.resourcesProvider = resourcesProvider3;
        this.progressView = view;
        this.stickerType = i;
        AnonymousClass2 r5 = new LinearLayout(context2) {
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
        this.linearLayout = r5;
        r5.setOrientation(1);
        BackupImageView backupImageView = new BackupImageView(context2);
        this.stickerView = backupImageView;
        backupImageView.setOnClickListener(new StickerEmptyView$$ExternalSyntheticLambda0(this));
        if (i == 1) {
            svg = "M503.1,302.3c-2-20-21.4-29.8-42.4-30.7CLASSNAME.8-56.8-8.2-121-52.8-164.1CLASSNAME.6,24,190,51.3,131.7,146.2\n\tc-21.2-30.5-65-34.3-91.1-7.6c-30,30.6-18.4,82.7,22.5,97.3c-4.7,2.4-6.4,7.6-5.7,12.4c-14.2,10.5-19,28.5-5.1,42.4\n\tc-5.4,15,13.2,28.8,26.9,18.8CLASSNAME.5,6.9,21,15,27.8,28.8c-17.1,55.3-8.5,79.4,8.5,98.7v0CLASSNAME.5,53.8,235.6,45.3,292.2,11.5\n\tCLASSNAME.6-13.5,39.5-34.6,30.4-96.8CLASSNAME.1,322.1,505.7,328.5,503.1,302.3z M107.4,234c0.1,2.8,0.2,5.8,0.4,8.8c-7-2.5-14-3.6-20.5-3.6\n\tCLASSNAME.4,238.6,101.2,236.9,107.4,234z";
        } else {
            svg = "m418 282.6CLASSNAME.4-21.1 20.2-44.9 20.2-70.8 0-88.3-79.8-175.3-178.9-175.3-100.1 0-178.9 88-178.9 175.3 0 46.6 16.9 73.1 29.1 86.1-19.3 23.4-30.9 52.3-34.6 86.1-2.5 22.7 3.2 41.4 17.4 57.3 14.3 16 51.7 35 148.1 35 41.2 0 119.9-5.3 156.7-18.3 49.5-17.4 59.2-41.1 59.2-76.2 0-41.5-12.9-74.8-38.3-99.2z";
        }
        LoadingStickerDrawable loadingStickerDrawable = new LoadingStickerDrawable(this.stickerView, svg, AndroidUtilities.dp(130.0f), AndroidUtilities.dp(130.0f));
        this.stubDrawable = loadingStickerDrawable;
        this.stickerView.setImageDrawable(loadingStickerDrawable);
        TextView textView = new TextView(context2);
        this.title = textView;
        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        textView.setTag("windowBackgroundWhiteBlackText");
        textView.setTextColor(getThemedColor("windowBackgroundWhiteBlackText"));
        textView.setTextSize(1, 20.0f);
        textView.setGravity(17);
        TextView textView2 = new TextView(context2);
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
            RadialProgressView radialProgressView = new RadialProgressView(context2, resourcesProvider3);
            this.progressBar = radialProgressView;
            radialProgressView.setAlpha(0.0f);
            this.progressBar.setScaleY(0.5f);
            this.progressBar.setScaleX(0.5f);
            addView(this.progressBar, LayoutHelper.createFrame(-2, -2, 17));
        }
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-StickerEmptyView  reason: not valid java name */
    public /* synthetic */ void m2620lambda$new$0$orgtelegramuiComponentsStickerEmptyView(View view) {
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
        this.stubDrawable.setColors(key1, key2);
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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v10, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v3, resolved type: org.telegram.tgnet.TLRPC$Document} */
    /* access modifiers changed from: private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setSticker() {
        /*
            r12 = this;
            r0 = 0
            r1 = 0
            r2 = 0
            int r3 = r12.stickerType
            java.lang.String r4 = "tg_placeholders_android"
            r5 = 2
            if (r3 != r5) goto L_0x0017
            int r3 = r12.currentAccount
            org.telegram.messenger.MediaDataController r3 = org.telegram.messenger.MediaDataController.getInstance(r3)
            java.lang.String r6 = "👍"
            org.telegram.tgnet.TLRPC$Document r1 = r3.getEmojiAnimatedSticker(r6)
            goto L_0x0044
        L_0x0017:
            int r3 = r12.currentAccount
            org.telegram.messenger.MediaDataController r3 = org.telegram.messenger.MediaDataController.getInstance(r3)
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r2 = r3.getStickerSetByName(r4)
            if (r2 != 0) goto L_0x002d
            int r3 = r12.currentAccount
            org.telegram.messenger.MediaDataController r3 = org.telegram.messenger.MediaDataController.getInstance(r3)
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r2 = r3.getStickerSetByEmojiOrName(r4)
        L_0x002d:
            if (r2 == 0) goto L_0x0042
            java.util.ArrayList r3 = r2.documents
            int r3 = r3.size()
            if (r3 < r5) goto L_0x0042
            java.util.ArrayList r3 = r2.documents
            int r6 = r12.stickerType
            java.lang.Object r3 = r3.get(r6)
            r1 = r3
            org.telegram.tgnet.TLRPC$Document r1 = (org.telegram.tgnet.TLRPC.Document) r1
        L_0x0042:
            java.lang.String r0 = "130_130"
        L_0x0044:
            if (r1 == 0) goto L_0x0060
            org.telegram.messenger.ImageLocation r3 = org.telegram.messenger.ImageLocation.getForDocument(r1)
            org.telegram.ui.Components.BackupImageView r6 = r12.stickerView
            org.telegram.ui.Components.LoadingStickerDrawable r10 = r12.stubDrawable
            java.lang.String r9 = "tgs"
            r7 = r3
            r8 = r0
            r11 = r2
            r6.setImage((org.telegram.messenger.ImageLocation) r7, (java.lang.String) r8, (java.lang.String) r9, (android.graphics.drawable.Drawable) r10, (java.lang.Object) r11)
            org.telegram.ui.Components.BackupImageView r4 = r12.stickerView
            org.telegram.messenger.ImageReceiver r4 = r4.getImageReceiver()
            r4.setAutoRepeat(r5)
            goto L_0x0076
        L_0x0060:
            int r3 = r12.currentAccount
            org.telegram.messenger.MediaDataController r3 = org.telegram.messenger.MediaDataController.getInstance(r3)
            r5 = 0
            if (r2 != 0) goto L_0x006b
            r6 = 1
            goto L_0x006c
        L_0x006b:
            r6 = 0
        L_0x006c:
            r3.loadStickersByEmojiOrName(r4, r5, r6)
            org.telegram.ui.Components.BackupImageView r3 = r12.stickerView
            org.telegram.ui.Components.LoadingStickerDrawable r4 = r12.stubDrawable
            r3.setImageDrawable(r4)
        L_0x0076:
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
}
