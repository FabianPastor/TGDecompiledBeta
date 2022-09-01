package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Iterator;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$TL_availableReaction;
import org.telegram.tgnet.TLRPC$TL_emojiStatus;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.DrawerLayoutContainer;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Premium.PremiumGradient;
import org.telegram.ui.Components.Premium.StarParticlesView;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.Reactions.AnimatedEmojiEffect;
import org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble;
import org.telegram.ui.Components.SnowflakesEffect;
import org.telegram.ui.ThemeActivity;

public class DrawerProfileCell extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    public static boolean switchingTheme;
    private boolean accountsShown;
    /* access modifiers changed from: private */
    public AnimatedStatusView animatedStatus;
    private ImageView arrowView;
    private BackupImageView avatarImageView;
    private Integer currentColor;
    private Integer currentMoonColor;
    private RLottieImageView darkThemeView;
    private Rect destRect = new Rect();
    public boolean drawPremium;
    public float drawPremiumProgress;
    PremiumGradient.GradientTools gradientTools;
    private int lastAccount;
    private TLRPC$User lastUser;
    private SimpleTextView nameTextView;
    private Paint paint = new Paint();
    private TextView phoneTextView;
    private Drawable premiumStar;
    private ImageView shadowView;
    private SnowflakesEffect snowflakesEffect;
    private Rect srcRect = new Rect();
    StarParticlesView.Drawable starParticlesDrawable;
    private AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable status;
    private RLottieDrawable sunDrawable;
    /* access modifiers changed from: private */
    public boolean updateRightDrawable = true;

    /* access modifiers changed from: protected */
    public void onPremiumClick() {
    }

    public DrawerProfileCell(Context context, DrawerLayoutContainer drawerLayoutContainer) {
        super(context);
        new Paint(1);
        this.lastAccount = -1;
        this.lastUser = null;
        this.premiumStar = null;
        ImageView imageView = new ImageView(context);
        this.shadowView = imageView;
        imageView.setVisibility(4);
        this.shadowView.setScaleType(ImageView.ScaleType.FIT_XY);
        this.shadowView.setImageResource(R.drawable.bottom_shadow);
        addView(this.shadowView, LayoutHelper.createFrame(-1, 70, 83));
        BackupImageView backupImageView = new BackupImageView(context);
        this.avatarImageView = backupImageView;
        backupImageView.getImageReceiver().setRoundRadius(AndroidUtilities.dp(32.0f));
        addView(this.avatarImageView, LayoutHelper.createFrame(64, 64.0f, 83, 16.0f, 0.0f, 0.0f, 67.0f));
        AnonymousClass1 r1 = new SimpleTextView(context) {
            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                if (DrawerProfileCell.this.updateRightDrawable) {
                    boolean unused = DrawerProfileCell.this.updateRightDrawable = false;
                    DrawerProfileCell drawerProfileCell = DrawerProfileCell.this;
                    Rect rect = AndroidUtilities.rectTmp2;
                    drawerProfileCell.getEmojiStatusLocation(rect);
                    DrawerProfileCell.this.animatedStatus.translate(rect.centerX(), rect.centerY());
                }
            }
        };
        this.nameTextView = r1;
        r1.setRightDrawableOnClick(new DrawerProfileCell$$ExternalSyntheticLambda1(this));
        this.nameTextView.setPadding(0, AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f));
        this.nameTextView.setTextSize(15);
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.nameTextView.setGravity(19);
        addView(this.nameTextView, LayoutHelper.createFrame(-1, -2.0f, 83, 16.0f, 0.0f, 76.0f, 28.0f));
        TextView textView = new TextView(context);
        this.phoneTextView = textView;
        textView.setTextSize(1, 13.0f);
        this.phoneTextView.setLines(1);
        this.phoneTextView.setMaxLines(1);
        this.phoneTextView.setSingleLine(true);
        this.phoneTextView.setGravity(3);
        addView(this.phoneTextView, LayoutHelper.createFrame(-1, -2.0f, 83, 16.0f, 0.0f, 76.0f, 9.0f));
        ImageView imageView2 = new ImageView(context);
        this.arrowView = imageView2;
        imageView2.setScaleType(ImageView.ScaleType.CENTER);
        this.arrowView.setImageResource(R.drawable.msg_expand);
        addView(this.arrowView, LayoutHelper.createFrame(59, 59, 85));
        setArrowState(false);
        int i = R.raw.sun;
        this.sunDrawable = new RLottieDrawable(i, "" + i, AndroidUtilities.dp(28.0f), AndroidUtilities.dp(28.0f), true, (int[]) null);
        if (Theme.isCurrentThemeDay()) {
            this.sunDrawable.setCustomEndFrame(36);
        } else {
            this.sunDrawable.setCustomEndFrame(0);
            this.sunDrawable.setCurrentFrame(36);
        }
        this.sunDrawable.setPlayInDirectionOfCustomEndFrame(true);
        AnonymousClass2 r12 = new RLottieImageView(this, context) {
            public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
                super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
                if (Theme.isCurrentThemeDark()) {
                    accessibilityNodeInfo.setText(LocaleController.getString("AccDescrSwitchToDayTheme", R.string.AccDescrSwitchToDayTheme));
                } else {
                    accessibilityNodeInfo.setText(LocaleController.getString("AccDescrSwitchToNightTheme", R.string.AccDescrSwitchToNightTheme));
                }
            }
        };
        this.darkThemeView = r12;
        r12.setFocusable(true);
        this.darkThemeView.setBackground(Theme.createCircleSelectorDrawable(Theme.getColor("dialogButtonSelector"), 0, 0));
        this.sunDrawable.beginApplyLayerColors();
        int color = Theme.getColor("chats_menuName");
        this.sunDrawable.setLayerColor("Sunny.**", color);
        this.sunDrawable.setLayerColor("Path 6.**", color);
        this.sunDrawable.setLayerColor("Path.**", color);
        this.sunDrawable.setLayerColor("Path 5.**", color);
        this.sunDrawable.commitApplyLayerColors();
        this.darkThemeView.setScaleType(ImageView.ScaleType.CENTER);
        this.darkThemeView.setAnimation(this.sunDrawable);
        if (Build.VERSION.SDK_INT >= 21) {
            this.darkThemeView.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("listSelectorSDK21"), 1, AndroidUtilities.dp(17.0f)));
            Theme.setRippleDrawableForceSoftware((RippleDrawable) this.darkThemeView.getBackground());
        }
        this.darkThemeView.setOnClickListener(new DrawerProfileCell$$ExternalSyntheticLambda0(this));
        this.darkThemeView.setOnLongClickListener(new DrawerProfileCell$$ExternalSyntheticLambda2(drawerLayoutContainer));
        addView(this.darkThemeView, LayoutHelper.createFrame(48, 48.0f, 85, 0.0f, 0.0f, 6.0f, 90.0f));
        if (Theme.getEventType() == 0) {
            SnowflakesEffect snowflakesEffect2 = new SnowflakesEffect(0);
            this.snowflakesEffect = snowflakesEffect2;
            snowflakesEffect2.setColorKey("chats_menuName");
        }
        AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable swapAnimatedEmojiDrawable = new AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable(this, AndroidUtilities.dp(20.0f));
        this.status = swapAnimatedEmojiDrawable;
        this.nameTextView.setRightDrawable((Drawable) swapAnimatedEmojiDrawable);
        AnimatedStatusView animatedStatusView = new AnimatedStatusView(context, 20, 60);
        this.animatedStatus = animatedStatusView;
        addView(animatedStatusView, LayoutHelper.createFrame(20, 20, 51));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(View view) {
        TLRPC$User tLRPC$User = this.lastUser;
        if (tLRPC$User != null && tLRPC$User.premium) {
            onPremiumClick();
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x006f  */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x007b  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x008d  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$new$1(android.view.View r7) {
        /*
            r6 = this;
            boolean r7 = switchingTheme
            if (r7 == 0) goto L_0x0005
            return
        L_0x0005:
            r7 = 1
            switchingTheme = r7
            android.content.Context r7 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.String r0 = "themeconfig"
            r1 = 0
            android.content.SharedPreferences r7 = r7.getSharedPreferences(r0, r1)
            java.lang.String r0 = "lastDayTheme"
            java.lang.String r2 = "Blue"
            java.lang.String r0 = r7.getString(r0, r2)
            org.telegram.ui.ActionBar.Theme$ThemeInfo r3 = org.telegram.ui.ActionBar.Theme.getTheme(r0)
            if (r3 == 0) goto L_0x0029
            org.telegram.ui.ActionBar.Theme$ThemeInfo r3 = org.telegram.ui.ActionBar.Theme.getTheme(r0)
            boolean r3 = r3.isDark()
            if (r3 == 0) goto L_0x002a
        L_0x0029:
            r0 = r2
        L_0x002a:
            java.lang.String r3 = "lastDarkTheme"
            java.lang.String r4 = "Dark Blue"
            java.lang.String r7 = r7.getString(r3, r4)
            org.telegram.ui.ActionBar.Theme$ThemeInfo r3 = org.telegram.ui.ActionBar.Theme.getTheme(r7)
            if (r3 == 0) goto L_0x0042
            org.telegram.ui.ActionBar.Theme$ThemeInfo r3 = org.telegram.ui.ActionBar.Theme.getTheme(r7)
            boolean r3 = r3.isDark()
            if (r3 != 0) goto L_0x0043
        L_0x0042:
            r7 = r4
        L_0x0043:
            org.telegram.ui.ActionBar.Theme$ThemeInfo r3 = org.telegram.ui.ActionBar.Theme.getActiveTheme()
            boolean r5 = r0.equals(r7)
            if (r5 == 0) goto L_0x0063
            boolean r5 = r3.isDark()
            if (r5 != 0) goto L_0x0061
            boolean r5 = r0.equals(r4)
            if (r5 != 0) goto L_0x0061
            java.lang.String r5 = "Night"
            boolean r5 = r0.equals(r5)
            if (r5 == 0) goto L_0x0064
        L_0x0061:
            r4 = r7
            goto L_0x0065
        L_0x0063:
            r4 = r7
        L_0x0064:
            r2 = r0
        L_0x0065:
            java.lang.String r7 = r3.getKey()
            boolean r7 = r2.equals(r7)
            if (r7 == 0) goto L_0x007b
            org.telegram.ui.ActionBar.Theme$ThemeInfo r0 = org.telegram.ui.ActionBar.Theme.getTheme(r4)
            org.telegram.ui.Components.RLottieDrawable r2 = r6.sunDrawable
            r3 = 36
            r2.setCustomEndFrame(r3)
            goto L_0x0084
        L_0x007b:
            org.telegram.ui.ActionBar.Theme$ThemeInfo r0 = org.telegram.ui.ActionBar.Theme.getTheme(r2)
            org.telegram.ui.Components.RLottieDrawable r2 = r6.sunDrawable
            r2.setCustomEndFrame(r1)
        L_0x0084:
            org.telegram.ui.Components.RLottieImageView r2 = r6.darkThemeView
            r2.playAnimation()
            int r2 = org.telegram.ui.ActionBar.Theme.selectedAutoNightType
            if (r2 == 0) goto L_0x00a8
            android.content.Context r2 = r6.getContext()
            int r3 = org.telegram.messenger.R.string.AutoNightModeOff
            java.lang.String r4 = "AutoNightModeOff"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            android.widget.Toast r2 = android.widget.Toast.makeText(r2, r3, r1)
            r2.show()
            org.telegram.ui.ActionBar.Theme.selectedAutoNightType = r1
            org.telegram.ui.ActionBar.Theme.saveAutoNightThemeConfig()
            org.telegram.ui.ActionBar.Theme.cancelAutoNightThemeCallbacks()
        L_0x00a8:
            r6.switchTheme(r0, r7)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.DrawerProfileCell.lambda$new$1(android.view.View):void");
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$new$2(DrawerLayoutContainer drawerLayoutContainer, View view) {
        if (drawerLayoutContainer == null) {
            return false;
        }
        drawerLayoutContainer.presentFragment(new ThemeActivity(0));
        return true;
    }

    public static class AnimatedStatusView extends View {
        private int animationUniq;
        private ArrayList<Object> animations = new ArrayList<>();
        private Integer color;
        private int effectsSize;
        private int renderedEffectsSize;
        private int stateSize;
        private float y1;
        private float y2;

        public AnimatedStatusView(Context context, int i, int i2) {
            super(context);
            this.stateSize = i;
            this.effectsSize = i2;
            this.renderedEffectsSize = i2;
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp((float) Math.max(this.renderedEffectsSize, Math.max(this.stateSize, this.effectsSize))), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp((float) Math.max(this.renderedEffectsSize, Math.max(this.stateSize, this.effectsSize))), NUM));
        }

        public void translate(int i, int i2) {
            setTranslationX(((float) i) - (((float) getMeasuredWidth()) / 2.0f));
            float measuredHeight = ((float) i2) - (((float) getMeasuredHeight()) / 2.0f);
            this.y1 = measuredHeight;
            setTranslationY(measuredHeight + this.y2);
        }

        public void translateY2(int i) {
            float f = this.y1;
            float f2 = (float) i;
            this.y2 = f2;
            setTranslationY(f + f2);
        }

        public void dispatchDraw(Canvas canvas) {
            int dp = AndroidUtilities.dp((float) this.renderedEffectsSize);
            int dp2 = AndroidUtilities.dp((float) this.effectsSize);
            for (int i = 0; i < this.animations.size(); i++) {
                Object obj = this.animations.get(i);
                if (obj instanceof ImageReceiver) {
                    ImageReceiver imageReceiver = (ImageReceiver) obj;
                    float f = (float) dp2;
                    imageReceiver.setImageCoords(((float) (getMeasuredWidth() - dp2)) / 2.0f, ((float) (getMeasuredHeight() - dp2)) / 2.0f, f, f);
                    imageReceiver.draw(canvas);
                    if (imageReceiver.getLottieAnimation() != null && imageReceiver.getLottieAnimation().isRunning() && imageReceiver.getLottieAnimation().isLastFrame()) {
                        imageReceiver.onDetachedFromWindow();
                        this.animations.remove(imageReceiver);
                    }
                } else if (obj instanceof AnimatedEmojiEffect) {
                    AnimatedEmojiEffect animatedEmojiEffect = (AnimatedEmojiEffect) obj;
                    animatedEmojiEffect.setBounds((int) (((float) (getMeasuredWidth() - dp)) / 2.0f), (int) (((float) (getMeasuredHeight() - dp)) / 2.0f), (int) (((float) (getMeasuredWidth() + dp)) / 2.0f), (int) (((float) (getMeasuredHeight() + dp)) / 2.0f));
                    animatedEmojiEffect.draw(canvas);
                    if (animatedEmojiEffect.done()) {
                        animatedEmojiEffect.removeView(this);
                        this.animations.remove(animatedEmojiEffect);
                    }
                }
            }
        }

        /* access modifiers changed from: protected */
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            detach();
        }

        private void detach() {
            if (!this.animations.isEmpty()) {
                Iterator<Object> it = this.animations.iterator();
                while (it.hasNext()) {
                    Object next = it.next();
                    if (next instanceof ImageReceiver) {
                        ((ImageReceiver) next).onDetachedFromWindow();
                    } else if (next instanceof AnimatedEmojiEffect) {
                        ((AnimatedEmojiEffect) next).removeView(this);
                    }
                }
            }
            this.animations.clear();
        }

        public void animateChange(ReactionsLayoutInBubble.VisibleReaction visibleReaction) {
            TLRPC$TL_availableReaction tLRPC$TL_availableReaction;
            AnimatedEmojiDrawable animatedEmojiDrawable;
            String findAnimatedEmojiEmoticon;
            if (visibleReaction == null) {
                detach();
                return;
            }
            TLRPC$Document tLRPC$Document = null;
            TLRPC$TL_availableReaction tLRPC$TL_availableReaction2 = visibleReaction.emojicon != null ? MediaDataController.getInstance(UserConfig.selectedAccount).getReactionsMap().get(visibleReaction.emojicon) : null;
            if (tLRPC$TL_availableReaction2 == null) {
                TLRPC$Document findDocument = AnimatedEmojiDrawable.findDocument(UserConfig.selectedAccount, visibleReaction.documentId);
                if (!(findDocument == null || (findAnimatedEmojiEmoticon = MessageObject.findAnimatedEmojiEmoticon(findDocument, (String) null)) == null)) {
                    tLRPC$TL_availableReaction2 = MediaDataController.getInstance(UserConfig.selectedAccount).getReactionsMap().get(findAnimatedEmojiEmoticon);
                }
                tLRPC$TL_availableReaction = tLRPC$TL_availableReaction2;
                tLRPC$Document = findDocument;
            } else {
                tLRPC$TL_availableReaction = tLRPC$TL_availableReaction2;
            }
            if (tLRPC$TL_availableReaction != null) {
                ImageReceiver imageReceiver = new ImageReceiver();
                imageReceiver.setParentView(this);
                int i = this.animationUniq;
                this.animationUniq = i + 1;
                imageReceiver.setUniqKeyPrefix(Integer.toString(i));
                ImageLocation forDocument = ImageLocation.getForDocument(tLRPC$TL_availableReaction.around_animation);
                imageReceiver.setImage(forDocument, this.effectsSize + "_" + this.effectsSize + "_nolimit", (Drawable) null, "tgs", tLRPC$TL_availableReaction, 1);
                imageReceiver.setAutoRepeat(0);
                imageReceiver.onAttachedToWindow();
                this.animations.add(imageReceiver);
                invalidate();
                return;
            }
            if (tLRPC$Document == null) {
                animatedEmojiDrawable = AnimatedEmojiDrawable.make(2, UserConfig.selectedAccount, visibleReaction.documentId);
            } else {
                animatedEmojiDrawable = AnimatedEmojiDrawable.make(2, UserConfig.selectedAccount, tLRPC$Document);
            }
            if (this.color != null) {
                animatedEmojiDrawable.setColorFilter(new PorterDuffColorFilter(this.color.intValue(), PorterDuff.Mode.MULTIPLY));
            }
            AnimatedEmojiEffect createFrom = AnimatedEmojiEffect.createFrom(animatedEmojiDrawable, false, !animatedEmojiDrawable.canOverrideColor());
            createFrom.setView(this);
            this.animations.add(createFrom);
            invalidate();
        }

        public void setColor(int i) {
            this.color = Integer.valueOf(i);
        }
    }

    public void animateStateChange(long j) {
        this.animatedStatus.animateChange(ReactionsLayoutInBubble.VisibleReaction.fromCustomEmoji(Long.valueOf(j)));
        this.updateRightDrawable = true;
    }

    public void getEmojiStatusLocation(Rect rect) {
        if (this.nameTextView.getRightDrawable() == null) {
            rect.set(this.nameTextView.getWidth() - 1, (this.nameTextView.getHeight() / 2) - 1, this.nameTextView.getWidth() + 1, (this.nameTextView.getHeight() / 2) + 1);
            return;
        }
        rect.set(this.nameTextView.getRightDrawable().getBounds());
        rect.offset((int) this.nameTextView.getX(), (int) this.nameTextView.getY());
        this.animatedStatus.translate(rect.centerX(), rect.centerY());
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v1, resolved type: java.lang.Object[]} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void switchTheme(org.telegram.ui.ActionBar.Theme.ThemeInfo r8, boolean r9) {
        /*
            r7 = this;
            r0 = 2
            int[] r1 = new int[r0]
            org.telegram.ui.Components.RLottieImageView r2 = r7.darkThemeView
            r2.getLocationInWindow(r1)
            r2 = 0
            r3 = r1[r2]
            org.telegram.ui.Components.RLottieImageView r4 = r7.darkThemeView
            int r4 = r4.getMeasuredWidth()
            int r4 = r4 / r0
            int r3 = r3 + r4
            r1[r2] = r3
            r3 = 1
            r4 = r1[r3]
            org.telegram.ui.Components.RLottieImageView r5 = r7.darkThemeView
            int r5 = r5.getMeasuredHeight()
            int r5 = r5 / r0
            int r4 = r4 + r5
            r1[r3] = r4
            org.telegram.messenger.NotificationCenter r4 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r5 = org.telegram.messenger.NotificationCenter.needSetDayNightTheme
            r6 = 6
            java.lang.Object[] r6 = new java.lang.Object[r6]
            r6[r2] = r8
            java.lang.Boolean r8 = java.lang.Boolean.FALSE
            r6[r3] = r8
            r6[r0] = r1
            r8 = -1
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            r0 = 3
            r6[r0] = r8
            java.lang.Boolean r8 = java.lang.Boolean.valueOf(r9)
            r9 = 4
            r6[r9] = r8
            org.telegram.ui.Components.RLottieImageView r8 = r7.darkThemeView
            r9 = 5
            r6[r9] = r8
            r4.postNotificationName(r5, r6)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.DrawerProfileCell.switchTheme(org.telegram.ui.ActionBar.Theme$ThemeInfo, boolean):void");
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        updateColors();
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
        for (int i = 0; i < 4; i++) {
            NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.currentUserPremiumStatusChanged);
        }
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
        for (int i = 0; i < 4; i++) {
            NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.currentUserPremiumStatusChanged);
        }
        int i2 = this.lastAccount;
        if (i2 >= 0) {
            NotificationCenter.getInstance(i2).removeObserver(this, NotificationCenter.userEmojiStatusUpdated);
            this.lastAccount = -1;
        }
        if (this.nameTextView.getRightDrawable() instanceof AnimatedEmojiDrawable.WrapSizeDrawable) {
            Drawable drawable = ((AnimatedEmojiDrawable.WrapSizeDrawable) this.nameTextView.getRightDrawable()).getDrawable();
            if (drawable instanceof AnimatedEmojiDrawable) {
                ((AnimatedEmojiDrawable) drawable).removeView((Drawable.Callback) this.nameTextView);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        if (Build.VERSION.SDK_INT >= 21) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(148.0f) + AndroidUtilities.statusBarHeight, NUM));
            return;
        }
        try {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(148.0f), NUM));
        } catch (Exception e) {
            setMeasuredDimension(View.MeasureSpec.getSize(i), AndroidUtilities.dp(148.0f));
            FileLog.e((Throwable) e);
        }
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        if (this.drawPremium) {
            if (this.starParticlesDrawable == null) {
                StarParticlesView.Drawable drawable = new StarParticlesView.Drawable(15);
                this.starParticlesDrawable = drawable;
                drawable.init();
                StarParticlesView.Drawable drawable2 = this.starParticlesDrawable;
                drawable2.speedScale = 0.8f;
                drawable2.minLifeTime = 3000;
            }
            this.starParticlesDrawable.rect.set((float) this.avatarImageView.getLeft(), (float) this.avatarImageView.getTop(), (float) this.avatarImageView.getRight(), (float) this.avatarImageView.getBottom());
            this.starParticlesDrawable.rect.inset((float) (-AndroidUtilities.dp(20.0f)), (float) (-AndroidUtilities.dp(20.0f)));
            this.starParticlesDrawable.resetPositions();
        }
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x01b7  */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x021e  */
    /* JADX WARNING: Removed duplicated region for block: B:78:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onDraw(android.graphics.Canvas r12) {
        /*
            r11 = this;
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.getCachedWallpaper()
            r1 = 0
            java.lang.String r2 = r11.applyBackground(r1)
            java.lang.String r3 = "chats_menuTopBackground"
            boolean r2 = r2.equals(r3)
            r3 = 1
            if (r2 != 0) goto L_0x002a
            boolean r2 = org.telegram.ui.ActionBar.Theme.isCustomTheme()
            if (r2 == 0) goto L_0x002a
            boolean r2 = org.telegram.ui.ActionBar.Theme.isPatternWallpaper()
            if (r2 != 0) goto L_0x002a
            if (r0 == 0) goto L_0x002a
            boolean r2 = r0 instanceof android.graphics.drawable.ColorDrawable
            if (r2 != 0) goto L_0x002a
            boolean r2 = r0 instanceof android.graphics.drawable.GradientDrawable
            if (r2 != 0) goto L_0x002a
            r2 = 1
            goto L_0x002b
        L_0x002a:
            r2 = 0
        L_0x002b:
            if (r2 != 0) goto L_0x003b
            java.lang.String r4 = "chats_menuTopShadowCats"
            boolean r5 = org.telegram.ui.ActionBar.Theme.hasThemeKey(r4)
            if (r5 == 0) goto L_0x003b
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            r5 = 1
            goto L_0x0050
        L_0x003b:
            java.lang.String r4 = "chats_menuTopShadow"
            boolean r5 = org.telegram.ui.ActionBar.Theme.hasThemeKey(r4)
            if (r5 == 0) goto L_0x0048
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            goto L_0x004f
        L_0x0048:
            int r4 = org.telegram.ui.ActionBar.Theme.getServiceMessageColor()
            r5 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r4 = r4 | r5
        L_0x004f:
            r5 = 0
        L_0x0050:
            java.lang.Integer r6 = r11.currentColor
            if (r6 == 0) goto L_0x005a
            int r6 = r6.intValue()
            if (r6 == r4) goto L_0x0070
        L_0x005a:
            java.lang.Integer r6 = java.lang.Integer.valueOf(r4)
            r11.currentColor = r6
            android.widget.ImageView r6 = r11.shadowView
            android.graphics.drawable.Drawable r6 = r6.getDrawable()
            android.graphics.PorterDuffColorFilter r7 = new android.graphics.PorterDuffColorFilter
            android.graphics.PorterDuff$Mode r8 = android.graphics.PorterDuff.Mode.MULTIPLY
            r7.<init>(r4, r8)
            r6.setColorFilter(r7)
        L_0x0070:
            java.lang.String r4 = "chats_menuName"
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            java.lang.Integer r7 = r11.currentMoonColor
            if (r7 == 0) goto L_0x0080
            int r7 = r7.intValue()
            if (r7 == r6) goto L_0x00c4
        L_0x0080:
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            r11.currentMoonColor = r6
            org.telegram.ui.Components.RLottieDrawable r6 = r11.sunDrawable
            r6.beginApplyLayerColors()
            org.telegram.ui.Components.RLottieDrawable r6 = r11.sunDrawable
            java.lang.Integer r7 = r11.currentMoonColor
            int r7 = r7.intValue()
            java.lang.String r8 = "Sunny.**"
            r6.setLayerColor(r8, r7)
            org.telegram.ui.Components.RLottieDrawable r6 = r11.sunDrawable
            java.lang.Integer r7 = r11.currentMoonColor
            int r7 = r7.intValue()
            java.lang.String r8 = "Path 6.**"
            r6.setLayerColor(r8, r7)
            org.telegram.ui.Components.RLottieDrawable r6 = r11.sunDrawable
            java.lang.Integer r7 = r11.currentMoonColor
            int r7 = r7.intValue()
            java.lang.String r8 = "Path.**"
            r6.setLayerColor(r8, r7)
            org.telegram.ui.Components.RLottieDrawable r6 = r11.sunDrawable
            java.lang.Integer r7 = r11.currentMoonColor
            int r7 = r7.intValue()
            java.lang.String r8 = "Path 5.**"
            r6.setLayerColor(r8, r7)
            org.telegram.ui.Components.RLottieDrawable r6 = r11.sunDrawable
            r6.commitApplyLayerColors()
        L_0x00c4:
            org.telegram.ui.ActionBar.SimpleTextView r6 = r11.nameTextView
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            r6.setTextColor(r4)
            java.lang.String r4 = "listSelectorSDK21"
            if (r2 == 0) goto L_0x016a
            android.widget.TextView r2 = r11.phoneTextView
            java.lang.String r5 = "chats_menuPhone"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r2.setTextColor(r5)
            android.widget.ImageView r2 = r11.shadowView
            int r2 = r2.getVisibility()
            if (r2 == 0) goto L_0x00e9
            android.widget.ImageView r2 = r11.shadowView
            r2.setVisibility(r1)
        L_0x00e9:
            boolean r2 = r0 instanceof android.graphics.drawable.ColorDrawable
            if (r2 != 0) goto L_0x0158
            boolean r2 = r0 instanceof android.graphics.drawable.GradientDrawable
            if (r2 == 0) goto L_0x00f2
            goto L_0x0158
        L_0x00f2:
            boolean r2 = r0 instanceof android.graphics.drawable.BitmapDrawable
            if (r2 == 0) goto L_0x018c
            android.graphics.drawable.BitmapDrawable r0 = (android.graphics.drawable.BitmapDrawable) r0
            android.graphics.Bitmap r0 = r0.getBitmap()
            int r2 = r11.getMeasuredWidth()
            float r2 = (float) r2
            int r4 = r0.getWidth()
            float r4 = (float) r4
            float r2 = r2 / r4
            int r4 = r11.getMeasuredHeight()
            float r4 = (float) r4
            int r5 = r0.getHeight()
            float r5 = (float) r5
            float r4 = r4 / r5
            float r2 = java.lang.Math.max(r2, r4)
            int r4 = r11.getMeasuredWidth()
            float r4 = (float) r4
            float r4 = r4 / r2
            int r4 = (int) r4
            int r5 = r11.getMeasuredHeight()
            float r5 = (float) r5
            float r5 = r5 / r2
            int r2 = (int) r5
            int r5 = r0.getWidth()
            int r5 = r5 - r4
            int r5 = r5 / 2
            int r6 = r0.getHeight()
            int r6 = r6 - r2
            int r6 = r6 / 2
            android.graphics.Rect r7 = r11.srcRect
            int r4 = r4 + r5
            int r2 = r2 + r6
            r7.set(r5, r6, r4, r2)
            android.graphics.Rect r2 = r11.destRect
            int r4 = r11.getMeasuredWidth()
            int r5 = r11.getMeasuredHeight()
            r2.set(r1, r1, r4, r5)
            android.graphics.Rect r1 = r11.srcRect     // Catch:{ all -> 0x0150 }
            android.graphics.Rect r2 = r11.destRect     // Catch:{ all -> 0x0150 }
            android.graphics.Paint r4 = r11.paint     // Catch:{ all -> 0x0150 }
            r12.drawBitmap(r0, r1, r2, r4)     // Catch:{ all -> 0x0150 }
            goto L_0x0154
        L_0x0150:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0154:
            org.telegram.ui.ActionBar.Theme.getServiceMessageColor()
            goto L_0x018c
        L_0x0158:
            int r2 = r11.getMeasuredWidth()
            int r5 = r11.getMeasuredHeight()
            r0.setBounds(r1, r1, r2, r5)
            r0.draw(r12)
            org.telegram.ui.ActionBar.Theme.getColor(r4)
            goto L_0x018c
        L_0x016a:
            if (r5 == 0) goto L_0x016d
            goto L_0x016e
        L_0x016d:
            r1 = 4
        L_0x016e:
            android.widget.ImageView r0 = r11.shadowView
            int r0 = r0.getVisibility()
            if (r0 == r1) goto L_0x017b
            android.widget.ImageView r0 = r11.shadowView
            r0.setVisibility(r1)
        L_0x017b:
            android.widget.TextView r0 = r11.phoneTextView
            java.lang.String r1 = "chats_menuPhoneCats"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setTextColor(r1)
            super.onDraw(r12)
            org.telegram.ui.ActionBar.Theme.getColor(r4)
        L_0x018c:
            boolean r0 = r11.drawPremium
            r1 = 1033171465(0x3d94var_, float:0.07272727)
            r2 = 1065353216(0x3var_, float:1.0)
            r4 = 0
            if (r0 == 0) goto L_0x01a0
            float r5 = r11.drawPremiumProgress
            int r6 = (r5 > r2 ? 1 : (r5 == r2 ? 0 : -1))
            if (r6 == 0) goto L_0x01a0
            float r5 = r5 + r1
            r11.drawPremiumProgress = r5
            goto L_0x01ab
        L_0x01a0:
            if (r0 != 0) goto L_0x01ab
            float r0 = r11.drawPremiumProgress
            int r5 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r5 == 0) goto L_0x01ab
            float r0 = r0 - r1
            r11.drawPremiumProgress = r0
        L_0x01ab:
            float r0 = r11.drawPremiumProgress
            float r0 = org.telegram.messenger.Utilities.clamp((float) r0, (float) r2, (float) r4)
            r11.drawPremiumProgress = r0
            int r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r0 == 0) goto L_0x021a
            org.telegram.ui.Components.Premium.PremiumGradient$GradientTools r0 = r11.gradientTools
            if (r0 != 0) goto L_0x01db
            org.telegram.ui.Components.Premium.PremiumGradient$GradientTools r0 = new org.telegram.ui.Components.Premium.PremiumGradient$GradientTools
            r1 = 0
            java.lang.String r2 = "premiumGradientBottomSheet1"
            java.lang.String r5 = "premiumGradientBottomSheet2"
            java.lang.String r6 = "premiumGradientBottomSheet3"
            r0.<init>(r2, r5, r6, r1)
            r11.gradientTools = r0
            r0.x1 = r4
            r1 = 1066192077(0x3f8ccccd, float:1.1)
            r0.y1 = r1
            r1 = 1069547520(0x3fCLASSNAME, float:1.5)
            r0.x2 = r1
            r1 = -1102263091(0xffffffffbe4ccccd, float:-0.2)
            r0.y2 = r1
            r0.exactly = r3
        L_0x01db:
            org.telegram.ui.Components.Premium.PremiumGradient$GradientTools r4 = r11.gradientTools
            r5 = 0
            r6 = 0
            int r7 = r11.getMeasuredWidth()
            int r8 = r11.getMeasuredHeight()
            r9 = 0
            r10 = 0
            r4.gradientMatrix(r5, r6, r7, r8, r9, r10)
            org.telegram.ui.Components.Premium.PremiumGradient$GradientTools r0 = r11.gradientTools
            android.graphics.Paint r0 = r0.paint
            float r1 = r11.drawPremiumProgress
            r2 = 1132396544(0x437var_, float:255.0)
            float r1 = r1 * r2
            int r1 = (int) r1
            r0.setAlpha(r1)
            r3 = 0
            r4 = 0
            int r0 = r11.getMeasuredWidth()
            float r5 = (float) r0
            int r0 = r11.getMeasuredHeight()
            float r6 = (float) r0
            org.telegram.ui.Components.Premium.PremiumGradient$GradientTools r0 = r11.gradientTools
            android.graphics.Paint r7 = r0.paint
            r2 = r12
            r2.drawRect(r3, r4, r5, r6, r7)
            org.telegram.ui.Components.Premium.StarParticlesView$Drawable r0 = r11.starParticlesDrawable
            if (r0 == 0) goto L_0x0217
            float r1 = r11.drawPremiumProgress
            r0.onDraw(r12, r1)
        L_0x0217:
            r11.invalidate()
        L_0x021a:
            org.telegram.ui.Components.SnowflakesEffect r0 = r11.snowflakesEffect
            if (r0 == 0) goto L_0x0221
            r0.onDraw(r11, r12)
        L_0x0221:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.DrawerProfileCell.onDraw(android.graphics.Canvas):void");
    }

    public boolean isInAvatar(float f, float f2) {
        return f >= ((float) this.avatarImageView.getLeft()) && f <= ((float) this.avatarImageView.getRight()) && f2 >= ((float) this.avatarImageView.getTop()) && f2 <= ((float) this.avatarImageView.getBottom());
    }

    public boolean hasAvatar() {
        return this.avatarImageView.getImageReceiver().hasNotThumb();
    }

    public void setAccountsShown(boolean z, boolean z2) {
        if (this.accountsShown != z) {
            this.accountsShown = z;
            setArrowState(z2);
        }
    }

    public void setUser(TLRPC$User tLRPC$User, boolean z) {
        int i = UserConfig.selectedAccount;
        int i2 = this.lastAccount;
        if (i != i2) {
            if (i2 >= 0) {
                NotificationCenter.getInstance(i2).removeObserver(this, NotificationCenter.userEmojiStatusUpdated);
            }
            this.lastAccount = i;
            NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.userEmojiStatusUpdated);
        }
        this.lastUser = tLRPC$User;
        if (tLRPC$User != null) {
            this.accountsShown = z;
            setArrowState(false);
            CharSequence userName = UserObject.getUserName(tLRPC$User);
            try {
                userName = Emoji.replaceEmoji(userName, this.nameTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(22.0f), false);
            } catch (Exception unused) {
            }
            this.drawPremium = false;
            this.nameTextView.setText(userName);
            String str = "chats_menuPhoneCats";
            if (tLRPC$User.emoji_status instanceof TLRPC$TL_emojiStatus) {
                this.animatedStatus.animate().alpha(1.0f).setDuration(200).start();
                this.nameTextView.setDrawablePadding(AndroidUtilities.dp(4.0f));
                this.status.set(((TLRPC$TL_emojiStatus) tLRPC$User.emoji_status).document_id, true);
            } else if (tLRPC$User.premium) {
                this.animatedStatus.animate().alpha(1.0f).setDuration(200).start();
                this.nameTextView.setDrawablePadding(AndroidUtilities.dp(4.0f));
                if (this.premiumStar == null) {
                    this.premiumStar = getResources().getDrawable(R.drawable.msg_premium_liststar).mutate();
                }
                this.premiumStar.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str), PorterDuff.Mode.MULTIPLY));
                this.status.set(this.premiumStar, true);
            } else {
                this.animatedStatus.animateChange((ReactionsLayoutInBubble.VisibleReaction) null);
                this.animatedStatus.animate().alpha(0.0f).setDuration(200).start();
                this.status.set((Drawable) null, true);
            }
            this.animatedStatus.setColor(Theme.getColor(Theme.isCurrentThemeDark() ? "chats_verifiedBackground" : str));
            AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable swapAnimatedEmojiDrawable = this.status;
            if (Theme.isCurrentThemeDark()) {
                str = "chats_verifiedBackground";
            }
            swapAnimatedEmojiDrawable.setColor(Integer.valueOf(Theme.getColor(str)));
            TextView textView = this.phoneTextView;
            PhoneFormat instance = PhoneFormat.getInstance();
            textView.setText(instance.format("+" + tLRPC$User.phone));
            AvatarDrawable avatarDrawable = new AvatarDrawable(tLRPC$User);
            avatarDrawable.setColor(Theme.getColor("avatar_backgroundInProfileBlue"));
            this.avatarImageView.setForUserOrChat(tLRPC$User, avatarDrawable);
            applyBackground(true);
            this.updateRightDrawable = true;
        }
    }

    public String applyBackground(boolean z) {
        String str = (String) getTag();
        String str2 = "chats_menuTopBackground";
        if (!Theme.hasThemeKey(str2) || Theme.getColor(str2) == 0) {
            str2 = "chats_menuTopBackgroundCats";
        }
        if (z || !str2.equals(str)) {
            setBackgroundColor(Theme.getColor(str2));
            setTag(str2);
        }
        return str2;
    }

    public void updateColors() {
        String str;
        SnowflakesEffect snowflakesEffect2 = this.snowflakesEffect;
        if (snowflakesEffect2 != null) {
            snowflakesEffect2.updateColors();
        }
        AnimatedStatusView animatedStatusView = this.animatedStatus;
        String str2 = "chats_verifiedBackground";
        if (animatedStatusView != null) {
            if (Theme.isCurrentThemeDark()) {
                str = str2;
            } else {
                str = "chats_menuPhoneCats";
            }
            animatedStatusView.setColor(Theme.getColor(str));
        }
        AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable swapAnimatedEmojiDrawable = this.status;
        if (swapAnimatedEmojiDrawable != null) {
            if (!Theme.isCurrentThemeDark()) {
                str2 = "chats_menuPhoneCats";
            }
            swapAnimatedEmojiDrawable.setColor(Integer.valueOf(Theme.getColor(str2)));
        }
    }

    private void setArrowState(boolean z) {
        String str;
        int i;
        float f = this.accountsShown ? 180.0f : 0.0f;
        if (z) {
            this.arrowView.animate().rotation(f).setDuration(220).setInterpolator(CubicBezierInterpolator.EASE_OUT).start();
        } else {
            this.arrowView.animate().cancel();
            this.arrowView.setRotation(f);
        }
        ImageView imageView = this.arrowView;
        if (this.accountsShown) {
            i = R.string.AccDescrHideAccounts;
            str = "AccDescrHideAccounts";
        } else {
            i = R.string.AccDescrShowAccounts;
            str = "AccDescrShowAccounts";
        }
        imageView.setContentDescription(LocaleController.getString(str, i));
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.emojiLoaded) {
            this.nameTextView.invalidate();
        } else if (i == NotificationCenter.userEmojiStatusUpdated) {
            setUser(objArr[0], this.accountsShown);
        } else if (i == NotificationCenter.currentUserPremiumStatusChanged) {
            setUser(UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser(), this.accountsShown);
        }
    }

    public AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable getEmojiStatusDrawable() {
        return this.status;
    }

    public View getEmojiStatusDrawableParent() {
        return this.nameTextView;
    }
}
