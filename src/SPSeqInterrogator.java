package org.jax.mgi.bio.seqrecord;

import org.apache.regexp.*;
import java.util.*;

public class SPSeqInterrogator
{
	// Concept:
        //        IS: an interrogator that gives boolean answers to questions
	//		about SwissProt/TREMBL records
        //       HAS: a mapping structure for mapping SwissProt/TREMBL control-
	//	      led vocabulary to string expressions	
        //      DOES: Given a sequence record and a controlled vocabulary string
	//		determine if a predicate is true. e.g. Given a sequence
	//		record 's' and a string "mouse" determine if 's' is a 
	//		mouse record 
	// RESPONSIBLE FOR: 1) mapping controlled vocab to string expressions
	//		    2) providing basic predicates to compare fields
	//		       of a sequence record to controlled vocabulary	
        // Implementation:

	//	
	//methods
	//

	public boolean isOrganism(	
		SeqRecord s,       // a SwissProt sequence record
		String organism)   // organism controlled vocabulary
	{
	// Purpose: Determines whether a sequence record is for a given organism
        // Returns: boolean true or false
        // Assumes: "organism" is a valid controlled vocabulary for 's'
	//		and has been converted to lower case
        // Effects: nothing
        // Throws: nothing
        // Notes:
		
		// catenate the organism(species) and classification fields
		String speciesAndClassif = ((EMBLSeqRecord)s).
				getOrganismClassif() + " " + s.getOrganism();
	
		// get the string expression that is mapped to "organism"
		String matchString = (String)expressions.get(organism);
		
                // If matchString is null then "organism" is not represented
                // in the hashmap. This shouldn't happen because the  Applic-
                // ation filter must know what organisms are supported

		// if "matchString" is found in 's' es organism field
                // 's' is the organism represented by matchString
		if((s.getOrganism()).indexOf(matchString) >  -1)
                        return true;
                else
                        return false;
	}
	
	//
	// instance variables
	//

	// controlled vocab
	// a hash map data structure that maps organism controlled vocab
        // to a String expression. All matching is done in lower case
	private static String MOUSE = "Mus musculus".toLowerCase();
	private static String RAT = "Rattus".toLowerCase();
	private static String RODENT = "Rodentia;".toLowerCase();
	private static String HUMAN = "sapiens".toLowerCase();
	
	// load HashMap with controlled vocab keys and string expression values
	private static HashMap expressions = new HashMap();
	static
	{
		expressions.put("mouse", MOUSE);
		expressions.put("rat", RAT);
		expressions.put("rodent", RODENT);
		expressions.put("human", HUMAN);
	}
     
}

