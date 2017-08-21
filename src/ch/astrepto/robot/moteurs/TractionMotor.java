package ch.astrepto.robot.moteurs;

import ch.astrepto.robot.Track;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;

public class TractionMotor {

	private EV3LargeRegulatedMotor motorLeft, rightMotor;
	private EV3LargeRegulatedMotor[] synchro;

	public final static float maxSpeed = 200f;
	public final static float cmInDegres = 0.037699112f; // pas touche (en fct des roues)
	public final static float wheelSpacing = 9.5f;


	public TractionMotor() {
		motorLeft = new EV3LargeRegulatedMotor(MotorPort.B);
		rightMotor = new EV3LargeRegulatedMotor(MotorPort.C);

		synchro = new EV3LargeRegulatedMotor[1];
		synchro[0] = rightMotor;
		motorLeft.synchronizeWith(synchro);
		motorLeft.setAcceleration(2000);
		rightMotor.setAcceleration(2000);
		
//		move(true);
		setSpeed();
	}

	/**
	 * R�gle la vitesse et l'ajuste pour chaque roue de traction en fonction du virage Chaque
	 * partie de la piste a ses r�glages. Si le robot va tout droit, quelques soit les r�glages,
	 * la vitesse de chaque moteur sera �gale
	 * 
	 * @param vitesseActuelle
	 */
	public void setSpeed() {

		float speedLeftMotor = 0;
		float speedRightMotor = 0;
		// on determine la nouvelle valeur de degr� � tourner au robot
		// en fonction de l'endroit sur la piste et du nombre de degr� que tourne le robot
		if (Track.trackSide == 1 && Track.trackPart == 1) {
			speedRightMotor = maxSpeed;
			// la vitesse en fonction du rayon du centre de la piste
			speedLeftMotor = (Track.largeRadius - wheelSpacing) * maxSpeed / Track.largeRadius;
			// puis en fonction du degr� de rotation
			speedLeftMotor = maxSpeed - ((maxSpeed - speedLeftMotor)
					/ DirectionMotor.maxDegree * DirectionMotor.getCurrentAngle());
		} else if (Track.trackSide == -1 && Track.trackPart == -1) {
			speedLeftMotor = Track.smallRadius * maxSpeed / (Track.smallRadius + wheelSpacing);
			speedLeftMotor = maxSpeed - ((maxSpeed - speedLeftMotor)
					/ DirectionMotor.maxDegree * DirectionMotor.getCurrentAngle());
			speedRightMotor = maxSpeed;
		} else if (Track.trackSide == 1 && Track.trackPart == -1) {
			speedLeftMotor = (Track.largeRadius - wheelSpacing) * maxSpeed / Track.largeRadius;
			speedLeftMotor = maxSpeed - ((maxSpeed - speedLeftMotor)
					/ DirectionMotor.maxDegree * DirectionMotor.getCurrentAngle());
			speedRightMotor = maxSpeed;
		} else if (Track.trackSide == -1 && Track.trackPart == 1) {
			speedRightMotor = maxSpeed;
			speedLeftMotor = Track.smallRadius * maxSpeed / (Track.smallRadius + wheelSpacing);
			speedLeftMotor = maxSpeed - ((maxSpeed - speedLeftMotor)
					/ DirectionMotor.maxDegree * DirectionMotor.getCurrentAngle());

		}

		// set la vitesse
		rightMotor.setSpeed(speedRightMotor);
		motorLeft.setSpeed(speedLeftMotor);
	}
	
	/**
	 * Gestion du mouvement du v�hicule (en marche et � l'arret)
	 * 
	 * @param move
	 *                true pour d�marrer, false pour arr�ter
	 */
	public void move(boolean move) {
		motorLeft.startSynchronization();

		if (move) {
			motorLeft.backward();
			rightMotor.backward();
		} else {
			motorLeft.stop();
			rightMotor.stop();
		}

		motorLeft.endSynchronization();
	}

	/**
	 * R�initialise le tachometre de la traction (roues gauche et droite)
	 */
	public void resetTacho() {
		motorLeft.resetTachoCount();
		rightMotor.resetTachoCount();
	}

	/**
	 * Mesure la distance parcourue par la traction. La mesure est une moyenne des deux roues
	 * 
	 * @return le nbr de degr�s de la traction
	 */
	public int getTachoCount() {
		return (motorLeft.getTachoCount() + rightMotor.getTachoCount()) / 2 * -1;
	}
}
