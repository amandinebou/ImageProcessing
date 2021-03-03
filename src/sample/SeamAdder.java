package sample;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class SeamAdder {

    private BufferedImage imageSortie;
    private final int norme;

    /**
     * Constructeur par défaut de la méthode
     * @param imageEntree image à redimensionner
     * @param norme 1 ou 2 pour definir quelle norme va être utilisée pour calculer l'énergie
     * @throws IllegalArgumentException si l'image est nulle ou la norme n'est pas de 1 ou 2
     */
    public SeamAdder(BufferedImage imageEntree, int norme)
    {
        if (imageEntree == null || norme != 1 && norme != 2 ){
            throw new IllegalArgumentException();
        }
        this.imageSortie = imageEntree;
        this.norme = norme;
    }

    /**
     * Ajoute des coutures Horizontales pour augmenter la taille de l'image
     * @param hauteur la nouvelle Hauteur de l'image
     * @throws IllegalArgumentException si hauteur est plus petite que la hauteur actuelle de l'image
     */
    protected void addSeamsH(int hauteur) {

        int ancienneHauteur = this.imageSortie.getHeight();
        int largeur = this.imageSortie.getWidth();
        int hauteurReduite = ancienneHauteur - (hauteur - ancienneHauteur);

        if (hauteur < ancienneHauteur){
            System.out.println("La hauteur doit être plus grande que celle actuelle");
            throw new IllegalArgumentException();
        }

        // On copie l'image
        BufferedImage imageTemporaire = this.imageSortie;

        // Puis on la seamCarve
        SeamCarver seamCarver = new SeamCarver(imageTemporaire, this.norme);
        seamCarver.carveImage(largeur, hauteurReduite);

        // On note les coutures enlevées dans l'ordre
        ArrayList<Couture> coutures = seamCarver.getCoutures();

        // Tableau de 0 et de 1
        // Les pixels de coutures sont marqués par des 1
        // Les autres par des 0
        int[][] localiserCoutures = new int[largeur][ancienneHauteur];

        //On commence par la remplir de 0
        for (int i= 0; i < largeur; i++){
            for (int j = 0; j < hauteurReduite; j++){
                localiserCoutures[i][j] = 0; }
        }

        //On décalle à chaque fois qu'un 1 doit etre marqué pour les avoir au bon endroit
        for (int c = coutures.size()-1 ; c >= 0 ; c--){
            Couture couture = coutures.get(c);
             for (int i = 0; i < largeur ; i++){
                    int jCouture = couture.getPixels()[i];
                    for (int j = hauteurReduite ; j > jCouture ; j--){
                        localiserCoutures[i][j] = localiserCoutures[i][j-1];
                    }
                    localiserCoutures[i][jCouture] = 1;
                }
                hauteurReduite++;
        }


        // Tableau pour stocker les pixels de la nouvelle image
        int[][] imageAugmenteeTable = new int[largeur][hauteur];

        // On la remplie colonne par colonne
        // On ajoute 2 fois le pixel si il y a un 1
        for (int i = 0 ; i < largeur ; i++){
            int jaugmentee = 0;
            for (int j = 0 ; j < ancienneHauteur ; j++){
                if (localiserCoutures[i][j] == 1){
                    imageAugmenteeTable[i][jaugmentee] = this.imageSortie.getRGB(i, j);
                    imageAugmenteeTable[i][++jaugmentee] = this.imageSortie.getRGB(i, j);
                }
                else{
                    imageAugmenteeTable[i][jaugmentee] = this.imageSortie.getRGB(i, j);
                }
                jaugmentee++;
            }
        }

        //On remplie la bufferedImage avec le tableau précédant
        BufferedImage imageAugmentee = new BufferedImage(largeur, hauteur, BufferedImage.TYPE_INT_RGB);

        for(int i = 0; i < largeur; i++) {
            for(int j = 0; j < hauteur; j++) {
                imageAugmentee.setRGB(i,j,imageAugmenteeTable[i][j]);
            }
        }
        this.imageSortie = imageAugmentee;
    }

    /**
     * Ajoute des coutures Verticales pour augmenter la taille de l'image
     * @param largeur la nouvelle Largeur de l'image
     * @throws IllegalArgumentException si largeur est plus petite que la largeur actuelle de l'image
     */
    protected void addSeamsV(int largeur) {

        int hauteur = this.imageSortie.getHeight();
        int ancienneLargeur = this.imageSortie.getWidth();
        int largeurReduite = ancienneLargeur - (largeur - ancienneLargeur);

        if (largeur < ancienneLargeur){
            System.out.println("La largeur doit être plus grande que celle actuelle");
            throw new IllegalArgumentException();
        }

        // On copie l'image
        BufferedImage imageTemporaire = this.imageSortie;

        // Puis on la seamCarve
        SeamCarver seamCarver = new SeamCarver(imageTemporaire, this.norme);
        seamCarver.carveImage(largeurReduite, hauteur );

        // On note les coutures enlevées dans l'ordre
        ArrayList<Couture> coutures = seamCarver.getCoutures();

        // Tableau de 0 et de 1
        // Les pixels de coutures sont marqués par des 1
        // Les autres par des 0
        int[][] localiserCoutures = new int[ancienneLargeur][hauteur];

        //On commence par la remplir de 0
        for (int i= 0; i < largeurReduite; i++){
            for (int j = 0; j < hauteur; j++){
                localiserCoutures[i][j] = 0; }
        }

        //On décalle à chaque fois qu'un 1 doit etre marqué pour les avoir au bon endroit
        for (int c = coutures.size()-1 ; c >= 0 ; c--){
            Couture couture = coutures.get(c);
             for (int j = 0; j < hauteur ; j++){
                    int iCouture = couture.getPixels()[j];
                    for (int i = largeurReduite ; i > iCouture ; i--){
                        localiserCoutures[i][j] = localiserCoutures[i-1][j];
                    }
                    localiserCoutures[iCouture][j] = 1;
                }
                largeurReduite++;
        }

        // Tableau pour stocker les pixels de la nouvelle image
        int[][] imageAugmenteeTable = new int[largeur][hauteur];

        // On la remplie ligne par ligne
        // On ajoute 2 fois le pixel si il y a un 1
        for (int j = 0 ; j < hauteur; j++){
            int iaugmentee = 0;
            for (int i = 0; i < ancienneLargeur; i++){
                if (localiserCoutures[i][j] == 1){
                    imageAugmenteeTable[iaugmentee][j] = this.imageSortie.getRGB(i, j);
                    imageAugmenteeTable[++iaugmentee][j] = this.imageSortie.getRGB(i, j);
                }
                else{
                    imageAugmenteeTable[iaugmentee][j] = this.imageSortie.getRGB(i, j);
                }
                iaugmentee++;
            }
        }

        //On remplie la bufferedImage avec le tableau précédant
        BufferedImage imageAugmentee = new BufferedImage(largeur, hauteur, BufferedImage.TYPE_INT_RGB);

        for(int i = 0; i < largeur; i++) {
            for(int j = 0; j < hauteur; j++) {
                imageAugmentee.setRGB(i,j,imageAugmenteeTable[i][j]);
            }
        }
        this.imageSortie = imageAugmentee;
    }

    /**
     * Méthode d'accès à l'imageSortie redimensionnée
     * @return BufferedImage qui stocke l'image redimensionnée
     */
    protected BufferedImage getImageSortie(){
        return this.imageSortie;
    }
}
