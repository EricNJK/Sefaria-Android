package org.sefaria.sefaria.LinkElements;

import android.view.View;
import android.widget.LinearLayout;

import org.sefaria.sefaria.R;
import org.sefaria.sefaria.Settings;
import org.sefaria.sefaria.Util;
import org.sefaria.sefaria.activities.SuperTextActivity;
import org.sefaria.sefaria.database.LinkFilter;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by nss on 1/26/16.
 */
public class LinkSelectorBar extends LinearLayout {
    public static final int MAX_NUM_LINK_SELECTORS = 5;
    private LinkedList<LinkFilter> linkSelectorQueue; //holds the linkCounts that display the previously selected linkCounts

    LinearLayout selectorListLayout;
    View backButton;
    SuperTextActivity activity;
    OnClickListener linkSelectorBarButtonClick;
    LinkFilter currLinkCount;

    private boolean hasBeenInitialized;

    public LinkSelectorBar(SuperTextActivity activity, OnClickListener linkSelectorBarButtonClick, OnClickListener linkSelectorBackClick) {
        super(activity);
        inflate(activity, R.layout.link_selector_bar, this);
        this.activity = activity;
        this.linkSelectorBarButtonClick = linkSelectorBarButtonClick;
        selectorListLayout = (LinearLayout) findViewById(R.id.link_selection_bar_list);
        backButton = findViewById(R.id.link_back_btn);
        backButton.setOnClickListener(linkSelectorBackClick);


        hasBeenInitialized = false;
    }

    public void add(LinkFilter linkCount, Util.Lang lang) {

        //make sure linkCount is unique. technically should be overriding equals() in LinkFilter, but this actually isn't a strict definition right n
        boolean exists = false;
        ListIterator<LinkFilter> linkIt = linkSelectorQueue.listIterator(0);
        while (linkIt.hasNext()) {
            LinkFilter tempLC = linkIt.next();
            if (LinkFilter.pseudoEquals(tempLC, linkCount)) {
                exists = true;
                break;
            }
        }
        if (!exists) {
            if (linkSelectorQueue.size() >= MAX_NUM_LINK_SELECTORS) linkSelectorQueue.remove();
            linkSelectorQueue.add(linkCount);
        }

        update(linkCount, lang);
    }

    //this function is used to only update the language of the link selector bar
    public void update(Util.Lang lang) {
        update(currLinkCount, lang);
    }

    public void initialUpdate(LinkFilter linkFilterAll, Util.Lang lang) {
        hasBeenInitialized = true;
        List<LinkFilter> yo = Settings.Link.getLinks(activity.getBook().getTitle(Util.Lang.EN), linkFilterAll);
        linkSelectorQueue = new LinkedList<>(yo);
        currLinkCount = null; //initialize to all grayed
        update(lang);
    }


    //if currLinkCount == null, all linkSelectorBarButtons will be gray
    public void update(LinkFilter currLinkCount, Util.Lang lang) {
        this.currLinkCount = currLinkCount;
        selectorListLayout.removeAllViews();
        if (linkSelectorQueue == null) return;
        ListIterator<LinkFilter> linkIt = linkSelectorQueue.listIterator(linkSelectorQueue.size());
        while (linkIt.hasPrevious()) {
            //add children in reverse order
            LinkFilter tempLC = linkIt.previous();
            LinkSelectorBarButton lssb = new LinkSelectorBarButton(activity, tempLC, activity.getBook(), lang);
            lssb.setOnClickListener(linkSelectorBarButtonClick);
            selectorListLayout.addView(lssb);

            if (!LinkFilter.pseudoEquals(tempLC, currLinkCount)) {
                lssb.setTextColor(Util.getColor(activity, R.attr.text_chapter_header_color));
            }
        }

        Settings.Link.setLinks(activity.getBook().getTitle(Util.Lang.EN), linkSelectorQueue);

    }

    public boolean getHasBeenInitialized() {
        return hasBeenInitialized;
    }
}
