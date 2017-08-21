package ch.astrepto.robot;

import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;

public class MainDriveItSolo {

	public static void main(String[] args) {

		// à faire avant de déposer le robot sur la piste

		RobotControls rob = new RobotControls();
//		displayStart();

		boolean boucle = true;
		long time1 = System.currentTimeMillis();
		long time2;
		long interval = 0;
		
		rob.robotStart();

//		rob.crossroads();
		
		

		do {
			// GESTION DU RELEVE LUMINEUX DE LA PISTE
			rob.updateLightIntensity();
				
			// GESTION DE LA DIRECTION AUTOMATIQUE
			rob.updateDirection();
			

			// GESTION DE LA VITESSE
			// pour régler les variations de vitesse de chacune des roues en fonction du virage
			rob.updateSpeed();

			// GESTION DU CROISEMENT
			if (Track.crossroads ) {
				rob.crossroads();
			}

			// GESTION DE L'ARRET DE LA COURSE
			// le robot s'arrête après 2 min 30 (et quelques secondes)
			time2 = System.currentTimeMillis();
			interval += time2 - time1;
			time1 = time2;
			if (interval >= 155l * 1000l)
				boucle = false;

		} while (boucle);

		rob.robotStop();

	}

	private static void displayStart() {
		// écran blanc
		for (int i = 0; i < 128; i++) {
			drawLine(1, 178, 0, i, 1);
			Delay.msDelay(10);
		}

		drawArrow(-1, 20, 66, 61, 0);
		drawArrow(1, 20, 106, 61, 0);
		for (int i = 61; i < 101; i++) {
			drawLine(1, 40, 66, i, 0);
			Delay.msDelay(10);
		}

		drawArrow(-1, 20, 68, 63, 1);
		drawArrow(1, 20, 108, 63, 1);
		for (int i = 63; i < 103; i++) {
			drawLine(1, 40, 69, i, 1);
			Delay.msDelay(10);
		}

		drawArrow(-1, 20, 70, 65, 0);
		drawArrow(1, 20, 110, 65, 0);
		for (int i = 65; i < 105; i++) {
			drawLine(1, 40, 71, i, 0);
			Delay.msDelay(10);
		}
		LCD.drawString("mode solo", 3, 0, true);
		LCD.drawString("......START.......", 0, 1, true);

		LCD.drawString("ENTER", 7, 5, false);

		Button.ENTER.waitForPress();
		LCD.clear();
	}

	private static void drawArrow(int direction, int size, int x, int y, int color) {
		int xi = 0;
		int yj = 0;
		int i = 1;
		while (yj != size * 2) {

			xi = 0;
			do {
				if (direction == -1)
					LCD.setPixel(x - xi, y + yj, color);
				else
					LCD.setPixel(x + xi, y + yj, color);
				xi += 1;
			} while (xi != i);

			yj += 1;

			if (yj > size)
				i -= 1;
			else
				i += 1;
		}
	}

	private static void drawLine(int direction, int size, int x, int y, int color) {

		if (direction == 1) {
			int xi = 0;
			while (xi != size) {
				LCD.setPixel(x + xi, y, color);
				xi += 1;
			}
		} else {
			int yj = 0;

			while (yj != size) {
				LCD.setPixel(x, y + yj, color);
				yj += 1;
			}
		}
	}
}
