package sample;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Slider;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import java.io.IOException;



public class DrawController extends StackPane {
    protected Controller controller;
    @FXML
    private ColorPickerController colorPickerController;
    @FXML
    private Slider epaisseur;
    protected EventHandler listenerPressed;
    protected EventHandler listenerDragged;
    protected EventHandler listenerReleased;


    /**
     * Methode pour charger le controller de l'interface de dessin
     * @throws RuntimeException si le fxml n'a pas chargé
     */
    public DrawController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("draw.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        }
        catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        //récupération du controller général
        this.controller = Controller.getController();
        initialize();
    }

    /**
     * On ajouter un Listener sur l'epaisseur du pinceau choisie
     */
    public void initialize() {
        epaisseur.valueProperty().addListener((observable, oldValue, newValue) -> {
            epaisseur.setValue((double) newValue);
        });
    }

    /**
     * Méthode qui lance les listener qui permettent de dessiner sur l'image
     */
    protected void drawOnImage() {
        this.listenerPressed = new EventHandler<MouseEvent>(){
            /**
             * Méthode qui récupère les coordonnées du clique et commence à dessiner sur le canva
             * @param event quand la souris clique
             */
            @Override
            public void handle(MouseEvent event) {
                controller.graphicsContext.beginPath();
                controller.graphicsContext.moveTo(event.getX(), event.getY());
                controller.graphicsContext.stroke();
            }
        };

        controller.drawImage.addEventHandler(MouseEvent.MOUSE_PRESSED, this.listenerPressed);

        this.listenerDragged = new EventHandler<MouseEvent>(){
            /**
             * Méthode qui actualise les coordonnées de la souris et crée un trait là où la souris passe sur le canva
             * @param event quand la souris est maintenue cliquée
             */
            @Override
            public void handle(MouseEvent event) {
                controller.graphicsContext.lineTo(event.getX(), event.getY());
                controller.graphicsContext.setStroke(colorPickerController.getSelectedColor());
                controller.graphicsContext.setLineWidth(epaisseur.getValue());
                controller.graphicsContext.stroke();
                controller.graphicsContext.closePath();
                controller.graphicsContext.beginPath();
                controller.graphicsContext.moveTo(event.getX(), event.getY());
            }
        };
        controller.drawImage.addEventHandler(MouseEvent.MOUSE_DRAGGED, this.listenerDragged);

        this.listenerReleased = new EventHandler<MouseEvent>(){
            /**
             * Methode qui enregistre le canva dans la bufferedImage pour pouvoir la sauvegarder et réaliser d'autres traitements
             * @param event quand la souris est relachée
             */
            @Override
            public void handle(MouseEvent event) {
                WritableImage wi = new WritableImage((int)controller.graphicsContext.getCanvas().getWidth(), (int)controller.graphicsContext.getCanvas().getHeight());
                controller.graphicsContext.getCanvas().snapshot(null, wi); //Coping all that now in Canvas
                ImageChainee imageNext = new ImageChainee(SwingFXUtils.fromFXImage((Image)wi, null));
                controller.imageChainee.setNext(imageNext);
                controller.imageChainee = imageNext;
                controller.imageView.setImage(SwingFXUtils.toFXImage(controller.imageChainee.getImage(), null));
            }
        };
        controller.drawImage.addEventHandler(MouseEvent.MOUSE_RELEASED, this.listenerReleased);
    }

    /**
     * Methode pour stopper les listener utilisés pour le dessin quand on ne souhaite plus dessiner
     */
    protected void removeDrawing() {
        if (this.listenerPressed != null) {
            controller.drawImage.removeEventHandler(MouseEvent.MOUSE_PRESSED, this.listenerPressed);
        }
        if (this.listenerDragged != null) {
            controller.drawImage.removeEventHandler(MouseEvent.MOUSE_DRAGGED, this.listenerDragged);
        }
        if (this.listenerReleased != null) {
            controller.drawImage.removeEventHandler(MouseEvent.MOUSE_RELEASED, this.listenerReleased);
        }
    }
}