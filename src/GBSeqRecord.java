package org.jax.mgi.bio.seqrecord;

import java.io.*;
import java.util.*;
import org.apache.regexp.*;

public class GBSeqRecord extends SeqRecord
{
	// Concept:
	//	  IS: an object that represents a Genbank-format sequence record
	//	 HAS: A GenInfo Identifier also see superclass 
	//	DOES: reads itself from an input stream (parsing) and
	//	      provides accessor for GI Id. Also see superclass 
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
        // Purpose: reads a Genbank-format sequence record from a file
        // Returns: nothing
        // Assumes: "reader" is a filestream of Genbank-format sequence records
        // Effects: "reader" has advanced to the next record in the stream 
        // Throws: IO, EOF,  and regular expression syntax exceptions
        // Notes:

		// breaks the Accession line into tokens
		// for easy access to seqId's
		StringTokenizer tokenizedAccession;

		// breaks the VERSION line into tokens for easy access to
		// version numbers and GenInfoId 
		StringTokenizer tokenizedVersion;

		// for unneeded tokens when parsing tokens
		String dummy;
	
		// LOCUS is the first line in a record
		// When the first LOCUS line in the stream is reached set to 
		// true and start appending "line" to "text".
		// This eliminates the file header.
		boolean flagLocus = false;

		// When the first ACCESSION line is reached set to true. When
		// true all subsequent lines are also ACCESSION lines until the 
		// VERSION line is reached, then set this flag to false.
		boolean flagAccession = false;

		// When an ORGANISM line is reached set to true. When true
		// all subsequent lines are also ORGANISM lines until the 
		// REFERENCE line is reached, then set this flag to false.
                boolean flagOrganism = false;

		// When an ORIGIN line is reached set to true. When true
		// all subsequent lines are also ORIGIN lines until the
		// "//" end of record line is reached.
		boolean flagOrigin = false;

		// carriage return
                String CRT = "\n";
		
		// value of the previous line for error checking
		String prevLine = "";
		
		// reinit all appended instance vars for a new record
		this.organism.setLength(0);
		this.text.setLength(0);
		this.sequence.setLength(0);
		this.seqIds.clear();
                
		// read current line in the reader stream. 
                this.line = reader.readLine();
 		
		// Debug
		this.lineCount ++;
	
		while(this.line != null && !(this.line.startsWith(EOREC)))
		// a null "line" indicates end of file. If EOF or end of record
                // quit the loop, the full record has been read
                {
			if(this.line.startsWith(LOCUS)) 
			// Parse LOCUS line
                        // Can be only one LOCUS line/record. Pieces of
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
				// get the sequence length
				this.seqLength = 
					(this.line.substring(22, 29)).trim();
				
				// get the sequence type
				this.type = 
					(this.line.substring(36, 41)).trim();
	
				// get the Genbank division code
				this.division = this.line.substring(52, 55);

				// get the date
				this.date = this.line.substring(62, 73);

				// We have found a lOCUS line
				flagLocus = true;
                        }
			
			else if ((this.line.startsWith(ACCESSION)) && 
					(flagLocus == true))
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
				
				// place each seqId (token) in "seqId" Vector	
				while(tokenizedAccession.hasMoreElements())
                                {
                                        this.seqIds.add(
						tokenizedAccession.nextToken());
                                }
				// We have found an ACCESSION line
				flagAccession = true;
				
			}

			else if ((this.line.startsWith(VERSION)) && 
					(flagLocus == true))
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
				this.version = tokenizedVersion.nextToken();

				// get the genbank info id
				this.genInfoId = tokenizedVersion.nextToken();
	
				// We have found the VERSION line indicating
				// end of ACCESSION lines for this record
				flagAccession = false;
				
			}

			else if(flagLocus == true && flagAccession == true)
			// If we have the ACCESSION line but haven't reached
                        //      VERSION line yet we have multiple ACCESSION
                        //      lines. Add these id's to the "seqIds" Vector
                        {
                                tokenizedAccession = 
					new StringTokenizer(this.line);
                                while(tokenizedAccession.hasMoreElements())
                                {
                                        this.seqIds.add(
						tokenizedAccession.nextToken());
                                }

                        }

                        else if((ORGANISM.match(this.line) == true) && 
							(flagLocus == true))
			// If we have found the ORGANISM line
                        //     save the formal scientific name
                        // ORGANISM is a sub-keyword of SOURCE and indented
                        //      use regexp to find it
                        {
                                this.organism.append(ORGANISM.getParen(1));

                                // We have found the ORGANISM line for this rcd
				flagOrganism = true;

                        }

			else if((this.line.startsWith(REFERENCE)) && 
							(flagLocus == true))
			// If line starts with REFERENCE we are at the end
                        // of ORGANISM lines. Set the organism flag to false
                        {
				flagOrganism = false;
                        }

                        else if (flagOrganism == true && flagLocus == true)
			// If we have found the ORGANISM line, but haven't found
                        //    the REFERENCE line, append the taxonomic
                        //    classification level lines that follow the
                        //    ORGANISM line to "organism"
                        {
                                this.organism.append(this.line);
                        }

			else if ((this.line.startsWith(ORIGIN)) && 
							(flagLocus == true))
			// If line starts with ORIGIN, set the origin flag which
                        // indicates the next line(s) will be sequence lines
			{
				flagOrigin = true;
			}

			else if (flagLocus == true && flagOrigin == true)
			// if the Origin flag is set append this line to
                        // "sequence". When EOREC is found, sequence is done
			{
				this.sequence.append(this.line);
				this.sequence.append(CRT);
			}

			if (flagLocus == true)
			// Append "line" to "text" only if first LOCUS line has
                        // been found
                        {
                                this.text.append(this.line);
				this.text.append(CRT);
                        }
			
			// keep track of previous line for error checking
			prevLine = this.line;

			// read the next line in the record
			this.line = reader.readLine();
				
			//For debugging
			this.lineCount++;
			
			// Append this.line if EOREC ("//") Since it is part
			// of the loop condition above it won't be appended 
			// otherwise
			if(this.line.startsWith(EOREC) && flagLocus == true)
			{
				this.text.append(this.line);
				this.text.append(CRT);
			} 

			if(this.line == null && !(prevLine.startsWith(EOREC)))
			// If "line" is null we are at end-of-file. If we are at
                        // EOF and the last line was not EOREC then throw an
                        // exception
                        {
                                throw new EOFException("EOF found before " +
					"end of record!!");
                        }
		}
		//DEBUG:
		//System.out.println("Sequence = " + sequence);
		//System.out.println("Text = " + text);
		//for(int i = 0; i < this.seqIds.size(); i++ )
		//	System.out.println(seqIds.elementAt(i));
		//System.out.println(this.getVersion());
		
	}
	
	// Accessor for GI Id
	public String getGenInfoId()
        {
                return this.genInfoId;
        }

	//
	//instance vars
	//

	// The NCBI GenInfo Identifier sequence identification number. This id
	// runs parallel to and is superseded by the accession.version system 
	// of sequence id's
	protected String genInfoId = "";
	
	//DEBUG:
	//public static int lineCount = 0;
	
	//String expressions for parsing Genbank-format records
        private static String LOCUS = "LOCUS";
        private static String ACCESSION = "ACCESSION";
	private static String VERSION = "VERSION";
        private static String REFERENCE = "REFERENCE";
        private static String ORIGIN = "ORIGIN";
        private static String EOREC = "//";

	//Regular Expression objects for parsing Genbank-format records
	private static RE ORGANISM;

        static
        {
            try
            {
                   ORGANISM = new RE("^ +ORGANISM +(.+)$");
            }
            catch(RESyntaxException e)
            {
                System.out.println(" RESyntaxException: " 
				+ e.getMessage());
            }
        }

}

