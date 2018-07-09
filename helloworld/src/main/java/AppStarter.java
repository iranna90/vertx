import java.util.Optional;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Cookie;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.CookieHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;

public class AppStarter {

  private static final String NAME = "actk";
  private static final String ROOT_PATH = "/docs/";
  private static final boolean RUN_PARALLER = false;

  // protect docs resource
  private static final AuthenticationProvider authenticationProvider = new AuthenticationProvider();
  private static final String login = "/docs/login";
  private static final String callbackUrl = "/docs/callback";

  public static void main(String... args) {
    final Vertx vertx = Vertx.vertx();
    final HttpServer server = vertx.createHttpServer();

    // router
    Router router = Router.router(vertx);

    // register cookie
    router.route().handler(CookieHandler.create());
    router.route().handler(SessionHandler.create(LocalSessionStore.create(vertx)));
    router.get("/*").handler(tokenVerifierHandler());
    router.get("/docs/*").handler(new DocsPortalPageHandler());
    router.get(login).blockingHandler(loginHandler(), RUN_PARALLER);
    router.get(callbackUrl).blockingHandler(tokenExchangeHandler(), RUN_PARALLER);
    server.requestHandler(router::accept).listen(8080);
  }

  private static Handler<RoutingContext> tokenExchangeHandler() {
    return rc -> {

      final Optional<String> authorizationCode = rc.queryParam("code").stream().findFirst();
      if (!authorizationCode.isPresent()) {
        rc.reroute(login);
      }

      final String token = authenticationProvider.token(authorizationCode.get());
      Cookie cookie = Cookie.cookie(NAME, token).setHttpOnly(true).setSecure(false);
      rc.addCookie(cookie);
      rc.response().putHeader("location", "/docs/index.html").setStatusCode(302).end();
    };
  }

  private static Handler<RoutingContext> loginHandler() {
    return rc -> {
      final String uri = authenticationProvider.authorize();
      rc.response().putHeader("location", uri).setStatusCode(302).end();
    };
  }

  private static Handler<RoutingContext> tokenVerifierHandler() {
    return rc -> {
      final Cookie cookie = rc.getCookie(NAME);
      final String path = rc.request().path();
      if (Utils.UNAUTHORIZED_PATHS.contains(path)) {
        rc.next();
      } else {
        securedAccess(rc, cookie, path);
      }
    };
  }

  private static void securedAccess(final RoutingContext rc, final Cookie cookie, final String path) {
    if (cookie == null) {
      rc.reroute("/docs/login");
    } else {
      if (path.equals(ROOT_PATH)) {
        rc.reroute("/docs/index.html");
      } else {
        rc.next();
      }
    }
  }

}
