/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxmousemove;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author sl
 */
public class JavaFXMouseMove extends Application {

	DatagramSocket socket;
	InetAddress serverAddr;
	int serverPort = 8033;
	Color clientColor;
	Point2D point = new Point2D(0, 0);
	Point2D sentPoint = new Point2D(0, 0);
	Timer timer = new Timer();

	@Override
	public void start(Stage primaryStage) {
		Label lbl = new Label();

		StackPane root = new StackPane();
		root.getChildren().add(lbl);

		Scene scene = new Scene(root, 800, 600);

		primaryStage.setTitle("Mouse position");

		final Parameters params = getParameters();
		final List<String> parameters = params.getRaw();

		try {
			if (!parameters.isEmpty()) {
				serverAddr = InetAddress.getByName(parameters.get(0));
				clientColor = Color.valueOf(parameters.get(1));
				socket = new DatagramSocket();
				TimerTask task = new TimerTask() {
					@Override
					public void run() {
						if (sentPoint != point) {
							DatagramPacket pac;
							String dataStr = String.valueOf(clientColor.toString()) + "|" + String.valueOf(point.getX()) + "|" + String.valueOf(point.getY());
							byte[] data = dataStr.getBytes();
							pac = new DatagramPacket(data, data.length, serverAddr, serverPort);
							try {
								socket.send(pac);
							} catch (IOException ex) {
								System.out.println(ex.getMessage());
							}
							sentPoint = point;
						}
					}
				};
				timer.schedule(task, 1000, 50);
			}
		} catch (NumberFormatException | SocketException | UnknownHostException ex) {
			System.out.println(ex.getMessage());
		}

		scene.setOnMouseMoved((MouseEvent event) -> {
			lbl.setText(String.valueOf(event.getX()) + ", " + String.valueOf(event.getY()));
			if (socket != null) {
				point = new Point2D(event.getX(), event.getY());
			}
		});
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent t) {
				Platform.exit();
				System.exit(0);
			}
		});
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}

}
