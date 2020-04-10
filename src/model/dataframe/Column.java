package model.dataframe;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Column {
    private ArrayList<String> values;
    private String name;


    public Column(String name) {
        this.name = name;

        this.values = new ArrayList<>();
    }

    public Column(String name, String... values) {
        this.name = name;

        this.values = new ArrayList<>();
        Collections.addAll(this.values, values);
    }

    public Column(String name, Integer... values) {
        this.name = name;

        this.values = new ArrayList<>();
        this.values.addAll(Arrays.stream(values).map(Object::toString).collect(Collectors.toList()));
    }

    public Column(String name, ArrayList<String> values) {
        this.name = name;

        this.values = values;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int length() {
        return this.values.size();
    }

    public String getValueAt(int pos) {
        if (pos < 0) {
            throw new IllegalArgumentException("Not possible to access row " + pos + ".");
        } else if (pos >= this.length()) {
            throw new IllegalArgumentException("Column contains " + this.length() +
                    " rows. Not possible to select row " + pos + ".");
        } else {
            return this.values.get(pos);
        }
    }

    public void setValueAt(int pos, String newValue) {
        if (pos < 0) {
            throw new IllegalArgumentException("Not possible to access row " + pos + ".");
        } else if (pos >= this.length()) {
            throw new IllegalArgumentException("Column contains " + this.length() +
                    " rows. Not possible to select row " + pos + ".");
        } else {
            this.values.set(pos, newValue);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (!Column.class.isAssignableFrom(obj.getClass())) {
            return false;
        }

        Column col2 = (Column) obj;

        if (!col2.getName().equals(this.getName())) {
            return false;
        }

        if (col2.length() != this.length()) {
            return false;
        }

        for (int i = 0; i < this.length(); i++) {
            if (!col2.getValueAt(i).equals(this.getValueAt(i))) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(values, name);
    }

    /**
     * can select elements more than once
     */
    public void select(List<Integer> indices) {
        ArrayList<String> result = new ArrayList<>();
        for (Integer indice : indices) {
            if (indice < 0) {
                throw new IllegalArgumentException("Not possible to access row " + indice + ".");
            }
            if (indice >= this.length()) {
                throw new IllegalArgumentException("Column contains " + this.length() +
                        " rows. Not possible to select row " + indice + ".");
            }

            result.add(this.getValueAt(indice));
        }
        this.values = result;
    }

    public List<Integer> arrangePermutation(Comparator<String> comparator) {
        Comparator<Integer> permutationComp = (a, b) -> comparator.compare(this.getValueAt(a), this.getValueAt(b));

        return IntStream.range(0, this.length()).
                boxed().
                sorted(permutationComp).
                collect(Collectors.toList());
    }

    public List<Integer> filterIndices(Predicate<String> predicate) {
        return IntStream.range(0, this.length()).
                boxed().
                filter(indice -> predicate.test(this.getValueAt(indice))).
                collect(Collectors.toList());
    }

}
