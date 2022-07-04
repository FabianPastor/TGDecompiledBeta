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
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.tgnet.TLRPC$TL_boolTrue;
import org.telegram.tgnet.TLRPC$TL_channels_getAdminedPublicChannels;
import org.telegram.tgnet.TLRPC$TL_channels_getInactiveChannels;
import org.telegram.tgnet.TLRPC$TL_channels_updateUsername;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_chats;
import org.telegram.tgnet.TLRPC$TL_messages_inactiveChats;
import org.telegram.tgnet.TLRPC$User;
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
    int chatStartRow = -1;
    ArrayList<TLRPC$Chat> chats = new ArrayList<>();
    int chatsTitleRow = -1;
    /* access modifiers changed from: private */
    public int currentValue = -1;
    View divider;
    int dividerRow = -1;
    RecyclerItemsEnterAnimator enterAnimator;
    int headerRow = -1;
    /* access modifiers changed from: private */
    public ArrayList<TLRPC$Chat> inactiveChats = new ArrayList<>();
    /* access modifiers changed from: private */
    public ArrayList<String> inactiveChatsSignatures = new ArrayList<>();
    /* access modifiers changed from: private */
    public boolean isVeryLargeFile;
    LimitParams limitParams;
    LimitPreviewView limitPreviewView;
    private boolean loading = false;
    int loadingRow = -1;
    public Runnable onShowPremiumScreenRunnable;
    public Runnable onSuccessRunnable;
    BaseFragment parentFragment;
    public boolean parentIsChannel;
    PremiumButtonView premiumButtonView;
    int rowCount;
    HashSet<TLRPC$Chat> selectedChats = new HashSet<>();
    final int type;

    public static class LimitParams {
        int defaultLimit = 0;
        String descriptionStr = null;
        String descriptionStrLocked = null;
        String descriptionStrPremium = null;
        int icon = 0;
        int premiumLimit = 0;
    }

    private static boolean hasFixedSize(int i) {
        return i == 0 || i == 3 || i == 4 || i == 6 || i == 7;
    }

    public static String limitTypeToServerString(int i) {
        switch (i) {
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

    public LimitReachedBottomSheet(BaseFragment baseFragment, Context context, int i, int i2) {
        super(baseFragment, false, hasFixedSize(i));
        fixNavigationBar();
        this.parentFragment = baseFragment;
        this.type = i;
        this.currentAccount = i2;
        updateRows();
        if (i == 2) {
            loadAdminedChannels();
        } else if (i == 5) {
            loadInactiveChannels();
        }
    }

    public void onViewCreated(FrameLayout frameLayout) {
        super.onViewCreated(frameLayout);
        Context context = frameLayout.getContext();
        this.premiumButtonView = new PremiumButtonView(context, true);
        updatePremiumButtonText();
        if (!this.hasFixedSize) {
            AnonymousClass1 r1 = new View(this, context) {
                /* access modifiers changed from: protected */
                public void onDraw(Canvas canvas) {
                    super.onDraw(canvas);
                    canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), 1.0f, Theme.dividerPaint);
                }
            };
            this.divider = r1;
            r1.setBackgroundColor(Theme.getColor("dialogBackground"));
            frameLayout.addView(this.divider, LayoutHelper.createFrame(-1, 72.0f, 80, 0.0f, 0.0f, 0.0f, 0.0f));
        }
        frameLayout.addView(this.premiumButtonView, LayoutHelper.createFrame(-1, 48.0f, 80, 16.0f, 0.0f, 16.0f, 12.0f));
        this.recyclerListView.setPadding(0, 0, 0, AndroidUtilities.dp(72.0f));
        this.recyclerListView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new LimitReachedBottomSheet$$ExternalSyntheticLambda9(this));
        this.recyclerListView.setOnItemLongClickListener((RecyclerListView.OnItemLongClickListener) new LimitReachedBottomSheet$$ExternalSyntheticLambda10(this));
        this.premiumButtonView.buttonLayout.setOnClickListener(new LimitReachedBottomSheet$$ExternalSyntheticLambda2(this));
        this.premiumButtonView.overlayTextView.setOnClickListener(new LimitReachedBottomSheet$$ExternalSyntheticLambda3(this));
        this.enterAnimator = new RecyclerItemsEnterAnimator(this.recyclerListView, true);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onViewCreated$0(View view, int i) {
        if (view instanceof AdminedChannelCell) {
            AdminedChannelCell adminedChannelCell = (AdminedChannelCell) view;
            TLRPC$Chat currentChannel = adminedChannelCell.getCurrentChannel();
            if (this.selectedChats.contains(currentChannel)) {
                this.selectedChats.remove(currentChannel);
            } else {
                this.selectedChats.add(currentChannel);
            }
            adminedChannelCell.setChecked(this.selectedChats.contains(currentChannel), true);
            updateButton();
        } else if (view instanceof GroupCreateUserCell) {
            GroupCreateUserCell groupCreateUserCell = (GroupCreateUserCell) view;
            TLRPC$Chat tLRPC$Chat = (TLRPC$Chat) groupCreateUserCell.getObject();
            if (this.selectedChats.contains(tLRPC$Chat)) {
                this.selectedChats.remove(tLRPC$Chat);
            } else {
                this.selectedChats.add(tLRPC$Chat);
            }
            groupCreateUserCell.setChecked(this.selectedChats.contains(tLRPC$Chat), true);
            updateButton();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$onViewCreated$1(View view, int i) {
        this.recyclerListView.getOnItemClickListener().onItemClick(view, i);
        view.performHapticFeedback(0);
        return false;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onViewCreated$2(View view) {
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

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onViewCreated$3(View view) {
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
        TLRPC$User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId()));
        ArrayList arrayList = new ArrayList(this.selectedChats);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(LocaleController.formatPluralString("LeaveCommunities", arrayList.size(), new Object[0]));
        if (arrayList.size() == 1) {
            builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("ChannelLeaveAlertWithName", NUM, ((TLRPC$Chat) arrayList.get(0)).title)));
        } else {
            builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("ChatsLeaveAlert", NUM, new Object[0])));
        }
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        builder.setPositiveButton(LocaleController.getString("RevokeButton", NUM), new LimitReachedBottomSheet$$ExternalSyntheticLambda1(this, arrayList, user));
        AlertDialog create = builder.create();
        create.show();
        TextView textView = (TextView) create.getButton(-1);
        if (textView != null) {
            textView.setTextColor(Theme.getColor("dialogTextRed2"));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$leaveFromSelectedGroups$4(ArrayList arrayList, TLRPC$User tLRPC$User, DialogInterface dialogInterface, int i) {
        dismiss();
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            TLRPC$Chat tLRPC$Chat = (TLRPC$Chat) arrayList.get(i2);
            MessagesController.getInstance(this.currentAccount).putChat(tLRPC$Chat, false);
            MessagesController.getInstance(this.currentAccount).deleteParticipantFromChat(tLRPC$Chat.id, tLRPC$User, (TLRPC$ChatFull) null);
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

    public CharSequence getTitle() {
        return LocaleController.getString("LimitReached", NUM);
    }

    public RecyclerListView.SelectionAdapter createAdapter() {
        return new RecyclerListView.SelectionAdapter() {
            public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
                return viewHolder.getItemViewType() == 1 || viewHolder.getItemViewType() == 4;
            }

            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                HeaderView headerView;
                Context context = viewGroup.getContext();
                if (i == 1) {
                    headerView = new AdminedChannelCell(context, new View.OnClickListener() {
                        public void onClick(View view) {
                            ArrayList arrayList = new ArrayList();
                            arrayList.add(((AdminedChannelCell) view.getParent()).getCurrentChannel());
                            LimitReachedBottomSheet.this.revokeLinks(arrayList);
                        }
                    }, true, 9);
                } else if (i == 2) {
                    headerView = new ShadowSectionCell(context, 12, Theme.getColor("windowBackgroundGray"));
                } else if (i == 3) {
                    HeaderCell headerCell = new HeaderCell(context);
                    headerCell.setPadding(0, 0, 0, AndroidUtilities.dp(8.0f));
                    headerView = headerCell;
                } else if (i == 4) {
                    headerView = new GroupCreateUserCell(context, 1, 8, false);
                } else if (i != 5) {
                    headerView = new HeaderView(LimitReachedBottomSheet.this, context);
                } else {
                    FlickerLoadingView flickerLoadingView = new FlickerLoadingView(context, (Theme.ResourcesProvider) null);
                    flickerLoadingView.setViewType(LimitReachedBottomSheet.this.type == 2 ? 22 : 21);
                    flickerLoadingView.setIsSingleCell(true);
                    flickerLoadingView.setIgnoreHeightCheck(true);
                    flickerLoadingView.setItemsCount(10);
                    headerView = flickerLoadingView;
                }
                headerView.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                return new RecyclerListView.Holder(headerView);
            }

            public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
                boolean z = false;
                if (viewHolder.getItemViewType() == 4) {
                    TLRPC$Chat tLRPC$Chat = (TLRPC$Chat) LimitReachedBottomSheet.this.inactiveChats.get(i - LimitReachedBottomSheet.this.chatStartRow);
                    GroupCreateUserCell groupCreateUserCell = (GroupCreateUserCell) viewHolder.itemView;
                    groupCreateUserCell.setObject(tLRPC$Chat, tLRPC$Chat.title, (String) LimitReachedBottomSheet.this.inactiveChatsSignatures.get(i - LimitReachedBottomSheet.this.chatStartRow), true);
                    groupCreateUserCell.setChecked(LimitReachedBottomSheet.this.selectedChats.contains(tLRPC$Chat), false);
                } else if (viewHolder.getItemViewType() == 1) {
                    LimitReachedBottomSheet limitReachedBottomSheet = LimitReachedBottomSheet.this;
                    TLRPC$Chat tLRPC$Chat2 = limitReachedBottomSheet.chats.get(i - limitReachedBottomSheet.chatStartRow);
                    AdminedChannelCell adminedChannelCell = (AdminedChannelCell) viewHolder.itemView;
                    TLRPC$Chat currentChannel = adminedChannelCell.getCurrentChannel();
                    adminedChannelCell.setChannel(tLRPC$Chat2, false);
                    boolean contains = LimitReachedBottomSheet.this.selectedChats.contains(tLRPC$Chat2);
                    if (currentChannel == tLRPC$Chat2) {
                        z = true;
                    }
                    adminedChannelCell.setChecked(contains, z);
                } else if (viewHolder.getItemViewType() == 3) {
                    HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
                    if (LimitReachedBottomSheet.this.type == 2) {
                        headerCell.setText(LocaleController.getString("YourPublicCommunities", NUM));
                    } else {
                        headerCell.setText(LocaleController.getString("LastActiveCommunities", NUM));
                    }
                }
            }

            public int getItemViewType(int i) {
                LimitReachedBottomSheet limitReachedBottomSheet = LimitReachedBottomSheet.this;
                if (limitReachedBottomSheet.headerRow == i) {
                    return 0;
                }
                if (limitReachedBottomSheet.dividerRow == i) {
                    return 2;
                }
                if (limitReachedBottomSheet.chatsTitleRow == i) {
                    return 3;
                }
                if (limitReachedBottomSheet.loadingRow == i) {
                    return 5;
                }
                return limitReachedBottomSheet.type == 5 ? 4 : 1;
            }

            public int getItemCount() {
                return LimitReachedBottomSheet.this.rowCount;
            }
        };
    }

    public void setCurrentValue(int i) {
        this.currentValue = i;
    }

    public void setVeryLargeFile(boolean z) {
        this.isVeryLargeFile = z;
        updatePremiumButtonText();
    }

    private class HeaderView extends LinearLayout {
        /* JADX WARNING: Illegal instructions before constructor call */
        @android.annotation.SuppressLint({"SetTextI18n"})
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public HeaderView(org.telegram.ui.Components.Premium.LimitReachedBottomSheet r18, android.content.Context r19) {
            /*
                r17 = this;
                r0 = r17
                r1 = r18
                r2 = r19
                r0.<init>(r2)
                r3 = 1
                r0.setOrientation(r3)
                r4 = 1086324736(0x40CLASSNAME, float:6.0)
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r4)
                int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
                r6 = 0
                r0.setPadding(r5, r6, r4, r6)
                int r4 = r1.type
                int r5 = r18.currentAccount
                org.telegram.ui.Components.Premium.LimitReachedBottomSheet$LimitParams r4 = org.telegram.ui.Components.Premium.LimitReachedBottomSheet.getLimitParams(r4, r5)
                r1.limitParams = r4
                int r4 = r4.icon
                int r5 = r18.currentAccount
                org.telegram.messenger.MessagesController r5 = org.telegram.messenger.MessagesController.getInstance(r5)
                boolean r5 = r5.premiumLocked
                if (r5 == 0) goto L_0x003a
                org.telegram.ui.Components.Premium.LimitReachedBottomSheet$LimitParams r7 = r1.limitParams
                java.lang.String r7 = r7.descriptionStrLocked
                goto L_0x0058
            L_0x003a:
                int r7 = r18.currentAccount
                org.telegram.messenger.UserConfig r7 = org.telegram.messenger.UserConfig.getInstance(r7)
                boolean r7 = r7.isPremium()
                if (r7 != 0) goto L_0x0054
                boolean r7 = r18.isVeryLargeFile
                if (r7 == 0) goto L_0x004f
                goto L_0x0054
            L_0x004f:
                org.telegram.ui.Components.Premium.LimitReachedBottomSheet$LimitParams r7 = r1.limitParams
                java.lang.String r7 = r7.descriptionStr
                goto L_0x0058
            L_0x0054:
                org.telegram.ui.Components.Premium.LimitReachedBottomSheet$LimitParams r7 = r1.limitParams
                java.lang.String r7 = r7.descriptionStrPremium
            L_0x0058:
                org.telegram.ui.Components.Premium.LimitReachedBottomSheet$LimitParams r8 = r1.limitParams
                int r9 = r8.defaultLimit
                int r8 = r8.premiumLimit
                int r10 = r18.currentValue
                int r12 = r1.type
                r13 = 3
                r14 = 7
                if (r12 != r13) goto L_0x0078
                int r10 = r18.currentAccount
                org.telegram.messenger.MessagesController r10 = org.telegram.messenger.MessagesController.getInstance(r10)
                java.util.ArrayList<org.telegram.messenger.MessagesController$DialogFilter> r10 = r10.dialogFilters
                int r10 = r10.size()
                int r10 = r10 - r3
                goto L_0x007e
            L_0x0078:
                if (r12 != r14) goto L_0x007e
                int r10 = org.telegram.messenger.UserConfig.getActivatedAccountsCount()
            L_0x007e:
                int r12 = r1.type
                if (r12 != 0) goto L_0x00ae
                int r10 = r18.currentAccount
                org.telegram.messenger.MessagesController r10 = org.telegram.messenger.MessagesController.getInstance(r10)
                java.util.ArrayList r10 = r10.getDialogs(r6)
                int r12 = r10.size()
                r13 = 0
                r15 = 0
            L_0x0094:
                if (r15 >= r12) goto L_0x00ad
                java.lang.Object r16 = r10.get(r15)
                r11 = r16
                org.telegram.tgnet.TLRPC$Dialog r11 = (org.telegram.tgnet.TLRPC$Dialog) r11
                boolean r3 = r11 instanceof org.telegram.tgnet.TLRPC$TL_dialogFolder
                if (r3 == 0) goto L_0x00a3
                goto L_0x00a9
            L_0x00a3:
                boolean r3 = r11.pinned
                if (r3 == 0) goto L_0x00a9
                int r13 = r13 + 1
            L_0x00a9:
                int r15 = r15 + 1
                r3 = 1
                goto L_0x0094
            L_0x00ad:
                r10 = r13
            L_0x00ae:
                int r3 = r18.currentAccount
                org.telegram.messenger.UserConfig r3 = org.telegram.messenger.UserConfig.getInstance(r3)
                boolean r3 = r3.isPremium()
                if (r3 != 0) goto L_0x00db
                boolean r3 = r18.isVeryLargeFile
                if (r3 == 0) goto L_0x00c3
                goto L_0x00db
            L_0x00c3:
                if (r10 >= 0) goto L_0x00c6
                r10 = r9
            L_0x00c6:
                int r3 = r1.type
                if (r3 != r14) goto L_0x00d6
                if (r10 <= r9) goto L_0x00d3
                int r3 = r10 - r9
                float r3 = (float) r3
                int r11 = r8 - r9
                float r11 = (float) r11
                goto L_0x00d8
            L_0x00d3:
                r11 = 1056964608(0x3var_, float:0.5)
                goto L_0x00de
            L_0x00d6:
                float r3 = (float) r10
                float r11 = (float) r8
            L_0x00d8:
                float r11 = r3 / r11
                goto L_0x00de
            L_0x00db:
                r11 = 1065353216(0x3var_, float:1.0)
                r10 = r8
            L_0x00de:
                org.telegram.ui.Components.Premium.LimitPreviewView r3 = new org.telegram.ui.Components.Premium.LimitPreviewView
                r3.<init>(r2, r4, r10, r8)
                r1.limitPreviewView = r3
                r3.setBagePosition(r11)
                org.telegram.ui.Components.Premium.LimitPreviewView r3 = r1.limitPreviewView
                int r4 = r1.type
                r3.setType(r4)
                org.telegram.ui.Components.Premium.LimitPreviewView r3 = r1.limitPreviewView
                android.widget.TextView r3 = r3.defaultCount
                r4 = 8
                r3.setVisibility(r4)
                r3 = 6
                if (r5 == 0) goto L_0x0101
                org.telegram.ui.Components.Premium.LimitPreviewView r4 = r1.limitPreviewView
                r4.setPremiumLocked()
                goto L_0x013c
            L_0x0101:
                int r5 = r18.currentAccount
                org.telegram.messenger.UserConfig r5 = org.telegram.messenger.UserConfig.getInstance(r5)
                boolean r5 = r5.isPremium()
                if (r5 != 0) goto L_0x0115
                boolean r5 = r18.isVeryLargeFile
                if (r5 == 0) goto L_0x013c
            L_0x0115:
                org.telegram.ui.Components.Premium.LimitPreviewView r5 = r1.limitPreviewView
                android.widget.TextView r5 = r5.premiumCount
                r5.setVisibility(r4)
                int r4 = r1.type
                if (r4 != r3) goto L_0x012a
                org.telegram.ui.Components.Premium.LimitPreviewView r4 = r1.limitPreviewView
                android.widget.TextView r4 = r4.defaultCount
                java.lang.String r5 = "2 GB"
                r4.setText(r5)
                goto L_0x0135
            L_0x012a:
                org.telegram.ui.Components.Premium.LimitPreviewView r4 = r1.limitPreviewView
                android.widget.TextView r4 = r4.defaultCount
                java.lang.String r5 = java.lang.Integer.toString(r9)
                r4.setText(r5)
            L_0x0135:
                org.telegram.ui.Components.Premium.LimitPreviewView r4 = r1.limitPreviewView
                android.widget.TextView r4 = r4.defaultCount
                r4.setVisibility(r6)
            L_0x013c:
                int r4 = r1.type
                r5 = 2
                if (r4 == r5) goto L_0x0144
                r5 = 5
                if (r4 != r5) goto L_0x0149
            L_0x0144:
                org.telegram.ui.Components.Premium.LimitPreviewView r4 = r1.limitPreviewView
                r4.setDelayedAnimation()
            L_0x0149:
                org.telegram.ui.Components.Premium.LimitPreviewView r4 = r1.limitPreviewView
                r8 = -1
                r9 = -2
                r10 = 0
                r11 = 0
                r12 = 0
                r13 = 0
                r14 = 0
                r15 = 0
                android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear(r8, r9, r10, r11, r12, r13, r14, r15)
                r0.addView(r4, r5)
                android.widget.TextView r4 = new android.widget.TextView
                r4.<init>(r2)
                java.lang.String r5 = "fonts/rmedium.ttf"
                android.graphics.Typeface r5 = org.telegram.messenger.AndroidUtilities.getTypeface(r5)
                r4.setTypeface(r5)
                int r1 = r1.type
                if (r1 != r3) goto L_0x0179
                r1 = 2131625804(0x7f0e074c, float:1.8878826E38)
                java.lang.String r3 = "FileTooLarge"
                java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
                r4.setText(r1)
                goto L_0x0185
            L_0x0179:
                r1 = 2131626399(0x7f0e099f, float:1.8880033E38)
                java.lang.String r3 = "LimitReached"
                java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
                r4.setText(r1)
            L_0x0185:
                r1 = 1101004800(0x41a00000, float:20.0)
                r3 = 1
                r4.setTextSize(r3, r1)
                java.lang.String r1 = "windowBackgroundWhiteBlackText"
                int r3 = org.telegram.ui.ActionBar.Theme.getColor(r1)
                r4.setTextColor(r3)
                r8 = -2
                r9 = -2
                r10 = 1
                r11 = 0
                r12 = 22
                r13 = 0
                r14 = 10
                android.widget.LinearLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r8, (int) r9, (int) r10, (int) r11, (int) r12, (int) r13, (int) r14)
                r0.addView(r4, r3)
                android.widget.TextView r3 = new android.widget.TextView
                r3.<init>(r2)
                android.text.SpannableStringBuilder r2 = org.telegram.messenger.AndroidUtilities.replaceTags(r7)
                r3.setText(r2)
                r2 = 1096810496(0x41600000, float:14.0)
                r4 = 1
                r3.setTextSize(r4, r2)
                r3.setGravity(r4)
                int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
                r3.setTextColor(r1)
                r4 = -2
                r5 = -2
                r6 = 0
                r7 = 24
                r8 = 0
                r9 = 24
                r10 = 24
                android.widget.LinearLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r4, (int) r5, (int) r6, (int) r7, (int) r8, (int) r9, (int) r10)
                r0.addView(r3, r1)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.Premium.LimitReachedBottomSheet.HeaderView.<init>(org.telegram.ui.Components.Premium.LimitReachedBottomSheet, android.content.Context):void");
        }
    }

    /* access modifiers changed from: private */
    public static LimitParams getLimitParams(int i, int i2) {
        LimitParams limitParams2 = new LimitParams();
        if (i == 0) {
            limitParams2.defaultLimit = MessagesController.getInstance(i2).dialogFiltersPinnedLimitDefault;
            limitParams2.premiumLimit = MessagesController.getInstance(i2).dialogFiltersPinnedLimitPremium;
            limitParams2.icon = NUM;
            limitParams2.descriptionStr = LocaleController.formatString("LimitReachedPinDialogs", NUM, Integer.valueOf(limitParams2.defaultLimit), Integer.valueOf(limitParams2.premiumLimit));
            limitParams2.descriptionStrPremium = LocaleController.formatString("LimitReachedPinDialogsPremium", NUM, Integer.valueOf(limitParams2.premiumLimit));
            limitParams2.descriptionStrLocked = LocaleController.formatString("LimitReachedPinDialogsLocked", NUM, Integer.valueOf(limitParams2.defaultLimit));
        } else if (i == 2) {
            limitParams2.defaultLimit = MessagesController.getInstance(i2).publicLinksLimitDefault;
            limitParams2.premiumLimit = MessagesController.getInstance(i2).publicLinksLimitPremium;
            limitParams2.icon = NUM;
            limitParams2.descriptionStr = LocaleController.formatString("LimitReachedPublicLinks", NUM, Integer.valueOf(limitParams2.defaultLimit), Integer.valueOf(limitParams2.premiumLimit));
            limitParams2.descriptionStrPremium = LocaleController.formatString("LimitReachedPublicLinksPremium", NUM, Integer.valueOf(limitParams2.premiumLimit));
            limitParams2.descriptionStrLocked = LocaleController.formatString("LimitReachedPublicLinksLocked", NUM, Integer.valueOf(limitParams2.defaultLimit));
        } else if (i == 3) {
            limitParams2.defaultLimit = MessagesController.getInstance(i2).dialogFiltersLimitDefault;
            limitParams2.premiumLimit = MessagesController.getInstance(i2).dialogFiltersLimitPremium;
            limitParams2.icon = NUM;
            limitParams2.descriptionStr = LocaleController.formatString("LimitReachedFolders", NUM, Integer.valueOf(limitParams2.defaultLimit), Integer.valueOf(limitParams2.premiumLimit));
            limitParams2.descriptionStrPremium = LocaleController.formatString("LimitReachedFoldersPremium", NUM, Integer.valueOf(limitParams2.premiumLimit));
            limitParams2.descriptionStrLocked = LocaleController.formatString("LimitReachedFoldersLocked", NUM, Integer.valueOf(limitParams2.defaultLimit));
        } else if (i == 4) {
            limitParams2.defaultLimit = MessagesController.getInstance(i2).dialogFiltersChatsLimitDefault;
            limitParams2.premiumLimit = MessagesController.getInstance(i2).dialogFiltersChatsLimitPremium;
            limitParams2.icon = NUM;
            limitParams2.descriptionStr = LocaleController.formatString("LimitReachedChatInFolders", NUM, Integer.valueOf(limitParams2.defaultLimit), Integer.valueOf(limitParams2.premiumLimit));
            limitParams2.descriptionStrPremium = LocaleController.formatString("LimitReachedChatInFoldersPremium", NUM, Integer.valueOf(limitParams2.premiumLimit));
            limitParams2.descriptionStrLocked = LocaleController.formatString("LimitReachedChatInFoldersLocked", NUM, Integer.valueOf(limitParams2.defaultLimit));
        } else if (i == 5) {
            limitParams2.defaultLimit = MessagesController.getInstance(i2).channelsLimitDefault;
            limitParams2.premiumLimit = MessagesController.getInstance(i2).channelsLimitPremium;
            limitParams2.icon = NUM;
            limitParams2.descriptionStr = LocaleController.formatString("LimitReachedCommunities", NUM, Integer.valueOf(limitParams2.defaultLimit), Integer.valueOf(limitParams2.premiumLimit));
            limitParams2.descriptionStrPremium = LocaleController.formatString("LimitReachedCommunitiesPremium", NUM, Integer.valueOf(limitParams2.premiumLimit));
            limitParams2.descriptionStrLocked = LocaleController.formatString("LimitReachedCommunitiesLocked", NUM, Integer.valueOf(limitParams2.defaultLimit));
        } else if (i == 6) {
            limitParams2.defaultLimit = 100;
            limitParams2.premiumLimit = 200;
            limitParams2.icon = NUM;
            limitParams2.descriptionStr = LocaleController.formatString("LimitReachedFileSize", NUM, "2 GB", "4 GB");
            limitParams2.descriptionStrPremium = LocaleController.formatString("LimitReachedFileSizePremium", NUM, "4 GB");
            limitParams2.descriptionStrLocked = LocaleController.formatString("LimitReachedFileSizeLocked", NUM, "2 GB");
        } else if (i == 7) {
            limitParams2.defaultLimit = 3;
            limitParams2.premiumLimit = 4;
            limitParams2.icon = NUM;
            limitParams2.descriptionStr = LocaleController.formatString("LimitReachedAccounts", NUM, 3, Integer.valueOf(limitParams2.premiumLimit));
            limitParams2.descriptionStrPremium = LocaleController.formatString("LimitReachedAccountsPremium", NUM, Integer.valueOf(limitParams2.premiumLimit));
            limitParams2.descriptionStrLocked = LocaleController.formatString("LimitReachedAccountsPremium", NUM, Integer.valueOf(limitParams2.defaultLimit));
        }
        return limitParams2;
    }

    private void loadAdminedChannels() {
        this.loading = true;
        updateRows();
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC$TL_channels_getAdminedPublicChannels(), new LimitReachedBottomSheet$$ExternalSyntheticLambda6(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadAdminedChannels$6(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new LimitReachedBottomSheet$$ExternalSyntheticLambda5(this, tLObject));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadAdminedChannels$5(TLObject tLObject) {
        if (tLObject != null) {
            this.chats.clear();
            this.chats.addAll(((TLRPC$TL_messages_chats) tLObject).chats);
            int i = 0;
            this.loading = false;
            this.enterAnimator.showItemsAnimated(this.chatsTitleRow + 4);
            int i2 = 0;
            while (true) {
                if (i2 >= this.recyclerListView.getChildCount()) {
                    break;
                } else if (this.recyclerListView.getChildAt(i2) instanceof HeaderView) {
                    i = this.recyclerListView.getChildAt(i2).getTop();
                    break;
                } else {
                    i2++;
                }
            }
            updateRows();
            if (this.headerRow >= 0 && i != 0) {
                ((LinearLayoutManager) this.recyclerListView.getLayoutManager()).scrollToPositionWithOffset(this.headerRow + 1, i);
            }
        }
        int max = Math.max(this.chats.size(), this.limitParams.defaultLimit);
        this.limitPreviewView.setIconValue(max);
        this.limitPreviewView.setBagePosition(((float) max) / ((float) this.limitParams.premiumLimit));
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
        revokeLinks(new ArrayList(this.selectedChats));
    }

    /* access modifiers changed from: private */
    public void revokeLinks(ArrayList<TLRPC$Chat> arrayList) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(LocaleController.formatPluralString("RevokeLinks", arrayList.size(), new Object[0]));
        if (arrayList.size() == 1) {
            TLRPC$Chat tLRPC$Chat = arrayList.get(0);
            if (this.parentIsChannel) {
                builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("RevokeLinkAlertChannel", NUM, MessagesController.getInstance(this.currentAccount).linkPrefix + "/" + tLRPC$Chat.username, tLRPC$Chat.title)));
            } else {
                builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("RevokeLinkAlert", NUM, MessagesController.getInstance(this.currentAccount).linkPrefix + "/" + tLRPC$Chat.username, tLRPC$Chat.title)));
            }
        } else if (this.parentIsChannel) {
            builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("RevokeLinksAlertChannel", NUM, new Object[0])));
        } else {
            builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("RevokeLinksAlert", NUM, new Object[0])));
        }
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        builder.setPositiveButton(LocaleController.getString("RevokeButton", NUM), new LimitReachedBottomSheet$$ExternalSyntheticLambda0(this, arrayList));
        AlertDialog create = builder.create();
        create.show();
        TextView textView = (TextView) create.getButton(-1);
        if (textView != null) {
            textView.setTextColor(Theme.getColor("dialogTextRed2"));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$revokeLinks$8(ArrayList arrayList, DialogInterface dialogInterface, int i) {
        dismiss();
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            TLRPC$TL_channels_updateUsername tLRPC$TL_channels_updateUsername = new TLRPC$TL_channels_updateUsername();
            tLRPC$TL_channels_updateUsername.channel = MessagesController.getInputChannel((TLRPC$Chat) arrayList.get(i2));
            tLRPC$TL_channels_updateUsername.username = "";
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_channels_updateUsername, new LimitReachedBottomSheet$$ExternalSyntheticLambda7(this), 64);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$revokeLinks$7(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject instanceof TLRPC$TL_boolTrue) {
            AndroidUtilities.runOnUIThread(this.onSuccessRunnable);
        }
    }

    private void loadInactiveChannels() {
        this.loading = true;
        updateRows();
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC$TL_channels_getInactiveChannels(), new LimitReachedBottomSheet$$ExternalSyntheticLambda8(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadInactiveChannels$10(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        String str;
        if (tLRPC$TL_error == null) {
            TLRPC$TL_messages_inactiveChats tLRPC$TL_messages_inactiveChats = (TLRPC$TL_messages_inactiveChats) tLObject;
            ArrayList arrayList = new ArrayList();
            for (int i = 0; i < tLRPC$TL_messages_inactiveChats.chats.size(); i++) {
                TLRPC$Chat tLRPC$Chat = tLRPC$TL_messages_inactiveChats.chats.get(i);
                int currentTime = (ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() - tLRPC$TL_messages_inactiveChats.dates.get(i).intValue()) / 86400;
                if (currentTime < 30) {
                    str = LocaleController.formatPluralString("Days", currentTime, new Object[0]);
                } else if (currentTime < 365) {
                    str = LocaleController.formatPluralString("Months", currentTime / 30, new Object[0]);
                } else {
                    str = LocaleController.formatPluralString("Years", currentTime / 365, new Object[0]);
                }
                if (ChatObject.isMegagroup(tLRPC$Chat)) {
                    arrayList.add(LocaleController.formatString("InactiveChatSignature", NUM, LocaleController.formatPluralString("Members", tLRPC$Chat.participants_count, new Object[0]), str));
                } else if (ChatObject.isChannel(tLRPC$Chat)) {
                    arrayList.add(LocaleController.formatString("InactiveChannelSignature", NUM, str));
                } else {
                    arrayList.add(LocaleController.formatString("InactiveChatSignature", NUM, LocaleController.formatPluralString("Members", tLRPC$Chat.participants_count, new Object[0]), str));
                }
            }
            AndroidUtilities.runOnUIThread(new LimitReachedBottomSheet$$ExternalSyntheticLambda4(this, arrayList, tLRPC$TL_messages_inactiveChats));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadInactiveChannels$9(ArrayList arrayList, TLRPC$TL_messages_inactiveChats tLRPC$TL_messages_inactiveChats) {
        this.inactiveChatsSignatures.clear();
        this.inactiveChats.clear();
        this.inactiveChatsSignatures.addAll(arrayList);
        this.inactiveChats.addAll(tLRPC$TL_messages_inactiveChats.chats);
        int i = 0;
        this.loading = false;
        this.enterAnimator.showItemsAnimated(this.chatsTitleRow + 4);
        int i2 = 0;
        while (true) {
            if (i2 >= this.recyclerListView.getChildCount()) {
                break;
            } else if (this.recyclerListView.getChildAt(i2) instanceof HeaderView) {
                i = this.recyclerListView.getChildAt(i2).getTop();
                break;
            } else {
                i2++;
            }
        }
        updateRows();
        if (this.headerRow >= 0 && i != 0) {
            ((LinearLayoutManager) this.recyclerListView.getLayoutManager()).scrollToPositionWithOffset(this.headerRow + 1, i);
        }
        int max = Math.max(this.inactiveChats.size(), this.limitParams.defaultLimit);
        this.limitPreviewView.setIconValue(max);
        this.limitPreviewView.setBagePosition(((float) max) / ((float) this.limitParams.premiumLimit));
        this.limitPreviewView.startDelayedAnimation();
    }
}
