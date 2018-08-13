package main;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;

public final class CommandBrowsingView {
	
	// The custom listview element with the deletion button.
	static class XCell extends ListCell<Command> {
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
            	Main.commands.clear();
				Main.commands.addAll(getListView().getItems());
				Main.write();
            });  
        }
        @Override
        protected void updateItem(Command item, boolean empty) {
            super.updateItem(item, empty);
            setText(null);
            setGraphic(null);

            if (item != null && !empty) {
                label.setText(item.name);
                setGraphic(hbox);
            }
        }
    }

    public static Scene commandBrowsingScene(Stage stage, Scene mainscene) {
    	
    	stage.setTitle("TkLauncher - Commands");
    	
    	final ListView<Command> commandList = new ListView<>();
        final Button back = new Button("Back");
        final HBox hbox = new HBox(back);
        final VBox vbox = new VBox(commandList, hbox);
        final Scene scene = new Scene(vbox, 500, 220); 
        
        // TODO: Styling.
        
        // Apply the custom cells.
        commandList.setCellFactory(param -> new XCell());
        ObservableList<Command> commands = FXCollections.observableArrayList(Main.commands);
        commandList.setItems(commands);
        
        back.setOnAction((ActionEvent e) -> {
            stage.setScene(mainscene);
            stage.setTitle("TkLauncher");
        });
        
        // Styling.
        commandList.setMaxHeight(140);
        commandList.setPrefHeight(140);
        vbox.setPadding(new Insets(20, 20, 20, 20));
        hbox.setPadding(new Insets(15, 0, 0, 0));

        return scene;
    }
}
