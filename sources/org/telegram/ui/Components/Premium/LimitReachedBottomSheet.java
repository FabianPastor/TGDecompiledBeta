package org.telegram.ui.Components.Premium;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashSet;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.AdminedChannelCell;
import org.telegram.ui.Cells.GroupCreateUserCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Components.BottomSheetWithRecyclerListView;
import org.telegram.ui.Components.FlickerLoadingView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerItemsEnterAnimator;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.PremiumPreviewFragment;

public class LimitReachedBottomSheet extends BottomSheetWithRecyclerListView {
    public static final int TYPE_ACCOUNTS = 7;
    public static final int TYPE_CAPTION = 8;
    public static final int TYPE_CHATS_IN_FOLDER = 4;
    public static final int TYPE_FOLDERS = 3;
    public static final int TYPE_GIFS = 9;
    public static final int TYPE_LARGE_FILE = 6;
    public static final int TYPE_PIN_DIALOGS = 0;
    public static final int TYPE_PUBLIC_LINKS = 2;
    public static final int TYPE_STICKERS = 10;
    public static final int TYPE_TO_MANY_COMMUNITIES = 5;
    int chatStartRow = -1;
    ArrayList<TLRPC.Chat> chats = new ArrayList<>();
    int chatsTitleRow = -1;
    /* access modifiers changed from: private */
    public int currentValue = -1;
    View divider;
    int dividerRow = -1;
    RecyclerItemsEnterAnimator enterAnimator;
    int headerRow = -1;
    /* access modifiers changed from: private */
    public ArrayList<TLRPC.Chat> inactiveChats = new ArrayList<>();
    /* access modifiers changed from: private */
    public ArrayList<String> inactiveChatsSignatures = new ArrayList<>();
    /* access modifiers changed from: private */
    public boolean isVeryLargeFile;
    LimitParams limitParams;
    LimitPreviewView limitPreviewView;
    private boolean loading = false;
    boolean loadingAdminedChannels;
    int loadingRow = -1;
    public Runnable onShowPremiumScreenRunnable;
    public Runnable onSuccessRunnable;
    BaseFragment parentFragment;
    public boolean parentIsChannel;
    PremiumButtonView premiumButtonView;
    int rowCount;
    HashSet<TLRPC.Chat> selectedChats = new HashSet<>();
    final int type;

    public static class LimitParams {
        int defaultLimit = 0;
        String descriptionStr = null;
        String descriptionStrLocked = null;
        String descriptionStrPremium = null;
        int icon = 0;
        int premiumLimit = 0;
    }

    public static String limitTypeToServerString(int type2) {
        switch (type2) {
            case 0:
                return "double_limits__dialog_pinned";
            case 2:
                return "double_limits__channels_public";
            case 3:
                return "double_limits__dialog_filters";
            case 4:
                return "double_limits__dialog_filters_chats";
            case 5:
                return "double_limits__channels";
            case 6:
                return "double_limits__upload_max_fileparts";
            case 8:
                return "double_limits__caption_length";
            case 9:
                return "double_limits__saved_gifs";
            case 10:
                return "double_limits__stickers_faved";
            default:
                return null;
        }
    }

    public LimitReachedBottomSheet(BaseFragment fragment, Context context, int type2, int currentAccount) {
        super(fragment, false, hasFixedSize(type2));
        fixNavigationBar();
        this.parentFragment = fragment;
        this.type = type2;
        this.currentAccount = currentAccount;
        updateRows();
        if (type2 == 2) {
            loadAdminedChannels();
        } else if (type2 == 5) {
            loadInactiveChannels();
        }
    }

    public void onViewCreated(FrameLayout containerView) {
        super.onViewCreated(containerView);
        Context context = containerView.getContext();
        this.premiumButtonView = new PremiumButtonView(context, true);
        updatePremiumButtonText();
        if (!this.hasFixedSize) {
            AnonymousClass1 r1 = new View(context) {
                /* access modifiers changed from: protected */
                public void onDraw(Canvas canvas) {
                    super.onDraw(canvas);
                    canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), 1.0f, Theme.dividerPaint);
                }
            };
            this.divider = r1;
            r1.setBackgroundColor(Theme.getColor("dialogBackground"));
            containerView.addView(this.divider, LayoutHelper.createFrame(-1, 72.0f, 80, 0.0f, 0.0f, 0.0f, 0.0f));
        }
        containerView.addView(this.premiumButtonView, LayoutHelper.createFrame(-1, 48.0f, 80, 16.0f, 0.0f, 16.0f, 12.0f));
        this.recyclerListView.setPadding(0, 0, 0, AndroidUtilities.dp(72.0f));
        this.recyclerListView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new LimitReachedBottomSheet$$ExternalSyntheticLambda10(this));
        this.recyclerListView.setOnItemLongClickListener((RecyclerListView.OnItemLongClickListener) new LimitReachedBottomSheet$$ExternalSyntheticLambda1(this));
        this.premiumButtonView.buttonLayout.setOnClickListener(new LimitReachedBottomSheet$$ExternalSyntheticLambda3(this));
        this.premiumButtonView.overlayTextView.setOnClickListener(new LimitReachedBottomSheet$$ExternalSyntheticLambda4(this));
        this.enterAnimator = new RecyclerItemsEnterAnimator(this.recyclerListView, true);
    }

    /* renamed from: lambda$onViewCreated$0$org-telegram-ui-Components-Premium-LimitReachedBottomSheet  reason: not valid java name */
    public /* synthetic */ void m1244x98a97397(View view, int position) {
        if (view instanceof AdminedChannelCell) {
            AdminedChannelCell adminedChannelCell = (AdminedChannelCell) view;
            TLRPC.Chat chat = adminedChannelCell.getCurrentChannel();
            if (this.selectedChats.contains(chat)) {
                this.selectedChats.remove(chat);
            } else {
                this.selectedChats.add(chat);
            }
            adminedChannelCell.setChecked(this.selectedChats.contains(chat), true);
            updateButton();
        } else if (view instanceof GroupCreateUserCell) {
            GroupCreateUserCell cell = (GroupCreateUserCell) view;
            TLRPC.Chat chat2 = (TLRPC.Chat) cell.getObject();
            if (this.selectedChats.contains(chat2)) {
                this.selectedChats.remove(chat2);
            } else {
                this.selectedChats.add(chat2);
            }
            cell.setChecked(this.selectedChats.contains(chat2), true);
            updateButton();
        }
    }

    /* renamed from: lambda$onViewCreated$1$org-telegram-ui-Components-Premium-LimitReachedBottomSheet  reason: not valid java name */
    public /* synthetic */ boolean m1245x9ead3ef6(View view, int position) {
        this.recyclerListView.getOnItemClickListener().onItemClick(view, position);
        view.performHapticFeedback(0);
        return false;
    }

    /* renamed from: lambda$onViewCreated$2$org-telegram-ui-Components-Premium-LimitReachedBottomSheet  reason: not valid java name */
    public /* synthetic */ void m1246xa4b10a55(View v) {
        if (UserConfig.getInstance(this.currentAccount).isPremium() || MessagesController.getInstance(this.currentAccount).premiumLocked || this.isVeryLargeFile) {
            dismiss();
            return;
        }
        BaseFragment baseFragment = this.parentFragment;
        if (baseFragment != null) {
            if (baseFragment.getVisibleDialog() != null) {
                this.parentFragment.getVisibleDialog().dismiss();
            }
            this.parentFragment.presentFragment(new PremiumPreviewFragment(limitTypeToServerString(this.type)));
            Runnable runnable = this.onShowPremiumScreenRunnable;
            if (runnable != null) {
                runnable.run();
            }
            dismiss();
        }
    }

    /* renamed from: lambda$onViewCreated$3$org-telegram-ui-Components-Premium-LimitReachedBottomSheet  reason: not valid java name */
    public /* synthetic */ void m1247xaab4d5b4(View v) {
        if (!this.selectedChats.isEmpty()) {
            int i = this.type;
            if (i == 2) {
                revokeSelectedLinks();
            } else if (i == 5) {
                leaveFromSelectedGroups();
            }
        }
    }

    public void updatePremiumButtonText() {
        if (UserConfig.getInstance(this.currentAccount).isPremium() || MessagesController.getInstance(this.currentAccount).premiumLocked || this.isVeryLargeFile) {
            this.premiumButtonView.buttonTextView.setText(LocaleController.getString(NUM));
            this.premiumButtonView.hideIcon();
            return;
        }
        this.premiumButtonView.buttonTextView.setText(LocaleController.getString("IncreaseLimit", NUM));
        this.premiumButtonView.setIcon(this.type == 7 ? NUM : NUM);
    }

    private void leaveFromSelectedGroups() {
        TLRPC.User currentUser = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId()));
        ArrayList<TLRPC.Chat> chats2 = new ArrayList<>(this.selectedChats);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(LocaleController.formatPluralString("LeaveCommunities", chats2.size(), new Object[0]));
        if (chats2.size() == 1) {
            builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("ChannelLeaveAlertWithName", NUM, chats2.get(0).title)));
        } else {
            builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("ChatsLeaveAlert", NUM, new Object[0])));
        }
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        builder.setPositiveButton(LocaleController.getString("RevokeButton", NUM), new LimitReachedBottomSheet$$ExternalSyntheticLambda2(this, chats2, currentUser));
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        TextView button = (TextView) alertDialog.getButton(-1);
        if (button != null) {
            button.setTextColor(Theme.getColor("dialogTextRed2"));
        }
    }

    /* renamed from: lambda$leaveFromSelectedGroups$4$org-telegram-ui-Components-Premium-LimitReachedBottomSheet  reason: not valid java name */
    public /* synthetic */ void m1239xe2652fc7(ArrayList chats2, TLRPC.User currentUser, DialogInterface dialogInterface, int interface2) {
        dismiss();
        for (int i = 0; i < chats2.size(); i++) {
            TLRPC.Chat chat = (TLRPC.Chat) chats2.get(i);
            MessagesController.getInstance(this.currentAccount).putChat(chat, false);
            MessagesController.getInstance(this.currentAccount).deleteParticipantFromChat(chat.id, currentUser, (TLRPC.ChatFull) null);
        }
    }

    private void updateButton() {
        if (this.selectedChats.size() > 0) {
            String str = null;
            int i = this.type;
            if (i == 2) {
                str = LocaleController.formatPluralString("RevokeLinks", this.selectedChats.size(), new Object[0]);
            } else if (i == 5) {
                str = LocaleController.formatPluralString("LeaveCommunities", this.selectedChats.size(), new Object[0]);
            }
            this.premiumButtonView.setOverlayText(str, true, true);
            return;
        }
        this.premiumButtonView.clearOverlayText();
    }

    private static boolean hasFixedSize(int type2) {
        if (type2 == 0 || type2 == 3 || type2 == 4 || type2 == 6 || type2 == 7) {
            return true;
        }
        return false;
    }

    public CharSequence getTitle() {
        return LocaleController.getString("LimitReached", NUM);
    }

    public RecyclerListView.SelectionAdapter createAdapter() {
        return new RecyclerListView.SelectionAdapter() {
            public boolean isEnabled(RecyclerView.ViewHolder holder) {
                return holder.getItemViewType() == 1 || holder.getItemViewType() == 4;
            }

            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view;
                Context context = parent.getContext();
                switch (viewType) {
                    case 1:
                        view = new AdminedChannelCell(context, new View.OnClickListener() {
                            public void onClick(View v) {
                                ArrayList<TLRPC.Chat> channels = new ArrayList<>();
                                channels.add(((AdminedChannelCell) v.getParent()).getCurrentChannel());
                                LimitReachedBottomSheet.this.revokeLinks(channels);
                            }
                        }, true, 9);
                        break;
                    case 2:
                        view = new ShadowSectionCell(context, 12, Theme.getColor("windowBackgroundGray"));
                        break;
                    case 3:
                        view = new HeaderCell(context);
                        view.setPadding(0, 0, 0, AndroidUtilities.dp(8.0f));
                        break;
                    case 4:
                        view = new GroupCreateUserCell(context, 1, 8, false);
                        break;
                    case 5:
                        FlickerLoadingView flickerLoadingView = new FlickerLoadingView(context, (Theme.ResourcesProvider) null);
                        flickerLoadingView.setViewType(LimitReachedBottomSheet.this.type == 2 ? 22 : 21);
                        flickerLoadingView.setIsSingleCell(true);
                        flickerLoadingView.setIgnoreHeightCheck(true);
                        flickerLoadingView.setItemsCount(10);
                        view = flickerLoadingView;
                        break;
                    default:
                        view = new HeaderView(LimitReachedBottomSheet.this, context);
                        break;
                }
                view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                return new RecyclerListView.Holder(view);
            }

            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                boolean z = false;
                if (holder.getItemViewType() == 4) {
                    TLRPC.Chat chat = (TLRPC.Chat) LimitReachedBottomSheet.this.inactiveChats.get(position - LimitReachedBottomSheet.this.chatStartRow);
                    GroupCreateUserCell cell = (GroupCreateUserCell) holder.itemView;
                    cell.setObject(chat, chat.title, (String) LimitReachedBottomSheet.this.inactiveChatsSignatures.get(position - LimitReachedBottomSheet.this.chatStartRow), true);
                    cell.setChecked(LimitReachedBottomSheet.this.selectedChats.contains(chat), false);
                } else if (holder.getItemViewType() == 1) {
                    TLRPC.Chat chat2 = LimitReachedBottomSheet.this.chats.get(position - LimitReachedBottomSheet.this.chatStartRow);
                    AdminedChannelCell adminedChannelCell = (AdminedChannelCell) holder.itemView;
                    TLRPC.Chat oldChat = adminedChannelCell.getCurrentChannel();
                    adminedChannelCell.setChannel(chat2, false);
                    boolean contains = LimitReachedBottomSheet.this.selectedChats.contains(chat2);
                    if (oldChat == chat2) {
                        z = true;
                    }
                    adminedChannelCell.setChecked(contains, z);
                } else if (holder.getItemViewType() == 3) {
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (LimitReachedBottomSheet.this.type == 2) {
                        headerCell.setText(LocaleController.getString("YourPublicCommunities", NUM));
                    } else {
                        headerCell.setText(LocaleController.getString("LastActiveCommunities", NUM));
                    }
                }
            }

            public int getItemViewType(int position) {
                if (LimitReachedBottomSheet.this.headerRow == position) {
                    return 0;
                }
                if (LimitReachedBottomSheet.this.dividerRow == position) {
                    return 2;
                }
                if (LimitReachedBottomSheet.this.chatsTitleRow == position) {
                    return 3;
                }
                if (LimitReachedBottomSheet.this.loadingRow == position) {
                    return 5;
                }
                if (LimitReachedBottomSheet.this.type == 5) {
                    return 4;
                }
                return 1;
            }

            public int getItemCount() {
                return LimitReachedBottomSheet.this.rowCount;
            }
        };
    }

    public void setCurrentValue(int currentValue2) {
        this.currentValue = currentValue2;
    }

    public void setVeryLargeFile(boolean b) {
        this.isVeryLargeFile = b;
        updatePremiumButtonText();
    }

    private class HeaderView extends LinearLayout {
        final /* synthetic */ LimitReachedBottomSheet this$0;

        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public HeaderView(org.telegram.ui.Components.Premium.LimitReachedBottomSheet r26, android.content.Context r27) {
            /*
                r25 = this;
                r0 = r25
                r1 = r26
                r2 = r27
                r0.this$0 = r1
                r0.<init>(r2)
                r3 = 1
                r0.setOrientation(r3)
                r4 = 1086324736(0x40CLASSNAME, float:6.0)
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r4)
                int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
                r6 = 0
                r0.setPadding(r5, r6, r4, r6)
                int r4 = r1.type
                int r5 = r26.currentAccount
                org.telegram.ui.Components.Premium.LimitReachedBottomSheet$LimitParams r4 = org.telegram.ui.Components.Premium.LimitReachedBottomSheet.getLimitParams(r4, r5)
                r1.limitParams = r4
                org.telegram.ui.Components.Premium.LimitReachedBottomSheet$LimitParams r4 = r1.limitParams
                int r4 = r4.icon
                int r5 = r26.currentAccount
                org.telegram.messenger.MessagesController r5 = org.telegram.messenger.MessagesController.getInstance(r5)
                boolean r5 = r5.premiumLocked
                if (r5 == 0) goto L_0x003e
                org.telegram.ui.Components.Premium.LimitReachedBottomSheet$LimitParams r7 = r1.limitParams
                java.lang.String r7 = r7.descriptionStrLocked
                goto L_0x005c
            L_0x003e:
                int r7 = r26.currentAccount
                org.telegram.messenger.UserConfig r7 = org.telegram.messenger.UserConfig.getInstance(r7)
                boolean r7 = r7.isPremium()
                if (r7 != 0) goto L_0x0058
                boolean r7 = r26.isVeryLargeFile
                if (r7 == 0) goto L_0x0053
                goto L_0x0058
            L_0x0053:
                org.telegram.ui.Components.Premium.LimitReachedBottomSheet$LimitParams r7 = r1.limitParams
                java.lang.String r7 = r7.descriptionStr
                goto L_0x005c
            L_0x0058:
                org.telegram.ui.Components.Premium.LimitReachedBottomSheet$LimitParams r7 = r1.limitParams
                java.lang.String r7 = r7.descriptionStrPremium
            L_0x005c:
                org.telegram.ui.Components.Premium.LimitReachedBottomSheet$LimitParams r8 = r1.limitParams
                int r8 = r8.defaultLimit
                org.telegram.ui.Components.Premium.LimitReachedBottomSheet$LimitParams r9 = r1.limitParams
                int r9 = r9.premiumLimit
                int r10 = r26.currentValue
                r11 = 1056964608(0x3var_, float:0.5)
                int r12 = r1.type
                r13 = 3
                r14 = 7
                if (r12 != r13) goto L_0x0081
                int r12 = r26.currentAccount
                org.telegram.messenger.MessagesController r12 = org.telegram.messenger.MessagesController.getInstance(r12)
                java.util.ArrayList<org.telegram.messenger.MessagesController$DialogFilter> r12 = r12.dialogFilters
                int r12 = r12.size()
                int r10 = r12 + -1
                goto L_0x0089
            L_0x0081:
                int r12 = r1.type
                if (r12 != r14) goto L_0x0089
                int r10 = org.telegram.messenger.UserConfig.getActivatedAccountsCount()
            L_0x0089:
                int r12 = r1.type
                if (r12 != 0) goto L_0x00ba
                r12 = 0
                int r13 = r26.currentAccount
                org.telegram.messenger.MessagesController r13 = org.telegram.messenger.MessagesController.getInstance(r13)
                java.util.ArrayList r13 = r13.getDialogs(r6)
                r15 = 0
                int r3 = r13.size()
            L_0x009f:
                if (r15 >= r3) goto L_0x00b9
                java.lang.Object r17 = r13.get(r15)
                r6 = r17
                org.telegram.tgnet.TLRPC$Dialog r6 = (org.telegram.tgnet.TLRPC.Dialog) r6
                boolean r14 = r6 instanceof org.telegram.tgnet.TLRPC.TL_dialogFolder
                if (r14 == 0) goto L_0x00ae
                goto L_0x00b4
            L_0x00ae:
                boolean r14 = r6.pinned
                if (r14 == 0) goto L_0x00b4
                int r12 = r12 + 1
            L_0x00b4:
                int r15 = r15 + 1
                r6 = 0
                r14 = 7
                goto L_0x009f
            L_0x00b9:
                r10 = r12
            L_0x00ba:
                int r3 = r26.currentAccount
                org.telegram.messenger.UserConfig r3 = org.telegram.messenger.UserConfig.getInstance(r3)
                boolean r3 = r3.isPremium()
                if (r3 != 0) goto L_0x00e7
                boolean r3 = r26.isVeryLargeFile
                if (r3 == 0) goto L_0x00cf
                goto L_0x00e7
            L_0x00cf:
                if (r10 >= 0) goto L_0x00d2
                r10 = r8
            L_0x00d2:
                int r3 = r1.type
                r6 = 7
                if (r3 != r6) goto L_0x00e2
                if (r10 <= r8) goto L_0x00ea
                int r3 = r10 - r8
                float r3 = (float) r3
                int r6 = r9 - r8
                float r6 = (float) r6
                float r11 = r3 / r6
                goto L_0x00ea
            L_0x00e2:
                float r3 = (float) r10
                float r6 = (float) r9
                float r11 = r3 / r6
                goto L_0x00ea
            L_0x00e7:
                r10 = r9
                r11 = 1065353216(0x3var_, float:1.0)
            L_0x00ea:
                org.telegram.ui.Components.Premium.LimitPreviewView r3 = new org.telegram.ui.Components.Premium.LimitPreviewView
                r3.<init>(r2, r4, r10, r9)
                r1.limitPreviewView = r3
                org.telegram.ui.Components.Premium.LimitPreviewView r3 = r1.limitPreviewView
                r3.setBagePosition(r11)
                org.telegram.ui.Components.Premium.LimitPreviewView r3 = r1.limitPreviewView
                int r6 = r1.type
                r3.setType(r6)
                org.telegram.ui.Components.Premium.LimitPreviewView r3 = r1.limitPreviewView
                android.widget.TextView r3 = r3.defaultCount
                r6 = 8
                r3.setVisibility(r6)
                r3 = 6
                if (r5 == 0) goto L_0x010f
                org.telegram.ui.Components.Premium.LimitPreviewView r6 = r1.limitPreviewView
                r6.setPremiumLocked()
                goto L_0x014b
            L_0x010f:
                int r12 = r26.currentAccount
                org.telegram.messenger.UserConfig r12 = org.telegram.messenger.UserConfig.getInstance(r12)
                boolean r12 = r12.isPremium()
                if (r12 != 0) goto L_0x0123
                boolean r12 = r26.isVeryLargeFile
                if (r12 == 0) goto L_0x014b
            L_0x0123:
                org.telegram.ui.Components.Premium.LimitPreviewView r12 = r1.limitPreviewView
                android.widget.TextView r12 = r12.premiumCount
                r12.setVisibility(r6)
                int r6 = r1.type
                if (r6 != r3) goto L_0x0138
                org.telegram.ui.Components.Premium.LimitPreviewView r6 = r1.limitPreviewView
                android.widget.TextView r6 = r6.defaultCount
                java.lang.String r12 = "2 GB"
                r6.setText(r12)
                goto L_0x0143
            L_0x0138:
                org.telegram.ui.Components.Premium.LimitPreviewView r6 = r1.limitPreviewView
                android.widget.TextView r6 = r6.defaultCount
                java.lang.String r12 = java.lang.Integer.toString(r8)
                r6.setText(r12)
            L_0x0143:
                org.telegram.ui.Components.Premium.LimitPreviewView r6 = r1.limitPreviewView
                android.widget.TextView r6 = r6.defaultCount
                r12 = 0
                r6.setVisibility(r12)
            L_0x014b:
                int r6 = r1.type
                r12 = 2
                if (r6 == r12) goto L_0x0155
                int r6 = r1.type
                r12 = 5
                if (r6 != r12) goto L_0x015a
            L_0x0155:
                org.telegram.ui.Components.Premium.LimitPreviewView r6 = r1.limitPreviewView
                r6.setDelayedAnimation()
            L_0x015a:
                org.telegram.ui.Components.Premium.LimitPreviewView r6 = r1.limitPreviewView
                r17 = -1
                r18 = -2
                r19 = 0
                r20 = 0
                r21 = 0
                r22 = 0
                r23 = 0
                r24 = 0
                android.widget.LinearLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createLinear(r17, r18, r19, r20, r21, r22, r23, r24)
                r0.addView(r6, r12)
                android.widget.TextView r6 = new android.widget.TextView
                r6.<init>(r2)
                java.lang.String r12 = "fonts/rmedium.ttf"
                android.graphics.Typeface r12 = org.telegram.messenger.AndroidUtilities.getTypeface(r12)
                r6.setTypeface(r12)
                int r1 = r1.type
                if (r1 != r3) goto L_0x0192
                r1 = 2131625807(0x7f0e074f, float:1.8878832E38)
                java.lang.String r3 = "FileTooLarge"
                java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
                r6.setText(r1)
                goto L_0x019e
            L_0x0192:
                r1 = 2131626402(0x7f0e09a2, float:1.888004E38)
                java.lang.String r3 = "LimitReached"
                java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
                r6.setText(r1)
            L_0x019e:
                r1 = 1101004800(0x41a00000, float:20.0)
                r3 = 1
                r6.setTextSize(r3, r1)
                java.lang.String r1 = "windowBackgroundWhiteBlackText"
                int r3 = org.telegram.ui.ActionBar.Theme.getColor(r1)
                r6.setTextColor(r3)
                r17 = -2
                r18 = -2
                r19 = 1
                r20 = 0
                r21 = 22
                r22 = 0
                r23 = 10
                android.widget.LinearLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r17, (int) r18, (int) r19, (int) r20, (int) r21, (int) r22, (int) r23)
                r0.addView(r6, r3)
                android.widget.TextView r3 = new android.widget.TextView
                r3.<init>(r2)
                android.text.SpannableStringBuilder r12 = org.telegram.messenger.AndroidUtilities.replaceTags(r7)
                r3.setText(r12)
                r12 = 1096810496(0x41600000, float:14.0)
                r13 = 1
                r3.setTextSize(r13, r12)
                r3.setGravity(r13)
                int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
                r3.setTextColor(r1)
                r12 = -2
                r13 = -2
                r14 = 0
                r15 = 24
                r16 = 0
                r17 = 24
                r18 = 24
                android.widget.LinearLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r12, (int) r13, (int) r14, (int) r15, (int) r16, (int) r17, (int) r18)
                r0.addView(r3, r1)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.Premium.LimitReachedBottomSheet.HeaderView.<init>(org.telegram.ui.Components.Premium.LimitReachedBottomSheet, android.content.Context):void");
        }
    }

    /* access modifiers changed from: private */
    public static LimitParams getLimitParams(int type2, int currentAccount) {
        LimitParams limitParams2 = new LimitParams();
        if (type2 == 0) {
            limitParams2.defaultLimit = MessagesController.getInstance(currentAccount).dialogFiltersPinnedLimitDefault;
            limitParams2.premiumLimit = MessagesController.getInstance(currentAccount).dialogFiltersPinnedLimitPremium;
            limitParams2.icon = NUM;
            limitParams2.descriptionStr = LocaleController.formatString("LimitReachedPinDialogs", NUM, Integer.valueOf(limitParams2.defaultLimit), Integer.valueOf(limitParams2.premiumLimit));
            limitParams2.descriptionStrPremium = LocaleController.formatString("LimitReachedPinDialogsPremium", NUM, Integer.valueOf(limitParams2.premiumLimit));
            limitParams2.descriptionStrLocked = LocaleController.formatString("LimitReachedPinDialogsLocked", NUM, Integer.valueOf(limitParams2.defaultLimit));
        } else if (type2 == 2) {
            limitParams2.defaultLimit = MessagesController.getInstance(currentAccount).publicLinksLimitDefault;
            limitParams2.premiumLimit = MessagesController.getInstance(currentAccount).publicLinksLimitPremium;
            limitParams2.icon = NUM;
            limitParams2.descriptionStr = LocaleController.formatString("LimitReachedPublicLinks", NUM, Integer.valueOf(limitParams2.defaultLimit), Integer.valueOf(limitParams2.premiumLimit));
            limitParams2.descriptionStrPremium = LocaleController.formatString("LimitReachedPublicLinksPremium", NUM, Integer.valueOf(limitParams2.premiumLimit));
            limitParams2.descriptionStrLocked = LocaleController.formatString("LimitReachedPublicLinksLocked", NUM, Integer.valueOf(limitParams2.defaultLimit));
        } else if (type2 == 3) {
            limitParams2.defaultLimit = MessagesController.getInstance(currentAccount).dialogFiltersLimitDefault;
            limitParams2.premiumLimit = MessagesController.getInstance(currentAccount).dialogFiltersLimitPremium;
            limitParams2.icon = NUM;
            limitParams2.descriptionStr = LocaleController.formatString("LimitReachedFolders", NUM, Integer.valueOf(limitParams2.defaultLimit), Integer.valueOf(limitParams2.premiumLimit));
            limitParams2.descriptionStrPremium = LocaleController.formatString("LimitReachedFoldersPremium", NUM, Integer.valueOf(limitParams2.premiumLimit));
            limitParams2.descriptionStrLocked = LocaleController.formatString("LimitReachedFoldersLocked", NUM, Integer.valueOf(limitParams2.defaultLimit));
        } else if (type2 == 4) {
            limitParams2.defaultLimit = MessagesController.getInstance(currentAccount).dialogFiltersChatsLimitDefault;
            limitParams2.premiumLimit = MessagesController.getInstance(currentAccount).dialogFiltersChatsLimitPremium;
            limitParams2.icon = NUM;
            limitParams2.descriptionStr = LocaleController.formatString("LimitReachedChatInFolders", NUM, Integer.valueOf(limitParams2.defaultLimit), Integer.valueOf(limitParams2.premiumLimit));
            limitParams2.descriptionStrPremium = LocaleController.formatString("LimitReachedChatInFoldersPremium", NUM, Integer.valueOf(limitParams2.premiumLimit));
            limitParams2.descriptionStrLocked = LocaleController.formatString("LimitReachedChatInFoldersLocked", NUM, Integer.valueOf(limitParams2.defaultLimit));
        } else if (type2 == 5) {
            limitParams2.defaultLimit = MessagesController.getInstance(currentAccount).channelsLimitDefault;
            limitParams2.premiumLimit = MessagesController.getInstance(currentAccount).channelsLimitPremium;
            limitParams2.icon = NUM;
            limitParams2.descriptionStr = LocaleController.formatString("LimitReachedCommunities", NUM, Integer.valueOf(limitParams2.defaultLimit), Integer.valueOf(limitParams2.premiumLimit));
            limitParams2.descriptionStrPremium = LocaleController.formatString("LimitReachedCommunitiesPremium", NUM, Integer.valueOf(limitParams2.premiumLimit));
            limitParams2.descriptionStrLocked = LocaleController.formatString("LimitReachedCommunitiesLocked", NUM, Integer.valueOf(limitParams2.defaultLimit));
        } else if (type2 == 6) {
            limitParams2.defaultLimit = 100;
            limitParams2.premiumLimit = 200;
            limitParams2.icon = NUM;
            limitParams2.descriptionStr = LocaleController.formatString("LimitReachedFileSize", NUM, "2 GB", "4 GB");
            limitParams2.descriptionStrPremium = LocaleController.formatString("LimitReachedFileSizePremium", NUM, "4 GB");
            limitParams2.descriptionStrLocked = LocaleController.formatString("LimitReachedFileSizeLocked", NUM, "2 GB");
        } else if (type2 == 7) {
            limitParams2.defaultLimit = 3;
            limitParams2.premiumLimit = 4;
            limitParams2.icon = NUM;
            limitParams2.descriptionStr = LocaleController.formatString("LimitReachedAccounts", NUM, Integer.valueOf(limitParams2.defaultLimit), Integer.valueOf(limitParams2.premiumLimit));
            limitParams2.descriptionStrPremium = LocaleController.formatString("LimitReachedAccountsPremium", NUM, Integer.valueOf(limitParams2.premiumLimit));
            limitParams2.descriptionStrLocked = LocaleController.formatString("LimitReachedAccountsPremium", NUM, Integer.valueOf(limitParams2.defaultLimit));
        }
        return limitParams2;
    }

    private void loadAdminedChannels() {
        this.loadingAdminedChannels = true;
        this.loading = true;
        updateRows();
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC.TL_channels_getAdminedPublicChannels(), new LimitReachedBottomSheet$$ExternalSyntheticLambda7(this));
    }

    /* renamed from: lambda$loadAdminedChannels$6$org-telegram-ui-Components-Premium-LimitReachedBottomSheet  reason: not valid java name */
    public /* synthetic */ void m1241xba41a75d(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new LimitReachedBottomSheet$$ExternalSyntheticLambda6(this, response));
    }

    /* renamed from: lambda$loadAdminedChannels$5$org-telegram-ui-Components-Premium-LimitReachedBottomSheet  reason: not valid java name */
    public /* synthetic */ void m1240xb43ddbfe(TLObject response) {
        this.loadingAdminedChannels = false;
        if (response != null) {
            this.chats.clear();
            this.chats.addAll(((TLRPC.TL_messages_chats) response).chats);
            this.loading = false;
            this.enterAnimator.showItemsAnimated(this.chatsTitleRow + 4);
            int savedTop = 0;
            int i = 0;
            while (true) {
                if (i >= this.recyclerListView.getChildCount()) {
                    break;
                } else if (this.recyclerListView.getChildAt(i) instanceof HeaderView) {
                    savedTop = this.recyclerListView.getChildAt(i).getTop();
                    break;
                } else {
                    i++;
                }
            }
            updateRows();
            if (this.headerRow >= 0 && savedTop != 0) {
                ((LinearLayoutManager) this.recyclerListView.getLayoutManager()).scrollToPositionWithOffset(this.headerRow + 1, savedTop);
            }
        }
        int currentValue2 = Math.max(this.chats.size(), this.limitParams.defaultLimit);
        this.limitPreviewView.setIconValue(currentValue2);
        this.limitPreviewView.setBagePosition(((float) currentValue2) / ((float) this.limitParams.premiumLimit));
        this.limitPreviewView.startDelayedAnimation();
    }

    private void updateRows() {
        this.rowCount = 0;
        this.dividerRow = -1;
        this.chatStartRow = -1;
        this.loadingRow = -1;
        this.rowCount = 0 + 1;
        this.headerRow = 0;
        if (!hasFixedSize(this.type)) {
            int i = this.rowCount;
            int i2 = i + 1;
            this.rowCount = i2;
            this.dividerRow = i;
            int i3 = i2 + 1;
            this.rowCount = i3;
            this.chatsTitleRow = i2;
            if (this.loading) {
                this.rowCount = i3 + 1;
                this.loadingRow = i3;
            } else {
                this.chatStartRow = i3;
                if (this.type == 5) {
                    this.rowCount = i3 + this.inactiveChats.size();
                } else {
                    this.rowCount = i3 + this.chats.size();
                }
            }
        }
        notifyDataSetChanged();
    }

    private void revokeSelectedLinks() {
        revokeLinks(new ArrayList<>(this.selectedChats));
    }

    /* access modifiers changed from: private */
    public void revokeLinks(ArrayList<TLRPC.Chat> channels) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(LocaleController.formatPluralString("RevokeLinks", channels.size(), new Object[0]));
        if (channels.size() == 1) {
            TLRPC.Chat channel = channels.get(0);
            if (this.parentIsChannel) {
                builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("RevokeLinkAlertChannel", NUM, MessagesController.getInstance(this.currentAccount).linkPrefix + "/" + channel.username, channel.title)));
            } else {
                builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("RevokeLinkAlert", NUM, MessagesController.getInstance(this.currentAccount).linkPrefix + "/" + channel.username, channel.title)));
            }
        } else if (this.parentIsChannel) {
            builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("RevokeLinksAlertChannel", NUM, new Object[0])));
        } else {
            builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("RevokeLinksAlert", NUM, new Object[0])));
        }
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        builder.setPositiveButton(LocaleController.getString("RevokeButton", NUM), new LimitReachedBottomSheet$$ExternalSyntheticLambda0(this, channels));
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        TextView button = (TextView) alertDialog.getButton(-1);
        if (button != null) {
            button.setTextColor(Theme.getColor("dialogTextRed2"));
        }
    }

    /* renamed from: lambda$revokeLinks$8$org-telegram-ui-Components-Premium-LimitReachedBottomSheet  reason: not valid java name */
    public /* synthetic */ void m1249xcbc3b480(ArrayList channels, DialogInterface dialogInterface, int interface2) {
        dismiss();
        for (int i = 0; i < channels.size(); i++) {
            TLRPC.TL_channels_updateUsername req1 = new TLRPC.TL_channels_updateUsername();
            req1.channel = MessagesController.getInputChannel((TLRPC.Chat) channels.get(i));
            req1.username = "";
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req1, new LimitReachedBottomSheet$$ExternalSyntheticLambda9(this), 64);
        }
    }

    /* renamed from: lambda$revokeLinks$7$org-telegram-ui-Components-Premium-LimitReachedBottomSheet  reason: not valid java name */
    public /* synthetic */ void m1248xc5bfe921(TLObject response1, TLRPC.TL_error error1) {
        if (response1 instanceof TLRPC.TL_boolTrue) {
            AndroidUtilities.runOnUIThread(this.onSuccessRunnable);
        }
    }

    private void loadInactiveChannels() {
        this.loading = true;
        updateRows();
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC.TL_channels_getInactiveChannels(), new LimitReachedBottomSheet$$ExternalSyntheticLambda8(this));
    }

    /* renamed from: lambda$loadInactiveChannels$10$org-telegram-ui-Components-Premium-LimitReachedBottomSheet  reason: not valid java name */
    public /* synthetic */ void m1242x3f9e20c5(TLObject response, TLRPC.TL_error error) {
        String dateFormat;
        if (error == null) {
            TLRPC.TL_messages_inactiveChats chats2 = (TLRPC.TL_messages_inactiveChats) response;
            ArrayList<String> signatures = new ArrayList<>();
            for (int i = 0; i < chats2.chats.size(); i++) {
                TLRPC.Chat chat = chats2.chats.get(i);
                int daysDif = (ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() - chats2.dates.get(i).intValue()) / 86400;
                if (daysDif < 30) {
                    dateFormat = LocaleController.formatPluralString("Days", daysDif, new Object[0]);
                } else if (daysDif < 365) {
                    dateFormat = LocaleController.formatPluralString("Months", daysDif / 30, new Object[0]);
                } else {
                    dateFormat = LocaleController.formatPluralString("Years", daysDif / 365, new Object[0]);
                }
                if (ChatObject.isMegagroup(chat)) {
                    signatures.add(LocaleController.formatString("InactiveChatSignature", NUM, LocaleController.formatPluralString("Members", chat.participants_count, new Object[0]), dateFormat));
                } else if (ChatObject.isChannel(chat)) {
                    signatures.add(LocaleController.formatString("InactiveChannelSignature", NUM, dateFormat));
                } else {
                    signatures.add(LocaleController.formatString("InactiveChatSignature", NUM, LocaleController.formatPluralString("Members", chat.participants_count, new Object[0]), dateFormat));
                }
            }
            AndroidUtilities.runOnUIThread(new LimitReachedBottomSheet$$ExternalSyntheticLambda5(this, signatures, chats2));
        }
    }

    /* renamed from: lambda$loadInactiveChannels$9$org-telegram-ui-Components-Premium-LimitReachedBottomSheet  reason: not valid java name */
    public /* synthetic */ void m1243x39064d37(ArrayList signatures, TLRPC.TL_messages_inactiveChats chats2) {
        this.inactiveChatsSignatures.clear();
        this.inactiveChats.clear();
        this.inactiveChatsSignatures.addAll(signatures);
        this.inactiveChats.addAll(chats2.chats);
        this.loading = false;
        this.enterAnimator.showItemsAnimated(this.chatsTitleRow + 4);
        int savedTop = 0;
        int i = 0;
        while (true) {
            if (i >= this.recyclerListView.getChildCount()) {
                break;
            } else if (this.recyclerListView.getChildAt(i) instanceof HeaderView) {
                savedTop = this.recyclerListView.getChildAt(i).getTop();
                break;
            } else {
                i++;
            }
        }
        updateRows();
        if (this.headerRow >= 0 && savedTop != 0) {
            ((LinearLayoutManager) this.recyclerListView.getLayoutManager()).scrollToPositionWithOffset(this.headerRow + 1, savedTop);
        }
        int currentValue2 = Math.max(this.inactiveChats.size(), this.limitParams.defaultLimit);
        this.limitPreviewView.setIconValue(currentValue2);
        this.limitPreviewView.setBagePosition(((float) currentValue2) / ((float) this.limitParams.premiumLimit));
        this.limitPreviewView.startDelayedAnimation();
    }
}
