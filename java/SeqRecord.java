package org.jax.mgi.bio.seqrecord;

import java.io.*;
import java.util.*;

public abstract class SeqRecord
{
	// Concept:
	//	  IS: an abstract representation of a sequence record which
	//		expects its concrete subclasses to implement the
	//		readText method to read/parse specific record types
	//	 HAS: an organism, division, type (mRNA, DNA, etc),
	//            sequence, seqIds, seqIdVersion, sequence length, date
	//	DOES: reads itself from an input stream parsing out the items
	// 	      in HAS. Provides accessors for each HAS above
	// Implementation:

	//
	// Constructors
	//

	//
	// Methods
	//

	public abstract void readText(
			BufferedReader reader)  // reader for text of the seq-
						// uence record
		throws IOException, EOFException;
		// Purpose: reads itself (a sequence record) from 'reader'
		// Returns: nothing
		// Assumes: it will be implemented in all subclasses
		// Effects: nothing
		// Throws: IO, EOF,  and regular expression syntax exceptions
		// Notes:

	public String getLine()
		// Purpose: accessor for last line read of each record.
		//          Value will be either end-of-record string for
		// 	     the sequence record OR null if EOF
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
        {
                return this.sequence.toString();
        }

        public Vector getSeqIds()
		// Purpose: accessor for the sequence record seqIds
		//           returns a Vector of Strings
        {
                return this.seqIds;
        }

	public String getVersion()
		// Purpose: accessor for the sequence record seqIdVersion
		//          Returns empty string for records that do not use
		//          the convention seqId + '.' + version
	{
		return this.seqIdVersion;
	}

	public String getVersionNumber()
	{
		// Purpose: accessor for the sequence record version number
		//          Returns empty string for records that do not use
		//	    the convention seqId + '.' + version
		int index = seqIdVersion.indexOf(".");
		return seqIdVersion.substring(index + 1,
			seqIdVersion.length());
	}

	public int getSeqLength()
		// Purpose: accessor for the sequence length
	{
		return this.seqLength;
	}

	public String getDate()
		// Purpose: accessor for the sequence record date
	{
		return this.date;
	}

	public String getComment()
            // Purpose: accessor for the full COMMENT field
        {
            return this.comment.toString();

        }
	public String getCommentClass()
            // Purpose: accessor for the 'Class' field of the COMMENT" field
        {
	    return this.commentClass;
	}
	public String getCommentContact()
	    // Purpose: accessor for the 'Contact' field of the COMMENT" field
	{
	    return this.commentContact;
	}

	//
	//instance vars
	//

	// null when EOF, else end of record string
	protected String line = "";

	// The text of one sequence record
	protected StringBuffer text = new StringBuffer();

	// The length of the sequence contained in the sequence record
	protected int seqLength = -1;

	// The sequence type (e.g. mRNA)
	protected String type = "";

	// The databank division for this sequence
	protected String division = "";

	// The sequence record date
	protected String date = "";

	// A Vector of Strings representing seqIds for this record.
	// Position 0 is the primary seqId
	protected Vector seqIds = new Vector();

	// This field used only by sequence records which version with the
	// convention - seqId + '.' + version number
        protected String seqIdVersion = "";

	// The sequence record organism type(s)
        protected StringBuffer organism = new StringBuffer();

	// The sequence
        protected StringBuffer sequence = new StringBuffer();
	// The full COMMENT field (for gene traps
	protected StringBuffer comment = new StringBuffer();
 
	// the "Class" field of the COMMENT field (for gene traps)	
	protected String commentClass = "";
	
	// the "Contact" field of the COMMENT field (for gene traps)
	protected String commentContact = "";
}

