package gatlingdemostore;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;

import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

public class DemostoreSimulation extends Simulation {

  private static final String DOMAIN = "demostore.gatling.io";
  private static final HttpProtocolBuilder HTTP_PROTOCOL = http.baseUrl("https://" + DOMAIN);

  private static final FeederBuilder<String> categoryFeeder =
      csv("data/categoryDetails.csv").random();

  private static final FeederBuilder<Object> jsonFeederProducts = 
      jsonFile("data/productDetails.json").random();

  private static class CmsPages {

    private static final ChainBuilder homepage =
        exec(
            http("Load Home Page")
                .get("/")
                .check(regex("<title>Gatling Demo-Store</title>").exists())
                .check(css("#_csrf", "content").saveAs("csrfValue")));

    private static final ChainBuilder aboutUs =
        exec(http("Load About Us Page").get("/about-us").check(substring("About Us")));
  }

  private static class Catalog {
    private static class Category {
        private static final ChainBuilder view = feed(categoryFeeder)
            .exec(
                http("Load Category Page - #{categoryName}")
                    .get("/category/#{categorySlug}")
                    .check(css("#categoryName").isEL("#{CategoryName}")));
    }

    private static class Product {
      private static final ChainBuilder view =
        feed(jsonFeederProducts)
          .exec(
            http("Load Product Page - #{name}")
              .get("/product/#{slug}")
              .check(css("#ProductDescription").isEL("#{#description}")));
    }
  }

  private static final ScenarioBuilder scn =
      scenario("DemostoreSimulation")
          .exec(CmsPages.homepage)
          .pause(2)
          .exec(CmsPages.aboutUs)
          .pause(2)
          .exec(Catalog.Category.view)
          .pause(2)
          .exec(Catalog.Product.view)
          .pause(2)
          .exec(http("Add Product to Cart").get("/cart/add/19"))
          .pause(2)
          .exec(http("View Cart").get("/cart/view"))
          .pause(2)
          .exec(
              http("Login User")
                  .post("/login")
                  .formParam("_csrf", "#{csrfValue}")
                  .formParam("username", "user1")
                  .formParam("password", "pass"))
          .pause(2)
          .exec(http("Checkout").get("/cart/checkout"));

  {
    setUp(scn.injectOpen(atOnceUsers(1))).protocols(HTTP_PROTOCOL);
  }
}