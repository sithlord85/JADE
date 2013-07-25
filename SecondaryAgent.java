/*
*Agent Based Research Assistant
*JADE Framework implementation
*API: Eclipse Juno 
*Secondary Agent
*/
package prototype.abra5;

import java.awt.Rectangle;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import org.w3c.dom.Document;
import org.jpedal.PdfDecoder;
import org.jpedal.exception.PdfException;
import org.jpedal.grouping.PdfGroupingAlgorithms;
import org.jpedal.grouping.SearchType;
import org.jpedal.objects.PdfPageData;
import jade.core.Agent;
import jade.core.AID;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.DFService;


public class SecondaryAgent extends Agent {
  
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String DONE = "DONE";
	ArrayList co_ords=null;
	
	private File xmlOutputFile;
	private  String xmlOutputPath;
	
	String textToFind = null;
	String m = null;
	File file = new File("chkdfiles.txt");
	String directory = null;
	
	 private static boolean enableXML = false;
	  private static boolean enableSTDout = true;
	  PdfDecoder decodePdf = null;
	  String separator = System.getProperty("file.separator");
	  int[] areaToScan=null;
	  int wrd_cnt = 0;
	  Document doc;
	  int end = 0;
	  
	  
/

/** Set up the agent. Register with the DF
* Secondary Agent behaviour is added to process
* Main agent is notified of Secondary Agent creation.
*/
protected void setup() {
   try {
   	
   	final Object[] args = getArguments();
   		directory = (String) args[1];
   		textToFind = (String) args[0];
       // create the agent descrption of itself
       ServiceDescription sd = new ServiceDescription();
       sd.setType( "searchAuest" );
       sd.setName( "AgentServiceDescription" );
       DFAgentDescription dfd = new DFAgentDescription();
       dfd.setName( getAID() );
       dfd.addServices( sd );

       // register the description with the DF
       DFService.register( this, dfd );

       // notify the host that we have arrived
       ACLMessage result = new ACLMessage( ACLMessage.INFORM );
       result.setContent( MainAgent.HELLO );
       result.addReceiver( new AID( "host", AID.ISLOCALNAME ) );
       send( result );

       // add a Behaviour to process incoming messages
       addBehaviour( new CyclicBehaviour( this ) {
                       public void action() {
                           // listen if a  message arrives
                           ACLMessage msg = receive( MessageTemplate.MatchPerformative( ACLMessage.INFORM ) );

                           if (msg != null) {
                               if (MainAgent.GOODBYE.equals( msg.getContent() )) {
                                   // time to go
                                   leaveSearch();
                               }
                               else if (msg.getContent().endsWith("pdf" )) {
                                   
                            	   	m = msg.getContent();
                               		doSearch(m );
                               }
                              
                           }
                           else {
                               // if no message is recieved, block the behaviour
                               block();
                           }
                       }
                   } );
   }
   catch (Exception e) {
       System.out.println( "Saw exception in SearchAgent: " + e );
       e.printStackTrace();
   }

}




/**
*  Search terminated and Secondary Agent deregistered from the DF and deleted from the platform.
*/
public void leaveSearch() {
   try {
       DFService.deregister( this );
       this.doDelete();
      
   }
   catch (FIPAException e) {
       System.err.println( "Saw FIPAException while leaving party: " + e );
       e.printStackTrace();
   }
}


/**
* Search is initiated
*/
public void doSearch(String p ) {
	

   	
  

	 
	  
	  
	   findText(p);
  
}



public ArrayList getCoords()
{
   return co_ords;
}

public float[] getCoords(int page)
   {
       return (float[]) co_ords.get(page - 1);
   }


//Search String passed to jpedal 'decodeFile' function
private void findText(String file_name)
{
   createXMLFile(true);

   co_ords = new ArrayList();

   /**
    * if file name ends pdf, do the file otherwise
    * do every pdf file in the directory. We already know file or
    * directory exists so no need to check that, but we do need to
    * check its a directory
    */
   File targetFile = new File(directory);

 if(targetFile.isDirectory()) {
 
       //get list of files and check directory
       String[] files = targetFile.list();

      
        decodeFile(directory + file_name);
        
   }
   else {
       System.err.println(file_name + " is not a directory. Exiting program");
   }

   //close XML file
   createXMLFile(false);
}



public void createXMLFile(boolean open){
   if(enableXML) {
       if(open) {
           xmlOutputFile = new File(xmlOutputPath);

           if(xmlOutputFile.exists()) {
               xmlOutputFile.delete();
               try {
                   xmlOutputFile.createNewFile();
               }
               catch (Exception e) {
                   enableXML = false;
                   System.err.println("Unable to create XML file: " + e + '\n');
               }
           }

           if(enableXML) {
               try {
                   PrintWriter outputStream = new PrintWriter(new FileWriter(xmlOutputFile));
                   outputStream.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                   outputStream.println("<search>");
                   outputStream.println("<term>" + textToFind + "</term>");
                   outputStream.close();
               }
               catch (Exception e) {
                   enableXML = false;
                   System.err.print("Failed to write to XML file: " + e + '\n');
               }
           }
       }
       else {
           try {
               PrintWriter outputStream = new PrintWriter(new FileWriter(xmlOutputFile, true));
               outputStream.println("</search>");
               outputStream.close();
           }
           catch (Exception e) {
               System.err.print("Exception creating closing XML file: " + e + '\n');
           }
       }
   }
}

public void createFileXMLElement(String filePath, boolean open)
   {
       if(enableXML) {
           try {
               PrintWriter outputStream = new PrintWriter(new FileWriter(xmlOutputFile, true));
               if(open) {
                   outputStream.println("<file>");
                   outputStream.println("<path>" + filePath + "</path>");
               }
               else {
                   outputStream.println("</file>");
               }
               outputStream.close();
           }
           catch (Exception e) {
               System.out.print("Creating new outputFile: " + e);
           }
       }
   }

public void createFindXMLElement(float x, float y, int pageNo)
   {
       if(enableXML) {
           try {
               PrintWriter outputStream = new PrintWriter(new FileWriter(xmlOutputFile, true));
               outputStream.println("<found>");
               outputStream.println("<pageNo>" + pageNo + "</pageNo>");
               outputStream.println("<x>" + x + "</x>");
               outputStream.println("<y>" + y + "</y>");
               outputStream.println("</found>");
               outputStream.close();
           }
           catch (Exception e) {
               System.out.print("Creating new outputFile: " + e);
           }
       }
   }
//File's checked to search text
private void decodeFile(String file_name)
{
   /**debugging code to create a log
    LogWriter.setupLogFile(true,0,"","v",false);
    LogWriter.log_name =  "log.txt";
    /***/

   createFileXMLElement(file_name, true);
   
  

   //PdfDecoder returns a PdfException if there is a problem
   try {
   	
   	
       decodePdf = new PdfDecoder(false);
       decodePdf.setExtractionMode(PdfDecoder.TEXT); //extract just text
       PdfDecoder.init(true);
       //make sure widths in data CRITICAL if we want to split lines correctly!!

       /**
        * open the file (and read metadata including pages in  file)
        */
       
       
       
       
       if(enableSTDout) {
       	
           System.out.println("Opening file: " + file_name);
           
       }
       
       decodePdf.openPdfFile(file_name);
   }
   catch (PdfException e) {
       System.err.println("Ignoring " + file_name);
       System.err.println("Due to: " + e);
       createFileXMLElement(file_name, false);
       return;
   }

   /**
    * extract data from pdf (if allowed).
    */
   if ((decodePdf.isEncrypted()&&(!decodePdf.isPasswordSupplied())) && (!decodePdf.isExtractionAllowed())) {
       System.out.println("Encrypted settings");
       System.out.println(
               "Please look at Viewer for code sample to handle such files");
       System.out.println("Or get support/consultancy");
   }
   else {
       //page range
       int start = 1;
       end = decodePdf.getPageCount();

       /**
        * extract data from pdf
        */
       try {
           for (int page = start; page <= end; page++) { //read pages

               if(enableSTDout) {
                   System.out.println("=========================");
                   System.out.println("Page "+page);
                   System.out.println("=========================");
               }

               //decode the page
               decodePdf.decodePage(page);

               /** create a grouping object to apply grouping to data*/
               PdfGroupingAlgorithms currentGrouping =decodePdf.getGroupingObject();
               if(currentGrouping!=null){

                   int x1,y1,x2,y2;

                   /**use whole page size for  demo - get data from PageData object unless set*/
                   if(areaToScan==null){
                       PdfPageData currentPageData = decodePdf.getPdfPageData();
                       x1 = currentPageData.getMediaBoxX(page);
                       x2 = currentPageData.getMediaBoxWidth(page)+x1;

                       y2 = currentPageData.getMediaBoxY(page);
                       y1 = currentPageData.getMediaBoxHeight(page)+y2;
                   }else{
                       x1=areaToScan[0];
                       y1=areaToScan[1];
                       x2=areaToScan[2];
                       y2=areaToScan[3];
                   }
                   //tell user
                   if(enableSTDout) {
                       System.out.println(
                               "Scanning for text ("+textToFind+") rectangle ("
                                       + x1
                                       + ','
                                       + y1
                                       + ' '
                                       + x2
                                       + ','
                                       + y2
                                       + ')');
                   }

                   /**Co-ordinates are x1,y1 (top left hand corner), x2,y2(bottom right) */

                   /**co-ords for start of object are returned in float object.
                    * if not found co-ords=null
                    * if found co_ords[0]=x1, co_ords[1]=y
                    */
                   float[] co_ords;

                   try{
                       co_ords =currentGrouping.findText(
                               new Rectangle(x1,y2,x2-x1,y1-y2),
                               page,
                               new String[]{textToFind},
                               SearchType.MUTLI_LINE_RESULTS);

                       this.co_ords.add(co_ords);

                   } catch (Exception e) {
                       decodePdf.closePdfFile();
                       System.err.println("Ignoring " + file_name);
                       System.err.println("Due to: " + e);
                       createFileXMLElement(file_name, false);
                       return;
                   }

                   if (co_ords == null) {
                       if(enableSTDout) {
                           System.out.println("Text not found on page.");
                       }
                   }
                   else {
                       if(enableSTDout) {
                           System.out.println("Found " + (co_ords.length/5) + " on page.");
                       }
                       for(int i = 0; i <co_ords.length; i+=5) {
                           if(enableSTDout) {
                        	   
                               System.out.println("Text found at "+co_ords[i]+", "+co_ords[i+1]);
                               wrd_cnt+=1;
                           }
                           createFindXMLElement(co_ords[i],co_ords[i+1],page);
                       }
                   }
               }
           }

           //remove data once written out
           decodePdf.flushObjectValues(false);
           

       }
       catch (Exception e) {
           decodePdf.closePdfFile();
           System.err.println("Exception: " + e.getMessage());
           createFileXMLElement(file_name, false);
           return;
       }

       /**
        * flush data structures - not strictly required but included
        * as example
        */
       decodePdf.flushObjectValues(true); //flush any text data read

       /**tell user*/
       if(enableSTDout) {
           System.out.println("File read...");
    
       }
       
       

   }
   
   

   //Close file xml element
   createFileXMLElement(file_name, false);

   /**close the pdf file*/
   decodePdf.closePdfFile();
   System.out.println(wrd_cnt + "Instances of searched text found");
   float ratio = wrd_cnt / (float) end;
   //Search results printed in 'filename'Results.txtz
   try{
	   
	   File f = new File(textToFind+"Results.txt");
	  	FileWriter outFile = new FileWriter(f, true);
	   	PrintWriter out = new PrintWriter(outFile);
	   	out.println(this.getAID() + " located " + wrd_cnt + " instances of " + textToFind + " in " + m);
	   	out.println("Relevance ratio = " + ratio);
	   	out.println("");
	   	out.close();
	   	
   }catch (Exception e){
	   System.out.println(e);
   }

   
   ACLMessage status = new ACLMessage( ACLMessage.INFORM );
   status.setContent( MainAgent.DONE );
   status.addReceiver( new AID( "host", AID.ISLOCALNAME ) );
   send( status);
   System.out.println(this.getAID() + " has completed " + file_name);
   System.out.println(wrd_cnt + " has completed " + end);
   try {
   	
   	
   	FileWriter outFile = new FileWriter(file, true);
   	PrintWriter out = new PrintWriter(outFile);
   	out.println(m);
   	out.close();
   	System.out.println("File Saved!");
   	
   }catch (IOException e) {
   	
   	e.printStackTrace();
   	
   }
}
   
   
}


