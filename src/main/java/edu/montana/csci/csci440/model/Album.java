package edu.montana.csci.csci440.model;

import edu.montana.csci.csci440.util.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Album extends Model {

    Long albumId;
    Long artistId;
    String title;

    public Album() {
    }

    private Album(ResultSet results) throws SQLException {
        title = results.getString("Title");
        albumId = results.getLong("AlbumId");
        artistId = results.getLong("ArtistId");
    }

    public Artist getArtist() {
        return Artist.find(artistId);
    }

    public void setArtist(Artist artist) {
        artistId = artist.getArtistId();
    }

    public List<Track> getTracks() {
        return Track.forAlbum(albumId);
    }

    public Long getAlbumId() {
        return albumId;
    }

    public void setAlbum(Album album) {
        this.albumId = album.getAlbumId();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        this.title = name;
    }

    public Long getArtistId() {
        return artistId;
    }

    public static List<Album> all() {
        return all(0, Integer.MAX_VALUE);
    }

    @Override
    public boolean create() {
        try (Connection conn = DB.connect();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO albums (AlbumId,Title) VALUES (?,?)"
             )) {
            long all = this.all().size();
            this.albumId = all+1;
            stmt.setLong(1, this.albumId);
            stmt.setString(2, this.title);
            stmt.executeUpdate();
            return true;
        } catch (SQLException sqlException) {
            return false;
        }
    }

    @Override
    public boolean update(){
        try (Connection conn = DB.connect();
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE albums SET Title=? WHERE AlbumId=?"
             )) {
            stmt.setString(1, this.title);
            stmt.setLong(2, this.albumId);
            stmt.executeUpdate();
            return true;
        } catch (SQLException sqlException) {
            return false;
        }
    }

    @Override
    public boolean verify() {
        _errors.clear(); // clear any existing errors
        if (title == null || "".equals(title)) {
            addError("Title can't be null or blank!");
        }
        if (artistId == null || 0 ==artistId) {
            addError("Title can't be null or blank!");
        }
        return !hasErrors();
    }
    public static List<Album> all(int page, int count) {
        try (Connection conn = DB.connect();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM albums LIMIT ? offset ?"
             )) {
            stmt.setInt(1, count);
            stmt.setInt(2, ((page-1)*count));
            ResultSet results = stmt.executeQuery();
            List<Album> resultList = new LinkedList<>();
            while (results.next()) {
                resultList.add(new Album(results));
            }
            return resultList;
        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
    }

    public static Album find(long i) {
        try (Connection conn = DB.connect();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM albums WHERE AlbumId=?")) {
            stmt.setLong(1, i);
            ResultSet results = stmt.executeQuery();
            if (results.next()) {
                return new Album(results);
            } else {
                return null;
            }
        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
    }

    public static List<Album> getForArtist(Long artistId) {
        // TODO implement
        return Collections.emptyList();
    }

}
