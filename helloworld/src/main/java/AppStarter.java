import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Cookie;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.CookieHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;

public class AppStarter {

  private static final String NAME = "actk";
  private static final String ROOT_PATH = "/docs/";

  private static final Set<String> UNAUTHORIZED_PATHS = new HashSet<>(Arrays.asList(
      "/docs/v1/version",
      "/docs/v1/health/alive", "/docs/v1/health/ready", "/docs/v1/health/status",
      "/docs/login", "/docs/logout", "/docs/callback"
  ));

  public static void main(String... args) {
    final Vertx vertx = Vertx.vertx();
    final HttpServer server = vertx.createHttpServer();

    // router
    Router router = Router.router(vertx);

    // register cookie
    router.route().handler(CookieHandler.create());
    router.route().handler(SessionHandler.create(LocalSessionStore.create(vertx)));

    // protect docs resource
    final AuthenticationProvider authenticationProvider = new AuthenticationProvider();
    // AuthHandler authHandler = RedirectAuthHandler.create(authenticationProvider);

    router.get("/*").handler(rc -> {
      final Cookie cookie = rc.getCookie(NAME);
      final String path = rc.request().path();
      if (UNAUTHORIZED_PATHS.contains(path)) {
        rc.next();
      } else {
        if (cookie == null) {
          rc.reroute("/docs/login");
        } else {
          if (path.equals(ROOT_PATH)) {
            rc.reroute("/docs/index.html");
          } else {
            StaticHandler.create().handle(rc);
          }
        }
      }
    });

    // login
    router.get("/docs/login").handler(rc -> {
      final String uri = authenticationProvider.authorize();
      rc.response().putHeader("location", uri).setStatusCode(302).end();
    });

    router.get("/docs/callback").handler(rc -> {
      final List<String> codes = rc.queryParam("code");
      final String token = authenticationProvider.token(codes.get(0));
      System.out.println(token);
      Cookie cookie = Cookie.cookie(NAME, token).setHttpOnly(true).setSecure(false);
      rc.addCookie(cookie);
      rc.response().putHeader("location", "/docs/index.html").setStatusCode(302).end();
    });

    server.requestHandler(router::accept).listen(8080);
  }

}
