package unnc.cs.grape.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXSlider;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import unnc.cs.grape.MainApp;
import unnc.cs.grape.algorithm.Algorithm_c;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

/**
 * The type Main frame controller.
 */
public class MainFrameController extends Algorithm_c implements Initializable {
	private final MainApp mainapp = new MainApp();

	private SequentialTransition st = new SequentialTransition();
	private SequentialTransition sq_c_1 = new SequentialTransition();
	private SequentialTransition sq_c_2 = new SequentialTransition();
	private int[] input;
	private int[] input_c;
	private final int[] defaultInput = { 4, 3, 2, 1, 5, 6, 9, 7, 8 };
	private int sortOrder = 0;
	private double duration = 600;
	private double duration_c = 100;
	private String selectAlgo = null;
	private String languageSelect = "Java";
	private final ArrayList<StackPane> list = new ArrayList<>();
	private final ArrayList<StackPane> list_l = new ArrayList<>();
	private final ArrayList<StackPane> list_r = new ArrayList<>();
	private static ArrayList<Rectangle> recList = new ArrayList<>();
	private final ArrayList<ToggleButton> toggleList = new ArrayList<>();
	private static Color color_change = Color.valueOf("#1565C0");

	private final Image pause = new Image("unnc/cs/grape/view/assets/icon/pause.png", 44, 46, false, false);
	private final Image play = new Image("unnc/cs/grape/view/assets/icon/play.png", 44, 46, false, false);
	private final Image bubbleGif = new Image("unnc/cs/grape/view/assets/icon/bubble.gif", 92, 76, false, false);
	private final Image bubbleJpg = new Image("unnc/cs/grape/view/assets/icon/bubble.jpg", 92, 76, false, false);

	@FXML
	private TextField inputString;

	@FXML
	private HBox hbox;

	@FXML
	private HBox hbox_left;

	@FXML
	private HBox hbox_right;

	@FXML
	private ToggleButton bubble;

	@FXML
	private ToggleButton insertion;

	@FXML
	private ToggleButton selection;

	@FXML
	private ToggleButton quick;

	@FXML
	private ToggleButton merge;

	@FXML
	private ToggleButton heap;

	@FXML
	private ImageView playbutton;

	@FXML
	private ImageView bubbleImg;
	@FXML
	private ImageView insertionImg;
	@FXML
	private ImageView selectionImg;
	@FXML
	private ImageView quickImg;
	@FXML
	private ImageView mergeImg;
	@FXML
	private ImageView heapImg;

	@FXML
	private Pane pane;

	@FXML
	private JFXSlider volumeSlider;

	@FXML
	private Slider timeSlider;

	@FXML
	private JFXSlider slider_c;

	@FXML
	private TextArea codeDisplay;

	@FXML
	private TextArea hintDisplay;

	@FXML
	private JFXButton start;

	@FXML
	private TextField inputString_c;

	@FXML
	private Label label_left;

	@FXML
	private Label label_right;

	@FXML
	private Label time_left;

	@FXML
	private Label time_right;

	@FXML
	private Label firstAlgoComplex;

	@FXML
	private Label secondAlgoComplex;

	@FXML
	private JFXComboBox<Label> combo1, combo2;
	private String compareAlgo1, compareAlgo2;

	/**
	 * Press to start sorting
	 */
	@FXML
	public void sortStart() {
		if (hbox.getChildren() == null || input == null) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Warning Dialog");
			alert.setHeaderText(null);
			alert.setContentText("Please initialize first!");
			alert.showAndWait();

			System.out.println("Please initialize first...");
		} else if (selectAlgo == null) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Warning Dialog");
			alert.setHeaderText(null);
			alert.setContentText("Please choose an algorithm to run!");
			alert.showAndWait();
		} else {
			sort(selectAlgo);
		}
	}

	private void changePlayButton() {
		playbutton.setId("playbutton");
		playbutton.setImage(play);
		start.setOnAction(event -> sortStart());
	}

	private void changePauseButton() {
		playbutton.setId("pausebutton");
		playbutton.setImage(pause);
		start.setOnAction(event -> {
			st.pause();
			changeReplayButton();
		});
	}

	private void changeReplayButton() {
		playbutton.setId("replaybutton");
		playbutton.setImage(play);
		start.setOnAction(event -> {
			st.play();
			timeSlider.setDisable(false);
			changePauseButton();
		});
	}

	public void timeChange() {
		timeSlider.valueProperty().addListener(e -> {
			if (timeSlider.isValueChanging() && timeSlider.getValue() != 0) {
				st.pause();
				st.playFrom(st.getTotalDuration().multiply(timeSlider.getValue() / 100));
				if (playbutton.getId() == "replaybutton") {
					st.pause();
				}
			}
		});
	}

	private void updateProgress() {
		Platform.runLater(() -> {
			Duration currentTime = st.getCurrentTime();
			Duration duration = st.getTotalDuration();
			if (!currentTime.equals(duration)) {
				timeSlider.setValue(currentTime.divide(duration.toMillis()).toMillis() * 100);
			} else {
				timeSlider.setDisable(true);
				inputString.setDisable(true);
				st.stop();
			}
		});
	}

	/**
	 * Speed change.
	 */
	@FXML
	public void speedChange() {
		volumeSlider.valueProperty()
				.addListener((observable, oldValue, newValue) -> st.setRate((double) (newValue) / 40));
	}

	@FXML
	public void bubbleEnter() {
		bubbleImg.setImage(bubbleGif);
	}

	@FXML
	public void bubbleExit() {
		bubbleImg.setImage(bubbleJpg);
	}

	@FXML
	public void insertionEnter() {
		insertionImg.setImage(new Image("unnc/cs/grape/view/assets/icon/insertion.gif", 92, 76, false, false));
	}

	@FXML
	public void insertionExit() {
		insertionImg.setImage(new Image("unnc/cs/grape/view/assets/icon/insertion.jpg", 92, 76, false, false));
	}

	@FXML
	public void selectionEnter() {
		selectionImg.setImage(new Image("unnc/cs/grape/view/assets/icon/selection.gif", 92, 76, false, false));
	}

	@FXML
	public void selectionExit() {
		selectionImg.setImage(new Image("unnc/cs/grape/view/assets/icon/selection.jpg", 92, 76, false, false));
	}

	@FXML
	public void quickEnter() {
		quickImg.setImage(new Image("unnc/cs/grape/view/assets/icon/quick.gif", 92, 76, false, false));
	}

	@FXML
	public void quickExit() {
		quickImg.setImage(new Image("unnc/cs/grape/view/assets/icon/quick.jpg", 92, 76, false, false));
	}

	@FXML
	public void mergeEnter() {
		mergeImg.setImage(new Image("unnc/cs/grape/view/assets/icon/merge.gif", 92, 76, false, false));
	}

	@FXML
	public void mergeExit() {
		mergeImg.setImage(new Image("unnc/cs/grape/view/assets/icon/merge.jpg", 92, 76, false, false));
	}

	@FXML
	public void heapEnter() {
		heapImg.setImage(new Image("unnc/cs/grape/view/assets/icon/heap.gif", 92, 76, false, false));
	}

	@FXML
	public void heapExit() {
		heapImg.setImage(new Image("unnc/cs/grape/view/assets/icon/heap.jpg", 92, 76, false, false));
	}

	/**
	 * Clear.
	 */
	public void clear() {
		inputString.clear();
	}

	/**
	 * Initialize rec.
	 */
	public void initializeRec() {
		// clear
		// mergeList.clear();s
		pane.getChildren().clear();
		recList.clear();
		list.clear();
		hbox.getChildren().clear();

		String str = inputString.getText();
		checkInput(str);
		try {
			if (input.length > 15) {
				Alert alert = new Alert(AlertType.WARNING);
				alert.setTitle("Warning Dialog");
				alert.setHeaderText(null);
				alert.setContentText("The input size should less than 15!");
				alert.showAndWait();
			} else {
				if (!checkMaxAndMinInput()) {
					Alert alert = new Alert(AlertType.WARNING);
					alert.setTitle("Warning Dialog");
					alert.setHeaderText(null);
					alert.setContentText("The input integer shouldn't be larger than 15 or smaller than 1!");
					alert.showAndWait();
				} else {
					generateRec();
				}
			}
		} catch (NullPointerException e) {
			System.out.println("---Exception---");
		}
	}

	private boolean checkMaxAndMinInput() {
		for (int i : input) {
			if (i > 15)
				return false;
			if (i < 1)
				return false;
		}
		return true;
	}

	private void checkInput(String str) {
		// if has input
		if (str.length() > 0) {
			char ch = str.charAt(0);
			if (Character.isDigit(ch)) {
				String[] sp = str.split("\\D+");
				input = Stream.of(sp).mapToInt(Integer::parseInt).toArray();
			} else {
				Alert alert = new Alert(AlertType.WARNING);
				alert.setTitle("Warning Dialog");
				alert.setHeaderText(null);
				alert.setContentText("The input should start with an integer!");
				alert.showAndWait();
			}
		} else {
			System.out.println("No input, use default input...");
			input = defaultInput;
		}
	}

	/**
	 * Random input.
	 */
	public void randomInput() {
		System.out.println("Generate a random input");
		int[] random = new int[15];
		for (int i = 0; i < 15; i++) {
			random[i] = (int) (Math.random() * 15 + 1);
		}
		input = random;
		String strInput = Arrays.toString(random);
		inputString.clear();
		inputString.setText(strInput.substring(1, strInput.length() - 1));

		recList.clear();
		list.clear();
		hbox.getChildren().clear();
		pane.getChildren().clear();
		generateRec();
	}

	/**
	 * Create rectangles into Hbox
	 */
	private void generateRec() {
		Color shapeColor = PreferenceController.color;

		if (input == null || input.length == 0) {
			input = defaultInput;
		}

		for (int anInput : input) {
			Rectangle rectangle = new Rectangle(20, 20 * anInput);
			rectangle.setFill(shapeColor);
			recList.add(rectangle);

			Text text = new Text(String.valueOf(anInput));
			StackPane sp = new StackPane();
			sp.setPrefSize(rectangle.getWidth(), rectangle.getHeight());
			sp.setId(String.valueOf(anInput));
			sp.getChildren().addAll(rectangle, text);
			sp.setAlignment(Pos.BOTTOM_CENTER);
			list.add(sp);
		}

		hbox.getChildren().addAll(list);
	}

	private void displayCode(String language, String algo) {
		String fileName = "./code/" + algo + language + ".txt";
		try {
			codeDisplay.setText(new String(Files.readAllBytes(Paths.get(fileName)), StandardCharsets.UTF_8));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void displayHint(String algo) {
		String fileName = "./code/" + algo + "Hint.txt";
		try {
			hintDisplay.setText(new String(Files.readAllBytes(Paths.get(fileName)), StandardCharsets.UTF_8));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Choose which algorithm to use
	 */
	@FXML
	public void chooseAlgo() {
		toggleList.forEach(button -> button.setOnMouseClicked(event -> {
			inputString.setDisable(false);
			changePlayButton();
			selectAlgo = button.getAccessibleText();
			System.out.println(selectAlgo);
			displayCode(languageSelect, selectAlgo);
			displayHint(selectAlgo);
		}));
	}

	private void sort(String selectAlgo) {
		inputString.setDisable(true);
		changePauseButton();
		st = new SequentialTransition();
		initializeRec();

		switch (selectAlgo) {
		case "bubble":
			st = BubbleSort(input, list, duration);
			break;
		case "insert":
			st = InsertionSort(input, list, duration);
			break;
		case "select":
			st = SelectionSort(input, list, duration);
			break;
		case "quick":
			st = QuickSort(input, st, duration);
			break;
		case "merge":
			st = MergeSort(input, st, duration);
			break;
		case "heap":
			st = HeapSort(input, list, duration);
			break;
		default:
			break;
		}

		st.currentTimeProperty().addListener(observable -> {
			updateProgress();
		});
		st.setRate(volumeSlider.getValue() / 40);
		st.setOnFinished(event -> {
			inputString.setDisable(false);
			changePlayButton();
		});
		st.play();
		timeSlider.setDisable(false);

		String str = inputString.getText();
		checkInput(str);
	}

	@FXML
	private void handleHelpBox() {
		mainapp.showHelpBox();
	}

	@FXML
	private void handlePreference() {
		mainapp.showPreference();
	}

	@FXML
	private void handleSaveCode() {
		mainapp.SaveAlgorithmCode(selectAlgo, languageSelect);
	}

	@FXML
	private void handleGuideline() {
		mainapp.showGuideline();
	}

	@FXML
	private void handleQuit() {
		mainapp.QuitProgram();
	}

	@FXML
	private void handleScreenshot() {
		mainapp.Screenshot();
	}

	@FXML
	private void handleJava() {
		languageSelect = "Java";
		displayCode(languageSelect, selectAlgo);
	}

	@FXML
	private void handleJavaScript() {
		languageSelect = "JS";
		displayCode(languageSelect, selectAlgo);
	}

	@FXML
	private void handleC() {
		languageSelect = "C";
		displayCode(languageSelect, selectAlgo);
	}

	@FXML
	private void handlePython() {
		languageSelect = "Python";
		displayCode(languageSelect, selectAlgo);
	}

	@FXML
	private void handlePHP() {
		languageSelect = "PHP";
		displayCode(languageSelect, selectAlgo);
	}

	@FXML
	private void handleAscendingOrder() {
		sortOrder = 0;
		;
	}

	@FXML
	private void handleDescendingOrder() {
		sortOrder = 1;
	}

	private ParallelTransition changeColor(StackPane l1, StackPane l2, Color from, Color to) {
		Rectangle rec1 = (Rectangle) l1.getChildren().get(0);
		Rectangle rec2 = (Rectangle) l2.getChildren().get(0);

		FillTransition f1 = new FillTransition(Duration.millis(10), rec1, from, to);
		FillTransition f2 = new FillTransition(Duration.millis(10), rec2, from, to);

		ParallelTransition pl = new ParallelTransition();
		pl.getChildren().addAll(f1, f2);

		return pl;
	}

	// sort control part
	private ParallelTransition swap(StackPane l1, StackPane l2, ArrayList<StackPane> list, double speed) {
		TranslateTransition t1 = new TranslateTransition(Duration.millis(speed), l1);
		TranslateTransition t2 = new TranslateTransition(Duration.millis(speed), l2);

		ParallelTransition pl = new ParallelTransition();
		t1.setByX(30);
		t2.setByX(-30);
		pl.getChildren().addAll(t1, t2);
		Collections.swap(list, list.indexOf(l1), list.indexOf(l2));
		return pl;
	}

	private ParallelTransition swap(StackPane l1, StackPane l2, int xLength, ArrayList<StackPane> list, double speed) {
		if (xLength < 0) {
			xLength = 0 - xLength;
		}

		TranslateTransition t1 = new TranslateTransition(Duration.millis(speed), l1);
		TranslateTransition t2 = new TranslateTransition(Duration.millis(speed), l2);
		ParallelTransition pl = new ParallelTransition();
		t1.setByX(xLength * 30);
		t2.setByX((0 - xLength) * 30);
		pl.getChildren().addAll(t1, t2);
		Collections.swap(list, list.indexOf(l1), list.indexOf(l2));
		return pl;
	}

	private ParallelTransition swapSelect(StackPane l1, StackPane l2, ArrayList<StackPane> list, double speed) {
		int num = 1;
		StackPane fSp;
		TranslateTransition t1 = new TranslateTransition(Duration.millis(speed), l1);
		TranslateTransition t2 = new TranslateTransition(Duration.millis(speed), l2);
		ParallelTransition pl = new ParallelTransition();

		boolean outerBreak = false;
		for (int i = 0; i < list.size(); i++) {
			if (outerBreak)
				break;
			if (list.get(i) == l1 || list.get(i) == l2) {
				fSp = list.get(i);
				for (int j = list.indexOf(fSp) + 1; j < list.size(); j++) {
					if ((list.get(j) == l1 && list.get(j) != fSp) || (list.get(j) == l2 && list.get(j) != fSp)) {
						outerBreak = true;
						num = j - i;
						break;
					}
				}
			}
		}
		num *= 30;
		t1.setByX(num);
		t2.setByX(-num);
		Collections.swap(list, list.indexOf(l1), list.indexOf(l2));
		pl.getChildren().addAll(t1, t2);
		return pl;
	}

	private SequentialTransition BubbleSort(int arr[], ArrayList<StackPane> list, double duration) {
		SequentialTransition sq = new SequentialTransition();
		int temp;
		for (int i = 0; i < arr.length - 1; i++) {
			for (int j = 1; j < arr.length - i; j++) {
				if (sortOrder == 0) {
					if (arr[j] < arr[j - 1]) {
						temp = arr[j - 1];
						arr[j - 1] = arr[j];
						arr[j] = temp;
						// change color and move
						sq.getChildren().add(
								changeColor(list.get(j - 1), list.get(j), PreferenceController.color, color_change));
						sq.getChildren().add(swap(list.get(j - 1), list.get(j), list, duration));
						sq.getChildren().add(
								changeColor(list.get(j - 1), list.get(j), color_change, PreferenceController.color));
					}
				} else {
					if (arr[j] > arr[j - 1]) {
						temp = arr[j - 1];
						arr[j - 1] = arr[j];
						arr[j] = temp;
						// change color and move
						sq.getChildren().add(
								changeColor(list.get(j - 1), list.get(j), PreferenceController.color, color_change));
						sq.getChildren().add(swap(list.get(j - 1), list.get(j), list, duration));
						sq.getChildren().add(
								changeColor(list.get(j - 1), list.get(j), color_change, PreferenceController.color));
					}
				}
			}
		}
		return sq;
	}

	private SequentialTransition InsertionSort(int[] arr, ArrayList<StackPane> list, double duration) {
		SequentialTransition sq = new SequentialTransition();
		int temp;
		for (int i = 1; i < arr.length; i++) {
			for (int j = i; j > 0; j--) {
				if (sortOrder == 0) {
					if (arr[j] < arr[j - 1]) {
						temp = arr[j];
						arr[j] = arr[j - 1];
						arr[j - 1] = temp;
						sq.getChildren().add(
								changeColor(list.get(j - 1), list.get(j), PreferenceController.color, color_change));
						sq.getChildren().add(swap(list.get(j - 1), list.get(j), list, duration));
						sq.getChildren().add(
								changeColor(list.get(j - 1), list.get(j), color_change, PreferenceController.color));
					} else {
						break;
					}
				} else {
					if (arr[j] > arr[j - 1]) {
						temp = arr[j];
						arr[j] = arr[j - 1];
						arr[j - 1] = temp;
						sq.getChildren().add(
								changeColor(list.get(j - 1), list.get(j), PreferenceController.color, color_change));
						sq.getChildren().add(swap(list.get(j - 1), list.get(j), list, duration));
						sq.getChildren().add(
								changeColor(list.get(j - 1), list.get(j), color_change, PreferenceController.color));
					} else {
						break;
					}
				}
			}
		}
		return sq;
	}

	private SequentialTransition SelectionSort(int arr[], ArrayList<StackPane> list, double duration) {
		SequentialTransition sq = new SequentialTransition();
		int i, j, minIndex, tmp;
		int n = arr.length;
		for (i = 0; i < n - 1; i++) {
			if (sortOrder == 0) {
				minIndex = i;
				for (j = i + 1; j < n; j++)
					if (arr[j] < arr[minIndex])
						minIndex = j;
				if (minIndex != i) {
					tmp = arr[i];
					arr[i] = arr[minIndex];
					arr[minIndex] = tmp;
					sq.getChildren().add(
							changeColor(list.get(i), list.get(minIndex), PreferenceController.color, color_change));
					sq.getChildren().add(swapSelect(list.get(i), list.get(minIndex), list, duration));
					sq.getChildren().add(
							changeColor(list.get(i), list.get(minIndex), color_change, PreferenceController.color));
				}
			} else {
				minIndex = i;
				for (j = i + 1; j < n; j++)
					if (arr[j] > arr[minIndex])
						minIndex = j;
				if (minIndex != i) {
					tmp = arr[i];
					arr[i] = arr[minIndex];
					arr[minIndex] = tmp;
					sq.getChildren().add(
							changeColor(list.get(i), list.get(minIndex), PreferenceController.color, color_change));
					sq.getChildren().add(swapSelect(list.get(i), list.get(minIndex), list, duration));
					sq.getChildren().add(
							changeColor(list.get(i), list.get(minIndex), color_change, PreferenceController.color));
				}
			}
		}
		return sq;
	}

	private ParallelTransition swapHeap1(StackPane l1, StackPane l2, ArrayList<StackPane> list, double speed,
			int parent, int child) {
		TranslateTransition t1 = new TranslateTransition();
		TranslateTransition t2 = new TranslateTransition();
		t1.setDuration(Duration.millis(speed));
		t2.setDuration(Duration.millis(speed));
		ParallelTransition pl = new ParallelTransition();
		t1.setNode(l1);
		t2.setNode(l2);
		int num = child - parent;
		num *= 30;
		t1.setByX(num);
		t2.setByX(-num);
		pl.getChildren().addAll(t1, t2);
		Collections.swap(list, list.indexOf(l1), list.indexOf(l2));
		return pl;
	}

	private ParallelTransition swapHeap2(StackPane l1, StackPane l2, ArrayList<StackPane> list, double speed,
			int parent, int child) {
		TranslateTransition t1 = new TranslateTransition();
		TranslateTransition t2 = new TranslateTransition();
		t1.setDuration(Duration.millis(speed));
		t2.setDuration(Duration.millis(speed));
		ParallelTransition pl = new ParallelTransition();
		t1.setNode(l1);
		t2.setNode(l2);
		int num = child - parent;
		num *= 30;
		t1.setByX(num);
		t1.setToY(0);
		t2.setByX(-num);
		t2.setToY(0);
		pl.getChildren().addAll(t1, t2);
		Collections.swap(list, list.indexOf(l1), list.indexOf(l2));
		return pl;
	}

	private ParallelTransition swapHeap3(StackPane l1, StackPane l2, double speed) {
		TranslateTransition t1 = new TranslateTransition();
		TranslateTransition t2 = new TranslateTransition();
		t1.setDuration(Duration.millis(speed));
		t2.setDuration(Duration.millis(speed));
		ParallelTransition pl = new ParallelTransition();
		t1.setNode(l1);
		t2.setNode(l2);
		t1.setToY(-100);
		t2.setToY(-100);
		pl.getChildren().addAll(t1, t2);
		return pl;
	}

	private SequentialTransition HeapSort(int arr[], ArrayList<StackPane> list, double duration) {
		SequentialTransition sq = new SequentialTransition();
		for (int i = arr.length / 2; i >= 0; i--) {
			int parent = i;
			int temp = arr[parent];
			int child = 2 * parent + 1;
			while (child < arr.length) {
				if (child + 1 < arr.length && arr[child] < arr[child + 1]) {
					child++;
				}
				if (temp >= arr[child])
					break;
				temp = arr[child];
				arr[child] = arr[parent];
				arr[parent] = temp;
				temp = arr[child];
				sq.getChildren()
						.add(changeColor(list.get(parent), list.get(child), PreferenceController.color, color_change));
				sq.getChildren().add(swapHeap1(list.get(parent), list.get(child), list, duration, parent, child));
				sq.getChildren()
						.add(changeColor(list.get(parent), list.get(child), color_change, PreferenceController.color));
				parent = child;
				child = 2 * child + 1;
			}
		}
		for (int i = arr.length - 1; i > 0; i--) {
			int temp = arr[i];
			arr[i] = arr[0];
			arr[0] = temp;
			sq.getChildren().add(changeColor(list.get(0), list.get(i), PreferenceController.color, color_change));
			sq.getChildren().add(swapHeap3(list.get(0), list.get(i), duration));
			sq.getChildren().add(swapHeap2(list.get(0), list.get(i), list, duration, 0, i));
			sq.getChildren().add(changeColor(list.get(0), list.get(i), color_change, PreferenceController.color));
			int parent = 0;
			int tempFather = arr[parent];
			int child = 2 * parent + 1;
			while (child < i) {
				if (child + 1 < i && arr[child] < arr[child + 1]) {
					child++;
				}
				if (tempFather >= arr[child])
					break;
				tempFather = arr[child];
				arr[child] = arr[parent];
				arr[parent] = tempFather;
				tempFather = arr[child];
				sq.getChildren()
						.add(changeColor(list.get(parent), list.get(child), PreferenceController.color, color_change));
				sq.getChildren().add(swapHeap1(list.get(parent), list.get(child), list, duration, parent, child));
				sq.getChildren()
						.add(changeColor(list.get(parent), list.get(child), color_change, PreferenceController.color));
				parent = child;
				child = 2 * child + 1;
			}
		}
		return sq;
	}

	private ArrayList<Animation> quickSortRec(int arr[], int start, int end, ArrayList<Animation> animationList,
			double duration) {
		if (start >= end)
			return animationList;
		int mid = arr[end];
		int left = start, right = end - 1;
		while (left < right) {
			while (arr[left] <= mid && left < right)
				left++;
			while (arr[right] >= mid && left < right)
				right--;
			int temp = arr[left];
			arr[left] = arr[right];
			arr[right] = temp;
			// System.out.println("SWAP " + list.get(left) + " AND " +
			// list.get(right));
			animationList.add(changeColor(list.get(left), list.get(right), PreferenceController.color, color_change));
			animationList.add(swap(list.get(left), list.get(right), left - right, list, duration));
			animationList.add(changeColor(list.get(left), list.get(right), color_change, PreferenceController.color));
		}
		if (arr[left] >= arr[end]) {
			int temp = arr[left];
			arr[left] = arr[end];
			arr[end] = temp;
			// System.out.println("SWAP " + list.get(left) + " AND " +
			// list.get(end));
			animationList.add(changeColor(list.get(left), list.get(end), PreferenceController.color, color_change));
			animationList.add(swap(list.get(left), list.get(end), left - end, list, duration));
			animationList.add(changeColor(list.get(left), list.get(end), color_change, PreferenceController.color));
		} else {
			left++;
		}
		quickSortRec(arr, start, left - 1, animationList, duration);
		quickSortRec(arr, left + 1, end, animationList, duration);
		return animationList;
	}

	private SequentialTransition QuickSort(int arr[], SequentialTransition sq, double duration) {
		ArrayList<Animation> animationList = new ArrayList<>();
		sq.getChildren().addAll(quickSortRec(arr, 0, arr.length - 1, animationList, duration));
		return sq;
	}

	private TranslateTransition moveUp(StackPane l1, double speed) {
		TranslateTransition t1 = new TranslateTransition(Duration.millis(speed), l1);
		t1.setByY(-40);
		return t1;
	}

	private TranslateTransition moveDown(StackPane l1, double speed, int distance) {
		distance *= 30;
		TranslateTransition t1 = new TranslateTransition(Duration.millis(speed), l1);
		t1.setByY(40);
		t1.setByX(distance);
		return t1;
	}

	private ArrayList<Animation> mergeSortRec(int arr[], int left, int right, ArrayList<Animation> animationList,
			double duration) {

		if (left >= right) {
			return animationList;
		}
		int center = (left + right) / 2;
		mergeSortRec(arr, left, center, animationList, duration);
		mergeSortRec(arr, center + 1, right, animationList, duration);
		animationList = merge(arr, animationList, left, center, right, duration);
		return animationList;
	}

	private ArrayList<Animation> merge(int[] arr, ArrayList<Animation> animationList, int left, int center, int right,
			double duration) {
		int[] tempArray = new int[arr.length];
		ArrayList<StackPane> tempList = new ArrayList<>(list);
		int mid = center + 1;
		int third = left;
		int temp = left;

		for (int i = left; i <= right; i++) {
			animationList.add(moveUp(list.get(i), 250));
		}

		while (left <= center && mid <= right) {
			if (arr[left] <= arr[mid]) {
				animationList.add(moveDown(list.get(left), duration, third - left));
				tempList.set(third, list.get(left));
				tempArray[third++] = arr[left++];
			} else {
				animationList.add(moveDown(list.get(mid), duration, third - mid));
				tempList.set(third, list.get(mid));
				tempArray[third++] = arr[mid++];
			}
		}

		while (mid <= right) {
			animationList.add(moveDown(list.get(mid), duration, third - mid));
			tempList.set(third, list.get(mid));
			tempArray[third++] = arr[mid++];
		}
		while (left <= center) {
			animationList.add(moveDown(list.get(left), duration, third - left));
			tempList.set(third, list.get(left));
			tempArray[third++] = arr[left++];
		}

		while (temp <= right) {
			list.set(temp, tempList.get(temp));
			arr[temp] = tempArray[temp++];
		}

		return animationList;
	}

	private SequentialTransition MergeSort(int arr[], SequentialTransition sq, double duration) {
		ArrayList<Animation> animationList = new ArrayList<>();
		animationList = mergeSortRec(arr, 0, arr.length - 1, animationList, duration);
		sq.getChildren().addAll(animationList);
		return sq;
	}
	//
	// private TranslateTransition move(StackPane sp, int X) {
	// TranslateTransition t = new TranslateTransition();
	// t.setNode(sp);
	// t.setDuration(SPEED);
	// t.setToX(X);
	// t.setToY(SORT_GROUP_MOVE_DELTA);
	// return t;
	//
	// }
	//
	// private SequentialTransition MergeSort(int arr[], ArrayList<StackPane>
	// list, SequentialTransition sq) {
	// int number = arr.length;
	// this.helper = new int[number];
	// this.helperNodes = new StackPane[number];
	// sortRange(0, number - 1, arr, sq, list);
	// return sq;
	// }
	//
	// private void sortRange(int low, int high, int arr[], SequentialTransition
	// sq, ArrayList<StackPane> list) {
	// // check if low is smaller then high, if not then the array is sorted
	// if (low < high) {
	// // Get the index of the element which is in the middle
	// int middle = low + (high - low) / 2;
	// // Sort the left side of the array
	// sortRange(low, middle, arr, sq, list);
	// // Sort the right side of the array
	// sortRange(middle + 1, high, arr, sq, list);
	// // Combine them both
	// merge(low, middle, high, arr, list, sq);
	// }
	// }
	//
	// private void merge(int low, int middle, int high, int arr[],
	// ArrayList<StackPane> list, SequentialTransition sq) {
	// // Copy both parts into the helper array
	// for (int i = low; i <= high; i++) {
	// helper[i] = arr[i];
	// helperNodes[i] = list.get(i);
	// }
	//
	// int i = low;
	// int j = middle + 1;
	// int k = low;
	// // Copy the smallest values from either the left or the right side back
	// // to the original array
	//
	// while (i <= middle && j <= high) {
	// if (helper[i] <= helper[j]) {
	// arr[k] = helper[i];
	// list.set(k, helperNodes[i]);
	// sq.getChildren().add(move(helperNodes[i], k * SPACING));
	// i++;
	// } else {
	// arr[k] = helper[j];
	// list.set(k, helperNodes[j]);
	// sq.getChildren().add(move(helperNodes[j], k * SPACING));
	// j++;
	// }
	// k++;
	// }
	// // Copy the rest of the left side of the array into the target array
	// while (i <= middle) {
	// arr[k] = helper[i];
	// list.set(k, helperNodes[i]);
	// sq.getChildren().add(move(helperNodes[i], k * SPACING));
	// k++;
	// i++;
	// }
	//
	// // Even if we didn't move in the array because it was already ordered,
	// // move on screen for any remaining nodes in the target array.
	// while (j <= high) {
	// sq.getChildren().add(move(helperNodes[j], k * SPACING));
	// k++;
	// j++;
	// }
	//
	// ParallelTransition moveUp = new ParallelTransition();
	//
	// for (int z = low; z <= high; z++) {
	// TranslateTransition moveNodeUp = new TranslateTransition();
	// moveNodeUp.setNode(helperNodes[z]);
	// moveNodeUp.setDuration(SPEED);
	// moveNodeUp.setByY(-SORT_GROUP_MOVE_DELTA);
	// moveUp.getChildren().add(moveNodeUp);
	// }
	//
	// sq.getChildren().add(moveUp);
	// }

	/** Compare Part **/
	public void chooseFirstAlgo() {
		compareAlgo1 = combo1.getSelectionModel().getSelectedItem().getAccessibleText();
		label_left.setText(combo1.getSelectionModel().getSelectedItem().getText());
		firstAlgoComplex.setText(combo1.getSelectionModel().getSelectedItem().getText());
	}

	public void chooseSecAlgo() {
		compareAlgo2 = combo2.getSelectionModel().getSelectedItem().getAccessibleText();
		label_right.setText(combo2.getSelectionModel().getSelectedItem().getText());
		secondAlgoComplex.setText(combo2.getSelectionModel().getSelectedItem().getText());
	}

	@FXML
	public void startCompare() {
		clear_c();

		if (compareAlgo1 == null || compareAlgo2 == null) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Warning Dialog");
			alert.setHeaderText(null);
			alert.setContentText("Please choose the algorithms to compare!");
			alert.showAndWait();
		} else {
			if (!inputString_c.getText().isEmpty()) {
				input_c = getInput();
				if (input_c == null) {
					Alert alert = new Alert(AlertType.WARNING);
					alert.setTitle("Warning Dialog");
					alert.setHeaderText(null);
					alert.setContentText("Please input with a right format!");
					alert.showAndWait();
				} else {
					generate_c(list_l, hbox_left, input_c);
					generate_c(list_r, hbox_right, input_c);

					sq_c_1 = sort_c(compareAlgo1, list_l, duration_c, input_c, hbox_left.getWidth());
					sq_c_1.play();
					showTime(sq_c_1, duration_c, time_left);

					sq_c_2 = sort_c(compareAlgo2, list_r, duration_c, input_c, hbox_right.getWidth());
					sq_c_2.play();
					showTime(sq_c_2, duration_c, time_right);
				}
			} else {
				Alert alert = new Alert(AlertType.WARNING);
				alert.setTitle("Warning Dialog");
				alert.setHeaderText(null);
				alert.setContentText("Please input first!");
				alert.showAndWait();
			}
		}
	}

	private void showTime(SequentialTransition sq, double duration, Label label) {
		try {
			if (sq.getChildren().size() != 0) {
				label.setText("time: " + String.valueOf(sq.getChildren().size() * duration / 1000 + "s"));
			} else {
				label.setText("time: 0");
			}
		} catch (NullPointerException e) {
			System.out.println(" -- Exception --");
		}
	}

	private SequentialTransition sort_c(String compareAlgo, ArrayList<StackPane> list, double duration, int[] input,
			double dist) {
		SequentialTransition sq_c = new SequentialTransition();

		double swap_dist = dist / input.length;
		switch (compareAlgo) {
		case "bubble":
			sq_c = BubbleSort_c(input, list, duration, swap_dist);
			break;
		case "insert":
			sq_c = InsertionSort_c(input, list, duration, swap_dist);
			break;
		case "select":
			sq_c = SelectionSort_c(input, list, duration, swap_dist);
			break;
		case "quick":
			sq_c = QuickSort_c(input, list, duration, swap_dist);
			break;
		// case "merge":
		// sq_c = MergeSort(input, list, sq, duration);
		// //intializeMergeRec();
		// //sq = MergeSort(input, mergelist, sq);
		// break;
		case "heap":
			sq_c = HeapSort_c(input, list, duration, swap_dist);
			break;
		default:
			break;
		}
		return sq_c;
	}

	private void clear_c() {
		list_l.clear();
		list_r.clear();

		if (!hbox_left.getChildren().isEmpty())
			hbox_left.getChildren().clear();
		if (!hbox_right.getChildren().isEmpty())
			hbox_right.getChildren().clear();
	}

	private int[] getInput() {
		int[] input_c = null;
		int min, max, randomNum;

		String str = inputString_c.getText();

		int[] in = new int[2];
		String[] sp = str.split("\\D+");
		in = Stream.of(sp).mapToInt(Integer::parseInt).toArray();
		if (in.length == 2) {
			min = in[0];
			max = in[1];
			input_c = new int[max - min + 1];

			for (int i = 0; i < input_c.length; i++) {
				randomNum = ThreadLocalRandom.current().nextInt(min, max + 1);
				input_c[i] = randomNum;
			}
		}

		return input_c;
	}

	private void generate_c(ArrayList<StackPane> list, HBox hbox, int[] input) {
		Color shapeColor = PreferenceController.color;
		double width, height, space;

		int max = input[0];
		for (int i : input) {
			if (i >= max)
				max = i;
		}

		width = (hbox.getWidth() / input.length) / 3 * 2;

		for (int anInput : input) {
			height = (hbox.getWidth() / max) * anInput;
			Rectangle rectangle = new Rectangle(width, height);
			rectangle.setFill(shapeColor);
			StackPane stackPane = new StackPane();
			stackPane.setPrefSize(rectangle.getWidth(), rectangle.getHeight());
			stackPane.setId(String.valueOf(anInput));
			stackPane.getChildren().add(rectangle);
			stackPane.setAlignment(Pos.BOTTOM_CENTER);
			list.add(stackPane);
		}

		space = width / 2;
		hbox.setSpacing(space);
		hbox.getChildren().addAll(list);
	}

	/**
	 * Gets rectangle.
	 *
	 * @return the rectangle
	 */
	public static ArrayList<Rectangle> getRectangle() {
		return recList;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		toggleList.add(bubble);
		toggleList.add(insertion);
		toggleList.add(selection);
		toggleList.add(quick);
		toggleList.add(merge);
		toggleList.add(heap);
	}
}
