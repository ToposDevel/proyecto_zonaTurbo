package com.topostechnology.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.mail.MessagingException;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import com.topostechnology.constant.ConektaEventConstants;
import com.topostechnology.constant.OfferConstans;
import com.topostechnology.constant.PaymentConstants;
import com.topostechnology.constant.SuscribersConstants;
import com.topostechnology.constant.UserConstants;
import com.topostechnology.domain.BinacleSubscription;
import com.topostechnology.domain.ConektaCustomer;
import com.topostechnology.domain.ConektaSubscription;
import com.topostechnology.domain.ConektaSubscriptionEvent;
import com.topostechnology.domain.ScheduledSubscription;
import com.topostechnology.exception.TrException;
import com.topostechnology.model.OdooInvoicing;
import com.topostechnology.model.PlanModel;
import com.topostechnology.model.SubscriptionModel;
import com.topostechnology.model.ValidUserPlans;
import com.topostechnology.repository.BinacleSubscriptionRepository;
import com.topostechnology.repository.ConektaCustomerRepository;
import com.topostechnology.repository.ConektaSubscriptionEventRepository;
import com.topostechnology.repository.ConektaSubscriptionRepository;
import com.topostechnology.repository.ScheduledSubscriptionRepository;
import com.topostechnology.rest.client.ConektaClient;
import com.topostechnology.rest.client.GategayApiClient;
import com.topostechnology.rest.client.GategayApiXtraClient;
import com.topostechnology.rest.client.request.OfferIdConektaAltanResponse;
import com.topostechnology.rest.client.request.PurchaseRequest;
import com.topostechnology.rest.client.request.UpdateOffer;
import com.topostechnology.rest.client.request.UpdateOfferRequest;
import com.topostechnology.rest.client.response.AltanActionResponse;
import com.topostechnology.rest.client.response.OfertasReemplazo;
import com.topostechnology.rest.client.response.OfertasReemplazoResponse;
import com.topostechnology.rest.client.response.PerfilResponse;
import com.topostechnology.rest.client.response.ProfileDataResponse;
import com.topostechnology.rest.client.response.PurchaseResponse;
import com.topostechnology.rest.client.response.SubscriberCoordinates;
import com.topostechnology.rest.client.response.SubscribersActivateRequest;
import com.topostechnology.utils.DateUtils;
import com.topostechnology.utils.StringUtils;
import com.topostechnology.validation.GeneralValidator;
import com.topostechnology.webhook.data.SubscriptionEvent;

import io.conekta.Customer;
import io.conekta.Error;
import io.conekta.ErrorList;
import io.conekta.Plan;
import io.conekta.Subscription;

@Service
public class ConektaSubscriptionService {

	private static final Logger logger = LoggerFactory.getLogger(ConektaSubscriptionService.class);
	private static String CELPHONE_VALIDATION_PROBLEM = " Este número celular no pudo ser validado,  intente nuevamente más tarde.";
	private static final String SUBSCRIPTIONS = "SuscripcionesPagadas";
	
	@Autowired
	private ConektaClient conektaClient;

	@Value("${mx.country.code}")
	private String mxCountryCode;

	@Value("${turbored.be.id}")
	private String beId;

	@Value("${iva.percentage}")
	private Integer ivaPercentage;
	
	@Value("${callcenter.phone}")
	private String callCenter;

	@Autowired
	private ConektaCustomerRepository conektaCustomerRepository;

	@Autowired
	private ConektaSubscriptionEventRepository conektaSubscriptionDetailRepository;

	@Autowired
	private ConektaSubscriptionRepository conektaSubscriptionRepository;
	
	@Autowired
	private ScheduledSubscriptionRepository scheduledSubscriptionRepository;

	@Autowired
	private GategayApiClient gategayApiClient;

	@Autowired
	private GategayApiXtraClient gategayApiXtraClient;

	@Autowired
	private InvoicingService subscriptionInvoicingService;
	
	@Autowired
	private ProfileService profileService;
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private AltanActionRequestService altanActionRequestService;
	
   @Autowired
   private BinacleSubscriptionRepository  binacleSubscriptionRepository;
	
	@Value("${callcenter.phone}")
	private String callCenterPhone;

	public String subscribe(SubscriptionModel subscriptionModel) throws Exception {
		/* logger.info("Empezando con el proceso de domiciliación para el número celular " +subscriptionModel.getCellphoneNumber() +" con la oferta " 
				+ subscriptionModel.getPlanSelectedId()); */
		logger.error("Empezando con el proceso de domiciliación para el número celular " +subscriptionModel.getCellphoneNumber() +" con la oferta " 
				+ subscriptionModel.getPlanSelectedId());
		String message = null;
		
		BinacleSubscription binacleSubscription = new BinacleSubscription(); 
		binacleSubscription.setCreateAt(new Date());
		binacleSubscription.setPhone(subscriptionModel.getCellphoneNumber());
		
		
		
		if(subscriptionModel.getSuperOfferType().equals(OfferConstans.HBB_SUPER_TYPE)) { // SI ES HBB consultar las coordenas que tiene en número
			subscriptionModel.setCoordinates(getHbbCoornidates(subscriptionModel.getCellphoneNumber()));
		}
		String status = "Fecha expira: ";
		Date expireDate = profileService.getOfferExpiredDate(subscriptionModel.getCellphoneNumber());
		status += expireDate.toString();
		if(this.isOfferExpired(expireDate)) { // SI LA FECHA DE LA OFERTA YA EXPIRO SE HACE LA DOMICILIACION
			// logger.info("La oferta "+subscriptionModel.getPlanSelectedId() + "del número celular: "+subscriptionModel.getCellphoneNumber() + " expiro el día " + expireDate);
			logger.error("La oferta "+subscriptionModel.getPlanSelectedId() + "del número celular: "+subscriptionModel.getCellphoneNumber() + " expiro el día " + expireDate);
			Subscription subscription = makeFullSubscriptionInConekta(subscriptionModel);
			this.sendSuscriptionBeginNotification(subscriptionModel.getEmail(), subscriptionModel.getCellphoneNumber(),subscriptionModel.getCardTitularName());
			if (subscription != null) {
				status+= " procesa";
				message = "La domiciliación esta en proceso, recibira una notificación al procesarse el pago y activar el servicio.";
			} else {
				status += " error";
				throw new TrException("La operación no pudo ser completada, intente nuevamente más tarde");
			}
			
		} else { // SI LA FECHA DE LA OFERTA NO HA EXPIRADO SE PROGRAMA LA DOMICILIACIÓN
			saveInfoToScheduleSubscription(subscriptionModel, expireDate);
			this.sendSuscriptionBeginNotification(subscriptionModel.getEmail(), subscriptionModel.getCellphoneNumber(),subscriptionModel.getCardTitularName());
			message = "La domiciliación esta en proceso, el día de la expiración de tu plan actual recibiras una notificación al procesarse el pago y activar el servicio.";
			status+= " programada";
		}
		
		binacleSubscription.setInternalStatus(status);
		binacleSubscriptionRepository.save(binacleSubscription);
		
		return message;
	}
	
	private String getHbbCoornidates(String cellphoneNumber) throws TrException {
		String coordinates = null;
		SubscriberCoordinates subscriberCoordinates = gategayApiClient.getSubscriberCoordinates(cellphoneNumber);
		if (subscriberCoordinates != null) {
			coordinates = subscriberCoordinates.getLatitude() + "," + subscriberCoordinates.getLongitude();
		} else {
			logger.error("Las coordenadas llegaron vacias");
			throw new TrException("La operación no pudo ser completada, comuniquese al call center ");
		}
		return coordinates;
	}
	
	/**
	 * Consulta los registros de suscripciones programadas para el día de hoy con status SCHEDULED_SUBSCRIPTION
	 * y realiza la suscripción en CONEKTA
	 */
	public void makeScheduledSubscriptions() {
		// logger.info("Empezando a ejecutar las suscripciones programadas para el día de hoy .");
		BinacleSubscription binacleSubscription = new BinacleSubscription();
		logger.error("Empezando a ejecutar las suscripciones programadas para el día de hoy .");
		Date todayDate = new Date();
		Date todayInitDate = DateUtils.getInitDateTime(todayDate);
		Date todayEndDate = DateUtils.getEndDateTime(todayDate);
		List<ScheduledSubscription> scheduledSubscriptionList = scheduledSubscriptionRepository.findByScheduledAtBetweenAndInternalStatus(todayInitDate, 
				todayEndDate, SuscribersConstants.SCHEDULED_SUBSCRIPTION_STATUS);
		for(ScheduledSubscription scheduledSubscription : scheduledSubscriptionList) {
			// logger.info("Empezando el proceso de suscripción para el número celular " + scheduledSubscription.getPhone());
			logger.error("Empezando el proceso de suscripción para el número celular " + scheduledSubscription.getPhone());
			Customer customer;
			try {
				binacleSubscription.setCreateAt(new Date());
				binacleSubscription.setPhone(scheduledSubscription.getPhone());
				customer = conektaClient.findCustomerById(scheduledSubscription.getConektaCustomer().getConektaCustomerId());
				// logger.info("Cliente localizado en conekta " + customer.phone);
				logger.error("Cliente localizado en conekta " + customer.phone);
				ConektaSubscription conektaSubscription = makeJustSubscriptionInConekta(scheduledSubscription, customer);
				// logger.info("Suscripción realizada en conekta exitosamente" + customer.phone);
				logger.error("Suscripción realizada en conekta exitosamente" + customer.phone);
				scheduledSubscription.setConektaSubscription(conektaSubscription);
				scheduledSubscription.setSubscriptionAt(conektaSubscription.getCreatedAt());
				scheduledSubscription.setInternalStatus(SuscribersConstants.SUBSCRIBED_STATUS);
				binacleSubscription.setInternalStatus("Sheduled: suscrito");
				
				scheduledSubscriptionRepository.save(scheduledSubscription);
			} catch (Error e) {
				e.printStackTrace();
				binacleSubscription.setInternalStatus("Sheduled: error");
				logger.error("No pudo realizarse la domiciliación programada del número "+scheduledSubscription.getPhone()+ ".-" + e.getMessage());
			} catch (ErrorList e) {
				String errorStr = "";
				ArrayList<Error> details = e.details;
				for (io.conekta.Error error : details) {
					errorStr = errorStr + " " + error.getMessage();
				}
				logger.error(errorStr);
				logger.error("No pudo realizarse la domiciliación programada del número "+scheduledSubscription.getPhone()+ ".-" + e.getMessage());
				binacleSubscription.setInternalStatus("Sheduled: error");
			} catch (Exception e) {
				logger.error("No pudo realizarse la domiciliación programada del número "+scheduledSubscription.getPhone()+ ".-" + e.getMessage());
				binacleSubscription.setInternalStatus("Sheduled: error");
			}
			
			binacleSubscriptionRepository.save(binacleSubscription);
		}
	}
	
	private void saveInfoToScheduleSubscription(SubscriptionModel suscriptionModel, Date expireDate) throws Exception {
		// logger.info("Se programa la suscripción del número celular" +suscriptionModel.getCellphoneNumber() + " para la fecha de expiracion de la oferta " +suscriptionModel.getPlanSelectedId() + "el día" + expireDate);
		logger.error("Se programa la suscripción del número celular" +suscriptionModel.getCellphoneNumber() + " para la fecha de expiracion de la oferta " +suscriptionModel.getPlanSelectedId() + "el día" + expireDate);
		Date scheduledAt = this.getScheduledDate(expireDate);
		// logger.info("fecha programada para el número celular " + suscriptionModel.getCellphoneNumber()  + "es " + scheduledAt);
		logger.error("fecha programada para el número celular " + suscriptionModel.getCellphoneNumber()  + "es " + scheduledAt);
		JSONObject jSONObjectCustomer = this.createJSONObjectCustomer(suscriptionModel);
		Customer customer = conektaClient.createCustomer(jSONObjectCustomer);
		// logger.info("Cliente registrado en conekta");
		logger.error("Cliente registrado en conekta");
		if (customer != null) {
			ConektaCustomer conektaCustomer = saveConektaCustomerInDb(customer);
			// logger.info("Cliente registrado en bd para el número celular " + suscriptionModel.getCellphoneNumber());
			logger.error("Cliente registrado en bd para el número celular " + suscriptionModel.getCellphoneNumber());
			ScheduledSubscription scheduledSubscription = createScheduledSubscriptionObject(suscriptionModel, conektaCustomer, scheduledAt);
			// logger.info("Programación de suscripción registrada en bd. para el número celular " + suscriptionModel.getCellphoneNumber());
			logger.error("Programación de suscripción registrada en bd. para el número celular " + suscriptionModel.getCellphoneNumber());
			scheduledSubscriptionRepository.save(scheduledSubscription);
		} else {
			throw new TrException("La operación no pudo ser completada, intente nuevamente más tarde");
		}
	}
	
	private Date getScheduledDate(Date expireDate) {
		Date scheduledDate = null;
		Calendar cal = Calendar.getInstance();
		cal.setTime(expireDate);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		Date endDateTime = cal.getTime();
		Date initExpireDay = DateUtils.getInitDateTime(expireDate);
		if (expireDate.before(endDateTime)) { // SI LA FECHA DE EXPIRACION ES ANTES DEL FIN DEL DÍA
			scheduledDate = initExpireDay; // LA FECHA PROGRAMADA SERA EL MISMO DIA
		} else { // SI LA FECHA ES EL FIN DEL DÍA
			scheduledDate = DateUtils.addOrRemoveDays(initExpireDay, 1);// LA FECHA PROGRAMADA SERA EL SIGUIENTE DIA
		}
		return scheduledDate;
	}

	private ScheduledSubscription createScheduledSubscriptionObject(SubscriptionModel suscriptionModel, ConektaCustomer conektaCustomer, Date scheduledAt) throws Exception {
		String altanOfferId = this.getAltanOfferId(suscriptionModel.getPlanSelectedId());
		ScheduledSubscription scheduledSubscription = new ScheduledSubscription();
		scheduledSubscription.setActive(true);
		scheduledSubscription.setConektaPlanId(suscriptionModel.getPlanSelectedId());
		scheduledSubscription.setAltanOfferId(altanOfferId != null ? altanOfferId : "0");
		scheduledSubscription.setCardId(conektaCustomer.getConektaCustomerId());
		scheduledSubscription.setConektaCustomer(conektaCustomer);
		scheduledSubscription.setCreatedAt(new Date());
		scheduledSubscription.setInternalStatus(SuscribersConstants.SCHEDULED_SUBSCRIPTION_STATUS);
		scheduledSubscription.setScheduledAt(scheduledAt);
		scheduledSubscription.setPhone(suscriptionModel.getCellphoneNumber());
		scheduledSubscription.setBelongsToHbb(suscriptionModel.isBelongsToHhb());
		scheduledSubscription.setCoordinates(suscriptionModel.getCoordinates());
		scheduledSubscription.setInternalOperation(suscriptionModel.getInternalOperation());
		scheduledSubscription.setPaymentProvider(PaymentConstants.PAYMENT_PROVIDER_CONEKTA);
		return scheduledSubscription;
	}
	
	private Subscription makeFullSubscriptionInConekta(SubscriptionModel suscriptionModel) throws Exception {
		// logger.info("Se empieza con el proceso de suscripción en conekta COMPLETA");
		logger.error("Se empieza con el proceso de suscripción en conekta COMPLETA");
		JSONObject jSONObjectCustomer = this.createJSONObjectCustomer(suscriptionModel);
		Customer customer = conektaClient.createCustomer(jSONObjectCustomer);
		Subscription subscription = null;
		if (customer != null) {
			subscription = customer
					.createSubscription(new JSONObject("{'plan':'" + suscriptionModel.getPlanSelectedId() + "'}"));
			/* logger.info("Suscripción creada en conekta " + subscription.getId() + " para el número celular "
					+ suscriptionModel.getCellphoneNumber()); */
			logger.error("Suscripción creada en conekta " + subscription.getId() + " para el número celular "
					+ suscriptionModel.getCellphoneNumber());
		}
		if (subscription != null) {
			this.saveFullConektaSubscriptionInDb(subscription, suscriptionModel);
		} else {
			// logger.info("La suscripción no se realizo");
			logger.info("La suscripción no se realizo");
			throw new TrException("La suscripción no pudo realizarse, intente nuevamente más tarde");
		}
		return subscription;
	}

	private ConektaSubscription makeJustSubscriptionInConekta(ScheduledSubscription scheduledSubscription, Customer customer) throws Exception {
		// logger.info("Se comienza con el proceso de suscripción en conekta PARCIAL(suscripción programada) para el número celular" + scheduledSubscription.getPhone() );
		logger.error("Se comienza con el proceso de suscripción en conekta PARCIAL(suscripción programada) para el número celular" + scheduledSubscription.getPhone() );
		ConektaSubscription conektaSubscription = null;
		if (customer != null) {
			Subscription subscription = customer
					.createSubscription(new JSONObject("{'plan':'" + scheduledSubscription.getConektaPlanId() + "'}"));
			/* logger.info("Suscripción creada en conekta " + subscription.getId() + " para el número celular "
					+ scheduledSubscription.getPhone()); */
			logger.error("Suscripción creada en conekta " + subscription.getId() + " para el número celular "
					+ scheduledSubscription.getPhone());
			
			if (subscription != null) {
				conektaSubscription = this.saveJustConektaSubscriptionInDb(subscription, scheduledSubscription);
			} 
		}
		return conektaSubscription;
	}

	public ConektaSubscriptionEvent saveSubscriptionEventInDb(SubscriptionEvent subscriptionEvent, String event) {
		// logger.info("Registrando SubscriptionEvent en bd");
		logger.error("Registrando SubscriptionEvent en bd");
		ConektaSubscriptionEvent conektaSubscriptionDetail = null;
		ConektaSubscription conektaSubscription = conektaSubscriptionRepository
				.findByConektaSubscriptionId(subscriptionEvent.getId());
		if (conektaSubscription != null) {
			conektaSubscriptionDetail = saveConektaSubscriptionDetail(subscriptionEvent,
					conektaSubscription, event);
			// logger.info("SubscriptionEvent registrado en bd " + conektaSubscriptionDetail.getId());
			logger.error("SubscriptionEvent registrado en bd " + conektaSubscriptionDetail.getId());
		} else {
			logger.error("No existe suscripcion registrada en BD para el id " +  subscriptionEvent.getId()  + ", el evento no se registro en BD");
		}
		return conektaSubscriptionDetail;
	}

	/**
	 * Inserta registro en bd de evento generado(tabla conekta_subscription_event)
	 * 
	 * @param subscriptionEvent
	 * @param conektaSubscription
	 * @param event
	 * @return
	 */
	private ConektaSubscriptionEvent saveConektaSubscriptionDetail(SubscriptionEvent subscriptionEvent,
			ConektaSubscription conektaSubscription, String event) {
		ConektaSubscriptionEvent conektaSubscriptionDetail = null;
		if (conektaSubscription != null) {
			conektaSubscriptionDetail = createConektaSubscriptionDetObject(subscriptionEvent, conektaSubscription,
					event);
			conektaSubscriptionDetailRepository.save(conektaSubscriptionDetail);
		} else {
			logger.error("No existe Subscripción registrada en bd para el evento de la suscripción "
					+ subscriptionEvent.getId());
			// TODO Consultar subscripcion en conekta y registrar Subscripcion y detalle en
			// bd
		}
		if (conektaSubscriptionDetail != null && conektaSubscriptionDetail.getId() != null) {
			logger.info("El evento de suscripción se registro en bd exitosamente");
		} else {
			logger.error("El evento de suscripción NO se registro en bd.");
		}
		return conektaSubscriptionDetail;
	}

	public BindingResult validateSubscriptionData(SubscriptionModel suscriptionModel, BindingResult bindingResult) {
		if (StringUtils.isBlank(suscriptionModel.getCardTitularName())) {
			bindingResult.rejectValue("customerName", "mandatory.field");
		}
		if (StringUtils.isBlank(suscriptionModel.getEmail())) {
			bindingResult.rejectValue("email", "mandatory.field");
		}
		if (StringUtils.isBlank(suscriptionModel.getPlanSelectedId())) {
			bindingResult.rejectValue("planSelectedId", "mandatory.field");
		}
		
		if(suscriptionModel.isSuscriptionWithImei()) {
			if(!GeneralValidator.validatePattern(UserConstants.REGEX_IMEI_NUMBER, suscriptionModel.getImei())) {
				bindingResult.rejectValue("imei", "imei.number.fifteen");
			}
		} else {
			if (!GeneralValidator.validatePattern(UserConstants.REGEX_CELLPHONE_NUMBER,
					suscriptionModel.getCellphoneNumber())) {
				bindingResult.rejectValue("cellphoneNumber", "cellphone.number.then");
			}
		}
		return bindingResult;
	}

	private Plan getPlanById(String id) throws Error, ErrorList {
		Plan plan = conektaClient.getPlanById(id);
		return plan;
	}

	/**
	 * Inserta registro en bd(conekta_subscription_customer) con los datos de
	 * cliente creado en conekta
	 * 
	 * @param customer
	 * @return
	 */
	public ConektaCustomer saveConektaCustomerInDb(Customer customer) {
		// logger.info("Registrando ConektaCustomer en bd");
		logger.error("Registrando ConektaCustomer en bd");
		ConektaCustomer conektaCustomer = createConektaCustomerObject(customer);
		conektaCustomerRepository.save(conektaCustomer);
		// logger.info("ConektaCustomer registrado en bd " + conektaCustomer.getId());
		logger.error("ConektaCustomer registrado en bd " + conektaCustomer.getId());
		return conektaCustomer;
	}

	/**
	 * Inserta registro en bd(tabla conekta_subscription) con los datos de la
	 * suscripción
	 * 
	 * @param subscription
	 * @param activationOrderId
	 * @return
	 * @throws Exception 
	 */
	private ConektaSubscription saveFullConektaSubscriptionInDb(Subscription subscription, SubscriptionModel suscriptionModel) throws Exception {
		logger.error("Registrando ConektaSubscription en bd");
		ConektaSubscription conektaSubscription = createConektaSubscriptionObject(subscription, suscriptionModel.isBelongsToHhb(), 
				suscriptionModel.getCoordinates(), suscriptionModel.getInternalOperation());
		if (subscription.customer != null) {
			ConektaCustomer conektaCustomer = saveConektaCustomerInDb(subscription.customer);
			conektaSubscription.setConektaCustomer(conektaCustomer);
		}
		conektaSubscriptionRepository.save(conektaSubscription);
		logger.error("ConektaSubscription registrado en bd " + conektaSubscription.getId());
		return conektaSubscription;
	}
	
	private ConektaSubscription saveJustConektaSubscriptionInDb(Subscription subscription, ScheduledSubscription scheduledSubscription) throws Exception {
		logger.error("Registrando ConektaSubscription en bd");
		ConektaSubscription conektaSubscription = createConektaSubscriptionObject(subscription, scheduledSubscription.isBelongsToHbb(),
				scheduledSubscription.getCoordinates(), scheduledSubscription.getInternalOperation());
		conektaSubscription.setConektaCustomer(scheduledSubscription.getConektaCustomer());
		conektaSubscriptionRepository.save(conektaSubscription);
		logger.error("ConektaSubscription registrado en bd " + conektaSubscription.getId());
		return conektaSubscription;
	}
	
	public ValidUserPlans getValidUserPlans(String cellphoneNumber, String superOfferType) throws Exception {
		ValidUserPlans validUserPlans = new ValidUserPlans();
		validUserPlans.setSuperOfferType(superOfferType);
		List<PlanModel> validUserConektaPlans = new ArrayList<PlanModel>();
		ProfileDataResponse profile = profileService.getProfile(cellphoneNumber);
		Boolean isTurboredMsisdn = profile != null;
		if (isTurboredMsisdn) {// EL NÚMERO SI PERTENECE A TURBORED
			this.isSubscribed(cellphoneNumber); // VERIFICA SI YA EXISTE UNA SUSCRIPCION // TODO REGRESAR
			List<PlanModel> conektaPlans = this.getPlansAsCat(); // Obtiene todos los planes registrados en conekta
			
			if(superOfferType.equals(OfferConstans.MIFI_SUPER_TYPE)) { // SI ES MIFI(FLUJO TEMPORAL SOLO PURCHASE)
				validUserConektaPlans = this.getReplacementOffers(cellphoneNumber,"0","0"); // Consulta opciones de reemplazo
//				validUserConektaPlans = filterOffersBySuperType(conektaPlans, superOfferType);// TODO QUITAR
				validUserPlans.setAltanOperation(SuscribersConstants.ALTAN_PURCHASE_OPERATION);
				
			} else if(profileService.isNewSim(profile)) { // ES UN SIM NUEVO (IDLE U OFFER 1000)
				logger.info(cellphoneNumber + " Es sim nuevo en estatus IDLE o ACTIVE con oferta default(1000)");
				//validUserConektaPlans = filterOffersBySuperType(conektaPlans, superOfferType);//Obtiene los planes validos
				validUserConektaPlans = this.getReplacementOffers("0", superOfferType ,"0"); // Consulta opciones de reemplazo
				validUserPlans.setAltanOperation(SuscribersConstants.ALTAN_ACTIVATION_OPERATION);
			} else { // SI ES UN SIM QUE YA TIENE UNA OFERTA ACTIVA 
				logger.error(cellphoneNumber + " Es sim con oferta activa que NO es oferta default");
				String offerId = profileService.getPrimaryOfferId(profile);
				if(profileService.isRenewableOffer(offerId)) { //  SI ES OFERTA RENOVABLE
					String profileStatus = this.getProfileStatus(profile);
					logger.error(cellphoneNumber + " con oferta RENOVABLE en esatus  " + profileStatus);
					switch(profileStatus) {
					case SuscribersConstants.ALTAN_BARRING_STATUS: // SI ESTA EN ESTATUS BARRING
						validUserPlans.setAltanOperation(SuscribersConstants.ALTAN_UNBARRING_OPERATION);
						break;
					case SuscribersConstants.ALTAN_SUSPEND_STATUS:// SI ESTA EN ESTATUS SUSPEND
						validUserPlans.setAltanOperation(SuscribersConstants.ALTAN_RESUME_OPERATION);
						break;
					default: // SI EL ESTATUS ES ACTIVE
						validUserPlans.setAltanOperation(SuscribersConstants.ALTAN_NONE_OPERATION);
					}
					/*PlanModel conektaPlan = this.findOfferInList(conektaPlans, offerId); // consulta la oferta activa
					if(conektaPlan != null) {
						validUserConektaPlans.add(conektaPlan);
					}*/
					validUserConektaPlans = this.getReplacementOffers("0", "0" , offerId); // Consulta opciones de reemplazo
					// TODO VERIFICAR SI SE MOSTRARAN OTRAS OCPIONES DE OFERTA
					// TODO OBTENER OFFERID DE ALTAN
				} else { //  SI ES OFERTA NO RENOVABLE
					validUserConektaPlans = this.getReplacementOffers(cellphoneNumber, "0","0"); // Consulta opciones de reemplazo
					validUserPlans.setAltanOperation(SuscribersConstants.ALTAN_UPDATE_OPERATION);
				}
			}
			logger.error("Operación: " + validUserPlans.getAltanOperation());
		}
		if(validUserConektaPlans == null || validUserConektaPlans.isEmpty()) {
			logger.error("No se encontraron planes disponibles en conekta para el número celular " + cellphoneNumber);
			throw new TrException("No se encontraron planes disponibles para el número ingresado, por favor comunícate a nuestro Call Center." );
		}
		validUserPlans.setPlanList(validUserConektaPlans);
		validUserPlans.setCellphoneNumber(cellphoneNumber);
		return validUserPlans;
	}
	
	public String getProfileStatus(ProfileDataResponse profile) throws TrException {
		String status = null;
		String profileStatus = profileService.getStatus(profile);
		if(profileStatus != null) {
			String profileStatusUpper = profileStatus != null ? profileStatus.toUpperCase(): "";
			if(profileStatusUpper.startsWith(SuscribersConstants.ALTAN_BARRING_STATUS)) {
				status = SuscribersConstants.ALTAN_BARRING_STATUS;
			} else if(profileStatusUpper.startsWith(SuscribersConstants.ALTAN_SUSPEND_STATUS)) {
				status = SuscribersConstants.ALTAN_SUSPEND_STATUS;
			} else {
				status = profileStatusUpper;
			}
		} else {
			throw new TrException( CELPHONE_VALIDATION_PROBLEM);
		}
		return status;
	}
	
	/**
	 * Filtra ofertas que inician con el texto superOfferType
	 * @param planList
	 * @return
	 */
	public List<PlanModel> filterOffersBySuperType(List<PlanModel> planList, String superOfferType) {
		List<PlanModel> planListFiltered = new ArrayList<PlanModel>();
		String superOfferType3positions = superOfferType.substring(0, 3);
		for(PlanModel planModel : planList) {
			String initString = planModel.getName() != null ? planModel.getName().substring(0, 3) : "";
			if(initString.equals(superOfferType3positions)) {
				planListFiltered.add(planModel);
			}
		}
		return planListFiltered;
	}
	
	/**
	 * Filtra ofertas que inician con el texto HBB ó MIF
	 * @param planList
	 * @return
	 */
	public List<PlanModel> filterHbbAndMifiOffers(List<PlanModel> planList) {
		List<PlanModel> planListFiltered = new ArrayList<PlanModel>();
		String mif = OfferConstans.MIFI_SUPER_TYPE.substring(0, 3);
		for(PlanModel planModel : planList) {
			String initString = planModel.getName() != null ? planModel.getName().substring(0, 3) : "";
			if(initString.equals(OfferConstans.HBB_SUPER_TYPE ) || initString.equals(mif)) {
				planListFiltered.add(planModel);
			}
		}
		return planListFiltered;
	}
	
	/**
	 * Filtra las ofertas que empiezan con el texto MOV
	 * @param planList
	 * @return
	 */
	public List<PlanModel> filterMovOffers(List<PlanModel> planList) {
		List<PlanModel> planListFiltered = new ArrayList<PlanModel>();
		for(PlanModel planModel : planList) {
			String initString = planModel.getName() != null ? planModel.getName().substring(0, 3) : "";
			if(initString.equals(OfferConstans.MOV_SUPER_TYPE)) {
				planListFiltered.add(planModel);
			}
		}
		return planListFiltered;
	}
	
	/**
	 * Obtiene el offerId de altan para el offerid registrado en conekta
	 * 
	 * @param conektaOfferId
	 * @return altanOfferId
	 * @throws Exception
	 */
	public String getAltanOfferId(String conektaOfferId) throws Exception {
		logger.info("Consultando offerId de altan para el conektaOfferId " + conektaOfferId);
		String altanOfferId = null;
		String tipoOperacion = "2"; // "El servicio requiere que se envíe 2 "
		OfferIdConektaAltanResponse offerIdConektaAltanResponse = gategayApiXtraClient
				.getofferIdConektaAltan(tipoOperacion, conektaOfferId);
		if (offerIdConektaAltanResponse != null) {
			altanOfferId = offerIdConektaAltanResponse.getOfertaAltan();
			logger.info("altanOfferId encontrado " +  altanOfferId);
			if(altanOfferId == null) {
				throw new TrException("altanOfferId NO encontrado para conektaOfferId" );
			} 
		}else {
			logger.error("altanOfferId NO encontrado para conektaOfferId " + conektaOfferId);
			throw new TrException("altanOfferId NO encontrado para conektaOfferId" );
		}
		return altanOfferId;
	}

	/**
	 * Consulta los eventos de pagos de Conekta para el día anterior de la fecha
	 * recibida
	 * 
	 * @param todayDate
	 * @return conektaSubscriptionDetailList lista de eventos pagados
	 */
	public List<ConektaSubscriptionEvent> getSubscriptionPaidEvents(Date todayDate) {
		List<OdooInvoicing> odooInvoicingList = new ArrayList<OdooInvoicing>();
		Date yesterdayDate = DateUtils.addOrRemoveDays(todayDate, -1);
		Date yesterdayInitDate = DateUtils.getInitDateTime(yesterdayDate);
		Date yesterdayEndDate = DateUtils.getEndDateTime(yesterdayDate);
		logger.info("Consultando suscripciones pagadas del día :  " + yesterdayInitDate + "-" + yesterdayEndDate);
		List<ConektaSubscriptionEvent> conektaSubscriptionDetailList = conektaSubscriptionDetailRepository
				.findByCreatedAtBetweenAndEventType(yesterdayInitDate, yesterdayEndDate, ConektaEventConstants.SUBSCRIPTION_PAID);
		for (ConektaSubscriptionEvent cnektaSubscriptionDetail : conektaSubscriptionDetailList) {
			OdooInvoicing odooInvoicing = new OdooInvoicing();
			ConektaSubscription conektaSubscription =cnektaSubscriptionDetail.getConektaSubscription();
			try {
				logger.info("plan id: " + conektaSubscription.getConektaPlanId());
				Plan plan = conektaClient.getPlanById(conektaSubscription.getConektaPlanId());
				if (plan != null) {
					Integer amount = plan.amount;
					if (ivaPercentage != null) {
						Double amountWithoutTaxes = (amount / 100.00) / ((100 / 100.00) + (ivaPercentage / 100.00));
						odooInvoicing.setSubtotal(String.valueOf(String.format("%.2f", amountWithoutTaxes)));
					}
					odooInvoicing.setTotal(String.valueOf(amount / 100));
				}
			} catch (Exception e) {
				logger.error("Se ha generado error al consultar costo del plan " + e.getMessage());
			}
			odooInvoicing.setCommercial("DOMICILIACIÓN");
			odooInvoicing
					.setCustomerName(conektaSubscription.getConektaCustomer().getName());
			odooInvoicing.setDebt("");
			odooInvoicing.setExpirationDate("");
			odooInvoicing.setInvoiceDate(DateUtils.formatDate(new Date(), DateUtils.YYYYMMDD_FORMAT));
			odooInvoicing.setInvoiceStatus("Factura no generada");
			odooInvoicing.setInvoiceIcon("");
			odooInvoicing.setNumber("");// TODO
			odooInvoicing.setOrigin("DOMICILIACIÓN");
			odooInvoicing.setPayments("");
			odooInvoicing.setStatus("Pagado");
			odooInvoicing.setOfferId(conektaSubscription.getAltanOfferId());
			odooInvoicing.setCellphoneNumber(conektaSubscription.getCellphoneNumber());

			odooInvoicingList.add(odooInvoicing);
		}
		try {
			subscriptionInvoicingService.createInvoiceExcel(odooInvoicingList, SUBSCRIPTIONS);
		} catch (Exception e) {
			logger.error("No se pudo generar el excel de facturacion para pago de las sucripciones diarias "
					+ e.getMessage());
		}
		return conektaSubscriptionDetailList;
	}

	public List<Plan> getPlans() throws Error, ErrorList {
		return conektaClient.getPlans();
	}

	public List<PlanModel> getPlansAsCat() throws Error, ErrorList {
		List<PlanModel> plansCat = new ArrayList<PlanModel>();
		List<Plan> plans = conektaClient.getPlans();
		for (Plan plan : plans) {
			PlanModel item = new PlanModel();
			item.setId(plan.id);
			item.setName(plan.name);
			if(plan.amount != null) {
				Integer amount =  plan.amount/100;
				item.setName(plan.name + " $" + amount);
			}
			item.setAmount(plan.amount);
			plansCat.add(item);
		}
		return plansCat;
	}

	public void processSubscriptionPaidEvent(ConektaSubscriptionEvent conektaSubscriptionDetail) {
		logger.info("Procesando evento de pago de suscripción");
		try {
			if (conektaSubscriptionDetail != null) {
				ConektaSubscription conektaSubscription = conektaSubscriptionDetail.getConektaSubscription();
				updatePaymentSubscriptionEvent(conektaSubscriptionDetail);
				if (conektaSubscription != null) {
					// ENVIO DE NOTIFICACION DE PAGO EXITOSO
					sendPaymentSuccesNotification(conektaSubscription.getConektaCustomer().getEmail(), conektaSubscription.getCellphoneNumber(), conektaSubscription.getConektaCustomer().getName());
					String altanOfferId = conektaSubscription.getAltanOfferId();
					String cellphoneNumber = conektaSubscription.getCellphoneNumber();
					String suscriptionInternalstatus = this.getSubscriptionInternalStatus(conektaSubscription.getConektaSubscriptionId());
					String currentConektaStatus = this.getContekaSubscriptionStatus(cellphoneNumber);
					logger.info("pago de suscripción en status  ->" + suscriptionInternalstatus + " para el número celular " +cellphoneNumber );
					switch (suscriptionInternalstatus) {
					case PaymentConstants.PENDING_FIRST_PAYMENT_INTERNAL_STATUS:
						logger.info("case PENDING_FIRST_PAYMENT_INTERNAL_STATUS  ->" + suscriptionInternalstatus + " para el número celular " +cellphoneNumber );
						doActionForFirstPaymentSuccess(cellphoneNumber, altanOfferId, conektaSubscription, currentConektaStatus);
						break;
					case SuscribersConstants.ALTAN_BARRING_STATUS:
						logger.info("case BARRING_STATUS  ->" + suscriptionInternalstatus + " para el número celular " +cellphoneNumber );
						this.doUnbarring(cellphoneNumber); // si estaba en BARRING se hace UNBARRING
						conektaSubscription.setInternalStatus(SuscribersConstants.ALTAN_ACTIVE_STATUS);
						conektaSubscription.setConektaStatus(currentConektaStatus);
						conektaSubscription.setUpdatedAt(new Date());
						conektaSubscriptionRepository.save(conektaSubscription);
						break;
					case SuscribersConstants.ALTAN_SUSPEND_STATUS:
						logger.info("case SUSPEND_STATUS  ->" + suscriptionInternalstatus + " para el número celular " +cellphoneNumber );
						this.doResume(cellphoneNumber); // si estaba en SUSPEND se hace RESUME
						conektaSubscription.setInternalStatus(SuscribersConstants.ALTAN_ACTIVE_STATUS);
						conektaSubscription.setConektaStatus(currentConektaStatus);
						conektaSubscription.setUpdatedAt(new Date());
						conektaSubscriptionRepository.save(conektaSubscription);
						break;
					default:
						logger.info("Es un pago de suscripción al corriente para el número celular " +cellphoneNumber );
					}
				}
			}
		} catch (Exception e) {
			logger.info("No se completo el proceso  SUBSCRIPTION_PAID " + e.getMessage());
		}
	}
	
	private void updatePaymentSubscriptionEvent(ConektaSubscriptionEvent conektaSubscriptionEvent) {
		Plan planSelected;
		try {
			planSelected = this.getPlanById(conektaSubscriptionEvent.getConektaPlanId());
			conektaSubscriptionEvent.setPaymentAmount(planSelected != null ? planSelected.amount : null);
			conektaSubscriptionEvent.setUpdatedAt(new Date());
			conektaSubscriptionDetailRepository.save(conektaSubscriptionEvent);
		} catch (Exception e) {
			logger.error("El monto del pago no pudo ser actualizado para " + conektaSubscriptionEvent.getConektaSubscription().getCellphoneNumber());
		} 
		
	}
	
	public void doActionForFirstPaymentSuccess(String cellphoneNumber, String altanOfferId,
			ConektaSubscription conektaSubscription, String currentConektaStatus) throws Exception {
		String altanOrderId = null;
		if (altanOfferId != null) {
			String internalOperation = conektaSubscription.getInternalOperation();
			switch (internalOperation) {
			case SuscribersConstants.ALTAN_ACTIVATION_OPERATION:
				logger.info("La oferta " + altanOfferId + " debe ser activada por primera vez para el número celular "
						+ cellphoneNumber);
				altanOrderId = this.activateSim(altanOfferId, conektaSubscription);// se ACTIVA el número en ALTAN
				 
				upadteConektaSubscription(conektaSubscription, altanOrderId, SuscribersConstants.ALTAN_ACTIVE_STATUS, currentConektaStatus, altanOfferId);
				break;
			case SuscribersConstants.ALTAN_UPDATE_OPERATION:
				logger.info("La oferta para el número celular " + cellphoneNumber + " debe ser actualizada a "
						+ altanOfferId);
				altanOrderId = this.replaceOffer(altanOfferId, conektaSubscription);// se CAMBIA LA OFERTA del número en
				upadteConektaSubscription(conektaSubscription, altanOrderId, SuscribersConstants.ALTAN_ACTIVE_STATUS, currentConektaStatus, altanOfferId);
				break;
			case SuscribersConstants.ALTAN_UNBARRING_OPERATION:
				altanOrderId = this.doUnbarring(cellphoneNumber); // se hace  UNBARRING
				upadteConektaSubscription(conektaSubscription, altanOrderId, SuscribersConstants.ALTAN_ACTIVE_STATUS, currentConektaStatus, altanOfferId);
				break;
			case SuscribersConstants.ALTAN_RESUME_OPERATION:
				altanOrderId = this.doResume(cellphoneNumber); // se hace RESUME
				upadteConektaSubscription(conektaSubscription, altanOrderId, SuscribersConstants.ALTAN_ACTIVE_STATUS, currentConektaStatus, altanOfferId);
				break;
			case SuscribersConstants.ALTAN_PURCHASE_OPERATION:
				altanOrderId = this.doPurchase(cellphoneNumber, conektaSubscription); // se hace PURCHASE SOLO pars MIFI(TEMPORAL)
				upadteConektaSubscription(conektaSubscription, altanOrderId, PaymentConstants.PENDING_FIRST_PAYMENT_INTERNAL_STATUS, currentConektaStatus, altanOfferId);
				break;
			case SuscribersConstants.ALTAN_NONE_OPERATION:
				conektaSubscription.setUpdatedAt(new Date());
				conektaSubscription.setInternalStatus(SuscribersConstants.ALTAN_ACTIVE_STATUS);
				conektaSubscription.setConektaStatus(currentConektaStatus);
				conektaSubscriptionRepository.save(conektaSubscription);
				logger.info("Actualizando altanOrderId e InternalStatus en Bd " + altanOrderId);
				sendCompleteSubscriptionNotification(conektaSubscription.getConektaCustomer().getEmail(), conektaSubscription.getCellphoneNumber(), conektaSubscription.getConektaCustomer().getName()); // ENVIO DE NOTIFICACION DE DOMICILIACIÓN COMPLETA
				break;
			}
		
		}
	}
	
	private void upadteConektaSubscription(ConektaSubscription conektaSubscription, String altanOrderId, String internalStatus, String conektaStatus,  String altanOfferId) {
		if (altanOrderId != null) {
			conektaSubscription.setAltanOrderId(altanOrderId);
			conektaSubscription.setUpdatedAt(new Date());
			conektaSubscription.setActivatedAltanAt(new Date());
			conektaSubscription.setAltanOfferId(altanOfferId);
			conektaSubscription.setInternalStatus(internalStatus);
			conektaSubscription.setConektaStatus(conektaStatus);
			conektaSubscriptionRepository.save(conektaSubscription);
			logger.info("Actualizando altanOrderId e InternalStatus en Bd " + altanOrderId);
			sendCompleteSubscriptionNotification(conektaSubscription.getConektaCustomer().getEmail(), conektaSubscription.getCellphoneNumber(), conektaSubscription.getConektaCustomer().getName()); // ENVIO DE NOTIFICACION DE DOMICILIACIÓN COMPLETA
		} else { // NO SE PUDO REALIZAR EXITOSAMENTE LA OPERACION DE ALTAN
			logger.info("Enviando notificacion de error en la domiciliacion al hacer update de oferta " + conektaSubscription.getCellphoneNumber());
			sendAltanErrorNotification(conektaSubscription.getConektaCustomer().getEmail(), conektaSubscription.getCellphoneNumber(), conektaSubscription.getConektaCustomer().getName());
		}
	}
	
	public void processSubscriptionPaymentFailedEvent(ConektaSubscriptionEvent conektaSubscriptionDetail) {
		logger.info("Procesando evento de fallo de cargo de suscripción");
		try {
			if (conektaSubscriptionDetail != null) {
				List<ConektaSubscriptionEvent> listado = conektaSubscriptionDetailRepository.findByConektaSubscriptionEventAndEventType(ConektaEventConstants.SUBSCRIPTION_PAYMENT_FAILED, conektaSubscriptionDetail.getConektaSubscription().getId());

				if(listado.size() == 2){
					
				ConektaSubscription conektaSubscription = conektaSubscriptionDetail.getConektaSubscription();
				if (conektaSubscription != null) {
					
					BinacleSubscription binacleSubscription = new BinacleSubscription(); 
					binacleSubscription.setCreateAt(new Date());					
					binacleSubscription.setPhone(conektaSubscriptionDetail.getConektaSubscription().getCellphoneNumber());
					
					
					String cellphoneNumber = conektaSubscription.getCellphoneNumber();
					String suscriptionInternalstatus = this.getSubscriptionInternalStatus(conektaSubscription.getConektaSubscriptionId());
					String conektaCurrentStatus = this.getContekaSubscriptionStatus(conektaSubscription.getCellphoneNumber());
					String status = "Webhook: "+ conektaSubscription.getConektaSubscriptionId() + " es "+ suscriptionInternalstatus; 
					logger.info("Estatus interno de la sucripción " + conektaSubscription.getConektaSubscriptionId() + " es "+ suscriptionInternalstatus);
					switch (suscriptionInternalstatus) {
					case SuscribersConstants.ALTAN_ACTIVE_STATUS:
					case PaymentConstants.PENDING_FIRST_PAYMENT_INTERNAL_STATUS:
						logger.info("Fallo de pago para  una suscripción en estatus ACTIVE   ->" + suscriptionInternalstatus + "  para el número celular " +cellphoneNumber ); 
						PerfilResponse perfilResponse = gategayApiXtraClient.getOfferSuperType(cellphoneNumber);
						if(perfilResponse != null) {
							String offerType = perfilResponse.getPerfil();
							logger.info("Super tipo de oferta " + offerType);
							if(offerType.equals(OfferConstans.MOV_SUPER_TYPE)) {
								this.doBarring(cellphoneNumber); // si es MOV hacer BARRING
								conektaSubscription.setInternalStatus(SuscribersConstants.ALTAN_BARRING_STATUS);
								status +=" dobarring";
							} else if(offerType.equals(OfferConstans.HBB_SUPER_TYPE) || offerType.equals(OfferConstans.MIFI_SUPER_TYPE)) {
								this.doSuspend(cellphoneNumber); // si es HBB ó MIFI hacer SUSPEND
								conektaSubscription.setInternalStatus(SuscribersConstants.ALTAN_SUSPEND_STATUS);
								status +=" dosuspend";
							}
							conektaSubscription.setConektaStatus(conektaCurrentStatus);
						}
						break;
					default:
						logger.info("Fallo de pago para  una suscripción en estatus -> " + suscriptionInternalstatus);
					}
					
					binacleSubscription.setInternalStatus(status);
					binacleSubscriptionRepository.save(binacleSubscription);
					
					conektaSubscriptionRepository.save(conektaSubscription);
					sendPaymentFailedNotification(conektaSubscription.getConektaCustomer().getEmail(), 
							conektaSubscription.getConektaCustomer().getName(), conektaSubscription.getCellphoneNumber());
				}
			  }
			}
		} catch (Exception e) {
			logger.info("No se completo el proceso del proceso SUBSCRIPTION_PAYMENT_FAILED" + e.getMessage());
		}
	}

	public void processSubscriptionCanceledEvent(ConektaSubscriptionEvent conektaSubscriptionEvent) {
		try {
			if (conektaSubscriptionEvent != null) {
				ConektaSubscription conektaSubscription = conektaSubscriptionEvent.getConektaSubscription();
				String conektaCurrentStatus = this.getContekaSubscriptionStatus(conektaSubscription.getCellphoneNumber());
				conektaSubscription.setInternalStatus(SuscribersConstants.CANCELLED_STATUS);
				conektaSubscription.setConektaStatus(conektaCurrentStatus);
				conektaSubscription.setUpdatedAt(new Date());
				conektaSubscriptionRepository.save(conektaSubscription);
			} else {
				logger.info("conektaSubscriptionDetail " + null);
			}
		} catch (Exception e) {
			logger.info("No se completo el proceso del proceso SUBSCRIPTION CANCELLED" + e.getMessage());
		}
	}
	
	/**
	 * Activa sim en altan
	 * 
	 * @param offerId
	 * @param cellphoneNumber
	 * @return
	 * @throws Exception
	 */
	public String activateSim(String offerId, ConektaSubscription conektaSubscription) throws Exception {
		String cellphoneNumber = conektaSubscription.getCellphoneNumber();
		logger.info("Activando en altan para " + cellphoneNumber);
		String activationOrderId = null;
		if (StringUtils.isNotBlank(offerId) && StringUtils.isNotBlank(cellphoneNumber)) {
			SubscribersActivateRequest subscriberActivateRequest = new SubscribersActivateRequest();
			subscriberActivateRequest.setOfferingId(offerId);
			if(conektaSubscription.isBelongsToHbb()) {  // Si es HBB enviar coordenadas
				subscriberActivateRequest.setAddress(conektaSubscription.getCoordinates());
			}
			AltanActionResponse altanActionResponse = gategayApiClient.activateMsisdn(cellphoneNumber, subscriberActivateRequest);// TODO
			activationOrderId = altanActionResponse.getAltanOrderId();
			altanActionRequestService.save(cellphoneNumber, altanActionResponse);
		} else {
			logger.error(
					"No se pudo realizar activacion,  offerId y cellphoneNumber son necesarios " + cellphoneNumber);
		}
		return activationOrderId;
	}
	
	public String replaceOffer(String offerId, ConektaSubscription conektaSubscription) throws Exception {
		String cellphoneNumber = conektaSubscription.getCellphoneNumber();
		logger.info("Remplazanado oferta en altan para el número celular" + cellphoneNumber);
		String replacementOrderId = null;
		if (StringUtils.isNotBlank(offerId) && StringUtils.isNotBlank(cellphoneNumber)) {
			UpdateOfferRequest updateOfferRequest = new UpdateOfferRequest();
			UpdateOffer primaryOffering = new UpdateOffer();
			primaryOffering.setOfferingId(offerId);
			if(conektaSubscription.isBelongsToHbb()) {  // Si es HBB enviar coordenadas
				primaryOffering.setAddress(conektaSubscription.getCoordinates());
			}
			updateOfferRequest.setPrimaryOffering(primaryOffering);
			AltanActionResponse altanActionResponse  = gategayApiClient.replaceOffer(cellphoneNumber, updateOfferRequest);
			replacementOrderId = altanActionResponse.getAltanOrderId();
			altanActionRequestService.save(cellphoneNumber, altanActionResponse);
		} else {
			logger.error(
					"No se pudo realizar el cambio de oferta,  offerId y cellphoneNumber son necesarios " + cellphoneNumber);
		}
		return replacementOrderId;
	}
	
	private String doBarring(String cellphoneNumber) {
		String altanOrderId = null;
		AltanActionResponse altanActionResponse = gategayApiClient.doBarring(cellphoneNumber);
		altanActionRequestService.save(cellphoneNumber, altanActionResponse);
		altanOrderId = altanActionResponse.getAltanOrderId();
		return altanOrderId;
	}
	
	private String doSuspend(String cellphoneNumber) {
		String altanOrderId = null;
		AltanActionResponse altanActionResponse = gategayApiClient.doSuspend(cellphoneNumber);
		altanActionRequestService.save(cellphoneNumber, altanActionResponse);
		altanOrderId = altanActionResponse.getAltanOrderId();
		return altanOrderId;
	}
	
	private String doUnbarring(String cellphoneNumber) {
		String altanOrderId = null;
		AltanActionResponse altanActionResponse = gategayApiClient.doUnbarring(cellphoneNumber);
		altanActionRequestService.save(cellphoneNumber, altanActionResponse);
		altanOrderId = altanActionResponse.getAltanOrderId();
		return altanOrderId;
	}
	
	private String doResume(String cellphoneNumber) {
		String altanOrderId = null;
		AltanActionResponse altanActionResponse = gategayApiClient.doResume(cellphoneNumber);
		altanActionRequestService.save(cellphoneNumber, altanActionResponse);
		altanOrderId = altanActionResponse.getAltanOrderId();
		return altanOrderId;
	}
	
	private String doPurchase(String cellphoneNumber, ConektaSubscription conektaSubscription ) {
		logger.info("Haciendo recarga de oferta " + conektaSubscription.getAltanOfferId() + " en altan para el número celular " + cellphoneNumber);
		String altanOrderId = null;
		if (StringUtils.isNotBlank(conektaSubscription.getAltanOfferId()) && StringUtils.isNotBlank(cellphoneNumber)) {
			String offerId = conektaSubscription.getAltanOfferId(); 
			PurchaseRequest purchaseRequest = new PurchaseRequest();
			List<String> offerings = new ArrayList<String>();
			offerings.add(offerId);
			purchaseRequest.setOfferings(offerings);
			purchaseRequest.setBeId(beId);
			purchaseRequest.setMsisdn(cellphoneNumber);
			PurchaseResponse purchaseResponse  = gategayApiClient.purchase(purchaseRequest); 
			if(purchaseResponse != null) {
				AltanActionResponse altanActionResponse = purchaseResponse.getAltanActionResponse();
				altanOrderId = altanActionResponse.getAltanOrderId();
				altanActionRequestService.save(cellphoneNumber, altanActionResponse);
				altanOrderId = altanActionResponse.getAltanOrderId();
				conektaSubscription.setUpdatedAt(new Date());
				conektaSubscriptionRepository.save(conektaSubscription);
			}
			
		} else {
			logger.error(
					"No se pudo realizar la recarga  de la  oferta,  offerId y cellphoneNumber son necesarios " + cellphoneNumber);
		}
		return altanOrderId;
	}

	/**
	 * Crea objeto ConektaCustomer para poder persistir en bd
	 * 
	 * @param customer
	 * @return
	 */
	private ConektaCustomer createConektaCustomerObject(Customer customer) {
		ConektaCustomer conektaCustomer = new ConektaCustomer();
		conektaCustomer.setActive(true);
		conektaCustomer.setConektaCreatedAt(customer.created_at);
		conektaCustomer.setCreatedAt(new Date());
		conektaCustomer.setConektaCustomerId(customer.getId());
		conektaCustomer.setEmail(customer.email);
		conektaCustomer.setName(customer.name);
		conektaCustomer.setPhone(customer.phone);
		return conektaCustomer;
	}

	/**
	 * Crea objeto ConektaSubscription para poder persistir en bd
	 * 
	 * @param subscription
	 * @param activationOrderId
	 * @return
	 * @throws Exception 
	 */
	private ConektaSubscription createConektaSubscriptionObject(Subscription subscription, boolean belongsToHhb, String coordinates, String  InternalOperation) throws Exception {
		ConektaSubscription conektaSubscription = new ConektaSubscription();
		String cellphoneNumber =(subscription.customer.phone).substring(2);
		conektaSubscription.setActive(true);
		conektaSubscription.setCardId(subscription.card_id);
		conektaSubscription.setConektaCreatedAt(subscription.created_at);
		conektaSubscription.setCreatedAt(new Date());
		conektaSubscription.setConektaPlanId(subscription.plan_id);
		conektaSubscription.setInternalStatus(PaymentConstants.PENDING_FIRST_PAYMENT_INTERNAL_STATUS);
		conektaSubscription.setConektaSubscriptionId(subscription.id);
		conektaSubscription.setConektaStatus(subscription.status);
		conektaSubscription.setCellphoneNumber(cellphoneNumber);
		conektaSubscription.setBelongsToHbb(belongsToHhb);
		conektaSubscription.setCoordinates(coordinates);
		conektaSubscription.setInternalOperation(InternalOperation);
		conektaSubscription.setAltanOfferId(this.getAltanOfferId(subscription.plan_id));
		conektaSubscription.setSuperOfferType(profileService.getSuperOfferType(cellphoneNumber));
		return conektaSubscription;
	}

	/**
	 * Crear objeto ConektaSubscriptionDetail para persistir en bd
	 * 
	 * @param subscriptionEvent
	 * @param conektaSubscription
	 * @param event
	 * @return conektaSubscriptionDetail
	 */
	private ConektaSubscriptionEvent createConektaSubscriptionDetObject(SubscriptionEvent subscriptionEvent,
			ConektaSubscription conektaSubscription, String event) {
		ConektaSubscriptionEvent conektaSubscriptionDetail = new ConektaSubscriptionEvent();
		conektaSubscriptionDetail.setActive(true);
		conektaSubscriptionDetail.setConektaCreatedAt(Integer.valueOf(subscriptionEvent.getCreated_at()));
		conektaSubscriptionDetail.setConektaSubscription(conektaSubscription);
		conektaSubscriptionDetail.setCreatedAt(new Date());
		conektaSubscriptionDetail.setEventType(event);
		conektaSubscriptionDetail.setConektaPlanId(subscriptionEvent.getPlan_id());
		if (subscriptionEvent.getSubscription_start() != null) {
			conektaSubscriptionDetail.setSubscriptionStart(Integer.valueOf(subscriptionEvent.getSubscription_start()));
		}
		if (subscriptionEvent.getCanceled_at() != null) {
			conektaSubscriptionDetail.setCanceledAt(Integer.valueOf(subscriptionEvent.getCanceled_at()));
		}
		if (subscriptionEvent.getPaused_at() != null) {
			conektaSubscriptionDetail.setPausedAt(Integer.valueOf(subscriptionEvent.getPaused_at()));
		}
		if (subscriptionEvent.getBilling_cycle_start() != null) {
			conektaSubscriptionDetail.setBillingCycleStart(Integer.valueOf(subscriptionEvent.getBilling_cycle_start()));
		}
		if (subscriptionEvent.getBilling_cycle_end() != null) {
			conektaSubscriptionDetail.setBillingCycleEnd(Integer.valueOf(subscriptionEvent.getBilling_cycle_end()));
		}
		if (subscriptionEvent.getCanceled_at() != null) {
			conektaSubscriptionDetail.setCanceledAt(Integer.valueOf(subscriptionEvent.getCanceled_at()));
		}
		return conektaSubscriptionDetail;
	}

	private JSONObject createJSONObjectCustomer(SubscriptionModel suscriptionModel) throws Error, ErrorList {
		Plan planSelected = this.getPlanById(suscriptionModel.getPlanSelectedId());
		JSONObject jSONObjectCustomer = new JSONObject("{" + "'name': '" + suscriptionModel.getCardTitularName() + "', "
				+ "'email': '" + suscriptionModel.getEmail() + "', " + "'phone': '" + mxCountryCode
				+ suscriptionModel.getCellphoneNumber() + "', " + "'metadata': {'description': '" + planSelected.name
				+ ":" + planSelected.amount + "' , 'reference' : '" + planSelected.id + "'}," + "'payment_sources':[{"
				+ "'type': 'card'," + "'token_id': '" + suscriptionModel.getTokenId() + "'" + "}]" + "}");
		return jSONObjectCustomer;
	}

	private PlanModel findOfferInList(List<PlanModel> validUserPlans, String offerIdStr) {
		Optional<PlanModel> conektaPlan = null;
		if (StringUtils.isNotBlank(offerIdStr)) {
			conektaPlan = validUserPlans.stream().filter(s -> offerIdStr.equals(s.getId())).findFirst();
		}
		return conektaPlan.isPresent() ? conektaPlan.get() : null;
	}

	private String getSubscriptionInternalStatus(String conektaSubscriptionId) {
		String status = null;
		ConektaSubscription conektaSubscription = conektaSubscriptionRepository
				.findByConektaSubscriptionId(conektaSubscriptionId);
		if (conektaSubscription != null) {
			status = conektaSubscription.getInternalStatus();
		}
		return status;
	}

	/**
	 * Valida si ya existe suscripción para el número celular ingresado
	 * @param cellphoneNumber
	 * @return
	 * @throws TrException
	 */
	private boolean isSubscribed(String cellphoneNumber) throws TrException {
		boolean isSubscribed = false;
		try {
			String cellphoneNumberWithCountryCode = mxCountryCode + cellphoneNumber; // TODO USAR ESTE
			Customer customer = conektaClient.findCustomerByCellphoneNumber(cellphoneNumberWithCountryCode);
			if(customer != null) {
				if(customer.subscription != null) {
					String subscriptionStatus = customer.subscription.status;
					if(subscriptionStatus != null && subscriptionStatus.equals(PaymentConstants.CONEKTA_SUBSCRIPTION_ACTIVE_STATUS)) {
						throw new TrException("Ya existe una suscripción para el número celular ingresado.");
					}
				}
			}
		} catch (TrException e) {
			throw new TrException(e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new TrException("El número celular ingresado no pudo ser validado,  intente nuevamente más tarde.");
		}
		return isSubscribed;
	}
	
	private List<PlanModel> getReplacementOffers(String cellphoneNumber, String product, String offer) throws TrException {
		List<PlanModel> conektaPlanModels = null;
		if(StringUtils.isNotBlank(cellphoneNumber)) {
			OfertasReemplazoResponse replacementOffersResponse = gategayApiXtraClient.getReplacementOffers(cellphoneNumber, product, offer);
			List<OfertasReemplazo> replacementOffers = replacementOffersResponse.getOfertasList();
			if(replacementOffers != null && !replacementOffers.isEmpty()) {
				conektaPlanModels = new ArrayList<PlanModel>();
				for(OfertasReemplazo ofertasReemplazo: replacementOffers) {
					String conektaOfferId = ofertasReemplazo.getPlanConekta();
					try {
						Plan conektaPlan = conektaClient.getPlanById(conektaOfferId);
						if(conektaPlan != null) {
							PlanModel conektaPlanModel = new PlanModel();
							conektaPlanModel.setId(conektaPlan.id);
							conektaPlanModel.setName(conektaPlan.name);
							conektaPlanModel.setAmount(conektaPlan.amount);
							conektaPlanModels.add(conektaPlanModel);
						} else {
							logger.error("OfferId " + conektaOfferId + " no se encontro en conekta");
						}
					} catch (Exception e) {
						logger.error("OfferId " + conektaOfferId + " no pudo ser consultado en conekta" );
					}
				}
			} else {
				throw new TrException("No se encontraron ofertas disponibles para el número ingresado, comunicate al call center.");
			}
		} else {
			logger.error("Numero de celular es requerido para consultar ofertas para reemplazo");
		}
		return conektaPlanModels;
	}
	
	private Boolean isOfferExpired(Date  expireDate) throws TrException {
		Boolean isExpired = null;
		if(expireDate != null) {
			Date todayDate = new Date();
			if (expireDate.after(todayDate)) {
				isExpired = false;
			} else {
				isExpired = true;
			}
		} else {
			throw new TrException("La fecha de expiración no pudo ser validad.");
		}
		return isExpired;
	}
	
	/**
	 * Envía notificación de suscripción inicada
	 * @param email
	 * @param customerName
	 * @throws MessagingException
	 * @throws IOException
	 * @throws TrException
	 */
	public void sendSuscriptionBeginNotification(String email, String cellphoneNumber, String customerName) {
		if(StringUtils.isNotBlank(email)) {
			try {
				String message = "Domiciliación  para número celular " +cellphoneNumber + " esta en proceso.";
				emailService.sendSimpleAutamaticFormatNotification(customerName,  cellphoneNumber, email, "Domiciliación en proceso", message, null);
			} catch (Exception e) {
				logger.info("La notificación de proceso de domiciliación iniciada " +cellphoneNumber +" no pudo ser enviada por correo a " + email
						+" Error msg " + e.getMessage());
			}
		} else {
			logger.info("Es requerido el email para enviar un correo " + email);
		}
	}
	
	/**
	 * Envía notificación de fallo al realizar cargo de suscripción
	 * @param email
	 * @param customerName
	 * @throws MessagingException
	 * @throws IOException
	 * @throws TrException
	 */
	private void sendPaymentFailedNotification(String email, String customerName, String cellphoneNumber)
			throws MessagingException, IOException, TrException {
		logger.info("Enviando correo de problema de cargo a " + email);
		if (StringUtils.isNotBlank(email)) {
			try {
				String message = "Lamentablemente no hemos podido procesar tu último pago  del número asociado " + cellphoneNumber 
						+ "		¡No te preocupes! Comprueba tus datos de pago, eso suele\r\n" 
						+ "			solucionar el problema\r\n" 
						+ "		y podrás seguir con el servicio."; 
				emailService.sendSimpleAutamaticFormatNotification(customerName,  cellphoneNumber, email, "No hemos podido procesar tu pago para el número asociado ", message, null);
			} catch (Exception e) {
				logger.error("La notificación de pago exitoso de " +cellphoneNumber +" no pudo ser enviada por correo a " + email
						+" Error msg " + e.getMessage());
			}
		} else {
			logger.info("Es requerido el email para enviar un correo " + email);
		}
	}
	
	/**
	 * Envía notificación de exito al realizar cargo de sucripción
	 * @param email
	 * @param customerName
	 * @throws MessagingException
	 * @throws IOException
	 * @throws TrException
	 */
	public void sendPaymentSuccesNotification(String email, String cellphoneNumber, String customerName) {
		if(StringUtils.isNotBlank(email)) {
			try {
				String message = "Su pago del servicio de domiciliación  del número asociado " +cellphoneNumber + " ha sido realizado exitosamente.";
				emailService.sendSimpleAutamaticFormatNotification(customerName,  cellphoneNumber, email, "Pago exitoso", message, null);
			} catch (Exception e) {
				logger.error("La notificación de pago exitoso de " +cellphoneNumber +" no pudo ser enviada por correo a " + email
						+" Error msg " + e.getMessage());
			}
		} else {
			logger.info("Es requerido el email para enviar un correo " + email);
		}
	}
	
	
	/**
	 * Envía notificación de suscripción completada
	 * @param email
	 * @param customerName
	 * @throws MessagingException
	 * @throws IOException
	 * @throws TrException
	 */
	public void sendCompleteSubscriptionNotification(String email, String cellphoneNumber, String customerName) {
		if(StringUtils.isNotBlank(email)) {
			try {
				String message = "El proceso de domiciliación  para número asociado " +cellphoneNumber + " ha sido completado exitosamente.";
				emailService.sendSimpleAutamaticFormatNotification(customerName,  cellphoneNumber, email, "Domiciliación completada", message, null);
			} catch (Exception e) {
				logger.info("La notificación de proceso de domiciliación completa " +cellphoneNumber +" no pudo ser enviada por correo a " + email
						+" Error msg " + e.getMessage());
			}
		}
	}
	
	/**
	 * Envía notificación de error al realizar la operacion con altan
	 * @param email
	 * @param customerName
	 * @throws MessagingException
	 * @throws IOException
	 * @throws TrException
	 */
	public void sendAltanErrorNotification(String email, String cellphoneNumber, String customerName) {
		if(StringUtils.isNotBlank(email)) {
			try {
				String message = "El proceso de domiciliación  para número asociado " +cellphoneNumber + " no pudo ser completado, comunicate al call center para completar el proceso " + callCenter + ".";
				emailService.sendSimpleAutamaticFormatNotification(customerName,  cellphoneNumber, email, "Domiciliación error", message, null);
			} catch (Exception e) {
				logger.info("La notificación de proceso de domiciliación no pudo ser completado " +cellphoneNumber +" no pudo ser enviada por correo a " + email
						+" Error msg " + e.getMessage());
			}
		}
	}
	
	private String getContekaSubscriptionStatus(String cellphoneNumber){
		logger.info("Conusltando estatus de suscripcion en conekta para el numero " + cellphoneNumber);
		String conektaStatus = "";
		Subscription subscription = conektaClient.findSubscription( mxCountryCode + cellphoneNumber);
			if(subscription != null) {
				logger.info("conektaStatus  para el numero " + cellphoneNumber + ": " + conektaStatus ); 
				conektaStatus = subscription.status;
			} else {
				logger.info("Estatus no se pudo obtener, subscription null para el numero " + cellphoneNumber );
				conektaStatus = SuscribersConstants.INCORRECT_STATUS;
			}
		return conektaStatus;
	}
	
}
