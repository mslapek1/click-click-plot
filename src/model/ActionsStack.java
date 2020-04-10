package model;

import model.action.Action;

import java.util.LinkedList;

public class ActionsStack {

    private LinkedList<Action> past;
    private LinkedList<Action> future;

    public ActionsStack() {
        this.past = new LinkedList<>();
        this.future = new LinkedList<>();
    }

    public boolean isUndoPossible() {
        return !past.isEmpty();
    }

    public boolean isRedoPossible() {
        return !future.isEmpty();
    }

    public String getUndoName() throws EmptyActionsStackException {
        if (isUndoPossible()) {
            assert this.past.peek() != null;
            return this.past.peek().getInverseName();
        } else {
            throw new EmptyActionsStackException();
        }
    }

    public String getRedoName() throws EmptyActionsStackException {
        if (isRedoPossible()) {
            assert this.future.peek() != null;
            return this.future.peek().getName();
        } else {
            throw new EmptyActionsStackException();
        }
    }

    public void undo() throws EmptyActionsStackException {
        if (isUndoPossible()) {
            this.future.push(past.pop().executeAndInverse());
        } else {
            throw new EmptyActionsStackException();
        }
    }

    public void redo() throws EmptyActionsStackException {
        if (isRedoPossible()) {
            this.past.push(future.pop().executeAndInverse());
        } else {
            throw new EmptyActionsStackException();
        }
    }

    public void addAndExecute(Action newAction) {
        this.future.clear();
        this.past.push(newAction.executeAndInverse());
    }

    public class EmptyActionsStackException extends Throwable {
    }
}
