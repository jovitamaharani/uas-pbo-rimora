package backend.repository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface CrudRepository<T, ID> {
    List<T> findAll() throws SQLException;

    Optional<T> findById(ID id) throws SQLException;

    boolean insert(T entity) throws SQLException;

    boolean update(T entity) throws SQLException;

    boolean delete(ID id) throws SQLException;
}