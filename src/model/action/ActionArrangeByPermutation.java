package model.action;

import model.dataframe.Dataframe;


public class ActionArrangeByPermutation extends Action {

    private Dataframe df;
    private Permutation permutation;

    public ActionArrangeByPermutation(Dataframe df, Permutation permutation) {
        if (permutation.length() != df.nrow()) {
            throw new IllegalArgumentException("ActionArrangePermutation has to arrange by permutation of correct length!");
        }

        this.df = df;
        this.permutation = permutation;
    }

    @Override
    public Action executeAndInverse() {
        df.select(permutation.get());

        return new ActionArrangeByPermutation(df, permutation.inverse());
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
