package lab.db.tables;

 import java.sql.Connection;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.Statement;
 import java.sql.SQLException;
 import java.sql.SQLIntegrityConstraintViolationException;
 import java.util.ArrayList;
 import java.util.Date;
 import java.util.List;
 import java.util.Objects;
 import java.util.Optional;

 import lab.utils.Utils;
 import lab.db.Table;
 import lab.model.Student;

 public final class StudentsTable implements Table<Student, Integer> {    
     public static final String TABLE_NAME = "students";

     private final Connection connection; 

     public StudentsTable(final Connection connection) {
         this.connection = Objects.requireNonNull(connection);
     }

     @Override
     public String getTableName() {
         return TABLE_NAME;
     }

     @Override
     public boolean createTable() {
         // 1. Create the statement from the open connection inside a try-with-resources
         try (final Statement statement = this.connection.createStatement()) {
             // 2. Execute the statement with the given query
             statement.executeUpdate(
                 "CREATE TABLE " + TABLE_NAME + " (" +
                         "id INT NOT NULL PRIMARY KEY," +
                         "firstName CHAR(40) NOT NULL," + 
                         "lastName CHAR(40) NOT NULL," + 
                         "birthday DATE" + 
                     ")");
             return true;
         } catch (final SQLException e) {
             // 3. Handle possible SQLExceptions
             return false;
         }
     }

     /*
     @Override
     public Optional<Student> findByPrimaryKey(final Integer id) {
         try (final Statement statement = this.connection.createStatement()) {
            final var resultSet = statement.executeQuery("SELECT * FROM " + TABLE_NAME + "WHERE id = " + id); //non va mai fatto, mai fidarsi dei valori provenienti dall'esterno in quanto ci potrebbero passare dall'esterno una query che ci elimina tutto. 
            return readStudentsFromResultSet(resultSet).stream().findFirst();
         } catch (final SQLException e) {
            return Optional.empty();
         }
     }*/

     @Override
     public Optional<Student> findByPrimaryKey(final Integer id) {
        final var query = "SELECT * FROM " + TABLE_NAME + "WHERE id = ?"; //TABLE_NAME così perché è un valore statico e finale alll'interno della classe, non viene dall'esterno
         try (final PreparedStatement statement = this.connection.prepareStatement(query)) { 
            statement.setInt(1, id); //il punto interrogativo numero 1 verrà impostato ll volore di id, è più sicuro rispetto a prima. Tutti i ? vanno impostati con un set. 
            final var resultSet = statement.executeQuery(); 
            return readStudentsFromResultSet(resultSet).stream().findFirst();
         } catch (final SQLException e) {
            return Optional.empty();
         }
     }

     /**
      * Given a ResultSet read all the students in it and collects them in a List
      * @param resultSet a ResultSet from which the Student(s) will be extracted
      * @return a List of all the students in the ResultSet
      */
     private List<Student> readStudentsFromResultSet(final ResultSet resultSet) {

         // Create an empty list, then
         // Inside a loop you should:
         //      1. Call resultSet.next() to advance the pointer and check there are still rows to fetch
         //      2. Use the getter methods to get the value of the columns
         //      3. After retrieving all the data create a Student object
         //      4. Put the student in the List
         // Then return the list with all the found students

         // Helpful resources:
         // https://docs.oracle.com/javase/7/docs/api/java/sql/ResultSet.html
         // https://docs.oracle.com/javase/tutorial/jdbc/basics/retrieving.html

        List<Student> students = new ArrayList();

        try {
            while (resultSet.next()) {
                     
                int id = resultSet.getInt("id");
                String name = resultSet.getString("firstName");
                String surname = resultSet.getString("lastName");
                Optional<Date> date = Optional.ofNullable(Utils.sqlDateToDate(resultSet.getDate("birthday")));
                 
                Student student = new Student(id, name, surname, date);
            
                students.add(student);
                
            }
            return students;
        } catch (Exception e) {
            // TODO: handle exception
        }
         throw new UnsupportedOperationException("TODO");

     }

     @Override
     public List<Student> findAll() {
         throw new UnsupportedOperationException("TODO");
     }


     public List<Student> findByBirthday(final Date date) {
        final var query = "SELECT * FROM " + TABLE_NAME + "WHERE date = ?";
        try (final PreparedStatement statement = this.connection.prepareStatement(query)) {
            statement.setDate(1, Utils.dateToSqlDate(date));
            final var resultSet = statement.executeQuery();
            return readStudentsFromResultSet(resultSet);
        } catch (final SQLException e) {
            return List.of();
        }
        
    }

     @Override
     public boolean dropTable() {
         throw new UnsupportedOperationException("TODO");
     }

     @Override
     public boolean save(final Student student) {
         throw new UnsupportedOperationException("TODO");
     }

     @Override
     public boolean delete(final Integer id) {
         throw new UnsupportedOperationException("TODO");
     }

     @Override
     public boolean update(final Student student) {
         throw new UnsupportedOperationException("TODO");
     }
 }