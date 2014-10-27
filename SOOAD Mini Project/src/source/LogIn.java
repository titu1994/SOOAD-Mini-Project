package source;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

public class LogIn extends JFrame {
	private JLabel username, password;
	private JTextField user;
	private JPasswordField pass;
	private JButton authenticate;
	
	private long id;
	private String pas;
	
	private AdminUI admin;
	
	private static final long serialVersionUID = -6234447662078402196L;

	public LogIn() throws IOException {
		setExtendedState(JFrame.MAXIMIZED_BOTH); 
		pack();
		initView();
	}

	private void initView() throws IOException {

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		getContentPane().setLayout(null);
<<<<<<< HEAD
		
		ImageIcon img = new ImageIcon("src/images/SwiftCardMini.png");
=======
		
		ImageIcon img = new ImageIcon("SwiftCardMini.png");
<<<<<<< HEAD
>>>>>>> parent of e0e40cd... Final
=======
>>>>>>> parent of e0e40cd... Final
		JLabel imgFrame = new JLabel(img);
		imgFrame.setBounds(400, 0, 400, 100);
		imgFrame.setVisible(true);
		getContentPane().add(imgFrame);
		
		
		username = new JLabel();
		password = new JLabel();
		user = new JTextField();
		pass = new JPasswordField();
		authenticate = new JButton();

		username.setText("User-ID");
		username.setBounds(300, 100, 100, 100);
		password.setText("Password");
		password.setBounds(300, 130, 100, 100);

		getContentPane().add(username);
		getContentPane().add(password);

		user.setBounds(450, 130, 100, 30);
		pass.setBounds(450, 160, 100, 30);

		getContentPane().add(user);
		getContentPane().add(pass);

		authenticate.setBounds(400, 200, 100, 40);
		authenticate.setText("Submit");

		authenticate.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				id = Long.parseLong(user.getText().toString());
				pas = new String(pass.getPassword());
				ClientUI.startClient(id, pas);
			}
		});

		getContentPane().add(authenticate);
	}
	
	private Object options[] = {"Add Account", "Cancel"};
	private Object options2[] = {"Yes", "No"};
	public void logInFailed() {
		int option = JOptionPane.showOptionDialog(this, "Account does not exist. Do you want to create a new account?", "Account Not Found", 
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
		
		if(option == 0) {
			
			int admin = JOptionPane.showOptionDialog(this, "Will this be an admin account?", "Account Not Found", 
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options2, options2[0]);
			
			if(admin == 0) {
				ClientUI.client.createNewAccount(id, pas, true);
			}
			else {
				ClientUI.client.createNewAccount(id, pas, false);
			}
		}
		else {
			ClientUI.client.cancelConnection();
		}
	}
	
	
}
