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
import org.telegram.tgnet.TLRPC;
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
    private TLRPC.ChatFull chatInfo;
    private int closePendingRequestsCount = -1;
    private ImageView closeView;
    private final int currentAccount;
    private final TLRPC.Chat currentChat;
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

    public ChatActivityMemberRequestsDelegate(BaseFragment fragment2, TLRPC.Chat currentChat2, Callback callback2) {
        this.fragment = fragment2;
        this.currentChat = currentChat2;
        this.currentAccount = fragment2.getCurrentAccount();
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
            View pendingRequestsSelector = new View(this.fragment.getParentActivity());
            pendingRequestsSelector.setBackground(Theme.getSelectorDrawable(false));
            pendingRequestsSelector.setOnClickListener(new ChatActivityMemberRequestsDelegate$$ExternalSyntheticLambda1(this));
            this.root.addView(pendingRequestsSelector, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 2.0f));
            LinearLayout requestsDataLayout = new LinearLayout(this.fragment.getParentActivity());
            requestsDataLayout.setOrientation(0);
            this.root.addView(requestsDataLayout, LayoutHelper.createFrame(-1, -1.0f, 48, 0.0f, 0.0f, 36.0f, 0.0f));
            AnonymousClass1 r3 = new AvatarsImageView(this.fragment.getParentActivity(), false) {
                /* access modifiers changed from: protected */
                public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp((float) (this.avatarsDarawable.count == 0 ? 0 : ((this.avatarsDarawable.count - 1) * 20) + 24)), NUM), heightMeasureSpec);
                }
            };
            this.avatarsView = r3;
            r3.reset();
            requestsDataLayout.addView(this.avatarsView, LayoutHelper.createFrame(-2, -1.0f, 48, 8.0f, 0.0f, 10.0f, 0.0f));
            TextView textView = new TextView(this.fragment.getParentActivity());
            this.requestsCountTextView = textView;
            textView.setEllipsize(TextUtils.TruncateAt.END);
            this.requestsCountTextView.setGravity(16);
            this.requestsCountTextView.setSingleLine();
            this.requestsCountTextView.setText((CharSequence) null);
            this.requestsCountTextView.setTextColor(this.fragment.getThemedColor("chat_topPanelTitle"));
            this.requestsCountTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            requestsDataLayout.addView(this.requestsCountTextView, LayoutHelper.createFrame(-1, -1.0f, 48, 0.0f, 0.0f, 0.0f, 0.0f));
            this.closeView = new ImageView(this.fragment.getParentActivity());
            if (Build.VERSION.SDK_INT >= 21) {
                this.closeView.setBackground(Theme.createSelectorDrawable(this.fragment.getThemedColor("inappPlayerClose") & NUM, 1, AndroidUtilities.dp(14.0f)));
            }
            this.closeView.setColorFilter(new PorterDuffColorFilter(this.fragment.getThemedColor("chat_topPanelClose"), PorterDuff.Mode.MULTIPLY));
            this.closeView.setContentDescription(LocaleController.getString("Close", NUM));
            this.closeView.setImageResource(NUM);
            this.closeView.setScaleType(ImageView.ScaleType.CENTER);
            this.closeView.setOnClickListener(new ChatActivityMemberRequestsDelegate$$ExternalSyntheticLambda2(this));
            this.root.addView(this.closeView, LayoutHelper.createFrame(36, -1.0f, 53, 0.0f, 0.0f, 2.0f, 0.0f));
            TLRPC.ChatFull chatFull = this.chatInfo;
            if (chatFull != null) {
                setPendingRequests(chatFull.requests_pending, this.chatInfo.recent_requesters, false);
            }
        }
        return this.root;
    }

    /* renamed from: lambda$getView$0$org-telegram-ui-Delegates-ChatActivityMemberRequestsDelegate  reason: not valid java name */
    public /* synthetic */ void m1626x24bd3119(View v) {
        showBottomSheet();
    }

    /* renamed from: lambda$getView$1$org-telegram-ui-Delegates-ChatActivityMemberRequestsDelegate  reason: not valid java name */
    public /* synthetic */ void m1627xb8fba0b8(View v) {
        this.fragment.getMessagesController().setChatPendingRequestsOnClose(this.currentChat.id, this.pendingRequestsCount);
        this.closePendingRequestsCount = this.pendingRequestsCount;
        animatePendingRequests(false, true);
    }

    public void setChatInfo(TLRPC.ChatFull chatInfo2, boolean animated) {
        this.chatInfo = chatInfo2;
        if (chatInfo2 != null) {
            setPendingRequests(chatInfo2.requests_pending, chatInfo2.recent_requesters, animated);
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
                public void dismiss() {
                    if (ChatActivityMemberRequestsDelegate.this.bottomSheet != null && !ChatActivityMemberRequestsDelegate.this.bottomSheet.isNeedRestoreDialog()) {
                        MemberRequestsBottomSheet unused = ChatActivityMemberRequestsDelegate.this.bottomSheet = null;
                    }
                    super.dismiss();
                }
            };
        }
        this.fragment.showDialog(this.bottomSheet);
    }

    private void setPendingRequests(int count, List<Long> recentRequestersIdList, boolean animated) {
        if (this.root != null) {
            if (count <= 0) {
                if (this.currentChat != null) {
                    this.fragment.getMessagesController().setChatPendingRequestsOnClose(this.currentChat.id, 0);
                    this.closePendingRequestsCount = 0;
                }
                animatePendingRequests(false, animated);
                this.pendingRequestsCount = 0;
            } else if (this.pendingRequestsCount != count) {
                this.pendingRequestsCount = count;
                this.requestsCountTextView.setText(LocaleController.formatPluralString("JoinUsersRequests", count, new Object[0]));
                animatePendingRequests(true, animated);
                if (recentRequestersIdList != null && !recentRequestersIdList.isEmpty()) {
                    int usersCount = Math.min(3, recentRequestersIdList.size());
                    for (int i = 0; i < usersCount; i++) {
                        TLRPC.User user = this.fragment.getMessagesController().getUser(recentRequestersIdList.get(i));
                        if (user != null) {
                            this.avatarsView.setObject(i, this.currentAccount, user);
                        }
                    }
                    this.avatarsView.setCount(usersCount);
                    this.avatarsView.commitTransition(true);
                }
            }
        }
    }

    private void animatePendingRequests(final boolean appear, boolean animated) {
        int i = 0;
        if (appear != (this.root.getVisibility() == 0)) {
            if (appear) {
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
            if (animated) {
                float[] fArr = new float[2];
                fArr[0] = appear ? 0.0f : 1.0f;
                if (appear) {
                    f = 1.0f;
                }
                fArr[1] = f;
                ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
                this.pendingRequestsAnimator = ofFloat;
                ofFloat.addUpdateListener(new ChatActivityMemberRequestsDelegate$$ExternalSyntheticLambda0(this));
                this.pendingRequestsAnimator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationStart(Animator animation) {
                        if (appear) {
                            ChatActivityMemberRequestsDelegate.this.root.setVisibility(0);
                        }
                    }

                    public void onAnimationEnd(Animator animation) {
                        if (!appear) {
                            ChatActivityMemberRequestsDelegate.this.root.setVisibility(8);
                        }
                    }
                });
                this.pendingRequestsAnimator.setDuration(200);
                this.pendingRequestsAnimator.start();
                return;
            }
            FrameLayout frameLayout = this.root;
            if (!appear) {
                i = 8;
            }
            frameLayout.setVisibility(i);
            if (!appear) {
                f = (float) (-getViewHeight());
            }
            this.pendingRequestsEnterOffset = f;
            Callback callback2 = this.callback;
            if (callback2 != null) {
                callback2.onEnterOffsetChanged();
            }
        }
    }

    /* renamed from: lambda$animatePendingRequests$2$org-telegram-ui-Delegates-ChatActivityMemberRequestsDelegate  reason: not valid java name */
    public /* synthetic */ void m1625x95fe199e(ValueAnimator animation) {
        this.pendingRequestsEnterOffset = ((float) (-getViewHeight())) * (1.0f - ((Float) animation.getAnimatedValue()).floatValue());
        Callback callback2 = this.callback;
        if (callback2 != null) {
            callback2.onEnterOffsetChanged();
        }
    }

    public void fillThemeDescriptions(List<ThemeDescription> themeDescriptions) {
        List<ThemeDescription> list = themeDescriptions;
        list.add(new ThemeDescription(this.root, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_topPanelBackground"));
        list.add(new ThemeDescription(this.requestsCountTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_topPanelTitle"));
        list.add(new ThemeDescription(this.closeView, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_topPanelClose"));
    }
}
