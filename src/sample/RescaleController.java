package sample;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Slider;
import javafx.scene.layout.StackPane;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Controller pour la mise a l'échelle de l'image
 */
public class RescaleController extends StackPane {
    @FXML
    protected Slider sliderRescaleWidth;
    @FXML
    protected Slider sliderRescaleHeight;

    protected Controller controller;


    /**
     * Construgteur par défaut de la classe
     * Chargement du fxml de mise à l'échelle de l'image et récupération du controller général
     */
    public RescaleController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("rescale.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception); }

        this.controller = Controller.getController();
        initialize();

    }

    /**
     * Listeners qui actualise les valeurs des sliders de mise à l'échelle
     */
    public void initialize() {
        this.sliderRescaleWidth.valueProperty().addListener((observable, oldValue, newValue) -> {
            this.sliderRescaleWidth.setValue((double) newValue);
        });
        this.sliderRescaleHeight.valueProperty().addListener((observable, oldValue, newValue) -> {
            this.sliderRescaleHeight.setValue((double) newValue);
        });
    }

    /**
     * Méthode qui met à l'échelle l'image en argument aux dimensions souhaitées
     *
     * @param bufferedImage : bufferedImage originale à traiter
     * @param width : largeur finale souhaitée
     * @param height : hauteur finale souhaitée
     * @return la bufferedImage mise à l'échelle
     * @throws IllegalArgumentException si la hauteur et largeur est inferieure ou égale à 0
     */
    private BufferedImage rescaleImage(BufferedImage bufferedImage, int width, int height) {
        if (width <= 0 || height <= 0 ){
            throw new IllegalArgumentException();
        }
        BufferedImage recentre = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g = recentre.createGraphics();
        g.drawImage(bufferedImage, 0, 0, width, height, null);
        g.dispose();
        return recentre;
    }

    /**
     * Méthode qui met à l'échelle l'image en cours d'affichage après appuie sur Traitement de l'Image
     */
    @FXML
    private void treatRescale() {
        ImageChainee imageNext = new ImageChainee(rescaleImage(controller.imageChainee.getImage(), (int) this.sliderRescaleWidth.getValue(), (int) this.sliderRescaleHeight.getValue()));
        controller.imageChainee.setNext(imageNext);
        controller.imageChainee = imageNext;
        controller.imageView.setImage(SwingFXUtils.toFXImage(controller.imageChainee.getImage(), null));

        int hauteur = controller.imageChainee.getImage().getHeight();
        int largeur = controller.imageChainee.getImage().getWidth();

        // Actualise l'affichage avec l'image pour avoir une visualisation homogène quelque soit la fonction de traitement choisie
        controller.drawImage.setHeight(hauteur);
        controller.drawImage.setWidth(largeur);
        controller.graphicsContext.drawImage(controller.imageView.getImage(), 0, 0, controller.drawImage.getWidth(), controller.drawImage.getHeight());
        this.controller.reinitialiserSliders();


    }
}
