/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphdrawingchallenge.tools;

import crossings.finder.Crossing;
import java.util.Comparator;

/**
 *
 * @author Panagiotis
 */
public class CrossingComparator implements Comparator<Crossing> {
    
    @Override
    public int compare(Crossing crossing1, Crossing crossing2) {
        //Αν έχουμε γωνίες μεγαλύτερες των 90 μοιρών, ελέγχουμε τις 
        //συμπληρωματικές τους
        double angle1 = crossing1.getSineOfAngle();
        double angle2 = crossing2. getSineOfAngle();
        double difference = (angle1 > 90 ? 180 - angle1 : angle1) -
                (angle2 > 90 ? 180 - angle2 : angle2);
        
        if (difference < 0)
            return -1;
        else if (difference > 0)
            return 1;
        else
            return 0;
    }
    
}
