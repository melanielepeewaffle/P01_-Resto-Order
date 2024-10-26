package ch.hearc.ig.orderresto.persistence;

import java.util.HashMap;
import java.util.Map;

public class IdentityMap<T> {
    private final Map<Long, T> cache = new HashMap<>();

    public T get(Long id) {
        return cache.get(id);
    }

    public void put(Long id, T entity) {
        cache.put(id, entity);
    }

    public boolean contains(Long id) {
        return cache.containsKey(id);
    }

    public void clear() {
        cache.clear();
    }
}
