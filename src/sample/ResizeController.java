package sample;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Slider;
import javafx.scene.layout.StackPane;
import java.awt.image.BufferedImage;
import java.io.IOException;


public class ResizeController extends StackPane {
    @FXML
    protected Slider sliderResizeLeft;
    @FXML
    protected Slider sliderResizeRight;
    @FXML
    protected Slider sliderResizeTop;
    @FXML
    protected Slider sliderResizeBottom;

    protected Controller controller;


    /**
     * Construteur par défaut de la classe
     * Chargement du fxml de recadrage de l'image et récupération du controller général
     */
    public ResizeController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("resize.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        this.controller = Controller.getController();
        initialize();

    }

    /**
     * Listeners qui actualise les valeurs des sliders de recadrage
     */
    public void initialize() {
        this.sliderResizeBottom.valueProperty().addListener((observable, oldValue, newValue) -> {
            this.sliderResizeBottom.setValue((double) newValue);
        });

        this.sliderResizeTop.valueProperty().addListener((observable, oldValue, newValue) -> {
            this.sliderResizeTop.setValue((double) newValue);
        });
        this.sliderResizeLeft.valueProperty().addListener((observable, oldValue, newValue) -> {
            this.sliderResizeLeft.setValue((double) newValue);
        });

        this.sliderResizeRight.valueProperty().addListener((observable, oldValue, newValue) -> {
            this.sliderResizeRight.setValue((double) newValue);
        });
    }

    /**
     * recadre une BufferedImage à la taille voulue : rognage sur les côtés
     * copie uniquement les pixels souhaités dans une nouvelle bufferedImage
     *
     * @param bufferedImage : bufferedImage que l'on souhaite recadrer
     * @param leftCrop      : taille à rogner à gauche
     * @param rightCrop     : taille à rogner à droite
     * @param topCrop       : taille à rogner en haut
     * @param bottomCrop    : taille à rogner en bas
     * @return : une bufferedImage recadrée
     * @throws IllegalArgumentException si on reduit une des dimensions à zero
     */
    private BufferedImage recadrageImage(BufferedImage bufferedImage, int leftCrop, int rightCrop, int topCrop, int bottomCrop) {
        int largeur = rightCrop - leftCrop;
        int hauteur = bottomCrop - topCrop;
        if (largeur <= 0 || hauteur <= 0){
            throw new IllegalArgumentException();
        }
        BufferedImage newBuff = new BufferedImage(largeur, hauteur, BufferedImage.TYPE_INT_RGB);
        int pixel;
        for(int i = leftCrop; i < rightCrop; i++)
        {
            for(int j = topCrop; j < bottomCrop; j++)
            {
                pixel = bufferedImage.getRGB(i, j);
                newBuff.setRGB(i - leftCrop, j - topCrop, pixel);
            }
        }
        return newBuff;
    }

    /**
     * Méthode qui recadre l'image en cours d'affichage après appuie sur Traitement de l'Image
     */
    @FXML
    private void treatResize() {

        ImageChainee imageNext = new ImageChainee (recadrageImage(controller.imageChainee.getImage(), (int) this.sliderResizeLeft.getValue(), (int) this.sliderResizeRight.getValue(), (int) this.sliderResizeTop.getValue(), (int) this.sliderResizeBottom.getValue()));
        controller.imageChainee.setNext(imageNext);
        controller.imageChainee = imageNext;
        controller.imageView.setImage(SwingFXUtils.toFXImage(controller.imageChainee.getImage(), null));

        // Actualise la taille max des sliders à la taille de l'image
        int hauteur = controller.imageChainee.getImage().getHeight();
        int largeur = controller.imageChainee.getImage().getWidth();

        // Actualise l'affichage de l'image
        controller.drawImage.setHeight(hauteur);
        controller.drawImage.setWidth(largeur);
        controller.graphicsContext.drawImage(controller.imageView.getImage(), 0, 0, controller.drawImage.getWidth(), controller.drawImage.getHeight());
        this.controller.reinitialiserSliders();

    }
}