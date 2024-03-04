package com.topostechnology;

import java.math.BigDecimal;
import java.util.Calendar;

import org.junit.Test;

import mx.openpay.client.Charge;
import mx.openpay.client.Customer;
import mx.openpay.client.core.OpenpayAPI;
import mx.openpay.client.core.requests.transactions.CreateStoreChargeParams;
import mx.openpay.client.exceptions.OpenpayServiceException;
import mx.openpay.client.exceptions.ServiceUnavailableException;
public class OpenPayTest {
	
//	 String merchantId = "mtfsdeoulmcoj0xofpfc";
//     String apiKey = "sk_4ec3ef18cd01471487ca719f566d4d3f";
	
//	final OpenpayAPI api = new OpenpayAPI("https://sandbox-api.openpay.mx", "mxtwicmcqlvkaf6rqzx0", "sk_58b0d531afec4c1eab7a3543379a065d");
	
	 String merchantId = "mxtwicmcqlvkaf6rqzx0";
     String apiKey = "sk_58b0d531afec4c1eab7a3543379a065d";
     
     @SuppressWarnings("deprecation")
 	@Test
 	public void test() { // TODO PROBAR
 		 OpenpayAPI api = new OpenpayAPI("https://sandbox-api.openpay.mx", apiKey, merchantId);
 		 
 		 BigDecimal amount = new BigDecimal("10.00");
 	        String desc = "Pago test3";
 	        String orderId = String.valueOf(System.currentTimeMillis());
 	        try {
 				Charge transaction = api.charges().create(
 				        new CreateStoreChargeParams().amount(amount).description(desc)
 				        .orderId(orderId).customer(new Customer().name("Test03").email("v@comerce.com").phoneNumber("154234623")));
 				System.out.println(transaction.getId());
 				System.out.println(transaction.getOrderId());
 				System.out.println(transaction.getPaymentMethod().getReference());
 				System.out.println(transaction.getPaymentMethod().getBarcodePaybinUrl());
 				System.out.println(transaction.getPaymentMethod().getPaymentAddress());
 			} catch (OpenpayServiceException e) {
 				// TODO Auto-generated catch block
 				e.printStackTrace();
 			} catch (ServiceUnavailableException e) {
 				// TODO Auto-generated catch block
 				e.printStackTrace();
 			}
 		
 	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void test1() {
		 OpenpayAPI api = new OpenpayAPI("https://sandbox-api.openpay.mx", apiKey, merchantId);
		 
		 BigDecimal amount = new BigDecimal("10.00");
	        String desc = "Pago test3";
	        String orderId = String.valueOf(System.currentTimeMillis());
	        try {
				Charge transaction = api.charges().create(
				        new CreateStoreChargeParams().amount(amount).description(desc)
				        .orderId(orderId).customer(new Customer().name("Test03").email("v@comerce.com").phoneNumber("154234623")));
				System.out.println(transaction.getId());
				System.out.println(transaction.getOrderId());
				System.out.println(transaction.getPaymentMethod().getReference());
				System.out.println(transaction.getPaymentMethod().getBarcodePaybinUrl());
				System.out.println(transaction.getPaymentMethod().getPaymentAddress());
			} catch (OpenpayServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ServiceUnavailableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}
	
	@Test
	public void test0() {
		 OpenpayAPI api = new OpenpayAPI("https://sandbox-api.openpay.mx", apiKey, merchantId);
		 
		 Calendar dueDate = Calendar.getInstance();
		 dueDate.set(2014, 5, 28, 13, 45, 0);
		 CreateStoreChargeParams request = new CreateStoreChargeParams();
		 request.amount(new BigDecimal("1.00"));
		 request.description("Cargo con tienda");
		 request.orderId("oid-test0001");
		 request.dueDate(dueDate.getTime());

		 try {
			Charge charge = api.charges().create("ag4nktpdzebjiye1tlze", request);
			System.out.println("Charge: " + charge.getId());
		} catch (OpenpayServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServiceUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


}
