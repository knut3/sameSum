package models;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Solution {
	public Set<Set<Number>> sums;
    private Set<Set<Number>> correctSums = null;
    private Set<Set<Number>> wrongSums = null;
    private Integer score = null;
    private int positiveScore = 0;
    private int negativeScore = 0;

    public Solution(Set<Set<Number>> sums){
        this.sums = sums;
    }

    public Solution(Set<Set<Number>> sums,
                    float millisSpent,
                    Integer score,
                    Set<Set<Number>> correctSums,
                    Set<Set<Number>> wrongSums){
        this.sums = sums;
        this.score = score;
        this.correctSums = correctSums;
        this.wrongSums = wrongSums;
    }

    public int getScore(){

        if (score != null)
            return score;

        separateWrongAndCorrectSums();
        return score;
    }

    public Set<Set<Number>> getCorrectSums(){
        if (correctSums == null)
            separateWrongAndCorrectSums();

        return correctSums;
    }

    public Set<Set<Number>> getWrongSums(){
        if (wrongSums == null)
            separateWrongAndCorrectSums();

        return wrongSums;
    }

    private void separateWrongAndCorrectSums(){

        if (sums.isEmpty()){
            score = 0;
            return;
        }

        Map<Integer, Set<Set<Number>>> equalSumsMap = new HashMap<>();
        for (Set<Number> sumSet : sums){
            int sum = 0;
            for (Number btn : sumSet)
                sum += btn.getValue();

            if (equalSumsMap.containsKey(sum)) {
                Set<Set<Number>> currentSetOfSets = equalSumsMap.get(sum);
                currentSetOfSets.add(sumSet);
            }
            else{
                Set<Set<Number>> newSet = new HashSet<>();
                newSet.add(sumSet);
                equalSumsMap.put(sum, newSet);
            }
        }

        positiveScore = 0;
        for (Set<Set<Number>> equalSumsSet : equalSumsMap.values() ){

            int currentScore = 0;
            int numSums = 0;
            for (Set<Number> set : equalSumsSet){
                currentScore += set.size();
                numSums++;
            }

            if(numSums < 2)
                continue;

            if (currentScore >= positiveScore){
                positiveScore = currentScore;
                correctSums = equalSumsSet;
            }
        }

        negativeScore = 0;
        wrongSums = new HashSet<>();
        for (Set<Set<Number>> equalSumsSet : equalSumsMap.values() ){

            if (equalSumsSet != correctSums){
                for (Set<Number> wrongSet : equalSumsSet){
                    wrongSums.add(wrongSet);
                    negativeScore += wrongSet.size();
                }
            }
        }

        score = positiveScore - negativeScore;
    }
}
