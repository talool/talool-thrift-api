########################################################
#
# Talool Customer Service API
#
########################################################
namespace java com.talool.api.thrift

include "Core.thrift"

const string CTOKEN_NAME = 'ctok'

struct CTokenAccess_t {
  1: required Core.Customer_t customer,
  2: required string token;
}

service CustomerService_t {

   CTokenAccess_t createAccount(1:Core.Customer_t customer,2:string password) throws (1:Core.ServiceException_t error);
  
   CTokenAccess_t authenticate(1:string email,2:string password) throws (1:Core.ServiceException_t error);
   
   bool customerEmailExists(1:string email) throws (1:Core.ServiceException_t error);
   
   void save(1:Core.Customer_t customer) throws (1:Core.ServiceException_t error);
     
   void addSocialAccount(1:Core.SocialAccount_t socialAccount) throws (1:Core.ServiceException_t error);
   
   list<Core.Merchant_t> getMerchants() throws (1:Core.ServiceException_t error);
      
   list<Core.Deal_t> getDeals(1:i64 merchantId) throws (1:Core.ServiceException_t error);
   


}