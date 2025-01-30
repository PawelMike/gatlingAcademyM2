package gatlingdemostore.pageobjects;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;
import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

public final class Checkout {
    public static final ChainBuilder viewCart = 
        doIf(session -> !session.getBoolean("customerLoggedIn"))
            .then(exec(Customer.login))
        .exec(http("Load Cart Page")
            .get("/cart/view")
            .check(css("#grandTotal").isEL("$#{cartTotal}")));
    
    public static final ChainBuilder completeCheckout =
        exec(http("Checkout Cart Page")
            .get("/cart/checkout")
            .check(substring("Thanks for your order! See you soon!")));
}