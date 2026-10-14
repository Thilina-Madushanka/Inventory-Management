package backend.Controller;

import backend.Exception.InventoryNotFoundException;
import backend.Model.InventoryModel;
import backend.Repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

@RestController
@CrossOrigin("http://localhost:3000")
public class InventoryController {
    @Autowired
    private InventoryRepository inventoryRepository;

    //date insert part
    @PostMapping("/inventory") // create path and cll the model, create object, body request ekk cll krala model ekata data aran eka db ekata return kara
    public InventoryModel newInventoryModel(@RequestBody InventoryModel newInventoryModel){
        return inventoryRepository.save(newInventoryModel);

    }

    //insert image
    @PostMapping("/inventory/itemImg")
    public String itemImage(@RequestParam("file") MultipartFile file){
       String folder="src/main/Upload/"; // image eka save krana folder patheka
       String itemImage = file.getOriginalFilename();
       try{
          File uploadDir = new File(folder);
          if(!uploadDir.exists()){
              uploadDir.mkdir();
          }
          file.transferTo(Paths.get(folder+itemImage));
       }catch (IOException e){
           e.printStackTrace();
           return "error:" + itemImage;
       }
        return itemImage;
    }

    //display all data
    @GetMapping("/inventory")
    List<InventoryModel> getAllItems(){
        return inventoryRepository.findAll();
    }

    //display data using id
    @GetMapping("/inventory/{id}")
    InventoryModel getItemById(@PathVariable Long id){
        return inventoryRepository.findById(id).orElseThrow(()->new InventoryNotFoundException(id));
    }

    //display image
    private final String UPLOAD_DIR = "src/main/upload/"; // assign image location into a variable

    @GetMapping("/upload/{filename}") // catch the file in the upload directory
    public ResponseEntity<FileSystemResource> getImage(@PathVariable String filename){
        File file = new File(UPLOAD_DIR + filename);
        if(!file.exists()){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new FileSystemResource(file));
    }

    //update function
    @PutMapping("/inventory/{id}") // call the api and set the file
    public InventoryModel updateItem(
            @RequestParam(value="itemDetail") String itemDetails,
            @RequestParam(value="file",required = false) MultipartFile file,
            @PathVariable Long id
    ){
        System.out.println("Item Details:" + itemDetails);

        if (file != null && !file.isEmpty()) {
            System.out.println("File Uploaded: " + file.getOriginalFilename());
        } else {
            System.out.println("No file uploaded");
        }

        ObjectMapper mapper = new ObjectMapper();
        InventoryModel newInventory;

        try{
            newInventory = mapper.readValue(itemDetails,InventoryModel.class); //tools.jackson.databind.ObjectMapper, which indicates you may be using Jackson 3 rather than the Jackson 2 package.With Jackson 3, readValue() may throw a different exception type, so catch (IOException e) may be invalid for that block.
        }catch(Exception e){
            throw new RuntimeException("Error while reading item details", e);
        }

        return inventoryRepository.findById(id).map(existingInventory-> {
            existingInventory.setItemName(newInventory.getItemName());
            existingInventory.setItemCategory(newInventory.getItemCategory());
            existingInventory.setItemDetail(newInventory.getItemDetail());

            if(file != null && !file.isEmpty()){
                String folder = "src/main/Upload/";
                File dir = new File(folder);
                if(!dir.exists()) dir.mkdir();

                String itemImage = file.getOriginalFilename();
                try{
                    file.transferTo(Paths.get(folder +itemImage));
                    existingInventory.setItemImage(itemImage);
                }catch(IOException e){
                    throw new RuntimeException("Error while reading item image", e);
                }
            }
            return inventoryRepository.save(existingInventory);
        }
        ).orElseThrow(()-> new InventoryNotFoundException(id));
    }

    //delete function

    @DeleteMapping("/inventory/{id}")
    String deleteItem(@PathVariable Long id){
        //check
        InventoryModel inventoryModel = inventoryRepository.findById(id)
                .orElseThrow(()->new InventoryNotFoundException(id));
        //image delete
        String itemImage = inventoryModel.getItemImage();
        if(itemImage != null && !itemImage.isEmpty()){
            File imageFile = new File("src/main/Upload/"+itemImage);
            if(imageFile.exists()){
                if(imageFile.delete()){
                    System.out.println("Delete Successfully!");
                }else{
                    System.out.println("Delete Failed!");
                }
            }
        }
        inventoryRepository.deleteById(id);
        return "data with id" +id+ "deleted";
    }


}
