package model.action;

import java.util.ArrayList;
import java.util.List;

public class Permutation {
    private List<Integer> permutation;


    public Permutation(List<Integer> permutation) {
        // checking if argument permutation is correct permutation (== 0:(n-1))
        ArrayList<Integer> sorted = new ArrayList<>(permutation);
        sorted.sort(Integer::compareTo);
        boolean correct = true;
        for (int i = 0; i < permutation.size(); i++) {
            correct = correct && (sorted.get(i) == i);
        }
        if (!correct) {
            throw new IllegalArgumentException("Permutation argument has to contain all subsequent integers from 0 to its length!");
        }

        this.permutation = permutation;
    }

    public List<Integer> get() {
        return permutation;
    }

    public Permutation inverse() {
        ArrayList<Integer> inversePermutation = new ArrayList<>(permutation);
        for (int i = 0; i < permutation.size(); i++) {
            inversePermutation.set(permutation.get(i), i);
        }

        return new Permutation(inversePermutation);
    }

    public int length() {
        return permutation.size();
    }
}
