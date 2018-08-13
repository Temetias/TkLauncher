package main;

import java.awt.*;
import java.io.*;
import java.util.LinkedList;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public final class Main extends Application {

	public SystemTrayHandler systemTrayHandler;
	private IPCHandler ipc;
	private Thread ipcThread;

	// The list of commands.
	public static LinkedList<Command> commands = new LinkedList<Command>();
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(final Stage mainStage) throws Exception {

		// Attempt access to system tray.
		systemTrayHandler = new SystemTrayHandler();

		// Attempt connection to the python-script handling the hot-key, etc.
		ipc = new IPCHandler(mainStage);
		ipcThread = new Thread(ipc);
		ipcThread.start();

		// TODO: Pretty much everything here.
		if (systemTrayHandler.systemTray == null) {
			System.out.println("nulli o");
		} else {

		}

		// TODO: Pretty much everything here.
		if (ipc == null) {
			System.err.println("nulli o");
		} else {

		}

		// Read file.
		try {
			commands = read();
		} catch (Exception e) {
			// Create new list if there is not file to read.
			commands = new LinkedList<Command>();
		}

		// Content
		final Label talker = new Label("So, what are we doing today?"); // NOTE: this is the main communication label.
		final Button goBtn = new Button("Go!");
		final TextField textField = new TextField();
		final MenuButton options = new MenuButton();
		final HBox hbox2 = new HBox(options);
		final HBox hbox = new HBox(textField, goBtn, hbox2);
		final VBox vbox = new VBox(talker, hbox);
		final Scene scene = new Scene(vbox, 420, 70);

		// TODO: Styling.

		// Main textfield handling
		textField.setOnKeyPressed((KeyEvent event) -> {
			if (event.getCode().equals(KeyCode.ENTER)) {
				if (go(textField, talker, mainStage)) {
					talker.setText("There you go, have fun! I'll be waiting in your taskbar.");
					// Sleep for 2000ms so that the user can see the message.
					sleep(mainStage, 2000, talker);
				} else {
					talker.setText("Didn't quite catch that. Are you sure you typed it right?");
				}
			}
		});

		// Same functionality as "Enter" -key event on the textfield.
		goBtn.setOnAction((ActionEvent event) -> {
			if (go(textField, talker, mainStage)) {
				talker.setText("There you go, have fun! I'll be waiting in your taskbar.");
				// Sleep for 2000ms so that the user can see the message.
				sleep(mainStage, 2000, talker);
			} else {
				talker.setText("Didn't quite catch that. Are you sure you typed it right?");
			}
		});

		// Build options menu.
		MenuItem addCommand = new MenuItem("Add command");
		addCommand.setOnAction((ActionEvent event) -> {
			mainStage.setScene(CommandCreationView.commandCreationScene(mainStage, scene));
		});
		MenuItem browseCommands = new MenuItem("Your commands");
		browseCommands.setOnAction((ActionEvent event) -> {
			mainStage.setScene(CommandBrowsingView.commandBrowsingScene(mainStage, scene));
		});
		options.getItems().addAll(addCommand, browseCommands);

		// Stage styling.
		final Image image = new Image("FILE:options.png", 17, 17, false, false);
		options.setGraphic(new ImageView(image));
		talker.setFont(Font.font(15));
		talker.setPadding(new Insets(5, 0, 5, 10));
		hbox.setPadding(new Insets(0, 0, 0, 10));
		hbox2.setPadding(new Insets(0, 0, 0, 158));

		// Stage behaviour.
		mainStage.setTitle("TkLauncher");
		mainStage.setResizable(false);
		mainStage.setScene(scene);
		mainStage.getIcons().add(new Image("FILE:tklauncher.png"));
		mainStage.show();
	}

	/**
	 * Handles the GUI side of the event of inputting a command. Happens on go-button press and enter.
	 * 
	 * @param textField The main command textfield.
	 * @param talker The main communication label.
	 * @param mainStage The mainstage.
	 * @return Boolean stating if we could find and execute a command.
	 */
	private boolean go(TextField textField, Label talker, Stage mainStage) {
		String request = textField.getText();
		try {
			if (handleRequest(request, talker)) {
				textField.clear();
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			talker.setText("Something went badly wrong when trying to execute your task, sorry. :(");
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Generic sleep function that is used when the user correctly types a command.
	 * 
	 * @param mainStage The mainstage.
	 * @param time The time we want the stage to sleep.
	 */
	private void sleep(Stage mainStage, int time, Label talker) {
		// Timeout so the user can see the responsetext from the program.
		Task<Void> sleep = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				try {
					Thread.sleep(time);
				} catch (InterruptedException e) {
					talker.setText("I should be minimized, but something went wrong. :(");
				}
				return null;
			}
		};
		sleep.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent event) {
				mainStage.setIconified(true);
				talker.setText("Oh, you're back! Need something else?");
			}
		});
		new Thread(sleep).start();
	}
	
	/**
	 * Handles the logic-side of the commands. Search for matching commands and try to open programs accordingly.
	 *  
	 * @param request The request typed by the user.
	 * @return True for command found and programs opened, false for failing to find commands.
	 */
	private boolean handleRequest(String request, Label talker) {
		for (Command command: commands) {
			if (request.toLowerCase().equals(command.name.toLowerCase())) {
				for (File app: command.apps) {
					if (Desktop.isDesktopSupported()) {
						new Thread(() -> {
							try {
								Desktop.getDesktop().open(app);
							} catch (Exception e) {
								talker.setText("Something went badly wrong when trying to execute your task, sorry. :(");
								e.printStackTrace();
							}
						}).start();
					}
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Writes the the list of commands to a file for later use.
	 */
	public static void write() {
		try {
			FileOutputStream fout = new FileOutputStream("tmp.txt");
			ObjectOutputStream out = new ObjectOutputStream(fout);
			out.writeObject(commands);
			out.flush();
			out.close();
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Reads saved data from a file. The saved data contains the list and data related to the commands.
	 * 
	 * @return The list of commands.
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private LinkedList<Command> read() throws Exception {
		FileInputStream fin = new FileInputStream("tmp.txt");
		ObjectInputStream in = new ObjectInputStream(fin);
		commands = (LinkedList<Command>) in.readObject();
		in.close();
		return commands;
	}
	
}
