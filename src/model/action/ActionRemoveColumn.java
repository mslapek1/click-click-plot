package model.action;

import model.dataframe.Column;
import model.dataframe.Dataframe;

public class ActionRemoveColumn extends Action {
    private Dataframe df;
    private int col;

    public ActionRemoveColumn(Dataframe df, int col) {
        this.df = df;
        this.col = col;
    }

    @Override
    public Action executeAndInverse() {
        Column removed = df.getColumn(col);
        df.removeColumn(col);
        return new ActionAddColumn(df, removed, col);
    }

    @Override
    public String getName() {
        return "Remove column";
    }

    @Override
    public String getInverseName() {
        return "Add column";
    }
}
