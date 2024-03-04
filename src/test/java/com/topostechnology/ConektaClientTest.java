package com.topostechnology;

import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.topostechnology.rest.client.ConektaClient;

import io.conekta.Conekta;
import io.conekta.Customer;
import io.conekta.Error;
import io.conekta.ErrorList;
import io.conekta.Subscription;

public class ConektaClientTest  extends TurboConsumoWebApplicationTests {

	@Autowired
	private ConektaClient conektaClient;
	
//	@Test
//	public void cancelSubscription() throws Error, ErrorList {
//		String cellphoneNumber = "525623518622";
//		conektaClient.cancelSubscription(cellphoneNumber);
//	}
//	
//	@Test
//	public void findSubscription() throws Error, ErrorList {
//		String cellphoneNumber = "525623518622";
//		Subscription subscription = conektaClient.findSubscription(cellphoneNumber);
//		System.out.println(subscription.status);
//	}
	
//	@Test
//	public void findSubscription() throws Error, ErrorList {
//		String cellphoneNumber = "525525611015";
//		Subscription subscription = conektaClient.findSubscription(cellphoneNumber);
//		System.out.println(subscription.status);
//	}
//	
//	@Test
//	public void cancelSubscription() throws Error, ErrorList {
//		String cellphoneNumber = "525525611015";
//		conektaClient.cancelSubscription(cellphoneNumber);
//	}
	
	
//	@Test
//	public void findSubscription() throws Error, ErrorList {
//		String cellphoneNumber = "523335715436";
//		Subscription subscription = conektaClient.findSubscription(cellphoneNumber);
//		System.out.println(subscription.status);
//	}
//	
//	@Test
//	public void cancelSubscription() throws Error, ErrorList {
//		String cellphoneNumber = "523335715436";
//		conektaClient.cancelSubscription(cellphoneNumber);
//	}
	
//	@Test
//	public void findSubscription() throws Error, ErrorList {
//		String cellphoneNumber = "525514404272";
//		Subscription subscription = conektaClient.findSubscription(cellphoneNumber);
//		System.out.println(subscription.status);
//	}
//	
//	@Test
//	public void cancelSubscription() throws Error, ErrorList {
//		String cellphoneNumber = "525514404272";
//		conektaClient.cancelSubscription(cellphoneNumber);
//	}
	
//	@Test
//	public void findSubscription() throws Error, ErrorList {
//		String cellphoneNumber = "525543691924";
//		Subscription subscription = conektaClient.findSubscription(cellphoneNumber);
//		System.out.println( new Date() + " " + subscription.status);
//	}
//	
//	@Test
//	public void cancelSubscription() throws Error, ErrorList {
//		String cellphoneNumber = "525543691924";
//		conektaClient.cancelSubscription(cellphoneNumber);
//	}
	
//	@Test
//	public void findSubscription() throws Error, ErrorList {
//		String cellphoneNumber = "525528504606";
//		Subscription subscription = conektaClient.findSubscription(cellphoneNumber);
//		System.out.println( new Date() + " " + subscription.status);
//	}
	
//	@Test
//	public void findS() throws JSONException, Error, ErrorList {
//		Customer customer = null;
//		Conekta.setApiKey("key_g6NYaEeMv9L3AKGpGDND8Q");
//		Conekta.setApiVerion("1.0.0");
//		List<Customer> customers = Customer.where(new JSONObject("{'id':'" + "cus_2r3ngv99L8Bf5UB6w" + "'}"));
//		if (!customers.isEmpty()) {
//			customer = customers.get(0);
//			System.out.println(customer.name);
//			Subscription subscription = customer.subscription;
//			System.out.println(subscription.status);
//			
//		}
//		
//	}
	
//	@Test
//	public void findS2() throws JSONException, Error, ErrorList {
//		Customer customer = null;
//		Conekta.setApiKey("key_g6NYaEeMv9L3AKGpGDND8Q");
//		Conekta.setApiVerion("1.0.0");
//		List<Customer> customers = Customer.where(new JSONObject("{'phone':'" + "525528504606" + "'}"));
//		if (!customers.isEmpty()) {
//			customer = customers.get(0);
//			System.out.println(customer.name);
//			Subscription subscription = customer.subscription;
//			System.out.println(subscription.status);
//			
//		}
//		
//	}
	
	
//	@Test
//	public void findSubscription() throws Error, ErrorList {
//		String cellphoneNumber = "525554525565";
//		Subscription subscription = conektaClient.findSubscription(cellphoneNumber);
//		System.out.println( new Date() + " " + subscription.status);
//	}
//	
	
//	@Test
//	public void cancelSubscription() throws Error, ErrorList {
//		String cellphoneNumber = "525554525565";
//		conektaClient.cancelSubscription(cellphoneNumber);
//	}
	


}
