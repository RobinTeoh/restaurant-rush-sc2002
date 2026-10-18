package restaurant.control;

public record ActionResult(boolean ok, String message) {

    public static ActionResult success(String message) {
        return new ActionResult(true, message);
    }
    
    public static ActionResult failure(String reason) {
        return new ActionResult(false, reason);
    }

}
