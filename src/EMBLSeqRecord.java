package org.jax.mgi.bio.seqrecord;

import java.io.*;
import java.util.*;
import org.apache.regexp.*;

public class EMBLSeqRecord extends SeqRecord
{
	// Concept:
	//	  IS: an object that represents a EMBL-format sequence record
	//	 HAS: see superclass 
	//	DOES: Implements the super class readText method to read itself
	//	 	from an input stream (parsing). Also see super class.
	// Implementation:

	//	
	// Constructors
	//

	//	
	// Methods
	//

	public void readText(BufferedReader reader) 
		throws IOException, EOFException, RESyntaxException
        {
        // Purpose: reads an EMBL-format sequence record from a file
        // Returns: nothing
        // Assumes: "reader" is a filestream of EMBL-format sequence records
        // Effects: "reader" has advanced to the next record in the stream
        // Throws: IO, EOF,  and regular expression syntax exceptions
        // Notes:

		// breaks the ID line into tokens for easy access to 
                // for easy access to sequence type and length
                StringTokenizer tokenizedID;

		// breaks the AC line into tokens
                // for easy access to seqId's
		StringTokenizer tokenizedAccession;

		// breaks the DT line into tokens
                // for easy access to date
		StringTokenizer tokenizedDate;

		// for discarded tokens 
		String dummy;

		// ID is the first line of a record
		// When the first ID line in the stream is reached set to
                // true and start appending "line" to "text".
                // This eliminates the file header.
		boolean flagID = false;  

		// When a SQ line is reached set to true. When true
                // all subsequent lines are sequence lines until the
                // "//" end of record line is reached 
		boolean flagSQ = false;  
               
		// carriage return 
		String CRT = "\n";
		
		// value of the previous line for error checking
		String prevLine = "";

		// for stripping ';' off the end of seqIds
		String tempId = "";	


		// reinit all appended instance vars for a new record
		this.text.setLength(0);
		this.organism.setLength(0);
		this.organismClassif.setLength(0);
		this.sequence.setLength(0);
		this.seqIds.clear();
		
		// The length of a seqId and molecule type so we can strip 
		// off the trailing ';'
		int fieldLen = 0;  
		
		// read current line in the reader stream
                this.line = reader.readLine();
		
		while((this.line != null) && !(this.line.startsWith(EOREC)))
		// a null line indicates end of file. If EOF or end of record
                // quit the loop; the full record has been read
                {	
                        if(this.line.startsWith(ID))
			// Parse the ID line
                        // There is only one ID line per record. There are
                        // always 6 tokens in an EMBL-format PROTEIN sequence
                        // token 0 = "ID", the tag for this line
                        // token 1 = Entry Name
                        // token 2 = Data Class
                        // token 3 = Molecule type (PRT for protein records)
                        // token 4 = sequence length
                        // token 5 = sequence type (AA (amino acid) for protein)
                        {
				// break the ID line into tokens
				tokenizedID = new StringTokenizer(this.line);

				// discard  "ID" tag
				dummy = tokenizedID.nextToken();
	
				// discard Entry Name
				dummy = tokenizedID.nextToken();
			
				//discard Data Class 
				dummy = tokenizedID.nextToken();

				// get the molecule type
				this.type = tokenizedID.nextToken();
				
				// Find out how long the molecule type is
				fieldLen = this.type.length();
				// then strip off the trailing ';'
				this.type = this.type.substring(0, fieldLen - 1);
				
				// get the sequence length
				this.seqLength = tokenizedID.nextToken();
				
				//discard the sequence type 
				dummy = tokenizedID.nextToken();
                               
				// We have found an ID line 
				flagID = true;
                        }
			
			// If "line" starts with "AC":
                        // Can be multiple ACCESSION lines per record
			else if ((this.line.startsWith(ACCESSION)) 
				&& (flagID == true))
			{
				// break this AC line into tokens
				tokenizedAccession = new StringTokenizer(
							this.line);	
				// discard the AC tag field
				dummy = tokenizedAccession.nextToken();
			
				// get all the seqId's on this line
				while(tokenizedAccession.hasMoreElements())
				{
	
			            // load the token into the seqId array
			    	    this.seqIds.add(
						tokenizedAccession.nextToken());
				    // find out the length of the seqId itself
				    // 1st convert token from Object to String
				    fieldLen = ((String)
					 (this.seqIds.lastElement())).length();	
				    
				    // strip off the trailing ';'
				    tempId = (String)this.seqIds.lastElement();
				    this.seqIds.remove(this.seqIds.size() - 1);
				    tempId = tempId.substring(0, fieldLen - 1);
				    this.seqIds.add(tempId);
				}
			}
			// If "line" starts with DT:
			// There can be multiple DT (date) lines, the last
			// one is the Last annotation update
			else if ((this.line.startsWith(DATE)) && 
						(flagID == true))
			{
				// break the DT line into tokens
				tokenizedDate = new StringTokenizer(this.line);

				// discard the DT tag field
				dummy = tokenizedDate.nextToken();
				
				// get the Date
				this.date = tokenizedDate.nextToken();
			}
			// If "line" starts with OS:
			// This line lists all the  organisms in which this
			// sequence has been found
                        else if((ORGANISMSOURCE.match(this.line) == true) && 
						(flagID == true))
                        {
				// save the organisms 
				this.organism.append(
					ORGANISMSOURCE.getParen(1));
                        }
			// If "line" starts with OC:
			// This line lists the organism classification for the 
			// first organism on the OS line 
			else if ((ORGANISMCLASSIF.match(this.line) == true) && 
						(flagID == true))
			{
				//save the organism classification
				this.organismClassif.append(
					ORGANISMCLASSIF.getParen(1));
			}
			// If "line" starts with SQ:
			// set the SQ flag which indicates the next line(s)
                        // will be sequence lines
                        else if ((this.line.startsWith(SEQUENCE)) && 
						flagID == true)
			{
				flagSQ = true;
			}
			// If the SQ flag is set this is a sequence line  
			else if (flagID == true && flagSQ == true)
			{
				// save the sequence line
				this.sequence.append(this.line);
				this.sequence.append(CRT);
			}
			// Append "line" to "text" only if first ID line has
                        // been found
			if (flagID == true)
                        {
                                this.text.append(this.line);
				this.text.append(CRT);
                        }

			// keep track of previous line for error checking
                        prevLine = this.line;

			// read the next line in the record
			this.line = reader.readLine();
			
			//Append this.line if EOREC ("//") Since it is part
                        // of the loop condition above it won't be appended
                        // otherwise
                        if(this.line.startsWith(EOREC) && flagID == true)
                        {
                                this.text.append(this.line);
				this.text.append(CRT);
                        }

			// If "line" is null we are at end-of-file. If we are at
                        // EOF and the last line was not EOREC then throw an
                        // exception
			if((this.line == null) && !(prevLine.startsWith(EOREC)))
			{
				throw new EOFException("EOF found before end " +					"of record!!");
			}
		}
	}
	// Accessor for Organism Classification
	public String getOrganismClassif()
	{
		return (this.organismClassif.toString()).toLowerCase();
	}
	
	//
	//instance vars
	//

	// The classification of the first organism source (OS) listed
	protected StringBuffer organismClassif = new StringBuffer();
	
	//regular expression objects for parsing EMBL-format records
	private static RE ORGANISMSOURCE;
	private static RE ORGANISMCLASSIF;
	
	//String expressions for parsing EMBL-format records
	private static String ID = "ID";
	private static String ACCESSION = "AC";
	
	//VERSION is only for EMBL nucleotide sequences
	//   do we need to somehow extrapolate a version for EMBL protein seqs?
	//private static String VERSION = "SV";
	private static String DATE = "DT";
	private static String SEQUENCE = "SQ";
	private static String EOREC = "//";


        static
        {
            try
            {
                   ORGANISMSOURCE = new RE("^OS +(.+)$");
                   ORGANISMCLASSIF = new RE("OC +(.+)$");
            }
            catch(RESyntaxException e)
            {
                System.out.println(" RESyntaxException in EMBLSeqRecord: " 
			+ e.getMessage());
            }
        }

}

