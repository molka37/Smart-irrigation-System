package com.enis.cot.smartirrigation.boundaries;
import jakarta.ejb.EJBException;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import com.nimbusds.jose.*;
import org.json.*;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.Base64;
import com.enis.cot.smartirrigation.util.OAuth2PKCE;
import com.enis.cot.smartirrigation.controllers.UserManager;
import com.enis.cot.smartirrigation.util.Identity;

@Path("//")
@RequestScoped
public class SignInEndpoint {
    public final static String XSS_COOKIE_NAME = "xssCookie";

    @EJB
    private OAuth2PKCE oAuth2PKCE;
    @EJB
    private UserManager identityController;
    @Context
    private UriInfo uriInfo;

    @POST
    @Path("/authorize")
    @Produces(MediaType.APPLICATION_JSON)
    public Response preSignIn(@HeaderParam("Pre-Authorization") String authorization) throws UnsupportedEncodingException {
        byte[] bytes = Base64.getDecoder().decode(authorization.substring("Bearer ".length()));
        String decoded = new String(bytes, "ISO_8859_1");
        String[] credentials = decoded.split("#");

        // FIX: Remplace le constructeur déprécié par NewCookie.Builder (Jakarta EE 10+)
        NewCookie cookie = new NewCookie.Builder(XSS_COOKIE_NAME)
                .value(oAuth2PKCE.generateXSSToken(credentials[0], uriInfo.getBaseUri().getPath()))
                .path(uriInfo.getBaseUri().getPath())
                .domain(uriInfo.getBaseUri().getHost())
                .comment("Secure Http Only Cookie")
                .maxAge(86400)
                .secure(true)
                .httpOnly(true)
                .build();

        return Response.status(Response.Status.FOUND)
                .cookie(cookie)
                .entity("{\"signInId\":\"" + oAuth2PKCE.addChallenge(credentials[1], credentials[0]) + "\"}")
                .build();
    }

    @POST
    @Path("/authenticate")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response signIn(String json) {
        JSONObject obj = new JSONObject(json);
        String mail = obj.getString("mail");
        String password = obj.getString("password");
        String signInId = obj.getString("signInId");
        if (mail == null || password == null || signInId == null ||
                mail.length() < 4 || mail.length() > 30) {
            return Response.status(Response.Status.NOT_ACCEPTABLE)
                    .entity("{\"message\":\"Invalid Credentials!\"}").build();
        }
        try {
            Identity identity = identityController.authenticate(mail, password);
            return Response.ok()
                    .entity("{\"authCode\":\"" + oAuth2PKCE.generateAuthorizationCode(signInId, identity) + "\"}")
                    .build();
        } catch (EJBException e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"message\":\"" + e.getMessage() + "\"}").build();
        }
    }

    @GET
    @Path("/oauth/token")
    @Produces(MediaType.APPLICATION_JSON)
    public Response postSignIn(@HeaderParam("Post-Authorization") String authorization)
            throws NoSuchAlgorithmException, UnsupportedEncodingException {
        byte[] bytes = Base64.getDecoder().decode(authorization.substring("Bearer ".length()));
        String decoded = new String(bytes, "ISO_8859_1");
        String[] credentials = decoded.split("#");
        String token;
        try {
            token = oAuth2PKCE.checkCode(credentials[0], credentials[1]);
        } catch (Exception e) {
            return Response.serverError().entity("{\"message\":\"" + e.getMessage() + "\"}").build();
        }
        return token == null
                ? Response.status(Response.Status.UNAUTHORIZED).entity("{\"message\":\"Unauthorized Access!\"}").build()
                : Response.ok().entity("{\"accessToken\":\"" + token + "\",\"refreshToken\":\"" + oAuth2PKCE.generateRefreshTokenFor(token) + "\"}").build();
    }

    @GET
    @Path("/oauth/token/refresh")
    @Produces(MediaType.APPLICATION_JSON)
    public Response refreshSignIn(
            @HeaderParam("Refresh-Authorization") String refreshToken,
            @HeaderParam(HttpHeaders.AUTHORIZATION) String accessToken
    ) throws ParseException, JOSEException {
        String refreshPayload = accessToken.substring("Bearer ".length(), accessToken.lastIndexOf("."));
        if (oAuth2PKCE.check(refreshPayload, refreshToken)) {
            String payloadBase64 = accessToken.substring(
                    accessToken.indexOf(".") + 1,
                    accessToken.lastIndexOf(".")
            );
            String payloadJson = new String(Base64.getUrlDecoder().decode(payloadBase64));
            JSONObject payload = new JSONObject(payloadJson);
            String username = payload.getString("sub");

            Identity identity = identityController.findByUsername(username);
            String token;
            try {
                token = oAuth2PKCE.generateTokenFor(identity);
            } catch (Exception e) {
                return Response.serverError().entity("{\"message\":\"" + e.getMessage() + "\"}").build();
            }
            return token == null
                    ? Response.status(Response.Status.UNAUTHORIZED).entity("{\"message\":\"Unauthorized Access!\"}").build()
                    : Response.ok().entity("{\"accessToken\":\"" + token + "\",\"refreshToken\":\"" + oAuth2PKCE.generateRefreshTokenFor(token) + "\"}").build();
        }
        return Response.status(Response.Status.UNAUTHORIZED).entity("{\"message\":\"Unauthorized Access!\"}").build();
    }

    @POST
    @Path("/authenticateadmin")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response signInAdmin(String json) {
        JSONObject obj = new JSONObject(json);
        String mail = obj.getString("mail");
        String password = obj.getString("password");
        String signInId = obj.getString("signInId");
        if (mail == null || password == null || signInId == null ||
                mail.length() < 4 || mail.length() > 30) {
            return Response.status(Response.Status.NOT_ACCEPTABLE)
                    .entity("{\"message\":\"Invalid Credentials!\"}").build();
        }
        try {
            Identity identity = identityController.authenticateadmin(mail, password);
            return Response.ok()
                    .entity("{\"authCode\":\"" + oAuth2PKCE.generateAuthorizationCode(signInId, identity) + "\"}")
                    .build();
        } catch (EJBException e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"message\":\"" + e.getMessage() + "\"}").build();
        }
    }
}
