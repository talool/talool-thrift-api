########################################################
#
# Talool Merchant Service API
#
########################################################
namespace java com.talool.thrift

include "core.thrift"
 
service MerchantService_t {
   void registerMerchant(1:TCustomer customer,2:string password) throws (1:TServiceException error);
   TCustomer authCustomer(1:string email,2:string password) throws (1:TServiceException error);
}