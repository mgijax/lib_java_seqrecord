package org.jax.mgi.bio.seqrecord;

import java.io.*;
import java.util.*;
import org.apache.regexp.*;

public class FASTASeqRecord extends SeqRecord
{
	// Concept:
	//	  IS: an object that represents a FASTA-format sequence record
	//	 HAS: A description string. See superclass 
	//	DOES: reads itself from an input stream (parsing) and
        //            provides accessor for description.
	//	      Also see superclass 
	// Implementation:
        //
	//	
	// Constructors: -) See SeqRecord
	//
	//	
	// Methods: -) readText - reads in a FASTA-format sequence
	//               record and sets attributes of a previously-
	//               instantiated FASTASeqRecord object.
	//	    -) setThyself - sets attributes for a previously-
	//	         instantiated FASTASeqRecord object by using
	//	         parameters passed to method.
        //
        //

	public void readText(BufferedReader reader) 
		throws IOException, EOFException, RESyntaxException
        {
        // Purpose: reads a FASTA-format sequence record from a file
        // Returns: nothing
        // Assumes: "reader" is a filestream of FASTA-format sequence records
        // Effects: "reader" has advanced to the next record in the stream 
        // Throws: IO, EOF,  and regular expression syntax exceptions
        // Notes:

		// breaks the FASTA description line into tokens
		// for easy access to seqId and description
		StringTokenizer tokenizedDescription;

		// for unneeded tokens when parsing tokens
		String dummy;

		// The description line is the first line in a
		// FASTA-format sequence record.  This flag
		// denotes whether it has been found.
		boolean flagDescription = false;
	
		// carriage return
                String CRT = "\n";
		
		// value of the previous line for error checking
		String prevLine = "";

		// values for seqID and description
		String seqID;
		String desc;
		
		// reinit all appended instance vars for a new record
		this.organism.setLength(0);  // Remove????
		this.text.setLength(0);
		this.sequence.setLength(0);
		this.seqIds.clear();
		this.description = "";
                
		// read current line in the reader stream. 
                this.line = reader.readLine();
 		
		// Debug
		this.lineCount ++;
	
		while(this.line != null)
		// a null "line" indicates end of file.
                {
			if(this.line.startsWith(EOREC)) 
			// Parse FASTA description line
                        // Can be only one description line/record. The
			// seqID is the string that appears immediately
			// after '>'.  The description of the sequence
			// follows the seqID.
                        {

			   // break this description line into tokens
			   tokenizedDescription = new StringTokenizer(
				                    this.line);

         		   // get the seqID
			   seqID = tokenizedDescription.nextToken();
			   seqID = seqID.substring(1,seqID.length());

        		   // set the seqID
			   this.seqIds.add(seqID);

			   // get and set the description if present
         		   if (tokenizedDescription.hasMoreElements())
			   {
			      desc = tokenizedDescription.nextToken();
			      desc = this.line.substring(this.line.indexOf(
				        desc),this.line.length());

			    }
			    // set the description if not present
			    else
			    {
			      // set the description
			      desc = "";

			    }

			    // set the description attribute
			    this.description = desc;

		            // We have found a description line
			    flagDescription = true;

                        }
			
			else if (flagDescription == true)
			// if the Description flag is set append this line to
                        // "sequence". When next EOREC is found, sequence is 
		        //done
			{
			   this.sequence.append(this.line.trim());
			}

			if (flagDescription == true)
			// Append "line" to "text" only if first description
			// line has been found.
                        {
			   // Append description line to text
                           this.text.append(this.line);
			   this.text.append(CRT);
                        }
			
			// keep track of previous line for error checking
			prevLine = this.line;

			// read the next line in the record
			this.line = reader.readLine();
				
			//For debugging
			this.lineCount++;
			
			// Append this.line if EOREC (">") Since it is part
			// of the loop condition above it won't be appended 
			// otherwise
			if (this.line == null)
			{
			    break;
			}
			else if (this.line.startsWith(EOREC))
			{
			    // reset reader to latest mark
			    reader.reset();
			    break;
			} 
			else
			{
			    // set mark
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

	    // initialize string that represents entire record
	    String entry = "";

	    // instantiate a StringBuffer in which to build the text of
	    // the entry.
	    StringBuffer sbentry = new StringBuffer();

	    // number of sequence characters per line
	    int maxLength = 70;

	    // set the seqID attribut
	    this.seqIds.add(seqID);

	    // set the description attribute
	    this.description = description;

	    // set the sequence attribute
	    StringBuffer sb = new StringBuffer();
	    sb.append(sequence);
	    this.sequence = sb;

	    // set the sequence length attribute
	    int seqLength = sequence.length();
	    this.seqLength = seqLength;

	    // build the description line
	    entry = ">" + seqID + " " + description + CRT;

	    // format sequence
	    if (seqLength > maxLength)
	    {
	    // if the sequence is long enough to break up

		// calculate the number of complete sequence lines
		int div = seqLength / maxLength;

		// calculate whether the last sequence line will not
		// have maxLength number of characters
		int modulus = seqLength % maxLength;

		// initiaize some variables
		int loopLimit = 0;
		int start = 0;
		int end = 0;

		// set the number of iterations required to reformat
		// the sequence.
		if (modulus > 0)
		{
		    loopLimit = div + 1;
		}
		else
		{
		    loopLimit = div;
		}

		// reformat the sequence by calculating the coordinates
		// for the sequence that will be appended to the entry.
		for (int i = 0; i < loopLimit; i++)
		{
		    // start coordinate
		    start = i*maxLength;

		    // end coordinate
		    end = (i+1)*maxLength;

		    // case for last line of subsequence
		    if (end > seqLength)
		    {
			end = seqLength;
		    }

		    // append the sequence
		    entry = entry + sequence.substring(start,end) + CRT;
		}
	    }
	    else
	    {
		entry = sequence + CRT;
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

	//
	//instance vars
	//

	// The FASTA description.
	protected String description = "";
	
        //DEBUG:
        public static int lineCount = 0;

	//String expressions for parsing FASTA-format records
        private static String EOREC = ">";

}

