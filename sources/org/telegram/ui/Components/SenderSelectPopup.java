package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.tgnet.TLRPC$Peer;
import org.telegram.tgnet.TLRPC$TL_channels_sendAsPeers;
import org.telegram.tgnet.TLRPC$TL_sendAsPeer;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.PremiumPreviewFragment;
/* loaded from: classes3.dex */
public class SenderSelectPopup extends ActionBarPopupWindow {
    private FrameLayout bulletinContainer;
    private Runnable bulletinHideCallback;
    private List<Bulletin> bulletins;
    private TLRPC$ChatFull chatFull;
    private boolean clicked;
    public View dimView;
    private boolean dismissed;
    private View headerShadow;
    public TextView headerText;
    private boolean isDismissingByBulletin;
    private Boolean isHeaderShadowVisible;
    private LinearLayoutManager layoutManager;
    private int popupX;
    private int popupY;
    public LinearLayout recyclerContainer;
    private RecyclerListView recyclerView;
    protected boolean runningCustomSprings;
    private FrameLayout scrimPopupContainerLayout;
    private TLRPC$TL_channels_sendAsPeers sendAsPeers;
    protected List<SpringAnimation> springAnimations;

    /* loaded from: classes3.dex */
    public interface OnSelectCallback {
        void onPeerSelected(RecyclerView recyclerView, SenderView senderView, TLRPC$Peer tLRPC$Peer);
    }

    @SuppressLint({"WrongConstant"})
    public SenderSelectPopup(final Context context, final ChatActivity chatActivity, final MessagesController messagesController, final TLRPC$ChatFull tLRPC$ChatFull, TLRPC$TL_channels_sendAsPeers tLRPC$TL_channels_sendAsPeers, final OnSelectCallback onSelectCallback) {
        super(context);
        this.springAnimations = new ArrayList();
        this.bulletins = new ArrayList();
        this.chatFull = tLRPC$ChatFull;
        this.sendAsPeers = tLRPC$TL_channels_sendAsPeers;
        BackButtonFrameLayout backButtonFrameLayout = new BackButtonFrameLayout(context);
        this.scrimPopupContainerLayout = backButtonFrameLayout;
        backButtonFrameLayout.setLayoutParams(LayoutHelper.createFrame(-2, -2.0f));
        setContentView(this.scrimPopupContainerLayout);
        setWidth(-2);
        setHeight(-2);
        setBackgroundDrawable(null);
        Drawable mutate = ContextCompat.getDrawable(context, R.drawable.popup_fixed_alert).mutate();
        mutate.setColorFilter(new PorterDuffColorFilter(Theme.getColor("actionBarDefaultSubmenuBackground"), PorterDuff.Mode.MULTIPLY));
        this.scrimPopupContainerLayout.setBackground(mutate);
        android.graphics.Rect rect = new android.graphics.Rect();
        mutate.getPadding(rect);
        this.scrimPopupContainerLayout.setPadding(rect.left, rect.top, rect.right, rect.bottom);
        View view = new View(context);
        this.dimView = view;
        view.setBackgroundColor(NUM);
        final int dp = AndroidUtilities.dp(450.0f);
        final int width = (int) (chatActivity.contentView.getWidth() * 0.75f);
        LinearLayout linearLayout = new LinearLayout(this, context) { // from class: org.telegram.ui.Components.SenderSelectPopup.1
            @Override // android.widget.LinearLayout, android.view.View
            protected void onMeasure(int i, int i2) {
                super.onMeasure(View.MeasureSpec.makeMeasureSpec(Math.min(View.MeasureSpec.getSize(i), width), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(Math.min(View.MeasureSpec.getSize(i2), dp), View.MeasureSpec.getMode(i2)));
            }

            @Override // android.view.View
            protected int getSuggestedMinimumWidth() {
                return AndroidUtilities.dp(260.0f);
            }
        };
        this.recyclerContainer = linearLayout;
        linearLayout.setOrientation(1);
        TextView textView = new TextView(context);
        this.headerText = textView;
        textView.setTextColor(Theme.getColor("dialogTextBlue"));
        this.headerText.setTextSize(1, 16.0f);
        this.headerText.setText(LocaleController.getString("SendMessageAsTitle", R.string.SendMessageAsTitle));
        this.headerText.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"), 1);
        int dp2 = AndroidUtilities.dp(18.0f);
        this.headerText.setPadding(dp2, AndroidUtilities.dp(12.0f), dp2, AndroidUtilities.dp(12.0f));
        this.recyclerContainer.addView(this.headerText);
        FrameLayout frameLayout = new FrameLayout(context);
        final ArrayList<TLRPC$TL_sendAsPeer> arrayList = tLRPC$TL_channels_sendAsPeers.peers;
        this.recyclerView = new RecyclerListView(context);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        this.layoutManager = linearLayoutManager;
        this.recyclerView.setLayoutManager(linearLayoutManager);
        this.recyclerView.setAdapter(new RecyclerListView.SelectionAdapter(this) { // from class: org.telegram.ui.Components.SenderSelectPopup.2
            @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
            public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
                return true;
            }

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            /* renamed from: onCreateViewHolder */
            public RecyclerView.ViewHolder mo1817onCreateViewHolder(ViewGroup viewGroup, int i) {
                return new RecyclerListView.Holder(new SenderView(viewGroup.getContext()));
            }

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
                SenderView senderView = (SenderView) viewHolder.itemView;
                TLRPC$TL_sendAsPeer tLRPC$TL_sendAsPeer = (TLRPC$TL_sendAsPeer) arrayList.get(i);
                TLRPC$Peer tLRPC$Peer = tLRPC$TL_sendAsPeer.peer;
                long j = tLRPC$Peer.channel_id;
                long j2 = j != 0 ? -j : 0L;
                if (j2 == 0) {
                    long j3 = tLRPC$Peer.user_id;
                    if (j3 != 0) {
                        j2 = j3;
                    }
                }
                boolean z = true;
                if (j2 < 0) {
                    TLRPC$Chat chat = messagesController.getChat(Long.valueOf(-j2));
                    if (chat != null) {
                        if (tLRPC$TL_sendAsPeer.premium_required) {
                            SpannableString spannableString = new SpannableString(((Object) TextUtils.ellipsize(chat.title, senderView.title.getPaint(), width - AndroidUtilities.dp(100.0f), TextUtils.TruncateAt.END)) + " d");
                            ColoredImageSpan coloredImageSpan = new ColoredImageSpan(R.drawable.msg_mini_premiumlock);
                            coloredImageSpan.setTopOffset(1);
                            coloredImageSpan.setSize(AndroidUtilities.dp(14.0f));
                            coloredImageSpan.setColorKey("windowBackgroundWhiteGrayText5");
                            spannableString.setSpan(coloredImageSpan, spannableString.length() - 1, spannableString.length(), 33);
                            senderView.title.setEllipsize(null);
                            senderView.title.setText(spannableString);
                        } else {
                            senderView.title.setEllipsize(TextUtils.TruncateAt.END);
                            senderView.title.setText(chat.title);
                        }
                        senderView.subtitle.setText(LocaleController.formatPluralString((!ChatObject.isChannel(chat) || chat.megagroup) ? "Members" : "Subscribers", chat.participants_count, new Object[0]));
                        senderView.avatar.setAvatar(chat);
                    }
                    SimpleAvatarView simpleAvatarView = senderView.avatar;
                    TLRPC$Peer tLRPC$Peer2 = tLRPC$ChatFull.default_send_as;
                    if (tLRPC$Peer2 == null ? i != 0 : tLRPC$Peer2.channel_id != tLRPC$Peer.channel_id) {
                        z = false;
                    }
                    simpleAvatarView.setSelected(z, false);
                    return;
                }
                TLRPC$User user = messagesController.getUser(Long.valueOf(j2));
                if (user != null) {
                    senderView.title.setText(UserObject.getUserName(user));
                    senderView.subtitle.setText(LocaleController.getString("VoipGroupPersonalAccount", R.string.VoipGroupPersonalAccount));
                    senderView.avatar.setAvatar(user);
                }
                SimpleAvatarView simpleAvatarView2 = senderView.avatar;
                TLRPC$Peer tLRPC$Peer3 = tLRPC$ChatFull.default_send_as;
                if (tLRPC$Peer3 == null ? i != 0 : tLRPC$Peer3.user_id != tLRPC$Peer.user_id) {
                    z = false;
                }
                simpleAvatarView2.setSelected(z, false);
            }

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public int getItemCount() {
                return arrayList.size();
            }
        });
        this.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.Components.SenderSelectPopup.3
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                boolean z = SenderSelectPopup.this.layoutManager.findFirstCompletelyVisibleItemPosition() != 0;
                if (SenderSelectPopup.this.isHeaderShadowVisible == null || z != SenderSelectPopup.this.isHeaderShadowVisible.booleanValue()) {
                    SenderSelectPopup.this.headerShadow.animate().cancel();
                    SenderSelectPopup.this.headerShadow.animate().alpha(z ? 1.0f : 0.0f).setDuration(150L).start();
                    SenderSelectPopup.this.isHeaderShadowVisible = Boolean.valueOf(z);
                }
            }
        });
        this.recyclerView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.Components.SenderSelectPopup$$ExternalSyntheticLambda10
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view2, int i) {
                SenderSelectPopup.this.lambda$new$2(arrayList, context, chatActivity, onSelectCallback, view2, i);
            }
        });
        this.recyclerView.setOverScrollMode(2);
        frameLayout.addView(this.recyclerView);
        this.headerShadow = new View(context);
        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.header_shadow);
        drawable.setAlpha(153);
        this.headerShadow.setBackground(drawable);
        this.headerShadow.setAlpha(0.0f);
        frameLayout.addView(this.headerShadow, LayoutHelper.createFrame(-1, 4.0f));
        this.recyclerContainer.addView(frameLayout, LayoutHelper.createFrame(-1, -2.0f));
        this.scrimPopupContainerLayout.addView(this.recyclerContainer);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$2(List list, Context context, final ChatActivity chatActivity, OnSelectCallback onSelectCallback, View view, int i) {
        TLRPC$TL_sendAsPeer tLRPC$TL_sendAsPeer = (TLRPC$TL_sendAsPeer) list.get(i);
        if (this.clicked) {
            return;
        }
        if (tLRPC$TL_sendAsPeer.premium_required && !UserConfig.getInstance(UserConfig.selectedAccount).isPremium()) {
            try {
                view.performHapticFeedback(3, 2);
            } catch (Exception unused) {
            }
            final WindowManager windowManager = (WindowManager) context.getSystemService("window");
            if (this.bulletinContainer == null) {
                this.bulletinContainer = new FrameLayout(context) { // from class: org.telegram.ui.Components.SenderSelectPopup.4
                    @Override // android.view.View
                    @SuppressLint({"ClickableViewAccessibility"})
                    public boolean onTouchEvent(MotionEvent motionEvent) {
                        View contentView = SenderSelectPopup.this.getContentView();
                        contentView.getLocationInWindow(r2);
                        int[] iArr = {iArr[0] + SenderSelectPopup.this.popupX, iArr[1] + SenderSelectPopup.this.popupY};
                        int[] iArr2 = new int[2];
                        getLocationInWindow(iArr2);
                        if ((motionEvent.getAction() == 0 && motionEvent.getX() <= iArr[0]) || motionEvent.getX() >= iArr[0] + contentView.getWidth() || motionEvent.getY() <= iArr[1] || motionEvent.getY() >= iArr[1] + contentView.getHeight()) {
                            if (!SenderSelectPopup.this.dismissed && !SenderSelectPopup.this.isDismissingByBulletin) {
                                SenderSelectPopup.this.isDismissingByBulletin = true;
                                SenderSelectPopup.this.startDismissAnimation(new SpringAnimation[0]);
                            }
                            return true;
                        }
                        motionEvent.offsetLocation(iArr2[0] - iArr[0], (AndroidUtilities.statusBarHeight + iArr2[1]) - iArr[1]);
                        return contentView.dispatchTouchEvent(motionEvent);
                    }
                };
            }
            Runnable runnable = this.bulletinHideCallback;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
            }
            if (this.bulletinContainer.getParent() == null) {
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.height = -1;
                layoutParams.width = -1;
                layoutParams.format = -3;
                layoutParams.type = 99;
                int i2 = Build.VERSION.SDK_INT;
                if (i2 >= 21) {
                    layoutParams.flags |= Integer.MIN_VALUE;
                }
                if (i2 >= 28) {
                    layoutParams.layoutInDisplayCutoutMode = 1;
                }
                windowManager.addView(this.bulletinContainer, layoutParams);
            }
            final Bulletin make = Bulletin.make(this.bulletinContainer, new SelectSendAsPremiumHintBulletinLayout(context, chatActivity.themeDelegate, new Runnable() { // from class: org.telegram.ui.Components.SenderSelectPopup$$ExternalSyntheticLambda9
                @Override // java.lang.Runnable
                public final void run() {
                    SenderSelectPopup.this.lambda$new$0(chatActivity);
                }
            }), 1500);
            make.getLayout().addCallback(new Bulletin.Layout.Callback() { // from class: org.telegram.ui.Components.SenderSelectPopup.5
                @Override // org.telegram.ui.Components.Bulletin.Layout.Callback
                public /* synthetic */ void onAttach(Bulletin.Layout layout, Bulletin bulletin) {
                    Bulletin.Layout.Callback.CC.$default$onAttach(this, layout, bulletin);
                }

                @Override // org.telegram.ui.Components.Bulletin.Layout.Callback
                public /* synthetic */ void onDetach(Bulletin.Layout layout) {
                    Bulletin.Layout.Callback.CC.$default$onDetach(this, layout);
                }

                @Override // org.telegram.ui.Components.Bulletin.Layout.Callback
                public /* synthetic */ void onEnterTransitionEnd(Bulletin.Layout layout) {
                    Bulletin.Layout.Callback.CC.$default$onEnterTransitionEnd(this, layout);
                }

                @Override // org.telegram.ui.Components.Bulletin.Layout.Callback
                public /* synthetic */ void onEnterTransitionStart(Bulletin.Layout layout) {
                    Bulletin.Layout.Callback.CC.$default$onEnterTransitionStart(this, layout);
                }

                @Override // org.telegram.ui.Components.Bulletin.Layout.Callback
                public /* synthetic */ void onExitTransitionEnd(Bulletin.Layout layout) {
                    Bulletin.Layout.Callback.CC.$default$onExitTransitionEnd(this, layout);
                }

                @Override // org.telegram.ui.Components.Bulletin.Layout.Callback
                public /* synthetic */ void onExitTransitionStart(Bulletin.Layout layout) {
                    Bulletin.Layout.Callback.CC.$default$onExitTransitionStart(this, layout);
                }

                @Override // org.telegram.ui.Components.Bulletin.Layout.Callback
                public void onShow(Bulletin.Layout layout) {
                    SenderSelectPopup.this.bulletins.add(make);
                }

                @Override // org.telegram.ui.Components.Bulletin.Layout.Callback
                public void onHide(Bulletin.Layout layout) {
                    SenderSelectPopup.this.bulletins.remove(make);
                }
            });
            make.show();
            Runnable runnable2 = new Runnable() { // from class: org.telegram.ui.Components.SenderSelectPopup$$ExternalSyntheticLambda8
                @Override // java.lang.Runnable
                public final void run() {
                    SenderSelectPopup.this.lambda$new$1(windowManager);
                }
            };
            this.bulletinHideCallback = runnable2;
            AndroidUtilities.runOnUIThread(runnable2, 2500L);
            return;
        }
        this.clicked = true;
        onSelectCallback.onPeerSelected(this.recyclerView, (SenderView) view, tLRPC$TL_sendAsPeer.peer);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(ChatActivity chatActivity) {
        if (chatActivity != null) {
            chatActivity.presentFragment(new PremiumPreviewFragment("select_sender"));
            dismiss();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(WindowManager windowManager) {
        windowManager.removeView(this.bulletinContainer);
    }

    @Override // org.telegram.ui.ActionBar.ActionBarPopupWindow, android.widget.PopupWindow
    public void dismiss() {
        if (this.dismissed) {
            return;
        }
        FrameLayout frameLayout = this.bulletinContainer;
        if (frameLayout != null && frameLayout.getAlpha() == 1.0f) {
            final WindowManager windowManager = (WindowManager) this.bulletinContainer.getContext().getSystemService("window");
            this.bulletinContainer.animate().alpha(0.0f).setDuration(150L).setListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.SenderSelectPopup.6
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    try {
                        windowManager.removeViewImmediate(SenderSelectPopup.this.bulletinContainer);
                    } catch (Exception unused) {
                    }
                    if (SenderSelectPopup.this.bulletinHideCallback != null) {
                        AndroidUtilities.cancelRunOnUIThread(SenderSelectPopup.this.bulletinHideCallback);
                    }
                }
            });
        }
        this.dismissed = true;
        super.dismiss();
    }

    @Override // org.telegram.ui.ActionBar.ActionBarPopupWindow, android.widget.PopupWindow
    public void showAtLocation(View view, int i, int i2, int i3) {
        this.popupX = i2;
        this.popupY = i3;
        super.showAtLocation(view, i, i2, i3);
    }

    public void startShowAnimation() {
        for (SpringAnimation springAnimation : this.springAnimations) {
            springAnimation.cancel();
        }
        this.springAnimations.clear();
        this.scrimPopupContainerLayout.setPivotX(AndroidUtilities.dp(8.0f));
        FrameLayout frameLayout = this.scrimPopupContainerLayout;
        frameLayout.setPivotY(frameLayout.getMeasuredHeight() - AndroidUtilities.dp(8.0f));
        this.recyclerContainer.setPivotX(0.0f);
        this.recyclerContainer.setPivotY(0.0f);
        ArrayList<TLRPC$TL_sendAsPeer> arrayList = this.sendAsPeers.peers;
        TLRPC$Peer tLRPC$Peer = this.chatFull.default_send_as;
        if (tLRPC$Peer == null) {
            tLRPC$Peer = null;
        }
        if (tLRPC$Peer != null) {
            int dp = AndroidUtilities.dp(54.0f);
            int size = arrayList.size() * dp;
            int i = 0;
            while (i < arrayList.size()) {
                TLRPC$Peer tLRPC$Peer2 = arrayList.get(i).peer;
                long j = tLRPC$Peer2.channel_id;
                if (j == 0 || j != tLRPC$Peer.channel_id) {
                    long j2 = tLRPC$Peer2.user_id;
                    if (j2 == 0 || j2 != tLRPC$Peer.user_id) {
                        long j3 = tLRPC$Peer2.chat_id;
                        if (j3 == 0 || j3 != tLRPC$Peer.chat_id) {
                            i++;
                        }
                    }
                }
                this.layoutManager.scrollToPositionWithOffset(i, ((i == arrayList.size() - 1 || this.recyclerView.getMeasuredHeight() >= size) ? 0 : this.recyclerView.getMeasuredHeight() % dp) + AndroidUtilities.dp(7.0f) + (size - ((arrayList.size() - 2) * dp)));
                if (this.recyclerView.computeVerticalScrollOffset() > 0) {
                    this.headerShadow.animate().cancel();
                    this.headerShadow.animate().alpha(1.0f).setDuration(150L).start();
                }
            }
        }
        this.scrimPopupContainerLayout.setScaleX(0.25f);
        this.scrimPopupContainerLayout.setScaleY(0.25f);
        this.recyclerContainer.setAlpha(0.25f);
        this.dimView.setAlpha(0.0f);
        FrameLayout frameLayout2 = this.scrimPopupContainerLayout;
        DynamicAnimation.ViewProperty viewProperty = DynamicAnimation.ALPHA;
        for (final SpringAnimation springAnimation2 : Arrays.asList(new SpringAnimation(this.scrimPopupContainerLayout, DynamicAnimation.SCALE_X).setSpring(new SpringForce(1.0f).setStiffness(750.0f).setDampingRatio(1.0f)).addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() { // from class: org.telegram.ui.Components.SenderSelectPopup$$ExternalSyntheticLambda5
            @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationUpdateListener
            public final void onAnimationUpdate(DynamicAnimation dynamicAnimation, float f, float f2) {
                SenderSelectPopup.this.lambda$startShowAnimation$3(dynamicAnimation, f, f2);
            }
        }), new SpringAnimation(this.scrimPopupContainerLayout, DynamicAnimation.SCALE_Y).setSpring(new SpringForce(1.0f).setStiffness(750.0f).setDampingRatio(1.0f)).addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() { // from class: org.telegram.ui.Components.SenderSelectPopup$$ExternalSyntheticLambda4
            @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationUpdateListener
            public final void onAnimationUpdate(DynamicAnimation dynamicAnimation, float f, float f2) {
                SenderSelectPopup.this.lambda$startShowAnimation$4(dynamicAnimation, f, f2);
            }
        }), new SpringAnimation(frameLayout2, viewProperty).setSpring(new SpringForce(1.0f).setStiffness(750.0f).setDampingRatio(1.0f)), new SpringAnimation(this.recyclerContainer, viewProperty).setSpring(new SpringForce(1.0f).setStiffness(750.0f).setDampingRatio(1.0f)), new SpringAnimation(this.dimView, viewProperty).setSpring(new SpringForce(1.0f).setStiffness(750.0f).setDampingRatio(1.0f)))) {
            this.springAnimations.add(springAnimation2);
            springAnimation2.addEndListener(new DynamicAnimation.OnAnimationEndListener() { // from class: org.telegram.ui.Components.SenderSelectPopup$$ExternalSyntheticLambda3
                @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener
                public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
                    SenderSelectPopup.this.lambda$startShowAnimation$5(springAnimation2, dynamicAnimation, z, f, f2);
                }
            });
            springAnimation2.start();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$startShowAnimation$3(DynamicAnimation dynamicAnimation, float f, float f2) {
        this.recyclerContainer.setScaleX(1.0f / f);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$startShowAnimation$4(DynamicAnimation dynamicAnimation, float f, float f2) {
        this.recyclerContainer.setScaleY(1.0f / f);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$startShowAnimation$5(SpringAnimation springAnimation, DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
        if (!z) {
            this.springAnimations.remove(springAnimation);
            dynamicAnimation.cancel();
        }
    }

    public void startDismissAnimation(SpringAnimation... springAnimationArr) {
        Iterator it = new ArrayList(this.springAnimations).iterator();
        while (it.hasNext()) {
            ((SpringAnimation) it.next()).cancel();
        }
        this.springAnimations.clear();
        this.scrimPopupContainerLayout.setPivotX(AndroidUtilities.dp(8.0f));
        FrameLayout frameLayout = this.scrimPopupContainerLayout;
        frameLayout.setPivotY(frameLayout.getMeasuredHeight() - AndroidUtilities.dp(8.0f));
        this.recyclerContainer.setPivotX(0.0f);
        this.recyclerContainer.setPivotY(0.0f);
        this.scrimPopupContainerLayout.setScaleX(1.0f);
        this.scrimPopupContainerLayout.setScaleY(1.0f);
        this.recyclerContainer.setAlpha(1.0f);
        this.dimView.setAlpha(1.0f);
        ArrayList<SpringAnimation> arrayList = new ArrayList();
        boolean z = true;
        FrameLayout frameLayout2 = this.scrimPopupContainerLayout;
        DynamicAnimation.ViewProperty viewProperty = DynamicAnimation.ALPHA;
        arrayList.addAll(Arrays.asList(new SpringAnimation(this.scrimPopupContainerLayout, DynamicAnimation.SCALE_X).setSpring(new SpringForce(0.25f).setStiffness(750.0f).setDampingRatio(1.0f)).addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() { // from class: org.telegram.ui.Components.SenderSelectPopup$$ExternalSyntheticLambda7
            @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationUpdateListener
            public final void onAnimationUpdate(DynamicAnimation dynamicAnimation, float f, float f2) {
                SenderSelectPopup.this.lambda$startDismissAnimation$6(dynamicAnimation, f, f2);
            }
        }), new SpringAnimation(this.scrimPopupContainerLayout, DynamicAnimation.SCALE_Y).setSpring(new SpringForce(0.25f).setStiffness(750.0f).setDampingRatio(1.0f)).addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() { // from class: org.telegram.ui.Components.SenderSelectPopup$$ExternalSyntheticLambda6
            @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationUpdateListener
            public final void onAnimationUpdate(DynamicAnimation dynamicAnimation, float f, float f2) {
                SenderSelectPopup.this.lambda$startDismissAnimation$7(dynamicAnimation, f, f2);
            }
        }), new SpringAnimation(frameLayout2, viewProperty).setSpring(new SpringForce(0.0f).setStiffness(750.0f).setDampingRatio(1.0f)), new SpringAnimation(this.recyclerContainer, viewProperty).setSpring(new SpringForce(0.25f).setStiffness(750.0f).setDampingRatio(1.0f)), new SpringAnimation(this.dimView, viewProperty).setSpring(new SpringForce(0.0f).setStiffness(750.0f).setDampingRatio(1.0f)).addEndListener(new DynamicAnimation.OnAnimationEndListener() { // from class: org.telegram.ui.Components.SenderSelectPopup$$ExternalSyntheticLambda1
            @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener
            public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z2, float f, float f2) {
                SenderSelectPopup.this.lambda$startDismissAnimation$8(dynamicAnimation, z2, f, f2);
            }
        })));
        arrayList.addAll(Arrays.asList(springAnimationArr));
        if (springAnimationArr.length <= 0) {
            z = false;
        }
        this.runningCustomSprings = z;
        ((SpringAnimation) arrayList.get(0)).addEndListener(new DynamicAnimation.OnAnimationEndListener() { // from class: org.telegram.ui.Components.SenderSelectPopup$$ExternalSyntheticLambda0
            @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener
            public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z2, float f, float f2) {
                SenderSelectPopup.this.lambda$startDismissAnimation$9(dynamicAnimation, z2, f, f2);
            }
        });
        for (final SpringAnimation springAnimation : arrayList) {
            this.springAnimations.add(springAnimation);
            springAnimation.addEndListener(new DynamicAnimation.OnAnimationEndListener() { // from class: org.telegram.ui.Components.SenderSelectPopup$$ExternalSyntheticLambda2
                @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener
                public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z2, float f, float f2) {
                    SenderSelectPopup.this.lambda$startDismissAnimation$10(springAnimation, dynamicAnimation, z2, f, f2);
                }
            });
            springAnimation.start();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$startDismissAnimation$6(DynamicAnimation dynamicAnimation, float f, float f2) {
        this.recyclerContainer.setScaleX(1.0f / f);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$startDismissAnimation$7(DynamicAnimation dynamicAnimation, float f, float f2) {
        this.recyclerContainer.setScaleY(1.0f / f);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$startDismissAnimation$8(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
        if (this.dimView.getParent() != null) {
            ((ViewGroup) this.dimView.getParent()).removeView(this.dimView);
        }
        dismiss();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$startDismissAnimation$9(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
        this.runningCustomSprings = false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$startDismissAnimation$10(SpringAnimation springAnimation, DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
        if (!z) {
            this.springAnimations.remove(springAnimation);
            dynamicAnimation.cancel();
        }
    }

    /* loaded from: classes3.dex */
    public static final class SenderView extends LinearLayout {
        public final SimpleAvatarView avatar;
        public final TextView subtitle;
        public final TextView title;

        public SenderView(Context context) {
            super(context);
            setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            setOrientation(0);
            setGravity(16);
            int dp = AndroidUtilities.dp(14.0f);
            int i = dp / 2;
            setPadding(dp, i, dp, i);
            SimpleAvatarView simpleAvatarView = new SimpleAvatarView(context);
            this.avatar = simpleAvatarView;
            addView(simpleAvatarView, LayoutHelper.createFrame(40, 40.0f));
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(1);
            addView(linearLayout, LayoutHelper.createLinear(0, -1, 1.0f, 12, 0, 0, 0));
            TextView textView = new TextView(context);
            this.title = textView;
            textView.setTextColor(Theme.getColor("actionBarDefaultSubmenuItem"));
            textView.setTextSize(1, 16.0f);
            textView.setTag(textView);
            textView.setMaxLines(1);
            linearLayout.addView(textView);
            TextView textView2 = new TextView(context);
            this.subtitle = textView2;
            textView2.setTextColor(ColorUtils.setAlphaComponent(Theme.getColor("actionBarDefaultSubmenuItem"), 102));
            textView2.setTextSize(1, 14.0f);
            textView2.setTag(textView2);
            textView2.setMaxLines(1);
            textView2.setEllipsize(TextUtils.TruncateAt.END);
            linearLayout.addView(textView2);
        }
    }

    /* loaded from: classes3.dex */
    private class BackButtonFrameLayout extends FrameLayout {
        public BackButtonFrameLayout(Context context) {
            super(context);
        }

        @Override // android.view.ViewGroup, android.view.View
        public boolean dispatchKeyEvent(KeyEvent keyEvent) {
            if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0 && SenderSelectPopup.this.isShowing()) {
                SenderSelectPopup.this.dismiss();
            }
            return super.dispatchKeyEvent(keyEvent);
        }
    }
}
