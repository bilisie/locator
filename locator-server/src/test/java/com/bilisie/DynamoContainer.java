package com.bilisie;

import com.google.common.io.Closer;
import org.testcontainers.containers.GenericContainer;

import static java.lang.String.*;

@SuppressWarnings("UnstableApiUsage")
class DynamoContainer {

    private static final int DYNAMO_PORT = 8000;
    private static final String IMAGE = "amazon/dynamodb-local:1.11.119";

    private final GenericContainer container = new GenericContainer(IMAGE).withExposedPorts(DYNAMO_PORT);

    DynamoContainer(Closer closer) {
        container.start();
        closer.register(container::close);
    }

    String getEndpoint() {
        return format("http://%s:%s", container.getContainerIpAddress(),container.getMappedPort(DYNAMO_PORT));
    }

}
