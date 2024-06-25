package com.pdfConvert.controllers.home;

import com.pdfConvert.util.ConvertUtil;
import com.pdfConvert.util.FileUtils;
import com.pdfConvert.util.Logger;
import com.pdfConvert.util.Utility;
import com.pdfConvert.util.gui.BuilderUI;
import com.pdfConvert.util.gui.component.FileBox;
import com.pdfConvert.util.validation.SingleInstance;
import com.jfoenix.controls.JFXProgressBar;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.awt.*;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;

public class HomeController implements Initializable {
    private List<File> files = new ArrayList<>();
    private File outputDir;

    @FXML
    private Label status;

    @FXML
    private VBox fileDetailsBox, chooseFileBox, progressBox, filesContainer;

    @FXML
    private AnchorPane inputBox;

    @FXML
    private TextField outputDirTextField;

    @FXML
    private Button convertButton, chooseOutputDirButton;

    @FXML
    private JFXProgressBar progressBar;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize the UI components and set initial states
        convertButton.setDisable(true);
        convertButton.setText("Not ready to convert yet");

        fileDetailsBox.setVisible(false);
        chooseFileBox.setVisible(true);
        progressBox.visibleProperty().bind(convertButton.disabledProperty().not());
        progressBox.visibleProperty().addListener((observable, oldValue, newValue) -> {
            progressBar.setProgress(0);
            status.setText("");
        });
    }

    /**
     * Handle the drag over event for files.
     */
    @FXML
    private void handleFileOverEvent(DragEvent event) {
        if (files != null && !files.isEmpty()) return;
        Dragboard db = event.getDragboard();
        if (db.hasFiles()) {
            event.acceptTransferModes(TransferMode.COPY);
        } else {
            event.consume();
        }
    }

    /**
     * Handle the drop event for files.
     */
    @FXML
    private void handleFileDroppedEvent(DragEvent event) {
        if (files != null && !files.isEmpty()) return;
        files = event.getDragboard().getFiles();
        if (files == null) {
            files = new ArrayList<>();
        } else {
            // Filter out non-PDF files
            files.removeIf(file -> !FileUtils.getFileExtension(file).equals(".pdf"));
        }
        setFilesData(files);

        if (canConvert()) {
            convertButton.setDisable(false);
            convertButton.setText("Convert");
        }
    }

    /**
     * Remove all selected files.
     */
    @FXML
    private void removeAllFiles() {
        if (files == null) {
            files = new ArrayList<>();
        }
        files.clear();
        setFilesData(files);

        convertButton.setDisable(true);
        convertButton.setText("Not ready to convert yet");
    }

    /**
     * Set the file data to the UI.
     */
    private void setFilesData(List<File> filesChosen) {
        System.out.println(filesChosen);
        if (filesChosen == null || filesChosen.isEmpty()) {
            filesContainer.getChildren().clear();
            fileDetailsBox.setVisible(false);
            chooseFileBox.setVisible(true);
            inputBox.setOnMouseClicked(event -> chooseFile());
        } else {
            fileDetailsBox.setVisible(true);
            chooseFileBox.setVisible(false);
            inputBox.setOnMouseClicked(null);
            filesContainer.getChildren().clear();
            filesChosen.forEach(file -> {
                try (final PDDocument DOCUMENT = PDDocument.load(file)) {
                    filesContainer.getChildren().add(new FileBox(file.getName(), DOCUMENT.getNumberOfPages()));
                } catch (Exception ex) {
                    Logger.error(ex.getLocalizedMessage(), ex, getClass().getName() + ".setFilesData()");
                }
            });
        }
    }

    /**
     * Open a file chooser to select PDF files.
     */
    @FXML
    private void chooseFile() {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose PDF");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PDF Only", "*.pdf", "*.PDF"));
        files.clear();
        final List<File> list = fileChooser.showOpenMultipleDialog(SingleInstance.getInstance().getCurrentStage());
        if (list != null) {
            files.addAll(list);
        }
        if (files != null) {
            setFilesData(files);
        }
        if (canConvert()) {
            convertButton.setDisable(false);
            convertButton.setText("Convert");
        }
    }

    /**
     * Open a directory chooser to select an output directory.
     */
    @FXML
    private void chooseOutputDir() {
        final DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choose output Folder");
        outputDir = directoryChooser.showDialog(SingleInstance.getInstance().getCurrentStage());
        if (outputDir != null) {
            outputDirTextField.setText(outputDir.getAbsolutePath());
        }
        if (canConvert()) {
            convertButton.setDisable(false);
            convertButton.setText("Convert");
        }
    }

    /**
     * Get the total number of pages in the selected PDF files.
     */
    private int getNumberOfPages(List<File> filesList) {
        int count = 0;
        for (File file : filesList) {
            try (final PDDocument DOCUMENT = PDDocument.load(file)) {
                count += DOCUMENT.getNumberOfPages();
            } catch (Exception ex) {
                Logger.error(ex.getLocalizedMessage(), ex, getClass().getName() + ".setFilesData()");
            }
        }
        return count;
    }

    /**
     * Convert the selected PDF files to images.
     */
    @FXML
    private void convert() {
        convertButton.setVisible(false);
        inputBox.setDisable(true);
        chooseOutputDirButton.setDisable(true);

        final int DPI = 300;
        final String FILE_EXTENSION = "png";
        final int totalNumOfPagesOfFiles = getNumberOfPages(files);
        AtomicInteger currentPageStat = new AtomicInteger();
        new Thread(() ->
                files.forEach(file -> {
                    try {
                        ConvertUtil.convert(file, outputDir, FILE_EXTENSION, DPI, (currentPage, totalNumberOfPages) -> {
                            Platform.runLater(() -> {
                                progressBar.setProgress((currentPageStat.get() + 1) * 1.0 / totalNumOfPagesOfFiles);

                                status.setText("Page " + (currentPageStat.get() + 1) + " Converted " +
                                        Utility.formatNum(((currentPageStat.get() + 1) * 1.0 / totalNumOfPagesOfFiles) * 100) + "%");
                            });

                            System.out.println("Page " + (currentPageStat.get() + 1) + " Converted ==> " +
                                    Utility.formatNum(((currentPageStat.get() + 1) * 1.0 / totalNumOfPagesOfFiles) * 100) + "%");
                            currentPageStat.getAndIncrement();
                        });
                        // If it's the last file, re-enable the UI components
                        if (files.indexOf(file) == (files.size() - 1)) {
                            Platform.runLater(() -> {
                                convertButton.setVisible(true);
                                inputBox.setDisable(false);
                                chooseOutputDirButton.setDisable(false);
                            });
                        }
                    } catch (Exception ex) {
                        Logger.error(ex.getLocalizedMessage(), ex, getClass().getName() + ".convert()");
                        Platform.runLater(() -> BuilderUI.showExceptionDialog(ex));
                    }
                })).start();
    }

    /**
     * Check if the application is ready to start converting files.
     */
    private boolean canConvert() {
        return (files != null && !files.isEmpty()) && (outputDir != null);
    }

    /**
     * Open the "About" page in the default web browser.
     */
    @FXML
    private void openAbout() {
        try {
            Desktop.getDesktop().browse(new URI("https://github.com/SergeyM96"));
        } catch (Exception ex) {
            Logger.error(ex.getLocalizedMessage(), ex, getClass().getName() + ".openAbout()");
        }
    }
}
