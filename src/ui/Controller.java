package ui;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.Chart;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import model.ActionsStack;
import model.action.*;
import model.dataframe.Dataframe;
import model.plot.*;


public class Controller implements Initializable {

    private static String logo = "logo.png";

    private Dataframe df;
    private ActionsStack actionsStack;
    private Plot plot;
    private ArrayList<Chart> plotHistory;
    private int currentPlot;
    private String currentFile;
    private boolean currentlySavedWithHeader;
    private boolean isSaved;


    @FXML private TableView table;
    @FXML private Pane plotPane;

    // menu items
    @FXML private MenuItem menuDeleteColumn;
    @FXML private MenuItem menuUndo;
    @FXML private MenuItem menuRedo;
    @FXML private MenuItem menuSave;
    @FXML private MenuItem menuSaveAs;
    @FXML private MenuItem menuOpenFile;
    @FXML private MenuItem exportAs;
    @FXML private MenuItem menuPlotSave;
    @FXML private MenuItem menuPlotSaveAs;
    @FXML private MenuItem plotOpenFile;

    // plot controls
    @FXML private RadioButton geomLine;
    @FXML private RadioButton geomScatter;
    @FXML private RadioButton geomArea;
    @FXML private RadioButton geomBar;
    @FXML private RadioButton geomPie;
    @FXML private RadioButton geomBubble;
    @FXML private ChoiceBox<Integer> aesX;
    @FXML private ChoiceBox<Integer> aesY;
    @FXML private ChoiceBox<Integer> aesSize;
    @FXML private TextField plotTitleField;
    @FXML private Button nextPlotButton;
    @FXML private Button prevPlotButton;
    @FXML private Button deletePlotButton;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        table.getSelectionModel().setCellSelectionEnabled(true);
        table.setEditable(true);
        geomBar.setSelected(true);

        nextPlotButton.setDisable(true);
        prevPlotButton.setDisable(true);
        deletePlotButton.setDisable(true);

        // initializing "global" variables
        actionsStack = new ActionsStack();
        currentFile = null;
        currentlySavedWithHeader = true;
        isSaved = true;
        plot = new Plot(null, null, null, null);
        plotHistory = new ArrayList<>();
        currentPlot = -1;

        // key shortcuts
        menuSave.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
        menuSaveAs.setAccelerator(KeyCombination.keyCombination("CTRL+SHIFT+S"));
        menuOpenFile.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
        menuUndo.setAccelerator(new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN));
        menuRedo.setAccelerator(new KeyCodeCombination(KeyCode.Y, KeyCombination.CONTROL_DOWN));
        exportAs.setAccelerator(new KeyCodeCombination(KeyCode.E, KeyCombination.CONTROL_DOWN));
        menuPlotSave.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
        menuPlotSaveAs.setAccelerator(KeyCombination.keyCombination("CTRL+SHIFT+S"));
        plotOpenFile.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
    }

    private EventHandler<TableColumn.CellEditEvent> cellEdit = event -> {
        ActionSetValueAt action = new ActionSetValueAt(df,
                event.getTablePosition().getColumn(),
                event.getTablePosition().getRow(),
                (String) event.getNewValue());

        actionsStack.addAndExecute(action);
        refresh();
    };

    @FXML
    public void redo() {
        try {
            actionsStack.redo();
            refresh();
        } catch (ActionsStack.EmptyActionsStackException e) {
            showErrorWindow("Error occurred. Not possible to redo.");
        }
    }

    @FXML
    public void undo() {
        try {
            actionsStack.undo();
            refresh();
        } catch (ActionsStack.EmptyActionsStackException e) {
            showErrorWindow("Error occurred. Not possible to undo.");
        }
    }

    private void refresh() {
        isSaved = false;

        refreshTable();
        refreshPloteditionPanel();
        refreshMenuButtons();
    }

    private void refreshTable() {
        // setting table columns
        table.getColumns().clear();
        for (int i = 0; i < df.ncol(); i++) {
            final int j = i;
            TableColumn col = new TableColumn(df.getColumnName(i));
            col.setCellValueFactory(
                    (Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>) param -> {
                        return new SimpleStringProperty(param.getValue().get(j).toString());
                    });

            // adding editing values in cells
            col.setCellFactory(TextFieldTableCell.forTableColumn());
            col.setOnEditCommit(cellEdit);

            // disabling sorting columns by clicking their header
            col.setSortable(false);

            table.getColumns().add(col);
        }

        // refreshing table items
        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
        for (int j = 0; j < df.nrow(); j++) {
            ObservableList<String> row = FXCollections.observableArrayList();
            ArrayList<String> dataList = new ArrayList<>();
            for (int i = 0; i < df.ncol(); i++) {
                dataList.add(df.getValueAt(i, j));
            }
            row.addAll(dataList);
            data.add(row);
        }
        table.setItems(data);


        // Disabling column reordering
        table.getColumns().addListener((ListChangeListener) change -> {
            change.next();
            if (change.wasReplaced() && change.wasRemoved()) {
                // getting columns permutation
                ArrayList<Integer> p = new ArrayList<>();
                change.getRemoved().stream().forEach(x -> p.add(table.getColumns().indexOf(x)));

                // performing action
                ActionRearangeColumns action = new ActionRearangeColumns(df, new Permutation(p));
                actionsStack.addAndExecute(action);

                refresh();
            }
        });
    }

    private void refreshPloteditionPanel() {
        ObservableList<Integer> items = FXCollections.observableArrayList();
        for (int i = 0; i < this.df.ncol(); i++) {
            items.add(i);
        }
        aesX.setItems(items);
        aesY.setItems(items);
        aesY.setItems(items);
        aesSize.setItems(items);

        if (items.size() > 0) {
            aesX.setValue(aesX.getItems().get(0));
            aesY.setValue(aesY.getItems().get(0));
            aesSize.setValue(aesSize.getItems().get(0));
        }
    }

    private void refreshMenuButtons() {
        //not possible to delete column if there's only 1:
        menuDeleteColumn.setDisable(df.ncol() <= 1);

        if (actionsStack.isUndoPossible()) {
            try {
                menuUndo.setText("Undo " + actionsStack.getUndoName());
            } catch (ActionsStack.EmptyActionsStackException e) {
                // we'll never get here because of checking .isUndoPossible()
                menuUndo.setText("Undo");
                e.printStackTrace();
            }
            menuUndo.setDisable(false);
        } else {
            menuUndo.setText("Undo");
            menuUndo.setDisable(true);
        }

        if (actionsStack.isRedoPossible()) {
            try {
                menuRedo.setText("Redo " + actionsStack.getRedoName());
            } catch (ActionsStack.EmptyActionsStackException e) {
                // we'll never get here because of checking .isRedoPossible()
                menuRedo.setText("Redo");
                e.printStackTrace();
            }
            menuRedo.setDisable(false);
        } else {
            menuRedo.setText("Redo");
            menuRedo.setDisable(true);
        }


        boolean savingDisableCondition = (df == null);
        menuPlotSave.setDisable(savingDisableCondition);
        menuPlotSaveAs.setDisable(savingDisableCondition);
        menuSave.setDisable(savingDisableCondition);
        menuSaveAs.setDisable(savingDisableCondition);
    }


    private void loadDataframeFromFile(String path, boolean hasHeader) {
        try {
            df = Dataframe.readCSV(path, hasHeader);

            plot = new Plot(df, null, null, null);
            actionsStack = new ActionsStack();
            isSaved = true;
            currentFile = path;
            currentlySavedWithHeader = hasHeader;

            Stage stage = (Stage) plotPane.getScene().getWindow();
            stage.setTitle(UI.applicationName() + " - [" + currentFile + "]");

            refresh();

        } catch (IOException e) {
            showErrorWindow("Chosen file is not a correct CSV file.");
        }
    }

    @FXML
    public void open() {
        FileChooser file = new FileChooser();
        file.setTitle("Open file");
        File choice = file.showOpenDialog(new Stage());

        if (choice != null) {
            String path = choice.getAbsolutePath();
            // Popup
            Alert popup = new Alert(Alert.AlertType.CONFIRMATION);
            popup.setTitle("Has header?");
            popup.setHeaderText("Has header?");
            popup.setContentText("Choose your option.");
            ButtonType buttonTypeYes = new ButtonType("Yes");
            ButtonType buttonTypeNo = new ButtonType("No");
            Stage popupStage = (Stage) popup.getDialogPane().getScene().getWindow();
            popupStage.getIcons().add(new Image(UI.class.getResourceAsStream(logo)));
            popup.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

            Optional<ButtonType> result = popup.showAndWait();

            if (result.isPresent()) {
                if (result.get() == buttonTypeYes) {
                    loadDataframeFromFile(path, true);
                } else if (result.get() == buttonTypeNo) {
                    loadDataframeFromFile(path, false);
                }
                isSaved = true;
            }
        }
    }

    @FXML
    public void quit() {
        Stage stage = (Stage) plotPane.getScene().getWindow();
        if (!isSaved) {
            Alert popup = new Alert(Alert.AlertType.CONFIRMATION);
            popup.setTitle("Save a file");
            popup.setHeaderText("This file is not saved. Do you want to quit anyway?");
            popup.setContentText("Choose your option.");
            ButtonType buttonTypeYes = new ButtonType("Yes");
            ButtonType buttonTypeNo = new ButtonType("No");
            Stage popupStage = (Stage) popup.getDialogPane().getScene().getWindow();
            popupStage.getIcons().add(new Image(UI.class.getResourceAsStream(logo)));
            popup.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);
            Optional<ButtonType> result = popup.showAndWait();

            if (result.isPresent() && result.get() == buttonTypeYes) {
                stage.close();
            }
        } else {
            stage.close();
        }
    }

    private void writeDataframeToFile(String path, boolean includeHeader) {
        try {
            df.writeCSV(path, includeHeader);

            currentFile = path;
            currentlySavedWithHeader = includeHeader;
            Stage stage = (Stage) plotPane.getScene().getWindow();
            stage.setTitle(UI.applicationName() + " - [" + currentFile + "]");
        } catch (IOException e) {
            showErrorWindow("Error writing to file.");
        }
    }

    @FXML
    public void saveAs() {
        FileChooser fileChooser = new FileChooser();

        // Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV File", "*.csv");
        fileChooser.getExtensionFilters().add(extFilter);

        // Show saveAs file dialog
        File file = fileChooser.showSaveDialog(new Stage());

        if (file != null) {
            // Popup asking if save with header
            Alert popup = new Alert(Alert.AlertType.CONFIRMATION);
            popup.setTitle("Include header?");
            popup.setHeaderText("Include header?");
            popup.setContentText("Choose your option.");
            ButtonType buttonTypeYes = new ButtonType("Yes");
            ButtonType buttonTypeNo = new ButtonType("No");
            popup.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);
            Stage stage = (Stage) popup.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image(UI.class.getResourceAsStream(logo)));

            Optional<ButtonType> result = popup.showAndWait();
            if (result.isPresent()) {
                if (result.get() == buttonTypeYes) {
                    writeDataframeToFile(file.getAbsolutePath(), true);
                } else if (result.get() == buttonTypeNo) {
                    writeDataframeToFile(file.getAbsolutePath(), false);
                }
            }
        }
    }

    @FXML
    public void save() {
        writeDataframeToFile(currentFile, currentlySavedWithHeader);
    }

    @FXML
    public void about() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.getDialogPane().setMinWidth(640);
        alert.getDialogPane().setMinHeight(160);
        alert.setTitle("About");
        alert.setHeaderText(null);
        alert.setContentText("Click-Click Plot 2020 \n" +
                "Version: 1.0.1 \n" +
                "More at github: @mslapek1 or @konrad-komisarczyk \n" +
                "Copywright: Konrad Komisarczyk, Mariusz Słapek 2019 - 2020");
        alert.setResizable(true);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(UI.class.getResourceAsStream(logo)));

        alert.showAndWait();
    }

    @FXML
    public void deleteColumn() {
        if (df.ncol() > 1) {
            TablePosition cell = table.getFocusModel().getFocusedCell();
            int col = cell.getColumn();
            ActionRemoveColumn actionRemoveColumn = new ActionRemoveColumn(df, col);
            actionsStack.addAndExecute(actionRemoveColumn);
            refresh();
        } else {
            showErrorWindow("Not posible to delete column, because Dataframe has to contain at least 1 column.");
        }
    }

    @FXML
    public void sortAscending() {
        TablePosition cell = table.getFocusModel().getFocusedCell();
        int col = cell.getColumn();

        // at first we set lexicographical comparator
        Comparator<String> cmp = Comparator.comparing(String::toString);
        try {
            for (int i = 0; i < this.df.nrow(); i++) {
                Double.parseDouble(df.getValueAt(col, i));
            }

            // if all column elements can be casted to Double, we set comparator to compare parsed Doubles
            cmp = Comparator.comparingDouble(Double::parseDouble);
        } catch (NumberFormatException ignored) {
            // if not all column elements can be casted to Double comparator stays lexicographical
        }

        ActionArrange actionArrange = new ActionArrange(df, col, cmp);
        actionsStack.addAndExecute(actionArrange);
        refresh();
    }

    @FXML
    public void sortDescending() {
        TablePosition cell = table.getFocusModel().getFocusedCell();
        int col = cell.getColumn();

        // at first we set lexicographical comparator
        Comparator<String> cmp = Comparator.comparing(String::toString).reversed();
        try {
            for (int i = 0; i < this.df.nrow(); i++) {
                Double.parseDouble(df.getValueAt(col, i));
            }

            // if all column elements can be casted to Double, we set comparator to compare parsed Doubles
            cmp = Comparator.comparingDouble(Double::parseDouble).reversed();
        } catch (NumberFormatException ignored) {
            // if not all column elements can be casted to Double comparator stays lexicographical
        }

        ActionArrange actionArrange = new ActionArrange(df, col, cmp);
        actionsStack.addAndExecute(actionArrange);
        refresh();
    }


    @FXML
    public void exportAs() {
        FileChooser fileChooser = new FileChooser();

        //Set extension filter
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("png files (*.png)", "*.png"));

        //Prompt user to select a file
        File file = fileChooser.showSaveDialog(null);

        WritableImage image = plotPane.snapshot(null,
                new WritableImage((int) plotPane.getWidth(), (int) plotPane.getHeight())
        );

        if (file != null) {
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private String renameColumnPopup(String oldName) {
        Stage popupwindow = new Stage();
        popupwindow.getIcons().add(new Image(UI.class.getResourceAsStream(logo)));
        popupwindow.initModality(Modality.APPLICATION_MODAL);
        popupwindow.setTitle("Rename Column");
        Label label1= new Label("Enter new column name.");
        TextField textField = new TextField(oldName);
        Button button1= new Button("Rename");
        button1.setOnAction(e -> popupwindow.close());
        VBox layout= new VBox(10);
        layout.getChildren().addAll(label1, textField, button1);
        layout.setAlignment(Pos.CENTER);
        Scene scene1= new Scene(layout, 300, 250);
        popupwindow.setScene(scene1);
        popupwindow.showAndWait();

        return textField.getText();
    }

    @FXML
    public void renameColumn() {
        TablePosition cell = table.getFocusModel().getFocusedCell();
        int col = cell.getColumn();

        String newName = renameColumnPopup(df.getColumnName(col));
        if (!newName.equals(df.getColumnName(col))) {
            ActionSetColumnName actionSetColumnName = new ActionSetColumnName(df, col, newName);
            actionsStack.addAndExecute(actionSetColumnName);
            refresh();
        }
    }

    @FXML
    public void generatePlot() {

        // Setting Geom
        Geom geom = Geom.BAR;
        if (geomLine.isSelected()) {
            geom = Geom.LINE;
        }
        if (geomScatter.isSelected()) {
            geom = Geom.SCATTER;
        }
        if (geomArea.isSelected()) {
            geom = Geom.AREA;
        }
        if (geomBar.isSelected()) {
            geom = Geom.BAR;
        }
        if (geomPie.isSelected()) {
            geom = Geom.PIE;
        }
        if (geomBubble.isSelected()) {
            geom = Geom.BUBBLE;
        }
        plot.setGeom(geom);

        //Setting Aes
        Aes aes = new Aes(aesX.getValue(), aesY.getValue(), aesSize.getValue());
        plot.setAes(aes);

        //Setting title
        plot.setTitle(plotTitleField.getText());

        plotPane.getChildren().clear();
        try {
            Chart chart = plot.getChart();
            plotHistory.add(chart);
            currentPlot = plotHistory.size() - 1;

            deletePlotButton.setDisable(false);
            nextPlotButton.setDisable(true);

            if (currentPlot > 0) {
                prevPlotButton.setDisable(false);
            } else {
                prevPlotButton.setDisable(true);
            }

            plotPane.getChildren().add(chart);
        } catch (PlotException e) {
            plotPane.getChildren().add(errorInfoPane(e.getMessage()));
            currentPlot = plotHistory.size();

            deletePlotButton.setDisable(true);
            nextPlotButton.setDisable(true);
            prevPlotButton.setDisable(false);
        }
    }

    private Node errorInfoPane(String error) {
        HBox hBox = new HBox();
        Text v = new Text(error);
        hBox.getChildren().add(v);
        hBox.setAlignment(Pos.CENTER);

        return hBox;
    }

    @FXML
    public void nextPlot() {
        if (currentPlot < plotHistory.size() - 1) {
            currentPlot ++;
            if (currentPlot == plotHistory.size() - 1) {
                nextPlotButton.setDisable(true);
            }
            prevPlotButton.setDisable(false);
            deletePlotButton.setDisable(false);

            plotPane.getChildren().clear();
            plotPane.getChildren().add(plotHistory.get(currentPlot));
        } else {
            showErrorWindow("Error occurred. Not possible to show next Plot.");
        }
    }

    @FXML
    public void prevPlot() {
        if (currentPlot > 0) {
            currentPlot --;
            if (currentPlot == 0) {
                prevPlotButton.setDisable(true);
            }
            if (currentPlot < plotHistory.size() - 1) {
                nextPlotButton.setDisable(false);
            } else {
                nextPlotButton.setDisable(true);
            }
            if (currentPlot >= 0) {
                deletePlotButton.setDisable(false);
            } else {
                deletePlotButton.setDisable(true);
            }

            plotPane.getChildren().clear();
            if (currentPlot >= 0) {
                plotPane.getChildren().add(plotHistory.get(currentPlot));
            }
        } else {
            plotPane.getChildren().clear();
            if (currentPlot <= 0) {
                prevPlotButton.setDisable(true);
            }
        }
    }

    @FXML
    public void deletePlot() {
        if (plotHistory.size() > 0) {
            plotHistory.remove(currentPlot);
            currentPlot = plotHistory.size() - 1;

            nextPlotButton.setDisable(true);

            if (currentPlot <= 0) {
                prevPlotButton.setDisable(true);
            } else {
                prevPlotButton.setDisable(false);
            }

            if (currentPlot < 0) {
                deletePlotButton.setDisable(true);
            } else {
                deletePlotButton.setDisable(false);
            }


            plotPane.getChildren().clear();
            if (currentPlot >= 0) {
                plotPane.getChildren().add(plotHistory.get(currentPlot));
            }
        } else {
            showErrorWindow("Error occurred. Not possible to delete plot.");
        }
    }

    private void showErrorWindow(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setResizable(false);
        alert.setHeaderText(message);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(UI.class.getResourceAsStream(logo)));
        alert.show();
    }
}