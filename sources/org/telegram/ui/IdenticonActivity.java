package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
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
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.IdenticonDrawable;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.URLSpanReplacement;

public class IdenticonActivity extends BaseFragment implements NotificationCenterDelegate {
    private AnimatorSet animatorSet;
    private int chat_id;
    private TextView codeTextView;
    private FrameLayout container;
    private boolean emojiSelected;
    private String emojiText;
    private TextView emojiTextView;
    private AnimatorSet hintAnimatorSet;
    private LinearLayout linearLayout;
    private LinearLayout linearLayout1;
    private TextView textView;
    private int textWidth;

    private static class LinkMovementMethodMy extends LinkMovementMethod {
        private LinkMovementMethodMy() {
        }

        /* synthetic */ LinkMovementMethodMy(AnonymousClass1 anonymousClass1) {
            this();
        }

        public boolean onTouchEvent(TextView textView, Spannable spannable, MotionEvent motionEvent) {
            try {
                return super.onTouchEvent(textView, spannable, motionEvent);
            } catch (Exception e) {
                FileLog.e(e);
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
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    IdenticonActivity.this.finishFragment();
                }
            }
        });
        this.fragmentView = new FrameLayout(context);
        View view = this.fragmentView;
        FrameLayout frameLayout = (FrameLayout) view;
        view.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        this.fragmentView.setOnTouchListener(-$$Lambda$IdenticonActivity$Yvzzx489TCib4oTluPwFgAoS_54.INSTANCE);
        this.linearLayout = new LinearLayout(context);
        this.linearLayout.setOrientation(1);
        this.linearLayout.setWeightSum(100.0f);
        frameLayout.addView(this.linearLayout, LayoutHelper.createFrame(-1, -1.0f));
        FrameLayout frameLayout2 = new FrameLayout(context);
        frameLayout2.setPadding(AndroidUtilities.dp(20.0f), AndroidUtilities.dp(20.0f), AndroidUtilities.dp(20.0f), AndroidUtilities.dp(20.0f));
        this.linearLayout.addView(frameLayout2, LayoutHelper.createLinear(-1, -1, 50.0f));
        ImageView imageView = new ImageView(context);
        imageView.setScaleType(ScaleType.FIT_XY);
        frameLayout2.addView(imageView, LayoutHelper.createFrame(-1, -1.0f));
        this.container = new FrameLayout(context) {
            /* Access modifiers changed, original: protected */
            public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                super.onLayout(z, i, i2, i3, i4);
                if (IdenticonActivity.this.codeTextView != null) {
                    int left = (IdenticonActivity.this.codeTextView.getLeft() + (IdenticonActivity.this.codeTextView.getMeasuredWidth() / 2)) - (IdenticonActivity.this.emojiTextView.getMeasuredWidth() / 2);
                    i = (((IdenticonActivity.this.codeTextView.getMeasuredHeight() - IdenticonActivity.this.emojiTextView.getMeasuredHeight()) / 2) + IdenticonActivity.this.linearLayout1.getTop()) - AndroidUtilities.dp(16.0f);
                    IdenticonActivity.this.emojiTextView.layout(left, i, IdenticonActivity.this.emojiTextView.getMeasuredWidth() + left, IdenticonActivity.this.emojiTextView.getMeasuredHeight() + i);
                }
            }
        };
        this.container.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        this.linearLayout.addView(this.container, LayoutHelper.createLinear(-1, -1, 50.0f));
        this.linearLayout1 = new LinearLayout(context);
        this.linearLayout1.setOrientation(1);
        this.linearLayout1.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
        this.container.addView(this.linearLayout1, LayoutHelper.createFrame(-2, -2, 17));
        this.codeTextView = new TextView(context);
        String str = "windowBackgroundWhiteGrayText4";
        this.codeTextView.setTextColor(Theme.getColor(str));
        this.codeTextView.setGravity(17);
        this.codeTextView.setTypeface(Typeface.MONOSPACE);
        this.codeTextView.setTextSize(1, 16.0f);
        this.linearLayout1.addView(this.codeTextView, LayoutHelper.createLinear(-2, -2, 1));
        this.textView = new TextView(context);
        this.textView.setTextColor(Theme.getColor(str));
        this.textView.setLinkTextColor(Theme.getColor("windowBackgroundWhiteLinkText"));
        this.textView.setTextSize(1, 16.0f);
        this.textView.setLinksClickable(true);
        this.textView.setClickable(true);
        this.textView.setGravity(17);
        this.textView.setMovementMethod(new LinkMovementMethodMy());
        this.linearLayout1.addView(this.textView, LayoutHelper.createFrame(-2, -2, 1));
        this.emojiTextView = new TextView(context);
        this.emojiTextView.setTextColor(Theme.getColor(str));
        this.emojiTextView.setGravity(17);
        this.emojiTextView.setTextSize(1, 32.0f);
        this.container.addView(this.emojiTextView, LayoutHelper.createFrame(-2, -2.0f));
        EncryptedChat encryptedChat = MessagesController.getInstance(this.currentAccount).getEncryptedChat(Integer.valueOf(this.chat_id));
        if (encryptedChat != null) {
            IdenticonDrawable identiconDrawable = new IdenticonDrawable();
            imageView.setImageDrawable(identiconDrawable);
            identiconDrawable.setEncryptedChat(encryptedChat);
            User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(encryptedChat.user_id));
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
            StringBuilder stringBuilder = new StringBuilder();
            byte[] bArr = encryptedChat.key_hash;
            if (bArr.length > 16) {
                int i;
                String bytesToHex = Utilities.bytesToHex(bArr);
                for (i = 0; i < 32; i++) {
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
                    i = (bArr2[i4 + 3] & 255) | ((((bArr2[i4] & 127) << 24) | ((bArr2[i4 + 1] & 255) << 16)) | ((bArr2[i4 + 2] & 255) << 8));
                    if (i3 != 0) {
                        stringBuilder.append(" ");
                    }
                    String[] strArr = EmojiData.emojiSecret;
                    stringBuilder.append(strArr[i % strArr.length]);
                }
                this.emojiText = stringBuilder.toString();
            }
            this.codeTextView.setText(spannableStringBuilder.toString());
            spannableStringBuilder.clear();
            r3 = new Object[2];
            String str2 = user.first_name;
            r3[0] = str2;
            r3[1] = str2;
            spannableStringBuilder.append(AndroidUtilities.replaceTags(LocaleController.formatString("EncryptionKeyDescription", NUM, r3)));
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
        if (i == NotificationCenter.emojiDidLoad) {
            TextView textView = this.emojiTextView;
            if (textView != null) {
                textView.invalidate();
            }
        }
    }

    private void updateEmojiButton(boolean z) {
        AnimatorSet animatorSet = this.animatorSet;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.animatorSet = null;
        }
        float f = 1.0f;
        if (z) {
            this.animatorSet = new AnimatorSet();
            AnimatorSet animatorSet2 = this.animatorSet;
            Animator[] animatorArr = new Animator[6];
            TextView textView = this.emojiTextView;
            float[] fArr = new float[1];
            fArr[0] = this.emojiSelected ? 1.0f : 0.0f;
            String str = "alpha";
            animatorArr[0] = ObjectAnimator.ofFloat(textView, str, fArr);
            textView = this.codeTextView;
            fArr = new float[1];
            fArr[0] = this.emojiSelected ? 0.0f : 1.0f;
            animatorArr[1] = ObjectAnimator.ofFloat(textView, str, fArr);
            TextView textView2 = this.emojiTextView;
            float[] fArr2 = new float[1];
            fArr2[0] = this.emojiSelected ? 1.0f : 0.0f;
            String str2 = "scaleX";
            animatorArr[2] = ObjectAnimator.ofFloat(textView2, str2, fArr2);
            textView2 = this.emojiTextView;
            fArr2 = new float[1];
            fArr2[0] = this.emojiSelected ? 1.0f : 0.0f;
            String str3 = "scaleY";
            animatorArr[3] = ObjectAnimator.ofFloat(textView2, str3, fArr2);
            textView2 = this.codeTextView;
            fArr2 = new float[1];
            fArr2[0] = this.emojiSelected ? 0.0f : 1.0f;
            animatorArr[4] = ObjectAnimator.ofFloat(textView2, str2, fArr2);
            textView2 = this.codeTextView;
            float[] fArr3 = new float[1];
            if (this.emojiSelected) {
                f = 0.0f;
            }
            fArr3[0] = f;
            animatorArr[5] = ObjectAnimator.ofFloat(textView2, str3, fArr3);
            animatorSet2.playTogether(animatorArr);
            this.animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (animator.equals(IdenticonActivity.this.animatorSet)) {
                        IdenticonActivity.this.animatorSet = null;
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
            TextView textView3 = this.codeTextView;
            if (this.emojiSelected) {
                f = 0.0f;
            }
            textView3.setScaleY(f);
        }
        this.emojiTextView.setTag(!this.emojiSelected ? "chat_emojiPanelIcon" : "chat_emojiPanelIconSelected");
    }

    private void fixLayout() {
        this.fragmentView.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
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

    /* Access modifiers changed, original: protected */
    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z && !z2) {
            String str = this.emojiText;
            if (str != null) {
                TextView textView = this.emojiTextView;
                textView.setText(Emoji.replaceEmoji(str, textView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(32.0f), false));
            }
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        return new ThemeDescription[]{new ThemeDescription(this.container, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"), new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector"), new ThemeDescription(this.textView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText4"), new ThemeDescription(this.codeTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText4"), new ThemeDescription(this.textView, ThemeDescription.FLAG_LINKCOLOR, null, null, null, null, "windowBackgroundWhiteLinkText")};
    }
}
