package gatlingdemostore.pageobjects;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;
import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

public final class Customer {

    private static final FeederBuilder<String> csvFeederLoginDetails =
        csv("data/loginDetails.csv").circular();

    public static final ChainBuilder login =
        feed(csvFeederLoginDetails)
            .exec(http("Load Login Page")
            .get("/login")
            .check(substring("Username:")))
            .exec(
            http("Customer Login Action")
                .post("/login")
                .formParam("_csrf", "#{csrfValue}")
                .formParam("username", "#{username}")
                .formParam("password", "#{password}"))
            .exec(session -> session.set("customerLoggedIn", true));
}