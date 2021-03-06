package com.cheuks.bin.original.common.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.WeakHashMap;

public class CollectionUtil {

	private static final CollectionUtil newInstance = new CollectionUtil();

	public static final CollectionUtil newInstance() {
		return newInstance;
	}

	public <K, V> Map<K, V> removeNullValue(final Map<K, V> collection) {
		Iterator<Entry<K, V>> it = collection.entrySet().iterator();
		Entry<K, V> en;
		while (it.hasNext()) {
			en = it.next();
			if (null == en.getValue())
				it.remove();
		}
		return collection;
	}

	public Map<String, Object> toMap(Object... params) {
		if (null == params || 0 != (params.length % 2))
			return null;
		Map<String, Object> map = new WeakHashMap<String, Object>();
		for (int i = 0, len = params.length; i < len; i++) {
			map.put((String) params[i++], params[i]);
		}
		return map;
	}

	@SuppressWarnings("unchecked")
	public <K> Map<K, Object> toMap(boolean isWeak, Object... params) {
		if (null == params || 0 != (params.length % 2))
			return null;
		Map<K, Object> result = isWeak ? new WeakHashMap<K, Object>() : new HashMap<K, Object>();
		for (int i = 0, len = params.length; i < len; i++) {
			result.put((K) params[i++], params[i]);
		}
		return result;
	}

	public static void main(String[] args) {

		Map<String, Object> a = newInstance.toMap(true, new Object[] { 1, "1", 2, "2" });
		Map<String, Object> b = newInstance.toMap("1", 1, "2", 2);
		System.out.println(a);
		System.out.println(b);
	}

}
