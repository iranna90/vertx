import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Utils {


  public static final Set<String> UNAUTHORIZED_PATHS = new HashSet<>(Arrays.asList(
      "/docs/v1/version",
      "/docs/v1/health/alive", "/docs/v1/health/ready", "/docs/v1/health/status",
      "/docs/login", "/docs/logout", "/docs/callback"
  ));
}
