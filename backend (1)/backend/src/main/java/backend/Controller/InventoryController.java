package backend.Controller;

import backend.Model.InventoryModel;
import backend.Repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

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

}
