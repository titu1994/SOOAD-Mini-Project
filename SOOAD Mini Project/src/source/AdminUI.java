package source;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.table.AbstractTableModel;

public class AdminUI extends JFrame {

	private static final long serialVersionUID = 819277906705746380L;
	private JButton viewCustInfo, storeButton;
	private JLabel accountIDLabel, accountCreditLabel, accountCreditMaxLabel;
	private JTextField accountIDText, accountCreditText, accountCreditMaxText;
	
	/**
	 * To make a Dialog for waiting, use ClientUI.waitDialog.setVisible(true);
	 * To hide the waiting dialog, use ClientUI.waitDialog.setVisible(false)
	 * 
	 * To call a direct access function to the client thread, use ClientUI.client. ... method 
	 * Note : All functions in ClientUI MUST be Static. 
	 * 
	 * See data flow in NetBankClient class. Try and maintain it. I got bored so i skipped the glue code 
	 * from JFrame -> ClientUI -> Client class and instead made direct connection such as
	 * JFrame -> Client using ClientUI.client object
	 */

	public AdminUI() {
		//TODO: Initialise Variables

		setExtendedState(JFrame.MAXIMIZED_BOTH); 
		pack();
		initView();
	}

	private void initView() {

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		getContentPane().setLayout(null);
		
		viewCustInfo = new JButton();
		storeButton = new JButton();
		accountIDLabel = new JLabel(); 
		accountCreditLabel = new JLabel();
		accountCreditMaxLabel = new JLabel();
		accountIDText = new JTextField();
		accountCreditText = new JTextField();
		accountCreditMaxText = new JTextField();
		
		ImageIcon img = new ImageIcon("src/SwiftCardMini.png");
		JLabel imgFrame = new JLabel(img);
		imgFrame.setBounds(1100, 0, 300, 100);
		imgFrame.setVisible(true);
		getContentPane().add(imgFrame);

		viewCustInfo.setText("Update Account");
		viewCustInfo.setBounds(350, 0, 200, 50);
		viewCustInfo.setVisible(true);
		viewCustInfo.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				showChoices();
			}
		});
		getContentPane().add(viewCustInfo);
		
		storeButton.setText("Submit");
		storeButton.setBounds(300, 400, 200, 50);
		storeButton.setVisible(true);
		storeButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				storeAction();
			}
		});
		getContentPane().add(storeButton);
		
		accountIDLabel.setText("Account ID");
		accountIDLabel.setBounds(200, 100, 100, 50);
		getContentPane().add(accountIDLabel);
		
		accountCreditLabel.setText("Account Credit");
		accountCreditLabel.setBounds(200,200, 100, 50);
		getContentPane().add(accountCreditLabel);
		
		accountCreditMaxLabel.setText("Account Max");
		accountCreditMaxLabel.setBounds(200, 300, 100, 50);
		getContentPane().add(accountCreditMaxLabel);
		
		accountIDText.setBounds(350, 100, 100, 50);
		getContentPane().add(accountIDText);
		
		accountCreditText.setBounds(350,200, 100, 50);
		getContentPane().add(accountCreditText);
		
		accountCreditMaxText.setBounds(350, 300, 100, 50);
		getContentPane().add(accountCreditMaxText);
		
	}

	protected void storeAction() {
		String accIDString = accountIDText.getText();
		String cred = accountCreditText.getText();
		String m = accountCreditMaxText.getText();
		long accID, credit, max;
		
		if(accIDString == null || accIDString.length() == 0) {
			JOptionPane.showMessageDialog(this, "ID must be filled");
			return;
		}
		else {
			accID = Long.parseLong(accIDString);
		}
		
		if(cred == null || cred.length() == 0) {
			credit = -1;
		}
		else {
			credit = Long.parseLong(cred);
		}
		
		if(m == null || m.length() == 0) {
			max = -1;
		}
		else {
			max = Long.parseLong(m);
		}
		
		ClientUI.client.updateAccount(accID, credit, max);
	}

	protected void showChoices() {
		accountIDLabel.setVisible(true);
		accountCreditLabel.setVisible(true);
		accountCreditMaxLabel.setVisible(true);
		accountIDText.setVisible(true);
		accountCreditText.setVisible(true);
		accountCreditMaxText.setVisible(true);
	}

	public void clearScreen() {
		//TODO: Add Clearing code to make all objects invisible
	}
	
	public void clientAccountUpdatedSuccesfully(boolean isSuccesful) {
		if(isSuccesful) {
			JOptionPane.showMessageDialog(this, "Account has been updated!");
		}
		else {
			JOptionPane.showMessageDialog(this, "Account failed to get updated");
		}
	}

}
