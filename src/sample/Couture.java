package sample;

/**
 * Classe qui représente une Couture. Elle est définie par une suite de pixels d'une certaine taille, une direction et une énergie
 * Permet une implémentation plus simple
 */

public class Couture {

    private double energie;
    private final int[] pixels;
    private final String direction;

    /**
     * Constructeur de la classe
     * @param taille Nombre de pixels de la couture
     * @param direction Indique la direction de la couture, horizontale ou verticale
     * @throws IllegalArgumentException si la direction n'est pas "horizontale" ou "verticale"
     */
    public Couture(int taille, String direction) {
        this.pixels = new int[taille];
        if (!direction.equals("verticale") && !direction.equals("horizontale")){
            throw new IllegalArgumentException();
        }
        else {
            this.direction = direction;
        }
    }

    /**
     * Méthode pour définir le pixel d'une couture
     * @param i l'indice i de la colonne si couture Horizontale, de la ligne si couture Verticale
     * @param j l'indice j du pixel de la couture sur cette colonne/ligne i
     * @throws IllegalArgumentException si i est plus grand que la taille de la couture
     */
    protected void setPixels(int i, int j) {
        if (i > this.pixels.length ){
            throw new IllegalArgumentException();
        }
        pixels[i] = j;
    }

    /**
     * Méthode pour acceder aux pixels de la couture
     * @return un tableau d'entiers où pixels[i] est la position du pixel dans la colonne i (si Couture Horizontale) ou la position du pixel dans la ligne i (si Couture Verticale).
     */
    protected int[] getPixels() {
        return pixels;
    }

    /**
     * Méthode pour définir l'énergie de la méthode
     * @param energie double d'energie de la couture
     * @throws IllegalArgumentException si l'energie est négative
     */
    protected void setEnergie(double energie) {
        if (energie < 0){
            throw new IllegalArgumentException(); }
        this.energie = energie;
    }

    /**
     * Méthode pour accéder à l'énergie de la couture
     * @return Energie totale de la couture
     */
    protected double getEnergie() {
        return energie;
    }

    /**
     * Méthode pour accéder à la direction
     * @return Direction de la couture, "horizontale" ou "verticale" en String
     */
    protected String getDirection() {
        return direction;
    }
}