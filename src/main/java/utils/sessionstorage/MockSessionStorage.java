package utils.sessionstorage;

import java.util.HashMap;

public class MockSessionStorage implements ISessionStorage {

    private HashMap<String, Object> storage = new HashMap<>();

    @Override
    public Object getValue(String key) {
        return storage.get(key);
    }

    @Override
    public void setValue(String key, Object value) {
        storage.put(key, value);
    }

    @Override
    public void remValue(String key) {
        storage.remove(key);
    }
}
