package com.manwon.happiness;

import io.sentry.Sentry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ManwonHappinessBackendApplication {

	public static void main(String[] args) {

        Sentry.init(options -> {
            options.setDsn("https://731854fba29d4c86a99fa72ee8d3a4e2@o1092570.ingest.us.sentry.io/6111069");

            // Add data like request headers and IP for users,
            // see https://docs.sentry.io/platforms/java/data-management/data-collected/ for more info
            options.setSendDefaultPii(true);
            // When first trying Sentry it's good to see what the SDK is doing:
            options.setDebug(true);
        });

        SpringApplication.run(ManwonHappinessBackendApplication.class, args);

	}

}
