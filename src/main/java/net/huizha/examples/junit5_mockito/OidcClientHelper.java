package net.huizha.examples.junit5_mockito;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

/**
 * A stub class of com.ibm.websphere.security.oidc.util.OidcClientHelper
 *
 * @author Zhahui
 *
 */
public class OidcClientHelper {
    private static final String OIDC_SESSION_REF_KEY = "oidc-session-ref";
    private static final String JWT_CLAIM_SUB_KEY = "sub";

    /**
     * Private constructor.
     */
    private OidcClientHelper() {

    }

    /**
     * Get the ID token from OIDC access token.
     *
     * @param accessToken the access token string
     * @return the ID token string
     */
    public static String getIdTokenFromAccessToken(String accessToken) {
        if (StringUtils.isBlank(accessToken)) {
            return null;
        }
        return "stub-id-token";
    }

    public static Map<String, String> getJwtClaimsFromIdTokenAsMap(String idToken) {
        if (StringUtils.isBlank(idToken)) {
            throw new IllegalArgumentException("Could not get JWT claims from ID token: ID token is null or empty");
        }
        Map<String, String> claimsMap = new HashMap<>();
        claimsMap.put(OIDC_SESSION_REF_KEY, "stub-oidc-session-ref");
        claimsMap.put(JWT_CLAIM_SUB_KEY, "stub-sub");
        return claimsMap;
    }
}
