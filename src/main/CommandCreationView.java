package main;

import java.io.File;
import java.util.LinkedList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.FileChooser;

public final class CommandCreationView {
	
	// The custom listview element with the deletion button.
	static class XCell extends ListCell<File> {
        HBox hbox = new HBox();
        Label label = new Label("");
        Pane pane = new Pane();
        Button deleteBtn = new Button();
        final Image image = new Image("FILE:delete.png", 17, 17, false, false);

        public XCell() {
            super();
            hbox.getChildren().addAll(label, pane, deleteBtn);
            HBox.setHgrow(pane, Priority.ALWAYS);
            deleteBtn.setGraphic(new ImageView(image));
            deleteBtn.setOnAction((ActionEvent e) -> {
            	getListView().getItems().remove(getItem());
            });     
        }
        @Override
        protected void updateItem(File item, boolean empty) {
            super.updateItem(item, empty);
            setText(null);
            setGraphic(null);
            if (item != null && !empty) {
                label.setText(item.toString());
                setGraphic(hbox);
            }
        }
    }
	
	// Temporary list used by the GUI. Contents copied to actual storagelist when the command is saved.
	private static LinkedList<File> programListTemp = new LinkedList<File>();
	
	public static Scene commandCreationScene(Stage stage, Scene mainscene) {
		
		stage.setTitle("TkLauncher - New command");

		// Content
		final Label title = new Label("Create a new command");
		final Label label = new Label("Give a name for the command. This is what you use to launch the command.");
		final TextField commandNameTf = new TextField();
		final ListView<File> list = new ListView<File>();
		final Button browse = new Button("Add programs and files");
		final Button save = new Button("Save");
		final Button cancel = new Button("Cancel");
		final HBox hbox = new HBox(save, cancel);
		final VBox vbox2 = new VBox(label, commandNameTf);
		final VBox vbox = new VBox(title, vbox2, list, browse, hbox);
		final Scene scene = new Scene(vbox, 500, 330);

		// TODO: Styling.
		
		// Custom listview elements.
		list.setCellFactory(params -> new XCell());

		browse.setOnAction((ActionEvent e) -> {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Add a program to the command");
			programListTemp.push(fileChooser.showOpenDialog(stage));

			// Update listview.
			ObservableList<File> items = FXCollections.observableArrayList(programListTemp);
			list.setItems(items);
		});

		save.setOnAction((ActionEvent e) -> {
			boolean success = true;
			String commandName = commandNameTf.getText();
			LinkedList<File> programList = new LinkedList<File>();
			programList.addAll(programListTemp);
			
			// Test if the command is taken.
			for (Command cmd: Main.commands) {
				if (cmd.name.toLowerCase().equals(commandName.toLowerCase())) {
					success = false;
					showError("Saving error", "Couldn't save your command.", "Command name is already in use. Use a different command name.");
					break;
				}
			}		
			// Test if there is nothing in the command.
			if (programListTemp.isEmpty()) {
				success = false;
				showError("Saving error", "Couldn't save your command.", "Your command doesn't have any programs or files in it.");
			}
			if (commandName.length() == 0) {
				success = false;
				showError("Saving error", "Couldn't save your command", "The command doesn't have a launch name. Please name your command.");
			}
			// If all is good, push the new command and return back from the scene.
			if (success) {
				Main.commands.push(new Command(commandName, programList));
				Main.write();
				programListTemp.clear();
				stage.setScene(mainscene);	
			}
		});
		
		cancel.setOnAction((ActionEvent e) -> {
			stage.setScene(mainscene);
			stage.setTitle("TkLauncher");
			programListTemp.clear();
		});
		
		// Styling.
		title.setFont(Font.font(15));
        title.setPadding(new Insets(10, 0, 10, 0));
        list.setMaxHeight(140);
        list.setPrefHeight(140);
		vbox2.setPadding(new Insets(10, 0, 10, 0));
		vbox.setPadding(new Insets(10, 10, 10, 10));
		hbox.setPadding(new Insets(10, 0, 10, 0));
		
		return scene;
	}
	
	/**
	 * Generic alertbox function.
	 * 
	 * @param title Alert title.
	 * @param header Alert header.
	 * @param content Alert content.
	 */
	private static void showError(String title, String header, String content) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.showAndWait();
	}
}
