package edu.chalmers.sikkr.frontend;

import android.content.Intent;
import android.view.View;

import edu.chalmers.sikkr.frontend.ContactGridActivity;

/**
 * A simple class to handle buttons clicked in ContactBookActivity.
 * @author Jesper Olsson
 */
class ContactBookClickListener implements View.OnClickListener {

    final private int position;
    private final Character initialLetter;
    public ContactBookClickListener(int position, Character initialLetter){

        this.position = position;
        this.initialLetter = initialLetter;
    }

    /**
     * A method that will perform certain actions when button is clicked.
     * @param view
     */
    public void onClick(View view){
        Intent intent = new Intent(view.getContext(), ContactGridActivity.class);
        intent.putExtra("initial_letter", initialLetter);
        view.getContext().startActivity(intent);
    }

}
