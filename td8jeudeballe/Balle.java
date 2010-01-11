/** Authors : ***********************************************/
/***** Roman MKRTCHIAN mkrtchia@polytech.unice.fr ***********/
/***** Guy CHAMPOLLION champoll@polytech.unice.fr ***********/
/** website : http://polytechnice.com ***********************/

import java.awt.Color;
import java.awt.Graphics;
import java.lang.Math;
import java.util.Random;

public class Balle{

	//Attributs
	private Color fond;
	private Point orig;
	private int diametre;
	private Random r;
	
	
	//Constructeur
	public Balle(Color f, Point p, int diam){
		fond = f;
		orig = p;
		diametre = diam;
		r = new Random();
	}
	
	public void setColor(Color c) {
		fond = c;
	}
	
	public Point getOrig() {
		return orig;
	}
	
	public Point getOrig2() {
		return new Point(orig.abscisse()+diametre/2, orig.ordonne()+diametre/2);
	}
	
	public int getDiam(){
		return diametre;
	}

	public void deplacer2(int x, int y) {
		orig.setAbscisse(x);
		orig.setOrdonne(y);
	}
	
	// déplace le point d’origine de la balle en (x,y)
	public void deplacer(int x, int y){
		orig.setAbscisse(x-(diametre/2));
		orig.setOrdonne(y-(diametre/2));
	}	
	
	// teste si le point (x,y) est dans la balle ou non
	public boolean appartient(int x, int y){
		return (distance(new Point(x, y)) <= diametre);
	}
	
	public boolean appartient2(int x, int y){
		return (distance(new Point(x/*-(diametre/2)*/, y/*-(diametre/2)*/)) <= diametre);
	}
	
	// retourne la distance entre le point p et le point d’orgine de la balle
	public double distance(Point p){
		return Math.sqrt( (p.abscisse() - orig.abscisse())*(p.abscisse() - orig.abscisse()) + (p.ordonne() - orig.ordonne())*(p.ordonne() - orig.ordonne()));
	}
	
	//Permet d'avoir une distance plus réaliste, en prenant l'origine non pas en haut à gauche mais au milieu de la balle principale
	public double distance2(Point p){
		return Math.sqrt( (p.abscisse() - orig.abscisse()-(diametre/2))*(p.abscisse() - orig.abscisse()-(diametre/2)) + (p.ordonne() - orig.ordonne()-(diametre/2))*(p.ordonne() - orig.ordonne()-(diametre/2)));
	}
	
	//Permet de grossir la balle
	public void grossir(){
		diametre += r.nextInt(2);
	}

	
	// dessine la balle dans le Graphics g
	public void dessiner(Graphics g){
		g.setColor(fond);
		g.fillOval(orig.abscisse(), orig.ordonne(), diametre, diametre);
	}



}

