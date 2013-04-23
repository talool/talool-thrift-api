########################################################
#
# Talool Core API Objects
#
########################################################

namespace java com.talool.api.thrift

typedef i64 Timestamp

enum Sex_t { M,F,U }

enum SocialNetwork_t { Facebook, Twitter, Pinterest }

exception ServiceException_t {
  1: required i32 errorCode,
  2: required string errorDesc
}

struct SocialNetworkDetail_t {
  1: required SocialNetwork_t socalNetwork;
  2: required string name;
  3: required string website;
  4: optional string apiUrl;
}

struct SocialAccount_t {
  1: required SocialNetwork_t socalNetwork;
  2: required string loginId;
  20: optional Timestamp created;
  21: optional Timestamp updated;
}


struct Address_t {
  1: optional i64 addressId;
  2: required string address1;
  3: optional string address2;
  4: optional string city;
  5: optional string stateProvinceCounty;
  6: optional string zip;
  7: optional string country;
}

struct MerchantLocation_t {
  1: optional i64 locationId;
  2: optional string name;
  3: optional string email;
  4: optional string websiteUrl;
  5: optional string logUrl;
  6: optional string phone;
  7: required Address_t address;
}

struct Customer_t {
  1: optional string customerId;
  2: required string firstName;
  3: required string lastName;
  4: required string email;
  6: optional Sex_t sex;
  7: optional map<SocialNetwork_t,SocialAccount_t> socialAccounts;
  8: optional Timestamp created;
  9: optional Timestamp updated;
}

struct Token_t {
 1: required string accountId;
 2: required string email;
 3: required i64 expires;
}

struct Merchant_t {
  1: optional string merchantId;
  2: required string name;
  3: optional list<MerchantLocation_t> locations;
  4: optional Timestamp created;
  5: optional Timestamp updated;
}

struct Deal_t {
  1: optional string dealId;
  2: required Merchant_t merchant;
  3: required string title;
  4: optional string summary;
  5: optional string details;
  6: optional string code;
  7: optional string imageUrl;
  8: optional Timestamp expires;
  9: optional Timestamp created;
  10: optional Timestamp updated;
}

struct SearchOptions_t {
  1: required bool ascending;
  2: required string sortProperty;
  3: required i32 firstResult;
  4: required i32 maxResults;
  5: required i32 page;
}

struct DealAcquire_t {
  1: optional string dealAcquireId;
  2: required Deal_t deal;
  3: optional string status; 
  4: optional Merchant_t sharedByMerchant;
  5: optional Customer_t sharedByCustomer;
  6: optional i32 shareCount;
  7: optional Timestamp redeemed;
  8: optional Timestamp created;
  9: optional Timestamp updated;
}


