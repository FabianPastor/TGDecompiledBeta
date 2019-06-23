package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.net.Uri;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.LayoutParams;
import androidx.recyclerview.widget.RecyclerView.OnScrollListener;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import java.util.ArrayList;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.ChatFull;
import org.telegram.tgnet.TLRPC.StickerSet;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputStickerSetShortName;
import org.telegram.tgnet.TLRPC.TL_messages_getStickerSet;
import org.telegram.tgnet.TLRPC.TL_messages_stickerSet;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.StickerSetCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.ContextProgressView;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.Components.StickersAlert;
import org.telegram.ui.Components.URLSpanNoUnderline;

public class GroupStickersActivity extends BaseFragment implements NotificationCenterDelegate {
    private static final int done_button = 1;
    private int chatId;
    private ActionBarMenuItem doneItem;
    private AnimatorSet doneItemAnimation;
    private boolean donePressed;
    private EditText editText;
    private ImageView eraseImageView;
    private int headerRow;
    private boolean ignoreTextChanges;
    private ChatFull info;
    private int infoRow;
    private LinearLayoutManager layoutManager;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private LinearLayout nameContainer;
    private int nameRow;
    private ContextProgressView progressView;
    private Runnable queryRunnable;
    private int reqId;
    private int rowCount;
    private boolean searchWas;
    private boolean searching;
    private int selectedStickerRow;
    private TL_messages_stickerSet selectedStickerSet;
    private int stickersEndRow;
    private int stickersShadowRow;
    private int stickersStartRow;
    private EditTextBoldCursor usernameTextView;

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return GroupStickersActivity.this.rowCount;
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            int itemViewType = viewHolder.getItemViewType();
            boolean z = true;
            StickerSetCell stickerSetCell;
            if (itemViewType == 0) {
                ArrayList stickerSets = MediaDataController.getInstance(GroupStickersActivity.this.currentAccount).getStickerSets(0);
                i -= GroupStickersActivity.this.stickersStartRow;
                stickerSetCell = (StickerSetCell) viewHolder.itemView;
                TL_messages_stickerSet tL_messages_stickerSet = (TL_messages_stickerSet) stickerSets.get(i);
                stickerSetCell.setStickersSet((TL_messages_stickerSet) stickerSets.get(i), i != stickerSets.size() - 1);
                long j = GroupStickersActivity.this.selectedStickerSet != null ? GroupStickersActivity.this.selectedStickerSet.set.id : (GroupStickersActivity.this.info == null || GroupStickersActivity.this.info.stickerset == null) ? 0 : GroupStickersActivity.this.info.stickerset.id;
                if (tL_messages_stickerSet.set.id != j) {
                    z = false;
                }
                stickerSetCell.setChecked(z);
            } else if (itemViewType != 1) {
                if (itemViewType == 4) {
                    ((HeaderCell) viewHolder.itemView).setText(LocaleController.getString("ChooseFromYourStickers", NUM));
                } else if (itemViewType == 5) {
                    stickerSetCell = (StickerSetCell) viewHolder.itemView;
                    if (GroupStickersActivity.this.selectedStickerSet != null) {
                        stickerSetCell.setStickersSet(GroupStickersActivity.this.selectedStickerSet, false);
                    } else if (GroupStickersActivity.this.searching) {
                        stickerSetCell.setText(LocaleController.getString("Loading", NUM), null, 0, false);
                    } else {
                        stickerSetCell.setText(LocaleController.getString("ChooseStickerSetNotFound", NUM), LocaleController.getString("ChooseStickerSetNotFoundInfo", NUM), NUM, false);
                    }
                }
            } else if (i == GroupStickersActivity.this.infoRow) {
                String string = LocaleController.getString("ChooseStickerSetMy", NUM);
                String str = "@stickers";
                int indexOf = string.indexOf(str);
                if (indexOf != -1) {
                    try {
                        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(string);
                        spannableStringBuilder.setSpan(new URLSpanNoUnderline(str) {
                            public void onClick(View view) {
                                MessagesController.getInstance(GroupStickersActivity.this.currentAccount).openByUserName("stickers", GroupStickersActivity.this, 1);
                            }
                        }, indexOf, indexOf + 9, 18);
                        ((TextInfoPrivacyCell) viewHolder.itemView).setText(spannableStringBuilder);
                        return;
                    } catch (Exception e) {
                        FileLog.e(e);
                        ((TextInfoPrivacyCell) viewHolder.itemView).setText(string);
                        return;
                    }
                }
                ((TextInfoPrivacyCell) viewHolder.itemView).setText(string);
            }
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            return itemViewType == 0 || itemViewType == 2 || itemViewType == 5;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View textInfoPrivacyCell;
            String str = "windowBackgroundWhite";
            int i2 = 3;
            if (i != 0) {
                String str2 = "windowBackgroundGrayShadow";
                if (i == 1) {
                    textInfoPrivacyCell = new TextInfoPrivacyCell(this.mContext);
                    textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, str2));
                } else if (i == 2) {
                    textInfoPrivacyCell = GroupStickersActivity.this.nameContainer;
                } else if (i == 3) {
                    textInfoPrivacyCell = new ShadowSectionCell(this.mContext);
                    textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, str2));
                } else if (i == 4) {
                    View headerCell = new HeaderCell(this.mContext);
                    headerCell.setBackgroundColor(Theme.getColor(str));
                    textInfoPrivacyCell = headerCell;
                } else if (i != 5) {
                    textInfoPrivacyCell = null;
                }
                textInfoPrivacyCell.setLayoutParams(new LayoutParams(-1, -2));
                return new Holder(textInfoPrivacyCell);
            }
            Context context = this.mContext;
            if (i != 0) {
                i2 = 2;
            }
            View stickerSetCell = new StickerSetCell(context, i2);
            stickerSetCell.setBackgroundColor(Theme.getColor(str));
            textInfoPrivacyCell = stickerSetCell;
            textInfoPrivacyCell.setLayoutParams(new LayoutParams(-1, -2));
            return new Holder(textInfoPrivacyCell);
        }

        public int getItemViewType(int i) {
            if (i >= GroupStickersActivity.this.stickersStartRow && i < GroupStickersActivity.this.stickersEndRow) {
                return 0;
            }
            if (i == GroupStickersActivity.this.infoRow) {
                return 1;
            }
            if (i == GroupStickersActivity.this.nameRow) {
                return 2;
            }
            if (i == GroupStickersActivity.this.stickersShadowRow) {
                return 3;
            }
            if (i == GroupStickersActivity.this.headerRow) {
                return 4;
            }
            if (i == GroupStickersActivity.this.selectedStickerRow) {
                return 5;
            }
            return 0;
        }
    }

    public GroupStickersActivity(int i) {
        this.chatId = i;
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        MediaDataController.getInstance(this.currentAccount).checkStickers(0);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.stickersDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatInfoDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.groupStickersDidLoad);
        updateRows();
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.stickersDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatInfoDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.groupStickersDidLoad);
    }

    public View createView(Context context) {
        Context context2 = context;
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("GroupStickers", NUM));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    GroupStickersActivity.this.finishFragment();
                } else if (i == 1 && !GroupStickersActivity.this.donePressed) {
                    GroupStickersActivity.this.donePressed = true;
                    if (GroupStickersActivity.this.searching) {
                        GroupStickersActivity.this.showEditDoneProgress(true);
                        return;
                    }
                    GroupStickersActivity.this.saveStickerSet();
                }
            }
        });
        this.doneItem = this.actionBar.createMenu().addItemWithWidth(1, NUM, AndroidUtilities.dp(56.0f));
        this.progressView = new ContextProgressView(context2, 1);
        this.progressView.setAlpha(0.0f);
        this.progressView.setScaleX(0.1f);
        this.progressView.setScaleY(0.1f);
        this.progressView.setVisibility(4);
        this.doneItem.addView(this.progressView, LayoutHelper.createFrame(-1, -1.0f));
        this.nameContainer = new LinearLayout(context2) {
            /* Access modifiers changed, original: protected */
            public void onMeasure(int i, int i2) {
                super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(42.0f), NUM));
            }

            /* Access modifiers changed, original: protected */
            public void onDraw(Canvas canvas) {
                if (GroupStickersActivity.this.selectedStickerSet != null) {
                    canvas.drawLine(0.0f, (float) (getHeight() - 1), (float) getWidth(), (float) (getHeight() - 1), Theme.dividerPaint);
                }
            }
        };
        this.nameContainer.setWeightSum(1.0f);
        this.nameContainer.setWillNotDraw(false);
        this.nameContainer.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        this.nameContainer.setOrientation(0);
        this.nameContainer.setPadding(AndroidUtilities.dp(17.0f), 0, AndroidUtilities.dp(14.0f), 0);
        this.editText = new EditText(context2);
        EditText editText = this.editText;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(MessagesController.getInstance(this.currentAccount).linkPrefix);
        stringBuilder.append("/addstickers/");
        editText.setText(stringBuilder.toString());
        this.editText.setTextSize(1, 17.0f);
        String str = "windowBackgroundWhiteHintText";
        this.editText.setHintTextColor(Theme.getColor(str));
        String str2 = "windowBackgroundWhiteBlackText";
        this.editText.setTextColor(Theme.getColor(str2));
        this.editText.setMaxLines(1);
        this.editText.setLines(1);
        this.editText.setEnabled(false);
        this.editText.setFocusable(false);
        this.editText.setBackgroundDrawable(null);
        this.editText.setPadding(0, 0, 0, 0);
        this.editText.setGravity(16);
        this.editText.setSingleLine(true);
        this.editText.setInputType(163840);
        this.editText.setImeOptions(6);
        this.nameContainer.addView(this.editText, LayoutHelper.createLinear(-2, 42));
        this.usernameTextView = new EditTextBoldCursor(context2);
        this.usernameTextView.setTextSize(1, 17.0f);
        this.usernameTextView.setCursorColor(Theme.getColor(str2));
        this.usernameTextView.setCursorSize(AndroidUtilities.dp(20.0f));
        this.usernameTextView.setCursorWidth(1.5f);
        this.usernameTextView.setHintTextColor(Theme.getColor(str));
        this.usernameTextView.setTextColor(Theme.getColor(str2));
        this.usernameTextView.setMaxLines(1);
        this.usernameTextView.setLines(1);
        this.usernameTextView.setBackgroundDrawable(null);
        this.usernameTextView.setPadding(0, 0, 0, 0);
        this.usernameTextView.setSingleLine(true);
        this.usernameTextView.setGravity(16);
        this.usernameTextView.setInputType(163872);
        this.usernameTextView.setImeOptions(6);
        this.usernameTextView.setHint(LocaleController.getString("ChooseStickerSetPlaceholder", NUM));
        this.usernameTextView.addTextChangedListener(new TextWatcher() {
            boolean ignoreTextChange;

            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void afterTextChanged(Editable editable) {
                if (GroupStickersActivity.this.eraseImageView != null) {
                    GroupStickersActivity.this.eraseImageView.setVisibility(editable.length() > 0 ? 0 : 4);
                }
                if (!this.ignoreTextChange && !GroupStickersActivity.this.ignoreTextChanges) {
                    if (editable.length() > 5) {
                        this.ignoreTextChange = true;
                        try {
                            Uri parse = Uri.parse(editable.toString());
                            if (parse != null) {
                                List pathSegments = parse.getPathSegments();
                                if (pathSegments.size() == 2 && ((String) pathSegments.get(0)).toLowerCase().equals("addstickers")) {
                                    GroupStickersActivity.this.usernameTextView.setText((CharSequence) pathSegments.get(1));
                                    GroupStickersActivity.this.usernameTextView.setSelection(GroupStickersActivity.this.usernameTextView.length());
                                }
                            }
                        } catch (Exception unused) {
                        }
                        this.ignoreTextChange = false;
                    }
                    GroupStickersActivity.this.resolveStickerSet();
                }
            }
        });
        this.nameContainer.addView(this.usernameTextView, LayoutHelper.createLinear(0, 42, 1.0f));
        this.eraseImageView = new ImageView(context2);
        this.eraseImageView.setScaleType(ScaleType.CENTER);
        this.eraseImageView.setImageResource(NUM);
        this.eraseImageView.setPadding(AndroidUtilities.dp(16.0f), 0, 0, 0);
        this.eraseImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteGrayText3"), Mode.MULTIPLY));
        this.eraseImageView.setVisibility(4);
        this.eraseImageView.setOnClickListener(new -$$Lambda$GroupStickersActivity$0gmuCbw_2WYqittTzpHTcYQ1s5I(this));
        this.nameContainer.addView(this.eraseImageView, LayoutHelper.createLinear(42, 42, 0.0f));
        ChatFull chatFull = this.info;
        if (chatFull != null) {
            StickerSet stickerSet = chatFull.stickerset;
            if (stickerSet != null) {
                this.ignoreTextChanges = true;
                this.usernameTextView.setText(stickerSet.short_name);
                EditTextBoldCursor editTextBoldCursor = this.usernameTextView;
                editTextBoldCursor.setSelection(editTextBoldCursor.length());
                this.ignoreTextChanges = false;
            }
        }
        this.listAdapter = new ListAdapter(context2);
        this.fragmentView = new FrameLayout(context2);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        this.listView = new RecyclerListView(context2);
        this.listView.setFocusable(true);
        this.listView.setItemAnimator(null);
        this.listView.setLayoutAnimation(null);
        this.layoutManager = new LinearLayoutManager(context2) {
            public boolean requestChildRectangleOnScreen(RecyclerView recyclerView, View view, Rect rect, boolean z, boolean z2) {
                return false;
            }

            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        this.layoutManager.setOrientation(1);
        this.listView.setLayoutManager(this.layoutManager);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener(new -$$Lambda$GroupStickersActivity$cr2TDaX3YsSRoO-CaDIAKHsP5zc(this));
        this.listView.setOnScrollListener(new OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
            }

            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                if (i == 1) {
                    AndroidUtilities.hideKeyboard(GroupStickersActivity.this.getParentActivity().getCurrentFocus());
                }
            }
        });
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$0$GroupStickersActivity(View view) {
        this.searchWas = false;
        this.selectedStickerSet = null;
        this.usernameTextView.setText("");
        updateRows();
    }

    public /* synthetic */ void lambda$createView$1$GroupStickersActivity(View view, int i) {
        if (getParentActivity() != null) {
            int i2 = this.selectedStickerRow;
            if (i == i2) {
                if (this.selectedStickerSet != null) {
                    showDialog(new StickersAlert(getParentActivity(), this, null, this.selectedStickerSet, null));
                }
            } else if (i >= this.stickersStartRow && i < this.stickersEndRow) {
                Object obj = i2 == -1 ? 1 : null;
                int findFirstVisibleItemPosition = this.layoutManager.findFirstVisibleItemPosition();
                Holder holder = (Holder) this.listView.findViewHolderForAdapterPosition(findFirstVisibleItemPosition);
                int top = holder != null ? holder.itemView.getTop() : Integer.MAX_VALUE;
                this.selectedStickerSet = (TL_messages_stickerSet) MediaDataController.getInstance(this.currentAccount).getStickerSets(0).get(i - this.stickersStartRow);
                this.ignoreTextChanges = true;
                this.usernameTextView.setText(this.selectedStickerSet.set.short_name);
                EditTextBoldCursor editTextBoldCursor = this.usernameTextView;
                editTextBoldCursor.setSelection(editTextBoldCursor.length());
                this.ignoreTextChanges = false;
                AndroidUtilities.hideKeyboard(this.usernameTextView);
                updateRows();
                if (!(obj == null || top == Integer.MAX_VALUE)) {
                    this.layoutManager.scrollToPositionWithOffset(findFirstVisibleItemPosition + 1, top);
                }
            }
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.stickersDidLoad) {
            if (((Integer) objArr[0]).intValue() == 0) {
                updateRows();
            }
        } else if (i == NotificationCenter.chatInfoDidLoad) {
            ChatFull chatFull = (ChatFull) objArr[0];
            if (chatFull.id == this.chatId) {
                if (this.info == null && chatFull.stickerset != null) {
                    this.selectedStickerSet = MediaDataController.getInstance(this.currentAccount).getGroupStickerSetById(chatFull.stickerset);
                }
                this.info = chatFull;
                updateRows();
            }
        } else if (i == NotificationCenter.groupStickersDidLoad) {
            ((Long) objArr[0]).longValue();
            ChatFull chatFull2 = this.info;
            if (chatFull2 != null) {
                StickerSet stickerSet = chatFull2.stickerset;
                if (stickerSet != null && stickerSet.id == ((long) i)) {
                    updateRows();
                }
            }
        }
    }

    public void setInfo(ChatFull chatFull) {
        this.info = chatFull;
        chatFull = this.info;
        if (chatFull != null && chatFull.stickerset != null) {
            this.selectedStickerSet = MediaDataController.getInstance(this.currentAccount).getGroupStickerSetById(this.info.stickerset);
        }
    }

    private void resolveStickerSet() {
        if (this.listAdapter != null) {
            if (this.reqId != 0) {
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.reqId, true);
                this.reqId = 0;
            }
            Runnable runnable = this.queryRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.queryRunnable = null;
            }
            this.selectedStickerSet = null;
            if (this.usernameTextView.length() <= 0) {
                this.searching = false;
                this.searchWas = false;
                if (this.selectedStickerRow != -1) {
                    updateRows();
                }
                return;
            }
            this.searching = true;
            this.searchWas = true;
            String obj = this.usernameTextView.getText().toString();
            TL_messages_stickerSet stickerSetByName = MediaDataController.getInstance(this.currentAccount).getStickerSetByName(obj);
            if (stickerSetByName != null) {
                this.selectedStickerSet = stickerSetByName;
            }
            int i = this.selectedStickerRow;
            if (i == -1) {
                updateRows();
            } else {
                this.listAdapter.notifyItemChanged(i);
            }
            if (stickerSetByName != null) {
                this.searching = false;
                return;
            }
            -$$Lambda$GroupStickersActivity$4Rqel5HptCXt4RyUWgtwIaVXIfk -__lambda_groupstickersactivity_4rqel5hptcxt4ryuwgtwiavxifk = new -$$Lambda$GroupStickersActivity$4Rqel5HptCXt4RyUWgtwIaVXIfk(this, obj);
            this.queryRunnable = -__lambda_groupstickersactivity_4rqel5hptcxt4ryuwgtwiavxifk;
            AndroidUtilities.runOnUIThread(-__lambda_groupstickersactivity_4rqel5hptcxt4ryuwgtwiavxifk, 500);
        }
    }

    public /* synthetic */ void lambda$resolveStickerSet$4$GroupStickersActivity(String str) {
        if (this.queryRunnable != null) {
            TL_messages_getStickerSet tL_messages_getStickerSet = new TL_messages_getStickerSet();
            tL_messages_getStickerSet.stickerset = new TL_inputStickerSetShortName();
            tL_messages_getStickerSet.stickerset.short_name = str;
            this.reqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_getStickerSet, new -$$Lambda$GroupStickersActivity$oURnGCXrOgZUJRKYNWwDhi5jZEA(this));
        }
    }

    public /* synthetic */ void lambda$null$3$GroupStickersActivity(TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$GroupStickersActivity$k9BjjbmWE3K4Pvar_XiggMGiLuI(this, tLObject));
    }

    public /* synthetic */ void lambda$null$2$GroupStickersActivity(TLObject tLObject) {
        this.searching = false;
        int i;
        if (tLObject instanceof TL_messages_stickerSet) {
            this.selectedStickerSet = (TL_messages_stickerSet) tLObject;
            if (this.donePressed) {
                saveStickerSet();
            } else {
                i = this.selectedStickerRow;
                if (i != -1) {
                    this.listAdapter.notifyItemChanged(i);
                } else {
                    updateRows();
                }
            }
        } else {
            i = this.selectedStickerRow;
            if (i != -1) {
                this.listAdapter.notifyItemChanged(i);
            }
            if (this.donePressed) {
                this.donePressed = false;
                showEditDoneProgress(false);
                if (getParentActivity() != null) {
                    Toast.makeText(getParentActivity(), LocaleController.getString("AddStickersNotFound", NUM), 0).show();
                }
            }
        }
        this.reqId = 0;
    }

    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$GroupStickersActivity$r9HzrnDTDEDApQN3gt-U8hwAtPA(this), 100);
        }
    }

    public /* synthetic */ void lambda$onTransitionAnimationEnd$5$GroupStickersActivity() {
        EditTextBoldCursor editTextBoldCursor = this.usernameTextView;
        if (editTextBoldCursor != null) {
            editTextBoldCursor.requestFocus();
            AndroidUtilities.showKeyboard(this.usernameTextView);
        }
    }

    /* JADX WARNING: Missing block: B:7:0x0014, code skipped:
            if (r1.set.id == r0.id) goto L_0x008e;
     */
    private void saveStickerSet() {
        /*
        r5 = this;
        r0 = r5.info;
        if (r0 == 0) goto L_0x008e;
    L_0x0004:
        r0 = r0.stickerset;
        if (r0 == 0) goto L_0x0016;
    L_0x0008:
        r1 = r5.selectedStickerSet;
        if (r1 == 0) goto L_0x0016;
    L_0x000c:
        r1 = r1.set;
        r1 = r1.id;
        r3 = r0.id;
        r0 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1));
        if (r0 == 0) goto L_0x008e;
    L_0x0016:
        r0 = r5.info;
        r0 = r0.stickerset;
        if (r0 != 0) goto L_0x0021;
    L_0x001c:
        r0 = r5.selectedStickerSet;
        if (r0 != 0) goto L_0x0021;
    L_0x0020:
        goto L_0x008e;
    L_0x0021:
        r0 = 1;
        r5.showEditDoneProgress(r0);
        r0 = new org.telegram.tgnet.TLRPC$TL_channels_setStickers;
        r0.<init>();
        r1 = r5.currentAccount;
        r1 = org.telegram.messenger.MessagesController.getInstance(r1);
        r2 = r5.chatId;
        r1 = r1.getInputChannel(r2);
        r0.channel = r1;
        r1 = r5.selectedStickerSet;
        if (r1 != 0) goto L_0x0044;
    L_0x003c:
        r1 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty;
        r1.<init>();
        r0.stickerset = r1;
        goto L_0x007f;
    L_0x0044:
        r1 = r5.currentAccount;
        r1 = org.telegram.messenger.MessagesController.getEmojiSettings(r1);
        r1 = r1.edit();
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = "group_hide_stickers_";
        r2.append(r3);
        r3 = r5.info;
        r3 = r3.id;
        r2.append(r3);
        r2 = r2.toString();
        r1 = r1.remove(r2);
        r1.commit();
        r1 = new org.telegram.tgnet.TLRPC$TL_inputStickerSetID;
        r1.<init>();
        r0.stickerset = r1;
        r1 = r0.stickerset;
        r2 = r5.selectedStickerSet;
        r2 = r2.set;
        r3 = r2.id;
        r1.id = r3;
        r2 = r2.access_hash;
        r1.access_hash = r2;
    L_0x007f:
        r1 = r5.currentAccount;
        r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r1);
        r2 = new org.telegram.ui.-$$Lambda$GroupStickersActivity$SODwEnuUWhbL0-5dEEd38j4JKu8;
        r2.<init>(r5);
        r1.sendRequest(r0, r2);
        return;
    L_0x008e:
        r5.finishFragment();
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.GroupStickersActivity.saveStickerSet():void");
    }

    public /* synthetic */ void lambda$saveStickerSet$7$GroupStickersActivity(TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$GroupStickersActivity$Z_OfWFCln7TEpsXJNMwvYTvczmA(this, tL_error));
    }

    public /* synthetic */ void lambda$null$6$GroupStickersActivity(TL_error tL_error) {
        if (tL_error == null) {
            TL_messages_stickerSet tL_messages_stickerSet = this.selectedStickerSet;
            if (tL_messages_stickerSet == null) {
                this.info.stickerset = null;
            } else {
                this.info.stickerset = tL_messages_stickerSet.set;
                MediaDataController.getInstance(this.currentAccount).putGroupStickerSet(this.selectedStickerSet);
            }
            ChatFull chatFull = this.info;
            if (chatFull.stickerset == null) {
                chatFull.flags |= 256;
            } else {
                chatFull.flags &= -257;
            }
            MessagesStorage.getInstance(this.currentAccount).updateChatInfo(this.info, false);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatInfoDidLoad, this.info, Integer.valueOf(0), Boolean.valueOf(true), null);
            finishFragment();
            return;
        }
        Activity parentActivity = getParentActivity();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(LocaleController.getString("ErrorOccurred", NUM));
        stringBuilder.append("\n");
        stringBuilder.append(tL_error.text);
        Toast.makeText(parentActivity, stringBuilder.toString(), 0).show();
        this.donePressed = false;
        showEditDoneProgress(false);
    }

    private void updateRows() {
        this.rowCount = 0;
        int i = this.rowCount;
        this.rowCount = i + 1;
        this.nameRow = i;
        if (this.selectedStickerSet != null || this.searchWas) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.selectedStickerRow = i;
        } else {
            this.selectedStickerRow = -1;
        }
        i = this.rowCount;
        this.rowCount = i + 1;
        this.infoRow = i;
        ArrayList stickerSets = MediaDataController.getInstance(this.currentAccount).getStickerSets(0);
        if (stickerSets.isEmpty()) {
            this.headerRow = -1;
            this.stickersStartRow = -1;
            this.stickersEndRow = -1;
            this.stickersShadowRow = -1;
        } else {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.headerRow = i;
            i = this.rowCount;
            this.stickersStartRow = i;
            this.stickersEndRow = i + stickerSets.size();
            this.rowCount += stickerSets.size();
            int i2 = this.rowCount;
            this.rowCount = i2 + 1;
            this.stickersShadowRow = i2;
        }
        LinearLayout linearLayout = this.nameContainer;
        if (linearLayout != null) {
            linearLayout.invalidate();
        }
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
        if (!MessagesController.getGlobalMainSettings().getBoolean("view_animations", true)) {
            this.usernameTextView.requestFocus();
            AndroidUtilities.showKeyboard(this.usernameTextView);
        }
    }

    private void showEditDoneProgress(boolean z) {
        final boolean z2 = z;
        if (this.doneItem != null) {
            AnimatorSet animatorSet = this.doneItemAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            this.doneItemAnimation = new AnimatorSet();
            String str = "alpha";
            String str2 = "scaleY";
            String str3 = "scaleX";
            if (z2) {
                this.progressView.setVisibility(0);
                this.doneItem.setEnabled(false);
                AnimatorSet animatorSet2 = this.doneItemAnimation;
                Animator[] animatorArr = new Animator[6];
                animatorArr[0] = ObjectAnimator.ofFloat(this.doneItem.getContentView(), str3, new float[]{0.1f});
                animatorArr[1] = ObjectAnimator.ofFloat(this.doneItem.getContentView(), str2, new float[]{0.1f});
                animatorArr[2] = ObjectAnimator.ofFloat(this.doneItem.getContentView(), str, new float[]{0.0f});
                animatorArr[3] = ObjectAnimator.ofFloat(this.progressView, str3, new float[]{1.0f});
                animatorArr[4] = ObjectAnimator.ofFloat(this.progressView, str2, new float[]{1.0f});
                animatorArr[5] = ObjectAnimator.ofFloat(this.progressView, str, new float[]{1.0f});
                animatorSet2.playTogether(animatorArr);
            } else {
                this.doneItem.getContentView().setVisibility(0);
                this.doneItem.setEnabled(true);
                animatorSet = this.doneItemAnimation;
                Animator[] animatorArr2 = new Animator[6];
                animatorArr2[0] = ObjectAnimator.ofFloat(this.progressView, str3, new float[]{0.1f});
                animatorArr2[1] = ObjectAnimator.ofFloat(this.progressView, str2, new float[]{0.1f});
                animatorArr2[2] = ObjectAnimator.ofFloat(this.progressView, str, new float[]{0.0f});
                animatorArr2[3] = ObjectAnimator.ofFloat(this.doneItem.getContentView(), str3, new float[]{1.0f});
                animatorArr2[4] = ObjectAnimator.ofFloat(this.doneItem.getContentView(), str2, new float[]{1.0f});
                animatorArr2[5] = ObjectAnimator.ofFloat(this.doneItem.getContentView(), str, new float[]{1.0f});
                animatorSet.playTogether(animatorArr2);
            }
            this.doneItemAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (GroupStickersActivity.this.doneItemAnimation != null && GroupStickersActivity.this.doneItemAnimation.equals(animator)) {
                        if (z2) {
                            GroupStickersActivity.this.doneItem.getContentView().setVisibility(4);
                        } else {
                            GroupStickersActivity.this.progressView.setVisibility(4);
                        }
                    }
                }

                public void onAnimationCancel(Animator animator) {
                    if (GroupStickersActivity.this.doneItemAnimation != null && GroupStickersActivity.this.doneItemAnimation.equals(animator)) {
                        GroupStickersActivity.this.doneItemAnimation = null;
                    }
                }
            });
            this.doneItemAnimation.setDuration(150);
            this.doneItemAnimation.start();
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[24];
        themeDescriptionArr[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{StickerSetCell.class, TextSettingsCell.class}, null, null, null, "windowBackgroundWhite");
        themeDescriptionArr[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray");
        themeDescriptionArr[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
        themeDescriptionArr[3] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
        themeDescriptionArr[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
        themeDescriptionArr[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
        themeDescriptionArr[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
        themeDescriptionArr[7] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
        themeDescriptionArr[8] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, "divider");
        themeDescriptionArr[9] = new ThemeDescription(this.editText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[10] = new ThemeDescription(this.editText, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText");
        themeDescriptionArr[11] = new ThemeDescription(this.usernameTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[12] = new ThemeDescription(this.usernameTextView, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText");
        themeDescriptionArr[13] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, "windowBackgroundGrayShadow");
        View view = this.listView;
        Class[] clsArr = new Class[]{TextInfoPrivacyCell.class};
        String[] strArr = new String[1];
        strArr[0] = "textView";
        themeDescriptionArr[14] = new ThemeDescription(view, 0, clsArr, strArr, null, null, null, "windowBackgroundWhiteGrayText4");
        themeDescriptionArr[15] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LINKCOLOR, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteLinkText");
        themeDescriptionArr[16] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        view = this.listView;
        clsArr = new Class[]{TextSettingsCell.class};
        strArr = new String[1];
        strArr[0] = "valueTextView";
        themeDescriptionArr[17] = new ThemeDescription(view, 0, clsArr, strArr, null, null, null, "windowBackgroundWhiteValueText");
        themeDescriptionArr[18] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, "windowBackgroundGrayShadow");
        themeDescriptionArr[19] = new ThemeDescription(this.nameContainer, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite");
        themeDescriptionArr[20] = new ThemeDescription(this.listView, 0, new Class[]{StickerSetCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[21] = new ThemeDescription(this.listView, 0, new Class[]{StickerSetCell.class}, new String[]{"valueTextView"}, null, null, null, "windowBackgroundWhiteGrayText2");
        view = this.listView;
        int i = ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE;
        clsArr = new Class[]{StickerSetCell.class};
        strArr = new String[1];
        strArr[0] = "optionsButton";
        themeDescriptionArr[22] = new ThemeDescription(view, i, clsArr, strArr, null, null, null, "stickers_menuSelector");
        themeDescriptionArr[23] = new ThemeDescription(this.listView, 0, new Class[]{StickerSetCell.class}, new String[]{"optionsButton"}, null, null, null, "stickers_menu");
        return themeDescriptionArr;
    }
}
