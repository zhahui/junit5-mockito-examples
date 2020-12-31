package net.huizha.examples.requestresponse;

import javax.servlet.http.HttpSession;

/**
 * Defines an object to provide client request information to a resource.
 *
 * @author Zhahui
 * @see <a
 * href=https://javaee.github.io/javaee-spec/javadocs/javax/servlet/ServletRequest.html>javax.servlet.ServletRequest</a>
 *
 */
public interface Request {
    /**
     * Returns the current session associated with this request, or if the request does not have a session, creates one.
     *
     * @return the HttpSession associated with this request
     */
    public HttpSession getSession();
}
