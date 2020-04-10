package model.action;

import model.dataframe.Dataframe;

import java.util.Comparator;
import java.util.List;

public class ActionArrange extends Action {

    private int col;
    private Dataframe df;
    private Comparator comparator;

    public ActionArrange(Dataframe df, int col, Comparator comparator) {
        this.df = df;
        this.col = col;
        this.comparator = comparator;
    }

    @Override
    public Action executeAndInverse() {
        List<Integer> permutation = df.arrangePermutation(col, comparator);
        df.select(permutation);

        return new ActionArrangeByPermutation(df, (new Permutation(permutation)).inverse());
    }

    @Override
    public String getName() {
        return "Arrange";
    }

    @Override
    public String getInverseName() {
        return "Arrange";
    }
}
