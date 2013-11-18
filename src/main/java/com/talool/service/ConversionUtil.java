/**
 * Copyright 2011, Comcast Corporation. This software and its contents are
 * Comcast confidential and proprietary. It cannot be used, disclosed, or
 * distributed without Comcast's prior written permission. Modification of this
 * software is only allowed at the direction of Comcast Corporation. All allowed
 * modifications must be provided to Comcast Corporation.
 */
package com.talool.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import com.talool.api.thrift.AcquireStatus_t;
import com.talool.api.thrift.Activity_t;
import com.talool.api.thrift.Address_t;
import com.talool.api.thrift.Category_t;
import com.talool.api.thrift.Customer_t;
import com.talool.api.thrift.DealAcquire_t;
import com.talool.api.thrift.DealOfferGeoSummary_t;
import com.talool.api.thrift.DealOffer_t;
import com.talool.api.thrift.DealType_t;
import com.talool.api.thrift.Deal_t;
import com.talool.api.thrift.GiftDetail_t;
import com.talool.api.thrift.GiftStatus_t;
import com.talool.api.thrift.Gift_t;
import com.talool.api.thrift.Location_t;
import com.talool.api.thrift.MerchantLocation_t;
import com.talool.api.thrift.Merchant_t;
import com.talool.api.thrift.PaymentDetail_t;
import com.talool.api.thrift.SearchOptions_t;
import com.talool.api.thrift.Sex_t;
import com.talool.api.thrift.SocialAccount_t;
import com.talool.api.thrift.SocialNetwork_t;
import com.talool.api.thrift.TransactionResult_t;
import com.talool.core.Category;
import com.talool.core.Customer;
import com.talool.core.Deal;
import com.talool.core.DealAcquire;
import com.talool.core.DealOffer;
import com.talool.core.DealOfferGeoSummary;
import com.talool.core.FactoryManager;
import com.talool.core.Location;
import com.talool.core.Merchant;
import com.talool.core.MerchantLocation;
import com.talool.core.SearchOptions;
import com.talool.core.Sex;
import com.talool.core.activity.Activity;
import com.talool.core.gift.EmailGift;
import com.talool.core.gift.FaceBookGift;
import com.talool.core.gift.Gift;
import com.talool.core.service.ServiceException;
import com.talool.core.social.CustomerSocialAccount;
import com.talool.core.social.SocialNetwork;
import com.talool.payment.Card;
import com.talool.payment.PaymentDetail;
import com.talool.payment.TransactionResult;
import com.talool.service.util.Constants;
import com.talool.thrift.ThriftUtil;

/**
 * @author clintz
 * 
 */
public final class ConversionUtil
{
	// TODO - Experiemental do not use ThreadLocal yet! Need to carfeful cleanup
	// tomcat threads in order to use this safely
	// static final ThreadLocal<ConversionOptions> conversionOptions = new
	// ThreadLocal<ConversionOptions>();

	public static class ConversionOptions
	{
		boolean loadMerchant = false;
		boolean loadDeal = false;
		boolean loadMerchantLocations = false;
		boolean loadMerchantAccounts = false;

		ConversionOptions()
		{}

		public ConversionOptions loadMerchant(boolean loadMerchant)
		{
			this.loadMerchant = loadMerchant;
			return this;
		}

		public ConversionOptions loadMerchantAccounts(boolean loadMerchantAccounts)
		{
			this.loadMerchantAccounts = loadMerchantAccounts;
			return this;
		}

		public ConversionOptions loadMerchantLocations(boolean loadMerchantLocations)
		{
			this.loadMerchantLocations = loadMerchantLocations;
			return this;
		}

		public ConversionOptions loadDeal(boolean loadDeal)
		{
			this.loadDeal = loadDeal;
			return this;
		}
	}

	private static final Logger LOG = LoggerFactory.getLogger(ConversionUtil.class);

	/**
	 * TODO - THis needs work - we need to attach to a hibernate session (detached
	 * object)
	 * 
	 * @param thriftCustomer
	 * @return
	 */
	public static Customer convertFromThrift(Customer_t thriftCustomer)
	{
		final Customer cust = FactoryManager.get().getDomainFactory().newCustomer();

		cust.setEmail(thriftCustomer.getEmail());
		cust.setFirstName(thriftCustomer.getFirstName());
		cust.setLastName(thriftCustomer.getLastName());
		cust.setBirthDate(new Date(thriftCustomer.getBirthDate()));

		if (thriftCustomer.getSex() != null)
		{
			cust.setSex(Sex.valueByLetter(thriftCustomer.getSex().name()));
		}

		if (thriftCustomer.getSocialAccounts() != null)
		{
			for (Entry<SocialNetwork_t, SocialAccount_t> entry : thriftCustomer.getSocialAccounts().entrySet())
			{
				final CustomerSocialAccount csa = FactoryManager.get().getDomainFactory().
						newCustomerSocialAccount(entry.getKey().toString());

				copyFromThrift(entry.getValue(), csa, cust);

			}

		}

		return cust;
	}

	public static SearchOptions convertFromThrift(final SearchOptions_t thriftSearchOptions)
	{
		SearchOptions searchOptions = null;

		if (thriftSearchOptions != null)
		{
			searchOptions = new SearchOptions.Builder().maxResults(thriftSearchOptions.getMaxResults())
					.page(thriftSearchOptions.getPage()).sortProperty(thriftSearchOptions.sortProperty)
					.ascending(thriftSearchOptions.ascending).build();
		}

		return searchOptions;

	}

	public static void copyFromThrift(final Customer_t thriftCustomer, final Customer cust)
			throws ServiceException
	{
		cust.setEmail(thriftCustomer.getEmail());
		cust.setFirstName(thriftCustomer.getFirstName());
		cust.setLastName(thriftCustomer.getLastName());

		if (thriftCustomer.getSex() != null)
		{
			cust.setSex(Sex.valueByLetter(thriftCustomer.getSex().name()));
		}

	}

	public static void copyFromThrift(final SocialAccount_t thriftSocialAccnt,
			final CustomerSocialAccount socialAccnt, final Customer customer)
	{
		socialAccnt.setLoginId(thriftSocialAccnt.getLoginId());
		socialAccnt.setCustomer(customer);
		customer.addSocialAccount(socialAccnt);
	}

	public static SocialAccount_t copyToThrift(final CustomerSocialAccount csa)
	{
		final SocialAccount_t socialAccnt = new SocialAccount_t();
		socialAccnt.setCreated(csa.getCreated().getTime());
		socialAccnt.setLoginId(csa.getLoginId());
		socialAccnt.setSocialNetwork(SocialNetwork_t.valueOf(csa.getSocialNetwork().getName()));

		return socialAccnt;
	}

	public static List<DealOffer_t> convertToThrift(final List<DealOffer> dealOffers)
	{
		if (!CollectionUtils.isEmpty(dealOffers))
		{
			List<DealOffer_t> dealOffers_t = new ArrayList<DealOffer_t>();

			for (final DealOffer dealOffer : dealOffers)
			{
				dealOffers_t.add(convertToThrift(dealOffer));
			}

			return dealOffers_t;
		}

		return null;

	}

	public static Deal_t convertToThrift(final Deal mDeal)
	{

		final Deal_t deal = new Deal_t();
		deal.setCode(mDeal.getCode());
		deal.setCreated(mDeal.getCreated().getTime());
		deal.setDealId(mDeal.getId().toString());
		deal.setDealOfferId(mDeal.getDealOfferId().toString());

		deal.setDetails(mDeal.geDetails());
		if (mDeal.getExpires() != null)
		{
			deal.setExpires(mDeal.getExpires().getTime());
		}

		if (mDeal.getImage() != null)
		{
			deal.setImageUrl(mDeal.getImage().getMediaUrl());
		}

		deal.setMerchant(convertToThrift(mDeal.getMerchant()));
		deal.setSummary(mDeal.getSummary());
		deal.setUpdated(mDeal.getUpdated().getTime());
		deal.setTitle(mDeal.getTitle());

		return deal;
	}

	public static Location convertFromThrift(final Location_t location_t)
	{
		if (location_t == null)
		{
			return null;

		}
		Location location = FactoryManager.get().getDomainFactory().newLocation(location_t.getLongitude(),
				location_t.getLatitude());

		return location;
	}

	public static CustomerSocialAccount convertFromThrift(final SocialAccount_t socialAccount_t, final Customer customer)
	{
		final CustomerSocialAccount sac = FactoryManager.get().getDomainFactory()
				.newCustomerSocialAccount(socialAccount_t.getSocialNetwork().name());

		sac.setLoginId(socialAccount_t.getLoginId());
		try
		{
			sac.setSocialNetwork(FactoryManager.get().getServiceFactory()
					.getTaloolService().getSocialNetwork(SocialNetwork.NetworkName.valueOf(socialAccount_t.getSocialNetwork().toString())));
		}
		catch (ServiceException e)
		{
			LOG.error("Problem getting socialNetwork " + socialAccount_t.getSocialNetwork().name());
		}

		sac.setCustomer(customer);

		return sac;
	}

	public static DealOffer_t convertToThrift(final DealOffer dealOffer)
	{
		final DealOffer_t dealOffer_t = new DealOffer_t();
		dealOffer_t.setCode(dealOffer.getCode());
		dealOffer_t.setDealOfferId(dealOffer.getId().toString());
		dealOffer_t.setDealType(DealType_t.valueOf(dealOffer.getType().toString()));
		dealOffer_t.setLocationName(dealOffer.getLocationName());

		if (dealOffer.getExpires() != null)
		{
			dealOffer_t.setExpires(dealOffer.getExpires().getTime());
		}

		if (dealOffer.getDealOfferLogo() != null)
		{
			dealOffer_t.setImageUrl(dealOffer.getDealOfferLogo().getMediaUrl());
		}

		if (dealOffer.getDealOfferBackgroundImage() != null)
		{
			dealOffer_t.setDealOfferBackgroundImage(dealOffer.getDealOfferBackgroundImage().getMediaUrl());
		}

		if (dealOffer.getDealOfferMerchantLogo() != null)
		{
			dealOffer_t.setDealOfferMerchantLogo(dealOffer.getDealOfferMerchantLogo().getMediaUrl());
		}

		dealOffer_t.setMerchant(convertToThrift(dealOffer.getMerchant()));
		dealOffer_t.setPrice(dealOffer.getPrice());
		dealOffer_t.setSummary(dealOffer.getSummary());
		dealOffer_t.setTitle(dealOffer.getTitle());

		return dealOffer_t;
	}

	public static Customer_t convertToThrift(final Customer customer)
	{
		if (customer == null)
		{
			return null;
		}

		final Customer_t thriftCust = new Customer_t();
		thriftCust.setCustomerId(customer.getId().toString());
		thriftCust.setEmail(customer.getEmail());
		thriftCust.setFirstName(customer.getFirstName());
		thriftCust.setLastName(customer.getLastName());
		thriftCust.setCreated(customer.getCreated().getTime());
		thriftCust.setUpdated(customer.getUpdated().getTime());

		if (customer.getSex() != null)
		{
			thriftCust.setSex(Sex_t.valueOf(customer.getSex().getLetter()));
		}

		if (customer.getSocialAccounts() != null)
		{
			Map<SocialNetwork_t, SocialAccount_t> socialAccnts = new HashMap<SocialNetwork_t, SocialAccount_t>();
			thriftCust.setSocialAccounts(socialAccnts);
			for (final CustomerSocialAccount csa : customer.getSocialAccounts().values())
			{
				SocialAccount_t sac = copyToThrift(csa);
				socialAccnts.put(sac.getSocialNetwork(), sac);
			}
		}

		return thriftCust;
	}

	public static MerchantLocation_t convertToThrift(final MerchantLocation merchantLocation)
	{
		if (merchantLocation == null)
		{
			return null;
		}

		final MerchantLocation_t mLoc = new MerchantLocation_t();

		final Address_t addr = new Address_t();
		addr.setAddress1(merchantLocation.getAddress1());
		addr.setAddress2(merchantLocation.getAddress2());
		addr.setCity(merchantLocation.getCity());
		addr.setZip(merchantLocation.getZip());
		addr.setStateProvinceCounty(merchantLocation.getStateProvinceCounty());
		addr.setCountry(merchantLocation.getCountry());
		mLoc.setAddress(addr);

		mLoc.setEmail(merchantLocation.getEmail());
		mLoc.setName(merchantLocation.getLocationName());

		mLoc.setLocationId(merchantLocation.getId());

		if (merchantLocation.getDistanceInMeters() != null)
		{
			mLoc.setDistanceInMeters(merchantLocation.getDistanceInMeters());
		}

		if (merchantLocation.getGeometry() != null)
		{
			mLoc.setLocation(new Location_t(merchantLocation.getGeometry().getCoordinate().x, merchantLocation.getGeometry()
					.getCoordinate().y));
		}

		if (merchantLocation.getLogo() != null)
		{
			mLoc.setLogoUrl(merchantLocation.getLogo().getMediaUrl());
		}

		if (merchantLocation.getMerchantImage() != null)
		{
			mLoc.setMerchantImageUrl(merchantLocation.getMerchantImage().getMediaUrl());
		}

		mLoc.setPhone(merchantLocation.getPhone());
		mLoc.setWebsiteUrl(merchantLocation.getWebsiteUrl());

		return mLoc;
	}

	public static Gift_t convertToThrift(final Gift gift)
	{
		final Gift_t thriftGift = new Gift_t();
		thriftGift.setCreated(gift.getCreated().getTime());
		thriftGift.setFromCustomer(convertToThrift(gift.getFromCustomer()));
		thriftGift.setGiftId(gift.getId().toString());
		thriftGift.setDeal(convertToThrift(gift.getDealAcquire().getDeal()));

		thriftGift.setGiftStatus(GiftStatus_t.valueOf(gift.getGiftStatus().name()));

		return thriftGift;
	}

	public static List<Gift_t> convertToThriftGifts(final List<Gift> gifts)
	{
		if (CollectionUtils.isEmpty(gifts))
		{
			return null;
		}

		final List<Gift_t> thriftGifts = new ArrayList<Gift_t>();

		for (final Gift gift : gifts)
		{
			final Gift_t thriftGift = new Gift_t();
			thriftGift.setCreated(gift.getCreated().getTime());
			thriftGift.setFromCustomer(convertToThrift(gift.getFromCustomer()));
			thriftGift.setGiftId(gift.getId().toString());
			thriftGift.setDeal(convertToThrift(gift.getDealAcquire().getDeal()));
			thriftGifts.add(thriftGift);
		}

		return thriftGifts;
	}

	public static Merchant_t convertToThrift(final Merchant merchant)
	{
		final Merchant_t thriftMerch = new Merchant_t();

		thriftMerch.setMerchantId(merchant.getId().toString());
		thriftMerch.setName(merchant.getName());

		if (merchant.getCategory() != null)
		{
			thriftMerch.setCategory(convertToThriftCategory(merchant.getCategory()));
		}

		final List<MerchantLocation_t> locations = new ArrayList<MerchantLocation_t>();
		for (MerchantLocation mLoc : merchant.getLocations())
		{
			locations.add(convertToThrift(mLoc));
		}
		thriftMerch.setLocations(locations);

		return thriftMerch;
	}

	public static DealAcquire_t convertToThrift(final DealAcquire dealAcquire)
	{
		final DealAcquire_t thriftDealAcquire = new DealAcquire_t();
		thriftDealAcquire.setDealAcquireId(dealAcquire.getId().toString());
		thriftDealAcquire.setCreated(dealAcquire.getCreated().getTime());
		thriftDealAcquire.setUpdated(dealAcquire.getUpdated().getTime());
		thriftDealAcquire.setDeal(convertToThrift(dealAcquire.getDeal()));

		if (dealAcquire.getRedemptionDate() != null)
		{
			thriftDealAcquire.setRedeemed(dealAcquire.getRedemptionDate().getTime());
		}

		if (dealAcquire.getRedemptionCode() != null)
		{
			thriftDealAcquire.setRedemptionCode(dealAcquire.getRedemptionCode());
		}

		thriftDealAcquire.setStatus(AcquireStatus_t.valueOf(dealAcquire.getAcquireStatus().toString()));

		if (dealAcquire.getGift() != null)
		{
			final GiftDetail_t giftDetail = new GiftDetail_t(dealAcquire.getGift().getId().toString(), dealAcquire.getGift().getCreated().getTime());
			giftDetail.setFromName(dealAcquire.getGift().getFromCustomer().getFirstName() + " " + dealAcquire.getGift().getFromCustomer().getLastName());
			giftDetail.setFromEmail(dealAcquire.getGift().getFromCustomer().getEmail());

			// toCustomer may be null because they haven't registered
			final Customer toCustomer = dealAcquire.getGift().getToCustomer();
			if (toCustomer == null)
			{
				if (dealAcquire.getGift() instanceof EmailGift)
				{
					final EmailGift emailGift = (EmailGift) dealAcquire.getGift();
					giftDetail.setToEmail(emailGift.getToEmail());
					giftDetail.setToName(emailGift.getReceipientName());
				}
				else
				{
					final FaceBookGift fbGift = (FaceBookGift) dealAcquire.getGift();
					giftDetail.setToName(fbGift.getReceipientName());
				}
			}
			else
			{
				giftDetail.setToEmail(toCustomer.getEmail());
				giftDetail.setToName(toCustomer.getFirstName() + " " + toCustomer.getLastName());
			}

			thriftDealAcquire.setGiftDetail(giftDetail);

		}
		return thriftDealAcquire;

	}

	public static List<DealAcquire_t> convertToThriftDealAcquires(final List<DealAcquire> dealAcquires)
	{
		final List<DealAcquire_t> deals = new ArrayList<DealAcquire_t>();

		for (final DealAcquire _dac : dealAcquires)
		{
			deals.add(convertToThrift(_dac));
		}

		return deals;
	}

	public static List<Merchant_t> convertToThriftMerchants(final List<Merchant> listOfMerchants)
	{
		final List<Merchant_t> merchants = new ArrayList<Merchant_t>();

		for (final Merchant merch : listOfMerchants)
		{
			merchants.add(convertToThrift(merch));
		}

		return merchants;
	}

	public static Category_t convertToThriftCategory(final Category categeory)
	{
		return new Category_t(categeory.getId(), categeory.getName());
	}

	public static List<Deal_t> convertToThriftDeals(final List<Deal> deals)
	{
		if (CollectionUtils.isEmpty(deals))
		{
			return null;
		}

		final List<Deal_t> thriftDeals = new ArrayList<Deal_t>();

		for (final Deal deal : deals)
		{
			thriftDeals.add(convertToThrift(deal));
		}

		return thriftDeals;

	}

	public static Activity_t convertToThrift(final Activity activity)
	{
		final Activity_t act = new Activity_t();
		try
		{
			ThriftUtil.deserialize(activity.getActivityData(), act, Constants.PROTOCOL_FACTORY);
			act.setActivityId(activity.getId().toString());
		}
		catch (TException e)
		{
			LOG.error("Problem deserializing Activity: " + e.getLocalizedMessage(), e);
		}
		return act;
	}

	public static List<Activity_t> convertToThriftActivites(final List<Activity> activities)
	{
		if (CollectionUtils.isEmpty(activities))
		{
			return null;
		}

		final List<Activity_t> thriftAct = new ArrayList<Activity_t>();

		for (final Activity act : activities)
		{
			thriftAct.add(convertToThrift(act));
		}

		return thriftAct;

	}

	public static TransactionResult_t convertToThrift(final TransactionResult transactionResult)
	{
		if (transactionResult == null)
		{
			return null;
		}

		final TransactionResult_t transactionResult_t = new TransactionResult_t();
		transactionResult_t.setMessage(transactionResult.getMessage());
		transactionResult_t.setSuccess(transactionResult.isSuccess());

		return transactionResult_t;

	}

	public static PaymentDetail convertFromThrift(final PaymentDetail_t paymentDetail_t)
	{
		if (paymentDetail_t == null)
		{
			return null;
		}

		final PaymentDetail paymentDetail = new PaymentDetail();
		paymentDetail.setEncryptedFields(paymentDetail_t.encryptedFields);
		paymentDetail.setSaveCard(paymentDetail_t.saveCard);

		final Card card = new Card();
		card.setAccountNumber(paymentDetail_t.getCard().getAccountNumber()).
				setSecurityCode(paymentDetail_t.getCard().getSecurityCode()).
				setExpirationMonth(paymentDetail_t.getCard().getExpirationMonth()).
				setExpirationYear(paymentDetail_t.getCard().getExpirationYear()).
				setZipCode(paymentDetail_t.getCard().getZipCode());

		paymentDetail.setCard(card);

		if (paymentDetail_t.getPaymentMetadataSize() > 0)
		{
			paymentDetail.setPaymentMetadata(paymentDetail_t.getPaymentMetadata());
		}

		return paymentDetail;

	}

	public static List<Category_t> convertToThriftCategories(final List<Category> categories)
	{
		if (CollectionUtils.isEmpty(categories))
		{
			return null;
		}

		final List<Category_t> categories_t = new ArrayList<Category_t>();

		for (final Category cat : categories)
		{
			categories_t.add(convertToThriftCategory(cat));
		}

		return categories_t;
	}

	public static List<DealOfferGeoSummary_t> convertToThriftDealOfferGeoSummaries(final List<DealOfferGeoSummary> dealOfferSummaries)
	{
		if (dealOfferSummaries == null)
		{
			return null;
		}

		final List<DealOfferGeoSummary_t> dealOfferGeoSummaries_t = new ArrayList<DealOfferGeoSummary_t>(dealOfferSummaries.size());

		for (final DealOfferGeoSummary dealOfferSummary : dealOfferSummaries)
		{
			final DealOfferGeoSummary_t dealOfferGeoSummary_t = new DealOfferGeoSummary_t();

			if (dealOfferSummary.getClosestMerchantInMeters() != null)
			{
				dealOfferGeoSummary_t.setClosestMerchantInMeters(dealOfferSummary.getClosestMerchantInMeters());
			}
			if (dealOfferSummary.getDistanceInMeters() != null)
			{
				dealOfferGeoSummary_t.setDistanceInMeters(dealOfferSummary.getDistanceInMeters());
			}

			dealOfferGeoSummary_t.setDealOffer(convertToThrift(dealOfferSummary.getDealOffer()));

			dealOfferGeoSummary_t.setLongMetrics(dealOfferSummary.getLongMetrics());

			dealOfferGeoSummary_t.setDoubleMetrics(dealOfferSummary.getDoubleMetrics());

			dealOfferGeoSummaries_t.add(dealOfferGeoSummary_t);

		}

		return dealOfferGeoSummaries_t;
	}

}
