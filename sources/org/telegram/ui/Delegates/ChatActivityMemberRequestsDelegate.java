package org.telegram.ui.Delegates;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.AvatarsImageView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.MemberRequestsBottomSheet;

public class ChatActivityMemberRequestsDelegate {
    private AvatarsImageView avatarsView;
    /* access modifiers changed from: private */
    public MemberRequestsBottomSheet bottomSheet;
    private final Callback callback;
    private TLRPC$ChatFull chatInfo;
    private int closePendingRequestsCount = -1;
    private ImageView closeView;
    private final int currentAccount;
    private final TLRPC$Chat currentChat;
    private final BaseFragment fragment;
    private ValueAnimator pendingRequestsAnimator;
    private int pendingRequestsCount;
    private float pendingRequestsEnterOffset;
    private TextView requestsCountTextView;
    /* access modifiers changed from: private */
    public FrameLayout root;

    public interface Callback {
        void onEnterOffsetChanged();
    }

    public ChatActivityMemberRequestsDelegate(BaseFragment baseFragment, TLRPC$Chat tLRPC$Chat, Callback callback2) {
        this.fragment = baseFragment;
        this.currentChat = tLRPC$Chat;
        this.currentAccount = baseFragment.getCurrentAccount();
        this.callback = callback2;
    }

    public View getView() {
        if (this.root == null) {
            FrameLayout frameLayout = new FrameLayout(this.fragment.getParentActivity());
            this.root = frameLayout;
            frameLayout.setBackgroundResource(NUM);
            this.root.getBackground().mutate().setColorFilter(new PorterDuffColorFilter(this.fragment.getThemedColor("chat_topPanelBackground"), PorterDuff.Mode.MULTIPLY));
            this.root.setVisibility(8);
            this.pendingRequestsEnterOffset = (float) (-getViewHeight());
            View view = new View(this.fragment.getParentActivity());
            view.setBackground(Theme.getSelectorDrawable(false));
            view.setOnClickListener(new ChatActivityMemberRequestsDelegate$$ExternalSyntheticLambda2(this));
            this.root.addView(view, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 2.0f));
            LinearLayout linearLayout = new LinearLayout(this.fragment.getParentActivity());
            linearLayout.setOrientation(0);
            this.root.addView(linearLayout, LayoutHelper.createFrame(-1, -1.0f, 48, 0.0f, 0.0f, 36.0f, 0.0f));
            AnonymousClass1 r2 = new AvatarsImageView(this, this.fragment.getParentActivity(), false) {
                /* access modifiers changed from: protected */
                public void onMeasure(int i, int i2) {
                    int i3 = this.count;
                    super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp((float) (i3 == 0 ? 0 : ((i3 - 1) * 20) + 24)), NUM), i2);
                }
            };
            this.avatarsView = r2;
            r2.reset();
            linearLayout.addView(this.avatarsView, LayoutHelper.createFrame(-2, -1.0f, 48, 8.0f, 0.0f, 10.0f, 0.0f));
            TextView textView = new TextView(this.fragment.getParentActivity());
            this.requestsCountTextView = textView;
            textView.setEllipsize(TextUtils.TruncateAt.END);
            this.requestsCountTextView.setGravity(16);
            this.requestsCountTextView.setSingleLine();
            this.requestsCountTextView.setText((CharSequence) null);
            this.requestsCountTextView.setTextColor(this.fragment.getThemedColor("chat_topPanelTitle"));
            this.requestsCountTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            linearLayout.addView(this.requestsCountTextView, LayoutHelper.createFrame(-1, -1.0f, 48, 0.0f, 0.0f, 0.0f, 0.0f));
            ImageView imageView = new ImageView(this.fragment.getParentActivity());
            this.closeView = imageView;
            if (Build.VERSION.SDK_INT >= 21) {
                imageView.setBackground(Theme.createSelectorDrawable(this.fragment.getThemedColor("inappPlayerClose") & NUM, 1, AndroidUtilities.dp(14.0f)));
            }
            this.closeView.setColorFilter(new PorterDuffColorFilter(this.fragment.getThemedColor("chat_topPanelClose"), PorterDuff.Mode.MULTIPLY));
            this.closeView.setContentDescription(LocaleController.getString("Close", NUM));
            this.closeView.setImageResource(NUM);
            this.closeView.setScaleType(ImageView.ScaleType.CENTER);
            this.closeView.setOnClickListener(new ChatActivityMemberRequestsDelegate$$ExternalSyntheticLambda1(this));
            this.root.addView(this.closeView, LayoutHelper.createFrame(36, -1.0f, 53, 0.0f, 0.0f, 2.0f, 0.0f));
            TLRPC$ChatFull tLRPC$ChatFull = this.chatInfo;
            if (tLRPC$ChatFull != null) {
                setPendingRequests(tLRPC$ChatFull.requests_pending, tLRPC$ChatFull.recent_requesters, false);
            }
        }
        return this.root;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$getView$0(View view) {
        showBottomSheet();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$getView$1(View view) {
        this.fragment.getMessagesController().setChatPendingRequestsOnClose(this.currentChat.id, this.pendingRequestsCount);
        this.closePendingRequestsCount = this.pendingRequestsCount;
        animatePendingRequests(false, true);
    }

    public void setChatInfo(TLRPC$ChatFull tLRPC$ChatFull, boolean z) {
        this.chatInfo = tLRPC$ChatFull;
        if (tLRPC$ChatFull != null) {
            setPendingRequests(tLRPC$ChatFull.requests_pending, tLRPC$ChatFull.recent_requesters, z);
        }
    }

    public int getViewHeight() {
        return AndroidUtilities.dp(40.0f);
    }

    public float getViewEnterOffset() {
        return this.pendingRequestsEnterOffset;
    }

    public void onBackToScreen() {
        MemberRequestsBottomSheet memberRequestsBottomSheet = this.bottomSheet;
        if (memberRequestsBottomSheet != null && memberRequestsBottomSheet.isNeedRestoreDialog()) {
            showBottomSheet();
        }
    }

    private void showBottomSheet() {
        if (this.bottomSheet == null) {
            this.bottomSheet = new MemberRequestsBottomSheet(this.fragment, this.currentChat.id) {
                public void dismissInternal() {
                    super.dismissInternal();
                    if (!ChatActivityMemberRequestsDelegate.this.bottomSheet.isNeedRestoreDialog()) {
                        MemberRequestsBottomSheet unused = ChatActivityMemberRequestsDelegate.this.bottomSheet = null;
                    }
                }
            };
        }
        this.fragment.showDialog(this.bottomSheet);
    }

    private void setPendingRequests(int i, List<Long> list, boolean z) {
        if (this.root != null) {
            if (i <= 0) {
                if (this.currentChat != null) {
                    this.fragment.getMessagesController().setChatPendingRequestsOnClose(this.currentChat.id, 0);
                    this.closePendingRequestsCount = 0;
                }
                animatePendingRequests(false, z);
                this.pendingRequestsCount = 0;
            } else if (this.pendingRequestsCount != i) {
                this.pendingRequestsCount = i;
                this.requestsCountTextView.setText(LocaleController.formatPluralString("JoinUsersRequests", i));
                animatePendingRequests(true, z);
                if (list != null && !list.isEmpty()) {
                    int min = Math.min(3, list.size());
                    for (int i2 = 0; i2 < min; i2++) {
                        TLRPC$User user = this.fragment.getMessagesController().getUser(list.get(i2));
                        if (user != null) {
                            this.avatarsView.setObject(i2, this.currentAccount, user);
                        }
                    }
                    this.avatarsView.setCount(min);
                    this.avatarsView.commitTransition(true);
                }
            }
        }
    }

    private void animatePendingRequests(final boolean z, boolean z2) {
        int i = 0;
        if (z != (this.root.getVisibility() == 0)) {
            if (z) {
                if (this.closePendingRequestsCount == -1 && this.currentChat != null) {
                    this.closePendingRequestsCount = this.fragment.getMessagesController().getChatPendingRequestsOnClosed(this.currentChat.id);
                }
                int i2 = this.pendingRequestsCount;
                int i3 = this.closePendingRequestsCount;
                if (i2 != i3) {
                    if (!(i3 == 0 || this.currentChat == null)) {
                        this.fragment.getMessagesController().setChatPendingRequestsOnClose(this.currentChat.id, 0);
                    }
                } else {
                    return;
                }
            }
            ValueAnimator valueAnimator = this.pendingRequestsAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            float f = 0.0f;
            if (z2) {
                float[] fArr = new float[2];
                fArr[0] = z ? 0.0f : 1.0f;
                if (z) {
                    f = 1.0f;
                }
                fArr[1] = f;
                ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
                this.pendingRequestsAnimator = ofFloat;
                ofFloat.addUpdateListener(new ChatActivityMemberRequestsDelegate$$ExternalSyntheticLambda0(this));
                this.pendingRequestsAnimator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationStart(Animator animator) {
                        if (z) {
                            ChatActivityMemberRequestsDelegate.this.root.setVisibility(0);
                        }
                    }

                    public void onAnimationEnd(Animator animator) {
                        if (!z) {
                            ChatActivityMemberRequestsDelegate.this.root.setVisibility(8);
                        }
                    }
                });
                this.pendingRequestsAnimator.setDuration(200);
                this.pendingRequestsAnimator.start();
                return;
            }
            FrameLayout frameLayout = this.root;
            if (!z) {
                i = 8;
            }
            frameLayout.setVisibility(i);
            if (!z) {
                f = (float) (-getViewHeight());
            }
            this.pendingRequestsEnterOffset = f;
            Callback callback2 = this.callback;
            if (callback2 != null) {
                callback2.onEnterOffsetChanged();
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$animatePendingRequests$2(ValueAnimator valueAnimator) {
        this.pendingRequestsEnterOffset = ((float) (-getViewHeight())) * (1.0f - ((Float) valueAnimator.getAnimatedValue()).floatValue());
        Callback callback2 = this.callback;
        if (callback2 != null) {
            callback2.onEnterOffsetChanged();
        }
    }

    public void fillThemeDescriptions(List<ThemeDescription> list) {
        List<ThemeDescription> list2 = list;
        list2.add(new ThemeDescription(this.root, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_topPanelBackground"));
        list2.add(new ThemeDescription(this.requestsCountTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_topPanelTitle"));
        list2.add(new ThemeDescription(this.closeView, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_topPanelClose"));
    }
}
