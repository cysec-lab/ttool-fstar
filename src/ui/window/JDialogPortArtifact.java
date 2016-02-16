/**Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
 *
 * ludovic.apvrille AT enst.fr
 *
 * This software is a computer program whose purpose is to allow the
 * edition of TURTLE analysis, design and deployment diagrams, to
 * allow the generation of RT-LOTOS or Java code from this diagram,
 * and at last to allow the analysis of formal validation traces
 * obtained from external tools, e.g. RTL from LAAS-CNRS and CADP
 * from INRIA Rhone-Alpes.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 *
 * /**
 * Class JDialogTMLTaskArtifact
 * Dialog for managing artifacts on hw nodes
 * Creation: 19/09/2007
 * @version 1.0 19/09/2007
 * @author Ludovic APVRILLE
 * @see
 */

package ui.window;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

import ui.*;
import ui.tmldd.*;
import tmltranslator.ctranslator.*;

import myutil.*;


public class JDialogPortArtifact extends javax.swing.JDialog implements ActionListener  {
    
	private boolean regularClose;
	private boolean emptyList = false;
    
  private JPanel panel2;
  private Frame frame;
  private TMLArchiPortArtifact artifact;
  private String mappedMemory = "VOID"; 

	protected JComboBox referenceCommunicationName, priority, memoryCB;
	protected JTextField baseAddressTF, numSamplesTF, bitsPerSymbolTF;
	protected String baseAddress, mappedPort, sampleLength, numSamples, bitsPerSymbol;
	protected String bank, dataType, symmetricalValue;
	protected JComboBox dataTypeCB, bankCB, symmetricalValueCB;

	//Intl Data In
	protected JTextField widthIntl_TF, bitInOffsetIntl_TF, inputOffsetIntl_TF;
	protected String widthIntl, bitInOffsetIntl, inputOffsetIntl, packedBinaryInIntl;
	protected JComboBox packedBinaryInIntl_CB;

	//Intl Data Out
	protected JTextField bitOutOffsetIntl_TF, outputOffsetIntl_TF;
	protected JComboBox packedBinaryOutIntl_CB;
	protected String packedBinaryOutIntl, bitOutOffsetIntl, outputOffsetIntl;

	//Intl Perm
	protected JTextField lengthPermIntl_TF, offsetPermIntl_TF;
	protected String lengthPermIntl, offsetPermIntl;

	//Mapper Data In
	protected JTextField baseAddressDataInMapp_TF, numSamplesDataInMapp_TF, bitsPerSymbolDataInMapp_TF;
	protected String baseAddressDataInMapp, numSamplesDataInMapp, bitsPerSymbolDataInMapp, symmetricalValueDataInMapp;
	protected JComboBox symmetricalValueDataInMapp_CB;
	//Mapper Data Out
	protected JTextField baseAddressDataOutMapp_TF;
	protected String baseAddressDataOutMapp;
	//Mapper LUT
	protected JTextField baseAddressLUTMapp_TF;
	protected String baseAddressLUTMapp;
	
  // Main Panel
  private JButton closeButton;
  private JButton cancelButton;

	//Code generation
	private JPanel panel3, panel4, panel5;
	private JTabbedPane tabbedPane;
	private int bufferType = 0;
	private ArrayList<String> bufferParameters;
	private String appName = "";
    
    /** Creates new form  */
    public JDialogPortArtifact(Frame _frame, String _title, TMLArchiPortArtifact _artifact, String _mappedMemory, ArrayList<String> _bufferParameters, String _mappedPort ) {
			super(_frame, _title, true);
			frame = _frame;
			artifact = _artifact;
			mappedMemory = _mappedMemory;
			bufferParameters = _bufferParameters;
			mappedPort = _mappedPort;
			appName = mappedPort.split("::")[0];

			TraceManager.addDev("init components");

			initComponents();

			TraceManager.addDev("my init components");

			myInitComponents();

			TraceManager.addDev("pack");
			pack();
    }
    
    private void myInitComponents() {
			selectPriority();
    }
    
    private void initComponents() {
        Container c = getContentPane();
        GridBagLayout gridbag0 = new GridBagLayout();
        GridBagLayout gridbag1 = new GridBagLayout();
        GridBagLayout gridbag2 = new GridBagLayout();
        GridBagConstraints c0 = new GridBagConstraints();
        GridBagConstraints c1 = new GridBagConstraints();
        GridBagConstraints c2 = new GridBagConstraints();
        
        setFont(new Font("Helvetica", Font.PLAIN, 14));
        c.setLayout(gridbag0);
        
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        panel2 = new JPanel();
        panel2.setLayout(gridbag2);
        panel2.setBorder(new javax.swing.border.TitledBorder("Artifact attributes"));
        panel2.setPreferredSize(new Dimension(650, 350));

        panel3 = new JPanel();
        panel3.setLayout(gridbag2);
        panel3.setBorder(new javax.swing.border.TitledBorder("Code generation: memory configuration"));
        panel3.setPreferredSize(new Dimension(650, 350));
				
				tabbedPane = new JTabbedPane();
		  	panel4 = new JPanel();
  			panel5 = new JPanel();
        
		c1.gridwidth = 1;
        c1.gridheight = 1;
        c1.weighty = 1.0;
        c1.weightx = 1.0;
        c1.fill = GridBagConstraints.HORIZONTAL;
        panel2.add(new JLabel("Port:"), c2);
        c1.gridwidth = GridBagConstraints.REMAINDER; //end row
		TraceManager.addDev("Getting communications");
		Vector<String> list = artifact.getTDiagramPanel().getMGUI().getAllTMLCommunicationNames();

		Vector<String> portsList = new Vector<String>();
		int index = 0;
		if (list.size() == 0) {
			list.add("No communication to map");
			emptyList = true;
		} else {
			
			index = 0;//indexOf(list, artifact.getFullValue());
			//parse each entry of list. Entry is in format AppName::chIn__chOut
			for( String s: list )	{
				if( s.contains( appName ) )	{	//build the DS for the mapped applications (filter out the case of multiple applications)
					TraceManager.addDev( "Parsing: " + s );
					String[] temp1 = s.split("__");
					String[] temp2 = temp1[0].split( "::" );
					String chOut = temp2[0] + "::" + temp1[1];
					String chIn = temp2[0] + "::" + temp2[1];
					if( !portsList.contains( chOut ) )	{
						portsList.add( chOut );
					}
					if( !portsList.contains( chIn ) )	{
						portsList.add( chIn );
					}
				}
			}
		}
		
		TraceManager.addDev("Got communications");

    referenceCommunicationName = new JComboBox(portsList);
		if( mappedPort.equals( "VOID" ) || mappedPort.equals( "" ) )	{
			referenceCommunicationName.setSelectedIndex( 0 );
		}
		else	{
			referenceCommunicationName.setSelectedIndex( portsList.indexOf( mappedPort ) );
		}
		referenceCommunicationName.addActionListener(this);
		panel2.add(referenceCommunicationName, c1);
		
		list = new Vector<String>();
		for(int i=0; i<11; i++) {
			list.add(""+i);
		}
		priority = new JComboBox(list);
		priority.setSelectedIndex(artifact.getPriority());
		panel2.add( new JLabel( "Priority: "),  c2 );
		panel2.add(priority, c1);
		
		//Make the list of memories
		LinkedList componentList = artifact.getTDiagramPanel().getComponentList();
		Vector<String> memoryList = new Vector<String>();
		for( int k = 0; k < componentList.size(); k++ )	{
			if( componentList.get(k) instanceof TMLArchiMemoryNode )	{
				memoryList.add( ( (TMLArchiMemoryNode) componentList.get(k) ).getName() );
			}
		}

		memoryCB = new JComboBox( memoryList );
		if( mappedMemory.equals( "VOID" ) || mappedMemory.equals( "" ) )	{
			memoryCB.setSelectedIndex( 0 );
		}
		else	{
			memoryCB.setSelectedIndex( memoryList.indexOf( mappedMemory ) );
		}
		panel2.add( new JLabel( "Memory: "),  c2 );
		memoryCB.addActionListener(this);
		panel2.add( memoryCB, c1 );

		/*if( bufferParameters.size() == 0 )	{
			bufferType = getBufferTypeFromSelectedMemory( (String)memoryCB.getItemAt( memoryCB.getSelectedIndex() ) );
			TraceManager.addDev( "From if branch, the buffer type is: " + bufferType );
		}
		else	{*/
			bufferType = Integer.parseInt( bufferParameters.get( Buffer.bufferTypeIndex ) );
		//}

		ArrayList<JPanel> panelsList;

		switch( bufferType )	{
			case Buffer.FepBuffer:	
				panelsList = FepBuffer.makePanel( c1, c2 );
				panel3 = panelsList.get(0);
				break;
			case Buffer.InterleaverBuffer:	
				panelsList = InterleaverBuffer.makePanel( c1, c2 );
				tabbedPane.addTab( "Data In", panelsList.get(0) );
				tabbedPane.addTab( "Data Out", panelsList.get(1) );
				tabbedPane.addTab( "Permutation Table", panelsList.get(2) );
				tabbedPane.setSelectedIndex(0);
				break;
			case Buffer.AdaifBuffer:	
				panelsList = AdaifBuffer.makePanel( c1, c2 );
				panel3 = panelsList.get(0);
				break;
			case Buffer.MapperBuffer:	
				tabbedPane.removeAll();
				panelsList = MapperBuffer.makePanel( c1, c2 );
				tabbedPane.addTab( "Data In", panelsList.get(0) );
				tabbedPane.addTab( "Data Out", panelsList.get(1) );
				tabbedPane.addTab( "Look Up Table", panelsList.get(2) );
				tabbedPane.setSelectedIndex(0);
				break;
			case Buffer.MainMemoryBuffer:	
				panelsList = MMBuffer.makePanel( c1, c2 );
				panel3 = panelsList.get(0);
				break;
			default:	//the fep buffer 
				panelsList = FepBuffer.makePanel( c1, c2 );
				panel3 = panelsList.get(0);
				break;
		}

		// main panel;
		c0.gridheight = 10;
		c0.weighty = 1.0;
		c0.weightx = 1.0;
		c0.gridwidth = GridBagConstraints.REMAINDER; //end row
		c0.fill = GridBagConstraints.BOTH;
		c.add( panel2, c0 );
		if( ( bufferType == Buffer.MainMemoryBuffer ) || ( bufferType == Buffer.FepBuffer ) || ( bufferType == Buffer.AdaifBuffer) )	{
      panel3.setBorder(new javax.swing.border.TitledBorder("Code generation: memory configuration"));
			tabbedPane.removeAll();
			tabbedPane.addTab( "Data", panel3 );
			tabbedPane.setSelectedIndex(0);
		}
		c.add( tabbedPane, c0 );

		c0.gridwidth = 1;
		c0.gridheight = 1;
		c0.fill = GridBagConstraints.HORIZONTAL;
		closeButton = new JButton("Save and Close", IconManager.imgic25);
		//closeButton.setPreferredSize(new Dimension(600, 50));
		closeButton.addActionListener(this);
		c.add(closeButton, c0);
		c0.gridwidth = GridBagConstraints.REMAINDER; //end row
		cancelButton = new JButton("Cancel", IconManager.imgic27);
		cancelButton.addActionListener(this);
		c.add(cancelButton, c0);
  }

	private int getBufferTypeFromSelectedMemory( String mappedMemory )	{
		
		LinkedList componentList = artifact.getTDiagramPanel().getComponentList();
		Vector<String> list = new Vector<String>();
		
		for( int k = 0; k < componentList.size(); k++ )	{
			if( componentList.get(k) instanceof TMLArchiMemoryNode )	{
				TMLArchiMemoryNode memoryNode = (TMLArchiMemoryNode)componentList.get(k);
				if( memoryNode.getName().equals( mappedMemory ) )	{
					return memoryNode.getBufferType();
				}
			}
		}
		return 0;	//default: the main memory buffer
	}
    
  public void	actionPerformed(ActionEvent evt)  {

		if (evt.getSource() == referenceCommunicationName) {
			selectPriority();
		}
		if( evt.getSource() == memoryCB )	{
			updateBufferPanel();
		}
        
    String command = evt.getActionCommand();
    // Compare the action command to the known actions.
   	if (command.equals("Save and Close"))  {
    	closeDialog();
    } else if (command.equals("Cancel")) {
    	cancelDialog();
		}
	}

	private void updateBufferPanel()	{

		GridBagConstraints c1 = new GridBagConstraints();
		GridBagConstraints c2 = new GridBagConstraints();

		c1.gridwidth = 1;
		c1.gridheight = 1;
		c1.weighty = 1.0;
		c1.weightx = 1.0;
		c1.fill = GridBagConstraints.HORIZONTAL;
    c1.gridwidth = GridBagConstraints.REMAINDER; //end row
		
		//flushBuffersStrings();
		bufferType = getBufferTypeFromSelectedMemory( (String)memoryCB.getItemAt( memoryCB.getSelectedIndex() ) );
		ArrayList<JPanel> panelsList;

		switch( bufferType )	{
			case Buffer.FepBuffer:	
				tabbedPane.removeAll();
				panelsList = FepBuffer.makePanel( c1, c2 );
				panel3 = panelsList.get(0);
				tabbedPane.addTab( "Data", panel3 );
				break;
			case Buffer.MapperBuffer:	
				tabbedPane.removeAll();
				panelsList = MapperBuffer.makePanel( c1, c2 );
				tabbedPane.addTab( "Data In", panelsList.get(0) );
				tabbedPane.addTab( "Data Out", panelsList.get(1) );
				tabbedPane.addTab( "Look Up Table", panelsList.get(2) );
				tabbedPane.setSelectedIndex(0);
				break;
			case Buffer.AdaifBuffer:	
				tabbedPane.removeAll();
				panelsList = AdaifBuffer.makePanel( c1, c2 );
				panel3 = panelsList.get(0);
				tabbedPane.addTab( "Data", panel3 );
				break;
			case Buffer.InterleaverBuffer:
				tabbedPane.removeAll();
				panelsList = InterleaverBuffer.makePanel( c1, c2 );
				tabbedPane.addTab( "Data In", panelsList.get(0) );
				tabbedPane.addTab( "Data Out", panelsList.get(1) );
				tabbedPane.addTab( "Permutation Table", panelsList.get(2) );
				tabbedPane.setSelectedIndex(0);
				break;
			case Buffer.MainMemoryBuffer:	
				tabbedPane.removeAll();
				panelsList = MMBuffer.makePanel( c1, c2 );
				panel3 = panelsList.get(0);
				tabbedPane.addTab( "Data", panel3 );
				break;
			default:	//the main memory buffer 
				tabbedPane.removeAll();
				panelsList = FepBuffer.makePanel( c1, c2 );
				panel3 = panelsList.get(0);
				tabbedPane.addTab( "Data", panel3 );
				break;
		}
	}

	public void selectPriority() {
		//System.out.println("Select priority");
		int index = ((TMLArchiDiagramPanel)artifact.getTDiagramPanel()).getMaxPriority((String)(referenceCommunicationName.getSelectedItem()));
		priority.setSelectedIndex(index);
	}
    
    public void closeDialog() {

        regularClose = true;
				mappedMemory = (String) memoryCB.getItemAt( memoryCB.getSelectedIndex() );
				bufferType = getBufferTypeFromSelectedMemory( (String)memoryCB.getItemAt( memoryCB.getSelectedIndex() ) );
				switch ( bufferType )	{
					case Buffer.FepBuffer:
						if( !FepBuffer.closePanel( frame ) )	{
							return;
						}
						break;
					case Buffer.MapperBuffer:	
						if( !MapperBuffer.closePanel( frame ) )	{
							return;
						}
						break;
					case Buffer.AdaifBuffer:	
						if( !AdaifBuffer.closePanel( frame ) )	{
							return;
						}
						break;
					case Buffer.InterleaverBuffer:	
						if( !InterleaverBuffer.closePanel( frame ) )	{
							return;
						}
						break;
					case Buffer.MainMemoryBuffer:	
						if( !MMBuffer.closePanel( frame ) )	{
							return;
						}
						break;
					default:	//the main memory buffer 
						if( !FepBuffer.closePanel( frame ) )	{
							return;
						}
						break;
				}
        dispose();
    }

		public String getMappedPort()	{
			return mappedPort;
		}

		public String getMappedMemory()	{
			return mappedMemory;
		}

		public String getStartAddress()	{
			return baseAddress;
		}

    public void cancelDialog() {
        dispose();
    }
    
    public boolean isRegularClose() {
        return regularClose;
    }
	
	public String getReferenceCommunicationName() {
		if (emptyList) {
			return null;
		}
		String tmp = (String)(referenceCommunicationName.getSelectedItem());
		int index = tmp.indexOf("::");
		if (index == -1) {
			return tmp;
		}
        return tmp.substring(0, index);
    }
    
    public String getCommunicationName() {
        String tmp = (String)(referenceCommunicationName.getSelectedItem());
		int index = tmp.indexOf("::");
		if (index == -1) {
			return tmp;
		}
        tmp = tmp.substring(index+2, tmp.length());
		
		index =  tmp.indexOf("(");
		if (index > -1) {
			tmp = tmp.substring(0, index).trim();
		}
		return tmp;
    }
	
	 public String getTypeName() {
		String tmp = (String)(referenceCommunicationName.getSelectedItem());
		int index1 = tmp.indexOf("(");
		int index2 = tmp.indexOf(")");
		if ((index1 > -1) && (index2 > index1)) {
			return tmp.substring(index1+1, index2);
		}
		return "";
	 }
	
	public int indexOf(Vector<String> _list, String name) {
		int i = 0;
		for(String s : _list) {
			if (s.equals(name)) {
				return i;
			}
			i++;
		}
		return 0;
	}
	
	public int getPriority() {
		return priority.getSelectedIndex();
	}

	public ArrayList<String> getBufferParameters()	{

		ArrayList<String> params = new ArrayList<String>();
		params.add( String.valueOf( bufferType ) );
		switch( bufferType )	{
			case Buffer.FepBuffer:
				params = FepBuffer.getBufferParameters();
				break;
			case Buffer.InterleaverBuffer:	
				params = InterleaverBuffer.getBufferParameters();
				break;
			case Buffer.AdaifBuffer:
				params = AdaifBuffer.getBufferParameters();
				break;
			case Buffer.MapperBuffer:	
				params = MapperBuffer.getBufferParameters();
				break;
			case Buffer.MainMemoryBuffer:	
				params = MMBuffer.getBufferParameters();
				break;
			default:	//the main memory buffer
				params = FepBuffer.getBufferParameters();
				break;
		}
		return params;
	}

	private void cleanPanels()	{
		panel3.removeAll();
		panel4.removeAll();
		panel5.removeAll();
		tabbedPane.removeAll();
	}

	private void revalidateAndRepaintPanels()	{
		panel3.revalidate();
		panel3.repaint();
		panel4.revalidate();
		panel4.repaint();
		panel5.revalidate();
		panel5.repaint();
	}

}	//End of class
