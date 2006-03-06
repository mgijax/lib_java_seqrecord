package org.jax.mgi.bio.seqrecord;

import java.util.*;

public class SPSeqInterrogator
{
	// Concept:
        //        IS: an object that queries a SwissProt/TREMBL sequence record
        //       HAS: a mapping structure for mapping controlled vocabulary
	//	      to string expressions
        //      DOES: Given a sequence record and a controlled vocabulary string
	//		determine if 's' is for a organism 'organism' .
	//		e.g. Given sequence record 's' and controlled vocab
	//		string "mouse" determine if 's' is a mouse record.
	//            Given a sequence record and a controlled vocabulary string
	// 		determine if 's' is organism classification for
	//              'organism'
	//		e.g. Given a sequence record 's' and controlled vocab
	//		string "rodentia" determine if 's' has "rodentia"
	//		organism classification
	// RESPONSIBLE FOR: 1) mapping controlled vocab terms to string
	//		       expressions
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
	// Purpose: Determines whether sequence record 's' is for 'organism'
        // Returns: boolean true or false
        // Assumes: 'organism' is a valid controlled vocabulary for 's'
	//		and has been converted to lower case
        // Effects: nothing
        // Throws: nothing
        // Notes:

		// get the string expression that is mapped to 'organism'
		String matchString = (String)expressions.get(organism);

		// return true if the string expression matches organism of  's'
		if((s.getOrganism()).indexOf(matchString) >  -1)
                        return true;
                else
                        return false;
	}

	public boolean isOrganismClassif(
                SeqRecord s,       // a SwissProt sequence record
                String organism)   // organism controlled vocabulary
        {
        // Purpose: Determines whether sequence record 's' has classification
	//           for 'organism'
        // Returns: boolean true or false
        // Assumes: 'organism' is a valid controlled vocabulary for 's'
        //              and has been converted to lower case
        // Effects: nothing
        // Throws: nothing
        // Notes:

                // get the string expression that is mapped to 'organism'
                String matchString = (String)expressions.get(organism);

                // return true if the string expression matches organism of  's'
                if((((EMBLSeqRecord)s).getOrganismClassif()).indexOf(matchString) >  -1)
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

