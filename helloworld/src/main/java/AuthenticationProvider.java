import com.auth0.client.auth.AuthAPI;
import com.auth0.exception.Auth0Exception;
import com.auth0.json.auth.TokenHolder;
import com.auth0.net.AuthRequest;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.auth.User;

public class AuthenticationProvider implements AuthProvider {

  AuthAPI auth = new AuthAPI("irdeto-e2e.eu.auth0.com", "rI3kt2rwnhBdXuV0wjMJY4mhcghfRVYP",
      "N-RusSpvQcq3VnxdRSqelAxeMOA5EVnuDLWrPloA2uQrDZymVvDiMSCKDcmqRFEi");

  public String authorize() {
    return auth.authorizeUrl("http://localhost:8080/docs/callback")
        .withAudience("https://dub-drng-live.dev.ott.irdeto.com")
        .build();
  }

  public String token(String code) {
    AuthRequest request = auth.exchangeCode(code, "http://localhost:8080/docs/callback")
        .setAudience("https://dub-drng-live.dev.ott.irdeto.com");

    try {
      TokenHolder holder = request.execute();
      return holder.getAccessToken();
    } catch (Auth0Exception exception) {
      exception.printStackTrace();
      throw new RuntimeException("error");
    }
  }

  @Override
  public void authenticate(final JsonObject jsonObject, final Handler<AsyncResult<User>> handler) {
    System.out.println("jsonobject authentication");
  }
/*
  public void verifyToken(final String accessToken) {
    final JWT jwt;
    final JWTClaimsSet claims;
    try {
      jwt = JWTParser.parse(accessToken);
      claims = jwt.getJWTClaimsSet();
      if (!(jwt instanceof SignedJWT)) {
        throw new InvalidTokenException("Access token is not a valid signed token: " + accessToken);
      }
    } catch (ParseException e) {
      throw new InvalidTokenException("Unable to parse the jwt token: " + accessToken);
    }

    //verifyTokenSignature((SignedJWT) jwt);

    final Date now = new Date();
    final Date expirationTime = claims.getExpirationTime();
    if (expirationTime == null || expirationTime.before(now)) {
      throw new InvalidTokenException("Token expired" + expirationTime);
    }

    final Date notBeforeTime = claims.getNotBeforeTime();
    if (notBeforeTime != null) {
      if (notBeforeTime.after(expirationTime)) {
        throw new InvalidTokenException("Invalid not before time");
      }
      if (notBeforeTime.after(now)) {
        throw new InvalidTokenException("Token can't be used before " + notBeforeTime);
      }
    }

    final Date issueTime = claims.getIssueTime();
    if (issueTime != null && issueTime.after(expirationTime)) {
      throw new InvalidTokenException("Invalid issue time");
    }

  }*/

  /*private void verifyTokenSignature(final SignedJWT jwt) {
    final String kidFromToken = jwt.getHeader().getKeyID();
    final Optional<PublicKey> publicKey = certificateProvider.getPublicKey(kidFromToken);

    if (!publicKey.isPresent()) {
      throw new InvalidTokenException("No public key with id " + kidFromToken + " is found to verify the signature of the token");
    } else if (!(publicKey.get() instanceof RSAPublicKey)) {
      throw new InvalidTokenException(
          "Public key with id " + kidFromToken + " was not an instance of RSAPublicKey, was: " + publicKey);
    }

    try {
      final JWSVerifier verifier = new RSASSAVerifier((RSAPublicKey) publicKey.get());
      if (!jwt.verify(verifier)) {
        throw new InvalidTokenException("Signature is not valid.");
      }
    } catch (JOSEException e) {
      throw new InvalidTokenException("Exception occurred while verifying token signature!");
    }
  }*/
}
