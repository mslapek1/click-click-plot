package model.plot;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.*;
import model.dataframe.Dataframe;

public class Plot {
    private Dataframe df;

    private Geom geom;
    private Aes aes;
    private String title;

    public Plot(Dataframe df, Geom geom, Aes aes, String title) {
        this.df = df;
        this.geom = geom;
        this.aes = aes;
        this.title = title;
    }

    public void setAes(Aes aes) {
        this.aes = aes;
    }

    public void setGeom(Geom geom) {
        this.geom = geom;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private int x() throws NoAesException {
        if (this.aes == null || this.aes.getX() == null) {
            throw new NoAesException();
        } else {
            return this.aes.getX();
        }
    }

    private int y() throws NoAesException {
        if (this.aes == null || this.aes.getY() == null) {
            throw new NoAesException();
        } else {
            return this.aes.getY();
        }
    }

    private int size() throws NoAesException {
        if (this.aes == null || this.aes.getSize() == null) {
            throw new NoAesException();
        } else {
            return this.aes.getSize();
        }
    }


    private Chart getPieChart() throws NoAesException, PlotException {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        try {
            for (int i = 0; i < this.df.nrow(); i++) {
                pieChartData.add(new PieChart.Data(this.df.getValueAt(this.x(), i), Double.parseDouble(this.df.getValueAt(this.y(), i))));
            }
            PieChart chart = new PieChart(pieChartData);
            chart.setTitle(this.title);
            return chart;
        } catch (NumberFormatException e) {
            throw new PlotException("Not possible to generate chart due to values not being Double.");
        }
    }

    private Chart getLineChart() throws NoAesException, PlotException {
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel(this.df.getColumnName(this.x()));
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel(this.df.getColumnName(this.y()));

        LineChart<Number,Number> lineChart =
                new LineChart<Number,Number>(xAxis,yAxis);

        //defining a series
        XYChart.Series series = new XYChart.Series();
        try {
            //populating the series with data
            for (int i = 0; i < this.df.nrow(); i++) {
                Double xValue = Double.parseDouble(this.df.getValueAt(this.x(), i));
                Double yValue = Double.parseDouble(this.df.getValueAt(this.y(), i));
                series.getData().add(new XYChart.Data(xValue, yValue));
            }
        } catch (NumberFormatException e) {
            throw new PlotException("Not possible to generate chart due to values not being Double.");
        }

        series.setName(this.title);
        lineChart.getData().add(series);

        return lineChart;
    }


    private Chart getBarChart() throws NoAesException, PlotException {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel(this.df.getColumnName(this.x()));
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel(this.df.getColumnName(this.y()));

        BarChart<String, Number> barChart =
                new BarChart<>(xAxis, yAxis);

        //defining a series
        XYChart.Series series = new XYChart.Series();
        try {
            //populating the series with data
            for (int i = 0; i < this.df.nrow(); i++) {
                String xValue = this.df.getValueAt(this.x(), i);
                Double yValue = Double.parseDouble(this.df.getValueAt(this.y(), i));
                series.getData().add(new XYChart.Data(xValue, yValue));
            }
        } catch (NumberFormatException e) {
            throw new PlotException("Not possible to generate chart due to values not being Double.");
        }

        series.setName(this.title);
        barChart.getData().add(series);

        //barChart.getXAxis().setLabel(this.df.getColumnName());

        return barChart;
    }

    private Chart getAreaChart() throws NoAesException, PlotException {
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel(this.df.getColumnName(this.x()));
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel(this.df.getColumnName(this.y()));

        AreaChart<Number,Number> areaChart =
                new AreaChart<Number,Number>(xAxis,yAxis);

        //defining a series
        XYChart.Series series = new XYChart.Series();
        try {
            //populating the series with data
            for (int i = 0; i < this.df.nrow(); i++) {
                Double xValue = Double.parseDouble(this.df.getValueAt(this.x(), i));
                Double yValue = Double.parseDouble(this.df.getValueAt(this.y(), i));
                series.getData().add(new XYChart.Data(xValue, yValue));
            }
        } catch (NumberFormatException e) {
            throw new PlotException("Not possible to generate chart due to values not being Double.");
        }

        series.setName(this.title);
        areaChart.getData().add(series);

        return areaChart;
    }

    private Chart getScatterChart() throws NoAesException, PlotException {
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel(this.df.getColumnName(this.x()));
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel(this.df.getColumnName(this.y()));

        ScatterChart<Number,Number> scatterChart =
                new ScatterChart<Number,Number>(xAxis,yAxis);

        //defining a series
        XYChart.Series series = new XYChart.Series();
        try {
            //populating the series with data
            for (int i = 0; i < this.df.nrow(); i++) {
                Double xValue = Double.parseDouble(this.df.getValueAt(this.x(), i));
                Double yValue = Double.parseDouble(this.df.getValueAt(this.y(), i));
                series.getData().add(new XYChart.Data(xValue, yValue));
            }
        } catch (NumberFormatException e) {
            throw new PlotException("Not possible to generate chart due to values not being Double.");
        }

        series.setName(this.title);
        scatterChart.getData().add(series);

        return scatterChart;
    }

    private Chart getBubbleChart() throws NoAesException, PlotException {
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel(this.df.getColumnName(this.x()));
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel(this.df.getColumnName(this.y()));
        NumberAxis sizeAxis = new NumberAxis();
        sizeAxis.setLabel(this.df.getColumnName(this.size()));

        BubbleChart<Number,Number> bubblePlot =
                new BubbleChart<>(xAxis,yAxis);

        //defining a series
        XYChart.Series series = new XYChart.Series();
        try {
            //populating the series with data
            for (int i = 0; i < this.df.nrow(); i++) {
                Double xValue = Double.parseDouble(this.df.getValueAt(this.x(), i));
                Double yValue = Double.parseDouble(this.df.getValueAt(this.y(), i));
                Double sizeValue = Double.parseDouble(this.df.getValueAt(this.size(), i));

                series.getData().add(new XYChart.Data(xValue, yValue, sizeValue));
            }
        } catch (NumberFormatException e) {
            throw new PlotException("Not possible to generate chart due to values not being Double.");
        }

        series.setName(this.title);
        bubblePlot.getData().add(series);

        return bubblePlot;
    }


    public Chart getChart() throws PlotException {
        if (df == null) {
            throw new PlotException("No data loaded. Not possible to generate plot.");
        } else {
            try {
                switch (geom) {
                    case BAR: return getBarChart();
                    case PIE: return getPieChart();
                    case AREA: return getAreaChart();
                    case LINE: return getLineChart();
                    case SCATTER: return getScatterChart();
                    case BUBBLE: return getBubbleChart();
                    default: throw new PlotException("Geom not chosen. Not possible to generate plot.");
                }
            } catch (NoAesException ignored) {
                throw new PlotException("Not enough Aes binds. Not possible to generate plot.");
            }
        }
    }

}
