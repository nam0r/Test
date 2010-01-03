import java.awt.Canvas;
import java.awt.Image;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Random;
import java.util.ArrayList;
import javax.swing.JOptionPane;


public class DamierScore extends Canvas implements MouseListener, MouseMotionListener, Runnable{

	//Variables d'instance
	private static final int COTECASE = 29; //Largeur d'une case
	private int nbObstacles = 0; //Nombre d'obstacles
	public int nbCase; //nombre de cases par côté de la grille
	private ArrayList<Case> lesObstacles;
	protected int longueurDamier;
	protected int largeurDamier;
	private boolean stop; private boolean exit=false;
	private Image offscreen; //Utile pour le double buffering
	private Graphics og;
	private Thread thread; //Utile pour faire avancer les cases indépendament
	private Random r;
	private Case arrivee;
	private Balle ball; //Balle principale à déplacer
	private Balle pierre; //Pierres à collecter
	private Balle amulette; //Amulettes de pouvoir
	private static int DIAMPIERRE = 10; //Diamètre d'une pierre précieuse
	private static int DIAMAMULET = 17; //Diamètre d'une amulette
	private int vitesse; //vitesse des objets
	private int type; //Le type de difficulté
	private int score; //Indique le score courant (nombre de rubis)
	private boolean isAmulette; //Indique si une amulette est sur le terrain
	private int isAmulettePoss; //Indique si une amulette est en possession de la personne et son type
	private int dureeAmulette; //Indique la durée qu'il reste d'activité à un pouvoir spécial
	private boolean isInvincible;

/********************************** Constructeurs **************************************/
	
	public DamierScore(int larg, int lng, int typ){
		type = typ;
		largeurDamier = larg;
		longueurDamier = lng;
		nbCase = larg;
		isAmulette = false;
		isAmulettePoss = 0;
		score = 0;
		isInvincible = false; //On est de base pas invincible
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
		
		lesObstacles = new ArrayList<Case>(); //On caste l'ArrayList pour pouvoir utiliser des méthodes dessus
		//Balle principale
		ball = new Balle(Color.green, new Point(0, 0), COTECASE-1);
		//Amulette de pouvoir
		genererAmulette();
		//Pierre précieuse
		genererPierre();
		//Les listeners
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		//Le thread indépendant
		thread = new Thread (this);
		thread.start();
	}

/********************************** Run (thread) **************************************/

	//Se lance automatiquement dès que le thread est démarré
	public void run(){
		while(true){
			if (!stop){
				//Pierre capturée
				if(ball.distance2(new Point(pierre.getOrig().abscisse(), pierre.getOrig().ordonne()))< COTECASE-8){
					score++;
					genererPierre(); //On génère déjà la pierre suivante
					initCase();
					//On génère une amulette si il n'y en a pas
					if(!isAmulette && isAmulettePoss == 0){
						genererAmulette();
					}
					repaint();
				}
				//Amulette juste capturée
				if(isAmulette && ball.distance2(new Point(amulette.getOrig().abscisse(), amulette.getOrig().ordonne()))< COTECASE-4){			
					//Processus pour avoir l'effet du pouvoir de m'amulette
					int rand = r.nextInt(3)+1;
					if(rand == 1) { //Amulette de ralentissement du temps
						isAmulettePoss = 1; dureeAmulette = 2100/vitesse;
						vitesse *= 3;
					}
					else if(rand == 2) { //Amulette d'accélération du temps
						isAmulettePoss = 2; dureeAmulette = 16000/vitesse;
						vitesse /= 3;
					}
					else if(rand == 3) { //Amulette d'invincibilité
						isAmulettePoss = 3; dureeAmulette = 6250/vitesse;
						isInvincible = true;
						
					}
					isAmulettePoss = rand; isAmulette = false;
					repaint();
				}
				//action en cours d'une amulette
				if(isAmulettePoss != 0){
					dureeAmulette--;
					//Si l'action est terminée on en annule l'effet et on n'a plus d'amulette
					if(dureeAmulette <= 0){
						if(isAmulettePoss == 1){
							vitesse /= 3;
						}
						else if(isAmulettePoss == 2){
							vitesse *= 3;
						}
						else if(isAmulettePoss == 3){
							isInvincible = false;
							
						}
						isAmulettePoss = 0;
					}
				}
				//On teste la collision avec un obstacle jaune
				if(!isInvincible){
					for(int i=0; i<lesObstacles.size(); i++){
						//Jeu terminé
						if(ball.distance(new Point(lesObstacles.get(i).origine().abscisse(), lesObstacles.get(i).origine().ordonne())) < lesObstacles.get(i).getTailleCase() ){
							String typeDifficulte="";
							switch(type){
								case 1: typeDifficulte = "Facile"; break;
								case 2: typeDifficulte = "Normale"; break;
								case 3: typeDifficulte = "Difficile"; break;
								case 4: typeDifficulte = "Légendaire"; break;
								case 5: typeDifficulte = "Contre la montre"; break;
							}
							JOptionPane.showMessageDialog(this, "La partie est terminée, vous avez fait un score de " + score + " en mode " + typeDifficulte);
							if((type == 3 || type == 4) && score >= 15){
								JOptionPane.showMessageDialog(this, "Le mot de passe pour le niveau caché est : toast");
							}
							score=0;
							stop = true;
							ball.setColor(Color.red);
							repaint();
							break;
						}
					}
				}
			}
			//On affiche les obstacles jaunes
			for(int j=0; j<lesObstacles.size(); j++){
				if(type==1 || type==2) lesObstacles.get(j).bougerXY();
				else if(type==3) lesObstacles.get(j).bougerXYZ();
				else if(type==4) lesObstacles.get(j).bougerL();
				else if(type==5) lesObstacles.get(j).bougerT();
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
			//On déplace la balle
			ball.deplacer(e.getX(), e.getY());
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

	//Permet de générer une case jaune
	public void initCase(){
		nbObstacles++;
		int var1 = r.nextInt(largeurDamier); int var2 = r.nextInt(longueurDamier);
		lesObstacles.add(new Case(Color.yellow, new Point(var1*COTECASE+1, var2*COTECASE+1), COTECASE-1, COTECASE-1, COTECASE, nbObstacles, nbCase));
		
	}
	
	//Permet de générer une pierre précieuses
	public void genererPierre(){
		pierre = new Balle(Color.red, new Point(r.nextInt(largeurDamier*COTECASE - 10)+5, r.nextInt(largeurDamier*COTECASE - 10)+5), DIAMPIERRE);
	}
	
	//permet de générer une amulette de pouvoir sur le terrain
	public void genererAmulette(){
		if(r.nextInt(5) == 1){//1 chance sur 5 de générer une amulette
			amulette = new Balle(Color.blue, new Point(r.nextInt(largeurDamier*COTECASE - 10)+5, r.nextInt(largeurDamier*COTECASE - 10)+5), DIAMAMULET);
			isAmulette = true;
		}
	}

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
	
	//Surcharge de la méthode update() nécessaire pour le double buffering
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
		for(int k=0; k<lesObstacles.size(); k++){
			lesObstacles.get(k).dessiner(og);
		}
		
		//Balle
		if(isInvincible && r.nextInt(2)==1) ball.dessiner(og);
		else if(!isInvincible) ball.dessiner(og);
		
		pierre.dessiner(og); //pierre précieuse
		if(isAmulette) amulette.dessiner(og); //amulette de pouvoir
		
		//super.paint(og);
		g.drawImage(offscreen, 0, 0, null);
		og.dispose();//on vide le Graphics og

		
	}
}
