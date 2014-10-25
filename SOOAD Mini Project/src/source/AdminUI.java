package source;

import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import javax.swing.table.AbstractTableModel;

public class AdminUI extends JFrame {

	private static final long serialVersionUID = 819277906705746380L;

	private ArrayList<TableDataHolder> tableList;
	private TableModel tmodel;
	private JTable table;
	
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

		//TODO: Add Initialization Code
		/*
		 * Eg : 
		 * JButton transAddButton = new JButton(); // Create a new Object
		 * transAddButton.setText("Add Transaction"); // Set its text
		   transAddButton.setBounds(100, 0, 200, 50); // Set its size and position
		   transAddButton.addActionListener(new ActionListener() { // Set up its onClickListener
			
			@Override
			public void actionPerformed(ActionEvent e) {
				transactionAddHandler();
			}
		   });
		   getContentPane().add(transAddButton); // Add it to the pane.
		   transAddButton.setVisible(true); // Make it visible
		 */
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
