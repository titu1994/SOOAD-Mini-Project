package source;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.table.AbstractTableModel;
import javax.xml.soap.Text;

public class TransactionUI extends JFrame {
	
	private static final long serialVersionUID = 1653398564068135279L;
	
	private ArrayList<DisplayTransaction> transList;
	private TransactionDataModel tmodel;
	
	private JTable table;
	private JButton transAddButton, viewTransButton, alterAccPassButton, viewAccountDataButton, submitAdd, accountEdit;
	private JTextField transIDText,  transToText, transAmtText;
	private JPasswordField accountOldText, accountNewText, accountNewCheckText;
	private JLabel transIDLabel, transToLabel, transAmtLabel, accountOldLabel, accountNewLabel, accountNewCheckLabel, accountIDLabel, accountMaxLabel, accountCreditLabel, accountIDValLabel, accountMaxValLabel, accountCreditValLabel;

	public TransactionUI() {
		setExtendedState(JFrame.MAXIMIZED_BOTH); 
		pack();
		transList = new ArrayList<DisplayTransaction>();
		initView();
	}
	
	private void initView() {

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		getContentPane().setLayout(null);
		
		ImageIcon img = new ImageIcon("src/SwiftCardMini.png");
		JLabel imgFrame = new JLabel(img);
		imgFrame.setBounds(1100, 0, 300, 100);
		imgFrame.setVisible(true);
		getContentPane().add(imgFrame);
		
		transAddButton = new JButton();
		viewTransButton = new JButton();
		alterAccPassButton = new JButton();
		viewAccountDataButton = new JButton();
		submitAdd = new JButton();
		accountEdit = new JButton();
		
		transIDText = new JTextField();	
		transToText = new JTextField();
		transAmtText = new JTextField();
		transIDLabel = new JLabel();
		transToLabel = new JLabel();
		transAmtLabel = new JLabel();
		
		accountOldLabel = new JLabel();
		accountNewLabel = new JLabel();
		accountNewCheckLabel = new JLabel();
		accountOldText = new JPasswordField();
		accountNewText = new JPasswordField();
		accountNewCheckText = new JPasswordField();
		
		accountIDLabel = new JLabel();
		accountMaxLabel = new JLabel();
		accountCreditLabel = new JLabel();
		accountIDValLabel = new JLabel();
		accountMaxValLabel = new JLabel();
		accountCreditValLabel = new JLabel();
		
		transAddButton.setText("Add Transaction");
		transAddButton.setBounds(100, 0, 200, 50);
		transAddButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				transactionAddHandler();
			}
		});
		getContentPane().add(transAddButton);
		
		viewTransButton.setText("View all Transactions");
		viewTransButton.setBounds(350, 0, 200, 50);
		viewTransButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				viewAllTransactionsHandler();
			}
		});
		getContentPane().add(viewTransButton);
		
		alterAccPassButton.setText("Alter Account Password");
		alterAccPassButton.setBounds(600, 0, 200, 50);
		alterAccPassButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				alterAccountPasswordHandler();
			}
		});
		getContentPane().add(alterAccPassButton);
		
		submitAdd.setText("Submit");
		submitAdd.setBounds(200, 300, 200, 50);
		submitAdd.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ClientUI.waitDialog.setVisible(true);
				ClientUI.client.addTransaction(Long.parseLong(transIDText.getText()), transToText.getText(), Double.parseDouble(transAmtText.getText()));
				transIDText.setText("");
				transToText.setText("");
				transAmtText.setText("");
			}
		});
		
		accountEdit.setText("Change Password");
		accountEdit.setBounds(200,400,200,50);
		accountEdit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String old = new String(accountOldText.getPassword());
				String new1 = new String(accountNewText.getPassword());
				String new2 = new String(accountNewCheckText.getPassword());
				if(new1.equals(new2)) {
					ClientUI.waitDialog.setVisible(true);
					ClientUI.client.alterPassword(old, new1);
					accountOldText.setText("");
					accountNewText.setText("");
					accountNewCheckText.setText("");
				}
			}
		});
		
		viewAccountDataButton.setText("View Account Data");
		viewAccountDataButton.setBounds(850, 0, 200, 50);
		viewAccountDataButton.setVisible(true);
		viewAccountDataButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ClientUI.waitDialog.setVisible(true);
				ClientUI.client.viewAccountDetails();
			}
		});
		getContentPane().add(viewAccountDataButton);
		
		transIDLabel.setBounds(0, 100, 100, 50);
		transIDLabel.setText("TransactionID");
		transToLabel.setBounds(125, 100, 100, 50);
		transToLabel.setText("To");
		transAmtLabel.setBounds(250, 100, 100, 50);
		transAmtLabel.setText("Amount");
		
		transIDText.setBounds(0, 200, 100, 50);
		transToText.setBounds(125, 200, 100, 50);
		transAmtText.setBounds(250, 200, 100, 50);
		
		accountOldLabel.setBounds(100, 100, 200, 50);
		accountOldLabel.setText("Enter Old Password");
		accountNewLabel.setBounds(100, 200, 200, 50);
		accountNewLabel.setText("Enter New Password");
		accountNewCheckLabel.setBounds(100, 300, 200, 50);
		accountNewCheckLabel.setText("Re-enter New Password");
		
		accountOldText.setBounds(350, 100, 100, 50);
		accountNewText.setBounds(350, 200, 100, 50);
		accountNewCheckText.setBounds(350, 300, 100, 50);
		
		accountIDLabel.setBounds(100, 100, 200, 50);
		accountIDLabel.setText("Account ID");
		accountMaxLabel.setBounds(100, 200, 200, 50);
		accountMaxLabel.setText("Max Credit");
		accountCreditLabel.setBounds(100, 300, 200, 50);
		accountCreditLabel.setText("Credit Limit");
		accountIDValLabel.setBounds(350, 100, 200, 50);
		accountMaxValLabel.setBounds(350, 200, 200, 50);
		accountCreditValLabel.setBounds(350, 300, 200, 50);
		
	}

	boolean accountFirst = false;
	protected void alterAccountPasswordHandler() {
		clearScreen();
		if(!accountFirst) {
			getContentPane().add(accountEdit);
			getContentPane().add(accountOldLabel);
			getContentPane().add(accountNewLabel);
			getContentPane().add(accountNewCheckLabel);
			getContentPane().add(accountOldText);
			getContentPane().add(accountNewText);
			getContentPane().add(accountNewCheckText);
			accountFirst = true;
		}
		
		accountEdit.setVisible(true);
		accountOldLabel.setVisible(true);
		accountNewLabel.setVisible(true);
		accountNewCheckLabel.setVisible(true);
		accountOldText.setVisible(true);
		accountNewText.setVisible(true);
		accountNewCheckText.setVisible(true);
		
	}

	protected void viewAllTransactionsHandler() {
		clearScreen();
		ClientUI.waitDialog.setVisible(true);
		tableSetup();
		ClientUI.client.viewAllTransactions();
	}

	boolean first = false;
	protected void transactionAddHandler() {
		clearScreen();
		System.out.println("Entered Transaction Handler");
		if(!first) {
			getContentPane().add(submitAdd);
			getContentPane().add(transIDLabel);
			getContentPane().add(transToLabel);
			getContentPane().add(transAmtLabel);
			getContentPane().add(transIDText);
			getContentPane().add(transToText);
			getContentPane().add(transAmtText);
			first = true;
		}
		submitAdd.setVisible(true);
		transIDText.setVisible(true);
		transToText.setVisible(true);
		transAmtText.setVisible(true);
		
		transIDLabel.setVisible(true);
		transToLabel.setVisible(true);
		transAmtLabel.setVisible(true);
		
	}
	
	public void clearScreen() {
		if(table != null)
			table.setVisible(false);
		transIDText.setVisible(false);
		transToText.setVisible(false);
		transAmtText.setVisible(false);
		
		transIDLabel.setVisible(false);
		transToLabel.setVisible(false);
		transAmtLabel.setVisible(false);
		
		submitAdd.setVisible(false);
		
		accountEdit.setVisible(false);
		accountOldLabel.setVisible(false);
		accountNewLabel.setVisible(false);
		accountNewCheckLabel.setVisible(false);
		accountOldText.setVisible(false);
		accountNewText.setVisible(false);
		accountNewCheckText.setVisible(false);
		
		accountIDLabel.setVisible(false);
		accountMaxLabel.setVisible(false);
		accountCreditLabel.setVisible(false);
		accountIDValLabel.setVisible(false);
		accountMaxValLabel.setVisible(false);
		accountCreditValLabel.setVisible(false);
	}

	private void tableSetup() {		
		
		if(table == null) {
			DisplayTransaction d = new DisplayTransaction();
			d.transactionID =  "Transaction ID";
			d.userID = "User ID";
			d.toName = "Send To";
			d.amount = "Amount";
			transList.add(d);
			
			tmodel = new TransactionDataModel();
			table = new JTable(tmodel);
			table.setBounds(300, 100, 800, 800);
			getContentPane().add(table);
		}
		else {
			tmodel.fireTableDataChanged();
		}
		table.setVisible(true);
		ClientUI.waitDialog.setVisible(false);
	}
	
	/*
	 * Duplicated Functions from the ClientUI class for clear distinction of data flow and operation on data.
	 */
	public void clientTransactionAdded(boolean succesful) {
		ClientUI.waitDialog.setVisible(false);
		if(succesful) {
			System.out.println("Added Transaction");
		}
		else {
			System.out.println("Trasaction failed to add");
		}
	}

	public void clientAllTransactionData(NetBankTransactionData[] datas) {
		transList.clear();
		
		DisplayTransaction d = new DisplayTransaction();
		d.transactionID =  "Transaction ID";
		d.userID = "User ID";
		d.toName = "Send To";
		d.amount = "Amount";
		transList.add(d);
		
		for(NetBankTransactionData data : datas) {
			d = new DisplayTransaction();
			d.transactionID =  "" + data.transactionID;
			d.userID = "" + data.userID;
			d.toName = data.getTransactionToName();
			d.amount = "" + data.transactionAmount;
			transList.add(d);
		}
		tableSetup();
	}

	public void clientOldPasswordMatchesNewPassword() {
		ClientUI.waitDialog.setVisible(false);
		JOptionPane.showMessageDialog(this, "Old Password Matches New Password");
	}

	public void clientOldPasswordNotEnteredCorrectly() {
		ClientUI.waitDialog.setVisible(false);
		JOptionPane.showMessageDialog(this, "Old Password entered was wrong.");
	}
	
	public void clientNewPasswordEmpty() {
		ClientUI.waitDialog.setVisible(false);
		JOptionPane.showMessageDialog(this, "New Password is empty");
	}

	public void clientOldPasswordNotFound() {
		ClientUI.waitDialog.setVisible(false);
		JOptionPane.showMessageDialog(this, "Old Password not found in database.");
	}
	
	public void clientPasswordChanged() {
		ClientUI.waitDialog.setVisible(false);
		JOptionPane.showMessageDialog(this, "Password was succesfully changed.");
	}
	
	boolean accountDataFirst = true;
	public void clientAccountData(NetBankAccountData data) {
		clearScreen();
		
		if(accountDataFirst) {
			getContentPane().add(accountIDLabel);
			getContentPane().add(accountMaxLabel);
			getContentPane().add(accountCreditLabel);
			getContentPane().add(accountIDValLabel);
			getContentPane().add(accountMaxValLabel);
			getContentPane().add(accountCreditValLabel);
			accountDataFirst = false;
		}
		
		accountIDLabel.setVisible(true);
		accountMaxLabel.setVisible(true);
		accountCreditLabel.setVisible(true);
		accountIDValLabel.setVisible(true);
		accountMaxValLabel.setVisible(true);
		accountCreditValLabel.setVisible(true);
		
		accountIDValLabel.setText(data.getAccountID()+"");
		accountMaxValLabel.setText(data.getCreditMaxLimit()+"");
		accountCreditValLabel.setText(data.getCreditConsumed()+"");
		ClientUI.waitDialog.setVisible(false);
	}
	
	private class DisplayTransaction {
		String transactionID;
		String userID;
		String toName;
		String amount;
	}
	
	private class TransactionDataModel extends AbstractTableModel {

		private static final long serialVersionUID = -1962279468479145799L;

		@Override
		public int getColumnCount() {
			return 4;
		}

		@Override
		public int getRowCount() {
			return transList.size();
		}

		@Override
		public Object getValueAt(int row, int col) {
			if(col == 0) 
				return transList.get(row).transactionID;
			else if(col == 1) 
				return transList.get(row).userID; 
			else if(col == 2) 
				return transList.get(row).toName; 
			else if(col == 3) 
				return transList.get(row).amount;
			
			return col; 
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return false;
		}
	}

}
