package org.telegram.ui.Components;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import androidx.core.widget.NestedScrollView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.tgnet.TLRPC$TL_chatInviteExported;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_exportChatInvite;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.LinkActionView;
import org.telegram.ui.ManageLinksActivity;

public class PermanentLinkBottomSheet extends BottomSheet {
    private int chatId;
    private BaseFragment fragment;
    private final RLottieImageView imageView;
    TLRPC$TL_chatInviteExported invite;
    private final LinkActionView linkActionView;
    boolean linkGenerating;
    RLottieDrawable linkIcon;
    private final TextView manage;
    private final TextView subtitle;
    private final TextView titleView;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public PermanentLinkBottomSheet(Context context, boolean z, BaseFragment baseFragment, TLRPC$ChatFull tLRPC$ChatFull, int i, boolean z2) {
        super(context, z);
        String str;
        int i2;
        TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported;
        Context context2 = context;
        TLRPC$ChatFull tLRPC$ChatFull2 = tLRPC$ChatFull;
        this.chatId = i;
        setAllowNestedScroll(true);
        setApplyBottomPadding(false);
        LinkActionView linkActionView2 = new LinkActionView(context, baseFragment, this, i, true, z2);
        this.linkActionView = linkActionView2;
        linkActionView2.setPermanent(true);
        RLottieImageView rLottieImageView = new RLottieImageView(context2);
        this.imageView = rLottieImageView;
        RLottieDrawable rLottieDrawable = new RLottieDrawable(NUM, "NUM", AndroidUtilities.dp(90.0f), AndroidUtilities.dp(90.0f), false, (int[]) null);
        this.linkIcon = rLottieDrawable;
        rLottieDrawable.setCustomEndFrame(42);
        rLottieImageView.setAnimation(this.linkIcon);
        linkActionView2.setUsers(0, (ArrayList<TLRPC$User>) null);
        linkActionView2.showRevokeOption(true);
        linkActionView2.setDelegate(new LinkActionView.Delegate() {
            public /* synthetic */ void editLink() {
                LinkActionView.Delegate.CC.$default$editLink(this);
            }

            public /* synthetic */ void removeLink() {
                LinkActionView.Delegate.CC.$default$removeLink(this);
            }

            public final void revokeLink() {
                PermanentLinkBottomSheet.this.lambda$new$0$PermanentLinkBottomSheet();
            }

            public /* synthetic */ void showUsersForPermanentLink() {
                LinkActionView.Delegate.CC.$default$showUsersForPermanentLink(this);
            }
        });
        TextView textView = new TextView(context2);
        this.titleView = textView;
        textView.setText(LocaleController.getString("InviteLink", NUM));
        textView.setTextSize(24.0f);
        textView.setGravity(1);
        textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        TextView textView2 = new TextView(context2);
        this.subtitle = textView2;
        if (z2) {
            i2 = NUM;
            str = "LinkInfoChannel";
        } else {
            i2 = NUM;
            str = "LinkInfo";
        }
        textView2.setText(LocaleController.getString(str, i2));
        textView2.setTextSize(14.0f);
        textView2.setGravity(1);
        textView2.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText"));
        TextView textView3 = new TextView(context2);
        this.manage = textView3;
        textView3.setText(LocaleController.getString("ManageInviteLinks", NUM));
        textView3.setTextSize(14.0f);
        textView3.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText"));
        textView3.setBackground(Theme.createRadSelectorDrawable(ColorUtils.setAlphaComponent(Theme.getColor("windowBackgroundWhiteBlueText"), 76), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f)));
        textView3.setPadding(AndroidUtilities.dp(12.0f), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(12.0f), AndroidUtilities.dp(4.0f));
        textView3.setOnClickListener(new View.OnClickListener(tLRPC$ChatFull2, baseFragment) {
            public final /* synthetic */ TLRPC$ChatFull f$1;
            public final /* synthetic */ BaseFragment f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void onClick(View view) {
                PermanentLinkBottomSheet.this.lambda$new$1$PermanentLinkBottomSheet(this.f$1, this.f$2, view);
            }
        });
        LinearLayout linearLayout = new LinearLayout(context2);
        linearLayout.setOrientation(1);
        linearLayout.addView(rLottieImageView, LayoutHelper.createLinear(90, 90, 1, 0, 24, 0, 0));
        linearLayout.addView(textView, LayoutHelper.createLinear(-1, -2, 1, 60, 16, 60, 0));
        linearLayout.addView(textView2, LayoutHelper.createLinear(-1, -2, 1, 60, 16, 60, 0));
        linearLayout.addView(linkActionView2, LayoutHelper.createLinear(-1, -2));
        linearLayout.addView(textView3, LayoutHelper.createLinear(-2, -2, 1, 60, 26, 60, 26));
        NestedScrollView nestedScrollView = new NestedScrollView(context2);
        nestedScrollView.setVerticalScrollBarEnabled(false);
        nestedScrollView.addView(linearLayout);
        setCustomView(nestedScrollView);
        TLRPC$Chat chat = MessagesController.getInstance(UserConfig.selectedAccount).getChat(Integer.valueOf(i));
        if (chat != null && chat.username != null) {
            linkActionView2.setLink("https://t.me/" + chat.username);
            textView3.setVisibility(8);
        } else if (tLRPC$ChatFull2 == null || (tLRPC$TL_chatInviteExported = tLRPC$ChatFull2.exported_invite) == null) {
            generateLink(false);
        } else {
            linkActionView2.setLink(tLRPC$TL_chatInviteExported.link);
        }
        lambda$getThemeDescriptions$5();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$PermanentLinkBottomSheet() {
        generateLink(true);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$1 */
    public /* synthetic */ void lambda$new$1$PermanentLinkBottomSheet(TLRPC$ChatFull tLRPC$ChatFull, BaseFragment baseFragment, View view) {
        ManageLinksActivity manageLinksActivity = new ManageLinksActivity(tLRPC$ChatFull.id, 0, 0);
        manageLinksActivity.setInfo(tLRPC$ChatFull, tLRPC$ChatFull.exported_invite);
        baseFragment.presentFragment(manageLinksActivity);
        dismiss();
    }

    private void generateLink(boolean z) {
        if (!this.linkGenerating) {
            this.linkGenerating = true;
            TLRPC$TL_messages_exportChatInvite tLRPC$TL_messages_exportChatInvite = new TLRPC$TL_messages_exportChatInvite();
            tLRPC$TL_messages_exportChatInvite.legacy_revoke_permanent = true;
            tLRPC$TL_messages_exportChatInvite.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(-this.chatId);
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_messages_exportChatInvite, new RequestDelegate(z) {
                public final /* synthetic */ boolean f$1;

                {
                    this.f$1 = r2;
                }

                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    PermanentLinkBottomSheet.this.lambda$generateLink$3$PermanentLinkBottomSheet(this.f$1, tLObject, tLRPC$TL_error);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$generateLink$3 */
    public /* synthetic */ void lambda$generateLink$3$PermanentLinkBottomSheet(boolean z, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error, tLObject, z) {
            public final /* synthetic */ TLRPC$TL_error f$1;
            public final /* synthetic */ TLObject f$2;
            public final /* synthetic */ boolean f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                PermanentLinkBottomSheet.this.lambda$null$2$PermanentLinkBottomSheet(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$2 */
    public /* synthetic */ void lambda$null$2$PermanentLinkBottomSheet(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, boolean z) {
        if (tLRPC$TL_error == null) {
            this.invite = (TLRPC$TL_chatInviteExported) tLObject;
            TLRPC$ChatFull chatFull = MessagesController.getInstance(this.currentAccount).getChatFull(this.chatId);
            if (chatFull != null) {
                chatFull.exported_invite = this.invite;
            }
            this.linkActionView.setLink(this.invite.link);
            if (z && this.fragment != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage(LocaleController.getString("RevokeAlertNewLink", NUM));
                builder.setTitle(LocaleController.getString("RevokeLink", NUM));
                builder.setNegativeButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
                this.fragment.showDialog(builder.create());
            }
        }
        this.linkGenerating = false;
    }

    public void show() {
        super.show();
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                PermanentLinkBottomSheet.this.lambda$show$4$PermanentLinkBottomSheet();
            }
        }, 50);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$show$4 */
    public /* synthetic */ void lambda$show$4$PermanentLinkBottomSheet() {
        this.linkIcon.start();
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        $$Lambda$PermanentLinkBottomSheet$I7VwQbadlOghW5t3iJxait4QiEY r10 = new ThemeDescription.ThemeDescriptionDelegate() {
            public final void didSetColor() {
                PermanentLinkBottomSheet.this.lambda$getThemeDescriptions$5$PermanentLinkBottomSheet();
            }
        };
        arrayList.add(new ThemeDescription(this.titleView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.subtitle, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText"));
        arrayList.add(new ThemeDescription(this.manage, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText"));
        $$Lambda$PermanentLinkBottomSheet$I7VwQbadlOghW5t3iJxait4QiEY r8 = r10;
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "featuredStickers_addButton"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "featuredStickers_buttonText"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "windowBackgroundWhiteBlueText"));
        return arrayList;
    }

    /* access modifiers changed from: private */
    /* renamed from: updateColors */
    public void lambda$getThemeDescriptions$5() {
        this.imageView.setBackground(Theme.createCircleDrawable(AndroidUtilities.dp(90.0f), Theme.getColor("featuredStickers_addButton")));
        this.manage.setBackground(Theme.createRadSelectorDrawable(ColorUtils.setAlphaComponent(Theme.getColor("windowBackgroundWhiteBlueText"), 76), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f)));
        int color = Theme.getColor("featuredStickers_buttonText");
        this.linkIcon.setLayerColor("Top.**", color);
        this.linkIcon.setLayerColor("Bottom.**", color);
        this.linkIcon.setLayerColor("Center.**", color);
        this.linkActionView.updateColors();
        setBackgroundColor(Theme.getColor("dialogBackground"));
    }

    public void dismissInternal() {
        super.dismissInternal();
    }

    public void dismiss() {
        super.dismiss();
    }
}
