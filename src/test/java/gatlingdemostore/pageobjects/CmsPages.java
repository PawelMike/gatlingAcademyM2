package gatlingdemostore.pageobjects;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;
import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

public final class CmsPages {

    public static final ChainBuilder homepage =
        exec(
            http("Load Home Page")
                .get("/")
                .check(regex("<title>Gatling Demo-Store</title>").exists())
                .check(css("#_csrf", "content").saveAs("csrfValue")));

    public static final ChainBuilder aboutUs =
        exec(http("Load About Us Page").get("/about-us").check(substring("About Us")));
}