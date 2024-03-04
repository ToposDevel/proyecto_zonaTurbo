package com.topostechnology.rest.client;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.topostechnology.constant.PaymentConstants;
import com.topostechnology.utils.StringUtils;

import io.conekta.Conekta;
import io.conekta.Customer;
import io.conekta.Error;
import io.conekta.ErrorList;
import io.conekta.Order;
import io.conekta.Plan;
import io.conekta.Subscription;

@Service
public class ConektaClient {
	
	private static final Logger logger = LoggerFactory.getLogger(ConektaClient.class);
	
	@Value("${conekta.private.api.key}")
	private String conektaPrivateApiKey;
	
	@Value("${conekta.public.api.key}")
	private String conektaPublicApiKey;
	
	@Value("${conekta.api.version.1}")
	private String conektaApiVersionOne;
	
	@Value("${conekta.api.version.2}")
	private String conektaApiVersionTwo;
	
	@Value("${conekta.oxxo.expiresat}")
	private int referenceOxxoExpiresat;
	
	@Value("${turbored.address}")
	private String turboredAddress;
	
	@Value("${turbored.zipcode}")
	private String turboredZipcode;
	
	@Value("${turbored.country.code}")
	private String turboredCountryCode;
	
	@Value("${turbored.default.email}")
	private String turboredDefaultEmail;
	
//	@Autowired
//	private ConfigParameterService configParameterService;
	
	
	public ConektaClient() {
	}
	
	@SuppressWarnings("unchecked")
	public List<Plan> getPlans() throws Error, ErrorList {
		logger.error("Consultando todos los planes en conekta");
		Conekta.setApiKey(this.getPrivateApiKey());
		Conekta.setApiVerion(conektaApiVersionOne);
		List<Plan> plans = Plan.where();
		Conekta.setApiVerion(conektaApiVersionTwo);
		return plans;
	}
	
	public Plan getPlanById(String id) throws Error, ErrorList {
		logger.error("Consultando planes en conekta por id");
		Conekta.setApiKey(this.getPrivateApiKey());
		Conekta.setApiVerion(conektaApiVersionTwo);
		Plan plan = Plan.find(id);
		return plan;
	}
	
	public Subscription makeSubscription(JSONObject customerJSONObject, String planId) throws Exception {
		Customer customer = this.createCustomer(customerJSONObject);
		Subscription subscription = this.createSubscription(customer, planId);
		return subscription;
	}
	
	public Customer createCustomer(JSONObject jSONObject) throws Error, ErrorList {
		logger.error("Creando cliente en conekta");
		Conekta.setApiKey(this.getPrivateApiKey());
		Conekta.setApiVerion(conektaApiVersionTwo);
		Customer customer = Customer.create(jSONObject );
		logger.error("Cliente registrado en conekta " + customer.getId());
		return customer;
	}
	
	public Subscription createSubscription(Customer customer, String planId) throws Exception {
		logger.error("Creando suscripción en conekta");
		Subscription subscription = customer.createSubscription(
		        new JSONObject("{'plan':'"+planId +"'}")
		);
		logger.error("Suscripcion registrada en conekta " + customer.getId());
		return subscription;
	}
	
	@SuppressWarnings("unchecked")
	public Customer findCustomerByCellphoneNumber(String cellphoneNumber) throws Error, ErrorList {
		logger.error("Consultando  en conekta al cliente para el número celular " + cellphoneNumber);
		Customer customer = null;
		Conekta.setApiKey(this.getPrivateApiKey());
		Conekta.setApiVerion(conektaApiVersionOne);
		List<Customer> customers = Customer.where(
				new JSONObject("{'phone':'"+cellphoneNumber +"'}")
				);
		if(!customers.isEmpty()){
			customer = customers.get(0);
		}
		Conekta.setApiVerion(conektaApiVersionTwo);
		return customer;
	}
	
	public Customer findCustomerById(String id) throws Error, ErrorList {
		logger.error("Consultando  en conekta el cliente con Id " + id);
		Customer customer = null;
		Conekta.setApiKey(this.getPrivateApiKey());
		Conekta.setApiVerion(conektaApiVersionTwo);
		customer = Customer.find(id);
		return customer;
	}
	
	public Order createOxxoOrder(String planName, Integer planAmount, String email, String cellphoneNumber) throws ErrorList, Error, JSONException {
		Conekta.setApiKey(this.getPrivateApiKey());
		Conekta.setApiVerion(conektaApiVersionTwo);
		Long nowUnixTimestamp = System.currentTimeMillis();
		Long expirationDaysFromNowUnixTimestamp =  (nowUnixTimestamp + referenceOxxoExpiresat * 24 * 60 * 60 * 1000) / 1000L;
		String daysFromNow = expirationDaysFromNowUnixTimestamp.toString(); 
		  String customerEmail = StringUtils.isNotBlank(email) ? email : turboredDefaultEmail;
		  Integer amount = planAmount != null ? planAmount * 100 : 0;
		  Order order = Order.create(
		    new JSONObject("{"
		      + "'line_items': [{"
		          + "'name': '" + planName +"',"
		          + "'unit_price': " + amount + ","
		          + "'quantity': "+ 1 
		      + "}],"
		      + "'currency': 'MXN',"
		      + "'customer_info': {"
		        + "'name': '"+ PaymentConstants.GENERAL_TURBORED_USER +"',"
		        + "'email': '"+ customerEmail + "',"
		        + "'phone': '" + cellphoneNumber + "'"
		      + "},"
		      + "'charges':[{"
		        + "'payment_method': {"
		          + "'type': 'oxxo_cash',"
		          + "'expires_at': " + daysFromNow
		        + "}"
		      + "}]"
		    + "}"
		    )
		  );
		  return order;
	}
	
	public Order createCardOrder(String planName, Integer unitPrice, String email, String cellphoneNumber, String tokenId, String customerName) throws ErrorList, Error, JSONException {
		Conekta.setApiKey(this.getPrivateApiKey());
		Conekta.setApiVerion(conektaApiVersionTwo);
		  String customerEmail = StringUtils.isNotBlank(email) ? email : turboredDefaultEmail;
		  Integer amount = unitPrice != null ? unitPrice * 100 : 0;
		  Order order = Order.create(
		    new JSONObject("{"
		      + "'line_items': [{"
		          + "'name': '" + planName +"',"
		          + "'unit_price': " + amount + ","
		          + "'quantity': "+ 1 
		      + "}],"
		      + "'currency': 'MXN',"
		      + "'customer_info': {"
		        + "'name': '"+ customerName +"',"
		        + "'email': '"+ customerEmail + "',"
		        + "'phone': '" + cellphoneNumber + "'"
		      + "},"
		      + "'charges':[{"
                + "'payment_method': {"
                + "    'type': 'card',"
                + "    'token_id': '"+tokenId+"'"
                + "}"
                + ", "
                + "'amount': "+amount+""
                + "}"
		      + "]"
		    + "}"
		    )
		  );
		  return order;
	}
	
	private JSONObject createCustomerJsonObject(String name, String phone, String email) {
		JSONObject customerJsonObject = new JSONObject(
	            "{ 'name': '"+name+"'," +
	                    "  'phone': '"+phone+"'," +
	                    "  'email': '"+email+"'" +
	                    "}"
				);
		return customerJsonObject;
	}
	
	public Subscription findSubscription(String cellphoneNumber){
		Subscription subscription = null;
		Customer customer;
		try {
			customer = this.findCustomerByCellphoneNumber(cellphoneNumber);
			if(customer != null) {
				subscription = customer.subscription;
			} else {
				logger.info("No existe cliente registrado con el numero " + cellphoneNumber);
			}
		} catch (Error e) {
			logger.error("Error al consultar estatus de sucripcion, No se encontro suscripcion en conekta para " + cellphoneNumber + " "  + e.getMessage());
		} catch (ErrorList e) {
			logger.error("Error al consultar estatus de sucripcion, No se encontro suscripcion en conekta para " + cellphoneNumber + " " + e.getMessage());
		}
		return subscription;
	}
	
	public void cancelSubscription(String cellphoneNumber) throws Error, ErrorList {
		Customer customer = this.findCustomerByCellphoneNumber(cellphoneNumber);
		if(customer != null) {
			if(customer.subscription != null) {
				String subscriptionStatus = customer.subscription.status;
				if(subscriptionStatus != null && subscriptionStatus.equals(PaymentConstants.CONEKTA_SUBSCRIPTION_ACTIVE_STATUS)) {
					customer.subscription.cancel();
				} else {
					logger.error("No existe suscripcion activa para el numero " + cellphoneNumber);
				}
			}else {
				logger.error("No existe suscripcion para el numero " + cellphoneNumber);
			}
		} else {
			logger.error("No existe cliente registrado con el numero " + cellphoneNumber);
		}
		
	}
	
	private JSONObject createChargeJsonObject(String name, String tokenId, Integer amount) {
		JSONObject  chargeJsonObject = new JSONObject("{"
                + "'payment_method': {"
                + "    'type': 'card',"
                + "    'token_id': '"+tokenId+"'"
                + "}, "
                + "'amount': "+amount+""
                + "}");
		return chargeJsonObject;
	}
	
	private JSONObject createOrderObject(String productName, String description, Integer unitPrice, Integer quantity) {
		JSONObject orderJsonObject = new JSONObject(
	            "{ 'currency': 'mxn'," +
	                    "  'line_items': [{" +
	                    "    'name': '"+productName+"," +
	                    "    'description': '"+description+"'," +
	                    "    'unit_price': "+unitPrice+"," +
	                    "    'quantity': "+quantity+"," +
	                    "  }]" +
	                    "}"
	                );
		return orderJsonObject;
	}
	
	private String getPrivateApiKey() {
//		return "key_g6NYaEeMv9L3AKGpGDND8Q";// TODO QUITAR PROD
//		return configParameterService.getParameterValue(ConfigParamConstants.CONEKTA_PRIVATE_API_KEY);// TODO REGRESAR
		return conektaPrivateApiKey;
	}
	
	private String getPublicApiKey() {
//		return configParameterService.getParameterValue(ConfigParamConstants.CONEKTA_PUBLIC_API_KEY); // TODO REGRESAR
//		return "key_g6NYaEeMv9L3AKGpGDND8Q";
		return conektaPublicApiKey;
	}
	
//	public Order createOxxoOrder(String planName, Integer planAmount, String email, String cellphoneNumber) throws ErrorList, Error, JSONException {
//		Conekta.setApiKey(conektaApiKey);
//		Conekta.setApiVerion(conektaApiVersionTwo);
//		Long nowUnixTimestamp = System.currentTimeMillis();
//		Long expirationDaysFromNowUnixTimestamp =  (nowUnixTimestamp + referenceOxxoExpiresat * 24 * 60 * 60 * 1000) / 1000L;
//		String daysFromNow = expirationDaysFromNowUnixTimestamp.toString(); 
//		  String customerEmail = StringUtils.isNotBlank(email) ? email : turboredDefaultEmail;
//		  Order order = Order.create(
//		    new JSONObject("{"
//		      + "'line_items': [{"
//		          + "'name': '" + planName +"',"
//		          + "'unit_price': " + planAmount + ","
//		          + "'quantity': "+ 1 
//		      + "}],"
//		      + "'shipping_lines': [{"
//		          + "'amount': 0,"
//		          + "'carrier': 'FEDEX',"
//		      + "}]," //shipping_lines - physical goods only
//		      + "'currency': 'MXN',"
//		      + "'customer_info': {"
//		        + "'name': '"+ PaymentConstants.CONEKTA_GENERAL_CUSTOMER +"',"
//		        + "'email': '"+ customerEmail + "',"
//		        + "'phone': '" + cellphoneNumber + "'"
//		      + "},"
//		      + "'shipping_contact':{"
//		         + "'address': {"
//		           + "'street1': '" + turboredAddress + "',"
//		           + "'postal_code': '" + turboredZipcode + "',"
//		           + "'country': '" + turboredCountryCode + "'"
//		         + "}"
//		       + "}," //shipping_contact - required only for physical goods
//		      + "'charges':[{"
//		        + "'payment_method': {"
//		          + "'type': 'oxxo_cash',"
//		          + "'expires_at': " + daysFromNow
//		        + "}"
//		      + "}]"
//		    + "}"
//		    )
//		  );
//		  
//		  return order;
//	}
	
	
	
	
}