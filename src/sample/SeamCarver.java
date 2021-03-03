package sample;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.io.File;
import java.util.ArrayList;

/**
 * Classe qui prend une image et peut réduire sa taille en la seamCarvant
 * L'energie est calculée en norme 1 ou 2 en fonction du parametre envoyé
 */

public class SeamCarver {

    private final BufferedImage imageEntree;
    private BufferedImage imageSortie;
    private final ArrayList<Couture> coutures;
    private final int norme;

    /**
     * Constructeur par defaut de la classe SeamCarver
     * @param imageEntree BufferedImage à redimensionner
     * @param norme entier de valeur 1 ou 2 pour savoir si l'energie est calculée en norme 1 ou 2
     * @throws IllegalArgumentException si l'image est nulle ou la norme n'est pas de 1 ou 2
     */
    public SeamCarver(BufferedImage imageEntree, int norme)
    {
        if (imageEntree == null || norme != 1 && norme != 2 ){
            throw new IllegalArgumentException();
        }

        this.imageEntree = imageEntree;
        this.imageSortie = imageEntree;
        coutures = new ArrayList<>();
        this.norme = norme;
    }

    /**
     * Methode qui reduit la taille de l'image en Seam Carving pour la rendre à la taille souhaitée
     * @param hauteur nouvelle hauteur souhaitée pour l'image
     * @param largeur nouvelle largeur souhaitée pour l'image
     * @return L'energie totale utilisée pour seamcarver.
     * @throws IllegalArgumentException Si la taille souhaitée est plus grande que l'image de base
     */
    protected double carveImage( int largeur, int hauteur)
    {
        int nombreCoutureH = this.imageEntree.getHeight() - hauteur;
        int nombreCoutureV = this.imageEntree.getWidth() - largeur;

        int nombreCoutureTotal = nombreCoutureH + nombreCoutureV;
        double energieTotale = 0;

        if(nombreCoutureH < 0 ||  nombreCoutureV < 0 )
        {
            throw new IllegalArgumentException();
        }

        Couture coutureH;
        Couture coutureV;

        //On regarde si on doit supprimer des coutures Horizontales et Verticales
        while(nombreCoutureH > 0 && nombreCoutureV > 0)
        {
            Progres(nombreCoutureTotal,nombreCoutureH+nombreCoutureV);

            coutureH = minCoutureHorizontale();
            coutureV = minCoutureVerticale();

            //On supprime la couture d'energie minimale entre celle horizontale ou verticale

            if(coutureH.getEnergie() < coutureV.getEnergie()) {
                this.coutures.add(coutureH);
                energieTotale += coutureH.getEnergie();
                supprimerCouture(coutureH);
                nombreCoutureH--;
            }
            else {
                this.coutures.add(coutureV);
                energieTotale += coutureV.getEnergie();
                supprimerCouture(coutureV);
                nombreCoutureV--;
            }
        }

        //On supprime le reste des coutures Horizontales ou Verticales si il en reste

        while(nombreCoutureH > 0) {
            Progres(nombreCoutureTotal,nombreCoutureH+nombreCoutureV);
            coutureH = minCoutureHorizontale();
            this.coutures.add(coutureH);
            energieTotale += coutureH.getEnergie();
            supprimerCouture(coutureH);
            nombreCoutureH--;
        }

        while(nombreCoutureV > 0){
            Progres(nombreCoutureTotal,nombreCoutureH+nombreCoutureV);
            coutureV = minCoutureVerticale();
            this.coutures.add(coutureV);
            energieTotale += coutureV.getEnergie();
            supprimerCouture(coutureV);
            nombreCoutureV--;
        }

        return energieTotale;
    }

    /**
     * calcule la table d'énergie de l'image à l'aide de gradients, en norme 1 ou 2.
     * Les cas de bords sont traités en les mettant au maximum
     * @return la table d'energie des pixels en double[][]
     */
    private double[][] tableEnergie()
    {

        int largeur = this.imageSortie.getWidth();
        int hauteur = this.imageSortie.getHeight();
        double[][] tableEnergie = new double[largeur][hauteur];

        for(int i = 0; i < largeur; i++)
        {
            for(int j = 0; j < hauteur; j++)
            {
                double energieIJ;

                if (i==0 || i == largeur-1 || j == 0 || j == hauteur-1){
                    if (this.norme == 1){
                        energieIJ = 6 * 255;
                    }
                    else {
                        energieIJ = Math.sqrt(6 * 255 * 255);
                    }
                }

                else{
                    int xRGB1 = this.imageSortie.getRGB(i-1, j);
                    int xRGB2 = this.imageSortie.getRGB(i+1, j);


                    int yRGB1 = this.imageSortie.getRGB(i, j-1);
                    int yRGB2 = this.imageSortie.getRGB(i, j+1);

                    energieIJ = gradientEnergie(xRGB1, xRGB2) + gradientEnergie (yRGB1, yRGB2);
                    if (this.norme == 2){
                        energieIJ = Math.sqrt(energieIJ);
                    }

                }
                tableEnergie[i][j] = energieIJ;
            }
        }

        return tableEnergie;
    }

    /**
     * Methode qui renvoie le gradient d'energie entre 2 pixels
     * @param RGB1 entier qui contient les informations RGB du pixel 1
     * @param RGB2 entier qui contient les informations RGB du pixel 2
     * @return un double contenant ce gradient en norme 1 ou 2
     */
    private double gradientEnergie (int RGB1, int RGB2){
        double bx1 = (RGB1) & 0xff;
        double gx1 = (RGB1 >> 8) & 0xff;
        double rx1 = (RGB1 >> 16) & 0xff;
        double bx2 = (RGB2) & 0xff;
        double gx2 = (RGB2 >> 8) & 0xff;
        double rx2 = (RGB2 >> 16) & 0xff;
        if (this.norme == 1){
            return  Math.abs(rx1 - rx2) + Math.abs(gx1 - gx2) + Math.abs(bx1 - bx2);
        }
        else{
            return (rx1-rx2)*(rx1-rx2) + (gx1 - gx2)*(gx1 - gx2) + (bx1 - bx2)*(bx1 - bx2);
        }

    }

    /**
     * Trouve de manière dynamique et en backtracking une couture horizontale d'énergie minimale pour la supprimer
     * @return une couture horizontale d'énergie minimale
     */
    private Couture minCoutureHorizontale()
    {
        int largeur = this.imageSortie.getWidth();
        int hauteur = this.imageSortie.getHeight();

        //On recalcule la table d'Energie de l'Image
        double[][] tableEnergie = tableEnergie();

        // Va permettre de calculer dynamiquement les energies des coutures
        double[][] tableDynamique = new double[largeur][hauteur];

        //Stocke les chemins empruntés pour former les coutures
        int[][] tableChemins = new int[largeur][hauteur];

        for(int i = 0; i < largeur; i++)
        {
            for(int j = 0; j < hauteur; j++)
            {
                double min;

                //La première colonne où les énergies restent les mêmes
                if(i == 0) {
                    min = 0;
                    tableChemins[i][j] = -1;
                }

                //Cas des bords
                else if(j == 0 )
                {
                    if(tableDynamique[i-1][j] < tableDynamique[i-1][j+1]) {
                        min = tableDynamique[i-1][j];
                        tableChemins[i][j] = j;
                    }
                    else {
                        min = tableDynamique[i-1][j+1];
                        tableChemins[i][j] = j+1;
                    }
                }

                else if (j == hauteur - 1)
                {
                    if(tableDynamique[i-1][j] < tableDynamique[i-1][j-1]) {
                        min = tableDynamique[i-1][j];
                        tableChemins[i][j] = j;
                    }
                    else {
                        min = tableDynamique[i-1][j-1];
                        tableChemins[i][j] = j-1;
                    }
                }

                // cas général : on compare les 3 energies des pixels à gauche
                else
                {
                    min = Math.min(tableDynamique[i-1][j], Math.min(tableDynamique[i-1][j-1],tableDynamique[i-1][j+1]));

                    if(min == tableDynamique[i-1][j-1]) {
                        tableChemins[i][j] = j-1;
                    }
                    else if (min == tableDynamique[i-1][j]) {
                        tableChemins[i][j] = j;
                    }
                    else {
                        tableChemins[i][j] = j+1;
                    }

                }
                tableDynamique[i][j] = tableEnergie[i][j] + min;
            }
        }

        //On regarde la couture d'energie minimale sur la derniere colonne et son indice

        double energieMin = tableDynamique[largeur-1][0];
        int jMin = 0;

        for(int j = 0; j < hauteur; j++)
        {
            if(energieMin > tableDynamique[largeur-1][j])
            {
                energieMin = tableDynamique[largeur-1][j];
                jMin = j;
            }
        }

        //On crée la couture minimale et on la remplit en remontant la table des chemins

        Couture minCouture = new Couture(largeur, "horizontale");
        minCouture.setEnergie(energieMin);

        for(int i = largeur-1; i >= 0; i--)
        {
            minCouture.setPixels(i, jMin);
            jMin = tableChemins[i][jMin];
        }

        return minCouture;
    }

    /**
     * Trouve de manière dynamique et en backtracking une couture verticale d'énergie minimale pour la supprimer
     * @return Une couture verticale d'énergie minimale
     */
    private Couture minCoutureVerticale()
    {

        int largeur = this.imageSortie.getWidth();
        int hauteur = this.imageSortie.getHeight();

        //On recalcule la table d'energie à chaque fois
        double[][] tableEnergie = tableEnergie();

        //Table pour calculer dynamiquement les energies des coutures
        double[][] tableDynamique = new double[largeur][hauteur];

        //Table pour stocker les chemins parcourus pour trouver ces coutures
        int[][] tableChemins = new int[largeur][hauteur];

        for(int j = 0; j < hauteur; j++)
        {
            for(int i = 0; i < largeur; i++)
            {
                double min;

                //Cas de la premiere ligne
                if(j == 0) {
                    min = 0;
                    tableChemins[i][j] = -1;
                }

                //Cas des bords
                else if (i == 0) {
                    if(tableDynamique[i][j-1] < tableDynamique[i+1][j-1]) {
                        min = tableDynamique[i][j-1];
                        tableChemins[i][j] = i;
                    }
                    else {
                        min = tableDynamique[i+1][j-1];
                        tableChemins[i][j] = i+1;
                    }
                }

                else if (i == largeur - 1) {
                    if(tableDynamique[i][j-1] < tableDynamique[i-1][j-1]) {
                        min = tableDynamique[i][j-1];
                        tableChemins[i][j] = i;
                    }
                    else {
                        min = tableDynamique[i-1][j-1];
                        tableChemins[i][j] = i-1;
                    }
                }

                //Cas général : on prend le minimum des 3 pixels au dessus
                else {
                    min = Math.min(tableDynamique[i][j-1], Math.min(tableDynamique[i-1][j-1],tableDynamique[i+1][j-1]));

                    if(min == tableDynamique[i-1][j-1]) {
                        tableChemins[i][j] = i-1;
                    }
                    else if (min == tableDynamique[i][j-1]) {
                        tableChemins[i][j] = i;
                    }
                    else {
                        tableChemins[i][j] = i+1;
                    }

                }
                tableDynamique[i][j] = tableEnergie[i][j] + min;
            }
        }

        //On regarde la couture d'energie minimale sur la derniere ligne
        double energieMin = tableDynamique[0][hauteur-1];
        int iMin = 0;
        for(int i = 0; i < largeur; i++)
        {
            if(energieMin > tableDynamique[i][hauteur-1])
            {
                energieMin = tableDynamique[i][hauteur-1];
                iMin = i;
            }
        }

        //On crée la couture et on la remplit avec la tableChemins
        Couture minCouture = new Couture(hauteur, "verticale");
        minCouture.setEnergie(energieMin);

        for(int j = hauteur-1; j >= 0; j--)
        {
            minCouture.setPixels(j, iMin);
            iMin = tableChemins[iMin][j];
        }

        return minCouture;
    }

    /**
     * Méthode qui supprime une couture Veritcale ou horizontale de pixels dans l'image
     * @param couture la couture Verticale a supprimer
     */
    private void supprimerCouture(Couture couture){
        int largeur = this.imageSortie.getWidth();
        int hauteur = this.imageSortie.getHeight();
        BufferedImage imageCarved;

        if (couture.getDirection().equals("horizontale")){
            imageCarved = new BufferedImage(largeur, hauteur - 1, BufferedImage.TYPE_INT_RGB);

            //On reremplie imageCarved en sautant les pixels de la couture
            for (int i = 0; i < largeur; i++)
            {
                boolean decaller = false;
                for (int j = 0; j < hauteur - 1; j++) {

                    if (couture.getPixels()[i] == j) {
                        decaller = true;
                    }
                    if(decaller)
                        imageCarved.setRGB(i, j, this.imageSortie.getRGB(i, j+1));
                    else
                        imageCarved.setRGB(i, j, this.imageSortie.getRGB(i, j));
                }
            }
        }

        //Cas si la couture est verticale
        else {

            imageCarved = new BufferedImage(largeur-1, hauteur, BufferedImage.TYPE_INT_RGB);
            for(int j = 0; j < hauteur; j++)
            {
                boolean decaller = false;
                for(int i = 0; i < largeur-1; i++)
                {
                    if(couture.getPixels()[j] == i) {
                        decaller = true;
                    }
                    if(decaller) {
                        imageCarved.setRGB(i, j, this.imageSortie.getRGB(i + 1, j));
                    }
                    else {
                        imageCarved.setRGB(i, j, this.imageSortie.getRGB(i, j));
                    }
                }
            }
        }
        this.imageSortie = imageCarved;
    }


    /**
     * Methode d'accès a l'array liste des coutures supprimées
     * @return L'arrayList des coutures supprimées dans l'ordre de suppression
     */
    protected ArrayList<Couture> getCoutures(){
        return this.coutures;
    }

    /**
     * Methode d'accès à l'image seamcarvé en bufferedImage
     * @return BufferedImage qui stocke l'image redimensionnée
     */
    protected BufferedImage getImageSortie(){
        return this.imageSortie;
    }


    //Méthodes Pour verifier le bon fonctionnement

    /**
     * Sauvegarde l'image réduite sous le type souhaité
     * @param adresse endroit où sauvegarder l'image et son nom
     * @param type type du fichier créé
     * @throws IOException Si le fichier n'arrive pas à être créé
     */
    protected void sauvegarderImageCree(String adresse, String type) throws IOException
    {
        try {
            File fichierSortie = new File(adresse);
            ImageIO.write(this.imageSortie, type, fichierSortie);
        } catch (IOException e)
        {
            System.out.println("Adresse introuvable " + adresse);
            throw (e);
        }
    }

    /**
     * Méthode qui enregistre une image des energies de l'image
     * @throws IOException Si l'image n'arrive pas a être créée
     */
    protected void sauvegarderImageEnergie() throws IOException
    {
        double[][] tableEnergie = this.tableEnergie();

        BufferedImage energy_image = new BufferedImage(this.imageEntree.getWidth(), this.imageEntree.getHeight(), BufferedImage.TYPE_INT_RGB);

        //On calcule l'energie maximale de la table en la parcourant
        double max = tableEnergie[0][0];
        for (double[] ligne : tableEnergie) {
            for (int j = 0; j < tableEnergie[0].length; j++){
                max = Math.max(max, ligne[j]);
            }
        }
        //On construit l'image en gradient de gris par rapport a l'énergie maximale ( max = banc, min = noir )
        for(int i = 0; i < energy_image.getWidth(); i++) {
            for(int j = 0; j < energy_image.getHeight(); j++)
            {
                int gris = (int) ((tableEnergie[i][j]/max)*256);
                int rgb = (gris << 16) + (gris << 8) + gris;
                energy_image.setRGB(i,j,rgb);
            }
        }

        //On enregistre l'image
        try {
            File fichierImageEnergie = new File("out/energieImage");
            ImageIO.write(energy_image, "jpg", fichierImageEnergie);
            System.out.println("La table des energies a été créée");
        } catch (IOException e) {
            System.out.println("L'image des energies n'a pas pu être créée");
            throw (e);
        }
    }

    /**
     * Méthode qui sauvegarde une image avec toutes les coutures supprimées
     * @throws IOException Si l'image n'arrive pas a être créée
     */
    protected void sauvegarderImageCoutures() throws IOException
    {

        int[][] coutureImageTable = new int[this.imageEntree.getWidth()][this.imageEntree.getHeight()];

        int ancienneLargeur = this.imageSortie.getWidth();
        int ancienneHauteur = this.imageSortie.getHeight();

        //On stocke l'image redimensionnée dans le tableau
        for(int i = 0; i < ancienneLargeur; i++) {
            for (int j = 0; j < ancienneHauteur; j++) {
                coutureImageTable[i][j] = this.imageSortie.getRGB(i, j);
            }
        }
        //On parcourt toutes les coutures qu'on a supprimé
        for(int c = this.coutures.size()-1; c >= 0; c--) {
            Couture couture = this.coutures.get(c);
            if(couture.getDirection().equals("horizontale"))
            {
                for(int i = 0; i < ancienneLargeur; i++)
                {
                    int jCouture = couture.getPixels()[i];
                    //On décalle les pixels
                    for (int j = ancienneHauteur ; j > jCouture ; j--){
                        coutureImageTable[i][j] = coutureImageTable[i][j-1];
                    }
                    //On met le pixel en question en blanc
                    coutureImageTable[i][jCouture] = (0xffffff);

                }
                ancienneHauteur++;
            }
            else
            {
                for(int j = 0; j < ancienneHauteur; j++)
                {
                    int iCouture = couture.getPixels()[j];
                    for(int i = ancienneLargeur; i > iCouture; i--){
                        coutureImageTable[i][j] = coutureImageTable[i-1][j];
                    }
                    coutureImageTable[iCouture][j] = (0xffffff);
                }

                ancienneLargeur++;
            }

        }


        BufferedImage coutureImage = new BufferedImage(this.imageEntree.getWidth(), this.imageEntree.getHeight(), BufferedImage.TYPE_INT_RGB);

        for(int i = 0; i < coutureImage.getWidth(); i++)
        {
            for(int j = 0; j < coutureImage.getHeight(); j++)
            {
                coutureImage.setRGB(i,j,coutureImageTable[i][j]);
            }
        }

        try {
            File output_file = new File("out/CoutureImage");
            ImageIO.write(coutureImage, "jpg", output_file);
            System.out.println("L'image des coutures a bien été enregistrée");
        } catch (IOException e)
        {
            System.out.println("L'image des coutures ne peut pas être créée");
            throw (e);
        }
    }

    /**
     * Methode qui affiche dans le terminal l'avancée du seamcarving en pourcentage
     * @param total nombre total d'opérations à réaliser
     * @param afaire nombre d'opérations qu'il reste à faire
     */
    private void Progres(double total, double afaire)
    {
        System.out.println( String.format("%.2f", (total-afaire)/total*100) + "%");
    }

}
