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
	 * Gestion du carrefour Une fois le carrefour d�tect�, cette section r�agit en fonction du
	 * c�t� du croisement
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
	 * Gestion de la direction automatique une fois que la pr�c�dente direction est termin�e, la
	 * nouvelle est d�termin�e en fonction de l'intensit� lumineuse d�tect�e
	 */
	public void updateDirection() {
		// si l'ultrason n'est pas li� aux roues

		// Maj la direction si "le pr�c�dent mvt est fini"
		if (directionMotor.previousMoveComplete()) {
			// l'angle est d�termin� par la situation du robot sur la piste
			int angle = directionMotor.determineAngle(intensity);

			// si on est juste apr�s le croisement, l'angle est divis� par 2
			// pour att�nuer la reprise de piste
			if (Track.justAfterCrossroads) {
				angle /= 2;
				Track.justAfterCrossroads = false;
			}

			directionMotor.goTo(angle);

		}
	}

	/**
	 * Gestion de la vitesse automatique la vitesse est d�termin�e en fonction de la distance en
	 * cm mesur�e
	 */
	public void updateSpeed() {
		// d�finition de la vitesse
		tractionMotor.setSpeed();
	}

	/**
	 * Gestion de la d�tection de l'intensit� lumineuse au sol Rel�ve l'intensit� lumineuse et
	 * d�tecte le croisement
	 */
	public void updateLightIntensity() {
		// Rel�ve la valeur lumineuse actuelle
		intensity = color.getIntensity();

		// D�tection du carrefour (+3 pour les variations lumineuses)
		if (intensity <= ColorSensor.trackCrossingValue + 3)
			// Indique qu'on est arriv� au carrefour
			Track.crossroads = true;
	}

	/**
	 * Arr�te le robot � la fin
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
