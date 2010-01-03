import java.awt.Canvas;
import java.awt.Image;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Random;
import javax.swing.JOptionPane;



public class Damier extends Canvas implements MouseListener, MouseMotionListener, Runnable{

	//Variables d'instance
	private static final int COTECASE = 29; //Largeur d'une case
	private static int NBOBSTACLES = 32; //Nombre d'obstacles
	public int nbCase; //nombre de cases par côté de la grille
	private Case [] lesObstacles;
	protected int longueurDamier;
	protected int largeurDamier;
	private boolean stop; private boolean exit=false;
	private Image offscreen; //Utile pour le double buffering
	private Graphics og;
	private Thread thread; //Utile pour faire avancer les cases indépendament
	private Random r;
	private Case arrivee;
	private Balle ball;
	private int vitesse; //vitesse des objets
	private int type; //Le type de difficulté

/********************************** Constructeurs *************************************/
	
	public Damier(int larg, int lng, int typ){
		type=typ;
		largeurDamier=larg;
		longueurDamier=lng;
		nbCase = larg;
		setSize(COTECASE*largeurDamier,COTECASE*longueurDamier);
		r = new Random();
		
		//Changements en fonction du type
		switch(type){
			case 1: vitesse=18; break;
			case 2: vitesse=10; break;
			case 3: vitesse=8; break;
			case 4: vitesse=8; break; //le temps est plus long mais ce sera plus rapide en réalité parce qu'on va doubler la distance de déplacement par frame dans la classe Case
			case 5: vitesse=9; break;
		}
		
		lesObstacles = new Case[NBOBSTACLES];
		//Cases à ne pas toucher
		for(int i=0; i<NBOBSTACLES; i++){
			int var1 = r.nextInt(largeurDamier); int var2 = r.nextInt(longueurDamier);
			//Pour éviter les cases de départ et d'arrivée
			while ((var1==0 && var2==0) || (var1==largeurDamier-1) && (var2==longueurDamier-1)){
				var1 = r.nextInt(largeurDamier);
				var2 = r.nextInt(longueurDamier);
			}
			lesObstacles[i]=new Case(Color.yellow, new Point(var1*COTECASE+1, var2*COTECASE+1), COTECASE-1, COTECASE-1, COTECASE, NBOBSTACLES, nbCase);
		}
		//Case d'arrivée
		arrivee = new Case(Color.blue, new Point((largeurDamier-1)*COTECASE+1,(longueurDamier-1)*COTECASE+1), COTECASE-1, COTECASE-1, COTECASE, NBOBSTACLES, nbCase);
		//Balle principale
		ball = new Balle(Color.green, new Point(0, 0), COTECASE-1);
		//Les listeners
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		//Le thread indépendant
		if (type != 0){
			thread = new Thread (this);
			thread.start();
		}
	}

/********************************** Run (thread) **************************************/

	//Se lance automatiquement dès que le thread est démarré
	public void run(){
		while(true){
			if (!stop){
				//gagné
				if(ball.appartient(arrivee.origine().abscisse(), arrivee.origine().ordonne())){
					String typeDifficulte="";
					switch(type){
						case 1: typeDifficulte = "Facile"; break;
						case 2: typeDifficulte = "Normale"; break;
						case 3: typeDifficulte = "Difficile"; break;
						case 4: typeDifficulte = "Légendaire"; break;
						case 5: typeDifficulte = "Contre la montre"; break;
					}
					JOptionPane.showMessageDialog(this, "Vous avez gagné la partie ! en mode : " + typeDifficulte);
					if(type == 3 || type == 4){
						JOptionPane.showMessageDialog(this, "Le mot de passe pour le niveau caché est : toast");
					}
					
					stop = true;
					ball.setColor(Color.blue);
					repaint();
				}
				for(Case c : lesObstacles){
					//perdu
					if(ball.distance(new Point(c.origine().abscisse(), c.origine().ordonne())) < c.getTailleCase()){
						JOptionPane.showMessageDialog(this, "Vous avez perdu la partie !");
						stop = true;
						ball.setColor(Color.red);
						repaint();
						break;
					}
				}
			}
			for(int j=0; j<lesObstacles.length; j++){
				if(type==1 || type==2) lesObstacles[j].bougerXY();
				else if(type==3) lesObstacles[j].bougerXYZ();
				else if(type==4) lesObstacles[j].bougerL();
				else if(type==5) lesObstacles[j].bougerT();
			}
			try {Thread.sleep(vitesse);} catch(Exception e) {}
			repaint();
		}
	}
	
/********************************** Ecouteurs ******************************************/

	public void mousePressed(MouseEvent e){
		if(ball.appartient(e.getX(), e.getY())){
			stop = false; exit=false;
		}
		else{
			stop=true;
		}
	}

	public void mouseDragged(MouseEvent e){
	
		if (!stop && !exit){
			//On déplace
			ball.deplacer(e.getX(), e.getY());
				
				if (type == 0){//Si la difficulté est super facile, on fait le test ici
					if(ball.appartient(arrivee.origine().abscisse(), arrivee.origine().ordonne())){
						JOptionPane.showMessageDialog(this, "Vous avez gagné la partie ! en mode : Très Facile...");
						stop = true;
						ball.setColor(Color.blue);
						repaint();
					}
					for(Case c : lesObstacles){
						//perdu
						if(ball.distance(new Point(c.origine().abscisse(), c.origine().ordonne())) < c.getTailleCase()){
							JOptionPane.showMessageDialog(this, "Vous avez perdu la partie !");
							stop = true;
							ball.setColor(Color.red);
							repaint();
							break;
						}
					}
				}
		}
		repaint();
		
	}
	
	//Utile pour empêcher de tricher en faisant sortir la balle de la grille
	public void mouseExited(MouseEvent e) {
		exit=true;
	}
	public void mouseReleased(MouseEvent e) {
		exit=true;
	}
	
/******************************** Méthodes diverses *************************************/

	//Méthodes inutiles mais nécessaires
	public void mouseEntered(MouseEvent e) {
	}
	public void mouseClicked(MouseEvent e) {
	}
	public void mouseMoved(MouseEvent e) {
	}
	
	//Méthode permettant de remettre à 0 si le jeu est terminé
	public void restart(){
		ball.deplacer2(0, 0);
		ball.setColor(Color.green);
		repaint();
	}
	
	//Surcharge de méthode nécessaire pour le double buffering
	public void update(Graphics g){
		paint(g);
	}
	
/*********************************** Paint ***********************************************/
	
	//Permet d'afficher les éléments
	public void paint(Graphics g){
		
		//On crée l'image qu'on affichera
		if(offscreen == null) {
			offscreen = createImage(getSize().width, getSize().height);
		}
		
		og = offscreen.getGraphics();
		og.setClip(0,0,getSize().width, getSize().height);
		 
		//Le fond
		og.setColor(Color.black);
		og.fillRect(0, 0, COTECASE*largeurDamier, COTECASE*longueurDamier);
		
		//Lignes verticales
		for(int i=0; i<largeurDamier; i++){
			og.setColor(Color.gray);
			og.drawLine(COTECASE*i, 0, COTECASE*i, COTECASE*longueurDamier);
		}
		//Lignes horizontales
		for(int j=0; j<longueurDamier; j++){
			og.setColor(Color.gray);
			og.drawLine(0, COTECASE*j, COTECASE*longueurDamier, COTECASE*j);
		}
		//Les objets
		for(int k=0; k<lesObstacles.length; k++){
			lesObstacles[k].dessiner(og);
		}
		arrivee.dessiner(og);
		ball.dessiner(og);
		
		//super.paint(og);
		g.drawImage(offscreen, 0, 0, null);
		og.dispose();//on vide le Graphics

		
	}
}
