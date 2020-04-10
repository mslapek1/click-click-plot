package model.action;

import model.dataframe.Dataframe;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class ActionRearangeColumns extends Action {
    private Dataframe df;
    private Permutation permutation;

    public ActionRearangeColumns(Dataframe df, Permutation permutation) {
        if (permutation.length() != df.ncol()) {
            throw new IllegalArgumentException("ActionRearangeColumn has to arrange by permutation of correct length!");
        }

        this.df = df;
        this.permutation = permutation;
    }

    @Override
    public Action executeAndInverse() {
        //permutate Dataframe Columns by swapping them
        ArrayList<Integer> permuted = new ArrayList<>(permutation.get());
        // == 0:n
        ArrayList<Integer> sorted = IntStream.rangeClosed(0, df.ncol() - 1)
                .boxed().collect(Collectors.toCollection(ArrayList::new));

        for (int i = 0; i < df.ncol(); i++) {
            while (!permuted.get(i).equals(sorted.get(i))) {
                // swapping elements on positions (i, sorted.get(i))
                int v = permuted.get(i);
                int v2 = permuted.get(v);
                permuted.set(v, v);
                permuted.set(i, v2);

                df.swapColumns(i, v);
            }
        }

        return new ActionRearangeColumns(df, permutation.inverse());
    }

    @Override
    public String getName() {
        return "Rearange columns";
    }

    @Override
    public String getInverseName() {
        return "Rearange columns";
    }
}
