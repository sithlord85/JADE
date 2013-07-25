/*
*Agent Based Research Assistant
*API: Eclipse Juno 
* JADE Framework implementation
*GUI
*/
package prototype.abra5;

import javax.swing.JFrame;
import jade.core.behaviours.OneShotBehaviour;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.JSlider;




public class abraGUI extends JFrame {

  private JPanel contentPane;
	public JTextField textField = new JTextField();
	public JTextField textField_1 = new JTextField();
	
	JButton btnSearch = new JButton("Search");
	JButton btnRes = new JButton("Reset Agent");
	
	protected MainAgent control;
	

	
	public abraGUI(MainAgent controller) {
		
		 try {
	            jbInit();
	        }
	        catch(Exception e) {
	            e.printStackTrace();
	        }
		 
		 control = controller;
		
	}
	
	
	
	private void jbInit() throws Exception {
		
		this.setTitle("Agent Based Research Assistant");
        
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 431);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblEnterSearchText = new JLabel("Enter Text");
		lblEnterSearchText.setBounds(30, 23, 110, 14);
		contentPane.add(lblEnterSearchText);
		
		
		textField.setBounds(160, 20, 203, 20);
		contentPane.add(textField);
		textField.setColumns(10);
		

		JLabel lblEnterFileLocation = new JLabel("Enter file location");
		lblEnterFileLocation.setBounds(30, 60, 110, 14);
		contentPane.add(lblEnterFileLocation);
		
		
		textField_1.setBounds(160, 60, 203, 20);
		contentPane.add(textField_1);
		textField_1.setColumns(10);

		
		btnSearch.setBounds(160, 100, 203, 20);
		btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                btn_search_actionPerformed(e);
            } });
		contentPane.add(btnSearch);
		
	
		btnRes.setBounds(160, 130, 203, 20);
		btnRes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                btn_stop_actionPerformed(e);
            } });
		contentPane.add(btnRes);
		
		
	
	
	}
	
	
	 
	 void enableControls( boolean starting ) {
	        btnSearch.setEnabled( !starting );
	        
	       
	    }
	 
	 void btn_search_actionPerformed(ActionEvent e) {
	        enableControls( true );
	        
	        
	 
	        

	        // One shot behaviour for Main Agent to send search parameters to Secondary Agent
	        control.addBehaviour( new OneShotBehaviour() {
	                                  public void action() {
	                                      ((MainAgent) myAgent).callAgent( textField.getText(), textField_1.getText() );
	                                      
	                                  }
	                              } );
	    }
	 
	  void btn_stop_actionPerformed(ActionEvent e) {
	        enableControls( false );

	        //  One shot behaviour for Main Agent to send cancel msg to Secondary Agent
	        control.addBehaviour( new OneShotBehaviour() {
	                                  public void action() {
	                                      ((MainAgent) myAgent).endSearch();
	                                  }
	                              } );
	    }


	
}
