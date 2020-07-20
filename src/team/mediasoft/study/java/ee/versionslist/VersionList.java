package team.mediasoft.study.java.ee.versionslist;

import java.util.*;

public class VersionList<E> implements List {
    private int fakeSize = 0;
    private int trueSize = 0;

    private Node<E> first;
    private Node<E> last;

    VersionList() {}

    VersionList(Collection<? extends E> c) {
        this();
        addAll(c);
    }

    private class ListItr implements ListIterator<E> {
        private VersionList.Node<E> lastReturned;
        private VersionList.Node<E> next;
        private int nextIndex;

        ListItr(int index) {
            next = (index == fakeSize) ? null : getNodeByIndex(index);
            nextIndex = index;
        }

        public boolean hasNext() {
            return nextIndex < fakeSize;
        }

        public E next() {
            if (!hasNext())
                throw new NoSuchElementException();


            lastReturned = next;
            next = next.next;
            if (next != null && next.isDeleted()) {
                while (next.isDeleted() && next.next != null) {
                    next = next.next;
                }
            }
            nextIndex++;
            return lastReturned.item;
        }

        public boolean hasPrevious() {
            return nextIndex > 0;
        }

        public E previous() {
            if (!hasPrevious())
                throw new NoSuchElementException();

            lastReturned = next = (next == null) ? last : next.prev;
            if (lastReturned.isDeleted()) {
                while (lastReturned.isDeleted() && lastReturned.prev != null) {
                    lastReturned = next = next.prev;
                }
            }
            nextIndex--;
            return lastReturned.item;
        }

        public int nextIndex() {
            return nextIndex;
        }

        public int previousIndex() {
            return nextIndex - 1;
        }

        public void remove() {
            if (lastReturned == null)
                throw new IllegalStateException();

            VersionList.Node<E> lastNext = lastReturned.next;
            markAsDeleted(lastReturned);
            if (next == lastReturned)
                next = lastNext;
            else
                nextIndex--;
            lastReturned = null;
        }

        public void set(E e) {
            if (lastReturned == null)
                throw new IllegalStateException();
            markAsDeleted(lastReturned);
            if (next == null)
                linkLast(e);
            else
                linkBefore(e, next);
            lastReturned = next;
            next = lastReturned.next;
        }

        public void add(E e) {
            lastReturned = null;
            if (next == null)
                linkLast(e);
            else
                linkBefore(e, next);
            nextIndex++;
        }
    }

    private class Itr implements Iterator<E> {

        private VersionList.Node<E> current;
        private int index = 0;

        Itr() {
            current = getNodeByIndex(index);// не first, т.к. он может быть удаленным, а getNodeByIndex возвращает ссылку на неудаленный элемент
        }

        @Override
        public boolean hasNext() {
            return index < fakeSize - 1;
        }

        @Override
        public E next() throws IndexOutOfBoundsException {
            E result = current.item;
            if (!hasNext()) throw new IndexOutOfBoundsException("End of list.");
            if (hasNext() && current.next.isDeleted()) {
                while (current.next.isDeleted() && hasNext()) {
                    current = current.next;
                }
            }
            current = current.next;
            index++;
            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private static class Node<E> {
        E item;
        VersionList.Node<E> next;
        VersionList.Node<E> prev;
        Date createdAt;
        Date deletedAt;

        Node(VersionList.Node<E> prev, E element, VersionList.Node<E> next) {
            this.item = element;
            this.next = next;
            this.prev = prev;
            this.createdAt = new Date();
        }

        boolean isDeleted() {
            return this.deletedAt != null;
        }
    }

    @Override
    public int size() {
        return fakeSize;
    }

    @Override
    public boolean isEmpty() {
        return fakeSize == 0;
    }

    @Override
    public boolean contains(Object o) {
        return indexOf(o) >= 0;
    }

    @Override
    public Iterator iterator() {
        return new VersionList.Itr();
    }

    @Override
    public Object[] toArray() {
        Object[] result = new Object[fakeSize];
        int i = 0;
        for (VersionList.Node<E> x = first; x != null; x = x.next)
            if (!x.isDeleted())
                result[i++] = x.item;
        return result;
    }

    @Override
    public Object[] toArray(Object[] a) {
        return new Object[0];
    }

    @Override
    public boolean add(Object o) {
        linkLast(o);
        return true;
    }

    @Override
    public boolean remove(Object o) {
        if (o == null) {
            for (VersionList.Node<E> x = first; x != null; x = x.next) {
                if (!x.isDeleted() && x.item == null) {
                    markAsDeleted(x);
                    return true;
                }
            }
        } else {
            for (VersionList.Node<E> x = first; x != null; x = x.next) {
                if (!x.isDeleted() && o.equals(x.item)) {
                    markAsDeleted(x);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection c) {
        boolean contains = true;
        for (Object element: c) {
            if (!contains(element)) {
                contains = false;
                break;
            }
        }
        return contains;
    }

    @Override
    public boolean addAll(Collection c) {
        for (Object element: c) {
            add(element);
        };
        return true;
    }

    @Override
    public boolean addAll(int index, Collection c) {
        for (Object element: c) {
            add(index, element);
            index++;
        };
        return true;
    }

    @Override
    public boolean removeAll(Collection c) {
        for (Object element: c) {
            remove(element);
        };
        return true;
    }

    @Override
    public boolean retainAll(Collection c) {
        for (VersionList.Node<E> x = first; x != null; x = x.next) {
            if (!x.isDeleted() && !c.contains(x.item)) {
                markAsDeleted(x);
            }
        }
        return true;
    }

    @Override
    public void clear() {
        for (VersionList.Node<E> x = first; x != null; ) {
            VersionList.Node<E> next = x.next;
            x.item = null;
            x.next = null;
            x.prev = null;
            x = next;
        }
        first = last = null;
        fakeSize = 0;
        trueSize = 0;
    }

    @Override
    public Object get(int index) {
        checkElementIndex(index);
        return getNodeByIndex(index).item;
    }

    @Override
    public Object set(int index, Object element) {
        int i = index;
        checkElementIndex(i);
        VersionList.Node<E> x = getNodeByIndex(i);
        E oldVal = x.item;
        // Для сохранности истории нельзя просто так взять и поменять элемент, элемент помечается как удаленный и вставляется новый с новым значением
        this.remove(i);
        this.add(i, element);
        return oldVal;
    }

    @Override
    public void add(int index, Object element) {
        checkPositionIndex(index);
        if (index == trueSize) {
            linkLast(element);
        } else {
            linkBefore((E)element, getNodeByIndex(index));
        }
    }

    @Override
    public Object remove(int index) {
        checkElementIndex(index);
        return markAsDeleted(getNodeByIndex(index));
    }

    @Override
    public int indexOf(Object o) {
        int index = 0;
        if (o == null) {
            for (VersionList.Node<E> x = first; x != null; x = x.next) {
                if (!x.isDeleted() && x.item == null)
                    return index;
                index++;
            }
        } else {
            for (VersionList.Node<E> x = first; x != null; x = x.next) {
                if (!x.isDeleted() && o.equals(x.item))
                    return index;
                index++;
            }
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        int index = trueSize;
        if (o == null) {
            for (VersionList.Node<E> x = last; x != null; x = x.prev) {
                index--;
                if (!x.isDeleted() && x.item == null)
                    return index;
            }
        } else {
            for (VersionList.Node<E> x = last; x != null; x = x.prev) {
                index--;
                if (!x.isDeleted() && o.equals(x.item))
                    return index;
            }
        }
        return -1;
    }

    @Override
    public ListIterator listIterator() {
        return listIterator(0);
    }

    @Override
    public ListIterator listIterator(int index) {
        checkPositionIndex(index);
        return new VersionList.ListItr(index);
    }

    @Override
    public List subList(int fromIndex, int toIndex) {
        checkPositionIndex(fromIndex);
        checkPositionIndex(toIndex);
        checkIndexOrder(fromIndex, toIndex);
        List result = new ArrayList();
        VersionList.Node<E> x = getNodeByIndex(fromIndex);
        for (int i = fromIndex; i <= toIndex; i++) {
            if (!x.isDeleted()) {
                result.add(x.item);
            }
            x = x.next;
        }
        return result;
    }

    private void linkLast(Object o) {
        final VersionList.Node<E> l = last;
        final VersionList.Node<E> newNode = new VersionList.Node(l, o, null);
        last = newNode;
        if (l == null)
            first = newNode;
        else
            l.next = newNode;
        fakeSize++;
        trueSize++;
    }

    public List getVersionByDate(Date date) {
        List result = new ArrayList();
        VersionList.Node<E> x = first;
        for (int i = 0; i < trueSize; i++) {
            if ((x.createdAt.before(date) || x.createdAt.equals(date)) && (!x.isDeleted() || x.deletedAt.after(date))) {
                result.add(x.item);
            }
            x = x.next;
        }
        return result;
    }

    private void linkBefore(E e, VersionList.Node<E> succ) {
        final VersionList.Node<E> pred = succ.prev;
        final VersionList.Node<E> newNode = new VersionList.Node<>(pred, e, succ);
        succ.prev = newNode;
        if (pred == null)
            first = newNode;
        else
            pred.next = newNode;
        fakeSize++;
        trueSize++;
    }

    private Object markAsDeleted(VersionList.Node<E> x) {
        x.deletedAt = new Date();
        fakeSize--;
        return x.item;
    }

    private Node<E> getNodeByIndex(int index) {
        VersionList.Node<E> x = first;
        for (int i = 0; i < index; i++) {
            if (x.isDeleted()) {
                while (x.isDeleted() && x.next != null) {
                    x = x.next;
                }
            }
            x = x.next;
        }
        return x;
    }

    private void checkElementIndex(int index) {
        if (!isElementIndex(index))
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }

    private boolean isElementIndex(int index) {
        return index >= 0 && index < fakeSize;
    }

    private String outOfBoundsMsg(int index) {
        return "Index: "+ index +", Size: "+ fakeSize;
    }

    private String indexOrderErrorMsg(int fromIndex, int toIndex) {
        return "From index: "+ fromIndex +" > toIndex: "+ toIndex;
    }

    private void checkPositionIndex(int index) {
        if (!isPositionIndex(index))
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }

    private void checkIndexOrder(int fromIndex, int toIndex) {
        if (fromIndex > toIndex)
            throw new IndexOutOfBoundsException(indexOrderErrorMsg(fromIndex, toIndex));
    }

    private boolean isPositionIndex(int index) {
        return index >= 0 && index <= fakeSize;
    }
}
