package ch.astrepto.robot;

public class Track {

	// VARIABLES POUR LA SITUATION SUR LA PISTE
	public static int trackSide; // 1 si grand, -1 si petit
	public static int trackPart; // 1 c�t� avec priorit� de droite, -1 c�t� prioritaire
	public final static float smallRadius = 10;
	public final static float largeRadius = 35;
	public final static float gradientWidth = 8;

	// VARIABLES POUR LE CARREFOUR
	public static boolean crossroads = false; // si arriv� au carrrefour
	// var permettant d'att�nuer l'angle d�tect� juste apr�s le carrefour et au d�marrage
	public static boolean justAfterCrossroads = true;
	public final static float crossroadsLength = 30; // en cm


	/**
	 * Change le c�t� de la piste
	 */
	public static void changeTrackSide() {
		trackSide *= -1;
	}

	/**
	 * Change la partie de la piste (du huit)
	 */
	public static void changeTrackPart() {
		trackPart *= -1;
	}
}
