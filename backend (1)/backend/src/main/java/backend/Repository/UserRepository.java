package backend.Repository;

import backend.Model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserModel, Integer> {

    //for user login - check email already exists in the db
    Optional<UserModel> findAllByEmail(String email);


}
