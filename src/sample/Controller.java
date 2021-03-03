package sample;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;


public class Controller {
    @FXML
    protected ImageView imageView;
    @FXML
    private ResizeController resizeController;
    @FXML
    private RescaleController rescaleController;
    @FXML
    private SeamController seamController;
    @FXML
    private DrawController drawController;
    @FXML
    protected Canvas drawImage;
    GraphicsContext graphicsContext;

    protected ImageChainee imageChainee;
    protected static Controller controller;


    /**
     * Constructeur qui vérifie la création unique du controller
     * @throws RuntimeException si il y a déjà un controller
     */
    public Controller() {
        if (controller == null) {
            controller = this;
        } else {
            throw new RuntimeException("Singleton FXML");
        }
        imageChainee = new ImageChainee(null);
    }

    /**
     * Méthode d'accès au controller, pour les autres controllers
     */
    protected static Controller getController() {
        return controller;
    }

    public void initialize() {
    }

    /**
     * Charge l'image choisie dans la fenêtre de dialogue
     *
     * @param actionEvent Lancé si bouton du Menu "Open file" est appuyé
     * @throws IOException si le fichier choisi n'est pas une image
     */
    @FXML
    private void loadImage(ActionEvent actionEvent) throws IOException {
        this.drawController.removeDrawing();

        this.imageView.setImage(null);
        if (this.imageChainee.getImage() != null) {
            this.graphicsContext.clearRect(0, 0, this.drawImage.getWidth(), this.drawImage.getHeight());
        }

        FileChooser filechooser = new FileChooser();
        filechooser.setTitle("Open File");
        File file = filechooser.showOpenDialog(this.imageView.getScene().getWindow());

        if (file != null) {
            try {
                this.imageChainee = new ImageChainee(ImageIO.read(file));
                assert(this.imageChainee.getImage() != null);
            } catch (NullPointerException nullPointerException) {
                throw new NullPointerException();
            }
            Image image = SwingFXUtils.toFXImage(this.imageChainee.getImage(), null);
            this.imageView.setImage(image);

            // N'affiche pas les differents traitements possibles sur la premiere page d'affichage
            this.seamController.setVisible(false);
            this.rescaleController.setVisible(false);
            this.resizeController.setVisible(false);
            this.drawController.setVisible(false);


            // Initialisation du canva qui servira pour l'interface de dessin
            this.drawImage.setHeight(imageChainee.getImage().getHeight());
            this.drawImage.setWidth(imageChainee.getImage().getWidth());

            this.graphicsContext = this.drawImage.getGraphicsContext2D();
        }
    }

    /**
     * Sauvegarde l'image en png en ouvrant une fenêtre de dialogue pour choisir la localisation de l'enregistrement
     */
    @FXML
    private void saveImage() {
        FileChooser filechooser = new FileChooser();
        filechooser.setTitle("Save File");
        File file = filechooser.showSaveDialog(imageView.getScene().getWindow());
        if (file != null) {
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(imageView.getImage(), null), "png", file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Reinitialise les valeurs de sliders en fonction de l'image actuellement affichée
     */
    protected void reinitialiserSliders(){
        int hauteur = this.imageChainee.getImage().getHeight();
        int largeur = this.imageChainee.getImage().getWidth();

        // Sliders Rescale
        this.rescaleController.sliderRescaleHeight.setMax(hauteur);
        this.rescaleController.sliderRescaleWidth.setMax(largeur);
        this.rescaleController.sliderRescaleHeight.setValue(hauteur);
        this.rescaleController.sliderRescaleWidth.setValue(largeur);

        // Sliders Resize
        this.resizeController.sliderResizeRight.setMax(largeur);
        this.resizeController.sliderResizeLeft.setMax(largeur);
        this.resizeController.sliderResizeBottom.setMax(hauteur);
        this.resizeController.sliderResizeTop.setMax(hauteur);
        this.resizeController.sliderResizeLeft.setValue(0);
        this.resizeController.sliderResizeTop.setValue(0);
        this.resizeController.sliderResizeRight.setValue(largeur);
        this.resizeController.sliderResizeBottom.setValue(hauteur);

        // Sliders SeamCarving

        this.seamController.sliderSeamCarvingHeight.setMax(hauteur*2);
        this.seamController.sliderSeamCarvingWidth.setMax(largeur*2);
        this.seamController.sliderSeamCarvingHeight.setValue(hauteur);
        this.seamController.sliderSeamCarvingWidth.setValue(largeur);

    }

    /**
     * Méthode pour afficher les options de traitement d'image de recadrage et initialiser les sliders
     * @param mouseEvent l'appui bouton menu Resize
     */
    @FXML
    private void resizeImage(MouseEvent mouseEvent) {
        if (imageChainee.getImage() != null) {
            resizeController.toFront();
            rescaleController.setVisible(false);
            seamController.setVisible(false);
            resizeController.setVisible(true);
            drawController.setVisible(false);

            // Eteint le listener qui permet de dessiner pour ne pas dessiner si option non choisie
            drawController.removeDrawing();

            this.reinitialiserSliders();

        }
    }

    /**
     * Méthode pour afficher les options de traitement d'image de mise à l'échelle et initialiser les sliders
     * @param mouseEvent l'appui bouton menu Rescale
     */
    @FXML
    private void rescaleImage(MouseEvent mouseEvent) {
        if (imageChainee.getImage() != null) {
            rescaleController.toFront();

            resizeController.setVisible(false);
            seamController.setVisible(false);
            rescaleController.setVisible(true);
            drawController.setVisible(false);

            drawImage.toFront();

            // Eteint le listener qui permet de dessiner pour ne pas dessiner si option non choisie
            drawController.removeDrawing();

            this.reinitialiserSliders();

        }
    }

    /**
     * Méthode pour afficher les options de traitement d'image de SeamCarving et initialiser les sliders
     * @param mouseEvent l'appui bouton menu Seam Carving
     */
    @FXML
    private void seamImage(MouseEvent mouseEvent) {
        if(imageChainee.getImage() != null) {
            seamController.toFront();

            resizeController.setVisible(false);
            rescaleController.setVisible(false);
            seamController.setVisible(true);
            drawController.setVisible(false);

            drawImage.toFront();

            // Eteint le listener qui permet de dessiner pour ne pas dessiner si option non choisie
            drawController.removeDrawing();

            this.reinitialiserSliders();

        }
    }

    /**
     * Méthode pour afficher l'interface de dessin
     * @param mouseEvent l'appui bouton menu Dessiner
     *
     */
    @FXML
    private void drawImage(MouseEvent mouseEvent) {
        if (imageChainee.getImage() != null) {

            int hauteur = imageChainee.getImage().getHeight();
            int largeur = imageChainee.getImage().getWidth();

            drawImage.setHeight(hauteur);
            drawImage.setWidth(largeur);
            graphicsContext.drawImage(imageView.getImage(), 0, 0, controller.drawImage.getWidth(), controller.drawImage.getHeight());

            drawImage.toFront();
            resizeController.setVisible(false);
            rescaleController.setVisible(false);
            seamController.setVisible(false);
            drawController.setVisible(true);
            drawController.drawOnImage();
            imageView.setImage(SwingFXUtils.toFXImage(this.imageChainee.getImage(), null));
        }
    }

    /**
     * Méthode qui annule la derniere modification de l'image
     * @param mouseEvent l'appui bouton menu Undo
     */
    @FXML
    private void treatUndo(MouseEvent mouseEvent) {
        if (imageChainee.isTherePrev()){
            imageChainee = imageChainee.getPrev();
            imageView.setImage(SwingFXUtils.toFXImage(controller.imageChainee.getImage(), null));

            int hauteur = controller.imageChainee.getImage().getHeight();
            int largeur = controller.imageChainee.getImage().getWidth();

            drawImage.setHeight(hauteur);
            drawImage.setWidth(largeur);
            graphicsContext.drawImage(controller.imageView.getImage(), 0, 0, controller.drawImage.getWidth(), controller.drawImage.getHeight());
            this.reinitialiserSliders();

        }
    }

    /**
     * Méthode qui refait la derniere modification annulée de l'image
     * @param mouseEvent l'appuie bouton menu Redo
     */
    @FXML
    private void treatRedo(MouseEvent mouseEvent) {
        if (imageChainee.isThereNext()) {
            imageChainee = imageChainee.getNext();
            imageView.setImage(SwingFXUtils.toFXImage(controller.imageChainee.getImage(), null));

            int hauteur = controller.imageChainee.getImage().getHeight();
            int largeur = controller.imageChainee.getImage().getWidth();

            drawImage.setHeight(hauteur);
            drawImage.setWidth(largeur);
            graphicsContext.drawImage(controller.imageView.getImage(), 0, 0, controller.drawImage.getWidth(), controller.drawImage.getHeight());
            this.reinitialiserSliders();

        }
    }
}

