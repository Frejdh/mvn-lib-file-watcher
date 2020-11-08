package com.frejdh.util;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Immutable collection due to lacking support in native Java.
 * @param <T> Type parameter for the collection.
 */
@SuppressWarnings("unused")
public class ImmutableCollection<T> implements Iterable<T> {

	private final Collection<T> collection;
	private final Class<?> type;

	@SafeVarargs
	public ImmutableCollection(T... elements) {
		this.collection = Arrays.asList(elements);
		this.type = elements.length > 0 ? elements[0].getClass() : ArrayList.class;
	}

	public ImmutableCollection(Collection<T> collection) {
		this.collection = collection != null ? copy(collection) : new ArrayList<>();
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

	/**
	 * Same as {@link Collection#size()}.
	 */
	public int size() {
		return this.collection.size();
	}

	/**
	 * Same as {@link Collection#isEmpty()}.
	 */
	public boolean isEmpty() {
		return this.collection.isEmpty();
	}

	/**
	 * Same as {@link Collection#contains(Object)}.
	 */
	public boolean contains(T element) {
		return this.collection.contains(element);
	}

	/**
	 * Same as {@link Collection#containsAll(Collection)}.
	 */
	public boolean containsAll(ImmutableCollection<T> elements) {
		return this.collection.containsAll(elements.collection);
	}

	/**
	 * Same as {@link Collection#containsAll(Collection)}.
	 */
	public boolean containsAll(Collection<?> elements) {
		return this.collection.containsAll(elements);
	}

	/**
	 * Deep copy into another immutable reference.
	 * @return A new instance of the collection.
	 */
	@SuppressWarnings("MethodDoesntCallSuperMethod")
	public ImmutableCollection<T> clone() {
		return new ImmutableCollection<>(collection);
	}

	/**
	 * Deep copies the collection.
	 * The new collection is mutable, assuming that the implementation passed in the constructor was mutable to begin with.
	 * @return A new instance of the collection.
	 */
	@SuppressWarnings("unchecked")
	public Collection<T> cloneToMutable() {
		try {
			return (Collection<T>) type.getConstructor(Collection.class).newInstance(collection);
		} catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
			return new ArrayList<>(collection);
		}
	}

	/**
	 * Same as {@link Collection#toArray()}.
	 */
	@SuppressWarnings("unchecked")
	public T[] toArray() {
		return toArray((T[]) Array.newInstance(type, this.collection.size()));
	}

	/**
	 * Same as {@link Collection#toArray(Object[])}.
	 */
	public T[] toArray(T[] array) {
		return this.collection.toArray(array);
	}

	/**
	 * Same as {@link Collection#equals(Object)}.
	 */
	@SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
	@Override
	public boolean equals(Object object) {
		return this.collection.equals(object);
	}

	/**
	 * Same as {@link Collection#hashCode()}.
	 */
	public int hashCode() {
		return this.collection.hashCode();
	}

	/**
	 * Same as {@link Collection#iterator()}.
	 */
	@NotNull
	public Iterator<T> iterator() {
		return this.collection.iterator();
	}

	/**
	 * Same as {@link Collection#forEach(Consumer)}.
	 */
	public void forEach(Consumer<? super T> action) {
		this.collection.forEach(action);
	}

	/**
	 * Same as {@link Collection#toString()}.
	 */
	public String toString() {
		return this.collection.toString();
	}

	/**
	 * Same as {@link Collection#stream()}.
	 */
	public Stream<T> stream() {
		return this.collection.stream();
	}

	/**
	 * Same as {@link Collection#parallelStream()}.
	 */
	public Stream<T> parallelStream() {
		return this.collection.parallelStream();
	}
}
