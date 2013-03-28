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
  3: optional string token;
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

struct Customer_t {
  1: optional i64 customerId;
  2: required string firstName;
  3: required string lastName;
  4: required string email;
  6: optional Sex_t sex;
  7: optional map<SocialNetwork_t,SocialAccount_t> socialAccounts;
  20: optional Timestamp created;
  21: optional Timestamp updated;
}

struct Token_t {
 1: required string accountId;
 2: required string email;
 3: required i64 expires;
}

struct Merchant_t {
  1: optional i64 merchantId;
  2: required string name;
  3: optional string email;
  4: optional string websiteUrl;
  5: optional string logoUrl;
  6: optional string phone;
  7: optional Address_t address;
  20: optional Timestamp created;
  21: optional Timestamp updated;
}

struct Deal_t {
  1: optional i64 dealId;
  2: required Merchant_t merchant;
  3: required string title;
  4: optional string summary;
  5: optional string details;
  6: optional string code;
  7: optional string imageUrl;
  8: optional Timestamp expires;
  20: optional Timestamp created;
  21: optional Timestamp updated;
}

