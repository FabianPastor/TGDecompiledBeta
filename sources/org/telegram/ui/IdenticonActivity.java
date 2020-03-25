package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.EmojiData;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC$EncryptedChat;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.IdenticonDrawable;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.URLSpanReplacement;

public class IdenticonActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    /* access modifiers changed from: private */
    public AnimatorSet animatorSet;
    private int chat_id;
    /* access modifiers changed from: private */
    public TextView codeTextView;
    private FrameLayout container;
    private boolean emojiSelected;
    private String emojiText;
    /* access modifiers changed from: private */
    public TextView emojiTextView;
    /* access modifiers changed from: private */
    public LinearLayout linearLayout;
    /* access modifiers changed from: private */
    public LinearLayout linearLayout1;
    private TextView textView;

    static /* synthetic */ boolean lambda$createView$0(View view, MotionEvent motionEvent) {
        return true;
    }

    private static class LinkMovementMethodMy extends LinkMovementMethod {
        private LinkMovementMethodMy() {
        }

        public boolean onTouchEvent(TextView textView, Spannable spannable, MotionEvent motionEvent) {
            try {
                return super.onTouchEvent(textView, spannable, motionEvent);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
                return false;
            }
        }
    }

    public IdenticonActivity(Bundle bundle) {
        super(bundle);
    }

    public boolean onFragmentCreate() {
        this.chat_id = getArguments().getInt("chat_id");
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiDidLoad);
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiDidLoad);
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("EncryptionKey", NUM));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    IdenticonActivity.this.finishFragment();
                }
            }
        });
        FrameLayout frameLayout = new FrameLayout(context);
        this.fragmentView = frameLayout;
        frameLayout.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        this.fragmentView.setOnTouchListener($$Lambda$IdenticonActivity$Yvzzx489TCib4oTluPwFgAoS_54.INSTANCE);
        LinearLayout linearLayout2 = new LinearLayout(context);
        this.linearLayout = linearLayout2;
        linearLayout2.setOrientation(1);
        this.linearLayout.setWeightSum(100.0f);
        frameLayout.addView(this.linearLayout, LayoutHelper.createFrame(-1, -1.0f));
        FrameLayout frameLayout2 = new FrameLayout(context);
        frameLayout2.setPadding(AndroidUtilities.dp(20.0f), AndroidUtilities.dp(20.0f), AndroidUtilities.dp(20.0f), AndroidUtilities.dp(20.0f));
        this.linearLayout.addView(frameLayout2, LayoutHelper.createLinear(-1, -1, 50.0f));
        ImageView imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        frameLayout2.addView(imageView, LayoutHelper.createFrame(-1, -1.0f));
        AnonymousClass2 r0 = new FrameLayout(context) {
            /* access modifiers changed from: protected */
            public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                super.onLayout(z, i, i2, i3, i4);
                if (IdenticonActivity.this.codeTextView != null) {
                    int left = (IdenticonActivity.this.codeTextView.getLeft() + (IdenticonActivity.this.codeTextView.getMeasuredWidth() / 2)) - (IdenticonActivity.this.emojiTextView.getMeasuredWidth() / 2);
                    int measuredHeight = (((IdenticonActivity.this.codeTextView.getMeasuredHeight() - IdenticonActivity.this.emojiTextView.getMeasuredHeight()) / 2) + IdenticonActivity.this.linearLayout1.getTop()) - AndroidUtilities.dp(16.0f);
                    IdenticonActivity.this.emojiTextView.layout(left, measuredHeight, IdenticonActivity.this.emojiTextView.getMeasuredWidth() + left, IdenticonActivity.this.emojiTextView.getMeasuredHeight() + measuredHeight);
                }
            }
        };
        this.container = r0;
        r0.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        this.linearLayout.addView(this.container, LayoutHelper.createLinear(-1, -1, 50.0f));
        LinearLayout linearLayout3 = new LinearLayout(context);
        this.linearLayout1 = linearLayout3;
        linearLayout3.setOrientation(1);
        this.linearLayout1.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
        this.container.addView(this.linearLayout1, LayoutHelper.createFrame(-2, -2, 17));
        TextView textView2 = new TextView(context);
        this.codeTextView = textView2;
        textView2.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText4"));
        this.codeTextView.setGravity(17);
        this.codeTextView.setTypeface(Typeface.MONOSPACE);
        this.codeTextView.setTextSize(1, 16.0f);
        this.linearLayout1.addView(this.codeTextView, LayoutHelper.createLinear(-2, -2, 1));
        TextView textView3 = new TextView(context);
        this.textView = textView3;
        textView3.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText4"));
        this.textView.setLinkTextColor(Theme.getColor("windowBackgroundWhiteLinkText"));
        this.textView.setTextSize(1, 16.0f);
        this.textView.setLinksClickable(true);
        this.textView.setClickable(true);
        this.textView.setGravity(17);
        this.textView.setMovementMethod(new LinkMovementMethodMy());
        this.linearLayout1.addView(this.textView, LayoutHelper.createFrame(-2, -2, 1));
        TextView textView4 = new TextView(context);
        this.emojiTextView = textView4;
        textView4.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText4"));
        this.emojiTextView.setGravity(17);
        this.emojiTextView.setTextSize(1, 32.0f);
        this.container.addView(this.emojiTextView, LayoutHelper.createFrame(-2, -2.0f));
        TLRPC$EncryptedChat encryptedChat = MessagesController.getInstance(this.currentAccount).getEncryptedChat(Integer.valueOf(this.chat_id));
        if (encryptedChat != null) {
            IdenticonDrawable identiconDrawable = new IdenticonDrawable();
            imageView.setImageDrawable(identiconDrawable);
            identiconDrawable.setEncryptedChat(encryptedChat);
            TLRPC$User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(encryptedChat.user_id));
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
            StringBuilder sb = new StringBuilder();
            byte[] bArr = encryptedChat.key_hash;
            if (bArr.length > 16) {
                String bytesToHex = Utilities.bytesToHex(bArr);
                for (int i = 0; i < 32; i++) {
                    if (i != 0) {
                        if (i % 8 == 0) {
                            spannableStringBuilder.append(10);
                        } else if (i % 4 == 0) {
                            spannableStringBuilder.append(' ');
                        }
                    }
                    int i2 = i * 2;
                    spannableStringBuilder.append(bytesToHex.substring(i2, i2 + 2));
                    spannableStringBuilder.append(' ');
                }
                spannableStringBuilder.append("\n");
                for (int i3 = 0; i3 < 5; i3++) {
                    byte[] bArr2 = encryptedChat.key_hash;
                    int i4 = (i3 * 4) + 16;
                    byte b = (bArr2[i4 + 3] & 255) | ((bArr2[i4] & Byte.MAX_VALUE) << 24) | ((bArr2[i4 + 1] & 255) << 16) | ((bArr2[i4 + 2] & 255) << 8);
                    if (i3 != 0) {
                        sb.append(" ");
                    }
                    String[] strArr = EmojiData.emojiSecret;
                    sb.append(strArr[b % strArr.length]);
                }
                this.emojiText = sb.toString();
            }
            this.codeTextView.setText(spannableStringBuilder.toString());
            spannableStringBuilder.clear();
            String str = user.first_name;
            spannableStringBuilder.append(AndroidUtilities.replaceTags(LocaleController.formatString("EncryptionKeyDescription", NUM, str, str)));
            int indexOf = spannableStringBuilder.toString().indexOf("telegram.org");
            if (indexOf != -1) {
                spannableStringBuilder.setSpan(new URLSpanReplacement(LocaleController.getString("EncryptionKeyLink", NUM)), indexOf, indexOf + 12, 33);
            }
            this.textView.setText(spannableStringBuilder);
        }
        updateEmojiButton(false);
        return this.fragmentView;
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        fixLayout();
    }

    public void onResume() {
        super.onResume();
        fixLayout();
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        TextView textView2;
        if (i == NotificationCenter.emojiDidLoad && (textView2 = this.emojiTextView) != null) {
            textView2.invalidate();
        }
    }

    private void updateEmojiButton(boolean z) {
        AnimatorSet animatorSet2 = this.animatorSet;
        if (animatorSet2 != null) {
            animatorSet2.cancel();
            this.animatorSet = null;
        }
        float f = 1.0f;
        if (z) {
            AnimatorSet animatorSet3 = new AnimatorSet();
            this.animatorSet = animatorSet3;
            Animator[] animatorArr = new Animator[6];
            TextView textView2 = this.emojiTextView;
            float[] fArr = new float[1];
            fArr[0] = this.emojiSelected ? 1.0f : 0.0f;
            animatorArr[0] = ObjectAnimator.ofFloat(textView2, "alpha", fArr);
            TextView textView3 = this.codeTextView;
            float[] fArr2 = new float[1];
            fArr2[0] = this.emojiSelected ? 0.0f : 1.0f;
            animatorArr[1] = ObjectAnimator.ofFloat(textView3, "alpha", fArr2);
            TextView textView4 = this.emojiTextView;
            float[] fArr3 = new float[1];
            fArr3[0] = this.emojiSelected ? 1.0f : 0.0f;
            animatorArr[2] = ObjectAnimator.ofFloat(textView4, "scaleX", fArr3);
            TextView textView5 = this.emojiTextView;
            float[] fArr4 = new float[1];
            fArr4[0] = this.emojiSelected ? 1.0f : 0.0f;
            animatorArr[3] = ObjectAnimator.ofFloat(textView5, "scaleY", fArr4);
            TextView textView6 = this.codeTextView;
            float[] fArr5 = new float[1];
            fArr5[0] = this.emojiSelected ? 0.0f : 1.0f;
            animatorArr[4] = ObjectAnimator.ofFloat(textView6, "scaleX", fArr5);
            TextView textView7 = this.codeTextView;
            float[] fArr6 = new float[1];
            if (this.emojiSelected) {
                f = 0.0f;
            }
            fArr6[0] = f;
            animatorArr[5] = ObjectAnimator.ofFloat(textView7, "scaleY", fArr6);
            animatorSet3.playTogether(animatorArr);
            this.animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (animator.equals(IdenticonActivity.this.animatorSet)) {
                        AnimatorSet unused = IdenticonActivity.this.animatorSet = null;
                    }
                }
            });
            this.animatorSet.setInterpolator(new DecelerateInterpolator());
            this.animatorSet.setDuration(150);
            this.animatorSet.start();
        } else {
            this.emojiTextView.setAlpha(this.emojiSelected ? 1.0f : 0.0f);
            this.codeTextView.setAlpha(this.emojiSelected ? 0.0f : 1.0f);
            this.emojiTextView.setScaleX(this.emojiSelected ? 1.0f : 0.0f);
            this.emojiTextView.setScaleY(this.emojiSelected ? 1.0f : 0.0f);
            this.codeTextView.setScaleX(this.emojiSelected ? 0.0f : 1.0f);
            TextView textView8 = this.codeTextView;
            if (this.emojiSelected) {
                f = 0.0f;
            }
            textView8.setScaleY(f);
        }
        this.emojiTextView.setTag(!this.emojiSelected ? "chat_emojiPanelIcon" : "chat_emojiPanelIconSelected");
    }

    private void fixLayout() {
        this.fragmentView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                if (IdenticonActivity.this.fragmentView == null) {
                    return true;
                }
                IdenticonActivity.this.fragmentView.getViewTreeObserver().removeOnPreDrawListener(this);
                int rotation = ((WindowManager) ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation();
                if (rotation == 3 || rotation == 1) {
                    IdenticonActivity.this.linearLayout.setOrientation(0);
                } else {
                    IdenticonActivity.this.linearLayout.setOrientation(1);
                }
                IdenticonActivity.this.fragmentView.setPadding(IdenticonActivity.this.fragmentView.getPaddingLeft(), 0, IdenticonActivity.this.fragmentView.getPaddingRight(), IdenticonActivity.this.fragmentView.getPaddingBottom());
                return true;
            }
        });
    }

    /* access modifiers changed from: protected */
    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        String str;
        if (z && !z2 && (str = this.emojiText) != null) {
            TextView textView2 = this.emojiTextView;
            textView2.setText(Emoji.replaceEmoji(str, textView2.getPaint().getFontMetricsInt(), AndroidUtilities.dp(32.0f), false));
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        return new ThemeDescription[]{new ThemeDescription(this.container, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"), new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"), new ThemeDescription(this.textView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"), new ThemeDescription(this.codeTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"), new ThemeDescription(this.textView, ThemeDescription.FLAG_LINKCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteLinkText")};
    }
}
