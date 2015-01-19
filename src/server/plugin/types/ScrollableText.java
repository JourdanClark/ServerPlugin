package server.plugin.types;

public class ScrollableText {

    String text;
    int    start = 0, length, ran = 0;

    public ScrollableText(String text, int length, int buffer){
        this.text = text;
        if(!text.endsWith(" "))
            this.text += " ";
        this.length = length;

        for(int i = 0;i <= buffer;i++)
            this.text += " ";
    }

    public String getText(){
        return text;
    }

    public String getScrollableText(){
        return scrollText();
    }

    private String scrollText(){
        String s = "";
        // int placed = 1;

        if(ran == length) {
            ran = -1;
        }

        if(ran != -1) {
            for(int i = 0;i <= (length - ran);i++) {
                s += " "; // empty space so that it can scroll in from the right
            }
            ran++;
        }

        s += text;
        s = s.substring(start);
        s += text; // adds it onto the end for when it gets to the end
        s = s.substring(0, length);

        if(start >= text.length()) {
            start = 0;
        } else if(ran == -1) { // should not increase start until the empty
                               // space is gone, otherwise
            start++; // it will take off two spaces each time because it will
                     // take off a space
                     // from the start and one from the end of the empty
                     // spaces
        }

        return s;
    }
}