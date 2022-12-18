package com.example.laboratorywork2;

import android.content.Context;
import androidx.appcompat.widget.AppCompatButton;

public class MyButton extends AppCompatButton
{
    public MyButton(Context context, boolean chosen)
    {
        super(context);
        setChosen(chosen);
    }

    boolean isChosen()
    {
        return m_chosen;
    }
    void setChosen(boolean chosen)
    {
        m_chosen = chosen;
    }
    private boolean m_chosen;
}
