package in.sriharilabs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.documentdb.ConnectionPolicy;
import com.microsoft.azure.documentdb.ConsistencyLevel;
import com.microsoft.azure.documentdb.DocumentClient;
import com.microsoft.azure.documentdb.RetryOptions;
import com.microsoft.azure.spring.data.cosmosdb.Constants;
import com.microsoft.azure.spring.data.cosmosdb.DocumentDbFactory;
import com.microsoft.azure.spring.data.cosmosdb.common.TelemetryProxy;
import com.microsoft.azure.spring.data.cosmosdb.config.DocumentDbConfigurationSupport;
import com.microsoft.azure.spring.data.cosmosdb.core.DocumentDbTemplate;
import com.microsoft.azure.spring.data.cosmosdb.core.convert.MappingDocumentDbConverter;
import com.microsoft.azure.spring.data.cosmosdb.repository.config.EnableDocumentDbRepositories;

@Configuration
@EnableDocumentDbRepositories
// @EnableConfigurationProperties(DocumentDbProperties.class)
// @PropertySource("classpath:application.properties")
public class CosmoDbConfig extends DocumentDbConfigurationSupport {

	// @Autowired
	// private DocumentDbProperties properties;

	@Value("${azure.cosmosdb.uri}")
	String uri;
	@Value("${azure.cosmosdb.key}")
	String key;
	@Value("${azure.cosmosdb.database}")
	String db;

	@Bean
	public RetryOptions retryOptions() {
		RetryOptions retryOptions = new RetryOptions();
		retryOptions.setMaxRetryWaitTimeInSeconds(120);
		retryOptions.setMaxRetryAttemptsOnThrottledRequests(100);
		return retryOptions;
	}

	@Bean
	public ConnectionPolicy connectionPolicy() {
		ConnectionPolicy connectionPolicy = new ConnectionPolicy();
		connectionPolicy.setRetryOptions(retryOptions());
		connectionPolicy.setMaxPoolSize(10);
		connectionPolicy.setIdleConnectionTimeout(10);
		connectionPolicy.setMediaRequestTimeout(10);
		connectionPolicy.setEnableEndpointDiscovery(false);
		return connectionPolicy;
	}

	@Bean
	public DocumentClient documentClient() {
		this.telemetryProxy = new TelemetryProxy(true);
		this.telemetryProxy.trackCustomEvent(this.getClass());

		System.out.println("G Srihari...." + uri);
		return new DocumentClient(uri, key, connectionPolicy(), ConsistencyLevel.Session);
	}

	@Qualifier(Constants.OBJECTMAPPER_BEAN_NAME)
	@Autowired(required = false)
	private ObjectMapper objectMapper;

	@Bean
	public DocumentDbFactory documentDbFactory() {
		return new DocumentDbFactory(documentClient());
	}

	@Bean
	public DocumentDbTemplate documentDbTemplate() throws ClassNotFoundException {
		return new DocumentDbTemplate(documentDbFactory(), mappingDocumentDbConverter(), db);
	}

	@Bean
	public MappingDocumentDbConverter mappingDocumentDbConverter() throws ClassNotFoundException {
		return new MappingDocumentDbConverter(this.documentDbMappingContext(), objectMapper);
	}
}
