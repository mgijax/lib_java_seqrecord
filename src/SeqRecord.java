package org.jax.mgi.bio.seqrecord;

import java.io.*;
import java.util.*;
import org.apache.regexp.*;

public abstract class SeqRecord
{
	// Concept:
	//	  IS: an abstract representation of a sequence record which
	//		expects its concrete subclasses to implement the
	//		readText method to read/parse specific record types
	//	 HAS: an organism, division, type (mRNA, DNA, etc), 
	//            sequence, seqIds, version, sequence length, date
	//	DOES: reads itself from an input stream (parsing) and
	//	      provides accessors for each HAS above.
	// Implementation:
	
	//
	// Constructors
	//

	//
	// Methods
	//

	public abstract void readText(BufferedReader reader) 
		throws IOException, EOFException, RESyntaxException;
	// Purpose: reads itself (a sequence record) from a file
        // Returns: nothing
        // Assumes: it will be implemented in all subclasses
        // Effects: nothing
        // Throws: IO, EOF,  and regular expression syntax exceptions
        // Notes:

	public String getLine()
	// Purpose: accessor for last line of each record. This is the mechanism
	//		for determining end of file
	{
		return this.line;
	}
	
	public String getText()
        // Purpose: accessor for the text of the whole sequence record
        {
                return this.text.toString();
        }

        public String getOrganism()
        // Purpose: accessor for the sequence record organism 
        {
		return (this.organism.toString()).toLowerCase();
        }

	 public String getType()
        // Purpose: accessor for the sequence record type
        {
                return this.type;
        }

        public String getDivision()
        // Purpose: accessor for the sequence record division
        {
                return this.division;
        }

        public String getSequence()
        // Purpose: accessor for the sequence record sequence
	//		converted to a string
        {
                return this.sequence.toString();
        }

        public Vector getSeqIds()
        // Purpose: accessor for the sequence record seqIds
        {
                return this.seqIds;
        }
	
	public String getVersion()
	// Purpose: accessor for the sequence record version
	{
		return this.version;
	}
	
	public String getVersionNumber()
	{
	// Purpose: accessor for the sequence record version number alone
		return "Not implemented Yet"; 
	}

	public String getSeqLength()
	// Purpose: accessor for the sequence length
	{
		return this.seqLength;
	}

	public String getDate()
	// Purpose: accessor for the sequence record date
	{
		return this.date;
	}
	
	//DEBUG
	public int lineCount = 0;
	
	//
	//instance vars
	//
	
	// The current line of a sequence record used to test last line
	// in a record for null which is end-of-file indicator
	protected String line = "";

	// A sequence record
	protected StringBuffer text = new StringBuffer();

	// The sequence length
	protected String seqLength = "";

	// The sequence type (e.g. mRNA)
	protected String type = "";

	// The databank division for this sequence
	protected String division = "";

	// The sequence record date
	protected String date = "";

	// The sequence record seqIds. Position 0 is the primary seqId
	protected Vector seqIds = new Vector();	

	// The seqId + '.' + version number
        protected String version = "";

	// The sequence record organism type(s) 
        protected StringBuffer organism = new StringBuffer();

	// The sequence
        protected StringBuffer sequence = new StringBuffer();
}

