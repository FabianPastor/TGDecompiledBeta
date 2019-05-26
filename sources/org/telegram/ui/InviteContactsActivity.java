package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.annotation.Keep;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.OnScrollListener;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.ContactsController.Contact;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.GroupCreateSectionCell;
import org.telegram.ui.Cells.InviteTextCell;
import org.telegram.ui.Cells.InviteUserCell;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.GroupCreateDividerItemDecoration;
import org.telegram.ui.Components.GroupCreateSpan;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public class InviteContactsActivity extends BaseFragment implements NotificationCenterDelegate, OnClickListener {
    private InviteAdapter adapter;
    private ArrayList<GroupCreateSpan> allSpans = new ArrayList();
    private int containerHeight;
    private TextView counterTextView;
    private FrameLayout counterView;
    private GroupCreateSpan currentDeletingSpan;
    private GroupCreateDividerItemDecoration decoration;
    private EditTextBoldCursor editText;
    private EmptyTextProgressView emptyView;
    private int fieldY;
    private boolean ignoreScrollEvent;
    private TextView infoTextView;
    private RecyclerListView listView;
    private ArrayList<Contact> phoneBookContacts;
    private ScrollView scrollView;
    private boolean searchWas;
    private boolean searching;
    private HashMap<String, GroupCreateSpan> selectedContacts = new HashMap();
    private SpansContainer spansContainer;
    private TextView textView;

    private class SpansContainer extends ViewGroup {
        private View addingSpan;
        private boolean animationStarted;
        private ArrayList<Animator> animators = new ArrayList();
        private AnimatorSet currentAnimation;
        private View removingSpan;

        public SpansContainer(Context context) {
            super(context);
        }

        /* Access modifiers changed, original: protected */
        public void onMeasure(int i, int i2) {
            String str;
            String str2;
            int childCount = getChildCount();
            int size = MeasureSpec.getSize(i);
            float f = 32.0f;
            int dp = size - AndroidUtilities.dp(32.0f);
            int dp2 = AndroidUtilities.dp(12.0f);
            int dp3 = AndroidUtilities.dp(12.0f);
            int i3 = 0;
            int i4 = 0;
            int i5 = 0;
            while (true) {
                str = "translationY";
                str2 = "translationX";
                if (i3 >= childCount) {
                    break;
                }
                View childAt = getChildAt(i3);
                if (childAt instanceof GroupCreateSpan) {
                    float f2;
                    float f3;
                    childAt.measure(MeasureSpec.makeMeasureSpec(size, Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(f), NUM));
                    if (childAt == this.removingSpan || childAt.getMeasuredWidth() + i4 <= dp) {
                        f2 = 12.0f;
                    } else {
                        f2 = 12.0f;
                        dp2 += childAt.getMeasuredHeight() + AndroidUtilities.dp(12.0f);
                        i4 = 0;
                    }
                    if (childAt.getMeasuredWidth() + i5 > dp) {
                        dp3 += childAt.getMeasuredHeight() + AndroidUtilities.dp(f2);
                        f3 = 16.0f;
                        i5 = 0;
                    } else {
                        f3 = 16.0f;
                    }
                    int dp4 = AndroidUtilities.dp(f3) + i4;
                    if (!this.animationStarted) {
                        View view = this.removingSpan;
                        if (childAt == view) {
                            childAt.setTranslationX((float) (AndroidUtilities.dp(f3) + i5));
                            childAt.setTranslationY((float) dp3);
                        } else if (view != null) {
                            int i6;
                            f2 = (float) dp4;
                            if (childAt.getTranslationX() != f2) {
                                ArrayList arrayList = this.animators;
                                dp4 = 1;
                                float[] fArr = new float[1];
                                i6 = 0;
                                fArr[0] = f2;
                                arrayList.add(ObjectAnimator.ofFloat(childAt, str2, fArr));
                            } else {
                                dp4 = 1;
                                i6 = 0;
                            }
                            f3 = (float) dp2;
                            if (childAt.getTranslationY() != f3) {
                                ArrayList arrayList2 = this.animators;
                                float[] fArr2 = new float[dp4];
                                fArr2[i6] = f3;
                                arrayList2.add(ObjectAnimator.ofFloat(childAt, str, fArr2));
                            }
                        } else {
                            childAt.setTranslationX((float) dp4);
                            childAt.setTranslationY((float) dp2);
                        }
                    }
                    if (childAt != this.removingSpan) {
                        i4 += childAt.getMeasuredWidth() + AndroidUtilities.dp(9.0f);
                    }
                    i5 += childAt.getMeasuredWidth() + AndroidUtilities.dp(9.0f);
                }
                i3++;
                f = 32.0f;
            }
            if (AndroidUtilities.isTablet()) {
                childCount = AndroidUtilities.dp(366.0f) / 3;
            } else {
                Point point = AndroidUtilities.displaySize;
                childCount = (Math.min(point.x, point.y) - AndroidUtilities.dp(164.0f)) / 3;
            }
            if (dp - i4 < childCount) {
                dp2 += AndroidUtilities.dp(44.0f);
                i4 = 0;
            }
            if (dp - i5 < childCount) {
                dp3 += AndroidUtilities.dp(44.0f);
            }
            InviteContactsActivity.this.editText.measure(MeasureSpec.makeMeasureSpec(dp - i4, NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(32.0f), NUM));
            if (!this.animationStarted) {
                dp3 += AndroidUtilities.dp(44.0f);
                i4 += AndroidUtilities.dp(16.0f);
                InviteContactsActivity.this.fieldY = dp2;
                if (this.currentAnimation != null) {
                    boolean z;
                    if (InviteContactsActivity.this.containerHeight != dp2 + AndroidUtilities.dp(44.0f)) {
                        this.animators.add(ObjectAnimator.ofInt(InviteContactsActivity.this, "containerHeight", new int[]{dp2}));
                    }
                    if (InviteContactsActivity.this.editText.getTranslationX() != ((float) i4)) {
                        this.animators.add(ObjectAnimator.ofFloat(InviteContactsActivity.this.editText, str2, new float[]{f}));
                    }
                    if (InviteContactsActivity.this.editText.getTranslationY() != ((float) InviteContactsActivity.this.fieldY)) {
                        ArrayList arrayList3 = this.animators;
                        EditTextBoldCursor access$000 = InviteContactsActivity.this.editText;
                        float[] fArr3 = new float[1];
                        z = false;
                        fArr3[0] = (float) InviteContactsActivity.this.fieldY;
                        arrayList3.add(ObjectAnimator.ofFloat(access$000, str, fArr3));
                    } else {
                        z = false;
                    }
                    InviteContactsActivity.this.editText.setAllowDrawCursor(z);
                    this.currentAnimation.playTogether(this.animators);
                    this.currentAnimation.start();
                    this.animationStarted = true;
                } else {
                    InviteContactsActivity.this.containerHeight = dp3;
                    InviteContactsActivity.this.editText.setTranslationX((float) i4);
                    InviteContactsActivity.this.editText.setTranslationY((float) InviteContactsActivity.this.fieldY);
                }
            } else if (!(this.currentAnimation == null || InviteContactsActivity.this.ignoreScrollEvent || this.removingSpan != null)) {
                InviteContactsActivity.this.editText.bringPointIntoView(InviteContactsActivity.this.editText.getSelectionStart());
            }
            setMeasuredDimension(size, InviteContactsActivity.this.containerHeight);
        }

        /* Access modifiers changed, original: protected */
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            int childCount = getChildCount();
            for (i2 = 0; i2 < childCount; i2++) {
                View childAt = getChildAt(i2);
                childAt.layout(0, 0, childAt.getMeasuredWidth(), childAt.getMeasuredHeight());
            }
        }

        public void addSpan(GroupCreateSpan groupCreateSpan) {
            InviteContactsActivity.this.allSpans.add(groupCreateSpan);
            InviteContactsActivity.this.selectedContacts.put(groupCreateSpan.getKey(), groupCreateSpan);
            InviteContactsActivity.this.editText.setHintVisible(false);
            AnimatorSet animatorSet = this.currentAnimation;
            if (animatorSet != null) {
                animatorSet.setupEndValues();
                this.currentAnimation.cancel();
            }
            this.animationStarted = false;
            this.currentAnimation = new AnimatorSet();
            this.currentAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    SpansContainer.this.addingSpan = null;
                    SpansContainer.this.currentAnimation = null;
                    SpansContainer.this.animationStarted = false;
                    InviteContactsActivity.this.editText.setAllowDrawCursor(true);
                }
            });
            this.currentAnimation.setDuration(150);
            this.addingSpan = groupCreateSpan;
            this.animators.clear();
            this.animators.add(ObjectAnimator.ofFloat(this.addingSpan, "scaleX", new float[]{0.01f, 1.0f}));
            this.animators.add(ObjectAnimator.ofFloat(this.addingSpan, "scaleY", new float[]{0.01f, 1.0f}));
            this.animators.add(ObjectAnimator.ofFloat(this.addingSpan, "alpha", new float[]{0.0f, 1.0f}));
            addView(groupCreateSpan);
        }

        public void removeSpan(final GroupCreateSpan groupCreateSpan) {
            InviteContactsActivity.this.ignoreScrollEvent = true;
            InviteContactsActivity.this.selectedContacts.remove(groupCreateSpan.getKey());
            InviteContactsActivity.this.allSpans.remove(groupCreateSpan);
            groupCreateSpan.setOnClickListener(null);
            AnimatorSet animatorSet = this.currentAnimation;
            if (animatorSet != null) {
                animatorSet.setupEndValues();
                this.currentAnimation.cancel();
            }
            this.animationStarted = false;
            this.currentAnimation = new AnimatorSet();
            this.currentAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    SpansContainer.this.removeView(groupCreateSpan);
                    SpansContainer.this.removingSpan = null;
                    SpansContainer.this.currentAnimation = null;
                    SpansContainer.this.animationStarted = false;
                    InviteContactsActivity.this.editText.setAllowDrawCursor(true);
                    if (InviteContactsActivity.this.allSpans.isEmpty()) {
                        InviteContactsActivity.this.editText.setHintVisible(true);
                    }
                }
            });
            this.currentAnimation.setDuration(150);
            this.removingSpan = groupCreateSpan;
            this.animators.clear();
            this.animators.add(ObjectAnimator.ofFloat(this.removingSpan, "scaleX", new float[]{1.0f, 0.01f}));
            this.animators.add(ObjectAnimator.ofFloat(this.removingSpan, "scaleY", new float[]{1.0f, 0.01f}));
            this.animators.add(ObjectAnimator.ofFloat(this.removingSpan, "alpha", new float[]{1.0f, 0.0f}));
            requestLayout();
        }
    }

    public class InviteAdapter extends SelectionAdapter {
        private Context context;
        private ArrayList<Contact> searchResult = new ArrayList();
        private ArrayList<CharSequence> searchResultNames = new ArrayList();
        private Timer searchTimer;
        private boolean searching;

        public boolean isEnabled(ViewHolder viewHolder) {
            return true;
        }

        public InviteAdapter(Context context) {
            this.context = context;
        }

        public void setSearching(boolean z) {
            if (this.searching != z) {
                this.searching = z;
                notifyDataSetChanged();
            }
        }

        public int getItemCount() {
            if (this.searching) {
                return this.searchResult.size();
            }
            return InviteContactsActivity.this.phoneBookContacts.size() + 1;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View inviteUserCell;
            if (i != 1) {
                inviteUserCell = new InviteUserCell(this.context, true);
            } else {
                inviteUserCell = new InviteTextCell(this.context);
                inviteUserCell.setTextAndIcon(LocaleController.getString("ShareTelegram", NUM), NUM);
            }
            return new Holder(inviteUserCell);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            if (viewHolder.getItemViewType() == 0) {
                Contact contact;
                CharSequence charSequence;
                InviteUserCell inviteUserCell = (InviteUserCell) viewHolder.itemView;
                if (this.searching) {
                    contact = (Contact) this.searchResult.get(i);
                    charSequence = (CharSequence) this.searchResultNames.get(i);
                } else {
                    contact = (Contact) InviteContactsActivity.this.phoneBookContacts.get(i - 1);
                    charSequence = null;
                }
                inviteUserCell.setUser(contact, charSequence);
                inviteUserCell.setChecked(InviteContactsActivity.this.selectedContacts.containsKey(contact.key), false);
            }
        }

        public int getItemViewType(int i) {
            return (this.searching || i != 0) ? 0 : 1;
        }

        public void onViewRecycled(ViewHolder viewHolder) {
            View view = viewHolder.itemView;
            if (view instanceof InviteUserCell) {
                ((InviteUserCell) view).recycle();
            }
        }

        public void searchDialogs(final String str) {
            try {
                if (this.searchTimer != null) {
                    this.searchTimer.cancel();
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
            if (str == null) {
                this.searchResult.clear();
                this.searchResultNames.clear();
                notifyDataSetChanged();
                return;
            }
            this.searchTimer = new Timer();
            this.searchTimer.schedule(new TimerTask() {
                public void run() {
                    try {
                        InviteAdapter.this.searchTimer.cancel();
                        InviteAdapter.this.searchTimer = null;
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                    AndroidUtilities.runOnUIThread(new -$$Lambda$InviteContactsActivity$InviteAdapter$1$puDDzs3DCPG3FhnDL1PVs4vd3QI(this, str));
                }

                public /* synthetic */ void lambda$run$1$InviteContactsActivity$InviteAdapter$1(String str) {
                    Utilities.searchQueue.postRunnable(new -$$Lambda$InviteContactsActivity$InviteAdapter$1$Acay2VbdoDOg6RSwRkjeUNE5RJs(this, str));
                }

                /* JADX WARNING: Removed duplicated region for block: B:36:0x00db A:{LOOP_END, LOOP:1: B:23:0x008b->B:36:0x00db} */
                /* JADX WARNING: Removed duplicated region for block: B:43:0x00cc A:{SYNTHETIC} */
                /* JADX WARNING: Missing block: B:32:0x00c7, code skipped:
            if (r10.contains(r15.toString()) != false) goto L_0x00c9;
     */
                public /* synthetic */ void lambda$null$0$InviteContactsActivity$InviteAdapter$1(java.lang.String r17) {
                    /*
                    r16 = this;
                    r0 = r16;
                    r1 = r17.trim();
                    r1 = r1.toLowerCase();
                    r2 = r1.length();
                    if (r2 != 0) goto L_0x0020;
                L_0x0010:
                    r1 = org.telegram.ui.InviteContactsActivity.InviteAdapter.this;
                    r2 = new java.util.ArrayList;
                    r2.<init>();
                    r3 = new java.util.ArrayList;
                    r3.<init>();
                    r1.updateSearchResults(r2, r3);
                    return;
                L_0x0020:
                    r2 = org.telegram.messenger.LocaleController.getInstance();
                    r2 = r2.getTranslitString(r1);
                    r3 = r1.equals(r2);
                    r4 = 0;
                    if (r3 != 0) goto L_0x0035;
                L_0x002f:
                    r3 = r2.length();
                    if (r3 != 0) goto L_0x0036;
                L_0x0035:
                    r2 = r4;
                L_0x0036:
                    r3 = 0;
                    r5 = 1;
                    if (r2 == 0) goto L_0x003c;
                L_0x003a:
                    r6 = 1;
                    goto L_0x003d;
                L_0x003c:
                    r6 = 0;
                L_0x003d:
                    r6 = r6 + r5;
                    r6 = new java.lang.String[r6];
                    r6[r3] = r1;
                    if (r2 == 0) goto L_0x0046;
                L_0x0044:
                    r6[r5] = r2;
                L_0x0046:
                    r1 = new java.util.ArrayList;
                    r1.<init>();
                    r2 = new java.util.ArrayList;
                    r2.<init>();
                    r7 = 0;
                L_0x0051:
                    r8 = org.telegram.ui.InviteContactsActivity.InviteAdapter.this;
                    r8 = org.telegram.ui.InviteContactsActivity.this;
                    r8 = r8.phoneBookContacts;
                    r8 = r8.size();
                    if (r7 >= r8) goto L_0x00e4;
                L_0x005f:
                    r8 = org.telegram.ui.InviteContactsActivity.InviteAdapter.this;
                    r8 = org.telegram.ui.InviteContactsActivity.this;
                    r8 = r8.phoneBookContacts;
                    r8 = r8.get(r7);
                    r8 = (org.telegram.messenger.ContactsController.Contact) r8;
                    r9 = r8.first_name;
                    r10 = r8.last_name;
                    r9 = org.telegram.messenger.ContactsController.formatName(r9, r10);
                    r9 = r9.toLowerCase();
                    r10 = org.telegram.messenger.LocaleController.getInstance();
                    r10 = r10.getTranslitString(r9);
                    r11 = r9.equals(r10);
                    if (r11 == 0) goto L_0x0088;
                L_0x0087:
                    r10 = r4;
                L_0x0088:
                    r11 = r6.length;
                    r12 = 0;
                    r13 = 0;
                L_0x008b:
                    if (r12 >= r11) goto L_0x00df;
                L_0x008d:
                    r14 = r6[r12];
                    r15 = r9.startsWith(r14);
                    if (r15 != 0) goto L_0x00c9;
                L_0x0095:
                    r15 = new java.lang.StringBuilder;
                    r15.<init>();
                    r3 = " ";
                    r15.append(r3);
                    r15.append(r14);
                    r15 = r15.toString();
                    r15 = r9.contains(r15);
                    if (r15 != 0) goto L_0x00c9;
                L_0x00ac:
                    if (r10 == 0) goto L_0x00ca;
                L_0x00ae:
                    r15 = r10.startsWith(r14);
                    if (r15 != 0) goto L_0x00c9;
                L_0x00b4:
                    r15 = new java.lang.StringBuilder;
                    r15.<init>();
                    r15.append(r3);
                    r15.append(r14);
                    r3 = r15.toString();
                    r3 = r10.contains(r3);
                    if (r3 == 0) goto L_0x00ca;
                L_0x00c9:
                    r13 = 1;
                L_0x00ca:
                    if (r13 == 0) goto L_0x00db;
                L_0x00cc:
                    r3 = r8.first_name;
                    r9 = r8.last_name;
                    r3 = org.telegram.messenger.AndroidUtilities.generateSearchName(r3, r9, r14);
                    r2.add(r3);
                    r1.add(r8);
                    goto L_0x00df;
                L_0x00db:
                    r12 = r12 + 1;
                    r3 = 0;
                    goto L_0x008b;
                L_0x00df:
                    r7 = r7 + 1;
                    r3 = 0;
                    goto L_0x0051;
                L_0x00e4:
                    r3 = org.telegram.ui.InviteContactsActivity.InviteAdapter.this;
                    r3.updateSearchResults(r1, r2);
                    return;
                    */
                    throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.InviteContactsActivity$InviteAdapter$AnonymousClass1.lambda$null$0$InviteContactsActivity$InviteAdapter$1(java.lang.String):void");
                }
            }, 200, 300);
        }

        private void updateSearchResults(ArrayList<Contact> arrayList, ArrayList<CharSequence> arrayList2) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$InviteContactsActivity$InviteAdapter$ZxgmfjRluVB9wR7WmXECALc6XVE(this, arrayList, arrayList2));
        }

        public /* synthetic */ void lambda$updateSearchResults$0$InviteContactsActivity$InviteAdapter(ArrayList arrayList, ArrayList arrayList2) {
            this.searchResult = arrayList;
            this.searchResultNames = arrayList2;
            notifyDataSetChanged();
        }

        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            int itemCount = getItemCount();
            boolean z = false;
            InviteContactsActivity.this.emptyView.setVisibility(itemCount == 1 ? 0 : 4);
            GroupCreateDividerItemDecoration access$2700 = InviteContactsActivity.this.decoration;
            if (itemCount == 1) {
                z = true;
            }
            access$2700.setSingle(z);
        }
    }

    public boolean onFragmentCreate() {
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.contactsImported);
        fetchContacts();
        if (!UserConfig.getInstance(this.currentAccount).contactsReimported) {
            ContactsController.getInstance(this.currentAccount).forceImportContacts();
            UserConfig.getInstance(this.currentAccount).contactsReimported = true;
            UserConfig.getInstance(this.currentAccount).saveConfig(false);
        }
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.contactsImported);
    }

    public void onClick(View view) {
        GroupCreateSpan groupCreateSpan = (GroupCreateSpan) view;
        if (groupCreateSpan.isDeleting()) {
            this.currentDeletingSpan = null;
            this.spansContainer.removeSpan(groupCreateSpan);
            updateHint();
            checkVisibleRows();
            return;
        }
        GroupCreateSpan groupCreateSpan2 = this.currentDeletingSpan;
        if (groupCreateSpan2 != null) {
            groupCreateSpan2.cancelDeleteAnimation();
        }
        this.currentDeletingSpan = groupCreateSpan;
        groupCreateSpan.startDeleteAnimation();
    }

    public View createView(Context context) {
        Context context2 = context;
        this.searching = false;
        this.searchWas = false;
        this.allSpans.clear();
        this.selectedContacts.clear();
        this.currentDeletingSpan = null;
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("InviteFriends", NUM));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    InviteContactsActivity.this.finishFragment();
                }
            }
        });
        this.fragmentView = new ViewGroup(context2) {
            /* Access modifiers changed, original: protected */
            public void onMeasure(int i, int i2) {
                int dp;
                int measuredHeight;
                i = MeasureSpec.getSize(i);
                i2 = MeasureSpec.getSize(i2);
                setMeasuredDimension(i, i2);
                if (AndroidUtilities.isTablet() || i2 > i) {
                    dp = AndroidUtilities.dp(144.0f);
                } else {
                    dp = AndroidUtilities.dp(56.0f);
                }
                InviteContactsActivity.this.infoTextView.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec(dp, Integer.MIN_VALUE));
                InviteContactsActivity.this.counterView.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), NUM));
                if (InviteContactsActivity.this.infoTextView.getVisibility() == 0) {
                    measuredHeight = InviteContactsActivity.this.infoTextView.getMeasuredHeight();
                } else {
                    measuredHeight = InviteContactsActivity.this.counterView.getMeasuredHeight();
                }
                InviteContactsActivity.this.scrollView.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec(dp, Integer.MIN_VALUE));
                InviteContactsActivity.this.listView.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec((i2 - InviteContactsActivity.this.scrollView.getMeasuredHeight()) - measuredHeight, NUM));
                InviteContactsActivity.this.emptyView.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec((i2 - InviteContactsActivity.this.scrollView.getMeasuredHeight()) - AndroidUtilities.dp(72.0f), NUM));
            }

            /* Access modifiers changed, original: protected */
            public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                InviteContactsActivity.this.scrollView.layout(0, 0, InviteContactsActivity.this.scrollView.getMeasuredWidth(), InviteContactsActivity.this.scrollView.getMeasuredHeight());
                InviteContactsActivity.this.listView.layout(0, InviteContactsActivity.this.scrollView.getMeasuredHeight(), InviteContactsActivity.this.listView.getMeasuredWidth(), InviteContactsActivity.this.scrollView.getMeasuredHeight() + InviteContactsActivity.this.listView.getMeasuredHeight());
                InviteContactsActivity.this.emptyView.layout(0, InviteContactsActivity.this.scrollView.getMeasuredHeight() + AndroidUtilities.dp(72.0f), InviteContactsActivity.this.emptyView.getMeasuredWidth(), InviteContactsActivity.this.scrollView.getMeasuredHeight() + InviteContactsActivity.this.emptyView.getMeasuredHeight());
                i4 -= i2;
                int measuredHeight = i4 - InviteContactsActivity.this.infoTextView.getMeasuredHeight();
                InviteContactsActivity.this.infoTextView.layout(0, measuredHeight, InviteContactsActivity.this.infoTextView.getMeasuredWidth(), InviteContactsActivity.this.infoTextView.getMeasuredHeight() + measuredHeight);
                i4 -= InviteContactsActivity.this.counterView.getMeasuredHeight();
                InviteContactsActivity.this.counterView.layout(0, i4, InviteContactsActivity.this.counterView.getMeasuredWidth(), InviteContactsActivity.this.counterView.getMeasuredHeight() + i4);
            }

            /* Access modifiers changed, original: protected */
            public boolean drawChild(Canvas canvas, View view, long j) {
                boolean drawChild = super.drawChild(canvas, view, j);
                if (view == InviteContactsActivity.this.listView || view == InviteContactsActivity.this.emptyView) {
                    InviteContactsActivity.this.parentLayout.drawHeaderShadow(canvas, InviteContactsActivity.this.scrollView.getMeasuredHeight());
                }
                return drawChild;
            }
        };
        ViewGroup viewGroup = (ViewGroup) this.fragmentView;
        this.scrollView = new ScrollView(context2) {
            public boolean requestChildRectangleOnScreen(View view, Rect rect, boolean z) {
                if (InviteContactsActivity.this.ignoreScrollEvent) {
                    InviteContactsActivity.this.ignoreScrollEvent = false;
                    return false;
                }
                rect.offset(view.getLeft() - view.getScrollX(), view.getTop() - view.getScrollY());
                rect.top += InviteContactsActivity.this.fieldY + AndroidUtilities.dp(20.0f);
                rect.bottom += InviteContactsActivity.this.fieldY + AndroidUtilities.dp(50.0f);
                return super.requestChildRectangleOnScreen(view, rect, z);
            }
        };
        this.scrollView.setVerticalScrollBarEnabled(false);
        AndroidUtilities.setScrollViewEdgeEffectColor(this.scrollView, Theme.getColor("windowBackgroundWhite"));
        viewGroup.addView(this.scrollView);
        this.spansContainer = new SpansContainer(context2);
        this.scrollView.addView(this.spansContainer, LayoutHelper.createFrame(-1, -2.0f));
        this.editText = new EditTextBoldCursor(context2) {
            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (InviteContactsActivity.this.currentDeletingSpan != null) {
                    InviteContactsActivity.this.currentDeletingSpan.cancelDeleteAnimation();
                    InviteContactsActivity.this.currentDeletingSpan = null;
                }
                if (motionEvent.getAction() == 0 && !AndroidUtilities.showKeyboard(this)) {
                    clearFocus();
                    requestFocus();
                }
                return super.onTouchEvent(motionEvent);
            }
        };
        this.editText.setTextSize(1, 18.0f);
        this.editText.setHintColor(Theme.getColor("groupcreate_hintText"));
        this.editText.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.editText.setCursorColor(Theme.getColor("groupcreate_cursor"));
        this.editText.setCursorWidth(1.5f);
        this.editText.setInputType(655536);
        this.editText.setSingleLine(true);
        this.editText.setBackgroundDrawable(null);
        this.editText.setVerticalScrollBarEnabled(false);
        this.editText.setHorizontalScrollBarEnabled(false);
        this.editText.setTextIsSelectable(false);
        this.editText.setPadding(0, 0, 0, 0);
        this.editText.setImeOptions(NUM);
        this.editText.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        this.spansContainer.addView(this.editText);
        this.editText.setHintText(LocaleController.getString("SearchFriends", NUM));
        this.editText.setCustomSelectionActionModeCallback(new Callback() {
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                return false;
            }

            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            public void onDestroyActionMode(ActionMode actionMode) {
            }

            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }
        });
        this.editText.setOnKeyListener(new OnKeyListener() {
            private boolean wasEmpty;

            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                boolean z = true;
                if (keyEvent.getAction() == 0) {
                    if (InviteContactsActivity.this.editText.length() != 0) {
                        z = false;
                    }
                    this.wasEmpty = z;
                } else if (keyEvent.getAction() == 1 && this.wasEmpty && !InviteContactsActivity.this.allSpans.isEmpty()) {
                    InviteContactsActivity.this.spansContainer.removeSpan((GroupCreateSpan) InviteContactsActivity.this.allSpans.get(InviteContactsActivity.this.allSpans.size() - 1));
                    InviteContactsActivity.this.updateHint();
                    InviteContactsActivity.this.checkVisibleRows();
                    return true;
                }
                return false;
            }
        });
        this.editText.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void afterTextChanged(Editable editable) {
                if (InviteContactsActivity.this.editText.length() != 0) {
                    InviteContactsActivity.this.searching = true;
                    InviteContactsActivity.this.searchWas = true;
                    InviteContactsActivity.this.adapter.setSearching(true);
                    InviteContactsActivity.this.adapter.searchDialogs(InviteContactsActivity.this.editText.getText().toString());
                    InviteContactsActivity.this.listView.setFastScrollVisible(false);
                    InviteContactsActivity.this.listView.setVerticalScrollBarEnabled(true);
                    InviteContactsActivity.this.emptyView.setText(LocaleController.getString("NoResult", NUM));
                    return;
                }
                InviteContactsActivity.this.closeSearch();
            }
        });
        this.emptyView = new EmptyTextProgressView(context2);
        if (ContactsController.getInstance(this.currentAccount).isLoadingContacts()) {
            this.emptyView.showProgress();
        } else {
            this.emptyView.showTextView();
        }
        this.emptyView.setText(LocaleController.getString("NoContacts", NUM));
        viewGroup.addView(this.emptyView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context2, 1, false);
        this.listView = new RecyclerListView(context2);
        this.listView.setEmptyView(this.emptyView);
        RecyclerListView recyclerListView = this.listView;
        InviteAdapter inviteAdapter = new InviteAdapter(context2);
        this.adapter = inviteAdapter;
        recyclerListView.setAdapter(inviteAdapter);
        this.listView.setLayoutManager(linearLayoutManager);
        this.listView.setVerticalScrollBarEnabled(true);
        this.listView.setVerticalScrollbarPosition(LocaleController.isRTL ? 1 : 2);
        RecyclerListView recyclerListView2 = this.listView;
        GroupCreateDividerItemDecoration groupCreateDividerItemDecoration = new GroupCreateDividerItemDecoration();
        this.decoration = groupCreateDividerItemDecoration;
        recyclerListView2.addItemDecoration(groupCreateDividerItemDecoration);
        viewGroup.addView(this.listView);
        this.listView.setOnItemClickListener(new -$$Lambda$InviteContactsActivity$2CAh12ObsNHUdlUsHSs_VZmRL0I(this));
        this.listView.setOnScrollListener(new OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                if (i == 1) {
                    AndroidUtilities.hideKeyboard(InviteContactsActivity.this.editText);
                }
            }
        });
        this.infoTextView = new TextView(context2);
        String str = "contacts_inviteBackground";
        this.infoTextView.setBackgroundColor(Theme.getColor(str));
        String str2 = "contacts_inviteText";
        this.infoTextView.setTextColor(Theme.getColor(str2));
        this.infoTextView.setGravity(17);
        this.infoTextView.setText(LocaleController.getString("InviteFriendsHelp", NUM));
        this.infoTextView.setTextSize(1, 13.0f);
        String str3 = "fonts/rmedium.ttf";
        this.infoTextView.setTypeface(AndroidUtilities.getTypeface(str3));
        this.infoTextView.setPadding(AndroidUtilities.dp(17.0f), AndroidUtilities.dp(9.0f), AndroidUtilities.dp(17.0f), AndroidUtilities.dp(9.0f));
        viewGroup.addView(this.infoTextView, LayoutHelper.createFrame(-1, -2, 83));
        this.counterView = new FrameLayout(context2);
        this.counterView.setBackgroundColor(Theme.getColor(str));
        this.counterView.setVisibility(4);
        viewGroup.addView(this.counterView, LayoutHelper.createFrame(-1, 48, 83));
        this.counterView.setOnClickListener(new -$$Lambda$InviteContactsActivity$nq-MZzgy9JlkAxS_tdx9DVT5pQg(this));
        LinearLayout linearLayout = new LinearLayout(context2);
        linearLayout.setOrientation(0);
        this.counterView.addView(linearLayout, LayoutHelper.createFrame(-2, -1, 17));
        this.counterTextView = new TextView(context2);
        this.counterTextView.setTypeface(AndroidUtilities.getTypeface(str3));
        this.counterTextView.setTextSize(1, 14.0f);
        this.counterTextView.setTextColor(Theme.getColor(str));
        this.counterTextView.setGravity(17);
        this.counterTextView.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(10.0f), Theme.getColor(str2)));
        this.counterTextView.setMinWidth(AndroidUtilities.dp(20.0f));
        this.counterTextView.setPadding(AndroidUtilities.dp(6.0f), 0, AndroidUtilities.dp(6.0f), AndroidUtilities.dp(1.0f));
        linearLayout.addView(this.counterTextView, LayoutHelper.createLinear(-2, 20, 16, 0, 0, 10, 0));
        this.textView = new TextView(context2);
        this.textView.setTextSize(1, 14.0f);
        this.textView.setTextColor(Theme.getColor(str2));
        this.textView.setGravity(17);
        this.textView.setCompoundDrawablePadding(AndroidUtilities.dp(8.0f));
        this.textView.setText(LocaleController.getString("InviteToTelegram", NUM).toUpperCase());
        this.textView.setTypeface(AndroidUtilities.getTypeface(str3));
        linearLayout.addView(this.textView, LayoutHelper.createLinear(-2, -2, 16));
        updateHint();
        this.adapter.notifyDataSetChanged();
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$0$InviteContactsActivity(View view, int i) {
        if (i == 0 && !this.searching) {
            try {
                Intent intent = new Intent("android.intent.action.SEND");
                intent.setType("text/plain");
                String inviteText = ContactsController.getInstance(this.currentAccount).getInviteText(0);
                intent.putExtra("android.intent.extra.TEXT", inviteText);
                getParentActivity().startActivityForResult(Intent.createChooser(intent, inviteText), 500);
            } catch (Exception e) {
                FileLog.e(e);
            }
        } else if (view instanceof InviteUserCell) {
            InviteUserCell inviteUserCell = (InviteUserCell) view;
            Contact contact = inviteUserCell.getContact();
            if (contact != null) {
                boolean containsKey = this.selectedContacts.containsKey(contact.key);
                if (containsKey) {
                    this.spansContainer.removeSpan((GroupCreateSpan) this.selectedContacts.get(contact.key));
                } else {
                    GroupCreateSpan groupCreateSpan = new GroupCreateSpan(this.editText.getContext(), contact);
                    this.spansContainer.addSpan(groupCreateSpan);
                    groupCreateSpan.setOnClickListener(this);
                }
                updateHint();
                if (this.searching || this.searchWas) {
                    AndroidUtilities.showKeyboard(this.editText);
                } else {
                    inviteUserCell.setChecked(containsKey ^ 1, true);
                }
                if (this.editText.length() > 0) {
                    this.editText.setText(null);
                }
            }
        }
    }

    public /* synthetic */ void lambda$createView$1$InviteContactsActivity(View view) {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            int i = 0;
            for (int i2 = 0; i2 < this.allSpans.size(); i2++) {
                Contact contact = ((GroupCreateSpan) this.allSpans.get(i2)).getContact();
                if (stringBuilder.length() != 0) {
                    stringBuilder.append(';');
                }
                stringBuilder.append((String) contact.phones.get(0));
                if (i2 == 0 && this.allSpans.size() == 1) {
                    i = contact.imported;
                }
            }
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("smsto:");
            stringBuilder2.append(stringBuilder.toString());
            Intent intent = new Intent("android.intent.action.SENDTO", Uri.parse(stringBuilder2.toString()));
            intent.putExtra("sms_body", ContactsController.getInstance(this.currentAccount).getInviteText(i));
            getParentActivity().startActivityForResult(intent, 500);
        } catch (Exception e) {
            FileLog.e(e);
        }
        finishFragment();
    }

    public void onResume() {
        super.onResume();
        EditTextBoldCursor editTextBoldCursor = this.editText;
        if (editTextBoldCursor != null) {
            editTextBoldCursor.requestFocus();
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.contactsImported) {
            fetchContacts();
        }
    }

    @Keep
    public void setContainerHeight(int i) {
        this.containerHeight = i;
        SpansContainer spansContainer = this.spansContainer;
        if (spansContainer != null) {
            spansContainer.requestLayout();
        }
    }

    public int getContainerHeight() {
        return this.containerHeight;
    }

    private void checkVisibleRows() {
        int childCount = this.listView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = this.listView.getChildAt(i);
            if (childAt instanceof InviteUserCell) {
                InviteUserCell inviteUserCell = (InviteUserCell) childAt;
                Contact contact = inviteUserCell.getContact();
                if (contact != null) {
                    inviteUserCell.setChecked(this.selectedContacts.containsKey(contact.key), true);
                }
            }
        }
    }

    private void updateHint() {
        if (this.selectedContacts.isEmpty()) {
            this.infoTextView.setVisibility(0);
            this.counterView.setVisibility(4);
            return;
        }
        this.infoTextView.setVisibility(4);
        this.counterView.setVisibility(0);
        this.counterTextView.setText(String.format("%d", new Object[]{Integer.valueOf(this.selectedContacts.size())}));
    }

    private void closeSearch() {
        this.searching = false;
        this.searchWas = false;
        this.adapter.setSearching(false);
        this.adapter.searchDialogs(null);
        this.listView.setFastScrollVisible(true);
        this.listView.setVerticalScrollBarEnabled(false);
        this.emptyView.setText(LocaleController.getString("NoContacts", NUM));
    }

    private void fetchContacts() {
        this.phoneBookContacts = new ArrayList(ContactsController.getInstance(this.currentAccount).phoneBookContacts);
        Collections.sort(this.phoneBookContacts, -$$Lambda$InviteContactsActivity$r58ALapXATHsxuXB3Kf3_z6GjIA.INSTANCE);
        EmptyTextProgressView emptyTextProgressView = this.emptyView;
        if (emptyTextProgressView != null) {
            emptyTextProgressView.showTextView();
        }
        InviteAdapter inviteAdapter = this.adapter;
        if (inviteAdapter != null) {
            inviteAdapter.notifyDataSetChanged();
        }
    }

    static /* synthetic */ int lambda$fetchContacts$2(Contact contact, Contact contact2) {
        int i = contact.imported;
        int i2 = contact2.imported;
        if (i > i2) {
            return -1;
        }
        return i < i2 ? 1 : 0;
    }

    public ThemeDescription[] getThemeDescriptions() {
        -$$Lambda$InviteContactsActivity$lRM1FX-g-ooIebl8RwlJ1GDWMOI -__lambda_invitecontactsactivity_lrm1fx-g-ooiebl8rwlj1gdwmoi = new -$$Lambda$InviteContactsActivity$lRM1FX-g-ooIebl8RwlJ1GDWMOI(this);
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[46];
        themeDescriptionArr[0] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite");
        themeDescriptionArr[1] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
        themeDescriptionArr[2] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
        themeDescriptionArr[3] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
        themeDescriptionArr[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
        themeDescriptionArr[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
        themeDescriptionArr[6] = new ThemeDescription(this.scrollView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "windowBackgroundWhite");
        themeDescriptionArr[7] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
        themeDescriptionArr[8] = new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, null, null, null, null, "fastScrollActive");
        themeDescriptionArr[9] = new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, null, null, null, null, "fastScrollInactive");
        themeDescriptionArr[10] = new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, null, null, null, null, "fastScrollText");
        themeDescriptionArr[11] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, "divider");
        themeDescriptionArr[12] = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "emptyListPlaceholder");
        themeDescriptionArr[13] = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, "progressCircle");
        themeDescriptionArr[14] = new ThemeDescription(this.editText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[15] = new ThemeDescription(this.editText, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "groupcreate_hintText");
        themeDescriptionArr[16] = new ThemeDescription(this.editText, ThemeDescription.FLAG_CURSORCOLOR, null, null, null, null, "groupcreate_cursor");
        themeDescriptionArr[17] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GroupCreateSectionCell.class}, null, null, null, "graySection");
        themeDescriptionArr[18] = new ThemeDescription(this.listView, 0, new Class[]{GroupCreateSectionCell.class}, new String[]{"drawable"}, null, null, null, "groupcreate_sectionShadow");
        View view = this.listView;
        int i = ThemeDescription.FLAG_TEXTCOLOR;
        Class[] clsArr = new Class[]{GroupCreateSectionCell.class};
        String[] strArr = new String[1];
        strArr[0] = "textView";
        themeDescriptionArr[19] = new ThemeDescription(view, i, clsArr, strArr, null, null, null, "groupcreate_sectionText");
        themeDescriptionArr[20] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{InviteUserCell.class}, new String[]{"textView"}, null, null, null, "groupcreate_sectionText");
        view = this.listView;
        i = ThemeDescription.FLAG_TEXTCOLOR;
        clsArr = new Class[]{InviteUserCell.class};
        strArr = new String[1];
        strArr[0] = "checkBox";
        themeDescriptionArr[21] = new ThemeDescription(view, i, clsArr, strArr, null, null, null, "groupcreate_checkbox");
        themeDescriptionArr[22] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{InviteUserCell.class}, new String[]{"checkBox"}, null, null, null, "groupcreate_checkboxCheck");
        view = this.listView;
        i = ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG;
        clsArr = new Class[]{InviteUserCell.class};
        strArr = new String[1];
        strArr[0] = "statusTextView";
        themeDescriptionArr[23] = new ThemeDescription(view, i, clsArr, strArr, null, null, null, "windowBackgroundWhiteBlueText");
        themeDescriptionArr[24] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{InviteUserCell.class}, new String[]{"statusTextView"}, null, null, null, "windowBackgroundWhiteGrayText");
        themeDescriptionArr[25] = new ThemeDescription(this.listView, 0, new Class[]{InviteUserCell.class}, null, new Drawable[]{Theme.avatar_broadcastDrawable, Theme.avatar_savedDrawable}, null, "avatar_text");
        -$$Lambda$InviteContactsActivity$lRM1FX-g-ooIebl8RwlJ1GDWMOI -__lambda_invitecontactsactivity_lrm1fx-g-ooiebl8rwlj1gdwmoi2 = -__lambda_invitecontactsactivity_lrm1fx-g-ooiebl8rwlj1gdwmoi;
        themeDescriptionArr[26] = new ThemeDescription(null, 0, null, null, null, -__lambda_invitecontactsactivity_lrm1fx-g-ooiebl8rwlj1gdwmoi2, "avatar_backgroundRed");
        themeDescriptionArr[27] = new ThemeDescription(null, 0, null, null, null, -__lambda_invitecontactsactivity_lrm1fx-g-ooiebl8rwlj1gdwmoi2, "avatar_backgroundOrange");
        themeDescriptionArr[28] = new ThemeDescription(null, 0, null, null, null, -__lambda_invitecontactsactivity_lrm1fx-g-ooiebl8rwlj1gdwmoi2, "avatar_backgroundViolet");
        themeDescriptionArr[29] = new ThemeDescription(null, 0, null, null, null, -__lambda_invitecontactsactivity_lrm1fx-g-ooiebl8rwlj1gdwmoi2, "avatar_backgroundGreen");
        themeDescriptionArr[30] = new ThemeDescription(null, 0, null, null, null, -__lambda_invitecontactsactivity_lrm1fx-g-ooiebl8rwlj1gdwmoi2, "avatar_backgroundCyan");
        themeDescriptionArr[31] = new ThemeDescription(null, 0, null, null, null, -__lambda_invitecontactsactivity_lrm1fx-g-ooiebl8rwlj1gdwmoi2, "avatar_backgroundBlue");
        themeDescriptionArr[32] = new ThemeDescription(null, 0, null, null, null, -__lambda_invitecontactsactivity_lrm1fx-g-ooiebl8rwlj1gdwmoi2, "avatar_backgroundPink");
        themeDescriptionArr[33] = new ThemeDescription(this.listView, 0, new Class[]{InviteTextCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[34] = new ThemeDescription(this.listView, 0, new Class[]{InviteTextCell.class}, new String[]{"imageView"}, null, null, null, "windowBackgroundWhiteGrayIcon");
        themeDescriptionArr[35] = new ThemeDescription(this.spansContainer, 0, new Class[]{GroupCreateSpan.class}, null, null, null, "avatar_backgroundGroupCreateSpanBlue");
        themeDescriptionArr[36] = new ThemeDescription(this.spansContainer, 0, new Class[]{GroupCreateSpan.class}, null, null, null, "groupcreate_spanBackground");
        themeDescriptionArr[37] = new ThemeDescription(this.spansContainer, 0, new Class[]{GroupCreateSpan.class}, null, null, null, "groupcreate_spanText");
        themeDescriptionArr[38] = new ThemeDescription(this.spansContainer, 0, new Class[]{GroupCreateSpan.class}, null, null, null, "groupcreate_spanDelete");
        themeDescriptionArr[39] = new ThemeDescription(this.spansContainer, 0, new Class[]{GroupCreateSpan.class}, null, null, null, "avatar_backgroundBlue");
        themeDescriptionArr[40] = new ThemeDescription(this.infoTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "contacts_inviteText");
        themeDescriptionArr[41] = new ThemeDescription(this.infoTextView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "contacts_inviteBackground");
        themeDescriptionArr[42] = new ThemeDescription(this.counterView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "contacts_inviteBackground");
        themeDescriptionArr[43] = new ThemeDescription(this.counterTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "contacts_inviteBackground");
        themeDescriptionArr[44] = new ThemeDescription(this.textView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "contacts_inviteText");
        themeDescriptionArr[45] = new ThemeDescription(this.counterTextView, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "contacts_inviteText");
        return themeDescriptionArr;
    }

    public /* synthetic */ void lambda$getThemeDescriptions$3$InviteContactsActivity() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int childCount = recyclerListView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = this.listView.getChildAt(i);
                if (childAt instanceof InviteUserCell) {
                    ((InviteUserCell) childAt).update(0);
                }
            }
        }
    }
}
