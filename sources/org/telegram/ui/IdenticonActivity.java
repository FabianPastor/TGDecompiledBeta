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
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.EmojiData;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC;
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
    private AnimatorSet hintAnimatorSet;
    /* access modifiers changed from: private */
    public LinearLayout linearLayout;
    /* access modifiers changed from: private */
    public LinearLayout linearLayout1;
    private TextView textView;
    private int textWidth;

    private static class LinkMovementMethodMy extends LinkMovementMethod {
        private LinkMovementMethodMy() {
        }

        public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
            try {
                return super.onTouchEvent(widget, buffer, event);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
                return false;
            }
        }
    }

    public IdenticonActivity(Bundle args) {
        super(args);
    }

    public boolean onFragmentCreate() {
        this.chat_id = getArguments().getInt("chat_id");
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
    }

    public View createView(Context context) {
        Context context2 = context;
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("EncryptionKey", NUM));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    IdenticonActivity.this.finishFragment();
                }
            }
        });
        this.fragmentView = new FrameLayout(context2);
        this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        this.fragmentView.setOnTouchListener(IdenticonActivity$$ExternalSyntheticLambda0.INSTANCE);
        LinearLayout linearLayout2 = new LinearLayout(context2);
        this.linearLayout = linearLayout2;
        linearLayout2.setOrientation(1);
        this.linearLayout.setWeightSum(100.0f);
        ((FrameLayout) this.fragmentView).addView(this.linearLayout, LayoutHelper.createFrame(-1, -1.0f));
        FrameLayout frameLayout = new FrameLayout(context2);
        frameLayout.setPadding(AndroidUtilities.dp(20.0f), AndroidUtilities.dp(20.0f), AndroidUtilities.dp(20.0f), AndroidUtilities.dp(20.0f));
        this.linearLayout.addView(frameLayout, LayoutHelper.createLinear(-1, -1, 50.0f));
        ImageView identiconView = new ImageView(context2);
        identiconView.setScaleType(ImageView.ScaleType.FIT_XY);
        frameLayout.addView(identiconView, LayoutHelper.createFrame(-1, -1.0f));
        AnonymousClass2 r6 = new FrameLayout(context2) {
            /* access modifiers changed from: protected */
            public void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                if (IdenticonActivity.this.codeTextView != null) {
                    int x = (IdenticonActivity.this.codeTextView.getLeft() + (IdenticonActivity.this.codeTextView.getMeasuredWidth() / 2)) - (IdenticonActivity.this.emojiTextView.getMeasuredWidth() / 2);
                    int y = (((IdenticonActivity.this.codeTextView.getMeasuredHeight() - IdenticonActivity.this.emojiTextView.getMeasuredHeight()) / 2) + IdenticonActivity.this.linearLayout1.getTop()) - AndroidUtilities.dp(16.0f);
                    IdenticonActivity.this.emojiTextView.layout(x, y, IdenticonActivity.this.emojiTextView.getMeasuredWidth() + x, IdenticonActivity.this.emojiTextView.getMeasuredHeight() + y);
                }
            }
        };
        this.container = r6;
        r6.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        this.linearLayout.addView(this.container, LayoutHelper.createLinear(-1, -1, 50.0f));
        LinearLayout linearLayout3 = new LinearLayout(context2);
        this.linearLayout1 = linearLayout3;
        linearLayout3.setOrientation(1);
        this.linearLayout1.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
        this.container.addView(this.linearLayout1, LayoutHelper.createFrame(-2, -2, 17));
        TextView textView2 = new TextView(context2);
        this.codeTextView = textView2;
        textView2.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText4"));
        this.codeTextView.setGravity(17);
        this.codeTextView.setTypeface(Typeface.MONOSPACE);
        this.codeTextView.setTextSize(1, 16.0f);
        this.linearLayout1.addView(this.codeTextView, LayoutHelper.createLinear(-2, -2, 1));
        TextView textView3 = new TextView(context2);
        this.textView = textView3;
        textView3.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText4"));
        this.textView.setLinkTextColor(Theme.getColor("windowBackgroundWhiteLinkText"));
        this.textView.setTextSize(1, 16.0f);
        this.textView.setLinksClickable(true);
        this.textView.setClickable(true);
        this.textView.setGravity(17);
        this.textView.setMovementMethod(new LinkMovementMethodMy());
        this.linearLayout1.addView(this.textView, LayoutHelper.createFrame(-2, -2, 1));
        TextView textView4 = new TextView(context2);
        this.emojiTextView = textView4;
        textView4.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText4"));
        this.emojiTextView.setGravity(17);
        this.emojiTextView.setTextSize(1, 32.0f);
        this.container.addView(this.emojiTextView, LayoutHelper.createFrame(-2, -2.0f));
        TLRPC.EncryptedChat encryptedChat = MessagesController.getInstance(this.currentAccount).getEncryptedChat(Integer.valueOf(this.chat_id));
        if (encryptedChat != null) {
            IdenticonDrawable drawable = new IdenticonDrawable();
            identiconView.setImageDrawable(drawable);
            drawable.setEncryptedChat(encryptedChat);
            TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(encryptedChat.user_id));
            SpannableStringBuilder hash = new SpannableStringBuilder();
            StringBuilder emojis = new StringBuilder();
            if (encryptedChat.key_hash.length > 16) {
                String hex = Utilities.bytesToHex(encryptedChat.key_hash);
                for (int a = 0; a < 32; a++) {
                    if (a != 0) {
                        if (a % 8 == 0) {
                            hash.append(10);
                        } else if (a % 4 == 0) {
                            hash.append(' ');
                        }
                    }
                    hash.append(hex.substring(a * 2, (a * 2) + 2));
                    hash.append(' ');
                }
                hash.append("\n");
                for (int a2 = 0; a2 < 5; a2++) {
                    int num = ((encryptedChat.key_hash[(a2 * 4) + 16] & Byte.MAX_VALUE) << 24) | ((encryptedChat.key_hash[((a2 * 4) + 16) + 1] & 255) << 16) | ((encryptedChat.key_hash[((a2 * 4) + 16) + 2] & 255) << 8) | (encryptedChat.key_hash[(a2 * 4) + 16 + 3] & 255);
                    if (a2 != 0) {
                        emojis.append(" ");
                    }
                    emojis.append(EmojiData.emojiSecret[num % EmojiData.emojiSecret.length]);
                }
                this.emojiText = emojis.toString();
            }
            this.codeTextView.setText(hash.toString());
            hash.clear();
            hash.append(AndroidUtilities.replaceTags(LocaleController.formatString("EncryptionKeyDescription", NUM, user.first_name, user.first_name)));
            int index = hash.toString().indexOf("telegram.org");
            if (index != -1) {
                hash.setSpan(new URLSpanReplacement(LocaleController.getString("EncryptionKeyLink", NUM)), index, "telegram.org".length() + index, 33);
            }
            this.textView.setText(hash);
        }
        updateEmojiButton(false);
        return this.fragmentView;
    }

    static /* synthetic */ boolean lambda$createView$0(View v, MotionEvent event) {
        return true;
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        fixLayout();
    }

    public void onResume() {
        super.onResume();
        fixLayout();
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        TextView textView2;
        if (id == NotificationCenter.emojiLoaded && (textView2 = this.emojiTextView) != null) {
            textView2.invalidate();
        }
    }

    private void updateEmojiButton(boolean animated) {
        AnimatorSet animatorSet2 = this.animatorSet;
        if (animatorSet2 != null) {
            animatorSet2.cancel();
            this.animatorSet = null;
        }
        float f = 1.0f;
        if (animated) {
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
                public void onAnimationEnd(Animator animation) {
                    if (animation.equals(IdenticonActivity.this.animatorSet)) {
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
    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        String str;
        if (isOpen && !backward && (str = this.emojiText) != null) {
            TextView textView2 = this.emojiTextView;
            textView2.setText(Emoji.replaceEmoji(str, textView2.getPaint().getFontMetricsInt(), AndroidUtilities.dp(32.0f), false));
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        themeDescriptions.add(new ThemeDescription(this.container, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        themeDescriptions.add(new ThemeDescription(this.textView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        themeDescriptions.add(new ThemeDescription(this.codeTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        themeDescriptions.add(new ThemeDescription(this.textView, ThemeDescription.FLAG_LINKCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteLinkText"));
        return themeDescriptions;
    }
}
