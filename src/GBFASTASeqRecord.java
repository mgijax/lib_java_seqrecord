package org.jax.mgi.bio.seqrecord;

import java.io.*;
import java.util.*;
import org.apache.regexp.*;

public class GBFASTASeqRecord extends FASTASeqRecord
{
	// Concept:
	//	  IS: an object that represents a GBFASTA-format sequence record
	//	 HAS: see superclass 
	//	DOES: Extends the super class readText method to parse out the
	//	      seqId from the description 
	//	      Also see superclass 
	// Implementation:

	//	
	// Constructors: 
	//

        public GBFASTASeqRecord()
        {
	    // Purpose: Creates a default GBFASTASeqRecord object.
	    // Throws: nothing
	    
		super();

        }

	//	
	// Methods: 
        //
        //

	public void readText(BufferedReader reader) 
		throws IOException, EOFException
        {
        // Purpose: reads a FASTA-format sequence record extending the super
	//          class method by parsing the GenBank seqId out of the Genbank
	//          FASTA 'unique identifier'
        // Returns: nothing
        // Assumes: "reader" is a stream of FASTA-format sequence records
        // Effects: "reader" has advanced to the next record in the stream 
        // Throws: IO, EOF,  and regular expression syntax exceptions

		// have the superclass method do its work
		super.readText(reader);

		// for discarded tokens
		String dummy;

		// carriage return
                String CRT = "\n";
		
		// tokenized FASTA Id - a FASTA Id is a unique identifier that
		// contains a GenBank SeqId which we must parse out e.g.
		// this.seqIds.get(0) has the following format
		// gi|3287367|gb|AC002397.1|AC002397
		// we must parse out the last token tokenized on '|'
		StringTokenizer tokenizedFASTAId = new StringTokenizer(
                        (String)(this.seqIds.get(0)), "|");
		
		// count of total tokens in tokenizedFASTAId
		int count = tokenizedFASTAId.countTokens();
	
		// to extract the seqId from this.seqIdVersion
		StringTokenizer tokenizedSeqId;
		
		// the last token is the seqId
		for(int i = 1; i < count ; i++)
		{
			dummy = tokenizedFASTAId.nextToken();
		}
		this.seqIds.set(0, tokenizedFASTAId.nextToken());

	}

}

