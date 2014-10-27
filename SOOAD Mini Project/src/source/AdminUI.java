package source;

import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import javax.swing.table.AbstractTableModel;

public class AdminUI extends JFrame {

	private static final long serialVersionUID = 819277906705746380L;

	private ArrayList<TableDataHolder> tableList;
	private TableModel tmodel;
	private JTable table, custTable, transTable;
	private JButton viewCustInfo, viewTransInfo;
	
	
	
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

		tableList = new ArrayList<TableDataHolder>();

		setExtendedState(JFrame.MAXIMIZED_BOTH); 
		pack();
		initView();
	}

	private void initView() {

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		getContentPane().setLayout(null);
<<<<<<< HEAD
<<<<<<< HEAD
		
		viewCustInfo = new JButton();
		storeButton = new JButton();
		accountIDLabel = new JLabel(); 
		accountCreditLabel = new JLabel();
		accountCreditMaxLabel = new JLabel();
		accountIDText = new JTextField();
		accountCreditText = new JTextField();
		accountCreditMaxText = new JTextField();
		
<<<<<<< HEAD
		ImageIcon img = new ImageIcon("src/images/SwiftCardMini.png");
=======
		
		ImageIcon img = new ImageIcon("SwiftCardMini.png");
>>>>>>> parent of e0e40cd... Final
=======
		
		ImageIcon img = new ImageIcon("SwiftCardMini.png");
>>>>>>> parent of e0e40cd... Final
=======
		ImageIcon img = new ImageIcon("src/SwiftCardMini.png");
>>>>>>> parent of 27197a5... Fn
		JLabel imgFrame = new JLabel(img);
		imgFrame.setBounds(1100, 0, 300, 100);
		imgFrame.setVisible(true);
		getContentPane().add(imgFrame);

		viewCustInfo = new JButton();
		viewTransInfo = new JButton();
		
		viewCustInfo.setText("View Customer Information");
		viewTransInfo.setText("View Transaction Information");
		
		viewCustInfo.setBounds(350, 0, 200, 50);
		viewTransInfo.setBounds(600, 0, 200, 50);
	}

	public void clearScreen() {
		//TODO: Add Clearing code to make all objects invisible
	}

	private void tableSetup() {		

		if(table == null) {
			TableDataHolder d = new TableDataHolder();
			//TODO: Add the headers to the Table here in String format
			tableList.add(d);

			tmodel = new TableModel();
			table = new JTable(tmodel);
			//TODO: Update the position with some other values
			table.setBounds(300, 100, 800, 800);
			getContentPane().add(table);
		}
		else {
			tmodel.fireTableDataChanged();
		}
		table.setVisible(true);
		ClientUI.waitDialog.setVisible(false);
	}

	private class TableDataHolder {
		//TODO: Add Table data items here
	}

	private class TableModel extends AbstractTableModel {

		private static final long serialVersionUID = -1962279468479145799L;

		/**
		 * TODO: Change the number returned here corresponding to the number of data variables in TableDataHolder
		 */
		@Override
		public int getColumnCount() {
			return 0;
		}

		@Override
		public int getRowCount() {
			return tableList.size();
		}

		@Override
		public Object getValueAt(int row, int col) {
			/*
			 * TODO: Change this code to retrieve specific items
			 * 
			 * if(col == 0) 
				return tableList.get(row).transactionID;
			else if(col == 1) 
				return tableList.get(row).userID; 
			 */

			return -1; 
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return false;
		}
	}

}
