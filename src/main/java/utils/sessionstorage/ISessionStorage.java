package utils.sessionstorage;

public interface ISessionStorage {
    Object getValue(String key);
    void setValue(String key, Object value);
    void remValue(String key);
}
