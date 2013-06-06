########################################################
#
# Talool Core API Objects
#
########################################################

namespace java com.talool.api.thrift

typedef i64 Timestamp

enum Sex_t { M,F,U }

enum SocialNetwork_t { Facebook, Twitter, Pinterest }

enum DealType_t { PAID_BOOK, FREE_BOOK, PAID_DEAL, FREE_DEAL }

exception ServiceException_t {
  1: required i32 errorCode,
  2: required string errorDesc
}

struct Category_t {
  1: required i32 categoryId;
  2: required string name;
}

struct Location_t {
  1: required double longitude;
  2: required double latitude;
}

struct SocialNetworkDetail_t {
  1: required SocialNetwork_t socalNetwork;
  2: required string name;
  3: required string website;
  4: optional string apiUrl;
}

struct SocialAccount_t {
  1: required SocialNetwork_t socialNetwork;
  2: required string loginId;
  20: optional Timestamp created;
}

struct Address_t {
  1: required string address1;
  2: optional string address2;
  3: required string city;
  4: required string stateProvinceCounty;
  6: optional string zip;
  7: optional string country;
}

struct MerchantLocation_t {
  1: optional i64 locationId;
  2: optional string name;
  3: optional string email;
  4: optional string websiteUrl;
  5: optional string logoUrl;
  6: optional string merchantImageUrl;
  7: optional string phone;
  8: optional Location_t location;
  9: required Address_t address;
  10: optional double distanceInMeters;
}

struct Customer_t {
  1: optional string customerId;
  2: required string firstName;
  3: required string lastName;
  4: required string email;
  6: optional Sex_t sex;
  7: optional Timestamp birthDate;
  8: optional map<SocialNetwork_t,SocialAccount_t> socialAccounts;
  9: optional Timestamp created;
  10: optional Timestamp updated;
}

struct Token_t {
 1: required string accountId;
 2: required string email;
 3: required Timestamp expires;
}

struct Merchant_t {
  1: optional string merchantId;
  2: required string name;
  3: optional Category_t category;
  4: optional list<MerchantLocation_t> locations;
}

struct Deal_t {
  1: required string dealId;
  2: required Merchant_t merchant;
  3: optional string dealOfferId;
  4: required string title;
  5: required string summary;
  6: optional string details;
  7: optional string code;
  8: optional string imageUrl;
  9: optional Timestamp expires;
  10: optional Timestamp created;
  11: optional Timestamp updated;
}

struct DealOffer_t {
  1: required string dealOfferId;
  2: required Merchant_t merchant;
  3: required DealType_t dealType;
  4: required string title;
  5: optional string summary;
  6: optional string code;
  7: optional string imageUrl;
  8: optional double price;
  9: optional Timestamp expires;
}

struct SearchOptions_t {
  1: required bool ascending;
  2: required string sortProperty;
  3: required i32 maxResults;
  4: required i32 page;
}

struct DealAcquire_t {
  1: optional string dealAcquireId;
  2: required Deal_t deal;
  3: optional string status; 
  4: optional Customer_t sharedByCustomer;
  5: optional i32 shareCount;
  6: optional Timestamp redeemed;
  7: optional Timestamp created;
  8: optional Timestamp updated;
}

struct Gift_t {
  1: required string giftId;
  2: required Deal_t deal;
  3: required Customer_t fromCustomer; 
  4: required Timestamp created;
}



