package ch.astrepto.robot;

import ch.astrepto.robot.capteurs.ColorSensor;
import ch.astrepto.robot.moteurs.DirectionMotor;
import ch.astrepto.robot.moteurs.TractionMotor;
import ch.astrepto.robot.moteurs.UltrasonicMotor;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.utility.Delay;

public class RobotControls {

	private DirectionMotor directionMotor;
	private UltrasonicMotor ultrasonicMotor;
	private TractionMotor tractionMotor;
	private ColorSensor color;
	private static float intensity = 0;
	public static int mode;

	public RobotControls() {
		// mode course solo
		Track.trackPart = 1;
		Track.trackSide = 1;
		directionMotor = new DirectionMotor();
		color = new ColorSensor();
		tractionMotor = new TractionMotor();
		ultrasonicMotor = new UltrasonicMotor();



	}

	/**
	 * Gestion du carrefour Une fois le carrefour détecté, cette section réagit en fonction du
	 * côté du croisement
	 */
	public void crossroads() {
		// indique qu'on est en train de passer le croisement
		tractionMotor.resetTacho();
		// les roues se remettent droites
		directionMotor.goTo(0);
		
		// on fait un zigzag
		double radius = Track.crossroadsLength - Track.gradientWidth / 2d;
		int angle = (int) (DirectionMotor.maxDegree / DirectionMotor.maxAngle *( Math.asin(DirectionMotor.wheelBase / radius) * 180d / Math.PI));
		
		System.out.println(radius);
		System.out.println(angle);
		System.out.println(radius * 2 * Math.PI / 8  / TractionMotor.cmInDegres);
//		Button.waitForAnyPress();
		
		if(Track.trackPart == 1)
			directionMotor.goTo(angle);
		else
			directionMotor.goTo(-angle);
		
		boolean boucle = true;
		while(boucle){
			if(tractionMotor.getTachoCount() >= radius * 2 * Math.PI / 8  / TractionMotor.cmInDegres)
				boucle = false;
		}

		if(Track.trackPart == 1)
			directionMotor.goTo(-angle);
		else
			directionMotor.goTo(angle);
		
		boucle = true;
		while(boucle){
			if(tractionMotor.getTachoCount() >= radius * 2 * Math.PI / 8  / TractionMotor.cmInDegres)
				boucle = false;
		}
		
		Track.crossroads = false;
		Track.justAfterCrossroads = true;
		Track.changeTrackPart();
		tractionMotor.resetTacho();
	}

	/**
	 * Gestion de la direction automatique une fois que la précédente direction est terminée, la
	 * nouvelle est déterminée en fonction de l'intensité lumineuse détectée
	 */
	public void updateDirection() {
		// si l'ultrason n'est pas lié aux roues

		// Maj la direction si "le précédent mvt est fini"
		if (directionMotor.previousMoveComplete()) {
			// l'angle est déterminé par la situation du robot sur la piste
			int angle = directionMotor.determineAngle(intensity);

			// si on est juste après le croisement, l'angle est divisé par 2
			// pour atténuer la reprise de piste
			if (Track.justAfterCrossroads) {
				angle /= 2;
				Track.justAfterCrossroads = false;
			}

			directionMotor.goTo(angle);

		}
	}

	/**
	 * Gestion de la vitesse automatique la vitesse est déterminée en fonction de la distance en
	 * cm mesurée
	 */
	public void updateSpeed() {
		// définition de la vitesse
		tractionMotor.setSpeed();
	}

	/**
	 * Gestion de la détection de l'intensité lumineuse au sol Relève l'intensité lumineuse et
	 * détecte le croisement
	 */
	public void updateLightIntensity() {
		// Relève la valeur lumineuse actuelle
		intensity = color.getIntensity();

		// Détection du carrefour (+3 pour les variations lumineuses)
		if (intensity <= ColorSensor.trackCrossingValue + 3)
			// Indique qu'on est arrivé au carrefour
			Track.crossroads = true;
	}

	/**
	 * Arrête le robot à la fin
	 */
	public void robotStop() {
		// arret du robot
		tractionMotor.move(false);
		// remet les roues droites
		Delay.msDelay(500);
		directionMotor.goTo(0);
		// remet l'ultrason droit
		Delay.msDelay(500);
		ultrasonicMotor.goTo(0, false);
	}

	public void robotStart() {
		tractionMotor.move(true);
		boolean boucle = true;
		while(boucle){
			if(tractionMotor.getTachoCount() >= Track.crossroadsLength + 10){
				boucle = false;
			}
		}
		Track.changeTrackPart();
		Track.changeTrackSide();
	}
}
