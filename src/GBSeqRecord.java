package org.jax.mgi.bio.seqrecord;

import java.io.*;
import java.util.*;
import org.apache.regexp.*;

public class GBSeqRecord extends SeqRecord
{
	// Concept:
	//	  IS: an object that represents a Genbank-format sequence record
	//	 HAS: A GenInfo Identifier (GI) - also see superclass 
	//	DOES: Implements the super class readText method to read itself 
	//	      from an input stream. Provides accessor for GI Id - 
	//	      Also see superclass 
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
        // Purpose: reads a Genbank-format sequence record using 'reader'
        // Returns: nothing
        // Assumes: "reader" is a stream of Genbank-format sequence records
        // Effects: "reader" has advanced to the next record in the stream 
        // Throws: IO and regular expression syntax exceptions
        // Notes:

		// the Accession line broken into tokens for easy access
		// seqId's
		StringTokenizer tokenizedAccession;

		// the VERSION line broken into tokens for easy access to
		// version numbers and GenInfoId 
		StringTokenizer tokenizedVersion;

		// for discarded tokens
		String dummy;
	
		// true if current line is an  ACCESSION line. false when 
		// VERSION line has been reached
		boolean flagAccession = false;

		// true if current line is an ORGANISM line. false when 
		// REFERENCE line has been reached
                boolean flagOrganism = false;

		// true if current line is an ORIGIN line
		// all subsequent lines are ORIGIN lines until EOREC 
		boolean flagOrigin = false;

		// carriage return
                String CRT = "\n";
		
		// reset all instance vars for a new record
                reset();

		// read current line in the reader stream. 
                this.line = reader.readLine();
 		
		// ignore any header lines, we're looking for the first line
                // of a record. Could happen midstream when input is piped to
                // stdin
                while( this.line != null && !(this.line.startsWith(this.LOCUS)))
                {
                        this.line = reader.readLine();
                }

		// a null line indicates EOF. If EOF or end of record we're done
		while(this.line != null && !(this.line.startsWith(EOREC)))
                {
			// append line to text
                        this.text.append(this.line + CRT);

			if ((this.line.startsWith(ORIGIN)))
                        // If line starts with ORIGIN, set the origin flag which
                        // indicates the next line(s) will be sequence lines
                        {
                                flagOrigin = true;
                        }

                        else if (flagOrigin == true)
                        // if the Origin flag is set append this line to
                        // "sequence". When EOREC is found, sequence is done
                        {
                                this.sequence.append(this.line + CRT);
                        }

			else if(this.line.startsWith(LOCUS)) 
			// Parse LOCUS line
                        // Can be only one LOCUS line per record. Pieces of
                        // info in the LOCUS line are always found in fixed pos
                        // With columns starting at 1:
                        // 23 - 29 = Length of sequence right justified
                        // 37 - 41 = type (blank | DNA | RNA | tRNA | rRNA |
                        //               mRNA | uRNA | snRNA | scRNA)
                        // 53 - 55 = The division code (see Section 3.3 in
                        //               NCBI-Genbank Flat File Release 123.0)
                        // 63 - 73 = Date in the form dd-MMM-yyy
                        //              (e.g. 15-MAR-1991)
                        {
				this.seqLength = 
					Integer.parseInt(
					  (this.line.substring(22, 29)).trim());
				
				// get the sequence type
				this.type = 
					(this.line.substring(36, 41)).trim();
	
				// get the Genbank division code
				this.division = this.line.substring(52, 55);

				// get the date
				this.date = this.line.substring(62, 73);
                        }
			
			else if ((this.line.startsWith(ACCESSION)))
			// Parse the ACCESSION line
                        // Can be multiple ACCESSION lines/record which end
                        //      when VERSION line is reached
                        // Accession numbers can be six or eight character
                        //      with a blank between each
                        // First Accession number on first line is the primary
                        //      accession number, remaining ID's are secondary
			{
				// break this ACCESSION line into tokens
				tokenizedAccession = new StringTokenizer(
						this.line);
				
				// discard the ACCESSION tag field
				dummy = tokenizedAccession.nextToken();
				
				// remaining tokens are seqIds, save them
				while(tokenizedAccession.hasMoreElements())
                                {
                                        this.seqIds.add(
						tokenizedAccession.nextToken());
                                }
				// We have found an ACCESSION line
				flagAccession = true;
				
			}

			else if ((this.line.startsWith(VERSION))) 
			// Parse the VERSION line
                        // The VERSION line indicates the end of the ACCESSION
                        //      lines
                        // The VERSION line contains two identifiers:
                        // 1) The PrimaryAccession.versionNumber
                        // 2) The NCBI GI identifier
			{
				// break this VERSION line into tokens
				tokenizedVersion = new StringTokenizer(
						this.line);
				
				// discard the VERSION tag field
				dummy = tokenizedVersion.nextToken();

				// get the version id
				this.seqIdVersion = tokenizedVersion.nextToken();

				// get the genbank info id
				this.genInfoId = tokenizedVersion.nextToken();
	
				// We have found the VERSION line indicating
				// end of ACCESSION lines for this record
				flagAccession = false;
				
			}

			else if(flagAccession == true)
			// we have the first ACCESSION line but haven't reached
                        //      VERSION line yet so we have multiple ACCESSION
                        //      lines. Save all these Ids
                        {
                                tokenizedAccession = 
					new StringTokenizer(this.line);
                                while(tokenizedAccession.hasMoreElements())
                                {
                                        this.seqIds.add(
						tokenizedAccession.nextToken());
                                }

                        }

                        else if((ORGANISM.match(this.line) == true)) 
			// we have found the ORGANISM line save it
                        // ORGANISM is a sub-keyword of SOURCE and indented
                        //      use regexp to find it
                        {
                                this.organism.append(ORGANISM.getParen(1));

                                // We have found the ORGANISM line for this rcd
				flagOrganism = true;

                        }

			else if((this.line.startsWith(REFERENCE)))
			// line starts with REFERENCE we are at the end
                        // of ORGANISM lines. Set the organism flag to false
                        {
				flagOrganism = false;
                        }

                        else if (flagOrganism == true)
			// If we have found the ORGANISM line, but haven't found
                        //    the REFERENCE line, append the taxonomic
                        //    classification level lines that follow the
                        //    ORGANISM line to "organism"
                        {
                                this.organism.append(this.line);
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
	
	// Accessor for GI Id
	public String getGenInfoId()
        {
                return this.genInfoId;
        }

	private void reset()
                // Purpose: reinitializes instance variables
        {
		this.line = "";
		this.text.setLength(0);
		this.seqLength = -1;
                this.type = "";
		this.division = "";
                this.date = "";
		this.seqIds.clear();
		this.seqIdVersion = "";
                this.organism.setLength(0);
                this.sequence.setLength(0);
		this.genInfoId = "";
		
        }

	//
	//instance vars
	//

	// The NCBI GenInfo Identifier sequence identification number. This id
	// runs parallel to and is superseded by the accession.version system 
	// of sequence id's
	protected String genInfoId = "";
	
	// String expressions for parsing Genbank-format records
        private static String LOCUS = "LOCUS";
        private static String ACCESSION = "ACCESSION";
	private static String VERSION = "VERSION";
        private static String REFERENCE = "REFERENCE";
        private static String ORIGIN = "ORIGIN";
        private static String EOREC = "//";

	//Regular Expression objects for parsing Genbank-format records
	private static RE ORGANISM;

	// define the regular expression
        static
        {
            try
            {
                   ORGANISM = new RE("^ +ORGANISM +(.+)$");
            }
	    // this should only happen during development - so catch here
            catch(RESyntaxException e)
            {
                System.out.println(" RESyntaxException: " 
				+ e.getMessage());
            }
        }

}

