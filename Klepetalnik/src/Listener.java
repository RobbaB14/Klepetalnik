import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


public class Listener implements ListSelectionListener {
	//'prislu�kuje' spremembam. Ko se zgodi sprememba izvede spodjo fn.
	    public void valueChanged(ListSelectionEvent e) {
	    	Gui.setReceiver();
	    	Gui.printReceiver();
	    }

}
