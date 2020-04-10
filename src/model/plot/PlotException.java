package model.plot;

public class PlotException extends Exception {
    private String message;

    public PlotException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
