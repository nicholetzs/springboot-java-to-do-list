package br.com.nicholenicolini01.todolist01.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import at.favre.lib.crypto.bcrypt.BCrypt;

@RestController
@RequestMapping("/users")
public class MinhaUserController {

    @Autowired
    private IUserRepository userRepository;

    @PostMapping("/")
    public ResponseEntity created(@RequestBody MinhaUserModel minhaUsarmodel){
       var user = this.userRepository.findByUsername(minhaUsarmodel.getUsername());

       if (user != null) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User alredy exists");
        
       } else {

       var passwordHashred = BCrypt.withDefaults()
       .hashToString(12, minhaUsarmodel.getPassword().toCharArray());

       minhaUsarmodel.setPassword(passwordHashred); 
        var userCreated = this.userRepository.save(minhaUsarmodel);
        return ResponseEntity.status(HttpStatus.CREATED).body(userCreated);
       }
    }
    
}
