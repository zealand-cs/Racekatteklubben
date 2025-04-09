package dk.zealandcs.racekatteklubben.infrastructure.cat;

import dk.zealandcs.racekatteklubben.config.DatabaseConfig;
import dk.zealandcs.racekatteklubben.domain.Cat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class CatRepository implements ICatRepository {
    private static final Logger logger = LoggerFactory.getLogger(CatRepository.class);
    @Autowired
    private DatabaseConfig databaseConfig;

    /**
     * Writes a new cat to database by given info
     */
    @Override
    public Cat write(Cat cat) {
        String sql = "INSERT INTO cats (ownerId, name, race, gender, dateOfBirth, imageUrl) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            logger.info("Insert cat {}", cat);
            stmt.setInt(1, cat.getOwnerId());
            stmt.setString(2, cat.getName());
            stmt.setString(3, cat.getRace());
            stmt.setString(4, cat.getGender());
            stmt.setDate(5, cat.getDateOfBirth());
            stmt.setString(6, cat.getImageUrl());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    cat.setId(keys.getInt(1));
                    logger.info("Insert cat id {}", cat.getId());
                    return cat;
                }
            }
            logger.error("Failed to retrieve generated ID");
            throw new CatWriteException("Something went wrong when creating cat");
        } catch (SQLException e) {
            logger.error("SQL Exception error inserting cat {}", cat, e);
            throw new CatWriteException("Something went wrong when creating cat");
        }
    }

    /*
    * finds cat by its ID
     */
    @Override
    public Optional<Cat> findById(int id) {
        String sql = "SELECT id, ownerId, name, race, gender, dateOfBirth, imageUrl FROM cats WHERE id = ?";
        try (Connection conn = databaseConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql);) {
            logger.info("Get cat {}", id);

            stmt.setInt(1, id);
            var rs = stmt.executeQuery();
            if (rs.next()) {
                return catFromResultSet(rs);
            }
            logger.warn("No record found for id {}", id);
            return Optional.empty();
        } catch (SQLException e) {
            logger.error("SQL Exception error getting cat {}", id, e);
            return Optional.empty();
        }
    }

    /*
     * returns list of cats owned by a user
     */
    @Override
    public List<Cat> findByUserId(int id) {
        String sql = "SELECT id, ownerId, name, race, gender, dateOfBirth, imageUrl FROM cats WHERE ownerId = ?";
        try (Connection conn = databaseConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql);) {

            logger.info("Get cats by owner {}", id);

            stmt.setInt(1, id);
            var rs = stmt.executeQuery();
            var cats = new ArrayList<Cat>();
            while (rs.next()) {
                var cat = catFromResultSet(rs);
                cat.ifPresent(cats::add);
            }
            logger.info("Found {} cats", cats.size());
            return cats;
        } catch (SQLException e) {
            logger.error("SQL Exception error getting cats", e);
            throw new RuntimeException(e);
        }
    }

    /*
     * Returns all cat objects from DB
     */
    @Override
    public List<Cat> findAll() {
        String sql = "SELECT id, ownerId, name, race, gender, dateOfBirth, imageUrl FROM cats";
        try (Connection conn = databaseConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql);) {

            logger.info("Fetching all cats");
            var rs = stmt.executeQuery();
            var cats = new ArrayList<Cat>();
            while (rs.next()) {
                var cat = catFromResultSet(rs);
                cat.ifPresent(cats::add);
            }
            logger.info("Found {} cats", cats.size());
            return cats;
        } catch (SQLException e) {
            logger.error("SQL Exception error getting cats", e);
            throw new RuntimeException(e);
        }
    }

    /*
    * Updates an existing cat in DB
     */
    @Override
    public void update(Cat cat) {
        String sql = "UPDATE cats SET ownerId = ?, name = ?, race = ?, gender = ?, dateOfBirth = ?, imageUrl = ? WHERE id = ?";

        try (Connection conn = databaseConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql);) {

            logger.info("Updating cat {}", cat);

            stmt.setInt(1, cat.getOwnerId());
            stmt.setString(2, cat.getName());
            stmt.setString(3, cat.getRace());
            stmt.setString(4, cat.getGender());
            stmt.setDate(5, cat.getDateOfBirth());
            stmt.setString(6, cat.getImageUrl());
            stmt.setInt(7, cat.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("SQL Exception error updating cat {}", cat, e);
            throw new RuntimeException(e);
        }
    }

    /*
    * Deletes cat by ID
     */
    @Override
    public void delete(int id) {
        String sql = "DELETE FROM cats WHERE id = ?";

        try (var conn = databaseConfig.getConnection(); var stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /*
    * maps a ResultSet to a Cat object
     */
    Optional<Cat> catFromResultSet(ResultSet rs) {
        try {
            var cat = new Cat();
            cat.setId(rs.getInt("id"));
            cat.setOwnerId(rs.getInt("ownerId"));
            cat.setName(rs.getString("name"));
            cat.setRace(rs.getString("race"));
            cat.setGender(rs.getString("gender"));
            cat.setDateOfBirth(rs.getDate("dateOfBirth"));
            cat.setImageUrl(rs.getString("imageUrl"));
            return Optional.of(cat);
        } catch (SQLException e) {
            logger.error("Failed to map ResultSet", e);
            return Optional.empty();
        }
    }
}
