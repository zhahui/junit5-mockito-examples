package net.huizha.examples.junit5_mockito;

/**
 * Define OIDC session configuration object.
 *
 * @author Zhahui
 *
 */
public class OidcSessionManagementConfig {
    private String baseUrl;
    private String validateSessionContextPath;
    private String refreshSessionContextPath;

    /**
     * Get session validation context path.
     *
     * @return the session validation context path
     */
    public String getValidateSessionContextPath() {
        return validateSessionContextPath;
    }

    /**
     * Set session validation context path.
     *
     * @param validateSessionContextPath the session validation context path to set
     */
    public void setValidateSessionContextPath(String validateSessionContextPath) {
        this.validateSessionContextPath = validateSessionContextPath;
    }

    /**
     * Get session refreshing context path.
     *
     * @return the session refreshing context path
     */
    public String getRefreshSessionContextPath() {
        return refreshSessionContextPath;
    }

    /**
     * Set session refreshing context path.
     *
     * @param refreshSessionContextPath the session refreshing context path to set
     */
    public void setRefreshSessionContextPath(String refreshSessionContextPath) {
        this.refreshSessionContextPath = refreshSessionContextPath;
    }

    /**
     * Get session management base URL.
     *
     * @return the base URL to set
     */
    public String getBaseUrl() {
        return baseUrl;
    }

    /**
     * Set session management base URL.
     *
     * @param baseUrl the base URL
     */
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
