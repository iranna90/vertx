import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.impl.StaticHandlerImpl;

public class DocsPortalPageHandler extends StaticHandlerImpl {

  @Override
  public void handle(RoutingContext context) {
    if (Utils.UNAUTHORIZED_PATHS.contains(context.request().path())) {
      context.next();
    } else {
      super.handle(context);
    }
  }
}
