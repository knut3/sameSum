package no.knut.addem.android.addem.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import no.knut.addem.android.addem.core.Board;
import no.knut.addem.android.addem.core.Number;
import no.knut.addem.android.addem.core.Solution;

public class BoardInputView extends BoardView implements View.OnTouchListener {

    private final static String LOG_KEY = "BoardView";
    private final int[] Colors = new int[]{Color.BLUE, Color.RED, Color.CYAN, Color.GREEN, Color.MAGENTA, Color.YELLOW};

    private Stack<NumberButtonSum> sums;
    private NumberButtonSum currentSum;
    private NumberButton firstButtonTouched;
    private Paint[] backgroundPaints;
    private int currentPaintIndex;
    private boolean allowInput;


    public BoardInputView(Context context, Board board) {
        super(context, board);
        sums = new Stack<>();
        firstButtonTouched = null;
        allowInput = true;
        setOnTouchListener(this);

        backgroundPaints = new Paint[Colors.length];

        for (int i = 0; i < Colors.length; i++){
            Paint paint = backgroundPaints[i] = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Colors[i]);
        }
        currentPaintIndex = 0;
    }

    public void undoLastSum(){
        if(sums.isEmpty())
            return;

        Set<NumberButton> sum = sums.pop();
        for (NumberButton button : sum){
            button.paint = basicPaint;
        }
        invalidate();
    }

    public void clearAllSums(){

        if(sums.isEmpty())
            return;

        for (Set<NumberButton> sum : sums){
            for (NumberButton button : sum){
                button.paint = basicPaint;
            }
        }
        sums.clear();
        invalidate();
    }

    public Solution getSolution(){
        Set<Set<Number>> sumsAsSet = new HashSet<>(sums.size());
        Set<Number> sumAsSet;

        for (Set<NumberButton> sum : sums){

            sumAsSet = new HashSet<>(sum.size());
            for( NumberButton button : sum ){
                sumAsSet.add(button.number);
            }

            sumsAsSet.add(sumAsSet);
        }

        return new Solution(sumsAsSet, 30);
    }

    public void deactivateInput(){
        allowInput = false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (!allowInput)
            return false;

        NumberButton buttonTouched = null;
        // Check if any button is touched
        for (NumberButton button : numberButtons){
            if(button.contains(event.getX(), event.getY()))
                buttonTouched = button;
        }

        if (buttonTouched == null) {
            if (event.getAction() == MotionEvent.ACTION_UP){
                if (currentSum != null && !currentSum.isEmpty())
                    sums.push(currentSum);

                currentSum = null;
                firstButtonTouched = null;
            }
            return true;
        }

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            currentSum = getSetOf(buttonTouched);

            if (currentSum == null) {
                // We are starting a new sum, so pick a new color
                currentSum = createNewSum();
            }
            else {
                firstButtonTouched = buttonTouched;
            }
        }
        else  if (event.getAction() == MotionEvent.ACTION_UP) {

            if (firstButtonTouched != null && firstButtonTouched == buttonTouched) {
                boolean allowDeletion = false;
                if(currentSum.size() <= 2)
                    allowDeletion = true;
                else {
                    List<NumberButton> afterDeletionList = new ArrayList<>(currentSum);
                    afterDeletionList.remove(buttonTouched);
                    if (legalSum(afterDeletionList)){
                        allowDeletion = true;
                    }

                }

                if (allowDeletion){
                    buttonTouched.paint = basicPaint;
                    currentSum.remove(buttonTouched);
                    invalidate();
                }
            }

            if (currentSum != null && !currentSum.isEmpty())
                sums.push(currentSum);

            currentSum = null;
            firstButtonTouched = null;
        }
        else if (event.getAction() == MotionEvent.ACTION_MOVE){
            if (currentSum == null){
                currentSum = createNewSum();
            }

            if(isSelectedInAnotherSet(buttonTouched, currentSum))
                return true;

            if (!legalAdditionToSet(buttonTouched, currentSum))
                return true;
            currentSum.add(buttonTouched);
            buttonTouched.paint = currentSum.getPaint();
            this.invalidate();
        }

        return true;
    }

    private boolean legalAdditionToSet(NumberButton button, Set<NumberButton> set){

        if (set.isEmpty())
            return true;

        boolean hasConnectedButton = false;

        for (NumberButton buttonInSet : set){
            if (button == buttonInSet)
                return false;

            if (areNeighbours(button, buttonInSet))
                hasConnectedButton = true;
        }

        return hasConnectedButton;
    }

    private boolean areNeighbours(NumberButton button1, NumberButton button2){
        if (Math.abs(button1.number.getRow() - button2.number.getRow()) > 1)
            return false;

        if (Math.abs(button1.number.getColumn() - button2.number.getColumn()) > 1)
            return false;

        return true;
    }

    private boolean legalSum(List<NumberButton> sum){
        List<NumberButton> removeList = new ArrayList<>();
        Stack<NumberButton> S = new Stack<>();
        S.push(sum.remove(0));
        while (!S.isEmpty()){
            NumberButton v = S.pop();

            for (NumberButton w : sum){
                if (!areNeighbours(v, w))
                    continue;

                S.push(w);
                removeList.add(w);
            }

            sum.removeAll(removeList);
            removeList.clear();
        }

        if (sum.isEmpty())
            return true;
        else return false;
    }

    private boolean isSelectedInAnotherSet(NumberButton button, Set<NumberButton> currentSet){

        for (Set<NumberButton> anotherSet : sums)
        {
            if (currentSet != anotherSet && anotherSet.contains(button))
            {
                return true;
            }
        }

        return false;
    }

    private NumberButtonSum createNewSum(){
        currentPaintIndex++;
        if(currentPaintIndex >= backgroundPaints.length)
            currentPaintIndex = 0;

        return new NumberButtonSum(backgroundPaints[currentPaintIndex]);
    }

    private NumberButtonSum getSetOf(NumberButton button)
    {
        for (NumberButtonSum sum : sums)
        {
            if(sum.contains(button))
            {
                return sum;
            }
        }

        return null;
    }
}
