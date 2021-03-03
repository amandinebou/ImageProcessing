package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import java.io.IOException;

/** Controller pour choisir la couleur basé sur 3 sliders
 */
public class ColorPickerController extends VBox
{
    @FXML
    private Canvas canvasColorPick; // canvas for preview of the chosen color
    @FXML
    private Slider sliderColorR;
    @FXML
    private Slider sliderColorG;
    @FXML
    private Slider sliderColorB;

    /**
     * Constructeur par défaut du controller
     * @throws RuntimeException si le fxml n'a pas chargé
     */
    public ColorPickerController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ColorPicker.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        initialize();
    }

    /** Méthode pour initialiser le controller
     */
    public void initialize()
    {
        // Actualise la couleur
        updateColorPicker();

        // Ajoute des Listeners pour actualiser la pré-visualisation de la couleur
        sliderColorR.valueProperty().addListener((observable, oldValue, newValue) -> {
            updateColorPicker();
            sliderColorR.setValue((double) newValue);
        });
        sliderColorG.valueProperty().addListener((observable, oldValue, newValue) -> {
            updateColorPicker();
            sliderColorG.setValue((double) newValue);
        });
        sliderColorB.valueProperty().addListener((observable, oldValue, newValue) -> {
            updateColorPicker();
            sliderColorB.setValue((double) newValue);
        });
    }

    /**
     * Méthode qui renvoie une couleur RGB selon les valeurs des sliders actuels
     * @return la couleur RGB sélectionnée
     */
    protected Color getSelectedColor() {
        return Color.rgb((int)sliderColorR.getValue(), (int)sliderColorG.getValue(), (int)sliderColorB.getValue());
    }

    /**
     * Méthode qui actualise la pré-visualisation de la couleur choisie en fonction des sliders
     */
    private void updateColorPicker() {
        GraphicsContext gc = canvasColorPick.getGraphicsContext2D();
        gc.setFill(getSelectedColor());
        gc.fillRect(0,0, canvasColorPick.getWidth(),canvasColorPick.getHeight());
    }
}