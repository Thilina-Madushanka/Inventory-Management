package backend.Exception;

public class InventoryNotFoundException extends RuntimeException{
    public InventoryNotFoundException(Long id){
        super("Inventory Not Found with id:"+id);
    }
    public InventoryNotFoundException(String message){
        super(message);
    }


}
