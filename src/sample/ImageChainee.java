package sample;

import java.awt.image.BufferedImage;

/**
 * Classe pour stocker une Buffered Image en liste Chainée pour le Undo Redo
 */
public class ImageChainee {

    ImageChainee next;
    ImageChainee prev;
    BufferedImage imageBuff;

    /**
     * Constructeur par défaut de la classe
     * @param imageBuff image à stocker
    */
    public ImageChainee(BufferedImage imageBuff){
        this.imageBuff = imageBuff;
        this.next = null;
        this.prev = null;
    }

    /**
     * Méthode qui indique si l'image a une image Chainée suivante dans la liste
     * @return true si il y a une ImageChainee en next, false si non.
     */
    protected boolean isThereNext(){
        return (this.next != null);
    }

    /**
     * Méthode qui indique si l'image a une image précedante dans la liste
     * @return true si il y a une ImageChainee en prev, false si non.
     */
    protected boolean isTherePrev(){
        return (this.prev != null);
    }

    /**
     * Methode d'accès à la BufferedImage
     * @return la BufferedImage stockée
     */
    protected BufferedImage getImage(){
        return this.imageBuff;
    }

    /**
     * Méthode d'accès à l'image Chainée suivante dans la Liste
     * @return l'Image Chainée suivante
     */
    protected ImageChainee getNext(){
        return next;
    }

    /**
     * Méthode d'accès à l'image Chainée précedante dans la Liste
     * @return l'Image Chainée précédante
     */
    protected ImageChainee getPrev(){
        return prev;
    }

    /**
     * Méthode pour attribuer une image chainée à la place précedante dans la liste
     * @param imageChaineePrev l'imageChainée à mettre en prev
     */
    private void setPrev(ImageChainee imageChaineePrev){
        this.prev = imageChaineePrev;
    }

    /**
     * Méthode pour attribuer une image Chainée à la place suivante dans la liste
     * @param imageNext l'imageChainée à mettre a la suite
     */
    protected void setNext(ImageChainee imageNext){
        this.next = imageNext;
        imageNext.setPrev(this);
    }
}
