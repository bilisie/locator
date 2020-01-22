package com.bilisie;

import com.ApiClient;
import com.Configuration;
import com.bilisie.locator.service.Locator;
import com.bilisie.locator.service.ServerInfo;
import com.google.common.io.Closer;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import static java.lang.String.format;

@SuppressWarnings("UnstableApiUsage")
class LocatorInMemoryBooter {

    private final LocatorApi locatorApi;

    LocatorInMemoryBooter(Closer closer, String dynamoEndpoint) {
        String[] options = new String[]{"--server.port=0", format("--aws.dynamo.endpoint=%s", dynamoEndpoint)};
        updateCredentials();
        ConfigurableApplicationContext context = SpringApplication.run(Locator.class, options);
        int port = context.getBeanFactory().getBean(ServerInfo.class).getServer().getWebServer().getPort();
        this.locatorApi = getClient(port);
        closer.register(context::stop);
    }

    LocatorApi getClient() {
        return locatorApi;
    }

    private LocatorApi getClient(int port) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath(format("http://localhost:%s", port));
        return new LocatorApi(defaultClient);
    }

    private void updateCredentials() {
        System.setProperty("aws.accessKeyId", "none");
        System.setProperty("aws.secretKey", "none");
        System.setProperty("aws.sessionToken", "none");
    }


}
