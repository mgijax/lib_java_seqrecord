package org.jax.mgi.bio.seqrecord;

import java.io.*;
import java.util.*;
import org.apache.regexp.*;

public class EMBLSeqRecord extends SeqRecord
{
	// Concept:
	//	  IS: an object that represents a EMBL-format sequence record
	//	 HAS: an organism classification - also see superclass 
	//	DOES: Implements the super class readText method to read itself
	//	 	from an input stream. Provides accessor for organism
	//	        classification. Also see super class.
	// Implementation:

	//	
	// Constructors
	//

	//	
	// Methods
	//

	public void readText(BufferedReader reader) 
		throws IOException, RESyntaxException
        {
		// Purpose: reads an EMBL-format sequence record using 'reader'
		// Returns: nothing
		// Assumes: "reader" is a stream of EMBL-format sequence records
		// Effects: "reader" has advanced to next record in the stream
		// Throws: IO,  and regular expression syntax exceptions
		// Notes:

                // the ID line broken into tokens for easy access to 
		// sequence type and length
                StringTokenizer tokenizedID;

		// the AC line broken into tokens for easy access to seqId's
		StringTokenizer tokenizedAccession;

		// the DT line broken into tokens for easy access to date
		StringTokenizer tokenizedDate;

		// for discarded tokens 
		String dummy;

		// true when SQ (sequence) line is reached. When true
                // all subsequent lines are sequence lines until EOREC '//'
		boolean flagSQ = false;  
               
		// carriage return 
		String CRT = "\n";
		
		// for stripping ';' off the end of seqIds on the AC line
		String tempId = "";	
		
		// reset all instance vars for a new record
		reset();
		
		// read current line in the reader stream
                this.line = reader.readLine();

		// ignore any header lines, we're looking for the first line 
		// of a record. Could happen midstream when input is piped to
		// stdin
		while( this.line != null && !(this.line.startsWith(this.ID)))
		{
			this.line = reader.readLine();
		}
	
		// a null line indicates EOF. If EOF or end of record we're done
		while((this.line != null) && !(this.line.startsWith(this.EOREC)))
                {
			// append line to text
			this.text.append(this.line + CRT);

			// If "line" starts with SQ:
                        // set the SQ flag which indicates the next line(s)
                        // will be sequence lines
                        if (this.line.startsWith(this.SEQUENCE))
                        {
                                flagSQ = true;
                        }

                        // If the SQ flag is set this is a sequence line
                        else if (flagSQ == true)
                        {
                                // save the sequence line
                                this.sequence.append(this.line + CRT);
                        }

                        else if(this.line.startsWith(this.ID))
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
				
				// strip off the trailing ';'
				this.type = this.type.substring(
					0, this.type.length() -1);
				// get the sequence length as an int
				this.seqLength = 
					Integer.parseInt(
						tokenizedID.nextToken());
				
				//discard the sequence type 
				dummy = tokenizedID.nextToken();
                        }
			
			// If "line" starts with "AC":
                        // Can be multiple ACCESSION lines per record
			else if (this.line.startsWith(this.ACCESSION)) 
			{
				// break AC line into tokens
				tokenizedAccession = new StringTokenizer(
							this.line);	
				// discard the AC tag field
				dummy = tokenizedAccession.nextToken();
			
				// get all the seqId's on this line
				while(tokenizedAccession.hasMoreElements())
				{
				    tempId = tokenizedAccession.nextToken();
		
				    // strip off trailing ';'
				    tempId = tempId.substring(
					0, tempId.length() - 1);

			            // load the id into the seqId array
			    	    this.seqIds.add(tempId);
				}
			}
			// If "line" starts with DT:
			// There can be multiple DT (date) lines, the last
			// one is what we want: the *last* annotation update
			else if (this.line.startsWith(this.DATE))
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
                        else if(this.ORGANISMSOURCE.match(this.line) == true)
                        {
				// save the organisms 
				this.organism.append(
					this.ORGANISMSOURCE.getParen(1));
                        }
			// If "line" starts with OC:
			// This line lists the organism classification for the 
			// first organism on the OS line 
			else if (this.ORGANISMCLASSIF.match(this.line) == true)
			{
				//save the organism classification
				this.organismClassif.append(
					this.ORGANISMCLASSIF.getParen(1));
			}
		
			// read the next line in the record
			this.line = reader.readLine();
			
		}
		// Here this.line is either null or EOREC
		// must test for null to avoid testing for EOREC when null
		if (this.line == null)
		{
			// do nothing we are at EOF
		}
		// we are at EOREC so append it to text
                else 
                { 
                	this.text.append(this.line + CRT);
                }
	}

	public String getOrganismClassif()
		// Purpose: accessor for the organism classification of this
		// sequence record 
	{
		return (this.organismClassif.toString()).toLowerCase();
	}
	
	private void reset()
		// Purpose: reinitializes instance variables
	{
		this.line = "";
		this.text.setLength(0);
		this.seqLength = -1;
		this.type = "";
		this.date = "";
                this.organism.setLength(0);
                this.sequence.setLength(0);
                this.seqIds.clear();
		this.organismClassif.setLength(0);
	}

	//
	//instance vars
	//

	// The classification (OC line) of the organism source species listed on
	// the *first* OS line
	protected StringBuffer organismClassif = new StringBuffer();

	//
	// class vars
	// 
	
	//regular expression objects for parsing EMBL-format records
	private static RE ORGANISMSOURCE;
	private static RE ORGANISMCLASSIF;
	
	//String expressions for parsing EMBL-format records
	private static String ID = "ID";
	private static String ACCESSION = "AC";
	
	private static String DATE = "DT";
	private static String SEQUENCE = "SQ";
	private static String EOREC = "//";

	// define the regular expressions
        static
        {
            try
            {
                   ORGANISMSOURCE = new RE("^OS +(.+)$");
                   ORGANISMCLASSIF = new RE("OC +(.+)$");
            }

	    // this should only happen during development - so catch here
            catch(RESyntaxException e)
            {
                System.err.println(" RESyntaxException in EMBLSeqRecord: " 
			+ e.getMessage());
            }
        }
}

