package com.example.demo.model.db.repository;

import com.example.demo.model.db.entity.Car;
import com.example.demo.model.db.entity.User;
import com.example.demo.model.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {

//    Optional<User> findByEmail(String email); //если возвращаем Optional-то тогда есть варианты для обработки
//    User findByEmail(String email); //а здесь может вернуться null, надо осторожно.
    // Также, может быть найдено несколько сущностей по email, и тогда выбросится исключение здесь, поэтому надо осторожно когда мы достаем одну сущность,
    // а не лист по какому-то параметру


    Optional<User> findByEmail(String email);

    List<User> findAllByStatus(UserStatus status);

    //а в этом методе обращаемся не с помощью HQL, а с помощью SQL
    //ищем пользователя по firstName,первого с начала
//    @Query(nativeQuery = true, value = "select * from users where first_name = :firstName order by first_name desc limit 1")
//    Page<User>  findByFirstName(@Param("firstName") String firstName);

    @Query(nativeQuery = true, value = "select * from users where first_name = :firstName")
    List<User> findByFirstName(@Param("firstName") String firstName); //"firstName" - это поле key в параметрах запроса, а :firstName - это идет в value

    //для HQL диалекта не надо писать nativeQuery = true
    //и можно использовать алиасы
    //в HQL мы обращаемся не к Таблице и ее колонкам, а к самОй сущности User и его полям
    @Query("select u from User u where u.firstName = :firstName")
    List<User> findAllFirstName(@Param("firstName") String firstName);

    @Query("select u from User u where u.status <> '2'")
    List<User> findAllNotDeleted(Pageable request);

    List<User> findAll();

    @Query("select u from User u where u.status <> '2' and u.email like %:filter%")
    List<User> findAllNotDeleted(Pageable request, @Param("filter") String filter);

    @Query(nativeQuery = true, value = "%:filter%")
    List<User> findAllIsNeeded (@Param("filter") String filter);

}
