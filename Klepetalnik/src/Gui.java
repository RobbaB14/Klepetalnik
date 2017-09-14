import java.awt.Container;

import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import javax.management.timer.Timer;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import java.awt.GridBagConstraints;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionListener;

import java.awt.Insets;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import java.awt.event.KeyAdapter;
import javax.swing.JLabel;
import java.awt.Font;

public class Gui extends JFrame implements ActionListener, KeyListener{
	
	JTextField input;
	static String name;
	static JList list;
	static JTextArea ChatHistory;
	private JTextArea output;
	static String receiver = "vsi";
	private static JLabel sendTo;
	static List users;
	

	private static final long serialVersionUID = 1L;

	
	public static void main (String[]args){
		Gui gui = new Gui();
	}

	public Gui() {
		super();
		
		Container pane = this.getContentPane();
		GridBagLayout gridBagLayout_1 = new GridBagLayout();
		gridBagLayout_1.rowWeights = new double[]{0.0, 0.0, 0.0, 1.0, 0.0, 1.0, 1.0, 0.0, 0.0};
		gridBagLayout_1.columnWeights = new double[]{1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0};
		pane.setLayout(gridBagLayout_1);
		
		//Dialog, ki ob zagonu programa vpra�a za ime. 
		String s = (String)JOptionPane.showInputDialog(this, "Vnesi ime:", "Prijava", JOptionPane.PLAIN_MESSAGE, 
				null, null, "");
		this.name = s;
		
		// �e je s = null, naj ga zapre. 
		if (s == null){
			System.exit(0);		
		}		
		
		//Se vpi�emo:
		Boolean tryLogIn = Chat.log_in(this.name);


		while (!tryLogIn){
			JOptionPane.showMessageDialog(this, "Uporabni�ko ime �e bostaja. Izberi drugo ime!", "Napaka", JOptionPane.ERROR_MESSAGE); 
			s = (String)JOptionPane.showInputDialog(this, "Vnesi ime:", "Prijava", JOptionPane.PLAIN_MESSAGE, 
					null, null, "");
			this.name = s;
			tryLogIn = Chat.log_in(this.name);
		}
		
		//Dodamo naslov okna.
		this.setTitle("Klepetalnik - "+name); 
		
		ChatHistory = new JTextArea();//ChatHistory je obmo�je, v katerem se bo izpisoval pogovor. 
		JScrollPane sp = new JScrollPane(this.ChatHistory); //Dodamo drsnik. 
		ChatHistory.setFont(new Font("Comic Sans MS", Font.PLAIN, 13));
		ChatHistory.setWrapStyleWord(true);
		ChatHistory.setEditable(false);
		ChatHistory.setRows(15);
		GridBagConstraints gbc_txtrChatHistory = new GridBagConstraints();
		gbc_txtrChatHistory.gridwidth = 8;
		gbc_txtrChatHistory.gridheight = 8;
		gbc_txtrChatHistory.insets = new Insets(0, 0, 5, 0);
		gbc_txtrChatHistory.fill = GridBagConstraints.BOTH;
		gbc_txtrChatHistory.gridx = 1;
		gbc_txtrChatHistory.gridy = 0;
		gbc_txtrChatHistory.weightx = 10.0;
		getContentPane().add(sp, gbc_txtrChatHistory);
		
		
		list = new JList();//Obmo�je na katerem bo seznam oprijavljenih uporabnikov. S klikom izberemo, komu �elimo poslati sporo�ilo. 
		list.setFont(new Font("Comic Sans MS", Font.PLAIN, 13));
		GridBagConstraints gbc_list = new GridBagConstraints();
		gbc_list.gridheight = 8;
		gbc_list.insets = new Insets(0, 0, 5, 5);
		gbc_list.fill = GridBagConstraints.BOTH;
		gbc_list.gridx = 0;
		gbc_list.gridy = 0;
		getContentPane().add(list, gbc_list);
		
		users = Chat.get_users(name); //Pridobimo seznam uporabnikov 
		users.add(new User("vsi", new Date())); //Umetno dodamo izbiro "vsi", �e �elimo sporo�ilo poslati vsem prijavljenim uporabnikom.  
		list.setListData(users.toArray());
		list.addListSelectionListener(new Listener());
		
		//Ustvarimo refresh, ki bo osve�eval na� seznam uporabnikov in gledal, �e smo dobili kak
		Refresh refresh = new Refresh(name);
		refresh.activate();
		
		
		input = new JTextField(); //Polje v katerega uporabnik vpisuje sporo�ilo. 
		input.setFont(new Font("Comic Sans MS", Font.PLAIN, 13));
				
		addWindowListener(new WindowAdapter() {
			// Ob zagonu programa je fokus na polju za vnos besedila. 
			public void windowOpened(WindowEvent e) {
				input.requestFocusInWindow();
		}});
		
		input.addKeyListener(new KeyAdapter() {
			//Ko uporabnik pritisne enter, po�lemo sporo�ilo, pobri�emo vnosno vrstico in izpi�emo besedilo v chatHistory.
			public void keyReleased(KeyEvent arg0) {
				Integer code = arg0.getKeyCode();
				if (code == 10) {
					if (receiver.equals("vsi")){
						Chat.send(name, fixText(input.getText()));		
					} else {
						Chat.send(name, receiver, fixText(input.getText()));
					}	
					//Izpis besedila v chatHistory:
					String res = input.getText();	//Besedilo, ki ga �e nismo izpisali shranjujemo v 'res'. 
					input.setText("");	//Pobri�emo tekst v vnosni vrstici.				
					Boolean firstLine = true; //Prva vrstica je posebna, ker v njej izpi�emo ime po�iljatelja. 
					int len = res.length();
					int num = 50; //Nastavimo dol�ino vrstice. 
					while (len > 0){
						if (len<num){ //Izpisujemo zadnjo vrstico.
							if (firstLine){
								ChatHistory.setText(ChatHistory.getText() + "\n" + name + ":	" + res);
							}else {ChatHistory.setText(ChatHistory.getText() + "\n" + "\t" + res);
							}
							ChatHistory.setText(ChatHistory.getText() + "\n"); //Sporo�ila med seboj lo�imo s prazno vrstico. 
							firstLine = false;
							len = 0;
						}else { //Vrstica, ki jo izpisujemo ni zadnja.
							int n = 0;
							boolean lineDone = false;
							while (n <= 5){ //Posku�amo najti ' ', za to, da bomo �imlep�e delili besedilo. 
								char last =res.charAt(num-1+n);
								if (last == ' '){
									String line = res.substring(0,num-1+n);
									if (firstLine){
										ChatHistory.setText(ChatHistory.getText() + "\n" + name + ":	" + line);
										firstLine = false;
									}else{ChatHistory.setText(ChatHistory.getText() + "\n" + "\t" + line);
									}
									lineDone = true;
									res = res.substring(num-1+n);
									break;
								}else {n++;
								}
							}
							if (lineDone == false){//�e nismo na�li ' ', potem besedo delimo z '-'. 
								String line = res.substring(0,num);
								if (firstLine){
									ChatHistory.setText(ChatHistory.getText() + "\n" + name + ":	" + line + "-");
									firstLine = false;
								}else{ ChatHistory.setText(ChatHistory.getText() + "\n" + "\t" + line + "-");
								}
								res = res.substring(num);
							}
							
							len = res.length();
						}
					}
				}
			}


				});
		
		//Ko uporabnik zapre okno, ga odjavimo. 
		this.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(WindowEvent winEvt) {
				Chat.log_out(name);
				System.exit(0);
			}
		});
		
		sendTo = new JLabel("Prejemnik: vsi"); //Prostor v katerem je izpisano, komu po�iljamo sporo�ilo. 
		sendTo.setFont(new Font("Comic Sans MS", Font.PLAIN, 13));
		GridBagConstraints gbc_sendTo = new GridBagConstraints();
		gbc_sendTo.insets = new Insets(0, 0, 0, 5);
		gbc_sendTo.anchor = GridBagConstraints.EAST;
		gbc_sendTo.gridx = 0;
		gbc_sendTo.gridy = 8;
		getContentPane().add(sendTo, gbc_sendTo);
		GridBagConstraints gbc_input = new GridBagConstraints();
		gbc_input.gridwidth = 8;
		gbc_input.fill = GridBagConstraints.HORIZONTAL;
		gbc_input.gridx = 1;
		gbc_input.gridy = 8;
		getContentPane().add(input, gbc_input);
		input.setColumns(10);
		setSize(800,800);
		setVisible(true);
	}


	/**
	 * Funkcija, ki na list izpi�e trenutni seznam uporabnikov. 
	 */
	public static void freshUsers(){
		users = Chat.get_users(name);
		users.add(new User("vsi", new Date()));
		list.setListData(users.toArray());
	}
	

	/**
	 * Funkcija gleda, �e smo dobili sporo�ilo. �e smo ga dobili, ga izpi�e na chatHistory. 
	 */
	public static void reciveMessage(){
		List<Message> message = Chat.recive(name);
		for (Message i: message){		
			String res = i.getText();	//V 'res' bomo shranjevali del sporo�ila, ki ga �e nismo zapisali. 	
			Boolean firstLine = true; //Prva vrstica je posebna, ker vsebuje ime po�iljatelja. 
			int len = res.length(); 
			int num = 50; //Nastavimo dol�ino vrstice. 
			while (len>0){
				if (len<num){ //Izpisujemo zadnjo vrstico.
					if (firstLine){
						ChatHistory.setText(ChatHistory.getText() + "\n" + i.getSender() + ":	" + res);
					}else {ChatHistory.setText(ChatHistory.getText() + "\n" + "\t" + res);
					}
					ChatHistory.setText(ChatHistory.getText() + "\n");
					firstLine = false;
					len = 0;
				}else { //Vrstica, ki jo izpisujemo ni zadnja. 
					int n = 0;
					boolean lineDone = false;
					while (n <= 5){ //I��emo ' ', da bo deljenje besedila �imlep�e. 
						char last =res.charAt(num-1+n);
						if (last == ' '){
							String line = res.substring(0,num-1+n);
							if (firstLine){
								ChatHistory.setText(ChatHistory.getText() + "\n" + i.getSender() + ":	" + line);
								firstLine = false;
							}else{ChatHistory.setText(ChatHistory.getText() + "\n" + "\t" + line);
							}
							lineDone = true;
							res = res.substring(num-1+n);
							break;
						}else {n++;
						}
					}
					if (lineDone == false){//�e presledka nismo na�li, besedo delimo z '-'. 
						String line = res.substring(0,num);
						if (firstLine){
							ChatHistory.setText(ChatHistory.getText() + "\n" + name + ":	" + line + "-");
							firstLine = false;
						}else{ ChatHistory.setText(ChatHistory.getText() + "\n" + "\t" + line + "-");
						}
						res = res.substring(num);
					}
					
					len = res.length();
				}
			}
			
		}
	}


	/**
	 * Receiver nastavimo na uporabnika, ki smo ga izbrali v list-u. 
	 */
	public static void setReceiver () {
		if (!list.isSelectionEmpty()){
			receiver = list.getSelectedValue().toString() ;
			list.clearSelection();
		}
	}
	
	/**
	 * Izbranega prejemnika izpi�emo v sendTo.  
	 */
	public static void printReceiver (){
		sendTo.setText("Prejemnik: "+ receiver);
	}
	
	/**
	 * Funkcija vzame string, ki ga je vpisal uporabnik in ga pretvori v string, ki ga lahko po�ljemo.  
	 */
	public static String fixText(String value)
	{
	    StringBuilder builder = new StringBuilder();
	    for( char c : value.toCharArray() )
	    {
	        if ( c == '\"' )
	            builder.append( "\\\"" );
	        else if( c == '\t' )
	            builder.append( "\\t" );
	        else
	            builder.append( c );
	    }
	    return builder.toString();
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub	
	}
}