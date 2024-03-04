package com.topostechnology.rest.client;

import java.net.URI;

import javax.net.ssl.SSLContext;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
@Service
public class BaseApiClient {
	
	
	@Value("${http.request.connection.timeout}")
	private int httpRquestTimeout;
;
	
	public RestTemplate getRestTemplateJsonGET() throws Exception {
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestWithBodyFactory();
		requestFactory.setReadTimeout(httpRquestTimeout);
		requestFactory.setConnectTimeout(httpRquestTimeout);
		return this.getRestTemplate(requestFactory);
	}

	public RestTemplate getRestTemplate() throws Exception {
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		requestFactory.setReadTimeout(httpRquestTimeout);
		requestFactory.setConnectTimeout(httpRquestTimeout);
		return this.getRestTemplate(requestFactory);
	}

	public RestTemplate getRestTemplate(HttpComponentsClientHttpRequestFactory requestFactory) throws Exception {
		RestTemplate custRestTemplate = new RestTemplate();
		CloseableHttpClient httpClient = this.getCloseableHttpClient();
		requestFactory.setHttpClient(httpClient);
		custRestTemplate.setRequestFactory(requestFactory);
		return custRestTemplate;
	}

	public CloseableHttpClient getCloseableHttpClient() throws Exception {
		CloseableHttpClient httpClient = null;
		TrustStrategy acceptingTrustStrategy = new TrustStrategy() {
			@Override
			public boolean isTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) {
				return true;
			}
		};
		SSLContext sslContext = null;
		sslContext = org.apache.http.ssl.SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
		SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext, new NoopHostnameVerifier());
		httpClient = HttpClients.custom().setSSLSocketFactory(csf).build();
		return httpClient;
	}
}

final class HttpComponentsClientHttpRequestWithBodyFactory extends HttpComponentsClientHttpRequestFactory {
	@Override
	protected HttpUriRequest createHttpUriRequest(HttpMethod httpMethod, URI uri) {
		if (httpMethod == HttpMethod.GET) {
			return new HttpGetRequestWithEntity(uri);
		}
		return super.createHttpUriRequest(httpMethod, uri);
	}
}

final class HttpGetRequestWithEntity extends HttpEntityEnclosingRequestBase {
	public HttpGetRequestWithEntity(final URI uri) {
		super.setURI(uri);
	}

	@Override
	public String getMethod() {
		return HttpMethod.GET.name();
	}
}

	
