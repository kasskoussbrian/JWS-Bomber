package fr.epita.assistants.presentation.rest.response;

public class ReverseResponse {
    public String original;
    public String reversed;


    public ReverseResponse(String original) {
        this.original = original;
        int i = original.length()-1;
        this.reversed ="";
        while (i>=0)
        {
            reversed = reversed+ original.charAt(i);
            i--;
        }
    }
}
