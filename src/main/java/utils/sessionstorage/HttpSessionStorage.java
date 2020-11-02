package utils.sessionstorage;

import javax.servlet.http.HttpSession;

public class HttpSessionStorage implements ISessionStorage {

    private HttpSession session;

    public HttpSessionStorage(HttpSession session) {
        this.session = session;
    }

    @Override
    public Object getValue(String key) {
        return session.getAttribute(key);
    }

    @Override
    public void setValue(String key, Object value) {
        session.setAttribute(key, value);
    }

    @Override
    public void remValue(String key) {
        session.removeAttribute(key);
    }
}
