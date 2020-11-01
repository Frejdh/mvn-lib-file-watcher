package com.frejdh.util;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Stream;


@SuppressWarnings("unused")
public class ImmutableCollection<T> implements Iterable<T> {

	private final Collection<T> list;
	private final Class<?> type;

	@SafeVarargs
	public ImmutableCollection(T... elements) {
		this.list = Arrays.asList(elements);
		this.type = elements.length > 0 ? elements[0].getClass() : ArrayList.class;
	}

	public ImmutableCollection(Collection<T> collection) {
		this.list = collection != null ? copy(collection) : new ArrayList<>();
		this.type = collection != null && collection.size() > 0 ? collection.iterator().next().getClass() : ArrayList.class;
	}

	@SuppressWarnings("unchecked")
	private Collection<T> copy(Collection<T> collection) {
		if (collection == null)
			return null;

		try {
			return collection.getClass().getConstructor(Collection.class).newInstance(collection);
		} catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
			return new ArrayList<>(collection);
		}
	}

	public int size() {
		return this.list.size();
	}

	public boolean isEmpty() {
		return this.list.isEmpty();
	}

	public boolean contains(T o) {
		return this.list.contains(o);
	}

	@SuppressWarnings("MethodDoesntCallSuperMethod")
	public ImmutableCollection<T> clone() {
		return new ImmutableCollection<>(list);
	}

	@SuppressWarnings("unchecked")
	public T[] toArray() {
		return toArray((T[]) Array.newInstance(type, this.list.size()));
	}

	public T[] toArray(T[] a) {
		return this.list.toArray(a);
	}

	@SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
	@Override
	public boolean equals(Object o) {
		return this.list.equals(o);
	}

	public int hashCode() {
		return this.list.hashCode();
	}

	@NotNull
	public Iterator<T> iterator() {
		return this.list.iterator();
	}

	public void forEach(Consumer<? super T> action) {
		this.list.forEach(action);
	}

	public boolean containsAll(Collection<?> c) {
		return this.list.containsAll(c);
	}

	public String toString() {
		return this.list.toString();
	}

	public Stream<T> stream() {
		return this.list.stream();
	}

	public Stream<T> parallelStream() {
		return this.list.parallelStream();
	}
}
