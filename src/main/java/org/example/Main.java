package org.example;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.exporter.prometheus.PrometheusHttpServer;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.semconv.ResourceAttributes;

import java.sql.SQLException;

public class Main {

    private static final PrometheusHttpServer promServer = buildPromServer();

    public static void main(String[] args) throws SQLException, InterruptedException {
        System.out.println("Hello world!");

        buildAndRegisterGlobalOtel();

        var trinoStuff = new TrinoStuff();
        trinoStuff.doSomething();

        // Closing stuff
        promServer.close();
    }

    private static PrometheusHttpServer buildPromServer() {
        return PrometheusHttpServer.builder().setPort(19000).build();
    }

    private static OpenTelemetry buildAndRegisterGlobalOtel() {
        Resource myAppResource = Resource.getDefault().merge(Resource.create(Attributes.of(ResourceAttributes.SERVICE_NAME, "my-app")));

        SdkMeterProvider meterProvider = SdkMeterProvider.builder()
                .setResource(myAppResource)
                .registerMetricReader(promServer)
                .build();

        OpenTelemetrySdk openTelemetrySdk = OpenTelemetrySdk.builder().setMeterProvider(meterProvider).buildAndRegisterGlobal();

        return openTelemetrySdk;
    }

}