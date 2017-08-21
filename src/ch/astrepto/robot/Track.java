package ch.astrepto.robot;

public class Track {

	// VARIABLES POUR LA SITUATION SUR LA PISTE
	public static int trackSide; // 1 si grand, -1 si petit
	public static int trackPart; // 1 côté avec priorité de droite, -1 côté prioritaire
	public final static float smallRadius = 10;
	public final static float largeRadius = 35;
	public final static float gradientWidth = 8;

	// VARIABLES POUR LE CARREFOUR
	public static boolean crossroads = false; // si arrivé au carrrefour
	// var permettant d'atténuer l'angle détecté juste après le carrefour et au démarrage
	public static boolean justAfterCrossroads = true;
	public final static float crossroadsLength = 30; // en cm


	/**
	 * Change le côté de la piste
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
