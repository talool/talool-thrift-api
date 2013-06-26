package com.talool.service.util;

import java.util.UUID;

import com.talool.api.thrift.ActivityEvent_t;
import com.talool.api.thrift.ActivityLink_t;
import com.talool.api.thrift.Activity_t;
import com.talool.api.thrift.LinkType;
import com.talool.core.DealAcquire;
import com.talool.core.gift.Gift;

/**
 * 
 * @author clintz
 * 
 */
public final class ActivityUtil
{
	public static Activity_t createPurchase(final UUID dealOfferId, final String dealOfferTitle)
	{
		final StringBuilder sb = new StringBuilder();
		final Activity_t act = new Activity_t();
		act.setActivityDate(System.currentTimeMillis());
		act.setActivityEvent(ActivityEvent_t.PURCHASE);
		sb.append("Purchased ").append(" \"").append(dealOfferTitle).append("\"");
		act.setTitle(sb.toString());

		ActivityLink_t link = new ActivityLink_t(LinkType.DEAL_OFFER, dealOfferId.toString());
		act.setActivityLink(link);

		return act;
	}

	public static Activity_t createFriendGiftReedem(final DealAcquire dealAcquire)
	{
		final StringBuilder sb = new StringBuilder();
		final Activity_t act = new Activity_t();
		act.setActivityDate(System.currentTimeMillis());
		act.setActivityEvent(ActivityEvent_t.FRIEND_GIFT_REDEEM);

		sb.append("Your friend ").append(dealAcquire.getCustomer().getFullName()).append(" redeemed a deal");
		act.setTitle(sb.toString());

		sb.setLength(0);
		sb.append(dealAcquire.getDeal().getTitle()).append(" at ").append(dealAcquire.getDeal().getMerchant().getName());
		act.setSubtitle(sb.toString());

		ActivityLink_t link = new ActivityLink_t(LinkType.DEAL, dealAcquire.getDeal().getId().toString());
		act.setActivityLink(link);

		return act;

	}

	public static Activity_t createFriendRejectGift(final Gift gift)
	{
		final StringBuilder sb = new StringBuilder();
		Activity_t act = new Activity_t();
		act.setActivityDate(System.currentTimeMillis());
		act.setActivityEvent(ActivityEvent_t.FRIEND_GIFT_REJECT);

		sb.append("Your friend ").append(gift.getReceipientName()).append(" rejected a deal");
		act.setTitle(sb.toString());
		sb.setLength(0);

		sb.append(gift.getDealAcquire().getDeal().getTitle()).append("\" at ").
				append(gift.getDealAcquire().getDeal().getMerchant().getName());

		act.setSubtitle(sb.toString());

		return act;

	}

	public static Activity_t createReject(final Gift gift)
	{
		final StringBuilder sb = new StringBuilder();
		Activity_t act = new Activity_t();
		act.setActivityDate(System.currentTimeMillis());
		act.setActivityEvent(ActivityEvent_t.REJECT_GIFT);

		sb.append("Rejected \"").append(gift.getDealAcquire().getDeal().getTitle()).append("\" at ").
				append(gift.getDealAcquire().getDeal().getMerchant().getName());

		act.setTitle(sb.toString());

		sb.setLength(0);

		sb.append("Gift from ").append(gift.getFromCustomer().getFullName());
		act.setSubtitle(sb.toString());

		return act;

	}

	public static Activity_t createRedeem(final DealAcquire dealAcquire)
	{
		final StringBuilder sb = new StringBuilder();
		Activity_t act = new Activity_t();
		act.setActivityDate(System.currentTimeMillis());
		act.setActivityEvent(ActivityEvent_t.REDEEM);

		sb.append("Redeemed \"").append(dealAcquire.getDeal().getTitle()).append("\" at ").
				append(dealAcquire.getDeal().getMerchant().getName());

		act.setTitle(sb.toString());

		sb.setLength(0);
		if (dealAcquire.getGiftId() != null)
		{
			sb.append("Gift from ").append(dealAcquire.getGift().getFromCustomer().getFullName());
			act.setSubtitle(sb.toString());
		}

		ActivityLink_t link = new ActivityLink_t(LinkType.DEAL, dealAcquire.getDeal().getId().toString());
		act.setActivityLink(link);

		return act;

	}
}
