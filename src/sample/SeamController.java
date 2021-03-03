package sample;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import java.awt.image.BufferedImage;
import java.io.IOException;


public class SeamController extends StackPane {

    @FXML
    protected Slider sliderSeamCarvingWidth;
    @FXML
    protected Slider sliderSeamCarvingHeight;
    @FXML
    protected Label energieTotale;
    @FXML
    protected RadioButton norme1;
    @FXML
    protected RadioButton norme2;


    protected Controller controller;
    protected ToggleGroup radioGroup;


    /**
     * Constructeur par défaut de la classe
     * Chargement du fxml de SeamCarving de l'image et récupération du controller général
     */
    public SeamController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("seamCarving.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        this.controller = Controller.getController();

        this.radioGroup = new ToggleGroup();
        norme1.setToggleGroup(radioGroup);
        norme2.setToggleGroup(radioGroup);

        initialize();
    }

    /**
     * Listeners qui actualise les valeurs des sliders de Seam Carving
     */
    public void initialize() {
        sliderSeamCarvingWidth.valueProperty().addListener((observable, oldValue, newValue) -> {
            sliderSeamCarvingWidth.setValue((double) newValue);
        });

        sliderSeamCarvingHeight.valueProperty().addListener((observable, oldValue, newValue) -> {
            sliderSeamCarvingHeight.setValue((double) newValue);
        });

    }

    /**
     * Redimensionne l'image affichée aux dimensions souhaitées après appuie sur Traitement de l'Image
     * @throws IllegalArgumentException si une des dimensions est à zero
     */
    @FXML
    private void treatSeamCarving() {
        double energieCarving = 0;
        BufferedImage inputImage = controller.imageChainee.getImage();

        int nouvelleLargeur = (int) sliderSeamCarvingWidth.getValue();
        int nouvelleHauteur = (int) sliderSeamCarvingHeight.getValue();

        if (nouvelleLargeur == 0 || nouvelleHauteur == 0){
            throw new IllegalArgumentException();
        }
        int largeur = inputImage.getWidth();
        int hauteur = inputImage.getHeight();

        int norme = 1;
        if (norme2.isSelected()){
            norme = 2;
        }

        // On commence par ajouter des seams si il le faut
        // Agrandissement de l'image (en largeur puis hauteur arbitrairement)

        SeamAdder seamAdder = new SeamAdder(inputImage, norme);
        if (nouvelleLargeur > largeur){
            seamAdder.addSeamsV(nouvelleLargeur);
        }

        if (nouvelleHauteur > hauteur){
            seamAdder.addSeamsH(nouvelleHauteur);
        }

        inputImage = seamAdder.getImageSortie();

        // On retire des seams si il le faut maintenant

        SeamCarver seamCarver = new SeamCarver(inputImage, norme);
        energieCarving = seamCarver.carveImage(nouvelleLargeur, nouvelleHauteur);
        inputImage = seamCarver.getImageSortie();
        try {
            seamCarver.sauvegarderImageCoutures();
        } catch (IOException e) {
            e.printStackTrace();
        }


        ImageChainee imageNext = new ImageChainee(inputImage);
        controller.imageChainee.setNext(imageNext);
        controller.imageChainee = imageNext;
        controller.imageView.setImage(SwingFXUtils.toFXImage(controller.imageChainee.getImage(), null));

        energieTotale.setText("Energie utilisée pour réduire la taille de l'image = " + (int) energieCarving);

        // Actualise l'affichage de l'image
        controller.drawImage.setHeight(controller.imageChainee.getImage().getHeight());
        controller.drawImage.setWidth(controller.imageChainee.getImage().getWidth());
        controller.graphicsContext.drawImage(controller.imageView.getImage(), 0, 0, controller.drawImage.getWidth(), controller.drawImage.getHeight());
        this.controller.reinitialiserSliders();

    }
}