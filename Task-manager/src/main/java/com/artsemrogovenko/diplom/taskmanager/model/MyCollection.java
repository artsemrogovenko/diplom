package com.artsemrogovenko.diplom.taskmanager.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

@Data
@NoArgsConstructor
public class MyCollection<T> implements List<T>, Set<T> {
    public MyCollection(Class<T> elementType, Collection<T> elements) {
        this.elements.addAll(elements);
        this.elementType = elementType;
    }

    public void set(Class<T> elementType, Collection<T> elements) {
        this.elements = (ArrayList<T>) elements;
        this.elementType = elementType;
    }

    private Class<T> elementType;
    private ArrayList<T> elements = new ArrayList<T>();


    @Override
    public int size() {
        return elements.size();
    }

    @Override
    public boolean isEmpty() {
        return elements.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return elements.contains(o);
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return elements.iterator();
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        elements.forEach(action);
    }

    @NotNull
    @Override
    public Object @NotNull [] toArray() {
        return elements.toArray();
    }

    @NotNull
    @Override
    public <T> T @NotNull [] toArray(@NotNull T[] a) {
        return elements.toArray(a);
    }

    @Override
    public <T> T[] toArray(IntFunction<T[]> generator) {
        return elements.toArray(generator);
    }

    @Override
    public boolean add(T componentRequest) {
        if (!elements.equals(componentRequest)) {
            return elements.add(componentRequest);
        }
        return false;
    }

    @Override
    public boolean remove(Object o) {
        return elements.remove(o);
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        return elements.containsAll(c);
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends T> c) {
        return elements.addAll(c.stream().filter(T -> !elements.equals(T)).toList());
    }

    @Override
    public boolean addAll(int index, @NotNull Collection<? extends T> c) {
        return elements.addAll(index, c);
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        return elements.removeAll(c);
    }

    @Override
    public boolean removeIf(Predicate<? super T> filter) {
        return List.super.removeIf(filter);
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        return elements.retainAll(c);
    }

    @Override
    public void replaceAll(UnaryOperator<T> operator) {
        elements.replaceAll(operator);
    }

    @Override
    public void sort(Comparator<? super T> c) {
        elements.sort(c);
    }

    @Override
    public void clear() {
        elements.clear();
    }

    @Override
    public boolean equals(Object o) {
        return elements.equals(o);
    }

    @Override
    public int hashCode() {
        return elements.hashCode();
    }

    @Override
    public Stream<T> stream() {
        return elements.stream();
    }

    @Override
    public Stream<T> parallelStream() {
        return elements.parallelStream();
    }

    @SneakyThrows
    @Override
    public T get(int index) {
        while (index >= elements.size()) {
            // Если индекс больше или равен размеру коллекции, создаем новый компонент
            try {
                T componentRequest = elementType.newInstance(); // Создаем новый экземпляр типа T
                elements.add(componentRequest);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace(); // Обработка исключения
            }
        }
        return elements.get(index);
    }

    @Override
    public T set(int index, T element) {
        return elements.set(index, element);
    }

    @Override
    public void add(int index, T element) {

        elements.add(index, element);
    }

    @Override
    public T remove(int index) {
        return elements.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return elements.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return elements.lastIndexOf(o);
    }

    @NotNull
    @Override
    public ListIterator<T> listIterator() {
        return elements.listIterator();
    }

    @NotNull
    @Override
    public ListIterator<T> listIterator(int index) {
        return elements.listIterator(index);
    }

    @NotNull
    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return elements.subList(fromIndex, toIndex);
    }

    @Override
    public Spliterator<T> spliterator() {
        return elements.spliterator();
    }

    @Override
    public String toString() {
        return elements.toString();
    }
}
