package org.jax.mgi.bio.seqrecord;

import java.io.*;
import java.util.*;
import org.apache.regexp.*;

public class FASTASeqRecord extends SeqRecord
{
	// Concept:
	//	  IS: an object that represents a FASTA-format sequence record
	//	 HAS: A description string - also see superclass 
	//	DOES: Implements the super class readText method to read itself
	//	      from an input stream.  Sets attributes for a previously
	//	      instantiated FASTASeqRecord object.  Provides accessor 
	//	      for description.  Resets attributes for a previously
	//	      instantiated FASTASeqRecord object.
	//	      Also see superclass 
	// Implementation:

	//	
	// Constructors: 
	//

        public FASTASeqRecord()
        {
	    // Purpose: Creates a default FASTASeqRecord object.
	    //          Calls reset to initialize variables.
	    // Throws: nothing
	    
	    this.reset();

        }

	//	
	// Methods: 
        //
        //

	public void readText(BufferedReader reader) 
		throws IOException, EOFException
        {
        // Purpose: reads a FASTA-format sequence record using 'reader'
        // Returns: nothing
        // Assumes: "reader" is a stream of FASTA-format sequence records
        // Effects: "reader" has advanced to the next record in the stream 
        // Throws: IO, EOF,  and regular expression syntax exceptions
        // Notes:

		// the FASTA description line broken into tokens
		// for easy access to seqId and description
		StringTokenizer tokenizedDescription;

		// for discarded tokens
		String dummy;

		// true if current line is a description line.
		boolean flagDescription = false;
	
		// carriage return
                String CRT = "\n";
		
		// values for current seqID
		String currentSeqID;
		
		// reinit all instance vars for a new record
		this.reset();
                
		// read current line in the reader stream. 
                this.line = reader.readLine();
 		
		// Debug
		this.lineCount ++;

		// a null "line" indicates EOF. If EOF we're done.	
		while(this.line != null)		
                {
			if(this.line.startsWith(DESCRIPTION)) 
			// Parse FASTA description line
			 // Can be only one description line per record. The
			 // seqID is the non-space string that appears immediately
			 // after '>'.  The description of the sequence
			 // follows the seqID and is the remainder of the line.
                        {

			   // break this description line into tokens
			   tokenizedDescription = new StringTokenizer(
				                    this.line);

         		   // get the current seqID by stripping away '>'
			   currentSeqID = tokenizedDescription.nextToken();
			   currentSeqID = currentSeqID.substring(
						1,currentSeqID.length());

        		   // set the seqID
			   this.seqIds.add(currentSeqID);

			   // get and set the description if present
         		   if (tokenizedDescription.hasMoreElements())
			   {
			      this.description = tokenizedDescription.nextToken();
			      this.description = this.line.substring(this.line.indexOf(
				        this.description),this.line.length());

			    }
			    // set the description if not present
			    else
			    {
			      // set the description
			      this.description = "";

			    }


			    // append line to text attribute
			    this.text.append(this.line + CRT);

		            // We have found a description line
			    flagDescription = true;

                        }
			
			else if (flagDescription == true)
			// Parse sequence lines
			 // if the Description flag is true append this line to
                         // "sequence". When next DESCRIPTION is found, sequence is 
		         //done
			{
			   this.sequence.append(this.line.trim());

			   // append line to text attribute
			   this.text.append(this.line + CRT);
			}
			
			// read the next line in the record
			this.line = reader.readLine();

			// Since first line of next record is the only delimiter
			// between records, reset reader to beginning of next 
			// record.
			if (this.line == null)
			// exit loop if EOF
			{
			    break;
			}
			else if (this.line.startsWith(DESCRIPTION))
			// reset the reader to latest mark and exit loop
			{
			    reader.reset();
			    break;
			} 
			else
			// mark the reder at the end of each sequence line
			{
			    reader.mark(5000);
			}
		}

		//set sequence length
		this.seqLength = sequence.length();
	}


        public void setThyself(
		String seqID,
		String description,
		String sequence)
        {
        // Purpose: sets attributes for a FASTASeqRecord object.
        // Returns: nothing
        // Assumes: nothing
        // Effects: nothing
        // Throws: nothing
        // Notes:

	    // newline character
	    String CRT = "\n";

	    // entire record
	    String entry = "";

	    // instantiate a StringBuffer in which to build the text of
	    // the entry.
	    StringBuffer sbentry = new StringBuffer();

	    // number of sequence characters per line
	    int maxLength = 70;

	    // set the seqID
	    this.seqIds.add(seqID);

	    // set the description
	    this.description = description;

	    // set the sequence
	    this.sequence = new StringBuffer(sequence);

	    // set the sequence length
	    this.seqLength = sequence.length();

	    // reconstruct the description line, add to entire record
	    entry = ">" + seqID + " " + description + CRT;

	    // Format sequence so there are maxLength characters per line.
	    if (this.seqLength > maxLength)
	    // Break up sequence if longer than maxLength.
	    {

		// number of complete sequence lines
		int div = this.seqLength / maxLength;

		// number of iterations required to reformat sequence
		int loopLimit = 0;

		// start coordinate for sequence substring
		int start = 0;

		// end coordinate for sequence substring
		int end = 0;

		// set the number of iterations required to reformat
		// the sequence.
		if ((this.seqLength % maxLength) > 0)
		{
		    loopLimit = div + 1;
		}
		else
		{
		    loopLimit = div;
		}

		// reformat the sequence by calculating the coordinates
		// for the sequence substring that will be appended to 
		// the entry.
		for (int i = 0; i < loopLimit; i++)
		{
		    // start coordinate
		    start = i*maxLength;

		    // end coordinate
		    end = (i+1)*maxLength;

		    // reset end coordinate if it extends beyond length of
		    // original sequence.
		    if (end > this.seqLength)
		    {
			end = this.seqLength;
		    }

		    // append the sequence substring to entire record
		    entry = entry + sequence.substring(start,end) + CRT;
		}
	    }
	    else
	    // append the sequence to entire record
	    {
		entry = entry + sequence + CRT;
	    }

	    // set the text attribute
	    sbentry.append(entry);
	    this.text = sbentry;

	}
	
	// Accessor for description attribute
	public String getDescription()
        {
                return this.description;
        }

	public void reset()
	// Purpose: reinitialize instance variables
	{
	    this.text.setLength(0);
	    this.seqLength = -1;
	    this.type = "";
	    this.division = "";
	    this.date = "";
	    this.seqIds.clear();
	    this.seqIdVersion = "";
	    this.organism.setLength(0);
	    this.sequence.setLength(0);
	    this.description = "";
	}

	//
	//instance vars
	//

	// The FASTA description.
	protected String description = "";
	
        //DEBUG:
        public static int lineCount = 0;

	//String expressions for parsing FASTA-format records
        private static String DESCRIPTION = ">";

}

