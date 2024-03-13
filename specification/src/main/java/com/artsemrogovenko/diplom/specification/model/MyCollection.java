package com.artsemrogovenko.diplom.specification.model;

import com.artsemrogovenko.diplom.specification.dto.ComponentRequest;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
@Data
@NoArgsConstructor
@org.springframework.stereotype.Component("componentList")
public class MyCollection implements List<ComponentRequest>,Set<ComponentRequest> {
    public MyCollection(Collection<ComponentRequest> componentRequests) {
        this.componentRequests.addAll(componentRequests) ;
    }

    public void setComponentRequests(List<ComponentRequest> componentRequests) {
        this.componentRequests = (ArrayList<ComponentRequest>) componentRequests;
    }

    private ArrayList<ComponentRequest> componentRequests=new ArrayList<ComponentRequest> ();


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
    public Iterator<ComponentRequest> iterator() {
        return componentRequests.iterator();
    }

    @Override
    public void forEach(Consumer<? super ComponentRequest> action) {
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
    public boolean add(ComponentRequest componentRequest) {
        return componentRequests.add(componentRequest);
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
    public boolean addAll(@NotNull Collection<? extends ComponentRequest> c) {
        return componentRequests.addAll(c);
    }

    @Override
    public boolean addAll(int index, @NotNull Collection<? extends ComponentRequest> c) {
        return componentRequests.addAll(index,c);
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        return componentRequests.removeAll(c);
    }

    @Override
    public boolean removeIf(Predicate<? super ComponentRequest> filter) {
        return List.super.removeIf(filter);
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        return componentRequests.retainAll(c);
    }

    @Override
    public void replaceAll(UnaryOperator<ComponentRequest> operator) {
        componentRequests.replaceAll(operator);
    }

    @Override
    public void sort(Comparator<? super ComponentRequest> c) {
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
    public Stream<ComponentRequest> stream() {
        return componentRequests.stream();
    }

    @Override
    public Stream<ComponentRequest> parallelStream() {
        return componentRequests.parallelStream();
    }

    @Override
    public ComponentRequest get(int index) {
        while (index >= componentRequests.size()) {
            // Если индекс больше или равен размеру коллекции, создаем новый компонент
            ComponentRequest componentRequest =new ComponentRequest();
            componentRequests.add(componentRequest);
        }
        return componentRequests.get(index);
    }

    @Override
    public ComponentRequest set(int index, ComponentRequest element) {
        return componentRequests.set(index,element);
    }

    @Override
    public void add(int index, ComponentRequest element) {
        componentRequests.add(index,element);
    }

    @Override
    public ComponentRequest remove(int index) {
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
    public ListIterator<ComponentRequest> listIterator() {
        return componentRequests.listIterator();
    }

    @NotNull
    @Override
    public ListIterator<ComponentRequest> listIterator(int index) {
        return componentRequests.listIterator(index);
    }

    @NotNull
    @Override
    public List<ComponentRequest> subList(int fromIndex, int toIndex) {
        return componentRequests.subList(fromIndex,toIndex);
    }

    @Override
    public Spliterator<ComponentRequest> spliterator() {
        return componentRequests.spliterator();
    }

    @Override
    public String toString() {
        return componentRequests.toString();
    }
}
