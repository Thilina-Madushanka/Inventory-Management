package backend.Controller;

import backend.Exception.UserNotFoundException;
import backend.Model.UserModel;
import backend.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@CrossOrigin("http://localhost:3000")
public class UserController {
    @Autowired
    public UserRepository userRepository;

    //user Register
    @PostMapping("/user")
    public UserModel newUserModel(@RequestBody UserModel newUserModel){
        return userRepository.save(newUserModel);
    }

    //user login
    @PostMapping("/login") //create the login path
    public ResponseEntity<Map<String, Object>> login(@RequestBody UserModel loginDetails){
        UserModel user = userRepository.findAllByEmail(loginDetails.getEmail())
                .orElseThrow(()-> new UserNotFoundException("user not found!" + loginDetails.getEmail()));

        //check the password is match
        if(user.getPassword().equals(loginDetails.getPassword())){
            Map<String, Object> response = new HashMap<>();
            response.put("message", "login Successful.");
            response.put("id",user.getId());
            return  ResponseEntity.ok(response);
        }else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "wrong Password"));
        }


    }

    //display user Profile
    @GetMapping("/user")
    List<UserModel> getAllUsers(){
        return userRepository.findAll();
    }
    @GetMapping("/user/{id}")
    UserModel getUserByID(@PathVariable Integer id){
        return userRepository.findById(id)
                .orElseThrow(()-> new UserNotFoundException("user not found:" + id));
    }


}
