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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import com.talool.api.thrift.Address_t;
import com.talool.api.thrift.Category_t;
import com.talool.api.thrift.Customer_t;
import com.talool.api.thrift.DealAcquire_t;
import com.talool.api.thrift.DealOffer_t;
import com.talool.api.thrift.DealType_t;
import com.talool.api.thrift.Deal_t;
import com.talool.api.thrift.Location_t;
import com.talool.api.thrift.MerchantLocation_t;
import com.talool.api.thrift.Merchant_t;
import com.talool.api.thrift.SearchOptions_t;
import com.talool.api.thrift.Sex_t;
import com.talool.api.thrift.SocialAccount_t;
import com.talool.api.thrift.SocialNetwork_t;
import com.talool.core.Address;
import com.talool.core.Category;
import com.talool.core.Customer;
import com.talool.core.Deal;
import com.talool.core.DealAcquire;
import com.talool.core.DealOffer;
import com.talool.core.FactoryManager;
import com.talool.core.Location;
import com.talool.core.Merchant;
import com.talool.core.MerchantLocation;
import com.talool.core.SearchOptions;
import com.talool.core.Sex;
import com.talool.core.service.ServiceException;
import com.talool.core.social.CustomerSocialAccount;

/**
 * @author clintz
 * 
 */
public final class ConversionUtil
{
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
					.getTaloolService().getSocialNetwork(socialAccount_t.getSocialNetwork().name()));
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

		if (dealOffer.getExpires() != null)
		{
			dealOffer_t.setExpires(dealOffer.getExpires().getTime());
		}

		if (dealOffer.getImage() != null)
		{
			dealOffer_t.setImageUrl(dealOffer.getImage().getMediaUrl());
		}

		dealOffer_t.setMerchant(convertToThrift(dealOffer.getMerchant()));
		dealOffer_t.setPrice(dealOffer.getPrice());
		dealOffer_t.setSummary(dealOffer.getSummary());
		dealOffer_t.setTitle(dealOffer.getTitle());

		return dealOffer_t;
	}

	public static Customer_t convertToThrift(final Customer customer)
	{
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

	public static Address_t convertToThrift(final Address address)
	{
		final Address_t thriftAddr = new Address_t();
		thriftAddr.setAddress1(address.getAddress1());
		thriftAddr.setAddress2(address.getAddress2());
		thriftAddr.setCity(address.getCity());
		thriftAddr.setZip(address.getZip());
		thriftAddr.setStateProvinceCounty(address.getStateProvinceCounty());
		thriftAddr.setCountry(address.getCountry());

		return thriftAddr;
	}

	public static MerchantLocation_t convertToThrift(final MerchantLocation merchantLocation)
	{
		if (merchantLocation == null)
		{
			return null;
		}

		final MerchantLocation_t mLoc = new MerchantLocation_t();
		mLoc.setAddress(convertToThrift(merchantLocation.getAddress()));
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

	public static List<DealAcquire_t> convertToThriftDealAcquires(final List<DealAcquire> dealAcquires)
	{
		final List<DealAcquire_t> deals = new ArrayList<DealAcquire_t>();

		for (final DealAcquire _dac : dealAcquires)
		{
			final DealAcquire_t dac = new DealAcquire_t();
			dac.setDealAcquireId(_dac.getId().toString());
			dac.setCreated(_dac.getCreated().getTime());
			dac.setUpdated(_dac.getUpdated().getTime());
			dac.setDeal(convertToThrift(_dac.getDeal()));

			if (_dac.getRedemptionDate() != null)
			{
				dac.setRedeemed(_dac.getRedemptionDate().getTime());
			}

			if (_dac.getSharedByCustomer() != null)
			{
				dac.setSharedByCustomer(convertToThrift(_dac.getSharedByCustomer()));
			}

			if (_dac.getSharedByMerchant() != null)
			{
				dac.setSharedByMerchant(convertToThrift(_dac.getSharedByMerchant()));
			}

			dac.setShareCount(_dac.getShareCount());
			dac.setStatus(_dac.getAcquireStatus().getStatus());

			deals.add(dac);

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
}
