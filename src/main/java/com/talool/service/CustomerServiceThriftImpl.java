package com.talool.service;

import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.talool.api.thrift.Activity_t;
import com.talool.api.thrift.CTokenAccessResponse_t;
import com.talool.api.thrift.CTokenAccess_t;
import com.talool.api.thrift.Category_t;
import com.talool.api.thrift.CustomerService_t;
import com.talool.api.thrift.Customer_t;
import com.talool.api.thrift.DealAcquire_t;
import com.talool.api.thrift.DealOfferGeoSummariesResponse_t;
import com.talool.api.thrift.DealOfferGeoSummary_t;
import com.talool.api.thrift.DealOffer_t;
import com.talool.api.thrift.Deal_t;
import com.talool.api.thrift.EmailMessageResponse_t;
import com.talool.api.thrift.Gift_t;
import com.talool.api.thrift.Location_t;
import com.talool.api.thrift.Merchant_t;
import com.talool.api.thrift.MerchantsResponse_t;
import com.talool.api.thrift.PaymentDetail_t;
import com.talool.api.thrift.PropertyConstants;
import com.talool.api.thrift.SearchOptions_t;
import com.talool.api.thrift.ServiceException_t;
import com.talool.api.thrift.SocialAccount_t;
import com.talool.api.thrift.SocialNetwork_t;
import com.talool.api.thrift.TNotFoundException_t;
import com.talool.api.thrift.TServiceException_t;
import com.talool.api.thrift.TUserException_t;
import com.talool.api.thrift.Token_t;
import com.talool.api.thrift.TransactionResult_t;
import com.talool.api.thrift.ValidateCodeResponse_t;
import com.talool.cache.TagCache;
import com.talool.core.AccountType;
import com.talool.core.Customer;
import com.talool.core.Deal;
import com.talool.core.DealAcquire;
import com.talool.core.DealOffer;
import com.talool.core.DealOfferGeoSummariesResult;
import com.talool.core.DealOfferPurchase;
import com.talool.core.DevicePresence;
import com.talool.core.FactoryManager;
import com.talool.core.Merchant;
import com.talool.core.MerchantCodeGroup;
import com.talool.core.activity.Activity;
import com.talool.core.gift.Gift;
import com.talool.core.service.ActivityService;
import com.talool.core.service.CustomerService;
import com.talool.core.service.InvalidInputException;
import com.talool.core.service.NotFoundException;
import com.talool.core.service.RequestHeaderSupport;
import com.talool.core.service.ServiceException;
import com.talool.core.service.TaloolService;
import com.talool.core.social.CustomerSocialAccount;
import com.talool.core.social.SocialNetwork;
import com.talool.domain.DevicePresenceImpl;
import com.talool.messaging.DevicePresenceManager;
import com.talool.payment.TransactionResult;
import com.talool.payment.braintree.BraintreeUtil;
import com.talool.service.mail.EmailMessage;
import com.talool.service.mail.FreemarkerUtil;
import com.talool.service.util.Constants;
import com.talool.service.util.ExceptionUtil;
import com.talool.service.util.TokenUtil;
import com.talool.thrift.ThriftUtil;
import com.talool.utils.KeyValue;

/**
 * 
 * Thrift implementation of the Talool Customer service
 * 
 * @author clintz
 */

public class CustomerServiceThriftImpl implements CustomerService_t.Iface {
  private static final Logger LOG = LoggerFactory.getLogger(CustomerServiceThriftImpl.class);

  private static final int CATEGORY_REFRESH_INTERVAL = 600000;

  private static final transient TaloolService taloolService = FactoryManager.get().getServiceFactory().getTaloolService();

  private static final transient CustomerService customerService = FactoryManager.get().getServiceFactory().getCustomerService();

  private static final transient ActivityService activityService = FactoryManager.get().getServiceFactory().getActivityService();

  private static final ImmutableList<Merchant_t> EMPTY_MERCHANTS = ImmutableList.of();
  private static final ImmutableList<Category_t> EMPTY_CATEGORIES = ImmutableList.of();
  private static final ImmutableList<Activity_t> EMPTY_ACTIVITIES = ImmutableList.of();
  private static final ImmutableList<DealAcquire_t> EMPTY_ACQUIRES = ImmutableList.of();

  private volatile List<Category_t> categories = EMPTY_CATEGORIES;

  private static final CTokenAccessResponse_t EMPTY_TOKEN_ACCESS_RESPONSE = new CTokenAccessResponse_t();
  private static final DealOfferGeoSummariesResponse_t EMPTY_DEAL_OFFER_GEO_SUMMARIES_RESPONSE = new DealOfferGeoSummariesResponse_t(false);

  // a thread local convenience
  private static ResponseTimer responseTimer = new ResponseTimer();

  private CategoryThread categoryThread;

  private class CategoryThread extends Thread {

    @Override
    public void run() {
      while (true) {
        try {
          categories = ConversionUtil.convertToThriftCategories(TagCache.get().getCategories());
          if (CollectionUtils.isEmpty(categories)) {
            LOG.info("Loaded empty categories, sleeping for 1 secs then retry");
            sleep(1000);
          } else {
            LOG.info("Loaded " + categories.size() + " total categories.  Refreshing in " + CATEGORY_REFRESH_INTERVAL + " ms");
            sleep(CATEGORY_REFRESH_INTERVAL);
          }
        } catch (Exception e) {
          LOG.error(e.getLocalizedMessage(), e);
        }

      }

    }

  }

  public CustomerServiceThriftImpl() {
    super();

    categoryThread = new CategoryThread();
    categoryThread.setName("ThriftCategoryThread");
    categoryThread.setDaemon(true);
    categoryThread.start();
    LOG.info("Started CategoryThread");

  }

  @Override
  public CTokenAccess_t createAccount(final Customer_t customer, final String password) throws ServiceException_t, TException {
    CTokenAccess_t token = null;
    UUID whiteLabelPublisherMerchantId = null;
    // white label header?
    final String whiteLabelIdHeader = RequestUtils.getRequest().getHeader(Constants.HEADER_X_WHITE_LABEL_ID);

    if (StringUtils.isNotEmpty(whiteLabelIdHeader)) {
      whiteLabelPublisherMerchantId = UUID.fromString(whiteLabelIdHeader);
    }


    if (LOG.isDebugEnabled()) {
      LOG.debug("createAccount received for :" + customer);
    }

    try {
      // test if account exits
      if (taloolService.emailExists(AccountType.CUS, customer.getEmail())) {
        LOG.error("Email already taken: " + customer.getEmail());
        throw new ServiceException_t(ErrorCode.ACCOUNT_ALREADY_TAKEN.getCode(), ErrorCode.ACCOUNT_ALREADY_TAKEN.getMessage());
      }
    } catch (ServiceException e) {
      LOG.error("Problem registering customer: " + e, e);
      throw new ServiceException_t(e.getErrorCode().getCode(), e.getMessage());
    }

    try {
      final Customer taloolCustomer = ConversionUtil.convertFromThrift(customer);
      if (customer.getSocialAccounts() != null) {
        for (final SocialAccount_t sac : customer.getSocialAccounts().values()) {
          final CustomerSocialAccount taloolSocialAccount = ConversionUtil.convertFromThrift(sac, taloolCustomer);
          taloolCustomer.addSocialAccount(taloolSocialAccount);
        }
      }

      customerService.createAccount(taloolCustomer, password, whiteLabelPublisherMerchantId);
      final Customer_t updatedCustomer = ConversionUtil.convertToThrift(taloolCustomer);
      token = TokenUtil.createTokenAccess(updatedCustomer);
    } catch (ServiceException se) {
      LOG.error("Problem creating account: " + se.getLocalizedMessage(), se);
      throw new ServiceException_t(se.getErrorCode().getCode(), se.getMessage());
    } catch (Exception e) {
      LOG.error("Problem creating customer: " + e.getLocalizedMessage(), e);
      throw new ServiceException_t(ErrorCode.UNKNOWN.getCode(), e.getLocalizedMessage());
    }

    return token;
  }

  private void beginRequest(final String method) {
    if (!ServiceApiConfig.get().logApiMethodResponseTimes()) {
      return;
    }

    responseTimer.get().watch.reset();
    responseTimer.get().method = method;
    responseTimer.get().watch.start();
  }

  private void endRequest() {
    if (!ServiceApiConfig.get().logApiMethodResponseTimes()) {
      return;
    }

    responseTimer.get().watch.stop();

    LOG.debug(responseTimer.get().method + "/" + responseTimer.get().watch.getTime());

  }

  @Override
  public CTokenAccess_t authenticate(final String email, final String password) throws ServiceException_t, TException {
    CTokenAccess_t token = null;
    Customer customer = null;
    Customer_t thriftCust = null;

    beginRequest("authenticate");

    if (LOG.isDebugEnabled()) {
      LOG.debug("authenticate received for :" + email);
    }

    try {
      try {
        setThreadLocalServiceHeaders(customerService);
        customer = customerService.authenticateCustomer(email, password);
      } catch (ServiceException e) {
        LOG.error("Problem authenticating customer: " + e, e);
        throw new ServiceException_t(e.getErrorCode().getCode(), e.getMessage());
      }

      if (customer == null) {
        throw new ServiceException_t(ErrorCode.EMAIL_OR_PASS_INVALID.getCode(), ErrorCode.EMAIL_OR_PASS_INVALID.getMessage());
      }
      try {
        thriftCust = ConversionUtil.convertToThrift(customer);
        token = TokenUtil.createTokenAccess(thriftCust);
        return token;
      } catch (Exception e) {
        LOG.error("Problem converting customer: " + e, e);
        throw new ServiceException_t(1000, e.getMessage());
      }
    } catch (ServiceException_t e) {
      // yes this catch is here simply so we can have a finally and endRequest
      // cleanly
      throw e;
    } finally {
      endRequest();
    }

  }

  @Override
  public boolean customerEmailExists(final String email) throws ServiceException_t, TException {

    if (LOG.isDebugEnabled()) {
      LOG.debug("customerEmailExists received: " + email);
    }

    try {
      return taloolService.emailExists(AccountType.CUS, email);
    }

    catch (Exception ex) {
      LOG.error("Problem saving customer: " + ex, ex);
      throw new ServiceException_t(1033, "Problem customerEmailExists");
    }
  }

  @Deprecated
  @Override
  /**
   * Deprecated - see  getMerchantAcquiresWithLocation(SearchOptions_t searchOptions, Location_t location) 
   */
  public List<Merchant_t> getMerchantAcquires(final SearchOptions_t searchOptions) throws ServiceException_t, TException {

    final Token_t token = TokenUtil.getTokenFromRequest(true);
    List<Merchant> merchants = null;
    List<Merchant_t> thriftMerchants = null;

    beginRequest("getMerchantAcquires");

    if (LOG.isDebugEnabled()) {
      if (searchOptions != null) {
        LOG.debug(String.format("CustomerId %s getMerchantAcquires with searchOptions %s", token.getAccountId(), searchOptions.toString()));
      } else {
        LOG.debug(String.format("CustomerId %s getMerchantAcquiress with no searchOptions", token.getAccountId()));
      }

    }

    try {
      merchants = customerService.getMerchantAcquires(UUID.fromString(token.getAccountId()), ConversionUtil.convertFromThrift(searchOptions));

      thriftMerchants = ConversionUtil.convertToThriftMerchants(merchants);

      if (LOG.isDebugEnabled() && thriftMerchants != null) {
        long byteTotal = 0;
        for (Merchant_t merchant : thriftMerchants) {
          byteTotal += ThriftUtil.serialize(merchant, Constants.PROTOCOL_FACTORY).length;
        }

        LOG.debug(String.format("getMerchantAcquires - serializing %d merchants totalBytes %d (%s)", thriftMerchants.size(), byteTotal,
            FileUtils.byteCountToDisplaySize(byteTotal)));

      }

      return CollectionUtils.isEmpty(thriftMerchants) ? EMPTY_MERCHANTS : thriftMerchants;
    } catch (Exception ex) {
      LOG.error("Problem getMerchantAcquires for customer " + token.getAccountId(), ex);
      throw new ServiceException_t(1087, "Problem getting merchants");
    } finally {
      endRequest();
    }

  }

  private void setThreadLocalServiceHeaders(final RequestHeaderSupport requestHeaderSupportService) {
    requestHeaderSupportService.setRequestHeaders(getRequestHeaders());
  }

  /**
   * Gets single header values only. Duplicate headers are not supported in this call. For example,
   * multiple "Set-Cookie" headers are not supported
   * 
   * @return
   */
  private Map<String, String> getRequestHeaders() {
    final Map<String, String> requestHeaders = new HashMap<String, String>();
    final HttpServletRequest request = RequestUtils.getRequest();

    @SuppressWarnings("unchecked")
    Enumeration<String> headerNames = request.getHeaderNames();

    while (headerNames.hasMoreElements()) {
      String headerName = headerNames.nextElement();
      if (headerName != null) {
        requestHeaders.put(headerName, request.getHeader(headerName));
      }

    }

    return requestHeaders;
  }

  @Override
  public List<DealAcquire_t> getDealAcquires(final String merchantId, final SearchOptions_t searchOptions) throws ServiceException_t, TException {
    final Token_t token = TokenUtil.getTokenFromRequest(true);
    List<DealAcquire> dealAcquires = null;

    beginRequest("getDealAcquires");

    if (LOG.isDebugEnabled()) {
      if (searchOptions != null) {
        LOG.debug(String.format("CustomerId %s getting deal acquires for merchantId %s and searchOptions %s", token.getAccountId(), merchantId,
            searchOptions));
      } else {
        LOG.debug(String.format("CustomerId %s getting deal acquires for merchantId %s and no searchOptions", token.getAccountId(), merchantId));
      }

    }

    try {
      setThreadLocalServiceHeaders(customerService);



      final HttpServletRequest request = RequestUtils.getRequest();
      final String userAgent = request.getHeader(Constants.HEADER_USER_AGENT);
      Calendar c = Calendar.getInstance();
      if (StringUtils.containsIgnoreCase(userAgent, "iphone")) {
        // Pass an old date for iphone, bc the data is managed on the device
        c.roll(Calendar.YEAR, -2);
      } else {
        // Pass the date for Android, bc any filtering of expired offers needs to happen here
        c.add(Calendar.DAY_OF_YEAR, -31);
      }

      dealAcquires = customerService.getDealAcquires(UUID.fromString(token.getAccountId()), UUID.fromString(merchantId), null, c.getTime());

      return CollectionUtils.isEmpty(dealAcquires) ? EMPTY_ACQUIRES : ConversionUtil.convertToThriftDealAcquires(dealAcquires);

    } catch (Exception ex) {
      LOG.error("There was a problem retrieving deals for merchant: " + merchantId, ex);
      throw new ServiceException_t(1087, "There was a problem retrieving deals for merchant");
    } finally {
      endRequest();
    }

  }

  @Override
  public String redeem(final String dealAcquireId, final Location_t location) throws ServiceException_t, TException {
    final Token_t token = TokenUtil.getTokenFromRequest(true);
    String redemptionCode = null;

    beginRequest("redeem");

    if (LOG.isDebugEnabled()) {
      LOG.debug(String.format("CustomerId %s redeeming dealAcquireId %s", token.getAccountId(), dealAcquireId));
    }

    final UUID customerUuid = UUID.fromString(token.getAccountId());
    final UUID dealAcquireUuid = UUID.fromString(dealAcquireId);

    setThreadLocalServiceHeaders(customerService);

    try {
      redemptionCode = customerService.redeemDeal(UUID.fromString(dealAcquireId), customerUuid, ConversionUtil.convertFromThrift(location));
    } catch (ServiceException e) {
      LOG.error("There was a problem redeeming deal : " + dealAcquireId, e);
      endRequest();
      throw new ServiceException_t(e.getErrorCode().getCode(), e.getMessage());
    }

    try {
      final DealAcquire dealAcquire = customerService.getDealAcquire(dealAcquireUuid);

      Activity activity = ActivityFactory.createRedeem(dealAcquire, customerUuid);
      activityService.save(activity);

      // if we have a gift, we have a bi-directional activity (for friend that
      // gave gift)
      final Gift gift = dealAcquire.getGift();
      if (gift != null) {
        activity = ActivityFactory.createFriendGiftReedem(dealAcquire);
        activityService.save(activity);
      }

      return redemptionCode;
    } catch (ServiceException e) {
      LOG.error("There was a problem redeeming deal : " + dealAcquireId, e);
      throw new ServiceException_t(e.getErrorCode().getCode(), e.getMessage());
    } finally {
      endRequest();
    }

  }

  @Override
  // TODO Change this method to getDealOffers based on some business criteria
  public List<DealOffer_t> getDealOffers() throws ServiceException_t, TException {
    List<DealOffer> dealOffers = null;

    try {
      dealOffers = taloolService.getDealOffers();
    } catch (ServiceException e) {
      LOG.error(e.getMessage(), e);
      throw new ServiceException_t(e.getErrorCode().getCode(), e.getMessage());
    }

    return ConversionUtil.convertToThrift(dealOffers);
  }

  @Override
  public void purchaseDealOffer(final String dealOfferId) throws ServiceException_t, TException {
    final Token_t token = TokenUtil.getTokenFromRequest(true);

    try {
      final UUID customerUuid = UUID.fromString(token.getAccountId());
      final UUID dealOfferUuid = UUID.fromString(dealOfferId);
      customerService.createDealOfferPurchase(customerUuid, dealOfferUuid);

      final DealOffer dealOffer = taloolService.getDealOffer(dealOfferUuid);

      // create a purchase activity
      final Activity activity = ActivityFactory.createPurchase(dealOffer, customerUuid);

      activityService.save(activity);
    } catch (ServiceException e) {
      LOG.error(e.getMessage(), e);
      throw new ServiceException_t(e.getErrorCode().getCode(), e.getMessage());
    }

  }

  @Override
  public List<Merchant_t> getMerchantsWithin(final Location_t location, final int maxMiles, final SearchOptions_t searchOptions)
      throws ServiceException_t, TException {
    List<Merchant> merchants = null;

    beginRequest("getMerchantsWithin");

    try {
      merchants =
          taloolService.getMerchantsWithin(ConversionUtil.convertFromThrift(location), maxMiles, ConversionUtil.convertFromThrift(searchOptions));

    } catch (ServiceException e) {
      LOG.error(e.getMessage(), e);
      throw new ServiceException_t(e.getErrorCode().getCode(), e.getMessage());
    } finally {
      endRequest();
    }

    final List<Merchant_t> thriftMerchants = ConversionUtil.convertToThriftMerchants(merchants);

    if (LOG.isDebugEnabled() && thriftMerchants != null) {
      long byteTotal = 0;
      for (Merchant_t merchant : thriftMerchants) {
        byteTotal += ThriftUtil.serialize(merchant, Constants.PROTOCOL_FACTORY).length;
      }

      LOG.debug(String.format("getMerchantsWithin - serializing %d merchants totalBytes %d (%s)", thriftMerchants.size(), byteTotal,
          FileUtils.byteCountToDisplaySize(byteTotal)));

    }

    return CollectionUtils.isEmpty(thriftMerchants) ? EMPTY_MERCHANTS : thriftMerchants;

  }

  @Override
  public void addFavoriteMerchant(final String merchantId) throws ServiceException_t, TException {
    final Token_t token = TokenUtil.getTokenFromRequest(true);

    beginRequest("addFavoriteMerchant");

    try {
      customerService.addFavoriteMerchant(UUID.fromString(token.getAccountId()), UUID.fromString(merchantId));
    } catch (ServiceException e) {
      throw new ServiceException_t(e.getErrorCode().getCode(), e.getMessage());
    } finally {
      endRequest();
    }

  }

  @Override
  public void removeFavoriteMerchant(final String merchantId) throws ServiceException_t, TException {
    final Token_t token = TokenUtil.getTokenFromRequest(true);

    beginRequest("removeFavoriteMerchant");

    try {
      customerService.removeFavoriteMerchant(UUID.fromString(token.getAccountId()), UUID.fromString(merchantId));
    } catch (ServiceException e) {
      throw new ServiceException_t(e.getErrorCode().getCode(), e.getMessage());
    } finally {
      endRequest();
    }

  }

  @Override
  public List<Merchant_t> getFavoriteMerchants(final SearchOptions_t searchOptions) throws ServiceException_t, TException {
    final Token_t token = TokenUtil.getTokenFromRequest(true);
    List<Merchant> merchants = null;

    beginRequest("getFavoriteMerchants");

    try {
      merchants = customerService.getFavoriteMerchants(UUID.fromString(token.getAccountId()), ConversionUtil.convertFromThrift(searchOptions));

      return ConversionUtil.convertToThriftMerchants(merchants);
    } catch (ServiceException e) {
      throw new ServiceException_t(e.getErrorCode().getCode(), e.getMessage());
    } finally {
      endRequest();
    }

  }

  @Override
  public List<Category_t> getCategories() throws ServiceException_t, TException {
    return categories;
  }

  @Override
  public List<Merchant_t> getMerchantAcquiresByCategory(final int categoryId, final SearchOptions_t searchOptions) throws ServiceException_t,
      TException {
    final Token_t token = TokenUtil.getTokenFromRequest(true);
    List<Merchant> merchants = null;

    beginRequest("getMerchantAcquiresByCategory");

    try {
      merchants =
          customerService.getMerchantAcquires(UUID.fromString(token.getAccountId()), categoryId, ConversionUtil.convertFromThrift(searchOptions));

      return ConversionUtil.convertToThriftMerchants(merchants);
    } catch (ServiceException e) {
      throw new ServiceException_t(e.getErrorCode().getCode(), e.getMessage());
    } finally {
      endRequest();
    }

  }

  @Override
  public void removeSocialAccount(final SocialNetwork_t socialNetwork_t) throws ServiceException_t, TException {
    final Token_t token = TokenUtil.getTokenFromRequest(true);

    try {
      customerService.removeSocialAccount(UUID.fromString(token.getAccountId()),
          taloolService.getSocialNetwork(SocialNetwork.NetworkName.valueOf(socialNetwork_t.toString())));
    } catch (ServiceException e) {
      throw new ServiceException_t(e.getErrorCode().getCode(), e.getMessage());
    }

  }

  @Override
  public void addSocialAccount(final SocialAccount_t socialAccount_t) throws ServiceException_t, TException {
    final Token_t token = TokenUtil.getTokenFromRequest(true);

    if (LOG.isDebugEnabled()) {
      LOG.debug(String.format("CustomerId %s adding social account %s", token.getAccountId(), socialAccount_t.toString()));
    }

    try {
      final Customer cust = customerService.getCustomerById(UUID.fromString(token.getAccountId()));

      final CustomerSocialAccount sac = FactoryManager.get().getDomainFactory().newCustomerSocialAccount(socialAccount_t.getSocialNetwork().name());

      ConversionUtil.copyFromThrift(socialAccount_t, sac, cust);

      customerService.save(sac);
    } catch (ServiceException e) {
      LOG.error("Problem addSocialAccount for customerId: " + token.getAccountId(), e);
      throw new ServiceException_t(e.getErrorCode().getCode(), e.getMessage());
    }

  }

  @Override
  public String giftToFacebook(final String dealAcquireId, final String facebookId, final String receipientName) throws ServiceException_t,
      TException {
    final Token_t token = TokenUtil.getTokenFromRequest(true);
    UUID giftId = null;

    beginRequest("giftToFacebook");

    if (LOG.isDebugEnabled()) {
      LOG.debug(String.format("CustomerId %s giftToFacebook dealAcquireId %s facebookId %s receipientName %s", token.getAccountId(), dealAcquireId,
          facebookId, receipientName));
    }

    try {
      giftId = customerService.giftToFacebook(UUID.fromString(token.getAccountId()), UUID.fromString(dealAcquireId), facebookId, receipientName);

      return giftId.toString();
    } catch (ServiceException e) {
      LOG.error("Problem giftToFacebook for customerId: " + token.getAccountId(), e);
      throw new ServiceException_t(e.getErrorCode().getCode(), e.getMessage());
    } finally {
      endRequest();
    }

  }

  @Override
  public String giftToEmail(final String dealAcquireId, final String email, final String receipientName) throws ServiceException_t, TException {
    final Token_t token = TokenUtil.getTokenFromRequest(true);
    UUID giftId = null;

    beginRequest("giftToEmail");

    if (LOG.isDebugEnabled()) {
      LOG.debug(String.format("CustomerId %s giftToEmail dealAcquireId %s email %s receipientName %s", token.getAccountId(), dealAcquireId, email,
          receipientName));
    }

    try {

      giftId = customerService.giftToEmail(UUID.fromString(token.getAccountId()), UUID.fromString(dealAcquireId), email, receipientName);

      return giftId.toString();
    } catch (ServiceException e) {
      LOG.error("Problem giftToEmail for customerId: " + token.getAccountId(), e);
      throw new ServiceException_t(e.getErrorCode().getCode(), e.getMessage());
    } finally {
      endRequest();
    }

  }

  @Override
  public Gift_t getGift(final String giftId) throws ServiceException_t, TException {
    final Token_t token = TokenUtil.getTokenFromRequest(true);
    Gift_t thriftGift = null;

    beginRequest("getGift");

    if (LOG.isDebugEnabled()) {
      LOG.debug(String.format("CustomerId %s getGifts", token.getAccountId()));
    }

    try {
      final Gift gift = customerService.getGift(UUID.fromString(giftId));

      // throw exception if toCustomer is a valid talool account and the token doesn't match
      if (gift != null && gift.getToCustomer() != null && (!gift.getToCustomer().getId().toString().equals(token.getAccountId()))) {
        throw new ServiceException_t(ErrorCode.NOT_GIFT_RECIPIENT.getCode(), ErrorCode.NOT_GIFT_RECIPIENT.getMessage());
      } else {
        thriftGift = ConversionUtil.convertToThrift(gift);
      }

    } catch (ServiceException e) {
      LOG.error("Problem getGifts for customerId: " + token.getAccountId(), e);
      throw new ServiceException_t(e.getErrorCode().getCode(), e.getMessage());
    } catch (ServiceException_t e) {
      LOG.error("Problem getGifts for customerId: " + token.getAccountId(), e);
      throw e;
    } finally {
      endRequest();
    }

    return thriftGift;

  }

  @Override
  public DealAcquire_t acceptGift(final String giftId) throws ServiceException_t, TException {
    final Token_t token = TokenUtil.getTokenFromRequest(true);

    beginRequest("acceptGift");

    if (LOG.isDebugEnabled()) {
      LOG.debug(String.format("CustomerId %s acceptGift giftId %s", token.getAccountId(), giftId));
    }

    try {
      final DealAcquire dac = customerService.acceptGift(UUID.fromString(giftId), UUID.fromString(token.getAccountId()));
      return ConversionUtil.convertToThrift(dac);
    } catch (ServiceException e) {
      LOG.error(String.format("Problem acceptGift for customerId %s giftId %s", token.getAccountId(), giftId), e);
      throw new ServiceException_t(e.getErrorCode().getCode(), e.getMessage());
    } finally {
      endRequest();
    }

  }

  @Override
  public void rejectGift(final String giftId) throws ServiceException_t, TException {
    final Token_t token = TokenUtil.getTokenFromRequest(true);

    beginRequest("rejectGift");

    if (LOG.isDebugEnabled()) {
      LOG.debug(String.format("CustomerId %s rejectGift giftId %s", token.getAccountId(), giftId));
    }

    try {
      final UUID customerUuid = UUID.fromString(token.getAccountId());
      customerService.rejectGift(UUID.fromString(giftId), customerUuid);
    } catch (ServiceException e) {
      LOG.error(String.format("Problem rejectGift for customerId %s giftId %s", token.getAccountId(), giftId), e);
      throw new ServiceException_t(e.getErrorCode().getCode(), e.getMessage());
    } finally {
      endRequest();
    }

  }

  @Override
  public DealOffer_t getDealOffer(final String dealOfferId) throws ServiceException_t, TException {
    final Token_t token = TokenUtil.getTokenFromRequest(true);

    DealOffer dealOffer = null;
    DealOffer_t thriftDealOffer = null;

    beginRequest("getDealOffer");

    if (LOG.isDebugEnabled()) {
      LOG.debug(String.format("CustomerId %s getDealOffer dealOfferId %s", token.getAccountId(), dealOfferId));
    }

    try {
      dealOffer = taloolService.getDealOffer(UUID.fromString(dealOfferId));
      thriftDealOffer = ConversionUtil.convertToThrift(dealOffer);

      return thriftDealOffer;
    } catch (ServiceException e) {
      LOG.error(e.getMessage(), e);
      throw new ServiceException_t(e.getErrorCode().getCode(), e.getMessage());
    } finally {
      endRequest();
    }
  }

  @Override
  public List<Deal_t> getDealsByDealOfferId(final String dealOfferId, final SearchOptions_t searchOptions) throws ServiceException_t, TException {
    TokenUtil.getTokenFromRequest(true);

    List<Deal> deals = null;

    beginRequest("getDealsByDealOfferId");

    try {
      deals = taloolService.getDealsByDealOfferId(UUID.fromString(dealOfferId), ConversionUtil.convertFromThrift(searchOptions), true);

      return ConversionUtil.convertToThriftDeals(deals);
    } catch (ServiceException e) {
      LOG.error(e.getMessage(), e);
      throw new ServiceException_t(e.getErrorCode().getCode(), e.getMessage());
    } finally {
      endRequest();
    }
  }

  @Override
  public List<Activity_t> getActivities(final SearchOptions_t searchOptions) throws ServiceException_t, TException {
    final Token_t token = TokenUtil.getTokenFromRequest(true);

    try {
      final List<Activity> activities =
          activityService.getActivities(UUID.fromString(token.getAccountId()), ConversionUtil.convertFromThrift(searchOptions));

      return CollectionUtils.isEmpty(activities) ? EMPTY_ACTIVITIES : ConversionUtil.convertToThriftActivites(activities);
    } catch (ServiceException e) {
      throw new ServiceException_t(e.getErrorCode().getCode(), e.getMessage());
    }
  }

  @Override
  public void activityAction(final String activityId) throws ServiceException_t, TException {
    final Token_t token = TokenUtil.getTokenFromRequest(true);

    beginRequest("activityAction");

    if (LOG.isDebugEnabled()) {
      LOG.debug(String.format("CustomerId %s activityAction activityId %s", token.getAccountId(), activityId));
    }

    try {
      activityService.activityAction(UUID.fromString(activityId));
    } catch (ServiceException e) {
      LOG.error(e.getMessage(), e);
      throw new ServiceException_t(e.getErrorCode().getCode(), e.getMessage());
    } finally {
      endRequest();
    }

  }

  @Override
  public void activateCode(final String dealOfferid, final String code) throws ServiceException_t, TException {
    final Token_t token = TokenUtil.getTokenFromRequest(true);

    beginRequest("activateCode");

    if (LOG.isDebugEnabled()) {
      LOG.debug(String.format("CustomerId %s activateCode dealOfferId %s code %s", token.getAccountId(), dealOfferid, code));
    }

    if (dealOfferid == null || code == null)

    {
      throw new ServiceException_t(ErrorCode.UNKNOWN.getCode(), "dealOfferId and code cannot be null");
    }

    try {
      customerService.activateCode(UUID.fromString(token.getAccountId()), UUID.fromString(dealOfferid), code);
    } catch (ServiceException e) {
      LOG.error(e.getMessage(), e);
      throw new ServiceException_t(e.getErrorCode().getCode(), e.getMessage());
    } finally {
      endRequest();
    }

  }

  @Override
  public List<Merchant_t> getMerchantAcquiresWithLocation(final SearchOptions_t searchOptions, final Location_t location) throws ServiceException_t,
      TException {
    final Token_t token = TokenUtil.getTokenFromRequest(true);
    List<Merchant> merchants = null;
    List<Merchant_t> thriftMerchants = null;

    beginRequest("getMerchantAcquiresWithLocation");

    if (LOG.isDebugEnabled()) {
      if (searchOptions != null) {
        LOG.debug(String.format("CustomerId %s getMerchantAcquires with searchOptions %s", token.getAccountId(), searchOptions.toString()));
      } else {
        LOG.debug(String.format("CustomerId %s getMerchantAcquiress with no searchOptions", token.getAccountId()));
      }

    }

    try {
      setThreadLocalServiceHeaders(customerService);
      merchants =
          customerService.getMerchantAcquires(UUID.fromString(token.getAccountId()), ConversionUtil.convertFromThrift(searchOptions),
              ConversionUtil.convertFromThrift(location));

      thriftMerchants = ConversionUtil.convertToThriftMerchants(merchants);

      if (LOG.isDebugEnabled() && thriftMerchants != null) {
        long byteTotal = 0;
        for (Merchant_t merchant : thriftMerchants) {
          byteTotal += ThriftUtil.serialize(merchant, Constants.PROTOCOL_FACTORY).length;
        }

        LOG.debug(String.format("getMerchantAcquiresWithLocation - serializing %d merchants totalBytes %d (%s)", thriftMerchants.size(), byteTotal,
            FileUtils.byteCountToDisplaySize(byteTotal)));

      }

      return CollectionUtils.isEmpty(thriftMerchants) ? EMPTY_MERCHANTS : thriftMerchants;
    } catch (Exception ex) {
      LOG.error("Problem getMerchantAcquiresWithLocation for customer " + token.getAccountId(), ex);
      throw new ServiceException_t(1087, "Problem getting merchants");
    } finally {
      endRequest();
    }
  }

  @Override
  public void sendResetPasswordEmail(final String email) throws TServiceException_t, TUserException_t, TNotFoundException_t, TException {
    Customer customer = null;
    beginRequest("sendResetPasswordEmail");

    LOG.debug("sendResetPasswordEmail " + email);

    if (LOG.isDebugEnabled()) {
      LOG.debug(String.format("sendResetPasswordEmail email " + email));
    }

    try {
      setThreadLocalServiceHeaders(customerService);
      customer = customerService.getCustomerByEmail(email);
    } catch (ServiceException se) {
      LOG.error("Problem finding customer by email: " + email, se);
      throw new ServiceException_t(se.getErrorCode().getCode(), se.getMessage());
    } catch (InvalidInputException e) {
      LOG.warn("Invalid input on password reset " + email, e);
      throw ExceptionUtil.safelyTranslate(e);
    }

    if (customer == null) {
      throw new TNotFoundException_t("email", email);
    }

    try {
      if (customer.getSocialAccounts().get(taloolService.getSocialNetwork(SocialNetwork.NetworkName.Facebook)) != null) {
        throw new ServiceException_t(ErrorCode.UNKNOWN.getCode(), "Cannot change Facebook account passwords");
      }
    } catch (ServiceException se) {
      LOG.error("Problem checking for facebook account for user " + email, se);
      throw new ServiceException_t(se.getErrorCode().getCode(), se.getMessage());
    }

    try {
      customerService.createPasswordReset(customer);
    } catch (ServiceException se) {
      LOG.error("Problem generating password reset for user " + email, se);
      throw new ServiceException_t(se.getErrorCode().getCode(), se.getMessage());
    } finally {
      endRequest();
    }

  }

  @Override
  public CTokenAccess_t resetPassword(final String customerId, final String resetPasswordCode, final String newPassword) throws TServiceException_t,
      TUserException_t, TNotFoundException_t, TException {
    Customer customer = null;
    CTokenAccess_t token = null;

    beginRequest("resetPassword");

    if (LOG.isDebugEnabled()) {
      LOG.debug("resetPassword customerId " + customerId);
    }

    setThreadLocalServiceHeaders(customerService);

    if (StringUtils.isEmpty(newPassword)) {
      throw new TUserException_t(ErrorCode.PASS_REQUIRED.getCode());
    }

    if (StringUtils.isEmpty(resetPasswordCode)) {
      throw new TUserException_t(ErrorCode.PASS_RESET_CODE_REQUIRED.getCode());
    }

    try {
      customer = customerService.getCustomerById(UUID.fromString(customerId));
    } catch (ServiceException se) {
      LOG.error("Problem resetPassword for customerId " + customerId, se);
      throw ExceptionUtil.safelyTranslate(se);
    }

    if (customer != null) {
      if (!customer.getResetPasswordCode().equals(resetPasswordCode)) {
        throw new TUserException_t(ErrorCode.PASS_RESET_CODE_INVALID.getCode());
      }

      final Date now = Calendar.getInstance().getTime();
      if (now.getTime() > customer.getResetPasswordExpires().getTime()) {
        LOG.warn(String.format("reset pass expired customerId %s now %s expiresTime %s", customerId, now, customer.getResetPasswordExpires()));
        throw new TServiceException_t(ErrorCode.PASS_RESET_CODE_EXPIRED.getCode());
      }

      try { // encrypts and sets
        customer.setPassword(newPassword);
        customer.setResetPasswordCode(null);
        customerService.save(customer);
        token = TokenUtil.createTokenAccess(ConversionUtil.convertToThrift(customer));
      } catch (ServiceException se) {
        LOG.error("Problem setting new pass/saving customerId " + customerId, se);
        throw ExceptionUtil.safelyTranslate(se);
      } finally {
        endRequest();
      }
    } else {
      LOG.warn("Customer not found customerId:" + customerId);
      throw new TNotFoundException_t("customer", customerId);
    }

    return token;

  }

  /**
   * Gateway purchase by credit card
   * 
   * @deprecated replaced by y {@link #buyWithCard()}
   */
  @Override
  public TransactionResult_t purchaseByCard(final String dealOfferId, final PaymentDetail_t paymentDetail) throws TServiceException_t,
      TUserException_t, TNotFoundException_t, TException {
    return doPurchaseByCard(dealOfferId, paymentDetail, null);
  }

  private TransactionResult_t doPurchaseByCard(final String dealOfferId, final PaymentDetail_t paymentDetail,
      final Map<String, String> paymentProperties) throws TServiceException_t, TUserException_t, TNotFoundException_t, TException {
    final Token_t token = TokenUtil.getTokenFromRequest(true);

    TransactionResult_t transactionResult_t = null;

    beginRequest("purchaseByCard");

    if (LOG.isDebugEnabled()) {
      LOG.debug(String.format("purchaseByCard customerId %s dealOfferId %s", token.getAccountId(), dealOfferId));
      LOG.debug(BraintreeUtil.get().getDebugString());
    }

    try {
      setThreadLocalServiceHeaders(customerService);
      final TransactionResult transactionResult =
          customerService.purchaseByCard(UUID.fromString(token.getAccountId()), UUID.fromString(dealOfferId),
              ConversionUtil.convertFromThrift(paymentDetail), paymentProperties);
      transactionResult_t = ConversionUtil.convertToThrift(transactionResult);

    } catch (ServiceException e) {
      LOG.error("Problem purchaseByCard: " + e.getLocalizedMessage(), e);
      throw ExceptionUtil.safelyTranslate(e);
    } catch (NotFoundException e) {
      LOG.error("Problem purchaseByCard: " + e.getLocalizedMessage(), e);
      throw ExceptionUtil.safelyTranslate(e);
    } finally {
      endRequest();
    }

    return transactionResult_t;

  }

  /**
   * Gateway purchase by gateway code
   * 
   * @deprecated replaced by y {@link #buyWithCode()}
   */
  @Override
  public TransactionResult_t purchaseByCode(final String dealOfferId, final String paymentCode) throws TServiceException_t, TUserException_t,
      TNotFoundException_t, TException {
    return doPurchaseByCode(dealOfferId, paymentCode, null);
  }

  protected TransactionResult_t doPurchaseByCode(final String dealOfferId, final String paymentCode, final Map<String, String> paymentProperties)
      throws TServiceException_t, TUserException_t, TNotFoundException_t, TException {
    final Token_t token = TokenUtil.getTokenFromRequest(true);
    TransactionResult_t transactionResult_t = null;

    beginRequest("purchaseByCode");

    if (LOG.isDebugEnabled()) {
      LOG.debug(String.format("purchaseByCode customerId %s dealOfferId %s", token.getAccountId(), dealOfferId));
    }

    try {
      setThreadLocalServiceHeaders(customerService);
      final TransactionResult transactionResult =
          customerService.purchaseByCode(UUID.fromString(token.getAccountId()), UUID.fromString(dealOfferId), paymentCode, paymentProperties);
      transactionResult_t = ConversionUtil.convertToThrift(transactionResult);
    } catch (ServiceException e) {
      LOG.error("Problem purchaseByCode: " + e.getLocalizedMessage(), e);
      throw ExceptionUtil.safelyTranslate(e);
    } catch (NotFoundException e) {
      LOG.error("Problem purchaseByCode: " + e.getLocalizedMessage(), e);
      throw ExceptionUtil.safelyTranslate(e);
    } finally {
      endRequest();
    }

    return transactionResult_t;

  }

  @Override
  public CTokenAccessResponse_t loginFacebook(final String facebookId, final String facebookTokenAccess) throws TServiceException_t, TException {
    beginRequest("loginFacebook");

    if (StringUtils.isEmpty(facebookId)) {
      LOG.warn("facebookId is null");
      return EMPTY_TOKEN_ACCESS_RESPONSE;
    }

    if (LOG.isDebugEnabled()) {
      LOG.debug(String.format("loginFacebook facebookId %s", facebookId));
    }

    try {
      setThreadLocalServiceHeaders(customerService);
      final Customer customer = customerService.getCustomerBySocialLoginId(facebookId);

      if (customer != null) {
        final CTokenAccessResponse_t tokenAccessResponse = new CTokenAccessResponse_t();
        final Customer_t customer_t = ConversionUtil.convertToThrift(customer);
        tokenAccessResponse.setTokenAccess(TokenUtil.createTokenAccess(customer_t));
        return tokenAccessResponse;
      }

    } catch (ServiceException e) {
      LOG.error("Problem loginFacebook: " + e.getLocalizedMessage(), e);
      throw ExceptionUtil.safelyTranslate(e);
    } finally {
      endRequest();
    }

    return EMPTY_TOKEN_ACCESS_RESPONSE;
  }

  @Override
  public DealOfferGeoSummariesResponse_t getDealOfferGeoSummariesWithin(final Location_t location, final int maxMiles,
      final SearchOptions_t searchOptions, final SearchOptions_t fallbackSearchOptions) throws TServiceException_t, TException {
    final Token_t token = TokenUtil.getTokenFromRequest(true);
    DealOfferGeoSummariesResponse_t response = null;
    DealOfferGeoSummariesResult result = null;
    UUID whiteLabelPublisherMerchantId = null;

    // supports free books?
    final boolean supportsFreeBooks = RequestUtils.getRequest().getHeader(Constants.HEADER_X_SUPPORTS_FREE_BOOKS) != null;
    // white label header?
    final String whiteLabelIdHeader = RequestUtils.getRequest().getHeader(Constants.HEADER_X_WHITE_LABEL_ID);

    if (StringUtils.isNotEmpty(whiteLabelIdHeader)) {
      whiteLabelPublisherMerchantId = UUID.fromString(whiteLabelIdHeader);
    }


    beginRequest("getDealOfferGeoSummariesWithin");

    if (LOG.isDebugEnabled()) {
      LOG.debug(String.format(
          "getDealOfferGeoSummariesWithin email: %s location: %s maxMiles: %d searchOpts: %s fallbackSeachOpts: %s whiteLabelId: %s", token
              .getEmail(), location == null ? null : location.toString(), maxMiles, searchOptions == null ? null : searchOptions.toString(),
          fallbackSearchOptions == null ? null : fallbackSearchOptions.toString(), whiteLabelPublisherMerchantId == null ? "null"
              : whiteLabelPublisherMerchantId.toString()));
    }

    try {
      setThreadLocalServiceHeaders(taloolService);

      result =
          taloolService.getDealOfferGeoSummariesWithin(ConversionUtil.convertFromThrift(location), maxMiles,
              ConversionUtil.convertFromThrift(searchOptions), ConversionUtil.convertFromThrift(fallbackSearchOptions), supportsFreeBooks,
              whiteLabelPublisherMerchantId);

      if (result != null && CollectionUtils.isNotEmpty(result.getSummaries())) {
        final List<DealOfferGeoSummary_t> dealOfferGeoSummaries_t = ConversionUtil.convertToThriftDealOfferGeoSummaries(result.getSummaries());
        response = new DealOfferGeoSummariesResponse_t();
        response.setDealOfferGeoSummaries(dealOfferGeoSummaries_t);
      }
    } catch (ServiceException e) {
      LOG.error("Problem getDealOfferGeoSummariesWithin: " + e.getLocalizedMessage(), e);
      throw ExceptionUtil.safelyTranslate(e);
    } finally {
      endRequest();
    }

    if (response != null) {
      response.setFallbackResponse(result.usedFallback());
      return response;
    } else {
      return EMPTY_DEAL_OFFER_GEO_SUMMARIES_RESPONSE;
    }
  }

  @Override
  public MerchantsResponse_t getMerchantsByDealOfferId(final String dealOfferId, final SearchOptions_t searchOptions) throws ServiceException_t,
      TException {
    final Token_t token = TokenUtil.getTokenFromRequest(true);
    final MerchantsResponse_t response = new MerchantsResponse_t();

    beginRequest("getMerchantsByDealOfferId");

    if (LOG.isDebugEnabled()) {
      LOG.debug(String.format("getMerchantsByDealOfferId email: %s", token.getEmail()));
    }

    try {
      setThreadLocalServiceHeaders(taloolService);
      final List<Merchant> merchants =
          taloolService.getMerchantsByDealOfferId(UUID.fromString(dealOfferId), ConversionUtil.convertFromThrift(searchOptions));

      if (CollectionUtils.isNotEmpty(merchants)) {
        response.setMerchants(ConversionUtil.convertToThriftMerchants(merchants));
      }

    } catch (ServiceException e) {
      LOG.error("Problem getMerchantsByDealOfferId: " + e.getLocalizedMessage(), e);
      throw ExceptionUtil.safelyTranslate(e);
    } finally {
      endRequest();
    }

    return response;
  }

  /**
   * This is just a stub method for now logging headers. Fill in with Iris service
   */
  @Override
  public List<Activity_t> getMessages(final SearchOptions_t searchOptions, final Location_t location) throws ServiceException_t, TException {
    final Token_t token = TokenUtil.getTokenFromRequest(true);
    List<Activity_t> acts = null;

    beginRequest("getMessages");

    try {
      final UUID customerId = UUID.fromString(token.getAccountId());
      if (LOG.isDebugEnabled()) {
        LOG.debug("getMessages called: " + token.getEmail());
      }
      final HttpServletRequest request = RequestUtils.getRequest();
      updateDevicePresence(customerId, request, location);
    } catch (Exception e) {
      LOG.error("Problem getMessages: " + token.getEmail() + " " + e.getLocalizedMessage(), e);
    }

    try {
      acts = getActivities(searchOptions);
    } catch (Exception e) {
      LOG.error("Problem getMessages: " + e.getLocalizedMessage(), e);
    } finally {
      endRequest();
    }


    return acts == null ? EMPTY_ACTIVITIES : acts;
  }

  /**
   * Creates an updates the devicePresence derived from the HTTP headers and client IP address
   * 
   * @param request
   * @return
   */
  DevicePresence updateDevicePresence(final UUID customerId, final HttpServletRequest request, final Location_t location) {
    final String apnDeviceToken = request.getHeader(Constants.HEADER_APN_DEVICE_TOKEN);
    final String deviceId = request.getHeader(Constants.HEADER_DEVICE_ID);
    final String gcmDeviceToken = request.getHeader(Constants.HEADER_GCM_DEVICE_TOKEN);
    final String userAgent = request.getHeader(Constants.HEADER_USER_AGENT);

    final String clientIp = request.getHeader(Constants.HEADER_X_CLIENT);

    final DevicePresence devicePresence = new DevicePresenceImpl();
    devicePresence.setLocation(FactoryManager.get().getDomainFactory().newPoint(ConversionUtil.convertFromThrift(location)));
    devicePresence.setUserAgent(userAgent);
    devicePresence.setDeviceId(deviceId);
    devicePresence.setCustomerId(customerId);
    devicePresence.setIp(clientIp);

    if (apnDeviceToken != null) {
      devicePresence.setDeviceToken(apnDeviceToken);
    } else if (gcmDeviceToken != null) {
      devicePresence.setDeviceToken(gcmDeviceToken);
    }

    // non-blocking
    DevicePresenceManager.get().updateDevicePresence(devicePresence);

    if (LOG.isDebugEnabled()) {
      dumpHttpHeadersToLog();
    }

    return devicePresence;

  }

  @SuppressWarnings("rawtypes")
  void dumpHttpHeadersToLog() {
    final HttpServletRequest request = RequestUtils.getRequest();
    final StringBuilder sb = new StringBuilder();
    sb.append("headers:");
    Enumeration headerNames = request.getHeaderNames();
    while (headerNames.hasMoreElements()) {
      String headerName = (String) headerNames.nextElement();
      sb.append(" ").append(headerName).append("->").append(request.getHeader(headerName));
    }
    LOG.debug(sb.toString());
  }

  @Override
  /**
   * Validate a code with a dealOfferId.  Algorithm to validate a code:
   * 
   * First try and validate as a merchant code.  If that is successful return the response. 
   * If it is unsuccessful, validate against activation codes (book codes).
   */
  public ValidateCodeResponse_t validateCode(final String code, final String dealOfferId) throws TServiceException_t, TException {
    TokenUtil.getTokenFromRequest(true);
    final ValidateCodeResponse_t response = new ValidateCodeResponse_t();
    final UUID dealOfferUuid = UUID.fromString(dealOfferId);
    boolean isValid = false;

    beginRequest("validateCode");

    if (LOG.isDebugEnabled()) {
      LOG.debug(String.format("validateCode code: %s dealOfferId %s", code, dealOfferId));
    }

    try {
      isValid = taloolService.isMerchantCodeValid(code, dealOfferUuid);
      if (isValid) {
        response.setCodeType(PropertyConstants.MERCHANT_CODE);
      } else {
        // validate code against activation codes (a.k.a printed book codes)
        isValid = customerService.isActivationCodeValid(code, dealOfferUuid);
        if (isValid) {
          response.setCodeType(PropertyConstants.ACTIVATION_CODE);
        }
      }

    } catch (ServiceException e) {
      LOG.error("Problem getMerchantsByDealOfferId: " + e.getLocalizedMessage(), e);
      throw ExceptionUtil.safelyTranslate(e);
    } finally {
      endRequest();
    }

    response.setValid(isValid);
    return response;

  }

  @Override
  public TransactionResult_t purchaseWithCard(final String dealOfferId, final PaymentDetail_t paymentDetail,
      final Map<String, String> paymentProperties) throws TServiceException_t, TUserException_t, TNotFoundException_t, TException {
    return doPurchaseByCard(dealOfferId, paymentDetail, paymentProperties);
  }

  @Override
  public TransactionResult_t purchaseWithCode(final String dealOfferId, final String paymentCode, final Map<String, String> paymentProperties)
      throws TServiceException_t, TUserException_t, TNotFoundException_t, TException {
    return doPurchaseByCode(dealOfferId, paymentCode, paymentProperties);
  }

  @Override
  public EmailMessageResponse_t getEmailMessage(final String templateId, final String entityId) throws TServiceException_t, TException {
    TokenUtil.getTokenFromRequest(true);
    final EmailMessageResponse_t response = new EmailMessageResponse_t();

    beginRequest("validateCode");

    if (LOG.isDebugEnabled()) {
      LOG.debug(String.format("getEmailBody templateId: %s entityId %s", entityId, entityId));
    }

    try {

      final ActivityEmailTemplateType type = ActivityEmailTemplateType.getByTemplateId(Integer.valueOf(templateId));

      switch (type) {
        case BasicFundRaiser:

          final DealOfferPurchase dop = taloolService.getDealOfferPurchase(UUID.fromString(entityId));
          final String merchantCode = dop.getPropertyValue(KeyValue.merchantCode);
          final Merchant fundraiser = taloolService.getFundraiserByTrackingCode(merchantCode);
          final MerchantCodeGroup group = taloolService.getMerchantCodeGroupForCode(merchantCode);
          final String student = group.getCodeGroupTitle();
          final EmailMessage message = FreemarkerUtil.get().renderFundraiserEmail(dop, fundraiser, merchantCode, student);

          response.setSubject(message.getSubject());
          response.setBody(message.getBody());

          break;

        case Unknown:
          LOG.error("Email template is unknown. Will not render on client until default template is installed!");

      }
    } catch (Exception e) {
      LOG.error("Problem getEmailBody: " + e.getLocalizedMessage(), e);
      // TODO we need to default to a template rather than thrown an exception
      // to client. Lets swallow exception for now
    } finally {
      endRequest();
    }

    return response;
  }

  @Override
  public TransactionResult_t purchaseWithNonce(String dealOfferId, String nonce, Map<String, String> paymentProperties) throws TServiceException_t,
      TUserException_t, TNotFoundException_t, TException {
    final Token_t token = TokenUtil.getTokenFromRequest(true);
    TransactionResult_t transactionResult_t = null;

    beginRequest("purchaseWithNonce");

    if (LOG.isDebugEnabled()) {
      LOG.debug(String.format("purchaseWithNonce customerId %s dealOfferId %s", token.getAccountId(), dealOfferId));
      dumpHttpHeadersToLog();
    }

    try {
      setThreadLocalServiceHeaders(customerService);

      final TransactionResult transactionResult =
          customerService.purchaseByNonce(UUID.fromString(token.getAccountId()), UUID.fromString(dealOfferId), nonce, paymentProperties);
      transactionResult_t = ConversionUtil.convertToThrift(transactionResult);
    } catch (ServiceException e) {
      LOG.error("Problem purchaseWithNonce: " + e.getLocalizedMessage(), e);
      throw ExceptionUtil.safelyTranslate(e);
    } catch (Exception e) {
      LOG.error("Problem purchaseWithNonce: " + e.getLocalizedMessage(), e);
    } finally {
      endRequest();
    }

    return transactionResult_t;
  }

  @Override
  public String generateBraintreeClientToken() throws ServiceException_t, TException {

    final Token_t token = TokenUtil.getTokenFromRequest(true);
    String btToken;

    beginRequest("generateBraintreeClientToken");

    try {
      btToken = customerService.generateBraintreeClientToken(UUID.fromString(token.getAccountId()));
    } catch (ServiceException e) {
      LOG.error("Problem generateBraintreeClientToken for customerId: " + token.getAccountId(), e);
      throw new ServiceException_t(e.getErrorCode().getCode(), e.getMessage());
    } catch (NotFoundException e) {
      LOG.error("Problem generateBraintreeClientToken: " + e.getLocalizedMessage(), e);
      throw ExceptionUtil.safelyTranslate(e);
    } finally {
      endRequest();
    }

    return btToken;
  }
}
