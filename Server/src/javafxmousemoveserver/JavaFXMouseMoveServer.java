/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxmousemoveserver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author sl
 */
public class JavaFXMouseMoveServer extends Application {

	final int serverPort = 8033;
	DatagramSocket socket;
	GraphicsContext gc;
	Timer timer = new Timer();
	Map<Color, Point2D> points = new HashMap<>();

	private void processMessage(String message) {
		String[] parts = message.split("\\|");
		Color color = Color.valueOf(parts[0]);
		Point2D point = new Point2D(Double.parseDouble(parts[1]), Double.parseDouble(parts[2]));

		if (points.containsKey(color)) {
			gc.clearRect(points.get(color).getX(), points.get(color).getY(), 10, 10);
		}
		drawPointer(color, point);
		points.put(color, point);
	}

	private void drawPointer(Color color, Point2D point) {
		gc.setFill(color);
		gc.fillOval(point.getX(), point.getY(), 10, 10);
	}

	@Override
	public void start(Stage primaryStage) {

		Group root = new Group();
		Canvas canvas = new Canvas(800, 600);
		gc = canvas.getGraphicsContext2D();
		root.getChildren().add(canvas);

		primaryStage.setTitle("Listening mouse move");
		primaryStage.setScene(new Scene(root));
		try {
			socket = new DatagramSocket(serverPort);
			TimerTask task = new TimerTask() {
				@Override
				public void run() {
					byte data[] = new byte[100];

					DatagramPacket pac = new DatagramPacket(data, data.length);
					try {
						socket.setSoTimeout(50);
						socket.receive(pac);
						processMessage(new String(data));
					} catch (Exception ex) {

					}
				}
			};
			timer.schedule(task, 1000, 50);
		} catch (SocketException ex) {
			System.out.println(ex.getMessage());
		}
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent t) {
				Platform.exit();
				System.exit(0);
			}
		});

		primaryStage.show();
	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
