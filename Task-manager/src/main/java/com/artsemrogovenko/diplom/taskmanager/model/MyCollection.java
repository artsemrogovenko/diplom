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
    public MyCollection(Class<T> elementType, Collection<T> componentRequests) {
        this.componentRequests.addAll(componentRequests);
    }

//    public void set T(Collection<T> componentRequests) {
//        this.componentRequests = (ArrayList<T>) componentRequests;
//    }

    private Class<T> elementType;
    private ArrayList<T> componentRequests = new ArrayList<T>();


    @Override
    public int size() {
        return componentRequests.size();
    }

    @Override
    public boolean isEmpty() {
        return componentRequests.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return componentRequests.contains(o);
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return componentRequests.iterator();
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        componentRequests.forEach(action);
    }

    @NotNull
    @Override
    public Object @NotNull [] toArray() {
        return componentRequests.toArray();
    }

    @NotNull
    @Override
    public <T> T @NotNull [] toArray(@NotNull T[] a) {
        return componentRequests.toArray(a);
    }

    @Override
    public <T> T[] toArray(IntFunction<T[]> generator) {
        return componentRequests.toArray(generator);
    }

    @Override
    public boolean add(T componentRequest) {
        if (!componentRequests.equals(componentRequest)) {
            return componentRequests.add(componentRequest);
        }
        return false;
    }

    @Override
    public boolean remove(Object o) {
        return componentRequests.remove(o);
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        return componentRequests.containsAll(c);
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends T> c) {
        return componentRequests.addAll(c.stream().filter(T -> !componentRequests.equals(T)).toList());
    }

    @Override
    public boolean addAll(int index, @NotNull Collection<? extends T> c) {
        return componentRequests.addAll(index, c);
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        return componentRequests.removeAll(c);
    }

    @Override
    public boolean removeIf(Predicate<? super T> filter) {
        return List.super.removeIf(filter);
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        return componentRequests.retainAll(c);
    }

    @Override
    public void replaceAll(UnaryOperator<T> operator) {
        componentRequests.replaceAll(operator);
    }

    @Override
    public void sort(Comparator<? super T> c) {
        componentRequests.sort(c);
    }

    @Override
    public void clear() {
        componentRequests.clear();
    }

    @Override
    public boolean equals(Object o) {
        return componentRequests.equals(o);
    }

    @Override
    public int hashCode() {
        return componentRequests.hashCode();
    }

    @Override
    public Stream<T> stream() {
        return componentRequests.stream();
    }

    @Override
    public Stream<T> parallelStream() {
        return componentRequests.parallelStream();
    }

    @SneakyThrows
    @Override
    public T get(int index) {
        while (index >= componentRequests.size()) {
            // Если индекс больше или равен размеру коллекции, создаем новый компонент
            try {
                T componentRequest = elementType.newInstance(); // Создаем новый экземпляр типа T
                componentRequests.add(componentRequest);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace(); // Обработка исключения
            }
        }
        return componentRequests.get(index);
    }

    @Override
    public T set(int index, T element) {
        return componentRequests.set(index, element);
    }

    @Override
    public void add(int index, T element) {

        componentRequests.add(index, element);
    }

    @Override
    public T remove(int index) {
        return componentRequests.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return componentRequests.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return componentRequests.lastIndexOf(o);
    }

    @NotNull
    @Override
    public ListIterator<T> listIterator() {
        return componentRequests.listIterator();
    }

    @NotNull
    @Override
    public ListIterator<T> listIterator(int index) {
        return componentRequests.listIterator(index);
    }

    @NotNull
    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return componentRequests.subList(fromIndex, toIndex);
    }

    @Override
    public Spliterator<T> spliterator() {
        return componentRequests.spliterator();
    }

    @Override
    public String toString() {
        return componentRequests.toString();
    }
}
