import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;


public class UI {
    public int ScreenWidth;
    public String pageTitle, pageHeadTop, pageHeadBot, pageEmptyHeadLine, pageEmptyBodyLine, pageDiv, pageDashDiv, pageBot, mainColour;
    public static Map<String, String> colours = new HashMap<>();
    public UI(int width) {
        colours.put("blue", "\u001B[34m");
        colours.put("yellow", "\u001B[33m");
        colours.put("green", "\u001B[32m");
        colours.put("red", "\u001B[31m");
        colours.put("purple", "\u001B[35m");
        colours.put("reset", "\u001B[0m");
        mainColour = colours.get("blue");

        ScreenWidth = Math.min(201,Math.max(width, 51));
        if (ScreenWidth % 2 == 0) ScreenWidth++;
        refreshBorders();
    }

    public void refreshBorders(){
        pageTitle = formatBorder("Home", "┌","┐");
        pageHeadTop = formatBorder("", "│ ┌", "┐ │");
        pageHeadBot = formatBorder("", "│ └", "┘ │");
        pageEmptyHeadLine = centerText("", "│ │");
        pageEmptyBodyLine = centerText("", "│");
        pageDiv = formatBorder("", "├", "┤");
        pageDashDiv = formatBorder(" ─", "├", "┤");
        pageBot = formatBorder("", "└", "┘");
    }

    public static String colourText(String text, String colour) {
        if (colours.containsKey(colour)){
            return colours.get(colour) + text + colours.get("reset");
        }
        else throw new IllegalArgumentException("Invalid colour argument");
    }

    public void setMainColour(String colour) {
        if (colours.containsKey(colour)){
            mainColour = colours.get(colour);
            refreshBorders();
        }
        else throw new IllegalArgumentException("Invalid colour argument");
    }
    public String colourMain(String message) {return mainColour + message + colours.get("reset");}
    

    public void clear(){
        System.out.print("\033[H\033[2J");  
        System.out.flush();  
    }

    public void setTitle(String text) {
        pageTitle = formatBorder(text, "┌","┐");
    }

    public String centerText(String text, String tip) {
        int left = (ScreenWidth - text.length() - (tip.length() * 2)) / 2;
        int right = ScreenWidth - text.length() - left - (tip.length() * 2);
        return colourMain(tip) + " ".repeat(left) + text + " ".repeat(right) + colourMain(tip);
    }

    public String centerText(String text, int length, String tip) {
        int left = (ScreenWidth - length - (tip.length() * 2)) / 2;
        int right = ScreenWidth - length - left - (tip.length() * 2);
        return colourMain(tip) + " ".repeat(left) + text + " ".repeat(right) + colourMain(tip);
    }

    public String formatBorder(String text, String leftTip, String rightTip) {
        int left = (ScreenWidth - text.length() - (leftTip.length() + rightTip.length())) / 2;
        int right = ScreenWidth - text.length() - left - (leftTip.length() * 2);
        return colourMain(leftTip + "─".repeat(left) + text + "─".repeat(right) + rightTip);
    }

    public String formatBorder(String text, String fill, String leftTip, String rightTip) {
        int left = (ScreenWidth - text.length() - (leftTip.length() + rightTip.length())) / 2;
        int right = ScreenWidth - text.length() - left - (leftTip.length() * 2);
        return colourMain(leftTip + fill.repeat(left) + text + fill.repeat(right) + rightTip);
    }

    public String formatHead(String text) {
        String[] lines = text.split(",");
        String out = "";
        for (int i = 0; i < lines.length; i++) {
            out += centerText(lines[i], "│ │");
            if (i < lines.length - 1) {
                out += "\n";
            }
        }
        return out;
    }

    public String formatBody(String text) {
        String[] lines = text.split(",");
        String out = "";
        for (int i = 0; i < lines.length; i++) {
            out += centerText(lines[i], "│");
            if (i < lines.length - 1) {
                out += "\n";
            }
        }
        return out;
    }

    public String formatOptions(String text){
        String[] lines = text.split(",");
        int left = 9, right;
        String out = "";
        for (int i = 0; i < Array.getLength(lines); i++){
            right = ScreenWidth - left - lines[i].length() - 5;
            out += colourMain("│") + " ".repeat(left) + (i + 1) + ") " + lines[i] + " ".repeat(right) + colourMain("│");
            if (i + 1 != Array.getLength(lines)) out += "\n";
        }
        return out;
    }

    public String formatError(String text) {
        return centerText(colourText(text, "red"), text.length(), "│");
    }

    public String formatPlayerDisplay(Game game){
        return 
            "│    " 
            + colourText(game.P1Name, game.P1Colour.equals("r") ? "red" : "yellow") 
            + " ".repeat(ScreenWidth - game.P1Name.length() - game.P2Name.length() - 10) 
            + colourText(game.P2Name, (game.P1Colour.equals("r") ? "yellow" : "red")) 
            + "    │";
    }

    public String formatBoard(String turn, String boardString) {
        String out = "";
        String b = colourMain("│");
        String pad = " ".repeat((ScreenWidth-31)/2);
        String[] lines = boardString.split("\n");
        for (int i = 0; i < Array.getLength(lines); i++) {
            out += b + pad + lines[i] +  pad + b;
            out += (i < lines.length -1) ? "\n" : "";  
        }
        return out;
    }

    public void printPage(String title, String head, String body, String err){
        clear();
        setTitle(title);
        System.out.println(pageTitle);
        System.out.println(pageHeadTop);
        System.out.println(pageEmptyHeadLine);
        System.out.println(formatHead(head));
        System.out.println(pageEmptyHeadLine);
        System.out.println(pageHeadBot);
        if (body != "") {
            System.out.println(pageDiv);
            System.out.println(pageEmptyBodyLine);
            System.out.println(body);
            System.out.println(pageEmptyBodyLine);
        }
        if (!err.equals("")){
            System.out.println(pageDiv);
            System.out.println(formatError(err));
        }
        System.out.println(pageBot);
    }

    public void printMCPage(String title, String prompt, String options, String err) {
        printPage(
            title,
            prompt,
            formatOptions(options),
            err
        );
    }

    public void printBoardPage(Game game, String boardString,String err) {
        String player = (game.currentTurn.equals("y")) ? "Yellow" : "Red";
        setMainColour(player.toLowerCase());
        printPage(
            " " + player + " Turn ",
            player + " Player Make Your Move:",
            formatPlayerDisplay(game) + "\n" + formatBoard(game.currentTurn, boardString) ,
            err
        );
    }

    public void printMessagePage(String title, String message, String err) {
        printPage(title, message, "", err);
    }

    
}
