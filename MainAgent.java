/*
*Agent Based Research Assistant
*API: Eclipse Juno 
* JADE Framework implementation
*Main Agent
*/
package prototype.abra5;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import jade.core.AID;
import jade.core.Agent;
import jade.core.ProfileImpl;
import jade.core.Profile;
import jade.wrapper.PlatformController;
import jade.wrapper.AgentController;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.DFService;
import jade.domain.FIPAException;
import javax.swing.*;
import org.apache.commons.collections.ListUtils;
import java.util.*;
import java.text.NumberFormat;


public class MainAgent extends Agent {
  
	public final static String GOODBYE = "GOODBYE";
	public final static String SEARCH_TEXT = "SEARCH";
	public final static String HELLO = "HELLO";
	public final static String DONE = "DONE";
	
	
	
	
	 protected NumberFormat m_avgFormat = NumberFormat.getInstance();
	 protected JFrame m_frame = null;
	 protected Vector agentList = new Vector();    
	 protected int agentCount = 0; 
	 protected long m_startTime = 0L;
	 protected boolean m_searchOver = false;
	 int m = 0;
	 int notify = 0;
	 String[] file_n = null;

	//constructor
	  public MainAgent() {
	        m_avgFormat.setMaximumFractionDigits( 2 );
	        m_avgFormat.setMinimumFractionDigits( 2 );
	    }
	  //Agent creation and registration
	  protected void setup() {
	        try {
	            System.out.println( getLocalName() + " setting up");

	            // create the agent description of itself
	            DFAgentDescription dfd = new DFAgentDescription();
	            dfd.setName( getAID() );
	            DFService.register( this, dfd );

	            // add the GUI
	            setupUI();

	            // add a Behaviour to handle messages from agents
	            addBehaviour( new CyclicBehaviour( this ) {
	                            public void action() {
	                                ACLMessage msg = receive();

	                                if (msg != null) {
	                                    if (HELLO.equals( msg.getContent() )) {
	                                        // agent has initiated 
	                                        agentCount++;
	                                        

	                                        if (agentCount == agentList.size()) {
	                                            System.out.println( "All agents have initiated, starting search" );
	                                            // all agents start searching
	                                            startSearch();
	                                            
	                                        }
	                                    }
	                                    else if (DONE.equals( msg.getContent() )) {
	                                       
	                                    	notify+=1;
	                                    	if (notify == agentCount){
	                                    		
	                                    		doCheck();
	                                    		System.out.println("YEAH!!!!");
	                                    		
	                                    	}
	                                    	
	                                    }
	                                    
	                                }
	                                else {
	                                    // if no message is arrived, block the behaviour
	                                    block();
	                                }
	                            }
	                        } );
	        }
	        catch (Exception e) {
	            System.out.println( "Saw exception in HostAgent: " + e );
	            e.printStackTrace();
	        }

	    }
		
	  //Generate list of pdf docs to be searched, Secondary Agent creation
	  protected void callAgent( String search, String directory) {
		  int k = 0;
		  int Agent = 0;
		  
		  File targetFile = new File(directory);

	    if(targetFile.isDirectory()) {
	          //get list of files and check directory
	          String[] files = targetFile.list();

	         

	          //now work through all pdf files
	          long fileCount = files.length;
	          
	          file_n = new String[(int) (long) fileCount];
	          
	          for (int i = 0; i < fileCount; i++) {


	              if (files[i].toLowerCase().endsWith(".pdf")) {

	            	  		m+=1;
	            	  		file_n[k] = files[i];
        	  				System.out.println(file_n[i]);
	                      
	                  
	              }
	              k+=1;
	              
	          }
	          
	          System.out.println("Total number of pdf files = " + m);
	      }
	      else {
	          System.err.println(directory + " is not a directory. Exiting program");
	      }

	 
		  	
		  	
		  	//System.out.println(search);
		  
		  	Object [] args = new Object[2];
	        args[0] = search;
	        args[1] = directory;
		  	
	        // remove any old state
	        agentList.clear();
	        agentCount = 0;
	        
	       if(m == 1 || m == 2){
	    	   
	    	   Agent = 1;
	    	   
	       } else if (m > 2){
	    	   
	    	   int d = (int)Math.floor(m/2);
	    	 
	    	   if (d == 1){
	    		   d = d + 1;
	    		   Agent = d;
	    	   	}	else {
	    	   
	    		   Agent = d;
	    	   		}
	    	   }
	    	   
	       
	       
	       System.out.println("Number of Agents = " + Agent);
	      

	        // notice the start time
	        m_startTime = System.currentTimeMillis();

	     

		PlatformController container = getContainerController(); // get a container controller for creating new agents
	        // create N secondary agents
	        try {
	            for (int i = 0;  i < Agent;  i++) {
	                // create a new agent
			String localName = "agent_"+i;
			AgentController search_agent = container.createNewAgent(localName, "prototype.abra5.SecondaryAgent", args);
			search_agent.start();
	          

	                // keep the agents's ID on a local list
					agentList.add( new AID(localName, AID.ISLOCALNAME) );
	            }
	        }
	        catch (Exception e) {
	            System.err.println( "Exception while adding guests: " + e );
	            e.printStackTrace();
	        }
	    }
	  
	  protected void getInfo (String text, String directory ){
		  
	  }
	  
		//GUI Initiation
	  
	  private void setupUI() {
		  
	        m_frame = new abraGUI( this);

	        m_frame.setSize( 400, 200 );
	        m_frame.setLocation( 400, 400 );
	        m_frame.setVisible( true );
	        m_frame.validate();
	    }
	  
	     // msg sent to all agents to kill the search
	   protected void endSearch() {
	       
	        m_searchOver = true;

	       
	        System.out.println( "Stopping search " );

	        // GOODBYE msg sent to all 
	        for (Iterator i = agentList.iterator();  i.hasNext();  ) {
	            ACLMessage msg = new ACLMessage( ACLMessage.INFORM );
	            msg.setContent( GOODBYE );

	            msg.addReceiver( (AID) i.next() );

	            send(msg);
	        }

	        agentList.clear();
	    }
	   //Message sent to all created secondary agents with name of file to be checked 
	   protected void startSearch() {
		   System.out.println("Files to be read = " + m);
			System.out.println("No. of Agents = " + agentCount);
		   
		   for (int msg_cnt = 0; msg_cnt < agentCount; msg_cnt++){
				ACLMessage msg = new ACLMessage( ACLMessage.INFORM );
		        msg.setContent( file_n[msg_cnt] );
		        msg.addReceiver( new AID( "agent_"+msg_cnt, AID.ISLOCALNAME ) );
		        send( msg );
			}
	    

	        
	    }
	   //Check 'chkdfiles.txt' and get list of pdf docs already checked
		protected void doCheck(){
			
			try {
				FileReader fileReader = new FileReader("chkdfiles.txt");
				BufferedReader bufferedReader = new BufferedReader(fileReader);
				List<String> lines = new ArrayList<String>();
				String line = null;
				while ((line = bufferedReader.readLine()) != null) {
		            lines.add(line);
		        }
		        bufferedReader.close();
		        
		        System.out.println(lines);
		        
		        List<String> allfiles = Arrays.asList(file_n);
		        System.out.println(allfiles);
		        List diff = ListUtils.subtract(allfiles, lines);
		        
		        if (diff == null){
		        	System.out.println("Done");
		        }
		        else
		        {
		        	
		        	List s1=new ArrayList();
		        	s1.add(null);

		        	diff.removeAll(s1);
		        
		       
		        	String[] array = (String[]) diff.toArray(new String[diff.size()]);
		        	
		        	int arr_sze = array.length;
		        	 System.out.println(arr_sze);
		        	
		        	for (int j = 0; j < arr_sze; j++){
		        		
		        		ACLMessage msg = new ACLMessage( ACLMessage.INFORM );
		    	        msg.setContent( array[j] );
		    	        msg.addReceiver( new AID( "agent_"+j, AID.ISLOCALNAME ) );
		    	        send( msg );
		        		
		        	}
		        }
				
			} catch (Exception e) {
				
				System.out.println(e);
				
			}
			
			
		}
	   
	
	   
	  



}
