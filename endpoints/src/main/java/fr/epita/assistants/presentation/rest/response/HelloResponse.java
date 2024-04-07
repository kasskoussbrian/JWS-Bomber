package fr.epita.assistants.presentation.rest.response;

public class HelloResponse {
    public String content;

    public HelloResponse(String content) {
        this.content = "hello "+content;
    }
}
