import java.awt.Color;
import java.awt.Graphics;
import java.lang.Math;

public class Balle{

	//Attributs
	protected Color fond;
	protected Point orig;
	protected int diametre;
	
	//Constructeur
	public Balle(Color f, Point p, int diam){
		fond = f;
		orig = p;
		diametre = diam;
	}
	
	public void setColor(Color c) {
		fond = c;
	}
	
	public Point getOrig() {
		return orig;
	}

	public void deplacer2(int x, int y) {
		orig.setAbscisse(x);
		orig.setOrdonne(y);
	}
	
	// déplace le point d’origine de la balle en (x,y)
	public void deplacer(int x, int y){
		orig.setAbscisse(x-10);
		orig.setOrdonne(y-9);
	}	
	
	// teste si le point (x,y) est dans la balle ou non
	public boolean appartient(int x, int y){
		return (distance(new Point(x, y)) <= diametre);
	}
	
	// retourne la distance entre le point p et le point d’orgine de la balle
	public double distance(Point p){
		return Math.sqrt( (p.abscisse() - orig.abscisse())*(p.abscisse() - orig.abscisse()) + (p.ordonne() - orig.ordonne())*(p.ordonne() - orig.ordonne()));
	}
	
	public double distance2(Point p){
		return Math.sqrt( (p.abscisse() - orig.abscisse()-10)*(p.abscisse() - orig.abscisse()-10) + (p.ordonne() - orig.ordonne()-10)*(p.ordonne() - orig.ordonne()-10));
	}
	
	// dessine la balle dans le Graphics g
	public void dessiner(Graphics g){
		g.setColor(fond);
		g.fillOval(orig.abscisse(), orig.ordonne(), diametre, diametre);
	}



}
