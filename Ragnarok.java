package teste_01;
import robocode.*;
import java.awt.Color;
import static robocode.util.Utils.normalRelativeAngleDegrees;

public class Ragnarok extends Robot {
	int count = 0;
	
	boolean peek;
	double moveAmount;
	double gunTurnAmt;
	String trackName;
	
	public void run() {
		while (true) {
			if (getEnergy() > 50) {
				setColors(Color.black, Color.black, Color.black);
				wallFollowing();
			} else {
				moveAndAttack();
			}
		}
	}
	public void wallFollowing() {
		moveAmount = Math.max(getBattleFieldWidth(), getBattleFieldHeight());
		peek = false;
		turnLeft(getHeading() % 90);
		ahead (moveAmount);
		peek = true;
		turnRight(90);
		turnGunRight(90);
		
		while (true) {
			peek = true;
			ahead(moveAmount);
			peek = false;
			turnRight(90);
		}
	}
	public void moveAndAttack() {
		trackName = null;
		setAdjustGunForRobotTurn(true);
		gunTurnAmt = 10;
		
		while (true) {
			turnGunRight(gunTurnAmt);
			count ++;
			if (count > 2) {
				gunTurnAmt = -10;
			}
			if (count > 5) {
				gunTurnAmt = 10;
			}
			if (count > 11) {
				trackName = null;
			}
		}
	}
	public void onScannedRobot(ScannedRobotEvent e) {
		if (getEnergy() > 50) {
			fire(2);
			if (peek) {
				scan();
			}
		} else {
			if (trackName !=null && !e.getName().equals(trackName)) {
				return;
			}
			if (trackName == null) {
				trackName = e.getName();
				out.println("Tracking " + trackName);
			}
			count = 0;
			if (e.getDistance() > 150) {
				gunTurnAmt = normalRelativeAngleDegrees(e.getBearing() + (getHeading() - getRadarHeading()));
				turnGunRight (gunTurnAmt);
				turnRight(e.getBearing());
				ahead(e.getDistance() - 140);
				return;
			}
			gunTurnAmt = normalRelativeAngleDegrees(e.getBearing() + (getHeading() - getRadarHeading()));
			turnGunRight (gunTurnAmt);
			fire(3);
			
			if (e.getDistance() < 100) {
				if (e.getBearing() > -90 && e.getBearing() <= 90) {
					back(40);
				} else {
					ahead(40);
				}
			}
			scan();
		}
	}
	public void onHitRobot(HitRobotEvent e) {
		if (getEnergy() > 50) {
			if (e.getBearing() > -90 && e.getBearing() < 90) {
				back(100);
			} else {
				ahead(100);
			}
		} else {
			if (trackName != null && !trackName.equals(e.getName())) {
				out.println("Tracking " + e.getName() + " due to collision");
			}
			trackName = e.getName();
			gunTurnAmt = normalRelativeAngleDegrees(e.getBearing() + (getHeading() - getRadarHeading()));
			turnGunRight(gunTurnAmt);
			fire(3);
			back(50);
		}
	}
	public void onWin(WinEvent e) {
		for (int i = 0; i < 50; i++) {
			turnRight(360);
			turnLeft(360);
		}
	}
}