/**
 * 
 */
package util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author subhro
 *
 */
public final class CollectionHelper {
	public static final class Dict<K, V> extends HashMap<K, V> {
		public Dict<K, V> putItem(K k, V v) {
			super.put(k, v);
			return this;
		}

		public V getItem(K k) {
			return super.get(k);
		}

		public V getItem(K k, V v) {
			V v1 = super.get(k);
			return v1 == null ? v : v1;
		}
	}

	public static final class OrderedDict<K, V> extends LinkedHashMap<K, V> {
		public OrderedDict<K, V> putItem(K k, V v) {
			super.put(k, v);
			return this;
		}

		public V getItem(K k) {
			return super.get(k);
		}

		public V getItem(K k, V v) {
			V v1 = super.get(k);
			return v1 == null ? v : v1;
		}
	}

	@SafeVarargs
	public static final <E> List<E> arrayList(E... args) {
		List<E> list = new ArrayList<>();
		for (E arg : args) {
			list.add(arg);
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	public static final <T> T[] toArray(Collection<T> collection) {
		return collection.toArray((T[]) new Object[0]);
	}
}
