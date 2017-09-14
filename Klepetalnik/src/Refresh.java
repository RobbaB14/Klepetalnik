import java.util.Timer;
import java.util.TimerTask;

public class Refresh extends TimerTask {

	String name;

	public Refresh(String name) {
		this.name = name;
	}

	public void activate() {
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(this, 1000, 1000);
	}

	@Override
	public void run() {
		Gui.freshUsers();
		Gui.reciveMessage();
	}
}
