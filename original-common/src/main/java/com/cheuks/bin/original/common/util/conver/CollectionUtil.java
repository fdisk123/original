package com.cheuks.bin.original.common.util.conver;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@SuppressWarnings("unchecked")
public class CollectionUtil {

	private static final CollectionUtil newInstance = new CollectionUtil();

	@Deprecated
	public static final CollectionUtil newInstance() {
		return newInstance;
	}

	public static <K, V> Map<K, V> removeNullValue(final Map<K, V> collection) {
		Iterator<Entry<K, V>> it = collection.entrySet().iterator();
		Entry<K, V> en;
		while (it.hasNext()) {
			en = it.next();
			if (null == en.getValue())
				it.remove();
		}
		return collection;
	}

	public static <K, V> Map<K, V> toMap(Object... params) {
		if (null == params || 0 != (params.length % 2))
			return null;
		Map<String, Object> map = new WeakHashMap<String, Object>(params.length * 2);
		for (int i = 0, len = params.length; i < len; i++) {
			map.put((String) params[i++], params[i]);
		}
		return (Map<K, V>) map;
	}

	public static <K, V> Map<K, V> toMap(boolean isWeak, Object... params) {
		if (null == params || 0 != (params.length % 2))
			return null;
		Map<K, Object> result = isWeak ? new WeakHashMap<K, Object>(params.length * 2)
				: new HashMap<K, Object>(params.length * 2);
		for (int i = 0, len = params.length; i < len; i++) {
			result.put((K) params[i++], params[i]);
		}
		return (Map<K, V>) result;
	}

	@SafeVarargs
	public static <K, V> Map<K, V> collage(Map<K, V>... maps) {
		Map<K, V> result = null;
		for (Map<K, V> map : maps) {
			if (null == map || map.size() < 1) {
				continue;
			}
			if (null == result) {
				result = map;
			}
			result.putAll(map);
		}
		return result;
	}

	public static boolean isNotEmpty(Collection<?> collection) {
		return !isEmpty(collection);
	}

	public static boolean isEmpty(Collection<?> collection) {
		return null == collection || collection.size() == 0;
	}

	public static boolean isNotEmpty(Map<?, ?> map) {
		return !isEmpty(map);
	}

	public static boolean isEmpty(Map<?, ?> map) {
		return null == map || map.size() == 0;
	}

	public static boolean isNotEmpty(Object... o) {
		return !isEmpty(o);
	}

	public static boolean isEmpty(Object... o) {
		return null == o || o.length < 1;
	}

	public static MapBuilder mapBuilder() {
		return new MapBuilder();
	}

	public static class MapBuilder {

		private Map<Object, Object> data;

		public MapBuilder append(Object k, Object v) {
			if (null == this.data)
				data = new HashMap<Object, Object>();
			data.put(k, v);
			return this;
		}

		public <K, V> Map<K, V> build() {
			return (Map<K, V>) data;
		}

	}

	public static SetBuilder setBuilder(boolean isConcurrent) {
		return new SetBuilder(isConcurrent);
	}

	public static class SetBuilder {

		private Set<Object> data;

		public SetBuilder(boolean isConcurrent) {
			data = isConcurrent ? new CopyOnWriteArraySet<Object>() : new HashSet<Object>();
		}

		public SetBuilder append(Object value) {
			data.add(value);
			return this;
		}

		public <V> Set<V> build() {
			return (Set<V>) data;
		}

	}

	public static void main(String[] args) {

		Map<String, Object> a = toMap(true, new Object[] { 1, "1", 2, "2" });
		Map<String, Object> b = toMap("1", 1, "2", 2);
		System.out.println(a);
		System.out.println(b);
	}

}
